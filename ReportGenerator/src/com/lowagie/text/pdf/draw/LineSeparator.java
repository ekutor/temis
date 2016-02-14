package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Color;

public class LineSeparator extends VerticalPositionMark
{
  protected float lineWidth = 1.0F;
  protected float percentage = 100.0F;
  protected Color lineColor;
  protected int alignment = 1;

  public LineSeparator(float paramFloat1, float paramFloat2, Color paramColor, int paramInt, float paramFloat3)
  {
    this.lineWidth = paramFloat1;
    this.percentage = paramFloat2;
    this.lineColor = paramColor;
    this.alignment = paramInt;
    this.offset = paramFloat3;
  }

  public LineSeparator()
  {
  }

  public void draw(PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    paramPdfContentByte.saveState();
    drawLine(paramPdfContentByte, paramFloat1, paramFloat3, paramFloat5);
    paramPdfContentByte.restoreState();
  }

  public void drawLine(PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f1;
    if (getPercentage() < 0.0F)
      f1 = -getPercentage();
    else
      f1 = (paramFloat2 - paramFloat1) * getPercentage() / 100.0F;
    float f2;
    switch (getAlignment())
    {
    case 0:
      f2 = 0.0F;
      break;
    case 2:
      f2 = paramFloat2 - paramFloat1 - f1;
      break;
    default:
      f2 = (paramFloat2 - paramFloat1 - f1) / 2.0F;
    }
    paramPdfContentByte.setLineWidth(getLineWidth());
    if (getLineColor() != null)
      paramPdfContentByte.setColorStroke(getLineColor());
    paramPdfContentByte.moveTo(f2 + paramFloat1, paramFloat3 + this.offset);
    paramPdfContentByte.lineTo(f2 + f1 + paramFloat1, paramFloat3 + this.offset);
    paramPdfContentByte.stroke();
  }

  public float getLineWidth()
  {
    return this.lineWidth;
  }

  public void setLineWidth(float paramFloat)
  {
    this.lineWidth = paramFloat;
  }

  public float getPercentage()
  {
    return this.percentage;
  }

  public void setPercentage(float paramFloat)
  {
    this.percentage = paramFloat;
  }

  public Color getLineColor()
  {
    return this.lineColor;
  }

  public void setLineColor(Color paramColor)
  {
    this.lineColor = paramColor;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.draw.LineSeparator
 * JD-Core Version:    0.6.0
 */