/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.NClob;
/*    */ import java.sql.RowId;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLXML;
/*    */ 
/*    */ public class JDBC4PreparedStatement extends PreparedStatement
/*    */ {
/*    */   public JDBC4PreparedStatement(MySQLConnection conn, String catalog)
/*    */     throws SQLException
/*    */   {
/* 43 */     super(conn, catalog);
/*    */   }
/*    */ 
/*    */   public JDBC4PreparedStatement(MySQLConnection conn, String sql, String catalog) throws SQLException
/*    */   {
/* 48 */     super(conn, sql, catalog);
/*    */   }
/*    */ 
/*    */   public JDBC4PreparedStatement(MySQLConnection conn, String sql, String catalog, PreparedStatement.ParseInfo cachedParseInfo) throws SQLException
/*    */   {
/* 53 */     super(conn, sql, catalog, cachedParseInfo);
/*    */   }
/*    */ 
/*    */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/* 57 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*    */   }
/*    */ 
/*    */   public void setNClob(int parameterIndex, NClob value)
/*    */     throws SQLException
/*    */   {
/* 72 */     JDBC4PreparedStatementHelper.setNClob(this, parameterIndex, value);
/*    */   }
/*    */ 
/*    */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
/*    */   {
/* 77 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4PreparedStatement
 * JD-Core Version:    0.6.0
 */