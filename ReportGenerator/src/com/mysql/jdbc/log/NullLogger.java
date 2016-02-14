/*    */ package com.mysql.jdbc.log;
/*    */ 
/*    */ public class NullLogger
/*    */   implements Log
/*    */ {
/*    */   public NullLogger(String instanceName)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean isDebugEnabled()
/*    */   {
/* 52 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isErrorEnabled()
/*    */   {
/* 60 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isFatalEnabled()
/*    */   {
/* 68 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isInfoEnabled()
/*    */   {
/* 76 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isTraceEnabled()
/*    */   {
/* 84 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isWarnEnabled()
/*    */   {
/* 92 */     return false;
/*    */   }
/*    */ 
/*    */   public void logDebug(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logDebug(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logError(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logError(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logFatal(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logFatal(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logInfo(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logInfo(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logTrace(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logTrace(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logWarn(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logWarn(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.log.NullLogger
 * JD-Core Version:    0.6.0
 */