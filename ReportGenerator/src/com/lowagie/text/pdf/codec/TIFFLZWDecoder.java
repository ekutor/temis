package com.lowagie.text.pdf.codec;

public class TIFFLZWDecoder
{
  byte[][] stringTable;
  byte[] data = null;
  byte[] uncompData;
  int tableIndex;
  int bitsToGet = 9;
  int bytePointer;
  int bitPointer;
  int dstIndex;
  int w;
  int h;
  int predictor;
  int samplesPerPixel;
  int nextData = 0;
  int nextBits = 0;
  int[] andTable = { 511, 1023, 2047, 4095 };

  public TIFFLZWDecoder(int paramInt1, int paramInt2, int paramInt3)
  {
    this.w = paramInt1;
    this.predictor = paramInt2;
    this.samplesPerPixel = paramInt3;
  }

  public byte[] decode(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    if ((paramArrayOfByte1[0] == 0) && (paramArrayOfByte1[1] == 1))
      throw new UnsupportedOperationException("TIFF 5.0-style LZW codes are not supported.");
    initializeStringTable();
    this.data = paramArrayOfByte1;
    this.h = paramInt;
    this.uncompData = paramArrayOfByte2;
    this.bytePointer = 0;
    this.bitPointer = 0;
    this.dstIndex = 0;
    this.nextData = 0;
    this.nextBits = 0;
    int j = 0;
    int i;
    while (((i = getNextCode()) != 257) && (this.dstIndex < paramArrayOfByte2.length))
    {
      if (i == 256)
      {
        initializeStringTable();
        i = getNextCode();
        if (i == 257)
          break;
        writeString(this.stringTable[i]);
        j = i;
        continue;
      }
      if (i < this.tableIndex)
      {
        arrayOfByte = this.stringTable[i];
        writeString(arrayOfByte);
        addStringToTable(this.stringTable[j], arrayOfByte[0]);
        j = i;
        continue;
      }
      byte[] arrayOfByte = this.stringTable[j];
      arrayOfByte = composeString(arrayOfByte, arrayOfByte[0]);
      writeString(arrayOfByte);
      addStringToTable(arrayOfByte);
      j = i;
    }
    if (this.predictor == 2)
      for (int m = 0; m < paramInt; m++)
      {
        int k = this.samplesPerPixel * (m * this.w + 1);
        for (int n = this.samplesPerPixel; n < this.w * this.samplesPerPixel; n++)
        {
          int tmp281_279 = k;
          byte[] tmp281_278 = paramArrayOfByte2;
          tmp281_278[tmp281_279] = (byte)(tmp281_278[tmp281_279] + paramArrayOfByte2[(k - this.samplesPerPixel)]);
          k++;
        }
      }
    return paramArrayOfByte2;
  }

  public void initializeStringTable()
  {
    this.stringTable = new byte[4096][];
    for (int i = 0; i < 256; i++)
    {
      this.stringTable[i] = new byte[1];
      this.stringTable[i][0] = (byte)i;
    }
    this.tableIndex = 258;
    this.bitsToGet = 9;
  }

  public void writeString(byte[] paramArrayOfByte)
  {
    int i = this.uncompData.length - this.dstIndex;
    if (paramArrayOfByte.length < i)
      i = paramArrayOfByte.length;
    System.arraycopy(paramArrayOfByte, 0, this.uncompData, this.dstIndex, i);
    this.dstIndex += i;
  }

  public void addStringToTable(byte[] paramArrayOfByte, byte paramByte)
  {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte = new byte[i + 1];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, i);
    arrayOfByte[i] = paramByte;
    this.stringTable[(this.tableIndex++)] = arrayOfByte;
    if (this.tableIndex == 511)
      this.bitsToGet = 10;
    else if (this.tableIndex == 1023)
      this.bitsToGet = 11;
    else if (this.tableIndex == 2047)
      this.bitsToGet = 12;
  }

  public void addStringToTable(byte[] paramArrayOfByte)
  {
    this.stringTable[(this.tableIndex++)] = paramArrayOfByte;
    if (this.tableIndex == 511)
      this.bitsToGet = 10;
    else if (this.tableIndex == 1023)
      this.bitsToGet = 11;
    else if (this.tableIndex == 2047)
      this.bitsToGet = 12;
  }

  public byte[] composeString(byte[] paramArrayOfByte, byte paramByte)
  {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte = new byte[i + 1];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, i);
    arrayOfByte[i] = paramByte;
    return arrayOfByte;
  }

  public int getNextCode()
  {
    try
    {
      this.nextData = (this.nextData << 8 | this.data[(this.bytePointer++)] & 0xFF);
      this.nextBits += 8;
      if (this.nextBits < this.bitsToGet)
      {
        this.nextData = (this.nextData << 8 | this.data[(this.bytePointer++)] & 0xFF);
        this.nextBits += 8;
      }
      int i = this.nextData >> this.nextBits - this.bitsToGet & this.andTable[(this.bitsToGet - 9)];
      this.nextBits -= this.bitsToGet;
      return i;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
    }
    return 257;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.TIFFLZWDecoder
 * JD-Core Version:    0.6.0
 */