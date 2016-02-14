/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLSyntaxErrorException extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = 6919059513432113764L;
/*    */ 
/*    */   public MySQLSyntaxErrorException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 36 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState) {
/* 40 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason) {
/* 44 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLSyntaxErrorException
 * JD-Core Version:    0.6.0
 */