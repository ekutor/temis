/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.ConnectionPoolDataSource;
/*    */ import javax.sql.PooledConnection;
/*    */ 
/*    */ public class MysqlConnectionPoolDataSource extends MysqlDataSource
/*    */   implements ConnectionPoolDataSource
/*    */ {
/*    */   static final long serialVersionUID = -7767325445592304961L;
/*    */ 
/*    */   public synchronized PooledConnection getPooledConnection()
/*    */     throws SQLException
/*    */   {
/* 62 */     java.sql.Connection connection = getConnection();
/* 63 */     MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((com.mysql.jdbc.Connection)connection);
/*    */ 
/* 66 */     return mysqlPooledConnection;
/*    */   }
/*    */ 
/*    */   public synchronized PooledConnection getPooledConnection(String s, String s1)
/*    */     throws SQLException
/*    */   {
/* 83 */     java.sql.Connection connection = getConnection(s, s1);
/* 84 */     MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((com.mysql.jdbc.Connection)connection);
/*    */ 
/* 87 */     return mysqlPooledConnection;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
 * JD-Core Version:    0.6.0
 */