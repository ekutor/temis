package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;

public class PdfPattern extends PdfStream
{
  PdfPattern(PdfPatternPainter paramPdfPatternPainter)
  {
    this(paramPdfPatternPainter, -1);
  }

  PdfPattern(PdfPatternPainter paramPdfPatternPainter, int paramInt)
  {
    PdfNumber localPdfNumber = new PdfNumber(1);
    PdfArray localPdfArray = paramPdfPatternPainter.getMatrix();
    if (localPdfArray != null)
      put(PdfName.MATRIX, localPdfArray);
    put(PdfName.TYPE, PdfName.PATTERN);
    put(PdfName.BBOX, new PdfRectangle(paramPdfPatternPainter.getBoundingBox()));
    put(PdfName.RESOURCES, paramPdfPatternPainter.getResources());
    put(PdfName.TILINGTYPE, localPdfNumber);
    put(PdfName.PATTERNTYPE, localPdfNumber);
    if (paramPdfPatternPainter.isStencil())
      put(PdfName.PAINTTYPE, new PdfNumber(2));
    else
      put(PdfName.PAINTTYPE, localPdfNumber);
    put(PdfName.XSTEP, new PdfNumber(paramPdfPatternPainter.getXStep()));
    put(PdfName.YSTEP, new PdfNumber(paramPdfPatternPainter.getYStep()));
    this.bytes = paramPdfPatternPainter.toPdf(null);
    put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
    try
    {
      flateCompress(paramInt);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPattern
 * JD-Core Version:    0.6.0
 */