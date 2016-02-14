/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ public class AssertionFailedException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public static void shouldNotHappen(Exception ex)
/*    */     throws AssertionFailedException
/*    */   {
/* 54 */     throw new AssertionFailedException(ex);
/*    */   }
/*    */ 
/*    */   public AssertionFailedException(Exception ex)
/*    */   {
/* 68 */     super(Messages.getString("AssertionFailedException.0") + ex.toString() + Messages.getString("AssertionFailedException.1"));
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.AssertionFailedException
 * JD-Core Version:    0.6.0
 */