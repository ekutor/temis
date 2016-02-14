/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.Date;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public abstract class ResultSetRow
/*      */ {
/*      */   protected ExceptionInterceptor exceptionInterceptor;
/*      */   protected Field[] metadata;
/*      */ 
/*      */   protected ResultSetRow(ExceptionInterceptor exceptionInterceptor)
/*      */   {
/*   54 */     this.exceptionInterceptor = exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   public abstract void closeOpenStreams();
/*      */ 
/*      */   public abstract InputStream getBinaryInputStream(int paramInt) throws SQLException;
/*      */ 
/*      */   public abstract byte[] getColumnValue(int paramInt) throws SQLException;
/*      */ 
/*      */   protected final Date getDateFast(int columnIndex, byte[] dateAsBytes, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
/*   98 */     int year = 0;
/*   99 */     int month = 0;
/*  100 */     int day = 0;
/*      */     SQLException sqlEx;
/*      */     try {
/*  103 */       if (dateAsBytes == null) {
/*  104 */         return null;
/*      */       }
/*      */ 
/*  107 */       boolean allZeroDate = true;
/*      */ 
/*  109 */       boolean onlyTimePresent = false;
/*      */ 
/*  111 */       for (int i = 0; i < length; i++) {
/*  112 */         if (dateAsBytes[(offset + i)] == 58) {
/*  113 */           onlyTimePresent = true;
/*  114 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  118 */       for (int i = 0; i < length; i++) {
/*  119 */         byte b = dateAsBytes[(offset + i)];
/*      */ 
/*  121 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  122 */           onlyTimePresent = false;
/*      */         }
/*      */ 
/*  125 */         if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */           continue;
/*  127 */         allZeroDate = false;
/*      */ 
/*  129 */         break;
/*      */       }
/*      */ 
/*  133 */       if ((!onlyTimePresent) && (allZeroDate))
/*      */       {
/*  135 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  138 */           return null;
/*  139 */         }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  141 */           throw SQLError.createSQLException("Value '" + StringUtils.toString(dateAsBytes) + "' can not be represented as java.sql.Date", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  149 */         return rs.fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       }
/*  151 */       if (this.metadata[columnIndex].getMysqlType() == 7)
/*      */       {
/*  153 */         switch (length) {
/*      */         case 19:
/*      */         case 21:
/*      */         case 29:
/*  157 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  159 */           month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*      */ 
/*  161 */           day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */ 
/*  164 */           return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */         case 8:
/*      */         case 14:
/*  169 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  171 */           month = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */ 
/*  173 */           day = StringUtils.getInt(dateAsBytes, offset + 6, offset + 8);
/*      */ 
/*  176 */           return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */         case 6:
/*      */         case 10:
/*      */         case 12:
/*  182 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */ 
/*  185 */           if (year <= 69) {
/*  186 */             year += 100;
/*      */           }
/*      */ 
/*  189 */           month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */ 
/*  191 */           day = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */ 
/*  194 */           return rs.fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */         case 4:
/*  198 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  201 */           if (year <= 69) {
/*  202 */             year += 100;
/*      */           }
/*      */ 
/*  205 */           month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */ 
/*  208 */           return rs.fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */         case 2:
/*  212 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */ 
/*  215 */           if (year <= 69) {
/*  216 */             year += 100;
/*      */           }
/*      */ 
/*  219 */           return rs.fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 20:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*  223 */         case 28: } throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  234 */       if (this.metadata[columnIndex].getMysqlType() == 13)
/*      */       {
/*  236 */         if ((length == 2) || (length == 1)) {
/*  237 */           year = StringUtils.getInt(dateAsBytes, offset, offset + length);
/*      */ 
/*  240 */           if (year <= 69) {
/*  241 */             year += 100;
/*      */           }
/*      */ 
/*  244 */           year += 1900;
/*      */         } else {
/*  246 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */         }
/*      */ 
/*  250 */         return rs.fastDateCreate(targetCalendar, year, 1, 1);
/*  251 */       }if (this.metadata[columnIndex].getMysqlType() == 11) {
/*  252 */         return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/*  254 */       if (length < 10) {
/*  255 */         if (length == 8) {
/*  256 */           return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */ 
/*  261 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  273 */       if (length != 18) {
/*  274 */         year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */ 
/*  276 */         month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*      */ 
/*  278 */         day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */       }
/*      */       else
/*      */       {
/*  283 */         StringTokenizer st = new StringTokenizer(StringUtils.toString(dateAsBytes, offset, length, "ISO8859_1"), "- ");
/*      */ 
/*  286 */         year = Integer.parseInt(st.nextToken());
/*  287 */         month = Integer.parseInt(st.nextToken());
/*  288 */         day = Integer.parseInt(st.nextToken());
/*      */       }
/*      */ 
/*  292 */       return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */     } catch (SQLException sqlEx) {
/*  294 */       throw sqlEx;
/*      */     } catch (Exception e) {
/*  296 */       sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */ 
/*  301 */       sqlEx.initCause(e);
/*      */     }
/*  303 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public abstract Date getDateFast(int paramInt, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl, Calendar paramCalendar)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract int getInt(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract long getLong(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex, byte[] bits, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*  350 */     int year = 0;
/*  351 */     int month = 0;
/*  352 */     int day = 0;
/*      */ 
/*  354 */     if (length != 0) {
/*  355 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */ 
/*  357 */       month = bits[(offset + 2)];
/*  358 */       day = bits[(offset + 3)];
/*      */     }
/*      */ 
/*  361 */     if ((length == 0) || ((year == 0) && (month == 0) && (day == 0))) {
/*  362 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  364 */         return null;
/*  365 */       }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  367 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  373 */       year = 1;
/*  374 */       month = 1;
/*  375 */       day = 1;
/*      */     }
/*      */ 
/*  378 */     if (!rs.useLegacyDatetimeCode) {
/*  379 */       return TimeUtil.fastDateCreate(year, month, day, cal);
/*      */     }
/*      */ 
/*  382 */     return rs.fastDateCreate(cal == null ? rs.getCalendarInstanceForSessionOrNew() : cal, year, month, day);
/*      */   }
/*      */ 
/*      */   public abstract Date getNativeDate(int paramInt, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl, Calendar paramCalendar)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Object getNativeDateTimeValue(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  394 */     int year = 0;
/*  395 */     int month = 0;
/*  396 */     int day = 0;
/*      */ 
/*  398 */     int hour = 0;
/*  399 */     int minute = 0;
/*  400 */     int seconds = 0;
/*      */ 
/*  402 */     int nanos = 0;
/*      */ 
/*  404 */     if (bits == null)
/*      */     {
/*  406 */       return null;
/*      */     }
/*      */ 
/*  409 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  413 */     boolean populatedFromDateTimeValue = false;
/*      */ 
/*  415 */     switch (mysqlType) {
/*      */     case 7:
/*      */     case 12:
/*  418 */       populatedFromDateTimeValue = true;
/*      */ 
/*  420 */       if (length == 0) break;
/*  421 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */ 
/*  423 */       month = bits[(offset + 2)];
/*  424 */       day = bits[(offset + 3)];
/*      */ 
/*  426 */       if (length > 4) {
/*  427 */         hour = bits[(offset + 4)];
/*  428 */         minute = bits[(offset + 5)];
/*  429 */         seconds = bits[(offset + 6)];
/*      */       }
/*      */ 
/*  432 */       if (length <= 7)
/*      */         break;
/*  434 */       nanos = (bits[(offset + 7)] & 0xFF | (bits[(offset + 8)] & 0xFF) << 8 | (bits[(offset + 9)] & 0xFF) << 16 | (bits[(offset + 10)] & 0xFF) << 24) * 1000; break;
/*      */     case 10:
/*  442 */       populatedFromDateTimeValue = true;
/*      */ 
/*  444 */       if (bits.length == 0) break;
/*  445 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */ 
/*  447 */       month = bits[(offset + 2)];
/*  448 */       day = bits[(offset + 3)]; break;
/*      */     case 11:
/*  453 */       populatedFromDateTimeValue = true;
/*      */ 
/*  455 */       if (bits.length != 0)
/*      */       {
/*  458 */         hour = bits[(offset + 5)];
/*  459 */         minute = bits[(offset + 6)];
/*  460 */         seconds = bits[(offset + 7)];
/*      */       }
/*      */ 
/*  463 */       year = 1970;
/*  464 */       month = 1;
/*  465 */       day = 1;
/*      */ 
/*  467 */       break;
/*      */     case 8:
/*      */     case 9:
/*      */     default:
/*  469 */       populatedFromDateTimeValue = false;
/*      */     }
/*      */ 
/*  472 */     switch (jdbcType) {
/*      */     case 92:
/*  474 */       if (populatedFromDateTimeValue) {
/*  475 */         if (!rs.useLegacyDatetimeCode) {
/*  476 */           return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar, this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  479 */         Time time = TimeUtil.fastTimeCreate(rs.getCalendarInstanceForSessionOrNew(), hour, minute, seconds, this.exceptionInterceptor);
/*      */ 
/*  483 */         Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  487 */         return adjustedTime;
/*      */       }
/*      */ 
/*  490 */       return rs.getNativeTimeViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     case 91:
/*  494 */       if (populatedFromDateTimeValue) {
/*  495 */         if ((year == 0) && (month == 0) && (day == 0)) {
/*  496 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  499 */             return null;
/*  500 */           }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  502 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */           }
/*      */ 
/*  507 */           year = 1;
/*  508 */           month = 1;
/*  509 */           day = 1;
/*      */         }
/*      */ 
/*  512 */         if (!rs.useLegacyDatetimeCode) {
/*  513 */           return TimeUtil.fastDateCreate(year, month, day, targetCalendar);
/*      */         }
/*      */ 
/*  516 */         return rs.fastDateCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day);
/*      */       }
/*      */ 
/*  522 */       return rs.getNativeDateViaParseConversion(columnIndex + 1);
/*      */     case 93:
/*  524 */       if (populatedFromDateTimeValue) {
/*  525 */         if ((year == 0) && (month == 0) && (day == 0)) {
/*  526 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  529 */             return null;
/*  530 */           }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  532 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */ 
/*  537 */           year = 1;
/*  538 */           month = 1;
/*  539 */           day = 1;
/*      */         }
/*      */ 
/*  542 */         if (!rs.useLegacyDatetimeCode) {
/*  543 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */         }
/*      */ 
/*  547 */         Timestamp ts = rs.fastTimestampCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day, hour, minute, seconds, nanos);
/*      */ 
/*  551 */         Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  555 */         return adjustedTs;
/*      */       }
/*      */ 
/*  558 */       return rs.getNativeTimestampViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     }
/*      */ 
/*  562 */     throw new SQLException("Internal error - conversion method doesn't support this type", "S1000");
/*      */   }
/*      */ 
/*      */   public abstract Object getNativeDateTimeValue(int paramInt1, Calendar paramCalendar, int paramInt2, int paramInt3, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected double getNativeDouble(byte[] bits, int offset)
/*      */   {
/*  574 */     long valueAsLong = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24 | (bits[(offset + 4)] & 0xFF) << 32 | (bits[(offset + 5)] & 0xFF) << 40 | (bits[(offset + 6)] & 0xFF) << 48 | (bits[(offset + 7)] & 0xFF) << 56;
/*      */ 
/*  583 */     return Double.longBitsToDouble(valueAsLong);
/*      */   }
/*      */   public abstract double getNativeDouble(int paramInt) throws SQLException;
/*      */ 
/*      */   protected float getNativeFloat(byte[] bits, int offset) {
/*  589 */     int asInt = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24;
/*      */ 
/*  594 */     return Float.intBitsToFloat(asInt);
/*      */   }
/*      */ 
/*      */   public abstract float getNativeFloat(int paramInt) throws SQLException;
/*      */ 
/*      */   protected int getNativeInt(byte[] bits, int offset) {
/*  601 */     int valueAsInt = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24;
/*      */ 
/*  606 */     return valueAsInt;
/*      */   }
/*      */   public abstract int getNativeInt(int paramInt) throws SQLException;
/*      */ 
/*      */   protected long getNativeLong(byte[] bits, int offset) {
/*  612 */     long valueAsLong = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24 | (bits[(offset + 4)] & 0xFF) << 32 | (bits[(offset + 5)] & 0xFF) << 40 | (bits[(offset + 6)] & 0xFF) << 48 | (bits[(offset + 7)] & 0xFF) << 56;
/*      */ 
/*  621 */     return valueAsLong;
/*      */   }
/*      */   public abstract long getNativeLong(int paramInt) throws SQLException;
/*      */ 
/*      */   protected short getNativeShort(byte[] bits, int offset) {
/*  627 */     short asShort = (short)(bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8);
/*      */ 
/*  629 */     return asShort;
/*      */   }
/*      */ 
/*      */   public abstract short getNativeShort(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Time getNativeTime(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  653 */     int hour = 0;
/*  654 */     int minute = 0;
/*  655 */     int seconds = 0;
/*      */ 
/*  657 */     if (length != 0)
/*      */     {
/*  660 */       hour = bits[(offset + 5)];
/*  661 */       minute = bits[(offset + 6)];
/*  662 */       seconds = bits[(offset + 7)];
/*      */     }
/*      */ 
/*  665 */     if (!rs.useLegacyDatetimeCode) {
/*  666 */       return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*  669 */     Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  671 */     synchronized (sessionCalendar) {
/*  672 */       Time time = TimeUtil.fastTimeCreate(sessionCalendar, hour, minute, seconds, this.exceptionInterceptor);
/*      */ 
/*  675 */       Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  679 */       return adjustedTime;
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract Time getNativeTime(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Timestamp getNativeTimestamp(byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  690 */     int year = 0;
/*  691 */     int month = 0;
/*  692 */     int day = 0;
/*      */ 
/*  694 */     int hour = 0;
/*  695 */     int minute = 0;
/*  696 */     int seconds = 0;
/*      */ 
/*  698 */     int nanos = 0;
/*      */ 
/*  700 */     if (length != 0) {
/*  701 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*  702 */       month = bits[(offset + 2)];
/*  703 */       day = bits[(offset + 3)];
/*      */ 
/*  705 */       if (length > 4) {
/*  706 */         hour = bits[(offset + 4)];
/*  707 */         minute = bits[(offset + 5)];
/*  708 */         seconds = bits[(offset + 6)];
/*      */       }
/*      */ 
/*  711 */       if (length > 7)
/*      */       {
/*  713 */         nanos = (bits[(offset + 7)] & 0xFF | (bits[(offset + 8)] & 0xFF) << 8 | (bits[(offset + 9)] & 0xFF) << 16 | (bits[(offset + 10)] & 0xFF) << 24) * 1000;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  719 */     if ((length == 0) || ((year == 0) && (month == 0) && (day == 0))) {
/*  720 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  723 */         return null;
/*  724 */       }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  726 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*  732 */       year = 1;
/*  733 */       month = 1;
/*  734 */       day = 1;
/*      */     }
/*      */ 
/*  737 */     if (!rs.useLegacyDatetimeCode) {
/*  738 */       return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */     }
/*      */ 
/*  742 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/*  746 */     synchronized (sessionCalendar) {
/*  747 */       Timestamp ts = rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minute, seconds, nanos);
/*      */ 
/*  750 */       Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*  754 */       return adjustedTs;
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract Timestamp getNativeTimestamp(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract Reader getReader(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract String getString(int paramInt, String paramString, MySQLConnection paramMySQLConnection)
/*      */     throws SQLException;
/*      */ 
/*      */   protected String getString(String encoding, MySQLConnection conn, byte[] value, int offset, int length)
/*      */     throws SQLException
/*      */   {
/*  808 */     String stringVal = null;
/*      */ 
/*  810 */     if ((conn != null) && (conn.getUseUnicode())) {
/*      */       try {
/*  812 */         if (encoding == null) {
/*  813 */           stringVal = StringUtils.toString(value);
/*      */         } else {
/*  815 */           SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
/*      */ 
/*  818 */           if (converter != null)
/*  819 */             stringVal = converter.toString(value, offset, length);
/*      */           else
/*  821 */             stringVal = StringUtils.toString(value, offset, length, encoding);
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException E) {
/*  825 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Unsupported_character_encoding____101") + encoding + "'.", "0S100", this.exceptionInterceptor);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  832 */       stringVal = StringUtils.toAsciiString(value, offset, length);
/*      */     }
/*      */ 
/*  835 */     return stringVal;
/*      */   }
/*  843 */   protected Time getTimeFast(int columnIndex, byte[] timeAsBytes, int offset, int fullLength, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException { int hr = 0;
/*  844 */     int min = 0;
/*  845 */     int sec = 0;
/*  846 */     int nanos = 0;
/*      */ 
/*  848 */     int decimalIndex = -1;
/*      */     SQLException sqlEx;
/*      */     try { if (timeAsBytes == null) {
/*  853 */         return null;
/*      */       }
/*      */ 
/*  856 */       boolean allZeroTime = true;
/*  857 */       boolean onlyTimePresent = false;
/*      */ 
/*  859 */       for (int i = 0; i < fullLength; i++) {
/*  860 */         if (timeAsBytes[(offset + i)] == 58) {
/*  861 */           onlyTimePresent = true;
/*  862 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  866 */       for (int i = 0; i < fullLength; i++) {
/*  867 */         if (timeAsBytes[(offset + i)] == 46) {
/*  868 */           decimalIndex = i;
/*  869 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  873 */       for (int i = 0; i < fullLength; i++) {
/*  874 */         byte b = timeAsBytes[(offset + i)];
/*      */ 
/*  876 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  877 */           onlyTimePresent = false;
/*      */         }
/*      */ 
/*  880 */         if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */           continue;
/*  882 */         allZeroTime = false;
/*      */ 
/*  884 */         break;
/*      */       }
/*      */ 
/*  888 */       if ((!onlyTimePresent) && (allZeroTime)) {
/*  889 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  891 */           return null;
/*  892 */         }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  894 */           throw SQLError.createSQLException("Value '" + StringUtils.toString(timeAsBytes) + "' can not be represented as java.sql.Time", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  902 */         return rs.fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */       }
/*      */ 
/*  905 */       Field timeColField = this.metadata[columnIndex];
/*      */ 
/*  907 */       int length = fullLength;
/*      */ 
/*  909 */       if (decimalIndex != -1)
/*      */       {
/*  911 */         length = decimalIndex;
/*      */ 
/*  913 */         if (decimalIndex + 2 <= fullLength) {
/*  914 */           nanos = StringUtils.getInt(timeAsBytes, offset + decimalIndex + 1, offset + fullLength);
/*      */ 
/*  916 */           int numDigits = fullLength - (decimalIndex + 1);
/*      */ 
/*  918 */           if (numDigits < 9) {
/*  919 */             int factor = (int)Math.pow(10.0D, 9 - numDigits);
/*  920 */             nanos *= factor;
/*      */           }
/*      */         } else {
/*  923 */           throw new IllegalArgumentException();
/*      */         }
/*      */       }
/*      */       SQLWarning precisionLost;
/*  932 */       if (timeColField.getMysqlType() == 7)
/*      */       {
/*  934 */         switch (length)
/*      */         {
/*      */         case 19:
/*  937 */           hr = StringUtils.getInt(timeAsBytes, offset + length - 8, offset + length - 6);
/*      */ 
/*  939 */           min = StringUtils.getInt(timeAsBytes, offset + length - 5, offset + length - 3);
/*      */ 
/*  941 */           sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */ 
/*  945 */           break;
/*      */         case 12:
/*      */         case 14:
/*  948 */           hr = StringUtils.getInt(timeAsBytes, offset + length - 6, offset + length - 4);
/*      */ 
/*  950 */           min = StringUtils.getInt(timeAsBytes, offset + length - 4, offset + length - 2);
/*      */ 
/*  952 */           sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */ 
/*  956 */           break;
/*      */         case 10:
/*  959 */           hr = StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */ 
/*  961 */           min = StringUtils.getInt(timeAsBytes, offset + 8, offset + 10);
/*      */ 
/*  963 */           sec = 0;
/*      */ 
/*  966 */           break;
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         default:
/*  969 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + (columnIndex + 1) + "(" + timeColField + ").", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */ 
/*  980 */         precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + timeColField + ").");
/*      */       }
/*      */       else
/*      */       {
/*      */         SQLWarning precisionLost;
/*  989 */         if (timeColField.getMysqlType() == 12) {
/*  990 */           hr = StringUtils.getInt(timeAsBytes, offset + 11, offset + 13);
/*  991 */           min = StringUtils.getInt(timeAsBytes, offset + 14, offset + 16);
/*  992 */           sec = StringUtils.getInt(timeAsBytes, offset + 17, offset + 19);
/*      */ 
/*  995 */           precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + (columnIndex + 1) + "(" + timeColField + ").");
/*      */         }
/*      */         else
/*      */         {
/* 1005 */           if (timeColField.getMysqlType() == 10) {
/* 1006 */             return rs.fastTimeCreate(null, 0, 0, 0);
/*      */           }
/*      */ 
/* 1011 */           if ((length != 5) && (length != 8)) {
/* 1012 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + StringUtils.toString(timeAsBytes) + Messages.getString("ResultSet.___in_column__268") + (columnIndex + 1), "S1009", this.exceptionInterceptor);
/*      */           }
/*      */ 
/* 1020 */           hr = StringUtils.getInt(timeAsBytes, offset + 0, offset + 2);
/* 1021 */           min = StringUtils.getInt(timeAsBytes, offset + 3, offset + 5);
/* 1022 */           sec = length == 5 ? 0 : StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */         }
/*      */       }
/*      */ 
/* 1026 */       Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/* 1028 */       if (!rs.useLegacyDatetimeCode)
/*      */       {
/* 1032 */         return rs.fastTimeCreate(targetCalendar, hr, min, sec);
/*      */       }
/*      */ 
/* 1035 */       synchronized (sessionCalendar) {
/* 1036 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimeCreate(sessionCalendar, hr, min, sec), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/* 1045 */       sqlEx = SQLError.createSQLException(ex.toString(), "S1009", this.exceptionInterceptor);
/*      */ 
/* 1047 */       sqlEx.initCause(ex);
/*      */     }
/* 1049 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public abstract Time getTimeFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   protected Timestamp getTimestampFast(int columnIndex, byte[] timestampAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*      */     SQLException sqlEx;
/*      */     try
/*      */     {
/* 1063 */       Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */ 
/* 1067 */       synchronized (sessionCalendar) {
/* 1068 */         boolean allZeroTimestamp = true;
/*      */ 
/* 1070 */         boolean onlyTimePresent = false;
/*      */ 
/* 1072 */         for (int i = 0; i < length; i++) {
/* 1073 */           if (timestampAsBytes[(offset + i)] == 58) {
/* 1074 */             onlyTimePresent = true;
/* 1075 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1079 */         for (int i = 0; i < length; i++) {
/* 1080 */           byte b = timestampAsBytes[(offset + i)];
/*      */ 
/* 1082 */           if ((b == 32) || (b == 45) || (b == 47)) {
/* 1083 */             onlyTimePresent = false;
/*      */           }
/*      */ 
/* 1086 */           if ((b == 48) || (b == 32) || (b == 58) || (b == 45) || (b == 47) || (b == 46))
/*      */             continue;
/* 1088 */           allZeroTimestamp = false;
/*      */ 
/* 1090 */           break;
/*      */         }
/*      */ 
/* 1094 */         if ((!onlyTimePresent) && (allZeroTimestamp))
/*      */         {
/* 1096 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/* 1099 */             return null;
/* 1100 */           }if ("exception".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/* 1102 */             throw SQLError.createSQLException("Value '" + StringUtils.toString(timestampAsBytes) + "' can not be represented as java.sql.Timestamp", "S1009", this.exceptionInterceptor);
/*      */           }
/*      */ 
/* 1110 */           if (!rs.useLegacyDatetimeCode) {
/* 1111 */             return TimeUtil.fastTimestampCreate(tz, 1, 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 1115 */           return rs.fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */         }
/* 1117 */         if (this.metadata[columnIndex].getMysqlType() == 13)
/*      */         {
/* 1119 */           if (!rs.useLegacyDatetimeCode) {
/* 1120 */             return TimeUtil.fastTimestampCreate(tz, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 1125 */           return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */         }
/*      */ 
/* 1132 */         if (timestampAsBytes[(offset + length - 1)] == 46) {
/* 1133 */           length--;
/*      */         }
/*      */ 
/* 1138 */         int year = 0;
/* 1139 */         int month = 0;
/* 1140 */         int day = 0;
/* 1141 */         int hour = 0;
/* 1142 */         int minutes = 0;
/* 1143 */         int seconds = 0;
/* 1144 */         int nanos = 0;
/*      */ 
/* 1146 */         switch (length) {
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 29:
/* 1156 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1158 */           month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/*      */ 
/* 1160 */           day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1162 */           hour = StringUtils.getInt(timestampAsBytes, offset + 11, offset + 13);
/*      */ 
/* 1164 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 14, offset + 16);
/*      */ 
/* 1166 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 17, offset + 19);
/*      */ 
/* 1169 */           nanos = 0;
/*      */ 
/* 1171 */           if (length <= 19) break;
/* 1172 */           int decimalIndex = -1;
/*      */ 
/* 1174 */           for (int i = 0; i < length; i++) {
/* 1175 */             if (timestampAsBytes[(offset + i)] == 46) {
/* 1176 */               decimalIndex = i;
/*      */             }
/*      */           }
/*      */ 
/* 1180 */           if (decimalIndex != -1) {
/* 1181 */             if (decimalIndex + 2 <= length) {
/* 1182 */               nanos = StringUtils.getInt(timestampAsBytes, offset + decimalIndex + 1, offset + length);
/*      */ 
/* 1186 */               int numDigits = length - (decimalIndex + 1);
/*      */ 
/* 1188 */               if (numDigits < 9) {
/* 1189 */                 int factor = (int)Math.pow(10.0D, 9 - numDigits);
/* 1190 */                 nanos *= factor;
/*      */               }
/*      */             } else {
/* 1193 */               throw new IllegalArgumentException();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1201 */           break;
/*      */         case 14:
/* 1207 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1209 */           month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1211 */           day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1213 */           hour = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1215 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */ 
/* 1217 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 12, offset + 14);
/*      */ 
/* 1220 */           break;
/*      */         case 12:
/* 1224 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1227 */           if (year <= 69) {
/* 1228 */             year += 100;
/*      */           }
/*      */ 
/* 1231 */           year += 1900;
/*      */ 
/* 1233 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1235 */           day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1237 */           hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1239 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1241 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */ 
/* 1244 */           break;
/*      */         case 10:
/* 1248 */           boolean hasDash = false;
/*      */ 
/* 1250 */           for (int i = 0; i < length; i++) {
/* 1251 */             if (timestampAsBytes[(offset + i)] == 45) {
/* 1252 */               hasDash = true;
/* 1253 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1257 */           if ((this.metadata[columnIndex].getMysqlType() == 10) || (hasDash))
/*      */           {
/* 1259 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1261 */             month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/*      */ 
/* 1263 */             day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1265 */             hour = 0;
/* 1266 */             minutes = 0;
/*      */           } else {
/* 1268 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1271 */             if (year <= 69) {
/* 1272 */               year += 100;
/*      */             }
/*      */ 
/* 1275 */             month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1277 */             day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1279 */             hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1281 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */ 
/* 1284 */             year += 1900;
/*      */           }
/*      */ 
/* 1287 */           break;
/*      */         case 8:
/* 1291 */           boolean hasColon = false;
/*      */ 
/* 1293 */           for (int i = 0; i < length; i++) {
/* 1294 */             if (timestampAsBytes[(offset + i)] == 58) {
/* 1295 */               hasColon = true;
/* 1296 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1300 */           if (hasColon) {
/* 1301 */             hour = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1303 */             minutes = StringUtils.getInt(timestampAsBytes, offset + 3, offset + 5);
/*      */ 
/* 1305 */             seconds = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1308 */             year = 1970;
/* 1309 */             month = 1;
/* 1310 */             day = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1315 */             year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/*      */ 
/* 1317 */             month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1319 */             day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */ 
/* 1322 */             year -= 1900;
/* 1323 */             month--;
/*      */           }
/* 1325 */           break;
/*      */         case 6:
/* 1329 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1332 */           if (year <= 69) {
/* 1333 */             year += 100;
/*      */           }
/*      */ 
/* 1336 */           year += 1900;
/*      */ 
/* 1338 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1340 */           day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */ 
/* 1343 */           break;
/*      */         case 4:
/* 1347 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1350 */           if (year <= 69) {
/* 1351 */             year += 100;
/*      */           }
/*      */ 
/* 1354 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */ 
/* 1357 */           day = 1;
/*      */ 
/* 1359 */           break;
/*      */         case 2:
/* 1363 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */ 
/* 1366 */           if (year <= 69) {
/* 1367 */             year += 100;
/*      */           }
/*      */ 
/* 1370 */           year += 1900;
/* 1371 */           month = 1;
/* 1372 */           day = 1;
/*      */ 
/* 1374 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 7:
/*      */         case 9:
/*      */         case 11:
/*      */         case 13:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 27:
/*      */         case 28:
/*      */         default:
/* 1378 */           throw new SQLException("Bad format for Timestamp '" + StringUtils.toString(timestampAsBytes) + "' in column " + (columnIndex + 1) + ".", "S1009");
/*      */         }
/*      */ 
/* 1386 */         if (!rs.useLegacyDatetimeCode) {
/* 1387 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/* 1393 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/* 1404 */       sqlEx = SQLError.createSQLException("Cannot convert value '" + getString(columnIndex, "ISO8859_1", conn) + "' from column " + (columnIndex + 1) + " to TIMESTAMP.", "S1009", this.exceptionInterceptor);
/*      */ 
/* 1408 */       sqlEx.initCause(e);
/*      */     }
/* 1410 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public abstract Timestamp getTimestampFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract boolean isFloatingPointNumber(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract boolean isNull(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract long length(int paramInt)
/*      */     throws SQLException;
/*      */ 
/*      */   public abstract void setColumnValue(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException;
/*      */ 
/*      */   public ResultSetRow setMetadata(Field[] f)
/*      */     throws SQLException
/*      */   {
/* 1478 */     this.metadata = f;
/*      */ 
/* 1480 */     return this;
/*      */   }
/*      */ 
/*      */   public abstract int getBytesSize();
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetRow
 * JD-Core Version:    0.6.0
 */