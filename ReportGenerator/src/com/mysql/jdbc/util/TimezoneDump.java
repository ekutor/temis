/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import com.mysql.jdbc.TimeUtil;
/*    */ import java.io.PrintStream;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class TimezoneDump
/*    */ {
/*    */   private static final String DEFAULT_URL = "jdbc:mysql:///test";
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 67 */     String jdbcUrl = "jdbc:mysql:///test";
/*    */ 
/* 69 */     if ((args.length == 1) && (args[0] != null)) {
/* 70 */       jdbcUrl = args[0];
/*    */     }
/*    */ 
/* 73 */     Class.forName("com.mysql.jdbc.Driver").newInstance();
/*    */ 
/* 75 */     ResultSet rs = null;
/*    */     try
/*    */     {
/* 78 */       rs = DriverManager.getConnection(jdbcUrl).createStatement().executeQuery("SHOW VARIABLES LIKE 'timezone'");
/*    */ 
/* 80 */       while (rs.next()) {
/* 81 */         String timezoneFromServer = rs.getString(2);
/* 82 */         System.out.println("MySQL timezone name: " + timezoneFromServer);
/*    */ 
/* 84 */         String canonicalTimezone = TimeUtil.getCanoncialTimezone(timezoneFromServer, null);
/*    */ 
/* 86 */         System.out.println("Java timezone name: " + canonicalTimezone);
/*    */       }
/*    */     } finally {
/* 89 */       if (rs != null)
/* 90 */         rs.close();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.util.TimezoneDump
 * JD-Core Version:    0.6.0
 */