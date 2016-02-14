/*     */ package com.co.hsg.generator.core;
/*     */ 
/*     */ import com.co.hsg.generator.bean.ReportField;
/*     */ import com.co.hsg.generator.bean.ReportParams;
/*     */ import com.co.hsg.generator.bean.Reports;
/*     */ import com.co.hsg.generator.db.DBManagerDAO;
/*     */ import com.co.hsg.generator.io.FileManager;
/*     */ import com.co.hsg.generator.log.LogInfo;
/*     */ import com.co.hsg.generator.util.Util;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DataFileManager
/*     */ {
/*     */   private DBManagerDAO db;
/*     */   private static final String PRE = "REPORT.";
/*     */ 
/*     */   public DataFileManager()
/*     */   {
/*  24 */     this.db = new DBManagerDAO();
/*     */   }
/*     */ 
/*     */   public List<Object> dataToObject(Reports repo, String data)
/*     */     throws Exception
/*     */   {
/*  34 */     LogInfo.T("[DataFileManager] cargando Informacion..");
/*  35 */     List objects = new ArrayList();
/*     */ 
/*  38 */     boolean cont = true;
/*     */     try
/*     */     {
/*  41 */       switch (repo)
/*     */       {
/*     */       case CONTRATO_ADMON_VIVIENDA:
/*  44 */         LogInfo.T("[DataFileManager] convirtiendo Objetos DVC_MES");
/*  45 */         objects.add(datatoReport(data));
/*  46 */         LogInfo.T("[DataFileManager] agregado ");
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (ArrayIndexOutOfBoundsException e)
/*     */     {
/*  55 */       LogInfo.E("[DataFileManager] Falla al generar  por  datos recibidos Incompletos ", e);
/*     */     }
/*     */ 
/*  59 */     return objects;
/*     */   }
/*     */ 
/*     */   private ReportField datatoReport(String data)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/*  65 */     String[] inf = data.split(",");
/*     */ 
/*  67 */     ReportField v = new ReportField();
/*  68 */     v.setARRENDATARIO1("arreendador uno");
/*  69 */     v.setDEUDOR1("Deudor 1");
/*     */ 
/*  71 */     return v;
/*     */   }
/*     */ 
/*     */   public void addReport()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ReportParams getReportInfo(Reports report, String contractID) throws Exception {
/*  79 */     ReportField reportInfo = this.db.getContract(contractID, report);
/*     */ 
/*  81 */     ReportParams r = new ReportParams();
/*  82 */     r.setReport(report);
/*  83 */     r.generateUID();
/*  84 */     String jasper = "";
/*  85 */     switch (report) {
/*     */     case CONTRATO_ADMON_VIVIENDA:
/*  87 */       jasper = FileManager.getInst().leerPropiedad("REPORT." + report.name());
/*     */ 
/*  89 */       break;
/*     */     case CONTRATO_ARRENDAMIENTO:
/*  91 */       jasper = FileManager.getInst().leerPropiedad("REPORT." + report.name());
/*  92 */       break;
/*     */     }
/*     */ 
/*  98 */     r.setJasperFile(Util.getAbsolutePath(jasper));
/*  99 */     if (reportInfo != null) {
/* 100 */       r.setDatos(reportInfo);
/* 101 */       return r;
/*     */     }
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<Reports, ReportParams> chargeParams()
/*     */     throws Exception
/*     */   {
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */   public void insertReportToSugar(ReportParams reportInfo, Reports reportTemplate) throws Exception
/*     */   {
/* 138 */     this.db.saveFile(reportInfo.getReportID(), reportInfo.getDatos(), reportInfo.getFileName(), reportTemplate.name());
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.core.DataFileManager
 * JD-Core Version:    0.6.0
 */