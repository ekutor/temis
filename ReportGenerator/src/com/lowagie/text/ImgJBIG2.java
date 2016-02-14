package com.lowagie.text;

import java.net.URL;
import java.security.MessageDigest;

public class ImgJBIG2 extends Image
{
  private byte[] global;
  private byte[] globalHash;

  ImgJBIG2(Image paramImage)
  {
    super(paramImage);
  }

  public ImgJBIG2()
  {
    super((Image)null);
  }

  public ImgJBIG2(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super((URL)null);
    this.type = 36;
    this.originalType = 9;
    this.scaledHeight = paramInt2;
    setTop(this.scaledHeight);
    this.scaledWidth = paramInt1;
    setRight(this.scaledWidth);
    this.bpc = 1;
    this.colorspace = 1;
    this.rawData = paramArrayOfByte1;
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
    if (paramArrayOfByte2 != null)
    {
      this.global = paramArrayOfByte2;
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(this.global);
        this.globalHash = localMessageDigest.digest();
      }
      catch (Exception localException)
      {
      }
    }
  }

  public byte[] getGlobalBytes()
  {
    return this.global;
  }

  public byte[] getGlobalHash()
  {
    return this.globalHash;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ImgJBIG2
 * JD-Core Version:    0.6.0
 */