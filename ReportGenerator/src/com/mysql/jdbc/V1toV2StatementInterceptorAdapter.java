/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class V1toV2StatementInterceptorAdapter
/*    */   implements StatementInterceptorV2
/*    */ {
/*    */   private final StatementInterceptor toProxy;
/*    */ 
/*    */   public V1toV2StatementInterceptorAdapter(StatementInterceptor toProxy)
/*    */   {
/* 29 */     this.toProxy = toProxy;
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException)
/*    */     throws SQLException
/*    */   {
/* 36 */     return this.toProxy.postProcess(sql, interceptedStatement, originalResultSet, connection);
/*    */   }
/*    */ 
/*    */   public void destroy() {
/* 40 */     this.toProxy.destroy();
/*    */   }
/*    */ 
/*    */   public boolean executeTopLevelOnly() {
/* 44 */     return this.toProxy.executeTopLevelOnly();
/*    */   }
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 48 */     this.toProxy.init(conn, props);
/*    */   }
/*    */ 
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 54 */     return this.toProxy.preProcess(sql, interceptedStatement, connection);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.V1toV2StatementInterceptorAdapter
 * JD-Core Version:    0.6.0
 */