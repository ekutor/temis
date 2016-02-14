package com.lowagie.text.pdf;

public class GrayColor extends ExtendedColor
{
  private static final long serialVersionUID = -6571835680819282746L;
  private float gray;
  public static final GrayColor GRAYBLACK = new GrayColor(0.0F);
  public static final GrayColor GRAYWHITE = new GrayColor(1.0F);

  public GrayColor(int paramInt)
  {
    this(paramInt / 255.0F);
  }

  public GrayColor(float paramFloat)
  {
    super(1, paramFloat, paramFloat, paramFloat);
    this.gray = normalize(paramFloat);
  }

  public float getGray()
  {
    return this.gray;
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof GrayColor)) && (((GrayColor)paramObject).gray == this.gray);
  }

  public int hashCode()
  {
    return Float.floatToIntBits(this.gray);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.GrayColor
 * JD-Core Version:    0.6.0
 */