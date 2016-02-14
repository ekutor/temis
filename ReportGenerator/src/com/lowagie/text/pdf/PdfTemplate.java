package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import java.io.IOException;

public class PdfTemplate extends PdfContentByte
{
  public static final int TYPE_TEMPLATE = 1;
  public static final int TYPE_IMPORTED = 2;
  public static final int TYPE_PATTERN = 3;
  protected int type = 1;
  protected PdfIndirectReference thisReference;
  protected PageResources pageResources;
  protected Rectangle bBox = new Rectangle(0.0F, 0.0F);
  protected PdfArray matrix;
  protected PdfTransparencyGroup group;
  protected PdfOCG layer;

  protected PdfTemplate()
  {
    super(null);
  }

  PdfTemplate(PdfWriter paramPdfWriter)
  {
    super(paramPdfWriter);
    this.pageResources = new PageResources();
    this.pageResources.addDefaultColor(paramPdfWriter.getDefaultColorspace());
    this.thisReference = this.writer.getPdfIndirectReference();
  }

  public static PdfTemplate createTemplate(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2)
  {
    return createTemplate(paramPdfWriter, paramFloat1, paramFloat2, null);
  }

  static PdfTemplate createTemplate(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, PdfName paramPdfName)
  {
    PdfTemplate localPdfTemplate = new PdfTemplate(paramPdfWriter);
    localPdfTemplate.setWidth(paramFloat1);
    localPdfTemplate.setHeight(paramFloat2);
    paramPdfWriter.addDirectTemplateSimple(localPdfTemplate, paramPdfName);
    return localPdfTemplate;
  }

  public void setWidth(float paramFloat)
  {
    this.bBox.setLeft(0.0F);
    this.bBox.setRight(paramFloat);
  }

  public void setHeight(float paramFloat)
  {
    this.bBox.setBottom(0.0F);
    this.bBox.setTop(paramFloat);
  }

  public float getWidth()
  {
    return this.bBox.getWidth();
  }

  public float getHeight()
  {
    return this.bBox.getHeight();
  }

  public Rectangle getBoundingBox()
  {
    return this.bBox;
  }

  public void setBoundingBox(Rectangle paramRectangle)
  {
    this.bBox = paramRectangle;
  }

  public void setLayer(PdfOCG paramPdfOCG)
  {
    this.layer = paramPdfOCG;
  }

  public PdfOCG getLayer()
  {
    return this.layer;
  }

  public void setMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.matrix = new PdfArray();
    this.matrix.add(new PdfNumber(paramFloat1));
    this.matrix.add(new PdfNumber(paramFloat2));
    this.matrix.add(new PdfNumber(paramFloat3));
    this.matrix.add(new PdfNumber(paramFloat4));
    this.matrix.add(new PdfNumber(paramFloat5));
    this.matrix.add(new PdfNumber(paramFloat6));
  }

  PdfArray getMatrix()
  {
    return this.matrix;
  }

  public PdfIndirectReference getIndirectReference()
  {
    if (this.thisReference == null)
      this.thisReference = this.writer.getPdfIndirectReference();
    return this.thisReference;
  }

  public void beginVariableText()
  {
    this.content.append("/Tx BMC ");
  }

  public void endVariableText()
  {
    this.content.append("EMC ");
  }

  PdfObject getResources()
  {
    return getPageResources().getResources();
  }

  PdfStream getFormXObject(int paramInt)
    throws IOException
  {
    return new PdfFormXObject(this, paramInt);
  }

  public PdfContentByte getDuplicate()
  {
    PdfTemplate localPdfTemplate = new PdfTemplate();
    localPdfTemplate.writer = this.writer;
    localPdfTemplate.pdf = this.pdf;
    localPdfTemplate.thisReference = this.thisReference;
    localPdfTemplate.pageResources = this.pageResources;
    localPdfTemplate.bBox = new Rectangle(this.bBox);
    localPdfTemplate.group = this.group;
    localPdfTemplate.layer = this.layer;
    if (this.matrix != null)
      localPdfTemplate.matrix = new PdfArray(this.matrix);
    localPdfTemplate.separator = this.separator;
    return localPdfTemplate;
  }

  public int getType()
  {
    return this.type;
  }

  PageResources getPageResources()
  {
    return this.pageResources;
  }

  public PdfTransparencyGroup getGroup()
  {
    return this.group;
  }

  public void setGroup(PdfTransparencyGroup paramPdfTransparencyGroup)
  {
    this.group = paramPdfTransparencyGroup;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfTemplate
 * JD-Core Version:    0.6.0
 */