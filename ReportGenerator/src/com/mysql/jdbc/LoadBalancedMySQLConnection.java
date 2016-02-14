/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.Calendar;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ public class LoadBalancedMySQLConnection
/*      */   implements MySQLConnection
/*      */ {
/*      */   protected LoadBalancingConnectionProxy proxy;
/*      */ 
/*      */   public LoadBalancingConnectionProxy getProxy()
/*      */   {
/*   47 */     return this.proxy;
/*      */   }
/*      */ 
/*      */   protected synchronized MySQLConnection getActiveMySQLConnection() {
/*   51 */     return this.proxy.currentConn;
/*      */   }
/*      */ 
/*      */   public LoadBalancedMySQLConnection(LoadBalancingConnectionProxy proxy) {
/*   55 */     this.proxy = proxy;
/*      */   }
/*      */ 
/*      */   public void abortInternal() throws SQLException {
/*   59 */     getActiveMySQLConnection().abortInternal();
/*      */   }
/*      */ 
/*      */   public void changeUser(String userName, String newPassword) throws SQLException
/*      */   {
/*   64 */     getActiveMySQLConnection().changeUser(userName, newPassword);
/*      */   }
/*      */ 
/*      */   public void checkClosed() throws SQLException {
/*   68 */     getActiveMySQLConnection().checkClosed();
/*      */   }
/*      */ 
/*      */   public void clearHasTriedMaster() {
/*   72 */     getActiveMySQLConnection().clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   public void clearWarnings() throws SQLException {
/*   76 */     getActiveMySQLConnection().clearWarnings();
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*   82 */     return getActiveMySQLConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*   88 */     return getActiveMySQLConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/*   94 */     return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyIndex);
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/*  100 */     return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyIndexes);
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/*  106 */     return getActiveMySQLConnection().clientPrepareStatement(sql, autoGenKeyColNames);
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  112 */     return getActiveMySQLConnection().clientPrepareStatement(sql);
/*      */   }
/*      */ 
/*      */   public synchronized void close() throws SQLException {
/*  116 */     getActiveMySQLConnection().close();
/*      */   }
/*      */ 
/*      */   public void commit() throws SQLException {
/*  120 */     getActiveMySQLConnection().commit();
/*      */   }
/*      */ 
/*      */   public void createNewIO(boolean isForReconnect) throws SQLException {
/*  124 */     getActiveMySQLConnection().createNewIO(isForReconnect);
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement() throws SQLException {
/*  128 */     return getActiveMySQLConnection().createStatement();
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  134 */     return getActiveMySQLConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  140 */     return getActiveMySQLConnection().createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public void dumpTestcaseQuery(String query)
/*      */   {
/*  145 */     getActiveMySQLConnection().dumpTestcaseQuery(query);
/*      */   }
/*      */ 
/*      */   public Connection duplicate() throws SQLException {
/*  149 */     return getActiveMySQLConnection().duplicate();
/*      */   }
/*      */ 
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/*  156 */     return getActiveMySQLConnection().execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, isBatch);
/*      */   }
/*      */ 
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws SQLException
/*      */   {
/*  165 */     return getActiveMySQLConnection().execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);
/*      */   }
/*      */ 
/*      */   public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition)
/*      */     throws SQLException
/*      */   {
/*  173 */     return getActiveMySQLConnection().extractSqlFromPacket(possibleSqlQuery, queryPacket, endOfQueryPacketPosition);
/*      */   }
/*      */ 
/*      */   public String exposeAsXml() throws SQLException
/*      */   {
/*  178 */     return getActiveMySQLConnection().exposeAsXml();
/*      */   }
/*      */ 
/*      */   public boolean getAllowLoadLocalInfile() {
/*  182 */     return getActiveMySQLConnection().getAllowLoadLocalInfile();
/*      */   }
/*      */ 
/*      */   public boolean getAllowMultiQueries() {
/*  186 */     return getActiveMySQLConnection().getAllowMultiQueries();
/*      */   }
/*      */ 
/*      */   public boolean getAllowNanAndInf() {
/*  190 */     return getActiveMySQLConnection().getAllowNanAndInf();
/*      */   }
/*      */ 
/*      */   public boolean getAllowUrlInLocalInfile() {
/*  194 */     return getActiveMySQLConnection().getAllowUrlInLocalInfile();
/*      */   }
/*      */ 
/*      */   public boolean getAlwaysSendSetIsolation() {
/*  198 */     return getActiveMySQLConnection().getAlwaysSendSetIsolation();
/*      */   }
/*      */ 
/*      */   public boolean getAutoClosePStmtStreams() {
/*  202 */     return getActiveMySQLConnection().getAutoClosePStmtStreams();
/*      */   }
/*      */ 
/*      */   public boolean getAutoDeserialize() {
/*  206 */     return getActiveMySQLConnection().getAutoDeserialize();
/*      */   }
/*      */ 
/*      */   public boolean getAutoGenerateTestcaseScript() {
/*  210 */     return getActiveMySQLConnection().getAutoGenerateTestcaseScript();
/*      */   }
/*      */ 
/*      */   public boolean getAutoReconnectForPools() {
/*  214 */     return getActiveMySQLConnection().getAutoReconnectForPools();
/*      */   }
/*      */ 
/*      */   public boolean getAutoSlowLog() {
/*  218 */     return getActiveMySQLConnection().getAutoSlowLog();
/*      */   }
/*      */ 
/*      */   public int getBlobSendChunkSize() {
/*  222 */     return getActiveMySQLConnection().getBlobSendChunkSize();
/*      */   }
/*      */ 
/*      */   public boolean getBlobsAreStrings() {
/*  226 */     return getActiveMySQLConnection().getBlobsAreStrings();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStatements() {
/*  230 */     return getActiveMySQLConnection().getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStmts() {
/*  234 */     return getActiveMySQLConnection().getCacheCallableStmts();
/*      */   }
/*      */ 
/*      */   public boolean getCachePrepStmts() {
/*  238 */     return getActiveMySQLConnection().getCachePrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getCachePreparedStatements() {
/*  242 */     return getActiveMySQLConnection().getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public boolean getCacheResultSetMetadata() {
/*  246 */     return getActiveMySQLConnection().getCacheResultSetMetadata();
/*      */   }
/*      */ 
/*      */   public boolean getCacheServerConfiguration() {
/*  250 */     return getActiveMySQLConnection().getCacheServerConfiguration();
/*      */   }
/*      */ 
/*      */   public int getCallableStatementCacheSize() {
/*  254 */     return getActiveMySQLConnection().getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public int getCallableStmtCacheSize() {
/*  258 */     return getActiveMySQLConnection().getCallableStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public boolean getCapitalizeTypeNames() {
/*  262 */     return getActiveMySQLConnection().getCapitalizeTypeNames();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetResults() {
/*  266 */     return getActiveMySQLConnection().getCharacterSetResults();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStorePassword() {
/*  270 */     return getActiveMySQLConnection().getClientCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreType()
/*      */   {
/*  275 */     return getActiveMySQLConnection().getClientCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreUrl() {
/*  279 */     return getActiveMySQLConnection().getClientCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public String getClientInfoProvider() {
/*  283 */     return getActiveMySQLConnection().getClientInfoProvider();
/*      */   }
/*      */ 
/*      */   public String getClobCharacterEncoding() {
/*  287 */     return getActiveMySQLConnection().getClobCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public boolean getClobberStreamingResults() {
/*  291 */     return getActiveMySQLConnection().getClobberStreamingResults();
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts() {
/*  295 */     return getActiveMySQLConnection().getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/*  300 */     return getActiveMySQLConnection().getConnectTimeout();
/*      */   }
/*      */ 
/*      */   public String getConnectionCollation() {
/*  304 */     return getActiveMySQLConnection().getConnectionCollation();
/*      */   }
/*      */ 
/*      */   public String getConnectionLifecycleInterceptors() {
/*  308 */     return getActiveMySQLConnection().getConnectionLifecycleInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getContinueBatchOnError() {
/*  312 */     return getActiveMySQLConnection().getContinueBatchOnError();
/*      */   }
/*      */ 
/*      */   public boolean getCreateDatabaseIfNotExist() {
/*  316 */     return getActiveMySQLConnection().getCreateDatabaseIfNotExist();
/*      */   }
/*      */ 
/*      */   public int getDefaultFetchSize() {
/*  320 */     return getActiveMySQLConnection().getDefaultFetchSize();
/*      */   }
/*      */ 
/*      */   public boolean getDontTrackOpenResources() {
/*  324 */     return getActiveMySQLConnection().getDontTrackOpenResources();
/*      */   }
/*      */ 
/*      */   public boolean getDumpMetadataOnColumnNotFound() {
/*  328 */     return getActiveMySQLConnection().getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */ 
/*      */   public boolean getDumpQueriesOnException() {
/*  332 */     return getActiveMySQLConnection().getDumpQueriesOnException();
/*      */   }
/*      */ 
/*      */   public boolean getDynamicCalendars() {
/*  336 */     return getActiveMySQLConnection().getDynamicCalendars();
/*      */   }
/*      */ 
/*      */   public boolean getElideSetAutoCommits() {
/*  340 */     return getActiveMySQLConnection().getElideSetAutoCommits();
/*      */   }
/*      */ 
/*      */   public boolean getEmptyStringsConvertToZero() {
/*  344 */     return getActiveMySQLConnection().getEmptyStringsConvertToZero();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateLocators() {
/*  348 */     return getActiveMySQLConnection().getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateUnsupportedPstmts() {
/*  352 */     return getActiveMySQLConnection().getEmulateUnsupportedPstmts();
/*      */   }
/*      */ 
/*      */   public boolean getEnablePacketDebug() {
/*  356 */     return getActiveMySQLConnection().getEnablePacketDebug();
/*      */   }
/*      */ 
/*      */   public boolean getEnableQueryTimeouts() {
/*  360 */     return getActiveMySQLConnection().getEnableQueryTimeouts();
/*      */   }
/*      */ 
/*      */   public String getEncoding() {
/*  364 */     return getActiveMySQLConnection().getEncoding();
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/*  368 */     return getActiveMySQLConnection().getExceptionInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getExplainSlowQueries() {
/*  372 */     return getActiveMySQLConnection().getExplainSlowQueries();
/*      */   }
/*      */ 
/*      */   public boolean getFailOverReadOnly() {
/*  376 */     return getActiveMySQLConnection().getFailOverReadOnly();
/*      */   }
/*      */ 
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/*  380 */     return getActiveMySQLConnection().getFunctionsNeverReturnBlobs();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerfMetrics() {
/*  384 */     return getActiveMySQLConnection().getGatherPerfMetrics();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerformanceMetrics() {
/*  388 */     return getActiveMySQLConnection().getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public boolean getGenerateSimpleParameterMetadata() {
/*  392 */     return getActiveMySQLConnection().getGenerateSimpleParameterMetadata();
/*      */   }
/*      */ 
/*      */   public boolean getIgnoreNonTxTables() {
/*  396 */     return getActiveMySQLConnection().getIgnoreNonTxTables();
/*      */   }
/*      */ 
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/*  400 */     return getActiveMySQLConnection().getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public int getInitialTimeout()
/*      */   {
/*  405 */     return getActiveMySQLConnection().getInitialTimeout();
/*      */   }
/*      */ 
/*      */   public boolean getInteractiveClient() {
/*  409 */     return getActiveMySQLConnection().getInteractiveClient();
/*      */   }
/*      */ 
/*      */   public boolean getIsInteractiveClient() {
/*  413 */     return getActiveMySQLConnection().getIsInteractiveClient();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncation() {
/*  417 */     return getActiveMySQLConnection().getJdbcCompliantTruncation();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncationForReads() {
/*  421 */     return getActiveMySQLConnection().getJdbcCompliantTruncationForReads();
/*      */   }
/*      */ 
/*      */   public String getLargeRowSizeThreshold() {
/*  425 */     return getActiveMySQLConnection().getLargeRowSizeThreshold();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/*  429 */     return getActiveMySQLConnection().getLoadBalanceBlacklistTimeout();
/*      */   }
/*      */ 
/*      */   public int getLoadBalancePingTimeout() {
/*  433 */     return getActiveMySQLConnection().getLoadBalancePingTimeout();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceStrategy() {
/*  437 */     return getActiveMySQLConnection().getLoadBalanceStrategy();
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceValidateConnectionOnSwapServer() {
/*  441 */     return getActiveMySQLConnection().getLoadBalanceValidateConnectionOnSwapServer();
/*      */   }
/*      */ 
/*      */   public String getLocalSocketAddress()
/*      */   {
/*  446 */     return getActiveMySQLConnection().getLocalSocketAddress();
/*      */   }
/*      */ 
/*      */   public int getLocatorFetchBufferSize() {
/*  450 */     return getActiveMySQLConnection().getLocatorFetchBufferSize();
/*      */   }
/*      */ 
/*      */   public boolean getLogSlowQueries() {
/*  454 */     return getActiveMySQLConnection().getLogSlowQueries();
/*      */   }
/*      */ 
/*      */   public boolean getLogXaCommands() {
/*  458 */     return getActiveMySQLConnection().getLogXaCommands();
/*      */   }
/*      */ 
/*      */   public String getLogger() {
/*  462 */     return getActiveMySQLConnection().getLogger();
/*      */   }
/*      */ 
/*      */   public String getLoggerClassName() {
/*  466 */     return getActiveMySQLConnection().getLoggerClassName();
/*      */   }
/*      */ 
/*      */   public boolean getMaintainTimeStats() {
/*  470 */     return getActiveMySQLConnection().getMaintainTimeStats();
/*      */   }
/*      */ 
/*      */   public int getMaxAllowedPacket() {
/*  474 */     return getActiveMySQLConnection().getMaxAllowedPacket();
/*      */   }
/*      */ 
/*      */   public int getMaxQuerySizeToLog() {
/*  478 */     return getActiveMySQLConnection().getMaxQuerySizeToLog();
/*      */   }
/*      */ 
/*      */   public int getMaxReconnects() {
/*  482 */     return getActiveMySQLConnection().getMaxReconnects();
/*      */   }
/*      */ 
/*      */   public int getMaxRows() {
/*  486 */     return getActiveMySQLConnection().getMaxRows();
/*      */   }
/*      */ 
/*      */   public int getMetadataCacheSize() {
/*  490 */     return getActiveMySQLConnection().getMetadataCacheSize();
/*      */   }
/*      */ 
/*      */   public int getNetTimeoutForStreamingResults() {
/*  494 */     return getActiveMySQLConnection().getNetTimeoutForStreamingResults();
/*      */   }
/*      */ 
/*      */   public boolean getNoAccessToProcedureBodies() {
/*  498 */     return getActiveMySQLConnection().getNoAccessToProcedureBodies();
/*      */   }
/*      */ 
/*      */   public boolean getNoDatetimeStringSync() {
/*  502 */     return getActiveMySQLConnection().getNoDatetimeStringSync();
/*      */   }
/*      */ 
/*      */   public boolean getNoTimezoneConversionForTimeType() {
/*  506 */     return getActiveMySQLConnection().getNoTimezoneConversionForTimeType();
/*      */   }
/*      */ 
/*      */   public boolean getNullCatalogMeansCurrent() {
/*  510 */     return getActiveMySQLConnection().getNullCatalogMeansCurrent();
/*      */   }
/*      */ 
/*      */   public boolean getNullNamePatternMatchesAll() {
/*  514 */     return getActiveMySQLConnection().getNullNamePatternMatchesAll();
/*      */   }
/*      */ 
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility() {
/*  518 */     return getActiveMySQLConnection().getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public int getPacketDebugBufferSize()
/*      */   {
/*  523 */     return getActiveMySQLConnection().getPacketDebugBufferSize();
/*      */   }
/*      */ 
/*      */   public boolean getPadCharsWithSpace() {
/*  527 */     return getActiveMySQLConnection().getPadCharsWithSpace();
/*      */   }
/*      */ 
/*      */   public boolean getParanoid() {
/*  531 */     return getActiveMySQLConnection().getParanoid();
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding() {
/*  535 */     return getActiveMySQLConnection().getPasswordCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public boolean getPedantic() {
/*  539 */     return getActiveMySQLConnection().getPedantic();
/*      */   }
/*      */ 
/*      */   public boolean getPinGlobalTxToPhysicalConnection() {
/*  543 */     return getActiveMySQLConnection().getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */ 
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/*  547 */     return getActiveMySQLConnection().getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSize()
/*      */   {
/*  552 */     return getActiveMySQLConnection().getPrepStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSqlLimit() {
/*  556 */     return getActiveMySQLConnection().getPrepStmtCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSize() {
/*  560 */     return getActiveMySQLConnection().getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSqlLimit() {
/*  564 */     return getActiveMySQLConnection().getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public boolean getProcessEscapeCodesForPrepStmts() {
/*  568 */     return getActiveMySQLConnection().getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSQL() {
/*  572 */     return getActiveMySQLConnection().getProfileSQL();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSql() {
/*  576 */     return getActiveMySQLConnection().getProfileSql();
/*      */   }
/*      */ 
/*      */   public String getProfilerEventHandler() {
/*  580 */     return getActiveMySQLConnection().getProfilerEventHandler();
/*      */   }
/*      */ 
/*      */   public String getPropertiesTransform() {
/*  584 */     return getActiveMySQLConnection().getPropertiesTransform();
/*      */   }
/*      */ 
/*      */   public int getQueriesBeforeRetryMaster() {
/*  588 */     return getActiveMySQLConnection().getQueriesBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/*  592 */     return getActiveMySQLConnection().getQueryTimeoutKillsConnection();
/*      */   }
/*      */ 
/*      */   public boolean getReconnectAtTxEnd() {
/*  596 */     return getActiveMySQLConnection().getReconnectAtTxEnd();
/*      */   }
/*      */ 
/*      */   public boolean getRelaxAutoCommit() {
/*  600 */     return getActiveMySQLConnection().getRelaxAutoCommit();
/*      */   }
/*      */ 
/*      */   public int getReportMetricsIntervalMillis() {
/*  604 */     return getActiveMySQLConnection().getReportMetricsIntervalMillis();
/*      */   }
/*      */ 
/*      */   public boolean getRequireSSL() {
/*  608 */     return getActiveMySQLConnection().getRequireSSL();
/*      */   }
/*      */ 
/*      */   public String getResourceId() {
/*  612 */     return getActiveMySQLConnection().getResourceId();
/*      */   }
/*      */ 
/*      */   public int getResultSetSizeThreshold() {
/*  616 */     return getActiveMySQLConnection().getResultSetSizeThreshold();
/*      */   }
/*      */ 
/*      */   public boolean getRetainStatementAfterResultSetClose() {
/*  620 */     return getActiveMySQLConnection().getRetainStatementAfterResultSetClose();
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown()
/*      */   {
/*  625 */     return getActiveMySQLConnection().getRetriesAllDown();
/*      */   }
/*      */ 
/*      */   public boolean getRewriteBatchedStatements() {
/*  629 */     return getActiveMySQLConnection().getRewriteBatchedStatements();
/*      */   }
/*      */ 
/*      */   public boolean getRollbackOnPooledClose() {
/*  633 */     return getActiveMySQLConnection().getRollbackOnPooledClose();
/*      */   }
/*      */ 
/*      */   public boolean getRoundRobinLoadBalance() {
/*  637 */     return getActiveMySQLConnection().getRoundRobinLoadBalance();
/*      */   }
/*      */ 
/*      */   public boolean getRunningCTS13() {
/*  641 */     return getActiveMySQLConnection().getRunningCTS13();
/*      */   }
/*      */ 
/*      */   public int getSecondsBeforeRetryMaster() {
/*  645 */     return getActiveMySQLConnection().getSecondsBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingMaxOperations() {
/*  649 */     return getActiveMySQLConnection().getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/*  653 */     return getActiveMySQLConnection().getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */ 
/*      */   public String getServerTimezone()
/*      */   {
/*  658 */     return getActiveMySQLConnection().getServerTimezone();
/*      */   }
/*      */ 
/*      */   public String getSessionVariables() {
/*  662 */     return getActiveMySQLConnection().getSessionVariables();
/*      */   }
/*      */ 
/*      */   public int getSlowQueryThresholdMillis() {
/*  666 */     return getActiveMySQLConnection().getSlowQueryThresholdMillis();
/*      */   }
/*      */ 
/*      */   public long getSlowQueryThresholdNanos() {
/*  670 */     return getActiveMySQLConnection().getSlowQueryThresholdNanos();
/*      */   }
/*      */ 
/*      */   public String getSocketFactory() {
/*  674 */     return getActiveMySQLConnection().getSocketFactory();
/*      */   }
/*      */ 
/*      */   public String getSocketFactoryClassName() {
/*  678 */     return getActiveMySQLConnection().getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public int getSocketTimeout() {
/*  682 */     return getActiveMySQLConnection().getSocketTimeout();
/*      */   }
/*      */ 
/*      */   public String getStatementInterceptors() {
/*  686 */     return getActiveMySQLConnection().getStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getStrictFloatingPoint() {
/*  690 */     return getActiveMySQLConnection().getStrictFloatingPoint();
/*      */   }
/*      */ 
/*      */   public boolean getStrictUpdates() {
/*  694 */     return getActiveMySQLConnection().getStrictUpdates();
/*      */   }
/*      */ 
/*      */   public boolean getTcpKeepAlive() {
/*  698 */     return getActiveMySQLConnection().getTcpKeepAlive();
/*      */   }
/*      */ 
/*      */   public boolean getTcpNoDelay() {
/*  702 */     return getActiveMySQLConnection().getTcpNoDelay();
/*      */   }
/*      */ 
/*      */   public int getTcpRcvBuf() {
/*  706 */     return getActiveMySQLConnection().getTcpRcvBuf();
/*      */   }
/*      */ 
/*      */   public int getTcpSndBuf() {
/*  710 */     return getActiveMySQLConnection().getTcpSndBuf();
/*      */   }
/*      */ 
/*      */   public int getTcpTrafficClass() {
/*  714 */     return getActiveMySQLConnection().getTcpTrafficClass();
/*      */   }
/*      */ 
/*      */   public boolean getTinyInt1isBit() {
/*  718 */     return getActiveMySQLConnection().getTinyInt1isBit();
/*      */   }
/*      */ 
/*      */   public boolean getTraceProtocol() {
/*  722 */     return getActiveMySQLConnection().getTraceProtocol();
/*      */   }
/*      */ 
/*      */   public boolean getTransformedBitIsBoolean() {
/*  726 */     return getActiveMySQLConnection().getTransformedBitIsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTreatUtilDateAsTimestamp() {
/*  730 */     return getActiveMySQLConnection().getTreatUtilDateAsTimestamp();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStorePassword() {
/*  734 */     return getActiveMySQLConnection().getTrustCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreType() {
/*  738 */     return getActiveMySQLConnection().getTrustCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreUrl() {
/*  742 */     return getActiveMySQLConnection().getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public boolean getUltraDevHack() {
/*  746 */     return getActiveMySQLConnection().getUltraDevHack();
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows() {
/*  750 */     return getActiveMySQLConnection().getUseAffectedRows();
/*      */   }
/*      */ 
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/*  754 */     return getActiveMySQLConnection().getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/*  758 */     return getActiveMySQLConnection().getUseColumnNamesInFindColumn();
/*      */   }
/*      */ 
/*      */   public boolean getUseCompression() {
/*  762 */     return getActiveMySQLConnection().getUseCompression();
/*      */   }
/*      */ 
/*      */   public String getUseConfigs() {
/*  766 */     return getActiveMySQLConnection().getUseConfigs();
/*      */   }
/*      */ 
/*      */   public boolean getUseCursorFetch() {
/*  770 */     return getActiveMySQLConnection().getUseCursorFetch();
/*      */   }
/*      */ 
/*      */   public boolean getUseDirectRowUnpack() {
/*  774 */     return getActiveMySQLConnection().getUseDirectRowUnpack();
/*      */   }
/*      */ 
/*      */   public boolean getUseDynamicCharsetInfo() {
/*  778 */     return getActiveMySQLConnection().getUseDynamicCharsetInfo();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastDateParsing() {
/*  782 */     return getActiveMySQLConnection().getUseFastDateParsing();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastIntParsing() {
/*  786 */     return getActiveMySQLConnection().getUseFastIntParsing();
/*      */   }
/*      */ 
/*      */   public boolean getUseGmtMillisForDatetimes() {
/*  790 */     return getActiveMySQLConnection().getUseGmtMillisForDatetimes();
/*      */   }
/*      */ 
/*      */   public boolean getUseHostsInPrivileges() {
/*  794 */     return getActiveMySQLConnection().getUseHostsInPrivileges();
/*      */   }
/*      */ 
/*      */   public boolean getUseInformationSchema() {
/*  798 */     return getActiveMySQLConnection().getUseInformationSchema();
/*      */   }
/*      */ 
/*      */   public boolean getUseJDBCCompliantTimezoneShift() {
/*  802 */     return getActiveMySQLConnection().getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */ 
/*      */   public boolean getUseJvmCharsetConverters() {
/*  806 */     return getActiveMySQLConnection().getUseJvmCharsetConverters();
/*      */   }
/*      */ 
/*      */   public boolean getUseLegacyDatetimeCode() {
/*  810 */     return getActiveMySQLConnection().getUseLegacyDatetimeCode();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalSessionState() {
/*  814 */     return getActiveMySQLConnection().getUseLocalSessionState();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/*  818 */     return getActiveMySQLConnection().getUseLocalTransactionState();
/*      */   }
/*      */ 
/*      */   public boolean getUseNanosForElapsedTime() {
/*  822 */     return getActiveMySQLConnection().getUseNanosForElapsedTime();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldAliasMetadataBehavior() {
/*  826 */     return getActiveMySQLConnection().getUseOldAliasMetadataBehavior();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldUTF8Behavior() {
/*  830 */     return getActiveMySQLConnection().getUseOldUTF8Behavior();
/*      */   }
/*      */ 
/*      */   public boolean getUseOnlyServerErrorMessages() {
/*  834 */     return getActiveMySQLConnection().getUseOnlyServerErrorMessages();
/*      */   }
/*      */ 
/*      */   public boolean getUseReadAheadInput() {
/*  838 */     return getActiveMySQLConnection().getUseReadAheadInput();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSL() {
/*  842 */     return getActiveMySQLConnection().getUseSSL();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSPSCompatibleTimezoneShift() {
/*  846 */     return getActiveMySQLConnection().getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPrepStmts() {
/*  850 */     return getActiveMySQLConnection().getUseServerPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPreparedStmts() {
/*  854 */     return getActiveMySQLConnection().getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseSqlStateCodes() {
/*  858 */     return getActiveMySQLConnection().getUseSqlStateCodes();
/*      */   }
/*      */ 
/*      */   public boolean getUseStreamLengthsInPrepStmts() {
/*  862 */     return getActiveMySQLConnection().getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseTimezone() {
/*  866 */     return getActiveMySQLConnection().getUseTimezone();
/*      */   }
/*      */ 
/*      */   public boolean getUseUltraDevWorkAround() {
/*  870 */     return getActiveMySQLConnection().getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnbufferedInput() {
/*  874 */     return getActiveMySQLConnection().getUseUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnicode() {
/*  878 */     return getActiveMySQLConnection().getUseUnicode();
/*      */   }
/*      */ 
/*      */   public boolean getUseUsageAdvisor() {
/*  882 */     return getActiveMySQLConnection().getUseUsageAdvisor();
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/*  886 */     return getActiveMySQLConnection().getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern()
/*      */   {
/*  891 */     return getActiveMySQLConnection().getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public boolean getVerifyServerCertificate()
/*      */   {
/*  896 */     return getActiveMySQLConnection().getVerifyServerCertificate();
/*      */   }
/*      */ 
/*      */   public boolean getYearIsDateType() {
/*  900 */     return getActiveMySQLConnection().getYearIsDateType();
/*      */   }
/*      */ 
/*      */   public String getZeroDateTimeBehavior() {
/*  904 */     return getActiveMySQLConnection().getZeroDateTimeBehavior();
/*      */   }
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property) {
/*  908 */     getActiveMySQLConnection().setAllowLoadLocalInfile(property);
/*      */   }
/*      */ 
/*      */   public void setAllowMultiQueries(boolean property) {
/*  912 */     getActiveMySQLConnection().setAllowMultiQueries(property);
/*      */   }
/*      */ 
/*      */   public void setAllowNanAndInf(boolean flag) {
/*  916 */     getActiveMySQLConnection().setAllowNanAndInf(flag);
/*      */   }
/*      */ 
/*      */   public void setAllowUrlInLocalInfile(boolean flag) {
/*  920 */     getActiveMySQLConnection().setAllowUrlInLocalInfile(flag);
/*      */   }
/*      */ 
/*      */   public void setAlwaysSendSetIsolation(boolean flag) {
/*  924 */     getActiveMySQLConnection().setAlwaysSendSetIsolation(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoClosePStmtStreams(boolean flag) {
/*  928 */     getActiveMySQLConnection().setAutoClosePStmtStreams(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoDeserialize(boolean flag) {
/*  932 */     getActiveMySQLConnection().setAutoDeserialize(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoGenerateTestcaseScript(boolean flag) {
/*  936 */     getActiveMySQLConnection().setAutoGenerateTestcaseScript(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnect(boolean flag) {
/*  940 */     getActiveMySQLConnection().setAutoReconnect(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForConnectionPools(boolean property) {
/*  944 */     getActiveMySQLConnection().setAutoReconnectForConnectionPools(property);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForPools(boolean flag) {
/*  948 */     getActiveMySQLConnection().setAutoReconnectForPools(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoSlowLog(boolean flag) {
/*  952 */     getActiveMySQLConnection().setAutoSlowLog(flag);
/*      */   }
/*      */ 
/*      */   public void setBlobSendChunkSize(String value) throws SQLException {
/*  956 */     getActiveMySQLConnection().setBlobSendChunkSize(value);
/*      */   }
/*      */ 
/*      */   public void setBlobsAreStrings(boolean flag) {
/*  960 */     getActiveMySQLConnection().setBlobsAreStrings(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStatements(boolean flag) {
/*  964 */     getActiveMySQLConnection().setCacheCallableStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStmts(boolean flag) {
/*  968 */     getActiveMySQLConnection().setCacheCallableStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePrepStmts(boolean flag) {
/*  972 */     getActiveMySQLConnection().setCachePrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePreparedStatements(boolean flag) {
/*  976 */     getActiveMySQLConnection().setCachePreparedStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheResultSetMetadata(boolean property) {
/*  980 */     getActiveMySQLConnection().setCacheResultSetMetadata(property);
/*      */   }
/*      */ 
/*      */   public void setCacheServerConfiguration(boolean flag) {
/*  984 */     getActiveMySQLConnection().setCacheServerConfiguration(flag);
/*      */   }
/*      */ 
/*      */   public void setCallableStatementCacheSize(int size) {
/*  988 */     getActiveMySQLConnection().setCallableStatementCacheSize(size);
/*      */   }
/*      */ 
/*      */   public void setCallableStmtCacheSize(int cacheSize) {
/*  992 */     getActiveMySQLConnection().setCallableStmtCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeDBMDTypes(boolean property) {
/*  996 */     getActiveMySQLConnection().setCapitalizeDBMDTypes(property);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeTypeNames(boolean flag) {
/* 1000 */     getActiveMySQLConnection().setCapitalizeTypeNames(flag);
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String encoding) {
/* 1004 */     getActiveMySQLConnection().setCharacterEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public void setCharacterSetResults(String characterSet) {
/* 1008 */     getActiveMySQLConnection().setCharacterSetResults(characterSet);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStorePassword(String value) {
/* 1012 */     getActiveMySQLConnection().setClientCertificateKeyStorePassword(value);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreType(String value) {
/* 1016 */     getActiveMySQLConnection().setClientCertificateKeyStoreType(value);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreUrl(String value) {
/* 1020 */     getActiveMySQLConnection().setClientCertificateKeyStoreUrl(value);
/*      */   }
/*      */ 
/*      */   public void setClientInfoProvider(String classname) {
/* 1024 */     getActiveMySQLConnection().setClientInfoProvider(classname);
/*      */   }
/*      */ 
/*      */   public void setClobCharacterEncoding(String encoding) {
/* 1028 */     getActiveMySQLConnection().setClobCharacterEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public void setClobberStreamingResults(boolean flag) {
/* 1032 */     getActiveMySQLConnection().setClobberStreamingResults(flag);
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
/* 1036 */     getActiveMySQLConnection().setCompensateOnDuplicateKeyUpdateCounts(flag);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int timeoutMs)
/*      */   {
/* 1041 */     getActiveMySQLConnection().setConnectTimeout(timeoutMs);
/*      */   }
/*      */ 
/*      */   public void setConnectionCollation(String collation) {
/* 1045 */     getActiveMySQLConnection().setConnectionCollation(collation);
/*      */   }
/*      */ 
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 1049 */     getActiveMySQLConnection().setConnectionLifecycleInterceptors(interceptors);
/*      */   }
/*      */ 
/*      */   public void setContinueBatchOnError(boolean property)
/*      */   {
/* 1054 */     getActiveMySQLConnection().setContinueBatchOnError(property);
/*      */   }
/*      */ 
/*      */   public void setCreateDatabaseIfNotExist(boolean flag) {
/* 1058 */     getActiveMySQLConnection().setCreateDatabaseIfNotExist(flag);
/*      */   }
/*      */ 
/*      */   public void setDefaultFetchSize(int n) {
/* 1062 */     getActiveMySQLConnection().setDefaultFetchSize(n);
/*      */   }
/*      */ 
/*      */   public void setDetectServerPreparedStmts(boolean property) {
/* 1066 */     getActiveMySQLConnection().setDetectServerPreparedStmts(property);
/*      */   }
/*      */ 
/*      */   public void setDontTrackOpenResources(boolean flag) {
/* 1070 */     getActiveMySQLConnection().setDontTrackOpenResources(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag) {
/* 1074 */     getActiveMySQLConnection().setDumpMetadataOnColumnNotFound(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpQueriesOnException(boolean flag) {
/* 1078 */     getActiveMySQLConnection().setDumpQueriesOnException(flag);
/*      */   }
/*      */ 
/*      */   public void setDynamicCalendars(boolean flag) {
/* 1082 */     getActiveMySQLConnection().setDynamicCalendars(flag);
/*      */   }
/*      */ 
/*      */   public void setElideSetAutoCommits(boolean flag) {
/* 1086 */     getActiveMySQLConnection().setElideSetAutoCommits(flag);
/*      */   }
/*      */ 
/*      */   public void setEmptyStringsConvertToZero(boolean flag) {
/* 1090 */     getActiveMySQLConnection().setEmptyStringsConvertToZero(flag);
/*      */   }
/*      */ 
/*      */   public void setEmulateLocators(boolean property) {
/* 1094 */     getActiveMySQLConnection().setEmulateLocators(property);
/*      */   }
/*      */ 
/*      */   public void setEmulateUnsupportedPstmts(boolean flag) {
/* 1098 */     getActiveMySQLConnection().setEmulateUnsupportedPstmts(flag);
/*      */   }
/*      */ 
/*      */   public void setEnablePacketDebug(boolean flag) {
/* 1102 */     getActiveMySQLConnection().setEnablePacketDebug(flag);
/*      */   }
/*      */ 
/*      */   public void setEnableQueryTimeouts(boolean flag) {
/* 1106 */     getActiveMySQLConnection().setEnableQueryTimeouts(flag);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String property) {
/* 1110 */     getActiveMySQLConnection().setEncoding(property);
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 1114 */     getActiveMySQLConnection().setExceptionInterceptors(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public void setExplainSlowQueries(boolean flag)
/*      */   {
/* 1119 */     getActiveMySQLConnection().setExplainSlowQueries(flag);
/*      */   }
/*      */ 
/*      */   public void setFailOverReadOnly(boolean flag) {
/* 1123 */     getActiveMySQLConnection().setFailOverReadOnly(flag);
/*      */   }
/*      */ 
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 1127 */     getActiveMySQLConnection().setFunctionsNeverReturnBlobs(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerfMetrics(boolean flag) {
/* 1131 */     getActiveMySQLConnection().setGatherPerfMetrics(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerformanceMetrics(boolean flag) {
/* 1135 */     getActiveMySQLConnection().setGatherPerformanceMetrics(flag);
/*      */   }
/*      */ 
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag) {
/* 1139 */     getActiveMySQLConnection().setGenerateSimpleParameterMetadata(flag);
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag) {
/* 1143 */     getActiveMySQLConnection().setHoldResultsOpenOverStatementClose(flag);
/*      */   }
/*      */ 
/*      */   public void setIgnoreNonTxTables(boolean property) {
/* 1147 */     getActiveMySQLConnection().setIgnoreNonTxTables(property);
/*      */   }
/*      */ 
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 1151 */     getActiveMySQLConnection().setIncludeInnodbStatusInDeadlockExceptions(flag);
/*      */   }
/*      */ 
/*      */   public void setInitialTimeout(int property)
/*      */   {
/* 1156 */     getActiveMySQLConnection().setInitialTimeout(property);
/*      */   }
/*      */ 
/*      */   public void setInteractiveClient(boolean property) {
/* 1160 */     getActiveMySQLConnection().setInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setIsInteractiveClient(boolean property) {
/* 1164 */     getActiveMySQLConnection().setIsInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncation(boolean flag) {
/* 1168 */     getActiveMySQLConnection().setJdbcCompliantTruncation(flag);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 1173 */     getActiveMySQLConnection().setJdbcCompliantTruncationForReads(jdbcCompliantTruncationForReads);
/*      */   }
/*      */ 
/*      */   public void setLargeRowSizeThreshold(String value)
/*      */   {
/* 1178 */     getActiveMySQLConnection().setLargeRowSizeThreshold(value);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 1182 */     getActiveMySQLConnection().setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public void setLoadBalancePingTimeout(int loadBalancePingTimeout)
/*      */   {
/* 1187 */     getActiveMySQLConnection().setLoadBalancePingTimeout(loadBalancePingTimeout);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceStrategy(String strategy)
/*      */   {
/* 1192 */     getActiveMySQLConnection().setLoadBalanceStrategy(strategy);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer)
/*      */   {
/* 1198 */     getActiveMySQLConnection().setLoadBalanceValidateConnectionOnSwapServer(loadBalanceValidateConnectionOnSwapServer);
/*      */   }
/*      */ 
/*      */   public void setLocalSocketAddress(String address)
/*      */   {
/* 1205 */     getActiveMySQLConnection().setLocalSocketAddress(address);
/*      */   }
/*      */ 
/*      */   public void setLocatorFetchBufferSize(String value) throws SQLException
/*      */   {
/* 1210 */     getActiveMySQLConnection().setLocatorFetchBufferSize(value);
/*      */   }
/*      */ 
/*      */   public void setLogSlowQueries(boolean flag)
/*      */   {
/* 1215 */     getActiveMySQLConnection().setLogSlowQueries(flag);
/*      */   }
/*      */ 
/*      */   public void setLogXaCommands(boolean flag)
/*      */   {
/* 1220 */     getActiveMySQLConnection().setLogXaCommands(flag);
/*      */   }
/*      */ 
/*      */   public void setLogger(String property)
/*      */   {
/* 1225 */     getActiveMySQLConnection().setLogger(property);
/*      */   }
/*      */ 
/*      */   public void setLoggerClassName(String className)
/*      */   {
/* 1230 */     getActiveMySQLConnection().setLoggerClassName(className);
/*      */   }
/*      */ 
/*      */   public void setMaintainTimeStats(boolean flag)
/*      */   {
/* 1235 */     getActiveMySQLConnection().setMaintainTimeStats(flag);
/*      */   }
/*      */ 
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes)
/*      */   {
/* 1240 */     getActiveMySQLConnection().setMaxQuerySizeToLog(sizeInBytes);
/*      */   }
/*      */ 
/*      */   public void setMaxReconnects(int property)
/*      */   {
/* 1245 */     getActiveMySQLConnection().setMaxReconnects(property);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int property)
/*      */   {
/* 1250 */     getActiveMySQLConnection().setMaxRows(property);
/*      */   }
/*      */ 
/*      */   public void setMetadataCacheSize(int value)
/*      */   {
/* 1255 */     getActiveMySQLConnection().setMetadataCacheSize(value);
/*      */   }
/*      */ 
/*      */   public void setNetTimeoutForStreamingResults(int value)
/*      */   {
/* 1260 */     getActiveMySQLConnection().setNetTimeoutForStreamingResults(value);
/*      */   }
/*      */ 
/*      */   public void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/* 1265 */     getActiveMySQLConnection().setNoAccessToProcedureBodies(flag);
/*      */   }
/*      */ 
/*      */   public void setNoDatetimeStringSync(boolean flag)
/*      */   {
/* 1270 */     getActiveMySQLConnection().setNoDatetimeStringSync(flag);
/*      */   }
/*      */ 
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/* 1275 */     getActiveMySQLConnection().setNoTimezoneConversionForTimeType(flag);
/*      */   }
/*      */ 
/*      */   public void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/* 1280 */     getActiveMySQLConnection().setNullCatalogMeansCurrent(value);
/*      */   }
/*      */ 
/*      */   public void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/* 1285 */     getActiveMySQLConnection().setNullNamePatternMatchesAll(value);
/*      */   }
/*      */ 
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/* 1290 */     getActiveMySQLConnection().setOverrideSupportsIntegrityEnhancementFacility(flag);
/*      */   }
/*      */ 
/*      */   public void setPacketDebugBufferSize(int size)
/*      */   {
/* 1296 */     getActiveMySQLConnection().setPacketDebugBufferSize(size);
/*      */   }
/*      */ 
/*      */   public void setPadCharsWithSpace(boolean flag)
/*      */   {
/* 1301 */     getActiveMySQLConnection().setPadCharsWithSpace(flag);
/*      */   }
/*      */ 
/*      */   public void setParanoid(boolean property)
/*      */   {
/* 1306 */     getActiveMySQLConnection().setParanoid(property);
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet)
/*      */   {
/* 1311 */     getActiveMySQLConnection().setPasswordCharacterEncoding(characterSet);
/*      */   }
/*      */ 
/*      */   public void setPedantic(boolean property)
/*      */   {
/* 1316 */     getActiveMySQLConnection().setPedantic(property);
/*      */   }
/*      */ 
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag)
/*      */   {
/* 1321 */     getActiveMySQLConnection().setPinGlobalTxToPhysicalConnection(flag);
/*      */   }
/*      */ 
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag)
/*      */   {
/* 1326 */     getActiveMySQLConnection().setPopulateInsertRowWithDefaultValues(flag);
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSize(int cacheSize)
/*      */   {
/* 1331 */     getActiveMySQLConnection().setPrepStmtCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit)
/*      */   {
/* 1336 */     getActiveMySQLConnection().setPrepStmtCacheSqlLimit(sqlLimit);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSize(int cacheSize)
/*      */   {
/* 1341 */     getActiveMySQLConnection().setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */   {
/* 1346 */     getActiveMySQLConnection().setPreparedStatementCacheSqlLimit(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/* 1352 */     getActiveMySQLConnection().setProcessEscapeCodesForPrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setProfileSQL(boolean flag)
/*      */   {
/* 1357 */     getActiveMySQLConnection().setProfileSQL(flag);
/*      */   }
/*      */ 
/*      */   public void setProfileSql(boolean property)
/*      */   {
/* 1362 */     getActiveMySQLConnection().setProfileSql(property);
/*      */   }
/*      */ 
/*      */   public void setProfilerEventHandler(String handler)
/*      */   {
/* 1367 */     getActiveMySQLConnection().setProfilerEventHandler(handler);
/*      */   }
/*      */ 
/*      */   public void setPropertiesTransform(String value)
/*      */   {
/* 1372 */     getActiveMySQLConnection().setPropertiesTransform(value);
/*      */   }
/*      */ 
/*      */   public void setQueriesBeforeRetryMaster(int property)
/*      */   {
/* 1377 */     getActiveMySQLConnection().setQueriesBeforeRetryMaster(property);
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection)
/*      */   {
/* 1383 */     getActiveMySQLConnection().setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   public void setReconnectAtTxEnd(boolean property)
/*      */   {
/* 1389 */     getActiveMySQLConnection().setReconnectAtTxEnd(property);
/*      */   }
/*      */ 
/*      */   public void setRelaxAutoCommit(boolean property)
/*      */   {
/* 1394 */     getActiveMySQLConnection().setRelaxAutoCommit(property);
/*      */   }
/*      */ 
/*      */   public void setReportMetricsIntervalMillis(int millis)
/*      */   {
/* 1399 */     getActiveMySQLConnection().setReportMetricsIntervalMillis(millis);
/*      */   }
/*      */ 
/*      */   public void setRequireSSL(boolean property)
/*      */   {
/* 1404 */     getActiveMySQLConnection().setRequireSSL(property);
/*      */   }
/*      */ 
/*      */   public void setResourceId(String resourceId)
/*      */   {
/* 1409 */     getActiveMySQLConnection().setResourceId(resourceId);
/*      */   }
/*      */ 
/*      */   public void setResultSetSizeThreshold(int threshold)
/*      */   {
/* 1414 */     getActiveMySQLConnection().setResultSetSizeThreshold(threshold);
/*      */   }
/*      */ 
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/* 1419 */     getActiveMySQLConnection().setRetainStatementAfterResultSetClose(flag);
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown)
/*      */   {
/* 1424 */     getActiveMySQLConnection().setRetriesAllDown(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public void setRewriteBatchedStatements(boolean flag)
/*      */   {
/* 1429 */     getActiveMySQLConnection().setRewriteBatchedStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setRollbackOnPooledClose(boolean flag)
/*      */   {
/* 1434 */     getActiveMySQLConnection().setRollbackOnPooledClose(flag);
/*      */   }
/*      */ 
/*      */   public void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/* 1439 */     getActiveMySQLConnection().setRoundRobinLoadBalance(flag);
/*      */   }
/*      */ 
/*      */   public void setRunningCTS13(boolean flag)
/*      */   {
/* 1444 */     getActiveMySQLConnection().setRunningCTS13(flag);
/*      */   }
/*      */ 
/*      */   public void setSecondsBeforeRetryMaster(int property)
/*      */   {
/* 1449 */     getActiveMySQLConnection().setSecondsBeforeRetryMaster(property);
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations)
/*      */   {
/* 1454 */     getActiveMySQLConnection().setSelfDestructOnPingMaxOperations(maxOperations);
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds)
/*      */   {
/* 1460 */     getActiveMySQLConnection().setSelfDestructOnPingSecondsLifetime(seconds);
/*      */   }
/*      */ 
/*      */   public void setServerTimezone(String property)
/*      */   {
/* 1466 */     getActiveMySQLConnection().setServerTimezone(property);
/*      */   }
/*      */ 
/*      */   public void setSessionVariables(String variables)
/*      */   {
/* 1471 */     getActiveMySQLConnection().setSessionVariables(variables);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdMillis(int millis)
/*      */   {
/* 1476 */     getActiveMySQLConnection().setSlowQueryThresholdMillis(millis);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdNanos(long nanos)
/*      */   {
/* 1481 */     getActiveMySQLConnection().setSlowQueryThresholdNanos(nanos);
/*      */   }
/*      */ 
/*      */   public void setSocketFactory(String name)
/*      */   {
/* 1486 */     getActiveMySQLConnection().setSocketFactory(name);
/*      */   }
/*      */ 
/*      */   public void setSocketFactoryClassName(String property)
/*      */   {
/* 1491 */     getActiveMySQLConnection().setSocketFactoryClassName(property);
/*      */   }
/*      */ 
/*      */   public void setSocketTimeout(int property)
/*      */   {
/* 1496 */     getActiveMySQLConnection().setSocketTimeout(property);
/*      */   }
/*      */ 
/*      */   public void setStatementInterceptors(String value)
/*      */   {
/* 1501 */     getActiveMySQLConnection().setStatementInterceptors(value);
/*      */   }
/*      */ 
/*      */   public void setStrictFloatingPoint(boolean property)
/*      */   {
/* 1506 */     getActiveMySQLConnection().setStrictFloatingPoint(property);
/*      */   }
/*      */ 
/*      */   public void setStrictUpdates(boolean property)
/*      */   {
/* 1511 */     getActiveMySQLConnection().setStrictUpdates(property);
/*      */   }
/*      */ 
/*      */   public void setTcpKeepAlive(boolean flag)
/*      */   {
/* 1516 */     getActiveMySQLConnection().setTcpKeepAlive(flag);
/*      */   }
/*      */ 
/*      */   public void setTcpNoDelay(boolean flag)
/*      */   {
/* 1521 */     getActiveMySQLConnection().setTcpNoDelay(flag);
/*      */   }
/*      */ 
/*      */   public void setTcpRcvBuf(int bufSize)
/*      */   {
/* 1526 */     getActiveMySQLConnection().setTcpRcvBuf(bufSize);
/*      */   }
/*      */ 
/*      */   public void setTcpSndBuf(int bufSize)
/*      */   {
/* 1531 */     getActiveMySQLConnection().setTcpSndBuf(bufSize);
/*      */   }
/*      */ 
/*      */   public void setTcpTrafficClass(int classFlags)
/*      */   {
/* 1536 */     getActiveMySQLConnection().setTcpTrafficClass(classFlags);
/*      */   }
/*      */ 
/*      */   public void setTinyInt1isBit(boolean flag)
/*      */   {
/* 1541 */     getActiveMySQLConnection().setTinyInt1isBit(flag);
/*      */   }
/*      */ 
/*      */   public void setTraceProtocol(boolean flag)
/*      */   {
/* 1546 */     getActiveMySQLConnection().setTraceProtocol(flag);
/*      */   }
/*      */ 
/*      */   public void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/* 1551 */     getActiveMySQLConnection().setTransformedBitIsBoolean(flag);
/*      */   }
/*      */ 
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/* 1556 */     getActiveMySQLConnection().setTreatUtilDateAsTimestamp(flag);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/* 1561 */     getActiveMySQLConnection().setTrustCertificateKeyStorePassword(value);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreType(String value)
/*      */   {
/* 1566 */     getActiveMySQLConnection().setTrustCertificateKeyStoreType(value);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/* 1571 */     getActiveMySQLConnection().setTrustCertificateKeyStoreUrl(value);
/*      */   }
/*      */ 
/*      */   public void setUltraDevHack(boolean flag)
/*      */   {
/* 1576 */     getActiveMySQLConnection().setUltraDevHack(flag);
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag)
/*      */   {
/* 1581 */     getActiveMySQLConnection().setUseAffectedRows(flag);
/*      */   }
/*      */ 
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag)
/*      */   {
/* 1586 */     getActiveMySQLConnection().setUseBlobToStoreUTF8OutsideBMP(flag);
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag)
/*      */   {
/* 1591 */     getActiveMySQLConnection().setUseColumnNamesInFindColumn(flag);
/*      */   }
/*      */ 
/*      */   public void setUseCompression(boolean property)
/*      */   {
/* 1596 */     getActiveMySQLConnection().setUseCompression(property);
/*      */   }
/*      */ 
/*      */   public void setUseConfigs(String configs)
/*      */   {
/* 1601 */     getActiveMySQLConnection().setUseConfigs(configs);
/*      */   }
/*      */ 
/*      */   public void setUseCursorFetch(boolean flag)
/*      */   {
/* 1606 */     getActiveMySQLConnection().setUseCursorFetch(flag);
/*      */   }
/*      */ 
/*      */   public void setUseDirectRowUnpack(boolean flag)
/*      */   {
/* 1611 */     getActiveMySQLConnection().setUseDirectRowUnpack(flag);
/*      */   }
/*      */ 
/*      */   public void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/* 1616 */     getActiveMySQLConnection().setUseDynamicCharsetInfo(flag);
/*      */   }
/*      */ 
/*      */   public void setUseFastDateParsing(boolean flag)
/*      */   {
/* 1621 */     getActiveMySQLConnection().setUseFastDateParsing(flag);
/*      */   }
/*      */ 
/*      */   public void setUseFastIntParsing(boolean flag)
/*      */   {
/* 1626 */     getActiveMySQLConnection().setUseFastIntParsing(flag);
/*      */   }
/*      */ 
/*      */   public void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/* 1631 */     getActiveMySQLConnection().setUseGmtMillisForDatetimes(flag);
/*      */   }
/*      */ 
/*      */   public void setUseHostsInPrivileges(boolean property)
/*      */   {
/* 1636 */     getActiveMySQLConnection().setUseHostsInPrivileges(property);
/*      */   }
/*      */ 
/*      */   public void setUseInformationSchema(boolean flag)
/*      */   {
/* 1641 */     getActiveMySQLConnection().setUseInformationSchema(flag);
/*      */   }
/*      */ 
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/* 1646 */     getActiveMySQLConnection().setUseJDBCCompliantTimezoneShift(flag);
/*      */   }
/*      */ 
/*      */   public void setUseJvmCharsetConverters(boolean flag)
/*      */   {
/* 1651 */     getActiveMySQLConnection().setUseJvmCharsetConverters(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLegacyDatetimeCode(boolean flag)
/*      */   {
/* 1656 */     getActiveMySQLConnection().setUseLegacyDatetimeCode(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLocalSessionState(boolean flag)
/*      */   {
/* 1661 */     getActiveMySQLConnection().setUseLocalSessionState(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag)
/*      */   {
/* 1666 */     getActiveMySQLConnection().setUseLocalTransactionState(flag);
/*      */   }
/*      */ 
/*      */   public void setUseNanosForElapsedTime(boolean flag)
/*      */   {
/* 1671 */     getActiveMySQLConnection().setUseNanosForElapsedTime(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/* 1676 */     getActiveMySQLConnection().setUseOldAliasMetadataBehavior(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/* 1681 */     getActiveMySQLConnection().setUseOldUTF8Behavior(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/* 1686 */     getActiveMySQLConnection().setUseOnlyServerErrorMessages(flag);
/*      */   }
/*      */ 
/*      */   public void setUseReadAheadInput(boolean flag)
/*      */   {
/* 1691 */     getActiveMySQLConnection().setUseReadAheadInput(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSSL(boolean property)
/*      */   {
/* 1696 */     getActiveMySQLConnection().setUseSSL(property);
/*      */   }
/*      */ 
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/* 1701 */     getActiveMySQLConnection().setUseSSPSCompatibleTimezoneShift(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPrepStmts(boolean flag)
/*      */   {
/* 1706 */     getActiveMySQLConnection().setUseServerPrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPreparedStmts(boolean flag)
/*      */   {
/* 1711 */     getActiveMySQLConnection().setUseServerPreparedStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSqlStateCodes(boolean flag)
/*      */   {
/* 1716 */     getActiveMySQLConnection().setUseSqlStateCodes(flag);
/*      */   }
/*      */ 
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/* 1721 */     getActiveMySQLConnection().setUseStreamLengthsInPrepStmts(property);
/*      */   }
/*      */ 
/*      */   public void setUseTimezone(boolean property)
/*      */   {
/* 1726 */     getActiveMySQLConnection().setUseTimezone(property);
/*      */   }
/*      */ 
/*      */   public void setUseUltraDevWorkAround(boolean property)
/*      */   {
/* 1731 */     getActiveMySQLConnection().setUseUltraDevWorkAround(property);
/*      */   }
/*      */ 
/*      */   public void setUseUnbufferedInput(boolean flag)
/*      */   {
/* 1736 */     getActiveMySQLConnection().setUseUnbufferedInput(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUnicode(boolean flag)
/*      */   {
/* 1741 */     getActiveMySQLConnection().setUseUnicode(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/* 1746 */     getActiveMySQLConnection().setUseUsageAdvisor(useUsageAdvisorFlag);
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern)
/*      */   {
/* 1751 */     getActiveMySQLConnection().setUtf8OutsideBmpExcludedColumnNamePattern(regexPattern);
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern)
/*      */   {
/* 1757 */     getActiveMySQLConnection().setUtf8OutsideBmpIncludedColumnNamePattern(regexPattern);
/*      */   }
/*      */ 
/*      */   public void setVerifyServerCertificate(boolean flag)
/*      */   {
/* 1763 */     getActiveMySQLConnection().setVerifyServerCertificate(flag);
/*      */   }
/*      */ 
/*      */   public void setYearIsDateType(boolean flag)
/*      */   {
/* 1768 */     getActiveMySQLConnection().setYearIsDateType(flag);
/*      */   }
/*      */ 
/*      */   public void setZeroDateTimeBehavior(String behavior)
/*      */   {
/* 1773 */     getActiveMySQLConnection().setZeroDateTimeBehavior(behavior);
/*      */   }
/*      */ 
/*      */   public boolean useUnbufferedInput()
/*      */   {
/* 1778 */     return getActiveMySQLConnection().useUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public StringBuffer generateConnectionCommentBlock(StringBuffer buf)
/*      */   {
/* 1783 */     return getActiveMySQLConnection().generateConnectionCommentBlock(buf);
/*      */   }
/*      */ 
/*      */   public int getActiveStatementCount()
/*      */   {
/* 1788 */     return getActiveMySQLConnection().getActiveStatementCount();
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit() throws SQLException
/*      */   {
/* 1793 */     return getActiveMySQLConnection().getAutoCommit();
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement()
/*      */   {
/* 1798 */     return getActiveMySQLConnection().getAutoIncrementIncrement();
/*      */   }
/*      */ 
/*      */   public CachedResultSetMetaData getCachedMetaData(String sql)
/*      */   {
/* 1803 */     return getActiveMySQLConnection().getCachedMetaData(sql);
/*      */   }
/*      */ 
/*      */   public Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 1808 */     return getActiveMySQLConnection().getCalendarInstanceForSessionOrNew();
/*      */   }
/*      */ 
/*      */   public synchronized Timer getCancelTimer()
/*      */   {
/* 1813 */     return getActiveMySQLConnection().getCancelTimer();
/*      */   }
/*      */ 
/*      */   public String getCatalog() throws SQLException
/*      */   {
/* 1818 */     return getActiveMySQLConnection().getCatalog();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetMetadata()
/*      */   {
/* 1823 */     return getActiveMySQLConnection().getCharacterSetMetadata();
/*      */   }
/*      */ 
/*      */   public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName)
/*      */     throws SQLException
/*      */   {
/* 1829 */     return getActiveMySQLConnection().getCharsetConverter(javaEncodingName);
/*      */   }
/*      */ 
/*      */   public String getCharsetNameForIndex(int charsetIndex) throws SQLException
/*      */   {
/* 1834 */     return getActiveMySQLConnection().getCharsetNameForIndex(charsetIndex);
/*      */   }
/*      */ 
/*      */   public TimeZone getDefaultTimeZone()
/*      */   {
/* 1839 */     return getActiveMySQLConnection().getDefaultTimeZone();
/*      */   }
/*      */ 
/*      */   public String getErrorMessageEncoding()
/*      */   {
/* 1844 */     return getActiveMySQLConnection().getErrorMessageEncoding();
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/* 1849 */     return getActiveMySQLConnection().getExceptionInterceptor();
/*      */   }
/*      */ 
/*      */   public int getHoldability() throws SQLException
/*      */   {
/* 1854 */     return getActiveMySQLConnection().getHoldability();
/*      */   }
/*      */ 
/*      */   public String getHost()
/*      */   {
/* 1859 */     return getActiveMySQLConnection().getHost();
/*      */   }
/*      */ 
/*      */   public long getId()
/*      */   {
/* 1864 */     return getActiveMySQLConnection().getId();
/*      */   }
/*      */ 
/*      */   public long getIdleFor()
/*      */   {
/* 1869 */     return getActiveMySQLConnection().getIdleFor();
/*      */   }
/*      */ 
/*      */   public MysqlIO getIO() throws SQLException
/*      */   {
/* 1874 */     return getActiveMySQLConnection().getIO();
/*      */   }
/*      */ 
/*      */   public MySQLConnection getLoadBalanceSafeProxy()
/*      */   {
/* 1879 */     return getActiveMySQLConnection().getLoadBalanceSafeProxy();
/*      */   }
/*      */ 
/*      */   public Log getLog() throws SQLException
/*      */   {
/* 1884 */     return getActiveMySQLConnection().getLog();
/*      */   }
/*      */ 
/*      */   public int getMaxBytesPerChar(String javaCharsetName) throws SQLException
/*      */   {
/* 1889 */     return getActiveMySQLConnection().getMaxBytesPerChar(javaCharsetName);
/*      */   }
/*      */ 
/*      */   public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException
/*      */   {
/* 1894 */     return getActiveMySQLConnection().getMaxBytesPerChar(charsetIndex, javaCharsetName);
/*      */   }
/*      */ 
/*      */   public DatabaseMetaData getMetaData() throws SQLException
/*      */   {
/* 1899 */     return getActiveMySQLConnection().getMetaData();
/*      */   }
/*      */ 
/*      */   public java.sql.Statement getMetadataSafeStatement() throws SQLException
/*      */   {
/* 1904 */     return getActiveMySQLConnection().getMetadataSafeStatement();
/*      */   }
/*      */ 
/*      */   public int getNetBufferLength()
/*      */   {
/* 1909 */     return getActiveMySQLConnection().getNetBufferLength();
/*      */   }
/*      */ 
/*      */   public Properties getProperties()
/*      */   {
/* 1914 */     return getActiveMySQLConnection().getProperties();
/*      */   }
/*      */ 
/*      */   public boolean getRequiresEscapingEncoder()
/*      */   {
/* 1919 */     return getActiveMySQLConnection().getRequiresEscapingEncoder();
/*      */   }
/*      */ 
/*      */   public String getServerCharacterEncoding()
/*      */   {
/* 1924 */     return getActiveMySQLConnection().getServerCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public int getServerMajorVersion()
/*      */   {
/* 1929 */     return getActiveMySQLConnection().getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   public int getServerMinorVersion()
/*      */   {
/* 1934 */     return getActiveMySQLConnection().getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   public int getServerSubMinorVersion()
/*      */   {
/* 1939 */     return getActiveMySQLConnection().getServerSubMinorVersion();
/*      */   }
/*      */ 
/*      */   public TimeZone getServerTimezoneTZ()
/*      */   {
/* 1944 */     return getActiveMySQLConnection().getServerTimezoneTZ();
/*      */   }
/*      */ 
/*      */   public String getServerVariable(String variableName)
/*      */   {
/* 1949 */     return getActiveMySQLConnection().getServerVariable(variableName);
/*      */   }
/*      */ 
/*      */   public String getServerVersion()
/*      */   {
/* 1954 */     return getActiveMySQLConnection().getServerVersion();
/*      */   }
/*      */ 
/*      */   public Calendar getSessionLockedCalendar()
/*      */   {
/* 1959 */     return getActiveMySQLConnection().getSessionLockedCalendar();
/*      */   }
/*      */ 
/*      */   public String getStatementComment()
/*      */   {
/* 1964 */     return getActiveMySQLConnection().getStatementComment();
/*      */   }
/*      */ 
/*      */   public List<StatementInterceptorV2> getStatementInterceptorsInstances()
/*      */   {
/* 1969 */     return getActiveMySQLConnection().getStatementInterceptorsInstances();
/*      */   }
/*      */ 
/*      */   public synchronized int getTransactionIsolation() throws SQLException
/*      */   {
/* 1974 */     return getActiveMySQLConnection().getTransactionIsolation();
/*      */   }
/*      */ 
/*      */   public synchronized Map<String, Class<?>> getTypeMap() throws SQLException
/*      */   {
/* 1979 */     return getActiveMySQLConnection().getTypeMap();
/*      */   }
/*      */ 
/*      */   public String getURL()
/*      */   {
/* 1984 */     return getActiveMySQLConnection().getURL();
/*      */   }
/*      */ 
/*      */   public String getUser()
/*      */   {
/* 1989 */     return getActiveMySQLConnection().getUser();
/*      */   }
/*      */ 
/*      */   public Calendar getUtcCalendar()
/*      */   {
/* 1994 */     return getActiveMySQLConnection().getUtcCalendar();
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings() throws SQLException
/*      */   {
/* 1999 */     return getActiveMySQLConnection().getWarnings();
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c)
/*      */   {
/* 2004 */     return getActiveMySQLConnection().hasSameProperties(c);
/*      */   }
/*      */ 
/*      */   public boolean hasTriedMaster()
/*      */   {
/* 2009 */     return getActiveMySQLConnection().hasTriedMaster();
/*      */   }
/*      */ 
/*      */   public void incrementNumberOfPreparedExecutes()
/*      */   {
/* 2014 */     getActiveMySQLConnection().incrementNumberOfPreparedExecutes();
/*      */   }
/*      */ 
/*      */   public void incrementNumberOfPrepares()
/*      */   {
/* 2019 */     getActiveMySQLConnection().incrementNumberOfPrepares();
/*      */   }
/*      */ 
/*      */   public void incrementNumberOfResultSetsCreated()
/*      */   {
/* 2024 */     getActiveMySQLConnection().incrementNumberOfResultSetsCreated();
/*      */   }
/*      */ 
/*      */   public void initializeExtension(Extension ex) throws SQLException
/*      */   {
/* 2029 */     getActiveMySQLConnection().initializeExtension(ex);
/*      */   }
/*      */ 
/*      */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet)
/*      */     throws SQLException
/*      */   {
/* 2036 */     getActiveMySQLConnection().initializeResultsMetadataFromCache(sql, cachedMetaData, resultSet);
/*      */   }
/*      */ 
/*      */   public void initializeSafeStatementInterceptors()
/*      */     throws SQLException
/*      */   {
/* 2042 */     getActiveMySQLConnection().initializeSafeStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos)
/*      */   {
/* 2047 */     return getActiveMySQLConnection().isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public boolean isClientTzUTC()
/*      */   {
/* 2052 */     return getActiveMySQLConnection().isClientTzUTC();
/*      */   }
/*      */ 
/*      */   public boolean isCursorFetchEnabled() throws SQLException
/*      */   {
/* 2057 */     return getActiveMySQLConnection().isCursorFetchEnabled();
/*      */   }
/*      */ 
/*      */   public boolean isInGlobalTx()
/*      */   {
/* 2062 */     return getActiveMySQLConnection().isInGlobalTx();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isMasterConnection()
/*      */   {
/* 2067 */     return getActiveMySQLConnection().isMasterConnection();
/*      */   }
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet()
/*      */   {
/* 2072 */     return getActiveMySQLConnection().isNoBackslashEscapesSet();
/*      */   }
/*      */ 
/*      */   public boolean isReadInfoMsgEnabled()
/*      */   {
/* 2077 */     return getActiveMySQLConnection().isReadInfoMsgEnabled();
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly() throws SQLException
/*      */   {
/* 2082 */     return getActiveMySQLConnection().isReadOnly();
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly(boolean useSessionStatus) throws SQLException
/*      */   {
/* 2087 */     return getActiveMySQLConnection().isReadOnly(useSessionStatus);
/*      */   }
/*      */ 
/*      */   public boolean isRunningOnJDK13()
/*      */   {
/* 2092 */     return getActiveMySQLConnection().isRunningOnJDK13();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isSameResource(Connection otherConnection)
/*      */   {
/* 2097 */     return getActiveMySQLConnection().isSameResource(otherConnection);
/*      */   }
/*      */ 
/*      */   public boolean isServerTzUTC()
/*      */   {
/* 2102 */     return getActiveMySQLConnection().isServerTzUTC();
/*      */   }
/*      */ 
/*      */   public boolean lowerCaseTableNames()
/*      */   {
/* 2107 */     return getActiveMySQLConnection().lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public void maxRowsChanged(Statement stmt)
/*      */   {
/* 2112 */     getActiveMySQLConnection().maxRowsChanged(stmt);
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String sql) throws SQLException
/*      */   {
/* 2117 */     return getActiveMySQLConnection().nativeSQL(sql);
/*      */   }
/*      */ 
/*      */   public boolean parserKnowsUnicode()
/*      */   {
/* 2122 */     return getActiveMySQLConnection().parserKnowsUnicode();
/*      */   }
/*      */ 
/*      */   public void ping() throws SQLException
/*      */   {
/* 2127 */     getActiveMySQLConnection().ping();
/*      */   }
/*      */ 
/*      */   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis)
/*      */     throws SQLException
/*      */   {
/* 2133 */     getActiveMySQLConnection().pingInternal(checkForClosedConnection, timeoutMillis);
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2141 */     return getActiveMySQLConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2148 */     return getActiveMySQLConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/* 2154 */     return getActiveMySQLConnection().prepareCall(sql);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2161 */     return getActiveMySQLConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2168 */     return getActiveMySQLConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 2175 */     return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyIndex);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 2182 */     return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyIndexes);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 2189 */     return getActiveMySQLConnection().prepareStatement(sql, autoGenKeyColNames);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 2195 */     return getActiveMySQLConnection().prepareStatement(sql);
/*      */   }
/*      */ 
/*      */   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason)
/*      */     throws SQLException
/*      */   {
/* 2201 */     getActiveMySQLConnection().realClose(calledExplicitly, issueRollback, skipLocalTeardown, reason);
/*      */   }
/*      */ 
/*      */   public void recachePreparedStatement(ServerPreparedStatement pstmt)
/*      */     throws SQLException
/*      */   {
/* 2208 */     getActiveMySQLConnection().recachePreparedStatement(pstmt);
/*      */   }
/*      */ 
/*      */   public void registerQueryExecutionTime(long queryTimeMs)
/*      */   {
/* 2213 */     getActiveMySQLConnection().registerQueryExecutionTime(queryTimeMs);
/*      */   }
/*      */ 
/*      */   public void registerStatement(Statement stmt)
/*      */   {
/* 2218 */     getActiveMySQLConnection().registerStatement(stmt);
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint arg0) throws SQLException
/*      */   {
/* 2223 */     getActiveMySQLConnection().releaseSavepoint(arg0);
/*      */   }
/*      */ 
/*      */   public void reportNumberOfTablesAccessed(int numTablesAccessed)
/*      */   {
/* 2228 */     getActiveMySQLConnection().reportNumberOfTablesAccessed(numTablesAccessed);
/*      */   }
/*      */ 
/*      */   public synchronized void reportQueryTime(long millisOrNanos)
/*      */   {
/* 2234 */     getActiveMySQLConnection().reportQueryTime(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public void resetServerState() throws SQLException
/*      */   {
/* 2239 */     getActiveMySQLConnection().resetServerState();
/*      */   }
/*      */ 
/*      */   public void rollback() throws SQLException
/*      */   {
/* 2244 */     getActiveMySQLConnection().rollback();
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint savepoint) throws SQLException
/*      */   {
/* 2249 */     getActiveMySQLConnection().rollback(savepoint);
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2256 */     return getActiveMySQLConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2263 */     return getActiveMySQLConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 2270 */     return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyIndex);
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 2277 */     return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyIndexes);
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 2284 */     return getActiveMySQLConnection().serverPrepareStatement(sql, autoGenKeyColNames);
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 2291 */     return getActiveMySQLConnection().serverPrepareStatement(sql);
/*      */   }
/*      */ 
/*      */   public boolean serverSupportsConvertFn() throws SQLException
/*      */   {
/* 2296 */     return getActiveMySQLConnection().serverSupportsConvertFn();
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean autoCommitFlag) throws SQLException
/*      */   {
/* 2301 */     getActiveMySQLConnection().setAutoCommit(autoCommitFlag);
/*      */   }
/*      */ 
/*      */   public void setCatalog(String catalog) throws SQLException
/*      */   {
/* 2306 */     getActiveMySQLConnection().setCatalog(catalog);
/*      */   }
/*      */ 
/*      */   public synchronized void setFailedOver(boolean flag)
/*      */   {
/* 2311 */     getActiveMySQLConnection().setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   public void setHoldability(int arg0) throws SQLException
/*      */   {
/* 2316 */     getActiveMySQLConnection().setHoldability(arg0);
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag)
/*      */   {
/* 2321 */     getActiveMySQLConnection().setInGlobalTx(flag);
/*      */   }
/*      */ 
/*      */   public void setPreferSlaveDuringFailover(boolean flag)
/*      */   {
/* 2326 */     getActiveMySQLConnection().setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */ 
/*      */   public void setProxy(MySQLConnection proxy)
/*      */   {
/* 2331 */     getActiveMySQLConnection().setProxy(proxy);
/*      */   }
/*      */ 
/*      */   public void setReadInfoMsgEnabled(boolean flag)
/*      */   {
/* 2336 */     getActiveMySQLConnection().setReadInfoMsgEnabled(flag);
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean readOnlyFlag) throws SQLException
/*      */   {
/* 2341 */     getActiveMySQLConnection().setReadOnly(readOnlyFlag);
/*      */   }
/*      */ 
/*      */   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
/* 2345 */     getActiveMySQLConnection().setReadOnlyInternal(readOnlyFlag);
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint() throws SQLException {
/* 2349 */     return getActiveMySQLConnection().setSavepoint();
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint(String name) throws SQLException {
/* 2353 */     return getActiveMySQLConnection().setSavepoint(name);
/*      */   }
/*      */ 
/*      */   public void setStatementComment(String comment) {
/* 2357 */     getActiveMySQLConnection().setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   public synchronized void setTransactionIsolation(int level) throws SQLException
/*      */   {
/* 2362 */     getActiveMySQLConnection().setTransactionIsolation(level);
/*      */   }
/*      */ 
/*      */   public void shutdownServer() throws SQLException
/*      */   {
/* 2367 */     getActiveMySQLConnection().shutdownServer();
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseTableName() {
/* 2371 */     return getActiveMySQLConnection().storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean supportsIsolationLevel() {
/* 2375 */     return getActiveMySQLConnection().supportsIsolationLevel();
/*      */   }
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers() {
/* 2379 */     return getActiveMySQLConnection().supportsQuotedIdentifiers();
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions() {
/* 2383 */     return getActiveMySQLConnection().supportsTransactions();
/*      */   }
/*      */ 
/*      */   public void throwConnectionClosedException() throws SQLException {
/* 2387 */     getActiveMySQLConnection().throwConnectionClosedException();
/*      */   }
/*      */ 
/*      */   public void transactionBegun() throws SQLException {
/* 2391 */     getActiveMySQLConnection().transactionBegun();
/*      */   }
/*      */ 
/*      */   public void transactionCompleted() throws SQLException {
/* 2395 */     getActiveMySQLConnection().transactionCompleted();
/*      */   }
/*      */ 
/*      */   public void unregisterStatement(Statement stmt) {
/* 2399 */     getActiveMySQLConnection().unregisterStatement(stmt);
/*      */   }
/*      */ 
/*      */   public void unSafeStatementInterceptors() throws SQLException {
/* 2403 */     getActiveMySQLConnection().unSafeStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public void unsetMaxRows(Statement stmt) throws SQLException {
/* 2407 */     getActiveMySQLConnection().unsetMaxRows(stmt);
/*      */   }
/*      */ 
/*      */   public boolean useAnsiQuotedIdentifiers() {
/* 2411 */     return getActiveMySQLConnection().useAnsiQuotedIdentifiers();
/*      */   }
/*      */ 
/*      */   public boolean useMaxRows() {
/* 2415 */     return getActiveMySQLConnection().useMaxRows();
/*      */   }
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/* 2420 */     return getActiveMySQLConnection().versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */ 
/*      */   public boolean isClosed() throws SQLException
/*      */   {
/* 2425 */     return getActiveMySQLConnection().isClosed();
/*      */   }
/*      */ 
/*      */   public boolean getHoldResultsOpenOverStatementClose() {
/* 2429 */     return getActiveMySQLConnection().getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceConnectionGroup()
/*      */   {
/* 2434 */     return getActiveMySQLConnection().getLoadBalanceConnectionGroup();
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceEnableJMX() {
/* 2438 */     return getActiveMySQLConnection().getLoadBalanceEnableJMX();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceExceptionChecker() {
/* 2442 */     return getActiveMySQLConnection().getLoadBalanceExceptionChecker();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLExceptionSubclassFailover()
/*      */   {
/* 2447 */     return getActiveMySQLConnection().getLoadBalanceSQLExceptionSubclassFailover();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLStateFailover()
/*      */   {
/* 2452 */     return getActiveMySQLConnection().getLoadBalanceSQLStateFailover();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup)
/*      */   {
/* 2457 */     getActiveMySQLConnection().setLoadBalanceConnectionGroup(loadBalanceConnectionGroup);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX)
/*      */   {
/* 2463 */     getActiveMySQLConnection().setLoadBalanceEnableJMX(loadBalanceEnableJMX);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker)
/*      */   {
/* 2470 */     getActiveMySQLConnection().setLoadBalanceExceptionChecker(loadBalanceExceptionChecker);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover)
/*      */   {
/* 2477 */     getActiveMySQLConnection().setLoadBalanceSQLExceptionSubclassFailover(loadBalanceSQLExceptionSubclassFailover);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover)
/*      */   {
/* 2484 */     getActiveMySQLConnection().setLoadBalanceSQLStateFailover(loadBalanceSQLStateFailover);
/*      */   }
/*      */ 
/*      */   public boolean shouldExecutionTriggerServerSwapAfter(String SQL)
/*      */   {
/* 2495 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isProxySet() {
/* 2499 */     return getActiveMySQLConnection().isProxySet();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceAutoCommitStatementRegex()
/*      */   {
/* 2504 */     return getActiveMySQLConnection().getLoadBalanceAutoCommitStatementRegex();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceAutoCommitStatementThreshold()
/*      */   {
/* 2509 */     return getActiveMySQLConnection().getLoadBalanceAutoCommitStatementThreshold();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex)
/*      */   {
/* 2515 */     getActiveMySQLConnection().setLoadBalanceAutoCommitStatementRegex(loadBalanceAutoCommitStatementRegex);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold)
/*      */   {
/* 2522 */     getActiveMySQLConnection().setLoadBalanceAutoCommitStatementThreshold(loadBalanceAutoCommitStatementThreshold);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadDumpInDeadlockExceptions()
/*      */   {
/* 2528 */     return getActiveMySQLConnection().getIncludeThreadDumpInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
/* 2532 */     getActiveMySQLConnection().setIncludeThreadDumpInDeadlockExceptions(flag);
/*      */   }
/*      */ 
/*      */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
/* 2536 */     getActiveMySQLConnection().setTypeMap(map);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadNamesAsStatementComment() {
/* 2540 */     return getActiveMySQLConnection().getIncludeThreadNamesAsStatementComment();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadNamesAsStatementComment(boolean flag) {
/* 2544 */     getActiveMySQLConnection().setIncludeThreadNamesAsStatementComment(flag);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isServerLocal() throws SQLException {
/* 2548 */     return getActiveMySQLConnection().isServerLocal();
/*      */   }
/*      */ 
/*      */   public void setAuthenticationPlugins(String authenticationPlugins) {
/* 2552 */     getActiveMySQLConnection().setAuthenticationPlugins(authenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getAuthenticationPlugins() {
/* 2556 */     return getActiveMySQLConnection().getAuthenticationPlugins();
/*      */   }
/*      */ 
/*      */   public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins)
/*      */   {
/* 2561 */     getActiveMySQLConnection().setDisabledAuthenticationPlugins(disabledAuthenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getDisabledAuthenticationPlugins() {
/* 2565 */     return getActiveMySQLConnection().getDisabledAuthenticationPlugins();
/*      */   }
/*      */ 
/*      */   public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin)
/*      */   {
/* 2570 */     getActiveMySQLConnection().setDefaultAuthenticationPlugin(defaultAuthenticationPlugin);
/*      */   }
/*      */ 
/*      */   public String getDefaultAuthenticationPlugin() {
/* 2574 */     return getActiveMySQLConnection().getDefaultAuthenticationPlugin();
/*      */   }
/*      */ 
/*      */   public void setParseInfoCacheFactory(String factoryClassname) {
/* 2578 */     getActiveMySQLConnection().setParseInfoCacheFactory(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getParseInfoCacheFactory() {
/* 2582 */     return getActiveMySQLConnection().getParseInfoCacheFactory();
/*      */   }
/*      */ 
/*      */   public void setSchema(String schema) throws SQLException {
/* 2586 */     getActiveMySQLConnection().setSchema(schema);
/*      */   }
/*      */ 
/*      */   public String getSchema() throws SQLException {
/* 2590 */     return getActiveMySQLConnection().getSchema();
/*      */   }
/*      */ 
/*      */   public void abort(Executor executor) throws SQLException {
/* 2594 */     getActiveMySQLConnection().abort(executor);
/*      */   }
/*      */ 
/*      */   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
/*      */   {
/* 2599 */     getActiveMySQLConnection().setNetworkTimeout(executor, milliseconds);
/*      */   }
/*      */ 
/*      */   public int getNetworkTimeout() throws SQLException {
/* 2603 */     return getActiveMySQLConnection().getNetworkTimeout();
/*      */   }
/*      */ 
/*      */   public void setServerConfigCacheFactory(String factoryClassname) {
/* 2607 */     getActiveMySQLConnection().setServerConfigCacheFactory(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getServerConfigCacheFactory() {
/* 2611 */     return getActiveMySQLConnection().getServerConfigCacheFactory();
/*      */   }
/*      */ 
/*      */   public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
/* 2615 */     getActiveMySQLConnection().setDisconnectOnExpiredPasswords(disconnectOnExpiredPasswords);
/*      */   }
/*      */ 
/*      */   public boolean getDisconnectOnExpiredPasswords() {
/* 2619 */     return getActiveMySQLConnection().getDisconnectOnExpiredPasswords();
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.LoadBalancedMySQLConnection
 * JD-Core Version:    0.6.0
 */