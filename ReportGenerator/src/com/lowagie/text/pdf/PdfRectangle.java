package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;

public class PdfRectangle extends PdfArray
{
  private float llx = 0.0F;
  private float lly = 0.0F;
  private float urx = 0.0F;
  private float ury = 0.0F;

  public PdfRectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
  {
    if ((paramInt == 90) || (paramInt == 270))
    {
      this.llx = paramFloat2;
      this.lly = paramFloat1;
      this.urx = paramFloat4;
      this.ury = paramFloat3;
    }
    else
    {
      this.llx = paramFloat1;
      this.lly = paramFloat2;
      this.urx = paramFloat3;
      this.ury = paramFloat4;
    }
    super.add(new PdfNumber(this.llx));
    super.add(new PdfNumber(this.lly));
    super.add(new PdfNumber(this.urx));
    super.add(new PdfNumber(this.ury));
  }

  public PdfRectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4, 0);
  }

  public PdfRectangle(float paramFloat1, float paramFloat2, int paramInt)
  {
    this(0.0F, 0.0F, paramFloat1, paramFloat2, paramInt);
  }

  public PdfRectangle(float paramFloat1, float paramFloat2)
  {
    this(0.0F, 0.0F, paramFloat1, paramFloat2, 0);
  }

  public PdfRectangle(Rectangle paramRectangle, int paramInt)
  {
    this(paramRectangle.getLeft(), paramRectangle.getBottom(), paramRectangle.getRight(), paramRectangle.getTop(), paramInt);
  }

  public PdfRectangle(Rectangle paramRectangle)
  {
    this(paramRectangle.getLeft(), paramRectangle.getBottom(), paramRectangle.getRight(), paramRectangle.getTop(), 0);
  }

  public Rectangle getRectangle()
  {
    return new Rectangle(left(), bottom(), right(), top());
  }

  public boolean add(PdfObject paramPdfObject)
  {
    return false;
  }

  public boolean add(float[] paramArrayOfFloat)
  {
    return false;
  }

  public boolean add(int[] paramArrayOfInt)
  {
    return false;
  }

  public void addFirst(PdfObject paramPdfObject)
  {
  }

  public float left()
  {
    return this.llx;
  }

  public float right()
  {
    return this.urx;
  }

  public float top()
  {
    return this.ury;
  }

  public float bottom()
  {
    return this.lly;
  }

  public float left(int paramInt)
  {
    return this.llx + paramInt;
  }

  public float right(int paramInt)
  {
    return this.urx - paramInt;
  }

  public float top(int paramInt)
  {
    return this.ury - paramInt;
  }

  public float bottom(int paramInt)
  {
    return this.lly + paramInt;
  }

  public float width()
  {
    return this.urx - this.llx;
  }

  public float height()
  {
    return this.ury - this.lly;
  }

  public PdfRectangle rotate()
  {
    return new PdfRectangle(this.lly, this.llx, this.ury, this.urx, 0);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfRectangle
 * JD-Core Version:    0.6.0
 */