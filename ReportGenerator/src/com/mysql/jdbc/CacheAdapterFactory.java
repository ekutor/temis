package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public abstract interface CacheAdapterFactory<K, V>
{
  public abstract CacheAdapter<K, V> getInstance(Connection paramConnection, String paramString, int paramInt1, int paramInt2, Properties paramProperties)
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.CacheAdapterFactory
 * JD-Core Version:    0.6.0
 */