package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class ByteVector
  implements Serializable
{
  private static final long serialVersionUID = -1096301185375029343L;
  private static final int DEFAULT_BLOCK_SIZE = 2048;
  private int blockSize;
  private byte[] array;
  private int n;

  public ByteVector()
  {
    this(2048);
  }

  public ByteVector(int paramInt)
  {
    if (paramInt > 0)
      this.blockSize = paramInt;
    else
      this.blockSize = 2048;
    this.array = new byte[this.blockSize];
    this.n = 0;
  }

  public ByteVector(byte[] paramArrayOfByte)
  {
    this.blockSize = 2048;
    this.array = paramArrayOfByte;
    this.n = 0;
  }

  public ByteVector(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt > 0)
      this.blockSize = paramInt;
    else
      this.blockSize = 2048;
    this.array = paramArrayOfByte;
    this.n = 0;
  }

  public byte[] getArray()
  {
    return this.array;
  }

  public int length()
  {
    return this.n;
  }

  public int capacity()
  {
    return this.array.length;
  }

  public void put(int paramInt, byte paramByte)
  {
    this.array[paramInt] = paramByte;
  }

  public byte get(int paramInt)
  {
    return this.array[paramInt];
  }

  public int alloc(int paramInt)
  {
    int i = this.n;
    int j = this.array.length;
    if (this.n + paramInt >= j)
    {
      byte[] arrayOfByte = new byte[j + this.blockSize];
      System.arraycopy(this.array, 0, arrayOfByte, 0, j);
      this.array = arrayOfByte;
    }
    this.n += paramInt;
    return i;
  }

  public void trimToSize()
  {
    if (this.n < this.array.length)
    {
      byte[] arrayOfByte = new byte[this.n];
      System.arraycopy(this.array, 0, arrayOfByte, 0, this.n);
      this.array = arrayOfByte;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.ByteVector
 * JD-Core Version:    0.6.0
 */