package com.lowagie.text.pdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class MappedRandomAccessFile
{
  private MappedByteBuffer mappedByteBuffer = null;
  private FileChannel channel = null;

  public MappedRandomAccessFile(String paramString1, String paramString2)
    throws FileNotFoundException, IOException
  {
    if (paramString2.equals("rw"))
      init(new RandomAccessFile(paramString1, paramString2).getChannel(), FileChannel.MapMode.READ_WRITE);
    else
      init(new FileInputStream(paramString1).getChannel(), FileChannel.MapMode.READ_ONLY);
  }

  private void init(FileChannel paramFileChannel, FileChannel.MapMode paramMapMode)
    throws IOException
  {
    this.channel = paramFileChannel;
    this.mappedByteBuffer = paramFileChannel.map(paramMapMode, 0L, paramFileChannel.size());
    this.mappedByteBuffer.load();
  }

  public FileChannel getChannel()
  {
    return this.channel;
  }

  public int read()
  {
    try
    {
      int i = this.mappedByteBuffer.get();
      int j = i & 0xFF;
      return j;
    }
    catch (BufferUnderflowException localBufferUnderflowException)
    {
    }
    return -1;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = this.mappedByteBuffer.position();
    int j = this.mappedByteBuffer.limit();
    if (i == j)
      return -1;
    int k = i + paramInt2 - paramInt1;
    if (k > j)
      paramInt2 = j - i;
    this.mappedByteBuffer.get(paramArrayOfByte, paramInt1, paramInt2);
    return paramInt2;
  }

  public long getFilePointer()
  {
    return this.mappedByteBuffer.position();
  }

  public void seek(long paramLong)
  {
    this.mappedByteBuffer.position((int)paramLong);
  }

  public long length()
  {
    return this.mappedByteBuffer.limit();
  }

  public void close()
    throws IOException
  {
    clean(this.mappedByteBuffer);
    this.mappedByteBuffer = null;
    if (this.channel != null)
      this.channel.close();
    this.channel = null;
  }

  protected void finalize()
    throws Throwable
  {
    close();
    super.finalize();
  }

  public static boolean clean(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer == null) || (!paramByteBuffer.isDirect()))
      return false;
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction(paramByteBuffer)
    {
      private final ByteBuffer val$buffer;

      public Object run()
      {
        Boolean localBoolean = Boolean.FALSE;
        try
        {
          Method localMethod1 = this.val$buffer.getClass().getMethod("cleaner", (Class[])null);
          localMethod1.setAccessible(true);
          Object localObject = localMethod1.invoke(this.val$buffer, (Object[])null);
          Method localMethod2 = localObject.getClass().getMethod("clean", (Class[])null);
          localMethod2.invoke(localObject, (Object[])null);
          localBoolean = Boolean.TRUE;
        }
        catch (Exception localException)
        {
        }
        return localBoolean;
      }
    });
    return localBoolean.booleanValue();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.MappedRandomAccessFile
 * JD-Core Version:    0.6.0
 */