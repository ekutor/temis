package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DEREncodableVector;
import org.bouncycastle.asn1.DEREnumerated;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.SingleResp;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;

public class PdfPKCS7
{
  private byte[] sigAttr;
  private byte[] digestAttr;
  private int version;
  private int signerversion;
  private Set digestalgos;
  private Collection certs;
  private Collection crls;
  private Collection signCerts;
  private X509Certificate signCert;
  private byte[] digest;
  private MessageDigest messageDigest;
  private String digestAlgorithm;
  private String digestEncryptionAlgorithm;
  private Signature sig;
  private transient PrivateKey privKey;
  private byte[] RSAdata;
  private boolean verified;
  private boolean verifyResult;
  private byte[] externalDigest;
  private byte[] externalRSAdata;
  private String provider;
  private static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
  private static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
  private static final String ID_RSA = "1.2.840.113549.1.1.1";
  private static final String ID_DSA = "1.2.840.10040.4.1";
  private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
  private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
  private static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
  private static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
  private String reason;
  private String location;
  private Calendar signDate;
  private String signName;
  private TimeStampToken timeStampToken;
  private static final HashMap digestNames = new HashMap();
  private static final HashMap algorithmNames = new HashMap();
  private static final HashMap allowedDigests = new HashMap();
  private BasicOCSPResp basicResp;

  public static String getDigest(String paramString)
  {
    String str = (String)digestNames.get(paramString);
    if (str == null)
      return paramString;
    return str;
  }

  public static String getAlgorithm(String paramString)
  {
    String str = (String)algorithmNames.get(paramString);
    if (str == null)
      return paramString;
    return str;
  }

  public TimeStampToken getTimeStampToken()
  {
    return this.timeStampToken;
  }

  public Calendar getTimeStampDate()
  {
    if (this.timeStampToken == null)
      return null;
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    Date localDate = this.timeStampToken.getTimeStampInfo().getGenTime();
    localGregorianCalendar.setTime(localDate);
    return localGregorianCalendar;
  }

  public PdfPKCS7(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString)
  {
    try
    {
      this.provider = paramString;
      X509CertParser localX509CertParser = new X509CertParser();
      localX509CertParser.engineInit(new ByteArrayInputStream(paramArrayOfByte2));
      this.certs = localX509CertParser.engineReadAll();
      this.signCerts = this.certs;
      this.signCert = ((X509Certificate)this.certs.iterator().next());
      this.crls = new ArrayList();
      ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte1));
      this.digest = ((DEROctetString)localASN1InputStream.readObject()).getOctets();
      if (paramString == null)
        this.sig = Signature.getInstance("SHA1withRSA");
      else
        this.sig = Signature.getInstance("SHA1withRSA", paramString);
      this.sig.initVerify(this.signCert.getPublicKey());
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public BasicOCSPResp getOcsp()
  {
    return this.basicResp;
  }

  private void findOcsp(ASN1Sequence paramASN1Sequence)
    throws IOException
  {
    this.basicResp = null;
    int i = 0;
    while ((!(paramASN1Sequence.getObjectAt(0) instanceof DERObjectIdentifier)) || (!((DERObjectIdentifier)paramASN1Sequence.getObjectAt(0)).getId().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic.getId())))
    {
      i = 1;
      for (int j = 0; j < paramASN1Sequence.size(); j++)
      {
        if ((paramASN1Sequence.getObjectAt(j) instanceof ASN1Sequence))
        {
          paramASN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(0);
          i = 0;
          break;
        }
        if (!(paramASN1Sequence.getObjectAt(j) instanceof ASN1TaggedObject))
          continue;
        localObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(j);
        if ((((ASN1TaggedObject)localObject).getObject() instanceof ASN1Sequence))
        {
          paramASN1Sequence = (ASN1Sequence)((ASN1TaggedObject)localObject).getObject();
          i = 0;
          break;
        }
        return;
      }
      if (i != 0)
        return;
    }
    DEROctetString localDEROctetString = (DEROctetString)paramASN1Sequence.getObjectAt(1);
    Object localObject = new ASN1InputStream(localDEROctetString.getOctets());
    BasicOCSPResponse localBasicOCSPResponse = BasicOCSPResponse.getInstance(((ASN1InputStream)localObject).readObject());
    this.basicResp = new BasicOCSPResp(localBasicOCSPResponse);
  }

  public PdfPKCS7(byte[] paramArrayOfByte, String paramString)
  {
    try
    {
      this.provider = paramString;
      ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte));
      DERObject localDERObject;
      try
      {
        localDERObject = localASN1InputStream.readObject();
      }
      catch (IOException localIOException)
      {
        throw new IllegalArgumentException("can't decode PKCS7SignedData object");
      }
      if (!(localDERObject instanceof ASN1Sequence))
        throw new IllegalArgumentException("Not a valid PKCS#7 object - not a sequence");
      ASN1Sequence localASN1Sequence1 = (ASN1Sequence)localDERObject;
      DERObjectIdentifier localDERObjectIdentifier = (DERObjectIdentifier)localASN1Sequence1.getObjectAt(0);
      if (!localDERObjectIdentifier.getId().equals("1.2.840.113549.1.7.2"))
        throw new IllegalArgumentException("Not a valid PKCS#7 object - not signed data");
      ASN1Sequence localASN1Sequence2 = (ASN1Sequence)((DERTaggedObject)localASN1Sequence1.getObjectAt(1)).getObject();
      this.version = ((DERInteger)localASN1Sequence2.getObjectAt(0)).getValue().intValue();
      this.digestalgos = new HashSet();
      Enumeration localEnumeration = ((ASN1Set)localASN1Sequence2.getObjectAt(1)).getObjects();
      while (localEnumeration.hasMoreElements())
      {
        localObject1 = (ASN1Sequence)localEnumeration.nextElement();
        localObject2 = (DERObjectIdentifier)((ASN1Sequence)localObject1).getObjectAt(0);
        this.digestalgos.add(((DERObjectIdentifier)localObject2).getId());
      }
      Object localObject1 = new X509CertParser();
      ((X509CertParser)localObject1).engineInit(new ByteArrayInputStream(paramArrayOfByte));
      this.certs = ((X509CertParser)localObject1).engineReadAll();
      Object localObject2 = new X509CRLParser();
      ((X509CRLParser)localObject2).engineInit(new ByteArrayInputStream(paramArrayOfByte));
      this.crls = ((X509CRLParser)localObject2).engineReadAll();
      ASN1Sequence localASN1Sequence3 = (ASN1Sequence)localASN1Sequence2.getObjectAt(2);
      if (localASN1Sequence3.size() > 1)
      {
        DEROctetString localDEROctetString = (DEROctetString)((DERTaggedObject)localASN1Sequence3.getObjectAt(1)).getObject();
        this.RSAdata = localDEROctetString.getOctets();
      }
      for (int i = 3; (localASN1Sequence2.getObjectAt(i) instanceof DERTaggedObject); i++);
      ASN1Set localASN1Set1 = (ASN1Set)localASN1Sequence2.getObjectAt(i);
      if (localASN1Set1.size() != 1)
        throw new IllegalArgumentException("This PKCS#7 object has multiple SignerInfos - only one is supported at this time");
      ASN1Sequence localASN1Sequence4 = (ASN1Sequence)localASN1Set1.getObjectAt(0);
      this.signerversion = ((DERInteger)localASN1Sequence4.getObjectAt(0)).getValue().intValue();
      ASN1Sequence localASN1Sequence5 = (ASN1Sequence)localASN1Sequence4.getObjectAt(1);
      BigInteger localBigInteger = ((DERInteger)localASN1Sequence5.getObjectAt(1)).getValue();
      Object localObject3 = this.certs.iterator();
      Object localObject4;
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (X509Certificate)((Iterator)localObject3).next();
        if (!localBigInteger.equals(((X509Certificate)localObject4).getSerialNumber()))
          continue;
        this.signCert = ((X509Certificate)localObject4);
      }
      if (this.signCert == null)
        throw new IllegalArgumentException("Can't find signing certificate with serial " + localBigInteger.toString(16));
      signCertificateChain();
      this.digestAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)localASN1Sequence4.getObjectAt(2)).getObjectAt(0)).getId();
      i = 3;
      Object localObject5;
      ASN1Set localASN1Set2;
      ASN1Sequence localASN1Sequence6;
      if ((localASN1Sequence4.getObjectAt(i) instanceof ASN1TaggedObject))
      {
        localObject3 = (ASN1TaggedObject)localASN1Sequence4.getObjectAt(i);
        localObject4 = ASN1Set.getInstance((ASN1TaggedObject)localObject3, false);
        this.sigAttr = ((ASN1Set)localObject4).getEncoded("DER");
        for (int j = 0; j < ((ASN1Set)localObject4).size(); j++)
        {
          localObject5 = (ASN1Sequence)((ASN1Set)localObject4).getObjectAt(j);
          if (((DERObjectIdentifier)((ASN1Sequence)localObject5).getObjectAt(0)).getId().equals("1.2.840.113549.1.9.4"))
          {
            localASN1Set2 = (ASN1Set)((ASN1Sequence)localObject5).getObjectAt(1);
            this.digestAttr = ((DEROctetString)localASN1Set2.getObjectAt(0)).getOctets();
          }
          else
          {
            if (!((DERObjectIdentifier)((ASN1Sequence)localObject5).getObjectAt(0)).getId().equals("1.2.840.113583.1.1.8"))
              continue;
            localASN1Set2 = (ASN1Set)((ASN1Sequence)localObject5).getObjectAt(1);
            localASN1Sequence6 = (ASN1Sequence)localASN1Set2.getObjectAt(0);
            for (int k = 0; k < localASN1Sequence6.size(); k++)
            {
              ASN1TaggedObject localASN1TaggedObject = (ASN1TaggedObject)localASN1Sequence6.getObjectAt(k);
              if (localASN1TaggedObject.getTagNo() != 1)
                continue;
              ASN1Sequence localASN1Sequence7 = (ASN1Sequence)localASN1TaggedObject.getObject();
              findOcsp(localASN1Sequence7);
            }
          }
        }
        if (this.digestAttr == null)
          throw new IllegalArgumentException("Authenticated attribute is missing the digest.");
        i++;
      }
      this.digestEncryptionAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)localASN1Sequence4.getObjectAt(i++)).getObjectAt(0)).getId();
      this.digest = ((DEROctetString)localASN1Sequence4.getObjectAt(i++)).getOctets();
      if ((i < localASN1Sequence4.size()) && ((localASN1Sequence4.getObjectAt(i) instanceof DERTaggedObject)))
      {
        localObject3 = (DERTaggedObject)localASN1Sequence4.getObjectAt(i);
        localObject4 = ASN1Set.getInstance((ASN1TaggedObject)localObject3, false);
        AttributeTable localAttributeTable = new AttributeTable((ASN1Set)localObject4);
        localObject5 = localAttributeTable.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
        if (localObject5 != null)
        {
          localASN1Set2 = ((Attribute)localObject5).getAttrValues();
          localASN1Sequence6 = ASN1Sequence.getInstance(localASN1Set2.getObjectAt(0));
          ContentInfo localContentInfo = new ContentInfo(localASN1Sequence6);
          this.timeStampToken = new TimeStampToken(localContentInfo);
        }
      }
      if ((this.RSAdata != null) || (this.digestAttr != null))
        if ((paramString == null) || (paramString.startsWith("SunPKCS11")))
          this.messageDigest = MessageDigest.getInstance(getHashAlgorithm());
        else
          this.messageDigest = MessageDigest.getInstance(getHashAlgorithm(), paramString);
      if (paramString == null)
        this.sig = Signature.getInstance(getDigestAlgorithm());
      else
        this.sig = Signature.getInstance(getDigestAlgorithm(), paramString);
      this.sig.initVerify(this.signCert.getPublicKey());
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public PdfPKCS7(PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate, CRL[] paramArrayOfCRL, String paramString1, String paramString2, boolean paramBoolean)
    throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException
  {
    this.privKey = paramPrivateKey;
    this.provider = paramString2;
    this.digestAlgorithm = ((String)allowedDigests.get(paramString1.toUpperCase()));
    if (this.digestAlgorithm == null)
      throw new NoSuchAlgorithmException("Unknown Hash Algorithm " + paramString1);
    this.version = (this.signerversion = 1);
    this.certs = new ArrayList();
    this.crls = new ArrayList();
    this.digestalgos = new HashSet();
    this.digestalgos.add(this.digestAlgorithm);
    this.signCert = ((X509Certificate)paramArrayOfCertificate[0]);
    for (int i = 0; i < paramArrayOfCertificate.length; i++)
      this.certs.add(paramArrayOfCertificate[i]);
    if (paramArrayOfCRL != null)
      for (i = 0; i < paramArrayOfCRL.length; i++)
        this.crls.add(paramArrayOfCRL[i]);
    if (paramPrivateKey != null)
    {
      this.digestEncryptionAlgorithm = paramPrivateKey.getAlgorithm();
      if (this.digestEncryptionAlgorithm.equals("RSA"))
        this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
      else if (this.digestEncryptionAlgorithm.equals("DSA"))
        this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
      else
        throw new NoSuchAlgorithmException("Unknown Key Algorithm " + this.digestEncryptionAlgorithm);
    }
    if (paramBoolean)
    {
      this.RSAdata = new byte[0];
      if ((paramString2 == null) || (paramString2.startsWith("SunPKCS11")))
        this.messageDigest = MessageDigest.getInstance(getHashAlgorithm());
      else
        this.messageDigest = MessageDigest.getInstance(getHashAlgorithm(), paramString2);
    }
    if (paramPrivateKey != null)
    {
      if (paramString2 == null)
        this.sig = Signature.getInstance(getDigestAlgorithm());
      else
        this.sig = Signature.getInstance(getDigestAlgorithm(), paramString2);
      this.sig.initSign(paramPrivateKey);
    }
  }

  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    if ((this.RSAdata != null) || (this.digestAttr != null))
      this.messageDigest.update(paramArrayOfByte, paramInt1, paramInt2);
    else
      this.sig.update(paramArrayOfByte, paramInt1, paramInt2);
  }

  public boolean verify()
    throws SignatureException
  {
    if (this.verified)
      return this.verifyResult;
    if (this.sigAttr != null)
    {
      this.sig.update(this.sigAttr);
      if (this.RSAdata != null)
      {
        byte[] arrayOfByte = this.messageDigest.digest();
        this.messageDigest.update(arrayOfByte);
      }
      this.verifyResult = ((Arrays.equals(this.messageDigest.digest(), this.digestAttr)) && (this.sig.verify(this.digest)));
    }
    else
    {
      if (this.RSAdata != null)
        this.sig.update(this.messageDigest.digest());
      this.verifyResult = this.sig.verify(this.digest);
    }
    this.verified = true;
    return this.verifyResult;
  }

  public boolean verifyTimestampImprint()
    throws NoSuchAlgorithmException
  {
    if (this.timeStampToken == null)
      return false;
    MessageImprint localMessageImprint = this.timeStampToken.getTimeStampInfo().toTSTInfo().getMessageImprint();
    byte[] arrayOfByte1 = MessageDigest.getInstance("SHA-1").digest(this.digest);
    byte[] arrayOfByte2 = localMessageImprint.getHashedMessage();
    boolean bool = Arrays.equals(arrayOfByte1, arrayOfByte2);
    return bool;
  }

  public Certificate[] getCertificates()
  {
    return (X509Certificate[])this.certs.toArray(new X509Certificate[this.certs.size()]);
  }

  public Certificate[] getSignCertificateChain()
  {
    return (X509Certificate[])this.signCerts.toArray(new X509Certificate[this.signCerts.size()]);
  }

  private void signCertificateChain()
  {
    ArrayList localArrayList1 = new ArrayList();
    localArrayList1.add(this.signCert);
    ArrayList localArrayList2 = new ArrayList(this.certs);
    for (int i = 0; i < localArrayList2.size(); i++)
    {
      if (!this.signCert.getSerialNumber().equals(((X509Certificate)localArrayList2.get(i)).getSerialNumber()))
        continue;
      localArrayList2.remove(i);
      i--;
    }
    i = 1;
    label190: 
    while (i != 0)
    {
      X509Certificate localX509Certificate = (X509Certificate)localArrayList1.get(localArrayList1.size() - 1);
      i = 0;
      int j = 0;
      while (true)
        while (true)
        {
          if (j >= localArrayList2.size())
            break label190;
          try
          {
            if (this.provider == null)
              localX509Certificate.verify(((X509Certificate)localArrayList2.get(j)).getPublicKey());
            else
              localX509Certificate.verify(((X509Certificate)localArrayList2.get(j)).getPublicKey(), this.provider);
            i = 1;
            localArrayList1.add(localArrayList2.get(j));
            localArrayList2.remove(j);
          }
          catch (Exception localException)
          {
            j++;
          }
        }
    }
    this.signCerts = localArrayList1;
  }

  public Collection getCRLs()
  {
    return this.crls;
  }

  public X509Certificate getSigningCertificate()
  {
    return this.signCert;
  }

  public int getVersion()
  {
    return this.version;
  }

  public int getSigningInfoVersion()
  {
    return this.signerversion;
  }

  public String getDigestAlgorithm()
  {
    String str = getAlgorithm(this.digestEncryptionAlgorithm);
    if (str == null)
      str = this.digestEncryptionAlgorithm;
    return getHashAlgorithm() + "with" + str;
  }

  public String getHashAlgorithm()
  {
    return getDigest(this.digestAlgorithm);
  }

  public static KeyStore loadCacertsKeyStore()
  {
    return loadCacertsKeyStore(null);
  }

  public static KeyStore loadCacertsKeyStore(String paramString)
  {
    File localFile = new File(System.getProperty("java.home"), "lib");
    localFile = new File(localFile, "security");
    localFile = new File(localFile, "cacerts");
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(localFile);
      KeyStore localKeyStore1;
      if (paramString == null)
        localKeyStore1 = KeyStore.getInstance("JKS");
      else
        localKeyStore1 = KeyStore.getInstance("JKS", paramString);
      localKeyStore1.load(localFileInputStream, null);
      localKeyStore2 = localKeyStore1;
    }
    catch (Exception localException1)
    {
      KeyStore localKeyStore2;
      throw new ExceptionConverter(localException1);
    }
    finally
    {
      try
      {
        if (localFileInputStream != null)
          localFileInputStream.close();
      }
      catch (Exception localException2)
      {
      }
    }
  }

  public static String verifyCertificate(X509Certificate paramX509Certificate, Collection paramCollection, Calendar paramCalendar)
  {
    if (paramCalendar == null)
      paramCalendar = new GregorianCalendar();
    if (paramX509Certificate.hasUnsupportedCriticalExtension())
      return "Has unsupported critical extension";
    try
    {
      paramX509Certificate.checkValidity(paramCalendar.getTime());
    }
    catch (Exception localException)
    {
      return localException.getMessage();
    }
    if (paramCollection != null)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
        if (((CRL)localIterator.next()).isRevoked(paramX509Certificate))
          return "Certificate revoked";
    }
    return null;
  }

  public static Object[] verifyCertificates(Certificate[] paramArrayOfCertificate, KeyStore paramKeyStore, Collection paramCollection, Calendar paramCalendar)
  {
    if (paramCalendar == null)
      paramCalendar = new GregorianCalendar();
    for (int i = 0; i < paramArrayOfCertificate.length; i++)
    {
      X509Certificate localX509Certificate1 = (X509Certificate)paramArrayOfCertificate[i];
      String str1 = verifyCertificate(localX509Certificate1, paramCollection, paramCalendar);
      if (str1 != null)
        return new Object[] { localX509Certificate1, str1 };
      try
      {
        Enumeration localEnumeration = paramKeyStore.aliases();
        while (localEnumeration.hasMoreElements())
          try
          {
            String str2 = (String)localEnumeration.nextElement();
            if (!paramKeyStore.isCertificateEntry(str2))
              continue;
            X509Certificate localX509Certificate3 = (X509Certificate)paramKeyStore.getCertificate(str2);
            if (verifyCertificate(localX509Certificate3, paramCollection, paramCalendar) != null)
              continue;
            try
            {
              localX509Certificate1.verify(localX509Certificate3.getPublicKey());
              return null;
            }
            catch (Exception localException4)
            {
            }
          }
          catch (Exception localException2)
          {
          }
      }
      catch (Exception localException1)
      {
      }
      for (int j = 0; j < paramArrayOfCertificate.length; j++)
      {
        if (j == i)
          continue;
        X509Certificate localX509Certificate2 = (X509Certificate)paramArrayOfCertificate[j];
        try
        {
          localX509Certificate1.verify(localX509Certificate2.getPublicKey());
        }
        catch (Exception localException3)
        {
        }
      }
      if (j == paramArrayOfCertificate.length)
        return new Object[] { localX509Certificate1, "Cannot be verified against the KeyStore or the certificate chain" };
    }
    return new Object[] { null, "Invalid state. Possible circular certificate chain" };
  }

  public static boolean verifyOcspCertificates(BasicOCSPResp paramBasicOCSPResp, KeyStore paramKeyStore, String paramString)
  {
    if (paramString == null)
      paramString = "BC";
    try
    {
      Enumeration localEnumeration = paramKeyStore.aliases();
      while (localEnumeration.hasMoreElements())
        try
        {
          String str = (String)localEnumeration.nextElement();
          if (!paramKeyStore.isCertificateEntry(str))
            continue;
          X509Certificate localX509Certificate = (X509Certificate)paramKeyStore.getCertificate(str);
          if (paramBasicOCSPResp.verify(localX509Certificate.getPublicKey(), paramString))
            return true;
        }
        catch (Exception localException2)
        {
        }
    }
    catch (Exception localException1)
    {
    }
    return false;
  }

  public static boolean verifyTimestampCertificates(TimeStampToken paramTimeStampToken, KeyStore paramKeyStore, String paramString)
  {
    if (paramString == null)
      paramString = "BC";
    try
    {
      Enumeration localEnumeration = paramKeyStore.aliases();
      while (localEnumeration.hasMoreElements())
        try
        {
          String str = (String)localEnumeration.nextElement();
          if (!paramKeyStore.isCertificateEntry(str))
            continue;
          X509Certificate localX509Certificate = (X509Certificate)paramKeyStore.getCertificate(str);
          paramTimeStampToken.validate(localX509Certificate, paramString);
          return true;
        }
        catch (Exception localException2)
        {
        }
    }
    catch (Exception localException1)
    {
    }
    return false;
  }

  public static String getOCSPURL(X509Certificate paramX509Certificate)
    throws CertificateParsingException
  {
    try
    {
      DERObject localDERObject = getExtensionValue(paramX509Certificate, X509Extensions.AuthorityInfoAccess.getId());
      if (localDERObject == null)
        return null;
      ASN1Sequence localASN1Sequence1 = (ASN1Sequence)localDERObject;
      for (int i = 0; i < localASN1Sequence1.size(); i++)
      {
        ASN1Sequence localASN1Sequence2 = (ASN1Sequence)localASN1Sequence1.getObjectAt(i);
        if ((localASN1Sequence2.size() != 2) || (!(localASN1Sequence2.getObjectAt(0) instanceof DERObjectIdentifier)) || (!((DERObjectIdentifier)localASN1Sequence2.getObjectAt(0)).getId().equals("1.3.6.1.5.5.7.48.1")))
          continue;
        String str = getStringFromGeneralName((DERObject)localASN1Sequence2.getObjectAt(1));
        if (str == null)
          return "";
        return str;
      }
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public boolean isRevocationValid()
  {
    if (this.basicResp == null)
      return false;
    if (this.signCerts.size() < 2)
      return false;
    try
    {
      X509Certificate[] arrayOfX509Certificate = (X509Certificate[])getSignCertificateChain();
      SingleResp localSingleResp = this.basicResp.getResponses()[0];
      CertificateID localCertificateID1 = localSingleResp.getCertID();
      X509Certificate localX509Certificate1 = getSigningCertificate();
      X509Certificate localX509Certificate2 = arrayOfX509Certificate[1];
      CertificateID localCertificateID2 = new CertificateID("1.3.14.3.2.26", localX509Certificate2, localX509Certificate1.getSerialNumber());
      return localCertificateID2.equals(localCertificateID1);
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  private static DERObject getExtensionValue(X509Certificate paramX509Certificate, String paramString)
    throws IOException
  {
    byte[] arrayOfByte = paramX509Certificate.getExtensionValue(paramString);
    if (arrayOfByte == null)
      return null;
    ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(arrayOfByte));
    ASN1OctetString localASN1OctetString = (ASN1OctetString)localASN1InputStream.readObject();
    localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(localASN1OctetString.getOctets()));
    return localASN1InputStream.readObject();
  }

  private static String getStringFromGeneralName(DERObject paramDERObject)
    throws IOException
  {
    DERTaggedObject localDERTaggedObject = (DERTaggedObject)paramDERObject;
    return new String(ASN1OctetString.getInstance(localDERTaggedObject, false).getOctets(), "ISO-8859-1");
  }

  private static DERObject getIssuer(byte[] paramArrayOfByte)
  {
    try
    {
      ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte));
      ASN1Sequence localASN1Sequence = (ASN1Sequence)localASN1InputStream.readObject();
      return (DERObject)localASN1Sequence.getObjectAt((localASN1Sequence.getObjectAt(0) instanceof DERTaggedObject) ? 3 : 2);
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  private static DERObject getSubject(byte[] paramArrayOfByte)
  {
    try
    {
      ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte));
      ASN1Sequence localASN1Sequence = (ASN1Sequence)localASN1InputStream.readObject();
      return (DERObject)localASN1Sequence.getObjectAt((localASN1Sequence.getObjectAt(0) instanceof DERTaggedObject) ? 5 : 4);
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  public static X509Name getIssuerFields(X509Certificate paramX509Certificate)
  {
    try
    {
      return new X509Name((ASN1Sequence)getIssuer(paramX509Certificate.getTBSCertificate()));
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public static X509Name getSubjectFields(X509Certificate paramX509Certificate)
  {
    try
    {
      return new X509Name((ASN1Sequence)getSubject(paramX509Certificate.getTBSCertificate()));
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public byte[] getEncodedPKCS1()
  {
    try
    {
      if (this.externalDigest != null)
        this.digest = this.externalDigest;
      else
        this.digest = this.sig.sign();
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      ASN1OutputStream localASN1OutputStream = new ASN1OutputStream(localByteArrayOutputStream);
      localASN1OutputStream.writeObject(new DEROctetString(this.digest));
      localASN1OutputStream.close();
      return localByteArrayOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public void setExternalDigest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString)
  {
    this.externalDigest = paramArrayOfByte1;
    this.externalRSAdata = paramArrayOfByte2;
    if (paramString != null)
      if (paramString.equals("RSA"))
        this.digestEncryptionAlgorithm = "1.2.840.113549.1.1.1";
      else if (paramString.equals("DSA"))
        this.digestEncryptionAlgorithm = "1.2.840.10040.4.1";
      else
        throw new ExceptionConverter(new NoSuchAlgorithmException("Unknown Key Algorithm " + paramString));
  }

  public byte[] getEncodedPKCS7()
  {
    return getEncodedPKCS7(null, null, null, null);
  }

  public byte[] getEncodedPKCS7(byte[] paramArrayOfByte, Calendar paramCalendar)
  {
    return getEncodedPKCS7(paramArrayOfByte, paramCalendar, null, null);
  }

  public byte[] getEncodedPKCS7(byte[] paramArrayOfByte1, Calendar paramCalendar, TSAClient paramTSAClient, byte[] paramArrayOfByte2)
  {
    try
    {
      if (this.externalDigest != null)
      {
        this.digest = this.externalDigest;
        if (this.RSAdata != null)
          this.RSAdata = this.externalRSAdata;
      }
      else if ((this.externalRSAdata != null) && (this.RSAdata != null))
      {
        this.RSAdata = this.externalRSAdata;
        this.sig.update(this.RSAdata);
        this.digest = this.sig.sign();
      }
      else
      {
        if (this.RSAdata != null)
        {
          this.RSAdata = this.messageDigest.digest();
          this.sig.update(this.RSAdata);
        }
        this.digest = this.sig.sign();
      }
      ASN1EncodableVector localASN1EncodableVector = new ASN1EncodableVector();
      Object localObject1 = this.digestalgos.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = new ASN1EncodableVector();
        ((ASN1EncodableVector)localObject2).add(new DERObjectIdentifier((String)((Iterator)localObject1).next()));
        ((ASN1EncodableVector)localObject2).add(DERNull.INSTANCE);
        localASN1EncodableVector.add(new DERSequence((DEREncodableVector)localObject2));
      }
      localObject1 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject1).add(new DERObjectIdentifier("1.2.840.113549.1.7.1"));
      if (this.RSAdata != null)
        ((ASN1EncodableVector)localObject1).add(new DERTaggedObject(0, new DEROctetString(this.RSAdata)));
      Object localObject2 = new DERSequence((DEREncodableVector)localObject1);
      localObject1 = new ASN1EncodableVector();
      Object localObject3 = this.certs.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = new ASN1InputStream(new ByteArrayInputStream(((X509Certificate)((Iterator)localObject3).next()).getEncoded()));
        ((ASN1EncodableVector)localObject1).add(((ASN1InputStream)localObject4).readObject());
      }
      localObject3 = new DERSet((DEREncodableVector)localObject1);
      Object localObject4 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject4).add(new DERInteger(this.signerversion));
      localObject1 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject1).add(getIssuer(this.signCert.getTBSCertificate()));
      ((ASN1EncodableVector)localObject1).add(new DERInteger(this.signCert.getSerialNumber()));
      ((ASN1EncodableVector)localObject4).add(new DERSequence((DEREncodableVector)localObject1));
      localObject1 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject1).add(new DERObjectIdentifier(this.digestAlgorithm));
      ((ASN1EncodableVector)localObject1).add(new DERNull());
      ((ASN1EncodableVector)localObject4).add(new DERSequence((DEREncodableVector)localObject1));
      if ((paramArrayOfByte1 != null) && (paramCalendar != null))
        ((ASN1EncodableVector)localObject4).add(new DERTaggedObject(false, 0, getAuthenticatedAttributeSet(paramArrayOfByte1, paramCalendar, paramArrayOfByte2)));
      localObject1 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject1).add(new DERObjectIdentifier(this.digestEncryptionAlgorithm));
      ((ASN1EncodableVector)localObject1).add(new DERNull());
      ((ASN1EncodableVector)localObject4).add(new DERSequence((DEREncodableVector)localObject1));
      ((ASN1EncodableVector)localObject4).add(new DEROctetString(this.digest));
      if (paramTSAClient != null)
      {
        localObject5 = MessageDigest.getInstance("SHA-1").digest(this.digest);
        localObject6 = paramTSAClient.getTimeStampToken(this, localObject5);
        if (localObject6 != null)
        {
          localObject7 = buildUnauthenticatedAttributes(localObject6);
          if (localObject7 != null)
            ((ASN1EncodableVector)localObject4).add(new DERTaggedObject(false, 1, new DERSet((DEREncodableVector)localObject7)));
        }
      }
      Object localObject5 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject5).add(new DERInteger(this.version));
      ((ASN1EncodableVector)localObject5).add(new DERSet(localASN1EncodableVector));
      ((ASN1EncodableVector)localObject5).add((DEREncodable)localObject2);
      ((ASN1EncodableVector)localObject5).add(new DERTaggedObject(false, 0, (DEREncodable)localObject3));
      if (!this.crls.isEmpty())
      {
        localObject1 = new ASN1EncodableVector();
        localObject6 = this.crls.iterator();
        while (((Iterator)localObject6).hasNext())
        {
          localObject7 = new ASN1InputStream(new ByteArrayInputStream(((X509CRL)((Iterator)localObject6).next()).getEncoded()));
          ((ASN1EncodableVector)localObject1).add(((ASN1InputStream)localObject7).readObject());
        }
        localObject6 = new DERSet((DEREncodableVector)localObject1);
        ((ASN1EncodableVector)localObject5).add(new DERTaggedObject(false, 1, (DEREncodable)localObject6));
      }
      ((ASN1EncodableVector)localObject5).add(new DERSet(new DERSequence((DEREncodableVector)localObject4)));
      Object localObject6 = new ASN1EncodableVector();
      ((ASN1EncodableVector)localObject6).add(new DERObjectIdentifier("1.2.840.113549.1.7.2"));
      ((ASN1EncodableVector)localObject6).add(new DERTaggedObject(0, new DERSequence((DEREncodableVector)localObject5)));
      Object localObject7 = new ByteArrayOutputStream();
      ASN1OutputStream localASN1OutputStream = new ASN1OutputStream((OutputStream)localObject7);
      localASN1OutputStream.writeObject(new DERSequence((DEREncodableVector)localObject6));
      localASN1OutputStream.close();
      return ((ByteArrayOutputStream)localObject7).toByteArray();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  private ASN1EncodableVector buildUnauthenticatedAttributes(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null)
      return null;
    String str = "1.2.840.113549.1.9.16.2.14";
    ASN1InputStream localASN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte));
    ASN1EncodableVector localASN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector localASN1EncodableVector2 = new ASN1EncodableVector();
    localASN1EncodableVector2.add(new DERObjectIdentifier(str));
    ASN1Sequence localASN1Sequence = (ASN1Sequence)localASN1InputStream.readObject();
    localASN1EncodableVector2.add(new DERSet(localASN1Sequence));
    localASN1EncodableVector1.add(new DERSequence(localASN1EncodableVector2));
    return localASN1EncodableVector1;
  }

  public byte[] getAuthenticatedAttributeBytes(byte[] paramArrayOfByte1, Calendar paramCalendar, byte[] paramArrayOfByte2)
  {
    try
    {
      return getAuthenticatedAttributeSet(paramArrayOfByte1, paramCalendar, paramArrayOfByte2).getEncoded("DER");
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  private DERSet getAuthenticatedAttributeSet(byte[] paramArrayOfByte1, Calendar paramCalendar, byte[] paramArrayOfByte2)
  {
    try
    {
      ASN1EncodableVector localASN1EncodableVector1 = new ASN1EncodableVector();
      ASN1EncodableVector localASN1EncodableVector2 = new ASN1EncodableVector();
      localASN1EncodableVector2.add(new DERObjectIdentifier("1.2.840.113549.1.9.3"));
      localASN1EncodableVector2.add(new DERSet(new DERObjectIdentifier("1.2.840.113549.1.7.1")));
      localASN1EncodableVector1.add(new DERSequence(localASN1EncodableVector2));
      localASN1EncodableVector2 = new ASN1EncodableVector();
      localASN1EncodableVector2.add(new DERObjectIdentifier("1.2.840.113549.1.9.5"));
      localASN1EncodableVector2.add(new DERSet(new DERUTCTime(paramCalendar.getTime())));
      localASN1EncodableVector1.add(new DERSequence(localASN1EncodableVector2));
      localASN1EncodableVector2 = new ASN1EncodableVector();
      localASN1EncodableVector2.add(new DERObjectIdentifier("1.2.840.113549.1.9.4"));
      localASN1EncodableVector2.add(new DERSet(new DEROctetString(paramArrayOfByte1)));
      localASN1EncodableVector1.add(new DERSequence(localASN1EncodableVector2));
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (paramArrayOfByte2 != null)
      {
        localASN1EncodableVector2 = new ASN1EncodableVector();
        localASN1EncodableVector2.add(new DERObjectIdentifier("1.2.840.113583.1.1.8"));
        localObject1 = new DEROctetString(paramArrayOfByte2);
        localObject2 = new ASN1EncodableVector();
        localObject3 = new ASN1EncodableVector();
        ((ASN1EncodableVector)localObject3).add(OCSPObjectIdentifiers.id_pkix_ocsp_basic);
        ((ASN1EncodableVector)localObject3).add((DEREncodable)localObject1);
        DEREnumerated localDEREnumerated = new DEREnumerated(0);
        ASN1EncodableVector localASN1EncodableVector3 = new ASN1EncodableVector();
        localASN1EncodableVector3.add(localDEREnumerated);
        localASN1EncodableVector3.add(new DERTaggedObject(true, 0, new DERSequence((DEREncodableVector)localObject3)));
        ((ASN1EncodableVector)localObject2).add(new DERSequence(localASN1EncodableVector3));
        localASN1EncodableVector2.add(new DERSet(new DERSequence(new DERTaggedObject(true, 1, new DERSequence((DEREncodableVector)localObject2)))));
        localASN1EncodableVector1.add(new DERSequence(localASN1EncodableVector2));
      }
      else if (!this.crls.isEmpty())
      {
        localASN1EncodableVector2 = new ASN1EncodableVector();
        localASN1EncodableVector2.add(new DERObjectIdentifier("1.2.840.113583.1.1.8"));
        localObject1 = new ASN1EncodableVector();
        localObject2 = this.crls.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = new ASN1InputStream(new ByteArrayInputStream(((X509CRL)((Iterator)localObject2).next()).getEncoded()));
          ((ASN1EncodableVector)localObject1).add(((ASN1InputStream)localObject3).readObject());
        }
        localASN1EncodableVector2.add(new DERSet(new DERSequence(new DERTaggedObject(true, 0, new DERSequence((DEREncodableVector)localObject1)))));
        localASN1EncodableVector1.add(new DERSequence(localASN1EncodableVector2));
      }
      return new DERSet(localASN1EncodableVector1);
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public String getReason()
  {
    return this.reason;
  }

  public void setReason(String paramString)
  {
    this.reason = paramString;
  }

  public String getLocation()
  {
    return this.location;
  }

  public void setLocation(String paramString)
  {
    this.location = paramString;
  }

  public Calendar getSignDate()
  {
    return this.signDate;
  }

  public void setSignDate(Calendar paramCalendar)
  {
    this.signDate = paramCalendar;
  }

  public String getSignName()
  {
    return this.signName;
  }

  public void setSignName(String paramString)
  {
    this.signName = paramString;
  }

  static
  {
    digestNames.put("1.2.840.113549.2.5", "MD5");
    digestNames.put("1.2.840.113549.2.2", "MD2");
    digestNames.put("1.3.14.3.2.26", "SHA1");
    digestNames.put("2.16.840.1.101.3.4.2.4", "SHA224");
    digestNames.put("2.16.840.1.101.3.4.2.1", "SHA256");
    digestNames.put("2.16.840.1.101.3.4.2.2", "SHA384");
    digestNames.put("2.16.840.1.101.3.4.2.3", "SHA512");
    digestNames.put("1.3.36.3.2.2", "RIPEMD128");
    digestNames.put("1.3.36.3.2.1", "RIPEMD160");
    digestNames.put("1.3.36.3.2.3", "RIPEMD256");
    digestNames.put("1.2.840.113549.1.1.4", "MD5");
    digestNames.put("1.2.840.113549.1.1.2", "MD2");
    digestNames.put("1.2.840.113549.1.1.5", "SHA1");
    digestNames.put("1.2.840.113549.1.1.14", "SHA224");
    digestNames.put("1.2.840.113549.1.1.11", "SHA256");
    digestNames.put("1.2.840.113549.1.1.12", "SHA384");
    digestNames.put("1.2.840.113549.1.1.13", "SHA512");
    digestNames.put("1.2.840.113549.2.5", "MD5");
    digestNames.put("1.2.840.113549.2.2", "MD2");
    digestNames.put("1.2.840.10040.4.3", "SHA1");
    digestNames.put("2.16.840.1.101.3.4.3.1", "SHA224");
    digestNames.put("2.16.840.1.101.3.4.3.2", "SHA256");
    digestNames.put("2.16.840.1.101.3.4.3.3", "SHA384");
    digestNames.put("2.16.840.1.101.3.4.3.4", "SHA512");
    digestNames.put("1.3.36.3.3.1.3", "RIPEMD128");
    digestNames.put("1.3.36.3.3.1.2", "RIPEMD160");
    digestNames.put("1.3.36.3.3.1.4", "RIPEMD256");
    algorithmNames.put("1.2.840.113549.1.1.1", "RSA");
    algorithmNames.put("1.2.840.10040.4.1", "DSA");
    algorithmNames.put("1.2.840.113549.1.1.2", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.4", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.5", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.14", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.11", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.12", "RSA");
    algorithmNames.put("1.2.840.113549.1.1.13", "RSA");
    algorithmNames.put("1.2.840.10040.4.3", "DSA");
    algorithmNames.put("2.16.840.1.101.3.4.3.1", "DSA");
    algorithmNames.put("2.16.840.1.101.3.4.3.2", "DSA");
    algorithmNames.put("1.3.36.3.3.1.3", "RSA");
    algorithmNames.put("1.3.36.3.3.1.2", "RSA");
    algorithmNames.put("1.3.36.3.3.1.4", "RSA");
    allowedDigests.put("MD5", "1.2.840.113549.2.5");
    allowedDigests.put("MD2", "1.2.840.113549.2.2");
    allowedDigests.put("SHA1", "1.3.14.3.2.26");
    allowedDigests.put("SHA224", "2.16.840.1.101.3.4.2.4");
    allowedDigests.put("SHA256", "2.16.840.1.101.3.4.2.1");
    allowedDigests.put("SHA384", "2.16.840.1.101.3.4.2.2");
    allowedDigests.put("SHA512", "2.16.840.1.101.3.4.2.3");
    allowedDigests.put("MD-5", "1.2.840.113549.2.5");
    allowedDigests.put("MD-2", "1.2.840.113549.2.2");
    allowedDigests.put("SHA-1", "1.3.14.3.2.26");
    allowedDigests.put("SHA-224", "2.16.840.1.101.3.4.2.4");
    allowedDigests.put("SHA-256", "2.16.840.1.101.3.4.2.1");
    allowedDigests.put("SHA-384", "2.16.840.1.101.3.4.2.2");
    allowedDigests.put("SHA-512", "2.16.840.1.101.3.4.2.3");
    allowedDigests.put("RIPEMD128", "1.3.36.3.2.2");
    allowedDigests.put("RIPEMD-128", "1.3.36.3.2.2");
    allowedDigests.put("RIPEMD160", "1.3.36.3.2.1");
    allowedDigests.put("RIPEMD-160", "1.3.36.3.2.1");
    allowedDigests.put("RIPEMD256", "1.3.36.3.2.3");
    allowedDigests.put("RIPEMD-256", "1.3.36.3.2.3");
  }

  public static class X509NameTokenizer
  {
    private String oid;
    private int index;
    private StringBuffer buf = new StringBuffer();

    public X509NameTokenizer(String paramString)
    {
      this.oid = paramString;
      this.index = -1;
    }

    public boolean hasMoreTokens()
    {
      return this.index != this.oid.length();
    }

    public String nextToken()
    {
      if (this.index == this.oid.length())
        return null;
      int i = this.index + 1;
      int j = 0;
      int k = 0;
      this.buf.setLength(0);
      while (i != this.oid.length())
      {
        char c = this.oid.charAt(i);
        if (c == '"')
        {
          if (k == 0)
            j = j == 0 ? 1 : 0;
          else
            this.buf.append(c);
          k = 0;
        }
        else if ((k != 0) || (j != 0))
        {
          this.buf.append(c);
          k = 0;
        }
        else if (c == '\\')
        {
          k = 1;
        }
        else
        {
          if (c == ',')
            break;
          this.buf.append(c);
        }
        i++;
      }
      this.index = i;
      return this.buf.toString().trim();
    }
  }

  public static class X509Name
  {
    public static final DERObjectIdentifier C = new DERObjectIdentifier("2.5.4.6");
    public static final DERObjectIdentifier O = new DERObjectIdentifier("2.5.4.10");
    public static final DERObjectIdentifier OU = new DERObjectIdentifier("2.5.4.11");
    public static final DERObjectIdentifier T = new DERObjectIdentifier("2.5.4.12");
    public static final DERObjectIdentifier CN = new DERObjectIdentifier("2.5.4.3");
    public static final DERObjectIdentifier SN = new DERObjectIdentifier("2.5.4.5");
    public static final DERObjectIdentifier L = new DERObjectIdentifier("2.5.4.7");
    public static final DERObjectIdentifier ST = new DERObjectIdentifier("2.5.4.8");
    public static final DERObjectIdentifier SURNAME = new DERObjectIdentifier("2.5.4.4");
    public static final DERObjectIdentifier GIVENNAME = new DERObjectIdentifier("2.5.4.42");
    public static final DERObjectIdentifier INITIALS = new DERObjectIdentifier("2.5.4.43");
    public static final DERObjectIdentifier GENERATION = new DERObjectIdentifier("2.5.4.44");
    public static final DERObjectIdentifier UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");
    public static final DERObjectIdentifier EmailAddress = new DERObjectIdentifier("1.2.840.113549.1.9.1");
    public static final DERObjectIdentifier E = EmailAddress;
    public static final DERObjectIdentifier DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");
    public static final DERObjectIdentifier UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");
    public static HashMap DefaultSymbols = new HashMap();
    public HashMap values = new HashMap();

    public X509Name(ASN1Sequence paramASN1Sequence)
    {
      Enumeration localEnumeration = paramASN1Sequence.getObjects();
      while (localEnumeration.hasMoreElements())
      {
        ASN1Set localASN1Set = (ASN1Set)localEnumeration.nextElement();
        for (int i = 0; i < localASN1Set.size(); i++)
        {
          ASN1Sequence localASN1Sequence = (ASN1Sequence)localASN1Set.getObjectAt(i);
          String str = (String)DefaultSymbols.get(localASN1Sequence.getObjectAt(0));
          if (str == null)
            continue;
          ArrayList localArrayList = (ArrayList)this.values.get(str);
          if (localArrayList == null)
          {
            localArrayList = new ArrayList();
            this.values.put(str, localArrayList);
          }
          localArrayList.add(((DERString)localASN1Sequence.getObjectAt(1)).getString());
        }
      }
    }

    public X509Name(String paramString)
    {
      PdfPKCS7.X509NameTokenizer localX509NameTokenizer = new PdfPKCS7.X509NameTokenizer(paramString);
      while (localX509NameTokenizer.hasMoreTokens())
      {
        String str1 = localX509NameTokenizer.nextToken();
        int i = str1.indexOf('=');
        if (i == -1)
          throw new IllegalArgumentException("badly formated directory string");
        String str2 = str1.substring(0, i).toUpperCase();
        String str3 = str1.substring(i + 1);
        ArrayList localArrayList = (ArrayList)this.values.get(str2);
        if (localArrayList == null)
        {
          localArrayList = new ArrayList();
          this.values.put(str2, localArrayList);
        }
        localArrayList.add(str3);
      }
    }

    public String getField(String paramString)
    {
      ArrayList localArrayList = (ArrayList)this.values.get(paramString);
      return localArrayList == null ? null : (String)localArrayList.get(0);
    }

    public ArrayList getFieldArray(String paramString)
    {
      ArrayList localArrayList = (ArrayList)this.values.get(paramString);
      return localArrayList == null ? null : localArrayList;
    }

    public HashMap getFields()
    {
      return this.values;
    }

    public String toString()
    {
      return this.values.toString();
    }

    static
    {
      DefaultSymbols.put(C, "C");
      DefaultSymbols.put(O, "O");
      DefaultSymbols.put(T, "T");
      DefaultSymbols.put(OU, "OU");
      DefaultSymbols.put(CN, "CN");
      DefaultSymbols.put(L, "L");
      DefaultSymbols.put(ST, "ST");
      DefaultSymbols.put(SN, "SN");
      DefaultSymbols.put(EmailAddress, "E");
      DefaultSymbols.put(DC, "DC");
      DefaultSymbols.put(UID, "UID");
      DefaultSymbols.put(SURNAME, "SURNAME");
      DefaultSymbols.put(GIVENNAME, "GIVENNAME");
      DefaultSymbols.put(INITIALS, "INITIALS");
      DefaultSymbols.put(GENERATION, "GENERATION");
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPKCS7
 * JD-Core Version:    0.6.0
 */