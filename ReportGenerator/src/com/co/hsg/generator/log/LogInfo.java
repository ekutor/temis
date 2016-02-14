/*    */ package com.co.hsg.generator.log;
/*    */ 
/*    */ import com.co.hsg.generator.util.Util;
/*    */ 
/*    */ public class LogInfo extends Throwable
/*    */ {
/*    */   private LogControl lc;
/*    */   private static final long serialVersionUID = 102603153538L;
/*    */ 
/*    */   public LogInfo(String mensaje, LogType tipo)
/*    */   {
/* 13 */     super(mensaje);
/* 14 */     this.lc = LogControl.getLog();
/* 15 */     mensaje = makeFormat(mensaje);
/*    */ 
/* 17 */     switch (tipo) {
/*    */     case Debug:
/* 19 */       this.lc.addTrace(mensaje);
/* 20 */       break;
/*    */     case Error:
/* 23 */       this.lc.addDebug(mensaje);
/* 24 */       break;
/*    */     case Info:
/* 27 */       this.lc.addWarn(mensaje);
/* 28 */       break;
/*    */     case Trace:
/* 31 */       this.lc.addError(mensaje);
/* 32 */       break;
/*    */     case Warning:
/* 35 */       this.lc.addFatal(mensaje);
/*    */     case Fatal:
/*    */     }
/*    */   }
/*    */ 
/*    */   public LogInfo(String mensaje, LogType tipo, Class clase)
/*    */   {
/* 47 */     this(clase + "->" + mensaje, tipo);
/*    */   }
/*    */ 
/*    */   private String makeFormat(String mensaje) {
/* 51 */     String finalMessage = " ";
/*    */ 
/* 53 */     finalMessage = finalMessage + mensaje;
/* 54 */     return finalMessage;
/*    */   }
/*    */ 
/*    */   private String formatter(int length)
/*    */   {
/* 59 */     String format = "*";
/* 60 */     String finalMessage = "";
/* 61 */     for (int i = 0; i < 5; i++) {
/* 62 */       finalMessage = finalMessage + format;
/*    */     }
/* 64 */     return finalMessage;
/*    */   }
/*    */ 
/*    */   public static void T(String mensaje)
/*    */   {
/* 69 */     new LogInfo(mensaje, LogType.Trace);
/*    */   }
/*    */ 
/*    */   public static void E(String mensaje) {
/* 73 */     new LogInfo(mensaje, LogType.Error);
/*    */   }
/*    */ 
/*    */   public static void E(String mensaje, Exception e) {
/* 77 */     new LogInfo(mensaje + " " + Util.errorToString(e), LogType.Error);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.log.LogInfo
 * JD-Core Version:    0.6.0
 */