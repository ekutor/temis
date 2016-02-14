/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ProfilerEventHandlerFactory
/*    */ {
/* 39 */   private static final Map<MySQLConnection, ProfilerEventHandler> CONNECTIONS_TO_SINKS = new HashMap();
/*    */ 
/* 41 */   private Connection ownerConnection = null;
/*    */ 
/* 43 */   protected Log log = null;
/*    */ 
/*    */   public static synchronized ProfilerEventHandler getInstance(MySQLConnection conn)
/*    */     throws SQLException
/*    */   {
/* 54 */     ProfilerEventHandler handler = (ProfilerEventHandler)CONNECTIONS_TO_SINKS.get(conn);
/*    */ 
/* 57 */     if (handler == null) {
/* 58 */       handler = (ProfilerEventHandler)Util.getInstance(conn.getProfilerEventHandler(), new Class[0], new Object[0], conn.getExceptionInterceptor());
/*    */ 
/* 63 */       conn.initializeExtension(handler);
/*    */ 
/* 65 */       CONNECTIONS_TO_SINKS.put(conn, handler);
/*    */     }
/*    */ 
/* 68 */     return handler;
/*    */   }
/*    */ 
/*    */   public static synchronized void removeInstance(Connection conn) {
/* 72 */     ProfilerEventHandler handler = (ProfilerEventHandler)CONNECTIONS_TO_SINKS.remove(conn);
/*    */ 
/* 74 */     if (handler != null)
/* 75 */       handler.destroy();
/*    */   }
/*    */ 
/*    */   private ProfilerEventHandlerFactory(Connection conn)
/*    */   {
/* 80 */     this.ownerConnection = conn;
/*    */     try
/*    */     {
/* 83 */       this.log = this.ownerConnection.getLog();
/*    */     } catch (SQLException sqlEx) {
/* 85 */       throw new RuntimeException("Unable to get logger from connection");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ProfilerEventHandlerFactory
 * JD-Core Version:    0.6.0
 */