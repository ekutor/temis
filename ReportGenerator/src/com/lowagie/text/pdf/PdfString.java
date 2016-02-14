package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PdfString extends PdfObject
{
  protected String value = "";
  protected String originalValue = null;
  protected String encoding = "PDF";
  protected int objNum = 0;
  protected int objGen = 0;
  protected boolean hexWriting = false;

  public PdfString()
  {
    super(3);
  }

  public PdfString(String paramString)
  {
    super(3);
    this.value = paramString;
  }

  public PdfString(String paramString1, String paramString2)
  {
    super(3);
    this.value = paramString1;
    this.encoding = paramString2;
  }

  public PdfString(byte[] paramArrayOfByte)
  {
    super(3);
    this.value = PdfEncodings.convertToString(paramArrayOfByte, null);
    this.encoding = "";
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = getBytes();
    PdfEncryption localPdfEncryption = null;
    if (paramPdfWriter != null)
      localPdfEncryption = paramPdfWriter.getEncryption();
    if ((localPdfEncryption != null) && (!localPdfEncryption.isEmbeddedFilesOnly()))
      arrayOfByte = localPdfEncryption.encryptByteArray(arrayOfByte);
    if (this.hexWriting)
    {
      ByteBuffer localByteBuffer = new ByteBuffer();
      localByteBuffer.append('<');
      int i = arrayOfByte.length;
      for (int j = 0; j < i; j++)
        localByteBuffer.appendHex(arrayOfByte[j]);
      localByteBuffer.append('>');
      paramOutputStream.write(localByteBuffer.toByteArray());
    }
    else
    {
      paramOutputStream.write(PdfContentByte.escapeString(arrayOfByte));
    }
  }

  public String toString()
  {
    return this.value;
  }

  public byte[] getBytes()
  {
    if (this.bytes == null)
      if ((this.encoding != null) && (this.encoding.equals("UnicodeBig")) && (PdfEncodings.isPdfDocEncoding(this.value)))
        this.bytes = PdfEncodings.convertToBytes(this.value, "PDF");
      else
        this.bytes = PdfEncodings.convertToBytes(this.value, this.encoding);
    return this.bytes;
  }

  public String toUnicodeString()
  {
    if ((this.encoding != null) && (this.encoding.length() != 0))
      return this.value;
    getBytes();
    if ((this.bytes.length >= 2) && (this.bytes[0] == -2) && (this.bytes[1] == -1))
      return PdfEncodings.convertToString(this.bytes, "UnicodeBig");
    return PdfEncodings.convertToString(this.bytes, "PDF");
  }

  public String getEncoding()
  {
    return this.encoding;
  }

  void setObjNum(int paramInt1, int paramInt2)
  {
    this.objNum = paramInt1;
    this.objGen = paramInt2;
  }

  void decrypt(PdfReader paramPdfReader)
  {
    PdfEncryption localPdfEncryption = paramPdfReader.getDecrypt();
    if (localPdfEncryption != null)
    {
      this.originalValue = this.value;
      localPdfEncryption.setHashKey(this.objNum, this.objGen);
      this.bytes = PdfEncodings.convertToBytes(this.value, null);
      this.bytes = localPdfEncryption.decryptByteArray(this.bytes);
      this.value = PdfEncodings.convertToString(this.bytes, null);
    }
  }

  public byte[] getOriginalBytes()
  {
    if (this.originalValue == null)
      return getBytes();
    return PdfEncodings.convertToBytes(this.originalValue, null);
  }

  public PdfString setHexWriting(boolean paramBoolean)
  {
    this.hexWriting = paramBoolean;
    return this;
  }

  public boolean isHexWriting()
  {
    return this.hexWriting;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfString
 * JD-Core Version:    0.6.0
 */