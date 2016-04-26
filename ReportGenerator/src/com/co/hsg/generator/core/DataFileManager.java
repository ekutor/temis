 package com.co.hsg.generator.core;
 
 import java.util.Map;

import com.co.hsg.generator.bean.ReportField;
import com.co.hsg.generator.bean.ReportParams;
import com.co.hsg.generator.bean.Reports;
import com.co.hsg.generator.db.DBManagerDAO;
import com.co.hsg.generator.io.FileManager;
import com.co.hsg.generator.util.Util;
 
 public class DataFileManager
 {
   private DBManagerDAO db;
   private static final String PRE = "REPORT.";
 
   public DataFileManager()
   {
     this.db = new DBManagerDAO();
   }
 
 
 
   private ReportField datatoReport(String data)
     throws ArrayIndexOutOfBoundsException
   {
     String[] inf = data.split(",");
 
     ReportField v = new ReportField();
     v.setARRENDATARIO1("arreendador uno");
     v.setDEUDOR1("Deudor 1");
 
     return v;
   }
 
   public void addReport()
   {
   }
 
   public ReportParams getReportInfo(Reports report, String contractID) throws Exception {
     ReportField reportInfo = this.db.getContract(contractID, report);
 
     ReportParams r = new ReportParams();
     r.setReport(report);
     r.generateUID();
     String jasper = "", subreportHeader = "",subreportSign = "",subreportArr = "";
     
     jasper = FileManager.getInst().leerPropiedad("REPORT." + report.name());
     subreportSign = FileManager.getInst().leerPropiedad("SUBREPORT.SIGN."+report.name());
     subreportHeader = FileManager.getInst().leerPropiedad("SUBREPORT.HEADER");
     subreportArr = FileManager.getInst().leerPropiedad("SUBREPORT.HEADER_ARRENDATARIOS");
     
     r.setJasperFile(Util.getAbsolutePath(jasper));
     //SUBREPORTS
     r.addParam("SUBREPORT", Util.getAbsolutePath(subreportHeader));
     r.addParam("SUBREPORTSIGN", Util.getAbsolutePath(subreportSign));
     r.addParam("SUBREPORTARRENDATARIOS", Util.getAbsolutePath(subreportArr));
     
     
     r.addParam("TITLE_ARRENDATARIOS", "ARRENDATARIO(S)");
     r.addParam("TITLE_PROPIETARIOS", "PROPIETARIO(S)");
     
     switch(report){
     
     case CONTRATO_ADMON_COMERCIAL:
     case CONTRATO_ADMON_VIVIENDA:
    	 r.addParam("SINGLE_TITLE", "CONSIGNANTE");
    	 r.addParam("TITLE_ARRENDATARIOS", "PROPIETARIO(S)");
    	 r.addParam("TITLE_DEUDORES", "CONSIGNANTES");
    	 break;
     case CONTRATO_ARRENDAMIENTO:
     case CONTRATO_ARRENDAMIENTO_VIVIENDA_DEST_COMERCIAL:
     case CONTRATO_ARRENDAMIENTO_COMERCIAL:
    	 r.addParam("SINGLE_TITLE", "DEUDOR");
    	 r.addParam("TITLE_DEUDORES", "DEUDORES SOLIDARIOS");
    	 break;
     }
     if (reportInfo != null) {
       r.setDatos(reportInfo);
       return r;
     }
     return null;
   }
 
   public Map<Reports, ReportParams> chargeParams()
     throws Exception
   {
     return null;
   }
 
   public void insertReportToSugar(ReportParams reportInfo, Reports reportTemplate) throws Exception
   {
     this.db.saveFile(reportInfo.getReportID(), reportInfo.getDatos(), reportInfo.getFileName(), reportTemplate.name());
   }
 }

