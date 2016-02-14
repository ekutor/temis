/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.regex.PatternSyntaxException;
/*      */ 
/*      */ public class Field
/*      */ {
/*      */   private static final int AUTO_INCREMENT_FLAG = 512;
/*      */   private static final int NO_CHARSET_INFO = -1;
/*      */   private byte[] buffer;
/*   47 */   private int charsetIndex = 0;
/*      */ 
/*   49 */   private String charsetName = null;
/*      */   private int colDecimals;
/*      */   private short colFlag;
/*   55 */   private String collationName = null;
/*      */ 
/*   57 */   private MySQLConnection connection = null;
/*      */ 
/*   59 */   private String databaseName = null;
/*      */ 
/*   61 */   private int databaseNameLength = -1;
/*      */ 
/*   64 */   private int databaseNameStart = -1;
/*      */ 
/*   66 */   protected int defaultValueLength = -1;
/*      */ 
/*   69 */   protected int defaultValueStart = -1;
/*      */ 
/*   71 */   private String fullName = null;
/*      */ 
/*   73 */   private String fullOriginalName = null;
/*      */ 
/*   75 */   private boolean isImplicitTempTable = false;
/*      */   private long length;
/*   79 */   private int mysqlType = -1;
/*      */   private String name;
/*      */   private int nameLength;
/*      */   private int nameStart;
/*   87 */   private String originalColumnName = null;
/*      */ 
/*   89 */   private int originalColumnNameLength = -1;
/*      */ 
/*   92 */   private int originalColumnNameStart = -1;
/*      */ 
/*   94 */   private String originalTableName = null;
/*      */ 
/*   96 */   private int originalTableNameLength = -1;
/*      */ 
/*   99 */   private int originalTableNameStart = -1;
/*      */ 
/*  101 */   private int precisionAdjustFactor = 0;
/*      */ 
/*  103 */   private int sqlType = -1;
/*      */   private String tableName;
/*      */   private int tableNameLength;
/*      */   private int tableNameStart;
/*  111 */   private boolean useOldNameMetadata = false;
/*      */   private boolean isSingleBit;
/*      */   private int maxBytesPerChar;
/*      */   private final boolean valueNeedsQuoting;
/*      */ 
/*      */   Field(MySQLConnection conn, byte[] buffer, int databaseNameStart, int databaseNameLength, int tableNameStart, int tableNameLength, int originalTableNameStart, int originalTableNameLength, int nameStart, int nameLength, int originalColumnNameStart, int originalColumnNameLength, long length, int mysqlType, short colFlag, int colDecimals, int defaultValueStart, int defaultValueLength, int charsetIndex)
/*      */     throws SQLException
/*      */   {
/*  129 */     this.connection = conn;
/*  130 */     this.buffer = buffer;
/*  131 */     this.nameStart = nameStart;
/*  132 */     this.nameLength = nameLength;
/*  133 */     this.tableNameStart = tableNameStart;
/*  134 */     this.tableNameLength = tableNameLength;
/*  135 */     this.length = length;
/*  136 */     this.colFlag = colFlag;
/*  137 */     this.colDecimals = colDecimals;
/*  138 */     this.mysqlType = mysqlType;
/*      */ 
/*  141 */     this.databaseNameStart = databaseNameStart;
/*  142 */     this.databaseNameLength = databaseNameLength;
/*      */ 
/*  144 */     this.originalTableNameStart = originalTableNameStart;
/*  145 */     this.originalTableNameLength = originalTableNameLength;
/*      */ 
/*  147 */     this.originalColumnNameStart = originalColumnNameStart;
/*  148 */     this.originalColumnNameLength = originalColumnNameLength;
/*      */ 
/*  150 */     this.defaultValueStart = defaultValueStart;
/*  151 */     this.defaultValueLength = defaultValueLength;
/*      */ 
/*  155 */     this.charsetIndex = charsetIndex;
/*      */ 
/*  159 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */ 
/*  161 */     checkForImplicitTemporaryTable();
/*      */ 
/*  163 */     boolean isFromFunction = this.originalTableNameLength == 0;
/*      */ 
/*  165 */     if (this.mysqlType == 252) {
/*  166 */       if (((this.connection != null) && (this.connection.getBlobsAreStrings())) || ((this.connection.getFunctionsNeverReturnBlobs()) && (isFromFunction)))
/*      */       {
/*  168 */         this.sqlType = 12;
/*  169 */         this.mysqlType = 15;
/*  170 */       } else if ((this.charsetIndex == 63) || (!this.connection.versionMeetsMinimum(4, 1, 0)))
/*      */       {
/*  172 */         if ((this.connection.getUseBlobToStoreUTF8OutsideBMP()) && (shouldSetupForUtf8StringInBlob()))
/*      */         {
/*  174 */           setupForUtf8StringInBlob();
/*      */         } else {
/*  176 */           setBlobTypeBasedOnLength();
/*  177 */           this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */         }
/*      */       }
/*      */       else {
/*  181 */         this.mysqlType = 253;
/*  182 */         this.sqlType = -1;
/*      */       }
/*      */     }
/*      */ 
/*  186 */     if ((this.sqlType == -6) && (this.length == 1L) && (this.connection.getTinyInt1isBit()))
/*      */     {
/*  189 */       if (conn.getTinyInt1isBit()) {
/*  190 */         if (conn.getTransformedBitIsBoolean())
/*  191 */           this.sqlType = 16;
/*      */         else {
/*  193 */           this.sqlType = -7;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  199 */     if ((!isNativeNumericType()) && (!isNativeDateTimeType())) {
/*  200 */       this.charsetName = this.connection.getCharsetNameForIndex(this.charsetIndex);
/*      */ 
/*  206 */       if ("UnicodeBig".equals(this.charsetName)) {
/*  207 */         this.charsetName = "UTF-16";
/*      */       }
/*      */ 
/*  213 */       boolean isBinary = isBinary();
/*      */ 
/*  215 */       if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 253) && (isBinary) && (this.charsetIndex == 63))
/*      */       {
/*  219 */         if ((this.connection != null) && (this.connection.getFunctionsNeverReturnBlobs()) && (isFromFunction)) {
/*  220 */           this.sqlType = 12;
/*  221 */           this.mysqlType = 15;
/*  222 */         } else if (isOpaqueBinary()) {
/*  223 */           this.sqlType = -3;
/*      */         }
/*      */       }
/*      */ 
/*  227 */       if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 254) && (isBinary) && (this.charsetIndex == 63))
/*      */       {
/*  237 */         if ((isOpaqueBinary()) && (!this.connection.getBlobsAreStrings())) {
/*  238 */           this.sqlType = -2;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  244 */       if (this.mysqlType == 16) {
/*  245 */         this.isSingleBit = (this.length == 0L);
/*      */ 
/*  247 */         if ((this.connection != null) && ((this.connection.versionMeetsMinimum(5, 0, 21)) || (this.connection.versionMeetsMinimum(5, 1, 10))) && (this.length == 1L))
/*      */         {
/*  249 */           this.isSingleBit = true;
/*      */         }
/*      */ 
/*  252 */         if (this.isSingleBit) {
/*  253 */           this.sqlType = -7;
/*      */         } else {
/*  255 */           this.sqlType = -3;
/*  256 */           this.colFlag = (short)(this.colFlag | 0x80);
/*  257 */           this.colFlag = (short)(this.colFlag | 0x10);
/*  258 */           isBinary = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  265 */       if ((this.sqlType == -4) && (!isBinary))
/*  266 */         this.sqlType = -1;
/*  267 */       else if ((this.sqlType == -3) && (!isBinary))
/*  268 */         this.sqlType = 12;
/*      */     }
/*      */     else {
/*  271 */       this.charsetName = "US-ASCII";
/*      */     }
/*      */ 
/*  277 */     if (!isUnsigned()) {
/*  278 */       switch (this.mysqlType) {
/*      */       case 0:
/*      */       case 246:
/*  281 */         this.precisionAdjustFactor = -1;
/*      */ 
/*  283 */         break;
/*      */       case 4:
/*      */       case 5:
/*  286 */         this.precisionAdjustFactor = 1;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  291 */       switch (this.mysqlType) {
/*      */       case 4:
/*      */       case 5:
/*  294 */         this.precisionAdjustFactor = 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  299 */     this.valueNeedsQuoting = determineNeedsQuoting();
/*      */   }
/*      */ 
/*      */   private boolean shouldSetupForUtf8StringInBlob() throws SQLException {
/*  303 */     String includePattern = this.connection.getUtf8OutsideBmpIncludedColumnNamePattern();
/*      */ 
/*  305 */     String excludePattern = this.connection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*      */ 
/*  308 */     if ((excludePattern != null) && (!StringUtils.isEmptyOrWhitespaceOnly(excludePattern))) {
/*      */       try
/*      */       {
/*  311 */         if (getOriginalName().matches(excludePattern)) {
/*  312 */           if ((includePattern != null) && (!StringUtils.isEmptyOrWhitespaceOnly(includePattern))) {
/*      */             try
/*      */             {
/*  315 */               if (getOriginalName().matches(includePattern))
/*  316 */                 return true;
/*      */             }
/*      */             catch (PatternSyntaxException pse) {
/*  319 */               SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpIncludedColumnNamePattern\"", "S1009", this.connection.getExceptionInterceptor());
/*      */ 
/*  324 */               if (!this.connection.getParanoid()) {
/*  325 */                 sqlEx.initCause(pse);
/*      */               }
/*      */ 
/*  328 */               throw sqlEx;
/*      */             }
/*      */           }
/*      */ 
/*  332 */           return false;
/*      */         }
/*      */       } catch (PatternSyntaxException pse) {
/*  335 */         SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpExcludedColumnNamePattern\"", "S1009", this.connection.getExceptionInterceptor());
/*      */ 
/*  340 */         if (!this.connection.getParanoid()) {
/*  341 */           sqlEx.initCause(pse);
/*      */         }
/*      */ 
/*  344 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */ 
/*  348 */     return true;
/*      */   }
/*      */ 
/*      */   private void setupForUtf8StringInBlob() {
/*  352 */     if ((this.length == 255L) || (this.length == 65535L)) {
/*  353 */       this.mysqlType = 15;
/*  354 */       this.sqlType = 12;
/*      */     } else {
/*  356 */       this.mysqlType = 253;
/*  357 */       this.sqlType = -1;
/*      */     }
/*      */ 
/*  360 */     this.charsetIndex = 33;
/*      */   }
/*      */ 
/*      */   Field(MySQLConnection conn, byte[] buffer, int nameStart, int nameLength, int tableNameStart, int tableNameLength, int length, int mysqlType, short colFlag, int colDecimals)
/*      */     throws SQLException
/*      */   {
/*  369 */     this(conn, buffer, -1, -1, tableNameStart, tableNameLength, -1, -1, nameStart, nameLength, -1, -1, length, mysqlType, colFlag, colDecimals, -1, -1, -1);
/*      */   }
/*      */ 
/*      */   Field(String tableName, String columnName, int jdbcType, int length)
/*      */   {
/*  378 */     this.tableName = tableName;
/*  379 */     this.name = columnName;
/*  380 */     this.length = length;
/*  381 */     this.sqlType = jdbcType;
/*  382 */     this.colFlag = 0;
/*  383 */     this.colDecimals = 0;
/*  384 */     this.valueNeedsQuoting = determineNeedsQuoting();
/*      */   }
/*      */ 
/*      */   Field(String tableName, String columnName, int charsetIndex, int jdbcType, int length)
/*      */   {
/*  405 */     this.tableName = tableName;
/*  406 */     this.name = columnName;
/*  407 */     this.length = length;
/*  408 */     this.sqlType = jdbcType;
/*  409 */     this.colFlag = 0;
/*  410 */     this.colDecimals = 0;
/*  411 */     this.charsetIndex = charsetIndex;
/*  412 */     this.valueNeedsQuoting = determineNeedsQuoting();
/*      */ 
/*  414 */     switch (this.sqlType) {
/*      */     case -3:
/*      */     case -2:
/*  417 */       this.colFlag = (short)(this.colFlag | 0x80);
/*  418 */       this.colFlag = (short)(this.colFlag | 0x10);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkForImplicitTemporaryTable()
/*      */   {
/*  424 */     this.isImplicitTempTable = ((this.tableNameLength > 5) && (this.buffer[this.tableNameStart] == 35) && (this.buffer[(this.tableNameStart + 1)] == 115) && (this.buffer[(this.tableNameStart + 2)] == 113) && (this.buffer[(this.tableNameStart + 3)] == 108) && (this.buffer[(this.tableNameStart + 4)] == 95));
/*      */   }
/*      */ 
/*      */   public String getCharacterSet()
/*      */     throws SQLException
/*      */   {
/*  438 */     return this.charsetName;
/*      */   }
/*      */ 
/*      */   public void setCharacterSet(String javaEncodingName) throws SQLException {
/*  442 */     this.charsetName = javaEncodingName;
/*      */     try {
/*  444 */       this.charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(javaEncodingName);
/*      */     }
/*      */     catch (RuntimeException ex) {
/*  447 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/*  448 */       sqlEx.initCause(ex);
/*  449 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized String getCollation() throws SQLException {
/*  454 */     if ((this.collationName == null) && 
/*  455 */       (this.connection != null) && 
/*  456 */       (this.connection.versionMeetsMinimum(4, 1, 0))) {
/*  457 */       if (this.connection.getUseDynamicCharsetInfo()) {
/*  458 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  461 */         String quotedIdStr = dbmd.getIdentifierQuoteString();
/*      */ 
/*  463 */         if (" ".equals(quotedIdStr)) {
/*  464 */           quotedIdStr = "";
/*      */         }
/*      */ 
/*  467 */         String csCatalogName = getDatabaseName();
/*  468 */         String csTableName = getOriginalTableName();
/*  469 */         String csColumnName = getOriginalName();
/*      */ 
/*  471 */         if ((csCatalogName != null) && (csCatalogName.length() != 0) && (csTableName != null) && (csTableName.length() != 0) && (csColumnName != null) && (csColumnName.length() != 0))
/*      */         {
/*  475 */           StringBuffer queryBuf = new StringBuffer(csCatalogName.length() + csTableName.length() + 28);
/*      */ 
/*  478 */           queryBuf.append("SHOW FULL COLUMNS FROM ");
/*  479 */           queryBuf.append(quotedIdStr);
/*  480 */           queryBuf.append(csCatalogName);
/*  481 */           queryBuf.append(quotedIdStr);
/*  482 */           queryBuf.append(".");
/*  483 */           queryBuf.append(quotedIdStr);
/*  484 */           queryBuf.append(csTableName);
/*  485 */           queryBuf.append(quotedIdStr);
/*      */ 
/*  487 */           Statement collationStmt = null;
/*  488 */           ResultSet collationRs = null;
/*      */           try
/*      */           {
/*  491 */             collationStmt = this.connection.createStatement();
/*      */ 
/*  493 */             collationRs = collationStmt.executeQuery(queryBuf.toString());
/*      */ 
/*  496 */             while (collationRs.next()) {
/*  497 */               if (!csColumnName.equals(collationRs.getString("Field")))
/*      */                 continue;
/*  499 */               this.collationName = collationRs.getString("Collation");
/*      */             }
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*  506 */             if (collationRs != null) {
/*  507 */               collationRs.close();
/*  508 */               collationRs = null;
/*      */             }
/*      */ 
/*  511 */             if (collationStmt != null) {
/*  512 */               collationStmt.close();
/*  513 */               collationStmt = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       } else {
/*      */         try {
/*  519 */           this.collationName = CharsetMapping.INDEX_TO_COLLATION[this.charsetIndex];
/*      */         } catch (RuntimeException ex) {
/*  521 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/*  522 */           sqlEx.initCause(ex);
/*  523 */           throw sqlEx;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  530 */     return this.collationName;
/*      */   }
/*      */ 
/*      */   public String getColumnLabel() throws SQLException {
/*  534 */     return getName();
/*      */   }
/*      */ 
/*      */   public String getDatabaseName()
/*      */     throws SQLException
/*      */   {
/*  543 */     if ((this.databaseName == null) && (this.databaseNameStart != -1) && (this.databaseNameLength != -1))
/*      */     {
/*  545 */       this.databaseName = getStringFromBytes(this.databaseNameStart, this.databaseNameLength);
/*      */     }
/*      */ 
/*  549 */     return this.databaseName;
/*      */   }
/*      */ 
/*      */   int getDecimals() {
/*  553 */     return this.colDecimals;
/*      */   }
/*      */ 
/*      */   public String getFullName()
/*      */     throws SQLException
/*      */   {
/*  562 */     if (this.fullName == null) {
/*  563 */       StringBuffer fullNameBuf = new StringBuffer(getTableName().length() + 1 + getName().length());
/*      */ 
/*  565 */       fullNameBuf.append(this.tableName);
/*      */ 
/*  568 */       fullNameBuf.append('.');
/*  569 */       fullNameBuf.append(this.name);
/*  570 */       this.fullName = fullNameBuf.toString();
/*  571 */       fullNameBuf = null;
/*      */     }
/*      */ 
/*  574 */     return this.fullName;
/*      */   }
/*      */ 
/*      */   public String getFullOriginalName()
/*      */     throws SQLException
/*      */   {
/*  583 */     getOriginalName();
/*      */ 
/*  585 */     if (this.originalColumnName == null) {
/*  586 */       return null;
/*      */     }
/*      */ 
/*  589 */     if (this.fullName == null) {
/*  590 */       StringBuffer fullOriginalNameBuf = new StringBuffer(getOriginalTableName().length() + 1 + getOriginalName().length());
/*      */ 
/*  593 */       fullOriginalNameBuf.append(this.originalTableName);
/*      */ 
/*  596 */       fullOriginalNameBuf.append('.');
/*  597 */       fullOriginalNameBuf.append(this.originalColumnName);
/*  598 */       this.fullOriginalName = fullOriginalNameBuf.toString();
/*  599 */       fullOriginalNameBuf = null;
/*      */     }
/*      */ 
/*  602 */     return this.fullOriginalName;
/*      */   }
/*      */ 
/*      */   public long getLength()
/*      */   {
/*  611 */     return this.length;
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxBytesPerCharacter() throws SQLException {
/*  615 */     if (this.maxBytesPerChar == 0) {
/*  616 */       this.maxBytesPerChar = this.connection.getMaxBytesPerChar(Integer.valueOf(this.charsetIndex), getCharacterSet());
/*      */     }
/*  618 */     return this.maxBytesPerChar;
/*      */   }
/*      */ 
/*      */   public int getMysqlType()
/*      */   {
/*  627 */     return this.mysqlType;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */     throws SQLException
/*      */   {
/*  636 */     if (this.name == null) {
/*  637 */       this.name = getStringFromBytes(this.nameStart, this.nameLength);
/*      */     }
/*      */ 
/*  640 */     return this.name;
/*      */   }
/*      */ 
/*      */   public String getNameNoAliases() throws SQLException {
/*  644 */     if (this.useOldNameMetadata) {
/*  645 */       return getName();
/*      */     }
/*      */ 
/*  648 */     if ((this.connection != null) && (this.connection.versionMeetsMinimum(4, 1, 0)))
/*      */     {
/*  650 */       return getOriginalName();
/*      */     }
/*      */ 
/*  653 */     return getName();
/*      */   }
/*      */ 
/*      */   public String getOriginalName()
/*      */     throws SQLException
/*      */   {
/*  662 */     if ((this.originalColumnName == null) && (this.originalColumnNameStart != -1) && (this.originalColumnNameLength != -1))
/*      */     {
/*  665 */       this.originalColumnName = getStringFromBytes(this.originalColumnNameStart, this.originalColumnNameLength);
/*      */     }
/*      */ 
/*  669 */     return this.originalColumnName;
/*      */   }
/*      */ 
/*      */   public String getOriginalTableName()
/*      */     throws SQLException
/*      */   {
/*  678 */     if ((this.originalTableName == null) && (this.originalTableNameStart != -1) && (this.originalTableNameLength != -1))
/*      */     {
/*  681 */       this.originalTableName = getStringFromBytes(this.originalTableNameStart, this.originalTableNameLength);
/*      */     }
/*      */ 
/*  685 */     return this.originalTableName;
/*      */   }
/*      */ 
/*      */   public int getPrecisionAdjustFactor()
/*      */   {
/*  697 */     return this.precisionAdjustFactor;
/*      */   }
/*      */ 
/*      */   public int getSQLType()
/*      */   {
/*  706 */     return this.sqlType;
/*      */   }
/*      */ 
/*      */   private String getStringFromBytes(int stringStart, int stringLength)
/*      */     throws SQLException
/*      */   {
/*  715 */     if ((stringStart == -1) || (stringLength == -1)) {
/*  716 */       return null;
/*      */     }
/*      */ 
/*  719 */     String stringVal = null;
/*      */ 
/*  721 */     if (this.connection != null) {
/*  722 */       if (this.connection.getUseUnicode()) {
/*  723 */         String encoding = this.connection.getCharacterSetMetadata();
/*      */ 
/*  725 */         if (encoding == null) {
/*  726 */           encoding = this.connection.getEncoding();
/*      */         }
/*      */ 
/*  729 */         if (encoding != null) {
/*  730 */           SingleByteCharsetConverter converter = null;
/*      */ 
/*  732 */           if (this.connection != null) {
/*  733 */             converter = this.connection.getCharsetConverter(encoding);
/*      */           }
/*      */ 
/*  737 */           if (converter != null) {
/*  738 */             stringVal = converter.toString(this.buffer, stringStart, stringLength);
/*      */           }
/*      */           else
/*      */           {
/*  742 */             byte[] stringBytes = new byte[stringLength];
/*      */ 
/*  744 */             int endIndex = stringStart + stringLength;
/*  745 */             int pos = 0;
/*      */ 
/*  747 */             for (int i = stringStart; i < endIndex; i++) {
/*  748 */               stringBytes[(pos++)] = this.buffer[i];
/*      */             }
/*      */             try
/*      */             {
/*  752 */               stringVal = StringUtils.toString(stringBytes, encoding);
/*      */             } catch (UnsupportedEncodingException ue) {
/*  754 */               throw new RuntimeException(Messages.getString("Field.12") + encoding + Messages.getString("Field.13"));
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  761 */           stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  766 */         stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  771 */       stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*      */     }
/*      */ 
/*  775 */     return stringVal;
/*      */   }
/*      */ 
/*      */   public String getTable()
/*      */     throws SQLException
/*      */   {
/*  784 */     return getTableName();
/*      */   }
/*      */ 
/*      */   public String getTableName()
/*      */     throws SQLException
/*      */   {
/*  793 */     if (this.tableName == null) {
/*  794 */       this.tableName = getStringFromBytes(this.tableNameStart, this.tableNameLength);
/*      */     }
/*      */ 
/*  798 */     return this.tableName;
/*      */   }
/*      */ 
/*      */   public String getTableNameNoAliases() throws SQLException {
/*  802 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  803 */       return getOriginalTableName();
/*      */     }
/*      */ 
/*  806 */     return getTableName();
/*      */   }
/*      */ 
/*      */   public boolean isAutoIncrement()
/*      */   {
/*  815 */     return (this.colFlag & 0x200) > 0;
/*      */   }
/*      */ 
/*      */   public boolean isBinary()
/*      */   {
/*  824 */     return (this.colFlag & 0x80) > 0;
/*      */   }
/*      */ 
/*      */   public boolean isBlob()
/*      */   {
/*  833 */     return (this.colFlag & 0x10) > 0;
/*      */   }
/*      */ 
/*      */   private boolean isImplicitTemporaryTable()
/*      */   {
/*  842 */     return this.isImplicitTempTable;
/*      */   }
/*      */ 
/*      */   public boolean isMultipleKey()
/*      */   {
/*  851 */     return (this.colFlag & 0x8) > 0;
/*      */   }
/*      */ 
/*      */   boolean isNotNull() {
/*  855 */     return (this.colFlag & 0x1) > 0;
/*      */   }
/*      */ 
/*      */   boolean isOpaqueBinary()
/*      */     throws SQLException
/*      */   {
/*  865 */     if ((this.charsetIndex == 63) && (isBinary()) && ((getMysqlType() == 254) || (getMysqlType() == 253)))
/*      */     {
/*  869 */       if ((this.originalTableNameLength == 0) && (this.connection != null) && (!this.connection.versionMeetsMinimum(5, 0, 25)))
/*      */       {
/*  871 */         return false;
/*      */       }
/*      */ 
/*  877 */       return !isImplicitTemporaryTable();
/*      */     }
/*      */ 
/*  880 */     return (this.connection.versionMeetsMinimum(4, 1, 0)) && ("binary".equalsIgnoreCase(getCharacterSet()));
/*      */   }
/*      */ 
/*      */   public boolean isPrimaryKey()
/*      */   {
/*  891 */     return (this.colFlag & 0x2) > 0;
/*      */   }
/*      */ 
/*      */   boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*  901 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/*  902 */       String orgColumnName = getOriginalName();
/*  903 */       String orgTableName = getOriginalTableName();
/*      */ 
/*  905 */       return (orgColumnName == null) || (orgColumnName.length() <= 0) || (orgTableName == null) || (orgTableName.length() <= 0);
/*      */     }
/*      */ 
/*  909 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isUniqueKey()
/*      */   {
/*  918 */     return (this.colFlag & 0x4) > 0;
/*      */   }
/*      */ 
/*      */   public boolean isUnsigned()
/*      */   {
/*  927 */     return (this.colFlag & 0x20) > 0;
/*      */   }
/*      */ 
/*      */   public void setUnsigned() {
/*  931 */     this.colFlag = (short)(this.colFlag | 0x20);
/*      */   }
/*      */ 
/*      */   public boolean isZeroFill()
/*      */   {
/*  940 */     return (this.colFlag & 0x40) > 0;
/*      */   }
/*      */ 
/*      */   private void setBlobTypeBasedOnLength()
/*      */   {
/*  949 */     if (this.length == 255L)
/*  950 */       this.mysqlType = 249;
/*  951 */     else if (this.length == 65535L)
/*  952 */       this.mysqlType = 252;
/*  953 */     else if (this.length == 16777215L)
/*  954 */       this.mysqlType = 250;
/*  955 */     else if (this.length == 4294967295L)
/*  956 */       this.mysqlType = 251;
/*      */   }
/*      */ 
/*      */   private boolean isNativeNumericType()
/*      */   {
/*  961 */     return ((this.mysqlType >= 1) && (this.mysqlType <= 5)) || (this.mysqlType == 8) || (this.mysqlType == 13);
/*      */   }
/*      */ 
/*      */   private boolean isNativeDateTimeType()
/*      */   {
/*  968 */     return (this.mysqlType == 10) || (this.mysqlType == 14) || (this.mysqlType == 12) || (this.mysqlType == 11) || (this.mysqlType == 7);
/*      */   }
/*      */ 
/*      */   public void setConnection(MySQLConnection conn)
/*      */   {
/*  982 */     this.connection = conn;
/*      */ 
/*  984 */     if ((this.charsetName == null) || (this.charsetIndex == 0))
/*  985 */       this.charsetName = this.connection.getEncoding();
/*      */   }
/*      */ 
/*      */   void setMysqlType(int type)
/*      */   {
/*  990 */     this.mysqlType = type;
/*  991 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*      */   }
/*      */ 
/*      */   protected void setUseOldNameMetadata(boolean useOldNameMetadata) {
/*  995 */     this.useOldNameMetadata = useOldNameMetadata;
/*      */   }
/*      */ 
/*      */   public String toString() {
/*      */     try {
/* 1000 */       StringBuffer asString = new StringBuffer();
/* 1001 */       asString.append(super.toString());
/* 1002 */       asString.append("[");
/* 1003 */       asString.append("catalog=");
/* 1004 */       asString.append(getDatabaseName());
/* 1005 */       asString.append(",tableName=");
/* 1006 */       asString.append(getTableName());
/* 1007 */       asString.append(",originalTableName=");
/* 1008 */       asString.append(getOriginalTableName());
/* 1009 */       asString.append(",columnName=");
/* 1010 */       asString.append(getName());
/* 1011 */       asString.append(",originalColumnName=");
/* 1012 */       asString.append(getOriginalName());
/* 1013 */       asString.append(",mysqlType=");
/* 1014 */       asString.append(getMysqlType());
/* 1015 */       asString.append("(");
/* 1016 */       asString.append(MysqlDefs.typeToName(getMysqlType()));
/* 1017 */       asString.append(")");
/* 1018 */       asString.append(",flags=");
/*      */ 
/* 1020 */       if (isAutoIncrement()) {
/* 1021 */         asString.append(" AUTO_INCREMENT");
/*      */       }
/*      */ 
/* 1024 */       if (isPrimaryKey()) {
/* 1025 */         asString.append(" PRIMARY_KEY");
/*      */       }
/*      */ 
/* 1028 */       if (isUniqueKey()) {
/* 1029 */         asString.append(" UNIQUE_KEY");
/*      */       }
/*      */ 
/* 1032 */       if (isBinary()) {
/* 1033 */         asString.append(" BINARY");
/*      */       }
/*      */ 
/* 1036 */       if (isBlob()) {
/* 1037 */         asString.append(" BLOB");
/*      */       }
/*      */ 
/* 1040 */       if (isMultipleKey()) {
/* 1041 */         asString.append(" MULTI_KEY");
/*      */       }
/*      */ 
/* 1044 */       if (isUnsigned()) {
/* 1045 */         asString.append(" UNSIGNED");
/*      */       }
/*      */ 
/* 1048 */       if (isZeroFill()) {
/* 1049 */         asString.append(" ZEROFILL");
/*      */       }
/*      */ 
/* 1052 */       asString.append(", charsetIndex=");
/* 1053 */       asString.append(this.charsetIndex);
/* 1054 */       asString.append(", charsetName=");
/* 1055 */       asString.append(this.charsetName);
/*      */ 
/* 1064 */       asString.append("]");
/*      */ 
/* 1066 */       return asString.toString(); } catch (Throwable t) {
/*      */     }
/* 1068 */     return super.toString();
/*      */   }
/*      */ 
/*      */   protected boolean isSingleBit()
/*      */   {
/* 1073 */     return this.isSingleBit;
/*      */   }
/*      */ 
/*      */   protected boolean getvalueNeedsQuoting() {
/* 1077 */     return this.valueNeedsQuoting;
/*      */   }
/*      */ 
/*      */   private boolean determineNeedsQuoting() {
/* 1081 */     boolean retVal = false;
/*      */ 
/* 1083 */     switch (this.sqlType) {
/*      */     case -7:
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 1094 */       retVal = false;
/* 1095 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     default:
/* 1097 */       retVal = true;
/*      */     }
/* 1099 */     return retVal;
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Field
 * JD-Core Version:    0.6.0
 */