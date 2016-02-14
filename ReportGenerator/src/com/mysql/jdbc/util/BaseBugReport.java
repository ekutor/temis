/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.Driver;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public abstract class BaseBugReport
/*     */ {
/*     */   private Connection conn;
/*     */   private Driver driver;
/*     */ 
/*     */   public BaseBugReport()
/*     */   {
/*     */     try
/*     */     {
/* 108 */       this.driver = new Driver();
/*     */     } catch (SQLException ex) {
/* 110 */       throw new RuntimeException(ex.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void setUp()
/*     */     throws Exception;
/*     */ 
/*     */   public abstract void tearDown()
/*     */     throws Exception;
/*     */ 
/*     */   public abstract void runTest()
/*     */     throws Exception;
/*     */ 
/*     */   public final void run()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 151 */       setUp();
/* 152 */       runTest();
/*     */     }
/*     */     finally {
/* 155 */       tearDown();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void assertTrue(String message, boolean condition)
/*     */     throws Exception
/*     */   {
/* 172 */     if (!condition)
/* 173 */       throw new Exception("Assertion failed: " + message);
/*     */   }
/*     */ 
/*     */   protected final void assertTrue(boolean condition)
/*     */     throws Exception
/*     */   {
/* 186 */     assertTrue("(no message given)", condition);
/*     */   }
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 197 */     return "jdbc:mysql:///test";
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 212 */     if ((this.conn == null) || (this.conn.isClosed())) {
/* 213 */       this.conn = getNewConnection();
/*     */     }
/*     */ 
/* 216 */     return this.conn;
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getNewConnection()
/*     */     throws SQLException
/*     */   {
/* 229 */     return getConnection(getUrl());
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getConnection(String url)
/*     */     throws SQLException
/*     */   {
/* 243 */     return getConnection(url, null);
/*     */   }
/*     */ 
/*     */   public final synchronized Connection getConnection(String url, Properties props)
/*     */     throws SQLException
/*     */   {
/* 263 */     return this.driver.connect(url, props);
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.util.BaseBugReport
 * JD-Core Version:    0.6.0
 */