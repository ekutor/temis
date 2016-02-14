/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ConnectionPropertiesImpl;
/*     */ import com.mysql.jdbc.NonRegisteringDriver;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ public class MysqlDataSource extends ConnectionPropertiesImpl
/*     */   implements DataSource, Referenceable, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -5515846944416881264L;
/*     */   protected static final NonRegisteringDriver mysqlDriver;
/*  66 */   protected transient PrintWriter logWriter = null;
/*     */ 
/*  69 */   protected String databaseName = null;
/*     */ 
/*  72 */   protected String encoding = null;
/*     */ 
/*  75 */   protected String hostName = null;
/*     */ 
/*  78 */   protected String password = null;
/*     */ 
/*  81 */   protected String profileSql = "false";
/*     */ 
/*  84 */   protected String url = null;
/*     */ 
/*  87 */   protected String user = null;
/*     */ 
/*  90 */   protected boolean explicitUrl = false;
/*     */ 
/*  93 */   protected int port = 3306;
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 111 */     return getConnection(this.user, this.password);
/*     */   }
/*     */ 
/*     */   public Connection getConnection(String userID, String pass)
/*     */     throws SQLException
/*     */   {
/* 129 */     Properties props = new Properties();
/*     */ 
/* 131 */     if (userID != null) {
/* 132 */       props.setProperty("user", userID);
/*     */     }
/*     */ 
/* 135 */     if (pass != null) {
/* 136 */       props.setProperty("password", pass);
/*     */     }
/*     */ 
/* 139 */     exposeAsProperties(props);
/*     */ 
/* 141 */     return getConnection(props);
/*     */   }
/*     */ 
/*     */   public void setDatabaseName(String dbName)
/*     */   {
/* 151 */     this.databaseName = dbName;
/*     */   }
/*     */ 
/*     */   public String getDatabaseName()
/*     */   {
/* 160 */     return this.databaseName != null ? this.databaseName : "";
/*     */   }
/*     */ 
/*     */   public void setLogWriter(PrintWriter output)
/*     */     throws SQLException
/*     */   {
/* 169 */     this.logWriter = output;
/*     */   }
/*     */ 
/*     */   public PrintWriter getLogWriter()
/*     */   {
/* 178 */     return this.logWriter;
/*     */   }
/*     */ 
/*     */   public void setLoginTimeout(int seconds)
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getLoginTimeout()
/*     */   {
/* 199 */     return 0;
/*     */   }
/*     */ 
/*     */   public void setPassword(String pass)
/*     */   {
/* 209 */     this.password = pass;
/*     */   }
/*     */ 
/*     */   public void setPort(int p)
/*     */   {
/* 219 */     this.port = p;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 228 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setPortNumber(int p)
/*     */   {
/* 240 */     setPort(p);
/*     */   }
/*     */ 
/*     */   public int getPortNumber()
/*     */   {
/* 249 */     return getPort();
/*     */   }
/*     */ 
/*     */   public void setPropertiesViaRef(Reference ref)
/*     */     throws SQLException
/*     */   {
/* 262 */     super.initializeFromRef(ref);
/*     */   }
/*     */ 
/*     */   public Reference getReference()
/*     */     throws NamingException
/*     */   {
/* 274 */     String factoryName = "com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory";
/* 275 */     Reference ref = new Reference(getClass().getName(), factoryName, null);
/* 276 */     ref.add(new StringRefAddr("user", getUser()));
/*     */ 
/* 278 */     ref.add(new StringRefAddr("password", this.password));
/*     */ 
/* 280 */     ref.add(new StringRefAddr("serverName", getServerName()));
/* 281 */     ref.add(new StringRefAddr("port", "" + getPort()));
/* 282 */     ref.add(new StringRefAddr("databaseName", getDatabaseName()));
/* 283 */     ref.add(new StringRefAddr("url", getUrl()));
/* 284 */     ref.add(new StringRefAddr("explicitUrl", String.valueOf(this.explicitUrl)));
/*     */     try
/*     */     {
/* 291 */       storeToRef(ref);
/*     */     } catch (SQLException sqlEx) {
/* 293 */       throw new NamingException(sqlEx.getMessage());
/*     */     }
/*     */ 
/* 296 */     return ref;
/*     */   }
/*     */ 
/*     */   public void setServerName(String serverName)
/*     */   {
/* 306 */     this.hostName = serverName;
/*     */   }
/*     */ 
/*     */   public String getServerName()
/*     */   {
/* 315 */     return this.hostName != null ? this.hostName : "";
/*     */   }
/*     */ 
/*     */   public void setURL(String url)
/*     */   {
/* 330 */     setUrl(url);
/*     */   }
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 339 */     return getUrl();
/*     */   }
/*     */ 
/*     */   public void setUrl(String url)
/*     */   {
/* 351 */     this.url = url;
/* 352 */     this.explicitUrl = true;
/*     */   }
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 361 */     if (!this.explicitUrl) {
/* 362 */       String builtUrl = "jdbc:mysql://";
/* 363 */       builtUrl = builtUrl + getServerName() + ":" + getPort() + "/" + getDatabaseName();
/*     */ 
/* 366 */       return builtUrl;
/*     */     }
/*     */ 
/* 369 */     return this.url;
/*     */   }
/*     */ 
/*     */   public void setUser(String userID)
/*     */   {
/* 379 */     this.user = userID;
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 388 */     return this.user;
/*     */   }
/*     */ 
/*     */   protected Connection getConnection(Properties props)
/*     */     throws SQLException
/*     */   {
/* 404 */     String jdbcUrlToUse = null;
/*     */ 
/* 406 */     if (!this.explicitUrl) {
/* 407 */       StringBuffer jdbcUrl = new StringBuffer("jdbc:mysql://");
/*     */ 
/* 409 */       if (this.hostName != null) {
/* 410 */         jdbcUrl.append(this.hostName);
/*     */       }
/*     */ 
/* 413 */       jdbcUrl.append(":");
/* 414 */       jdbcUrl.append(this.port);
/* 415 */       jdbcUrl.append("/");
/*     */ 
/* 417 */       if (this.databaseName != null) {
/* 418 */         jdbcUrl.append(this.databaseName);
/*     */       }
/*     */ 
/* 421 */       jdbcUrlToUse = jdbcUrl.toString();
/*     */     } else {
/* 423 */       jdbcUrlToUse = this.url;
/*     */     }
/*     */ 
/* 430 */     Properties urlProps = mysqlDriver.parseURL(jdbcUrlToUse, null);
/* 431 */     urlProps.remove("DBNAME");
/* 432 */     urlProps.remove("HOST");
/* 433 */     urlProps.remove("PORT");
/*     */ 
/* 435 */     Iterator keys = urlProps.keySet().iterator();
/*     */ 
/* 437 */     while (keys.hasNext()) {
/* 438 */       String key = (String)keys.next();
/*     */ 
/* 440 */       props.setProperty(key, urlProps.getProperty(key));
/*     */     }
/*     */ 
/* 443 */     return mysqlDriver.connect(jdbcUrlToUse, props);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  58 */       mysqlDriver = new NonRegisteringDriver();
/*     */     } catch (Exception E) {
/*  60 */       throw new RuntimeException("Can not load Driver class com.mysql.jdbc.Driver");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlDataSource
 * JD-Core Version:    0.6.0
 */