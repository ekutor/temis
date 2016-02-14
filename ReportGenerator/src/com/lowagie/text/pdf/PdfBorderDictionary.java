package com.lowagie.text.pdf;

public class PdfBorderDictionary extends PdfDictionary
{
  public static final int STYLE_SOLID = 0;
  public static final int STYLE_DASHED = 1;
  public static final int STYLE_BEVELED = 2;
  public static final int STYLE_INSET = 3;
  public static final int STYLE_UNDERLINE = 4;

  public PdfBorderDictionary(float paramFloat, int paramInt, PdfDashPattern paramPdfDashPattern)
  {
    put(PdfName.W, new PdfNumber(paramFloat));
    switch (paramInt)
    {
    case 0:
      put(PdfName.S, PdfName.S);
      break;
    case 1:
      if (paramPdfDashPattern != null)
        put(PdfName.D, paramPdfDashPattern);
      put(PdfName.S, PdfName.D);
      break;
    case 2:
      put(PdfName.S, PdfName.B);
      break;
    case 3:
      put(PdfName.S, PdfName.I);
      break;
    case 4:
      put(PdfName.S, PdfName.U);
      break;
    default:
      throw new IllegalArgumentException("Invalid border style.");
    }
  }

  public PdfBorderDictionary(float paramFloat, int paramInt)
  {
    this(paramFloat, paramInt, null);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfBorderDictionary
 * JD-Core Version:    0.6.0
 */