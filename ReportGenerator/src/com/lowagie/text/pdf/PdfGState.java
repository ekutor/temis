package com.lowagie.text.pdf;

public class PdfGState extends PdfDictionary
{
  public static final PdfName BM_NORMAL = new PdfName("Normal");
  public static final PdfName BM_COMPATIBLE = new PdfName("Compatible");
  public static final PdfName BM_MULTIPLY = new PdfName("Multiply");
  public static final PdfName BM_SCREEN = new PdfName("Screen");
  public static final PdfName BM_OVERLAY = new PdfName("Overlay");
  public static final PdfName BM_DARKEN = new PdfName("Darken");
  public static final PdfName BM_LIGHTEN = new PdfName("Lighten");
  public static final PdfName BM_COLORDODGE = new PdfName("ColorDodge");
  public static final PdfName BM_COLORBURN = new PdfName("ColorBurn");
  public static final PdfName BM_HARDLIGHT = new PdfName("HardLight");
  public static final PdfName BM_SOFTLIGHT = new PdfName("SoftLight");
  public static final PdfName BM_DIFFERENCE = new PdfName("Difference");
  public static final PdfName BM_EXCLUSION = new PdfName("Exclusion");

  public void setOverPrintStroking(boolean paramBoolean)
  {
    put(PdfName.OP, paramBoolean ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
  }

  public void setOverPrintNonStroking(boolean paramBoolean)
  {
    put(PdfName.op, paramBoolean ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
  }

  public void setOverPrintMode(int paramInt)
  {
    put(PdfName.OPM, new PdfNumber(paramInt == 0 ? 0 : 1));
  }

  public void setStrokeOpacity(float paramFloat)
  {
    put(PdfName.CA, new PdfNumber(paramFloat));
  }

  public void setFillOpacity(float paramFloat)
  {
    put(PdfName.ca, new PdfNumber(paramFloat));
  }

  public void setAlphaIsShape(boolean paramBoolean)
  {
    put(PdfName.AIS, paramBoolean ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
  }

  public void setTextKnockout(boolean paramBoolean)
  {
    put(PdfName.TK, paramBoolean ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
  }

  public void setBlendMode(PdfName paramPdfName)
  {
    put(PdfName.BM, paramPdfName);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfGState
 * JD-Core Version:    0.6.0
 */