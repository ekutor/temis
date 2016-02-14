package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class BarcodeCodabar extends Barcode
{
  private static final byte[][] BARS = { { 0, 0, 0, 0, 0, 1, 1 }, { 0, 0, 0, 0, 1, 1, 0 }, { 0, 0, 0, 1, 0, 0, 1 }, { 1, 1, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0, 1, 0 }, { 1, 0, 0, 0, 0, 1, 0 }, { 0, 1, 0, 0, 0, 0, 1 }, { 0, 1, 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 1, 1, 0, 0 }, { 0, 0, 1, 1, 0, 0, 0 }, { 1, 0, 0, 0, 1, 0, 1 }, { 1, 0, 1, 0, 0, 0, 1 }, { 1, 0, 1, 0, 1, 0, 0 }, { 0, 0, 1, 0, 1, 0, 1 }, { 0, 0, 1, 1, 0, 1, 0 }, { 0, 1, 0, 1, 0, 0, 1 }, { 0, 0, 0, 1, 0, 1, 1 }, { 0, 0, 0, 1, 1, 1, 0 } };
  private static final String CHARS = "0123456789-$:/.+ABCD";
  private static final int START_STOP_IDX = 16;

  public BarcodeCodabar()
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
      this.startStopText = false;
      this.codeType = 12;
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public static byte[] getBarsCodabar(String paramString)
  {
    paramString = paramString.toUpperCase();
    int i = paramString.length();
    if (i < 2)
      throw new IllegalArgumentException("Codabar must have at least a start and stop character.");
    if (("0123456789-$:/.+ABCD".indexOf(paramString.charAt(0)) < 16) || ("0123456789-$:/.+ABCD".indexOf(paramString.charAt(i - 1)) < 16))
      throw new IllegalArgumentException("Codabar must have one of 'ABCD' as start/stop character.");
    byte[] arrayOfByte = new byte[paramString.length() * 8 - 1];
    for (int j = 0; j < i; j++)
    {
      int k = "0123456789-$:/.+ABCD".indexOf(paramString.charAt(j));
      if ((k >= 16) && (j > 0) && (j < i - 1))
        throw new IllegalArgumentException("In codabar, start/stop characters are only allowed at the extremes.");
      if (k < 0)
        throw new IllegalArgumentException("The character '" + paramString.charAt(j) + "' is illegal in codabar.");
      System.arraycopy(BARS[k], 0, arrayOfByte, j * 8, 7);
    }
    return arrayOfByte;
  }

  public static String calculateChecksum(String paramString)
  {
    if (paramString.length() < 2)
      return paramString;
    String str = paramString.toUpperCase();
    int i = 0;
    int j = str.length();
    for (int k = 0; k < j; k++)
      i += "0123456789-$:/.+ABCD".indexOf(str.charAt(k));
    i = (i + 15) / 16 * 16 - i;
    return paramString.substring(0, j - 1) + "0123456789-$:/.+ABCD".charAt(i) + paramString.substring(j - 1);
  }

  public Rectangle getBarcodeSize()
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    String str = this.code;
    if ((this.generateChecksum) && (this.checksumText))
      str = calculateChecksum(this.code);
    if (!this.startStopText)
      str = str.substring(1, str.length() - 1);
    if (this.font != null)
    {
      if (this.baseline > 0.0F)
        f2 = this.baseline - this.font.getFontDescriptor(3, this.size);
      else
        f2 = -this.baseline + this.size;
      f1 = this.font.getWidthPoint(this.altText != null ? this.altText : str, this.size);
    }
    str = this.code;
    if (this.generateChecksum)
      str = calculateChecksum(this.code);
    byte[] arrayOfByte = getBarsCodabar(str);
    int i = 0;
    for (int j = 0; j < arrayOfByte.length; j++)
      i += arrayOfByte[j];
    j = arrayOfByte.length - i;
    float f3 = this.x * (j + i * this.n);
    f3 = Math.max(f3, f1);
    float f4 = this.barHeight + f2;
    return new Rectangle(f3, f4);
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    String str = this.code;
    if ((this.generateChecksum) && (this.checksumText))
      str = calculateChecksum(this.code);
    if (!this.startStopText)
      str = str.substring(1, str.length() - 1);
    float f1 = 0.0F;
    if (this.font != null)
      f1 = this.font.getWidthPoint(str = this.altText != null ? this.altText : str, this.size);
    byte[] arrayOfByte = getBarsCodabar(this.generateChecksum ? calculateChecksum(this.code) : this.code);
    int i = 0;
    for (int j = 0; j < arrayOfByte.length; j++)
      i += arrayOfByte[j];
    j = arrayOfByte.length - i;
    float f2 = this.x * (j + i * this.n);
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
    int k = 1;
    if (paramColor1 != null)
      paramPdfContentByte.setColorFill(paramColor1);
    for (int m = 0; m < arrayOfByte.length; m++)
    {
      float f7 = arrayOfByte[m] == 0 ? this.x : this.x * this.n;
      if (k != 0)
        paramPdfContentByte.rectangle(f3, f5, f7 - this.inkSpreading, this.barHeight);
      k = k == 0 ? 1 : 0;
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
      paramPdfContentByte.showText(str);
      paramPdfContentByte.endText();
    }
    return getBarcodeSize();
  }

  public Image createAwtImage(Color paramColor1, Color paramColor2)
  {
    int i = paramColor1.getRGB();
    int j = paramColor2.getRGB();
    Canvas localCanvas = new Canvas();
    String str = this.code;
    if ((this.generateChecksum) && (this.checksumText))
      str = calculateChecksum(this.code);
    if (!this.startStopText)
      str = str.substring(1, str.length() - 1);
    byte[] arrayOfByte = getBarsCodabar(this.generateChecksum ? calculateChecksum(this.code) : this.code);
    int k = 0;
    for (int m = 0; m < arrayOfByte.length; m++)
      k += arrayOfByte[m];
    m = arrayOfByte.length - k;
    int n = m + k * (int)this.n;
    int i1 = 1;
    int i2 = 0;
    int i3 = (int)this.barHeight;
    int[] arrayOfInt = new int[n * i3];
    for (int i4 = 0; i4 < arrayOfByte.length; i4++)
    {
      int i5 = arrayOfByte[i4] == 0 ? 1 : (int)this.n;
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
 * Qualified Name:     com.lowagie.text.pdf.BarcodeCodabar
 * JD-Core Version:    0.6.0
 */