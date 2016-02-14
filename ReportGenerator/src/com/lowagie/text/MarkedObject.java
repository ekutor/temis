package com.lowagie.text;

import java.util.ArrayList;
import java.util.Properties;

public class MarkedObject
  implements Element
{
  protected Element element;
  protected Properties markupAttributes = new Properties();

  protected MarkedObject()
  {
    this.element = null;
  }

  public MarkedObject(Element paramElement)
  {
    this.element = paramElement;
  }

  public ArrayList getChunks()
  {
    return this.element.getChunks();
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      return paramElementListener.add(this.element);
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return 50;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
  }

  public Properties getMarkupAttributes()
  {
    return this.markupAttributes;
  }

  public void setMarkupAttribute(String paramString1, String paramString2)
  {
    this.markupAttributes.setProperty(paramString1, paramString2);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.MarkedObject
 * JD-Core Version:    0.6.0
 */