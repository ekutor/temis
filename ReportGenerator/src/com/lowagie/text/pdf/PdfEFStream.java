package com.lowagie.text.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfEFStream extends PdfStream
{
  public PdfEFStream(InputStream paramInputStream, PdfWriter paramPdfWriter)
  {
    super(paramInputStream, paramPdfWriter);
  }

  public PdfEFStream(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    if ((this.inputStream != null) && (this.compressed))
      put(PdfName.FILTER, PdfName.FLATEDECODE);
    PdfEncryption localPdfEncryption = null;
    if (paramPdfWriter != null)
      localPdfEncryption = paramPdfWriter.getEncryption();
    PdfArray localPdfArray;
    if (localPdfEncryption != null)
    {
      localObject1 = get(PdfName.FILTER);
      if (localObject1 != null)
        if (PdfName.CRYPT.equals(localObject1))
        {
          localPdfEncryption = null;
        }
        else if (((PdfObject)localObject1).isArray())
        {
          localPdfArray = (PdfArray)localObject1;
          if ((!localPdfArray.isEmpty()) && (PdfName.CRYPT.equals(localPdfArray.getPdfObject(0))))
            localPdfEncryption = null;
        }
    }
    Object localObject3;
    if ((localPdfEncryption != null) && (localPdfEncryption.isEmbeddedFilesOnly()))
    {
      localObject1 = new PdfArray();
      localPdfArray = new PdfArray();
      localObject3 = new PdfDictionary();
      ((PdfDictionary)localObject3).put(PdfName.NAME, PdfName.STDCF);
      ((PdfArray)localObject1).add(PdfName.CRYPT);
      localPdfArray.add((PdfObject)localObject3);
      if (this.compressed)
      {
        ((PdfArray)localObject1).add(PdfName.FLATEDECODE);
        localPdfArray.add(new PdfNull());
      }
      put(PdfName.FILTER, (PdfObject)localObject1);
      put(PdfName.DECODEPARMS, localPdfArray);
    }
    Object localObject1 = get(PdfName.LENGTH);
    if ((localPdfEncryption != null) && (localObject1 != null) && (((PdfObject)localObject1).isNumber()))
    {
      int i = ((PdfNumber)localObject1).intValue();
      put(PdfName.LENGTH, new PdfNumber(localPdfEncryption.calculateStreamSize(i)));
      superToPdf(paramPdfWriter, paramOutputStream);
      put(PdfName.LENGTH, (PdfObject)localObject1);
    }
    else
    {
      superToPdf(paramPdfWriter, paramOutputStream);
    }
    paramOutputStream.write(STARTSTREAM);
    Object localObject2;
    if (this.inputStream != null)
    {
      this.rawLength = 0;
      localObject2 = null;
      localObject3 = new OutputStreamCounter(paramOutputStream);
      OutputStreamEncryption localOutputStreamEncryption = null;
      Object localObject4 = localObject3;
      if (localPdfEncryption != null)
        localObject4 = localOutputStreamEncryption = localPdfEncryption.getEncryptionStream((OutputStream)localObject4);
      Deflater localDeflater = null;
      if (this.compressed)
      {
        localDeflater = new Deflater(this.compressionLevel);
        localObject4 = localObject2 = new DeflaterOutputStream((OutputStream)localObject4, localDeflater, 32768);
      }
      byte[] arrayOfByte = new byte[4192];
      while (true)
      {
        int j = this.inputStream.read(arrayOfByte);
        if (j <= 0)
          break;
        ((OutputStream)localObject4).write(arrayOfByte, 0, j);
        this.rawLength += j;
      }
      if (localObject2 != null)
      {
        ((DeflaterOutputStream)localObject2).finish();
        localDeflater.end();
      }
      if (localOutputStreamEncryption != null)
        localOutputStreamEncryption.finish();
      this.inputStreamLength = ((OutputStreamCounter)localObject3).getCounter();
    }
    else if (localPdfEncryption == null)
    {
      if (this.streamBytes != null)
        this.streamBytes.writeTo(paramOutputStream);
      else
        paramOutputStream.write(this.bytes);
    }
    else
    {
      if (this.streamBytes != null)
        localObject2 = localPdfEncryption.encryptByteArray(this.streamBytes.toByteArray());
      else
        localObject2 = localPdfEncryption.encryptByteArray(this.bytes);
      paramOutputStream.write(localObject2);
    }
    paramOutputStream.write(ENDSTREAM);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfEFStream
 * JD-Core Version:    0.6.0
 */