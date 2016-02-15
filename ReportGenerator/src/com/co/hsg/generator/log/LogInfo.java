 package com.co.hsg.generator.log;
 
 import com.co.hsg.generator.util.Util;
 
 public class LogInfo extends Throwable
 {
   private LogControl lc;
   private static final long serialVersionUID = 102603153538L;
 
   public LogInfo(String mensaje, LogType tipo)
   {
     super(mensaje);
     this.lc = LogControl.getLog();
     mensaje = makeFormat(mensaje);
 
     switch (tipo) {
     case Debug:
       this.lc.addDebug(mensaje);
       break;
     case Error:
       this.lc.addError(mensaje);
       break;
     case Info:
       this.lc.addInfo(mensaje);
       break;
     case Trace:
       this.lc.addTrace(mensaje);
       break;
     case Warning:
       this.lc.addWarn(mensaje);
     case Fatal:
     }
   }
 
   public LogInfo(String mensaje, LogType tipo, Class clase)
   {
     this(clase + "->" + mensaje, tipo);
   }
 
   private String makeFormat(String mensaje) {
     String finalMessage = " ";
 
     finalMessage = finalMessage + mensaje;
     return finalMessage;
   }
 
   private String formatter(int length)
   {
     String format = "*";
     String finalMessage = "";
     for (int i = 0; i < 5; i++) {
       finalMessage = finalMessage + format;
     }
     return finalMessage;
   }
 
   public static void T(String mensaje)
   {
     new LogInfo(mensaje, LogType.Trace);
   }
 
   public static void E(String mensaje) {
     new LogInfo(mensaje, LogType.Error);
   }
 
   public static void E(String mensaje, Exception e) {
     new LogInfo(mensaje + " " + Util.errorToString(e), LogType.Error);
   }
 }