/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.NClob;
/*    */ 
/*    */ public class JDBC4NClob extends Clob
/*    */   implements NClob
/*    */ {
/*    */   JDBC4NClob(ExceptionInterceptor exceptionInterceptor)
/*    */   {
/* 39 */     super(exceptionInterceptor);
/*    */   }
/*    */ 
/*    */   JDBC4NClob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
/* 43 */     super(charDataInit, exceptionInterceptor);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4NClob
 * JD-Core Version:    0.6.0
 */