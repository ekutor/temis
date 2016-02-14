package com.lowagie.text.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;

public class PdfPublicKeySecurityHandler
{
  static final int SEED_LENGTH = 20;
  private ArrayList recipients = null;
  private byte[] seed = new byte[20];

  public PdfPublicKeySecurityHandler()
  {
    try
    {
      KeyGenerator localKeyGenerator = KeyGenerator.getInstance("AES");
      localKeyGenerator.init(192, new SecureRandom());
      SecretKey localSecretKey = localKeyGenerator.generateKey();
      System.arraycopy(localSecretKey.getEncoded(), 0, this.seed, 0, 20);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      this.seed = SecureRandom.getSeed(20);
    }
    this.recipients = new ArrayList();
  }

  public static byte[] unescapedString(byte[] paramArrayOfByte)
    throws BadPdfFormatException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    if ((paramArrayOfByte[0] != 40) && (paramArrayOfByte[(paramArrayOfByte.length - 1)] != 41))
      throw new BadPdfFormatException("Expect '(' and ')' at begin and end of the string.");
    while (i < paramArrayOfByte.length)
    {
      if (paramArrayOfByte[i] == 92)
      {
        i++;
        switch (paramArrayOfByte[i])
        {
        case 98:
          localByteArrayOutputStream.write(8);
          break;
        case 102:
          localByteArrayOutputStream.write(12);
          break;
        case 116:
          localByteArrayOutputStream.write(9);
          break;
        case 110:
          localByteArrayOutputStream.write(10);
          break;
        case 114:
          localByteArrayOutputStream.write(13);
          break;
        case 40:
          localByteArrayOutputStream.write(40);
          break;
        case 41:
          localByteArrayOutputStream.write(41);
          break;
        case 92:
          localByteArrayOutputStream.write(92);
        }
      }
      else
      {
        localByteArrayOutputStream.write(paramArrayOfByte[i]);
      }
      i++;
    }
    return localByteArrayOutputStream.toByteArray();
  }

  public void addRecipient(PdfPublicKeyRecipient paramPdfPublicKeyRecipient)
  {
    this.recipients.add(paramPdfPublicKeyRecipient);
  }

  protected byte[] getSeed()
  {
    return (byte[])this.seed.clone();
  }

  public int getRecipientsSize()
  {
    return this.recipients.size();
  }

  public byte[] getEncodedRecipient(int paramInt)
    throws IOException, GeneralSecurityException
  {
    PdfPublicKeyRecipient localPdfPublicKeyRecipient = (PdfPublicKeyRecipient)this.recipients.get(paramInt);
    byte[] arrayOfByte1 = localPdfPublicKeyRecipient.getCms();
    if (arrayOfByte1 != null)
      return arrayOfByte1;
    Certificate localCertificate = localPdfPublicKeyRecipient.getCertificate();
    int i = localPdfPublicKeyRecipient.getPermission();
    int j = 3;
    i |= (j == 3 ? -3904 : -64);
    i &= -4;
    i++;
    byte[] arrayOfByte2 = new byte[24];
    int k = (byte)i;
    int m = (byte)(i >> 8);
    int n = (byte)(i >> 16);
    int i1 = (byte)(i >> 24);
    System.arraycopy(this.seed, 0, arrayOfByte2, 0, 20);
    arrayOfByte2[20] = i1;
    arrayOfByte2[21] = n;
    arrayOfByte2[22] = m;
    arrayOfByte2[23] = k;
    DERObject localDERObject = createDERForRecipient(arrayOfByte2, (X509Certificate)localCertificate);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DEROutputStream localDEROutputStream = new DEROutputStream(localByteArrayOutputStream);
    localDEROutputStream.writeObject(localDERObject);
    arrayOfByte1 = localByteArrayOutputStream.toByteArray();
    localPdfPublicKeyRecipient.setCms(arrayOfByte1);
    return arrayOfByte1;
  }

  public PdfArray getEncodedRecipients()
    throws IOException, GeneralSecurityException
  {
    PdfArray localPdfArray = new PdfArray();
    byte[] arrayOfByte = null;
    for (int i = 0; i < this.recipients.size(); i++)
      try
      {
        arrayOfByte = getEncodedRecipient(i);
        localPdfArray.add(new PdfLiteral(PdfContentByte.escapeString(arrayOfByte)));
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        localPdfArray = null;
      }
      catch (IOException localIOException)
      {
        localPdfArray = null;
      }
    return localPdfArray;
  }

  private DERObject createDERForRecipient(byte[] paramArrayOfByte, X509Certificate paramX509Certificate)
    throws IOException, GeneralSecurityException
  {
    String str = "1.2.840.113549.3.2";
    AlgorithmParameterGenerator localAlgorithmParameterGenerator = AlgorithmParameterGenerator.getInstance(str);
    AlgorithmParameters localAlgorithmParameters = localAlgorithmParameterGenerator.generateParameters();
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(localAlgorithmParameters.getEncoded("ASN.1"));
    ASN1InputStream localASN1InputStream = new ASN1InputStream(localByteArrayInputStream);
    DERObject localDERObject = localASN1InputStream.readObject();
    KeyGenerator localKeyGenerator = KeyGenerator.getInstance(str);
    localKeyGenerator.init(128);
    SecretKey localSecretKey = localKeyGenerator.generateKey();
    Cipher localCipher = Cipher.getInstance(str);
    localCipher.init(1, localSecretKey, localAlgorithmParameters);
    byte[] arrayOfByte = localCipher.doFinal(paramArrayOfByte);
    DEROctetString localDEROctetString = new DEROctetString(arrayOfByte);
    KeyTransRecipientInfo localKeyTransRecipientInfo = computeRecipientInfo(paramX509Certificate, localSecretKey.getEncoded());
    DERSet localDERSet = new DERSet(new RecipientInfo(localKeyTransRecipientInfo));
    AlgorithmIdentifier localAlgorithmIdentifier = new AlgorithmIdentifier(new DERObjectIdentifier(str), localDERObject);
    EncryptedContentInfo localEncryptedContentInfo = new EncryptedContentInfo(PKCSObjectIdentifiers.data, localAlgorithmIdentifier, localDEROctetString);
    EnvelopedData localEnvelopedData = new EnvelopedData(null, localDERSet, localEncryptedContentInfo, null);
    ContentInfo localContentInfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, localEnvelopedData);
    return localContentInfo.getDERObject();
  }

  private KeyTransRecipientInfo computeRecipientInfo(X509Certificate paramX509Certificate, byte[] paramArrayOfByte)
    throws GeneralSecurityException, IOException
  {
    ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramX509Certificate.getTBSCertificate()));
    TBSCertificateStructure localTBSCertificateStructure = TBSCertificateStructure.getInstance(localASN1InputStream.readObject());
    AlgorithmIdentifier localAlgorithmIdentifier = localTBSCertificateStructure.getSubjectPublicKeyInfo().getAlgorithmId();
    IssuerAndSerialNumber localIssuerAndSerialNumber = new IssuerAndSerialNumber(localTBSCertificateStructure.getIssuer(), localTBSCertificateStructure.getSerialNumber().getValue());
    Cipher localCipher = Cipher.getInstance(localAlgorithmIdentifier.getObjectId().getId());
    localCipher.init(1, paramX509Certificate);
    DEROctetString localDEROctetString = new DEROctetString(localCipher.doFinal(paramArrayOfByte));
    RecipientIdentifier localRecipientIdentifier = new RecipientIdentifier(localIssuerAndSerialNumber);
    return new KeyTransRecipientInfo(localRecipientIdentifier, localAlgorithmIdentifier, localDEROctetString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPublicKeySecurityHandler
 * JD-Core Version:    0.6.0
 */