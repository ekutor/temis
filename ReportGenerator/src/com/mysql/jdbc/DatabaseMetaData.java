/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.Connection;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class DatabaseMetaData
/*      */   implements java.sql.DatabaseMetaData
/*      */ {
/*      */   private static String mysqlKeywordsThatArentSQL92;
/*      */   protected static final int MAX_IDENTIFIER_LENGTH = 64;
/*      */   private static final int DEFERRABILITY = 13;
/*      */   private static final int DELETE_RULE = 10;
/*      */   private static final int FK_NAME = 11;
/*      */   private static final int FKCOLUMN_NAME = 7;
/*      */   private static final int FKTABLE_CAT = 4;
/*      */   private static final int FKTABLE_NAME = 6;
/*      */   private static final int FKTABLE_SCHEM = 5;
/*      */   private static final int KEY_SEQ = 8;
/*      */   private static final int PK_NAME = 12;
/*      */   private static final int PKCOLUMN_NAME = 3;
/*      */   private static final int PKTABLE_CAT = 0;
/*      */   private static final int PKTABLE_NAME = 2;
/*      */   private static final int PKTABLE_SCHEM = 1;
/*      */   private static final String SUPPORTS_FK = "SUPPORTS_FK";
/*  461 */   protected static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
/*      */ 
/*  463 */   protected static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
/*      */   private static final int UPDATE_RULE = 9;
/*  467 */   protected static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
/*      */   private static final Constructor<?> JDBC_4_DBMD_SHOW_CTOR;
/*      */   private static final Constructor<?> JDBC_4_DBMD_IS_CTOR;
/*      */   protected MySQLConnection conn;
/*  631 */   protected String database = null;
/*      */ 
/*  634 */   protected String quotedId = null;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */ 
/*      */   protected static DatabaseMetaData getInstance(MySQLConnection connToSet, String databaseToSet, boolean checkForInfoSchema)
/*      */     throws SQLException
/*      */   {
/*  642 */     if (!Util.isJdbc4()) {
/*  643 */       if ((checkForInfoSchema) && (connToSet != null) && (connToSet.getUseInformationSchema()) && (connToSet.versionMeetsMinimum(5, 0, 7)))
/*      */       {
/*  646 */         return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet);
/*      */       }
/*      */ 
/*  650 */       return new DatabaseMetaData(connToSet, databaseToSet);
/*      */     }
/*      */ 
/*  653 */     if ((checkForInfoSchema) && (connToSet != null) && (connToSet.getUseInformationSchema()) && (connToSet.versionMeetsMinimum(5, 0, 7)))
/*      */     {
/*  657 */       return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_IS_CTOR, new Object[] { connToSet, databaseToSet }, connToSet.getExceptionInterceptor());
/*      */     }
/*      */ 
/*  662 */     return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_SHOW_CTOR, new Object[] { connToSet, databaseToSet }, connToSet.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected DatabaseMetaData(MySQLConnection connToSet, String databaseToSet)
/*      */   {
/*  675 */     this.conn = connToSet;
/*  676 */     this.database = databaseToSet;
/*  677 */     this.exceptionInterceptor = this.conn.getExceptionInterceptor();
/*      */     try
/*      */     {
/*  680 */       this.quotedId = (this.conn.supportsQuotedIdentifiers() ? getIdentifierQuoteString() : "");
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  686 */       AssertionFailedException.shouldNotHappen(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean allProceduresAreCallable()
/*      */     throws SQLException
/*      */   {
/*  699 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean allTablesAreSelectable()
/*      */     throws SQLException
/*      */   {
/*  710 */     return false;
/*      */   }
/*      */ 
/*      */   private ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows) throws SQLException
/*      */   {
/*  715 */     return buildResultSet(fields, rows, this.conn);
/*      */   }
/*      */ 
/*      */   static ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows, MySQLConnection c) throws SQLException
/*      */   {
/*  720 */     int fieldsLength = fields.length;
/*      */ 
/*  722 */     for (int i = 0; i < fieldsLength; i++) {
/*  723 */       int jdbcType = fields[i].getSQLType();
/*      */ 
/*  725 */       switch (jdbcType) {
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*  729 */         fields[i].setCharacterSet(c.getCharacterSetMetadata());
/*  730 */         break;
/*      */       }
/*      */ 
/*  735 */       fields[i].setConnection(c);
/*  736 */       fields[i].setUseOldNameMetadata(true);
/*      */     }
/*      */ 
/*  739 */     return ResultSetImpl.getInstance(c.getCatalog(), fields, new RowDataStatic(rows), c, null, false);
/*      */   }
/*      */ 
/*      */   protected void convertToJdbcFunctionList(String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, Map<String, ResultSetRow> procedureRowsOrderedByName, int nameIndex, Field[] fields)
/*      */     throws SQLException
/*      */   {
/*  747 */     while (proceduresRs.next()) {
/*  748 */       boolean shouldAdd = true;
/*      */ 
/*  750 */       if (needsClientFiltering) {
/*  751 */         shouldAdd = false;
/*      */ 
/*  753 */         String procDb = proceduresRs.getString(1);
/*      */ 
/*  755 */         if ((db == null) && (procDb == null))
/*  756 */           shouldAdd = true;
/*  757 */         else if ((db != null) && (db.equals(procDb))) {
/*  758 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */ 
/*  762 */       if (shouldAdd) {
/*  763 */         String functionName = proceduresRs.getString(nameIndex);
/*      */ 
/*  765 */         byte[][] rowData = (byte[][])null;
/*      */ 
/*  767 */         if ((fields != null) && (fields.length == 9))
/*      */         {
/*  769 */           rowData = new byte[9][];
/*  770 */           rowData[0] = (catalog == null ? null : s2b(catalog));
/*  771 */           rowData[1] = null;
/*  772 */           rowData[2] = s2b(functionName);
/*  773 */           rowData[3] = null;
/*  774 */           rowData[4] = null;
/*  775 */           rowData[5] = null;
/*  776 */           rowData[6] = s2b(proceduresRs.getString("comment"));
/*  777 */           rowData[7] = s2b(Integer.toString(2));
/*  778 */           rowData[8] = s2b(functionName);
/*      */         }
/*      */         else {
/*  781 */           rowData = new byte[6][];
/*      */ 
/*  783 */           rowData[0] = (catalog == null ? null : s2b(catalog));
/*  784 */           rowData[1] = null;
/*  785 */           rowData[2] = s2b(functionName);
/*  786 */           rowData[3] = s2b(proceduresRs.getString("comment"));
/*  787 */           rowData[4] = s2b(Integer.toString(getJDBC4FunctionNoTableConstant()));
/*  788 */           rowData[5] = s2b(functionName);
/*      */         }
/*      */ 
/*  791 */         procedureRowsOrderedByName.put(functionName, new ByteArrayRow(rowData, getExceptionInterceptor()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getJDBC4FunctionNoTableConstant() {
/*  797 */     return 0;
/*      */   }
/*      */ 
/*      */   protected void convertToJdbcProcedureList(boolean fromSelect, String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, Map<String, ResultSetRow> procedureRowsOrderedByName, int nameIndex)
/*      */     throws SQLException
/*      */   {
/*  803 */     while (proceduresRs.next()) {
/*  804 */       boolean shouldAdd = true;
/*      */ 
/*  806 */       if (needsClientFiltering) {
/*  807 */         shouldAdd = false;
/*      */ 
/*  809 */         String procDb = proceduresRs.getString(1);
/*      */ 
/*  811 */         if ((db == null) && (procDb == null))
/*  812 */           shouldAdd = true;
/*  813 */         else if ((db != null) && (db.equals(procDb))) {
/*  814 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */ 
/*  818 */       if (shouldAdd) {
/*  819 */         String procedureName = proceduresRs.getString(nameIndex);
/*  820 */         byte[][] rowData = new byte[9][];
/*  821 */         rowData[0] = (catalog == null ? null : s2b(catalog));
/*  822 */         rowData[1] = null;
/*  823 */         rowData[2] = s2b(procedureName);
/*  824 */         rowData[3] = null;
/*  825 */         rowData[4] = null;
/*  826 */         rowData[5] = null;
/*  827 */         rowData[6] = null;
/*      */ 
/*  829 */         boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
/*      */ 
/*  832 */         rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(0));
/*      */ 
/*  836 */         rowData[8] = s2b(procedureName);
/*      */ 
/*  838 */         procedureRowsOrderedByName.put(procedureName, new ByteArrayRow(rowData, getExceptionInterceptor()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSetRow convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, byte[] procCatAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal)
/*      */     throws SQLException
/*      */   {
/*  849 */     byte[][] row = forGetFunctionColumns ? new byte[17][] : new byte[14][];
/*  850 */     row[0] = procCatAsBytes;
/*  851 */     row[1] = null;
/*  852 */     row[2] = procNameAsBytes;
/*  853 */     row[3] = s2b(paramName);
/*      */ 
/*  860 */     if ((isInParam) && (isOutParam))
/*  861 */       row[4] = s2b(String.valueOf(2));
/*  862 */     else if (isInParam)
/*  863 */       row[4] = s2b(String.valueOf(1));
/*  864 */     else if (isOutParam)
/*  865 */       row[4] = s2b(String.valueOf(4));
/*  866 */     else if (isReturnParam)
/*  867 */       row[4] = s2b(String.valueOf(5));
/*      */     else {
/*  869 */       row[4] = s2b(String.valueOf(0));
/*      */     }
/*  871 */     row[5] = s2b(Short.toString(typeDesc.dataType));
/*  872 */     row[6] = s2b(typeDesc.typeName);
/*  873 */     row[7] = (typeDesc.columnSize == null ? null : s2b(typeDesc.columnSize.toString()));
/*  874 */     row[8] = row[7];
/*  875 */     row[9] = (typeDesc.decimalDigits == null ? null : s2b(typeDesc.decimalDigits.toString()));
/*  876 */     row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */ 
/*  878 */     switch (typeDesc.nullability) {
/*      */     case 0:
/*  880 */       row[11] = s2b(String.valueOf(0));
/*      */ 
/*  882 */       break;
/*      */     case 1:
/*  885 */       row[11] = s2b(String.valueOf(1));
/*      */ 
/*  887 */       break;
/*      */     case 2:
/*  890 */       row[11] = s2b(String.valueOf(2));
/*      */ 
/*  892 */       break;
/*      */     default:
/*  895 */       throw SQLError.createSQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/*  900 */     row[12] = null;
/*      */ 
/*  902 */     if (forGetFunctionColumns)
/*      */     {
/*  904 */       row[13] = null;
/*      */ 
/*  907 */       row[14] = s2b(String.valueOf(ordinal));
/*      */ 
/*  910 */       row[15] = Constants.EMPTY_BYTE_ARRAY;
/*      */ 
/*  912 */       row[16] = s2b(paramName);
/*      */     }
/*      */ 
/*  915 */     return new ByteArrayRow(row, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/*  921 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionCausesTransactionCommit()
/*      */     throws SQLException
/*      */   {
/*  933 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionIgnoredInTransactions()
/*      */     throws SQLException
/*      */   {
/*  944 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean deletesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/*  959 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean doesMaxRowSizeIncludeBlobs()
/*      */     throws SQLException
/*      */   {
/*  972 */     return true;
/*      */   }
/*      */ 
/*      */   public List<ResultSetRow> extractForeignKeyForTable(ArrayList<ResultSetRow> rows, ResultSet rs, String catalog)
/*      */     throws SQLException
/*      */   {
/*  990 */     byte[][] row = new byte[3][];
/*  991 */     row[0] = rs.getBytes(1);
/*  992 */     row[1] = s2b("SUPPORTS_FK");
/*      */ 
/*  994 */     String createTableString = rs.getString(2);
/*  995 */     StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/*      */ 
/*  997 */     StringBuffer commentBuf = new StringBuffer("comment; ");
/*  998 */     boolean firstTime = true;
/*      */ 
/* 1000 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/* 1002 */     if (quoteChar == null) {
/* 1003 */       quoteChar = "`";
/*      */     }
/*      */ 
/* 1006 */     while (lineTokenizer.hasMoreTokens()) {
/* 1007 */       String line = lineTokenizer.nextToken().trim();
/*      */ 
/* 1009 */       String constraintName = null;
/*      */ 
/* 1011 */       if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
/* 1012 */         boolean usingBackTicks = true;
/* 1013 */         int beginPos = line.indexOf(quoteChar);
/*      */ 
/* 1015 */         if (beginPos == -1) {
/* 1016 */           beginPos = line.indexOf("\"");
/* 1017 */           usingBackTicks = false;
/*      */         }
/*      */ 
/* 1020 */         if (beginPos != -1) {
/* 1021 */           int endPos = -1;
/*      */ 
/* 1023 */           if (usingBackTicks)
/* 1024 */             endPos = line.indexOf(quoteChar, beginPos + 1);
/*      */           else {
/* 1026 */             endPos = line.indexOf("\"", beginPos + 1);
/*      */           }
/*      */ 
/* 1029 */           if (endPos != -1) {
/* 1030 */             constraintName = line.substring(beginPos + 1, endPos);
/* 1031 */             line = line.substring(endPos + 1, line.length()).trim();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1037 */       if (line.startsWith("FOREIGN KEY")) {
/* 1038 */         if (line.endsWith(",")) {
/* 1039 */           line = line.substring(0, line.length() - 1);
/*      */         }
/*      */ 
/* 1042 */         char quote = this.quotedId.charAt(0);
/*      */ 
/* 1044 */         int indexOfFK = line.indexOf("FOREIGN KEY");
/*      */ 
/* 1046 */         String localColumnName = null;
/* 1047 */         String referencedCatalogName = this.quotedId + catalog + this.quotedId;
/* 1048 */         String referencedTableName = null;
/* 1049 */         String referencedColumnName = null;
/*      */ 
/* 1052 */         if (indexOfFK != -1) {
/* 1053 */           int afterFk = indexOfFK + "FOREIGN KEY".length();
/*      */ 
/* 1055 */           int indexOfRef = StringUtils.indexOfIgnoreCaseRespectQuotes(afterFk, line, "REFERENCES", quote, true);
/*      */ 
/* 1057 */           if (indexOfRef != -1)
/*      */           {
/* 1059 */             int indexOfParenOpen = line.indexOf('(', afterFk);
/* 1060 */             int indexOfParenClose = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfParenOpen, line, ")", quote, true);
/*      */ 
/* 1062 */             if ((indexOfParenOpen != -1) && (indexOfParenClose == -1));
/* 1066 */             localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
/*      */ 
/* 1068 */             int afterRef = indexOfRef + "REFERENCES".length();
/*      */ 
/* 1070 */             int referencedColumnBegin = StringUtils.indexOfIgnoreCaseRespectQuotes(afterRef, line, "(", quote, true);
/*      */ 
/* 1072 */             if (referencedColumnBegin != -1) {
/* 1073 */               referencedTableName = line.substring(afterRef, referencedColumnBegin);
/*      */ 
/* 1075 */               int referencedColumnEnd = StringUtils.indexOfIgnoreCaseRespectQuotes(referencedColumnBegin + 1, line, ")", quote, true);
/*      */ 
/* 1077 */               if (referencedColumnEnd != -1) {
/* 1078 */                 referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd);
/*      */               }
/*      */ 
/* 1081 */               int indexOfCatalogSep = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referencedTableName, ".", quote, true);
/*      */ 
/* 1083 */               if (indexOfCatalogSep != -1) {
/* 1084 */                 referencedCatalogName = referencedTableName.substring(0, indexOfCatalogSep);
/* 1085 */                 referencedTableName = referencedTableName.substring(indexOfCatalogSep + 1);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1092 */         if (!firstTime)
/* 1093 */           commentBuf.append("; ");
/*      */         else {
/* 1095 */           firstTime = false;
/*      */         }
/*      */ 
/* 1098 */         if (constraintName != null)
/* 1099 */           commentBuf.append(constraintName);
/*      */         else {
/* 1101 */           commentBuf.append("not_available");
/*      */         }
/*      */ 
/* 1104 */         commentBuf.append("(");
/* 1105 */         commentBuf.append(localColumnName);
/* 1106 */         commentBuf.append(") REFER ");
/* 1107 */         commentBuf.append(referencedCatalogName);
/* 1108 */         commentBuf.append("/");
/* 1109 */         commentBuf.append(referencedTableName);
/* 1110 */         commentBuf.append("(");
/* 1111 */         commentBuf.append(referencedColumnName);
/* 1112 */         commentBuf.append(")");
/*      */ 
/* 1114 */         int lastParenIndex = line.lastIndexOf(")");
/*      */ 
/* 1116 */         if (lastParenIndex != line.length() - 1) {
/* 1117 */           String cascadeOptions = line.substring(lastParenIndex + 1);
/*      */ 
/* 1119 */           commentBuf.append(" ");
/* 1120 */           commentBuf.append(cascadeOptions);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1125 */     row[2] = s2b(commentBuf.toString());
/* 1126 */     rows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */ 
/* 1128 */     return rows;
/*      */   }
/*      */ 
/*      */   public ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName)
/*      */     throws SQLException
/*      */   {
/* 1149 */     ArrayList tableList = new ArrayList();
/* 1150 */     ResultSet rs = null;
/* 1151 */     java.sql.Statement stmt = null;
/*      */ 
/* 1153 */     if (tableName != null)
/* 1154 */       tableList.add(tableName);
/*      */     else {
/*      */       try {
/* 1157 */         rs = getTables(catalog, "", "%", new String[] { "TABLE" });
/*      */ 
/* 1159 */         while (rs.next())
/* 1160 */           tableList.add(rs.getString("TABLE_NAME"));
/*      */       }
/*      */       finally {
/* 1163 */         if (rs != null) {
/* 1164 */           rs.close();
/*      */         }
/*      */ 
/* 1167 */         rs = null;
/*      */       }
/*      */     }
/*      */ 
/* 1171 */     ArrayList rows = new ArrayList();
/* 1172 */     Field[] fields = new Field[3];
/* 1173 */     fields[0] = new Field("", "Name", 1, 2147483647);
/* 1174 */     fields[1] = new Field("", "Type", 1, 255);
/* 1175 */     fields[2] = new Field("", "Comment", 1, 2147483647);
/*      */ 
/* 1177 */     int numTables = tableList.size();
/* 1178 */     stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 1180 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/* 1182 */     if (quoteChar == null) {
/* 1183 */       quoteChar = "`";
/*      */     }
/*      */     try
/*      */     {
/* 1187 */       for (int i = 0; i < numTables; i++) {
/* 1188 */         String tableToExtract = (String)tableList.get(i);
/* 1189 */         if (tableToExtract.indexOf(quoteChar) > 0) {
/* 1190 */           tableToExtract = StringUtils.escapeQuote(tableToExtract, quoteChar);
/*      */         }
/*      */ 
/* 1193 */         String query = "SHOW CREATE TABLE " + quoteChar + catalog + quoteChar + "." + quoteChar + tableToExtract + quoteChar;
/*      */         try
/*      */         {
/* 1199 */           rs = stmt.executeQuery(query);
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 1202 */           String sqlState = sqlEx.getSQLState();
/*      */ 
/* 1204 */           if ((!"42S02".equals(sqlState)) && (sqlEx.getErrorCode() != 1146))
/*      */           {
/* 1206 */             throw sqlEx;
/*      */           }
/*      */ 
/* 1209 */           continue;
/*      */         }
/*      */ 
/* 1212 */         while (rs.next())
/* 1213 */           extractForeignKeyForTable(rows, rs, catalog);
/*      */       }
/*      */     }
/*      */     finally {
/* 1217 */       if (rs != null) {
/* 1218 */         rs.close();
/*      */       }
/*      */ 
/* 1221 */       rs = null;
/*      */ 
/* 1223 */       if (stmt != null) {
/* 1224 */         stmt.close();
/*      */       }
/*      */ 
/* 1227 */       stmt = null;
/*      */     }
/*      */ 
/* 1230 */     return buildResultSet(fields, rows);
/*      */   }
/*      */ 
/*      */   public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3)
/*      */     throws SQLException
/*      */   {
/* 1238 */     Field[] fields = new Field[21];
/* 1239 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 1240 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 1241 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 1242 */     fields[3] = new Field("", "ATTR_NAME", 1, 32);
/* 1243 */     fields[4] = new Field("", "DATA_TYPE", 5, 32);
/* 1244 */     fields[5] = new Field("", "ATTR_TYPE_NAME", 1, 32);
/* 1245 */     fields[6] = new Field("", "ATTR_SIZE", 4, 32);
/* 1246 */     fields[7] = new Field("", "DECIMAL_DIGITS", 4, 32);
/* 1247 */     fields[8] = new Field("", "NUM_PREC_RADIX", 4, 32);
/* 1248 */     fields[9] = new Field("", "NULLABLE ", 4, 32);
/* 1249 */     fields[10] = new Field("", "REMARKS", 1, 32);
/* 1250 */     fields[11] = new Field("", "ATTR_DEF", 1, 32);
/* 1251 */     fields[12] = new Field("", "SQL_DATA_TYPE", 4, 32);
/* 1252 */     fields[13] = new Field("", "SQL_DATETIME_SUB", 4, 32);
/* 1253 */     fields[14] = new Field("", "CHAR_OCTET_LENGTH", 4, 32);
/* 1254 */     fields[15] = new Field("", "ORDINAL_POSITION", 4, 32);
/* 1255 */     fields[16] = new Field("", "IS_NULLABLE", 1, 32);
/* 1256 */     fields[17] = new Field("", "SCOPE_CATALOG", 1, 32);
/* 1257 */     fields[18] = new Field("", "SCOPE_SCHEMA", 1, 32);
/* 1258 */     fields[19] = new Field("", "SCOPE_TABLE", 1, 32);
/* 1259 */     fields[20] = new Field("", "SOURCE_DATA_TYPE", 5, 32);
/*      */ 
/* 1261 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
/*      */     throws SQLException
/*      */   {
/* 1312 */     if (table == null) {
/* 1313 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1317 */     Field[] fields = new Field[8];
/* 1318 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 1319 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 1320 */     fields[2] = new Field("", "DATA_TYPE", 4, 32);
/* 1321 */     fields[3] = new Field("", "TYPE_NAME", 1, 32);
/* 1322 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 10);
/* 1323 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 1324 */     fields[6] = new Field("", "DECIMAL_DIGITS", 5, 10);
/* 1325 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */ 
/* 1327 */     ArrayList rows = new ArrayList();
/* 1328 */     java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 1332 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 1334 */           ResultSet results = null;
/*      */           try
/*      */           {
/* 1337 */             StringBuffer queryBuf = new StringBuffer("SHOW COLUMNS FROM ");
/*      */ 
/* 1339 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1340 */             queryBuf.append(this.val$table);
/* 1341 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1342 */             queryBuf.append(" FROM ");
/* 1343 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1344 */             queryBuf.append(catalogStr);
/* 1345 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 1347 */             results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 1349 */             while (results.next()) {
/* 1350 */               String keyType = results.getString("Key");
/*      */ 
/* 1352 */               if ((keyType != null) && 
/* 1353 */                 (StringUtils.startsWithIgnoreCase(keyType, "PRI")))
/*      */               {
/* 1355 */                 byte[][] rowVal = new byte[8][];
/* 1356 */                 rowVal[0] = Integer.toString(2).getBytes();
/*      */ 
/* 1360 */                 rowVal[1] = results.getBytes("Field");
/*      */ 
/* 1362 */                 String type = results.getString("Type");
/* 1363 */                 int size = MysqlIO.getMaxBuf();
/* 1364 */                 int decimals = 0;
/*      */ 
/* 1369 */                 if (type.indexOf("enum") != -1) {
/* 1370 */                   String temp = type.substring(type.indexOf("("), type.indexOf(")"));
/*      */ 
/* 1373 */                   StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/* 1375 */                   int maxLength = 0;
/*      */ 
/* 1377 */                   while (tokenizer.hasMoreTokens()) {
/* 1378 */                     maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */                   }
/*      */ 
/* 1383 */                   size = maxLength;
/* 1384 */                   decimals = 0;
/* 1385 */                   type = "enum";
/* 1386 */                 } else if (type.indexOf("(") != -1) {
/* 1387 */                   if (type.indexOf(",") != -1) {
/* 1388 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
/*      */ 
/* 1392 */                     decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
/*      */                   }
/*      */                   else
/*      */                   {
/* 1397 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
/*      */                   }
/*      */ 
/* 1403 */                   type = type.substring(0, type.indexOf("("));
/*      */                 }
/*      */ 
/* 1407 */                 rowVal[2] = DatabaseMetaData.this.s2b(String.valueOf(MysqlDefs.mysqlToJavaType(type)));
/*      */ 
/* 1409 */                 rowVal[3] = DatabaseMetaData.this.s2b(type);
/* 1410 */                 rowVal[4] = Integer.toString(size + decimals).getBytes();
/*      */ 
/* 1412 */                 rowVal[5] = Integer.toString(size + decimals).getBytes();
/*      */ 
/* 1414 */                 rowVal[6] = Integer.toString(decimals).getBytes();
/*      */ 
/* 1416 */                 rowVal[7] = Integer.toString(1).getBytes();
/*      */ 
/* 1421 */                 this.val$rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 1426 */             if (!"42S02".equals(sqlEx.getSQLState()))
/* 1427 */               throw sqlEx;
/*      */           }
/*      */           finally {
/* 1430 */             if (results != null) {
/*      */               try {
/* 1432 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 1437 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1332 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 1443 */       if (stmt != null) {
/* 1444 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 1448 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 1450 */     return results;
/*      */   }
/*      */ 
/*      */   protected void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List<ResultSetRow> resultRows)
/*      */     throws SQLException
/*      */   {
/* 1488 */     getCallStmtParameterTypes(catalog, procName, parameterNamePattern, resultRows, false);
/*      */   }
/*      */ 
/*      */   private void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List<ResultSetRow> resultRows, boolean forGetFunctionColumns)
/*      */     throws SQLException
/*      */   {
/* 1495 */     java.sql.Statement paramRetrievalStmt = null;
/* 1496 */     ResultSet paramRetrievalRs = null;
/*      */ 
/* 1498 */     if (parameterNamePattern == null) {
/* 1499 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1500 */         parameterNamePattern = "%";
/*      */       else {
/* 1502 */         throw SQLError.createSQLException("Parameter/Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1508 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/* 1510 */     String parameterDef = null;
/*      */ 
/* 1512 */     byte[] procNameAsBytes = null;
/* 1513 */     byte[] procCatAsBytes = null;
/*      */ 
/* 1515 */     boolean isProcedureInAnsiMode = false;
/* 1516 */     String storageDefnDelims = null;
/* 1517 */     String storageDefnClosures = null;
/*      */     try
/*      */     {
/* 1520 */       paramRetrievalStmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 1522 */       String oldCatalog = this.conn.getCatalog();
/* 1523 */       if ((this.conn.lowerCaseTableNames()) && (catalog != null) && (catalog.length() != 0) && (oldCatalog != null) && (oldCatalog.length() != 0))
/*      */       {
/* 1530 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 1533 */           this.conn.setCatalog(catalog.replaceAll(quoteChar, ""));
/* 1534 */           rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
/* 1535 */           rs.next();
/*      */ 
/* 1537 */           catalog = rs.getString(1);
/*      */         }
/*      */         finally
/*      */         {
/* 1541 */           this.conn.setCatalog(oldCatalog);
/*      */ 
/* 1543 */           if (rs != null) {
/* 1544 */             rs.close();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1549 */       if (paramRetrievalStmt.getMaxRows() != 0) {
/* 1550 */         paramRetrievalStmt.setMaxRows(0);
/*      */       }
/*      */ 
/* 1553 */       int dotIndex = -1;
/*      */ 
/* 1555 */       if (!" ".equals(quoteChar)) {
/* 1556 */         dotIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procName, ".", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */       }
/*      */       else
/*      */       {
/* 1560 */         dotIndex = procName.indexOf(".");
/*      */       }
/*      */ 
/* 1563 */       String dbName = null;
/*      */ 
/* 1565 */       if ((dotIndex != -1) && (dotIndex + 1 < procName.length())) {
/* 1566 */         dbName = procName.substring(0, dotIndex);
/* 1567 */         procName = procName.substring(dotIndex + 1);
/*      */       } else {
/* 1569 */         dbName = catalog;
/*      */       }
/*      */ 
/* 1575 */       String tmpProcName = procName;
/* 1576 */       tmpProcName = tmpProcName.replaceAll(quoteChar, "");
/*      */       try {
/* 1578 */         procNameAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
/*      */       } catch (UnsupportedEncodingException ueEx) {
/* 1580 */         procNameAsBytes = s2b(tmpProcName);
/*      */       }
/*      */ 
/* 1585 */       tmpProcName = dbName;
/* 1586 */       tmpProcName = tmpProcName.replaceAll(quoteChar, "");
/*      */       try {
/* 1588 */         procCatAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
/*      */       } catch (UnsupportedEncodingException ueEx) {
/* 1590 */         procCatAsBytes = s2b(tmpProcName);
/*      */       }
/*      */ 
/* 1595 */       StringBuffer procNameBuf = new StringBuffer();
/*      */ 
/* 1597 */       if (dbName != null) {
/* 1598 */         if ((!" ".equals(quoteChar)) && (!dbName.startsWith(quoteChar))) {
/* 1599 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */ 
/* 1602 */         procNameBuf.append(dbName);
/*      */ 
/* 1604 */         if ((!" ".equals(quoteChar)) && (!dbName.startsWith(quoteChar))) {
/* 1605 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */ 
/* 1608 */         procNameBuf.append(".");
/*      */       }
/*      */ 
/* 1611 */       boolean procNameIsNotQuoted = !procName.startsWith(quoteChar);
/*      */ 
/* 1613 */       if ((!" ".equals(quoteChar)) && (procNameIsNotQuoted)) {
/* 1614 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */ 
/* 1617 */       procNameBuf.append(procName);
/*      */ 
/* 1619 */       if ((!" ".equals(quoteChar)) && (procNameIsNotQuoted)) {
/* 1620 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */ 
/* 1623 */       boolean parsingFunction = false;
/*      */       try
/*      */       {
/* 1626 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
/*      */ 
/* 1629 */         parsingFunction = false;
/*      */       } catch (SQLException sqlEx) {
/* 1631 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
/*      */ 
/* 1634 */         parsingFunction = true;
/*      */       }
/*      */ 
/* 1637 */       if (paramRetrievalRs.next()) {
/* 1638 */         String procedureDef = parsingFunction ? paramRetrievalRs.getString("Create Function") : paramRetrievalRs.getString("Create Procedure");
/*      */ 
/* 1642 */         if ((!this.conn.getNoAccessToProcedureBodies()) && ((procedureDef == null) || (procedureDef.length() == 0)))
/*      */         {
/* 1644 */           throw SQLError.createSQLException("User does not have access to metadata required to determine stored procedure parameter types. If rights can not be granted, configure connection with \"noAccessToProcedureBodies=true\" to have driver generate parameters that represent INOUT strings irregardless of actual parameter types.", "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1651 */           String sqlMode = paramRetrievalRs.getString("sql_mode");
/*      */ 
/* 1653 */           if (StringUtils.indexOfIgnoreCase(sqlMode, "ANSI") != -1) {
/* 1654 */             isProcedureInAnsiMode = true;
/*      */           }
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*      */         }
/* 1660 */         String identifierMarkers = isProcedureInAnsiMode ? "`\"" : "`";
/* 1661 */         String identifierAndStringMarkers = "'" + identifierMarkers;
/* 1662 */         storageDefnDelims = "(" + identifierMarkers;
/* 1663 */         storageDefnClosures = ")" + identifierMarkers;
/*      */ 
/* 1665 */         if ((procedureDef != null) && (procedureDef.length() != 0))
/*      */         {
/* 1667 */           procedureDef = StringUtils.stripComments(procedureDef, identifierAndStringMarkers, identifierAndStringMarkers, true, false, true, true);
/*      */ 
/* 1670 */           int openParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1674 */           int endOfParamDeclarationIndex = 0;
/*      */ 
/* 1676 */           endOfParamDeclarationIndex = endPositionOfParameterDeclaration(openParenIndex, procedureDef, quoteChar);
/*      */ 
/* 1679 */           if (parsingFunction)
/*      */           {
/* 1683 */             int returnsIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, " RETURNS ", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1688 */             int endReturnsDef = findEndOfReturnsClause(procedureDef, quoteChar, returnsIndex);
/*      */ 
/* 1693 */             int declarationStart = returnsIndex + "RETURNS ".length();
/*      */ 
/* 1695 */             while ((declarationStart < procedureDef.length()) && 
/* 1696 */               (Character.isWhitespace(procedureDef.charAt(declarationStart)))) {
/* 1697 */               declarationStart++;
/*      */             }
/*      */ 
/* 1703 */             String returnsDefn = procedureDef.substring(declarationStart, endReturnsDef).trim();
/* 1704 */             TypeDescriptor returnDescriptor = new TypeDescriptor(returnsDefn, "YES");
/*      */ 
/* 1707 */             resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, "", false, false, true, returnDescriptor, forGetFunctionColumns, 0));
/*      */           }
/*      */ 
/* 1712 */           if ((openParenIndex == -1) || (endOfParamDeclarationIndex == -1))
/*      */           {
/* 1715 */             throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 1721 */           parameterDef = procedureDef.substring(openParenIndex + 1, endOfParamDeclarationIndex);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1727 */       SQLException sqlExRethrow = null;
/*      */ 
/* 1729 */       if (paramRetrievalRs != null) {
/*      */         try {
/* 1731 */           paramRetrievalRs.close();
/*      */         } catch (SQLException sqlEx) {
/* 1733 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1736 */         paramRetrievalRs = null;
/*      */       }
/*      */ 
/* 1739 */       if (paramRetrievalStmt != null) {
/*      */         try {
/* 1741 */           paramRetrievalStmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 1743 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1746 */         paramRetrievalStmt = null;
/*      */       }
/*      */ 
/* 1749 */       if (sqlExRethrow != null) {
/* 1750 */         throw sqlExRethrow;
/*      */       }
/*      */     }
/*      */ 
/* 1754 */     if (parameterDef != null) {
/* 1755 */       int ordinal = 1;
/*      */ 
/* 1757 */       List parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
/*      */ 
/* 1760 */       int parseListLen = parseList.size();
/*      */ 
/* 1762 */       for (int i = 0; i < parseListLen; i++) {
/* 1763 */         String declaration = (String)parseList.get(i);
/*      */ 
/* 1765 */         if (declaration.trim().length() == 0)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/* 1771 */         declaration = declaration.replaceAll("[\\t\\n\\x0B\\f\\r]", " ");
/* 1772 */         StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
/*      */ 
/* 1775 */         String paramName = null;
/* 1776 */         boolean isOutParam = false;
/* 1777 */         boolean isInParam = false;
/*      */ 
/* 1779 */         if (declarationTok.hasMoreTokens()) {
/* 1780 */           String possibleParamName = declarationTok.nextToken();
/*      */ 
/* 1782 */           if (possibleParamName.equalsIgnoreCase("OUT")) {
/* 1783 */             isOutParam = true;
/*      */ 
/* 1785 */             if (declarationTok.hasMoreTokens())
/* 1786 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1788 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/* 1792 */           else if (possibleParamName.equalsIgnoreCase("INOUT")) {
/* 1793 */             isOutParam = true;
/* 1794 */             isInParam = true;
/*      */ 
/* 1796 */             if (declarationTok.hasMoreTokens())
/* 1797 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1799 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */ 
/*      */           }
/* 1803 */           else if (possibleParamName.equalsIgnoreCase("IN")) {
/* 1804 */             isOutParam = false;
/* 1805 */             isInParam = true;
/*      */ 
/* 1807 */             if (declarationTok.hasMoreTokens())
/* 1808 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1810 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1815 */             isOutParam = false;
/* 1816 */             isInParam = true;
/*      */ 
/* 1818 */             paramName = possibleParamName;
/*      */           }
/*      */ 
/* 1821 */           TypeDescriptor typeDesc = null;
/*      */ 
/* 1823 */           if (declarationTok.hasMoreTokens()) {
/* 1824 */             StringBuffer typeInfoBuf = new StringBuffer(declarationTok.nextToken());
/*      */ 
/* 1827 */             while (declarationTok.hasMoreTokens()) {
/* 1828 */               typeInfoBuf.append(" ");
/* 1829 */               typeInfoBuf.append(declarationTok.nextToken());
/*      */             }
/*      */ 
/* 1832 */             String typeInfo = typeInfoBuf.toString();
/*      */ 
/* 1834 */             typeDesc = new TypeDescriptor(typeInfo, "YES");
/*      */           } else {
/* 1836 */             throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter type)", "S1000", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 1841 */           if (((paramName.startsWith("`")) && (paramName.endsWith("`"))) || ((isProcedureInAnsiMode) && (paramName.startsWith("\"")) && (paramName.endsWith("\""))))
/*      */           {
/* 1843 */             paramName = paramName.substring(1, paramName.length() - 1);
/*      */           }
/*      */ 
/* 1846 */           int wildCompareRes = StringUtils.wildCompare(paramName, parameterNamePattern);
/*      */ 
/* 1849 */           if (wildCompareRes != -1) {
/* 1850 */             ResultSetRow row = convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, paramName, isOutParam, isInParam, false, typeDesc, forGetFunctionColumns, ordinal++);
/*      */ 
/* 1855 */             resultRows.add(row);
/*      */           }
/*      */         } else {
/* 1858 */           throw SQLError.createSQLException("Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')", "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar)
/*      */     throws SQLException
/*      */   {
/* 1887 */     int currentPos = beginIndex + 1;
/* 1888 */     int parenDepth = 1;
/*      */ 
/* 1890 */     while ((parenDepth > 0) && (currentPos < procedureDef.length())) {
/* 1891 */       int closedParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, procedureDef, ")", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1895 */       if (closedParenIndex != -1) {
/* 1896 */         int nextOpenParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1901 */         if ((nextOpenParenIndex != -1) && (nextOpenParenIndex < closedParenIndex))
/*      */         {
/* 1903 */           parenDepth++;
/* 1904 */           currentPos = closedParenIndex + 1;
/*      */         }
/*      */         else
/*      */         {
/* 1908 */           parenDepth--;
/* 1909 */           currentPos = closedParenIndex;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1914 */         throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1921 */     return currentPos;
/*      */   }
/*      */ 
/*      */   private int findEndOfReturnsClause(String procedureDefn, String quoteChar, int positionOfReturnKeyword)
/*      */     throws SQLException
/*      */   {
/* 1946 */     String[] tokens = { "LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN" };
/*      */ 
/* 1950 */     int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
/*      */ 
/* 1952 */     int endOfReturn = -1;
/*      */ 
/* 1954 */     for (int i = 0; i < tokens.length; i++) {
/* 1955 */       int nextEndOfReturn = StringUtils.indexOfIgnoreCaseRespectQuotes(startLookingAt, procedureDefn, tokens[i], quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1959 */       if ((nextEndOfReturn == -1) || (
/* 1960 */         (endOfReturn != -1) && (nextEndOfReturn >= endOfReturn))) continue;
/* 1961 */       endOfReturn = nextEndOfReturn;
/*      */     }
/*      */ 
/* 1966 */     if (endOfReturn != -1) {
/* 1967 */       return endOfReturn;
/*      */     }
/*      */ 
/* 1971 */     endOfReturn = StringUtils.indexOfIgnoreCaseRespectQuotes(startLookingAt, procedureDefn, ":", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1975 */     if (endOfReturn != -1)
/*      */     {
/* 1977 */       for (int i = endOfReturn; i > 0; i--) {
/* 1978 */         if (Character.isWhitespace(procedureDefn.charAt(i))) {
/* 1979 */           return i;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1986 */     throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private int getCascadeDeleteOption(String cascadeOptions)
/*      */   {
/* 2000 */     int onDeletePos = cascadeOptions.indexOf("ON DELETE");
/*      */ 
/* 2002 */     if (onDeletePos != -1) {
/* 2003 */       String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
/*      */ 
/* 2006 */       if (deleteOptions.startsWith("ON DELETE CASCADE"))
/* 2007 */         return 0;
/* 2008 */       if (deleteOptions.startsWith("ON DELETE SET NULL"))
/* 2009 */         return 2;
/* 2010 */       if (deleteOptions.startsWith("ON DELETE RESTRICT"))
/* 2011 */         return 1;
/* 2012 */       if (deleteOptions.startsWith("ON DELETE NO ACTION")) {
/* 2013 */         return 3;
/*      */       }
/*      */     }
/*      */ 
/* 2017 */     return 3;
/*      */   }
/*      */ 
/*      */   private int getCascadeUpdateOption(String cascadeOptions)
/*      */   {
/* 2029 */     int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
/*      */ 
/* 2031 */     if (onUpdatePos != -1) {
/* 2032 */       String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
/*      */ 
/* 2035 */       if (updateOptions.startsWith("ON UPDATE CASCADE"))
/* 2036 */         return 0;
/* 2037 */       if (updateOptions.startsWith("ON UPDATE SET NULL"))
/* 2038 */         return 2;
/* 2039 */       if (updateOptions.startsWith("ON UPDATE RESTRICT"))
/* 2040 */         return 1;
/* 2041 */       if (updateOptions.startsWith("ON UPDATE NO ACTION")) {
/* 2042 */         return 3;
/*      */       }
/*      */     }
/*      */ 
/* 2046 */     return 3;
/*      */   }
/*      */ 
/*      */   protected IteratorWithCleanup<String> getCatalogIterator(String catalogSpec)
/*      */     throws SQLException
/*      */   {
/*      */     IteratorWithCleanup allCatalogsIter;
/*      */     IteratorWithCleanup allCatalogsIter;
/* 2052 */     if (catalogSpec != null)
/*      */     {
/*      */       IteratorWithCleanup allCatalogsIter;
/* 2053 */       if (!catalogSpec.equals("")) {
/* 2054 */         allCatalogsIter = new SingleStringIterator(unQuoteQuotedIdentifier(catalogSpec));
/*      */       }
/*      */       else
/* 2057 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */     }
/*      */     else
/*      */     {
/*      */       IteratorWithCleanup allCatalogsIter;
/* 2059 */       if (this.conn.getNullCatalogMeansCurrent())
/*      */       {
/* 2061 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */       }
/* 2063 */       else allCatalogsIter = new ResultSetIterator(getCatalogs(), 1);
/*      */     }
/*      */ 
/* 2066 */     return allCatalogsIter;
/*      */   }
/*      */ 
/*      */   protected String unQuoteQuotedIdentifier(String identifier) {
/* 2070 */     boolean trimQuotes = false;
/*      */ 
/* 2072 */     if (identifier != null)
/*      */     {
/* 2075 */       if ((identifier.startsWith("`")) && (identifier.endsWith("`"))) {
/* 2076 */         trimQuotes = true;
/*      */       }
/*      */ 
/* 2079 */       if ((this.conn.useAnsiQuotedIdentifiers()) && 
/* 2080 */         (identifier.startsWith("\"")) && (identifier.endsWith("\""))) {
/* 2081 */         trimQuotes = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2086 */     if (trimQuotes) {
/* 2087 */       return identifier.substring(1, identifier.length() - 1);
/*      */     }
/*      */ 
/* 2090 */     return identifier;
/*      */   }
/*      */ 
/*      */   public ResultSet getCatalogs()
/*      */     throws SQLException
/*      */   {
/* 2109 */     ResultSet results = null;
/* 2110 */     java.sql.Statement stmt = null;
/*      */     try
/*      */     {
/* 2113 */       stmt = this.conn.createStatement();
/* 2114 */       stmt.setEscapeProcessing(false);
/* 2115 */       results = stmt.executeQuery("SHOW DATABASES");
/*      */ 
/* 2117 */       ResultSetMetaData resultsMD = results.getMetaData();
/* 2118 */       Field[] fields = new Field[1];
/* 2119 */       fields[0] = new Field("", "TABLE_CAT", 12, resultsMD.getColumnDisplaySize(1));
/*      */ 
/* 2122 */       ArrayList tuples = new ArrayList();
/*      */ 
/* 2124 */       while (results.next()) {
/* 2125 */         rowVal = new byte[1][];
/* 2126 */         rowVal[0] = results.getBytes(1);
/* 2127 */         tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */       }
/*      */ 
/* 2130 */       rowVal = buildResultSet(fields, tuples);
/*      */     }
/*      */     finally
/*      */     {
/*      */       byte[][] rowVal;
/* 2132 */       if (results != null) {
/*      */         try {
/* 2134 */           results.close();
/*      */         } catch (SQLException sqlEx) {
/* 2136 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/* 2139 */         results = null;
/*      */       }
/*      */ 
/* 2142 */       if (stmt != null) {
/*      */         try {
/* 2144 */           stmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 2146 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/* 2149 */         stmt = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalogSeparator()
/*      */     throws SQLException
/*      */   {
/* 2162 */     return ".";
/*      */   }
/*      */ 
/*      */   public String getCatalogTerm()
/*      */     throws SQLException
/*      */   {
/* 2179 */     return "database";
/*      */   }
/*      */ 
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 2220 */     Field[] fields = new Field[8];
/* 2221 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 2222 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 2223 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 2224 */     fields[3] = new Field("", "COLUMN_NAME", 1, 64);
/* 2225 */     fields[4] = new Field("", "GRANTOR", 1, 77);
/* 2226 */     fields[5] = new Field("", "GRANTEE", 1, 77);
/* 2227 */     fields[6] = new Field("", "PRIVILEGE", 1, 64);
/* 2228 */     fields[7] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */ 
/* 2230 */     StringBuffer grantQuery = new StringBuffer("SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv from mysql.columns_priv c, mysql.tables_priv t where c.host = t.host and c.db = t.db and c.table_name = t.table_name ");
/*      */ 
/* 2237 */     if ((catalog != null) && (catalog.length() != 0)) {
/* 2238 */       grantQuery.append(" AND c.db='");
/* 2239 */       grantQuery.append(catalog);
/* 2240 */       grantQuery.append("' ");
/*      */     }
/*      */ 
/* 2244 */     grantQuery.append(" AND c.table_name ='");
/* 2245 */     grantQuery.append(table);
/* 2246 */     grantQuery.append("' AND c.column_name like '");
/* 2247 */     grantQuery.append(columnNamePattern);
/* 2248 */     grantQuery.append("'");
/*      */ 
/* 2250 */     java.sql.Statement stmt = null;
/* 2251 */     ResultSet results = null;
/* 2252 */     ArrayList grantRows = new ArrayList();
/*      */     try
/*      */     {
/* 2255 */       stmt = this.conn.createStatement();
/* 2256 */       stmt.setEscapeProcessing(false);
/* 2257 */       results = stmt.executeQuery(grantQuery.toString());
/*      */ 
/* 2259 */       while (results.next()) {
/* 2260 */         String host = results.getString(1);
/* 2261 */         String db = results.getString(2);
/* 2262 */         String grantor = results.getString(3);
/* 2263 */         String user = results.getString(4);
/*      */ 
/* 2265 */         if ((user == null) || (user.length() == 0)) {
/* 2266 */           user = "%";
/*      */         }
/*      */ 
/* 2269 */         StringBuffer fullUser = new StringBuffer(user);
/*      */ 
/* 2271 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 2272 */           fullUser.append("@");
/* 2273 */           fullUser.append(host);
/*      */         }
/*      */ 
/* 2276 */         String columnName = results.getString(6);
/* 2277 */         String allPrivileges = results.getString(7);
/*      */ 
/* 2279 */         if (allPrivileges != null) {
/* 2280 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 2282 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */ 
/* 2284 */           while (st.hasMoreTokens()) {
/* 2285 */             String privilege = st.nextToken().trim();
/* 2286 */             byte[][] tuple = new byte[8][];
/* 2287 */             tuple[0] = s2b(db);
/* 2288 */             tuple[1] = null;
/* 2289 */             tuple[2] = s2b(table);
/* 2290 */             tuple[3] = s2b(columnName);
/*      */ 
/* 2292 */             if (grantor != null)
/* 2293 */               tuple[4] = s2b(grantor);
/*      */             else {
/* 2295 */               tuple[4] = null;
/*      */             }
/*      */ 
/* 2298 */             tuple[5] = s2b(fullUser.toString());
/* 2299 */             tuple[6] = s2b(privilege);
/* 2300 */             tuple[7] = null;
/* 2301 */             grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2306 */       if (results != null) {
/*      */         try {
/* 2308 */           results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 2313 */         results = null;
/*      */       }
/*      */ 
/* 2316 */       if (stmt != null) {
/*      */         try {
/* 2318 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 2323 */         stmt = null;
/*      */       }
/*      */     }
/*      */ 
/* 2327 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 2391 */     if (columnNamePattern == null) {
/* 2392 */       if (this.conn.getNullNamePatternMatchesAll())
/* 2393 */         columnNamePattern = "%";
/*      */       else {
/* 2395 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2401 */     String colPattern = columnNamePattern;
/*      */ 
/* 2403 */     Field[] fields = createColumnsFields();
/*      */ 
/* 2405 */     ArrayList rows = new ArrayList();
/* 2406 */     java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 2410 */       new IterateBlock(getCatalogIterator(catalog), tableNamePattern, schemaPattern, colPattern, stmt, rows)
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 2413 */           ArrayList tableNameList = new ArrayList();
/*      */ 
/* 2415 */           if (this.val$tableNamePattern == null)
/*      */           {
/* 2417 */             ResultSet tables = null;
/*      */             try
/*      */             {
/* 2420 */               tables = DatabaseMetaData.this.getTables(catalogStr, this.val$schemaPattern, "%", new String[0]);
/*      */ 
/* 2423 */               while (tables.next()) {
/* 2424 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/*      */ 
/* 2426 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 2429 */               if (tables != null) {
/*      */                 try {
/* 2431 */                   tables.close();
/*      */                 } catch (Exception sqlEx) {
/* 2433 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2437 */                 tables = null;
/*      */               }
/*      */             }
/*      */           } else {
/* 2441 */             ResultSet tables = null;
/*      */             try
/*      */             {
/* 2444 */               tables = DatabaseMetaData.this.getTables(catalogStr, this.val$schemaPattern, this.val$tableNamePattern, new String[0]);
/*      */ 
/* 2447 */               while (tables.next()) {
/* 2448 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/*      */ 
/* 2450 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 2453 */               if (tables != null) {
/*      */                 try {
/* 2455 */                   tables.close();
/*      */                 } catch (SQLException sqlEx) {
/* 2457 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2461 */                 tables = null;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2466 */           for (String tableName : tableNameList)
/*      */           {
/* 2468 */             ResultSet results = null;
/*      */             try
/*      */             {
/* 2471 */               StringBuffer queryBuf = new StringBuffer("SHOW ");
/*      */ 
/* 2473 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2474 */                 queryBuf.append("FULL ");
/*      */               }
/*      */ 
/* 2477 */               queryBuf.append("COLUMNS FROM ");
/* 2478 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2479 */               queryBuf.append(tableName);
/* 2480 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2481 */               queryBuf.append(" FROM ");
/* 2482 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2483 */               queryBuf.append(catalogStr);
/* 2484 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2485 */               queryBuf.append(" LIKE '");
/* 2486 */               queryBuf.append(this.val$colPattern);
/* 2487 */               queryBuf.append("'");
/*      */ 
/* 2494 */               boolean fixUpOrdinalsRequired = false;
/* 2495 */               Object ordinalFixUpMap = null;
/*      */ 
/* 2497 */               if (!this.val$colPattern.equals("%")) {
/* 2498 */                 fixUpOrdinalsRequired = true;
/*      */ 
/* 2500 */                 StringBuffer fullColumnQueryBuf = new StringBuffer("SHOW ");
/*      */ 
/* 2503 */                 if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2504 */                   fullColumnQueryBuf.append("FULL ");
/*      */                 }
/*      */ 
/* 2507 */                 fullColumnQueryBuf.append("COLUMNS FROM ");
/* 2508 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2509 */                 fullColumnQueryBuf.append(tableName);
/* 2510 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2511 */                 fullColumnQueryBuf.append(" FROM ");
/* 2512 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2513 */                 fullColumnQueryBuf.append(catalogStr);
/*      */ 
/* 2515 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2517 */                 results = this.val$stmt.executeQuery(fullColumnQueryBuf.toString());
/*      */ 
/* 2520 */                 ordinalFixUpMap = new HashMap();
/*      */ 
/* 2522 */                 int fullOrdinalPos = 1;
/*      */ 
/* 2524 */                 while (results.next()) {
/* 2525 */                   String fullOrdColName = results.getString("Field");
/*      */ 
/* 2528 */                   ((Map)ordinalFixUpMap).put(fullOrdColName, Integer.valueOf(fullOrdinalPos++));
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2533 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 2535 */               int ordPos = 1;
/*      */ 
/* 2537 */               while (results.next()) {
/* 2538 */                 byte[][] rowVal = new byte[23][];
/* 2539 */                 rowVal[0] = DatabaseMetaData.this.s2b(catalogStr);
/* 2540 */                 rowVal[1] = null;
/*      */ 
/* 2543 */                 rowVal[2] = DatabaseMetaData.this.s2b(tableName);
/* 2544 */                 rowVal[3] = results.getBytes("Field");
/*      */ 
/* 2546 */                 DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
/*      */ 
/* 2550 */                 rowVal[4] = Short.toString(typeDesc.dataType).getBytes();
/*      */ 
/* 2554 */                 rowVal[5] = DatabaseMetaData.this.s2b(typeDesc.typeName);
/*      */ 
/* 2556 */                 if (typeDesc.columnSize == null) {
/* 2557 */                   rowVal[6] = null;
/*      */                 } else {
/* 2559 */                   String collation = results.getString("Collation");
/* 2560 */                   int mbminlen = 1;
/* 2561 */                   if ((collation != null) && (("TEXT".equals(typeDesc.typeName)) || ("TINYTEXT".equals(typeDesc.typeName)) || ("MEDIUMTEXT".equals(typeDesc.typeName))))
/*      */                   {
/* 2566 */                     if ((collation.indexOf("ucs2") > -1) || (collation.indexOf("utf16") > -1))
/*      */                     {
/* 2568 */                       mbminlen = 2;
/* 2569 */                     } else if (collation.indexOf("utf32") > -1) {
/* 2570 */                       mbminlen = 4;
/*      */                     }
/*      */                   }
/* 2573 */                   rowVal[6] = (mbminlen == 1 ? DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()) : DatabaseMetaData.this.s2b(Integer.valueOf(typeDesc.columnSize.intValue() / mbminlen).toString()));
/*      */                 }
/*      */ 
/* 2577 */                 rowVal[7] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/* 2578 */                 rowVal[8] = (typeDesc.decimalDigits == null ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString()));
/* 2579 */                 rowVal[9] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */ 
/* 2581 */                 rowVal[10] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.nullability));
/*      */                 try
/*      */                 {
/* 2592 */                   if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2593 */                     rowVal[11] = results.getBytes("Comment");
/*      */                   }
/*      */                   else
/* 2596 */                     rowVal[11] = results.getBytes("Extra");
/*      */                 }
/*      */                 catch (Exception E) {
/* 2599 */                   rowVal[11] = new byte[0];
/*      */                 }
/*      */ 
/* 2603 */                 rowVal[12] = results.getBytes("Default");
/*      */ 
/* 2605 */                 rowVal[13] = { 48 };
/* 2606 */                 rowVal[14] = { 48 };
/*      */ 
/* 2608 */                 if ((StringUtils.indexOfIgnoreCase(typeDesc.typeName, "CHAR") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "BLOB") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "TEXT") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "BINARY") != -1))
/*      */                 {
/* 2612 */                   rowVal[15] = rowVal[6];
/*      */                 }
/* 2614 */                 else rowVal[15] = null;
/*      */ 
/* 2618 */                 if (!fixUpOrdinalsRequired) {
/* 2619 */                   rowVal[16] = Integer.toString(ordPos++).getBytes();
/*      */                 }
/*      */                 else {
/* 2622 */                   String origColName = results.getString("Field");
/*      */ 
/* 2624 */                   Integer realOrdinal = (Integer)((Map)ordinalFixUpMap).get(origColName);
/*      */ 
/* 2627 */                   if (realOrdinal != null) {
/* 2628 */                     rowVal[16] = realOrdinal.toString().getBytes();
/*      */                   }
/*      */                   else {
/* 2631 */                     throw SQLError.createSQLException("Can not find column in full column list to determine true ordinal position.", "S1000", DatabaseMetaData.this.getExceptionInterceptor());
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/* 2637 */                 rowVal[17] = DatabaseMetaData.this.s2b(typeDesc.isNullable);
/*      */ 
/* 2640 */                 rowVal[18] = null;
/* 2641 */                 rowVal[19] = null;
/* 2642 */                 rowVal[20] = null;
/* 2643 */                 rowVal[21] = null;
/*      */ 
/* 2645 */                 rowVal[22] = DatabaseMetaData.this.s2b("");
/*      */ 
/* 2647 */                 String extra = results.getString("Extra");
/*      */ 
/* 2649 */                 if (extra != null) {
/* 2650 */                   rowVal[22] = DatabaseMetaData.this.s2b(StringUtils.indexOfIgnoreCase(extra, "auto_increment") != -1 ? "YES" : "NO");
/*      */                 }
/*      */ 
/* 2656 */                 this.val$rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             } finally {
/* 2659 */               if (results != null) {
/*      */                 try {
/* 2661 */                   results.close();
/*      */                 }
/*      */                 catch (Exception ex)
/*      */                 {
/*      */                 }
/* 2666 */                 results = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2410 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 2673 */       if (stmt != null) {
/* 2674 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 2678 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 2680 */     return results;
/*      */   }
/*      */ 
/*      */   protected Field[] createColumnsFields() {
/* 2684 */     Field[] fields = new Field[23];
/* 2685 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 2686 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 2687 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 2688 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 2689 */     fields[4] = new Field("", "DATA_TYPE", 4, 5);
/* 2690 */     fields[5] = new Field("", "TYPE_NAME", 1, 16);
/* 2691 */     fields[6] = new Field("", "COLUMN_SIZE", 4, Integer.toString(2147483647).length());
/*      */ 
/* 2693 */     fields[7] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 2694 */     fields[8] = new Field("", "DECIMAL_DIGITS", 4, 10);
/* 2695 */     fields[9] = new Field("", "NUM_PREC_RADIX", 4, 10);
/* 2696 */     fields[10] = new Field("", "NULLABLE", 4, 10);
/* 2697 */     fields[11] = new Field("", "REMARKS", 1, 0);
/* 2698 */     fields[12] = new Field("", "COLUMN_DEF", 1, 0);
/* 2699 */     fields[13] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 2700 */     fields[14] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 2701 */     fields[15] = new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(2147483647).length());
/*      */ 
/* 2703 */     fields[16] = new Field("", "ORDINAL_POSITION", 4, 10);
/* 2704 */     fields[17] = new Field("", "IS_NULLABLE", 1, 3);
/* 2705 */     fields[18] = new Field("", "SCOPE_CATALOG", 1, 255);
/* 2706 */     fields[19] = new Field("", "SCOPE_SCHEMA", 1, 255);
/* 2707 */     fields[20] = new Field("", "SCOPE_TABLE", 1, 255);
/* 2708 */     fields[21] = new Field("", "SOURCE_DATA_TYPE", 5, 10);
/* 2709 */     fields[22] = new Field("", "IS_AUTOINCREMENT", 1, 3);
/* 2710 */     return fields;
/*      */   }
/*      */ 
/*      */   public Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 2721 */     return this.conn;
/*      */   }
/*      */ 
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/* 2795 */     if (primaryTable == null) {
/* 2796 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2800 */     Field[] fields = createFkMetadataFields();
/*      */ 
/* 2802 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 2804 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2806 */       java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 2810 */         new IterateBlock(getCatalogIterator(foreignCatalog), stmt, foreignTable, primaryTable, foreignCatalog, foreignSchema, primaryCatalog, primarySchema, tuples)
/*      */         {
/*      */           void forEach(String catalogStr) throws SQLException {
/* 2813 */             ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 2820 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
/* 2821 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, null);
/*      */               }
/*      */               else {
/* 2824 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */ 
/* 2826 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2827 */                 queryBuf.append(catalogStr);
/* 2828 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2830 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 2834 */               String foreignTableWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$foreignTable);
/* 2835 */               String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$primaryTable);
/*      */ 
/* 2843 */               while (fkresults.next()) {
/* 2844 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 2846 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 2850 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 2853 */                   if (comment != null) {
/* 2854 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 2857 */                     if (commentTokens.hasMoreTokens()) {
/* 2858 */                       String str1 = commentTokens.nextToken();
/*      */                     }
/*      */ 
/* 2863 */                     while (commentTokens.hasMoreTokens()) {
/* 2864 */                       String keys = commentTokens.nextToken();
/*      */ 
/* 2866 */                       DatabaseMetaData.LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
/*      */ 
/* 2868 */                       int keySeq = 0;
/*      */ 
/* 2870 */                       Iterator referencingColumns = parsedInfo.localColumnsList.iterator();
/*      */ 
/* 2872 */                       Iterator referencedColumns = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/* 2875 */                       while (referencingColumns.hasNext()) {
/* 2876 */                         String referencingColumn = DatabaseMetaData.this.removeQuotedId((String)referencingColumns.next());
/*      */ 
/* 2882 */                         byte[][] tuple = new byte[14][];
/* 2883 */                         tuple[4] = (this.val$foreignCatalog == null ? null : DatabaseMetaData.this.s2b(this.val$foreignCatalog));
/*      */ 
/* 2885 */                         tuple[5] = (this.val$foreignSchema == null ? null : DatabaseMetaData.this.s2b(this.val$foreignSchema));
/*      */ 
/* 2887 */                         String dummy = fkresults.getString("Name");
/*      */ 
/* 2890 */                         if (dummy.compareTo(foreignTableWithCase) != 0)
/*      */                         {
/*      */                           continue;
/*      */                         }
/*      */ 
/* 2895 */                         tuple[6] = DatabaseMetaData.this.s2b(dummy);
/*      */ 
/* 2897 */                         tuple[7] = DatabaseMetaData.this.s2b(referencingColumn);
/* 2898 */                         tuple[0] = (this.val$primaryCatalog == null ? null : DatabaseMetaData.this.s2b(this.val$primaryCatalog));
/*      */ 
/* 2900 */                         tuple[1] = (this.val$primarySchema == null ? null : DatabaseMetaData.this.s2b(this.val$primarySchema));
/*      */ 
/* 2906 */                         if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) != 0)
/*      */                         {
/*      */                           continue;
/*      */                         }
/*      */ 
/* 2911 */                         tuple[2] = DatabaseMetaData.this.s2b(parsedInfo.referencedTable);
/* 2912 */                         tuple[3] = DatabaseMetaData.this.s2b(DatabaseMetaData.this.removeQuotedId((String)referencedColumns.next()));
/*      */ 
/* 2914 */                         tuple[8] = Integer.toString(keySeq).getBytes();
/*      */ 
/* 2917 */                         int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
/*      */ 
/* 2919 */                         tuple[9] = Integer.toString(actions[1]).getBytes();
/*      */ 
/* 2921 */                         tuple[10] = Integer.toString(actions[0]).getBytes();
/*      */ 
/* 2923 */                         tuple[11] = null;
/* 2924 */                         tuple[12] = null;
/* 2925 */                         tuple[13] = Integer.toString(7).getBytes();
/*      */ 
/* 2929 */                         this.val$tuples.add(new ByteArrayRow(tuple, DatabaseMetaData.this.getExceptionInterceptor()));
/* 2930 */                         keySeq++;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/* 2938 */               if (fkresults != null) {
/*      */                 try {
/* 2940 */                   fkresults.close();
/*      */                 } catch (Exception sqlEx) {
/* 2942 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2946 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2810 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 2952 */         if (stmt != null) {
/* 2953 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2958 */     ResultSet results = buildResultSet(fields, tuples);
/*      */ 
/* 2960 */     return results;
/*      */   }
/*      */ 
/*      */   protected Field[] createFkMetadataFields() {
/* 2964 */     Field[] fields = new Field[14];
/* 2965 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2966 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2967 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2968 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2969 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2970 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2971 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2972 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2973 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2974 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2975 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2976 */     fields[11] = new Field("", "FK_NAME", 1, 0);
/* 2977 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2978 */     fields[13] = new Field("", "DEFERRABILITY", 5, 2);
/* 2979 */     return fields;
/*      */   }
/*      */ 
/*      */   public int getDatabaseMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 2986 */     return this.conn.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   public int getDatabaseMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 2993 */     return this.conn.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/* 3004 */     return "MySQL";
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/* 3015 */     return this.conn.getServerVersion();
/*      */   }
/*      */ 
/*      */   public int getDefaultTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 3028 */     if (this.conn.supportsIsolationLevel()) {
/* 3029 */       return 2;
/*      */     }
/*      */ 
/* 3032 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getDriverMajorVersion()
/*      */   {
/* 3041 */     return NonRegisteringDriver.getMajorVersionInternal();
/*      */   }
/*      */ 
/*      */   public int getDriverMinorVersion()
/*      */   {
/* 3050 */     return NonRegisteringDriver.getMinorVersionInternal();
/*      */   }
/*      */ 
/*      */   public String getDriverName()
/*      */     throws SQLException
/*      */   {
/* 3061 */     return "MySQL-AB JDBC Driver";
/*      */   }
/*      */ 
/*      */   public String getDriverVersion()
/*      */     throws SQLException
/*      */   {
/* 3072 */     return "mysql-connector-java-5.1.23 ( Revision: ${bzr.revision-id} )";
/*      */   }
/*      */ 
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3136 */     if (table == null) {
/* 3137 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3141 */     Field[] fields = createFkMetadataFields();
/*      */ 
/* 3143 */     ArrayList rows = new ArrayList();
/*      */ 
/* 3145 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 3147 */       java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 3151 */         new IterateBlock(getCatalogIterator(catalog), stmt, table, rows) {
/*      */           void forEach(String catalogStr) throws SQLException {
/* 3153 */             ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 3160 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/* 3163 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, null);
/*      */               }
/*      */               else {
/* 3166 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */ 
/* 3168 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3169 */                 queryBuf.append(catalogStr);
/* 3170 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 3172 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 3177 */               String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$table);
/*      */ 
/* 3183 */               while (fkresults.next()) {
/* 3184 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 3186 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 3190 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 3193 */                   if (comment != null) {
/* 3194 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 3197 */                     if (commentTokens.hasMoreTokens()) {
/* 3198 */                       commentTokens.nextToken();
/*      */ 
/* 3203 */                       while (commentTokens.hasMoreTokens()) {
/* 3204 */                         String keys = commentTokens.nextToken();
/*      */ 
/* 3206 */                         DatabaseMetaData.this.getExportKeyResults(catalogStr, tableNameWithCase, keys, this.val$rows, fkresults.getString("Name"));
/*      */                       }
/*      */ 
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */             finally
/*      */             {
/* 3220 */               if (fkresults != null) {
/*      */                 try {
/* 3222 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 3224 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 3228 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3151 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 3234 */         if (stmt != null) {
/* 3235 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3240 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 3242 */     return results;
/*      */   }
/*      */ 
/*      */   protected void getExportKeyResults(String catalog, String exportingTable, String keysComment, List<ResultSetRow> tuples, String fkTableName)
/*      */     throws SQLException
/*      */   {
/* 3266 */     getResultsImpl(catalog, exportingTable, keysComment, tuples, fkTableName, true);
/*      */   }
/*      */ 
/*      */   public String getExtraNameCharacters()
/*      */     throws SQLException
/*      */   {
/* 3279 */     return "#@";
/*      */   }
/*      */ 
/*      */   protected int[] getForeignKeyActions(String commentString)
/*      */   {
/* 3292 */     int[] actions = { 3, 3 };
/*      */ 
/* 3296 */     int lastParenIndex = commentString.lastIndexOf(")");
/*      */ 
/* 3298 */     if (lastParenIndex != commentString.length() - 1) {
/* 3299 */       String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
/*      */ 
/* 3302 */       actions[0] = getCascadeDeleteOption(cascadeOptions);
/* 3303 */       actions[1] = getCascadeUpdateOption(cascadeOptions);
/*      */     }
/*      */ 
/* 3306 */     return actions;
/*      */   }
/*      */ 
/*      */   public String getIdentifierQuoteString()
/*      */     throws SQLException
/*      */   {
/* 3319 */     if (this.conn.supportsQuotedIdentifiers()) {
/* 3320 */       if (!this.conn.useAnsiQuotedIdentifiers()) {
/* 3321 */         return "`";
/*      */       }
/*      */ 
/* 3324 */       return "\"";
/*      */     }
/*      */ 
/* 3327 */     return " ";
/*      */   }
/*      */ 
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3391 */     if (table == null) {
/* 3392 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3396 */     Field[] fields = createFkMetadataFields();
/*      */ 
/* 3398 */     ArrayList rows = new ArrayList();
/*      */ 
/* 3400 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 3402 */       java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 3406 */         new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) {
/*      */           void forEach(String catalogStr) throws SQLException {
/* 3408 */             ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 3415 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/* 3418 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, this.val$table);
/*      */               }
/*      */               else {
/* 3421 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS ");
/*      */ 
/* 3423 */                 queryBuf.append(" FROM ");
/* 3424 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3425 */                 queryBuf.append(catalogStr);
/* 3426 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3427 */                 queryBuf.append(" LIKE '");
/* 3428 */                 queryBuf.append(this.val$table);
/* 3429 */                 queryBuf.append("'");
/*      */ 
/* 3431 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 3439 */               while (fkresults.next()) {
/* 3440 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 3442 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 3446 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 3449 */                   if (comment != null) {
/* 3450 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 3453 */                     if (commentTokens.hasMoreTokens()) {
/* 3454 */                       commentTokens.nextToken();
/*      */ 
/* 3459 */                       while (commentTokens.hasMoreTokens()) {
/* 3460 */                         String keys = commentTokens.nextToken();
/*      */ 
/* 3462 */                         DatabaseMetaData.this.getImportKeyResults(catalogStr, this.val$table, keys, this.val$rows);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 3471 */               if (fkresults != null) {
/*      */                 try {
/* 3473 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 3475 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 3479 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 3406 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 3485 */         if (stmt != null) {
/* 3486 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3491 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 3493 */     return results;
/*      */   }
/*      */ 
/*      */   protected void getImportKeyResults(String catalog, String importingTable, String keysComment, List<ResultSetRow> tuples)
/*      */     throws SQLException
/*      */   {
/* 3515 */     getResultsImpl(catalog, importingTable, keysComment, tuples, null, false);
/*      */   }
/*      */ 
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/* 3586 */     Field[] fields = createIndexInfoFields();
/*      */ 
/* 3588 */     ArrayList rows = new ArrayList();
/* 3589 */     java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 3593 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, unique, rows)
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 3596 */           ResultSet results = null;
/*      */           try
/*      */           {
/* 3599 */             StringBuffer queryBuf = new StringBuffer("SHOW INDEX FROM ");
/*      */ 
/* 3601 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3602 */             queryBuf.append(this.val$table);
/* 3603 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3604 */             queryBuf.append(" FROM ");
/* 3605 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3606 */             queryBuf.append(catalogStr);
/* 3607 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */             try
/*      */             {
/* 3610 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */             } catch (SQLException sqlEx) {
/* 3612 */               int errorCode = sqlEx.getErrorCode();
/*      */ 
/* 3616 */               if (!"42S02".equals(sqlEx.getSQLState()))
/*      */               {
/* 3619 */                 if (errorCode != 1146) {
/* 3620 */                   throw sqlEx;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 3625 */             while ((results != null) && (results.next())) {
/* 3626 */               byte[][] row = new byte[14][];
/* 3627 */               row[0] = (catalogStr == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr));
/*      */ 
/* 3630 */               row[1] = null;
/* 3631 */               row[2] = results.getBytes("Table");
/*      */ 
/* 3633 */               boolean indexIsUnique = results.getInt("Non_unique") == 0;
/*      */ 
/* 3636 */               row[3] = (!indexIsUnique ? DatabaseMetaData.this.s2b("true") : DatabaseMetaData.this.s2b("false"));
/*      */ 
/* 3638 */               row[4] = new byte[0];
/* 3639 */               row[5] = results.getBytes("Key_name");
/* 3640 */               row[6] = Integer.toString(3).getBytes();
/*      */ 
/* 3643 */               row[7] = results.getBytes("Seq_in_index");
/* 3644 */               row[8] = results.getBytes("Column_name");
/* 3645 */               row[9] = results.getBytes("Collation");
/*      */ 
/* 3649 */               long cardinality = results.getLong("Cardinality");
/*      */ 
/* 3651 */               if (cardinality > 2147483647L) {
/* 3652 */                 cardinality = 2147483647L;
/*      */               }
/*      */ 
/* 3655 */               row[10] = DatabaseMetaData.this.s2b(String.valueOf(cardinality));
/* 3656 */               row[11] = DatabaseMetaData.this.s2b("0");
/* 3657 */               row[12] = null;
/*      */ 
/* 3659 */               if (this.val$unique) {
/* 3660 */                 if (indexIsUnique) {
/* 3661 */                   this.val$rows.add(new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 }
/*      */               }
/*      */               else
/* 3665 */                 this.val$rows.add(new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */             }
/*      */           }
/*      */           finally {
/* 3669 */             if (results != null) {
/*      */               try {
/* 3671 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 3676 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3593 */       .doForAll();
/*      */ 
/* 3682 */       ResultSet indexInfo = buildResultSet(fields, rows);
/*      */ 
/* 3684 */       localResultSet1 = indexInfo;
/*      */     }
/*      */     finally
/*      */     {
/*      */       ResultSet localResultSet1;
/* 3686 */       if (stmt != null)
/* 3687 */         stmt.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Field[] createIndexInfoFields()
/*      */   {
/* 3693 */     Field[] fields = new Field[13];
/* 3694 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3695 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3696 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3697 */     fields[3] = new Field("", "NON_UNIQUE", 16, 4);
/* 3698 */     fields[4] = new Field("", "INDEX_QUALIFIER", 1, 1);
/* 3699 */     fields[5] = new Field("", "INDEX_NAME", 1, 32);
/* 3700 */     fields[6] = new Field("", "TYPE", 5, 32);
/* 3701 */     fields[7] = new Field("", "ORDINAL_POSITION", 5, 5);
/* 3702 */     fields[8] = new Field("", "COLUMN_NAME", 1, 32);
/* 3703 */     fields[9] = new Field("", "ASC_OR_DESC", 1, 1);
/* 3704 */     fields[10] = new Field("", "CARDINALITY", 4, 20);
/* 3705 */     fields[11] = new Field("", "PAGES", 4, 10);
/* 3706 */     fields[12] = new Field("", "FILTER_CONDITION", 1, 32);
/* 3707 */     return fields;
/*      */   }
/*      */ 
/*      */   public int getJDBCMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 3714 */     return 4;
/*      */   }
/*      */ 
/*      */   public int getJDBCMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 3721 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxBinaryLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3732 */     return 16777208;
/*      */   }
/*      */ 
/*      */   public int getMaxCatalogNameLength()
/*      */     throws SQLException
/*      */   {
/* 3743 */     return 32;
/*      */   }
/*      */ 
/*      */   public int getMaxCharLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3754 */     return 16777208;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnNameLength()
/*      */     throws SQLException
/*      */   {
/* 3765 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInGroupBy()
/*      */     throws SQLException
/*      */   {
/* 3776 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInIndex()
/*      */     throws SQLException
/*      */   {
/* 3787 */     return 16;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 3798 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInSelect()
/*      */     throws SQLException
/*      */   {
/* 3809 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInTable()
/*      */     throws SQLException
/*      */   {
/* 3820 */     return 512;
/*      */   }
/*      */ 
/*      */   public int getMaxConnections()
/*      */     throws SQLException
/*      */   {
/* 3831 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxCursorNameLength()
/*      */     throws SQLException
/*      */   {
/* 3842 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxIndexLength()
/*      */     throws SQLException
/*      */   {
/* 3853 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxProcedureNameLength()
/*      */     throws SQLException
/*      */   {
/* 3864 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxRowSize()
/*      */     throws SQLException
/*      */   {
/* 3875 */     return 2147483639;
/*      */   }
/*      */ 
/*      */   public int getMaxSchemaNameLength()
/*      */     throws SQLException
/*      */   {
/* 3886 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxStatementLength()
/*      */     throws SQLException
/*      */   {
/* 3897 */     return MysqlIO.getMaxBuf() - 4;
/*      */   }
/*      */ 
/*      */   public int getMaxStatements()
/*      */     throws SQLException
/*      */   {
/* 3908 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxTableNameLength()
/*      */     throws SQLException
/*      */   {
/* 3919 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxTablesInSelect()
/*      */     throws SQLException
/*      */   {
/* 3930 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxUserNameLength()
/*      */     throws SQLException
/*      */   {
/* 3941 */     return 16;
/*      */   }
/*      */ 
/*      */   public String getNumericFunctions()
/*      */     throws SQLException
/*      */   {
/* 3952 */     return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
/*      */   }
/*      */ 
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3984 */     Field[] fields = new Field[6];
/* 3985 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3986 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3987 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3988 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 3989 */     fields[4] = new Field("", "KEY_SEQ", 5, 5);
/* 3990 */     fields[5] = new Field("", "PK_NAME", 1, 32);
/*      */ 
/* 3992 */     if (table == null) {
/* 3993 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3997 */     ArrayList rows = new ArrayList();
/* 3998 */     java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 4002 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 4004 */           ResultSet rs = null;
/*      */           try
/*      */           {
/* 4008 */             StringBuffer queryBuf = new StringBuffer("SHOW KEYS FROM ");
/*      */ 
/* 4010 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 4011 */             queryBuf.append(this.val$table);
/* 4012 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 4013 */             queryBuf.append(" FROM ");
/* 4014 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 4015 */             queryBuf.append(catalogStr);
/* 4016 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 4018 */             rs = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 4020 */             TreeMap sortMap = new TreeMap();
/*      */ 
/* 4022 */             while (rs.next()) {
/* 4023 */               String keyType = rs.getString("Key_name");
/*      */ 
/* 4025 */               if ((keyType != null) && (
/* 4026 */                 (keyType.equalsIgnoreCase("PRIMARY")) || (keyType.equalsIgnoreCase("PRI"))))
/*      */               {
/* 4028 */                 byte[][] tuple = new byte[6][];
/* 4029 */                 tuple[0] = (catalogStr == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr));
/*      */ 
/* 4031 */                 tuple[1] = null;
/* 4032 */                 tuple[2] = DatabaseMetaData.this.s2b(this.val$table);
/*      */ 
/* 4034 */                 String columnName = rs.getString("Column_name");
/*      */ 
/* 4036 */                 tuple[3] = DatabaseMetaData.this.s2b(columnName);
/* 4037 */                 tuple[4] = DatabaseMetaData.this.s2b(rs.getString("Seq_in_index"));
/* 4038 */                 tuple[5] = DatabaseMetaData.this.s2b(keyType);
/* 4039 */                 sortMap.put(columnName, tuple);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 4045 */             Iterator sortedIterator = sortMap.values().iterator();
/*      */ 
/* 4047 */             while (sortedIterator.hasNext())
/* 4048 */               this.val$rows.add(new ByteArrayRow((byte[][])sortedIterator.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */           }
/*      */           finally
/*      */           {
/* 4052 */             if (rs != null) {
/*      */               try {
/* 4054 */                 rs.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 4059 */               rs = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 4002 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 4065 */       if (stmt != null) {
/* 4066 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 4070 */     ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 4072 */     return results;
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4144 */     Field[] fields = createProcedureColumnsFields();
/*      */ 
/* 4146 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, true);
/*      */   }
/*      */ 
/*      */   protected Field[] createProcedureColumnsFields()
/*      */   {
/* 4153 */     Field[] fields = new Field[13];
/*      */ 
/* 4155 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 512);
/* 4156 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 512);
/* 4157 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 512);
/* 4158 */     fields[3] = new Field("", "COLUMN_NAME", 1, 512);
/* 4159 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 64);
/* 4160 */     fields[5] = new Field("", "DATA_TYPE", 5, 6);
/* 4161 */     fields[6] = new Field("", "TYPE_NAME", 1, 64);
/* 4162 */     fields[7] = new Field("", "PRECISION", 4, 12);
/* 4163 */     fields[8] = new Field("", "LENGTH", 4, 12);
/* 4164 */     fields[9] = new Field("", "SCALE", 5, 12);
/* 4165 */     fields[10] = new Field("", "RADIX", 5, 6);
/* 4166 */     fields[11] = new Field("", "NULLABLE", 5, 6);
/* 4167 */     fields[12] = new Field("", "REMARKS", 1, 512);
/* 4168 */     return fields;
/*      */   }
/*      */ 
/*      */   protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions)
/*      */     throws SQLException
/*      */   {
/* 4177 */     List proceduresToExtractList = new ArrayList();
/*      */ 
/* 4179 */     ResultSet procedureNameRs = null;
/*      */ 
/* 4181 */     if (supportsStoredProcedures())
/*      */     {
/*      */       try
/*      */       {
/* 4185 */         String tmpProcedureOrFunctionNamePattern = null;
/*      */ 
/* 4187 */         if ((procedureOrFunctionNamePattern != null) && (!procedureOrFunctionNamePattern.equals("%"))) {
/* 4188 */           tmpProcedureOrFunctionNamePattern = StringUtils.sanitizeProcOrFuncName(procedureOrFunctionNamePattern);
/*      */         }
/*      */ 
/* 4192 */         if (tmpProcedureOrFunctionNamePattern == null) {
/* 4193 */           tmpProcedureOrFunctionNamePattern = procedureOrFunctionNamePattern;
/*      */         }
/*      */         else
/*      */         {
/* 4198 */           String tmpCatalog = catalog;
/* 4199 */           List parseList = StringUtils.splitDBdotName(tmpProcedureOrFunctionNamePattern, tmpCatalog, this.quotedId, this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 4203 */           if (parseList.size() == 2) {
/* 4204 */             tmpCatalog = (String)parseList.get(0);
/* 4205 */             tmpProcedureOrFunctionNamePattern = (String)parseList.get(1);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 4211 */         procedureNameRs = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, tmpProcedureOrFunctionNamePattern, returnProcedures, returnFunctions);
/*      */ 
/* 4223 */         String tmpstrPNameRs = null;
/* 4224 */         String tmpstrCatNameRs = null;
/*      */ 
/* 4226 */         boolean hasResults = false;
/* 4227 */         while (procedureNameRs.next()) {
/* 4228 */           tmpstrCatNameRs = procedureNameRs.getString(1);
/* 4229 */           tmpstrPNameRs = procedureNameRs.getString(3);
/*      */ 
/* 4231 */           if (((!tmpstrCatNameRs.startsWith(this.quotedId)) || (!tmpstrCatNameRs.endsWith(this.quotedId))) && ((!tmpstrCatNameRs.startsWith("\"")) || (!tmpstrCatNameRs.endsWith("\""))))
/*      */           {
/* 4233 */             tmpstrCatNameRs = this.quotedId + tmpstrCatNameRs + this.quotedId;
/*      */           }
/* 4235 */           if (((!tmpstrPNameRs.startsWith(this.quotedId)) || (!tmpstrPNameRs.endsWith(this.quotedId))) && ((!tmpstrPNameRs.startsWith("\"")) || (!tmpstrPNameRs.endsWith("\""))))
/*      */           {
/* 4237 */             tmpstrPNameRs = this.quotedId + tmpstrPNameRs + this.quotedId;
/*      */           }
/*      */ 
/* 4240 */           if (proceduresToExtractList.indexOf(tmpstrCatNameRs + "." + tmpstrPNameRs) < 0) {
/* 4241 */             proceduresToExtractList.add(tmpstrCatNameRs + "." + tmpstrPNameRs);
/*      */           }
/* 4243 */           hasResults = true;
/*      */         }
/*      */ 
/* 4247 */         if (hasResults)
/*      */         {
/* 4254 */           Collections.sort(proceduresToExtractList);
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 4263 */         SQLException rethrowSqlEx = null;
/*      */ 
/* 4265 */         if (procedureNameRs != null) {
/*      */           try {
/* 4267 */             procedureNameRs.close();
/*      */           } catch (SQLException sqlEx) {
/* 4269 */             rethrowSqlEx = sqlEx;
/*      */           }
/*      */         }
/*      */ 
/* 4273 */         if (rethrowSqlEx != null) {
/* 4274 */           throw rethrowSqlEx;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 4279 */     ArrayList resultRows = new ArrayList();
/* 4280 */     int idx = 0;
/* 4281 */     String procNameToCall = "";
/*      */ 
/* 4283 */     for (Iterator iter = proceduresToExtractList.iterator(); iter.hasNext(); ) {
/* 4284 */       String procName = (String)iter.next();
/*      */ 
/* 4287 */       if (!" ".equals(this.quotedId)) {
/* 4288 */         idx = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procName, ".", this.quotedId.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */       }
/*      */       else
/*      */       {
/* 4292 */         idx = procName.indexOf(".");
/*      */       }
/*      */ 
/* 4295 */       if (idx > 0) {
/* 4296 */         catalog = procName.substring(0, idx);
/* 4297 */         if ((this.quotedId != " ") && (catalog.startsWith(this.quotedId)) && (catalog.endsWith(this.quotedId))) {
/* 4298 */           catalog = procName.substring(1, catalog.length() - 1);
/*      */         }
/* 4300 */         procNameToCall = procName;
/*      */       }
/*      */       else {
/* 4303 */         procNameToCall = procName;
/*      */       }
/* 4305 */       getCallStmtParameterTypes(catalog, procNameToCall, columnNamePattern, resultRows, fields.length == 17);
/*      */     }
/*      */ 
/* 4310 */     return buildResultSet(fields, resultRows);
/*      */   }
/*      */ 
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4356 */     Field[] fields = createFieldMetadataForGetProcedures();
/*      */ 
/* 4358 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, procedureNamePattern, true, true);
/*      */   }
/*      */ 
/*      */   private Field[] createFieldMetadataForGetProcedures()
/*      */   {
/* 4363 */     Field[] fields = new Field[9];
/* 4364 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 255);
/* 4365 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 255);
/* 4366 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 255);
/* 4367 */     fields[3] = new Field("", "reserved1", 1, 0);
/* 4368 */     fields[4] = new Field("", "reserved2", 1, 0);
/* 4369 */     fields[5] = new Field("", "reserved3", 1, 0);
/* 4370 */     fields[6] = new Field("", "REMARKS", 1, 255);
/* 4371 */     fields[7] = new Field("", "PROCEDURE_TYPE", 5, 6);
/* 4372 */     fields[8] = new Field("", "SPECIFIC_NAME", 1, 255);
/*      */ 
/* 4374 */     return fields;
/*      */   }
/*      */ 
/*      */   protected ResultSet getProceduresAndOrFunctions(Field[] fields, String catalog, String schemaPattern, String procedureNamePattern, boolean returnProcedures, boolean returnFunctions)
/*      */     throws SQLException
/*      */   {
/* 4395 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/* 4397 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4398 */         procedureNamePattern = "%";
/*      */       else {
/* 4400 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4406 */     ArrayList procedureRows = new ArrayList();
/*      */ 
/* 4408 */     if (supportsStoredProcedures()) {
/* 4409 */       String procNamePattern = procedureNamePattern;
/*      */ 
/* 4411 */       Map procedureRowsOrderedByName = new TreeMap();
/*      */ 
/* 4413 */       new IterateBlock(getCatalogIterator(catalog), procNamePattern, returnProcedures, procedureRowsOrderedByName, returnFunctions, fields, procedureRows) {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 4415 */           String db = catalogStr;
/*      */ 
/* 4417 */           boolean fromSelect = false;
/* 4418 */           ResultSet proceduresRs = null;
/* 4419 */           boolean needsClientFiltering = true;
/* 4420 */           PreparedStatement proceduresStmt = DatabaseMetaData.this.conn.clientPrepareStatement("SELECT name, type, comment FROM mysql.proc WHERE name like ? and db <=> ? ORDER BY name");
/*      */           try
/*      */           {
/* 4429 */             boolean hasTypeColumn = false;
/*      */ 
/* 4431 */             if (db != null) {
/* 4432 */               if (DatabaseMetaData.this.conn.lowerCaseTableNames()) {
/* 4433 */                 db = db.toLowerCase();
/*      */               }
/* 4435 */               proceduresStmt.setString(2, db);
/*      */             } else {
/* 4437 */               proceduresStmt.setNull(2, 12);
/*      */             }
/*      */ 
/* 4440 */             int nameIndex = 1;
/*      */ 
/* 4442 */             if (proceduresStmt.getMaxRows() != 0) {
/* 4443 */               proceduresStmt.setMaxRows(0);
/*      */             }
/*      */ 
/* 4446 */             proceduresStmt.setString(1, this.val$procNamePattern);
/*      */             try
/*      */             {
/* 4449 */               proceduresRs = proceduresStmt.executeQuery();
/* 4450 */               fromSelect = true;
/* 4451 */               needsClientFiltering = false;
/* 4452 */               hasTypeColumn = true;
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/* 4460 */               proceduresStmt.close();
/*      */ 
/* 4462 */               fromSelect = false;
/*      */ 
/* 4464 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 1))
/* 4465 */                 nameIndex = 2;
/*      */               else {
/* 4467 */                 nameIndex = 1;
/*      */               }
/*      */ 
/* 4470 */               proceduresStmt = DatabaseMetaData.this.conn.clientPrepareStatement("SHOW PROCEDURE STATUS LIKE ?");
/*      */ 
/* 4473 */               if (proceduresStmt.getMaxRows() != 0) {
/* 4474 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */ 
/* 4477 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */ 
/* 4479 */               proceduresRs = proceduresStmt.executeQuery();
/*      */             }
/*      */ 
/* 4482 */             if (this.val$returnProcedures) {
/* 4483 */               DatabaseMetaData.this.convertToJdbcProcedureList(fromSelect, db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex);
/*      */             }
/*      */ 
/* 4488 */             if (!hasTypeColumn)
/*      */             {
/* 4490 */               if (proceduresStmt != null) {
/* 4491 */                 proceduresStmt.close();
/*      */               }
/*      */ 
/* 4494 */               proceduresStmt = DatabaseMetaData.this.conn.clientPrepareStatement("SHOW FUNCTION STATUS LIKE ?");
/*      */ 
/* 4497 */               if (proceduresStmt.getMaxRows() != 0) {
/* 4498 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */ 
/* 4501 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */ 
/* 4503 */               proceduresRs = proceduresStmt.executeQuery();
/*      */             }
/*      */ 
/* 4507 */             if (this.val$returnFunctions) {
/* 4508 */               DatabaseMetaData.this.convertToJdbcFunctionList(db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex, this.val$fields);
/*      */             }
/*      */ 
/* 4516 */             Iterator proceduresIter = this.val$procedureRowsOrderedByName.values().iterator();
/*      */ 
/* 4519 */             while (proceduresIter.hasNext())
/* 4520 */               this.val$procedureRows.add(proceduresIter.next());
/*      */           }
/*      */           finally {
/* 4523 */             SQLException rethrowSqlEx = null;
/*      */ 
/* 4525 */             if (proceduresRs != null) {
/*      */               try {
/* 4527 */                 proceduresRs.close();
/*      */               } catch (SQLException sqlEx) {
/* 4529 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */ 
/* 4533 */             if (proceduresStmt != null) {
/*      */               try {
/* 4535 */                 proceduresStmt.close();
/*      */               } catch (SQLException sqlEx) {
/* 4537 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */ 
/* 4541 */             if (rethrowSqlEx != null)
/* 4542 */               throw rethrowSqlEx;
/*      */           }
/*      */         }
/*      */       }
/* 4413 */       .doForAll();
/*      */     }
/*      */ 
/* 4549 */     return buildResultSet(fields, procedureRows);
/*      */   }
/*      */ 
/*      */   public String getProcedureTerm()
/*      */     throws SQLException
/*      */   {
/* 4561 */     return "PROCEDURE";
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 4568 */     return 1;
/*      */   }
/*      */ 
/*      */   private void getResultsImpl(String catalog, String table, String keysComment, List<ResultSetRow> tuples, String fkTableName, boolean isExport)
/*      */     throws SQLException
/*      */   {
/* 4575 */     LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
/*      */ 
/* 4577 */     if ((isExport) && (!parsedInfo.referencedTable.equals(table))) {
/* 4578 */       return;
/*      */     }
/*      */ 
/* 4581 */     if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size())
/*      */     {
/* 4583 */       throw SQLError.createSQLException("Error parsing foreign keys definition,number of local and referenced columns is not the same.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4589 */     Iterator localColumnNames = parsedInfo.localColumnsList.iterator();
/* 4590 */     Iterator referColumnNames = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/* 4592 */     int keySeqIndex = 1;
/*      */ 
/* 4594 */     while (localColumnNames.hasNext()) {
/* 4595 */       byte[][] tuple = new byte[14][];
/* 4596 */       String lColumnName = removeQuotedId((String)localColumnNames.next());
/* 4597 */       String rColumnName = removeQuotedId((String)referColumnNames.next());
/* 4598 */       tuple[4] = (catalog == null ? new byte[0] : s2b(catalog));
/*      */ 
/* 4600 */       tuple[5] = null;
/* 4601 */       tuple[6] = s2b(isExport ? fkTableName : table);
/* 4602 */       tuple[7] = s2b(lColumnName);
/* 4603 */       tuple[0] = s2b(parsedInfo.referencedCatalog);
/* 4604 */       tuple[1] = null;
/* 4605 */       tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
/*      */ 
/* 4607 */       tuple[3] = s2b(rColumnName);
/* 4608 */       tuple[8] = s2b(Integer.toString(keySeqIndex++));
/*      */ 
/* 4610 */       int[] actions = getForeignKeyActions(keysComment);
/*      */ 
/* 4612 */       tuple[9] = s2b(Integer.toString(actions[1]));
/* 4613 */       tuple[10] = s2b(Integer.toString(actions[0]));
/* 4614 */       tuple[11] = s2b(parsedInfo.constraintName);
/* 4615 */       tuple[12] = null;
/* 4616 */       tuple[13] = s2b(Integer.toString(7));
/*      */ 
/* 4618 */       tuples.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet getSchemas()
/*      */     throws SQLException
/*      */   {
/* 4638 */     Field[] fields = new Field[2];
/* 4639 */     fields[0] = new Field("", "TABLE_SCHEM", 1, 0);
/* 4640 */     fields[1] = new Field("", "TABLE_CATALOG", 1, 0);
/*      */ 
/* 4642 */     ArrayList tuples = new ArrayList();
/* 4643 */     ResultSet results = buildResultSet(fields, tuples);
/*      */ 
/* 4645 */     return results;
/*      */   }
/*      */ 
/*      */   public String getSchemaTerm()
/*      */     throws SQLException
/*      */   {
/* 4656 */     return "";
/*      */   }
/*      */ 
/*      */   public String getSearchStringEscape()
/*      */     throws SQLException
/*      */   {
/* 4674 */     return "\\";
/*      */   }
/*      */ 
/*      */   public String getSQLKeywords()
/*      */     throws SQLException
/*      */   {
/* 4686 */     return mysqlKeywordsThatArentSQL92;
/*      */   }
/*      */ 
/*      */   public int getSQLStateType()
/*      */     throws SQLException
/*      */   {
/* 4693 */     if (this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 4694 */       return 2;
/*      */     }
/*      */ 
/* 4697 */     if (this.conn.getUseSqlStateCodes()) {
/* 4698 */       return 2;
/*      */     }
/*      */ 
/* 4701 */     return 1;
/*      */   }
/*      */ 
/*      */   public String getStringFunctions()
/*      */     throws SQLException
/*      */   {
/* 4712 */     return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
/*      */   }
/*      */ 
/*      */   public ResultSet getSuperTables(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4726 */     Field[] fields = new Field[4];
/* 4727 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4728 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4729 */     fields[2] = new Field("", "TABLE_NAME", 1, 32);
/* 4730 */     fields[3] = new Field("", "SUPERTABLE_NAME", 1, 32);
/*      */ 
/* 4732 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public ResultSet getSuperTypes(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4740 */     Field[] fields = new Field[6];
/* 4741 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 4742 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 4743 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 4744 */     fields[3] = new Field("", "SUPERTYPE_CAT", 1, 32);
/* 4745 */     fields[4] = new Field("", "SUPERTYPE_SCHEM", 1, 32);
/* 4746 */     fields[5] = new Field("", "SUPERTYPE_NAME", 1, 32);
/*      */ 
/* 4748 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public String getSystemFunctions()
/*      */     throws SQLException
/*      */   {
/* 4759 */     return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
/*      */   }
/*      */ 
/*      */   protected String getTableNameWithCase(String table) {
/* 4763 */     String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
/*      */ 
/* 4766 */     return tableNameWithCase;
/*      */   }
/*      */ 
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4806 */     if (tableNamePattern == null) {
/* 4807 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4808 */         tableNamePattern = "%";
/*      */       else {
/* 4810 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4816 */     Field[] fields = new Field[7];
/* 4817 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 4818 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 4819 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 4820 */     fields[3] = new Field("", "GRANTOR", 1, 77);
/* 4821 */     fields[4] = new Field("", "GRANTEE", 1, 77);
/* 4822 */     fields[5] = new Field("", "PRIVILEGE", 1, 64);
/* 4823 */     fields[6] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */ 
/* 4825 */     StringBuffer grantQuery = new StringBuffer("SELECT host,db,table_name,grantor,user,table_priv from mysql.tables_priv ");
/*      */ 
/* 4827 */     grantQuery.append(" WHERE ");
/*      */ 
/* 4829 */     if ((catalog != null) && (catalog.length() != 0)) {
/* 4830 */       grantQuery.append(" db='");
/* 4831 */       grantQuery.append(catalog);
/* 4832 */       grantQuery.append("' AND ");
/*      */     }
/*      */ 
/* 4835 */     grantQuery.append("table_name like '");
/* 4836 */     grantQuery.append(tableNamePattern);
/* 4837 */     grantQuery.append("'");
/*      */ 
/* 4839 */     ResultSet results = null;
/* 4840 */     ArrayList grantRows = new ArrayList();
/* 4841 */     java.sql.Statement stmt = null;
/*      */     try
/*      */     {
/* 4844 */       stmt = this.conn.createStatement();
/* 4845 */       stmt.setEscapeProcessing(false);
/*      */ 
/* 4847 */       results = stmt.executeQuery(grantQuery.toString());
/*      */ 
/* 4849 */       while (results.next()) {
/* 4850 */         String host = results.getString(1);
/* 4851 */         String db = results.getString(2);
/* 4852 */         String table = results.getString(3);
/* 4853 */         String grantor = results.getString(4);
/* 4854 */         String user = results.getString(5);
/*      */ 
/* 4856 */         if ((user == null) || (user.length() == 0)) {
/* 4857 */           user = "%";
/*      */         }
/*      */ 
/* 4860 */         StringBuffer fullUser = new StringBuffer(user);
/*      */ 
/* 4862 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 4863 */           fullUser.append("@");
/* 4864 */           fullUser.append(host);
/*      */         }
/*      */ 
/* 4867 */         String allPrivileges = results.getString(6);
/*      */ 
/* 4869 */         if (allPrivileges != null) {
/* 4870 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 4872 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */ 
/* 4874 */           while (st.hasMoreTokens()) {
/* 4875 */             String privilege = st.nextToken().trim();
/*      */ 
/* 4878 */             ResultSet columnResults = null;
/*      */             try
/*      */             {
/* 4881 */               columnResults = getColumns(catalog, schemaPattern, table, "%");
/*      */ 
/* 4884 */               while (columnResults.next()) {
/* 4885 */                 byte[][] tuple = new byte[8][];
/* 4886 */                 tuple[0] = s2b(db);
/* 4887 */                 tuple[1] = null;
/* 4888 */                 tuple[2] = s2b(table);
/*      */ 
/* 4890 */                 if (grantor != null)
/* 4891 */                   tuple[3] = s2b(grantor);
/*      */                 else {
/* 4893 */                   tuple[3] = null;
/*      */                 }
/*      */ 
/* 4896 */                 tuple[4] = s2b(fullUser.toString());
/* 4897 */                 tuple[5] = s2b(privilege);
/* 4898 */                 tuple[6] = null;
/* 4899 */                 grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */               }
/*      */             } finally {
/* 4902 */               if (columnResults != null)
/*      */                 try {
/* 4904 */                   columnResults.close();
/*      */                 }
/*      */                 catch (Exception ex) {
/*      */                 }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 4914 */       if (results != null) {
/*      */         try {
/* 4916 */           results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 4921 */         results = null;
/*      */       }
/*      */ 
/* 4924 */       if (stmt != null) {
/*      */         try {
/* 4926 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 4931 */         stmt = null;
/*      */       }
/*      */     }
/*      */ 
/* 4935 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/* 4977 */     if (tableNamePattern == null) {
/* 4978 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4979 */         tableNamePattern = "%";
/*      */       else {
/* 4981 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4987 */     Field[] fields = new Field[5];
/* 4988 */     fields[0] = new Field("", "TABLE_CAT", 12, 255);
/* 4989 */     fields[1] = new Field("", "TABLE_SCHEM", 12, 0);
/* 4990 */     fields[2] = new Field("", "TABLE_NAME", 12, 255);
/* 4991 */     fields[3] = new Field("", "TABLE_TYPE", 12, 5);
/* 4992 */     fields[4] = new Field("", "REMARKS", 12, 0);
/*      */ 
/* 4994 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 4996 */     java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 4999 */     String tmpCat = "";
/*      */ 
/* 5001 */     if ((catalog == null) || (catalog.length() == 0)) {
/* 5002 */       if (this.conn.getNullCatalogMeansCurrent())
/* 5003 */         tmpCat = this.database;
/*      */     }
/*      */     else {
/* 5006 */       tmpCat = catalog;
/*      */     }
/*      */ 
/* 5009 */     List parseList = StringUtils.splitDBdotName(tableNamePattern, tmpCat, this.quotedId, this.conn.isNoBackslashEscapesSet());
/*      */     String tableNamePat;
/*      */     String tableNamePat;
/* 5012 */     if (parseList.size() == 2)
/* 5013 */       tableNamePat = (String)parseList.get(1);
/*      */     else {
/* 5015 */       tableNamePat = tableNamePattern;
/*      */     }
/*      */ 
/* 5018 */     boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase(catalog);
/*      */     try
/*      */     {
/* 5022 */       new IterateBlock(getCatalogIterator(catalog), stmt, tableNamePat, types, operatingOnInformationSchema, tuples) {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 5024 */           ResultSet results = null;
/*      */           try
/*      */           {
/* 5028 */             if (!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2))
/*      */               try {
/* 5030 */                 results = this.val$stmt.executeQuery("SHOW TABLES FROM " + DatabaseMetaData.this.quotedId + catalogStr + DatabaseMetaData.this.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/* 5036 */                 if ("08S01".equals(sqlEx.getSQLState())) {
/* 5037 */                   throw sqlEx;
/*      */                 }
/*      */ 
/* 5040 */                 jsr 783;
/*      */               }
/*      */             else {
/*      */               try {
/* 5044 */                 results = this.val$stmt.executeQuery("SHOW FULL TABLES FROM " + DatabaseMetaData.this.quotedId + catalogStr + DatabaseMetaData.this.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/* 5050 */                 if ("08S01".equals(sqlEx.getSQLState())) {
/* 5051 */                   throw sqlEx;
/*      */                 }
/*      */ 
/* 5054 */                 jsr 695;
/*      */               }
/*      */             }
/*      */ 
/* 5058 */             boolean shouldReportTables = false;
/* 5059 */             boolean shouldReportViews = false;
/* 5060 */             boolean shouldReportSystemTables = false;
/*      */ 
/* 5062 */             if ((this.val$types == null) || (this.val$types.length == 0)) {
/* 5063 */               shouldReportTables = true;
/* 5064 */               shouldReportViews = true;
/* 5065 */               shouldReportSystemTables = true;
/*      */             } else {
/* 5067 */               for (int i = 0; i < this.val$types.length; i++) {
/* 5068 */                 if ("TABLE".equalsIgnoreCase(this.val$types[i])) {
/* 5069 */                   shouldReportTables = true;
/*      */                 }
/*      */ 
/* 5072 */                 if ("VIEW".equalsIgnoreCase(this.val$types[i])) {
/* 5073 */                   shouldReportViews = true;
/*      */                 }
/*      */ 
/* 5076 */                 if ("SYSTEM TABLE".equalsIgnoreCase(this.val$types[i])) {
/* 5077 */                   shouldReportSystemTables = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 5082 */             int typeColumnIndex = 0;
/* 5083 */             boolean hasTableTypes = false;
/*      */ 
/* 5085 */             if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2))
/*      */             {
/*      */               try
/*      */               {
/* 5090 */                 typeColumnIndex = results.findColumn("table_type");
/*      */ 
/* 5092 */                 hasTableTypes = true;
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/*      */                 try
/*      */                 {
/* 5104 */                   typeColumnIndex = results.findColumn("Type");
/*      */ 
/* 5106 */                   hasTableTypes = true;
/*      */                 } catch (SQLException sqlEx2) {
/* 5108 */                   hasTableTypes = false;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 5113 */             TreeMap tablesOrderedByName = null;
/* 5114 */             TreeMap viewsOrderedByName = null;
/*      */ 
/* 5116 */             while (results.next()) {
/* 5117 */               byte[][] row = new byte[5][];
/* 5118 */               row[0] = (catalogStr == null ? null : DatabaseMetaData.this.s2b(catalogStr));
/*      */ 
/* 5120 */               row[1] = null;
/* 5121 */               row[2] = results.getBytes(1);
/* 5122 */               row[4] = new byte[0];
/*      */ 
/* 5124 */               if (hasTableTypes) {
/* 5125 */                 String tableType = results.getString(typeColumnIndex);
/*      */ 
/* 5128 */                 if ((("table".equalsIgnoreCase(tableType)) || ("base table".equalsIgnoreCase(tableType))) && (shouldReportTables))
/*      */                 {
/* 5131 */                   boolean reportTable = false;
/*      */ 
/* 5133 */                   if ((!this.val$operatingOnInformationSchema) && (shouldReportTables)) {
/* 5134 */                     row[3] = DatabaseMetaData.TABLE_AS_BYTES;
/* 5135 */                     reportTable = true;
/* 5136 */                   } else if ((this.val$operatingOnInformationSchema) && (shouldReportSystemTables)) {
/* 5137 */                     row[3] = DatabaseMetaData.SYSTEM_TABLE_AS_BYTES;
/* 5138 */                     reportTable = true;
/*      */                   }
/*      */ 
/* 5141 */                   if (reportTable) {
/* 5142 */                     if (tablesOrderedByName == null) {
/* 5143 */                       tablesOrderedByName = new TreeMap();
/*      */                     }
/*      */ 
/* 5146 */                     tablesOrderedByName.put(results.getString(1), row);
/*      */                   }
/*      */                 }
/* 5149 */                 else if (("system view".equalsIgnoreCase(tableType)) && (shouldReportSystemTables)) {
/* 5150 */                   row[3] = DatabaseMetaData.SYSTEM_TABLE_AS_BYTES;
/*      */ 
/* 5152 */                   if (tablesOrderedByName == null) {
/* 5153 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 5156 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 }
/* 5158 */                 else if (("view".equalsIgnoreCase(tableType)) && (shouldReportViews))
/*      */                 {
/* 5160 */                   row[3] = DatabaseMetaData.VIEW_AS_BYTES;
/*      */ 
/* 5162 */                   if (viewsOrderedByName == null) {
/* 5163 */                     viewsOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 5166 */                   viewsOrderedByName.put(results.getString(1), row);
/*      */                 }
/* 5168 */                 else if (!hasTableTypes)
/*      */                 {
/* 5170 */                   row[3] = DatabaseMetaData.TABLE_AS_BYTES;
/*      */ 
/* 5172 */                   if (tablesOrderedByName == null) {
/* 5173 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 5176 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 }
/*      */ 
/*      */               }
/* 5180 */               else if (shouldReportTables)
/*      */               {
/* 5182 */                 row[3] = DatabaseMetaData.TABLE_AS_BYTES;
/*      */ 
/* 5184 */                 if (tablesOrderedByName == null) {
/* 5185 */                   tablesOrderedByName = new TreeMap();
/*      */                 }
/*      */ 
/* 5188 */                 tablesOrderedByName.put(results.getString(1), row);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 5197 */             if (tablesOrderedByName != null) {
/* 5198 */               Iterator tablesIter = tablesOrderedByName.values().iterator();
/*      */ 
/* 5201 */               while (tablesIter.hasNext()) {
/* 5202 */                 this.val$tuples.add(new ByteArrayRow((byte[][])tablesIter.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             }
/*      */ 
/* 5206 */             if (viewsOrderedByName != null) {
/* 5207 */               Iterator viewsIter = viewsOrderedByName.values().iterator();
/*      */ 
/* 5210 */               while (viewsIter.hasNext())
/* 5211 */                 this.val$tuples.add(new ByteArrayRow((byte[][])viewsIter.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 5216 */             if (results != null) {
/*      */               try {
/* 5218 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 5223 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 5022 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 5230 */       if (stmt != null) {
/* 5231 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 5235 */     ResultSet tables = buildResultSet(fields, tuples);
/*      */ 
/* 5237 */     return tables;
/*      */   }
/*      */ 
/*      */   public ResultSet getTableTypes()
/*      */     throws SQLException
/*      */   {
/* 5258 */     ArrayList tuples = new ArrayList();
/* 5259 */     Field[] fields = new Field[1];
/* 5260 */     fields[0] = new Field("", "TABLE_TYPE", 12, 5);
/*      */ 
/* 5262 */     byte[][] tableTypeRow = new byte[1][];
/* 5263 */     tableTypeRow[0] = TABLE_AS_BYTES;
/* 5264 */     tuples.add(new ByteArrayRow(tableTypeRow, getExceptionInterceptor()));
/*      */ 
/* 5266 */     if (this.conn.versionMeetsMinimum(5, 0, 1)) {
/* 5267 */       byte[][] viewTypeRow = new byte[1][];
/* 5268 */       viewTypeRow[0] = VIEW_AS_BYTES;
/* 5269 */       tuples.add(new ByteArrayRow(viewTypeRow, getExceptionInterceptor()));
/*      */     }
/*      */ 
/* 5272 */     byte[][] tempTypeRow = new byte[1][];
/* 5273 */     tempTypeRow[0] = s2b("LOCAL TEMPORARY");
/* 5274 */     tuples.add(new ByteArrayRow(tempTypeRow, getExceptionInterceptor()));
/*      */ 
/* 5276 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public String getTimeDateFunctions()
/*      */     throws SQLException
/*      */   {
/* 5287 */     return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
/*      */   }
/*      */ 
/*      */   public ResultSet getTypeInfo()
/*      */     throws SQLException
/*      */   {
/* 5396 */     Field[] fields = new Field[18];
/* 5397 */     fields[0] = new Field("", "TYPE_NAME", 1, 32);
/* 5398 */     fields[1] = new Field("", "DATA_TYPE", 4, 5);
/* 5399 */     fields[2] = new Field("", "PRECISION", 4, 10);
/* 5400 */     fields[3] = new Field("", "LITERAL_PREFIX", 1, 4);
/* 5401 */     fields[4] = new Field("", "LITERAL_SUFFIX", 1, 4);
/* 5402 */     fields[5] = new Field("", "CREATE_PARAMS", 1, 32);
/* 5403 */     fields[6] = new Field("", "NULLABLE", 5, 5);
/* 5404 */     fields[7] = new Field("", "CASE_SENSITIVE", 16, 3);
/* 5405 */     fields[8] = new Field("", "SEARCHABLE", 5, 3);
/* 5406 */     fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", 16, 3);
/* 5407 */     fields[10] = new Field("", "FIXED_PREC_SCALE", 16, 3);
/* 5408 */     fields[11] = new Field("", "AUTO_INCREMENT", 16, 3);
/* 5409 */     fields[12] = new Field("", "LOCAL_TYPE_NAME", 1, 32);
/* 5410 */     fields[13] = new Field("", "MINIMUM_SCALE", 5, 5);
/* 5411 */     fields[14] = new Field("", "MAXIMUM_SCALE", 5, 5);
/* 5412 */     fields[15] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 5413 */     fields[16] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 5414 */     fields[17] = new Field("", "NUM_PREC_RADIX", 4, 10);
/*      */ 
/* 5416 */     byte[][] rowVal = (byte[][])null;
/* 5417 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 5426 */     rowVal = new byte[18][];
/* 5427 */     rowVal[0] = s2b("BIT");
/* 5428 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/* 5431 */     rowVal[2] = s2b("1");
/* 5432 */     rowVal[3] = s2b("");
/* 5433 */     rowVal[4] = s2b("");
/* 5434 */     rowVal[5] = s2b("");
/* 5435 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5439 */     rowVal[7] = s2b("true");
/* 5440 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5444 */     rowVal[9] = s2b("false");
/* 5445 */     rowVal[10] = s2b("false");
/* 5446 */     rowVal[11] = s2b("false");
/* 5447 */     rowVal[12] = s2b("BIT");
/* 5448 */     rowVal[13] = s2b("0");
/* 5449 */     rowVal[14] = s2b("0");
/* 5450 */     rowVal[15] = s2b("0");
/* 5451 */     rowVal[16] = s2b("0");
/* 5452 */     rowVal[17] = s2b("10");
/* 5453 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5458 */     rowVal = new byte[18][];
/* 5459 */     rowVal[0] = s2b("BOOL");
/* 5460 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/* 5463 */     rowVal[2] = s2b("1");
/* 5464 */     rowVal[3] = s2b("");
/* 5465 */     rowVal[4] = s2b("");
/* 5466 */     rowVal[5] = s2b("");
/* 5467 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5471 */     rowVal[7] = s2b("true");
/* 5472 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5476 */     rowVal[9] = s2b("false");
/* 5477 */     rowVal[10] = s2b("false");
/* 5478 */     rowVal[11] = s2b("false");
/* 5479 */     rowVal[12] = s2b("BOOL");
/* 5480 */     rowVal[13] = s2b("0");
/* 5481 */     rowVal[14] = s2b("0");
/* 5482 */     rowVal[15] = s2b("0");
/* 5483 */     rowVal[16] = s2b("0");
/* 5484 */     rowVal[17] = s2b("10");
/* 5485 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5490 */     rowVal = new byte[18][];
/* 5491 */     rowVal[0] = s2b("TINYINT");
/* 5492 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/* 5495 */     rowVal[2] = s2b("3");
/* 5496 */     rowVal[3] = s2b("");
/* 5497 */     rowVal[4] = s2b("");
/* 5498 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5499 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5503 */     rowVal[7] = s2b("false");
/* 5504 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5508 */     rowVal[9] = s2b("true");
/* 5509 */     rowVal[10] = s2b("false");
/* 5510 */     rowVal[11] = s2b("true");
/* 5511 */     rowVal[12] = s2b("TINYINT");
/* 5512 */     rowVal[13] = s2b("0");
/* 5513 */     rowVal[14] = s2b("0");
/* 5514 */     rowVal[15] = s2b("0");
/* 5515 */     rowVal[16] = s2b("0");
/* 5516 */     rowVal[17] = s2b("10");
/* 5517 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5519 */     rowVal = new byte[18][];
/* 5520 */     rowVal[0] = s2b("TINYINT UNSIGNED");
/* 5521 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/* 5524 */     rowVal[2] = s2b("3");
/* 5525 */     rowVal[3] = s2b("");
/* 5526 */     rowVal[4] = s2b("");
/* 5527 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5528 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5532 */     rowVal[7] = s2b("false");
/* 5533 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5537 */     rowVal[9] = s2b("true");
/* 5538 */     rowVal[10] = s2b("false");
/* 5539 */     rowVal[11] = s2b("true");
/* 5540 */     rowVal[12] = s2b("TINYINT UNSIGNED");
/* 5541 */     rowVal[13] = s2b("0");
/* 5542 */     rowVal[14] = s2b("0");
/* 5543 */     rowVal[15] = s2b("0");
/* 5544 */     rowVal[16] = s2b("0");
/* 5545 */     rowVal[17] = s2b("10");
/* 5546 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5551 */     rowVal = new byte[18][];
/* 5552 */     rowVal[0] = s2b("BIGINT");
/* 5553 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/* 5556 */     rowVal[2] = s2b("19");
/* 5557 */     rowVal[3] = s2b("");
/* 5558 */     rowVal[4] = s2b("");
/* 5559 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5560 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5564 */     rowVal[7] = s2b("false");
/* 5565 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5569 */     rowVal[9] = s2b("true");
/* 5570 */     rowVal[10] = s2b("false");
/* 5571 */     rowVal[11] = s2b("true");
/* 5572 */     rowVal[12] = s2b("BIGINT");
/* 5573 */     rowVal[13] = s2b("0");
/* 5574 */     rowVal[14] = s2b("0");
/* 5575 */     rowVal[15] = s2b("0");
/* 5576 */     rowVal[16] = s2b("0");
/* 5577 */     rowVal[17] = s2b("10");
/* 5578 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5580 */     rowVal = new byte[18][];
/* 5581 */     rowVal[0] = s2b("BIGINT UNSIGNED");
/* 5582 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/* 5585 */     rowVal[2] = s2b("20");
/* 5586 */     rowVal[3] = s2b("");
/* 5587 */     rowVal[4] = s2b("");
/* 5588 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5589 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5593 */     rowVal[7] = s2b("false");
/* 5594 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5598 */     rowVal[9] = s2b("true");
/* 5599 */     rowVal[10] = s2b("false");
/* 5600 */     rowVal[11] = s2b("true");
/* 5601 */     rowVal[12] = s2b("BIGINT UNSIGNED");
/* 5602 */     rowVal[13] = s2b("0");
/* 5603 */     rowVal[14] = s2b("0");
/* 5604 */     rowVal[15] = s2b("0");
/* 5605 */     rowVal[16] = s2b("0");
/* 5606 */     rowVal[17] = s2b("10");
/* 5607 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5612 */     rowVal = new byte[18][];
/* 5613 */     rowVal[0] = s2b("LONG VARBINARY");
/* 5614 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5617 */     rowVal[2] = s2b("16777215");
/* 5618 */     rowVal[3] = s2b("'");
/* 5619 */     rowVal[4] = s2b("'");
/* 5620 */     rowVal[5] = s2b("");
/* 5621 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5625 */     rowVal[7] = s2b("true");
/* 5626 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5630 */     rowVal[9] = s2b("false");
/* 5631 */     rowVal[10] = s2b("false");
/* 5632 */     rowVal[11] = s2b("false");
/* 5633 */     rowVal[12] = s2b("LONG VARBINARY");
/* 5634 */     rowVal[13] = s2b("0");
/* 5635 */     rowVal[14] = s2b("0");
/* 5636 */     rowVal[15] = s2b("0");
/* 5637 */     rowVal[16] = s2b("0");
/* 5638 */     rowVal[17] = s2b("10");
/* 5639 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5644 */     rowVal = new byte[18][];
/* 5645 */     rowVal[0] = s2b("MEDIUMBLOB");
/* 5646 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5649 */     rowVal[2] = s2b("16777215");
/* 5650 */     rowVal[3] = s2b("'");
/* 5651 */     rowVal[4] = s2b("'");
/* 5652 */     rowVal[5] = s2b("");
/* 5653 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5657 */     rowVal[7] = s2b("true");
/* 5658 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5662 */     rowVal[9] = s2b("false");
/* 5663 */     rowVal[10] = s2b("false");
/* 5664 */     rowVal[11] = s2b("false");
/* 5665 */     rowVal[12] = s2b("MEDIUMBLOB");
/* 5666 */     rowVal[13] = s2b("0");
/* 5667 */     rowVal[14] = s2b("0");
/* 5668 */     rowVal[15] = s2b("0");
/* 5669 */     rowVal[16] = s2b("0");
/* 5670 */     rowVal[17] = s2b("10");
/* 5671 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5676 */     rowVal = new byte[18][];
/* 5677 */     rowVal[0] = s2b("LONGBLOB");
/* 5678 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5681 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/* 5684 */     rowVal[3] = s2b("'");
/* 5685 */     rowVal[4] = s2b("'");
/* 5686 */     rowVal[5] = s2b("");
/* 5687 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5691 */     rowVal[7] = s2b("true");
/* 5692 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5696 */     rowVal[9] = s2b("false");
/* 5697 */     rowVal[10] = s2b("false");
/* 5698 */     rowVal[11] = s2b("false");
/* 5699 */     rowVal[12] = s2b("LONGBLOB");
/* 5700 */     rowVal[13] = s2b("0");
/* 5701 */     rowVal[14] = s2b("0");
/* 5702 */     rowVal[15] = s2b("0");
/* 5703 */     rowVal[16] = s2b("0");
/* 5704 */     rowVal[17] = s2b("10");
/* 5705 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5710 */     rowVal = new byte[18][];
/* 5711 */     rowVal[0] = s2b("BLOB");
/* 5712 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5715 */     rowVal[2] = s2b("65535");
/* 5716 */     rowVal[3] = s2b("'");
/* 5717 */     rowVal[4] = s2b("'");
/* 5718 */     rowVal[5] = s2b("");
/* 5719 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5723 */     rowVal[7] = s2b("true");
/* 5724 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5728 */     rowVal[9] = s2b("false");
/* 5729 */     rowVal[10] = s2b("false");
/* 5730 */     rowVal[11] = s2b("false");
/* 5731 */     rowVal[12] = s2b("BLOB");
/* 5732 */     rowVal[13] = s2b("0");
/* 5733 */     rowVal[14] = s2b("0");
/* 5734 */     rowVal[15] = s2b("0");
/* 5735 */     rowVal[16] = s2b("0");
/* 5736 */     rowVal[17] = s2b("10");
/* 5737 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5742 */     rowVal = new byte[18][];
/* 5743 */     rowVal[0] = s2b("TINYBLOB");
/* 5744 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5747 */     rowVal[2] = s2b("255");
/* 5748 */     rowVal[3] = s2b("'");
/* 5749 */     rowVal[4] = s2b("'");
/* 5750 */     rowVal[5] = s2b("");
/* 5751 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5755 */     rowVal[7] = s2b("true");
/* 5756 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5760 */     rowVal[9] = s2b("false");
/* 5761 */     rowVal[10] = s2b("false");
/* 5762 */     rowVal[11] = s2b("false");
/* 5763 */     rowVal[12] = s2b("TINYBLOB");
/* 5764 */     rowVal[13] = s2b("0");
/* 5765 */     rowVal[14] = s2b("0");
/* 5766 */     rowVal[15] = s2b("0");
/* 5767 */     rowVal[16] = s2b("0");
/* 5768 */     rowVal[17] = s2b("10");
/* 5769 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5775 */     rowVal = new byte[18][];
/* 5776 */     rowVal[0] = s2b("VARBINARY");
/* 5777 */     rowVal[1] = Integer.toString(-3).getBytes();
/*      */ 
/* 5780 */     rowVal[2] = s2b("255");
/* 5781 */     rowVal[3] = s2b("'");
/* 5782 */     rowVal[4] = s2b("'");
/* 5783 */     rowVal[5] = s2b("(M)");
/* 5784 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5788 */     rowVal[7] = s2b("true");
/* 5789 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5793 */     rowVal[9] = s2b("false");
/* 5794 */     rowVal[10] = s2b("false");
/* 5795 */     rowVal[11] = s2b("false");
/* 5796 */     rowVal[12] = s2b("VARBINARY");
/* 5797 */     rowVal[13] = s2b("0");
/* 5798 */     rowVal[14] = s2b("0");
/* 5799 */     rowVal[15] = s2b("0");
/* 5800 */     rowVal[16] = s2b("0");
/* 5801 */     rowVal[17] = s2b("10");
/* 5802 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5808 */     rowVal = new byte[18][];
/* 5809 */     rowVal[0] = s2b("BINARY");
/* 5810 */     rowVal[1] = Integer.toString(-2).getBytes();
/*      */ 
/* 5813 */     rowVal[2] = s2b("255");
/* 5814 */     rowVal[3] = s2b("'");
/* 5815 */     rowVal[4] = s2b("'");
/* 5816 */     rowVal[5] = s2b("(M)");
/* 5817 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5821 */     rowVal[7] = s2b("true");
/* 5822 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5826 */     rowVal[9] = s2b("false");
/* 5827 */     rowVal[10] = s2b("false");
/* 5828 */     rowVal[11] = s2b("false");
/* 5829 */     rowVal[12] = s2b("BINARY");
/* 5830 */     rowVal[13] = s2b("0");
/* 5831 */     rowVal[14] = s2b("0");
/* 5832 */     rowVal[15] = s2b("0");
/* 5833 */     rowVal[16] = s2b("0");
/* 5834 */     rowVal[17] = s2b("10");
/* 5835 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5840 */     rowVal = new byte[18][];
/* 5841 */     rowVal[0] = s2b("LONG VARCHAR");
/* 5842 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5845 */     rowVal[2] = s2b("16777215");
/* 5846 */     rowVal[3] = s2b("'");
/* 5847 */     rowVal[4] = s2b("'");
/* 5848 */     rowVal[5] = s2b("");
/* 5849 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5853 */     rowVal[7] = s2b("false");
/* 5854 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5858 */     rowVal[9] = s2b("false");
/* 5859 */     rowVal[10] = s2b("false");
/* 5860 */     rowVal[11] = s2b("false");
/* 5861 */     rowVal[12] = s2b("LONG VARCHAR");
/* 5862 */     rowVal[13] = s2b("0");
/* 5863 */     rowVal[14] = s2b("0");
/* 5864 */     rowVal[15] = s2b("0");
/* 5865 */     rowVal[16] = s2b("0");
/* 5866 */     rowVal[17] = s2b("10");
/* 5867 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5872 */     rowVal = new byte[18][];
/* 5873 */     rowVal[0] = s2b("MEDIUMTEXT");
/* 5874 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5877 */     rowVal[2] = s2b("16777215");
/* 5878 */     rowVal[3] = s2b("'");
/* 5879 */     rowVal[4] = s2b("'");
/* 5880 */     rowVal[5] = s2b("");
/* 5881 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5885 */     rowVal[7] = s2b("false");
/* 5886 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5890 */     rowVal[9] = s2b("false");
/* 5891 */     rowVal[10] = s2b("false");
/* 5892 */     rowVal[11] = s2b("false");
/* 5893 */     rowVal[12] = s2b("MEDIUMTEXT");
/* 5894 */     rowVal[13] = s2b("0");
/* 5895 */     rowVal[14] = s2b("0");
/* 5896 */     rowVal[15] = s2b("0");
/* 5897 */     rowVal[16] = s2b("0");
/* 5898 */     rowVal[17] = s2b("10");
/* 5899 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5904 */     rowVal = new byte[18][];
/* 5905 */     rowVal[0] = s2b("LONGTEXT");
/* 5906 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5909 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/* 5912 */     rowVal[3] = s2b("'");
/* 5913 */     rowVal[4] = s2b("'");
/* 5914 */     rowVal[5] = s2b("");
/* 5915 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5919 */     rowVal[7] = s2b("false");
/* 5920 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5924 */     rowVal[9] = s2b("false");
/* 5925 */     rowVal[10] = s2b("false");
/* 5926 */     rowVal[11] = s2b("false");
/* 5927 */     rowVal[12] = s2b("LONGTEXT");
/* 5928 */     rowVal[13] = s2b("0");
/* 5929 */     rowVal[14] = s2b("0");
/* 5930 */     rowVal[15] = s2b("0");
/* 5931 */     rowVal[16] = s2b("0");
/* 5932 */     rowVal[17] = s2b("10");
/* 5933 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5938 */     rowVal = new byte[18][];
/* 5939 */     rowVal[0] = s2b("TEXT");
/* 5940 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5943 */     rowVal[2] = s2b("65535");
/* 5944 */     rowVal[3] = s2b("'");
/* 5945 */     rowVal[4] = s2b("'");
/* 5946 */     rowVal[5] = s2b("");
/* 5947 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5951 */     rowVal[7] = s2b("false");
/* 5952 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5956 */     rowVal[9] = s2b("false");
/* 5957 */     rowVal[10] = s2b("false");
/* 5958 */     rowVal[11] = s2b("false");
/* 5959 */     rowVal[12] = s2b("TEXT");
/* 5960 */     rowVal[13] = s2b("0");
/* 5961 */     rowVal[14] = s2b("0");
/* 5962 */     rowVal[15] = s2b("0");
/* 5963 */     rowVal[16] = s2b("0");
/* 5964 */     rowVal[17] = s2b("10");
/* 5965 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 5970 */     rowVal = new byte[18][];
/* 5971 */     rowVal[0] = s2b("TINYTEXT");
/* 5972 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5975 */     rowVal[2] = s2b("255");
/* 5976 */     rowVal[3] = s2b("'");
/* 5977 */     rowVal[4] = s2b("'");
/* 5978 */     rowVal[5] = s2b("");
/* 5979 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5983 */     rowVal[7] = s2b("false");
/* 5984 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5988 */     rowVal[9] = s2b("false");
/* 5989 */     rowVal[10] = s2b("false");
/* 5990 */     rowVal[11] = s2b("false");
/* 5991 */     rowVal[12] = s2b("TINYTEXT");
/* 5992 */     rowVal[13] = s2b("0");
/* 5993 */     rowVal[14] = s2b("0");
/* 5994 */     rowVal[15] = s2b("0");
/* 5995 */     rowVal[16] = s2b("0");
/* 5996 */     rowVal[17] = s2b("10");
/* 5997 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6002 */     rowVal = new byte[18][];
/* 6003 */     rowVal[0] = s2b("CHAR");
/* 6004 */     rowVal[1] = Integer.toString(1).getBytes();
/*      */ 
/* 6007 */     rowVal[2] = s2b("255");
/* 6008 */     rowVal[3] = s2b("'");
/* 6009 */     rowVal[4] = s2b("'");
/* 6010 */     rowVal[5] = s2b("(M)");
/* 6011 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6015 */     rowVal[7] = s2b("false");
/* 6016 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6020 */     rowVal[9] = s2b("false");
/* 6021 */     rowVal[10] = s2b("false");
/* 6022 */     rowVal[11] = s2b("false");
/* 6023 */     rowVal[12] = s2b("CHAR");
/* 6024 */     rowVal[13] = s2b("0");
/* 6025 */     rowVal[14] = s2b("0");
/* 6026 */     rowVal[15] = s2b("0");
/* 6027 */     rowVal[16] = s2b("0");
/* 6028 */     rowVal[17] = s2b("10");
/* 6029 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6033 */     int decimalPrecision = 254;
/*      */ 
/* 6035 */     if (this.conn.versionMeetsMinimum(5, 0, 3)) {
/* 6036 */       if (this.conn.versionMeetsMinimum(5, 0, 6))
/* 6037 */         decimalPrecision = 65;
/*      */       else {
/* 6039 */         decimalPrecision = 64;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 6047 */     rowVal = new byte[18][];
/* 6048 */     rowVal[0] = s2b("NUMERIC");
/* 6049 */     rowVal[1] = Integer.toString(2).getBytes();
/*      */ 
/* 6052 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 6053 */     rowVal[3] = s2b("");
/* 6054 */     rowVal[4] = s2b("");
/* 6055 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 6056 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6060 */     rowVal[7] = s2b("false");
/* 6061 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6065 */     rowVal[9] = s2b("false");
/* 6066 */     rowVal[10] = s2b("false");
/* 6067 */     rowVal[11] = s2b("true");
/* 6068 */     rowVal[12] = s2b("NUMERIC");
/* 6069 */     rowVal[13] = s2b("-308");
/* 6070 */     rowVal[14] = s2b("308");
/* 6071 */     rowVal[15] = s2b("0");
/* 6072 */     rowVal[16] = s2b("0");
/* 6073 */     rowVal[17] = s2b("10");
/* 6074 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6079 */     rowVal = new byte[18][];
/* 6080 */     rowVal[0] = s2b("DECIMAL");
/* 6081 */     rowVal[1] = Integer.toString(3).getBytes();
/*      */ 
/* 6084 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 6085 */     rowVal[3] = s2b("");
/* 6086 */     rowVal[4] = s2b("");
/* 6087 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 6088 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6092 */     rowVal[7] = s2b("false");
/* 6093 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6097 */     rowVal[9] = s2b("false");
/* 6098 */     rowVal[10] = s2b("false");
/* 6099 */     rowVal[11] = s2b("true");
/* 6100 */     rowVal[12] = s2b("DECIMAL");
/* 6101 */     rowVal[13] = s2b("-308");
/* 6102 */     rowVal[14] = s2b("308");
/* 6103 */     rowVal[15] = s2b("0");
/* 6104 */     rowVal[16] = s2b("0");
/* 6105 */     rowVal[17] = s2b("10");
/* 6106 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6111 */     rowVal = new byte[18][];
/* 6112 */     rowVal[0] = s2b("INTEGER");
/* 6113 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6116 */     rowVal[2] = s2b("10");
/* 6117 */     rowVal[3] = s2b("");
/* 6118 */     rowVal[4] = s2b("");
/* 6119 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6120 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6124 */     rowVal[7] = s2b("false");
/* 6125 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6129 */     rowVal[9] = s2b("true");
/* 6130 */     rowVal[10] = s2b("false");
/* 6131 */     rowVal[11] = s2b("true");
/* 6132 */     rowVal[12] = s2b("INTEGER");
/* 6133 */     rowVal[13] = s2b("0");
/* 6134 */     rowVal[14] = s2b("0");
/* 6135 */     rowVal[15] = s2b("0");
/* 6136 */     rowVal[16] = s2b("0");
/* 6137 */     rowVal[17] = s2b("10");
/* 6138 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6140 */     rowVal = new byte[18][];
/* 6141 */     rowVal[0] = s2b("INTEGER UNSIGNED");
/* 6142 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6145 */     rowVal[2] = s2b("10");
/* 6146 */     rowVal[3] = s2b("");
/* 6147 */     rowVal[4] = s2b("");
/* 6148 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6149 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6153 */     rowVal[7] = s2b("false");
/* 6154 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6158 */     rowVal[9] = s2b("true");
/* 6159 */     rowVal[10] = s2b("false");
/* 6160 */     rowVal[11] = s2b("true");
/* 6161 */     rowVal[12] = s2b("INTEGER UNSIGNED");
/* 6162 */     rowVal[13] = s2b("0");
/* 6163 */     rowVal[14] = s2b("0");
/* 6164 */     rowVal[15] = s2b("0");
/* 6165 */     rowVal[16] = s2b("0");
/* 6166 */     rowVal[17] = s2b("10");
/* 6167 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6172 */     rowVal = new byte[18][];
/* 6173 */     rowVal[0] = s2b("INT");
/* 6174 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6177 */     rowVal[2] = s2b("10");
/* 6178 */     rowVal[3] = s2b("");
/* 6179 */     rowVal[4] = s2b("");
/* 6180 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6181 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6185 */     rowVal[7] = s2b("false");
/* 6186 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6190 */     rowVal[9] = s2b("true");
/* 6191 */     rowVal[10] = s2b("false");
/* 6192 */     rowVal[11] = s2b("true");
/* 6193 */     rowVal[12] = s2b("INT");
/* 6194 */     rowVal[13] = s2b("0");
/* 6195 */     rowVal[14] = s2b("0");
/* 6196 */     rowVal[15] = s2b("0");
/* 6197 */     rowVal[16] = s2b("0");
/* 6198 */     rowVal[17] = s2b("10");
/* 6199 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6201 */     rowVal = new byte[18][];
/* 6202 */     rowVal[0] = s2b("INT UNSIGNED");
/* 6203 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6206 */     rowVal[2] = s2b("10");
/* 6207 */     rowVal[3] = s2b("");
/* 6208 */     rowVal[4] = s2b("");
/* 6209 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6210 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6214 */     rowVal[7] = s2b("false");
/* 6215 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6219 */     rowVal[9] = s2b("true");
/* 6220 */     rowVal[10] = s2b("false");
/* 6221 */     rowVal[11] = s2b("true");
/* 6222 */     rowVal[12] = s2b("INT UNSIGNED");
/* 6223 */     rowVal[13] = s2b("0");
/* 6224 */     rowVal[14] = s2b("0");
/* 6225 */     rowVal[15] = s2b("0");
/* 6226 */     rowVal[16] = s2b("0");
/* 6227 */     rowVal[17] = s2b("10");
/* 6228 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6233 */     rowVal = new byte[18][];
/* 6234 */     rowVal[0] = s2b("MEDIUMINT");
/* 6235 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6238 */     rowVal[2] = s2b("7");
/* 6239 */     rowVal[3] = s2b("");
/* 6240 */     rowVal[4] = s2b("");
/* 6241 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6242 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6246 */     rowVal[7] = s2b("false");
/* 6247 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6251 */     rowVal[9] = s2b("true");
/* 6252 */     rowVal[10] = s2b("false");
/* 6253 */     rowVal[11] = s2b("true");
/* 6254 */     rowVal[12] = s2b("MEDIUMINT");
/* 6255 */     rowVal[13] = s2b("0");
/* 6256 */     rowVal[14] = s2b("0");
/* 6257 */     rowVal[15] = s2b("0");
/* 6258 */     rowVal[16] = s2b("0");
/* 6259 */     rowVal[17] = s2b("10");
/* 6260 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6262 */     rowVal = new byte[18][];
/* 6263 */     rowVal[0] = s2b("MEDIUMINT UNSIGNED");
/* 6264 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 6267 */     rowVal[2] = s2b("8");
/* 6268 */     rowVal[3] = s2b("");
/* 6269 */     rowVal[4] = s2b("");
/* 6270 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6271 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6275 */     rowVal[7] = s2b("false");
/* 6276 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6280 */     rowVal[9] = s2b("true");
/* 6281 */     rowVal[10] = s2b("false");
/* 6282 */     rowVal[11] = s2b("true");
/* 6283 */     rowVal[12] = s2b("MEDIUMINT UNSIGNED");
/* 6284 */     rowVal[13] = s2b("0");
/* 6285 */     rowVal[14] = s2b("0");
/* 6286 */     rowVal[15] = s2b("0");
/* 6287 */     rowVal[16] = s2b("0");
/* 6288 */     rowVal[17] = s2b("10");
/* 6289 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6294 */     rowVal = new byte[18][];
/* 6295 */     rowVal[0] = s2b("SMALLINT");
/* 6296 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/* 6299 */     rowVal[2] = s2b("5");
/* 6300 */     rowVal[3] = s2b("");
/* 6301 */     rowVal[4] = s2b("");
/* 6302 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 6303 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6307 */     rowVal[7] = s2b("false");
/* 6308 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6312 */     rowVal[9] = s2b("true");
/* 6313 */     rowVal[10] = s2b("false");
/* 6314 */     rowVal[11] = s2b("true");
/* 6315 */     rowVal[12] = s2b("SMALLINT");
/* 6316 */     rowVal[13] = s2b("0");
/* 6317 */     rowVal[14] = s2b("0");
/* 6318 */     rowVal[15] = s2b("0");
/* 6319 */     rowVal[16] = s2b("0");
/* 6320 */     rowVal[17] = s2b("10");
/* 6321 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6323 */     rowVal = new byte[18][];
/* 6324 */     rowVal[0] = s2b("SMALLINT UNSIGNED");
/* 6325 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/* 6328 */     rowVal[2] = s2b("5");
/* 6329 */     rowVal[3] = s2b("");
/* 6330 */     rowVal[4] = s2b("");
/* 6331 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 6332 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6336 */     rowVal[7] = s2b("false");
/* 6337 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6341 */     rowVal[9] = s2b("true");
/* 6342 */     rowVal[10] = s2b("false");
/* 6343 */     rowVal[11] = s2b("true");
/* 6344 */     rowVal[12] = s2b("SMALLINT UNSIGNED");
/* 6345 */     rowVal[13] = s2b("0");
/* 6346 */     rowVal[14] = s2b("0");
/* 6347 */     rowVal[15] = s2b("0");
/* 6348 */     rowVal[16] = s2b("0");
/* 6349 */     rowVal[17] = s2b("10");
/* 6350 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6356 */     rowVal = new byte[18][];
/* 6357 */     rowVal[0] = s2b("FLOAT");
/* 6358 */     rowVal[1] = Integer.toString(7).getBytes();
/*      */ 
/* 6361 */     rowVal[2] = s2b("10");
/* 6362 */     rowVal[3] = s2b("");
/* 6363 */     rowVal[4] = s2b("");
/* 6364 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6365 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6369 */     rowVal[7] = s2b("false");
/* 6370 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6374 */     rowVal[9] = s2b("false");
/* 6375 */     rowVal[10] = s2b("false");
/* 6376 */     rowVal[11] = s2b("true");
/* 6377 */     rowVal[12] = s2b("FLOAT");
/* 6378 */     rowVal[13] = s2b("-38");
/* 6379 */     rowVal[14] = s2b("38");
/* 6380 */     rowVal[15] = s2b("0");
/* 6381 */     rowVal[16] = s2b("0");
/* 6382 */     rowVal[17] = s2b("10");
/* 6383 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6388 */     rowVal = new byte[18][];
/* 6389 */     rowVal[0] = s2b("DOUBLE");
/* 6390 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 6393 */     rowVal[2] = s2b("17");
/* 6394 */     rowVal[3] = s2b("");
/* 6395 */     rowVal[4] = s2b("");
/* 6396 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6397 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6401 */     rowVal[7] = s2b("false");
/* 6402 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6406 */     rowVal[9] = s2b("false");
/* 6407 */     rowVal[10] = s2b("false");
/* 6408 */     rowVal[11] = s2b("true");
/* 6409 */     rowVal[12] = s2b("DOUBLE");
/* 6410 */     rowVal[13] = s2b("-308");
/* 6411 */     rowVal[14] = s2b("308");
/* 6412 */     rowVal[15] = s2b("0");
/* 6413 */     rowVal[16] = s2b("0");
/* 6414 */     rowVal[17] = s2b("10");
/* 6415 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6420 */     rowVal = new byte[18][];
/* 6421 */     rowVal[0] = s2b("DOUBLE PRECISION");
/* 6422 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 6425 */     rowVal[2] = s2b("17");
/* 6426 */     rowVal[3] = s2b("");
/* 6427 */     rowVal[4] = s2b("");
/* 6428 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6429 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6433 */     rowVal[7] = s2b("false");
/* 6434 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6438 */     rowVal[9] = s2b("false");
/* 6439 */     rowVal[10] = s2b("false");
/* 6440 */     rowVal[11] = s2b("true");
/* 6441 */     rowVal[12] = s2b("DOUBLE PRECISION");
/* 6442 */     rowVal[13] = s2b("-308");
/* 6443 */     rowVal[14] = s2b("308");
/* 6444 */     rowVal[15] = s2b("0");
/* 6445 */     rowVal[16] = s2b("0");
/* 6446 */     rowVal[17] = s2b("10");
/* 6447 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6452 */     rowVal = new byte[18][];
/* 6453 */     rowVal[0] = s2b("REAL");
/* 6454 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 6457 */     rowVal[2] = s2b("17");
/* 6458 */     rowVal[3] = s2b("");
/* 6459 */     rowVal[4] = s2b("");
/* 6460 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 6461 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6465 */     rowVal[7] = s2b("false");
/* 6466 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6470 */     rowVal[9] = s2b("false");
/* 6471 */     rowVal[10] = s2b("false");
/* 6472 */     rowVal[11] = s2b("true");
/* 6473 */     rowVal[12] = s2b("REAL");
/* 6474 */     rowVal[13] = s2b("-308");
/* 6475 */     rowVal[14] = s2b("308");
/* 6476 */     rowVal[15] = s2b("0");
/* 6477 */     rowVal[16] = s2b("0");
/* 6478 */     rowVal[17] = s2b("10");
/* 6479 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6484 */     rowVal = new byte[18][];
/* 6485 */     rowVal[0] = s2b("VARCHAR");
/* 6486 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 6489 */     rowVal[2] = s2b("255");
/* 6490 */     rowVal[3] = s2b("'");
/* 6491 */     rowVal[4] = s2b("'");
/* 6492 */     rowVal[5] = s2b("(M)");
/* 6493 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6497 */     rowVal[7] = s2b("false");
/* 6498 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6502 */     rowVal[9] = s2b("false");
/* 6503 */     rowVal[10] = s2b("false");
/* 6504 */     rowVal[11] = s2b("false");
/* 6505 */     rowVal[12] = s2b("VARCHAR");
/* 6506 */     rowVal[13] = s2b("0");
/* 6507 */     rowVal[14] = s2b("0");
/* 6508 */     rowVal[15] = s2b("0");
/* 6509 */     rowVal[16] = s2b("0");
/* 6510 */     rowVal[17] = s2b("10");
/* 6511 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6516 */     rowVal = new byte[18][];
/* 6517 */     rowVal[0] = s2b("ENUM");
/* 6518 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 6521 */     rowVal[2] = s2b("65535");
/* 6522 */     rowVal[3] = s2b("'");
/* 6523 */     rowVal[4] = s2b("'");
/* 6524 */     rowVal[5] = s2b("");
/* 6525 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6529 */     rowVal[7] = s2b("false");
/* 6530 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6534 */     rowVal[9] = s2b("false");
/* 6535 */     rowVal[10] = s2b("false");
/* 6536 */     rowVal[11] = s2b("false");
/* 6537 */     rowVal[12] = s2b("ENUM");
/* 6538 */     rowVal[13] = s2b("0");
/* 6539 */     rowVal[14] = s2b("0");
/* 6540 */     rowVal[15] = s2b("0");
/* 6541 */     rowVal[16] = s2b("0");
/* 6542 */     rowVal[17] = s2b("10");
/* 6543 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6548 */     rowVal = new byte[18][];
/* 6549 */     rowVal[0] = s2b("SET");
/* 6550 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 6553 */     rowVal[2] = s2b("64");
/* 6554 */     rowVal[3] = s2b("'");
/* 6555 */     rowVal[4] = s2b("'");
/* 6556 */     rowVal[5] = s2b("");
/* 6557 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6561 */     rowVal[7] = s2b("false");
/* 6562 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6566 */     rowVal[9] = s2b("false");
/* 6567 */     rowVal[10] = s2b("false");
/* 6568 */     rowVal[11] = s2b("false");
/* 6569 */     rowVal[12] = s2b("SET");
/* 6570 */     rowVal[13] = s2b("0");
/* 6571 */     rowVal[14] = s2b("0");
/* 6572 */     rowVal[15] = s2b("0");
/* 6573 */     rowVal[16] = s2b("0");
/* 6574 */     rowVal[17] = s2b("10");
/* 6575 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6580 */     rowVal = new byte[18][];
/* 6581 */     rowVal[0] = s2b("DATE");
/* 6582 */     rowVal[1] = Integer.toString(91).getBytes();
/*      */ 
/* 6585 */     rowVal[2] = s2b("0");
/* 6586 */     rowVal[3] = s2b("'");
/* 6587 */     rowVal[4] = s2b("'");
/* 6588 */     rowVal[5] = s2b("");
/* 6589 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6593 */     rowVal[7] = s2b("false");
/* 6594 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6598 */     rowVal[9] = s2b("false");
/* 6599 */     rowVal[10] = s2b("false");
/* 6600 */     rowVal[11] = s2b("false");
/* 6601 */     rowVal[12] = s2b("DATE");
/* 6602 */     rowVal[13] = s2b("0");
/* 6603 */     rowVal[14] = s2b("0");
/* 6604 */     rowVal[15] = s2b("0");
/* 6605 */     rowVal[16] = s2b("0");
/* 6606 */     rowVal[17] = s2b("10");
/* 6607 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6612 */     rowVal = new byte[18][];
/* 6613 */     rowVal[0] = s2b("TIME");
/* 6614 */     rowVal[1] = Integer.toString(92).getBytes();
/*      */ 
/* 6617 */     rowVal[2] = s2b("0");
/* 6618 */     rowVal[3] = s2b("'");
/* 6619 */     rowVal[4] = s2b("'");
/* 6620 */     rowVal[5] = s2b("");
/* 6621 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6625 */     rowVal[7] = s2b("false");
/* 6626 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6630 */     rowVal[9] = s2b("false");
/* 6631 */     rowVal[10] = s2b("false");
/* 6632 */     rowVal[11] = s2b("false");
/* 6633 */     rowVal[12] = s2b("TIME");
/* 6634 */     rowVal[13] = s2b("0");
/* 6635 */     rowVal[14] = s2b("0");
/* 6636 */     rowVal[15] = s2b("0");
/* 6637 */     rowVal[16] = s2b("0");
/* 6638 */     rowVal[17] = s2b("10");
/* 6639 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6644 */     rowVal = new byte[18][];
/* 6645 */     rowVal[0] = s2b("DATETIME");
/* 6646 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/* 6649 */     rowVal[2] = s2b("0");
/* 6650 */     rowVal[3] = s2b("'");
/* 6651 */     rowVal[4] = s2b("'");
/* 6652 */     rowVal[5] = s2b("");
/* 6653 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6657 */     rowVal[7] = s2b("false");
/* 6658 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6662 */     rowVal[9] = s2b("false");
/* 6663 */     rowVal[10] = s2b("false");
/* 6664 */     rowVal[11] = s2b("false");
/* 6665 */     rowVal[12] = s2b("DATETIME");
/* 6666 */     rowVal[13] = s2b("0");
/* 6667 */     rowVal[14] = s2b("0");
/* 6668 */     rowVal[15] = s2b("0");
/* 6669 */     rowVal[16] = s2b("0");
/* 6670 */     rowVal[17] = s2b("10");
/* 6671 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6676 */     rowVal = new byte[18][];
/* 6677 */     rowVal[0] = s2b("TIMESTAMP");
/* 6678 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/* 6681 */     rowVal[2] = s2b("0");
/* 6682 */     rowVal[3] = s2b("'");
/* 6683 */     rowVal[4] = s2b("'");
/* 6684 */     rowVal[5] = s2b("[(M)]");
/* 6685 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 6689 */     rowVal[7] = s2b("false");
/* 6690 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 6694 */     rowVal[9] = s2b("false");
/* 6695 */     rowVal[10] = s2b("false");
/* 6696 */     rowVal[11] = s2b("false");
/* 6697 */     rowVal[12] = s2b("TIMESTAMP");
/* 6698 */     rowVal[13] = s2b("0");
/* 6699 */     rowVal[14] = s2b("0");
/* 6700 */     rowVal[15] = s2b("0");
/* 6701 */     rowVal[16] = s2b("0");
/* 6702 */     rowVal[17] = s2b("10");
/* 6703 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */ 
/* 6705 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
/*      */     throws SQLException
/*      */   {
/* 6751 */     Field[] fields = new Field[6];
/* 6752 */     fields[0] = new Field("", "TYPE_CAT", 12, 32);
/* 6753 */     fields[1] = new Field("", "TYPE_SCHEM", 12, 32);
/* 6754 */     fields[2] = new Field("", "TYPE_NAME", 12, 32);
/* 6755 */     fields[3] = new Field("", "CLASS_NAME", 12, 32);
/* 6756 */     fields[4] = new Field("", "DATA_TYPE", 12, 32);
/* 6757 */     fields[5] = new Field("", "REMARKS", 12, 32);
/*      */ 
/* 6759 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 6761 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public String getURL()
/*      */     throws SQLException
/*      */   {
/* 6772 */     return this.conn.getURL();
/*      */   }
/*      */ 
/*      */   public String getUserName()
/*      */     throws SQLException
/*      */   {
/* 6783 */     if (this.conn.getUseHostsInPrivileges()) {
/* 6784 */       java.sql.Statement stmt = null;
/* 6785 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 6788 */         stmt = this.conn.createStatement();
/* 6789 */         stmt.setEscapeProcessing(false);
/*      */ 
/* 6791 */         rs = stmt.executeQuery("SELECT USER()");
/* 6792 */         rs.next();
/*      */ 
/* 6794 */         str = rs.getString(1);
/*      */       }
/*      */       finally
/*      */       {
/*      */         String str;
/* 6796 */         if (rs != null) {
/*      */           try {
/* 6798 */             rs.close();
/*      */           } catch (Exception ex) {
/* 6800 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/* 6803 */           rs = null;
/*      */         }
/*      */ 
/* 6806 */         if (stmt != null) {
/*      */           try {
/* 6808 */             stmt.close();
/*      */           } catch (Exception ex) {
/* 6810 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/* 6813 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 6818 */     return this.conn.getUser();
/*      */   }
/*      */ 
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 6858 */     if (table == null) {
/* 6859 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 6863 */     Field[] fields = new Field[8];
/* 6864 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 6865 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 6866 */     fields[2] = new Field("", "DATA_TYPE", 4, 5);
/* 6867 */     fields[3] = new Field("", "TYPE_NAME", 1, 16);
/* 6868 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 16);
/* 6869 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 16);
/* 6870 */     fields[6] = new Field("", "DECIMAL_DIGITS", 5, 16);
/* 6871 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */ 
/* 6873 */     ArrayList rows = new ArrayList();
/*      */ 
/* 6875 */     java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 6879 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows)
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 6882 */           ResultSet results = null;
/* 6883 */           boolean with_where = DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 0);
/*      */           try
/*      */           {
/* 6886 */             StringBuffer whereBuf = new StringBuffer(" Extra LIKE '%on update CURRENT_TIMESTAMP%'");
/* 6887 */             List rsFields = new ArrayList();
/*      */ 
/* 6891 */             if (!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 1, 23))
/*      */             {
/* 6893 */               whereBuf = new StringBuffer();
/* 6894 */               boolean firstTime = true;
/*      */ 
/* 6896 */               String query = "SHOW CREATE TABLE " + DatabaseMetaData.this.quotedId + catalogStr + DatabaseMetaData.this.quotedId + "." + DatabaseMetaData.this.quotedId + this.val$table + DatabaseMetaData.this.quotedId;
/*      */ 
/* 6901 */               results = this.val$stmt.executeQuery(query);
/* 6902 */               while (results.next()) {
/* 6903 */                 String createTableString = results.getString(2);
/* 6904 */                 StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/*      */ 
/* 6906 */                 while (lineTokenizer.hasMoreTokens()) {
/* 6907 */                   String line = lineTokenizer.nextToken().trim();
/* 6908 */                   if (StringUtils.indexOfIgnoreCase(line, "on update CURRENT_TIMESTAMP") > -1) {
/* 6909 */                     boolean usingBackTicks = true;
/* 6910 */                     int beginPos = line.indexOf(DatabaseMetaData.this.quotedId);
/*      */ 
/* 6912 */                     if (beginPos == -1) {
/* 6913 */                       beginPos = line.indexOf("\"");
/* 6914 */                       usingBackTicks = false;
/*      */                     }
/*      */ 
/* 6917 */                     if (beginPos != -1) {
/* 6918 */                       int endPos = -1;
/*      */ 
/* 6920 */                       if (usingBackTicks)
/* 6921 */                         endPos = line.indexOf(DatabaseMetaData.this.quotedId, beginPos + 1);
/*      */                       else {
/* 6923 */                         endPos = line.indexOf("\"", beginPos + 1);
/*      */                       }
/*      */ 
/* 6926 */                       if (endPos != -1) {
/* 6927 */                         if (with_where) {
/* 6928 */                           if (!firstTime)
/* 6929 */                             whereBuf.append(" or");
/*      */                           else {
/* 6931 */                             firstTime = false;
/*      */                           }
/* 6933 */                           whereBuf.append(" Field='");
/* 6934 */                           whereBuf.append(line.substring(beginPos + 1, endPos));
/* 6935 */                           whereBuf.append("'");
/*      */                         } else {
/* 6937 */                           rsFields.add(line.substring(beginPos + 1, endPos));
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 6946 */             if ((whereBuf.length() > 0) || (rsFields.size() > 0)) {
/* 6947 */               StringBuffer queryBuf = new StringBuffer("SHOW ");
/* 6948 */               queryBuf.append("COLUMNS FROM ");
/* 6949 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 6950 */               queryBuf.append(this.val$table);
/* 6951 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 6952 */               queryBuf.append(" FROM ");
/* 6953 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 6954 */               queryBuf.append(catalogStr);
/* 6955 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 6956 */               if (with_where) {
/* 6957 */                 queryBuf.append(" WHERE");
/* 6958 */                 queryBuf.append(whereBuf.toString());
/*      */               }
/*      */ 
/* 6961 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 6963 */               while (results.next())
/* 6964 */                 if ((with_where) || (rsFields.contains(results.getString("Field")))) {
/* 6965 */                   DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
/* 6966 */                   byte[][] rowVal = new byte[8][];
/*      */ 
/* 6968 */                   rowVal[0] = null;
/*      */ 
/* 6970 */                   rowVal[1] = results.getBytes("Field");
/*      */ 
/* 6972 */                   rowVal[2] = Short.toString(typeDesc.dataType).getBytes();
/*      */ 
/* 6974 */                   rowVal[3] = DatabaseMetaData.this.s2b(typeDesc.typeName);
/*      */ 
/* 6976 */                   rowVal[4] = (typeDesc.columnSize == null ? null : DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()));
/*      */ 
/* 6978 */                   rowVal[5] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/*      */ 
/* 6980 */                   rowVal[6] = (typeDesc.decimalDigits == null ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString()));
/*      */ 
/* 6982 */                   rowVal[7] = Integer.toString(1).getBytes();
/*      */ 
/* 6984 */                   this.val$rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 }
/*      */             }
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 6989 */             if (!"42S02".equals(sqlEx.getSQLState()))
/* 6990 */               throw sqlEx;
/*      */           }
/*      */           finally {
/* 6993 */             if (results != null) {
/*      */               try {
/* 6995 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 7000 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 6879 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 7007 */       if (stmt != null) {
/* 7008 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 7012 */     return buildResultSet(fields, rows);
/*      */   }
/*      */ 
/*      */   public boolean insertsAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 7026 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isCatalogAtStart()
/*      */     throws SQLException
/*      */   {
/* 7038 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 7049 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean locatorsUpdateCopy()
/*      */     throws SQLException
/*      */   {
/* 7056 */     return !this.conn.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public boolean nullPlusNonNullIsNull()
/*      */     throws SQLException
/*      */   {
/* 7068 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtEnd()
/*      */     throws SQLException
/*      */   {
/* 7079 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtStart()
/*      */     throws SQLException
/*      */   {
/* 7090 */     return (this.conn.versionMeetsMinimum(4, 0, 2)) && (!this.conn.versionMeetsMinimum(4, 0, 11));
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedHigh()
/*      */     throws SQLException
/*      */   {
/* 7102 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedLow()
/*      */     throws SQLException
/*      */   {
/* 7113 */     return !nullsAreSortedHigh();
/*      */   }
/*      */ 
/*      */   public boolean othersDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 7126 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean othersInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 7139 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean othersUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 7152 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 7165 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 7178 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 7191 */     return false;
/*      */   }
/*      */ 
/*      */   protected LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment)
/*      */     throws SQLException
/*      */   {
/* 7212 */     String columnsDelimitter = ",";
/*      */ 
/* 7214 */     char quoteChar = this.quotedId.length() == 0 ? '\000' : this.quotedId.charAt(0);
/*      */ 
/* 7217 */     int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysComment, "(", quoteChar, true);
/*      */ 
/* 7221 */     if (indexOfOpenParenLocalColumns == -1) {
/* 7222 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of local columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7227 */     String constraintName = removeQuotedId(keysComment.substring(0, indexOfOpenParenLocalColumns).trim());
/*      */ 
/* 7229 */     keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
/*      */ 
/* 7232 */     String keysCommentTrimmed = keysComment.trim();
/*      */ 
/* 7234 */     int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/* 7238 */     if (indexOfCloseParenLocalColumns == -1) {
/* 7239 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of local columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7244 */     String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
/*      */ 
/* 7247 */     int indexOfRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, "REFER ", this.quotedId.charAt(0), true);
/*      */ 
/* 7250 */     if (indexOfRefer == -1) {
/* 7251 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced tables list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7256 */     int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfRefer, keysCommentTrimmed, "(", quoteChar, false);
/*      */ 
/* 7260 */     if (indexOfOpenParenReferCol == -1) {
/* 7261 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7266 */     String referCatalogTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
/*      */ 
/* 7269 */     int indexOfSlash = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referCatalogTableString, "/", this.quotedId.charAt(0), false);
/*      */ 
/* 7272 */     if (indexOfSlash == -1) {
/* 7273 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find name of referenced catalog.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7278 */     String referCatalog = removeQuotedId(referCatalogTableString.substring(0, indexOfSlash));
/*      */ 
/* 7280 */     String referTable = removeQuotedId(referCatalogTableString.substring(indexOfSlash + 1).trim());
/*      */ 
/* 7283 */     int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfOpenParenReferCol, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/* 7287 */     if (indexOfCloseParenRefer == -1) {
/* 7288 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of referenced columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 7293 */     String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
/*      */ 
/* 7296 */     List referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/* 7298 */     List localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/* 7301 */     return new LocalAndReferencedColumns(localColumnsList, referColumnsList, constraintName, referCatalog, referTable);
/*      */   }
/*      */ 
/*      */   protected String removeQuotedId(String s)
/*      */   {
/* 7306 */     if (s == null) {
/* 7307 */       return null;
/*      */     }
/*      */ 
/* 7310 */     if (this.quotedId.equals("")) {
/* 7311 */       return s;
/*      */     }
/*      */ 
/* 7314 */     s = s.trim();
/*      */ 
/* 7316 */     int frontOffset = 0;
/* 7317 */     int backOffset = s.length();
/* 7318 */     int quoteLength = this.quotedId.length();
/*      */ 
/* 7320 */     if (s.startsWith(this.quotedId)) {
/* 7321 */       frontOffset = quoteLength;
/*      */     }
/*      */ 
/* 7324 */     if (s.endsWith(this.quotedId)) {
/* 7325 */       backOffset -= quoteLength;
/*      */     }
/*      */ 
/* 7328 */     return s.substring(frontOffset, backOffset);
/*      */   }
/*      */ 
/*      */   protected byte[] s2b(String s)
/*      */     throws SQLException
/*      */   {
/* 7340 */     if (s == null) {
/* 7341 */       return null;
/*      */     }
/*      */ 
/* 7344 */     return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata(), this.conn.getServerCharacterEncoding(), this.conn.parserKnowsUnicode(), this.conn, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7358 */     return this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7370 */     return this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7382 */     return !this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7393 */     return !this.conn.storesLowerCaseTableName();
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7405 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7417 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithAddColumn()
/*      */     throws SQLException
/*      */   {
/* 7428 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithDropColumn()
/*      */     throws SQLException
/*      */   {
/* 7439 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92EntryLevelSQL()
/*      */     throws SQLException
/*      */   {
/* 7451 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92FullSQL()
/*      */     throws SQLException
/*      */   {
/* 7462 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92IntermediateSQL()
/*      */     throws SQLException
/*      */   {
/* 7473 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsBatchUpdates()
/*      */     throws SQLException
/*      */   {
/* 7485 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 7497 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7509 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7521 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 7533 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7545 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsColumnAliasing()
/*      */     throws SQLException
/*      */   {
/* 7561 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert()
/*      */     throws SQLException
/*      */   {
/* 7572 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert(int fromType, int toType)
/*      */     throws SQLException
/*      */   {
/* 7589 */     switch (fromType)
/*      */     {
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 7600 */       switch (toType) {
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 12:
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/*      */       case 1111:
/* 7620 */         return true;
/*      */       }
/*      */ 
/* 7623 */       return false;
/*      */     case -7:
/* 7630 */       return false;
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 7646 */       switch (toType) {
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 12:
/* 7662 */         return true;
/*      */       case 0:
/*      */       case 9:
/*      */       case 10:
/* 7665 */       case 11: } return false;
/*      */     case 0:
/* 7670 */       return false;
/*      */     case 1111:
/* 7678 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7685 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 7688 */       case 11: } return false;
/*      */     case 91:
/* 7694 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7701 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 7704 */       case 11: } return false;
/*      */     case 92:
/* 7710 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 7717 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 7720 */       case 11: } return false;
/*      */     case 93:
/* 7729 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*      */       case 91:
/*      */       case 92:
/* 7738 */         return true;
/*      */       }
/*      */ 
/* 7741 */       return false;
/*      */     }
/*      */ 
/* 7746 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsCoreSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7758 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsCorrelatedSubqueries()
/*      */     throws SQLException
/*      */   {
/* 7770 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions()
/*      */     throws SQLException
/*      */   {
/* 7783 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsDataManipulationTransactionsOnly()
/*      */     throws SQLException
/*      */   {
/* 7795 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsDifferentTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 7808 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsExpressionsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 7819 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsExtendedSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7830 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsFullOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7841 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsGetGeneratedKeys()
/*      */   {
/* 7850 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupBy()
/*      */     throws SQLException
/*      */   {
/* 7861 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByBeyondSelect()
/*      */     throws SQLException
/*      */   {
/* 7873 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 7884 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsIntegrityEnhancementFacility()
/*      */     throws SQLException
/*      */   {
/* 7896 */     return this.conn.getOverrideSupportsIntegrityEnhancementFacility();
/*      */   }
/*      */ 
/*      */   public boolean supportsLikeEscapeClause()
/*      */     throws SQLException
/*      */   {
/* 7911 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsLimitedOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7923 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMinimumSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7935 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7946 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7958 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleOpenResults()
/*      */     throws SQLException
/*      */   {
/* 7965 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleResultSets()
/*      */     throws SQLException
/*      */   {
/* 7976 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleTransactions()
/*      */     throws SQLException
/*      */   {
/* 7988 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsNamedParameters()
/*      */     throws SQLException
/*      */   {
/* 7995 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsNonNullableColumns()
/*      */     throws SQLException
/*      */   {
/* 8007 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 8019 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 8031 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 8043 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 8055 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOrderByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 8066 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 8077 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedDelete()
/*      */     throws SQLException
/*      */   {
/* 8088 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedUpdate()
/*      */     throws SQLException
/*      */   {
/* 8099 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency)
/*      */     throws SQLException
/*      */   {
/* 8117 */     switch (type) {
/*      */     case 1004:
/* 8119 */       if ((concurrency == 1007) || (concurrency == 1008))
/*      */       {
/* 8121 */         return true;
/*      */       }
/* 8123 */       throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */     case 1003:
/* 8128 */       if ((concurrency == 1007) || (concurrency == 1008))
/*      */       {
/* 8130 */         return true;
/*      */       }
/* 8132 */       throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */     case 1005:
/* 8137 */       return false;
/*      */     }
/* 8139 */     throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/* 8151 */     return holdability == 1;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetType(int type)
/*      */     throws SQLException
/*      */   {
/* 8165 */     return type == 1004;
/*      */   }
/*      */ 
/*      */   public boolean supportsSavepoints()
/*      */     throws SQLException
/*      */   {
/* 8173 */     return (this.conn.versionMeetsMinimum(4, 0, 14)) || (this.conn.versionMeetsMinimum(4, 1, 1));
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 8185 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 8196 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 8207 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 8218 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 8229 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSelectForUpdate()
/*      */     throws SQLException
/*      */   {
/* 8240 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsStatementPooling()
/*      */     throws SQLException
/*      */   {
/* 8247 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredProcedures()
/*      */     throws SQLException
/*      */   {
/* 8259 */     return this.conn.versionMeetsMinimum(5, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInComparisons()
/*      */     throws SQLException
/*      */   {
/* 8271 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInExists()
/*      */     throws SQLException
/*      */   {
/* 8283 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInIns()
/*      */     throws SQLException
/*      */   {
/* 8295 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInQuantifieds()
/*      */     throws SQLException
/*      */   {
/* 8307 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 8319 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactionIsolationLevel(int level)
/*      */     throws SQLException
/*      */   {
/* 8334 */     if (this.conn.supportsIsolationLevel()) {
/* 8335 */       switch (level) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/* 8340 */         return true;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/* 8343 */       case 7: } return false;
/*      */     }
/*      */ 
/* 8347 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */     throws SQLException
/*      */   {
/* 8359 */     return this.conn.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public boolean supportsUnion()
/*      */     throws SQLException
/*      */   {
/* 8370 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsUnionAll()
/*      */     throws SQLException
/*      */   {
/* 8381 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean updatesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 8395 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFilePerTable()
/*      */     throws SQLException
/*      */   {
/* 8406 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFiles()
/*      */     throws SQLException
/*      */   {
/* 8417 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 8433 */     Field[] fields = createFunctionColumnsFields();
/*      */ 
/* 8435 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true);
/*      */   }
/*      */ 
/*      */   protected Field[] createFunctionColumnsFields()
/*      */   {
/* 8442 */     Field[] fields = { new Field("", "FUNCTION_CAT", 12, 512), new Field("", "FUNCTION_SCHEM", 12, 512), new Field("", "FUNCTION_NAME", 12, 512), new Field("", "COLUMN_NAME", 12, 512), new Field("", "COLUMN_TYPE", 12, 64), new Field("", "DATA_TYPE", 5, 6), new Field("", "TYPE_NAME", 12, 64), new Field("", "PRECISION", 4, 12), new Field("", "LENGTH", 4, 12), new Field("", "SCALE", 5, 12), new Field("", "RADIX", 5, 6), new Field("", "NULLABLE", 5, 6), new Field("", "REMARKS", 12, 512), new Field("", "CHAR_OCTET_LENGTH", 4, 32), new Field("", "ORDINAL_POSITION", 4, 32), new Field("", "IS_NULLABLE", 12, 12), new Field("", "SPECIFIC_NAME", 12, 64) };
/*      */ 
/* 8460 */     return fields;
/*      */   }
/*      */ 
/*      */   public boolean providesQueryObjectGenerator() throws SQLException {
/* 8464 */     return false;
/*      */   }
/*      */ 
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern)
/*      */     throws SQLException
/*      */   {
/* 8476 */     Field[] fields = { new Field("", "TABLE_SCHEM", 12, 255), new Field("", "TABLE_CATALOG", 12, 255) };
/*      */ 
/* 8481 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
/* 8485 */     return true;
/*      */   }
/*      */ 
/*      */   protected PreparedStatement prepareMetaDataSafeStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 8498 */     PreparedStatement pStmt = this.conn.clientPrepareStatement(sql);
/*      */ 
/* 8500 */     if (pStmt.getMaxRows() != 0) {
/* 8501 */       pStmt.setMaxRows(0);
/*      */     }
/*      */ 
/* 8504 */     ((Statement)pStmt).setHoldResultsOpenOverClose(true);
/*      */ 
/* 8506 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 8522 */     Field[] fields = { new Field("", "TABLE_CAT", 12, 512), new Field("", "TABLE_SCHEM", 12, 512), new Field("", "TABLE_NAME", 12, 512), new Field("", "COLUMN_NAME", 12, 512), new Field("", "DATA_TYPE", 4, 12), new Field("", "COLUMN_SIZE", 4, 12), new Field("", "DECIMAL_DIGITS", 4, 12), new Field("", "NUM_PREC_RADIX", 4, 12), new Field("", "COLUMN_USAGE", 12, 512), new Field("", "REMARKS", 12, 512), new Field("", "CHAR_OCTET_LENGTH", 4, 12), new Field("", "IS_NULLABLE", 12, 512) };
/*      */ 
/* 8535 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public boolean generatedKeyAlwaysReturned()
/*      */     throws SQLException
/*      */   {
/* 8541 */     return true;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  474 */     if (Util.isJdbc4()) {
/*      */       try {
/*  476 */         JDBC_4_DBMD_SHOW_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaData").getConstructor(new Class[] { MySQLConnection.class, String.class });
/*      */ 
/*  480 */         JDBC_4_DBMD_IS_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema").getConstructor(new Class[] { MySQLConnection.class, String.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  486 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  488 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  490 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  493 */       JDBC_4_DBMD_IS_CTOR = null;
/*  494 */       JDBC_4_DBMD_SHOW_CTOR = null;
/*      */     }
/*      */ 
/*  498 */     String[] allMySQLKeywords = { "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_ONLY", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL", "GENERAL", "IGNORE_SERVER_IDS", "MASTER_HEARTBEAT_PERIOD", "MAXVALUE", "RESIGNAL", "SIGNAL", "SLOW", "PARTITION", "REORGANIZE", "REBUILD", "REPAIR", "REMOVE", "ALGORITHM", "INPLACE", "COPY", "NONE", "SHARED", "EXCLUSIVE", "DATA", "DIRECTORY", "DISCARD", "TABLESPACE", "FLUSH", "TABLES", "EXPORT", "IMPORT", "EXCHANGE" };
/*      */ 
/*  551 */     String[] sql92Keywords = { "ABSOLUTE", "EXEC", "OVERLAPS", "ACTION", "EXECUTE", "PAD", "ADA", "EXISTS", "PARTIAL", "ADD", "EXTERNAL", "PASCAL", "ALL", "EXTRACT", "POSITION", "ALLOCATE", "FALSE", "PRECISION", "ALTER", "FETCH", "PREPARE", "AND", "FIRST", "PRESERVE", "ANY", "FLOAT", "PRIMARY", "ARE", "FOR", "PRIOR", "AS", "FOREIGN", "PRIVILEGES", "ASC", "FORTRAN", "PROCEDURE", "ASSERTION", "FOUND", "PUBLIC", "AT", "FROM", "READ", "AUTHORIZATION", "FULL", "REAL", "AVG", "GET", "REFERENCES", "BEGIN", "GLOBAL", "RELATIVE", "BETWEEN", "GO", "RESTRICT", "BIT", "GOTO", "REVOKE", "BIT_LENGTH", "GRANT", "RIGHT", "BOTH", "GROUP", "ROLLBACK", "BY", "HAVING", "ROWS", "CASCADE", "HOUR", "SCHEMA", "CASCADED", "IDENTITY", "SCROLL", "CASE", "IMMEDIATE", "SECOND", "CAST", "IN", "SECTION", "CATALOG", "INCLUDE", "SELECT", "CHAR", "INDEX", "SESSION", "CHAR_LENGTH", "INDICATOR", "SESSION_USER", "CHARACTER", "INITIALLY", "SET", "CHARACTER_LENGTH", "INNER", "SIZE", "CHECK", "INPUT", "SMALLINT", "CLOSE", "INSENSITIVE", "SOME", "COALESCE", "INSERT", "SPACE", "COLLATE", "INT", "SQL", "COLLATION", "INTEGER", "SQLCA", "COLUMN", "INTERSECT", "SQLCODE", "COMMIT", "INTERVAL", "SQLERROR", "CONNECT", "INTO", "SQLSTATE", "CONNECTION", "IS", "SQLWARNING", "CONSTRAINT", "ISOLATION", "SUBSTRING", "CONSTRAINTS", "JOIN", "SUM", "CONTINUE", "KEY", "SYSTEM_USER", "CONVERT", "LANGUAGE", "TABLE", "CORRESPONDING", "LAST", "TEMPORARY", "COUNT", "LEADING", "THEN", "CREATE", "LEFT", "TIME", "CROSS", "LEVEL", "TIMESTAMP", "CURRENT", "LIKE", "TIMEZONE_HOUR", "CURRENT_DATE", "LOCAL", "TIMEZONE_MINUTE", "CURRENT_TIME", "LOWER", "TO", "CURRENT_TIMESTAMP", "MATCH", "TRAILING", "CURRENT_USER", "MAX", "TRANSACTION", "CURSOR", "MIN", "TRANSLATE", "DATE", "MINUTE", "TRANSLATION", "DAY", "MODULE", "TRIM", "DEALLOCATE", "MONTH", "TRUE", "DEC", "NAMES", "UNION", "DECIMAL", "NATIONAL", "UNIQUE", "DECLARE", "NATURAL", "UNKNOWN", "DEFAULT", "NCHAR", "UPDATE", "DEFERRABLE", "NEXT", "UPPER", "DEFERRED", "NO", "USAGE", "DELETE", "NONE", "USER", "DESC", "NOT", "USING", "DESCRIBE", "NULL", "VALUE", "DESCRIPTOR", "NULLIF", "VALUES", "DIAGNOSTICS", "NUMERIC", "VARCHAR", "DISCONNECT", "OCTET_LENGTH", "VARYING", "DISTINCT", "OF", "VIEW", "DOMAIN", "ON", "WHEN", "DOUBLE", "ONLY", "WHENEVER", "DROP", "OPEN", "WHERE", "ELSE", "OPTION", "WITH", "END", "OR", "WORK", "END-EXEC", "ORDER", "WRITE", "ESCAPE", "OUTER", "YEAR", "EXCEPT", "OUTPUT", "ZONE", "EXCEPTION" };
/*      */ 
/*  593 */     TreeMap mySQLKeywordMap = new TreeMap();
/*      */ 
/*  595 */     for (int i = 0; i < allMySQLKeywords.length; i++) {
/*  596 */       mySQLKeywordMap.put(allMySQLKeywords[i], null);
/*      */     }
/*      */ 
/*  599 */     HashMap sql92KeywordMap = new HashMap(sql92Keywords.length);
/*      */ 
/*  601 */     for (int i = 0; i < sql92Keywords.length; i++) {
/*  602 */       sql92KeywordMap.put(sql92Keywords[i], null);
/*      */     }
/*      */ 
/*  605 */     Iterator it = sql92KeywordMap.keySet().iterator();
/*      */ 
/*  607 */     while (it.hasNext()) {
/*  608 */       mySQLKeywordMap.remove(it.next());
/*      */     }
/*      */ 
/*  611 */     StringBuffer keywordBuf = new StringBuffer();
/*      */ 
/*  613 */     it = mySQLKeywordMap.keySet().iterator();
/*      */ 
/*  615 */     if (it.hasNext()) {
/*  616 */       keywordBuf.append(((String)it.next()).toString());
/*      */     }
/*      */ 
/*  619 */     while (it.hasNext()) {
/*  620 */       keywordBuf.append(",");
/*  621 */       keywordBuf.append(((String)it.next()).toString());
/*      */     }
/*      */ 
/*  624 */     mysqlKeywordsThatArentSQL92 = keywordBuf.toString();
/*      */   }
/*      */ 
/*      */   class TypeDescriptor
/*      */   {
/*      */     int bufferLength;
/*      */     int charOctetLength;
/*      */     Integer columnSize;
/*      */     short dataType;
/*      */     Integer decimalDigits;
/*      */     String isNullable;
/*      */     int nullability;
/*  161 */     int numPrecRadix = 10;
/*      */     String typeName;
/*      */ 
/*      */     TypeDescriptor(String typeInfo, String nullabilityInfo)
/*      */       throws SQLException
/*      */     {
/*  167 */       if (typeInfo == null) {
/*  168 */         throw SQLError.createSQLException("NULL typeinfo not supported.", "S1009", DatabaseMetaData.this.getExceptionInterceptor());
/*      */       }
/*      */ 
/*  172 */       String mysqlType = "";
/*  173 */       String fullMysqlType = null;
/*      */ 
/*  175 */       if (typeInfo.indexOf("(") != -1)
/*  176 */         mysqlType = typeInfo.substring(0, typeInfo.indexOf("("));
/*      */       else {
/*  178 */         mysqlType = typeInfo;
/*      */       }
/*      */ 
/*  181 */       int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
/*      */ 
/*  184 */       if (indexOfUnsignedInMysqlType != -1) {
/*  185 */         mysqlType = mysqlType.substring(0, indexOfUnsignedInMysqlType - 1);
/*      */       }
/*      */ 
/*  192 */       boolean isUnsigned = false;
/*      */ 
/*  194 */       if ((StringUtils.indexOfIgnoreCase(typeInfo, "unsigned") != -1) && (StringUtils.indexOfIgnoreCase(typeInfo, "set") != 0) && (StringUtils.indexOfIgnoreCase(typeInfo, "enum") != 0))
/*      */       {
/*  197 */         fullMysqlType = mysqlType + " unsigned";
/*  198 */         isUnsigned = true;
/*      */       } else {
/*  200 */         fullMysqlType = mysqlType;
/*      */       }
/*      */ 
/*  203 */       if (DatabaseMetaData.this.conn.getCapitalizeTypeNames()) {
/*  204 */         fullMysqlType = fullMysqlType.toUpperCase(Locale.ENGLISH);
/*      */       }
/*      */ 
/*  207 */       this.dataType = (short)MysqlDefs.mysqlToJavaType(mysqlType);
/*      */ 
/*  209 */       this.typeName = fullMysqlType;
/*      */ 
/*  213 */       if (StringUtils.startsWithIgnoreCase(typeInfo, "enum")) {
/*  214 */         String temp = typeInfo.substring(typeInfo.indexOf("("), typeInfo.lastIndexOf(")"));
/*      */ 
/*  216 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/*  218 */         int maxLength = 0;
/*      */ 
/*  220 */         while (tokenizer.hasMoreTokens()) {
/*  221 */           maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */         }
/*      */ 
/*  225 */         this.columnSize = Integer.valueOf(maxLength);
/*  226 */         this.decimalDigits = null;
/*  227 */       } else if (StringUtils.startsWithIgnoreCase(typeInfo, "set")) {
/*  228 */         String temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
/*      */ 
/*  230 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/*  232 */         int maxLength = 0;
/*      */ 
/*  234 */         int numElements = tokenizer.countTokens();
/*      */ 
/*  236 */         if (numElements > 0) {
/*  237 */           maxLength += numElements - 1;
/*      */         }
/*      */ 
/*  240 */         while (tokenizer.hasMoreTokens()) {
/*  241 */           String setMember = tokenizer.nextToken().trim();
/*      */ 
/*  243 */           if ((setMember.startsWith("'")) && (setMember.endsWith("'")))
/*      */           {
/*  245 */             maxLength += setMember.length() - 2;
/*      */           }
/*  247 */           else maxLength += setMember.length();
/*      */ 
/*      */         }
/*      */ 
/*  251 */         this.columnSize = Integer.valueOf(maxLength);
/*  252 */         this.decimalDigits = null;
/*  253 */       } else if (typeInfo.indexOf(",") != -1)
/*      */       {
/*  255 */         this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
/*      */ 
/*  257 */         this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
/*      */       }
/*      */       else
/*      */       {
/*  261 */         this.columnSize = null;
/*  262 */         this.decimalDigits = null;
/*      */ 
/*  265 */         if (((StringUtils.indexOfIgnoreCase(typeInfo, "char") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "text") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "blob") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "binary") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "bit") != -1)) && (typeInfo.indexOf("(") != -1))
/*      */         {
/*  272 */           int endParenIndex = typeInfo.indexOf(")");
/*      */ 
/*  274 */           if (endParenIndex == -1) {
/*  275 */             endParenIndex = typeInfo.length();
/*      */           }
/*      */ 
/*  278 */           this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
/*      */ 
/*  282 */           if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (this.columnSize.intValue() == 1) && (StringUtils.startsWithIgnoreCase(typeInfo, 0, "tinyint")))
/*      */           {
/*  286 */             if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  287 */               this.dataType = 16;
/*  288 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  290 */               this.dataType = -7;
/*  291 */               this.typeName = "BIT";
/*      */             }
/*      */           }
/*  294 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyint"))
/*      */         {
/*  296 */           if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (typeInfo.indexOf("(1)") != -1)) {
/*  297 */             if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  298 */               this.dataType = 16;
/*  299 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  301 */               this.dataType = -7;
/*  302 */               this.typeName = "BIT";
/*      */             }
/*      */           } else {
/*  305 */             this.columnSize = Integer.valueOf(3);
/*  306 */             this.decimalDigits = Integer.valueOf(0);
/*      */           }
/*  308 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "smallint"))
/*      */         {
/*  310 */           this.columnSize = Integer.valueOf(5);
/*  311 */           this.decimalDigits = Integer.valueOf(0);
/*  312 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumint"))
/*      */         {
/*  314 */           this.columnSize = Integer.valueOf(isUnsigned ? 8 : 7);
/*  315 */           this.decimalDigits = Integer.valueOf(0);
/*  316 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int"))
/*      */         {
/*  318 */           this.columnSize = Integer.valueOf(10);
/*  319 */           this.decimalDigits = Integer.valueOf(0);
/*  320 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "integer"))
/*      */         {
/*  322 */           this.columnSize = Integer.valueOf(10);
/*  323 */           this.decimalDigits = Integer.valueOf(0);
/*  324 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "bigint"))
/*      */         {
/*  326 */           this.columnSize = Integer.valueOf(isUnsigned ? 20 : 19);
/*  327 */           this.decimalDigits = Integer.valueOf(0);
/*  328 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int24"))
/*      */         {
/*  330 */           this.columnSize = Integer.valueOf(19);
/*  331 */           this.decimalDigits = Integer.valueOf(0);
/*  332 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "real"))
/*      */         {
/*  334 */           this.columnSize = Integer.valueOf(12);
/*  335 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "float"))
/*      */         {
/*  337 */           this.columnSize = Integer.valueOf(12);
/*  338 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "decimal"))
/*      */         {
/*  340 */           this.columnSize = Integer.valueOf(12);
/*  341 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "numeric"))
/*      */         {
/*  343 */           this.columnSize = Integer.valueOf(12);
/*  344 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "double"))
/*      */         {
/*  346 */           this.columnSize = Integer.valueOf(22);
/*  347 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "char"))
/*      */         {
/*  349 */           this.columnSize = Integer.valueOf(1);
/*  350 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "varchar"))
/*      */         {
/*  352 */           this.columnSize = Integer.valueOf(255);
/*  353 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "timestamp"))
/*      */         {
/*  355 */           this.columnSize = Integer.valueOf(19);
/*  356 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "datetime"))
/*      */         {
/*  358 */           this.columnSize = Integer.valueOf(19);
/*  359 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "date"))
/*      */         {
/*  361 */           this.columnSize = Integer.valueOf(10);
/*  362 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "time"))
/*      */         {
/*  364 */           this.columnSize = Integer.valueOf(8);
/*      */         }
/*  366 */         else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyblob"))
/*      */         {
/*  368 */           this.columnSize = Integer.valueOf(255);
/*  369 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "blob"))
/*      */         {
/*  371 */           this.columnSize = Integer.valueOf(65535);
/*  372 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumblob"))
/*      */         {
/*  374 */           this.columnSize = Integer.valueOf(16777215);
/*  375 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longblob"))
/*      */         {
/*  377 */           this.columnSize = Integer.valueOf(2147483647);
/*  378 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinytext"))
/*      */         {
/*  380 */           this.columnSize = Integer.valueOf(255);
/*  381 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "text"))
/*      */         {
/*  383 */           this.columnSize = Integer.valueOf(65535);
/*  384 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumtext"))
/*      */         {
/*  386 */           this.columnSize = Integer.valueOf(16777215);
/*  387 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longtext"))
/*      */         {
/*  389 */           this.columnSize = Integer.valueOf(2147483647);
/*  390 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "enum"))
/*      */         {
/*  392 */           this.columnSize = Integer.valueOf(255);
/*  393 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "set"))
/*      */         {
/*  395 */           this.columnSize = Integer.valueOf(255);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  401 */       this.bufferLength = MysqlIO.getMaxBuf();
/*      */ 
/*  404 */       this.numPrecRadix = 10;
/*      */ 
/*  407 */       if (nullabilityInfo != null) {
/*  408 */         if (nullabilityInfo.equals("YES")) {
/*  409 */           this.nullability = 1;
/*  410 */           this.isNullable = "YES";
/*      */         }
/*      */         else
/*      */         {
/*  414 */           this.nullability = 0;
/*  415 */           this.isNullable = "NO";
/*      */         }
/*      */       } else {
/*  418 */         this.nullability = 0;
/*  419 */         this.isNullable = "NO";
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class SingleStringIterator extends DatabaseMetaData.IteratorWithCleanup<String>
/*      */   {
/*  119 */     boolean onFirst = true;
/*      */     String value;
/*      */ 
/*      */     SingleStringIterator(String s)
/*      */     {
/*  123 */       super();
/*  124 */       this.value = s;
/*      */     }
/*      */ 
/*      */     void close() throws SQLException
/*      */     {
/*      */     }
/*      */ 
/*      */     boolean hasNext() throws SQLException
/*      */     {
/*  133 */       return this.onFirst;
/*      */     }
/*      */ 
/*      */     String next() throws SQLException {
/*  137 */       this.onFirst = false;
/*  138 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ResultSetIterator extends DatabaseMetaData.IteratorWithCleanup<String>
/*      */   {
/*      */     int colIndex;
/*      */     ResultSet resultSet;
/*      */ 
/*      */     ResultSetIterator(ResultSet rs, int index)
/*      */     {
/*  100 */       super();
/*  101 */       this.resultSet = rs;
/*  102 */       this.colIndex = index;
/*      */     }
/*      */ 
/*      */     void close() throws SQLException {
/*  106 */       this.resultSet.close();
/*      */     }
/*      */ 
/*      */     boolean hasNext() throws SQLException {
/*  110 */       return this.resultSet.next();
/*      */     }
/*      */ 
/*      */     String next() throws SQLException {
/*  114 */       return this.resultSet.getObject(this.colIndex).toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   class LocalAndReferencedColumns
/*      */   {
/*      */     String constraintName;
/*      */     List<String> localColumnsList;
/*      */     String referencedCatalog;
/*      */     List<String> referencedColumnsList;
/*      */     String referencedTable;
/*      */ 
/*      */     LocalAndReferencedColumns(List<String> localColumns, String refColumns, String constName, String refCatalog)
/*      */     {
/*   87 */       this.localColumnsList = localColumns;
/*   88 */       this.referencedColumnsList = refColumns;
/*   89 */       this.constraintName = constName;
/*   90 */       this.referencedTable = refTable;
/*   91 */       this.referencedCatalog = refCatalog;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract class IteratorWithCleanup<T>
/*      */   {
/*      */     protected IteratorWithCleanup()
/*      */     {
/*      */     }
/*      */ 
/*      */     abstract void close()
/*      */       throws SQLException;
/*      */ 
/*      */     abstract boolean hasNext()
/*      */       throws SQLException;
/*      */ 
/*      */     abstract T next()
/*      */       throws SQLException;
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.DatabaseMetaData
 * JD-Core Version:    0.6.0
 */