/*     */ package com.co.hsg.generator.core;
/*     */ 
/*     */ import com.co.hsg.generator.bean.ReportParams;
/*     */ import com.co.hsg.generator.bean.Reports;
/*     */ import com.co.hsg.generator.io.Constants;
/*     */ import com.co.hsg.generator.io.FileManager;
/*     */ import com.co.hsg.generator.log.LogInfo;
/*     */ import com.co.hsg.generator.util.Util;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import net.sf.jasperreports.engine.JRExporterParameter;
/*     */ import net.sf.jasperreports.engine.JasperCompileManager;
/*     */ import net.sf.jasperreports.engine.JasperFillManager;
/*     */ import net.sf.jasperreports.engine.JasperPrint;
/*     */ import net.sf.jasperreports.engine.JasperReport;
/*     */ import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
/*     */ import net.sf.jasperreports.engine.design.JasperDesign;
/*     */ import net.sf.jasperreports.engine.export.JRPdfExporter;
/*     */ import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
/*     */ import net.sf.jasperreports.engine.xml.JRXmlLoader;
/*     */ 
/*     */ public class ReportManager
/*     */ {
/*     */   private DataFileManager dataMgr;
/*     */   private Map<Integer, Object> pages;
/*     */   private List<Object> reportsJP;
/*     */ 
/*     */   public ReportManager()
/*     */   {
/*  45 */     this.dataMgr = new DataFileManager();
/*  46 */     this.pages = new HashMap();
/*     */   }
/*     */ 
/*     */   public boolean generateReports(String id, String type) {
/*  50 */     boolean resp = false;
/*  51 */     LogInfo.T("[ReportManager] Cargando Datos de Archivo:" + id);
/*  52 */     Reports reportTemplate = Reports.getReportType(type);
/*  53 */     if (reportTemplate != null) {
/*  54 */       resp = generate(id, reportTemplate, TypeReport.JASPER);
/*     */     }
/*     */ 
/*  57 */     return resp;
/*     */   }
/*     */ 
/*     */   public boolean generate(String id, Reports reportTemplate, TypeReport type) {
/*  61 */     boolean resp = false;
/*     */     try {
/*  63 */       ReportParams reportInfo = this.dataMgr.getReportInfo(reportTemplate, id);
/*  64 */       List data = new ArrayList();
/*  65 */       data.add(reportInfo.getDatos());
/*  66 */       this.reportsJP = new ArrayList();
/*     */ 
/*  68 */       JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);
/*  69 */       reportInfo.setDatasourcePpal(ds);
/*  70 */       String pathTarget = FileManager.getInst().leerPropiedad("RUTA_TEMP");
/*     */ 
/*  73 */       LogInfo.T("[ReportManager] Nombre Archivo :" + reportInfo.getFileName());
/*     */ 
/*  76 */       switch (type) {
/*     */       case JASPER:
/*  78 */         compileJasper(pathTarget, reportInfo);
/*  79 */         break;
/*     */       case JRXML:
/*  81 */         compileJRXML(pathTarget, reportInfo);
/*  82 */         break;
/*     */       }
/*     */ 
/*  87 */       this.dataMgr.insertReportToSugar(reportInfo, reportTemplate);
/*     */ 
/*  89 */       LogInfo.T("[ReportManager] Finalizado");
/*  90 */       resp = true;
/*     */     } catch (Exception e) {
/*  92 */       LogInfo.E("[ReportManager] Fallo al generar Reporte:", e);
/*     */     }
/*     */ 
/*  95 */     return resp;
/*     */   }
/*     */ 
/*     */   public void chargeData(String filePath)
/*     */   {
/* 104 */     this.dataMgr = new DataFileManager();
/*     */     try
/*     */     {
/* 107 */       this.dataMgr.chargeParams();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 111 */       LogInfo.E("[ReportManager] Fallo al Cargar Datos Archivo Plano", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void compileJRXML(String pathTarget, ReportParams reportInfo) throws Exception
/*     */   {
/* 117 */     JasperDesign jd = JRXmlLoader.load(pathTarget + reportInfo.getFileName());
/*     */ 
/* 120 */     JasperReport report = JasperCompileManager.compileReport(jd);
/*     */ 
/* 126 */     JasperPrint print = JasperFillManager.fillReport(report, reportInfo.getParams(), reportInfo.getDatasourcePpal());
/*     */ 
/* 131 */     JRPdfExporter exporter = new JRPdfExporter();
/* 132 */     pathTarget = pathTarget + "/PruebaChart.pdf";
/* 133 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     try
/*     */     {
/* 141 */       exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, 
/* 142 */         "UTF-8");
/* 143 */       exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
/* 144 */       exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
/* 145 */       exporter.exportReport();
/*     */ 
/* 147 */       File f = new File(pathTarget);
/* 148 */       FileOutputStream fos = new FileOutputStream(f);
/* 149 */       fos.write(baos.toByteArray());
/* 150 */       fos.flush();
/* 151 */       fos.close();
/* 152 */       LogInfo.T("[ReportManager] Ruta Archivo Unico JRXML :" + pathTarget);
/*     */     } catch (Exception e) {
/* 154 */       LogInfo.E("[ReportManager] Fallo al generar Reporte Completo:" + 
/* 155 */         pathTarget, e);
/*     */     }
/*     */ 
/* 158 */     LogInfo.T("[ReportManager] Ruta Archivo GENERADO :" + pathTarget);
/*     */   }
/*     */ 
/*     */   private void compileJasper(String pathTarget, ReportParams reportInfo) throws Exception
/*     */   {
/* 163 */     String filePath = pathTarget + reportInfo.getFileName();
/*     */ 
/* 165 */     LogInfo.T("[ReportManager] Compilando Archivo :" + 
/* 166 */       filePath);
/* 167 */     LogInfo.T("[ReportManager] PLantilla :" + 
/* 168 */       reportInfo.getJasperFile());
/*     */ 
/* 170 */     JasperPrint jp = JasperFillManager.fillReport(reportInfo.getJasperFile(), 
/* 171 */       reportInfo.getParams(), reportInfo.getDatasourcePpal());
/* 172 */     LogInfo.T("[ReportManager] Archivo Cargado");
/*     */ 
/* 174 */     JRPdfExporter exporter = new JRPdfExporter();
/*     */ 
/* 176 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */ 
/* 179 */     exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, 
/* 180 */       "UTF-8");
/* 181 */     exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
/* 182 */     exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
/* 183 */     exporter.exportReport();
/*     */ 
/* 185 */     File f = new File(pathTarget, reportInfo.getReportID());
/* 186 */     LogInfo.T("[ReportManager] Creando Archivo");
/*     */ 
/* 188 */     FileOutputStream fos = new FileOutputStream(f);
/*     */ 
/* 190 */     fos.write(baos.toByteArray());
/* 191 */     LogInfo.T("[ReportManager] Escribiendo");
/* 192 */     fos.flush();
/* 193 */     fos.close();
/*     */ 
/* 195 */     LogInfo.T("[ReportManager] Ruta Archivo GENERADO Sugar :" + 
/* 196 */       f.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   private void exportReport(String pathTarget)
/*     */   {
/*     */     try
/*     */     {
/* 208 */       Map params = new HashMap();
/* 209 */       params.put("LOGO", Util.getAbsolutePath(Constants.LOGO.getValue()));
/*     */ 
/* 212 */       JRPdfExporter exporter = new JRPdfExporter();
/*     */ 
/* 214 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 215 */       exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, 
/* 216 */         this.reportsJP);
/*     */ 
/* 219 */       exporter.setParameter(
/* 220 */         JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, 
/* 221 */         Boolean.TRUE);
/* 222 */       exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
/* 223 */       exporter.exportReport();
/*     */ 
/* 225 */       File f = new File(pathTarget);
/* 226 */       FileOutputStream fos = new FileOutputStream(f);
/* 227 */       fos.write(baos.toByteArray());
/* 228 */       fos.flush();
/* 229 */       fos.close();
/* 230 */       LogInfo.T("[ReportManager] Ruta Archivo Unico GENERADO :" + pathTarget);
/*     */     } catch (Exception e) {
/* 232 */       LogInfo.T("[ReportManager] Fallo al generar Reporte Completo:" + pathTarget);
/* 233 */       LogInfo.E("[ReportManager] Fallo al generar Reporte Unico::", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum TypeReport
/*     */   {
/*  41 */     JASPER, JRXML;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.core.ReportManager
 * JD-Core Version:    0.6.0
 */