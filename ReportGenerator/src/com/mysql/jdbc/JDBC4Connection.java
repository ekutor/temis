/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JDBC4Connection extends ConnectionImpl
/*     */ {
/*     */   private JDBC4ClientInfoProvider infoProvider;
/*     */ 
/*     */   public JDBC4Connection(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*     */     throws SQLException
/*     */   {
/*  47 */     super(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*     */   }
/*     */ 
/*     */   public SQLXML createSQLXML() throws SQLException
/*     */   {
/*  52 */     return new JDBC4MysqlSQLXML(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*  56 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*  60 */     throw SQLError.notImplemented();
/*     */   }
/*     */ 
/*     */   public Properties getClientInfo() throws SQLException {
/*  64 */     return getClientInfoProviderImpl().getClientInfo(this);
/*     */   }
/*     */ 
/*     */   public String getClientInfo(String name) throws SQLException {
/*  68 */     return getClientInfoProviderImpl().getClientInfo(this, name);
/*     */   }
/*     */ 
/*     */   public synchronized boolean isValid(int timeout)
/*     */     throws SQLException
/*     */   {
/*  93 */     if (isClosed())
/*  94 */       return false;
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/*  99 */         pingInternal(false, timeout * 1000);
/*     */       } catch (Throwable t) {
/*     */         try {
/* 102 */           abortInternal();
/*     */         }
/*     */         catch (Throwable ignoreThrown)
/*     */         {
/*     */         }
/* 107 */         return false;
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/* 111 */       return false;
/*     */     }
/*     */ 
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 119 */       getClientInfoProviderImpl().setClientInfo(this, properties);
/*     */     } catch (SQLClientInfoException ciEx) {
/* 121 */       throw ciEx;
/*     */     } catch (SQLException sqlEx) {
/* 123 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 124 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 126 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 132 */       getClientInfoProviderImpl().setClientInfo(this, name, value);
/*     */     } catch (SQLClientInfoException ciEx) {
/* 134 */       throw ciEx;
/*     */     } catch (SQLException sqlEx) {
/* 136 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 137 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 139 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 159 */     checkClosed();
/*     */ 
/* 163 */     return iface.isInstance(this);
/*     */   }
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 184 */       return iface.cast(this); } catch (ClassCastException cce) {
/*     */     }
/* 186 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public java.sql.Blob createBlob()
/*     */   {
/* 195 */     return new Blob(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public java.sql.Clob createClob()
/*     */   {
/* 202 */     return new Clob(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public NClob createNClob()
/*     */   {
/* 209 */     return new JDBC4NClob(getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   protected synchronized JDBC4ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/* 213 */     if (this.infoProvider == null) {
/*     */       try {
/*     */         try {
/* 216 */           this.infoProvider = ((JDBC4ClientInfoProvider)Util.getInstance(getClientInfoProvider(), new Class[0], new Object[0], getExceptionInterceptor()));
/*     */         }
/*     */         catch (SQLException sqlEx) {
/* 219 */           if ((sqlEx.getCause() instanceof ClassCastException))
/*     */           {
/* 221 */             this.infoProvider = ((JDBC4ClientInfoProvider)Util.getInstance("com.mysql.jdbc." + getClientInfoProvider(), new Class[0], new Object[0], getExceptionInterceptor()));
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (ClassCastException cce)
/*     */       {
/* 227 */         throw SQLError.createSQLException(Messages.getString("JDBC4Connection.ClientInfoNotImplemented", new Object[] { getClientInfoProvider() }), "S1009", getExceptionInterceptor());
/*     */       }
/*     */ 
/* 232 */       this.infoProvider.initialize(this, this.props);
/*     */     }
/*     */ 
/* 235 */     return this.infoProvider;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4Connection
 * JD-Core Version:    0.6.0
 */