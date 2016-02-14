/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class BlobFromLocator
/*     */   implements Blob
/*     */ {
/*  56 */   private List<String> primaryKeyColumns = null;
/*     */ 
/*  58 */   private List<String> primaryKeyValues = null;
/*     */   private ResultSetImpl creatorResultSet;
/*  63 */   private String blobColumnName = null;
/*     */ 
/*  65 */   private String tableName = null;
/*     */ 
/*  67 */   private int numColsInResultSet = 0;
/*     */ 
/*  69 */   private int numPrimaryKeys = 0;
/*     */   private String quotedId;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  80 */     this.exceptionInterceptor = exceptionInterceptor;
/*  81 */     this.creatorResultSet = creatorResultSetToSet;
/*     */ 
/*  83 */     this.numColsInResultSet = this.creatorResultSet.fields.length;
/*  84 */     this.quotedId = this.creatorResultSet.connection.getMetaData().getIdentifierQuoteString();
/*     */ 
/*  87 */     if (this.numColsInResultSet > 1) {
/*  88 */       this.primaryKeyColumns = new ArrayList();
/*  89 */       this.primaryKeyValues = new ArrayList();
/*     */ 
/*  91 */       for (int i = 0; i < this.numColsInResultSet; i++)
/*  92 */         if (this.creatorResultSet.fields[i].isPrimaryKey()) {
/*  93 */           StringBuffer keyName = new StringBuffer();
/*  94 */           keyName.append(this.quotedId);
/*     */ 
/*  96 */           String originalColumnName = this.creatorResultSet.fields[i].getOriginalName();
/*     */ 
/*  99 */           if ((originalColumnName != null) && (originalColumnName.length() > 0))
/*     */           {
/* 101 */             keyName.append(originalColumnName);
/*     */           }
/* 103 */           else keyName.append(this.creatorResultSet.fields[i].getName());
/*     */ 
/* 107 */           keyName.append(this.quotedId);
/*     */ 
/* 109 */           this.primaryKeyColumns.add(keyName.toString());
/* 110 */           this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 115 */       notEnoughInformationInQuery();
/*     */     }
/*     */ 
/* 118 */     this.numPrimaryKeys = this.primaryKeyColumns.size();
/*     */ 
/* 120 */     if (this.numPrimaryKeys == 0) {
/* 121 */       notEnoughInformationInQuery();
/*     */     }
/*     */ 
/* 124 */     if (this.creatorResultSet.fields[0].getOriginalTableName() != null) {
/* 125 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */ 
/* 127 */       String databaseName = this.creatorResultSet.fields[0].getDatabaseName();
/*     */ 
/* 130 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/* 131 */         tableNameBuffer.append(this.quotedId);
/* 132 */         tableNameBuffer.append(databaseName);
/* 133 */         tableNameBuffer.append(this.quotedId);
/* 134 */         tableNameBuffer.append('.');
/*     */       }
/*     */ 
/* 137 */       tableNameBuffer.append(this.quotedId);
/* 138 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getOriginalTableName());
/*     */ 
/* 140 */       tableNameBuffer.append(this.quotedId);
/*     */ 
/* 142 */       this.tableName = tableNameBuffer.toString();
/*     */     } else {
/* 144 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */ 
/* 146 */       tableNameBuffer.append(this.quotedId);
/* 147 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getTableName());
/*     */ 
/* 149 */       tableNameBuffer.append(this.quotedId);
/*     */ 
/* 151 */       this.tableName = tableNameBuffer.toString();
/*     */     }
/*     */ 
/* 154 */     this.blobColumnName = (this.quotedId + this.creatorResultSet.getString(blobColumnIndex) + this.quotedId);
/*     */   }
/*     */ 
/*     */   private void notEnoughInformationInQuery() throws SQLException
/*     */   {
/* 159 */     throw SQLError.createSQLException("Emulated BLOB locators must come from a ResultSet with only one table selected, and all primary keys selected", "S1000", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 169 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 182 */     return new BufferedInputStream(new LocatorInputStream(), this.creatorResultSet.connection.getLocatorFetchBufferSize());
/*     */   }
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 191 */     PreparedStatement pStmt = null;
/*     */ 
/* 193 */     if (offset + length > bytes.length) {
/* 194 */       length = bytes.length - offset;
/*     */     }
/*     */ 
/* 197 */     byte[] bytesToWrite = new byte[length];
/* 198 */     System.arraycopy(bytes, offset, bytesToWrite, 0, length);
/*     */ 
/* 201 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 202 */     query.append(this.tableName);
/* 203 */     query.append(" SET ");
/* 204 */     query.append(this.blobColumnName);
/* 205 */     query.append(" = INSERT(");
/* 206 */     query.append(this.blobColumnName);
/* 207 */     query.append(", ");
/* 208 */     query.append(writeAt);
/* 209 */     query.append(", ");
/* 210 */     query.append(length);
/* 211 */     query.append(", ?) WHERE ");
/*     */ 
/* 213 */     query.append((String)this.primaryKeyColumns.get(0));
/* 214 */     query.append(" = ?");
/*     */ 
/* 216 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 217 */       query.append(" AND ");
/* 218 */       query.append((String)this.primaryKeyColumns.get(i));
/* 219 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 224 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 227 */       pStmt.setBytes(1, bytesToWrite);
/*     */ 
/* 229 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 230 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 233 */       int rowsUpdated = pStmt.executeUpdate();
/*     */ 
/* 235 */       if (rowsUpdated != 1) {
/* 236 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 241 */       if (pStmt != null) {
/*     */         try {
/* 243 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 248 */         pStmt = null;
/*     */       }
/*     */     }
/*     */ 
/* 252 */     return (int)length();
/*     */   }
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 259 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 278 */     PreparedStatement pStmt = null;
/*     */     try
/*     */     {
/* 282 */       pStmt = createGetBytesStatement();
/*     */ 
/* 284 */       arrayOfByte = getBytesInternal(pStmt, pos, length);
/*     */     }
/*     */     finally
/*     */     {
/*     */       byte[] arrayOfByte;
/* 286 */       if (pStmt != null) {
/*     */         try {
/* 288 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 293 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 308 */     ResultSet blobRs = null;
/* 309 */     PreparedStatement pStmt = null;
/*     */ 
/* 312 */     StringBuffer query = new StringBuffer("SELECT LENGTH(");
/* 313 */     query.append(this.blobColumnName);
/* 314 */     query.append(") FROM ");
/* 315 */     query.append(this.tableName);
/* 316 */     query.append(" WHERE ");
/*     */ 
/* 318 */     query.append((String)this.primaryKeyColumns.get(0));
/* 319 */     query.append(" = ?");
/*     */ 
/* 321 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 322 */       query.append(" AND ");
/* 323 */       query.append((String)this.primaryKeyColumns.get(i));
/* 324 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 329 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 332 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 333 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 336 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 338 */       if (blobRs.next()) {
/* 339 */         i = blobRs.getLong(1); jsr 26;
/*     */       }
/*     */ 
/* 342 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     finally
/*     */     {
/* 346 */       if (blobRs != null) {
/*     */         try {
/* 348 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 353 */         blobRs = null;
/*     */       }
/*     */ 
/* 356 */       if (pStmt != null) {
/*     */         try {
/* 358 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 363 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long position(Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 383 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */ 
/*     */   public long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 390 */     ResultSet blobRs = null;
/* 391 */     PreparedStatement pStmt = null;
/*     */ 
/* 394 */     StringBuffer query = new StringBuffer("SELECT LOCATE(");
/* 395 */     query.append("?, ");
/* 396 */     query.append(this.blobColumnName);
/* 397 */     query.append(", ");
/* 398 */     query.append(start);
/* 399 */     query.append(") FROM ");
/* 400 */     query.append(this.tableName);
/* 401 */     query.append(" WHERE ");
/*     */ 
/* 403 */     query.append((String)this.primaryKeyColumns.get(0));
/* 404 */     query.append(" = ?");
/*     */ 
/* 406 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 407 */       query.append(" AND ");
/* 408 */       query.append((String)this.primaryKeyColumns.get(i));
/* 409 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 414 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 416 */       pStmt.setBytes(1, pattern);
/*     */ 
/* 418 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 419 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 422 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 424 */       if (blobRs.next()) {
/* 425 */         i = blobRs.getLong(1); jsr 26;
/*     */       }
/*     */ 
/* 428 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     finally
/*     */     {
/* 432 */       if (blobRs != null) {
/*     */         try {
/* 434 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 439 */         blobRs = null;
/*     */       }
/*     */ 
/* 442 */       if (pStmt != null) {
/*     */         try {
/* 444 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 449 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 458 */     PreparedStatement pStmt = null;
/*     */ 
/* 461 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 462 */     query.append(this.tableName);
/* 463 */     query.append(" SET ");
/* 464 */     query.append(this.blobColumnName);
/* 465 */     query.append(" = LEFT(");
/* 466 */     query.append(this.blobColumnName);
/* 467 */     query.append(", ");
/* 468 */     query.append(length);
/* 469 */     query.append(") WHERE ");
/*     */ 
/* 471 */     query.append((String)this.primaryKeyColumns.get(0));
/* 472 */     query.append(" = ?");
/*     */ 
/* 474 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 475 */       query.append(" AND ");
/* 476 */       query.append((String)this.primaryKeyColumns.get(i));
/* 477 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 482 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 485 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 486 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 489 */       int rowsUpdated = pStmt.executeUpdate();
/*     */ 
/* 491 */       if (rowsUpdated != 1) {
/* 492 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 497 */       if (pStmt != null) {
/*     */         try {
/* 499 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 504 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   PreparedStatement createGetBytesStatement() throws SQLException {
/* 510 */     StringBuffer query = new StringBuffer("SELECT SUBSTRING(");
/*     */ 
/* 512 */     query.append(this.blobColumnName);
/* 513 */     query.append(", ");
/* 514 */     query.append("?");
/* 515 */     query.append(", ");
/* 516 */     query.append("?");
/* 517 */     query.append(") FROM ");
/* 518 */     query.append(this.tableName);
/* 519 */     query.append(" WHERE ");
/*     */ 
/* 521 */     query.append((String)this.primaryKeyColumns.get(0));
/* 522 */     query.append(" = ?");
/*     */ 
/* 524 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 525 */       query.append(" AND ");
/* 526 */       query.append((String)this.primaryKeyColumns.get(i));
/* 527 */       query.append(" = ?");
/*     */     }
/*     */ 
/* 530 */     return this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */   }
/*     */ 
/*     */   byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 537 */     ResultSet blobRs = null;
/*     */     try
/*     */     {
/* 541 */       pStmt.setLong(1, pos);
/* 542 */       pStmt.setInt(2, length);
/*     */ 
/* 544 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 545 */         pStmt.setString(i + 3, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 548 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 550 */       if (blobRs.next()) {
/* 551 */         i = ((ResultSetImpl)blobRs).getBytes(1, true); jsr 26;
/*     */       }
/*     */ 
/* 554 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     finally
/*     */     {
/* 558 */       if (blobRs != null) {
/*     */         try {
/* 560 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 565 */         blobRs = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void free()
/*     */     throws SQLException
/*     */   {
/* 700 */     this.creatorResultSet = null;
/* 701 */     this.primaryKeyColumns = null;
/* 702 */     this.primaryKeyValues = null;
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream(long pos, long length) throws SQLException {
/* 706 */     return new LocatorInputStream(pos, length);
/*     */   }
/*     */ 
/*     */   class LocatorInputStream extends InputStream
/*     */   {
/* 571 */     long currentPositionInBlob = 0L;
/*     */ 
/* 573 */     long length = 0L;
/*     */ 
/* 575 */     PreparedStatement pStmt = null;
/*     */ 
/*     */     LocatorInputStream() throws SQLException {
/* 578 */       this.length = BlobFromLocator.this.length();
/* 579 */       this.pStmt = BlobFromLocator.this.createGetBytesStatement();
/*     */     }
/*     */ 
/*     */     LocatorInputStream(long pos, long len) throws SQLException
/*     */     {
/* 584 */       this.length = (pos + len);
/* 585 */       this.currentPositionInBlob = pos;
/* 586 */       long blobLength = BlobFromLocator.this.length();
/*     */ 
/* 588 */       if (pos + len > blobLength) {
/* 589 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamLength", new Object[] { Long.valueOf(blobLength), Long.valueOf(pos), Long.valueOf(len) }), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */ 
/* 595 */       if (pos < 1L) {
/* 596 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */ 
/* 600 */       if (pos > blobLength)
/* 601 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 607 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 608 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 612 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob++ + 1L, 1);
/*     */ 
/* 615 */         if (asBytes == null) {
/* 616 */           return -1;
/*     */         }
/*     */ 
/* 619 */         return asBytes[0]; } catch (SQLException sqlEx) {
/*     */       }
/* 621 */       throw new IOException(sqlEx.toString());
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 631 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 632 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 636 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, len);
/*     */ 
/* 639 */         if (asBytes == null) {
/* 640 */           return -1;
/*     */         }
/*     */ 
/* 643 */         System.arraycopy(asBytes, 0, b, off, asBytes.length);
/*     */ 
/* 645 */         this.currentPositionInBlob += asBytes.length;
/*     */ 
/* 647 */         return asBytes.length; } catch (SQLException sqlEx) {
/*     */       }
/* 649 */       throw new IOException(sqlEx.toString());
/*     */     }
/*     */ 
/*     */     public int read(byte[] b)
/*     */       throws IOException
/*     */     {
/* 659 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 660 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 664 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, b.length);
/*     */ 
/* 667 */         if (asBytes == null) {
/* 668 */           return -1;
/*     */         }
/*     */ 
/* 671 */         System.arraycopy(asBytes, 0, b, 0, asBytes.length);
/*     */ 
/* 673 */         this.currentPositionInBlob += asBytes.length;
/*     */ 
/* 675 */         return asBytes.length; } catch (SQLException sqlEx) {
/*     */       }
/* 677 */       throw new IOException(sqlEx.toString());
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 687 */       if (this.pStmt != null) {
/*     */         try {
/* 689 */           this.pStmt.close();
/*     */         } catch (SQLException sqlEx) {
/* 691 */           throw new IOException(sqlEx.toString());
/*     */         }
/*     */       }
/*     */ 
/* 695 */       super.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.BlobFromLocator
 * JD-Core Version:    0.6.0
 */