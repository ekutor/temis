package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class PdfEncryption
{
  public static final int STANDARD_ENCRYPTION_40 = 2;
  public static final int STANDARD_ENCRYPTION_128 = 3;
  public static final int AES_128 = 4;
  private static final byte[] pad = { 40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122 };
  private static final byte[] salt = { 115, 65, 108, 84 };
  private static final byte[] metadataPad = { -1, -1, -1, -1 };
  byte[] key;
  int keySize;
  byte[] mkey;
  byte[] extra = new byte[5];
  MessageDigest md5;
  byte[] ownerKey = new byte[32];
  byte[] userKey = new byte[32];
  protected PdfPublicKeySecurityHandler publicKeyHandler = null;
  int permissions;
  byte[] documentID;
  static long seq = System.currentTimeMillis();
  private int revision;
  private ARCFOUREncryption arcfour = new ARCFOUREncryption();
  private int keyLength;
  private boolean encryptMetadata;
  private boolean embeddedFilesOnly;
  private int cryptoMode;

  public PdfEncryption()
  {
    try
    {
      this.md5 = MessageDigest.getInstance("MD5");
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    this.publicKeyHandler = new PdfPublicKeySecurityHandler();
  }

  public PdfEncryption(PdfEncryption paramPdfEncryption)
  {
    this();
    this.mkey = ((byte[])paramPdfEncryption.mkey.clone());
    this.ownerKey = ((byte[])paramPdfEncryption.ownerKey.clone());
    this.userKey = ((byte[])paramPdfEncryption.userKey.clone());
    this.permissions = paramPdfEncryption.permissions;
    if (paramPdfEncryption.documentID != null)
      this.documentID = ((byte[])paramPdfEncryption.documentID.clone());
    this.revision = paramPdfEncryption.revision;
    this.keyLength = paramPdfEncryption.keyLength;
    this.encryptMetadata = paramPdfEncryption.encryptMetadata;
    this.embeddedFilesOnly = paramPdfEncryption.embeddedFilesOnly;
    this.publicKeyHandler = paramPdfEncryption.publicKeyHandler;
  }

  public void setCryptoMode(int paramInt1, int paramInt2)
  {
    this.cryptoMode = paramInt1;
    this.encryptMetadata = ((paramInt1 & 0x8) == 0);
    this.embeddedFilesOnly = ((paramInt1 & 0x18) != 0);
    paramInt1 &= 7;
    switch (paramInt1)
    {
    case 0:
      this.encryptMetadata = true;
      this.embeddedFilesOnly = false;
      this.keyLength = 40;
      this.revision = 2;
      break;
    case 1:
      this.embeddedFilesOnly = false;
      if (paramInt2 > 0)
        this.keyLength = paramInt2;
      else
        this.keyLength = 128;
      this.revision = 3;
      break;
    case 2:
      this.keyLength = 128;
      this.revision = 4;
      break;
    default:
      throw new IllegalArgumentException("No valid encryption mode");
    }
  }

  public int getCryptoMode()
  {
    return this.cryptoMode;
  }

  public boolean isMetadataEncrypted()
  {
    return this.encryptMetadata;
  }

  public boolean isEmbeddedFilesOnly()
  {
    return this.embeddedFilesOnly;
  }

  private byte[] padPassword(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[32];
    if (paramArrayOfByte == null)
    {
      System.arraycopy(pad, 0, arrayOfByte, 0, 32);
    }
    else
    {
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, 32));
      if (paramArrayOfByte.length < 32)
        System.arraycopy(pad, 0, arrayOfByte, paramArrayOfByte.length, 32 - paramArrayOfByte.length);
    }
    return arrayOfByte;
  }

  private byte[] computeOwnerKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    byte[] arrayOfByte1 = new byte[32];
    byte[] arrayOfByte2 = this.md5.digest(paramArrayOfByte2);
    byte[] arrayOfByte3;
    int i;
    if ((this.revision == 3) || (this.revision == 4))
    {
      arrayOfByte3 = new byte[this.keyLength / 8];
      for (i = 0; i < 50; i++)
        System.arraycopy(this.md5.digest(arrayOfByte2), 0, arrayOfByte2, 0, arrayOfByte3.length);
      System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, 0, 32);
      i = 0;
    }
    while (i < 20)
    {
      for (int j = 0; j < arrayOfByte3.length; j++)
        arrayOfByte3[j] = (byte)(arrayOfByte2[j] ^ i);
      this.arcfour.prepareARCFOURKey(arrayOfByte3);
      this.arcfour.encryptARCFOUR(arrayOfByte1);
      i++;
      continue;
      this.arcfour.prepareARCFOURKey(arrayOfByte2, 0, 5);
      this.arcfour.encryptARCFOUR(paramArrayOfByte1, arrayOfByte1);
    }
    return arrayOfByte1;
  }

  private void setupGlobalEncryptionKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
  {
    this.documentID = paramArrayOfByte1;
    this.ownerKey = paramArrayOfByte3;
    this.permissions = paramInt;
    this.mkey = new byte[this.keyLength / 8];
    this.md5.reset();
    this.md5.update(paramArrayOfByte2);
    this.md5.update(paramArrayOfByte3);
    byte[] arrayOfByte1 = new byte[4];
    arrayOfByte1[0] = (byte)paramInt;
    arrayOfByte1[1] = (byte)(paramInt >> 8);
    arrayOfByte1[2] = (byte)(paramInt >> 16);
    arrayOfByte1[3] = (byte)(paramInt >> 24);
    this.md5.update(arrayOfByte1, 0, 4);
    if (paramArrayOfByte1 != null)
      this.md5.update(paramArrayOfByte1);
    if (!this.encryptMetadata)
      this.md5.update(metadataPad);
    byte[] arrayOfByte2 = new byte[this.mkey.length];
    System.arraycopy(this.md5.digest(), 0, arrayOfByte2, 0, this.mkey.length);
    if ((this.revision == 3) || (this.revision == 4))
      for (int i = 0; i < 50; i++)
        System.arraycopy(this.md5.digest(arrayOfByte2), 0, arrayOfByte2, 0, this.mkey.length);
    System.arraycopy(arrayOfByte2, 0, this.mkey, 0, this.mkey.length);
  }

  private void setupUserKey()
  {
    byte[] arrayOfByte;
    int i;
    if ((this.revision == 3) || (this.revision == 4))
    {
      this.md5.update(pad);
      arrayOfByte = this.md5.digest(this.documentID);
      System.arraycopy(arrayOfByte, 0, this.userKey, 0, 16);
      for (i = 16; i < 32; i++)
        this.userKey[i] = 0;
      i = 0;
    }
    while (i < 20)
    {
      for (int j = 0; j < this.mkey.length; j++)
        arrayOfByte[j] = (byte)(this.mkey[j] ^ i);
      this.arcfour.prepareARCFOURKey(arrayOfByte, 0, this.mkey.length);
      this.arcfour.encryptARCFOUR(this.userKey, 0, 16);
      i++;
      continue;
      this.arcfour.prepareARCFOURKey(this.mkey);
      this.arcfour.encryptARCFOUR(pad, this.userKey);
    }
  }

  public void setupAllKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    if ((paramArrayOfByte2 == null) || (paramArrayOfByte2.length == 0))
      paramArrayOfByte2 = this.md5.digest(createDocumentId());
    paramInt |= ((this.revision == 3) || (this.revision == 4) ? -3904 : -64);
    paramInt &= -4;
    byte[] arrayOfByte1 = padPassword(paramArrayOfByte1);
    byte[] arrayOfByte2 = padPassword(paramArrayOfByte2);
    this.ownerKey = computeOwnerKey(arrayOfByte1, arrayOfByte2);
    this.documentID = createDocumentId();
    setupByUserPad(this.documentID, arrayOfByte1, this.ownerKey, paramInt);
  }

  public static byte[] createDocumentId()
  {
    MessageDigest localMessageDigest;
    try
    {
      localMessageDigest = MessageDigest.getInstance("MD5");
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    long l1 = System.currentTimeMillis();
    long l2 = Runtime.getRuntime().freeMemory();
    String str = l1 + "+" + l2 + "+" + seq++;
    return localMessageDigest.digest(str.getBytes());
  }

  public void setupByUserPassword(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
  {
    setupByUserPad(paramArrayOfByte1, padPassword(paramArrayOfByte2), paramArrayOfByte3, paramInt);
  }

  private void setupByUserPad(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
  {
    setupGlobalEncryptionKey(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramInt);
    setupUserKey();
  }

  public void setupByOwnerPassword(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt)
  {
    setupByOwnerPad(paramArrayOfByte1, padPassword(paramArrayOfByte2), paramArrayOfByte3, paramArrayOfByte4, paramInt);
  }

  private void setupByOwnerPad(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, int paramInt)
  {
    byte[] arrayOfByte = computeOwnerKey(paramArrayOfByte4, paramArrayOfByte2);
    setupGlobalEncryptionKey(paramArrayOfByte1, arrayOfByte, paramArrayOfByte4, paramInt);
    setupUserKey();
  }

  public void setupByEncryptionKey(byte[] paramArrayOfByte, int paramInt)
  {
    this.mkey = new byte[paramInt / 8];
    System.arraycopy(paramArrayOfByte, 0, this.mkey, 0, this.mkey.length);
  }

  public void setHashKey(int paramInt1, int paramInt2)
  {
    this.md5.reset();
    this.extra[0] = (byte)paramInt1;
    this.extra[1] = (byte)(paramInt1 >> 8);
    this.extra[2] = (byte)(paramInt1 >> 16);
    this.extra[3] = (byte)paramInt2;
    this.extra[4] = (byte)(paramInt2 >> 8);
    this.md5.update(this.mkey);
    this.md5.update(this.extra);
    if (this.revision == 4)
      this.md5.update(salt);
    this.key = this.md5.digest();
    this.keySize = (this.mkey.length + 5);
    if (this.keySize > 16)
      this.keySize = 16;
  }

  public static PdfObject createInfoId(byte[] paramArrayOfByte)
  {
    ByteBuffer localByteBuffer = new ByteBuffer(90);
    localByteBuffer.append('[').append('<');
    for (int i = 0; i < 16; i++)
      localByteBuffer.appendHex(paramArrayOfByte[i]);
    localByteBuffer.append('>').append('<');
    paramArrayOfByte = createDocumentId();
    for (i = 0; i < 16; i++)
      localByteBuffer.appendHex(paramArrayOfByte[i]);
    localByteBuffer.append('>').append(']');
    return new PdfLiteral(localByteBuffer.toByteArray());
  }

  public PdfDictionary getEncryptionDictionary()
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    Object localObject1;
    Object localObject2;
    if (this.publicKeyHandler.getRecipientsSize() > 0)
    {
      localObject1 = null;
      localPdfDictionary.put(PdfName.FILTER, PdfName.PUBSEC);
      localPdfDictionary.put(PdfName.R, new PdfNumber(this.revision));
      try
      {
        localObject1 = this.publicKeyHandler.getEncodedRecipients();
      }
      catch (Exception localException1)
      {
        throw new ExceptionConverter(localException1);
      }
      if (this.revision == 2)
      {
        localPdfDictionary.put(PdfName.V, new PdfNumber(1));
        localPdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
        localPdfDictionary.put(PdfName.RECIPIENTS, (PdfObject)localObject1);
      }
      else if ((this.revision == 3) && (this.encryptMetadata))
      {
        localPdfDictionary.put(PdfName.V, new PdfNumber(2));
        localPdfDictionary.put(PdfName.LENGTH, new PdfNumber(128));
        localPdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
        localPdfDictionary.put(PdfName.RECIPIENTS, (PdfObject)localObject1);
      }
      else
      {
        localPdfDictionary.put(PdfName.R, new PdfNumber(4));
        localPdfDictionary.put(PdfName.V, new PdfNumber(4));
        localPdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S5);
        localObject2 = new PdfDictionary();
        ((PdfDictionary)localObject2).put(PdfName.RECIPIENTS, (PdfObject)localObject1);
        if (!this.encryptMetadata)
          ((PdfDictionary)localObject2).put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
        if (this.revision == 4)
          ((PdfDictionary)localObject2).put(PdfName.CFM, PdfName.AESV2);
        else
          ((PdfDictionary)localObject2).put(PdfName.CFM, PdfName.V2);
        localObject3 = new PdfDictionary();
        ((PdfDictionary)localObject3).put(PdfName.DEFAULTCRYPTFILTER, (PdfObject)localObject2);
        localPdfDictionary.put(PdfName.CF, (PdfObject)localObject3);
        if (this.embeddedFilesOnly)
        {
          localPdfDictionary.put(PdfName.EFF, PdfName.DEFAULTCRYPTFILTER);
          localPdfDictionary.put(PdfName.STRF, PdfName.IDENTITY);
          localPdfDictionary.put(PdfName.STMF, PdfName.IDENTITY);
        }
        else
        {
          localPdfDictionary.put(PdfName.STRF, PdfName.DEFAULTCRYPTFILTER);
          localPdfDictionary.put(PdfName.STMF, PdfName.DEFAULTCRYPTFILTER);
        }
      }
      localObject2 = null;
      Object localObject3 = null;
      try
      {
        localObject2 = MessageDigest.getInstance("SHA-1");
        ((MessageDigest)localObject2).update(this.publicKeyHandler.getSeed());
        for (int i = 0; i < this.publicKeyHandler.getRecipientsSize(); i++)
        {
          localObject3 = this.publicKeyHandler.getEncodedRecipient(i);
          ((MessageDigest)localObject2).update(localObject3);
        }
        if (!this.encryptMetadata)
          ((MessageDigest)localObject2).update(new byte[] { -1, -1, -1, -1 });
      }
      catch (Exception localException2)
      {
        throw new ExceptionConverter(localException2);
      }
      byte[] arrayOfByte = ((MessageDigest)localObject2).digest();
      setupByEncryptionKey(arrayOfByte, this.keyLength);
    }
    else
    {
      localPdfDictionary.put(PdfName.FILTER, PdfName.STANDARD);
      localPdfDictionary.put(PdfName.O, new PdfLiteral(PdfContentByte.escapeString(this.ownerKey)));
      localPdfDictionary.put(PdfName.U, new PdfLiteral(PdfContentByte.escapeString(this.userKey)));
      localPdfDictionary.put(PdfName.P, new PdfNumber(this.permissions));
      localPdfDictionary.put(PdfName.R, new PdfNumber(this.revision));
      if (this.revision == 2)
      {
        localPdfDictionary.put(PdfName.V, new PdfNumber(1));
      }
      else if ((this.revision == 3) && (this.encryptMetadata))
      {
        localPdfDictionary.put(PdfName.V, new PdfNumber(2));
        localPdfDictionary.put(PdfName.LENGTH, new PdfNumber(128));
      }
      else
      {
        if (!this.encryptMetadata)
          localPdfDictionary.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
        localPdfDictionary.put(PdfName.R, new PdfNumber(4));
        localPdfDictionary.put(PdfName.V, new PdfNumber(4));
        localPdfDictionary.put(PdfName.LENGTH, new PdfNumber(128));
        localObject1 = new PdfDictionary();
        ((PdfDictionary)localObject1).put(PdfName.LENGTH, new PdfNumber(16));
        if (this.embeddedFilesOnly)
        {
          ((PdfDictionary)localObject1).put(PdfName.AUTHEVENT, PdfName.EFOPEN);
          localPdfDictionary.put(PdfName.EFF, PdfName.STDCF);
          localPdfDictionary.put(PdfName.STRF, PdfName.IDENTITY);
          localPdfDictionary.put(PdfName.STMF, PdfName.IDENTITY);
        }
        else
        {
          ((PdfDictionary)localObject1).put(PdfName.AUTHEVENT, PdfName.DOCOPEN);
          localPdfDictionary.put(PdfName.STRF, PdfName.STDCF);
          localPdfDictionary.put(PdfName.STMF, PdfName.STDCF);
        }
        if (this.revision == 4)
          ((PdfDictionary)localObject1).put(PdfName.CFM, PdfName.AESV2);
        else
          ((PdfDictionary)localObject1).put(PdfName.CFM, PdfName.V2);
        localObject2 = new PdfDictionary();
        ((PdfDictionary)localObject2).put(PdfName.STDCF, (PdfObject)localObject1);
        localPdfDictionary.put(PdfName.CF, (PdfObject)localObject2);
      }
    }
    return (PdfDictionary)(PdfDictionary)(PdfDictionary)localPdfDictionary;
  }

  public PdfObject getFileID()
  {
    return createInfoId(this.documentID);
  }

  public OutputStreamEncryption getEncryptionStream(OutputStream paramOutputStream)
  {
    return new OutputStreamEncryption(paramOutputStream, this.key, 0, this.keySize, this.revision);
  }

  public int calculateStreamSize(int paramInt)
  {
    if (this.revision == 4)
      return (paramInt & 0x7FFFFFF0) + 32;
    return paramInt;
  }

  public byte[] encryptByteArray(byte[] paramArrayOfByte)
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      OutputStreamEncryption localOutputStreamEncryption = getEncryptionStream(localByteArrayOutputStream);
      localOutputStreamEncryption.write(paramArrayOfByte);
      localOutputStreamEncryption.finish();
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  public StandardDecryption getDecryptor()
  {
    return new StandardDecryption(this.key, 0, this.keySize, this.revision);
  }

  public byte[] decryptByteArray(byte[] paramArrayOfByte)
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      StandardDecryption localStandardDecryption = getDecryptor();
      byte[] arrayOfByte = localStandardDecryption.update(paramArrayOfByte, 0, paramArrayOfByte.length);
      if (arrayOfByte != null)
        localByteArrayOutputStream.write(arrayOfByte);
      arrayOfByte = localStandardDecryption.finish();
      if (arrayOfByte != null)
        localByteArrayOutputStream.write(arrayOfByte);
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  public void addRecipient(Certificate paramCertificate, int paramInt)
  {
    this.documentID = createDocumentId();
    this.publicKeyHandler.addRecipient(new PdfPublicKeyRecipient(paramCertificate, paramInt));
  }

  public byte[] computeUserPassword(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = computeOwnerKey(this.ownerKey, padPassword(paramArrayOfByte));
    for (int i = 0; i < arrayOfByte1.length; i++)
    {
      int j = 1;
      for (int k = 0; k < arrayOfByte1.length - i; k++)
      {
        if (arrayOfByte1[(i + k)] == pad[k])
          continue;
        j = 0;
        break;
      }
      if (j == 0)
        continue;
      byte[] arrayOfByte2 = new byte[i];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
      return arrayOfByte2;
    }
    return arrayOfByte1;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfEncryption
 * JD-Core Version:    0.6.0
 */