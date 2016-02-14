package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.awt.Color;

public final class PdfPatternPainter extends PdfTemplate
{
  float xstep;
  float ystep;
  boolean stencil = false;
  Color defaultColor;

  private PdfPatternPainter()
  {
    this.type = 3;
  }

  PdfPatternPainter(PdfWriter paramPdfWriter)
  {
    super(paramPdfWriter);
    this.type = 3;
  }

  PdfPatternPainter(PdfWriter paramPdfWriter, Color paramColor)
  {
    this(paramPdfWriter);
    this.stencil = true;
    if (paramColor == null)
      this.defaultColor = Color.gray;
    else
      this.defaultColor = paramColor;
  }

  public void setXStep(float paramFloat)
  {
    this.xstep = paramFloat;
  }

  public void setYStep(float paramFloat)
  {
    this.ystep = paramFloat;
  }

  public float getXStep()
  {
    return this.xstep;
  }

  public float getYStep()
  {
    return this.ystep;
  }

  public boolean isStencil()
  {
    return this.stencil;
  }

  public void setPatternMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    setMatrix(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
  }

  PdfPattern getPattern()
  {
    return new PdfPattern(this);
  }

  PdfPattern getPattern(int paramInt)
  {
    return new PdfPattern(this, paramInt);
  }

  public PdfContentByte getDuplicate()
  {
    PdfPatternPainter localPdfPatternPainter = new PdfPatternPainter();
    localPdfPatternPainter.writer = this.writer;
    localPdfPatternPainter.pdf = this.pdf;
    localPdfPatternPainter.thisReference = this.thisReference;
    localPdfPatternPainter.pageResources = this.pageResources;
    localPdfPatternPainter.bBox = new Rectangle(this.bBox);
    localPdfPatternPainter.xstep = this.xstep;
    localPdfPatternPainter.ystep = this.ystep;
    localPdfPatternPainter.matrix = this.matrix;
    localPdfPatternPainter.stencil = this.stencil;
    localPdfPatternPainter.defaultColor = this.defaultColor;
    return localPdfPatternPainter;
  }

  public Color getDefaultColor()
  {
    return this.defaultColor;
  }

  public void setGrayFill(float paramFloat)
  {
    checkNoColor();
    super.setGrayFill(paramFloat);
  }

  public void resetGrayFill()
  {
    checkNoColor();
    super.resetGrayFill();
  }

  public void setGrayStroke(float paramFloat)
  {
    checkNoColor();
    super.setGrayStroke(paramFloat);
  }

  public void resetGrayStroke()
  {
    checkNoColor();
    super.resetGrayStroke();
  }

  public void setRGBColorFillF(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    checkNoColor();
    super.setRGBColorFillF(paramFloat1, paramFloat2, paramFloat3);
  }

  public void resetRGBColorFill()
  {
    checkNoColor();
    super.resetRGBColorFill();
  }

  public void setRGBColorStrokeF(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    checkNoColor();
    super.setRGBColorStrokeF(paramFloat1, paramFloat2, paramFloat3);
  }

  public void resetRGBColorStroke()
  {
    checkNoColor();
    super.resetRGBColorStroke();
  }

  public void setCMYKColorFillF(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    checkNoColor();
    super.setCMYKColorFillF(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public void resetCMYKColorFill()
  {
    checkNoColor();
    super.resetCMYKColorFill();
  }

  public void setCMYKColorStrokeF(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    checkNoColor();
    super.setCMYKColorStrokeF(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public void resetCMYKColorStroke()
  {
    checkNoColor();
    super.resetCMYKColorStroke();
  }

  public void addImage(Image paramImage, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    throws DocumentException
  {
    if ((this.stencil) && (!paramImage.isMask()))
      checkNoColor();
    super.addImage(paramImage, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
  }

  public void setCMYKColorFill(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    checkNoColor();
    super.setCMYKColorFill(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setCMYKColorStroke(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    checkNoColor();
    super.setCMYKColorStroke(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setRGBColorFill(int paramInt1, int paramInt2, int paramInt3)
  {
    checkNoColor();
    super.setRGBColorFill(paramInt1, paramInt2, paramInt3);
  }

  public void setRGBColorStroke(int paramInt1, int paramInt2, int paramInt3)
  {
    checkNoColor();
    super.setRGBColorStroke(paramInt1, paramInt2, paramInt3);
  }

  public void setColorStroke(Color paramColor)
  {
    checkNoColor();
    super.setColorStroke(paramColor);
  }

  public void setColorFill(Color paramColor)
  {
    checkNoColor();
    super.setColorFill(paramColor);
  }

  public void setColorFill(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    checkNoColor();
    super.setColorFill(paramPdfSpotColor, paramFloat);
  }

  public void setColorStroke(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    checkNoColor();
    super.setColorStroke(paramPdfSpotColor, paramFloat);
  }

  public void setPatternFill(PdfPatternPainter paramPdfPatternPainter)
  {
    checkNoColor();
    super.setPatternFill(paramPdfPatternPainter);
  }

  public void setPatternFill(PdfPatternPainter paramPdfPatternPainter, Color paramColor, float paramFloat)
  {
    checkNoColor();
    super.setPatternFill(paramPdfPatternPainter, paramColor, paramFloat);
  }

  public void setPatternStroke(PdfPatternPainter paramPdfPatternPainter, Color paramColor, float paramFloat)
  {
    checkNoColor();
    super.setPatternStroke(paramPdfPatternPainter, paramColor, paramFloat);
  }

  public void setPatternStroke(PdfPatternPainter paramPdfPatternPainter)
  {
    checkNoColor();
    super.setPatternStroke(paramPdfPatternPainter);
  }

  void checkNoColor()
  {
    if (this.stencil)
      throw new RuntimeException("Colors are not allowed in uncolored tile patterns.");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPatternPainter
 * JD-Core Version:    0.6.0
 */