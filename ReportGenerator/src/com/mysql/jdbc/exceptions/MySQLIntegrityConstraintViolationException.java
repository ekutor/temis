/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLIntegrityConstraintViolationException extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = -5528363270635808904L;
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLIntegrityConstraintViolationException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException
 * JD-Core Version:    0.6.0
 */