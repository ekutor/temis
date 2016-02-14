package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;

public class DottedLineSeparator extends LineSeparator
{
  protected float gap = 5.0F;

  public void draw(PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    paramPdfContentByte.saveState();
    paramPdfContentByte.setLineWidth(this.lineWidth);
    paramPdfContentByte.setLineCap(1);
    paramPdfContentByte.setLineDash(0.0F, this.gap, this.gap / 2.0F);
    drawLine(paramPdfContentByte, paramFloat1, paramFloat3, paramFloat5);
    paramPdfContentByte.restoreState();
  }

  public float getGap()
  {
    return this.gap;
  }

  public void setGap(float paramFloat)
  {
    this.gap = paramFloat;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.draw.DottedLineSeparator
 * JD-Core Version:    0.6.0
 */