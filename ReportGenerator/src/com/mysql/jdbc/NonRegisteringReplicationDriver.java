/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class NonRegisteringReplicationDriver extends NonRegisteringDriver
/*    */ {
/*    */   public NonRegisteringReplicationDriver()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   public Connection connect(String url, Properties info)
/*    */     throws SQLException
/*    */   {
/* 51 */     return connectReplicationConnection(url, info);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NonRegisteringReplicationDriver
 * JD-Core Version:    0.6.0
 */