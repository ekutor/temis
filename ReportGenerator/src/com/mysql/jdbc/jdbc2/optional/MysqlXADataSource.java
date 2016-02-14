/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.XAConnection;
/*    */ import javax.sql.XADataSource;
/*    */ 
/*    */ public class MysqlXADataSource extends MysqlDataSource
/*    */   implements XADataSource
/*    */ {
/*    */   static final long serialVersionUID = 7911390333152247455L;
/*    */ 
/*    */   public XAConnection getXAConnection()
/*    */     throws SQLException
/*    */   {
/* 50 */     java.sql.Connection conn = getConnection();
/*    */ 
/* 52 */     return wrapConnection(conn);
/*    */   }
/*    */ 
/*    */   public XAConnection getXAConnection(String u, String p)
/*    */     throws SQLException
/*    */   {
/* 61 */     java.sql.Connection conn = getConnection(u, p);
/*    */ 
/* 63 */     return wrapConnection(conn);
/*    */   }
/*    */ 
/*    */   private XAConnection wrapConnection(java.sql.Connection conn)
/*    */     throws SQLException
/*    */   {
/* 71 */     if ((getPinGlobalTxToPhysicalConnection()) || (((com.mysql.jdbc.Connection)conn).getPinGlobalTxToPhysicalConnection()))
/*    */     {
/* 73 */       return SuspendableXAConnection.getInstance((ConnectionImpl)conn);
/*    */     }
/*    */ 
/* 76 */     return MysqlXAConnection.getInstance((ConnectionImpl)conn, getLogXaCommands());
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlXADataSource
 * JD-Core Version:    0.6.0
 */