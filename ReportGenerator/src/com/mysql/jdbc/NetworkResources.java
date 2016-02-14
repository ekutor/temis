/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.net.Socket;
/*    */ 
/*    */ class NetworkResources
/*    */ {
/*    */   private final Socket mysqlConnection;
/*    */   private final InputStream mysqlInput;
/*    */   private final OutputStream mysqlOutput;
/*    */ 
/*    */   protected NetworkResources(Socket mysqlConnection, InputStream mysqlInput, OutputStream mysqlOutput)
/*    */   {
/* 40 */     this.mysqlConnection = mysqlConnection;
/* 41 */     this.mysqlInput = mysqlInput;
/* 42 */     this.mysqlOutput = mysqlOutput;
/*    */   }
/*    */ 
/*    */   protected final void forceClose()
/*    */   {
/*    */     try
/*    */     {
/*    */       try
/*    */       {
/* 51 */         if (this.mysqlInput != null)
/* 52 */           this.mysqlInput.close();
/*    */       }
/*    */       finally {
/* 55 */         if ((this.mysqlConnection != null) && (!this.mysqlConnection.isClosed()) && (!this.mysqlConnection.isInputShutdown()))
/*    */           try {
/* 57 */             this.mysqlConnection.shutdownInput();
/*    */           }
/*    */           catch (UnsupportedOperationException ex)
/*    */           {
/*    */           }
/*    */       }
/*    */     }
/*    */     catch (IOException ioEx)
/*    */     {
/*    */     }
/*    */     try {
/*    */       try {
/* 69 */         if (this.mysqlOutput != null)
/* 70 */           this.mysqlOutput.close();
/*    */       }
/*    */       finally {
/* 73 */         if ((this.mysqlConnection != null) && (!this.mysqlConnection.isClosed()) && (!this.mysqlConnection.isOutputShutdown()))
/*    */           try {
/* 75 */             this.mysqlConnection.shutdownOutput();
/*    */           }
/*    */           catch (UnsupportedOperationException ex)
/*    */           {
/*    */           }
/*    */       }
/*    */     }
/*    */     catch (IOException ioEx)
/*    */     {
/*    */     }
/*    */     try {
/* 86 */       if (this.mysqlConnection != null)
/* 87 */         this.mysqlConnection.close();
/*    */     }
/*    */     catch (IOException ioEx)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NetworkResources
 * JD-Core Version:    0.6.0
 */