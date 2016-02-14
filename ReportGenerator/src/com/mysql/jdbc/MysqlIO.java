/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;
/*      */ import com.mysql.jdbc.authentication.MysqlNativePasswordPlugin;
/*      */ import com.mysql.jdbc.authentication.MysqlOldPasswordPlugin;
/*      */ import com.mysql.jdbc.authentication.Sha256PasswordPlugin;
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.util.ReadAheadInputStream;
/*      */ import com.mysql.jdbc.util.ResultSetUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.management.ManagementFactory;
/*      */ import java.lang.management.ThreadInfo;
/*      */ import java.lang.management.ThreadMXBean;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.net.URL;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.Deflater;
/*      */ 
/*      */ public class MysqlIO
/*      */ {
/*      */   private static final int UTF8_CHARSET_INDEX = 33;
/*      */   private static final String CODE_PAGE_1252 = "Cp1252";
/*      */   protected static final int NULL_LENGTH = -1;
/*      */   protected static final int COMP_HEADER_LENGTH = 3;
/*      */   protected static final int MIN_COMPRESS_LEN = 50;
/*      */   protected static final int HEADER_LENGTH = 4;
/*      */   protected static final int AUTH_411_OVERHEAD = 33;
/*   88 */   private static int maxBufferSize = 65535;
/*      */   private static final int CLIENT_COMPRESS = 32;
/*      */   protected static final int CLIENT_CONNECT_WITH_DB = 8;
/*      */   private static final int CLIENT_FOUND_ROWS = 2;
/*      */   private static final int CLIENT_LOCAL_FILES = 128;
/*      */   private static final int CLIENT_LONG_FLAG = 4;
/*      */   private static final int CLIENT_LONG_PASSWORD = 1;
/*      */   private static final int CLIENT_PROTOCOL_41 = 512;
/*      */   private static final int CLIENT_INTERACTIVE = 1024;
/*      */   protected static final int CLIENT_SSL = 2048;
/*      */   private static final int CLIENT_TRANSACTIONS = 8192;
/*      */   protected static final int CLIENT_RESERVED = 16384;
/*      */   protected static final int CLIENT_SECURE_CONNECTION = 32768;
/*      */   private static final int CLIENT_MULTI_QUERIES = 65536;
/*      */   private static final int CLIENT_MULTI_RESULTS = 131072;
/*      */   private static final int CLIENT_PLUGIN_AUTH = 524288;
/*      */   private static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 4194304;
/*      */   private static final int SERVER_STATUS_IN_TRANS = 1;
/*      */   private static final int SERVER_STATUS_AUTOCOMMIT = 2;
/*      */   static final int SERVER_MORE_RESULTS_EXISTS = 8;
/*      */   private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
/*      */   private static final int SERVER_QUERY_NO_INDEX_USED = 32;
/*      */   private static final int SERVER_QUERY_WAS_SLOW = 2048;
/*      */   private static final int SERVER_STATUS_CURSOR_EXISTS = 64;
/*      */   private static final String FALSE_SCRAMBLE = "xxxxxxxx";
/*      */   protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
/*      */   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
/*      */   protected static final int INITIAL_PACKET_SIZE = 1024;
/*  127 */   private static String jvmPlatformCharset = null;
/*      */   protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
/*      */   protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
/*      */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*  160 */   private boolean packetSequenceReset = false;
/*      */   protected int serverCharsetIndex;
/*  168 */   private Buffer reusablePacket = null;
/*  169 */   private Buffer sendPacket = null;
/*  170 */   private Buffer sharedSendPacket = null;
/*      */ 
/*  173 */   protected BufferedOutputStream mysqlOutput = null;
/*      */   protected MySQLConnection connection;
/*  175 */   private Deflater deflater = null;
/*  176 */   protected InputStream mysqlInput = null;
/*  177 */   private LinkedList<StringBuffer> packetDebugRingBuffer = null;
/*  178 */   private RowData streamingData = null;
/*      */ 
/*  181 */   protected Socket mysqlConnection = null;
/*  182 */   protected SocketFactory socketFactory = null;
/*      */   private SoftReference<Buffer> loadFileBufRef;
/*      */   private SoftReference<Buffer> splitBufRef;
/*      */   private SoftReference<Buffer> compressBufRef;
/*  199 */   protected String host = null;
/*      */   protected String seed;
/*  201 */   private String serverVersion = null;
/*  202 */   private String socketFactoryClassName = null;
/*  203 */   private byte[] packetHeaderBuf = new byte[4];
/*  204 */   private boolean colDecimalNeedsBump = false;
/*  205 */   private boolean hadWarnings = false;
/*  206 */   private boolean has41NewNewProt = false;
/*      */ 
/*  209 */   private boolean hasLongColumnInfo = false;
/*  210 */   private boolean isInteractiveClient = false;
/*  211 */   private boolean logSlowQueries = false;
/*      */ 
/*  217 */   private boolean platformDbCharsetMatches = true;
/*  218 */   private boolean profileSql = false;
/*  219 */   private boolean queryBadIndexUsed = false;
/*  220 */   private boolean queryNoIndexUsed = false;
/*  221 */   private boolean serverQueryWasSlow = false;
/*      */ 
/*  224 */   private boolean use41Extensions = false;
/*  225 */   private boolean useCompression = false;
/*  226 */   private boolean useNewLargePackets = false;
/*  227 */   private boolean useNewUpdateCounts = false;
/*  228 */   private byte packetSequence = 0;
/*  229 */   private byte compressedPacketSequence = 0;
/*  230 */   private byte readPacketSequence = -1;
/*  231 */   private boolean checkPacketSequence = false;
/*  232 */   private byte protocolVersion = 0;
/*  233 */   private int maxAllowedPacket = 1048576;
/*  234 */   protected int maxThreeBytes = 16581375;
/*  235 */   protected int port = 3306;
/*      */   protected int serverCapabilities;
/*  237 */   private int serverMajorVersion = 0;
/*  238 */   private int serverMinorVersion = 0;
/*  239 */   private int oldServerStatus = 0;
/*  240 */   private int serverStatus = 0;
/*  241 */   private int serverSubMinorVersion = 0;
/*  242 */   private int warningCount = 0;
/*  243 */   protected long clientParam = 0L;
/*  244 */   protected long lastPacketSentTimeMs = 0L;
/*  245 */   protected long lastPacketReceivedTimeMs = 0L;
/*  246 */   private boolean traceProtocol = false;
/*  247 */   private boolean enablePacketDebug = false;
/*      */   private boolean useConnectWithDb;
/*      */   private boolean needToGrabQueryFromPacket;
/*      */   private boolean autoGenerateTestcaseScript;
/*      */   private long threadId;
/*      */   private boolean useNanosForElapsedTime;
/*      */   private long slowQueryThreshold;
/*      */   private String queryTimingUnits;
/*  255 */   private boolean useDirectRowUnpack = true;
/*      */   private int useBufferRowSizeThreshold;
/*  257 */   private int commandCount = 0;
/*      */   private List<StatementInterceptorV2> statementInterceptors;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*  260 */   private int authPluginDataLength = 0;
/*      */ 
/* 1425 */   private Map<String, AuthenticationPlugin> authenticationPlugins = null;
/*      */ 
/* 1430 */   private List<String> disabledAuthenticationPlugins = null;
/*      */ 
/* 1434 */   private String defaultAuthenticationPlugin = null;
/*      */ 
/* 1438 */   private String defaultAuthenticationPluginProtocolName = null;
/*      */ 
/* 2522 */   private int statementExecutionDepth = 0;
/*      */   private boolean useAutoSlowLog;
/*      */ 
/*      */   public MysqlIO(String host, int port, Properties props, String socketFactoryClassName, MySQLConnection conn, int socketTimeout, int useBufferRowSizeThreshold)
/*      */     throws IOException, SQLException
/*      */   {
/*  279 */     this.connection = conn;
/*      */ 
/*  281 */     if (this.connection.getEnablePacketDebug()) {
/*  282 */       this.packetDebugRingBuffer = new LinkedList();
/*      */     }
/*  284 */     this.traceProtocol = this.connection.getTraceProtocol();
/*      */ 
/*  287 */     this.useAutoSlowLog = this.connection.getAutoSlowLog();
/*      */ 
/*  289 */     this.useBufferRowSizeThreshold = useBufferRowSizeThreshold;
/*  290 */     this.useDirectRowUnpack = this.connection.getUseDirectRowUnpack();
/*      */ 
/*  292 */     this.logSlowQueries = this.connection.getLogSlowQueries();
/*      */ 
/*  294 */     this.reusablePacket = new Buffer(1024);
/*  295 */     this.sendPacket = new Buffer(1024);
/*      */ 
/*  297 */     this.port = port;
/*  298 */     this.host = host;
/*      */ 
/*  300 */     this.socketFactoryClassName = socketFactoryClassName;
/*  301 */     this.socketFactory = createSocketFactory();
/*  302 */     this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */     try
/*      */     {
/*  305 */       this.mysqlConnection = this.socketFactory.connect(this.host, this.port, props);
/*      */ 
/*  309 */       if (socketTimeout != 0) {
/*      */         try {
/*  311 */           this.mysqlConnection.setSoTimeout(socketTimeout);
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/*  317 */       this.mysqlConnection = this.socketFactory.beforeHandshake();
/*      */ 
/*  319 */       if (this.connection.getUseReadAheadInput()) {
/*  320 */         this.mysqlInput = new ReadAheadInputStream(this.mysqlConnection.getInputStream(), 16384, this.connection.getTraceProtocol(), this.connection.getLog());
/*      */       }
/*  323 */       else if (this.connection.useUnbufferedInput())
/*  324 */         this.mysqlInput = this.mysqlConnection.getInputStream();
/*      */       else {
/*  326 */         this.mysqlInput = new BufferedInputStream(this.mysqlConnection.getInputStream(), 16384);
/*      */       }
/*      */ 
/*  330 */       this.mysqlOutput = new BufferedOutputStream(this.mysqlConnection.getOutputStream(), 16384);
/*      */ 
/*  334 */       this.isInteractiveClient = this.connection.getInteractiveClient();
/*  335 */       this.profileSql = this.connection.getProfileSql();
/*  336 */       this.autoGenerateTestcaseScript = this.connection.getAutoGenerateTestcaseScript();
/*      */ 
/*  338 */       this.needToGrabQueryFromPacket = ((this.profileSql) || (this.logSlowQueries) || (this.autoGenerateTestcaseScript));
/*      */ 
/*  342 */       if ((this.connection.getUseNanosForElapsedTime()) && (Util.nanoTimeAvailable()))
/*      */       {
/*  344 */         this.useNanosForElapsedTime = true;
/*      */ 
/*  346 */         this.queryTimingUnits = Messages.getString("Nanoseconds");
/*      */       } else {
/*  348 */         this.queryTimingUnits = Messages.getString("Milliseconds");
/*      */       }
/*      */ 
/*  351 */       if (this.connection.getLogSlowQueries())
/*  352 */         calculateSlowQueryThreshold();
/*      */     }
/*      */     catch (IOException ioEx) {
/*  355 */       throw SQLError.createCommunicationsException(this.connection, 0L, 0L, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean hasLongColumnInfo()
/*      */   {
/*  365 */     return this.hasLongColumnInfo;
/*      */   }
/*      */ 
/*      */   protected boolean isDataAvailable() throws SQLException {
/*      */     try {
/*  370 */       return this.mysqlInput.available() > 0; } catch (IOException ioEx) {
/*      */     }
/*  372 */     throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   protected long getLastPacketSentTimeMs()
/*      */   {
/*  383 */     return this.lastPacketSentTimeMs;
/*      */   }
/*      */ 
/*      */   protected long getLastPacketReceivedTimeMs() {
/*  387 */     return this.lastPacketReceivedTimeMs;
/*      */   }
/*      */ 
/*      */   protected ResultSetImpl getResultSet(StatementImpl callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/*  417 */     Field[] fields = null;
/*      */ 
/*  421 */     if (metadataFromCache == null) {
/*  422 */       fields = new Field[(int)columnCount];
/*      */ 
/*  424 */       for (int i = 0; i < columnCount; i++) {
/*  425 */         Buffer fieldPacket = null;
/*      */ 
/*  427 */         fieldPacket = readPacket();
/*  428 */         fields[i] = unpackField(fieldPacket, false);
/*      */       }
/*      */     } else {
/*  431 */       for (int i = 0; i < columnCount; i++) {
/*  432 */         skipPacket();
/*      */       }
/*      */     }
/*      */ 
/*  436 */     Buffer packet = reuseAndReadPacket(this.reusablePacket);
/*      */ 
/*  438 */     readServerStatusForResultSets(packet);
/*      */ 
/*  444 */     if ((this.connection.versionMeetsMinimum(5, 0, 2)) && (this.connection.getUseCursorFetch()) && (isBinaryEncoded) && (callingStatement != null) && (callingStatement.getFetchSize() != 0) && (callingStatement.getResultSetType() == 1003))
/*      */     {
/*  450 */       ServerPreparedStatement prepStmt = (ServerPreparedStatement)callingStatement;
/*      */ 
/*  452 */       boolean usingCursor = true;
/*      */ 
/*  460 */       if (this.connection.versionMeetsMinimum(5, 0, 5)) {
/*  461 */         usingCursor = (this.serverStatus & 0x40) != 0;
/*      */       }
/*      */ 
/*  465 */       if (usingCursor) {
/*  466 */         RowData rows = new RowDataCursor(this, prepStmt, fields);
/*      */ 
/*  471 */         ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, fields, rows, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*  477 */         if (usingCursor) {
/*  478 */           rs.setFetchSize(callingStatement.getFetchSize());
/*      */         }
/*      */ 
/*  481 */         return rs;
/*      */       }
/*      */     }
/*      */ 
/*  485 */     RowData rowData = null;
/*      */ 
/*  487 */     if (!streamResults) {
/*  488 */       rowData = readSingleRowSet(columnCount, maxRows, resultSetConcurrency, isBinaryEncoded, metadataFromCache == null ? fields : metadataFromCache);
/*      */     }
/*      */     else
/*      */     {
/*  492 */       rowData = new RowDataDynamic(this, (int)columnCount, metadataFromCache == null ? fields : metadataFromCache, isBinaryEncoded);
/*      */ 
/*  495 */       this.streamingData = rowData;
/*      */     }
/*      */ 
/*  498 */     ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, metadataFromCache == null ? fields : metadataFromCache, rowData, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*  504 */     return rs;
/*      */   }
/*      */ 
/*      */   protected NetworkResources getNetworkResources()
/*      */   {
/*  512 */     return new NetworkResources(this.mysqlConnection, this.mysqlInput, this.mysqlOutput);
/*      */   }
/*      */ 
/*      */   protected final void forceClose()
/*      */   {
/*      */     try
/*      */     {
/*  520 */       getNetworkResources().forceClose();
/*      */     } finally {
/*  522 */       this.mysqlConnection = null;
/*  523 */       this.mysqlInput = null;
/*  524 */       this.mysqlOutput = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void skipPacket()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  537 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*  540 */       if (lengthRead < 4) {
/*  541 */         forceClose();
/*  542 */         throw new IOException(Messages.getString("MysqlIO.1"));
/*      */       }
/*      */ 
/*  545 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/*  549 */       if (this.traceProtocol) {
/*  550 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/*  552 */         traceMessageBuf.append(Messages.getString("MysqlIO.2"));
/*  553 */         traceMessageBuf.append(packetLength);
/*  554 */         traceMessageBuf.append(Messages.getString("MysqlIO.3"));
/*  555 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */ 
/*  558 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */ 
/*  561 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/*  563 */       if (!this.packetSequenceReset) {
/*  564 */         if ((this.enablePacketDebug) && (this.checkPacketSequence))
/*  565 */           checkPacketSequencing(multiPacketSeq);
/*      */       }
/*      */       else {
/*  568 */         this.packetSequenceReset = false;
/*      */       }
/*      */ 
/*  571 */       this.readPacketSequence = multiPacketSeq;
/*      */ 
/*  573 */       skipFully(this.mysqlInput, packetLength);
/*      */     } catch (IOException ioEx) {
/*  575 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/*      */       try {
/*  579 */         this.connection.realClose(false, false, true, oom);
/*      */       } catch (Exception ex) {
/*      */       }
/*  582 */       throw oom;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final Buffer readPacket()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  597 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/*  600 */       if (lengthRead < 4) {
/*  601 */         forceClose();
/*  602 */         throw new IOException(Messages.getString("MysqlIO.1"));
/*      */       }
/*      */ 
/*  605 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/*  609 */       if (packetLength > this.maxAllowedPacket) {
/*  610 */         throw new PacketTooBigException(packetLength, this.maxAllowedPacket);
/*      */       }
/*      */ 
/*  613 */       if (this.traceProtocol) {
/*  614 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/*  616 */         traceMessageBuf.append(Messages.getString("MysqlIO.2"));
/*  617 */         traceMessageBuf.append(packetLength);
/*  618 */         traceMessageBuf.append(Messages.getString("MysqlIO.3"));
/*  619 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */ 
/*  622 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */ 
/*  625 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/*  627 */       if (!this.packetSequenceReset) {
/*  628 */         if ((this.enablePacketDebug) && (this.checkPacketSequence))
/*  629 */           checkPacketSequencing(multiPacketSeq);
/*      */       }
/*      */       else {
/*  632 */         this.packetSequenceReset = false;
/*      */       }
/*      */ 
/*  635 */       this.readPacketSequence = multiPacketSeq;
/*      */ 
/*  638 */       byte[] buffer = new byte[packetLength + 1];
/*  639 */       int numBytesRead = readFully(this.mysqlInput, buffer, 0, packetLength);
/*      */ 
/*  642 */       if (numBytesRead != packetLength) {
/*  643 */         throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
/*      */       }
/*      */ 
/*  647 */       buffer[packetLength] = 0;
/*      */ 
/*  649 */       Buffer packet = new Buffer(buffer);
/*  650 */       packet.setBufLength(packetLength + 1);
/*      */ 
/*  652 */       if (this.traceProtocol) {
/*  653 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/*  655 */         traceMessageBuf.append(Messages.getString("MysqlIO.4"));
/*  656 */         traceMessageBuf.append(getPacketDumpToLog(packet, packetLength));
/*      */ 
/*  659 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */ 
/*  662 */       if (this.enablePacketDebug) {
/*  663 */         enqueuePacketForDebugging(false, false, 0, this.packetHeaderBuf, packet);
/*      */       }
/*      */ 
/*  667 */       if (this.connection.getMaintainTimeStats()) {
/*  668 */         this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/*      */       }
/*      */ 
/*  671 */       return packet;
/*      */     } catch (IOException ioEx) {
/*  673 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/*      */       try {
/*  677 */         this.connection.realClose(false, false, true, oom); } catch (Exception ex) {
/*      */       }
/*      */     }
/*  680 */     throw oom;
/*      */   }
/*      */ 
/*      */   protected final Field unpackField(Buffer packet, boolean extractDefaultValues)
/*      */     throws SQLException
/*      */   {
/*  697 */     if (this.use41Extensions)
/*      */     {
/*  700 */       if (this.has41NewNewProt)
/*      */       {
/*  702 */         int catalogNameStart = packet.getPosition() + 1;
/*  703 */         int catalogNameLength = packet.fastSkipLenString();
/*  704 */         catalogNameStart = adjustStartForFieldLength(catalogNameStart, catalogNameLength);
/*      */       }
/*      */ 
/*  707 */       int databaseNameStart = packet.getPosition() + 1;
/*  708 */       int databaseNameLength = packet.fastSkipLenString();
/*  709 */       databaseNameStart = adjustStartForFieldLength(databaseNameStart, databaseNameLength);
/*      */ 
/*  711 */       int tableNameStart = packet.getPosition() + 1;
/*  712 */       int tableNameLength = packet.fastSkipLenString();
/*  713 */       tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */ 
/*  716 */       int originalTableNameStart = packet.getPosition() + 1;
/*  717 */       int originalTableNameLength = packet.fastSkipLenString();
/*  718 */       originalTableNameStart = adjustStartForFieldLength(originalTableNameStart, originalTableNameLength);
/*      */ 
/*  721 */       int nameStart = packet.getPosition() + 1;
/*  722 */       int nameLength = packet.fastSkipLenString();
/*      */ 
/*  724 */       nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */ 
/*  727 */       int originalColumnNameStart = packet.getPosition() + 1;
/*  728 */       int originalColumnNameLength = packet.fastSkipLenString();
/*  729 */       originalColumnNameStart = adjustStartForFieldLength(originalColumnNameStart, originalColumnNameLength);
/*      */ 
/*  731 */       packet.readByte();
/*      */ 
/*  733 */       short charSetNumber = (short)packet.readInt();
/*      */ 
/*  735 */       long colLength = 0L;
/*      */ 
/*  737 */       if (this.has41NewNewProt)
/*  738 */         colLength = packet.readLong();
/*      */       else {
/*  740 */         colLength = packet.readLongInt();
/*      */       }
/*      */ 
/*  743 */       int colType = packet.readByte() & 0xFF;
/*      */ 
/*  745 */       short colFlag = 0;
/*      */ 
/*  747 */       if (this.hasLongColumnInfo)
/*  748 */         colFlag = (short)packet.readInt();
/*      */       else {
/*  750 */         colFlag = (short)(packet.readByte() & 0xFF);
/*      */       }
/*      */ 
/*  753 */       int colDecimals = packet.readByte() & 0xFF;
/*      */ 
/*  755 */       int defaultValueStart = -1;
/*  756 */       int defaultValueLength = -1;
/*      */ 
/*  758 */       if (extractDefaultValues) {
/*  759 */         defaultValueStart = packet.getPosition() + 1;
/*  760 */         defaultValueLength = packet.fastSkipLenString();
/*      */       }
/*      */ 
/*  763 */       Field field = new Field(this.connection, packet.getByteBuffer(), databaseNameStart, databaseNameLength, tableNameStart, tableNameLength, originalTableNameStart, originalTableNameLength, nameStart, nameLength, originalColumnNameStart, originalColumnNameLength, colLength, colType, colFlag, colDecimals, defaultValueStart, defaultValueLength, charSetNumber);
/*      */ 
/*  771 */       return field;
/*      */     }
/*      */ 
/*  774 */     int tableNameStart = packet.getPosition() + 1;
/*  775 */     int tableNameLength = packet.fastSkipLenString();
/*  776 */     tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */ 
/*  778 */     int nameStart = packet.getPosition() + 1;
/*  779 */     int nameLength = packet.fastSkipLenString();
/*  780 */     nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */ 
/*  782 */     int colLength = packet.readnBytes();
/*  783 */     int colType = packet.readnBytes();
/*  784 */     packet.readByte();
/*      */ 
/*  786 */     short colFlag = 0;
/*      */ 
/*  788 */     if (this.hasLongColumnInfo)
/*  789 */       colFlag = (short)packet.readInt();
/*      */     else {
/*  791 */       colFlag = (short)(packet.readByte() & 0xFF);
/*      */     }
/*      */ 
/*  794 */     int colDecimals = packet.readByte() & 0xFF;
/*      */ 
/*  796 */     if (this.colDecimalNeedsBump) {
/*  797 */       colDecimals++;
/*      */     }
/*      */ 
/*  800 */     Field field = new Field(this.connection, packet.getByteBuffer(), nameStart, nameLength, tableNameStart, tableNameLength, colLength, colType, colFlag, colDecimals);
/*      */ 
/*  804 */     return field;
/*      */   }
/*      */ 
/*      */   private int adjustStartForFieldLength(int nameStart, int nameLength) {
/*  808 */     if (nameLength < 251) {
/*  809 */       return nameStart;
/*      */     }
/*      */ 
/*  812 */     if ((nameLength >= 251) && (nameLength < 65536)) {
/*  813 */       return nameStart + 2;
/*      */     }
/*      */ 
/*  816 */     if ((nameLength >= 65536) && (nameLength < 16777216)) {
/*  817 */       return nameStart + 3;
/*      */     }
/*      */ 
/*  820 */     return nameStart + 8;
/*      */   }
/*      */ 
/*      */   protected boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
/*  824 */     if ((this.use41Extensions) && (this.connection.getElideSetAutoCommits())) {
/*  825 */       boolean autoCommitModeOnServer = (this.serverStatus & 0x2) != 0;
/*      */ 
/*  828 */       if ((!autoCommitFlag) && (versionMeetsMinimum(5, 0, 0)))
/*      */       {
/*  832 */         boolean inTransactionOnServer = (this.serverStatus & 0x1) != 0;
/*      */ 
/*  835 */         return !inTransactionOnServer;
/*      */       }
/*      */ 
/*  838 */       return autoCommitModeOnServer != autoCommitFlag;
/*      */     }
/*      */ 
/*  841 */     return true;
/*      */   }
/*      */ 
/*      */   protected boolean inTransactionOnServer() {
/*  845 */     return (this.serverStatus & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   protected void changeUser(String userName, String password, String database)
/*      */     throws SQLException
/*      */   {
/*  859 */     this.packetSequence = -1;
/*  860 */     this.compressedPacketSequence = -1;
/*      */ 
/*  862 */     int passwordLength = 16;
/*  863 */     int userLength = userName != null ? userName.length() : 0;
/*  864 */     int databaseLength = database != null ? database.length() : 0;
/*      */ 
/*  866 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */ 
/*  868 */     if ((this.serverCapabilities & 0x80000) != 0)
/*      */     {
/*  870 */       proceedHandshakeWithPluggableAuthentication(userName, password, database, null);
/*      */     }
/*  872 */     else if ((this.serverCapabilities & 0x8000) != 0) {
/*  873 */       Buffer changeUserPacket = new Buffer(packLength + 1);
/*  874 */       changeUserPacket.writeByte(17);
/*      */ 
/*  876 */       if (versionMeetsMinimum(4, 1, 1)) {
/*  877 */         secureAuth411(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */       else {
/*  880 */         secureAuth(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  885 */       Buffer packet = new Buffer(packLength);
/*  886 */       packet.writeByte(17);
/*      */ 
/*  889 */       packet.writeString(userName);
/*      */ 
/*  891 */       if (this.protocolVersion > 9)
/*  892 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       else {
/*  894 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       }
/*      */ 
/*  897 */       boolean localUseConnectWithDb = (this.useConnectWithDb) && (database != null) && (database.length() > 0);
/*      */ 
/*  900 */       if (localUseConnectWithDb) {
/*  901 */         packet.writeString(database);
/*      */       }
/*      */ 
/*  907 */       send(packet, packet.getPosition());
/*  908 */       checkErrorPacket();
/*      */ 
/*  910 */       if (!localUseConnectWithDb)
/*  911 */         changeDatabaseTo(database);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Buffer checkErrorPacket()
/*      */     throws SQLException
/*      */   {
/*  925 */     return checkErrorPacket(-1);
/*      */   }
/*      */ 
/*      */   protected void checkForCharsetMismatch()
/*      */   {
/*  932 */     if ((this.connection.getUseUnicode()) && (this.connection.getEncoding() != null))
/*      */     {
/*  934 */       String encodingToCheck = jvmPlatformCharset;
/*      */ 
/*  936 */       if (encodingToCheck == null) {
/*  937 */         encodingToCheck = System.getProperty("file.encoding");
/*      */       }
/*      */ 
/*  940 */       if (encodingToCheck == null)
/*  941 */         this.platformDbCharsetMatches = false;
/*      */       else
/*  943 */         this.platformDbCharsetMatches = encodingToCheck.equals(this.connection.getEncoding());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void clearInputStream() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  951 */       int len = this.mysqlInput.available();
/*      */ 
/*  953 */       while (len > 0) {
/*  954 */         this.mysqlInput.skip(len);
/*  955 */         len = this.mysqlInput.available();
/*      */       }
/*      */     } catch (IOException ioEx) {
/*  958 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void resetReadPacketSequence()
/*      */   {
/*  964 */     this.readPacketSequence = 0;
/*      */   }
/*      */ 
/*      */   protected void dumpPacketRingBuffer() throws SQLException {
/*  968 */     if ((this.packetDebugRingBuffer != null) && (this.connection.getEnablePacketDebug()))
/*      */     {
/*  970 */       StringBuffer dumpBuffer = new StringBuffer();
/*      */ 
/*  972 */       dumpBuffer.append("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
/*      */ 
/*  974 */       dumpBuffer.append("\n");
/*      */ 
/*  976 */       Iterator ringBufIter = this.packetDebugRingBuffer.iterator();
/*  977 */       while (ringBufIter.hasNext()) {
/*  978 */         dumpBuffer.append((StringBuffer)ringBufIter.next());
/*  979 */         dumpBuffer.append("\n");
/*      */       }
/*      */ 
/*  982 */       this.connection.getLog().logTrace(dumpBuffer.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void explainSlowQuery(byte[] querySQL, String truncatedQuery)
/*      */     throws SQLException
/*      */   {
/*  996 */     if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT"))
/*      */     {
/*  998 */       PreparedStatement stmt = null;
/*  999 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 1002 */         stmt = (PreparedStatement)this.connection.clientPrepareStatement("EXPLAIN ?");
/* 1003 */         stmt.setBytesNoEscapeNoQuotes(1, querySQL);
/* 1004 */         rs = stmt.executeQuery();
/*      */ 
/* 1006 */         StringBuffer explainResults = new StringBuffer(Messages.getString("MysqlIO.8") + truncatedQuery + Messages.getString("MysqlIO.9"));
/*      */ 
/* 1010 */         ResultSetUtil.appendResultSetSlashGStyle(explainResults, rs);
/*      */ 
/* 1012 */         this.connection.getLog().logWarn(explainResults.toString());
/*      */       } catch (SQLException sqlEx) {
/*      */       } finally {
/* 1015 */         if (rs != null) {
/* 1016 */           rs.close();
/*      */         }
/*      */ 
/* 1019 */         if (stmt != null)
/* 1020 */           stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static int getMaxBuf()
/*      */   {
/* 1028 */     return maxBufferSize;
/*      */   }
/*      */ 
/*      */   final int getServerMajorVersion()
/*      */   {
/* 1037 */     return this.serverMajorVersion;
/*      */   }
/*      */ 
/*      */   final int getServerMinorVersion()
/*      */   {
/* 1046 */     return this.serverMinorVersion;
/*      */   }
/*      */ 
/*      */   final int getServerSubMinorVersion()
/*      */   {
/* 1055 */     return this.serverSubMinorVersion;
/*      */   }
/*      */ 
/*      */   String getServerVersion()
/*      */   {
/* 1064 */     return this.serverVersion;
/*      */   }
/*      */ 
/*      */   void doHandshake(String user, String password, String database)
/*      */     throws SQLException
/*      */   {
/* 1081 */     this.checkPacketSequence = false;
/* 1082 */     this.readPacketSequence = 0;
/*      */ 
/* 1084 */     Buffer buf = readPacket();
/*      */ 
/* 1087 */     this.protocolVersion = buf.readByte();
/*      */ 
/* 1089 */     if (this.protocolVersion == -1) {
/*      */       try {
/* 1091 */         this.mysqlConnection.close();
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/* 1096 */       int errno = 2000;
/*      */ 
/* 1098 */       errno = buf.readInt();
/*      */ 
/* 1100 */       String serverErrorMessage = buf.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1102 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.10"));
/*      */ 
/* 1104 */       errorBuf.append(serverErrorMessage);
/* 1105 */       errorBuf.append("\"");
/*      */ 
/* 1107 */       String xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */ 
/* 1110 */       throw SQLError.createSQLException(SQLError.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1114 */     this.serverVersion = buf.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1117 */     int point = this.serverVersion.indexOf('.');
/*      */ 
/* 1119 */     if (point != -1) {
/*      */       try {
/* 1121 */         int n = Integer.parseInt(this.serverVersion.substring(0, point));
/* 1122 */         this.serverMajorVersion = n;
/*      */       }
/*      */       catch (NumberFormatException NFE1)
/*      */       {
/*      */       }
/* 1127 */       String remaining = this.serverVersion.substring(point + 1, this.serverVersion.length());
/*      */ 
/* 1129 */       point = remaining.indexOf('.');
/*      */ 
/* 1131 */       if (point != -1) {
/*      */         try {
/* 1133 */           int n = Integer.parseInt(remaining.substring(0, point));
/* 1134 */           this.serverMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/*      */         }
/* 1139 */         remaining = remaining.substring(point + 1, remaining.length());
/*      */ 
/* 1141 */         int pos = 0;
/*      */ 
/* 1143 */         while ((pos < remaining.length()) && 
/* 1144 */           (remaining.charAt(pos) >= '0') && (remaining.charAt(pos) <= '9'))
/*      */         {
/* 1149 */           pos++;
/*      */         }
/*      */         try
/*      */         {
/* 1153 */           int n = Integer.parseInt(remaining.substring(0, pos));
/* 1154 */           this.serverSubMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/* 1161 */     if (versionMeetsMinimum(4, 0, 8)) {
/* 1162 */       this.maxThreeBytes = 16777215;
/* 1163 */       this.useNewLargePackets = true;
/*      */     } else {
/* 1165 */       this.maxThreeBytes = 16581375;
/* 1166 */       this.useNewLargePackets = false;
/*      */     }
/*      */ 
/* 1169 */     this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
/* 1170 */     this.colDecimalNeedsBump = (!versionMeetsMinimum(3, 23, 15));
/* 1171 */     this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
/*      */ 
/* 1173 */     this.threadId = buf.readLong();
/* 1174 */     this.seed = buf.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1176 */     this.serverCapabilities = 0;
/*      */ 
/* 1178 */     if (buf.getPosition() < buf.getBufLength()) {
/* 1179 */       this.serverCapabilities = buf.readInt();
/*      */     }
/*      */ 
/* 1182 */     if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1183 */       int position = buf.getPosition();
/*      */ 
/* 1186 */       this.serverCharsetIndex = (buf.readByte() & 0xFF);
/* 1187 */       this.serverStatus = buf.readInt();
/* 1188 */       checkTransactionState(0);
/*      */ 
/* 1190 */       this.serverCapabilities += 65536 * buf.readInt();
/* 1191 */       this.authPluginDataLength = (buf.readByte() & 0xFF);
/*      */ 
/* 1193 */       buf.setPosition(position + 16);
/*      */ 
/* 1195 */       String seedPart2 = buf.readString("ASCII", getExceptionInterceptor());
/* 1196 */       StringBuffer newSeed = new StringBuffer(20);
/* 1197 */       newSeed.append(this.seed);
/* 1198 */       newSeed.append(seedPart2);
/* 1199 */       this.seed = newSeed.toString();
/*      */     }
/*      */ 
/* 1202 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1204 */       this.clientParam |= 32L;
/*      */     }
/*      */ 
/* 1207 */     this.useConnectWithDb = ((database != null) && (database.length() > 0) && (!this.connection.getCreateDatabaseIfNotExist()));
/*      */ 
/* 1211 */     if (this.useConnectWithDb) {
/* 1212 */       this.clientParam |= 8L;
/*      */     }
/*      */ 
/* 1215 */     if (((this.serverCapabilities & 0x800) == 0) && (this.connection.getUseSSL()))
/*      */     {
/* 1217 */       if (this.connection.getRequireSSL()) {
/* 1218 */         this.connection.close();
/* 1219 */         forceClose();
/* 1220 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.15"), "08001", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1224 */       this.connection.setUseSSL(false);
/*      */     }
/*      */ 
/* 1227 */     if ((this.serverCapabilities & 0x4) != 0)
/*      */     {
/* 1229 */       this.clientParam |= 4L;
/* 1230 */       this.hasLongColumnInfo = true;
/*      */     }
/*      */ 
/* 1234 */     if (!this.connection.getUseAffectedRows()) {
/* 1235 */       this.clientParam |= 2L;
/*      */     }
/*      */ 
/* 1238 */     if (this.connection.getAllowLoadLocalInfile()) {
/* 1239 */       this.clientParam |= 128L;
/*      */     }
/*      */ 
/* 1242 */     if (this.isInteractiveClient) {
/* 1243 */       this.clientParam |= 1024L;
/*      */     }
/*      */ 
/* 1249 */     if ((this.serverCapabilities & 0x80000) != 0) {
/* 1250 */       proceedHandshakeWithPluggableAuthentication(user, password, database, buf);
/* 1251 */       return;
/*      */     }
/*      */ 
/* 1255 */     if (this.protocolVersion > 9)
/* 1256 */       this.clientParam |= 1L;
/*      */     else {
/* 1258 */       this.clientParam &= -2L;
/*      */     }
/*      */ 
/* 1264 */     if ((versionMeetsMinimum(4, 1, 0)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x4000) != 0))) {
/* 1265 */       if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1266 */         this.clientParam |= 512L;
/* 1267 */         this.has41NewNewProt = true;
/*      */ 
/* 1270 */         this.clientParam |= 8192L;
/*      */ 
/* 1273 */         this.clientParam |= 131072L;
/*      */ 
/* 1278 */         if (this.connection.getAllowMultiQueries())
/* 1279 */           this.clientParam |= 65536L;
/*      */       }
/*      */       else {
/* 1282 */         this.clientParam |= 16384L;
/* 1283 */         this.has41NewNewProt = false;
/*      */       }
/*      */ 
/* 1286 */       this.use41Extensions = true;
/*      */     }
/*      */ 
/* 1289 */     int passwordLength = 16;
/* 1290 */     int userLength = user != null ? user.length() : 0;
/* 1291 */     int databaseLength = database != null ? database.length() : 0;
/*      */ 
/* 1293 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */ 
/* 1295 */     Buffer packet = null;
/*      */ 
/* 1297 */     if (!this.connection.getUseSSL()) {
/* 1298 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1299 */         this.clientParam |= 32768L;
/*      */ 
/* 1301 */         if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1302 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         }
/*      */         else
/* 1305 */           secureAuth(null, packLength, user, password, database, true);
/*      */       }
/*      */       else
/*      */       {
/* 1309 */         packet = new Buffer(packLength);
/*      */ 
/* 1311 */         if ((this.clientParam & 0x4000) != 0L) {
/* 1312 */           if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1313 */             packet.writeLong(this.clientParam);
/* 1314 */             packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 1319 */             packet.writeByte(8);
/*      */ 
/* 1322 */             packet.writeBytesNoNull(new byte[23]);
/*      */           } else {
/* 1324 */             packet.writeLong(this.clientParam);
/* 1325 */             packet.writeLong(this.maxThreeBytes);
/*      */           }
/*      */         } else {
/* 1328 */           packet.writeInt((int)this.clientParam);
/* 1329 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         }
/*      */ 
/* 1333 */         packet.writeString(user, "Cp1252", this.connection);
/*      */ 
/* 1335 */         if (this.protocolVersion > 9)
/* 1336 */           packet.writeString(Util.newCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         else {
/* 1338 */           packet.writeString(Util.oldCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         }
/*      */ 
/* 1341 */         if (this.useConnectWithDb) {
/* 1342 */           packet.writeString(database, "Cp1252", this.connection);
/*      */         }
/*      */ 
/* 1345 */         send(packet, packet.getPosition());
/*      */       }
/*      */     } else {
/* 1348 */       negotiateSSLConnection(user, password, database, packLength);
/*      */ 
/* 1350 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1351 */         if (versionMeetsMinimum(4, 1, 1))
/* 1352 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         else
/* 1354 */           secureAuth411(null, packLength, user, password, database, true);
/*      */       }
/*      */       else
/*      */       {
/* 1358 */         packet = new Buffer(packLength);
/*      */ 
/* 1360 */         if (this.use41Extensions) {
/* 1361 */           packet.writeLong(this.clientParam);
/* 1362 */           packet.writeLong(this.maxThreeBytes);
/*      */         } else {
/* 1364 */           packet.writeInt((int)this.clientParam);
/* 1365 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         }
/*      */ 
/* 1369 */         packet.writeString(user);
/*      */ 
/* 1371 */         if (this.protocolVersion > 9)
/* 1372 */           packet.writeString(Util.newCrypt(password, this.seed));
/*      */         else {
/* 1374 */           packet.writeString(Util.oldCrypt(password, this.seed));
/*      */         }
/*      */ 
/* 1377 */         if (((this.serverCapabilities & 0x8) != 0) && (database != null) && (database.length() > 0))
/*      */         {
/* 1379 */           packet.writeString(database);
/*      */         }
/*      */ 
/* 1382 */         send(packet, packet.getPosition());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1390 */     if ((!versionMeetsMinimum(4, 1, 1)) && (this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0)) {
/* 1391 */       checkErrorPacket();
/*      */     }
/*      */ 
/* 1397 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1401 */       this.deflater = new Deflater();
/* 1402 */       this.useCompression = true;
/* 1403 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     }
/*      */ 
/* 1407 */     if (!this.useConnectWithDb) {
/* 1408 */       changeDatabaseTo(database);
/*      */     }
/*      */     try
/*      */     {
/* 1412 */       this.mysqlConnection = this.socketFactory.afterHandshake();
/*      */     } catch (IOException ioEx) {
/* 1414 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void loadAuthenticationPlugins()
/*      */     throws SQLException
/*      */   {
/* 1459 */     this.defaultAuthenticationPlugin = this.connection.getDefaultAuthenticationPlugin();
/* 1460 */     if ((this.defaultAuthenticationPlugin == null) || ("".equals(this.defaultAuthenticationPlugin.trim()))) {
/* 1461 */       throw SQLError.createSQLException(Messages.getString("Connection.BadDefaultAuthenticationPlugin", new Object[] { this.defaultAuthenticationPlugin }), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1468 */     String disabledPlugins = this.connection.getDisabledAuthenticationPlugins();
/* 1469 */     if ((disabledPlugins != null) && (!"".equals(disabledPlugins))) {
/* 1470 */       this.disabledAuthenticationPlugins = new ArrayList();
/* 1471 */       List pluginsToDisable = StringUtils.split(disabledPlugins, ",", true);
/* 1472 */       Iterator iter = pluginsToDisable.iterator();
/* 1473 */       while (iter.hasNext()) {
/* 1474 */         this.disabledAuthenticationPlugins.add(iter.next());
/*      */       }
/*      */     }
/*      */ 
/* 1478 */     this.authenticationPlugins = new HashMap();
/*      */ 
/* 1481 */     AuthenticationPlugin plugin = new MysqlOldPasswordPlugin();
/* 1482 */     plugin.init(this.connection, this.connection.getProperties());
/* 1483 */     boolean defaultIsFound = addAuthenticationPlugin(plugin);
/*      */ 
/* 1485 */     plugin = new MysqlNativePasswordPlugin();
/* 1486 */     plugin.init(this.connection, this.connection.getProperties());
/* 1487 */     if (addAuthenticationPlugin(plugin)) defaultIsFound = true;
/*      */ 
/* 1489 */     plugin = new MysqlClearPasswordPlugin();
/* 1490 */     plugin.init(this.connection, this.connection.getProperties());
/* 1491 */     if (addAuthenticationPlugin(plugin)) defaultIsFound = true;
/*      */ 
/* 1493 */     plugin = new Sha256PasswordPlugin();
/* 1494 */     plugin.init(this.connection, this.connection.getProperties());
/* 1495 */     if (addAuthenticationPlugin(plugin)) defaultIsFound = true;
/*      */ 
/* 1498 */     String authenticationPluginClasses = this.connection.getAuthenticationPlugins();
/* 1499 */     if ((authenticationPluginClasses != null) && (!"".equals(authenticationPluginClasses)))
/*      */     {
/* 1501 */       List plugins = Util.loadExtensions(this.connection, this.connection.getProperties(), authenticationPluginClasses, "Connection.BadAuthenticationPlugin", getExceptionInterceptor());
/*      */ 
/* 1505 */       for (Extension object : plugins) {
/* 1506 */         plugin = (AuthenticationPlugin)object;
/* 1507 */         if (addAuthenticationPlugin(plugin)) defaultIsFound = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1512 */     if (!defaultIsFound)
/* 1513 */       throw SQLError.createSQLException(Messages.getString("Connection.DefaultAuthenticationPluginIsNotListed", new Object[] { this.defaultAuthenticationPlugin }), getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private boolean addAuthenticationPlugin(AuthenticationPlugin plugin)
/*      */     throws SQLException
/*      */   {
/* 1529 */     boolean isDefault = false;
/* 1530 */     String pluginClassName = plugin.getClass().getName();
/* 1531 */     String pluginProtocolName = plugin.getProtocolPluginName();
/* 1532 */     boolean disabledByClassName = (this.disabledAuthenticationPlugins != null) && (this.disabledAuthenticationPlugins.contains(pluginClassName));
/*      */ 
/* 1535 */     boolean disabledByMechanism = (this.disabledAuthenticationPlugins != null) && (this.disabledAuthenticationPlugins.contains(pluginProtocolName));
/*      */ 
/* 1539 */     if ((disabledByClassName) || (disabledByMechanism))
/*      */     {
/* 1541 */       if (this.defaultAuthenticationPlugin.equals(pluginClassName)) {
/* 1542 */         throw SQLError.createSQLException(Messages.getString("Connection.BadDisabledAuthenticationPlugin", new Object[] { disabledByClassName ? pluginClassName : pluginProtocolName }), getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1548 */       this.authenticationPlugins.put(pluginProtocolName, plugin);
/* 1549 */       if (this.defaultAuthenticationPlugin.equals(pluginClassName)) {
/* 1550 */         this.defaultAuthenticationPluginProtocolName = pluginProtocolName;
/* 1551 */         isDefault = true;
/*      */       }
/*      */     }
/* 1554 */     return isDefault;
/*      */   }
/*      */ 
/*      */   private AuthenticationPlugin getAuthenticationPlugin(String pluginName)
/*      */     throws SQLException
/*      */   {
/* 1574 */     AuthenticationPlugin plugin = (AuthenticationPlugin)this.authenticationPlugins.get(pluginName);
/*      */ 
/* 1576 */     if ((plugin != null) && (!plugin.isReusable())) {
/*      */       try {
/* 1578 */         plugin = (AuthenticationPlugin)plugin.getClass().newInstance();
/* 1579 */         plugin.init(this.connection, this.connection.getProperties());
/*      */       } catch (Throwable t) {
/* 1581 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.BadAuthenticationPlugin", new Object[] { plugin.getClass().getName() }), getExceptionInterceptor());
/*      */ 
/* 1583 */         sqlEx.initCause(t);
/* 1584 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */ 
/* 1588 */     return plugin;
/*      */   }
/*      */ 
/*      */   private void checkConfidentiality(AuthenticationPlugin plugin)
/*      */     throws SQLException
/*      */   {
/* 1597 */     if ((plugin.requiresConfidentiality()) && ((!this.connection.getUseSSL()) || (!this.connection.getRequireSSL())))
/* 1598 */       throw SQLError.createSQLException(Messages.getString("Connection.AuthenticationPluginRequiresSSL", new Object[] { plugin.getProtocolPluginName() }), getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private void proceedHandshakeWithPluggableAuthentication(String user, String password, String database, Buffer challenge)
/*      */     throws SQLException
/*      */   {
/* 1625 */     if (this.authenticationPlugins == null) {
/* 1626 */       loadAuthenticationPlugins();
/*      */     }
/*      */ 
/* 1629 */     int passwordLength = 16;
/* 1630 */     int userLength = user != null ? user.length() : 0;
/* 1631 */     int databaseLength = database != null ? database.length() : 0;
/*      */ 
/* 1633 */     int packLength = (userLength + passwordLength + databaseLength) * 2 + 7 + 4 + 33;
/*      */ 
/* 1635 */     AuthenticationPlugin plugin = null;
/* 1636 */     Buffer fromServer = null;
/* 1637 */     ArrayList toServer = new ArrayList();
/* 1638 */     Boolean done = null;
/* 1639 */     Buffer last_sent = null;
/*      */ 
/* 1641 */     boolean old_raw_challenge = false;
/*      */ 
/* 1643 */     int counter = 100;
/*      */ 
/* 1645 */     while (0 < counter--)
/*      */     {
/* 1647 */       if (done == null)
/*      */       {
/* 1649 */         if (challenge != null)
/*      */         {
/* 1652 */           this.clientParam |= 696833L;
/*      */ 
/* 1664 */           if (this.connection.getAllowMultiQueries()) {
/* 1665 */             this.clientParam |= 65536L;
/*      */           }
/*      */ 
/* 1668 */           if (((this.serverCapabilities & 0x400000) != 0) && (!this.connection.getDisconnectOnExpiredPasswords())) {
/* 1669 */             this.clientParam |= 4194304L;
/*      */           }
/*      */ 
/* 1672 */           this.has41NewNewProt = true;
/* 1673 */           this.use41Extensions = true;
/*      */ 
/* 1675 */           if (this.connection.getUseSSL()) {
/* 1676 */             negotiateSSLConnection(user, password, database, packLength);
/*      */           }
/*      */ 
/* 1679 */           String pluginName = null;
/*      */ 
/* 1681 */           if ((this.serverCapabilities & 0x80000) != 0) {
/* 1682 */             if ((!versionMeetsMinimum(5, 5, 10)) || ((versionMeetsMinimum(5, 6, 0)) && (!versionMeetsMinimum(5, 6, 2))))
/* 1683 */               pluginName = challenge.readString("ASCII", getExceptionInterceptor(), this.authPluginDataLength);
/*      */             else {
/* 1685 */               pluginName = challenge.readString("ASCII", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */ 
/* 1689 */           plugin = getAuthenticationPlugin(pluginName);
/*      */ 
/* 1691 */           if (plugin == null) plugin = getAuthenticationPlugin(this.defaultAuthenticationPluginProtocolName);
/*      */ 
/* 1693 */           checkConfidentiality(plugin);
/* 1694 */           fromServer = new Buffer(StringUtils.getBytes(this.seed));
/*      */         }
/*      */         else {
/* 1697 */           plugin = getAuthenticationPlugin(this.defaultAuthenticationPluginProtocolName);
/* 1698 */           checkConfidentiality(plugin);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1704 */         challenge = checkErrorPacket();
/* 1705 */         old_raw_challenge = false;
/*      */ 
/* 1707 */         if (challenge.isOKPacket())
/*      */         {
/* 1709 */           if (!done.booleanValue()) {
/* 1710 */             throw SQLError.createSQLException(Messages.getString("Connection.UnexpectedAuthenticationApproval", new Object[] { plugin.getProtocolPluginName() }), getExceptionInterceptor());
/*      */           }
/* 1712 */           plugin.destroy();
/* 1713 */           break;
/*      */         }
/* 1715 */         if (challenge.isAuthMethodSwitchRequestPacket())
/*      */         {
/* 1717 */           String pluginName = challenge.readString("ASCII", getExceptionInterceptor());
/*      */ 
/* 1720 */           if ((plugin != null) && (!plugin.getProtocolPluginName().equals(pluginName))) {
/* 1721 */             plugin.destroy();
/* 1722 */             plugin = getAuthenticationPlugin(pluginName);
/*      */ 
/* 1724 */             if (plugin == null) {
/* 1725 */               throw SQLError.createSQLException(Messages.getString("Connection.BadAuthenticationPlugin", new Object[] { pluginName }), getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */ 
/* 1729 */           checkConfidentiality(plugin);
/* 1730 */           fromServer = new Buffer(StringUtils.getBytes(challenge.readString("ASCII", getExceptionInterceptor())));
/*      */         }
/* 1734 */         else if (versionMeetsMinimum(5, 5, 16)) {
/* 1735 */           fromServer = new Buffer(challenge.getBytes(challenge.getPosition(), challenge.getBufLength() - challenge.getPosition()));
/*      */         } else {
/* 1737 */           old_raw_challenge = true;
/* 1738 */           fromServer = new Buffer(challenge.getBytes(challenge.getPosition() - 1, challenge.getBufLength() - challenge.getPosition() + 1));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1746 */         plugin.setAuthenticationParameters(user, password);
/* 1747 */         done = Boolean.valueOf(plugin.nextAuthenticationStep(fromServer, toServer));
/*      */       } catch (SQLException e) {
/* 1749 */         throw SQLError.createSQLException(e.getMessage(), e.getSQLState(), e, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1753 */       if (toServer.size() > 0) {
/* 1754 */         if (challenge == null)
/*      */         {
/* 1757 */           String enc = this.connection.getEncoding();
/* 1758 */           int charsetIndex = 0;
/* 1759 */           if (enc != null)
/* 1760 */             charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(CharsetMapping.getMysqlEncodingForJavaEncoding(enc, this.connection));
/*      */           else {
/* 1762 */             enc = "utf-8";
/*      */           }
/* 1764 */           if (charsetIndex == 0) {
/* 1765 */             charsetIndex = 33;
/*      */           }
/*      */ 
/* 1769 */           last_sent = new Buffer(packLength + 1);
/* 1770 */           last_sent.writeByte(17);
/*      */ 
/* 1773 */           last_sent.writeString(user, enc, this.connection);
/*      */ 
/* 1775 */           last_sent.writeByte((byte)((Buffer)toServer.get(0)).getBufLength());
/* 1776 */           last_sent.writeBytesNoNull(((Buffer)toServer.get(0)).getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/*      */ 
/* 1778 */           if (this.useConnectWithDb) {
/* 1779 */             last_sent.writeString(database, enc, this.connection);
/*      */           }
/*      */           else {
/* 1782 */             last_sent.writeByte(0);
/*      */           }
/*      */ 
/* 1786 */           last_sent.writeByte((byte)(charsetIndex % 256));
/* 1787 */           if (charsetIndex > 255)
/* 1788 */             last_sent.writeByte((byte)(charsetIndex / 256));
/*      */           else {
/* 1790 */             last_sent.writeByte(0);
/*      */           }
/*      */ 
/* 1794 */           if ((this.serverCapabilities & 0x80000) != 0) {
/* 1795 */             last_sent.writeString(plugin.getProtocolPluginName(), enc, this.connection);
/*      */           }
/*      */ 
/* 1798 */           send(last_sent, last_sent.getPosition());
/*      */ 
/* 1800 */           continue; } if (challenge.isAuthMethodSwitchRequestPacket())
/*      */         {
/* 1803 */           byte savePacketSequence = this.packetSequence++;
/* 1804 */           savePacketSequence = (byte)(savePacketSequence + 1); this.packetSequence = savePacketSequence;
/*      */ 
/* 1806 */           last_sent = new Buffer(((Buffer)toServer.get(0)).getBufLength() + 4);
/* 1807 */           last_sent.writeBytesNoNull(((Buffer)toServer.get(0)).getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/* 1808 */           send(last_sent, last_sent.getPosition());
/*      */ 
/* 1810 */           continue; } if ((challenge.isRawPacket()) || (old_raw_challenge))
/*      */         {
/* 1812 */           byte savePacketSequence = this.packetSequence++;
/*      */ 
/* 1814 */           for (Buffer buffer : toServer) {
/* 1815 */             savePacketSequence = (byte)(savePacketSequence + 1); this.packetSequence = savePacketSequence;
/*      */ 
/* 1817 */             last_sent = new Buffer(buffer.getBufLength() + 4);
/* 1818 */             last_sent.writeBytesNoNull(buffer.getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/* 1819 */             send(last_sent, last_sent.getPosition());
/*      */           }
/*      */ 
/* 1822 */           continue;
/*      */         }
/*      */ 
/* 1825 */         last_sent = new Buffer(packLength);
/* 1826 */         last_sent.writeLong(this.clientParam);
/* 1827 */         last_sent.writeLong(this.maxThreeBytes);
/*      */ 
/* 1832 */         last_sent.writeByte(33);
/*      */ 
/* 1834 */         last_sent.writeBytesNoNull(new byte[23]);
/*      */ 
/* 1837 */         last_sent.writeString(user, "utf-8", this.connection);
/*      */ 
/* 1839 */         last_sent.writeByte((byte)((Buffer)toServer.get(0)).getBufLength());
/* 1840 */         last_sent.writeBytesNoNull(((Buffer)toServer.get(0)).getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/*      */ 
/* 1842 */         if (this.useConnectWithDb) {
/* 1843 */           last_sent.writeString(database, "utf-8", this.connection);
/*      */         }
/*      */         else {
/* 1846 */           last_sent.writeByte(0);
/*      */         }
/*      */ 
/* 1849 */         if ((this.serverCapabilities & 0x80000) != 0) {
/* 1850 */           last_sent.writeString(plugin.getProtocolPluginName(), "utf-8", this.connection);
/*      */         }
/*      */ 
/* 1853 */         send(last_sent, last_sent.getPosition());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1860 */     if (counter == 0) {
/* 1861 */       throw SQLError.createSQLException(Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 1867 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1871 */       this.deflater = new Deflater();
/* 1872 */       this.useCompression = true;
/* 1873 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     }
/*      */ 
/* 1876 */     if (!this.useConnectWithDb) {
/* 1877 */       changeDatabaseTo(database);
/*      */     }
/*      */     try
/*      */     {
/* 1881 */       this.mysqlConnection = this.socketFactory.afterHandshake();
/*      */     } catch (IOException ioEx) {
/* 1883 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void changeDatabaseTo(String database) throws SQLException {
/* 1888 */     if ((database == null) || (database.length() == 0)) {
/* 1889 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1893 */       sendCommand(2, database, null, false, null, 0);
/*      */     } catch (Exception ex) {
/* 1895 */       if (this.connection.getCreateDatabaseIfNotExist()) {
/* 1896 */         sendCommand(3, "CREATE DATABASE IF NOT EXISTS " + database, null, false, null, 0);
/*      */ 
/* 1899 */         sendCommand(2, database, null, false, null, 0);
/*      */       } else {
/* 1901 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   final ResultSetRow nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacketForBufferRow, Buffer existingRowPacket)
/*      */     throws SQLException
/*      */   {
/* 1929 */     if ((this.useDirectRowUnpack) && (existingRowPacket == null) && (!isBinaryEncoded) && (!useBufferRowIfPossible) && (!useBufferRowExplicit))
/*      */     {
/* 1932 */       return nextRowFast(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacketForBufferRow);
/*      */     }
/*      */ 
/* 1936 */     Buffer rowPacket = null;
/*      */ 
/* 1938 */     if (existingRowPacket == null) {
/* 1939 */       rowPacket = checkErrorPacket();
/*      */ 
/* 1941 */       if ((!useBufferRowExplicit) && (useBufferRowIfPossible) && 
/* 1942 */         (rowPacket.getBufLength() > this.useBufferRowSizeThreshold)) {
/* 1943 */         useBufferRowExplicit = true;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1949 */       rowPacket = existingRowPacket;
/* 1950 */       checkErrorPacket(existingRowPacket);
/*      */     }
/*      */ 
/* 1954 */     if (!isBinaryEncoded)
/*      */     {
/* 1959 */       rowPacket.setPosition(rowPacket.getPosition() - 1);
/*      */ 
/* 1961 */       if (!rowPacket.isLastDataPacket()) {
/* 1962 */         if ((resultSetConcurrency == 1008) || ((!useBufferRowIfPossible) && (!useBufferRowExplicit)))
/*      */         {
/* 1965 */           byte[][] rowData = new byte[columnCount][];
/*      */ 
/* 1967 */           for (int i = 0; i < columnCount; i++) {
/* 1968 */             rowData[i] = rowPacket.readLenByteArray(0);
/*      */           }
/*      */ 
/* 1971 */           return new ByteArrayRow(rowData, getExceptionInterceptor());
/*      */         }
/*      */ 
/* 1974 */         if (!canReuseRowPacketForBufferRow) {
/* 1975 */           this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */         }
/*      */ 
/* 1978 */         return new BufferRow(rowPacket, fields, false, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1982 */       readServerStatusForResultSets(rowPacket);
/*      */ 
/* 1984 */       return null;
/*      */     }
/*      */ 
/* 1991 */     if (!rowPacket.isLastDataPacket()) {
/* 1992 */       if ((resultSetConcurrency == 1008) || ((!useBufferRowIfPossible) && (!useBufferRowExplicit)))
/*      */       {
/* 1994 */         return unpackBinaryResultSetRow(fields, rowPacket, resultSetConcurrency);
/*      */       }
/*      */ 
/* 1998 */       if (!canReuseRowPacketForBufferRow) {
/* 1999 */         this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */       }
/*      */ 
/* 2002 */       return new BufferRow(rowPacket, fields, true, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2005 */     rowPacket.setPosition(rowPacket.getPosition() - 1);
/* 2006 */     readServerStatusForResultSets(rowPacket);
/*      */ 
/* 2008 */     return null;
/*      */   }
/*      */ 
/*      */   final ResultSetRow nextRowFast(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacket)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2017 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 2020 */       if (lengthRead < 4) {
/* 2021 */         forceClose();
/* 2022 */         throw new RuntimeException(Messages.getString("MysqlIO.43"));
/*      */       }
/*      */ 
/* 2025 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/* 2030 */       if (packetLength == this.maxThreeBytes) {
/* 2031 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */ 
/* 2034 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacket, this.reusablePacket);
/*      */       }
/*      */ 
/* 2041 */       if (packetLength > this.useBufferRowSizeThreshold) {
/* 2042 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */ 
/* 2045 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, true, true, false, this.reusablePacket);
/*      */       }
/*      */ 
/* 2050 */       int remaining = packetLength;
/*      */ 
/* 2052 */       boolean firstTime = true;
/*      */ 
/* 2054 */       byte[][] rowData = (byte[][])null;
/*      */ 
/* 2056 */       for (int i = 0; i < columnCount; i++)
/*      */       {
/* 2058 */         int sw = this.mysqlInput.read() & 0xFF;
/* 2059 */         remaining--;
/*      */ 
/* 2061 */         if (firstTime) {
/* 2062 */           if (sw == 255)
/*      */           {
/* 2067 */             Buffer errorPacket = new Buffer(packetLength + 4);
/* 2068 */             errorPacket.setPosition(0);
/* 2069 */             errorPacket.writeByte(this.packetHeaderBuf[0]);
/* 2070 */             errorPacket.writeByte(this.packetHeaderBuf[1]);
/* 2071 */             errorPacket.writeByte(this.packetHeaderBuf[2]);
/* 2072 */             errorPacket.writeByte(1);
/* 2073 */             errorPacket.writeByte((byte)sw);
/* 2074 */             readFully(this.mysqlInput, errorPacket.getByteBuffer(), 5, packetLength - 1);
/* 2075 */             errorPacket.setPosition(4);
/* 2076 */             checkErrorPacket(errorPacket);
/*      */           }
/*      */ 
/* 2079 */           if ((sw == 254) && (packetLength < 9)) {
/* 2080 */             if (this.use41Extensions) {
/* 2081 */               this.warningCount = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/*      */ 
/* 2083 */               remaining -= 2;
/*      */ 
/* 2085 */               if (this.warningCount > 0) {
/* 2086 */                 this.hadWarnings = true;
/*      */               }
/*      */ 
/* 2092 */               this.oldServerStatus = this.serverStatus;
/*      */ 
/* 2094 */               this.serverStatus = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/*      */ 
/* 2096 */               checkTransactionState(this.oldServerStatus);
/*      */ 
/* 2098 */               remaining -= 2;
/*      */ 
/* 2100 */               if (remaining > 0) {
/* 2101 */                 skipFully(this.mysqlInput, remaining);
/*      */               }
/*      */             }
/*      */ 
/* 2105 */             return null;
/*      */           }
/*      */ 
/* 2108 */           rowData = new byte[columnCount][];
/*      */ 
/* 2110 */           firstTime = false;
/*      */         }
/*      */ 
/* 2113 */         int len = 0;
/*      */ 
/* 2115 */         switch (sw) {
/*      */         case 251:
/* 2117 */           len = -1;
/* 2118 */           break;
/*      */         case 252:
/* 2121 */           len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8;
/*      */ 
/* 2123 */           remaining -= 2;
/* 2124 */           break;
/*      */         case 253:
/* 2127 */           len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16;
/*      */ 
/* 2131 */           remaining -= 3;
/* 2132 */           break;
/*      */         case 254:
/* 2135 */           len = (int)(this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16 | (this.mysqlInput.read() & 0xFF) << 24 | (this.mysqlInput.read() & 0xFF) << 32 | (this.mysqlInput.read() & 0xFF) << 40 | (this.mysqlInput.read() & 0xFF) << 48 | (this.mysqlInput.read() & 0xFF) << 56);
/*      */ 
/* 2143 */           remaining -= 8;
/* 2144 */           break;
/*      */         default:
/* 2147 */           len = sw;
/*      */         }
/*      */ 
/* 2150 */         if (len == -1) {
/* 2151 */           rowData[i] = null;
/* 2152 */         } else if (len == 0) {
/* 2153 */           rowData[i] = Constants.EMPTY_BYTE_ARRAY;
/*      */         } else {
/* 2155 */           rowData[i] = new byte[len];
/*      */ 
/* 2157 */           int bytesRead = readFully(this.mysqlInput, rowData[i], 0, len);
/*      */ 
/* 2160 */           if (bytesRead != len) {
/* 2161 */             throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException(Messages.getString("MysqlIO.43")), getExceptionInterceptor());
/*      */           }
/*      */ 
/* 2166 */           remaining -= bytesRead;
/*      */         }
/*      */       }
/*      */ 
/* 2170 */       if (remaining > 0) {
/* 2171 */         skipFully(this.mysqlInput, remaining);
/*      */       }
/*      */ 
/* 2174 */       return new ByteArrayRow(rowData, getExceptionInterceptor()); } catch (IOException ioEx) {
/*      */     }
/* 2176 */     throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   final void quit()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/* 2192 */         if (!this.mysqlConnection.isClosed())
/*      */           try {
/* 2194 */             this.mysqlConnection.shutdownInput();
/*      */           }
/*      */           catch (UnsupportedOperationException ex) {
/*      */           }
/*      */       }
/*      */       catch (IOException ioEx) {
/* 2200 */         this.connection.getLog().logWarn("Caught while disconnecting...", ioEx);
/*      */       }
/*      */ 
/* 2203 */       Buffer packet = new Buffer(6);
/* 2204 */       this.packetSequence = -1;
/* 2205 */       this.compressedPacketSequence = -1;
/* 2206 */       packet.writeByte(1);
/* 2207 */       send(packet, packet.getPosition());
/*      */     } finally {
/* 2209 */       forceClose();
/*      */     }
/*      */   }
/*      */ 
/*      */   Buffer getSharedSendPacket()
/*      */   {
/* 2220 */     if (this.sharedSendPacket == null) {
/* 2221 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */ 
/* 2224 */     return this.sharedSendPacket;
/*      */   }
/*      */ 
/*      */   void closeStreamer(RowData streamer) throws SQLException {
/* 2228 */     if (this.streamingData == null) {
/* 2229 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2233 */     if (streamer != this.streamingData) {
/* 2234 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.19") + streamer + Messages.getString("MysqlIO.20") + Messages.getString("MysqlIO.21") + Messages.getString("MysqlIO.22"), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2240 */     this.streamingData = null;
/*      */   }
/*      */ 
/*      */   boolean tackOnMoreStreamingResults(ResultSetImpl addingTo) throws SQLException {
/* 2244 */     if ((this.serverStatus & 0x8) != 0)
/*      */     {
/* 2246 */       boolean moreRowSetsExist = true;
/* 2247 */       ResultSetImpl currentResultSet = addingTo;
/* 2248 */       boolean firstTime = true;
/*      */ 
/* 2250 */       while ((moreRowSetsExist) && (
/* 2251 */         (firstTime) || (!currentResultSet.reallyResult())))
/*      */       {
/* 2255 */         firstTime = false;
/*      */ 
/* 2257 */         Buffer fieldPacket = checkErrorPacket();
/* 2258 */         fieldPacket.setPosition(0);
/*      */ 
/* 2260 */         java.sql.Statement owningStatement = addingTo.getStatement();
/*      */ 
/* 2262 */         int maxRows = owningStatement.getMaxRows();
/*      */ 
/* 2266 */         ResultSetImpl newResultSet = readResultsForQueryOrUpdate((StatementImpl)owningStatement, maxRows, owningStatement.getResultSetType(), owningStatement.getResultSetConcurrency(), true, owningStatement.getConnection().getCatalog(), fieldPacket, addingTo.isBinaryEncoded, -1L, null);
/*      */ 
/* 2274 */         currentResultSet.setNextResultSet(newResultSet);
/*      */ 
/* 2276 */         currentResultSet = newResultSet;
/*      */ 
/* 2278 */         moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */ 
/* 2280 */         if ((!currentResultSet.reallyResult()) && (!moreRowSetsExist))
/*      */         {
/* 2282 */           return false;
/*      */         }
/*      */       }
/*      */ 
/* 2286 */       return true;
/*      */     }
/*      */ 
/* 2289 */     return false;
/*      */   }
/*      */ 
/*      */   ResultSetImpl readAllResults(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 2297 */     resultPacket.setPosition(resultPacket.getPosition() - 1);
/*      */ 
/* 2299 */     ResultSetImpl topLevelResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */ 
/* 2304 */     ResultSetImpl currentResultSet = topLevelResultSet;
/*      */ 
/* 2306 */     boolean checkForMoreResults = (this.clientParam & 0x20000) != 0L;
/*      */ 
/* 2309 */     boolean serverHasMoreResults = (this.serverStatus & 0x8) != 0;
/*      */ 
/* 2315 */     if ((serverHasMoreResults) && (streamResults))
/*      */     {
/* 2320 */       if (topLevelResultSet.getUpdateCount() != -1L) {
/* 2321 */         tackOnMoreStreamingResults(topLevelResultSet);
/*      */       }
/*      */ 
/* 2324 */       reclaimLargeReusablePacket();
/*      */ 
/* 2326 */       return topLevelResultSet;
/*      */     }
/*      */ 
/* 2329 */     boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
/*      */ 
/* 2331 */     while (moreRowSetsExist) {
/* 2332 */       Buffer fieldPacket = checkErrorPacket();
/* 2333 */       fieldPacket.setPosition(0);
/*      */ 
/* 2335 */       ResultSetImpl newResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, fieldPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */ 
/* 2340 */       currentResultSet.setNextResultSet(newResultSet);
/*      */ 
/* 2342 */       currentResultSet = newResultSet;
/*      */ 
/* 2344 */       moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */     }
/*      */ 
/* 2347 */     if (!streamResults) {
/* 2348 */       clearInputStream();
/*      */     }
/*      */ 
/* 2351 */     reclaimLargeReusablePacket();
/*      */ 
/* 2353 */     return topLevelResultSet;
/*      */   }
/*      */ 
/*      */   void resetMaxBuf()
/*      */   {
/* 2360 */     this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */   }
/*      */ 
/*      */   final Buffer sendCommand(int command, String extraData, Buffer queryPacket, boolean skipCheck, String extraDataCharEncoding, int timeoutMillis)
/*      */     throws SQLException
/*      */   {
/* 2386 */     this.commandCount += 1;
/*      */ 
/* 2393 */     this.enablePacketDebug = this.connection.getEnablePacketDebug();
/* 2394 */     this.readPacketSequence = 0;
/*      */ 
/* 2396 */     int oldTimeout = 0;
/*      */ 
/* 2398 */     if (timeoutMillis != 0) {
/*      */       try {
/* 2400 */         oldTimeout = this.mysqlConnection.getSoTimeout();
/* 2401 */         this.mysqlConnection.setSoTimeout(timeoutMillis);
/*      */       } catch (SocketException e) {
/* 2403 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2410 */       checkForOutstandingStreamingData();
/*      */ 
/* 2415 */       this.oldServerStatus = this.serverStatus;
/* 2416 */       this.serverStatus = 0;
/* 2417 */       this.hadWarnings = false;
/* 2418 */       this.warningCount = 0;
/*      */ 
/* 2420 */       this.queryNoIndexUsed = false;
/* 2421 */       this.queryBadIndexUsed = false;
/* 2422 */       this.serverQueryWasSlow = false;
/*      */ 
/* 2428 */       if (this.useCompression) {
/* 2429 */         int bytesLeft = this.mysqlInput.available();
/*      */ 
/* 2431 */         if (bytesLeft > 0) {
/* 2432 */           this.mysqlInput.skip(bytesLeft);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2437 */         clearInputStream();
/*      */ 
/* 2446 */         if (queryPacket == null) {
/* 2447 */           int packLength = 8 + (extraData != null ? extraData.length() : 0) + 2;
/*      */ 
/* 2450 */           if (this.sendPacket == null) {
/* 2451 */             this.sendPacket = new Buffer(packLength);
/*      */           }
/*      */ 
/* 2454 */           this.packetSequence = -1;
/* 2455 */           this.compressedPacketSequence = -1;
/* 2456 */           this.readPacketSequence = 0;
/* 2457 */           this.checkPacketSequence = true;
/* 2458 */           this.sendPacket.clear();
/*      */ 
/* 2460 */           this.sendPacket.writeByte((byte)command);
/*      */ 
/* 2462 */           if ((command == 2) || (command == 5) || (command == 6) || (command == 3) || (command == 22))
/*      */           {
/* 2467 */             if (extraDataCharEncoding == null)
/* 2468 */               this.sendPacket.writeStringNoNull(extraData);
/*      */             else {
/* 2470 */               this.sendPacket.writeStringNoNull(extraData, extraDataCharEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */             }
/*      */ 
/*      */           }
/* 2475 */           else if (command == 12) {
/* 2476 */             id = Long.parseLong(extraData);
/* 2477 */             this.sendPacket.writeLong(id);
/*      */           }
/*      */ 
/* 2480 */           send(this.sendPacket, this.sendPacket.getPosition());
/*      */         } else {
/* 2482 */           this.packetSequence = -1;
/* 2483 */           this.compressedPacketSequence = -1;
/* 2484 */           send(queryPacket, queryPacket.getPosition());
/*      */         }
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 2488 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 2490 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2494 */       Buffer returnPacket = null;
/*      */ 
/* 2496 */       if (!skipCheck) {
/* 2497 */         if ((command == 23) || (command == 26))
/*      */         {
/* 2499 */           this.readPacketSequence = 0;
/* 2500 */           this.packetSequenceReset = true;
/*      */         }
/*      */ 
/* 2503 */         returnPacket = checkErrorPacket(command);
/*      */       }
/*      */ 
/* 2506 */       id = returnPacket;
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*      */       long id;
/* 2508 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     finally {
/* 2511 */       if (timeoutMillis != 0)
/*      */         try {
/* 2513 */           this.mysqlConnection.setSoTimeout(oldTimeout);
/*      */         } catch (SocketException e) {
/* 2515 */           throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean shouldIntercept()
/*      */   {
/* 2526 */     return this.statementInterceptors != null;
/*      */   }
/*      */ 
/*      */   final ResultSetInternalMethods sqlQueryDirect(StatementImpl callingStatement, String query, String characterEncoding, Buffer queryPacket, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws Exception
/*      */   {
/* 2553 */     this.statementExecutionDepth += 1;
/*      */     try
/*      */     {
/*      */       ResultSetInternalMethods localResultSetInternalMethods1;
/* 2556 */       if (this.statementInterceptors != null) {
/* 2557 */         ResultSetInternalMethods interceptedResults = invokeStatementInterceptorsPre(query, callingStatement, false);
/*      */ 
/* 2560 */         if (interceptedResults != null) {
/* 2561 */           localResultSetInternalMethods1 = interceptedResults; jsr 1728;
/*      */         }
/*      */       }
/*      */ 
/* 2565 */       long queryStartTime = 0L;
/* 2566 */       long queryEndTime = 0L;
/*      */ 
/* 2568 */       String statementComment = this.connection.getStatementComment();
/*      */ 
/* 2570 */       if (this.connection.getIncludeThreadNamesAsStatementComment()) {
/* 2571 */         statementComment = (statementComment != null ? statementComment + ", " : "") + "java thread: " + Thread.currentThread().getName();
/*      */       }
/*      */ 
/* 2574 */       if (query != null)
/*      */       {
/* 2578 */         int packLength = 5 + query.length() * 2 + 2;
/*      */ 
/* 2580 */         byte[] commentAsBytes = null;
/*      */ 
/* 2582 */         if (statementComment != null) {
/* 2583 */           commentAsBytes = StringUtils.getBytes(statementComment, null, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */ 
/* 2588 */           packLength += commentAsBytes.length;
/* 2589 */           packLength += 6;
/*      */         }
/*      */ 
/* 2592 */         if (this.sendPacket == null)
/* 2593 */           this.sendPacket = new Buffer(packLength);
/*      */         else {
/* 2595 */           this.sendPacket.clear();
/*      */         }
/*      */ 
/* 2598 */         this.sendPacket.writeByte(3);
/*      */ 
/* 2600 */         if (commentAsBytes != null) {
/* 2601 */           this.sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2602 */           this.sendPacket.writeBytesNoNull(commentAsBytes);
/* 2603 */           this.sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */         }
/*      */ 
/* 2606 */         if (characterEncoding != null) {
/* 2607 */           if (this.platformDbCharsetMatches) {
/* 2608 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/* 2613 */           else if (StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA"))
/* 2614 */             this.sendPacket.writeBytesNoNull(StringUtils.getBytes(query));
/*      */           else {
/* 2616 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2624 */           this.sendPacket.writeStringNoNull(query);
/*      */         }
/*      */ 
/* 2627 */         queryPacket = this.sendPacket;
/*      */       }
/*      */ 
/* 2630 */       byte[] queryBuf = null;
/* 2631 */       int oldPacketPosition = 0;
/*      */ 
/* 2633 */       if (this.needToGrabQueryFromPacket) {
/* 2634 */         queryBuf = queryPacket.getByteBuffer();
/*      */ 
/* 2637 */         oldPacketPosition = queryPacket.getPosition();
/*      */ 
/* 2639 */         queryStartTime = getCurrentTimeNanosOrMillis();
/*      */       }
/*      */ 
/* 2642 */       if (this.autoGenerateTestcaseScript) {
/* 2643 */         String testcaseQuery = null;
/*      */ 
/* 2645 */         if (query != null) {
/* 2646 */           if (statementComment != null)
/* 2647 */             testcaseQuery = "/* " + statementComment + " */ " + query;
/*      */           else
/* 2649 */             testcaseQuery = query;
/*      */         }
/*      */         else {
/* 2652 */           testcaseQuery = StringUtils.toString(queryBuf, 5, oldPacketPosition - 5);
/*      */         }
/*      */ 
/* 2656 */         StringBuffer debugBuf = new StringBuffer(testcaseQuery.length() + 32);
/* 2657 */         this.connection.generateConnectionCommentBlock(debugBuf);
/* 2658 */         debugBuf.append(testcaseQuery);
/* 2659 */         debugBuf.append(';');
/* 2660 */         this.connection.dumpTestcaseQuery(debugBuf.toString());
/*      */       }
/*      */ 
/* 2664 */       Buffer resultPacket = sendCommand(3, null, queryPacket, false, null, 0);
/*      */ 
/* 2667 */       long fetchBeginTime = 0L;
/* 2668 */       long fetchEndTime = 0L;
/*      */ 
/* 2670 */       String profileQueryToLog = null;
/*      */ 
/* 2672 */       boolean queryWasSlow = false;
/*      */ 
/* 2674 */       if ((this.profileSql) || (this.logSlowQueries)) {
/* 2675 */         queryEndTime = getCurrentTimeNanosOrMillis();
/*      */ 
/* 2677 */         boolean shouldExtractQuery = false;
/*      */ 
/* 2679 */         if (this.profileSql) {
/* 2680 */           shouldExtractQuery = true;
/* 2681 */         } else if (this.logSlowQueries) {
/* 2682 */           long queryTime = queryEndTime - queryStartTime;
/*      */ 
/* 2684 */           boolean logSlow = false;
/*      */ 
/* 2686 */           if (!this.useAutoSlowLog) {
/* 2687 */             logSlow = queryTime > this.connection.getSlowQueryThresholdMillis();
/*      */           } else {
/* 2689 */             logSlow = this.connection.isAbonormallyLongQuery(queryTime);
/*      */ 
/* 2691 */             this.connection.reportQueryTime(queryTime);
/*      */           }
/*      */ 
/* 2694 */           if (logSlow) {
/* 2695 */             shouldExtractQuery = true;
/* 2696 */             queryWasSlow = true;
/*      */           }
/*      */         }
/*      */ 
/* 2700 */         if (shouldExtractQuery)
/*      */         {
/* 2702 */           boolean truncated = false;
/*      */ 
/* 2704 */           int extractPosition = oldPacketPosition;
/*      */ 
/* 2706 */           if (oldPacketPosition > this.connection.getMaxQuerySizeToLog()) {
/* 2707 */             extractPosition = this.connection.getMaxQuerySizeToLog() + 5;
/* 2708 */             truncated = true;
/*      */           }
/*      */ 
/* 2711 */           profileQueryToLog = StringUtils.toString(queryBuf, 5, extractPosition - 5);
/*      */ 
/* 2714 */           if (truncated) {
/* 2715 */             profileQueryToLog = profileQueryToLog + Messages.getString("MysqlIO.25");
/*      */           }
/*      */         }
/*      */ 
/* 2719 */         fetchBeginTime = queryEndTime;
/*      */       }
/*      */ 
/* 2722 */       ResultSetInternalMethods rs = readAllResults(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, false, -1L, cachedMetadata);
/*      */ 
/* 2726 */       if ((queryWasSlow) && (!this.serverQueryWasSlow)) {
/* 2727 */         StringBuffer mesgBuf = new StringBuffer(48 + profileQueryToLog.length());
/*      */ 
/* 2730 */         mesgBuf.append(Messages.getString("MysqlIO.SlowQuery", new Object[] { String.valueOf(this.useAutoSlowLog ? " 95% of all queries " : Long.valueOf(this.slowQueryThreshold)), this.queryTimingUnits, Long.valueOf(queryEndTime - queryStartTime) }));
/*      */ 
/* 2735 */         mesgBuf.append(profileQueryToLog);
/*      */ 
/* 2737 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 2739 */         eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), mesgBuf.toString()));
/*      */ 
/* 2746 */         if (this.connection.getExplainSlowQueries()) {
/* 2747 */           if (oldPacketPosition < 1048576) {
/* 2748 */             explainSlowQuery(queryPacket.getBytes(5, oldPacketPosition - 5), profileQueryToLog);
/*      */           }
/*      */           else {
/* 2751 */             this.connection.getLog().logWarn(Messages.getString("MysqlIO.28") + 1048576 + Messages.getString("MysqlIO.29"));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2759 */       if (this.logSlowQueries)
/*      */       {
/* 2761 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 2763 */         if ((this.queryBadIndexUsed) && (this.profileSql)) {
/* 2764 */           eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), Messages.getString("MysqlIO.33") + profileQueryToLog));
/*      */         }
/*      */ 
/* 2777 */         if ((this.queryNoIndexUsed) && (this.profileSql)) {
/* 2778 */           eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), Messages.getString("MysqlIO.35") + profileQueryToLog));
/*      */         }
/*      */ 
/* 2791 */         if ((this.serverQueryWasSlow) && (this.profileSql)) {
/* 2792 */           eventSink.consumeEvent(new ProfilerEvent(6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), Messages.getString("MysqlIO.ServerSlowQuery") + profileQueryToLog));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2806 */       if (this.profileSql) {
/* 2807 */         fetchEndTime = getCurrentTimeNanosOrMillis();
/*      */ 
/* 2809 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */ 
/* 2811 */         eventSink.consumeEvent(new ProfilerEvent(3, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), profileQueryToLog));
/*      */ 
/* 2819 */         eventSink.consumeEvent(new ProfilerEvent(5, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), fetchEndTime - fetchBeginTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), null));
/*      */       }
/*      */ 
/* 2828 */       if (this.hadWarnings) {
/* 2829 */         scanForAndThrowDataTruncation();
/*      */       }
/*      */ 
/* 2832 */       if (this.statementInterceptors != null) {
/* 2833 */         interceptedResults = invokeStatementInterceptorsPost(query, callingStatement, rs, false, null);
/*      */ 
/* 2836 */         if (interceptedResults != null) {
/* 2837 */           rs = interceptedResults;
/*      */         }
/*      */       }
/*      */ 
/* 2841 */       interceptedResults = rs;
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*      */       ResultSetInternalMethods interceptedResults;
/* 2843 */       if (this.statementInterceptors != null) {
/* 2844 */         invokeStatementInterceptorsPost(query, callingStatement, null, false, sqlEx);
/*      */       }
/*      */ 
/* 2848 */       if (callingStatement != null) {
/* 2849 */         synchronized (callingStatement.cancelTimeoutMutex) {
/* 2850 */           if (callingStatement.wasCancelled) {
/* 2851 */             SQLException cause = null;
/*      */ 
/* 2853 */             if (callingStatement.wasCancelledByTimeout)
/* 2854 */               cause = new MySQLTimeoutException();
/*      */             else {
/* 2856 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */ 
/* 2859 */             callingStatement.resetCancelledState();
/*      */ 
/* 2861 */             throw cause;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2866 */       throw sqlEx;
/*      */     } finally {
/* 2868 */       this.statementExecutionDepth -= 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   ResultSetInternalMethods invokeStatementInterceptorsPre(String sql, Statement interceptedStatement, boolean forceExecute) throws SQLException
/*      */   {
/* 2874 */     ResultSetInternalMethods previousResultSet = null;
/*      */ 
/* 2876 */     Iterator interceptors = this.statementInterceptors.iterator();
/*      */ 
/* 2878 */     while (interceptors.hasNext()) {
/* 2879 */       StatementInterceptorV2 interceptor = (StatementInterceptorV2)interceptors.next();
/*      */ 
/* 2881 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2882 */       boolean shouldExecute = ((executeTopLevelOnly) && ((this.statementExecutionDepth == 1) || (forceExecute))) || (!executeTopLevelOnly);
/*      */ 
/* 2885 */       if (shouldExecute) {
/* 2886 */         String sqlToInterceptor = sql;
/*      */ 
/* 2893 */         ResultSetInternalMethods interceptedResultSet = interceptor.preProcess(sqlToInterceptor, interceptedStatement, this.connection);
/*      */ 
/* 2897 */         if (interceptedResultSet != null) {
/* 2898 */           previousResultSet = interceptedResultSet;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2903 */     return previousResultSet;
/*      */   }
/*      */ 
/*      */   ResultSetInternalMethods invokeStatementInterceptorsPost(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, boolean forceExecute, SQLException statementException)
/*      */     throws SQLException
/*      */   {
/* 2909 */     Iterator interceptors = this.statementInterceptors.iterator();
/*      */ 
/* 2911 */     while (interceptors.hasNext()) {
/* 2912 */       StatementInterceptorV2 interceptor = (StatementInterceptorV2)interceptors.next();
/*      */ 
/* 2914 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2915 */       boolean shouldExecute = ((executeTopLevelOnly) && ((this.statementExecutionDepth == 1) || (forceExecute))) || (!executeTopLevelOnly);
/*      */ 
/* 2918 */       if (shouldExecute) {
/* 2919 */         String sqlToInterceptor = sql;
/*      */ 
/* 2921 */         ResultSetInternalMethods interceptedResultSet = interceptor.postProcess(sqlToInterceptor, interceptedStatement, originalResultSet, this.connection, this.warningCount, this.queryNoIndexUsed, this.queryBadIndexUsed, statementException);
/*      */ 
/* 2926 */         if (interceptedResultSet != null) {
/* 2927 */           originalResultSet = interceptedResultSet;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2932 */     return originalResultSet;
/*      */   }
/*      */ 
/*      */   private void calculateSlowQueryThreshold() {
/* 2936 */     this.slowQueryThreshold = this.connection.getSlowQueryThresholdMillis();
/*      */ 
/* 2938 */     if (this.connection.getUseNanosForElapsedTime()) {
/* 2939 */       long nanosThreshold = this.connection.getSlowQueryThresholdNanos();
/*      */ 
/* 2941 */       if (nanosThreshold != 0L)
/* 2942 */         this.slowQueryThreshold = nanosThreshold;
/*      */       else
/* 2944 */         this.slowQueryThreshold *= 1000000L;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected long getCurrentTimeNanosOrMillis()
/*      */   {
/* 2950 */     if (this.useNanosForElapsedTime) {
/* 2951 */       return Util.getCurrentTimeNanosOrMillis();
/*      */     }
/*      */ 
/* 2954 */     return System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   String getHost()
/*      */   {
/* 2963 */     return this.host;
/*      */   }
/*      */ 
/*      */   boolean isVersion(int major, int minor, int subminor)
/*      */   {
/* 2978 */     return (major == getServerMajorVersion()) && (minor == getServerMinorVersion()) && (subminor == getServerSubMinorVersion());
/*      */   }
/*      */ 
/*      */   boolean versionMeetsMinimum(int major, int minor, int subminor)
/*      */   {
/* 2994 */     if (getServerMajorVersion() >= major) {
/* 2995 */       if (getServerMajorVersion() == major) {
/* 2996 */         if (getServerMinorVersion() >= minor) {
/* 2997 */           if (getServerMinorVersion() == minor) {
/* 2998 */             return getServerSubMinorVersion() >= subminor;
/*      */           }
/*      */ 
/* 3002 */           return true;
/*      */         }
/*      */ 
/* 3006 */         return false;
/*      */       }
/*      */ 
/* 3010 */       return true;
/*      */     }
/*      */ 
/* 3013 */     return false;
/*      */   }
/*      */ 
/*      */   private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength)
/*      */   {
/* 3027 */     if (packetLength < 1024) {
/* 3028 */       return packetToDump.dump(packetLength);
/*      */     }
/*      */ 
/* 3031 */     StringBuffer packetDumpBuf = new StringBuffer(4096);
/* 3032 */     packetDumpBuf.append(packetToDump.dump(1024));
/* 3033 */     packetDumpBuf.append(Messages.getString("MysqlIO.36"));
/* 3034 */     packetDumpBuf.append(1024);
/* 3035 */     packetDumpBuf.append(Messages.getString("MysqlIO.37"));
/*      */ 
/* 3037 */     return packetDumpBuf.toString();
/*      */   }
/*      */ 
/*      */   private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException
/*      */   {
/* 3042 */     if (len < 0) {
/* 3043 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */ 
/* 3046 */     int n = 0;
/*      */ 
/* 3048 */     while (n < len) {
/* 3049 */       int count = in.read(b, off + n, len - n);
/*      */ 
/* 3051 */       if (count < 0) {
/* 3052 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { Integer.valueOf(len), Integer.valueOf(n) }));
/*      */       }
/*      */ 
/* 3056 */       n += count;
/*      */     }
/*      */ 
/* 3059 */     return n;
/*      */   }
/*      */ 
/*      */   private final long skipFully(InputStream in, long len) throws IOException {
/* 3063 */     if (len < 0L) {
/* 3064 */       throw new IOException("Negative skip length not allowed");
/*      */     }
/*      */ 
/* 3067 */     long n = 0L;
/*      */ 
/* 3069 */     while (n < len) {
/* 3070 */       long count = in.skip(len - n);
/*      */ 
/* 3072 */       if (count < 0L) {
/* 3073 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { Long.valueOf(len), Long.valueOf(n) }));
/*      */       }
/*      */ 
/* 3077 */       n += count;
/*      */     }
/*      */ 
/* 3080 */     return n;
/*      */   }
/*      */ 
/*      */   protected final ResultSetImpl readResultsForQueryOrUpdate(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 3108 */     long columnCount = resultPacket.readFieldLength();
/*      */ 
/* 3110 */     if (columnCount == 0L)
/* 3111 */       return buildResultSetWithUpdates(callingStatement, resultPacket);
/* 3112 */     if (columnCount == -1L) {
/* 3113 */       String charEncoding = null;
/*      */ 
/* 3115 */       if (this.connection.getUseUnicode()) {
/* 3116 */         charEncoding = this.connection.getEncoding();
/*      */       }
/*      */ 
/* 3119 */       String fileName = null;
/*      */ 
/* 3121 */       if (this.platformDbCharsetMatches) {
/* 3122 */         fileName = charEncoding != null ? resultPacket.readString(charEncoding, getExceptionInterceptor()) : resultPacket.readString();
/*      */       }
/*      */       else
/*      */       {
/* 3126 */         fileName = resultPacket.readString();
/*      */       }
/*      */ 
/* 3129 */       return sendFileToServer(callingStatement, fileName);
/*      */     }
/* 3131 */     ResultSetImpl results = getResultSet(callingStatement, columnCount, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, isBinaryEncoded, metadataFromCache);
/*      */ 
/* 3136 */     return results;
/*      */   }
/*      */ 
/*      */   private int alignPacketSize(int a, int l)
/*      */   {
/* 3141 */     return a + l - 1 & (l - 1 ^ 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   private ResultSetImpl buildResultSetWithRows(StatementImpl callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded)
/*      */     throws SQLException
/*      */   {
/* 3149 */     ResultSetImpl rs = null;
/*      */ 
/* 3151 */     switch (resultSetConcurrency) {
/*      */     case 1007:
/* 3153 */       rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */ 
/* 3156 */       if (!isBinaryEncoded) break;
/* 3157 */       rs.setBinaryEncoded(); break;
/*      */     case 1008:
/* 3163 */       rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, true);
/*      */ 
/* 3166 */       break;
/*      */     default:
/* 3169 */       return ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */     }
/*      */ 
/* 3173 */     rs.setResultSetType(resultSetType);
/* 3174 */     rs.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 3176 */     return rs;
/*      */   }
/*      */ 
/*      */   private ResultSetImpl buildResultSetWithUpdates(StatementImpl callingStatement, Buffer resultPacket)
/*      */     throws SQLException
/*      */   {
/* 3182 */     long updateCount = -1L;
/* 3183 */     long updateID = -1L;
/* 3184 */     String info = null;
/*      */     try
/*      */     {
/* 3187 */       if (this.useNewUpdateCounts) {
/* 3188 */         updateCount = resultPacket.newReadLength();
/* 3189 */         updateID = resultPacket.newReadLength();
/*      */       } else {
/* 3191 */         updateCount = resultPacket.readLength();
/* 3192 */         updateID = resultPacket.readLength();
/*      */       }
/*      */ 
/* 3195 */       if (this.use41Extensions)
/*      */       {
/* 3197 */         this.serverStatus = resultPacket.readInt();
/*      */ 
/* 3199 */         checkTransactionState(this.oldServerStatus);
/*      */ 
/* 3201 */         this.warningCount = resultPacket.readInt();
/*      */ 
/* 3203 */         if (this.warningCount > 0) {
/* 3204 */           this.hadWarnings = true;
/*      */         }
/*      */ 
/* 3207 */         resultPacket.readByte();
/*      */ 
/* 3209 */         setServerSlowQueryFlags();
/*      */       }
/*      */ 
/* 3212 */       if (this.connection.isReadInfoMsgEnabled())
/* 3213 */         info = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */     }
/*      */     catch (Exception ex) {
/* 3216 */       SQLException sqlEx = SQLError.createSQLException(SQLError.get("S1000"), "S1000", -1, getExceptionInterceptor());
/*      */ 
/* 3218 */       sqlEx.initCause(ex);
/*      */ 
/* 3220 */       throw sqlEx;
/*      */     }
/*      */ 
/* 3223 */     ResultSetInternalMethods updateRs = ResultSetImpl.getInstance(updateCount, updateID, this.connection, callingStatement);
/*      */ 
/* 3226 */     if (info != null) {
/* 3227 */       ((ResultSetImpl)updateRs).setServerInfo(info);
/*      */     }
/*      */ 
/* 3230 */     return (ResultSetImpl)updateRs;
/*      */   }
/*      */ 
/*      */   private void setServerSlowQueryFlags() {
/* 3234 */     this.queryBadIndexUsed = ((this.serverStatus & 0x10) != 0);
/*      */ 
/* 3236 */     this.queryNoIndexUsed = ((this.serverStatus & 0x20) != 0);
/*      */ 
/* 3238 */     this.serverQueryWasSlow = ((this.serverStatus & 0x800) != 0);
/*      */   }
/*      */ 
/*      */   private void checkForOutstandingStreamingData() throws SQLException
/*      */   {
/* 3243 */     if (this.streamingData != null) {
/* 3244 */       boolean shouldClobber = this.connection.getClobberStreamingResults();
/*      */ 
/* 3246 */       if (!shouldClobber) {
/* 3247 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3255 */       this.streamingData.getOwner().realClose(false);
/*      */ 
/* 3258 */       clearInputStream();
/*      */     }
/*      */   }
/*      */ 
/*      */   private Buffer compressPacket(Buffer packet, int offset, int packetLen)
/*      */     throws SQLException
/*      */   {
/* 3273 */     int compressedLength = packetLen;
/* 3274 */     int uncompressedLength = 0;
/* 3275 */     byte[] compressedBytes = null;
/* 3276 */     int offsetWrite = offset;
/*      */ 
/* 3278 */     if (packetLen < 50) {
/* 3279 */       compressedBytes = packet.getByteBuffer();
/*      */     }
/*      */     else {
/* 3282 */       byte[] bytesToCompress = packet.getByteBuffer();
/* 3283 */       compressedBytes = new byte[bytesToCompress.length * 2];
/*      */ 
/* 3285 */       this.deflater.reset();
/* 3286 */       this.deflater.setInput(bytesToCompress, offset, packetLen);
/* 3287 */       this.deflater.finish();
/*      */ 
/* 3289 */       compressedLength = this.deflater.deflate(compressedBytes);
/*      */ 
/* 3291 */       if (compressedLength > packetLen)
/*      */       {
/* 3293 */         compressedBytes = packet.getByteBuffer();
/* 3294 */         compressedLength = packetLen;
/*      */       } else {
/* 3296 */         uncompressedLength = packetLen;
/* 3297 */         offsetWrite = 0;
/*      */       }
/*      */     }
/*      */ 
/* 3301 */     Buffer compressedPacket = new Buffer(7 + compressedLength);
/*      */ 
/* 3303 */     compressedPacket.setPosition(0);
/* 3304 */     compressedPacket.writeLongInt(compressedLength);
/* 3305 */     compressedPacket.writeByte(this.compressedPacketSequence);
/* 3306 */     compressedPacket.writeLongInt(uncompressedLength);
/* 3307 */     compressedPacket.writeBytesNoNull(compressedBytes, offsetWrite, compressedLength);
/*      */ 
/* 3309 */     return compressedPacket;
/*      */   }
/*      */ 
/*      */   private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException
/*      */   {
/* 3314 */     if (this.use41Extensions) {
/* 3315 */       rowPacket.readByte();
/*      */ 
/* 3317 */       this.warningCount = rowPacket.readInt();
/*      */ 
/* 3319 */       if (this.warningCount > 0) {
/* 3320 */         this.hadWarnings = true;
/*      */       }
/*      */ 
/* 3323 */       this.oldServerStatus = this.serverStatus;
/* 3324 */       this.serverStatus = rowPacket.readInt();
/* 3325 */       checkTransactionState(this.oldServerStatus);
/*      */ 
/* 3327 */       setServerSlowQueryFlags();
/*      */     }
/*      */   }
/*      */   private SocketFactory createSocketFactory() throws SQLException {
/*      */     SQLException sqlEx;
/*      */     try {
/* 3333 */       if (this.socketFactoryClassName == null) {
/* 3334 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.75"), "08001", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3338 */       return (SocketFactory)(SocketFactory)Class.forName(this.socketFactoryClassName).newInstance();
/*      */     }
/*      */     catch (Exception ex) {
/* 3341 */       sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.76") + this.socketFactoryClassName + Messages.getString("MysqlIO.77"), "08001", getExceptionInterceptor());
/*      */ 
/* 3346 */       sqlEx.initCause(ex);
/*      */     }
/* 3348 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet)
/*      */     throws SQLException
/*      */   {
/* 3355 */     if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
/* 3356 */       this.packetDebugRingBuffer.removeFirst();
/*      */     }
/*      */ 
/* 3359 */     StringBuffer packetDump = null;
/*      */ 
/* 3361 */     if (!isPacketBeingSent) {
/* 3362 */       int bytesToDump = Math.min(1024, packet.getBufLength());
/*      */ 
/* 3365 */       Buffer packetToDump = new Buffer(4 + bytesToDump);
/*      */ 
/* 3367 */       packetToDump.setPosition(0);
/* 3368 */       packetToDump.writeBytesNoNull(header);
/* 3369 */       packetToDump.writeBytesNoNull(packet.getBytes(0, bytesToDump));
/*      */ 
/* 3371 */       String packetPayload = packetToDump.dump(bytesToDump);
/*      */ 
/* 3373 */       packetDump = new StringBuffer(96 + packetPayload.length());
/*      */ 
/* 3375 */       packetDump.append("Server ");
/*      */ 
/* 3377 */       if (isPacketReused)
/* 3378 */         packetDump.append("(re-used)");
/*      */       else {
/* 3380 */         packetDump.append("(new)");
/*      */       }
/*      */ 
/* 3383 */       packetDump.append(" ");
/* 3384 */       packetDump.append(packet.toSuperString());
/* 3385 */       packetDump.append(" --------------------> Client\n");
/* 3386 */       packetDump.append("\nPacket payload:\n\n");
/* 3387 */       packetDump.append(packetPayload);
/*      */ 
/* 3389 */       if (bytesToDump == 1024) {
/* 3390 */         packetDump.append("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 3395 */       int bytesToDump = Math.min(1024, sendLength);
/*      */ 
/* 3397 */       String packetPayload = packet.dump(bytesToDump);
/*      */ 
/* 3399 */       packetDump = new StringBuffer(68 + packetPayload.length());
/*      */ 
/* 3401 */       packetDump.append("Client ");
/* 3402 */       packetDump.append(packet.toSuperString());
/* 3403 */       packetDump.append("--------------------> Server\n");
/* 3404 */       packetDump.append("\nPacket payload:\n\n");
/* 3405 */       packetDump.append(packetPayload);
/*      */ 
/* 3407 */       if (bytesToDump == 1024) {
/* 3408 */         packetDump.append("\nNote: Packet of " + sendLength + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3414 */     this.packetDebugRingBuffer.addLast(packetDump);
/*      */   }
/*      */ 
/*      */   private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields)
/*      */     throws SQLException
/*      */   {
/* 3421 */     ArrayList rows = new ArrayList();
/*      */ 
/* 3423 */     boolean useBufferRowExplicit = useBufferRowExplicit(fields);
/*      */ 
/* 3426 */     ResultSetRow row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */ 
/* 3429 */     int rowCount = 0;
/*      */ 
/* 3431 */     if (row != null) {
/* 3432 */       rows.add(row);
/* 3433 */       rowCount = 1;
/*      */     }
/*      */ 
/* 3436 */     while (row != null) {
/* 3437 */       row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */ 
/* 3440 */       if ((row == null) || (
/* 3441 */         (maxRows != -1) && (rowCount >= maxRows))) continue;
/* 3442 */       rows.add(row);
/* 3443 */       rowCount++;
/*      */     }
/*      */ 
/* 3448 */     RowData rowData = new RowDataStatic(rows);
/*      */ 
/* 3450 */     return rowData;
/*      */   }
/*      */ 
/*      */   public static boolean useBufferRowExplicit(Field[] fields) {
/* 3454 */     if (fields == null) {
/* 3455 */       return false;
/*      */     }
/*      */ 
/* 3458 */     for (int i = 0; i < fields.length; i++) {
/* 3459 */       switch (fields[i].getSQLType()) {
/*      */       case -4:
/*      */       case -1:
/*      */       case 2004:
/*      */       case 2005:
/* 3464 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 3468 */     return false;
/*      */   }
/*      */ 
/*      */   private void reclaimLargeReusablePacket()
/*      */   {
/* 3475 */     if ((this.reusablePacket != null) && (this.reusablePacket.getCapacity() > 1048576))
/*      */     {
/* 3477 */       this.reusablePacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse)
/*      */     throws SQLException
/*      */   {
/* 3492 */     return reuseAndReadPacket(reuse, -1);
/*      */   }
/*      */ 
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse, int existingPacketLength) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3499 */       reuse.setWasMultiPacket(false);
/* 3500 */       int packetLength = 0;
/*      */ 
/* 3502 */       if (existingPacketLength == -1) {
/* 3503 */         int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 3506 */         if (lengthRead < 4) {
/* 3507 */           forceClose();
/* 3508 */           throw new IOException(Messages.getString("MysqlIO.43"));
/*      */         }
/*      */ 
/* 3511 */         packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       }
/*      */       else
/*      */       {
/* 3515 */         packetLength = existingPacketLength;
/*      */       }
/*      */ 
/* 3518 */       if (this.traceProtocol) {
/* 3519 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 3521 */         traceMessageBuf.append(Messages.getString("MysqlIO.44"));
/* 3522 */         traceMessageBuf.append(packetLength);
/* 3523 */         traceMessageBuf.append(Messages.getString("MysqlIO.45"));
/* 3524 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */ 
/* 3527 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */ 
/* 3530 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/* 3532 */       if (!this.packetSequenceReset) {
/* 3533 */         if ((this.enablePacketDebug) && (this.checkPacketSequence))
/* 3534 */           checkPacketSequencing(multiPacketSeq);
/*      */       }
/*      */       else {
/* 3537 */         this.packetSequenceReset = false;
/*      */       }
/*      */ 
/* 3540 */       this.readPacketSequence = multiPacketSeq;
/*      */ 
/* 3543 */       reuse.setPosition(0);
/*      */ 
/* 3551 */       if (reuse.getByteBuffer().length <= packetLength) {
/* 3552 */         reuse.setByteBuffer(new byte[packetLength + 1]);
/*      */       }
/*      */ 
/* 3556 */       reuse.setBufLength(packetLength);
/*      */ 
/* 3559 */       int numBytesRead = readFully(this.mysqlInput, reuse.getByteBuffer(), 0, packetLength);
/*      */ 
/* 3562 */       if (numBytesRead != packetLength) {
/* 3563 */         throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
/*      */       }
/*      */ 
/* 3567 */       if (this.traceProtocol) {
/* 3568 */         StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 3570 */         traceMessageBuf.append(Messages.getString("MysqlIO.46"));
/* 3571 */         traceMessageBuf.append(getPacketDumpToLog(reuse, packetLength));
/*      */ 
/* 3574 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */ 
/* 3577 */       if (this.enablePacketDebug) {
/* 3578 */         enqueuePacketForDebugging(false, true, 0, this.packetHeaderBuf, reuse);
/*      */       }
/*      */ 
/* 3582 */       boolean isMultiPacket = false;
/*      */ 
/* 3584 */       if (packetLength == this.maxThreeBytes) {
/* 3585 */         reuse.setPosition(this.maxThreeBytes);
/*      */ 
/* 3588 */         isMultiPacket = true;
/*      */ 
/* 3590 */         packetLength = readRemainingMultiPackets(reuse, multiPacketSeq);
/*      */       }
/*      */ 
/* 3593 */       if (!isMultiPacket) {
/* 3594 */         reuse.getByteBuffer()[packetLength] = 0;
/*      */       }
/*      */ 
/* 3597 */       if (this.connection.getMaintainTimeStats()) {
/* 3598 */         this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 3601 */       return reuse;
/*      */     } catch (IOException ioEx) {
/* 3603 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     catch (OutOfMemoryError oom)
/*      */     {
/*      */       try {
/* 3608 */         clearInputStream();
/*      */       } catch (Exception ex) {
/*      */       }
/*      */       try {
/* 3612 */         this.connection.realClose(false, false, true, oom); } catch (Exception ex) {
/*      */       }
/*      */     }
/* 3615 */     throw oom;
/*      */   }
/*      */ 
/*      */   private int readRemainingMultiPackets(Buffer reuse, byte multiPacketSeq)
/*      */     throws IOException, SQLException
/*      */   {
/* 3624 */     int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 3627 */     if (lengthRead < 4) {
/* 3628 */       forceClose();
/* 3629 */       throw new IOException(Messages.getString("MysqlIO.47"));
/*      */     }
/*      */ 
/* 3632 */     int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */ 
/* 3636 */     Buffer multiPacket = new Buffer(packetLength);
/* 3637 */     boolean firstMultiPkt = true;
/*      */     while (true)
/*      */     {
/* 3640 */       if (!firstMultiPkt) {
/* 3641 */         lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */ 
/* 3644 */         if (lengthRead < 4) {
/* 3645 */           forceClose();
/* 3646 */           throw new IOException(Messages.getString("MysqlIO.48"));
/*      */         }
/*      */ 
/* 3650 */         packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       }
/*      */       else
/*      */       {
/* 3654 */         firstMultiPkt = false;
/*      */       }
/*      */ 
/* 3657 */       if ((!this.useNewLargePackets) && (packetLength == 1)) {
/* 3658 */         clearInputStream();
/*      */ 
/* 3660 */         break;
/* 3661 */       }if (packetLength < this.maxThreeBytes) {
/* 3662 */         byte newPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/* 3664 */         if (newPacketSeq != multiPacketSeq + 1) {
/* 3665 */           throw new IOException(Messages.getString("MysqlIO.49"));
/*      */         }
/*      */ 
/* 3669 */         multiPacketSeq = newPacketSeq;
/*      */ 
/* 3672 */         multiPacket.setPosition(0);
/*      */ 
/* 3675 */         multiPacket.setBufLength(packetLength);
/*      */ 
/* 3678 */         byte[] byteBuf = multiPacket.getByteBuffer();
/* 3679 */         int lengthToWrite = packetLength;
/*      */ 
/* 3681 */         int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
/*      */ 
/* 3684 */         if (bytesRead != lengthToWrite) {
/* 3685 */           throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.50") + lengthToWrite + Messages.getString("MysqlIO.51") + bytesRead + ".", getExceptionInterceptor()), getExceptionInterceptor());
/*      */         }
/*      */ 
/* 3695 */         reuse.writeBytesNoNull(byteBuf, 0, lengthToWrite);
/*      */ 
/* 3697 */         break;
/*      */       }
/*      */ 
/* 3700 */       byte newPacketSeq = this.packetHeaderBuf[3];
/*      */ 
/* 3702 */       if (newPacketSeq != multiPacketSeq + 1) {
/* 3703 */         throw new IOException(Messages.getString("MysqlIO.53"));
/*      */       }
/*      */ 
/* 3707 */       multiPacketSeq = newPacketSeq;
/*      */ 
/* 3710 */       multiPacket.setPosition(0);
/*      */ 
/* 3713 */       multiPacket.setBufLength(packetLength);
/*      */ 
/* 3716 */       byte[] byteBuf = multiPacket.getByteBuffer();
/* 3717 */       int lengthToWrite = packetLength;
/*      */ 
/* 3719 */       int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
/*      */ 
/* 3722 */       if (bytesRead != lengthToWrite) {
/* 3723 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.54") + lengthToWrite + Messages.getString("MysqlIO.55") + bytesRead + ".", getExceptionInterceptor()), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3732 */       reuse.writeBytesNoNull(byteBuf, 0, lengthToWrite);
/*      */     }
/*      */ 
/* 3735 */     reuse.setPosition(0);
/* 3736 */     reuse.setWasMultiPacket(true);
/* 3737 */     return packetLength;
/*      */   }
/*      */ 
/*      */   private void checkPacketSequencing(byte multiPacketSeq)
/*      */     throws SQLException
/*      */   {
/* 3746 */     if ((multiPacketSeq == -128) && (this.readPacketSequence != 127)) {
/* 3747 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3753 */     if ((this.readPacketSequence == -1) && (multiPacketSeq != 0)) {
/* 3754 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3760 */     if ((multiPacketSeq != -128) && (this.readPacketSequence != -1) && (multiPacketSeq != this.readPacketSequence + 1))
/*      */     {
/* 3762 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   void enableMultiQueries()
/*      */     throws SQLException
/*      */   {
/* 3771 */     Buffer buf = getSharedSendPacket();
/*      */ 
/* 3773 */     buf.clear();
/* 3774 */     buf.writeByte(27);
/* 3775 */     buf.writeInt(0);
/* 3776 */     sendCommand(27, null, buf, false, null, 0);
/*      */   }
/*      */ 
/*      */   void disableMultiQueries() throws SQLException {
/* 3780 */     Buffer buf = getSharedSendPacket();
/*      */ 
/* 3782 */     buf.clear();
/* 3783 */     buf.writeByte(27);
/* 3784 */     buf.writeInt(1);
/* 3785 */     sendCommand(27, null, buf, false, null, 0);
/*      */   }
/*      */ 
/*      */   private final void send(Buffer packet, int packetLen)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3797 */       if ((this.maxAllowedPacket > 0) && (packetLen > this.maxAllowedPacket)) {
/* 3798 */         throw new PacketTooBigException(packetLen, this.maxAllowedPacket);
/*      */       }
/*      */ 
/* 3801 */       if ((this.serverMajorVersion >= 4) && ((packetLen - 4 >= this.maxThreeBytes) || ((this.useCompression) && (packetLen - 4 >= this.maxThreeBytes - 3))))
/*      */       {
/* 3805 */         sendSplitPackets(packet, packetLen);
/*      */       }
/*      */       else {
/* 3808 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3810 */         Buffer packetToSend = packet;
/* 3811 */         packetToSend.setPosition(0);
/* 3812 */         packetToSend.writeLongInt(packetLen - 4);
/* 3813 */         packetToSend.writeByte(this.packetSequence);
/*      */ 
/* 3815 */         if (this.useCompression) {
/* 3816 */           this.compressedPacketSequence = (byte)(this.compressedPacketSequence + 1);
/* 3817 */           int originalPacketLen = packetLen;
/*      */ 
/* 3819 */           packetToSend = compressPacket(packetToSend, 0, packetLen);
/* 3820 */           packetLen = packetToSend.getPosition();
/*      */ 
/* 3822 */           if (this.traceProtocol) {
/* 3823 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 3825 */             traceMessageBuf.append(Messages.getString("MysqlIO.57"));
/* 3826 */             traceMessageBuf.append(getPacketDumpToLog(packetToSend, packetLen));
/*      */ 
/* 3828 */             traceMessageBuf.append(Messages.getString("MysqlIO.58"));
/* 3829 */             traceMessageBuf.append(getPacketDumpToLog(packet, originalPacketLen));
/*      */ 
/* 3832 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           }
/*      */ 
/*      */         }
/* 3836 */         else if (this.traceProtocol) {
/* 3837 */           StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 3839 */           traceMessageBuf.append(Messages.getString("MysqlIO.59"));
/* 3840 */           traceMessageBuf.append("host: '");
/* 3841 */           traceMessageBuf.append(this.host);
/* 3842 */           traceMessageBuf.append("' threadId: '");
/* 3843 */           traceMessageBuf.append(this.threadId);
/* 3844 */           traceMessageBuf.append("'\n");
/* 3845 */           traceMessageBuf.append(packetToSend.dump(packetLen));
/*      */ 
/* 3847 */           this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */         }
/*      */ 
/* 3851 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/* 3852 */         this.mysqlOutput.flush();
/*      */       }
/*      */ 
/* 3855 */       if (this.enablePacketDebug) {
/* 3856 */         enqueuePacketForDebugging(true, false, packetLen + 5, this.packetHeaderBuf, packet);
/*      */       }
/*      */ 
/* 3863 */       if (packet == this.sharedSendPacket) {
/* 3864 */         reclaimLargeSharedSendPacket();
/*      */       }
/*      */ 
/* 3867 */       if (this.connection.getMaintainTimeStats())
/* 3868 */         this.lastPacketSentTimeMs = System.currentTimeMillis();
/*      */     }
/*      */     catch (IOException ioEx) {
/* 3871 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private final ResultSetImpl sendFileToServer(StatementImpl callingStatement, String fileName)
/*      */     throws SQLException
/*      */   {
/* 3889 */     if (this.useCompression) {
/* 3890 */       this.compressedPacketSequence = (byte)(this.compressedPacketSequence + 1);
/*      */     }
/*      */ 
/* 3893 */     Buffer filePacket = this.loadFileBufRef == null ? null : (Buffer)this.loadFileBufRef.get();
/*      */ 
/* 3896 */     int bigPacketLength = Math.min(this.connection.getMaxAllowedPacket() - 12, alignPacketSize(this.connection.getMaxAllowedPacket() - 16, 4096) - 12);
/*      */ 
/* 3901 */     int oneMeg = 1048576;
/*      */ 
/* 3903 */     int smallerPacketSizeAligned = Math.min(oneMeg - 12, alignPacketSize(oneMeg - 16, 4096) - 12);
/*      */ 
/* 3906 */     int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
/*      */ 
/* 3908 */     if (filePacket == null) {
/*      */       try {
/* 3910 */         filePacket = new Buffer(packetLength + 4);
/* 3911 */         this.loadFileBufRef = new SoftReference(filePacket);
/*      */       } catch (OutOfMemoryError oom) {
/* 3913 */         throw SQLError.createSQLException("Could not allocate packet of " + packetLength + " bytes required for LOAD DATA LOCAL INFILE operation." + " Try increasing max heap allocation for JVM or decreasing server variable " + "'max_allowed_packet'", "S1001", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3921 */     filePacket.clear();
/* 3922 */     send(filePacket, 0);
/*      */ 
/* 3924 */     byte[] fileBuf = new byte[packetLength];
/*      */ 
/* 3926 */     BufferedInputStream fileIn = null;
/*      */     try
/*      */     {
/* 3929 */       if (!this.connection.getAllowLoadLocalInfile()) {
/* 3930 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.LoadDataLocalNotAllowed"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 3935 */       InputStream hookedStream = null;
/*      */ 
/* 3937 */       if (callingStatement != null) {
/* 3938 */         hookedStream = callingStatement.getLocalInfileInputStream();
/*      */       }
/*      */ 
/* 3941 */       if (hookedStream != null)
/* 3942 */         fileIn = new BufferedInputStream(hookedStream);
/* 3943 */       else if (!this.connection.getAllowUrlInLocalInfile()) {
/* 3944 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/* 3947 */       else if (fileName.indexOf(':') != -1) {
/*      */         try {
/* 3949 */           URL urlFromFileName = new URL(fileName);
/* 3950 */           fileIn = new BufferedInputStream(urlFromFileName.openStream());
/*      */         }
/*      */         catch (MalformedURLException badUrlEx) {
/* 3953 */           fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */         }
/*      */       }
/*      */       else {
/* 3957 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/*      */ 
/* 3962 */       int bytesRead = 0;
/*      */ 
/* 3964 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 3965 */         filePacket.clear();
/* 3966 */         filePacket.writeBytesNoNull(fileBuf, 0, bytesRead);
/* 3967 */         send(filePacket, filePacket.getPosition());
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3970 */       StringBuffer messageBuf = new StringBuffer(Messages.getString("MysqlIO.60"));
/*      */ 
/* 3973 */       if (!this.connection.getParanoid()) {
/* 3974 */         messageBuf.append("'");
/*      */ 
/* 3976 */         if (fileName != null) {
/* 3977 */           messageBuf.append(fileName);
/*      */         }
/*      */ 
/* 3980 */         messageBuf.append("'");
/*      */       }
/*      */ 
/* 3983 */       messageBuf.append(Messages.getString("MysqlIO.63"));
/*      */ 
/* 3985 */       if (!this.connection.getParanoid()) {
/* 3986 */         messageBuf.append(Messages.getString("MysqlIO.64"));
/* 3987 */         messageBuf.append(Util.stackTraceToString(ioEx));
/*      */       }
/*      */ 
/* 3990 */       throw SQLError.createSQLException(messageBuf.toString(), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     finally {
/* 3993 */       if (fileIn != null) {
/*      */         try {
/* 3995 */           fileIn.close();
/*      */         } catch (Exception ex) {
/* 3997 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.65"), "S1000", getExceptionInterceptor());
/*      */ 
/* 3999 */           sqlEx.initCause(ex);
/*      */ 
/* 4001 */           throw sqlEx;
/*      */         }
/*      */ 
/* 4004 */         fileIn = null;
/*      */       }
/*      */       else {
/* 4007 */         filePacket.clear();
/* 4008 */         send(filePacket, filePacket.getPosition());
/* 4009 */         checkErrorPacket();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4014 */     filePacket.clear();
/* 4015 */     send(filePacket, filePacket.getPosition());
/*      */ 
/* 4017 */     Buffer resultPacket = checkErrorPacket();
/*      */ 
/* 4019 */     return buildResultSetWithUpdates(callingStatement, resultPacket);
/*      */   }
/*      */ 
/*      */   private Buffer checkErrorPacket(int command)
/*      */     throws SQLException
/*      */   {
/* 4035 */     Buffer resultPacket = null;
/* 4036 */     this.serverStatus = 0;
/*      */     try
/*      */     {
/* 4043 */       resultPacket = reuseAndReadPacket(this.reusablePacket);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 4046 */       throw sqlEx;
/*      */     } catch (Exception fallThru) {
/* 4048 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, fallThru, getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4052 */     checkErrorPacket(resultPacket);
/*      */ 
/* 4054 */     return resultPacket;
/*      */   }
/*      */ 
/*      */   private void checkErrorPacket(Buffer resultPacket) throws SQLException
/*      */   {
/* 4059 */     int statusCode = resultPacket.readByte();
/*      */ 
/* 4062 */     if (statusCode == -1)
/*      */     {
/* 4064 */       int errno = 2000;
/*      */ 
/* 4066 */       if (this.protocolVersion > 9) {
/* 4067 */         errno = resultPacket.readInt();
/*      */ 
/* 4069 */         String xOpen = null;
/*      */ 
/* 4071 */         String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */ 
/* 4074 */         if (serverErrorMessage.charAt(0) == '#')
/*      */         {
/* 4077 */           if (serverErrorMessage.length() > 6) {
/* 4078 */             xOpen = serverErrorMessage.substring(1, 6);
/* 4079 */             serverErrorMessage = serverErrorMessage.substring(6);
/*      */ 
/* 4081 */             if (xOpen.equals("HY000"))
/* 4082 */               xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */           else
/*      */           {
/* 4086 */             xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */         }
/*      */         else {
/* 4090 */           xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */         }
/*      */ 
/* 4094 */         clearInputStream();
/*      */ 
/* 4096 */         StringBuffer errorBuf = new StringBuffer();
/*      */ 
/* 4098 */         String xOpenErrorMessage = SQLError.get(xOpen);
/*      */ 
/* 4100 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 4101 */           (xOpenErrorMessage != null)) {
/* 4102 */           errorBuf.append(xOpenErrorMessage);
/* 4103 */           errorBuf.append(Messages.getString("MysqlIO.68"));
/*      */         }
/*      */ 
/* 4107 */         errorBuf.append(serverErrorMessage);
/*      */ 
/* 4109 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 4110 */           (xOpenErrorMessage != null)) {
/* 4111 */           errorBuf.append("\"");
/*      */         }
/*      */ 
/* 4115 */         appendDeadlockStatusInformation(xOpen, errorBuf);
/*      */ 
/* 4117 */         if ((xOpen != null) && (xOpen.startsWith("22"))) {
/* 4118 */           throw new MysqlDataTruncation(errorBuf.toString(), 0, true, false, 0, 0, errno);
/*      */         }
/* 4120 */         throw SQLError.createSQLException(errorBuf.toString(), xOpen, errno, false, getExceptionInterceptor(), this.connection);
/*      */       }
/*      */ 
/* 4124 */       String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */ 
/* 4126 */       clearInputStream();
/*      */ 
/* 4128 */       if (serverErrorMessage.indexOf(Messages.getString("MysqlIO.70")) != -1) {
/* 4129 */         throw SQLError.createSQLException(SQLError.get("S0022") + ", " + serverErrorMessage, "S0022", -1, false, getExceptionInterceptor(), this.connection);
/*      */       }
/*      */ 
/* 4136 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.72"));
/*      */ 
/* 4138 */       errorBuf.append(serverErrorMessage);
/* 4139 */       errorBuf.append("\"");
/*      */ 
/* 4141 */       throw SQLError.createSQLException(SQLError.get("S1000") + ", " + errorBuf.toString(), "S1000", -1, false, getExceptionInterceptor(), this.connection);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void appendDeadlockStatusInformation(String xOpen, StringBuffer errorBuf)
/*      */     throws SQLException
/*      */   {
/* 4149 */     if ((this.connection.getIncludeInnodbStatusInDeadlockExceptions()) && (xOpen != null) && ((xOpen.startsWith("40")) || (xOpen.startsWith("41"))) && (this.streamingData == null))
/*      */     {
/* 4153 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 4156 */         rs = sqlQueryDirect(null, "SHOW ENGINE INNODB STATUS", this.connection.getEncoding(), null, -1, 1003, 1007, false, this.connection.getCatalog(), null);
/*      */ 
/* 4162 */         if (rs.next()) {
/* 4163 */           errorBuf.append("\n\n");
/* 4164 */           errorBuf.append(rs.getString("Status"));
/*      */         } else {
/* 4166 */           errorBuf.append("\n\n");
/* 4167 */           errorBuf.append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
/*      */         }
/*      */       }
/*      */       catch (Exception ex) {
/* 4171 */         errorBuf.append("\n\n");
/* 4172 */         errorBuf.append(Messages.getString("MysqlIO.InnoDBStatusFailed"));
/*      */ 
/* 4174 */         errorBuf.append("\n\n");
/* 4175 */         errorBuf.append(Util.stackTraceToString(ex));
/*      */       } finally {
/* 4177 */         if (rs != null) {
/* 4178 */           rs.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 4183 */     if (this.connection.getIncludeThreadDumpInDeadlockExceptions()) {
/* 4184 */       errorBuf.append("\n\n*** Java threads running at time of deadlock ***\n\n");
/*      */ 
/* 4186 */       ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
/* 4187 */       long[] threadIds = threadMBean.getAllThreadIds();
/*      */ 
/* 4189 */       ThreadInfo[] threads = threadMBean.getThreadInfo(threadIds, 2147483647);
/*      */ 
/* 4191 */       Object activeThreads = new ArrayList();
/*      */ 
/* 4193 */       for (ThreadInfo info : threads) {
/* 4194 */         if (info != null) {
/* 4195 */           ((List)activeThreads).add(info);
/*      */         }
/*      */       }
/*      */ 
/* 4199 */       for (ThreadInfo threadInfo : (List)activeThreads)
/*      */       {
/* 4203 */         errorBuf.append('"');
/* 4204 */         errorBuf.append(threadInfo.getThreadName());
/* 4205 */         errorBuf.append("\" tid=");
/* 4206 */         errorBuf.append(threadInfo.getThreadId());
/* 4207 */         errorBuf.append(" ");
/* 4208 */         errorBuf.append(threadInfo.getThreadState());
/*      */ 
/* 4210 */         if (threadInfo.getLockName() != null) {
/* 4211 */           errorBuf.append(" on lock=" + threadInfo.getLockName());
/*      */         }
/* 4213 */         if (threadInfo.isSuspended()) {
/* 4214 */           errorBuf.append(" (suspended)");
/*      */         }
/* 4216 */         if (threadInfo.isInNative()) {
/* 4217 */           errorBuf.append(" (running in native)");
/*      */         }
/*      */ 
/* 4220 */         StackTraceElement[] stackTrace = threadInfo.getStackTrace();
/*      */ 
/* 4222 */         if (stackTrace.length > 0) {
/* 4223 */           errorBuf.append(" in ");
/* 4224 */           errorBuf.append(stackTrace[0].getClassName());
/* 4225 */           errorBuf.append(".");
/* 4226 */           errorBuf.append(stackTrace[0].getMethodName());
/* 4227 */           errorBuf.append("()");
/*      */         }
/*      */ 
/* 4230 */         errorBuf.append("\n");
/*      */ 
/* 4232 */         if (threadInfo.getLockOwnerName() != null) {
/* 4233 */           errorBuf.append("\t owned by " + threadInfo.getLockOwnerName() + " Id=" + threadInfo.getLockOwnerId());
/*      */ 
/* 4235 */           errorBuf.append("\n");
/*      */         }
/*      */ 
/* 4238 */         for (int j = 0; j < stackTrace.length; j++) {
/* 4239 */           StackTraceElement ste = stackTrace[j];
/* 4240 */           errorBuf.append("\tat " + ste.toString());
/* 4241 */           errorBuf.append("\n");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void sendSplitPackets(Buffer packet, int packetLen)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4258 */       Buffer packetToSend = this.splitBufRef == null ? null : (Buffer)this.splitBufRef.get();
/* 4259 */       Buffer toCompress = (!this.useCompression) || (this.compressBufRef == null) ? null : (Buffer)this.compressBufRef.get();
/*      */ 
/* 4266 */       if (packetToSend == null) {
/* 4267 */         packetToSend = new Buffer(this.maxThreeBytes + 4);
/* 4268 */         this.splitBufRef = new SoftReference(packetToSend);
/*      */       }
/* 4270 */       if (this.useCompression) {
/* 4271 */         int cbuflen = packetLen + (packetLen / this.maxThreeBytes + 1) * 4;
/* 4272 */         if (toCompress == null) {
/* 4273 */           toCompress = new Buffer(cbuflen);
/* 4274 */         } else if (toCompress.getBufLength() < cbuflen) {
/* 4275 */           toCompress.setPosition(toCompress.getBufLength());
/* 4276 */           toCompress.ensureCapacity(cbuflen - toCompress.getBufLength());
/*      */         }
/*      */       }
/*      */ 
/* 4280 */       int len = packetLen - 4;
/* 4281 */       int splitSize = this.maxThreeBytes;
/* 4282 */       int originalPacketPos = 4;
/* 4283 */       byte[] origPacketBytes = packet.getByteBuffer();
/*      */ 
/* 4285 */       int toCompressPosition = 0;
/*      */ 
/* 4288 */       while (len >= 0) {
/* 4289 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 4291 */         if (len < splitSize) {
/* 4292 */           splitSize = len;
/*      */         }
/*      */ 
/* 4295 */         packetToSend.setPosition(0);
/* 4296 */         packetToSend.writeLongInt(splitSize);
/* 4297 */         packetToSend.writeByte(this.packetSequence);
/* 4298 */         if (len > 0) {
/* 4299 */           System.arraycopy(origPacketBytes, originalPacketPos, packetToSend.getByteBuffer(), 4, splitSize);
/*      */         }
/*      */ 
/* 4302 */         if (this.useCompression) {
/* 4303 */           System.arraycopy(packetToSend.getByteBuffer(), 0, toCompress.getByteBuffer(), toCompressPosition, 4 + splitSize);
/* 4304 */           toCompressPosition += 4 + splitSize;
/*      */         } else {
/* 4306 */           this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, 4 + splitSize);
/* 4307 */           this.mysqlOutput.flush();
/*      */         }
/*      */ 
/* 4310 */         originalPacketPos += splitSize;
/* 4311 */         len -= this.maxThreeBytes;
/*      */       }
/*      */ 
/* 4316 */       if (this.useCompression) {
/* 4317 */         len = toCompressPosition;
/* 4318 */         toCompressPosition = 0;
/* 4319 */         splitSize = this.maxThreeBytes - 3;
/* 4320 */         while (len >= 0) {
/* 4321 */           this.compressedPacketSequence = (byte)(this.compressedPacketSequence + 1);
/*      */ 
/* 4323 */           if (len < splitSize) {
/* 4324 */             splitSize = len;
/*      */           }
/*      */ 
/* 4327 */           Buffer compressedPacketToSend = compressPacket(toCompress, toCompressPosition, splitSize);
/* 4328 */           packetLen = compressedPacketToSend.getPosition();
/* 4329 */           this.mysqlOutput.write(compressedPacketToSend.getByteBuffer(), 0, packetLen);
/* 4330 */           this.mysqlOutput.flush();
/*      */ 
/* 4333 */           toCompressPosition += splitSize;
/* 4334 */           len -= this.maxThreeBytes - 3;
/*      */         }
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 4338 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reclaimLargeSharedSendPacket()
/*      */   {
/* 4344 */     if ((this.sharedSendPacket != null) && (this.sharedSendPacket.getCapacity() > 1048576))
/*      */     {
/* 4346 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean hadWarnings() {
/* 4351 */     return this.hadWarnings;
/*      */   }
/*      */ 
/*      */   void scanForAndThrowDataTruncation() throws SQLException {
/* 4355 */     if ((this.streamingData == null) && (versionMeetsMinimum(4, 1, 0)) && (this.connection.getJdbcCompliantTruncation()) && (this.warningCount > 0))
/*      */     {
/* 4357 */       SQLError.convertShowWarningsToSQLWarnings(this.connection, this.warningCount, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 4378 */     if (packet == null) {
/* 4379 */       packet = new Buffer(packLength);
/*      */     }
/*      */ 
/* 4382 */     if (writeClientParams) {
/* 4383 */       if (this.use41Extensions) {
/* 4384 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 4385 */           packet.writeLong(this.clientParam);
/* 4386 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 4391 */           packet.writeByte(8);
/*      */ 
/* 4394 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 4396 */           packet.writeLong(this.clientParam);
/* 4397 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 4400 */         packet.writeInt((int)this.clientParam);
/* 4401 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4406 */     packet.writeString(user, "Cp1252", this.connection);
/*      */ 
/* 4408 */     if (password.length() != 0)
/*      */     {
/* 4410 */       packet.writeString("xxxxxxxx", "Cp1252", this.connection);
/*      */     }
/*      */     else {
/* 4413 */       packet.writeString("", "Cp1252", this.connection);
/*      */     }
/*      */ 
/* 4416 */     if (this.useConnectWithDb) {
/* 4417 */       packet.writeString(database, "Cp1252", this.connection);
/*      */     }
/*      */ 
/* 4420 */     send(packet, packet.getPosition());
/*      */ 
/* 4425 */     if (password.length() > 0) {
/* 4426 */       Buffer b = readPacket();
/*      */ 
/* 4428 */       b.setPosition(0);
/*      */ 
/* 4430 */       byte[] replyAsBytes = b.getByteBuffer();
/*      */ 
/* 4432 */       if ((replyAsBytes.length == 25) && (replyAsBytes[0] != 0))
/*      */       {
/* 4434 */         if (replyAsBytes[0] != 42) {
/*      */           try
/*      */           {
/* 4437 */             byte[] buff = Security.passwordHashStage1(password);
/*      */ 
/* 4440 */             byte[] passwordHash = new byte[buff.length];
/* 4441 */             System.arraycopy(buff, 0, passwordHash, 0, buff.length);
/*      */ 
/* 4444 */             passwordHash = Security.passwordHashStage2(passwordHash, replyAsBytes);
/*      */ 
/* 4447 */             byte[] packetDataAfterSalt = new byte[replyAsBytes.length - 5];
/*      */ 
/* 4450 */             System.arraycopy(replyAsBytes, 4, packetDataAfterSalt, 0, replyAsBytes.length - 5);
/*      */ 
/* 4453 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/* 4456 */             Security.passwordCrypt(packetDataAfterSalt, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/* 4460 */             Security.passwordCrypt(mysqlScrambleBuff, buff, buff, 20);
/*      */ 
/* 4462 */             Buffer packet2 = new Buffer(25);
/* 4463 */             packet2.writeBytesNoNull(buff);
/*      */ 
/* 4465 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 4467 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 4469 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           try
/*      */           {
/* 4476 */             byte[] passwordHash = Security.createKeyFromOldPassword(password);
/*      */ 
/* 4479 */             byte[] netReadPos4 = new byte[replyAsBytes.length - 5];
/*      */ 
/* 4481 */             System.arraycopy(replyAsBytes, 4, netReadPos4, 0, replyAsBytes.length - 5);
/*      */ 
/* 4484 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/* 4487 */             Security.passwordCrypt(netReadPos4, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/* 4491 */             String scrambledPassword = Util.scramble(StringUtils.toString(mysqlScrambleBuff), password);
/*      */ 
/* 4494 */             Buffer packet2 = new Buffer(packLength);
/* 4495 */             packet2.writeString(scrambledPassword, "Cp1252", this.connection);
/* 4496 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 4498 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 4500 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.93") + Messages.getString("MysqlIO.94"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 4542 */     if (packet == null) {
/* 4543 */       packet = new Buffer(packLength);
/*      */     }
/*      */ 
/* 4546 */     if (writeClientParams) {
/* 4547 */       if (this.use41Extensions) {
/* 4548 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 4549 */           packet.writeLong(this.clientParam);
/* 4550 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 4555 */           packet.writeByte(33);
/*      */ 
/* 4558 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 4560 */           packet.writeLong(this.clientParam);
/* 4561 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 4564 */         packet.writeInt((int)this.clientParam);
/* 4565 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4570 */     packet.writeString(user, "utf-8", this.connection);
/*      */ 
/* 4572 */     if (password.length() != 0) {
/* 4573 */       packet.writeByte(20);
/*      */       try
/*      */       {
/* 4576 */         packet.writeBytesNoNull(Security.scramble411(password, this.seed, this.connection));
/*      */       } catch (NoSuchAlgorithmException nse) {
/* 4578 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/* 4582 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 4588 */       packet.writeByte(0);
/*      */     }
/*      */ 
/* 4591 */     if (this.useConnectWithDb) {
/* 4592 */       packet.writeString(database, "utf-8", this.connection);
/*      */     }
/*      */     else {
/* 4595 */       packet.writeByte(0);
/*      */     }
/*      */ 
/* 4598 */     if ((this.serverCapabilities & 0x200) != 0)
/*      */     {
/* 4600 */       String mysqlEncodingName = this.connection.getServerCharacterEncoding();
/* 4601 */       if (("ucs2".equalsIgnoreCase(mysqlEncodingName)) || ("utf16".equalsIgnoreCase(mysqlEncodingName)) || ("utf16le".equalsIgnoreCase(mysqlEncodingName)) || ("uft32".equalsIgnoreCase(mysqlEncodingName)))
/*      */       {
/* 4605 */         packet.writeByte(33);
/* 4606 */         packet.writeByte(0);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4611 */     send(packet, packet.getPosition());
/*      */ 
/* 4613 */     byte savePacketSequence = this.packetSequence++;
/*      */ 
/* 4615 */     Buffer reply = checkErrorPacket();
/*      */ 
/* 4617 */     if (reply.isLastDataPacket())
/*      */     {
/* 4622 */       savePacketSequence = (byte)(savePacketSequence + 1); this.packetSequence = savePacketSequence;
/* 4623 */       packet.clear();
/*      */ 
/* 4625 */       String seed323 = this.seed.substring(0, 8);
/* 4626 */       packet.writeString(Util.newCrypt(password, seed323));
/* 4627 */       send(packet, packet.getPosition());
/*      */ 
/* 4630 */       checkErrorPacket();
/*      */     }
/*      */   }
/*      */ 
/*      */   private final ResultSetRow unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4647 */     int numFields = fields.length;
/*      */ 
/* 4649 */     byte[][] unpackedRowData = new byte[numFields][];
/*      */ 
/* 4656 */     int nullCount = (numFields + 9) / 8;
/*      */ 
/* 4658 */     byte[] nullBitMask = new byte[nullCount];
/*      */ 
/* 4660 */     for (int i = 0; i < nullCount; i++) {
/* 4661 */       nullBitMask[i] = binaryData.readByte();
/*      */     }
/*      */ 
/* 4664 */     int nullMaskPos = 0;
/* 4665 */     int bit = 4;
/*      */ 
/* 4672 */     for (int i = 0; i < numFields; i++) {
/* 4673 */       if ((nullBitMask[nullMaskPos] & bit) != 0) {
/* 4674 */         unpackedRowData[i] = null;
/*      */       }
/* 4676 */       else if (resultSetConcurrency != 1008) {
/* 4677 */         extractNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */       else {
/* 4680 */         unpackNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */ 
/* 4685 */       if ((bit <<= 1 & 0xFF) == 0) {
/* 4686 */         bit = 1;
/*      */ 
/* 4688 */         nullMaskPos++;
/*      */       }
/*      */     }
/*      */ 
/* 4692 */     return new ByteArrayRow(unpackedRowData, getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 4698 */     Field curField = fields[columnIndex];
/*      */     int length;
/* 4700 */     switch (curField.getMysqlType()) {
/*      */     case 6:
/* 4702 */       break;
/*      */     case 1:
/* 4706 */       unpackedRowData[columnIndex] = { binaryData.readByte() };
/* 4707 */       break;
/*      */     case 2:
/*      */     case 13:
/* 4712 */       unpackedRowData[columnIndex] = binaryData.getBytes(2);
/* 4713 */       break;
/*      */     case 3:
/*      */     case 9:
/* 4717 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 4718 */       break;
/*      */     case 8:
/* 4721 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 4722 */       break;
/*      */     case 4:
/* 4725 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 4726 */       break;
/*      */     case 5:
/* 4729 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 4730 */       break;
/*      */     case 11:
/* 4733 */       length = (int)binaryData.readFieldLength();
/*      */ 
/* 4735 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/* 4737 */       break;
/*      */     case 10:
/* 4740 */       length = (int)binaryData.readFieldLength();
/*      */ 
/* 4742 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/* 4744 */       break;
/*      */     case 7:
/*      */     case 12:
/* 4747 */       length = (int)binaryData.readFieldLength();
/*      */ 
/* 4749 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/* 4750 */       break;
/*      */     case 0:
/*      */     case 15:
/*      */     case 246:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/*      */     case 253:
/*      */     case 254:
/*      */     case 255:
/* 4761 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 4763 */       break;
/*      */     case 16:
/* 4765 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 4767 */       break;
/*      */     default:
/* 4769 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 4781 */     Field curField = fields[columnIndex];
/*      */     int length;
/*      */     int hour;
/*      */     int minute;
/*      */     int seconds;
/*      */     int year;
/*      */     int month;
/*      */     int day;
/*      */     int after1000;
/*      */     int after100;
/* 4783 */     switch (curField.getMysqlType()) {
/*      */     case 6:
/* 4785 */       break;
/*      */     case 1:
/* 4789 */       byte tinyVal = binaryData.readByte();
/*      */ 
/* 4791 */       if (!curField.isUnsigned()) {
/* 4792 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(tinyVal));
/*      */       }
/*      */       else {
/* 4795 */         short unsignedTinyVal = (short)(tinyVal & 0xFF);
/*      */ 
/* 4797 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(unsignedTinyVal));
/*      */       }
/*      */ 
/* 4801 */       break;
/*      */     case 2:
/*      */     case 13:
/* 4806 */       short shortVal = (short)binaryData.readInt();
/*      */ 
/* 4808 */       if (!curField.isUnsigned()) {
/* 4809 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(shortVal));
/*      */       }
/*      */       else {
/* 4812 */         int unsignedShortVal = shortVal & 0xFFFF;
/*      */ 
/* 4814 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(unsignedShortVal));
/*      */       }
/*      */ 
/* 4818 */       break;
/*      */     case 3:
/*      */     case 9:
/* 4823 */       int intVal = (int)binaryData.readLong();
/*      */ 
/* 4825 */       if (!curField.isUnsigned()) {
/* 4826 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(intVal));
/*      */       }
/*      */       else {
/* 4829 */         long longVal = intVal & 0xFFFFFFFF;
/*      */ 
/* 4831 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(longVal));
/*      */       }
/*      */ 
/* 4835 */       break;
/*      */     case 8:
/* 4839 */       long longVal = binaryData.readLongLong();
/*      */ 
/* 4841 */       if (!curField.isUnsigned()) {
/* 4842 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(longVal));
/*      */       }
/*      */       else {
/* 4845 */         BigInteger asBigInteger = ResultSetImpl.convertLongToUlong(longVal);
/*      */ 
/* 4847 */         unpackedRowData[columnIndex] = StringUtils.getBytes(asBigInteger.toString());
/*      */       }
/*      */ 
/* 4851 */       break;
/*      */     case 4:
/* 4855 */       float floatVal = Float.intBitsToFloat(binaryData.readIntAsLong());
/*      */ 
/* 4857 */       unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(floatVal));
/*      */ 
/* 4860 */       break;
/*      */     case 5:
/* 4864 */       double doubleVal = Double.longBitsToDouble(binaryData.readLongLong());
/*      */ 
/* 4866 */       unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(doubleVal));
/*      */ 
/* 4869 */       break;
/*      */     case 11:
/* 4873 */       length = (int)binaryData.readFieldLength();
/*      */ 
/* 4875 */       hour = 0;
/* 4876 */       minute = 0;
/* 4877 */       seconds = 0;
/*      */ 
/* 4879 */       if (length != 0) {
/* 4880 */         binaryData.readByte();
/* 4881 */         binaryData.readLong();
/* 4882 */         hour = binaryData.readByte();
/* 4883 */         minute = binaryData.readByte();
/* 4884 */         seconds = binaryData.readByte();
/*      */ 
/* 4886 */         if (length > 8) {
/* 4887 */           binaryData.readLong();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4892 */       byte[] timeAsBytes = new byte[8];
/*      */ 
/* 4894 */       timeAsBytes[0] = (byte)Character.forDigit(hour / 10, 10);
/* 4895 */       timeAsBytes[1] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/* 4897 */       timeAsBytes[2] = 58;
/*      */ 
/* 4899 */       timeAsBytes[3] = (byte)Character.forDigit(minute / 10, 10);
/*      */ 
/* 4901 */       timeAsBytes[4] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/* 4904 */       timeAsBytes[5] = 58;
/*      */ 
/* 4906 */       timeAsBytes[6] = (byte)Character.forDigit(seconds / 10, 10);
/*      */ 
/* 4908 */       timeAsBytes[7] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/* 4911 */       unpackedRowData[columnIndex] = timeAsBytes;
/*      */ 
/* 4914 */       break;
/*      */     case 10:
/* 4917 */       length = (int)binaryData.readFieldLength();
/*      */ 
/* 4919 */       year = 0;
/* 4920 */       month = 0;
/* 4921 */       day = 0;
/*      */ 
/* 4923 */       hour = 0;
/* 4924 */       minute = 0;
/* 4925 */       seconds = 0;
/*      */ 
/* 4927 */       if (length != 0) {
/* 4928 */         year = binaryData.readInt();
/* 4929 */         month = binaryData.readByte();
/* 4930 */         day = binaryData.readByte();
/*      */       }
/*      */ 
/* 4933 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4934 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 4936 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 4939 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 4941 */             throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 4945 */           year = 1;
/* 4946 */           month = 1;
/* 4947 */           day = 1;
/*      */         }
/*      */       }
/*      */       else {
/* 4951 */         byte[] dateAsBytes = new byte[10];
/*      */ 
/* 4953 */         dateAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/* 4956 */         after1000 = year % 1000;
/*      */ 
/* 4958 */         dateAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/* 4961 */         after100 = after1000 % 100;
/*      */ 
/* 4963 */         dateAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */ 
/* 4965 */         dateAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/* 4968 */         dateAsBytes[4] = 45;
/*      */ 
/* 4970 */         dateAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */ 
/* 4972 */         dateAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/* 4975 */         dateAsBytes[7] = 45;
/*      */ 
/* 4977 */         dateAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/* 4978 */         dateAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/* 4980 */         unpackedRowData[columnIndex] = dateAsBytes;
/*      */       }
/*      */ 
/* 4983 */       break;
/*      */     case 7:
/*      */     case 12:
/* 4987 */       length = (int)binaryData.readFieldLength();
/*      */ 
/* 4989 */       year = 0;
/* 4990 */       month = 0;
/* 4991 */       day = 0;
/*      */ 
/* 4993 */       hour = 0;
/* 4994 */       minute = 0;
/* 4995 */       seconds = 0;
/*      */ 
/* 4997 */       int nanos = 0;
/*      */ 
/* 4999 */       if (length != 0) {
/* 5000 */         year = binaryData.readInt();
/* 5001 */         month = binaryData.readByte();
/* 5002 */         day = binaryData.readByte();
/*      */ 
/* 5004 */         if (length > 4) {
/* 5005 */           hour = binaryData.readByte();
/* 5006 */           minute = binaryData.readByte();
/* 5007 */           seconds = binaryData.readByte();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5015 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 5016 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5018 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 5021 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 5023 */             throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 5027 */           year = 1;
/* 5028 */           month = 1;
/* 5029 */           day = 1;
/*      */         }
/*      */       }
/*      */       else {
/* 5033 */         int stringLength = 19;
/*      */ 
/* 5035 */         byte[] nanosAsBytes = StringUtils.getBytes(Integer.toString(nanos));
/*      */ 
/* 5037 */         stringLength += 1 + nanosAsBytes.length;
/*      */ 
/* 5039 */         byte[] datetimeAsBytes = new byte[stringLength];
/*      */ 
/* 5041 */         datetimeAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/* 5044 */         after1000 = year % 1000;
/*      */ 
/* 5046 */         datetimeAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/* 5049 */         after100 = after1000 % 100;
/*      */ 
/* 5051 */         datetimeAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */ 
/* 5053 */         datetimeAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/* 5056 */         datetimeAsBytes[4] = 45;
/*      */ 
/* 5058 */         datetimeAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */ 
/* 5060 */         datetimeAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/* 5063 */         datetimeAsBytes[7] = 45;
/*      */ 
/* 5065 */         datetimeAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/*      */ 
/* 5067 */         datetimeAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/* 5070 */         datetimeAsBytes[10] = 32;
/*      */ 
/* 5072 */         datetimeAsBytes[11] = (byte)Character.forDigit(hour / 10, 10);
/*      */ 
/* 5074 */         datetimeAsBytes[12] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/* 5077 */         datetimeAsBytes[13] = 58;
/*      */ 
/* 5079 */         datetimeAsBytes[14] = (byte)Character.forDigit(minute / 10, 10);
/*      */ 
/* 5081 */         datetimeAsBytes[15] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/* 5084 */         datetimeAsBytes[16] = 58;
/*      */ 
/* 5086 */         datetimeAsBytes[17] = (byte)Character.forDigit(seconds / 10, 10);
/*      */ 
/* 5088 */         datetimeAsBytes[18] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/* 5091 */         datetimeAsBytes[19] = 46;
/*      */ 
/* 5093 */         int nanosOffset = 20;
/*      */ 
/* 5095 */         for (int j = 0; j < nanosAsBytes.length; j++) {
/* 5096 */           datetimeAsBytes[(nanosOffset + j)] = nanosAsBytes[j];
/*      */         }
/*      */ 
/* 5099 */         unpackedRowData[columnIndex] = datetimeAsBytes;
/*      */       }
/*      */ 
/* 5102 */       break;
/*      */     case 0:
/*      */     case 15:
/*      */     case 16:
/*      */     case 246:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/*      */     case 253:
/*      */     case 254:
/* 5114 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 5116 */       break;
/*      */     default:
/* 5119 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void negotiateSSLConnection(String user, String password, String database, int packLength)
/*      */     throws SQLException
/*      */   {
/* 5142 */     if (!ExportControlled.enabled()) {
/* 5143 */       throw new ConnectionFeatureNotAvailableException(this.connection, this.lastPacketSentTimeMs, null);
/*      */     }
/*      */ 
/* 5147 */     if ((this.serverCapabilities & 0x8000) != 0) {
/* 5148 */       this.clientParam |= 32768L;
/*      */     }
/*      */ 
/* 5151 */     this.clientParam |= 2048L;
/*      */ 
/* 5153 */     Buffer packet = new Buffer(packLength);
/*      */ 
/* 5155 */     if (this.use41Extensions) {
/* 5156 */       packet.writeLong(this.clientParam);
/* 5157 */       packet.writeLong(this.maxThreeBytes);
/* 5158 */       int charsetIndex = 0;
/* 5159 */       if (this.connection.getEncoding() != null) {
/* 5160 */         charsetIndex = CharsetMapping.getCharsetIndexForMysqlEncodingName(CharsetMapping.getMysqlEncodingForJavaEncoding(this.connection.getEncoding(), this.connection));
/*      */       }
/* 5162 */       packet.writeByte(charsetIndex == 0 ? 33 : (byte)charsetIndex);
/* 5163 */       packet.writeBytesNoNull(new byte[23]);
/*      */     } else {
/* 5165 */       packet.writeInt((int)this.clientParam);
/*      */     }
/*      */ 
/* 5168 */     send(packet, packet.getPosition());
/*      */ 
/* 5170 */     ExportControlled.transformSocketToSSLSocket(this);
/*      */   }
/*      */ 
/*      */   protected int getServerStatus() {
/* 5174 */     return this.serverStatus;
/*      */   }
/*      */ 
/*      */   protected List<ResultSetRow> fetchRowsViaCursor(List<ResultSetRow> fetchedRows, long statementId, Field[] columnTypes, int fetchSize, boolean useBufferRowExplicit)
/*      */     throws SQLException
/*      */   {
/* 5180 */     if (fetchedRows == null)
/* 5181 */       fetchedRows = new ArrayList(fetchSize);
/*      */     else {
/* 5183 */       fetchedRows.clear();
/*      */     }
/*      */ 
/* 5186 */     this.sharedSendPacket.clear();
/*      */ 
/* 5188 */     this.sharedSendPacket.writeByte(28);
/* 5189 */     this.sharedSendPacket.writeLong(statementId);
/* 5190 */     this.sharedSendPacket.writeLong(fetchSize);
/*      */ 
/* 5192 */     sendCommand(28, null, this.sharedSendPacket, true, null, 0);
/*      */ 
/* 5195 */     ResultSetRow row = null;
/*      */ 
/* 5198 */     while ((row = nextRow(columnTypes, columnTypes.length, true, 1007, false, useBufferRowExplicit, false, null)) != null) {
/* 5199 */       fetchedRows.add(row);
/*      */     }
/*      */ 
/* 5202 */     return fetchedRows;
/*      */   }
/*      */ 
/*      */   protected long getThreadId() {
/* 5206 */     return this.threadId;
/*      */   }
/*      */ 
/*      */   protected boolean useNanosForElapsedTime() {
/* 5210 */     return this.useNanosForElapsedTime;
/*      */   }
/*      */ 
/*      */   protected long getSlowQueryThreshold() {
/* 5214 */     return this.slowQueryThreshold;
/*      */   }
/*      */ 
/*      */   protected String getQueryTimingUnits() {
/* 5218 */     return this.queryTimingUnits;
/*      */   }
/*      */ 
/*      */   protected int getCommandCount() {
/* 5222 */     return this.commandCount;
/*      */   }
/*      */ 
/*      */   private void checkTransactionState(int oldStatus) throws SQLException {
/* 5226 */     boolean previouslyInTrans = (oldStatus & 0x1) != 0;
/* 5227 */     boolean currentlyInTrans = (this.serverStatus & 0x1) != 0;
/*      */ 
/* 5229 */     if ((previouslyInTrans) && (!currentlyInTrans))
/* 5230 */       this.connection.transactionCompleted();
/* 5231 */     else if ((!previouslyInTrans) && (currentlyInTrans))
/* 5232 */       this.connection.transactionBegun();
/*      */   }
/*      */ 
/*      */   protected void setStatementInterceptors(List<StatementInterceptorV2> statementInterceptors)
/*      */   {
/* 5237 */     this.statementInterceptors = statementInterceptors;
/*      */   }
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 5241 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   protected void setSocketTimeout(int milliseconds) throws SQLException {
/*      */     try {
/* 5246 */       this.mysqlConnection.setSoTimeout(milliseconds);
/*      */     } catch (SocketException e) {
/* 5248 */       SQLException sqlEx = SQLError.createSQLException("Invalid socket timeout value or state", "S1009", getExceptionInterceptor());
/* 5249 */       sqlEx.initCause(e);
/*      */ 
/* 5251 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  137 */     OutputStreamWriter outWriter = null;
/*      */     try
/*      */     {
/*  145 */       outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
/*  146 */       jvmPlatformCharset = outWriter.getEncoding();
/*      */     } finally {
/*      */       try {
/*  149 */         if (outWriter != null)
/*  150 */           outWriter.close();
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.MysqlIO
 * JD-Core Version:    0.6.0
 */