package com.lowagie.text.pdf;

import java.awt.Color;

class PdfColor extends PdfArray
{
  PdfColor(int paramInt1, int paramInt2, int paramInt3)
  {
    super(new PdfNumber((paramInt1 & 0xFF) / 255.0D));
    add(new PdfNumber((paramInt2 & 0xFF) / 255.0D));
    add(new PdfNumber((paramInt3 & 0xFF) / 255.0D));
  }

  PdfColor(Color paramColor)
  {
    this(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue());
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfColor
 * JD-Core Version:    0.6.0
 */