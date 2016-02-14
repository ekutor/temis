/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class DocsConnectionPropsHelper extends ConnectionPropertiesImpl
/*    */ {
/*    */   static final long serialVersionUID = -1580779062220390294L;
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 37 */     System.out.println(new DocsConnectionPropsHelper().exposeAsXml());
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.DocsConnectionPropsHelper
 * JD-Core Version:    0.6.0
 */