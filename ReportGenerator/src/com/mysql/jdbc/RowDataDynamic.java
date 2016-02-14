/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public class RowDataDynamic
/*     */   implements RowData
/*     */ {
/*     */   private int columnCount;
/*     */   private Field[] metadata;
/*  55 */   private int index = -1;
/*     */   private MysqlIO io;
/*  59 */   private boolean isAfterEnd = false;
/*     */ 
/*  61 */   private boolean noMoreRows = false;
/*     */ 
/*  63 */   private boolean isBinaryEncoded = false;
/*     */   private ResultSetRow nextRow;
/*     */   private ResultSetImpl owner;
/*  69 */   private boolean streamerClosed = false;
/*     */ 
/*  71 */   private boolean wasEmpty = false;
/*     */   private boolean useBufferRowExplicit;
/*     */   private boolean moreResultsExisted;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   public RowDataDynamic(MysqlIO io, int colCount, Field[] fields, boolean isBinaryEncoded)
/*     */     throws SQLException
/*     */   {
/*  95 */     this.io = io;
/*  96 */     this.columnCount = colCount;
/*  97 */     this.isBinaryEncoded = isBinaryEncoded;
/*  98 */     this.metadata = fields;
/*  99 */     this.exceptionInterceptor = this.io.getExceptionInterceptor();
/* 100 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
/*     */   }
/*     */ 
/*     */   public void addRow(ResultSetRow row)
/*     */     throws SQLException
/*     */   {
/* 112 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 122 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 132 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 142 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 157 */     Object mutex = this;
/*     */ 
/* 159 */     MySQLConnection conn = null;
/*     */ 
/* 161 */     if (this.owner != null) {
/* 162 */       conn = this.owner.connection;
/*     */ 
/* 164 */       if (conn != null) {
/* 165 */         mutex = conn;
/*     */       }
/*     */     }
/*     */ 
/* 169 */     boolean hadMore = false;
/* 170 */     int howMuchMore = 0;
/*     */ 
/* 172 */     synchronized (mutex)
/*     */     {
/* 174 */       while (next() != null) {
/* 175 */         hadMore = true;
/* 176 */         howMuchMore++;
/*     */ 
/* 178 */         if (howMuchMore % 100 == 0) {
/* 179 */           Thread.yield();
/*     */         }
/*     */       }
/*     */ 
/* 183 */       if (conn != null) {
/* 184 */         if ((!conn.getClobberStreamingResults()) && (conn.getNetTimeoutForStreamingResults() > 0))
/*     */         {
/* 186 */           String oldValue = conn.getServerVariable("net_write_timeout");
/*     */ 
/* 189 */           if ((oldValue == null) || (oldValue.length() == 0)) {
/* 190 */             oldValue = "60";
/*     */           }
/*     */ 
/* 193 */           this.io.clearInputStream();
/*     */ 
/* 195 */           Statement stmt = null;
/*     */           try
/*     */           {
/* 198 */             stmt = conn.createStatement();
/* 199 */             ((StatementImpl)stmt).executeSimpleNonQuery(conn, "SET net_write_timeout=" + oldValue);
/*     */           } finally {
/* 201 */             if (stmt != null) {
/* 202 */               stmt.close();
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 207 */         if ((conn.getUseUsageAdvisor()) && 
/* 208 */           (hadMore))
/*     */         {
/* 210 */           ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(conn);
/*     */ 
/* 213 */           eventSink.consumeEvent(new ProfilerEvent(0, "", this.owner.owningStatement == null ? "N/A" : this.owner.owningStatement.currentCatalog, this.owner.connectionId, this.owner.owningStatement == null ? -1 : this.owner.owningStatement.getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, null, Messages.getString("RowDataDynamic.2") + howMuchMore + Messages.getString("RowDataDynamic.3") + Messages.getString("RowDataDynamic.4") + Messages.getString("RowDataDynamic.5") + Messages.getString("RowDataDynamic.6") + this.owner.pointOfOrigin));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 244 */     this.metadata = null;
/* 245 */     this.owner = null;
/*     */   }
/*     */ 
/*     */   public ResultSetRow getAt(int ind)
/*     */     throws SQLException
/*     */   {
/* 258 */     notSupported();
/*     */ 
/* 260 */     return null;
/*     */   }
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */     throws SQLException
/*     */   {
/* 271 */     notSupported();
/*     */ 
/* 273 */     return -1;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 280 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 291 */     boolean hasNext = this.nextRow != null;
/*     */ 
/* 293 */     if ((!hasNext) && (!this.streamerClosed)) {
/* 294 */       this.io.closeStreamer(this);
/* 295 */       this.streamerClosed = true;
/*     */     }
/*     */ 
/* 298 */     return hasNext;
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */     throws SQLException
/*     */   {
/* 309 */     return this.isAfterEnd;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 320 */     return this.index < 0;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 332 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws SQLException
/*     */   {
/* 343 */     notSupported();
/*     */ 
/* 345 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 356 */     notSupported();
/*     */ 
/* 358 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 369 */     notSupported();
/*     */ 
/* 371 */     return false;
/*     */   }
/*     */ 
/*     */   public void moveRowRelative(int rows)
/*     */     throws SQLException
/*     */   {
/* 383 */     notSupported();
/*     */   }
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 396 */     nextRecord();
/*     */ 
/* 398 */     if ((this.nextRow == null) && (!this.streamerClosed) && (!this.moreResultsExisted)) {
/* 399 */       this.io.closeStreamer(this);
/* 400 */       this.streamerClosed = true;
/*     */     }
/*     */ 
/* 403 */     if ((this.nextRow != null) && 
/* 404 */       (this.index != 2147483647)) {
/* 405 */       this.index += 1;
/*     */     }
/*     */ 
/* 409 */     return this.nextRow;
/*     */   }
/*     */ 
/*     */   private void nextRecord() throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 416 */       if (!this.noMoreRows) {
/* 417 */         this.nextRow = this.io.nextRow(this.metadata, this.columnCount, this.isBinaryEncoded, 1007, true, this.useBufferRowExplicit, true, null);
/*     */ 
/* 422 */         if (this.nextRow == null) {
/* 423 */           this.noMoreRows = true;
/* 424 */           this.isAfterEnd = true;
/* 425 */           this.moreResultsExisted = this.io.tackOnMoreStreamingResults(this.owner);
/*     */ 
/* 427 */           if (this.index == -1)
/* 428 */             this.wasEmpty = true;
/*     */         }
/*     */       }
/*     */       else {
/* 432 */         this.isAfterEnd = true;
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 435 */       if ((sqlEx instanceof StreamingNotifiable)) {
/* 436 */         ((StreamingNotifiable)sqlEx).setWasStreamingResults();
/*     */       }
/*     */ 
/* 440 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 442 */       String exceptionType = ex.getClass().getName();
/* 443 */       String exceptionMessage = ex.getMessage();
/*     */ 
/* 445 */       exceptionMessage = exceptionMessage + Messages.getString("RowDataDynamic.7");
/* 446 */       exceptionMessage = exceptionMessage + Util.stackTraceToString(ex);
/*     */ 
/* 448 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("RowDataDynamic.8") + exceptionType + Messages.getString("RowDataDynamic.9") + exceptionMessage, "S1000", this.exceptionInterceptor);
/*     */ 
/* 452 */       sqlEx.initCause(ex);
/*     */ 
/* 454 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notSupported() throws SQLException {
/* 459 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */   public void removeRow(int ind)
/*     */     throws SQLException
/*     */   {
/* 471 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void setCurrentRow(int rowNumber)
/*     */     throws SQLException
/*     */   {
/* 483 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 490 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 499 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty() {
/* 503 */     return this.wasEmpty;
/*     */   }
/*     */ 
/*     */   public void setMetadata(Field[] metadata) {
/* 507 */     this.metadata = metadata;
/*     */   }
/*     */ 
/*     */   class OperationNotSupportedException extends SQLException
/*     */   {
/*     */     static final long serialVersionUID = 5582227030787355276L;
/*     */ 
/*     */     OperationNotSupportedException()
/*     */     {
/*  46 */       super("S1009");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.RowDataDynamic
 * JD-Core Version:    0.6.0
 */