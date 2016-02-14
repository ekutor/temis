/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTimeoutException;
/*    */ 
/*    */ public class MySQLTimeoutException extends SQLTimeoutException
/*    */ {
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
/*    */ 
/*    */   public int getErrorCode()
/*    */   {
/* 49 */     return super.getErrorCode();
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTimeoutException
 * JD-Core Version:    0.6.0
 */