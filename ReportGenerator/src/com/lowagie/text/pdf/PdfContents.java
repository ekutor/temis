package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class PdfContents extends PdfStream
{
  static final byte[] SAVESTATE = DocWriter.getISOBytes("q\n");
  static final byte[] RESTORESTATE = DocWriter.getISOBytes("Q\n");
  static final byte[] ROTATE90 = DocWriter.getISOBytes("0 1 -1 0 ");
  static final byte[] ROTATE180 = DocWriter.getISOBytes("-1 0 0 -1 ");
  static final byte[] ROTATE270 = DocWriter.getISOBytes("0 -1 1 0 ");
  static final byte[] ROTATEFINAL = DocWriter.getISOBytes(" cm\n");

  PdfContents(PdfContentByte paramPdfContentByte1, PdfContentByte paramPdfContentByte2, PdfContentByte paramPdfContentByte3, PdfContentByte paramPdfContentByte4, Rectangle paramRectangle)
    throws BadPdfFormatException
  {
    try
    {
      Object localObject = null;
      Deflater localDeflater = null;
      this.streamBytes = new ByteArrayOutputStream();
      if (Document.compress)
      {
        this.compressed = true;
        this.compressionLevel = paramPdfContentByte3.getPdfWriter().getCompressionLevel();
        localDeflater = new Deflater(this.compressionLevel);
        localObject = new DeflaterOutputStream(this.streamBytes, localDeflater);
      }
      else
      {
        localObject = this.streamBytes;
      }
      int i = paramRectangle.getRotation();
      switch (i)
      {
      case 90:
        ((OutputStream)localObject).write(ROTATE90);
        ((OutputStream)localObject).write(DocWriter.getISOBytes(ByteBuffer.formatDouble(paramRectangle.getTop())));
        ((OutputStream)localObject).write(32);
        ((OutputStream)localObject).write(48);
        ((OutputStream)localObject).write(ROTATEFINAL);
        break;
      case 180:
        ((OutputStream)localObject).write(ROTATE180);
        ((OutputStream)localObject).write(DocWriter.getISOBytes(ByteBuffer.formatDouble(paramRectangle.getRight())));
        ((OutputStream)localObject).write(32);
        ((OutputStream)localObject).write(DocWriter.getISOBytes(ByteBuffer.formatDouble(paramRectangle.getTop())));
        ((OutputStream)localObject).write(ROTATEFINAL);
        break;
      case 270:
        ((OutputStream)localObject).write(ROTATE270);
        ((OutputStream)localObject).write(48);
        ((OutputStream)localObject).write(32);
        ((OutputStream)localObject).write(DocWriter.getISOBytes(ByteBuffer.formatDouble(paramRectangle.getRight())));
        ((OutputStream)localObject).write(ROTATEFINAL);
      }
      if (paramPdfContentByte1.size() > 0)
      {
        ((OutputStream)localObject).write(SAVESTATE);
        paramPdfContentByte1.getInternalBuffer().writeTo((OutputStream)localObject);
        ((OutputStream)localObject).write(RESTORESTATE);
      }
      if (paramPdfContentByte2.size() > 0)
      {
        ((OutputStream)localObject).write(SAVESTATE);
        paramPdfContentByte2.getInternalBuffer().writeTo((OutputStream)localObject);
        ((OutputStream)localObject).write(RESTORESTATE);
      }
      if (paramPdfContentByte3 != null)
      {
        ((OutputStream)localObject).write(SAVESTATE);
        paramPdfContentByte3.getInternalBuffer().writeTo((OutputStream)localObject);
        ((OutputStream)localObject).write(RESTORESTATE);
      }
      if (paramPdfContentByte4.size() > 0)
        paramPdfContentByte4.getInternalBuffer().writeTo((OutputStream)localObject);
      ((OutputStream)localObject).close();
      if (localDeflater != null)
        localDeflater.end();
    }
    catch (Exception localException)
    {
      throw new BadPdfFormatException(localException.getMessage());
    }
    put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
    if (this.compressed)
      put(PdfName.FILTER, PdfName.FLATEDECODE);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfContents
 * JD-Core Version:    0.6.0
 */