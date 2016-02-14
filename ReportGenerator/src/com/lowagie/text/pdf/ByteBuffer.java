package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ByteBuffer extends OutputStream
{
  protected int count;
  protected byte[] buf;
  private static int byteCacheSize = 0;
  private static byte[][] byteCache = new byte[byteCacheSize][];
  public static final byte ZERO = 48;
  private static final char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  private static final byte[] bytes = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  public static boolean HIGH_PRECISION = false;
  private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

  public ByteBuffer()
  {
    this(128);
  }

  public ByteBuffer(int paramInt)
  {
    if (paramInt < 1)
      paramInt = 128;
    this.buf = new byte[paramInt];
  }

  public static void setCacheSize(int paramInt)
  {
    if (paramInt > 3276700)
      paramInt = 3276700;
    if (paramInt <= byteCacheSize)
      return;
    byte[][] arrayOfByte = new byte[paramInt][];
    System.arraycopy(byteCache, 0, arrayOfByte, 0, byteCacheSize);
    byteCache = arrayOfByte;
    byteCacheSize = paramInt;
  }

  public static void fillCache(int paramInt)
  {
    int i = 1;
    switch (paramInt)
    {
    case 0:
      i = 100;
      break;
    case 1:
      i = 10;
    }
    int j = 1;
    while (j < byteCacheSize)
    {
      if (byteCache[j] == null)
        byteCache[j] = convertToBytes(j);
      j += i;
    }
  }

  private static byte[] convertToBytes(int paramInt)
  {
    int i = (int)Math.floor(Math.log(paramInt) / Math.log(10.0D));
    if (paramInt % 100 != 0)
      i += 2;
    if (paramInt % 10 != 0)
      i++;
    if (paramInt < 100)
    {
      i++;
      if (paramInt < 10)
        i++;
    }
    i--;
    byte[] arrayOfByte = new byte[i];
    i--;
    if (paramInt < 100)
      arrayOfByte[0] = 48;
    if (paramInt % 10 != 0)
      arrayOfByte[(i--)] = bytes[(paramInt % 10)];
    if (paramInt % 100 != 0)
    {
      arrayOfByte[(i--)] = bytes[(paramInt / 10 % 10)];
      arrayOfByte[(i--)] = 46;
    }
    i = (int)Math.floor(Math.log(paramInt) / Math.log(10.0D)) - 1;
    for (int j = 0; j < i; j++)
      arrayOfByte[j] = bytes[(paramInt / (int)Math.pow(10.0D, i - j + 1) % 10)];
    return arrayOfByte;
  }

  public ByteBuffer append_i(int paramInt)
  {
    int i = this.count + 1;
    if (i > this.buf.length)
    {
      byte[] arrayOfByte = new byte[Math.max(this.buf.length << 1, i)];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
      this.buf = arrayOfByte;
    }
    this.buf[this.count] = (byte)paramInt;
    this.count = i;
    return this;
  }

  public ByteBuffer append(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0) || (paramInt2 == 0))
      return this;
    int i = this.count + paramInt2;
    if (i > this.buf.length)
    {
      byte[] arrayOfByte = new byte[Math.max(this.buf.length << 1, i)];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
      this.buf = arrayOfByte;
    }
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.count, paramInt2);
    this.count = i;
    return this;
  }

  public ByteBuffer append(byte[] paramArrayOfByte)
  {
    return append(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public ByteBuffer append(String paramString)
  {
    if (paramString != null)
      return append(DocWriter.getISOBytes(paramString));
    return this;
  }

  public ByteBuffer append(char paramChar)
  {
    return append_i(paramChar);
  }

  public ByteBuffer append(ByteBuffer paramByteBuffer)
  {
    return append(paramByteBuffer.buf, 0, paramByteBuffer.count);
  }

  public ByteBuffer append(int paramInt)
  {
    return append(paramInt);
  }

  public ByteBuffer append(byte paramByte)
  {
    return append_i(paramByte);
  }

  public ByteBuffer appendHex(byte paramByte)
  {
    append(bytes[(paramByte >> 4 & 0xF)]);
    return append(bytes[(paramByte & 0xF)]);
  }

  public ByteBuffer append(float paramFloat)
  {
    return append(paramFloat);
  }

  public ByteBuffer append(double paramDouble)
  {
    append(formatDouble(paramDouble, this));
    return this;
  }

  public static String formatDouble(double paramDouble)
  {
    return formatDouble(paramDouble, null);
  }

  public static String formatDouble(double paramDouble, ByteBuffer paramByteBuffer)
  {
    if (HIGH_PRECISION)
    {
      DecimalFormat localDecimalFormat = new DecimalFormat("0.######", dfs);
      String str = localDecimalFormat.format(paramDouble);
      if (paramByteBuffer == null)
        return str;
      paramByteBuffer.append(str);
      return null;
    }
    int i = 0;
    if (Math.abs(paramDouble) < 1.5E-005D)
    {
      if (paramByteBuffer != null)
      {
        paramByteBuffer.append(48);
        return null;
      }
      return "0";
    }
    if (paramDouble < 0.0D)
    {
      i = 1;
      paramDouble = -paramDouble;
    }
    int j;
    int n;
    if (paramDouble < 1.0D)
    {
      paramDouble += 5.E-006D;
      if (paramDouble >= 1.0D)
      {
        if (i != 0)
        {
          if (paramByteBuffer != null)
          {
            paramByteBuffer.append(45);
            paramByteBuffer.append(49);
            return null;
          }
          return "-1";
        }
        if (paramByteBuffer != null)
        {
          paramByteBuffer.append(49);
          return null;
        }
        return "1";
      }
      if (paramByteBuffer != null)
      {
        j = (int)(paramDouble * 100000.0D);
        if (i != 0)
          paramByteBuffer.append(45);
        paramByteBuffer.append(48);
        paramByteBuffer.append(46);
        paramByteBuffer.append((byte)(j / 10000 + 48));
        if (j % 10000 != 0)
        {
          paramByteBuffer.append((byte)(j / 1000 % 10 + 48));
          if (j % 1000 != 0)
          {
            paramByteBuffer.append((byte)(j / 100 % 10 + 48));
            if (j % 100 != 0)
            {
              paramByteBuffer.append((byte)(j / 10 % 10 + 48));
              if (j % 10 != 0)
                paramByteBuffer.append((byte)(j % 10 + 48));
            }
          }
        }
        return null;
      }
      j = 100000;
      int k = (int)(paramDouble * j);
      StringBuffer localStringBuffer2 = new StringBuffer();
      if (i != 0)
        localStringBuffer2.append('-');
      localStringBuffer2.append("0.");
      while (k < j / 10)
      {
        localStringBuffer2.append('0');
        j /= 10;
      }
      localStringBuffer2.append(k);
      for (n = localStringBuffer2.length() - 1; localStringBuffer2.charAt(n) == '0'; n--);
      localStringBuffer2.setLength(n + 1);
      return localStringBuffer2.toString();
    }
    if (paramDouble <= 32767.0D)
    {
      paramDouble += 0.005D;
      j = (int)(paramDouble * 100.0D);
      if ((j < byteCacheSize) && (byteCache[j] != null))
      {
        if (paramByteBuffer != null)
        {
          if (i != 0)
            paramByteBuffer.append(45);
          paramByteBuffer.append(byteCache[j]);
          return null;
        }
        localObject = PdfEncodings.convertToString(byteCache[j], null);
        if (i != 0)
          localObject = "-" + (String)localObject;
        return localObject;
      }
      if (paramByteBuffer != null)
      {
        if (j < byteCacheSize)
        {
          int m = 0;
          if (j >= 1000000)
            m += 5;
          else if (j >= 100000)
            m += 4;
          else if (j >= 10000)
            m += 3;
          else if (j >= 1000)
            m += 2;
          else if (j >= 100)
            m++;
          if (j % 100 != 0)
            m += 2;
          if (j % 10 != 0)
            m++;
          localObject = new byte[m];
          n = 0;
          if (j >= 1000000)
            localObject[(n++)] = bytes[(j / 1000000)];
          if (j >= 100000)
            localObject[(n++)] = bytes[(j / 100000 % 10)];
          if (j >= 10000)
            localObject[(n++)] = bytes[(j / 10000 % 10)];
          if (j >= 1000)
            localObject[(n++)] = bytes[(j / 1000 % 10)];
          if (j >= 100)
            localObject[(n++)] = bytes[(j / 100 % 10)];
          if (j % 100 != 0)
          {
            localObject[(n++)] = 46;
            localObject[(n++)] = bytes[(j / 10 % 10)];
            if (j % 10 != 0)
              localObject[(n++)] = bytes[(j % 10)];
          }
          byteCache[j] = localObject;
        }
        if (i != 0)
          paramByteBuffer.append(45);
        if (j >= 1000000)
          paramByteBuffer.append(bytes[(j / 1000000)]);
        if (j >= 100000)
          paramByteBuffer.append(bytes[(j / 100000 % 10)]);
        if (j >= 10000)
          paramByteBuffer.append(bytes[(j / 10000 % 10)]);
        if (j >= 1000)
          paramByteBuffer.append(bytes[(j / 1000 % 10)]);
        if (j >= 100)
          paramByteBuffer.append(bytes[(j / 100 % 10)]);
        if (j % 100 != 0)
        {
          paramByteBuffer.append(46);
          paramByteBuffer.append(bytes[(j / 10 % 10)]);
          if (j % 10 != 0)
            paramByteBuffer.append(bytes[(j % 10)]);
        }
        return null;
      }
      Object localObject = new StringBuffer();
      if (i != 0)
        ((StringBuffer)localObject).append('-');
      if (j >= 1000000)
        ((StringBuffer)localObject).append(chars[(j / 1000000)]);
      if (j >= 100000)
        ((StringBuffer)localObject).append(chars[(j / 100000 % 10)]);
      if (j >= 10000)
        ((StringBuffer)localObject).append(chars[(j / 10000 % 10)]);
      if (j >= 1000)
        ((StringBuffer)localObject).append(chars[(j / 1000 % 10)]);
      if (j >= 100)
        ((StringBuffer)localObject).append(chars[(j / 100 % 10)]);
      if (j % 100 != 0)
      {
        ((StringBuffer)localObject).append('.');
        ((StringBuffer)localObject).append(chars[(j / 10 % 10)]);
        if (j % 10 != 0)
          ((StringBuffer)localObject).append(chars[(j % 10)]);
      }
      return ((StringBuffer)localObject).toString();
    }
    StringBuffer localStringBuffer1 = new StringBuffer();
    if (i != 0)
      localStringBuffer1.append('-');
    paramDouble += 0.5D;
    long l = ()paramDouble;
    return (String)l;
  }

  public void reset()
  {
    this.count = 0;
  }

  public byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[this.count];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
    return arrayOfByte;
  }

  public int size()
  {
    return this.count;
  }

  public void setSize(int paramInt)
  {
    if ((paramInt > this.count) || (paramInt < 0))
      throw new IndexOutOfBoundsException("The new size must be positive and <= of the current size");
    this.count = paramInt;
  }

  public String toString()
  {
    return new String(this.buf, 0, this.count);
  }

  public String toString(String paramString)
    throws UnsupportedEncodingException
  {
    return new String(this.buf, 0, this.count, paramString);
  }

  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(this.buf, 0, this.count);
  }

  public void write(int paramInt)
    throws IOException
  {
    append((byte)paramInt);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    append(paramArrayOfByte, paramInt1, paramInt2);
  }

  public byte[] getBuffer()
  {
    return this.buf;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ByteBuffer
 * JD-Core Version:    0.6.0
 */