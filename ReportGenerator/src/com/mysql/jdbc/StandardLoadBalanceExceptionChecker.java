/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class StandardLoadBalanceExceptionChecker
/*     */   implements LoadBalanceExceptionChecker
/*     */ {
/*     */   private List<String> sqlStateList;
/*     */   private List<Class<?>> sqlExClassList;
/*     */ 
/*     */   public boolean shouldExceptionTriggerFailover(SQLException ex)
/*     */   {
/*  39 */     String sqlState = ex.getSQLState();
/*     */     Iterator i;
/*  41 */     if (sqlState != null) {
/*  42 */       if (sqlState.startsWith("08"))
/*     */       {
/*  44 */         return true;
/*     */       }
/*  46 */       if (this.sqlStateList != null)
/*     */       {
/*  48 */         for (i = this.sqlStateList.iterator(); i.hasNext(); ) {
/*  49 */           if (sqlState.startsWith(((String)i.next()).toString())) {
/*  50 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  57 */     if ((ex instanceof CommunicationsException))
/*  58 */       return true;
/*     */     Iterator i;
/*  60 */     if (this.sqlExClassList != null)
/*     */     {
/*  62 */       for (i = this.sqlExClassList.iterator(); i.hasNext(); ) {
/*  63 */         if (((Class)i.next()).isInstance(ex)) {
/*  64 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {
/*  79 */     configureSQLStateList(props.getProperty("loadBalanceSQLStateFailover", null));
/*  80 */     configureSQLExceptionSubclassList(props.getProperty("loadBalanceSQLExceptionSubclassFailover", null));
/*     */   }
/*     */ 
/*     */   private void configureSQLStateList(String sqlStates)
/*     */   {
/*  85 */     if ((sqlStates == null) || ("".equals(sqlStates))) {
/*  86 */       return;
/*     */     }
/*  88 */     List states = StringUtils.split(sqlStates, ",", true);
/*  89 */     List newStates = new ArrayList();
/*     */ 
/*  91 */     for (String state : states) {
/*  92 */       if (state.length() > 0) {
/*  93 */         newStates.add(state);
/*     */       }
/*     */     }
/*  96 */     if (newStates.size() > 0)
/*  97 */       this.sqlStateList = newStates;
/*     */   }
/*     */ 
/*     */   private void configureSQLExceptionSubclassList(String sqlExClasses)
/*     */   {
/* 102 */     if ((sqlExClasses == null) || ("".equals(sqlExClasses))) {
/* 103 */       return;
/*     */     }
/* 105 */     List classes = StringUtils.split(sqlExClasses, ",", true);
/* 106 */     List newClasses = new ArrayList();
/*     */ 
/* 108 */     for (String exClass : classes)
/*     */       try {
/* 110 */         Class c = Class.forName(exClass);
/* 111 */         newClasses.add(c);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/* 116 */     if (newClasses.size() > 0)
/* 117 */       this.sqlExClassList = newClasses;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.StandardLoadBalanceExceptionChecker
 * JD-Core Version:    0.6.0
 */