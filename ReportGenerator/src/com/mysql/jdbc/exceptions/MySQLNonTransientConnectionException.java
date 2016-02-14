/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLNonTransientConnectionException extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = -3050543822763367670L;
/*    */ 
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
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException
 * JD-Core Version:    0.6.0
 */