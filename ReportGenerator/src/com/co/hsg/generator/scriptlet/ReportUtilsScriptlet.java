/*    */ package com.co.hsg.generator.scriptlet;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.DecimalFormatSymbols;
/*    */ import net.sf.jasperreports.engine.JRDefaultScriptlet;
/*    */ import net.sf.jasperreports.engine.JRScriptletException;
/*    */ 
/*    */ public class ReportUtilsScriptlet extends JRDefaultScriptlet
/*    */ {
/*    */   public String formatNumber(String value)
/*    */     throws JRScriptletException
/*    */   {
/* 13 */     Integer val = Integer.valueOf(Integer.parseInt(value));
/*    */     String resp;
/*    */     try
/*    */     {
/* 15 */       DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
/* 16 */       decimalFormatSymbols.setDecimalSeparator(',');
/* 17 */       decimalFormatSymbols.setGroupingSeparator('.');
/* 18 */       DecimalFormat decimalFormat = new DecimalFormat("###,###.##", decimalFormatSymbols);
/* 19 */       resp = decimalFormat.format(val);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */       String resp;
/* 21 */       resp = value;
/* 22 */       e.printStackTrace();
/*    */     }
/* 24 */     return resp;
/*    */   }
/*    */   public String formatNumber(Integer value) throws JRScriptletException {
/*    */     String resp;
/*    */     try {
/* 30 */       DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
/* 31 */       decimalFormatSymbols.setDecimalSeparator(',');
/* 32 */       decimalFormatSymbols.setGroupingSeparator('.');
/* 33 */       DecimalFormat decimalFormat = new DecimalFormat("###,###.##", decimalFormatSymbols);
/* 34 */       resp = decimalFormat.format(value);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */       String resp;
/* 37 */       resp = String.valueOf(value);
/* 38 */       e.printStackTrace();
/*    */     }
/* 40 */     return resp;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.scriptlet.ReportUtilsScriptlet
 * JD-Core Version:    0.6.0
 */