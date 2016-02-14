package com.lowagie.text.pdf;

public class PatternColor extends ExtendedColor
{
  private static final long serialVersionUID = -1185448552860615964L;
  PdfPatternPainter painter;

  public PatternColor(PdfPatternPainter paramPdfPatternPainter)
  {
    super(4, 0.5F, 0.5F, 0.5F);
    this.painter = paramPdfPatternPainter;
  }

  public PdfPatternPainter getPainter()
  {
    return this.painter;
  }

  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }

  public int hashCode()
  {
    return this.painter.hashCode();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PatternColor
 * JD-Core Version:    0.6.0
 */