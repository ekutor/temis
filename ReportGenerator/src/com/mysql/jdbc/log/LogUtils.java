/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ 
/*     */ public class LogUtils
/*     */ {
/*     */   public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
/*  33 */   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*  36 */   private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
/*     */ 
/*     */   public static Object expandProfilerEventIfNecessary(Object possibleProfilerEvent)
/*     */   {
/*  41 */     if ((possibleProfilerEvent instanceof ProfilerEvent)) {
/*  42 */       StringBuffer msgBuf = new StringBuffer();
/*     */ 
/*  44 */       ProfilerEvent evt = (ProfilerEvent)possibleProfilerEvent;
/*     */ 
/*  46 */       String locationInformation = evt.getEventCreationPointAsString();
/*     */ 
/*  48 */       if (locationInformation == null) {
/*  49 */         locationInformation = Util.stackTraceToString(new Throwable());
/*     */       }
/*     */ 
/*  52 */       msgBuf.append("Profiler Event: [");
/*     */ 
/*  54 */       switch (evt.getEventType()) {
/*     */       case 4:
/*  56 */         msgBuf.append("EXECUTE");
/*     */ 
/*  58 */         break;
/*     */       case 5:
/*  61 */         msgBuf.append("FETCH");
/*     */ 
/*  63 */         break;
/*     */       case 1:
/*  66 */         msgBuf.append("CONSTRUCT");
/*     */ 
/*  68 */         break;
/*     */       case 2:
/*  71 */         msgBuf.append("PREPARE");
/*     */ 
/*  73 */         break;
/*     */       case 3:
/*  76 */         msgBuf.append("QUERY");
/*     */ 
/*  78 */         break;
/*     */       case 0:
/*  81 */         msgBuf.append("WARN");
/*     */ 
/*  83 */         break;
/*     */       case 6:
/*  86 */         msgBuf.append("SLOW QUERY");
/*     */ 
/*  88 */         break;
/*     */       default:
/*  91 */         msgBuf.append("UNKNOWN");
/*     */       }
/*     */ 
/*  94 */       msgBuf.append("] ");
/*  95 */       msgBuf.append(locationInformation);
/*  96 */       msgBuf.append(" duration: ");
/*  97 */       msgBuf.append(evt.getEventDuration());
/*  98 */       msgBuf.append(" ");
/*  99 */       msgBuf.append(evt.getDurationUnits());
/* 100 */       msgBuf.append(", connection-id: ");
/* 101 */       msgBuf.append(evt.getConnectionId());
/* 102 */       msgBuf.append(", statement-id: ");
/* 103 */       msgBuf.append(evt.getStatementId());
/* 104 */       msgBuf.append(", resultset-id: ");
/* 105 */       msgBuf.append(evt.getResultSetId());
/*     */ 
/* 107 */       String evtMessage = evt.getMessage();
/*     */ 
/* 109 */       if (evtMessage != null) {
/* 110 */         msgBuf.append(", message: ");
/* 111 */         msgBuf.append(evtMessage);
/*     */       }
/*     */ 
/* 114 */       return msgBuf;
/*     */     }
/*     */ 
/* 117 */     return possibleProfilerEvent;
/*     */   }
/*     */ 
/*     */   public static String findCallingClassAndMethod(Throwable t) {
/* 121 */     String stackTraceAsString = Util.stackTraceToString(t);
/*     */ 
/* 123 */     String callingClassAndMethod = "Caller information not available";
/*     */ 
/* 125 */     int endInternalMethods = stackTraceAsString.lastIndexOf("com.mysql.jdbc");
/*     */ 
/* 128 */     if (endInternalMethods != -1) {
/* 129 */       int endOfLine = -1;
/* 130 */       int compliancePackage = stackTraceAsString.indexOf("com.mysql.jdbc.compliance", endInternalMethods);
/*     */ 
/* 133 */       if (compliancePackage != -1)
/* 134 */         endOfLine = compliancePackage - LINE_SEPARATOR_LENGTH;
/*     */       else {
/* 136 */         endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
/*     */       }
/*     */ 
/* 140 */       if (endOfLine != -1) {
/* 141 */         int nextEndOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endOfLine + LINE_SEPARATOR_LENGTH);
/*     */ 
/* 144 */         if (nextEndOfLine != -1) {
/* 145 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH, nextEndOfLine);
/*     */         }
/*     */         else {
/* 148 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 154 */     if ((!callingClassAndMethod.startsWith("\tat ")) && (!callingClassAndMethod.startsWith("at ")))
/*     */     {
/* 156 */       return "at " + callingClassAndMethod;
/*     */     }
/*     */ 
/* 159 */     return callingClassAndMethod;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.log.LogUtils
 * JD-Core Version:    0.6.0
 */