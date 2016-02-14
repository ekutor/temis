package com.lowagie.text.xml;

import java.util.Properties;
import org.xml.sax.Attributes;

public class XmlPeer
{
  protected String tagname;
  protected String customTagname;
  protected Properties attributeAliases = new Properties();
  protected Properties attributeValues = new Properties();
  protected String defaultContent = null;

  public XmlPeer(String paramString1, String paramString2)
  {
    this.tagname = paramString1;
    this.customTagname = paramString2;
  }

  public String getTag()
  {
    return this.tagname;
  }

  public String getAlias()
  {
    return this.customTagname;
  }

  public Properties getAttributes(Attributes paramAttributes)
  {
    Properties localProperties = new Properties();
    localProperties.putAll(this.attributeValues);
    if (this.defaultContent != null)
      localProperties.put("itext", this.defaultContent);
    if (paramAttributes != null)
      for (int i = 0; i < paramAttributes.getLength(); i++)
      {
        String str = getName(paramAttributes.getQName(i));
        localProperties.setProperty(str, paramAttributes.getValue(i));
      }
    return localProperties;
  }

  public void addAlias(String paramString1, String paramString2)
  {
    this.attributeAliases.put(paramString2, paramString1);
  }

  public void addValue(String paramString1, String paramString2)
  {
    this.attributeValues.put(paramString1, paramString2);
  }

  public void setContent(String paramString)
  {
    this.defaultContent = paramString;
  }

  public String getName(String paramString)
  {
    String str;
    if ((str = this.attributeAliases.getProperty(paramString)) != null)
      return str;
    return paramString;
  }

  public Properties getDefaultValues()
  {
    return this.attributeValues;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.XmlPeer
 * JD-Core Version:    0.6.0
 */