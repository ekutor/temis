 package com.co.hsg.generator.core;
 
 import com.co.hsg.generator.log.LogInfo;
 
 public class ReportGenerator
 {
   public void execute(String id, String typeReport)
   {
     LogInfo.T("[ReportGenerator] Iniciando Proceso..");
     id = id.trim();
     typeReport = typeReport.trim();
 
     ReportManager rpm = new ReportManager();
 
     LogInfo.T("[ReportGenerator] Generando Reporte:");
     if (rpm.generateReports(id, typeReport))
       LogInfo.T("[ReportGenerator] Reporte Generado Exitosamente");
     else
       LogInfo.T("[ReportGenerator] Fallo: Reporte NO Generado");
   }
 
   public static void main(String[] args)
   {
    ReportGenerator rm = new ReportGenerator();
    LogInfo.T("[ReportGenerator] INICIANDO OPERACION");
    rm.execute(args[0], args[1]);
    LogInfo.T("[ReportGenerator] OPERACION FINALIZADA");
   }
 }
