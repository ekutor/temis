/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JDBC4ClientInfoProviderSP
/*     */   implements JDBC4ClientInfoProvider
/*     */ {
/*     */   PreparedStatement setClientInfoSp;
/*     */   PreparedStatement getClientInfoSp;
/*     */   PreparedStatement getClientInfoBulkSp;
/*     */ 
/*     */   public synchronized void initialize(java.sql.Connection conn, Properties configurationProps)
/*     */     throws SQLException
/*     */   {
/*  43 */     String identifierQuote = conn.getMetaData().getIdentifierQuoteString();
/*  44 */     String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
/*     */ 
/*  46 */     String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
/*     */ 
/*  48 */     String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
/*     */ 
/*  50 */     String clientInfoCatalog = configurationProps.getProperty("clientInfoCatalog", "");
/*     */ 
/*  54 */     String catalog = "".equals(clientInfoCatalog) ? conn.getCatalog() : clientInfoCatalog;
/*     */ 
/*  57 */     this.setClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
/*     */ 
/*  62 */     this.getClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL" + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
/*     */ 
/*  67 */     this.getClientInfoBulkSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
/*     */   }
/*     */ 
/*     */   public synchronized void destroy()
/*     */     throws SQLException
/*     */   {
/*  74 */     if (this.setClientInfoSp != null) {
/*  75 */       this.setClientInfoSp.close();
/*  76 */       this.setClientInfoSp = null;
/*     */     }
/*     */ 
/*  79 */     if (this.getClientInfoSp != null) {
/*  80 */       this.getClientInfoSp.close();
/*  81 */       this.getClientInfoSp = null;
/*     */     }
/*     */ 
/*  84 */     if (this.getClientInfoBulkSp != null) {
/*  85 */       this.getClientInfoBulkSp.close();
/*  86 */       this.getClientInfoBulkSp = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Properties getClientInfo(java.sql.Connection conn) throws SQLException
/*     */   {
/*  92 */     ResultSet rs = null;
/*     */ 
/*  94 */     Properties props = new Properties();
/*     */     try
/*     */     {
/*  97 */       this.getClientInfoBulkSp.execute();
/*     */ 
/*  99 */       rs = this.getClientInfoBulkSp.getResultSet();
/*     */ 
/* 101 */       while (rs.next())
/* 102 */         props.setProperty(rs.getString(1), rs.getString(2));
/*     */     }
/*     */     finally {
/* 105 */       if (rs != null) {
/* 106 */         rs.close();
/*     */       }
/*     */     }
/*     */ 
/* 110 */     return props;
/*     */   }
/*     */ 
/*     */   public synchronized String getClientInfo(java.sql.Connection conn, String name) throws SQLException
/*     */   {
/* 115 */     ResultSet rs = null;
/*     */ 
/* 117 */     String clientInfo = null;
/*     */     try
/*     */     {
/* 120 */       this.getClientInfoSp.setString(1, name);
/* 121 */       this.getClientInfoSp.execute();
/*     */ 
/* 123 */       rs = this.getClientInfoSp.getResultSet();
/*     */ 
/* 125 */       if (rs.next())
/* 126 */         clientInfo = rs.getString(1);
/*     */     }
/*     */     finally {
/* 129 */       if (rs != null) {
/* 130 */         rs.close();
/*     */       }
/*     */     }
/*     */ 
/* 134 */     return clientInfo;
/*     */   }
/*     */ 
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, Properties properties) throws SQLClientInfoException
/*     */   {
/*     */     try {
/* 140 */       Enumeration propNames = properties.propertyNames();
/*     */ 
/* 142 */       while (propNames.hasMoreElements()) {
/* 143 */         String name = (String)propNames.nextElement();
/* 144 */         String value = properties.getProperty(name);
/*     */ 
/* 146 */         setClientInfo(conn, name, value);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 149 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 150 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 152 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, String name, String value) throws SQLClientInfoException
/*     */   {
/*     */     try {
/* 159 */       this.setClientInfoSp.setString(1, name);
/* 160 */       this.setClientInfoSp.setString(2, value);
/* 161 */       this.setClientInfoSp.execute();
/*     */     } catch (SQLException sqlEx) {
/* 163 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 164 */       clientInfoEx.initCause(sqlEx);
/*     */ 
/* 166 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4ClientInfoProviderSP
 * JD-Core Version:    0.6.0
 */