/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import J;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ 
/*      */ public class LoadBalancingConnectionProxy
/*      */   implements InvocationHandler, PingTarget
/*      */ {
/*      */   private static Method getLocalTimeMethod;
/*   67 */   private long totalPhysicalConnections = 0L;
/*   68 */   private long activePhysicalConnections = 0L;
/*   69 */   private String hostToRemove = null;
/*   70 */   private long lastUsed = 0L;
/*   71 */   private long transactionCount = 0L;
/*   72 */   private ConnectionGroup connectionGroup = null;
/*   73 */   private String closedReason = null;
/*      */   public static final String BLACKLIST_TIMEOUT_PROPERTY_KEY = "loadBalanceBlacklistTimeout";
/*      */   protected MySQLConnection currentConn;
/*      */   protected List<String> hostList;
/*      */   protected Map<String, ConnectionImpl> liveConnections;
/*      */   private Map<ConnectionImpl, String> connectionsToHostsMap;
/*      */   private long[] responseTimes;
/*      */   private Map<String, Integer> hostsToListIndexMap;
/*  128 */   private boolean inTransaction = false;
/*      */ 
/*  130 */   private long transactionStartTime = 0L;
/*      */   private Properties localProps;
/*  134 */   private boolean isClosed = false;
/*      */   private BalanceStrategy balancer;
/*      */   private int retriesAllDown;
/*      */   private static Map<String, Long> globalBlacklist;
/*  142 */   private int globalBlacklistTimeout = 0;
/*      */ 
/*  144 */   private long connectionGroupProxyID = 0L;
/*      */   private LoadBalanceExceptionChecker exceptionChecker;
/*  148 */   private Map<Class<?>, Boolean> jdbcInterfacesForProxyCache = new HashMap();
/*      */ 
/*  150 */   private MySQLConnection thisAsConnection = null;
/*      */ 
/*  152 */   private int autoCommitSwapThreshold = 0;
/*      */   private static Constructor<?> JDBC_4_LB_CONNECTION_CTOR;
/*  685 */   private Map<Class<?>, Class<?>[]> allInterfacesToProxy = new HashMap();
/*      */ 
/*      */   LoadBalancingConnectionProxy(List<String> hosts, Properties props)
/*      */     throws SQLException
/*      */   {
/*  189 */     String group = props.getProperty("loadBalanceConnectionGroup", null);
/*      */ 
/*  191 */     boolean enableJMX = false;
/*  192 */     String enableJMXAsString = props.getProperty("loadBalanceEnableJMX", "false");
/*      */     try
/*      */     {
/*  195 */       enableJMX = Boolean.parseBoolean(enableJMXAsString);
/*      */     } catch (Exception e) {
/*  197 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForLoadBalanceEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
/*      */     }
/*      */ 
/*  203 */     if (group != null) {
/*  204 */       this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance(group);
/*  205 */       if (enableJMX) {
/*  206 */         ConnectionGroupManager.registerJmx();
/*      */       }
/*  208 */       this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy(this, hosts);
/*  209 */       hosts = new ArrayList(this.connectionGroup.getInitialHosts());
/*      */     }
/*      */ 
/*  212 */     this.hostList = hosts;
/*      */ 
/*  214 */     int numHosts = this.hostList.size();
/*      */ 
/*  216 */     this.liveConnections = new HashMap(numHosts);
/*  217 */     this.connectionsToHostsMap = new HashMap(numHosts);
/*  218 */     this.responseTimes = new long[numHosts];
/*  219 */     this.hostsToListIndexMap = new HashMap(numHosts);
/*      */ 
/*  221 */     this.localProps = ((Properties)props.clone());
/*  222 */     this.localProps.remove("HOST");
/*  223 */     this.localProps.remove("PORT");
/*      */ 
/*  225 */     for (int i = 0; i < numHosts; i++) {
/*  226 */       this.hostsToListIndexMap.put(this.hostList.get(i), Integer.valueOf(i));
/*  227 */       this.localProps.remove("HOST." + (i + 1));
/*      */ 
/*  229 */       this.localProps.remove("PORT." + (i + 1));
/*      */     }
/*      */ 
/*  233 */     this.localProps.remove("NUM_HOSTS");
/*  234 */     this.localProps.setProperty("useLocalSessionState", "true");
/*      */ 
/*  236 */     String strategy = this.localProps.getProperty("loadBalanceStrategy", "random");
/*      */ 
/*  239 */     String lbExceptionChecker = this.localProps.getProperty("loadBalanceExceptionChecker", "com.mysql.jdbc.StandardLoadBalanceExceptionChecker");
/*      */ 
/*  243 */     String retriesAllDownAsString = this.localProps.getProperty("retriesAllDown", "120");
/*      */     try
/*      */     {
/*  247 */       this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
/*      */     } catch (NumberFormatException nfe) {
/*  249 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009", null);
/*      */     }
/*      */ 
/*  254 */     String blacklistTimeoutAsString = this.localProps.getProperty("loadBalanceBlacklistTimeout", "0");
/*      */     try
/*      */     {
/*  258 */       this.globalBlacklistTimeout = Integer.parseInt(blacklistTimeoutAsString);
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/*  261 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForLoadBalanceBlacklistTimeout", new Object[] { retriesAllDownAsString }), "S1009", null);
/*      */     }
/*      */ 
/*  270 */     if ("random".equals(strategy)) {
/*  271 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, "com.mysql.jdbc.RandomBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0));
/*      */     }
/*  274 */     else if ("bestResponseTime".equals(strategy)) {
/*  275 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, "com.mysql.jdbc.BestResponseTimeBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0));
/*      */     }
/*      */     else
/*      */     {
/*  279 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, strategy, "InvalidLoadBalanceStrategy", null).get(0));
/*      */     }
/*      */ 
/*  283 */     String autoCommitSwapThresholdAsString = props.getProperty("loadBalanceAutoCommitStatementThreshold", "0");
/*      */     try
/*      */     {
/*  286 */       this.autoCommitSwapThreshold = Integer.parseInt(autoCommitSwapThresholdAsString);
/*      */     } catch (NumberFormatException nfe) {
/*  288 */       throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", new Object[] { autoCommitSwapThresholdAsString }), "S1009", null);
/*      */     }
/*      */ 
/*  294 */     String autoCommitSwapRegex = props.getProperty("loadBalanceAutoCommitStatementRegex", "");
/*  295 */     if (!"".equals(autoCommitSwapRegex)) {
/*      */       try {
/*  297 */         "".matches(autoCommitSwapRegex);
/*      */       } catch (Exception e) {
/*  299 */         throw SQLError.createSQLException(Messages.getString("LoadBalancingConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", new Object[] { autoCommitSwapRegex }), "S1009", null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  306 */     if (this.autoCommitSwapThreshold > 0) {
/*  307 */       String statementInterceptors = this.localProps.getProperty("statementInterceptors");
/*  308 */       if (statementInterceptors == null)
/*  309 */         this.localProps.setProperty("statementInterceptors", "com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
/*  310 */       else if (statementInterceptors.length() > 0) {
/*  311 */         this.localProps.setProperty("statementInterceptors", statementInterceptors + ",com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
/*      */       }
/*  313 */       props.setProperty("statementInterceptors", this.localProps.getProperty("statementInterceptors"));
/*      */     }
/*      */ 
/*  316 */     this.balancer.init(null, props);
/*      */ 
/*  319 */     this.exceptionChecker = ((LoadBalanceExceptionChecker)Util.loadExtensions(null, props, lbExceptionChecker, "InvalidLoadBalanceExceptionChecker", null).get(0));
/*      */ 
/*  321 */     this.exceptionChecker.init(null, props);
/*      */ 
/*  323 */     if ((Util.isJdbc4()) || (JDBC_4_LB_CONNECTION_CTOR != null)) {
/*  324 */       this.thisAsConnection = ((MySQLConnection)Util.handleNewInstance(JDBC_4_LB_CONNECTION_CTOR, new Object[] { this }, null));
/*      */     }
/*      */     else {
/*  327 */       this.thisAsConnection = new LoadBalancedMySQLConnection(this);
/*      */     }
/*  329 */     pickNewConnection();
/*      */   }
/*      */ 
/*      */   public synchronized ConnectionImpl createConnectionForHost(String hostPortSpec)
/*      */     throws SQLException
/*      */   {
/*  344 */     Properties connProps = (Properties)this.localProps.clone();
/*      */ 
/*  346 */     String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(hostPortSpec);
/*      */ 
/*  348 */     String hostName = hostPortPair[0];
/*  349 */     String portNumber = hostPortPair[1];
/*  350 */     String dbName = connProps.getProperty("DBNAME");
/*      */ 
/*  353 */     if (hostName == null) {
/*  354 */       throw new SQLException("Could not find a hostname to start a connection to");
/*      */     }
/*      */ 
/*  357 */     if (portNumber == null) {
/*  358 */       portNumber = "3306";
/*      */     }
/*      */ 
/*  361 */     connProps.setProperty("HOST", hostName);
/*  362 */     connProps.setProperty("PORT", portNumber);
/*      */ 
/*  364 */     connProps.setProperty("HOST.1", hostName);
/*      */ 
/*  366 */     connProps.setProperty("PORT.1", portNumber);
/*      */ 
/*  368 */     connProps.setProperty("NUM_HOSTS", "1");
/*  369 */     connProps.setProperty("roundRobinLoadBalance", "false");
/*      */ 
/*  376 */     ConnectionImpl conn = (ConnectionImpl)ConnectionImpl.getInstance(hostName, Integer.parseInt(portNumber), connProps, dbName, "jdbc:mysql://" + hostName + ":" + portNumber + "/");
/*      */ 
/*  382 */     this.liveConnections.put(hostPortSpec, conn);
/*  383 */     this.connectionsToHostsMap.put(conn, hostPortSpec);
/*      */ 
/*  386 */     this.activePhysicalConnections += 1L;
/*  387 */     this.totalPhysicalConnections += 1L;
/*      */ 
/*  389 */     conn.setProxy(this.thisAsConnection);
/*      */ 
/*  391 */     return conn;
/*      */   }
/*      */ 
/*      */   void dealWithInvocationException(InvocationTargetException e)
/*      */     throws SQLException, Throwable, InvocationTargetException
/*      */   {
/*  402 */     Throwable t = e.getTargetException();
/*      */ 
/*  404 */     if (t != null) {
/*  405 */       if (((t instanceof SQLException)) && (shouldExceptionTriggerFailover((SQLException)t))) {
/*  406 */         invalidateCurrentConnection();
/*  407 */         pickNewConnection();
/*      */       }
/*      */ 
/*  410 */       throw t;
/*      */     }
/*      */ 
/*  413 */     throw e;
/*      */   }
/*      */ 
/*      */   synchronized void invalidateCurrentConnection()
/*      */     throws SQLException
/*      */   {
/*  422 */     invalidateConnection(this.currentConn);
/*      */   }
/*      */ 
/*      */   synchronized void invalidateConnection(MySQLConnection conn)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  433 */       if (!conn.isClosed())
/*  434 */         conn.close();
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*      */     finally {
/*  440 */       if (isGlobalBlacklistEnabled()) {
/*  441 */         addToGlobalBlacklist((String)this.connectionsToHostsMap.get(conn));
/*      */       }
/*      */ 
/*  446 */       this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/*      */ 
/*  448 */       Object mappedHost = this.connectionsToHostsMap.remove(conn);
/*      */ 
/*  450 */       if ((mappedHost != null) && (this.hostsToListIndexMap.containsKey(mappedHost)))
/*      */       {
/*  452 */         int hostIndex = ((Integer)this.hostsToListIndexMap.get(mappedHost)).intValue();
/*      */ 
/*  455 */         synchronized (this.responseTimes) {
/*  456 */           this.responseTimes[hostIndex] = 0L;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void closeAllConnections()
/*      */   {
/*  464 */     synchronized (this)
/*      */     {
/*  466 */       Iterator allConnections = this.liveConnections.values().iterator();
/*      */ 
/*  468 */       while (allConnections.hasNext())
/*      */         try {
/*  470 */           this.activePhysicalConnections -= 1L;
/*  471 */           ((ConnectionImpl)allConnections.next()).close();
/*      */         }
/*      */         catch (SQLException e)
/*      */         {
/*      */         }
/*  476 */       if (!this.isClosed) {
/*  477 */         this.balancer.destroy();
/*  478 */         if (this.connectionGroup != null) {
/*  479 */           this.connectionGroup.closeConnectionProxy(this);
/*      */         }
/*      */       }
/*      */ 
/*  483 */       this.liveConnections.clear();
/*  484 */       this.connectionsToHostsMap.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object invoke(Object proxy, Method method, Object[] args)
/*      */     throws Throwable
/*      */   {
/*  491 */     return invoke(proxy, method, args, true);
/*      */   }
/*      */ 
/*      */   public synchronized Object invoke(Object proxy, Method method, Object[] args, boolean swapAtTransactionBoundary)
/*      */     throws Throwable
/*      */   {
/*  509 */     String methodName = method.getName();
/*      */ 
/*  511 */     if ("getLoadBalanceSafeProxy".equals(methodName)) {
/*  512 */       return this.currentConn;
/*      */     }
/*      */ 
/*  515 */     if (("equals".equals(methodName)) && (args.length == 1)) {
/*  516 */       if ((args[0] instanceof Proxy)) {
/*  517 */         return Boolean.valueOf(((Proxy)args[0]).equals(this));
/*      */       }
/*  519 */       return Boolean.valueOf(equals(args[0]));
/*      */     }
/*      */ 
/*  522 */     if ("hashCode".equals(methodName)) {
/*  523 */       return Integer.valueOf(hashCode());
/*      */     }
/*      */ 
/*  526 */     if ("close".equals(methodName)) {
/*  527 */       closeAllConnections();
/*      */ 
/*  529 */       this.isClosed = true;
/*  530 */       this.closedReason = "Connection explicitly closed.";
/*      */ 
/*  532 */       return null;
/*      */     }
/*      */ 
/*  535 */     if ("isClosed".equals(methodName)) {
/*  536 */       return Boolean.valueOf(this.isClosed);
/*      */     }
/*      */ 
/*  539 */     if (this.isClosed) {
/*  540 */       String reason = "No operations allowed after connection closed.";
/*  541 */       if (this.closedReason != null) {
/*  542 */         reason = reason + "  " + this.closedReason;
/*      */       }
/*  544 */       throw SQLError.createSQLException(reason, "08003", null);
/*      */     }
/*      */ 
/*  553 */     if (!this.inTransaction) {
/*  554 */       this.inTransaction = true;
/*  555 */       this.transactionStartTime = getLocalTimeBestResolution();
/*  556 */       this.transactionCount += 1L;
/*      */     }
/*      */ 
/*  559 */     Object result = null;
/*      */     try
/*      */     {
/*  562 */       this.lastUsed = System.currentTimeMillis();
/*  563 */       result = method.invoke(this.thisAsConnection, args);
/*      */ 
/*  565 */       if (result != null) {
/*  566 */         if ((result instanceof Statement)) {
/*  567 */           ((Statement)result).setPingTarget(this);
/*      */         }
/*      */ 
/*  570 */         result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*      */       }
/*      */     } catch (InvocationTargetException e) {
/*  573 */       dealWithInvocationException(e);
/*      */     } finally {
/*  575 */       if ((swapAtTransactionBoundary) && (("commit".equals(methodName)) || ("rollback".equals(methodName)))) {
/*  576 */         this.inTransaction = false;
/*      */ 
/*  579 */         String host = (String)this.connectionsToHostsMap.get(this.currentConn);
/*      */ 
/*  583 */         if (host != null) {
/*  584 */           synchronized (this.responseTimes) {
/*  585 */             int hostIndex = ((Integer)this.hostsToListIndexMap.get(host)).intValue();
/*      */ 
/*  588 */             if (hostIndex < this.responseTimes.length) {
/*  589 */               this.responseTimes[hostIndex] = (getLocalTimeBestResolution() - this.transactionStartTime);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  594 */         pickNewConnection();
/*      */       }
/*      */     }
/*      */ 
/*  598 */     return result;
/*      */   }
/*      */ 
/*      */   protected synchronized void pickNewConnection()
/*      */     throws SQLException
/*      */   {
/*  608 */     if (this.currentConn == null) {
/*  609 */       this.currentConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*      */ 
/*  613 */       return;
/*      */     }
/*      */ 
/*  616 */     if (this.currentConn.isClosed()) {
/*  617 */       invalidateCurrentConnection();
/*      */     }
/*      */ 
/*  620 */     int pingTimeout = this.currentConn.getLoadBalancePingTimeout();
/*  621 */     boolean pingBeforeReturn = this.currentConn.getLoadBalanceValidateConnectionOnSwapServer();
/*      */ 
/*  623 */     int hostsTried = 0; for (int hostsToTry = this.hostList.size(); hostsTried <= hostsToTry; hostsTried++) {
/*  624 */       ConnectionImpl newConn = null;
/*      */       try {
/*  626 */         newConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*      */ 
/*  632 */         if (this.currentConn != null) {
/*  633 */           if (pingBeforeReturn) {
/*  634 */             if (pingTimeout == 0)
/*  635 */               newConn.ping();
/*      */             else {
/*  637 */               newConn.pingInternal(true, pingTimeout);
/*      */             }
/*      */           }
/*      */ 
/*  641 */           syncSessionState(this.currentConn, newConn);
/*      */         }
/*      */ 
/*  644 */         this.currentConn = newConn;
/*  645 */         return;
/*      */       }
/*      */       catch (SQLException e) {
/*  648 */         if ((!shouldExceptionTriggerFailover(e)) || (newConn == null)) {
/*      */           continue;
/*      */         }
/*  651 */         invalidateConnection(newConn);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  657 */     this.isClosed = true;
/*  658 */     this.closedReason = "Connection closed after inability to pick valid new connection during fail-over.";
/*      */   }
/*      */ 
/*      */   Object proxyIfInterfaceIsJdbc(Object toProxy, Class<?> clazz)
/*      */   {
/*  673 */     if (isInterfaceJdbc(clazz))
/*      */     {
/*  675 */       Class[] interfacesToProxy = getAllInterfacesToProxy(clazz);
/*      */ 
/*  677 */       return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfacesToProxy, createConnectionProxy(toProxy));
/*      */     }
/*      */ 
/*  682 */     return toProxy;
/*      */   }
/*      */ 
/*      */   private Class<?>[] getAllInterfacesToProxy(Class<?> clazz)
/*      */   {
/*  688 */     Class[] interfacesToProxy = (Class[])this.allInterfacesToProxy.get(clazz);
/*      */ 
/*  690 */     if (interfacesToProxy != null) {
/*  691 */       return interfacesToProxy;
/*      */     }
/*      */ 
/*  694 */     List interfaces = new LinkedList();
/*      */ 
/*  696 */     Class superClass = clazz;
/*      */ 
/*  698 */     while (!superClass.equals(Object.class)) {
/*  699 */       Class[] declared = superClass.getInterfaces();
/*      */ 
/*  701 */       for (int i = 0; i < declared.length; i++) {
/*  702 */         interfaces.add(declared[i]);
/*      */       }
/*      */ 
/*  705 */       superClass = superClass.getSuperclass();
/*      */     }
/*      */ 
/*  708 */     interfacesToProxy = new Class[interfaces.size()];
/*  709 */     interfaces.toArray(interfacesToProxy);
/*      */ 
/*  711 */     this.allInterfacesToProxy.put(clazz, interfacesToProxy);
/*      */ 
/*  713 */     return interfacesToProxy;
/*      */   }
/*      */ 
/*      */   private boolean isInterfaceJdbc(Class<?> clazz)
/*      */   {
/*  718 */     if (this.jdbcInterfacesForProxyCache.containsKey(clazz)) {
/*  719 */       return ((Boolean)this.jdbcInterfacesForProxyCache.get(clazz)).booleanValue();
/*      */     }
/*      */ 
/*  722 */     Class[] interfaces = clazz.getInterfaces();
/*      */ 
/*  724 */     for (int i = 0; i < interfaces.length; i++) {
/*  725 */       String packageName = interfaces[i].getPackage().getName();
/*      */ 
/*  727 */       if (("java.sql".equals(packageName)) || ("javax.sql".equals(packageName)) || ("com.mysql.jdbc".equals(packageName)))
/*      */       {
/*  730 */         this.jdbcInterfacesForProxyCache.put(clazz, Boolean.valueOf(true));
/*      */ 
/*  732 */         return true;
/*      */       }
/*      */ 
/*  735 */       if (isInterfaceJdbc(interfaces[i])) {
/*  736 */         this.jdbcInterfacesForProxyCache.put(clazz, Boolean.valueOf(true));
/*      */ 
/*  738 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  742 */     this.jdbcInterfacesForProxyCache.put(clazz, Boolean.valueOf(false));
/*  743 */     return false;
/*      */   }
/*      */ 
/*      */   protected ConnectionErrorFiringInvocationHandler createConnectionProxy(Object toProxy)
/*      */   {
/*  749 */     return new ConnectionErrorFiringInvocationHandler(toProxy);
/*      */   }
/*      */ 
/*      */   private static long getLocalTimeBestResolution()
/*      */   {
/*  757 */     if (getLocalTimeMethod != null)
/*      */       try {
/*  759 */         return ((Long)getLocalTimeMethod.invoke(null, (Object[])null)).longValue();
/*      */       }
/*      */       catch (IllegalArgumentException e)
/*      */       {
/*      */       }
/*      */       catch (IllegalAccessException e)
/*      */       {
/*      */       }
/*      */       catch (InvocationTargetException e)
/*      */       {
/*      */       }
/*  770 */     return System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   public synchronized void doPing() throws SQLException {
/*  774 */     SQLException se = null;
/*  775 */     boolean foundHost = false;
/*  776 */     int pingTimeout = this.currentConn.getLoadBalancePingTimeout();
/*      */     Iterator i;
/*  777 */     synchronized (this) {
/*  778 */       for (i = this.hostList.iterator(); i.hasNext(); ) {
/*  779 */         String host = (String)i.next();
/*  780 */         ConnectionImpl conn = (ConnectionImpl)this.liveConnections.get(host);
/*  781 */         if (conn == null)
/*      */           continue;
/*      */         try
/*      */         {
/*  785 */           if (pingTimeout == 0)
/*  786 */             conn.ping();
/*      */           else {
/*  788 */             conn.pingInternal(true, pingTimeout);
/*      */           }
/*  790 */           foundHost = true;
/*      */         } catch (SQLException e) {
/*  792 */           this.activePhysicalConnections -= 1L;
/*      */ 
/*  795 */           if (host.equals(this.connectionsToHostsMap.get(this.currentConn)))
/*      */           {
/*  799 */             closeAllConnections();
/*  800 */             this.isClosed = true;
/*  801 */             this.closedReason = "Connection closed because ping of current connection failed.";
/*  802 */             throw e;
/*      */           }
/*      */ 
/*  807 */           if (e.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime")))
/*      */           {
/*  813 */             if (se == null)
/*  814 */               se = e;
/*      */           }
/*      */           else
/*      */           {
/*  818 */             se = e;
/*  819 */             if (isGlobalBlacklistEnabled()) {
/*  820 */               addToGlobalBlacklist(host);
/*      */             }
/*      */           }
/*      */ 
/*  824 */           this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  830 */     if (!foundHost) {
/*  831 */       closeAllConnections();
/*  832 */       this.isClosed = true;
/*  833 */       this.closedReason = "Connection closed due to inability to ping any active connections.";
/*      */ 
/*  835 */       if (se != null) {
/*  836 */         throw se;
/*      */       }
/*      */ 
/*  840 */       ((ConnectionImpl)this.currentConn).throwConnectionClosedException();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addToGlobalBlacklist(String host, long timeout)
/*      */   {
/*  846 */     if (isGlobalBlacklistEnabled())
/*  847 */       synchronized (globalBlacklist) {
/*  848 */         globalBlacklist.put(host, Long.valueOf(timeout));
/*      */       }
/*      */   }
/*      */ 
/*      */   public void addToGlobalBlacklist(String host)
/*      */   {
/*  854 */     addToGlobalBlacklist(host, System.currentTimeMillis() + this.globalBlacklistTimeout);
/*      */   }
/*      */ 
/*      */   public boolean isGlobalBlacklistEnabled()
/*      */   {
/*  860 */     return this.globalBlacklistTimeout > 0;
/*      */   }
/*      */ 
/*      */   public synchronized Map<String, Long> getGlobalBlacklist() {
/*  864 */     if (!isGlobalBlacklistEnabled()) {
/*  865 */       String localHostToRemove = this.hostToRemove;
/*      */ 
/*  867 */       if (this.hostToRemove != null) {
/*  868 */         HashMap fakedBlacklist = new HashMap();
/*  869 */         fakedBlacklist.put(localHostToRemove, Long.valueOf(System.currentTimeMillis() + 5000L));
/*  870 */         return fakedBlacklist;
/*      */       }
/*      */ 
/*  873 */       return new HashMap(1);
/*      */     }
/*      */ 
/*  877 */     Map blacklistClone = new HashMap(globalBlacklist.size());
/*      */ 
/*  880 */     synchronized (globalBlacklist) {
/*  881 */       blacklistClone.putAll(globalBlacklist);
/*      */     }
/*  883 */     Set keys = blacklistClone.keySet();
/*      */ 
/*  886 */     keys.retainAll(this.hostList);
/*      */ 
/*  889 */     for (Iterator i = keys.iterator(); i.hasNext(); ) {
/*  890 */       String host = (String)i.next();
/*      */ 
/*  893 */       Long timeout = (Long)globalBlacklist.get(host);
/*  894 */       if ((timeout != null) && (timeout.longValue() < System.currentTimeMillis()))
/*      */       {
/*  897 */         synchronized (globalBlacklist) {
/*  898 */           globalBlacklist.remove(host);
/*      */         }
/*  900 */         i.remove();
/*      */       }
/*      */     }
/*      */ 
/*  904 */     if (keys.size() == this.hostList.size())
/*      */     {
/*  910 */       return new HashMap(1);
/*      */     }
/*      */ 
/*  913 */     return blacklistClone;
/*      */   }
/*      */ 
/*      */   public boolean shouldExceptionTriggerFailover(SQLException ex) {
/*  917 */     return this.exceptionChecker.shouldExceptionTriggerFailover(ex);
/*      */   }
/*      */ 
/*      */   public void removeHostWhenNotInUse(String host)
/*      */     throws SQLException
/*      */   {
/*  923 */     int timeBetweenChecks = 1000;
/*  924 */     long timeBeforeHardFail = 15000L;
/*      */ 
/*  926 */     synchronized (this) {
/*  927 */       addToGlobalBlacklist(host, timeBeforeHardFail + 1000L);
/*      */ 
/*  929 */       long cur = System.currentTimeMillis();
/*      */ 
/*  931 */       while (System.currentTimeMillis() - timeBeforeHardFail < cur)
/*      */       {
/*  933 */         this.hostToRemove = host;
/*      */ 
/*  935 */         if (!host.equals(this.currentConn.getHost())) {
/*  936 */           removeHost(host);
/*  937 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  943 */       Thread.sleep(timeBetweenChecks);
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/*      */     }
/*  948 */     removeHost(host);
/*      */   }
/*      */ 
/*      */   public synchronized void removeHost(String host) throws SQLException
/*      */   {
/*  953 */     if (this.connectionGroup != null) {
/*  954 */       if ((this.connectionGroup.getInitialHosts().size() == 1) && (this.connectionGroup.getInitialHosts().contains(host)))
/*      */       {
/*  956 */         throw SQLError.createSQLException("Cannot remove only configured host.", null);
/*      */       }
/*      */ 
/*  960 */       this.hostToRemove = host;
/*      */ 
/*  962 */       if (host.equals(this.currentConn.getHost())) {
/*  963 */         closeAllConnections();
/*      */       } else {
/*  965 */         this.connectionsToHostsMap.remove(this.liveConnections.remove(host));
/*      */ 
/*  967 */         Integer idx = (Integer)this.hostsToListIndexMap.remove(host);
/*  968 */         long[] newResponseTimes = new long[this.responseTimes.length - 1];
/*  969 */         int newIdx = 0;
/*  970 */         for (Iterator i = this.hostList.iterator(); i.hasNext(); newIdx++) {
/*  971 */           String copyHost = (String)i.next();
/*  972 */           if ((idx == null) || (idx.intValue() >= this.responseTimes.length))
/*      */             continue;
/*  974 */           newResponseTimes[newIdx] = this.responseTimes[idx.intValue()];
/*      */ 
/*  976 */           this.hostsToListIndexMap.put(copyHost, Integer.valueOf(newIdx));
/*      */         }
/*      */ 
/*  980 */         this.responseTimes = newResponseTimes;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean addHost(String host)
/*      */   {
/*  988 */     if (this.hostsToListIndexMap.containsKey(host)) {
/*  989 */       return false;
/*      */     }
/*      */ 
/*  992 */     long[] newResponseTimes = new long[this.responseTimes.length + 1];
/*      */ 
/*  994 */     for (int i = 0; i < this.responseTimes.length; i++) {
/*  995 */       newResponseTimes[i] = this.responseTimes[i];
/*      */     }
/*      */ 
/*  998 */     this.responseTimes = newResponseTimes;
/*  999 */     this.hostList.add(host);
/* 1000 */     this.hostsToListIndexMap.put(host, Integer.valueOf(this.responseTimes.length - 1));
/*      */ 
/* 1003 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized long getLastUsed() {
/* 1007 */     return this.lastUsed;
/*      */   }
/*      */ 
/*      */   public synchronized boolean inTransaction() {
/* 1011 */     return this.inTransaction;
/*      */   }
/*      */ 
/*      */   public synchronized long getTransactionCount() {
/* 1015 */     return this.transactionCount;
/*      */   }
/*      */ 
/*      */   public synchronized long getActivePhysicalConnectionCount() {
/* 1019 */     return this.activePhysicalConnections;
/*      */   }
/*      */ 
/*      */   public synchronized long getTotalPhysicalConnectionCount() {
/* 1023 */     return this.totalPhysicalConnections;
/*      */   }
/*      */ 
/*      */   public synchronized long getConnectionGroupProxyID() {
/* 1027 */     return this.connectionGroupProxyID;
/*      */   }
/*      */ 
/*      */   public synchronized String getCurrentActiveHost() {
/* 1031 */     MySQLConnection c = this.currentConn;
/* 1032 */     if (c != null) {
/* 1033 */       Object o = this.connectionsToHostsMap.get(c);
/* 1034 */       if (o != null) {
/* 1035 */         return o.toString();
/*      */       }
/*      */     }
/* 1038 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized long getCurrentTransactionDuration()
/*      */   {
/* 1043 */     if ((this.inTransaction) && (this.transactionStartTime > 0L)) {
/* 1044 */       return getLocalTimeBestResolution() - this.transactionStartTime;
/*      */     }
/*      */ 
/* 1047 */     return 0L;
/*      */   }
/*      */ 
/*      */   protected void syncSessionState(Connection initial, Connection target) throws SQLException
/*      */   {
/* 1052 */     if ((initial == null) || (target == null)) {
/* 1053 */       return;
/*      */     }
/* 1055 */     target.setAutoCommit(initial.getAutoCommit());
/* 1056 */     target.setCatalog(initial.getCatalog());
/* 1057 */     target.setTransactionIsolation(initial.getTransactionIsolation());
/* 1058 */     target.setReadOnly(initial.isReadOnly());
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*   79 */       getLocalTimeMethod = System.class.getMethod("nanoTime", new Class[0]);
/*      */     }
/*      */     catch (SecurityException e)
/*      */     {
/*      */     }
/*      */     catch (NoSuchMethodException e)
/*      */     {
/*      */     }
/*      */ 
/*  140 */     globalBlacklist = new HashMap();
/*      */ 
/*  157 */     if (Util.isJdbc4())
/*      */       try {
/*  159 */         JDBC_4_LB_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4LoadBalancedMySQLConnection").getConstructor(new Class[] { LoadBalancingConnectionProxy.class });
/*      */       }
/*      */       catch (SecurityException e)
/*      */       {
/*  163 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  165 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  167 */         throw new RuntimeException(e);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected class ConnectionErrorFiringInvocationHandler
/*      */     implements InvocationHandler
/*      */   {
/*   92 */     Object invokeOn = null;
/*      */ 
/*      */     public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
/*   95 */       this.invokeOn = toInvokeOn;
/*      */     }
/*      */ 
/*      */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*      */     {
/*  100 */       Object result = null;
/*      */       try
/*      */       {
/*  103 */         result = method.invoke(this.invokeOn, args);
/*      */ 
/*  105 */         if (result != null)
/*  106 */           result = LoadBalancingConnectionProxy.this.proxyIfInterfaceIsJdbc(result, result.getClass());
/*      */       }
/*      */       catch (InvocationTargetException e) {
/*  109 */         LoadBalancingConnectionProxy.this.dealWithInvocationException(e);
/*      */       }
/*      */ 
/*  112 */       return result;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.LoadBalancingConnectionProxy
 * JD-Core Version:    0.6.0
 */