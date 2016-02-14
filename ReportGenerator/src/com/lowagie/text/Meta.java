package com.lowagie.text;

import java.util.ArrayList;

public class Meta
  implements Element
{
  private int type;
  private StringBuffer content;

  Meta(int paramInt, String paramString)
  {
    this.type = paramInt;
    this.content = new StringBuffer(paramString);
  }

  public Meta(String paramString1, String paramString2)
  {
    this.type = getType(paramString1);
    this.content = new StringBuffer(paramString2);
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      return paramElementListener.add(this);
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return this.type;
  }

  public ArrayList getChunks()
  {
    return new ArrayList();
  }

  public boolean isContent()
  {
    return false;
  }

  public boolean isNestable()
  {
    return false;
  }

  public StringBuffer append(String paramString)
  {
    return this.content.append(paramString);
  }

  public String getContent()
  {
    return this.content.toString();
  }

  public String getName()
  {
    switch (this.type)
    {
    case 2:
      return "subject";
    case 3:
      return "keywords";
    case 4:
      return "author";
    case 1:
      return "title";
    case 5:
      return "producer";
    case 6:
      return "creationdate";
    }
    return "unknown";
  }

  public static int getType(String paramString)
  {
    if ("subject".equals(paramString))
      return 2;
    if ("keywords".equals(paramString))
      return 3;
    if ("author".equals(paramString))
      return 4;
    if ("title".equals(paramString))
      return 1;
    if ("producer".equals(paramString))
      return 5;
    if ("creationdate".equals(paramString))
      return 6;
    return 0;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Meta
 * JD-Core Version:    0.6.0
 */