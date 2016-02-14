/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class StandardLogger
/*     */   implements Log
/*     */ {
/*     */   private static final int FATAL = 0;
/*     */   private static final int ERROR = 1;
/*     */   private static final int WARN = 2;
/*     */   private static final int INFO = 3;
/*     */   private static final int DEBUG = 4;
/*     */   private static final int TRACE = 5;
/*  54 */   public static StringBuffer bufferedLog = null;
/*     */ 
/*  56 */   private boolean logLocationInfo = true;
/*     */ 
/*     */   public StandardLogger(String name)
/*     */   {
/*  65 */     this(name, false);
/*     */   }
/*     */ 
/*     */   public StandardLogger(String name, boolean logLocationInfo)
/*     */   {
/*  74 */     this.logLocationInfo = logLocationInfo;
/*     */   }
/*     */ 
/*     */   public static void saveLogsToBuffer() {
/*  78 */     if (bufferedLog == null)
/*  79 */       bufferedLog = new StringBuffer();
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 101 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 108 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 115 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 122 */     return true;
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message)
/*     */   {
/* 132 */     logInternal(4, message, null);
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message, Throwable exception)
/*     */   {
/* 144 */     logInternal(4, message, exception);
/*     */   }
/*     */ 
/*     */   public void logError(Object message)
/*     */   {
/* 154 */     logInternal(1, message, null);
/*     */   }
/*     */ 
/*     */   public void logError(Object message, Throwable exception)
/*     */   {
/* 166 */     logInternal(1, message, exception);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message)
/*     */   {
/* 176 */     logInternal(0, message, null);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message, Throwable exception)
/*     */   {
/* 188 */     logInternal(0, message, exception);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message)
/*     */   {
/* 198 */     logInternal(3, message, null);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message, Throwable exception)
/*     */   {
/* 210 */     logInternal(3, message, exception);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message)
/*     */   {
/* 220 */     logInternal(5, message, null);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message, Throwable exception)
/*     */   {
/* 232 */     logInternal(5, message, exception);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message)
/*     */   {
/* 242 */     logInternal(2, message, null);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message, Throwable exception)
/*     */   {
/* 254 */     logInternal(2, message, exception);
/*     */   }
/*     */ 
/*     */   protected void logInternal(int level, Object msg, Throwable exception) {
/* 258 */     StringBuffer msgBuf = new StringBuffer();
/* 259 */     msgBuf.append(new Date().toString());
/* 260 */     msgBuf.append(" ");
/*     */ 
/* 262 */     switch (level) {
/*     */     case 0:
/* 264 */       msgBuf.append("FATAL: ");
/*     */ 
/* 266 */       break;
/*     */     case 1:
/* 269 */       msgBuf.append("ERROR: ");
/*     */ 
/* 271 */       break;
/*     */     case 2:
/* 274 */       msgBuf.append("WARN: ");
/*     */ 
/* 276 */       break;
/*     */     case 3:
/* 279 */       msgBuf.append("INFO: ");
/*     */ 
/* 281 */       break;
/*     */     case 4:
/* 284 */       msgBuf.append("DEBUG: ");
/*     */ 
/* 286 */       break;
/*     */     case 5:
/* 289 */       msgBuf.append("TRACE: ");
/*     */     }
/*     */ 
/* 294 */     if ((msg instanceof ProfilerEvent)) {
/* 295 */       msgBuf.append(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */     }
/*     */     else {
/* 298 */       if ((this.logLocationInfo) && (level != 5)) {
/* 299 */         Throwable locationException = new Throwable();
/* 300 */         msgBuf.append(LogUtils.findCallingClassAndMethod(locationException));
/*     */ 
/* 302 */         msgBuf.append(" ");
/*     */       }
/*     */ 
/* 305 */       if (msg != null) {
/* 306 */         msgBuf.append(String.valueOf(msg));
/*     */       }
/*     */     }
/*     */ 
/* 310 */     if (exception != null) {
/* 311 */       msgBuf.append("\n");
/* 312 */       msgBuf.append("\n");
/* 313 */       msgBuf.append("EXCEPTION STACK TRACE:");
/* 314 */       msgBuf.append("\n");
/* 315 */       msgBuf.append("\n");
/* 316 */       msgBuf.append(Util.stackTraceToString(exception));
/*     */     }
/*     */ 
/* 319 */     String messageAsString = msgBuf.toString();
/*     */ 
/* 321 */     System.err.println(messageAsString);
/*     */ 
/* 323 */     if (bufferedLog != null)
/* 324 */       bufferedLog.append(messageAsString);
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.log.StandardLogger
 * JD-Core Version:    0.6.0
 */