package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface ExceptionInterceptor extends Extension
{
  public abstract SQLException interceptException(SQLException paramSQLException, Connection paramConnection);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.ExceptionInterceptor
 * JD-Core Version:    0.6.0
 */