/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class FailoverConnectionProxy extends LoadBalancingConnectionProxy
/*     */ {
/*     */   boolean failedOver;
/*     */   boolean hasTriedMaster;
/*     */   private long masterFailTimeMillis;
/*     */   boolean preferSlaveDuringFailover;
/*     */   private String primaryHostPortSpec;
/*     */   private long queriesBeforeRetryMaster;
/*     */   long queriesIssuedFailedOver;
/*     */   private int secondsBeforeRetryMaster;
/*     */ 
/*     */   FailoverConnectionProxy(List<String> hosts, Properties props)
/*     */     throws SQLException
/*     */   {
/*  67 */     super(hosts, props);
/*  68 */     ConnectionPropertiesImpl connectionProps = new ConnectionPropertiesImpl();
/*  69 */     connectionProps.initializeProperties(props);
/*     */ 
/*  71 */     this.queriesBeforeRetryMaster = connectionProps.getQueriesBeforeRetryMaster();
/*  72 */     this.secondsBeforeRetryMaster = connectionProps.getSecondsBeforeRetryMaster();
/*  73 */     this.preferSlaveDuringFailover = false;
/*     */   }
/*     */ 
/*     */   protected LoadBalancingConnectionProxy.ConnectionErrorFiringInvocationHandler createConnectionProxy(Object toProxy)
/*     */   {
/*  78 */     return new FailoverInvocationHandler(toProxy);
/*     */   }
/*     */ 
/*     */   synchronized void dealWithInvocationException(InvocationTargetException e)
/*     */     throws SQLException, Throwable, InvocationTargetException
/*     */   {
/*  86 */     Throwable t = e.getTargetException();
/*     */ 
/*  88 */     if (t != null) {
/*  89 */       if (this.failedOver) {
/*  90 */         createPrimaryConnection();
/*     */ 
/*  92 */         if (this.currentConn != null) {
/*  93 */           throw t;
/*     */         }
/*     */       }
/*     */ 
/*  97 */       failOver();
/*     */ 
/*  99 */       throw t;
/*     */     }
/*     */ 
/* 102 */     throw e;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */   {
/* 107 */     String methodName = method.getName();
/*     */ 
/* 109 */     if ("setPreferSlaveDuringFailover".equals(methodName)) {
/* 110 */       this.preferSlaveDuringFailover = ((Boolean)args[0]).booleanValue();
/* 111 */     } else if ("clearHasTriedMaster".equals(methodName)) {
/* 112 */       this.hasTriedMaster = false; } else {
/* 113 */       if ("hasTriedMaster".equals(methodName))
/* 114 */         return Boolean.valueOf(this.hasTriedMaster);
/* 115 */       if ("isMasterConnection".equals(methodName))
/* 116 */         return Boolean.valueOf(!this.failedOver);
/* 117 */       if ("isSlaveConnection".equals(methodName))
/* 118 */         return Boolean.valueOf(this.failedOver);
/* 119 */       if ("setReadOnly".equals(methodName)) {
/* 120 */         if (this.failedOver)
/* 121 */           return null;
/*     */       } else {
/* 123 */         if (("setAutoCommit".equals(methodName)) && (this.failedOver) && (shouldFallBack()) && (Boolean.TRUE.equals(args[0])) && (this.failedOver))
/*     */         {
/* 125 */           createPrimaryConnection();
/*     */ 
/* 127 */           return super.invoke(proxy, method, args, this.failedOver);
/* 128 */         }if ("hashCode".equals(methodName))
/* 129 */           return Integer.valueOf(hashCode());
/* 130 */         if ("equals".equals(methodName)) {
/* 131 */           if ((args[0] instanceof Proxy)) {
/* 132 */             return Boolean.valueOf(((Proxy)args[0]).equals(this));
/*     */           }
/* 134 */           return Boolean.valueOf(equals(args[0]));
/*     */         }
/*     */       }
/*     */     }
/* 136 */     return super.invoke(proxy, method, args, this.failedOver);
/*     */   }
/*     */ 
/*     */   private synchronized void createPrimaryConnection() throws SQLException {
/*     */     try {
/* 141 */       this.currentConn = createConnectionForHost(this.primaryHostPortSpec);
/* 142 */       this.failedOver = false;
/* 143 */       this.hasTriedMaster = true;
/*     */ 
/* 146 */       this.queriesIssuedFailedOver = 0L;
/*     */     } catch (SQLException sqlEx) {
/* 148 */       this.failedOver = true;
/*     */ 
/* 150 */       if (this.currentConn != null)
/* 151 */         this.currentConn.getLog().logWarn("Connection to primary host failed", sqlEx);
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void invalidateCurrentConnection() throws SQLException
/*     */   {
/* 157 */     if (!this.failedOver) {
/* 158 */       this.failedOver = true;
/* 159 */       this.queriesIssuedFailedOver = 0L;
/* 160 */       this.masterFailTimeMillis = System.currentTimeMillis();
/*     */     }
/* 162 */     super.invalidateCurrentConnection();
/*     */   }
/*     */ 
/*     */   protected synchronized void pickNewConnection() throws SQLException {
/* 166 */     if (this.primaryHostPortSpec == null) {
/* 167 */       this.primaryHostPortSpec = ((String)this.hostList.remove(0));
/*     */     }
/*     */ 
/* 170 */     if ((this.currentConn == null) || ((this.failedOver) && (shouldFallBack()))) {
/* 171 */       createPrimaryConnection();
/*     */ 
/* 173 */       if (this.currentConn != null) {
/* 174 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 178 */     failOver();
/*     */   }
/*     */ 
/*     */   private synchronized void failOver() throws SQLException {
/* 182 */     if (this.failedOver) {
/* 183 */       Iterator iter = this.liveConnections.entrySet().iterator();
/*     */ 
/* 185 */       while (iter.hasNext()) {
/* 186 */         Map.Entry entry = (Map.Entry)iter.next();
/* 187 */         ((ConnectionImpl)entry.getValue()).close();
/*     */       }
/*     */ 
/* 190 */       this.liveConnections.clear();
/*     */     }
/*     */ 
/* 193 */     super.pickNewConnection();
/*     */ 
/* 195 */     if (this.currentConn.getFailOverReadOnly())
/* 196 */       this.currentConn.setReadOnly(true);
/*     */     else {
/* 198 */       this.currentConn.setReadOnly(false);
/*     */     }
/*     */ 
/* 201 */     this.failedOver = true;
/*     */   }
/*     */ 
/*     */   private boolean shouldFallBack()
/*     */   {
/* 212 */     long secondsSinceFailedOver = (System.currentTimeMillis() - this.masterFailTimeMillis) / 1000L;
/*     */ 
/* 214 */     if (secondsSinceFailedOver >= this.secondsBeforeRetryMaster)
/*     */     {
/* 216 */       this.masterFailTimeMillis = System.currentTimeMillis();
/*     */ 
/* 218 */       return true;
/*     */     }
/* 220 */     return (this.queriesBeforeRetryMaster != 0L) && (this.queriesIssuedFailedOver >= this.queriesBeforeRetryMaster);
/*     */   }
/*     */ 
/*     */   class FailoverInvocationHandler extends LoadBalancingConnectionProxy.ConnectionErrorFiringInvocationHandler
/*     */   {
/*     */     public FailoverInvocationHandler(Object toInvokeOn)
/*     */     {
/*  41 */       super(toInvokeOn);
/*     */     }
/*     */ 
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */     {
/*  46 */       String methodName = method.getName();
/*     */ 
/*  48 */       if ((FailoverConnectionProxy.this.failedOver) && (methodName.indexOf("execute") != -1)) {
/*  49 */         FailoverConnectionProxy.this.queriesIssuedFailedOver += 1L;
/*     */       }
/*     */ 
/*  52 */       return super.invoke(proxy, method, args);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.FailoverConnectionProxy
 * JD-Core Version:    0.6.0
 */