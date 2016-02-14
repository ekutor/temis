package com.lowagie.text.html;

import com.lowagie.text.xml.XmlPeer;
import java.util.Properties;
import org.xml.sax.Attributes;

public class HtmlPeer extends XmlPeer
{
  public HtmlPeer(String paramString1, String paramString2)
  {
    super(paramString1, paramString2.toLowerCase());
  }

  public void addAlias(String paramString1, String paramString2)
  {
    this.attributeAliases.put(paramString2.toLowerCase(), paramString1);
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
        String str1 = getName(paramAttributes.getQName(i).toLowerCase());
        String str2 = paramAttributes.getValue(i);
        localProperties.setProperty(str1, str2);
      }
    return localProperties;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.HtmlPeer
 * JD-Core Version:    0.6.0
 */