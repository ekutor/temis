/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.List;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ 
/*      */ public class ServerPreparedStatement extends PreparedStatement
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_SPS_CTOR;
/*      */   protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
/*  252 */   private boolean hasOnDuplicateKeyUpdate = false;
/*      */ 
/*  283 */   private boolean detectedLongParameterSwitch = false;
/*      */   private int fieldCount;
/*  292 */   private boolean invalid = false;
/*      */   private SQLException invalidationException;
/*      */   private Buffer outByteBuffer;
/*      */   private BindValue[] parameterBindings;
/*      */   private Field[] parameterFields;
/*      */   private Field[] resultFields;
/*  309 */   private boolean sendTypesToServer = false;
/*      */   private long serverStatementId;
/*  315 */   private int stringTypeCode = 254;
/*      */   private boolean serverNeedsResetBeforeEachExecution;
/*  577 */   protected boolean isCached = false;
/*      */   private boolean useAutoSlowLog;
/*      */   private Calendar serverTzCalendar;
/*      */   private Calendar defaultTzCalendar;
/* 2838 */   private boolean hasCheckedRewrite = false;
/* 2839 */   private boolean canRewrite = false;
/*      */ 
/* 2895 */   private int locationOfOnDuplicateKeyUpdate = -2;
/*      */ 
/*      */   private void storeTime(Buffer intoBuf, Time tm)
/*      */     throws SQLException
/*      */   {
/*  256 */     intoBuf.ensureCapacity(9);
/*  257 */     intoBuf.writeByte(8);
/*  258 */     intoBuf.writeByte(0);
/*  259 */     intoBuf.writeLong(0L);
/*      */ 
/*  261 */     Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/*  263 */     synchronized (sessionCalendar) {
/*  264 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try {
/*  266 */         sessionCalendar.setTime(tm);
/*  267 */         intoBuf.writeByte((byte)sessionCalendar.get(11));
/*  268 */         intoBuf.writeByte((byte)sessionCalendar.get(12));
/*  269 */         intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */       }
/*      */       finally
/*      */       {
/*  273 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static ServerPreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  329 */     if (!Util.isJdbc4())
/*  330 */       return new ServerPreparedStatement(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*      */     Throwable target;
/*      */     try
/*      */     {
/*  335 */       return (ServerPreparedStatement)JDBC_4_SPS_CTOR.newInstance(new Object[] { conn, sql, catalog, Integer.valueOf(resultSetType), Integer.valueOf(resultSetConcurrency) });
/*      */     }
/*      */     catch (IllegalArgumentException e)
/*      */     {
/*  339 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (InstantiationException e) {
/*  341 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (IllegalAccessException e) {
/*  343 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (InvocationTargetException e) {
/*  345 */       target = e.getTargetException();
/*      */ 
/*  347 */       if ((target instanceof SQLException)) {
/*  348 */         throw ((SQLException)target);
/*      */       }
/*      */     }
/*  351 */     throw new SQLException(target.toString(), "S1000");
/*      */   }
/*      */ 
/*      */   protected ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  371 */     super(conn, catalog);
/*      */ 
/*  373 */     checkNullOrEmptyQuery(sql);
/*      */ 
/*  375 */     this.hasOnDuplicateKeyUpdate = containsOnDuplicateKeyInString(sql);
/*      */ 
/*  377 */     int startOfStatement = findStartOfStatement(sql);
/*      */ 
/*  379 */     this.firstCharOfStmt = StringUtils.firstAlphaCharUc(sql, startOfStatement);
/*      */ 
/*  381 */     if (this.connection.versionMeetsMinimum(5, 0, 0)) {
/*  382 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(5, 0, 3));
/*      */     }
/*      */     else {
/*  385 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(4, 1, 10));
/*      */     }
/*      */ 
/*  389 */     this.useAutoSlowLog = this.connection.getAutoSlowLog();
/*  390 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*  391 */     int lim_id = StringUtils.indexOfIgnoreCase(sql, "LIMIT");
/*  392 */     if (lim_id != -1) {
/*  393 */       boolean hasPreviosIdChar = false;
/*  394 */       boolean hasFollowingIdChar = false;
/*  395 */       if ((lim_id > 0) && ((sql.charAt(lim_id - 1) == '`') || (StringUtils.isValidIdChar(sql.charAt(lim_id - 1)))))
/*      */       {
/*  399 */         hasPreviosIdChar = true;
/*      */       }
/*  401 */       if ((lim_id + 5 < sql.length()) && ((sql.charAt(lim_id + 5) == '`') || (StringUtils.isValidIdChar(sql.charAt(lim_id + 5)))))
/*      */       {
/*  405 */         hasFollowingIdChar = true;
/*      */       }
/*  407 */       if ((!hasPreviosIdChar) && (!hasFollowingIdChar))
/*  408 */         this.hasLimitClause = true;
/*      */     }
/*      */     else {
/*  411 */       this.hasLimitClause = false;
/*      */     }
/*      */ 
/*  415 */     String statementComment = this.connection.getStatementComment();
/*      */ 
/*  417 */     this.originalSql = ("/* " + statementComment + " */ " + sql);
/*      */ 
/*  420 */     if (this.connection.versionMeetsMinimum(4, 1, 2))
/*  421 */       this.stringTypeCode = 253;
/*      */     else {
/*  423 */       this.stringTypeCode = 254;
/*      */     }
/*      */     try
/*      */     {
/*  427 */       serverPrepare(sql);
/*      */     } catch (SQLException sqlEx) {
/*  429 */       realClose(false, true);
/*      */ 
/*  431 */       throw sqlEx;
/*      */     } catch (Exception ex) {
/*  433 */       realClose(false, true);
/*      */ 
/*  435 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/*  437 */       sqlEx.initCause(ex);
/*      */ 
/*  439 */       throw sqlEx;
/*      */     }
/*      */ 
/*  442 */     setResultSetType(resultSetType);
/*  443 */     setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/*  445 */     this.parameterTypes = new int[this.parameterCount];
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  457 */     synchronized (checkClosed())
/*      */     {
/*  459 */       if (this.batchedArgs == null) {
/*  460 */         this.batchedArgs = new ArrayList();
/*      */       }
/*      */ 
/*  463 */       this.batchedArgs.add(new BatchedBindValues(this.parameterBindings));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException
/*      */   {
/*  469 */     synchronized (checkClosed())
/*      */     {
/*  471 */       PreparedStatement pStmtForSub = null;
/*      */       try
/*      */       {
/*  474 */         pStmtForSub = PreparedStatement.getInstance(this.connection, this.originalSql, this.currentCatalog);
/*      */ 
/*  477 */         int numParameters = pStmtForSub.parameterCount;
/*  478 */         int ourNumParameters = this.parameterCount;
/*      */ 
/*  480 */         for (int i = 0; (i < numParameters) && (i < ourNumParameters); i++) {
/*  481 */           if (this.parameterBindings[i] != null) {
/*  482 */             if (this.parameterBindings[i].isNull) {
/*  483 */               pStmtForSub.setNull(i + 1, 0);
/*      */             } else {
/*  485 */               BindValue bindValue = this.parameterBindings[i];
/*      */ 
/*  490 */               switch (bindValue.bufferType)
/*      */               {
/*      */               case 1:
/*  493 */                 pStmtForSub.setByte(i + 1, (byte)(int)bindValue.longBinding);
/*  494 */                 break;
/*      */               case 2:
/*  496 */                 pStmtForSub.setShort(i + 1, (short)(int)bindValue.longBinding);
/*  497 */                 break;
/*      */               case 3:
/*  499 */                 pStmtForSub.setInt(i + 1, (int)bindValue.longBinding);
/*  500 */                 break;
/*      */               case 8:
/*  502 */                 pStmtForSub.setLong(i + 1, bindValue.longBinding);
/*  503 */                 break;
/*      */               case 4:
/*  505 */                 pStmtForSub.setFloat(i + 1, bindValue.floatBinding);
/*  506 */                 break;
/*      */               case 5:
/*  508 */                 pStmtForSub.setDouble(i + 1, bindValue.doubleBinding);
/*      */ 
/*  510 */                 break;
/*      */               case 6:
/*      */               case 7:
/*      */               default:
/*  512 */                 pStmtForSub.setObject(i + 1, this.parameterBindings[i].value);
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  520 */         i = pStmtForSub.asSql(quoteStreamsAndUnknowns); jsr 16; return i;
/*      */       } finally {
/*  522 */         jsr 6; } localObject2 = returnAddress; if (pStmtForSub != null)
/*      */         try {
/*  524 */           pStmtForSub.close();
/*      */         } catch (SQLException sqlEx) {
/*      */         }
/*  527 */       ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected MySQLConnection checkClosed()
/*      */     throws SQLException
/*      */   {
/*  539 */     if (this.invalid) {
/*  540 */       throw this.invalidationException;
/*      */     }
/*      */ 
/*  543 */     return super.checkClosed();
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  550 */     synchronized (checkClosed()) {
/*  551 */       clearParametersInternal(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void clearParametersInternal(boolean clearServerParameters) throws SQLException
/*      */   {
/*  557 */     boolean hadLongData = false;
/*      */ 
/*  559 */     if (this.parameterBindings != null) {
/*  560 */       for (int i = 0; i < this.parameterCount; i++) {
/*  561 */         if ((this.parameterBindings[i] != null) && (this.parameterBindings[i].isLongData))
/*      */         {
/*  563 */           hadLongData = true;
/*      */         }
/*      */ 
/*  566 */         this.parameterBindings[i].reset();
/*      */       }
/*      */     }
/*      */ 
/*  570 */     if ((clearServerParameters) && (hadLongData)) {
/*  571 */       serverResetStatement();
/*      */ 
/*  573 */       this.detectedLongParameterSwitch = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setClosed(boolean flag)
/*      */   {
/*  586 */     this.isClosed = flag;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  594 */       synchronized (checkClosed()) {
/*  595 */         if ((this.isCached) && (!this.isClosed)) {
/*  596 */           clearParameters();
/*      */ 
/*  598 */           this.isClosed = true;
/*      */ 
/*  600 */           this.connection.recachePreparedStatement(this);
/*  601 */           return;
/*      */         }
/*      */ 
/*  604 */         realClose(true, true);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  607 */       if ("08003".equals(sqlEx.getSQLState())) {
/*  608 */         return;
/*      */       }
/*      */ 
/*  611 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void dumpCloseForTestcase() throws SQLException {
/*  616 */     synchronized (checkClosed()) {
/*  617 */       StringBuffer buf = new StringBuffer();
/*  618 */       this.connection.generateConnectionCommentBlock(buf);
/*  619 */       buf.append("DEALLOCATE PREPARE debug_stmt_");
/*  620 */       buf.append(this.statementId);
/*  621 */       buf.append(";\n");
/*      */ 
/*  623 */       this.connection.dumpTestcaseQuery(buf.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void dumpExecuteForTestcase() throws SQLException {
/*  628 */     synchronized (checkClosed()) {
/*  629 */       StringBuffer buf = new StringBuffer();
/*      */ 
/*  631 */       for (int i = 0; i < this.parameterCount; i++) {
/*  632 */         this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  634 */         buf.append("SET @debug_stmt_param");
/*  635 */         buf.append(this.statementId);
/*  636 */         buf.append("_");
/*  637 */         buf.append(i);
/*  638 */         buf.append("=");
/*      */ 
/*  640 */         if (this.parameterBindings[i].isNull)
/*  641 */           buf.append("NULL");
/*      */         else {
/*  643 */           buf.append(this.parameterBindings[i].toString(true));
/*      */         }
/*      */ 
/*  646 */         buf.append(";\n");
/*      */       }
/*      */ 
/*  649 */       this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  651 */       buf.append("EXECUTE debug_stmt_");
/*  652 */       buf.append(this.statementId);
/*      */ 
/*  654 */       if (this.parameterCount > 0) {
/*  655 */         buf.append(" USING ");
/*  656 */         for (int i = 0; i < this.parameterCount; i++) {
/*  657 */           if (i > 0) {
/*  658 */             buf.append(", ");
/*      */           }
/*      */ 
/*  661 */           buf.append("@debug_stmt_param");
/*  662 */           buf.append(this.statementId);
/*  663 */           buf.append("_");
/*  664 */           buf.append(i);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  669 */       buf.append(";\n");
/*      */ 
/*  671 */       this.connection.dumpTestcaseQuery(buf.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void dumpPrepareForTestcase() throws SQLException {
/*  676 */     synchronized (checkClosed()) {
/*  677 */       StringBuffer buf = new StringBuffer(this.originalSql.length() + 64);
/*      */ 
/*  679 */       this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  681 */       buf.append("PREPARE debug_stmt_");
/*  682 */       buf.append(this.statementId);
/*  683 */       buf.append(" FROM \"");
/*  684 */       buf.append(this.originalSql);
/*  685 */       buf.append("\";\n");
/*      */ 
/*  687 */       this.connection.dumpTestcaseQuery(buf.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int[] executeBatchSerially(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/*      */     MySQLConnection locallyScopedConn;
/*      */     BindValue[] oldBindValues;
/*  692 */     synchronized (checkClosed()) {
/*  693 */       locallyScopedConn = this.connection;
/*      */ 
/*  696 */       if (locallyScopedConn.isReadOnly()) {
/*  697 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  703 */       clearWarnings();
/*      */ 
/*  707 */       oldBindValues = this.parameterBindings;
/*      */     }
/*      */     try {
/*  710 */       int[] updateCounts = null;
/*      */ 
/*  712 */       if (this.batchedArgs != null) {
/*  713 */         nbrCommands = this.batchedArgs.size();
/*  714 */         updateCounts = new int[nbrCommands];
/*      */ 
/*  716 */         if (this.retrieveGeneratedKeys) {
/*  717 */           this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */         }
/*      */ 
/*  720 */         for (int i = 0; i < nbrCommands; i++) {
/*  721 */           updateCounts[i] = -3;
/*      */         }
/*      */ 
/*  724 */         SQLException sqlEx = null;
/*      */ 
/*  726 */         int commandIndex = 0;
/*      */ 
/*  728 */         BindValue[] previousBindValuesForBatch = null;
/*      */ 
/*  730 */         StatementImpl.CancelTask timeoutTask = null;
/*      */         try
/*      */         {
/*  733 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0)))
/*      */           {
/*  736 */             timeoutTask = new StatementImpl.CancelTask(this, this);
/*  737 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */ 
/*  741 */           for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*  742 */             Object arg = this.batchedArgs.get(commandIndex);
/*      */ 
/*  744 */             if ((arg instanceof String)) {
/*  745 */               updateCounts[commandIndex] = executeUpdate((String)arg);
/*      */             } else {
/*  747 */               this.parameterBindings = ((BatchedBindValues)arg).batchedParameterValues;
/*      */               try
/*      */               {
/*  754 */                 if (previousBindValuesForBatch != null) {
/*  755 */                   for (int j = 0; j < this.parameterBindings.length; j++) {
/*  756 */                     if (this.parameterBindings[j].bufferType != previousBindValuesForBatch[j].bufferType) {
/*  757 */                       this.sendTypesToServer = true;
/*      */ 
/*  759 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 try
/*      */                 {
/*  765 */                   updateCounts[commandIndex] = executeUpdate(false, true);
/*      */                 } finally {
/*  767 */                   previousBindValuesForBatch = this.parameterBindings;
/*      */                 }
/*      */ 
/*  770 */                 if (this.retrieveGeneratedKeys) {
/*  771 */                   ResultSet rs = null;
/*      */                   try
/*      */                   {
/*  783 */                     rs = getGeneratedKeysInternal();
/*      */ 
/*  785 */                     while (rs.next()) {
/*  786 */                       this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/*  791 */                     if (rs != null)
/*  792 */                       rs.close();
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (SQLException ex) {
/*  797 */                 updateCounts[commandIndex] = -3;
/*      */ 
/*  799 */                 if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */                 {
/*  803 */                   sqlEx = ex;
/*      */                 } else {
/*  805 */                   int[] newUpdateCounts = new int[commandIndex];
/*  806 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*  809 */                   throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  817 */           if (timeoutTask != null) {
/*  818 */             timeoutTask.cancel();
/*      */ 
/*  820 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */ 
/*  823 */           resetCancelledState();
/*      */         }
/*      */ 
/*  826 */         if (sqlEx != null) {
/*  827 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  833 */       int nbrCommands = updateCounts != null ? updateCounts : new int[0]; jsr 16; monitorexit; return nbrCommands;
/*      */     } finally {
/*  835 */       jsr 6; } localObject8 = returnAddress; this.parameterBindings = oldBindValues;
/*  836 */     this.sendTypesToServer = true;
/*      */ 
/*  838 */     clearBatch(); ret;
/*      */ 
/*  840 */     localObject9 = finally;
/*      */ 
/*  840 */     monitorexit; throw localObject9;
/*      */   }
/*      */ 
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/*  852 */     synchronized (checkClosed()) {
/*  853 */       this.numberOfExecutions += 1;
/*      */       try
/*      */       {
/*  857 */         return serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadataFromCache);
/*      */       }
/*      */       catch (SQLException sqlEx)
/*      */       {
/*  861 */         if (this.connection.getEnablePacketDebug()) {
/*  862 */           this.connection.getIO().dumpPacketRingBuffer();
/*      */         }
/*      */ 
/*  865 */         if (this.connection.getDumpQueriesOnException()) {
/*  866 */           String extractedSql = toString();
/*  867 */           StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/*  869 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/*      */ 
/*  871 */           messageBuf.append(extractedSql);
/*  872 */           messageBuf.append("\n\n");
/*      */ 
/*  874 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */ 
/*  878 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/*  880 */         if (this.connection.getEnablePacketDebug()) {
/*  881 */           this.connection.getIO().dumpPacketRingBuffer();
/*      */         }
/*      */ 
/*  884 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/*  887 */         if (this.connection.getDumpQueriesOnException()) {
/*  888 */           String extractedSql = toString();
/*  889 */           StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/*  891 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/*      */ 
/*  893 */           messageBuf.append(extractedSql);
/*  894 */           messageBuf.append("\n\n");
/*      */ 
/*  896 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */ 
/*  900 */         sqlEx.initCause(ex);
/*      */ 
/*  902 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/*  911 */     return null;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/*  921 */     return null;
/*      */   }
/*      */ 
/*      */   protected BindValue getBinding(int parameterIndex, boolean forLongData)
/*      */     throws SQLException
/*      */   {
/*  935 */     synchronized (checkClosed())
/*      */     {
/*  937 */       if (this.parameterBindings.length == 0) {
/*  938 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.8"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  943 */       parameterIndex--;
/*      */ 
/*  945 */       if ((parameterIndex < 0) || (parameterIndex >= this.parameterBindings.length))
/*      */       {
/*  947 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + this.parameterBindings.length, "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/*  955 */       if (this.parameterBindings[parameterIndex] == null) {
/*  956 */         this.parameterBindings[parameterIndex] = new BindValue();
/*      */       }
/*  958 */       else if ((this.parameterBindings[parameterIndex].isLongData) && (!forLongData))
/*      */       {
/*  960 */         this.detectedLongParameterSwitch = true;
/*      */       }
/*      */ 
/*  964 */       this.parameterBindings[parameterIndex].isSet = true;
/*  965 */       this.parameterBindings[parameterIndex].boundBeforeExecutionNum = this.numberOfExecutions;
/*      */ 
/*  967 */       return this.parameterBindings[parameterIndex];
/*      */     }
/*      */   }
/*      */ 
/*      */   public BindValue[] getParameterBindValues()
/*      */   {
/*  978 */     return this.parameterBindings;
/*      */   }
/*      */ 
/*      */   byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  985 */     synchronized (checkClosed()) {
/*  986 */       BindValue bindValue = getBinding(parameterIndex, false);
/*      */ 
/*  988 */       if (bindValue.isNull)
/*  989 */         return null;
/*  990 */       if (bindValue.isLongData) {
/*  991 */         throw SQLError.notImplemented();
/*      */       }
/*  993 */       if (this.outByteBuffer == null) {
/*  994 */         this.outByteBuffer = new Buffer(this.connection.getNetBufferLength());
/*      */       }
/*      */ 
/*  998 */       this.outByteBuffer.clear();
/*      */ 
/* 1000 */       int originalPosition = this.outByteBuffer.getPosition();
/*      */ 
/* 1002 */       storeBinding(this.outByteBuffer, bindValue, this.connection.getIO());
/*      */ 
/* 1004 */       int newPosition = this.outByteBuffer.getPosition();
/*      */ 
/* 1006 */       int length = newPosition - originalPosition;
/*      */ 
/* 1008 */       byte[] valueAsBytes = new byte[length];
/*      */ 
/* 1010 */       System.arraycopy(this.outByteBuffer.getByteBuffer(), originalPosition, valueAsBytes, 0, length);
/*      */ 
/* 1013 */       return valueAsBytes;
/*      */     }
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 1022 */     synchronized (checkClosed())
/*      */     {
/* 1024 */       if (this.resultFields == null) {
/* 1025 */         return null;
/*      */       }
/*      */ 
/* 1028 */       return new ResultSetMetaData(this.resultFields, this.connection.getUseOldAliasMetadataBehavior(), getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 1037 */     synchronized (checkClosed())
/*      */     {
/* 1039 */       if (this.parameterMetaData == null) {
/* 1040 */         this.parameterMetaData = new MysqlParameterMetadata(this.parameterFields, this.parameterCount, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1044 */       return this.parameterMetaData;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isNull(int paramIndex)
/*      */   {
/* 1052 */     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/*      */     MySQLConnection locallyScopedConn;
/*      */     try
/*      */     {
/* 1070 */       locallyScopedConn = checkClosed();
/*      */     } catch (SQLException sqlEx) {
/* 1072 */       return;
/*      */     }
/*      */ 
/* 1075 */     synchronized (locallyScopedConn)
/*      */     {
/* 1077 */       if (this.connection != null) {
/* 1078 */         if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1079 */           dumpCloseForTestcase();
/*      */         }
/*      */ 
/* 1093 */         SQLException exceptionDuringClose = null;
/*      */ 
/* 1095 */         if ((calledExplicitly) && (!this.connection.isClosed())) {
/* 1096 */           synchronized (this.connection)
/*      */           {
/*      */             try {
/* 1099 */               MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1101 */               Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1103 */               packet.writeByte(25);
/* 1104 */               packet.writeLong(this.serverStatementId);
/*      */ 
/* 1106 */               mysql.sendCommand(25, null, packet, true, null, 0);
/*      */             }
/*      */             catch (SQLException sqlEx) {
/* 1109 */               exceptionDuringClose = sqlEx;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1114 */         super.realClose(calledExplicitly, closeOpenResults);
/*      */ 
/* 1116 */         clearParametersInternal(false);
/* 1117 */         this.parameterBindings = null;
/*      */ 
/* 1119 */         this.parameterFields = null;
/* 1120 */         this.resultFields = null;
/*      */ 
/* 1122 */         if (exceptionDuringClose != null)
/* 1123 */           throw exceptionDuringClose;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void rePrepare()
/*      */     throws SQLException
/*      */   {
/* 1137 */     synchronized (checkClosed()) {
/* 1138 */       this.invalidationException = null;
/*      */       try
/*      */       {
/* 1141 */         serverPrepare(this.originalSql);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1144 */         this.invalidationException = sqlEx;
/*      */       } catch (Exception ex) {
/* 1146 */         this.invalidationException = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 1148 */         this.invalidationException.initCause(ex);
/*      */       }
/*      */ 
/* 1151 */       if (this.invalidationException != null) {
/* 1152 */         this.invalid = true;
/*      */ 
/* 1154 */         this.parameterBindings = null;
/*      */ 
/* 1156 */         this.parameterFields = null;
/* 1157 */         this.resultFields = null;
/*      */ 
/* 1159 */         if (this.results != null) {
/*      */           try {
/* 1161 */             this.results.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */         }
/* 1167 */         if (this.generatedKeysResults != null)
/*      */           try {
/* 1169 */             this.generatedKeysResults.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */         try
/*      */         {
/* 1176 */           closeAllOpenResults();
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/* 1181 */         if (this.connection != null) {
/* 1182 */           if (this.maxRowsChanged) {
/* 1183 */             this.connection.unsetMaxRows(this);
/*      */           }
/*      */ 
/* 1186 */           if (!this.connection.getDontTrackOpenResources())
/* 1187 */             this.connection.unregisterStatement(this);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 1230 */     synchronized (checkClosed()) {
/* 1231 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1233 */       if (mysql.shouldIntercept()) {
/* 1234 */         ResultSetInternalMethods interceptedResults = mysql.invokeStatementInterceptorsPre(this.originalSql, this, true);
/*      */ 
/* 1237 */         if (interceptedResults != null) {
/* 1238 */           return interceptedResults;
/*      */         }
/*      */       }
/*      */ 
/* 1242 */       if (this.detectedLongParameterSwitch)
/*      */       {
/* 1244 */         boolean firstFound = false;
/* 1245 */         long boundTimeToCheck = 0L;
/*      */ 
/* 1247 */         for (int i = 0; i < this.parameterCount - 1; i++) {
/* 1248 */           if (this.parameterBindings[i].isLongData) {
/* 1249 */             if ((firstFound) && (boundTimeToCheck != this.parameterBindings[i].boundBeforeExecutionNum))
/*      */             {
/* 1251 */               throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.11") + Messages.getString("ServerPreparedStatement.12"), "S1C00", getExceptionInterceptor());
/*      */             }
/*      */ 
/* 1256 */             firstFound = true;
/* 1257 */             boundTimeToCheck = this.parameterBindings[i].boundBeforeExecutionNum;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1264 */         serverResetStatement();
/*      */       }
/*      */ 
/* 1269 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1270 */         if (!this.parameterBindings[i].isSet) {
/* 1271 */           throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.13") + (i + 1) + Messages.getString("ServerPreparedStatement.14"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1281 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1282 */         if (this.parameterBindings[i].isLongData) {
/* 1283 */           serverLongData(i, this.parameterBindings[i]);
/*      */         }
/*      */       }
/*      */ 
/* 1287 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1288 */         dumpExecuteForTestcase();
/*      */       }
/*      */ 
/* 1295 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1297 */       packet.clear();
/* 1298 */       packet.writeByte(23);
/* 1299 */       packet.writeLong(this.serverStatementId);
/*      */ 
/* 1303 */       if (this.connection.versionMeetsMinimum(4, 1, 2))
/*      */       {
/* 1310 */         if ((this.resultFields != null) && (this.connection.isCursorFetchEnabled()) && (getResultSetType() == 1003) && (getResultSetConcurrency() == 1007) && (getFetchSize() > 0))
/*      */         {
/* 1315 */           packet.writeByte(1);
/*      */         }
/*      */         else {
/* 1318 */           packet.writeByte(0);
/*      */         }
/*      */ 
/* 1321 */         packet.writeLong(1L);
/*      */       }
/*      */ 
/* 1326 */       int nullCount = (this.parameterCount + 7) / 8;
/*      */ 
/* 1331 */       int nullBitsPosition = packet.getPosition();
/*      */ 
/* 1333 */       for (int i = 0; i < nullCount; i++) {
/* 1334 */         packet.writeByte(0);
/*      */       }
/*      */ 
/* 1337 */       byte[] nullBitsBuffer = new byte[nullCount];
/*      */ 
/* 1340 */       packet.writeByte(this.sendTypesToServer ? 1 : 0);
/*      */ 
/* 1342 */       if (this.sendTypesToServer)
/*      */       {
/* 1347 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1348 */           packet.writeInt(this.parameterBindings[i].bufferType);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1355 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1356 */         if (!this.parameterBindings[i].isLongData) {
/* 1357 */           if (!this.parameterBindings[i].isNull) {
/* 1358 */             storeBinding(packet, this.parameterBindings[i], mysql);
/*      */           }
/*      */           else
/*      */           {
/*      */             int tmp584_583 = (i / 8);
/*      */             byte[] tmp584_577 = nullBitsBuffer; tmp584_577[tmp584_583] = (byte)(tmp584_577[tmp584_583] | 1 << (i & 0x7));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1369 */       int endPosition = packet.getPosition();
/* 1370 */       packet.setPosition(nullBitsPosition);
/* 1371 */       packet.writeBytesNoNull(nullBitsBuffer);
/* 1372 */       packet.setPosition(endPosition);
/*      */ 
/* 1374 */       long begin = 0L;
/*      */ 
/* 1376 */       boolean logSlowQueries = this.connection.getLogSlowQueries();
/* 1377 */       boolean gatherPerformanceMetrics = this.connection.getGatherPerformanceMetrics();
/*      */ 
/* 1380 */       if ((this.profileSQL) || (logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1381 */         begin = mysql.getCurrentTimeNanosOrMillis();
/*      */       }
/*      */ 
/* 1384 */       resetCancelledState();
/*      */ 
/* 1386 */       StatementImpl.CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1389 */         if ((this.connection.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (this.connection.versionMeetsMinimum(5, 0, 0)))
/*      */         {
/* 1392 */           timeoutTask = new StatementImpl.CancelTask(this, this);
/* 1393 */           this.connection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */ 
/* 1397 */         statementBegins();
/*      */ 
/* 1399 */         Buffer resultPacket = mysql.sendCommand(23, null, packet, false, null, 0);
/*      */ 
/* 1402 */         long queryEndTime = 0L;
/*      */ 
/* 1404 */         if ((logSlowQueries) || (gatherPerformanceMetrics) || (this.profileSQL)) {
/* 1405 */           queryEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */         }
/*      */ 
/* 1408 */         if (timeoutTask != null) {
/* 1409 */           timeoutTask.cancel();
/*      */ 
/* 1411 */           this.connection.getCancelTimer().purge();
/*      */ 
/* 1413 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1414 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */ 
/* 1417 */           timeoutTask = null;
/*      */         }
/*      */ 
/* 1420 */         synchronized (this.cancelTimeoutMutex) {
/* 1421 */           if (this.wasCancelled) {
/* 1422 */             SQLException cause = null;
/*      */ 
/* 1424 */             if (this.wasCancelledByTimeout)
/* 1425 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 1427 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 1430 */             resetCancelledState();
/*      */ 
/* 1432 */             throw cause;
/*      */           }
/*      */         }
/*      */ 
/* 1436 */         boolean queryWasSlow = false;
/*      */ 
/* 1438 */         if ((logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1439 */           long elapsedTime = queryEndTime - begin;
/*      */ 
/* 1441 */           if (logSlowQueries) {
/* 1442 */             if (this.useAutoSlowLog) {
/* 1443 */               queryWasSlow = elapsedTime > this.connection.getSlowQueryThresholdMillis();
/*      */             } else {
/* 1445 */               queryWasSlow = this.connection.isAbonormallyLongQuery(elapsedTime);
/*      */ 
/* 1447 */               this.connection.reportQueryTime(elapsedTime);
/*      */             }
/*      */           }
/*      */ 
/* 1451 */           if (queryWasSlow)
/*      */           {
/* 1453 */             StringBuffer mesgBuf = new StringBuffer(48 + this.originalSql.length());
/*      */ 
/* 1455 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15"));
/*      */ 
/* 1457 */             mesgBuf.append(mysql.getSlowQueryThreshold());
/* 1458 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15a"));
/*      */ 
/* 1460 */             mesgBuf.append(elapsedTime);
/* 1461 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.16"));
/*      */ 
/* 1464 */             mesgBuf.append("as prepared: ");
/* 1465 */             mesgBuf.append(this.originalSql);
/* 1466 */             mesgBuf.append("\n\n with parameters bound:\n\n");
/* 1467 */             mesgBuf.append(asSql(true));
/*      */ 
/* 1469 */             this.eventSink.consumeEvent(new ProfilerEvent(6, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), elapsedTime, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), mesgBuf.toString()));
/*      */           }
/*      */ 
/* 1479 */           if (gatherPerformanceMetrics) {
/* 1480 */             this.connection.registerQueryExecutionTime(elapsedTime);
/*      */           }
/*      */         }
/*      */ 
/* 1484 */         this.connection.incrementNumberOfPreparedExecutes();
/*      */ 
/* 1486 */         if (this.profileSQL) {
/* 1487 */           this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 1490 */           this.eventSink.consumeEvent(new ProfilerEvent(4, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), mysql.getCurrentTimeNanosOrMillis() - begin, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), truncateQueryToLog(asSql(true))));
/*      */         }
/*      */ 
/* 1500 */         ResultSetInternalMethods rs = mysql.readAllResults(this, maxRowsToRetrieve, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, resultPacket, true, this.fieldCount, metadataFromCache);
/*      */ 
/* 1506 */         if (mysql.shouldIntercept()) {
/* 1507 */           ResultSetInternalMethods interceptedResults = mysql.invokeStatementInterceptorsPost(this.originalSql, this, rs, true, null);
/*      */ 
/* 1510 */           if (interceptedResults != null) {
/* 1511 */             rs = interceptedResults;
/*      */           }
/*      */         }
/*      */ 
/* 1515 */         if (this.profileSQL) {
/* 1516 */           long fetchEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */ 
/* 1518 */           this.eventSink.consumeEvent(new ProfilerEvent(5, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), fetchEndTime - queryEndTime, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), null));
/*      */         }
/*      */ 
/* 1527 */         if ((queryWasSlow) && (this.connection.getExplainSlowQueries())) {
/* 1528 */           queryAsString = asSql(true);
/*      */ 
/* 1530 */           mysql.explainSlowQuery(StringUtils.getBytes((String)queryAsString), (String)queryAsString);
/*      */         }
/*      */ 
/* 1534 */         if ((!createStreamingResultSet) && (this.serverNeedsResetBeforeEachExecution))
/*      */         {
/* 1536 */           serverResetStatement();
/*      */         }
/*      */ 
/* 1540 */         this.sendTypesToServer = false;
/* 1541 */         this.results = rs;
/*      */ 
/* 1543 */         if (mysql.hadWarnings()) {
/* 1544 */           mysql.scanForAndThrowDataTruncation();
/*      */         }
/*      */ 
/* 1547 */         Object queryAsString = rs; jsr 45; return queryAsString;
/*      */       } catch (SQLException sqlEx) {
/* 1549 */         if (mysql.shouldIntercept()) {
/* 1550 */           mysql.invokeStatementInterceptorsPost(this.originalSql, this, null, true, sqlEx);
/*      */         }
/*      */ 
/* 1553 */         throw sqlEx;
/*      */       } finally {
/* 1555 */         jsr 6; } localObject3 = returnAddress; this.statementExecuting.set(false);
/*      */ 
/* 1557 */       if (timeoutTask != null) {
/* 1558 */         timeoutTask.cancel();
/* 1559 */         this.connection.getCancelTimer().purge(); } ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverLongData(int parameterIndex, BindValue longData)
/*      */     throws SQLException
/*      */   {
/* 1594 */     synchronized (checkClosed()) {
/* 1595 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1597 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1599 */       Object value = longData.value;
/*      */ 
/* 1601 */       if ((value instanceof byte[])) {
/* 1602 */         packet.clear();
/* 1603 */         packet.writeByte(24);
/* 1604 */         packet.writeLong(this.serverStatementId);
/* 1605 */         packet.writeInt(parameterIndex);
/*      */ 
/* 1607 */         packet.writeBytesNoNull((byte[])(byte[])longData.value);
/*      */ 
/* 1609 */         mysql.sendCommand(24, null, packet, true, null, 0);
/*      */       }
/* 1611 */       else if ((value instanceof InputStream)) {
/* 1612 */         storeStream(mysql, parameterIndex, packet, (InputStream)value);
/* 1613 */       } else if ((value instanceof Blob)) {
/* 1614 */         storeStream(mysql, parameterIndex, packet, ((Blob)value).getBinaryStream());
/*      */       }
/* 1616 */       else if ((value instanceof Reader)) {
/* 1617 */         storeReader(mysql, parameterIndex, packet, (Reader)value);
/*      */       } else {
/* 1619 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverPrepare(String sql)
/*      */     throws SQLException
/*      */   {
/* 1628 */     synchronized (checkClosed()) {
/* 1629 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1631 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1632 */         dumpPrepareForTestcase();
/*      */       }
/*      */       try
/*      */       {
/* 1636 */         long begin = 0L;
/*      */ 
/* 1638 */         if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA"))
/* 1639 */           this.isLoadDataQuery = true;
/*      */         else {
/* 1641 */           this.isLoadDataQuery = false;
/*      */         }
/*      */ 
/* 1644 */         if (this.connection.getProfileSql()) {
/* 1645 */           begin = System.currentTimeMillis();
/*      */         }
/*      */ 
/* 1648 */         String characterEncoding = null;
/* 1649 */         String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 1651 */         if ((!this.isLoadDataQuery) && (this.connection.getUseUnicode()) && (connectionEncoding != null))
/*      */         {
/* 1653 */           characterEncoding = connectionEncoding;
/*      */         }
/*      */ 
/* 1656 */         Buffer prepareResultPacket = mysql.sendCommand(22, sql, null, false, characterEncoding, 0);
/*      */ 
/* 1660 */         if (this.connection.versionMeetsMinimum(4, 1, 1))
/*      */         {
/* 1665 */           prepareResultPacket.setPosition(1);
/*      */         }
/*      */         else
/*      */         {
/* 1669 */           prepareResultPacket.setPosition(0);
/*      */         }
/*      */ 
/* 1672 */         this.serverStatementId = prepareResultPacket.readLong();
/* 1673 */         this.fieldCount = prepareResultPacket.readInt();
/* 1674 */         this.parameterCount = prepareResultPacket.readInt();
/* 1675 */         this.parameterBindings = new BindValue[this.parameterCount];
/*      */ 
/* 1677 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1678 */           this.parameterBindings[i] = new BindValue();
/*      */         }
/*      */ 
/* 1681 */         this.connection.incrementNumberOfPrepares();
/*      */ 
/* 1683 */         if (this.profileSQL) {
/* 1684 */           this.eventSink.consumeEvent(new ProfilerEvent(2, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), mysql.getCurrentTimeNanosOrMillis() - begin, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), truncateQueryToLog(sql)));
/*      */         }
/*      */ 
/* 1694 */         if ((this.parameterCount > 0) && 
/* 1695 */           (this.connection.versionMeetsMinimum(4, 1, 2)) && (!mysql.isVersion(5, 0, 0)))
/*      */         {
/* 1697 */           this.parameterFields = new Field[this.parameterCount];
/*      */ 
/* 1699 */           Buffer metaDataPacket = mysql.readPacket();
/*      */ 
/* 1701 */           int i = 0;
/*      */ 
/* 1704 */           while ((!metaDataPacket.isLastDataPacket()) && (i < this.parameterCount)) {
/* 1705 */             this.parameterFields[(i++)] = mysql.unpackField(metaDataPacket, false);
/*      */ 
/* 1707 */             metaDataPacket = mysql.readPacket();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1712 */         if (this.fieldCount > 0) {
/* 1713 */           this.resultFields = new Field[this.fieldCount];
/*      */ 
/* 1715 */           Buffer fieldPacket = mysql.readPacket();
/*      */ 
/* 1717 */           int i = 0;
/*      */ 
/* 1721 */           while ((!fieldPacket.isLastDataPacket()) && (i < this.fieldCount)) {
/* 1722 */             this.resultFields[(i++)] = mysql.unpackField(fieldPacket, false);
/*      */ 
/* 1724 */             fieldPacket = mysql.readPacket();
/*      */           }
/*      */         }
/*      */       } catch (SQLException sqlEx) {
/* 1728 */         if (this.connection.getDumpQueriesOnException()) {
/* 1729 */           StringBuffer messageBuf = new StringBuffer(this.originalSql.length() + 32);
/*      */ 
/* 1731 */           messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
/*      */ 
/* 1733 */           messageBuf.append(this.originalSql);
/*      */ 
/* 1735 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1739 */         throw sqlEx;
/*      */       }
/*      */       finally
/*      */       {
/* 1744 */         this.connection.getIO().clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String truncateQueryToLog(String sql) throws SQLException {
/* 1750 */     synchronized (checkClosed()) {
/* 1751 */       String query = null;
/*      */ 
/* 1753 */       if (sql.length() > this.connection.getMaxQuerySizeToLog()) {
/* 1754 */         StringBuffer queryBuf = new StringBuffer(this.connection.getMaxQuerySizeToLog() + 12);
/*      */ 
/* 1756 */         queryBuf.append(sql.substring(0, this.connection.getMaxQuerySizeToLog()));
/* 1757 */         queryBuf.append(Messages.getString("MysqlIO.25"));
/*      */ 
/* 1759 */         query = queryBuf.toString();
/*      */       } else {
/* 1761 */         query = sql;
/*      */       }
/*      */ 
/* 1764 */       return query;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverResetStatement() throws SQLException {
/* 1769 */     synchronized (checkClosed())
/*      */     {
/* 1771 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1773 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1775 */       packet.clear();
/* 1776 */       packet.writeByte(26);
/* 1777 */       packet.writeLong(this.serverStatementId);
/*      */       try
/*      */       {
/* 1780 */         mysql.sendCommand(26, null, packet, !this.connection.versionMeetsMinimum(4, 1, 2), null, 0);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1783 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 1785 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 1787 */         sqlEx.initCause(ex);
/*      */ 
/* 1789 */         throw sqlEx;
/*      */       } finally {
/* 1791 */         mysql.clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 1800 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1809 */     synchronized (checkClosed()) {
/* 1810 */       if (x == null) {
/* 1811 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1813 */         BindValue binding = getBinding(parameterIndex, true);
/* 1814 */         setType(binding, 252);
/*      */ 
/* 1816 */         binding.value = x;
/* 1817 */         binding.isNull = false;
/* 1818 */         binding.isLongData = true;
/*      */ 
/* 1820 */         if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1821 */           binding.bindLength = length;
/*      */         else
/* 1823 */           binding.bindLength = -1L;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1834 */     synchronized (checkClosed())
/*      */     {
/* 1836 */       if (x == null) {
/* 1837 */         setNull(parameterIndex, 3);
/*      */       }
/*      */       else {
/* 1840 */         BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 1842 */         if (this.connection.versionMeetsMinimum(5, 0, 3))
/* 1843 */           setType(binding, 246);
/*      */         else {
/* 1845 */           setType(binding, this.stringTypeCode);
/*      */         }
/*      */ 
/* 1848 */         binding.value = StringUtils.fixDecimalExponent(StringUtils.consistentToString(x));
/*      */ 
/* 1850 */         binding.isNull = false;
/* 1851 */         binding.isLongData = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1862 */     synchronized (checkClosed())
/*      */     {
/* 1864 */       if (x == null) {
/* 1865 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1867 */         BindValue binding = getBinding(parameterIndex, true);
/* 1868 */         setType(binding, 252);
/*      */ 
/* 1870 */         binding.value = x;
/* 1871 */         binding.isNull = false;
/* 1872 */         binding.isLongData = true;
/*      */ 
/* 1874 */         if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1875 */           binding.bindLength = length;
/*      */         else
/* 1877 */           binding.bindLength = -1L;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, Blob x)
/*      */     throws SQLException
/*      */   {
/* 1887 */     synchronized (checkClosed())
/*      */     {
/* 1889 */       if (x == null) {
/* 1890 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1892 */         BindValue binding = getBinding(parameterIndex, true);
/* 1893 */         setType(binding, 252);
/*      */ 
/* 1895 */         binding.value = x;
/* 1896 */         binding.isNull = false;
/* 1897 */         binding.isLongData = true;
/*      */ 
/* 1899 */         if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1900 */           binding.bindLength = x.length();
/*      */         else
/* 1902 */           binding.bindLength = -1L;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1912 */     setByte(parameterIndex, x ? 1 : 0);
/*      */   }
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1919 */     checkClosed();
/*      */ 
/* 1921 */     BindValue binding = getBinding(parameterIndex, false);
/* 1922 */     setType(binding, 1);
/*      */ 
/* 1924 */     binding.value = null;
/* 1925 */     binding.longBinding = x;
/* 1926 */     binding.isNull = false;
/* 1927 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1934 */     checkClosed();
/*      */ 
/* 1936 */     if (x == null) {
/* 1937 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1939 */       BindValue binding = getBinding(parameterIndex, false);
/* 1940 */       setType(binding, 253);
/*      */ 
/* 1942 */       binding.value = x;
/* 1943 */       binding.isNull = false;
/* 1944 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1954 */     synchronized (checkClosed())
/*      */     {
/* 1956 */       if (reader == null) {
/* 1957 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1959 */         BindValue binding = getBinding(parameterIndex, true);
/* 1960 */         setType(binding, 252);
/*      */ 
/* 1962 */         binding.value = reader;
/* 1963 */         binding.isNull = false;
/* 1964 */         binding.isLongData = true;
/*      */ 
/* 1966 */         if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1967 */           binding.bindLength = length;
/*      */         else
/* 1969 */           binding.bindLength = -1L;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Clob x)
/*      */     throws SQLException
/*      */   {
/* 1979 */     synchronized (checkClosed())
/*      */     {
/* 1981 */       if (x == null) {
/* 1982 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1984 */         BindValue binding = getBinding(parameterIndex, true);
/* 1985 */         setType(binding, 252);
/*      */ 
/* 1987 */         binding.value = x.getCharacterStream();
/* 1988 */         binding.isNull = false;
/* 1989 */         binding.isLongData = true;
/*      */ 
/* 1991 */         if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1992 */           binding.bindLength = x.length();
/*      */         else
/* 1994 */           binding.bindLength = -1L;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 2013 */     setDate(parameterIndex, x, null);
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2032 */     if (x == null) {
/* 2033 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 2035 */       BindValue binding = getBinding(parameterIndex, false);
/* 2036 */       setType(binding, 10);
/*      */ 
/* 2038 */       binding.value = x;
/* 2039 */       binding.isNull = false;
/* 2040 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 2048 */     synchronized (checkClosed())
/*      */     {
/* 2050 */       if ((!this.connection.getAllowNanAndInf()) && ((x == (1.0D / 0.0D)) || (x == (-1.0D / 0.0D)) || (Double.isNaN(x))))
/*      */       {
/* 2053 */         throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2059 */       BindValue binding = getBinding(parameterIndex, false);
/* 2060 */       setType(binding, 5);
/*      */ 
/* 2062 */       binding.value = null;
/* 2063 */       binding.doubleBinding = x;
/* 2064 */       binding.isNull = false;
/* 2065 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 2073 */     checkClosed();
/*      */ 
/* 2075 */     BindValue binding = getBinding(parameterIndex, false);
/* 2076 */     setType(binding, 4);
/*      */ 
/* 2078 */     binding.value = null;
/* 2079 */     binding.floatBinding = x;
/* 2080 */     binding.isNull = false;
/* 2081 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 2088 */     checkClosed();
/*      */ 
/* 2090 */     BindValue binding = getBinding(parameterIndex, false);
/* 2091 */     setType(binding, 3);
/*      */ 
/* 2093 */     binding.value = null;
/* 2094 */     binding.longBinding = x;
/* 2095 */     binding.isNull = false;
/* 2096 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2103 */     checkClosed();
/*      */ 
/* 2105 */     BindValue binding = getBinding(parameterIndex, false);
/* 2106 */     setType(binding, 8);
/*      */ 
/* 2108 */     binding.value = null;
/* 2109 */     binding.longBinding = x;
/* 2110 */     binding.isNull = false;
/* 2111 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2118 */     checkClosed();
/*      */ 
/* 2120 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 2126 */     if (binding.bufferType == 0) {
/* 2127 */       setType(binding, 6);
/*      */     }
/*      */ 
/* 2130 */     binding.value = null;
/* 2131 */     binding.isNull = true;
/* 2132 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2140 */     checkClosed();
/*      */ 
/* 2142 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 2148 */     if (binding.bufferType == 0) {
/* 2149 */       setType(binding, 6);
/*      */     }
/*      */ 
/* 2152 */     binding.value = null;
/* 2153 */     binding.isNull = true;
/* 2154 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 2161 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2168 */     checkClosed();
/*      */ 
/* 2170 */     BindValue binding = getBinding(parameterIndex, false);
/* 2171 */     setType(binding, 2);
/*      */ 
/* 2173 */     binding.value = null;
/* 2174 */     binding.longBinding = x;
/* 2175 */     binding.isNull = false;
/* 2176 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2183 */     checkClosed();
/*      */ 
/* 2185 */     if (x == null) {
/* 2186 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 2188 */       BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 2190 */       setType(binding, this.stringTypeCode);
/*      */ 
/* 2192 */       binding.value = x;
/* 2193 */       binding.isNull = false;
/* 2194 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2211 */     synchronized (checkClosed()) {
/* 2212 */       setTimeInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2233 */     setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   protected void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 2254 */     synchronized (checkClosed()) {
/* 2255 */       if (x == null) {
/* 2256 */         setNull(parameterIndex, 92);
/*      */       } else {
/* 2258 */         BindValue binding = getBinding(parameterIndex, false);
/* 2259 */         setType(binding, 11);
/*      */ 
/* 2261 */         if (!this.useLegacyDatetimeCode) {
/* 2262 */           binding.value = x;
/*      */         } else {
/* 2264 */           Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */ 
/* 2266 */           binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         }
/*      */ 
/* 2274 */         binding.isNull = false;
/* 2275 */         binding.isLongData = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2294 */     synchronized (checkClosed()) {
/* 2295 */       setTimestampInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2315 */     synchronized (checkClosed()) {
/* 2316 */       setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 2324 */     synchronized (checkClosed()) {
/* 2325 */       if (x == null) {
/* 2326 */         setNull(parameterIndex, 93);
/*      */       } else {
/* 2328 */         BindValue binding = getBinding(parameterIndex, false);
/* 2329 */         setType(binding, 12);
/*      */ 
/* 2331 */         if (!this.useLegacyDatetimeCode) {
/* 2332 */           binding.value = x;
/*      */         } else {
/* 2334 */           Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */ 
/* 2338 */           binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */ 
/* 2345 */           binding.isNull = false;
/* 2346 */           binding.isLongData = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setType(BindValue oldValue, int bufferType) throws SQLException {
/* 2353 */     synchronized (checkClosed()) {
/* 2354 */       if (oldValue.bufferType != bufferType) {
/* 2355 */         this.sendTypesToServer = true;
/*      */       }
/*      */ 
/* 2358 */       oldValue.bufferType = bufferType;
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2383 */     checkClosed();
/*      */ 
/* 2385 */     throw SQLError.notImplemented();
/*      */   }
/*      */ 
/*      */   public void setURL(int parameterIndex, URL x)
/*      */     throws SQLException
/*      */   {
/* 2392 */     checkClosed();
/*      */ 
/* 2394 */     setString(parameterIndex, x.toString());
/*      */   }
/*      */ 
/*      */   private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql)
/*      */     throws SQLException
/*      */   {
/* 2410 */     synchronized (checkClosed()) {
/*      */       try {
/* 2412 */         Object value = bindValue.value;
/*      */ 
/* 2417 */         switch (bindValue.bufferType)
/*      */         {
/*      */         case 1:
/* 2420 */           packet.writeByte((byte)(int)bindValue.longBinding);
/* 2421 */           return;
/*      */         case 2:
/* 2423 */           packet.ensureCapacity(2);
/* 2424 */           packet.writeInt((int)bindValue.longBinding);
/* 2425 */           return;
/*      */         case 3:
/* 2427 */           packet.ensureCapacity(4);
/* 2428 */           packet.writeLong((int)bindValue.longBinding);
/* 2429 */           return;
/*      */         case 8:
/* 2431 */           packet.ensureCapacity(8);
/* 2432 */           packet.writeLongLong(bindValue.longBinding);
/* 2433 */           return;
/*      */         case 4:
/* 2435 */           packet.ensureCapacity(4);
/* 2436 */           packet.writeFloat(bindValue.floatBinding);
/* 2437 */           return;
/*      */         case 5:
/* 2439 */           packet.ensureCapacity(8);
/* 2440 */           packet.writeDouble(bindValue.doubleBinding);
/* 2441 */           return;
/*      */         case 11:
/* 2443 */           storeTime(packet, (Time)value);
/* 2444 */           return;
/*      */         case 7:
/*      */         case 10:
/*      */         case 12:
/* 2448 */           storeDateTime(packet, (java.util.Date)value, mysql, bindValue.bufferType);
/* 2449 */           return;
/*      */         case 0:
/*      */         case 15:
/*      */         case 246:
/*      */         case 253:
/*      */         case 254:
/* 2455 */           if ((value instanceof byte[]))
/* 2456 */             packet.writeLenBytes((byte[])(byte[])value);
/* 2457 */           else if (!this.isLoadDataQuery) {
/* 2458 */             packet.writeLenString((String)value, this.charEncoding, this.connection.getServerCharacterEncoding(), this.charConverter, this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/*      */           else
/*      */           {
/* 2464 */             packet.writeLenBytes(StringUtils.getBytes((String)value));
/*      */           }
/*      */ 
/* 2467 */           return;
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException uEE)
/*      */       {
/* 2472 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.22") + this.connection.getEncoding() + "'", "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDateTime412AndOlder(Buffer intoBuf, java.util.Date dt, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2482 */     synchronized (checkClosed()) {
/* 2483 */       Calendar sessionCalendar = null;
/*      */ 
/* 2485 */       if (!this.useLegacyDatetimeCode) {
/* 2486 */         if (bufferType == 10)
/* 2487 */           sessionCalendar = getDefaultTzCalendar();
/*      */         else
/* 2489 */           sessionCalendar = getServerTzCalendar();
/*      */       }
/*      */       else {
/* 2492 */         sessionCalendar = ((dt instanceof Timestamp)) && (this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */ 
/* 2497 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try
/*      */       {
/* 2500 */         intoBuf.ensureCapacity(8);
/* 2501 */         intoBuf.writeByte(7);
/*      */ 
/* 2503 */         sessionCalendar.setTime(dt);
/*      */ 
/* 2505 */         int year = sessionCalendar.get(1);
/* 2506 */         int month = sessionCalendar.get(2) + 1;
/* 2507 */         int date = sessionCalendar.get(5);
/*      */ 
/* 2509 */         intoBuf.writeInt(year);
/* 2510 */         intoBuf.writeByte((byte)month);
/* 2511 */         intoBuf.writeByte((byte)date);
/*      */ 
/* 2513 */         if ((dt instanceof java.sql.Date)) {
/* 2514 */           intoBuf.writeByte(0);
/* 2515 */           intoBuf.writeByte(0);
/* 2516 */           intoBuf.writeByte(0);
/*      */         } else {
/* 2518 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/*      */ 
/* 2520 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/*      */ 
/* 2522 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         }
/*      */       }
/*      */       finally {
/* 2526 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDateTime(Buffer intoBuf, java.util.Date dt, MysqlIO mysql, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2541 */     synchronized (checkClosed()) {
/* 2542 */       if (this.connection.versionMeetsMinimum(4, 1, 3))
/* 2543 */         storeDateTime413AndNewer(intoBuf, dt, bufferType);
/*      */       else
/* 2545 */         storeDateTime412AndOlder(intoBuf, dt, bufferType);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDateTime413AndNewer(Buffer intoBuf, java.util.Date dt, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2552 */     synchronized (checkClosed()) {
/* 2553 */       Calendar sessionCalendar = null;
/*      */ 
/* 2555 */       if (!this.useLegacyDatetimeCode) {
/* 2556 */         if (bufferType == 10)
/* 2557 */           sessionCalendar = getDefaultTzCalendar();
/*      */         else
/* 2559 */           sessionCalendar = getServerTzCalendar();
/*      */       }
/*      */       else {
/* 2562 */         sessionCalendar = ((dt instanceof Timestamp)) && (this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */ 
/* 2568 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try
/*      */       {
/* 2571 */         sessionCalendar.setTime(dt);
/*      */ 
/* 2573 */         if ((dt instanceof java.sql.Date)) {
/* 2574 */           sessionCalendar.set(11, 0);
/* 2575 */           sessionCalendar.set(12, 0);
/* 2576 */           sessionCalendar.set(13, 0);
/*      */         }
/*      */ 
/* 2579 */         byte length = 7;
/*      */ 
/* 2581 */         if ((dt instanceof Timestamp)) {
/* 2582 */           length = 11;
/*      */         }
/*      */ 
/* 2585 */         intoBuf.ensureCapacity(length);
/*      */ 
/* 2587 */         intoBuf.writeByte(length);
/*      */ 
/* 2589 */         int year = sessionCalendar.get(1);
/* 2590 */         int month = sessionCalendar.get(2) + 1;
/* 2591 */         int date = sessionCalendar.get(5);
/*      */ 
/* 2593 */         intoBuf.writeInt(year);
/* 2594 */         intoBuf.writeByte((byte)month);
/* 2595 */         intoBuf.writeByte((byte)date);
/*      */ 
/* 2597 */         if ((dt instanceof java.sql.Date)) {
/* 2598 */           intoBuf.writeByte(0);
/* 2599 */           intoBuf.writeByte(0);
/* 2600 */           intoBuf.writeByte(0);
/*      */         } else {
/* 2602 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/*      */ 
/* 2604 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/*      */ 
/* 2606 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         }
/*      */ 
/* 2610 */         if (length == 11)
/*      */         {
/* 2612 */           intoBuf.writeLong(((Timestamp)dt).getNanos() / 1000);
/*      */         }
/*      */       }
/*      */       finally {
/* 2616 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Calendar getServerTzCalendar() throws SQLException {
/* 2622 */     synchronized (checkClosed()) {
/* 2623 */       if (this.serverTzCalendar == null) {
/* 2624 */         this.serverTzCalendar = new GregorianCalendar(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */ 
/* 2627 */       return this.serverTzCalendar;
/*      */     }
/*      */   }
/*      */ 
/*      */   private Calendar getDefaultTzCalendar() throws SQLException {
/* 2632 */     synchronized (checkClosed()) {
/* 2633 */       if (this.defaultTzCalendar == null) {
/* 2634 */         this.defaultTzCalendar = new GregorianCalendar(TimeZone.getDefault());
/*      */       }
/*      */ 
/* 2637 */       return this.defaultTzCalendar;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeReader(MysqlIO mysql, int parameterIndex, Buffer packet, Reader inStream)
/*      */     throws SQLException
/*      */   {
/* 2646 */     synchronized (checkClosed()) {
/* 2647 */       String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */ 
/* 2649 */       String clobEncoding = forcedEncoding == null ? this.connection.getEncoding() : forcedEncoding;
/*      */ 
/* 2652 */       int maxBytesChar = 2;
/*      */ 
/* 2654 */       if (clobEncoding != null) {
/* 2655 */         if (!clobEncoding.equals("UTF-16")) {
/* 2656 */           maxBytesChar = this.connection.getMaxBytesPerChar(clobEncoding);
/*      */ 
/* 2658 */           if (maxBytesChar == 1)
/* 2659 */             maxBytesChar = 2;
/*      */         }
/*      */         else {
/* 2662 */           maxBytesChar = 4;
/*      */         }
/*      */       }
/*      */ 
/* 2666 */       char[] buf = new char[8192 / maxBytesChar];
/*      */ 
/* 2668 */       int numRead = 0;
/*      */ 
/* 2670 */       int bytesInPacket = 0;
/* 2671 */       int totalBytesRead = 0;
/* 2672 */       int bytesReadAtLastSend = 0;
/* 2673 */       int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */       try
/*      */       {
/* 2678 */         packet.clear();
/* 2679 */         packet.writeByte(24);
/* 2680 */         packet.writeLong(this.serverStatementId);
/* 2681 */         packet.writeInt(parameterIndex);
/*      */ 
/* 2683 */         boolean readAny = false;
/*      */ 
/* 2685 */         while ((numRead = inStream.read(buf)) != -1) {
/* 2686 */           readAny = true;
/*      */ 
/* 2688 */           byte[] valueAsBytes = StringUtils.getBytes(buf, null, clobEncoding, this.connection.getServerCharacterEncoding(), 0, numRead, this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */ 
/* 2693 */           packet.writeBytesNoNull(valueAsBytes, 0, valueAsBytes.length);
/*      */ 
/* 2695 */           bytesInPacket += valueAsBytes.length;
/* 2696 */           totalBytesRead += valueAsBytes.length;
/*      */ 
/* 2698 */           if (bytesInPacket >= packetIsFullAt) {
/* 2699 */             bytesReadAtLastSend = totalBytesRead;
/*      */ 
/* 2701 */             mysql.sendCommand(24, null, packet, true, null, 0);
/*      */ 
/* 2704 */             bytesInPacket = 0;
/* 2705 */             packet.clear();
/* 2706 */             packet.writeByte(24);
/* 2707 */             packet.writeLong(this.serverStatementId);
/* 2708 */             packet.writeInt(parameterIndex);
/*      */           }
/*      */         }
/*      */ 
/* 2712 */         if (totalBytesRead != bytesReadAtLastSend) {
/* 2713 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */         }
/*      */ 
/* 2717 */         if (!readAny)
/* 2718 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/* 2722 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.24") + ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 2725 */         sqlEx.initCause(ioEx);
/*      */ 
/* 2727 */         throw sqlEx;
/*      */       } finally {
/* 2729 */         if ((this.connection.getAutoClosePStmtStreams()) && 
/* 2730 */           (inStream != null))
/*      */           try {
/* 2732 */             inStream.close();
/*      */           }
/*      */           catch (IOException ioEx)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeStream(MysqlIO mysql, int parameterIndex, Buffer packet, InputStream inStream)
/*      */     throws SQLException
/*      */   {
/* 2744 */     synchronized (checkClosed()) {
/* 2745 */       byte[] buf = new byte[8192];
/*      */ 
/* 2747 */       int numRead = 0;
/*      */       try
/*      */       {
/* 2750 */         int bytesInPacket = 0;
/* 2751 */         int totalBytesRead = 0;
/* 2752 */         int bytesReadAtLastSend = 0;
/* 2753 */         int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */ 
/* 2755 */         packet.clear();
/* 2756 */         packet.writeByte(24);
/* 2757 */         packet.writeLong(this.serverStatementId);
/* 2758 */         packet.writeInt(parameterIndex);
/*      */ 
/* 2760 */         boolean readAny = false;
/*      */ 
/* 2762 */         while ((numRead = inStream.read(buf)) != -1)
/*      */         {
/* 2764 */           readAny = true;
/*      */ 
/* 2766 */           packet.writeBytesNoNull(buf, 0, numRead);
/* 2767 */           bytesInPacket += numRead;
/* 2768 */           totalBytesRead += numRead;
/*      */ 
/* 2770 */           if (bytesInPacket >= packetIsFullAt) {
/* 2771 */             bytesReadAtLastSend = totalBytesRead;
/*      */ 
/* 2773 */             mysql.sendCommand(24, null, packet, true, null, 0);
/*      */ 
/* 2776 */             bytesInPacket = 0;
/* 2777 */             packet.clear();
/* 2778 */             packet.writeByte(24);
/* 2779 */             packet.writeLong(this.serverStatementId);
/* 2780 */             packet.writeInt(parameterIndex);
/*      */           }
/*      */         }
/*      */ 
/* 2784 */         if (totalBytesRead != bytesReadAtLastSend) {
/* 2785 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */         }
/*      */ 
/* 2789 */         if (!readAny)
/* 2790 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/* 2794 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.25") + ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */ 
/* 2797 */         sqlEx.initCause(ioEx);
/*      */ 
/* 2799 */         throw sqlEx;
/*      */       } finally {
/* 2801 */         if ((this.connection.getAutoClosePStmtStreams()) && 
/* 2802 */           (inStream != null))
/*      */           try {
/* 2804 */             inStream.close();
/*      */           }
/*      */           catch (IOException ioEx)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2818 */     StringBuffer toStringBuf = new StringBuffer();
/*      */ 
/* 2820 */     toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement[");
/* 2821 */     toStringBuf.append(this.serverStatementId);
/* 2822 */     toStringBuf.append("] - ");
/*      */     try
/*      */     {
/* 2825 */       toStringBuf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 2827 */       toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
/* 2828 */       toStringBuf.append(sqlEx);
/*      */     }
/*      */ 
/* 2831 */     return toStringBuf.toString();
/*      */   }
/*      */ 
/*      */   protected long getServerStatementId() {
/* 2835 */     return this.serverStatementId;
/*      */   }
/*      */ 
/*      */   public boolean canRewriteAsMultiValueInsertAtSqlLevel()
/*      */     throws SQLException
/*      */   {
/* 2842 */     synchronized (checkClosed()) {
/* 2843 */       if (!this.hasCheckedRewrite) {
/* 2844 */         this.hasCheckedRewrite = true;
/* 2845 */         this.canRewrite = canRewrite(this.originalSql, isOnDuplicateKeyUpdate(), getLocationOfOnDuplicateKeyUpdate(), 0);
/*      */ 
/* 2847 */         this.parseInfo = new PreparedStatement.ParseInfo(this, this.originalSql, this.connection, this.connection.getMetaData(), this.charEncoding, this.charConverter);
/*      */       }
/*      */ 
/* 2850 */       return this.canRewrite;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canRewriteAsMultivalueInsertStatement() throws SQLException
/*      */   {
/* 2856 */     synchronized (checkClosed()) {
/* 2857 */       if (!canRewriteAsMultiValueInsertAtSqlLevel()) {
/* 2858 */         return false;
/*      */       }
/*      */ 
/* 2861 */       BindValue[] currentBindValues = null;
/* 2862 */       BindValue[] previousBindValues = null;
/*      */ 
/* 2864 */       int nbrCommands = this.batchedArgs.size();
/*      */ 
/* 2868 */       for (int commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 2869 */         Object arg = this.batchedArgs.get(commandIndex);
/*      */ 
/* 2871 */         if ((arg instanceof String))
/*      */           continue;
/* 2873 */         currentBindValues = ((BatchedBindValues)arg).batchedParameterValues;
/*      */ 
/* 2879 */         if (previousBindValues != null) {
/* 2880 */           for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2881 */             if (currentBindValues[j].bufferType != previousBindValues[j].bufferType) {
/* 2882 */               return false;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2891 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int getLocationOfOnDuplicateKeyUpdate()
/*      */     throws SQLException
/*      */   {
/* 2898 */     synchronized (checkClosed()) {
/* 2899 */       if (this.locationOfOnDuplicateKeyUpdate == -2) {
/* 2900 */         this.locationOfOnDuplicateKeyUpdate = getOnDuplicateKeyLocation(this.originalSql);
/*      */       }
/*      */ 
/* 2903 */       return this.locationOfOnDuplicateKeyUpdate;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isOnDuplicateKeyUpdate() throws SQLException {
/* 2908 */     synchronized (checkClosed()) {
/* 2909 */       return getLocationOfOnDuplicateKeyUpdate() != -1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 2922 */     synchronized (checkClosed()) {
/* 2923 */       long sizeOfEntireBatch = 10L;
/* 2924 */       long maxSizeOfParameterSet = 0L;
/*      */ 
/* 2926 */       for (int i = 0; i < numBatchedArgs; i++) {
/* 2927 */         BindValue[] paramArg = ((BatchedBindValues)this.batchedArgs.get(i)).batchedParameterValues;
/*      */ 
/* 2929 */         long sizeOfParameterSet = 0L;
/*      */ 
/* 2931 */         sizeOfParameterSet += (this.parameterCount + 7) / 8;
/*      */ 
/* 2933 */         sizeOfParameterSet += this.parameterCount * 2;
/*      */ 
/* 2935 */         for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2936 */           if (paramArg[j].isNull)
/*      */             continue;
/* 2938 */           long size = paramArg[j].getBoundLength();
/*      */ 
/* 2940 */           if (paramArg[j].isLongData) {
/* 2941 */             if (size != -1L)
/* 2942 */               sizeOfParameterSet += size;
/*      */           }
/*      */           else {
/* 2945 */             sizeOfParameterSet += size;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2950 */         sizeOfEntireBatch += sizeOfParameterSet;
/*      */ 
/* 2952 */         if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 2953 */           maxSizeOfParameterSet = sizeOfParameterSet;
/*      */         }
/*      */       }
/*      */ 
/* 2957 */       return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet)
/*      */     throws SQLException
/*      */   {
/* 2964 */     BindValue[] paramArg = ((BatchedBindValues)paramSet).batchedParameterValues;
/*      */ 
/* 2966 */     for (int j = 0; j < paramArg.length; j++) {
/* 2967 */       if (paramArg[j].isNull) {
/* 2968 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 2970 */       else if (paramArg[j].isLongData) {
/* 2971 */         Object value = paramArg[j].value;
/*      */ 
/* 2973 */         if ((value instanceof InputStream)) {
/* 2974 */           batchedStatement.setBinaryStream(batchedParamIndex++, (InputStream)value, (int)paramArg[j].bindLength);
/*      */         }
/*      */         else
/*      */         {
/* 2978 */           batchedStatement.setCharacterStream(batchedParamIndex++, (Reader)value, (int)paramArg[j].bindLength);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2984 */         switch (paramArg[j].bufferType)
/*      */         {
/*      */         case 1:
/* 2987 */           batchedStatement.setByte(batchedParamIndex++, (byte)(int)paramArg[j].longBinding);
/*      */ 
/* 2989 */           break;
/*      */         case 2:
/* 2991 */           batchedStatement.setShort(batchedParamIndex++, (short)(int)paramArg[j].longBinding);
/*      */ 
/* 2993 */           break;
/*      */         case 3:
/* 2995 */           batchedStatement.setInt(batchedParamIndex++, (int)paramArg[j].longBinding);
/*      */ 
/* 2997 */           break;
/*      */         case 8:
/* 2999 */           batchedStatement.setLong(batchedParamIndex++, paramArg[j].longBinding);
/*      */ 
/* 3001 */           break;
/*      */         case 4:
/* 3003 */           batchedStatement.setFloat(batchedParamIndex++, paramArg[j].floatBinding);
/*      */ 
/* 3005 */           break;
/*      */         case 5:
/* 3007 */           batchedStatement.setDouble(batchedParamIndex++, paramArg[j].doubleBinding);
/*      */ 
/* 3009 */           break;
/*      */         case 11:
/* 3011 */           batchedStatement.setTime(batchedParamIndex++, (Time)paramArg[j].value);
/*      */ 
/* 3013 */           break;
/*      */         case 10:
/* 3015 */           batchedStatement.setDate(batchedParamIndex++, (java.sql.Date)paramArg[j].value);
/*      */ 
/* 3017 */           break;
/*      */         case 7:
/*      */         case 12:
/* 3020 */           batchedStatement.setTimestamp(batchedParamIndex++, (Timestamp)paramArg[j].value);
/*      */ 
/* 3022 */           break;
/*      */         case 0:
/*      */         case 15:
/*      */         case 246:
/*      */         case 253:
/*      */         case 254:
/* 3028 */           Object value = paramArg[j].value;
/*      */ 
/* 3030 */           if ((value instanceof byte[])) {
/* 3031 */             batchedStatement.setBytes(batchedParamIndex, (byte[])(byte[])value);
/*      */           }
/*      */           else {
/* 3034 */             batchedStatement.setString(batchedParamIndex, (String)value);
/*      */           }
/*      */ 
/* 3040 */           if ((batchedStatement instanceof ServerPreparedStatement)) {
/* 3041 */             BindValue asBound = ((ServerPreparedStatement)batchedStatement).getBinding(batchedParamIndex, false);
/*      */ 
/* 3045 */             asBound.bufferType = paramArg[j].bufferType;
/*      */           }
/*      */ 
/* 3048 */           batchedParamIndex++;
/*      */ 
/* 3050 */           break;
/*      */         default:
/* 3052 */           throw new IllegalArgumentException("Unknown type when re-binding parameter into batched statement for parameter index " + batchedParamIndex);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3060 */     return batchedParamIndex;
/*      */   }
/*      */ 
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 3064 */     return this.hasOnDuplicateKeyUpdate;
/*      */   }
/*      */ 
/*      */   protected PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches) throws SQLException {
/* 3068 */     synchronized (checkClosed()) {
/*      */       try {
/* 3070 */         PreparedStatement pstmt = new ServerPreparedStatement(localConn, this.parseInfo.getSqlForBatch(numBatches), this.currentCatalog, this.resultSetConcurrency, this.resultSetType);
/* 3071 */         pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/*      */ 
/* 3073 */         return pstmt;
/*      */       } catch (UnsupportedEncodingException e) {
/* 3075 */         SQLException sqlEx = SQLError.createSQLException("Unable to prepare batch statement", "S1000", getExceptionInterceptor());
/* 3076 */         sqlEx.initCause(e);
/*      */ 
/* 3078 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   68 */     if (Util.isJdbc4())
/*      */       try {
/*   70 */         JDBC_4_SPS_CTOR = Class.forName("com.mysql.jdbc.JDBC4ServerPreparedStatement").getConstructor(new Class[] { MySQLConnection.class, String.class, String.class, Integer.TYPE, Integer.TYPE });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*   75 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   77 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   79 */         throw new RuntimeException(e);
/*      */       }
/*      */     else
/*   82 */       JDBC_4_SPS_CTOR = null;
/*      */   }
/*      */ 
/*      */   public static class BindValue {
/*  104 */     public long boundBeforeExecutionNum = 0L;
/*      */     public long bindLength;
/*      */     public int bufferType;
/*      */     public double doubleBinding;
/*      */     public float floatBinding;
/*      */     public boolean isLongData;
/*      */     public boolean isNull;
/*  118 */     public boolean isSet = false;
/*      */     public long longBinding;
/*      */     public Object value;
/*      */ 
/*      */     BindValue() {
/*      */     }
/*      */ 
/*      */     BindValue(BindValue copyMe) {
/*  128 */       this.value = copyMe.value;
/*  129 */       this.isSet = copyMe.isSet;
/*  130 */       this.isLongData = copyMe.isLongData;
/*  131 */       this.isNull = copyMe.isNull;
/*  132 */       this.bufferType = copyMe.bufferType;
/*  133 */       this.bindLength = copyMe.bindLength;
/*  134 */       this.longBinding = copyMe.longBinding;
/*  135 */       this.floatBinding = copyMe.floatBinding;
/*  136 */       this.doubleBinding = copyMe.doubleBinding;
/*      */     }
/*      */ 
/*      */     void reset() {
/*  140 */       this.isSet = false;
/*  141 */       this.value = null;
/*  142 */       this.isLongData = false;
/*      */ 
/*  144 */       this.longBinding = 0L;
/*  145 */       this.floatBinding = 0.0F;
/*  146 */       this.doubleBinding = 0.0D;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  150 */       return toString(false);
/*      */     }
/*      */ 
/*      */     public String toString(boolean quoteIfNeeded) {
/*  154 */       if (this.isLongData) {
/*  155 */         return "' STREAM DATA '";
/*      */       }
/*      */ 
/*  158 */       switch (this.bufferType) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 8:
/*  163 */         return String.valueOf(this.longBinding);
/*      */       case 4:
/*  165 */         return String.valueOf(this.floatBinding);
/*      */       case 5:
/*  167 */         return String.valueOf(this.doubleBinding);
/*      */       case 7:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 15:
/*      */       case 253:
/*      */       case 254:
/*  175 */         if (quoteIfNeeded) {
/*  176 */           return "'" + String.valueOf(this.value) + "'";
/*      */         }
/*  178 */         return String.valueOf(this.value);
/*      */       }
/*      */ 
/*  181 */       if ((this.value instanceof byte[])) {
/*  182 */         return "byte data";
/*      */       }
/*  184 */       if (quoteIfNeeded) {
/*  185 */         return "'" + String.valueOf(this.value) + "'";
/*      */       }
/*  187 */       return String.valueOf(this.value);
/*      */     }
/*      */ 
/*      */     long getBoundLength()
/*      */     {
/*  192 */       if (this.isNull) {
/*  193 */         return 0L;
/*      */       }
/*      */ 
/*  196 */       if (this.isLongData) {
/*  197 */         return this.bindLength;
/*      */       }
/*      */ 
/*  200 */       switch (this.bufferType)
/*      */       {
/*      */       case 1:
/*  203 */         return 1L;
/*      */       case 2:
/*  205 */         return 2L;
/*      */       case 3:
/*  207 */         return 4L;
/*      */       case 8:
/*  209 */         return 8L;
/*      */       case 4:
/*  211 */         return 4L;
/*      */       case 5:
/*  213 */         return 8L;
/*      */       case 11:
/*  215 */         return 9L;
/*      */       case 10:
/*  217 */         return 7L;
/*      */       case 7:
/*      */       case 12:
/*  220 */         return 11L;
/*      */       case 0:
/*      */       case 15:
/*      */       case 246:
/*      */       case 253:
/*      */       case 254:
/*  226 */         if ((this.value instanceof byte[])) {
/*  227 */           return ((byte[])(byte[])this.value).length;
/*      */         }
/*  229 */         return ((String)this.value).length();
/*      */       }
/*      */ 
/*  232 */       return 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class BatchedBindValues
/*      */   {
/*      */     public ServerPreparedStatement.BindValue[] batchedParameterValues;
/*      */ 
/*      */     BatchedBindValues(ServerPreparedStatement.BindValue[] paramVals)
/*      */     {
/*   92 */       int numParams = paramVals.length;
/*      */ 
/*   94 */       this.batchedParameterValues = new ServerPreparedStatement.BindValue[numParams];
/*      */ 
/*   96 */       for (int i = 0; i < numParams; i++)
/*   97 */         this.batchedParameterValues[i] = new ServerPreparedStatement.BindValue(paramVals[i]);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ServerPreparedStatement
 * JD-Core Version:    0.6.0
 */