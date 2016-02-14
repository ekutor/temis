/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class CallableStatement extends PreparedStatement
/*      */   implements java.sql.CallableStatement
/*      */ {
/*      */   protected static final Constructor<?> JDBC_4_CSTMT_2_ARGS_CTOR;
/*      */   protected static final Constructor<?> JDBC_4_CSTMT_4_ARGS_CTOR;
/*      */   private static final int NOT_OUTPUT_PARAMETER_INDICATOR = -2147483648;
/*      */   private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
/*  467 */   private boolean callingStoredFunction = false;
/*      */   private ResultSetInternalMethods functionReturnValueResults;
/*  471 */   private boolean hasOutputParams = false;
/*      */   private ResultSetInternalMethods outputParameterResults;
/*  477 */   protected boolean outputParamWasNull = false;
/*      */   private int[] parameterIndexToRsIndex;
/*      */   protected CallableStatementParamInfo paramInfo;
/*      */   private CallableStatementParam returnValueParam;
/*      */   private int[] placeholderToParameterIndexMap;
/*      */ 
/*      */   private static String mangleParameterName(String origParameterName)
/*      */   {
/*  447 */     if (origParameterName == null) {
/*  448 */       return null;
/*      */     }
/*      */ 
/*  451 */     int offset = 0;
/*      */ 
/*  453 */     if ((origParameterName.length() > 0) && (origParameterName.charAt(0) == '@'))
/*      */     {
/*  455 */       offset = 1;
/*      */     }
/*      */ 
/*  458 */     StringBuffer paramNameBuf = new StringBuffer("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
/*      */ 
/*  461 */     paramNameBuf.append("@com_mysql_jdbc_outparam_");
/*  462 */     paramNameBuf.append(origParameterName.substring(offset));
/*      */ 
/*  464 */     return paramNameBuf.toString();
/*      */   }
/*      */ 
/*      */   public CallableStatement(MySQLConnection conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  498 */     super(conn, paramInfo.nativeSql, paramInfo.catalogInUse);
/*      */ 
/*  500 */     this.paramInfo = paramInfo;
/*  501 */     this.callingStoredFunction = this.paramInfo.isFunctionCall;
/*      */ 
/*  503 */     if (this.callingStoredFunction) {
/*  504 */       this.parameterCount += 1;
/*      */     }
/*      */ 
/*  507 */     this.retrieveGeneratedKeys = true;
/*      */   }
/*      */ 
/*      */   protected static CallableStatement getInstance(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  519 */     if (!Util.isJdbc4()) {
/*  520 */       return new CallableStatement(conn, sql, catalog, isFunctionCall);
/*      */     }
/*      */ 
/*  523 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_4_ARGS_CTOR, new Object[] { conn, sql, catalog, Boolean.valueOf(isFunctionCall) }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected static CallableStatement getInstance(MySQLConnection conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  537 */     if (!Util.isJdbc4()) {
/*  538 */       return new CallableStatement(conn, paramInfo);
/*      */     }
/*      */ 
/*  541 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_2_ARGS_CTOR, new Object[] { conn, paramInfo }, conn.getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private void generateParameterMap()
/*      */     throws SQLException
/*      */   {
/*  549 */     synchronized (checkClosed()) {
/*  550 */       if (this.paramInfo == null) {
/*  551 */         return;
/*      */       }
/*      */ 
/*  558 */       int parameterCountFromMetaData = this.paramInfo.getParameterCount();
/*      */ 
/*  562 */       if (this.callingStoredFunction) {
/*  563 */         parameterCountFromMetaData--;
/*      */       }
/*      */ 
/*  566 */       if ((this.paramInfo != null) && (this.parameterCount != parameterCountFromMetaData))
/*      */       {
/*  568 */         this.placeholderToParameterIndexMap = new int[this.parameterCount];
/*      */ 
/*  570 */         int startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase(this.originalSql, "SELECT") : StringUtils.indexOfIgnoreCase(this.originalSql, "CALL");
/*      */ 
/*  573 */         if (startPos != -1) {
/*  574 */           int parenOpenPos = this.originalSql.indexOf('(', startPos + 4);
/*      */ 
/*  576 */           if (parenOpenPos != -1) {
/*  577 */             int parenClosePos = StringUtils.indexOfIgnoreCaseRespectQuotes(parenOpenPos, this.originalSql, ")", '\'', true);
/*      */ 
/*  580 */             if (parenClosePos != -1) {
/*  581 */               List parsedParameters = StringUtils.split(this.originalSql.substring(parenOpenPos + 1, parenClosePos), ",", "'\"", "'\"", true);
/*      */ 
/*  583 */               int numParsedParameters = parsedParameters.size();
/*      */ 
/*  587 */               if (numParsedParameters != this.parameterCount);
/*  591 */               int placeholderCount = 0;
/*      */ 
/*  593 */               for (int i = 0; i < numParsedParameters; i++)
/*  594 */                 if (((String)parsedParameters.get(i)).equals("?"))
/*  595 */                   this.placeholderToParameterIndexMap[(placeholderCount++)] = i;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  620 */     super(conn, sql, catalog);
/*      */ 
/*  622 */     this.callingStoredFunction = isFunctionCall;
/*      */ 
/*  624 */     if (!this.callingStoredFunction) {
/*  625 */       if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL"))
/*      */       {
/*  627 */         fakeParameterTypes(false);
/*      */       }
/*  629 */       else determineParameterTypes();
/*      */ 
/*  632 */       generateParameterMap();
/*      */     } else {
/*  634 */       determineParameterTypes();
/*  635 */       generateParameterMap();
/*      */ 
/*  637 */       this.parameterCount += 1;
/*      */     }
/*      */ 
/*  640 */     this.retrieveGeneratedKeys = true;
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  649 */     setOutParams();
/*      */ 
/*  651 */     super.addBatch();
/*      */   }
/*      */ 
/*      */   private CallableStatementParam checkIsOutputParam(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  657 */     synchronized (checkClosed()) {
/*  658 */       if (this.callingStoredFunction) {
/*  659 */         if (paramIndex == 1)
/*      */         {
/*  661 */           if (this.returnValueParam == null) {
/*  662 */             this.returnValueParam = new CallableStatementParam("", 0, false, true, 12, "VARCHAR", 0, 0, 2, 5);
/*      */           }
/*      */ 
/*  668 */           return this.returnValueParam;
/*      */         }
/*      */ 
/*  672 */         paramIndex--;
/*      */       }
/*      */ 
/*  675 */       checkParameterIndexBounds(paramIndex);
/*      */ 
/*  677 */       int localParamIndex = paramIndex - 1;
/*      */ 
/*  679 */       if (this.placeholderToParameterIndexMap != null) {
/*  680 */         localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */       }
/*      */ 
/*  683 */       CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
/*      */ 
/*  689 */       if (this.connection.getNoAccessToProcedureBodies()) {
/*  690 */         paramDescriptor.isOut = true;
/*  691 */         paramDescriptor.isIn = true;
/*  692 */         paramDescriptor.inOutModifier = 2;
/*  693 */       } else if (!paramDescriptor.isOut) {
/*  694 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.9") + paramIndex + Messages.getString("CallableStatement.10"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  700 */       this.hasOutputParams = true;
/*      */ 
/*  702 */       return paramDescriptor;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkParameterIndexBounds(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  714 */     synchronized (checkClosed()) {
/*  715 */       this.paramInfo.checkBounds(paramIndex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkStreamability()
/*      */     throws SQLException
/*      */   {
/*  728 */     if ((this.hasOutputParams) && (createStreamingResultSet()))
/*  729 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), "S1C00", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  735 */     synchronized (checkClosed()) {
/*  736 */       super.clearParameters();
/*      */       try
/*      */       {
/*  739 */         if (this.outputParameterResults != null)
/*  740 */           this.outputParameterResults.close();
/*      */       }
/*      */       finally {
/*  743 */         this.outputParameterResults = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fakeParameterTypes(boolean isReallyProcedure)
/*      */     throws SQLException
/*      */   {
/*  755 */     synchronized (checkClosed()) {
/*  756 */       Field[] fields = new Field[13];
/*      */ 
/*  758 */       fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/*  759 */       fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/*  760 */       fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/*  761 */       fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/*  762 */       fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/*  763 */       fields[5] = new Field("", "DATA_TYPE", 5, 0);
/*  764 */       fields[6] = new Field("", "TYPE_NAME", 1, 0);
/*  765 */       fields[7] = new Field("", "PRECISION", 4, 0);
/*  766 */       fields[8] = new Field("", "LENGTH", 4, 0);
/*  767 */       fields[9] = new Field("", "SCALE", 5, 0);
/*  768 */       fields[10] = new Field("", "RADIX", 5, 0);
/*  769 */       fields[11] = new Field("", "NULLABLE", 5, 0);
/*  770 */       fields[12] = new Field("", "REMARKS", 1, 0);
/*      */ 
/*  772 */       String procName = isReallyProcedure ? extractProcedureName() : null;
/*      */ 
/*  774 */       byte[] procNameAsBytes = null;
/*      */       try
/*      */       {
/*  777 */         procNameAsBytes = procName == null ? null : StringUtils.getBytes(procName, "UTF-8");
/*      */       } catch (UnsupportedEncodingException ueEx) {
/*  779 */         procNameAsBytes = StringUtils.s2b(procName, this.connection);
/*      */       }
/*      */ 
/*  782 */       ArrayList resultRows = new ArrayList();
/*      */ 
/*  784 */       for (int i = 0; i < this.parameterCount; i++) {
/*  785 */         byte[][] row = new byte[13][];
/*  786 */         row[0] = null;
/*  787 */         row[1] = null;
/*  788 */         row[2] = procNameAsBytes;
/*  789 */         row[3] = StringUtils.s2b(String.valueOf(i), this.connection);
/*      */ 
/*  791 */         row[4] = StringUtils.s2b(String.valueOf(1), this.connection);
/*      */ 
/*  795 */         row[5] = StringUtils.s2b(String.valueOf(12), this.connection);
/*      */ 
/*  797 */         row[6] = StringUtils.s2b("VARCHAR", this.connection);
/*  798 */         row[7] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  799 */         row[8] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  800 */         row[9] = StringUtils.s2b(Integer.toString(0), this.connection);
/*  801 */         row[10] = StringUtils.s2b(Integer.toString(10), this.connection);
/*      */ 
/*  803 */         row[11] = StringUtils.s2b(Integer.toString(2), this.connection);
/*      */ 
/*  807 */         row[12] = null;
/*      */ 
/*  809 */         resultRows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */       }
/*      */ 
/*  812 */       ResultSet paramTypesRs = DatabaseMetaData.buildResultSet(fields, resultRows, this.connection);
/*      */ 
/*  815 */       convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void determineParameterTypes() throws SQLException {
/*  820 */     synchronized (checkClosed()) {
/*  821 */       ResultSet paramTypesRs = null;
/*      */       try
/*      */       {
/*  826 */         String procName = extractProcedureName();
/*  827 */         String quotedId = "";
/*      */         try {
/*  829 */           quotedId = this.connection.supportsQuotedIdentifiers() ? this.connection.getMetaData().getIdentifierQuoteString() : "";
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*  834 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/*  837 */         List parseList = StringUtils.splitDBdotName(procName, "", quotedId, this.connection.isNoBackslashEscapesSet());
/*      */ 
/*  839 */         String tmpCatalog = "";
/*      */ 
/*  841 */         if (parseList.size() == 2) {
/*  842 */           tmpCatalog = (String)parseList.get(0);
/*  843 */           procName = (String)parseList.get(1);
/*      */         }
/*      */ 
/*  848 */         java.sql.DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  850 */         boolean useCatalog = false;
/*      */ 
/*  852 */         if (tmpCatalog.length() <= 0) {
/*  853 */           useCatalog = true;
/*      */         }
/*      */ 
/*  856 */         paramTypesRs = dbmd.getProcedureColumns((this.connection.versionMeetsMinimum(5, 0, 2)) && (useCatalog) ? this.currentCatalog : tmpCatalog, null, procName, "%");
/*      */ 
/*  861 */         boolean hasResults = false;
/*      */         try {
/*  863 */           if (paramTypesRs.next()) {
/*  864 */             paramTypesRs.previous();
/*  865 */             hasResults = true;
/*      */           }
/*      */         }
/*      */         catch (Exception e) {
/*      */         }
/*  870 */         if (hasResults)
/*  871 */           convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */         else
/*  873 */           fakeParameterTypes(true);
/*      */       }
/*      */       finally {
/*  876 */         SQLException sqlExRethrow = null;
/*      */ 
/*  878 */         if (paramTypesRs != null) {
/*      */           try {
/*  880 */             paramTypesRs.close();
/*      */           } catch (SQLException sqlEx) {
/*  882 */             sqlExRethrow = sqlEx;
/*      */           }
/*      */ 
/*  885 */           paramTypesRs = null;
/*      */         }
/*      */ 
/*  888 */         if (sqlExRethrow != null)
/*  889 */           throw sqlExRethrow;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException
/*      */   {
/*  896 */     synchronized (checkClosed()) {
/*  897 */       if (!this.connection.isRunningOnJDK13()) {
/*  898 */         this.paramInfo = new CallableStatementParamInfoJDBC3(paramTypesRs);
/*      */       }
/*      */       else
/*  901 */         this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  912 */     synchronized (checkClosed()) {
/*  913 */       boolean returnVal = false;
/*      */ 
/*  915 */       checkStreamability();
/*      */ 
/*  917 */       setInOutParamsOnServer();
/*  918 */       setOutParams();
/*      */ 
/*  920 */       returnVal = super.execute();
/*      */ 
/*  922 */       if (this.callingStoredFunction) {
/*  923 */         this.functionReturnValueResults = this.results;
/*  924 */         this.functionReturnValueResults.next();
/*  925 */         this.results = null;
/*      */       }
/*      */ 
/*  928 */       retrieveOutParams();
/*      */ 
/*  931 */       if (!this.callingStoredFunction) {
/*  932 */         return returnVal;
/*      */       }
/*      */ 
/*  936 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  946 */     synchronized (checkClosed())
/*      */     {
/*  948 */       checkStreamability();
/*      */ 
/*  950 */       ResultSet execResults = null;
/*      */ 
/*  952 */       setInOutParamsOnServer();
/*  953 */       setOutParams();
/*      */ 
/*  955 */       execResults = super.executeQuery();
/*      */ 
/*  957 */       retrieveOutParams();
/*      */ 
/*  959 */       return execResults;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  969 */     synchronized (checkClosed()) {
/*  970 */       int returnVal = -1;
/*      */ 
/*  973 */       checkStreamability();
/*      */ 
/*  975 */       if (this.callingStoredFunction) {
/*  976 */         execute();
/*      */ 
/*  978 */         return -1;
/*      */       }
/*      */ 
/*  981 */       setInOutParamsOnServer();
/*  982 */       setOutParams();
/*      */ 
/*  984 */       returnVal = super.executeUpdate();
/*      */ 
/*  986 */       retrieveOutParams();
/*      */ 
/*  988 */       return returnVal;
/*      */     }
/*      */   }
/*      */ 
/*      */   private String extractProcedureName() throws SQLException {
/*  993 */     String sanitizedSql = StringUtils.stripComments(this.originalSql, "`\"'", "`\"'", true, false, true, true);
/*      */ 
/*  997 */     int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
/*      */ 
/*  999 */     int offset = 5;
/*      */ 
/* 1001 */     if (endCallIndex == -1) {
/* 1002 */       endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
/*      */ 
/* 1004 */       offset = 7;
/*      */     }
/*      */ 
/* 1007 */     if (endCallIndex != -1) {
/* 1008 */       StringBuffer nameBuf = new StringBuffer();
/*      */ 
/* 1010 */       String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
/*      */ 
/* 1013 */       int statementLength = trimmedStatement.length();
/*      */ 
/* 1015 */       for (int i = 0; i < statementLength; i++) {
/* 1016 */         char c = trimmedStatement.charAt(i);
/*      */ 
/* 1018 */         if ((Character.isWhitespace(c)) || (c == '(') || (c == '?')) {
/*      */           break;
/*      */         }
/* 1021 */         nameBuf.append(c);
/*      */       }
/*      */ 
/* 1025 */       return nameBuf.toString();
/*      */     }
/*      */ 
/* 1028 */     throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), "S1000", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected String fixParameterName(String paramNameIn)
/*      */     throws SQLException
/*      */   {
/* 1044 */     synchronized (checkClosed())
/*      */     {
/* 1046 */       if (((paramNameIn == null) || (paramNameIn.length() == 0)) && (!hasParametersView())) {
/* 1047 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.0") + paramNameIn == null ? Messages.getString("CallableStatement.15") : Messages.getString("CallableStatement.16"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1053 */       if ((paramNameIn == null) && (hasParametersView())) {
/* 1054 */         paramNameIn = "nullpn";
/*      */       }
/*      */ 
/* 1057 */       if (this.connection.getNoAccessToProcedureBodies()) {
/* 1058 */         throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1062 */       return mangleParameterName(paramNameIn);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/* 1070 */     synchronized (checkClosed()) {
/* 1071 */       ResultSetInternalMethods rs = getOutputParameters(i);
/*      */ 
/* 1073 */       Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
/*      */ 
/* 1075 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1077 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Array getArray(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1086 */     synchronized (checkClosed()) {
/* 1087 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1090 */       Array retValue = rs.getArray(fixParameterName(parameterName));
/*      */ 
/* 1092 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1094 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1103 */     synchronized (checkClosed()) {
/* 1104 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1106 */       BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1109 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1111 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1133 */     synchronized (checkClosed()) {
/* 1134 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1136 */       BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
/*      */ 
/* 1139 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1141 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1150 */     synchronized (checkClosed()) {
/* 1151 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1154 */       BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
/*      */ 
/* 1156 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1158 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Blob getBlob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1166 */     synchronized (checkClosed()) {
/* 1167 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1169 */       Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1172 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1174 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Blob getBlob(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1182 */     synchronized (checkClosed()) {
/* 1183 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1186 */       Blob retValue = rs.getBlob(fixParameterName(parameterName));
/*      */ 
/* 1188 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1190 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1199 */     synchronized (checkClosed()) {
/* 1200 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1202 */       boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1205 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1207 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1216 */     synchronized (checkClosed()) {
/* 1217 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1220 */       boolean retValue = rs.getBoolean(fixParameterName(parameterName));
/*      */ 
/* 1222 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1224 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte getByte(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1232 */     synchronized (checkClosed()) {
/* 1233 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1235 */       byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1238 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1240 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte getByte(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1248 */     synchronized (checkClosed()) {
/* 1249 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1252 */       byte retValue = rs.getByte(fixParameterName(parameterName));
/*      */ 
/* 1254 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1256 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1264 */     synchronized (checkClosed()) {
/* 1265 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1267 */       byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1270 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1272 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1281 */     synchronized (checkClosed()) {
/* 1282 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1285 */       byte[] retValue = rs.getBytes(fixParameterName(parameterName));
/*      */ 
/* 1287 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1289 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Clob getClob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1297 */     synchronized (checkClosed()) {
/* 1298 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1300 */       Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1303 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1305 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Clob getClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1313 */     synchronized (checkClosed()) {
/* 1314 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1317 */       Clob retValue = rs.getClob(fixParameterName(parameterName));
/*      */ 
/* 1319 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1321 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Date getDate(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1329 */     synchronized (checkClosed()) {
/* 1330 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1332 */       Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1335 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1337 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Date getDate(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1346 */     synchronized (checkClosed()) {
/* 1347 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1349 */       Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1352 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1354 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Date getDate(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1362 */     synchronized (checkClosed()) {
/* 1363 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1366 */       Date retValue = rs.getDate(fixParameterName(parameterName));
/*      */ 
/* 1368 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1370 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Date getDate(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1380 */     synchronized (checkClosed()) {
/* 1381 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1384 */       Date retValue = rs.getDate(fixParameterName(parameterName), cal);
/*      */ 
/* 1386 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1388 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public double getDouble(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1397 */     synchronized (checkClosed()) {
/* 1398 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1400 */       double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1403 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1405 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public double getDouble(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1414 */     synchronized (checkClosed()) {
/* 1415 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1418 */       double retValue = rs.getDouble(fixParameterName(parameterName));
/*      */ 
/* 1420 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1422 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public float getFloat(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1430 */     synchronized (checkClosed()) {
/* 1431 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1433 */       float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1436 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1438 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public float getFloat(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1447 */     synchronized (checkClosed()) {
/* 1448 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1451 */       float retValue = rs.getFloat(fixParameterName(parameterName));
/*      */ 
/* 1453 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1455 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getInt(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1463 */     synchronized (checkClosed()) {
/* 1464 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1466 */       int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1469 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1471 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getInt(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1479 */     synchronized (checkClosed()) {
/* 1480 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1483 */       int retValue = rs.getInt(fixParameterName(parameterName));
/*      */ 
/* 1485 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1487 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getLong(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1495 */     synchronized (checkClosed()) {
/* 1496 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1498 */       long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1501 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1503 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getLong(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1511 */     synchronized (checkClosed()) {
/* 1512 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1515 */       long retValue = rs.getLong(fixParameterName(parameterName));
/*      */ 
/* 1517 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1519 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException
/*      */   {
/* 1525 */     synchronized (checkClosed()) {
/* 1526 */       if (this.connection.getNoAccessToProcedureBodies()) {
/* 1527 */         throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1532 */       if ((paramName == null) || (paramName.length() == 0)) {
/* 1533 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1537 */       if (this.paramInfo == null) {
/* 1538 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.3") + paramName + Messages.getString("CallableStatement.4"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1543 */       CallableStatementParam namedParamInfo = this.paramInfo.getParameter(paramName);
/*      */ 
/* 1546 */       if ((forOut) && (!namedParamInfo.isOut)) {
/* 1547 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.5") + paramName + Messages.getString("CallableStatement.6"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1554 */       if (this.placeholderToParameterIndexMap == null) {
/* 1555 */         return namedParamInfo.index + 1;
/*      */       }
/*      */ 
/* 1558 */       for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 1559 */         if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index) {
/* 1560 */           return i + 1;
/*      */         }
/*      */       }
/*      */ 
/* 1564 */       throw SQLError.createSQLException("Can't find local placeholder mapping for parameter named \"" + paramName + "\".", "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getObject(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1574 */     synchronized (checkClosed()) {
/* 1575 */       CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/*      */ 
/* 1577 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1579 */       Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredJdbcType);
/*      */ 
/* 1583 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1585 */       return retVal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getObject(int parameterIndex, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 1594 */     synchronized (checkClosed()) {
/* 1595 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1597 */       Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
/*      */ 
/* 1600 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1602 */       return retVal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getObject(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1611 */     synchronized (checkClosed()) {
/* 1612 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1615 */       Object retValue = rs.getObject(fixParameterName(parameterName));
/*      */ 
/* 1617 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1619 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getObject(String parameterName, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 1629 */     synchronized (checkClosed()) {
/* 1630 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1633 */       Object retValue = rs.getObject(fixParameterName(parameterName), map);
/*      */ 
/* 1635 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1637 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException
/*      */   {
/* 1643 */     synchronized (checkClosed()) {
/* 1644 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1647 */       Object retVal = ((ResultSetImpl)rs).getObject(mapOutputParameterIndexToRsIndex(parameterIndex), type);
/*      */ 
/* 1650 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1652 */       return retVal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
/* 1657 */     synchronized (checkClosed()) {
/* 1658 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1661 */       Object retValue = ((ResultSetImpl)rs).getObject(fixParameterName(parameterName), type);
/*      */ 
/* 1663 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1665 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods getOutputParameters(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1680 */     synchronized (checkClosed()) {
/* 1681 */       this.outputParamWasNull = false;
/*      */ 
/* 1683 */       if ((paramIndex == 1) && (this.callingStoredFunction) && (this.returnValueParam != null))
/*      */       {
/* 1685 */         return this.functionReturnValueResults;
/*      */       }
/*      */ 
/* 1688 */       if (this.outputParameterResults == null) {
/* 1689 */         if (this.paramInfo.numberOfParameters() == 0) {
/* 1690 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1694 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1698 */       return this.outputParameterResults;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException
/*      */   {
/* 1704 */     synchronized (checkClosed()) {
/* 1705 */       if (this.placeholderToParameterIndexMap == null) {
/* 1706 */         return (CallableStatementParamInfoJDBC3)this.paramInfo;
/*      */       }
/*      */ 
/* 1709 */       return new CallableStatementParamInfoJDBC3(this.paramInfo);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Ref getRef(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1717 */     synchronized (checkClosed()) {
/* 1718 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1720 */       Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1723 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1725 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Ref getRef(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1733 */     synchronized (checkClosed()) {
/* 1734 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1737 */       Ref retValue = rs.getRef(fixParameterName(parameterName));
/*      */ 
/* 1739 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1741 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public short getShort(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1749 */     synchronized (checkClosed()) {
/* 1750 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1752 */       short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1755 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1757 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public short getShort(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1766 */     synchronized (checkClosed()) {
/* 1767 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1770 */       short retValue = rs.getShort(fixParameterName(parameterName));
/*      */ 
/* 1772 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1774 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getString(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1783 */     synchronized (checkClosed()) {
/* 1784 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1786 */       String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1789 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1791 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getString(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1800 */     synchronized (checkClosed()) {
/* 1801 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1804 */       String retValue = rs.getString(fixParameterName(parameterName));
/*      */ 
/* 1806 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1808 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Time getTime(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1816 */     synchronized (checkClosed()) {
/* 1817 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1819 */       Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1822 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1824 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Time getTime(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1833 */     synchronized (checkClosed()) {
/* 1834 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1836 */       Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1839 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1841 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Time getTime(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1849 */     synchronized (checkClosed()) {
/* 1850 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1853 */       Time retValue = rs.getTime(fixParameterName(parameterName));
/*      */ 
/* 1855 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1857 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Time getTime(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1867 */     synchronized (checkClosed()) {
/* 1868 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1871 */       Time retValue = rs.getTime(fixParameterName(parameterName), cal);
/*      */ 
/* 1873 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1875 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1884 */     synchronized (checkClosed()) {
/* 1885 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1887 */       Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1890 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1892 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1901 */     synchronized (checkClosed()) {
/* 1902 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1904 */       Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1907 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1909 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1918 */     synchronized (checkClosed()) {
/* 1919 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1922 */       Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
/*      */ 
/* 1924 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1926 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1936 */     synchronized (checkClosed()) {
/* 1937 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1940 */       Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
/*      */ 
/* 1943 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1945 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public URL getURL(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1953 */     synchronized (checkClosed()) {
/* 1954 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1956 */       URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1959 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1961 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public URL getURL(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1969 */     synchronized (checkClosed()) {
/* 1970 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */ 
/* 1973 */       URL retValue = rs.getURL(fixParameterName(parameterName));
/*      */ 
/* 1975 */       this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1977 */       return retValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int mapOutputParameterIndexToRsIndex(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1984 */     synchronized (checkClosed()) {
/* 1985 */       if ((this.returnValueParam != null) && (paramIndex == 1)) {
/* 1986 */         return 1;
/*      */       }
/*      */ 
/* 1989 */       checkParameterIndexBounds(paramIndex);
/*      */ 
/* 1991 */       int localParamIndex = paramIndex - 1;
/*      */ 
/* 1993 */       if (this.placeholderToParameterIndexMap != null) {
/* 1994 */         localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */       }
/*      */ 
/* 1997 */       int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
/*      */ 
/* 1999 */       if (rsIndex == -2147483648) {
/* 2000 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + paramIndex + Messages.getString("CallableStatement.22"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2006 */       return rsIndex + 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2015 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/* 2016 */     paramDescriptor.desiredJdbcType = sqlType;
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 2024 */     registerOutParameter(parameterIndex, sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2033 */     checkIsOutputParam(parameterIndex);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2042 */     synchronized (checkClosed()) {
/* 2043 */       registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 2053 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2062 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
/*      */   }
/*      */ 
/*      */   private void retrieveOutParams()
/*      */     throws SQLException
/*      */   {
/* 2073 */     synchronized (checkClosed()) {
/* 2074 */       int numParameters = this.paramInfo.numberOfParameters();
/*      */ 
/* 2076 */       this.parameterIndexToRsIndex = new int[numParameters];
/*      */ 
/* 2078 */       for (int i = 0; i < numParameters; i++) {
/* 2079 */         this.parameterIndexToRsIndex[i] = -2147483648;
/*      */       }
/*      */ 
/* 2082 */       int localParamIndex = 0;
/*      */ 
/* 2084 */       if (numParameters > 0) {
/* 2085 */         StringBuffer outParameterQuery = new StringBuffer("SELECT ");
/*      */ 
/* 2087 */         boolean firstParam = true;
/* 2088 */         boolean hadOutputParams = false;
/*      */ 
/* 2090 */         Iterator paramIter = this.paramInfo.iterator();
/* 2091 */         while (paramIter.hasNext()) {
/* 2092 */           CallableStatementParam retrParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 2095 */           if (retrParamInfo.isOut) {
/* 2096 */             hadOutputParams = true;
/*      */ 
/* 2098 */             this.parameterIndexToRsIndex[retrParamInfo.index] = (localParamIndex++);
/*      */ 
/* 2100 */             if ((retrParamInfo.paramName == null) && (hasParametersView())) {
/* 2101 */               retrParamInfo.paramName = ("nullnp" + retrParamInfo.index);
/*      */             }
/*      */ 
/* 2104 */             String outParameterName = mangleParameterName(retrParamInfo.paramName);
/*      */ 
/* 2106 */             if (!firstParam)
/* 2107 */               outParameterQuery.append(",");
/*      */             else {
/* 2109 */               firstParam = false;
/*      */             }
/*      */ 
/* 2112 */             if (!outParameterName.startsWith("@")) {
/* 2113 */               outParameterQuery.append('@');
/*      */             }
/*      */ 
/* 2116 */             outParameterQuery.append(outParameterName);
/*      */           }
/*      */         }
/*      */ 
/* 2120 */         if (hadOutputParams)
/*      */         {
/* 2123 */           Statement outParameterStmt = null;
/* 2124 */           ResultSet outParamRs = null;
/*      */           try
/*      */           {
/* 2127 */             outParameterStmt = this.connection.createStatement();
/* 2128 */             outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
/*      */ 
/* 2130 */             this.outputParameterResults = ((ResultSetInternalMethods)outParamRs).copy();
/*      */ 
/* 2133 */             if (!this.outputParameterResults.next()) {
/* 2134 */               this.outputParameterResults.close();
/* 2135 */               this.outputParameterResults = null;
/*      */             }
/*      */           } finally {
/* 2138 */             if (outParameterStmt != null)
/* 2139 */               outParameterStmt.close();
/*      */           }
/*      */         }
/*      */         else {
/* 2143 */           this.outputParameterResults = null;
/*      */         }
/*      */       } else {
/* 2146 */         this.outputParameterResults = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2157 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(String parameterName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 2166 */     setBigDecimal(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2175 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBoolean(String parameterName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 2182 */     setBoolean(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setByte(String parameterName, byte x)
/*      */     throws SQLException
/*      */   {
/* 2189 */     setByte(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBytes(String parameterName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 2196 */     setBytes(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 2205 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x)
/*      */     throws SQLException
/*      */   {
/* 2213 */     setDate(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2222 */     setDate(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setDouble(String parameterName, double x)
/*      */     throws SQLException
/*      */   {
/* 2229 */     setDouble(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setFloat(String parameterName, float x)
/*      */     throws SQLException
/*      */   {
/* 2236 */     setFloat(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   private void setInOutParamsOnServer()
/*      */     throws SQLException
/*      */   {
/* 2243 */     synchronized (checkClosed()) {
/* 2244 */       if (this.paramInfo.numParameters > 0) {
/* 2245 */         Iterator paramIter = this.paramInfo.iterator();
/* 2246 */         while (paramIter.hasNext())
/*      */         {
/* 2248 */           CallableStatementParam inParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 2252 */           if ((inParamInfo.isOut) && (inParamInfo.isIn)) {
/* 2253 */             if ((inParamInfo.paramName == null) && (hasParametersView())) {
/* 2254 */               inParamInfo.paramName = ("nullnp" + inParamInfo.index);
/*      */             }
/*      */ 
/* 2257 */             String inOutParameterName = mangleParameterName(inParamInfo.paramName);
/* 2258 */             StringBuffer queryBuf = new StringBuffer(4 + inOutParameterName.length() + 1 + 1);
/*      */ 
/* 2260 */             queryBuf.append("SET ");
/* 2261 */             queryBuf.append(inOutParameterName);
/* 2262 */             queryBuf.append("=?");
/*      */ 
/* 2264 */             PreparedStatement setPstmt = null;
/*      */             try
/*      */             {
/* 2267 */               setPstmt = (PreparedStatement)this.connection.clientPrepareStatement(queryBuf.toString());
/*      */ 
/* 2270 */               byte[] parameterAsBytes = getBytesRepresentation(inParamInfo.index);
/*      */ 
/* 2273 */               if (parameterAsBytes != null) {
/* 2274 */                 if ((parameterAsBytes.length > 8) && (parameterAsBytes[0] == 95) && (parameterAsBytes[1] == 98) && (parameterAsBytes[2] == 105) && (parameterAsBytes[3] == 110) && (parameterAsBytes[4] == 97) && (parameterAsBytes[5] == 114) && (parameterAsBytes[6] == 121) && (parameterAsBytes[7] == 39))
/*      */                 {
/* 2283 */                   setPstmt.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
/*      */                 }
/*      */                 else {
/* 2286 */                   int sqlType = inParamInfo.desiredJdbcType;
/*      */ 
/* 2288 */                   switch (sqlType) {
/*      */                   case -7:
/*      */                   case -4:
/*      */                   case -3:
/*      */                   case -2:
/*      */                   case 2000:
/*      */                   case 2004:
/* 2295 */                     setPstmt.setBytes(1, parameterAsBytes);
/* 2296 */                     break;
/*      */                   default:
/* 2300 */                     setPstmt.setBytesNoEscape(1, parameterAsBytes);
/*      */                   }
/*      */                 }
/*      */               }
/* 2304 */               else setPstmt.setNull(1, 0);
/*      */ 
/* 2307 */               setPstmt.executeUpdate();
/*      */             } finally {
/* 2309 */               if (setPstmt != null)
/* 2310 */                 setPstmt.close();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInt(String parameterName, int x)
/*      */     throws SQLException
/*      */   {
/* 2323 */     setInt(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setLong(String parameterName, long x)
/*      */     throws SQLException
/*      */   {
/* 2330 */     setLong(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2337 */     setNull(getNamedParamIndex(parameterName, false), sqlType);
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2346 */     setNull(getNamedParamIndex(parameterName, false), sqlType, typeName);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2354 */     setObject(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 2363 */     setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void setOutParams()
/*      */     throws SQLException
/*      */   {
/* 2375 */     synchronized (checkClosed()) {
/* 2376 */       if (this.paramInfo.numParameters > 0) {
/* 2377 */         Iterator paramIter = this.paramInfo.iterator();
/* 2378 */         while (paramIter.hasNext()) {
/* 2379 */           CallableStatementParam outParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 2382 */           if ((!this.callingStoredFunction) && (outParamInfo.isOut))
/*      */           {
/* 2384 */             if ((outParamInfo.paramName == null) && (hasParametersView())) {
/* 2385 */               outParamInfo.paramName = ("nullnp" + outParamInfo.index);
/*      */             }
/*      */ 
/* 2388 */             String outParameterName = mangleParameterName(outParamInfo.paramName);
/*      */ 
/* 2390 */             int outParamIndex = 0;
/*      */ 
/* 2392 */             if (this.placeholderToParameterIndexMap == null) {
/* 2393 */               outParamIndex = outParamInfo.index + 1;
/*      */             }
/*      */             else {
/* 2396 */               boolean found = false;
/*      */ 
/* 2398 */               for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 2399 */                 if (this.placeholderToParameterIndexMap[i] == outParamInfo.index) {
/* 2400 */                   outParamIndex = i + 1;
/* 2401 */                   found = true;
/* 2402 */                   break;
/*      */                 }
/*      */               }
/*      */ 
/* 2406 */               if (!found) {
/* 2407 */                 throw SQLError.createSQLException("boo!", "S1000", this.connection.getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */ 
/* 2411 */             setBytesNoEscapeNoQuotes(outParamIndex, StringUtils.getBytes(outParameterName, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(String parameterName, short x)
/*      */     throws SQLException
/*      */   {
/* 2427 */     setShort(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setString(String parameterName, String x)
/*      */     throws SQLException
/*      */   {
/* 2435 */     setString(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2442 */     setTime(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2451 */     setTime(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2460 */     setTimestamp(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2469 */     setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setURL(String parameterName, URL val)
/*      */     throws SQLException
/*      */   {
/* 2476 */     setURL(getNamedParamIndex(parameterName, false), val);
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 2483 */     synchronized (checkClosed()) {
/* 2484 */       return this.outputParamWasNull;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] executeBatch() throws SQLException {
/* 2489 */     if (this.hasOutputParams) {
/* 2490 */       throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2494 */     return super.executeBatch();
/*      */   }
/*      */ 
/*      */   protected int getParameterIndexOffset() {
/* 2498 */     if (this.callingStoredFunction) {
/* 2499 */       return -1;
/*      */     }
/*      */ 
/* 2502 */     return super.getParameterIndexOffset();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/* 2506 */     setAsciiStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException
/*      */   {
/* 2511 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException
/*      */   {
/* 2516 */     setBinaryStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException
/*      */   {
/* 2521 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException
/*      */   {
/* 2526 */     setBlob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, InputStream inputStream) throws SQLException
/*      */   {
/* 2531 */     setBlob(getNamedParamIndex(parameterName, false), inputStream);
/*      */   }
/*      */ 
/*      */   public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException
/*      */   {
/* 2536 */     setBlob(getNamedParamIndex(parameterName, false), inputStream, length);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException
/*      */   {
/* 2541 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException
/*      */   {
/* 2546 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Clob x) throws SQLException
/*      */   {
/* 2551 */     setClob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException
/*      */   {
/* 2556 */     setClob(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */ 
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException
/*      */   {
/* 2561 */     setClob(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(String parameterName, Reader value) throws SQLException
/*      */   {
/* 2566 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value);
/*      */   }
/*      */ 
/*      */   public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException
/*      */   {
/* 2571 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value, length);
/*      */   }
/*      */ 
/*      */   private boolean checkReadOnlyProcedure()
/*      */     throws SQLException
/*      */   {
/* 2582 */     synchronized (checkClosed()) {
/* 2583 */       if (this.connection.getNoAccessToProcedureBodies()) {
/* 2584 */         return false;
/*      */       }
/*      */ 
/* 2587 */       if (this.paramInfo.isReadOnlySafeChecked) {
/* 2588 */         return this.paramInfo.isReadOnlySafeProcedure;
/*      */       }
/*      */ 
/* 2591 */       ResultSet rs = null;
/* 2592 */       java.sql.PreparedStatement ps = null;
/*      */       try
/*      */       {
/* 2595 */         String procName = extractProcedureName();
/*      */ 
/* 2597 */         String catalog = this.currentCatalog;
/*      */ 
/* 2599 */         if (procName.indexOf(".") != -1) {
/* 2600 */           catalog = procName.substring(0, procName.indexOf("."));
/*      */ 
/* 2602 */           if ((StringUtils.startsWithIgnoreCaseAndWs(catalog, "`")) && (catalog.trim().endsWith("`"))) {
/* 2603 */             catalog = catalog.substring(1, catalog.length() - 1);
/*      */           }
/*      */ 
/* 2606 */           procName = procName.substring(procName.indexOf(".") + 1);
/* 2607 */           procName = StringUtils.toString(StringUtils.stripEnclosure(StringUtils.getBytes(procName), "`", "`"));
/*      */         }
/*      */ 
/* 2610 */         ps = this.connection.prepareStatement("SELECT SQL_DATA_ACCESS FROM  information_schema.routines  WHERE routine_schema = ?  AND routine_name = ?");
/*      */ 
/* 2615 */         ps.setMaxRows(0);
/* 2616 */         ps.setFetchSize(0);
/*      */ 
/* 2618 */         ps.setString(1, catalog);
/* 2619 */         ps.setString(2, procName);
/* 2620 */         rs = ps.executeQuery();
/* 2621 */         if (rs.next()) {
/* 2622 */           String sqlDataAccess = rs.getString(1);
/* 2623 */           if (("READS SQL DATA".equalsIgnoreCase(sqlDataAccess)) || ("NO SQL".equalsIgnoreCase(sqlDataAccess)))
/*      */           {
/* 2625 */             synchronized (this.paramInfo) {
/* 2626 */               this.paramInfo.isReadOnlySafeChecked = true;
/* 2627 */               this.paramInfo.isReadOnlySafeProcedure = true;
/*      */             }
/* 2629 */             ??? = 1; jsr 30; return ???;
/*      */           }
/*      */         }
/*      */       } catch (SQLException e) {
/*      */       }
/*      */       finally {
/* 2635 */         jsr 6; } localObject3 = returnAddress; if (rs != null) {
/* 2636 */         rs.close();
/*      */       }
/* 2638 */       if (ps != null)
/* 2639 */         ps.close(); ret;
/*      */ 
/* 2643 */       this.paramInfo.isReadOnlySafeChecked = false;
/* 2644 */       this.paramInfo.isReadOnlySafeProcedure = false;
/*      */     }
/* 2646 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean checkReadOnlySafeStatement() throws SQLException
/*      */   {
/* 2651 */     return (super.checkReadOnlySafeStatement()) || (checkReadOnlyProcedure());
/*      */   }
/*      */ 
/*      */   private boolean hasParametersView() throws SQLException {
/* 2655 */     synchronized (checkClosed()) {
/*      */       try {
/* 2657 */         if (this.connection.versionMeetsMinimum(5, 5, 0)) {
/* 2658 */           java.sql.DatabaseMetaData dbmd1 = new DatabaseMetaDataUsingInfoSchema(this.connection, this.connection.getCatalog());
/* 2659 */           return ((DatabaseMetaDataUsingInfoSchema)dbmd1).gethasParametersView();
/*      */         }
/*      */ 
/* 2662 */         return false;
/*      */       } catch (SQLException e) {
/* 2664 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   63 */     if (Util.isJdbc4()) {
/*      */       try {
/*   65 */         JDBC_4_CSTMT_2_ARGS_CTOR = Class.forName("com.mysql.jdbc.JDBC4CallableStatement").getConstructor(new Class[] { MySQLConnection.class, CallableStatementParamInfo.class });
/*      */ 
/*   70 */         JDBC_4_CSTMT_4_ARGS_CTOR = Class.forName("com.mysql.jdbc.JDBC4CallableStatement").getConstructor(new Class[] { MySQLConnection.class, String.class, String.class, Boolean.TYPE });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   77 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   79 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   81 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*   84 */       JDBC_4_CSTMT_4_ARGS_CTOR = null;
/*   85 */       JDBC_4_CSTMT_2_ARGS_CTOR = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class CallableStatementParamInfoJDBC3 extends CallableStatement.CallableStatementParamInfo
/*      */     implements ParameterMetaData
/*      */   {
/*      */     CallableStatementParamInfoJDBC3(ResultSet paramTypesRs)
/*      */       throws SQLException
/*      */     {
/*  385 */       super(paramTypesRs);
/*      */     }
/*      */ 
/*      */     public CallableStatementParamInfoJDBC3(CallableStatement.CallableStatementParamInfo paramInfo) {
/*  389 */       super(paramInfo);
/*      */     }
/*      */ 
/*      */     public boolean isWrapperFor(Class<?> iface)
/*      */       throws SQLException
/*      */     {
/*  408 */       CallableStatement.this.checkClosed();
/*      */ 
/*  412 */       return iface.isInstance(this);
/*      */     }
/*      */ 
/*      */     public Object unwrap(Class<?> iface)
/*      */       throws SQLException
/*      */     {
/*      */       try
/*      */       {
/*  433 */         return Util.cast(iface, this); } catch (ClassCastException cce) {
/*      */       }
/*  435 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", CallableStatement.this.getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class CallableStatementParamInfo
/*      */   {
/*      */     String catalogInUse;
/*      */     boolean isFunctionCall;
/*      */     String nativeSql;
/*      */     int numParameters;
/*      */     List<CallableStatement.CallableStatementParam> parameterList;
/*      */     Map<String, CallableStatement.CallableStatementParam> parameterMap;
/*  155 */     boolean isReadOnlySafeProcedure = false;
/*      */ 
/*  160 */     boolean isReadOnlySafeChecked = false;
/*      */ 
/*      */     CallableStatementParamInfo(CallableStatementParamInfo fullParamInfo)
/*      */     {
/*  170 */       this.nativeSql = CallableStatement.this.originalSql;
/*  171 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  172 */       this.isFunctionCall = fullParamInfo.isFunctionCall;
/*      */ 
/*  174 */       int[] localParameterMap = CallableStatement.this.placeholderToParameterIndexMap;
/*  175 */       int parameterMapLength = localParameterMap.length;
/*      */ 
/*  177 */       this.isReadOnlySafeProcedure = fullParamInfo.isReadOnlySafeProcedure;
/*  178 */       this.isReadOnlySafeChecked = fullParamInfo.isReadOnlySafeChecked;
/*  179 */       this.parameterList = new ArrayList(fullParamInfo.numParameters);
/*  180 */       this.parameterMap = new HashMap(fullParamInfo.numParameters);
/*      */ 
/*  182 */       if (this.isFunctionCall)
/*      */       {
/*  184 */         this.parameterList.add(fullParamInfo.parameterList.get(0));
/*      */       }
/*      */ 
/*  187 */       int offset = this.isFunctionCall ? 1 : 0;
/*      */ 
/*  189 */       for (int i = 0; i < parameterMapLength; i++) {
/*  190 */         if (localParameterMap[i] != 0) {
/*  191 */           CallableStatement.CallableStatementParam param = (CallableStatement.CallableStatementParam)fullParamInfo.parameterList.get(localParameterMap[i] + offset);
/*      */ 
/*  193 */           this.parameterList.add(param);
/*  194 */           this.parameterMap.put(param.paramName, param);
/*      */         }
/*      */       }
/*      */ 
/*  198 */       this.numParameters = this.parameterList.size();
/*      */     }
/*      */ 
/*      */     CallableStatementParamInfo(ResultSet paramTypesRs)
/*      */       throws SQLException
/*      */     {
/*  204 */       boolean hadRows = paramTypesRs.last();
/*      */ 
/*  206 */       this.nativeSql = CallableStatement.this.originalSql;
/*  207 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  208 */       this.isFunctionCall = CallableStatement.this.callingStoredFunction;
/*      */ 
/*  210 */       if (hadRows) {
/*  211 */         this.numParameters = paramTypesRs.getRow();
/*      */ 
/*  213 */         this.parameterList = new ArrayList(this.numParameters);
/*  214 */         this.parameterMap = new HashMap(this.numParameters);
/*      */ 
/*  216 */         paramTypesRs.beforeFirst();
/*      */ 
/*  218 */         addParametersFromDBMD(paramTypesRs);
/*      */       } else {
/*  220 */         this.numParameters = 0;
/*      */       }
/*      */ 
/*  223 */       if (this.isFunctionCall)
/*  224 */         this.numParameters += 1;
/*      */     }
/*      */ 
/*      */     private void addParametersFromDBMD(ResultSet paramTypesRs)
/*      */       throws SQLException
/*      */     {
/*  230 */       int i = 0;
/*      */ 
/*  232 */       while (paramTypesRs.next()) {
/*  233 */         String paramName = paramTypesRs.getString(4);
/*  234 */         int inOutModifier = paramTypesRs.getInt(5);
/*      */ 
/*  236 */         boolean isOutParameter = false;
/*  237 */         boolean isInParameter = false;
/*      */ 
/*  239 */         if ((i == 0) && (this.isFunctionCall)) {
/*  240 */           isOutParameter = true;
/*  241 */           isInParameter = false;
/*  242 */         } else if (inOutModifier == 2) {
/*  243 */           isOutParameter = true;
/*  244 */           isInParameter = true;
/*  245 */         } else if (inOutModifier == 1) {
/*  246 */           isOutParameter = false;
/*  247 */           isInParameter = true;
/*  248 */         } else if (inOutModifier == 4) {
/*  249 */           isOutParameter = true;
/*  250 */           isInParameter = false;
/*      */         }
/*      */ 
/*  253 */         int jdbcType = paramTypesRs.getInt(6);
/*  254 */         String typeName = paramTypesRs.getString(7);
/*  255 */         int precision = paramTypesRs.getInt(8);
/*  256 */         int scale = paramTypesRs.getInt(10);
/*  257 */         short nullability = paramTypesRs.getShort(12);
/*      */ 
/*  259 */         CallableStatement.CallableStatementParam paramInfoToAdd = new CallableStatement.CallableStatementParam(paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
/*      */ 
/*  264 */         this.parameterList.add(paramInfoToAdd);
/*  265 */         this.parameterMap.put(paramName, paramInfoToAdd);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void checkBounds(int paramIndex) throws SQLException {
/*  270 */       int localParamIndex = paramIndex - 1;
/*      */ 
/*  272 */       if ((paramIndex < 0) || (localParamIndex >= this.numParameters))
/*  273 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.11") + paramIndex + Messages.getString("CallableStatement.12") + this.numParameters + Messages.getString("CallableStatement.13"), "S1009", CallableStatement.this.getExceptionInterceptor());
/*      */     }
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  286 */       return super.clone();
/*      */     }
/*      */ 
/*      */     CallableStatement.CallableStatementParam getParameter(int index) {
/*  290 */       return (CallableStatement.CallableStatementParam)this.parameterList.get(index);
/*      */     }
/*      */ 
/*      */     CallableStatement.CallableStatementParam getParameter(String name) {
/*  294 */       return (CallableStatement.CallableStatementParam)this.parameterMap.get(name);
/*      */     }
/*      */ 
/*      */     public String getParameterClassName(int arg0) throws SQLException {
/*  298 */       String mysqlTypeName = getParameterTypeName(arg0);
/*      */ 
/*  300 */       boolean isBinaryOrBlob = (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BLOB") != -1) || (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BINARY") != -1);
/*      */ 
/*  303 */       boolean isUnsigned = StringUtils.indexOfIgnoreCase(mysqlTypeName, "UNSIGNED") != -1;
/*      */ 
/*  305 */       int mysqlTypeIfKnown = 0;
/*      */ 
/*  307 */       if (StringUtils.startsWithIgnoreCase(mysqlTypeName, "MEDIUMINT")) {
/*  308 */         mysqlTypeIfKnown = 9;
/*      */       }
/*      */ 
/*  311 */       return ResultSetMetaData.getClassNameForJavaType(getParameterType(arg0), isUnsigned, mysqlTypeIfKnown, isBinaryOrBlob, false);
/*      */     }
/*      */ 
/*      */     public int getParameterCount() throws SQLException
/*      */     {
/*  316 */       if (this.parameterList == null) {
/*  317 */         return 0;
/*      */       }
/*      */ 
/*  320 */       return this.parameterList.size();
/*      */     }
/*      */ 
/*      */     public int getParameterMode(int arg0) throws SQLException {
/*  324 */       checkBounds(arg0);
/*      */ 
/*  326 */       return getParameter(arg0 - 1).inOutModifier;
/*      */     }
/*      */ 
/*      */     public int getParameterType(int arg0) throws SQLException {
/*  330 */       checkBounds(arg0);
/*      */ 
/*  332 */       return getParameter(arg0 - 1).jdbcType;
/*      */     }
/*      */ 
/*      */     public String getParameterTypeName(int arg0) throws SQLException {
/*  336 */       checkBounds(arg0);
/*      */ 
/*  338 */       return getParameter(arg0 - 1).typeName;
/*      */     }
/*      */ 
/*      */     public int getPrecision(int arg0) throws SQLException {
/*  342 */       checkBounds(arg0);
/*      */ 
/*  344 */       return getParameter(arg0 - 1).precision;
/*      */     }
/*      */ 
/*      */     public int getScale(int arg0) throws SQLException {
/*  348 */       checkBounds(arg0);
/*      */ 
/*  350 */       return getParameter(arg0 - 1).scale;
/*      */     }
/*      */ 
/*      */     public int isNullable(int arg0) throws SQLException {
/*  354 */       checkBounds(arg0);
/*      */ 
/*  356 */       return getParameter(arg0 - 1).nullability;
/*      */     }
/*      */ 
/*      */     public boolean isSigned(int arg0) throws SQLException {
/*  360 */       checkBounds(arg0);
/*      */ 
/*  362 */       return false;
/*      */     }
/*      */ 
/*      */     Iterator<CallableStatement.CallableStatementParam> iterator() {
/*  366 */       return this.parameterList.iterator();
/*      */     }
/*      */ 
/*      */     int numberOfParameters() {
/*  370 */       return this.numParameters;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static class CallableStatementParam
/*      */   {
/*      */     int desiredJdbcType;
/*      */     int index;
/*      */     int inOutModifier;
/*      */     boolean isIn;
/*      */     boolean isOut;
/*      */     int jdbcType;
/*      */     short nullability;
/*      */     String paramName;
/*      */     int precision;
/*      */     int scale;
/*      */     String typeName;
/*      */ 
/*      */     CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier)
/*      */     {
/*  115 */       this.paramName = name;
/*  116 */       this.isIn = in;
/*  117 */       this.isOut = out;
/*  118 */       this.index = idx;
/*      */ 
/*  120 */       this.jdbcType = jdbcType;
/*  121 */       this.typeName = typeName;
/*  122 */       this.precision = precision;
/*  123 */       this.scale = scale;
/*  124 */       this.nullability = nullability;
/*  125 */       this.inOutModifier = inOutModifier;
/*      */     }
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  134 */       return super.clone();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.CallableStatement
 * JD-Core Version:    0.6.0
 */