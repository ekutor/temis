/*     */ package com.mysql.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.Statement;
/*     */ import com.mysql.jdbc.StatementInterceptor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class ResultSetScannerInterceptor
/*     */   implements StatementInterceptor
/*     */ {
/*     */   protected Pattern regexP;
/*     */ 
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {
/*  45 */     String regexFromUser = props.getProperty("resultSetScannerRegex");
/*     */ 
/*  47 */     if ((regexFromUser == null) || (regexFromUser.length() == 0)) {
/*  48 */       throw new SQLException("resultSetScannerRegex must be configured, and must be > 0 characters");
/*     */     }
/*     */     try
/*     */     {
/*  52 */       this.regexP = Pattern.compile(regexFromUser);
/*     */     } catch (Throwable t) {
/*  54 */       SQLException sqlEx = new SQLException("Can't use configured regex due to underlying exception.");
/*  55 */       sqlEx.initCause(t);
/*     */ 
/*  57 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection)
/*     */     throws SQLException
/*     */   {
/*  67 */     ResultSetInternalMethods finalResultSet = originalResultSet;
/*     */ 
/*  69 */     return (ResultSetInternalMethods)Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class[] { ResultSetInternalMethods.class }, new InvocationHandler(finalResultSet)
/*     */     {
/*     */       public Object invoke(Object proxy, Method method, Object[] args)
/*     */         throws Throwable
/*     */       {
/*  76 */         Object invocationResult = method.invoke(this.val$finalResultSet, args);
/*     */ 
/*  78 */         String methodName = method.getName();
/*     */ 
/*  80 */         if (((invocationResult != null) && ((invocationResult instanceof String))) || ("getString".equals(methodName)) || ("getObject".equals(methodName)) || ("getObjectStoredProc".equals(methodName)))
/*     */         {
/*  84 */           Matcher matcher = ResultSetScannerInterceptor.this.regexP.matcher(invocationResult.toString());
/*     */ 
/*  86 */           if (matcher.matches()) {
/*  87 */             throw new SQLException("value disallowed by filter");
/*     */           }
/*     */         }
/*     */ 
/*  91 */         return invocationResult;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*     */     throws SQLException
/*     */   {
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean executeTopLevelOnly()
/*     */   {
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.interceptors.ResultSetScannerInterceptor
 * JD-Core Version:    0.6.0
 */