package com.lowagie.text.pdf.codec;

import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class TIFFDirectory
  implements Serializable
{
  private static final long serialVersionUID = -168636766193675380L;
  boolean isBigEndian;
  int numEntries;
  TIFFField[] fields;
  Hashtable fieldIndex = new Hashtable();
  long IFDOffset = 8L;
  long nextIFDOffset = 0L;
  private static final int[] sizeOfType = { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };

  TIFFDirectory()
  {
  }

  private static boolean isValidEndianTag(int paramInt)
  {
    return (paramInt == 18761) || (paramInt == 19789);
  }

  public TIFFDirectory(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt)
    throws IOException
  {
    long l1 = paramRandomAccessFileOrArray.getFilePointer();
    paramRandomAccessFileOrArray.seek(0L);
    int i = paramRandomAccessFileOrArray.readUnsignedShort();
    if (!isValidEndianTag(i))
      throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
    this.isBigEndian = (i == 19789);
    int j = readUnsignedShort(paramRandomAccessFileOrArray);
    if (j != 42)
      throw new IllegalArgumentException("Bad magic number, should be 42.");
    long l2 = readUnsignedInt(paramRandomAccessFileOrArray);
    for (int k = 0; k < paramInt; k++)
    {
      if (l2 == 0L)
        throw new IllegalArgumentException("Directory number too large.");
      paramRandomAccessFileOrArray.seek(l2);
      int m = readUnsignedShort(paramRandomAccessFileOrArray);
      paramRandomAccessFileOrArray.skip(12 * m);
      l2 = readUnsignedInt(paramRandomAccessFileOrArray);
    }
    paramRandomAccessFileOrArray.seek(l2);
    initialize(paramRandomAccessFileOrArray);
    paramRandomAccessFileOrArray.seek(l1);
  }

  public TIFFDirectory(RandomAccessFileOrArray paramRandomAccessFileOrArray, long paramLong, int paramInt)
    throws IOException
  {
    long l = paramRandomAccessFileOrArray.getFilePointer();
    paramRandomAccessFileOrArray.seek(0L);
    int i = paramRandomAccessFileOrArray.readUnsignedShort();
    if (!isValidEndianTag(i))
      throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
    this.isBigEndian = (i == 19789);
    paramRandomAccessFileOrArray.seek(paramLong);
    for (int j = 0; j < paramInt; j++)
    {
      int k = readUnsignedShort(paramRandomAccessFileOrArray);
      paramRandomAccessFileOrArray.seek(paramLong + 12 * k);
      paramLong = readUnsignedInt(paramRandomAccessFileOrArray);
      paramRandomAccessFileOrArray.seek(paramLong);
    }
    initialize(paramRandomAccessFileOrArray);
    paramRandomAccessFileOrArray.seek(l);
  }

  private void initialize(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    long l1 = 0L;
    long l2 = paramRandomAccessFileOrArray.length();
    this.IFDOffset = paramRandomAccessFileOrArray.getFilePointer();
    this.numEntries = readUnsignedShort(paramRandomAccessFileOrArray);
    this.fields = new TIFFField[this.numEntries];
    for (int i = 0; (i < this.numEntries) && (l1 < l2); i++)
    {
      int k = readUnsignedShort(paramRandomAccessFileOrArray);
      int m = readUnsignedShort(paramRandomAccessFileOrArray);
      int n = (int)readUnsignedInt(paramRandomAccessFileOrArray);
      int i1 = 1;
      l1 = paramRandomAccessFileOrArray.getFilePointer() + 4;
      try
      {
        if (n * sizeOfType[m] > 4)
        {
          long l3 = readUnsignedInt(paramRandomAccessFileOrArray);
          if (l3 < l2)
            paramRandomAccessFileOrArray.seek(l3);
          else
            i1 = 0;
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        i1 = 0;
      }
      if (i1 != 0)
      {
        this.fieldIndex.put(new Integer(k), new Integer(i));
        Object localObject1 = null;
        Object localObject2;
        Object localObject3;
        int j;
        switch (m)
        {
        case 1:
        case 2:
        case 6:
        case 7:
          byte[] arrayOfByte = new byte[n];
          paramRandomAccessFileOrArray.readFully(arrayOfByte, 0, n);
          if (m == 2)
          {
            int i2 = 0;
            int i3 = 0;
            localObject2 = new ArrayList();
            while (i2 < n)
            {
              while ((i2 < n) && (arrayOfByte[(i2++)] != 0));
              ((ArrayList)localObject2).add(new String(arrayOfByte, i3, i2 - i3));
              i3 = i2;
            }
            n = ((ArrayList)localObject2).size();
            localObject3 = new String[n];
            for (int i4 = 0; i4 < n; i4++)
              localObject3[i4] = ((String)((ArrayList)localObject2).get(i4));
            localObject1 = localObject3;
          }
          else
          {
            localObject1 = arrayOfByte;
          }
          break;
        case 3:
          char[] arrayOfChar = new char[n];
          for (j = 0; j < n; j++)
            arrayOfChar[j] = (char)readUnsignedShort(paramRandomAccessFileOrArray);
          localObject1 = arrayOfChar;
          break;
        case 4:
          long[] arrayOfLong = new long[n];
          for (j = 0; j < n; j++)
            arrayOfLong[j] = readUnsignedInt(paramRandomAccessFileOrArray);
          localObject1 = arrayOfLong;
          break;
        case 5:
          localObject2 = new long[n][2];
          for (j = 0; j < n; j++)
          {
            localObject2[j][0] = readUnsignedInt(paramRandomAccessFileOrArray);
            localObject2[j][1] = readUnsignedInt(paramRandomAccessFileOrArray);
          }
          localObject1 = localObject2;
          break;
        case 8:
          localObject3 = new short[n];
          for (j = 0; j < n; j++)
            localObject3[j] = readShort(paramRandomAccessFileOrArray);
          localObject1 = localObject3;
          break;
        case 9:
          int[] arrayOfInt = new int[n];
          for (j = 0; j < n; j++)
            arrayOfInt[j] = readInt(paramRandomAccessFileOrArray);
          localObject1 = arrayOfInt;
          break;
        case 10:
          int[][] arrayOfInt1 = new int[n][2];
          for (j = 0; j < n; j++)
          {
            arrayOfInt1[j][0] = readInt(paramRandomAccessFileOrArray);
            arrayOfInt1[j][1] = readInt(paramRandomAccessFileOrArray);
          }
          localObject1 = arrayOfInt1;
          break;
        case 11:
          float[] arrayOfFloat = new float[n];
          for (j = 0; j < n; j++)
            arrayOfFloat[j] = readFloat(paramRandomAccessFileOrArray);
          localObject1 = arrayOfFloat;
          break;
        case 12:
          double[] arrayOfDouble = new double[n];
          for (j = 0; j < n; j++)
            arrayOfDouble[j] = readDouble(paramRandomAccessFileOrArray);
          localObject1 = arrayOfDouble;
          break;
        }
        this.fields[i] = new TIFFField(k, m, n, localObject1);
      }
      paramRandomAccessFileOrArray.seek(l1);
    }
    try
    {
      this.nextIFDOffset = readUnsignedInt(paramRandomAccessFileOrArray);
    }
    catch (Exception localException)
    {
      this.nextIFDOffset = 0L;
    }
  }

  public int getNumEntries()
  {
    return this.numEntries;
  }

  public TIFFField getField(int paramInt)
  {
    Integer localInteger = (Integer)this.fieldIndex.get(new Integer(paramInt));
    if (localInteger == null)
      return null;
    return this.fields[localInteger.intValue()];
  }

  public boolean isTagPresent(int paramInt)
  {
    return this.fieldIndex.containsKey(new Integer(paramInt));
  }

  public int[] getTags()
  {
    int[] arrayOfInt = new int[this.fieldIndex.size()];
    Enumeration localEnumeration = this.fieldIndex.keys();
    int i = 0;
    while (localEnumeration.hasMoreElements())
      arrayOfInt[(i++)] = ((Integer)localEnumeration.nextElement()).intValue();
    return arrayOfInt;
  }

  public TIFFField[] getFields()
  {
    return this.fields;
  }

  public byte getFieldAsByte(int paramInt1, int paramInt2)
  {
    Integer localInteger = (Integer)this.fieldIndex.get(new Integer(paramInt1));
    byte[] arrayOfByte = this.fields[localInteger.intValue()].getAsBytes();
    return arrayOfByte[paramInt2];
  }

  public byte getFieldAsByte(int paramInt)
  {
    return getFieldAsByte(paramInt, 0);
  }

  public long getFieldAsLong(int paramInt1, int paramInt2)
  {
    Integer localInteger = (Integer)this.fieldIndex.get(new Integer(paramInt1));
    return this.fields[localInteger.intValue()].getAsLong(paramInt2);
  }

  public long getFieldAsLong(int paramInt)
  {
    return getFieldAsLong(paramInt, 0);
  }

  public float getFieldAsFloat(int paramInt1, int paramInt2)
  {
    Integer localInteger = (Integer)this.fieldIndex.get(new Integer(paramInt1));
    return this.fields[localInteger.intValue()].getAsFloat(paramInt2);
  }

  public float getFieldAsFloat(int paramInt)
  {
    return getFieldAsFloat(paramInt, 0);
  }

  public double getFieldAsDouble(int paramInt1, int paramInt2)
  {
    Integer localInteger = (Integer)this.fieldIndex.get(new Integer(paramInt1));
    return this.fields[localInteger.intValue()].getAsDouble(paramInt2);
  }

  public double getFieldAsDouble(int paramInt)
  {
    return getFieldAsDouble(paramInt, 0);
  }

  private short readShort(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readShort();
    return paramRandomAccessFileOrArray.readShortLE();
  }

  private int readUnsignedShort(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readUnsignedShort();
    return paramRandomAccessFileOrArray.readUnsignedShortLE();
  }

  private int readInt(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readInt();
    return paramRandomAccessFileOrArray.readIntLE();
  }

  private long readUnsignedInt(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readUnsignedInt();
    return paramRandomAccessFileOrArray.readUnsignedIntLE();
  }

  private long readLong(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readLong();
    return paramRandomAccessFileOrArray.readLongLE();
  }

  private float readFloat(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readFloat();
    return paramRandomAccessFileOrArray.readFloatLE();
  }

  private double readDouble(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    if (this.isBigEndian)
      return paramRandomAccessFileOrArray.readDouble();
    return paramRandomAccessFileOrArray.readDoubleLE();
  }

  private static int readUnsignedShort(RandomAccessFileOrArray paramRandomAccessFileOrArray, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
      return paramRandomAccessFileOrArray.readUnsignedShort();
    return paramRandomAccessFileOrArray.readUnsignedShortLE();
  }

  private static long readUnsignedInt(RandomAccessFileOrArray paramRandomAccessFileOrArray, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
      return paramRandomAccessFileOrArray.readUnsignedInt();
    return paramRandomAccessFileOrArray.readUnsignedIntLE();
  }

  public static int getNumDirectories(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    long l1 = paramRandomAccessFileOrArray.getFilePointer();
    paramRandomAccessFileOrArray.seek(0L);
    int i = paramRandomAccessFileOrArray.readUnsignedShort();
    if (!isValidEndianTag(i))
      throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
    boolean bool = i == 19789;
    int j = readUnsignedShort(paramRandomAccessFileOrArray, bool);
    if (j != 42)
      throw new IllegalArgumentException("Bad magic number, should be 42.");
    paramRandomAccessFileOrArray.seek(4L);
    long l2 = readUnsignedInt(paramRandomAccessFileOrArray, bool);
    int k = 0;
    while (true)
      if (l2 != 0L)
      {
        k++;
        try
        {
          paramRandomAccessFileOrArray.seek(l2);
          int m = readUnsignedShort(paramRandomAccessFileOrArray, bool);
          paramRandomAccessFileOrArray.skip(12 * m);
          l2 = readUnsignedInt(paramRandomAccessFileOrArray, bool);
        }
        catch (EOFException localEOFException)
        {
        }
      }
    paramRandomAccessFileOrArray.seek(l1);
    return k;
  }

  public boolean isBigEndian()
  {
    return this.isBigEndian;
  }

  public long getIFDOffset()
  {
    return this.IFDOffset;
  }

  public long getNextIFDOffset()
  {
    return this.nextIFDOffset;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.TIFFDirectory
 * JD-Core Version:    0.6.0
 */