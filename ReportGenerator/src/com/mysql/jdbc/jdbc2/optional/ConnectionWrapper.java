/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.Connection;
/*      */ import com.mysql.jdbc.ExceptionInterceptor;
/*      */ import com.mysql.jdbc.Extension;
/*      */ import com.mysql.jdbc.MySQLConnection;
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.Util;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ public class ConnectionWrapper extends WrapperBase
/*      */   implements Connection
/*      */ {
/*   69 */   protected Connection mc = null;
/*      */ 
/*   71 */   private String invalidHandleStr = "Logical handle no longer valid";
/*      */   private boolean closed;
/*      */   private boolean isForXa;
/*      */   private static final Constructor<?> JDBC_4_CONNECTION_WRAPPER_CTOR;
/*      */ 
/*      */   protected static ConnectionWrapper getInstance(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa)
/*      */     throws SQLException
/*      */   {
/*  102 */     if (!Util.isJdbc4()) {
/*  103 */       return new ConnectionWrapper(mysqlPooledConnection, mysqlConnection, forXa);
/*      */     }
/*      */ 
/*  107 */     return (ConnectionWrapper)Util.handleNewInstance(JDBC_4_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlPooledConnection, mysqlConnection, Boolean.valueOf(forXa) }, mysqlPooledConnection.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, Connection mysqlConnection, boolean forXa)
/*      */     throws SQLException
/*      */   {
/*  126 */     super(mysqlPooledConnection);
/*      */ 
/*  128 */     this.mc = mysqlConnection;
/*  129 */     this.closed = false;
/*  130 */     this.isForXa = forXa;
/*      */ 
/*  132 */     if (this.isForXa)
/*  133 */       setInGlobalTx(false);
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean autoCommit)
/*      */     throws SQLException
/*      */   {
/*  144 */     checkClosed();
/*      */ 
/*  146 */     if ((autoCommit) && (isInGlobalTx())) {
/*  147 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  154 */       this.mc.setAutoCommit(autoCommit);
/*      */     } catch (SQLException sqlException) {
/*  156 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/*  167 */     checkClosed();
/*      */     try
/*      */     {
/*  170 */       return this.mc.getAutoCommit();
/*      */     } catch (SQLException sqlException) {
/*  172 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  175 */     return false;
/*      */   }
/*      */ 
/*      */   public void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/*  185 */     checkClosed();
/*      */     try
/*      */     {
/*  188 */       this.mc.setCatalog(catalog);
/*      */     } catch (SQLException sqlException) {
/*  190 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/*  204 */     checkClosed();
/*      */     try
/*      */     {
/*  207 */       return this.mc.getCatalog();
/*      */     } catch (SQLException sqlException) {
/*  209 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  212 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/*  222 */     return (this.closed) || (this.mc.isClosed());
/*      */   }
/*      */ 
/*      */   public boolean isMasterConnection() {
/*  226 */     return this.mc.isMasterConnection();
/*      */   }
/*      */ 
/*      */   public void setHoldability(int arg0)
/*      */     throws SQLException
/*      */   {
/*  233 */     checkClosed();
/*      */     try
/*      */     {
/*  236 */       this.mc.setHoldability(arg0);
/*      */     } catch (SQLException sqlException) {
/*  238 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/*  246 */     checkClosed();
/*      */     try
/*      */     {
/*  249 */       return this.mc.getHoldability();
/*      */     } catch (SQLException sqlException) {
/*  251 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  254 */     return 1;
/*      */   }
/*      */ 
/*      */   public long getIdleFor()
/*      */   {
/*  264 */     return this.mc.getIdleFor();
/*      */   }
/*      */ 
/*      */   public DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  277 */     checkClosed();
/*      */     try
/*      */     {
/*  280 */       return this.mc.getMetaData();
/*      */     } catch (SQLException sqlException) {
/*  282 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  285 */     return null;
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean readOnly)
/*      */     throws SQLException
/*      */   {
/*  295 */     checkClosed();
/*      */     try
/*      */     {
/*  298 */       this.mc.setReadOnly(readOnly);
/*      */     } catch (SQLException sqlException) {
/*  300 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  311 */     checkClosed();
/*      */     try
/*      */     {
/*  314 */       return this.mc.isReadOnly();
/*      */     } catch (SQLException sqlException) {
/*  316 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  319 */     return false;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/*  326 */     checkClosed();
/*      */ 
/*  328 */     if (isInGlobalTx()) {
/*  329 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  336 */       return this.mc.setSavepoint();
/*      */     } catch (SQLException sqlException) {
/*  338 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  341 */     return null;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint(String arg0)
/*      */     throws SQLException
/*      */   {
/*  348 */     checkClosed();
/*      */ 
/*  350 */     if (isInGlobalTx()) {
/*  351 */       throw SQLError.createSQLException("Can't set autocommit to 'true' on an XAConnection", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  358 */       return this.mc.setSavepoint(arg0);
/*      */     } catch (SQLException sqlException) {
/*  360 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  363 */     return null;
/*      */   }
/*      */ 
/*      */   public void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/*  373 */     checkClosed();
/*      */     try
/*      */     {
/*  376 */       this.mc.setTransactionIsolation(level);
/*      */     } catch (SQLException sqlException) {
/*  378 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/*  389 */     checkClosed();
/*      */     try
/*      */     {
/*  392 */       return this.mc.getTransactionIsolation();
/*      */     } catch (SQLException sqlException) {
/*  394 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  397 */     return 4;
/*      */   }
/*      */ 
/*      */   public Map<String, Class<?>> getTypeMap()
/*      */     throws SQLException
/*      */   {
/*  409 */     checkClosed();
/*      */     try
/*      */     {
/*  412 */       return this.mc.getTypeMap();
/*      */     } catch (SQLException sqlException) {
/*  414 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  417 */     return null;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*  427 */     checkClosed();
/*      */     try
/*      */     {
/*  430 */       return this.mc.getWarnings();
/*      */     } catch (SQLException sqlException) {
/*  432 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  435 */     return null;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  446 */     checkClosed();
/*      */     try
/*      */     {
/*  449 */       this.mc.clearWarnings();
/*      */     } catch (SQLException sqlException) {
/*  451 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  466 */     close(true);
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/*  477 */     checkClosed();
/*      */ 
/*  479 */     if (isInGlobalTx()) {
/*  480 */       throw SQLError.createSQLException("Can't call commit() on an XAConnection associated with a global transaction", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  488 */       this.mc.commit();
/*      */     } catch (SQLException sqlException) {
/*  490 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Statement createStatement()
/*      */     throws SQLException
/*      */   {
/*  501 */     checkClosed();
/*      */     try
/*      */     {
/*  504 */       return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement());
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  507 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  510 */     return null;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  521 */     checkClosed();
/*      */     try
/*      */     {
/*  524 */       return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  527 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  530 */     return null;
/*      */   }
/*      */ 
/*      */   public Statement createStatement(int arg0, int arg1, int arg2)
/*      */     throws SQLException
/*      */   {
/*  538 */     checkClosed();
/*      */     try
/*      */     {
/*  541 */       return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(arg0, arg1, arg2));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  544 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  547 */     return null;
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/*  557 */     checkClosed();
/*      */     try
/*      */     {
/*  560 */       return this.mc.nativeSQL(sql);
/*      */     } catch (SQLException sqlException) {
/*  562 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  565 */     return null;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/*  576 */     checkClosed();
/*      */     try
/*      */     {
/*  579 */       return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  582 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  585 */     return null;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  596 */     checkClosed();
/*      */     try
/*      */     {
/*  599 */       return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  602 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  605 */     return null;
/*      */   }
/*      */ 
/*      */   public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3)
/*      */     throws SQLException
/*      */   {
/*  613 */     checkClosed();
/*      */     try
/*      */     {
/*  616 */       return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(arg0, arg1, arg2, arg3));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  619 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  622 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepare(String sql) throws SQLException
/*      */   {
/*  627 */     checkClosed();
/*      */     try
/*      */     {
/*  630 */       return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  633 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  636 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*  641 */     checkClosed();
/*      */     try
/*      */     {
/*  644 */       return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  648 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  651 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/*  662 */     checkClosed();
/*      */     try
/*      */     {
/*  665 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  668 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  671 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  682 */     checkClosed();
/*      */     try
/*      */     {
/*  685 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  689 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  692 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3)
/*      */     throws SQLException
/*      */   {
/*  700 */     checkClosed();
/*      */     try
/*      */     {
/*  703 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1, arg2, arg3));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  706 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  709 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, int arg1)
/*      */     throws SQLException
/*      */   {
/*  717 */     checkClosed();
/*      */     try
/*      */     {
/*  720 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  723 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  726 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, int[] arg1)
/*      */     throws SQLException
/*      */   {
/*  734 */     checkClosed();
/*      */     try
/*      */     {
/*  737 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  740 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  743 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement prepareStatement(String arg0, String[] arg1)
/*      */     throws SQLException
/*      */   {
/*  751 */     checkClosed();
/*      */     try
/*      */     {
/*  754 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  757 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  760 */     return null;
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*  767 */     checkClosed();
/*      */     try
/*      */     {
/*  770 */       this.mc.releaseSavepoint(arg0);
/*      */     } catch (SQLException sqlException) {
/*  772 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/*  783 */     checkClosed();
/*      */ 
/*  785 */     if (isInGlobalTx()) {
/*  786 */       throw SQLError.createSQLException("Can't call rollback() on an XAConnection associated with a global transaction", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  794 */       this.mc.rollback();
/*      */     } catch (SQLException sqlException) {
/*  796 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*  804 */     checkClosed();
/*      */ 
/*  806 */     if (isInGlobalTx()) {
/*  807 */       throw SQLError.createSQLException("Can't call rollback() on an XAConnection associated with a global transaction", "2D000", 1401, this.exceptionInterceptor);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  815 */       this.mc.rollback(arg0);
/*      */     } catch (SQLException sqlException) {
/*  817 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isSameResource(Connection c) {
/*  822 */     if ((c instanceof ConnectionWrapper)) {
/*  823 */       return this.mc.isSameResource(((ConnectionWrapper)c).mc);
/*      */     }
/*  825 */     return this.mc.isSameResource(c);
/*      */   }
/*      */ 
/*      */   protected void close(boolean fireClosedEvent) throws SQLException {
/*  829 */     synchronized (this.pooledConnection) {
/*  830 */       if (this.closed) {
/*  831 */         return;
/*      */       }
/*      */ 
/*  834 */       if ((!isInGlobalTx()) && (this.mc.getRollbackOnPooledClose()) && (!getAutoCommit()))
/*      */       {
/*  836 */         rollback();
/*      */       }
/*      */ 
/*  839 */       if (fireClosedEvent) {
/*  840 */         this.pooledConnection.callConnectionEventListeners(2, null);
/*      */       }
/*      */ 
/*  849 */       this.closed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkClosed() throws SQLException {
/*  854 */     if (this.closed)
/*  855 */       throw SQLError.createSQLException(this.invalidHandleStr, this.exceptionInterceptor);
/*      */   }
/*      */ 
/*      */   public boolean isInGlobalTx()
/*      */   {
/*  860 */     return this.mc.isInGlobalTx();
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag) {
/*  864 */     this.mc.setInGlobalTx(flag);
/*      */   }
/*      */ 
/*      */   public void ping() throws SQLException {
/*  868 */     if (this.mc != null)
/*  869 */       this.mc.ping();
/*      */   }
/*      */ 
/*      */   public void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/*  875 */     checkClosed();
/*      */     try
/*      */     {
/*  878 */       this.mc.changeUser(userName, newPassword);
/*      */     } catch (SQLException sqlException) {
/*  880 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearHasTriedMaster() {
/*  885 */     this.mc.clearHasTriedMaster();
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException
/*      */   {
/*  890 */     checkClosed();
/*      */     try
/*      */     {
/*  893 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  896 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  899 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*      */     try {
/*  905 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndex));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  908 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  911 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*      */     try {
/*  917 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  921 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  924 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  931 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/*  935 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  938 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*      */     try {
/*  944 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndexes));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  947 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  950 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*      */     try {
/*  956 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyColNames));
/*      */     }
/*      */     catch (SQLException sqlException) {
/*  959 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/*  962 */     return null;
/*      */   }
/*      */ 
/*      */   public int getActiveStatementCount() {
/*  966 */     return this.mc.getActiveStatementCount();
/*      */   }
/*      */ 
/*      */   public Log getLog() throws SQLException {
/*  970 */     return this.mc.getLog();
/*      */   }
/*      */ 
/*      */   public String getServerCharacterEncoding() {
/*  974 */     return this.mc.getServerCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public TimeZone getServerTimezoneTZ() {
/*  978 */     return this.mc.getServerTimezoneTZ();
/*      */   }
/*      */ 
/*      */   public String getStatementComment() {
/*  982 */     return this.mc.getStatementComment();
/*      */   }
/*      */ 
/*      */   public boolean hasTriedMaster() {
/*  986 */     return this.mc.hasTriedMaster();
/*      */   }
/*      */ 
/*      */   public boolean isAbonormallyLongQuery(long millisOrNanos) {
/*  990 */     return this.mc.isAbonormallyLongQuery(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet() {
/*  994 */     return this.mc.isNoBackslashEscapesSet();
/*      */   }
/*      */ 
/*      */   public boolean lowerCaseTableNames() {
/*  998 */     return this.mc.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean parserKnowsUnicode() {
/* 1002 */     return this.mc.parserKnowsUnicode();
/*      */   }
/*      */ 
/*      */   public void reportQueryTime(long millisOrNanos) {
/* 1006 */     this.mc.reportQueryTime(millisOrNanos);
/*      */   }
/*      */ 
/*      */   public void resetServerState() throws SQLException {
/* 1010 */     checkClosed();
/*      */     try
/*      */     {
/* 1013 */       this.mc.resetServerState();
/*      */     } catch (SQLException sqlException) {
/* 1015 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException
/*      */   {
/* 1021 */     checkClosed();
/*      */     try
/*      */     {
/* 1024 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1027 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1030 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException
/*      */   {
/*      */     try {
/* 1036 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndex));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1039 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1042 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*      */   {
/*      */     try {
/* 1048 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 1052 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1055 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1062 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 1066 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1069 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException
/*      */   {
/*      */     try {
/* 1075 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndexes));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1078 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1081 */     return null;
/*      */   }
/*      */ 
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException
/*      */   {
/*      */     try {
/* 1087 */       return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyColNames));
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 1090 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1093 */     return null;
/*      */   }
/*      */ 
/*      */   public void setFailedOver(boolean flag) {
/* 1097 */     this.mc.setFailedOver(flag);
/*      */   }
/*      */ 
/*      */   public void setPreferSlaveDuringFailover(boolean flag)
/*      */   {
/* 1102 */     this.mc.setPreferSlaveDuringFailover(flag);
/*      */   }
/*      */ 
/*      */   public void setStatementComment(String comment) {
/* 1106 */     this.mc.setStatementComment(comment);
/*      */   }
/*      */ 
/*      */   public void shutdownServer() throws SQLException
/*      */   {
/* 1111 */     checkClosed();
/*      */     try
/*      */     {
/* 1114 */       this.mc.shutdownServer();
/*      */     } catch (SQLException sqlException) {
/* 1116 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean supportsIsolationLevel()
/*      */   {
/* 1122 */     return this.mc.supportsIsolationLevel();
/*      */   }
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers() {
/* 1126 */     return this.mc.supportsQuotedIdentifiers();
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions() {
/* 1130 */     return this.mc.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/* 1135 */     checkClosed();
/*      */     try
/*      */     {
/* 1138 */       return this.mc.versionMeetsMinimum(major, minor, subminor);
/*      */     } catch (SQLException sqlException) {
/* 1140 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1143 */     return false;
/*      */   }
/*      */ 
/*      */   public String exposeAsXml() throws SQLException {
/* 1147 */     checkClosed();
/*      */     try
/*      */     {
/* 1150 */       return this.mc.exposeAsXml();
/*      */     } catch (SQLException sqlException) {
/* 1152 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */ 
/* 1155 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean getAllowLoadLocalInfile() {
/* 1159 */     return this.mc.getAllowLoadLocalInfile();
/*      */   }
/*      */ 
/*      */   public boolean getAllowMultiQueries() {
/* 1163 */     return this.mc.getAllowMultiQueries();
/*      */   }
/*      */ 
/*      */   public boolean getAllowNanAndInf() {
/* 1167 */     return this.mc.getAllowNanAndInf();
/*      */   }
/*      */ 
/*      */   public boolean getAllowUrlInLocalInfile() {
/* 1171 */     return this.mc.getAllowUrlInLocalInfile();
/*      */   }
/*      */ 
/*      */   public boolean getAlwaysSendSetIsolation() {
/* 1175 */     return this.mc.getAlwaysSendSetIsolation();
/*      */   }
/*      */ 
/*      */   public boolean getAutoClosePStmtStreams() {
/* 1179 */     return this.mc.getAutoClosePStmtStreams();
/*      */   }
/*      */ 
/*      */   public boolean getAutoDeserialize() {
/* 1183 */     return this.mc.getAutoDeserialize();
/*      */   }
/*      */ 
/*      */   public boolean getAutoGenerateTestcaseScript() {
/* 1187 */     return this.mc.getAutoGenerateTestcaseScript();
/*      */   }
/*      */ 
/*      */   public boolean getAutoReconnectForPools() {
/* 1191 */     return this.mc.getAutoReconnectForPools();
/*      */   }
/*      */ 
/*      */   public boolean getAutoSlowLog() {
/* 1195 */     return this.mc.getAutoSlowLog();
/*      */   }
/*      */ 
/*      */   public int getBlobSendChunkSize() {
/* 1199 */     return this.mc.getBlobSendChunkSize();
/*      */   }
/*      */ 
/*      */   public boolean getBlobsAreStrings() {
/* 1203 */     return this.mc.getBlobsAreStrings();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStatements() {
/* 1207 */     return this.mc.getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStmts() {
/* 1211 */     return this.mc.getCacheCallableStmts();
/*      */   }
/*      */ 
/*      */   public boolean getCachePrepStmts() {
/* 1215 */     return this.mc.getCachePrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getCachePreparedStatements() {
/* 1219 */     return this.mc.getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public boolean getCacheResultSetMetadata() {
/* 1223 */     return this.mc.getCacheResultSetMetadata();
/*      */   }
/*      */ 
/*      */   public boolean getCacheServerConfiguration() {
/* 1227 */     return this.mc.getCacheServerConfiguration();
/*      */   }
/*      */ 
/*      */   public int getCallableStatementCacheSize() {
/* 1231 */     return this.mc.getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public int getCallableStmtCacheSize() {
/* 1235 */     return this.mc.getCallableStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public boolean getCapitalizeTypeNames() {
/* 1239 */     return this.mc.getCapitalizeTypeNames();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetResults() {
/* 1243 */     return this.mc.getCharacterSetResults();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStorePassword() {
/* 1247 */     return this.mc.getClientCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreType() {
/* 1251 */     return this.mc.getClientCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreUrl() {
/* 1255 */     return this.mc.getClientCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public String getClientInfoProvider() {
/* 1259 */     return this.mc.getClientInfoProvider();
/*      */   }
/*      */ 
/*      */   public String getClobCharacterEncoding() {
/* 1263 */     return this.mc.getClobCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public boolean getClobberStreamingResults() {
/* 1267 */     return this.mc.getClobberStreamingResults();
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout() {
/* 1271 */     return this.mc.getConnectTimeout();
/*      */   }
/*      */ 
/*      */   public String getConnectionCollation() {
/* 1275 */     return this.mc.getConnectionCollation();
/*      */   }
/*      */ 
/*      */   public String getConnectionLifecycleInterceptors() {
/* 1279 */     return this.mc.getConnectionLifecycleInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getContinueBatchOnError() {
/* 1283 */     return this.mc.getContinueBatchOnError();
/*      */   }
/*      */ 
/*      */   public boolean getCreateDatabaseIfNotExist() {
/* 1287 */     return this.mc.getCreateDatabaseIfNotExist();
/*      */   }
/*      */ 
/*      */   public int getDefaultFetchSize() {
/* 1291 */     return this.mc.getDefaultFetchSize();
/*      */   }
/*      */ 
/*      */   public boolean getDontTrackOpenResources() {
/* 1295 */     return this.mc.getDontTrackOpenResources();
/*      */   }
/*      */ 
/*      */   public boolean getDumpMetadataOnColumnNotFound() {
/* 1299 */     return this.mc.getDumpMetadataOnColumnNotFound();
/*      */   }
/*      */ 
/*      */   public boolean getDumpQueriesOnException() {
/* 1303 */     return this.mc.getDumpQueriesOnException();
/*      */   }
/*      */ 
/*      */   public boolean getDynamicCalendars() {
/* 1307 */     return this.mc.getDynamicCalendars();
/*      */   }
/*      */ 
/*      */   public boolean getElideSetAutoCommits() {
/* 1311 */     return this.mc.getElideSetAutoCommits();
/*      */   }
/*      */ 
/*      */   public boolean getEmptyStringsConvertToZero() {
/* 1315 */     return this.mc.getEmptyStringsConvertToZero();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateLocators() {
/* 1319 */     return this.mc.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateUnsupportedPstmts() {
/* 1323 */     return this.mc.getEmulateUnsupportedPstmts();
/*      */   }
/*      */ 
/*      */   public boolean getEnablePacketDebug() {
/* 1327 */     return this.mc.getEnablePacketDebug();
/*      */   }
/*      */ 
/*      */   public boolean getEnableQueryTimeouts() {
/* 1331 */     return this.mc.getEnableQueryTimeouts();
/*      */   }
/*      */ 
/*      */   public String getEncoding() {
/* 1335 */     return this.mc.getEncoding();
/*      */   }
/*      */ 
/*      */   public boolean getExplainSlowQueries() {
/* 1339 */     return this.mc.getExplainSlowQueries();
/*      */   }
/*      */ 
/*      */   public boolean getFailOverReadOnly() {
/* 1343 */     return this.mc.getFailOverReadOnly();
/*      */   }
/*      */ 
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 1347 */     return this.mc.getFunctionsNeverReturnBlobs();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerfMetrics() {
/* 1351 */     return this.mc.getGatherPerfMetrics();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerformanceMetrics() {
/* 1355 */     return this.mc.getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public boolean getGenerateSimpleParameterMetadata() {
/* 1359 */     return this.mc.getGenerateSimpleParameterMetadata();
/*      */   }
/*      */ 
/*      */   public boolean getHoldResultsOpenOverStatementClose() {
/* 1363 */     return this.mc.getHoldResultsOpenOverStatementClose();
/*      */   }
/*      */ 
/*      */   public boolean getIgnoreNonTxTables() {
/* 1367 */     return this.mc.getIgnoreNonTxTables();
/*      */   }
/*      */ 
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 1371 */     return this.mc.getIncludeInnodbStatusInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public int getInitialTimeout() {
/* 1375 */     return this.mc.getInitialTimeout();
/*      */   }
/*      */ 
/*      */   public boolean getInteractiveClient() {
/* 1379 */     return this.mc.getInteractiveClient();
/*      */   }
/*      */ 
/*      */   public boolean getIsInteractiveClient() {
/* 1383 */     return this.mc.getIsInteractiveClient();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncation() {
/* 1387 */     return this.mc.getJdbcCompliantTruncation();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncationForReads() {
/* 1391 */     return this.mc.getJdbcCompliantTruncationForReads();
/*      */   }
/*      */ 
/*      */   public String getLargeRowSizeThreshold() {
/* 1395 */     return this.mc.getLargeRowSizeThreshold();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceStrategy() {
/* 1399 */     return this.mc.getLoadBalanceStrategy();
/*      */   }
/*      */ 
/*      */   public String getLocalSocketAddress() {
/* 1403 */     return this.mc.getLocalSocketAddress();
/*      */   }
/*      */ 
/*      */   public int getLocatorFetchBufferSize() {
/* 1407 */     return this.mc.getLocatorFetchBufferSize();
/*      */   }
/*      */ 
/*      */   public boolean getLogSlowQueries() {
/* 1411 */     return this.mc.getLogSlowQueries();
/*      */   }
/*      */ 
/*      */   public boolean getLogXaCommands() {
/* 1415 */     return this.mc.getLogXaCommands();
/*      */   }
/*      */ 
/*      */   public String getLogger() {
/* 1419 */     return this.mc.getLogger();
/*      */   }
/*      */ 
/*      */   public String getLoggerClassName() {
/* 1423 */     return this.mc.getLoggerClassName();
/*      */   }
/*      */ 
/*      */   public boolean getMaintainTimeStats() {
/* 1427 */     return this.mc.getMaintainTimeStats();
/*      */   }
/*      */ 
/*      */   public int getMaxQuerySizeToLog() {
/* 1431 */     return this.mc.getMaxQuerySizeToLog();
/*      */   }
/*      */ 
/*      */   public int getMaxReconnects() {
/* 1435 */     return this.mc.getMaxReconnects();
/*      */   }
/*      */ 
/*      */   public int getMaxRows() {
/* 1439 */     return this.mc.getMaxRows();
/*      */   }
/*      */ 
/*      */   public int getMetadataCacheSize() {
/* 1443 */     return this.mc.getMetadataCacheSize();
/*      */   }
/*      */ 
/*      */   public int getNetTimeoutForStreamingResults() {
/* 1447 */     return this.mc.getNetTimeoutForStreamingResults();
/*      */   }
/*      */ 
/*      */   public boolean getNoAccessToProcedureBodies() {
/* 1451 */     return this.mc.getNoAccessToProcedureBodies();
/*      */   }
/*      */ 
/*      */   public boolean getNoDatetimeStringSync() {
/* 1455 */     return this.mc.getNoDatetimeStringSync();
/*      */   }
/*      */ 
/*      */   public boolean getNoTimezoneConversionForTimeType() {
/* 1459 */     return this.mc.getNoTimezoneConversionForTimeType();
/*      */   }
/*      */ 
/*      */   public boolean getNullCatalogMeansCurrent() {
/* 1463 */     return this.mc.getNullCatalogMeansCurrent();
/*      */   }
/*      */ 
/*      */   public boolean getNullNamePatternMatchesAll() {
/* 1467 */     return this.mc.getNullNamePatternMatchesAll();
/*      */   }
/*      */ 
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility() {
/* 1471 */     return this.mc.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public int getPacketDebugBufferSize() {
/* 1475 */     return this.mc.getPacketDebugBufferSize();
/*      */   }
/*      */ 
/*      */   public boolean getPadCharsWithSpace() {
/* 1479 */     return this.mc.getPadCharsWithSpace();
/*      */   }
/*      */ 
/*      */   public boolean getParanoid() {
/* 1483 */     return this.mc.getParanoid();
/*      */   }
/*      */ 
/*      */   public boolean getPedantic() {
/* 1487 */     return this.mc.getPedantic();
/*      */   }
/*      */ 
/*      */   public boolean getPinGlobalTxToPhysicalConnection() {
/* 1491 */     return this.mc.getPinGlobalTxToPhysicalConnection();
/*      */   }
/*      */ 
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 1495 */     return this.mc.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSize() {
/* 1499 */     return this.mc.getPrepStmtCacheSize();
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSqlLimit() {
/* 1503 */     return this.mc.getPrepStmtCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSize() {
/* 1507 */     return this.mc.getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSqlLimit() {
/* 1511 */     return this.mc.getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public boolean getProcessEscapeCodesForPrepStmts() {
/* 1515 */     return this.mc.getProcessEscapeCodesForPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSQL() {
/* 1519 */     return this.mc.getProfileSQL();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSql() {
/* 1523 */     return this.mc.getProfileSql();
/*      */   }
/*      */ 
/*      */   public String getPropertiesTransform() {
/* 1527 */     return this.mc.getPropertiesTransform();
/*      */   }
/*      */ 
/*      */   public int getQueriesBeforeRetryMaster() {
/* 1531 */     return this.mc.getQueriesBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public boolean getReconnectAtTxEnd() {
/* 1535 */     return this.mc.getReconnectAtTxEnd();
/*      */   }
/*      */ 
/*      */   public boolean getRelaxAutoCommit() {
/* 1539 */     return this.mc.getRelaxAutoCommit();
/*      */   }
/*      */ 
/*      */   public int getReportMetricsIntervalMillis() {
/* 1543 */     return this.mc.getReportMetricsIntervalMillis();
/*      */   }
/*      */ 
/*      */   public boolean getRequireSSL() {
/* 1547 */     return this.mc.getRequireSSL();
/*      */   }
/*      */ 
/*      */   public String getResourceId() {
/* 1551 */     return this.mc.getResourceId();
/*      */   }
/*      */ 
/*      */   public int getResultSetSizeThreshold() {
/* 1555 */     return this.mc.getResultSetSizeThreshold();
/*      */   }
/*      */ 
/*      */   public boolean getRewriteBatchedStatements() {
/* 1559 */     return this.mc.getRewriteBatchedStatements();
/*      */   }
/*      */ 
/*      */   public boolean getRollbackOnPooledClose() {
/* 1563 */     return this.mc.getRollbackOnPooledClose();
/*      */   }
/*      */ 
/*      */   public boolean getRoundRobinLoadBalance() {
/* 1567 */     return this.mc.getRoundRobinLoadBalance();
/*      */   }
/*      */ 
/*      */   public boolean getRunningCTS13() {
/* 1571 */     return this.mc.getRunningCTS13();
/*      */   }
/*      */ 
/*      */   public int getSecondsBeforeRetryMaster() {
/* 1575 */     return this.mc.getSecondsBeforeRetryMaster();
/*      */   }
/*      */ 
/*      */   public String getServerTimezone() {
/* 1579 */     return this.mc.getServerTimezone();
/*      */   }
/*      */ 
/*      */   public String getSessionVariables() {
/* 1583 */     return this.mc.getSessionVariables();
/*      */   }
/*      */ 
/*      */   public int getSlowQueryThresholdMillis() {
/* 1587 */     return this.mc.getSlowQueryThresholdMillis();
/*      */   }
/*      */ 
/*      */   public long getSlowQueryThresholdNanos() {
/* 1591 */     return this.mc.getSlowQueryThresholdNanos();
/*      */   }
/*      */ 
/*      */   public String getSocketFactory() {
/* 1595 */     return this.mc.getSocketFactory();
/*      */   }
/*      */ 
/*      */   public String getSocketFactoryClassName() {
/* 1599 */     return this.mc.getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public int getSocketTimeout() {
/* 1603 */     return this.mc.getSocketTimeout();
/*      */   }
/*      */ 
/*      */   public String getStatementInterceptors() {
/* 1607 */     return this.mc.getStatementInterceptors();
/*      */   }
/*      */ 
/*      */   public boolean getStrictFloatingPoint() {
/* 1611 */     return this.mc.getStrictFloatingPoint();
/*      */   }
/*      */ 
/*      */   public boolean getStrictUpdates() {
/* 1615 */     return this.mc.getStrictUpdates();
/*      */   }
/*      */ 
/*      */   public boolean getTcpKeepAlive() {
/* 1619 */     return this.mc.getTcpKeepAlive();
/*      */   }
/*      */ 
/*      */   public boolean getTcpNoDelay() {
/* 1623 */     return this.mc.getTcpNoDelay();
/*      */   }
/*      */ 
/*      */   public int getTcpRcvBuf() {
/* 1627 */     return this.mc.getTcpRcvBuf();
/*      */   }
/*      */ 
/*      */   public int getTcpSndBuf() {
/* 1631 */     return this.mc.getTcpSndBuf();
/*      */   }
/*      */ 
/*      */   public int getTcpTrafficClass() {
/* 1635 */     return this.mc.getTcpTrafficClass();
/*      */   }
/*      */ 
/*      */   public boolean getTinyInt1isBit() {
/* 1639 */     return this.mc.getTinyInt1isBit();
/*      */   }
/*      */ 
/*      */   public boolean getTraceProtocol() {
/* 1643 */     return this.mc.getTraceProtocol();
/*      */   }
/*      */ 
/*      */   public boolean getTransformedBitIsBoolean() {
/* 1647 */     return this.mc.getTransformedBitIsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTreatUtilDateAsTimestamp() {
/* 1651 */     return this.mc.getTreatUtilDateAsTimestamp();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStorePassword() {
/* 1655 */     return this.mc.getTrustCertificateKeyStorePassword();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreType() {
/* 1659 */     return this.mc.getTrustCertificateKeyStoreType();
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreUrl() {
/* 1663 */     return this.mc.getTrustCertificateKeyStoreUrl();
/*      */   }
/*      */ 
/*      */   public boolean getUltraDevHack() {
/* 1667 */     return this.mc.getUltraDevHack();
/*      */   }
/*      */ 
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 1671 */     return this.mc.getUseBlobToStoreUTF8OutsideBMP();
/*      */   }
/*      */ 
/*      */   public boolean getUseCompression() {
/* 1675 */     return this.mc.getUseCompression();
/*      */   }
/*      */ 
/*      */   public String getUseConfigs() {
/* 1679 */     return this.mc.getUseConfigs();
/*      */   }
/*      */ 
/*      */   public boolean getUseCursorFetch() {
/* 1683 */     return this.mc.getUseCursorFetch();
/*      */   }
/*      */ 
/*      */   public boolean getUseDirectRowUnpack() {
/* 1687 */     return this.mc.getUseDirectRowUnpack();
/*      */   }
/*      */ 
/*      */   public boolean getUseDynamicCharsetInfo() {
/* 1691 */     return this.mc.getUseDynamicCharsetInfo();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastDateParsing() {
/* 1695 */     return this.mc.getUseFastDateParsing();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastIntParsing() {
/* 1699 */     return this.mc.getUseFastIntParsing();
/*      */   }
/*      */ 
/*      */   public boolean getUseGmtMillisForDatetimes() {
/* 1703 */     return this.mc.getUseGmtMillisForDatetimes();
/*      */   }
/*      */ 
/*      */   public boolean getUseHostsInPrivileges() {
/* 1707 */     return this.mc.getUseHostsInPrivileges();
/*      */   }
/*      */ 
/*      */   public boolean getUseInformationSchema() {
/* 1711 */     return this.mc.getUseInformationSchema();
/*      */   }
/*      */ 
/*      */   public boolean getUseJDBCCompliantTimezoneShift() {
/* 1715 */     return this.mc.getUseJDBCCompliantTimezoneShift();
/*      */   }
/*      */ 
/*      */   public boolean getUseJvmCharsetConverters() {
/* 1719 */     return this.mc.getUseJvmCharsetConverters();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalSessionState() {
/* 1723 */     return this.mc.getUseLocalSessionState();
/*      */   }
/*      */ 
/*      */   public boolean getUseNanosForElapsedTime() {
/* 1727 */     return this.mc.getUseNanosForElapsedTime();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldAliasMetadataBehavior() {
/* 1731 */     return this.mc.getUseOldAliasMetadataBehavior();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldUTF8Behavior() {
/* 1735 */     return this.mc.getUseOldUTF8Behavior();
/*      */   }
/*      */ 
/*      */   public boolean getUseOnlyServerErrorMessages() {
/* 1739 */     return this.mc.getUseOnlyServerErrorMessages();
/*      */   }
/*      */ 
/*      */   public boolean getUseReadAheadInput() {
/* 1743 */     return this.mc.getUseReadAheadInput();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSL() {
/* 1747 */     return this.mc.getUseSSL();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSPSCompatibleTimezoneShift() {
/* 1751 */     return this.mc.getUseSSPSCompatibleTimezoneShift();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPrepStmts() {
/* 1755 */     return this.mc.getUseServerPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPreparedStmts() {
/* 1759 */     return this.mc.getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseSqlStateCodes() {
/* 1763 */     return this.mc.getUseSqlStateCodes();
/*      */   }
/*      */ 
/*      */   public boolean getUseStreamLengthsInPrepStmts() {
/* 1767 */     return this.mc.getUseStreamLengthsInPrepStmts();
/*      */   }
/*      */ 
/*      */   public boolean getUseTimezone() {
/* 1771 */     return this.mc.getUseTimezone();
/*      */   }
/*      */ 
/*      */   public boolean getUseUltraDevWorkAround() {
/* 1775 */     return this.mc.getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnbufferedInput() {
/* 1779 */     return this.mc.getUseUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnicode() {
/* 1783 */     return this.mc.getUseUnicode();
/*      */   }
/*      */ 
/*      */   public boolean getUseUsageAdvisor() {
/* 1787 */     return this.mc.getUseUsageAdvisor();
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 1791 */     return this.mc.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 1795 */     return this.mc.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */   }
/*      */ 
/*      */   public boolean getYearIsDateType() {
/* 1799 */     return this.mc.getYearIsDateType();
/*      */   }
/*      */ 
/*      */   public String getZeroDateTimeBehavior() {
/* 1803 */     return this.mc.getZeroDateTimeBehavior();
/*      */   }
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property) {
/* 1807 */     this.mc.setAllowLoadLocalInfile(property);
/*      */   }
/*      */ 
/*      */   public void setAllowMultiQueries(boolean property) {
/* 1811 */     this.mc.setAllowMultiQueries(property);
/*      */   }
/*      */ 
/*      */   public void setAllowNanAndInf(boolean flag) {
/* 1815 */     this.mc.setAllowNanAndInf(flag);
/*      */   }
/*      */ 
/*      */   public void setAllowUrlInLocalInfile(boolean flag) {
/* 1819 */     this.mc.setAllowUrlInLocalInfile(flag);
/*      */   }
/*      */ 
/*      */   public void setAlwaysSendSetIsolation(boolean flag) {
/* 1823 */     this.mc.setAlwaysSendSetIsolation(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoClosePStmtStreams(boolean flag) {
/* 1827 */     this.mc.setAutoClosePStmtStreams(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoDeserialize(boolean flag) {
/* 1831 */     this.mc.setAutoDeserialize(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoGenerateTestcaseScript(boolean flag) {
/* 1835 */     this.mc.setAutoGenerateTestcaseScript(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnect(boolean flag) {
/* 1839 */     this.mc.setAutoReconnect(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForConnectionPools(boolean property) {
/* 1843 */     this.mc.setAutoReconnectForConnectionPools(property);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForPools(boolean flag) {
/* 1847 */     this.mc.setAutoReconnectForPools(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoSlowLog(boolean flag) {
/* 1851 */     this.mc.setAutoSlowLog(flag);
/*      */   }
/*      */ 
/*      */   public void setBlobSendChunkSize(String value) throws SQLException {
/* 1855 */     this.mc.setBlobSendChunkSize(value);
/*      */   }
/*      */ 
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 1859 */     this.mc.setBlobsAreStrings(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStatements(boolean flag) {
/* 1863 */     this.mc.setCacheCallableStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStmts(boolean flag) {
/* 1867 */     this.mc.setCacheCallableStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePrepStmts(boolean flag) {
/* 1871 */     this.mc.setCachePrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePreparedStatements(boolean flag) {
/* 1875 */     this.mc.setCachePreparedStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheResultSetMetadata(boolean property) {
/* 1879 */     this.mc.setCacheResultSetMetadata(property);
/*      */   }
/*      */ 
/*      */   public void setCacheServerConfiguration(boolean flag) {
/* 1883 */     this.mc.setCacheServerConfiguration(flag);
/*      */   }
/*      */ 
/*      */   public void setCallableStatementCacheSize(int size) {
/* 1887 */     this.mc.setCallableStatementCacheSize(size);
/*      */   }
/*      */ 
/*      */   public void setCallableStmtCacheSize(int cacheSize) {
/* 1891 */     this.mc.setCallableStmtCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeDBMDTypes(boolean property) {
/* 1895 */     this.mc.setCapitalizeDBMDTypes(property);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeTypeNames(boolean flag) {
/* 1899 */     this.mc.setCapitalizeTypeNames(flag);
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String encoding) {
/* 1903 */     this.mc.setCharacterEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public void setCharacterSetResults(String characterSet) {
/* 1907 */     this.mc.setCharacterSetResults(characterSet);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStorePassword(String value) {
/* 1911 */     this.mc.setClientCertificateKeyStorePassword(value);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreType(String value) {
/* 1915 */     this.mc.setClientCertificateKeyStoreType(value);
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreUrl(String value) {
/* 1919 */     this.mc.setClientCertificateKeyStoreUrl(value);
/*      */   }
/*      */ 
/*      */   public void setClientInfoProvider(String classname) {
/* 1923 */     this.mc.setClientInfoProvider(classname);
/*      */   }
/*      */ 
/*      */   public void setClobCharacterEncoding(String encoding) {
/* 1927 */     this.mc.setClobCharacterEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public void setClobberStreamingResults(boolean flag) {
/* 1931 */     this.mc.setClobberStreamingResults(flag);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int timeoutMs) {
/* 1935 */     this.mc.setConnectTimeout(timeoutMs);
/*      */   }
/*      */ 
/*      */   public void setConnectionCollation(String collation) {
/* 1939 */     this.mc.setConnectionCollation(collation);
/*      */   }
/*      */ 
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 1943 */     this.mc.setConnectionLifecycleInterceptors(interceptors);
/*      */   }
/*      */ 
/*      */   public void setContinueBatchOnError(boolean property) {
/* 1947 */     this.mc.setContinueBatchOnError(property);
/*      */   }
/*      */ 
/*      */   public void setCreateDatabaseIfNotExist(boolean flag) {
/* 1951 */     this.mc.setCreateDatabaseIfNotExist(flag);
/*      */   }
/*      */ 
/*      */   public void setDefaultFetchSize(int n) {
/* 1955 */     this.mc.setDefaultFetchSize(n);
/*      */   }
/*      */ 
/*      */   public void setDetectServerPreparedStmts(boolean property) {
/* 1959 */     this.mc.setDetectServerPreparedStmts(property);
/*      */   }
/*      */ 
/*      */   public void setDontTrackOpenResources(boolean flag) {
/* 1963 */     this.mc.setDontTrackOpenResources(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag) {
/* 1967 */     this.mc.setDumpMetadataOnColumnNotFound(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpQueriesOnException(boolean flag) {
/* 1971 */     this.mc.setDumpQueriesOnException(flag);
/*      */   }
/*      */ 
/*      */   public void setDynamicCalendars(boolean flag) {
/* 1975 */     this.mc.setDynamicCalendars(flag);
/*      */   }
/*      */ 
/*      */   public void setElideSetAutoCommits(boolean flag) {
/* 1979 */     this.mc.setElideSetAutoCommits(flag);
/*      */   }
/*      */ 
/*      */   public void setEmptyStringsConvertToZero(boolean flag) {
/* 1983 */     this.mc.setEmptyStringsConvertToZero(flag);
/*      */   }
/*      */ 
/*      */   public void setEmulateLocators(boolean property) {
/* 1987 */     this.mc.setEmulateLocators(property);
/*      */   }
/*      */ 
/*      */   public void setEmulateUnsupportedPstmts(boolean flag) {
/* 1991 */     this.mc.setEmulateUnsupportedPstmts(flag);
/*      */   }
/*      */ 
/*      */   public void setEnablePacketDebug(boolean flag) {
/* 1995 */     this.mc.setEnablePacketDebug(flag);
/*      */   }
/*      */ 
/*      */   public void setEnableQueryTimeouts(boolean flag) {
/* 1999 */     this.mc.setEnableQueryTimeouts(flag);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String property) {
/* 2003 */     this.mc.setEncoding(property);
/*      */   }
/*      */ 
/*      */   public void setExplainSlowQueries(boolean flag) {
/* 2007 */     this.mc.setExplainSlowQueries(flag);
/*      */   }
/*      */ 
/*      */   public void setFailOverReadOnly(boolean flag) {
/* 2011 */     this.mc.setFailOverReadOnly(flag);
/*      */   }
/*      */ 
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 2015 */     this.mc.setFunctionsNeverReturnBlobs(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerfMetrics(boolean flag) {
/* 2019 */     this.mc.setGatherPerfMetrics(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerformanceMetrics(boolean flag) {
/* 2023 */     this.mc.setGatherPerformanceMetrics(flag);
/*      */   }
/*      */ 
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag) {
/* 2027 */     this.mc.setGenerateSimpleParameterMetadata(flag);
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag) {
/* 2031 */     this.mc.setHoldResultsOpenOverStatementClose(flag);
/*      */   }
/*      */ 
/*      */   public void setIgnoreNonTxTables(boolean property) {
/* 2035 */     this.mc.setIgnoreNonTxTables(property);
/*      */   }
/*      */ 
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 2039 */     this.mc.setIncludeInnodbStatusInDeadlockExceptions(flag);
/*      */   }
/*      */ 
/*      */   public void setInitialTimeout(int property) {
/* 2043 */     this.mc.setInitialTimeout(property);
/*      */   }
/*      */ 
/*      */   public void setInteractiveClient(boolean property) {
/* 2047 */     this.mc.setInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setIsInteractiveClient(boolean property) {
/* 2051 */     this.mc.setIsInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncation(boolean flag) {
/* 2055 */     this.mc.setJdbcCompliantTruncation(flag);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 2060 */     this.mc.setJdbcCompliantTruncationForReads(jdbcCompliantTruncationForReads);
/*      */   }
/*      */ 
/*      */   public void setLargeRowSizeThreshold(String value)
/*      */   {
/* 2065 */     this.mc.setLargeRowSizeThreshold(value);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 2069 */     this.mc.setLoadBalanceStrategy(strategy);
/*      */   }
/*      */ 
/*      */   public void setLocalSocketAddress(String address) {
/* 2073 */     this.mc.setLocalSocketAddress(address);
/*      */   }
/*      */ 
/*      */   public void setLocatorFetchBufferSize(String value) throws SQLException {
/* 2077 */     this.mc.setLocatorFetchBufferSize(value);
/*      */   }
/*      */ 
/*      */   public void setLogSlowQueries(boolean flag) {
/* 2081 */     this.mc.setLogSlowQueries(flag);
/*      */   }
/*      */ 
/*      */   public void setLogXaCommands(boolean flag) {
/* 2085 */     this.mc.setLogXaCommands(flag);
/*      */   }
/*      */ 
/*      */   public void setLogger(String property) {
/* 2089 */     this.mc.setLogger(property);
/*      */   }
/*      */ 
/*      */   public void setLoggerClassName(String className) {
/* 2093 */     this.mc.setLoggerClassName(className);
/*      */   }
/*      */ 
/*      */   public void setMaintainTimeStats(boolean flag) {
/* 2097 */     this.mc.setMaintainTimeStats(flag);
/*      */   }
/*      */ 
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes) {
/* 2101 */     this.mc.setMaxQuerySizeToLog(sizeInBytes);
/*      */   }
/*      */ 
/*      */   public void setMaxReconnects(int property) {
/* 2105 */     this.mc.setMaxReconnects(property);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int property) {
/* 2109 */     this.mc.setMaxRows(property);
/*      */   }
/*      */ 
/*      */   public void setMetadataCacheSize(int value) {
/* 2113 */     this.mc.setMetadataCacheSize(value);
/*      */   }
/*      */ 
/*      */   public void setNetTimeoutForStreamingResults(int value) {
/* 2117 */     this.mc.setNetTimeoutForStreamingResults(value);
/*      */   }
/*      */ 
/*      */   public void setNoAccessToProcedureBodies(boolean flag) {
/* 2121 */     this.mc.setNoAccessToProcedureBodies(flag);
/*      */   }
/*      */ 
/*      */   public void setNoDatetimeStringSync(boolean flag) {
/* 2125 */     this.mc.setNoDatetimeStringSync(flag);
/*      */   }
/*      */ 
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag) {
/* 2129 */     this.mc.setNoTimezoneConversionForTimeType(flag);
/*      */   }
/*      */ 
/*      */   public void setNullCatalogMeansCurrent(boolean value) {
/* 2133 */     this.mc.setNullCatalogMeansCurrent(value);
/*      */   }
/*      */ 
/*      */   public void setNullNamePatternMatchesAll(boolean value) {
/* 2137 */     this.mc.setNullNamePatternMatchesAll(value);
/*      */   }
/*      */ 
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
/* 2141 */     this.mc.setOverrideSupportsIntegrityEnhancementFacility(flag);
/*      */   }
/*      */ 
/*      */   public void setPacketDebugBufferSize(int size) {
/* 2145 */     this.mc.setPacketDebugBufferSize(size);
/*      */   }
/*      */ 
/*      */   public void setPadCharsWithSpace(boolean flag) {
/* 2149 */     this.mc.setPadCharsWithSpace(flag);
/*      */   }
/*      */ 
/*      */   public void setParanoid(boolean property) {
/* 2153 */     this.mc.setParanoid(property);
/*      */   }
/*      */ 
/*      */   public void setPedantic(boolean property) {
/* 2157 */     this.mc.setPedantic(property);
/*      */   }
/*      */ 
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag) {
/* 2161 */     this.mc.setPinGlobalTxToPhysicalConnection(flag);
/*      */   }
/*      */ 
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 2165 */     this.mc.setPopulateInsertRowWithDefaultValues(flag);
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSize(int cacheSize) {
/* 2169 */     this.mc.setPrepStmtCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit) {
/* 2173 */     this.mc.setPrepStmtCacheSqlLimit(sqlLimit);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSize(int cacheSize) {
/* 2177 */     this.mc.setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) {
/* 2181 */     this.mc.setPreparedStatementCacheSqlLimit(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag) {
/* 2185 */     this.mc.setProcessEscapeCodesForPrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setProfileSQL(boolean flag) {
/* 2189 */     this.mc.setProfileSQL(flag);
/*      */   }
/*      */ 
/*      */   public void setProfileSql(boolean property) {
/* 2193 */     this.mc.setProfileSql(property);
/*      */   }
/*      */ 
/*      */   public void setPropertiesTransform(String value) {
/* 2197 */     this.mc.setPropertiesTransform(value);
/*      */   }
/*      */ 
/*      */   public void setQueriesBeforeRetryMaster(int property) {
/* 2201 */     this.mc.setQueriesBeforeRetryMaster(property);
/*      */   }
/*      */ 
/*      */   public void setReconnectAtTxEnd(boolean property) {
/* 2205 */     this.mc.setReconnectAtTxEnd(property);
/*      */   }
/*      */ 
/*      */   public void setRelaxAutoCommit(boolean property) {
/* 2209 */     this.mc.setRelaxAutoCommit(property);
/*      */   }
/*      */ 
/*      */   public void setReportMetricsIntervalMillis(int millis) {
/* 2213 */     this.mc.setReportMetricsIntervalMillis(millis);
/*      */   }
/*      */ 
/*      */   public void setRequireSSL(boolean property) {
/* 2217 */     this.mc.setRequireSSL(property);
/*      */   }
/*      */ 
/*      */   public void setResourceId(String resourceId) {
/* 2221 */     this.mc.setResourceId(resourceId);
/*      */   }
/*      */ 
/*      */   public void setResultSetSizeThreshold(int threshold) {
/* 2225 */     this.mc.setResultSetSizeThreshold(threshold);
/*      */   }
/*      */ 
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag) {
/* 2229 */     this.mc.setRetainStatementAfterResultSetClose(flag);
/*      */   }
/*      */ 
/*      */   public void setRewriteBatchedStatements(boolean flag) {
/* 2233 */     this.mc.setRewriteBatchedStatements(flag);
/*      */   }
/*      */ 
/*      */   public void setRollbackOnPooledClose(boolean flag) {
/* 2237 */     this.mc.setRollbackOnPooledClose(flag);
/*      */   }
/*      */ 
/*      */   public void setRoundRobinLoadBalance(boolean flag) {
/* 2241 */     this.mc.setRoundRobinLoadBalance(flag);
/*      */   }
/*      */ 
/*      */   public void setRunningCTS13(boolean flag) {
/* 2245 */     this.mc.setRunningCTS13(flag);
/*      */   }
/*      */ 
/*      */   public void setSecondsBeforeRetryMaster(int property) {
/* 2249 */     this.mc.setSecondsBeforeRetryMaster(property);
/*      */   }
/*      */ 
/*      */   public void setServerTimezone(String property) {
/* 2253 */     this.mc.setServerTimezone(property);
/*      */   }
/*      */ 
/*      */   public void setSessionVariables(String variables) {
/* 2257 */     this.mc.setSessionVariables(variables);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdMillis(int millis) {
/* 2261 */     this.mc.setSlowQueryThresholdMillis(millis);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdNanos(long nanos) {
/* 2265 */     this.mc.setSlowQueryThresholdNanos(nanos);
/*      */   }
/*      */ 
/*      */   public void setSocketFactory(String name) {
/* 2269 */     this.mc.setSocketFactory(name);
/*      */   }
/*      */ 
/*      */   public void setSocketFactoryClassName(String property) {
/* 2273 */     this.mc.setSocketFactoryClassName(property);
/*      */   }
/*      */ 
/*      */   public void setSocketTimeout(int property) {
/* 2277 */     this.mc.setSocketTimeout(property);
/*      */   }
/*      */ 
/*      */   public void setStatementInterceptors(String value) {
/* 2281 */     this.mc.setStatementInterceptors(value);
/*      */   }
/*      */ 
/*      */   public void setStrictFloatingPoint(boolean property) {
/* 2285 */     this.mc.setStrictFloatingPoint(property);
/*      */   }
/*      */ 
/*      */   public void setStrictUpdates(boolean property) {
/* 2289 */     this.mc.setStrictUpdates(property);
/*      */   }
/*      */ 
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 2293 */     this.mc.setTcpKeepAlive(flag);
/*      */   }
/*      */ 
/*      */   public void setTcpNoDelay(boolean flag) {
/* 2297 */     this.mc.setTcpNoDelay(flag);
/*      */   }
/*      */ 
/*      */   public void setTcpRcvBuf(int bufSize) {
/* 2301 */     this.mc.setTcpRcvBuf(bufSize);
/*      */   }
/*      */ 
/*      */   public void setTcpSndBuf(int bufSize) {
/* 2305 */     this.mc.setTcpSndBuf(bufSize);
/*      */   }
/*      */ 
/*      */   public void setTcpTrafficClass(int classFlags) {
/* 2309 */     this.mc.setTcpTrafficClass(classFlags);
/*      */   }
/*      */ 
/*      */   public void setTinyInt1isBit(boolean flag) {
/* 2313 */     this.mc.setTinyInt1isBit(flag);
/*      */   }
/*      */ 
/*      */   public void setTraceProtocol(boolean flag) {
/* 2317 */     this.mc.setTraceProtocol(flag);
/*      */   }
/*      */ 
/*      */   public void setTransformedBitIsBoolean(boolean flag) {
/* 2321 */     this.mc.setTransformedBitIsBoolean(flag);
/*      */   }
/*      */ 
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag) {
/* 2325 */     this.mc.setTreatUtilDateAsTimestamp(flag);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStorePassword(String value) {
/* 2329 */     this.mc.setTrustCertificateKeyStorePassword(value);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreType(String value) {
/* 2333 */     this.mc.setTrustCertificateKeyStoreType(value);
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreUrl(String value) {
/* 2337 */     this.mc.setTrustCertificateKeyStoreUrl(value);
/*      */   }
/*      */ 
/*      */   public void setUltraDevHack(boolean flag) {
/* 2341 */     this.mc.setUltraDevHack(flag);
/*      */   }
/*      */ 
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 2345 */     this.mc.setUseBlobToStoreUTF8OutsideBMP(flag);
/*      */   }
/*      */ 
/*      */   public void setUseCompression(boolean property) {
/* 2349 */     this.mc.setUseCompression(property);
/*      */   }
/*      */ 
/*      */   public void setUseConfigs(String configs) {
/* 2353 */     this.mc.setUseConfigs(configs);
/*      */   }
/*      */ 
/*      */   public void setUseCursorFetch(boolean flag) {
/* 2357 */     this.mc.setUseCursorFetch(flag);
/*      */   }
/*      */ 
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 2361 */     this.mc.setUseDirectRowUnpack(flag);
/*      */   }
/*      */ 
/*      */   public void setUseDynamicCharsetInfo(boolean flag) {
/* 2365 */     this.mc.setUseDynamicCharsetInfo(flag);
/*      */   }
/*      */ 
/*      */   public void setUseFastDateParsing(boolean flag) {
/* 2369 */     this.mc.setUseFastDateParsing(flag);
/*      */   }
/*      */ 
/*      */   public void setUseFastIntParsing(boolean flag) {
/* 2373 */     this.mc.setUseFastIntParsing(flag);
/*      */   }
/*      */ 
/*      */   public void setUseGmtMillisForDatetimes(boolean flag) {
/* 2377 */     this.mc.setUseGmtMillisForDatetimes(flag);
/*      */   }
/*      */ 
/*      */   public void setUseHostsInPrivileges(boolean property) {
/* 2381 */     this.mc.setUseHostsInPrivileges(property);
/*      */   }
/*      */ 
/*      */   public void setUseInformationSchema(boolean flag) {
/* 2385 */     this.mc.setUseInformationSchema(flag);
/*      */   }
/*      */ 
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag) {
/* 2389 */     this.mc.setUseJDBCCompliantTimezoneShift(flag);
/*      */   }
/*      */ 
/*      */   public void setUseJvmCharsetConverters(boolean flag) {
/* 2393 */     this.mc.setUseJvmCharsetConverters(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLocalSessionState(boolean flag) {
/* 2397 */     this.mc.setUseLocalSessionState(flag);
/*      */   }
/*      */ 
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 2401 */     this.mc.setUseNanosForElapsedTime(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag) {
/* 2405 */     this.mc.setUseOldAliasMetadataBehavior(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldUTF8Behavior(boolean flag) {
/* 2409 */     this.mc.setUseOldUTF8Behavior(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOnlyServerErrorMessages(boolean flag) {
/* 2413 */     this.mc.setUseOnlyServerErrorMessages(flag);
/*      */   }
/*      */ 
/*      */   public void setUseReadAheadInput(boolean flag) {
/* 2417 */     this.mc.setUseReadAheadInput(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSSL(boolean property) {
/* 2421 */     this.mc.setUseSSL(property);
/*      */   }
/*      */ 
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
/* 2425 */     this.mc.setUseSSPSCompatibleTimezoneShift(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPrepStmts(boolean flag) {
/* 2429 */     this.mc.setUseServerPrepStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPreparedStmts(boolean flag) {
/* 2433 */     this.mc.setUseServerPreparedStmts(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSqlStateCodes(boolean flag) {
/* 2437 */     this.mc.setUseSqlStateCodes(flag);
/*      */   }
/*      */ 
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property) {
/* 2441 */     this.mc.setUseStreamLengthsInPrepStmts(property);
/*      */   }
/*      */ 
/*      */   public void setUseTimezone(boolean property) {
/* 2445 */     this.mc.setUseTimezone(property);
/*      */   }
/*      */ 
/*      */   public void setUseUltraDevWorkAround(boolean property) {
/* 2449 */     this.mc.setUseUltraDevWorkAround(property);
/*      */   }
/*      */ 
/*      */   public void setUseUnbufferedInput(boolean flag) {
/* 2453 */     this.mc.setUseUnbufferedInput(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUnicode(boolean flag) {
/* 2457 */     this.mc.setUseUnicode(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
/* 2461 */     this.mc.setUseUsageAdvisor(useUsageAdvisorFlag);
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 2465 */     this.mc.setUtf8OutsideBmpExcludedColumnNamePattern(regexPattern);
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 2469 */     this.mc.setUtf8OutsideBmpIncludedColumnNamePattern(regexPattern);
/*      */   }
/*      */ 
/*      */   public void setYearIsDateType(boolean flag) {
/* 2473 */     this.mc.setYearIsDateType(flag);
/*      */   }
/*      */ 
/*      */   public void setZeroDateTimeBehavior(String behavior) {
/* 2477 */     this.mc.setZeroDateTimeBehavior(behavior);
/*      */   }
/*      */ 
/*      */   public boolean useUnbufferedInput() {
/* 2481 */     return this.mc.useUnbufferedInput();
/*      */   }
/*      */ 
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 2485 */     this.mc.initializeExtension(ex);
/*      */   }
/*      */ 
/*      */   public String getProfilerEventHandler() {
/* 2489 */     return this.mc.getProfilerEventHandler();
/*      */   }
/*      */ 
/*      */   public void setProfilerEventHandler(String handler) {
/* 2493 */     this.mc.setProfilerEventHandler(handler);
/*      */   }
/*      */ 
/*      */   public boolean getVerifyServerCertificate() {
/* 2497 */     return this.mc.getVerifyServerCertificate();
/*      */   }
/*      */ 
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 2501 */     this.mc.setVerifyServerCertificate(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 2505 */     return this.mc.getUseLegacyDatetimeCode();
/*      */   }
/*      */ 
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 2509 */     this.mc.setUseLegacyDatetimeCode(flag);
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 2513 */     return this.mc.getSelfDestructOnPingMaxOperations();
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 2517 */     return this.mc.getSelfDestructOnPingSecondsLifetime();
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) {
/* 2521 */     this.mc.setSelfDestructOnPingMaxOperations(maxOperations);
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) {
/* 2525 */     this.mc.setSelfDestructOnPingSecondsLifetime(seconds);
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 2529 */     return this.mc.getUseColumnNamesInFindColumn();
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag) {
/* 2533 */     this.mc.setUseColumnNamesInFindColumn(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/* 2537 */     return this.mc.getUseLocalTransactionState();
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag) {
/* 2541 */     this.mc.setUseLocalTransactionState(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts() {
/* 2545 */     return this.mc.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
/* 2549 */     this.mc.setCompensateOnDuplicateKeyUpdateCounts(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows() {
/* 2553 */     return this.mc.getUseAffectedRows();
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag) {
/* 2557 */     this.mc.setUseAffectedRows(flag);
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding() {
/* 2561 */     return this.mc.getPasswordCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 2565 */     this.mc.setPasswordCharacterEncoding(characterSet);
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement() {
/* 2569 */     return this.mc.getAutoIncrementIncrement();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 2573 */     return this.mc.getLoadBalanceBlacklistTimeout();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 2577 */     this.mc.setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/*      */   }
/*      */   public int getLoadBalancePingTimeout() {
/* 2580 */     return this.mc.getLoadBalancePingTimeout();
/*      */   }
/*      */ 
/*      */   public void setLoadBalancePingTimeout(int loadBalancePingTimeout) {
/* 2584 */     this.mc.setLoadBalancePingTimeout(loadBalancePingTimeout);
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceValidateConnectionOnSwapServer() {
/* 2588 */     return this.mc.getLoadBalanceValidateConnectionOnSwapServer();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer)
/*      */   {
/* 2593 */     this.mc.setLoadBalanceValidateConnectionOnSwapServer(loadBalanceValidateConnectionOnSwapServer);
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown) {
/* 2597 */     this.mc.setRetriesAllDown(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown() {
/* 2601 */     return this.mc.getRetriesAllDown();
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2605 */     return this.pooledConnection.getExceptionInterceptor();
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/* 2609 */     return this.mc.getExceptionInterceptors();
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 2613 */     this.mc.setExceptionInterceptors(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 2617 */     return this.mc.getQueryTimeoutKillsConnection();
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection)
/*      */   {
/* 2622 */     this.mc.setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 2626 */     return this.mc.hasSameProperties(c);
/*      */   }
/*      */ 
/*      */   public Properties getProperties() {
/* 2630 */     return this.mc.getProperties();
/*      */   }
/*      */ 
/*      */   public String getHost() {
/* 2634 */     return this.mc.getHost();
/*      */   }
/*      */ 
/*      */   public void setProxy(MySQLConnection conn) {
/* 2638 */     this.mc.setProxy(conn);
/*      */   }
/*      */ 
/*      */   public boolean getRetainStatementAfterResultSetClose() {
/* 2642 */     return this.mc.getRetainStatementAfterResultSetClose();
/*      */   }
/*      */ 
/*      */   public int getMaxAllowedPacket() {
/* 2646 */     return this.mc.getMaxAllowedPacket();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceConnectionGroup() {
/* 2650 */     return this.mc.getLoadBalanceConnectionGroup();
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceEnableJMX() {
/* 2654 */     return this.mc.getLoadBalanceEnableJMX();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceExceptionChecker() {
/* 2658 */     return this.mc.getLoadBalanceExceptionChecker();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLExceptionSubclassFailover()
/*      */   {
/* 2663 */     return this.mc.getLoadBalanceSQLExceptionSubclassFailover();
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLStateFailover()
/*      */   {
/* 2668 */     return this.mc.getLoadBalanceSQLStateFailover();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup)
/*      */   {
/* 2673 */     this.mc.setLoadBalanceConnectionGroup(loadBalanceConnectionGroup);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX)
/*      */   {
/* 2679 */     this.mc.setLoadBalanceEnableJMX(loadBalanceEnableJMX);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker)
/*      */   {
/* 2686 */     this.mc.setLoadBalanceExceptionChecker(loadBalanceExceptionChecker);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover)
/*      */   {
/* 2693 */     this.mc.setLoadBalanceSQLExceptionSubclassFailover(loadBalanceSQLExceptionSubclassFailover);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover)
/*      */   {
/* 2700 */     this.mc.setLoadBalanceSQLStateFailover(loadBalanceSQLStateFailover);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceAutoCommitStatementRegex()
/*      */   {
/* 2707 */     return this.mc.getLoadBalanceAutoCommitStatementRegex();
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceAutoCommitStatementThreshold() {
/* 2711 */     return this.mc.getLoadBalanceAutoCommitStatementThreshold();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex)
/*      */   {
/* 2716 */     this.mc.setLoadBalanceAutoCommitStatementRegex(loadBalanceAutoCommitStatementRegex);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold)
/*      */   {
/* 2722 */     this.mc.setLoadBalanceAutoCommitStatementThreshold(loadBalanceAutoCommitStatementThreshold);
/*      */   }
/*      */ 
/*      */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException
/*      */   {
/* 2727 */     checkClosed();
/*      */     try
/*      */     {
/* 2730 */       this.mc.setTypeMap(map);
/*      */     } catch (SQLException sqlException) {
/* 2732 */       checkAndFireConnectionError(sqlException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadDumpInDeadlockExceptions() {
/* 2737 */     return this.mc.getIncludeThreadDumpInDeadlockExceptions();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
/* 2741 */     this.mc.setIncludeThreadDumpInDeadlockExceptions(flag);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadNamesAsStatementComment()
/*      */   {
/* 2746 */     return this.mc.getIncludeThreadNamesAsStatementComment();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadNamesAsStatementComment(boolean flag) {
/* 2750 */     this.mc.setIncludeThreadNamesAsStatementComment(flag);
/*      */   }
/*      */ 
/*      */   public boolean isServerLocal() throws SQLException {
/* 2754 */     return this.mc.isServerLocal();
/*      */   }
/*      */ 
/*      */   public void setAuthenticationPlugins(String authenticationPlugins) {
/* 2758 */     this.mc.setAuthenticationPlugins(authenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getAuthenticationPlugins() {
/* 2762 */     return this.mc.getAuthenticationPlugins();
/*      */   }
/*      */ 
/*      */   public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins)
/*      */   {
/* 2767 */     this.mc.setDisabledAuthenticationPlugins(disabledAuthenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getDisabledAuthenticationPlugins() {
/* 2771 */     return this.mc.getDisabledAuthenticationPlugins();
/*      */   }
/*      */ 
/*      */   public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin)
/*      */   {
/* 2776 */     this.mc.setDefaultAuthenticationPlugin(defaultAuthenticationPlugin);
/*      */   }
/*      */ 
/*      */   public String getDefaultAuthenticationPlugin()
/*      */   {
/* 2781 */     return this.mc.getDefaultAuthenticationPlugin();
/*      */   }
/*      */ 
/*      */   public void setParseInfoCacheFactory(String factoryClassname) {
/* 2785 */     this.mc.setParseInfoCacheFactory(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getParseInfoCacheFactory() {
/* 2789 */     return this.mc.getParseInfoCacheFactory();
/*      */   }
/*      */ 
/*      */   public void setSchema(String schema) throws SQLException {
/* 2793 */     this.mc.setSchema(schema);
/*      */   }
/*      */ 
/*      */   public String getSchema() throws SQLException {
/* 2797 */     return this.mc.getSchema();
/*      */   }
/*      */ 
/*      */   public void abort(Executor executor) throws SQLException {
/* 2801 */     this.mc.abort(executor);
/*      */   }
/*      */ 
/*      */   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
/*      */   {
/* 2806 */     this.mc.setNetworkTimeout(executor, milliseconds);
/*      */   }
/*      */ 
/*      */   public int getNetworkTimeout() throws SQLException {
/* 2810 */     return this.mc.getNetworkTimeout();
/*      */   }
/*      */ 
/*      */   public void setServerConfigCacheFactory(String factoryClassname) {
/* 2814 */     this.mc.setServerConfigCacheFactory(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getServerConfigCacheFactory() {
/* 2818 */     return this.mc.getServerConfigCacheFactory();
/*      */   }
/*      */ 
/*      */   public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
/* 2822 */     this.mc.setDisconnectOnExpiredPasswords(disconnectOnExpiredPasswords);
/*      */   }
/*      */ 
/*      */   public boolean getDisconnectOnExpiredPasswords() {
/* 2826 */     return this.mc.getDisconnectOnExpiredPasswords();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   80 */     if (Util.isJdbc4())
/*      */       try {
/*   82 */         JDBC_4_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4ConnectionWrapper").getConstructor(new Class[] { MysqlPooledConnection.class, Connection.class, Boolean.TYPE });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   88 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   90 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   92 */         throw new RuntimeException(e);
/*      */       }
/*      */     else
/*   95 */       JDBC_4_CONNECTION_WRAPPER_CTOR = null;
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.ConnectionWrapper
 * JD-Core Version:    0.6.0
 */