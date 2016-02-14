package com.lowagie.text;

import com.lowagie.text.pdf.PdfChunk;

public abstract interface SplitCharacter
{
  public abstract boolean isSplitCharacter(int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar, PdfChunk[] paramArrayOfPdfChunk);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.SplitCharacter
 * JD-Core Version:    0.6.0
 */