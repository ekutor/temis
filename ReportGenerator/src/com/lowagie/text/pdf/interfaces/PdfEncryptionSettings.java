package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import java.security.cert.Certificate;

public abstract interface PdfEncryptionSettings
{
  public abstract void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
    throws DocumentException;

  public abstract void setEncryption(Certificate[] paramArrayOfCertificate, int[] paramArrayOfInt, int paramInt)
    throws DocumentException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.interfaces.PdfEncryptionSettings
 * JD-Core Version:    0.6.0
 */