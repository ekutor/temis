/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLDataException;
/*    */ 
/*    */ public class MySQLDataException extends SQLDataException
/*    */ {
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
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLDataException
 * JD-Core Version:    0.6.0
 */