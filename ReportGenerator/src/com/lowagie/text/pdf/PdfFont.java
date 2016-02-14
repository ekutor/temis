package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;

class PdfFont
  implements Comparable
{
  private BaseFont font;
  private float size;
  protected Image image;
  protected float hScale = 1.0F;

  PdfFont(BaseFont paramBaseFont, float paramFloat)
  {
    this.size = paramFloat;
    this.font = paramBaseFont;
  }

  public int compareTo(Object paramObject)
  {
    if (this.image != null)
      return 0;
    if (paramObject == null)
      return -1;
    try
    {
      PdfFont localPdfFont = (PdfFont)paramObject;
      if (this.font != localPdfFont.font)
        return 1;
      if (size() != localPdfFont.size())
        return 2;
      return 0;
    }
    catch (ClassCastException localClassCastException)
    {
    }
    return -2;
  }

  float size()
  {
    if (this.image == null)
      return this.size;
    return this.image.getScaledHeight();
  }

  float width()
  {
    return width(32);
  }

  float width(int paramInt)
  {
    if (this.image == null)
      return this.font.getWidthPoint(paramInt, this.size) * this.hScale;
    return this.image.getScaledWidth();
  }

  float width(String paramString)
  {
    if (this.image == null)
      return this.font.getWidthPoint(paramString, this.size) * this.hScale;
    return this.image.getScaledWidth();
  }

  BaseFont getFont()
  {
    return this.font;
  }

  void setImage(Image paramImage)
  {
    this.image = paramImage;
  }

  static PdfFont getDefaultFont()
  {
    try
    {
      BaseFont localBaseFont = BaseFont.createFont("Helvetica", "Cp1252", false);
      return new PdfFont(localBaseFont, 12.0F);
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  void setHorizontalScaling(float paramFloat)
  {
    this.hScale = paramFloat;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfFont
 * JD-Core Version:    0.6.0
 */