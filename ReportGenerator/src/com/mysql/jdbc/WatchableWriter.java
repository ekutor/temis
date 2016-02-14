/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.CharArrayWriter;
/*    */ 
/*    */ class WatchableWriter extends CharArrayWriter
/*    */ {
/*    */   private WriterWatcher watcher;
/*    */ 
/*    */   public void close()
/*    */   {
/* 48 */     super.close();
/*    */ 
/* 51 */     if (this.watcher != null)
/* 52 */       this.watcher.writerClosed(this);
/*    */   }
/*    */ 
/*    */   public void setWatcher(WriterWatcher watcher)
/*    */   {
/* 63 */     this.watcher = watcher;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.WatchableWriter
 * JD-Core Version:    0.6.0
 */