/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SingleByteCharsetConverter
/*     */ {
/*     */   private static final int BYTE_RANGE = 256;
/*  45 */   private static byte[] allBytes = new byte[256];
/*  46 */   private static final Map<String, SingleByteCharsetConverter> CONVERTER_MAP = new HashMap();
/*     */ 
/*  48 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */ 
/*  53 */   private static byte[] unknownCharsMap = new byte[65536];
/*     */ 
/* 146 */   private char[] byteToChars = new char[256];
/*     */ 
/* 148 */   private byte[] charToByteMap = new byte[65536];
/*     */ 
/*     */   public static synchronized SingleByteCharsetConverter getInstance(String encodingName, Connection conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/*  83 */     SingleByteCharsetConverter instance = (SingleByteCharsetConverter)CONVERTER_MAP.get(encodingName);
/*     */ 
/*  86 */     if (instance == null) {
/*  87 */       instance = initCharset(encodingName);
/*     */     }
/*     */ 
/*  90 */     return instance;
/*     */   }
/*     */ 
/*     */   public static SingleByteCharsetConverter initCharset(String javaEncodingName)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/*     */     try
/*     */     {
/* 106 */       if (CharsetMapping.isMultibyteCharset(javaEncodingName))
/* 107 */         return null;
/*     */     }
/*     */     catch (RuntimeException ex) {
/* 110 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 111 */       sqlEx.initCause(ex);
/* 112 */       throw sqlEx;
/*     */     }
/*     */ 
/* 115 */     SingleByteCharsetConverter converter = new SingleByteCharsetConverter(javaEncodingName);
/*     */ 
/* 118 */     CONVERTER_MAP.put(javaEncodingName, converter);
/*     */ 
/* 120 */     return converter;
/*     */   }
/*     */ 
/*     */   public static String toStringDefaultEncoding(byte[] buffer, int startPos, int length)
/*     */   {
/* 140 */     return new String(buffer, startPos, length);
/*     */   }
/*     */ 
/*     */   private SingleByteCharsetConverter(String encodingName)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 160 */     String allBytesString = new String(allBytes, 0, 256, encodingName);
/*     */ 
/* 162 */     int allBytesLen = allBytesString.length();
/*     */ 
/* 164 */     System.arraycopy(unknownCharsMap, 0, this.charToByteMap, 0, this.charToByteMap.length);
/*     */ 
/* 167 */     for (int i = 0; (i < 256) && (i < allBytesLen); i++) {
/* 168 */       char c = allBytesString.charAt(i);
/* 169 */       this.byteToChars[i] = c;
/* 170 */       this.charToByteMap[c] = allBytes[i];
/*     */     }
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(char[] c) {
/* 175 */     if (c == null) {
/* 176 */       return null;
/*     */     }
/*     */ 
/* 179 */     int length = c.length;
/* 180 */     byte[] bytes = new byte[length];
/*     */ 
/* 182 */     for (int i = 0; i < length; i++) {
/* 183 */       bytes[i] = this.charToByteMap[c[i]];
/*     */     }
/*     */ 
/* 186 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytesWrapped(char[] c, char beginWrap, char endWrap) {
/* 190 */     if (c == null) {
/* 191 */       return null;
/*     */     }
/*     */ 
/* 194 */     int length = c.length + 2;
/* 195 */     int charLength = c.length;
/*     */ 
/* 197 */     byte[] bytes = new byte[length];
/* 198 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */ 
/* 200 */     for (int i = 0; i < charLength; i++) {
/* 201 */       bytes[(i + 1)] = this.charToByteMap[c[i]];
/*     */     }
/*     */ 
/* 204 */     bytes[(length - 1)] = this.charToByteMap[endWrap];
/*     */ 
/* 206 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(char[] chars, int offset, int length) {
/* 210 */     if (chars == null) {
/* 211 */       return null;
/*     */     }
/*     */ 
/* 214 */     if (length == 0) {
/* 215 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 218 */     byte[] bytes = new byte[length];
/*     */ 
/* 220 */     for (int i = 0; i < length; i++) {
/* 221 */       bytes[i] = this.charToByteMap[chars[(i + offset)]];
/*     */     }
/*     */ 
/* 224 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(String s)
/*     */   {
/* 235 */     if (s == null) {
/* 236 */       return null;
/*     */     }
/*     */ 
/* 239 */     int length = s.length();
/* 240 */     byte[] bytes = new byte[length];
/*     */ 
/* 242 */     for (int i = 0; i < length; i++) {
/* 243 */       bytes[i] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */ 
/* 246 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytesWrapped(String s, char beginWrap, char endWrap) {
/* 250 */     if (s == null) {
/* 251 */       return null;
/*     */     }
/*     */ 
/* 254 */     int stringLength = s.length();
/*     */ 
/* 256 */     int length = stringLength + 2;
/*     */ 
/* 258 */     byte[] bytes = new byte[length];
/*     */ 
/* 260 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */ 
/* 262 */     for (int i = 0; i < stringLength; i++) {
/* 263 */       bytes[(i + 1)] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */ 
/* 266 */     bytes[(length - 1)] = this.charToByteMap[endWrap];
/*     */ 
/* 268 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final byte[] toBytes(String s, int offset, int length)
/*     */   {
/* 284 */     if (s == null) {
/* 285 */       return null;
/*     */     }
/*     */ 
/* 288 */     if (length == 0) {
/* 289 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 292 */     byte[] bytes = new byte[length];
/*     */ 
/* 294 */     for (int i = 0; i < length; i++) {
/* 295 */       char c = s.charAt(i + offset);
/* 296 */       bytes[i] = this.charToByteMap[c];
/*     */     }
/*     */ 
/* 299 */     return bytes;
/*     */   }
/*     */ 
/*     */   public final String toString(byte[] buffer)
/*     */   {
/* 311 */     return toString(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */   public final String toString(byte[] buffer, int startPos, int length)
/*     */   {
/* 327 */     char[] charArray = new char[length];
/* 328 */     int readpoint = startPos;
/*     */ 
/* 330 */     for (int i = 0; i < length; i++) {
/* 331 */       charArray[i] = this.byteToChars[(buffer[readpoint] - -128)];
/* 332 */       readpoint++;
/*     */     }
/*     */ 
/* 335 */     return new String(charArray);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  56 */     for (int i = -128; i <= 127; i++) {
/*  57 */       allBytes[(i - -128)] = (byte)i;
/*     */     }
/*     */ 
/*  60 */     for (int i = 0; i < unknownCharsMap.length; i++)
/*  61 */       unknownCharsMap[i] = 63;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.SingleByteCharsetConverter
 * JD-Core Version:    0.6.0
 */