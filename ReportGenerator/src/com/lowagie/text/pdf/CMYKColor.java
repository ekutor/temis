package com.lowagie.text.pdf;

public class CMYKColor extends ExtendedColor
{
  private static final long serialVersionUID = 5940378778276468452L;
  float cyan;
  float magenta;
  float yellow;
  float black;

  public CMYKColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1 / 255.0F, paramInt2 / 255.0F, paramInt3 / 255.0F, paramInt4 / 255.0F);
  }

  public CMYKColor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    super(2, 1.0F - paramFloat1 - paramFloat4, 1.0F - paramFloat2 - paramFloat4, 1.0F - paramFloat3 - paramFloat4);
    this.cyan = normalize(paramFloat1);
    this.magenta = normalize(paramFloat2);
    this.yellow = normalize(paramFloat3);
    this.black = normalize(paramFloat4);
  }

  public float getCyan()
  {
    return this.cyan;
  }

  public float getMagenta()
  {
    return this.magenta;
  }

  public float getYellow()
  {
    return this.yellow;
  }

  public float getBlack()
  {
    return this.black;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CMYKColor))
      return false;
    CMYKColor localCMYKColor = (CMYKColor)paramObject;
    return (this.cyan == localCMYKColor.cyan) && (this.magenta == localCMYKColor.magenta) && (this.yellow == localCMYKColor.yellow) && (this.black == localCMYKColor.black);
  }

  public int hashCode()
  {
    return Float.floatToIntBits(this.cyan) ^ Float.floatToIntBits(this.magenta) ^ Float.floatToIntBits(this.yellow) ^ Float.floatToIntBits(this.black);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.CMYKColor
 * JD-Core Version:    0.6.0
 */