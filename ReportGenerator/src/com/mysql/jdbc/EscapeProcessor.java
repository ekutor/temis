/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ class EscapeProcessor
/*     */ {
/*     */   private static Map<String, String> JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
/*     */   private static Map<String, String> JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;
/*     */ 
/*     */   public static final Object escapeSQL(String sql, boolean serverSupportsConvertFn, MySQLConnection conn)
/*     */     throws SQLException
/*     */   {
/* 106 */     boolean replaceEscapeSequence = false;
/* 107 */     String escapeSequence = null;
/*     */ 
/* 109 */     if (sql == null) {
/* 110 */       return null;
/*     */     }
/*     */ 
/* 117 */     int beginBrace = sql.indexOf('{');
/* 118 */     int nextEndBrace = beginBrace == -1 ? -1 : sql.indexOf('}', beginBrace);
/*     */ 
/* 121 */     if (nextEndBrace == -1) {
/* 122 */       return sql;
/*     */     }
/*     */ 
/* 125 */     StringBuffer newSql = new StringBuffer();
/*     */ 
/* 127 */     EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
/*     */ 
/* 129 */     byte usesVariables = 0;
/* 130 */     boolean callingStoredFunction = false;
/*     */ 
/* 132 */     while (escapeTokenizer.hasMoreTokens()) {
/* 133 */       String token = escapeTokenizer.nextToken();
/*     */ 
/* 135 */       if (token.length() != 0) {
/* 136 */         if (token.charAt(0) == '{')
/*     */         {
/* 138 */           if (!token.endsWith("}")) {
/* 139 */             throw SQLError.createSQLException("Not a valid escape sequence: " + token, conn.getExceptionInterceptor());
/*     */           }
/*     */ 
/* 144 */           if (token.length() > 2) {
/* 145 */             int nestedBrace = token.indexOf('{', 2);
/*     */ 
/* 147 */             if (nestedBrace != -1) {
/* 148 */               StringBuffer buf = new StringBuffer(token.substring(0, 1));
/*     */ 
/* 151 */               Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), serverSupportsConvertFn, conn);
/*     */ 
/* 155 */               String remaining = null;
/*     */ 
/* 157 */               if ((remainingResults instanceof String)) {
/* 158 */                 remaining = (String)remainingResults;
/*     */               } else {
/* 160 */                 remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
/*     */ 
/* 162 */                 if (usesVariables != 1) {
/* 163 */                   usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
/*     */                 }
/*     */               }
/*     */ 
/* 167 */               buf.append(remaining);
/*     */ 
/* 169 */               buf.append('}');
/*     */ 
/* 171 */               token = buf.toString();
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 177 */           String collapsedToken = removeWhitespace(token);
/*     */ 
/* 182 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape"))
/*     */           {
/*     */             try {
/* 185 */               StringTokenizer st = new StringTokenizer(token, " '");
/*     */ 
/* 187 */               st.nextToken();
/* 188 */               escapeSequence = st.nextToken();
/*     */ 
/* 190 */               if (escapeSequence.length() < 3) {
/* 191 */                 newSql.append(token);
/*     */               }
/*     */               else
/*     */               {
/* 197 */                 escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
/*     */ 
/* 199 */                 replaceEscapeSequence = true;
/*     */               }
/*     */             } catch (NoSuchElementException e) {
/* 202 */               newSql.append(token);
/*     */             }
/*     */ 
/*     */           }
/* 207 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn"))
/*     */           {
/* 209 */             int startPos = token.toLowerCase().indexOf("fn ") + 3;
/* 210 */             int endPos = token.length() - 1;
/*     */ 
/* 212 */             String fnToken = token.substring(startPos, endPos);
/*     */ 
/* 216 */             if (StringUtils.startsWithIgnoreCaseAndWs(fnToken, "convert"))
/*     */             {
/* 218 */               newSql.append(processConvertToken(fnToken, serverSupportsConvertFn, conn));
/*     */             }
/*     */             else
/*     */             {
/* 222 */               newSql.append(fnToken);
/*     */             }
/* 224 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d"))
/*     */           {
/* 226 */             int startPos = token.indexOf('\'') + 1;
/* 227 */             int endPos = token.lastIndexOf('\'');
/*     */ 
/* 229 */             if ((startPos == -1) || (endPos == -1)) {
/* 230 */               newSql.append(token);
/*     */             }
/*     */             else
/*     */             {
/* 236 */               String argument = token.substring(startPos, endPos);
/*     */               try
/*     */               {
/* 239 */                 StringTokenizer st = new StringTokenizer(argument, " -");
/*     */ 
/* 241 */                 String year4 = st.nextToken();
/* 242 */                 String month2 = st.nextToken();
/* 243 */                 String day2 = st.nextToken();
/* 244 */                 String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
/*     */ 
/* 246 */                 newSql.append(dateString);
/*     */               } catch (NoSuchElementException e) {
/* 248 */                 throw SQLError.createSQLException("Syntax error for DATE escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/* 253 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts"))
/*     */           {
/* 255 */             processTimestampToken(conn, newSql, token);
/* 256 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t"))
/*     */           {
/* 258 */             processTimeToken(conn, newSql, token);
/* 259 */           } else if ((StringUtils.startsWithIgnoreCase(collapsedToken, "{call")) || (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")))
/*     */           {
/* 264 */             int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
/*     */ 
/* 266 */             int endPos = token.length() - 1;
/*     */ 
/* 268 */             if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call"))
/*     */             {
/* 270 */               callingStoredFunction = true;
/* 271 */               newSql.append("SELECT ");
/* 272 */               newSql.append(token.substring(startPos, endPos));
/*     */             } else {
/* 274 */               callingStoredFunction = false;
/* 275 */               newSql.append("CALL ");
/* 276 */               newSql.append(token.substring(startPos, endPos));
/*     */             }
/*     */ 
/* 279 */             for (int i = endPos - 1; i >= startPos; i--) {
/* 280 */               char c = token.charAt(i);
/*     */ 
/* 282 */               if (Character.isWhitespace(c))
/*     */               {
/*     */                 continue;
/*     */               }
/* 286 */               if (c == ')') break;
/* 287 */               newSql.append("()"); break;
/*     */             }
/*     */ 
/*     */           }
/* 295 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj"))
/*     */           {
/* 299 */             newSql.append(token);
/*     */           }
/*     */           else {
/* 302 */             newSql.append(token);
/*     */           }
/*     */         } else {
/* 305 */           newSql.append(token);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 310 */     String escapedSql = newSql.toString();
/*     */ 
/* 316 */     if (replaceEscapeSequence) {
/* 317 */       String currentSql = escapedSql;
/*     */ 
/* 319 */       while (currentSql.indexOf(escapeSequence) != -1) {
/* 320 */         int escapePos = currentSql.indexOf(escapeSequence);
/* 321 */         String lhs = currentSql.substring(0, escapePos);
/* 322 */         String rhs = currentSql.substring(escapePos + 1, currentSql.length());
/*     */ 
/* 324 */         currentSql = lhs + "\\" + rhs;
/*     */       }
/*     */ 
/* 327 */       escapedSql = currentSql;
/*     */     }
/*     */ 
/* 330 */     EscapeProcessorResult epr = new EscapeProcessorResult();
/* 331 */     epr.escapedSql = escapedSql;
/* 332 */     epr.callingStoredFunction = callingStoredFunction;
/*     */ 
/* 334 */     if (usesVariables != 1) {
/* 335 */       if (escapeTokenizer.sawVariableUse())
/* 336 */         epr.usesVariables = 1;
/*     */       else {
/* 338 */         epr.usesVariables = 0;
/*     */       }
/*     */     }
/*     */ 
/* 342 */     return epr;
/*     */   }
/*     */ 
/*     */   private static void processTimeToken(MySQLConnection conn, StringBuffer newSql, String token) throws SQLException
/*     */   {
/* 347 */     int startPos = token.indexOf('\'') + 1;
/* 348 */     int endPos = token.lastIndexOf('\'');
/*     */ 
/* 350 */     if ((startPos == -1) || (endPos == -1)) {
/* 351 */       newSql.append(token);
/*     */     }
/*     */     else
/*     */     {
/* 357 */       String argument = token.substring(startPos, endPos);
/*     */       try
/*     */       {
/* 360 */         StringTokenizer st = new StringTokenizer(argument, " :.");
/*     */ 
/* 362 */         String hour = st.nextToken();
/* 363 */         String minute = st.nextToken();
/* 364 */         String second = st.nextToken();
/*     */ 
/* 366 */         boolean serverSupportsFractionalSecond = false;
/* 367 */         String fractionalSecond = "";
/*     */ 
/* 369 */         if ((st.hasMoreTokens()) && 
/* 370 */           (conn.versionMeetsMinimum(5, 6, 4))) {
/* 371 */           serverSupportsFractionalSecond = true;
/* 372 */           fractionalSecond = "." + st.nextToken();
/*     */         }
/*     */ 
/* 376 */         if ((conn != null) && ((!conn.getUseTimezone()) || (!conn.getUseLegacyDatetimeCode()))) {
/* 377 */           newSql.append("'");
/* 378 */           newSql.append(hour);
/* 379 */           newSql.append(":");
/* 380 */           newSql.append(minute);
/* 381 */           newSql.append(":");
/* 382 */           newSql.append(second);
/* 383 */           newSql.append(fractionalSecond);
/* 384 */           newSql.append("'");
/*     */         } else {
/* 386 */           Calendar sessionCalendar = null;
/*     */ 
/* 388 */           if (conn != null) {
/* 389 */             sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */           }
/*     */           else {
/* 392 */             sessionCalendar = new GregorianCalendar();
/*     */           }
/*     */           try
/*     */           {
/* 396 */             int hourInt = Integer.parseInt(hour);
/* 397 */             int minuteInt = Integer.parseInt(minute);
/*     */ 
/* 399 */             int secondInt = Integer.parseInt(second);
/*     */ 
/* 402 */             synchronized (sessionCalendar) {
/* 403 */               Time toBeAdjusted = TimeUtil.fastTimeCreate(sessionCalendar, hourInt, minuteInt, secondInt, conn.getExceptionInterceptor());
/*     */ 
/* 409 */               Time inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */ 
/* 421 */               newSql.append("'");
/* 422 */               newSql.append(inServerTimezone.toString());
/*     */ 
/* 425 */               if (serverSupportsFractionalSecond) {
/* 426 */                 newSql.append(fractionalSecond);
/*     */               }
/*     */ 
/* 429 */               newSql.append("'");
/*     */             }
/*     */           }
/*     */           catch (NumberFormatException nfe) {
/* 433 */             throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009", conn.getExceptionInterceptor());
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (NoSuchElementException e)
/*     */       {
/* 441 */         throw SQLError.createSQLException("Syntax error for escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void processTimestampToken(MySQLConnection conn, StringBuffer newSql, String token)
/*     */     throws SQLException
/*     */   {
/* 450 */     int startPos = token.indexOf('\'') + 1;
/* 451 */     int endPos = token.lastIndexOf('\'');
/*     */ 
/* 453 */     if ((startPos == -1) || (endPos == -1)) {
/* 454 */       newSql.append(token);
/*     */     }
/*     */     else
/*     */     {
/* 460 */       String argument = token.substring(startPos, endPos);
/*     */       try
/*     */       {
/* 463 */         if ((conn != null) && (!conn.getUseLegacyDatetimeCode())) {
/* 464 */           Timestamp ts = Timestamp.valueOf(argument);
/* 465 */           SimpleDateFormat tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*     */ 
/* 468 */           tsdf.setTimeZone(conn.getServerTimezoneTZ());
/*     */ 
/* 472 */           newSql.append(tsdf.format(ts));
/*     */ 
/* 474 */           if ((ts.getNanos() > 0) && (conn.versionMeetsMinimum(5, 6, 4))) {
/* 475 */             newSql.append('.');
/* 476 */             newSql.append(TimeUtil.formatNanos(ts.getNanos(), true));
/*     */           }
/*     */ 
/* 479 */           newSql.append('\'');
/*     */         }
/*     */         else {
/* 482 */           StringTokenizer st = new StringTokenizer(argument, " .-:");
/*     */           try
/*     */           {
/* 485 */             String year4 = st.nextToken();
/* 486 */             String month2 = st.nextToken();
/* 487 */             String day2 = st.nextToken();
/* 488 */             String hour = st.nextToken();
/* 489 */             String minute = st.nextToken();
/* 490 */             String second = st.nextToken();
/*     */ 
/* 492 */             boolean serverSupportsFractionalSecond = false;
/* 493 */             String fractionalSecond = "";
/* 494 */             if ((st.hasMoreTokens()) && 
/* 495 */               (conn.versionMeetsMinimum(5, 6, 4))) {
/* 496 */               serverSupportsFractionalSecond = true;
/* 497 */               fractionalSecond = "." + st.nextToken();
/*     */             }
/*     */ 
/* 520 */             if ((conn != null) && (!conn.getUseTimezone()) && (!conn.getUseJDBCCompliantTimezoneShift()))
/*     */             {
/* 523 */               newSql.append("'").append(year4).append("-").append(month2).append("-").append(day2).append(" ").append(hour).append(":").append(minute).append(":").append(second).append(fractionalSecond).append("'");
/*     */             }
/*     */             else
/*     */             {
/*     */               Calendar sessionCalendar;
/*     */               Calendar sessionCalendar;
/* 534 */               if (conn != null) {
/* 535 */                 sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */               }
/*     */               else {
/* 538 */                 sessionCalendar = new GregorianCalendar();
/* 539 */                 sessionCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */               }
/*     */ 
/*     */               try
/*     */               {
/* 545 */                 int year4Int = Integer.parseInt(year4);
/*     */ 
/* 547 */                 int month2Int = Integer.parseInt(month2);
/*     */ 
/* 549 */                 int day2Int = Integer.parseInt(day2);
/*     */ 
/* 551 */                 int hourInt = Integer.parseInt(hour);
/*     */ 
/* 553 */                 int minuteInt = Integer.parseInt(minute);
/*     */ 
/* 555 */                 int secondInt = Integer.parseInt(second);
/*     */ 
/* 558 */                 synchronized (sessionCalendar) {
/* 559 */                   boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
/*     */ 
/* 562 */                   Timestamp toBeAdjusted = TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? Calendar.getInstance(TimeZone.getTimeZone("GMT")) : null, sessionCalendar, year4Int, month2Int, day2Int, hourInt, minuteInt, secondInt, 0);
/*     */ 
/* 578 */                   Timestamp inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */ 
/* 590 */                   newSql.append("'");
/*     */ 
/* 592 */                   String timezoneLiteral = inServerTimezone.toString();
/*     */ 
/* 595 */                   int indexOfDot = timezoneLiteral.indexOf(".");
/*     */ 
/* 598 */                   if (indexOfDot != -1) {
/* 599 */                     timezoneLiteral = timezoneLiteral.substring(0, indexOfDot);
/*     */                   }
/*     */ 
/* 604 */                   newSql.append(timezoneLiteral);
/*     */                 }
/*     */ 
/* 608 */                 if (serverSupportsFractionalSecond) {
/* 609 */                   newSql.append(fractionalSecond);
/*     */                 }
/* 611 */                 newSql.append("'");
/*     */               }
/*     */               catch (NumberFormatException nfe) {
/* 614 */                 throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009", conn.getExceptionInterceptor());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */           catch (NoSuchElementException e)
/*     */           {
/* 623 */             throw SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (IllegalArgumentException illegalArgumentException)
/*     */       {
/* 630 */         SQLException sqlEx = SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */ 
/* 635 */         sqlEx.initCause(illegalArgumentException);
/*     */ 
/* 637 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn, MySQLConnection conn)
/*     */     throws SQLException
/*     */   {
/* 682 */     int firstIndexOfParen = functionToken.indexOf("(");
/*     */ 
/* 684 */     if (firstIndexOfParen == -1) {
/* 685 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 692 */     int indexOfComma = functionToken.lastIndexOf(",");
/*     */ 
/* 694 */     if (indexOfComma == -1) {
/* 695 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 702 */     int indexOfCloseParen = functionToken.indexOf(')', indexOfComma);
/*     */ 
/* 704 */     if (indexOfCloseParen == -1) {
/* 705 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 713 */     String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
/*     */ 
/* 715 */     String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
/*     */ 
/* 718 */     String newType = null;
/*     */ 
/* 720 */     String trimmedType = type.trim();
/*     */ 
/* 722 */     if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
/* 723 */       trimmedType = trimmedType.substring(4, trimmedType.length());
/*     */     }
/*     */ 
/* 726 */     if (serverSupportsConvertFn) {
/* 727 */       newType = (String)JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */     }
/*     */     else {
/* 730 */       newType = (String)JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */ 
/* 740 */       if (newType == null) {
/* 741 */         throw SQLError.createSQLException("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens.", "S1000", conn.getExceptionInterceptor());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 750 */     if (newType == null) {
/* 751 */       throw SQLError.createSQLException("Unsupported conversion type '" + type.trim() + "' found while processing escape token.", "S1000", conn.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 756 */     int replaceIndex = newType.indexOf("?");
/*     */ 
/* 758 */     if (replaceIndex != -1) {
/* 759 */       StringBuffer convertRewrite = new StringBuffer(newType.substring(0, replaceIndex));
/*     */ 
/* 761 */       convertRewrite.append(expression);
/* 762 */       convertRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
/*     */ 
/* 765 */       return convertRewrite.toString();
/*     */     }
/*     */ 
/* 768 */     StringBuffer castRewrite = new StringBuffer("CAST(");
/* 769 */     castRewrite.append(expression);
/* 770 */     castRewrite.append(" AS ");
/* 771 */     castRewrite.append(newType);
/* 772 */     castRewrite.append(")");
/*     */ 
/* 774 */     return castRewrite.toString();
/*     */   }
/*     */ 
/*     */   private static String removeWhitespace(String toCollapse)
/*     */   {
/* 788 */     if (toCollapse == null) {
/* 789 */       return null;
/*     */     }
/*     */ 
/* 792 */     int length = toCollapse.length();
/*     */ 
/* 794 */     StringBuffer collapsed = new StringBuffer(length);
/*     */ 
/* 796 */     for (int i = 0; i < length; i++) {
/* 797 */       char c = toCollapse.charAt(i);
/*     */ 
/* 799 */       if (!Character.isWhitespace(c)) {
/* 800 */         collapsed.append(c);
/*     */       }
/*     */     }
/*     */ 
/* 804 */     return collapsed.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  52 */     Map tempMap = new HashMap();
/*     */ 
/*  54 */     tempMap.put("BIGINT", "0 + ?");
/*  55 */     tempMap.put("BINARY", "BINARY");
/*  56 */     tempMap.put("BIT", "0 + ?");
/*  57 */     tempMap.put("CHAR", "CHAR");
/*  58 */     tempMap.put("DATE", "DATE");
/*  59 */     tempMap.put("DECIMAL", "0.0 + ?");
/*  60 */     tempMap.put("DOUBLE", "0.0 + ?");
/*  61 */     tempMap.put("FLOAT", "0.0 + ?");
/*  62 */     tempMap.put("INTEGER", "0 + ?");
/*  63 */     tempMap.put("LONGVARBINARY", "BINARY");
/*  64 */     tempMap.put("LONGVARCHAR", "CONCAT(?)");
/*  65 */     tempMap.put("REAL", "0.0 + ?");
/*  66 */     tempMap.put("SMALLINT", "CONCAT(?)");
/*  67 */     tempMap.put("TIME", "TIME");
/*  68 */     tempMap.put("TIMESTAMP", "DATETIME");
/*  69 */     tempMap.put("TINYINT", "CONCAT(?)");
/*  70 */     tempMap.put("VARBINARY", "BINARY");
/*  71 */     tempMap.put("VARCHAR", "CONCAT(?)");
/*     */ 
/*  73 */     JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
/*     */ 
/*  75 */     tempMap = new HashMap(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
/*     */ 
/*  77 */     tempMap.put("BINARY", "CONCAT(?)");
/*  78 */     tempMap.put("CHAR", "CONCAT(?)");
/*  79 */     tempMap.remove("DATE");
/*  80 */     tempMap.put("LONGVARBINARY", "CONCAT(?)");
/*  81 */     tempMap.remove("TIME");
/*  82 */     tempMap.remove("TIMESTAMP");
/*  83 */     tempMap.put("VARBINARY", "CONCAT(?)");
/*     */ 
/*  85 */     JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap);
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.EscapeProcessor
 * JD-Core Version:    0.6.0
 */