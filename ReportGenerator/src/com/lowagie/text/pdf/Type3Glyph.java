package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;

public final class Type3Glyph extends PdfContentByte
{
  private PageResources pageResources;
  private boolean colorized;

  private Type3Glyph()
  {
    super(null);
  }

  Type3Glyph(PdfWriter paramPdfWriter, PageResources paramPageResources, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, boolean paramBoolean)
  {
    super(paramPdfWriter);
    this.pageResources = paramPageResources;
    this.colorized = paramBoolean;
    if (paramBoolean)
      this.content.append(paramFloat1).append(" 0 d0\n");
    else
      this.content.append(paramFloat1).append(" 0 ").append(paramFloat2).append(' ').append(paramFloat3).append(' ').append(paramFloat4).append(' ').append(paramFloat5).append(" d1\n");
  }

  PageResources getPageResources()
  {
    return this.pageResources;
  }

  public void addImage(Image paramImage, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, boolean paramBoolean)
    throws DocumentException
  {
    if ((!this.colorized) && ((!paramImage.isMask()) || ((paramImage.getBpc() != 1) && (paramImage.getBpc() <= 255))))
      throw new DocumentException("Not colorized Typed3 fonts only accept mask images.");
    super.addImage(paramImage, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramBoolean);
  }

  public PdfContentByte getDuplicate()
  {
    Type3Glyph localType3Glyph = new Type3Glyph();
    localType3Glyph.writer = this.writer;
    localType3Glyph.pdf = this.pdf;
    localType3Glyph.pageResources = this.pageResources;
    localType3Glyph.colorized = this.colorized;
    return localType3Glyph;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.Type3Glyph
 * JD-Core Version:    0.6.0
 */