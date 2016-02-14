/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLDataException extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = 4317904269797988676L;
/*    */ 
/*    */   public MySQLDataException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 36 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason, String SQLState) {
/* 40 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLDataException(String reason) {
/* 44 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLDataException
 * JD-Core Version:    0.6.0
 */