/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParsePosition;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ 
/*      */ public class PreparedStatement extends StatementImpl
/*      */   implements java.sql.PreparedStatement
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_PSTMT_2_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_PSTMT_3_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_PSTMT_4_ARG_CTOR;
/*      */   private static final byte[] HEX_DIGITS;
/*  777 */   protected boolean batchHasPlainStatements = false;
/*      */ 
/*  779 */   private DatabaseMetaData dbmd = null;
/*      */ 
/*  785 */   protected char firstCharOfStmt = '\000';
/*      */ 
/*  788 */   protected boolean hasLimitClause = false;
/*      */ 
/*  791 */   protected boolean isLoadDataQuery = false;
/*      */ 
/*  793 */   protected boolean[] isNull = null;
/*      */ 
/*  795 */   private boolean[] isStream = null;
/*      */ 
/*  797 */   protected int numberOfExecutions = 0;
/*      */ 
/*  800 */   protected String originalSql = null;
/*      */   protected int parameterCount;
/*      */   protected MysqlParameterMetadata parameterMetaData;
/*  807 */   private InputStream[] parameterStreams = null;
/*      */ 
/*  809 */   private byte[][] parameterValues = (byte[][])null;
/*      */ 
/*  815 */   protected int[] parameterTypes = null;
/*      */   protected ParseInfo parseInfo;
/*      */   private java.sql.ResultSetMetaData pstmtResultMetaData;
/*  821 */   private byte[][] staticSqlStrings = (byte[][])null;
/*      */ 
/*  823 */   private byte[] streamConvertBuf = null;
/*      */ 
/*  825 */   private int[] streamLengths = null;
/*      */ 
/*  827 */   private SimpleDateFormat tsdf = null;
/*      */ 
/*  832 */   protected boolean useTrueBoolean = false;
/*      */   protected boolean usingAnsiMode;
/*      */   protected String batchedValuesClause;
/*      */   private boolean doPingInstead;
/*      */   private SimpleDateFormat ddf;
/*      */   private SimpleDateFormat tdf;
/*  842 */   private boolean compensateForOnDuplicateKeyUpdate = false;
/*      */   private CharsetEncoder charsetEncoder;
/*  848 */   protected int batchCommandIndex = -1;
/*      */   protected boolean serverSupportsFracSecs;
/* 2636 */   protected int rewrittenBatchSize = 0;
/*      */ 
/*      */   protected static int readFully(Reader reader, char[] buf, int length)
/*      */     throws IOException
/*      */   {
/*  756 */     int numCharsRead = 0;
/*      */ 
/*  758 */     while (numCharsRead < length) {
/*  759 */       int count = reader.read(buf, numCharsRead, length - numCharsRead);
/*      */ 
/*  761 */       if (count < 0)
/*      */       {
/*      */         break;
/*      */       }
/*  765 */       numCharsRead += count;
/*      */     }
/*      */ 
/*  768 */     return numCharsRead;
/*      */   }
/*      */ 
/*      */   protected static PreparedStatement getInstance(MySQLConnection conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  861 */     if (!Util.isJdbc4()) {
/*  862 */       return new PreparedStatement(conn, catalog);
/*      */     }
/*      */ 
/*  865 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_2_ARG_CTOR, new Object[] { conn, catalog }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  878 */     if (!Util.isJdbc4()) {
/*  879 */       return new PreparedStatement(conn, sql, catalog);
/*      */     }
/*      */ 
/*  882 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_3_ARG_CTOR, new Object[] { conn, sql, catalog }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  895 */     if (!Util.isJdbc4()) {
/*  896 */       return new PreparedStatement(conn, sql, catalog, cachedParseInfo);
/*      */     }
/*      */ 
/*  899 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_4_ARG_CTOR, new Object[] { conn, sql, catalog, cachedParseInfo }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public PreparedStatement(MySQLConnection conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  917 */     super(conn, catalog);
/*      */ 
/*  919 */     detectFractionalSecondsSupport();
/*  920 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   protected void detectFractionalSecondsSupport() throws SQLException {
/*  924 */     this.serverSupportsFracSecs = ((this.connection != null) && (this.connection.versionMeetsMinimum(5, 6, 4)));
/*      */   }
/*      */ 
/*      */   public PreparedStatement(MySQLConnection conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  943 */     super(conn, catalog);
/*      */ 
/*  945 */     if (sql == null) {
/*  946 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.0"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  950 */     detectFractionalSecondsSupport();
/*  951 */     this.originalSql = sql;
/*      */ 
/*  953 */     if (this.originalSql.startsWith("/* ping */"))
/*  954 */       this.doPingInstead = true;
/*      */     else {
/*  956 */       this.doPingInstead = false;
/*      */     }
/*      */ 
/*  959 */     this.dbmd = this.connection.getMetaData();
/*      */ 
/*  961 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */ 
/*  963 */     this.parseInfo = new ParseInfo(sql, this.connection, this.dbmd, this.charEncoding, this.charConverter);
/*      */ 
/*  966 */     initializeFromParseInfo();
/*      */ 
/*  968 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */ 
/*  970 */     if (conn.getRequiresEscapingEncoder())
/*  971 */       this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
/*      */   }
/*      */ 
/*      */   public PreparedStatement(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  991 */     super(conn, catalog);
/*      */ 
/*  993 */     if (sql == null) {
/*  994 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.1"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  998 */     detectFractionalSecondsSupport();
/*  999 */     this.originalSql = sql;
/*      */ 
/* 1001 */     this.dbmd = this.connection.getMetaData();
/*      */ 
/* 1003 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */ 
/* 1005 */     this.parseInfo = cachedParseInfo;
/*      */ 
/* 1007 */     this.usingAnsiMode = (!this.connection.useAnsiQuotedIdentifiers());
/*      */ 
/* 1009 */     initializeFromParseInfo();
/*      */ 
/* 1011 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */ 
/* 1013 */     if (conn.getRequiresEscapingEncoder())
/* 1014 */       this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/* 1026 */     synchronized (checkClosed()) {
/* 1027 */       if (this.batchedArgs == null) {
/* 1028 */         this.batchedArgs = new ArrayList();
/*      */       }
/*      */ 
/* 1031 */       for (int i = 0; i < this.parameterValues.length; i++) {
/* 1032 */         checkAllParametersSet(this.parameterValues[i], this.parameterStreams[i], i);
/*      */       }
/*      */ 
/* 1036 */       this.batchedArgs.add(new BatchParams(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addBatch(String sql)
/*      */     throws SQLException
/*      */   {
/* 1043 */     synchronized (checkClosed()) {
/* 1044 */       this.batchHasPlainStatements = true;
/*      */ 
/* 1046 */       super.addBatch(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String asSql() throws SQLException {
/* 1051 */     return asSql(false);
/*      */   }
/*      */ 
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/* 1055 */     synchronized (checkClosed())
/*      */     {
/* 1057 */       StringBuffer buf = new StringBuffer();
/*      */       try
/*      */       {
/* 1060 */         int realParameterCount = this.parameterCount + getParameterIndexOffset();
/* 1061 */         Object batchArg = null;
/* 1062 */         if (this.batchCommandIndex != -1) {
/* 1063 */           batchArg = this.batchedArgs.get(this.batchCommandIndex);
/*      */         }
/* 1065 */         for (int i = 0; i < realParameterCount; i++) {
/* 1066 */           if (this.charEncoding != null) {
/* 1067 */             buf.append(StringUtils.toString(this.staticSqlStrings[i], this.charEncoding));
/*      */           }
/*      */           else {
/* 1070 */             buf.append(StringUtils.toString(this.staticSqlStrings[i]));
/*      */           }
/*      */ 
/* 1073 */           byte[] val = null;
/* 1074 */           if ((batchArg != null) && ((batchArg instanceof String))) {
/* 1075 */             buf.append((String)batchArg);
/*      */           }
/*      */           else {
/* 1078 */             if (this.batchCommandIndex == -1)
/* 1079 */               val = this.parameterValues[i];
/*      */             else {
/* 1081 */               val = ((BatchParams)batchArg).parameterStrings[i];
/*      */             }
/* 1083 */             boolean isStreamParam = false;
/* 1084 */             if (this.batchCommandIndex == -1)
/* 1085 */               isStreamParam = this.isStream[i];
/*      */             else {
/* 1087 */               isStreamParam = ((BatchParams)batchArg).isStream[i];
/*      */             }
/* 1089 */             if ((val == null) && (!isStreamParam)) {
/* 1090 */               if (quoteStreamsAndUnknowns) {
/* 1091 */                 buf.append("'");
/*      */               }
/*      */ 
/* 1094 */               buf.append("** NOT SPECIFIED **");
/*      */ 
/* 1096 */               if (quoteStreamsAndUnknowns)
/* 1097 */                 buf.append("'");
/*      */             }
/* 1099 */             else if (isStreamParam) {
/* 1100 */               if (quoteStreamsAndUnknowns) {
/* 1101 */                 buf.append("'");
/*      */               }
/*      */ 
/* 1104 */               buf.append("** STREAM DATA **");
/*      */ 
/* 1106 */               if (quoteStreamsAndUnknowns) {
/* 1107 */                 buf.append("'");
/*      */               }
/*      */             }
/* 1110 */             else if (this.charConverter != null) {
/* 1111 */               buf.append(this.charConverter.toString(val));
/*      */             }
/* 1113 */             else if (this.charEncoding != null) {
/* 1114 */               buf.append(new String(val, this.charEncoding));
/*      */             } else {
/* 1116 */               buf.append(StringUtils.toAsciiString(val));
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1122 */         if (this.charEncoding != null) {
/* 1123 */           buf.append(StringUtils.toString(this.staticSqlStrings[(this.parameterCount + getParameterIndexOffset())], this.charEncoding));
/*      */         }
/*      */         else
/*      */         {
/* 1127 */           buf.append(StringUtils.toAsciiString(this.staticSqlStrings[(this.parameterCount + getParameterIndexOffset())]));
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException uue)
/*      */       {
/* 1132 */         throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */       }
/*      */ 
/* 1138 */       return buf.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearBatch() throws SQLException {
/* 1143 */     synchronized (checkClosed()) {
/* 1144 */       this.batchHasPlainStatements = false;
/*      */ 
/* 1146 */       super.clearBatch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/* 1161 */     synchronized (checkClosed())
/*      */     {
/* 1163 */       for (int i = 0; i < this.parameterValues.length; i++) {
/* 1164 */         this.parameterValues[i] = null;
/* 1165 */         this.parameterStreams[i] = null;
/* 1166 */         this.isStream[i] = false;
/* 1167 */         this.isNull[i] = false;
/* 1168 */         this.parameterTypes[i] = 0;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException
/*      */   {
/* 1175 */     int lastwritten = 0;
/*      */ 
/* 1177 */     for (int i = 0; i < size; i++) {
/* 1178 */       byte b = buf[i];
/*      */ 
/* 1180 */       if (b == 0)
/*      */       {
/* 1182 */         if (i > lastwritten) {
/* 1183 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1187 */         packet.writeByte(92);
/* 1188 */         packet.writeByte(48);
/* 1189 */         lastwritten = i + 1;
/*      */       } else {
/* 1191 */         if ((b != 92) && (b != 39) && ((this.usingAnsiMode) || (b != 34))) {
/*      */           continue;
/*      */         }
/* 1194 */         if (i > lastwritten) {
/* 1195 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1200 */         packet.writeByte(92);
/* 1201 */         lastwritten = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1207 */     if (lastwritten < size)
/* 1208 */       packet.writeBytesNoNull(buf, lastwritten, size - lastwritten);
/*      */   }
/*      */ 
/*      */   private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size)
/*      */   {
/* 1214 */     int lastwritten = 0;
/*      */ 
/* 1216 */     for (int i = 0; i < size; i++) {
/* 1217 */       byte b = buf[i];
/*      */ 
/* 1219 */       if (b == 0)
/*      */       {
/* 1221 */         if (i > lastwritten) {
/* 1222 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1226 */         bytesOut.write(92);
/* 1227 */         bytesOut.write(48);
/* 1228 */         lastwritten = i + 1;
/*      */       } else {
/* 1230 */         if ((b != 92) && (b != 39) && ((this.usingAnsiMode) || (b != 34))) {
/*      */           continue;
/*      */         }
/* 1233 */         if (i > lastwritten) {
/* 1234 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/* 1238 */         bytesOut.write(92);
/* 1239 */         lastwritten = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1245 */     if (lastwritten < size)
/* 1246 */       bytesOut.write(buf, lastwritten, size - lastwritten);
/*      */   }
/*      */ 
/*      */   protected boolean checkReadOnlySafeStatement()
/*      */     throws SQLException
/*      */   {
/* 1257 */     synchronized (checkClosed()) {
/* 1258 */       return (!this.connection.isReadOnly()) || (this.firstCharOfStmt == 'S');
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/* 1274 */     synchronized (checkClosed())
/*      */     {
/* 1276 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1278 */       if (!checkReadOnlySafeStatement()) {
/* 1279 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1284 */       ResultSetInternalMethods rs = null;
/*      */ 
/* 1286 */       CachedResultSetMetaData cachedMetadata = null;
/*      */ 
/* 1288 */       this.lastQueryIsOnDupKeyUpdate = false;
/*      */ 
/* 1290 */       if (this.retrieveGeneratedKeys) {
/* 1291 */         this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyUpdateInSQL();
/*      */       }
/*      */ 
/* 1294 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/* 1296 */       clearWarnings();
/*      */ 
/* 1306 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/* 1308 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */       }
/*      */ 
/* 1314 */       this.batchedGeneratedKeys = null;
/*      */ 
/* 1316 */       Buffer sendPacket = fillSendPacket();
/*      */ 
/* 1318 */       String oldCatalog = null;
/*      */ 
/* 1320 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1321 */         oldCatalog = locallyScopedConn.getCatalog();
/* 1322 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 1328 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1329 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */ 
/* 1332 */       Field[] metadataFromCache = null;
/*      */ 
/* 1334 */       if (cachedMetadata != null) {
/* 1335 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */ 
/* 1338 */       boolean oldInfoMsgState = false;
/*      */ 
/* 1340 */       if (this.retrieveGeneratedKeys) {
/* 1341 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 1342 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */ 
/* 1354 */       if (locallyScopedConn.useMaxRows()) {
/* 1355 */         int rowLimit = -1;
/*      */ 
/* 1357 */         if (this.firstCharOfStmt == 'S') {
/* 1358 */           if (this.hasLimitClause) {
/* 1359 */             rowLimit = this.maxRows;
/*      */           }
/* 1361 */           else if (this.maxRows <= 0) {
/* 1362 */             executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */           }
/*      */           else {
/* 1365 */             executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=" + this.maxRows);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1370 */           executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */         }
/*      */ 
/* 1375 */         rs = executeInternal(rowLimit, sendPacket, doStreaming, this.firstCharOfStmt == 'S', metadataFromCache, false);
/*      */       }
/*      */       else
/*      */       {
/* 1379 */         rs = executeInternal(-1, sendPacket, doStreaming, this.firstCharOfStmt == 'S', metadataFromCache, false);
/*      */       }
/*      */ 
/* 1384 */       if (cachedMetadata != null) {
/* 1385 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       }
/* 1388 */       else if ((rs.reallyResult()) && (locallyScopedConn.getCacheResultSetMetadata())) {
/* 1389 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, null, rs);
/*      */       }
/*      */ 
/* 1394 */       if (this.retrieveGeneratedKeys) {
/* 1395 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 1396 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */ 
/* 1399 */       if (oldCatalog != null) {
/* 1400 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */ 
/* 1403 */       if (rs != null) {
/* 1404 */         this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 1406 */         this.results = rs;
/*      */       }
/*      */ 
/* 1409 */       return (rs != null) && (rs.reallyResult());
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 1428 */     synchronized (checkClosed())
/*      */     {
/* 1430 */       if (this.connection.isReadOnly()) {
/* 1431 */         throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009");
/*      */       }
/*      */ 
/* 1436 */       if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
/* 1437 */         return new int[0];
/*      */       }
/*      */ 
/* 1441 */       int batchTimeout = this.timeoutInMillis;
/* 1442 */       this.timeoutInMillis = 0;
/*      */ 
/* 1444 */       resetCancelledState();
/*      */       try
/*      */       {
/* 1447 */         statementBegins();
/*      */ 
/* 1449 */         clearWarnings();
/*      */ 
/* 1451 */         if ((!this.batchHasPlainStatements) && (this.connection.getRewriteBatchedStatements()))
/*      */         {
/* 1455 */           if (canRewriteAsMultiValueInsertAtSqlLevel()) {
/* 1456 */             arrayOfInt = executeBatchedInserts(batchTimeout);
/*      */ 
/* 1469 */             this.statementExecuting.set(false);
/*      */ 
/* 1471 */             clearBatch(); return arrayOfInt;
/*      */           }
/* 1459 */           if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (!this.batchHasPlainStatements) && (this.batchedArgs != null) && (this.batchedArgs.size() > 3))
/*      */           {
/* 1463 */             arrayOfInt = executePreparedBatchAsMultiStatement(batchTimeout);
/*      */ 
/* 1469 */             this.statementExecuting.set(false);
/*      */ 
/* 1471 */             clearBatch(); return arrayOfInt;
/*      */           }
/*      */         }
/* 1467 */         int[] arrayOfInt = executeBatchSerially(batchTimeout);
/*      */ 
/* 1469 */         this.statementExecuting.set(false);
/*      */ 
/* 1471 */         clearBatch(); return arrayOfInt;
/*      */       }
/*      */       finally
/*      */       {
/* 1469 */         this.statementExecuting.set(false);
/*      */ 
/* 1471 */         clearBatch();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException {
/* 1477 */     return this.parseInfo.canRewriteAsMultiValueInsert;
/*      */   }
/*      */ 
/*      */   protected int getLocationOfOnDuplicateKeyUpdate() throws SQLException {
/* 1481 */     return this.parseInfo.locationOfOnDuplicateKeyUpdate;
/*      */   }
/*      */ 
/*      */   protected int[] executePreparedBatchAsMultiStatement(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/* 1495 */     synchronized (checkClosed())
/*      */     {
/* 1497 */       if (this.batchedValuesClause == null) {
/* 1498 */         this.batchedValuesClause = (this.originalSql + ";");
/*      */       }
/*      */ 
/* 1501 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1503 */       boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
/* 1504 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1507 */         clearWarnings();
/*      */ 
/* 1509 */         int numBatchedArgs = this.batchedArgs.size();
/*      */ 
/* 1511 */         if (this.retrieveGeneratedKeys) {
/* 1512 */           this.batchedGeneratedKeys = new ArrayList(numBatchedArgs);
/*      */         }
/*      */ 
/* 1515 */         int numValuesPerBatch = computeBatchSize(numBatchedArgs);
/*      */ 
/* 1517 */         if (numBatchedArgs < numValuesPerBatch) {
/* 1518 */           numValuesPerBatch = numBatchedArgs;
/*      */         }
/*      */ 
/* 1521 */         java.sql.PreparedStatement batchedStatement = null;
/*      */ 
/* 1523 */         int batchedParamIndex = 1;
/* 1524 */         int numberToExecuteAsMultiValue = 0;
/* 1525 */         int batchCounter = 0;
/* 1526 */         int updateCountCounter = 0;
/* 1527 */         int[] updateCounts = new int[numBatchedArgs];
/* 1528 */         SQLException sqlEx = null;
/*      */         try
/*      */         {
/* 1531 */           if (!multiQueriesEnabled) {
/* 1532 */             locallyScopedConn.getIO().enableMultiQueries();
/*      */           }
/*      */ 
/* 1535 */           if (this.retrieveGeneratedKeys) {
/* 1536 */             batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch), 1);
/*      */           }
/*      */           else
/*      */           {
/* 1540 */             batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch));
/*      */           }
/*      */ 
/* 1544 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/* 1547 */             timeoutTask = new StatementImpl.CancelTask(this, (StatementImpl)batchedStatement);
/* 1548 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */ 
/* 1552 */           if (numBatchedArgs < numValuesPerBatch)
/* 1553 */             numberToExecuteAsMultiValue = numBatchedArgs;
/*      */           else {
/* 1555 */             numberToExecuteAsMultiValue = numBatchedArgs / numValuesPerBatch;
/*      */           }
/*      */ 
/* 1558 */           int numberArgsToExecute = numberToExecuteAsMultiValue * numValuesPerBatch;
/*      */ 
/* 1560 */           for (int i = 0; i < numberArgsToExecute; i++) {
/* 1561 */             if ((i != 0) && (i % numValuesPerBatch == 0)) {
/*      */               try {
/* 1563 */                 batchedStatement.execute();
/*      */               } catch (SQLException ex) {
/* 1565 */                 sqlEx = handleExceptionForBatch(batchCounter, numValuesPerBatch, updateCounts, ex);
/*      */               }
/*      */ 
/* 1569 */               updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/* 1573 */               batchedStatement.clearParameters();
/* 1574 */               batchedParamIndex = 1;
/*      */             }
/*      */ 
/* 1577 */             batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1583 */             batchedStatement.execute();
/*      */           } catch (SQLException ex) {
/* 1585 */             sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */           }
/*      */ 
/* 1589 */           updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/* 1593 */           batchedStatement.clearParameters();
/*      */ 
/* 1595 */           numValuesPerBatch = numBatchedArgs - batchCounter;
/*      */         } finally {
/* 1597 */           if (batchedStatement != null) {
/* 1598 */             batchedStatement.close();
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/* 1603 */           if (numValuesPerBatch > 0)
/*      */           {
/* 1605 */             if (this.retrieveGeneratedKeys) {
/* 1606 */               batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch), 1);
/*      */             }
/*      */             else
/*      */             {
/* 1610 */               batchedStatement = locallyScopedConn.prepareStatement(generateMultiStatementForBatch(numValuesPerBatch));
/*      */             }
/*      */ 
/* 1614 */             if (timeoutTask != null) {
/* 1615 */               timeoutTask.toCancel = ((StatementImpl)batchedStatement);
/*      */             }
/*      */ 
/* 1618 */             batchedParamIndex = 1;
/*      */ 
/* 1620 */             while (batchCounter < numBatchedArgs) {
/* 1621 */               batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 1627 */               batchedStatement.execute();
/*      */             } catch (SQLException ex) {
/* 1629 */               sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */             }
/*      */ 
/* 1633 */             updateCountCounter = processMultiCountsAndKeys((StatementImpl)batchedStatement, updateCountCounter, updateCounts);
/*      */ 
/* 1637 */             batchedStatement.clearParameters();
/*      */           }
/*      */ 
/* 1640 */           if (timeoutTask != null) {
/* 1641 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 1642 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */ 
/* 1645 */             timeoutTask.cancel();
/*      */ 
/* 1647 */             locallyScopedConn.getCancelTimer().purge();
/*      */ 
/* 1649 */             timeoutTask = null;
/*      */           }
/*      */ 
/* 1652 */           if (sqlEx != null) {
/* 1653 */             batchUpdateException = new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */ 
/* 1656 */             batchUpdateException.initCause(sqlEx);
/* 1657 */             throw batchUpdateException;
/*      */           }
/*      */ 
/* 1660 */           SQLException batchUpdateException = updateCounts;
/*      */ 
/* 1662 */           if (batchedStatement != null) {
/* 1663 */             batchedStatement.close();
/*      */           }
/*      */ 
/* 1667 */           if (timeoutTask != null) {
/* 1668 */             timeoutTask.cancel();
/* 1669 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */ 
/* 1672 */           resetCancelledState();
/*      */ 
/* 1674 */           if (!multiQueriesEnabled) {
/* 1675 */             locallyScopedConn.getIO().disableMultiQueries();
/*      */           }
/*      */ 
/* 1678 */           clearBatch(); return batchUpdateException;
/*      */         }
/*      */         finally
/*      */         {
/* 1662 */           if (batchedStatement != null)
/* 1663 */             batchedStatement.close();
/*      */         }
/*      */       }
/*      */       finally {
/* 1667 */         if (timeoutTask != null) {
/* 1668 */           timeoutTask.cancel();
/* 1669 */           locallyScopedConn.getCancelTimer().purge();
/*      */         }
/*      */ 
/* 1672 */         resetCancelledState();
/*      */ 
/* 1674 */         if (!multiQueriesEnabled) {
/* 1675 */           locallyScopedConn.getIO().disableMultiQueries();
/*      */         }
/*      */ 
/* 1678 */         clearBatch();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String generateMultiStatementForBatch(int numBatches) throws SQLException {
/* 1684 */     synchronized (checkClosed()) {
/* 1685 */       StringBuffer newStatementSql = new StringBuffer((this.originalSql.length() + 1) * numBatches);
/*      */ 
/* 1688 */       newStatementSql.append(this.originalSql);
/*      */ 
/* 1690 */       for (int i = 0; i < numBatches - 1; i++) {
/* 1691 */         newStatementSql.append(';');
/* 1692 */         newStatementSql.append(this.originalSql);
/*      */       }
/*      */ 
/* 1695 */       return newStatementSql.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int[] executeBatchedInserts(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/* 1709 */     synchronized (checkClosed()) {
/* 1710 */       String valuesClause = getValuesClause();
/*      */ 
/* 1712 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1714 */       if (valuesClause == null) {
/* 1715 */         return executeBatchSerially(batchTimeout);
/*      */       }
/*      */ 
/* 1718 */       int numBatchedArgs = this.batchedArgs.size();
/*      */ 
/* 1720 */       if (this.retrieveGeneratedKeys) {
/* 1721 */         this.batchedGeneratedKeys = new ArrayList(numBatchedArgs);
/*      */       }
/*      */ 
/* 1724 */       int numValuesPerBatch = computeBatchSize(numBatchedArgs);
/*      */ 
/* 1726 */       if (numBatchedArgs < numValuesPerBatch) {
/* 1727 */         numValuesPerBatch = numBatchedArgs;
/*      */       }
/*      */ 
/* 1730 */       java.sql.PreparedStatement batchedStatement = null;
/*      */ 
/* 1732 */       int batchedParamIndex = 1;
/* 1733 */       int updateCountRunningTotal = 0;
/* 1734 */       int numberToExecuteAsMultiValue = 0;
/* 1735 */       int batchCounter = 0;
/* 1736 */       StatementImpl.CancelTask timeoutTask = null;
/* 1737 */       SQLException sqlEx = null;
/*      */ 
/* 1739 */       int[] updateCounts = new int[numBatchedArgs];
/*      */ 
/* 1741 */       for (int i = 0; i < this.batchedArgs.size(); i++)
/* 1742 */         updateCounts[i] = 1;
/*      */       try
/*      */       {
/*      */         try
/*      */         {
/* 1747 */           batchedStatement = prepareBatchedInsertSQL(locallyScopedConn, numValuesPerBatch);
/*      */ 
/* 1750 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/* 1753 */             timeoutTask = new StatementImpl.CancelTask(this, (StatementImpl)batchedStatement);
/*      */ 
/* 1755 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */ 
/* 1759 */           if (numBatchedArgs < numValuesPerBatch)
/* 1760 */             numberToExecuteAsMultiValue = numBatchedArgs;
/*      */           else {
/* 1762 */             numberToExecuteAsMultiValue = numBatchedArgs / numValuesPerBatch;
/*      */           }
/*      */ 
/* 1766 */           int numberArgsToExecute = numberToExecuteAsMultiValue * numValuesPerBatch;
/*      */ 
/* 1769 */           for (int i = 0; i < numberArgsToExecute; i++) {
/* 1770 */             if ((i != 0) && (i % numValuesPerBatch == 0)) {
/*      */               try {
/* 1772 */                 updateCountRunningTotal += batchedStatement.executeUpdate();
/*      */               }
/*      */               catch (SQLException ex) {
/* 1775 */                 sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */               }
/*      */ 
/* 1779 */               getBatchedGeneratedKeys(batchedStatement);
/* 1780 */               batchedStatement.clearParameters();
/* 1781 */               batchedParamIndex = 1;
/*      */             }
/*      */ 
/* 1785 */             batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1792 */             batchedStatement.executeUpdate();
/*      */           } catch (SQLException ex) {
/* 1794 */             sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */           }
/*      */ 
/* 1798 */           getBatchedGeneratedKeys(batchedStatement);
/*      */ 
/* 1800 */           numValuesPerBatch = numBatchedArgs - batchCounter;
/*      */         } finally {
/* 1802 */           if (batchedStatement != null) {
/* 1803 */             batchedStatement.close();
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/* 1808 */           if (numValuesPerBatch > 0) {
/* 1809 */             batchedStatement = prepareBatchedInsertSQL(locallyScopedConn, numValuesPerBatch);
/*      */ 
/* 1813 */             if (timeoutTask != null) {
/* 1814 */               timeoutTask.toCancel = ((StatementImpl)batchedStatement);
/*      */             }
/*      */ 
/* 1817 */             batchedParamIndex = 1;
/*      */ 
/* 1819 */             while (batchCounter < numBatchedArgs) {
/* 1820 */               batchedParamIndex = setOneBatchedParameterSet(batchedStatement, batchedParamIndex, this.batchedArgs.get(batchCounter++));
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 1826 */               updateCountRunningTotal += batchedStatement.executeUpdate();
/*      */             } catch (SQLException ex) {
/* 1828 */               sqlEx = handleExceptionForBatch(batchCounter - 1, numValuesPerBatch, updateCounts, ex);
/*      */             }
/*      */ 
/* 1832 */             getBatchedGeneratedKeys(batchedStatement);
/*      */           }
/*      */ 
/* 1835 */           if (sqlEx != null) {
/* 1836 */             batchUpdateException = new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */ 
/* 1839 */             batchUpdateException.initCause(sqlEx);
/* 1840 */             throw batchUpdateException;
/*      */           }
/*      */ 
/* 1843 */           SQLException batchUpdateException = updateCounts;
/*      */ 
/* 1845 */           if (batchedStatement != null) {
/* 1846 */             batchedStatement.close();
/*      */           }
/*      */ 
/* 1850 */           if (timeoutTask != null) {
/* 1851 */             timeoutTask.cancel();
/* 1852 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */ 
/* 1855 */           resetCancelledState(); return batchUpdateException;
/*      */         }
/*      */         finally
/*      */         {
/* 1845 */           if (batchedStatement != null)
/* 1846 */             batchedStatement.close();
/*      */         }
/*      */       }
/*      */       finally {
/* 1850 */         if (timeoutTask != null) {
/* 1851 */           timeoutTask.cancel();
/* 1852 */           locallyScopedConn.getCancelTimer().purge();
/*      */         }
/*      */ 
/* 1855 */         resetCancelledState();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getValuesClause() throws SQLException {
/* 1861 */     return this.parseInfo.valuesClause;
/*      */   }
/*      */ 
/*      */   protected int computeBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 1873 */     synchronized (checkClosed()) {
/* 1874 */       long[] combinedValues = computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
/*      */ 
/* 1876 */       long maxSizeOfParameterSet = combinedValues[0];
/* 1877 */       long sizeOfEntireBatch = combinedValues[1];
/*      */ 
/* 1879 */       int maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */ 
/* 1881 */       if (sizeOfEntireBatch < maxAllowedPacket - this.originalSql.length()) {
/* 1882 */         return numBatchedArgs;
/*      */       }
/*      */ 
/* 1885 */       return (int)Math.max(1L, (maxAllowedPacket - this.originalSql.length()) / maxSizeOfParameterSet);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 1895 */     synchronized (checkClosed()) {
/* 1896 */       long sizeOfEntireBatch = 0L;
/* 1897 */       long maxSizeOfParameterSet = 0L;
/*      */ 
/* 1899 */       for (int i = 0; i < numBatchedArgs; i++) {
/* 1900 */         BatchParams paramArg = (BatchParams)this.batchedArgs.get(i);
/*      */ 
/* 1903 */         boolean[] isNullBatch = paramArg.isNull;
/* 1904 */         boolean[] isStreamBatch = paramArg.isStream;
/*      */ 
/* 1906 */         long sizeOfParameterSet = 0L;
/*      */ 
/* 1908 */         for (int j = 0; j < isNullBatch.length; j++) {
/* 1909 */           if (isNullBatch[j] == 0)
/*      */           {
/* 1911 */             if (isStreamBatch[j] != 0) {
/* 1912 */               int streamLength = paramArg.streamLengths[j];
/*      */ 
/* 1914 */               if (streamLength != -1) {
/* 1915 */                 sizeOfParameterSet += streamLength * 2;
/*      */               } else {
/* 1917 */                 int paramLength = paramArg.parameterStrings[j].length;
/* 1918 */                 sizeOfParameterSet += paramLength;
/*      */               }
/*      */             } else {
/* 1921 */               sizeOfParameterSet += paramArg.parameterStrings[j].length;
/*      */             }
/*      */           }
/* 1924 */           else sizeOfParameterSet += 4L;
/*      */ 
/*      */         }
/*      */ 
/* 1936 */         if (getValuesClause() != null)
/* 1937 */           sizeOfParameterSet += getValuesClause().length() + 1;
/*      */         else {
/* 1939 */           sizeOfParameterSet += this.originalSql.length() + 1;
/*      */         }
/*      */ 
/* 1942 */         sizeOfEntireBatch += sizeOfParameterSet;
/*      */ 
/* 1944 */         if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 1945 */           maxSizeOfParameterSet = sizeOfParameterSet;
/*      */         }
/*      */       }
/*      */ 
/* 1949 */       return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int[] executeBatchSerially(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/* 1963 */     synchronized (checkClosed()) {
/* 1964 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1966 */       if (locallyScopedConn == null) {
/* 1967 */         checkClosed();
/*      */       }
/*      */ 
/* 1970 */       int[] updateCounts = null;
/*      */ 
/* 1972 */       if (this.batchedArgs != null) {
/* 1973 */         int nbrCommands = this.batchedArgs.size();
/* 1974 */         updateCounts = new int[nbrCommands];
/*      */ 
/* 1976 */         for (int i = 0; i < nbrCommands; i++) {
/* 1977 */           updateCounts[i] = -3;
/*      */         }
/*      */ 
/* 1980 */         SQLException sqlEx = null;
/*      */ 
/* 1982 */         StatementImpl.CancelTask timeoutTask = null;
/*      */         try
/*      */         {
/* 1985 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/* 1988 */             timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1989 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */ 
/* 1993 */           if (this.retrieveGeneratedKeys) {
/* 1994 */             this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */           }
/*      */ 
/* 1997 */           for (this.batchCommandIndex = 0; this.batchCommandIndex < nbrCommands; this.batchCommandIndex += 1) {
/* 1998 */             Object arg = this.batchedArgs.get(this.batchCommandIndex);
/*      */ 
/* 2000 */             if ((arg instanceof String)) {
/* 2001 */               updateCounts[this.batchCommandIndex] = executeUpdate((String)arg);
/*      */             } else {
/* 2003 */               BatchParams paramArg = (BatchParams)arg;
/*      */               try
/*      */               {
/* 2006 */                 updateCounts[this.batchCommandIndex] = executeUpdate(paramArg.parameterStrings, paramArg.parameterStreams, paramArg.isStream, paramArg.streamLengths, paramArg.isNull, true);
/*      */ 
/* 2011 */                 if (this.retrieveGeneratedKeys) {
/* 2012 */                   ResultSet rs = null;
/*      */                   try
/*      */                   {
/* 2015 */                     if (containsOnDuplicateKeyUpdateInSQL())
/* 2016 */                       rs = getGeneratedKeysInternal(1);
/*      */                     else {
/* 2018 */                       rs = getGeneratedKeysInternal();
/*      */                     }
/* 2020 */                     while (rs.next())
/* 2021 */                       this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */                   }
/*      */                   finally
/*      */                   {
/* 2025 */                     if (rs != null)
/* 2026 */                       rs.close();
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (SQLException ex) {
/* 2031 */                 updateCounts[this.batchCommandIndex] = -3;
/*      */ 
/* 2033 */                 if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */                 {
/* 2037 */                   sqlEx = ex;
/*      */                 } else {
/* 2039 */                   int[] newUpdateCounts = new int[this.batchCommandIndex];
/* 2040 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, this.batchCommandIndex);
/*      */ 
/* 2043 */                   SQLException batchUpdateException = new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */ 
/* 2046 */                   batchUpdateException.initCause(ex);
/* 2047 */                   throw batchUpdateException;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2053 */           if (sqlEx != null) {
/* 2054 */             SQLException batchUpdateException = new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */ 
/* 2056 */             batchUpdateException.initCause(sqlEx);
/* 2057 */             throw batchUpdateException;
/*      */           }
/*      */         } catch (NullPointerException npe) {
/*      */           try {
/* 2061 */             checkClosed();
/*      */           } catch (SQLException connectionClosedEx) {
/* 2063 */             updateCounts[this.batchCommandIndex] = -3;
/*      */ 
/* 2065 */             int[] newUpdateCounts = new int[this.batchCommandIndex];
/*      */ 
/* 2067 */             System.arraycopy(updateCounts, 0, newUpdateCounts, 0, this.batchCommandIndex);
/*      */ 
/* 2070 */             throw new BatchUpdateException(connectionClosedEx.getMessage(), connectionClosedEx.getSQLState(), connectionClosedEx.getErrorCode(), newUpdateCounts);
/*      */           }
/*      */ 
/* 2075 */           throw npe;
/*      */         } finally {
/* 2077 */           this.batchCommandIndex = -1;
/*      */ 
/* 2079 */           if (timeoutTask != null) {
/* 2080 */             timeoutTask.cancel();
/* 2081 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */ 
/* 2084 */           resetCancelledState();
/*      */         }
/*      */       }
/*      */ 
/* 2088 */       return updateCounts != null ? updateCounts : new int[0];
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getDateTime(String pattern)
/*      */   {
/* 2094 */     SimpleDateFormat sdf = new SimpleDateFormat(pattern);
/* 2095 */     return sdf.format(new java.util.Date());
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 2123 */     synchronized (checkClosed())
/*      */     {
/*      */       try {
/* 2126 */         resetCancelledState();
/*      */ 
/* 2128 */         MySQLConnection locallyScopedConnection = this.connection;
/*      */ 
/* 2130 */         this.numberOfExecutions += 1;
/*      */ 
/* 2132 */         if (this.doPingInstead) {
/* 2133 */           doPingInstead();
/*      */ 
/* 2135 */           return this.results;
/*      */         }
/*      */ 
/* 2140 */         StatementImpl.CancelTask timeoutTask = null;
/*      */         ResultSetInternalMethods rs;
/*      */         try {
/* 2143 */           if ((locallyScopedConnection.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConnection.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/* 2146 */             timeoutTask = new StatementImpl.CancelTask(this, this);
/* 2147 */             locallyScopedConnection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */           }
/*      */ 
/* 2151 */           if (!isBatch) {
/* 2152 */             statementBegins();
/*      */           }
/*      */ 
/* 2155 */           rs = locallyScopedConnection.execSQL(this, null, maxRowsToRetrieve, sendPacket, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, metadataFromCache, isBatch);
/*      */ 
/* 2160 */           if (timeoutTask != null) {
/* 2161 */             timeoutTask.cancel();
/*      */ 
/* 2163 */             locallyScopedConnection.getCancelTimer().purge();
/*      */ 
/* 2165 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 2166 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */ 
/* 2169 */             timeoutTask = null;
/*      */           }
/*      */ 
/* 2172 */           synchronized (this.cancelTimeoutMutex) {
/* 2173 */             if (this.wasCancelled) {
/* 2174 */               SQLException cause = null;
/*      */ 
/* 2176 */               if (this.wasCancelledByTimeout)
/* 2177 */                 cause = new MySQLTimeoutException();
/*      */               else {
/* 2179 */                 cause = new MySQLStatementCancelledException();
/*      */               }
/*      */ 
/* 2182 */               resetCancelledState();
/*      */ 
/* 2184 */               throw cause;
/*      */             }
/*      */           }
/*      */         } finally {
/* 2188 */           if (!isBatch) {
/* 2189 */             this.statementExecuting.set(false);
/*      */           }
/*      */ 
/* 2192 */           if (timeoutTask != null) {
/* 2193 */             timeoutTask.cancel();
/* 2194 */             locallyScopedConnection.getCancelTimer().purge();
/*      */           }
/*      */         }
/*      */ 
/* 2198 */         return rs;
/*      */       } catch (NullPointerException npe) {
/* 2200 */         checkClosed();
/*      */ 
/* 2204 */         throw npe;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/* 2219 */     synchronized (checkClosed())
/*      */     {
/* 2221 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 2223 */       checkForDml(this.originalSql, this.firstCharOfStmt);
/*      */ 
/* 2225 */       CachedResultSetMetaData cachedMetadata = null;
/*      */ 
/* 2228 */       clearWarnings();
/*      */ 
/* 2230 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/* 2232 */       this.batchedGeneratedKeys = null;
/*      */ 
/* 2242 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/* 2245 */         Statement stmt = null;
/*      */         try
/*      */         {
/* 2248 */           stmt = this.connection.createStatement();
/*      */ 
/* 2250 */           ((StatementImpl)stmt).executeSimpleNonQuery(this.connection, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */         }
/*      */         finally {
/* 2253 */           if (stmt != null) {
/* 2254 */             stmt.close();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2259 */       Buffer sendPacket = fillSendPacket();
/*      */ 
/* 2261 */       if ((!this.connection.getHoldResultsOpenOverStatementClose()) && 
/* 2262 */         (!this.holdResultsOpenOverClose)) {
/* 2263 */         if (this.results != null) {
/* 2264 */           this.results.realClose(false);
/*      */         }
/* 2266 */         if (this.generatedKeysResults != null) {
/* 2267 */           this.generatedKeysResults.realClose(false);
/*      */         }
/* 2269 */         closeAllOpenResults();
/*      */       }
/*      */ 
/* 2273 */       String oldCatalog = null;
/*      */ 
/* 2275 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 2276 */         oldCatalog = locallyScopedConn.getCatalog();
/* 2277 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 2283 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 2284 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */ 
/* 2287 */       Field[] metadataFromCache = null;
/*      */ 
/* 2289 */       if (cachedMetadata != null) {
/* 2290 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */ 
/* 2293 */       if (locallyScopedConn.useMaxRows())
/*      */       {
/* 2300 */         if (this.hasLimitClause) {
/* 2301 */           this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, metadataFromCache, false);
/*      */         }
/*      */         else
/*      */         {
/* 2305 */           if (this.maxRows <= 0) {
/* 2306 */             executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */           }
/*      */           else {
/* 2309 */             executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=" + this.maxRows);
/*      */           }
/*      */ 
/* 2313 */           this.results = executeInternal(-1, sendPacket, doStreaming, true, metadataFromCache, false);
/*      */ 
/* 2317 */           if (oldCatalog != null)
/* 2318 */             this.connection.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */       else {
/* 2322 */         this.results = executeInternal(-1, sendPacket, doStreaming, true, metadataFromCache, false);
/*      */       }
/*      */ 
/* 2327 */       if (oldCatalog != null) {
/* 2328 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */ 
/* 2331 */       if (cachedMetadata != null) {
/* 2332 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       }
/* 2335 */       else if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 2336 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, null, this.results);
/*      */       }
/*      */ 
/* 2341 */       this.lastInsertId = this.results.getUpdateID();
/*      */ 
/* 2343 */       return this.results;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/* 2359 */     return executeUpdate(true, false);
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 2369 */     synchronized (checkClosed()) {
/* 2370 */       if (clearBatchedGeneratedKeysAndWarnings) {
/* 2371 */         clearWarnings();
/* 2372 */         this.batchedGeneratedKeys = null;
/*      */       }
/*      */ 
/* 2375 */       return executeUpdate(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull, isBatch);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull, boolean isReallyBatch)
/*      */     throws SQLException
/*      */   {
/* 2404 */     synchronized (checkClosed())
/*      */     {
/* 2406 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 2408 */       if (locallyScopedConn.isReadOnly()) {
/* 2409 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2414 */       if ((this.firstCharOfStmt == 'S') && (isSelectQuery()))
/*      */       {
/* 2416 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), "01S03", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2420 */       if (!locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
/* 2421 */         if (this.results != null) {
/* 2422 */           this.results.realClose(false);
/*      */         }
/* 2424 */         if (this.generatedKeysResults != null) {
/* 2425 */           this.generatedKeysResults.realClose(false);
/*      */         }
/* 2427 */         closeAllOpenResults();
/*      */       }
/*      */ 
/* 2430 */       ResultSetInternalMethods rs = null;
/*      */ 
/* 2432 */       Buffer sendPacket = fillSendPacket(batchedParameterStrings, batchedParameterStreams, batchedIsStream, batchedStreamLengths);
/*      */ 
/* 2436 */       String oldCatalog = null;
/*      */ 
/* 2438 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 2439 */         oldCatalog = locallyScopedConn.getCatalog();
/* 2440 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 2446 */       if (locallyScopedConn.useMaxRows()) {
/* 2447 */         executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */       }
/*      */ 
/* 2451 */       boolean oldInfoMsgState = false;
/*      */ 
/* 2453 */       if (this.retrieveGeneratedKeys) {
/* 2454 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 2455 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */ 
/* 2458 */       rs = executeInternal(-1, sendPacket, false, false, null, isReallyBatch);
/*      */ 
/* 2461 */       if (this.retrieveGeneratedKeys) {
/* 2462 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 2463 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */ 
/* 2466 */       if (oldCatalog != null) {
/* 2467 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */ 
/* 2470 */       this.results = rs;
/*      */ 
/* 2472 */       this.updateCount = rs.getUpdateCount();
/*      */ 
/* 2474 */       if ((containsOnDuplicateKeyUpdateInSQL()) && (this.compensateForOnDuplicateKeyUpdate))
/*      */       {
/* 2476 */         if ((this.updateCount == 2L) || (this.updateCount == 0L)) {
/* 2477 */           this.updateCount = 1L;
/*      */         }
/*      */       }
/*      */ 
/* 2481 */       int truncatedUpdateCount = 0;
/*      */ 
/* 2483 */       if (this.updateCount > 2147483647L)
/* 2484 */         truncatedUpdateCount = 2147483647;
/*      */       else {
/* 2486 */         truncatedUpdateCount = (int)this.updateCount;
/*      */       }
/*      */ 
/* 2489 */       this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 2491 */       return truncatedUpdateCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 2496 */     return this.parseInfo.isOnDuplicateKeyUpdate;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/* 2509 */     synchronized (checkClosed()) {
/* 2510 */       return fillSendPacket(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/* 2535 */     synchronized (checkClosed()) {
/* 2536 */       Buffer sendPacket = this.connection.getIO().getSharedSendPacket();
/*      */ 
/* 2538 */       sendPacket.clear();
/*      */ 
/* 2540 */       sendPacket.writeByte(3);
/*      */ 
/* 2542 */       boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 2549 */       int ensurePacketSize = 0;
/*      */ 
/* 2551 */       String statementComment = this.connection.getStatementComment();
/*      */ 
/* 2553 */       byte[] commentAsBytes = null;
/*      */ 
/* 2555 */       if (statementComment != null) {
/* 2556 */         if (this.charConverter != null)
/* 2557 */           commentAsBytes = this.charConverter.toBytes(statementComment);
/*      */         else {
/* 2559 */           commentAsBytes = StringUtils.getBytes(statementComment, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */         }
/*      */ 
/* 2565 */         ensurePacketSize += commentAsBytes.length;
/* 2566 */         ensurePacketSize += 6;
/*      */       }
/*      */ 
/* 2569 */       for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 2570 */         if ((batchedIsStream[i] != 0) && (useStreamLengths)) {
/* 2571 */           ensurePacketSize += batchedStreamLengths[i];
/*      */         }
/*      */       }
/*      */ 
/* 2575 */       if (ensurePacketSize != 0) {
/* 2576 */         sendPacket.ensureCapacity(ensurePacketSize);
/*      */       }
/*      */ 
/* 2579 */       if (commentAsBytes != null) {
/* 2580 */         sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2581 */         sendPacket.writeBytesNoNull(commentAsBytes);
/* 2582 */         sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */       }
/*      */ 
/* 2585 */       for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 2586 */         checkAllParametersSet(batchedParameterStrings[i], batchedParameterStreams[i], i);
/*      */ 
/* 2589 */         sendPacket.writeBytesNoNull(this.staticSqlStrings[i]);
/*      */ 
/* 2591 */         if (batchedIsStream[i] != 0) {
/* 2592 */           streamToBytes(sendPacket, batchedParameterStreams[i], true, batchedStreamLengths[i], useStreamLengths);
/*      */         }
/*      */         else {
/* 2595 */           sendPacket.writeBytesNoNull(batchedParameterStrings[i]);
/*      */         }
/*      */       }
/*      */ 
/* 2599 */       sendPacket.writeBytesNoNull(this.staticSqlStrings[batchedParameterStrings.length]);
/*      */ 
/* 2602 */       return sendPacket;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkAllParametersSet(byte[] parameterString, InputStream parameterStream, int columnIndex) throws SQLException
/*      */   {
/* 2608 */     if ((parameterString == null) && (parameterStream == null))
/*      */     {
/* 2611 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.40") + (columnIndex + 1), "07001", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches)
/*      */     throws SQLException
/*      */   {
/* 2621 */     synchronized (checkClosed()) {
/* 2622 */       PreparedStatement pstmt = new PreparedStatement(localConn, "Rewritten batch of: " + this.originalSql, this.currentCatalog, this.parseInfo.getParseInfoForBatch(numBatches));
/* 2623 */       pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/* 2624 */       pstmt.rewrittenBatchSize = numBatches;
/*      */ 
/* 2626 */       return pstmt;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setRetrieveGeneratedKeys(boolean flag) throws SQLException {
/* 2631 */     synchronized (checkClosed()) {
/* 2632 */       this.retrieveGeneratedKeys = flag;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getRewrittenBatchSize()
/*      */   {
/* 2639 */     return this.rewrittenBatchSize;
/*      */   }
/*      */ 
/*      */   public String getNonRewrittenSql() throws SQLException {
/* 2643 */     synchronized (checkClosed()) {
/* 2644 */       int indexOfBatch = this.originalSql.indexOf(" of: ");
/*      */ 
/* 2646 */       if (indexOfBatch != -1) {
/* 2647 */         return this.originalSql.substring(indexOfBatch + 5);
/*      */       }
/*      */ 
/* 2650 */       return this.originalSql;
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getBytesRepresentation(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 2668 */     synchronized (checkClosed()) {
/* 2669 */       if (this.isStream[parameterIndex] != 0) {
/* 2670 */         return streamToBytes(this.parameterStreams[parameterIndex], false, this.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */       }
/*      */ 
/* 2675 */       byte[] parameterVal = this.parameterValues[parameterIndex];
/*      */ 
/* 2677 */       if (parameterVal == null) {
/* 2678 */         return null;
/*      */       }
/*      */ 
/* 2681 */       if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39))
/*      */       {
/* 2683 */         byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2684 */         System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */ 
/* 2687 */         return valNoQuotes;
/*      */       }
/*      */ 
/* 2690 */       return parameterVal;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected byte[] getBytesRepresentationForBatch(int parameterIndex, int commandIndex)
/*      */     throws SQLException
/*      */   {
/* 2703 */     synchronized (checkClosed()) {
/* 2704 */       Object batchedArg = this.batchedArgs.get(commandIndex);
/* 2705 */       if ((batchedArg instanceof String)) {
/*      */         try {
/* 2707 */           return StringUtils.getBytes((String)batchedArg, this.charEncoding);
/*      */         }
/*      */         catch (UnsupportedEncodingException uue) {
/* 2710 */           throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2717 */       BatchParams params = (BatchParams)batchedArg;
/* 2718 */       if (params.isStream[parameterIndex] != 0) {
/* 2719 */         return streamToBytes(params.parameterStreams[parameterIndex], false, params.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */       }
/*      */ 
/* 2722 */       byte[] parameterVal = params.parameterStrings[parameterIndex];
/* 2723 */       if (parameterVal == null) {
/* 2724 */         return null;
/*      */       }
/* 2726 */       if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39))
/*      */       {
/* 2728 */         byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2729 */         System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */ 
/* 2732 */         return valNoQuotes;
/*      */       }
/*      */ 
/* 2735 */       return parameterVal;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final String getDateTimePattern(String dt, boolean toTime)
/*      */     throws Exception
/*      */   {
/* 2746 */     int dtLength = dt != null ? dt.length() : 0;
/*      */ 
/* 2748 */     if ((dtLength >= 8) && (dtLength <= 10)) {
/* 2749 */       int dashCount = 0;
/* 2750 */       boolean isDateOnly = true;
/*      */ 
/* 2752 */       for (int i = 0; i < dtLength; i++) {
/* 2753 */         char c = dt.charAt(i);
/*      */ 
/* 2755 */         if ((!Character.isDigit(c)) && (c != '-')) {
/* 2756 */           isDateOnly = false;
/*      */ 
/* 2758 */           break;
/*      */         }
/*      */ 
/* 2761 */         if (c == '-') {
/* 2762 */           dashCount++;
/*      */         }
/*      */       }
/*      */ 
/* 2766 */       if ((isDateOnly) && (dashCount == 2)) {
/* 2767 */         return "yyyy-MM-dd";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2774 */     boolean colonsOnly = true;
/*      */ 
/* 2776 */     for (int i = 0; i < dtLength; i++) {
/* 2777 */       char c = dt.charAt(i);
/*      */ 
/* 2779 */       if ((!Character.isDigit(c)) && (c != ':')) {
/* 2780 */         colonsOnly = false;
/*      */ 
/* 2782 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 2786 */     if (colonsOnly) {
/* 2787 */       return "HH:mm:ss";
/*      */     }
/*      */ 
/* 2796 */     StringReader reader = new StringReader(dt + " ");
/* 2797 */     ArrayList vec = new ArrayList();
/* 2798 */     ArrayList vecRemovelist = new ArrayList();
/* 2799 */     Object[] nv = new Object[3];
/*      */ 
/* 2801 */     nv[0] = Character.valueOf('y');
/* 2802 */     nv[1] = new StringBuffer();
/* 2803 */     nv[2] = Integer.valueOf(0);
/* 2804 */     vec.add(nv);
/*      */ 
/* 2806 */     if (toTime) {
/* 2807 */       nv = new Object[3];
/* 2808 */       nv[0] = Character.valueOf('h');
/* 2809 */       nv[1] = new StringBuffer();
/* 2810 */       nv[2] = Integer.valueOf(0);
/* 2811 */       vec.add(nv);
/*      */     }
/*      */     int z;
/* 2814 */     while ((z = reader.read()) != -1) {
/* 2815 */       char separator = (char)z;
/* 2816 */       int maxvecs = vec.size();
/*      */ 
/* 2818 */       for (int count = 0; count < maxvecs; count++) {
/* 2819 */         Object[] v = (Object[])vec.get(count);
/* 2820 */         int n = ((Integer)v[2]).intValue();
/* 2821 */         char c = getSuccessor(((Character)v[0]).charValue(), n);
/*      */ 
/* 2823 */         if (!Character.isLetterOrDigit(separator)) {
/* 2824 */           if ((c == ((Character)v[0]).charValue()) && (c != 'S')) {
/* 2825 */             vecRemovelist.add(v);
/*      */           } else {
/* 2827 */             ((StringBuffer)v[1]).append(separator);
/*      */ 
/* 2829 */             if ((c == 'X') || (c == 'Y'))
/* 2830 */               v[2] = Integer.valueOf(4);
/*      */           }
/*      */         }
/*      */         else {
/* 2834 */           if (c == 'X') {
/* 2835 */             c = 'y';
/* 2836 */             nv = new Object[3];
/* 2837 */             nv[1] = new StringBuffer(((StringBuffer)v[1]).toString()).append('M');
/*      */ 
/* 2839 */             nv[0] = Character.valueOf('M');
/* 2840 */             nv[2] = Integer.valueOf(1);
/* 2841 */             vec.add(nv);
/* 2842 */           } else if (c == 'Y') {
/* 2843 */             c = 'M';
/* 2844 */             nv = new Object[3];
/* 2845 */             nv[1] = new StringBuffer(((StringBuffer)v[1]).toString()).append('d');
/*      */ 
/* 2847 */             nv[0] = Character.valueOf('d');
/* 2848 */             nv[2] = Integer.valueOf(1);
/* 2849 */             vec.add(nv);
/*      */           }
/*      */ 
/* 2852 */           ((StringBuffer)v[1]).append(c);
/*      */ 
/* 2854 */           if (c == ((Character)v[0]).charValue()) {
/* 2855 */             v[2] = Integer.valueOf(n + 1);
/*      */           } else {
/* 2857 */             v[0] = Character.valueOf(c);
/* 2858 */             v[2] = Integer.valueOf(1);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2863 */       int size = vecRemovelist.size();
/*      */ 
/* 2865 */       for (int i = 0; i < size; i++) {
/* 2866 */         Object[] v = (Object[])vecRemovelist.get(i);
/* 2867 */         vec.remove(v);
/*      */       }
/*      */ 
/* 2870 */       vecRemovelist.clear();
/*      */     }
/*      */ 
/* 2873 */     int size = vec.size();
/*      */ 
/* 2875 */     for (int i = 0; i < size; i++) {
/* 2876 */       Object[] v = (Object[])vec.get(i);
/* 2877 */       char c = ((Character)v[0]).charValue();
/* 2878 */       int n = ((Integer)v[2]).intValue();
/*      */ 
/* 2880 */       boolean bk = getSuccessor(c, n) != c;
/* 2881 */       boolean atEnd = ((c == 's') || (c == 'm') || ((c == 'h') && (toTime))) && (bk);
/* 2882 */       boolean finishesAtDate = (bk) && (c == 'd') && (!toTime);
/* 2883 */       boolean containsEnd = ((StringBuffer)v[1]).toString().indexOf('W') != -1;
/*      */ 
/* 2886 */       if (((!atEnd) && (!finishesAtDate)) || (containsEnd)) {
/* 2887 */         vecRemovelist.add(v);
/*      */       }
/*      */     }
/*      */ 
/* 2891 */     size = vecRemovelist.size();
/*      */ 
/* 2893 */     for (int i = 0; i < size; i++) {
/* 2894 */       vec.remove(vecRemovelist.get(i));
/*      */     }
/*      */ 
/* 2897 */     vecRemovelist.clear();
/* 2898 */     Object[] v = (Object[])vec.get(0);
/*      */ 
/* 2900 */     StringBuffer format = (StringBuffer)v[1];
/* 2901 */     format.setLength(format.length() - 1);
/*      */ 
/* 2903 */     return format.toString();
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2918 */     synchronized (checkClosed())
/*      */     {
/* 2930 */       if (!isSelectQuery()) {
/* 2931 */         return null;
/*      */       }
/*      */ 
/* 2934 */       PreparedStatement mdStmt = null;
/* 2935 */       ResultSet mdRs = null;
/*      */ 
/* 2937 */       if (this.pstmtResultMetaData == null) {
/*      */         try {
/* 2939 */           mdStmt = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog, this.parseInfo);
/*      */ 
/* 2942 */           mdStmt.setMaxRows(1);
/*      */ 
/* 2944 */           int paramCount = this.parameterValues.length;
/*      */ 
/* 2946 */           for (int i = 1; i <= paramCount; i++) {
/* 2947 */             mdStmt.setString(i, "");
/*      */           }
/*      */ 
/* 2950 */           boolean hadResults = mdStmt.execute();
/*      */ 
/* 2952 */           if (hadResults) {
/* 2953 */             mdRs = mdStmt.getResultSet();
/*      */ 
/* 2955 */             this.pstmtResultMetaData = mdRs.getMetaData();
/*      */           } else {
/* 2957 */             this.pstmtResultMetaData = new ResultSetMetaData(new Field[0], this.connection.getUseOldAliasMetadataBehavior(), getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*      */           SQLException sqlExRethrow;
/* 2962 */           SQLException sqlExRethrow = null;
/*      */ 
/* 2964 */           if (mdRs != null) {
/*      */             try {
/* 2966 */               mdRs.close();
/*      */             } catch (SQLException sqlEx) {
/* 2968 */               sqlExRethrow = sqlEx;
/*      */             }
/*      */ 
/* 2971 */             mdRs = null;
/*      */           }
/*      */ 
/* 2974 */           if (mdStmt != null) {
/*      */             try {
/* 2976 */               mdStmt.close();
/*      */             } catch (SQLException sqlEx) {
/* 2978 */               sqlExRethrow = sqlEx;
/*      */             }
/*      */ 
/* 2981 */             mdStmt = null;
/*      */           }
/*      */ 
/* 2984 */           if (sqlExRethrow != null) {
/* 2985 */             throw sqlExRethrow;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2990 */       return this.pstmtResultMetaData;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isSelectQuery() throws SQLException {
/* 2995 */     synchronized (checkClosed()) {
/* 2996 */       return StringUtils.startsWithIgnoreCaseAndWs(StringUtils.stripComments(this.originalSql, "'\"", "'\"", true, false, true, true), "SELECT");
/*      */     }
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 3008 */     synchronized (checkClosed()) {
/* 3009 */       if (this.parameterMetaData == null) {
/* 3010 */         if (this.connection.getGenerateSimpleParameterMetadata())
/* 3011 */           this.parameterMetaData = new MysqlParameterMetadata(this.parameterCount);
/*      */         else {
/* 3013 */           this.parameterMetaData = new MysqlParameterMetadata(null, this.parameterCount, getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3018 */       return this.parameterMetaData;
/*      */     }
/*      */   }
/*      */ 
/*      */   ParseInfo getParseInfo() {
/* 3023 */     return this.parseInfo;
/*      */   }
/*      */ 
/*      */   private final char getSuccessor(char c, int n) {
/* 3027 */     return (c == 's') && (n < 2) ? 's' : c == 'm' ? 's' : (c == 'm') && (n < 2) ? 'm' : c == 'H' ? 'm' : (c == 'H') && (n < 2) ? 'H' : c == 'd' ? 'H' : (c == 'd') && (n < 2) ? 'd' : c == 'M' ? 'd' : (c == 'M') && (n < 3) ? 'M' : (c == 'M') && (n == 2) ? 'Y' : c == 'y' ? 'M' : (c == 'y') && (n < 4) ? 'y' : (c == 'y') && (n == 2) ? 'X' : 'W';
/*      */   }
/*      */ 
/*      */   private final void hexEscapeBlock(byte[] buf, Buffer packet, int size)
/*      */     throws SQLException
/*      */   {
/* 3053 */     for (int i = 0; i < size; i++) {
/* 3054 */       byte b = buf[i];
/* 3055 */       int lowBits = (b & 0xFF) / 16;
/* 3056 */       int highBits = (b & 0xFF) % 16;
/*      */ 
/* 3058 */       packet.writeByte(HEX_DIGITS[lowBits]);
/* 3059 */       packet.writeByte(HEX_DIGITS[highBits]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initializeFromParseInfo() throws SQLException {
/* 3064 */     synchronized (checkClosed()) {
/* 3065 */       this.staticSqlStrings = this.parseInfo.staticSql;
/* 3066 */       this.hasLimitClause = this.parseInfo.foundLimitClause;
/* 3067 */       this.isLoadDataQuery = this.parseInfo.foundLoadData;
/* 3068 */       this.firstCharOfStmt = this.parseInfo.firstStmtChar;
/*      */ 
/* 3070 */       this.parameterCount = (this.staticSqlStrings.length - 1);
/*      */ 
/* 3072 */       this.parameterValues = new byte[this.parameterCount][];
/* 3073 */       this.parameterStreams = new InputStream[this.parameterCount];
/* 3074 */       this.isStream = new boolean[this.parameterCount];
/* 3075 */       this.streamLengths = new int[this.parameterCount];
/* 3076 */       this.isNull = new boolean[this.parameterCount];
/* 3077 */       this.parameterTypes = new int[this.parameterCount];
/*      */ 
/* 3079 */       clearParameters();
/*      */ 
/* 3081 */       for (int j = 0; j < this.parameterCount; j++)
/* 3082 */         this.isStream[j] = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isNull(int paramIndex) throws SQLException
/*      */   {
/* 3088 */     synchronized (checkClosed()) {
/* 3089 */       return this.isNull[paramIndex];
/*      */     }
/*      */   }
/*      */   private final int readblock(InputStream i, byte[] b) throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try {
/* 3095 */       return i.read(b);
/*      */     } catch (Throwable ex) {
/* 3097 */       sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000", getExceptionInterceptor());
/*      */ 
/* 3099 */       sqlEx.initCause(ex);
/*      */     }
/* 3101 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private final int readblock(InputStream i, byte[] b, int length) throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try {
/* 3108 */       int lengthToRead = length;
/*      */ 
/* 3110 */       if (lengthToRead > b.length) {
/* 3111 */         lengthToRead = b.length;
/*      */       }
/*      */ 
/* 3114 */       return i.read(b, 0, lengthToRead);
/*      */     } catch (Throwable ex) {
/* 3116 */       sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000", getExceptionInterceptor());
/*      */ 
/* 3118 */       sqlEx.initCause(ex);
/*      */     }
/* 3120 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/*      */     MySQLConnection locallyScopedConn;
/*      */     try
/*      */     {
/* 3138 */       locallyScopedConn = checkClosed();
/*      */     } catch (SQLException sqlEx) {
/* 3140 */       return;
/*      */     }
/*      */ 
/* 3143 */     synchronized (locallyScopedConn)
/*      */     {
/* 3145 */       if ((this.useUsageAdvisor) && 
/* 3146 */         (this.numberOfExecutions <= 1)) {
/* 3147 */         String message = Messages.getString("PreparedStatement.43");
/*      */ 
/* 3149 */         this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */       }
/*      */ 
/* 3158 */       super.realClose(calledExplicitly, closeOpenResults);
/*      */ 
/* 3160 */       this.dbmd = null;
/* 3161 */       this.originalSql = null;
/* 3162 */       this.staticSqlStrings = ((byte[][])null);
/* 3163 */       this.parameterValues = ((byte[][])null);
/* 3164 */       this.parameterStreams = null;
/* 3165 */       this.isStream = null;
/* 3166 */       this.streamLengths = null;
/* 3167 */       this.isNull = null;
/* 3168 */       this.streamConvertBuf = null;
/* 3169 */       this.parameterTypes = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 3187 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 3214 */     if (x == null)
/* 3215 */       setNull(parameterIndex, 12);
/*      */     else
/* 3217 */       setBinaryStream(parameterIndex, x, length);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 3235 */     if (x == null) {
/* 3236 */       setNull(parameterIndex, 3);
/*      */     } else {
/* 3238 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString(x)));
/*      */ 
/* 3241 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 3;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 3267 */     synchronized (checkClosed()) {
/* 3268 */       if (x == null) {
/* 3269 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 3271 */         int parameterIndexOffset = getParameterIndexOffset();
/*      */ 
/* 3273 */         if ((parameterIndex < 1) || (parameterIndex > this.staticSqlStrings.length))
/*      */         {
/* 3275 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.2") + parameterIndex + Messages.getString("PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString("PreparedStatement.4"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 3280 */         if ((parameterIndexOffset == -1) && (parameterIndex == 1)) {
/* 3281 */           throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 3286 */         this.parameterStreams[(parameterIndex - 1 + parameterIndexOffset)] = x;
/* 3287 */         this.isStream[(parameterIndex - 1 + parameterIndexOffset)] = true;
/* 3288 */         this.streamLengths[(parameterIndex - 1 + parameterIndexOffset)] = length;
/* 3289 */         this.isNull[(parameterIndex - 1 + parameterIndexOffset)] = false;
/* 3290 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2004;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
/*      */   {
/* 3297 */     setBinaryStream(parameterIndex, inputStream, (int)length);
/*      */   }
/*      */ 
/*      */   public void setBlob(int i, Blob x)
/*      */     throws SQLException
/*      */   {
/* 3312 */     if (x == null) {
/* 3313 */       setNull(i, 2004);
/*      */     } else {
/* 3315 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/* 3317 */       bytesOut.write(39);
/* 3318 */       escapeblockFast(x.getBytes(1L, (int)x.length()), bytesOut, (int)x.length());
/*      */ 
/* 3320 */       bytesOut.write(39);
/*      */ 
/* 3322 */       setInternal(i, bytesOut.toByteArray());
/*      */ 
/* 3324 */       this.parameterTypes[(i - 1 + getParameterIndexOffset())] = 2004;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 3341 */     if (this.useTrueBoolean) {
/* 3342 */       setInternal(parameterIndex, x ? "1" : "0");
/*      */     } else {
/* 3344 */       setInternal(parameterIndex, x ? "'t'" : "'f'");
/*      */ 
/* 3346 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 16;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 3363 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 3365 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -6;
/*      */   }
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 3382 */     setBytes(parameterIndex, x, true, true);
/*      */ 
/* 3384 */     if (x != null)
/* 3385 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -2;
/*      */   }
/*      */ 
/*      */   protected void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars)
/*      */     throws SQLException
/*      */   {
/* 3392 */     synchronized (checkClosed()) {
/* 3393 */       if (x == null) {
/* 3394 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 3396 */         String connectionEncoding = this.connection.getEncoding();
/*      */         try
/*      */         {
/* 3399 */           if ((this.connection.isNoBackslashEscapesSet()) || ((escapeForMBChars) && (this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding))))
/*      */           {
/* 3407 */             ByteArrayOutputStream bOut = new ByteArrayOutputStream(x.length * 2 + 3);
/*      */ 
/* 3409 */             bOut.write(120);
/* 3410 */             bOut.write(39);
/*      */ 
/* 3412 */             for (int i = 0; i < x.length; i++) {
/* 3413 */               int lowBits = (x[i] & 0xFF) / 16;
/* 3414 */               int highBits = (x[i] & 0xFF) % 16;
/*      */ 
/* 3416 */               bOut.write(HEX_DIGITS[lowBits]);
/* 3417 */               bOut.write(HEX_DIGITS[highBits]);
/*      */             }
/*      */ 
/* 3420 */             bOut.write(39);
/*      */ 
/* 3422 */             setInternal(parameterIndex, bOut.toByteArray());
/*      */ 
/* 3424 */             return;
/*      */           }
/*      */         } catch (SQLException ex) {
/* 3427 */           throw ex;
/*      */         } catch (RuntimeException ex) {
/* 3429 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 3430 */           sqlEx.initCause(ex);
/* 3431 */           throw sqlEx;
/*      */         }
/*      */ 
/* 3435 */         int numBytes = x.length;
/*      */ 
/* 3437 */         int pad = 2;
/*      */ 
/* 3439 */         boolean needsIntroducer = (checkForIntroducer) && (this.connection.versionMeetsMinimum(4, 1, 0));
/*      */ 
/* 3442 */         if (needsIntroducer) {
/* 3443 */           pad += 7;
/*      */         }
/*      */ 
/* 3446 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
/*      */ 
/* 3449 */         if (needsIntroducer) {
/* 3450 */           bOut.write(95);
/* 3451 */           bOut.write(98);
/* 3452 */           bOut.write(105);
/* 3453 */           bOut.write(110);
/* 3454 */           bOut.write(97);
/* 3455 */           bOut.write(114);
/* 3456 */           bOut.write(121);
/*      */         }
/* 3458 */         bOut.write(39);
/*      */ 
/* 3460 */         for (int i = 0; i < numBytes; i++) {
/* 3461 */           byte b = x[i];
/*      */ 
/* 3463 */           switch (b) {
/*      */           case 0:
/* 3465 */             bOut.write(92);
/* 3466 */             bOut.write(48);
/*      */ 
/* 3468 */             break;
/*      */           case 10:
/* 3471 */             bOut.write(92);
/* 3472 */             bOut.write(110);
/*      */ 
/* 3474 */             break;
/*      */           case 13:
/* 3477 */             bOut.write(92);
/* 3478 */             bOut.write(114);
/*      */ 
/* 3480 */             break;
/*      */           case 92:
/* 3483 */             bOut.write(92);
/* 3484 */             bOut.write(92);
/*      */ 
/* 3486 */             break;
/*      */           case 39:
/* 3489 */             bOut.write(92);
/* 3490 */             bOut.write(39);
/*      */ 
/* 3492 */             break;
/*      */           case 34:
/* 3495 */             bOut.write(92);
/* 3496 */             bOut.write(34);
/*      */ 
/* 3498 */             break;
/*      */           case 26:
/* 3501 */             bOut.write(92);
/* 3502 */             bOut.write(90);
/*      */ 
/* 3504 */             break;
/*      */           default:
/* 3507 */             bOut.write(b);
/*      */           }
/*      */         }
/*      */ 
/* 3511 */         bOut.write(39);
/*      */ 
/* 3513 */         setInternal(parameterIndex, bOut.toByteArray());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes)
/*      */     throws SQLException
/*      */   {
/* 3532 */     byte[] parameterWithQuotes = new byte[parameterAsBytes.length + 2];
/* 3533 */     parameterWithQuotes[0] = 39;
/* 3534 */     System.arraycopy(parameterAsBytes, 0, parameterWithQuotes, 1, parameterAsBytes.length);
/*      */ 
/* 3536 */     parameterWithQuotes[(parameterAsBytes.length + 1)] = 39;
/*      */ 
/* 3538 */     setInternal(parameterIndex, parameterWithQuotes);
/*      */   }
/*      */ 
/*      */   protected void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException
/*      */   {
/* 3543 */     setInternal(parameterIndex, parameterAsBytes);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 3570 */     synchronized (checkClosed()) {
/*      */       try {
/* 3572 */         if (reader == null) {
/* 3573 */           setNull(parameterIndex, -1);
/*      */         } else {
/* 3575 */           char[] c = null;
/* 3576 */           int len = 0;
/*      */ 
/* 3578 */           boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 3581 */           String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 3583 */           if ((useLength) && (length != -1)) {
/* 3584 */             c = new char[length];
/*      */ 
/* 3586 */             int numCharsRead = readFully(reader, c, length);
/*      */ 
/* 3591 */             if (forcedEncoding == null)
/* 3592 */               setString(parameterIndex, new String(c, 0, numCharsRead));
/*      */             else
/*      */               try {
/* 3595 */                 setBytes(parameterIndex, StringUtils.getBytes(new String(c, 0, numCharsRead), forcedEncoding));
/*      */               }
/*      */               catch (UnsupportedEncodingException uee)
/*      */               {
/* 3599 */                 throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */               }
/*      */           }
/*      */           else
/*      */           {
/* 3604 */             c = new char[4096];
/*      */ 
/* 3606 */             StringBuffer buf = new StringBuffer();
/*      */ 
/* 3608 */             while ((len = reader.read(c)) != -1) {
/* 3609 */               buf.append(c, 0, len);
/*      */             }
/*      */ 
/* 3612 */             if (forcedEncoding == null)
/* 3613 */               setString(parameterIndex, buf.toString());
/*      */             else {
/*      */               try {
/* 3616 */                 setBytes(parameterIndex, StringUtils.getBytes(buf.toString(), forcedEncoding));
/*      */               }
/*      */               catch (UnsupportedEncodingException uee) {
/* 3619 */                 throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 3625 */           this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */         }
/*      */       } catch (IOException ioEx) {
/* 3628 */         throw SQLError.createSQLException(ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int i, Clob x)
/*      */     throws SQLException
/*      */   {
/* 3646 */     synchronized (checkClosed()) {
/* 3647 */       if (x == null) {
/* 3648 */         setNull(i, 2005);
/*      */       }
/*      */       else {
/* 3651 */         String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 3653 */         if (forcedEncoding == null)
/* 3654 */           setString(i, x.getSubString(1L, (int)x.length()));
/*      */         else {
/*      */           try {
/* 3657 */             setBytes(i, StringUtils.getBytes(x.getSubString(1L, (int)x.length()), forcedEncoding));
/*      */           }
/*      */           catch (UnsupportedEncodingException uee) {
/* 3660 */             throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3665 */         this.parameterTypes[(i - 1 + getParameterIndexOffset())] = 2005;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 3684 */     setDate(parameterIndex, x, null);
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3703 */     if (x == null) {
/* 3704 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 3706 */       checkClosed();
/*      */ 
/* 3708 */       if (!this.useLegacyDatetimeCode) {
/* 3709 */         newSetDateInternal(parameterIndex, x, cal);
/*      */       }
/*      */       else
/*      */       {
/* 3713 */         SimpleDateFormat dateFormatter = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */ 
/* 3715 */         setInternal(parameterIndex, dateFormatter.format(x));
/*      */ 
/* 3717 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 91;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 3735 */     synchronized (checkClosed()) {
/* 3736 */       if ((!this.connection.getAllowNanAndInf()) && ((x == (1.0D / 0.0D)) || (x == (-1.0D / 0.0D)) || (Double.isNaN(x))))
/*      */       {
/* 3739 */         throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3745 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */ 
/* 3748 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 8;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 3765 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */ 
/* 3768 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 6;
/*      */   }
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 3784 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 3786 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 4;
/*      */   }
/*      */ 
/*      */   protected final void setInternal(int paramIndex, byte[] val) throws SQLException
/*      */   {
/* 3791 */     synchronized (checkClosed())
/*      */     {
/* 3793 */       int parameterIndexOffset = getParameterIndexOffset();
/*      */ 
/* 3795 */       checkBounds(paramIndex, parameterIndexOffset);
/*      */ 
/* 3797 */       this.isStream[(paramIndex - 1 + parameterIndexOffset)] = false;
/* 3798 */       this.isNull[(paramIndex - 1 + parameterIndexOffset)] = false;
/* 3799 */       this.parameterStreams[(paramIndex - 1 + parameterIndexOffset)] = null;
/* 3800 */       this.parameterValues[(paramIndex - 1 + parameterIndexOffset)] = val;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException
/*      */   {
/* 3806 */     synchronized (checkClosed()) {
/* 3807 */       if (paramIndex < 1) {
/* 3808 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3812 */       if (paramIndex > this.parameterCount) {
/* 3813 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + this.parameterValues.length + Messages.getString("PreparedStatement.53"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3818 */       if ((parameterIndexOffset == -1) && (paramIndex == 1))
/* 3819 */         throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void setInternal(int paramIndex, String val)
/*      */     throws SQLException
/*      */   {
/* 3827 */     synchronized (checkClosed())
/*      */     {
/* 3829 */       byte[] parameterAsBytes = null;
/*      */ 
/* 3831 */       if (this.charConverter != null)
/* 3832 */         parameterAsBytes = this.charConverter.toBytes(val);
/*      */       else {
/* 3834 */         parameterAsBytes = StringUtils.getBytes(val, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3840 */       setInternal(paramIndex, parameterAsBytes);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 3857 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 3859 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -5;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 3879 */     synchronized (checkClosed()) {
/* 3880 */       setInternal(parameterIndex, "null");
/* 3881 */       this.isNull[(parameterIndex - 1 + getParameterIndexOffset())] = true;
/*      */ 
/* 3883 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String arg)
/*      */     throws SQLException
/*      */   {
/* 3906 */     setNull(parameterIndex, sqlType);
/*      */ 
/* 3908 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 0;
/*      */   }
/*      */ 
/*      */   private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     Number parameterAsNum;
/*      */     Number parameterAsNum;
/* 3914 */     if ((parameterObj instanceof Boolean)) {
/* 3915 */       parameterAsNum = ((Boolean)parameterObj).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
/*      */     }
/* 3918 */     else if ((parameterObj instanceof String))
/*      */     {
/*      */       Number parameterAsNum;
/* 3919 */       switch (targetSqlType) {
/*      */       case -7:
/* 3921 */         if (("1".equals(parameterObj)) || ("0".equals(parameterObj)))
/*      */         {
/* 3923 */           Number parameterAsNum = Integer.valueOf((String)parameterObj); break;
/*      */         }
/* 3925 */         boolean parameterAsBoolean = "true".equalsIgnoreCase((String)parameterObj);
/*      */ 
/* 3928 */         parameterAsNum = parameterAsBoolean ? Integer.valueOf(1) : Integer.valueOf(0);
/*      */ 
/* 3932 */         break;
/*      */       case -6:
/*      */       case 4:
/*      */       case 5:
/* 3937 */         parameterAsNum = Integer.valueOf((String)parameterObj);
/*      */ 
/* 3940 */         break;
/*      */       case -5:
/* 3943 */         parameterAsNum = Long.valueOf((String)parameterObj);
/*      */ 
/* 3946 */         break;
/*      */       case 7:
/* 3949 */         parameterAsNum = Float.valueOf((String)parameterObj);
/*      */ 
/* 3952 */         break;
/*      */       case 6:
/*      */       case 8:
/* 3956 */         parameterAsNum = Double.valueOf((String)parameterObj);
/*      */ 
/* 3959 */         break;
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       default:
/* 3964 */         parameterAsNum = new BigDecimal((String)parameterObj); break;
/*      */       }
/*      */     }
/*      */     else {
/* 3968 */       parameterAsNum = (Number)parameterObj;
/*      */     }
/*      */ 
/* 3971 */     switch (targetSqlType) {
/*      */     case -7:
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/* 3976 */       setInt(parameterIndex, parameterAsNum.intValue());
/*      */ 
/* 3978 */       break;
/*      */     case -5:
/* 3981 */       setLong(parameterIndex, parameterAsNum.longValue());
/*      */ 
/* 3983 */       break;
/*      */     case 7:
/* 3986 */       setFloat(parameterIndex, parameterAsNum.floatValue());
/*      */ 
/* 3988 */       break;
/*      */     case 6:
/*      */     case 8:
/* 3992 */       setDouble(parameterIndex, parameterAsNum.doubleValue());
/*      */ 
/* 3994 */       break;
/*      */     case 2:
/*      */     case 3:
/* 3999 */       if ((parameterAsNum instanceof BigDecimal)) {
/* 4000 */         BigDecimal scaledBigDecimal = null;
/*      */         try
/*      */         {
/* 4003 */           scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale);
/*      */         }
/*      */         catch (ArithmeticException ex) {
/*      */           try {
/* 4007 */             scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale, 4);
/*      */           }
/*      */           catch (ArithmeticException arEx)
/*      */           {
/* 4011 */             throw SQLError.createSQLException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 4020 */         setBigDecimal(parameterIndex, scaledBigDecimal);
/* 4021 */       } else if ((parameterAsNum instanceof BigInteger)) {
/* 4022 */         setBigDecimal(parameterIndex, new BigDecimal((BigInteger)parameterAsNum, scale));
/*      */       }
/*      */       else
/*      */       {
/* 4028 */         setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
/*      */       }case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj) throws SQLException {
/* 4039 */     synchronized (checkClosed()) {
/* 4040 */       if (parameterObj == null) {
/* 4041 */         setNull(parameterIndex, 1111);
/*      */       }
/* 4043 */       else if ((parameterObj instanceof Byte))
/* 4044 */         setInt(parameterIndex, ((Byte)parameterObj).intValue());
/* 4045 */       else if ((parameterObj instanceof String))
/* 4046 */         setString(parameterIndex, (String)parameterObj);
/* 4047 */       else if ((parameterObj instanceof BigDecimal))
/* 4048 */         setBigDecimal(parameterIndex, (BigDecimal)parameterObj);
/* 4049 */       else if ((parameterObj instanceof Short))
/* 4050 */         setShort(parameterIndex, ((Short)parameterObj).shortValue());
/* 4051 */       else if ((parameterObj instanceof Integer))
/* 4052 */         setInt(parameterIndex, ((Integer)parameterObj).intValue());
/* 4053 */       else if ((parameterObj instanceof Long))
/* 4054 */         setLong(parameterIndex, ((Long)parameterObj).longValue());
/* 4055 */       else if ((parameterObj instanceof Float))
/* 4056 */         setFloat(parameterIndex, ((Float)parameterObj).floatValue());
/* 4057 */       else if ((parameterObj instanceof Double))
/* 4058 */         setDouble(parameterIndex, ((Double)parameterObj).doubleValue());
/* 4059 */       else if ((parameterObj instanceof byte[]))
/* 4060 */         setBytes(parameterIndex, (byte[])(byte[])parameterObj);
/* 4061 */       else if ((parameterObj instanceof java.sql.Date))
/* 4062 */         setDate(parameterIndex, (java.sql.Date)parameterObj);
/* 4063 */       else if ((parameterObj instanceof Time))
/* 4064 */         setTime(parameterIndex, (Time)parameterObj);
/* 4065 */       else if ((parameterObj instanceof Timestamp))
/* 4066 */         setTimestamp(parameterIndex, (Timestamp)parameterObj);
/* 4067 */       else if ((parameterObj instanceof Boolean)) {
/* 4068 */         setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */       }
/* 4070 */       else if ((parameterObj instanceof InputStream))
/* 4071 */         setBinaryStream(parameterIndex, (InputStream)parameterObj, -1);
/* 4072 */       else if ((parameterObj instanceof Blob))
/* 4073 */         setBlob(parameterIndex, (Blob)parameterObj);
/* 4074 */       else if ((parameterObj instanceof Clob))
/* 4075 */         setClob(parameterIndex, (Clob)parameterObj);
/* 4076 */       else if ((this.connection.getTreatUtilDateAsTimestamp()) && ((parameterObj instanceof java.util.Date)))
/*      */       {
/* 4078 */         setTimestamp(parameterIndex, new Timestamp(((java.util.Date)parameterObj).getTime()));
/*      */       }
/* 4080 */       else if ((parameterObj instanceof BigInteger))
/* 4081 */         setString(parameterIndex, parameterObj.toString());
/*      */       else
/* 4083 */         setSerializableObject(parameterIndex, parameterObj);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 4105 */     if (!(parameterObj instanceof BigDecimal))
/* 4106 */       setObject(parameterIndex, parameterObj, targetSqlType, 0);
/*      */     else
/* 4108 */       setObject(parameterIndex, parameterObj, targetSqlType, ((BigDecimal)parameterObj).scale());
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 4144 */     synchronized (checkClosed()) {
/* 4145 */       if (parameterObj == null)
/* 4146 */         setNull(parameterIndex, 1111);
/*      */       else
/*      */         try {
/* 4149 */           switch (targetSqlType)
/*      */           {
/*      */           case 16:
/* 4169 */             if ((parameterObj instanceof Boolean)) {
/* 4170 */               setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */             }
/* 4173 */             else if ((parameterObj instanceof String)) {
/* 4174 */               setBoolean(parameterIndex, ("true".equalsIgnoreCase((String)parameterObj)) || (!"0".equalsIgnoreCase((String)parameterObj)));
/*      */             }
/* 4178 */             else if ((parameterObj instanceof Number)) {
/* 4179 */               int intValue = ((Number)parameterObj).intValue();
/*      */ 
/* 4181 */               setBoolean(parameterIndex, intValue != 0);
/*      */             }
/*      */             else
/*      */             {
/* 4185 */               throw SQLError.createSQLException("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible.", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           case -7:
/*      */           case -6:
/*      */           case -5:
/*      */           case 2:
/*      */           case 3:
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/* 4201 */             setNumericObject(parameterIndex, parameterObj, targetSqlType, scale);
/*      */ 
/* 4203 */             break;
/*      */           case -1:
/*      */           case 1:
/*      */           case 12:
/* 4208 */             if ((parameterObj instanceof BigDecimal)) {
/* 4209 */               setString(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString((BigDecimal)parameterObj)));
/*      */             }
/*      */             else
/*      */             {
/* 4215 */               setString(parameterIndex, parameterObj.toString());
/*      */             }
/*      */ 
/* 4218 */             break;
/*      */           case 2005:
/* 4222 */             if ((parameterObj instanceof Clob))
/* 4223 */               setClob(parameterIndex, (Clob)parameterObj);
/*      */             else {
/* 4225 */               setString(parameterIndex, parameterObj.toString());
/*      */             }
/*      */ 
/* 4228 */             break;
/*      */           case -4:
/*      */           case -3:
/*      */           case -2:
/*      */           case 2004:
/* 4235 */             if ((parameterObj instanceof byte[]))
/* 4236 */               setBytes(parameterIndex, (byte[])(byte[])parameterObj);
/* 4237 */             else if ((parameterObj instanceof Blob))
/* 4238 */               setBlob(parameterIndex, (Blob)parameterObj);
/*      */             else {
/* 4240 */               setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */             }
/*      */ 
/* 4247 */             break;
/*      */           case 91:
/*      */           case 93:
/*      */             java.util.Date parameterAsDate;
/*      */             java.util.Date parameterAsDate;
/* 4254 */             if ((parameterObj instanceof String)) {
/* 4255 */               ParsePosition pp = new ParsePosition(0);
/* 4256 */               DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, false), Locale.US);
/*      */ 
/* 4258 */               parameterAsDate = sdf.parse((String)parameterObj, pp);
/*      */             } else {
/* 4260 */               parameterAsDate = (java.util.Date)parameterObj;
/*      */             }
/*      */ 
/* 4263 */             switch (targetSqlType)
/*      */             {
/*      */             case 91:
/* 4266 */               if ((parameterAsDate instanceof java.sql.Date)) {
/* 4267 */                 setDate(parameterIndex, (java.sql.Date)parameterAsDate);
/*      */               }
/*      */               else {
/* 4270 */                 setDate(parameterIndex, new java.sql.Date(parameterAsDate.getTime()));
/*      */               }
/*      */ 
/* 4274 */               break;
/*      */             case 93:
/* 4278 */               if ((parameterAsDate instanceof Timestamp)) {
/* 4279 */                 setTimestamp(parameterIndex, (Timestamp)parameterAsDate);
/*      */               }
/*      */               else {
/* 4282 */                 setTimestamp(parameterIndex, new Timestamp(parameterAsDate.getTime()));
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 4290 */             break;
/*      */           case 92:
/* 4294 */             if ((parameterObj instanceof String)) {
/* 4295 */               DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, true), Locale.US);
/*      */ 
/* 4297 */               setTime(parameterIndex, new Time(sdf.parse((String)parameterObj).getTime()));
/*      */             }
/* 4299 */             else if ((parameterObj instanceof Timestamp)) {
/* 4300 */               Timestamp xT = (Timestamp)parameterObj;
/* 4301 */               setTime(parameterIndex, new Time(xT.getTime()));
/*      */             } else {
/* 4303 */               setTime(parameterIndex, (Time)parameterObj);
/*      */             }
/*      */ 
/* 4306 */             break;
/*      */           case 1111:
/* 4309 */             setSerializableObject(parameterIndex, parameterObj);
/*      */ 
/* 4311 */             break;
/*      */           default:
/* 4314 */             throw SQLError.createSQLException(Messages.getString("PreparedStatement.16"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/* 4319 */           if ((ex instanceof SQLException)) {
/* 4320 */             throw ((SQLException)ex);
/*      */           }
/*      */ 
/* 4323 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex.getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), "S1000", getExceptionInterceptor());
/*      */ 
/* 4331 */           sqlEx.initCause(ex);
/*      */ 
/* 4333 */           throw sqlEx;
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet)
/*      */     throws SQLException
/*      */   {
/* 4342 */     BatchParams paramArg = (BatchParams)paramSet;
/*      */ 
/* 4344 */     boolean[] isNullBatch = paramArg.isNull;
/* 4345 */     boolean[] isStreamBatch = paramArg.isStream;
/*      */ 
/* 4347 */     for (int j = 0; j < isNullBatch.length; j++) {
/* 4348 */       if (isNullBatch[j] != 0) {
/* 4349 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 4351 */       else if (isStreamBatch[j] != 0) {
/* 4352 */         batchedStatement.setBinaryStream(batchedParamIndex++, paramArg.parameterStreams[j], paramArg.streamLengths[j]);
/*      */       }
/*      */       else
/*      */       {
/* 4356 */         ((PreparedStatement)batchedStatement).setBytesNoEscapeNoQuotes(batchedParamIndex++, paramArg.parameterStrings[j]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4363 */     return batchedParamIndex;
/*      */   }
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 4380 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   private final void setSerializableObject(int parameterIndex, Object parameterObj)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4399 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/* 4400 */       ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
/* 4401 */       objectOut.writeObject(parameterObj);
/* 4402 */       objectOut.flush();
/* 4403 */       objectOut.close();
/* 4404 */       bytesOut.flush();
/* 4405 */       bytesOut.close();
/*      */ 
/* 4407 */       byte[] buf = bytesOut.toByteArray();
/* 4408 */       ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
/* 4409 */       setBinaryStream(parameterIndex, bytesIn, buf.length);
/* 4410 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -2;
/*      */     } catch (Exception ex) {
/* 4412 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.54") + ex.getClass().getName(), "S1009", getExceptionInterceptor());
/*      */ 
/* 4415 */       sqlEx.initCause(ex);
/*      */ 
/* 4417 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 4434 */     setInternal(parameterIndex, String.valueOf(x));
/*      */ 
/* 4436 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 5;
/*      */   }
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 4453 */     synchronized (checkClosed())
/*      */     {
/* 4455 */       if (x == null) {
/* 4456 */         setNull(parameterIndex, 1);
/*      */       } else {
/* 4458 */         checkClosed();
/*      */ 
/* 4460 */         int stringLength = x.length();
/*      */ 
/* 4462 */         if (this.connection.isNoBackslashEscapesSet())
/*      */         {
/* 4465 */           boolean needsHexEscape = isEscapeNeededForString(x, stringLength);
/*      */ 
/* 4468 */           if (!needsHexEscape) {
/* 4469 */             byte[] parameterAsBytes = null;
/*      */ 
/* 4471 */             StringBuffer quotedString = new StringBuffer(x.length() + 2);
/* 4472 */             quotedString.append('\'');
/* 4473 */             quotedString.append(x);
/* 4474 */             quotedString.append('\'');
/*      */ 
/* 4476 */             if (!this.isLoadDataQuery) {
/* 4477 */               parameterAsBytes = StringUtils.getBytes(quotedString.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */             }
/*      */             else
/*      */             {
/* 4483 */               parameterAsBytes = StringUtils.getBytes(quotedString.toString());
/*      */             }
/*      */ 
/* 4486 */             setInternal(parameterIndex, parameterAsBytes);
/*      */           } else {
/* 4488 */             byte[] parameterAsBytes = null;
/*      */ 
/* 4490 */             if (!this.isLoadDataQuery) {
/* 4491 */               parameterAsBytes = StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */             }
/*      */             else
/*      */             {
/* 4497 */               parameterAsBytes = StringUtils.getBytes(x);
/*      */             }
/*      */ 
/* 4500 */             setBytes(parameterIndex, parameterAsBytes);
/*      */           }
/*      */ 
/* 4503 */           return;
/*      */         }
/*      */ 
/* 4506 */         String parameterAsString = x;
/* 4507 */         boolean needsQuoted = true;
/*      */ 
/* 4509 */         if ((this.isLoadDataQuery) || (isEscapeNeededForString(x, stringLength))) {
/* 4510 */           needsQuoted = false;
/*      */ 
/* 4512 */           StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D));
/*      */ 
/* 4514 */           buf.append('\'');
/*      */ 
/* 4523 */           for (int i = 0; i < stringLength; i++) {
/* 4524 */             char c = x.charAt(i);
/*      */ 
/* 4526 */             switch (c) {
/*      */             case '\000':
/* 4528 */               buf.append('\\');
/* 4529 */               buf.append('0');
/*      */ 
/* 4531 */               break;
/*      */             case '\n':
/* 4534 */               buf.append('\\');
/* 4535 */               buf.append('n');
/*      */ 
/* 4537 */               break;
/*      */             case '\r':
/* 4540 */               buf.append('\\');
/* 4541 */               buf.append('r');
/*      */ 
/* 4543 */               break;
/*      */             case '\\':
/* 4546 */               buf.append('\\');
/* 4547 */               buf.append('\\');
/*      */ 
/* 4549 */               break;
/*      */             case '\'':
/* 4552 */               buf.append('\\');
/* 4553 */               buf.append('\'');
/*      */ 
/* 4555 */               break;
/*      */             case '"':
/* 4558 */               if (this.usingAnsiMode) {
/* 4559 */                 buf.append('\\');
/*      */               }
/*      */ 
/* 4562 */               buf.append('"');
/*      */ 
/* 4564 */               break;
/*      */             case '\032':
/* 4567 */               buf.append('\\');
/* 4568 */               buf.append('Z');
/*      */ 
/* 4570 */               break;
/*      */             case '¥':
/*      */             case '₩':
/* 4575 */               if (this.charsetEncoder == null) break;
/* 4576 */               CharBuffer cbuf = CharBuffer.allocate(1);
/* 4577 */               ByteBuffer bbuf = ByteBuffer.allocate(1);
/* 4578 */               cbuf.put(c);
/* 4579 */               cbuf.position(0);
/* 4580 */               this.charsetEncoder.encode(cbuf, bbuf, true);
/* 4581 */               if (bbuf.get(0) != 92) break;
/* 4582 */               buf.append('\\');
/*      */             }
/*      */ 
/* 4588 */             buf.append(c);
/*      */           }
/*      */ 
/* 4592 */           buf.append('\'');
/*      */ 
/* 4594 */           parameterAsString = buf.toString();
/*      */         }
/*      */ 
/* 4597 */         byte[] parameterAsBytes = null;
/*      */ 
/* 4599 */         if (!this.isLoadDataQuery) {
/* 4600 */           if (needsQuoted) {
/* 4601 */             parameterAsBytes = StringUtils.getBytesWrapped(parameterAsString, '\'', '\'', this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           }
/*      */           else
/*      */           {
/* 4606 */             parameterAsBytes = StringUtils.getBytes(parameterAsString, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 4613 */           parameterAsBytes = StringUtils.getBytes(parameterAsString);
/*      */         }
/*      */ 
/* 4616 */         setInternal(parameterIndex, parameterAsBytes);
/*      */ 
/* 4618 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 12;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isEscapeNeededForString(String x, int stringLength) {
/* 4624 */     boolean needsHexEscape = false;
/*      */ 
/* 4626 */     for (int i = 0; i < stringLength; i++) {
/* 4627 */       char c = x.charAt(i);
/*      */ 
/* 4629 */       switch (c)
/*      */       {
/*      */       case '\000':
/* 4632 */         needsHexEscape = true;
/* 4633 */         break;
/*      */       case '\n':
/* 4636 */         needsHexEscape = true;
/*      */ 
/* 4638 */         break;
/*      */       case '\r':
/* 4641 */         needsHexEscape = true;
/* 4642 */         break;
/*      */       case '\\':
/* 4645 */         needsHexEscape = true;
/*      */ 
/* 4647 */         break;
/*      */       case '\'':
/* 4650 */         needsHexEscape = true;
/*      */ 
/* 4652 */         break;
/*      */       case '"':
/* 4655 */         needsHexEscape = true;
/*      */ 
/* 4657 */         break;
/*      */       case '\032':
/* 4660 */         needsHexEscape = true;
/*      */       }
/*      */ 
/* 4664 */       if (needsHexEscape) {
/*      */         break;
/*      */       }
/*      */     }
/* 4668 */     return needsHexEscape;
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 4687 */     setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 4704 */     setTimeInternal(parameterIndex, x, null, Util.getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4725 */     synchronized (checkClosed()) {
/* 4726 */       if (x == null) {
/* 4727 */         setNull(parameterIndex, 92);
/*      */       } else {
/* 4729 */         checkClosed();
/*      */ 
/* 4731 */         if (!this.useLegacyDatetimeCode) {
/* 4732 */           newSetTimeInternal(parameterIndex, x, targetCalendar);
/*      */         } else {
/* 4734 */           Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/* 4736 */           synchronized (sessionCalendar) {
/* 4737 */             x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */           }
/*      */ 
/* 4744 */           setInternal(parameterIndex, "'" + x.toString() + "'");
/*      */         }
/*      */ 
/* 4747 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 92;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 4768 */     setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 4785 */     setTimestampInternal(parameterIndex, x, null, Util.getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4805 */     synchronized (checkClosed()) {
/* 4806 */       if (x == null) {
/* 4807 */         setNull(parameterIndex, 93);
/*      */       } else {
/* 4809 */         checkClosed();
/*      */ 
/* 4811 */         if (!this.useLegacyDatetimeCode) {
/* 4812 */           newSetTimestampInternal(parameterIndex, x, targetCalendar);
/*      */         } else {
/* 4814 */           Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 4818 */           synchronized (sessionCalendar) {
/* 4819 */             x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */           }
/*      */ 
/* 4826 */           if (this.connection.getUseSSPSCompatibleTimezoneShift())
/* 4827 */             doSSPSCompatibleTimezoneShift(parameterIndex, x, sessionCalendar);
/*      */           else {
/* 4829 */             synchronized (this) {
/* 4830 */               if (this.tsdf == null) {
/* 4831 */                 this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*      */               }
/*      */ 
/* 4834 */               StringBuffer buf = new StringBuffer();
/* 4835 */               buf.append(this.tsdf.format(x));
/*      */ 
/* 4837 */               if (this.serverSupportsFracSecs) {
/* 4838 */                 int nanos = x.getNanos();
/*      */ 
/* 4840 */                 if (nanos != 0) {
/* 4841 */                   buf.append('.');
/* 4842 */                   buf.append(TimeUtil.formatNanos(nanos, this.serverSupportsFracSecs));
/*      */                 }
/*      */               }
/*      */ 
/* 4846 */               buf.append('\'');
/*      */ 
/* 4848 */               setInternal(parameterIndex, buf.toString());
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 4854 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 93;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void newSetTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar) throws SQLException
/*      */   {
/* 4861 */     synchronized (checkClosed()) {
/* 4862 */       if (this.tsdf == null) {
/* 4863 */         this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*      */       }
/*      */ 
/* 4866 */       String timestampString = null;
/*      */ 
/* 4868 */       if (targetCalendar != null) {
/* 4869 */         targetCalendar.setTime(x);
/* 4870 */         this.tsdf.setTimeZone(targetCalendar.getTimeZone());
/*      */ 
/* 4872 */         timestampString = this.tsdf.format(x);
/*      */       } else {
/* 4874 */         this.tsdf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4875 */         timestampString = this.tsdf.format(x);
/*      */       }
/*      */ 
/* 4878 */       StringBuffer buf = new StringBuffer();
/* 4879 */       buf.append(timestampString);
/* 4880 */       buf.append('.');
/* 4881 */       buf.append(TimeUtil.formatNanos(x.getNanos(), this.serverSupportsFracSecs));
/* 4882 */       buf.append('\'');
/*      */ 
/* 4884 */       setInternal(parameterIndex, buf.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void newSetTimeInternal(int parameterIndex, Time x, Calendar targetCalendar) throws SQLException
/*      */   {
/* 4890 */     synchronized (checkClosed()) {
/* 4891 */       if (this.tdf == null) {
/* 4892 */         this.tdf = new SimpleDateFormat("''HH:mm:ss''", Locale.US);
/*      */       }
/*      */ 
/* 4896 */       String timeString = null;
/*      */ 
/* 4898 */       if (targetCalendar != null) {
/* 4899 */         targetCalendar.setTime(x);
/* 4900 */         this.tdf.setTimeZone(targetCalendar.getTimeZone());
/*      */ 
/* 4902 */         timeString = this.tdf.format(x);
/*      */       } else {
/* 4904 */         this.tdf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4905 */         timeString = this.tdf.format(x);
/*      */       }
/*      */ 
/* 4908 */       setInternal(parameterIndex, timeString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void newSetDateInternal(int parameterIndex, java.sql.Date x, Calendar targetCalendar) throws SQLException
/*      */   {
/* 4914 */     synchronized (checkClosed()) {
/* 4915 */       if (this.ddf == null) {
/* 4916 */         this.ddf = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */       }
/*      */ 
/* 4919 */       String timeString = null;
/*      */ 
/* 4921 */       if (targetCalendar != null) {
/* 4922 */         targetCalendar.setTime(x);
/* 4923 */         this.ddf.setTimeZone(targetCalendar.getTimeZone());
/*      */ 
/* 4925 */         timeString = this.ddf.format(x);
/*      */       } else {
/* 4927 */         this.ddf.setTimeZone(this.connection.getServerTimezoneTZ());
/* 4928 */         timeString = this.ddf.format(x);
/*      */       }
/*      */ 
/* 4931 */       setInternal(parameterIndex, timeString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doSSPSCompatibleTimezoneShift(int parameterIndex, Timestamp x, Calendar sessionCalendar) throws SQLException {
/* 4936 */     synchronized (checkClosed()) {
/* 4937 */       Calendar sessionCalendar2 = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 4942 */       synchronized (sessionCalendar2) {
/* 4943 */         java.util.Date oldTime = sessionCalendar2.getTime();
/*      */         try
/*      */         {
/* 4946 */           sessionCalendar2.setTime(x);
/*      */ 
/* 4948 */           int year = sessionCalendar2.get(1);
/* 4949 */           int month = sessionCalendar2.get(2) + 1;
/* 4950 */           int date = sessionCalendar2.get(5);
/*      */ 
/* 4952 */           int hour = sessionCalendar2.get(11);
/* 4953 */           int minute = sessionCalendar2.get(12);
/* 4954 */           int seconds = sessionCalendar2.get(13);
/*      */ 
/* 4956 */           StringBuffer tsBuf = new StringBuffer();
/*      */ 
/* 4958 */           tsBuf.append('\'');
/* 4959 */           tsBuf.append(year);
/*      */ 
/* 4961 */           tsBuf.append("-");
/*      */ 
/* 4963 */           if (month < 10) {
/* 4964 */             tsBuf.append('0');
/*      */           }
/*      */ 
/* 4967 */           tsBuf.append(month);
/*      */ 
/* 4969 */           tsBuf.append('-');
/*      */ 
/* 4971 */           if (date < 10) {
/* 4972 */             tsBuf.append('0');
/*      */           }
/*      */ 
/* 4975 */           tsBuf.append(date);
/*      */ 
/* 4977 */           tsBuf.append(' ');
/*      */ 
/* 4979 */           if (hour < 10) {
/* 4980 */             tsBuf.append('0');
/*      */           }
/*      */ 
/* 4983 */           tsBuf.append(hour);
/*      */ 
/* 4985 */           tsBuf.append(':');
/*      */ 
/* 4987 */           if (minute < 10) {
/* 4988 */             tsBuf.append('0');
/*      */           }
/*      */ 
/* 4991 */           tsBuf.append(minute);
/*      */ 
/* 4993 */           tsBuf.append(':');
/*      */ 
/* 4995 */           if (seconds < 10) {
/* 4996 */             tsBuf.append('0');
/*      */           }
/*      */ 
/* 4999 */           tsBuf.append(seconds);
/*      */ 
/* 5001 */           tsBuf.append('.');
/* 5002 */           tsBuf.append(TimeUtil.formatNanos(x.getNanos(), this.serverSupportsFracSecs));
/* 5003 */           tsBuf.append('\'');
/*      */ 
/* 5005 */           setInternal(parameterIndex, tsBuf.toString());
/*      */         }
/*      */         finally {
/* 5008 */           sessionCalendar.setTime(oldTime);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 5040 */     if (x == null) {
/* 5041 */       setNull(parameterIndex, 12);
/*      */     } else {
/* 5043 */       setBinaryStream(parameterIndex, x, length);
/*      */ 
/* 5045 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setURL(int parameterIndex, URL arg)
/*      */     throws SQLException
/*      */   {
/* 5053 */     if (arg != null) {
/* 5054 */       setString(parameterIndex, arg.toString());
/*      */ 
/* 5056 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 70;
/*      */     } else {
/* 5058 */       setNull(parameterIndex, 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void streamToBytes(Buffer packet, InputStream in, boolean escape, int streamLength, boolean useLength)
/*      */     throws SQLException
/*      */   {
/* 5065 */     synchronized (checkClosed()) {
/*      */       try {
/* 5067 */         if (this.streamConvertBuf == null) {
/* 5068 */           this.streamConvertBuf = new byte[4096];
/*      */         }
/*      */ 
/* 5071 */         String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 5073 */         boolean hexEscape = false;
/*      */         try
/*      */         {
/* 5076 */           if ((this.connection.isNoBackslashEscapesSet()) || ((this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding)) && (!this.connection.parserKnowsUnicode())))
/*      */           {
/* 5081 */             hexEscape = true;
/*      */           }
/*      */         } catch (RuntimeException ex) {
/* 5084 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 5085 */           sqlEx.initCause(ex);
/* 5086 */           throw sqlEx;
/*      */         }
/*      */ 
/* 5089 */         if (streamLength == -1) {
/* 5090 */           useLength = false;
/*      */         }
/*      */ 
/* 5093 */         int bc = -1;
/*      */ 
/* 5095 */         if (useLength)
/* 5096 */           bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */         else {
/* 5098 */           bc = readblock(in, this.streamConvertBuf);
/*      */         }
/*      */ 
/* 5101 */         int lengthLeftToRead = streamLength - bc;
/*      */ 
/* 5103 */         if (hexEscape)
/* 5104 */           packet.writeStringNoNull("x");
/* 5105 */         else if (this.connection.getIO().versionMeetsMinimum(4, 1, 0)) {
/* 5106 */           packet.writeStringNoNull("_binary");
/*      */         }
/*      */ 
/* 5109 */         if (escape) {
/* 5110 */           packet.writeByte(39);
/*      */         }
/*      */ 
/* 5113 */         while (bc > 0) {
/* 5114 */           if (hexEscape)
/* 5115 */             hexEscapeBlock(this.streamConvertBuf, packet, bc);
/* 5116 */           else if (escape)
/* 5117 */             escapeblockFast(this.streamConvertBuf, packet, bc);
/*      */           else {
/* 5119 */             packet.writeBytesNoNull(this.streamConvertBuf, 0, bc);
/*      */           }
/*      */ 
/* 5122 */           if (useLength) {
/* 5123 */             bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */ 
/* 5125 */             if (bc > 0) {
/* 5126 */               lengthLeftToRead -= bc; continue;
/*      */             }
/*      */           }
/* 5129 */           bc = readblock(in, this.streamConvertBuf);
/*      */         }
/*      */ 
/* 5133 */         if (escape)
/* 5134 */           packet.writeByte(39);
/*      */       }
/*      */       finally {
/* 5137 */         if (this.connection.getAutoClosePStmtStreams()) {
/*      */           try {
/* 5139 */             in.close();
/*      */           }
/*      */           catch (IOException ioEx)
/*      */           {
/*      */           }
/* 5144 */           in = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final byte[] streamToBytes(InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException
/*      */   {
/* 5152 */     synchronized (checkClosed()) {
/*      */       try {
/* 5154 */         if (this.streamConvertBuf == null) {
/* 5155 */           this.streamConvertBuf = new byte[4096];
/*      */         }
/* 5157 */         if (streamLength == -1) {
/* 5158 */           useLength = false;
/*      */         }
/*      */ 
/* 5161 */         ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/* 5163 */         int bc = -1;
/*      */ 
/* 5165 */         if (useLength)
/* 5166 */           bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */         else {
/* 5168 */           bc = readblock(in, this.streamConvertBuf);
/*      */         }
/*      */ 
/* 5171 */         int lengthLeftToRead = streamLength - bc;
/*      */ 
/* 5173 */         if (escape) {
/* 5174 */           if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 5175 */             bytesOut.write(95);
/* 5176 */             bytesOut.write(98);
/* 5177 */             bytesOut.write(105);
/* 5178 */             bytesOut.write(110);
/* 5179 */             bytesOut.write(97);
/* 5180 */             bytesOut.write(114);
/* 5181 */             bytesOut.write(121);
/*      */           }
/*      */ 
/* 5184 */           bytesOut.write(39);
/*      */         }
/*      */ 
/* 5187 */         while (bc > 0) {
/* 5188 */           if (escape)
/* 5189 */             escapeblockFast(this.streamConvertBuf, bytesOut, bc);
/*      */           else {
/* 5191 */             bytesOut.write(this.streamConvertBuf, 0, bc);
/*      */           }
/*      */ 
/* 5194 */           if (useLength) {
/* 5195 */             bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */ 
/* 5197 */             if (bc > 0) {
/* 5198 */               lengthLeftToRead -= bc; continue;
/*      */             }
/*      */           }
/* 5201 */           bc = readblock(in, this.streamConvertBuf);
/*      */         }
/*      */ 
/* 5205 */         if (escape) {
/* 5206 */           bytesOut.write(39);
/*      */         }
/*      */ 
/* 5209 */         byte[] arrayOfByte = bytesOut.toByteArray();
/*      */ 
/* 5211 */         if (this.connection.getAutoClosePStmtStreams()) {
/*      */           try {
/* 5213 */             in.close();
/*      */           }
/*      */           catch (IOException ioEx)
/*      */           {
/*      */           }
/* 5218 */           in = null; } return arrayOfByte;
/*      */       }
/*      */       finally
/*      */       {
/* 5211 */         if (this.connection.getAutoClosePStmtStreams()) {
/*      */           try {
/* 5213 */             in.close();
/*      */           }
/*      */           catch (IOException ioEx)
/*      */           {
/*      */           }
/* 5218 */           in = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 5230 */     StringBuffer buf = new StringBuffer();
/* 5231 */     buf.append(super.toString());
/* 5232 */     buf.append(": ");
/*      */     try
/*      */     {
/* 5235 */       buf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 5237 */       buf.append("EXCEPTION: " + sqlEx.toString());
/*      */     }
/*      */ 
/* 5240 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   protected int getParameterIndexOffset()
/*      */   {
/* 5252 */     return 0;
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/* 5256 */     setAsciiStream(parameterIndex, x, -1);
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 5260 */     setAsciiStream(parameterIndex, x, (int)length);
/* 5261 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/* 5265 */     setBinaryStream(parameterIndex, x, -1);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 5269 */     setBinaryStream(parameterIndex, x, (int)length);
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/* 5273 */     setBinaryStream(parameterIndex, inputStream);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/* 5277 */     setCharacterStream(parameterIndex, reader, -1);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/* 5281 */     setCharacterStream(parameterIndex, reader, (int)length);
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/* 5286 */     setCharacterStream(parameterIndex, reader);
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 5292 */     setCharacterStream(parameterIndex, reader, length);
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/* 5296 */     setNCharacterStream(parameterIndex, value, -1L);
/*      */   }
/*      */ 
/*      */   public void setNString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 5314 */     synchronized (checkClosed()) {
/* 5315 */       if ((this.charEncoding.equalsIgnoreCase("UTF-8")) || (this.charEncoding.equalsIgnoreCase("utf8")))
/*      */       {
/* 5317 */         setString(parameterIndex, x);
/* 5318 */         return;
/*      */       }
/*      */ 
/* 5322 */       if (x == null) {
/* 5323 */         setNull(parameterIndex, 1);
/*      */       } else {
/* 5325 */         int stringLength = x.length();
/*      */ 
/* 5329 */         StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D + 4.0D));
/* 5330 */         buf.append("_utf8");
/* 5331 */         buf.append('\'');
/*      */ 
/* 5340 */         for (int i = 0; i < stringLength; i++) {
/* 5341 */           char c = x.charAt(i);
/*      */ 
/* 5343 */           switch (c) {
/*      */           case '\000':
/* 5345 */             buf.append('\\');
/* 5346 */             buf.append('0');
/*      */ 
/* 5348 */             break;
/*      */           case '\n':
/* 5351 */             buf.append('\\');
/* 5352 */             buf.append('n');
/*      */ 
/* 5354 */             break;
/*      */           case '\r':
/* 5357 */             buf.append('\\');
/* 5358 */             buf.append('r');
/*      */ 
/* 5360 */             break;
/*      */           case '\\':
/* 5363 */             buf.append('\\');
/* 5364 */             buf.append('\\');
/*      */ 
/* 5366 */             break;
/*      */           case '\'':
/* 5369 */             buf.append('\\');
/* 5370 */             buf.append('\'');
/*      */ 
/* 5372 */             break;
/*      */           case '"':
/* 5375 */             if (this.usingAnsiMode) {
/* 5376 */               buf.append('\\');
/*      */             }
/*      */ 
/* 5379 */             buf.append('"');
/*      */ 
/* 5381 */             break;
/*      */           case '\032':
/* 5384 */             buf.append('\\');
/* 5385 */             buf.append('Z');
/*      */ 
/* 5387 */             break;
/*      */           default:
/* 5390 */             buf.append(c);
/*      */           }
/*      */         }
/*      */ 
/* 5394 */         buf.append('\'');
/*      */ 
/* 5396 */         String parameterAsString = buf.toString();
/*      */ 
/* 5398 */         byte[] parameterAsBytes = null;
/*      */ 
/* 5400 */         if (!this.isLoadDataQuery) {
/* 5401 */           parameterAsBytes = StringUtils.getBytes(parameterAsString, this.connection.getCharsetConverter("UTF-8"), "UTF-8", this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */         }
/*      */         else
/*      */         {
/* 5407 */           parameterAsBytes = StringUtils.getBytes(parameterAsString);
/*      */         }
/*      */ 
/* 5410 */         setInternal(parameterIndex, parameterAsBytes);
/*      */ 
/* 5412 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -9;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 5441 */     synchronized (checkClosed()) {
/*      */       try {
/* 5443 */         if (reader == null) {
/* 5444 */           setNull(parameterIndex, -1);
/*      */         }
/*      */         else {
/* 5447 */           char[] c = null;
/* 5448 */           int len = 0;
/*      */ 
/* 5450 */           boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 5455 */           if ((useLength) && (length != -1L)) {
/* 5456 */             c = new char[(int)length];
/*      */ 
/* 5458 */             int numCharsRead = readFully(reader, c, (int)length);
/*      */ 
/* 5462 */             setNString(parameterIndex, new String(c, 0, numCharsRead));
/*      */           }
/*      */           else {
/* 5465 */             c = new char[4096];
/*      */ 
/* 5467 */             StringBuffer buf = new StringBuffer();
/*      */ 
/* 5469 */             while ((len = reader.read(c)) != -1) {
/* 5470 */               buf.append(c, 0, len);
/*      */             }
/*      */ 
/* 5473 */             setNString(parameterIndex, buf.toString());
/*      */           }
/*      */ 
/* 5476 */           this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2011;
/*      */         }
/*      */       } catch (IOException ioEx) {
/* 5479 */         throw SQLError.createSQLException(ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/* 5486 */     setNCharacterStream(parameterIndex, reader);
/*      */   }
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 5504 */     if (reader == null)
/* 5505 */       setNull(parameterIndex, -1);
/*      */     else
/* 5507 */       setNCharacterStream(parameterIndex, reader, length);
/*      */   }
/*      */ 
/*      */   public ParameterBindings getParameterBindings() throws SQLException
/*      */   {
/* 5512 */     synchronized (checkClosed()) {
/* 5513 */       return new EmulatedPreparedStatementBindings();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getPreparedSql()
/*      */   {
/*      */     try
/*      */     {
/* 5706 */       synchronized (checkClosed()) {
/* 5707 */         if (this.rewrittenBatchSize == 0) {
/* 5708 */           return this.originalSql;
/*      */         }
/*      */         try
/*      */         {
/* 5712 */           return this.parseInfo.getSqlForBatch(this.parseInfo);
/*      */         } catch (UnsupportedEncodingException e) {
/* 5714 */           throw new RuntimeException(e);
/*      */         }
/*      */       }
/*      */     } catch (SQLException e) {
/*      */     }
/* 5718 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   public int getUpdateCount() throws SQLException
/*      */   {
/* 5723 */     int count = super.getUpdateCount();
/*      */ 
/* 5725 */     if ((containsOnDuplicateKeyUpdateInSQL()) && (this.compensateForOnDuplicateKeyUpdate))
/*      */     {
/* 5727 */       if ((count == 2) || (count == 0)) {
/* 5728 */         count = 1;
/*      */       }
/*      */     }
/*      */ 
/* 5732 */     return count;
/*      */   }
/*      */ 
/*      */   protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos)
/*      */   {
/* 5739 */     boolean rewritableOdku = true;
/*      */ 
/* 5741 */     if (isOnDuplicateKeyUpdate) {
/* 5742 */       int updateClausePos = StringUtils.indexOfIgnoreCase(locationOfOnDuplicateKeyUpdate, sql, " UPDATE ");
/*      */ 
/* 5745 */       if (updateClausePos != -1) {
/* 5746 */         rewritableOdku = StringUtils.indexOfIgnoreCaseRespectMarker(updateClausePos, sql, "LAST_INSERT_ID", "\"'`", "\"'`", false) == -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5753 */     return (StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementStartPos)) && (StringUtils.indexOfIgnoreCaseRespectMarker(statementStartPos, sql, "SELECT", "\"'`", "\"'`", false) == -1) && (rewritableOdku);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   96 */     if (Util.isJdbc4()) {
/*      */       try {
/*   98 */         JDBC_4_PSTMT_2_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { MySQLConnection.class, String.class });
/*      */ 
/*  102 */         JDBC_4_PSTMT_3_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { MySQLConnection.class, String.class, String.class });
/*      */ 
/*  107 */         JDBC_4_PSTMT_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4PreparedStatement").getConstructor(new Class[] { MySQLConnection.class, String.class, String.class, ParseInfo.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  113 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  115 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  117 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  120 */       JDBC_4_PSTMT_2_ARG_CTOR = null;
/*  121 */       JDBC_4_PSTMT_3_ARG_CTOR = null;
/*  122 */       JDBC_4_PSTMT_4_ARG_CTOR = null;
/*      */     }
/*      */ 
/*  733 */     HEX_DIGITS = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */   }
/*      */ 
/*      */   class EmulatedPreparedStatementBindings
/*      */     implements ParameterBindings
/*      */   {
/*      */     private ResultSetImpl bindingsAsRs;
/*      */     private boolean[] parameterIsNull;
/*      */ 
/*      */     EmulatedPreparedStatementBindings()
/*      */       throws SQLException
/*      */     {
/* 5523 */       List rows = new ArrayList();
/* 5524 */       this.parameterIsNull = new boolean[PreparedStatement.this.parameterCount];
/* 5525 */       System.arraycopy(PreparedStatement.this.isNull, 0, this.parameterIsNull, 0, PreparedStatement.this.parameterCount);
/*      */ 
/* 5528 */       byte[][] rowData = new byte[PreparedStatement.this.parameterCount][];
/* 5529 */       Field[] typeMetadata = new Field[PreparedStatement.this.parameterCount];
/*      */ 
/* 5531 */       for (int i = 0; i < PreparedStatement.this.parameterCount; i++) {
/* 5532 */         if (PreparedStatement.this.batchCommandIndex == -1)
/* 5533 */           rowData[i] = PreparedStatement.this.getBytesRepresentation(i);
/*      */         else {
/* 5535 */           rowData[i] = PreparedStatement.this.getBytesRepresentationForBatch(i, PreparedStatement.this.batchCommandIndex);
/*      */         }
/* 5537 */         int charsetIndex = 0;
/*      */ 
/* 5539 */         if ((PreparedStatement.this.parameterTypes[i] == -2) || (PreparedStatement.this.parameterTypes[i] == 2004))
/*      */         {
/* 5541 */           charsetIndex = 63;
/*      */         }
/*      */         else try {
/* 5544 */             String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(PreparedStatement.this.connection.getEncoding(), PreparedStatement.this.connection);
/*      */ 
/* 5547 */             charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(mysqlEncodingName);
/*      */           }
/*      */           catch (SQLException ex) {
/* 5550 */             throw ex;
/*      */           } catch (RuntimeException ex) {
/* 5552 */             SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 5553 */             sqlEx.initCause(ex);
/* 5554 */             throw sqlEx;
/*      */           }
/*      */ 
/*      */ 
/* 5558 */         Field parameterMetadata = new Field(null, "parameter_" + (i + 1), charsetIndex, PreparedStatement.this.parameterTypes[i], rowData[i].length);
/*      */ 
/* 5561 */         parameterMetadata.setConnection(PreparedStatement.this.connection);
/* 5562 */         typeMetadata[i] = parameterMetadata;
/*      */       }
/*      */ 
/* 5565 */       rows.add(new ByteArrayRow(rowData, PreparedStatement.this.getExceptionInterceptor()));
/*      */ 
/* 5567 */       this.bindingsAsRs = new ResultSetImpl(PreparedStatement.this.connection.getCatalog(), typeMetadata, new RowDataStatic(rows), PreparedStatement.this.connection, null);
/*      */ 
/* 5569 */       this.bindingsAsRs.next();
/*      */     }
/*      */ 
/*      */     public Array getArray(int parameterIndex) throws SQLException {
/* 5573 */       return this.bindingsAsRs.getArray(parameterIndex);
/*      */     }
/*      */ 
/*      */     public InputStream getAsciiStream(int parameterIndex) throws SQLException
/*      */     {
/* 5578 */       return this.bindingsAsRs.getAsciiStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/* 5582 */       return this.bindingsAsRs.getBigDecimal(parameterIndex);
/*      */     }
/*      */ 
/*      */     public InputStream getBinaryStream(int parameterIndex) throws SQLException
/*      */     {
/* 5587 */       return this.bindingsAsRs.getBinaryStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Blob getBlob(int parameterIndex) throws SQLException {
/* 5591 */       return this.bindingsAsRs.getBlob(parameterIndex);
/*      */     }
/*      */ 
/*      */     public boolean getBoolean(int parameterIndex) throws SQLException {
/* 5595 */       return this.bindingsAsRs.getBoolean(parameterIndex);
/*      */     }
/*      */ 
/*      */     public byte getByte(int parameterIndex) throws SQLException {
/* 5599 */       return this.bindingsAsRs.getByte(parameterIndex);
/*      */     }
/*      */ 
/*      */     public byte[] getBytes(int parameterIndex) throws SQLException {
/* 5603 */       return this.bindingsAsRs.getBytes(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Reader getCharacterStream(int parameterIndex) throws SQLException
/*      */     {
/* 5608 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Clob getClob(int parameterIndex) throws SQLException {
/* 5612 */       return this.bindingsAsRs.getClob(parameterIndex);
/*      */     }
/*      */ 
/*      */     public java.sql.Date getDate(int parameterIndex) throws SQLException {
/* 5616 */       return this.bindingsAsRs.getDate(parameterIndex);
/*      */     }
/*      */ 
/*      */     public double getDouble(int parameterIndex) throws SQLException {
/* 5620 */       return this.bindingsAsRs.getDouble(parameterIndex);
/*      */     }
/*      */ 
/*      */     public float getFloat(int parameterIndex) throws SQLException {
/* 5624 */       return this.bindingsAsRs.getFloat(parameterIndex);
/*      */     }
/*      */ 
/*      */     public int getInt(int parameterIndex) throws SQLException {
/* 5628 */       return this.bindingsAsRs.getInt(parameterIndex);
/*      */     }
/*      */ 
/*      */     public long getLong(int parameterIndex) throws SQLException {
/* 5632 */       return this.bindingsAsRs.getLong(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Reader getNCharacterStream(int parameterIndex) throws SQLException
/*      */     {
/* 5637 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Reader getNClob(int parameterIndex) throws SQLException {
/* 5641 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Object getObject(int parameterIndex) throws SQLException {
/* 5645 */       PreparedStatement.this.checkBounds(parameterIndex, 0);
/*      */ 
/* 5647 */       if (this.parameterIsNull[(parameterIndex - 1)] != 0) {
/* 5648 */         return null;
/*      */       }
/*      */ 
/* 5655 */       switch (PreparedStatement.this.parameterTypes[(parameterIndex - 1)]) {
/*      */       case -6:
/* 5657 */         return Byte.valueOf(getByte(parameterIndex));
/*      */       case 5:
/* 5659 */         return Short.valueOf(getShort(parameterIndex));
/*      */       case 4:
/* 5661 */         return Integer.valueOf(getInt(parameterIndex));
/*      */       case -5:
/* 5663 */         return Long.valueOf(getLong(parameterIndex));
/*      */       case 6:
/* 5665 */         return Float.valueOf(getFloat(parameterIndex));
/*      */       case 8:
/* 5667 */         return Double.valueOf(getDouble(parameterIndex));
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/* 5669 */       case 7: } return this.bindingsAsRs.getObject(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Ref getRef(int parameterIndex) throws SQLException
/*      */     {
/* 5674 */       return this.bindingsAsRs.getRef(parameterIndex);
/*      */     }
/*      */ 
/*      */     public short getShort(int parameterIndex) throws SQLException {
/* 5678 */       return this.bindingsAsRs.getShort(parameterIndex);
/*      */     }
/*      */ 
/*      */     public String getString(int parameterIndex) throws SQLException {
/* 5682 */       return this.bindingsAsRs.getString(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Time getTime(int parameterIndex) throws SQLException {
/* 5686 */       return this.bindingsAsRs.getTime(parameterIndex);
/*      */     }
/*      */ 
/*      */     public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/* 5690 */       return this.bindingsAsRs.getTimestamp(parameterIndex);
/*      */     }
/*      */ 
/*      */     public URL getURL(int parameterIndex) throws SQLException {
/* 5694 */       return this.bindingsAsRs.getURL(parameterIndex);
/*      */     }
/*      */ 
/*      */     public boolean isNull(int parameterIndex) throws SQLException {
/* 5698 */       PreparedStatement.this.checkBounds(parameterIndex, 0);
/*      */ 
/* 5700 */       return this.parameterIsNull[(parameterIndex - 1)];
/*      */     }
/*      */   }
/*      */ 
/*      */   class AppendingBatchVisitor
/*      */     implements PreparedStatement.BatchVisitor
/*      */   {
/*  686 */     LinkedList<byte[]> statementComponents = new LinkedList();
/*      */ 
/*      */     AppendingBatchVisitor() {  }
/*      */ 
/*  689 */     public PreparedStatement.BatchVisitor append(byte[] values) { this.statementComponents.addLast(values);
/*      */ 
/*  691 */       return this;
/*      */     }
/*      */ 
/*      */     public PreparedStatement.BatchVisitor increment()
/*      */     {
/*  696 */       return this;
/*      */     }
/*      */ 
/*      */     public PreparedStatement.BatchVisitor decrement() {
/*  700 */       this.statementComponents.removeLast();
/*      */ 
/*  702 */       return this;
/*      */     }
/*      */ 
/*      */     public PreparedStatement.BatchVisitor merge(byte[] front, byte[] back) {
/*  706 */       int mergedLength = front.length + back.length;
/*  707 */       byte[] merged = new byte[mergedLength];
/*  708 */       System.arraycopy(front, 0, merged, 0, front.length);
/*  709 */       System.arraycopy(back, 0, merged, front.length, back.length);
/*  710 */       this.statementComponents.addLast(merged);
/*  711 */       return this;
/*      */     }
/*      */ 
/*      */     public byte[][] getStaticSqlStrings() {
/*  715 */       byte[][] asBytes = new byte[this.statementComponents.size()][];
/*  716 */       this.statementComponents.toArray(asBytes);
/*      */ 
/*  718 */       return asBytes;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  722 */       StringBuffer buf = new StringBuffer();
/*  723 */       Iterator iter = this.statementComponents.iterator();
/*  724 */       while (iter.hasNext()) {
/*  725 */         buf.append(StringUtils.toString((byte[])iter.next()));
/*      */       }
/*      */ 
/*  728 */       return buf.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract interface BatchVisitor
/*      */   {
/*      */     public abstract BatchVisitor increment();
/*      */ 
/*      */     public abstract BatchVisitor decrement();
/*      */ 
/*      */     public abstract BatchVisitor append(byte[] paramArrayOfByte);
/*      */ 
/*      */     public abstract BatchVisitor merge(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */   }
/*      */ 
/*      */   class ParseInfo
/*      */   {
/*  172 */     char firstStmtChar = '\000';
/*      */ 
/*  174 */     boolean foundLimitClause = false;
/*      */ 
/*  176 */     boolean foundLoadData = false;
/*      */ 
/*  178 */     long lastUsed = 0L;
/*      */ 
/*  180 */     int statementLength = 0;
/*      */ 
/*  182 */     int statementStartPos = 0;
/*      */ 
/*  184 */     boolean canRewriteAsMultiValueInsert = false;
/*      */ 
/*  186 */     byte[][] staticSql = (byte[][])null;
/*      */ 
/*  188 */     boolean isOnDuplicateKeyUpdate = false;
/*      */ 
/*  190 */     int locationOfOnDuplicateKeyUpdate = -1;
/*      */     String valuesClause;
/*  194 */     boolean parametersInDuplicateKeyClause = false;
/*      */     private ParseInfo batchHead;
/*      */     private ParseInfo batchValues;
/*      */     private ParseInfo batchODKUClause;
/*      */ 
/*      */     ParseInfo(String sql, MySQLConnection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  205 */       this(sql, conn, dbmd, encoding, converter, true);
/*      */     }
/*      */ 
/*      */     public ParseInfo(String sql, MySQLConnection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter, boolean buildRewriteInfo) throws SQLException
/*      */     {
/*      */       try
/*      */       {
/*  212 */         if (sql == null) {
/*  213 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.61"), "S1009", PreparedStatement.this.getExceptionInterceptor());
/*      */         }
/*      */ 
/*  218 */         this.locationOfOnDuplicateKeyUpdate = PreparedStatement.this.getOnDuplicateKeyLocation(sql);
/*  219 */         this.isOnDuplicateKeyUpdate = (this.locationOfOnDuplicateKeyUpdate != -1);
/*      */ 
/*  221 */         this.lastUsed = System.currentTimeMillis();
/*      */ 
/*  223 */         quotedIdentifierString = dbmd.getIdentifierQuoteString();
/*      */ 
/*  225 */         char quotedIdentifierChar = '\000';
/*      */ 
/*  227 */         if ((quotedIdentifierString != null) && (!quotedIdentifierString.equals(" ")) && (quotedIdentifierString.length() > 0))
/*      */         {
/*  230 */           quotedIdentifierChar = quotedIdentifierString.charAt(0);
/*      */         }
/*      */ 
/*  233 */         this.statementLength = sql.length();
/*      */ 
/*  235 */         ArrayList endpointList = new ArrayList();
/*  236 */         boolean inQuotes = false;
/*  237 */         char quoteChar = '\000';
/*  238 */         boolean inQuotedId = false;
/*  239 */         int lastParmEnd = 0;
/*      */ 
/*  242 */         int stopLookingForLimitClause = this.statementLength - 5;
/*      */ 
/*  244 */         this.foundLimitClause = false;
/*      */ 
/*  246 */         boolean noBackslashEscapes = PreparedStatement.this.connection.isNoBackslashEscapesSet();
/*      */ 
/*  252 */         this.statementStartPos = PreparedStatement.this.findStartOfStatement(sql);
/*      */ 
/*  254 */         for (int i = this.statementStartPos; i < this.statementLength; i++) {
/*  255 */           char c = sql.charAt(i);
/*      */ 
/*  257 */           if ((this.firstStmtChar == 0) && (Character.isLetter(c)))
/*      */           {
/*  260 */             this.firstStmtChar = Character.toUpperCase(c);
/*      */           }
/*      */ 
/*  263 */           if ((!noBackslashEscapes) && (c == '\\') && (i < this.statementLength - 1))
/*      */           {
/*  265 */             i++;
/*      */           }
/*      */           else
/*      */           {
/*  271 */             if ((!inQuotes) && (quotedIdentifierChar != 0) && (c == quotedIdentifierChar))
/*      */             {
/*  273 */               inQuotedId = !inQuotedId;
/*  274 */             } else if (!inQuotedId)
/*      */             {
/*  277 */               if (inQuotes) {
/*  278 */                 if (((c == '\'') || (c == '"')) && (c == quoteChar)) {
/*  279 */                   if ((i < this.statementLength - 1) && (sql.charAt(i + 1) == quoteChar)) {
/*  280 */                     i++;
/*  281 */                     continue;
/*      */                   }
/*      */ 
/*  284 */                   inQuotes = !inQuotes;
/*  285 */                   quoteChar = '\000';
/*  286 */                 } else if (((c == '\'') || (c == '"')) && (c == quoteChar)) {
/*  287 */                   inQuotes = !inQuotes;
/*  288 */                   quoteChar = '\000';
/*      */                 }
/*      */               } else {
/*  291 */                 if ((c == '#') || ((c == '-') && (i + 1 < this.statementLength) && (sql.charAt(i + 1) == '-')))
/*      */                 {
/*  296 */                   int endOfStmt = this.statementLength - 1;
/*      */ 
/*  298 */                   for (; i < endOfStmt; i++) {
/*  299 */                     c = sql.charAt(i);
/*      */ 
/*  301 */                     if ((c == '\r') || (c == '\n'))
/*      */                     {
/*      */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*  307 */                 if ((c == '/') && (i + 1 < this.statementLength))
/*      */                 {
/*  309 */                   char cNext = sql.charAt(i + 1);
/*      */ 
/*  311 */                   if (cNext == '*') {
/*  312 */                     i += 2;
/*      */ 
/*  314 */                     for (int j = i; j < this.statementLength; j++) {
/*  315 */                       i++;
/*  316 */                       cNext = sql.charAt(j);
/*      */ 
/*  318 */                       if ((cNext != '*') || (j + 1 >= this.statementLength) || 
/*  319 */                         (sql.charAt(j + 1) != '/')) continue;
/*  320 */                       i++;
/*      */ 
/*  322 */                       if (i >= this.statementLength) break;
/*  323 */                       c = sql.charAt(i); break;
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/*      */                 }
/*  331 */                 else if ((c == '\'') || (c == '"')) {
/*  332 */                   inQuotes = true;
/*  333 */                   quoteChar = c;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  338 */             if ((c == '?') && (!inQuotes) && (!inQuotedId)) {
/*  339 */               endpointList.add(new int[] { lastParmEnd, i });
/*  340 */               lastParmEnd = i + 1;
/*      */ 
/*  342 */               if ((this.isOnDuplicateKeyUpdate) && (i > this.locationOfOnDuplicateKeyUpdate)) {
/*  343 */                 this.parametersInDuplicateKeyClause = true;
/*      */               }
/*      */             }
/*      */ 
/*  347 */             if ((inQuotes) || (inQuotedId) || (i >= stopLookingForLimitClause) || (
/*  348 */               (c != 'L') && (c != 'l'))) continue;
/*  349 */             char posI1 = sql.charAt(i + 1);
/*      */ 
/*  351 */             if ((posI1 == 'I') || (posI1 == 'i')) {
/*  352 */               char posM = sql.charAt(i + 2);
/*      */ 
/*  354 */               if ((posM == 'M') || (posM == 'm')) {
/*  355 */                 char posI2 = sql.charAt(i + 3);
/*      */ 
/*  357 */                 if ((posI2 == 'I') || (posI2 == 'i')) {
/*  358 */                   char posT = sql.charAt(i + 4);
/*      */ 
/*  360 */                   if ((posT != 'T') && (posT != 't'))
/*      */                     continue;
/*  362 */                   boolean hasPreviosIdChar = false;
/*  363 */                   boolean hasFollowingIdChar = false;
/*  364 */                   if ((i > this.statementStartPos) && (StringUtils.isValidIdChar(sql.charAt(i - 1)))) {
/*  365 */                     hasPreviosIdChar = true;
/*      */                   }
/*  367 */                   if ((i + 5 < this.statementLength) && (StringUtils.isValidIdChar(sql.charAt(i + 5)))) {
/*  368 */                     hasFollowingIdChar = true;
/*      */                   }
/*  370 */                   if ((!hasPreviosIdChar) && (!hasFollowingIdChar)) {
/*  371 */                     this.foundLimitClause = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  381 */         if (this.firstStmtChar == 'L') {
/*  382 */           if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA"))
/*  383 */             this.foundLoadData = true;
/*      */           else
/*  385 */             this.foundLoadData = false;
/*      */         }
/*      */         else {
/*  388 */           this.foundLoadData = false;
/*      */         }
/*      */ 
/*  391 */         endpointList.add(new int[] { lastParmEnd, this.statementLength });
/*  392 */         this.staticSql = new byte[endpointList.size()][];
/*  393 */         char[] asCharArray = sql.toCharArray();
/*      */ 
/*  395 */         for (i = 0; i < this.staticSql.length; i++) {
/*  396 */           int[] ep = (int[])endpointList.get(i);
/*  397 */           int end = ep[1];
/*  398 */           int begin = ep[0];
/*  399 */           int len = end - begin;
/*      */ 
/*  401 */           if (this.foundLoadData) {
/*  402 */             String temp = new String(asCharArray, begin, len);
/*  403 */             this.staticSql[i] = StringUtils.getBytes(temp);
/*  404 */           } else if (encoding == null) {
/*  405 */             byte[] buf = new byte[len];
/*      */ 
/*  407 */             for (int j = 0; j < len; j++) {
/*  408 */               buf[j] = (byte)sql.charAt(begin + j);
/*      */             }
/*      */ 
/*  411 */             this.staticSql[i] = buf;
/*      */           }
/*  413 */           else if (converter != null) {
/*  414 */             this.staticSql[i] = StringUtils.getBytes(sql, converter, encoding, PreparedStatement.this.connection.getServerCharacterEncoding(), begin, len, PreparedStatement.this.connection.parserKnowsUnicode(), PreparedStatement.this.getExceptionInterceptor());
/*      */           }
/*      */           else
/*      */           {
/*  419 */             String temp = new String(asCharArray, begin, len);
/*      */ 
/*  421 */             this.staticSql[i] = StringUtils.getBytes(temp, encoding, PreparedStatement.this.connection.getServerCharacterEncoding(), PreparedStatement.this.connection.parserKnowsUnicode(), conn, PreparedStatement.this.getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (StringIndexOutOfBoundsException oobEx)
/*      */       {
/*      */         String quotedIdentifierString;
/*  429 */         SQLException sqlEx = new SQLException("Parse error for " + sql);
/*  430 */         sqlEx.initCause(oobEx);
/*      */ 
/*  432 */         throw sqlEx;
/*      */       }
/*      */ 
/*  436 */       if (buildRewriteInfo) {
/*  437 */         this.canRewriteAsMultiValueInsert = ((PreparedStatement.canRewrite(sql, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementStartPos)) && (!this.parametersInDuplicateKeyClause));
/*      */ 
/*  442 */         if ((this.canRewriteAsMultiValueInsert) && (conn.getRewriteBatchedStatements()))
/*      */         {
/*  444 */           buildRewriteBatchedParams(sql, conn, dbmd, encoding, converter);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void buildRewriteBatchedParams(String sql, MySQLConnection conn, DatabaseMetaData metadata, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  459 */       this.valuesClause = extractValuesClause(sql);
/*  460 */       String odkuClause = this.isOnDuplicateKeyUpdate ? sql.substring(this.locationOfOnDuplicateKeyUpdate) : null;
/*      */ 
/*  463 */       String headSql = null;
/*      */ 
/*  465 */       if (this.isOnDuplicateKeyUpdate)
/*  466 */         headSql = sql.substring(0, this.locationOfOnDuplicateKeyUpdate);
/*      */       else {
/*  468 */         headSql = sql;
/*      */       }
/*      */ 
/*  471 */       this.batchHead = new ParseInfo(PreparedStatement.this, headSql, conn, metadata, encoding, converter, false);
/*      */ 
/*  473 */       this.batchValues = new ParseInfo(PreparedStatement.this, "," + this.valuesClause, conn, metadata, encoding, converter, false);
/*      */ 
/*  475 */       this.batchODKUClause = null;
/*      */ 
/*  477 */       if ((odkuClause != null) && (odkuClause.length() > 0))
/*  478 */         this.batchODKUClause = new ParseInfo(PreparedStatement.this, "," + this.valuesClause + " " + odkuClause, conn, metadata, encoding, converter, false);
/*      */     }
/*      */ 
/*      */     private String extractValuesClause(String sql)
/*      */       throws SQLException
/*      */     {
/*  485 */       String quoteCharStr = PreparedStatement.this.connection.getMetaData().getIdentifierQuoteString();
/*      */ 
/*  488 */       int indexOfValues = -1;
/*  489 */       int valuesSearchStart = this.statementStartPos;
/*      */ 
/*  491 */       while (indexOfValues == -1) {
/*  492 */         if (quoteCharStr.length() > 0) {
/*  493 */           indexOfValues = StringUtils.indexOfIgnoreCaseRespectQuotes(valuesSearchStart, PreparedStatement.this.originalSql, "VALUES", quoteCharStr.charAt(0), false);
/*      */         }
/*      */         else
/*      */         {
/*  497 */           indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, PreparedStatement.this.originalSql, "VALUES");
/*      */         }
/*      */ 
/*  502 */         if (indexOfValues <= 0)
/*      */           break;
/*  504 */         char c = PreparedStatement.this.originalSql.charAt(indexOfValues - 1);
/*  505 */         if ((!Character.isWhitespace(c)) && (c != ')') && (c != '`')) {
/*  506 */           valuesSearchStart = indexOfValues + 6;
/*  507 */           indexOfValues = -1;
/*      */         }
/*      */         else {
/*  510 */           c = PreparedStatement.this.originalSql.charAt(indexOfValues + 6);
/*  511 */           if ((!Character.isWhitespace(c)) && (c != '(')) {
/*  512 */             valuesSearchStart = indexOfValues + 6;
/*  513 */             indexOfValues = -1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  521 */       if (indexOfValues == -1) {
/*  522 */         return null;
/*      */       }
/*      */ 
/*  525 */       int indexOfFirstParen = sql.indexOf('(', indexOfValues + 6);
/*      */ 
/*  527 */       if (indexOfFirstParen == -1) {
/*  528 */         return null;
/*      */       }
/*      */ 
/*  531 */       int endOfValuesClause = sql.lastIndexOf(')');
/*      */ 
/*  533 */       if (endOfValuesClause == -1) {
/*  534 */         return null;
/*      */       }
/*      */ 
/*  537 */       if (this.isOnDuplicateKeyUpdate) {
/*  538 */         endOfValuesClause = this.locationOfOnDuplicateKeyUpdate - 1;
/*      */       }
/*      */ 
/*  541 */       return sql.substring(indexOfFirstParen, endOfValuesClause + 1);
/*      */     }
/*      */ 
/*      */     synchronized ParseInfo getParseInfoForBatch(int numBatch)
/*      */     {
/*  548 */       PreparedStatement.AppendingBatchVisitor apv = new PreparedStatement.AppendingBatchVisitor(PreparedStatement.this);
/*  549 */       buildInfoForBatch(numBatch, apv);
/*      */ 
/*  551 */       ParseInfo batchParseInfo = new ParseInfo(PreparedStatement.this, apv.getStaticSqlStrings(), this.firstStmtChar, this.foundLimitClause, this.foundLoadData, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementLength, this.statementStartPos);
/*      */ 
/*  557 */       return batchParseInfo;
/*      */     }
/*      */ 
/*      */     String getSqlForBatch(int numBatch)
/*      */       throws UnsupportedEncodingException
/*      */     {
/*  566 */       ParseInfo batchInfo = getParseInfoForBatch(numBatch);
/*      */ 
/*  568 */       return getSqlForBatch(batchInfo);
/*      */     }
/*      */ 
/*      */     String getSqlForBatch(ParseInfo batchInfo)
/*      */       throws UnsupportedEncodingException
/*      */     {
/*  575 */       int size = 0;
/*  576 */       byte[][] sqlStrings = batchInfo.staticSql;
/*  577 */       int sqlStringsLength = sqlStrings.length;
/*      */ 
/*  579 */       for (int i = 0; i < sqlStringsLength; i++) {
/*  580 */         size += sqlStrings[i].length;
/*  581 */         size++;
/*      */       }
/*      */ 
/*  584 */       StringBuffer buf = new StringBuffer(size);
/*      */ 
/*  586 */       for (int i = 0; i < sqlStringsLength - 1; i++) {
/*  587 */         buf.append(StringUtils.toString(sqlStrings[i], PreparedStatement.this.charEncoding));
/*  588 */         buf.append("?");
/*      */       }
/*      */ 
/*  591 */       buf.append(StringUtils.toString(sqlStrings[(sqlStringsLength - 1)]));
/*      */ 
/*  593 */       return buf.toString();
/*      */     }
/*      */ 
/*      */     private void buildInfoForBatch(int numBatch, PreparedStatement.BatchVisitor visitor)
/*      */     {
/*  605 */       byte[][] headStaticSql = this.batchHead.staticSql;
/*  606 */       int headStaticSqlLength = headStaticSql.length;
/*      */ 
/*  608 */       if (headStaticSqlLength > 1) {
/*  609 */         for (int i = 0; i < headStaticSqlLength - 1; i++) {
/*  610 */           visitor.append(headStaticSql[i]).increment();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  615 */       byte[] endOfHead = headStaticSql[(headStaticSqlLength - 1)];
/*  616 */       byte[][] valuesStaticSql = this.batchValues.staticSql;
/*  617 */       byte[] beginOfValues = valuesStaticSql[0];
/*      */ 
/*  619 */       visitor.merge(endOfHead, beginOfValues).increment();
/*      */ 
/*  621 */       int numValueRepeats = numBatch - 1;
/*      */ 
/*  623 */       if (this.batchODKUClause != null) {
/*  624 */         numValueRepeats--;
/*      */       }
/*      */ 
/*  627 */       int valuesStaticSqlLength = valuesStaticSql.length;
/*  628 */       byte[] endOfValues = valuesStaticSql[(valuesStaticSqlLength - 1)];
/*      */ 
/*  630 */       for (int i = 0; i < numValueRepeats; i++) {
/*  631 */         for (int j = 1; j < valuesStaticSqlLength - 1; j++) {
/*  632 */           visitor.append(valuesStaticSql[j]).increment();
/*      */         }
/*  634 */         visitor.merge(endOfValues, beginOfValues).increment();
/*      */       }
/*      */ 
/*  637 */       if (this.batchODKUClause != null) {
/*  638 */         byte[][] batchOdkuStaticSql = this.batchODKUClause.staticSql;
/*  639 */         byte[] beginOfOdku = batchOdkuStaticSql[0];
/*  640 */         visitor.decrement().merge(endOfValues, beginOfOdku).increment();
/*      */ 
/*  642 */         int batchOdkuStaticSqlLength = batchOdkuStaticSql.length;
/*      */ 
/*  644 */         if (numBatch > 1) {
/*  645 */           for (int i = 1; i < batchOdkuStaticSqlLength; i++) {
/*  646 */             visitor.append(batchOdkuStaticSql[i]).increment();
/*      */           }
/*      */         }
/*      */         else {
/*  650 */           visitor.decrement().append(batchOdkuStaticSql[(batchOdkuStaticSqlLength - 1)]);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  655 */         visitor.decrement().append(this.staticSql[(this.staticSql.length - 1)]);
/*      */       }
/*      */     }
/*      */ 
/*      */     private ParseInfo(byte[][] staticSql, char firstStmtChar, boolean foundLimitClause, boolean foundLoadData, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementLength, int statementStartPos)
/*      */     {
/*  664 */       this.firstStmtChar = firstStmtChar;
/*  665 */       this.foundLimitClause = foundLimitClause;
/*  666 */       this.foundLoadData = foundLoadData;
/*  667 */       this.isOnDuplicateKeyUpdate = isOnDuplicateKeyUpdate;
/*  668 */       this.locationOfOnDuplicateKeyUpdate = locationOfOnDuplicateKeyUpdate;
/*  669 */       this.statementLength = statementLength;
/*  670 */       this.statementStartPos = statementStartPos;
/*  671 */       this.staticSql = staticSql;
/*      */     }
/*      */   }
/*      */ 
/*      */   class EndPoint
/*      */   {
/*      */     int begin;
/*      */     int end;
/*      */ 
/*      */     EndPoint(int b, int e)
/*      */     {
/*  166 */       this.begin = b;
/*  167 */       this.end = e;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class BatchParams
/*      */   {
/*  127 */     public boolean[] isNull = null;
/*      */ 
/*  129 */     public boolean[] isStream = null;
/*      */ 
/*  131 */     public InputStream[] parameterStreams = null;
/*      */ 
/*  133 */     public byte[][] parameterStrings = (byte[][])null;
/*      */ 
/*  135 */     public int[] streamLengths = null;
/*      */ 
/*      */     BatchParams(byte[][] strings, InputStream[] streams, boolean[] isStreamFlags, int[] lengths, boolean[] isNullFlags)
/*      */     {
/*  142 */       this.parameterStrings = new byte[strings.length][];
/*  143 */       this.parameterStreams = new InputStream[streams.length];
/*  144 */       this.isStream = new boolean[isStreamFlags.length];
/*  145 */       this.streamLengths = new int[lengths.length];
/*  146 */       this.isNull = new boolean[isNullFlags.length];
/*  147 */       System.arraycopy(strings, 0, this.parameterStrings, 0, strings.length);
/*      */ 
/*  149 */       System.arraycopy(streams, 0, this.parameterStreams, 0, streams.length);
/*      */ 
/*  151 */       System.arraycopy(isStreamFlags, 0, this.isStream, 0, isStreamFlags.length);
/*      */ 
/*  153 */       System.arraycopy(lengths, 0, this.streamLengths, 0, lengths.length);
/*  154 */       System.arraycopy(isNullFlags, 0, this.isNull, 0, isNullFlags.length);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.PreparedStatement
 * JD-Core Version:    0.6.0
 */