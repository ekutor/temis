package com.lowagie.text.pdf;

import java.awt.Color;

public class PdfSpotColor
{
  protected float tint;
  public PdfName name;
  public Color altcs;

  public PdfSpotColor(String paramString, float paramFloat, Color paramColor)
  {
    this.name = new PdfName(paramString);
    this.tint = paramFloat;
    this.altcs = paramColor;
  }

  public float getTint()
  {
    return this.tint;
  }

  public Color getAlternativeCS()
  {
    return this.altcs;
  }

  protected PdfObject getSpotObject(PdfWriter paramPdfWriter)
  {
    PdfArray localPdfArray = new PdfArray(PdfName.SEPARATION);
    localPdfArray.add(this.name);
    PdfFunction localPdfFunction = null;
    if ((this.altcs instanceof ExtendedColor))
    {
      int i = ((ExtendedColor)this.altcs).type;
      switch (i)
      {
      case 1:
        localPdfArray.add(PdfName.DEVICEGRAY);
        localPdfFunction = PdfFunction.type2(paramPdfWriter, new float[] { 0.0F, 1.0F }, null, new float[] { 0.0F }, new float[] { ((GrayColor)this.altcs).getGray() }, 1.0F);
        break;
      case 2:
        localPdfArray.add(PdfName.DEVICECMYK);
        CMYKColor localCMYKColor = (CMYKColor)this.altcs;
        localPdfFunction = PdfFunction.type2(paramPdfWriter, new float[] { 0.0F, 1.0F }, null, new float[] { 0.0F, 0.0F, 0.0F, 0.0F }, new float[] { localCMYKColor.getCyan(), localCMYKColor.getMagenta(), localCMYKColor.getYellow(), localCMYKColor.getBlack() }, 1.0F);
        break;
      default:
        throw new RuntimeException("Only RGB, Gray and CMYK are supported as alternative color spaces.");
      }
    }
    else
    {
      localPdfArray.add(PdfName.DEVICERGB);
      localPdfFunction = PdfFunction.type2(paramPdfWriter, new float[] { 0.0F, 1.0F }, null, new float[] { 1.0F, 1.0F, 1.0F }, new float[] { this.altcs.getRed() / 255.0F, this.altcs.getGreen() / 255.0F, this.altcs.getBlue() / 255.0F }, 1.0F);
    }
    localPdfArray.add(localPdfFunction.getReference());
    return localPdfArray;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfSpotColor
 * JD-Core Version:    0.6.0
 */