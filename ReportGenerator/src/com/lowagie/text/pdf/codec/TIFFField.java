package com.lowagie.text.pdf.codec;

import java.io.Serializable;

public class TIFFField
  implements Comparable, Serializable
{
  private static final long serialVersionUID = 9088332901412823834L;
  public static final int TIFF_BYTE = 1;
  public static final int TIFF_ASCII = 2;
  public static final int TIFF_SHORT = 3;
  public static final int TIFF_LONG = 4;
  public static final int TIFF_RATIONAL = 5;
  public static final int TIFF_SBYTE = 6;
  public static final int TIFF_UNDEFINED = 7;
  public static final int TIFF_SSHORT = 8;
  public static final int TIFF_SLONG = 9;
  public static final int TIFF_SRATIONAL = 10;
  public static final int TIFF_FLOAT = 11;
  public static final int TIFF_DOUBLE = 12;
  int tag;
  int type;
  int count;
  Object data;

  TIFFField()
  {
  }

  public TIFFField(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    this.tag = paramInt1;
    this.type = paramInt2;
    this.count = paramInt3;
    this.data = paramObject;
  }

  public int getTag()
  {
    return this.tag;
  }

  public int getType()
  {
    return this.type;
  }

  public int getCount()
  {
    return this.count;
  }

  public byte[] getAsBytes()
  {
    return (byte[])this.data;
  }

  public char[] getAsChars()
  {
    return (char[])this.data;
  }

  public short[] getAsShorts()
  {
    return (short[])this.data;
  }

  public int[] getAsInts()
  {
    return (int[])this.data;
  }

  public long[] getAsLongs()
  {
    return (long[])this.data;
  }

  public float[] getAsFloats()
  {
    return (float[])this.data;
  }

  public double[] getAsDoubles()
  {
    return (double[])this.data;
  }

  public int[][] getAsSRationals()
  {
    return (int[][])this.data;
  }

  public long[][] getAsRationals()
  {
    return (long[][])this.data;
  }

  public int getAsInt(int paramInt)
  {
    switch (this.type)
    {
    case 1:
    case 7:
      return ((byte[])this.data)[paramInt] & 0xFF;
    case 6:
      return ((byte[])this.data)[paramInt];
    case 3:
      return ((char[])this.data)[paramInt] & 0xFFFF;
    case 8:
      return ((short[])this.data)[paramInt];
    case 9:
      return ((int[])this.data)[paramInt];
    case 2:
    case 4:
    case 5:
    }
    throw new ClassCastException();
  }

  public long getAsLong(int paramInt)
  {
    switch (this.type)
    {
    case 1:
    case 7:
      return ((byte[])this.data)[paramInt] & 0xFF;
    case 6:
      return ((byte[])this.data)[paramInt];
    case 3:
      return ((char[])this.data)[paramInt] & 0xFFFF;
    case 8:
      return ((short[])this.data)[paramInt];
    case 9:
      return ((int[])this.data)[paramInt];
    case 4:
      return ((long[])this.data)[paramInt];
    case 2:
    case 5:
    }
    throw new ClassCastException();
  }

  public float getAsFloat(int paramInt)
  {
    switch (this.type)
    {
    case 1:
      return ((byte[])this.data)[paramInt] & 0xFF;
    case 6:
      return ((byte[])this.data)[paramInt];
    case 3:
      return ((char[])this.data)[paramInt] & 0xFFFF;
    case 8:
      return ((short[])this.data)[paramInt];
    case 9:
      return ((int[])this.data)[paramInt];
    case 4:
      return (float)((long[])this.data)[paramInt];
    case 11:
      return ((float[])this.data)[paramInt];
    case 12:
      return (float)((double[])this.data)[paramInt];
    case 10:
      int[] arrayOfInt = getAsSRational(paramInt);
      return (float)(arrayOfInt[0] / arrayOfInt[1]);
    case 5:
      long[] arrayOfLong = getAsRational(paramInt);
      return (float)(arrayOfLong[0] / arrayOfLong[1]);
    case 2:
    case 7:
    }
    throw new ClassCastException();
  }

  public double getAsDouble(int paramInt)
  {
    switch (this.type)
    {
    case 1:
      return ((byte[])this.data)[paramInt] & 0xFF;
    case 6:
      return ((byte[])this.data)[paramInt];
    case 3:
      return ((char[])this.data)[paramInt] & 0xFFFF;
    case 8:
      return ((short[])this.data)[paramInt];
    case 9:
      return ((int[])this.data)[paramInt];
    case 4:
      return ((long[])this.data)[paramInt];
    case 11:
      return ((float[])this.data)[paramInt];
    case 12:
      return ((double[])this.data)[paramInt];
    case 10:
      int[] arrayOfInt = getAsSRational(paramInt);
      return arrayOfInt[0] / arrayOfInt[1];
    case 5:
      long[] arrayOfLong = getAsRational(paramInt);
      return arrayOfLong[0] / arrayOfLong[1];
    case 2:
    case 7:
    }
    throw new ClassCastException();
  }

  public String getAsString(int paramInt)
  {
    return ((String[])this.data)[paramInt];
  }

  public int[] getAsSRational(int paramInt)
  {
    return ((int[][])this.data)[paramInt];
  }

  public long[] getAsRational(int paramInt)
  {
    if (this.type == 4)
      return getAsLongs();
    return ((long[][])this.data)[paramInt];
  }

  public int compareTo(Object paramObject)
  {
    if (paramObject == null)
      throw new IllegalArgumentException();
    int i = ((TIFFField)paramObject).getTag();
    if (this.tag < i)
      return -1;
    if (this.tag > i)
      return 1;
    return 0;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.TIFFField
 * JD-Core Version:    0.6.0
 */