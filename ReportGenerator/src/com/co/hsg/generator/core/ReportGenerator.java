/*    */ package com.co.hsg.generator.core;
/*    */ 
/*    */ import com.co.hsg.generator.log.LogInfo;
/*    */ 
/*    */ public class ReportGenerator
/*    */ {
/*    */   public void execute(String id, String typeReport)
/*    */   {
/*  9 */     LogInfo.T("[ReportGenerator] Iniciando Proceso..");
/* 10 */     id = id.trim();
/* 11 */     typeReport = typeReport.trim();
/*    */ 
/* 13 */     ReportManager rpm = new ReportManager();
/*    */ 
/* 15 */     LogInfo.T("[ReportGenerator] Generando Reporte:");
/* 16 */     if (rpm.generateReports(id, typeReport))
/* 17 */       LogInfo.T("[ReportGenerator] Reporte Generado Exitosamente");
/*    */     else
/* 19 */       LogInfo.T("[ReportGenerator] Fallo: Reporte NO Generado");
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 24 */     ReportGenerator rm = new ReportGenerator();
/* 25 */     LogInfo.T("[ReportGenerator] INICIANDO OPERACION");
/* 26 */     rm.execute(args[0], args[1]);
/* 27 */     LogInfo.T("[ReportGenerator] OPERACION FINALIZADA");
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.core.ReportGenerator
 * JD-Core Version:    0.6.0
 */