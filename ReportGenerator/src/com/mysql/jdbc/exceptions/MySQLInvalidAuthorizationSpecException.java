/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ public class MySQLInvalidAuthorizationSpecException extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = 6878889837492500030L;
/*    */ 
/*    */   public MySQLInvalidAuthorizationSpecException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLInvalidAuthorizationSpecException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLInvalidAuthorizationSpecException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLInvalidAuthorizationSpecException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLInvalidAuthorizationSpecException
 * JD-Core Version:    0.6.0
 */