package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public abstract interface ConnectionPropertiesTransform
{
  public abstract Properties transformProperties(Properties paramProperties)
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionPropertiesTransform
 * JD-Core Version:    0.6.0
 */