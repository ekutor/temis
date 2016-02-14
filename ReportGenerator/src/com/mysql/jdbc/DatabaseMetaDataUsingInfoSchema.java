/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.List;
/*      */ 
/*      */ public class DatabaseMetaDataUsingInfoSchema extends DatabaseMetaData
/*      */ {
/*      */   private boolean hasReferentialConstraintsView;
/*      */   private final boolean hasParametersView;
/*      */ 
/*      */   protected DatabaseMetaDataUsingInfoSchema(MySQLConnection connToSet, String databaseToSet)
/*      */     throws SQLException
/*      */   {
/*   44 */     super(connToSet, databaseToSet);
/*      */ 
/*   46 */     this.hasReferentialConstraintsView = this.conn.versionMeetsMinimum(5, 1, 10);
/*      */ 
/*   49 */     ResultSet rs = null;
/*      */     try
/*      */     {
/*   52 */       rs = super.getTables("INFORMATION_SCHEMA", null, "PARAMETERS", new String[0]);
/*      */ 
/*   54 */       this.hasParametersView = rs.next();
/*      */     } finally {
/*   56 */       if (rs != null)
/*   57 */         rs.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSet executeMetadataQuery(PreparedStatement pStmt)
/*      */     throws SQLException
/*      */   {
/*   64 */     ResultSet rs = pStmt.executeQuery();
/*   65 */     ((ResultSetInternalMethods)rs).setOwningStatement(null);
/*      */ 
/*   67 */     return rs;
/*      */   }
/*      */ 
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  108 */     if (columnNamePattern == null) {
/*  109 */       if (this.conn.getNullNamePatternMatchesAll())
/*  110 */         columnNamePattern = "%";
/*      */       else {
/*  112 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  118 */     if ((catalog == null) && 
/*  119 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  120 */       catalog = this.database;
/*      */     }
/*      */ 
/*  124 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME,COLUMN_NAME, NULL AS GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME =? AND COLUMN_NAME LIKE ? ORDER BY COLUMN_NAME, PRIVILEGE_TYPE";
/*      */ 
/*  131 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  134 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  136 */       if (catalog != null)
/*  137 */         pStmt.setString(1, catalog);
/*      */       else {
/*  139 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  142 */       pStmt.setString(2, table);
/*  143 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/*  145 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  146 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 64), new Field("", "TABLE_SCHEM", 1, 1), new Field("", "TABLE_NAME", 1, 64), new Field("", "COLUMN_NAME", 1, 64), new Field("", "GRANTOR", 1, 77), new Field("", "GRANTEE", 1, 77), new Field("", "PRIVILEGE", 1, 64), new Field("", "IS_GRANTABLE", 1, 3) });
/*      */ 
/*  156 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  158 */       if (pStmt != null)
/*  159 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableName, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  210 */     if (columnNamePattern == null) {
/*  211 */       if (this.conn.getNullNamePatternMatchesAll())
/*  212 */         columnNamePattern = "%";
/*      */       else {
/*  214 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  220 */     if ((catalog == null) && 
/*  221 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  222 */       catalog = this.database;
/*      */     }
/*      */ 
/*  226 */     StringBuffer sqlBuf = new StringBuffer("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,TABLE_NAME,COLUMN_NAME,");
/*      */ 
/*  229 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */ 
/*  231 */     sqlBuf.append(" AS DATA_TYPE, ");
/*      */ 
/*  233 */     if (this.conn.getCapitalizeTypeNames())
/*  234 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 AND LOCATE('set', DATA_TYPE) <> 1 AND LOCATE('enum', DATA_TYPE) <> 1 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS TYPE_NAME,");
/*      */     else {
/*  236 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 AND LOCATE('set', DATA_TYPE) <> 1 AND LOCATE('enum', DATA_TYPE) <> 1 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS TYPE_NAME,");
/*      */     }
/*      */ 
/*  239 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, " + MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH," + "NUMERIC_SCALE AS DECIMAL_DIGITS," + "10 AS NUM_PREC_RADIX," + "CASE WHEN IS_NULLABLE='NO' THEN " + 0 + " ELSE CASE WHEN IS_NULLABLE='YES' THEN " + 1 + " ELSE " + 2 + " END END AS NULLABLE," + "COLUMN_COMMENT AS REMARKS," + "COLUMN_DEFAULT AS COLUMN_DEF," + "0 AS SQL_DATA_TYPE," + "0 AS SQL_DATETIME_SUB," + "CASE WHEN CHARACTER_OCTET_LENGTH > " + 2147483647 + " THEN " + 2147483647 + " ELSE CHARACTER_OCTET_LENGTH END AS CHAR_OCTET_LENGTH," + "ORDINAL_POSITION," + "IS_NULLABLE," + "NULL AS SCOPE_CATALOG," + "NULL AS SCOPE_SCHEMA," + "NULL AS SCOPE_TABLE," + "NULL AS SOURCE_DATA_TYPE," + "IF (EXTRA LIKE '%auto_increment%','YES','NO') AS IS_AUTOINCREMENT " + "FROM INFORMATION_SCHEMA.COLUMNS WHERE ");
/*      */ 
/*  260 */     boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase(catalog);
/*      */ 
/*  262 */     if (catalog != null) {
/*  263 */       if ((operatingOnInformationSchema) || ((StringUtils.indexOfIgnoreCase(0, catalog, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, catalog, "_") == -1)))
/*      */       {
/*  265 */         sqlBuf.append("TABLE_SCHEMA = ? AND ");
/*      */       }
/*  267 */       else sqlBuf.append("TABLE_SCHEMA LIKE ? AND ");
/*      */     }
/*      */     else
/*      */     {
/*  271 */       sqlBuf.append("TABLE_SCHEMA LIKE ? AND ");
/*      */     }
/*      */ 
/*  274 */     if (tableName != null) {
/*  275 */       if ((StringUtils.indexOfIgnoreCase(0, tableName, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, tableName, "_") == -1))
/*      */       {
/*  277 */         sqlBuf.append("TABLE_NAME = ? AND ");
/*      */       }
/*  279 */       else sqlBuf.append("TABLE_NAME LIKE ? AND ");
/*      */     }
/*      */     else
/*      */     {
/*  283 */       sqlBuf.append("TABLE_NAME LIKE ? AND ");
/*      */     }
/*      */ 
/*  286 */     if ((StringUtils.indexOfIgnoreCase(0, columnNamePattern, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, columnNamePattern, "_") == -1))
/*      */     {
/*  288 */       sqlBuf.append("COLUMN_NAME = ? ");
/*      */     }
/*  290 */     else sqlBuf.append("COLUMN_NAME LIKE ? ");
/*      */ 
/*  292 */     sqlBuf.append("ORDER BY TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");
/*      */ 
/*  294 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  297 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/*  299 */       if (catalog != null)
/*  300 */         pStmt.setString(1, catalog);
/*      */       else {
/*  302 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  305 */       pStmt.setString(2, tableName);
/*  306 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/*  308 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  310 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createColumnsFields());
/*  311 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  313 */       if (pStmt != null)
/*  314 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/*  389 */     if (primaryTable == null) {
/*  390 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  394 */     if ((primaryCatalog == null) && 
/*  395 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  396 */       primaryCatalog = this.database;
/*      */     }
/*      */ 
/*  400 */     if ((foreignCatalog == null) && 
/*  401 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  402 */       foreignCatalog = this.database;
/*      */     }
/*      */ 
/*  406 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "AND A.TABLE_SCHEMA LIKE ? AND A.TABLE_NAME=? " + "ORDER BY " + "A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
/*      */ 
/*  440 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  443 */       pStmt = prepareMetaDataSafeStatement(sql);
/*  444 */       if (primaryCatalog != null)
/*  445 */         pStmt.setString(1, primaryCatalog);
/*      */       else {
/*  447 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  450 */       pStmt.setString(2, primaryTable);
/*      */ 
/*  452 */       if (foreignCatalog != null)
/*  453 */         pStmt.setString(3, foreignCatalog);
/*      */       else {
/*  455 */         pStmt.setString(3, "%");
/*      */       }
/*      */ 
/*  458 */       pStmt.setString(4, foreignTable);
/*      */ 
/*  460 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  461 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */ 
/*  463 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  465 */       if (pStmt != null)
/*  466 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  534 */     if (table == null) {
/*  535 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  539 */     if ((catalog == null) && 
/*  540 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  541 */       catalog = this.database;
/*      */     }
/*      */ 
/*  547 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME,A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
/*      */ 
/*  580 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  583 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  585 */       if (catalog != null)
/*  586 */         pStmt.setString(1, catalog);
/*      */       else {
/*  588 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  591 */       pStmt.setString(2, table);
/*      */ 
/*  593 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  595 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */ 
/*  597 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  599 */       if (pStmt != null)
/*  600 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private String generateOptionalRefContraintsJoin()
/*      */   {
/*  607 */     return this.hasReferentialConstraintsView ? "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND R.TABLE_NAME = B.TABLE_NAME AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA) " : "";
/*      */   }
/*      */ 
/*      */   private String generateDeleteRuleClause()
/*      */   {
/*  615 */     return this.hasReferentialConstraintsView ? "CASE WHEN R.DELETE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.DELETE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.DELETE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.DELETE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.DELETE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END " : String.valueOf(1);
/*      */   }
/*      */ 
/*      */   private String generateUpdateRuleClause()
/*      */   {
/*  625 */     return this.hasReferentialConstraintsView ? "CASE WHEN R.UPDATE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.UPDATE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.UPDATE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.UPDATE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.UPDATE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END " : String.valueOf(1);
/*      */   }
/*      */ 
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  695 */     if (table == null) {
/*  696 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  700 */     if ((catalog == null) && 
/*  701 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  702 */       catalog = this.database;
/*      */     }
/*      */ 
/*  706 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,A.TABLE_SCHEMA AS FKTABLE_CAT,NULL AS FKTABLE_SCHEM,A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A " + "JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING " + "(CONSTRAINT_NAME, TABLE_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.TABLE_SCHEMA LIKE ? " + "AND A.TABLE_NAME=? " + "AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL " + "ORDER BY " + "A.REFERENCED_TABLE_SCHEMA, A.REFERENCED_TABLE_NAME, " + "A.ORDINAL_POSITION";
/*      */ 
/*  743 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  746 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  748 */       if (catalog != null)
/*  749 */         pStmt.setString(1, catalog);
/*      */       else {
/*  751 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  754 */       pStmt.setString(2, table);
/*      */ 
/*  756 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  758 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */ 
/*  760 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  762 */       if (pStmt != null)
/*  763 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/*  828 */     StringBuffer sqlBuf = new StringBuffer("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,TABLE_NAME,NON_UNIQUE,TABLE_SCHEMA AS INDEX_QUALIFIER,INDEX_NAME,3 AS TYPE,SEQ_IN_INDEX AS ORDINAL_POSITION,COLUMN_NAME,COLLATION AS ASC_OR_DESC,CARDINALITY,NULL AS PAGES,NULL AS FILTER_CONDITION FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ?");
/*      */ 
/*  839 */     if (unique) {
/*  840 */       sqlBuf.append(" AND NON_UNIQUE=0 ");
/*      */     }
/*      */ 
/*  843 */     sqlBuf.append("ORDER BY NON_UNIQUE, INDEX_NAME, SEQ_IN_INDEX");
/*      */ 
/*  845 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  848 */       if ((catalog == null) && 
/*  849 */         (this.conn.getNullCatalogMeansCurrent())) {
/*  850 */         catalog = this.database;
/*      */       }
/*      */ 
/*  854 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/*  856 */       if (catalog != null)
/*  857 */         pStmt.setString(1, catalog);
/*      */       else {
/*  859 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  862 */       pStmt.setString(2, table);
/*      */ 
/*  864 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/*  866 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createIndexInfoFields());
/*      */ 
/*  868 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  870 */       if (pStmt != null)
/*  871 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  904 */     if ((catalog == null) && 
/*  905 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  906 */       catalog = this.database;
/*      */     }
/*      */ 
/*  910 */     if (table == null) {
/*  911 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  915 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, SEQ_IN_INDEX AS KEY_SEQ, 'PRIMARY' AS PK_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND INDEX_NAME='PRIMARY' ORDER BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";
/*      */ 
/*  920 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  923 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/*  925 */       if (catalog != null)
/*  926 */         pStmt.setString(1, catalog);
/*      */       else {
/*  928 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/*  931 */       pStmt.setString(2, table);
/*      */ 
/*  933 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  934 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 5), new Field("", "PK_NAME", 1, 32) });
/*      */ 
/*  942 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/*  944 */       if (pStmt != null)
/*  945 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/*  993 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/*  995 */       if (this.conn.getNullNamePatternMatchesAll())
/*  996 */         procedureNamePattern = "%";
/*      */       else {
/*  998 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1004 */     String db = null;
/*      */ 
/* 1006 */     if (catalog == null) {
/* 1007 */       if (this.conn.getNullCatalogMeansCurrent())
/* 1008 */         db = this.database;
/*      */     }
/*      */     else {
/* 1011 */       db = catalog;
/*      */     }
/*      */ 
/* 1014 */     String sql = "SELECT ROUTINE_SCHEMA AS PROCEDURE_CAT, NULL AS PROCEDURE_SCHEM, ROUTINE_NAME AS PROCEDURE_NAME, NULL AS RESERVED_1, NULL AS RESERVED_2, NULL AS RESERVED_3, ROUTINE_COMMENT AS REMARKS, CASE WHEN ROUTINE_TYPE = 'PROCEDURE' THEN 1 WHEN ROUTINE_TYPE='FUNCTION' THEN 2 ELSE 0 END AS PROCEDURE_TYPE FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_SCHEMA LIKE ? AND ROUTINE_NAME LIKE ? ORDER BY ROUTINE_SCHEMA, ROUTINE_NAME";
/*      */ 
/* 1027 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1030 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/* 1032 */       if (db != null)
/* 1033 */         pStmt.setString(1, db);
/*      */       else {
/* 1035 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1038 */       pStmt.setString(2, procedureNamePattern);
/*      */ 
/* 1040 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1041 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "PROCEDURE_CAT", 1, 0), new Field("", "PROCEDURE_SCHEM", 1, 0), new Field("", "PROCEDURE_NAME", 1, 0), new Field("", "reserved1", 1, 0), new Field("", "reserved2", 1, 0), new Field("", "reserved3", 1, 0), new Field("", "REMARKS", 1, 0), new Field("", "PROCEDURE_TYPE", 5, 0) });
/*      */ 
/* 1051 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/* 1053 */       if (pStmt != null)
/* 1054 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1157 */     if (!this.conn.versionMeetsMinimum(5, 4, 0)) {
/* 1158 */       return super.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1162 */     if (!this.hasParametersView) {
/* 1163 */       return super.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1166 */     if ((functionNamePattern == null) || (functionNamePattern.length() == 0))
/*      */     {
/* 1168 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1169 */         functionNamePattern = "%";
/*      */       else {
/* 1171 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1177 */     String db = null;
/*      */ 
/* 1179 */     if (catalog == null) {
/* 1180 */       if (this.conn.getNullCatalogMeansCurrent())
/* 1181 */         db = this.database;
/*      */     }
/*      */     else {
/* 1184 */       db = catalog;
/*      */     }
/*      */ 
/* 1194 */     StringBuffer sqlBuf = new StringBuffer("SELECT SPECIFIC_SCHEMA AS FUNCTION_CAT, NULL AS `FUNCTION_SCHEM`, SPECIFIC_NAME AS `FUNCTION_NAME`, PARAMETER_NAME AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN 1 WHEN PARAMETER_MODE='OUT' THEN 3 WHEN PARAMETER_MODE='INOUT' THEN 2 WHEN ORDINAL_POSITION=0 THEN 4 ELSE 0 END AS `COLUMN_TYPE`, ");
/*      */ 
/* 1206 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */ 
/* 1208 */     sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */ 
/* 1211 */     if (this.conn.getCapitalizeTypeNames())
/* 1212 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
/*      */     else {
/* 1214 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
/*      */     }
/*      */ 
/* 1218 */     sqlBuf.append("NUMERIC_PRECISION AS `PRECISION`, ");
/*      */ 
/* 1220 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */ 
/* 1225 */     sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*      */ 
/* 1227 */     sqlBuf.append("10 AS RADIX,");
/*      */ 
/* 1234 */     sqlBuf.append("2 AS `NULLABLE`,  NULL AS `REMARKS`, CHARACTER_OCTET_LENGTH AS `CHAR_OCTET_LENGTH`,  ORDINAL_POSITION, '' AS `IS_NULLABLE`, SPECIFIC_NAME FROM INFORMATION_SCHEMA.PARAMETERS WHERE SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) AND ROUTINE_TYPE='FUNCTION' ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION");
/*      */ 
/* 1244 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1247 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/* 1249 */       if (db != null)
/* 1250 */         pStmt.setString(1, db);
/*      */       else {
/* 1252 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1255 */       pStmt.setString(2, functionNamePattern);
/* 1256 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/* 1258 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1259 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFunctionColumnsFields());
/*      */ 
/* 1261 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/* 1263 */       if (pStmt != null)
/* 1264 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1335 */     if (!this.conn.versionMeetsMinimum(5, 4, 0)) {
/* 1336 */       return super.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1340 */     if (!this.hasParametersView) {
/* 1341 */       return super.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*      */     }
/*      */ 
/* 1344 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/* 1346 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1347 */         procedureNamePattern = "%";
/*      */       else {
/* 1349 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1355 */     String db = null;
/*      */ 
/* 1357 */     if (catalog == null) {
/* 1358 */       if (this.conn.getNullCatalogMeansCurrent())
/* 1359 */         db = this.database;
/*      */     }
/*      */     else {
/* 1362 */       db = catalog;
/*      */     }
/*      */ 
/* 1381 */     StringBuffer sqlBuf = new StringBuffer("SELECT SPECIFIC_SCHEMA AS PROCEDURE_CAT, NULL AS `PROCEDURE_SCHEM`, SPECIFIC_NAME AS `PROCEDURE_NAME`, PARAMETER_NAME AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN 1 WHEN PARAMETER_MODE='OUT' THEN 4 WHEN PARAMETER_MODE='INOUT' THEN 2 WHEN ORDINAL_POSITION=0 THEN 5 ELSE 0 END AS `COLUMN_TYPE`, ");
/*      */ 
/* 1393 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */ 
/* 1395 */     sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */ 
/* 1398 */     if (this.conn.getCapitalizeTypeNames())
/* 1399 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
/*      */     else {
/* 1401 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
/*      */     }
/*      */ 
/* 1405 */     sqlBuf.append("NUMERIC_PRECISION AS `PRECISION`, ");
/*      */ 
/* 1407 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */ 
/* 1412 */     sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*      */ 
/* 1414 */     sqlBuf.append("10 AS RADIX,");
/* 1415 */     sqlBuf.append("2 AS `NULLABLE`,  NULL AS `REMARKS` FROM INFORMATION_SCHEMA.PARAMETERS WHERE SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION");
/*      */ 
/* 1421 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1424 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/* 1426 */       if (db != null)
/* 1427 */         pStmt.setString(1, db);
/*      */       else {
/* 1429 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1432 */       pStmt.setString(2, procedureNamePattern);
/* 1433 */       pStmt.setString(3, columnNamePattern);
/*      */ 
/* 1435 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1436 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createProcedureColumnsFields());
/*      */ 
/* 1438 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/* 1440 */       if (pStmt != null)
/* 1441 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/* 1484 */     if ((catalog == null) && 
/* 1485 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1486 */       catalog = this.database;
/*      */     }
/*      */ 
/* 1490 */     if (tableNamePattern == null) {
/* 1491 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1492 */         tableNamePattern = "%";
/*      */       else {
/* 1494 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1501 */     String tmpCat = "";
/*      */ 
/* 1503 */     if ((catalog == null) || (catalog.length() == 0)) {
/* 1504 */       if (this.conn.getNullCatalogMeansCurrent())
/* 1505 */         tmpCat = this.database;
/*      */     }
/*      */     else {
/* 1508 */       tmpCat = catalog;
/*      */     }
/*      */ 
/* 1511 */     List parseList = StringUtils.splitDBdotName(tableNamePattern, tmpCat, this.quotedId, this.conn.isNoBackslashEscapesSet());
/*      */     String tableNamePat;
/*      */     String tableNamePat;
/* 1514 */     if (parseList.size() == 2)
/* 1515 */       tableNamePat = (String)parseList.get(1);
/*      */     else {
/* 1517 */       tableNamePat = tableNamePattern;
/*      */     }
/*      */ 
/* 1520 */     PreparedStatement pStmt = null;
/*      */ 
/* 1522 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, CASE WHEN TABLE_TYPE='BASE TABLE' THEN 'TABLE' WHEN TABLE_TYPE='TEMPORARY' THEN 'LOCAL_TEMPORARY' ELSE TABLE_TYPE END AS TABLE_TYPE, TABLE_COMMENT AS REMARKS FROM INFORMATION_SCHEMA.TABLES WHERE ";
/*      */ 
/* 1528 */     boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase(catalog);
/* 1529 */     if (catalog != null) {
/* 1530 */       if ((operatingOnInformationSchema) || ((StringUtils.indexOfIgnoreCase(0, catalog, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, catalog, "_") == -1)))
/*      */       {
/* 1532 */         sql = sql + "TABLE_SCHEMA = ? AND ";
/*      */       }
/* 1534 */       else sql = sql + "TABLE_SCHEMA LIKE ? AND ";
/*      */     }
/*      */     else
/*      */     {
/* 1538 */       sql = sql + "TABLE_SCHEMA LIKE ? AND ";
/*      */     }
/*      */ 
/* 1541 */     if (tableNamePat != null) {
/* 1542 */       if ((StringUtils.indexOfIgnoreCase(0, tableNamePat, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, tableNamePat, "_") == -1))
/*      */       {
/* 1544 */         sql = sql + "TABLE_NAME = ? AND ";
/*      */       }
/* 1546 */       else sql = sql + "TABLE_NAME LIKE ? AND ";
/*      */     }
/*      */     else
/*      */     {
/* 1550 */       sql = sql + "TABLE_NAME LIKE ? AND ";
/*      */     }
/* 1552 */     sql = sql + "TABLE_TYPE IN (?,?,?) ";
/* 1553 */     sql = sql + "ORDER BY TABLE_TYPE, TABLE_SCHEMA, TABLE_NAME";
/*      */     try {
/* 1555 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */ 
/* 1557 */       if (catalog != null)
/* 1558 */         pStmt.setString(1, catalog);
/*      */       else {
/* 1560 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1563 */       pStmt.setString(2, tableNamePat);
/*      */ 
/* 1567 */       if ((types == null) || (types.length == 0)) {
/* 1568 */         pStmt.setString(3, "BASE TABLE");
/* 1569 */         pStmt.setString(4, "VIEW");
/* 1570 */         pStmt.setString(5, "TEMPORARY");
/*      */       } else {
/* 1572 */         pStmt.setNull(3, 12);
/* 1573 */         pStmt.setNull(4, 12);
/* 1574 */         pStmt.setNull(5, 12);
/*      */ 
/* 1576 */         for (int i = 0; i < types.length; i++) {
/* 1577 */           if ("TABLE".equalsIgnoreCase(types[i])) {
/* 1578 */             pStmt.setString(3, "BASE TABLE");
/*      */           }
/*      */ 
/* 1581 */           if ("VIEW".equalsIgnoreCase(types[i])) {
/* 1582 */             pStmt.setString(4, "VIEW");
/*      */           }
/*      */ 
/* 1585 */           if ("LOCAL TEMPORARY".equalsIgnoreCase(types[i])) {
/* 1586 */             pStmt.setString(5, "TEMPORARY");
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1591 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */ 
/* 1593 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 12, catalog == null ? 0 : catalog.length()), new Field("", "TABLE_SCHEM", 12, 0), new Field("", "TABLE_NAME", 12, 255), new Field("", "TABLE_TYPE", 12, 5), new Field("", "REMARKS", 12, 0) });
/*      */ 
/* 1601 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/* 1603 */       if (pStmt != null)
/* 1604 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean gethasParametersView()
/*      */   {
/* 1610 */     return this.hasParametersView;
/*      */   }
/*      */ 
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 1618 */     if ((catalog == null) && 
/* 1619 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1620 */       catalog = this.database;
/*      */     }
/*      */ 
/* 1624 */     if (table == null) {
/* 1625 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1629 */     StringBuffer sqlBuf = new StringBuffer("SELECT NULL AS SCOPE, COLUMN_NAME, ");
/*      */ 
/* 1631 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/* 1632 */     sqlBuf.append(" AS DATA_TYPE, ");
/*      */ 
/* 1634 */     sqlBuf.append("COLUMN_TYPE AS TYPE_NAME, ");
/* 1635 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, ");
/*      */ 
/* 1639 */     sqlBuf.append(MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH," + "NUMERIC_SCALE AS DECIMAL_DIGITS, " + Integer.toString(1) + " AS PSEUDO_COLUMN " + "FROM INFORMATION_SCHEMA.COLUMNS " + "WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ?" + " AND EXTRA LIKE '%on update CURRENT_TIMESTAMP%'");
/*      */ 
/* 1647 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1650 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */ 
/* 1652 */       if (catalog != null)
/* 1653 */         pStmt.setString(1, catalog);
/*      */       else {
/* 1655 */         pStmt.setString(1, "%");
/*      */       }
/*      */ 
/* 1658 */       pStmt.setString(2, table);
/*      */ 
/* 1660 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1661 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "SCOPE", 5, 5), new Field("", "COLUMN_NAME", 1, 32), new Field("", "DATA_TYPE", 4, 5), new Field("", "TYPE_NAME", 1, 16), new Field("", "COLUMN_SIZE", 4, 16), new Field("", "BUFFER_LENGTH", 4, 16), new Field("", "DECIMAL_DIGITS", 5, 16), new Field("", "PSEUDO_COLUMN", 5, 5) });
/*      */ 
/* 1672 */       localResultSet1 = rs;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/* 1674 */       if (pStmt != null)
/* 1675 */         pStmt.close();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema
 * JD-Core Version:    0.6.0
 */