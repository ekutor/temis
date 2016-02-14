package com.lowagie.text.xml.xmp;

import java.util.Enumeration;
import java.util.Properties;

public class LangAlt extends Properties
{
  private static final long serialVersionUID = 4396971487200843099L;
  public static final String DEFAULT = "x-default";

  public LangAlt(String paramString)
  {
    addLanguage("x-default", paramString);
  }

  public LangAlt()
  {
  }

  public void addLanguage(String paramString1, String paramString2)
  {
    setProperty(paramString1, XmpSchema.escape(paramString2));
  }

  protected void process(StringBuffer paramStringBuffer, Object paramObject)
  {
    paramStringBuffer.append("<rdf:li xml:lang=\"");
    paramStringBuffer.append(paramObject);
    paramStringBuffer.append("\" >");
    paramStringBuffer.append(get(paramObject));
    paramStringBuffer.append("</rdf:li>");
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("<rdf:Alt>");
    Enumeration localEnumeration = propertyNames();
    while (localEnumeration.hasMoreElements())
      process(localStringBuffer, localEnumeration.nextElement());
    localStringBuffer.append("</rdf:Alt>");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.LangAlt
 * JD-Core Version:    0.6.0
 */