/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ class OperationNotSupportedException extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = 474918612056813430L;
/*    */ 
/*    */   OperationNotSupportedException()
/*    */   {
/* 31 */     super(Messages.getString("RowDataDynamic.10"), "S1009");
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.OperationNotSupportedException
 * JD-Core Version:    0.6.0
 */