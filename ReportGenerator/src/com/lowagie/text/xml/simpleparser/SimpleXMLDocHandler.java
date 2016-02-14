package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;

public abstract interface SimpleXMLDocHandler
{
  public abstract void startElement(String paramString, HashMap paramHashMap);

  public abstract void endElement(String paramString);

  public abstract void startDocument();

  public abstract void endDocument();

  public abstract void text(String paramString);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler
 * JD-Core Version:    0.6.0
 */