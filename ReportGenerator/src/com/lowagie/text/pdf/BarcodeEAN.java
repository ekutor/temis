package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

public class BarcodeEAN extends Barcode
{
  private static final int[] GUARD_EMPTY = new int[0];
  private static final int[] GUARD_UPCA = { 0, 2, 4, 6, 28, 30, 52, 54, 56, 58 };
  private static final int[] GUARD_EAN13 = { 0, 2, 28, 30, 56, 58 };
  private static final int[] GUARD_EAN8 = { 0, 2, 20, 22, 40, 42 };
  private static final int[] GUARD_UPCE = { 0, 2, 28, 30, 32 };
  private static final float[] TEXTPOS_EAN13 = { 6.5F, 13.5F, 20.5F, 27.5F, 34.5F, 41.5F, 53.5F, 60.5F, 67.5F, 74.5F, 81.5F, 88.5F };
  private static final float[] TEXTPOS_EAN8 = { 6.5F, 13.5F, 20.5F, 27.5F, 39.5F, 46.5F, 53.5F, 60.5F };
  private static final byte[][] BARS = { { 3, 2, 1, 1 }, { 2, 2, 2, 1 }, { 2, 1, 2, 2 }, { 1, 4, 1, 1 }, { 1, 1, 3, 2 }, { 1, 2, 3, 1 }, { 1, 1, 1, 4 }, { 1, 3, 1, 2 }, { 1, 2, 1, 3 }, { 3, 1, 1, 2 } };
  private static final int TOTALBARS_EAN13 = 59;
  private static final int TOTALBARS_EAN8 = 43;
  private static final int TOTALBARS_UPCE = 33;
  private static final int TOTALBARS_SUPP2 = 13;
  private static final int TOTALBARS_SUPP5 = 31;
  private static final int ODD = 0;
  private static final int EVEN = 1;
  private static final byte[][] PARITY13 = { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 1, 1 }, { 0, 0, 1, 1, 0, 1 }, { 0, 0, 1, 1, 1, 0 }, { 0, 1, 0, 0, 1, 1 }, { 0, 1, 1, 0, 0, 1 }, { 0, 1, 1, 1, 0, 0 }, { 0, 1, 0, 1, 0, 1 }, { 0, 1, 0, 1, 1, 0 }, { 0, 1, 1, 0, 1, 0 } };
  private static final byte[][] PARITY2 = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
  private static final byte[][] PARITY5 = { { 1, 1, 0, 0, 0 }, { 1, 0, 1, 0, 0 }, { 1, 0, 0, 1, 0 }, { 1, 0, 0, 0, 1 }, { 0, 1, 1, 0, 0 }, { 0, 0, 1, 1, 0 }, { 0, 0, 0, 1, 1 }, { 0, 1, 0, 1, 0 }, { 0, 1, 0, 0, 1 }, { 0, 0, 1, 0, 1 } };
  private static final byte[][] PARITYE = { { 1, 1, 1, 0, 0, 0 }, { 1, 1, 0, 1, 0, 0 }, { 1, 1, 0, 0, 1, 0 }, { 1, 1, 0, 0, 0, 1 }, { 1, 0, 1, 1, 0, 0 }, { 1, 0, 0, 1, 1, 0 }, { 1, 0, 0, 0, 1, 1 }, { 1, 0, 1, 0, 1, 0 }, { 1, 0, 1, 0, 0, 1 }, { 1, 0, 0, 1, 0, 1 } };

  public BarcodeEAN()
  {
    try
    {
      this.x = 0.8F;
      this.font = BaseFont.createFont("Helvetica", "winansi", false);
      this.size = 8.0F;
      this.baseline = this.size;
      this.barHeight = (this.size * 3.0F);
      this.guardBars = true;
      this.codeType = 1;
      this.code = "";
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public static int calculateEANParity(String paramString)
  {
    int i = 3;
    int j = 0;
    for (int k = paramString.length() - 1; k >= 0; k--)
    {
      int m = paramString.charAt(k) - '0';
      j += i * m;
      i ^= 2;
    }
    return (10 - j % 10) % 10;
  }

  public static String convertUPCAtoUPCE(String paramString)
  {
    if ((paramString.length() != 12) || ((!paramString.startsWith("0")) && (!paramString.startsWith("1"))))
      return null;
    if ((paramString.substring(3, 6).equals("000")) || (paramString.substring(3, 6).equals("100")) || (paramString.substring(3, 6).equals("200")))
    {
      if (paramString.substring(6, 8).equals("00"))
        return paramString.substring(0, 1) + paramString.substring(1, 3) + paramString.substring(8, 11) + paramString.substring(3, 4) + paramString.substring(11);
    }
    else if (paramString.substring(4, 6).equals("00"))
    {
      if (paramString.substring(6, 9).equals("000"))
        return paramString.substring(0, 1) + paramString.substring(1, 4) + paramString.substring(9, 11) + "3" + paramString.substring(11);
    }
    else if (paramString.substring(5, 6).equals("0"))
    {
      if (paramString.substring(6, 10).equals("0000"))
        return paramString.substring(0, 1) + paramString.substring(1, 5) + paramString.substring(10, 11) + "4" + paramString.substring(11);
    }
    else if ((paramString.charAt(10) >= '5') && (paramString.substring(6, 10).equals("0000")))
      return paramString.substring(0, 1) + paramString.substring(1, 6) + paramString.substring(10, 11) + paramString.substring(11);
    return null;
  }

  public static byte[] getBarsEAN13(String paramString)
  {
    int[] arrayOfInt = new int[paramString.length()];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = (paramString.charAt(i) - '0');
    byte[] arrayOfByte1 = new byte[59];
    int j = 0;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    byte[] arrayOfByte2 = PARITY13[arrayOfInt[0]];
    int m;
    byte[] arrayOfByte3;
    for (int k = 0; k < arrayOfByte2.length; k++)
    {
      m = arrayOfInt[(k + 1)];
      arrayOfByte3 = BARS[m];
      if (arrayOfByte2[k] == 0)
      {
        arrayOfByte1[(j++)] = arrayOfByte3[0];
        arrayOfByte1[(j++)] = arrayOfByte3[1];
        arrayOfByte1[(j++)] = arrayOfByte3[2];
        arrayOfByte1[(j++)] = arrayOfByte3[3];
      }
      else
      {
        arrayOfByte1[(j++)] = arrayOfByte3[3];
        arrayOfByte1[(j++)] = arrayOfByte3[2];
        arrayOfByte1[(j++)] = arrayOfByte3[1];
        arrayOfByte1[(j++)] = arrayOfByte3[0];
      }
    }
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    for (k = 7; k < 13; k++)
    {
      m = arrayOfInt[k];
      arrayOfByte3 = BARS[m];
      arrayOfByte1[(j++)] = arrayOfByte3[0];
      arrayOfByte1[(j++)] = arrayOfByte3[1];
      arrayOfByte1[(j++)] = arrayOfByte3[2];
      arrayOfByte1[(j++)] = arrayOfByte3[3];
    }
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    return arrayOfByte1;
  }

  public static byte[] getBarsEAN8(String paramString)
  {
    int[] arrayOfInt = new int[paramString.length()];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = (paramString.charAt(i) - '0');
    byte[] arrayOfByte1 = new byte[43];
    int j = 0;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    int m;
    byte[] arrayOfByte2;
    for (int k = 0; k < 4; k++)
    {
      m = arrayOfInt[k];
      arrayOfByte2 = BARS[m];
      arrayOfByte1[(j++)] = arrayOfByte2[0];
      arrayOfByte1[(j++)] = arrayOfByte2[1];
      arrayOfByte1[(j++)] = arrayOfByte2[2];
      arrayOfByte1[(j++)] = arrayOfByte2[3];
    }
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    for (k = 4; k < 8; k++)
    {
      m = arrayOfInt[k];
      arrayOfByte2 = BARS[m];
      arrayOfByte1[(j++)] = arrayOfByte2[0];
      arrayOfByte1[(j++)] = arrayOfByte2[1];
      arrayOfByte1[(j++)] = arrayOfByte2[2];
      arrayOfByte1[(j++)] = arrayOfByte2[3];
    }
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    return arrayOfByte1;
  }

  public static byte[] getBarsUPCE(String paramString)
  {
    int[] arrayOfInt = new int[paramString.length()];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = (paramString.charAt(i) - '0');
    byte[] arrayOfByte1 = new byte[33];
    int j = arrayOfInt[0] != 0 ? 1 : 0;
    int k = 0;
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    byte[] arrayOfByte2 = PARITYE[arrayOfInt[(arrayOfInt.length - 1)]];
    for (int m = 1; m < arrayOfInt.length - 1; m++)
    {
      int n = arrayOfInt[m];
      byte[] arrayOfByte3 = BARS[n];
      if (arrayOfByte2[(m - 1)] == (j != 0 ? 1 : 0))
      {
        arrayOfByte1[(k++)] = arrayOfByte3[0];
        arrayOfByte1[(k++)] = arrayOfByte3[1];
        arrayOfByte1[(k++)] = arrayOfByte3[2];
        arrayOfByte1[(k++)] = arrayOfByte3[3];
      }
      else
      {
        arrayOfByte1[(k++)] = arrayOfByte3[3];
        arrayOfByte1[(k++)] = arrayOfByte3[2];
        arrayOfByte1[(k++)] = arrayOfByte3[1];
        arrayOfByte1[(k++)] = arrayOfByte3[0];
      }
    }
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    arrayOfByte1[(k++)] = 1;
    return arrayOfByte1;
  }

  public static byte[] getBarsSupplemental2(String paramString)
  {
    int[] arrayOfInt = new int[2];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = (paramString.charAt(i) - '0');
    byte[] arrayOfByte1 = new byte[13];
    int j = 0;
    int k = (arrayOfInt[0] * 10 + arrayOfInt[1]) % 4;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 2;
    byte[] arrayOfByte2 = PARITY2[k];
    for (int m = 0; m < arrayOfByte2.length; m++)
    {
      if (m == 1)
      {
        arrayOfByte1[(j++)] = 1;
        arrayOfByte1[(j++)] = 1;
      }
      int n = arrayOfInt[m];
      byte[] arrayOfByte3 = BARS[n];
      if (arrayOfByte2[m] == 0)
      {
        arrayOfByte1[(j++)] = arrayOfByte3[0];
        arrayOfByte1[(j++)] = arrayOfByte3[1];
        arrayOfByte1[(j++)] = arrayOfByte3[2];
        arrayOfByte1[(j++)] = arrayOfByte3[3];
      }
      else
      {
        arrayOfByte1[(j++)] = arrayOfByte3[3];
        arrayOfByte1[(j++)] = arrayOfByte3[2];
        arrayOfByte1[(j++)] = arrayOfByte3[1];
        arrayOfByte1[(j++)] = arrayOfByte3[0];
      }
    }
    return arrayOfByte1;
  }

  public static byte[] getBarsSupplemental5(String paramString)
  {
    int[] arrayOfInt = new int[5];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = (paramString.charAt(i) - '0');
    byte[] arrayOfByte1 = new byte[31];
    int j = 0;
    int k = ((arrayOfInt[0] + arrayOfInt[2] + arrayOfInt[4]) * 3 + (arrayOfInt[1] + arrayOfInt[3]) * 9) % 10;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 1;
    arrayOfByte1[(j++)] = 2;
    byte[] arrayOfByte2 = PARITY5[k];
    for (int m = 0; m < arrayOfByte2.length; m++)
    {
      if (m != 0)
      {
        arrayOfByte1[(j++)] = 1;
        arrayOfByte1[(j++)] = 1;
      }
      int n = arrayOfInt[m];
      byte[] arrayOfByte3 = BARS[n];
      if (arrayOfByte2[m] == 0)
      {
        arrayOfByte1[(j++)] = arrayOfByte3[0];
        arrayOfByte1[(j++)] = arrayOfByte3[1];
        arrayOfByte1[(j++)] = arrayOfByte3[2];
        arrayOfByte1[(j++)] = arrayOfByte3[3];
      }
      else
      {
        arrayOfByte1[(j++)] = arrayOfByte3[3];
        arrayOfByte1[(j++)] = arrayOfByte3[2];
        arrayOfByte1[(j++)] = arrayOfByte3[1];
        arrayOfByte1[(j++)] = arrayOfByte3[0];
      }
    }
    return arrayOfByte1;
  }

  public Rectangle getBarcodeSize()
  {
    float f1 = 0.0F;
    float f2 = this.barHeight;
    if (this.font != null)
      if (this.baseline <= 0.0F)
        f2 += -this.baseline + this.size;
      else
        f2 += this.baseline - this.font.getFontDescriptor(3, this.size);
    switch (this.codeType)
    {
    case 1:
      f1 = this.x * 95.0F;
      if (this.font == null)
        break;
      f1 += this.font.getWidthPoint(this.code.charAt(0), this.size);
      break;
    case 2:
      f1 = this.x * 67.0F;
      break;
    case 3:
      f1 = this.x * 95.0F;
      if (this.font == null)
        break;
      f1 += this.font.getWidthPoint(this.code.charAt(0), this.size) + this.font.getWidthPoint(this.code.charAt(11), this.size);
      break;
    case 4:
      f1 = this.x * 51.0F;
      if (this.font == null)
        break;
      f1 += this.font.getWidthPoint(this.code.charAt(0), this.size) + this.font.getWidthPoint(this.code.charAt(7), this.size);
      break;
    case 5:
      f1 = this.x * 20.0F;
      break;
    case 6:
      f1 = this.x * 47.0F;
      break;
    default:
      throw new RuntimeException("Invalid code type.");
    }
    return new Rectangle(f1, f2);
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    Rectangle localRectangle = getBarcodeSize();
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    if (this.font != null)
      if (this.baseline <= 0.0F)
      {
        f3 = this.barHeight - this.baseline;
      }
      else
      {
        f3 = -this.font.getFontDescriptor(3, this.size);
        f2 = f3 + this.baseline;
      }
    switch (this.codeType)
    {
    case 1:
    case 3:
    case 4:
      if (this.font == null)
        break;
      f1 += this.font.getWidthPoint(this.code.charAt(0), this.size);
    case 2:
    }
    byte[] arrayOfByte = null;
    int[] arrayOfInt = GUARD_EMPTY;
    switch (this.codeType)
    {
    case 1:
      arrayOfByte = getBarsEAN13(this.code);
      arrayOfInt = GUARD_EAN13;
      break;
    case 2:
      arrayOfByte = getBarsEAN8(this.code);
      arrayOfInt = GUARD_EAN8;
      break;
    case 3:
      arrayOfByte = getBarsEAN13("0" + this.code);
      arrayOfInt = GUARD_UPCA;
      break;
    case 4:
      arrayOfByte = getBarsUPCE(this.code);
      arrayOfInt = GUARD_UPCE;
      break;
    case 5:
      arrayOfByte = getBarsSupplemental2(this.code);
      break;
    case 6:
      arrayOfByte = getBarsSupplemental5(this.code);
    }
    float f4 = f1;
    int i = 1;
    float f5 = 0.0F;
    if ((this.font != null) && (this.baseline > 0.0F) && (this.guardBars))
      f5 = this.baseline / 2.0F;
    if (paramColor1 != null)
      paramPdfContentByte.setColorFill(paramColor1);
    for (int j = 0; j < arrayOfByte.length; j++)
    {
      float f6 = arrayOfByte[j] * this.x;
      if (i != 0)
        if (Arrays.binarySearch(arrayOfInt, j) >= 0)
          paramPdfContentByte.rectangle(f1, f2 - f5, f6 - this.inkSpreading, this.barHeight + f5);
        else
          paramPdfContentByte.rectangle(f1, f2, f6 - this.inkSpreading, this.barHeight);
      i = i == 0 ? 1 : 0;
      f1 += f6;
    }
    paramPdfContentByte.fill();
    if (this.font != null)
    {
      if (paramColor2 != null)
        paramPdfContentByte.setColorFill(paramColor2);
      paramPdfContentByte.beginText();
      paramPdfContentByte.setFontAndSize(this.font, this.size);
      switch (this.codeType)
      {
      case 1:
        paramPdfContentByte.setTextMatrix(0.0F, f3);
        paramPdfContentByte.showText(this.code.substring(0, 1));
        j = 1;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
        while (j < 13)
        {
          String str = this.code.substring(j, j + 1);
          float f7 = this.font.getWidthPoint(str, this.size);
          float f8 = f4 + TEXTPOS_EAN13[(j - 1)] * this.x - f7 / 2.0F;
          paramPdfContentByte.setTextMatrix(f8, f3);
          paramPdfContentByte.showText(str);
          j++;
          continue;
          j = 0;
          while (j < 8)
          {
            str = this.code.substring(j, j + 1);
            f7 = this.font.getWidthPoint(str, this.size);
            f8 = TEXTPOS_EAN8[j] * this.x - f7 / 2.0F;
            paramPdfContentByte.setTextMatrix(f8, f3);
            paramPdfContentByte.showText(str);
            j++;
            continue;
            paramPdfContentByte.setTextMatrix(0.0F, f3);
            paramPdfContentByte.showText(this.code.substring(0, 1));
            for (j = 1; j < 11; j++)
            {
              str = this.code.substring(j, j + 1);
              f7 = this.font.getWidthPoint(str, this.size);
              f8 = f4 + TEXTPOS_EAN13[j] * this.x - f7 / 2.0F;
              paramPdfContentByte.setTextMatrix(f8, f3);
              paramPdfContentByte.showText(str);
            }
            paramPdfContentByte.setTextMatrix(f4 + this.x * 95.0F, f3);
            paramPdfContentByte.showText(this.code.substring(11, 12));
            break;
            paramPdfContentByte.setTextMatrix(0.0F, f3);
            paramPdfContentByte.showText(this.code.substring(0, 1));
            for (j = 1; j < 7; j++)
            {
              str = this.code.substring(j, j + 1);
              f7 = this.font.getWidthPoint(str, this.size);
              f8 = f4 + TEXTPOS_EAN13[(j - 1)] * this.x - f7 / 2.0F;
              paramPdfContentByte.setTextMatrix(f8, f3);
              paramPdfContentByte.showText(str);
            }
            paramPdfContentByte.setTextMatrix(f4 + this.x * 51.0F, f3);
            paramPdfContentByte.showText(this.code.substring(7, 8));
            break;
            for (j = 0; j < this.code.length(); j++)
            {
              str = this.code.substring(j, j + 1);
              f7 = this.font.getWidthPoint(str, this.size);
              f8 = (7.5F + 9 * j) * this.x - f7 / 2.0F;
              paramPdfContentByte.setTextMatrix(f8, f3);
              paramPdfContentByte.showText(str);
            }
          }
        }
      }
      paramPdfContentByte.endText();
    }
    return localRectangle;
  }

  public Image createAwtImage(Color paramColor1, Color paramColor2)
  {
    int i = paramColor1.getRGB();
    int j = paramColor2.getRGB();
    Canvas localCanvas = new Canvas();
    int k = 0;
    byte[] arrayOfByte = null;
    switch (this.codeType)
    {
    case 1:
      arrayOfByte = getBarsEAN13(this.code);
      k = 95;
      break;
    case 2:
      arrayOfByte = getBarsEAN8(this.code);
      k = 67;
      break;
    case 3:
      arrayOfByte = getBarsEAN13("0" + this.code);
      k = 95;
      break;
    case 4:
      arrayOfByte = getBarsUPCE(this.code);
      k = 51;
      break;
    case 5:
      arrayOfByte = getBarsSupplemental2(this.code);
      k = 20;
      break;
    case 6:
      arrayOfByte = getBarsSupplemental5(this.code);
      k = 47;
      break;
    default:
      throw new RuntimeException("Invalid code type.");
    }
    int m = 1;
    int n = 0;
    int i1 = (int)this.barHeight;
    int[] arrayOfInt = new int[k * i1];
    for (int i2 = 0; i2 < arrayOfByte.length; i2++)
    {
      int i3 = arrayOfByte[i2];
      int i4 = j;
      if (m != 0)
        i4 = i;
      m = m == 0 ? 1 : 0;
      for (int i5 = 0; i5 < i3; i5++)
        arrayOfInt[(n++)] = i4;
    }
    i2 = k;
    while (i2 < arrayOfInt.length)
    {
      System.arraycopy(arrayOfInt, 0, arrayOfInt, i2, k);
      i2 += k;
    }
    Image localImage = localCanvas.createImage(new MemoryImageSource(k, i1, arrayOfInt, 0, k));
    return localImage;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BarcodeEAN
 * JD-Core Version:    0.6.0
 */