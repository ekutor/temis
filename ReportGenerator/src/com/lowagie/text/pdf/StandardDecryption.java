package com.lowagie.text.pdf;

import com.lowagie.text.pdf.crypto.AESCipher;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;

public class StandardDecryption
{
  protected ARCFOUREncryption arcfour;
  protected AESCipher cipher;
  private byte[] key;
  private static final int AES_128 = 4;
  private boolean aes;
  private boolean initiated;
  private byte[] iv = new byte[16];
  private int ivptr;

  public StandardDecryption(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    this.aes = (paramInt3 == 4);
    if (this.aes)
    {
      this.key = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, this.key, 0, paramInt2);
    }
    else
    {
      this.arcfour = new ARCFOUREncryption();
      this.arcfour.prepareARCFOURKey(paramArrayOfByte, paramInt1, paramInt2);
    }
  }

  public byte[] update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.aes)
    {
      if (this.initiated)
        return this.cipher.update(paramArrayOfByte, paramInt1, paramInt2);
      int i = Math.min(this.iv.length - this.ivptr, paramInt2);
      System.arraycopy(paramArrayOfByte, paramInt1, this.iv, this.ivptr, i);
      paramInt1 += i;
      paramInt2 -= i;
      this.ivptr += i;
      if (this.ivptr == this.iv.length)
      {
        this.cipher = new AESCipher(false, this.key, this.iv);
        this.initiated = true;
        if (paramInt2 > 0)
          return this.cipher.update(paramArrayOfByte, paramInt1, paramInt2);
      }
      return null;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    this.arcfour.encryptARCFOUR(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte, 0);
    return arrayOfByte;
  }

  public byte[] finish()
  {
    if (this.aes)
      return this.cipher.doFinal();
    return null;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.StandardDecryption
 * JD-Core Version:    0.6.0
 */