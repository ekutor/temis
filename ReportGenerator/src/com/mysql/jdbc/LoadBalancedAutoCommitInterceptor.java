/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class LoadBalancedAutoCommitInterceptor
/*     */   implements StatementInterceptorV2
/*     */ {
/*  38 */   private int matchingAfterStatementCount = 0;
/*  39 */   private int matchingAfterStatementThreshold = 0;
/*     */   private String matchingAfterStatementRegex;
/*     */   private ConnectionImpl conn;
/*  42 */   private LoadBalancingConnectionProxy proxy = null;
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean executeTopLevelOnly()
/*     */   {
/*  55 */     return false;
/*     */   }
/*     */ 
/*     */   public void init(Connection connection, Properties props) throws SQLException {
/*  59 */     this.conn = ((ConnectionImpl)connection);
/*     */ 
/*  61 */     String autoCommitSwapThresholdAsString = props.getProperty("loadBalanceAutoCommitStatementThreshold", "0");
/*     */     try
/*     */     {
/*  64 */       this.matchingAfterStatementThreshold = Integer.parseInt(autoCommitSwapThresholdAsString);
/*     */     }
/*     */     catch (NumberFormatException nfe) {
/*     */     }
/*  68 */     String autoCommitSwapRegex = props.getProperty("loadBalanceAutoCommitStatementRegex", "");
/*  69 */     if ("".equals(autoCommitSwapRegex)) {
/*  70 */       return;
/*     */     }
/*  72 */     this.matchingAfterStatementRegex = autoCommitSwapRegex;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException)
/*     */     throws SQLException
/*     */   {
/*  86 */     if (!this.conn.getAutoCommit()) {
/*  87 */       this.matchingAfterStatementCount = 0;
/*     */     }
/*     */     else
/*     */     {
/*  91 */       if ((this.proxy == null) && (this.conn.isProxySet())) {
/*  92 */         MySQLConnection lcl_proxy = this.conn.getLoadBalanceSafeProxy();
/*  93 */         while ((lcl_proxy != null) && (!(lcl_proxy instanceof LoadBalancedMySQLConnection))) {
/*  94 */           lcl_proxy = lcl_proxy.getLoadBalanceSafeProxy();
/*     */         }
/*  96 */         if (lcl_proxy != null) {
/*  97 */           this.proxy = ((LoadBalancedMySQLConnection)lcl_proxy).getProxy();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 102 */       if (this.proxy != null)
/*     */       {
/* 104 */         if ((this.matchingAfterStatementRegex == null) || (sql.matches(this.matchingAfterStatementRegex)))
/*     */         {
/* 106 */           this.matchingAfterStatementCount += 1;
/*     */         }
/*     */       }
/*     */ 
/* 110 */       if (this.matchingAfterStatementCount >= this.matchingAfterStatementThreshold) {
/* 111 */         this.matchingAfterStatementCount = 0;
/*     */         try {
/* 113 */           if (this.proxy != null) {
/* 114 */             this.proxy.pickNewConnection();
/*     */           }
/*     */         }
/*     */         catch (SQLException e)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 125 */     return originalResultSet;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*     */     throws SQLException
/*     */   {
/* 133 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.LoadBalancedAutoCommitInterceptor
 * JD-Core Version:    0.6.0
 */