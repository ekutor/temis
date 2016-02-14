 package com.co.hsg.generator.bean;
 
 public enum Reports
 {
   CONTRATO_ARRENDAMIENTO, CONTRATO_ADMON_VIVIENDA;
 
   public static Reports getReportType(String type) {
     Reports[] reports = values();
     for (Reports r : reports) {
       if (r.name().equals(type)) {
         return r;
       }
     }
     return null;
   }
 }
