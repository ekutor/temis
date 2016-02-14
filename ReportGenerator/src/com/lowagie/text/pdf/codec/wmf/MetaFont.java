package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MetaFont extends MetaObject
{
  static final String[] fontNames = { "Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique", "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique", "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic", "Symbol", "ZapfDingbats" };
  static final int MARKER_BOLD = 1;
  static final int MARKER_ITALIC = 2;
  static final int MARKER_COURIER = 0;
  static final int MARKER_HELVETICA = 4;
  static final int MARKER_TIMES = 8;
  static final int MARKER_SYMBOL = 12;
  static final int DEFAULT_PITCH = 0;
  static final int FIXED_PITCH = 1;
  static final int VARIABLE_PITCH = 2;
  static final int FF_DONTCARE = 0;
  static final int FF_ROMAN = 1;
  static final int FF_SWISS = 2;
  static final int FF_MODERN = 3;
  static final int FF_SCRIPT = 4;
  static final int FF_DECORATIVE = 5;
  static final int BOLDTHRESHOLD = 600;
  static final int nameSize = 32;
  static final int ETO_OPAQUE = 2;
  static final int ETO_CLIPPED = 4;
  int height;
  float angle;
  int bold;
  int italic;
  boolean underline;
  boolean strikeout;
  int charset;
  int pitchAndFamily;
  String faceName = "arial";
  BaseFont font = null;

  public MetaFont()
  {
    this.type = 3;
  }

  public void init(InputMeta paramInputMeta)
    throws IOException
  {
    this.height = Math.abs(paramInputMeta.readShort());
    paramInputMeta.skip(2);
    this.angle = (float)(paramInputMeta.readShort() / 1800.0D * 3.141592653589793D);
    paramInputMeta.skip(2);
    this.bold = (paramInputMeta.readShort() >= 600 ? 1 : 0);
    this.italic = (paramInputMeta.readByte() != 0 ? 2 : 0);
    this.underline = (paramInputMeta.readByte() != 0);
    this.strikeout = (paramInputMeta.readByte() != 0);
    this.charset = paramInputMeta.readByte();
    paramInputMeta.skip(3);
    this.pitchAndFamily = paramInputMeta.readByte();
    byte[] arrayOfByte = new byte[32];
    for (int i = 0; i < 32; i++)
    {
      int j = paramInputMeta.readByte();
      if (j == 0)
        break;
      arrayOfByte[i] = (byte)j;
    }
    try
    {
      this.faceName = new String(arrayOfByte, 0, i, "Cp1252");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      this.faceName = new String(arrayOfByte, 0, i);
    }
    this.faceName = this.faceName.toLowerCase();
  }

  public BaseFont getFont()
  {
    if (this.font != null)
      return this.font;
    Font localFont = FontFactory.getFont(this.faceName, "Cp1252", true, 10.0F, (this.italic != 0 ? 2 : 0) | (this.bold != 0 ? 1 : 0));
    this.font = localFont.getBaseFont();
    if (this.font != null)
      return this.font;
    String str;
    if ((this.faceName.indexOf("courier") != -1) || (this.faceName.indexOf("terminal") != -1) || (this.faceName.indexOf("fixedsys") != -1))
    {
      str = fontNames[(0 + this.italic + this.bold)];
    }
    else if ((this.faceName.indexOf("ms sans serif") != -1) || (this.faceName.indexOf("arial") != -1) || (this.faceName.indexOf("system") != -1))
    {
      str = fontNames[(4 + this.italic + this.bold)];
    }
    else if (this.faceName.indexOf("arial black") != -1)
    {
      str = fontNames[(4 + this.italic + 1)];
    }
    else if ((this.faceName.indexOf("times") != -1) || (this.faceName.indexOf("ms serif") != -1) || (this.faceName.indexOf("roman") != -1))
    {
      str = fontNames[(8 + this.italic + this.bold)];
    }
    else if (this.faceName.indexOf("symbol") != -1)
    {
      str = fontNames[12];
    }
    else
    {
      int i = this.pitchAndFamily & 0x3;
      int j = this.pitchAndFamily >> 4 & 0x7;
      switch (j)
      {
      case 3:
        str = fontNames[(0 + this.italic + this.bold)];
        break;
      case 1:
        str = fontNames[(8 + this.italic + this.bold)];
        break;
      case 2:
      case 4:
      case 5:
        str = fontNames[(4 + this.italic + this.bold)];
        break;
      default:
        switch (i)
        {
        case 1:
          str = fontNames[(0 + this.italic + this.bold)];
          break;
        default:
          str = fontNames[(4 + this.italic + this.bold)];
        }
      }
    }
    try
    {
      this.font = BaseFont.createFont(str, "Cp1252", false);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    return this.font;
  }

  public float getAngle()
  {
    return this.angle;
  }

  public boolean isUnderline()
  {
    return this.underline;
  }

  public boolean isStrikeout()
  {
    return this.strikeout;
  }

  public float getFontSize(MetaState paramMetaState)
  {
    return Math.abs(paramMetaState.transformY(this.height) - paramMetaState.transformY(0)) * Document.wmfFontCorrection;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.MetaFont
 * JD-Core Version:    0.6.0
 */