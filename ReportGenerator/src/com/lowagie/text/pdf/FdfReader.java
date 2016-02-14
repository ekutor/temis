package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class FdfReader extends PdfReader
{
  HashMap fields;
  String fileSpec;
  PdfName encoding;

  public FdfReader(String paramString)
    throws IOException
  {
    super(paramString);
  }

  public FdfReader(byte[] paramArrayOfByte)
    throws IOException
  {
    super(paramArrayOfByte);
  }

  public FdfReader(URL paramURL)
    throws IOException
  {
    super(paramURL);
  }

  public FdfReader(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
  }

  protected void readPdf()
    throws IOException
  {
    this.fields = new HashMap();
    try
    {
      this.tokens.checkFdfHeader();
      rebuildXref();
      readDocObj();
    }
    finally
    {
      try
      {
        this.tokens.close();
      }
      catch (Exception localException2)
      {
      }
    }
    readFields();
  }

  protected void kidNode(PdfDictionary paramPdfDictionary, String paramString)
  {
    PdfArray localPdfArray = paramPdfDictionary.getAsArray(PdfName.KIDS);
    if ((localPdfArray == null) || (localPdfArray.isEmpty()))
    {
      if (paramString.length() > 0)
        paramString = paramString.substring(1);
      this.fields.put(paramString, paramPdfDictionary);
    }
    else
    {
      paramPdfDictionary.remove(PdfName.KIDS);
      for (int i = 0; i < localPdfArray.size(); i++)
      {
        PdfDictionary localPdfDictionary1 = new PdfDictionary();
        localPdfDictionary1.merge(paramPdfDictionary);
        PdfDictionary localPdfDictionary2 = localPdfArray.getAsDict(i);
        PdfString localPdfString = localPdfDictionary2.getAsString(PdfName.T);
        String str = paramString;
        if (localPdfString != null)
          str = str + "." + localPdfString.toUnicodeString();
        localPdfDictionary1.merge(localPdfDictionary2);
        localPdfDictionary1.remove(PdfName.T);
        kidNode(localPdfDictionary1, str);
      }
    }
  }

  protected void readFields()
  {
    this.catalog = this.trailer.getAsDict(PdfName.ROOT);
    PdfDictionary localPdfDictionary1 = this.catalog.getAsDict(PdfName.FDF);
    if (localPdfDictionary1 == null)
      return;
    PdfString localPdfString = localPdfDictionary1.getAsString(PdfName.F);
    if (localPdfString != null)
      this.fileSpec = localPdfString.toUnicodeString();
    PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.FIELDS);
    if (localPdfArray == null)
      return;
    this.encoding = localPdfDictionary1.getAsName(PdfName.ENCODING);
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.KIDS, localPdfArray);
    kidNode(localPdfDictionary2, "");
  }

  public HashMap getFields()
  {
    return this.fields;
  }

  public PdfDictionary getField(String paramString)
  {
    return (PdfDictionary)this.fields.get(paramString);
  }

  public String getFieldValue(String paramString)
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)this.fields.get(paramString);
    if (localPdfDictionary == null)
      return null;
    PdfObject localPdfObject = getPdfObject(localPdfDictionary.get(PdfName.V));
    if (localPdfObject == null)
      return null;
    if (localPdfObject.isName())
      return PdfName.decodeName(((PdfName)localPdfObject).toString());
    if (localPdfObject.isString())
    {
      PdfString localPdfString = (PdfString)localPdfObject;
      if ((this.encoding == null) || (localPdfString.getEncoding() != null))
        return localPdfString.toUnicodeString();
      byte[] arrayOfByte = localPdfString.getBytes();
      if ((arrayOfByte.length >= 2) && (arrayOfByte[0] == -2) && (arrayOfByte[1] == -1))
        return localPdfString.toUnicodeString();
      try
      {
        if (this.encoding.equals(PdfName.SHIFT_JIS))
          return new String(arrayOfByte, "SJIS");
        if (this.encoding.equals(PdfName.UHC))
          return new String(arrayOfByte, "MS949");
        if (this.encoding.equals(PdfName.GBK))
          return new String(arrayOfByte, "GBK");
        if (this.encoding.equals(PdfName.BIGFIVE))
          return new String(arrayOfByte, "Big5");
      }
      catch (Exception localException)
      {
      }
      return localPdfString.toUnicodeString();
    }
    return null;
  }

  public String getFileSpec()
  {
    return this.fileSpec;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.FdfReader
 * JD-Core Version:    0.6.0
 */