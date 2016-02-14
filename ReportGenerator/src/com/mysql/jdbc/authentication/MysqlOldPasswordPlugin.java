/*    */ package com.mysql.jdbc.authentication;
/*    */ 
/*    */ import com.mysql.jdbc.AuthenticationPlugin;
/*    */ import com.mysql.jdbc.Buffer;
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.StringUtils;
/*    */ import com.mysql.jdbc.Util;
/*    */ import java.sql.SQLException;
/*    */ import java.util.List;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class MysqlOldPasswordPlugin
/*    */   implements AuthenticationPlugin
/*    */ {
/*    */   private Properties properties;
/* 43 */   private String password = null;
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 46 */     this.properties = props;
/*    */   }
/*    */ 
/*    */   public void destroy() {
/* 50 */     this.password = null;
/*    */   }
/*    */ 
/*    */   public String getProtocolPluginName() {
/* 54 */     return "mysql_old_password";
/*    */   }
/*    */ 
/*    */   public boolean requiresConfidentiality() {
/* 58 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isReusable() {
/* 62 */     return true;
/*    */   }
/*    */ 
/*    */   public void setAuthenticationParameters(String user, String password) {
/* 66 */     this.password = password;
/*    */   }
/*    */ 
/*    */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
/* 70 */     toServer.clear();
/*    */ 
/* 72 */     Buffer bresp = null;
/*    */ 
/* 74 */     String pwd = this.password;
/* 75 */     if (pwd == null) {
/* 76 */       pwd = this.properties.getProperty("password");
/*    */     }
/*    */ 
/* 79 */     bresp = new Buffer(StringUtils.getBytes((fromServer == null) || (pwd == null) || (pwd.length() == 0) ? "" : Util.newCrypt(pwd, fromServer.readString().substring(0, 8))));
/*    */ 
/* 81 */     bresp.setPosition(bresp.getBufLength());
/* 82 */     int oldBufLength = bresp.getBufLength();
/*    */ 
/* 84 */     bresp.writeByte(0);
/*    */ 
/* 86 */     bresp.setBufLength(oldBufLength + 1);
/* 87 */     bresp.setPosition(0);
/*    */ 
/* 89 */     toServer.add(bresp);
/*    */ 
/* 91 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.authentication.MysqlOldPasswordPlugin
 * JD-Core Version:    0.6.0
 */