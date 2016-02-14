/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ class WatchableOutputStream extends ByteArrayOutputStream
/*    */ {
/*    */   private OutputStreamWatcher watcher;
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 49 */     super.close();
/*    */ 
/* 51 */     if (this.watcher != null)
/* 52 */       this.watcher.streamClosed(this);
/*    */   }
/*    */ 
/*    */   public void setWatcher(OutputStreamWatcher watcher)
/*    */   {
/* 63 */     this.watcher = watcher;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.WatchableOutputStream
 * JD-Core Version:    0.6.0
 */