/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.rmi.server.UID;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Savepoint;
/*     */ 
/*     */ public class MysqlSavepoint
/*     */   implements Savepoint
/*     */ {
/*     */   private String savepointName;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   private static String getUniqueId()
/*     */   {
/*  42 */     String uidStr = new UID().toString();
/*     */ 
/*  44 */     int uidLength = uidStr.length();
/*     */ 
/*  46 */     StringBuffer safeString = new StringBuffer(uidLength + 1);
/*  47 */     safeString.append('_');
/*     */ 
/*  49 */     for (int i = 0; i < uidLength; i++) {
/*  50 */       char c = uidStr.charAt(i);
/*     */ 
/*  52 */       if ((Character.isLetter(c)) || (Character.isDigit(c)))
/*  53 */         safeString.append(c);
/*     */       else {
/*  55 */         safeString.append('_');
/*     */       }
/*     */     }
/*     */ 
/*  59 */     return safeString.toString();
/*     */   }
/*     */ 
/*     */   MysqlSavepoint(ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  75 */     this(getUniqueId(), exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   MysqlSavepoint(String name, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  88 */     if ((name == null) || (name.length() == 0)) {
/*  89 */       throw SQLError.createSQLException("Savepoint name can not be NULL or empty", "S1009", exceptionInterceptor);
/*     */     }
/*     */ 
/*  93 */     this.savepointName = name;
/*     */ 
/*  95 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   public int getSavepointId()
/*     */     throws SQLException
/*     */   {
/* 102 */     throw SQLError.createSQLException("Only named savepoints are supported.", "S1C00", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public String getSavepointName()
/*     */     throws SQLException
/*     */   {
/* 110 */     return this.savepointName;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.MysqlSavepoint
 * JD-Core Version:    0.6.0
 */