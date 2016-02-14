package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class Barcode128 extends Barcode
{
  private static final byte[][] BARS = { { 2, 1, 2, 2, 2, 2 }, { 2, 2, 2, 1, 2, 2 }, { 2, 2, 2, 2, 2, 1 }, { 1, 2, 1, 2, 2, 3 }, { 1, 2, 1, 3, 2, 2 }, { 1, 3, 1, 2, 2, 2 }, { 1, 2, 2, 2, 1, 3 }, { 1, 2, 2, 3, 1, 2 }, { 1, 3, 2, 2, 1, 2 }, { 2, 2, 1, 2, 1, 3 }, { 2, 2, 1, 3, 1, 2 }, { 2, 3, 1, 2, 1, 2 }, { 1, 1, 2, 2, 3, 2 }, { 1, 2, 2, 1, 3, 2 }, { 1, 2, 2, 2, 3, 1 }, { 1, 1, 3, 2, 2, 2 }, { 1, 2, 3, 1, 2, 2 }, { 1, 2, 3, 2, 2, 1 }, { 2, 2, 3, 2, 1, 1 }, { 2, 2, 1, 1, 3, 2 }, { 2, 2, 1, 2, 3, 1 }, { 2, 1, 3, 2, 1, 2 }, { 2, 2, 3, 1, 1, 2 }, { 3, 1, 2, 1, 3, 1 }, { 3, 1, 1, 2, 2, 2 }, { 3, 2, 1, 1, 2, 2 }, { 3, 2, 1, 2, 2, 1 }, { 3, 1, 2, 2, 1, 2 }, { 3, 2, 2, 1, 1, 2 }, { 3, 2, 2, 2, 1, 1 }, { 2, 1, 2, 1, 2, 3 }, { 2, 1, 2, 3, 2, 1 }, { 2, 3, 2, 1, 2, 1 }, { 1, 1, 1, 3, 2, 3 }, { 1, 3, 1, 1, 2, 3 }, { 1, 3, 1, 3, 2, 1 }, { 1, 1, 2, 3, 1, 3 }, { 1, 3, 2, 1, 1, 3 }, { 1, 3, 2, 3, 1, 1 }, { 2, 1, 1, 3, 1, 3 }, { 2, 3, 1, 1, 1, 3 }, { 2, 3, 1, 3, 1, 1 }, { 1, 1, 2, 1, 3, 3 }, { 1, 1, 2, 3, 3, 1 }, { 1, 3, 2, 1, 3, 1 }, { 1, 1, 3, 1, 2, 3 }, { 1, 1, 3, 3, 2, 1 }, { 1, 3, 3, 1, 2, 1 }, { 3, 1, 3, 1, 2, 1 }, { 2, 1, 1, 3, 3, 1 }, { 2, 3, 1, 1, 3, 1 }, { 2, 1, 3, 1, 1, 3 }, { 2, 1, 3, 3, 1, 1 }, { 2, 1, 3, 1, 3, 1 }, { 3, 1, 1, 1, 2, 3 }, { 3, 1, 1, 3, 2, 1 }, { 3, 3, 1, 1, 2, 1 }, { 3, 1, 2, 1, 1, 3 }, { 3, 1, 2, 3, 1, 1 }, { 3, 3, 2, 1, 1, 1 }, { 3, 1, 4, 1, 1, 1 }, { 2, 2, 1, 4, 1, 1 }, { 4, 3, 1, 1, 1, 1 }, { 1, 1, 1, 2, 2, 4 }, { 1, 1, 1, 4, 2, 2 }, { 1, 2, 1, 1, 2, 4 }, { 1, 2, 1, 4, 2, 1 }, { 1, 4, 1, 1, 2, 2 }, { 1, 4, 1, 2, 2, 1 }, { 1, 1, 2, 2, 1, 4 }, { 1, 1, 2, 4, 1, 2 }, { 1, 2, 2, 1, 1, 4 }, { 1, 2, 2, 4, 1, 1 }, { 1, 4, 2, 1, 1, 2 }, { 1, 4, 2, 2, 1, 1 }, { 2, 4, 1, 2, 1, 1 }, { 2, 2, 1, 1, 1, 4 }, { 4, 1, 3, 1, 1, 1 }, { 2, 4, 1, 1, 1, 2 }, { 1, 3, 4, 1, 1, 1 }, { 1, 1, 1, 2, 4, 2 }, { 1, 2, 1, 1, 4, 2 }, { 1, 2, 1, 2, 4, 1 }, { 1, 1, 4, 2, 1, 2 }, { 1, 2, 4, 1, 1, 2 }, { 1, 2, 4, 2, 1, 1 }, { 4, 1, 1, 2, 1, 2 }, { 4, 2, 1, 1, 1, 2 }, { 4, 2, 1, 2, 1, 1 }, { 2, 1, 2, 1, 4, 1 }, { 2, 1, 4, 1, 2, 1 }, { 4, 1, 2, 1, 2, 1 }, { 1, 1, 1, 1, 4, 3 }, { 1, 1, 1, 3, 4, 1 }, { 1, 3, 1, 1, 4, 1 }, { 1, 1, 4, 1, 1, 3 }, { 1, 1, 4, 3, 1, 1 }, { 4, 1, 1, 1, 1, 3 }, { 4, 1, 1, 3, 1, 1 }, { 1, 1, 3, 1, 4, 1 }, { 1, 1, 4, 1, 3, 1 }, { 3, 1, 1, 1, 4, 1 }, { 4, 1, 1, 1, 3, 1 }, { 2, 1, 1, 4, 1, 2 }, { 2, 1, 1, 2, 1, 4 }, { 2, 1, 1, 2, 3, 2 } };
  private static final byte[] BARS_STOP = { 2, 3, 3, 1, 1, 1, 2 };
  public static final char CODE_AB_TO_C = 'c';
  public static final char CODE_AC_TO_B = 'd';
  public static final char CODE_BC_TO_A = 'e';
  public static final char FNC1_INDEX = 'f';
  public static final char START_A = 'g';
  public static final char START_B = 'h';
  public static final char START_C = 'i';
  public static final char FNC1 = 'Ê';
  public static final char DEL = 'Ã';
  public static final char FNC3 = 'Ä';
  public static final char FNC2 = 'Å';
  public static final char SHIFT = 'Æ';
  public static final char CODE_C = 'Ç';
  public static final char CODE_A = 'È';
  public static final char FNC4 = 'È';
  public static final char STARTA = 'Ë';
  public static final char STARTB = 'Ì';
  public static final char STARTC = 'Í';
  private static final IntHashtable ais = new IntHashtable();

  public Barcode128()
  {
    try
    {
      this.x = 0.8F;
      this.font = BaseFont.createFont("Helvetica", "winansi", false);
      this.size = 8.0F;
      this.baseline = this.size;
      this.barHeight = (this.size * 3.0F);
      this.textAlignment = 1;
      this.codeType = 9;
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public static String removeFNC1(String paramString)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer(i);
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      if ((c < ' ') || (c > '~'))
        continue;
      localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }

  public static String getHumanReadableUCCEAN(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    String str = String.valueOf('Ê');
    try
    {
      while (true)
      {
        if (paramString.startsWith(str))
        {
          paramString = paramString.substring(1);
          continue;
        }
        int i = 0;
        int j = 0;
        for (int k = 2; (k < 5) && (paramString.length() >= k); k++)
        {
          if ((i = ais.get(Integer.parseInt(paramString.substring(0, k)))) == 0)
            continue;
          j = k;
          break;
        }
        if (j == 0)
          break;
        localStringBuffer.append('(').append(paramString.substring(0, j)).append(')');
        paramString = paramString.substring(j);
        if (i > 0)
        {
          i -= j;
          if (paramString.length() <= i)
            break;
          localStringBuffer.append(removeFNC1(paramString.substring(0, i)));
          paramString = paramString.substring(i);
          continue;
        }
        k = paramString.indexOf('Ê');
        if (k < 0)
          break;
        localStringBuffer.append(paramString.substring(0, k));
        paramString = paramString.substring(k + 1);
      }
    }
    catch (Exception localException)
    {
    }
    localStringBuffer.append(removeFNC1(paramString));
    return localStringBuffer.toString();
  }

  static boolean isNextDigits(String paramString, int paramInt1, int paramInt2)
  {
    int i = paramString.length();
    while ((paramInt1 < i) && (paramInt2 > 0))
    {
      if (paramString.charAt(paramInt1) == 'Ê')
      {
        paramInt1++;
        continue;
      }
      int j = Math.min(2, paramInt2);
      if (paramInt1 + j > i)
        return false;
      while (j-- > 0)
      {
        int k = paramString.charAt(paramInt1++);
        if ((k < 48) || (k > 57))
          return false;
        paramInt2--;
      }
    }
    return paramInt2 == 0;
  }

  static String getPackedRawDigits(String paramString, int paramInt1, int paramInt2)
  {
    String str = "";
    int i = paramInt1;
    while (paramInt2 > 0)
    {
      if (paramString.charAt(paramInt1) == 'Ê')
      {
        str = str + 'f';
        paramInt1++;
        continue;
      }
      paramInt2 -= 2;
      int j = paramString.charAt(paramInt1++) - '0';
      int k = paramString.charAt(paramInt1++) - '0';
      str = str + (char)(j * 10 + k);
    }
    return (char)(paramInt1 - i) + str;
  }

  public static String getRawText(String paramString, boolean paramBoolean)
  {
    String str1 = "";
    int i = paramString.length();
    if (i == 0)
    {
      str1 = str1 + 'h';
      if (paramBoolean)
        str1 = str1 + 'f';
      return str1;
    }
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      j = paramString.charAt(k);
      if ((j <= 127) || (j == 202))
        continue;
      throw new RuntimeException("There are illegal characters for barcode 128 in '" + paramString + "'.");
    }
    j = paramString.charAt(0);
    k = 104;
    int m = 0;
    String str2;
    char c;
    if (isNextDigits(paramString, m, 2))
    {
      k = 105;
      str1 = str1 + k;
      if (paramBoolean)
        str1 = str1 + 'f';
      str2 = getPackedRawDigits(paramString, m, 2);
      m += str2.charAt(0);
      str1 = str1 + str2.substring(1);
    }
    else if (j < 32)
    {
      c = 'g';
      str1 = str1 + c;
      if (paramBoolean)
        str1 = str1 + 'f';
      str1 = str1 + (char)(j + 64);
      m++;
    }
    else
    {
      str1 = str1 + c;
      if (paramBoolean)
        str1 = str1 + 'f';
      if (j == 202)
        str1 = str1 + 'f';
      else
        str1 = str1 + (char)(j - 32);
      m++;
    }
    while (m < i)
      switch (c)
      {
      case 'g':
        if (isNextDigits(paramString, m, 4))
        {
          c = 'i';
          str1 = str1 + 'c';
          str2 = getPackedRawDigits(paramString, m, 4);
          m += str2.charAt(0);
          str1 = str1 + str2.substring(1);
        }
        else
        {
          j = paramString.charAt(m++);
          if (j == 202)
          {
            str1 = str1 + 'f';
          }
          else if (j > 95)
          {
            c = 'h';
            str1 = str1 + 'd';
            str1 = str1 + (char)(j - 32);
          }
          else if (j < 32)
          {
            str1 = str1 + (char)(j + 64);
          }
          else
          {
            str1 = str1 + (char)(j - 32);
          }
        }
        break;
      case 'h':
        if (isNextDigits(paramString, m, 4))
        {
          c = 'i';
          str1 = str1 + 'c';
          str2 = getPackedRawDigits(paramString, m, 4);
          m += str2.charAt(0);
          str1 = str1 + str2.substring(1);
        }
        else
        {
          j = paramString.charAt(m++);
          if (j == 202)
          {
            str1 = str1 + 'f';
          }
          else if (j < 32)
          {
            c = 'g';
            str1 = str1 + 'e';
            str1 = str1 + (char)(j + 64);
          }
          else
          {
            str1 = str1 + (char)(j - 32);
          }
        }
        break;
      case 'i':
        if (isNextDigits(paramString, m, 2))
        {
          str2 = getPackedRawDigits(paramString, m, 2);
          m += str2.charAt(0);
          str1 = str1 + str2.substring(1);
        }
        else
        {
          j = paramString.charAt(m++);
          if (j == 202)
          {
            str1 = str1 + 'f';
          }
          else if (j < 32)
          {
            c = 'g';
            str1 = str1 + 'e';
            str1 = str1 + (char)(j + 64);
          }
          else
          {
            c = 'h';
            str1 = str1 + 'd';
            str1 = str1 + (char)(j - 32);
          }
        }
      }
    return str1;
  }

  public static byte[] getBarsCode128Raw(String paramString)
  {
    int i = paramString.indexOf(65535);
    if (i >= 0)
      paramString = paramString.substring(0, i);
    int j = paramString.charAt(0);
    for (int k = 1; k < paramString.length(); k++)
      j += k * paramString.charAt(k);
    j %= 103;
    paramString = paramString + (char)j;
    byte[] arrayOfByte = new byte[(paramString.length() + 1) * 6 + 7];
    for (int m = 0; m < paramString.length(); m++)
      System.arraycopy(BARS[paramString.charAt(m)], 0, arrayOfByte, m * 6, 6);
    System.arraycopy(BARS_STOP, 0, arrayOfByte, m * 6, 7);
    return arrayOfByte;
  }

  public Rectangle getBarcodeSize()
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    String str;
    if (this.font != null)
    {
      if (this.baseline > 0.0F)
        f2 = this.baseline - this.font.getFontDescriptor(3, this.size);
      else
        f2 = -this.baseline + this.size;
      if (this.codeType == 11)
      {
        i = this.code.indexOf(65535);
        if (i < 0)
          str = "";
        else
          str = this.code.substring(i + 1);
      }
      else if (this.codeType == 10)
      {
        str = getHumanReadableUCCEAN(this.code);
      }
      else
      {
        str = removeFNC1(this.code);
      }
      f1 = this.font.getWidthPoint(this.altText != null ? this.altText : str, this.size);
    }
    if (this.codeType == 11)
    {
      i = this.code.indexOf(65535);
      if (i >= 0)
        str = this.code.substring(0, i);
      else
        str = this.code;
    }
    else
    {
      str = getRawText(this.code, this.codeType == 10);
    }
    int i = str.length();
    float f3 = (i + 2) * 11 * this.x + 2.0F * this.x;
    f3 = Math.max(f3, f1);
    float f4 = this.barHeight + f2;
    return new Rectangle(f3, f4);
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    String str1;
    if (this.codeType == 11)
    {
      int i = this.code.indexOf(65535);
      if (i < 0)
        str1 = "";
      else
        str1 = this.code.substring(i + 1);
    }
    else if (this.codeType == 10)
    {
      str1 = getHumanReadableUCCEAN(this.code);
    }
    else
    {
      str1 = removeFNC1(this.code);
    }
    float f1 = 0.0F;
    if (this.font != null)
      f1 = this.font.getWidthPoint(str1 = this.altText != null ? this.altText : str1, this.size);
    String str2;
    if (this.codeType == 11)
    {
      j = this.code.indexOf(65535);
      if (j >= 0)
        str2 = this.code.substring(0, j);
      else
        str2 = this.code;
    }
    else
    {
      str2 = getRawText(this.code, this.codeType == 10);
    }
    int j = str2.length();
    float f2 = (j + 2) * 11 * this.x + 2.0F * this.x;
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
    byte[] arrayOfByte = getBarsCode128Raw(str2);
    int k = 1;
    if (paramColor1 != null)
      paramPdfContentByte.setColorFill(paramColor1);
    for (int m = 0; m < arrayOfByte.length; m++)
    {
      float f7 = arrayOfByte[m] * this.x;
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
    String str;
    if (this.codeType == 11)
    {
      k = this.code.indexOf(65535);
      if (k >= 0)
        str = this.code.substring(0, k);
      else
        str = this.code;
    }
    else
    {
      str = getRawText(this.code, this.codeType == 10);
    }
    int k = str.length();
    int m = (k + 2) * 11 + 2;
    byte[] arrayOfByte = getBarsCode128Raw(str);
    int n = 1;
    int i1 = 0;
    int i2 = (int)this.barHeight;
    int[] arrayOfInt = new int[m * i2];
    for (int i3 = 0; i3 < arrayOfByte.length; i3++)
    {
      int i4 = arrayOfByte[i3];
      int i5 = j;
      if (n != 0)
        i5 = i;
      n = n == 0 ? 1 : 0;
      for (int i6 = 0; i6 < i4; i6++)
        arrayOfInt[(i1++)] = i5;
    }
    i3 = m;
    while (i3 < arrayOfInt.length)
    {
      System.arraycopy(arrayOfInt, 0, arrayOfInt, i3, m);
      i3 += m;
    }
    Image localImage = localCanvas.createImage(new MemoryImageSource(m, i2, arrayOfInt, 0, m));
    return localImage;
  }

  public void setCode(String paramString)
  {
    if ((getCodeType() == 10) && (paramString.startsWith("(")))
    {
      int i = 0;
      String str1 = "";
      while (i >= 0)
      {
        int j = paramString.indexOf(')', i);
        if (j < 0)
          throw new IllegalArgumentException("Badly formed UCC string: " + paramString);
        String str2 = paramString.substring(i + 1, j);
        if (str2.length() < 2)
          throw new IllegalArgumentException("AI too short: (" + str2 + ")");
        int k = Integer.parseInt(str2);
        int m = ais.get(k);
        if (m == 0)
          throw new IllegalArgumentException("AI not found: (" + str2 + ")");
        str2 = String.valueOf(k);
        if (str2.length() == 1)
          str2 = "0" + str2;
        i = paramString.indexOf('(', j);
        int n = i < 0 ? paramString.length() : i;
        str1 = str1 + str2 + paramString.substring(j + 1, n);
        if (m < 0)
        {
          if (i < 0)
            continue;
          str1 = str1 + 'Ê';
          continue;
        }
        if (n - j - 1 + str2.length() == m)
          continue;
        throw new IllegalArgumentException("Invalid AI length: (" + str2 + ")");
      }
      super.setCode(str1);
    }
    else
    {
      super.setCode(paramString);
    }
  }

  static
  {
    ais.put(0, 20);
    ais.put(1, 16);
    ais.put(2, 16);
    ais.put(10, -1);
    ais.put(11, 9);
    ais.put(12, 8);
    ais.put(13, 8);
    ais.put(15, 8);
    ais.put(17, 8);
    ais.put(20, 4);
    ais.put(21, -1);
    ais.put(22, -1);
    ais.put(23, -1);
    ais.put(240, -1);
    ais.put(241, -1);
    ais.put(250, -1);
    ais.put(251, -1);
    ais.put(252, -1);
    ais.put(30, -1);
    for (int i = 3100; i < 3700; i++)
      ais.put(i, 10);
    ais.put(37, -1);
    for (i = 3900; i < 3940; i++)
      ais.put(i, -1);
    ais.put(400, -1);
    ais.put(401, -1);
    ais.put(402, 20);
    ais.put(403, -1);
    for (i = 410; i < 416; i++)
      ais.put(i, 16);
    ais.put(420, -1);
    ais.put(421, -1);
    ais.put(422, 6);
    ais.put(423, -1);
    ais.put(424, 6);
    ais.put(425, 6);
    ais.put(426, 6);
    ais.put(7001, 17);
    ais.put(7002, -1);
    for (i = 7030; i < 7040; i++)
      ais.put(i, -1);
    ais.put(8001, 18);
    ais.put(8002, -1);
    ais.put(8003, -1);
    ais.put(8004, -1);
    ais.put(8005, 10);
    ais.put(8006, 22);
    ais.put(8007, -1);
    ais.put(8008, -1);
    ais.put(8018, 22);
    ais.put(8020, -1);
    ais.put(8100, 10);
    ais.put(8101, 14);
    ais.put(8102, 6);
    for (i = 90; i < 100; i++)
      ais.put(i, -1);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.Barcode128
 * JD-Core Version:    0.6.0
 */