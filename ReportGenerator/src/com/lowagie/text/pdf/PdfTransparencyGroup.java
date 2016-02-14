package com.lowagie.text.pdf;

public class PdfTransparencyGroup extends PdfDictionary
{
  public PdfTransparencyGroup()
  {
    put(PdfName.S, PdfName.TRANSPARENCY);
  }

  public void setIsolated(boolean paramBoolean)
  {
    if (paramBoolean)
      put(PdfName.I, PdfBoolean.PDFTRUE);
    else
      remove(PdfName.I);
  }

  public void setKnockout(boolean paramBoolean)
  {
    if (paramBoolean)
      put(PdfName.K, PdfBoolean.PDFTRUE);
    else
      remove(PdfName.K);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfTransparencyGroup
 * JD-Core Version:    0.6.0
 */