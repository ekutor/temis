package com.lowagie.text.pdf;

public abstract interface HyphenationEvent
{
  public abstract String getHyphenSymbol();

  public abstract String getHyphenatedWordPre(String paramString, BaseFont paramBaseFont, float paramFloat1, float paramFloat2);

  public abstract String getHyphenatedWordPost();
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.HyphenationEvent
 * JD-Core Version:    0.6.0
 */