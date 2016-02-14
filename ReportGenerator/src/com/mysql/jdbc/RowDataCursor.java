/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RowDataCursor
/*     */   implements RowData
/*     */ {
/*     */   private static final int BEFORE_START_OF_ROWS = -1;
/*     */   private List<ResultSetRow> fetchedRows;
/*  50 */   private int currentPositionInEntireResult = -1;
/*     */ 
/*  56 */   private int currentPositionInFetchedRows = -1;
/*     */   private ResultSetImpl owner;
/*  66 */   private boolean lastRowFetched = false;
/*     */   private Field[] metadata;
/*     */   private MysqlIO mysql;
/*     */   private long statementIdOnServer;
/*     */   private ServerPreparedStatement prepStmt;
/*     */   private static final int SERVER_STATUS_LAST_ROW_SENT = 128;
/*  99 */   private boolean firstFetchCompleted = false;
/*     */ 
/* 101 */   private boolean wasEmpty = false;
/*     */ 
/* 103 */   private boolean useBufferRowExplicit = false;
/*     */ 
/*     */   public RowDataCursor(MysqlIO ioChannel, ServerPreparedStatement creatingStatement, Field[] metadata)
/*     */   {
/* 117 */     this.currentPositionInEntireResult = -1;
/* 118 */     this.metadata = metadata;
/* 119 */     this.mysql = ioChannel;
/* 120 */     this.statementIdOnServer = creatingStatement.getServerStatementId();
/* 121 */     this.prepStmt = creatingStatement;
/* 122 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */   {
/* 132 */     return (this.lastRowFetched) && (this.currentPositionInFetchedRows > this.fetchedRows.size());
/*     */   }
/*     */ 
/*     */   public ResultSetRow getAt(int ind)
/*     */     throws SQLException
/*     */   {
/* 146 */     notSupported();
/*     */ 
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 159 */     return this.currentPositionInEntireResult < 0;
/*     */   }
/*     */ 
/*     */   public void setCurrentRow(int rowNumber)
/*     */     throws SQLException
/*     */   {
/* 171 */     notSupported();
/*     */   }
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */     throws SQLException
/*     */   {
/* 182 */     return this.currentPositionInEntireResult + 1;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 194 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws SQLException
/*     */   {
/* 205 */     return (isBeforeFirst()) && (isAfterLast());
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 216 */     return this.currentPositionInEntireResult == 0;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 227 */     return (this.lastRowFetched) && (this.currentPositionInFetchedRows == this.fetchedRows.size() - 1);
/*     */   }
/*     */ 
/*     */   public void addRow(ResultSetRow row)
/*     */     throws SQLException
/*     */   {
/* 241 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 251 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 261 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 271 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 282 */     this.metadata = null;
/* 283 */     this.owner = null;
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 295 */     if ((this.fetchedRows != null) && (this.fetchedRows.size() == 0)) {
/* 296 */       return false;
/*     */     }
/*     */ 
/* 299 */     if ((this.owner != null) && (this.owner.owningStatement != null)) {
/* 300 */       int maxRows = this.owner.owningStatement.maxRows;
/*     */ 
/* 302 */       if ((maxRows != -1) && (this.currentPositionInEntireResult + 1 > maxRows)) {
/* 303 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 307 */     if (this.currentPositionInEntireResult != -1)
/*     */     {
/* 310 */       if (this.currentPositionInFetchedRows < this.fetchedRows.size() - 1)
/* 311 */         return true;
/* 312 */       if ((this.currentPositionInFetchedRows == this.fetchedRows.size()) && (this.lastRowFetched))
/*     */       {
/* 315 */         return false;
/*     */       }
/*     */ 
/* 318 */       fetchMoreRows();
/*     */ 
/* 320 */       return this.fetchedRows.size() > 0;
/*     */     }
/*     */ 
/* 326 */     fetchMoreRows();
/*     */ 
/* 328 */     return this.fetchedRows.size() > 0;
/*     */   }
/*     */ 
/*     */   public void moveRowRelative(int rows)
/*     */     throws SQLException
/*     */   {
/* 340 */     notSupported();
/*     */   }
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 351 */     if ((this.fetchedRows == null) && (this.currentPositionInEntireResult != -1)) {
/* 352 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", this.mysql.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 358 */     if (!hasNext()) {
/* 359 */       return null;
/*     */     }
/*     */ 
/* 362 */     this.currentPositionInEntireResult += 1;
/* 363 */     this.currentPositionInFetchedRows += 1;
/*     */ 
/* 366 */     if ((this.fetchedRows != null) && (this.fetchedRows.size() == 0)) {
/* 367 */       return null;
/*     */     }
/*     */ 
/* 370 */     if (this.currentPositionInFetchedRows > this.fetchedRows.size() - 1) {
/* 371 */       fetchMoreRows();
/* 372 */       this.currentPositionInFetchedRows = 0;
/*     */     }
/*     */ 
/* 375 */     ResultSetRow row = (ResultSetRow)this.fetchedRows.get(this.currentPositionInFetchedRows);
/*     */ 
/* 378 */     row.setMetadata(this.metadata);
/*     */ 
/* 380 */     return row;
/*     */   }
/*     */ 
/*     */   private void fetchMoreRows()
/*     */     throws SQLException
/*     */   {
/* 387 */     if (this.lastRowFetched) {
/* 388 */       this.fetchedRows = new ArrayList(0);
/* 389 */       return;
/*     */     }
/*     */ 
/* 392 */     synchronized (this.owner.connection) {
/* 393 */       boolean oldFirstFetchCompleted = this.firstFetchCompleted;
/*     */ 
/* 395 */       if (!this.firstFetchCompleted) {
/* 396 */         this.firstFetchCompleted = true;
/*     */       }
/*     */ 
/* 399 */       int numRowsToFetch = this.owner.getFetchSize();
/*     */ 
/* 401 */       if (numRowsToFetch == 0) {
/* 402 */         numRowsToFetch = this.prepStmt.getFetchSize();
/*     */       }
/*     */ 
/* 405 */       if (numRowsToFetch == -2147483648)
/*     */       {
/* 409 */         numRowsToFetch = 1;
/*     */       }
/*     */ 
/* 412 */       this.fetchedRows = this.mysql.fetchRowsViaCursor(this.fetchedRows, this.statementIdOnServer, this.metadata, numRowsToFetch, this.useBufferRowExplicit);
/*     */ 
/* 415 */       this.currentPositionInFetchedRows = -1;
/*     */ 
/* 417 */       if ((this.mysql.getServerStatus() & 0x80) != 0) {
/* 418 */         this.lastRowFetched = true;
/*     */ 
/* 420 */         if ((!oldFirstFetchCompleted) && (this.fetchedRows.size() == 0))
/* 421 */           this.wasEmpty = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeRow(int ind)
/*     */     throws SQLException
/*     */   {
/* 436 */     notSupported();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 445 */     return -1;
/*     */   }
/*     */ 
/*     */   protected void nextRecord() throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   private void notSupported() throws SQLException {
/* 453 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 462 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 471 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty() {
/* 475 */     return this.wasEmpty;
/*     */   }
/*     */ 
/*     */   public void setMetadata(Field[] metadata) {
/* 479 */     this.metadata = metadata;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.RowDataCursor
 * JD-Core Version:    0.6.0
 */