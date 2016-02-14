/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class TimeUtil
/*      */ {
/*      */   static final Map<String, String[]> ABBREVIATED_TIMEZONES;
/*   48 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*      */   static final Map<String, String> TIMEZONE_MAPPINGS;
/*      */ 
/*      */   public static Time changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*      */   {
/*  843 */     if (conn != null) {
/*  844 */       if ((conn.getUseTimezone()) && (!conn.getNoTimezoneConversionForTimeType()))
/*      */       {
/*  847 */         Calendar fromCal = Calendar.getInstance(fromTz);
/*  848 */         fromCal.setTime(t);
/*      */ 
/*  850 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */ 
/*  852 */         Calendar toCal = Calendar.getInstance(toTz);
/*  853 */         toCal.setTime(t);
/*      */ 
/*  855 */         int toOffset = toCal.get(15) + toCal.get(16);
/*      */ 
/*  857 */         int offsetDiff = fromOffset - toOffset;
/*  858 */         long toTime = toCal.getTime().getTime();
/*      */ 
/*  860 */         if ((rollForward) || ((conn.isServerTzUTC()) && (!conn.isClientTzUTC())))
/*  861 */           toTime += offsetDiff;
/*      */         else {
/*  863 */           toTime -= offsetDiff;
/*      */         }
/*      */ 
/*  866 */         Time changedTime = new Time(toTime);
/*      */ 
/*  868 */         return changedTime;
/*  869 */       }if ((conn.getUseJDBCCompliantTimezoneShift()) && 
/*  870 */         (targetCalendar != null))
/*      */       {
/*  872 */         Time adjustedTime = new Time(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, t));
/*      */ 
/*  876 */         return adjustedTime;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  881 */     return t;
/*      */   }
/*      */ 
/*      */   public static Timestamp changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*      */   {
/*  905 */     if (conn != null) {
/*  906 */       if (conn.getUseTimezone())
/*      */       {
/*  908 */         Calendar fromCal = Calendar.getInstance(fromTz);
/*  909 */         fromCal.setTime(tstamp);
/*      */ 
/*  911 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/*      */ 
/*  913 */         Calendar toCal = Calendar.getInstance(toTz);
/*  914 */         toCal.setTime(tstamp);
/*      */ 
/*  916 */         int toOffset = toCal.get(15) + toCal.get(16);
/*      */ 
/*  918 */         int offsetDiff = fromOffset - toOffset;
/*  919 */         long toTime = toCal.getTime().getTime();
/*      */ 
/*  921 */         if ((rollForward) || ((conn.isServerTzUTC()) && (!conn.isClientTzUTC())))
/*  922 */           toTime += offsetDiff;
/*      */         else {
/*  924 */           toTime -= offsetDiff;
/*      */         }
/*      */ 
/*  927 */         Timestamp changedTimestamp = new Timestamp(toTime);
/*      */ 
/*  929 */         return changedTimestamp;
/*  930 */       }if ((conn.getUseJDBCCompliantTimezoneShift()) && 
/*  931 */         (targetCalendar != null))
/*      */       {
/*  933 */         Timestamp adjustedTimestamp = new Timestamp(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, tstamp));
/*      */ 
/*  937 */         adjustedTimestamp.setNanos(tstamp.getNanos());
/*      */ 
/*  939 */         return adjustedTimestamp;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  944 */     return tstamp;
/*      */   }
/*      */ 
/*      */   private static long jdbcCompliantZoneShift(Calendar sessionCalendar, Calendar targetCalendar, java.util.Date dt)
/*      */   {
/*  950 */     if (sessionCalendar == null) {
/*  951 */       sessionCalendar = new GregorianCalendar();
/*      */     }
/*      */ 
/*  958 */     java.util.Date origCalDate = targetCalendar.getTime();
/*  959 */     java.util.Date origSessionDate = sessionCalendar.getTime();
/*      */     try
/*      */     {
/*  962 */       sessionCalendar.setTime(dt);
/*      */ 
/*  964 */       targetCalendar.set(1, sessionCalendar.get(1));
/*  965 */       targetCalendar.set(2, sessionCalendar.get(2));
/*  966 */       targetCalendar.set(5, sessionCalendar.get(5));
/*      */ 
/*  968 */       targetCalendar.set(11, sessionCalendar.get(11));
/*  969 */       targetCalendar.set(12, sessionCalendar.get(12));
/*  970 */       targetCalendar.set(13, sessionCalendar.get(13));
/*  971 */       targetCalendar.set(14, sessionCalendar.get(14));
/*      */ 
/*  973 */       l = targetCalendar.getTime().getTime();
/*      */     }
/*      */     finally
/*      */     {
/*      */       long l;
/*  976 */       sessionCalendar.setTime(origSessionDate);
/*  977 */       targetCalendar.setTime(origCalDate);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final java.sql.Date fastDateCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day)
/*      */   {
/*  989 */     Calendar dateCal = cal;
/*      */ 
/*  991 */     if (useGmtConversion)
/*      */     {
/*  993 */       if (gmtCalIfNeeded == null) {
/*  994 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */       }
/*  996 */       gmtCalIfNeeded.clear();
/*      */ 
/*  998 */       dateCal = gmtCalIfNeeded;
/*      */     }
/*      */ 
/* 1001 */     dateCal.clear();
/* 1002 */     dateCal.set(14, 0);
/*      */ 
/* 1007 */     dateCal.set(year, month - 1, day, 0, 0, 0);
/*      */ 
/* 1009 */     long dateAsMillis = 0L;
/*      */     try
/*      */     {
/* 1012 */       dateAsMillis = dateCal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1015 */       dateAsMillis = dateCal.getTime().getTime();
/*      */     }
/*      */ 
/* 1018 */     return new java.sql.Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */   static final java.sql.Date fastDateCreate(int year, int month, int day, Calendar targetCalendar)
/*      */   {
/* 1024 */     Calendar dateCal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
/*      */ 
/* 1026 */     dateCal.clear();
/*      */ 
/* 1032 */     dateCal.set(year, month - 1, day, 0, 0, 0);
/* 1033 */     dateCal.set(14, 0);
/*      */ 
/* 1035 */     long dateAsMillis = 0L;
/*      */     try
/*      */     {
/* 1038 */       dateAsMillis = dateCal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1041 */       dateAsMillis = dateCal.getTime().getTime();
/*      */     }
/*      */ 
/* 1044 */     return new java.sql.Date(dateAsMillis);
/*      */   }
/*      */ 
/*      */   static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*      */   {
/* 1049 */     if ((hour < 0) || (hour > 24)) {
/* 1050 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1055 */     if ((minute < 0) || (minute > 59)) {
/* 1056 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1061 */     if ((second < 0) || (second > 59)) {
/* 1062 */       throw SQLError.createSQLException("Illegal minute value '" + second + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1067 */     cal.clear();
/*      */ 
/* 1070 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */ 
/* 1072 */     long timeAsMillis = 0L;
/*      */     try
/*      */     {
/* 1075 */       timeAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1078 */       timeAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1081 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */   static final Time fastTimeCreate(int hour, int minute, int second, Calendar targetCalendar, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*      */   {
/* 1086 */     if ((hour < 0) || (hour > 23)) {
/* 1087 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1092 */     if ((minute < 0) || (minute > 59)) {
/* 1093 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1098 */     if ((second < 0) || (second > 59)) {
/* 1099 */       throw SQLError.createSQLException("Illegal minute value '" + second + "'" + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*      */     }
/*      */ 
/* 1104 */     Calendar cal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
/* 1105 */     cal.clear();
/*      */ 
/* 1108 */     cal.set(1970, 0, 1, hour, minute, second);
/*      */ 
/* 1110 */     long timeAsMillis = 0L;
/*      */     try
/*      */     {
/* 1113 */       timeAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1116 */       timeAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1119 */     return new Time(timeAsMillis);
/*      */   }
/*      */ 
/*      */   static final Timestamp fastTimestampCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/* 1127 */     cal.clear();
/*      */ 
/* 1132 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */ 
/* 1134 */     int offsetDiff = 0;
/*      */ 
/* 1136 */     if (useGmtConversion) {
/* 1137 */       int fromOffset = cal.get(15) + cal.get(16);
/*      */ 
/* 1140 */       if (gmtCalIfNeeded == null) {
/* 1141 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */       }
/* 1143 */       gmtCalIfNeeded.clear();
/*      */ 
/* 1145 */       gmtCalIfNeeded.setTimeInMillis(cal.getTimeInMillis());
/*      */ 
/* 1147 */       int toOffset = gmtCalIfNeeded.get(15) + gmtCalIfNeeded.get(16);
/*      */ 
/* 1149 */       offsetDiff = fromOffset - toOffset;
/*      */     }
/*      */ 
/* 1152 */     if (secondsPart != 0) {
/* 1153 */       cal.set(14, secondsPart / 1000000);
/*      */     }
/*      */ 
/* 1156 */     long tsAsMillis = 0L;
/*      */     try
/*      */     {
/* 1160 */       tsAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1163 */       tsAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1166 */     Timestamp ts = new Timestamp(tsAsMillis + offsetDiff);
/*      */ 
/* 1168 */     ts.setNanos(secondsPart);
/*      */ 
/* 1170 */     return ts;
/*      */   }
/*      */ 
/*      */   static final Timestamp fastTimestampCreate(TimeZone tz, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/* 1176 */     Calendar cal = tz == null ? new GregorianCalendar() : new GregorianCalendar(tz);
/* 1177 */     cal.clear();
/*      */ 
/* 1182 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*      */ 
/* 1184 */     long tsAsMillis = 0L;
/*      */     try
/*      */     {
/* 1187 */       tsAsMillis = cal.getTimeInMillis();
/*      */     }
/*      */     catch (IllegalAccessError iae) {
/* 1190 */       tsAsMillis = cal.getTime().getTime();
/*      */     }
/*      */ 
/* 1193 */     Timestamp ts = new Timestamp(tsAsMillis);
/* 1194 */     ts.setNanos(secondsPart);
/*      */ 
/* 1196 */     return ts;
/*      */   }
/*      */ 
/*      */   public static String getCanoncialTimezone(String timezoneStr, ExceptionInterceptor exceptionInterceptor)
/*      */     throws SQLException
/*      */   {
/* 1212 */     if (timezoneStr == null) {
/* 1213 */       return null;
/*      */     }
/*      */ 
/* 1216 */     timezoneStr = timezoneStr.trim();
/*      */ 
/* 1220 */     if ((timezoneStr.length() > 2) && 
/* 1221 */       ((timezoneStr.charAt(0) == '+') || (timezoneStr.charAt(0) == '-')) && (Character.isDigit(timezoneStr.charAt(1))))
/*      */     {
/* 1223 */       return "GMT" + timezoneStr;
/*      */     }
/*      */ 
/* 1228 */     int daylightIndex = StringUtils.indexOfIgnoreCase(timezoneStr, "DAYLIGHT");
/*      */ 
/* 1231 */     if (daylightIndex != -1) {
/* 1232 */       StringBuffer timezoneBuf = new StringBuffer();
/* 1233 */       timezoneBuf.append(timezoneStr.substring(0, daylightIndex));
/* 1234 */       timezoneBuf.append("Standard");
/* 1235 */       timezoneBuf.append(timezoneStr.substring(daylightIndex + "DAYLIGHT".length(), timezoneStr.length()));
/*      */ 
/* 1237 */       timezoneStr = timezoneBuf.toString();
/*      */     }
/*      */ 
/* 1240 */     String canonicalTz = (String)TIMEZONE_MAPPINGS.get(timezoneStr);
/*      */ 
/* 1243 */     if (canonicalTz == null) {
/* 1244 */       String[] abbreviatedTimezone = (String[])ABBREVIATED_TIMEZONES.get(timezoneStr);
/*      */ 
/* 1247 */       if (abbreviatedTimezone != null)
/*      */       {
/* 1249 */         if (abbreviatedTimezone.length == 1) {
/* 1250 */           canonicalTz = abbreviatedTimezone[0];
/*      */         } else {
/* 1252 */           StringBuffer possibleTimezones = new StringBuffer(128);
/*      */ 
/* 1254 */           possibleTimezones.append(abbreviatedTimezone[0]);
/*      */ 
/* 1256 */           for (int i = 1; i < abbreviatedTimezone.length; i++) {
/* 1257 */             possibleTimezones.append(", ");
/* 1258 */             possibleTimezones.append(abbreviatedTimezone[i]);
/*      */           }
/*      */ 
/* 1261 */           throw SQLError.createSQLException(Messages.getString("TimeUtil.TooGenericTimezoneId", new Object[] { timezoneStr, possibleTimezones }), "01S00", exceptionInterceptor);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1267 */     return canonicalTz;
/*      */   }
/*      */ 
/*      */   private static String timeFormattedString(int hours, int minutes, int seconds)
/*      */   {
/* 1276 */     StringBuffer buf = new StringBuffer(8);
/* 1277 */     if (hours < 10) {
/* 1278 */       buf.append("0");
/*      */     }
/*      */ 
/* 1281 */     buf.append(hours);
/* 1282 */     buf.append(":");
/*      */ 
/* 1284 */     if (minutes < 10) {
/* 1285 */       buf.append("0");
/*      */     }
/*      */ 
/* 1288 */     buf.append(minutes);
/* 1289 */     buf.append(":");
/*      */ 
/* 1291 */     if (seconds < 10) {
/* 1292 */       buf.append("0");
/*      */     }
/*      */ 
/* 1295 */     buf.append(seconds);
/*      */ 
/* 1297 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public static String formatNanos(int nanos, boolean serverSupportsFracSecs) {
/* 1301 */     if ((!serverSupportsFracSecs) || (nanos == 0)) {
/* 1302 */       return "0";
/*      */     }
/*      */ 
/* 1305 */     boolean usingMicros = true;
/*      */ 
/* 1307 */     if (usingMicros) {
/* 1308 */       nanos /= 1000;
/*      */     }
/*      */ 
/* 1311 */     int digitCount = usingMicros ? 6 : 9;
/*      */ 
/* 1313 */     String nanosString = Integer.toString(nanos);
/* 1314 */     String zeroPadding = usingMicros ? "000000" : "000000000";
/*      */ 
/* 1316 */     nanosString = zeroPadding.substring(0, digitCount - nanosString.length()) + nanosString;
/*      */ 
/* 1319 */     int pos = digitCount - 1;
/*      */ 
/* 1321 */     while (nanosString.charAt(pos) == '0') {
/* 1322 */       pos--;
/*      */     }
/*      */ 
/* 1325 */     nanosString = nanosString.substring(0, pos + 1);
/*      */ 
/* 1327 */     return nanosString;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   53 */     HashMap tempTzMap = new HashMap();
/*      */ 
/*   58 */     tempTzMap.put("Romance", "Europe/Paris");
/*   59 */     tempTzMap.put("Romance Standard Time", "Europe/Paris");
/*   60 */     tempTzMap.put("Warsaw", "Europe/Warsaw");
/*   61 */     tempTzMap.put("Central Europe", "Europe/Prague");
/*   62 */     tempTzMap.put("Central Europe Standard Time", "Europe/Prague");
/*   63 */     tempTzMap.put("Prague Bratislava", "Europe/Prague");
/*   64 */     tempTzMap.put("W. Central Africa Standard Time", "Africa/Luanda");
/*   65 */     tempTzMap.put("FLE", "Europe/Helsinki");
/*   66 */     tempTzMap.put("FLE Standard Time", "Europe/Helsinki");
/*   67 */     tempTzMap.put("GFT", "Europe/Athens");
/*   68 */     tempTzMap.put("GFT Standard Time", "Europe/Athens");
/*   69 */     tempTzMap.put("GTB", "Europe/Athens");
/*   70 */     tempTzMap.put("GTB Standard Time", "Europe/Athens");
/*   71 */     tempTzMap.put("Israel", "Asia/Jerusalem");
/*   72 */     tempTzMap.put("Israel Standard Time", "Asia/Jerusalem");
/*   73 */     tempTzMap.put("Arab", "Asia/Riyadh");
/*   74 */     tempTzMap.put("Arab Standard Time", "Asia/Riyadh");
/*   75 */     tempTzMap.put("Arabic Standard Time", "Asia/Baghdad");
/*   76 */     tempTzMap.put("E. Africa", "Africa/Nairobi");
/*   77 */     tempTzMap.put("E. Africa Standard Time", "Africa/Nairobi");
/*   78 */     tempTzMap.put("Saudi Arabia", "Asia/Riyadh");
/*   79 */     tempTzMap.put("Saudi Arabia Standard Time", "Asia/Riyadh");
/*   80 */     tempTzMap.put("Iran", "Asia/Tehran");
/*   81 */     tempTzMap.put("Iran Standard Time", "Asia/Tehran");
/*   82 */     tempTzMap.put("Afghanistan", "Asia/Kabul");
/*   83 */     tempTzMap.put("Afghanistan Standard Time", "Asia/Kabul");
/*   84 */     tempTzMap.put("India", "Asia/Calcutta");
/*   85 */     tempTzMap.put("India Standard Time", "Asia/Calcutta");
/*   86 */     tempTzMap.put("Myanmar Standard Time", "Asia/Rangoon");
/*   87 */     tempTzMap.put("Nepal Standard Time", "Asia/Katmandu");
/*   88 */     tempTzMap.put("Sri Lanka", "Asia/Colombo");
/*   89 */     tempTzMap.put("Sri Lanka Standard Time", "Asia/Colombo");
/*   90 */     tempTzMap.put("Beijing", "Asia/Shanghai");
/*   91 */     tempTzMap.put("China", "Asia/Shanghai");
/*   92 */     tempTzMap.put("China Standard Time", "Asia/Shanghai");
/*   93 */     tempTzMap.put("AUS Central", "Australia/Darwin");
/*   94 */     tempTzMap.put("AUS Central Standard Time", "Australia/Darwin");
/*   95 */     tempTzMap.put("Cen. Australia", "Australia/Adelaide");
/*   96 */     tempTzMap.put("Cen. Australia Standard Time", "Australia/Adelaide");
/*   97 */     tempTzMap.put("Vladivostok", "Asia/Vladivostok");
/*   98 */     tempTzMap.put("Vladivostok Standard Time", "Asia/Vladivostok");
/*   99 */     tempTzMap.put("West Pacific", "Pacific/Guam");
/*  100 */     tempTzMap.put("West Pacific Standard Time", "Pacific/Guam");
/*  101 */     tempTzMap.put("E. South America", "America/Sao_Paulo");
/*  102 */     tempTzMap.put("E. South America Standard Time", "America/Sao_Paulo");
/*  103 */     tempTzMap.put("Greenland Standard Time", "America/Godthab");
/*  104 */     tempTzMap.put("Newfoundland", "America/St_Johns");
/*  105 */     tempTzMap.put("Newfoundland Standard Time", "America/St_Johns");
/*  106 */     tempTzMap.put("Pacific SA", "America/Caracas");
/*  107 */     tempTzMap.put("Pacific SA Standard Time", "America/Caracas");
/*  108 */     tempTzMap.put("SA Western", "America/Caracas");
/*  109 */     tempTzMap.put("SA Western Standard Time", "America/Caracas");
/*  110 */     tempTzMap.put("SA Pacific", "America/Bogota");
/*  111 */     tempTzMap.put("SA Pacific Standard Time", "America/Bogota");
/*  112 */     tempTzMap.put("US Eastern", "America/Indianapolis");
/*  113 */     tempTzMap.put("US Eastern Standard Time", "America/Indianapolis");
/*  114 */     tempTzMap.put("Central America Standard Time", "America/Regina");
/*  115 */     tempTzMap.put("Mexico", "America/Mexico_City");
/*  116 */     tempTzMap.put("Mexico Standard Time", "America/Mexico_City");
/*  117 */     tempTzMap.put("Canada Central", "America/Regina");
/*  118 */     tempTzMap.put("Canada Central Standard Time", "America/Regina");
/*  119 */     tempTzMap.put("US Mountain", "America/Phoenix");
/*  120 */     tempTzMap.put("US Mountain Standard Time", "America/Phoenix");
/*  121 */     tempTzMap.put("GMT", "GMT");
/*  122 */     tempTzMap.put("Ekaterinburg", "Asia/Yekaterinburg");
/*  123 */     tempTzMap.put("Ekaterinburg Standard Time", "Asia/Yekaterinburg");
/*  124 */     tempTzMap.put("West Asia", "Asia/Karachi");
/*  125 */     tempTzMap.put("West Asia Standard Time", "Asia/Karachi");
/*  126 */     tempTzMap.put("Central Asia", "Asia/Dhaka");
/*  127 */     tempTzMap.put("Central Asia Standard Time", "Asia/Dhaka");
/*  128 */     tempTzMap.put("N. Central Asia Standard Time", "Asia/Novosibirsk");
/*  129 */     tempTzMap.put("Bangkok", "Asia/Bangkok");
/*  130 */     tempTzMap.put("Bangkok Standard Time", "Asia/Bangkok");
/*  131 */     tempTzMap.put("North Asia Standard Time", "Asia/Krasnoyarsk");
/*  132 */     tempTzMap.put("SE Asia", "Asia/Bangkok");
/*  133 */     tempTzMap.put("SE Asia Standard Time", "Asia/Bangkok");
/*  134 */     tempTzMap.put("North Asia East Standard Time", "Asia/Ulaanbaatar");
/*  135 */     tempTzMap.put("Singapore", "Asia/Singapore");
/*  136 */     tempTzMap.put("Singapore Standard Time", "Asia/Singapore");
/*  137 */     tempTzMap.put("Taipei", "Asia/Taipei");
/*  138 */     tempTzMap.put("Taipei Standard Time", "Asia/Taipei");
/*  139 */     tempTzMap.put("W. Australia", "Australia/Perth");
/*  140 */     tempTzMap.put("W. Australia Standard Time", "Australia/Perth");
/*  141 */     tempTzMap.put("Korea", "Asia/Seoul");
/*  142 */     tempTzMap.put("Korea Standard Time", "Asia/Seoul");
/*  143 */     tempTzMap.put("Tokyo", "Asia/Tokyo");
/*  144 */     tempTzMap.put("Tokyo Standard Time", "Asia/Tokyo");
/*  145 */     tempTzMap.put("Yakutsk", "Asia/Yakutsk");
/*  146 */     tempTzMap.put("Yakutsk Standard Time", "Asia/Yakutsk");
/*  147 */     tempTzMap.put("Central European", "Europe/Belgrade");
/*  148 */     tempTzMap.put("Central European Standard Time", "Europe/Belgrade");
/*  149 */     tempTzMap.put("W. Europe", "Europe/Berlin");
/*  150 */     tempTzMap.put("W. Europe Standard Time", "Europe/Berlin");
/*  151 */     tempTzMap.put("Tasmania", "Australia/Hobart");
/*  152 */     tempTzMap.put("Tasmania Standard Time", "Australia/Hobart");
/*  153 */     tempTzMap.put("AUS Eastern", "Australia/Sydney");
/*  154 */     tempTzMap.put("AUS Eastern Standard Time", "Australia/Sydney");
/*  155 */     tempTzMap.put("E. Australia", "Australia/Brisbane");
/*  156 */     tempTzMap.put("E. Australia Standard Time", "Australia/Brisbane");
/*  157 */     tempTzMap.put("Sydney Standard Time", "Australia/Sydney");
/*  158 */     tempTzMap.put("Central Pacific", "Pacific/Guadalcanal");
/*  159 */     tempTzMap.put("Central Pacific Standard Time", "Pacific/Guadalcanal");
/*  160 */     tempTzMap.put("Dateline", "Pacific/Majuro");
/*  161 */     tempTzMap.put("Dateline Standard Time", "Pacific/Majuro");
/*  162 */     tempTzMap.put("Fiji", "Pacific/Fiji");
/*  163 */     tempTzMap.put("Fiji Standard Time", "Pacific/Fiji");
/*  164 */     tempTzMap.put("Samoa", "Pacific/Apia");
/*  165 */     tempTzMap.put("Samoa Standard Time", "Pacific/Apia");
/*  166 */     tempTzMap.put("Hawaiian", "Pacific/Honolulu");
/*  167 */     tempTzMap.put("Hawaiian Standard Time", "Pacific/Honolulu");
/*  168 */     tempTzMap.put("Alaskan", "America/Anchorage");
/*  169 */     tempTzMap.put("Alaskan Standard Time", "America/Anchorage");
/*  170 */     tempTzMap.put("Pacific", "America/Los_Angeles");
/*  171 */     tempTzMap.put("Pacific Standard Time", "America/Los_Angeles");
/*  172 */     tempTzMap.put("Mexico Standard Time 2", "America/Chihuahua");
/*  173 */     tempTzMap.put("Mountain", "America/Denver");
/*  174 */     tempTzMap.put("Mountain Standard Time", "America/Denver");
/*  175 */     tempTzMap.put("Central", "America/Chicago");
/*  176 */     tempTzMap.put("Central Standard Time", "America/Chicago");
/*  177 */     tempTzMap.put("Eastern", "America/New_York");
/*  178 */     tempTzMap.put("Eastern Standard Time", "America/New_York");
/*  179 */     tempTzMap.put("E. Europe", "Europe/Bucharest");
/*  180 */     tempTzMap.put("E. Europe Standard Time", "Europe/Bucharest");
/*  181 */     tempTzMap.put("Egypt", "Africa/Cairo");
/*  182 */     tempTzMap.put("Egypt Standard Time", "Africa/Cairo");
/*  183 */     tempTzMap.put("South Africa", "Africa/Harare");
/*  184 */     tempTzMap.put("South Africa Standard Time", "Africa/Harare");
/*  185 */     tempTzMap.put("Atlantic", "America/Halifax");
/*  186 */     tempTzMap.put("Atlantic Standard Time", "America/Halifax");
/*  187 */     tempTzMap.put("SA Eastern", "America/Buenos_Aires");
/*  188 */     tempTzMap.put("SA Eastern Standard Time", "America/Buenos_Aires");
/*  189 */     tempTzMap.put("Mid-Atlantic", "Atlantic/South_Georgia");
/*  190 */     tempTzMap.put("Mid-Atlantic Standard Time", "Atlantic/South_Georgia");
/*  191 */     tempTzMap.put("Azores", "Atlantic/Azores");
/*  192 */     tempTzMap.put("Azores Standard Time", "Atlantic/Azores");
/*  193 */     tempTzMap.put("Cape Verde Standard Time", "Atlantic/Cape_Verde");
/*  194 */     tempTzMap.put("Russian", "Europe/Moscow");
/*  195 */     tempTzMap.put("Russian Standard Time", "Europe/Moscow");
/*  196 */     tempTzMap.put("New Zealand", "Pacific/Auckland");
/*  197 */     tempTzMap.put("New Zealand Standard Time", "Pacific/Auckland");
/*  198 */     tempTzMap.put("Tonga Standard Time", "Pacific/Tongatapu");
/*  199 */     tempTzMap.put("Arabian", "Asia/Muscat");
/*  200 */     tempTzMap.put("Arabian Standard Time", "Asia/Muscat");
/*  201 */     tempTzMap.put("Caucasus", "Asia/Tbilisi");
/*  202 */     tempTzMap.put("Caucasus Standard Time", "Asia/Tbilisi");
/*  203 */     tempTzMap.put("GMT Standard Time", "GMT");
/*  204 */     tempTzMap.put("Greenwich", "GMT");
/*  205 */     tempTzMap.put("Greenwich Standard Time", "GMT");
/*  206 */     tempTzMap.put("UTC", "GMT");
/*      */ 
/*  209 */     Iterator entries = tempTzMap.entrySet().iterator();
/*  210 */     Map entryMap = new HashMap(tempTzMap.size());
/*      */ 
/*  212 */     while (entries.hasNext()) {
/*  213 */       String name = (String)((Map.Entry)entries.next()).getValue();
/*  214 */       entryMap.put(name, name);
/*      */     }
/*      */ 
/*  217 */     tempTzMap.putAll(entryMap);
/*      */ 
/*  219 */     TIMEZONE_MAPPINGS = Collections.unmodifiableMap(tempTzMap);
/*      */ 
/*  224 */     HashMap tempAbbrMap = new HashMap();
/*      */ 
/*  226 */     tempAbbrMap.put("ACST", new String[] { "America/Porto_Acre" });
/*  227 */     tempAbbrMap.put("ACT", new String[] { "America/Porto_Acre" });
/*  228 */     tempAbbrMap.put("ADDT", new String[] { "America/Pangnirtung" });
/*  229 */     tempAbbrMap.put("ADMT", new String[] { "Africa/Asmera", "Africa/Addis_Ababa" });
/*      */ 
/*  231 */     tempAbbrMap.put("ADT", new String[] { "Atlantic/Bermuda", "Asia/Baghdad", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Barbados", "America/Martinique" });
/*      */ 
/*  235 */     tempAbbrMap.put("AFT", new String[] { "Asia/Kabul" });
/*  236 */     tempAbbrMap.put("AHDT", new String[] { "America/Anchorage" });
/*  237 */     tempAbbrMap.put("AHST", new String[] { "America/Anchorage" });
/*  238 */     tempAbbrMap.put("AHWT", new String[] { "America/Anchorage" });
/*  239 */     tempAbbrMap.put("AKDT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  241 */     tempAbbrMap.put("AKST", new String[] { "Asia/Aqtobe", "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  243 */     tempAbbrMap.put("AKT", new String[] { "Asia/Aqtobe" });
/*  244 */     tempAbbrMap.put("AKTST", new String[] { "Asia/Aqtobe" });
/*  245 */     tempAbbrMap.put("AKWT", new String[] { "America/Juneau", "America/Yakutat", "America/Anchorage", "America/Nome" });
/*      */ 
/*  247 */     tempAbbrMap.put("ALMST", new String[] { "Asia/Almaty" });
/*  248 */     tempAbbrMap.put("ALMT", new String[] { "Asia/Almaty" });
/*  249 */     tempAbbrMap.put("AMST", new String[] { "Asia/Yerevan", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus" });
/*      */ 
/*  251 */     tempAbbrMap.put("AMT", new String[] { "Europe/Athens", "Europe/Amsterdam", "Asia/Yerevan", "Africa/Asmera", "America/Cuiaba", "America/Porto_Velho", "America/Boa_Vista", "America/Manaus", "America/Asuncion" });
/*      */ 
/*  255 */     tempAbbrMap.put("ANAMT", new String[] { "Asia/Anadyr" });
/*  256 */     tempAbbrMap.put("ANAST", new String[] { "Asia/Anadyr" });
/*  257 */     tempAbbrMap.put("ANAT", new String[] { "Asia/Anadyr" });
/*  258 */     tempAbbrMap.put("ANT", new String[] { "America/Aruba", "America/Curacao" });
/*  259 */     tempAbbrMap.put("AQTST", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  260 */     tempAbbrMap.put("AQTT", new String[] { "Asia/Aqtobe", "Asia/Aqtau" });
/*  261 */     tempAbbrMap.put("ARST", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*  264 */     tempAbbrMap.put("ART", new String[] { "Antarctica/Palmer", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza" });
/*      */ 
/*  267 */     tempAbbrMap.put("ASHST", new String[] { "Asia/Ashkhabad" });
/*  268 */     tempAbbrMap.put("ASHT", new String[] { "Asia/Ashkhabad" });
/*  269 */     tempAbbrMap.put("AST", new String[] { "Atlantic/Bermuda", "Asia/Bahrain", "Asia/Baghdad", "Asia/Kuwait", "Asia/Qatar", "Asia/Riyadh", "Asia/Aden", "America/Thule", "America/Goose_Bay", "America/Halifax", "America/Glace_Bay", "America/Pangnirtung", "America/Anguilla", "America/Antigua", "America/Barbados", "America/Dominica", "America/Santo_Domingo", "America/Grenada", "America/Guadeloupe", "America/Martinique", "America/Montserrat", "America/Puerto_Rico", "America/St_Kitts", "America/St_Lucia", "America/Miquelon", "America/St_Vincent", "America/Tortola", "America/St_Thomas", "America/Aruba", "America/Curacao", "America/Port_of_Spain" });
/*      */ 
/*  280 */     tempAbbrMap.put("AWT", new String[] { "America/Puerto_Rico" });
/*  281 */     tempAbbrMap.put("AZOST", new String[] { "Atlantic/Azores" });
/*  282 */     tempAbbrMap.put("AZOT", new String[] { "Atlantic/Azores" });
/*  283 */     tempAbbrMap.put("AZST", new String[] { "Asia/Baku" });
/*  284 */     tempAbbrMap.put("AZT", new String[] { "Asia/Baku" });
/*  285 */     tempAbbrMap.put("BAKST", new String[] { "Asia/Baku" });
/*  286 */     tempAbbrMap.put("BAKT", new String[] { "Asia/Baku" });
/*  287 */     tempAbbrMap.put("BDT", new String[] { "Asia/Dacca", "America/Nome", "America/Adak" });
/*      */ 
/*  289 */     tempAbbrMap.put("BEAT", new String[] { "Africa/Nairobi", "Africa/Mogadishu", "Africa/Kampala" });
/*      */ 
/*  291 */     tempAbbrMap.put("BEAUT", new String[] { "Africa/Nairobi", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*  293 */     tempAbbrMap.put("BMT", new String[] { "Europe/Brussels", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Bucharest", "Europe/Zurich", "Asia/Baghdad", "Asia/Bangkok", "Africa/Banjul", "America/Barbados", "America/Bogota" });
/*      */ 
/*  297 */     tempAbbrMap.put("BNT", new String[] { "Asia/Brunei" });
/*  298 */     tempAbbrMap.put("BORT", new String[] { "Asia/Ujung_Pandang", "Asia/Kuching" });
/*      */ 
/*  300 */     tempAbbrMap.put("BOST", new String[] { "America/La_Paz" });
/*  301 */     tempAbbrMap.put("BOT", new String[] { "America/La_Paz" });
/*  302 */     tempAbbrMap.put("BRST", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*  305 */     tempAbbrMap.put("BRT", new String[] { "America/Belem", "America/Fortaleza", "America/Araguaina", "America/Maceio", "America/Sao_Paulo" });
/*      */ 
/*  307 */     tempAbbrMap.put("BST", new String[] { "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak" });
/*      */ 
/*  310 */     tempAbbrMap.put("BTT", new String[] { "Asia/Thimbu" });
/*  311 */     tempAbbrMap.put("BURT", new String[] { "Asia/Dacca", "Asia/Rangoon", "Asia/Calcutta" });
/*      */ 
/*  313 */     tempAbbrMap.put("BWT", new String[] { "America/Nome", "America/Adak" });
/*  314 */     tempAbbrMap.put("CANT", new String[] { "Atlantic/Canary" });
/*  315 */     tempAbbrMap.put("CAST", new String[] { "Africa/Gaborone", "Africa/Khartoum" });
/*      */ 
/*  317 */     tempAbbrMap.put("CAT", new String[] { "Africa/Gaborone", "Africa/Bujumbura", "Africa/Lubumbashi", "Africa/Blantyre", "Africa/Maputo", "Africa/Windhoek", "Africa/Kigali", "Africa/Khartoum", "Africa/Lusaka", "Africa/Harare", "America/Anchorage" });
/*      */ 
/*  322 */     tempAbbrMap.put("CCT", new String[] { "Indian/Cocos" });
/*  323 */     tempAbbrMap.put("CDDT", new String[] { "America/Rankin_Inlet" });
/*  324 */     tempAbbrMap.put("CDT", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*  336 */     tempAbbrMap.put("CEST", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  350 */     tempAbbrMap.put("CET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  364 */     tempAbbrMap.put("CGST", new String[] { "America/Scoresbysund" });
/*  365 */     tempAbbrMap.put("CGT", new String[] { "America/Scoresbysund" });
/*  366 */     tempAbbrMap.put("CHDT", new String[] { "America/Belize" });
/*  367 */     tempAbbrMap.put("CHUT", new String[] { "Asia/Chungking" });
/*  368 */     tempAbbrMap.put("CJT", new String[] { "Asia/Tokyo" });
/*  369 */     tempAbbrMap.put("CKHST", new String[] { "Pacific/Rarotonga" });
/*  370 */     tempAbbrMap.put("CKT", new String[] { "Pacific/Rarotonga" });
/*  371 */     tempAbbrMap.put("CLST", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */ 
/*  373 */     tempAbbrMap.put("CLT", new String[] { "Antarctica/Palmer", "America/Santiago" });
/*      */ 
/*  375 */     tempAbbrMap.put("CMT", new String[] { "Europe/Copenhagen", "Europe/Chisinau", "Europe/Tiraspol", "America/St_Lucia", "America/Buenos_Aires", "America/Rosario", "America/Cordoba", "America/Jujuy", "America/Catamarca", "America/Mendoza", "America/Caracas" });
/*      */ 
/*  380 */     tempAbbrMap.put("COST", new String[] { "America/Bogota" });
/*  381 */     tempAbbrMap.put("COT", new String[] { "America/Bogota" });
/*  382 */     tempAbbrMap.put("CST", new String[] { "Asia/Harbin", "Asia/Shanghai", "Asia/Chungking", "Asia/Urumqi", "Asia/Kashgar", "Asia/Taipei", "Asia/Macao", "Asia/Jayapura", "Australia/Darwin", "Australia/Adelaide", "Australia/Broken_Hill", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Rainy_River", "America/Winnipeg", "America/Regina", "America/Swift_Current", "America/Pangnirtung", "America/Iqaluit", "America/Rankin_Inlet", "America/Cambridge_Bay", "America/Cancun", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Belize", "America/Costa_Rica", "America/Havana", "America/El_Salvador", "America/Guatemala", "America/Tegucigalpa", "America/Managua" });
/*      */ 
/*  402 */     tempAbbrMap.put("CUT", new String[] { "Europe/Zaporozhye" });
/*  403 */     tempAbbrMap.put("CVST", new String[] { "Atlantic/Cape_Verde" });
/*  404 */     tempAbbrMap.put("CVT", new String[] { "Atlantic/Cape_Verde" });
/*  405 */     tempAbbrMap.put("CWT", new String[] { "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Menominee" });
/*      */ 
/*  409 */     tempAbbrMap.put("CXT", new String[] { "Indian/Christmas" });
/*  410 */     tempAbbrMap.put("DACT", new String[] { "Asia/Dacca" });
/*  411 */     tempAbbrMap.put("DAVT", new String[] { "Antarctica/Davis" });
/*  412 */     tempAbbrMap.put("DDUT", new String[] { "Antarctica/DumontDUrville" });
/*  413 */     tempAbbrMap.put("DFT", new String[] { "Europe/Oslo", "Europe/Paris" });
/*  414 */     tempAbbrMap.put("DMT", new String[] { "Europe/Belfast", "Europe/Dublin" });
/*  415 */     tempAbbrMap.put("DUSST", new String[] { "Asia/Dushanbe" });
/*  416 */     tempAbbrMap.put("DUST", new String[] { "Asia/Dushanbe" });
/*  417 */     tempAbbrMap.put("EASST", new String[] { "Pacific/Easter" });
/*  418 */     tempAbbrMap.put("EAST", new String[] { "Indian/Antananarivo", "Pacific/Easter" });
/*      */ 
/*  420 */     tempAbbrMap.put("EAT", new String[] { "Indian/Comoro", "Indian/Antananarivo", "Indian/Mayotte", "Africa/Djibouti", "Africa/Asmera", "Africa/Addis_Ababa", "Africa/Nairobi", "Africa/Mogadishu", "Africa/Khartoum", "Africa/Dar_es_Salaam", "Africa/Kampala" });
/*      */ 
/*  425 */     tempAbbrMap.put("ECT", new String[] { "Pacific/Galapagos", "America/Guayaquil" });
/*      */ 
/*  427 */     tempAbbrMap.put("EDDT", new String[] { "America/Iqaluit" });
/*  428 */     tempAbbrMap.put("EDT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Nassau", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Grand_Turk" });
/*      */ 
/*  436 */     tempAbbrMap.put("EEMT", new String[] { "Europe/Minsk", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Kaliningrad", "Europe/Moscow" });
/*      */ 
/*  438 */     tempAbbrMap.put("EEST", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo" });
/*      */ 
/*  446 */     tempAbbrMap.put("EET", new String[] { "Europe/Minsk", "Europe/Sofia", "Europe/Tallinn", "Europe/Helsinki", "Europe/Athens", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Warsaw", "Europe/Bucharest", "Europe/Kaliningrad", "Europe/Moscow", "Europe/Istanbul", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Asia/Nicosia", "Asia/Amman", "Asia/Beirut", "Asia/Gaza", "Asia/Damascus", "Africa/Cairo", "Africa/Tripoli" });
/*      */ 
/*  455 */     tempAbbrMap.put("EGST", new String[] { "America/Scoresbysund" });
/*  456 */     tempAbbrMap.put("EGT", new String[] { "Atlantic/Jan_Mayen", "America/Scoresbysund" });
/*      */ 
/*  458 */     tempAbbrMap.put("EHDT", new String[] { "America/Santo_Domingo" });
/*  459 */     tempAbbrMap.put("EST", new String[] { "Australia/Brisbane", "Australia/Lindeman", "Australia/Hobart", "Australia/Melbourne", "Australia/Sydney", "Australia/Broken_Hill", "Australia/Lord_Howe", "America/New_York", "America/Chicago", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Knox", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Menominee", "America/Montreal", "America/Thunder_Bay", "America/Nipigon", "America/Pangnirtung", "America/Iqaluit", "America/Cancun", "America/Antigua", "America/Nassau", "America/Cayman", "America/Santo_Domingo", "America/Port-au-Prince", "America/Jamaica", "America/Managua", "America/Panama", "America/Grand_Turk" });
/*      */ 
/*  473 */     tempAbbrMap.put("EWT", new String[] { "America/New_York", "America/Indianapolis", "America/Indiana/Marengo", "America/Indiana/Vevay", "America/Louisville", "America/Detroit", "America/Jamaica" });
/*      */ 
/*  477 */     tempAbbrMap.put("FFMT", new String[] { "America/Martinique" });
/*  478 */     tempAbbrMap.put("FJST", new String[] { "Pacific/Fiji" });
/*  479 */     tempAbbrMap.put("FJT", new String[] { "Pacific/Fiji" });
/*  480 */     tempAbbrMap.put("FKST", new String[] { "Atlantic/Stanley" });
/*  481 */     tempAbbrMap.put("FKT", new String[] { "Atlantic/Stanley" });
/*  482 */     tempAbbrMap.put("FMT", new String[] { "Atlantic/Madeira", "Africa/Freetown" });
/*      */ 
/*  484 */     tempAbbrMap.put("FNST", new String[] { "America/Noronha" });
/*  485 */     tempAbbrMap.put("FNT", new String[] { "America/Noronha" });
/*  486 */     tempAbbrMap.put("FRUST", new String[] { "Asia/Bishkek" });
/*  487 */     tempAbbrMap.put("FRUT", new String[] { "Asia/Bishkek" });
/*  488 */     tempAbbrMap.put("GALT", new String[] { "Pacific/Galapagos" });
/*  489 */     tempAbbrMap.put("GAMT", new String[] { "Pacific/Gambier" });
/*  490 */     tempAbbrMap.put("GBGT", new String[] { "America/Guyana" });
/*  491 */     tempAbbrMap.put("GEST", new String[] { "Asia/Tbilisi" });
/*  492 */     tempAbbrMap.put("GET", new String[] { "Asia/Tbilisi" });
/*  493 */     tempAbbrMap.put("GFT", new String[] { "America/Cayenne" });
/*  494 */     tempAbbrMap.put("GHST", new String[] { "Africa/Accra" });
/*  495 */     tempAbbrMap.put("GILT", new String[] { "Pacific/Tarawa" });
/*  496 */     tempAbbrMap.put("GMT", new String[] { "Atlantic/St_Helena", "Atlantic/Reykjavik", "Europe/London", "Europe/Belfast", "Europe/Dublin", "Europe/Gibraltar", "Africa/Porto-Novo", "Africa/Ouagadougou", "Africa/Abidjan", "Africa/Malabo", "Africa/Banjul", "Africa/Accra", "Africa/Conakry", "Africa/Bissau", "Africa/Monrovia", "Africa/Bamako", "Africa/Timbuktu", "Africa/Nouakchott", "Africa/Niamey", "Africa/Sao_Tome", "Africa/Dakar", "Africa/Freetown", "Africa/Lome" });
/*      */ 
/*  505 */     tempAbbrMap.put("GST", new String[] { "Atlantic/South_Georgia", "Asia/Bahrain", "Asia/Muscat", "Asia/Qatar", "Asia/Dubai", "Pacific/Guam" });
/*      */ 
/*  508 */     tempAbbrMap.put("GYT", new String[] { "America/Guyana" });
/*  509 */     tempAbbrMap.put("HADT", new String[] { "America/Adak" });
/*  510 */     tempAbbrMap.put("HART", new String[] { "Asia/Harbin" });
/*  511 */     tempAbbrMap.put("HAST", new String[] { "America/Adak" });
/*  512 */     tempAbbrMap.put("HAWT", new String[] { "America/Adak" });
/*  513 */     tempAbbrMap.put("HDT", new String[] { "Pacific/Honolulu" });
/*  514 */     tempAbbrMap.put("HKST", new String[] { "Asia/Hong_Kong" });
/*  515 */     tempAbbrMap.put("HKT", new String[] { "Asia/Hong_Kong" });
/*  516 */     tempAbbrMap.put("HMT", new String[] { "Atlantic/Azores", "Europe/Helsinki", "Asia/Dacca", "Asia/Calcutta", "America/Havana" });
/*      */ 
/*  518 */     tempAbbrMap.put("HOVST", new String[] { "Asia/Hovd" });
/*  519 */     tempAbbrMap.put("HOVT", new String[] { "Asia/Hovd" });
/*  520 */     tempAbbrMap.put("HST", new String[] { "Pacific/Johnston", "Pacific/Honolulu" });
/*      */ 
/*  522 */     tempAbbrMap.put("HWT", new String[] { "Pacific/Honolulu" });
/*  523 */     tempAbbrMap.put("ICT", new String[] { "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Bangkok", "Asia/Saigon" });
/*      */ 
/*  525 */     tempAbbrMap.put("IDDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  526 */     tempAbbrMap.put("IDT", new String[] { "Asia/Jerusalem", "Asia/Gaza" });
/*  527 */     tempAbbrMap.put("IHST", new String[] { "Asia/Colombo" });
/*  528 */     tempAbbrMap.put("IMT", new String[] { "Europe/Sofia", "Europe/Istanbul", "Asia/Irkutsk" });
/*      */ 
/*  530 */     tempAbbrMap.put("IOT", new String[] { "Indian/Chagos" });
/*  531 */     tempAbbrMap.put("IRKMT", new String[] { "Asia/Irkutsk" });
/*  532 */     tempAbbrMap.put("IRKST", new String[] { "Asia/Irkutsk" });
/*  533 */     tempAbbrMap.put("IRKT", new String[] { "Asia/Irkutsk" });
/*  534 */     tempAbbrMap.put("IRST", new String[] { "Asia/Tehran" });
/*  535 */     tempAbbrMap.put("IRT", new String[] { "Asia/Tehran" });
/*  536 */     tempAbbrMap.put("ISST", new String[] { "Atlantic/Reykjavik" });
/*  537 */     tempAbbrMap.put("IST", new String[] { "Atlantic/Reykjavik", "Europe/Belfast", "Europe/Dublin", "Asia/Dacca", "Asia/Thimbu", "Asia/Calcutta", "Asia/Jerusalem", "Asia/Katmandu", "Asia/Karachi", "Asia/Gaza", "Asia/Colombo" });
/*      */ 
/*  541 */     tempAbbrMap.put("JAYT", new String[] { "Asia/Jayapura" });
/*  542 */     tempAbbrMap.put("JMT", new String[] { "Atlantic/St_Helena", "Asia/Jerusalem" });
/*      */ 
/*  544 */     tempAbbrMap.put("JST", new String[] { "Asia/Rangoon", "Asia/Dili", "Asia/Ujung_Pandang", "Asia/Tokyo", "Asia/Kuala_Lumpur", "Asia/Kuching", "Asia/Manila", "Asia/Singapore", "Pacific/Nauru" });
/*      */ 
/*  548 */     tempAbbrMap.put("KART", new String[] { "Asia/Karachi" });
/*  549 */     tempAbbrMap.put("KAST", new String[] { "Asia/Kashgar" });
/*  550 */     tempAbbrMap.put("KDT", new String[] { "Asia/Seoul" });
/*  551 */     tempAbbrMap.put("KGST", new String[] { "Asia/Bishkek" });
/*  552 */     tempAbbrMap.put("KGT", new String[] { "Asia/Bishkek" });
/*  553 */     tempAbbrMap.put("KMT", new String[] { "Europe/Vilnius", "Europe/Kiev", "America/Cayman", "America/Jamaica", "America/St_Vincent", "America/Grand_Turk" });
/*      */ 
/*  556 */     tempAbbrMap.put("KOST", new String[] { "Pacific/Kosrae" });
/*  557 */     tempAbbrMap.put("KRAMT", new String[] { "Asia/Krasnoyarsk" });
/*  558 */     tempAbbrMap.put("KRAST", new String[] { "Asia/Krasnoyarsk" });
/*  559 */     tempAbbrMap.put("KRAT", new String[] { "Asia/Krasnoyarsk" });
/*  560 */     tempAbbrMap.put("KST", new String[] { "Asia/Seoul", "Asia/Pyongyang" });
/*  561 */     tempAbbrMap.put("KUYMT", new String[] { "Europe/Samara" });
/*  562 */     tempAbbrMap.put("KUYST", new String[] { "Europe/Samara" });
/*  563 */     tempAbbrMap.put("KUYT", new String[] { "Europe/Samara" });
/*  564 */     tempAbbrMap.put("KWAT", new String[] { "Pacific/Kwajalein" });
/*  565 */     tempAbbrMap.put("LHST", new String[] { "Australia/Lord_Howe" });
/*  566 */     tempAbbrMap.put("LINT", new String[] { "Pacific/Kiritimati" });
/*  567 */     tempAbbrMap.put("LKT", new String[] { "Asia/Colombo" });
/*  568 */     tempAbbrMap.put("LPMT", new String[] { "America/La_Paz" });
/*  569 */     tempAbbrMap.put("LRT", new String[] { "Africa/Monrovia" });
/*  570 */     tempAbbrMap.put("LST", new String[] { "Europe/Riga" });
/*  571 */     tempAbbrMap.put("M", new String[] { "Europe/Moscow" });
/*  572 */     tempAbbrMap.put("MADST", new String[] { "Atlantic/Madeira" });
/*  573 */     tempAbbrMap.put("MAGMT", new String[] { "Asia/Magadan" });
/*  574 */     tempAbbrMap.put("MAGST", new String[] { "Asia/Magadan" });
/*  575 */     tempAbbrMap.put("MAGT", new String[] { "Asia/Magadan" });
/*  576 */     tempAbbrMap.put("MALT", new String[] { "Asia/Kuala_Lumpur", "Asia/Singapore" });
/*      */ 
/*  578 */     tempAbbrMap.put("MART", new String[] { "Pacific/Marquesas" });
/*  579 */     tempAbbrMap.put("MAWT", new String[] { "Antarctica/Mawson" });
/*  580 */     tempAbbrMap.put("MDDT", new String[] { "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik" });
/*      */ 
/*  582 */     tempAbbrMap.put("MDST", new String[] { "Europe/Moscow" });
/*  583 */     tempAbbrMap.put("MDT", new String[] { "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan" });
/*      */ 
/*  588 */     tempAbbrMap.put("MEST", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  602 */     tempAbbrMap.put("MET", new String[] { "Europe/Tirane", "Europe/Andorra", "Europe/Vienna", "Europe/Minsk", "Europe/Brussels", "Europe/Sofia", "Europe/Prague", "Europe/Copenhagen", "Europe/Tallinn", "Europe/Berlin", "Europe/Gibraltar", "Europe/Athens", "Europe/Budapest", "Europe/Rome", "Europe/Riga", "Europe/Vaduz", "Europe/Vilnius", "Europe/Luxembourg", "Europe/Malta", "Europe/Chisinau", "Europe/Tiraspol", "Europe/Monaco", "Europe/Amsterdam", "Europe/Oslo", "Europe/Warsaw", "Europe/Lisbon", "Europe/Kaliningrad", "Europe/Madrid", "Europe/Stockholm", "Europe/Zurich", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol", "Europe/Belgrade", "Africa/Algiers", "Africa/Tripoli", "Africa/Casablanca", "Africa/Tunis", "Africa/Ceuta" });
/*      */ 
/*  616 */     tempAbbrMap.put("MHT", new String[] { "Pacific/Majuro", "Pacific/Kwajalein" });
/*      */ 
/*  618 */     tempAbbrMap.put("MMT", new String[] { "Indian/Maldives", "Europe/Minsk", "Europe/Moscow", "Asia/Rangoon", "Asia/Ujung_Pandang", "Asia/Colombo", "Pacific/Easter", "Africa/Monrovia", "America/Managua", "America/Montevideo" });
/*      */ 
/*  622 */     tempAbbrMap.put("MOST", new String[] { "Asia/Macao" });
/*  623 */     tempAbbrMap.put("MOT", new String[] { "Asia/Macao" });
/*  624 */     tempAbbrMap.put("MPT", new String[] { "Pacific/Saipan" });
/*  625 */     tempAbbrMap.put("MSK", new String[] { "Europe/Minsk", "Europe/Tallinn", "Europe/Riga", "Europe/Vilnius", "Europe/Chisinau", "Europe/Kiev", "Europe/Uzhgorod", "Europe/Zaporozhye", "Europe/Simferopol" });
/*      */ 
/*  629 */     tempAbbrMap.put("MST", new String[] { "Europe/Moscow", "America/Denver", "America/Phoenix", "America/Boise", "America/Regina", "America/Swift_Current", "America/Edmonton", "America/Dawson_Creek", "America/Cambridge_Bay", "America/Yellowknife", "America/Inuvik", "America/Mexico_City", "America/Chihuahua", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*  636 */     tempAbbrMap.put("MUT", new String[] { "Indian/Mauritius" });
/*  637 */     tempAbbrMap.put("MVT", new String[] { "Indian/Maldives" });
/*  638 */     tempAbbrMap.put("MWT", new String[] { "America/Denver", "America/Phoenix", "America/Boise" });
/*      */ 
/*  640 */     tempAbbrMap.put("MYT", new String[] { "Asia/Kuala_Lumpur", "Asia/Kuching" });
/*      */ 
/*  643 */     tempAbbrMap.put("NCST", new String[] { "Pacific/Noumea" });
/*  644 */     tempAbbrMap.put("NCT", new String[] { "Pacific/Noumea" });
/*  645 */     tempAbbrMap.put("NDT", new String[] { "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*  647 */     tempAbbrMap.put("NEGT", new String[] { "America/Paramaribo" });
/*  648 */     tempAbbrMap.put("NFT", new String[] { "Europe/Paris", "Europe/Oslo", "Pacific/Norfolk" });
/*      */ 
/*  650 */     tempAbbrMap.put("NMT", new String[] { "Pacific/Norfolk" });
/*  651 */     tempAbbrMap.put("NOVMT", new String[] { "Asia/Novosibirsk" });
/*  652 */     tempAbbrMap.put("NOVST", new String[] { "Asia/Novosibirsk" });
/*  653 */     tempAbbrMap.put("NOVT", new String[] { "Asia/Novosibirsk" });
/*  654 */     tempAbbrMap.put("NPT", new String[] { "Asia/Katmandu" });
/*  655 */     tempAbbrMap.put("NRT", new String[] { "Pacific/Nauru" });
/*  656 */     tempAbbrMap.put("NST", new String[] { "Europe/Amsterdam", "Pacific/Pago_Pago", "Pacific/Midway", "America/Nome", "America/Adak", "America/St_Johns", "America/Goose_Bay" });
/*      */ 
/*  659 */     tempAbbrMap.put("NUT", new String[] { "Pacific/Niue" });
/*  660 */     tempAbbrMap.put("NWT", new String[] { "America/Nome", "America/Adak" });
/*  661 */     tempAbbrMap.put("NZDT", new String[] { "Antarctica/McMurdo" });
/*  662 */     tempAbbrMap.put("NZHDT", new String[] { "Pacific/Auckland" });
/*  663 */     tempAbbrMap.put("NZST", new String[] { "Antarctica/McMurdo", "Pacific/Auckland" });
/*      */ 
/*  665 */     tempAbbrMap.put("OMSMT", new String[] { "Asia/Omsk" });
/*  666 */     tempAbbrMap.put("OMSST", new String[] { "Asia/Omsk" });
/*  667 */     tempAbbrMap.put("OMST", new String[] { "Asia/Omsk" });
/*  668 */     tempAbbrMap.put("PDDT", new String[] { "America/Inuvik", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  670 */     tempAbbrMap.put("PDT", new String[] { "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Tijuana" });
/*      */ 
/*  674 */     tempAbbrMap.put("PEST", new String[] { "America/Lima" });
/*  675 */     tempAbbrMap.put("PET", new String[] { "America/Lima" });
/*  676 */     tempAbbrMap.put("PETMT", new String[] { "Asia/Kamchatka" });
/*  677 */     tempAbbrMap.put("PETST", new String[] { "Asia/Kamchatka" });
/*  678 */     tempAbbrMap.put("PETT", new String[] { "Asia/Kamchatka" });
/*  679 */     tempAbbrMap.put("PGT", new String[] { "Pacific/Port_Moresby" });
/*  680 */     tempAbbrMap.put("PHOT", new String[] { "Pacific/Enderbury" });
/*  681 */     tempAbbrMap.put("PHST", new String[] { "Asia/Manila" });
/*  682 */     tempAbbrMap.put("PHT", new String[] { "Asia/Manila" });
/*  683 */     tempAbbrMap.put("PKT", new String[] { "Asia/Karachi" });
/*  684 */     tempAbbrMap.put("PMDT", new String[] { "America/Miquelon" });
/*  685 */     tempAbbrMap.put("PMMT", new String[] { "Pacific/Port_Moresby" });
/*  686 */     tempAbbrMap.put("PMST", new String[] { "America/Miquelon" });
/*  687 */     tempAbbrMap.put("PMT", new String[] { "Antarctica/DumontDUrville", "Europe/Prague", "Europe/Paris", "Europe/Monaco", "Africa/Algiers", "Africa/Tunis", "America/Panama", "America/Paramaribo" });
/*      */ 
/*  691 */     tempAbbrMap.put("PNT", new String[] { "Pacific/Pitcairn" });
/*  692 */     tempAbbrMap.put("PONT", new String[] { "Pacific/Ponape" });
/*  693 */     tempAbbrMap.put("PPMT", new String[] { "America/Port-au-Prince" });
/*  694 */     tempAbbrMap.put("PST", new String[] { "Pacific/Pitcairn", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Vancouver", "America/Dawson_Creek", "America/Inuvik", "America/Whitehorse", "America/Dawson", "America/Hermosillo", "America/Mazatlan", "America/Tijuana" });
/*      */ 
/*  699 */     tempAbbrMap.put("PWT", new String[] { "Pacific/Palau", "America/Los_Angeles", "America/Juneau", "America/Boise", "America/Tijuana" });
/*      */ 
/*  702 */     tempAbbrMap.put("PYST", new String[] { "America/Asuncion" });
/*  703 */     tempAbbrMap.put("PYT", new String[] { "America/Asuncion" });
/*  704 */     tempAbbrMap.put("QMT", new String[] { "America/Guayaquil" });
/*  705 */     tempAbbrMap.put("RET", new String[] { "Indian/Reunion" });
/*  706 */     tempAbbrMap.put("RMT", new String[] { "Atlantic/Reykjavik", "Europe/Rome", "Europe/Riga", "Asia/Rangoon" });
/*      */ 
/*  708 */     tempAbbrMap.put("S", new String[] { "Europe/Moscow" });
/*  709 */     tempAbbrMap.put("SAMMT", new String[] { "Europe/Samara" });
/*  710 */     tempAbbrMap.put("SAMST", new String[] { "Europe/Samara", "Asia/Samarkand" });
/*      */ 
/*  713 */     tempAbbrMap.put("SAMT", new String[] { "Europe/Samara", "Asia/Samarkand", "Pacific/Pago_Pago", "Pacific/Apia" });
/*      */ 
/*  715 */     tempAbbrMap.put("SAST", new String[] { "Africa/Maseru", "Africa/Windhoek", "Africa/Johannesburg", "Africa/Mbabane" });
/*      */ 
/*  717 */     tempAbbrMap.put("SBT", new String[] { "Pacific/Guadalcanal" });
/*  718 */     tempAbbrMap.put("SCT", new String[] { "Indian/Mahe" });
/*  719 */     tempAbbrMap.put("SDMT", new String[] { "America/Santo_Domingo" });
/*  720 */     tempAbbrMap.put("SGT", new String[] { "Asia/Singapore" });
/*  721 */     tempAbbrMap.put("SHEST", new String[] { "Asia/Aqtau" });
/*  722 */     tempAbbrMap.put("SHET", new String[] { "Asia/Aqtau" });
/*  723 */     tempAbbrMap.put("SJMT", new String[] { "America/Costa_Rica" });
/*  724 */     tempAbbrMap.put("SLST", new String[] { "Africa/Freetown" });
/*  725 */     tempAbbrMap.put("SMT", new String[] { "Atlantic/Stanley", "Europe/Stockholm", "Europe/Simferopol", "Asia/Phnom_Penh", "Asia/Vientiane", "Asia/Kuala_Lumpur", "Asia/Singapore", "Asia/Saigon", "America/Santiago" });
/*      */ 
/*  729 */     tempAbbrMap.put("SRT", new String[] { "America/Paramaribo" });
/*  730 */     tempAbbrMap.put("SST", new String[] { "Pacific/Pago_Pago", "Pacific/Midway" });
/*      */ 
/*  732 */     tempAbbrMap.put("SVEMT", new String[] { "Asia/Yekaterinburg" });
/*  733 */     tempAbbrMap.put("SVEST", new String[] { "Asia/Yekaterinburg" });
/*  734 */     tempAbbrMap.put("SVET", new String[] { "Asia/Yekaterinburg" });
/*  735 */     tempAbbrMap.put("SWAT", new String[] { "Africa/Windhoek" });
/*  736 */     tempAbbrMap.put("SYOT", new String[] { "Antarctica/Syowa" });
/*  737 */     tempAbbrMap.put("TAHT", new String[] { "Pacific/Tahiti" });
/*  738 */     tempAbbrMap.put("TASST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*      */ 
/*  741 */     tempAbbrMap.put("TAST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  742 */     tempAbbrMap.put("TBIST", new String[] { "Asia/Tbilisi" });
/*  743 */     tempAbbrMap.put("TBIT", new String[] { "Asia/Tbilisi" });
/*  744 */     tempAbbrMap.put("TBMT", new String[] { "Asia/Tbilisi" });
/*  745 */     tempAbbrMap.put("TFT", new String[] { "Indian/Kerguelen" });
/*  746 */     tempAbbrMap.put("TJT", new String[] { "Asia/Dushanbe" });
/*  747 */     tempAbbrMap.put("TKT", new String[] { "Pacific/Fakaofo" });
/*  748 */     tempAbbrMap.put("TMST", new String[] { "Asia/Ashkhabad" });
/*  749 */     tempAbbrMap.put("TMT", new String[] { "Europe/Tallinn", "Asia/Tehran", "Asia/Ashkhabad" });
/*      */ 
/*  751 */     tempAbbrMap.put("TOST", new String[] { "Pacific/Tongatapu" });
/*  752 */     tempAbbrMap.put("TOT", new String[] { "Pacific/Tongatapu" });
/*  753 */     tempAbbrMap.put("TPT", new String[] { "Asia/Dili" });
/*  754 */     tempAbbrMap.put("TRST", new String[] { "Europe/Istanbul" });
/*  755 */     tempAbbrMap.put("TRT", new String[] { "Europe/Istanbul" });
/*  756 */     tempAbbrMap.put("TRUT", new String[] { "Pacific/Truk" });
/*  757 */     tempAbbrMap.put("TVT", new String[] { "Pacific/Funafuti" });
/*  758 */     tempAbbrMap.put("ULAST", new String[] { "Asia/Ulaanbaatar" });
/*  759 */     tempAbbrMap.put("ULAT", new String[] { "Asia/Ulaanbaatar" });
/*  760 */     tempAbbrMap.put("URUT", new String[] { "Asia/Urumqi" });
/*  761 */     tempAbbrMap.put("UYHST", new String[] { "America/Montevideo" });
/*  762 */     tempAbbrMap.put("UYT", new String[] { "America/Montevideo" });
/*  763 */     tempAbbrMap.put("UZST", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  764 */     tempAbbrMap.put("UZT", new String[] { "Asia/Samarkand", "Asia/Tashkent" });
/*  765 */     tempAbbrMap.put("VET", new String[] { "America/Caracas" });
/*  766 */     tempAbbrMap.put("VLAMT", new String[] { "Asia/Vladivostok" });
/*  767 */     tempAbbrMap.put("VLAST", new String[] { "Asia/Vladivostok" });
/*  768 */     tempAbbrMap.put("VLAT", new String[] { "Asia/Vladivostok" });
/*  769 */     tempAbbrMap.put("VUST", new String[] { "Pacific/Efate" });
/*  770 */     tempAbbrMap.put("VUT", new String[] { "Pacific/Efate" });
/*  771 */     tempAbbrMap.put("WAKT", new String[] { "Pacific/Wake" });
/*  772 */     tempAbbrMap.put("WARST", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*  774 */     tempAbbrMap.put("WART", new String[] { "America/Jujuy", "America/Mendoza" });
/*      */ 
/*  777 */     tempAbbrMap.put("WAST", new String[] { "Africa/Ndjamena", "Africa/Windhoek" });
/*      */ 
/*  779 */     tempAbbrMap.put("WAT", new String[] { "Africa/Luanda", "Africa/Porto-Novo", "Africa/Douala", "Africa/Bangui", "Africa/Ndjamena", "Africa/Kinshasa", "Africa/Brazzaville", "Africa/Malabo", "Africa/Libreville", "Africa/Banjul", "Africa/Conakry", "Africa/Bissau", "Africa/Bamako", "Africa/Nouakchott", "Africa/El_Aaiun", "Africa/Windhoek", "Africa/Niamey", "Africa/Lagos", "Africa/Dakar", "Africa/Freetown" });
/*      */ 
/*  786 */     tempAbbrMap.put("WEST", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/Ceuta" });
/*      */ 
/*  791 */     tempAbbrMap.put("WET", new String[] { "Atlantic/Faeroe", "Atlantic/Azores", "Atlantic/Madeira", "Atlantic/Canary", "Europe/Andorra", "Europe/Brussels", "Europe/Luxembourg", "Europe/Monaco", "Europe/Lisbon", "Europe/Madrid", "Africa/Algiers", "Africa/Casablanca", "Africa/El_Aaiun", "Africa/Ceuta" });
/*      */ 
/*  796 */     tempAbbrMap.put("WFT", new String[] { "Pacific/Wallis" });
/*  797 */     tempAbbrMap.put("WGST", new String[] { "America/Godthab" });
/*  798 */     tempAbbrMap.put("WGT", new String[] { "America/Godthab" });
/*  799 */     tempAbbrMap.put("WMT", new String[] { "Europe/Vilnius", "Europe/Warsaw" });
/*  800 */     tempAbbrMap.put("WST", new String[] { "Antarctica/Casey", "Pacific/Apia", "Australia/Perth" });
/*      */ 
/*  802 */     tempAbbrMap.put("YAKMT", new String[] { "Asia/Yakutsk" });
/*  803 */     tempAbbrMap.put("YAKST", new String[] { "Asia/Yakutsk" });
/*  804 */     tempAbbrMap.put("YAKT", new String[] { "Asia/Yakutsk" });
/*  805 */     tempAbbrMap.put("YAPT", new String[] { "Pacific/Yap" });
/*  806 */     tempAbbrMap.put("YDDT", new String[] { "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  808 */     tempAbbrMap.put("YDT", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  810 */     tempAbbrMap.put("YEKMT", new String[] { "Asia/Yekaterinburg" });
/*  811 */     tempAbbrMap.put("YEKST", new String[] { "Asia/Yekaterinburg" });
/*  812 */     tempAbbrMap.put("YEKT", new String[] { "Asia/Yekaterinburg" });
/*  813 */     tempAbbrMap.put("YERST", new String[] { "Asia/Yerevan" });
/*  814 */     tempAbbrMap.put("YERT", new String[] { "Asia/Yerevan" });
/*  815 */     tempAbbrMap.put("YST", new String[] { "America/Yakutat", "America/Whitehorse", "America/Dawson" });
/*      */ 
/*  817 */     tempAbbrMap.put("YWT", new String[] { "America/Yakutat" });
/*      */ 
/*  819 */     ABBREVIATED_TIMEZONES = Collections.unmodifiableMap(tempAbbrMap);
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.TimeUtil
 * JD-Core Version:    0.6.0
 */