/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import com.mysql.jdbc.exceptions.DeadlockTimeoutRollbackMarker;
/*    */ import java.sql.SQLTransactionRollbackException;
/*    */ 
/*    */ public class MySQLTransactionRollbackException extends SQLTransactionRollbackException
/*    */   implements DeadlockTimeoutRollbackMarker
/*    */ {
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 35 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */ 
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState) {
/* 39 */     super(reason, SQLState);
/*    */   }
/*    */ 
/*    */   public MySQLTransactionRollbackException(String reason) {
/* 43 */     super(reason);
/*    */   }
/*    */ 
/*    */   public MySQLTransactionRollbackException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException
 * JD-Core Version:    0.6.0
 */