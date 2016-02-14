 package com.co.hsg.generator.db;
 
 import com.co.hsg.generator.io.FileManager;
 import com.co.hsg.generator.io.InfoDB;
 import com.co.hsg.generator.log.LogInfo;
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 
 public class JDBCResourceManager
 {
   private Connection connection;
 
   public Connection getConnection()
     throws SQLException
   {
     if ((this.connection == null) || (this.connection.isClosed())) {
       try {
         Class.forName("com.mysql.jdbc.Driver").newInstance();
 
         InfoDB info = FileManager.getInst().chargeServerProps();
         String host = info.getHost();
         if ((info.getPort() != null) && (info.getPort().length() > 1)) {
           host = host + ":" + info.getPort();
         }
         String sqlCon = "jdbc:mysql://" + host + "/" + info.getDb() + "?" + 
           "user=" + info.getUser() + "&password=" + info.getPassw();
         if ((info.getAdds() != null) && (info.getAdds().length() > 1)) {
           sqlCon = sqlCon + info.getAdds();
         }
 
         this.connection = DriverManager.getConnection(sqlCon);
       } catch (InstantiationException e) {
         LogInfo.E("Falla al instanciar el driver ", e);
       } catch (IllegalAccessException e) {
         LogInfo.E("Falla al conectarse a la BD ", e);
       } catch (ClassNotFoundException e) {
         LogInfo.E("Falla al conectarse a la BD ", e);
       }
     }
 
     return this.connection;
   }
 
   protected void closeResources()
   {
     try
     {
       if ((this.connection != null) && 
         (!this.connection.isClosed()))
         this.connection.close();
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
   }
 }

