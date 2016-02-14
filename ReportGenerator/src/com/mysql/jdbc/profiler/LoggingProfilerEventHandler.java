/*    */ package com.mysql.jdbc.profiler;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class LoggingProfilerEventHandler
/*    */   implements ProfilerEventHandler
/*    */ {
/*    */   private Log log;
/*    */ 
/*    */   public void consumeEvent(ProfilerEvent evt)
/*    */   {
/* 44 */     if (evt.eventType == 0)
/* 45 */       this.log.logWarn(evt);
/*    */     else
/* 47 */       this.log.logInfo(evt);
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/* 52 */     this.log = null;
/*    */   }
/*    */ 
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 56 */     this.log = conn.getLog();
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.profiler.LoggingProfilerEventHandler
 * JD-Core Version:    0.6.0
 */