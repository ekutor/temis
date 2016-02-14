/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionPropertiesImpl;
/*    */ import java.io.PrintStream;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class PropertiesDocGenerator extends ConnectionPropertiesImpl
/*    */ {
/*    */   static final long serialVersionUID = -4869689139143855383L;
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws SQLException
/*    */   {
/* 41 */     System.out.println(new PropertiesDocGenerator().exposeAsXml());
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.util.PropertiesDocGenerator
 * JD-Core Version:    0.6.0
 */