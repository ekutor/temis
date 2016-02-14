/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import com.mysql.jdbc.MySQLConnection;
/*    */ import com.mysql.jdbc.SQLError;
/*    */ import com.mysql.jdbc.StreamingNotifiable;
/*    */ import java.sql.SQLRecoverableException;
/*    */ 
/*    */ public class CommunicationsException extends SQLRecoverableException
/*    */   implements StreamingNotifiable
/*    */ {
/*    */   private String exceptionMessage;
/* 52 */   private boolean streamingResultSetInPlay = false;
/*    */ 
/*    */   public CommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException)
/*    */   {
/* 58 */     this.exceptionMessage = SQLError.createLinkFailureMessageBasedOnHeuristics(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException, this.streamingResultSetInPlay);
/*    */ 
/* 61 */     if (underlyingException != null)
/* 62 */       initCause(underlyingException);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 72 */     return this.exceptionMessage;
/*    */   }
/*    */ 
/*    */   public String getSQLState()
/*    */   {
/* 81 */     return "08S01";
/*    */   }
/*    */ 
/*    */   public void setWasStreamingResults() {
/* 85 */     this.streamingResultSetInPlay = true;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.exceptions.jdbc4.CommunicationsException
 * JD-Core Version:    0.6.0
 */