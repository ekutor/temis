/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class AbandonedConnectionCleanupThread extends Thread
/*    */ {
/* 29 */   private static boolean running = true;
/* 30 */   private static Thread threadRef = null;
/*    */ 
/*    */   public AbandonedConnectionCleanupThread() {
/* 33 */     super("Abandoned connection cleanup thread");
/*    */   }
/*    */ 
/*    */   public void run() {
/* 37 */     threadRef = this;
/* 38 */     threadRef.setContextClassLoader(null);
/* 39 */     while (running)
/*    */       try {
/* 41 */         Reference ref = NonRegisteringDriver.refQueue.remove(100L);
/* 42 */         if (ref != null)
/*    */           try {
/* 44 */             ((NonRegisteringDriver.ConnectionPhantomReference)ref).cleanup();
/*    */           } finally {
/* 46 */             NonRegisteringDriver.connectionPhantomRefs.remove(ref);
/*    */           }
/*    */       }
/*    */       catch (Exception ex)
/*    */       {
/*    */       }
/*    */   }
/*    */ 
/*    */   public static void shutdown()
/*    */     throws InterruptedException
/*    */   {
/* 57 */     running = false;
/* 58 */     if (threadRef != null) {
/* 59 */       threadRef.interrupt();
/* 60 */       threadRef.join();
/* 61 */       threadRef = null;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.AbandonedConnectionCleanupThread
 * JD-Core Version:    0.6.0
 */