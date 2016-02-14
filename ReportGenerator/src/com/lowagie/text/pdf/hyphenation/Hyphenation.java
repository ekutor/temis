package com.lowagie.text.pdf.hyphenation;

public class Hyphenation
{
  private int[] hyphenPoints;
  private String word;
  private int len;

  Hyphenation(String paramString, int[] paramArrayOfInt)
  {
    this.word = paramString;
    this.hyphenPoints = paramArrayOfInt;
    this.len = paramArrayOfInt.length;
  }

  public int length()
  {
    return this.len;
  }

  public String getPreHyphenText(int paramInt)
  {
    return this.word.substring(0, this.hyphenPoints[paramInt]);
  }

  public String getPostHyphenText(int paramInt)
  {
    return this.word.substring(this.hyphenPoints[paramInt]);
  }

  public int[] getHyphenationPoints()
  {
    return this.hyphenPoints;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    for (int j = 0; j < this.len; j++)
    {
      localStringBuffer.append(this.word.substring(i, this.hyphenPoints[j])).append('-');
      i = this.hyphenPoints[j];
    }
    localStringBuffer.append(this.word.substring(i));
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.Hyphenation
 * JD-Core Version:    0.6.0
 */