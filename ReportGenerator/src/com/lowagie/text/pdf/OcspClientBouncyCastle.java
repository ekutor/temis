package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Vector;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.CertificateStatus;
import org.bouncycastle.ocsp.OCSPException;
import org.bouncycastle.ocsp.OCSPReq;
import org.bouncycastle.ocsp.OCSPReqGenerator;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.ocsp.RevokedStatus;
import org.bouncycastle.ocsp.SingleResp;

public class OcspClientBouncyCastle
  implements OcspClient
{
  private X509Certificate rootCert;
  private X509Certificate checkCert;
  private String url;

  public OcspClientBouncyCastle(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, String paramString)
  {
    this.checkCert = paramX509Certificate1;
    this.rootCert = paramX509Certificate2;
    this.url = paramString;
  }

  private static OCSPReq generateOCSPRequest(X509Certificate paramX509Certificate, BigInteger paramBigInteger)
    throws OCSPException, IOException
  {
    Security.addProvider(new BouncyCastleProvider());
    CertificateID localCertificateID = new CertificateID("1.3.14.3.2.26", paramX509Certificate, paramBigInteger);
    OCSPReqGenerator localOCSPReqGenerator = new OCSPReqGenerator();
    localOCSPReqGenerator.addRequest(localCertificateID);
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    localVector1.add(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
    localVector2.add(new X509Extension(false, new DEROctetString(new DEROctetString(PdfEncryption.createDocumentId()).getEncoded())));
    localOCSPReqGenerator.setRequestExtensions(new X509Extensions(localVector1, localVector2));
    return localOCSPReqGenerator.generate();
  }

  public byte[] getEncoded()
  {
    try
    {
      OCSPReq localOCSPReq = generateOCSPRequest(this.rootCert, this.checkCert.getSerialNumber());
      byte[] arrayOfByte = localOCSPReq.getEncoded();
      URL localURL = new URL(this.url);
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
      localHttpURLConnection.setRequestProperty("Content-Type", "application/ocsp-request");
      localHttpURLConnection.setRequestProperty("Accept", "application/ocsp-response");
      localHttpURLConnection.setDoOutput(true);
      OutputStream localOutputStream = localHttpURLConnection.getOutputStream();
      DataOutputStream localDataOutputStream = new DataOutputStream(new BufferedOutputStream(localOutputStream));
      localDataOutputStream.write(arrayOfByte);
      localDataOutputStream.flush();
      localDataOutputStream.close();
      if (localHttpURLConnection.getResponseCode() / 100 != 2)
        throw new IOException("Invalid HTTP response");
      InputStream localInputStream = (InputStream)localHttpURLConnection.getContent();
      OCSPResp localOCSPResp = new OCSPResp(localInputStream);
      if (localOCSPResp.getStatus() != 0)
        throw new IOException("Invalid status: " + localOCSPResp.getStatus());
      BasicOCSPResp localBasicOCSPResp = (BasicOCSPResp)localOCSPResp.getResponseObject();
      if (localBasicOCSPResp != null)
      {
        SingleResp[] arrayOfSingleResp = localBasicOCSPResp.getResponses();
        if (arrayOfSingleResp.length == 1)
        {
          SingleResp localSingleResp = arrayOfSingleResp[0];
          Object localObject = localSingleResp.getCertStatus();
          if (localObject == CertificateStatus.GOOD)
            return localBasicOCSPResp.getEncoded();
          if ((localObject instanceof RevokedStatus))
            throw new IOException("OCSP Status is revoked!");
          throw new IOException("OCSP Status is unknown!");
        }
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    return null;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.OcspClientBouncyCastle
 * JD-Core Version:    0.6.0
 */