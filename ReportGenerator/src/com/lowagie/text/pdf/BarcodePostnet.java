package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class BarcodePostnet extends Barcode
{
  private static final byte[][] BARS = { { 1, 1, 0, 0, 0 }, { 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 1 }, { 0, 0, 1, 1, 0 }, { 0, 1, 0, 0, 1 }, { 0, 1, 0, 1, 0 }, { 0, 1, 1, 0, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 1, 0 }, { 1, 0, 1, 0, 0 } };

  public BarcodePostnet()
  {
    this.n = 3.272727F;
    this.x = 1.44F;
    this.barHeight = 9.0F;
    this.size = 3.6F;
    this.codeType = 7;
  }

  public static byte[] getBarsPostnet(String paramString)
  {
    int i = 0;
    for (int j = paramString.length() - 1; j >= 0; j--)
    {
      k = paramString.charAt(j) - '0';
      i += k;
    }
    paramString = paramString + (char)((10 - i % 10) % 10 + 48);
    byte[] arrayOfByte = new byte[paramString.length() * 5 + 2];
    arrayOfByte[0] = 1;
    arrayOfByte[(arrayOfByte.length - 1)] = 1;
    for (int k = 0; k < paramString.length(); k++)
    {
      int m = paramString.charAt(k) - '0';
      System.arraycopy(BARS[m], 0, arrayOfByte, k * 5 + 1, 5);
    }
    return arrayOfByte;
  }

  public Rectangle getBarcodeSize()
  {
    float f = ((this.code.length() + 1) * 5 + 1) * this.n + this.x;
    return new Rectangle(f, this.barHeight);
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    if (paramColor1 != null)
      paramPdfContentByte.setColorFill(paramColor1);
    byte[] arrayOfByte = getBarsPostnet(this.code);
    int i = 1;
    if (this.codeType == 8)
    {
      i = 0;
      arrayOfByte[0] = 0;
      arrayOfByte[(arrayOfByte.length - 1)] = 0;
    }
    float f = 0.0F;
    for (int j = 0; j < arrayOfByte.length; j++)
    {
      paramPdfContentByte.rectangle(f, 0.0F, this.x - this.inkSpreading, arrayOfByte[j] == i ? this.barHeight : this.size);
      f += this.n;
    }
    paramPdfContentByte.fill();
    return getBarcodeSize();
  }

  public Image createAwtImage(Color paramColor1, Color paramColor2)
  {
    int i = paramColor1.getRGB();
    int j = paramColor2.getRGB();
    Canvas localCanvas = new Canvas();
    int k = (int)this.x;
    if (k <= 0)
      k = 1;
    int m = (int)this.n;
    if (m <= k)
      m = k + 1;
    int n = (int)this.size;
    if (n <= 0)
      n = 1;
    int i1 = (int)this.barHeight;
    if (i1 <= n)
      i1 = n + 1;
    int i2 = ((this.code.length() + 1) * 5 + 1) * m + k;
    int[] arrayOfInt = new int[i2 * i1];
    byte[] arrayOfByte = getBarsPostnet(this.code);
    int i3 = 1;
    if (this.codeType == 8)
    {
      i3 = 0;
      arrayOfByte[0] = 0;
      arrayOfByte[(arrayOfByte.length - 1)] = 0;
    }
    int i4 = 0;
    int i7;
    for (int i5 = 0; i5 < arrayOfByte.length; i5++)
    {
      i6 = arrayOfByte[i5] == i3 ? 1 : 0;
      for (i7 = 0; i7 < m; i7++)
        arrayOfInt[(i4 + i7)] = ((i6 != 0) && (i7 < k) ? i : j);
      i4 += m;
    }
    i5 = i2 * (i1 - n);
    int i6 = i2;
    while (i6 < i5)
    {
      System.arraycopy(arrayOfInt, 0, arrayOfInt, i6, i2);
      i6 += i2;
    }
    i4 = i5;
    for (i6 = 0; i6 < arrayOfByte.length; i6++)
    {
      for (i7 = 0; i7 < m; i7++)
        arrayOfInt[(i4 + i7)] = (i7 < k ? i : j);
      i4 += m;
    }
    i6 = i5 + i2;
    while (i6 < arrayOfInt.length)
    {
      System.arraycopy(arrayOfInt, i5, arrayOfInt, i6, i2);
      i6 += i2;
    }
    Image localImage = localCanvas.createImage(new MemoryImageSource(i2, i1, arrayOfInt, 0, i2));
    return localImage;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BarcodePostnet
 * JD-Core Version:    0.6.0
 */