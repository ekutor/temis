/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ 
/*     */ public class Security
/*     */ {
/*     */   private static final char PVERSION41_CHAR = '*';
/*     */   private static final int SHA1_HASH_SIZE = 20;
/*     */ 
/*     */   private static int charVal(char c)
/*     */   {
/*  48 */     return (c >= 'A') && (c <= 'Z') ? c - 'A' + 10 : (c >= '0') && (c <= '9') ? c - '0' : c - 'a' + 10;
/*     */   }
/*     */ 
/*     */   static byte[] createKeyFromOldPassword(String passwd)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/*  79 */     passwd = makeScrambledPassword(passwd);
/*     */ 
/*  82 */     int[] salt = getSaltFromPassword(passwd);
/*     */ 
/*  85 */     return getBinaryPassword(salt, false);
/*     */   }
/*     */ 
/*     */   static byte[] getBinaryPassword(int[] salt, boolean usingNewPasswords)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 103 */     int val = 0;
/*     */ 
/* 105 */     byte[] binaryPassword = new byte[20];
/*     */ 
/* 110 */     if (usingNewPasswords) {
/* 111 */       int pos = 0;
/*     */ 
/* 113 */       for (int i = 0; i < 4; i++) {
/* 114 */         val = salt[i];
/*     */ 
/* 116 */         for (int t = 3; t >= 0; t--) {
/* 117 */           binaryPassword[(pos++)] = (byte)(val & 0xFF);
/* 118 */           val >>= 8;
/*     */         }
/*     */       }
/*     */ 
/* 122 */       return binaryPassword;
/*     */     }
/*     */ 
/* 125 */     int offset = 0;
/*     */ 
/* 127 */     for (int i = 0; i < 2; i++) {
/* 128 */       val = salt[i];
/*     */ 
/* 130 */       for (int t = 3; t >= 0; t--) {
/* 131 */         binaryPassword[(t + offset)] = (byte)(val % 256);
/* 132 */         val >>= 8;
/*     */       }
/*     */ 
/* 135 */       offset += 4;
/*     */     }
/*     */ 
/* 138 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */ 
/* 140 */     md.update(binaryPassword, 0, 8);
/*     */ 
/* 142 */     return md.digest();
/*     */   }
/*     */ 
/*     */   private static int[] getSaltFromPassword(String password) {
/* 146 */     int[] result = new int[6];
/*     */ 
/* 148 */     if ((password == null) || (password.length() == 0)) {
/* 149 */       return result;
/*     */     }
/*     */ 
/* 152 */     if (password.charAt(0) == '*')
/*     */     {
/* 154 */       String saltInHex = password.substring(1, 5);
/*     */ 
/* 156 */       int val = 0;
/*     */ 
/* 158 */       for (int i = 0; i < 4; i++) {
/* 159 */         val = (val << 4) + charVal(saltInHex.charAt(i));
/*     */       }
/*     */ 
/* 162 */       return result;
/*     */     }
/*     */ 
/* 165 */     int resultPos = 0;
/* 166 */     int pos = 0;
/* 167 */     int length = password.length();
/*     */ 
/* 169 */     while (pos < length) {
/* 170 */       int val = 0;
/*     */ 
/* 172 */       for (int i = 0; i < 8; i++) {
/* 173 */         val = (val << 4) + charVal(password.charAt(pos++));
/*     */       }
/*     */ 
/* 176 */       result[(resultPos++)] = val;
/*     */     }
/*     */ 
/* 179 */     return result;
/*     */   }
/*     */ 
/*     */   private static String longToHex(long val) {
/* 183 */     String longHex = Long.toHexString(val);
/*     */ 
/* 185 */     int length = longHex.length();
/*     */ 
/* 187 */     if (length < 8) {
/* 188 */       int padding = 8 - length;
/* 189 */       StringBuffer buf = new StringBuffer();
/*     */ 
/* 191 */       for (int i = 0; i < padding; i++) {
/* 192 */         buf.append("0");
/*     */       }
/*     */ 
/* 195 */       buf.append(longHex);
/*     */ 
/* 197 */       return buf.toString();
/*     */     }
/*     */ 
/* 200 */     return longHex.substring(0, 8);
/*     */   }
/*     */ 
/*     */   static String makeScrambledPassword(String password)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 218 */     long[] passwordHash = Util.newHash(password);
/* 219 */     StringBuffer scramble = new StringBuffer();
/*     */ 
/* 221 */     scramble.append(longToHex(passwordHash[0]));
/* 222 */     scramble.append(longToHex(passwordHash[1]));
/*     */ 
/* 224 */     return scramble.toString();
/*     */   }
/*     */ 
/*     */   static void passwordCrypt(byte[] from, byte[] to, byte[] password, int length)
/*     */   {
/* 243 */     int pos = 0;
/*     */ 
/* 245 */     while ((pos < from.length) && (pos < length)) {
/* 246 */       to[pos] = (byte)(from[pos] ^ password[pos]);
/* 247 */       pos++;
/*     */     }
/*     */   }
/*     */ 
/*     */   static byte[] passwordHashStage1(String password)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 264 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 265 */     StringBuffer cleansedPassword = new StringBuffer();
/*     */ 
/* 267 */     int passwordLength = password.length();
/*     */ 
/* 269 */     for (int i = 0; i < passwordLength; i++) {
/* 270 */       char c = password.charAt(i);
/*     */ 
/* 272 */       if ((c == ' ') || (c == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 276 */       cleansedPassword.append(c);
/*     */     }
/*     */ 
/* 279 */     return md.digest(StringUtils.getBytes(cleansedPassword.toString()));
/*     */   }
/*     */ 
/*     */   static byte[] passwordHashStage2(byte[] hashedPassword, byte[] salt)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 297 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/*     */ 
/* 300 */     md.update(salt, 0, 4);
/*     */ 
/* 302 */     md.update(hashedPassword, 0, 20);
/*     */ 
/* 304 */     return md.digest();
/*     */   }
/*     */ 
/*     */   public static byte[] scramble411(String password, String seed, Connection conn)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/* 326 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 327 */     String passwordEncoding = conn.getPasswordCharacterEncoding();
/*     */ 
/* 329 */     byte[] passwordHashStage1 = md.digest((passwordEncoding == null) || (passwordEncoding.length() == 0) ? StringUtils.getBytes(password) : StringUtils.getBytes(password, passwordEncoding));
/*     */ 
/* 333 */     md.reset();
/*     */ 
/* 335 */     byte[] passwordHashStage2 = md.digest(passwordHashStage1);
/* 336 */     md.reset();
/*     */ 
/* 338 */     byte[] seedAsBytes = StringUtils.getBytes(seed, "ASCII");
/* 339 */     md.update(seedAsBytes);
/* 340 */     md.update(passwordHashStage2);
/*     */ 
/* 342 */     byte[] toBeXord = md.digest();
/*     */ 
/* 344 */     int numToXor = toBeXord.length;
/*     */ 
/* 346 */     for (int i = 0; i < numToXor; i++) {
/* 347 */       toBeXord[i] = (byte)(toBeXord[i] ^ passwordHashStage1[i]);
/*     */     }
/*     */ 
/* 350 */     return toBeXord;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Security
 * JD-Core Version:    0.6.0
 */