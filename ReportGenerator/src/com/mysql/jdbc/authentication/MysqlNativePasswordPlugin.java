/*     */ package com.mysql.jdbc.authentication;
/*     */ 
/*     */ import com.mysql.jdbc.AuthenticationPlugin;
/*     */ import com.mysql.jdbc.Buffer;
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.Messages;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Security;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class MysqlNativePasswordPlugin
/*     */   implements AuthenticationPlugin
/*     */ {
/*     */   private Connection connection;
/*     */   private Properties properties;
/*  47 */   private String password = null;
/*     */ 
/*     */   public void init(Connection conn, Properties props) throws SQLException {
/*  50 */     this.connection = conn;
/*  51 */     this.properties = props;
/*     */   }
/*     */ 
/*     */   public void destroy() {
/*  55 */     this.password = null;
/*     */   }
/*     */ 
/*     */   public String getProtocolPluginName() {
/*  59 */     return "mysql_native_password";
/*     */   }
/*     */ 
/*     */   public boolean requiresConfidentiality() {
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isReusable() {
/*  67 */     return true;
/*     */   }
/*     */ 
/*     */   public void setAuthenticationParameters(String user, String password) {
/*  71 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException
/*     */   {
/*     */     try {
/*  77 */       toServer.clear();
/*     */ 
/*  79 */       Buffer bresp = null;
/*     */ 
/*  81 */       String pwd = this.password;
/*  82 */       if (pwd == null) {
/*  83 */         pwd = this.properties.getProperty("password");
/*     */       }
/*     */ 
/*  86 */       if ((fromServer == null) || (pwd == null) || (pwd.length() == 0))
/*  87 */         bresp = new Buffer(new byte[0]);
/*     */       else {
/*  89 */         bresp = new Buffer(Security.scramble411(pwd, fromServer.readString(), this.connection));
/*     */       }
/*  91 */       toServer.add(bresp);
/*     */     }
/*     */     catch (NoSuchAlgorithmException nse) {
/*  94 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000", null);
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*  98 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000", null);
/*     */     }
/*     */ 
/* 103 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.authentication.MysqlNativePasswordPlugin
 * JD-Core Version:    0.6.0
 */