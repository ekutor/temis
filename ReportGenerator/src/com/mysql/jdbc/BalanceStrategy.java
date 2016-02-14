package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract interface BalanceStrategy extends Extension
{
  public abstract ConnectionImpl pickConnection(LoadBalancingConnectionProxy paramLoadBalancingConnectionProxy, List<String> paramList, Map<String, ConnectionImpl> paramMap, long[] paramArrayOfLong, int paramInt)
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.BalanceStrategy
 * JD-Core Version:    0.6.0
 */