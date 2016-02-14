package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class CharVector
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = -4875768298308363544L;
  private static final int DEFAULT_BLOCK_SIZE = 2048;
  private int blockSize;
  private char[] array;
  private int n;

  public CharVector()
  {
    this(2048);
  }

  public CharVector(int paramInt)
  {
    if (paramInt > 0)
      this.blockSize = paramInt;
    else
      this.blockSize = 2048;
    this.array = new char[this.blockSize];
    this.n = 0;
  }

  public CharVector(char[] paramArrayOfChar)
  {
    this.blockSize = 2048;
    this.array = paramArrayOfChar;
    this.n = paramArrayOfChar.length;
  }

  public CharVector(char[] paramArrayOfChar, int paramInt)
  {
    if (paramInt > 0)
      this.blockSize = paramInt;
    else
      this.blockSize = 2048;
    this.array = paramArrayOfChar;
    this.n = paramArrayOfChar.length;
  }

  public void clear()
  {
    this.n = 0;
  }

  public Object clone()
  {
    CharVector localCharVector = new CharVector((char[])this.array.clone(), this.blockSize);
    localCharVector.n = this.n;
    return localCharVector;
  }

  public char[] getArray()
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

  public void put(int paramInt, char paramChar)
  {
    this.array[paramInt] = paramChar;
  }

  public char get(int paramInt)
  {
    return this.array[paramInt];
  }

  public int alloc(int paramInt)
  {
    int i = this.n;
    int j = this.array.length;
    if (this.n + paramInt >= j)
    {
      char[] arrayOfChar = new char[j + this.blockSize];
      System.arraycopy(this.array, 0, arrayOfChar, 0, j);
      this.array = arrayOfChar;
    }
    this.n += paramInt;
    return i;
  }

  public void trimToSize()
  {
    if (this.n < this.array.length)
    {
      char[] arrayOfChar = new char[this.n];
      System.arraycopy(this.array, 0, arrayOfChar, 0, this.n);
      this.array = arrayOfChar;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.CharVector
 * JD-Core Version:    0.6.0
 */