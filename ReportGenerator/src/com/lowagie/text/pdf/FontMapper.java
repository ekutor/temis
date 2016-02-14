package com.lowagie.text.pdf;

import java.awt.Font;

public abstract interface FontMapper
{
  public abstract BaseFont awtToPdf(Font paramFont);

  public abstract Font pdfToAwt(BaseFont paramBaseFont, int paramInt);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.FontMapper
 * JD-Core Version:    0.6.0
 */