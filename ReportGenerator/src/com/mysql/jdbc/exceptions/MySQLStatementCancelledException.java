/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLStatementCancelledException extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = -8762717748377197378L;
/*    */ 
/*    */   public MySQLStatementCancelledException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 32 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLStatementCancelledException(String reason, String SQLState) {
/* 36 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLStatementCancelledException(String reason) {
/* 40 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLStatementCancelledException() {
/* 44 */     super("Statement cancelled due to client request");
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLStatementCancelledException
 * JD-Core Version:    0.6.0
 */