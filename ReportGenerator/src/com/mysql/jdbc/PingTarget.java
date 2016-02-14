package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface PingTarget
{
  public abstract void doPing()
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.PingTarget
 * JD-Core Version:    0.6.0
 */