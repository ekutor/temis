package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class Barcode39 extends Barcode
{
  private static final byte[][] BARS = { { 0, 0, 0, 1, 1, 0, 1, 0, 0 }, { 1, 0, 0, 1, 0, 0, 0, 0, 1 }, { 0, 0, 1, 1, 0, 0, 0, 0, 1 }, { 1, 0, 1, 1, 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 1, 0, 0, 0, 1 }, { 1, 0, 0, 1, 1, 0, 0, 0, 0 }, { 0, 0, 1, 1, 1, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 1, 0, 1 }, { 1, 0, 0, 1, 0, 0, 1, 0, 0 }, { 0, 0, 1, 1, 0, 0, 1, 0, 0 }, { 1, 0, 0, 0, 0, 1, 0, 0, 1 }, { 0, 0, 1, 0, 0, 1, 0, 0, 1 }, { 1, 0, 1, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 1, 1, 0, 0, 1 }, { 1, 0, 0, 0, 1, 1, 0, 0, 0 }, { 0, 0, 1, 0, 1, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 1, 1, 0, 1 }, { 1, 0, 0, 0, 0, 1, 1, 0, 0 }, { 0, 0, 1, 0, 0, 1, 1, 0, 0 }, { 0, 0, 0, 0, 1, 1, 1, 0, 0 }, { 1, 0, 0, 0, 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 0, 0, 0, 1, 1 }, { 1, 0, 1, 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1, 0, 0, 1, 1 }, { 1, 0, 0, 0, 1, 0, 0, 1, 0 }, { 0, 0, 1, 0, 1, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 1, 1, 0 }, { 0, 0, 1, 0, 0, 0, 1, 1, 0 }, { 0, 0, 0, 0, 1, 0, 1, 1, 0 }, { 1, 1, 0, 0, 0, 0, 0, 0, 1 }, { 0, 1, 1, 0, 0, 0, 0, 0, 1 }, { 1, 1, 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 1, 0, 0, 0, 1 }, { 1, 1, 0, 0, 1, 0, 0, 0, 0 }, { 0, 1, 1, 0, 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 0, 1, 0, 1 }, { 1, 1, 0, 0, 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0, 0, 1, 0, 0 }, { 0, 1, 0, 1, 0, 1, 0, 0, 0 }, { 0, 1, 0, 1, 0, 0, 0, 1, 0 }, { 0, 1, 0, 0, 0, 1, 0, 1, 0 }, { 0, 0, 0, 1, 0, 1, 0, 1, 0 }, { 0, 1, 0, 0, 1, 0, 1, 0, 0 } };
  private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*";
  private static final String EXTENDED = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T";

  public Barcode39()
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
      this.startStopText = true;
      this.extended = false;
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public static byte[] getBarsCode39(String paramString)
  {
    paramString = "*" + paramString + "*";
    byte[] arrayOfByte = new byte[paramString.length() * 10 - 1];
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".indexOf(paramString.charAt(i));
      if (j < 0)
        throw new IllegalArgumentException("The character '" + paramString.charAt(i) + "' is illegal in code 39.");
      System.arraycopy(BARS[j], 0, arrayOfByte, i * 10, 9);
    }
    return arrayOfByte;
  }

  public static String getCode39Ex(String paramString)
  {
    String str = "";
    for (int i = 0; i < paramString.length(); i++)
    {
      char c1 = paramString.charAt(i);
      if (c1 > '')
        throw new IllegalArgumentException("The character '" + c1 + "' is illegal in code 39 extended.");
      char c2 = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T".charAt(c1 * '\002');
      char c3 = "%U$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V A B C D E F G H I J K L M N O P Q R S T U V W X Y Z%K%L%M%N%O%W+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z%P%Q%R%S%T".charAt(c1 * '\002' + 1);
      if (c2 != ' ')
        str = str + c2;
      str = str + c3;
    }
    return str;
  }

  static char getChecksum(String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".indexOf(paramString.charAt(j));
      if (k < 0)
        throw new IllegalArgumentException("The character '" + paramString.charAt(j) + "' is illegal in code 39.");
      i += k;
    }
    return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*".charAt(i % 43);
  }

  public Rectangle getBarcodeSize()
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    String str1 = this.code;
    if (this.extended)
      str1 = getCode39Ex(this.code);
    if (this.font != null)
    {
      if (this.baseline > 0.0F)
        f2 = this.baseline - this.font.getFontDescriptor(3, this.size);
      else
        f2 = -this.baseline + this.size;
      String str2 = this.code;
      if ((this.generateChecksum) && (this.checksumText))
        str2 = str2 + getChecksum(str1);
      if (this.startStopText)
        str2 = "*" + str2 + "*";
      f1 = this.font.getWidthPoint(this.altText != null ? this.altText : str2, this.size);
    }
    int i = str1.length() + 2;
    if (this.generateChecksum)
      i++;
    float f3 = i * (6.0F * this.x + 3.0F * this.x * this.n) + (i - 1) * this.x;
    f3 = Math.max(f3, f1);
    float f4 = this.barHeight + f2;
    return new Rectangle(f3, f4);
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    String str1 = this.code;
    float f1 = 0.0F;
    String str2 = this.code;
    if (this.extended)
      str2 = getCode39Ex(this.code);
    if (this.font != null)
    {
      if ((this.generateChecksum) && (this.checksumText))
        str1 = str1 + getChecksum(str2);
      if (this.startStopText)
        str1 = "*" + str1 + "*";
      f1 = this.font.getWidthPoint(str1 = this.altText != null ? this.altText : str1, this.size);
    }
    if (this.generateChecksum)
      str2 = str2 + getChecksum(str2);
    int i = str2.length() + 2;
    float f2 = i * (6.0F * this.x + 3.0F * this.x * this.n) + (i - 1) * this.x;
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
    byte[] arrayOfByte = getBarsCode39(str2);
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
    String str = this.code;
    if (this.extended)
      str = getCode39Ex(this.code);
    if (this.generateChecksum)
      str = str + getChecksum(str);
    int k = str.length() + 2;
    int m = (int)this.n;
    int n = k * (6 + 3 * m) + (k - 1);
    byte[] arrayOfByte = getBarsCode39(str);
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
 * Qualified Name:     com.lowagie.text.pdf.Barcode39
 * JD-Core Version:    0.6.0
 */