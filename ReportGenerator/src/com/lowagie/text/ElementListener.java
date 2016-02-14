package com.lowagie.text;

import java.util.EventListener;

public abstract interface ElementListener extends EventListener
{
  public abstract boolean add(Element paramElement)
    throws DocumentException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ElementListener
 * JD-Core Version:    0.6.0
 */