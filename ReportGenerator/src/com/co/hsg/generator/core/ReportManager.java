 package com.co.hsg.generator.core;
 
 import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.co.hsg.generator.bean.ReportParams;
import com.co.hsg.generator.bean.Reports;
import com.co.hsg.generator.io.FileManager;
import com.co.hsg.generator.log.LogInfo;
 
 public class ReportManager
 {
   private DataFileManager dataMgr;
   private Map<Integer, Object> pages;
  
 
   public ReportManager()
   {
     this.dataMgr = new DataFileManager();
     this.pages = new HashMap();
   }
 
   public boolean generateReports(String id, String type) {
     boolean resp = false;
     LogInfo.T("[ReportManager] Cargando Datos de Archivo:" + id);
     Reports reportTemplate = Reports.getReportType(type);
     if (reportTemplate != null) {
       resp = generate(id, reportTemplate, TypeReport.JASPER);
     }
 
     return resp;
   }
 
   public boolean generate(String id, Reports reportTemplate, TypeReport type) {
     boolean resp = false;
     try {
       ReportParams reportInfo = this.dataMgr.getReportInfo(reportTemplate, id);
       List data = new ArrayList();
       data.add(reportInfo.getDatos());
       
       //AGREGAR PARAMETROS Y DS AL REPORTE
       if(reportInfo.getDatos().getDeudores().size() > 0){
    	   JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(reportInfo.getDatos().getDeudores());
           reportInfo.addParam("SUB_DATASOURCE", ds1) ;
           JRBeanCollectionDataSource ds2 = new JRBeanCollectionDataSource(reportInfo.getDatos().getDeudores());
           reportInfo.addParam("SUB_DATASOURCE2", ds2) ;
       } 
       if(reportInfo.getDatos().getInquilinos().size() > 0){ 
           JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(reportInfo.getDatos().getInquilinos());
           reportInfo.addParam("SUB_DATASOURCE_ARR", ds3);
          
           JRBeanCollectionDataSource ds4= new JRBeanCollectionDataSource(reportInfo.getDatos().getInquilinos());
           reportInfo.addParam("SUB_DATASOURCE2_SIGN", ds4);
     
       }
       JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);
       reportInfo.setDatasourcePpal(ds);
       String pathTarget = FileManager.getInst().leerPropiedad("RUTA_TEMP");
 
       LogInfo.T("[ReportManager] Nombre Archivo :" + reportInfo.getFileName());
 
       switch (type) {
       case JASPER:
         compileJasper(pathTarget, reportInfo);
         break;
       case JRXML:
         compileJRXML(pathTarget, reportInfo);
         break;
       }
 
       this.dataMgr.insertReportToSugar(reportInfo, reportTemplate);
 
       LogInfo.T("[ReportManager] Finalizado");
       resp = true;
     } catch (Exception e) {
       LogInfo.E("[ReportManager] Fallo al generar Reporte:", e);
     }
 
     return resp;
   }
 
   public void chargeData(String filePath)
   {
     this.dataMgr = new DataFileManager();
     try
     {
       this.dataMgr.chargeParams();
     }
     catch (Exception e)
     {
       LogInfo.E("[ReportManager] Fallo al Cargar Datos Archivo Plano", e);
     }
   }
 
   private void compileJRXML(String pathTarget, ReportParams reportInfo) throws Exception
   {
     JasperDesign jd = JRXmlLoader.load(pathTarget + reportInfo.getFileName());
 
     JasperReport report = JasperCompileManager.compileReport(jd);
 
     JasperPrint print = JasperFillManager.fillReport(report, reportInfo.getParams(), reportInfo.getDatasourcePpal());
 
     JRPdfExporter exporter = new JRPdfExporter();
     pathTarget = pathTarget + "/PruebaChart.pdf";
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     try
     {
       exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, 
         "UTF-8");
       exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
       exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
       exporter.exportReport();
 
       File f = new File(pathTarget);
       FileOutputStream fos = new FileOutputStream(f);
       fos.write(baos.toByteArray());
       fos.flush();
       fos.close();
       LogInfo.T("[ReportManager] Ruta Archivo Unico JRXML :" + pathTarget);
     } catch (Exception e) {
       LogInfo.E("[ReportManager] Fallo al generar Reporte Completo:" + 
         pathTarget, e);
     }
 
     LogInfo.T("[ReportManager] Ruta Archivo GENERADO :" + pathTarget);
   }
 
   private void compileJasper(String pathTarget, ReportParams reportInfo) throws Exception
   {
     String filePath = pathTarget + reportInfo.getFileName();
 
     LogInfo.T("[ReportManager] Compilando Archivo :" + 
       filePath);
     LogInfo.T("[ReportManager] PLantilla :" + 
       reportInfo.getJasperFile());
 
     JasperPrint jp = JasperFillManager.fillReport(reportInfo.getJasperFile(), 
       reportInfo.getParams(), reportInfo.getDatasourcePpal());
     LogInfo.T("[ReportManager] Archivo Cargado");
 
     JRPdfExporter exporter = new JRPdfExporter();
 
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
 
     exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, 
       "UTF-8");
     exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
     exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
     exporter.exportReport();
 
     File f = new File(pathTarget, reportInfo.getReportID());
     LogInfo.T("[ReportManager] Creando Archivo");
 
     FileOutputStream fos = new FileOutputStream(f);
 
     fos.write(baos.toByteArray());
     LogInfo.T("[ReportManager] Escribiendo");
     fos.flush();
     fos.close();
 
     LogInfo.T("[ReportManager] Ruta Archivo GENERADO Sugar :" + 
       f.getAbsolutePath());
   }

 
   public static enum TypeReport
   {
     JASPER, JRXML;
   }
 }
