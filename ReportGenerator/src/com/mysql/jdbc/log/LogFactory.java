/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class LogFactory
/*     */ {
/*     */   public static Log getLogger(String className, String instanceName, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  59 */     if (className == null) {
/*  60 */       throw SQLError.createSQLException("Logger class can not be NULL", "S1009", exceptionInterceptor);
/*     */     }
/*     */ 
/*  64 */     if (instanceName == null) {
/*  65 */       throw SQLError.createSQLException("Logger instance name can not be NULL", "S1009", exceptionInterceptor);
/*     */     }
/*     */     SQLException sqlEx;
/*     */     try
/*     */     {
/*  71 */       Class loggerClass = null;
/*     */       try
/*     */       {
/*  74 */         loggerClass = Class.forName(className);
/*     */       } catch (ClassNotFoundException nfe) {
/*  76 */         loggerClass = Class.forName(Log.class.getPackage().getName() + "." + className);
/*     */       }
/*     */ 
/*  80 */       Constructor constructor = loggerClass.getConstructor(new Class[] { String.class });
/*     */ 
/*  83 */       return (Log)constructor.newInstance(new Object[] { instanceName });
/*     */     } catch (ClassNotFoundException cnfe) {
/*  85 */       SQLException sqlEx = SQLError.createSQLException("Unable to load class for logger '" + className + "'", "S1009", exceptionInterceptor);
/*     */ 
/*  88 */       sqlEx.initCause(cnfe);
/*     */ 
/*  90 */       throw sqlEx;
/*     */     } catch (NoSuchMethodException nsme) {
/*  92 */       SQLException sqlEx = SQLError.createSQLException("Logger class does not have a single-arg constructor that takes an instance name", "S1009", exceptionInterceptor);
/*     */ 
/*  96 */       sqlEx.initCause(nsme);
/*     */ 
/*  98 */       throw sqlEx;
/*     */     } catch (InstantiationException inse) {
/* 100 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
/*     */ 
/* 104 */       sqlEx.initCause(inse);
/*     */ 
/* 106 */       throw sqlEx;
/*     */     } catch (InvocationTargetException ite) {
/* 108 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
/*     */ 
/* 112 */       sqlEx.initCause(ite);
/*     */ 
/* 114 */       throw sqlEx;
/*     */     } catch (IllegalAccessException iae) {
/* 116 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', constructor not public", "S1009", exceptionInterceptor);
/*     */ 
/* 120 */       sqlEx.initCause(iae);
/*     */ 
/* 122 */       throw sqlEx;
/*     */     } catch (ClassCastException cce) {
/* 124 */       sqlEx = SQLError.createSQLException("Logger class '" + className + "' does not implement the '" + Log.class.getName() + "' interface", "S1009", exceptionInterceptor);
/*     */ 
/* 128 */       sqlEx.initCause(cce);
/*     */     }
/* 130 */     throw sqlEx;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.log.LogFactory
 * JD-Core Version:    0.6.0
 */