package com.lowagie.text.pdf;

public class PdfBorderArray extends PdfArray
{
  public PdfBorderArray(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this(paramFloat1, paramFloat2, paramFloat3, null);
  }

  public PdfBorderArray(float paramFloat1, float paramFloat2, float paramFloat3, PdfDashPattern paramPdfDashPattern)
  {
    super(new PdfNumber(paramFloat1));
    add(new PdfNumber(paramFloat2));
    add(new PdfNumber(paramFloat3));
    if (paramPdfDashPattern != null)
      add(paramPdfDashPattern);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfBorderArray
 * JD-Core Version:    0.6.0
 */