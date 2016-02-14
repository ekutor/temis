/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class Driver extends NonRegisteringDriver
/*    */   implements java.sql.Driver
/*    */ {
/*    */   public Driver()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 63 */       DriverManager.registerDriver(new Driver());
/*    */     } catch (SQLException E) {
/* 65 */       throw new RuntimeException("Can't register driver!");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Driver
 * JD-Core Version:    0.6.0
 */