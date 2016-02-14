/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class NdbLoadBalanceExceptionChecker extends StandardLoadBalanceExceptionChecker
/*    */ {
/*    */   public boolean shouldExceptionTriggerFailover(SQLException ex)
/*    */   {
/*  9 */     return (super.shouldExceptionTriggerFailover(ex)) || (checkNdbException(ex));
/*    */   }
/*    */ 
/*    */   private boolean checkNdbException(SQLException ex)
/*    */   {
/* 14 */     return (ex.getMessage().startsWith("Lock wait timeout exceeded")) || ((ex.getMessage().startsWith("Got temporary error")) && (ex.getMessage().endsWith("from NDB")));
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NdbLoadBalanceExceptionChecker
 * JD-Core Version:    0.6.0
 */