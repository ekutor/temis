package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfIndirectObject
{
  protected int number;
  protected int generation = 0;
  static final byte[] STARTOBJ = DocWriter.getISOBytes(" obj\n");
  static final byte[] ENDOBJ = DocWriter.getISOBytes("\nendobj\n");
  static final int SIZEOBJ = STARTOBJ.length + ENDOBJ.length;
  PdfObject object;
  PdfWriter writer;

  PdfIndirectObject(int paramInt, PdfObject paramPdfObject, PdfWriter paramPdfWriter)
  {
    this(paramInt, 0, paramPdfObject, paramPdfWriter);
  }

  PdfIndirectObject(PdfIndirectReference paramPdfIndirectReference, PdfObject paramPdfObject, PdfWriter paramPdfWriter)
  {
    this(paramPdfIndirectReference.getNumber(), paramPdfIndirectReference.getGeneration(), paramPdfObject, paramPdfWriter);
  }

  PdfIndirectObject(int paramInt1, int paramInt2, PdfObject paramPdfObject, PdfWriter paramPdfWriter)
  {
    this.writer = paramPdfWriter;
    this.number = paramInt1;
    this.generation = paramInt2;
    this.object = paramPdfObject;
    PdfEncryption localPdfEncryption = null;
    if (paramPdfWriter != null)
      localPdfEncryption = paramPdfWriter.getEncryption();
    if (localPdfEncryption != null)
      localPdfEncryption.setHashKey(paramInt1, paramInt2);
  }

  public PdfIndirectReference getIndirectReference()
  {
    return new PdfIndirectReference(this.object.type(), this.number, this.generation);
  }

  void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(DocWriter.getISOBytes(String.valueOf(this.number)));
    paramOutputStream.write(32);
    paramOutputStream.write(DocWriter.getISOBytes(String.valueOf(this.generation)));
    paramOutputStream.write(STARTOBJ);
    this.object.toPdf(this.writer, paramOutputStream);
    paramOutputStream.write(ENDOBJ);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfIndirectObject
 * JD-Core Version:    0.6.0
 */