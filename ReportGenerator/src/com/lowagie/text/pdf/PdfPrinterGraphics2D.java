package com.lowagie.text.pdf;

import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;

public class PdfPrinterGraphics2D extends PdfGraphics2D
  implements PrinterGraphics
{
  private PrinterJob printerJob;

  public PdfPrinterGraphics2D(PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, FontMapper paramFontMapper, boolean paramBoolean1, boolean paramBoolean2, float paramFloat3, PrinterJob paramPrinterJob)
  {
    super(paramPdfContentByte, paramFloat1, paramFloat2, paramFontMapper, paramBoolean1, paramBoolean2, paramFloat3);
    this.printerJob = paramPrinterJob;
  }

  public PrinterJob getPrinterJob()
  {
    return this.printerJob;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPrinterGraphics2D
 * JD-Core Version:    0.6.0
 */