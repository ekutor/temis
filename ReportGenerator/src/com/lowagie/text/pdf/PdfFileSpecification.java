package com.lowagie.text.pdf;

import com.lowagie.text.pdf.collection.PdfCollectionItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PdfFileSpecification extends PdfDictionary
{
  protected PdfWriter writer;
  protected PdfIndirectReference ref;

  public PdfFileSpecification()
  {
    super(PdfName.FILESPEC);
  }

  public static PdfFileSpecification url(PdfWriter paramPdfWriter, String paramString)
  {
    PdfFileSpecification localPdfFileSpecification = new PdfFileSpecification();
    localPdfFileSpecification.writer = paramPdfWriter;
    localPdfFileSpecification.put(PdfName.FS, PdfName.URL);
    localPdfFileSpecification.put(PdfName.F, new PdfString(paramString));
    return localPdfFileSpecification;
  }

  public static PdfFileSpecification fileEmbedded(PdfWriter paramPdfWriter, String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws IOException
  {
    return fileEmbedded(paramPdfWriter, paramString1, paramString2, paramArrayOfByte, 9);
  }

  public static PdfFileSpecification fileEmbedded(PdfWriter paramPdfWriter, String paramString1, String paramString2, byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    return fileEmbedded(paramPdfWriter, paramString1, paramString2, paramArrayOfByte, null, null, paramInt);
  }

  public static PdfFileSpecification fileEmbedded(PdfWriter paramPdfWriter, String paramString1, String paramString2, byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    return fileEmbedded(paramPdfWriter, paramString1, paramString2, paramArrayOfByte, null, null, paramBoolean ? 9 : 0);
  }

  public static PdfFileSpecification fileEmbedded(PdfWriter paramPdfWriter, String paramString1, String paramString2, byte[] paramArrayOfByte, boolean paramBoolean, String paramString3, PdfDictionary paramPdfDictionary)
    throws IOException
  {
    return fileEmbedded(paramPdfWriter, paramString1, paramString2, paramArrayOfByte, null, null, paramBoolean ? 9 : 0);
  }

  public static PdfFileSpecification fileEmbedded(PdfWriter paramPdfWriter, String paramString1, String paramString2, byte[] paramArrayOfByte, String paramString3, PdfDictionary paramPdfDictionary, int paramInt)
    throws IOException
  {
    PdfFileSpecification localPdfFileSpecification = new PdfFileSpecification();
    localPdfFileSpecification.writer = paramPdfWriter;
    localPdfFileSpecification.put(PdfName.F, new PdfString(paramString2));
    localPdfFileSpecification.setUnicodeFileName(paramString2, false);
    Object localObject1 = null;
    PdfIndirectReference localPdfIndirectReference1;
    try
    {
      PdfIndirectReference localPdfIndirectReference2 = paramPdfWriter.getPdfIndirectReference();
      PdfEFStream localPdfEFStream;
      if (paramArrayOfByte == null)
      {
        localObject2 = new File(paramString1);
        if (((File)localObject2).canRead())
        {
          localObject1 = new FileInputStream(paramString1);
        }
        else if ((paramString1.startsWith("file:/")) || (paramString1.startsWith("http://")) || (paramString1.startsWith("https://")) || (paramString1.startsWith("jar:")))
        {
          localObject1 = new URL(paramString1).openStream();
        }
        else
        {
          localObject1 = BaseFont.getResourceStream(paramString1);
          if (localObject1 == null)
            throw new IOException(paramString1 + " not found as file or resource.");
        }
        localPdfEFStream = new PdfEFStream((InputStream)localObject1, paramPdfWriter);
      }
      else
      {
        localPdfEFStream = new PdfEFStream(paramArrayOfByte);
      }
      localPdfEFStream.put(PdfName.TYPE, PdfName.EMBEDDEDFILE);
      localPdfEFStream.flateCompress(paramInt);
      localPdfEFStream.put(PdfName.PARAMS, localPdfIndirectReference2);
      if (paramString3 != null)
        localPdfEFStream.put(PdfName.SUBTYPE, new PdfName(paramString3));
      localPdfIndirectReference1 = paramPdfWriter.addToBody(localPdfEFStream).getIndirectReference();
      if (paramArrayOfByte == null)
        localPdfEFStream.writeLength();
      localObject2 = new PdfDictionary();
      if (paramPdfDictionary != null)
        ((PdfDictionary)localObject2).merge(paramPdfDictionary);
      ((PdfDictionary)localObject2).put(PdfName.SIZE, new PdfNumber(localPdfEFStream.getRawLength()));
      paramPdfWriter.addToBody((PdfObject)localObject2, localPdfIndirectReference2);
    }
    finally
    {
      if (localObject1 != null)
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (Exception localException)
        {
        }
    }
    Object localObject2 = new PdfDictionary();
    ((PdfDictionary)localObject2).put(PdfName.F, localPdfIndirectReference1);
    ((PdfDictionary)localObject2).put(PdfName.UF, localPdfIndirectReference1);
    localPdfFileSpecification.put(PdfName.EF, (PdfObject)localObject2);
    return (PdfFileSpecification)(PdfFileSpecification)localPdfFileSpecification;
  }

  public static PdfFileSpecification fileExtern(PdfWriter paramPdfWriter, String paramString)
  {
    PdfFileSpecification localPdfFileSpecification = new PdfFileSpecification();
    localPdfFileSpecification.writer = paramPdfWriter;
    localPdfFileSpecification.put(PdfName.F, new PdfString(paramString));
    localPdfFileSpecification.setUnicodeFileName(paramString, false);
    return localPdfFileSpecification;
  }

  public PdfIndirectReference getReference()
    throws IOException
  {
    if (this.ref != null)
      return this.ref;
    this.ref = this.writer.addToBody(this).getIndirectReference();
    return this.ref;
  }

  public void setMultiByteFileName(byte[] paramArrayOfByte)
  {
    put(PdfName.F, new PdfString(paramArrayOfByte).setHexWriting(true));
  }

  public void setUnicodeFileName(String paramString, boolean paramBoolean)
  {
    put(PdfName.UF, new PdfString(paramString, paramBoolean ? "UnicodeBig" : "PDF"));
  }

  public void setVolatile(boolean paramBoolean)
  {
    put(PdfName.V, new PdfBoolean(paramBoolean));
  }

  public void addDescription(String paramString, boolean paramBoolean)
  {
    put(PdfName.DESC, new PdfString(paramString, paramBoolean ? "UnicodeBig" : "PDF"));
  }

  public void addCollectionItem(PdfCollectionItem paramPdfCollectionItem)
  {
    put(PdfName.CI, paramPdfCollectionItem);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfFileSpecification
 * JD-Core Version:    0.6.0
 */