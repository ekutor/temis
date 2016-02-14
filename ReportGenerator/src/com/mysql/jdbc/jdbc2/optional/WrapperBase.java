/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
/*     */ 
/*     */ abstract class WrapperBase
/*     */ {
/*     */   protected MysqlPooledConnection pooledConnection;
/*  69 */   protected Map unwrappedInterfaces = null;
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   protected void checkAndFireConnectionError(SQLException sqlEx)
/*     */     throws SQLException
/*     */   {
/*  58 */     if ((this.pooledConnection != null) && 
/*  59 */       ("08S01".equals(sqlEx.getSQLState())))
/*     */     {
/*  61 */       this.pooledConnection.callConnectionEventListeners(1, sqlEx);
/*     */     }
/*     */ 
/*  66 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   protected WrapperBase(MysqlPooledConnection pooledConnection)
/*     */   {
/*  73 */     this.pooledConnection = pooledConnection;
/*  74 */     this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
/*     */   }
/*     */ 
/*     */   protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler {
/*  78 */     Object invokeOn = null;
/*     */ 
/*     */     public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
/*  81 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */ 
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */     {
/*  86 */       Object result = null;
/*     */       try
/*     */       {
/*  89 */         result = method.invoke(this.invokeOn, args);
/*     */ 
/*  91 */         if (result != null)
/*  92 */           result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/*  96 */         if ((e.getTargetException() instanceof SQLException)) {
/*  97 */           WrapperBase.this.checkAndFireConnectionError((SQLException)e.getTargetException());
/*     */         }
/*     */         else {
/* 100 */           throw e;
/*     */         }
/*     */       }
/*     */ 
/* 104 */       return result;
/*     */     }
/*     */ 
/*     */     private Object proxyIfInterfaceIsJdbc(Object toProxy, Class<?> clazz)
/*     */     {
/* 116 */       Class[] interfaces = clazz.getInterfaces();
/*     */ 
/* 118 */       Class[] arr$ = interfaces; int len$ = arr$.length; int i$ = 0; if (i$ < len$) { Class iclass = arr$[i$];
/* 119 */         String packageName = iclass.getPackage().getName();
/*     */ 
/* 121 */         if (("java.sql".equals(packageName)) || ("javax.sql".equals(packageName)))
/*     */         {
/* 123 */           return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(WrapperBase.this, toProxy));
/*     */         }
/*     */ 
/* 128 */         return proxyIfInterfaceIsJdbc(toProxy, iclass);
/*     */       }
/*     */ 
/* 131 */       return toProxy;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.WrapperBase
 * JD-Core Version:    0.6.0
 */