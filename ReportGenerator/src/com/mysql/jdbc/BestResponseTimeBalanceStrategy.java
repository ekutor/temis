/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class BestResponseTimeBalanceStrategy
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
/*  50 */     Map blackList = proxy.getGlobalBlacklist();
/*     */ 
/*  52 */     SQLException ex = null;
/*     */ 
/*  54 */     int attempts = 0;
/*     */     ConnectionImpl conn;
/*     */     while (true)
/*     */     {
/*  54 */       if (attempts >= numRetries) break label240; long minResponseTime = 9223372036854775807L;
/*     */ 
/*  57 */       int bestHostIndex = 0;
/*     */ 
/*  60 */       if (blackList.size() == configuredHosts.size()) {
/*  61 */         blackList = proxy.getGlobalBlacklist();
/*     */       }
/*     */ 
/*  64 */       for (int i = 0; i < responseTimes.length; i++) {
/*  65 */         long candidateResponseTime = responseTimes[i];
/*     */ 
/*  67 */         if ((candidateResponseTime >= minResponseTime) || (blackList.containsKey(configuredHosts.get(i))))
/*     */           continue;
/*  69 */         if (candidateResponseTime == 0L) {
/*  70 */           bestHostIndex = i;
/*     */ 
/*  72 */           break;
/*     */         }
/*     */ 
/*  75 */         bestHostIndex = i;
/*  76 */         minResponseTime = candidateResponseTime;
/*     */       }
/*     */ 
/*  80 */       String bestHost = (String)configuredHosts.get(bestHostIndex);
/*     */ 
/*  82 */       conn = (ConnectionImpl)liveConnections.get(bestHost);
/*     */ 
/*  84 */       if (conn != null) break;
/*     */       try {
/*  86 */         conn = proxy.createConnectionForHost(bestHost);
/*     */       } catch (SQLException sqlEx) {
/*  88 */         ex = sqlEx;
/*     */ 
/*  90 */         if (proxy.shouldExceptionTriggerFailover(sqlEx)) {
/*  91 */           proxy.addToGlobalBlacklist(bestHost);
/*  92 */           blackList.put(bestHost, null);
/*     */ 
/*  95 */           if (blackList.size() == configuredHosts.size()) {
/*  96 */             attempts++;
/*     */             try {
/*  98 */               Thread.sleep(250L);
/*     */             } catch (InterruptedException e) {
/*     */             }
/* 101 */             blackList = proxy.getGlobalBlacklist();
/*     */           }
/*     */ 
/* 104 */           continue;
/*     */         }
/*     */ 
/* 107 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */ 
/* 111 */     return conn;
/*     */ 
/* 114 */     label240: if (ex != null) {
/* 115 */       throw ex;
/*     */     }
/*     */ 
/* 118 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.BestResponseTimeBalanceStrategy
 * JD-Core Version:    0.6.0
 */