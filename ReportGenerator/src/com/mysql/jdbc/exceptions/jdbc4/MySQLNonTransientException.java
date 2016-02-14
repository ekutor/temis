/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLNonTransientException;
/*    */ 
/*    */ public class MySQLNonTransientException extends SQLNonTransientException
/*    */ {
/*    */   public MySQLNonTransientException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientException
 * JD-Core Version:    0.6.0
 */