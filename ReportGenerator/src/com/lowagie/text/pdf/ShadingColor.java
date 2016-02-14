package com.lowagie.text.pdf;

public class ShadingColor extends ExtendedColor
{
  private static final long serialVersionUID = 4817929454941328671L;
  PdfShadingPattern shadingPattern;

  public ShadingColor(PdfShadingPattern paramPdfShadingPattern)
  {
    super(5, 0.5F, 0.5F, 0.5F);
    this.shadingPattern = paramPdfShadingPattern;
  }

  public PdfShadingPattern getPdfShadingPattern()
  {
    return this.shadingPattern;
  }

  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }

  public int hashCode()
  {
    return this.shadingPattern.hashCode();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ShadingColor
 * JD-Core Version:    0.6.0
 */