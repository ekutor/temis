package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.io.IOException;

public class PdfImportedPage extends PdfTemplate
{
  PdfReaderInstance readerInstance;
  int pageNumber;

  PdfImportedPage(PdfReaderInstance paramPdfReaderInstance, PdfWriter paramPdfWriter, int paramInt)
  {
    this.readerInstance = paramPdfReaderInstance;
    this.pageNumber = paramInt;
    this.writer = paramPdfWriter;
    this.bBox = paramPdfReaderInstance.getReader().getPageSize(paramInt);
    setMatrix(1.0F, 0.0F, 0.0F, 1.0F, -this.bBox.getLeft(), -this.bBox.getBottom());
    this.type = 2;
  }

  public PdfImportedPage getFromReader()
  {
    return this;
  }

  public int getPageNumber()
  {
    return this.pageNumber;
  }

  public void addImage(Image paramImage, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    throws DocumentException
  {
    throwError();
  }

  public void addTemplate(PdfTemplate paramPdfTemplate, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    throwError();
  }

  public PdfContentByte getDuplicate()
  {
    throwError();
    return null;
  }

  PdfStream getFormXObject(int paramInt)
    throws IOException
  {
    return this.readerInstance.getFormXObject(this.pageNumber, paramInt);
  }

  public void setColorFill(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    throwError();
  }

  public void setColorStroke(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    throwError();
  }

  PdfObject getResources()
  {
    return this.readerInstance.getResources(this.pageNumber);
  }

  public void setFontAndSize(BaseFont paramBaseFont, float paramFloat)
  {
    throwError();
  }

  public void setGroup(PdfTransparencyGroup paramPdfTransparencyGroup)
  {
    throwError();
  }

  void throwError()
  {
    throw new RuntimeException("Content can not be added to a PdfImportedPage.");
  }

  PdfReaderInstance getPdfReaderInstance()
  {
    return this.readerInstance;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfImportedPage
 * JD-Core Version:    0.6.0
 */