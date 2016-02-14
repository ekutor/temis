/*     */ package com.co.hsg.generator.bean;
/*     */ 
/*     */ import com.co.hsg.generator.io.Constants;
/*     */ import com.co.hsg.generator.log.LogInfo;
/*     */ import com.co.hsg.generator.util.Util;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import net.sf.jasperreports.engine.JRAbstractScriptlet;
/*     */ import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
/*     */ import net.sf.jasperreports.engine.util.JRClassLoader;
/*     */ 
/*     */ public class ReportParams
/*     */ {
/*     */   private JRAbstractScriptlet scriptlet;
/*     */   private Map<String, Object> params;
/*     */   private String fileName;
/*     */   private ReportField datos;
/*     */   private JRBeanCollectionDataSource datasourcePpal;
/*     */   private Reports report;
/*     */   private String jasperFile;
/*     */   private UUID uid;
/*     */ 
/*     */   public ReportParams()
/*     */   {
/*  31 */     this.params = new HashMap();
/*     */ 
/*  33 */     this.params.put("LOGO", Util.getAbsolutePath(Constants.LOGO.getValue()));
/*  34 */     Locale locale = new Locale("es", "ES");
/*  35 */     this.params.put("REPORT_LOCALE", locale);
/*     */   }
/*     */ 
/*     */   private void chargeScriptlet()
/*     */   {
/*     */     try
/*     */     {
/*  42 */       Class scriptletClass = 
/*  43 */         JRClassLoader.loadClassForName("com.co.hsg.generator.scriptlet.ReportUtilsScriptlet");
/*  44 */       this.scriptlet = ((JRAbstractScriptlet)scriptletClass.newInstance());
/*  45 */       this.params.put("REPORT_SCRIPTLET", this.scriptlet);
/*  46 */       LogInfo.T("[ReportManager] Scriptlet Agregado SAtisfactoriamente ");
/*     */     } catch (Exception ex) {
/*  48 */       LogInfo.E("[ReportManager] Scriptlet no encontrado ::" + Util.errorToString(ex));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getParams()
/*     */   {
/*  56 */     return this.params;
/*     */   }
/*     */ 
/*     */   public void addParam(String llave, Object param)
/*     */   {
/*  61 */     this.params.put(llave, param);
/*     */   }
/*     */ 
/*     */   public void setFile(String absolutePath) {
/*  65 */     this.fileName = absolutePath;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  70 */     if (this.fileName == null) {
/*  71 */       this.fileName = (this.report.name() + ".pdf");
/*     */     }
/*  73 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public ReportField getDatos() {
/*  77 */     return this.datos;
/*     */   }
/*     */ 
/*     */   public void setDatos(ReportField reportInfo) {
/*  81 */     this.datos = reportInfo;
/*     */   }
/*     */ 
/*     */   public JRBeanCollectionDataSource getDatasourcePpal() {
/*  85 */     return this.datasourcePpal;
/*     */   }
/*     */ 
/*     */   public void setDatasourcePpal(JRBeanCollectionDataSource datasourcePpal)
/*     */   {
/*  90 */     this.datasourcePpal = datasourcePpal;
/*     */   }
/*     */ 
/*     */   public void setFileName(String fileName)
/*     */   {
/*  95 */     this.fileName = fileName;
/*     */   }
/*     */ 
/*     */   public Reports getReport()
/*     */   {
/* 100 */     return this.report;
/*     */   }
/*     */ 
/*     */   public void setReport(Reports report)
/*     */   {
/* 105 */     this.report = report;
/*     */   }
/*     */ 
/*     */   public void setReporte(Reports report)
/*     */   {
/* 110 */     this.report = report;
/*     */   }
/*     */ 
/*     */   public String getJasperFile()
/*     */   {
/* 115 */     return this.jasperFile;
/*     */   }
/*     */ 
/*     */   public void setJasperFile(String jasperFile)
/*     */   {
/* 120 */     this.jasperFile = jasperFile;
/*     */   }
/*     */ 
/*     */   public void generateUID()
/*     */   {
/* 125 */     UUID uuidRel = UUID.randomUUID();
/* 126 */     this.uid = uuidRel;
/*     */   }
/*     */ 
/*     */   public String getReportID() {
/* 130 */     return this.uid;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.bean.ReportParams
 * JD-Core Version:    0.6.0
 */