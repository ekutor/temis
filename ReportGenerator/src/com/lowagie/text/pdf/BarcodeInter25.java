package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class BarcodeInter25 extends Barcode
{
  private static final byte[][] BARS = { { 0, 0, 1, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 0, 0, 1 }, { 1, 1, 0, 0, 0 }, { 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0 }, { 0, 0, 0, 1, 1 }, { 1, 0, 0, 1, 0 }, { 0, 1, 0, 1, 0 } };

  public BarcodeInter25()
  {
    try
    {
      this.x = 0.8F;
      this.n = 2.0F;
      this.font = BaseFont.createFont("Helvetica", "winansi", false);
      this.size = 8.0F;
      this.baseline = this.size;
      this.barHeight = (this.size * 3.0F);
      this.textAlignment = 1;
      this.generateChecksum = false;
      this.checksumText = false;
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public static String keepNumbers(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c < '0') || (c > '9'))
        continue;
      localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }

  public static char getChecksum(String paramString)
  {
    int i = 3;
    int j = 0;
    for (int k = paramString.length() - 1; k >= 0; k--)
    {
      int m = paramString.charAt(k) - '0';
      j += i * m;
      i ^= 2;
    }
    return (char)((10 - j % 10) % 10 + 48);
  }

  public static byte[] getBarsInter25(String paramString)
  {
    paramString = keepNumbers(paramString);
    if ((paramString.length() & 0x1) != 0)
      throw new IllegalArgumentException("The text length must be even.");
    byte[] arrayOfByte1 = new byte[paramString.length() * 5 + 7];
    int i = 0;
    arrayOfByte1[(i++)] = 0;
    arrayOfByte1[(i++)] = 0;
    arrayOfByte1[(i++)] = 0;
    arrayOfByte1[(i++)] = 0;
    int j = paramString.length() / 2;
    for (int k = 0; k < j; k++)
    {
      int m = paramString.charAt(k * 2) - '0';
      int n = paramString.charAt(k * 2 + 1) - '0';
      byte[] arrayOfByte2 = BARS[m];
      byte[] arrayOfByte3 = BARS[n];
      for (int i1 = 0; i1 < 5; i1++)
      {
        arrayOfByte1[(i++)] = arrayOfByte2[i1];
        arrayOfByte1[(i++)] = arrayOfByte3[i1];
      }
    }
    arrayOfByte1[(i++)] = 1;
    arrayOfByte1[(i++)] = 0;
    arrayOfByte1[(i++)] = 0;
    return arrayOfByte1;
  }

  public Rectangle getBarcodeSize()
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (this.font != null)
    {
      if (this.baseline > 0.0F)
        f2 = this.baseline - this.font.getFontDescriptor(3, this.size);
      else
        f2 = -this.baseline + this.size;
      str = this.code;
      if ((this.generateChecksum) && (this.checksumText))
        str = str + getChecksum(str);
      f1 = this.font.getWidthPoint(this.altText != null ? this.altText : str, this.size);
    }
    String str = keepNumbers(this.code);
    int i = str.length();
    if (this.generateChecksum)
      i++;
    float f3 = i * (3.0F * this.x + 2.0F * this.x * this.n) + (6.0F + this.n) * this.x;
    f3 = Math.max(f3, f1);
    float f4 = this.barHeight + f2;
    return new Rectangle(f3, f4);
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    String str1 = this.code;
    float f1 = 0.0F;
    if (this.font != null)
    {
      if ((this.generateChecksum) && (this.checksumText))
        str1 = str1 + getChecksum(str1);
      f1 = this.font.getWidthPoint(str1 = this.altText != null ? this.altText : str1, this.size);
    }
    String str2 = keepNumbers(this.code);
    if (this.generateChecksum)
      str2 = str2 + getChecksum(str2);
    int i = str2.length();
    float f2 = i * (3.0F * this.x + 2.0F * this.x * this.n) + (6.0F + this.n) * this.x;
    float f3 = 0.0F;
    float f4 = 0.0F;
    switch (this.textAlignment)
    {
    case 0:
      break;
    case 2:
      if (f1 > f2)
        f3 = f1 - f2;
      else
        f4 = f2 - f1;
      break;
    default:
      if (f1 > f2)
        f3 = (f1 - f2) / 2.0F;
      else
        f4 = (f2 - f1) / 2.0F;
    }
    float f5 = 0.0F;
    float f6 = 0.0F;
    if (this.font != null)
      if (this.baseline <= 0.0F)
      {
        f6 = this.barHeight - this.baseline;
      }
      else
      {
        f6 = -this.font.getFontDescriptor(3, this.size);
        f5 = f6 + this.baseline;
      }
    byte[] arrayOfByte = getBarsInter25(str2);
    int j = 1;
    if (paramColor1 != null)
      paramPdfContentByte.setColorFill(paramColor1);
    for (int k = 0; k < arrayOfByte.length; k++)
    {
      float f7 = arrayOfByte[k] == 0 ? this.x : this.x * this.n;
      if (j != 0)
        paramPdfContentByte.rectangle(f3, f5, f7 - this.inkSpreading, this.barHeight);
      j = j == 0 ? 1 : 0;
      f3 += f7;
    }
    paramPdfContentByte.fill();
    if (this.font != null)
    {
      if (paramColor2 != null)
        paramPdfContentByte.setColorFill(paramColor2);
      paramPdfContentByte.beginText();
      paramPdfContentByte.setFontAndSize(this.font, this.size);
      paramPdfContentByte.setTextMatrix(f4, f6);
      paramPdfContentByte.showText(str1);
      paramPdfContentByte.endText();
    }
    return getBarcodeSize();
  }

  public Image createAwtImage(Color paramColor1, Color paramColor2)
  {
    int i = paramColor1.getRGB();
    int j = paramColor2.getRGB();
    Canvas localCanvas = new Canvas();
    String str = keepNumbers(this.code);
    if (this.generateChecksum)
      str = str + getChecksum(str);
    int k = str.length();
    int m = (int)this.n;
    int n = k * (3 + 2 * m) + (6 + m);
    byte[] arrayOfByte = getBarsInter25(str);
    int i1 = 1;
    int i2 = 0;
    int i3 = (int)this.barHeight;
    int[] arrayOfInt = new int[n * i3];
    for (int i4 = 0; i4 < arrayOfByte.length; i4++)
    {
      int i5 = arrayOfByte[i4] == 0 ? 1 : m;
      int i6 = j;
      if (i1 != 0)
        i6 = i;
      i1 = i1 == 0 ? 1 : 0;
      for (int i7 = 0; i7 < i5; i7++)
        arrayOfInt[(i2++)] = i6;
    }
    i4 = n;
    while (i4 < arrayOfInt.length)
    {
      System.arraycopy(arrayOfInt, 0, arrayOfInt, i4, n);
      i4 += n;
    }
    Image localImage = localCanvas.createImage(new MemoryImageSource(n, i3, arrayOfInt, 0, n));
    return localImage;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BarcodeInter25
 * JD-Core Version:    0.6.0
 */