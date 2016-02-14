/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class Blob
/*     */   implements java.sql.Blob, OutputStreamWatcher
/*     */ {
/*  60 */   private byte[] binaryData = null;
/*  61 */   private boolean isClosed = false;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   Blob(ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  68 */     setBinaryData(Constants.EMPTY_BYTE_ARRAY);
/*  69 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   Blob(byte[] data, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  79 */     setBinaryData(data);
/*  80 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   Blob(byte[] data, ResultSetInternalMethods creatorResultSetToSet, int columnIndexToSet)
/*     */   {
/*  94 */     setBinaryData(data);
/*     */   }
/*     */ 
/*     */   private synchronized byte[] getBinaryData() {
/*  98 */     return this.binaryData;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 110 */     checkClosed();
/*     */ 
/* 112 */     return new ByteArrayInputStream(getBinaryData());
/*     */   }
/*     */ 
/*     */   public synchronized byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 131 */     checkClosed();
/*     */ 
/* 133 */     if (pos < 1L) {
/* 134 */       throw SQLError.createSQLException(Messages.getString("Blob.2"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 138 */     pos -= 1L;
/*     */ 
/* 140 */     if (pos > this.binaryData.length) {
/* 141 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 145 */     if (pos + length > this.binaryData.length) {
/* 146 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 150 */     byte[] newData = new byte[length];
/* 151 */     System.arraycopy(getBinaryData(), (int)pos, newData, 0, length);
/*     */ 
/* 153 */     return newData;
/*     */   }
/*     */ 
/*     */   public synchronized long length()
/*     */     throws SQLException
/*     */   {
/* 166 */     checkClosed();
/*     */ 
/* 168 */     return getBinaryData().length;
/*     */   }
/*     */ 
/*     */   public synchronized long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 175 */     throw SQLError.createSQLException("Not implemented", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public synchronized long position(java.sql.Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 193 */     checkClosed();
/*     */ 
/* 195 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */ 
/*     */   private synchronized void setBinaryData(byte[] newBinaryData) {
/* 199 */     this.binaryData = newBinaryData;
/*     */   }
/*     */ 
/*     */   public synchronized OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 207 */     checkClosed();
/*     */ 
/* 209 */     if (indexToWriteAt < 1L) {
/* 210 */       throw SQLError.createSQLException(Messages.getString("Blob.0"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 214 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 215 */     bytesOut.setWatcher(this);
/*     */ 
/* 217 */     if (indexToWriteAt > 0L) {
/* 218 */       bytesOut.write(this.binaryData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */ 
/* 221 */     return bytesOut;
/*     */   }
/*     */ 
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 228 */     checkClosed();
/*     */ 
/* 230 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 238 */     checkClosed();
/*     */ 
/* 240 */     OutputStream bytesOut = setBinaryStream(writeAt);
/*     */     try
/*     */     {
/* 243 */       bytesOut.write(bytes, offset, length);
/*     */     } catch (IOException ioEx) {
/* 245 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Blob.1"), "S1000", this.exceptionInterceptor);
/*     */ 
/* 247 */       sqlEx.initCause(ioEx);
/*     */ 
/* 249 */       throw sqlEx;
/*     */     } finally {
/*     */       try {
/* 252 */         bytesOut.close();
/*     */       }
/*     */       catch (IOException doNothing)
/*     */       {
/*     */       }
/*     */     }
/* 258 */     return length;
/*     */   }
/*     */ 
/*     */   public synchronized void streamClosed(byte[] byteData)
/*     */   {
/* 265 */     this.binaryData = byteData;
/*     */   }
/*     */ 
/*     */   public synchronized void streamClosed(WatchableOutputStream out)
/*     */   {
/* 272 */     int streamSize = out.size();
/*     */ 
/* 274 */     if (streamSize < this.binaryData.length) {
/* 275 */       out.write(this.binaryData, streamSize, this.binaryData.length - streamSize);
/*     */     }
/*     */ 
/* 279 */     this.binaryData = out.toByteArray();
/*     */   }
/*     */ 
/*     */   public synchronized void truncate(long len)
/*     */     throws SQLException
/*     */   {
/* 301 */     checkClosed();
/*     */ 
/* 303 */     if (len < 0L) {
/* 304 */       throw SQLError.createSQLException("\"len\" argument can not be < 1.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 308 */     if (len > this.binaryData.length) {
/* 309 */       throw SQLError.createSQLException("\"len\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 316 */     byte[] newData = new byte[(int)len];
/* 317 */     System.arraycopy(getBinaryData(), 0, newData, 0, (int)len);
/* 318 */     this.binaryData = newData;
/*     */   }
/*     */ 
/*     */   public synchronized void free()
/*     */     throws SQLException
/*     */   {
/* 340 */     this.binaryData = null;
/* 341 */     this.isClosed = true;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getBinaryStream(long pos, long length)
/*     */     throws SQLException
/*     */   {
/* 361 */     checkClosed();
/*     */ 
/* 363 */     if (pos < 1L) {
/* 364 */       throw SQLError.createSQLException("\"pos\" argument can not be < 1.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 368 */     pos -= 1L;
/*     */ 
/* 370 */     if (pos > this.binaryData.length) {
/* 371 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 375 */     if (pos + length > this.binaryData.length) {
/* 376 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/* 380 */     return new ByteArrayInputStream(getBinaryData(), (int)pos, (int)length);
/*     */   }
/*     */ 
/*     */   private synchronized void checkClosed() throws SQLException {
/* 384 */     if (this.isClosed)
/* 385 */       throw SQLError.createSQLException("Invalid operation on closed BLOB", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Blob
 * JD-Core Version:    0.6.0
 */