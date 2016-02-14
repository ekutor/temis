/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class ResultSetImpl
/*      */   implements ResultSetInternalMethods
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_RS_4_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_RS_6_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_UPD_RS_6_ARG_CTOR;
/*      */   protected static final double MIN_DIFF_PREC;
/*      */   protected static final double MAX_DIFF_PREC;
/*      */   static int resultCounter;
/*  194 */   protected String catalog = null;
/*      */ 
/*  197 */   protected Map<String, Integer> columnLabelToIndex = null;
/*      */ 
/*  203 */   protected Map<String, Integer> columnToIndexCache = null;
/*      */ 
/*  206 */   protected boolean[] columnUsed = null;
/*      */   protected volatile MySQLConnection connection;
/*  212 */   protected long connectionId = 0L;
/*      */ 
/*  215 */   protected int currentRow = -1;
/*      */   TimeZone defaultTimeZone;
/*  220 */   protected boolean doingUpdates = false;
/*      */ 
/*  222 */   protected ProfilerEventHandler eventSink = null;
/*      */ 
/*  224 */   Calendar fastDateCal = null;
/*      */ 
/*  227 */   protected int fetchDirection = 1000;
/*      */ 
/*  230 */   protected int fetchSize = 0;
/*      */   protected Field[] fields;
/*      */   protected char firstCharOfQuery;
/*  243 */   protected Map<String, Integer> fullColumnNameToIndex = null;
/*      */ 
/*  245 */   protected Map<String, Integer> columnNameToIndex = null;
/*      */ 
/*  247 */   protected boolean hasBuiltIndexMapping = false;
/*      */ 
/*  253 */   protected boolean isBinaryEncoded = false;
/*      */ 
/*  256 */   protected boolean isClosed = false;
/*      */ 
/*  258 */   protected ResultSetInternalMethods nextResultSet = null;
/*      */ 
/*  261 */   protected boolean onInsertRow = false;
/*      */   protected StatementImpl owningStatement;
/*      */   protected String pointOfOrigin;
/*  272 */   protected boolean profileSql = false;
/*      */ 
/*  278 */   protected boolean reallyResult = false;
/*      */   protected int resultId;
/*  284 */   protected int resultSetConcurrency = 0;
/*      */ 
/*  287 */   protected int resultSetType = 0;
/*      */   protected RowData rowData;
/*  296 */   protected String serverInfo = null;
/*      */   PreparedStatement statementUsedForFetchingRows;
/*  301 */   protected ResultSetRow thisRow = null;
/*      */   protected long updateCount;
/*  315 */   protected long updateId = -1L;
/*      */ 
/*  317 */   private boolean useStrictFloatingPoint = false;
/*      */ 
/*  319 */   protected boolean useUsageAdvisor = false;
/*      */ 
/*  322 */   protected SQLWarning warningChain = null;
/*      */ 
/*  325 */   protected boolean wasNullFlag = false;
/*      */   protected Statement wrapperStatement;
/*      */   protected boolean retainOwningStatement;
/*  331 */   protected Calendar gmtCalendar = null;
/*      */ 
/*  333 */   protected boolean useFastDateParsing = false;
/*      */ 
/*  335 */   private boolean padCharsWithSpace = false;
/*      */   private boolean jdbcCompliantTruncationForReads;
/*  339 */   private boolean useFastIntParsing = true;
/*      */   private boolean useColumnNamesInFindColumn;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   static final char[] EMPTY_SPACE;
/*  860 */   private boolean onValidRow = false;
/*  861 */   private String invalidRowReason = null;
/*      */   protected boolean useLegacyDatetimeCode;
/*      */   private TimeZone serverTimeZoneTz;
/*      */ 
/*      */   protected static BigInteger convertLongToUlong(long longVal)
/*      */   {
/*  180 */     byte[] asBytes = new byte[8];
/*  181 */     asBytes[7] = (byte)(int)(longVal & 0xFF);
/*  182 */     asBytes[6] = (byte)(int)(longVal >>> 8);
/*  183 */     asBytes[5] = (byte)(int)(longVal >>> 16);
/*  184 */     asBytes[4] = (byte)(int)(longVal >>> 24);
/*  185 */     asBytes[3] = (byte)(int)(longVal >>> 32);
/*  186 */     asBytes[2] = (byte)(int)(longVal >>> 40);
/*  187 */     asBytes[1] = (byte)(int)(longVal >>> 48);
/*  188 */     asBytes[0] = (byte)(int)(longVal >>> 56);
/*      */ 
/*  190 */     return new BigInteger(1, asBytes);
/*      */   }
/*      */ 
/*      */   protected static ResultSetImpl getInstance(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  354 */     if (!Util.isJdbc4()) {
/*  355 */       return new ResultSetImpl(updateCount, updateID, conn, creatorStmt);
/*      */     }
/*      */ 
/*  358 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, new Object[] { Long.valueOf(updateCount), Long.valueOf(updateID), conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static ResultSetImpl getInstance(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt, boolean isUpdatable)
/*      */     throws SQLException
/*      */   {
/*  374 */     if (!Util.isJdbc4()) {
/*  375 */       if (!isUpdatable) {
/*  376 */         return new ResultSetImpl(catalog, fields, tuples, conn, creatorStmt);
/*      */       }
/*      */ 
/*  379 */       return new UpdatableResultSet(catalog, fields, tuples, conn, creatorStmt);
/*      */     }
/*      */ 
/*  383 */     if (!isUpdatable) {
/*  384 */       return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */     }
/*      */ 
/*  389 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_UPD_RS_6_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ResultSetImpl(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
/*      */   {
/*  407 */     this.updateCount = updateCount;
/*  408 */     this.updateId = updateID;
/*  409 */     this.reallyResult = false;
/*  410 */     this.fields = new Field[0];
/*      */ 
/*  412 */     this.connection = conn;
/*  413 */     this.owningStatement = creatorStmt;
/*      */ 
/*  415 */     this.retainOwningStatement = false;
/*      */ 
/*  417 */     if (this.connection != null) {
/*  418 */       this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */ 
/*  420 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */ 
/*  423 */       this.connectionId = this.connection.getId();
/*  424 */       this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
/*  425 */       this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
/*      */ 
/*  427 */       this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSetImpl(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  450 */     this.connection = conn;
/*      */ 
/*  452 */     this.retainOwningStatement = false;
/*      */ 
/*  454 */     if (this.connection != null) {
/*  455 */       this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
/*      */ 
/*  457 */       setDefaultTimeZone(this.connection.getDefaultTimeZone());
/*  458 */       this.connectionId = this.connection.getId();
/*  459 */       this.useFastDateParsing = this.connection.getUseFastDateParsing();
/*  460 */       this.profileSql = this.connection.getProfileSql();
/*  461 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */ 
/*  463 */       this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads();
/*  464 */       this.useFastIntParsing = this.connection.getUseFastIntParsing();
/*  465 */       this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
/*  466 */       this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
/*      */     }
/*      */ 
/*  469 */     this.owningStatement = creatorStmt;
/*      */ 
/*  471 */     this.catalog = catalog;
/*      */ 
/*  473 */     this.fields = fields;
/*  474 */     this.rowData = tuples;
/*  475 */     this.updateCount = this.rowData.size();
/*      */ 
/*  482 */     this.reallyResult = true;
/*      */ 
/*  485 */     if (this.rowData.size() > 0) {
/*  486 */       if ((this.updateCount == 1L) && 
/*  487 */         (this.thisRow == null)) {
/*  488 */         this.rowData.close();
/*  489 */         this.updateCount = -1L;
/*      */       }
/*      */     }
/*      */     else {
/*  493 */       this.thisRow = null;
/*      */     }
/*      */ 
/*  496 */     this.rowData.setOwner(this);
/*      */ 
/*  498 */     if (this.fields != null) {
/*  499 */       initializeWithMetadata();
/*      */     }
/*  501 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */ 
/*  503 */     this.useColumnNamesInFindColumn = this.connection.getUseColumnNamesInFindColumn();
/*      */ 
/*  505 */     setRowPositionValidity();
/*      */   }
/*      */ 
/*      */   public void initializeWithMetadata() throws SQLException {
/*  509 */     synchronized (checkClosed()) {
/*  510 */       this.rowData.setMetadata(this.fields);
/*      */ 
/*  512 */       this.columnToIndexCache = new HashMap();
/*      */ 
/*  514 */       if ((this.profileSql) || (this.connection.getUseUsageAdvisor())) {
/*  515 */         this.columnUsed = new boolean[this.fields.length];
/*  516 */         this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
/*  517 */         this.resultId = (resultCounter++);
/*  518 */         this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  519 */         this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */       }
/*      */ 
/*  522 */       if (this.connection.getGatherPerformanceMetrics()) {
/*  523 */         this.connection.incrementNumberOfResultSetsCreated();
/*      */ 
/*  525 */         Set tableNamesSet = new HashSet();
/*      */ 
/*  527 */         for (int i = 0; i < this.fields.length; i++) {
/*  528 */           Field f = this.fields[i];
/*      */ 
/*  530 */           String tableName = f.getOriginalTableName();
/*      */ 
/*  532 */           if (tableName == null) {
/*  533 */             tableName = f.getTableName();
/*      */           }
/*      */ 
/*  536 */           if (tableName != null) {
/*  537 */             if (this.connection.lowerCaseTableNames()) {
/*  538 */               tableName = tableName.toLowerCase();
/*      */             }
/*      */ 
/*  542 */             tableNamesSet.add(tableName);
/*      */           }
/*      */         }
/*      */ 
/*  546 */         this.connection.reportNumberOfTablesAccessed(tableNamesSet.size());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void createCalendarIfNeeded() {
/*  552 */     if (this.fastDateCal == null) {
/*  553 */       this.fastDateCal = new GregorianCalendar(Locale.US);
/*  554 */       this.fastDateCal.setTimeZone(getDefaultTimeZone());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  597 */     synchronized (checkClosed())
/*      */     {
/*      */       boolean b;
/*      */       boolean b;
/*  601 */       if (this.rowData.size() == 0) {
/*  602 */         b = false;
/*      */       } else {
/*  604 */         if (row == 0) {
/*  605 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Cannot_absolute_position_to_row_0_110"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*  611 */         if (this.onInsertRow) {
/*  612 */           this.onInsertRow = false;
/*      */         }
/*      */ 
/*  615 */         if (this.doingUpdates) {
/*  616 */           this.doingUpdates = false;
/*      */         }
/*      */ 
/*  619 */         if (this.thisRow != null)
/*  620 */           this.thisRow.closeOpenStreams();
/*      */         boolean b;
/*  623 */         if (row == 1) {
/*  624 */           b = first();
/*      */         }
/*      */         else
/*      */         {
/*      */           boolean b;
/*  625 */           if (row == -1) {
/*  626 */             b = last();
/*      */           }
/*      */           else
/*      */           {
/*      */             boolean b;
/*  627 */             if (row > this.rowData.size()) {
/*  628 */               afterLast();
/*  629 */               b = false;
/*      */             }
/*      */             else
/*      */             {
/*      */               boolean b;
/*  631 */               if (row < 0)
/*      */               {
/*  633 */                 int newRowPosition = this.rowData.size() + row + 1;
/*      */                 boolean b;
/*  635 */                 if (newRowPosition <= 0) {
/*  636 */                   beforeFirst();
/*  637 */                   b = false;
/*      */                 } else {
/*  639 */                   b = absolute(newRowPosition);
/*      */                 }
/*      */               } else {
/*  642 */                 row--;
/*  643 */                 this.rowData.setCurrentRow(row);
/*  644 */                 this.thisRow = this.rowData.getAt(row);
/*  645 */                 b = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  650 */       setRowPositionValidity();
/*      */ 
/*  652 */       return b;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void afterLast()
/*      */     throws SQLException
/*      */   {
/*  669 */     synchronized (checkClosed())
/*      */     {
/*  671 */       if (this.onInsertRow) {
/*  672 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/*  675 */       if (this.doingUpdates) {
/*  676 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/*  679 */       if (this.thisRow != null) {
/*  680 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */ 
/*  683 */       if (this.rowData.size() != 0) {
/*  684 */         this.rowData.afterLast();
/*  685 */         this.thisRow = null;
/*      */       }
/*      */ 
/*  688 */       setRowPositionValidity();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  705 */     synchronized (checkClosed())
/*      */     {
/*  707 */       if (this.onInsertRow) {
/*  708 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/*  711 */       if (this.doingUpdates) {
/*  712 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/*  715 */       if (this.rowData.size() == 0) {
/*  716 */         return;
/*      */       }
/*      */ 
/*  719 */       if (this.thisRow != null) {
/*  720 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */ 
/*  723 */       this.rowData.beforeFirst();
/*  724 */       this.thisRow = null;
/*      */ 
/*  726 */       setRowPositionValidity();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void buildIndexMapping()
/*      */     throws SQLException
/*      */   {
/*  738 */     int numFields = this.fields.length;
/*  739 */     this.columnLabelToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  740 */     this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  741 */     this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */ 
/*  755 */     for (int i = numFields - 1; i >= 0; i--) {
/*  756 */       Integer index = Integer.valueOf(i);
/*  757 */       String columnName = this.fields[i].getOriginalName();
/*  758 */       String columnLabel = this.fields[i].getName();
/*  759 */       String fullColumnName = this.fields[i].getFullName();
/*      */ 
/*  761 */       if (columnLabel != null) {
/*  762 */         this.columnLabelToIndex.put(columnLabel, index);
/*      */       }
/*      */ 
/*  765 */       if (fullColumnName != null) {
/*  766 */         this.fullColumnNameToIndex.put(fullColumnName, index);
/*      */       }
/*      */ 
/*  769 */       if (columnName != null) {
/*  770 */         this.columnNameToIndex.put(columnName, index);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  775 */     this.hasBuiltIndexMapping = true;
/*      */   }
/*      */ 
/*      */   public void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  791 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   protected final MySQLConnection checkClosed()
/*      */     throws SQLException
/*      */   {
/*  801 */     MySQLConnection c = this.connection;
/*      */ 
/*  803 */     if (c == null) {
/*  804 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  810 */     return c;
/*      */   }
/*      */ 
/*      */   protected final void checkColumnBounds(int columnIndex)
/*      */     throws SQLException
/*      */   {
/*  823 */     synchronized (checkClosed()) {
/*  824 */       if (columnIndex < 1) {
/*  825 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  830 */       if (columnIndex > this.fields.length) {
/*  831 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  838 */       if ((this.profileSql) || (this.useUsageAdvisor))
/*  839 */         this.columnUsed[(columnIndex - 1)] = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  852 */     checkClosed();
/*      */ 
/*  854 */     if (!this.onValidRow)
/*  855 */       throw SQLError.createSQLException(this.invalidRowReason, "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private void setRowPositionValidity()
/*      */     throws SQLException
/*      */   {
/*  866 */     if ((!this.rowData.isDynamic()) && (this.rowData.size() == 0)) {
/*  867 */       this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
/*      */ 
/*  869 */       this.onValidRow = false;
/*  870 */     } else if (this.rowData.isBeforeFirst()) {
/*  871 */       this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
/*      */ 
/*  873 */       this.onValidRow = false;
/*  874 */     } else if (this.rowData.isAfterLast()) {
/*  875 */       this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
/*      */ 
/*  877 */       this.onValidRow = false;
/*      */     } else {
/*  879 */       this.onValidRow = true;
/*  880 */       this.invalidRowReason = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void clearNextResult()
/*      */   {
/*  889 */     this.nextResultSet = null;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  900 */     synchronized (checkClosed()) {
/*  901 */       this.warningChain = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  923 */     realClose(true);
/*      */   }
/*      */ 
/*      */   private int convertToZeroWithEmptyCheck()
/*      */     throws SQLException
/*      */   {
/*  930 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  931 */       return 0;
/*      */     }
/*      */ 
/*  934 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private String convertToZeroLiteralStringWithEmptyCheck()
/*      */     throws SQLException
/*      */   {
/*  941 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  942 */       return "0";
/*      */     }
/*      */ 
/*  945 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ResultSetInternalMethods copy()
/*      */     throws SQLException
/*      */   {
/*  953 */     synchronized (checkClosed()) {
/*  954 */       ResultSetInternalMethods rs = getInstance(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement, false);
/*      */ 
/*  957 */       return rs;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void redefineFieldsForDBMD(Field[] f) {
/*  962 */     this.fields = f;
/*      */ 
/*  964 */     for (int i = 0; i < this.fields.length; i++) {
/*  965 */       this.fields[i].setUseOldNameMetadata(true);
/*  966 */       this.fields[i].setConnection(this.connection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException
/*      */   {
/*  972 */     cachedMetaData.fields = this.fields;
/*  973 */     cachedMetaData.columnNameToIndex = this.columnLabelToIndex;
/*  974 */     cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
/*  975 */     cachedMetaData.metadata = getMetaData();
/*      */   }
/*      */ 
/*      */   public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
/*  979 */     this.fields = cachedMetaData.fields;
/*  980 */     this.columnLabelToIndex = cachedMetaData.columnNameToIndex;
/*  981 */     this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
/*  982 */     this.hasBuiltIndexMapping = true;
/*      */   }
/*      */ 
/*      */   public void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  997 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   private String extractStringFromNativeColumn(int columnIndex, int mysqlType)
/*      */     throws SQLException
/*      */   {
/* 1009 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1011 */     this.wasNullFlag = false;
/*      */ 
/* 1013 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1014 */       this.wasNullFlag = true;
/*      */ 
/* 1016 */       return null;
/*      */     }
/*      */ 
/* 1019 */     this.wasNullFlag = false;
/*      */ 
/* 1021 */     String encoding = this.fields[columnIndexMinusOne].getCharacterSet();
/*      */ 
/* 1024 */     return this.thisRow.getString(columnIndex - 1, encoding, this.connection);
/*      */   }
/*      */ 
/*      */   protected Date fastDateCreate(Calendar cal, int year, int month, int day) throws SQLException
/*      */   {
/* 1029 */     synchronized (checkClosed()) {
/* 1030 */       if (this.useLegacyDatetimeCode) {
/* 1031 */         return TimeUtil.fastDateCreate(year, month, day, cal);
/*      */       }
/*      */ 
/* 1034 */       if (cal == null) {
/* 1035 */         createCalendarIfNeeded();
/* 1036 */         cal = this.fastDateCal;
/*      */       }
/*      */ 
/* 1039 */       boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */ 
/* 1041 */       return TimeUtil.fastDateCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : cal, cal, year, month, day);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Time fastTimeCreate(Calendar cal, int hour, int minute, int second)
/*      */     throws SQLException
/*      */   {
/* 1049 */     synchronized (checkClosed()) {
/* 1050 */       if (!this.useLegacyDatetimeCode) {
/* 1051 */         return TimeUtil.fastTimeCreate(hour, minute, second, cal, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1054 */       if (cal == null) {
/* 1055 */         createCalendarIfNeeded();
/* 1056 */         cal = this.fastDateCal;
/*      */       }
/*      */ 
/* 1059 */       return TimeUtil.fastTimeCreate(cal, hour, minute, second, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */     throws SQLException
/*      */   {
/* 1066 */     synchronized (checkClosed()) {
/* 1067 */       if (!this.useLegacyDatetimeCode) {
/* 1068 */         return TimeUtil.fastTimestampCreate(cal.getTimeZone(), year, month, day, hour, minute, seconds, secondsPart);
/*      */       }
/*      */ 
/* 1072 */       if (cal == null) {
/* 1073 */         createCalendarIfNeeded();
/* 1074 */         cal = this.fastDateCal;
/*      */       }
/*      */ 
/* 1077 */       boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */ 
/* 1079 */       return TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : null, cal, year, month, day, hour, minute, seconds, secondsPart);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int findColumn(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1127 */     synchronized (checkClosed())
/*      */     {
/* 1130 */       if (!this.hasBuiltIndexMapping) {
/* 1131 */         buildIndexMapping();
/*      */       }
/*      */ 
/* 1134 */       Integer index = (Integer)this.columnToIndexCache.get(columnName);
/*      */ 
/* 1136 */       if (index != null) {
/* 1137 */         return index.intValue() + 1;
/*      */       }
/*      */ 
/* 1140 */       index = (Integer)this.columnLabelToIndex.get(columnName);
/*      */ 
/* 1142 */       if ((index == null) && (this.useColumnNamesInFindColumn)) {
/* 1143 */         index = (Integer)this.columnNameToIndex.get(columnName);
/*      */       }
/*      */ 
/* 1146 */       if (index == null) {
/* 1147 */         index = (Integer)this.fullColumnNameToIndex.get(columnName);
/*      */       }
/*      */ 
/* 1150 */       if (index != null) {
/* 1151 */         this.columnToIndexCache.put(columnName, index);
/*      */ 
/* 1153 */         return index.intValue() + 1;
/*      */       }
/*      */ 
/* 1158 */       for (int i = 0; i < this.fields.length; i++) {
/* 1159 */         if (this.fields[i].getName().equalsIgnoreCase(columnName))
/* 1160 */           return i + 1;
/* 1161 */         if (this.fields[i].getFullName().equalsIgnoreCase(columnName))
/*      */         {
/* 1163 */           return i + 1;
/*      */         }
/*      */       }
/*      */ 
/* 1167 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean first()
/*      */     throws SQLException
/*      */   {
/* 1188 */     synchronized (checkClosed())
/*      */     {
/* 1190 */       boolean b = true;
/*      */ 
/* 1192 */       if (this.rowData.isEmpty()) {
/* 1193 */         b = false;
/*      */       }
/*      */       else {
/* 1196 */         if (this.onInsertRow) {
/* 1197 */           this.onInsertRow = false;
/*      */         }
/*      */ 
/* 1200 */         if (this.doingUpdates) {
/* 1201 */           this.doingUpdates = false;
/*      */         }
/*      */ 
/* 1204 */         this.rowData.beforeFirst();
/* 1205 */         this.thisRow = this.rowData.next();
/*      */       }
/*      */ 
/* 1208 */       setRowPositionValidity();
/*      */ 
/* 1210 */       return b;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/* 1228 */     checkColumnBounds(i);
/*      */ 
/* 1230 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public Array getArray(String colName)
/*      */     throws SQLException
/*      */   {
/* 1247 */     return getArray(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1276 */     checkRowPos();
/*      */ 
/* 1278 */     if (!this.isBinaryEncoded) {
/* 1279 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */ 
/* 1282 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1297 */     return getAsciiStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1314 */     if (!this.isBinaryEncoded) {
/* 1315 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1318 */       if (stringVal != null) {
/* 1319 */         if (stringVal.length() == 0)
/*      */         {
/* 1321 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */ 
/* 1324 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 1328 */           BigDecimal val = new BigDecimal(stringVal);
/*      */ 
/* 1330 */           return val;
/*      */         } catch (NumberFormatException ex) {
/* 1332 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1340 */       return null;
/*      */     }
/*      */ 
/* 1343 */     return getNativeBigDecimal(columnIndex);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1364 */     if (!this.isBinaryEncoded) {
/* 1365 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1368 */       if (stringVal != null) {
/* 1369 */         if (stringVal.length() == 0) {
/* 1370 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */           try
/*      */           {
/* 1374 */             return val.setScale(scale);
/*      */           } catch (ArithmeticException ex) {
/*      */             try {
/* 1377 */               return val.setScale(scale, 4);
/*      */             }
/*      */             catch (ArithmeticException arEx) {
/* 1380 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1390 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 1392 */           if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 1393 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 1395 */             val = new BigDecimal(valueAsLong);
/*      */           } else {
/* 1397 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1406 */           return val.setScale(scale);
/*      */         }
/*      */         catch (ArithmeticException ex)
/*      */         {
/*      */           try
/*      */           {
/*      */             BigDecimal val;
/* 1409 */             return val.setScale(scale, 4);
/*      */           } catch (ArithmeticException arithEx) {
/* 1411 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1420 */       return null;
/*      */     }
/*      */ 
/* 1423 */     return getNativeBigDecimal(columnIndex, scale);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1439 */     return getBigDecimal(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(String columnName, int scale)
/*      */     throws SQLException
/*      */   {
/* 1459 */     return getBigDecimal(findColumn(columnName), scale);
/*      */   }
/*      */ 
/*      */   private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1466 */     if (stringVal != null) {
/* 1467 */       if (stringVal.length() == 0) {
/* 1468 */         BigDecimal bdVal = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */         try
/*      */         {
/* 1471 */           return bdVal.setScale(scale);
/*      */         } catch (ArithmeticException ex) {
/*      */           try {
/* 1474 */             return bdVal.setScale(scale, 4);
/*      */           } catch (ArithmeticException arEx) {
/* 1476 */             throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1487 */         return new BigDecimal(stringVal).setScale(scale);
/*      */       } catch (ArithmeticException ex) {
/*      */         try {
/* 1490 */           return new BigDecimal(stringVal).setScale(scale, 4);
/*      */         }
/*      */         catch (ArithmeticException arEx) {
/* 1493 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/* 1501 */         if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 1502 */           long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */           try
/*      */           {
/* 1505 */             return new BigDecimal(valueAsLong).setScale(scale);
/*      */           } catch (ArithmeticException arEx1) {
/*      */             try {
/* 1508 */               return new BigDecimal(valueAsLong).setScale(scale, 4);
/*      */             }
/*      */             catch (ArithmeticException arEx2) {
/* 1511 */               throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1520 */         if ((this.fields[(columnIndex - 1)].getMysqlType() == 1) && (this.connection.getTinyInt1isBit()) && (this.fields[(columnIndex - 1)].getLength() == 1L))
/*      */         {
/* 1522 */           return new BigDecimal(stringVal.equalsIgnoreCase("true") ? 1 : 0).setScale(scale);
/*      */         }
/*      */ 
/* 1525 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1533 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1554 */     checkRowPos();
/*      */ 
/* 1556 */     if (!this.isBinaryEncoded) {
/* 1557 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1559 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1561 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1562 */         this.wasNullFlag = true;
/*      */ 
/* 1564 */         return null;
/*      */       }
/*      */ 
/* 1567 */       this.wasNullFlag = false;
/*      */ 
/* 1569 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1572 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1587 */     return getBinaryStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public java.sql.Blob getBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1602 */     if (!this.isBinaryEncoded) {
/* 1603 */       checkRowPos();
/*      */ 
/* 1605 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1607 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1609 */       if (this.thisRow.isNull(columnIndexMinusOne))
/* 1610 */         this.wasNullFlag = true;
/*      */       else {
/* 1612 */         this.wasNullFlag = false;
/*      */       }
/*      */ 
/* 1615 */       if (this.wasNullFlag) {
/* 1616 */         return null;
/*      */       }
/*      */ 
/* 1619 */       if (!this.connection.getEmulateLocators()) {
/* 1620 */         return new Blob(this.thisRow.getColumnValue(columnIndexMinusOne), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1623 */       return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1626 */     return getNativeBlob(columnIndex);
/*      */   }
/*      */ 
/*      */   public java.sql.Blob getBlob(String colName)
/*      */     throws SQLException
/*      */   {
/* 1641 */     return getBlob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1657 */     checkColumnBounds(columnIndex);
/*      */ 
/* 1664 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1666 */     Field field = this.fields[columnIndexMinusOne];
/*      */ 
/* 1668 */     if (field.getMysqlType() == 16) {
/* 1669 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1672 */     this.wasNullFlag = false;
/*      */ 
/* 1674 */     int sqlType = field.getSQLType();
/*      */     long boolVal;
/* 1676 */     switch (sqlType) {
/*      */     case 16:
/* 1678 */       if (field.getMysqlType() == -1) {
/* 1679 */         String stringVal = getString(columnIndex);
/*      */ 
/* 1681 */         return getBooleanFromString(stringVal);
/*      */       }
/*      */ 
/* 1684 */       boolVal = getLong(columnIndex, false);
/*      */ 
/* 1686 */       return (boolVal == -1L) || (boolVal > 0L);
/*      */     case -7:
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 1697 */       boolVal = getLong(columnIndex, false);
/*      */ 
/* 1699 */       return (boolVal == -1L) || (boolVal > 0L);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/* 1701 */     case 15: } if (this.connection.getPedantic())
/*      */     {
/* 1703 */       switch (sqlType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case 70:
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/*      */       case 2000:
/*      */       case 2002:
/*      */       case 2003:
/*      */       case 2004:
/*      */       case 2005:
/*      */       case 2006:
/* 1717 */         throw SQLError.createSQLException("Required type conversion not allowed", "22018", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1722 */     if ((sqlType == -2) || (sqlType == -3) || (sqlType == -4) || (sqlType == 2004))
/*      */     {
/* 1726 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1729 */     if (this.useUsageAdvisor) {
/* 1730 */       issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow.getColumnValue(columnIndexMinusOne), this.fields[columnIndex], new int[] { 16, 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 1742 */     String stringVal = getString(columnIndex);
/*      */ 
/* 1744 */     return getBooleanFromString(stringVal);
/*      */   }
/*      */ 
/*      */   private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException
/*      */   {
/* 1749 */     Object value = this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */ 
/* 1751 */     if (value == null) {
/* 1752 */       this.wasNullFlag = true;
/*      */ 
/* 1754 */       return false;
/*      */     }
/*      */ 
/* 1757 */     this.wasNullFlag = false;
/*      */ 
/* 1759 */     if (((byte[])(byte[])value).length == 0) {
/* 1760 */       return false;
/*      */     }
/*      */ 
/* 1763 */     byte boolVal = ((byte[])(byte[])value)[0];
/*      */ 
/* 1765 */     if (boolVal == 49)
/* 1766 */       return true;
/* 1767 */     if (boolVal == 48) {
/* 1768 */       return false;
/*      */     }
/*      */ 
/* 1771 */     return (boolVal == -1) || (boolVal > 0);
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1786 */     return getBoolean(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final boolean getBooleanFromString(String stringVal) throws SQLException
/*      */   {
/* 1791 */     if ((stringVal != null) && (stringVal.length() > 0)) {
/* 1792 */       int c = Character.toLowerCase(stringVal.charAt(0));
/*      */ 
/* 1794 */       return (c == 116) || (c == 121) || (c == 49) || (stringVal.equals("-1"));
/*      */     }
/*      */ 
/* 1798 */     return false;
/*      */   }
/*      */ 
/*      */   public byte getByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1813 */     if (!this.isBinaryEncoded) {
/* 1814 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1816 */       if ((this.wasNullFlag) || (stringVal == null)) {
/* 1817 */         return 0;
/*      */       }
/*      */ 
/* 1820 */       return getByteFromString(stringVal, columnIndex);
/*      */     }
/*      */ 
/* 1823 */     return getNativeByte(columnIndex);
/*      */   }
/*      */ 
/*      */   public byte getByte(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1838 */     return getByte(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final byte getByteFromString(String stringVal, int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1844 */     if ((stringVal != null) && (stringVal.length() == 0)) {
/* 1845 */       return (byte)convertToZeroWithEmptyCheck();
/*      */     }
/*      */ 
/* 1856 */     if (stringVal == null) {
/* 1857 */       return 0;
/*      */     }
/*      */ 
/* 1860 */     stringVal = stringVal.trim();
/*      */     try
/*      */     {
/* 1863 */       int decimalIndex = stringVal.indexOf(".");
/*      */ 
/* 1866 */       if (decimalIndex != -1) {
/* 1867 */         double valueAsDouble = Double.parseDouble(stringVal);
/*      */ 
/* 1869 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 1870 */           (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
/*      */         {
/* 1872 */           throwRangeException(stringVal, columnIndex, -6);
/*      */         }
/*      */ 
/* 1877 */         return (byte)(int)valueAsDouble;
/*      */       }
/*      */ 
/* 1880 */       long valueAsLong = Long.parseLong(stringVal);
/*      */ 
/* 1882 */       if ((this.jdbcCompliantTruncationForReads) && (
/* 1883 */         (valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 1885 */         throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
/*      */       }
/*      */ 
/* 1890 */       return (byte)(int)valueAsLong; } catch (NumberFormatException NFE) {
/*      */     }
/* 1892 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Value____173") + stringVal + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1917 */     return getBytes(columnIndex, false);
/*      */   }
/*      */ 
/*      */   protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException
/*      */   {
/* 1922 */     if (!this.isBinaryEncoded) {
/* 1923 */       checkRowPos();
/*      */ 
/* 1925 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1927 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1929 */       if (this.thisRow.isNull(columnIndexMinusOne))
/* 1930 */         this.wasNullFlag = true;
/*      */       else {
/* 1932 */         this.wasNullFlag = false;
/*      */       }
/*      */ 
/* 1935 */       if (this.wasNullFlag) {
/* 1936 */         return null;
/*      */       }
/*      */ 
/* 1939 */       return this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 1942 */     return getNativeBytes(columnIndex, noConversion);
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1957 */     return getBytes(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final byte[] getBytesFromString(String stringVal) throws SQLException
/*      */   {
/* 1962 */     if (stringVal != null) {
/* 1963 */       return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1970 */     return null;
/*      */   }
/*      */ 
/*      */   public int getBytesSize() throws SQLException {
/* 1974 */     RowData localRowData = this.rowData;
/*      */ 
/* 1976 */     checkClosed();
/*      */ 
/* 1978 */     if ((localRowData instanceof RowDataStatic)) {
/* 1979 */       int bytesSize = 0;
/*      */ 
/* 1981 */       int numRows = localRowData.size();
/*      */ 
/* 1983 */       for (int i = 0; i < numRows; i++) {
/* 1984 */         bytesSize += localRowData.getAt(i).getBytesSize();
/*      */       }
/*      */ 
/* 1987 */       return bytesSize;
/*      */     }
/*      */ 
/* 1990 */     return -1;
/*      */   }
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */     throws SQLException
/*      */   {
/* 1998 */     synchronized (checkClosed()) {
/* 1999 */       if (this.connection != null) {
/* 2000 */         return this.connection.getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */ 
/* 2004 */       return new GregorianCalendar();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2025 */     if (!this.isBinaryEncoded) {
/* 2026 */       checkColumnBounds(columnIndex);
/*      */ 
/* 2028 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 2030 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2031 */         this.wasNullFlag = true;
/*      */ 
/* 2033 */         return null;
/*      */       }
/*      */ 
/* 2036 */       this.wasNullFlag = false;
/*      */ 
/* 2038 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 2041 */     return getNativeCharacterStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2061 */     return getCharacterStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final Reader getCharacterStreamFromString(String stringVal) throws SQLException
/*      */   {
/* 2066 */     if (stringVal != null) {
/* 2067 */       return new StringReader(stringVal);
/*      */     }
/*      */ 
/* 2070 */     return null;
/*      */   }
/*      */ 
/*      */   public java.sql.Clob getClob(int i)
/*      */     throws SQLException
/*      */   {
/* 2085 */     if (!this.isBinaryEncoded) {
/* 2086 */       String asString = getStringForClob(i);
/*      */ 
/* 2088 */       if (asString == null) {
/* 2089 */         return null;
/*      */       }
/*      */ 
/* 2092 */       return new Clob(asString, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2095 */     return getNativeClob(i);
/*      */   }
/*      */ 
/*      */   public java.sql.Clob getClob(String colName)
/*      */     throws SQLException
/*      */   {
/* 2110 */     return getClob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   private final java.sql.Clob getClobFromString(String stringVal) throws SQLException {
/* 2114 */     return new Clob(stringVal, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/* 2127 */     return 1007;
/*      */   }
/*      */ 
/*      */   public String getCursorName()
/*      */     throws SQLException
/*      */   {
/* 2156 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public Date getDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2173 */     return getDate(columnIndex, null);
/*      */   }
/*      */ 
/*      */   public Date getDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2194 */     if (this.isBinaryEncoded) {
/* 2195 */       return getNativeDate(columnIndex, cal);
/*      */     }
/*      */ 
/* 2198 */     if (!this.useFastDateParsing) {
/* 2199 */       String stringVal = getStringInternal(columnIndex, false);
/*      */ 
/* 2201 */       if (stringVal == null) {
/* 2202 */         return null;
/*      */       }
/*      */ 
/* 2205 */       return getDateFromString(stringVal, columnIndex, cal);
/*      */     }
/*      */ 
/* 2208 */     checkColumnBounds(columnIndex);
/*      */ 
/* 2210 */     int columnIndexMinusOne = columnIndex - 1;
/* 2211 */     Date tmpDate = this.thisRow.getDateFast(columnIndexMinusOne, this.connection, this, cal);
/* 2212 */     if ((this.thisRow.isNull(columnIndexMinusOne)) || (tmpDate == null))
/*      */     {
/* 2215 */       this.wasNullFlag = true;
/*      */ 
/* 2217 */       return null;
/*      */     }
/*      */ 
/* 2220 */     this.wasNullFlag = false;
/*      */ 
/* 2222 */     return tmpDate;
/*      */   }
/*      */ 
/*      */   public Date getDate(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2238 */     return getDate(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Date getDate(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2258 */     return getDate(findColumn(columnName), cal);
/*      */   }
/* 2263 */   private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException { int year = 0;
/* 2264 */     int month = 0;
/* 2265 */     int day = 0;
/*      */     SQLException sqlEx;
/*      */     try { this.wasNullFlag = false;
/*      */ 
/* 2270 */       if (stringVal == null) {
/* 2271 */         this.wasNullFlag = true;
/*      */ 
/* 2273 */         return null;
/*      */       }
/*      */ 
/* 2284 */       stringVal = stringVal.trim();
/*      */ 
/* 2286 */       if ((stringVal.equals("0")) || (stringVal.equals("0000-00-00")) || (stringVal.equals("0000-00-00 00:00:00")) || (stringVal.equals("00000000000000")) || (stringVal.equals("0")))
/*      */       {
/* 2291 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 2293 */           this.wasNullFlag = true;
/*      */ 
/* 2295 */           return null;
/* 2296 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 2298 */           throw SQLError.createSQLException("Value '" + stringVal + "' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 2305 */         return fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       }
/* 2307 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 7)
/*      */       {
/* 2309 */         switch (stringVal.length()) {
/*      */         case 19:
/*      */         case 21:
/* 2312 */           year = Integer.parseInt(stringVal.substring(0, 4));
/* 2313 */           month = Integer.parseInt(stringVal.substring(5, 7));
/* 2314 */           day = Integer.parseInt(stringVal.substring(8, 10));
/*      */ 
/* 2316 */           return fastDateCreate(targetCalendar, year, month, day);
/*      */         case 8:
/*      */         case 14:
/* 2321 */           year = Integer.parseInt(stringVal.substring(0, 4));
/* 2322 */           month = Integer.parseInt(stringVal.substring(4, 6));
/* 2323 */           day = Integer.parseInt(stringVal.substring(6, 8));
/*      */ 
/* 2325 */           return fastDateCreate(targetCalendar, year, month, day);
/*      */         case 6:
/*      */         case 10:
/*      */         case 12:
/* 2331 */           year = Integer.parseInt(stringVal.substring(0, 2));
/*      */ 
/* 2333 */           if (year <= 69) {
/* 2334 */             year += 100;
/*      */           }
/*      */ 
/* 2337 */           month = Integer.parseInt(stringVal.substring(2, 4));
/* 2338 */           day = Integer.parseInt(stringVal.substring(4, 6));
/*      */ 
/* 2340 */           return fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */         case 4:
/* 2344 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */ 
/* 2346 */           if (year <= 69) {
/* 2347 */             year += 100;
/*      */           }
/*      */ 
/* 2350 */           month = Integer.parseInt(stringVal.substring(2, 4));
/*      */ 
/* 2352 */           return fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */         case 2:
/* 2356 */           year = Integer.parseInt(stringVal.substring(0, 2));
/*      */ 
/* 2358 */           if (year <= 69) {
/* 2359 */             year += 100;
/*      */           }
/*      */ 
/* 2362 */           return fastDateCreate(targetCalendar, year + 1900, 1, 1);
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
/* 2366 */         case 20: } throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2371 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */       {
/* 2373 */         if ((stringVal.length() == 2) || (stringVal.length() == 1)) {
/* 2374 */           year = Integer.parseInt(stringVal);
/*      */ 
/* 2376 */           if (year <= 69) {
/* 2377 */             year += 100;
/*      */           }
/*      */ 
/* 2380 */           year += 1900;
/*      */         } else {
/* 2382 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */         }
/*      */ 
/* 2385 */         return fastDateCreate(targetCalendar, year, 1, 1);
/* 2386 */       }if (this.fields[(columnIndex - 1)].getMysqlType() == 11) {
/* 2387 */         return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/* 2389 */       if (stringVal.length() < 10) {
/* 2390 */         if (stringVal.length() == 8) {
/* 2391 */           return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */ 
/* 2394 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2400 */       if (stringVal.length() != 18) {
/* 2401 */         year = Integer.parseInt(stringVal.substring(0, 4));
/* 2402 */         month = Integer.parseInt(stringVal.substring(5, 7));
/* 2403 */         day = Integer.parseInt(stringVal.substring(8, 10));
/*      */       }
/*      */       else {
/* 2406 */         StringTokenizer st = new StringTokenizer(stringVal, "- ");
/*      */ 
/* 2408 */         year = Integer.parseInt(st.nextToken());
/* 2409 */         month = Integer.parseInt(st.nextToken());
/* 2410 */         day = Integer.parseInt(st.nextToken());
/*      */       }
/*      */ 
/* 2414 */       return fastDateCreate(targetCalendar, year, month, day);
/*      */     } catch (SQLException sqlEx) {
/* 2416 */       throw sqlEx;
/*      */     } catch (Exception e) {
/* 2418 */       sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */ 
/* 2423 */       sqlEx.initCause(e);
/*      */     }
/* 2425 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private TimeZone getDefaultTimeZone()
/*      */   {
/* 2430 */     if ((!this.useLegacyDatetimeCode) && (this.connection != null)) {
/* 2431 */       return this.serverTimeZoneTz;
/*      */     }
/*      */ 
/* 2434 */     return this.connection.getDefaultTimeZone();
/*      */   }
/*      */ 
/*      */   public double getDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2449 */     if (!this.isBinaryEncoded) {
/* 2450 */       return getDoubleInternal(columnIndex);
/*      */     }
/*      */ 
/* 2453 */     return getNativeDouble(columnIndex);
/*      */   }
/*      */ 
/*      */   public double getDouble(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2468 */     return getDouble(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 2473 */     return getDoubleInternal(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   protected double getDoubleInternal(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 2489 */     return getDoubleInternal(getString(colIndex), colIndex); } 
/*      */   protected double getDoubleInternal(String stringVal, int colIndex) throws SQLException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ifnonnull +5 -> 6
/*      */     //   4: dconst_0
/*      */     //   5: dreturn
/*      */     //   6: aload_1
/*      */     //   7: invokevirtual 199	java/lang/String:length	()I
/*      */     //   10: ifne +9 -> 19
/*      */     //   13: aload_0
/*      */     //   14: invokespecial 247	com/mysql/jdbc/ResultSetImpl:convertToZeroWithEmptyCheck	()I
/*      */     //   17: i2d
/*      */     //   18: dreturn
/*      */     //   19: aload_1
/*      */     //   20: invokestatic 251	java/lang/Double:parseDouble	(Ljava/lang/String;)D
/*      */     //   23: dstore_3
/*      */     //   24: aload_0
/*      */     //   25: getfield 46	com/mysql/jdbc/ResultSetImpl:useStrictFloatingPoint	Z
/*      */     //   28: ifeq +120 -> 148
/*      */     //   31: dload_3
/*      */     //   32: ldc2_w 316
/*      */     //   35: dcmpl
/*      */     //   36: ifne +10 -> 46
/*      */     //   39: ldc2_w 318
/*      */     //   42: dstore_3
/*      */     //   43: goto +105 -> 148
/*      */     //   46: dload_3
/*      */     //   47: ldc2_w 320
/*      */     //   50: dcmpl
/*      */     //   51: ifne +10 -> 61
/*      */     //   54: ldc2_w 322
/*      */     //   57: dstore_3
/*      */     //   58: goto +90 -> 148
/*      */     //   61: dload_3
/*      */     //   62: ldc2_w 324
/*      */     //   65: dcmpl
/*      */     //   66: ifne +10 -> 76
/*      */     //   69: ldc2_w 326
/*      */     //   72: dstore_3
/*      */     //   73: goto +75 -> 148
/*      */     //   76: dload_3
/*      */     //   77: ldc2_w 328
/*      */     //   80: dcmpl
/*      */     //   81: ifne +10 -> 91
/*      */     //   84: ldc2_w 330
/*      */     //   87: dstore_3
/*      */     //   88: goto +60 -> 148
/*      */     //   91: dload_3
/*      */     //   92: ldc2_w 332
/*      */     //   95: dcmpl
/*      */     //   96: ifne +10 -> 106
/*      */     //   99: ldc2_w 330
/*      */     //   102: dstore_3
/*      */     //   103: goto +45 -> 148
/*      */     //   106: dload_3
/*      */     //   107: ldc2_w 334
/*      */     //   110: dcmpl
/*      */     //   111: ifne +10 -> 121
/*      */     //   114: ldc2_w 336
/*      */     //   117: dstore_3
/*      */     //   118: goto +30 -> 148
/*      */     //   121: dload_3
/*      */     //   122: ldc2_w 338
/*      */     //   125: dcmpl
/*      */     //   126: ifne +10 -> 136
/*      */     //   129: ldc2_w 340
/*      */     //   132: dstore_3
/*      */     //   133: goto +15 -> 148
/*      */     //   136: dload_3
/*      */     //   137: ldc2_w 342
/*      */     //   140: dcmpl
/*      */     //   141: ifne +7 -> 148
/*      */     //   144: ldc2_w 336
/*      */     //   147: dstore_3
/*      */     //   148: dload_3
/*      */     //   149: dreturn
/*      */     //   150: astore_3
/*      */     //   151: aload_0
/*      */     //   152: getfield 58	com/mysql/jdbc/ResultSetImpl:fields	[Lcom/mysql/jdbc/Field;
/*      */     //   155: iload_2
/*      */     //   156: iconst_1
/*      */     //   157: isub
/*      */     //   158: aaload
/*      */     //   159: invokevirtual 209	com/mysql/jdbc/Field:getMysqlType	()I
/*      */     //   162: bipush 16
/*      */     //   164: if_icmpne +14 -> 178
/*      */     //   167: aload_0
/*      */     //   168: iload_2
/*      */     //   169: invokespecial 210	com/mysql/jdbc/ResultSetImpl:getNumericRepresentationOfSQLBitType	(I)J
/*      */     //   172: lstore 4
/*      */     //   174: lload 4
/*      */     //   176: l2d
/*      */     //   177: dreturn
/*      */     //   178: ldc_w 344
/*      */     //   181: iconst_2
/*      */     //   182: anewarray 9	java/lang/Object
/*      */     //   185: dup
/*      */     //   186: iconst_0
/*      */     //   187: aload_1
/*      */     //   188: aastore
/*      */     //   189: dup
/*      */     //   190: iconst_1
/*      */     //   191: iload_2
/*      */     //   192: invokestatic 132	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   195: aastore
/*      */     //   196: invokestatic 142	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   199: ldc 116
/*      */     //   201: aload_0
/*      */     //   202: invokevirtual 117	com/mysql/jdbc/ResultSetImpl:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   205: invokestatic 118	com/mysql/jdbc/SQLError:createSQLException	(Ljava/lang/String;Ljava/lang/String;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   208: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	5	150	java/lang/NumberFormatException
/*      */     //   6	18	150	java/lang/NumberFormatException
/*      */     //   19	149	150	java/lang/NumberFormatException } 
/* 2566 */   public int getFetchDirection() throws SQLException { synchronized (checkClosed()) {
/* 2567 */       return this.fetchDirection;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 2580 */     synchronized (checkClosed()) {
/* 2581 */       return this.fetchSize;
/*      */     }
/*      */   }
/*      */ 
/*      */   public char getFirstCharOfQuery()
/*      */   {
/*      */     try
/*      */     {
/* 2593 */       synchronized (checkClosed()) {
/* 2594 */         return this.firstCharOfQuery;
/*      */       }
/*      */     } catch (SQLException e) {
/*      */     }
/* 2597 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   public float getFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2613 */     if (!this.isBinaryEncoded) {
/* 2614 */       String val = null;
/*      */ 
/* 2616 */       val = getString(columnIndex);
/*      */ 
/* 2618 */       return getFloatFromString(val, columnIndex);
/*      */     }
/*      */ 
/* 2621 */     return getNativeFloat(columnIndex);
/*      */   }
/*      */ 
/*      */   public float getFloat(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2636 */     return getFloat(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final float getFloatFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2642 */       if (val != null) {
/* 2643 */         if (val.length() == 0) {
/* 2644 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2647 */         float f = Float.parseFloat(val);
/*      */ 
/* 2649 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2650 */           (f == 1.4E-45F) || (f == 3.4028235E+38F))) {
/* 2651 */           double valAsDouble = Double.parseDouble(val);
/*      */ 
/* 2657 */           if ((valAsDouble < 1.401298464324817E-045D - MIN_DIFF_PREC) || (valAsDouble > 3.402823466385289E+038D - MAX_DIFF_PREC))
/*      */           {
/* 2659 */             throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2665 */         return f;
/*      */       }
/*      */ 
/* 2668 */       return 0.0F;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2671 */         Double valueAsDouble = new Double(val);
/* 2672 */         float valueAsFloat = valueAsDouble.floatValue();
/*      */ 
/* 2674 */         if (this.jdbcCompliantTruncationForReads)
/*      */         {
/* 2676 */           if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == (1.0F / -1.0F))) || (valueAsFloat == (1.0F / 1.0F)))
/*      */           {
/* 2679 */             throwRangeException(valueAsDouble.toString(), columnIndex, 6);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2684 */         return valueAsFloat;
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2689 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public int getInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2710 */     checkRowPos();
/*      */ 
/* 2712 */     if (!this.isBinaryEncoded) {
/* 2713 */       int columnIndexMinusOne = columnIndex - 1;
/* 2714 */       if (this.useFastIntParsing) {
/* 2715 */         checkColumnBounds(columnIndex);
/*      */ 
/* 2717 */         if (this.thisRow.isNull(columnIndexMinusOne))
/* 2718 */           this.wasNullFlag = true;
/*      */         else {
/* 2720 */           this.wasNullFlag = false;
/*      */         }
/*      */ 
/* 2723 */         if (this.wasNullFlag) {
/* 2724 */           return 0;
/*      */         }
/*      */ 
/* 2727 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2728 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2731 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */ 
/* 2734 */         if (!needsFullParse) {
/*      */           try {
/* 2736 */             return getIntWithOverflowCheck(columnIndexMinusOne);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 2740 */               return parseIntAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
/*      */             }
/*      */             catch (NumberFormatException valueAsLong)
/*      */             {
/* 2749 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2750 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 2752 */                 if ((this.connection.getJdbcCompliantTruncationForReads()) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */                 {
/* 2755 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */                 }
/*      */ 
/* 2760 */                 return (int)valueAsLong;
/*      */               }
/*      */ 
/* 2763 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2779 */       String val = null;
/*      */       try
/*      */       {
/* 2782 */         val = getString(columnIndex);
/*      */ 
/* 2784 */         if (val != null) {
/* 2785 */           if (val.length() == 0) {
/* 2786 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 2789 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */           {
/* 2791 */             int intVal = Integer.parseInt(val);
/*      */ 
/* 2793 */             checkForIntegerTruncation(columnIndexMinusOne, null, intVal);
/*      */ 
/* 2795 */             return intVal;
/*      */           }
/*      */ 
/* 2799 */           int intVal = parseIntAsDouble(columnIndex, val);
/*      */ 
/* 2801 */           checkForIntegerTruncation(columnIndex, null, intVal);
/*      */ 
/* 2803 */           return intVal;
/*      */         }
/*      */ 
/* 2806 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2809 */           return parseIntAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException valueAsLong)
/*      */         {
/* 2814 */           if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2815 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 2817 */             if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */             {
/* 2819 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */ 
/* 2823 */             return (int)valueAsLong;
/*      */           }
/*      */ 
/* 2826 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2836 */     return getNativeInt(columnIndex);
/*      */   }
/*      */ 
/*      */   public int getInt(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2851 */     return getInt(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final int getIntFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2857 */       if (val != null)
/*      */       {
/* 2859 */         if (val.length() == 0) {
/* 2860 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2863 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/* 2873 */           val = val.trim();
/*      */ 
/* 2875 */           int valueAsInt = Integer.parseInt(val);
/*      */ 
/* 2877 */           if ((this.jdbcCompliantTruncationForReads) && (
/* 2878 */             (valueAsInt == -2147483648) || (valueAsInt == 2147483647)))
/*      */           {
/* 2880 */             long valueAsLong = Long.parseLong(val);
/*      */ 
/* 2882 */             if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
/*      */             {
/* 2884 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2891 */           return valueAsInt;
/*      */         }
/*      */ 
/* 2896 */         double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 2898 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2899 */           (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */         {
/* 2901 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/* 2906 */         return (int)valueAsDouble;
/*      */       }
/*      */ 
/* 2909 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2912 */         double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 2914 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2915 */           (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */         {
/* 2917 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/* 2922 */         return (int)valueAsDouble;
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2927 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public long getLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2947 */     return getLong(columnIndex, true);
/*      */   }
/*      */ 
/*      */   private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
/* 2951 */     if (!this.isBinaryEncoded) {
/* 2952 */       checkRowPos();
/*      */ 
/* 2954 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 2956 */       if (this.useFastIntParsing)
/*      */       {
/* 2958 */         checkColumnBounds(columnIndex);
/*      */ 
/* 2960 */         if (this.thisRow.isNull(columnIndexMinusOne))
/* 2961 */           this.wasNullFlag = true;
/*      */         else {
/* 2963 */           this.wasNullFlag = false;
/*      */         }
/*      */ 
/* 2966 */         if (this.wasNullFlag) {
/* 2967 */           return 0L;
/*      */         }
/*      */ 
/* 2970 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2971 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2974 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */ 
/* 2976 */         if (!needsFullParse) {
/*      */           try {
/* 2978 */             return getLongWithOverflowCheck(columnIndexMinusOne, overflowCheck);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 2982 */               return parseLongAsDouble(columnIndexMinusOne, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection));
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/* 2991 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2992 */                 return getNumericRepresentationOfSQLBitType(columnIndex);
/*      */               }
/*      */ 
/* 2995 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getCharacterSet(), this.connection) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3009 */       String val = null;
/*      */       try
/*      */       {
/* 3012 */         val = getString(columnIndex);
/*      */ 
/* 3014 */         if (val != null) {
/* 3015 */           if (val.length() == 0) {
/* 3016 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 3019 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 3020 */             return parseLongWithOverflowCheck(columnIndexMinusOne, null, val, overflowCheck);
/*      */           }
/*      */ 
/* 3025 */           return parseLongAsDouble(columnIndexMinusOne, val);
/*      */         }
/*      */ 
/* 3028 */         return 0L;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 3031 */           return parseLongAsDouble(columnIndexMinusOne, val);
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 3036 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3044 */     return getNativeLong(columnIndex, overflowCheck, true);
/*      */   }
/*      */ 
/*      */   public long getLong(String columnName)
/*      */     throws SQLException
/*      */   {
/* 3059 */     return getLong(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final long getLongFromString(String val, int columnIndexZeroBased) throws SQLException
/*      */   {
/*      */     try {
/* 3065 */       if (val != null)
/*      */       {
/* 3067 */         if (val.length() == 0) {
/* 3068 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 3071 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 3072 */           return parseLongWithOverflowCheck(columnIndexZeroBased, null, val, true);
/*      */         }
/*      */ 
/* 3076 */         return parseLongAsDouble(columnIndexZeroBased, val);
/*      */       }
/*      */ 
/* 3079 */       return 0L;
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/*      */       try {
/* 3083 */         return parseLongAsDouble(columnIndexZeroBased, val);
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 3088 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + (columnIndexZeroBased + 1), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 3107 */     checkClosed();
/*      */ 
/* 3109 */     return new ResultSetMetaData(this.fields, this.connection.getUseOldAliasMetadataBehavior(), getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected Array getNativeArray(int i)
/*      */     throws SQLException
/*      */   {
/* 3127 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3157 */     checkRowPos();
/*      */ 
/* 3159 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3178 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3180 */     int scale = this.fields[(columnIndex - 1)].getDecimals();
/*      */ 
/* 3182 */     return getNativeBigDecimal(columnIndex, scale);
/*      */   }
/*      */ 
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 3201 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3203 */     String stringVal = null;
/*      */ 
/* 3205 */     Field f = this.fields[(columnIndex - 1)];
/*      */ 
/* 3207 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3209 */     if (value == null) {
/* 3210 */       this.wasNullFlag = true;
/*      */ 
/* 3212 */       return null;
/*      */     }
/*      */ 
/* 3215 */     this.wasNullFlag = false;
/*      */ 
/* 3217 */     switch (f.getSQLType()) {
/*      */     case 2:
/*      */     case 3:
/* 3220 */       stringVal = StringUtils.toAsciiString((byte[])(byte[])value);
/*      */ 
/* 3222 */       break;
/*      */     default:
/* 3224 */       stringVal = getNativeString(columnIndex);
/*      */     }
/*      */ 
/* 3227 */     return getBigDecimalFromString(stringVal, columnIndex, scale);
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3249 */     checkRowPos();
/*      */ 
/* 3251 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 3253 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3254 */       this.wasNullFlag = true;
/*      */ 
/* 3256 */       return null;
/*      */     }
/*      */ 
/* 3259 */     this.wasNullFlag = false;
/*      */ 
/* 3261 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */     case -7:
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case 2004:
/* 3267 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 3270 */     byte[] b = getNativeBytes(columnIndex, false);
/*      */ 
/* 3272 */     if (b != null) {
/* 3273 */       return new ByteArrayInputStream(b);
/*      */     }
/*      */ 
/* 3276 */     return null;
/*      */   }
/*      */ 
/*      */   protected java.sql.Blob getNativeBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3291 */     checkRowPos();
/*      */ 
/* 3293 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3295 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3297 */     if (value == null)
/* 3298 */       this.wasNullFlag = true;
/*      */     else {
/* 3300 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 3303 */     if (this.wasNullFlag) {
/* 3304 */       return null;
/*      */     }
/*      */ 
/* 3307 */     int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
/*      */ 
/* 3309 */     byte[] dataAsBytes = null;
/*      */ 
/* 3311 */     switch (mysqlType) {
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3316 */       dataAsBytes = (byte[])(byte[])value;
/* 3317 */       break;
/*      */     default:
/* 3320 */       dataAsBytes = getNativeBytes(columnIndex, false);
/*      */     }
/*      */ 
/* 3323 */     if (!this.connection.getEmulateLocators()) {
/* 3324 */       return new Blob(dataAsBytes, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3327 */     return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public static boolean arraysEqual(byte[] left, byte[] right) {
/* 3331 */     if (left == null) {
/* 3332 */       return right == null;
/*      */     }
/* 3334 */     if (right == null) {
/* 3335 */       return false;
/*      */     }
/* 3337 */     if (left.length != right.length) {
/* 3338 */       return false;
/*      */     }
/* 3340 */     for (int i = 0; i < left.length; i++) {
/* 3341 */       if (left[i] != right[i]) {
/* 3342 */         return false;
/*      */       }
/*      */     }
/* 3345 */     return true;
/*      */   }
/*      */ 
/*      */   protected byte getNativeByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3360 */     return getNativeByte(columnIndex, true);
/*      */   }
/*      */ 
/*      */   protected byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException {
/* 3364 */     checkRowPos();
/*      */ 
/* 3366 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3368 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3370 */     if (value == null) {
/* 3371 */       this.wasNullFlag = true;
/*      */ 
/* 3373 */       return 0;
/*      */     }
/*      */ 
/* 3376 */     this.wasNullFlag = false;
/*      */ 
/* 3378 */     columnIndex--;
/*      */ 
/* 3380 */     Field field = this.fields[columnIndex];
/*      */     long valueAsLong;
/*      */     short valueAsShort;
/* 3382 */     switch (field.getMysqlType()) {
/*      */     case 16:
/* 3384 */       valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */ 
/* 3386 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 3389 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3393 */       return (byte)(int)valueAsLong;
/*      */     case 1:
/* 3395 */       byte valueAsByte = ((byte[])(byte[])value)[0];
/*      */ 
/* 3397 */       if (!field.isUnsigned()) {
/* 3398 */         return valueAsByte;
/*      */       }
/*      */ 
/* 3401 */       valueAsShort = valueAsByte >= 0 ? (short)valueAsByte : (short)(valueAsByte + 256);
/*      */ 
/* 3404 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && 
/* 3405 */         (valueAsShort > 127)) {
/* 3406 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3411 */       return (byte)valueAsShort;
/*      */     case 2:
/*      */     case 13:
/* 3415 */       valueAsShort = getNativeShort(columnIndex + 1);
/*      */ 
/* 3417 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3418 */         (valueAsShort < -128) || (valueAsShort > 127)))
/*      */       {
/* 3420 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3425 */       return (byte)valueAsShort;
/*      */     case 3:
/*      */     case 9:
/* 3428 */       int valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */ 
/* 3430 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3431 */         (valueAsInt < -128) || (valueAsInt > 127))) {
/* 3432 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3437 */       return (byte)valueAsInt;
/*      */     case 4:
/* 3440 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */ 
/* 3442 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3443 */         (valueAsFloat < -128.0F) || (valueAsFloat > 127.0F)))
/*      */       {
/* 3446 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3451 */       return (byte)(int)valueAsFloat;
/*      */     case 5:
/* 3454 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 3456 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3457 */         (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
/*      */       {
/* 3459 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3464 */       return (byte)(int)valueAsDouble;
/*      */     case 8:
/* 3467 */       valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */ 
/* 3469 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3470 */         (valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 3472 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3477 */       return (byte)(int)valueAsLong;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 3480 */     case 15: } if (this.useUsageAdvisor) {
/* 3481 */       issueConversionViaParsingWarning("getByte()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3491 */     return getByteFromString(getNativeString(columnIndex + 1), columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected byte[] getNativeBytes(int columnIndex, boolean noConversion)
/*      */     throws SQLException
/*      */   {
/* 3513 */     checkRowPos();
/*      */ 
/* 3515 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3517 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 3519 */     if (value == null)
/* 3520 */       this.wasNullFlag = true;
/*      */     else {
/* 3522 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 3525 */     if (this.wasNullFlag) {
/* 3526 */       return null;
/*      */     }
/*      */ 
/* 3529 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 3531 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 3535 */     if (noConversion) {
/* 3536 */       mysqlType = 252;
/*      */     }
/*      */ 
/* 3539 */     switch (mysqlType) {
/*      */     case 16:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3545 */       return (byte[])(byte[])value;
/*      */     case 15:
/*      */     case 253:
/*      */     case 254:
/* 3550 */       if (!(value instanceof byte[])) break;
/* 3551 */       return (byte[])(byte[])value;
/*      */     }
/*      */ 
/* 3555 */     int sqlType = field.getSQLType();
/*      */ 
/* 3557 */     if ((sqlType == -3) || (sqlType == -2)) {
/* 3558 */       return (byte[])(byte[])value;
/*      */     }
/*      */ 
/* 3561 */     return getBytesFromString(getNativeString(columnIndex));
/*      */   }
/*      */ 
/*      */   protected Reader getNativeCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3582 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 3584 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/*      */     case 2005:
/* 3589 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3590 */         this.wasNullFlag = true;
/*      */ 
/* 3592 */         return null;
/*      */       }
/*      */ 
/* 3595 */       this.wasNullFlag = false;
/*      */ 
/* 3597 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     }
/*      */ 
/* 3600 */     String asString = getStringForClob(columnIndex);
/*      */ 
/* 3602 */     if (asString == null) {
/* 3603 */       return null;
/*      */     }
/*      */ 
/* 3606 */     return getCharacterStreamFromString(asString);
/*      */   }
/*      */ 
/*      */   protected java.sql.Clob getNativeClob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3621 */     String stringVal = getStringForClob(columnIndex);
/*      */ 
/* 3623 */     if (stringVal == null) {
/* 3624 */       return null;
/*      */     }
/*      */ 
/* 3627 */     return getClobFromString(stringVal);
/*      */   }
/*      */ 
/*      */   private String getNativeConvertToString(int columnIndex, Field field)
/*      */     throws SQLException
/*      */   {
/* 3633 */     synchronized (checkClosed())
/*      */     {
/* 3635 */       int sqlType = field.getSQLType();
/* 3636 */       int mysqlType = field.getMysqlType();
/*      */       int intVal;
/*      */       long longVal;
/* 3638 */       switch (sqlType) {
/*      */       case -7:
/* 3640 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       case 16:
/* 3642 */         boolean booleanVal = getBoolean(columnIndex);
/*      */ 
/* 3644 */         if (this.wasNullFlag) {
/* 3645 */           return null;
/*      */         }
/*      */ 
/* 3648 */         return String.valueOf(booleanVal);
/*      */       case -6:
/* 3651 */         byte tinyintVal = getNativeByte(columnIndex, false);
/*      */ 
/* 3653 */         if (this.wasNullFlag) {
/* 3654 */           return null;
/*      */         }
/*      */ 
/* 3657 */         if ((!field.isUnsigned()) || (tinyintVal >= 0)) {
/* 3658 */           return String.valueOf(tinyintVal);
/*      */         }
/*      */ 
/* 3661 */         short unsignedTinyVal = (short)(tinyintVal & 0xFF);
/*      */ 
/* 3663 */         return String.valueOf(unsignedTinyVal);
/*      */       case 5:
/* 3667 */         intVal = getNativeInt(columnIndex, false);
/*      */ 
/* 3669 */         if (this.wasNullFlag) {
/* 3670 */           return null;
/*      */         }
/*      */ 
/* 3673 */         if ((!field.isUnsigned()) || (intVal >= 0)) {
/* 3674 */           return String.valueOf(intVal);
/*      */         }
/*      */ 
/* 3677 */         intVal &= 65535;
/*      */ 
/* 3679 */         return String.valueOf(intVal);
/*      */       case 4:
/* 3682 */         intVal = getNativeInt(columnIndex, false);
/*      */ 
/* 3684 */         if (this.wasNullFlag) {
/* 3685 */           return null;
/*      */         }
/*      */ 
/* 3688 */         if ((!field.isUnsigned()) || (intVal >= 0) || (field.getMysqlType() == 9))
/*      */         {
/* 3691 */           return String.valueOf(intVal);
/*      */         }
/*      */ 
/* 3694 */         longVal = intVal & 0xFFFFFFFF;
/*      */ 
/* 3696 */         return String.valueOf(longVal);
/*      */       case -5:
/* 3700 */         if (!field.isUnsigned()) {
/* 3701 */           longVal = getNativeLong(columnIndex, false, true);
/*      */ 
/* 3703 */           if (this.wasNullFlag) {
/* 3704 */             return null;
/*      */           }
/*      */ 
/* 3707 */           return String.valueOf(longVal);
/*      */         }
/*      */ 
/* 3710 */         long longVal = getNativeLong(columnIndex, false, false);
/*      */ 
/* 3712 */         if (this.wasNullFlag) {
/* 3713 */           return null;
/*      */         }
/*      */ 
/* 3716 */         return String.valueOf(convertLongToUlong(longVal));
/*      */       case 7:
/* 3718 */         float floatVal = getNativeFloat(columnIndex);
/*      */ 
/* 3720 */         if (this.wasNullFlag) {
/* 3721 */           return null;
/*      */         }
/*      */ 
/* 3724 */         return String.valueOf(floatVal);
/*      */       case 6:
/*      */       case 8:
/* 3728 */         double doubleVal = getNativeDouble(columnIndex);
/*      */ 
/* 3730 */         if (this.wasNullFlag) {
/* 3731 */           return null;
/*      */         }
/*      */ 
/* 3734 */         return String.valueOf(doubleVal);
/*      */       case 2:
/*      */       case 3:
/* 3738 */         String stringVal = StringUtils.toAsciiString(this.thisRow.getColumnValue(columnIndex - 1));
/*      */ 
/* 3743 */         if (stringVal != null) {
/* 3744 */           this.wasNullFlag = false;
/*      */ 
/* 3746 */           if (stringVal.length() == 0) {
/* 3747 */             BigDecimal val = new BigDecimal(0);
/*      */ 
/* 3749 */             return val.toString();
/*      */           }BigDecimal val;
/*      */           try {
/* 3753 */             val = new BigDecimal(stringVal);
/*      */           } catch (NumberFormatException ex) {
/* 3755 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 3762 */           return val.toString();
/*      */         }
/*      */ 
/* 3765 */         this.wasNullFlag = true;
/*      */ 
/* 3767 */         return null;
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 3773 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/* 3778 */         if (!field.isBlob())
/* 3779 */           return extractStringFromNativeColumn(columnIndex, mysqlType);
/* 3780 */         if (!field.isBinary()) {
/* 3781 */           return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */         }
/* 3783 */         byte[] data = getBytes(columnIndex);
/* 3784 */         Object obj = data;
/*      */ 
/* 3786 */         if ((data != null) && (data.length >= 2)) {
/* 3787 */           if ((data[0] == -84) && (data[1] == -19)) {
/*      */             try
/*      */             {
/* 3790 */               ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */ 
/* 3792 */               ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */ 
/* 3794 */               obj = objIn.readObject();
/* 3795 */               objIn.close();
/* 3796 */               bytesIn.close();
/*      */             } catch (ClassNotFoundException cnfe) {
/* 3798 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/*      */             }
/*      */             catch (IOException ex)
/*      */             {
/* 3805 */               obj = data;
/*      */             }
/*      */           }
/*      */ 
/* 3809 */           return obj.toString();
/*      */         }
/*      */ 
/* 3812 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       case 91:
/* 3818 */         if (mysqlType == 13) {
/* 3819 */           short shortVal = getNativeShort(columnIndex);
/*      */ 
/* 3821 */           if (!this.connection.getYearIsDateType())
/*      */           {
/* 3823 */             if (this.wasNullFlag) {
/* 3824 */               return null;
/*      */             }
/*      */ 
/* 3827 */             return String.valueOf(shortVal);
/*      */           }
/*      */ 
/* 3830 */           if (field.getLength() == 2L)
/*      */           {
/* 3832 */             if (shortVal <= 69) {
/* 3833 */               shortVal = (short)(shortVal + 100);
/*      */             }
/*      */ 
/* 3836 */             shortVal = (short)(shortVal + 1900);
/*      */           }
/*      */ 
/* 3839 */           return fastDateCreate(null, shortVal, 1, 1).toString();
/*      */         }
/*      */ 
/* 3843 */         if (this.connection.getNoDatetimeStringSync()) {
/* 3844 */           byte[] asBytes = getNativeBytes(columnIndex, true);
/*      */ 
/* 3846 */           if (asBytes == null) {
/* 3847 */             return null;
/*      */           }
/*      */ 
/* 3850 */           if (asBytes.length == 0)
/*      */           {
/* 3852 */             return "0000-00-00";
/*      */           }
/*      */ 
/* 3855 */           int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
/*      */ 
/* 3857 */           int month = asBytes[2];
/* 3858 */           int day = asBytes[3];
/*      */ 
/* 3860 */           if ((year == 0) && (month == 0) && (day == 0)) {
/* 3861 */             return "0000-00-00";
/*      */           }
/*      */         }
/*      */ 
/* 3865 */         Date dt = getNativeDate(columnIndex);
/*      */ 
/* 3867 */         if (dt == null) {
/* 3868 */           return null;
/*      */         }
/*      */ 
/* 3871 */         return String.valueOf(dt);
/*      */       case 92:
/* 3874 */         Time tm = getNativeTime(columnIndex, null, this.defaultTimeZone, false);
/*      */ 
/* 3876 */         if (tm == null) {
/* 3877 */           return null;
/*      */         }
/*      */ 
/* 3880 */         return String.valueOf(tm);
/*      */       case 93:
/* 3883 */         if (this.connection.getNoDatetimeStringSync()) {
/* 3884 */           byte[] asBytes = getNativeBytes(columnIndex, true);
/*      */ 
/* 3886 */           if (asBytes == null) {
/* 3887 */             return null;
/*      */           }
/*      */ 
/* 3890 */           if (asBytes.length == 0)
/*      */           {
/* 3892 */             return "0000-00-00 00:00:00";
/*      */           }
/*      */ 
/* 3895 */           int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
/*      */ 
/* 3897 */           int month = asBytes[2];
/* 3898 */           int day = asBytes[3];
/*      */ 
/* 3900 */           if ((year == 0) && (month == 0) && (day == 0)) {
/* 3901 */             return "0000-00-00 00:00:00";
/*      */           }
/*      */         }
/*      */ 
/* 3905 */         Timestamp tstamp = getNativeTimestamp(columnIndex, null, this.defaultTimeZone, false);
/*      */ 
/* 3908 */         if (tstamp == null) {
/* 3909 */           return null;
/*      */         }
/*      */ 
/* 3912 */         String result = String.valueOf(tstamp);
/*      */ 
/* 3914 */         if (!this.connection.getNoDatetimeStringSync()) {
/* 3915 */           return result;
/*      */         }
/*      */ 
/* 3918 */         if (!result.endsWith(".0")) break;
/* 3919 */         return result.substring(0, result.length() - 2);
/*      */       }
/*      */ 
/* 3923 */       return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3940 */     return getNativeDate(columnIndex, null);
/*      */   }
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3961 */     checkRowPos();
/* 3962 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3964 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 3966 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */ 
/* 3968 */     Date dateToReturn = null;
/*      */ 
/* 3970 */     if (mysqlType == 10)
/*      */     {
/* 3972 */       dateToReturn = this.thisRow.getNativeDate(columnIndexMinusOne, this.connection, this, cal);
/*      */     }
/*      */     else {
/* 3975 */       TimeZone tz = cal != null ? cal.getTimeZone() : getDefaultTimeZone();
/*      */ 
/* 3978 */       boolean rollForward = (tz != null) && (!tz.equals(getDefaultTimeZone()));
/*      */ 
/* 3980 */       dateToReturn = (Date)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 91, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 3992 */     if (dateToReturn == null)
/*      */     {
/* 3994 */       this.wasNullFlag = true;
/*      */ 
/* 3996 */       return null;
/*      */     }
/*      */ 
/* 3999 */     this.wasNullFlag = false;
/*      */ 
/* 4001 */     return dateToReturn;
/*      */   }
/*      */ 
/*      */   Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
/* 4005 */     if (this.useUsageAdvisor) {
/* 4006 */       issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 10 });
/*      */     }
/*      */ 
/* 4011 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 4013 */     return getDateFromString(stringVal, columnIndex, null);
/*      */   }
/*      */ 
/*      */   protected double getNativeDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4028 */     checkRowPos();
/* 4029 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4031 */     columnIndex--;
/*      */ 
/* 4033 */     if (this.thisRow.isNull(columnIndex)) {
/* 4034 */       this.wasNullFlag = true;
/*      */ 
/* 4036 */       return 0.0D;
/*      */     }
/*      */ 
/* 4039 */     this.wasNullFlag = false;
/*      */ 
/* 4041 */     Field f = this.fields[columnIndex];
/*      */ 
/* 4043 */     switch (f.getMysqlType()) {
/*      */     case 5:
/* 4045 */       return this.thisRow.getNativeDouble(columnIndex);
/*      */     case 1:
/* 4047 */       if (!f.isUnsigned()) {
/* 4048 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 4051 */       return getNativeShort(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 4054 */       if (!f.isUnsigned()) {
/* 4055 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 4058 */       return getNativeInt(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 4061 */       if (!f.isUnsigned()) {
/* 4062 */         return getNativeInt(columnIndex + 1);
/*      */       }
/*      */ 
/* 4065 */       return getNativeLong(columnIndex + 1);
/*      */     case 8:
/* 4067 */       long valueAsLong = getNativeLong(columnIndex + 1);
/*      */ 
/* 4069 */       if (!f.isUnsigned()) {
/* 4070 */         return valueAsLong;
/*      */       }
/*      */ 
/* 4073 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4077 */       return asBigInt.doubleValue();
/*      */     case 4:
/* 4079 */       return getNativeFloat(columnIndex + 1);
/*      */     case 16:
/* 4081 */       return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4083 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4085 */     if (this.useUsageAdvisor) {
/* 4086 */       issueConversionViaParsingWarning("getDouble()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4096 */     return getDoubleFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected float getNativeFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4112 */     checkRowPos();
/* 4113 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4115 */     columnIndex--;
/*      */ 
/* 4117 */     if (this.thisRow.isNull(columnIndex)) {
/* 4118 */       this.wasNullFlag = true;
/*      */ 
/* 4120 */       return 0.0F;
/*      */     }
/*      */ 
/* 4123 */     this.wasNullFlag = false;
/*      */ 
/* 4125 */     Field f = this.fields[columnIndex];
/*      */     long valueAsLong;
/* 4127 */     switch (f.getMysqlType()) {
/*      */     case 16:
/* 4129 */       valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */ 
/* 4131 */       return (float)valueAsLong;
/*      */     case 5:
/* 4138 */       Double valueAsDouble = new Double(getNativeDouble(columnIndex + 1));
/*      */ 
/* 4140 */       float valueAsFloat = valueAsDouble.floatValue();
/*      */ 
/* 4142 */       if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == (1.0F / -1.0F))) || (valueAsFloat == (1.0F / 1.0F)))
/*      */       {
/* 4145 */         throwRangeException(valueAsDouble.toString(), columnIndex + 1, 6);
/*      */       }
/*      */ 
/* 4149 */       return (float)getNativeDouble(columnIndex + 1);
/*      */     case 1:
/* 4151 */       if (!f.isUnsigned()) {
/* 4152 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 4155 */       return getNativeShort(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 4158 */       if (!f.isUnsigned()) {
/* 4159 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 4162 */       return getNativeInt(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 4165 */       if (!f.isUnsigned()) {
/* 4166 */         return getNativeInt(columnIndex + 1);
/*      */       }
/*      */ 
/* 4169 */       return (float)getNativeLong(columnIndex + 1);
/*      */     case 8:
/* 4171 */       valueAsLong = getNativeLong(columnIndex + 1);
/*      */ 
/* 4173 */       if (!f.isUnsigned()) {
/* 4174 */         return (float)valueAsLong;
/*      */       }
/*      */ 
/* 4177 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4181 */       return asBigInt.floatValue();
/*      */     case 4:
/* 4184 */       return this.thisRow.getNativeFloat(columnIndex);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4187 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4189 */     if (this.useUsageAdvisor) {
/* 4190 */       issueConversionViaParsingWarning("getFloat()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4200 */     return getFloatFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected int getNativeInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4216 */     return getNativeInt(columnIndex, true);
/*      */   }
/*      */ 
/*      */   protected int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException {
/* 4220 */     checkRowPos();
/* 4221 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4223 */     columnIndex--;
/*      */ 
/* 4225 */     if (this.thisRow.isNull(columnIndex)) {
/* 4226 */       this.wasNullFlag = true;
/*      */ 
/* 4228 */       return 0;
/*      */     }
/*      */ 
/* 4231 */     this.wasNullFlag = false;
/*      */ 
/* 4233 */     Field f = this.fields[columnIndex];
/*      */     long valueAsLong;
/*      */     double valueAsDouble;
/* 4235 */     switch (f.getMysqlType()) {
/*      */     case 16:
/* 4237 */       valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */ 
/* 4239 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */       {
/* 4242 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4246 */       return (short)(int)valueAsLong;
/*      */     case 1:
/* 4248 */       byte tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */ 
/* 4250 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 4251 */         return tinyintVal;
/*      */       }
/*      */ 
/* 4254 */       return tinyintVal + 256;
/*      */     case 2:
/*      */     case 13:
/* 4257 */       short asShort = getNativeShort(columnIndex + 1, false);
/*      */ 
/* 4259 */       if ((!f.isUnsigned()) || (asShort >= 0)) {
/* 4260 */         return asShort;
/*      */       }
/*      */ 
/* 4263 */       return asShort + 65536;
/*      */     case 3:
/*      */     case 9:
/* 4267 */       int valueAsInt = this.thisRow.getNativeInt(columnIndex);
/*      */ 
/* 4269 */       if (!f.isUnsigned()) {
/* 4270 */         return valueAsInt;
/*      */       }
/*      */ 
/* 4273 */       valueAsLong = valueAsInt >= 0 ? valueAsInt : valueAsInt + 4294967296L;
/*      */ 
/* 4276 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 2147483647L))
/*      */       {
/* 4278 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4282 */       return (int)valueAsLong;
/*      */     case 8:
/* 4284 */       valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */ 
/* 4286 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4287 */         (valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */       {
/* 4289 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4294 */       return (int)valueAsLong;
/*      */     case 5:
/* 4296 */       valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 4298 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4299 */         (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */       {
/* 4301 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4306 */       return (int)valueAsDouble;
/*      */     case 4:
/* 4308 */       valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */ 
/* 4310 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4311 */         (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */       {
/* 4313 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 4318 */       return (int)valueAsDouble;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4321 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4323 */     if (this.useUsageAdvisor) {
/* 4324 */       issueConversionViaParsingWarning("getInt()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4334 */     return getIntFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected long getNativeLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4350 */     return getNativeLong(columnIndex, true, true);
/*      */   }
/*      */ 
/*      */   protected long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException
/*      */   {
/* 4355 */     checkRowPos();
/* 4356 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4358 */     columnIndex--;
/*      */ 
/* 4360 */     if (this.thisRow.isNull(columnIndex)) {
/* 4361 */       this.wasNullFlag = true;
/*      */ 
/* 4363 */       return 0L;
/*      */     }
/*      */ 
/* 4366 */     this.wasNullFlag = false;
/*      */ 
/* 4368 */     Field f = this.fields[columnIndex];
/*      */     double valueAsDouble;
/* 4370 */     switch (f.getMysqlType()) {
/*      */     case 16:
/* 4372 */       return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     case 1:
/* 4374 */       if (!f.isUnsigned()) {
/* 4375 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 4378 */       return getNativeInt(columnIndex + 1);
/*      */     case 2:
/* 4380 */       if (!f.isUnsigned()) {
/* 4381 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 4384 */       return getNativeInt(columnIndex + 1, false);
/*      */     case 13:
/* 4387 */       return getNativeShort(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 4390 */       int asInt = getNativeInt(columnIndex + 1, false);
/*      */ 
/* 4392 */       if ((!f.isUnsigned()) || (asInt >= 0)) {
/* 4393 */         return asInt;
/*      */       }
/*      */ 
/* 4396 */       return asInt + 4294967296L;
/*      */     case 8:
/* 4398 */       long valueAsLong = this.thisRow.getNativeLong(columnIndex);
/*      */ 
/* 4400 */       if ((!f.isUnsigned()) || (!expandUnsignedLong)) {
/* 4401 */         return valueAsLong;
/*      */       }
/*      */ 
/* 4404 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4406 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(9223372036854775807L))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(-9223372036854775808L))) < 0)))
/*      */       {
/* 4409 */         throwRangeException(asBigInt.toString(), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 4413 */       return getLongFromString(asBigInt.toString(), columnIndex);
/*      */     case 5:
/* 4416 */       valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 4418 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4419 */         (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
/*      */       {
/* 4421 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 4426 */       return ()valueAsDouble;
/*      */     case 4:
/* 4428 */       valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */ 
/* 4430 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4431 */         (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
/*      */       {
/* 4433 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 4438 */       return ()valueAsDouble;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 14:
/* 4440 */     case 15: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4442 */     if (this.useUsageAdvisor) {
/* 4443 */       issueConversionViaParsingWarning("getLong()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4453 */     return getLongFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected Ref getNativeRef(int i)
/*      */     throws SQLException
/*      */   {
/* 4471 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected short getNativeShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4486 */     return getNativeShort(columnIndex, true);
/*      */   }
/*      */ 
/*      */   protected short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException {
/* 4490 */     checkRowPos();
/* 4491 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4493 */     columnIndex--;
/*      */ 
/* 4496 */     if (this.thisRow.isNull(columnIndex)) {
/* 4497 */       this.wasNullFlag = true;
/*      */ 
/* 4499 */       return 0;
/*      */     }
/*      */ 
/* 4502 */     this.wasNullFlag = false;
/*      */ 
/* 4504 */     Field f = this.fields[columnIndex];
/*      */     int valueAsInt;
/*      */     long valueAsLong;
/* 4506 */     switch (f.getMysqlType())
/*      */     {
/*      */     case 1:
/* 4509 */       byte tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */ 
/* 4511 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 4512 */         return (short)tinyintVal;
/*      */       }
/*      */ 
/* 4515 */       return (short)(tinyintVal + 256);
/*      */     case 2:
/*      */     case 13:
/* 4519 */       short asShort = this.thisRow.getNativeShort(columnIndex);
/*      */ 
/* 4521 */       if (!f.isUnsigned()) {
/* 4522 */         return asShort;
/*      */       }
/*      */ 
/* 4525 */       valueAsInt = asShort & 0xFFFF;
/*      */ 
/* 4527 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767))
/*      */       {
/* 4529 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4533 */       return (short)valueAsInt;
/*      */     case 3:
/*      */     case 9:
/* 4536 */       if (!f.isUnsigned()) {
/* 4537 */         valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */ 
/* 4539 */         if (((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767)) || (valueAsInt < -32768))
/*      */         {
/* 4542 */           throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */         }
/*      */ 
/* 4546 */         return (short)valueAsInt;
/*      */       }
/*      */ 
/* 4549 */       valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */ 
/* 4551 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 32767L))
/*      */       {
/* 4553 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4557 */       return (short)(int)valueAsLong;
/*      */     case 8:
/* 4560 */       valueAsLong = getNativeLong(columnIndex + 1, false, false);
/*      */ 
/* 4562 */       if (!f.isUnsigned()) {
/* 4563 */         if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4564 */           (valueAsLong < -32768L) || (valueAsLong > 32767L)))
/*      */         {
/* 4566 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */         }
/*      */ 
/* 4571 */         return (short)(int)valueAsLong;
/*      */       }
/*      */ 
/* 4574 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */ 
/* 4576 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(32767))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(-32768))) < 0)))
/*      */       {
/* 4579 */         throwRangeException(asBigInt.toString(), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4583 */       return (short)getIntFromString(asBigInt.toString(), columnIndex + 1);
/*      */     case 5:
/* 4586 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 4588 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4589 */         (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
/*      */       {
/* 4591 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4596 */       return (short)(int)valueAsDouble;
/*      */     case 4:
/* 4598 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */ 
/* 4600 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4601 */         (valueAsFloat < -32768.0F) || (valueAsFloat > 32767.0F)))
/*      */       {
/* 4603 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, 5);
/*      */       }
/*      */ 
/* 4608 */       return (short)(int)valueAsFloat;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 4610 */     case 12: } String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 4612 */     if (this.useUsageAdvisor) {
/* 4613 */       issueConversionViaParsingWarning("getShort()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 4623 */     return getShortFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected String getNativeString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4639 */     checkRowPos();
/* 4640 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4642 */     if (this.fields == null) {
/* 4643 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), "S1002", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4649 */     if (this.thisRow.isNull(columnIndex - 1)) {
/* 4650 */       this.wasNullFlag = true;
/*      */ 
/* 4652 */       return null;
/*      */     }
/*      */ 
/* 4655 */     this.wasNullFlag = false;
/*      */ 
/* 4657 */     String stringVal = null;
/*      */ 
/* 4659 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 4662 */     stringVal = getNativeConvertToString(columnIndex, field);
/* 4663 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 4665 */     if ((mysqlType != 7) && (mysqlType != 10) && (field.isZeroFill()) && (stringVal != null))
/*      */     {
/* 4668 */       int origLength = stringVal.length();
/*      */ 
/* 4670 */       StringBuffer zeroFillBuf = new StringBuffer(origLength);
/*      */ 
/* 4672 */       long numZeros = field.getLength() - origLength;
/*      */ 
/* 4674 */       for (long i = 0L; i < numZeros; i += 1L) {
/* 4675 */         zeroFillBuf.append('0');
/*      */       }
/*      */ 
/* 4678 */       zeroFillBuf.append(stringVal);
/*      */ 
/* 4680 */       stringVal = zeroFillBuf.toString();
/*      */     }
/*      */ 
/* 4683 */     return stringVal;
/*      */   }
/*      */ 
/*      */   private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4689 */     checkRowPos();
/* 4690 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4692 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 4694 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */ 
/* 4696 */     Time timeVal = null;
/*      */ 
/* 4698 */     if (mysqlType == 11) {
/* 4699 */       timeVal = this.thisRow.getNativeTime(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */     else
/*      */     {
/* 4703 */       timeVal = (Time)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 92, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 4715 */     if (timeVal == null)
/*      */     {
/* 4717 */       this.wasNullFlag = true;
/*      */ 
/* 4719 */       return null;
/*      */     }
/*      */ 
/* 4722 */     this.wasNullFlag = false;
/*      */ 
/* 4724 */     return timeVal;
/*      */   }
/*      */ 
/*      */   Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/* 4729 */     if (this.useUsageAdvisor) {
/* 4730 */       issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 11 });
/*      */     }
/*      */ 
/* 4735 */     String strTime = getNativeString(columnIndex);
/*      */ 
/* 4737 */     return getTimeFromString(strTime, targetCalendar, columnIndex, tz, rollForward);
/*      */   }
/*      */ 
/*      */   private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4744 */     checkRowPos();
/* 4745 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4747 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 4749 */     Timestamp tsVal = null;
/*      */ 
/* 4751 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */ 
/* 4753 */     switch (mysqlType) {
/*      */     case 7:
/*      */     case 12:
/* 4756 */       tsVal = this.thisRow.getNativeTimestamp(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */ 
/* 4758 */       break;
/*      */     default:
/* 4763 */       tsVal = (Timestamp)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 93, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 4775 */     if (tsVal == null)
/*      */     {
/* 4777 */       this.wasNullFlag = true;
/*      */ 
/* 4779 */       return null;
/*      */     }
/*      */ 
/* 4782 */     this.wasNullFlag = false;
/*      */ 
/* 4784 */     return tsVal;
/*      */   }
/*      */ 
/*      */   Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/* 4789 */     if (this.useUsageAdvisor) {
/* 4790 */       issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 7, 12 });
/*      */     }
/*      */ 
/* 4796 */     String strTimestamp = getNativeString(columnIndex);
/*      */ 
/* 4798 */     return getTimestampFromString(columnIndex, targetCalendar, strTimestamp, tz, rollForward);
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4825 */     checkRowPos();
/*      */ 
/* 4827 */     return getBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   protected URL getNativeURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 4834 */     String val = getString(colIndex);
/*      */ 
/* 4836 */     if (val == null) {
/* 4837 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 4841 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 4843 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public synchronized ResultSetInternalMethods getNextResultSet()
/*      */   {
/* 4855 */     return this.nextResultSet;
/*      */   }
/*      */ 
/*      */   public Object getObject(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4882 */     checkRowPos();
/* 4883 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4885 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 4887 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 4888 */       this.wasNullFlag = true;
/*      */ 
/* 4890 */       return null;
/*      */     }
/*      */ 
/* 4893 */     this.wasNullFlag = false;
/*      */ 
/* 4896 */     Field field = this.fields[columnIndexMinusOne];
/*      */     String stringVal;
/* 4898 */     switch (field.getSQLType()) {
/*      */     case -7:
/*      */     case 16:
/* 4901 */       if ((field.getMysqlType() == 16) && (!field.isSingleBit()))
/*      */       {
/* 4903 */         return getBytes(columnIndex);
/*      */       }
/*      */ 
/* 4909 */       return Boolean.valueOf(getBoolean(columnIndex));
/*      */     case -6:
/* 4912 */       if (!field.isUnsigned()) {
/* 4913 */         return Integer.valueOf(getByte(columnIndex));
/*      */       }
/*      */ 
/* 4916 */       return Integer.valueOf(getInt(columnIndex));
/*      */     case 5:
/* 4920 */       return Integer.valueOf(getInt(columnIndex));
/*      */     case 4:
/* 4924 */       if ((!field.isUnsigned()) || (field.getMysqlType() == 9))
/*      */       {
/* 4926 */         return Integer.valueOf(getInt(columnIndex));
/*      */       }
/*      */ 
/* 4929 */       return Long.valueOf(getLong(columnIndex));
/*      */     case -5:
/* 4933 */       if (!field.isUnsigned()) {
/* 4934 */         return Long.valueOf(getLong(columnIndex));
/*      */       }
/*      */ 
/* 4937 */       stringVal = getString(columnIndex);
/*      */ 
/* 4939 */       if (stringVal == null) {
/* 4940 */         return null;
/*      */       }
/*      */       try
/*      */       {
/* 4944 */         return new BigInteger(stringVal);
/*      */       } catch (NumberFormatException nfe) {
/* 4946 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     case 2:
/*      */     case 3:
/* 4954 */       stringVal = getString(columnIndex);
/*      */ 
/* 4958 */       if (stringVal != null) {
/* 4959 */         if (stringVal.length() == 0) {
/* 4960 */           BigDecimal val = new BigDecimal(0);
/*      */ 
/* 4962 */           return val;
/*      */         }BigDecimal val;
/*      */         try {
/* 4966 */           val = new BigDecimal(stringVal);
/*      */         } catch (NumberFormatException ex) {
/* 4968 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 4975 */         return val;
/*      */       }
/*      */ 
/* 4978 */       return null;
/*      */     case 7:
/* 4981 */       return new Float(getFloat(columnIndex));
/*      */     case 6:
/*      */     case 8:
/* 4985 */       return new Double(getDouble(columnIndex));
/*      */     case 1:
/*      */     case 12:
/* 4989 */       if (!field.isOpaqueBinary()) {
/* 4990 */         return getString(columnIndex);
/*      */       }
/*      */ 
/* 4993 */       return getBytes(columnIndex);
/*      */     case -1:
/* 4995 */       if (!field.isOpaqueBinary()) {
/* 4996 */         return getStringForClob(columnIndex);
/*      */       }
/*      */ 
/* 4999 */       return getBytes(columnIndex);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 5004 */       if (field.getMysqlType() == 255)
/* 5005 */         return getBytes(columnIndex);
/* 5006 */       if ((field.isBinary()) || (field.isBlob())) {
/* 5007 */         byte[] data = getBytes(columnIndex);
/*      */ 
/* 5009 */         if (this.connection.getAutoDeserialize()) {
/* 5010 */           Object obj = data;
/*      */ 
/* 5012 */           if ((data != null) && (data.length >= 2)) {
/* 5013 */             if ((data[0] == -84) && (data[1] == -19))
/*      */               try
/*      */               {
/* 5016 */                 ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */ 
/* 5018 */                 ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */ 
/* 5020 */                 obj = objIn.readObject();
/* 5021 */                 objIn.close();
/* 5022 */                 bytesIn.close();
/*      */               } catch (ClassNotFoundException cnfe) {
/* 5024 */                 throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/*      */               }
/*      */               catch (IOException ex)
/*      */               {
/* 5031 */                 obj = data;
/*      */               }
/*      */             else {
/* 5034 */               return getString(columnIndex);
/*      */             }
/*      */           }
/*      */ 
/* 5038 */           return obj;
/*      */         }
/*      */ 
/* 5041 */         return data;
/*      */       }
/*      */ 
/* 5044 */       return getBytes(columnIndex);
/*      */     case 91:
/* 5047 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
/*      */       {
/* 5049 */         return Short.valueOf(getShort(columnIndex));
/*      */       }
/*      */ 
/* 5052 */       return getDate(columnIndex);
/*      */     case 92:
/* 5055 */       return getTime(columnIndex);
/*      */     case 93:
/* 5058 */       return getTimestamp(columnIndex);
/*      */     }
/*      */ 
/* 5061 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */   public <T> T getObject(int columnIndex, Class<T> type)
/*      */     throws SQLException
/*      */   {
/* 5067 */     if (type == null) {
/* 5068 */       throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 5072 */     if (type.equals(String.class))
/* 5073 */       return getString(columnIndex);
/* 5074 */     if (type.equals(BigDecimal.class))
/* 5075 */       return getBigDecimal(columnIndex);
/* 5076 */     if ((type.equals(Boolean.class)) || (type.equals(Boolean.TYPE)))
/* 5077 */       return Boolean.valueOf(getBoolean(columnIndex));
/* 5078 */     if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE)))
/* 5079 */       return Integer.valueOf(getInt(columnIndex));
/* 5080 */     if ((type.equals(Long.class)) || (type.equals(Long.TYPE)))
/* 5081 */       return Long.valueOf(getLong(columnIndex));
/* 5082 */     if ((type.equals(Float.class)) || (type.equals(Float.TYPE)))
/* 5083 */       return Float.valueOf(getFloat(columnIndex));
/* 5084 */     if ((type.equals(Double.class)) || (type.equals(Double.TYPE)))
/* 5085 */       return Double.valueOf(getDouble(columnIndex));
/* 5086 */     if (type.equals([B.class))
/* 5087 */       return getBytes(columnIndex);
/* 5088 */     if (type.equals(Date.class))
/* 5089 */       return getDate(columnIndex);
/* 5090 */     if (type.equals(Time.class))
/* 5091 */       return getTime(columnIndex);
/* 5092 */     if (type.equals(Timestamp.class))
/* 5093 */       return getTimestamp(columnIndex);
/* 5094 */     if (type.equals(Clob.class))
/* 5095 */       return getClob(columnIndex);
/* 5096 */     if (type.equals(Blob.class))
/* 5097 */       return getBlob(columnIndex);
/* 5098 */     if (type.equals(Array.class))
/* 5099 */       return getArray(columnIndex);
/* 5100 */     if (type.equals(Ref.class))
/* 5101 */       return getRef(columnIndex);
/* 5102 */     if (type.equals(URL.class)) {
/* 5103 */       return getURL(columnIndex);
/*      */     }
/*      */ 
/* 5114 */     if (this.connection.getAutoDeserialize()) {
/*      */       try {
/* 5116 */         return getObject(columnIndex);
/*      */       } catch (ClassCastException cce) {
/* 5118 */         SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", getExceptionInterceptor());
/*      */ 
/* 5120 */         sqlEx.initCause(cce);
/*      */ 
/* 5122 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */ 
/* 5126 */     throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public <T> T getObject(String columnLabel, Class<T> type)
/*      */     throws SQLException
/*      */   {
/* 5133 */     return getObject(findColumn(columnLabel), type);
/*      */   }
/*      */ 
/*      */   public Object getObject(int i, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 5152 */     return getObject(i);
/*      */   }
/*      */ 
/*      */   public Object getObject(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5179 */     return getObject(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Object getObject(String colName, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 5199 */     return getObject(findColumn(colName), map);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException
/*      */   {
/* 5204 */     checkRowPos();
/* 5205 */     checkColumnBounds(columnIndex);
/*      */ 
/* 5207 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 5209 */     if (value == null) {
/* 5210 */       this.wasNullFlag = true;
/*      */ 
/* 5212 */       return null;
/*      */     }
/*      */ 
/* 5215 */     this.wasNullFlag = false;
/*      */ 
/* 5218 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 5220 */     switch (desiredSqlType)
/*      */     {
/*      */     case -7:
/*      */     case 16:
/* 5226 */       return Boolean.valueOf(getBoolean(columnIndex));
/*      */     case -6:
/* 5229 */       return Integer.valueOf(getInt(columnIndex));
/*      */     case 5:
/* 5232 */       return Integer.valueOf(getInt(columnIndex));
/*      */     case 4:
/* 5236 */       if ((!field.isUnsigned()) || (field.getMysqlType() == 9))
/*      */       {
/* 5238 */         return Integer.valueOf(getInt(columnIndex));
/*      */       }
/*      */ 
/* 5241 */       return Long.valueOf(getLong(columnIndex));
/*      */     case -5:
/* 5245 */       if (field.isUnsigned()) {
/* 5246 */         return getBigDecimal(columnIndex);
/*      */       }
/*      */ 
/* 5249 */       return Long.valueOf(getLong(columnIndex));
/*      */     case 2:
/*      */     case 3:
/* 5254 */       String stringVal = getString(columnIndex);
/*      */ 
/* 5257 */       if (stringVal != null) {
/* 5258 */         if (stringVal.length() == 0) {
/* 5259 */           BigDecimal val = new BigDecimal(0);
/*      */ 
/* 5261 */           return val;
/*      */         }BigDecimal val;
/*      */         try {
/* 5265 */           val = new BigDecimal(stringVal);
/*      */         } catch (NumberFormatException ex) {
/* 5267 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5274 */         return val;
/*      */       }
/*      */ 
/* 5277 */       return null;
/*      */     case 7:
/* 5280 */       return new Float(getFloat(columnIndex));
/*      */     case 6:
/* 5284 */       if (!this.connection.getRunningCTS13()) {
/* 5285 */         return new Double(getFloat(columnIndex));
/*      */       }
/* 5287 */       return new Float(getFloat(columnIndex));
/*      */     case 8:
/* 5294 */       return new Double(getDouble(columnIndex));
/*      */     case 1:
/*      */     case 12:
/* 5298 */       return getString(columnIndex);
/*      */     case -1:
/* 5300 */       return getStringForClob(columnIndex);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 5304 */       return getBytes(columnIndex);
/*      */     case 91:
/* 5307 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
/*      */       {
/* 5309 */         return Short.valueOf(getShort(columnIndex));
/*      */       }
/*      */ 
/* 5312 */       return getDate(columnIndex);
/*      */     case 92:
/* 5315 */       return getTime(columnIndex);
/*      */     case 93:
/* 5318 */       return getTimestamp(columnIndex);
/*      */     }
/*      */ 
/* 5321 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType)
/*      */     throws SQLException
/*      */   {
/* 5327 */     return getObjectStoredProc(i, desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException
/*      */   {
/* 5332 */     return getObjectStoredProc(findColumn(columnName), desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException
/*      */   {
/* 5337 */     return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Ref getRef(int i)
/*      */     throws SQLException
/*      */   {
/* 5354 */     checkColumnBounds(i);
/* 5355 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public Ref getRef(String colName)
/*      */     throws SQLException
/*      */   {
/* 5372 */     return getRef(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public int getRow()
/*      */     throws SQLException
/*      */   {
/* 5389 */     checkClosed();
/*      */ 
/* 5391 */     int currentRowNumber = this.rowData.getCurrentRowNumber();
/* 5392 */     int row = 0;
/*      */ 
/* 5396 */     if (!this.rowData.isDynamic()) {
/* 5397 */       if ((currentRowNumber < 0) || (this.rowData.isAfterLast()) || (this.rowData.isEmpty()))
/*      */       {
/* 5399 */         row = 0;
/*      */       }
/* 5401 */       else row = currentRowNumber + 1;
/*      */     }
/*      */     else
/*      */     {
/* 5405 */       row = currentRowNumber + 1;
/*      */     }
/*      */ 
/* 5408 */     return row;
/*      */   }
/*      */ 
/*      */   public String getServerInfo()
/*      */   {
/*      */     try
/*      */     {
/* 5418 */       synchronized (checkClosed()) {
/* 5419 */         return this.serverInfo;
/*      */       }
/*      */     } catch (SQLException e) {
/*      */     }
/* 5422 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   private long getNumericRepresentationOfSQLBitType(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5428 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 5430 */     if ((this.fields[(columnIndex - 1)].isSingleBit()) || (((byte[])(byte[])value).length == 1))
/*      */     {
/* 5432 */       return ((byte[])(byte[])value)[0];
/*      */     }
/*      */ 
/* 5436 */     byte[] asBytes = (byte[])(byte[])value;
/*      */ 
/* 5439 */     int shift = 0;
/*      */ 
/* 5441 */     long[] steps = new long[asBytes.length];
/*      */ 
/* 5443 */     for (int i = asBytes.length - 1; i >= 0; i--) {
/* 5444 */       steps[i] = ((asBytes[i] & 0xFF) << shift);
/* 5445 */       shift += 8;
/*      */     }
/*      */ 
/* 5448 */     long valueAsLong = 0L;
/*      */ 
/* 5450 */     for (int i = 0; i < asBytes.length; i++) {
/* 5451 */       valueAsLong |= steps[i];
/*      */     }
/*      */ 
/* 5454 */     return valueAsLong;
/*      */   }
/*      */ 
/*      */   public short getShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5469 */     if (!this.isBinaryEncoded) {
/* 5470 */       checkRowPos();
/*      */ 
/* 5472 */       if (this.useFastIntParsing)
/*      */       {
/* 5474 */         checkColumnBounds(columnIndex);
/*      */ 
/* 5476 */         Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */ 
/* 5478 */         if (value == null)
/* 5479 */           this.wasNullFlag = true;
/*      */         else {
/* 5481 */           this.wasNullFlag = false;
/*      */         }
/*      */ 
/* 5484 */         if (this.wasNullFlag) {
/* 5485 */           return 0;
/*      */         }
/*      */ 
/* 5488 */         byte[] shortAsBytes = (byte[])(byte[])value;
/*      */ 
/* 5490 */         if (shortAsBytes.length == 0) {
/* 5491 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 5494 */         boolean needsFullParse = false;
/*      */ 
/* 5496 */         for (int i = 0; i < shortAsBytes.length; i++) {
/* 5497 */           if (((char)shortAsBytes[i] != 'e') && ((char)shortAsBytes[i] != 'E'))
/*      */             continue;
/* 5499 */           needsFullParse = true;
/*      */ 
/* 5501 */           break;
/*      */         }
/*      */ 
/* 5505 */         if (!needsFullParse) {
/*      */           try {
/* 5507 */             return parseShortWithOverflowCheck(columnIndex, shortAsBytes, null);
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*      */             try {
/* 5512 */               return parseShortAsDouble(columnIndex, StringUtils.toString(shortAsBytes));
/*      */             }
/*      */             catch (NumberFormatException valueAsLong)
/*      */             {
/* 5518 */               if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 5519 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 5521 */                 if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L)))
/*      */                 {
/* 5524 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */                 }
/*      */ 
/* 5528 */                 return (short)(int)valueAsLong;
/*      */               }
/*      */ 
/* 5531 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + StringUtils.toString(shortAsBytes) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5541 */       String val = null;
/*      */       try
/*      */       {
/* 5544 */         val = getString(columnIndex);
/*      */ 
/* 5546 */         if (val != null)
/*      */         {
/* 5548 */           if (val.length() == 0) {
/* 5549 */             return (short)convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 5552 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */           {
/* 5554 */             return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */           }
/*      */ 
/* 5559 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */ 
/* 5562 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 5565 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException valueAsLong)
/*      */         {
/* 5570 */           if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 5571 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */ 
/* 5573 */             if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L)))
/*      */             {
/* 5576 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */             }
/*      */ 
/* 5580 */             return (short)(int)valueAsLong;
/*      */           }
/*      */ 
/* 5583 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5591 */     return getNativeShort(columnIndex);
/*      */   }
/*      */ 
/*      */   public short getShort(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5606 */     return getShort(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final short getShortFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 5612 */       if (val != null)
/*      */       {
/* 5614 */         if (val.length() == 0) {
/* 5615 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 5618 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/* 5620 */           return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */         }
/*      */ 
/* 5624 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */ 
/* 5627 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 5630 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 5635 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public Statement getStatement()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5655 */       synchronized (checkClosed()) {
/* 5656 */         if (this.wrapperStatement != null) {
/* 5657 */           return this.wrapperStatement;
/*      */         }
/*      */ 
/* 5660 */         return this.owningStatement;
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 5664 */       if (!this.retainOwningStatement) {
/* 5665 */         throw SQLError.createSQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 5672 */       if (this.wrapperStatement != null) {
/* 5673 */         return this.wrapperStatement;
/*      */       }
/*      */     }
/* 5676 */     return this.owningStatement;
/*      */   }
/*      */ 
/*      */   public String getString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5693 */     String stringVal = getStringInternal(columnIndex, true);
/*      */ 
/* 5695 */     if ((this.padCharsWithSpace) && (stringVal != null)) {
/* 5696 */       Field f = this.fields[(columnIndex - 1)];
/*      */ 
/* 5698 */       if (f.getMysqlType() == 254) {
/* 5699 */         int fieldLength = (int)f.getLength() / f.getMaxBytesPerCharacter();
/*      */ 
/* 5702 */         int currentLength = stringVal.length();
/*      */ 
/* 5704 */         if (currentLength < fieldLength) {
/* 5705 */           StringBuffer paddedBuf = new StringBuffer(fieldLength);
/* 5706 */           paddedBuf.append(stringVal);
/*      */ 
/* 5708 */           int difference = fieldLength - currentLength;
/*      */ 
/* 5710 */           paddedBuf.append(EMPTY_SPACE, 0, difference);
/*      */ 
/* 5712 */           stringVal = paddedBuf.toString();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 5717 */     return stringVal;
/*      */   }
/*      */ 
/*      */   public String getString(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5733 */     return getString(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private String getStringForClob(int columnIndex) throws SQLException {
/* 5737 */     String asString = null;
/*      */ 
/* 5739 */     String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 5742 */     if (forcedEncoding == null) {
/* 5743 */       if (!this.isBinaryEncoded)
/* 5744 */         asString = getString(columnIndex);
/*      */       else
/* 5746 */         asString = getNativeString(columnIndex);
/*      */     }
/*      */     else {
/*      */       try {
/* 5750 */         byte[] asBytes = null;
/*      */ 
/* 5752 */         if (!this.isBinaryEncoded)
/* 5753 */           asBytes = getBytes(columnIndex);
/*      */         else {
/* 5755 */           asBytes = getNativeBytes(columnIndex, true);
/*      */         }
/*      */ 
/* 5758 */         if (asBytes != null)
/* 5759 */           asString = StringUtils.toString(asBytes, forcedEncoding);
/*      */       }
/*      */       catch (UnsupportedEncodingException uee) {
/* 5762 */         throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5767 */     return asString;
/*      */   }
/*      */ 
/*      */   protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException
/*      */   {
/* 5772 */     if (!this.isBinaryEncoded) {
/* 5773 */       checkRowPos();
/* 5774 */       checkColumnBounds(columnIndex);
/*      */ 
/* 5776 */       if (this.fields == null) {
/* 5777 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), "S1002", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 5785 */       int internalColumnIndex = columnIndex - 1;
/*      */ 
/* 5787 */       if (this.thisRow.isNull(internalColumnIndex)) {
/* 5788 */         this.wasNullFlag = true;
/*      */ 
/* 5790 */         return null;
/*      */       }
/*      */ 
/* 5793 */       this.wasNullFlag = false;
/*      */ 
/* 5796 */       Field metadata = this.fields[internalColumnIndex];
/*      */ 
/* 5798 */       String stringVal = null;
/*      */ 
/* 5800 */       if (metadata.getMysqlType() == 16) {
/* 5801 */         if (metadata.isSingleBit()) {
/* 5802 */           byte[] value = this.thisRow.getColumnValue(internalColumnIndex);
/*      */ 
/* 5804 */           if (value.length == 0) {
/* 5805 */             return String.valueOf(convertToZeroWithEmptyCheck());
/*      */           }
/*      */ 
/* 5808 */           return String.valueOf(value[0]);
/*      */         }
/*      */ 
/* 5811 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       }
/*      */ 
/* 5814 */       String encoding = metadata.getCharacterSet();
/*      */ 
/* 5816 */       stringVal = this.thisRow.getString(internalColumnIndex, encoding, this.connection);
/*      */ 
/* 5823 */       if (metadata.getMysqlType() == 13) {
/* 5824 */         if (!this.connection.getYearIsDateType()) {
/* 5825 */           return stringVal;
/*      */         }
/*      */ 
/* 5828 */         Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */ 
/* 5830 */         if (dt == null) {
/* 5831 */           this.wasNullFlag = true;
/*      */ 
/* 5833 */           return null;
/*      */         }
/*      */ 
/* 5836 */         this.wasNullFlag = false;
/*      */ 
/* 5838 */         return dt.toString();
/*      */       }
/*      */ 
/* 5843 */       if ((checkDateTypes) && (!this.connection.getNoDatetimeStringSync())) {
/* 5844 */         switch (metadata.getSQLType()) {
/*      */         case 92:
/* 5846 */           Time tm = getTimeFromString(stringVal, null, columnIndex, getDefaultTimeZone(), false);
/*      */ 
/* 5849 */           if (tm == null) {
/* 5850 */             this.wasNullFlag = true;
/*      */ 
/* 5852 */             return null;
/*      */           }
/*      */ 
/* 5855 */           this.wasNullFlag = false;
/*      */ 
/* 5857 */           return tm.toString();
/*      */         case 91:
/* 5860 */           Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */ 
/* 5862 */           if (dt == null) {
/* 5863 */             this.wasNullFlag = true;
/*      */ 
/* 5865 */             return null;
/*      */           }
/*      */ 
/* 5868 */           this.wasNullFlag = false;
/*      */ 
/* 5870 */           return dt.toString();
/*      */         case 93:
/* 5872 */           Timestamp ts = getTimestampFromString(columnIndex, null, stringVal, getDefaultTimeZone(), false);
/*      */ 
/* 5875 */           if (ts == null) {
/* 5876 */             this.wasNullFlag = true;
/*      */ 
/* 5878 */             return null;
/*      */           }
/*      */ 
/* 5881 */           this.wasNullFlag = false;
/*      */ 
/* 5883 */           return ts.toString();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5889 */       return stringVal;
/*      */     }
/*      */ 
/* 5892 */     return getNativeString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Time getTime(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5907 */     return getTimeInternal(columnIndex, null, getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public Time getTime(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5927 */     return getTimeInternal(columnIndex, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public Time getTime(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5942 */     return getTime(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Time getTime(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5962 */     return getTime(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */   private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 5969 */     synchronized (checkClosed()) {
/* 5970 */       int hr = 0;
/* 5971 */       int min = 0;
/* 5972 */       int sec = 0;
/*      */       try
/*      */       {
/* 5976 */         if (timeAsString == null) {
/* 5977 */           this.wasNullFlag = true;
/*      */ 
/* 5979 */           return null;
/*      */         }
/*      */ 
/* 5990 */         timeAsString = timeAsString.trim();
/*      */ 
/* 5992 */         if ((timeAsString.equals("0")) || (timeAsString.equals("0000-00-00")) || (timeAsString.equals("0000-00-00 00:00:00")) || (timeAsString.equals("00000000000000")))
/*      */         {
/* 5996 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 5998 */             this.wasNullFlag = true;
/*      */ 
/* 6000 */             return null;
/* 6001 */           }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6003 */             throw SQLError.createSQLException("Value '" + timeAsString + "' can not be represented as java.sql.Time", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 6010 */           return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */         }
/*      */ 
/* 6013 */         this.wasNullFlag = false;
/*      */ 
/* 6015 */         Field timeColField = this.fields[(columnIndex - 1)];
/*      */ 
/* 6017 */         if (timeColField.getMysqlType() == 7)
/*      */         {
/* 6019 */           int length = timeAsString.length();
/*      */ 
/* 6021 */           switch (length)
/*      */           {
/*      */           case 19:
/* 6024 */             hr = Integer.parseInt(timeAsString.substring(length - 8, length - 6));
/*      */ 
/* 6026 */             min = Integer.parseInt(timeAsString.substring(length - 5, length - 3));
/*      */ 
/* 6028 */             sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */ 
/* 6032 */             break;
/*      */           case 12:
/*      */           case 14:
/* 6035 */             hr = Integer.parseInt(timeAsString.substring(length - 6, length - 4));
/*      */ 
/* 6037 */             min = Integer.parseInt(timeAsString.substring(length - 4, length - 2));
/*      */ 
/* 6039 */             sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */ 
/* 6043 */             break;
/*      */           case 10:
/* 6046 */             hr = Integer.parseInt(timeAsString.substring(6, 8));
/* 6047 */             min = Integer.parseInt(timeAsString.substring(8, 10));
/* 6048 */             sec = 0;
/*      */ 
/* 6051 */             break;
/*      */           case 11:
/*      */           case 13:
/*      */           case 15:
/*      */           case 16:
/*      */           case 17:
/*      */           case 18:
/*      */           default:
/* 6054 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 6063 */           SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */ 
/* 6070 */           if (this.warningChain == null)
/* 6071 */             this.warningChain = precisionLost;
/*      */           else
/* 6073 */             this.warningChain.setNextWarning(precisionLost);
/*      */         }
/* 6075 */         else if (timeColField.getMysqlType() == 12) {
/* 6076 */           hr = Integer.parseInt(timeAsString.substring(11, 13));
/* 6077 */           min = Integer.parseInt(timeAsString.substring(14, 16));
/* 6078 */           sec = Integer.parseInt(timeAsString.substring(17, 19));
/*      */ 
/* 6080 */           SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */ 
/* 6087 */           if (this.warningChain == null)
/* 6088 */             this.warningChain = precisionLost;
/*      */           else
/* 6090 */             this.warningChain.setNextWarning(precisionLost);
/*      */         } else {
/* 6092 */           if (timeColField.getMysqlType() == 10) {
/* 6093 */             return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */           }
/*      */ 
/* 6097 */           if ((timeAsString.length() != 5) && (timeAsString.length() != 8))
/*      */           {
/* 6099 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString("ResultSet.___in_column__268") + columnIndex, "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 6106 */           hr = Integer.parseInt(timeAsString.substring(0, 2));
/* 6107 */           min = Integer.parseInt(timeAsString.substring(3, 5));
/* 6108 */           sec = timeAsString.length() == 5 ? 0 : Integer.parseInt(timeAsString.substring(6));
/*      */         }
/*      */ 
/* 6112 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/* 6114 */         synchronized (sessionCalendar)
/*      */         {
/*      */         }
/*      */ 
/* 6122 */         monitorexit; throw localObject1;
/*      */       } catch (RuntimeException ex) {
/* 6124 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", getExceptionInterceptor());
/*      */ 
/* 6126 */         sqlEx.initCause(ex);
/*      */ 
/* 6128 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 6150 */     checkRowPos();
/*      */ 
/* 6152 */     if (this.isBinaryEncoded) {
/* 6153 */       return getNativeTime(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */ 
/* 6156 */     if (!this.useFastDateParsing) {
/* 6157 */       String timeAsString = getStringInternal(columnIndex, false);
/*      */ 
/* 6159 */       return getTimeFromString(timeAsString, targetCalendar, columnIndex, tz, rollForward);
/*      */     }
/*      */ 
/* 6163 */     checkColumnBounds(columnIndex);
/*      */ 
/* 6165 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 6167 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 6168 */       this.wasNullFlag = true;
/*      */ 
/* 6170 */       return null;
/*      */     }
/*      */ 
/* 6173 */     this.wasNullFlag = false;
/*      */ 
/* 6175 */     return this.thisRow.getTimeFast(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 6192 */     return getTimestampInternal(columnIndex, null, getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 6214 */     return getTimestampInternal(columnIndex, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName)
/*      */     throws SQLException
/*      */   {
/* 6230 */     return getTimestamp(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 6251 */     return getTimestamp(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampFromString(int columnIndex, Calendar targetCalendar, String timestampValue, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/*      */     SQLException sqlEx;
/*      */     try {
/* 6259 */       this.wasNullFlag = false;
/*      */ 
/* 6261 */       if (timestampValue == null) {
/* 6262 */         this.wasNullFlag = true;
/*      */ 
/* 6264 */         return null;
/*      */       }
/*      */ 
/* 6275 */       timestampValue = timestampValue.trim();
/*      */ 
/* 6277 */       int length = timestampValue.length();
/*      */ 
/* 6279 */       Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 6283 */       synchronized (sessionCalendar) {
/* 6284 */         if ((length > 0) && (timestampValue.charAt(0) == '0') && ((timestampValue.equals("0000-00-00")) || (timestampValue.equals("0000-00-00 00:00:00")) || (timestampValue.equals("00000000000000")) || (timestampValue.equals("0"))))
/*      */         {
/* 6291 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6293 */             this.wasNullFlag = true;
/*      */ 
/* 6295 */             return null;
/* 6296 */           }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 6298 */             throw SQLError.createSQLException("Value '" + timestampValue + "' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 6305 */           return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */         }
/* 6307 */         if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */         {
/* 6309 */           if (!this.useLegacyDatetimeCode) {
/* 6310 */             return TimeUtil.fastTimestampCreate(tz, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0);
/*      */           }
/*      */ 
/* 6315 */           return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */         }
/*      */ 
/* 6325 */         if (timestampValue.endsWith(".")) {
/* 6326 */           timestampValue = timestampValue.substring(0, timestampValue.length() - 1);
/*      */         }
/*      */ 
/* 6332 */         int year = 0;
/* 6333 */         int month = 0;
/* 6334 */         int day = 0;
/* 6335 */         int hour = 0;
/* 6336 */         int minutes = 0;
/* 6337 */         int seconds = 0;
/* 6338 */         int nanos = 0;
/*      */ 
/* 6340 */         switch (length) {
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/* 6349 */           year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6350 */           month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */ 
/* 6352 */           day = Integer.parseInt(timestampValue.substring(8, 10));
/* 6353 */           hour = Integer.parseInt(timestampValue.substring(11, 13));
/*      */ 
/* 6355 */           minutes = Integer.parseInt(timestampValue.substring(14, 16));
/*      */ 
/* 6357 */           seconds = Integer.parseInt(timestampValue.substring(17, 19));
/*      */ 
/* 6360 */           nanos = 0;
/*      */ 
/* 6362 */           if (length <= 19) break;
/* 6363 */           int decimalIndex = timestampValue.lastIndexOf('.');
/*      */ 
/* 6365 */           if (decimalIndex != -1) {
/* 6366 */             if (decimalIndex + 2 <= length) {
/* 6367 */               nanos = Integer.parseInt(timestampValue.substring(decimalIndex + 1));
/*      */ 
/* 6370 */               int numDigits = length - (decimalIndex + 1);
/*      */ 
/* 6372 */               if (numDigits < 9) {
/* 6373 */                 int factor = (int)Math.pow(10.0D, 9 - numDigits);
/* 6374 */                 nanos *= factor;
/*      */               }
/*      */             } else {
/* 6377 */               throw new IllegalArgumentException();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 6385 */           break;
/*      */         case 14:
/* 6391 */           year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6392 */           month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 6394 */           day = Integer.parseInt(timestampValue.substring(6, 8));
/* 6395 */           hour = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 6397 */           minutes = Integer.parseInt(timestampValue.substring(10, 12));
/*      */ 
/* 6399 */           seconds = Integer.parseInt(timestampValue.substring(12, 14));
/*      */ 
/* 6402 */           break;
/*      */         case 12:
/* 6406 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6408 */           if (year <= 69) {
/* 6409 */             year += 100;
/*      */           }
/*      */ 
/* 6412 */           year += 1900;
/*      */ 
/* 6414 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6416 */           day = Integer.parseInt(timestampValue.substring(4, 6));
/* 6417 */           hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 6418 */           minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 6420 */           seconds = Integer.parseInt(timestampValue.substring(10, 12));
/*      */ 
/* 6423 */           break;
/*      */         case 10:
/* 6427 */           if ((this.fields[(columnIndex - 1)].getMysqlType() == 10) || (timestampValue.indexOf("-") != -1))
/*      */           {
/* 6429 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6430 */             month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */ 
/* 6432 */             day = Integer.parseInt(timestampValue.substring(8, 10));
/* 6433 */             hour = 0;
/* 6434 */             minutes = 0;
/*      */           } else {
/* 6436 */             year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6438 */             if (year <= 69) {
/* 6439 */               year += 100;
/*      */             }
/*      */ 
/* 6442 */             month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6444 */             day = Integer.parseInt(timestampValue.substring(4, 6));
/* 6445 */             hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 6446 */             minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 6449 */             year += 1900;
/*      */           }
/*      */ 
/* 6452 */           break;
/*      */         case 8:
/* 6456 */           if (timestampValue.indexOf(":") != -1) {
/* 6457 */             hour = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6459 */             minutes = Integer.parseInt(timestampValue.substring(3, 5));
/*      */ 
/* 6461 */             seconds = Integer.parseInt(timestampValue.substring(6, 8));
/*      */ 
/* 6463 */             year = 1970;
/* 6464 */             month = 1;
/* 6465 */             day = 1;
/*      */           }
/*      */           else
/*      */           {
/* 6469 */             year = Integer.parseInt(timestampValue.substring(0, 4));
/* 6470 */             month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 6472 */             day = Integer.parseInt(timestampValue.substring(6, 8));
/*      */ 
/* 6474 */             year -= 1900;
/* 6475 */             month--;
/*      */           }
/* 6477 */           break;
/*      */         case 6:
/* 6481 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6483 */           if (year <= 69) {
/* 6484 */             year += 100;
/*      */           }
/*      */ 
/* 6487 */           year += 1900;
/*      */ 
/* 6489 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6491 */           day = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 6493 */           break;
/*      */         case 4:
/* 6497 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6499 */           if (year <= 69) {
/* 6500 */             year += 100;
/*      */           }
/*      */ 
/* 6503 */           year += 1900;
/*      */ 
/* 6505 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 6508 */           day = 1;
/*      */ 
/* 6510 */           break;
/*      */         case 2:
/* 6514 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 6516 */           if (year <= 69) {
/* 6517 */             year += 100;
/*      */           }
/*      */ 
/* 6520 */           year += 1900;
/* 6521 */           month = 1;
/* 6522 */           day = 1;
/*      */ 
/* 6524 */           break;
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
/*      */         default:
/* 6528 */           throw new SQLException("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + ".", "S1009");
/*      */         }
/*      */ 
/* 6534 */         if (!this.useLegacyDatetimeCode) {
/* 6535 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */         }
/*      */ 
/* 6539 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/* 6548 */       sqlEx = SQLError.createSQLException("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP.", "S1009", getExceptionInterceptor());
/*      */ 
/* 6551 */       sqlEx.initCause(e);
/*      */     }
/* 6553 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 6575 */     if (this.isBinaryEncoded) {
/* 6576 */       return getNativeTimestamp(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */ 
/* 6579 */     Timestamp tsVal = null;
/*      */ 
/* 6581 */     if (!this.useFastDateParsing) {
/* 6582 */       String timestampValue = getStringInternal(columnIndex, false);
/*      */ 
/* 6584 */       tsVal = getTimestampFromString(columnIndex, targetCalendar, timestampValue, tz, rollForward);
/*      */     }
/*      */     else
/*      */     {
/* 6588 */       checkClosed();
/* 6589 */       checkRowPos();
/* 6590 */       checkColumnBounds(columnIndex);
/*      */ 
/* 6592 */       tsVal = this.thisRow.getTimestampFast(columnIndex - 1, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */ 
/* 6596 */     if (tsVal == null)
/* 6597 */       this.wasNullFlag = true;
/*      */     else {
/* 6599 */       this.wasNullFlag = false;
/*      */     }
/*      */ 
/* 6602 */     return tsVal;
/*      */   }
/*      */ 
/*      */   public int getType()
/*      */     throws SQLException
/*      */   {
/* 6616 */     return this.resultSetType;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 6638 */     if (!this.isBinaryEncoded) {
/* 6639 */       checkRowPos();
/*      */ 
/* 6641 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */ 
/* 6644 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 6661 */     return getUnicodeStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public long getUpdateCount() {
/* 6665 */     return this.updateCount;
/*      */   }
/*      */ 
/*      */   public long getUpdateID() {
/* 6669 */     return this.updateId;
/*      */   }
/*      */ 
/*      */   public URL getURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 6676 */     String val = getString(colIndex);
/*      */ 
/* 6678 */     if (val == null) {
/* 6679 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6683 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 6685 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public URL getURL(String colName)
/*      */     throws SQLException
/*      */   {
/* 6695 */     String val = getString(colName);
/*      */ 
/* 6697 */     if (val == null) {
/* 6698 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6702 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 6704 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 6731 */     synchronized (checkClosed()) {
/* 6732 */       return this.warningChain;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void insertRow()
/*      */     throws SQLException
/*      */   {
/* 6748 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/* 6765 */     synchronized (checkClosed()) {
/* 6766 */       boolean b = this.rowData.isAfterLast();
/*      */ 
/* 6768 */       return b;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/* 6786 */     synchronized (checkClosed()) {
/* 6787 */       return this.rowData.isBeforeFirst();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isFirst()
/*      */     throws SQLException
/*      */   {
/* 6804 */     synchronized (checkClosed()) {
/* 6805 */       return this.rowData.isFirst();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isLast()
/*      */     throws SQLException
/*      */   {
/* 6825 */     synchronized (checkClosed()) {
/* 6826 */       return this.rowData.isLast();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion)
/*      */     throws SQLException
/*      */   {
/* 6838 */     synchronized (checkClosed()) {
/* 6839 */       StringBuffer originalQueryBuf = new StringBuffer();
/*      */ 
/* 6841 */       if ((this.owningStatement != null) && ((this.owningStatement instanceof PreparedStatement)))
/*      */       {
/* 6843 */         originalQueryBuf.append(Messages.getString("ResultSet.CostlyConversionCreatedFromQuery"));
/* 6844 */         originalQueryBuf.append(((PreparedStatement)this.owningStatement).originalSql);
/*      */ 
/* 6846 */         originalQueryBuf.append("\n\n");
/*      */       } else {
/* 6848 */         originalQueryBuf.append(".");
/*      */       }
/*      */ 
/* 6851 */       StringBuffer convertibleTypesBuf = new StringBuffer();
/*      */ 
/* 6853 */       for (int i = 0; i < typesWithNoParseConversion.length; i++) {
/* 6854 */         convertibleTypesBuf.append(MysqlDefs.typeToName(typesWithNoParseConversion[i]));
/* 6855 */         convertibleTypesBuf.append("\n");
/*      */       }
/*      */ 
/* 6858 */       String message = Messages.getString("ResultSet.CostlyConversion", new Object[] { methodName, Integer.valueOf(columnIndex + 1), fieldInfo.getOriginalName(), fieldInfo.getOriginalTableName(), originalQueryBuf.toString(), value != null ? value.getClass().getName() : ResultSetMetaData.getClassNameForJavaType(fieldInfo.getSQLType(), fieldInfo.isUnsigned(), fieldInfo.getMysqlType(), (fieldInfo.isBinary()) || (fieldInfo.isBlob()) ? 1 : false, fieldInfo.isOpaqueBinary()), MysqlDefs.typeToName(fieldInfo.getMysqlType()), convertibleTypesBuf.toString() });
/*      */ 
/* 6873 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean last()
/*      */     throws SQLException
/*      */   {
/* 6897 */     synchronized (checkClosed())
/*      */     {
/* 6899 */       boolean b = true;
/*      */ 
/* 6901 */       if (this.rowData.size() == 0) {
/* 6902 */         b = false;
/*      */       }
/*      */       else {
/* 6905 */         if (this.onInsertRow) {
/* 6906 */           this.onInsertRow = false;
/*      */         }
/*      */ 
/* 6909 */         if (this.doingUpdates) {
/* 6910 */           this.doingUpdates = false;
/*      */         }
/*      */ 
/* 6913 */         if (this.thisRow != null) {
/* 6914 */           this.thisRow.closeOpenStreams();
/*      */         }
/*      */ 
/* 6917 */         this.rowData.beforeLast();
/* 6918 */         this.thisRow = this.rowData.next();
/*      */       }
/*      */ 
/* 6921 */       setRowPositionValidity();
/*      */ 
/* 6923 */       return b;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 6946 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 6967 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/* 6986 */     synchronized (checkClosed())
/*      */     {
/* 6988 */       if (this.onInsertRow) {
/* 6989 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/* 6992 */       if (this.doingUpdates) {
/* 6993 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/* 6998 */       if (!reallyResult()) {
/* 6999 */         throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 7005 */       if (this.thisRow != null)
/* 7006 */         this.thisRow.closeOpenStreams();
/*      */       boolean b;
/*      */       boolean b;
/* 7009 */       if (this.rowData.size() == 0) {
/* 7010 */         b = false;
/*      */       } else {
/* 7012 */         this.thisRow = this.rowData.next();
/*      */         boolean b;
/* 7014 */         if (this.thisRow == null) {
/* 7015 */           b = false;
/*      */         } else {
/* 7017 */           clearWarnings();
/*      */ 
/* 7019 */           b = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 7024 */       setRowPositionValidity();
/*      */ 
/* 7026 */       return b;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException
/*      */   {
/* 7032 */     if (val == null) {
/* 7033 */       return 0;
/*      */     }
/*      */ 
/* 7036 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 7038 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7039 */       (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */     {
/* 7041 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */     }
/*      */ 
/* 7046 */     return (int)valueAsDouble;
/*      */   }
/*      */ 
/*      */   private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
/* 7050 */     int intValue = this.thisRow.getInt(columnIndex);
/*      */ 
/* 7052 */     checkForIntegerTruncation(columnIndex, null, intValue);
/*      */ 
/* 7055 */     return intValue;
/*      */   }
/*      */ 
/*      */   private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, int intValue)
/*      */     throws SQLException
/*      */   {
/* 7061 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7062 */       (intValue == -2147483648) || (intValue == 2147483647))) {
/* 7063 */       String valueAsString = null;
/*      */ 
/* 7065 */       if (valueAsBytes == null) {
/* 7066 */         valueAsString = this.thisRow.getString(columnIndex, this.fields[columnIndex].getCharacterSet(), this.connection);
/*      */       }
/*      */ 
/* 7071 */       long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
/*      */ 
/* 7075 */       if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
/*      */       {
/* 7077 */         throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex + 1, 4);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private long parseLongAsDouble(int columnIndexZeroBased, String val)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7087 */     if (val == null) {
/* 7088 */       return 0L;
/*      */     }
/*      */ 
/* 7091 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 7093 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7094 */       (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
/*      */     {
/* 7096 */       throwRangeException(val, columnIndexZeroBased + 1, -5);
/*      */     }
/*      */ 
/* 7100 */     return ()valueAsDouble;
/*      */   }
/*      */ 
/*      */   private long getLongWithOverflowCheck(int columnIndexZeroBased, boolean doOverflowCheck) throws SQLException {
/* 7104 */     long longValue = this.thisRow.getLong(columnIndexZeroBased);
/*      */ 
/* 7106 */     if (doOverflowCheck) {
/* 7107 */       checkForLongTruncation(columnIndexZeroBased, null, longValue);
/*      */     }
/*      */ 
/* 7110 */     return longValue;
/*      */   }
/*      */ 
/*      */   private long parseLongWithOverflowCheck(int columnIndexZeroBased, byte[] valueAsBytes, String valueAsString, boolean doCheck)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7117 */     long longValue = 0L;
/*      */ 
/* 7119 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 7120 */       return 0L;
/*      */     }
/*      */ 
/* 7123 */     if (valueAsBytes != null) {
/* 7124 */       longValue = StringUtils.getLong(valueAsBytes);
/*      */     }
/*      */     else
/*      */     {
/* 7134 */       valueAsString = valueAsString.trim();
/*      */ 
/* 7136 */       longValue = Long.parseLong(valueAsString);
/*      */     }
/*      */ 
/* 7139 */     if ((doCheck) && (this.jdbcCompliantTruncationForReads)) {
/* 7140 */       checkForLongTruncation(columnIndexZeroBased, valueAsBytes, longValue);
/*      */     }
/*      */ 
/* 7143 */     return longValue;
/*      */   }
/*      */ 
/*      */   private void checkForLongTruncation(int columnIndexZeroBased, byte[] valueAsBytes, long longValue) throws SQLException {
/* 7147 */     if ((longValue == -9223372036854775808L) || (longValue == 9223372036854775807L))
/*      */     {
/* 7149 */       String valueAsString = null;
/*      */ 
/* 7151 */       if (valueAsBytes == null) {
/* 7152 */         valueAsString = this.thisRow.getString(columnIndexZeroBased, this.fields[columnIndexZeroBased].getCharacterSet(), this.connection);
/*      */       }
/*      */ 
/* 7157 */       double valueAsDouble = Double.parseDouble(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
/*      */ 
/* 7161 */       if ((valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D))
/*      */       {
/* 7163 */         throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndexZeroBased + 1, -5);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private short parseShortAsDouble(int columnIndex, String val)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7172 */     if (val == null) {
/* 7173 */       return 0;
/*      */     }
/*      */ 
/* 7176 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 7178 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7179 */       (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
/*      */     {
/* 7181 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
/*      */     }
/*      */ 
/* 7186 */     return (short)(int)valueAsDouble;
/*      */   }
/*      */ 
/*      */   private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 7193 */     short shortValue = 0;
/*      */ 
/* 7195 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 7196 */       return 0;
/*      */     }
/*      */ 
/* 7199 */     if (valueAsBytes != null) {
/* 7200 */       shortValue = StringUtils.getShort(valueAsBytes);
/*      */     }
/*      */     else
/*      */     {
/* 7210 */       valueAsString = valueAsString.trim();
/*      */ 
/* 7212 */       shortValue = Short.parseShort(valueAsString);
/*      */     }
/*      */ 
/* 7215 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 7216 */       (shortValue == -32768) || (shortValue == 32767))) {
/* 7217 */       long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
/*      */ 
/* 7221 */       if ((valueAsLong < -32768L) || (valueAsLong > 32767L))
/*      */       {
/* 7223 */         throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex, 5);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 7230 */     return shortValue;
/*      */   }
/*      */ 
/*      */   public boolean prev()
/*      */     throws SQLException
/*      */   {
/* 7254 */     synchronized (checkClosed())
/*      */     {
/* 7256 */       int rowIndex = this.rowData.getCurrentRowNumber();
/*      */ 
/* 7258 */       if (this.thisRow != null) {
/* 7259 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */ 
/* 7262 */       boolean b = true;
/*      */ 
/* 7264 */       if (rowIndex - 1 >= 0) {
/* 7265 */         rowIndex--;
/* 7266 */         this.rowData.setCurrentRow(rowIndex);
/* 7267 */         this.thisRow = this.rowData.getAt(rowIndex);
/*      */ 
/* 7269 */         b = true;
/* 7270 */       } else if (rowIndex - 1 == -1) {
/* 7271 */         rowIndex--;
/* 7272 */         this.rowData.setCurrentRow(rowIndex);
/* 7273 */         this.thisRow = null;
/*      */ 
/* 7275 */         b = false;
/*      */       } else {
/* 7277 */         b = false;
/*      */       }
/*      */ 
/* 7280 */       setRowPositionValidity();
/*      */ 
/* 7282 */       return b;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean previous()
/*      */     throws SQLException
/*      */   {
/* 7305 */     synchronized (checkClosed()) {
/* 7306 */       if (this.onInsertRow) {
/* 7307 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/* 7310 */       if (this.doingUpdates) {
/* 7311 */         this.doingUpdates = false;
/*      */       }
/*      */ 
/* 7314 */       return prev();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/*      */     MySQLConnection locallyScopedConn;
/*      */     try
/*      */     {
/* 7331 */       locallyScopedConn = checkClosed();
/*      */     } catch (SQLException sqlEx) {
/* 7333 */       return;
/*      */     }
/*      */ 
/* 7336 */     synchronized (locallyScopedConn)
/*      */     {
/*      */       try {
/* 7339 */         if (this.useUsageAdvisor)
/*      */         {
/* 7343 */           if (!calledExplicitly) {
/* 7344 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver")));
/*      */           }
/*      */ 
/* 7363 */           if ((this.rowData instanceof RowDataStatic))
/*      */           {
/* 7367 */             if (this.rowData.size() > this.connection.getResultSetSizeThreshold())
/*      */             {
/* 7369 */               this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] { Integer.valueOf(this.rowData.size()), Integer.valueOf(this.connection.getResultSetSizeThreshold()) })));
/*      */             }
/*      */ 
/* 7397 */             if ((!isLast()) && (!isAfterLast()) && (this.rowData.size() != 0))
/*      */             {
/* 7399 */               this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] { Integer.valueOf(getRow()), Integer.valueOf(this.rowData.size()) })));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 7432 */           if ((this.columnUsed.length > 0) && (!this.rowData.wasEmpty())) {
/* 7433 */             StringBuffer buf = new StringBuffer(Messages.getString("ResultSet.The_following_columns_were_never_referenced"));
/*      */ 
/* 7437 */             boolean issueWarn = false;
/*      */ 
/* 7439 */             for (int i = 0; i < this.columnUsed.length; i++) {
/* 7440 */               if (this.columnUsed[i] == 0) {
/* 7441 */                 if (!issueWarn)
/* 7442 */                   issueWarn = true;
/*      */                 else {
/* 7444 */                   buf.append(", ");
/*      */                 }
/*      */ 
/* 7447 */                 buf.append(this.fields[i].getFullName());
/*      */               }
/*      */             }
/*      */ 
/* 7451 */             if (issueWarn) {
/* 7452 */               this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), 0, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, buf.toString()));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 7466 */         if ((this.owningStatement != null) && (calledExplicitly)) {
/* 7467 */           this.owningStatement.removeOpenResultSet(this);
/*      */         }
/*      */ 
/* 7470 */         SQLException exceptionDuringClose = null;
/*      */ 
/* 7472 */         if (this.rowData != null) {
/*      */           try {
/* 7474 */             this.rowData.close();
/*      */           } catch (SQLException sqlEx) {
/* 7476 */             exceptionDuringClose = sqlEx;
/*      */           }
/*      */         }
/*      */ 
/* 7480 */         if (this.statementUsedForFetchingRows != null) {
/*      */           try {
/* 7482 */             this.statementUsedForFetchingRows.realClose(true, false);
/*      */           } catch (SQLException sqlEx) {
/* 7484 */             if (exceptionDuringClose != null)
/* 7485 */               exceptionDuringClose.setNextException(sqlEx);
/*      */             else {
/* 7487 */               exceptionDuringClose = sqlEx;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 7492 */         this.rowData = null;
/* 7493 */         this.defaultTimeZone = null;
/* 7494 */         this.fields = null;
/* 7495 */         this.columnLabelToIndex = null;
/* 7496 */         this.fullColumnNameToIndex = null;
/* 7497 */         this.columnToIndexCache = null;
/* 7498 */         this.eventSink = null;
/* 7499 */         this.warningChain = null;
/*      */ 
/* 7501 */         if (!this.retainOwningStatement) {
/* 7502 */           this.owningStatement = null;
/*      */         }
/*      */ 
/* 7505 */         this.catalog = null;
/* 7506 */         this.serverInfo = null;
/* 7507 */         this.thisRow = null;
/* 7508 */         this.fastDateCal = null;
/* 7509 */         this.connection = null;
/*      */ 
/* 7511 */         this.isClosed = true;
/*      */ 
/* 7513 */         if (exceptionDuringClose != null)
/* 7514 */           throw exceptionDuringClose;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean reallyResult()
/*      */   {
/* 7521 */     if (this.rowData != null) {
/* 7522 */       return true;
/*      */     }
/*      */ 
/* 7525 */     return this.reallyResult;
/*      */   }
/*      */ 
/*      */   public void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 7549 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 7579 */     synchronized (checkClosed())
/*      */     {
/* 7581 */       if (this.rowData.size() == 0) {
/* 7582 */         setRowPositionValidity();
/*      */ 
/* 7584 */         return false;
/*      */       }
/*      */ 
/* 7587 */       if (this.thisRow != null) {
/* 7588 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */ 
/* 7591 */       this.rowData.moveRowRelative(rows);
/* 7592 */       this.thisRow = this.rowData.getAt(this.rowData.getCurrentRowNumber());
/*      */ 
/* 7594 */       setRowPositionValidity();
/*      */ 
/* 7596 */       return (!this.rowData.isAfterLast()) && (!this.rowData.isBeforeFirst());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 7616 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 7634 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 7652 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected void setBinaryEncoded()
/*      */   {
/* 7660 */     this.isBinaryEncoded = true;
/*      */   }
/*      */ 
/*      */   private void setDefaultTimeZone(TimeZone defaultTimeZone) throws SQLException {
/* 7664 */     synchronized (checkClosed()) {
/* 7665 */       this.defaultTimeZone = defaultTimeZone;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 7685 */     synchronized (checkClosed()) {
/* 7686 */       if ((direction != 1000) && (direction != 1001) && (direction != 1002))
/*      */       {
/* 7688 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 7694 */       this.fetchDirection = direction;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 7715 */     synchronized (checkClosed()) {
/* 7716 */       if (rows < 0) {
/* 7717 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 7723 */       this.fetchSize = rows;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFirstCharOfQuery(char c)
/*      */   {
/*      */     try
/*      */     {
/* 7736 */       synchronized (checkClosed()) {
/* 7737 */         this.firstCharOfQuery = c;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7740 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void setNextResultSet(ResultSetInternalMethods nextResultSet)
/*      */   {
/* 7752 */     this.nextResultSet = nextResultSet;
/*      */   }
/*      */ 
/*      */   public void setOwningStatement(StatementImpl owningStatement) {
/*      */     try {
/* 7757 */       synchronized (checkClosed()) {
/* 7758 */         this.owningStatement = owningStatement;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7761 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/*      */     try
/*      */     {
/* 7773 */       synchronized (checkClosed()) {
/* 7774 */         this.resultSetConcurrency = concurrencyFlag;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7777 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void setResultSetType(int typeFlag)
/*      */   {
/*      */     try
/*      */     {
/* 7790 */       synchronized (checkClosed()) {
/* 7791 */         this.resultSetType = typeFlag;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7794 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void setServerInfo(String info)
/*      */   {
/*      */     try
/*      */     {
/* 7806 */       synchronized (checkClosed()) {
/* 7807 */         this.serverInfo = info;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7810 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setStatementUsedForFetchingRows(PreparedStatement stmt) {
/*      */     try {
/* 7816 */       synchronized (checkClosed()) {
/* 7817 */         this.statementUsedForFetchingRows = stmt;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7820 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setWrapperStatement(Statement wrapperStatement)
/*      */   {
/*      */     try
/*      */     {
/* 7830 */       synchronized (checkClosed()) {
/* 7831 */         this.wrapperStatement = wrapperStatement;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7834 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException
/*      */   {
/* 7840 */     String datatype = null;
/*      */ 
/* 7842 */     switch (jdbcType) {
/*      */     case -6:
/* 7844 */       datatype = "TINYINT";
/* 7845 */       break;
/*      */     case 5:
/* 7847 */       datatype = "SMALLINT";
/* 7848 */       break;
/*      */     case 4:
/* 7850 */       datatype = "INTEGER";
/* 7851 */       break;
/*      */     case -5:
/* 7853 */       datatype = "BIGINT";
/* 7854 */       break;
/*      */     case 7:
/* 7856 */       datatype = "REAL";
/* 7857 */       break;
/*      */     case 6:
/* 7859 */       datatype = "FLOAT";
/* 7860 */       break;
/*      */     case 8:
/* 7862 */       datatype = "DOUBLE";
/* 7863 */       break;
/*      */     case 3:
/* 7865 */       datatype = "DECIMAL";
/* 7866 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     default:
/* 7868 */       datatype = " (JDBC type '" + jdbcType + "')";
/*      */     }
/*      */ 
/* 7871 */     throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 7882 */     if (this.reallyResult) {
/* 7883 */       return super.toString();
/*      */     }
/*      */ 
/* 7886 */     return "Result set representing update count of " + this.updateCount;
/*      */   }
/*      */ 
/*      */   public void updateArray(int arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 7893 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateArray(String arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 7900 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 7924 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 7946 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 7967 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 7986 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 8010 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 8032 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 8039 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBlob(String arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 8046 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 8066 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 8084 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 8104 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 8122 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 8142 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 8160 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 8184 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 8206 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */   public void updateClob(int arg0, java.sql.Clob arg1)
/*      */     throws SQLException
/*      */   {
/* 8213 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateClob(String columnName, java.sql.Clob clob)
/*      */     throws SQLException
/*      */   {
/* 8221 */     updateClob(findColumn(columnName), clob);
/*      */   }
/*      */ 
/*      */   public void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 8242 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 8261 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 8281 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 8299 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 8319 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 8337 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 8357 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 8375 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 8395 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 8413 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 8431 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 8447 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 8467 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 8492 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 8510 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 8533 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateRef(int arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 8540 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateRef(String arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 8547 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void updateRow()
/*      */     throws SQLException
/*      */   {
/* 8561 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 8581 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 8599 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 8619 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 8637 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 8658 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 8677 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 8699 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 8718 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 8733 */     return this.wasNullFlag;
/*      */   }
/*      */ 
/*      */   protected Calendar getGmtCalendar()
/*      */   {
/* 8740 */     if (this.gmtCalendar == null) {
/* 8741 */       this.gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */ 
/* 8744 */     return this.gmtCalendar;
/*      */   }
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 8748 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  126 */     if (Util.isJdbc4()) {
/*      */       try {
/*  128 */         JDBC_4_RS_4_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { Long.TYPE, Long.TYPE, MySQLConnection.class, StatementImpl.class });
/*      */ 
/*  133 */         JDBC_4_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4ResultSet").getConstructor(new Class[] { String.class, [Lcom.mysql.jdbc.Field.class, RowData.class, MySQLConnection.class, StatementImpl.class });
/*      */ 
/*  139 */         JDBC_4_UPD_RS_6_ARG_CTOR = Class.forName("com.mysql.jdbc.JDBC4UpdatableResultSet").getConstructor(new Class[] { String.class, [Lcom.mysql.jdbc.Field.class, RowData.class, MySQLConnection.class, StatementImpl.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  147 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  149 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  151 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  154 */       JDBC_4_RS_4_ARG_CTOR = null;
/*  155 */       JDBC_4_RS_6_ARG_CTOR = null;
/*  156 */       JDBC_4_UPD_RS_6_ARG_CTOR = null;
/*      */     }
/*      */ 
/*  163 */     MIN_DIFF_PREC = Float.parseFloat(Float.toString(1.4E-45F)) - Double.parseDouble(Float.toString(1.4E-45F));
/*      */ 
/*  169 */     MAX_DIFF_PREC = Float.parseFloat(Float.toString(3.4028235E+38F)) - Double.parseDouble(Float.toString(3.4028235E+38F));
/*      */ 
/*  173 */     resultCounter = 1;
/*      */ 
/*  344 */     EMPTY_SPACE = new char['ÿ'];
/*      */ 
/*  347 */     for (int i = 0; i < EMPTY_SPACE.length; i++)
/*  348 */       EMPTY_SPACE[i] = ' ';
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetImpl
 * JD-Core Version:    0.6.0
 */