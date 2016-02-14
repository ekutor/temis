/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class RandomBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public ConnectionImpl pickConnection(LoadBalancingConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries)
/*     */     throws SQLException
/*     */   {
/*  49 */     int numHosts = configuredHosts.size();
/*     */ 
/*  51 */     SQLException ex = null;
/*     */ 
/*  53 */     List whiteList = new ArrayList(numHosts);
/*  54 */     whiteList.addAll(configuredHosts);
/*     */ 
/*  56 */     Map blackList = proxy.getGlobalBlacklist();
/*     */ 
/*  58 */     whiteList.removeAll(blackList.keySet());
/*     */ 
/*  60 */     Map whiteListMap = getArrayIndexMap(whiteList);
/*     */ 
/*  63 */     int attempts = 0;
/*     */     ConnectionImpl conn;
/*     */     while (true)
/*     */     {
/*  63 */       if (attempts >= numRetries) break label291; int random = (int)Math.floor(Math.random() * whiteList.size());
/*  65 */       if (whiteList.size() == 0) {
/*  66 */         throw SQLError.createSQLException("No hosts configured", null);
/*     */       }
/*     */ 
/*  69 */       String hostPortSpec = (String)whiteList.get(random);
/*     */ 
/*  71 */       conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
/*     */ 
/*  73 */       if (conn != null) break;
/*     */       try {
/*  75 */         conn = proxy.createConnectionForHost(hostPortSpec);
/*     */       } catch (SQLException sqlEx) {
/*  77 */         ex = sqlEx;
/*     */ 
/*  79 */         if (proxy.shouldExceptionTriggerFailover(sqlEx))
/*     */         {
/*  81 */           Integer whiteListIndex = (Integer)whiteListMap.get(hostPortSpec);
/*     */ 
/*  84 */           if (whiteListIndex != null) {
/*  85 */             whiteList.remove(whiteListIndex.intValue());
/*  86 */             whiteListMap = getArrayIndexMap(whiteList);
/*     */           }
/*  88 */           proxy.addToGlobalBlacklist(hostPortSpec);
/*     */ 
/*  90 */           if (whiteList.size() == 0) {
/*  91 */             attempts++;
/*     */             try {
/*  93 */               Thread.sleep(250L);
/*     */             }
/*     */             catch (InterruptedException e)
/*     */             {
/*     */             }
/*  98 */             whiteListMap = new HashMap(numHosts);
/*  99 */             whiteList.addAll(configuredHosts);
/* 100 */             blackList = proxy.getGlobalBlacklist();
/*     */ 
/* 102 */             whiteList.removeAll(blackList.keySet());
/* 103 */             whiteListMap = getArrayIndexMap(whiteList);
/*     */           }
/*     */ 
/* 106 */           continue;
/*     */         }
/*     */ 
/* 109 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */ 
/* 113 */     return conn;
/*     */ 
/* 116 */     label291: if (ex != null) {
/* 117 */       throw ex;
/*     */     }
/*     */ 
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   private Map<String, Integer> getArrayIndexMap(List<String> l) {
/* 124 */     Map m = new HashMap(l.size());
/* 125 */     for (int i = 0; i < l.size(); i++) {
/* 126 */       m.put(l.get(i), Integer.valueOf(i));
/*     */     }
/* 128 */     return m;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.RandomBalanceStrategy
 * JD-Core Version:    0.6.0
 */