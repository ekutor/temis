package com.lowagie.text.html.simpleparser;

import com.lowagie.text.DocListener;
import com.lowagie.text.Image;
import java.util.HashMap;

public abstract interface ImageProvider
{
  public abstract Image getImage(String paramString, HashMap paramHashMap, ChainedProperties paramChainedProperties, DocListener paramDocListener);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.ImageProvider
 * JD-Core Version:    0.6.0
 */