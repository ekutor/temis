package com.lowagie.text;

import java.awt.Color;

public class RectangleReadOnly extends Rectangle
{
  public RectangleReadOnly(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    super(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public RectangleReadOnly(float paramFloat1, float paramFloat2)
  {
    super(0.0F, 0.0F, paramFloat1, paramFloat2);
  }

  public RectangleReadOnly(Rectangle paramRectangle)
  {
    super(paramRectangle.llx, paramRectangle.lly, paramRectangle.urx, paramRectangle.ury);
    super.cloneNonPositionParameters(paramRectangle);
  }

  private void throwReadOnlyError()
  {
    throw new UnsupportedOperationException("RectangleReadOnly: this Rectangle is read only.");
  }

  public void setLeft(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setRight(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setTop(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBottom(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void normalize()
  {
    throwReadOnlyError();
  }

  public void setBackgroundColor(Color paramColor)
  {
    throwReadOnlyError();
  }

  public void setGrayFill(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBorder(int paramInt)
  {
    throwReadOnlyError();
  }

  public void setUseVariableBorders(boolean paramBoolean)
  {
    throwReadOnlyError();
  }

  public void enableBorderSide(int paramInt)
  {
    throwReadOnlyError();
  }

  public void disableBorderSide(int paramInt)
  {
    throwReadOnlyError();
  }

  public void setBorderWidth(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBorderWidthLeft(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBorderWidthRight(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBorderWidthTop(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBorderWidthBottom(float paramFloat)
  {
    throwReadOnlyError();
  }

  public void setBorderColor(Color paramColor)
  {
    throwReadOnlyError();
  }

  public void setBorderColorLeft(Color paramColor)
  {
    throwReadOnlyError();
  }

  public void setBorderColorRight(Color paramColor)
  {
    throwReadOnlyError();
  }

  public void setBorderColorTop(Color paramColor)
  {
    throwReadOnlyError();
  }

  public void setBorderColorBottom(Color paramColor)
  {
    throwReadOnlyError();
  }

  public void cloneNonPositionParameters(Rectangle paramRectangle)
  {
    throwReadOnlyError();
  }

  public void softCloneNonPositionParameters(Rectangle paramRectangle)
  {
    throwReadOnlyError();
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("RectangleReadOnly: ");
    localStringBuffer.append(getWidth());
    localStringBuffer.append('x');
    localStringBuffer.append(getHeight());
    localStringBuffer.append(" (rot: ");
    localStringBuffer.append(this.rotation);
    localStringBuffer.append(" degrees)");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.RectangleReadOnly
 * JD-Core Version:    0.6.0
 */