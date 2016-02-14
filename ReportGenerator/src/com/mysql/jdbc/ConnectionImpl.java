/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogFactory;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.log.NullLogger;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.util.LRUCache;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLPermission;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ public class ConnectionImpl extends ConnectionPropertiesImpl
/*      */   implements MySQLConnection
/*      */ {
/*      */   private static final long serialVersionUID = 2877471301981509474L;
/*   87 */   private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission("setNetworkTimeout");
/*      */ 
/*   89 */   private static final SQLPermission ABORT_PERM = new SQLPermission("abort");
/*      */   private static final String JDBC_LOCAL_CHARACTER_SET_RESULTS = "jdbc.local.character_set_results";
/*   97 */   private MySQLConnection proxy = null;
/*      */ 
/*  225 */   private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
/*      */   public static Map<?, ?> charsetMap;
/*      */   protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
/*      */   private static final int HISTOGRAM_BUCKETS = 20;
/*      */   private static final String LOGGER_INSTANCE_NAME = "MySQL";
/*  245 */   private static Map<String, Integer> mapTransIsolationNameToValue = null;
/*      */ 
/*  248 */   private static final Log NULL_LOGGER = new NullLogger("MySQL");
/*      */   protected static Map<?, ?> roundRobinStatsMap;
/*  252 */   private static final Map<String, Map<Long, String>> serverCollationByUrl = new HashMap();
/*      */ 
/*  258 */   private static final Map<String, Map<Integer, String>> serverJavaCharsetByUrl = new HashMap();
/*      */ 
/*  263 */   private static final Map<String, Map<Integer, String>> serverCustomCharsetByUrl = new HashMap();
/*      */ 
/*  268 */   private static final Map<String, Map<String, Integer>> serverCustomMblenByUrl = new HashMap();
/*      */   private CacheAdapter<String, Map<String, String>> serverConfigCache;
/*      */   private long queryTimeCount;
/*      */   private double queryTimeSum;
/*      */   private double queryTimeSumSquares;
/*      */   private double queryTimeMean;
/*      */   private transient Timer cancelTimer;
/*      */   private List<Extension> connectionLifecycleInterceptors;
/*      */   private static final Constructor<?> JDBC_4_CONNECTION_CTOR;
/*      */   private static final int DEFAULT_RESULT_SET_TYPE = 1003;
/*      */   private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
/*      */   private static final Random random;
/*  443 */   private boolean autoCommit = true;
/*      */   private CacheAdapter<String, PreparedStatement.ParseInfo> cachedPreparedStatementParams;
/*  451 */   private String characterSetMetadata = null;
/*      */ 
/*  457 */   private String characterSetResultsOnServer = null;
/*      */ 
/*  464 */   private Map<String, Object> charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
/*      */ 
/*  468 */   private long connectionCreationTimeMillis = 0L;
/*      */   private long connectionId;
/*  474 */   private String database = null;
/*      */ 
/*  477 */   private java.sql.DatabaseMetaData dbmd = null;
/*      */   private TimeZone defaultTimeZone;
/*      */   private ProfilerEventHandler eventSink;
/*      */   private Throwable forceClosedReason;
/*  488 */   private boolean hasIsolationLevels = false;
/*      */ 
/*  491 */   private boolean hasQuotedIdentifiers = false;
/*      */ 
/*  494 */   private String host = null;
/*      */ 
/*  500 */   public Map<Integer, String> indexToJavaCharset = new HashMap();
/*      */ 
/*  502 */   public Map<Integer, String> indexToCustomMysqlCharset = new HashMap();
/*      */ 
/*  504 */   private Map<String, Integer> mysqlCharsetToCustomMblen = new HashMap();
/*      */ 
/*  507 */   private transient MysqlIO io = null;
/*      */ 
/*  509 */   private boolean isClientTzUTC = false;
/*      */ 
/*  512 */   private boolean isClosed = true;
/*      */ 
/*  515 */   private boolean isInGlobalTx = false;
/*      */ 
/*  518 */   private boolean isRunningOnJDK13 = false;
/*      */ 
/*  521 */   private int isolationLevel = 2;
/*      */ 
/*  523 */   private boolean isServerTzUTC = false;
/*      */ 
/*  526 */   private long lastQueryFinishedTime = 0L;
/*      */ 
/*  529 */   private transient Log log = NULL_LOGGER;
/*      */ 
/*  535 */   private long longestQueryTimeMs = 0L;
/*      */ 
/*  538 */   private boolean lowerCaseTableNames = false;
/*      */ 
/*  543 */   private long maximumNumberTablesAccessed = 0L;
/*      */ 
/*  546 */   private boolean maxRowsChanged = false;
/*      */   private long metricsLastReportedMs;
/*  551 */   private long minimumNumberTablesAccessed = 9223372036854775807L;
/*      */ 
/*  554 */   private String myURL = null;
/*      */ 
/*  557 */   private boolean needsPing = false;
/*      */ 
/*  559 */   private int netBufferLength = 16384;
/*      */ 
/*  561 */   private boolean noBackslashEscapes = false;
/*      */ 
/*  563 */   private long numberOfPreparedExecutes = 0L;
/*      */ 
/*  565 */   private long numberOfPrepares = 0L;
/*      */ 
/*  567 */   private long numberOfQueriesIssued = 0L;
/*      */ 
/*  569 */   private long numberOfResultSetsCreated = 0L;
/*      */   private long[] numTablesMetricsHistBreakpoints;
/*      */   private int[] numTablesMetricsHistCounts;
/*  575 */   private long[] oldHistBreakpoints = null;
/*      */ 
/*  577 */   private int[] oldHistCounts = null;
/*      */   private Map<Statement, Statement> openStatements;
/*      */   private LRUCache parsedCallableStatementCache;
/*  584 */   private boolean parserKnowsUnicode = false;
/*      */ 
/*  587 */   private String password = null;
/*      */   private long[] perfMetricsHistBreakpoints;
/*      */   private int[] perfMetricsHistCounts;
/*      */   private String pointOfOrigin;
/*  597 */   private int port = 3306;
/*      */ 
/*  600 */   protected Properties props = null;
/*      */ 
/*  603 */   private boolean readInfoMsg = false;
/*      */ 
/*  606 */   private boolean readOnly = false;
/*      */   protected LRUCache resultSetMetadataCache;
/*  612 */   private TimeZone serverTimezoneTZ = null;
/*      */ 
/*  615 */   private Map<String, String> serverVariables = null;
/*      */ 
/*  617 */   private long shortestQueryTimeMs = 9223372036854775807L;
/*      */   private Map<Statement, Statement> statementsUsingMaxRows;
/*  622 */   private double totalQueryTimeMs = 0.0D;
/*      */ 
/*  625 */   private boolean transactionsSupported = false;
/*      */   private Map<String, Class<?>> typeMap;
/*  634 */   private boolean useAnsiQuotes = false;
/*      */ 
/*  637 */   private String user = null;
/*      */ 
/*  643 */   private boolean useServerPreparedStmts = false;
/*      */   private LRUCache serverSideStatementCheckCache;
/*      */   private LRUCache serverSideStatementCache;
/*      */   private Calendar sessionCalendar;
/*      */   private Calendar utcCalendar;
/*      */   private String origHostToConnectTo;
/*      */   private int origPortToConnectTo;
/*      */   private String origDatabaseToConnectTo;
/*  659 */   private String errorMessageEncoding = "Cp1252";
/*      */   private boolean usePlatformCharsetConverters;
/*  666 */   private boolean hasTriedMasterFlag = false;
/*      */ 
/*  672 */   private String statementComment = null;
/*      */   private boolean storesLowerCaseTableName;
/*      */   private List<StatementInterceptorV2> statementInterceptors;
/*      */   private boolean requiresEscapingEncoder;
/*      */   private String hostPortPair;
/* 3988 */   private boolean usingCachedConfig = false;
/*      */   private static final String SERVER_VERSION_STRING_VAR_NAME = "server_version_string";
/* 4196 */   private int autoIncrementIncrement = 0;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */ 
/*      */   public String getHost()
/*      */   {
/*   94 */     return this.host;
/*      */   }
/*      */ 
/*      */   public boolean isProxySet()
/*      */   {
/*  100 */     return this.proxy != null;
/*      */   }
/*      */ 
/*      */   public void setProxy(MySQLConnection proxy) {
/*  104 */     this.proxy = proxy;
/*      */   }
/*      */ 
/*      */   private MySQLConnection getProxy()
/*      */   {
/*  111 */     return this.proxy != null ? this.proxy : this;
/*      */   }
/*      */ 
/*      */   public MySQLConnection getLoadBalanceSafeProxy() {
/*  115 */     return getProxy();
/*      */   }
/*      */ 
/*      */   protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor)
/*      */   {
/*  315 */     String origMessage = sqlEx.getMessage();
/*  316 */     String sqlState = sqlEx.getSQLState();
/*  317 */     int vendorErrorCode = sqlEx.getErrorCode();
/*      */ 
/*  319 */     StringBuffer messageBuf = new StringBuffer(origMessage.length() + messageToAppend.length());
/*      */ 
/*  321 */     messageBuf.append(origMessage);
/*  322 */     messageBuf.append(messageToAppend);
/*      */ 
/*  324 */     SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(messageBuf.toString(), sqlState, vendorErrorCode, interceptor);
/*      */     try
/*      */     {
/*  334 */       Method getStackTraceMethod = null;
/*  335 */       Method setStackTraceMethod = null;
/*  336 */       Object theStackTraceAsObject = null;
/*      */ 
/*  338 */       Class stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
/*  339 */       Class stackTraceElementArrayClass = Array.newInstance(stackTraceElementClass, new int[] { 0 }).getClass();
/*      */ 
/*  342 */       getStackTraceMethod = Throwable.class.getMethod("getStackTrace", new Class[0]);
/*      */ 
/*  345 */       setStackTraceMethod = Throwable.class.getMethod("setStackTrace", new Class[] { stackTraceElementArrayClass });
/*      */ 
/*  348 */       if ((getStackTraceMethod != null) && (setStackTraceMethod != null)) {
/*  349 */         theStackTraceAsObject = getStackTraceMethod.invoke(sqlEx, new Object[0]);
/*      */ 
/*  351 */         setStackTraceMethod.invoke(sqlExceptionWithNewMessage, new Object[] { theStackTraceAsObject });
/*      */       }
/*      */     }
/*      */     catch (NoClassDefFoundError noClassDefFound)
/*      */     {
/*      */     }
/*      */     catch (NoSuchMethodException noSuchMethodEx)
/*      */     {
/*      */     }
/*      */     catch (Throwable catchAll) {
/*      */     }
/*  362 */     return sqlExceptionWithNewMessage;
/*      */   }
/*      */ 
/*      */   public synchronized Timer getCancelTimer() {
/*  366 */     if (this.cancelTimer == null) {
/*  367 */       boolean createdNamedTimer = false;
/*      */       try
/*      */       {
/*  372 */         Constructor ctr = Timer.class.getConstructor(new Class[] { String.class, Boolean.TYPE });
/*      */ 
/*  374 */         this.cancelTimer = ((Timer)ctr.newInstance(new Object[] { "MySQL Statement Cancellation Timer", Boolean.TRUE }));
/*  375 */         createdNamedTimer = true;
/*      */       } catch (Throwable t) {
/*  377 */         createdNamedTimer = false;
/*      */       }
/*      */ 
/*  380 */       if (!createdNamedTimer) {
/*  381 */         this.cancelTimer = new Timer(true);
/*      */       }
/*      */     }
/*      */ 
/*  385 */     return this.cancelTimer;
/*      */   }
/*      */ 
/*      */   protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*      */     throws SQLException
/*      */   {
/*  399 */     if (!Util.isJdbc4()) {
/*  400 */       return new ConnectionImpl(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*      */     }
/*      */ 
/*  404 */     return (Connection)Util.handleNewInstance(JDBC_4_CONNECTION_CTOR, new Object[] { hostToConnectTo, Integer.valueOf(portToConnectTo), info, databaseToConnectTo, url }, null);
/*      */   }
/*      */ 
/*      */   protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList)
/*      */   {
/*  423 */     int indexRange = hostList.size();
/*      */ 
/*  425 */     int index = random.nextInt(indexRange);
/*      */ 
/*  427 */     return index;
/*      */   }
/*      */ 
/*      */   private static boolean nullSafeCompare(String s1, String s2) {
/*  431 */     if ((s1 == null) && (s2 == null)) {
/*  432 */       return true;
/*      */     }
/*      */ 
/*  435 */     if ((s1 == null) && (s2 != null)) {
/*  436 */       return false;
/*      */     }
/*      */ 
/*  439 */     return (s1 != null) && (s1.equals(s2));
/*      */   }
/*      */ 
/*      */   protected ConnectionImpl()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*      */     throws SQLException
/*      */   {
/*  714 */     this.connectionCreationTimeMillis = System.currentTimeMillis();
/*      */ 
/*  716 */     if (databaseToConnectTo == null) {
/*  717 */       databaseToConnectTo = "";
/*      */     }
/*      */ 
/*  724 */     this.origHostToConnectTo = hostToConnectTo;
/*  725 */     this.origPortToConnectTo = portToConnectTo;
/*  726 */     this.origDatabaseToConnectTo = databaseToConnectTo;
/*      */     try
/*      */     {
/*  729 */       Blob.class.getMethod("truncate", new Class[] { Long.TYPE });
/*      */ 
/*  731 */       this.isRunningOnJDK13 = false;
/*      */     } catch (NoSuchMethodException nsme) {
/*  733 */       this.isRunningOnJDK13 = true;
/*      */     }
/*      */ 
/*  736 */     this.sessionCalendar = new GregorianCalendar();
/*  737 */     this.utcCalendar = new GregorianCalendar();
/*  738 */     this.utcCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */ 
/*  750 */     this.log = LogFactory.getLogger(getLogger(), "MySQL", getExceptionInterceptor());
/*      */ 
/*  754 */     this.defaultTimeZone = Util.getDefaultTimeZone();
/*      */ 
/*  756 */     if ("GMT".equalsIgnoreCase(this.defaultTimeZone.getID()))
/*  757 */       this.isClientTzUTC = true;
/*      */     else {
/*  759 */       this.isClientTzUTC = false;
/*      */     }
/*      */ 
/*  762 */     this.openStatements = new HashMap();
/*      */ 
/*  764 */     if (NonRegisteringDriver.isHostPropertiesList(hostToConnectTo)) {
/*  765 */       Properties hostSpecificProps = NonRegisteringDriver.expandHostKeyValues(hostToConnectTo);
/*      */ 
/*  767 */       Enumeration propertyNames = hostSpecificProps.propertyNames();
/*      */ 
/*  769 */       while (propertyNames.hasMoreElements()) {
/*  770 */         String propertyName = propertyNames.nextElement().toString();
/*  771 */         String propertyValue = hostSpecificProps.getProperty(propertyName);
/*      */ 
/*  773 */         info.setProperty(propertyName, propertyValue);
/*      */       }
/*      */ 
/*      */     }
/*  777 */     else if (hostToConnectTo == null) {
/*  778 */       this.host = "localhost";
/*  779 */       this.hostPortPair = (this.host + ":" + portToConnectTo);
/*      */     } else {
/*  781 */       this.host = hostToConnectTo;
/*      */ 
/*  783 */       if (hostToConnectTo.indexOf(":") == -1)
/*  784 */         this.hostPortPair = (this.host + ":" + portToConnectTo);
/*      */       else {
/*  786 */         this.hostPortPair = this.host;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  791 */     this.port = portToConnectTo;
/*      */ 
/*  793 */     this.database = databaseToConnectTo;
/*  794 */     this.myURL = url;
/*  795 */     this.user = info.getProperty("user");
/*  796 */     this.password = info.getProperty("password");
/*      */ 
/*  799 */     if ((this.user == null) || (this.user.equals(""))) {
/*  800 */       this.user = "";
/*      */     }
/*      */ 
/*  803 */     if (this.password == null) {
/*  804 */       this.password = "";
/*      */     }
/*      */ 
/*  807 */     this.props = info;
/*      */ 
/*  811 */     initializeDriverProperties(info);
/*      */ 
/*  813 */     if (getUseUsageAdvisor())
/*  814 */       this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
/*      */     else {
/*  816 */       this.pointOfOrigin = "";
/*      */     }
/*      */     try
/*      */     {
/*  820 */       this.dbmd = getMetaData(false, false);
/*  821 */       initializeSafeStatementInterceptors();
/*  822 */       createNewIO(false);
/*  823 */       unSafeStatementInterceptors();
/*      */     } catch (SQLException ex) {
/*  825 */       cleanup(ex);
/*      */ 
/*  828 */       throw ex;
/*      */     } catch (Exception ex) {
/*  830 */       cleanup(ex);
/*      */ 
/*  832 */       StringBuffer mesg = new StringBuffer(128);
/*      */ 
/*  834 */       if (!getParanoid()) {
/*  835 */         mesg.append("Cannot connect to MySQL server on ");
/*  836 */         mesg.append(this.host);
/*  837 */         mesg.append(":");
/*  838 */         mesg.append(this.port);
/*  839 */         mesg.append(".\n\n");
/*  840 */         mesg.append("Make sure that there is a MySQL server ");
/*  841 */         mesg.append("running on the machine/port you are trying ");
/*  842 */         mesg.append("to connect to and that the machine this software is running on ");
/*      */ 
/*  845 */         mesg.append("is able to connect to this host/port (i.e. not firewalled). ");
/*      */ 
/*  847 */         mesg.append("Also make sure that the server has not been started with the --skip-networking ");
/*      */ 
/*  850 */         mesg.append("flag.\n\n");
/*      */       } else {
/*  852 */         mesg.append("Unable to connect to database.");
/*      */       }
/*      */ 
/*  855 */       SQLException sqlEx = SQLError.createSQLException(mesg.toString(), "08S01", getExceptionInterceptor());
/*      */ 
/*  858 */       sqlEx.initCause(ex);
/*      */ 
/*  860 */       throw sqlEx;
/*      */     }
/*      */ 
/*  863 */     NonRegisteringDriver.trackConnection(this);
/*      */   }
/*      */ 
/*      */   public void unSafeStatementInterceptors() throws SQLException
/*      */   {
/*  868 */     ArrayList unSafedStatementInterceptors = new ArrayList(this.statementInterceptors.size());
/*      */ 
/*  870 */     for (int i = 0; i < this.statementInterceptors.size(); i++) {
/*  871 */       NoSubInterceptorWrapper wrappedInterceptor = (NoSubInterceptorWrapper)this.statementInterceptors.get(i);
/*      */ 
/*  873 */       unSafedStatementInterceptors.add(wrappedInterceptor.getUnderlyingInterceptor());
/*      */     }
/*      */ 
/*  876 */     this.statementInterceptors = unSafedStatementInterceptors;
/*      */ 
/*  878 */     if (this.io != null)
/*  879 */       this.io.setStatementInterceptors(this.statementInterceptors);
/*      */   }
/*      */ 
/*      */   public void initializeSafeStatementInterceptors() throws SQLException
/*      */   {
/*  884 */     this.isClosed = false;
/*      */ 
/*  886 */     List unwrappedInterceptors = Util.loadExtensions(this, this.props, getStatementInterceptors(), "MysqlIo.BadStatementInterceptor", getExceptionInterceptor());
/*      */ 
/*  890 */     this.statementInterceptors = new ArrayList(unwrappedInterceptors.size());
/*      */ 
/*  892 */     for (int i = 0; i < unwrappedInterceptors.size(); i++) {
/*  893 */       Extension interceptor = (Extension)unwrappedInterceptors.get(i);
/*      */ 
/*  897 */       if ((interceptor instanceof StatementInterceptor)) {
/*  898 */         if (ReflectiveStatementInterceptorAdapter.getV2PostProcessMethod(interceptor.getClass()) != null)
/*  899 */           this.statementInterceptors.add(new NoSubInterceptorWrapper(new ReflectiveStatementInterceptorAdapter((StatementInterceptor)interceptor)));
/*      */         else
/*  901 */           this.statementInterceptors.add(new NoSubInterceptorWrapper(new V1toV2StatementInterceptorAdapter((StatementInterceptor)interceptor)));
/*      */       }
/*      */       else
/*  904 */         this.statementInterceptors.add(new NoSubInterceptorWrapper((StatementInterceptorV2)interceptor));
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<StatementInterceptorV2> getStatementInterceptorsInstances()
/*      */   {
/*  912 */     return this.statementInterceptors;
/*      */   }
/*      */ 
/*      */   private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound)
/*      */   {
/*  918 */     if (histogramCounts == null) {
/*  919 */       createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
/*      */     }
/*      */     else
/*  922 */       for (int i = 0; i < 20; i++)
/*  923 */         if (histogramBreakpoints[i] >= value) {
/*  924 */           histogramCounts[i] += numberOfTimes;
/*      */ 
/*  926 */           break;
/*      */         }
/*      */   }
/*      */ 
/*      */   private void addToPerformanceHistogram(long value, int numberOfTimes)
/*      */   {
/*  933 */     checkAndCreatePerformanceHistogram();
/*      */ 
/*  935 */     addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, this.shortestQueryTimeMs == 9223372036854775807L ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */   private void addToTablesAccessedHistogram(long value, int numberOfTimes)
/*      */   {
/*  942 */     checkAndCreateTablesAccessedHistogram();
/*      */ 
/*  944 */     addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, this.minimumNumberTablesAccessed == 9223372036854775807L ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */   private void buildCollationMapping()
/*      */     throws SQLException
/*      */   {
/*  960 */     HashMap javaCharset = null;
/*      */ 
/*  962 */     if (versionMeetsMinimum(4, 1, 0))
/*      */     {
/*  964 */       TreeMap sortedCollationMap = null;
/*  965 */       HashMap customCharset = null;
/*  966 */       HashMap customMblen = null;
/*      */ 
/*  968 */       if (getCacheServerConfiguration()) {
/*  969 */         synchronized (serverCollationByUrl) {
/*  970 */           sortedCollationMap = (TreeMap)serverCollationByUrl.get(getURL());
/*  971 */           javaCharset = (HashMap)serverJavaCharsetByUrl.get(getURL());
/*  972 */           customCharset = (HashMap)serverCustomCharsetByUrl.get(getURL());
/*  973 */           customMblen = (HashMap)serverCustomMblenByUrl.get(getURL());
/*      */         }
/*      */       }
/*      */ 
/*  977 */       java.sql.Statement stmt = null;
/*  978 */       ResultSet results = null;
/*      */       try
/*      */       {
/*  981 */         if (sortedCollationMap == null) {
/*  982 */           sortedCollationMap = new TreeMap();
/*  983 */           javaCharset = new HashMap();
/*  984 */           customCharset = new HashMap();
/*  985 */           customMblen = new HashMap();
/*      */ 
/*  987 */           stmt = getMetadataSafeStatement();
/*      */           try
/*      */           {
/*  990 */             results = stmt.executeQuery("SHOW COLLATION");
/*  991 */             if (versionMeetsMinimum(5, 0, 0))
/*  992 */               Util.resultSetToMap(sortedCollationMap, results, 3, 2);
/*      */             else
/*  994 */               while (results.next())
/*  995 */                 sortedCollationMap.put(Long.valueOf(results.getLong(3)), results.getString(2));
/*      */           }
/*      */           catch (SQLException ex)
/*      */           {
/*  999 */             if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1000 */               throw ex;
/*      */             }
/*      */           }
/*      */ 
/* 1004 */           for (Iterator indexIter = sortedCollationMap.entrySet().iterator(); indexIter.hasNext(); ) {
/* 1005 */             Map.Entry indexEntry = (Map.Entry)indexIter.next();
/*      */ 
/* 1007 */             int collationIndex = ((Long)indexEntry.getKey()).intValue();
/* 1008 */             String charsetName = (String)indexEntry.getValue();
/*      */ 
/* 1010 */             javaCharset.put(Integer.valueOf(collationIndex), getJavaEncodingForMysqlEncoding(charsetName));
/*      */ 
/* 1015 */             if ((collationIndex >= 255) || (!charsetName.equals(CharsetMapping.STATIC_INDEX_TO_MYSQL_CHARSET_MAP.get(Integer.valueOf(collationIndex)))))
/*      */             {
/* 1017 */               customCharset.put(Integer.valueOf(collationIndex), charsetName);
/*      */             }
/*      */ 
/* 1021 */             if ((!CharsetMapping.STATIC_CHARSET_TO_NUM_BYTES_MAP.containsKey(charsetName)) && (!CharsetMapping.STATIC_4_0_CHARSET_TO_NUM_BYTES_MAP.containsKey(charsetName)))
/*      */             {
/* 1023 */               customMblen.put(charsetName, null);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1028 */           if (customMblen.size() > 0) {
/*      */             try {
/* 1030 */               results = stmt.executeQuery("SHOW CHARACTER SET");
/* 1031 */               while (results.next()) {
/* 1032 */                 String charsetName = results.getString("Charset");
/* 1033 */                 if (customMblen.containsKey(charsetName))
/* 1034 */                   customMblen.put(charsetName, Integer.valueOf(results.getInt("Maxlen")));
/*      */               }
/*      */             }
/*      */             catch (SQLException ex) {
/* 1038 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1039 */                 throw ex;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1044 */           if (getCacheServerConfiguration()) {
/* 1045 */             synchronized (serverCollationByUrl) {
/* 1046 */               serverCollationByUrl.put(getURL(), sortedCollationMap);
/* 1047 */               serverJavaCharsetByUrl.put(getURL(), javaCharset);
/* 1048 */               serverCustomCharsetByUrl.put(getURL(), customCharset);
/* 1049 */               serverCustomMblenByUrl.put(getURL(), customMblen);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1055 */         this.indexToJavaCharset = Collections.unmodifiableMap(javaCharset);
/* 1056 */         this.indexToCustomMysqlCharset = Collections.unmodifiableMap(customCharset);
/* 1057 */         this.mysqlCharsetToCustomMblen = Collections.unmodifiableMap(customMblen);
/*      */       }
/*      */       catch (SQLException ex) {
/* 1060 */         throw ex;
/*      */       } catch (RuntimeException ex) {
/* 1062 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1063 */         sqlEx.initCause(ex);
/* 1064 */         throw sqlEx;
/*      */       } finally {
/* 1066 */         if (results != null) {
/*      */           try {
/* 1068 */             results.close();
/*      */           }
/*      */           catch (SQLException sqlE)
/*      */           {
/*      */           }
/*      */         }
/* 1074 */         if (stmt != null)
/*      */           try {
/* 1076 */             stmt.close();
/*      */           }
/*      */           catch (SQLException sqlE) {
/*      */           }
/*      */       }
/*      */     }
/*      */     else {
/* 1083 */       javaCharset = new HashMap();
/* 1084 */       for (int i = 0; i < CharsetMapping.INDEX_TO_CHARSET.length; i++) {
/* 1085 */         javaCharset.put(Integer.valueOf(i), CharsetMapping.INDEX_TO_CHARSET[i]);
/*      */       }
/* 1087 */       this.indexToJavaCharset = Collections.unmodifiableMap(javaCharset);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getJavaEncodingForMysqlEncoding(String mysqlEncoding) throws SQLException
/*      */   {
/* 1093 */     if ((versionMeetsMinimum(4, 1, 0)) && ("latin1".equalsIgnoreCase(mysqlEncoding))) {
/* 1094 */       return "Cp1252";
/*      */     }
/*      */ 
/* 1097 */     return (String)CharsetMapping.MYSQL_TO_JAVA_CHARSET_MAP.get(mysqlEncoding);
/*      */   }
/*      */ 
/*      */   private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException
/*      */   {
/* 1102 */     if ((sql == null) || (sql.length() == 0)) {
/* 1103 */       return true;
/*      */     }
/*      */ 
/* 1106 */     if (!this.useServerPreparedStmts) {
/* 1107 */       return false;
/*      */     }
/*      */ 
/* 1110 */     if (getCachePreparedStatements()) {
/* 1111 */       synchronized (this.serverSideStatementCheckCache) {
/* 1112 */         Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
/*      */ 
/* 1114 */         if (flag != null) {
/* 1115 */           return flag.booleanValue();
/*      */         }
/*      */ 
/* 1118 */         boolean canHandle = canHandleAsServerPreparedStatementNoCache(sql);
/*      */ 
/* 1120 */         if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 1121 */           this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE);
/*      */         }
/*      */ 
/* 1125 */         return canHandle;
/*      */       }
/*      */     }
/*      */ 
/* 1129 */     return canHandleAsServerPreparedStatementNoCache(sql);
/*      */   }
/*      */ 
/*      */   private boolean canHandleAsServerPreparedStatementNoCache(String sql)
/*      */     throws SQLException
/*      */   {
/* 1136 */     if (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL")) {
/* 1137 */       return false;
/*      */     }
/*      */ 
/* 1140 */     boolean canHandleAsStatement = true;
/*      */ 
/* 1142 */     if ((!versionMeetsMinimum(5, 0, 7)) && ((StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "SELECT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "REPLACE"))))
/*      */     {
/* 1160 */       int currentPos = 0;
/* 1161 */       int statementLength = sql.length();
/* 1162 */       int lastPosToLook = statementLength - 7;
/* 1163 */       boolean allowBackslashEscapes = !this.noBackslashEscapes;
/* 1164 */       char quoteChar = this.useAnsiQuotes ? '"' : '\'';
/* 1165 */       boolean foundLimitWithPlaceholder = false;
/*      */ 
/* 1167 */       while (currentPos < lastPosToLook) {
/* 1168 */         int limitStart = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, sql, "LIMIT ", quoteChar, allowBackslashEscapes);
/*      */ 
/* 1172 */         if (limitStart == -1)
/*      */         {
/*      */           break;
/*      */         }
/* 1176 */         currentPos = limitStart + 7;
/*      */ 
/* 1178 */         while (currentPos < statementLength) {
/* 1179 */           char c = sql.charAt(currentPos);
/*      */ 
/* 1186 */           if ((!Character.isDigit(c)) && (!Character.isWhitespace(c)) && (c != ',') && (c != '?'))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1191 */           if (c == '?') {
/* 1192 */             foundLimitWithPlaceholder = true;
/* 1193 */             break;
/*      */           }
/*      */ 
/* 1196 */           currentPos++;
/*      */         }
/*      */       }
/*      */ 
/* 1200 */       canHandleAsStatement = !foundLimitWithPlaceholder;
/* 1201 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
/* 1202 */       canHandleAsStatement = false;
/* 1203 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "DO")) {
/* 1204 */       canHandleAsStatement = false;
/* 1205 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "SET")) {
/* 1206 */       canHandleAsStatement = false;
/*      */     }
/*      */ 
/* 1211 */     return canHandleAsStatement;
/*      */   }
/*      */ 
/*      */   public synchronized void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/* 1229 */     checkClosed();
/*      */ 
/* 1231 */     if ((userName == null) || (userName.equals(""))) {
/* 1232 */       userName = "";
/*      */     }
/*      */ 
/* 1235 */     if (newPassword == null) {
/* 1236 */       newPassword = "";
/*      */     }
/*      */ 
/* 1239 */     this.io.changeUser(userName, newPassword, this.database);
/* 1240 */     this.user = userName;
/* 1241 */     this.password = newPassword;
/*      */ 
/* 1243 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 1244 */       configureClientCharacterSet(true);
/*      */     }
/*      */ 
/* 1247 */     setSessionVariables();
/*      */ 
/* 1249 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */   private boolean characterSetNamesMatches(String mysqlEncodingName)
/*      */   {
/* 1255 */     return (mysqlEncodingName != null) && (mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_client"))) && (mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_connection")));
/*      */   }
/*      */ 
/*      */   private void checkAndCreatePerformanceHistogram()
/*      */   {
/* 1261 */     if (this.perfMetricsHistCounts == null) {
/* 1262 */       this.perfMetricsHistCounts = new int[20];
/*      */     }
/*      */ 
/* 1265 */     if (this.perfMetricsHistBreakpoints == null)
/* 1266 */       this.perfMetricsHistBreakpoints = new long[20];
/*      */   }
/*      */ 
/*      */   private void checkAndCreateTablesAccessedHistogram()
/*      */   {
/* 1271 */     if (this.numTablesMetricsHistCounts == null) {
/* 1272 */       this.numTablesMetricsHistCounts = new int[20];
/*      */     }
/*      */ 
/* 1275 */     if (this.numTablesMetricsHistBreakpoints == null)
/* 1276 */       this.numTablesMetricsHistBreakpoints = new long[20];
/*      */   }
/*      */ 
/*      */   public void checkClosed() throws SQLException
/*      */   {
/* 1281 */     if (this.isClosed)
/* 1282 */       throwConnectionClosedException();
/*      */   }
/*      */ 
/*      */   public void throwConnectionClosedException() throws SQLException
/*      */   {
/* 1287 */     StringBuffer messageBuf = new StringBuffer("No operations allowed after connection closed.");
/*      */ 
/* 1290 */     SQLException ex = SQLError.createSQLException(messageBuf.toString(), "08003", getExceptionInterceptor());
/*      */ 
/* 1293 */     if (this.forceClosedReason != null) {
/* 1294 */       ex.initCause(this.forceClosedReason);
/*      */     }
/*      */ 
/* 1297 */     throw ex;
/*      */   }
/*      */ 
/*      */   private void checkServerEncoding()
/*      */     throws SQLException
/*      */   {
/* 1308 */     if ((getUseUnicode()) && (getEncoding() != null))
/*      */     {
/* 1310 */       return;
/*      */     }
/*      */ 
/* 1313 */     String serverEncoding = (String)this.serverVariables.get("character_set");
/*      */ 
/* 1315 */     if (serverEncoding == null)
/*      */     {
/* 1317 */       serverEncoding = (String)this.serverVariables.get("character_set_server");
/*      */     }
/*      */ 
/* 1320 */     String mappedServerEncoding = null;
/*      */ 
/* 1322 */     if (serverEncoding != null) {
/*      */       try {
/* 1324 */         mappedServerEncoding = getJavaEncodingForMysqlEncoding(serverEncoding.toUpperCase(Locale.ENGLISH));
/*      */       }
/*      */       catch (SQLException ex) {
/* 1327 */         throw ex;
/*      */       } catch (RuntimeException ex) {
/* 1329 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1330 */         sqlEx.initCause(ex);
/* 1331 */         throw sqlEx;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1338 */     if ((!getUseUnicode()) && (mappedServerEncoding != null)) {
/* 1339 */       SingleByteCharsetConverter converter = getCharsetConverter(mappedServerEncoding);
/*      */ 
/* 1341 */       if (converter != null) {
/* 1342 */         setUseUnicode(true);
/* 1343 */         setEncoding(mappedServerEncoding);
/*      */ 
/* 1345 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1353 */     if (serverEncoding != null) {
/* 1354 */       if (mappedServerEncoding == null)
/*      */       {
/* 1357 */         if (Character.isLowerCase(serverEncoding.charAt(0))) {
/* 1358 */           char[] ach = serverEncoding.toCharArray();
/* 1359 */           ach[0] = Character.toUpperCase(serverEncoding.charAt(0));
/* 1360 */           setEncoding(new String(ach));
/*      */         }
/*      */       }
/*      */ 
/* 1364 */       if (mappedServerEncoding == null) {
/* 1365 */         throw SQLError.createSQLException("Unknown character encoding on server '" + serverEncoding + "', use 'characterEncoding=' property " + " to provide correct mapping", "01S00", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1377 */         StringUtils.getBytes("abc", mappedServerEncoding);
/* 1378 */         setEncoding(mappedServerEncoding);
/* 1379 */         setUseUnicode(true);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 1381 */         throw SQLError.createSQLException("The driver can not map the character encoding '" + getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You " + "can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" " + "to your JDBC URL.", "0S100", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkTransactionIsolationLevel()
/*      */     throws SQLException
/*      */   {
/* 1401 */     String txIsolationName = null;
/*      */ 
/* 1403 */     if (versionMeetsMinimum(4, 0, 3))
/* 1404 */       txIsolationName = "tx_isolation";
/*      */     else {
/* 1406 */       txIsolationName = "transaction_isolation";
/*      */     }
/*      */ 
/* 1409 */     String s = (String)this.serverVariables.get(txIsolationName);
/*      */ 
/* 1411 */     if (s != null) {
/* 1412 */       Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/* 1414 */       if (intTI != null)
/* 1415 */         this.isolationLevel = intTI.intValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abortInternal()
/*      */     throws SQLException
/*      */   {
/* 1427 */     if (this.io != null) {
/*      */       try {
/* 1429 */         this.io.forceClose();
/*      */       }
/*      */       catch (Throwable t) {
/*      */       }
/* 1433 */       this.io = null;
/*      */     }
/*      */ 
/* 1436 */     this.isClosed = true;
/*      */   }
/*      */ 
/*      */   private void cleanup(Throwable whyCleanedUp)
/*      */   {
/*      */     try
/*      */     {
/* 1449 */       if ((this.io != null) && (!isClosed()))
/* 1450 */         realClose(false, false, false, whyCleanedUp);
/* 1451 */       else if (this.io != null) {
/* 1452 */         this.io.forceClose();
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*      */     }
/*      */ 
/* 1459 */     this.isClosed = true;
/*      */   }
/*      */ 
/*      */   public void clearHasTriedMaster() {
/* 1463 */     this.hasTriedMasterFlag = false;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 1488 */     return clientPrepareStatement(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 1498 */     java.sql.PreparedStatement pStmt = clientPrepareStatement(sql);
/*      */ 
/* 1500 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 1503 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 1521 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded)
/*      */     throws SQLException
/*      */   {
/* 1529 */     checkClosed();
/*      */ 
/* 1531 */     String nativeSql = (processEscapeCodesIfNeeded) && (getProcessEscapeCodesForPrepStmts()) ? nativeSQL(sql) : sql;
/*      */ 
/* 1533 */     PreparedStatement pStmt = null;
/*      */ 
/* 1535 */     if (getCachePreparedStatements()) {
/* 1536 */       PreparedStatement.ParseInfo pStmtInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(nativeSql);
/*      */ 
/* 1538 */       if (pStmtInfo == null) {
/* 1539 */         pStmt = PreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, this.database);
/*      */ 
/* 1542 */         this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
/*      */       }
/*      */       else {
/* 1545 */         pStmt = new PreparedStatement(getLoadBalanceSafeProxy(), nativeSql, this.database, pStmtInfo);
/*      */       }
/*      */     }
/*      */     else {
/* 1549 */       pStmt = PreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, this.database);
/*      */     }
/*      */ 
/* 1553 */     pStmt.setResultSetType(resultSetType);
/* 1554 */     pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 1556 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 1565 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */ 
/* 1567 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 1571 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 1579 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */ 
/* 1581 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 1585 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 1591 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/* 1607 */     if (this.connectionLifecycleInterceptors != null) {
/* 1608 */       new IterateBlock(this.connectionLifecycleInterceptors.iterator()) {
/*      */         void forEach(Extension each) throws SQLException {
/* 1610 */           ((ConnectionLifecycleInterceptor)each).close();
/*      */         }
/*      */       }
/* 1608 */       .doForAll();
/*      */     }
/*      */ 
/* 1615 */     realClose(true, true, false, null);
/*      */   }
/*      */ 
/*      */   private void closeAllOpenStatements()
/*      */     throws SQLException
/*      */   {
/* 1625 */     SQLException postponedException = null;
/*      */ 
/* 1627 */     if (this.openStatements != null) {
/* 1628 */       List currentlyOpenStatements = new ArrayList();
/*      */ 
/* 1632 */       for (Iterator iter = this.openStatements.keySet().iterator(); iter.hasNext(); ) {
/* 1633 */         currentlyOpenStatements.add(iter.next());
/*      */       }
/*      */ 
/* 1636 */       int numStmts = currentlyOpenStatements.size();
/*      */ 
/* 1638 */       for (int i = 0; i < numStmts; i++) {
/* 1639 */         StatementImpl stmt = (StatementImpl)currentlyOpenStatements.get(i);
/*      */         try
/*      */         {
/* 1642 */           stmt.realClose(false, true);
/*      */         } catch (SQLException sqlEx) {
/* 1644 */           postponedException = sqlEx;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1649 */       if (postponedException != null)
/* 1650 */         throw postponedException;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void closeStatement(java.sql.Statement stmt)
/*      */   {
/* 1656 */     if (stmt != null) {
/*      */       try {
/* 1658 */         stmt.close();
/*      */       }
/*      */       catch (SQLException sqlEx)
/*      */       {
/*      */       }
/* 1663 */       stmt = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void commit()
/*      */     throws SQLException
/*      */   {
/* 1682 */     checkClosed();
/*      */     try
/*      */     {
/* 1685 */       if (this.connectionLifecycleInterceptors != null) {
/* 1686 */         IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException {
/* 1689 */             if (!((ConnectionLifecycleInterceptor)each).commit())
/* 1690 */               this.stopIterating = true;
/*      */           }
/*      */         };
/* 1695 */         iter.doForAll();
/*      */ 
/* 1697 */         if (!iter.fullIteration()) {
/* 1698 */           jsr 132;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1703 */       if ((this.autoCommit) && (!getRelaxAutoCommit()))
/* 1704 */         throw SQLError.createSQLException("Can't call commit when autocommit=true", getExceptionInterceptor());
/* 1705 */       if (this.transactionsSupported) {
/* 1706 */         if ((getUseLocalTransactionState()) && (versionMeetsMinimum(5, 0, 0)) && 
/* 1707 */           (!this.io.inTransactionOnServer())) {
/* 1708 */           jsr 69;
/*      */         }
/*      */ 
/* 1712 */         execSQL(null, "commit", -1, null, 1003, 1007, false, this.database, null, false);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 1719 */       if ("08S01".equals(sqlException.getSQLState()))
/*      */       {
/* 1721 */         throw SQLError.createSQLException("Communications link failure during commit(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 1728 */       throw sqlException;
/*      */     } finally {
/* 1730 */       this.needsPing = getReconnectAtTxEnd();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void configureCharsetProperties()
/*      */     throws SQLException
/*      */   {
/* 1743 */     if (getEncoding() != null)
/*      */     {
/*      */       try
/*      */       {
/* 1747 */         String testString = "abc";
/* 1748 */         StringUtils.getBytes(testString, getEncoding());
/*      */       }
/*      */       catch (UnsupportedEncodingException UE) {
/* 1751 */         String oldEncoding = getEncoding();
/*      */         try
/*      */         {
/* 1754 */           setEncoding(getJavaEncodingForMysqlEncoding(oldEncoding));
/*      */         } catch (SQLException ex) {
/* 1756 */           throw ex;
/*      */         } catch (RuntimeException ex) {
/* 1758 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1759 */           sqlEx.initCause(ex);
/* 1760 */           throw sqlEx;
/*      */         }
/*      */ 
/* 1763 */         if (getEncoding() == null) {
/* 1764 */           throw SQLError.createSQLException("Java does not support the MySQL character encoding  encoding '" + oldEncoding + "'.", "01S00", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1771 */           String testString = "abc";
/* 1772 */           StringUtils.getBytes(testString, getEncoding());
/*      */         } catch (UnsupportedEncodingException encodingEx) {
/* 1774 */           throw SQLError.createSQLException("Unsupported character encoding '" + getEncoding() + "'.", "01S00", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean configureClientCharacterSet(boolean dontCheckServerMatch)
/*      */     throws SQLException
/*      */   {
/* 1796 */     String realJavaEncoding = getEncoding();
/* 1797 */     boolean characterSetAlreadyConfigured = false;
/*      */     try
/*      */     {
/* 1800 */       if (versionMeetsMinimum(4, 1, 0)) {
/* 1801 */         characterSetAlreadyConfigured = true;
/*      */ 
/* 1803 */         setUseUnicode(true);
/*      */ 
/* 1805 */         configureCharsetProperties();
/* 1806 */         realJavaEncoding = getEncoding();
/*      */         try
/*      */         {
/* 1814 */           if ((this.props != null) && (this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex") != null)) {
/* 1815 */             this.io.serverCharsetIndex = Integer.parseInt(this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex"));
/*      */           }
/*      */ 
/* 1820 */           String serverEncodingToSet = CharsetMapping.INDEX_TO_CHARSET[this.io.serverCharsetIndex];
/*      */ 
/* 1823 */           if ((serverEncodingToSet == null) || (serverEncodingToSet.length() == 0)) {
/* 1824 */             if (realJavaEncoding != null)
/*      */             {
/* 1826 */               setEncoding(realJavaEncoding);
/*      */             }
/* 1828 */             else throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000", getExceptionInterceptor());
/*      */ 
/*      */           }
/*      */ 
/* 1837 */           if ((versionMeetsMinimum(4, 1, 0)) && ("ISO8859_1".equalsIgnoreCase(serverEncodingToSet)))
/*      */           {
/* 1839 */             serverEncodingToSet = "Cp1252";
/*      */           }
/* 1841 */           if (("UnicodeBig".equalsIgnoreCase(serverEncodingToSet)) || ("UTF-16".equalsIgnoreCase(serverEncodingToSet)) || ("UTF-16LE".equalsIgnoreCase(serverEncodingToSet)) || ("UTF-32".equalsIgnoreCase(serverEncodingToSet)))
/*      */           {
/* 1846 */             serverEncodingToSet = "UTF-8";
/*      */           }
/*      */ 
/* 1849 */           setEncoding(serverEncodingToSet);
/*      */         }
/*      */         catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 1852 */           if (realJavaEncoding != null)
/*      */           {
/* 1854 */             setEncoding(realJavaEncoding);
/*      */           }
/* 1856 */           else throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000", getExceptionInterceptor());
/*      */ 
/*      */         }
/*      */         catch (SQLException ex)
/*      */         {
/* 1863 */           throw ex;
/*      */         } catch (RuntimeException ex) {
/* 1865 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1866 */           sqlEx.initCause(ex);
/* 1867 */           throw sqlEx;
/*      */         }
/*      */ 
/* 1870 */         if (getEncoding() == null)
/*      */         {
/* 1872 */           setEncoding("ISO8859_1");
/*      */         }
/*      */ 
/* 1879 */         if (getUseUnicode()) {
/* 1880 */           if (realJavaEncoding != null)
/*      */           {
/* 1886 */             if ((realJavaEncoding.equalsIgnoreCase("UTF-8")) || (realJavaEncoding.equalsIgnoreCase("UTF8")))
/*      */             {
/* 1890 */               boolean utf8mb4Supported = versionMeetsMinimum(5, 5, 2);
/* 1891 */               boolean useutf8mb4 = false;
/*      */ 
/* 1893 */               if (utf8mb4Supported) {
/* 1894 */                 useutf8mb4 = this.io.serverCharsetIndex == 45;
/*      */               }
/*      */ 
/* 1897 */               if (!getUseOldUTF8Behavior()) {
/* 1898 */                 if ((dontCheckServerMatch) || (!characterSetNamesMatches("utf8")) || ((utf8mb4Supported) && (!characterSetNamesMatches("utf8mb4"))))
/*      */                 {
/* 1900 */                   execSQL(null, "SET NAMES " + (useutf8mb4 ? "utf8mb4" : "utf8"), -1, null, 1003, 1007, false, this.database, null, false);
/*      */                 }
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/* 1906 */                 execSQL(null, "SET NAMES latin1", -1, null, 1003, 1007, false, this.database, null, false);
/*      */               }
/*      */ 
/* 1912 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 1914 */               String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(realJavaEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 1929 */               if (mysqlEncodingName != null)
/*      */               {
/* 1931 */                 if ((dontCheckServerMatch) || (!characterSetNamesMatches(mysqlEncodingName))) {
/* 1932 */                   execSQL(null, "SET NAMES " + mysqlEncodingName, -1, null, 1003, 1007, false, this.database, null, false);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 1943 */               setEncoding(realJavaEncoding);
/*      */             }
/* 1945 */           } else if (getEncoding() != null)
/*      */           {
/* 1949 */             String mysqlEncodingName = getServerCharacterEncoding();
/*      */ 
/* 1951 */             if (getUseOldUTF8Behavior()) {
/* 1952 */               mysqlEncodingName = "latin1";
/*      */             }
/*      */ 
/* 1955 */             boolean ucs2 = false;
/* 1956 */             if (("ucs2".equalsIgnoreCase(mysqlEncodingName)) || ("utf16".equalsIgnoreCase(mysqlEncodingName)) || ("utf16le".equalsIgnoreCase(mysqlEncodingName)) || ("utf32".equalsIgnoreCase(mysqlEncodingName)))
/*      */             {
/* 1960 */               mysqlEncodingName = "utf8";
/* 1961 */               ucs2 = true;
/* 1962 */               if (getCharacterSetResults() == null) {
/* 1963 */                 setCharacterSetResults("UTF-8");
/*      */               }
/*      */             }
/*      */ 
/* 1967 */             if ((dontCheckServerMatch) || (!characterSetNamesMatches(mysqlEncodingName)) || (ucs2)) {
/*      */               try {
/* 1969 */                 execSQL(null, "SET NAMES " + mysqlEncodingName, -1, null, 1003, 1007, false, this.database, null, false);
/*      */               }
/*      */               catch (SQLException ex)
/*      */               {
/* 1974 */                 if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1975 */                   throw ex;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 1980 */             realJavaEncoding = getEncoding();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1991 */         String onServer = null;
/* 1992 */         boolean isNullOnServer = false;
/*      */ 
/* 1994 */         if (this.serverVariables != null) {
/* 1995 */           onServer = (String)this.serverVariables.get("character_set_results");
/*      */ 
/* 1997 */           isNullOnServer = (onServer == null) || ("NULL".equalsIgnoreCase(onServer)) || (onServer.length() == 0);
/*      */         }
/*      */ 
/* 2000 */         if (getCharacterSetResults() == null)
/*      */         {
/* 2007 */           if (!isNullOnServer) {
/*      */             try {
/* 2009 */               execSQL(null, "SET character_set_results = NULL", -1, null, 1003, 1007, false, this.database, null, false);
/*      */             }
/*      */             catch (SQLException ex)
/*      */             {
/* 2015 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 2016 */                 throw ex;
/*      */               }
/*      */             }
/* 2019 */             if (!this.usingCachedConfig) {
/* 2020 */               this.serverVariables.put("jdbc.local.character_set_results", null);
/*      */             }
/*      */           }
/* 2023 */           else if (!this.usingCachedConfig) {
/* 2024 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 2029 */           if (getUseOldUTF8Behavior()) {
/*      */             try {
/* 2031 */               execSQL(null, "SET NAMES latin1", -1, null, 1003, 1007, false, this.database, null, false);
/*      */             }
/*      */             catch (SQLException ex)
/*      */             {
/* 2036 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 2037 */                 throw ex;
/*      */               }
/*      */             }
/*      */           }
/* 2041 */           String charsetResults = getCharacterSetResults();
/* 2042 */           String mysqlEncodingName = null;
/*      */ 
/* 2044 */           if (("UTF-8".equalsIgnoreCase(charsetResults)) || ("UTF8".equalsIgnoreCase(charsetResults)))
/*      */           {
/* 2046 */             mysqlEncodingName = "utf8";
/* 2047 */           } else if ("null".equalsIgnoreCase(charsetResults))
/* 2048 */             mysqlEncodingName = "NULL";
/*      */           else {
/* 2050 */             mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(charsetResults.toUpperCase(Locale.ENGLISH), this);
/*      */           }
/*      */ 
/* 2059 */           if (mysqlEncodingName == null) {
/* 2060 */             throw SQLError.createSQLException("Can't map " + charsetResults + " given for characterSetResults to a supported MySQL encoding.", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 2065 */           if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_results")))
/*      */           {
/* 2067 */             StringBuffer setBuf = new StringBuffer("SET character_set_results = ".length() + mysqlEncodingName.length());
/*      */ 
/* 2070 */             setBuf.append("SET character_set_results = ").append(mysqlEncodingName);
/*      */             try
/*      */             {
/* 2074 */               execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */             }
/*      */             catch (SQLException ex)
/*      */             {
/* 2079 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 2080 */                 throw ex;
/*      */               }
/*      */             }
/*      */ 
/* 2084 */             if (!this.usingCachedConfig) {
/* 2085 */               this.serverVariables.put("jdbc.local.character_set_results", mysqlEncodingName);
/*      */             }
/*      */ 
/* 2091 */             if (versionMeetsMinimum(5, 5, 0)) {
/* 2092 */               this.errorMessageEncoding = charsetResults;
/*      */             }
/*      */ 
/*      */           }
/* 2096 */           else if (!this.usingCachedConfig) {
/* 2097 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2102 */         if (getConnectionCollation() != null) {
/* 2103 */           StringBuffer setBuf = new StringBuffer("SET collation_connection = ".length() + getConnectionCollation().length());
/*      */ 
/* 2106 */           setBuf.append("SET collation_connection = ").append(getConnectionCollation());
/*      */           try
/*      */           {
/* 2110 */             execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */           }
/*      */           catch (SQLException ex)
/*      */           {
/* 2115 */             if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords()))
/* 2116 */               throw ex;
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 2122 */         realJavaEncoding = getEncoding();
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 2130 */       setEncoding(realJavaEncoding);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 2138 */       CharsetEncoder enc = Charset.forName(getEncoding()).newEncoder();
/* 2139 */       CharBuffer cbuf = CharBuffer.allocate(1);
/* 2140 */       ByteBuffer bbuf = ByteBuffer.allocate(1);
/*      */ 
/* 2142 */       cbuf.put("¥");
/* 2143 */       cbuf.position(0);
/* 2144 */       enc.encode(cbuf, bbuf, true);
/* 2145 */       if (bbuf.get(0) == 92) {
/* 2146 */         this.requiresEscapingEncoder = true;
/*      */       } else {
/* 2148 */         cbuf.clear();
/* 2149 */         bbuf.clear();
/*      */ 
/* 2151 */         cbuf.put("₩");
/* 2152 */         cbuf.position(0);
/* 2153 */         enc.encode(cbuf, bbuf, true);
/* 2154 */         if (bbuf.get(0) == 92)
/* 2155 */           this.requiresEscapingEncoder = true;
/*      */       }
/*      */     }
/*      */     catch (UnsupportedCharsetException ucex)
/*      */     {
/*      */       try {
/* 2161 */         byte[] bbuf = StringUtils.getBytes("¥", getEncoding());
/* 2162 */         if (bbuf[0] == 92) {
/* 2163 */           this.requiresEscapingEncoder = true;
/*      */         } else {
/* 2165 */           bbuf = StringUtils.getBytes("₩", getEncoding());
/* 2166 */           if (bbuf[0] == 92)
/* 2167 */             this.requiresEscapingEncoder = true;
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException ueex) {
/* 2171 */         throw SQLError.createSQLException("Unable to use encoding: " + getEncoding(), "S1000", ueex, getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2177 */     return characterSetAlreadyConfigured;
/*      */   }
/*      */ 
/*      */   private void configureTimezone()
/*      */     throws SQLException
/*      */   {
/* 2188 */     String configuredTimeZoneOnServer = (String)this.serverVariables.get("timezone");
/*      */ 
/* 2190 */     if (configuredTimeZoneOnServer == null) {
/* 2191 */       configuredTimeZoneOnServer = (String)this.serverVariables.get("time_zone");
/*      */ 
/* 2193 */       if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
/* 2194 */         configuredTimeZoneOnServer = (String)this.serverVariables.get("system_time_zone");
/*      */       }
/*      */     }
/*      */ 
/* 2198 */     String canoncicalTimezone = getServerTimezone();
/*      */ 
/* 2200 */     if (((getUseTimezone()) || (!getUseLegacyDatetimeCode())) && (configuredTimeZoneOnServer != null))
/*      */     {
/* 2202 */       if ((canoncicalTimezone == null) || (StringUtils.isEmptyOrWhitespaceOnly(canoncicalTimezone))) {
/*      */         try {
/* 2204 */           canoncicalTimezone = TimeUtil.getCanoncialTimezone(configuredTimeZoneOnServer, getExceptionInterceptor());
/*      */ 
/* 2207 */           if (canoncicalTimezone == null) {
/* 2208 */             throw SQLError.createSQLException("Can't map timezone '" + configuredTimeZoneOnServer + "' to " + " canonical timezone.", "S1009", getExceptionInterceptor());
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IllegalArgumentException iae)
/*      */         {
/* 2214 */           throw SQLError.createSQLException(iae.getMessage(), "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2219 */       canoncicalTimezone = getServerTimezone();
/*      */     }
/*      */ 
/* 2222 */     if ((canoncicalTimezone != null) && (canoncicalTimezone.length() > 0)) {
/* 2223 */       this.serverTimezoneTZ = TimeZone.getTimeZone(canoncicalTimezone);
/*      */ 
/* 2230 */       if ((!canoncicalTimezone.equalsIgnoreCase("GMT")) && (this.serverTimezoneTZ.getID().equals("GMT")))
/*      */       {
/* 2232 */         throw SQLError.createSQLException("No timezone mapping entry for '" + canoncicalTimezone + "'", "S1009", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2237 */       if ("GMT".equalsIgnoreCase(this.serverTimezoneTZ.getID()))
/* 2238 */         this.isServerTzUTC = true;
/*      */       else
/* 2240 */         this.isServerTzUTC = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound)
/*      */   {
/* 2248 */     double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
/*      */ 
/* 2250 */     if (bucketSize < 1.0D) {
/* 2251 */       bucketSize = 1.0D;
/*      */     }
/*      */ 
/* 2254 */     for (int i = 0; i < 20; i++) {
/* 2255 */       breakpoints[i] = lowerBound;
/* 2256 */       lowerBound = ()(lowerBound + bucketSize);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void createNewIO(boolean isForReconnect)
/*      */     throws SQLException
/*      */   {
/* 2280 */     Properties mergedProps = exposeAsProperties(this.props);
/*      */ 
/* 2282 */     if (!getHighAvailability()) {
/* 2283 */       connectOneTryOnly(isForReconnect, mergedProps);
/*      */ 
/* 2285 */       return;
/*      */     }
/*      */ 
/* 2288 */     connectWithRetries(isForReconnect, mergedProps);
/*      */   }
/*      */ 
/*      */   private void connectWithRetries(boolean isForReconnect, Properties mergedProps)
/*      */     throws SQLException
/*      */   {
/* 2294 */     double timeout = getInitialTimeout();
/* 2295 */     boolean connectionGood = false;
/*      */ 
/* 2297 */     Exception connectionException = null;
/*      */ 
/* 2299 */     int attemptCount = 0;
/*      */     while (true) if ((attemptCount < getMaxReconnects()) && (!connectionGood)) {
/*      */         try {
/* 2302 */           if (this.io != null) {
/* 2303 */             this.io.forceClose();
/*      */           }
/*      */ 
/* 2306 */           coreConnect(mergedProps);
/* 2307 */           pingInternal(false, 0);
/*      */           boolean oldAutoCommit;
/*      */           int oldIsolationLevel;
/*      */           boolean oldReadOnly;
/*      */           String oldCatalog;
/* 2314 */           synchronized (this) {
/* 2315 */             this.connectionId = this.io.getThreadId();
/* 2316 */             this.isClosed = false;
/*      */ 
/* 2319 */             oldAutoCommit = getAutoCommit();
/* 2320 */             oldIsolationLevel = this.isolationLevel;
/* 2321 */             oldReadOnly = isReadOnly(false);
/* 2322 */             oldCatalog = getCatalog();
/*      */ 
/* 2324 */             this.io.setStatementInterceptors(this.statementInterceptors);
/*      */           }
/*      */ 
/* 2330 */           initializePropsFromServer();
/*      */ 
/* 2332 */           if (isForReconnect)
/*      */           {
/* 2334 */             setAutoCommit(oldAutoCommit);
/*      */ 
/* 2336 */             if (this.hasIsolationLevels) {
/* 2337 */               setTransactionIsolation(oldIsolationLevel);
/*      */             }
/*      */ 
/* 2340 */             setCatalog(oldCatalog);
/* 2341 */             setReadOnly(oldReadOnly);
/*      */           }
/*      */ 
/* 2344 */           connectionGood = true;
/*      */         }
/*      */         catch (Exception IE)
/*      */         {
/* 2348 */           connectionException = EEE;
/* 2349 */           connectionGood = false;
/*      */ 
/* 2352 */           if (!connectionGood)
/*      */           {
/* 2356 */             if (attemptCount > 0)
/*      */               try {
/* 2358 */                 Thread.sleep(()timeout * 1000L);
/*      */               }
/*      */               catch (InterruptedException IE)
/*      */               {
/*      */               }
/* 2300 */             attemptCount++; continue;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */ 
/* 2365 */     if (!connectionGood)
/*      */     {
/* 2367 */       SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnectWithRetries", new Object[] { Integer.valueOf(getMaxReconnects()) }), "08001", getExceptionInterceptor());
/*      */ 
/* 2371 */       chainedEx.initCause(connectionException);
/*      */ 
/* 2373 */       throw chainedEx;
/*      */     }
/*      */ 
/* 2376 */     if ((getParanoid()) && (!getHighAvailability())) {
/* 2377 */       this.password = null;
/* 2378 */       this.user = null;
/*      */     }
/*      */ 
/* 2381 */     if (isForReconnect)
/*      */     {
/* 2385 */       Iterator statementIter = this.openStatements.values().iterator();
/*      */ 
/* 2396 */       Stack serverPreparedStatements = null;
/*      */ 
/* 2398 */       while (statementIter.hasNext()) {
/* 2399 */         Statement statementObj = (Statement)statementIter.next();
/*      */ 
/* 2401 */         if ((statementObj instanceof ServerPreparedStatement)) {
/* 2402 */           if (serverPreparedStatements == null) {
/* 2403 */             serverPreparedStatements = new Stack();
/*      */           }
/*      */ 
/* 2406 */           serverPreparedStatements.add(statementObj);
/*      */         }
/*      */       }
/*      */ 
/* 2410 */       if (serverPreparedStatements != null)
/* 2411 */         while (!serverPreparedStatements.isEmpty())
/* 2412 */           ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void coreConnect(Properties mergedProps)
/*      */     throws SQLException, IOException
/*      */   {
/* 2421 */     int newPort = 3306;
/* 2422 */     String newHost = "localhost";
/*      */ 
/* 2424 */     String protocol = mergedProps.getProperty("PROTOCOL");
/*      */ 
/* 2426 */     if (protocol != null)
/*      */     {
/* 2429 */       if ("tcp".equalsIgnoreCase(protocol)) {
/* 2430 */         newHost = normalizeHost(mergedProps.getProperty("HOST"));
/* 2431 */         newPort = parsePortNumber(mergedProps.getProperty("PORT", "3306"));
/* 2432 */       } else if ("pipe".equalsIgnoreCase(protocol)) {
/* 2433 */         setSocketFactoryClassName(NamedPipeSocketFactory.class.getName());
/*      */ 
/* 2435 */         String path = mergedProps.getProperty("PATH");
/*      */ 
/* 2437 */         if (path != null)
/* 2438 */           mergedProps.setProperty("namedPipePath", path);
/*      */       }
/*      */       else
/*      */       {
/* 2442 */         newHost = normalizeHost(mergedProps.getProperty("HOST"));
/* 2443 */         newPort = parsePortNumber(mergedProps.getProperty("PORT", "3306"));
/*      */       }
/*      */     }
/*      */     else {
/* 2447 */       String[] parsedHostPortPair = NonRegisteringDriver.parseHostPortPair(this.hostPortPair);
/*      */ 
/* 2449 */       newHost = parsedHostPortPair[0];
/*      */ 
/* 2451 */       newHost = normalizeHost(newHost);
/*      */ 
/* 2453 */       if (parsedHostPortPair[1] != null) {
/* 2454 */         newPort = parsePortNumber(parsedHostPortPair[1]);
/*      */       }
/*      */     }
/*      */ 
/* 2458 */     this.port = newPort;
/* 2459 */     this.host = newHost;
/*      */ 
/* 2461 */     this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), getProxy(), getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
/*      */ 
/* 2465 */     this.io.doHandshake(this.user, this.password, this.database);
/*      */   }
/*      */ 
/*      */   private String normalizeHost(String hostname)
/*      */   {
/* 2470 */     if ((hostname == null) || (StringUtils.isEmptyOrWhitespaceOnly(hostname))) {
/* 2471 */       return "localhost";
/*      */     }
/*      */ 
/* 2474 */     return hostname;
/*      */   }
/*      */ 
/*      */   private int parsePortNumber(String portAsString) throws SQLException {
/* 2478 */     int portNumber = 3306;
/*      */     try {
/* 2480 */       portNumber = Integer.parseInt(portAsString);
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/* 2483 */       throw SQLError.createSQLException("Illegal connection port value '" + portAsString + "'", "01S00", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2489 */     return portNumber;
/*      */   }
/*      */ 
/*      */   private void connectOneTryOnly(boolean isForReconnect, Properties mergedProps) throws SQLException {
/* 2494 */     Exception connectionNotEstablishedBecause = null;
/*      */     SQLException chainedEx;
/*      */     try {
/* 2498 */       coreConnect(mergedProps);
/* 2499 */       this.connectionId = this.io.getThreadId();
/* 2500 */       this.isClosed = false;
/*      */ 
/* 2503 */       boolean oldAutoCommit = getAutoCommit();
/* 2504 */       int oldIsolationLevel = this.isolationLevel;
/* 2505 */       boolean oldReadOnly = isReadOnly(false);
/* 2506 */       String oldCatalog = getCatalog();
/*      */ 
/* 2508 */       this.io.setStatementInterceptors(this.statementInterceptors);
/*      */ 
/* 2513 */       initializePropsFromServer();
/*      */ 
/* 2515 */       if (isForReconnect)
/*      */       {
/* 2517 */         setAutoCommit(oldAutoCommit);
/*      */ 
/* 2519 */         if (this.hasIsolationLevels) {
/* 2520 */           setTransactionIsolation(oldIsolationLevel);
/*      */         }
/*      */ 
/* 2523 */         setCatalog(oldCatalog);
/*      */ 
/* 2525 */         setReadOnly(oldReadOnly);
/*      */       }
/* 2527 */       return;
/*      */     }
/*      */     catch (Exception EEE)
/*      */     {
/* 2531 */       if (((EEE instanceof SQLException)) && (((SQLException)EEE).getErrorCode() == 1820) && (!getDisconnectOnExpiredPasswords()))
/*      */       {
/* 2534 */         return;
/*      */       }
/*      */ 
/* 2537 */       if (this.io != null) {
/* 2538 */         this.io.forceClose();
/*      */       }
/*      */ 
/* 2541 */       connectionNotEstablishedBecause = EEE;
/*      */ 
/* 2543 */       if ((EEE instanceof SQLException)) {
/* 2544 */         throw ((SQLException)EEE);
/*      */       }
/*      */ 
/* 2547 */       chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001", getExceptionInterceptor());
/*      */ 
/* 2550 */       chainedEx.initCause(connectionNotEstablishedBecause);
/*      */     }
/* 2552 */     throw chainedEx;
/*      */   }
/*      */ 
/*      */   private synchronized void createPreparedStatementCaches() throws SQLException
/*      */   {
/* 2557 */     int cacheSize = getPreparedStatementCacheSize();
/*      */     try
/*      */     {
/* 2562 */       Class factoryClass = Class.forName(getParseInfoCacheFactory());
/*      */ 
/* 2565 */       CacheAdapterFactory cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
/*      */ 
/* 2567 */       this.cachedPreparedStatementParams = cacheFactory.getInstance(this, this.myURL, getPreparedStatementCacheSize(), getPreparedStatementCacheSqlLimit(), this.props);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/* 2570 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */ 
/* 2573 */       sqlEx.initCause(e);
/*      */ 
/* 2575 */       throw sqlEx;
/*      */     } catch (InstantiationException e) {
/* 2577 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */ 
/* 2580 */       sqlEx.initCause(e);
/*      */ 
/* 2582 */       throw sqlEx;
/*      */     } catch (IllegalAccessException e) {
/* 2584 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */ 
/* 2587 */       sqlEx.initCause(e);
/*      */ 
/* 2589 */       throw sqlEx;
/*      */     }
/*      */ 
/* 2592 */     if (getUseServerPreparedStmts()) {
/* 2593 */       this.serverSideStatementCheckCache = new LRUCache(cacheSize);
/*      */ 
/* 2595 */       this.serverSideStatementCache = new LRUCache(cacheSize) {
/*      */         private static final long serialVersionUID = 7692318650375988114L;
/*      */ 
/*      */         protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
/* 2600 */           if (this.maxElements <= 1) {
/* 2601 */             return false;
/*      */           }
/*      */ 
/* 2604 */           boolean removeIt = super.removeEldestEntry(eldest);
/*      */ 
/* 2606 */           if (removeIt) {
/* 2607 */             ServerPreparedStatement ps = (ServerPreparedStatement)eldest.getValue();
/*      */ 
/* 2609 */             ps.isCached = false;
/* 2610 */             ps.setClosed(false);
/*      */             try
/*      */             {
/* 2613 */               ps.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/*      */           }
/* 2619 */           return removeIt;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement()
/*      */     throws SQLException
/*      */   {
/* 2635 */     return createStatement(1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2653 */     checkClosed();
/*      */ 
/* 2655 */     StatementImpl stmt = new StatementImpl(getLoadBalanceSafeProxy(), this.database);
/* 2656 */     stmt.setResultSetType(resultSetType);
/* 2657 */     stmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 2659 */     return stmt;
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2668 */     if ((getPedantic()) && 
/* 2669 */       (resultSetHoldability != 1)) {
/* 2670 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2676 */     return createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public void dumpTestcaseQuery(String query) {
/* 2680 */     System.err.println(query);
/*      */   }
/*      */ 
/*      */   public Connection duplicate() throws SQLException {
/* 2684 */     return new ConnectionImpl(this.origHostToConnectTo, this.origPortToConnectTo, this.props, this.origDatabaseToConnectTo, this.myURL);
/*      */   }
/*      */ 
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws SQLException
/*      */   {
/* 2738 */     return execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, false);
/*      */   }
/*      */ 
/*      */   public synchronized ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 2754 */     long queryStartTime = 0L;
/*      */ 
/* 2756 */     int endOfQueryPacketPosition = 0;
/*      */ 
/* 2758 */     if (packet != null) {
/* 2759 */       endOfQueryPacketPosition = packet.getPosition();
/*      */     }
/*      */ 
/* 2762 */     if (getGatherPerformanceMetrics()) {
/* 2763 */       queryStartTime = System.currentTimeMillis();
/*      */     }
/*      */ 
/* 2766 */     this.lastQueryFinishedTime = 0L;
/*      */ 
/* 2768 */     if ((getHighAvailability()) && ((this.autoCommit) || (getAutoReconnectForPools())) && (this.needsPing) && (!isBatch))
/*      */     {
/*      */       try
/*      */       {
/* 2772 */         pingInternal(false, 0);
/*      */ 
/* 2774 */         this.needsPing = false;
/*      */       } catch (Exception Ex) {
/* 2776 */         createNewIO(true);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*      */       ResultSetInternalMethods localResultSetInternalMethods;
/* 2781 */       if (packet == null) {
/* 2782 */         encoding = null;
/*      */ 
/* 2784 */         if (getUseUnicode()) {
/* 2785 */           encoding = getEncoding();
/*      */         }
/*      */ 
/* 2788 */         localResultSetInternalMethods = this.io.sqlQueryDirect(callingStatement, sql, encoding, null, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata); jsr 230;
/*      */       }
/*      */ 
/* 2794 */       encoding = this.io.sqlQueryDirect(callingStatement, null, null, packet, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);
/*      */     }
/*      */     catch (SQLException sqlE)
/*      */     {
/*      */       String encoding;
/* 2801 */       if (getDumpQueriesOnException()) {
/* 2802 */         String extractedSql = extractSqlFromPacket(sql, packet, endOfQueryPacketPosition);
/*      */ 
/* 2804 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/* 2806 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/*      */ 
/* 2808 */         messageBuf.append(extractedSql);
/* 2809 */         messageBuf.append("\n\n");
/*      */ 
/* 2811 */         sqlE = appendMessageToException(sqlE, messageBuf.toString(), getExceptionInterceptor());
/*      */       }
/*      */ 
/* 2814 */       if (getHighAvailability()) {
/* 2815 */         this.needsPing = true;
/*      */       } else {
/* 2817 */         String sqlState = sqlE.getSQLState();
/*      */ 
/* 2819 */         if ((sqlState != null) && (sqlState.equals("08S01")))
/*      */         {
/* 2822 */           cleanup(sqlE);
/*      */         }
/*      */       }
/*      */ 
/* 2826 */       throw sqlE;
/*      */     } catch (Exception ex) {
/* 2828 */       if (getHighAvailability())
/* 2829 */         this.needsPing = true;
/* 2830 */       else if ((ex instanceof IOException)) {
/* 2831 */         cleanup(ex);
/*      */       }
/*      */ 
/* 2834 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnexpectedException"), "S1000", getExceptionInterceptor());
/*      */ 
/* 2837 */       sqlEx.initCause(ex);
/*      */ 
/* 2839 */       throw sqlEx;
/*      */     } finally {
/* 2841 */       if (getMaintainTimeStats()) {
/* 2842 */         this.lastQueryFinishedTime = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 2846 */       if (getGatherPerformanceMetrics()) {
/* 2847 */         long queryTime = System.currentTimeMillis() - queryStartTime;
/*      */ 
/* 2850 */         registerQueryExecutionTime(queryTime);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition)
/*      */     throws SQLException
/*      */   {
/* 2859 */     String extractedSql = null;
/*      */ 
/* 2861 */     if (possibleSqlQuery != null) {
/* 2862 */       if (possibleSqlQuery.length() > getMaxQuerySizeToLog()) {
/* 2863 */         StringBuffer truncatedQueryBuf = new StringBuffer(possibleSqlQuery.substring(0, getMaxQuerySizeToLog()));
/*      */ 
/* 2865 */         truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
/* 2866 */         extractedSql = truncatedQueryBuf.toString();
/*      */       } else {
/* 2868 */         extractedSql = possibleSqlQuery;
/*      */       }
/*      */     }
/*      */ 
/* 2872 */     if (extractedSql == null)
/*      */     {
/* 2876 */       int extractPosition = endOfQueryPacketPosition;
/*      */ 
/* 2878 */       boolean truncated = false;
/*      */ 
/* 2880 */       if (endOfQueryPacketPosition > getMaxQuerySizeToLog()) {
/* 2881 */         extractPosition = getMaxQuerySizeToLog();
/* 2882 */         truncated = true;
/*      */       }
/*      */ 
/* 2885 */       extractedSql = StringUtils.toString(queryPacket.getByteBuffer(), 5, extractPosition - 5);
/*      */ 
/* 2888 */       if (truncated) {
/* 2889 */         extractedSql = extractedSql + Messages.getString("MysqlIO.25");
/*      */       }
/*      */     }
/*      */ 
/* 2893 */     return extractedSql;
/*      */   }
/*      */ 
/*      */   public StringBuffer generateConnectionCommentBlock(StringBuffer buf)
/*      */   {
/* 2898 */     buf.append("/* conn id ");
/* 2899 */     buf.append(getId());
/* 2900 */     buf.append(" clock: ");
/* 2901 */     buf.append(System.currentTimeMillis());
/* 2902 */     buf.append(" */ ");
/*      */ 
/* 2904 */     return buf;
/*      */   }
/*      */ 
/*      */   public int getActiveStatementCount()
/*      */   {
/* 2910 */     if (this.openStatements != null) {
/* 2911 */       synchronized (this.openStatements) {
/* 2912 */         return this.openStatements.size();
/*      */       }
/*      */     }
/*      */ 
/* 2916 */     return 0;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/* 2928 */     return this.autoCommit;
/*      */   }
/*      */ 
/*      */   public Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 2936 */     if (getDynamicCalendars()) {
/* 2937 */       return Calendar.getInstance();
/*      */     }
/*      */ 
/* 2940 */     return getSessionLockedCalendar();
/*      */   }
/*      */ 
/*      */   public synchronized String getCatalog()
/*      */     throws SQLException
/*      */   {
/* 2955 */     return this.database;
/*      */   }
/*      */ 
/*      */   public synchronized String getCharacterSetMetadata()
/*      */   {
/* 2962 */     return this.characterSetMetadata;
/*      */   }
/*      */ 
/*      */   public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName)
/*      */     throws SQLException
/*      */   {
/* 2975 */     if (javaEncodingName == null) {
/* 2976 */       return null;
/*      */     }
/*      */ 
/* 2979 */     if (this.usePlatformCharsetConverters) {
/* 2980 */       return null;
/*      */     }
/*      */ 
/* 2984 */     SingleByteCharsetConverter converter = null;
/*      */ 
/* 2986 */     synchronized (this.charsetConverterMap) {
/* 2987 */       Object asObject = this.charsetConverterMap.get(javaEncodingName);
/*      */ 
/* 2990 */       if (asObject == CHARSET_CONVERTER_NOT_AVAILABLE_MARKER) {
/* 2991 */         return null;
/*      */       }
/*      */ 
/* 2994 */       converter = (SingleByteCharsetConverter)asObject;
/*      */ 
/* 2996 */       if (converter == null) {
/*      */         try {
/* 2998 */           converter = SingleByteCharsetConverter.getInstance(javaEncodingName, this);
/*      */ 
/* 3001 */           if (converter == null) {
/* 3002 */             this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */           }
/*      */           else
/* 3005 */             this.charsetConverterMap.put(javaEncodingName, converter);
/*      */         }
/*      */         catch (UnsupportedEncodingException unsupEncEx) {
/* 3008 */           this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */ 
/* 3011 */           converter = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3016 */     return converter;
/*      */   }
/*      */ 
/*      */   public String getCharsetNameForIndex(int charsetIndex)
/*      */     throws SQLException
/*      */   {
/* 3031 */     String charsetName = null;
/*      */ 
/* 3033 */     if (getUseOldUTF8Behavior()) {
/* 3034 */       return getEncoding();
/*      */     }
/*      */ 
/* 3037 */     if (charsetIndex != -1) {
/*      */       try {
/* 3039 */         charsetName = (String)this.indexToJavaCharset.get(Integer.valueOf(charsetIndex));
/*      */ 
/* 3041 */         if (charsetName == null) charsetName = CharsetMapping.INDEX_TO_CHARSET[charsetIndex];
/*      */ 
/* 3043 */         if ((this.characterEncodingIsAliasForSjis) && (
/* 3044 */           ("sjis".equalsIgnoreCase(charsetName)) || ("MS932".equalsIgnoreCase(charsetName))))
/*      */         {
/* 3047 */           charsetName = getEncoding();
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 3051 */         throw SQLError.createSQLException("Unknown character set index for field '" + charsetIndex + "' received from server.", "S1000", getExceptionInterceptor());
/*      */       }
/*      */       catch (RuntimeException ex)
/*      */       {
/* 3056 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 3057 */         sqlEx.initCause(ex);
/* 3058 */         throw sqlEx;
/*      */       }
/*      */ 
/* 3062 */       if (charsetName == null)
/* 3063 */         charsetName = getEncoding();
/*      */     }
/*      */     else {
/* 3066 */       charsetName = getEncoding();
/*      */     }
/*      */ 
/* 3069 */     return charsetName;
/*      */   }
/*      */ 
/*      */   public TimeZone getDefaultTimeZone()
/*      */   {
/* 3078 */     return this.defaultTimeZone;
/*      */   }
/*      */ 
/*      */   public String getErrorMessageEncoding() {
/* 3082 */     return this.errorMessageEncoding;
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/* 3089 */     return 2;
/*      */   }
/*      */ 
/*      */   public long getId() {
/* 3093 */     return this.connectionId;
/*      */   }
/*      */ 
/*      */   public synchronized long getIdleFor()
/*      */   {
/* 3105 */     if (this.lastQueryFinishedTime == 0L) {
/* 3106 */       return 0L;
/*      */     }
/*      */ 
/* 3109 */     long now = System.currentTimeMillis();
/* 3110 */     long idleTime = now - this.lastQueryFinishedTime;
/*      */ 
/* 3112 */     return idleTime;
/*      */   }
/*      */ 
/*      */   public MysqlIO getIO()
/*      */     throws SQLException
/*      */   {
/* 3123 */     if ((this.io == null) || (this.isClosed)) {
/* 3124 */       throw SQLError.createSQLException("Operation not allowed on closed connection", "08003", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3129 */     return this.io;
/*      */   }
/*      */ 
/*      */   public Log getLog()
/*      */     throws SQLException
/*      */   {
/* 3141 */     return this.log;
/*      */   }
/*      */ 
/*      */   public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
/* 3145 */     return getMaxBytesPerChar(null, javaCharsetName);
/*      */   }
/*      */ 
/*      */   public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException
/*      */   {
/* 3150 */     String charset = null;
/*      */     try
/*      */     {
/* 3158 */       charset = (String)this.indexToCustomMysqlCharset.get(charsetIndex);
/*      */ 
/* 3160 */       if (charset == null) charset = (String)CharsetMapping.STATIC_INDEX_TO_MYSQL_CHARSET_MAP.get(charsetIndex);
/*      */ 
/* 3163 */       if (charset == null) {
/* 3164 */         charset = CharsetMapping.getMysqlEncodingForJavaEncoding(javaCharsetName, this);
/* 3165 */         if ((this.io.serverCharsetIndex == 33) && (versionMeetsMinimum(5, 5, 3)) && (javaCharsetName.equalsIgnoreCase("UTF-8")))
/*      */         {
/* 3167 */           charset = "utf8";
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3172 */       Integer mblen = (Integer)this.mysqlCharsetToCustomMblen.get(charset);
/*      */ 
/* 3175 */       if (mblen == null) mblen = (Integer)CharsetMapping.STATIC_CHARSET_TO_NUM_BYTES_MAP.get(charset);
/* 3176 */       if (mblen == null) mblen = (Integer)CharsetMapping.STATIC_4_0_CHARSET_TO_NUM_BYTES_MAP.get(charset);
/*      */ 
/* 3178 */       if (mblen != null) return mblen.intValue(); 
/*      */     }
/*      */     catch (SQLException ex) {
/* 3180 */       throw ex;
/*      */     } catch (RuntimeException ex) {
/* 3182 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 3183 */       sqlEx.initCause(ex);
/* 3184 */       throw sqlEx;
/*      */     }
/*      */ 
/* 3187 */     return 1;
/*      */   }
/*      */ 
/*      */   public java.sql.DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 3201 */     return getMetaData(true, true);
/*      */   }
/*      */ 
/*      */   private java.sql.DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
/* 3205 */     if (checkClosed) {
/* 3206 */       checkClosed();
/*      */     }
/*      */ 
/* 3209 */     return DatabaseMetaData.getInstance(getLoadBalanceSafeProxy(), this.database, checkForInfoSchema);
/*      */   }
/*      */ 
/*      */   public java.sql.Statement getMetadataSafeStatement() throws SQLException {
/* 3213 */     java.sql.Statement stmt = createStatement();
/*      */ 
/* 3215 */     if (stmt.getMaxRows() != 0) {
/* 3216 */       stmt.setMaxRows(0);
/*      */     }
/*      */ 
/* 3219 */     stmt.setEscapeProcessing(false);
/*      */ 
/* 3221 */     if (stmt.getFetchSize() != 0) {
/* 3222 */       stmt.setFetchSize(0);
/*      */     }
/*      */ 
/* 3225 */     return stmt;
/*      */   }
/*      */ 
/*      */   public int getNetBufferLength()
/*      */   {
/* 3234 */     return this.netBufferLength;
/*      */   }
/*      */ 
/*      */   public String getServerCharacterEncoding()
/*      */   {
/* 3243 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3244 */       String charset = (String)this.indexToCustomMysqlCharset.get(Integer.valueOf(this.io.serverCharsetIndex));
/* 3245 */       if (charset == null) charset = (String)CharsetMapping.STATIC_INDEX_TO_MYSQL_CHARSET_MAP.get(Integer.valueOf(this.io.serverCharsetIndex));
/* 3246 */       return charset != null ? charset : (String)this.serverVariables.get("character_set_server");
/*      */     }
/* 3248 */     return (String)this.serverVariables.get("character_set");
/*      */   }
/*      */ 
/*      */   public int getServerMajorVersion() {
/* 3252 */     return this.io.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   public int getServerMinorVersion() {
/* 3256 */     return this.io.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   public int getServerSubMinorVersion() {
/* 3260 */     return this.io.getServerSubMinorVersion();
/*      */   }
/*      */ 
/*      */   public TimeZone getServerTimezoneTZ()
/*      */   {
/* 3269 */     return this.serverTimezoneTZ;
/*      */   }
/*      */ 
/*      */   public String getServerVariable(String variableName)
/*      */   {
/* 3274 */     if (this.serverVariables != null) {
/* 3275 */       return (String)this.serverVariables.get(variableName);
/*      */     }
/*      */ 
/* 3278 */     return null;
/*      */   }
/*      */ 
/*      */   public String getServerVersion() {
/* 3282 */     return this.io.getServerVersion();
/*      */   }
/*      */ 
/*      */   public Calendar getSessionLockedCalendar()
/*      */   {
/* 3287 */     return this.sessionCalendar;
/*      */   }
/*      */ 
/*      */   public synchronized int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 3299 */     if ((this.hasIsolationLevels) && (!getUseLocalSessionState())) {
/* 3300 */       java.sql.Statement stmt = null;
/* 3301 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 3304 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 3306 */         String query = null;
/*      */ 
/* 3308 */         int offset = 0;
/*      */ 
/* 3310 */         if (versionMeetsMinimum(4, 0, 3)) {
/* 3311 */           query = "SELECT @@session.tx_isolation";
/* 3312 */           offset = 1;
/*      */         } else {
/* 3314 */           query = "SHOW VARIABLES LIKE 'transaction_isolation'";
/* 3315 */           offset = 2;
/*      */         }
/*      */ 
/* 3318 */         rs = stmt.executeQuery(query);
/*      */ 
/* 3320 */         if (rs.next()) {
/* 3321 */           String s = rs.getString(offset);
/*      */           int i;
/* 3323 */           if (s != null) {
/* 3324 */             Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/* 3326 */             if (intTI != null) {
/* 3327 */               i = intTI.intValue(); jsr 66;
/*      */             }
/*      */           }
/*      */ 
/* 3331 */           throw SQLError.createSQLException("Could not map transaction isolation '" + s + " to a valid JDBC level.", "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 3337 */         throw SQLError.createSQLException("Could not retrieve transaction isolation level from server", "S1000", getExceptionInterceptor());
/*      */       }
/*      */       finally
/*      */       {
/* 3342 */         if (rs != null) {
/*      */           try {
/* 3344 */             rs.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */ 
/* 3350 */           rs = null;
/*      */         }
/*      */ 
/* 3353 */         if (stmt != null) {
/*      */           try {
/* 3355 */             stmt.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */ 
/* 3361 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3366 */     return this.isolationLevel;
/*      */   }
/*      */ 
/*      */   public synchronized Map<String, Class<?>> getTypeMap()
/*      */     throws SQLException
/*      */   {
/* 3378 */     if (this.typeMap == null) {
/* 3379 */       this.typeMap = new HashMap();
/*      */     }
/*      */ 
/* 3382 */     return this.typeMap;
/*      */   }
/*      */ 
/*      */   public String getURL() {
/* 3386 */     return this.myURL;
/*      */   }
/*      */ 
/*      */   public String getUser() {
/* 3390 */     return this.user;
/*      */   }
/*      */ 
/*      */   public Calendar getUtcCalendar() {
/* 3394 */     return this.utcCalendar;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 3407 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 3411 */     return this.props.equals(c.getProperties());
/*      */   }
/*      */ 
/*      */   public Properties getProperties() {
/* 3415 */     return this.props;
/*      */   }
/*      */ 
/*      */   public boolean hasTriedMaster() {
/* 3419 */     return this.hasTriedMasterFlag;
/*      */   }
/*      */ 
/*      */   public void incrementNumberOfPreparedExecutes() {
/* 3423 */     if (getGatherPerformanceMetrics()) {
/* 3424 */       this.numberOfPreparedExecutes += 1L;
/*      */ 
/* 3429 */       this.numberOfQueriesIssued += 1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void incrementNumberOfPrepares() {
/* 3434 */     if (getGatherPerformanceMetrics())
/* 3435 */       this.numberOfPrepares += 1L;
/*      */   }
/*      */ 
/*      */   public void incrementNumberOfResultSetsCreated()
/*      */   {
/* 3440 */     if (getGatherPerformanceMetrics())
/* 3441 */       this.numberOfResultSetsCreated += 1L;
/*      */   }
/*      */ 
/*      */   private void initializeDriverProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 3456 */     initializeProperties(info);
/*      */ 
/* 3458 */     String exceptionInterceptorClasses = getExceptionInterceptors();
/*      */ 
/* 3460 */     if ((exceptionInterceptorClasses != null) && (!"".equals(exceptionInterceptorClasses))) {
/* 3461 */       this.exceptionInterceptor = new ExceptionInterceptorChain(exceptionInterceptorClasses);
/* 3462 */       this.exceptionInterceptor.init(this, info);
/*      */     }
/*      */ 
/* 3465 */     this.usePlatformCharsetConverters = getUseJvmCharsetConverters();
/*      */ 
/* 3467 */     this.log = LogFactory.getLogger(getLogger(), "MySQL", getExceptionInterceptor());
/*      */ 
/* 3469 */     if ((getProfileSql()) || (getUseUsageAdvisor())) {
/* 3470 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(getLoadBalanceSafeProxy());
/*      */     }
/*      */ 
/* 3473 */     if (getCachePreparedStatements()) {
/* 3474 */       createPreparedStatementCaches();
/*      */     }
/*      */ 
/* 3477 */     if ((getNoDatetimeStringSync()) && (getUseTimezone())) {
/* 3478 */       throw SQLError.createSQLException("Can't enable noDatetimeSync and useTimezone configuration properties at the same time", "01S00", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3484 */     if (getCacheCallableStatements()) {
/* 3485 */       this.parsedCallableStatementCache = new LRUCache(getCallableStatementCacheSize());
/*      */     }
/*      */ 
/* 3489 */     if (getAllowMultiQueries()) {
/* 3490 */       setCacheResultSetMetadata(false);
/*      */     }
/*      */ 
/* 3493 */     if (getCacheResultSetMetadata())
/* 3494 */       this.resultSetMetadataCache = new LRUCache(getMetadataCacheSize());
/*      */   }
/*      */ 
/*      */   private void initializePropsFromServer()
/*      */     throws SQLException
/*      */   {
/* 3509 */     String connectionInterceptorClasses = getConnectionLifecycleInterceptors();
/*      */ 
/* 3511 */     this.connectionLifecycleInterceptors = null;
/*      */ 
/* 3513 */     if (connectionInterceptorClasses != null) {
/* 3514 */       this.connectionLifecycleInterceptors = Util.loadExtensions(this, this.props, connectionInterceptorClasses, "Connection.badLifecycleInterceptor", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 3519 */     setSessionVariables();
/*      */ 
/* 3525 */     if (!versionMeetsMinimum(4, 1, 0)) {
/* 3526 */       setTransformedBitIsBoolean(false);
/*      */     }
/*      */ 
/* 3529 */     this.parserKnowsUnicode = versionMeetsMinimum(4, 1, 0);
/*      */ 
/* 3534 */     if ((getUseServerPreparedStmts()) && (versionMeetsMinimum(4, 1, 0))) {
/* 3535 */       this.useServerPreparedStmts = true;
/*      */ 
/* 3537 */       if ((versionMeetsMinimum(5, 0, 0)) && (!versionMeetsMinimum(5, 0, 3))) {
/* 3538 */         this.useServerPreparedStmts = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3547 */     if (versionMeetsMinimum(3, 21, 22)) {
/* 3548 */       loadServerVariables();
/*      */ 
/* 3550 */       if (versionMeetsMinimum(5, 0, 2))
/* 3551 */         this.autoIncrementIncrement = getServerVariableAsInt("auto_increment_increment", 1);
/*      */       else {
/* 3553 */         this.autoIncrementIncrement = 1;
/*      */       }
/*      */ 
/* 3556 */       buildCollationMapping();
/*      */ 
/* 3558 */       LicenseConfiguration.checkLicenseType(this.serverVariables);
/*      */ 
/* 3560 */       String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
/*      */ 
/* 3562 */       this.lowerCaseTableNames = (("on".equalsIgnoreCase(lowerCaseTables)) || ("1".equalsIgnoreCase(lowerCaseTables)) || ("2".equalsIgnoreCase(lowerCaseTables)));
/*      */ 
/* 3566 */       this.storesLowerCaseTableName = (("1".equalsIgnoreCase(lowerCaseTables)) || ("on".equalsIgnoreCase(lowerCaseTables)));
/*      */ 
/* 3569 */       configureTimezone();
/*      */ 
/* 3571 */       if (this.serverVariables.containsKey("max_allowed_packet")) {
/* 3572 */         int serverMaxAllowedPacket = getServerVariableAsInt("max_allowed_packet", -1);
/*      */ 
/* 3574 */         if ((serverMaxAllowedPacket != -1) && ((serverMaxAllowedPacket < getMaxAllowedPacket()) || (getMaxAllowedPacket() <= 0)))
/*      */         {
/* 3576 */           setMaxAllowedPacket(serverMaxAllowedPacket);
/* 3577 */         } else if ((serverMaxAllowedPacket == -1) && (getMaxAllowedPacket() == -1)) {
/* 3578 */           setMaxAllowedPacket(65535);
/*      */         }
/* 3580 */         int preferredBlobSendChunkSize = getBlobSendChunkSize();
/*      */ 
/* 3582 */         int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, getMaxAllowedPacket()) - 8192 - 11;
/*      */ 
/* 3587 */         setBlobSendChunkSize(String.valueOf(allowedBlobSendChunkSize));
/*      */       }
/*      */ 
/* 3590 */       if (this.serverVariables.containsKey("net_buffer_length")) {
/* 3591 */         this.netBufferLength = getServerVariableAsInt("net_buffer_length", 16384);
/*      */       }
/*      */ 
/* 3594 */       checkTransactionIsolationLevel();
/*      */ 
/* 3596 */       if (!versionMeetsMinimum(4, 1, 0)) {
/* 3597 */         checkServerEncoding();
/*      */       }
/*      */ 
/* 3600 */       this.io.checkForCharsetMismatch();
/*      */ 
/* 3602 */       if (this.serverVariables.containsKey("sql_mode")) {
/* 3603 */         int sqlMode = 0;
/*      */ 
/* 3605 */         String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
/*      */         try {
/* 3607 */           sqlMode = Integer.parseInt(sqlModeAsString);
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/* 3611 */           sqlMode = 0;
/*      */ 
/* 3613 */           if (sqlModeAsString != null) {
/* 3614 */             if (sqlModeAsString.indexOf("ANSI_QUOTES") != -1) {
/* 3615 */               sqlMode |= 4;
/*      */             }
/*      */ 
/* 3618 */             if (sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1) {
/* 3619 */               this.noBackslashEscapes = true;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 3624 */         if ((sqlMode & 0x4) > 0)
/* 3625 */           this.useAnsiQuotes = true;
/*      */         else {
/* 3627 */           this.useAnsiQuotes = false;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3633 */       this.errorMessageEncoding = CharsetMapping.getCharacterEncodingForErrorMessages(this);
/*      */     }
/*      */     catch (SQLException ex) {
/* 3636 */       throw ex;
/*      */     } catch (RuntimeException ex) {
/* 3638 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 3639 */       sqlEx.initCause(ex);
/* 3640 */       throw sqlEx;
/*      */     }
/*      */ 
/* 3644 */     boolean overrideDefaultAutocommit = isAutoCommitNonDefaultOnServer();
/*      */ 
/* 3646 */     configureClientCharacterSet(false);
/*      */ 
/* 3648 */     if (versionMeetsMinimum(3, 23, 15)) {
/* 3649 */       this.transactionsSupported = true;
/*      */ 
/* 3651 */       if (!overrideDefaultAutocommit)
/*      */         try {
/* 3653 */           setAutoCommit(true);
/*      */         }
/*      */         catch (SQLException ex)
/*      */         {
/* 3657 */           if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords()))
/* 3658 */             throw ex;
/*      */         }
/*      */     }
/*      */     else
/*      */     {
/* 3663 */       this.transactionsSupported = false;
/*      */     }
/*      */ 
/* 3667 */     if (versionMeetsMinimum(3, 23, 36))
/* 3668 */       this.hasIsolationLevels = true;
/*      */     else {
/* 3670 */       this.hasIsolationLevels = false;
/*      */     }
/*      */ 
/* 3673 */     this.hasQuotedIdentifiers = versionMeetsMinimum(3, 23, 6);
/*      */ 
/* 3675 */     this.io.resetMaxBuf();
/*      */ 
/* 3685 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3686 */       String characterSetResultsOnServerMysql = (String)this.serverVariables.get("jdbc.local.character_set_results");
/*      */ 
/* 3688 */       if ((characterSetResultsOnServerMysql == null) || (StringUtils.startsWithIgnoreCaseAndWs(characterSetResultsOnServerMysql, "NULL")) || (characterSetResultsOnServerMysql.length() == 0))
/*      */       {
/* 3692 */         String defaultMetadataCharsetMysql = (String)this.serverVariables.get("character_set_system");
/* 3693 */         String defaultMetadataCharset = null;
/*      */ 
/* 3695 */         if (defaultMetadataCharsetMysql != null)
/* 3696 */           defaultMetadataCharset = getJavaEncodingForMysqlEncoding(defaultMetadataCharsetMysql);
/*      */         else {
/* 3698 */           defaultMetadataCharset = "UTF-8";
/*      */         }
/*      */ 
/* 3701 */         this.characterSetMetadata = defaultMetadataCharset;
/*      */       } else {
/* 3703 */         this.characterSetResultsOnServer = getJavaEncodingForMysqlEncoding(characterSetResultsOnServerMysql);
/* 3704 */         this.characterSetMetadata = this.characterSetResultsOnServer;
/*      */       }
/*      */     } else {
/* 3707 */       this.characterSetMetadata = getEncoding();
/*      */     }
/*      */ 
/* 3714 */     if ((versionMeetsMinimum(4, 1, 0)) && (!versionMeetsMinimum(4, 1, 10)) && (getAllowMultiQueries()))
/*      */     {
/* 3717 */       if (isQueryCacheEnabled()) {
/* 3718 */         setAllowMultiQueries(false);
/*      */       }
/*      */     }
/*      */ 
/* 3722 */     if ((versionMeetsMinimum(5, 0, 0)) && ((getUseLocalTransactionState()) || (getElideSetAutoCommits())) && (isQueryCacheEnabled()) && (!versionMeetsMinimum(6, 0, 10)))
/*      */     {
/* 3727 */       setUseLocalTransactionState(false);
/* 3728 */       setElideSetAutoCommits(false);
/*      */     }
/*      */ 
/* 3735 */     setupServerForTruncationChecks();
/*      */   }
/*      */ 
/*      */   private boolean isQueryCacheEnabled() {
/* 3739 */     return ("ON".equalsIgnoreCase((String)this.serverVariables.get("query_cache_type"))) && (!"0".equalsIgnoreCase((String)this.serverVariables.get("query_cache_size")));
/*      */   }
/*      */ 
/*      */   private int getServerVariableAsInt(String variableName, int fallbackValue) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3746 */       return Integer.parseInt((String)this.serverVariables.get(variableName));
/*      */     } catch (NumberFormatException nfe) {
/* 3748 */       getLog().logWarn(Messages.getString("Connection.BadValueInServerVariables", new Object[] { variableName, this.serverVariables.get(variableName), Integer.valueOf(fallbackValue) }));
/*      */     }
/*      */ 
/* 3751 */     return fallbackValue;
/*      */   }
/*      */ 
/*      */   private boolean isAutoCommitNonDefaultOnServer()
/*      */     throws SQLException
/*      */   {
/* 3764 */     boolean overrideDefaultAutocommit = false;
/*      */ 
/* 3766 */     String initConnectValue = (String)this.serverVariables.get("init_connect");
/*      */ 
/* 3768 */     if ((versionMeetsMinimum(4, 1, 2)) && (initConnectValue != null) && (initConnectValue.length() > 0))
/*      */     {
/* 3770 */       if (!getElideSetAutoCommits())
/*      */       {
/* 3772 */         ResultSet rs = null;
/* 3773 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 3776 */           stmt = getMetadataSafeStatement();
/*      */ 
/* 3778 */           rs = stmt.executeQuery("SELECT @@session.autocommit");
/*      */ 
/* 3780 */           if (rs.next()) {
/* 3781 */             this.autoCommit = rs.getBoolean(1);
/* 3782 */             if (this.autoCommit != true)
/* 3783 */               overrideDefaultAutocommit = true;
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 3788 */           if (rs != null) {
/*      */             try {
/* 3790 */               rs.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/*      */           }
/* 3796 */           if (stmt != null)
/*      */             try {
/* 3798 */               stmt.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/*      */         }
/*      */       }
/* 3805 */       else if (getIO().isSetNeededForAutoCommitMode(true))
/*      */       {
/* 3807 */         this.autoCommit = false;
/* 3808 */         overrideDefaultAutocommit = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3813 */     return overrideDefaultAutocommit;
/*      */   }
/*      */ 
/*      */   public boolean isClientTzUTC() {
/* 3817 */     return this.isClientTzUTC;
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/* 3826 */     return this.isClosed;
/*      */   }
/*      */ 
/*      */   public boolean isCursorFetchEnabled() throws SQLException {
/* 3830 */     return (versionMeetsMinimum(5, 0, 2)) && (getUseCursorFetch());
/*      */   }
/*      */ 
/*      */   public boolean isInGlobalTx() {
/* 3834 */     return this.isInGlobalTx;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isMasterConnection()
/*      */   {
/* 3845 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet()
/*      */   {
/* 3855 */     return this.noBackslashEscapes;
/*      */   }
/*      */ 
/*      */   public boolean isReadInfoMsgEnabled() {
/* 3859 */     return this.readInfoMsg;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 3871 */     return isReadOnly(true);
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly(boolean useSessionStatus)
/*      */     throws SQLException
/*      */   {
/* 3886 */     if ((useSessionStatus) && (!this.isClosed) && (versionMeetsMinimum(5, 6, 5)) && (!getUseLocalSessionState())) {
/* 3887 */       java.sql.Statement stmt = null;
/* 3888 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 3892 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 3894 */         rs = stmt.executeQuery("select @@session.tx_read_only");
/* 3895 */         if (rs.next())
/* 3896 */           return rs.getInt(1) != 0 ? 1 : 0;
/*      */       }
/*      */       catch (SQLException ex1) {
/* 3899 */         if ((ex1.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 3900 */           throw SQLError.createSQLException("Could not retrieve transation read-only status server", "S1000", ex1, getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 3907 */         jsr 6; } if (rs != null) {
/*      */         try {
/* 3909 */           rs.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 3914 */         rs = null;
/*      */       }
/*      */ 
/* 3917 */       if (stmt != null) {
/*      */         try {
/* 3919 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 3924 */         stmt = null; } ret;
/*      */     }
/*      */ 
/* 3929 */     return this.readOnly;
/*      */   }
/*      */ 
/*      */   public boolean isRunningOnJDK13() {
/* 3933 */     return this.isRunningOnJDK13;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isSameResource(Connection otherConnection) {
/* 3937 */     if (otherConnection == null) {
/* 3938 */       return false;
/*      */     }
/*      */ 
/* 3941 */     boolean directCompare = true;
/*      */ 
/* 3943 */     String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
/* 3944 */     String otherOrigDatabase = ((ConnectionImpl)otherConnection).origDatabaseToConnectTo;
/* 3945 */     String otherCurrentCatalog = ((ConnectionImpl)otherConnection).database;
/*      */ 
/* 3947 */     if (!nullSafeCompare(otherHost, this.origHostToConnectTo))
/* 3948 */       directCompare = false;
/* 3949 */     else if ((otherHost != null) && (otherHost.indexOf(',') == -1) && (otherHost.indexOf(':') == -1))
/*      */     {
/* 3952 */       directCompare = ((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo;
/*      */     }
/*      */ 
/* 3956 */     if (directCompare) {
/* 3957 */       if (!nullSafeCompare(otherOrigDatabase, this.origDatabaseToConnectTo)) { directCompare = false;
/* 3958 */         directCompare = false;
/* 3959 */       } else if (!nullSafeCompare(otherCurrentCatalog, this.database)) {
/* 3960 */         directCompare = false;
/*      */       }
/*      */     }
/*      */ 
/* 3964 */     if (directCompare) {
/* 3965 */       return true;
/*      */     }
/*      */ 
/* 3969 */     String otherResourceId = ((ConnectionImpl)otherConnection).getResourceId();
/* 3970 */     String myResourceId = getResourceId();
/*      */ 
/* 3972 */     if ((otherResourceId != null) || (myResourceId != null)) {
/* 3973 */       directCompare = nullSafeCompare(otherResourceId, myResourceId);
/*      */ 
/* 3975 */       if (directCompare) {
/* 3976 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 3980 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isServerTzUTC() {
/* 3984 */     return this.isServerTzUTC;
/*      */   }
/*      */ 
/*      */   private synchronized void createConfigCacheIfNeeded()
/*      */     throws SQLException
/*      */   {
/* 3991 */     if (this.serverConfigCache != null) {
/* 3992 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3998 */       Class factoryClass = Class.forName(getServerConfigCacheFactory());
/*      */ 
/* 4001 */       CacheAdapterFactory cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
/*      */ 
/* 4003 */       this.serverConfigCache = cacheFactory.getInstance(this, this.myURL, 2147483647, 2147483647, this.props);
/*      */ 
/* 4005 */       ExceptionInterceptor evictOnCommsError = new ExceptionInterceptor()
/*      */       {
/*      */         public void init(Connection conn, Properties config) throws SQLException
/*      */         {
/*      */         }
/*      */ 
/*      */         public void destroy()
/*      */         {
/*      */         }
/*      */ 
/*      */         public SQLException interceptException(SQLException sqlEx, Connection conn)
/*      */         {
/* 4017 */           if ((sqlEx.getSQLState() != null) && (sqlEx.getSQLState().startsWith("08"))) {
/* 4018 */             ConnectionImpl.this.serverConfigCache.invalidate(ConnectionImpl.this.getURL());
/*      */           }
/* 4020 */           return null;
/*      */         }
/*      */       };
/* 4023 */       if (this.exceptionInterceptor == null)
/* 4024 */         this.exceptionInterceptor = evictOnCommsError;
/*      */       else
/* 4026 */         ((ExceptionInterceptorChain)this.exceptionInterceptor).addRingZero(evictOnCommsError);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/* 4029 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */ 
/* 4032 */       sqlEx.initCause(e);
/*      */ 
/* 4034 */       throw sqlEx;
/*      */     } catch (InstantiationException e) {
/* 4036 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */ 
/* 4039 */       sqlEx.initCause(e);
/*      */ 
/* 4041 */       throw sqlEx;
/*      */     } catch (IllegalAccessException e) {
/* 4043 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */ 
/* 4046 */       sqlEx.initCause(e);
/*      */ 
/* 4048 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void loadServerVariables()
/*      */     throws SQLException
/*      */   {
/* 4063 */     if (getCacheServerConfiguration()) {
/* 4064 */       createConfigCacheIfNeeded();
/*      */ 
/* 4066 */       Map cachedVariableMap = (Map)this.serverConfigCache.get(getURL());
/*      */ 
/* 4068 */       if (cachedVariableMap != null) {
/* 4069 */         String cachedServerVersion = (String)cachedVariableMap.get("server_version_string");
/*      */ 
/* 4071 */         if ((cachedServerVersion != null) && (this.io.getServerVersion() != null) && (cachedServerVersion.equals(this.io.getServerVersion())))
/*      */         {
/* 4073 */           this.serverVariables = cachedVariableMap;
/* 4074 */           this.usingCachedConfig = true;
/*      */ 
/* 4076 */           return;
/*      */         }
/*      */ 
/* 4079 */         this.serverConfigCache.invalidate(getURL());
/*      */       }
/*      */     }
/*      */ 
/* 4083 */     java.sql.Statement stmt = null;
/* 4084 */     ResultSet results = null;
/*      */     try
/*      */     {
/* 4087 */       stmt = getMetadataSafeStatement();
/*      */ 
/* 4089 */       String version = this.dbmd.getDriverVersion();
/*      */ 
/* 4091 */       if ((version != null) && (version.indexOf('*') != -1)) {
/* 4092 */         StringBuffer buf = new StringBuffer(version.length() + 10);
/*      */ 
/* 4094 */         for (int i = 0; i < version.length(); i++) {
/* 4095 */           char c = version.charAt(i);
/*      */ 
/* 4097 */           if (c == '*')
/* 4098 */             buf.append("[star]");
/*      */           else {
/* 4100 */             buf.append(c);
/*      */           }
/*      */         }
/*      */ 
/* 4104 */         version = buf.toString();
/*      */       }
/*      */ 
/* 4107 */       String versionComment = "/* " + version + " */";
/*      */ 
/* 4110 */       String query = versionComment + "SHOW VARIABLES";
/*      */ 
/* 4112 */       if (versionMeetsMinimum(5, 0, 3)) {
/* 4113 */         query = versionComment + "SHOW VARIABLES WHERE Variable_name ='language'" + " OR Variable_name = 'net_write_timeout'" + " OR Variable_name = 'interactive_timeout'" + " OR Variable_name = 'wait_timeout'" + " OR Variable_name = 'character_set_client'" + " OR Variable_name = 'character_set_connection'" + " OR Variable_name = 'character_set'" + " OR Variable_name = 'character_set_server'" + " OR Variable_name = 'tx_isolation'" + " OR Variable_name = 'transaction_isolation'" + " OR Variable_name = 'character_set_results'" + " OR Variable_name = 'timezone'" + " OR Variable_name = 'time_zone'" + " OR Variable_name = 'system_time_zone'" + " OR Variable_name = 'lower_case_table_names'" + " OR Variable_name = 'max_allowed_packet'" + " OR Variable_name = 'net_buffer_length'" + " OR Variable_name = 'sql_mode'" + " OR Variable_name = 'query_cache_type'" + " OR Variable_name = 'query_cache_size'" + " OR Variable_name = 'init_connect'";
/*      */       }
/*      */ 
/* 4136 */       this.serverVariables = new HashMap();
/*      */       try
/*      */       {
/* 4139 */         results = stmt.executeQuery(query);
/*      */ 
/* 4141 */         while (results.next()) {
/* 4142 */           this.serverVariables.put(results.getString(1), results.getString(2));
/*      */         }
/*      */ 
/* 4146 */         results.close();
/* 4147 */         results = null;
/*      */       } catch (SQLException ex) {
/* 4149 */         if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 4150 */           throw ex;
/*      */         }
/*      */       }
/*      */ 
/* 4154 */       if (versionMeetsMinimum(5, 0, 2)) {
/*      */         try {
/* 4156 */           results = stmt.executeQuery(versionComment + "SELECT @@session.auto_increment_increment");
/*      */ 
/* 4158 */           if (results.next())
/* 4159 */             this.serverVariables.put("auto_increment_increment", results.getString(1));
/*      */         }
/*      */         catch (SQLException ex) {
/* 4162 */           if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 4163 */             throw ex;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 4168 */       if (getCacheServerConfiguration()) {
/* 4169 */         this.serverVariables.put("server_version_string", this.io.getServerVersion());
/*      */ 
/* 4171 */         this.serverConfigCache.put(getURL(), this.serverVariables);
/*      */ 
/* 4173 */         this.usingCachedConfig = true;
/*      */       }
/*      */     } catch (SQLException e) {
/* 4176 */       throw e;
/*      */     } finally {
/* 4178 */       if (results != null) {
/*      */         try {
/* 4180 */           results.close();
/*      */         }
/*      */         catch (SQLException sqlE)
/*      */         {
/*      */         }
/*      */       }
/* 4186 */       if (stmt != null)
/*      */         try {
/* 4188 */           stmt.close();
/*      */         }
/*      */         catch (SQLException sqlE)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getAutoIncrementIncrement()
/*      */   {
/* 4199 */     return this.autoIncrementIncrement;
/*      */   }
/*      */ 
/*      */   public boolean lowerCaseTableNames()
/*      */   {
/* 4208 */     return this.lowerCaseTableNames;
/*      */   }
/*      */ 
/*      */   public synchronized void maxRowsChanged(Statement stmt)
/*      */   {
/* 4218 */     if (this.statementsUsingMaxRows == null) {
/* 4219 */       this.statementsUsingMaxRows = new HashMap();
/*      */     }
/*      */ 
/* 4222 */     this.statementsUsingMaxRows.put(stmt, stmt);
/*      */ 
/* 4224 */     this.maxRowsChanged = true;
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/* 4240 */     if (sql == null) {
/* 4241 */       return null;
/*      */     }
/*      */ 
/* 4244 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), getLoadBalanceSafeProxy());
/*      */ 
/* 4248 */     if ((escapedSqlResult instanceof String)) {
/* 4249 */       return (String)escapedSqlResult;
/*      */     }
/*      */ 
/* 4252 */     return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */   }
/*      */ 
/*      */   private CallableStatement parseCallableStatement(String sql) throws SQLException
/*      */   {
/* 4257 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), getLoadBalanceSafeProxy());
/*      */ 
/* 4260 */     boolean isFunctionCall = false;
/* 4261 */     String parsedSql = null;
/*      */ 
/* 4263 */     if ((escapedSqlResult instanceof EscapeProcessorResult)) {
/* 4264 */       parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/* 4265 */       isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
/*      */     } else {
/* 4267 */       parsedSql = (String)escapedSqlResult;
/* 4268 */       isFunctionCall = false;
/*      */     }
/*      */ 
/* 4271 */     return CallableStatement.getInstance(getLoadBalanceSafeProxy(), parsedSql, this.database, isFunctionCall);
/*      */   }
/*      */ 
/*      */   public boolean parserKnowsUnicode()
/*      */   {
/* 4281 */     return this.parserKnowsUnicode;
/*      */   }
/*      */ 
/*      */   public void ping()
/*      */     throws SQLException
/*      */   {
/* 4291 */     pingInternal(true, 0);
/*      */   }
/*      */ 
/*      */   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException
/*      */   {
/* 4296 */     if (checkForClosedConnection) {
/* 4297 */       checkClosed();
/*      */     }
/*      */ 
/* 4300 */     long pingMillisLifetime = getSelfDestructOnPingSecondsLifetime();
/* 4301 */     int pingMaxOperations = getSelfDestructOnPingMaxOperations();
/*      */ 
/* 4303 */     if (((pingMillisLifetime > 0L) && (System.currentTimeMillis() - this.connectionCreationTimeMillis > pingMillisLifetime)) || ((pingMaxOperations > 0) && (pingMaxOperations <= this.io.getCommandCount())))
/*      */     {
/* 4307 */       close();
/*      */ 
/* 4309 */       throw SQLError.createSQLException(Messages.getString("Connection.exceededConnectionLifetime"), "08S01", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4314 */     this.io.sendCommand(14, null, null, false, null, timeoutMillis);
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/* 4329 */     return prepareCall(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4350 */     if (versionMeetsMinimum(5, 0, 0)) {
/* 4351 */       CallableStatement cStmt = null;
/*      */ 
/* 4353 */       if (!getCacheCallableStatements())
/*      */       {
/* 4355 */         cStmt = parseCallableStatement(sql);
/*      */       }
/* 4357 */       else synchronized (this.parsedCallableStatementCache) {
/* 4358 */           CompoundCacheKey key = new CompoundCacheKey(getCatalog(), sql);
/*      */ 
/* 4360 */           CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
/*      */ 
/* 4363 */           if (cachedParamInfo != null) {
/* 4364 */             cStmt = CallableStatement.getInstance(getLoadBalanceSafeProxy(), cachedParamInfo);
/*      */           } else {
/* 4366 */             cStmt = parseCallableStatement(sql);
/*      */ 
/* 4368 */             synchronized (cStmt) {
/* 4369 */               cachedParamInfo = cStmt.paramInfo;
/*      */             }
/*      */ 
/* 4372 */             this.parsedCallableStatementCache.put(key, cachedParamInfo);
/*      */           }
/*      */         }
/*      */ 
/*      */ 
/* 4377 */       cStmt.setResultSetType(resultSetType);
/* 4378 */       cStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 4380 */       return cStmt;
/*      */     }
/*      */ 
/* 4383 */     throw SQLError.createSQLException("Callable statements not supported.", "S1C00", getExceptionInterceptor());
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4393 */     if ((getPedantic()) && 
/* 4394 */       (resultSetHoldability != 1)) {
/* 4395 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4401 */     CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */ 
/* 4404 */     return cStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 4434 */     return prepareStatement(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 4443 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4445 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 4448 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4468 */     checkClosed();
/*      */ 
/* 4474 */     PreparedStatement pStmt = null;
/*      */ 
/* 4476 */     boolean canServerPrepare = true;
/*      */ 
/* 4478 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 4480 */     if ((this.useServerPreparedStmts) && (getEmulateUnsupportedPstmts())) {
/* 4481 */       canServerPrepare = canHandleAsServerPreparedStatement(nativeSql);
/*      */     }
/*      */ 
/* 4484 */     if ((this.useServerPreparedStmts) && (canServerPrepare)) {
/* 4485 */       if (getCachePreparedStatements())
/* 4486 */         synchronized (this.serverSideStatementCache) {
/* 4487 */           pStmt = (ServerPreparedStatement)this.serverSideStatementCache.remove(sql);
/*      */ 
/* 4489 */           if (pStmt != null) {
/* 4490 */             ((ServerPreparedStatement)pStmt).setClosed(false);
/* 4491 */             pStmt.clearParameters();
/*      */           }
/*      */ 
/* 4494 */           if (pStmt == null)
/*      */             try {
/* 4496 */               pStmt = ServerPreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */ 
/* 4498 */               if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 4499 */                 ((ServerPreparedStatement)pStmt).isCached = true;
/*      */               }
/*      */ 
/* 4502 */               pStmt.setResultSetType(resultSetType);
/* 4503 */               pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */             }
/*      */             catch (SQLException sqlEx) {
/* 4506 */               if (getEmulateUnsupportedPstmts()) {
/* 4507 */                 pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */ 
/* 4509 */                 if (sql.length() < getPreparedStatementCacheSqlLimit())
/* 4510 */                   this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
/*      */               }
/*      */               else {
/* 4513 */                 throw sqlEx;
/*      */               }
/*      */             }
/*      */         }
/*      */       else {
/*      */         try
/*      */         {
/* 4520 */           pStmt = ServerPreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */ 
/* 4523 */           pStmt.setResultSetType(resultSetType);
/* 4524 */           pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 4527 */           if (getEmulateUnsupportedPstmts())
/* 4528 */             pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */           else
/* 4530 */             throw sqlEx;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 4535 */       pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */     }
/*      */ 
/* 4538 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4547 */     if ((getPedantic()) && 
/* 4548 */       (resultSetHoldability != 1)) {
/* 4549 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 4555 */     return prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 4563 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4565 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 4569 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 4577 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4579 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 4583 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason)
/*      */     throws SQLException
/*      */   {
/* 4598 */     SQLException sqlEx = null;
/*      */ 
/* 4600 */     if (isClosed()) {
/* 4601 */       return;
/*      */     }
/*      */ 
/* 4604 */     this.forceClosedReason = reason;
/*      */     try
/*      */     {
/* 4607 */       if (!skipLocalTeardown) {
/* 4608 */         if ((!getAutoCommit()) && (issueRollback)) {
/*      */           try {
/* 4610 */             rollback();
/*      */           } catch (SQLException ex) {
/* 4612 */             sqlEx = ex;
/*      */           }
/*      */         }
/*      */ 
/* 4616 */         reportMetrics();
/*      */ 
/* 4618 */         if (getUseUsageAdvisor()) {
/* 4619 */           if (!calledExplicitly) {
/* 4620 */             String message = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks.";
/*      */ 
/* 4622 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           }
/*      */ 
/* 4630 */           long connectionLifeTime = System.currentTimeMillis() - this.connectionCreationTimeMillis;
/*      */ 
/* 4633 */           if (connectionLifeTime < 500L) {
/* 4634 */             String message = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient.";
/*      */ 
/* 4636 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 4646 */           closeAllOpenStatements();
/*      */         } catch (SQLException ex) {
/* 4648 */           sqlEx = ex;
/*      */         }
/*      */ 
/* 4651 */         if (this.io != null)
/*      */           try {
/* 4653 */             this.io.quit();
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*      */           }
/*      */       }
/*      */       else {
/* 4660 */         this.io.forceClose();
/*      */       }
/*      */ 
/* 4663 */       if (this.statementInterceptors != null) {
/* 4664 */         for (int i = 0; i < this.statementInterceptors.size(); i++) {
/* 4665 */           ((StatementInterceptorV2)this.statementInterceptors.get(i)).destroy();
/*      */         }
/*      */       }
/*      */ 
/* 4669 */       if (this.exceptionInterceptor != null)
/* 4670 */         this.exceptionInterceptor.destroy();
/*      */     }
/*      */     finally {
/* 4673 */       this.openStatements = null;
/* 4674 */       this.io = null;
/* 4675 */       this.statementInterceptors = null;
/* 4676 */       this.exceptionInterceptor = null;
/* 4677 */       ProfilerEventHandlerFactory.removeInstance(this);
/*      */ 
/* 4679 */       synchronized (this) {
/* 4680 */         if (this.cancelTimer != null) {
/* 4681 */           this.cancelTimer.cancel();
/*      */         }
/*      */       }
/*      */ 
/* 4685 */       this.isClosed = true;
/*      */     }
/*      */ 
/* 4688 */     if (sqlEx != null)
/* 4689 */       throw sqlEx;
/*      */   }
/*      */ 
/*      */   public synchronized void recachePreparedStatement(ServerPreparedStatement pstmt)
/*      */     throws SQLException
/*      */   {
/* 4695 */     if (pstmt.isPoolable())
/* 4696 */       synchronized (this.serverSideStatementCache) {
/* 4697 */         this.serverSideStatementCache.put(pstmt.originalSql, pstmt);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void registerQueryExecutionTime(long queryTimeMs)
/*      */   {
/* 4708 */     if (queryTimeMs > this.longestQueryTimeMs) {
/* 4709 */       this.longestQueryTimeMs = queryTimeMs;
/*      */ 
/* 4711 */       repartitionPerformanceHistogram();
/*      */     }
/*      */ 
/* 4714 */     addToPerformanceHistogram(queryTimeMs, 1);
/*      */ 
/* 4716 */     if (queryTimeMs < this.shortestQueryTimeMs) {
/* 4717 */       this.shortestQueryTimeMs = (queryTimeMs == 0L ? 1L : queryTimeMs);
/*      */     }
/*      */ 
/* 4720 */     this.numberOfQueriesIssued += 1L;
/*      */ 
/* 4722 */     this.totalQueryTimeMs += queryTimeMs;
/*      */   }
/*      */ 
/*      */   public void registerStatement(Statement stmt)
/*      */   {
/* 4732 */     synchronized (this.openStatements) {
/* 4733 */       this.openStatements.put(stmt, stmt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound)
/*      */   {
/* 4747 */     if (this.oldHistCounts == null) {
/* 4748 */       this.oldHistCounts = new int[histCounts.length];
/* 4749 */       this.oldHistBreakpoints = new long[histBreakpoints.length];
/*      */     }
/*      */ 
/* 4752 */     System.arraycopy(histCounts, 0, this.oldHistCounts, 0, histCounts.length);
/*      */ 
/* 4754 */     System.arraycopy(histBreakpoints, 0, this.oldHistBreakpoints, 0, histBreakpoints.length);
/*      */ 
/* 4757 */     createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
/*      */ 
/* 4760 */     for (int i = 0; i < 20; i++)
/* 4761 */       addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
/*      */   }
/*      */ 
/*      */   private void repartitionPerformanceHistogram()
/*      */   {
/* 4767 */     checkAndCreatePerformanceHistogram();
/*      */ 
/* 4769 */     repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, this.shortestQueryTimeMs == 9223372036854775807L ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */   private void repartitionTablesAccessedHistogram()
/*      */   {
/* 4776 */     checkAndCreateTablesAccessedHistogram();
/*      */ 
/* 4778 */     repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, this.minimumNumberTablesAccessed == 9223372036854775807L ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */   private void reportMetrics()
/*      */   {
/* 4786 */     if (getGatherPerformanceMetrics()) {
/* 4787 */       StringBuffer logMessage = new StringBuffer(256);
/*      */ 
/* 4789 */       logMessage.append("** Performance Metrics Report **\n");
/* 4790 */       logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
/*      */ 
/* 4792 */       logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
/*      */ 
/* 4794 */       logMessage.append("\nAverage query execution time: " + this.totalQueryTimeMs / this.numberOfQueriesIssued + " ms");
/*      */ 
/* 4798 */       logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
/*      */ 
/* 4800 */       logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
/*      */ 
/* 4802 */       logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
/*      */ 
/* 4804 */       logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
/*      */ 
/* 4807 */       if (this.perfMetricsHistBreakpoints != null) {
/* 4808 */         logMessage.append("\n\n\tTiming Histogram:\n");
/* 4809 */         int maxNumPoints = 20;
/* 4810 */         int highestCount = -2147483648;
/*      */ 
/* 4812 */         for (int i = 0; i < 20; i++) {
/* 4813 */           if (this.perfMetricsHistCounts[i] > highestCount) {
/* 4814 */             highestCount = this.perfMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */ 
/* 4818 */         if (highestCount == 0) {
/* 4819 */           highestCount = 1;
/*      */         }
/*      */ 
/* 4822 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4824 */           if (i == 0) {
/* 4825 */             logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */           else
/*      */           {
/* 4829 */             logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */ 
/* 4835 */           logMessage.append("\t");
/*      */ 
/* 4837 */           int numPointsToGraph = (int)(maxNumPoints * (this.perfMetricsHistCounts[i] / highestCount));
/*      */ 
/* 4839 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4840 */             logMessage.append("*");
/*      */           }
/*      */ 
/* 4843 */           if (this.longestQueryTimeMs < this.perfMetricsHistCounts[(i + 1)])
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/* 4848 */         if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
/* 4849 */           logMessage.append("\n\tbetween ");
/* 4850 */           logMessage.append(this.perfMetricsHistBreakpoints[18]);
/*      */ 
/* 4852 */           logMessage.append(" and ");
/* 4853 */           logMessage.append(this.perfMetricsHistBreakpoints[19]);
/*      */ 
/* 4855 */           logMessage.append(" ms: \t");
/* 4856 */           logMessage.append(this.perfMetricsHistCounts[19]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4861 */       if (this.numTablesMetricsHistBreakpoints != null) {
/* 4862 */         logMessage.append("\n\n\tTable Join Histogram:\n");
/* 4863 */         int maxNumPoints = 20;
/* 4864 */         int highestCount = -2147483648;
/*      */ 
/* 4866 */         for (int i = 0; i < 20; i++) {
/* 4867 */           if (this.numTablesMetricsHistCounts[i] > highestCount) {
/* 4868 */             highestCount = this.numTablesMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */ 
/* 4872 */         if (highestCount == 0) {
/* 4873 */           highestCount = 1;
/*      */         }
/*      */ 
/* 4876 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4878 */           if (i == 0) {
/* 4879 */             logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */           else
/*      */           {
/* 4884 */             logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */ 
/* 4892 */           logMessage.append("\t");
/*      */ 
/* 4894 */           int numPointsToGraph = (int)(maxNumPoints * (this.numTablesMetricsHistCounts[i] / highestCount));
/*      */ 
/* 4896 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4897 */             logMessage.append("*");
/*      */           }
/*      */ 
/* 4900 */           if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[(i + 1)])
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/* 4905 */         if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
/* 4906 */           logMessage.append("\n\tbetween ");
/* 4907 */           logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
/*      */ 
/* 4909 */           logMessage.append(" and ");
/* 4910 */           logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
/*      */ 
/* 4912 */           logMessage.append(" tables: ");
/* 4913 */           logMessage.append(this.numTablesMetricsHistCounts[19]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4918 */       this.log.logInfo(logMessage);
/*      */ 
/* 4920 */       this.metricsLastReportedMs = System.currentTimeMillis();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void reportMetricsIfNeeded()
/*      */   {
/* 4929 */     if ((getGatherPerformanceMetrics()) && 
/* 4930 */       (System.currentTimeMillis() - this.metricsLastReportedMs > getReportMetricsIntervalMillis()))
/* 4931 */       reportMetrics();
/*      */   }
/*      */ 
/*      */   public void reportNumberOfTablesAccessed(int numTablesAccessed)
/*      */   {
/* 4937 */     if (numTablesAccessed < this.minimumNumberTablesAccessed) {
/* 4938 */       this.minimumNumberTablesAccessed = numTablesAccessed;
/*      */     }
/*      */ 
/* 4941 */     if (numTablesAccessed > this.maximumNumberTablesAccessed) {
/* 4942 */       this.maximumNumberTablesAccessed = numTablesAccessed;
/*      */ 
/* 4944 */       repartitionTablesAccessedHistogram();
/*      */     }
/*      */ 
/* 4947 */     addToTablesAccessedHistogram(numTablesAccessed, 1);
/*      */   }
/*      */ 
/*      */   public void resetServerState()
/*      */     throws SQLException
/*      */   {
/* 4959 */     if ((!getParanoid()) && (this.io != null) && (versionMeetsMinimum(4, 0, 6)))
/*      */     {
/* 4961 */       changeUser(this.user, this.password);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void rollback()
/*      */     throws SQLException
/*      */   {
/* 4975 */     checkClosed();
/*      */     try
/*      */     {
/* 4978 */       if (this.connectionLifecycleInterceptors != null) {
/* 4979 */         IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException {
/* 4982 */             if (!((ConnectionLifecycleInterceptor)each).rollback())
/* 4983 */               this.stopIterating = true;
/*      */           }
/*      */         };
/* 4988 */         iter.doForAll();
/*      */ 
/* 4990 */         if (!iter.fullIteration()) {
/* 4991 */           jsr 111;
/*      */         }
/*      */       }
/*      */ 
/* 4995 */       if ((this.autoCommit) && (!getRelaxAutoCommit())) {
/* 4996 */         throw SQLError.createSQLException("Can't call rollback when autocommit=true", "08003", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 4999 */       if (this.transactionsSupported)
/*      */         try {
/* 5001 */           rollbackNoChecks();
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 5004 */           if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() == 1196))
/*      */           {
/* 5006 */             jsr 47;
/*      */           }
/* 5008 */           throw sqlEx;
/*      */         }
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 5013 */       if ("08S01".equals(sqlException.getSQLState()))
/*      */       {
/* 5015 */         throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */       }
/*      */ 
/* 5020 */       throw sqlException;
/*      */     } finally {
/* 5022 */       this.needsPing = getReconnectAtTxEnd();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void rollback(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/* 5031 */     if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 5032 */       checkClosed();
/*      */       try
/*      */       {
/* 5035 */         if (this.connectionLifecycleInterceptors != null) {
/* 5036 */           IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator(), savepoint)
/*      */           {
/*      */             void forEach(Extension each) throws SQLException {
/* 5039 */               if (!((ConnectionLifecycleInterceptor)each).rollback(this.val$savepoint))
/* 5040 */                 this.stopIterating = true;
/*      */             }
/*      */           };
/* 5045 */           iter.doForAll();
/*      */ 
/* 5047 */           if (!iter.fullIteration()) {
/* 5048 */             jsr 235;
/*      */           }
/*      */         }
/*      */ 
/* 5052 */         StringBuffer rollbackQuery = new StringBuffer("ROLLBACK TO SAVEPOINT ");
/*      */ 
/* 5054 */         rollbackQuery.append('`');
/* 5055 */         rollbackQuery.append(savepoint.getSavepointName());
/* 5056 */         rollbackQuery.append('`');
/*      */ 
/* 5058 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 5061 */           stmt = getMetadataSafeStatement();
/*      */ 
/* 5063 */           stmt.executeUpdate(rollbackQuery.toString());
/*      */         } catch (SQLException sqlEx) {
/* 5065 */           int errno = sqlEx.getErrorCode();
/*      */ 
/* 5067 */           if (errno == 1181) {
/* 5068 */             String msg = sqlEx.getMessage();
/*      */ 
/* 5070 */             if (msg != null) {
/* 5071 */               int indexOfError153 = msg.indexOf("153");
/*      */ 
/* 5073 */               if (indexOfError153 != -1) {
/* 5074 */                 throw SQLError.createSQLException("Savepoint '" + savepoint.getSavepointName() + "' does not exist", "S1009", errno, getExceptionInterceptor());
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 5084 */           if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() != 1196))
/*      */           {
/* 5086 */             throw sqlEx;
/*      */           }
/*      */ 
/* 5089 */           if ("08S01".equals(sqlEx.getSQLState()))
/*      */           {
/* 5091 */             throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */           }
/*      */ 
/* 5096 */           throw sqlEx;
/*      */         } finally {
/* 5098 */           closeStatement(stmt);
/*      */         }
/*      */       } finally {
/* 5101 */         this.needsPing = getReconnectAtTxEnd();
/*      */       }
/*      */     } else {
/* 5104 */       throw SQLError.notImplemented();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void rollbackNoChecks() throws SQLException {
/* 5109 */     if ((getUseLocalTransactionState()) && (versionMeetsMinimum(5, 0, 0)) && 
/* 5110 */       (!this.io.inTransactionOnServer())) {
/* 5111 */       return;
/*      */     }
/*      */ 
/* 5115 */     execSQL(null, "rollback", -1, null, 1003, 1007, false, this.database, null, false);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 5127 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 5129 */     return ServerPreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, getCatalog(), 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 5139 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 5141 */     PreparedStatement pStmt = ServerPreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, getCatalog(), 1003, 1007);
/*      */ 
/* 5145 */     pStmt.setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 5148 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 5156 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */ 
/* 5158 */     return ServerPreparedStatement.getInstance(getLoadBalanceSafeProxy(), nativeSql, getCatalog(), resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 5169 */     if ((getPedantic()) && 
/* 5170 */       (resultSetHoldability != 1)) {
/* 5171 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 5177 */     return serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 5186 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */ 
/* 5188 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 5192 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 5200 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */ 
/* 5202 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 5206 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public boolean serverSupportsConvertFn() throws SQLException {
/* 5210 */     return versionMeetsMinimum(4, 0, 2);
/*      */   }
/*      */ 
/*      */   public synchronized void setAutoCommit(boolean autoCommitFlag)
/*      */     throws SQLException
/*      */   {
/* 5236 */     checkClosed();
/*      */ 
/* 5238 */     if (this.connectionLifecycleInterceptors != null) {
/* 5239 */       IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator(), autoCommitFlag)
/*      */       {
/*      */         void forEach(Extension each) throws SQLException {
/* 5242 */           if (!((ConnectionLifecycleInterceptor)each).setAutoCommit(this.val$autoCommitFlag))
/* 5243 */             this.stopIterating = true;
/*      */         }
/*      */       };
/* 5248 */       iter.doForAll();
/*      */ 
/* 5250 */       if (!iter.fullIteration()) {
/* 5251 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 5255 */     if (getAutoReconnectForPools()) {
/* 5256 */       setHighAvailability(true);
/*      */     }
/*      */     try
/*      */     {
/* 5260 */       if (this.transactionsSupported)
/*      */       {
/* 5262 */         boolean needsSetOnServer = true;
/*      */ 
/* 5264 */         if ((getUseLocalSessionState()) && (this.autoCommit == autoCommitFlag))
/*      */         {
/* 5266 */           needsSetOnServer = false;
/* 5267 */         } else if (!getHighAvailability()) {
/* 5268 */           needsSetOnServer = getIO().isSetNeededForAutoCommitMode(autoCommitFlag);
/*      */         }
/*      */ 
/* 5279 */         this.autoCommit = autoCommitFlag;
/*      */ 
/* 5281 */         if (needsSetOnServer) {
/* 5282 */           execSQL(null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, null, 1003, 1007, false, this.database, null, false);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 5290 */         if ((!autoCommitFlag) && (!getRelaxAutoCommit())) {
/* 5291 */           throw SQLError.createSQLException("MySQL Versions Older than 3.23.15 do not support transactions", "08003", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5296 */         this.autoCommit = autoCommitFlag;
/*      */       }
/*      */     } finally {
/* 5299 */       if (getAutoReconnectForPools())
/* 5300 */         setHighAvailability(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/* 5321 */     checkClosed();
/*      */ 
/* 5323 */     if (catalog == null) {
/* 5324 */       throw SQLError.createSQLException("Catalog can not be null", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 5328 */     if (this.connectionLifecycleInterceptors != null) {
/* 5329 */       IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator(), catalog)
/*      */       {
/*      */         void forEach(Extension each) throws SQLException {
/* 5332 */           if (!((ConnectionLifecycleInterceptor)each).setCatalog(this.val$catalog))
/* 5333 */             this.stopIterating = true;
/*      */         }
/*      */       };
/* 5338 */       iter.doForAll();
/*      */ 
/* 5340 */       if (!iter.fullIteration()) {
/* 5341 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 5345 */     if (getUseLocalSessionState()) {
/* 5346 */       if (this.lowerCaseTableNames) {
/* 5347 */         if (this.database.equalsIgnoreCase(catalog)) {
/* 5348 */           return;
/*      */         }
/*      */       }
/* 5351 */       else if (this.database.equals(catalog)) {
/* 5352 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5357 */     String quotedId = this.dbmd.getIdentifierQuoteString();
/*      */ 
/* 5359 */     if ((quotedId == null) || (quotedId.equals(" "))) {
/* 5360 */       quotedId = "";
/*      */     }
/*      */ 
/* 5363 */     StringBuffer query = new StringBuffer("USE ");
/* 5364 */     query.append(quotedId);
/* 5365 */     query.append(catalog);
/* 5366 */     query.append(quotedId);
/*      */ 
/* 5368 */     execSQL(null, query.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5373 */     this.database = catalog;
/*      */   }
/*      */ 
/*      */   public synchronized void setFailedOver(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setHoldability(int arg0)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setInGlobalTx(boolean flag)
/*      */   {
/* 5392 */     this.isInGlobalTx = flag;
/*      */   }
/*      */ 
/*      */   public void setPreferSlaveDuringFailover(boolean flag)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setReadInfoMsgEnabled(boolean flag)
/*      */   {
/* 5405 */     this.readInfoMsg = flag;
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean readOnlyFlag)
/*      */     throws SQLException
/*      */   {
/* 5419 */     checkClosed();
/*      */ 
/* 5421 */     setReadOnlyInternal(readOnlyFlag);
/*      */   }
/*      */ 
/*      */   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException
/*      */   {
/* 5426 */     if ((versionMeetsMinimum(5, 6, 5)) && (
/* 5427 */       (!getUseLocalSessionState()) || (readOnlyFlag != this.readOnly))) {
/* 5428 */       execSQL(null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, null, 1003, 1007, false, this.database, null, false);
/*      */     }
/*      */ 
/* 5436 */     this.readOnly = readOnlyFlag;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/* 5443 */     MysqlSavepoint savepoint = new MysqlSavepoint(getExceptionInterceptor());
/*      */ 
/* 5445 */     setSavepoint(savepoint);
/*      */ 
/* 5447 */     return savepoint;
/*      */   }
/*      */ 
/*      */   private synchronized void setSavepoint(MysqlSavepoint savepoint) throws SQLException
/*      */   {
/* 5452 */     if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 5453 */       checkClosed();
/*      */ 
/* 5455 */       StringBuffer savePointQuery = new StringBuffer("SAVEPOINT ");
/* 5456 */       savePointQuery.append('`');
/* 5457 */       savePointQuery.append(savepoint.getSavepointName());
/* 5458 */       savePointQuery.append('`');
/*      */ 
/* 5460 */       java.sql.Statement stmt = null;
/*      */       try
/*      */       {
/* 5463 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 5465 */         stmt.executeUpdate(savePointQuery.toString());
/*      */       } finally {
/* 5467 */         closeStatement(stmt);
/*      */       }
/*      */     } else {
/* 5470 */       throw SQLError.notImplemented();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized Savepoint setSavepoint(String name)
/*      */     throws SQLException
/*      */   {
/* 5478 */     MysqlSavepoint savepoint = new MysqlSavepoint(name, getExceptionInterceptor());
/*      */ 
/* 5480 */     setSavepoint(savepoint);
/*      */ 
/* 5482 */     return savepoint;
/*      */   }
/*      */ 
/*      */   private void setSessionVariables()
/*      */     throws SQLException
/*      */   {
/* 5489 */     if ((versionMeetsMinimum(4, 0, 0)) && (getSessionVariables() != null)) {
/* 5490 */       List variablesToSet = StringUtils.split(getSessionVariables(), ",", "\"'", "\"'", false);
/*      */ 
/* 5493 */       int numVariablesToSet = variablesToSet.size();
/*      */ 
/* 5495 */       java.sql.Statement stmt = null;
/*      */       try
/*      */       {
/* 5498 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 5500 */         for (int i = 0; i < numVariablesToSet; i++) {
/* 5501 */           String variableValuePair = (String)variablesToSet.get(i);
/*      */ 
/* 5503 */           if (variableValuePair.startsWith("@"))
/* 5504 */             stmt.executeUpdate("SET " + variableValuePair);
/*      */           else
/* 5506 */             stmt.executeUpdate("SET SESSION " + variableValuePair);
/*      */         }
/*      */       }
/*      */       finally {
/* 5510 */         if (stmt != null)
/* 5511 */           stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/* 5527 */     checkClosed();
/*      */ 
/* 5529 */     if (this.hasIsolationLevels) {
/* 5530 */       String sql = null;
/*      */ 
/* 5532 */       boolean shouldSendSet = false;
/*      */ 
/* 5534 */       if (getAlwaysSendSetIsolation()) {
/* 5535 */         shouldSendSet = true;
/*      */       }
/* 5537 */       else if (level != this.isolationLevel) {
/* 5538 */         shouldSendSet = true;
/*      */       }
/*      */ 
/* 5542 */       if (getUseLocalSessionState()) {
/* 5543 */         shouldSendSet = this.isolationLevel != level;
/*      */       }
/*      */ 
/* 5546 */       if (shouldSendSet) {
/* 5547 */         switch (level) {
/*      */         case 0:
/* 5549 */           throw SQLError.createSQLException("Transaction isolation level NONE not supported by MySQL", getExceptionInterceptor());
/*      */         case 2:
/* 5553 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
/*      */ 
/* 5555 */           break;
/*      */         case 1:
/* 5558 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
/*      */ 
/* 5560 */           break;
/*      */         case 4:
/* 5563 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
/*      */ 
/* 5565 */           break;
/*      */         case 8:
/* 5568 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
/*      */ 
/* 5570 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         default:
/* 5573 */           throw SQLError.createSQLException("Unsupported transaction isolation level '" + level + "'", "S1C00", getExceptionInterceptor());
/*      */         }
/*      */ 
/* 5578 */         execSQL(null, sql, -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5583 */         this.isolationLevel = level;
/*      */       }
/*      */     } else {
/* 5586 */       throw SQLError.createSQLException("Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", "S1C00", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setTypeMap(Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 5602 */     this.typeMap = map;
/*      */   }
/*      */ 
/*      */   private void setupServerForTruncationChecks() throws SQLException {
/* 5606 */     if ((getJdbcCompliantTruncation()) && 
/* 5607 */       (versionMeetsMinimum(5, 0, 2))) {
/* 5608 */       String currentSqlMode = (String)this.serverVariables.get("sql_mode");
/*      */ 
/* 5611 */       boolean strictTransTablesIsSet = StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1;
/*      */ 
/* 5613 */       if ((currentSqlMode == null) || (currentSqlMode.length() == 0) || (!strictTransTablesIsSet))
/*      */       {
/* 5615 */         StringBuffer commandBuf = new StringBuffer("SET sql_mode='");
/*      */ 
/* 5617 */         if ((currentSqlMode != null) && (currentSqlMode.length() > 0)) {
/* 5618 */           commandBuf.append(currentSqlMode);
/* 5619 */           commandBuf.append(",");
/*      */         }
/*      */ 
/* 5622 */         commandBuf.append("STRICT_TRANS_TABLES'");
/*      */ 
/* 5624 */         execSQL(null, commandBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5629 */         setJdbcCompliantTruncation(false);
/* 5630 */       } else if (strictTransTablesIsSet)
/*      */       {
/* 5632 */         setJdbcCompliantTruncation(false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shutdownServer()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5648 */       this.io.sendCommand(8, null, null, false, null, 0);
/*      */     } catch (Exception ex) {
/* 5650 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000", getExceptionInterceptor());
/*      */ 
/* 5654 */       sqlEx.initCause(ex);
/*      */ 
/* 5656 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean supportsIsolationLevel()
/*      */   {
/* 5666 */     return this.hasIsolationLevels;
/*      */   }
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers()
/*      */   {
/* 5675 */     return this.hasQuotedIdentifiers;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */   {
/* 5684 */     return this.transactionsSupported;
/*      */   }
/*      */ 
/*      */   public void unregisterStatement(Statement stmt)
/*      */   {
/* 5694 */     if (this.openStatements != null)
/* 5695 */       synchronized (this.openStatements) {
/* 5696 */         this.openStatements.remove(stmt);
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized void unsetMaxRows(Statement stmt)
/*      */     throws SQLException
/*      */   {
/* 5712 */     if (this.statementsUsingMaxRows != null) {
/* 5713 */       Object found = this.statementsUsingMaxRows.remove(stmt);
/*      */ 
/* 5715 */       if ((found != null) && (this.statementsUsingMaxRows.size() == 0))
/*      */       {
/* 5717 */         execSQL(null, "SET SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, this.database, null, false);
/*      */ 
/* 5722 */         this.maxRowsChanged = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean useAnsiQuotedIdentifiers() {
/* 5728 */     return this.useAnsiQuotes;
/*      */   }
/*      */ 
/*      */   public synchronized boolean useMaxRows()
/*      */   {
/* 5737 */     return this.maxRowsChanged;
/*      */   }
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/* 5742 */     checkClosed();
/*      */ 
/* 5744 */     return this.io.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */ 
/*      */   public CachedResultSetMetaData getCachedMetaData(String sql)
/*      */   {
/* 5762 */     if (this.resultSetMetadataCache != null) {
/* 5763 */       synchronized (this.resultSetMetadataCache) {
/* 5764 */         return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5769 */     return null;
/*      */   }
/*      */ 
/*      */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet)
/*      */     throws SQLException
/*      */   {
/* 5790 */     if (cachedMetaData == null)
/*      */     {
/* 5793 */       cachedMetaData = new CachedResultSetMetaData();
/*      */ 
/* 5797 */       resultSet.buildIndexMapping();
/* 5798 */       resultSet.initializeWithMetadata();
/*      */ 
/* 5800 */       if ((resultSet instanceof UpdatableResultSet)) {
/* 5801 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */       }
/*      */ 
/* 5804 */       resultSet.populateCachedMetaData(cachedMetaData);
/*      */ 
/* 5806 */       this.resultSetMetadataCache.put(sql, cachedMetaData);
/*      */     } else {
/* 5808 */       resultSet.initializeFromCachedMetaData(cachedMetaData);
/* 5809 */       resultSet.initializeWithMetadata();
/*      */ 
/* 5811 */       if ((resultSet instanceof UpdatableResultSet))
/* 5812 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getStatementComment()
/*      */   {
/* 5825 */     return this.statementComment;
/*      */   }
/*      */ 
/*      */   public void setStatementComment(String comment)
/*      */   {
/* 5837 */     this.statementComment = comment;
/*      */   }
/*      */ 
/*      */   public synchronized void reportQueryTime(long millisOrNanos) {
/* 5841 */     this.queryTimeCount += 1L;
/* 5842 */     this.queryTimeSum += millisOrNanos;
/* 5843 */     this.queryTimeSumSquares += millisOrNanos * millisOrNanos;
/* 5844 */     this.queryTimeMean = ((this.queryTimeMean * (this.queryTimeCount - 1L) + millisOrNanos) / this.queryTimeCount);
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAbonormallyLongQuery(long millisOrNanos)
/*      */   {
/* 5849 */     if (this.queryTimeCount < 15L) {
/* 5850 */       return false;
/*      */     }
/*      */ 
/* 5853 */     double stddev = Math.sqrt((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / this.queryTimeCount) / (this.queryTimeCount - 1L));
/*      */ 
/* 5855 */     return millisOrNanos > this.queryTimeMean + 5.0D * stddev;
/*      */   }
/*      */ 
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 5859 */     ex.init(this, this.props);
/*      */   }
/*      */ 
/*      */   public synchronized void transactionBegun() throws SQLException {
/* 5863 */     if (this.connectionLifecycleInterceptors != null) {
/* 5864 */       IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */       {
/*      */         void forEach(Extension each) throws SQLException {
/* 5867 */           ((ConnectionLifecycleInterceptor)each).transactionBegun();
/*      */         }
/*      */       };
/* 5871 */       iter.doForAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void transactionCompleted() throws SQLException {
/* 5876 */     if (this.connectionLifecycleInterceptors != null) {
/* 5877 */       IterateBlock iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */       {
/*      */         void forEach(Extension each) throws SQLException {
/* 5880 */           ((ConnectionLifecycleInterceptor)each).transactionCompleted();
/*      */         }
/*      */       };
/* 5884 */       iter.doForAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseTableName() {
/* 5889 */     return this.storesLowerCaseTableName;
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/* 5895 */     return this.exceptionInterceptor;
/*      */   }
/*      */ 
/*      */   public boolean getRequiresEscapingEncoder() {
/* 5899 */     return this.requiresEscapingEncoder;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isServerLocal() throws SQLException {
/* 5903 */     SocketFactory factory = getIO().socketFactory;
/*      */ 
/* 5905 */     if ((factory instanceof SocketMetadata)) {
/* 5906 */       return ((SocketMetadata)factory).isLocallyConnected(this);
/*      */     }
/* 5908 */     getLog().logWarn(Messages.getString("Connection.NoMetadataOnSocketFactory"));
/* 5909 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized void setSchema(String schema)
/*      */     throws SQLException
/*      */   {
/* 5915 */     checkClosed();
/*      */   }
/*      */ 
/*      */   public synchronized String getSchema() throws SQLException
/*      */   {
/* 5920 */     checkClosed();
/*      */ 
/* 5922 */     return null;
/*      */   }
/*      */ 
/*      */   public void abort(Executor executor)
/*      */     throws SQLException
/*      */   {
/* 5962 */     SecurityManager sec = System.getSecurityManager();
/*      */ 
/* 5964 */     if (sec != null) {
/* 5965 */       sec.checkPermission(ABORT_PERM);
/*      */     }
/*      */ 
/* 5968 */     if (executor == null) {
/* 5969 */       throw SQLError.createSQLException("Executor can not be null", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 5972 */     executor.execute(new Runnable()
/*      */     {
/*      */       public void run() {
/*      */         try {
/* 5976 */           ConnectionImpl.this.abortInternal();
/*      */         } catch (SQLException e) {
/* 5978 */           throw new RuntimeException(e);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public synchronized void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
/* 5986 */     SecurityManager sec = System.getSecurityManager();
/*      */ 
/* 5988 */     if (sec != null) {
/* 5989 */       sec.checkPermission(SET_NETWORK_TIMEOUT_PERM);
/*      */     }
/*      */ 
/* 5992 */     if (executor == null) {
/* 5993 */       throw SQLError.createSQLException("Executor can not be null", "S1009", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 5996 */     checkClosed();
/* 5997 */     MysqlIO mysqlIo = this.io;
/*      */ 
/* 5999 */     executor.execute(new Runnable(milliseconds, mysqlIo)
/*      */     {
/*      */       public void run() {
/* 6002 */         ConnectionImpl.this.setSocketTimeout(this.val$milliseconds);
/*      */         try {
/* 6004 */           this.val$mysqlIo.setSocketTimeout(this.val$milliseconds);
/*      */         } catch (SQLException e) {
/* 6006 */           throw new RuntimeException(e);
/*      */         }
/*      */       } } );
/*      */   }
/*      */ 
/*      */   public synchronized int getNetworkTimeout() throws SQLException {
/* 6013 */     checkClosed();
/* 6014 */     return getSocketTimeout();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  288 */     mapTransIsolationNameToValue = new HashMap(8);
/*  289 */     mapTransIsolationNameToValue.put("READ-UNCOMMITED", Integer.valueOf(1));
/*  290 */     mapTransIsolationNameToValue.put("READ-UNCOMMITTED", Integer.valueOf(1));
/*  291 */     mapTransIsolationNameToValue.put("READ-COMMITTED", Integer.valueOf(2));
/*  292 */     mapTransIsolationNameToValue.put("REPEATABLE-READ", Integer.valueOf(4));
/*  293 */     mapTransIsolationNameToValue.put("SERIALIZABLE", Integer.valueOf(8));
/*      */ 
/*  295 */     if (Util.isJdbc4())
/*      */       try {
/*  297 */         JDBC_4_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4Connection").getConstructor(new Class[] { String.class, Integer.TYPE, Properties.class, String.class, String.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  302 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  304 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  306 */         throw new RuntimeException(e);
/*      */       }
/*      */     else {
/*  309 */       JDBC_4_CONNECTION_CTOR = null;
/*      */     }
/*      */ 
/*  410 */     random = new Random();
/*      */   }
/*      */ 
/*      */   static class CompoundCacheKey
/*      */   {
/*      */     String componentOne;
/*      */     String componentTwo;
/*      */     int hashCode;
/*      */ 
/*      */     CompoundCacheKey(String partOne, String partTwo)
/*      */     {
/*  177 */       this.componentOne = partOne;
/*  178 */       this.componentTwo = partTwo;
/*      */ 
/*  182 */       this.hashCode = ((this.componentOne != null ? this.componentOne : "") + this.componentTwo).hashCode();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  192 */       if ((obj instanceof CompoundCacheKey)) {
/*  193 */         CompoundCacheKey another = (CompoundCacheKey)obj;
/*      */ 
/*  195 */         boolean firstPartEqual = false;
/*      */ 
/*  197 */         if (this.componentOne == null)
/*  198 */           firstPartEqual = another.componentOne == null;
/*      */         else {
/*  200 */           firstPartEqual = this.componentOne.equals(another.componentOne);
/*      */         }
/*      */ 
/*  204 */         return (firstPartEqual) && (this.componentTwo.equals(another.componentTwo));
/*      */       }
/*      */ 
/*  208 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  217 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ 
/*      */   class ExceptionInterceptorChain
/*      */     implements ExceptionInterceptor
/*      */   {
/*      */     List<Extension> interceptors;
/*      */ 
/*      */     ExceptionInterceptorChain(String interceptorClasses)
/*      */       throws SQLException
/*      */     {
/*  123 */       this.interceptors = Util.loadExtensions(ConnectionImpl.this, ConnectionImpl.this.props, interceptorClasses, "Connection.BadExceptionInterceptor", this);
/*      */     }
/*      */ 
/*      */     void addRingZero(ExceptionInterceptor interceptor) throws SQLException {
/*  127 */       this.interceptors.add(0, interceptor);
/*      */     }
/*      */ 
/*      */     public SQLException interceptException(SQLException sqlEx, Connection conn) {
/*  131 */       if (this.interceptors != null) {
/*  132 */         Iterator iter = this.interceptors.iterator();
/*      */ 
/*  134 */         while (iter.hasNext()) {
/*  135 */           sqlEx = ((ExceptionInterceptor)iter.next()).interceptException(sqlEx, ConnectionImpl.this);
/*      */         }
/*      */       }
/*      */ 
/*  139 */       return sqlEx;
/*      */     }
/*      */ 
/*      */     public void destroy() {
/*  143 */       if (this.interceptors != null) {
/*  144 */         Iterator iter = this.interceptors.iterator();
/*      */ 
/*  146 */         while (iter.hasNext())
/*  147 */           ((ExceptionInterceptor)iter.next()).destroy();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init(Connection conn, Properties properties)
/*      */       throws SQLException
/*      */     {
/*  154 */       if (this.interceptors != null) {
/*  155 */         Iterator iter = this.interceptors.iterator();
/*      */ 
/*  157 */         while (iter.hasNext())
/*  158 */           ((ExceptionInterceptor)iter.next()).init(conn, properties);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionImpl
 * JD-Core Version:    0.6.0
 */