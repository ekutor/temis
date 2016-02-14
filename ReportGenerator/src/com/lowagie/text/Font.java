package com.lowagie.text;

import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;

public class Font
  implements Comparable
{
  public static final int COURIER = 0;
  public static final int HELVETICA = 1;
  public static final int TIMES_ROMAN = 2;
  public static final int SYMBOL = 3;
  public static final int ZAPFDINGBATS = 4;
  public static final int NORMAL = 0;
  public static final int BOLD = 1;
  public static final int ITALIC = 2;
  public static final int UNDERLINE = 4;
  public static final int STRIKETHRU = 8;
  public static final int BOLDITALIC = 3;
  public static final int UNDEFINED = -1;
  public static final int DEFAULTSIZE = 12;
  private int family = -1;
  private float size = -1.0F;
  private int style = -1;
  private Color color = null;
  private BaseFont baseFont = null;

  public Font(Font paramFont)
  {
    this.family = paramFont.family;
    this.size = paramFont.size;
    this.style = paramFont.style;
    this.color = paramFont.color;
    this.baseFont = paramFont.baseFont;
  }

  public Font(int paramInt1, float paramFloat, int paramInt2, Color paramColor)
  {
    this.family = paramInt1;
    this.size = paramFloat;
    this.style = paramInt2;
    this.color = paramColor;
  }

  public Font(BaseFont paramBaseFont, float paramFloat, int paramInt, Color paramColor)
  {
    this.baseFont = paramBaseFont;
    this.size = paramFloat;
    this.style = paramInt;
    this.color = paramColor;
  }

  public Font(BaseFont paramBaseFont, float paramFloat, int paramInt)
  {
    this(paramBaseFont, paramFloat, paramInt, null);
  }

  public Font(BaseFont paramBaseFont, float paramFloat)
  {
    this(paramBaseFont, paramFloat, -1, null);
  }

  public Font(BaseFont paramBaseFont)
  {
    this(paramBaseFont, -1.0F, -1, null);
  }

  public Font(int paramInt1, float paramFloat, int paramInt2)
  {
    this(paramInt1, paramFloat, paramInt2, null);
  }

  public Font(int paramInt, float paramFloat)
  {
    this(paramInt, paramFloat, -1, null);
  }

  public Font(int paramInt)
  {
    this(paramInt, -1.0F, -1, null);
  }

  public Font()
  {
    this(-1, -1.0F, -1, null);
  }

  public int compareTo(Object paramObject)
  {
    if (paramObject == null)
      return -1;
    try
    {
      Font localFont = (Font)paramObject;
      if ((this.baseFont != null) && (!this.baseFont.equals(localFont.getBaseFont())))
        return -2;
      if (this.family != localFont.getFamily())
        return 1;
      if (this.size != localFont.getSize())
        return 2;
      if (this.style != localFont.getStyle())
        return 3;
      if (this.color == null)
      {
        if (localFont.color == null)
          return 0;
        return 4;
      }
      if (localFont.color == null)
        return 4;
      if (this.color.equals(localFont.getColor()))
        return 0;
      return 4;
    }
    catch (ClassCastException localClassCastException)
    {
    }
    return -3;
  }

  public int getFamily()
  {
    return this.family;
  }

  public String getFamilyname()
  {
    String str = "unknown";
    switch (getFamily())
    {
    case 0:
      return "Courier";
    case 1:
      return "Helvetica";
    case 2:
      return "Times-Roman";
    case 3:
      return "Symbol";
    case 4:
      return "ZapfDingbats";
    }
    if (this.baseFont != null)
    {
      String[][] arrayOfString = this.baseFont.getFamilyFontName();
      for (int i = 0; i < arrayOfString.length; i++)
      {
        if ("0".equals(arrayOfString[i][2]))
          return arrayOfString[i][3];
        if ("1033".equals(arrayOfString[i][2]))
          str = arrayOfString[i][3];
        if (!"".equals(arrayOfString[i][2]))
          continue;
        str = arrayOfString[i][3];
      }
    }
    return str;
  }

  public void setFamily(String paramString)
  {
    this.family = getFamilyIndex(paramString);
  }

  public static int getFamilyIndex(String paramString)
  {
    if (paramString.equalsIgnoreCase("Courier"))
      return 0;
    if (paramString.equalsIgnoreCase("Helvetica"))
      return 1;
    if (paramString.equalsIgnoreCase("Times-Roman"))
      return 2;
    if (paramString.equalsIgnoreCase("Symbol"))
      return 3;
    if (paramString.equalsIgnoreCase("ZapfDingbats"))
      return 4;
    return -1;
  }

  public float getSize()
  {
    return this.size;
  }

  public float getCalculatedSize()
  {
    float f = this.size;
    if (f == -1.0F)
      f = 12.0F;
    return f;
  }

  public float getCalculatedLeading(float paramFloat)
  {
    return paramFloat * getCalculatedSize();
  }

  public void setSize(float paramFloat)
  {
    this.size = paramFloat;
  }

  public int getStyle()
  {
    return this.style;
  }

  public int getCalculatedStyle()
  {
    int i = this.style;
    if (i == -1)
      i = 0;
    if (this.baseFont != null)
      return i;
    if ((this.family == 3) || (this.family == 4))
      return i;
    return i & 0xFFFFFFFC;
  }

  public boolean isBold()
  {
    if (this.style == -1)
      return false;
    return (this.style & 0x1) == 1;
  }

  public boolean isItalic()
  {
    if (this.style == -1)
      return false;
    return (this.style & 0x2) == 2;
  }

  public boolean isUnderlined()
  {
    if (this.style == -1)
      return false;
    return (this.style & 0x4) == 4;
  }

  public boolean isStrikethru()
  {
    if (this.style == -1)
      return false;
    return (this.style & 0x8) == 8;
  }

  public void setStyle(int paramInt)
  {
    this.style = paramInt;
  }

  public void setStyle(String paramString)
  {
    if (this.style == -1)
      this.style = 0;
    this.style |= getStyleValue(paramString);
  }

  public static int getStyleValue(String paramString)
  {
    int i = 0;
    if (paramString.indexOf("normal") != -1)
      i |= 0;
    if (paramString.indexOf("bold") != -1)
      i |= 1;
    if (paramString.indexOf("italic") != -1)
      i |= 2;
    if (paramString.indexOf("oblique") != -1)
      i |= 2;
    if (paramString.indexOf("underline") != -1)
      i |= 4;
    if (paramString.indexOf("line-through") != -1)
      i |= 8;
    return i;
  }

  public Color getColor()
  {
    return this.color;
  }

  public void setColor(Color paramColor)
  {
    this.color = paramColor;
  }

  public void setColor(int paramInt1, int paramInt2, int paramInt3)
  {
    this.color = new Color(paramInt1, paramInt2, paramInt3);
  }

  public BaseFont getBaseFont()
  {
    return this.baseFont;
  }

  public BaseFont getCalculatedBaseFont(boolean paramBoolean)
  {
    if (this.baseFont != null)
      return this.baseFont;
    int i = this.style;
    if (i == -1)
      i = 0;
    String str1 = "Helvetica";
    String str2 = "Cp1252";
    BaseFont localBaseFont = null;
    switch (this.family)
    {
    case 0:
      switch (i & 0x3)
      {
      case 1:
        str1 = "Courier-Bold";
        break;
      case 2:
        str1 = "Courier-Oblique";
        break;
      case 3:
        str1 = "Courier-BoldOblique";
        break;
      default:
        str1 = "Courier";
      }
      break;
    case 2:
      switch (i & 0x3)
      {
      case 1:
        str1 = "Times-Bold";
        break;
      case 2:
        str1 = "Times-Italic";
        break;
      case 3:
        str1 = "Times-BoldItalic";
        break;
      case 0:
      default:
        str1 = "Times-Roman";
      }
      break;
    case 3:
      str1 = "Symbol";
      if (!paramBoolean)
        break;
      str2 = "Symbol";
      break;
    case 4:
      str1 = "ZapfDingbats";
      if (!paramBoolean)
        break;
      str2 = "ZapfDingbats";
      break;
    case 1:
    default:
      switch (i & 0x3)
      {
      case 1:
        str1 = "Helvetica-Bold";
        break;
      case 2:
        str1 = "Helvetica-Oblique";
        break;
      case 3:
        str1 = "Helvetica-BoldOblique";
        break;
      case 0:
      default:
        str1 = "Helvetica";
      }
    }
    try
    {
      localBaseFont = BaseFont.createFont(str1, str2, false);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    return localBaseFont;
  }

  public boolean isStandardFont()
  {
    return (this.family == -1) && (this.size == -1.0F) && (this.style == -1) && (this.color == null) && (this.baseFont == null);
  }

  public Font difference(Font paramFont)
  {
    if (paramFont == null)
      return this;
    float f = paramFont.size;
    if (f == -1.0F)
      f = this.size;
    int i = -1;
    int j = this.style;
    int k = paramFont.getStyle();
    if ((j != -1) || (k != -1))
    {
      if (j == -1)
        j = 0;
      if (k == -1)
        k = 0;
      i = j | k;
    }
    Color localColor = paramFont.color;
    if (localColor == null)
      localColor = this.color;
    if (paramFont.baseFont != null)
      return new Font(paramFont.baseFont, f, i, localColor);
    if (paramFont.getFamily() != -1)
      return new Font(paramFont.family, f, i, localColor);
    if (this.baseFont != null)
    {
      if (i == j)
        return new Font(this.baseFont, f, i, localColor);
      return FontFactory.getFont(getFamilyname(), f, i, localColor);
    }
    return new Font(this.family, f, i, localColor);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Font
 * JD-Core Version:    0.6.0
 */