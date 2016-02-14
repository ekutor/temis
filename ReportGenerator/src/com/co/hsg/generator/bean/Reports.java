/*    */ package com.co.hsg.generator.bean;
/*    */ 
/*    */ public enum Reports
/*    */ {
/*  4 */   CONTRATO_ARRENDAMIENTO, CONTRATO_ADMON_VIVIENDA;
/*    */ 
/*    */   public static Reports getReportType(String type) {
/*  7 */     Reports[] reports = values();
/*  8 */     for (Reports r : reports) {
/*  9 */       if (r.name().equals(type)) {
/* 10 */         return r;
/*    */       }
/*    */     }
/* 13 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.bean.Reports
 * JD-Core Version:    0.6.0
 */