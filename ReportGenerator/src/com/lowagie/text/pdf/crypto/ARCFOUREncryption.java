package com.lowagie.text.pdf.crypto;

public class ARCFOUREncryption
{
  private byte[] state = new byte[256];
  private int x;
  private int y;

  public void prepareARCFOURKey(byte[] paramArrayOfByte)
  {
    prepareARCFOURKey(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void prepareARCFOURKey(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < 256; k++)
      this.state[k] = (byte)k;
    this.x = 0;
    this.y = 0;
    for (int m = 0; m < 256; m++)
    {
      j = paramArrayOfByte[(i + paramInt1)] + this.state[m] + j & 0xFF;
      k = this.state[m];
      this.state[m] = this.state[j];
      this.state[j] = k;
      i = (i + 1) % paramInt2;
    }
  }

  public void encryptARCFOUR(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    int i = paramInt2 + paramInt1;
    for (int k = paramInt1; k < i; k++)
    {
      this.x = (this.x + 1 & 0xFF);
      this.y = (this.state[this.x] + this.y & 0xFF);
      int j = this.state[this.x];
      this.state[this.x] = this.state[this.y];
      this.state[this.y] = j;
      paramArrayOfByte2[(k - paramInt1 + paramInt3)] = (byte)(paramArrayOfByte1[k] ^ this.state[(this.state[this.x] + this.state[this.y] & 0xFF)]);
    }
  }

  public void encryptARCFOUR(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    encryptARCFOUR(paramArrayOfByte, paramInt1, paramInt2, paramArrayOfByte, paramInt1);
  }

  public void encryptARCFOUR(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    encryptARCFOUR(paramArrayOfByte1, 0, paramArrayOfByte1.length, paramArrayOfByte2, 0);
  }

  public void encryptARCFOUR(byte[] paramArrayOfByte)
  {
    encryptARCFOUR(paramArrayOfByte, 0, paramArrayOfByte.length, paramArrayOfByte, 0);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.crypto.ARCFOUREncryption
 * JD-Core Version:    0.6.0
 */