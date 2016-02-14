package com.lowagie.text.pdf;

import java.io.IOException;

public class PdfPSXObject extends PdfTemplate
{
  protected PdfPSXObject()
  {
  }

  public PdfPSXObject(PdfWriter paramPdfWriter)
  {
    super(paramPdfWriter);
  }

  PdfStream getFormXObject(int paramInt)
    throws IOException
  {
    PdfStream localPdfStream = new PdfStream(this.content.toByteArray());
    localPdfStream.put(PdfName.TYPE, PdfName.XOBJECT);
    localPdfStream.put(PdfName.SUBTYPE, PdfName.PS);
    localPdfStream.flateCompress(paramInt);
    return localPdfStream;
  }

  public PdfContentByte getDuplicate()
  {
    PdfPSXObject localPdfPSXObject = new PdfPSXObject();
    localPdfPSXObject.writer = this.writer;
    localPdfPSXObject.pdf = this.pdf;
    localPdfPSXObject.thisReference = this.thisReference;
    localPdfPSXObject.pageResources = this.pageResources;
    localPdfPSXObject.separator = this.separator;
    return localPdfPSXObject;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPSXObject
 * JD-Core Version:    0.6.0
 */