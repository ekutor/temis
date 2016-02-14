package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class RandomAccessFileOrArray
  implements DataInput
{
  MappedRandomAccessFile rf;
  RandomAccessFile trf;
  boolean plainRandomAccess;
  String filename;
  byte[] arrayIn;
  int arrayInPtr;
  byte back;
  boolean isBack = false;
  private int startOffset = 0;

  public RandomAccessFileOrArray(String paramString)
    throws IOException
  {
    this(paramString, false, Document.plainRandomAccess);
  }

  public RandomAccessFileOrArray(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    this.plainRandomAccess = paramBoolean2;
    File localFile = new File(paramString);
    Object localObject1;
    if (!localFile.canRead())
    {
      if ((paramString.startsWith("file:/")) || (paramString.startsWith("http://")) || (paramString.startsWith("https://")) || (paramString.startsWith("jar:")))
      {
        localObject1 = new URL(paramString).openStream();
        try
        {
          this.arrayIn = InputStreamToArray((InputStream)localObject1);
          return;
        }
        finally
        {
          try
          {
            ((InputStream)localObject1).close();
          }
          catch (IOException localIOException3)
          {
          }
        }
      }
      localObject1 = BaseFont.getResourceStream(paramString);
      if (localObject1 == null)
        throw new IOException(paramString + " not found as file or resource.");
      try
      {
        this.arrayIn = InputStreamToArray((InputStream)localObject1);
        return;
      }
      finally
      {
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (IOException localIOException4)
        {
        }
      }
    }
    if (paramBoolean1)
    {
      localObject1 = null;
      try
      {
        localObject1 = new FileInputStream(localFile);
        this.arrayIn = InputStreamToArray((InputStream)localObject1);
      }
      finally
      {
        try
        {
          if (localObject1 != null)
            ((InputStream)localObject1).close();
        }
        catch (Exception localException)
        {
        }
      }
      return;
    }
    this.filename = paramString;
    if (paramBoolean2)
      this.trf = new RandomAccessFile(paramString, "r");
    else
      this.rf = new MappedRandomAccessFile(paramString, "r");
  }

  public RandomAccessFileOrArray(URL paramURL)
    throws IOException
  {
    InputStream localInputStream = paramURL.openStream();
    try
    {
      this.arrayIn = InputStreamToArray(localInputStream);
    }
    finally
    {
      try
      {
        localInputStream.close();
      }
      catch (IOException localIOException2)
      {
      }
    }
  }

  public RandomAccessFileOrArray(InputStream paramInputStream)
    throws IOException
  {
    this.arrayIn = InputStreamToArray(paramInputStream);
  }

  public static byte[] InputStreamToArray(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[8192];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    while (true)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i < 1)
        break;
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    localByteArrayOutputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }

  public RandomAccessFileOrArray(byte[] paramArrayOfByte)
  {
    this.arrayIn = paramArrayOfByte;
  }

  public RandomAccessFileOrArray(RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    this.filename = paramRandomAccessFileOrArray.filename;
    this.arrayIn = paramRandomAccessFileOrArray.arrayIn;
    this.startOffset = paramRandomAccessFileOrArray.startOffset;
    this.plainRandomAccess = paramRandomAccessFileOrArray.plainRandomAccess;
  }

  public void pushBack(byte paramByte)
  {
    this.back = paramByte;
    this.isBack = true;
  }

  public int read()
    throws IOException
  {
    if (this.isBack)
    {
      this.isBack = false;
      return this.back & 0xFF;
    }
    if (this.arrayIn == null)
      return this.plainRandomAccess ? this.trf.read() : this.rf.read();
    if (this.arrayInPtr >= this.arrayIn.length)
      return -1;
    return this.arrayIn[(this.arrayInPtr++)] & 0xFF;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0)
      return 0;
    int i = 0;
    if (this.isBack)
    {
      this.isBack = false;
      if (paramInt2 == 1)
      {
        paramArrayOfByte[paramInt1] = this.back;
        return 1;
      }
      i = 1;
      paramArrayOfByte[(paramInt1++)] = this.back;
      paramInt2--;
    }
    if (this.arrayIn == null)
      return (this.plainRandomAccess ? this.trf.read(paramArrayOfByte, paramInt1, paramInt2) : this.rf.read(paramArrayOfByte, paramInt1, paramInt2)) + i;
    if (this.arrayInPtr >= this.arrayIn.length)
      return -1;
    if (this.arrayInPtr + paramInt2 > this.arrayIn.length)
      paramInt2 = this.arrayIn.length - this.arrayInPtr;
    System.arraycopy(this.arrayIn, this.arrayInPtr, paramArrayOfByte, paramInt1, paramInt2);
    this.arrayInPtr += paramInt2;
    return paramInt2 + i;
  }

  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void readFully(byte[] paramArrayOfByte)
    throws IOException
  {
    readFully(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    do
    {
      int j = read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j < 0)
        throw new EOFException();
      i += j;
    }
    while (i < paramInt2);
  }

  public long skip(long paramLong)
    throws IOException
  {
    return skipBytes((int)paramLong);
  }

  public int skipBytes(int paramInt)
    throws IOException
  {
    if (paramInt <= 0)
      return 0;
    int i = 0;
    if (this.isBack)
    {
      this.isBack = false;
      if (paramInt == 1)
        return 1;
      paramInt--;
      i = 1;
    }
    int j = getFilePointer();
    int k = length();
    int m = j + paramInt;
    if (m > k)
      m = k;
    seek(m);
    return m - j + i;
  }

  public void reOpen()
    throws IOException
  {
    if ((this.filename != null) && (this.rf == null) && (this.trf == null))
      if (this.plainRandomAccess)
        this.trf = new RandomAccessFile(this.filename, "r");
      else
        this.rf = new MappedRandomAccessFile(this.filename, "r");
    seek(0);
  }

  protected void insureOpen()
    throws IOException
  {
    if ((this.filename != null) && (this.rf == null) && (this.trf == null))
      reOpen();
  }

  public boolean isOpen()
  {
    return (this.filename == null) || (this.rf != null) || (this.trf != null);
  }

  public void close()
    throws IOException
  {
    this.isBack = false;
    if (this.rf != null)
    {
      this.rf.close();
      this.rf = null;
      this.plainRandomAccess = true;
    }
    else if (this.trf != null)
    {
      this.trf.close();
      this.trf = null;
    }
  }

  public int length()
    throws IOException
  {
    if (this.arrayIn == null)
    {
      insureOpen();
      return (int)(this.plainRandomAccess ? this.trf.length() : this.rf.length()) - this.startOffset;
    }
    return this.arrayIn.length - this.startOffset;
  }

  public void seek(int paramInt)
    throws IOException
  {
    paramInt += this.startOffset;
    this.isBack = false;
    if (this.arrayIn == null)
    {
      insureOpen();
      if (this.plainRandomAccess)
        this.trf.seek(paramInt);
      else
        this.rf.seek(paramInt);
    }
    else
    {
      this.arrayInPtr = paramInt;
    }
  }

  public void seek(long paramLong)
    throws IOException
  {
    seek((int)paramLong);
  }

  public int getFilePointer()
    throws IOException
  {
    insureOpen();
    int i = this.isBack ? 1 : 0;
    if (this.arrayIn == null)
      return (int)(this.plainRandomAccess ? this.trf.getFilePointer() : this.rf.getFilePointer()) - i - this.startOffset;
    return this.arrayInPtr - i - this.startOffset;
  }

  public boolean readBoolean()
    throws IOException
  {
    int i = read();
    if (i < 0)
      throw new EOFException();
    return i != 0;
  }

  public byte readByte()
    throws IOException
  {
    int i = read();
    if (i < 0)
      throw new EOFException();
    return (byte)i;
  }

  public int readUnsignedByte()
    throws IOException
  {
    int i = read();
    if (i < 0)
      throw new EOFException();
    return i;
  }

  public short readShort()
    throws IOException
  {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException();
    return (short)((i << 8) + j);
  }

  public final short readShortLE()
    throws IOException
  {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException();
    return (short)((j << 8) + (i << 0));
  }

  public int readUnsignedShort()
    throws IOException
  {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException();
    return (i << 8) + j;
  }

  public final int readUnsignedShortLE()
    throws IOException
  {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException();
    return (j << 8) + (i << 0);
  }

  public char readChar()
    throws IOException
  {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException();
    return (char)((i << 8) + j);
  }

  public final char readCharLE()
    throws IOException
  {
    int i = read();
    int j = read();
    if ((i | j) < 0)
      throw new EOFException();
    return (char)((j << 8) + (i << 0));
  }

  public int readInt()
    throws IOException
  {
    int i = read();
    int j = read();
    int k = read();
    int m = read();
    if ((i | j | k | m) < 0)
      throw new EOFException();
    return (i << 24) + (j << 16) + (k << 8) + m;
  }

  public final int readIntLE()
    throws IOException
  {
    int i = read();
    int j = read();
    int k = read();
    int m = read();
    if ((i | j | k | m) < 0)
      throw new EOFException();
    return (m << 24) + (k << 16) + (j << 8) + (i << 0);
  }

  public final long readUnsignedInt()
    throws IOException
  {
    long l1 = read();
    long l2 = read();
    long l3 = read();
    long l4 = read();
    if ((l1 | l2 | l3 | l4) < 0L)
      throw new EOFException();
    return (l1 << 24) + (l2 << 16) + (l3 << 8) + (l4 << 0);
  }

  public final long readUnsignedIntLE()
    throws IOException
  {
    long l1 = read();
    long l2 = read();
    long l3 = read();
    long l4 = read();
    if ((l1 | l2 | l3 | l4) < 0L)
      throw new EOFException();
    return (l4 << 24) + (l3 << 16) + (l2 << 8) + (l1 << 0);
  }

  public long readLong()
    throws IOException
  {
    return (readInt() << 32) + (readInt() & 0xFFFFFFFF);
  }

  public final long readLongLE()
    throws IOException
  {
    int i = readIntLE();
    int j = readIntLE();
    return (j << 32) + (i & 0xFFFFFFFF);
  }

  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }

  public final float readFloatLE()
    throws IOException
  {
    return Float.intBitsToFloat(readIntLE());
  }

  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }

  public final double readDoubleLE()
    throws IOException
  {
    return Double.longBitsToDouble(readLongLE());
  }

  public String readLine()
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = -1;
    int j = 0;
    while (j == 0)
      switch (i = read())
      {
      case -1:
      case 10:
        j = 1;
        break;
      case 13:
        j = 1;
        int k = getFilePointer();
        if (read() == 10)
          continue;
        seek(k);
        break;
      default:
        localStringBuffer.append((char)i);
      }
    if ((i == -1) && (localStringBuffer.length() == 0))
      return null;
    return localStringBuffer.toString();
  }

  public String readUTF()
    throws IOException
  {
    return DataInputStream.readUTF(this);
  }

  public int getStartOffset()
  {
    return this.startOffset;
  }

  public void setStartOffset(int paramInt)
  {
    this.startOffset = paramInt;
  }

  public ByteBuffer getNioByteBuffer()
    throws IOException
  {
    if (this.filename != null)
    {
      FileChannel localFileChannel;
      if (this.plainRandomAccess)
        localFileChannel = this.trf.getChannel();
      else
        localFileChannel = this.rf.getChannel();
      return localFileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, localFileChannel.size());
    }
    return ByteBuffer.wrap(this.arrayIn);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.RandomAccessFileOrArray
 * JD-Core Version:    0.6.0
 */