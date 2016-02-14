/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ public class MysqlPooledConnection
/*     */   implements PooledConnection
/*     */ {
/*     */   private static final Constructor<?> JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR;
/*     */   public static final int CONNECTION_ERROR_EVENT = 1;
/*     */   public static final int CONNECTION_CLOSED_EVENT = 2;
/*     */   private Map<ConnectionEventListener, ConnectionEventListener> connectionEventListeners;
/*     */   private java.sql.Connection logicalHandle;
/*     */   private com.mysql.jdbc.Connection physicalConn;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   protected static MysqlPooledConnection getInstance(com.mysql.jdbc.Connection connection)
/*     */     throws SQLException
/*     */   {
/*  75 */     if (!Util.isJdbc4()) {
/*  76 */       return new MysqlPooledConnection(connection);
/*     */     }
/*     */ 
/*  79 */     return (MysqlPooledConnection)Util.handleNewInstance(JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR, new Object[] { connection }, connection.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public MysqlPooledConnection(com.mysql.jdbc.Connection connection)
/*     */   {
/* 113 */     this.logicalHandle = null;
/* 114 */     this.physicalConn = connection;
/* 115 */     this.connectionEventListeners = new HashMap();
/* 116 */     this.exceptionInterceptor = this.physicalConn.getExceptionInterceptor();
/*     */   }
/*     */ 
/*     */   public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener)
/*     */   {
/* 129 */     if (this.connectionEventListeners != null)
/* 130 */       this.connectionEventListeners.put(connectioneventlistener, connectioneventlistener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener)
/*     */   {
/* 145 */     if (this.connectionEventListeners != null)
/* 146 */       this.connectionEventListeners.remove(connectioneventlistener);
/*     */   }
/*     */ 
/*     */   public synchronized java.sql.Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 157 */     return getConnection(true, false);
/*     */   }
/*     */ 
/*     */   protected synchronized java.sql.Connection getConnection(boolean resetServerState, boolean forXa)
/*     */     throws SQLException
/*     */   {
/* 164 */     if (this.physicalConn == null)
/*     */     {
/* 166 */       SQLException sqlException = SQLError.createSQLException("Physical Connection doesn't exist", this.exceptionInterceptor);
/*     */ 
/* 168 */       callConnectionEventListeners(1, sqlException);
/*     */ 
/* 170 */       throw sqlException;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 175 */       if (this.logicalHandle != null) {
/* 176 */         ((ConnectionWrapper)this.logicalHandle).close(false);
/*     */       }
/*     */ 
/* 179 */       if (resetServerState) {
/* 180 */         this.physicalConn.resetServerState();
/*     */       }
/*     */ 
/* 183 */       this.logicalHandle = ConnectionWrapper.getInstance(this, this.physicalConn, forXa);
/*     */     }
/*     */     catch (SQLException sqlException)
/*     */     {
/* 187 */       callConnectionEventListeners(1, sqlException);
/*     */ 
/* 189 */       throw sqlException;
/*     */     }
/*     */ 
/* 192 */     return this.logicalHandle;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws SQLException
/*     */   {
/* 203 */     if (this.physicalConn != null) {
/* 204 */       this.physicalConn.close();
/*     */ 
/* 206 */       this.physicalConn = null;
/*     */     }
/*     */ 
/* 209 */     if (this.connectionEventListeners != null) {
/* 210 */       this.connectionEventListeners.clear();
/*     */ 
/* 212 */       this.connectionEventListeners = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void callConnectionEventListeners(int eventType, SQLException sqlException)
/*     */   {
/* 231 */     if (this.connectionEventListeners == null)
/*     */     {
/* 233 */       return;
/*     */     }
/*     */ 
/* 236 */     Iterator iterator = this.connectionEventListeners.entrySet().iterator();
/*     */ 
/* 238 */     ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);
/*     */ 
/* 241 */     while (iterator.hasNext())
/*     */     {
/* 243 */       ConnectionEventListener connectioneventlistener = (ConnectionEventListener)((Map.Entry)iterator.next()).getValue();
/*     */ 
/* 245 */       if (eventType == 2)
/* 246 */         connectioneventlistener.connectionClosed(connectionevent);
/* 247 */       else if (eventType == 1)
/* 248 */         connectioneventlistener.connectionErrorOccurred(connectionevent);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected ExceptionInterceptor getExceptionInterceptor()
/*     */   {
/* 255 */     return this.exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  56 */     if (Util.isJdbc4())
/*     */       try {
/*  58 */         JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection").getConstructor(new Class[] { com.mysql.jdbc.Connection.class });
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*  63 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  65 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  67 */         throw new RuntimeException(e);
/*     */       }
/*     */     else
/*  70 */       JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = null;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection
 * JD-Core Version:    0.6.0
 */