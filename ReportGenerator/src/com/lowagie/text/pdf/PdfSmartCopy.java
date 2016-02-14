package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PdfSmartCopy extends PdfCopy
{
  private HashMap streamMap = null;

  public PdfSmartCopy(Document paramDocument, OutputStream paramOutputStream)
    throws DocumentException
  {
    super(paramDocument, paramOutputStream);
  }

  protected PdfIndirectReference copyIndirect(PRIndirectReference paramPRIndirectReference)
    throws IOException, BadPdfFormatException
  {
    PdfObject localPdfObject1 = PdfReader.getPdfObjectRelease(paramPRIndirectReference);
    ByteStore localByteStore = null;
    int i = 0;
    PdfIndirectReference localPdfIndirectReference;
    if (localPdfObject1.isStream())
    {
      localByteStore = new ByteStore((PRStream)localPdfObject1);
      i = 1;
      localPdfIndirectReference = (PdfIndirectReference)this.streamMap.get(localByteStore);
      if (localPdfIndirectReference != null)
        return localPdfIndirectReference;
    }
    PdfCopy.RefKey localRefKey = new PdfCopy.RefKey(paramPRIndirectReference);
    PdfCopy.IndirectReferences localIndirectReferences = (PdfCopy.IndirectReferences)this.indirects.get(localRefKey);
    if (localIndirectReferences != null)
    {
      localPdfIndirectReference = localIndirectReferences.getRef();
      if (localIndirectReferences.getCopied())
        return localPdfIndirectReference;
    }
    else
    {
      localPdfIndirectReference = this.body.getPdfIndirectReference();
      localIndirectReferences = new PdfCopy.IndirectReferences(localPdfIndirectReference);
      this.indirects.put(localRefKey, localIndirectReferences);
    }
    if (localPdfObject1.isDictionary())
    {
      localPdfObject2 = PdfReader.getPdfObjectRelease(((PdfDictionary)localPdfObject1).get(PdfName.TYPE));
      if ((localPdfObject2 != null) && (PdfName.PAGE.equals(localPdfObject2)))
        return localPdfIndirectReference;
    }
    localIndirectReferences.setCopied();
    if (i != 0)
      this.streamMap.put(localByteStore, localPdfIndirectReference);
    PdfObject localPdfObject2 = copyObject(localPdfObject1);
    addToBody(localPdfObject2, localPdfIndirectReference);
    return localPdfIndirectReference;
  }

  static class ByteStore
  {
    private byte[] b;
    private int hash;
    private MessageDigest md5;

    private void serObject(PdfObject paramPdfObject, int paramInt, ByteBuffer paramByteBuffer)
      throws IOException
    {
      if (paramInt <= 0)
        return;
      if (paramPdfObject == null)
      {
        paramByteBuffer.append("$Lnull");
        return;
      }
      paramPdfObject = PdfReader.getPdfObject(paramPdfObject);
      if (paramPdfObject.isStream())
      {
        paramByteBuffer.append("$B");
        serDic((PdfDictionary)paramPdfObject, paramInt - 1, paramByteBuffer);
        if (paramInt > 0)
        {
          this.md5.reset();
          paramByteBuffer.append(this.md5.digest(PdfReader.getStreamBytesRaw((PRStream)paramPdfObject)));
        }
      }
      else if (paramPdfObject.isDictionary())
      {
        serDic((PdfDictionary)paramPdfObject, paramInt - 1, paramByteBuffer);
      }
      else if (paramPdfObject.isArray())
      {
        serArray((PdfArray)paramPdfObject, paramInt - 1, paramByteBuffer);
      }
      else if (paramPdfObject.isString())
      {
        paramByteBuffer.append("$S").append(paramPdfObject.toString());
      }
      else if (paramPdfObject.isName())
      {
        paramByteBuffer.append("$N").append(paramPdfObject.toString());
      }
      else
      {
        paramByteBuffer.append("$L").append(paramPdfObject.toString());
      }
    }

    private void serDic(PdfDictionary paramPdfDictionary, int paramInt, ByteBuffer paramByteBuffer)
      throws IOException
    {
      paramByteBuffer.append("$D");
      if (paramInt <= 0)
        return;
      Object[] arrayOfObject = paramPdfDictionary.getKeys().toArray();
      Arrays.sort(arrayOfObject);
      for (int i = 0; i < arrayOfObject.length; i++)
      {
        serObject((PdfObject)arrayOfObject[i], paramInt, paramByteBuffer);
        serObject(paramPdfDictionary.get((PdfName)arrayOfObject[i]), paramInt, paramByteBuffer);
      }
    }

    private void serArray(PdfArray paramPdfArray, int paramInt, ByteBuffer paramByteBuffer)
      throws IOException
    {
      paramByteBuffer.append("$A");
      if (paramInt <= 0)
        return;
      for (int i = 0; i < paramPdfArray.size(); i++)
        serObject(paramPdfArray.getPdfObject(i), paramInt, paramByteBuffer);
    }

    ByteStore(PRStream paramPRStream)
      throws IOException
    {
      try
      {
        this.md5 = MessageDigest.getInstance("MD5");
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
      ByteBuffer localByteBuffer = new ByteBuffer();
      int i = 100;
      serObject(paramPRStream, i, localByteBuffer);
      this.b = localByteBuffer.toByteArray();
      this.md5 = null;
    }

    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof ByteStore))
        return false;
      if (hashCode() != paramObject.hashCode())
        return false;
      return Arrays.equals(this.b, ((ByteStore)paramObject).b);
    }

    public int hashCode()
    {
      if (this.hash == 0)
      {
        int i = this.b.length;
        for (int j = 0; j < i; j++)
          this.hash = (this.hash * 31 + (this.b[j] & 0xFF));
      }
      return this.hash;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfSmartCopy
 * JD-Core Version:    0.6.0
 */