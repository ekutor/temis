/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class NotUpdatable extends SQLException
/*    */ {
/*    */   private static final long serialVersionUID = 8084742846039782258L;
/* 45 */   public static final String NOT_UPDATEABLE_MESSAGE = Messages.getString("NotUpdatable.0") + Messages.getString("NotUpdatable.1") + Messages.getString("NotUpdatable.2") + Messages.getString("NotUpdatable.3") + Messages.getString("NotUpdatable.4") + Messages.getString("NotUpdatable.5");
/*    */ 
/*    */   public NotUpdatable()
/*    */   {
/* 57 */     this(NOT_UPDATEABLE_MESSAGE);
/*    */   }
/*    */ 
/*    */   public NotUpdatable(String reason)
/*    */   {
/* 65 */     super(reason + Messages.getString("NotUpdatable.1") + Messages.getString("NotUpdatable.2") + Messages.getString("NotUpdatable.3") + Messages.getString("NotUpdatable.4") + Messages.getString("NotUpdatable.5"), "S1000");
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NotUpdatable
 * JD-Core Version:    0.6.0
 */