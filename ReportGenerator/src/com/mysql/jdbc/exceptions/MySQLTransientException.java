/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class MySQLTransientException extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = -1885878228558607563L;
/*    */ 
/*    */   public MySQLTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 34 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason, String SQLState) {
/* 38 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException(String reason) {
/* 42 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransientException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.MySQLTransientException
 * JD-Core Version:    0.6.0
 */