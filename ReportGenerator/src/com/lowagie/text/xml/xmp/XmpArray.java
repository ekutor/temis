package com.lowagie.text.xml.xmp;

import java.util.ArrayList;
import java.util.Iterator;

public class XmpArray extends ArrayList
{
  private static final long serialVersionUID = 5722854116328732742L;
  public static final String UNORDERED = "rdf:Bag";
  public static final String ORDERED = "rdf:Seq";
  public static final String ALTERNATIVE = "rdf:Alt";
  protected String type;

  public XmpArray(String paramString)
  {
    this.type = paramString;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("<");
    localStringBuffer.append(this.type);
    localStringBuffer.append('>');
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuffer.append("<rdf:li>");
      localStringBuffer.append(XmpSchema.escape(str));
      localStringBuffer.append("</rdf:li>");
    }
    localStringBuffer.append("</");
    localStringBuffer.append(this.type);
    localStringBuffer.append('>');
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.XmpArray
 * JD-Core Version:    0.6.0
 */