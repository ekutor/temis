package com.lowagie.text.pdf;

import java.awt.Color;

public abstract class ExtendedColor extends Color
{
  private static final long serialVersionUID = 2722660170712380080L;
  public static final int TYPE_RGB = 0;
  public static final int TYPE_GRAY = 1;
  public static final int TYPE_CMYK = 2;
  public static final int TYPE_SEPARATION = 3;
  public static final int TYPE_PATTERN = 4;
  public static final int TYPE_SHADING = 5;
  protected int type;

  public ExtendedColor(int paramInt)
  {
    super(0, 0, 0);
    this.type = paramInt;
  }

  public ExtendedColor(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    super(normalize(paramFloat1), normalize(paramFloat2), normalize(paramFloat3));
    this.type = paramInt;
  }

  public int getType()
  {
    return this.type;
  }

  public static int getType(Color paramColor)
  {
    if ((paramColor instanceof ExtendedColor))
      return ((ExtendedColor)paramColor).getType();
    return 0;
  }

  static final float normalize(float paramFloat)
  {
    if (paramFloat < 0.0F)
      return 0.0F;
    if (paramFloat > 1.0F)
      return 1.0F;
    return paramFloat;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ExtendedColor
 * JD-Core Version:    0.6.0
 */