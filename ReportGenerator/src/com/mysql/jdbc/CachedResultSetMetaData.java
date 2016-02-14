/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CachedResultSetMetaData
/*    */ {
/* 31 */   Map<String, Integer> columnNameToIndex = null;
/*    */   Field[] fields;
/* 37 */   Map<String, Integer> fullColumnNameToIndex = null;
/*    */   ResultSetMetaData metadata;
/*    */ 
/*    */   public Map<String, Integer> getColumnNameToIndex()
/*    */   {
/* 43 */     return this.columnNameToIndex;
/*    */   }
/*    */ 
/*    */   public Field[] getFields() {
/* 47 */     return this.fields;
/*    */   }
/*    */ 
/*    */   public Map<String, Integer> getFullColumnNameToIndex() {
/* 51 */     return this.fullColumnNameToIndex;
/*    */   }
/*    */ 
/*    */   public ResultSetMetaData getMetadata() {
/* 55 */     return this.metadata;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.CachedResultSetMetaData
 * JD-Core Version:    0.6.0
 */