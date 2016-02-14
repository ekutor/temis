package com.lowagie.text;

import com.lowagie.text.pdf.codec.TIFFFaxDecoder;
import java.net.URL;

public class ImgCCITT extends Image
{
  ImgCCITT(Image paramImage)
  {
    super(paramImage);
  }

  public ImgCCITT(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws BadElementException
  {
    super((URL)null);
    if ((paramInt3 != 256) && (paramInt3 != 257) && (paramInt3 != 258))
      throw new BadElementException("The CCITT compression type must be CCITTG4, CCITTG3_1D or CCITTG3_2D");
    if (paramBoolean)
      TIFFFaxDecoder.reverseBits(paramArrayOfByte);
    this.type = 34;
    this.scaledHeight = paramInt2;
    setTop(this.scaledHeight);
    this.scaledWidth = paramInt1;
    setRight(this.scaledWidth);
    this.colorspace = paramInt4;
    this.bpc = paramInt3;
    this.rawData = paramArrayOfByte;
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ImgCCITT
 * JD-Core Version:    0.6.0
 */