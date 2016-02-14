/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.DataTruncation;
/*    */ 
/*    */ public class MysqlDataTruncation extends DataTruncation
/*    */ {
/*    */   static final long serialVersionUID = 3263928195256986226L;
/*    */   private String message;
/*    */   private int vendorErrorCode;
/*    */ 
/*    */   public MysqlDataTruncation(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize, int vendorErrorCode)
/*    */   {
/* 65 */     super(index, parameter, read, dataSize, transferSize);
/*    */ 
/* 67 */     this.message = message;
/* 68 */     this.vendorErrorCode = vendorErrorCode;
/*    */   }
/*    */ 
/*    */   public int getErrorCode() {
/* 72 */     return this.vendorErrorCode;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 81 */     return super.getMessage() + ": " + this.message;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.MysqlDataTruncation
 * JD-Core Version:    0.6.0
 */