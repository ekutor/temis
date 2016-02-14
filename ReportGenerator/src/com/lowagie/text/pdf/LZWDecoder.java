package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.io.OutputStream;

public class LZWDecoder
{
  byte[][] stringTable;
  byte[] data = null;
  OutputStream uncompData;
  int tableIndex;
  int bitsToGet = 9;
  int bytePointer;
  int bitPointer;
  int nextData = 0;
  int nextBits = 0;
  int[] andTable = { 511, 1023, 2047, 4095 };

  public void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream)
  {
    if ((paramArrayOfByte[0] == 0) && (paramArrayOfByte[1] == 1))
      throw new RuntimeException("LZW flavour not supported.");
    initializeStringTable();
    this.data = paramArrayOfByte;
    this.uncompData = paramOutputStream;
    this.bytePointer = 0;
    this.bitPointer = 0;
    this.nextData = 0;
    this.nextBits = 0;
    int j = 0;
    int i;
    while ((i = getNextCode()) != 257)
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
  }

  public void initializeStringTable()
  {
    this.stringTable = new byte[8192][];
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
    try
    {
      this.uncompData.write(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
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
 * Qualified Name:     com.lowagie.text.pdf.LZWDecoder
 * JD-Core Version:    0.6.0
 */