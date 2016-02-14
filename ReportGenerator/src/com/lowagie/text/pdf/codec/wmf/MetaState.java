package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class MetaState
{
  public static final int TA_NOUPDATECP = 0;
  public static final int TA_UPDATECP = 1;
  public static final int TA_LEFT = 0;
  public static final int TA_RIGHT = 2;
  public static final int TA_CENTER = 6;
  public static final int TA_TOP = 0;
  public static final int TA_BOTTOM = 8;
  public static final int TA_BASELINE = 24;
  public static final int TRANSPARENT = 1;
  public static final int OPAQUE = 2;
  public static final int ALTERNATE = 1;
  public static final int WINDING = 2;
  public Stack savedStates;
  public ArrayList MetaObjects;
  public Point currentPoint;
  public MetaPen currentPen;
  public MetaBrush currentBrush;
  public MetaFont currentFont;
  public Color currentBackgroundColor = Color.white;
  public Color currentTextColor = Color.black;
  public int backgroundMode = 2;
  public int polyFillMode = 1;
  public int lineJoin = 1;
  public int textAlign;
  public int offsetWx;
  public int offsetWy;
  public int extentWx;
  public int extentWy;
  public float scalingX;
  public float scalingY;

  public MetaState()
  {
    this.savedStates = new Stack();
    this.MetaObjects = new ArrayList();
    this.currentPoint = new Point(0, 0);
    this.currentPen = new MetaPen();
    this.currentBrush = new MetaBrush();
    this.currentFont = new MetaFont();
  }

  public MetaState(MetaState paramMetaState)
  {
    setMetaState(paramMetaState);
  }

  public void setMetaState(MetaState paramMetaState)
  {
    this.savedStates = paramMetaState.savedStates;
    this.MetaObjects = paramMetaState.MetaObjects;
    this.currentPoint = paramMetaState.currentPoint;
    this.currentPen = paramMetaState.currentPen;
    this.currentBrush = paramMetaState.currentBrush;
    this.currentFont = paramMetaState.currentFont;
    this.currentBackgroundColor = paramMetaState.currentBackgroundColor;
    this.currentTextColor = paramMetaState.currentTextColor;
    this.backgroundMode = paramMetaState.backgroundMode;
    this.polyFillMode = paramMetaState.polyFillMode;
    this.textAlign = paramMetaState.textAlign;
    this.lineJoin = paramMetaState.lineJoin;
    this.offsetWx = paramMetaState.offsetWx;
    this.offsetWy = paramMetaState.offsetWy;
    this.extentWx = paramMetaState.extentWx;
    this.extentWy = paramMetaState.extentWy;
    this.scalingX = paramMetaState.scalingX;
    this.scalingY = paramMetaState.scalingY;
  }

  public void addMetaObject(MetaObject paramMetaObject)
  {
    for (int i = 0; i < this.MetaObjects.size(); i++)
    {
      if (this.MetaObjects.get(i) != null)
        continue;
      this.MetaObjects.set(i, paramMetaObject);
      return;
    }
    this.MetaObjects.add(paramMetaObject);
  }

  public void selectMetaObject(int paramInt, PdfContentByte paramPdfContentByte)
  {
    MetaObject localMetaObject = (MetaObject)this.MetaObjects.get(paramInt);
    if (localMetaObject == null)
      return;
    int i;
    Color localColor;
    switch (localMetaObject.getType())
    {
    case 2:
      this.currentBrush = ((MetaBrush)localMetaObject);
      i = this.currentBrush.getStyle();
      if (i == 0)
      {
        localColor = this.currentBrush.getColor();
        paramPdfContentByte.setColorFill(localColor);
      }
      else
      {
        if (i != 2)
          break;
        localColor = this.currentBackgroundColor;
        paramPdfContentByte.setColorFill(localColor);
      }
      break;
    case 1:
      this.currentPen = ((MetaPen)localMetaObject);
      i = this.currentPen.getStyle();
      if (i == 5)
        break;
      localColor = this.currentPen.getColor();
      paramPdfContentByte.setColorStroke(localColor);
      paramPdfContentByte.setLineWidth(Math.abs(this.currentPen.getPenWidth() * this.scalingX / this.extentWx));
      switch (i)
      {
      case 1:
        paramPdfContentByte.setLineDash(18.0F, 6.0F, 0.0F);
        break;
      case 3:
        paramPdfContentByte.setLiteral("[9 6 3 6]0 d\n");
        break;
      case 4:
        paramPdfContentByte.setLiteral("[9 3 3 3 3 3]0 d\n");
        break;
      case 2:
        paramPdfContentByte.setLineDash(3.0F, 0.0F);
        break;
      default:
        paramPdfContentByte.setLineDash(0.0F);
      }
      break;
    case 3:
      this.currentFont = ((MetaFont)localMetaObject);
    }
  }

  public void deleteMetaObject(int paramInt)
  {
    this.MetaObjects.set(paramInt, null);
  }

  public void saveState(PdfContentByte paramPdfContentByte)
  {
    paramPdfContentByte.saveState();
    MetaState localMetaState = new MetaState(this);
    this.savedStates.push(localMetaState);
  }

  public void restoreState(int paramInt, PdfContentByte paramPdfContentByte)
  {
    int i;
    if (paramInt < 0)
      i = Math.min(-paramInt, this.savedStates.size());
    else
      i = Math.max(this.savedStates.size() - paramInt, 0);
    if (i == 0)
      return;
    for (MetaState localMetaState = null; i-- != 0; localMetaState = (MetaState)this.savedStates.pop())
      paramPdfContentByte.restoreState();
    setMetaState(localMetaState);
  }

  public void cleanup(PdfContentByte paramPdfContentByte)
  {
    int i = this.savedStates.size();
    while (i-- > 0)
      paramPdfContentByte.restoreState();
  }

  public float transformX(int paramInt)
  {
    return (paramInt - this.offsetWx) * this.scalingX / this.extentWx;
  }

  public float transformY(int paramInt)
  {
    return (1.0F - (paramInt - this.offsetWy) / this.extentWy) * this.scalingY;
  }

  public void setScalingX(float paramFloat)
  {
    this.scalingX = paramFloat;
  }

  public void setScalingY(float paramFloat)
  {
    this.scalingY = paramFloat;
  }

  public void setOffsetWx(int paramInt)
  {
    this.offsetWx = paramInt;
  }

  public void setOffsetWy(int paramInt)
  {
    this.offsetWy = paramInt;
  }

  public void setExtentWx(int paramInt)
  {
    this.extentWx = paramInt;
  }

  public void setExtentWy(int paramInt)
  {
    this.extentWy = paramInt;
  }

  public float transformAngle(float paramFloat)
  {
    float f = this.scalingY < 0.0F ? -paramFloat : paramFloat;
    return (float)f;
  }

  public void setCurrentPoint(Point paramPoint)
  {
    this.currentPoint = paramPoint;
  }

  public Point getCurrentPoint()
  {
    return this.currentPoint;
  }

  public MetaBrush getCurrentBrush()
  {
    return this.currentBrush;
  }

  public MetaPen getCurrentPen()
  {
    return this.currentPen;
  }

  public MetaFont getCurrentFont()
  {
    return this.currentFont;
  }

  public Color getCurrentBackgroundColor()
  {
    return this.currentBackgroundColor;
  }

  public void setCurrentBackgroundColor(Color paramColor)
  {
    this.currentBackgroundColor = paramColor;
  }

  public Color getCurrentTextColor()
  {
    return this.currentTextColor;
  }

  public void setCurrentTextColor(Color paramColor)
  {
    this.currentTextColor = paramColor;
  }

  public int getBackgroundMode()
  {
    return this.backgroundMode;
  }

  public void setBackgroundMode(int paramInt)
  {
    this.backgroundMode = paramInt;
  }

  public int getTextAlign()
  {
    return this.textAlign;
  }

  public void setTextAlign(int paramInt)
  {
    this.textAlign = paramInt;
  }

  public int getPolyFillMode()
  {
    return this.polyFillMode;
  }

  public void setPolyFillMode(int paramInt)
  {
    this.polyFillMode = paramInt;
  }

  public void setLineJoinRectangle(PdfContentByte paramPdfContentByte)
  {
    if (this.lineJoin != 0)
    {
      this.lineJoin = 0;
      paramPdfContentByte.setLineJoin(0);
    }
  }

  public void setLineJoinPolygon(PdfContentByte paramPdfContentByte)
  {
    if (this.lineJoin == 0)
    {
      this.lineJoin = 1;
      paramPdfContentByte.setLineJoin(1);
    }
  }

  public boolean getLineNeutral()
  {
    return this.lineJoin == 0;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.MetaState
 * JD-Core Version:    0.6.0
 */