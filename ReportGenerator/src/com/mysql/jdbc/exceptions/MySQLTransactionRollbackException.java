/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLTransactionRollbackException extends MySQLTransientException
/*    */   implements DeadlockTimeoutRollbackMarker
/*    */ {
/*    */   static final long serialVersionUID = 6034999468737801730L;
/*    */ 
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 33 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState) {
/* 37 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransactionRollbackException(String reason) {
/* 41 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransactionRollbackException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLTransactionRollbackException
 * JD-Core Version:    0.6.0
 */