package com.lowagie.text.pdf;

import B;
import com.lowagie.text.pdf.codec.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;

public class TSAClientBouncyCastle
  implements TSAClient
{
  protected String tsaURL;
  protected String tsaUsername;
  protected String tsaPassword;
  protected int tokSzEstimate;

  public TSAClientBouncyCastle(String paramString)
  {
    this(paramString, null, null, 4096);
  }

  public TSAClientBouncyCastle(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, paramString2, paramString3, 4096);
  }

  public TSAClientBouncyCastle(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    this.tsaURL = paramString1;
    this.tsaUsername = paramString2;
    this.tsaPassword = paramString3;
    this.tokSzEstimate = paramInt;
  }

  public int getTokenSizeEstimate()
  {
    return this.tokSzEstimate;
  }

  public byte[] getTimeStampToken(PdfPKCS7 paramPdfPKCS7, byte[] paramArrayOfByte)
    throws Exception
  {
    return getTimeStampToken(paramArrayOfByte);
  }

  protected byte[] getTimeStampToken(byte[] paramArrayOfByte)
    throws Exception
  {
    byte[] arrayOfByte1 = null;
    try
    {
      TimeStampRequestGenerator localTimeStampRequestGenerator = new TimeStampRequestGenerator();
      localTimeStampRequestGenerator.setCertReq(true);
      BigInteger localBigInteger = BigInteger.valueOf(System.currentTimeMillis());
      TimeStampRequest localTimeStampRequest = localTimeStampRequestGenerator.generate(X509ObjectIdentifiers.id_SHA1.getId(), paramArrayOfByte, localBigInteger);
      byte[] arrayOfByte2 = localTimeStampRequest.getEncoded();
      arrayOfByte1 = getTSAResponse(arrayOfByte2);
      TimeStampResponse localTimeStampResponse = new TimeStampResponse(arrayOfByte1);
      localTimeStampResponse.validate(localTimeStampRequest);
      PKIFailureInfo localPKIFailureInfo = localTimeStampResponse.getFailInfo();
      int i = localPKIFailureInfo == null ? 0 : localPKIFailureInfo.intValue();
      if (i != 0)
        throw new Exception("Invalid TSA '" + this.tsaURL + "' response, code " + i);
      TimeStampToken localTimeStampToken = localTimeStampResponse.getTimeStampToken();
      if (localTimeStampToken == null)
        throw new Exception("TSA '" + this.tsaURL + "' failed to return time stamp token: " + localTimeStampResponse.getStatusString());
      TimeStampTokenInfo localTimeStampTokenInfo = localTimeStampToken.getTimeStampInfo();
      byte[] arrayOfByte3 = localTimeStampToken.getEncoded();
      long l = System.currentTimeMillis();
      this.tokSzEstimate = (arrayOfByte3.length + 32);
      return arrayOfByte3;
    }
    catch (Exception localException)
    {
      throw localException;
    }
    catch (Throwable localThrowable)
    {
    }
    throw new Exception("Failed to get TSA response from '" + this.tsaURL + "'", localThrowable);
  }

  protected byte[] getTSAResponse(byte[] paramArrayOfByte)
    throws Exception
  {
    URL localURL = new URL(this.tsaURL);
    URLConnection localURLConnection = localURL.openConnection();
    localURLConnection.setDoInput(true);
    localURLConnection.setDoOutput(true);
    localURLConnection.setUseCaches(false);
    localURLConnection.setRequestProperty("Content-Type", "application/timestamp-query");
    localURLConnection.setRequestProperty("Content-Transfer-Encoding", "binary");
    if ((this.tsaUsername != null) && (!this.tsaUsername.equals("")))
    {
      localObject = this.tsaUsername + ":" + this.tsaPassword;
      localURLConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBytes(((String)localObject).getBytes())));
    }
    Object localObject = localURLConnection.getOutputStream();
    ((OutputStream)localObject).write(paramArrayOfByte);
    ((OutputStream)localObject).close();
    InputStream localInputStream = localURLConnection.getInputStream();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte1 = new byte[1024];
    int i = 0;
    while ((i = localInputStream.read(arrayOfByte1, 0, arrayOfByte1.length)) >= 0)
      localByteArrayOutputStream.write(arrayOfByte1, 0, i);
    byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
    String str = localURLConnection.getContentEncoding();
    if ((str != null) && (str.equalsIgnoreCase("base64")))
      arrayOfByte2 = Base64.decode(new String(arrayOfByte2));
    return (B)arrayOfByte2;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.TSAClientBouncyCastle
 * JD-Core Version:    0.6.0
 */