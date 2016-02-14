package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PRStream extends PdfStream
{
  protected PdfReader reader;
  protected int offset;
  protected int length;
  protected int objNum = 0;
  protected int objGen = 0;

  public PRStream(PRStream paramPRStream, PdfDictionary paramPdfDictionary)
  {
    this.reader = paramPRStream.reader;
    this.offset = paramPRStream.offset;
    this.length = paramPRStream.length;
    this.compressed = paramPRStream.compressed;
    this.compressionLevel = paramPRStream.compressionLevel;
    this.streamBytes = paramPRStream.streamBytes;
    this.bytes = paramPRStream.bytes;
    this.objNum = paramPRStream.objNum;
    this.objGen = paramPRStream.objGen;
    if (paramPdfDictionary != null)
      putAll(paramPdfDictionary);
    else
      this.hashMap.putAll(paramPRStream.hashMap);
  }

  public PRStream(PRStream paramPRStream, PdfDictionary paramPdfDictionary, PdfReader paramPdfReader)
  {
    this(paramPRStream, paramPdfDictionary);
    this.reader = paramPdfReader;
  }

  public PRStream(PdfReader paramPdfReader, int paramInt)
  {
    this.reader = paramPdfReader;
    this.offset = paramInt;
  }

  public PRStream(PdfReader paramPdfReader, byte[] paramArrayOfByte)
  {
    this(paramPdfReader, paramArrayOfByte, -1);
  }

  public PRStream(PdfReader paramPdfReader, byte[] paramArrayOfByte, int paramInt)
  {
    this.reader = paramPdfReader;
    this.offset = -1;
    if (Document.compress)
    {
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        Deflater localDeflater = new Deflater(paramInt);
        DeflaterOutputStream localDeflaterOutputStream = new DeflaterOutputStream(localByteArrayOutputStream, localDeflater);
        localDeflaterOutputStream.write(paramArrayOfByte);
        localDeflaterOutputStream.close();
        localDeflater.end();
        this.bytes = localByteArrayOutputStream.toByteArray();
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
      put(PdfName.FILTER, PdfName.FLATEDECODE);
    }
    else
    {
      this.bytes = paramArrayOfByte;
    }
    setLength(this.bytes.length);
  }

  public void setData(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    setData(paramArrayOfByte, paramBoolean, -1);
  }

  public void setData(byte[] paramArrayOfByte, boolean paramBoolean, int paramInt)
  {
    remove(PdfName.FILTER);
    this.offset = -1;
    if ((Document.compress) && (paramBoolean))
    {
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        Deflater localDeflater = new Deflater(paramInt);
        DeflaterOutputStream localDeflaterOutputStream = new DeflaterOutputStream(localByteArrayOutputStream, localDeflater);
        localDeflaterOutputStream.write(paramArrayOfByte);
        localDeflaterOutputStream.close();
        localDeflater.end();
        this.bytes = localByteArrayOutputStream.toByteArray();
        this.compressionLevel = paramInt;
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
      put(PdfName.FILTER, PdfName.FLATEDECODE);
    }
    else
    {
      this.bytes = paramArrayOfByte;
    }
    setLength(this.bytes.length);
  }

  public void setData(byte[] paramArrayOfByte)
  {
    setData(paramArrayOfByte, true);
  }

  public void setLength(int paramInt)
  {
    this.length = paramInt;
    put(PdfName.LENGTH, new PdfNumber(paramInt));
  }

  public int getOffset()
  {
    return this.offset;
  }

  public int getLength()
  {
    return this.length;
  }

  public PdfReader getReader()
  {
    return this.reader;
  }

  public byte[] getBytes()
  {
    return this.bytes;
  }

  public void setObjNum(int paramInt1, int paramInt2)
  {
    this.objNum = paramInt1;
    this.objGen = paramInt2;
  }

  int getObjNum()
  {
    return this.objNum;
  }

  int getObjGen()
  {
    return this.objGen;
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = PdfReader.getStreamBytesRaw(this);
    PdfEncryption localPdfEncryption = null;
    if (paramPdfWriter != null)
      localPdfEncryption = paramPdfWriter.getEncryption();
    PdfObject localPdfObject = get(PdfName.LENGTH);
    int i = arrayOfByte.length;
    if (localPdfEncryption != null)
      i = localPdfEncryption.calculateStreamSize(i);
    put(PdfName.LENGTH, new PdfNumber(i));
    superToPdf(paramPdfWriter, paramOutputStream);
    put(PdfName.LENGTH, localPdfObject);
    paramOutputStream.write(STARTSTREAM);
    if (this.length > 0)
    {
      if ((localPdfEncryption != null) && (!localPdfEncryption.isEmbeddedFilesOnly()))
        arrayOfByte = localPdfEncryption.encryptByteArray(arrayOfByte);
      paramOutputStream.write(arrayOfByte);
    }
    paramOutputStream.write(ENDSTREAM);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PRStream
 * JD-Core Version:    0.6.0
 */