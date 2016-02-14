package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface SocketMetadata
{
  public abstract boolean isLocallyConnected(ConnectionImpl paramConnectionImpl)
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.SocketMetadata
 * JD-Core Version:    0.6.0
 */