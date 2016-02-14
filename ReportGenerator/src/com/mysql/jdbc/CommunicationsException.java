/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class CommunicationsException extends SQLException
/*     */   implements StreamingNotifiable
/*     */ {
/*     */   static final long serialVersionUID = 3193864990663398317L;
/*  46 */   private String exceptionMessage = null;
/*     */ 
/*  48 */   private boolean streamingResultSetInPlay = false;
/*     */   private MySQLConnection conn;
/*     */   private long lastPacketSentTimeMs;
/*     */   private long lastPacketReceivedTimeMs;
/*     */   private Exception underlyingException;
/*     */ 
/*     */   public CommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException)
/*     */   {
/*  59 */     this.conn = conn;
/*  60 */     this.lastPacketReceivedTimeMs = lastPacketReceivedTimeMs;
/*  61 */     this.lastPacketSentTimeMs = lastPacketSentTimeMs;
/*  62 */     this.underlyingException = underlyingException;
/*     */ 
/*  64 */     if (underlyingException != null)
/*  65 */       initCause(underlyingException);
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/*  79 */     if (this.exceptionMessage == null) {
/*  80 */       this.exceptionMessage = SQLError.createLinkFailureMessageBasedOnHeuristics(this.conn, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, this.underlyingException, this.streamingResultSetInPlay);
/*     */ 
/*  83 */       this.conn = null;
/*  84 */       this.underlyingException = null;
/*     */     }
/*  86 */     return this.exceptionMessage;
/*     */   }
/*     */ 
/*     */   public String getSQLState()
/*     */   {
/*  95 */     return "08S01";
/*     */   }
/*     */ 
/*     */   public void setWasStreamingResults()
/*     */   {
/* 102 */     this.streamingResultSetInPlay = true;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.CommunicationsException
 * JD-Core Version:    0.6.0
 */