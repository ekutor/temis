package com.lowagie.text;

import com.lowagie.text.pdf.GrayColor;
import java.awt.Color;
import java.util.ArrayList;

public class Rectangle
  implements Element
{
  public static final int UNDEFINED = -1;
  public static final int TOP = 1;
  public static final int BOTTOM = 2;
  public static final int LEFT = 4;
  public static final int RIGHT = 8;
  public static final int NO_BORDER = 0;
  public static final int BOX = 15;
  protected float llx;
  protected float lly;
  protected float urx;
  protected float ury;
  protected int rotation = 0;
  protected Color backgroundColor = null;
  protected int border = -1;
  protected boolean useVariableBorders = false;
  protected float borderWidth = -1.0F;
  protected float borderWidthLeft = -1.0F;
  protected float borderWidthRight = -1.0F;
  protected float borderWidthTop = -1.0F;
  protected float borderWidthBottom = -1.0F;
  protected Color borderColor = null;
  protected Color borderColorLeft = null;
  protected Color borderColorRight = null;
  protected Color borderColorTop = null;
  protected Color borderColorBottom = null;

  public Rectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.llx = paramFloat1;
    this.lly = paramFloat2;
    this.urx = paramFloat3;
    this.ury = paramFloat4;
  }

  public Rectangle(float paramFloat1, float paramFloat2)
  {
    this(0.0F, 0.0F, paramFloat1, paramFloat2);
  }

  public Rectangle(Rectangle paramRectangle)
  {
    this(paramRectangle.llx, paramRectangle.lly, paramRectangle.urx, paramRectangle.ury);
    cloneNonPositionParameters(paramRectangle);
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      return paramElementListener.add(this);
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return 30;
  }

  public ArrayList getChunks()
  {
    return new ArrayList();
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return false;
  }

  public void setLeft(float paramFloat)
  {
    this.llx = paramFloat;
  }

  public float getLeft()
  {
    return this.llx;
  }

  public float getLeft(float paramFloat)
  {
    return this.llx + paramFloat;
  }

  public void setRight(float paramFloat)
  {
    this.urx = paramFloat;
  }

  public float getRight()
  {
    return this.urx;
  }

  public float getRight(float paramFloat)
  {
    return this.urx - paramFloat;
  }

  public float getWidth()
  {
    return this.urx - this.llx;
  }

  public void setTop(float paramFloat)
  {
    this.ury = paramFloat;
  }

  public float getTop()
  {
    return this.ury;
  }

  public float getTop(float paramFloat)
  {
    return this.ury - paramFloat;
  }

  public void setBottom(float paramFloat)
  {
    this.lly = paramFloat;
  }

  public float getBottom()
  {
    return this.lly;
  }

  public float getBottom(float paramFloat)
  {
    return this.lly + paramFloat;
  }

  public float getHeight()
  {
    return this.ury - this.lly;
  }

  public void normalize()
  {
    float f;
    if (this.llx > this.urx)
    {
      f = this.llx;
      this.llx = this.urx;
      this.urx = f;
    }
    if (this.lly > this.ury)
    {
      f = this.lly;
      this.lly = this.ury;
      this.ury = f;
    }
  }

  public int getRotation()
  {
    return this.rotation;
  }

  public Rectangle rotate()
  {
    Rectangle localRectangle = new Rectangle(this.lly, this.llx, this.ury, this.urx);
    this.rotation += 90;
    localRectangle.rotation %= 360;
    return localRectangle;
  }

  public Color getBackgroundColor()
  {
    return this.backgroundColor;
  }

  public void setBackgroundColor(Color paramColor)
  {
    this.backgroundColor = paramColor;
  }

  public float getGrayFill()
  {
    if ((this.backgroundColor instanceof GrayColor))
      return ((GrayColor)this.backgroundColor).getGray();
    return 0.0F;
  }

  public void setGrayFill(float paramFloat)
  {
    this.backgroundColor = new GrayColor(paramFloat);
  }

  public int getBorder()
  {
    return this.border;
  }

  public boolean hasBorders()
  {
    switch (this.border)
    {
    case -1:
    case 0:
      return false;
    }
    return (this.borderWidth > 0.0F) || (this.borderWidthLeft > 0.0F) || (this.borderWidthRight > 0.0F) || (this.borderWidthTop > 0.0F) || (this.borderWidthBottom > 0.0F);
  }

  public boolean hasBorder(int paramInt)
  {
    if (this.border == -1)
      return false;
    return (this.border & paramInt) == paramInt;
  }

  public void setBorder(int paramInt)
  {
    this.border = paramInt;
  }

  public boolean isUseVariableBorders()
  {
    return this.useVariableBorders;
  }

  public void setUseVariableBorders(boolean paramBoolean)
  {
    this.useVariableBorders = paramBoolean;
  }

  public void enableBorderSide(int paramInt)
  {
    if (this.border == -1)
      this.border = 0;
    this.border |= paramInt;
  }

  public void disableBorderSide(int paramInt)
  {
    if (this.border == -1)
      this.border = 0;
    this.border &= (paramInt ^ 0xFFFFFFFF);
  }

  public float getBorderWidth()
  {
    return this.borderWidth;
  }

  public void setBorderWidth(float paramFloat)
  {
    this.borderWidth = paramFloat;
  }

  private float getVariableBorderWidth(float paramFloat, int paramInt)
  {
    if ((this.border & paramInt) != 0)
      return paramFloat != -1.0F ? paramFloat : this.borderWidth;
    return 0.0F;
  }

  private void updateBorderBasedOnWidth(float paramFloat, int paramInt)
  {
    this.useVariableBorders = true;
    if (paramFloat > 0.0F)
      enableBorderSide(paramInt);
    else
      disableBorderSide(paramInt);
  }

  public float getBorderWidthLeft()
  {
    return getVariableBorderWidth(this.borderWidthLeft, 4);
  }

  public void setBorderWidthLeft(float paramFloat)
  {
    this.borderWidthLeft = paramFloat;
    updateBorderBasedOnWidth(paramFloat, 4);
  }

  public float getBorderWidthRight()
  {
    return getVariableBorderWidth(this.borderWidthRight, 8);
  }

  public void setBorderWidthRight(float paramFloat)
  {
    this.borderWidthRight = paramFloat;
    updateBorderBasedOnWidth(paramFloat, 8);
  }

  public float getBorderWidthTop()
  {
    return getVariableBorderWidth(this.borderWidthTop, 1);
  }

  public void setBorderWidthTop(float paramFloat)
  {
    this.borderWidthTop = paramFloat;
    updateBorderBasedOnWidth(paramFloat, 1);
  }

  public float getBorderWidthBottom()
  {
    return getVariableBorderWidth(this.borderWidthBottom, 2);
  }

  public void setBorderWidthBottom(float paramFloat)
  {
    this.borderWidthBottom = paramFloat;
    updateBorderBasedOnWidth(paramFloat, 2);
  }

  public Color getBorderColor()
  {
    return this.borderColor;
  }

  public void setBorderColor(Color paramColor)
  {
    this.borderColor = paramColor;
  }

  public Color getBorderColorLeft()
  {
    if (this.borderColorLeft == null)
      return this.borderColor;
    return this.borderColorLeft;
  }

  public void setBorderColorLeft(Color paramColor)
  {
    this.borderColorLeft = paramColor;
  }

  public Color getBorderColorRight()
  {
    if (this.borderColorRight == null)
      return this.borderColor;
    return this.borderColorRight;
  }

  public void setBorderColorRight(Color paramColor)
  {
    this.borderColorRight = paramColor;
  }

  public Color getBorderColorTop()
  {
    if (this.borderColorTop == null)
      return this.borderColor;
    return this.borderColorTop;
  }

  public void setBorderColorTop(Color paramColor)
  {
    this.borderColorTop = paramColor;
  }

  public Color getBorderColorBottom()
  {
    if (this.borderColorBottom == null)
      return this.borderColor;
    return this.borderColorBottom;
  }

  public void setBorderColorBottom(Color paramColor)
  {
    this.borderColorBottom = paramColor;
  }

  public Rectangle rectangle(float paramFloat1, float paramFloat2)
  {
    Rectangle localRectangle = new Rectangle(this);
    if (getTop() > paramFloat1)
    {
      localRectangle.setTop(paramFloat1);
      localRectangle.disableBorderSide(1);
    }
    if (getBottom() < paramFloat2)
    {
      localRectangle.setBottom(paramFloat2);
      localRectangle.disableBorderSide(2);
    }
    return localRectangle;
  }

  public void cloneNonPositionParameters(Rectangle paramRectangle)
  {
    this.rotation = paramRectangle.rotation;
    this.backgroundColor = paramRectangle.backgroundColor;
    this.border = paramRectangle.border;
    this.useVariableBorders = paramRectangle.useVariableBorders;
    this.borderWidth = paramRectangle.borderWidth;
    this.borderWidthLeft = paramRectangle.borderWidthLeft;
    this.borderWidthRight = paramRectangle.borderWidthRight;
    this.borderWidthTop = paramRectangle.borderWidthTop;
    this.borderWidthBottom = paramRectangle.borderWidthBottom;
    this.borderColor = paramRectangle.borderColor;
    this.borderColorLeft = paramRectangle.borderColorLeft;
    this.borderColorRight = paramRectangle.borderColorRight;
    this.borderColorTop = paramRectangle.borderColorTop;
    this.borderColorBottom = paramRectangle.borderColorBottom;
  }

  public void softCloneNonPositionParameters(Rectangle paramRectangle)
  {
    if (paramRectangle.rotation != 0)
      this.rotation = paramRectangle.rotation;
    if (paramRectangle.backgroundColor != null)
      this.backgroundColor = paramRectangle.backgroundColor;
    if (paramRectangle.border != -1)
      this.border = paramRectangle.border;
    if (this.useVariableBorders)
      this.useVariableBorders = paramRectangle.useVariableBorders;
    if (paramRectangle.borderWidth != -1.0F)
      this.borderWidth = paramRectangle.borderWidth;
    if (paramRectangle.borderWidthLeft != -1.0F)
      this.borderWidthLeft = paramRectangle.borderWidthLeft;
    if (paramRectangle.borderWidthRight != -1.0F)
      this.borderWidthRight = paramRectangle.borderWidthRight;
    if (paramRectangle.borderWidthTop != -1.0F)
      this.borderWidthTop = paramRectangle.borderWidthTop;
    if (paramRectangle.borderWidthBottom != -1.0F)
      this.borderWidthBottom = paramRectangle.borderWidthBottom;
    if (paramRectangle.borderColor != null)
      this.borderColor = paramRectangle.borderColor;
    if (paramRectangle.borderColorLeft != null)
      this.borderColorLeft = paramRectangle.borderColorLeft;
    if (paramRectangle.borderColorRight != null)
      this.borderColorRight = paramRectangle.borderColorRight;
    if (paramRectangle.borderColorTop != null)
      this.borderColorTop = paramRectangle.borderColorTop;
    if (paramRectangle.borderColorBottom != null)
      this.borderColorBottom = paramRectangle.borderColorBottom;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Rectangle: ");
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
 * Qualified Name:     com.lowagie.text.Rectangle
 * JD-Core Version:    0.6.0
 */