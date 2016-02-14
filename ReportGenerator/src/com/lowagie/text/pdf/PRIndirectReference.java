package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PRIndirectReference extends PdfIndirectReference
{
  protected PdfReader reader;

  PRIndirectReference(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    this.type = 10;
    this.number = paramInt1;
    this.generation = paramInt2;
    this.reader = paramPdfReader;
  }

  PRIndirectReference(PdfReader paramPdfReader, int paramInt)
  {
    this(paramPdfReader, paramInt, 0);
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramPdfWriter.getNewObjectNumber(this.reader, this.number, this.generation);
    paramOutputStream.write(PdfEncodings.convertToBytes(i + " 0 R", null));
  }

  public PdfReader getReader()
  {
    return this.reader;
  }

  public void setNumber(int paramInt1, int paramInt2)
  {
    this.number = paramInt1;
    this.generation = paramInt2;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PRIndirectReference
 * JD-Core Version:    0.6.0
 */