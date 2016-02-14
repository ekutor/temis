/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.InputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ 
/*      */ public class StatementImpl
/*      */   implements Statement
/*      */ {
/*      */   protected static final String PING_MARKER = "/* ping */";
/*  183 */   protected Object cancelTimeoutMutex = new Object();
/*      */ 
/*  186 */   static int statementCounter = 1;
/*      */   public static final byte USES_VARIABLES_FALSE = 0;
/*      */   public static final byte USES_VARIABLES_TRUE = 1;
/*      */   public static final byte USES_VARIABLES_UNKNOWN = -1;
/*  194 */   protected boolean wasCancelled = false;
/*  195 */   protected boolean wasCancelledByTimeout = false;
/*      */   protected List<Object> batchedArgs;
/*  201 */   protected SingleByteCharsetConverter charConverter = null;
/*      */ 
/*  204 */   protected String charEncoding = null;
/*      */ 
/*  207 */   protected volatile MySQLConnection connection = null;
/*      */ 
/*  209 */   protected long connectionId = 0L;
/*      */ 
/*  212 */   protected String currentCatalog = null;
/*      */ 
/*  215 */   protected boolean doEscapeProcessing = true;
/*      */ 
/*  218 */   protected ProfilerEventHandler eventSink = null;
/*      */ 
/*  221 */   private int fetchSize = 0;
/*      */ 
/*  224 */   protected boolean isClosed = false;
/*      */ 
/*  227 */   protected long lastInsertId = -1L;
/*      */ 
/*  230 */   protected int maxFieldSize = MysqlIO.getMaxBuf();
/*      */ 
/*  236 */   protected int maxRows = -1;
/*      */ 
/*  239 */   protected boolean maxRowsChanged = false;
/*      */ 
/*  242 */   protected Set<ResultSetInternalMethods> openResults = new HashSet();
/*      */ 
/*  245 */   protected boolean pedantic = false;
/*      */   protected String pointOfOrigin;
/*  254 */   protected boolean profileSQL = false;
/*      */ 
/*  257 */   protected ResultSetInternalMethods results = null;
/*      */ 
/*  259 */   protected ResultSetInternalMethods generatedKeysResults = null;
/*      */ 
/*  262 */   protected int resultSetConcurrency = 0;
/*      */ 
/*  265 */   protected int resultSetType = 0;
/*      */   protected int statementId;
/*  271 */   protected int timeoutInMillis = 0;
/*      */ 
/*  274 */   protected long updateCount = -1L;
/*      */ 
/*  277 */   protected boolean useUsageAdvisor = false;
/*      */ 
/*  280 */   protected SQLWarning warningChain = null;
/*      */ 
/*  283 */   protected boolean clearWarningsCalled = false;
/*      */ 
/*  289 */   protected boolean holdResultsOpenOverClose = false;
/*      */ 
/*  291 */   protected ArrayList<ResultSetRow> batchedGeneratedKeys = null;
/*      */ 
/*  293 */   protected boolean retrieveGeneratedKeys = false;
/*      */ 
/*  295 */   protected boolean continueBatchOnError = false;
/*      */ 
/*  297 */   protected PingTarget pingTarget = null;
/*      */   protected boolean useLegacyDatetimeCode;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*  304 */   protected boolean lastQueryIsOnDupKeyUpdate = false;
/*      */ 
/*  307 */   protected final AtomicBoolean statementExecuting = new AtomicBoolean(false);
/*      */ 
/*  696 */   private int originalResultSetType = 0;
/*  697 */   private int originalFetchSize = 0;
/*      */ 
/* 2932 */   private boolean isPoolable = true;
/*      */   private InputStream localInfileInputStream;
/*      */   protected final boolean version5013OrNewer;
/*      */   private boolean closeOnCompletion;
/*      */ 
/*      */   public StatementImpl(MySQLConnection c, String catalog)
/*      */     throws SQLException
/*      */   {
/*  321 */     if ((c == null) || (c.isClosed())) {
/*  322 */       throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", null);
/*      */     }
/*      */ 
/*  327 */     this.connection = c;
/*  328 */     this.connectionId = this.connection.getId();
/*  329 */     this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */ 
/*  332 */     this.currentCatalog = catalog;
/*  333 */     this.pedantic = this.connection.getPedantic();
/*  334 */     this.continueBatchOnError = this.connection.getContinueBatchOnError();
/*  335 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */ 
/*  337 */     if (!this.connection.getDontTrackOpenResources()) {
/*  338 */       this.connection.registerStatement(this);
/*      */     }
/*      */ 
/*  345 */     if (this.connection != null) {
/*  346 */       this.maxFieldSize = this.connection.getMaxAllowedPacket();
/*      */ 
/*  348 */       int defaultFetchSize = this.connection.getDefaultFetchSize();
/*      */ 
/*  350 */       if (defaultFetchSize != 0) {
/*  351 */         setFetchSize(defaultFetchSize);
/*      */       }
/*      */ 
/*  354 */       if (this.connection.getUseUnicode()) {
/*  355 */         this.charEncoding = this.connection.getEncoding();
/*      */ 
/*  357 */         this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */       }
/*      */ 
/*  362 */       boolean profiling = (this.connection.getProfileSql()) || (this.connection.getUseUsageAdvisor()) || (this.connection.getLogSlowQueries());
/*      */ 
/*  365 */       if ((this.connection.getAutoGenerateTestcaseScript()) || (profiling)) {
/*  366 */         this.statementId = (statementCounter++);
/*      */       }
/*      */ 
/*  369 */       if (profiling) {
/*  370 */         this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
/*  371 */         this.profileSQL = this.connection.getProfileSql();
/*  372 */         this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  373 */         this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */       }
/*      */ 
/*  376 */       int maxRowsConn = this.connection.getMaxRows();
/*      */ 
/*  378 */       if (maxRowsConn != -1) {
/*  379 */         setMaxRows(maxRowsConn);
/*      */       }
/*      */ 
/*  382 */       this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
/*      */     }
/*      */ 
/*  385 */     this.version5013OrNewer = this.connection.versionMeetsMinimum(5, 0, 13);
/*      */   }
/*      */ 
/*      */   public void addBatch(String sql)
/*      */     throws SQLException
/*      */   {
/*  398 */     synchronized (checkClosed()) {
/*  399 */       if (this.batchedArgs == null) {
/*  400 */         this.batchedArgs = new ArrayList();
/*      */       }
/*      */ 
/*  403 */       if (sql != null)
/*  404 */         this.batchedArgs.add(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<Object> getBatchedArgs()
/*      */   {
/*  416 */     return this.batchedArgs == null ? null : Collections.unmodifiableList(this.batchedArgs);
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*  425 */     if (!this.statementExecuting.get()) {
/*  426 */       return;
/*      */     }
/*      */ 
/*  429 */     if ((!this.isClosed) && (this.connection != null) && (this.connection.versionMeetsMinimum(5, 0, 0)))
/*      */     {
/*  432 */       Connection cancelConn = null;
/*  433 */       java.sql.Statement cancelStmt = null;
/*      */       try
/*      */       {
/*  436 */         cancelConn = this.connection.duplicate();
/*  437 */         cancelStmt = cancelConn.createStatement();
/*  438 */         cancelStmt.execute("KILL QUERY " + this.connection.getIO().getThreadId());
/*      */ 
/*  440 */         this.wasCancelled = true;
/*      */       } finally {
/*  442 */         if (cancelStmt != null) {
/*  443 */           cancelStmt.close();
/*      */         }
/*      */ 
/*  446 */         if (cancelConn != null)
/*  447 */           cancelConn.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected MySQLConnection checkClosed()
/*      */     throws SQLException
/*      */   {
/*  463 */     MySQLConnection c = this.connection;
/*      */ 
/*  465 */     if (c == null) {
/*  466 */       throw SQLError.createSQLException(Messages.getString("Statement.49"), "08003", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  471 */     return c;
/*      */   }
/*      */ 
/*      */   protected void checkForDml(String sql, char firstStatementChar)
/*      */     throws SQLException
/*      */   {
/*  488 */     if ((firstStatementChar == 'I') || (firstStatementChar == 'U') || (firstStatementChar == 'D') || (firstStatementChar == 'A') || (firstStatementChar == 'C') || (firstStatementChar == 'T') || (firstStatementChar == 'R'))
/*      */     {
/*  492 */       String noCommentSql = StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
/*      */ 
/*  495 */       if ((StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DROP")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "CREATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "ALTER")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "TRUNCATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "RENAME")))
/*      */       {
/*  504 */         throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkNullOrEmptyQuery(String sql)
/*      */     throws SQLException
/*      */   {
/*  521 */     if (sql == null) {
/*  522 */       throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  527 */     if (sql.length() == 0)
/*  528 */       throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public void clearBatch()
/*      */     throws SQLException
/*      */   {
/*  543 */     synchronized (checkClosed()) {
/*  544 */       if (this.batchedArgs != null)
/*  545 */         this.batchedArgs.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  558 */     synchronized (checkClosed()) {
/*  559 */       this.clearWarningsCalled = true;
/*  560 */       this.warningChain = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  581 */       synchronized (checkClosed()) {
/*  582 */         realClose(true, true);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  585 */       if ("08003".equals(sqlEx.getSQLState())) {
/*  586 */         return;
/*      */       }
/*      */ 
/*  589 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void closeAllOpenResults()
/*      */     throws SQLException
/*      */   {
/*  597 */     synchronized (checkClosed()) {
/*  598 */       if (this.openResults != null) {
/*  599 */         for (ResultSetInternalMethods element : this.openResults) {
/*      */           try {
/*  601 */             element.realClose(false);
/*      */           } catch (SQLException sqlEx) {
/*  603 */             AssertionFailedException.shouldNotHappen(sqlEx);
/*      */           }
/*      */         }
/*      */ 
/*  607 */         this.openResults.clear();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeOpenResultSet(ResultSet rs) {
/*      */     try {
/*  614 */       synchronized (checkClosed()) {
/*  615 */         if (this.openResults != null)
/*  616 */           this.openResults.remove(rs);
/*      */       }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getOpenResultSetCount() {
/*      */     try {
/*  626 */       synchronized (checkClosed()) {
/*  627 */         if (this.openResults != null) {
/*  628 */           return this.openResults.size();
/*      */         }
/*      */ 
/*  631 */         return 0;
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {
/*      */     }
/*  636 */     return 0;
/*      */   }
/*      */ 
/*      */   private ResultSetInternalMethods createResultSetUsingServerFetch(String sql)
/*      */     throws SQLException
/*      */   {
/*  646 */     synchronized (checkClosed()) {
/*  647 */       java.sql.PreparedStatement pStmt = this.connection.prepareStatement(sql, this.resultSetType, this.resultSetConcurrency);
/*      */ 
/*  650 */       pStmt.setFetchSize(this.fetchSize);
/*      */ 
/*  652 */       if (this.maxRows > -1) {
/*  653 */         pStmt.setMaxRows(this.maxRows);
/*      */       }
/*      */ 
/*  656 */       statementBegins();
/*      */ 
/*  658 */       pStmt.execute();
/*      */ 
/*  664 */       ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
/*      */ 
/*  667 */       rs.setStatementUsedForFetchingRows((PreparedStatement)pStmt);
/*      */ 
/*  670 */       this.results = rs;
/*      */ 
/*  672 */       return rs;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean createStreamingResultSet()
/*      */   {
/*      */     try
/*      */     {
/*  685 */       synchronized (checkClosed()) {
/*  686 */         return (this.resultSetType == 1003) && (this.resultSetConcurrency == 1007) && (this.fetchSize == -2147483648);
/*      */       }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*  692 */     return false;
/*      */   }
/*      */ 
/*      */   public void enableStreamingResults()
/*      */     throws SQLException
/*      */   {
/*  703 */     synchronized (checkClosed()) {
/*  704 */       this.originalResultSetType = this.resultSetType;
/*  705 */       this.originalFetchSize = this.fetchSize;
/*      */ 
/*  707 */       setFetchSize(-2147483648);
/*  708 */       setResultSetType(1003);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disableStreamingResults() throws SQLException {
/*  713 */     synchronized (checkClosed()) {
/*  714 */       if ((this.fetchSize == -2147483648) && (this.resultSetType == 1003))
/*      */       {
/*  716 */         setFetchSize(this.originalFetchSize);
/*  717 */         setResultSetType(this.originalResultSetType);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql)
/*      */     throws SQLException
/*      */   {
/*  737 */     return execute(sql, false);
/*      */   }
/*      */ 
/*      */   private boolean execute(String sql, boolean returnGeneratedKeys) throws SQLException {
/*  741 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */     char firstNonWsChar;
/*      */     boolean isSelect;
/*      */     boolean doStreaming;
/*  743 */     synchronized (locallyScopedConn) {
/*  744 */       this.retrieveGeneratedKeys = returnGeneratedKeys;
/*  745 */       this.lastQueryIsOnDupKeyUpdate = false;
/*  746 */       if (returnGeneratedKeys) {
/*  747 */         this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyInString(sql);
/*      */       }
/*  749 */       resetCancelledState();
/*      */ 
/*  751 */       checkNullOrEmptyQuery(sql);
/*      */ 
/*  753 */       checkClosed();
/*      */ 
/*  755 */       firstNonWsChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*      */ 
/*  757 */       isSelect = true;
/*      */ 
/*  759 */       if (firstNonWsChar != 'S') {
/*  760 */         isSelect = false;
/*      */ 
/*  762 */         if (locallyScopedConn.isReadOnly()) {
/*  763 */           throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  770 */       doStreaming = createStreamingResultSet();
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  781 */       if ((doStreaming) && (locallyScopedConn.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/*  783 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + locallyScopedConn.getNetTimeoutForStreamingResults());
/*      */       }
/*      */       Object escapedSqlResult;
/*  787 */       if (this.doEscapeProcessing) {
/*  788 */         escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), locallyScopedConn);
/*      */ 
/*  791 */         if ((escapedSqlResult instanceof String))
/*  792 */           sql = (String)escapedSqlResult;
/*      */         else {
/*  794 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */ 
/*  798 */       if (!locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
/*  799 */         if (this.results != null) {
/*  800 */           this.results.realClose(false);
/*      */         }
/*  802 */         if (this.generatedKeysResults != null) {
/*  803 */           this.generatedKeysResults.realClose(false);
/*      */         }
/*  805 */         closeAllOpenResults();
/*      */       }
/*      */ 
/*  808 */       if ((sql.charAt(0) == '/') && 
/*  809 */         (sql.startsWith("/* ping */"))) {
/*  810 */         doPingInstead();
/*      */ 
/*  812 */         escapedSqlResult = 1; jsr 595; monitorexit; return escapedSqlResult;
/*      */       }
/*      */ 
/*  816 */       CachedResultSetMetaData cachedMetaData = null;
/*      */ 
/*  818 */       ResultSetInternalMethods rs = null;
/*      */ 
/*  827 */       this.batchedGeneratedKeys = null;
/*      */ 
/*  829 */       if (useServerFetch()) {
/*  830 */         rs = createResultSetUsingServerFetch(sql);
/*      */       } else {
/*  832 */         timeoutTask = null;
/*      */ 
/*  834 */         String oldCatalog = null;
/*      */         try
/*      */         {
/*  837 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/*  840 */             timeoutTask = new CancelTask(this);
/*  841 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */           }
/*      */ 
/*  845 */           if (!locallyScopedConn.getCatalog().equals(this.currentCatalog))
/*      */           {
/*  847 */             oldCatalog = locallyScopedConn.getCatalog();
/*  848 */             locallyScopedConn.setCatalog(this.currentCatalog);
/*      */           }
/*      */ 
/*  855 */           Field[] cachedFields = null;
/*      */ 
/*  857 */           if (locallyScopedConn.getCacheResultSetMetadata()) {
/*  858 */             cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
/*      */ 
/*  860 */             if (cachedMetaData != null) {
/*  861 */               cachedFields = cachedMetaData.fields;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  868 */           if (locallyScopedConn.useMaxRows()) {
/*  869 */             int rowLimit = -1;
/*      */ 
/*  871 */             if (isSelect) {
/*  872 */               if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
/*  873 */                 rowLimit = this.maxRows;
/*      */               }
/*  875 */               else if (this.maxRows <= 0) {
/*  876 */                 executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */               }
/*      */               else {
/*  879 */                 executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=" + this.maxRows);
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/*  884 */               executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */             }
/*      */ 
/*  889 */             statementBegins();
/*      */ 
/*  892 */             rs = locallyScopedConn.execSQL(this, sql, rowLimit, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */           }
/*      */           else
/*      */           {
/*  897 */             statementBegins();
/*      */ 
/*  899 */             rs = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */           }
/*      */ 
/*  905 */           if (timeoutTask != null) {
/*  906 */             if (timeoutTask.caughtWhileCancelling != null) {
/*  907 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */ 
/*  910 */             timeoutTask.cancel();
/*  911 */             timeoutTask = null;
/*      */           }
/*      */ 
/*  914 */           synchronized (this.cancelTimeoutMutex) {
/*  915 */             if (this.wasCancelled) {
/*  916 */               SQLException cause = null;
/*      */ 
/*  918 */               if (this.wasCancelledByTimeout)
/*  919 */                 cause = new MySQLTimeoutException();
/*      */               else {
/*  921 */                 cause = new MySQLStatementCancelledException();
/*      */               }
/*      */ 
/*  924 */               resetCancelledState();
/*      */ 
/*  926 */               throw cause;
/*      */             }
/*      */           }
/*      */         } finally {
/*  930 */           if (timeoutTask != null) {
/*  931 */             timeoutTask.cancel();
/*  932 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */ 
/*  935 */           if (oldCatalog != null) {
/*  936 */             locallyScopedConn.setCatalog(oldCatalog);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  941 */       if (rs != null) {
/*  942 */         this.lastInsertId = rs.getUpdateID();
/*      */ 
/*  944 */         this.results = rs;
/*      */ 
/*  946 */         rs.setFirstCharOfQuery(firstNonWsChar);
/*      */ 
/*  948 */         if (rs.reallyResult()) {
/*  949 */           if (cachedMetaData != null) {
/*  950 */             locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */           }
/*  953 */           else if (this.connection.getCacheResultSetMetadata()) {
/*  954 */             locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  961 */       CancelTask timeoutTask = (rs != null) && (rs.reallyResult()) ? 1 : 0; jsr 17; monitorexit; return timeoutTask;
/*      */     } finally {
/*  963 */       jsr 6; } localObject5 = returnAddress; this.statementExecuting.set(false); ret;
/*      */ 
/*  965 */     localObject6 = finally;
/*      */ 
/*  965 */     monitorexit; throw localObject6;
/*      */   }
/*      */ 
/*      */   protected void statementBegins() {
/*  969 */     this.clearWarningsCalled = false;
/*  970 */     this.statementExecuting.set(true);
/*      */   }
/*      */ 
/*      */   protected void resetCancelledState() throws SQLException {
/*  974 */     synchronized (checkClosed()) {
/*  975 */       if (this.cancelTimeoutMutex == null) {
/*  976 */         return;
/*      */       }
/*      */ 
/*  979 */       synchronized (this.cancelTimeoutMutex) {
/*  980 */         this.wasCancelled = false;
/*  981 */         this.wasCancelledByTimeout = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  993 */     if (returnGeneratedKeys == 1) {
/*  994 */       checkClosed();
/*      */ 
/*  996 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/*  998 */       synchronized (locallyScopedConn)
/*      */       {
/* 1002 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/* 1004 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1007 */           boolean bool1 = execute(sql, true); jsr 17; return bool1;
/*      */         } finally {
/* 1009 */           jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1014 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/* 1022 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */ 
/* 1024 */     synchronized (locallyScopedConn) {
/* 1025 */       if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0))
/*      */       {
/* 1027 */         this.retrieveGeneratedKeys = true;
/*      */ 
/* 1032 */         boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*      */ 
/* 1034 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1037 */           boolean bool1 = execute(sql, true); jsr 17; return bool1;
/*      */         } finally {
/* 1039 */           jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
/*      */       }
/*      */ 
/* 1043 */       return execute(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/* 1052 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */ 
/* 1054 */     synchronized (locallyScopedConn) {
/* 1055 */       if ((generatedKeyNames != null) && (generatedKeyNames.length > 0))
/*      */       {
/* 1057 */         this.retrieveGeneratedKeys = true;
/*      */ 
/* 1061 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/* 1063 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1066 */           boolean bool1 = execute(sql, true); jsr 17; return bool1;
/*      */         } finally {
/* 1068 */           jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
/*      */       }
/*      */ 
/* 1072 */       return execute(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 1091 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */ 
/* 1093 */     synchronized (locallyScopedConn) {
/* 1094 */       if (locallyScopedConn.isReadOnly()) {
/* 1095 */         throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1101 */       if (!locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
/* 1102 */         if (this.results != null) {
/* 1103 */           this.results.realClose(false);
/*      */         }
/* 1105 */         if (this.generatedKeysResults != null) {
/* 1106 */           this.generatedKeysResults.realClose(false);
/*      */         }
/* 1108 */         closeAllOpenResults();
/*      */       }
/*      */ 
/* 1111 */       if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
/* 1112 */         return new int[0];
/*      */       }
/*      */ 
/* 1116 */       int individualStatementTimeout = this.timeoutInMillis;
/* 1117 */       this.timeoutInMillis = 0;
/*      */ 
/* 1119 */       CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1122 */         resetCancelledState();
/*      */ 
/* 1124 */         statementBegins();
/*      */         try
/*      */         {
/* 1127 */           this.retrieveGeneratedKeys = true;
/*      */ 
/* 1129 */           int[] updateCounts = null;
/*      */ 
/* 1132 */           if (this.batchedArgs != null) {
/* 1133 */             nbrCommands = this.batchedArgs.size();
/*      */ 
/* 1135 */             this.batchedGeneratedKeys = new ArrayList(this.batchedArgs.size());
/*      */ 
/* 1137 */             boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
/*      */ 
/* 1139 */             if ((locallyScopedConn.versionMeetsMinimum(4, 1, 1)) && ((multiQueriesEnabled) || ((locallyScopedConn.getRewriteBatchedStatements()) && (nbrCommands > 4))))
/*      */             {
/* 1143 */               int[] arrayOfInt1 = executeBatchUsingMultiQueries(multiQueriesEnabled, nbrCommands, individualStatementTimeout); jsr 389; return arrayOfInt1;
/*      */             }
/*      */ 
/* 1146 */             if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */             {
/* 1149 */               timeoutTask = new CancelTask(this);
/* 1150 */               locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
/*      */             }
/*      */ 
/* 1154 */             updateCounts = new int[nbrCommands];
/*      */ 
/* 1156 */             for (int i = 0; i < nbrCommands; i++) {
/* 1157 */               updateCounts[i] = -3;
/*      */             }
/*      */ 
/* 1160 */             SQLException sqlEx = null;
/*      */ 
/* 1162 */             int commandIndex = 0;
/*      */ 
/* 1164 */             for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*      */               try {
/* 1166 */                 String sql = (String)this.batchedArgs.get(commandIndex);
/* 1167 */                 updateCounts[commandIndex] = executeUpdate(sql, true, true);
/*      */ 
/* 1169 */                 getBatchedGeneratedKeys(containsOnDuplicateKeyInString(sql) ? 1 : 0);
/*      */               } catch (SQLException ex) {
/* 1171 */                 updateCounts[commandIndex] = -3;
/*      */ 
/* 1173 */                 if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */                 {
/* 1177 */                   sqlEx = ex;
/*      */                 } else {
/* 1179 */                   int[] newUpdateCounts = new int[commandIndex];
/*      */ 
/* 1181 */                   if (hasDeadlockOrTimeoutRolledBackTx(ex)) {
/* 1182 */                     for (int i = 0; i < newUpdateCounts.length; i++)
/* 1183 */                       newUpdateCounts[i] = -3;
/*      */                   }
/*      */                   else {
/* 1186 */                     System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */                   }
/*      */ 
/* 1190 */                   throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1197 */             if (sqlEx != null) {
/* 1198 */               throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1204 */           if (timeoutTask != null) {
/* 1205 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 1206 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */ 
/* 1209 */             timeoutTask.cancel();
/*      */ 
/* 1211 */             locallyScopedConn.getCancelTimer().purge();
/* 1212 */             timeoutTask = null;
/*      */           }
/*      */ 
/* 1215 */           int nbrCommands = updateCounts != null ? updateCounts : new int[0]; jsr 19; return nbrCommands;
/*      */         } finally {
/* 1217 */           this.statementExecuting.set(false);
/*      */         }
/*      */       }
/*      */       finally {
/* 1221 */         jsr 6; } localObject4 = returnAddress; if (timeoutTask != null) {
/* 1222 */         timeoutTask.cancel();
/*      */ 
/* 1224 */         locallyScopedConn.getCancelTimer().purge();
/*      */       }
/*      */ 
/* 1227 */       resetCancelledState();
/*      */ 
/* 1229 */       this.timeoutInMillis = individualStatementTimeout;
/*      */ 
/* 1231 */       clearBatch(); ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex)
/*      */   {
/* 1237 */     int vendorCode = ex.getErrorCode();
/*      */ 
/* 1239 */     switch (vendorCode) {
/*      */     case 1206:
/*      */     case 1213:
/* 1242 */       return true;
/*      */     case 1205:
/* 1244 */       return !this.version5013OrNewer;
/*      */     }
/* 1246 */     return false;
/*      */   }
/*      */ 
/*      */   private int[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout)
/*      */     throws SQLException
/*      */   {
/* 1261 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */ 
/* 1263 */     synchronized (locallyScopedConn) {
/* 1264 */       if (!multiQueriesEnabled) {
/* 1265 */         locallyScopedConn.getIO().enableMultiQueries();
/*      */       }
/*      */ 
/* 1268 */       java.sql.Statement batchStmt = null;
/*      */ 
/* 1270 */       CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1273 */         int[] updateCounts = new int[nbrCommands];
/*      */ 
/* 1275 */         for (int i = 0; i < nbrCommands; i++) {
/* 1276 */           updateCounts[i] = -3;
/*      */         }
/*      */ 
/* 1279 */         int commandIndex = 0;
/*      */ 
/* 1281 */         StringBuffer queryBuf = new StringBuffer();
/*      */ 
/* 1283 */         batchStmt = locallyScopedConn.createStatement();
/*      */ 
/* 1285 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1288 */           timeoutTask = new CancelTask((StatementImpl)batchStmt);
/* 1289 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
/*      */         }
/*      */ 
/* 1293 */         int counter = 0;
/*      */ 
/* 1295 */         int numberOfBytesPerChar = 1;
/*      */ 
/* 1297 */         String connectionEncoding = locallyScopedConn.getEncoding();
/*      */ 
/* 1299 */         if (StringUtils.startsWithIgnoreCase(connectionEncoding, "utf"))
/* 1300 */           numberOfBytesPerChar = 3;
/* 1301 */         else if (CharsetMapping.isMultibyteCharset(connectionEncoding)) {
/* 1302 */           numberOfBytesPerChar = 2;
/*      */         }
/*      */ 
/* 1305 */         int escapeAdjust = 1;
/*      */ 
/* 1307 */         batchStmt.setEscapeProcessing(this.doEscapeProcessing);
/*      */ 
/* 1309 */         if (this.doEscapeProcessing)
/*      */         {
/* 1311 */           escapeAdjust = 2;
/*      */         }
/*      */ 
/* 1316 */         SQLException sqlEx = null;
/*      */ 
/* 1318 */         int argumentSetsInBatchSoFar = 0;
/*      */ 
/* 1320 */         for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 1321 */           String nextQuery = (String)this.batchedArgs.get(commandIndex);
/*      */ 
/* 1323 */           if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > this.connection.getMaxAllowedPacket())
/*      */           {
/*      */             try
/*      */             {
/* 1328 */               batchStmt.execute(queryBuf.toString(), 1);
/*      */             } catch (SQLException ex) {
/* 1330 */               sqlEx = handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, ex);
/*      */             }
/*      */ 
/* 1334 */             counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
/*      */ 
/* 1337 */             queryBuf = new StringBuffer();
/* 1338 */             argumentSetsInBatchSoFar = 0;
/*      */           }
/*      */ 
/* 1341 */           queryBuf.append(nextQuery);
/* 1342 */           queryBuf.append(";");
/* 1343 */           argumentSetsInBatchSoFar++;
/*      */         }
/*      */ 
/* 1346 */         if (queryBuf.length() > 0) {
/*      */           try {
/* 1348 */             batchStmt.execute(queryBuf.toString(), 1);
/*      */           } catch (SQLException ex) {
/* 1350 */             sqlEx = handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, ex);
/*      */           }
/*      */ 
/* 1354 */           counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
/*      */         }
/*      */ 
/* 1358 */         if (timeoutTask != null) {
/* 1359 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1360 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1363 */           timeoutTask.cancel();
/*      */ 
/* 1365 */           locallyScopedConn.getCancelTimer().purge();
/*      */ 
/* 1367 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1370 */         if (sqlEx != null) {
/* 1371 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */ 
/* 1376 */         ex = updateCounts != null ? updateCounts : new int[0]; jsr 17; return ex;
/*      */       } finally {
/* 1378 */         jsr 6; } localObject2 = returnAddress; if (timeoutTask != null) {
/* 1379 */         timeoutTask.cancel();
/*      */ 
/* 1381 */         locallyScopedConn.getCancelTimer().purge();
/*      */       }
/*      */ 
/* 1384 */       resetCancelledState();
/*      */       try
/*      */       {
/* 1387 */         if (batchStmt != null)
/* 1388 */           batchStmt.close();
/*      */       }
/*      */       finally {
/* 1391 */         if (!multiQueriesEnabled)
/* 1392 */           locallyScopedConn.getIO().disableMultiQueries(); 
/*      */       }
/* 1392 */     }ret;
/*      */ 
/* 1396 */     localObject5 = finally;
/*      */ 
/* 1396 */     monitorexit; throw localObject5;
/*      */   }
/*      */ 
/*      */   protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, int[] updateCounts)
/*      */     throws SQLException
/*      */   {
/* 1402 */     synchronized (checkClosed()) {
/* 1403 */       updateCounts[(updateCountCounter++)] = batchedStatement.getUpdateCount();
/*      */ 
/* 1405 */       boolean doGenKeys = this.batchedGeneratedKeys != null;
/*      */ 
/* 1407 */       byte[][] row = (byte[][])null;
/*      */ 
/* 1409 */       if (doGenKeys) {
/* 1410 */         long generatedKey = batchedStatement.getLastInsertID();
/*      */ 
/* 1412 */         row = new byte[1][];
/* 1413 */         row[0] = StringUtils.getBytes(Long.toString(generatedKey));
/* 1414 */         this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */       }
/*      */ 
/* 1418 */       while ((batchedStatement.getMoreResults()) || (batchedStatement.getUpdateCount() != -1)) {
/* 1419 */         updateCounts[(updateCountCounter++)] = batchedStatement.getUpdateCount();
/*      */ 
/* 1421 */         if (doGenKeys) {
/* 1422 */           long generatedKey = batchedStatement.getLastInsertID();
/*      */ 
/* 1424 */           row = new byte[1][];
/* 1425 */           row[0] = StringUtils.getBytes(Long.toString(generatedKey));
/* 1426 */           this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */         }
/*      */       }
/*      */ 
/* 1430 */       return updateCountCounter;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, int[] updateCounts, SQLException ex)
/*      */     throws BatchUpdateException
/*      */   {
/* 1439 */     for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; j--)
/* 1440 */       updateCounts[j] = -3;
/*      */     SQLException sqlEx;
/* 1443 */     if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */     {
/* 1447 */       sqlEx = ex;
/*      */     } else {
/* 1449 */       int[] newUpdateCounts = new int[endOfBatchIndex];
/* 1450 */       System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
/*      */ 
/* 1453 */       BatchUpdateException batchException = new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */ 
/* 1456 */       batchException.initCause(ex);
/* 1457 */       throw batchException;
/*      */     }
/*      */     SQLException sqlEx;
/* 1460 */     return sqlEx;
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery(String sql)
/*      */     throws SQLException
/*      */   {
/* 1476 */     synchronized (checkClosed()) {
/* 1477 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1479 */       this.retrieveGeneratedKeys = false;
/*      */ 
/* 1481 */       resetCancelledState();
/*      */ 
/* 1483 */       checkNullOrEmptyQuery(sql);
/*      */ 
/* 1485 */       boolean doStreaming = createStreamingResultSet();
/*      */ 
/* 1495 */       if ((doStreaming) && (this.connection.getNetTimeoutForStreamingResults() > 0))
/*      */       {
/* 1497 */         executeSimpleNonQuery(locallyScopedConn, "SET net_write_timeout=" + this.connection.getNetTimeoutForStreamingResults());
/*      */       }
/*      */ 
/* 1501 */       if (this.doEscapeProcessing) {
/* 1502 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), this.connection);
/*      */ 
/* 1505 */         if ((escapedSqlResult instanceof String))
/* 1506 */           sql = (String)escapedSqlResult;
/*      */         else {
/* 1508 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */ 
/* 1512 */       char firstStatementChar = StringUtils.firstNonWsCharUc(sql, findStartOfStatement(sql));
/*      */ 
/* 1515 */       if ((sql.charAt(0) == '/') && 
/* 1516 */         (sql.startsWith("/* ping */"))) {
/* 1517 */         doPingInstead();
/*      */ 
/* 1519 */         return this.results;
/*      */       }
/*      */ 
/* 1523 */       checkForDml(sql, firstStatementChar);
/*      */ 
/* 1525 */       if (!locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
/* 1526 */         if (this.results != null) {
/* 1527 */           this.results.realClose(false);
/*      */         }
/* 1529 */         if (this.generatedKeysResults != null) {
/* 1530 */           this.generatedKeysResults.realClose(false);
/*      */         }
/* 1532 */         closeAllOpenResults();
/*      */       }
/*      */ 
/* 1535 */       CachedResultSetMetaData cachedMetaData = null;
/*      */ 
/* 1544 */       if (useServerFetch()) {
/* 1545 */         this.results = createResultSetUsingServerFetch(sql);
/*      */ 
/* 1547 */         return this.results;
/*      */       }
/*      */ 
/* 1550 */       CancelTask timeoutTask = null;
/*      */ 
/* 1552 */       String oldCatalog = null;
/*      */       try
/*      */       {
/* 1555 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1558 */           timeoutTask = new CancelTask(this);
/* 1559 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 1563 */         if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1564 */           oldCatalog = locallyScopedConn.getCatalog();
/* 1565 */           locallyScopedConn.setCatalog(this.currentCatalog);
/*      */         }
/*      */ 
/* 1572 */         Field[] cachedFields = null;
/*      */ 
/* 1574 */         if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1575 */           cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
/*      */ 
/* 1577 */           if (cachedMetaData != null) {
/* 1578 */             cachedFields = cachedMetaData.fields;
/*      */           }
/*      */         }
/*      */ 
/* 1582 */         if (locallyScopedConn.useMaxRows())
/*      */         {
/* 1587 */           if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
/* 1588 */             this.results = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */           }
/*      */           else
/*      */           {
/* 1594 */             if (this.maxRows <= 0) {
/* 1595 */               executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */             }
/*      */             else {
/* 1598 */               executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=" + this.maxRows);
/*      */             }
/*      */ 
/* 1602 */             statementBegins();
/*      */ 
/* 1604 */             this.results = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */ 
/* 1610 */             if (oldCatalog != null)
/* 1611 */               locallyScopedConn.setCatalog(oldCatalog);
/*      */           }
/*      */         }
/*      */         else {
/* 1615 */           statementBegins();
/*      */ 
/* 1617 */           this.results = locallyScopedConn.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, doStreaming, this.currentCatalog, cachedFields);
/*      */         }
/*      */ 
/* 1623 */         if (timeoutTask != null) {
/* 1624 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1625 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1628 */           timeoutTask.cancel();
/*      */ 
/* 1630 */           locallyScopedConn.getCancelTimer().purge();
/*      */ 
/* 1632 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1635 */         synchronized (this.cancelTimeoutMutex) {
/* 1636 */           if (this.wasCancelled) {
/* 1637 */             SQLException cause = null;
/*      */ 
/* 1639 */             if (this.wasCancelledByTimeout)
/* 1640 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 1642 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 1645 */             resetCancelledState();
/*      */ 
/* 1647 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 1651 */         this.statementExecuting.set(false);
/*      */ 
/* 1653 */         if (timeoutTask != null) {
/* 1654 */           timeoutTask.cancel();
/*      */ 
/* 1656 */           locallyScopedConn.getCancelTimer().purge();
/*      */         }
/*      */ 
/* 1659 */         if (oldCatalog != null) {
/* 1660 */           locallyScopedConn.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */ 
/* 1664 */       this.lastInsertId = this.results.getUpdateID();
/*      */ 
/* 1666 */       if (cachedMetaData != null) {
/* 1667 */         locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */       }
/* 1670 */       else if (this.connection.getCacheResultSetMetadata()) {
/* 1671 */         locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
/*      */       }
/*      */ 
/* 1676 */       return this.results;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void doPingInstead() throws SQLException {
/* 1681 */     synchronized (checkClosed()) {
/* 1682 */       if (this.pingTarget != null)
/* 1683 */         this.pingTarget.doPing();
/*      */       else {
/* 1685 */         this.connection.ping();
/*      */       }
/*      */ 
/* 1688 */       ResultSetInternalMethods fakeSelectOneResultSet = generatePingResultSet();
/* 1689 */       this.results = fakeSelectOneResultSet;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
/* 1694 */     synchronized (checkClosed()) {
/* 1695 */       Field[] fields = { new Field(null, "1", -5, 1) };
/* 1696 */       ArrayList rows = new ArrayList();
/* 1697 */       byte[] colVal = { 49 };
/*      */ 
/* 1699 */       rows.add(new ByteArrayRow(new byte[][] { colVal }, getExceptionInterceptor()));
/*      */ 
/* 1701 */       return (ResultSetInternalMethods)DatabaseMetaData.buildResultSet(fields, rows, this.connection);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void executeSimpleNonQuery(MySQLConnection c, String nonQuery)
/*      */     throws SQLException
/*      */   {
/* 1708 */     c.execSQL(this, nonQuery, -1, null, 1003, 1007, false, this.currentCatalog, null, false).close();
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql)
/*      */     throws SQLException
/*      */   {
/* 1730 */     return executeUpdate(sql, false, false);
/*      */   }
/*      */ 
/*      */   protected int executeUpdate(String sql, boolean isBatch, boolean returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/* 1736 */     synchronized (checkClosed()) {
/* 1737 */       MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1739 */       char firstStatementChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*      */ 
/* 1742 */       ResultSetInternalMethods rs = null;
/*      */ 
/* 1744 */       this.retrieveGeneratedKeys = returnGeneratedKeys;
/*      */ 
/* 1746 */       resetCancelledState();
/*      */ 
/* 1748 */       checkNullOrEmptyQuery(sql);
/*      */ 
/* 1750 */       if (this.doEscapeProcessing) {
/* 1751 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn(), this.connection);
/*      */ 
/* 1754 */         if ((escapedSqlResult instanceof String))
/* 1755 */           sql = (String)escapedSqlResult;
/*      */         else {
/* 1757 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */ 
/* 1761 */       if (locallyScopedConn.isReadOnly(false)) {
/* 1762 */         throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1768 */       if (StringUtils.startsWithIgnoreCaseAndWs(sql, "select")) {
/* 1769 */         throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1774 */       if (!locallyScopedConn.getHoldResultsOpenOverStatementClose()) {
/* 1775 */         if (this.results != null) {
/* 1776 */           this.results.realClose(false);
/*      */         }
/* 1778 */         if (this.generatedKeysResults != null) {
/* 1779 */           this.generatedKeysResults.realClose(false);
/*      */         }
/* 1781 */         closeAllOpenResults();
/*      */       }
/*      */ 
/* 1788 */       CancelTask timeoutTask = null;
/*      */ 
/* 1790 */       String oldCatalog = null;
/*      */       try
/*      */       {
/* 1793 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1796 */           timeoutTask = new CancelTask(this);
/* 1797 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 1801 */         if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1802 */           oldCatalog = locallyScopedConn.getCatalog();
/* 1803 */           locallyScopedConn.setCatalog(this.currentCatalog);
/*      */         }
/*      */ 
/* 1809 */         if (locallyScopedConn.useMaxRows()) {
/* 1810 */           executeSimpleNonQuery(locallyScopedConn, "SET SQL_SELECT_LIMIT=DEFAULT");
/*      */         }
/*      */ 
/* 1814 */         statementBegins();
/*      */ 
/* 1816 */         rs = locallyScopedConn.execSQL(this, sql, -1, null, 1003, 1007, false, this.currentCatalog, null, isBatch);
/*      */ 
/* 1823 */         if (timeoutTask != null) {
/* 1824 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1825 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1828 */           timeoutTask.cancel();
/*      */ 
/* 1830 */           locallyScopedConn.getCancelTimer().purge();
/*      */ 
/* 1832 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1835 */         synchronized (this.cancelTimeoutMutex) {
/* 1836 */           if (this.wasCancelled) {
/* 1837 */             SQLException cause = null;
/*      */ 
/* 1839 */             if (this.wasCancelledByTimeout)
/* 1840 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 1842 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 1845 */             resetCancelledState();
/*      */ 
/* 1847 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 1851 */         if (timeoutTask != null) {
/* 1852 */           timeoutTask.cancel();
/*      */ 
/* 1854 */           locallyScopedConn.getCancelTimer().purge();
/*      */         }
/*      */ 
/* 1857 */         if (oldCatalog != null) {
/* 1858 */           locallyScopedConn.setCatalog(oldCatalog);
/*      */         }
/*      */ 
/* 1861 */         if (!isBatch) {
/* 1862 */           this.statementExecuting.set(false);
/*      */         }
/*      */       }
/*      */ 
/* 1866 */       this.results = rs;
/*      */ 
/* 1868 */       rs.setFirstCharOfQuery(firstStatementChar);
/*      */ 
/* 1870 */       this.updateCount = rs.getUpdateCount();
/*      */ 
/* 1872 */       int truncatedUpdateCount = 0;
/*      */ 
/* 1874 */       if (this.updateCount > 2147483647L)
/* 1875 */         truncatedUpdateCount = 2147483647;
/*      */       else {
/* 1877 */         truncatedUpdateCount = (int)this.updateCount;
/*      */       }
/*      */ 
/* 1880 */       this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 1882 */       return truncatedUpdateCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/* 1892 */     synchronized (checkClosed()) {
/* 1893 */       if (returnGeneratedKeys == 1) {
/* 1894 */         MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1899 */         boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*      */ 
/* 1901 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1904 */           int i = executeUpdate(sql, false, true); jsr 16; return i;
/*      */         } finally {
/* 1906 */           jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
/*      */       }
/*      */ 
/* 1910 */       return executeUpdate(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/* 1919 */     synchronized (checkClosed()) {
/* 1920 */       if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0)) {
/* 1921 */         checkClosed();
/*      */ 
/* 1923 */         MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1928 */         boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*      */ 
/* 1930 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1933 */           int i = executeUpdate(sql, false, true); jsr 16; return i;
/*      */         } finally {
/* 1935 */           jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
/*      */       }
/*      */ 
/* 1939 */       return executeUpdate(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/* 1948 */     synchronized (checkClosed()) {
/* 1949 */       if ((generatedKeyNames != null) && (generatedKeyNames.length > 0)) {
/* 1950 */         MySQLConnection locallyScopedConn = this.connection;
/*      */ 
/* 1954 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/* 1956 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1959 */           int i = executeUpdate(sql, false, true); jsr 16; return i;
/*      */         } finally {
/* 1961 */           jsr 6; } localObject2 = returnAddress; locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState); ret;
/*      */       }
/*      */ 
/* 1965 */       return executeUpdate(sql);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */     throws SQLException
/*      */   {
/* 1974 */     synchronized (checkClosed()) {
/* 1975 */       if (this.connection != null) {
/* 1976 */         return this.connection.getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */ 
/* 1979 */       return new GregorianCalendar();
/*      */     }
/*      */   }
/*      */ 
/*      */   public java.sql.Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 1992 */     synchronized (checkClosed()) {
/* 1993 */       return this.connection;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/* 2006 */     return 1000;
/*      */   }
/*      */ 
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 2018 */     synchronized (checkClosed()) {
/* 2019 */       return this.fetchSize;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 2033 */     synchronized (checkClosed()) {
/* 2034 */       if (!this.retrieveGeneratedKeys) {
/* 2035 */         throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2038 */       if (this.batchedGeneratedKeys == null) {
/* 2039 */         if (this.lastQueryIsOnDupKeyUpdate) {
/* 2040 */           return getGeneratedKeysInternal(1);
/*      */         }
/* 2042 */         return getGeneratedKeysInternal();
/*      */       }
/*      */ 
/* 2045 */       Field[] fields = new Field[1];
/* 2046 */       fields[0] = new Field("", "GENERATED_KEY", -5, 17);
/* 2047 */       fields[0].setConnection(this.connection);
/*      */ 
/* 2049 */       this.generatedKeysResults = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this, false);
/*      */ 
/* 2053 */       return this.generatedKeysResults;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ResultSet getGeneratedKeysInternal()
/*      */     throws SQLException
/*      */   {
/* 2064 */     int numKeys = getUpdateCount();
/* 2065 */     return getGeneratedKeysInternal(numKeys);
/*      */   }
/*      */ 
/*      */   protected ResultSet getGeneratedKeysInternal(int numKeys) throws SQLException
/*      */   {
/* 2070 */     synchronized (checkClosed()) {
/* 2071 */       Field[] fields = new Field[1];
/* 2072 */       fields[0] = new Field("", "GENERATED_KEY", -5, 17);
/* 2073 */       fields[0].setConnection(this.connection);
/* 2074 */       fields[0].setUseOldNameMetadata(true);
/*      */ 
/* 2076 */       ArrayList rowSet = new ArrayList();
/*      */ 
/* 2078 */       long beginAt = getLastInsertID();
/*      */ 
/* 2080 */       if (beginAt < 0L) {
/* 2081 */         fields[0].setUnsigned();
/*      */       }
/*      */ 
/* 2084 */       if (this.results != null) {
/* 2085 */         String serverInfo = this.results.getServerInfo();
/*      */ 
/* 2091 */         if ((numKeys > 0) && (this.results.getFirstCharOfQuery() == 'R') && (serverInfo != null) && (serverInfo.length() > 0))
/*      */         {
/* 2093 */           numKeys = getRecordCountFromInfo(serverInfo);
/*      */         }
/*      */ 
/* 2096 */         if ((beginAt != 0L) && (numKeys > 0)) {
/* 2097 */           for (int i = 0; i < numKeys; i++) {
/* 2098 */             byte[][] row = new byte[1][];
/* 2099 */             if (beginAt > 0L) {
/* 2100 */               row[0] = StringUtils.getBytes(Long.toString(beginAt));
/*      */             } else {
/* 2102 */               byte[] asBytes = new byte[8];
/* 2103 */               asBytes[7] = (byte)(int)(beginAt & 0xFF);
/* 2104 */               asBytes[6] = (byte)(int)(beginAt >>> 8);
/* 2105 */               asBytes[5] = (byte)(int)(beginAt >>> 16);
/* 2106 */               asBytes[4] = (byte)(int)(beginAt >>> 24);
/* 2107 */               asBytes[3] = (byte)(int)(beginAt >>> 32);
/* 2108 */               asBytes[2] = (byte)(int)(beginAt >>> 40);
/* 2109 */               asBytes[1] = (byte)(int)(beginAt >>> 48);
/* 2110 */               asBytes[0] = (byte)(int)(beginAt >>> 56);
/*      */ 
/* 2112 */               BigInteger val = new BigInteger(1, asBytes);
/*      */ 
/* 2114 */               row[0] = val.toString().getBytes();
/*      */             }
/* 2116 */             rowSet.add(new ByteArrayRow(row, getExceptionInterceptor()));
/* 2117 */             beginAt += this.connection.getAutoIncrementIncrement();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2122 */       ResultSetImpl gkRs = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this, false);
/*      */ 
/* 2125 */       this.openResults.add(gkRs);
/*      */ 
/* 2127 */       return gkRs;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getId()
/*      */   {
/* 2137 */     return this.statementId;
/*      */   }
/*      */ 
/*      */   public long getLastInsertID()
/*      */   {
/*      */     try
/*      */     {
/* 2155 */       synchronized (checkClosed()) {
/* 2156 */         return this.lastInsertId;
/*      */       }
/*      */     } catch (SQLException e) {
/*      */     }
/* 2159 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   public long getLongUpdateCount()
/*      */   {
/*      */     try
/*      */     {
/* 2177 */       synchronized (checkClosed()) {
/* 2178 */         if (this.results == null) {
/* 2179 */           return -1L;
/*      */         }
/*      */ 
/* 2182 */         if (this.results.reallyResult()) {
/* 2183 */           return -1L;
/*      */         }
/*      */ 
/* 2186 */         return this.updateCount;
/*      */       }
/*      */     } catch (SQLException e) {
/*      */     }
/* 2189 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   public int getMaxFieldSize()
/*      */     throws SQLException
/*      */   {
/* 2205 */     synchronized (checkClosed()) {
/* 2206 */       return this.maxFieldSize;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */     throws SQLException
/*      */   {
/* 2221 */     synchronized (checkClosed()) {
/* 2222 */       if (this.maxRows <= 0) {
/* 2223 */         return 0;
/*      */       }
/*      */ 
/* 2226 */       return this.maxRows;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults()
/*      */     throws SQLException
/*      */   {
/* 2240 */     return getMoreResults(1);
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults(int current)
/*      */     throws SQLException
/*      */   {
/* 2247 */     synchronized (checkClosed()) {
/* 2248 */       if (this.results == null) {
/* 2249 */         return false;
/*      */       }
/*      */ 
/* 2252 */       boolean streamingMode = createStreamingResultSet();
/*      */ 
/* 2254 */       while ((streamingMode) && 
/* 2255 */         (this.results.reallyResult()) && 
/* 2256 */         (this.results.next()));
/* 2261 */       ResultSetInternalMethods nextResultSet = this.results.getNextResultSet();
/*      */ 
/* 2263 */       switch (current)
/*      */       {
/*      */       case 1:
/* 2266 */         if (this.results == null) break;
/* 2267 */         if (!streamingMode) {
/* 2268 */           this.results.close();
/*      */         }
/*      */ 
/* 2271 */         this.results.clearNextResult(); break;
/*      */       case 3:
/* 2278 */         if (this.results != null) {
/* 2279 */           if (!streamingMode) {
/* 2280 */             this.results.close();
/*      */           }
/*      */ 
/* 2283 */           this.results.clearNextResult();
/*      */         }
/*      */ 
/* 2286 */         closeAllOpenResults();
/*      */ 
/* 2288 */         break;
/*      */       case 2:
/* 2291 */         if (!this.connection.getDontTrackOpenResources()) {
/* 2292 */           this.openResults.add(this.results);
/*      */         }
/*      */ 
/* 2295 */         this.results.clearNextResult();
/*      */ 
/* 2297 */         break;
/*      */       default:
/* 2300 */         throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2305 */       this.results = nextResultSet;
/*      */ 
/* 2307 */       if (this.results == null) {
/* 2308 */         this.updateCount = -1L;
/* 2309 */         this.lastInsertId = -1L;
/* 2310 */       } else if (this.results.reallyResult()) {
/* 2311 */         this.updateCount = -1L;
/* 2312 */         this.lastInsertId = -1L;
/*      */       } else {
/* 2314 */         this.updateCount = this.results.getUpdateCount();
/* 2315 */         this.lastInsertId = this.results.getUpdateID();
/*      */       }
/*      */ 
/* 2318 */       return (this.results != null) && (this.results.reallyResult());
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getQueryTimeout()
/*      */     throws SQLException
/*      */   {
/* 2334 */     synchronized (checkClosed()) {
/* 2335 */       return this.timeoutInMillis / 1000;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getRecordCountFromInfo(String serverInfo)
/*      */   {
/* 2348 */     StringBuffer recordsBuf = new StringBuffer();
/* 2349 */     int recordsCount = 0;
/* 2350 */     int duplicatesCount = 0;
/*      */ 
/* 2352 */     char c = '\000';
/*      */ 
/* 2354 */     int length = serverInfo.length();
/* 2355 */     int i = 0;
/*      */ 
/* 2357 */     for (; i < length; i++) {
/* 2358 */       c = serverInfo.charAt(i);
/*      */ 
/* 2360 */       if (Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 2365 */     recordsBuf.append(c);
/* 2366 */     i++;
/*      */ 
/* 2368 */     for (; i < length; i++) {
/* 2369 */       c = serverInfo.charAt(i);
/*      */ 
/* 2371 */       if (!Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/* 2375 */       recordsBuf.append(c);
/*      */     }
/*      */ 
/* 2378 */     recordsCount = Integer.parseInt(recordsBuf.toString());
/*      */ 
/* 2380 */     StringBuffer duplicatesBuf = new StringBuffer();
/*      */ 
/* 2382 */     for (; i < length; i++) {
/* 2383 */       c = serverInfo.charAt(i);
/*      */ 
/* 2385 */       if (Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 2390 */     duplicatesBuf.append(c);
/* 2391 */     i++;
/*      */ 
/* 2393 */     for (; i < length; i++) {
/* 2394 */       c = serverInfo.charAt(i);
/*      */ 
/* 2396 */       if (!Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/* 2400 */       duplicatesBuf.append(c);
/*      */     }
/*      */ 
/* 2403 */     duplicatesCount = Integer.parseInt(duplicatesBuf.toString());
/*      */ 
/* 2405 */     return recordsCount - duplicatesCount;
/*      */   }
/*      */ 
/*      */   public ResultSet getResultSet()
/*      */     throws SQLException
/*      */   {
/* 2418 */     synchronized (checkClosed()) {
/* 2419 */       return (this.results != null) && (this.results.reallyResult()) ? this.results : null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getResultSetConcurrency()
/*      */     throws SQLException
/*      */   {
/* 2433 */     synchronized (checkClosed()) {
/* 2434 */       return this.resultSetConcurrency;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 2442 */     return 1;
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods getResultSetInternal() {
/*      */     try {
/* 2447 */       synchronized (checkClosed()) {
/* 2448 */         return this.results;
/*      */       }
/*      */     } catch (SQLException e) {
/*      */     }
/* 2451 */     return this.results;
/*      */   }
/*      */ 
/*      */   public int getResultSetType()
/*      */     throws SQLException
/*      */   {
/* 2464 */     synchronized (checkClosed()) {
/* 2465 */       return this.resultSetType;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 2480 */     synchronized (checkClosed()) {
/* 2481 */       if (this.results == null) {
/* 2482 */         return -1;
/*      */       }
/*      */ 
/* 2485 */       if (this.results.reallyResult()) {
/* 2486 */         return -1;
/*      */       }
/*      */ 
/* 2489 */       int truncatedUpdateCount = 0;
/*      */ 
/* 2491 */       if (this.results.getUpdateCount() > 2147483647L)
/* 2492 */         truncatedUpdateCount = 2147483647;
/*      */       else {
/* 2494 */         truncatedUpdateCount = (int)this.results.getUpdateCount();
/*      */       }
/*      */ 
/* 2497 */       return truncatedUpdateCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 2523 */     synchronized (checkClosed())
/*      */     {
/* 2525 */       if (this.clearWarningsCalled) {
/* 2526 */         return null;
/*      */       }
/*      */ 
/* 2529 */       if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 2530 */         SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
/*      */ 
/* 2533 */         if (this.warningChain != null)
/* 2534 */           this.warningChain.setNextWarning(pendingWarningsFromServer);
/*      */         else {
/* 2536 */           this.warningChain = pendingWarningsFromServer;
/*      */         }
/*      */ 
/* 2539 */         return this.warningChain;
/*      */       }
/*      */ 
/* 2542 */       return this.warningChain;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/*      */     MySQLConnection locallyScopedConn;
/*      */     try
/*      */     {
/* 2560 */       locallyScopedConn = checkClosed();
/*      */     } catch (SQLException sqlEx) {
/* 2562 */       return;
/*      */     }
/*      */ 
/* 2565 */     synchronized (locallyScopedConn)
/*      */     {
/* 2567 */       if ((this.useUsageAdvisor) && 
/* 2568 */         (!calledExplicitly)) {
/* 2569 */         String message = Messages.getString("Statement.63") + Messages.getString("Statement.64");
/*      */ 
/* 2572 */         this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */       }
/*      */ 
/* 2582 */       if (closeOpenResults) {
/* 2583 */         closeOpenResults = !this.holdResultsOpenOverClose;
/*      */       }
/*      */ 
/* 2586 */       if (closeOpenResults) {
/* 2587 */         if (this.results != null) {
/*      */           try
/*      */           {
/* 2590 */             this.results.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */         }
/* 2596 */         if (this.generatedKeysResults != null) {
/*      */           try
/*      */           {
/* 2599 */             this.generatedKeysResults.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */         }
/* 2605 */         closeAllOpenResults();
/*      */       }
/*      */ 
/* 2608 */       if (this.connection != null) {
/* 2609 */         if (this.maxRowsChanged) {
/* 2610 */           this.connection.unsetMaxRows(this);
/*      */         }
/*      */ 
/* 2613 */         if (!this.connection.getDontTrackOpenResources()) {
/* 2614 */           this.connection.unregisterStatement(this);
/*      */         }
/*      */       }
/*      */ 
/* 2618 */       this.isClosed = true;
/*      */ 
/* 2620 */       this.results = null;
/* 2621 */       this.generatedKeysResults = null;
/* 2622 */       this.connection = null;
/* 2623 */       this.warningChain = null;
/* 2624 */       this.openResults = null;
/* 2625 */       this.batchedGeneratedKeys = null;
/* 2626 */       this.localInfileInputStream = null;
/* 2627 */       this.pingTarget = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCursorName(String name)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setEscapeProcessing(boolean enable)
/*      */     throws SQLException
/*      */   {
/* 2664 */     synchronized (checkClosed()) {
/* 2665 */       this.doEscapeProcessing = enable;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 2683 */     switch (direction) {
/*      */     case 1000:
/*      */     case 1001:
/*      */     case 1002:
/* 2687 */       break;
/*      */     default:
/* 2690 */       throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 2711 */     synchronized (checkClosed()) {
/* 2712 */       if (((rows < 0) && (rows != -2147483648)) || ((this.maxRows != 0) && (this.maxRows != -1) && (rows > getMaxRows())))
/*      */       {
/* 2715 */         throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2720 */       this.fetchSize = rows;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) {
/*      */     try {
/* 2726 */       synchronized (checkClosed()) {
/* 2727 */         this.holdResultsOpenOverClose = holdResultsOpenOverClose;
/*      */       }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setMaxFieldSize(int max)
/*      */     throws SQLException
/*      */   {
/* 2744 */     synchronized (checkClosed()) {
/* 2745 */       if (max < 0) {
/* 2746 */         throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2751 */       int maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
/*      */ 
/* 2754 */       if (max > maxBuf) {
/* 2755 */         throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[] { Long.valueOf(maxBuf) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2761 */       this.maxFieldSize = max;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int max)
/*      */     throws SQLException
/*      */   {
/* 2777 */     synchronized (checkClosed()) {
/* 2778 */       if ((max > 50000000) || (max < 0)) {
/* 2779 */         throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2786 */       if (max == 0) {
/* 2787 */         max = -1;
/*      */       }
/*      */ 
/* 2790 */       this.maxRows = max;
/* 2791 */       this.maxRowsChanged = true;
/*      */ 
/* 2793 */       if (this.maxRows == -1) {
/* 2794 */         this.connection.unsetMaxRows(this);
/* 2795 */         this.maxRowsChanged = false;
/*      */       }
/*      */       else
/*      */       {
/* 2802 */         this.connection.maxRowsChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setQueryTimeout(int seconds)
/*      */     throws SQLException
/*      */   {
/* 2817 */     synchronized (checkClosed()) {
/* 2818 */       if (seconds < 0) {
/* 2819 */         throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2824 */       this.timeoutInMillis = (seconds * 1000);
/*      */     }
/*      */   }
/*      */ 
/*      */   void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/*      */     try
/*      */     {
/* 2836 */       synchronized (checkClosed()) {
/* 2837 */         this.resultSetConcurrency = concurrencyFlag;
/*      */       }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   void setResultSetType(int typeFlag)
/*      */   {
/*      */     try
/*      */     {
/* 2853 */       synchronized (checkClosed()) {
/* 2854 */         this.resultSetType = typeFlag;
/*      */       }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void getBatchedGeneratedKeys(java.sql.Statement batchedStatement) throws SQLException {
/* 2863 */     synchronized (checkClosed()) {
/* 2864 */       if (this.retrieveGeneratedKeys) {
/* 2865 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 2868 */           rs = batchedStatement.getGeneratedKeys();
/*      */ 
/* 2870 */           while (rs.next())
/* 2871 */             this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */         }
/*      */         finally
/*      */         {
/* 2875 */           if (rs != null)
/* 2876 */             rs.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException
/*      */   {
/* 2884 */     synchronized (checkClosed()) {
/* 2885 */       if (this.retrieveGeneratedKeys) {
/* 2886 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 2889 */           if (maxKeys == 0)
/* 2890 */             rs = getGeneratedKeysInternal();
/*      */           else {
/* 2892 */             rs = getGeneratedKeysInternal(maxKeys);
/*      */           }
/* 2894 */           while (rs.next())
/* 2895 */             this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */         }
/*      */         finally
/*      */         {
/* 2899 */           if (rs != null)
/* 2900 */             rs.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean useServerFetch()
/*      */     throws SQLException
/*      */   {
/* 2911 */     synchronized (checkClosed()) {
/* 2912 */       return (this.connection.isCursorFetchEnabled()) && (this.fetchSize > 0) && (this.resultSetConcurrency == 1007) && (this.resultSetType == 1003);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isClosed() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2920 */       synchronized (checkClosed()) {
/* 2921 */         return this.isClosed;
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/* 2924 */       if ("08003".equals(sqlEx.getSQLState())) {
/* 2925 */         return true;
/*      */       }
/*      */     }
/* 2928 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   public boolean isPoolable()
/*      */     throws SQLException
/*      */   {
/* 2935 */     return this.isPoolable;
/*      */   }
/*      */ 
/*      */   public void setPoolable(boolean poolable) throws SQLException {
/* 2939 */     this.isPoolable = poolable;
/*      */   }
/*      */ 
/*      */   public boolean isWrapperFor(Class<?> iface)
/*      */     throws SQLException
/*      */   {
/* 2958 */     checkClosed();
/*      */ 
/* 2962 */     return iface.isInstance(this);
/*      */   }
/*      */ 
/*      */   public Object unwrap(Class<?> iface)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2983 */       return Util.cast(iface, this); } catch (ClassCastException cce) {
/*      */     }
/* 2985 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected int findStartOfStatement(String sql)
/*      */   {
/* 2991 */     int statementStartPos = 0;
/*      */ 
/* 2993 */     if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
/* 2994 */       statementStartPos = sql.indexOf("*/");
/*      */ 
/* 2996 */       if (statementStartPos == -1)
/* 2997 */         statementStartPos = 0;
/*      */       else
/* 2999 */         statementStartPos += 2;
/*      */     }
/* 3001 */     else if ((StringUtils.startsWithIgnoreCaseAndWs(sql, "--")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "#")))
/*      */     {
/* 3003 */       statementStartPos = sql.indexOf('\n');
/*      */ 
/* 3005 */       if (statementStartPos == -1) {
/* 3006 */         statementStartPos = sql.indexOf('\r');
/*      */ 
/* 3008 */         if (statementStartPos == -1) {
/* 3009 */           statementStartPos = 0;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3014 */     return statementStartPos;
/*      */   }
/*      */ 
/*      */   public InputStream getLocalInfileInputStream()
/*      */   {
/* 3022 */     return this.localInfileInputStream;
/*      */   }
/*      */ 
/*      */   public void setLocalInfileInputStream(InputStream stream) {
/* 3026 */     this.localInfileInputStream = stream;
/*      */   }
/*      */ 
/*      */   public void setPingTarget(PingTarget pingTarget) {
/* 3030 */     this.pingTarget = pingTarget;
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 3034 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   protected boolean containsOnDuplicateKeyInString(String sql) {
/* 3038 */     return getOnDuplicateKeyLocation(sql) != -1;
/*      */   }
/*      */ 
/*      */   protected int getOnDuplicateKeyLocation(String sql) {
/* 3042 */     return StringUtils.indexOfIgnoreCaseRespectMarker(0, sql, "ON DUPLICATE KEY UPDATE ", "\"'`", "\"'`", !this.connection.isNoBackslashEscapesSet());
/*      */   }
/*      */ 
/*      */   public void closeOnCompletion()
/*      */     throws SQLException
/*      */   {
/* 3049 */     synchronized (checkClosed()) {
/* 3050 */       this.closeOnCompletion = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isCloseOnCompletion() throws SQLException {
/* 3055 */     synchronized (checkClosed()) {
/* 3056 */       return this.closeOnCompletion;
/*      */     }
/*      */   }
/*      */ 
/*      */   class CancelTask extends TimerTask
/*      */   {
/*   78 */     long connectionId = 0L;
/*   79 */     String origHost = "";
/*   80 */     SQLException caughtWhileCancelling = null;
/*      */     StatementImpl toCancel;
/*   82 */     Properties origConnProps = null;
/*   83 */     String origConnURL = "";
/*      */ 
/*      */     CancelTask(StatementImpl cancellee) throws SQLException {
/*   86 */       this.connectionId = cancellee.connectionId;
/*   87 */       this.origHost = StatementImpl.this.connection.getHost();
/*   88 */       this.toCancel = cancellee;
/*   89 */       this.origConnProps = new Properties();
/*      */ 
/*   91 */       Properties props = StatementImpl.this.connection.getProperties();
/*      */ 
/*   93 */       Enumeration keys = props.propertyNames();
/*      */ 
/*   95 */       while (keys.hasMoreElements()) {
/*   96 */         String key = keys.nextElement().toString();
/*   97 */         this.origConnProps.setProperty(key, props.getProperty(key));
/*      */       }
/*      */ 
/*  100 */       this.origConnURL = StatementImpl.this.connection.getURL();
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*  105 */       Thread cancelThread = new Thread()
/*      */       {
/*      */         public void run() {
/*  108 */           if (StatementImpl.this.connection.getQueryTimeoutKillsConnection()) {
/*      */             try {
/*  110 */               StatementImpl.CancelTask.this.toCancel.wasCancelled = true;
/*  111 */               StatementImpl.CancelTask.this.toCancel.wasCancelledByTimeout = true;
/*  112 */               StatementImpl.this.connection.realClose(false, false, true, new MySQLStatementCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
/*      */             }
/*      */             catch (NullPointerException npe) {
/*      */             }
/*      */             catch (SQLException sqlEx) {
/*  117 */               StatementImpl.CancelTask.this.caughtWhileCancelling = sqlEx;
/*      */             }
/*      */           } else {
/*  120 */             Connection cancelConn = null;
/*  121 */             java.sql.Statement cancelStmt = null;
/*      */             try
/*      */             {
/*  124 */               synchronized (StatementImpl.this.cancelTimeoutMutex) {
/*  125 */                 if (StatementImpl.CancelTask.this.origConnURL.equals(StatementImpl.this.connection.getURL()))
/*      */                 {
/*  127 */                   cancelConn = StatementImpl.this.connection.duplicate();
/*  128 */                   cancelStmt = cancelConn.createStatement();
/*  129 */                   cancelStmt.execute("KILL QUERY " + StatementImpl.CancelTask.this.connectionId);
/*      */                 } else {
/*      */                   try {
/*  132 */                     cancelConn = (Connection)DriverManager.getConnection(StatementImpl.CancelTask.this.origConnURL, StatementImpl.CancelTask.this.origConnProps);
/*  133 */                     cancelStmt = cancelConn.createStatement();
/*  134 */                     cancelStmt.execute("KILL QUERY " + StatementImpl.CancelTask.this.connectionId);
/*      */                   }
/*      */                   catch (NullPointerException npe) {
/*      */                   }
/*      */                 }
/*  139 */                 StatementImpl.CancelTask.this.toCancel.wasCancelled = true;
/*  140 */                 StatementImpl.CancelTask.this.toCancel.wasCancelledByTimeout = true;
/*      */               }
/*      */             } catch (SQLException sqlEx) {
/*  143 */               StatementImpl.CancelTask.this.caughtWhileCancelling = sqlEx;
/*      */             }
/*      */             catch (NullPointerException npe)
/*      */             {
/*      */             }
/*      */             finally
/*      */             {
/*  152 */               if (cancelStmt != null) {
/*      */                 try {
/*  154 */                   cancelStmt.close();
/*      */                 } catch (SQLException sqlEx) {
/*  156 */                   throw new RuntimeException(sqlEx.toString());
/*      */                 }
/*      */               }
/*      */ 
/*  160 */               if (cancelConn != null) {
/*      */                 try {
/*  162 */                   cancelConn.close();
/*      */                 } catch (SQLException sqlEx) {
/*  164 */                   throw new RuntimeException(sqlEx.toString());
/*      */                 }
/*      */               }
/*      */ 
/*  168 */               StatementImpl.CancelTask.this.toCancel = null;
/*  169 */               StatementImpl.CancelTask.this.origConnProps = null;
/*  170 */               StatementImpl.CancelTask.this.origConnURL = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       };
/*  176 */       cancelThread.start();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.StatementImpl
 * JD-Core Version:    0.6.0
 */