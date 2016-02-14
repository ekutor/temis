/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.StandardLogger;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.sql.DriverPropertyInfo;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TreeMap;
/*      */ import javax.naming.RefAddr;
/*      */ import javax.naming.Reference;
/*      */ import javax.naming.StringRefAddr;
/*      */ 
/*      */ public class ConnectionPropertiesImpl
/*      */   implements Serializable, ConnectionProperties
/*      */ {
/*      */   private static final long serialVersionUID = 4257801713007640580L;
/*  615 */   private static final String CONNECTION_AND_AUTH_CATEGORY = Messages.getString("ConnectionProperties.categoryConnectionAuthentication");
/*      */ 
/*  617 */   private static final String NETWORK_CATEGORY = Messages.getString("ConnectionProperties.categoryNetworking");
/*      */ 
/*  619 */   private static final String DEBUGING_PROFILING_CATEGORY = Messages.getString("ConnectionProperties.categoryDebuggingProfiling");
/*      */ 
/*  621 */   private static final String HA_CATEGORY = Messages.getString("ConnectionProperties.categorryHA");
/*      */ 
/*  623 */   private static final String MISC_CATEGORY = Messages.getString("ConnectionProperties.categoryMisc");
/*      */ 
/*  625 */   private static final String PERFORMANCE_CATEGORY = Messages.getString("ConnectionProperties.categoryPerformance");
/*      */ 
/*  627 */   private static final String SECURITY_CATEGORY = Messages.getString("ConnectionProperties.categorySecurity");
/*      */ 
/*  629 */   private static final String[] PROPERTY_CATEGORIES = { CONNECTION_AND_AUTH_CATEGORY, NETWORK_CATEGORY, HA_CATEGORY, SECURITY_CATEGORY, PERFORMANCE_CATEGORY, DEBUGING_PROFILING_CATEGORY, MISC_CATEGORY };
/*      */ 
/*  634 */   private static final ArrayList<Field> PROPERTY_LIST = new ArrayList();
/*      */ 
/*  639 */   private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
/*      */   private BooleanConnectionProperty allowLoadLocalInfile;
/*      */   private BooleanConnectionProperty allowMultiQueries;
/*      */   private BooleanConnectionProperty allowNanAndInf;
/*      */   private BooleanConnectionProperty allowUrlInLocalInfile;
/*      */   private BooleanConnectionProperty alwaysSendSetIsolation;
/*      */   private BooleanConnectionProperty autoClosePStmtStreams;
/*      */   private BooleanConnectionProperty autoDeserialize;
/*      */   private BooleanConnectionProperty autoGenerateTestcaseScript;
/*      */   private boolean autoGenerateTestcaseScriptAsBoolean;
/*      */   private BooleanConnectionProperty autoReconnect;
/*      */   private BooleanConnectionProperty autoReconnectForPools;
/*      */   private boolean autoReconnectForPoolsAsBoolean;
/*      */   private MemorySizeConnectionProperty blobSendChunkSize;
/*      */   private BooleanConnectionProperty autoSlowLog;
/*      */   private BooleanConnectionProperty blobsAreStrings;
/*      */   private BooleanConnectionProperty functionsNeverReturnBlobs;
/*      */   private BooleanConnectionProperty cacheCallableStatements;
/*      */   private BooleanConnectionProperty cachePreparedStatements;
/*      */   private BooleanConnectionProperty cacheResultSetMetadata;
/*      */   private boolean cacheResultSetMetaDataAsBoolean;
/*      */   private StringConnectionProperty serverConfigCacheFactory;
/*      */   private BooleanConnectionProperty cacheServerConfiguration;
/*      */   private IntegerConnectionProperty callableStatementCacheSize;
/*      */   private BooleanConnectionProperty capitalizeTypeNames;
/*      */   private StringConnectionProperty characterEncoding;
/*      */   private String characterEncodingAsString;
/*      */   protected boolean characterEncodingIsAliasForSjis;
/*      */   private StringConnectionProperty characterSetResults;
/*      */   private StringConnectionProperty clientInfoProvider;
/*      */   private BooleanConnectionProperty clobberStreamingResults;
/*      */   private StringConnectionProperty clobCharacterEncoding;
/*      */   private BooleanConnectionProperty compensateOnDuplicateKeyUpdateCounts;
/*      */   private StringConnectionProperty connectionCollation;
/*      */   private StringConnectionProperty connectionLifecycleInterceptors;
/*      */   private IntegerConnectionProperty connectTimeout;
/*      */   private BooleanConnectionProperty continueBatchOnError;
/*      */   private BooleanConnectionProperty createDatabaseIfNotExist;
/*      */   private IntegerConnectionProperty defaultFetchSize;
/*      */   private BooleanConnectionProperty detectServerPreparedStmts;
/*      */   private BooleanConnectionProperty dontTrackOpenResources;
/*      */   private BooleanConnectionProperty dumpQueriesOnException;
/*      */   private BooleanConnectionProperty dynamicCalendars;
/*      */   private BooleanConnectionProperty elideSetAutoCommits;
/*      */   private BooleanConnectionProperty emptyStringsConvertToZero;
/*      */   private BooleanConnectionProperty emulateLocators;
/*      */   private BooleanConnectionProperty emulateUnsupportedPstmts;
/*      */   private BooleanConnectionProperty enablePacketDebug;
/*      */   private BooleanConnectionProperty enableQueryTimeouts;
/*      */   private BooleanConnectionProperty explainSlowQueries;
/*      */   private StringConnectionProperty exceptionInterceptors;
/*      */   private BooleanConnectionProperty failOverReadOnly;
/*      */   private BooleanConnectionProperty gatherPerformanceMetrics;
/*      */   private BooleanConnectionProperty generateSimpleParameterMetadata;
/*      */   private boolean highAvailabilityAsBoolean;
/*      */   private BooleanConnectionProperty holdResultsOpenOverStatementClose;
/*      */   private BooleanConnectionProperty includeInnodbStatusInDeadlockExceptions;
/*      */   private BooleanConnectionProperty includeThreadDumpInDeadlockExceptions;
/*      */   private BooleanConnectionProperty includeThreadNamesAsStatementComment;
/*      */   private BooleanConnectionProperty ignoreNonTxTables;
/*      */   private IntegerConnectionProperty initialTimeout;
/*      */   private BooleanConnectionProperty isInteractiveClient;
/*      */   private BooleanConnectionProperty jdbcCompliantTruncation;
/*      */   private boolean jdbcCompliantTruncationForReads;
/*      */   protected MemorySizeConnectionProperty largeRowSizeThreshold;
/*      */   private StringConnectionProperty loadBalanceStrategy;
/*      */   private IntegerConnectionProperty loadBalanceBlacklistTimeout;
/*      */   private IntegerConnectionProperty loadBalancePingTimeout;
/*      */   private BooleanConnectionProperty loadBalanceValidateConnectionOnSwapServer;
/*      */   private StringConnectionProperty loadBalanceConnectionGroup;
/*      */   private StringConnectionProperty loadBalanceExceptionChecker;
/*      */   private StringConnectionProperty loadBalanceSQLStateFailover;
/*      */   private StringConnectionProperty loadBalanceSQLExceptionSubclassFailover;
/*      */   private BooleanConnectionProperty loadBalanceEnableJMX;
/*      */   private StringConnectionProperty loadBalanceAutoCommitStatementRegex;
/*      */   private IntegerConnectionProperty loadBalanceAutoCommitStatementThreshold;
/*      */   private StringConnectionProperty localSocketAddress;
/*      */   private MemorySizeConnectionProperty locatorFetchBufferSize;
/*      */   private StringConnectionProperty loggerClassName;
/*      */   private BooleanConnectionProperty logSlowQueries;
/*      */   private BooleanConnectionProperty logXaCommands;
/*      */   private BooleanConnectionProperty maintainTimeStats;
/*      */   private boolean maintainTimeStatsAsBoolean;
/*      */   private IntegerConnectionProperty maxQuerySizeToLog;
/*      */   private IntegerConnectionProperty maxReconnects;
/*      */   private IntegerConnectionProperty retriesAllDown;
/*      */   private IntegerConnectionProperty maxRows;
/*      */   private int maxRowsAsInt;
/*      */   private IntegerConnectionProperty metadataCacheSize;
/*      */   private IntegerConnectionProperty netTimeoutForStreamingResults;
/*      */   private BooleanConnectionProperty noAccessToProcedureBodies;
/*      */   private BooleanConnectionProperty noDatetimeStringSync;
/*      */   private BooleanConnectionProperty noTimezoneConversionForTimeType;
/*      */   private BooleanConnectionProperty nullCatalogMeansCurrent;
/*      */   private BooleanConnectionProperty nullNamePatternMatchesAll;
/*      */   private IntegerConnectionProperty packetDebugBufferSize;
/*      */   private BooleanConnectionProperty padCharsWithSpace;
/*      */   private BooleanConnectionProperty paranoid;
/*      */   private BooleanConnectionProperty pedantic;
/*      */   private BooleanConnectionProperty pinGlobalTxToPhysicalConnection;
/*      */   private BooleanConnectionProperty populateInsertRowWithDefaultValues;
/*      */   private IntegerConnectionProperty preparedStatementCacheSize;
/*      */   private IntegerConnectionProperty preparedStatementCacheSqlLimit;
/*      */   private StringConnectionProperty parseInfoCacheFactory;
/*      */   private BooleanConnectionProperty processEscapeCodesForPrepStmts;
/*      */   private StringConnectionProperty profilerEventHandler;
/*      */   private StringConnectionProperty profileSql;
/*      */   private BooleanConnectionProperty profileSQL;
/*      */   private boolean profileSQLAsBoolean;
/*      */   private StringConnectionProperty propertiesTransform;
/*      */   private IntegerConnectionProperty queriesBeforeRetryMaster;
/*      */   private BooleanConnectionProperty queryTimeoutKillsConnection;
/*      */   private BooleanConnectionProperty reconnectAtTxEnd;
/*      */   private boolean reconnectTxAtEndAsBoolean;
/*      */   private BooleanConnectionProperty relaxAutoCommit;
/*      */   private IntegerConnectionProperty reportMetricsIntervalMillis;
/*      */   private BooleanConnectionProperty requireSSL;
/*      */   private StringConnectionProperty resourceId;
/*      */   private IntegerConnectionProperty resultSetSizeThreshold;
/*      */   private BooleanConnectionProperty retainStatementAfterResultSetClose;
/*      */   private BooleanConnectionProperty rewriteBatchedStatements;
/*      */   private BooleanConnectionProperty rollbackOnPooledClose;
/*      */   private BooleanConnectionProperty roundRobinLoadBalance;
/*      */   private BooleanConnectionProperty runningCTS13;
/*      */   private IntegerConnectionProperty secondsBeforeRetryMaster;
/*      */   private IntegerConnectionProperty selfDestructOnPingSecondsLifetime;
/*      */   private IntegerConnectionProperty selfDestructOnPingMaxOperations;
/*      */   private StringConnectionProperty serverTimezone;
/*      */   private StringConnectionProperty sessionVariables;
/*      */   private IntegerConnectionProperty slowQueryThresholdMillis;
/*      */   private LongConnectionProperty slowQueryThresholdNanos;
/*      */   private StringConnectionProperty socketFactoryClassName;
/*      */   private IntegerConnectionProperty socketTimeout;
/*      */   private StringConnectionProperty statementInterceptors;
/*      */   private BooleanConnectionProperty strictFloatingPoint;
/*      */   private BooleanConnectionProperty strictUpdates;
/*      */   private BooleanConnectionProperty overrideSupportsIntegrityEnhancementFacility;
/*      */   private BooleanConnectionProperty tcpNoDelay;
/*      */   private BooleanConnectionProperty tcpKeepAlive;
/*      */   private IntegerConnectionProperty tcpRcvBuf;
/*      */   private IntegerConnectionProperty tcpSndBuf;
/*      */   private IntegerConnectionProperty tcpTrafficClass;
/*      */   private BooleanConnectionProperty tinyInt1isBit;
/*      */   private BooleanConnectionProperty traceProtocol;
/*      */   private BooleanConnectionProperty treatUtilDateAsTimestamp;
/*      */   private BooleanConnectionProperty transformedBitIsBoolean;
/*      */   private BooleanConnectionProperty useBlobToStoreUTF8OutsideBMP;
/*      */   private StringConnectionProperty utf8OutsideBmpExcludedColumnNamePattern;
/*      */   private StringConnectionProperty utf8OutsideBmpIncludedColumnNamePattern;
/*      */   private BooleanConnectionProperty useCompression;
/*      */   private BooleanConnectionProperty useColumnNamesInFindColumn;
/*      */   private StringConnectionProperty useConfigs;
/*      */   private BooleanConnectionProperty useCursorFetch;
/*      */   private BooleanConnectionProperty useDynamicCharsetInfo;
/*      */   private BooleanConnectionProperty useDirectRowUnpack;
/*      */   private BooleanConnectionProperty useFastIntParsing;
/*      */   private BooleanConnectionProperty useFastDateParsing;
/*      */   private BooleanConnectionProperty useHostsInPrivileges;
/*      */   private BooleanConnectionProperty useInformationSchema;
/*      */   private BooleanConnectionProperty useJDBCCompliantTimezoneShift;
/*      */   private BooleanConnectionProperty useLocalSessionState;
/*      */   private BooleanConnectionProperty useLocalTransactionState;
/*      */   private BooleanConnectionProperty useLegacyDatetimeCode;
/*      */   private BooleanConnectionProperty useNanosForElapsedTime;
/*      */   private BooleanConnectionProperty useOldAliasMetadataBehavior;
/*      */   private BooleanConnectionProperty useOldUTF8Behavior;
/*      */   private boolean useOldUTF8BehaviorAsBoolean;
/*      */   private BooleanConnectionProperty useOnlyServerErrorMessages;
/*      */   private BooleanConnectionProperty useReadAheadInput;
/*      */   private BooleanConnectionProperty useSqlStateCodes;
/*      */   private BooleanConnectionProperty useSSL;
/*      */   private BooleanConnectionProperty useSSPSCompatibleTimezoneShift;
/*      */   private BooleanConnectionProperty useStreamLengthsInPrepStmts;
/*      */   private BooleanConnectionProperty useTimezone;
/*      */   private BooleanConnectionProperty useUltraDevWorkAround;
/*      */   private BooleanConnectionProperty useUnbufferedInput;
/*      */   private BooleanConnectionProperty useUnicode;
/*      */   private boolean useUnicodeAsBoolean;
/*      */   private BooleanConnectionProperty useUsageAdvisor;
/*      */   private boolean useUsageAdvisorAsBoolean;
/*      */   private BooleanConnectionProperty yearIsDateType;
/*      */   private StringConnectionProperty zeroDateTimeBehavior;
/*      */   private BooleanConnectionProperty useJvmCharsetConverters;
/*      */   private BooleanConnectionProperty useGmtMillisForDatetimes;
/*      */   private BooleanConnectionProperty dumpMetadataOnColumnNotFound;
/*      */   private StringConnectionProperty clientCertificateKeyStoreUrl;
/*      */   private StringConnectionProperty trustCertificateKeyStoreUrl;
/*      */   private StringConnectionProperty clientCertificateKeyStoreType;
/*      */   private StringConnectionProperty clientCertificateKeyStorePassword;
/*      */   private StringConnectionProperty trustCertificateKeyStoreType;
/*      */   private StringConnectionProperty trustCertificateKeyStorePassword;
/*      */   private BooleanConnectionProperty verifyServerCertificate;
/*      */   private BooleanConnectionProperty useAffectedRows;
/*      */   private StringConnectionProperty passwordCharacterEncoding;
/*      */   private IntegerConnectionProperty maxAllowedPacket;
/*      */   private StringConnectionProperty authenticationPlugins;
/*      */   private StringConnectionProperty disabledAuthenticationPlugins;
/*      */   private StringConnectionProperty defaultAuthenticationPlugin;
/*      */   private BooleanConnectionProperty disconnectOnExpiredPasswords;
/*      */ 
/*      */   public ConnectionPropertiesImpl()
/*      */   {
/*  691 */     this.allowLoadLocalInfile = new BooleanConnectionProperty("allowLoadLocalInfile", true, Messages.getString("ConnectionProperties.loadDataLocal"), "3.0.3", SECURITY_CATEGORY, 2147483647);
/*      */ 
/*  697 */     this.allowMultiQueries = new BooleanConnectionProperty("allowMultiQueries", false, Messages.getString("ConnectionProperties.allowMultiQueries"), "3.1.1", SECURITY_CATEGORY, 1);
/*      */ 
/*  703 */     this.allowNanAndInf = new BooleanConnectionProperty("allowNanAndInf", false, Messages.getString("ConnectionProperties.allowNANandINF"), "3.1.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*  709 */     this.allowUrlInLocalInfile = new BooleanConnectionProperty("allowUrlInLocalInfile", false, Messages.getString("ConnectionProperties.allowUrlInLoadLocal"), "3.1.4", SECURITY_CATEGORY, 2147483647);
/*      */ 
/*  715 */     this.alwaysSendSetIsolation = new BooleanConnectionProperty("alwaysSendSetIsolation", true, Messages.getString("ConnectionProperties.alwaysSendSetIsolation"), "3.1.7", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/*  721 */     this.autoClosePStmtStreams = new BooleanConnectionProperty("autoClosePStmtStreams", false, Messages.getString("ConnectionProperties.autoClosePstmtStreams"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/*  729 */     this.autoDeserialize = new BooleanConnectionProperty("autoDeserialize", false, Messages.getString("ConnectionProperties.autoDeserialize"), "3.1.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*  735 */     this.autoGenerateTestcaseScript = new BooleanConnectionProperty("autoGenerateTestcaseScript", false, Messages.getString("ConnectionProperties.autoGenerateTestcaseScript"), "3.1.9", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  740 */     this.autoGenerateTestcaseScriptAsBoolean = false;
/*      */ 
/*  742 */     this.autoReconnect = new BooleanConnectionProperty("autoReconnect", false, Messages.getString("ConnectionProperties.autoReconnect"), "1.1", HA_CATEGORY, 0);
/*      */ 
/*  748 */     this.autoReconnectForPools = new BooleanConnectionProperty("autoReconnectForPools", false, Messages.getString("ConnectionProperties.autoReconnectForPools"), "3.1.3", HA_CATEGORY, 1);
/*      */ 
/*  754 */     this.autoReconnectForPoolsAsBoolean = false;
/*      */ 
/*  756 */     this.blobSendChunkSize = new MemorySizeConnectionProperty("blobSendChunkSize", 1048576, 1, 2147483647, Messages.getString("ConnectionProperties.blobSendChunkSize"), "3.1.9", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  764 */     this.autoSlowLog = new BooleanConnectionProperty("autoSlowLog", true, Messages.getString("ConnectionProperties.autoSlowLog"), "5.1.4", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  769 */     this.blobsAreStrings = new BooleanConnectionProperty("blobsAreStrings", false, "Should the driver always treat BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  775 */     this.functionsNeverReturnBlobs = new BooleanConnectionProperty("functionsNeverReturnBlobs", false, "Should the driver always treat data from functions returning BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  781 */     this.cacheCallableStatements = new BooleanConnectionProperty("cacheCallableStmts", false, Messages.getString("ConnectionProperties.cacheCallableStatements"), "3.1.2", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  786 */     this.cachePreparedStatements = new BooleanConnectionProperty("cachePrepStmts", false, Messages.getString("ConnectionProperties.cachePrepStmts"), "3.0.10", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  792 */     this.cacheResultSetMetadata = new BooleanConnectionProperty("cacheResultSetMetadata", false, Messages.getString("ConnectionProperties.cacheRSMetadata"), "3.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  800 */     this.serverConfigCacheFactory = new StringConnectionProperty("serverConfigCacheFactory", PerVmServerConfigCacheFactory.class.getName(), Messages.getString("ConnectionProperties.serverConfigCacheFactory"), "5.1.1", PERFORMANCE_CATEGORY, 12);
/*      */ 
/*  803 */     this.cacheServerConfiguration = new BooleanConnectionProperty("cacheServerConfiguration", false, Messages.getString("ConnectionProperties.cacheServerConfiguration"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  809 */     this.callableStatementCacheSize = new IntegerConnectionProperty("callableStmtCacheSize", 100, 0, 2147483647, Messages.getString("ConnectionProperties.callableStmtCacheSize"), "3.1.2", PERFORMANCE_CATEGORY, 5);
/*      */ 
/*  817 */     this.capitalizeTypeNames = new BooleanConnectionProperty("capitalizeTypeNames", true, Messages.getString("ConnectionProperties.capitalizeTypeNames"), "2.0.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*  823 */     this.characterEncoding = new StringConnectionProperty("characterEncoding", null, Messages.getString("ConnectionProperties.characterEncoding"), "1.1g", MISC_CATEGORY, 5);
/*      */ 
/*  829 */     this.characterEncodingAsString = null;
/*      */ 
/*  831 */     this.characterEncodingIsAliasForSjis = false;
/*      */ 
/*  833 */     this.characterSetResults = new StringConnectionProperty("characterSetResults", null, Messages.getString("ConnectionProperties.characterSetResults"), "3.0.13", MISC_CATEGORY, 6);
/*      */ 
/*  838 */     this.clientInfoProvider = new StringConnectionProperty("clientInfoProvider", "com.mysql.jdbc.JDBC4CommentClientInfoProvider", Messages.getString("ConnectionProperties.clientInfoProvider"), "5.1.0", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  844 */     this.clobberStreamingResults = new BooleanConnectionProperty("clobberStreamingResults", false, Messages.getString("ConnectionProperties.clobberStreamingResults"), "3.0.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*  850 */     this.clobCharacterEncoding = new StringConnectionProperty("clobCharacterEncoding", null, Messages.getString("ConnectionProperties.clobCharacterEncoding"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*  856 */     this.compensateOnDuplicateKeyUpdateCounts = new BooleanConnectionProperty("compensateOnDuplicateKeyUpdateCounts", false, Messages.getString("ConnectionProperties.compensateOnDuplicateKeyUpdateCounts"), "5.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*  861 */     this.connectionCollation = new StringConnectionProperty("connectionCollation", null, Messages.getString("ConnectionProperties.connectionCollation"), "3.0.13", MISC_CATEGORY, 7);
/*      */ 
/*  867 */     this.connectionLifecycleInterceptors = new StringConnectionProperty("connectionLifecycleInterceptors", null, Messages.getString("ConnectionProperties.connectionLifecycleInterceptors"), "5.1.4", CONNECTION_AND_AUTH_CATEGORY, 2147483647);
/*      */ 
/*  873 */     this.connectTimeout = new IntegerConnectionProperty("connectTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.connectTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 9);
/*      */ 
/*  878 */     this.continueBatchOnError = new BooleanConnectionProperty("continueBatchOnError", true, Messages.getString("ConnectionProperties.continueBatchOnError"), "3.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/*  884 */     this.createDatabaseIfNotExist = new BooleanConnectionProperty("createDatabaseIfNotExist", false, Messages.getString("ConnectionProperties.createDatabaseIfNotExist"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/*  890 */     this.defaultFetchSize = new IntegerConnectionProperty("defaultFetchSize", 0, Messages.getString("ConnectionProperties.defaultFetchSize"), "3.1.9", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  896 */     this.detectServerPreparedStmts = new BooleanConnectionProperty("useServerPrepStmts", false, Messages.getString("ConnectionProperties.useServerPrepStmts"), "3.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*  902 */     this.dontTrackOpenResources = new BooleanConnectionProperty("dontTrackOpenResources", false, Messages.getString("ConnectionProperties.dontTrackOpenResources"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  908 */     this.dumpQueriesOnException = new BooleanConnectionProperty("dumpQueriesOnException", false, Messages.getString("ConnectionProperties.dumpQueriesOnException"), "3.1.3", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  914 */     this.dynamicCalendars = new BooleanConnectionProperty("dynamicCalendars", false, Messages.getString("ConnectionProperties.dynamicCalendars"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  920 */     this.elideSetAutoCommits = new BooleanConnectionProperty("elideSetAutoCommits", false, Messages.getString("ConnectionProperties.eliseSetAutoCommit"), "3.1.3", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  926 */     this.emptyStringsConvertToZero = new BooleanConnectionProperty("emptyStringsConvertToZero", true, Messages.getString("ConnectionProperties.emptyStringsConvertToZero"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  931 */     this.emulateLocators = new BooleanConnectionProperty("emulateLocators", false, Messages.getString("ConnectionProperties.emulateLocators"), "3.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/*  935 */     this.emulateUnsupportedPstmts = new BooleanConnectionProperty("emulateUnsupportedPstmts", true, Messages.getString("ConnectionProperties.emulateUnsupportedPstmts"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/*  941 */     this.enablePacketDebug = new BooleanConnectionProperty("enablePacketDebug", false, Messages.getString("ConnectionProperties.enablePacketDebug"), "3.1.3", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  947 */     this.enableQueryTimeouts = new BooleanConnectionProperty("enableQueryTimeouts", true, Messages.getString("ConnectionProperties.enableQueryTimeouts"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  954 */     this.explainSlowQueries = new BooleanConnectionProperty("explainSlowQueries", false, Messages.getString("ConnectionProperties.explainSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  960 */     this.exceptionInterceptors = new StringConnectionProperty("exceptionInterceptors", null, Messages.getString("ConnectionProperties.exceptionInterceptors"), "5.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/*  967 */     this.failOverReadOnly = new BooleanConnectionProperty("failOverReadOnly", true, Messages.getString("ConnectionProperties.failoverReadOnly"), "3.0.12", HA_CATEGORY, 2);
/*      */ 
/*  973 */     this.gatherPerformanceMetrics = new BooleanConnectionProperty("gatherPerfMetrics", false, Messages.getString("ConnectionProperties.gatherPerfMetrics"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 1);
/*      */ 
/*  979 */     this.generateSimpleParameterMetadata = new BooleanConnectionProperty("generateSimpleParameterMetadata", false, Messages.getString("ConnectionProperties.generateSimpleParameterMetadata"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/*  982 */     this.highAvailabilityAsBoolean = false;
/*      */ 
/*  984 */     this.holdResultsOpenOverStatementClose = new BooleanConnectionProperty("holdResultsOpenOverStatementClose", false, Messages.getString("ConnectionProperties.holdRSOpenOverStmtClose"), "3.1.7", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/*  990 */     this.includeInnodbStatusInDeadlockExceptions = new BooleanConnectionProperty("includeInnodbStatusInDeadlockExceptions", false, Messages.getString("ConnectionProperties.includeInnodbStatusInDeadlockExceptions"), "5.0.7", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/*  996 */     this.includeThreadDumpInDeadlockExceptions = new BooleanConnectionProperty("includeThreadDumpInDeadlockExceptions", false, Messages.getString("ConnectionProperties.includeThreadDumpInDeadlockExceptions"), "5.1.15", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1002 */     this.includeThreadNamesAsStatementComment = new BooleanConnectionProperty("includeThreadNamesAsStatementComment", false, Messages.getString("ConnectionProperties.includeThreadNamesAsStatementComment"), "5.1.15", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1008 */     this.ignoreNonTxTables = new BooleanConnectionProperty("ignoreNonTxTables", false, Messages.getString("ConnectionProperties.ignoreNonTxTables"), "3.0.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1014 */     this.initialTimeout = new IntegerConnectionProperty("initialTimeout", 2, 1, 2147483647, Messages.getString("ConnectionProperties.initialTimeout"), "1.1", HA_CATEGORY, 5);
/*      */ 
/* 1019 */     this.isInteractiveClient = new BooleanConnectionProperty("interactiveClient", false, Messages.getString("ConnectionProperties.interactiveClient"), "3.1.0", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1025 */     this.jdbcCompliantTruncation = new BooleanConnectionProperty("jdbcCompliantTruncation", true, Messages.getString("ConnectionProperties.jdbcCompliantTruncation"), "3.1.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1031 */     this.jdbcCompliantTruncationForReads = this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */ 
/* 1034 */     this.largeRowSizeThreshold = new MemorySizeConnectionProperty("largeRowSizeThreshold", 2048, 0, 2147483647, Messages.getString("ConnectionProperties.largeRowSizeThreshold"), "5.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1039 */     this.loadBalanceStrategy = new StringConnectionProperty("loadBalanceStrategy", "random", null, Messages.getString("ConnectionProperties.loadBalanceStrategy"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1046 */     this.loadBalanceBlacklistTimeout = new IntegerConnectionProperty("loadBalanceBlacklistTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.loadBalanceBlacklistTimeout"), "5.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1052 */     this.loadBalancePingTimeout = new IntegerConnectionProperty("loadBalancePingTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.loadBalancePingTimeout"), "5.1.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1058 */     this.loadBalanceValidateConnectionOnSwapServer = new BooleanConnectionProperty("loadBalanceValidateConnectionOnSwapServer", false, Messages.getString("ConnectionProperties.loadBalanceValidateConnectionOnSwapServer"), "5.1.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1064 */     this.loadBalanceConnectionGroup = new StringConnectionProperty("loadBalanceConnectionGroup", null, Messages.getString("ConnectionProperties.loadBalanceConnectionGroup"), "5.1.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1070 */     this.loadBalanceExceptionChecker = new StringConnectionProperty("loadBalanceExceptionChecker", "com.mysql.jdbc.StandardLoadBalanceExceptionChecker", null, Messages.getString("ConnectionProperties.loadBalanceExceptionChecker"), "5.1.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1077 */     this.loadBalanceSQLStateFailover = new StringConnectionProperty("loadBalanceSQLStateFailover", null, Messages.getString("ConnectionProperties.loadBalanceSQLStateFailover"), "5.1.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1083 */     this.loadBalanceSQLExceptionSubclassFailover = new StringConnectionProperty("loadBalanceSQLExceptionSubclassFailover", null, Messages.getString("ConnectionProperties.loadBalanceSQLExceptionSubclassFailover"), "5.1.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1089 */     this.loadBalanceEnableJMX = new BooleanConnectionProperty("loadBalanceEnableJMX", false, Messages.getString("ConnectionProperties.loadBalanceEnableJMX"), "5.1.13", MISC_CATEGORY, 2147483647);
/*      */ 
/* 1095 */     this.loadBalanceAutoCommitStatementRegex = new StringConnectionProperty("loadBalanceAutoCommitStatementRegex", null, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementRegex"), "5.1.15", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1101 */     this.loadBalanceAutoCommitStatementThreshold = new IntegerConnectionProperty("loadBalanceAutoCommitStatementThreshold", 0, 0, 2147483647, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementThreshold"), "5.1.15", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1108 */     this.localSocketAddress = new StringConnectionProperty("localSocketAddress", null, Messages.getString("ConnectionProperties.localSocketAddress"), "5.0.5", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1112 */     this.locatorFetchBufferSize = new MemorySizeConnectionProperty("locatorFetchBufferSize", 1048576, 0, 2147483647, Messages.getString("ConnectionProperties.locatorFetchBufferSize"), "3.2.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1120 */     this.loggerClassName = new StringConnectionProperty("logger", STANDARD_LOGGER_NAME, Messages.getString("ConnectionProperties.logger", new Object[] { Log.class.getName(), STANDARD_LOGGER_NAME }), "3.1.1", DEBUGING_PROFILING_CATEGORY, 0);
/*      */ 
/* 1126 */     this.logSlowQueries = new BooleanConnectionProperty("logSlowQueries", false, Messages.getString("ConnectionProperties.logSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1132 */     this.logXaCommands = new BooleanConnectionProperty("logXaCommands", false, Messages.getString("ConnectionProperties.logXaCommands"), "5.0.5", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1138 */     this.maintainTimeStats = new BooleanConnectionProperty("maintainTimeStats", true, Messages.getString("ConnectionProperties.maintainTimeStats"), "3.1.9", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/* 1144 */     this.maintainTimeStatsAsBoolean = true;
/*      */ 
/* 1146 */     this.maxQuerySizeToLog = new IntegerConnectionProperty("maxQuerySizeToLog", 2048, 0, 2147483647, Messages.getString("ConnectionProperties.maxQuerySizeToLog"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 4);
/*      */ 
/* 1154 */     this.maxReconnects = new IntegerConnectionProperty("maxReconnects", 3, 1, 2147483647, Messages.getString("ConnectionProperties.maxReconnects"), "1.1", HA_CATEGORY, 4);
/*      */ 
/* 1162 */     this.retriesAllDown = new IntegerConnectionProperty("retriesAllDown", 120, 0, 2147483647, Messages.getString("ConnectionProperties.retriesAllDown"), "5.1.6", HA_CATEGORY, 4);
/*      */ 
/* 1170 */     this.maxRows = new IntegerConnectionProperty("maxRows", -1, -1, 2147483647, Messages.getString("ConnectionProperties.maxRows"), Messages.getString("ConnectionProperties.allVersions"), MISC_CATEGORY, -2147483648);
/*      */ 
/* 1175 */     this.maxRowsAsInt = -1;
/*      */ 
/* 1177 */     this.metadataCacheSize = new IntegerConnectionProperty("metadataCacheSize", 50, 1, 2147483647, Messages.getString("ConnectionProperties.metadataCacheSize"), "3.1.1", PERFORMANCE_CATEGORY, 5);
/*      */ 
/* 1185 */     this.netTimeoutForStreamingResults = new IntegerConnectionProperty("netTimeoutForStreamingResults", 600, 0, 2147483647, Messages.getString("ConnectionProperties.netTimeoutForStreamingResults"), "5.1.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1191 */     this.noAccessToProcedureBodies = new BooleanConnectionProperty("noAccessToProcedureBodies", false, "When determining procedure parameter types for CallableStatements, and the connected user  can't access procedure bodies through \"SHOW CREATE PROCEDURE\" or select on mysql.proc  should the driver instead create basic metadata (all parameters reported as IN VARCHARs, but allowing registerOutParameter() to be called on them anyway) instead  of throwing an exception?", "5.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1201 */     this.noDatetimeStringSync = new BooleanConnectionProperty("noDatetimeStringSync", false, Messages.getString("ConnectionProperties.noDatetimeStringSync"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1207 */     this.noTimezoneConversionForTimeType = new BooleanConnectionProperty("noTimezoneConversionForTimeType", false, Messages.getString("ConnectionProperties.noTzConversionForTimeType"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1213 */     this.nullCatalogMeansCurrent = new BooleanConnectionProperty("nullCatalogMeansCurrent", true, Messages.getString("ConnectionProperties.nullCatalogMeansCurrent"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1219 */     this.nullNamePatternMatchesAll = new BooleanConnectionProperty("nullNamePatternMatchesAll", true, Messages.getString("ConnectionProperties.nullNamePatternMatchesAll"), "3.1.8", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1225 */     this.packetDebugBufferSize = new IntegerConnectionProperty("packetDebugBufferSize", 20, 0, 2147483647, Messages.getString("ConnectionProperties.packetDebugBufferSize"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 7);
/*      */ 
/* 1233 */     this.padCharsWithSpace = new BooleanConnectionProperty("padCharsWithSpace", false, Messages.getString("ConnectionProperties.padCharsWithSpace"), "5.0.6", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1241 */     this.paranoid = new BooleanConnectionProperty("paranoid", false, Messages.getString("ConnectionProperties.paranoid"), "3.0.1", SECURITY_CATEGORY, -2147483648);
/*      */ 
/* 1247 */     this.pedantic = new BooleanConnectionProperty("pedantic", false, Messages.getString("ConnectionProperties.pedantic"), "3.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1251 */     this.pinGlobalTxToPhysicalConnection = new BooleanConnectionProperty("pinGlobalTxToPhysicalConnection", false, Messages.getString("ConnectionProperties.pinGlobalTxToPhysicalConnection"), "5.0.1", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1255 */     this.populateInsertRowWithDefaultValues = new BooleanConnectionProperty("populateInsertRowWithDefaultValues", false, Messages.getString("ConnectionProperties.populateInsertRowWithDefaultValues"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1260 */     this.preparedStatementCacheSize = new IntegerConnectionProperty("prepStmtCacheSize", 25, 0, 2147483647, Messages.getString("ConnectionProperties.prepStmtCacheSize"), "3.0.10", PERFORMANCE_CATEGORY, 10);
/*      */ 
/* 1265 */     this.preparedStatementCacheSqlLimit = new IntegerConnectionProperty("prepStmtCacheSqlLimit", 256, 1, 2147483647, Messages.getString("ConnectionProperties.prepStmtCacheSqlLimit"), "3.0.10", PERFORMANCE_CATEGORY, 11);
/*      */ 
/* 1273 */     this.parseInfoCacheFactory = new StringConnectionProperty("parseInfoCacheFactory", PerConnectionLRUFactory.class.getName(), Messages.getString("ConnectionProperties.parseInfoCacheFactory"), "5.1.1", PERFORMANCE_CATEGORY, 12);
/*      */ 
/* 1276 */     this.processEscapeCodesForPrepStmts = new BooleanConnectionProperty("processEscapeCodesForPrepStmts", true, Messages.getString("ConnectionProperties.processEscapeCodesForPrepStmts"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1283 */     this.profilerEventHandler = new StringConnectionProperty("profilerEventHandler", "com.mysql.jdbc.profiler.LoggingProfilerEventHandler", Messages.getString("ConnectionProperties.profilerEventHandler"), "5.1.6", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1289 */     this.profileSql = new StringConnectionProperty("profileSql", null, Messages.getString("ConnectionProperties.profileSqlDeprecated"), "2.0.14", DEBUGING_PROFILING_CATEGORY, 3);
/*      */ 
/* 1295 */     this.profileSQL = new BooleanConnectionProperty("profileSQL", false, Messages.getString("ConnectionProperties.profileSQL"), "3.1.0", DEBUGING_PROFILING_CATEGORY, 1);
/*      */ 
/* 1301 */     this.profileSQLAsBoolean = false;
/*      */ 
/* 1303 */     this.propertiesTransform = new StringConnectionProperty("propertiesTransform", null, Messages.getString("ConnectionProperties.connectionPropertiesTransform"), "3.1.4", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1309 */     this.queriesBeforeRetryMaster = new IntegerConnectionProperty("queriesBeforeRetryMaster", 50, 1, 2147483647, Messages.getString("ConnectionProperties.queriesBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 7);
/*      */ 
/* 1317 */     this.queryTimeoutKillsConnection = new BooleanConnectionProperty("queryTimeoutKillsConnection", false, Messages.getString("ConnectionProperties.queryTimeoutKillsConnection"), "5.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1321 */     this.reconnectAtTxEnd = new BooleanConnectionProperty("reconnectAtTxEnd", false, Messages.getString("ConnectionProperties.reconnectAtTxEnd"), "3.0.10", HA_CATEGORY, 4);
/*      */ 
/* 1326 */     this.reconnectTxAtEndAsBoolean = false;
/*      */ 
/* 1328 */     this.relaxAutoCommit = new BooleanConnectionProperty("relaxAutoCommit", false, Messages.getString("ConnectionProperties.relaxAutoCommit"), "2.0.13", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1334 */     this.reportMetricsIntervalMillis = new IntegerConnectionProperty("reportMetricsIntervalMillis", 30000, 0, 2147483647, Messages.getString("ConnectionProperties.reportMetricsIntervalMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 3);
/*      */ 
/* 1342 */     this.requireSSL = new BooleanConnectionProperty("requireSSL", false, Messages.getString("ConnectionProperties.requireSSL"), "3.1.0", SECURITY_CATEGORY, 3);
/*      */ 
/* 1347 */     this.resourceId = new StringConnectionProperty("resourceId", null, Messages.getString("ConnectionProperties.resourceId"), "5.0.1", HA_CATEGORY, -2147483648);
/*      */ 
/* 1354 */     this.resultSetSizeThreshold = new IntegerConnectionProperty("resultSetSizeThreshold", 100, Messages.getString("ConnectionProperties.resultSetSizeThreshold"), "5.0.5", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1357 */     this.retainStatementAfterResultSetClose = new BooleanConnectionProperty("retainStatementAfterResultSetClose", false, Messages.getString("ConnectionProperties.retainStatementAfterResultSetClose"), "3.1.11", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1363 */     this.rewriteBatchedStatements = new BooleanConnectionProperty("rewriteBatchedStatements", false, Messages.getString("ConnectionProperties.rewriteBatchedStatements"), "3.1.13", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1369 */     this.rollbackOnPooledClose = new BooleanConnectionProperty("rollbackOnPooledClose", true, Messages.getString("ConnectionProperties.rollbackOnPooledClose"), "3.0.15", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1375 */     this.roundRobinLoadBalance = new BooleanConnectionProperty("roundRobinLoadBalance", false, Messages.getString("ConnectionProperties.roundRobinLoadBalance"), "3.1.2", HA_CATEGORY, 5);
/*      */ 
/* 1381 */     this.runningCTS13 = new BooleanConnectionProperty("runningCTS13", false, Messages.getString("ConnectionProperties.runningCTS13"), "3.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1387 */     this.secondsBeforeRetryMaster = new IntegerConnectionProperty("secondsBeforeRetryMaster", 30, 1, 2147483647, Messages.getString("ConnectionProperties.secondsBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 8);
/*      */ 
/* 1395 */     this.selfDestructOnPingSecondsLifetime = new IntegerConnectionProperty("selfDestructOnPingSecondsLifetime", 0, 0, 2147483647, Messages.getString("ConnectionProperties.selfDestructOnPingSecondsLifetime"), "5.1.6", HA_CATEGORY, 2147483647);
/*      */ 
/* 1403 */     this.selfDestructOnPingMaxOperations = new IntegerConnectionProperty("selfDestructOnPingMaxOperations", 0, 0, 2147483647, Messages.getString("ConnectionProperties.selfDestructOnPingMaxOperations"), "5.1.6", HA_CATEGORY, 2147483647);
/*      */ 
/* 1411 */     this.serverTimezone = new StringConnectionProperty("serverTimezone", null, Messages.getString("ConnectionProperties.serverTimezone"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1417 */     this.sessionVariables = new StringConnectionProperty("sessionVariables", null, Messages.getString("ConnectionProperties.sessionVariables"), "3.1.8", MISC_CATEGORY, 2147483647);
/*      */ 
/* 1422 */     this.slowQueryThresholdMillis = new IntegerConnectionProperty("slowQueryThresholdMillis", 2000, 0, 2147483647, Messages.getString("ConnectionProperties.slowQueryThresholdMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 9);
/*      */ 
/* 1430 */     this.slowQueryThresholdNanos = new LongConnectionProperty("slowQueryThresholdNanos", 0L, Messages.getString("ConnectionProperties.slowQueryThresholdNanos"), "5.0.7", DEBUGING_PROFILING_CATEGORY, 10);
/*      */ 
/* 1438 */     this.socketFactoryClassName = new StringConnectionProperty("socketFactory", StandardSocketFactory.class.getName(), Messages.getString("ConnectionProperties.socketFactory"), "3.0.3", CONNECTION_AND_AUTH_CATEGORY, 4);
/*      */ 
/* 1444 */     this.socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, 2147483647, Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 10);
/*      */ 
/* 1452 */     this.statementInterceptors = new StringConnectionProperty("statementInterceptors", null, Messages.getString("ConnectionProperties.statementInterceptors"), "5.1.1", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1455 */     this.strictFloatingPoint = new BooleanConnectionProperty("strictFloatingPoint", false, Messages.getString("ConnectionProperties.strictFloatingPoint"), "3.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1460 */     this.strictUpdates = new BooleanConnectionProperty("strictUpdates", true, Messages.getString("ConnectionProperties.strictUpdates"), "3.0.4", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1466 */     this.overrideSupportsIntegrityEnhancementFacility = new BooleanConnectionProperty("overrideSupportsIntegrityEnhancementFacility", false, Messages.getString("ConnectionProperties.overrideSupportsIEF"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1472 */     this.tcpNoDelay = new BooleanConnectionProperty("tcpNoDelay", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpNoDelay"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1478 */     this.tcpKeepAlive = new BooleanConnectionProperty("tcpKeepAlive", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpKeepAlive"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1484 */     this.tcpRcvBuf = new IntegerConnectionProperty("tcpRcvBuf", Integer.parseInt("0"), 0, 2147483647, Messages.getString("ConnectionProperties.tcpSoRcvBuf"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1491 */     this.tcpSndBuf = new IntegerConnectionProperty("tcpSndBuf", Integer.parseInt("0"), 0, 2147483647, Messages.getString("ConnectionProperties.tcpSoSndBuf"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1498 */     this.tcpTrafficClass = new IntegerConnectionProperty("tcpTrafficClass", Integer.parseInt("0"), 0, 255, Messages.getString("ConnectionProperties.tcpTrafficClass"), "5.0.7", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1505 */     this.tinyInt1isBit = new BooleanConnectionProperty("tinyInt1isBit", true, Messages.getString("ConnectionProperties.tinyInt1isBit"), "3.0.16", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1511 */     this.traceProtocol = new BooleanConnectionProperty("traceProtocol", false, Messages.getString("ConnectionProperties.traceProtocol"), "3.1.2", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1516 */     this.treatUtilDateAsTimestamp = new BooleanConnectionProperty("treatUtilDateAsTimestamp", true, Messages.getString("ConnectionProperties.treatUtilDateAsTimestamp"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1521 */     this.transformedBitIsBoolean = new BooleanConnectionProperty("transformedBitIsBoolean", false, Messages.getString("ConnectionProperties.transformedBitIsBoolean"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1527 */     this.useBlobToStoreUTF8OutsideBMP = new BooleanConnectionProperty("useBlobToStoreUTF8OutsideBMP", false, Messages.getString("ConnectionProperties.useBlobToStoreUTF8OutsideBMP"), "5.1.3", MISC_CATEGORY, 128);
/*      */ 
/* 1533 */     this.utf8OutsideBmpExcludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpExcludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpExcludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */ 
/* 1539 */     this.utf8OutsideBmpIncludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpIncludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpIncludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */ 
/* 1545 */     this.useCompression = new BooleanConnectionProperty("useCompression", false, Messages.getString("ConnectionProperties.useCompression"), "3.0.17", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1551 */     this.useColumnNamesInFindColumn = new BooleanConnectionProperty("useColumnNamesInFindColumn", false, Messages.getString("ConnectionProperties.useColumnNamesInFindColumn"), "5.1.7", MISC_CATEGORY, 2147483647);
/*      */ 
/* 1557 */     this.useConfigs = new StringConnectionProperty("useConfigs", null, Messages.getString("ConnectionProperties.useConfigs"), "3.1.5", CONNECTION_AND_AUTH_CATEGORY, 2147483647);
/*      */ 
/* 1563 */     this.useCursorFetch = new BooleanConnectionProperty("useCursorFetch", false, Messages.getString("ConnectionProperties.useCursorFetch"), "5.0.0", PERFORMANCE_CATEGORY, 2147483647);
/*      */ 
/* 1569 */     this.useDynamicCharsetInfo = new BooleanConnectionProperty("useDynamicCharsetInfo", true, Messages.getString("ConnectionProperties.useDynamicCharsetInfo"), "5.0.6", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1575 */     this.useDirectRowUnpack = new BooleanConnectionProperty("useDirectRowUnpack", true, "Use newer result set row unpacking code that skips a copy from network buffers  to a MySQL packet instance and instead reads directly into the result set row data buffers.", "5.1.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1581 */     this.useFastIntParsing = new BooleanConnectionProperty("useFastIntParsing", true, Messages.getString("ConnectionProperties.useFastIntParsing"), "3.1.4", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1587 */     this.useFastDateParsing = new BooleanConnectionProperty("useFastDateParsing", true, Messages.getString("ConnectionProperties.useFastDateParsing"), "5.0.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1593 */     this.useHostsInPrivileges = new BooleanConnectionProperty("useHostsInPrivileges", true, Messages.getString("ConnectionProperties.useHostsInPrivileges"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1598 */     this.useInformationSchema = new BooleanConnectionProperty("useInformationSchema", false, Messages.getString("ConnectionProperties.useInformationSchema"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1603 */     this.useJDBCCompliantTimezoneShift = new BooleanConnectionProperty("useJDBCCompliantTimezoneShift", false, Messages.getString("ConnectionProperties.useJDBCCompliantTimezoneShift"), "5.0.0", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1610 */     this.useLocalSessionState = new BooleanConnectionProperty("useLocalSessionState", false, Messages.getString("ConnectionProperties.useLocalSessionState"), "3.1.7", PERFORMANCE_CATEGORY, 5);
/*      */ 
/* 1616 */     this.useLocalTransactionState = new BooleanConnectionProperty("useLocalTransactionState", false, Messages.getString("ConnectionProperties.useLocalTransactionState"), "5.1.7", PERFORMANCE_CATEGORY, 6);
/*      */ 
/* 1622 */     this.useLegacyDatetimeCode = new BooleanConnectionProperty("useLegacyDatetimeCode", true, Messages.getString("ConnectionProperties.useLegacyDatetimeCode"), "5.1.6", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1628 */     this.useNanosForElapsedTime = new BooleanConnectionProperty("useNanosForElapsedTime", false, Messages.getString("ConnectionProperties.useNanosForElapsedTime"), "5.0.7", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1635 */     this.useOldAliasMetadataBehavior = new BooleanConnectionProperty("useOldAliasMetadataBehavior", false, Messages.getString("ConnectionProperties.useOldAliasMetadataBehavior"), "5.0.4", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1643 */     this.useOldUTF8Behavior = new BooleanConnectionProperty("useOldUTF8Behavior", false, Messages.getString("ConnectionProperties.useOldUtf8Behavior"), "3.1.6", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1649 */     this.useOldUTF8BehaviorAsBoolean = false;
/*      */ 
/* 1651 */     this.useOnlyServerErrorMessages = new BooleanConnectionProperty("useOnlyServerErrorMessages", true, Messages.getString("ConnectionProperties.useOnlyServerErrorMessages"), "3.0.15", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1657 */     this.useReadAheadInput = new BooleanConnectionProperty("useReadAheadInput", true, Messages.getString("ConnectionProperties.useReadAheadInput"), "3.1.5", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1663 */     this.useSqlStateCodes = new BooleanConnectionProperty("useSqlStateCodes", true, Messages.getString("ConnectionProperties.useSqlStateCodes"), "3.1.3", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1669 */     this.useSSL = new BooleanConnectionProperty("useSSL", false, Messages.getString("ConnectionProperties.useSSL"), "3.0.2", SECURITY_CATEGORY, 2);
/*      */ 
/* 1675 */     this.useSSPSCompatibleTimezoneShift = new BooleanConnectionProperty("useSSPSCompatibleTimezoneShift", false, Messages.getString("ConnectionProperties.useSSPSCompatibleTimezoneShift"), "5.0.5", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1681 */     this.useStreamLengthsInPrepStmts = new BooleanConnectionProperty("useStreamLengthsInPrepStmts", true, Messages.getString("ConnectionProperties.useStreamLengthsInPrepStmts"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1687 */     this.useTimezone = new BooleanConnectionProperty("useTimezone", false, Messages.getString("ConnectionProperties.useTimezone"), "3.0.2", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1693 */     this.useUltraDevWorkAround = new BooleanConnectionProperty("ultraDevHack", false, Messages.getString("ConnectionProperties.ultraDevHack"), "2.0.3", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1699 */     this.useUnbufferedInput = new BooleanConnectionProperty("useUnbufferedInput", true, Messages.getString("ConnectionProperties.useUnbufferedInput"), "3.0.11", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1704 */     this.useUnicode = new BooleanConnectionProperty("useUnicode", true, Messages.getString("ConnectionProperties.useUnicode"), "1.1g", MISC_CATEGORY, 0);
/*      */ 
/* 1711 */     this.useUnicodeAsBoolean = true;
/*      */ 
/* 1713 */     this.useUsageAdvisor = new BooleanConnectionProperty("useUsageAdvisor", false, Messages.getString("ConnectionProperties.useUsageAdvisor"), "3.1.1", DEBUGING_PROFILING_CATEGORY, 10);
/*      */ 
/* 1719 */     this.useUsageAdvisorAsBoolean = false;
/*      */ 
/* 1721 */     this.yearIsDateType = new BooleanConnectionProperty("yearIsDateType", true, Messages.getString("ConnectionProperties.yearIsDateType"), "3.1.9", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1727 */     this.zeroDateTimeBehavior = new StringConnectionProperty("zeroDateTimeBehavior", "exception", new String[] { "exception", "round", "convertToNull" }, Messages.getString("ConnectionProperties.zeroDateTimeBehavior", new Object[] { "exception", "round", "convertToNull" }), "3.1.4", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1737 */     this.useJvmCharsetConverters = new BooleanConnectionProperty("useJvmCharsetConverters", false, Messages.getString("ConnectionProperties.useJvmCharsetConverters"), "5.0.1", PERFORMANCE_CATEGORY, -2147483648);
/*      */ 
/* 1740 */     this.useGmtMillisForDatetimes = new BooleanConnectionProperty("useGmtMillisForDatetimes", false, Messages.getString("ConnectionProperties.useGmtMillisForDatetimes"), "3.1.12", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1742 */     this.dumpMetadataOnColumnNotFound = new BooleanConnectionProperty("dumpMetadataOnColumnNotFound", false, Messages.getString("ConnectionProperties.dumpMetadataOnColumnNotFound"), "3.1.13", DEBUGING_PROFILING_CATEGORY, -2147483648);
/*      */ 
/* 1746 */     this.clientCertificateKeyStoreUrl = new StringConnectionProperty("clientCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 5);
/*      */ 
/* 1751 */     this.trustCertificateKeyStoreUrl = new StringConnectionProperty("trustCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 8);
/*      */ 
/* 1756 */     this.clientCertificateKeyStoreType = new StringConnectionProperty("clientCertificateKeyStoreType", "JKS", Messages.getString("ConnectionProperties.clientCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 6);
/*      */ 
/* 1761 */     this.clientCertificateKeyStorePassword = new StringConnectionProperty("clientCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.clientCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 7);
/*      */ 
/* 1766 */     this.trustCertificateKeyStoreType = new StringConnectionProperty("trustCertificateKeyStoreType", "JKS", Messages.getString("ConnectionProperties.trustCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 9);
/*      */ 
/* 1771 */     this.trustCertificateKeyStorePassword = new StringConnectionProperty("trustCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.trustCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 10);
/*      */ 
/* 1776 */     this.verifyServerCertificate = new BooleanConnectionProperty("verifyServerCertificate", true, Messages.getString("ConnectionProperties.verifyServerCertificate"), "5.1.6", SECURITY_CATEGORY, 4);
/*      */ 
/* 1782 */     this.useAffectedRows = new BooleanConnectionProperty("useAffectedRows", false, Messages.getString("ConnectionProperties.useAffectedRows"), "5.1.7", MISC_CATEGORY, -2147483648);
/*      */ 
/* 1787 */     this.passwordCharacterEncoding = new StringConnectionProperty("passwordCharacterEncoding", null, Messages.getString("ConnectionProperties.passwordCharacterEncoding"), "5.1.7", SECURITY_CATEGORY, -2147483648);
/*      */ 
/* 1792 */     this.maxAllowedPacket = new IntegerConnectionProperty("maxAllowedPacket", -1, Messages.getString("ConnectionProperties.maxAllowedPacket"), "5.1.8", NETWORK_CATEGORY, -2147483648);
/*      */ 
/* 1796 */     this.authenticationPlugins = new StringConnectionProperty("authenticationPlugins", null, Messages.getString("ConnectionProperties.authenticationPlugins"), "5.1.19", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1802 */     this.disabledAuthenticationPlugins = new StringConnectionProperty("disabledAuthenticationPlugins", null, Messages.getString("ConnectionProperties.disabledAuthenticationPlugins"), "5.1.19", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1808 */     this.defaultAuthenticationPlugin = new StringConnectionProperty("defaultAuthenticationPlugin", "com.mysql.jdbc.authentication.MysqlNativePasswordPlugin", Messages.getString("ConnectionProperties.defaultAuthenticationPlugin"), "5.1.19", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */ 
/* 1814 */     this.disconnectOnExpiredPasswords = new BooleanConnectionProperty("disconnectOnExpiredPasswords", true, Messages.getString("ConnectionProperties.disconnectOnExpiredPasswords"), "5.1.23", CONNECTION_AND_AUTH_CATEGORY, -2147483648);
/*      */   }
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/*  667 */     return null;
/*      */   }
/*      */ 
/*      */   protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/*  686 */     return new ConnectionPropertiesImpl() { private static final long serialVersionUID = 4257801713007640581L; } .exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
/*      */   }
/*      */ 
/*      */   protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/* 1823 */     initializeProperties(info);
/*      */ 
/* 1825 */     int numProperties = PROPERTY_LIST.size();
/*      */ 
/* 1827 */     int listSize = numProperties + slotsToReserve;
/*      */ 
/* 1829 */     DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
/*      */ 
/* 1831 */     for (int i = slotsToReserve; i < listSize; i++) {
/* 1832 */       Field propertyField = (Field)PROPERTY_LIST.get(i - slotsToReserve);
/*      */       try
/*      */       {
/* 1836 */         ConnectionProperty propToExpose = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1839 */         if (info != null) {
/* 1840 */           propToExpose.initializeFrom(info);
/*      */         }
/*      */ 
/* 1844 */         driverProperties[i] = propToExpose.getAsDriverPropertyInfo();
/*      */       } catch (IllegalAccessException iae) {
/* 1846 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.InternalPropertiesFailure"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1851 */     return driverProperties;
/*      */   }
/*      */ 
/*      */   protected Properties exposeAsProperties(Properties info) throws SQLException
/*      */   {
/* 1856 */     if (info == null) {
/* 1857 */       info = new Properties();
/*      */     }
/*      */ 
/* 1860 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 1862 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 1863 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 1867 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1870 */         Object propValue = propToGet.getValueAsObject();
/*      */ 
/* 1872 */         if (propValue != null)
/* 1873 */           info.setProperty(propToGet.getPropertyName(), propValue.toString());
/*      */       }
/*      */       catch (IllegalAccessException iae)
/*      */       {
/* 1877 */         throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1882 */     return info;
/*      */   }
/*      */ 
/*      */   public String exposeAsXml()
/*      */     throws SQLException
/*      */   {
/* 1894 */     StringBuffer xmlBuf = new StringBuffer();
/* 1895 */     xmlBuf.append("<ConnectionProperties>");
/*      */ 
/* 1897 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 1899 */     int numCategories = PROPERTY_CATEGORIES.length;
/*      */ 
/* 1901 */     Map propertyListByCategory = new HashMap();
/*      */ 
/* 1903 */     for (int i = 0; i < numCategories; i++) {
/* 1904 */       propertyListByCategory.put(PROPERTY_CATEGORIES[i], new XmlMap());
/*      */     }
/*      */ 
/* 1913 */     StringConnectionProperty userProp = new StringConnectionProperty("user", null, Messages.getString("ConnectionProperties.Username"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483647);
/*      */ 
/* 1917 */     StringConnectionProperty passwordProp = new StringConnectionProperty("password", null, Messages.getString("ConnectionProperties.Password"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483646);
/*      */ 
/* 1922 */     XmlMap connectionSortMaps = (XmlMap)propertyListByCategory.get(CONNECTION_AND_AUTH_CATEGORY);
/* 1923 */     TreeMap userMap = new TreeMap();
/* 1924 */     userMap.put(userProp.getPropertyName(), userProp);
/*      */ 
/* 1926 */     connectionSortMaps.ordered.put(Integer.valueOf(userProp.getOrder()), userMap);
/*      */ 
/* 1928 */     TreeMap passwordMap = new TreeMap();
/* 1929 */     passwordMap.put(passwordProp.getPropertyName(), passwordProp);
/*      */ 
/* 1931 */     connectionSortMaps.ordered.put(new Integer(passwordProp.getOrder()), passwordMap);
/*      */     try
/*      */     {
/* 1934 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 1935 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */ 
/* 1937 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1939 */         XmlMap sortMaps = (XmlMap)propertyListByCategory.get(propToGet.getCategoryName());
/*      */ 
/* 1941 */         int orderInCategory = propToGet.getOrder();
/*      */ 
/* 1943 */         if (orderInCategory == -2147483648) {
/* 1944 */           sortMaps.alpha.put(propToGet.getPropertyName(), propToGet);
/*      */         } else {
/* 1946 */           Integer order = Integer.valueOf(orderInCategory);
/* 1947 */           Map orderMap = (Map)sortMaps.ordered.get(order);
/*      */ 
/* 1949 */           if (orderMap == null) {
/* 1950 */             orderMap = new TreeMap();
/* 1951 */             sortMaps.ordered.put(order, orderMap);
/*      */           }
/*      */ 
/* 1954 */           orderMap.put(propToGet.getPropertyName(), propToGet);
/*      */         }
/*      */       }
/*      */ 
/* 1958 */       for (int j = 0; j < numCategories; j++) {
/* 1959 */         XmlMap sortMaps = (XmlMap)propertyListByCategory.get(PROPERTY_CATEGORIES[j]);
/*      */ 
/* 1961 */         xmlBuf.append("\n <PropertyCategory name=\"");
/* 1962 */         xmlBuf.append(PROPERTY_CATEGORIES[j]);
/* 1963 */         xmlBuf.append("\">");
/*      */ 
/* 1965 */         for (Map orderedEl : sortMaps.ordered.values()) {
/* 1966 */           for (ConnectionProperty propToGet : orderedEl.values()) {
/* 1967 */             xmlBuf.append("\n  <Property name=\"");
/* 1968 */             xmlBuf.append(propToGet.getPropertyName());
/* 1969 */             xmlBuf.append("\" required=\"");
/* 1970 */             xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */ 
/* 1972 */             xmlBuf.append("\" default=\"");
/*      */ 
/* 1974 */             if (propToGet.getDefaultValue() != null) {
/* 1975 */               xmlBuf.append(propToGet.getDefaultValue());
/*      */             }
/*      */ 
/* 1978 */             xmlBuf.append("\" sortOrder=\"");
/* 1979 */             xmlBuf.append(propToGet.getOrder());
/* 1980 */             xmlBuf.append("\" since=\"");
/* 1981 */             xmlBuf.append(propToGet.sinceVersion);
/* 1982 */             xmlBuf.append("\">\n");
/* 1983 */             xmlBuf.append("    ");
/* 1984 */             String escapedDescription = propToGet.description;
/* 1985 */             escapedDescription = escapedDescription.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
/*      */ 
/* 1987 */             xmlBuf.append(escapedDescription);
/* 1988 */             xmlBuf.append("\n  </Property>");
/*      */           }
/*      */         }
/*      */ 
/* 1992 */         for (ConnectionProperty propToGet : sortMaps.alpha.values()) {
/* 1993 */           xmlBuf.append("\n  <Property name=\"");
/* 1994 */           xmlBuf.append(propToGet.getPropertyName());
/* 1995 */           xmlBuf.append("\" required=\"");
/* 1996 */           xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */ 
/* 1998 */           xmlBuf.append("\" default=\"");
/*      */ 
/* 2000 */           if (propToGet.getDefaultValue() != null) {
/* 2001 */             xmlBuf.append(propToGet.getDefaultValue());
/*      */           }
/*      */ 
/* 2004 */           xmlBuf.append("\" sortOrder=\"alpha\" since=\"");
/* 2005 */           xmlBuf.append(propToGet.sinceVersion);
/* 2006 */           xmlBuf.append("\">\n");
/* 2007 */           xmlBuf.append("    ");
/* 2008 */           xmlBuf.append(propToGet.description);
/* 2009 */           xmlBuf.append("\n  </Property>");
/*      */         }
/*      */ 
/* 2012 */         xmlBuf.append("\n </PropertyCategory>");
/*      */       }
/*      */     } catch (IllegalAccessException iae) {
/* 2015 */       throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */     }
/*      */ 
/* 2019 */     xmlBuf.append("\n</ConnectionProperties>");
/*      */ 
/* 2021 */     return xmlBuf.toString();
/*      */   }
/*      */ 
/*      */   public boolean getAllowLoadLocalInfile()
/*      */   {
/* 2028 */     return this.allowLoadLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowMultiQueries()
/*      */   {
/* 2035 */     return this.allowMultiQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowNanAndInf()
/*      */   {
/* 2042 */     return this.allowNanAndInf.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowUrlInLocalInfile()
/*      */   {
/* 2049 */     return this.allowUrlInLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAlwaysSendSetIsolation()
/*      */   {
/* 2056 */     return this.alwaysSendSetIsolation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAutoDeserialize()
/*      */   {
/* 2063 */     return this.autoDeserialize.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAutoGenerateTestcaseScript()
/*      */   {
/* 2070 */     return this.autoGenerateTestcaseScriptAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getAutoReconnectForPools()
/*      */   {
/* 2077 */     return this.autoReconnectForPoolsAsBoolean;
/*      */   }
/*      */ 
/*      */   public int getBlobSendChunkSize()
/*      */   {
/* 2084 */     return this.blobSendChunkSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStatements()
/*      */   {
/* 2091 */     return this.cacheCallableStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getCachePreparedStatements()
/*      */   {
/* 2098 */     return ((Boolean)this.cachePreparedStatements.getValueAsObject()).booleanValue();
/*      */   }
/*      */ 
/*      */   public boolean getCacheResultSetMetadata()
/*      */   {
/* 2106 */     return this.cacheResultSetMetaDataAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getCacheServerConfiguration()
/*      */   {
/* 2113 */     return this.cacheServerConfiguration.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getCallableStatementCacheSize()
/*      */   {
/* 2120 */     return this.callableStatementCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getCapitalizeTypeNames()
/*      */   {
/* 2127 */     return this.capitalizeTypeNames.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetResults()
/*      */   {
/* 2134 */     return this.characterSetResults.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getClobberStreamingResults()
/*      */   {
/* 2141 */     return this.clobberStreamingResults.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getClobCharacterEncoding()
/*      */   {
/* 2148 */     return this.clobCharacterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getConnectionCollation()
/*      */   {
/* 2155 */     return this.connectionCollation.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/* 2162 */     return this.connectTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getContinueBatchOnError()
/*      */   {
/* 2169 */     return this.continueBatchOnError.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getCreateDatabaseIfNotExist()
/*      */   {
/* 2176 */     return this.createDatabaseIfNotExist.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getDefaultFetchSize()
/*      */   {
/* 2183 */     return this.defaultFetchSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getDontTrackOpenResources()
/*      */   {
/* 2190 */     return this.dontTrackOpenResources.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDumpQueriesOnException()
/*      */   {
/* 2197 */     return this.dumpQueriesOnException.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDynamicCalendars()
/*      */   {
/* 2204 */     return this.dynamicCalendars.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getElideSetAutoCommits()
/*      */   {
/* 2211 */     return this.elideSetAutoCommits.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmptyStringsConvertToZero()
/*      */   {
/* 2218 */     return this.emptyStringsConvertToZero.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateLocators()
/*      */   {
/* 2225 */     return this.emulateLocators.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateUnsupportedPstmts()
/*      */   {
/* 2232 */     return this.emulateUnsupportedPstmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEnablePacketDebug()
/*      */   {
/* 2239 */     return this.enablePacketDebug.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getEncoding()
/*      */   {
/* 2246 */     return this.characterEncodingAsString;
/*      */   }
/*      */ 
/*      */   public boolean getExplainSlowQueries()
/*      */   {
/* 2253 */     return this.explainSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getFailOverReadOnly()
/*      */   {
/* 2260 */     return this.failOverReadOnly.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerformanceMetrics()
/*      */   {
/* 2267 */     return this.gatherPerformanceMetrics.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected boolean getHighAvailability()
/*      */   {
/* 2276 */     return this.highAvailabilityAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getHoldResultsOpenOverStatementClose()
/*      */   {
/* 2283 */     return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getIgnoreNonTxTables()
/*      */   {
/* 2290 */     return this.ignoreNonTxTables.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getInitialTimeout()
/*      */   {
/* 2297 */     return this.initialTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getInteractiveClient()
/*      */   {
/* 2304 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getIsInteractiveClient()
/*      */   {
/* 2311 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncation()
/*      */   {
/* 2318 */     return this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getLocatorFetchBufferSize()
/*      */   {
/* 2325 */     return this.locatorFetchBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getLogger()
/*      */   {
/* 2332 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getLoggerClassName()
/*      */   {
/* 2339 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getLogSlowQueries()
/*      */   {
/* 2346 */     return this.logSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getMaintainTimeStats()
/*      */   {
/* 2353 */     return this.maintainTimeStatsAsBoolean;
/*      */   }
/*      */ 
/*      */   public int getMaxQuerySizeToLog()
/*      */   {
/* 2360 */     return this.maxQuerySizeToLog.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public int getMaxReconnects()
/*      */   {
/* 2367 */     return this.maxReconnects.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */   {
/* 2374 */     return this.maxRowsAsInt;
/*      */   }
/*      */ 
/*      */   public int getMetadataCacheSize()
/*      */   {
/* 2381 */     return this.metadataCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getNoDatetimeStringSync()
/*      */   {
/* 2388 */     return this.noDatetimeStringSync.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getNullCatalogMeansCurrent()
/*      */   {
/* 2395 */     return this.nullCatalogMeansCurrent.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getNullNamePatternMatchesAll()
/*      */   {
/* 2402 */     return this.nullNamePatternMatchesAll.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getPacketDebugBufferSize()
/*      */   {
/* 2409 */     return this.packetDebugBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getParanoid()
/*      */   {
/* 2416 */     return this.paranoid.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getPedantic()
/*      */   {
/* 2423 */     return this.pedantic.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSize()
/*      */   {
/* 2430 */     return ((Integer)this.preparedStatementCacheSize.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSqlLimit()
/*      */   {
/* 2438 */     return ((Integer)this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSql()
/*      */   {
/* 2446 */     return this.profileSQLAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getProfileSQL()
/*      */   {
/* 2453 */     return this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getPropertiesTransform()
/*      */   {
/* 2460 */     return this.propertiesTransform.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getQueriesBeforeRetryMaster()
/*      */   {
/* 2467 */     return this.queriesBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getReconnectAtTxEnd()
/*      */   {
/* 2474 */     return this.reconnectTxAtEndAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getRelaxAutoCommit()
/*      */   {
/* 2481 */     return this.relaxAutoCommit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getReportMetricsIntervalMillis()
/*      */   {
/* 2488 */     return this.reportMetricsIntervalMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getRequireSSL()
/*      */   {
/* 2495 */     return this.requireSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRetainStatementAfterResultSetClose() {
/* 2499 */     return this.retainStatementAfterResultSetClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRollbackOnPooledClose()
/*      */   {
/* 2506 */     return this.rollbackOnPooledClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRoundRobinLoadBalance()
/*      */   {
/* 2513 */     return this.roundRobinLoadBalance.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRunningCTS13()
/*      */   {
/* 2520 */     return this.runningCTS13.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getSecondsBeforeRetryMaster()
/*      */   {
/* 2527 */     return this.secondsBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getServerTimezone()
/*      */   {
/* 2534 */     return this.serverTimezone.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getSessionVariables()
/*      */   {
/* 2541 */     return this.sessionVariables.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getSlowQueryThresholdMillis()
/*      */   {
/* 2548 */     return this.slowQueryThresholdMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getSocketFactoryClassName()
/*      */   {
/* 2555 */     return this.socketFactoryClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getSocketTimeout()
/*      */   {
/* 2562 */     return this.socketTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getStrictFloatingPoint()
/*      */   {
/* 2569 */     return this.strictFloatingPoint.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getStrictUpdates()
/*      */   {
/* 2576 */     return this.strictUpdates.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTinyInt1isBit()
/*      */   {
/* 2583 */     return this.tinyInt1isBit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTraceProtocol()
/*      */   {
/* 2590 */     return this.traceProtocol.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTransformedBitIsBoolean()
/*      */   {
/* 2597 */     return this.transformedBitIsBoolean.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseCompression()
/*      */   {
/* 2604 */     return this.useCompression.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastIntParsing()
/*      */   {
/* 2611 */     return this.useFastIntParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseHostsInPrivileges()
/*      */   {
/* 2618 */     return this.useHostsInPrivileges.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseInformationSchema()
/*      */   {
/* 2625 */     return this.useInformationSchema.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalSessionState()
/*      */   {
/* 2632 */     return this.useLocalSessionState.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldUTF8Behavior()
/*      */   {
/* 2639 */     return this.useOldUTF8BehaviorAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseOnlyServerErrorMessages()
/*      */   {
/* 2646 */     return this.useOnlyServerErrorMessages.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseReadAheadInput()
/*      */   {
/* 2653 */     return this.useReadAheadInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPreparedStmts()
/*      */   {
/* 2660 */     return this.detectServerPreparedStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseSqlStateCodes()
/*      */   {
/* 2667 */     return this.useSqlStateCodes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSL()
/*      */   {
/* 2674 */     return this.useSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseStreamLengthsInPrepStmts()
/*      */   {
/* 2681 */     return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseTimezone()
/*      */   {
/* 2688 */     return this.useTimezone.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUltraDevWorkAround()
/*      */   {
/* 2695 */     return this.useUltraDevWorkAround.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnbufferedInput()
/*      */   {
/* 2702 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnicode()
/*      */   {
/* 2709 */     return this.useUnicodeAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseUsageAdvisor()
/*      */   {
/* 2716 */     return this.useUsageAdvisorAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getYearIsDateType()
/*      */   {
/* 2723 */     return this.yearIsDateType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getZeroDateTimeBehavior()
/*      */   {
/* 2730 */     return this.zeroDateTimeBehavior.getValueAsString();
/*      */   }
/*      */ 
/*      */   protected void initializeFromRef(Reference ref)
/*      */     throws SQLException
/*      */   {
/* 2744 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 2746 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 2747 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 2751 */         ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 2754 */         if (ref != null)
/* 2755 */           propToSet.initializeFrom(ref);
/*      */       }
/*      */       catch (IllegalAccessException iae) {
/* 2758 */         throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2763 */     postInitialization();
/*      */   }
/*      */ 
/*      */   protected void initializeProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 2776 */     if (info != null)
/*      */     {
/* 2778 */       String profileSqlLc = info.getProperty("profileSql");
/*      */ 
/* 2780 */       if (profileSqlLc != null) {
/* 2781 */         info.put("profileSQL", profileSqlLc);
/*      */       }
/*      */ 
/* 2784 */       Properties infoCopy = (Properties)info.clone();
/*      */ 
/* 2786 */       infoCopy.remove("HOST");
/* 2787 */       infoCopy.remove("user");
/* 2788 */       infoCopy.remove("password");
/* 2789 */       infoCopy.remove("DBNAME");
/* 2790 */       infoCopy.remove("PORT");
/* 2791 */       infoCopy.remove("profileSql");
/*      */ 
/* 2793 */       int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 2795 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 2796 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */         try
/*      */         {
/* 2800 */           ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 2803 */           propToSet.initializeFrom(infoCopy);
/*      */         } catch (IllegalAccessException iae) {
/* 2805 */           throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unableToInitDriverProperties") + iae.toString(), "S1000", getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2812 */       postInitialization();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void postInitialization()
/*      */     throws SQLException
/*      */   {
/* 2819 */     if (this.profileSql.getValueAsObject() != null) {
/* 2820 */       this.profileSQL.initializeFrom(this.profileSql.getValueAsObject().toString());
/*      */     }
/*      */ 
/* 2824 */     this.reconnectTxAtEndAsBoolean = ((Boolean)this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
/*      */ 
/* 2828 */     if (getMaxRows() == 0)
/*      */     {
/* 2831 */       this.maxRows.setValueAsObject(Integer.valueOf(-1));
/*      */     }
/*      */ 
/* 2837 */     String testEncoding = getEncoding();
/*      */ 
/* 2839 */     if (testEncoding != null)
/*      */     {
/*      */       try
/*      */       {
/* 2843 */         String testString = "abc";
/* 2844 */         StringUtils.getBytes(testString, testEncoding);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 2846 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unsupportedCharacterEncoding", new Object[] { testEncoding }), "0S100", getExceptionInterceptor());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2855 */     if (((Boolean)this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
/*      */       try
/*      */       {
/* 2858 */         Class.forName("java.util.LinkedHashMap");
/*      */       } catch (ClassNotFoundException cnfe) {
/* 2860 */         this.cacheResultSetMetadata.setValue(false);
/*      */       }
/*      */     }
/*      */ 
/* 2864 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */ 
/* 2866 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/* 2867 */     this.characterEncodingAsString = ((String)this.characterEncoding.getValueAsObject());
/*      */ 
/* 2869 */     this.characterEncodingIsAliasForSjis = CharsetMapping.isAliasForSjis(this.characterEncodingAsString);
/* 2870 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/* 2871 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */ 
/* 2873 */     this.maxRowsAsInt = ((Integer)this.maxRows.getValueAsObject()).intValue();
/*      */ 
/* 2875 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/* 2876 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */ 
/* 2878 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */ 
/* 2880 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */ 
/* 2882 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */ 
/* 2884 */     this.jdbcCompliantTruncationForReads = getJdbcCompliantTruncation();
/*      */ 
/* 2886 */     if (getUseCursorFetch())
/*      */     {
/* 2889 */       setDetectServerPreparedStmts(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property)
/*      */   {
/* 2897 */     this.allowLoadLocalInfile.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setAllowMultiQueries(boolean property)
/*      */   {
/* 2904 */     this.allowMultiQueries.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setAllowNanAndInf(boolean flag)
/*      */   {
/* 2911 */     this.allowNanAndInf.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/* 2918 */     this.allowUrlInLocalInfile.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/* 2925 */     this.alwaysSendSetIsolation.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoDeserialize(boolean flag)
/*      */   {
/* 2932 */     this.autoDeserialize.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoGenerateTestcaseScript(boolean flag)
/*      */   {
/* 2939 */     this.autoGenerateTestcaseScript.setValue(flag);
/* 2940 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoReconnect(boolean flag)
/*      */   {
/* 2948 */     this.autoReconnect.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/* 2955 */     this.autoReconnectForPools.setValue(property);
/* 2956 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForPools(boolean flag)
/*      */   {
/* 2964 */     this.autoReconnectForPools.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setBlobSendChunkSize(String value)
/*      */     throws SQLException
/*      */   {
/* 2971 */     this.blobSendChunkSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStatements(boolean flag)
/*      */   {
/* 2978 */     this.cacheCallableStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePreparedStatements(boolean flag)
/*      */   {
/* 2985 */     this.cachePreparedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheResultSetMetadata(boolean property)
/*      */   {
/* 2992 */     this.cacheResultSetMetadata.setValue(property);
/* 2993 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setCacheServerConfiguration(boolean flag)
/*      */   {
/* 3001 */     this.cacheServerConfiguration.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCallableStatementCacheSize(int size)
/*      */   {
/* 3008 */     this.callableStatementCacheSize.setValue(size);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/* 3015 */     this.capitalizeTypeNames.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeTypeNames(boolean flag)
/*      */   {
/* 3022 */     this.capitalizeTypeNames.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String encoding)
/*      */   {
/* 3029 */     this.characterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */   public void setCharacterSetResults(String characterSet)
/*      */   {
/* 3036 */     this.characterSetResults.setValue(characterSet);
/*      */   }
/*      */ 
/*      */   public void setClobberStreamingResults(boolean flag)
/*      */   {
/* 3043 */     this.clobberStreamingResults.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setClobCharacterEncoding(String encoding)
/*      */   {
/* 3050 */     this.clobCharacterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */   public void setConnectionCollation(String collation)
/*      */   {
/* 3057 */     this.connectionCollation.setValue(collation);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int timeoutMs)
/*      */   {
/* 3064 */     this.connectTimeout.setValue(timeoutMs);
/*      */   }
/*      */ 
/*      */   public void setContinueBatchOnError(boolean property)
/*      */   {
/* 3071 */     this.continueBatchOnError.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setCreateDatabaseIfNotExist(boolean flag)
/*      */   {
/* 3078 */     this.createDatabaseIfNotExist.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDefaultFetchSize(int n)
/*      */   {
/* 3085 */     this.defaultFetchSize.setValue(n);
/*      */   }
/*      */ 
/*      */   public void setDetectServerPreparedStmts(boolean property)
/*      */   {
/* 3092 */     this.detectServerPreparedStmts.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setDontTrackOpenResources(boolean flag)
/*      */   {
/* 3099 */     this.dontTrackOpenResources.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpQueriesOnException(boolean flag)
/*      */   {
/* 3106 */     this.dumpQueriesOnException.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDynamicCalendars(boolean flag)
/*      */   {
/* 3113 */     this.dynamicCalendars.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setElideSetAutoCommits(boolean flag)
/*      */   {
/* 3120 */     this.elideSetAutoCommits.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEmptyStringsConvertToZero(boolean flag)
/*      */   {
/* 3127 */     this.emptyStringsConvertToZero.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEmulateLocators(boolean property)
/*      */   {
/* 3134 */     this.emulateLocators.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/* 3141 */     this.emulateUnsupportedPstmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEnablePacketDebug(boolean flag)
/*      */   {
/* 3148 */     this.enablePacketDebug.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String property)
/*      */   {
/* 3155 */     this.characterEncoding.setValue(property);
/* 3156 */     this.characterEncodingAsString = this.characterEncoding.getValueAsString();
/*      */ 
/* 3158 */     this.characterEncodingIsAliasForSjis = CharsetMapping.isAliasForSjis(this.characterEncodingAsString);
/*      */   }
/*      */ 
/*      */   public void setExplainSlowQueries(boolean flag)
/*      */   {
/* 3165 */     this.explainSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setFailOverReadOnly(boolean flag)
/*      */   {
/* 3172 */     this.failOverReadOnly.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/* 3179 */     this.gatherPerformanceMetrics.setValue(flag);
/*      */   }
/*      */ 
/*      */   protected void setHighAvailability(boolean property)
/*      */   {
/* 3188 */     this.autoReconnect.setValue(property);
/* 3189 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/* 3196 */     this.holdResultsOpenOverStatementClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setIgnoreNonTxTables(boolean property)
/*      */   {
/* 3203 */     this.ignoreNonTxTables.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setInitialTimeout(int property)
/*      */   {
/* 3210 */     this.initialTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setIsInteractiveClient(boolean property)
/*      */   {
/* 3217 */     this.isInteractiveClient.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/* 3224 */     this.jdbcCompliantTruncation.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setLocatorFetchBufferSize(String value)
/*      */     throws SQLException
/*      */   {
/* 3231 */     this.locatorFetchBufferSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setLogger(String property)
/*      */   {
/* 3238 */     this.loggerClassName.setValueAsObject(property);
/*      */   }
/*      */ 
/*      */   public void setLoggerClassName(String className)
/*      */   {
/* 3245 */     this.loggerClassName.setValue(className);
/*      */   }
/*      */ 
/*      */   public void setLogSlowQueries(boolean flag)
/*      */   {
/* 3252 */     this.logSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setMaintainTimeStats(boolean flag)
/*      */   {
/* 3259 */     this.maintainTimeStats.setValue(flag);
/* 3260 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes)
/*      */   {
/* 3268 */     this.maxQuerySizeToLog.setValue(sizeInBytes);
/*      */   }
/*      */ 
/*      */   public void setMaxReconnects(int property)
/*      */   {
/* 3275 */     this.maxReconnects.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int property)
/*      */   {
/* 3282 */     this.maxRows.setValue(property);
/* 3283 */     this.maxRowsAsInt = this.maxRows.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setMetadataCacheSize(int value)
/*      */   {
/* 3290 */     this.metadataCacheSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setNoDatetimeStringSync(boolean flag)
/*      */   {
/* 3297 */     this.noDatetimeStringSync.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/* 3304 */     this.nullCatalogMeansCurrent.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/* 3311 */     this.nullNamePatternMatchesAll.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setPacketDebugBufferSize(int size)
/*      */   {
/* 3318 */     this.packetDebugBufferSize.setValue(size);
/*      */   }
/*      */ 
/*      */   public void setParanoid(boolean property)
/*      */   {
/* 3325 */     this.paranoid.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setPedantic(boolean property)
/*      */   {
/* 3332 */     this.pedantic.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSize(int cacheSize)
/*      */   {
/* 3339 */     this.preparedStatementCacheSize.setValue(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */   {
/* 3346 */     this.preparedStatementCacheSqlLimit.setValue(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */   public void setProfileSql(boolean property)
/*      */   {
/* 3353 */     this.profileSQL.setValue(property);
/* 3354 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setProfileSQL(boolean flag)
/*      */   {
/* 3361 */     this.profileSQL.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setPropertiesTransform(String value)
/*      */   {
/* 3368 */     this.propertiesTransform.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setQueriesBeforeRetryMaster(int property)
/*      */   {
/* 3375 */     this.queriesBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setReconnectAtTxEnd(boolean property)
/*      */   {
/* 3382 */     this.reconnectAtTxEnd.setValue(property);
/* 3383 */     this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setRelaxAutoCommit(boolean property)
/*      */   {
/* 3391 */     this.relaxAutoCommit.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setReportMetricsIntervalMillis(int millis)
/*      */   {
/* 3398 */     this.reportMetricsIntervalMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */   public void setRequireSSL(boolean property)
/*      */   {
/* 3405 */     this.requireSSL.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/* 3412 */     this.retainStatementAfterResultSetClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRollbackOnPooledClose(boolean flag)
/*      */   {
/* 3419 */     this.rollbackOnPooledClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/* 3426 */     this.roundRobinLoadBalance.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRunningCTS13(boolean flag)
/*      */   {
/* 3433 */     this.runningCTS13.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setSecondsBeforeRetryMaster(int property)
/*      */   {
/* 3440 */     this.secondsBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setServerTimezone(String property)
/*      */   {
/* 3447 */     this.serverTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setSessionVariables(String variables)
/*      */   {
/* 3454 */     this.sessionVariables.setValue(variables);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdMillis(int millis)
/*      */   {
/* 3461 */     this.slowQueryThresholdMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */   public void setSocketFactoryClassName(String property)
/*      */   {
/* 3468 */     this.socketFactoryClassName.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setSocketTimeout(int property)
/*      */   {
/* 3475 */     this.socketTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setStrictFloatingPoint(boolean property)
/*      */   {
/* 3482 */     this.strictFloatingPoint.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setStrictUpdates(boolean property)
/*      */   {
/* 3489 */     this.strictUpdates.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setTinyInt1isBit(boolean flag)
/*      */   {
/* 3496 */     this.tinyInt1isBit.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setTraceProtocol(boolean flag)
/*      */   {
/* 3503 */     this.traceProtocol.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/* 3510 */     this.transformedBitIsBoolean.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseCompression(boolean property)
/*      */   {
/* 3517 */     this.useCompression.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseFastIntParsing(boolean flag)
/*      */   {
/* 3524 */     this.useFastIntParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseHostsInPrivileges(boolean property)
/*      */   {
/* 3531 */     this.useHostsInPrivileges.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseInformationSchema(boolean flag)
/*      */   {
/* 3538 */     this.useInformationSchema.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseLocalSessionState(boolean flag)
/*      */   {
/* 3545 */     this.useLocalSessionState.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/* 3552 */     this.useOldUTF8Behavior.setValue(flag);
/* 3553 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/* 3561 */     this.useOnlyServerErrorMessages.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseReadAheadInput(boolean flag)
/*      */   {
/* 3568 */     this.useReadAheadInput.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPreparedStmts(boolean flag)
/*      */   {
/* 3575 */     this.detectServerPreparedStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSqlStateCodes(boolean flag)
/*      */   {
/* 3582 */     this.useSqlStateCodes.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSSL(boolean property)
/*      */   {
/* 3589 */     this.useSSL.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/* 3596 */     this.useStreamLengthsInPrepStmts.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseTimezone(boolean property)
/*      */   {
/* 3603 */     this.useTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseUltraDevWorkAround(boolean property)
/*      */   {
/* 3610 */     this.useUltraDevWorkAround.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseUnbufferedInput(boolean flag)
/*      */   {
/* 3617 */     this.useUnbufferedInput.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUnicode(boolean flag)
/*      */   {
/* 3624 */     this.useUnicode.setValue(flag);
/* 3625 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/* 3632 */     this.useUsageAdvisor.setValue(useUsageAdvisorFlag);
/* 3633 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setYearIsDateType(boolean flag)
/*      */   {
/* 3641 */     this.yearIsDateType.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setZeroDateTimeBehavior(String behavior)
/*      */   {
/* 3648 */     this.zeroDateTimeBehavior.setValue(behavior);
/*      */   }
/*      */ 
/*      */   protected void storeToRef(Reference ref) throws SQLException {
/* 3652 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 3654 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 3655 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 3659 */         ConnectionProperty propToStore = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 3662 */         if (ref != null)
/* 3663 */           propToStore.storeTo(ref);
/*      */       }
/*      */       catch (IllegalAccessException iae) {
/* 3666 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.errorNotExpected"), getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean useUnbufferedInput()
/*      */   {
/* 3675 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseCursorFetch()
/*      */   {
/* 3682 */     return this.useCursorFetch.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseCursorFetch(boolean flag)
/*      */   {
/* 3689 */     this.useCursorFetch.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility()
/*      */   {
/* 3696 */     return this.overrideSupportsIntegrityEnhancementFacility.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/* 3703 */     this.overrideSupportsIntegrityEnhancementFacility.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getNoTimezoneConversionForTimeType()
/*      */   {
/* 3710 */     return this.noTimezoneConversionForTimeType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/* 3717 */     this.noTimezoneConversionForTimeType.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseJDBCCompliantTimezoneShift()
/*      */   {
/* 3724 */     return this.useJDBCCompliantTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/* 3731 */     this.useJDBCCompliantTimezoneShift.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getAutoClosePStmtStreams()
/*      */   {
/* 3738 */     return this.autoClosePStmtStreams.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoClosePStmtStreams(boolean flag)
/*      */   {
/* 3745 */     this.autoClosePStmtStreams.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getProcessEscapeCodesForPrepStmts()
/*      */   {
/* 3752 */     return this.processEscapeCodesForPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/* 3759 */     this.processEscapeCodesForPrepStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseGmtMillisForDatetimes()
/*      */   {
/* 3766 */     return this.useGmtMillisForDatetimes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/* 3773 */     this.useGmtMillisForDatetimes.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getDumpMetadataOnColumnNotFound()
/*      */   {
/* 3780 */     return this.dumpMetadataOnColumnNotFound.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag)
/*      */   {
/* 3787 */     this.dumpMetadataOnColumnNotFound.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getResourceId()
/*      */   {
/* 3794 */     return this.resourceId.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setResourceId(String resourceId)
/*      */   {
/* 3801 */     this.resourceId.setValue(resourceId);
/*      */   }
/*      */ 
/*      */   public boolean getRewriteBatchedStatements()
/*      */   {
/* 3808 */     return this.rewriteBatchedStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setRewriteBatchedStatements(boolean flag)
/*      */   {
/* 3815 */     this.rewriteBatchedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncationForReads()
/*      */   {
/* 3822 */     return this.jdbcCompliantTruncationForReads;
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 3830 */     this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads;
/*      */   }
/*      */ 
/*      */   public boolean getUseJvmCharsetConverters()
/*      */   {
/* 3837 */     return this.useJvmCharsetConverters.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseJvmCharsetConverters(boolean flag)
/*      */   {
/* 3844 */     this.useJvmCharsetConverters.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getPinGlobalTxToPhysicalConnection()
/*      */   {
/* 3851 */     return this.pinGlobalTxToPhysicalConnection.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag)
/*      */   {
/* 3858 */     this.pinGlobalTxToPhysicalConnection.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerfMetrics(boolean flag)
/*      */   {
/* 3870 */     setGatherPerformanceMetrics(flag);
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerfMetrics()
/*      */   {
/* 3877 */     return getGatherPerformanceMetrics();
/*      */   }
/*      */ 
/*      */   public void setUltraDevHack(boolean flag)
/*      */   {
/* 3884 */     setUseUltraDevWorkAround(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUltraDevHack()
/*      */   {
/* 3891 */     return getUseUltraDevWorkAround();
/*      */   }
/*      */ 
/*      */   public void setInteractiveClient(boolean property)
/*      */   {
/* 3898 */     setIsInteractiveClient(property);
/*      */   }
/*      */ 
/*      */   public void setSocketFactory(String name)
/*      */   {
/* 3905 */     setSocketFactoryClassName(name);
/*      */   }
/*      */ 
/*      */   public String getSocketFactory()
/*      */   {
/* 3912 */     return getSocketFactoryClassName();
/*      */   }
/*      */ 
/*      */   public void setUseServerPrepStmts(boolean flag)
/*      */   {
/* 3919 */     setUseServerPreparedStmts(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPrepStmts()
/*      */   {
/* 3926 */     return getUseServerPreparedStmts();
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStmts(boolean flag)
/*      */   {
/* 3933 */     setCacheCallableStatements(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStmts()
/*      */   {
/* 3940 */     return getCacheCallableStatements();
/*      */   }
/*      */ 
/*      */   public void setCachePrepStmts(boolean flag)
/*      */   {
/* 3947 */     setCachePreparedStatements(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCachePrepStmts()
/*      */   {
/* 3954 */     return getCachePreparedStatements();
/*      */   }
/*      */ 
/*      */   public void setCallableStmtCacheSize(int cacheSize)
/*      */   {
/* 3961 */     setCallableStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public int getCallableStmtCacheSize()
/*      */   {
/* 3968 */     return getCallableStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSize(int cacheSize)
/*      */   {
/* 3975 */     setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSize()
/*      */   {
/* 3982 */     return getPreparedStatementCacheSize();
/*      */   }
/*      */ 
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit)
/*      */   {
/* 3989 */     setPreparedStatementCacheSqlLimit(sqlLimit);
/*      */   }
/*      */ 
/*      */   public int getPrepStmtCacheSqlLimit()
/*      */   {
/* 3996 */     return getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */ 
/*      */   public boolean getNoAccessToProcedureBodies()
/*      */   {
/* 4003 */     return this.noAccessToProcedureBodies.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/* 4010 */     this.noAccessToProcedureBodies.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseOldAliasMetadataBehavior()
/*      */   {
/* 4017 */     return this.useOldAliasMetadataBehavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/* 4024 */     this.useOldAliasMetadataBehavior.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStorePassword()
/*      */   {
/* 4031 */     return this.clientCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStorePassword(String value)
/*      */   {
/* 4039 */     this.clientCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreType()
/*      */   {
/* 4046 */     return this.clientCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreType(String value)
/*      */   {
/* 4054 */     this.clientCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getClientCertificateKeyStoreUrl()
/*      */   {
/* 4061 */     return this.clientCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientCertificateKeyStoreUrl(String value)
/*      */   {
/* 4069 */     this.clientCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStorePassword()
/*      */   {
/* 4076 */     return this.trustCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/* 4084 */     this.trustCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreType()
/*      */   {
/* 4091 */     return this.trustCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreType(String value)
/*      */   {
/* 4099 */     this.trustCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */ 
/*      */   public String getTrustCertificateKeyStoreUrl()
/*      */   {
/* 4106 */     return this.trustCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/* 4114 */     this.trustCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */ 
/*      */   public boolean getUseSSPSCompatibleTimezoneShift()
/*      */   {
/* 4121 */     return this.useSSPSCompatibleTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/* 4128 */     this.useSSPSCompatibleTimezoneShift.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getTreatUtilDateAsTimestamp()
/*      */   {
/* 4135 */     return this.treatUtilDateAsTimestamp.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/* 4142 */     this.treatUtilDateAsTimestamp.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseFastDateParsing()
/*      */   {
/* 4149 */     return this.useFastDateParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseFastDateParsing(boolean flag)
/*      */   {
/* 4156 */     this.useFastDateParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getLocalSocketAddress()
/*      */   {
/* 4163 */     return this.localSocketAddress.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLocalSocketAddress(String address)
/*      */   {
/* 4170 */     this.localSocketAddress.setValue(address);
/*      */   }
/*      */ 
/*      */   public void setUseConfigs(String configs)
/*      */   {
/* 4177 */     this.useConfigs.setValue(configs);
/*      */   }
/*      */ 
/*      */   public String getUseConfigs()
/*      */   {
/* 4184 */     return this.useConfigs.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getGenerateSimpleParameterMetadata()
/*      */   {
/* 4192 */     return this.generateSimpleParameterMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag)
/*      */   {
/* 4199 */     this.generateSimpleParameterMetadata.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getLogXaCommands()
/*      */   {
/* 4206 */     return this.logXaCommands.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setLogXaCommands(boolean flag)
/*      */   {
/* 4213 */     this.logXaCommands.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getResultSetSizeThreshold()
/*      */   {
/* 4220 */     return this.resultSetSizeThreshold.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setResultSetSizeThreshold(int threshold)
/*      */   {
/* 4227 */     this.resultSetSizeThreshold.setValue(threshold);
/*      */   }
/*      */ 
/*      */   public int getNetTimeoutForStreamingResults()
/*      */   {
/* 4234 */     return this.netTimeoutForStreamingResults.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setNetTimeoutForStreamingResults(int value)
/*      */   {
/* 4241 */     this.netTimeoutForStreamingResults.setValue(value);
/*      */   }
/*      */ 
/*      */   public boolean getEnableQueryTimeouts()
/*      */   {
/* 4248 */     return this.enableQueryTimeouts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setEnableQueryTimeouts(boolean flag)
/*      */   {
/* 4255 */     this.enableQueryTimeouts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getPadCharsWithSpace()
/*      */   {
/* 4262 */     return this.padCharsWithSpace.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPadCharsWithSpace(boolean flag)
/*      */   {
/* 4269 */     this.padCharsWithSpace.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseDynamicCharsetInfo()
/*      */   {
/* 4276 */     return this.useDynamicCharsetInfo.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/* 4283 */     this.useDynamicCharsetInfo.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getClientInfoProvider()
/*      */   {
/* 4290 */     return this.clientInfoProvider.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setClientInfoProvider(String classname)
/*      */   {
/* 4297 */     this.clientInfoProvider.setValue(classname);
/*      */   }
/*      */ 
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 4301 */     return this.populateInsertRowWithDefaultValues.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 4305 */     this.populateInsertRowWithDefaultValues.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceStrategy() {
/* 4309 */     return this.loadBalanceStrategy.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 4313 */     this.loadBalanceStrategy.setValue(strategy);
/*      */   }
/*      */ 
/*      */   public boolean getTcpNoDelay() {
/* 4317 */     return this.tcpNoDelay.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setTcpNoDelay(boolean flag) {
/* 4321 */     this.tcpNoDelay.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getTcpKeepAlive() {
/* 4325 */     return this.tcpKeepAlive.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 4329 */     this.tcpKeepAlive.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getTcpRcvBuf() {
/* 4333 */     return this.tcpRcvBuf.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setTcpRcvBuf(int bufSize) {
/* 4337 */     this.tcpRcvBuf.setValue(bufSize);
/*      */   }
/*      */ 
/*      */   public int getTcpSndBuf() {
/* 4341 */     return this.tcpSndBuf.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setTcpSndBuf(int bufSize) {
/* 4345 */     this.tcpSndBuf.setValue(bufSize);
/*      */   }
/*      */ 
/*      */   public int getTcpTrafficClass() {
/* 4349 */     return this.tcpTrafficClass.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setTcpTrafficClass(int classFlags) {
/* 4353 */     this.tcpTrafficClass.setValue(classFlags);
/*      */   }
/*      */ 
/*      */   public boolean getUseNanosForElapsedTime() {
/* 4357 */     return this.useNanosForElapsedTime.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 4361 */     this.useNanosForElapsedTime.setValue(flag);
/*      */   }
/*      */ 
/*      */   public long getSlowQueryThresholdNanos() {
/* 4365 */     return this.slowQueryThresholdNanos.getValueAsLong();
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdNanos(long nanos) {
/* 4369 */     this.slowQueryThresholdNanos.setValue(nanos);
/*      */   }
/*      */ 
/*      */   public String getStatementInterceptors() {
/* 4373 */     return this.statementInterceptors.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setStatementInterceptors(String value) {
/* 4377 */     this.statementInterceptors.setValue(value);
/*      */   }
/*      */ 
/*      */   public boolean getUseDirectRowUnpack() {
/* 4381 */     return this.useDirectRowUnpack.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 4385 */     this.useDirectRowUnpack.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getLargeRowSizeThreshold() {
/* 4389 */     return this.largeRowSizeThreshold.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLargeRowSizeThreshold(String value) {
/*      */     try {
/* 4394 */       this.largeRowSizeThreshold.setValue(value);
/*      */     } catch (SQLException sqlEx) {
/* 4396 */       RuntimeException ex = new RuntimeException(sqlEx.getMessage());
/* 4397 */       ex.initCause(sqlEx);
/*      */ 
/* 4399 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 4404 */     return this.useBlobToStoreUTF8OutsideBMP.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 4408 */     this.useBlobToStoreUTF8OutsideBMP.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 4412 */     return this.utf8OutsideBmpExcludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 4416 */     this.utf8OutsideBmpExcludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */ 
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 4420 */     return this.utf8OutsideBmpIncludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 4424 */     this.utf8OutsideBmpIncludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 4428 */     return this.includeInnodbStatusInDeadlockExceptions.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 4432 */     this.includeInnodbStatusInDeadlockExceptions.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getBlobsAreStrings() {
/* 4436 */     return this.blobsAreStrings.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 4440 */     this.blobsAreStrings.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 4444 */     return this.functionsNeverReturnBlobs.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 4448 */     this.functionsNeverReturnBlobs.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getAutoSlowLog() {
/* 4452 */     return this.autoSlowLog.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoSlowLog(boolean flag) {
/* 4456 */     this.autoSlowLog.setValue(flag);
/*      */   }
/*      */ 
/*      */   public String getConnectionLifecycleInterceptors() {
/* 4460 */     return this.connectionLifecycleInterceptors.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 4464 */     this.connectionLifecycleInterceptors.setValue(interceptors);
/*      */   }
/*      */ 
/*      */   public String getProfilerEventHandler() {
/* 4468 */     return this.profilerEventHandler.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setProfilerEventHandler(String handler) {
/* 4472 */     this.profilerEventHandler.setValue(handler);
/*      */   }
/*      */ 
/*      */   public boolean getVerifyServerCertificate() {
/* 4476 */     return this.verifyServerCertificate.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 4480 */     this.verifyServerCertificate.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 4484 */     return this.useLegacyDatetimeCode.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 4488 */     this.useLegacyDatetimeCode.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 4492 */     return this.selfDestructOnPingSecondsLifetime.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) {
/* 4496 */     this.selfDestructOnPingSecondsLifetime.setValue(seconds);
/*      */   }
/*      */ 
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 4500 */     return this.selfDestructOnPingMaxOperations.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) {
/* 4504 */     this.selfDestructOnPingMaxOperations.setValue(maxOperations);
/*      */   }
/*      */ 
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 4508 */     return this.useColumnNamesInFindColumn.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseColumnNamesInFindColumn(boolean flag) {
/* 4512 */     this.useColumnNamesInFindColumn.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalTransactionState() {
/* 4516 */     return this.useLocalTransactionState.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseLocalTransactionState(boolean flag) {
/* 4520 */     this.useLocalTransactionState.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts() {
/* 4524 */     return this.compensateOnDuplicateKeyUpdateCounts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
/* 4528 */     this.compensateOnDuplicateKeyUpdateCounts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 4532 */     return this.loadBalanceBlacklistTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) {
/* 4536 */     this.loadBalanceBlacklistTimeout.setValue(loadBalanceBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public int getLoadBalancePingTimeout() {
/* 4540 */     return this.loadBalancePingTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setLoadBalancePingTimeout(int loadBalancePingTimeout) {
/* 4544 */     this.loadBalancePingTimeout.setValue(loadBalancePingTimeout);
/*      */   }
/*      */ 
/*      */   public void setRetriesAllDown(int retriesAllDown) {
/* 4548 */     this.retriesAllDown.setValue(retriesAllDown);
/*      */   }
/*      */ 
/*      */   public int getRetriesAllDown() {
/* 4552 */     return this.retriesAllDown.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setUseAffectedRows(boolean flag) {
/* 4556 */     this.useAffectedRows.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getUseAffectedRows() {
/* 4560 */     return this.useAffectedRows.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 4564 */     this.passwordCharacterEncoding.setValue(characterSet);
/*      */   }
/*      */ 
/*      */   public String getPasswordCharacterEncoding() {
/* 4568 */     return this.passwordCharacterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 4572 */     this.exceptionInterceptors.setValue(exceptionInterceptors);
/*      */   }
/*      */ 
/*      */   public String getExceptionInterceptors() {
/* 4576 */     return this.exceptionInterceptors.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setMaxAllowedPacket(int max) {
/* 4580 */     this.maxAllowedPacket.setValue(max);
/*      */   }
/*      */ 
/*      */   public int getMaxAllowedPacket() {
/* 4584 */     return this.maxAllowedPacket.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 4588 */     return this.queryTimeoutKillsConnection.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
/* 4592 */     this.queryTimeoutKillsConnection.setValue(queryTimeoutKillsConnection);
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceValidateConnectionOnSwapServer() {
/* 4596 */     return this.loadBalanceValidateConnectionOnSwapServer.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer)
/*      */   {
/* 4601 */     this.loadBalanceValidateConnectionOnSwapServer.setValue(loadBalanceValidateConnectionOnSwapServer);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceConnectionGroup()
/*      */   {
/* 4606 */     return this.loadBalanceConnectionGroup.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
/* 4610 */     this.loadBalanceConnectionGroup.setValue(loadBalanceConnectionGroup);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceExceptionChecker() {
/* 4614 */     return this.loadBalanceExceptionChecker.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
/* 4618 */     this.loadBalanceExceptionChecker.setValue(loadBalanceExceptionChecker);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLStateFailover() {
/* 4622 */     return this.loadBalanceSQLStateFailover.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
/* 4626 */     this.loadBalanceSQLStateFailover.setValue(loadBalanceSQLStateFailover);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceSQLExceptionSubclassFailover() {
/* 4630 */     return this.loadBalanceSQLExceptionSubclassFailover.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
/* 4634 */     this.loadBalanceSQLExceptionSubclassFailover.setValue(loadBalanceSQLExceptionSubclassFailover);
/*      */   }
/*      */ 
/*      */   public boolean getLoadBalanceEnableJMX() {
/* 4638 */     return this.loadBalanceEnableJMX.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
/* 4642 */     this.loadBalanceEnableJMX.setValue(loadBalanceEnableJMX);
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) {
/* 4646 */     this.loadBalanceAutoCommitStatementThreshold.setValue(loadBalanceAutoCommitStatementThreshold);
/*      */   }
/*      */ 
/*      */   public int getLoadBalanceAutoCommitStatementThreshold() {
/* 4650 */     return this.loadBalanceAutoCommitStatementThreshold.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
/* 4654 */     this.loadBalanceAutoCommitStatementRegex.setValue(loadBalanceAutoCommitStatementRegex);
/*      */   }
/*      */ 
/*      */   public String getLoadBalanceAutoCommitStatementRegex() {
/* 4658 */     return this.loadBalanceAutoCommitStatementRegex.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
/* 4662 */     this.includeThreadDumpInDeadlockExceptions.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadDumpInDeadlockExceptions() {
/* 4666 */     return this.includeThreadDumpInDeadlockExceptions.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setIncludeThreadNamesAsStatementComment(boolean flag) {
/* 4670 */     this.includeThreadNamesAsStatementComment.setValue(flag);
/*      */   }
/*      */ 
/*      */   public boolean getIncludeThreadNamesAsStatementComment() {
/* 4674 */     return this.includeThreadNamesAsStatementComment.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAuthenticationPlugins(String authenticationPlugins) {
/* 4678 */     this.authenticationPlugins.setValue(authenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getAuthenticationPlugins() {
/* 4682 */     return this.authenticationPlugins.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
/* 4686 */     this.disabledAuthenticationPlugins.setValue(disabledAuthenticationPlugins);
/*      */   }
/*      */ 
/*      */   public String getDisabledAuthenticationPlugins() {
/* 4690 */     return this.disabledAuthenticationPlugins.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
/* 4694 */     this.defaultAuthenticationPlugin.setValue(defaultAuthenticationPlugin);
/*      */   }
/*      */ 
/*      */   public String getDefaultAuthenticationPlugin()
/*      */   {
/* 4699 */     return this.defaultAuthenticationPlugin.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setParseInfoCacheFactory(String factoryClassname) {
/* 4703 */     this.parseInfoCacheFactory.setValue(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getParseInfoCacheFactory() {
/* 4707 */     return this.parseInfoCacheFactory.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setServerConfigCacheFactory(String factoryClassname) {
/* 4711 */     this.serverConfigCacheFactory.setValue(factoryClassname);
/*      */   }
/*      */ 
/*      */   public String getServerConfigCacheFactory() {
/* 4715 */     return this.serverConfigCacheFactory.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
/* 4719 */     this.disconnectOnExpiredPasswords.setValue(disconnectOnExpiredPasswords);
/*      */   }
/*      */ 
/*      */   public boolean getDisconnectOnExpiredPasswords() {
/* 4723 */     return this.disconnectOnExpiredPasswords.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  649 */       Field[] declaredFields = ConnectionPropertiesImpl.class.getDeclaredFields();
/*      */ 
/*  652 */       for (int i = 0; i < declaredFields.length; i++) {
/*  653 */         if (!ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType()))
/*      */           continue;
/*  655 */         PROPERTY_LIST.add(declaredFields[i]);
/*      */       }
/*      */     }
/*      */     catch (Exception ex) {
/*  659 */       RuntimeException rtEx = new RuntimeException();
/*  660 */       rtEx.initCause(ex);
/*      */ 
/*  662 */       throw rtEx;
/*      */     }
/*      */   }
/*      */ 
/*      */   class XmlMap
/*      */   {
/* 1886 */     protected Map<Integer, Map<String, ConnectionPropertiesImpl.ConnectionProperty>> ordered = new TreeMap();
/* 1887 */     protected Map<String, ConnectionPropertiesImpl.ConnectionProperty> alpha = new TreeMap();
/*      */ 
/*      */     XmlMap()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   class StringConnectionProperty extends ConnectionPropertiesImpl.ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 5432127962785948272L;
/*      */ 
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  555 */       this(propertyNameToSet, defaultValueToSet, null, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String[] allowableValuesToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  573 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String getValueAsString()
/*      */     {
/*  579 */       return (String)this.valueAsObject;
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  586 */       return (this.allowableValues != null) && (this.allowableValues.length > 0);
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  594 */       if (extractedValue != null) {
/*  595 */         validateStringValues(extractedValue);
/*      */ 
/*  597 */         this.valueAsObject = extractedValue;
/*      */       } else {
/*  599 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  607 */       return false;
/*      */     }
/*      */ 
/*      */     void setValue(String valueFlag) {
/*  611 */       this.valueAsObject = valueFlag;
/*      */     }
/*      */   }
/*      */ 
/*      */   class MemorySizeConnectionProperty extends ConnectionPropertiesImpl.IntegerConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7351065128998572656L;
/*      */     private String valueAsString;
/*      */ 
/*      */     MemorySizeConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  496 */       super(propertyNameToSet, defaultValueToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  502 */       this.valueAsString = extractedValue;
/*      */ 
/*  504 */       if (extractedValue != null) {
/*  505 */         if ((extractedValue.endsWith("k")) || (extractedValue.endsWith("K")) || (extractedValue.endsWith("kb")) || (extractedValue.endsWith("Kb")) || (extractedValue.endsWith("kB")))
/*      */         {
/*  510 */           this.multiplier = 1024;
/*  511 */           int indexOfK = StringUtils.indexOfIgnoreCase(extractedValue, "k");
/*      */ 
/*  513 */           extractedValue = extractedValue.substring(0, indexOfK);
/*  514 */         } else if ((extractedValue.endsWith("m")) || (extractedValue.endsWith("M")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("mb")) || (extractedValue.endsWith("Mb")) || (extractedValue.endsWith("mB")))
/*      */         {
/*  520 */           this.multiplier = 1048576;
/*  521 */           int indexOfM = StringUtils.indexOfIgnoreCase(extractedValue, "m");
/*      */ 
/*  523 */           extractedValue = extractedValue.substring(0, indexOfM);
/*  524 */         } else if ((extractedValue.endsWith("g")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("gb")) || (extractedValue.endsWith("Gb")) || (extractedValue.endsWith("gB")))
/*      */         {
/*  529 */           this.multiplier = 1073741824;
/*  530 */           int indexOfG = StringUtils.indexOfIgnoreCase(extractedValue, "g");
/*      */ 
/*  532 */           extractedValue = extractedValue.substring(0, indexOfG);
/*      */         }
/*      */       }
/*      */ 
/*  536 */       super.initializeFrom(extractedValue);
/*      */     }
/*      */ 
/*      */     void setValue(String value) throws SQLException {
/*  540 */       initializeFrom(value);
/*      */     }
/*      */ 
/*      */     String getValueAsString() {
/*  544 */       return this.valueAsString;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class LongConnectionProperty extends ConnectionPropertiesImpl.IntegerConnectionProperty
/*      */   {
/*      */     private static final long serialVersionUID = 6068572984340480895L;
/*      */ 
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, long lowerBoundToSet, long upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  442 */       super(propertyNameToSet, Long.valueOf(defaultValueToSet), null, (int)lowerBoundToSet, (int)upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  451 */       this(propertyNameToSet, defaultValueToSet, 0L, 0L, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     void setValue(long value)
/*      */     {
/*  458 */       this.valueAsObject = Long.valueOf(value);
/*      */     }
/*      */ 
/*      */     long getValueAsLong() {
/*  462 */       return ((Long)this.valueAsObject).longValue();
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue) throws SQLException {
/*  466 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  469 */           long longValue = Double.valueOf(extractedValue).longValue();
/*      */ 
/*  471 */           this.valueAsObject = Long.valueOf(longValue);
/*      */         } catch (NumberFormatException nfe) {
/*  473 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values. The value '" + extractedValue + "' can not be converted to a long integer.", "S1009", this.this$0.getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  481 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class IntegerConnectionProperty extends ConnectionPropertiesImpl.ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -3004305481796850832L;
/*  330 */     int multiplier = 1;
/*      */ 
/*      */     public IntegerConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  325 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  336 */       super(propertyNameToSet, Integer.valueOf(defaultValueToSet), null, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  354 */       this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*  362 */       return null;
/*      */     }
/*      */ 
/*      */     int getLowerBound()
/*      */     {
/*  369 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */     int getUpperBound()
/*      */     {
/*  376 */       return this.upperBound;
/*      */     }
/*      */ 
/*      */     int getValueAsInt() {
/*  380 */       return ((Integer)this.valueAsObject).intValue();
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  387 */       return false;
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  394 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  397 */           int intValue = Double.valueOf(extractedValue).intValue();
/*      */ 
/*  408 */           this.valueAsObject = Integer.valueOf(intValue * this.multiplier);
/*      */         } catch (NumberFormatException nfe) {
/*  410 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values. The value '" + extractedValue + "' can not be converted to an integer.", "S1009", this.this$0.getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  418 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  426 */       return getUpperBound() != getLowerBound();
/*      */     }
/*      */ 
/*      */     void setValue(int valueFlag) {
/*  430 */       this.valueAsObject = Integer.valueOf(valueFlag);
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     static final long serialVersionUID = -6644853639584478367L;
/*      */     String[] allowableValues;
/*      */     String categoryName;
/*      */     Object defaultValue;
/*      */     int lowerBound;
/*      */     int order;
/*      */     String propertyName;
/*      */     String sinceVersion;
/*      */     int upperBound;
/*      */     Object valueAsObject;
/*      */     boolean required;
/*      */     String description;
/*      */ 
/*      */     public ConnectionProperty()
/*      */     {
/*      */     }
/*      */ 
/*      */     ConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  154 */       this.description = descriptionToSet;
/*  155 */       this.propertyName = propertyNameToSet;
/*  156 */       this.defaultValue = defaultValueToSet;
/*  157 */       this.valueAsObject = defaultValueToSet;
/*  158 */       this.allowableValues = allowableValuesToSet;
/*  159 */       this.lowerBound = lowerBoundToSet;
/*  160 */       this.upperBound = upperBoundToSet;
/*  161 */       this.required = false;
/*  162 */       this.sinceVersion = sinceVersionToSet;
/*  163 */       this.categoryName = category;
/*  164 */       this.order = orderInCategory;
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues() {
/*  168 */       return this.allowableValues;
/*      */     }
/*      */ 
/*      */     String getCategoryName()
/*      */     {
/*  175 */       return this.categoryName;
/*      */     }
/*      */ 
/*      */     Object getDefaultValue() {
/*  179 */       return this.defaultValue;
/*      */     }
/*      */ 
/*      */     int getLowerBound() {
/*  183 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */     int getOrder()
/*      */     {
/*  190 */       return this.order;
/*      */     }
/*      */ 
/*      */     String getPropertyName() {
/*  194 */       return this.propertyName;
/*      */     }
/*      */ 
/*      */     int getUpperBound() {
/*  198 */       return this.upperBound;
/*      */     }
/*      */ 
/*      */     Object getValueAsObject() {
/*  202 */       return this.valueAsObject;
/*      */     }
/*      */     abstract boolean hasValueConstraints();
/*      */ 
/*      */     void initializeFrom(Properties extractFrom) throws SQLException {
/*  208 */       String extractedValue = extractFrom.getProperty(getPropertyName());
/*  209 */       extractFrom.remove(getPropertyName());
/*  210 */       initializeFrom(extractedValue);
/*      */     }
/*      */ 
/*      */     void initializeFrom(Reference ref) throws SQLException {
/*  214 */       RefAddr refAddr = ref.get(getPropertyName());
/*      */ 
/*  216 */       if (refAddr != null) {
/*  217 */         String refContentAsString = (String)refAddr.getContent();
/*      */ 
/*  219 */         initializeFrom(refContentAsString);
/*      */       }
/*      */     }
/*      */ 
/*      */     abstract void initializeFrom(String paramString)
/*      */       throws SQLException;
/*      */ 
/*      */     abstract boolean isRangeBased();
/*      */ 
/*      */     void setCategoryName(String categoryName)
/*      */     {
/*  232 */       this.categoryName = categoryName;
/*      */     }
/*      */ 
/*      */     void setOrder(int order)
/*      */     {
/*  240 */       this.order = order;
/*      */     }
/*      */ 
/*      */     void setValueAsObject(Object obj) {
/*  244 */       this.valueAsObject = obj;
/*      */     }
/*      */ 
/*      */     void storeTo(Reference ref) {
/*  248 */       if (getValueAsObject() != null)
/*  249 */         ref.add(new StringRefAddr(getPropertyName(), getValueAsObject().toString()));
/*      */     }
/*      */ 
/*      */     DriverPropertyInfo getAsDriverPropertyInfo()
/*      */     {
/*  255 */       DriverPropertyInfo dpi = new DriverPropertyInfo(this.propertyName, null);
/*  256 */       dpi.choices = getAllowableValues();
/*  257 */       dpi.value = (this.valueAsObject != null ? this.valueAsObject.toString() : null);
/*  258 */       dpi.required = this.required;
/*  259 */       dpi.description = this.description;
/*      */ 
/*  261 */       return dpi;
/*      */     }
/*      */ 
/*      */     void validateStringValues(String valueToValidate) throws SQLException
/*      */     {
/*  266 */       String[] validateAgainst = getAllowableValues();
/*      */ 
/*  268 */       if (valueToValidate == null) {
/*  269 */         return;
/*      */       }
/*      */ 
/*  272 */       if ((validateAgainst == null) || (validateAgainst.length == 0)) {
/*  273 */         return;
/*      */       }
/*      */ 
/*  276 */       for (int i = 0; i < validateAgainst.length; i++) {
/*  277 */         if ((validateAgainst[i] != null) && (validateAgainst[i].equalsIgnoreCase(valueToValidate)))
/*      */         {
/*  279 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  283 */       StringBuffer errorMessageBuf = new StringBuffer();
/*      */ 
/*  285 */       errorMessageBuf.append("The connection property '");
/*  286 */       errorMessageBuf.append(getPropertyName());
/*  287 */       errorMessageBuf.append("' only accepts values of the form: ");
/*      */ 
/*  289 */       if (validateAgainst.length != 0) {
/*  290 */         errorMessageBuf.append("'");
/*  291 */         errorMessageBuf.append(validateAgainst[0]);
/*  292 */         errorMessageBuf.append("'");
/*      */ 
/*  294 */         for (int i = 1; i < validateAgainst.length - 1; i++) {
/*  295 */           errorMessageBuf.append(", ");
/*  296 */           errorMessageBuf.append("'");
/*  297 */           errorMessageBuf.append(validateAgainst[i]);
/*  298 */           errorMessageBuf.append("'");
/*      */         }
/*      */ 
/*  301 */         errorMessageBuf.append(" or '");
/*  302 */         errorMessageBuf.append(validateAgainst[(validateAgainst.length - 1)]);
/*      */ 
/*  304 */         errorMessageBuf.append("'");
/*      */       }
/*      */ 
/*  307 */       errorMessageBuf.append(". The value '");
/*  308 */       errorMessageBuf.append(valueToValidate);
/*  309 */       errorMessageBuf.append("' is not in this set.");
/*      */ 
/*  311 */       throw SQLError.createSQLException(errorMessageBuf.toString(), "S1009", ConnectionPropertiesImpl.this.getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */ 
/*      */   class BooleanConnectionProperty extends ConnectionPropertiesImpl.ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 2540132501709159404L;
/*      */ 
/*      */     BooleanConnectionProperty(String propertyNameToSet, boolean defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*   71 */       super(propertyNameToSet, Boolean.valueOf(defaultValueToSet), null, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*   80 */       return new String[] { "true", "false", "yes", "no" };
/*      */     }
/*      */ 
/*      */     boolean getValueAsBoolean() {
/*   84 */       return ((Boolean)this.valueAsObject).booleanValue();
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*   91 */       return true;
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*   98 */       if (extractedValue != null) {
/*   99 */         validateStringValues(extractedValue);
/*      */ 
/*  101 */         this.valueAsObject = Boolean.valueOf((extractedValue.equalsIgnoreCase("TRUE")) || (extractedValue.equalsIgnoreCase("YES")));
/*      */       }
/*      */       else
/*      */       {
/*  105 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  113 */       return false;
/*      */     }
/*      */ 
/*      */     void setValue(boolean valueFlag) {
/*  117 */       this.valueAsObject = Boolean.valueOf(valueFlag);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionPropertiesImpl
 * JD-Core Version:    0.6.0
 */