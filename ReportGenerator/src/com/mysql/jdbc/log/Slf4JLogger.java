/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Slf4JLogger
/*     */   implements Log
/*     */ {
/*     */   private Logger log;
/*     */ 
/*     */   public Slf4JLogger(String name)
/*     */   {
/*  36 */     this.log = LoggerFactory.getLogger(name);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled() {
/*  40 */     return this.log.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled() {
/*  44 */     return this.log.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled() {
/*  48 */     return this.log.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled() {
/*  52 */     return this.log.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled() {
/*  56 */     return this.log.isTraceEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled() {
/*  60 */     return this.log.isWarnEnabled();
/*     */   }
/*     */ 
/*     */   public void logDebug(Object msg) {
/*  64 */     this.log.debug(msg.toString());
/*     */   }
/*     */ 
/*     */   public void logDebug(Object msg, Throwable thrown) {
/*  68 */     this.log.debug(msg.toString(), thrown);
/*     */   }
/*     */ 
/*     */   public void logError(Object msg) {
/*  72 */     this.log.error(msg.toString());
/*     */   }
/*     */ 
/*     */   public void logError(Object msg, Throwable thrown) {
/*  76 */     this.log.error(msg.toString(), thrown);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object msg) {
/*  80 */     this.log.error(msg.toString());
/*     */   }
/*     */ 
/*     */   public void logFatal(Object msg, Throwable thrown) {
/*  84 */     this.log.error(msg.toString(), thrown);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object msg) {
/*  88 */     this.log.info(msg.toString());
/*     */   }
/*     */ 
/*     */   public void logInfo(Object msg, Throwable thrown) {
/*  92 */     this.log.info(msg.toString(), thrown);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object msg) {
/*  96 */     this.log.trace(msg.toString());
/*     */   }
/*     */ 
/*     */   public void logTrace(Object msg, Throwable thrown) {
/* 100 */     this.log.trace(msg.toString(), thrown);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object msg) {
/* 104 */     this.log.warn(msg.toString());
/*     */   }
/*     */ 
/*     */   public void logWarn(Object msg, Throwable thrown) {
/* 108 */     this.log.warn(msg.toString(), thrown);
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.log.Slf4JLogger
 * JD-Core Version:    0.6.0
 */