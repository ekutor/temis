 package com.co.hsg.generator.scriptlet;
 
 import java.text.DecimalFormat;
 import java.text.DecimalFormatSymbols;
 import net.sf.jasperreports.engine.JRDefaultScriptlet;
 import net.sf.jasperreports.engine.JRScriptletException;
 
 public class ReportUtilsScriptlet extends JRDefaultScriptlet
 {
   public String formatNumber(String value)
     throws JRScriptletException
   {
     Integer val = Integer.valueOf(Integer.parseInt(value));
     String resp;
     try
     {
       DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
       decimalFormatSymbols.setDecimalSeparator(',');
       decimalFormatSymbols.setGroupingSeparator('.');
       DecimalFormat decimalFormat = new DecimalFormat("###,###.##", decimalFormatSymbols);
       resp = decimalFormat.format(val);
     }
     catch (Exception e)
     {
       resp = value;
       e.printStackTrace();
     }
     return resp;
   }
   public String formatNumber(Integer value) throws JRScriptletException {
     String resp;
     try {
       DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
       decimalFormatSymbols.setDecimalSeparator(',');
       decimalFormatSymbols.setGroupingSeparator('.');
       DecimalFormat decimalFormat = new DecimalFormat("###,###.##", decimalFormatSymbols);
       resp = decimalFormat.format(value);
     }
     catch (Exception e)
     {
       resp = String.valueOf(value);
       e.printStackTrace();
     }
     return resp;
   }
 }
