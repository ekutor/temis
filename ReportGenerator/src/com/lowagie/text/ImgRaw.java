package com.lowagie.text;

import java.net.URL;

public class ImgRaw extends Image
{
  ImgRaw(Image paramImage)
  {
    super(paramImage);
  }

  public ImgRaw(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws BadElementException
  {
    super((URL)null);
    this.type = 34;
    this.scaledHeight = paramInt2;
    setTop(this.scaledHeight);
    this.scaledWidth = paramInt1;
    setRight(this.scaledWidth);
    if ((paramInt3 != 1) && (paramInt3 != 3) && (paramInt3 != 4))
      throw new BadElementException("Components must be 1, 3, or 4.");
    if ((paramInt4 != 1) && (paramInt4 != 2) && (paramInt4 != 4) && (paramInt4 != 8))
      throw new BadElementException("Bits-per-component must be 1, 2, 4, or 8.");
    this.colorspace = paramInt3;
    this.bpc = paramInt4;
    this.rawData = paramArrayOfByte;
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ImgRaw
 * JD-Core Version:    0.6.0
 */