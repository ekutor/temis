package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;

public abstract class PdfSigGenericPKCS extends PdfSignature
{
  protected String hashAlgorithm;
  protected String provider = null;
  protected PdfPKCS7 pkcs;
  protected String name;
  private byte[] externalDigest;
  private byte[] externalRSAdata;
  private String digestEncryptionAlgorithm;

  public PdfSigGenericPKCS(PdfName paramPdfName1, PdfName paramPdfName2)
  {
    super(paramPdfName1, paramPdfName2);
  }

  public void setSignInfo(PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate, CRL[] paramArrayOfCRL)
  {
    try
    {
      this.pkcs = new PdfPKCS7(paramPrivateKey, paramArrayOfCertificate, paramArrayOfCRL, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(get(PdfName.SUBFILTER)));
      this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
      if (PdfName.ADBE_X509_RSA_SHA1.equals(get(PdfName.SUBFILTER)))
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < paramArrayOfCertificate.length; i++)
          localByteArrayOutputStream.write(paramArrayOfCertificate[i].getEncoded());
        localByteArrayOutputStream.close();
        setCert(localByteArrayOutputStream.toByteArray());
        setContents(this.pkcs.getEncodedPKCS1());
      }
      else
      {
        setContents(this.pkcs.getEncodedPKCS7());
      }
      this.name = PdfPKCS7.getSubjectFields(this.pkcs.getSigningCertificate()).getField("CN");
      if (this.name != null)
        put(PdfName.NAME, new PdfString(this.name, "UnicodeBig"));
      this.pkcs = new PdfPKCS7(paramPrivateKey, paramArrayOfCertificate, paramArrayOfCRL, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(get(PdfName.SUBFILTER)));
      this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public void setExternalDigest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString)
  {
    this.externalDigest = paramArrayOfByte1;
    this.externalRSAdata = paramArrayOfByte2;
    this.digestEncryptionAlgorithm = paramString;
  }

  public String getName()
  {
    return this.name;
  }

  public PdfPKCS7 getSigner()
  {
    return this.pkcs;
  }

  public byte[] getSignerContents()
  {
    if (PdfName.ADBE_X509_RSA_SHA1.equals(get(PdfName.SUBFILTER)))
      return this.pkcs.getEncodedPKCS1();
    return this.pkcs.getEncodedPKCS7();
  }

  public static class PPKMS extends PdfSigGenericPKCS
  {
    public PPKMS()
    {
      super(PdfName.ADBE_PKCS7_SHA1);
      this.hashAlgorithm = "SHA1";
    }

    public PPKMS(String paramString)
    {
      this();
      this.provider = paramString;
    }
  }

  public static class PPKLite extends PdfSigGenericPKCS
  {
    public PPKLite()
    {
      super(PdfName.ADBE_X509_RSA_SHA1);
      this.hashAlgorithm = "SHA1";
      put(PdfName.R, new PdfNumber(65541));
    }

    public PPKLite(String paramString)
    {
      this();
      this.provider = paramString;
    }
  }

  public static class VeriSign extends PdfSigGenericPKCS
  {
    public VeriSign()
    {
      super(PdfName.ADBE_PKCS7_DETACHED);
      this.hashAlgorithm = "MD5";
      put(PdfName.R, new PdfNumber(65537));
    }

    public VeriSign(String paramString)
    {
      this();
      this.provider = paramString;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfSigGenericPKCS
 * JD-Core Version:    0.6.0
 */