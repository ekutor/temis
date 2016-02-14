/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.RowIdLifetime;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class JDBC4DatabaseMetaDataUsingInfoSchema extends DatabaseMetaDataUsingInfoSchema
/*    */ {
/*    */   public JDBC4DatabaseMetaDataUsingInfoSchema(MySQLConnection connToSet, String databaseToSet)
/*    */     throws SQLException
/*    */   {
/* 37 */     super(connToSet, databaseToSet);
/*    */   }
/*    */ 
/*    */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/* 41 */     return RowIdLifetime.ROWID_UNSUPPORTED;
/*    */   }
/*    */ 
/*    */   public boolean isWrapperFor(Class<?> iface)
/*    */     throws SQLException
/*    */   {
/* 62 */     return iface.isInstance(this);
/*    */   }
/*    */ 
/*    */   public <T> T unwrap(Class<T> iface)
/*    */     throws SQLException
/*    */   {
/*    */     try
/*    */     {
/* 83 */       return iface.cast(this); } catch (ClassCastException cce) {
/*    */     }
/* 85 */     throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.conn.getExceptionInterceptor());
/*    */   }
/*    */ 
/*    */   protected int getJDBC4FunctionNoTableConstant()
/*    */   {
/* 91 */     return 1;
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema
 * JD-Core Version:    0.6.0
 */