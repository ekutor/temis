/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLTimeoutException extends MySQLTransientException
/*    */ {
/*    */   static final long serialVersionUID = -789621240523230339L;
/*    */ 
/*    */   public MySQLTimeoutException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 32 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTimeoutException(String reason, String SQLState) {
/* 36 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTimeoutException(String reason) {
/* 40 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTimeoutException() {
/* 44 */     super("Statement cancelled due to timeout or client request");
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLTimeoutException
 * JD-Core Version:    0.6.0
 */