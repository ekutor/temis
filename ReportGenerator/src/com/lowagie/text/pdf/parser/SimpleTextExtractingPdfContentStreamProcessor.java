package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.CMapAwareDocumentFont;

public class SimpleTextExtractingPdfContentStreamProcessor extends PdfContentStreamProcessor
{
  Matrix lastTextLineMatrix = null;
  Matrix lastEndingTextMatrix = null;
  StringBuffer result = null;

  public void reset()
  {
    super.reset();
    this.lastTextLineMatrix = null;
    this.lastEndingTextMatrix = null;
    this.result = new StringBuffer();
  }

  public String getResultantText()
  {
    return this.result.toString();
  }

  public void displayText(String paramString, Matrix paramMatrix)
  {
    int i = 0;
    if ((this.lastTextLineMatrix != null) && (this.lastTextLineMatrix.get(7) != getCurrentTextLineMatrix().get(7)))
      i = 1;
    float f1 = getCurrentTextMatrix().get(6);
    if (i != 0)
    {
      this.result.append('\n');
    }
    else if (this.lastEndingTextMatrix != null)
    {
      float f2 = this.lastEndingTextMatrix.get(6);
      float f3 = gs().font.getWidth(32) / 1000.0F;
      float f4 = (f3 * gs().fontSize + gs().characterSpacing + gs().wordSpacing) * gs().horizontalScaling;
      Matrix localMatrix = new Matrix(f4, 0.0F).multiply(getCurrentTextMatrix());
      float f5 = localMatrix.get(6) - getCurrentTextMatrix().get(6);
      if (f1 - f2 > f5 / 2.0F)
        this.result.append(' ');
    }
    this.result.append(paramString);
    this.lastTextLineMatrix = getCurrentTextLineMatrix();
    this.lastEndingTextMatrix = paramMatrix;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.SimpleTextExtractingPdfContentStreamProcessor
 * JD-Core Version:    0.6.0
 */