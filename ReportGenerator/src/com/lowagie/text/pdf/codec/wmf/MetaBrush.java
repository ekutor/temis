package com.lowagie.text.pdf.codec.wmf;

import java.awt.Color;
import java.io.IOException;

public class MetaBrush extends MetaObject
{
  public static final int BS_SOLID = 0;
  public static final int BS_NULL = 1;
  public static final int BS_HATCHED = 2;
  public static final int BS_PATTERN = 3;
  public static final int BS_DIBPATTERN = 5;
  public static final int HS_HORIZONTAL = 0;
  public static final int HS_VERTICAL = 1;
  public static final int HS_FDIAGONAL = 2;
  public static final int HS_BDIAGONAL = 3;
  public static final int HS_CROSS = 4;
  public static final int HS_DIAGCROSS = 5;
  int style = 0;
  int hatch;
  Color color = Color.white;

  public MetaBrush()
  {
    this.type = 2;
  }

  public void init(InputMeta paramInputMeta)
    throws IOException
  {
    this.style = paramInputMeta.readWord();
    this.color = paramInputMeta.readColor();
    this.hatch = paramInputMeta.readWord();
  }

  public int getStyle()
  {
    return this.style;
  }

  public int getHatch()
  {
    return this.hatch;
  }

  public Color getColor()
  {
    return this.color;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.MetaBrush
 * JD-Core Version:    0.6.0
 */