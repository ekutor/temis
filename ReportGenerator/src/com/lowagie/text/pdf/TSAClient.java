package com.lowagie.text.pdf;

public abstract interface TSAClient
{
  public abstract int getTokenSizeEstimate();

  public abstract byte[] getTimeStampToken(PdfPKCS7 paramPdfPKCS7, byte[] paramArrayOfByte)
    throws Exception;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.TSAClient
 * JD-Core Version:    0.6.0
 */