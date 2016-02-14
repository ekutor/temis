package com.lowagie.text.pdf.crypto;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class AESCipher
{
  private PaddedBufferedBlockCipher bp;

  public AESCipher(boolean paramBoolean, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    AESFastEngine localAESFastEngine = new AESFastEngine();
    CBCBlockCipher localCBCBlockCipher = new CBCBlockCipher(localAESFastEngine);
    this.bp = new PaddedBufferedBlockCipher(localCBCBlockCipher);
    KeyParameter localKeyParameter = new KeyParameter(paramArrayOfByte1);
    ParametersWithIV localParametersWithIV = new ParametersWithIV(localKeyParameter, paramArrayOfByte2);
    this.bp.init(paramBoolean, localParametersWithIV);
  }

  public byte[] update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = this.bp.getUpdateOutputSize(paramInt2);
    byte[] arrayOfByte = null;
    if (i > 0)
      arrayOfByte = new byte[i];
    else
      i = 0;
    this.bp.processBytes(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte, 0);
    return arrayOfByte;
  }

  public byte[] doFinal()
  {
    int i = this.bp.getOutputSize(0);
    byte[] arrayOfByte1 = new byte[i];
    int j = 0;
    try
    {
      j = this.bp.doFinal(arrayOfByte1, 0);
    }
    catch (Exception localException)
    {
      return arrayOfByte1;
    }
    if (j != arrayOfByte1.length)
    {
      byte[] arrayOfByte2 = new byte[j];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, j);
      return arrayOfByte2;
    }
    return arrayOfByte1;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.crypto.AESCipher
 * JD-Core Version:    0.6.0
 */