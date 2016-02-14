/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class LRUCache extends LinkedHashMap<Object, Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected int maxElements;
/*    */ 
/*    */   public LRUCache(int maxSize)
/*    */   {
/* 40 */     super(maxSize, 0.75F, true);
/* 41 */     this.maxElements = maxSize;
/*    */   }
/*    */ 
/*    */   protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest)
/*    */   {
/* 51 */     return size() > this.maxElements;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.util.LRUCache
 * JD-Core Version:    0.6.0
 */