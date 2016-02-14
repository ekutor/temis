/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class NotImplemented extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = 7768433826547599990L;
/*    */ 
/*    */   public NotImplemented()
/*    */   {
/* 44 */     super(Messages.getString("NotImplemented.0"), "S1C00");
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NotImplemented
 * JD-Core Version:    0.6.0
 */