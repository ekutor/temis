/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.sql.StatementEvent;
/*    */ import javax.sql.StatementEventListener;
/*    */ 
/*    */ public class JDBC4MysqlXAConnection extends MysqlXAConnection
/*    */ {
/*    */   private Map<StatementEventListener, StatementEventListener> statementEventListeners;
/*    */ 
/*    */   public JDBC4MysqlXAConnection(ConnectionImpl connection, boolean logXaCommands)
/*    */     throws SQLException
/*    */   {
/* 43 */     super(connection, logXaCommands);
/*    */ 
/* 45 */     this.statementEventListeners = new HashMap();
/*    */   }
/*    */ 
/*    */   public synchronized void close() throws SQLException {
/* 49 */     super.close();
/*    */ 
/* 51 */     if (this.statementEventListeners != null) {
/* 52 */       this.statementEventListeners.clear();
/*    */ 
/* 54 */       this.statementEventListeners = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void addStatementEventListener(StatementEventListener listener)
/*    */   {
/* 71 */     synchronized (this.statementEventListeners) {
/* 72 */       this.statementEventListeners.put(listener, listener);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void removeStatementEventListener(StatementEventListener listener)
/*    */   {
/* 88 */     synchronized (this.statementEventListeners) {
/* 89 */       this.statementEventListeners.remove(listener);
/*    */     }
/*    */   }
/*    */ 
/*    */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 94 */     synchronized (this.statementEventListeners) {
/* 95 */       for (StatementEventListener listener : this.statementEventListeners.keySet())
/* 96 */         listener.statementClosed(event);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection
 * JD-Core Version:    0.6.0
 */