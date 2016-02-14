/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class StandardSocketFactory
/*     */   implements SocketFactory, SocketMetadata
/*     */ {
/*     */   public static final String TCP_NO_DELAY_PROPERTY_NAME = "tcpNoDelay";
/*     */   public static final String TCP_KEEP_ALIVE_DEFAULT_VALUE = "true";
/*     */   public static final String TCP_KEEP_ALIVE_PROPERTY_NAME = "tcpKeepAlive";
/*     */   public static final String TCP_RCV_BUF_PROPERTY_NAME = "tcpRcvBuf";
/*     */   public static final String TCP_SND_BUF_PROPERTY_NAME = "tcpSndBuf";
/*     */   public static final String TCP_TRAFFIC_CLASS_PROPERTY_NAME = "tcpTrafficClass";
/*     */   public static final String TCP_RCV_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_SND_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_TRAFFIC_CLASS_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_NO_DELAY_DEFAULT_VALUE = "true";
/*     */   private static Method setTraficClassMethod;
/*  85 */   protected String host = null;
/*     */ 
/*  88 */   protected int port = 3306;
/*     */ 
/*  91 */   protected Socket rawSocket = null;
/*     */   public static final String IS_LOCAL_HOSTNAME_REPLACEMENT_PROPERTY_NAME = "com.mysql.jdbc.test.isLocalHostnameReplacement";
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 105 */     return this.rawSocket;
/*     */   }
/*     */ 
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 120 */     return this.rawSocket;
/*     */   }
/*     */ 
/*     */   private void configureSocket(Socket sock, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/*     */     try
/*     */     {
/* 134 */       sock.setTcpNoDelay(Boolean.valueOf(props.getProperty("tcpNoDelay", "true")).booleanValue());
/*     */ 
/* 138 */       String keepAlive = props.getProperty("tcpKeepAlive", "true");
/*     */ 
/* 141 */       if ((keepAlive != null) && (keepAlive.length() > 0)) {
/* 142 */         sock.setKeepAlive(Boolean.valueOf(keepAlive).booleanValue());
/*     */       }
/*     */ 
/* 146 */       int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */ 
/* 149 */       if (receiveBufferSize > 0) {
/* 150 */         sock.setReceiveBufferSize(receiveBufferSize);
/*     */       }
/*     */ 
/* 153 */       int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */ 
/* 156 */       if (sendBufferSize > 0) {
/* 157 */         sock.setSendBufferSize(sendBufferSize);
/*     */       }
/*     */ 
/* 160 */       int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */ 
/* 164 */       if ((trafficClass > 0) && (setTraficClassMethod != null))
/* 165 */         setTraficClassMethod.invoke(sock, new Object[] { Integer.valueOf(trafficClass) });
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 169 */       unwrapExceptionToProperClassAndThrowIt(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Socket connect(String hostname, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 179 */     if (props != null) {
/* 180 */       this.host = hostname;
/*     */ 
/* 182 */       this.port = portNumber;
/*     */ 
/* 184 */       Method connectWithTimeoutMethod = null;
/* 185 */       Method socketBindMethod = null;
/* 186 */       Class socketAddressClass = null;
/*     */ 
/* 188 */       String localSocketHostname = props.getProperty("localSocketAddress");
/*     */ 
/* 191 */       String connectTimeoutStr = props.getProperty("connectTimeout");
/*     */ 
/* 193 */       int connectTimeout = 0;
/*     */ 
/* 195 */       boolean wantsTimeout = (connectTimeoutStr != null) && (connectTimeoutStr.length() > 0) && (!connectTimeoutStr.equals("0"));
/*     */ 
/* 199 */       boolean wantsLocalBind = (localSocketHostname != null) && (localSocketHostname.length() > 0);
/*     */ 
/* 202 */       boolean needsConfigurationBeforeConnect = socketNeedsConfigurationBeforeConnect(props);
/*     */ 
/* 204 */       if ((wantsTimeout) || (wantsLocalBind) || (needsConfigurationBeforeConnect))
/*     */       {
/* 206 */         if (connectTimeoutStr != null) {
/*     */           try {
/* 208 */             connectTimeout = Integer.parseInt(connectTimeoutStr);
/*     */           } catch (NumberFormatException nfe) {
/* 210 */             throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 218 */           socketAddressClass = Class.forName("java.net.SocketAddress");
/*     */ 
/* 221 */           connectWithTimeoutMethod = Socket.class.getMethod("connect", new Class[] { socketAddressClass, Integer.TYPE });
/*     */ 
/* 225 */           socketBindMethod = Socket.class.getMethod("bind", new Class[] { socketAddressClass });
/*     */         }
/*     */         catch (NoClassDefFoundError noClassDefFound)
/*     */         {
/*     */         }
/*     */         catch (NoSuchMethodException noSuchMethodEx)
/*     */         {
/*     */         }
/*     */         catch (Throwable catchAll)
/*     */         {
/*     */         }
/* 236 */         if ((wantsLocalBind) && (socketBindMethod == null)) {
/* 237 */           throw new SocketException("Can't specify \"localSocketAddress\" on JVMs older than 1.4");
/*     */         }
/*     */ 
/* 241 */         if ((wantsTimeout) && (connectWithTimeoutMethod == null)) {
/* 242 */           throw new SocketException("Can't specify \"connectTimeout\" on JVMs older than 1.4");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 247 */       if (this.host != null) {
/* 248 */         if ((!wantsLocalBind) && (!wantsTimeout) && (!needsConfigurationBeforeConnect)) {
/* 249 */           InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/* 252 */           Throwable caughtWhileConnecting = null;
/*     */ 
/* 257 */           for (int i = 0; i < possibleAddresses.length; i++) {
/*     */             try {
/* 259 */               this.rawSocket = new Socket(possibleAddresses[i], this.port);
/*     */ 
/* 262 */               configureSocket(this.rawSocket, props);
/*     */             }
/*     */             catch (Exception ex)
/*     */             {
/* 266 */               caughtWhileConnecting = ex;
/*     */             }
/*     */           }
/*     */ 
/* 270 */           if (this.rawSocket == null) {
/* 271 */             unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 278 */             InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/* 281 */             Throwable caughtWhileConnecting = null;
/*     */ 
/* 283 */             Object localSockAddr = null;
/*     */ 
/* 285 */             Class inetSocketAddressClass = null;
/*     */ 
/* 287 */             Constructor addrConstructor = null;
/*     */             try
/*     */             {
/* 290 */               inetSocketAddressClass = Class.forName("java.net.InetSocketAddress");
/*     */ 
/* 293 */               addrConstructor = inetSocketAddressClass.getConstructor(new Class[] { InetAddress.class, Integer.TYPE });
/*     */ 
/* 297 */               if (wantsLocalBind) {
/* 298 */                 localSockAddr = addrConstructor.newInstance(new Object[] { InetAddress.getByName(localSocketHostname), new Integer(0) });
/*     */               }
/*     */ 
/*     */             }
/*     */             catch (Throwable ex)
/*     */             {
/* 309 */               unwrapExceptionToProperClassAndThrowIt(ex);
/*     */             }
/*     */ 
/* 315 */             for (int i = 0; i < possibleAddresses.length; i++) {
/*     */               try
/*     */               {
/* 318 */                 this.rawSocket = new Socket();
/*     */ 
/* 320 */                 configureSocket(this.rawSocket, props);
/*     */ 
/* 322 */                 Object sockAddr = addrConstructor.newInstance(new Object[] { possibleAddresses[i], Integer.valueOf(this.port) });
/*     */ 
/* 327 */                 if (localSockAddr != null) {
/* 328 */                   socketBindMethod.invoke(this.rawSocket, new Object[] { localSockAddr });
/*     */                 }
/*     */ 
/* 332 */                 connectWithTimeoutMethod.invoke(this.rawSocket, new Object[] { sockAddr, Integer.valueOf(connectTimeout) });
/*     */               }
/*     */               catch (Exception ex)
/*     */               {
/* 338 */                 this.rawSocket = null;
/*     */ 
/* 340 */                 caughtWhileConnecting = ex;
/*     */               }
/*     */             }
/*     */ 
/* 344 */             if (this.rawSocket == null)
/* 345 */               unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 349 */             unwrapExceptionToProperClassAndThrowIt(t);
/*     */           }
/*     */         }
/*     */ 
/* 353 */         return this.rawSocket;
/*     */       }
/*     */     }
/*     */ 
/* 357 */     throw new SocketException("Unable to create socket");
/*     */   }
/*     */ 
/*     */   private boolean socketNeedsConfigurationBeforeConnect(Properties props)
/*     */   {
/* 366 */     int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */ 
/* 369 */     if (receiveBufferSize > 0) {
/* 370 */       return true;
/*     */     }
/*     */ 
/* 373 */     int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */ 
/* 376 */     if (sendBufferSize > 0) {
/* 377 */       return true;
/*     */     }
/*     */ 
/* 380 */     int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */ 
/* 385 */     return (trafficClass > 0) && (setTraficClassMethod != null);
/*     */   }
/*     */ 
/*     */   private void unwrapExceptionToProperClassAndThrowIt(Throwable caughtWhileConnecting)
/*     */     throws SocketException, IOException
/*     */   {
/* 394 */     if ((caughtWhileConnecting instanceof InvocationTargetException))
/*     */     {
/* 398 */       caughtWhileConnecting = ((InvocationTargetException)caughtWhileConnecting).getTargetException();
/*     */     }
/*     */ 
/* 402 */     if ((caughtWhileConnecting instanceof SocketException)) {
/* 403 */       throw ((SocketException)caughtWhileConnecting);
/*     */     }
/*     */ 
/* 406 */     if ((caughtWhileConnecting instanceof IOException)) {
/* 407 */       throw ((IOException)caughtWhileConnecting);
/*     */     }
/*     */ 
/* 410 */     throw new SocketException(caughtWhileConnecting.toString());
/*     */   }
/*     */ 
/*     */   public boolean isLocallyConnected(ConnectionImpl conn)
/*     */     throws SQLException
/*     */   {
/* 417 */     long threadId = conn.getId();
/* 418 */     Statement processListStmt = conn.getMetadataSafeStatement();
/* 419 */     ResultSet rs = null;
/*     */     try
/*     */     {
/* 422 */       String processHost = null;
/*     */ 
/* 424 */       rs = processListStmt.executeQuery("SHOW PROCESSLIST");
/*     */ 
/* 426 */       while (rs.next()) {
/* 427 */         long id = rs.getLong(1);
/*     */ 
/* 429 */         if (threadId == id)
/*     */         {
/* 431 */           processHost = rs.getString(3);
/*     */ 
/* 433 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 438 */       if (System.getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement") != null) {
/* 439 */         processHost = System.getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement");
/*     */       }
/* 441 */       else if (conn.getProperties().getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement") != null) {
/* 442 */         processHost = conn.getProperties().getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement");
/*     */       }
/*     */ 
/* 445 */       if ((processHost != null) && 
/* 446 */         (processHost.indexOf(":") != -1)) { processHost = processHost.split(":")[0];
/*     */         InetAddress whereMysqlThinksIConnectedFrom;
/*     */         try { boolean isLocal = false;
/*     */ 
/* 452 */           whereMysqlThinksIConnectedFrom = InetAddress.getByName(processHost);
/* 453 */           SocketAddress remoteSocketAddr = this.rawSocket.getRemoteSocketAddress();
/*     */ 
/* 455 */           if ((remoteSocketAddr instanceof InetSocketAddress)) {
/* 456 */             whereIConnectedTo = ((InetSocketAddress)remoteSocketAddr).getAddress();
/*     */ 
/* 458 */             isLocal = whereMysqlThinksIConnectedFrom.equals(whereIConnectedTo);
/*     */           }
/*     */ 
/* 461 */           whereIConnectedTo = isLocal; jsr 61;
/*     */         }
/*     */         catch (UnknownHostException e)
/*     */         {
/*     */           InetAddress whereIConnectedTo;
/* 463 */           conn.getLog().logWarn(Messages.getString("Connection.CantDetectLocalConnect", new Object[] { this.host }), e);
/*     */ 
/* 468 */           whereMysqlThinksIConnectedFrom = 0; jsr 23;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 473 */       e = 0;
/*     */     } finally {
/* 475 */       processListStmt.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  75 */       setTraficClassMethod = Socket.class.getMethod("setTrafficClass", new Class[] { Integer.TYPE });
/*     */     }
/*     */     catch (SecurityException e) {
/*  78 */       setTraficClassMethod = null;
/*     */     } catch (NoSuchMethodException e) {
/*  80 */       setTraficClassMethod = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.StandardSocketFactory
 * JD-Core Version:    0.6.0
 */