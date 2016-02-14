/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTransientException;
/*    */ 
/*    */ public class MySQLTransientException extends SQLTransientException
/*    */ {
/*    */   public MySQLTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 33 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason, String SQLState) {
/* 37 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason) {
/* 41 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTransientException
 * JD-Core Version:    0.6.0
 */