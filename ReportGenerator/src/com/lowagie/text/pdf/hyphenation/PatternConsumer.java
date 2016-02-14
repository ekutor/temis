package com.lowagie.text.pdf.hyphenation;

import java.util.ArrayList;

public abstract interface PatternConsumer
{
  public abstract void addClass(String paramString);

  public abstract void addException(String paramString, ArrayList paramArrayList);

  public abstract void addPattern(String paramString1, String paramString2);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.PatternConsumer
 * JD-Core Version:    0.6.0
 */