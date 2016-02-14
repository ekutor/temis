package com.lowagie.text.pdf;

import com.lowagie.text.SplitCharacter;

public class DefaultSplitCharacter
  implements SplitCharacter
{
  public static final SplitCharacter DEFAULT = new DefaultSplitCharacter();

  public boolean isSplitCharacter(int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar, PdfChunk[] paramArrayOfPdfChunk)
  {
    int i = getCurrentCharacter(paramInt2, paramArrayOfChar, paramArrayOfPdfChunk);
    if ((i <= 32) || (i == 45) || (i == 8208))
      return true;
    if (i < 8194)
      return false;
    return ((i >= 8194) && (i <= 8203)) || ((i >= 11904) && (i < 55200)) || ((i >= 63744) && (i < 64256)) || ((i >= 65072) && (i < 65104)) || ((i >= 65377) && (i < 65440));
  }

  protected char getCurrentCharacter(int paramInt, char[] paramArrayOfChar, PdfChunk[] paramArrayOfPdfChunk)
  {
    if (paramArrayOfPdfChunk == null)
      return paramArrayOfChar[paramInt];
    return (char)paramArrayOfPdfChunk[java.lang.Math.min(paramInt, paramArrayOfPdfChunk.length - 1)].getUnicodeEquivalent(paramArrayOfChar[paramInt]);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.DefaultSplitCharacter
 * JD-Core Version:    0.6.0
 */