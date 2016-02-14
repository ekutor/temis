/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class UpdatableResultSet extends ResultSetImpl
/*      */ {
/*   46 */   static final byte[] STREAM_DATA_MARKER = StringUtils.getBytes("** STREAM DATA **");
/*      */   protected SingleByteCharsetConverter charConverter;
/*      */   private String charEncoding;
/*      */   private byte[][] defaultColumnValue;
/*   56 */   private PreparedStatement deleter = null;
/*      */ 
/*   58 */   private String deleteSQL = null;
/*      */ 
/*   60 */   private boolean initializedCharConverter = false;
/*      */ 
/*   63 */   protected PreparedStatement inserter = null;
/*      */ 
/*   65 */   private String insertSQL = null;
/*      */ 
/*   68 */   private boolean isUpdatable = false;
/*      */ 
/*   71 */   private String notUpdatableReason = null;
/*      */ 
/*   74 */   private List<Integer> primaryKeyIndicies = null;
/*      */   private String qualifiedAndQuotedTableName;
/*   78 */   private String quotedIdChar = null;
/*      */   private PreparedStatement refresher;
/*   83 */   private String refreshSQL = null;
/*      */   private ResultSetRow savedCurrentRow;
/*   89 */   protected PreparedStatement updater = null;
/*      */ 
/*   92 */   private String updateSQL = null;
/*      */ 
/*   94 */   private boolean populateInserterWithDefaultValues = false;
/*      */ 
/*   96 */   private Map<String, Map<String, Map<String, Integer>>> databasesUsedToTablesUsed = null;
/*      */ 
/*      */   protected UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  118 */     super(catalog, fields, tuples, conn, creatorStmt);
/*  119 */     checkUpdatability();
/*  120 */     this.populateInserterWithDefaultValues = this.connection.getPopulateInsertRowWithDefaultValues();
/*      */   }
/*      */ 
/*      */   public synchronized boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  163 */     return super.absolute(row);
/*      */   }
/*      */ 
/*      */   public synchronized void afterLast()
/*      */     throws SQLException
/*      */   {
/*  179 */     super.afterLast();
/*      */   }
/*      */ 
/*      */   public synchronized void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  195 */     super.beforeFirst();
/*      */   }
/*      */ 
/*      */   public synchronized void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  209 */     checkClosed();
/*      */ 
/*  211 */     if (this.doingUpdates) {
/*  212 */       this.doingUpdates = false;
/*  213 */       this.updater.clearParameters();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  223 */     checkClosed();
/*      */ 
/*  225 */     if (!this.onInsertRow)
/*  226 */       super.checkRowPos();
/*      */   }
/*      */ 
/*      */   protected void checkUpdatability()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  238 */       if (this.fields == null)
/*      */       {
/*  245 */         return;
/*      */       }
/*      */ 
/*  248 */       String singleTableName = null;
/*  249 */       String catalogName = null;
/*      */ 
/*  251 */       int primaryKeyCount = 0;
/*      */ 
/*  258 */       if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  259 */         this.catalog = this.fields[0].getDatabaseName();
/*      */ 
/*  261 */         if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  262 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  268 */       if (this.fields.length > 0) {
/*  269 */         singleTableName = this.fields[0].getOriginalTableName();
/*  270 */         catalogName = this.fields[0].getDatabaseName();
/*      */ 
/*  272 */         if (singleTableName == null) {
/*  273 */           singleTableName = this.fields[0].getTableName();
/*  274 */           catalogName = this.catalog;
/*      */         }
/*      */ 
/*  277 */         if ((singleTableName != null) && (singleTableName.length() == 0)) {
/*  278 */           this.isUpdatable = false;
/*  279 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */ 
/*  281 */           return;
/*      */         }
/*      */ 
/*  284 */         if (this.fields[0].isPrimaryKey()) {
/*  285 */           primaryKeyCount++;
/*      */         }
/*      */ 
/*  291 */         for (int i = 1; i < this.fields.length; i++) {
/*  292 */           String otherTableName = this.fields[i].getOriginalTableName();
/*  293 */           String otherCatalogName = this.fields[i].getDatabaseName();
/*      */ 
/*  295 */           if (otherTableName == null) {
/*  296 */             otherTableName = this.fields[i].getTableName();
/*  297 */             otherCatalogName = this.catalog;
/*      */           }
/*      */ 
/*  300 */           if ((otherTableName != null) && (otherTableName.length() == 0)) {
/*  301 */             this.isUpdatable = false;
/*  302 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */ 
/*  304 */             return;
/*      */           }
/*      */ 
/*  307 */           if ((singleTableName == null) || (!otherTableName.equals(singleTableName)))
/*      */           {
/*  309 */             this.isUpdatable = false;
/*  310 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
/*      */ 
/*  312 */             return;
/*      */           }
/*      */ 
/*  316 */           if ((catalogName == null) || (!otherCatalogName.equals(catalogName)))
/*      */           {
/*  318 */             this.isUpdatable = false;
/*  319 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
/*      */ 
/*  321 */             return;
/*      */           }
/*      */ 
/*  324 */           if (this.fields[i].isPrimaryKey()) {
/*  325 */             primaryKeyCount++;
/*      */           }
/*      */         }
/*      */ 
/*  329 */         if ((singleTableName == null) || (singleTableName.length() == 0)) {
/*  330 */           this.isUpdatable = false;
/*  331 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.2");
/*      */ 
/*  333 */           return;
/*      */         }
/*      */       } else {
/*  336 */         this.isUpdatable = false;
/*  337 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */ 
/*  339 */         return;
/*      */       }
/*      */ 
/*  342 */       if (this.connection.getStrictUpdates()) {
/*  343 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  345 */         ResultSet rs = null;
/*  346 */         HashMap primaryKeyNames = new HashMap();
/*      */         try
/*      */         {
/*  349 */           rs = dbmd.getPrimaryKeys(catalogName, null, singleTableName);
/*      */ 
/*  351 */           while (rs.next()) {
/*  352 */             String keyName = rs.getString(4);
/*  353 */             keyName = keyName.toUpperCase();
/*  354 */             primaryKeyNames.put(keyName, keyName);
/*      */           }
/*      */         } finally {
/*  357 */           if (rs != null) {
/*      */             try {
/*  359 */               rs.close();
/*      */             } catch (Exception ex) {
/*  361 */               AssertionFailedException.shouldNotHappen(ex);
/*      */             }
/*      */ 
/*  364 */             rs = null;
/*      */           }
/*      */         }
/*      */ 
/*  368 */         int existingPrimaryKeysCount = primaryKeyNames.size();
/*      */ 
/*  370 */         if (existingPrimaryKeysCount == 0) {
/*  371 */           this.isUpdatable = false;
/*  372 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
/*      */ 
/*  374 */           return;
/*      */         }
/*      */ 
/*  380 */         for (int i = 0; i < this.fields.length; i++) {
/*  381 */           if (this.fields[i].isPrimaryKey()) {
/*  382 */             String columnNameUC = this.fields[i].getName().toUpperCase();
/*      */ 
/*  385 */             if (primaryKeyNames.remove(columnNameUC) != null)
/*      */               continue;
/*  387 */             String originalName = this.fields[i].getOriginalName();
/*      */ 
/*  389 */             if ((originalName == null) || 
/*  390 */               (primaryKeyNames.remove(originalName.toUpperCase()) != null)) {
/*      */               continue;
/*      */             }
/*  393 */             this.isUpdatable = false;
/*  394 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[] { originalName });
/*      */ 
/*  397 */             return;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  404 */         this.isUpdatable = primaryKeyNames.isEmpty();
/*      */ 
/*  406 */         if (!this.isUpdatable) {
/*  407 */           if (existingPrimaryKeysCount > 1)
/*  408 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.7");
/*      */           else {
/*  410 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */           }
/*      */ 
/*  413 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  420 */       if (primaryKeyCount == 0) {
/*  421 */         this.isUpdatable = false;
/*  422 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */ 
/*  424 */         return;
/*      */       }
/*      */ 
/*  427 */       this.isUpdatable = true;
/*  428 */       this.notUpdatableReason = null;
/*      */ 
/*  430 */       return;
/*      */     } catch (SQLException sqlEx) {
/*  432 */       this.isUpdatable = false;
/*  433 */       this.notUpdatableReason = sqlEx.getMessage();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  448 */     checkClosed();
/*      */ 
/*  450 */     if (!this.isUpdatable) {
/*  451 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/*  454 */     if (this.onInsertRow)
/*  455 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1"), getExceptionInterceptor());
/*  456 */     if (this.rowData.size() == 0)
/*  457 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2"), getExceptionInterceptor());
/*  458 */     if (isBeforeFirst())
/*  459 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3"), getExceptionInterceptor());
/*  460 */     if (isAfterLast()) {
/*  461 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"), getExceptionInterceptor());
/*      */     }
/*      */ 
/*  464 */     if (this.deleter == null) {
/*  465 */       if (this.deleteSQL == null) {
/*  466 */         generateStatements();
/*      */       }
/*      */ 
/*  469 */       this.deleter = ((PreparedStatement)this.connection.clientPrepareStatement(this.deleteSQL));
/*      */     }
/*      */ 
/*  473 */     this.deleter.clearParameters();
/*      */ 
/*  475 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/*  477 */     if (numKeys == 1) {
/*  478 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */ 
/*  480 */       setParamValue(this.deleter, 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */     }
/*      */     else {
/*  483 */       for (int i = 0; i < numKeys; i++) {
/*  484 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/*  486 */         setParamValue(this.deleter, i + 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  492 */     this.deleter.executeUpdate();
/*  493 */     this.rowData.removeRow(this.rowData.getCurrentRowNumber());
/*      */ 
/*  496 */     previous();
/*      */   }
/*      */ 
/*      */   private synchronized void setParamValue(PreparedStatement ps, int psIdx, ResultSetRow row, int rsIdx, int sqlType)
/*      */     throws SQLException
/*      */   {
/*  503 */     byte[] val = row.getColumnValue(rsIdx);
/*  504 */     if (val == null) {
/*  505 */       ps.setNull(psIdx, 0);
/*  506 */       return;
/*      */     }
/*  508 */     switch (sqlType) {
/*      */     case 0:
/*  510 */       ps.setNull(psIdx, 0);
/*  511 */       break;
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/*  515 */       ps.setInt(psIdx, row.getInt(rsIdx));
/*  516 */       break;
/*      */     case -5:
/*  518 */       ps.setLong(psIdx, row.getLong(rsIdx));
/*  519 */       break;
/*      */     case -1:
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 12:
/*  525 */       ps.setString(psIdx, row.getString(rsIdx, this.charEncoding, this.connection));
/*  526 */       break;
/*      */     case 91:
/*  528 */       ps.setDate(psIdx, row.getDateFast(rsIdx, this.connection, this, this.fastDateCal), this.fastDateCal);
/*  529 */       break;
/*      */     case 93:
/*  531 */       ps.setTimestamp(psIdx, row.getTimestampFast(rsIdx, this.fastDateCal, this.defaultTimeZone, false, this.connection, this));
/*  532 */       break;
/*      */     case 92:
/*  534 */       ps.setTime(psIdx, row.getTimeFast(rsIdx, this.fastDateCal, this.defaultTimeZone, false, this.connection, this));
/*  535 */       break;
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 16:
/*  540 */       ps.setBytesNoEscapeNoQuotes(psIdx, val);
/*  541 */       break;
/*      */     default:
/*  547 */       ps.setBytes(psIdx, val);
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void extractDefaultValues()
/*      */     throws SQLException
/*      */   {
/*  554 */     DatabaseMetaData dbmd = this.connection.getMetaData();
/*  555 */     this.defaultColumnValue = new byte[this.fields.length][];
/*      */ 
/*  557 */     ResultSet columnsResultSet = null;
/*      */ 
/*  559 */     for (Map.Entry dbEntry : this.databasesUsedToTablesUsed.entrySet())
/*      */     {
/*  561 */       for (Map.Entry tableEntry : ((Map)dbEntry.getValue()).entrySet()) {
/*  562 */         String tableName = (String)tableEntry.getKey();
/*  563 */         Map columnNamesToIndices = (Map)tableEntry.getValue();
/*      */         try
/*      */         {
/*  566 */           columnsResultSet = dbmd.getColumns(this.catalog, null, tableName, "%");
/*      */ 
/*  569 */           while (columnsResultSet.next()) {
/*  570 */             String columnName = columnsResultSet.getString("COLUMN_NAME");
/*  571 */             byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
/*      */ 
/*  573 */             if (columnNamesToIndices.containsKey(columnName)) {
/*  574 */               int localColumnIndex = ((Integer)columnNamesToIndices.get(columnName)).intValue();
/*      */ 
/*  576 */               this.defaultColumnValue[localColumnIndex] = defaultValue;
/*      */             }
/*      */           }
/*      */         } finally {
/*  580 */           if (columnsResultSet != null) {
/*  581 */             columnsResultSet.close();
/*      */ 
/*  583 */             columnsResultSet = null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean first()
/*      */     throws SQLException
/*      */   {
/*  604 */     return super.first();
/*      */   }
/*      */ 
/*      */   protected synchronized void generateStatements()
/*      */     throws SQLException
/*      */   {
/*  617 */     if (!this.isUpdatable) {
/*  618 */       this.doingUpdates = false;
/*  619 */       this.onInsertRow = false;
/*      */ 
/*  621 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/*  624 */     String quotedId = getQuotedIdChar();
/*      */ 
/*  626 */     Map tableNamesSoFar = null;
/*      */ 
/*  628 */     if (this.connection.lowerCaseTableNames()) {
/*  629 */       tableNamesSoFar = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  630 */       this.databasesUsedToTablesUsed = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     } else {
/*  632 */       tableNamesSoFar = new TreeMap();
/*  633 */       this.databasesUsedToTablesUsed = new TreeMap();
/*      */     }
/*      */ 
/*  636 */     this.primaryKeyIndicies = new ArrayList();
/*      */ 
/*  638 */     StringBuffer fieldValues = new StringBuffer();
/*  639 */     StringBuffer keyValues = new StringBuffer();
/*  640 */     StringBuffer columnNames = new StringBuffer();
/*  641 */     StringBuffer insertPlaceHolders = new StringBuffer();
/*  642 */     StringBuffer allTablesBuf = new StringBuffer();
/*  643 */     Map columnIndicesToTable = new HashMap();
/*      */ 
/*  645 */     boolean firstTime = true;
/*  646 */     boolean keysFirstTime = true;
/*      */ 
/*  648 */     String equalsStr = this.connection.versionMeetsMinimum(3, 23, 0) ? "<=>" : "=";
/*      */ 
/*  651 */     for (int i = 0; i < this.fields.length; i++) {
/*  652 */       StringBuffer tableNameBuffer = new StringBuffer();
/*  653 */       Map updColumnNameToIndex = null;
/*      */ 
/*  656 */       if (this.fields[i].getOriginalTableName() != null)
/*      */       {
/*  658 */         String databaseName = this.fields[i].getDatabaseName();
/*      */ 
/*  660 */         if ((databaseName != null) && (databaseName.length() > 0)) {
/*  661 */           tableNameBuffer.append(quotedId);
/*  662 */           tableNameBuffer.append(databaseName);
/*  663 */           tableNameBuffer.append(quotedId);
/*  664 */           tableNameBuffer.append('.');
/*      */         }
/*      */ 
/*  667 */         String tableOnlyName = this.fields[i].getOriginalTableName();
/*      */ 
/*  669 */         tableNameBuffer.append(quotedId);
/*  670 */         tableNameBuffer.append(tableOnlyName);
/*  671 */         tableNameBuffer.append(quotedId);
/*      */ 
/*  673 */         String fqTableName = tableNameBuffer.toString();
/*      */ 
/*  675 */         if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  676 */           if (!tableNamesSoFar.isEmpty()) {
/*  677 */             allTablesBuf.append(',');
/*      */           }
/*      */ 
/*  680 */           allTablesBuf.append(fqTableName);
/*  681 */           tableNamesSoFar.put(fqTableName, fqTableName);
/*      */         }
/*      */ 
/*  684 */         columnIndicesToTable.put(Integer.valueOf(i), fqTableName);
/*      */ 
/*  686 */         updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(databaseName, tableOnlyName);
/*      */       } else {
/*  688 */         String tableOnlyName = this.fields[i].getTableName();
/*      */ 
/*  690 */         if (tableOnlyName != null) {
/*  691 */           tableNameBuffer.append(quotedId);
/*  692 */           tableNameBuffer.append(tableOnlyName);
/*  693 */           tableNameBuffer.append(quotedId);
/*      */ 
/*  695 */           String fqTableName = tableNameBuffer.toString();
/*      */ 
/*  697 */           if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  698 */             if (!tableNamesSoFar.isEmpty()) {
/*  699 */               allTablesBuf.append(',');
/*      */             }
/*      */ 
/*  702 */             allTablesBuf.append(fqTableName);
/*  703 */             tableNamesSoFar.put(fqTableName, fqTableName);
/*      */           }
/*      */ 
/*  706 */           columnIndicesToTable.put(Integer.valueOf(i), fqTableName);
/*      */ 
/*  708 */           updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(this.catalog, tableOnlyName);
/*      */         }
/*      */       }
/*      */ 
/*  712 */       String originalColumnName = this.fields[i].getOriginalName();
/*  713 */       String columnName = null;
/*      */ 
/*  715 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalColumnName != null) && (originalColumnName.length() > 0))
/*      */       {
/*  718 */         columnName = originalColumnName;
/*      */       }
/*  720 */       else columnName = this.fields[i].getName();
/*      */ 
/*  723 */       if ((updColumnNameToIndex != null) && (columnName != null)) {
/*  724 */         updColumnNameToIndex.put(columnName, Integer.valueOf(i));
/*      */       }
/*      */ 
/*  727 */       String originalTableName = this.fields[i].getOriginalTableName();
/*  728 */       String tableName = null;
/*      */ 
/*  730 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalTableName != null) && (originalTableName.length() > 0))
/*      */       {
/*  733 */         tableName = originalTableName;
/*      */       }
/*  735 */       else tableName = this.fields[i].getTableName();
/*      */ 
/*  738 */       StringBuffer fqcnBuf = new StringBuffer();
/*  739 */       String databaseName = this.fields[i].getDatabaseName();
/*      */ 
/*  741 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/*  742 */         fqcnBuf.append(quotedId);
/*  743 */         fqcnBuf.append(databaseName);
/*  744 */         fqcnBuf.append(quotedId);
/*  745 */         fqcnBuf.append('.');
/*      */       }
/*      */ 
/*  748 */       fqcnBuf.append(quotedId);
/*  749 */       fqcnBuf.append(tableName);
/*  750 */       fqcnBuf.append(quotedId);
/*  751 */       fqcnBuf.append('.');
/*  752 */       fqcnBuf.append(quotedId);
/*  753 */       fqcnBuf.append(columnName);
/*  754 */       fqcnBuf.append(quotedId);
/*      */ 
/*  756 */       String qualifiedColumnName = fqcnBuf.toString();
/*      */ 
/*  758 */       if (this.fields[i].isPrimaryKey()) {
/*  759 */         this.primaryKeyIndicies.add(Integer.valueOf(i));
/*      */ 
/*  761 */         if (!keysFirstTime)
/*  762 */           keyValues.append(" AND ");
/*      */         else {
/*  764 */           keysFirstTime = false;
/*      */         }
/*      */ 
/*  767 */         keyValues.append(qualifiedColumnName);
/*  768 */         keyValues.append(equalsStr);
/*  769 */         keyValues.append("?");
/*      */       }
/*      */ 
/*  772 */       if (firstTime) {
/*  773 */         firstTime = false;
/*  774 */         fieldValues.append("SET ");
/*      */       } else {
/*  776 */         fieldValues.append(",");
/*  777 */         columnNames.append(",");
/*  778 */         insertPlaceHolders.append(",");
/*      */       }
/*      */ 
/*  781 */       insertPlaceHolders.append("?");
/*      */ 
/*  783 */       columnNames.append(qualifiedColumnName);
/*      */ 
/*  785 */       fieldValues.append(qualifiedColumnName);
/*  786 */       fieldValues.append("=?");
/*      */     }
/*      */ 
/*  789 */     this.qualifiedAndQuotedTableName = allTablesBuf.toString();
/*      */ 
/*  791 */     this.updateSQL = ("UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString());
/*      */ 
/*  794 */     this.insertSQL = ("INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")");
/*      */ 
/*  797 */     this.refreshSQL = ("SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */ 
/*  800 */     this.deleteSQL = ("DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */   }
/*      */ 
/*      */   private Map<String, Integer> getColumnsToIndexMapForTableAndDB(String databaseName, String tableName)
/*      */   {
/*  807 */     Map tablesUsedToColumnsMap = (Map)this.databasesUsedToTablesUsed.get(databaseName);
/*      */ 
/*  809 */     if (tablesUsedToColumnsMap == null) {
/*  810 */       if (this.connection.lowerCaseTableNames())
/*  811 */         tablesUsedToColumnsMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */       else {
/*  813 */         tablesUsedToColumnsMap = new TreeMap();
/*      */       }
/*      */ 
/*  816 */       this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
/*      */     }
/*      */ 
/*  819 */     Map nameToIndex = (Map)tablesUsedToColumnsMap.get(tableName);
/*      */ 
/*  821 */     if (nameToIndex == null) {
/*  822 */       nameToIndex = new HashMap();
/*  823 */       tablesUsedToColumnsMap.put(tableName, nameToIndex);
/*      */     }
/*      */ 
/*  826 */     return nameToIndex;
/*      */   }
/*      */ 
/*      */   private synchronized SingleByteCharsetConverter getCharConverter() throws SQLException
/*      */   {
/*  831 */     if (!this.initializedCharConverter) {
/*  832 */       this.initializedCharConverter = true;
/*      */ 
/*  834 */       if (this.connection.getUseUnicode()) {
/*  835 */         this.charEncoding = this.connection.getEncoding();
/*  836 */         this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  841 */     return this.charConverter;
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/*  854 */     return this.isUpdatable ? 1008 : 1007;
/*      */   }
/*      */ 
/*      */   private synchronized String getQuotedIdChar() throws SQLException {
/*  858 */     if (this.quotedIdChar == null) {
/*  859 */       boolean useQuotedIdentifiers = this.connection.supportsQuotedIdentifiers();
/*      */ 
/*  862 */       if (useQuotedIdentifiers) {
/*  863 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*  864 */         this.quotedIdChar = dbmd.getIdentifierQuoteString();
/*      */       } else {
/*  866 */         this.quotedIdChar = "";
/*      */       }
/*      */     }
/*      */ 
/*  870 */     return this.quotedIdChar;
/*      */   }
/*      */ 
/*      */   public synchronized void insertRow()
/*      */     throws SQLException
/*      */   {
/*  883 */     checkClosed();
/*      */ 
/*  885 */     if (!this.onInsertRow) {
/*  886 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"), getExceptionInterceptor());
/*      */     }
/*      */ 
/*  889 */     this.inserter.executeUpdate();
/*      */ 
/*  891 */     long autoIncrementId = this.inserter.getLastInsertID();
/*  892 */     int numFields = this.fields.length;
/*  893 */     byte[][] newRow = new byte[numFields][];
/*      */ 
/*  895 */     for (int i = 0; i < numFields; i++) {
/*  896 */       if (this.inserter.isNull(i))
/*  897 */         newRow[i] = null;
/*      */       else {
/*  899 */         newRow[i] = this.inserter.getBytesRepresentation(i);
/*      */       }
/*      */ 
/*  906 */       if ((this.fields[i].isAutoIncrement()) && (autoIncrementId > 0L)) {
/*  907 */         newRow[i] = StringUtils.getBytes(String.valueOf(autoIncrementId));
/*  908 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
/*      */       }
/*      */     }
/*      */ 
/*  912 */     ResultSetRow resultSetRow = new ByteArrayRow(newRow, getExceptionInterceptor());
/*      */ 
/*  914 */     refreshRow(this.inserter, resultSetRow);
/*      */ 
/*  916 */     this.rowData.addRow(resultSetRow);
/*  917 */     resetInserter();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/*  934 */     return super.isAfterLast();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/*  951 */     return super.isBeforeFirst();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isFirst()
/*      */     throws SQLException
/*      */   {
/*  967 */     return super.isFirst();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isLast()
/*      */     throws SQLException
/*      */   {
/*  986 */     return super.isLast();
/*      */   }
/*      */ 
/*      */   boolean isUpdatable() {
/*  990 */     return this.isUpdatable;
/*      */   }
/*      */ 
/*      */   public synchronized boolean last()
/*      */     throws SQLException
/*      */   {
/* 1007 */     return super.last();
/*      */   }
/*      */ 
/*      */   public synchronized void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 1021 */     checkClosed();
/*      */ 
/* 1023 */     if (!this.isUpdatable) {
/* 1024 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/* 1027 */     if (this.onInsertRow) {
/* 1028 */       this.onInsertRow = false;
/* 1029 */       this.thisRow = this.savedCurrentRow;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 1051 */     checkClosed();
/*      */ 
/* 1053 */     if (!this.isUpdatable) {
/* 1054 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/* 1057 */     if (this.inserter == null) {
/* 1058 */       if (this.insertSQL == null) {
/* 1059 */         generateStatements();
/*      */       }
/*      */ 
/* 1062 */       this.inserter = ((PreparedStatement)this.connection.clientPrepareStatement(this.insertSQL));
/*      */ 
/* 1064 */       if (this.populateInserterWithDefaultValues) {
/* 1065 */         extractDefaultValues();
/*      */       }
/*      */ 
/* 1068 */       resetInserter();
/*      */     } else {
/* 1070 */       resetInserter();
/*      */     }
/*      */ 
/* 1073 */     int numFields = this.fields.length;
/*      */ 
/* 1075 */     this.onInsertRow = true;
/* 1076 */     this.doingUpdates = false;
/* 1077 */     this.savedCurrentRow = this.thisRow;
/* 1078 */     byte[][] newRowData = new byte[numFields][];
/* 1079 */     this.thisRow = new ByteArrayRow(newRowData, getExceptionInterceptor());
/*      */ 
/* 1081 */     for (int i = 0; i < numFields; i++)
/* 1082 */       if (!this.populateInserterWithDefaultValues) {
/* 1083 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, StringUtils.getBytes("DEFAULT"));
/*      */ 
/* 1085 */         newRowData = (byte[][])null;
/*      */       }
/* 1087 */       else if (this.defaultColumnValue[i] != null) {
/* 1088 */         Field f = this.fields[i];
/*      */ 
/* 1090 */         switch (f.getMysqlType())
/*      */         {
/*      */         case 7:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 14:
/* 1097 */           if ((this.defaultColumnValue[i].length <= 7) || (this.defaultColumnValue[i][0] != 67) || (this.defaultColumnValue[i][1] != 85) || (this.defaultColumnValue[i][2] != 82) || (this.defaultColumnValue[i][3] != 82) || (this.defaultColumnValue[i][4] != 69) || (this.defaultColumnValue[i][5] != 78) || (this.defaultColumnValue[i][6] != 84) || (this.defaultColumnValue[i][7] != 95))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1106 */           this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
/*      */ 
/* 1109 */           break;
/*      */         case 8:
/*      */         case 9:
/* 1112 */         case 13: } this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */ 
/* 1118 */         byte[] defaultValueCopy = new byte[this.defaultColumnValue[i].length];
/* 1119 */         System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
/*      */ 
/* 1121 */         newRowData[i] = defaultValueCopy;
/*      */       } else {
/* 1123 */         this.inserter.setNull(i + 1, 0);
/* 1124 */         newRowData[i] = null;
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized boolean next()
/*      */     throws SQLException
/*      */   {
/* 1150 */     return super.next();
/*      */   }
/*      */ 
/*      */   public synchronized boolean prev()
/*      */     throws SQLException
/*      */   {
/* 1169 */     return super.prev();
/*      */   }
/*      */ 
/*      */   public synchronized boolean previous()
/*      */     throws SQLException
/*      */   {
/* 1191 */     return super.previous();
/*      */   }
/*      */ 
/*      */   public synchronized void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 1204 */     if (this.isClosed) {
/* 1205 */       return;
/*      */     }
/*      */ 
/* 1208 */     SQLException sqlEx = null;
/*      */ 
/* 1210 */     if ((this.useUsageAdvisor) && 
/* 1211 */       (this.deleter == null) && (this.inserter == null) && (this.refresher == null) && (this.updater == null))
/*      */     {
/* 1213 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 1215 */       String message = Messages.getString("UpdatableResultSet.34");
/*      */ 
/* 1217 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1231 */       if (this.deleter != null)
/* 1232 */         this.deleter.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1235 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1239 */       if (this.inserter != null)
/* 1240 */         this.inserter.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1243 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1247 */       if (this.refresher != null)
/* 1248 */         this.refresher.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1251 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1255 */       if (this.updater != null)
/* 1256 */         this.updater.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1259 */       sqlEx = ex;
/*      */     }
/*      */ 
/* 1262 */     super.realClose(calledExplicitly);
/*      */ 
/* 1264 */     if (sqlEx != null)
/* 1265 */       throw sqlEx;
/*      */   }
/*      */ 
/*      */   public synchronized void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 1290 */     checkClosed();
/*      */ 
/* 1292 */     if (!this.isUpdatable) {
/* 1293 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/* 1296 */     if (this.onInsertRow)
/* 1297 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8"), getExceptionInterceptor());
/* 1298 */     if (this.rowData.size() == 0)
/* 1299 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9"), getExceptionInterceptor());
/* 1300 */     if (isBeforeFirst())
/* 1301 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10"), getExceptionInterceptor());
/* 1302 */     if (isAfterLast()) {
/* 1303 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1306 */     refreshRow(this.updater, this.thisRow);
/*      */   }
/*      */ 
/*      */   private synchronized void refreshRow(PreparedStatement updateInsertStmt, ResultSetRow rowToRefresh) throws SQLException
/*      */   {
/* 1311 */     if (this.refresher == null) {
/* 1312 */       if (this.refreshSQL == null) {
/* 1313 */         generateStatements();
/*      */       }
/*      */ 
/* 1316 */       this.refresher = ((PreparedStatement)this.connection.clientPrepareStatement(this.refreshSQL));
/*      */     }
/*      */ 
/* 1320 */     this.refresher.clearParameters();
/*      */ 
/* 1322 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/* 1324 */     if (numKeys == 1) {
/* 1325 */       byte[] dataFrom = null;
/* 1326 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */ 
/* 1328 */       if ((!this.doingUpdates) && (!this.onInsertRow)) {
/* 1329 */         dataFrom = rowToRefresh.getColumnValue(index);
/*      */       } else {
/* 1331 */         dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */ 
/* 1334 */         if ((updateInsertStmt.isNull(index)) || (dataFrom.length == 0))
/* 1335 */           dataFrom = rowToRefresh.getColumnValue(index);
/*      */         else {
/* 1337 */           dataFrom = stripBinaryPrefix(dataFrom);
/*      */         }
/*      */       }
/*      */ 
/* 1341 */       if (this.fields[index].getvalueNeedsQuoting())
/* 1342 */         this.refresher.setBytesNoEscape(1, dataFrom);
/*      */       else
/* 1344 */         this.refresher.setBytesNoEscapeNoQuotes(1, dataFrom);
/*      */     }
/*      */     else
/*      */     {
/* 1348 */       for (int i = 0; i < numKeys; i++) {
/* 1349 */         byte[] dataFrom = null;
/* 1350 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/* 1353 */         if ((!this.doingUpdates) && (!this.onInsertRow)) {
/* 1354 */           dataFrom = rowToRefresh.getColumnValue(index);
/*      */         } else {
/* 1356 */           dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */ 
/* 1359 */           if ((updateInsertStmt.isNull(index)) || (dataFrom.length == 0))
/* 1360 */             dataFrom = rowToRefresh.getColumnValue(index);
/*      */           else {
/* 1362 */             dataFrom = stripBinaryPrefix(dataFrom);
/*      */           }
/*      */         }
/*      */ 
/* 1366 */         this.refresher.setBytesNoEscape(i + 1, dataFrom);
/*      */       }
/*      */     }
/*      */ 
/* 1370 */     ResultSet rs = null;
/*      */     try
/*      */     {
/* 1373 */       rs = this.refresher.executeQuery();
/*      */ 
/* 1375 */       int numCols = rs.getMetaData().getColumnCount();
/*      */ 
/* 1377 */       if (rs.next()) {
/* 1378 */         for (int i = 0; i < numCols; i++) {
/* 1379 */           byte[] val = rs.getBytes(i + 1);
/*      */ 
/* 1381 */           if ((val == null) || (rs.wasNull()))
/* 1382 */             rowToRefresh.setColumnValue(i, null);
/*      */           else
/* 1384 */             rowToRefresh.setColumnValue(i, rs.getBytes(i + 1));
/*      */         }
/*      */       }
/*      */       else {
/* 1388 */         throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1393 */       if (rs != null)
/*      */         try {
/* 1395 */           rs.close();
/*      */         }
/*      */         catch (SQLException ex)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 1430 */     return super.relative(rows);
/*      */   }
/*      */ 
/*      */   private void resetInserter() throws SQLException {
/* 1434 */     this.inserter.clearParameters();
/*      */ 
/* 1436 */     for (int i = 0; i < this.fields.length; i++)
/* 1437 */       this.inserter.setNull(i + 1, 0);
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 1457 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 1475 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 1493 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   protected void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 1503 */     super.setResultSetConcurrency(concurrencyFlag);
/*      */   }
/*      */ 
/*      */   private byte[] stripBinaryPrefix(byte[] dataFrom)
/*      */   {
/* 1517 */     return StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
/*      */   }
/*      */ 
/*      */   protected synchronized void syncUpdate()
/*      */     throws SQLException
/*      */   {
/* 1528 */     if (this.updater == null) {
/* 1529 */       if (this.updateSQL == null) {
/* 1530 */         generateStatements();
/*      */       }
/*      */ 
/* 1533 */       this.updater = ((PreparedStatement)this.connection.clientPrepareStatement(this.updateSQL));
/*      */     }
/*      */ 
/* 1537 */     int numFields = this.fields.length;
/* 1538 */     this.updater.clearParameters();
/*      */ 
/* 1540 */     for (int i = 0; i < numFields; i++) {
/* 1541 */       if (this.thisRow.getColumnValue(i) != null)
/*      */       {
/* 1543 */         if (this.fields[i].getvalueNeedsQuoting()) {
/* 1544 */           this.updater.setBytes(i + 1, this.thisRow.getColumnValue(i), this.fields[i].isBinary(), false);
/*      */         }
/*      */         else
/* 1547 */           this.updater.setBytesNoEscapeNoQuotes(i + 1, this.thisRow.getColumnValue(i));
/*      */       }
/*      */       else {
/* 1550 */         this.updater.setNull(i + 1, 0);
/*      */       }
/*      */     }
/*      */ 
/* 1554 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/* 1556 */     if (numKeys == 1) {
/* 1557 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/* 1558 */       setParamValue(this.updater, numFields + 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */     }
/*      */     else {
/* 1561 */       for (int i = 0; i < numKeys; i++) {
/* 1562 */         int idx = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/* 1563 */         setParamValue(this.updater, numFields + i + 1, this.thisRow, idx, this.fields[idx].getSQLType());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1588 */     if (!this.onInsertRow) {
/* 1589 */       if (!this.doingUpdates) {
/* 1590 */         this.doingUpdates = true;
/* 1591 */         syncUpdate();
/*      */       }
/*      */ 
/* 1594 */       this.updater.setAsciiStream(columnIndex, x, length);
/*      */     } else {
/* 1596 */       this.inserter.setAsciiStream(columnIndex, x, length);
/* 1597 */       this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1620 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1639 */     if (!this.onInsertRow) {
/* 1640 */       if (!this.doingUpdates) {
/* 1641 */         this.doingUpdates = true;
/* 1642 */         syncUpdate();
/*      */       }
/*      */ 
/* 1645 */       this.updater.setBigDecimal(columnIndex, x);
/*      */     } else {
/* 1647 */       this.inserter.setBigDecimal(columnIndex, x);
/*      */ 
/* 1649 */       if (x == null)
/* 1650 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1652 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x.toString()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1673 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1695 */     if (!this.onInsertRow) {
/* 1696 */       if (!this.doingUpdates) {
/* 1697 */         this.doingUpdates = true;
/* 1698 */         syncUpdate();
/*      */       }
/*      */ 
/* 1701 */       this.updater.setBinaryStream(columnIndex, x, length);
/*      */     } else {
/* 1703 */       this.inserter.setBinaryStream(columnIndex, x, length);
/*      */ 
/* 1705 */       if (x == null)
/* 1706 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1708 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1732 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBlob(int columnIndex, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1740 */     if (!this.onInsertRow) {
/* 1741 */       if (!this.doingUpdates) {
/* 1742 */         this.doingUpdates = true;
/* 1743 */         syncUpdate();
/*      */       }
/*      */ 
/* 1746 */       this.updater.setBlob(columnIndex, blob);
/*      */     } else {
/* 1748 */       this.inserter.setBlob(columnIndex, blob);
/*      */ 
/* 1750 */       if (blob == null)
/* 1751 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1753 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBlob(String columnName, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1763 */     updateBlob(findColumn(columnName), blob);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1782 */     if (!this.onInsertRow) {
/* 1783 */       if (!this.doingUpdates) {
/* 1784 */         this.doingUpdates = true;
/* 1785 */         syncUpdate();
/*      */       }
/*      */ 
/* 1788 */       this.updater.setBoolean(columnIndex, x);
/*      */     } else {
/* 1790 */       this.inserter.setBoolean(columnIndex, x);
/*      */ 
/* 1792 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1813 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1832 */     if (!this.onInsertRow) {
/* 1833 */       if (!this.doingUpdates) {
/* 1834 */         this.doingUpdates = true;
/* 1835 */         syncUpdate();
/*      */       }
/*      */ 
/* 1838 */       this.updater.setByte(columnIndex, x);
/*      */     } else {
/* 1840 */       this.inserter.setByte(columnIndex, x);
/*      */ 
/* 1842 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 1863 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1882 */     if (!this.onInsertRow) {
/* 1883 */       if (!this.doingUpdates) {
/* 1884 */         this.doingUpdates = true;
/* 1885 */         syncUpdate();
/*      */       }
/*      */ 
/* 1888 */       this.updater.setBytes(columnIndex, x);
/*      */     } else {
/* 1890 */       this.inserter.setBytes(columnIndex, x);
/*      */ 
/* 1892 */       this.thisRow.setColumnValue(columnIndex - 1, x);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1912 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 1934 */     if (!this.onInsertRow) {
/* 1935 */       if (!this.doingUpdates) {
/* 1936 */         this.doingUpdates = true;
/* 1937 */         syncUpdate();
/*      */       }
/*      */ 
/* 1940 */       this.updater.setCharacterStream(columnIndex, x, length);
/*      */     } else {
/* 1942 */       this.inserter.setCharacterStream(columnIndex, x, length);
/*      */ 
/* 1944 */       if (x == null)
/* 1945 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       else
/* 1947 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1971 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */   public void updateClob(int columnIndex, Clob clob)
/*      */     throws SQLException
/*      */   {
/* 1979 */     if (clob == null)
/* 1980 */       updateNull(columnIndex);
/*      */     else
/* 1982 */       updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
/*      */   }
/*      */ 
/*      */   public synchronized void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 2003 */     if (!this.onInsertRow) {
/* 2004 */       if (!this.doingUpdates) {
/* 2005 */         this.doingUpdates = true;
/* 2006 */         syncUpdate();
/*      */       }
/*      */ 
/* 2009 */       this.updater.setDate(columnIndex, x);
/*      */     } else {
/* 2011 */       this.inserter.setDate(columnIndex, x);
/*      */ 
/* 2013 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 2034 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 2053 */     if (!this.onInsertRow) {
/* 2054 */       if (!this.doingUpdates) {
/* 2055 */         this.doingUpdates = true;
/* 2056 */         syncUpdate();
/*      */       }
/*      */ 
/* 2059 */       this.updater.setDouble(columnIndex, x);
/*      */     } else {
/* 2061 */       this.inserter.setDouble(columnIndex, x);
/*      */ 
/* 2063 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 2084 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 2103 */     if (!this.onInsertRow) {
/* 2104 */       if (!this.doingUpdates) {
/* 2105 */         this.doingUpdates = true;
/* 2106 */         syncUpdate();
/*      */       }
/*      */ 
/* 2109 */       this.updater.setFloat(columnIndex, x);
/*      */     } else {
/* 2111 */       this.inserter.setFloat(columnIndex, x);
/*      */ 
/* 2113 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 2134 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 2153 */     if (!this.onInsertRow) {
/* 2154 */       if (!this.doingUpdates) {
/* 2155 */         this.doingUpdates = true;
/* 2156 */         syncUpdate();
/*      */       }
/*      */ 
/* 2159 */       this.updater.setInt(columnIndex, x);
/*      */     } else {
/* 2161 */       this.inserter.setInt(columnIndex, x);
/*      */ 
/* 2163 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 2184 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2203 */     if (!this.onInsertRow) {
/* 2204 */       if (!this.doingUpdates) {
/* 2205 */         this.doingUpdates = true;
/* 2206 */         syncUpdate();
/*      */       }
/*      */ 
/* 2209 */       this.updater.setLong(columnIndex, x);
/*      */     } else {
/* 2211 */       this.inserter.setLong(columnIndex, x);
/*      */ 
/* 2213 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 2234 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2250 */     if (!this.onInsertRow) {
/* 2251 */       if (!this.doingUpdates) {
/* 2252 */         this.doingUpdates = true;
/* 2253 */         syncUpdate();
/*      */       }
/*      */ 
/* 2256 */       this.updater.setNull(columnIndex, 0);
/*      */     } else {
/* 2258 */       this.inserter.setNull(columnIndex, 0);
/*      */ 
/* 2260 */       this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2277 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 2296 */     if (!this.onInsertRow) {
/* 2297 */       if (!this.doingUpdates) {
/* 2298 */         this.doingUpdates = true;
/* 2299 */         syncUpdate();
/*      */       }
/*      */ 
/* 2302 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2304 */       this.inserter.setObject(columnIndex, x);
/*      */ 
/* 2306 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2331 */     if (!this.onInsertRow) {
/* 2332 */       if (!this.doingUpdates) {
/* 2333 */         this.doingUpdates = true;
/* 2334 */         syncUpdate();
/*      */       }
/*      */ 
/* 2337 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2339 */       this.inserter.setObject(columnIndex, x);
/*      */ 
/* 2341 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2362 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2385 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateRow()
/*      */     throws SQLException
/*      */   {
/* 2399 */     if (!this.isUpdatable) {
/* 2400 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */ 
/* 2403 */     if (this.doingUpdates) {
/* 2404 */       this.updater.executeUpdate();
/* 2405 */       refreshRow();
/* 2406 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 2412 */     syncUpdate();
/*      */   }
/*      */ 
/*      */   public synchronized void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2431 */     if (!this.onInsertRow) {
/* 2432 */       if (!this.doingUpdates) {
/* 2433 */         this.doingUpdates = true;
/* 2434 */         syncUpdate();
/*      */       }
/*      */ 
/* 2437 */       this.updater.setShort(columnIndex, x);
/*      */     } else {
/* 2439 */       this.inserter.setShort(columnIndex, x);
/*      */ 
/* 2441 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 2462 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2481 */     checkClosed();
/*      */ 
/* 2483 */     if (!this.onInsertRow) {
/* 2484 */       if (!this.doingUpdates) {
/* 2485 */         this.doingUpdates = true;
/* 2486 */         syncUpdate();
/*      */       }
/*      */ 
/* 2489 */       this.updater.setString(columnIndex, x);
/*      */     } else {
/* 2491 */       this.inserter.setString(columnIndex, x);
/*      */ 
/* 2493 */       if (x == null) {
/* 2494 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       }
/* 2496 */       else if (getCharConverter() != null) {
/* 2497 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */       }
/*      */       else
/*      */       {
/* 2502 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 2524 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2543 */     if (!this.onInsertRow) {
/* 2544 */       if (!this.doingUpdates) {
/* 2545 */         this.doingUpdates = true;
/* 2546 */         syncUpdate();
/*      */       }
/*      */ 
/* 2549 */       this.updater.setTime(columnIndex, x);
/*      */     } else {
/* 2551 */       this.inserter.setTime(columnIndex, x);
/*      */ 
/* 2553 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2574 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2593 */     if (!this.onInsertRow) {
/* 2594 */       if (!this.doingUpdates) {
/* 2595 */         this.doingUpdates = true;
/* 2596 */         syncUpdate();
/*      */       }
/*      */ 
/* 2599 */       this.updater.setTimestamp(columnIndex, x);
/*      */     } else {
/* 2601 */       this.inserter.setTimestamp(columnIndex, x);
/*      */ 
/* 2603 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2624 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.UpdatableResultSet
 * JD-Core Version:    0.6.0
 */