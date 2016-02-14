/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class MySQLNonTransientException extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = -8714521137552613517L;
/*    */ 
/*    */   public MySQLNonTransientException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 38 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason, String SQLState) {
/* 42 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLNonTransientException(String reason) {
/* 46 */     super(reason);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLNonTransientException
 * JD-Core Version:    0.6.0
 */