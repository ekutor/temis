package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfStream extends PdfDictionary
{
  public static final int DEFAULT_COMPRESSION = -1;
  public static final int NO_COMPRESSION = 0;
  public static final int BEST_SPEED = 1;
  public static final int BEST_COMPRESSION = 9;
  protected boolean compressed = false;
  protected int compressionLevel = 0;
  protected ByteArrayOutputStream streamBytes = null;
  protected InputStream inputStream;
  protected PdfIndirectReference ref;
  protected int inputStreamLength = -1;
  protected PdfWriter writer;
  protected int rawLength;
  static final byte[] STARTSTREAM = DocWriter.getISOBytes("stream\n");
  static final byte[] ENDSTREAM = DocWriter.getISOBytes("\nendstream");
  static final int SIZESTREAM = STARTSTREAM.length + ENDSTREAM.length;

  public PdfStream(byte[] paramArrayOfByte)
  {
    this.type = 7;
    this.bytes = paramArrayOfByte;
    this.rawLength = paramArrayOfByte.length;
    put(PdfName.LENGTH, new PdfNumber(paramArrayOfByte.length));
  }

  public PdfStream(InputStream paramInputStream, PdfWriter paramPdfWriter)
  {
    this.type = 7;
    this.inputStream = paramInputStream;
    this.writer = paramPdfWriter;
    this.ref = paramPdfWriter.getPdfIndirectReference();
    put(PdfName.LENGTH, this.ref);
  }

  protected PdfStream()
  {
    this.type = 7;
  }

  public void writeLength()
    throws IOException
  {
    if (this.inputStream == null)
      throw new UnsupportedOperationException("writeLength() can only be called in a contructed PdfStream(InputStream,PdfWriter).");
    if (this.inputStreamLength == -1)
      throw new IOException("writeLength() can only be called after output of the stream body.");
    this.writer.addToBody(new PdfNumber(this.inputStreamLength), this.ref, false);
  }

  public int getRawLength()
  {
    return this.rawLength;
  }

  public void flateCompress()
  {
    flateCompress(-1);
  }

  public void flateCompress(int paramInt)
  {
    if (!Document.compress)
      return;
    if (this.compressed)
      return;
    this.compressionLevel = paramInt;
    if (this.inputStream != null)
    {
      this.compressed = true;
      return;
    }
    PdfObject localPdfObject = PdfReader.getPdfObject(get(PdfName.FILTER));
    if (localPdfObject != null)
      if (localPdfObject.isName())
      {
        if (PdfName.FLATEDECODE.equals(localPdfObject))
          return;
      }
      else if (localPdfObject.isArray())
      {
        if (((PdfArray)localPdfObject).contains(PdfName.FLATEDECODE))
          return;
      }
      else
        throw new RuntimeException("Stream could not be compressed: filter is not a name or array.");
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      Deflater localDeflater = new Deflater(paramInt);
      DeflaterOutputStream localDeflaterOutputStream = new DeflaterOutputStream(localByteArrayOutputStream, localDeflater);
      if (this.streamBytes != null)
        this.streamBytes.writeTo(localDeflaterOutputStream);
      else
        localDeflaterOutputStream.write(this.bytes);
      localDeflaterOutputStream.close();
      localDeflater.end();
      this.streamBytes = localByteArrayOutputStream;
      this.bytes = null;
      put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
      if (localPdfObject == null)
      {
        put(PdfName.FILTER, PdfName.FLATEDECODE);
      }
      else
      {
        PdfArray localPdfArray = new PdfArray(localPdfObject);
        localPdfArray.add(PdfName.FLATEDECODE);
        put(PdfName.FILTER, localPdfArray);
      }
      this.compressed = true;
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  protected void superToPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    super.toPdf(paramPdfWriter, paramOutputStream);
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    if ((this.inputStream != null) && (this.compressed))
      put(PdfName.FILTER, PdfName.FLATEDECODE);
    PdfEncryption localPdfEncryption = null;
    if (paramPdfWriter != null)
      localPdfEncryption = paramPdfWriter.getEncryption();
    if (localPdfEncryption != null)
    {
      localPdfObject = get(PdfName.FILTER);
      if (localPdfObject != null)
        if (PdfName.CRYPT.equals(localPdfObject))
        {
          localPdfEncryption = null;
        }
        else if (localPdfObject.isArray())
        {
          PdfArray localPdfArray = (PdfArray)localPdfObject;
          if ((!localPdfArray.isEmpty()) && (PdfName.CRYPT.equals(localPdfArray.getPdfObject(0))))
            localPdfEncryption = null;
        }
    }
    PdfObject localPdfObject = get(PdfName.LENGTH);
    if ((localPdfEncryption != null) && (localPdfObject != null) && (localPdfObject.isNumber()))
    {
      int i = ((PdfNumber)localPdfObject).intValue();
      put(PdfName.LENGTH, new PdfNumber(localPdfEncryption.calculateStreamSize(i)));
      superToPdf(paramPdfWriter, paramOutputStream);
      put(PdfName.LENGTH, localPdfObject);
    }
    else
    {
      superToPdf(paramPdfWriter, paramOutputStream);
    }
    paramOutputStream.write(STARTSTREAM);
    Object localObject1;
    if (this.inputStream != null)
    {
      this.rawLength = 0;
      localObject1 = null;
      OutputStreamCounter localOutputStreamCounter = new OutputStreamCounter(paramOutputStream);
      OutputStreamEncryption localOutputStreamEncryption = null;
      Object localObject2 = localOutputStreamCounter;
      if ((localPdfEncryption != null) && (!localPdfEncryption.isEmbeddedFilesOnly()))
        localObject2 = localOutputStreamEncryption = localPdfEncryption.getEncryptionStream((OutputStream)localObject2);
      Deflater localDeflater = null;
      if (this.compressed)
      {
        localDeflater = new Deflater(this.compressionLevel);
        localObject2 = localObject1 = new DeflaterOutputStream((OutputStream)localObject2, localDeflater, 32768);
      }
      byte[] arrayOfByte = new byte[4192];
      while (true)
      {
        int j = this.inputStream.read(arrayOfByte);
        if (j <= 0)
          break;
        ((OutputStream)localObject2).write(arrayOfByte, 0, j);
        this.rawLength += j;
      }
      if (localObject1 != null)
      {
        ((DeflaterOutputStream)localObject1).finish();
        localDeflater.end();
      }
      if (localOutputStreamEncryption != null)
        localOutputStreamEncryption.finish();
      this.inputStreamLength = localOutputStreamCounter.getCounter();
    }
    else if ((localPdfEncryption != null) && (!localPdfEncryption.isEmbeddedFilesOnly()))
    {
      if (this.streamBytes != null)
        localObject1 = localPdfEncryption.encryptByteArray(this.streamBytes.toByteArray());
      else
        localObject1 = localPdfEncryption.encryptByteArray(this.bytes);
      paramOutputStream.write(localObject1);
    }
    else if (this.streamBytes != null)
    {
      this.streamBytes.writeTo(paramOutputStream);
    }
    else
    {
      paramOutputStream.write(this.bytes);
    }
    paramOutputStream.write(ENDSTREAM);
  }

  public void writeContent(OutputStream paramOutputStream)
    throws IOException
  {
    if (this.streamBytes != null)
      this.streamBytes.writeTo(paramOutputStream);
    else if (this.bytes != null)
      paramOutputStream.write(this.bytes);
  }

  public String toString()
  {
    if (get(PdfName.TYPE) == null)
      return "Stream";
    return "Stream of type: " + get(PdfName.TYPE);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfStream
 * JD-Core Version:    0.6.0
 */