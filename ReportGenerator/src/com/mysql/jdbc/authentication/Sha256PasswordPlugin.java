/*    */ package com.mysql.jdbc.authentication;
/*    */ 
/*    */ import com.mysql.jdbc.AuthenticationPlugin;
/*    */ import com.mysql.jdbc.Buffer;
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.StringUtils;
/*    */ import java.sql.SQLException;
/*    */ import java.util.List;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class Sha256PasswordPlugin
/*    */   implements AuthenticationPlugin
/*    */ {
/* 41 */   private String password = null;
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/*    */   }
/*    */ 
/*    */   public void destroy() {
/* 47 */     this.password = null;
/*    */   }
/*    */ 
/*    */   public String getProtocolPluginName() {
/* 51 */     return "sha256_password";
/*    */   }
/*    */ 
/*    */   public boolean requiresConfidentiality() {
/* 55 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isReusable() {
/* 59 */     return true;
/*    */   }
/*    */ 
/*    */   public void setAuthenticationParameters(String user, String password) {
/* 63 */     this.password = password;
/*    */   }
/*    */ 
/*    */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
/* 67 */     toServer.clear();
/*    */ 
/* 69 */     Buffer bresp = new Buffer(StringUtils.getBytes(this.password != null ? this.password : ""));
/*    */ 
/* 71 */     bresp.setPosition(bresp.getBufLength());
/* 72 */     int oldBufLength = bresp.getBufLength();
/*    */ 
/* 74 */     bresp.writeByte(0);
/*    */ 
/* 76 */     bresp.setBufLength(oldBufLength + 1);
/* 77 */     bresp.setPosition(0);
/*    */ 
/* 79 */     toServer.add(bresp);
/* 80 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.authentication.Sha256PasswordPlugin
 * JD-Core Version:    0.6.0
 */