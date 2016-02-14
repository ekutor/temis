package com.mysql.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public abstract interface ResultSetInternalMethods extends ResultSet
{
  public abstract ResultSetInternalMethods copy()
    throws SQLException;

  public abstract boolean reallyResult();

  public abstract Object getObjectStoredProc(int paramInt1, int paramInt2)
    throws SQLException;

  public abstract Object getObjectStoredProc(int paramInt1, Map<Object, Object> paramMap, int paramInt2)
    throws SQLException;

  public abstract Object getObjectStoredProc(String paramString, int paramInt)
    throws SQLException;

  public abstract Object getObjectStoredProc(String paramString, Map<Object, Object> paramMap, int paramInt)
    throws SQLException;

  public abstract String getServerInfo();

  public abstract long getUpdateCount();

  public abstract long getUpdateID();

  public abstract void realClose(boolean paramBoolean)
    throws SQLException;

  public abstract void setFirstCharOfQuery(char paramChar);

  public abstract void setOwningStatement(StatementImpl paramStatementImpl);

  public abstract char getFirstCharOfQuery();

  public abstract void clearNextResult();

  public abstract ResultSetInternalMethods getNextResultSet();

  public abstract void setStatementUsedForFetchingRows(PreparedStatement paramPreparedStatement);

  public abstract void setWrapperStatement(Statement paramStatement);

  public abstract void buildIndexMapping()
    throws SQLException;

  public abstract void initializeWithMetadata()
    throws SQLException;

  public abstract void redefineFieldsForDBMD(Field[] paramArrayOfField);

  public abstract void populateCachedMetaData(CachedResultSetMetaData paramCachedResultSetMetaData)
    throws SQLException;

  public abstract void initializeFromCachedMetaData(CachedResultSetMetaData paramCachedResultSetMetaData);

  public abstract int getBytesSize()
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetInternalMethods
 * JD-Core Version:    0.6.0
 */