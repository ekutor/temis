/*     */ package com.mysql.jdbc.integration.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mysql.jdbc.CommunicationsException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public final class MysqlConnectionTester
/*     */   implements QueryConnectionTester
/*     */ {
/*     */   private static final long serialVersionUID = 3256444690067896368L;
/*  48 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*     */   private transient Method pingMethod;
/*     */ 
/*     */   public MysqlConnectionTester()
/*     */   {
/*     */     try
/*     */     {
/*  54 */       this.pingMethod = com.mysql.jdbc.Connection.class.getMethod("ping", (Class[])null);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection con)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       if (this.pingMethod != null) {
/*  71 */         if ((con instanceof com.mysql.jdbc.Connection))
/*     */         {
/*  74 */           ((com.mysql.jdbc.Connection)con).ping();
/*     */         }
/*     */         else {
/*  77 */           C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
/*  78 */           castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
/*     */         }
/*     */       }
/*     */       else {
/*  82 */         Statement pingStatement = null;
/*     */         try
/*     */         {
/*  85 */           pingStatement = con.createStatement();
/*  86 */           pingStatement.executeQuery("SELECT 1").close();
/*     */         } finally {
/*  88 */           if (pingStatement != null) {
/*  89 */             pingStatement.close();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  94 */       return 0; } catch (Exception ex) {
/*     */     }
/*  96 */     return -1;
/*     */   }
/*     */ 
/*     */   public int statusOnException(java.sql.Connection arg0, Throwable throwable)
/*     */   {
/* 107 */     if (((throwable instanceof CommunicationsException)) || ("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals(throwable.getClass().getName())))
/*     */     {
/* 110 */       return -1;
/*     */     }
/*     */ 
/* 113 */     if ((throwable instanceof SQLException)) {
/* 114 */       String sqlState = ((SQLException)throwable).getSQLState();
/*     */ 
/* 116 */       if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 117 */         return -1;
/*     */       }
/*     */ 
/* 120 */       return 0;
/*     */     }
/*     */ 
/* 125 */     return -1;
/*     */   }
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection arg0, String arg1)
/*     */   {
/* 135 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.integration.c3p0.MysqlConnectionTester
 * JD-Core Version:    0.6.0
 */