/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import com.mysql.jdbc.util.LRUCache;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class PerConnectionLRUFactory
/*    */   implements CacheAdapterFactory<String, PreparedStatement.ParseInfo>
/*    */ {
/*    */   public CacheAdapter<String, PreparedStatement.ParseInfo> getInstance(Connection forConnection, String url, int cacheMaxSize, int maxKeySize, Properties connectionProperties)
/*    */     throws SQLException
/*    */   {
/* 39 */     return new PerConnectionLRU(forConnection, cacheMaxSize, maxKeySize);
/*    */   }
/*    */   class PerConnectionLRU implements CacheAdapter<String, PreparedStatement.ParseInfo> {
/*    */     private final int cacheSqlLimit;
/*    */     private final LRUCache cache;
/*    */     private final Connection conn;
/*    */ 
/* 49 */     protected PerConnectionLRU(Connection forConnection, int cacheMaxSize, int maxKeySize) { int cacheSize = cacheMaxSize;
/* 50 */       this.cacheSqlLimit = maxKeySize;
/* 51 */       this.cache = new LRUCache(cacheSize);
/* 52 */       this.conn = forConnection; }
/*    */ 
/*    */     public PreparedStatement.ParseInfo get(String key)
/*    */     {
/* 56 */       if ((key == null) || (key.length() > this.cacheSqlLimit)) {
/* 57 */         return null;
/*    */       }
/*    */ 
/* 60 */       synchronized (this.conn) {
/* 61 */         return (PreparedStatement.ParseInfo)this.cache.get(key);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void put(String key, PreparedStatement.ParseInfo value) {
/* 66 */       if ((key == null) || (key.length() > this.cacheSqlLimit)) {
/* 67 */         return;
/*    */       }
/*    */ 
/* 70 */       synchronized (this.conn) {
/* 71 */         this.cache.put(key, value);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void invalidate(String key) {
/* 76 */       synchronized (this.conn) {
/* 77 */         this.cache.remove(key);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void invalidateAll(Set<String> keys) {
/* 82 */       synchronized (this.conn) {
/* 83 */         for (String key : keys)
/* 84 */           this.cache.remove(key);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void invalidateAll()
/*    */     {
/* 91 */       synchronized (this.conn) {
/* 92 */         this.cache.clear();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.PerConnectionLRUFactory
 * JD-Core Version:    0.6.0
 */