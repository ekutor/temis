/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public abstract class IterateBlock<T>
/*    */ {
/*    */   DatabaseMetaData.IteratorWithCleanup<T> iteratorWithCleanup;
/*    */   Iterator<T> javaIterator;
/* 35 */   boolean stopIterating = false;
/*    */ 
/*    */   IterateBlock(DatabaseMetaData.IteratorWithCleanup<T> i) {
/* 38 */     this.iteratorWithCleanup = i;
/* 39 */     this.javaIterator = null;
/*    */   }
/*    */ 
/*    */   IterateBlock(Iterator<T> i) {
/* 43 */     this.javaIterator = i;
/* 44 */     this.iteratorWithCleanup = null;
/*    */   }
/*    */ 
/*    */   public void doForAll() throws SQLException {
/* 48 */     if (this.iteratorWithCleanup != null)
/*    */       try {
/* 50 */         while (this.iteratorWithCleanup.hasNext()) {
/* 51 */           forEach(this.iteratorWithCleanup.next());
/*    */ 
/* 53 */           if (this.stopIterating)
/* 54 */             break;
/*    */         }
/*    */       }
/*    */       finally {
/* 58 */         this.iteratorWithCleanup.close();
/*    */       }
/*    */     else
/* 61 */       while (this.javaIterator.hasNext()) {
/* 62 */         forEach(this.javaIterator.next());
/*    */ 
/* 64 */         if (this.stopIterating)
/* 65 */           break;
/*    */       }
/*    */   }
/*    */ 
/*    */   abstract void forEach(T paramT) throws SQLException;
/*    */ 
/*    */   public final boolean fullIteration()
/*    */   {
/* 74 */     return !this.stopIterating;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.IterateBlock
 * JD-Core Version:    0.6.0
 */