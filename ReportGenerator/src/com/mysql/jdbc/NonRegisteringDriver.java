/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   private static final String ALLOWED_QUOTES = "\"'";
/*     */   private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
/*     */   private static final String URL_PREFIX = "jdbc:mysql://";
/*     */   private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
/*     */   private static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
/*  83 */   protected static final ConcurrentHashMap<ConnectionPhantomReference, ConnectionPhantomReference> connectionPhantomRefs = new ConcurrentHashMap();
/*     */ 
/*  85 */   protected static final ReferenceQueue<ConnectionImpl> refQueue = new ReferenceQueue();
/*     */   public static final String DBNAME_PROPERTY_KEY = "DBNAME";
/*     */   public static final boolean DEBUG = false;
/*     */   public static final int HOST_NAME_INDEX = 0;
/*     */   public static final String HOST_PROPERTY_KEY = "HOST";
/*     */   public static final String NUM_HOSTS_PROPERTY_KEY = "NUM_HOSTS";
/*     */   public static final String PASSWORD_PROPERTY_KEY = "password";
/*     */   public static final int PORT_NUMBER_INDEX = 1;
/*     */   public static final String PORT_PROPERTY_KEY = "PORT";
/*     */   public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
/*     */   public static final boolean TRACE = false;
/*     */   public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
/*     */   public static final String USER_PROPERTY_KEY = "user";
/*     */   public static final String PROTOCOL_PROPERTY_KEY = "PROTOCOL";
/*     */   public static final String PATH_PROPERTY_KEY = "PATH";
/*     */ 
/*     */   static int getMajorVersionInternal()
/*     */   {
/* 150 */     return safeIntParse("5");
/*     */   }
/*     */ 
/*     */   static int getMinorVersionInternal()
/*     */   {
/* 159 */     return safeIntParse("1");
/*     */   }
/*     */ 
/*     */   protected static String[] parseHostPortPair(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 180 */     String[] splitValues = new String[2];
/*     */ 
/* 182 */     if (StringUtils.startsWithIgnoreCaseAndWs(hostPortPair, "address")) {
/* 183 */       splitValues[0] = hostPortPair.trim();
/* 184 */       splitValues[1] = null;
/*     */ 
/* 186 */       return splitValues;
/*     */     }
/*     */ 
/* 189 */     int portIndex = hostPortPair.indexOf(":");
/*     */ 
/* 191 */     String hostname = null;
/*     */ 
/* 193 */     if (portIndex != -1) {
/* 194 */       if (portIndex + 1 < hostPortPair.length()) {
/* 195 */         String portAsString = hostPortPair.substring(portIndex + 1);
/* 196 */         hostname = hostPortPair.substring(0, portIndex);
/*     */ 
/* 198 */         splitValues[0] = hostname;
/*     */ 
/* 200 */         splitValues[1] = portAsString;
/*     */       } else {
/* 202 */         throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.37"), "01S00", null);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 207 */       splitValues[0] = hostPortPair;
/* 208 */       splitValues[1] = null;
/*     */     }
/*     */ 
/* 211 */     return splitValues;
/*     */   }
/*     */ 
/*     */   private static int safeIntParse(String intAsString) {
/*     */     try {
/* 216 */       return Integer.parseInt(intAsString); } catch (NumberFormatException nfe) {
/*     */     }
/* 218 */     return 0;
/*     */   }
/*     */ 
/*     */   public NonRegisteringDriver()
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/* 248 */     return parseURL(url, null) != null;
/*     */   }
/*     */ 
/*     */   public java.sql.Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 297 */     if (url != null) {
/* 298 */       if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://"))
/* 299 */         return connectLoadBalanced(url, info);
/* 300 */       if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://"))
/*     */       {
/* 302 */         return connectReplicationConnection(url, info);
/*     */       }
/*     */     }
/*     */ 
/* 306 */     Properties props = null;
/*     */ 
/* 308 */     if ((props = parseURL(url, info)) == null) {
/* 309 */       return null;
/*     */     }
/*     */ 
/* 312 */     if (!"1".equals(props.getProperty("NUM_HOSTS")))
/* 313 */       return connectFailover(url, info);
/*     */     SQLException sqlEx;
/*     */     try {
/* 317 */       Connection newConn = ConnectionImpl.getInstance(host(props), port(props), props, database(props), url);
/*     */ 
/* 320 */       return newConn;
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 324 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 326 */       sqlEx = SQLError.createSQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), "08001", null);
/*     */ 
/* 332 */       sqlEx.initCause(ex);
/*     */     }
/* 334 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   protected static void trackConnection(Connection newConn)
/*     */   {
/* 340 */     ConnectionPhantomReference phantomRef = new ConnectionPhantomReference((ConnectionImpl)newConn, refQueue);
/* 341 */     connectionPhantomRefs.put(phantomRef, phantomRef);
/*     */   }
/*     */ 
/*     */   private java.sql.Connection connectLoadBalanced(String url, Properties info) throws SQLException
/*     */   {
/* 346 */     Properties parsedProps = parseURL(url, info);
/*     */ 
/* 348 */     if (parsedProps == null) {
/* 349 */       return null;
/*     */     }
/*     */ 
/* 353 */     parsedProps.remove("roundRobinLoadBalance");
/*     */ 
/* 355 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */ 
/* 357 */     List hostList = new ArrayList();
/*     */ 
/* 359 */     for (int i = 0; i < numHosts; i++) {
/* 360 */       int index = i + 1;
/*     */ 
/* 362 */       hostList.add(parsedProps.getProperty(new StringBuilder().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuilder().append("PORT.").append(index).toString()));
/*     */     }
/*     */ 
/* 366 */     LoadBalancingConnectionProxy proxyBal = new LoadBalancingConnectionProxy(hostList, parsedProps);
/*     */ 
/* 369 */     return (java.sql.Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, proxyBal);
/*     */   }
/*     */ 
/*     */   private java.sql.Connection connectFailover(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 376 */     Properties parsedProps = parseURL(url, info);
/*     */ 
/* 378 */     if (parsedProps == null) {
/* 379 */       return null;
/*     */     }
/*     */ 
/* 383 */     parsedProps.remove("roundRobinLoadBalance");
/* 384 */     parsedProps.setProperty("autoReconnect", "false");
/*     */ 
/* 386 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */ 
/* 389 */     List hostList = new ArrayList();
/*     */ 
/* 391 */     for (int i = 0; i < numHosts; i++) {
/* 392 */       int index = i + 1;
/*     */ 
/* 394 */       hostList.add(parsedProps.getProperty(new StringBuilder().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuilder().append("PORT.").append(index).toString()));
/*     */     }
/*     */ 
/* 400 */     FailoverConnectionProxy connProxy = new FailoverConnectionProxy(hostList, parsedProps);
/*     */ 
/* 403 */     return (java.sql.Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, connProxy);
/*     */   }
/*     */ 
/*     */   protected java.sql.Connection connectReplicationConnection(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 410 */     Properties parsedProps = parseURL(url, info);
/*     */ 
/* 412 */     if (parsedProps == null) {
/* 413 */       return null;
/*     */     }
/*     */ 
/* 416 */     Properties masterProps = (Properties)parsedProps.clone();
/* 417 */     Properties slavesProps = (Properties)parsedProps.clone();
/*     */ 
/* 421 */     slavesProps.setProperty("com.mysql.jdbc.ReplicationConnection.isSlave", "true");
/*     */ 
/* 424 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */ 
/* 426 */     if (numHosts < 2) {
/* 427 */       throw SQLError.createSQLException("Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", "01S00", null);
/*     */     }
/*     */ 
/* 433 */     for (int i = 1; i < numHosts; i++) {
/* 434 */       int index = i + 1;
/*     */ 
/* 436 */       masterProps.remove("HOST." + index);
/* 437 */       masterProps.remove("PORT." + index);
/*     */ 
/* 439 */       slavesProps.setProperty("HOST." + i, parsedProps.getProperty("HOST." + index));
/* 440 */       slavesProps.setProperty("PORT." + i, parsedProps.getProperty("PORT." + index));
/*     */     }
/*     */ 
/* 443 */     masterProps.setProperty("NUM_HOSTS", "1");
/* 444 */     slavesProps.remove("HOST." + numHosts);
/* 445 */     slavesProps.remove("PORT." + numHosts);
/* 446 */     slavesProps.setProperty("NUM_HOSTS", String.valueOf(numHosts - 1));
/* 447 */     slavesProps.setProperty("HOST", slavesProps.getProperty("HOST.1"));
/* 448 */     slavesProps.setProperty("PORT", slavesProps.getProperty("PORT.1"));
/*     */ 
/* 450 */     return new ReplicationConnection(masterProps, slavesProps);
/*     */   }
/*     */ 
/*     */   public String database(Properties props)
/*     */   {
/* 462 */     return props.getProperty("DBNAME");
/*     */   }
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 471 */     return getMajorVersionInternal();
/*     */   }
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 480 */     return getMinorVersionInternal();
/*     */   }
/*     */ 
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 511 */     if (info == null) {
/* 512 */       info = new Properties();
/*     */     }
/*     */ 
/* 515 */     if ((url != null) && (url.startsWith("jdbc:mysql://"))) {
/* 516 */       info = parseURL(url, info);
/*     */     }
/*     */ 
/* 519 */     DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST"));
/*     */ 
/* 521 */     hostProp.required = true;
/* 522 */     hostProp.description = Messages.getString("NonRegisteringDriver.3");
/*     */ 
/* 524 */     DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", "3306"));
/*     */ 
/* 526 */     portProp.required = false;
/* 527 */     portProp.description = Messages.getString("NonRegisteringDriver.7");
/*     */ 
/* 529 */     DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME"));
/*     */ 
/* 531 */     dbProp.required = false;
/* 532 */     dbProp.description = "Database name";
/*     */ 
/* 534 */     DriverPropertyInfo userProp = new DriverPropertyInfo("user", info.getProperty("user"));
/*     */ 
/* 536 */     userProp.required = true;
/* 537 */     userProp.description = Messages.getString("NonRegisteringDriver.13");
/*     */ 
/* 539 */     DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", info.getProperty("password"));
/*     */ 
/* 542 */     passwordProp.required = true;
/* 543 */     passwordProp.description = Messages.getString("NonRegisteringDriver.16");
/*     */ 
/* 546 */     DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo(info, 5);
/*     */ 
/* 549 */     dpi[0] = hostProp;
/* 550 */     dpi[1] = portProp;
/* 551 */     dpi[2] = dbProp;
/* 552 */     dpi[3] = userProp;
/* 553 */     dpi[4] = passwordProp;
/*     */ 
/* 555 */     return dpi;
/*     */   }
/*     */ 
/*     */   public String host(Properties props)
/*     */   {
/* 572 */     return props.getProperty("HOST", "localhost");
/*     */   }
/*     */ 
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 588 */     return false;
/*     */   }
/*     */ 
/*     */   public Properties parseURL(String url, Properties defaults) throws SQLException
/*     */   {
/* 593 */     Properties urlProps = defaults != null ? new Properties(defaults) : new Properties();
/*     */ 
/* 596 */     if (url == null) {
/* 597 */       return null;
/*     */     }
/*     */ 
/* 600 */     if ((!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://")))
/*     */     {
/* 607 */       return null;
/*     */     }
/*     */ 
/* 610 */     int beginningOfSlashes = url.indexOf("//");
/*     */ 
/* 612 */     if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://"))
/*     */     {
/* 614 */       urlProps.setProperty("socketFactory", "com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
/*     */     }
/*     */ 
/* 623 */     int index = url.indexOf("?");
/*     */ 
/* 625 */     if (index != -1) {
/* 626 */       String paramString = url.substring(index + 1, url.length());
/* 627 */       url = url.substring(0, index);
/*     */ 
/* 629 */       StringTokenizer queryParams = new StringTokenizer(paramString, "&");
/*     */ 
/* 631 */       while (queryParams.hasMoreTokens()) {
/* 632 */         String parameterValuePair = queryParams.nextToken();
/*     */ 
/* 634 */         int indexOfEquals = StringUtils.indexOfIgnoreCase(0, parameterValuePair, "=");
/*     */ 
/* 637 */         String parameter = null;
/* 638 */         String value = null;
/*     */ 
/* 640 */         if (indexOfEquals != -1) {
/* 641 */           parameter = parameterValuePair.substring(0, indexOfEquals);
/*     */ 
/* 643 */           if (indexOfEquals + 1 < parameterValuePair.length()) {
/* 644 */             value = parameterValuePair.substring(indexOfEquals + 1);
/*     */           }
/*     */         }
/*     */ 
/* 648 */         if ((value != null) && (value.length() > 0) && (parameter != null) && (parameter.length() > 0)) {
/*     */           try
/*     */           {
/* 651 */             urlProps.put(parameter, URLDecoder.decode(value, "UTF-8"));
/*     */           }
/*     */           catch (UnsupportedEncodingException badEncoding)
/*     */           {
/* 655 */             urlProps.put(parameter, URLDecoder.decode(value));
/*     */           }
/*     */           catch (NoSuchMethodError nsme) {
/* 658 */             urlProps.put(parameter, URLDecoder.decode(value));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 664 */     url = url.substring(beginningOfSlashes + 2);
/*     */ 
/* 666 */     String hostStuff = null;
/*     */ 
/* 668 */     int slashIndex = StringUtils.indexOfIgnoreCaseRespectMarker(0, url, "/", "\"'", "\"'", true);
/*     */ 
/* 670 */     if (slashIndex != -1) {
/* 671 */       hostStuff = url.substring(0, slashIndex);
/*     */ 
/* 673 */       if (slashIndex + 1 < url.length())
/* 674 */         urlProps.put("DBNAME", url.substring(slashIndex + 1, url.length()));
/*     */     }
/*     */     else
/*     */     {
/* 678 */       hostStuff = url;
/*     */     }
/*     */ 
/* 681 */     int numHosts = 0;
/*     */ 
/* 683 */     if ((hostStuff != null) && (hostStuff.trim().length() > 0)) {
/* 684 */       List hosts = StringUtils.split(hostStuff, ",", "\"'", "\"'", false);
/*     */ 
/* 687 */       for (String hostAndPort : hosts) {
/* 688 */         numHosts++;
/*     */ 
/* 690 */         String[] hostPortPair = parseHostPortPair(hostAndPort);
/*     */ 
/* 692 */         if ((hostPortPair[0] != null) && (hostPortPair[0].trim().length() > 0))
/* 693 */           urlProps.setProperty("HOST." + numHosts, hostPortPair[0]);
/*     */         else {
/* 695 */           urlProps.setProperty("HOST." + numHosts, "localhost");
/*     */         }
/*     */ 
/* 698 */         if (hostPortPair[1] != null)
/* 699 */           urlProps.setProperty("PORT." + numHosts, hostPortPair[1]);
/*     */         else
/* 701 */           urlProps.setProperty("PORT." + numHosts, "3306");
/*     */       }
/*     */     }
/*     */     else {
/* 705 */       numHosts = 1;
/* 706 */       urlProps.setProperty("HOST.1", "localhost");
/* 707 */       urlProps.setProperty("PORT.1", "3306");
/*     */     }
/*     */ 
/* 710 */     urlProps.setProperty("NUM_HOSTS", String.valueOf(numHosts));
/* 711 */     urlProps.setProperty("HOST", urlProps.getProperty("HOST.1"));
/* 712 */     urlProps.setProperty("PORT", urlProps.getProperty("PORT.1"));
/*     */ 
/* 714 */     String propertiesTransformClassName = urlProps.getProperty("propertiesTransform");
/*     */ 
/* 717 */     if (propertiesTransformClassName != null) {
/*     */       try {
/* 719 */         ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
/*     */ 
/* 722 */         urlProps = propTransformer.transformProperties(urlProps);
/*     */       } catch (InstantiationException e) {
/* 724 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/* 731 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/* 738 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 747 */     if ((Util.isColdFusion()) && (urlProps.getProperty("autoConfigureForColdFusion", "true").equalsIgnoreCase("true")))
/*     */     {
/* 749 */       String configs = urlProps.getProperty("useConfigs");
/*     */ 
/* 751 */       StringBuffer newConfigs = new StringBuffer();
/*     */ 
/* 753 */       if (configs != null) {
/* 754 */         newConfigs.append(configs);
/* 755 */         newConfigs.append(",");
/*     */       }
/*     */ 
/* 758 */       newConfigs.append("coldFusion");
/*     */ 
/* 760 */       urlProps.setProperty("useConfigs", newConfigs.toString());
/*     */     }
/*     */ 
/* 766 */     String configNames = null;
/*     */ 
/* 768 */     if (defaults != null) {
/* 769 */       configNames = defaults.getProperty("useConfigs");
/*     */     }
/*     */ 
/* 772 */     if (configNames == null) {
/* 773 */       configNames = urlProps.getProperty("useConfigs");
/*     */     }
/*     */ 
/* 776 */     if (configNames != null) {
/* 777 */       List splitNames = StringUtils.split(configNames, ",", true);
/*     */ 
/* 779 */       Properties configProps = new Properties();
/*     */ 
/* 781 */       Iterator namesIter = splitNames.iterator();
/*     */ 
/* 783 */       while (namesIter.hasNext()) {
/* 784 */         String configName = (String)namesIter.next();
/*     */         try
/*     */         {
/* 787 */           InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
/*     */ 
/* 791 */           if (configAsStream == null) {
/* 792 */             throw SQLError.createSQLException("Can't find configuration template named '" + configName + "'", "01S00", null);
/*     */           }
/*     */ 
/* 798 */           configProps.load(configAsStream);
/*     */         } catch (IOException ioEx) {
/* 800 */           SQLException sqlEx = SQLError.createSQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, "01S00", null);
/*     */ 
/* 806 */           sqlEx.initCause(ioEx);
/*     */ 
/* 808 */           throw sqlEx;
/*     */         }
/*     */       }
/*     */ 
/* 812 */       Iterator propsIter = urlProps.keySet().iterator();
/*     */ 
/* 814 */       while (propsIter.hasNext()) {
/* 815 */         String key = propsIter.next().toString();
/* 816 */         String property = urlProps.getProperty(key);
/* 817 */         configProps.setProperty(key, property);
/*     */       }
/*     */ 
/* 820 */       urlProps = configProps;
/*     */     }
/*     */ 
/* 825 */     if (defaults != null) {
/* 826 */       Iterator propsIter = defaults.keySet().iterator();
/*     */ 
/* 828 */       while (propsIter.hasNext()) {
/* 829 */         String key = propsIter.next().toString();
/* 830 */         if (!key.equals("NUM_HOSTS")) {
/* 831 */           String property = defaults.getProperty(key);
/* 832 */           urlProps.setProperty(key, property);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 837 */     return urlProps;
/*     */   }
/*     */ 
/*     */   public int port(Properties props)
/*     */   {
/* 849 */     return Integer.parseInt(props.getProperty("PORT", "3306"));
/*     */   }
/*     */ 
/*     */   public String property(String name, Properties props)
/*     */   {
/* 863 */     return props.getProperty(name);
/*     */   }
/*     */ 
/*     */   public static Properties expandHostKeyValues(String host)
/*     */   {
/* 873 */     Properties hostProps = new Properties();
/*     */ 
/* 875 */     if (isHostPropertiesList(host)) {
/* 876 */       host = host.substring("address=".length() + 1);
/* 877 */       List hostPropsList = StringUtils.split(host, ")", "'\"", "'\"", true);
/*     */ 
/* 879 */       for (String propDef : hostPropsList) {
/* 880 */         if (propDef.startsWith("(")) {
/* 881 */           propDef = propDef.substring(1);
/*     */         }
/*     */ 
/* 884 */         List kvp = StringUtils.split(propDef, "=", "'\"", "'\"", true);
/*     */ 
/* 886 */         String key = (String)kvp.get(0);
/* 887 */         String value = kvp.size() > 1 ? (String)kvp.get(1) : null;
/*     */ 
/* 889 */         if ((value != null) && (((value.startsWith("\"")) && (value.endsWith("\""))) || ((value.startsWith("'")) && (value.endsWith("'"))))) {
/* 890 */           value = value.substring(1, value.length() - 1);
/*     */         }
/*     */ 
/* 893 */         if (value != null) {
/* 894 */           if (("HOST".equalsIgnoreCase(key)) || ("DBNAME".equalsIgnoreCase(key)) || ("PORT".equalsIgnoreCase(key)) || ("PROTOCOL".equalsIgnoreCase(key)) || ("PATH".equalsIgnoreCase(key)))
/*     */           {
/* 899 */             key = key.toUpperCase(Locale.ENGLISH);
/* 900 */           } else if (("user".equalsIgnoreCase(key)) || ("password".equalsIgnoreCase(key)))
/*     */           {
/* 902 */             key = key.toLowerCase(Locale.ENGLISH);
/*     */           }
/*     */ 
/* 905 */           hostProps.setProperty(key, value);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 910 */     return hostProps;
/*     */   }
/*     */ 
/*     */   public static boolean isHostPropertiesList(String host) {
/* 914 */     return (host != null) && (StringUtils.startsWithIgnoreCase(host, "address="));
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  88 */     AbandonedConnectionCleanupThread referenceThread = new AbandonedConnectionCleanupThread();
/*  89 */     referenceThread.setDaemon(true);
/*  90 */     referenceThread.start();
/*     */   }
/*     */ 
/*     */   static class ConnectionPhantomReference extends PhantomReference<ConnectionImpl>
/*     */   {
/*     */     private NetworkResources io;
/*     */ 
/*     */     ConnectionPhantomReference(ConnectionImpl connectionImpl, ReferenceQueue<ConnectionImpl> q)
/*     */     {
/* 921 */       super(q);
/*     */       try
/*     */       {
/* 924 */         this.io = connectionImpl.getIO().getNetworkResources();
/*     */       }
/*     */       catch (SQLException e) {
/*     */       }
/*     */     }
/*     */ 
/*     */     void cleanup() {
/* 931 */       if (this.io != null)
/*     */         try {
/* 933 */           this.io.forceClose();
/*     */         } finally {
/* 935 */           this.io = null;
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NonRegisteringDriver
 * JD-Core Version:    0.6.0
 */