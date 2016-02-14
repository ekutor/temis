package com.lowagie.text.pdf;

import java.awt.Color;

public class SpotColor extends ExtendedColor
{
  private static final long serialVersionUID = -6257004582113248079L;
  PdfSpotColor spot;
  float tint;

  public SpotColor(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    super(3, (paramPdfSpotColor.getAlternativeCS().getRed() / 255.0F - 1.0F) * paramFloat + 1.0F, (paramPdfSpotColor.getAlternativeCS().getGreen() / 255.0F - 1.0F) * paramFloat + 1.0F, (paramPdfSpotColor.getAlternativeCS().getBlue() / 255.0F - 1.0F) * paramFloat + 1.0F);
    this.spot = paramPdfSpotColor;
    this.tint = paramFloat;
  }

  public SpotColor(PdfSpotColor paramPdfSpotColor)
  {
    this(paramPdfSpotColor, paramPdfSpotColor.getTint());
  }

  public PdfSpotColor getPdfSpotColor()
  {
    return this.spot;
  }

  public float getTint()
  {
    return this.tint;
  }

  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }

  public int hashCode()
  {
    return this.spot.hashCode() ^ Float.floatToIntBits(this.tint);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.SpotColor
 * JD-Core Version:    0.6.0
 */