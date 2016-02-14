package com.lowagie.text;

import com.lowagie.text.pdf.PdfTemplate;
import java.net.URL;

public class ImgTemplate extends Image
{
  ImgTemplate(Image paramImage)
  {
    super(paramImage);
  }

  public ImgTemplate(PdfTemplate paramPdfTemplate)
    throws BadElementException
  {
    super((URL)null);
    if (paramPdfTemplate == null)
      throw new BadElementException("The template can not be null.");
    if (paramPdfTemplate.getType() == 3)
      throw new BadElementException("A pattern can not be used as a template to create an image.");
    this.type = 35;
    this.scaledHeight = paramPdfTemplate.getHeight();
    setTop(this.scaledHeight);
    this.scaledWidth = paramPdfTemplate.getWidth();
    setRight(this.scaledWidth);
    setTemplateData(paramPdfTemplate);
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ImgTemplate
 * JD-Core Version:    0.6.0
 */