package com.lowagie.text.pdf.codec.wmf;

import java.awt.Color;
import java.io.IOException;

public class MetaPen extends MetaObject
{
  public static final int PS_SOLID = 0;
  public static final int PS_DASH = 1;
  public static final int PS_DOT = 2;
  public static final int PS_DASHDOT = 3;
  public static final int PS_DASHDOTDOT = 4;
  public static final int PS_NULL = 5;
  public static final int PS_INSIDEFRAME = 6;
  int style = 0;
  int penWidth = 1;
  Color color = Color.black;

  public MetaPen()
  {
    this.type = 1;
  }

  public void init(InputMeta paramInputMeta)
    throws IOException
  {
    this.style = paramInputMeta.readWord();
    this.penWidth = paramInputMeta.readShort();
    paramInputMeta.readWord();
    this.color = paramInputMeta.readColor();
  }

  public int getStyle()
  {
    return this.style;
  }

  public int getPenWidth()
  {
    return this.penWidth;
  }

  public Color getColor()
  {
    return this.color;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.MetaPen
 * JD-Core Version:    0.6.0
 */