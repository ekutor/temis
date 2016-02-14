package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;

public abstract interface AuthenticationPlugin extends Extension
{
  public abstract String getProtocolPluginName();

  public abstract boolean requiresConfidentiality();

  public abstract boolean isReusable();

  public abstract void setAuthenticationParameters(String paramString1, String paramString2);

  public abstract boolean nextAuthenticationStep(Buffer paramBuffer, List<Buffer> paramList)
    throws SQLException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.AuthenticationPlugin
 * JD-Core Version:    0.6.0
 */