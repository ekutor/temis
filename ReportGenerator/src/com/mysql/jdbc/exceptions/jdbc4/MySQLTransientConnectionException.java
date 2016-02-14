/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTransientConnectionException;
/*    */ 
/*    */ public class MySQLTransientConnectionException extends SQLTransientConnectionException
/*    */ {
/*    */   public MySQLTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 33 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException(String reason, String SQLState) {
/* 37 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException(String reason) {
/* 41 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientConnectionException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException
 * JD-Core Version:    0.6.0
 */