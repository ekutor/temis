/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   protected static final Method systemNanoTimeMethod;
/*     */   private static Method CAST_METHOD;
/*     */   private static final TimeZone DEFAULT_TIMEZONE;
/*     */   private static Util enclosingInstance;
/*     */   private static boolean isJdbc4;
/*     */   private static boolean isColdFusion;
/*     */ 
/*     */   public static boolean nanoTimeAvailable()
/*     */   {
/*  68 */     return systemNanoTimeMethod != null;
/*     */   }
/*     */ 
/*     */   static final TimeZone getDefaultTimeZone()
/*     */   {
/*  79 */     return (TimeZone)DEFAULT_TIMEZONE.clone();
/*     */   }
/*     */ 
/*     */   public static boolean isJdbc4()
/*     */   {
/* 133 */     return isJdbc4;
/*     */   }
/*     */ 
/*     */   public static boolean isColdFusion() {
/* 137 */     return isColdFusion;
/*     */   }
/*     */ 
/*     */   public static String newCrypt(String password, String seed)
/*     */   {
/* 145 */     if ((password == null) || (password.length() == 0)) {
/* 146 */       return password;
/*     */     }
/*     */ 
/* 149 */     long[] pw = newHash(seed);
/* 150 */     long[] msg = newHash(password);
/* 151 */     long max = 1073741823L;
/* 152 */     long seed1 = (pw[0] ^ msg[0]) % max;
/* 153 */     long seed2 = (pw[1] ^ msg[1]) % max;
/* 154 */     char[] chars = new char[seed.length()];
/*     */ 
/* 156 */     for (int i = 0; i < seed.length(); i++) {
/* 157 */       seed1 = (seed1 * 3L + seed2) % max;
/* 158 */       seed2 = (seed1 + seed2 + 33L) % max;
/* 159 */       double d = seed1 / max;
/* 160 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 161 */       chars[i] = (char)b;
/*     */     }
/*     */ 
/* 164 */     seed1 = (seed1 * 3L + seed2) % max;
/* 165 */     seed2 = (seed1 + seed2 + 33L) % max;
/* 166 */     double d = seed1 / max;
/* 167 */     byte b = (byte)(int)Math.floor(d * 31.0D);
/*     */ 
/* 169 */     for (int i = 0; i < seed.length(); tmp205_203++)
/*     */     {
/*     */       int tmp205_203 = i;
/*     */       char[] tmp205_201 = chars; tmp205_201[tmp205_203] = (char)(tmp205_201[tmp205_203] ^ (char)b);
/*     */     }
/*     */ 
/* 173 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   static long[] newHash(String password) {
/* 177 */     long nr = 1345345333L;
/* 178 */     long add = 7L;
/* 179 */     long nr2 = 305419889L;
/*     */ 
/* 182 */     for (int i = 0; i < password.length(); i++) {
/* 183 */       if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 187 */       long tmp = 0xFF & password.charAt(i);
/* 188 */       nr ^= ((nr & 0x3F) + add) * tmp + (nr << 8);
/* 189 */       nr2 += (nr2 << 8 ^ nr);
/* 190 */       add += tmp;
/*     */     }
/*     */ 
/* 193 */     long[] result = new long[2];
/* 194 */     result[0] = (nr & 0x7FFFFFFF);
/* 195 */     result[1] = (nr2 & 0x7FFFFFFF);
/*     */ 
/* 197 */     return result;
/*     */   }
/*     */ 
/*     */   public static String oldCrypt(String password, String seed)
/*     */   {
/* 205 */     long max = 33554431L;
/*     */ 
/* 209 */     if ((password == null) || (password.length() == 0)) {
/* 210 */       return password;
/*     */     }
/*     */ 
/* 213 */     long hp = oldHash(seed);
/* 214 */     long hm = oldHash(password);
/*     */ 
/* 216 */     long nr = hp ^ hm;
/* 217 */     nr %= max;
/* 218 */     long s1 = nr;
/* 219 */     long s2 = nr / 2L;
/*     */ 
/* 221 */     char[] chars = new char[seed.length()];
/*     */ 
/* 223 */     for (int i = 0; i < seed.length(); i++) {
/* 224 */       s1 = (s1 * 3L + s2) % max;
/* 225 */       s2 = (s1 + s2 + 33L) % max;
/* 226 */       double d = s1 / max;
/* 227 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 228 */       chars[i] = (char)b;
/*     */     }
/*     */ 
/* 231 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   static long oldHash(String password) {
/* 235 */     long nr = 1345345333L;
/* 236 */     long nr2 = 7L;
/*     */ 
/* 239 */     for (int i = 0; i < password.length(); i++) {
/* 240 */       if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 244 */       long tmp = password.charAt(i);
/* 245 */       nr ^= ((nr & 0x3F) + nr2) * tmp + (nr << 8);
/* 246 */       nr2 += tmp;
/*     */     }
/*     */ 
/* 249 */     return nr & 0x7FFFFFFF;
/*     */   }
/*     */ 
/*     */   private static RandStructcture randomInit(long seed1, long seed2)
/*     */   {
/*     */     Util tmp7_4 = enclosingInstance; tmp7_4.getClass(); RandStructcture randStruct = new RandStructcture();
/*     */ 
/* 255 */     randStruct.maxValue = 1073741823L;
/* 256 */     randStruct.maxValueDbl = randStruct.maxValue;
/* 257 */     randStruct.seed1 = (seed1 % randStruct.maxValue);
/* 258 */     randStruct.seed2 = (seed2 % randStruct.maxValue);
/*     */ 
/* 260 */     return randStruct;
/*     */   }
/*     */ 
/*     */   public static Object readObject(ResultSet resultSet, int index)
/*     */     throws Exception
/*     */   {
/* 278 */     ObjectInputStream objIn = new ObjectInputStream(resultSet.getBinaryStream(index));
/*     */ 
/* 280 */     Object obj = objIn.readObject();
/* 281 */     objIn.close();
/*     */ 
/* 283 */     return obj;
/*     */   }
/*     */ 
/*     */   private static double rnd(RandStructcture randStruct) {
/* 287 */     randStruct.seed1 = ((randStruct.seed1 * 3L + randStruct.seed2) % randStruct.maxValue);
/*     */ 
/* 289 */     randStruct.seed2 = ((randStruct.seed1 + randStruct.seed2 + 33L) % randStruct.maxValue);
/*     */ 
/* 292 */     return randStruct.seed1 / randStruct.maxValueDbl;
/*     */   }
/*     */ 
/*     */   public static String scramble(String message, String password)
/*     */   {
/* 308 */     byte[] to = new byte[8];
/* 309 */     String val = "";
/*     */ 
/* 311 */     message = message.substring(0, 8);
/*     */ 
/* 313 */     if ((password != null) && (password.length() > 0)) {
/* 314 */       long[] hashPass = newHash(password);
/* 315 */       long[] hashMessage = newHash(message);
/*     */ 
/* 317 */       RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);
/*     */ 
/* 320 */       int msgPos = 0;
/* 321 */       int msgLength = message.length();
/* 322 */       int toPos = 0;
/*     */ 
/* 324 */       while (msgPos++ < msgLength) {
/* 325 */         to[(toPos++)] = (byte)(int)(Math.floor(rnd(randStruct) * 31.0D) + 64.0D);
/*     */       }
/*     */ 
/* 329 */       byte extra = (byte)(int)Math.floor(rnd(randStruct) * 31.0D);
/*     */ 
/* 331 */       for (int i = 0; i < to.length; i++)
/*     */       {
/*     */         int tmp140_138 = i;
/*     */         byte[] tmp140_136 = to; tmp140_136[tmp140_138] = (byte)(tmp140_136[tmp140_138] ^ extra);
/*     */       }
/*     */ 
/* 335 */       val = StringUtils.toString(to);
/*     */     }
/*     */ 
/* 338 */     return val;
/*     */   }
/*     */ 
/*     */   public static String stackTraceToString(Throwable ex)
/*     */   {
/* 354 */     StringBuffer traceBuf = new StringBuffer();
/* 355 */     traceBuf.append(Messages.getString("Util.1"));
/*     */ 
/* 357 */     if (ex != null) {
/* 358 */       traceBuf.append(ex.getClass().getName());
/*     */ 
/* 360 */       String message = ex.getMessage();
/*     */ 
/* 362 */       if (message != null) {
/* 363 */         traceBuf.append(Messages.getString("Util.2"));
/* 364 */         traceBuf.append(message);
/*     */       }
/*     */ 
/* 367 */       StringWriter out = new StringWriter();
/*     */ 
/* 369 */       PrintWriter printOut = new PrintWriter(out);
/*     */ 
/* 371 */       ex.printStackTrace(printOut);
/*     */ 
/* 373 */       traceBuf.append(Messages.getString("Util.3"));
/* 374 */       traceBuf.append(out.toString());
/*     */     }
/*     */ 
/* 377 */     traceBuf.append(Messages.getString("Util.4"));
/*     */ 
/* 379 */     return traceBuf.toString();
/*     */   }
/*     */ 
/*     */   public static Object getInstance(String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 386 */       return handleNewInstance(Class.forName(className).getConstructor(argTypes), args, exceptionInterceptor);
/*     */     }
/*     */     catch (SecurityException e) {
/* 389 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 393 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/*     */     }
/* 397 */     throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public static final Object handleNewInstance(Constructor<?> ctor, Object[] args, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*     */     Throwable target;
/*     */     try
/*     */     {
/* 411 */       return ctor.newInstance(args);
/*     */     } catch (IllegalArgumentException e) {
/* 413 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (InstantiationException e)
/*     */     {
/* 417 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/* 421 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 425 */       target = e.getTargetException();
/*     */ 
/* 427 */       if ((target instanceof SQLException)) {
/* 428 */         throw ((SQLException)target);
/*     */       }
/*     */ 
/* 431 */       if ((target instanceof ExceptionInInitializerError)) {
/* 432 */         target = ((ExceptionInInitializerError)target).getException();
/*     */       }
/*     */     }
/* 435 */     throw SQLError.createSQLException(target.toString(), "S1000", exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public static boolean interfaceExists(String hostname)
/*     */   {
/*     */     try
/*     */     {
/* 450 */       Class networkInterfaceClass = Class.forName("java.net.NetworkInterface");
/*     */ 
/* 452 */       return networkInterfaceClass.getMethod("getByName", (Class[])null).invoke(networkInterfaceClass, new Object[] { hostname }) != null;
/*     */     } catch (Throwable t) {
/*     */     }
/* 455 */     return false;
/*     */   }
/*     */ 
/*     */   public static Object cast(Object invokeOn, Object toCast)
/*     */   {
/* 468 */     if (CAST_METHOD != null) {
/*     */       try {
/* 470 */         return CAST_METHOD.invoke(invokeOn, new Object[] { toCast });
/*     */       } catch (Throwable t) {
/* 472 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 476 */     return null;
/*     */   }
/*     */ 
/*     */   public static long getCurrentTimeNanosOrMillis() {
/* 480 */     if (systemNanoTimeMethod != null)
/*     */       try {
/* 482 */         return ((Long)systemNanoTimeMethod.invoke(null, (Object[])null)).longValue();
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/*     */       }
/* 493 */     return System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs)
/*     */     throws SQLException
/*     */   {
/* 499 */     while (rs.next())
/* 500 */       mappedValues.put(rs.getObject(1), rs.getObject(2));
/*     */   }
/*     */ 
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs, int key, int value)
/*     */     throws SQLException
/*     */   {
/* 507 */     while (rs.next())
/* 508 */       mappedValues.put(rs.getObject(key), rs.getObject(value));
/*     */   }
/*     */ 
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs, String key, String value)
/*     */     throws SQLException
/*     */   {
/* 515 */     while (rs.next())
/* 516 */       mappedValues.put(rs.getObject(key), rs.getObject(value));
/*     */   }
/*     */ 
/*     */   public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2)
/*     */   {
/* 521 */     Map diffMap = new HashMap();
/*     */ 
/* 523 */     for (Map.Entry entry : map1.entrySet()) {
/* 524 */       Object key = entry.getKey();
/*     */ 
/* 526 */       Number value1 = null;
/* 527 */       Number value2 = null;
/*     */ 
/* 529 */       if ((entry.getValue() instanceof Number))
/*     */       {
/* 531 */         value1 = (Number)entry.getValue();
/* 532 */         value2 = (Number)map2.get(key);
/*     */       } else {
/*     */         try {
/* 535 */           value1 = new Double(entry.getValue().toString());
/* 536 */           value2 = new Double(map2.get(key).toString()); } catch (NumberFormatException nfe) {
/*     */         }
/* 538 */         continue;
/*     */       }
/*     */ 
/* 542 */       if (value1.equals(value2))
/*     */       {
/*     */         continue;
/*     */       }
/* 546 */       if ((value1 instanceof Byte)) {
/* 547 */         diffMap.put(key, Byte.valueOf((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue())));
/*     */       }
/* 550 */       else if ((value1 instanceof Short)) {
/* 551 */         diffMap.put(key, Short.valueOf((short)(((Short)value2).shortValue() - ((Short)value1).shortValue())));
/*     */       }
/* 553 */       else if ((value1 instanceof Integer)) {
/* 554 */         diffMap.put(key, Integer.valueOf(((Integer)value2).intValue() - ((Integer)value1).intValue()));
/*     */       }
/* 557 */       else if ((value1 instanceof Long)) {
/* 558 */         diffMap.put(key, Long.valueOf(((Long)value2).longValue() - ((Long)value1).longValue()));
/*     */       }
/* 561 */       else if ((value1 instanceof Float)) {
/* 562 */         diffMap.put(key, Float.valueOf(((Float)value2).floatValue() - ((Float)value1).floatValue()));
/*     */       }
/* 564 */       else if ((value1 instanceof Double)) {
/* 565 */         diffMap.put(key, Double.valueOf(((Double)value2).shortValue() - ((Double)value1).shortValue()));
/*     */       }
/* 568 */       else if ((value1 instanceof BigDecimal)) {
/* 569 */         diffMap.put(key, ((BigDecimal)value2).subtract((BigDecimal)value1));
/*     */       }
/* 571 */       else if ((value1 instanceof BigInteger)) {
/* 572 */         diffMap.put(key, ((BigInteger)value2).subtract((BigInteger)value1));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 577 */     return diffMap;
/*     */   }
/*     */ 
/*     */   public static List<Extension> loadExtensions(Connection conn, Properties props, String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 583 */     List extensionList = new LinkedList();
/*     */ 
/* 585 */     List interceptorsToCreate = StringUtils.split(extensionClassNames, ",", true);
/*     */ 
/* 588 */     Iterator iter = interceptorsToCreate.iterator();
/*     */ 
/* 590 */     String className = null;
/*     */     try
/*     */     {
/* 593 */       while (iter.hasNext()) {
/* 594 */         className = ((String)iter.next()).toString();
/* 595 */         Extension extensionInstance = (Extension)Class.forName(className).newInstance();
/*     */ 
/* 597 */         extensionInstance.init(conn, props);
/*     */ 
/* 599 */         extensionList.add(extensionInstance);
/*     */       }
/*     */     } catch (Throwable t) {
/* 602 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString(errorMessageKey, new Object[] { className }), exceptionInterceptor);
/*     */ 
/* 604 */       sqlEx.initCause(t);
/*     */ 
/* 606 */       throw sqlEx;
/*     */     }
/*     */ 
/* 609 */     return extensionList;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     Method aMethod;
/*     */     try
/*     */     {
/*  57 */       aMethod = System.class.getMethod("nanoTime", (Class[])null);
/*     */     } catch (SecurityException e) {
/*  59 */       aMethod = null;
/*     */     } catch (NoSuchMethodException e) {
/*  61 */       aMethod = null;
/*     */     }
/*     */ 
/*  64 */     systemNanoTimeMethod = aMethod;
/*     */ 
/*  76 */     DEFAULT_TIMEZONE = TimeZone.getDefault();
/*     */ 
/*  92 */     enclosingInstance = new Util();
/*     */ 
/*  94 */     isJdbc4 = false;
/*     */ 
/*  96 */     isColdFusion = false;
/*     */     try
/*     */     {
/* 100 */       CAST_METHOD = Class.class.getMethod("cast", new Class[] { Object.class });
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */     try
/*     */     {
/* 107 */       Class.forName("java.sql.NClob");
/* 108 */       isJdbc4 = true;
/*     */     } catch (Throwable t) {
/* 110 */       isJdbc4 = false;
/*     */     }
/*     */ 
/* 120 */     String loadedFrom = stackTraceToString(new Throwable());
/*     */ 
/* 122 */     if (loadedFrom != null)
/* 123 */       isColdFusion = loadedFrom.indexOf("coldfusion") != -1;
/*     */     else
/* 125 */       isColdFusion = false;
/*     */   }
/*     */ 
/*     */   class RandStructcture
/*     */   {
/*     */     long maxValue;
/*     */     double maxValueDbl;
/*     */     long seed1;
/*     */     long seed2;
/*     */ 
/*     */     RandStructcture()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Util
 * JD-Core Version:    0.6.0
 */