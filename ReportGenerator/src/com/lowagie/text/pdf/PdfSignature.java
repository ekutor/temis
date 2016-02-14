package com.lowagie.text.pdf;

public class PdfSignature extends PdfDictionary
{
  public PdfSignature(PdfName paramPdfName1, PdfName paramPdfName2)
  {
    super(PdfName.SIG);
    put(PdfName.FILTER, paramPdfName1);
    put(PdfName.SUBFILTER, paramPdfName2);
  }

  public void setByteRange(int[] paramArrayOfInt)
  {
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfInt.length; i++)
      localPdfArray.add(new PdfNumber(paramArrayOfInt[i]));
    put(PdfName.BYTERANGE, localPdfArray);
  }

  public void setContents(byte[] paramArrayOfByte)
  {
    put(PdfName.CONTENTS, new PdfString(paramArrayOfByte).setHexWriting(true));
  }

  public void setCert(byte[] paramArrayOfByte)
  {
    put(PdfName.CERT, new PdfString(paramArrayOfByte));
  }

  public void setName(String paramString)
  {
    put(PdfName.NAME, new PdfString(paramString, "UnicodeBig"));
  }

  public void setDate(PdfDate paramPdfDate)
  {
    put(PdfName.M, paramPdfDate);
  }

  public void setLocation(String paramString)
  {
    put(PdfName.LOCATION, new PdfString(paramString, "UnicodeBig"));
  }

  public void setReason(String paramString)
  {
    put(PdfName.REASON, new PdfString(paramString, "UnicodeBig"));
  }

  public void setContact(String paramString)
  {
    put(PdfName.CONTACTINFO, new PdfString(paramString, "UnicodeBig"));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfSignature
 * JD-Core Version:    0.6.0
 */