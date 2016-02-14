package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.crypto.AESCipher;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;
import com.lowagie.text.pdf.crypto.IVGenerator;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamEncryption extends OutputStream
{
  protected OutputStream out;
  protected ARCFOUREncryption arcfour;
  protected AESCipher cipher;
  private byte[] sb = new byte[1];
  private static final int AES_128 = 4;
  private boolean aes;
  private boolean finished;

  public OutputStreamEncryption(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      this.out = paramOutputStream;
      this.aes = (paramInt3 == 4);
      if (this.aes)
      {
        byte[] arrayOfByte1 = IVGenerator.getIV();
        byte[] arrayOfByte2 = new byte[paramInt2];
        System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte2, 0, paramInt2);
        this.cipher = new AESCipher(true, arrayOfByte2, arrayOfByte1);
        write(arrayOfByte1);
      }
      else
      {
        this.arcfour = new ARCFOUREncryption();
        this.arcfour.prepareARCFOURKey(paramArrayOfByte, paramInt1, paramInt2);
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public OutputStreamEncryption(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt)
  {
    this(paramOutputStream, paramArrayOfByte, 0, paramArrayOfByte.length, paramInt);
  }

  public void close()
    throws IOException
  {
    finish();
    this.out.close();
  }

  public void flush()
    throws IOException
  {
    this.out.flush();
  }

  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void write(int paramInt)
    throws IOException
  {
    this.sb[0] = (byte)paramInt;
    write(this.sb, 0, 1);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    byte[] arrayOfByte;
    if (this.aes)
    {
      arrayOfByte = this.cipher.update(paramArrayOfByte, paramInt1, paramInt2);
      if ((arrayOfByte == null) || (arrayOfByte.length == 0))
        return;
      this.out.write(arrayOfByte, 0, arrayOfByte.length);
    }
    else
    {
      arrayOfByte = new byte[Math.min(paramInt2, 4192)];
      while (paramInt2 > 0)
      {
        int i = Math.min(paramInt2, arrayOfByte.length);
        this.arcfour.encryptARCFOUR(paramArrayOfByte, paramInt1, i, arrayOfByte, 0);
        this.out.write(arrayOfByte, 0, i);
        paramInt2 -= i;
        paramInt1 += i;
      }
    }
  }

  public void finish()
    throws IOException
  {
    if (!this.finished)
    {
      this.finished = true;
      if (this.aes)
      {
        byte[] arrayOfByte;
        try
        {
          arrayOfByte = this.cipher.doFinal();
        }
        catch (Exception localException)
        {
          throw new ExceptionConverter(localException);
        }
        this.out.write(arrayOfByte, 0, arrayOfByte.length);
      }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.OutputStreamEncryption
 * JD-Core Version:    0.6.0
 */