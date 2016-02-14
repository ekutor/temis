package com.lowagie.text.pdf;

import java.security.cert.Certificate;

public class PdfPublicKeyRecipient
{
  private Certificate certificate = null;
  private int permission = 0;
  protected byte[] cms = null;

  public PdfPublicKeyRecipient(Certificate paramCertificate, int paramInt)
  {
    this.certificate = paramCertificate;
    this.permission = paramInt;
  }

  public Certificate getCertificate()
  {
    return this.certificate;
  }

  public int getPermission()
  {
    return this.permission;
  }

  protected void setCms(byte[] paramArrayOfByte)
  {
    this.cms = paramArrayOfByte;
  }

  protected byte[] getCms()
  {
    return this.cms;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPublicKeyRecipient
 * JD-Core Version:    0.6.0
 */