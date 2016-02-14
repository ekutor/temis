package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class Hyphen
  implements Serializable
{
  private static final long serialVersionUID = -7666138517324763063L;
  public String preBreak;
  public String noBreak;
  public String postBreak;

  Hyphen(String paramString1, String paramString2, String paramString3)
  {
    this.preBreak = paramString1;
    this.noBreak = paramString2;
    this.postBreak = paramString3;
  }

  Hyphen(String paramString)
  {
    this.preBreak = paramString;
    this.noBreak = null;
    this.postBreak = null;
  }

  public String toString()
  {
    if ((this.noBreak == null) && (this.postBreak == null) && (this.preBreak != null) && (this.preBreak.equals("-")))
      return "-";
    StringBuffer localStringBuffer = new StringBuffer("{");
    localStringBuffer.append(this.preBreak);
    localStringBuffer.append("}{");
    localStringBuffer.append(this.postBreak);
    localStringBuffer.append("}{");
    localStringBuffer.append(this.noBreak);
    localStringBuffer.append('}');
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.Hyphen
 * JD-Core Version:    0.6.0
 */