package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public abstract interface Extension
{
  public abstract void init(Connection paramConnection, Properties paramProperties)
    throws SQLException;

  public abstract void destroy();
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Extension
 * JD-Core Version:    0.6.0
 */