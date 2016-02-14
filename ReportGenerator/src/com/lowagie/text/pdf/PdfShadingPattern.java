package com.lowagie.text.pdf;

import java.io.IOException;

public class PdfShadingPattern extends PdfDictionary
{
  protected PdfShading shading;
  protected PdfWriter writer;
  protected float[] matrix = { 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F };
  protected PdfName patternName;
  protected PdfIndirectReference patternReference;

  public PdfShadingPattern(PdfShading paramPdfShading)
  {
    this.writer = paramPdfShading.getWriter();
    put(PdfName.PATTERNTYPE, new PdfNumber(2));
    this.shading = paramPdfShading;
  }

  PdfName getPatternName()
  {
    return this.patternName;
  }

  PdfName getShadingName()
  {
    return this.shading.getShadingName();
  }

  PdfIndirectReference getPatternReference()
  {
    if (this.patternReference == null)
      this.patternReference = this.writer.getPdfIndirectReference();
    return this.patternReference;
  }

  PdfIndirectReference getShadingReference()
  {
    return this.shading.getShadingReference();
  }

  void setName(int paramInt)
  {
    this.patternName = new PdfName("P" + paramInt);
  }

  void addToBody()
    throws IOException
  {
    put(PdfName.SHADING, getShadingReference());
    put(PdfName.MATRIX, new PdfArray(this.matrix));
    this.writer.addToBody(this, getPatternReference());
  }

  public void setMatrix(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length != 6)
      throw new RuntimeException("The matrix size must be 6.");
    this.matrix = paramArrayOfFloat;
  }

  public float[] getMatrix()
  {
    return this.matrix;
  }

  public PdfShading getShading()
  {
    return this.shading;
  }

  ColorDetails getColorDetails()
  {
    return this.shading.getColorDetails();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfShadingPattern
 * JD-Core Version:    0.6.0
 */