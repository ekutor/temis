/*    */ package com.co.hsg.generator.io;
/*    */ 
/*    */ public enum Constants
/*    */ {
/*  5 */   PATH_APP("/bbn6/dvc"), 
/*  6 */   LOGO("resources/icono.png");
/*    */ 
/*    */   private String value;
/*    */ 
/* 11 */   private Constants(String value) { this.value = value; }
/*    */ 
/*    */   public String getValue()
/*    */   {
/* 15 */     return this.value;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.io.Constants
 * JD-Core Version:    0.6.0
 */