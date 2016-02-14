/*    */ package com.co.hsg.generator.db;
/*    */ 
/*    */ import com.co.hsg.generator.io.FileManager;
/*    */ import com.co.hsg.generator.io.InfoDB;
/*    */ import com.co.hsg.generator.log.LogInfo;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class JDBCResourceManager
/*    */ {
/*    */   private Connection connection;
/*    */ 
/*    */   public Connection getConnection()
/*    */     throws SQLException
/*    */   {
/* 36 */     if ((this.connection == null) || (this.connection.isClosed())) {
/*    */       try {
/* 38 */         Class.forName("com.mysql.jdbc.Driver").newInstance();
/*    */ 
/* 40 */         InfoDB info = FileManager.getInst().chargeServerProps();
/* 41 */         String host = info.getHost();
/* 42 */         if ((info.getPort() != null) && (info.getPort().length() > 1)) {
/* 43 */           host = host + ":" + info.getPort();
/*    */         }
/* 45 */         String sqlCon = "jdbc:mysql://" + host + "/" + info.getDb() + "?" + 
/* 46 */           "user=" + info.getUser() + "&password=" + info.getPassw();
/* 47 */         if ((info.getAdds() != null) && (info.getAdds().length() > 1)) {
/* 48 */           sqlCon = sqlCon + info.getAdds();
/*    */         }
/*    */ 
/* 52 */         this.connection = DriverManager.getConnection(sqlCon);
/*    */       } catch (InstantiationException e) {
/* 54 */         LogInfo.E("Falla al instanciar el driver ", e);
/*    */       } catch (IllegalAccessException e) {
/* 56 */         LogInfo.E("Falla al conectarse a la BD ", e);
/*    */       } catch (ClassNotFoundException e) {
/* 58 */         LogInfo.E("Falla al conectarse a la BD ", e);
/*    */       }
/*    */     }
/*    */ 
/* 62 */     return this.connection;
/*    */   }
/*    */ 
/*    */   protected void closeResources()
/*    */   {
/*    */     try
/*    */     {
/* 72 */       if ((this.connection != null) && 
/* 73 */         (!this.connection.isClosed()))
/* 74 */         this.connection.close();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 78 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.db.JDBCResourceManager
 * JD-Core Version:    0.6.0
 */