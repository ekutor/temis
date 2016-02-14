package com.lowagie.text.pdf;

import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.Hyphenator;

public class HyphenationAuto
  implements HyphenationEvent
{
  protected Hyphenator hyphenator;
  protected String post;

  public HyphenationAuto(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    this.hyphenator = new Hyphenator(paramString1, paramString2, paramInt1, paramInt2);
  }

  public String getHyphenSymbol()
  {
    return "-";
  }

  public String getHyphenatedWordPre(String paramString, BaseFont paramBaseFont, float paramFloat1, float paramFloat2)
  {
    this.post = paramString;
    String str = getHyphenSymbol();
    float f = paramBaseFont.getWidthPoint(str, paramFloat1);
    if (f > paramFloat2)
      return "";
    Hyphenation localHyphenation = this.hyphenator.hyphenate(paramString);
    if (localHyphenation == null)
      return "";
    int i = localHyphenation.length();
    for (int j = 0; (j < i) && (paramBaseFont.getWidthPoint(localHyphenation.getPreHyphenText(j), paramFloat1) + f <= paramFloat2); j++);
    j--;
    if (j < 0)
      return "";
    this.post = localHyphenation.getPostHyphenText(j);
    return localHyphenation.getPreHyphenText(j) + str;
  }

  public String getHyphenatedWordPost()
  {
    return this.post;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.HyphenationAuto
 * JD-Core Version:    0.6.0
 */