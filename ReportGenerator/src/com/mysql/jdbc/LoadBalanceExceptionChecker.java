package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface LoadBalanceExceptionChecker extends Extension
{
  public abstract boolean shouldExceptionTriggerFailover(SQLException paramSQLException);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.LoadBalanceExceptionChecker
 * JD-Core Version:    0.6.0
 */