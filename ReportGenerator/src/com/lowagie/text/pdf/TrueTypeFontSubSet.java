package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class TrueTypeFontSubSet
{
  static final String[] tableNamesSimple = { "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep" };
  static final String[] tableNamesCmap = { "cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep" };
  static final String[] tableNamesExtra = { "OS/2", "cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "name, prep" };
  static final int[] entrySelectors = { 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4 };
  static final int TABLE_CHECKSUM = 0;
  static final int TABLE_OFFSET = 1;
  static final int TABLE_LENGTH = 2;
  static final int HEAD_LOCA_FORMAT_OFFSET = 51;
  static final int ARG_1_AND_2_ARE_WORDS = 1;
  static final int WE_HAVE_A_SCALE = 8;
  static final int MORE_COMPONENTS = 32;
  static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
  static final int WE_HAVE_A_TWO_BY_TWO = 128;
  protected HashMap tableDirectory;
  protected RandomAccessFileOrArray rf;
  protected String fileName;
  protected boolean includeCmap;
  protected boolean includeExtras;
  protected boolean locaShortTable;
  protected int[] locaTable;
  protected HashMap glyphsUsed;
  protected ArrayList glyphsInList;
  protected int tableGlyphOffset;
  protected int[] newLocaTable;
  protected byte[] newLocaTableOut;
  protected byte[] newGlyfTable;
  protected int glyfTableRealSize;
  protected int locaTableRealSize;
  protected byte[] outFont;
  protected int fontPtr;
  protected int directoryOffset;

  TrueTypeFontSubSet(String paramString, RandomAccessFileOrArray paramRandomAccessFileOrArray, HashMap paramHashMap, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.fileName = paramString;
    this.rf = paramRandomAccessFileOrArray;
    this.glyphsUsed = paramHashMap;
    this.includeCmap = paramBoolean1;
    this.includeExtras = paramBoolean2;
    this.directoryOffset = paramInt;
    this.glyphsInList = new ArrayList(paramHashMap.keySet());
  }

  byte[] process()
    throws IOException, DocumentException
  {
    try
    {
      this.rf.reOpen();
      createTableDirectory();
      readLoca();
      flatGlyphs();
      createNewGlyphTables();
      locaTobytes();
      assembleFont();
      byte[] arrayOfByte = this.outFont;
      return arrayOfByte;
    }
    finally
    {
      try
      {
        this.rf.close();
      }
      catch (Exception localException2)
      {
      }
    }
    throw localObject;
  }

  protected void assembleFont()
    throws IOException
  {
    int i = 0;
    String[] arrayOfString;
    if (this.includeExtras)
      arrayOfString = tableNamesExtra;
    else if (this.includeCmap)
      arrayOfString = tableNamesCmap;
    else
      arrayOfString = tableNamesSimple;
    int j = 2;
    int k = 0;
    int[] arrayOfInt;
    for (int m = 0; m < arrayOfString.length; m++)
    {
      String str1 = arrayOfString[m];
      if ((str1.equals("glyf")) || (str1.equals("loca")))
        continue;
      arrayOfInt = (int[])this.tableDirectory.get(str1);
      if (arrayOfInt == null)
        continue;
      j++;
      i += (arrayOfInt[2] + 3 & 0xFFFFFFFC);
    }
    i += this.newLocaTableOut.length;
    i += this.newGlyfTable.length;
    m = 16 * j + 12;
    i += m;
    this.outFont = new byte[i];
    this.fontPtr = 0;
    writeFontInt(65536);
    writeFontShort(j);
    int n = entrySelectors[j];
    writeFontShort((1 << n) * 16);
    writeFontShort(n);
    writeFontShort((j - (1 << n)) * 16);
    String str2;
    for (int i1 = 0; i1 < arrayOfString.length; i1++)
    {
      str2 = arrayOfString[i1];
      arrayOfInt = (int[])this.tableDirectory.get(str2);
      if (arrayOfInt == null)
        continue;
      writeFontString(str2);
      if (str2.equals("glyf"))
      {
        writeFontInt(calculateChecksum(this.newGlyfTable));
        k = this.glyfTableRealSize;
      }
      else if (str2.equals("loca"))
      {
        writeFontInt(calculateChecksum(this.newLocaTableOut));
        k = this.locaTableRealSize;
      }
      else
      {
        writeFontInt(arrayOfInt[0]);
        k = arrayOfInt[2];
      }
      writeFontInt(m);
      writeFontInt(k);
      m += (k + 3 & 0xFFFFFFFC);
    }
    for (i1 = 0; i1 < arrayOfString.length; i1++)
    {
      str2 = arrayOfString[i1];
      arrayOfInt = (int[])this.tableDirectory.get(str2);
      if (arrayOfInt == null)
        continue;
      if (str2.equals("glyf"))
      {
        System.arraycopy(this.newGlyfTable, 0, this.outFont, this.fontPtr, this.newGlyfTable.length);
        this.fontPtr += this.newGlyfTable.length;
        this.newGlyfTable = null;
      }
      else if (str2.equals("loca"))
      {
        System.arraycopy(this.newLocaTableOut, 0, this.outFont, this.fontPtr, this.newLocaTableOut.length);
        this.fontPtr += this.newLocaTableOut.length;
        this.newLocaTableOut = null;
      }
      else
      {
        this.rf.seek(arrayOfInt[1]);
        this.rf.readFully(this.outFont, this.fontPtr, arrayOfInt[2]);
        this.fontPtr += (arrayOfInt[2] + 3 & 0xFFFFFFFC);
      }
    }
  }

  protected void createTableDirectory()
    throws IOException, DocumentException
  {
    this.tableDirectory = new HashMap();
    this.rf.seek(this.directoryOffset);
    int i = this.rf.readInt();
    if (i != 65536)
      throw new DocumentException(this.fileName + " is not a true type file.");
    int j = this.rf.readUnsignedShort();
    this.rf.skipBytes(6);
    for (int k = 0; k < j; k++)
    {
      String str = readStandardString(4);
      int[] arrayOfInt = new int[3];
      arrayOfInt[0] = this.rf.readInt();
      arrayOfInt[1] = this.rf.readInt();
      arrayOfInt[2] = this.rf.readInt();
      this.tableDirectory.put(str, arrayOfInt);
    }
  }

  protected void readLoca()
    throws IOException, DocumentException
  {
    int[] arrayOfInt = (int[])this.tableDirectory.get("head");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'head' does not exist in " + this.fileName);
    this.rf.seek(arrayOfInt[1] + 51);
    this.locaShortTable = (this.rf.readUnsignedShort() == 0);
    arrayOfInt = (int[])this.tableDirectory.get("loca");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'loca' does not exist in " + this.fileName);
    this.rf.seek(arrayOfInt[1]);
    if (this.locaShortTable)
    {
      i = arrayOfInt[2] / 2;
      this.locaTable = new int[i];
      for (j = 0; j < i; j++)
        this.locaTable[j] = (this.rf.readUnsignedShort() * 2);
    }
    int i = arrayOfInt[2] / 4;
    this.locaTable = new int[i];
    for (int j = 0; j < i; j++)
      this.locaTable[j] = this.rf.readInt();
  }

  protected void createNewGlyphTables()
    throws IOException
  {
    this.newLocaTable = new int[this.locaTable.length];
    int[] arrayOfInt = new int[this.glyphsInList.size()];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = ((Integer)this.glyphsInList.get(i)).intValue();
    Arrays.sort(arrayOfInt);
    i = 0;
    for (int j = 0; j < arrayOfInt.length; j++)
    {
      k = arrayOfInt[j];
      i += this.locaTable[(k + 1)] - this.locaTable[k];
    }
    this.glyfTableRealSize = i;
    i = i + 3 & 0xFFFFFFFC;
    this.newGlyfTable = new byte[i];
    j = 0;
    int k = 0;
    for (int m = 0; m < this.newLocaTable.length; m++)
    {
      this.newLocaTable[m] = j;
      if ((k >= arrayOfInt.length) || (arrayOfInt[k] != m))
        continue;
      k++;
      this.newLocaTable[m] = j;
      int n = this.locaTable[m];
      int i1 = this.locaTable[(m + 1)] - n;
      if (i1 <= 0)
        continue;
      this.rf.seek(this.tableGlyphOffset + n);
      this.rf.readFully(this.newGlyfTable, j, i1);
      j += i1;
    }
  }

  protected void locaTobytes()
  {
    if (this.locaShortTable)
      this.locaTableRealSize = (this.newLocaTable.length * 2);
    else
      this.locaTableRealSize = (this.newLocaTable.length * 4);
    this.newLocaTableOut = new byte[this.locaTableRealSize + 3 & 0xFFFFFFFC];
    this.outFont = this.newLocaTableOut;
    this.fontPtr = 0;
    for (int i = 0; i < this.newLocaTable.length; i++)
      if (this.locaShortTable)
        writeFontShort(this.newLocaTable[i] / 2);
      else
        writeFontInt(this.newLocaTable[i]);
  }

  protected void flatGlyphs()
    throws IOException, DocumentException
  {
    int[] arrayOfInt = (int[])this.tableDirectory.get("glyf");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'glyf' does not exist in " + this.fileName);
    Integer localInteger = new Integer(0);
    if (!this.glyphsUsed.containsKey(localInteger))
    {
      this.glyphsUsed.put(localInteger, null);
      this.glyphsInList.add(localInteger);
    }
    this.tableGlyphOffset = arrayOfInt[1];
    for (int i = 0; i < this.glyphsInList.size(); i++)
    {
      int j = ((Integer)this.glyphsInList.get(i)).intValue();
      checkGlyphComposite(j);
    }
  }

  protected void checkGlyphComposite(int paramInt)
    throws IOException
  {
    int i = this.locaTable[paramInt];
    if (i == this.locaTable[(paramInt + 1)])
      return;
    this.rf.seek(this.tableGlyphOffset + i);
    int j = this.rf.readShort();
    if (j >= 0)
      return;
    this.rf.skipBytes(8);
    while (true)
    {
      int k = this.rf.readUnsignedShort();
      Integer localInteger = new Integer(this.rf.readUnsignedShort());
      if (!this.glyphsUsed.containsKey(localInteger))
      {
        this.glyphsUsed.put(localInteger, null);
        this.glyphsInList.add(localInteger);
      }
      if ((k & 0x20) == 0)
        return;
      int m;
      if ((k & 0x1) != 0)
        m = 4;
      else
        m = 2;
      if ((k & 0x8) != 0)
        m += 2;
      else if ((k & 0x40) != 0)
        m += 4;
      if ((k & 0x80) != 0)
        m += 8;
      this.rf.skipBytes(m);
    }
  }

  protected String readStandardString(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    this.rf.readFully(arrayOfByte);
    try
    {
      return new String(arrayOfByte, "Cp1252");
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  protected void writeFontShort(int paramInt)
  {
    this.outFont[(this.fontPtr++)] = (byte)(paramInt >> 8);
    this.outFont[(this.fontPtr++)] = (byte)paramInt;
  }

  protected void writeFontInt(int paramInt)
  {
    this.outFont[(this.fontPtr++)] = (byte)(paramInt >> 24);
    this.outFont[(this.fontPtr++)] = (byte)(paramInt >> 16);
    this.outFont[(this.fontPtr++)] = (byte)(paramInt >> 8);
    this.outFont[(this.fontPtr++)] = (byte)paramInt;
  }

  protected void writeFontString(String paramString)
  {
    byte[] arrayOfByte = PdfEncodings.convertToBytes(paramString, "Cp1252");
    System.arraycopy(arrayOfByte, 0, this.outFont, this.fontPtr, arrayOfByte.length);
    this.fontPtr += arrayOfByte.length;
  }

  protected int calculateChecksum(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length / 4;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    for (int i2 = 0; i2 < i; i2++)
    {
      n += (paramArrayOfByte[(i1++)] & 0xFF);
      m += (paramArrayOfByte[(i1++)] & 0xFF);
      k += (paramArrayOfByte[(i1++)] & 0xFF);
      j += (paramArrayOfByte[(i1++)] & 0xFF);
    }
    return j + (k << 8) + (m << 16) + (n << 24);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.TrueTypeFontSubSet
 * JD-Core Version:    0.6.0
 */