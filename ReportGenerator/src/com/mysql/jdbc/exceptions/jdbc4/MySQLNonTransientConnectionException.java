/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLNonTransientConnectionException;
/*    */ 
/*    */ public class MySQLNonTransientConnectionException extends SQLNonTransientConnectionException
/*    */ {
/*    */   public MySQLNonTransientConnectionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientConnectionException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException
 * JD-Core Version:    0.6.0
 */