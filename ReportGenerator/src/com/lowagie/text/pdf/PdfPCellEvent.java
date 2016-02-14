package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;

public abstract interface PdfPCellEvent
{
  public abstract void cellLayout(PdfPCell paramPdfPCell, Rectangle paramRectangle, PdfContentByte[] paramArrayOfPdfContentByte);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPCellEvent
 * JD-Core Version:    0.6.0
 */