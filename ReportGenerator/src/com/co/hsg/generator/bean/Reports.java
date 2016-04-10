 package com.co.hsg.generator.bean;
 
 public enum Reports
 {
   CONTRATO_ARRENDAMIENTO, CONTRATO_ADMON_VIVIENDA, CONTRATO_ADMON_COMERCIAL,CONTRATO_ARRENDAMIENTO_COMERCIAL, CONTRATO_ARRENDAMIENTO_VIVIENDA_DEST_COMERCIAL,
   CONTRATO_ARRENDAMIENTO_REP_LEGAL, CONTRATO_ADMINISTRACION_REP_LEGAL;
 
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
