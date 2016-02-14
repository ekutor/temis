/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ import java.util.Set;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class PerVmServerConfigCacheFactory
/*    */   implements CacheAdapterFactory<String, Map<String, String>>
/*    */ {
/* 34 */   static final ConcurrentHashMap<String, Map<String, String>> serverConfigByUrl = new ConcurrentHashMap();
/*    */ 
/* 36 */   private static final CacheAdapter<String, Map<String, String>> serverConfigCache = new CacheAdapter()
/*    */   {
/*    */     public Map<String, String> get(String key) {
/* 39 */       return (Map)PerVmServerConfigCacheFactory.serverConfigByUrl.get(key);
/*    */     }
/*    */ 
/*    */     public void put(String key, Map<String, String> value) {
/* 43 */       PerVmServerConfigCacheFactory.serverConfigByUrl.putIfAbsent(key, value);
/*    */     }
/*    */ 
/*    */     public void invalidate(String key) {
/* 47 */       PerVmServerConfigCacheFactory.serverConfigByUrl.remove(key);
/*    */     }
/*    */ 
/*    */     public void invalidateAll(Set<String> keys) {
/* 51 */       for (String key : keys)
/* 52 */         PerVmServerConfigCacheFactory.serverConfigByUrl.remove(key);
/*    */     }
/*    */ 
/*    */     public void invalidateAll()
/*    */     {
/* 57 */       PerVmServerConfigCacheFactory.serverConfigByUrl.clear();
/*    */     }
/* 36 */   };
/*    */ 
/*    */   public CacheAdapter<String, Map<String, String>> getInstance(Connection forConn, String url, int cacheMaxSize, int maxKeySize, Properties connectionProperties)
/*    */     throws SQLException
/*    */   {
/* 63 */     return serverConfigCache;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.PerVmServerConfigCacheFactory
 * JD-Core Version:    0.6.0
 */