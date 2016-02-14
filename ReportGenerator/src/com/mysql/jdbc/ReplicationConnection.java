/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ public class ReplicationConnection
/*      */   implements Connection, PingTarget
/*      */ {
/*      */   protected Connection currentConnection;
/*      */   protected Connection masterConnection;
/*      */   protected Connection slavesConnection;
/*      */ 
/*      */   protected ReplicationConnection()
/*      */   {
/*      */   }
/*      */ 
/*      */   public ReplicationConnection(Properties masterProperties, Properties slaveProperties)
/*      */     throws SQLException
/*      */   {
/*   57 */     NonRegisteringDriver driver = new NonRegisteringDriver();
/*      */ 
/*   59 */     StringBuffer masterUrl = new StringBuffer("jdbc:mysql://");
/*   60 */     StringBuffer slaveUrl = new StringBuffer("jdbc:mysql:loadbalance://");
/*      */ 
/*   62 */     String masterHost = masterProperties.getProperty("HOST");
/*      */ 
/*   65 */     if (masterHost != null) {
/*   66 */       masterUrl.append(masterHost);
/*      */     }
/*      */ 
/*   69 */     int numHosts = Integer.parseInt(slaveProperties.getProperty("NUM_HOSTS"));
/*      */ 
/*   72 */     for (int i = 1; i <= numHosts; i++) {
/*   73 */       String slaveHost = slaveProperties.getProperty("HOST." + i);
/*      */ 
/*   76 */       if (slaveHost != null) {
/*   77 */         if (i > 1) {
/*   78 */           slaveUrl.append(',');
/*      */         }
/*   80 */         slaveUrl.append(slaveHost);
/*      */       }
/*      */     }
/*      */ 
/*   84 */     String masterDb = masterProperties.getProperty("DBNAME");
/*      */ 
/*   87 */     masterUrl.append("/");
/*      */ 
/*   89 */     if (masterDb != null) {
/*   90 */       masterUrl.append(masterDb);
/*      */     }
/*      */ 
/*   93 */     String slaveDb = slaveProperties.getProperty("DBNAME");
/*      */ 
/*   96 */     slaveUrl.append("/");
/*      */ 
/*   98 */     if (slaveDb != null) {
/*   99 */       slaveUrl.append(slaveDb);
/*      */     }
/*      */ 
/*  102 */     slaveProperties.setProperty("roundRobinLoadBalance", "true");
/*      */ 
/*  104 */     this.masterConnection = ((PingTarget)driver.connect(masterUrl.toString(), masterProperties));
/*      */ 
/*  106 */     this.slavesConnection = ((PingTarget)driver.connect(slaveUrl.toString(), slaveProperties));
/*      */ 
/*  108 */     this.slavesConnection.setReadOnly(true);
/*      */ 
/*  110 */     this.currentConnection = this.masterConnection;
/*      */   }
/*      */ 
/*      */   public synchronized void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  119 */     this.currentConnection.clearWarnings();
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  128 */     this.masterConnection.close();
/*  129 */     this.slavesConnection.close();
/*      */   }
/*      */ 
/*      */   public synchronized void commit()
/*      */     throws SQLException
/*      */   {
/*  138 */     this.currentConnection.commit();
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement()
/*      */     throws SQLException
/*      */   {
/*  147 */     java.sql.Statement stmt = this.currentConnection.createStatement();
/*  148 */     ((Statement)stmt).setPingTarget(this);
/*      */ 
/*  150 */     return stmt;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  160 */     java.sql.Statement stmt = this.currentConnection.createStatement(resultSetType, resultSetConcurrency);
/*      */ 
/*  163 */     ((Statement)stmt).setPingTarget(this);
/*      */ 
/*  165 */     return stmt;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  176 */     java.sql.Statement stmt = this.currentConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */ 
/*  179 */     ((Statement)stmt).setPingTarget(this);
/*      */ 
/*  181 */     return stmt;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/*  190 */     return this.currentConnection.getAutoCommit();
/*      */   }
/*      */ 
/*      */   public synchronized String getCatalog()
/*      */     throws SQLException
/*      */   {
/*  199 */     return this.currentConnection.getCatalog();
/*      */   }
/*      */ 
/*      */   public synchronized Connection getCurrentConnection() {
/*  203 */     return this.currentConnection;
/*      */   }
/*      */ 
/*      */   public synchronized int getHoldability()
/*      */     throws SQLException
/*      */   {
/*  212 */     return this.currentConnection.getHoldability();
/*      */   }
/*      */ 
/*      */   public synchronized Connection getMasterConnection() {
/*  216 */     return this.masterConnection;
/*      */   }
/*      */ 
/*      */   public synchronized DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  225 */     return this.currentConnection.getMetaData();
/*      */   }
/*      */ 
/*      */   public synchronized Connection getSlavesConnection() {
/*  229 */     return this.slavesConnection;
/*      */   }
/*      */ 
/*      */   public synchronized int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/*  238 */     return this.currentConnection.getTransactionIsolation();
/*      */   }
/*      */ 
/*      */   public synchronized Map<String, Class<?>> getTypeMap()
/*      */     throws SQLException
/*      */   {
/*  247 */     return this.currentConnection.getTypeMap();
/*      */   }
/*      */ 
/*      */   public synchronized SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*  256 */     return this.currentConnection.getWarnings();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isClosed()
/*      */     throws SQLException
/*      */   {
/*  265 */     return this.currentConnection.isClosed();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  274 */     return this.currentConnection == this.slavesConnection;
/*      */   }
/*      */ 
/*      */   public synchronized String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/*  283 */     return this.currentConnection.nativeSQL(sql);
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/*  292 */     return this.currentConnection.prepareCall(sql);
/*      */   }
/*      */ 
/*      */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  302 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  314 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  324 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql);
/*      */ 
/*  326 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  328 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  338 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, autoGeneratedKeys);
/*      */ 
/*  340 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  342 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  352 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */ 
/*  355 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  357 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  369 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */ 
/*  372 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  374 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, int[] columnIndexes)
/*      */     throws SQLException
/*      */   {
/*  384 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, columnIndexes);
/*      */ 
/*  386 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  388 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement prepareStatement(String sql, String[] columnNames)
/*      */     throws SQLException
/*      */   {
/*  399 */     PreparedStatement pstmt = this.currentConnection.prepareStatement(sql, columnNames);
/*      */ 
/*  401 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  403 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized void releaseSavepoint(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/*  413 */     this.currentConnection.releaseSavepoint(savepoint);
/*      */   }
/*      */ 
/*      */   public synchronized void rollback()
/*      */     throws SQLException
/*      */   {
/*  422 */     this.currentConnection.rollback();
/*      */   }
/*      */ 
/*      */   public synchronized void rollback(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/*  431 */     this.currentConnection.rollback(savepoint);
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoCommit(boolean autoCommit)
/*      */     throws SQLException
/*      */   {
/*  441 */     this.currentConnection.setAutoCommit(autoCommit);
/*      */   }
/*      */ 
/*      */   public synchronized void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/*  450 */     this.currentConnection.setCatalog(catalog);
/*      */   }
/*      */ 
/*      */   public synchronized void setHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/*  460 */     this.currentConnection.setHoldability(holdability);
/*      */   }
/*      */ 
/*      */   public synchronized void setReadOnly(boolean readOnly)
/*      */     throws SQLException
/*      */   {
/*  469 */     if (readOnly) {
/*  470 */       if (this.currentConnection != this.slavesConnection) {
/*  471 */         switchToSlavesConnection();
/*      */       }
/*      */     }
/*  474 */     else if (this.currentConnection != this.masterConnection)
/*  475 */       switchToMasterConnection();
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/*  486 */     return this.currentConnection.setSavepoint();
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint(String name)
/*      */     throws SQLException
/*      */   {
/*  495 */     return this.currentConnection.setSavepoint(name);
/*      */   }
/*      */ 
/*      */   public synchronized void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/*  505 */     this.currentConnection.setTransactionIsolation(level);
/*      */   }
/*      */ 
/*      */   private synchronized void switchToMasterConnection()
/*      */     throws SQLException
/*      */   {
/*  511 */     swapConnections(this.masterConnection, this.slavesConnection);
/*      */   }
/*      */ 
/*      */   private synchronized void switchToSlavesConnection() throws SQLException {
/*  515 */     swapConnections(this.slavesConnection, this.masterConnection);
/*  516 */     this.slavesConnection.setReadOnly(true);
/*      */   }
/*      */ 
/*      */   private synchronized void swapConnections(Connection switchToConnection, Connection switchFromConnection)
/*      */     throws SQLException
/*      */   {
/*  531 */     String switchFromCatalog = switchFromConnection.getCatalog();
/*  532 */     String switchToCatalog = switchToConnection.getCatalog();
/*      */ 
/*  534 */     if ((switchToCatalog != null) && (!switchToCatalog.equals(switchFromCatalog)))
/*  535 */       switchToConnection.setCatalog(switchFromCatalog);
/*  536 */     else if (switchFromCatalog != null) {
/*  537 */       switchToConnection.setCatalog(switchFromCatalog);
/*      */     }
/*      */ 
/*  540 */     boolean switchToAutoCommit = switchToConnection.getAutoCommit();
/*  541 */     boolean switchFromConnectionAutoCommit = switchFromConnection.getAutoCommit();
/*      */ 
/*  543 */     if (switchFromConnectionAutoCommit != switchToAutoCommit) {
/*  544 */       switchToConnection.setAutoCommit(switchFromConnectionAutoCommit);
/*      */     }
/*      */ 
/*  547 */     int switchToIsolation = switchToConnection.getTransactionIsolation();
/*      */ 
/*  550 */     int switchFromIsolation = switchFromConnection.getTransactionIsolation();
/*      */ 
/*  552 */     if (switchFromIsolation != switchToIsolation) {
/*  553 */       switchToConnection.setTransactionIsolation(switchFromIsolation);
/*      */     }
/*      */ 
/*  557 */     this.currentConnection = switchToConnection;
/*      */   }
/*      */ 
/*      */   public synchronized void doPing() throws SQLException {
/*  561 */     if (this.masterConnection != null) {
/*  562 */       this.masterConnection.ping();
/*      */     }
/*      */ 
/*  565 */     if (this.slavesConnection != null)
/*  566 */       this.slavesConnection.ping();
/*      */   }
/*      */ 
/*      */   public synchronized void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/*  572 */     this.masterConnection.changeUser(userName, newPassword);
/*  573 */     this.slavesConnection.changeUser(userName, newPassword);
/*      */   }
/*      */ 
/*      */   public synchronized void clearHasTriedMaster() {
/*  577 */     this.masterConnection.clearHasTriedMaster();
/*  578 */     this.slavesConnection.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  584 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql);
/*  585 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  587 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*  592 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyIndex);
/*  593 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  595 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*  600 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*  601 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  603 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*  608 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyIndexes);
/*  609 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  611 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  617 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*  618 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  620 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*  625 */     PreparedStatement pstmt = this.currentConnection.clientPrepareStatement(sql, autoGenKeyColNames);
/*  626 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  628 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized int getActiveStatementCount() {
/*  632 */     return this.currentConnection.getActiveStatementCount();
/*      */   }
/*      */ 
/*      */   public synchronized long getIdleFor() {
/*  636 */     return this.currentConnection.getIdleFor();
/*      */   }
/*      */ 
/*      */   public synchronized Log getLog() throws SQLException {
/*  640 */     return this.currentConnection.getLog();
/*      */   }
/*      */ 
/*      */   public synchronized String getServerCharacterEncoding() {
/*  644 */     return this.currentConnection.getServerCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public synchronized TimeZone getServerTimezoneTZ() {
/*  648 */     return this.currentConnection.getServerTimezoneTZ();
/*      */   }
/*      */ 
/*      */   public synchronized String getStatementComment() {
/*  652 */     return this.currentConnection.getStatementComment();
/*      */   }
/*      */ 
/*      */   public synchronized boolean hasTriedMaster() {
/*  656 */     return this.currentConnection.hasTriedMaster();
/*      */   }
/*      */ 
/*      */   public synchronized void initializeExtension(Extension ex) throws SQLException {
/*  660 */     this.currentConnection.initializeExtension(ex);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos) {
/*  664 */     return this.currentConnection.isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isInGlobalTx() {
/*  668 */     return this.currentConnection.isInGlobalTx();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isMasterConnection() {
/*  672 */     return this.currentConnection.isMasterConnection();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isNoBackslashEscapesSet() {
/*  676 */     return this.currentConnection.isNoBackslashEscapesSet();
/*      */   }
/*      */ 
/*      */   public synchronized boolean lowerCaseTableNames() {
/*  680 */     return this.currentConnection.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public synchronized boolean parserKnowsUnicode() {
/*  684 */     return this.currentConnection.parserKnowsUnicode();
/*      */   }
/*      */ 
/*      */   public synchronized void ping() throws SQLException {
/*  688 */     this.masterConnection.ping();
/*  689 */     this.slavesConnection.ping();
/*      */   }
/*      */ 
/*      */   public synchronized void reportQueryTime(long millisOrNanos) {
/*  693 */     this.currentConnection.reportQueryTime(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public synchronized void resetServerState() throws SQLException {
/*  697 */     this.currentConnection.resetServerState();
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql) throws SQLException
/*      */   {
/*  702 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql);
/*  703 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  705 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*  710 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyIndex);
/*  711 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  713 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*  718 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*  719 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  721 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/*  727 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*  728 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  730 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*  735 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyIndexes);
/*  736 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  738 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*  743 */     PreparedStatement pstmt = this.currentConnection.serverPrepareStatement(sql, autoGenKeyColNames);
/*  744 */     ((Statement)pstmt).setPingTarget(this);
/*      */ 
/*  746 */     return pstmt;
/*      */   }
/*      */ 
/*      */   public synchronized void setFailedOver(boolean flag) {
/*  750 */     this.currentConnection.setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   public synchronized void setPreferSlaveDuringFailover(boolean flag) {
/*  754 */     this.currentConnection.setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */ 
/*      */   public synchronized void setStatementComment(String comment) {
/*  758 */     this.masterConnection.setStatementComment(comment);
/*  759 */     this.slavesConnection.setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   public synchronized void shutdownServer() throws SQLException {
/*  763 */     this.currentConnection.shutdownServer();
/*      */   }
/*      */ 
/*      */   public synchronized boolean supportsIsolationLevel() {
/*  767 */     return this.currentConnection.supportsIsolationLevel();
/*      */   }
/*      */ 
/*      */   public synchronized boolean supportsQuotedIdentifiers() {
/*  771 */     return this.currentConnection.supportsQuotedIdentifiers();
/*      */   }
/*      */ 
/*      */   public synchronized boolean supportsTransactions() {
/*  775 */     return this.currentConnection.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public synchronized boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/*  780 */     return this.currentConnection.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */ 
/*      */   public synchronized String exposeAsXml() throws SQLException {
/*  784 */     return this.currentConnection.exposeAsXml();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowLoadLocalInfile() {
/*  788 */     return this.currentConnection.getAllowLoadLocalInfile();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowMultiQueries() {
/*  792 */     return this.currentConnection.getAllowMultiQueries();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowNanAndInf() {
/*  796 */     return this.currentConnection.getAllowNanAndInf();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAllowUrlInLocalInfile() {
/*  800 */     return this.currentConnection.getAllowUrlInLocalInfile();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAlwaysSendSetIsolation() {
/*  804 */     return this.currentConnection.getAlwaysSendSetIsolation();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoClosePStmtStreams() {
/*  808 */     return this.currentConnection.getAutoClosePStmtStreams();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoDeserialize() {
/*  812 */     return this.currentConnection.getAutoDeserialize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoGenerateTestcaseScript() {
/*  816 */     return this.currentConnection.getAutoGenerateTestcaseScript();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoReconnectForPools() {
/*  820 */     return this.currentConnection.getAutoReconnectForPools();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoSlowLog() {
/*  824 */     return this.currentConnection.getAutoSlowLog();
/*      */   }
/*      */ 
/*      */   public synchronized int getBlobSendChunkSize() {
/*  828 */     return this.currentConnection.getBlobSendChunkSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBlobsAreStrings() {
/*  832 */     return this.currentConnection.getBlobsAreStrings();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheCallableStatements() {
/*  836 */     return this.currentConnection.getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheCallableStmts() {
/*  840 */     return this.currentConnection.getCacheCallableStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCachePrepStmts() {
/*  844 */     return this.currentConnection.getCachePrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCachePreparedStatements() {
/*  848 */     return this.currentConnection.getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheResultSetMetadata() {
/*  852 */     return this.currentConnection.getCacheResultSetMetadata();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCacheServerConfiguration() {
/*  856 */     return this.currentConnection.getCacheServerConfiguration();
/*      */   }
/*      */ 
/*      */   public synchronized int getCallableStatementCacheSize() {
/*  860 */     return this.currentConnection.getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getCallableStmtCacheSize() {
/*  864 */     return this.currentConnection.getCallableStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCapitalizeTypeNames() {
/*  868 */     return this.currentConnection.getCapitalizeTypeNames();
/*      */   }
/*      */ 
/*      */   public synchronized String getCharacterSetResults() {
/*  872 */     return this.currentConnection.getCharacterSetResults();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientCertificateKeyStorePassword() {
/*  876 */     return this.currentConnection.getClientCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientCertificateKeyStoreType() {
/*  880 */     return this.currentConnection.getClientCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientCertificateKeyStoreUrl() {
/*  884 */     return this.currentConnection.getClientCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public synchronized String getClientInfoProvider() {
/*  888 */     return this.currentConnection.getClientInfoProvider();
/*      */   }
/*      */ 
/*      */   public synchronized String getClobCharacterEncoding() {
/*  892 */     return this.currentConnection.getClobCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getClobberStreamingResults() {
/*  896 */     return this.currentConnection.getClobberStreamingResults();
/*      */   }
/*      */ 
/*      */   public synchronized int getConnectTimeout() {
/*  900 */     return this.currentConnection.getConnectTimeout();
/*      */   }
/*      */ 
/*      */   public synchronized String getConnectionCollation() {
/*  904 */     return this.currentConnection.getConnectionCollation();
/*      */   }
/*      */ 
/*      */   public synchronized String getConnectionLifecycleInterceptors() {
/*  908 */     return this.currentConnection.getConnectionLifecycleInterceptors();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getContinueBatchOnError() {
/*  912 */     return this.currentConnection.getContinueBatchOnError();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getCreateDatabaseIfNotExist() {
/*  916 */     return this.currentConnection.getCreateDatabaseIfNotExist();
/*      */   }
/*      */ 
/*      */   public synchronized int getDefaultFetchSize() {
/*  920 */     return this.currentConnection.getDefaultFetchSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDontTrackOpenResources() {
/*  924 */     return this.currentConnection.getDontTrackOpenResources();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDumpMetadataOnColumnNotFound() {
/*  928 */     return this.currentConnection.getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDumpQueriesOnException() {
/*  932 */     return this.currentConnection.getDumpQueriesOnException();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getDynamicCalendars() {
/*  936 */     return this.currentConnection.getDynamicCalendars();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getElideSetAutoCommits() {
/*  940 */     return this.currentConnection.getElideSetAutoCommits();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEmptyStringsConvertToZero() {
/*  944 */     return this.currentConnection.getEmptyStringsConvertToZero();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEmulateLocators() {
/*  948 */     return this.currentConnection.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEmulateUnsupportedPstmts() {
/*  952 */     return this.currentConnection.getEmulateUnsupportedPstmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEnablePacketDebug() {
/*  956 */     return this.currentConnection.getEnablePacketDebug();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getEnableQueryTimeouts() {
/*  960 */     return this.currentConnection.getEnableQueryTimeouts();
/*      */   }
/*      */ 
/*      */   public synchronized String getEncoding() {
/*  964 */     return this.currentConnection.getEncoding();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getExplainSlowQueries() {
/*  968 */     return this.currentConnection.getExplainSlowQueries();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getFailOverReadOnly() {
/*  972 */     return this.currentConnection.getFailOverReadOnly();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getFunctionsNeverReturnBlobs() {
/*  976 */     return this.currentConnection.getFunctionsNeverReturnBlobs();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getGatherPerfMetrics() {
/*  980 */     return this.currentConnection.getGatherPerfMetrics();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getGatherPerformanceMetrics() {
/*  984 */     return this.currentConnection.getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getGenerateSimpleParameterMetadata() {
/*  988 */     return this.currentConnection.getGenerateSimpleParameterMetadata();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getHoldResultsOpenOverStatementClose() {
/*  992 */     return this.currentConnection.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getIgnoreNonTxTables() {
/*  996 */     return this.currentConnection.getIgnoreNonTxTables();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 1000 */     return this.currentConnection.getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public synchronized int getInitialTimeout() {
/* 1004 */     return this.currentConnection.getInitialTimeout();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getInteractiveClient() {
/* 1008 */     return this.currentConnection.getInteractiveClient();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getIsInteractiveClient() {
/* 1012 */     return this.currentConnection.getIsInteractiveClient();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getJdbcCompliantTruncation() {
/* 1016 */     return this.currentConnection.getJdbcCompliantTruncation();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getJdbcCompliantTruncationForReads() {
/* 1020 */     return this.currentConnection.getJdbcCompliantTruncationForReads();
/*      */   }
/*      */ 
/*      */   public synchronized String getLargeRowSizeThreshold() {
/* 1024 */     return this.currentConnection.getLargeRowSizeThreshold();
/*      */   }
/*      */ 
/*      */   public synchronized String getLoadBalanceStrategy() {
/* 1028 */     return this.currentConnection.getLoadBalanceStrategy();
/*      */   }
/*      */ 
/*      */   public synchronized String getLocalSocketAddress() {
/* 1032 */     return this.currentConnection.getLocalSocketAddress();
/*      */   }
/*      */ 
/*      */   public synchronized int getLocatorFetchBufferSize() {
/* 1036 */     return this.currentConnection.getLocatorFetchBufferSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getLogSlowQueries() {
/* 1040 */     return this.currentConnection.getLogSlowQueries();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getLogXaCommands() {
/* 1044 */     return this.currentConnection.getLogXaCommands();
/*      */   }
/*      */ 
/*      */   public synchronized String getLogger() {
/* 1048 */     return this.currentConnection.getLogger();
/*      */   }
/*      */ 
/*      */   public synchronized String getLoggerClassName() {
/* 1052 */     return this.currentConnection.getLoggerClassName();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getMaintainTimeStats() {
/* 1056 */     return this.currentConnection.getMaintainTimeStats();
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxQuerySizeToLog() {
/* 1060 */     return this.currentConnection.getMaxQuerySizeToLog();
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxReconnects() {
/* 1064 */     return this.currentConnection.getMaxReconnects();
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxRows() {
/* 1068 */     return this.currentConnection.getMaxRows();
/*      */   }
/*      */ 
/*      */   public synchronized int getMetadataCacheSize() {
/* 1072 */     return this.currentConnection.getMetadataCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getNetTimeoutForStreamingResults() {
/* 1076 */     return this.currentConnection.getNetTimeoutForStreamingResults();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNoAccessToProcedureBodies() {
/* 1080 */     return this.currentConnection.getNoAccessToProcedureBodies();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNoDatetimeStringSync() {
/* 1084 */     return this.currentConnection.getNoDatetimeStringSync();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNoTimezoneConversionForTimeType() {
/* 1088 */     return this.currentConnection.getNoTimezoneConversionForTimeType();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNullCatalogMeansCurrent() {
/* 1092 */     return this.currentConnection.getNullCatalogMeansCurrent();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getNullNamePatternMatchesAll() {
/* 1096 */     return this.currentConnection.getNullNamePatternMatchesAll();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 1100 */     return this.currentConnection.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public synchronized int getPacketDebugBufferSize() {
/* 1104 */     return this.currentConnection.getPacketDebugBufferSize();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPadCharsWithSpace() {
/* 1108 */     return this.currentConnection.getPadCharsWithSpace();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getParanoid() {
/* 1112 */     return this.currentConnection.getParanoid();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPedantic() {
/* 1116 */     return this.currentConnection.getPedantic();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPinGlobalTxToPhysicalConnection() {
/* 1120 */     return this.currentConnection.getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getPopulateInsertRowWithDefaultValues() {
/* 1124 */     return this.currentConnection.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public synchronized int getPrepStmtCacheSize() {
/* 1128 */     return this.currentConnection.getPrepStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getPrepStmtCacheSqlLimit() {
/* 1132 */     return this.currentConnection.getPrepStmtCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public synchronized int getPreparedStatementCacheSize() {
/* 1136 */     return this.currentConnection.getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public synchronized int getPreparedStatementCacheSqlLimit() {
/* 1140 */     return this.currentConnection.getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getProcessEscapeCodesForPrepStmts() {
/* 1144 */     return this.currentConnection.getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getProfileSQL() {
/* 1148 */     return this.currentConnection.getProfileSQL();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getProfileSql() {
/* 1152 */     return this.currentConnection.getProfileSql();
/*      */   }
/*      */ 
/*      */   public synchronized String getProfilerEventHandler() {
/* 1156 */     return this.currentConnection.getProfilerEventHandler();
/*      */   }
/*      */ 
/*      */   public synchronized String getPropertiesTransform() {
/* 1160 */     return this.currentConnection.getPropertiesTransform();
/*      */   }
/*      */ 
/*      */   public synchronized int getQueriesBeforeRetryMaster() {
/* 1164 */     return this.currentConnection.getQueriesBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getReconnectAtTxEnd() {
/* 1168 */     return this.currentConnection.getReconnectAtTxEnd();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRelaxAutoCommit() {
/* 1172 */     return this.currentConnection.getRelaxAutoCommit();
/*      */   }
/*      */ 
/*      */   public synchronized int getReportMetricsIntervalMillis() {
/* 1176 */     return this.currentConnection.getReportMetricsIntervalMillis();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRequireSSL() {
/* 1180 */     return this.currentConnection.getRequireSSL();
/*      */   }
/*      */ 
/*      */   public synchronized String getResourceId() {
/* 1184 */     return this.currentConnection.getResourceId();
/*      */   }
/*      */ 
/*      */   public synchronized int getResultSetSizeThreshold() {
/* 1188 */     return this.currentConnection.getResultSetSizeThreshold();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRewriteBatchedStatements() {
/* 1192 */     return this.currentConnection.getRewriteBatchedStatements();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRollbackOnPooledClose() {
/* 1196 */     return this.currentConnection.getRollbackOnPooledClose();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRoundRobinLoadBalance() {
/* 1200 */     return this.currentConnection.getRoundRobinLoadBalance();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRunningCTS13() {
/* 1204 */     return this.currentConnection.getRunningCTS13();
/*      */   }
/*      */ 
/*      */   public synchronized int getSecondsBeforeRetryMaster() {
/* 1208 */     return this.currentConnection.getSecondsBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public synchronized int getSelfDestructOnPingMaxOperations() {
/* 1212 */     return this.currentConnection.getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */ 
/*      */   public synchronized int getSelfDestructOnPingSecondsLifetime() {
/* 1216 */     return this.currentConnection.getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */ 
/*      */   public synchronized String getServerTimezone() {
/* 1220 */     return this.currentConnection.getServerTimezone();
/*      */   }
/*      */ 
/*      */   public synchronized String getSessionVariables() {
/* 1224 */     return this.currentConnection.getSessionVariables();
/*      */   }
/*      */ 
/*      */   public synchronized int getSlowQueryThresholdMillis() {
/* 1228 */     return this.currentConnection.getSlowQueryThresholdMillis();
/*      */   }
/*      */ 
/*      */   public synchronized long getSlowQueryThresholdNanos() {
/* 1232 */     return this.currentConnection.getSlowQueryThresholdNanos();
/*      */   }
/*      */ 
/*      */   public synchronized String getSocketFactory() {
/* 1236 */     return this.currentConnection.getSocketFactory();
/*      */   }
/*      */ 
/*      */   public synchronized String getSocketFactoryClassName() {
/* 1240 */     return this.currentConnection.getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public synchronized int getSocketTimeout() {
/* 1244 */     return this.currentConnection.getSocketTimeout();
/*      */   }
/*      */ 
/*      */   public synchronized String getStatementInterceptors() {
/* 1248 */     return this.currentConnection.getStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getStrictFloatingPoint() {
/* 1252 */     return this.currentConnection.getStrictFloatingPoint();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getStrictUpdates() {
/* 1256 */     return this.currentConnection.getStrictUpdates();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTcpKeepAlive() {
/* 1260 */     return this.currentConnection.getTcpKeepAlive();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTcpNoDelay() {
/* 1264 */     return this.currentConnection.getTcpNoDelay();
/*      */   }
/*      */ 
/*      */   public synchronized int getTcpRcvBuf() {
/* 1268 */     return this.currentConnection.getTcpRcvBuf();
/*      */   }
/*      */ 
/*      */   public synchronized int getTcpSndBuf() {
/* 1272 */     return this.currentConnection.getTcpSndBuf();
/*      */   }
/*      */ 
/*      */   public synchronized int getTcpTrafficClass() {
/* 1276 */     return this.currentConnection.getTcpTrafficClass();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTinyInt1isBit() {
/* 1280 */     return this.currentConnection.getTinyInt1isBit();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTraceProtocol() {
/* 1284 */     return this.currentConnection.getTraceProtocol();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTransformedBitIsBoolean() {
/* 1288 */     return this.currentConnection.getTransformedBitIsBoolean();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTreatUtilDateAsTimestamp() {
/* 1292 */     return this.currentConnection.getTreatUtilDateAsTimestamp();
/*      */   }
/*      */ 
/*      */   public synchronized String getTrustCertificateKeyStorePassword() {
/* 1296 */     return this.currentConnection.getTrustCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public synchronized String getTrustCertificateKeyStoreType() {
/* 1300 */     return this.currentConnection.getTrustCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public synchronized String getTrustCertificateKeyStoreUrl() {
/* 1304 */     return this.currentConnection.getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUltraDevHack() {
/* 1308 */     return this.currentConnection.getUltraDevHack();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 1312 */     return this.currentConnection.getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseCompression() {
/* 1316 */     return this.currentConnection.getUseCompression();
/*      */   }
/*      */ 
/*      */   public synchronized String getUseConfigs() {
/* 1320 */     return this.currentConnection.getUseConfigs();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseCursorFetch() {
/* 1324 */     return this.currentConnection.getUseCursorFetch();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseDirectRowUnpack() {
/* 1328 */     return this.currentConnection.getUseDirectRowUnpack();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseDynamicCharsetInfo() {
/* 1332 */     return this.currentConnection.getUseDynamicCharsetInfo();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseFastDateParsing() {
/* 1336 */     return this.currentConnection.getUseFastDateParsing();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseFastIntParsing() {
/* 1340 */     return this.currentConnection.getUseFastIntParsing();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseGmtMillisForDatetimes() {
/* 1344 */     return this.currentConnection.getUseGmtMillisForDatetimes();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseHostsInPrivileges() {
/* 1348 */     return this.currentConnection.getUseHostsInPrivileges();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseInformationSchema() {
/* 1352 */     return this.currentConnection.getUseInformationSchema();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseJDBCCompliantTimezoneShift() {
/* 1356 */     return this.currentConnection.getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseJvmCharsetConverters() {
/* 1360 */     return this.currentConnection.getUseJvmCharsetConverters();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseLegacyDatetimeCode() {
/* 1364 */     return this.currentConnection.getUseLegacyDatetimeCode();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseLocalSessionState() {
/* 1368 */     return this.currentConnection.getUseLocalSessionState();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseNanosForElapsedTime() {
/* 1372 */     return this.currentConnection.getUseNanosForElapsedTime();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseOldAliasMetadataBehavior() {
/* 1376 */     return this.currentConnection.getUseOldAliasMetadataBehavior();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseOldUTF8Behavior() {
/* 1380 */     return this.currentConnection.getUseOldUTF8Behavior();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseOnlyServerErrorMessages() {
/* 1384 */     return this.currentConnection.getUseOnlyServerErrorMessages();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseReadAheadInput() {
/* 1388 */     return this.currentConnection.getUseReadAheadInput();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseSSL() {
/* 1392 */     return this.currentConnection.getUseSSL();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseSSPSCompatibleTimezoneShift() {
/* 1396 */     return this.currentConnection.getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseServerPrepStmts() {
/* 1400 */     return this.currentConnection.getUseServerPrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseServerPreparedStmts() {
/* 1404 */     return this.currentConnection.getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseSqlStateCodes() {
/* 1408 */     return this.currentConnection.getUseSqlStateCodes();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseStreamLengthsInPrepStmts() {
/* 1412 */     return this.currentConnection.getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseTimezone() {
/* 1416 */     return this.currentConnection.getUseTimezone();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUltraDevWorkAround() {
/* 1420 */     return this.currentConnection.getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUnbufferedInput() {
/* 1424 */     return this.currentConnection.getUseUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUnicode() {
/* 1428 */     return this.currentConnection.getUseUnicode();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getUseUsageAdvisor() {
/* 1432 */     return this.currentConnection.getUseUsageAdvisor();
/*      */   }
/*      */ 
/*      */   public synchronized String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 1436 */     return this.currentConnection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public synchronized String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 1440 */     return this.currentConnection.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getVerifyServerCertificate() {
/* 1444 */     return this.currentConnection.getVerifyServerCertificate();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getYearIsDateType() {
/* 1448 */     return this.currentConnection.getYearIsDateType();
/*      */   }
/*      */ 
/*      */   public synchronized String getZeroDateTimeBehavior() {
/* 1452 */     return this.currentConnection.getZeroDateTimeBehavior();
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowLoadLocalInfile(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowMultiQueries(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowNanAndInf(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoClosePStmtStreams(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoDeserialize(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoGenerateTestcaseScript(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoReconnect(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoReconnectForPools(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoSlowLog(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setBlobSendChunkSize(String value)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setBlobsAreStrings(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheCallableStatements(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheCallableStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCachePrepStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCachePreparedStatements(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheResultSetMetadata(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCacheServerConfiguration(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCallableStatementCacheSize(int size)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCallableStmtCacheSize(int cacheSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCapitalizeTypeNames(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCharacterEncoding(String encoding)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCharacterSetResults(String characterSet)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientCertificateKeyStorePassword(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientCertificateKeyStoreType(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientCertificateKeyStoreUrl(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClientInfoProvider(String classname)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClobCharacterEncoding(String encoding)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setClobberStreamingResults(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setConnectTimeout(int timeoutMs)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setConnectionCollation(String collation)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setConnectionLifecycleInterceptors(String interceptors)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setContinueBatchOnError(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setCreateDatabaseIfNotExist(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDefaultFetchSize(int n)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDetectServerPreparedStmts(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDontTrackOpenResources(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDumpMetadataOnColumnNotFound(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDumpQueriesOnException(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setDynamicCalendars(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setElideSetAutoCommits(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEmptyStringsConvertToZero(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEmulateLocators(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEnablePacketDebug(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEnableQueryTimeouts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEncoding(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setExplainSlowQueries(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setFailOverReadOnly(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setFunctionsNeverReturnBlobs(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setGatherPerfMetrics(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setGenerateSimpleParameterMetadata(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setIgnoreNonTxTables(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setIncludeInnodbStatusInDeadlockExceptions(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setInitialTimeout(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setInteractiveClient(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setIsInteractiveClient(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLargeRowSizeThreshold(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLoadBalanceStrategy(String strategy)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLocalSocketAddress(String address)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLocatorFetchBufferSize(String value)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLogSlowQueries(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLogXaCommands(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLogger(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setLoggerClassName(String className)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaintainTimeStats(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxQuerySizeToLog(int sizeInBytes)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxReconnects(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxRows(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setMetadataCacheSize(int value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNetTimeoutForStreamingResults(int value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNoDatetimeStringSync(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPacketDebugBufferSize(int size)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPadCharsWithSpace(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setParanoid(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPedantic(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPinGlobalTxToPhysicalConnection(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPopulateInsertRowWithDefaultValues(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPrepStmtCacheSize(int cacheSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPrepStmtCacheSqlLimit(int sqlLimit)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPreparedStatementCacheSize(int cacheSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProfileSQL(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProfileSql(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setProfilerEventHandler(String handler)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setPropertiesTransform(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setQueriesBeforeRetryMaster(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setReconnectAtTxEnd(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRelaxAutoCommit(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setReportMetricsIntervalMillis(int millis)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRequireSSL(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setResourceId(String resourceId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setResultSetSizeThreshold(int threshold)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRewriteBatchedStatements(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRollbackOnPooledClose(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setRunningCTS13(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSecondsBeforeRetryMaster(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSelfDestructOnPingMaxOperations(int maxOperations)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSelfDestructOnPingSecondsLifetime(int seconds)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setServerTimezone(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSessionVariables(String variables)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSlowQueryThresholdMillis(int millis)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSlowQueryThresholdNanos(long nanos)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSocketFactory(String name)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSocketFactoryClassName(String property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setSocketTimeout(int property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setStatementInterceptors(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setStrictFloatingPoint(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setStrictUpdates(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpKeepAlive(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpNoDelay(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpRcvBuf(int bufSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpSndBuf(int bufSize)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTcpTrafficClass(int classFlags)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTinyInt1isBit(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTraceProtocol(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTrustCertificateKeyStoreType(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUltraDevHack(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseBlobToStoreUTF8OutsideBMP(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseCompression(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseConfigs(String configs)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseCursorFetch(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseDirectRowUnpack(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseFastDateParsing(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseFastIntParsing(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseHostsInPrivileges(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseInformationSchema(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseJvmCharsetConverters(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseLegacyDatetimeCode(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseLocalSessionState(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseNanosForElapsedTime(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseReadAheadInput(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseSSL(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseServerPrepStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseServerPreparedStmts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseSqlStateCodes(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseTimezone(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUltraDevWorkAround(boolean property)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUnbufferedInput(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUnicode(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setVerifyServerCertificate(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setYearIsDateType(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setZeroDateTimeBehavior(String behavior)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized boolean useUnbufferedInput()
/*      */   {
/* 2322 */     return this.currentConnection.useUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isSameResource(Connection c) {
/* 2326 */     return this.currentConnection.isSameResource(c);
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag) {
/* 2330 */     this.currentConnection.setInGlobalTx(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 2334 */     return this.currentConnection.getUseColumnNamesInFindColumn();
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/* 2342 */     return this.currentConnection.getUseLocalTransactionState();
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts()
/*      */   {
/* 2351 */     return this.currentConnection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows()
/*      */   {
/* 2360 */     return this.currentConnection.getUseAffectedRows();
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding()
/*      */   {
/* 2369 */     return this.currentConnection.getPasswordCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 2373 */     this.currentConnection.setPasswordCharacterEncoding(characterSet);
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement() {
/* 2377 */     return this.currentConnection.getAutoIncrementIncrement();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 2381 */     return this.currentConnection.getLoadBalanceBlacklistTimeout();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 2385 */     this.currentConnection.setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public int getLoadBalancePingTimeout() {
/* 2389 */     return this.currentConnection.getLoadBalancePingTimeout();
/*      */   }
/*      */ 
/*      */   public void setLoadBalancePingTimeout(int loadBalancePingTimeout) {
/* 2393 */     this.currentConnection.setLoadBalancePingTimeout(loadBalancePingTimeout);
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceValidateConnectionOnSwapServer() {
/* 2397 */     return this.currentConnection.getLoadBalanceValidateConnectionOnSwapServer();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
/* 2401 */     this.currentConnection.setLoadBalanceValidateConnectionOnSwapServer(loadBalanceValidateConnectionOnSwapServer);
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown() {
/* 2405 */     return this.currentConnection.getRetriesAllDown();
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown) {
/* 2409 */     this.currentConnection.setRetriesAllDown(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2413 */     return this.currentConnection.getExceptionInterceptor();
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/* 2417 */     return this.currentConnection.getExceptionInterceptors();
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 2421 */     this.currentConnection.setExceptionInterceptors(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 2425 */     return this.currentConnection.getQueryTimeoutKillsConnection();
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection)
/*      */   {
/* 2430 */     this.currentConnection.setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 2434 */     return (this.masterConnection.hasSameProperties(c)) && (this.slavesConnection.hasSameProperties(c));
/*      */   }
/*      */ 
/*      */   public Properties getProperties()
/*      */   {
/* 2439 */     Properties props = new Properties();
/* 2440 */     props.putAll(this.masterConnection.getProperties());
/* 2441 */     props.putAll(this.slavesConnection.getProperties());
/*      */ 
/* 2443 */     return props;
/*      */   }
/*      */ 
/*      */   public String getHost() {
/* 2447 */     return this.currentConnection.getHost();
/*      */   }
/*      */ 
/*      */   public void setProxy(MySQLConnection proxy) {
/* 2451 */     this.currentConnection.setProxy(proxy);
/*      */   }
/*      */ 
/*      */   public synchronized boolean getRetainStatementAfterResultSetClose() {
/* 2455 */     return this.currentConnection.getRetainStatementAfterResultSetClose();
/*      */   }
/*      */ 
/*      */   public int getMaxAllowedPacket() {
/* 2459 */     return this.currentConnection.getMaxAllowedPacket();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceConnectionGroup() {
/* 2463 */     return this.currentConnection.getLoadBalanceConnectionGroup();
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceEnableJMX() {
/* 2467 */     return this.currentConnection.getLoadBalanceEnableJMX();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceExceptionChecker() {
/* 2471 */     return this.currentConnection.getLoadBalanceExceptionChecker();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLExceptionSubclassFailover()
/*      */   {
/* 2476 */     return this.currentConnection.getLoadBalanceSQLExceptionSubclassFailover();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLStateFailover()
/*      */   {
/* 2481 */     return this.currentConnection.getLoadBalanceSQLStateFailover();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup)
/*      */   {
/* 2486 */     this.currentConnection.setLoadBalanceConnectionGroup(loadBalanceConnectionGroup);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX)
/*      */   {
/* 2492 */     this.currentConnection.setLoadBalanceEnableJMX(loadBalanceEnableJMX);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker)
/*      */   {
/* 2499 */     this.currentConnection.setLoadBalanceExceptionChecker(loadBalanceExceptionChecker);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover)
/*      */   {
/* 2506 */     this.currentConnection.setLoadBalanceSQLExceptionSubclassFailover(loadBalanceSQLExceptionSubclassFailover);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover)
/*      */   {
/* 2513 */     this.currentConnection.setLoadBalanceSQLStateFailover(loadBalanceSQLStateFailover);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceAutoCommitStatementRegex()
/*      */   {
/* 2519 */     return this.currentConnection.getLoadBalanceAutoCommitStatementRegex();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceAutoCommitStatementThreshold() {
/* 2523 */     return this.currentConnection.getLoadBalanceAutoCommitStatementThreshold();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex)
/*      */   {
/* 2528 */     this.currentConnection.setLoadBalanceAutoCommitStatementRegex(loadBalanceAutoCommitStatementRegex);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold)
/*      */   {
/* 2534 */     this.currentConnection.setLoadBalanceAutoCommitStatementThreshold(loadBalanceAutoCommitStatementThreshold);
/*      */   }
/*      */ 
/*      */   public void setTypeMap(Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadDumpInDeadlockExceptions()
/*      */   {
/* 2544 */     return this.currentConnection.getIncludeThreadDumpInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
/* 2548 */     this.currentConnection.setIncludeThreadDumpInDeadlockExceptions(flag);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadNamesAsStatementComment()
/*      */   {
/* 2553 */     return this.currentConnection.getIncludeThreadNamesAsStatementComment();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadNamesAsStatementComment(boolean flag) {
/* 2557 */     this.currentConnection.setIncludeThreadNamesAsStatementComment(flag);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isServerLocal() throws SQLException {
/* 2561 */     return this.currentConnection.isServerLocal();
/*      */   }
/*      */ 
/*      */   public void setAuthenticationPlugins(String authenticationPlugins) {
/* 2565 */     this.currentConnection.setAuthenticationPlugins(authenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getAuthenticationPlugins() {
/* 2569 */     return this.currentConnection.getAuthenticationPlugins();
/*      */   }
/*      */ 
/*      */   public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins)
/*      */   {
/* 2574 */     this.currentConnection.setDisabledAuthenticationPlugins(disabledAuthenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getDisabledAuthenticationPlugins() {
/* 2578 */     return this.currentConnection.getDisabledAuthenticationPlugins();
/*      */   }
/*      */ 
/*      */   public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin)
/*      */   {
/* 2583 */     this.currentConnection.setDefaultAuthenticationPlugin(defaultAuthenticationPlugin);
/*      */   }
/*      */ 
/*      */   public String getDefaultAuthenticationPlugin() {
/* 2587 */     return this.currentConnection.getDefaultAuthenticationPlugin();
/*      */   }
/*      */ 
/*      */   public void setParseInfoCacheFactory(String factoryClassname) {
/* 2591 */     this.currentConnection.setParseInfoCacheFactory(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getParseInfoCacheFactory() {
/* 2595 */     return this.currentConnection.getParseInfoCacheFactory();
/*      */   }
/*      */ 
/*      */   public void setSchema(String schema) throws SQLException {
/* 2599 */     this.currentConnection.setSchema(schema);
/*      */   }
/*      */ 
/*      */   public String getSchema() throws SQLException {
/* 2603 */     return this.currentConnection.getSchema();
/*      */   }
/*      */ 
/*      */   public void abort(Executor executor) throws SQLException {
/* 2607 */     this.currentConnection.abort(executor);
/*      */   }
/*      */ 
/*      */   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
/*      */   {
/* 2612 */     this.currentConnection.setNetworkTimeout(executor, milliseconds);
/*      */   }
/*      */ 
/*      */   public int getNetworkTimeout() throws SQLException {
/* 2616 */     return this.currentConnection.getNetworkTimeout();
/*      */   }
/*      */ 
/*      */   public void setServerConfigCacheFactory(String factoryClassname) {
/* 2620 */     this.currentConnection.setServerConfigCacheFactory(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getServerConfigCacheFactory() {
/* 2624 */     return this.currentConnection.getServerConfigCacheFactory();
/*      */   }
/*      */ 
/*      */   public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
/* 2628 */     this.currentConnection.setDisconnectOnExpiredPasswords(disconnectOnExpiredPasswords);
/*      */   }
/*      */ 
/*      */   public boolean getDisconnectOnExpiredPasswords() {
/* 2632 */     return this.currentConnection.getDisconnectOnExpiredPasswords();
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ReplicationConnection
 * JD-Core Version:    0.6.0
 */