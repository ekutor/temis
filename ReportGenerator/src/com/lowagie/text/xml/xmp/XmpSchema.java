package com.lowagie.text.xml.xmp;

import java.util.Enumeration;
import java.util.Properties;

public abstract class XmpSchema extends Properties
{
  private static final long serialVersionUID = -176374295948945272L;
  protected String xmlns;

  public XmpSchema(String paramString)
  {
    this.xmlns = paramString;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Enumeration localEnumeration = propertyNames();
    while (localEnumeration.hasMoreElements())
      process(localStringBuffer, localEnumeration.nextElement());
    return localStringBuffer.toString();
  }

  protected void process(StringBuffer paramStringBuffer, Object paramObject)
  {
    paramStringBuffer.append('<');
    paramStringBuffer.append(paramObject);
    paramStringBuffer.append('>');
    paramStringBuffer.append(get(paramObject));
    paramStringBuffer.append("</");
    paramStringBuffer.append(paramObject);
    paramStringBuffer.append('>');
  }

  public String getXmlns()
  {
    return this.xmlns;
  }

  public Object addProperty(String paramString1, String paramString2)
  {
    return setProperty(paramString1, paramString2);
  }

  public Object setProperty(String paramString1, String paramString2)
  {
    return super.setProperty(paramString1, escape(paramString2));
  }

  public Object setProperty(String paramString, XmpArray paramXmpArray)
  {
    return super.setProperty(paramString, paramXmpArray.toString());
  }

  public Object setProperty(String paramString, LangAlt paramLangAlt)
  {
    return super.setProperty(paramString, paramLangAlt.toString());
  }

  public static String escape(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
      switch (paramString.charAt(i))
      {
      case '<':
        localStringBuffer.append("&lt;");
        break;
      case '>':
        localStringBuffer.append("&gt;");
        break;
      case '\'':
        localStringBuffer.append("&apos;");
        break;
      case '"':
        localStringBuffer.append("&quot;");
        break;
      case '&':
        localStringBuffer.append("&amp;");
        break;
      default:
        localStringBuffer.append(paramString.charAt(i));
      }
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.XmpSchema
 * JD-Core Version:    0.6.0
 */