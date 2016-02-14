/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import org.jboss.resource.adapter.jdbc.ValidConnectionChecker;
/*    */ 
/*    */ public final class MysqlValidConnectionChecker
/*    */   implements ValidConnectionChecker, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8909421133577519177L;
/*    */ 
/*    */   public SQLException isValidConnection(Connection conn)
/*    */   {
/* 58 */     Statement pingStatement = null;
/*    */     try
/*    */     {
/* 61 */       pingStatement = conn.createStatement();
/*    */ 
/* 63 */       pingStatement.executeQuery("/* ping */ SELECT 1").close();
/*    */ 
/* 65 */       localObject1 = null;
/*    */     }
/*    */     catch (SQLException sqlEx)
/*    */     {
/*    */       Object localObject1;
/* 67 */       return sqlEx;
/*    */     } finally {
/* 69 */       if (pingStatement != null)
/*    */         try {
/* 71 */           pingStatement.close();
/*    */         }
/*    */         catch (SQLException sqlEx)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.integration.jboss.MysqlValidConnectionChecker
 * JD-Core Version:    0.6.0
 */