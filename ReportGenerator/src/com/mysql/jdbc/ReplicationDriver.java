/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Driver;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class ReplicationDriver extends NonRegisteringReplicationDriver
/*    */   implements Driver
/*    */ {
/*    */   public ReplicationDriver()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 65 */       DriverManager.registerDriver(new NonRegisteringReplicationDriver());
/*    */     }
/*    */     catch (SQLException E) {
/* 68 */       throw new RuntimeException("Can't register driver!");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ReplicationDriver
 * JD-Core Version:    0.6.0
 */