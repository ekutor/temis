/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter;
/*    */ 
/*    */ public final class ExtendedMysqlExceptionSorter extends MySQLExceptionSorter
/*    */ {
/*    */   static final long serialVersionUID = -2454582336945931069L;
/*    */ 
/*    */   public boolean isExceptionFatal(SQLException ex)
/*    */   {
/* 47 */     String sqlState = ex.getSQLState();
/*    */ 
/* 49 */     if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 50 */       return true;
/*    */     }
/*    */ 
/* 53 */     return super.isExceptionFatal(ex);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.integration.jboss.ExtendedMysqlExceptionSorter
 * JD-Core Version:    0.6.0
 */