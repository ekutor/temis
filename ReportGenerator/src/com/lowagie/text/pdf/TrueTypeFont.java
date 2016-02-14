package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

class TrueTypeFont extends BaseFont
{
  static final String[] codePages = { "1252 Latin 1", "1250 Latin 2: Eastern Europe", "1251 Cyrillic", "1253 Greek", "1254 Turkish", "1255 Hebrew", "1256 Arabic", "1257 Windows Baltic", "1258 Vietnamese", null, null, null, null, null, null, null, "874 Thai", "932 JIS/Japan", "936 Chinese: Simplified chars--PRC and Singapore", "949 Korean Wansung", "950 Chinese: Traditional chars--Taiwan and Hong Kong", "1361 Korean Johab", null, null, null, null, null, null, null, "Macintosh Character Set (US Roman)", "OEM Character Set", "Symbol Character Set", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "869 IBM Greek", "866 MS-DOS Russian", "865 MS-DOS Nordic", "864 Arabic", "863 MS-DOS Canadian French", "862 Hebrew", "861 MS-DOS Icelandic", "860 MS-DOS Portuguese", "857 IBM Turkish", "855 IBM Cyrillic; primarily Russian", "852 Latin 2", "775 MS-DOS Baltic", "737 Greek; former 437 G", "708 Arabic; ASMO 708", "850 WE/Latin 1", "437 US" };
  protected boolean justNames = false;
  protected HashMap tables;
  protected RandomAccessFileOrArray rf;
  protected String fileName;
  protected boolean cff = false;
  protected int cffOffset;
  protected int cffLength;
  protected int directoryOffset;
  protected String ttcIndex;
  protected String style = "";
  protected FontHeader head = new FontHeader();
  protected HorizontalHeader hhea = new HorizontalHeader();
  protected WindowsMetrics os_2 = new WindowsMetrics();
  protected int[] GlyphWidths;
  protected int[][] bboxes;
  protected HashMap cmap10;
  protected HashMap cmap31;
  protected HashMap cmapExt;
  protected IntHashtable kerning = new IntHashtable();
  protected String fontName;
  protected String[][] fullName;
  protected String[][] allNameEntries;
  protected String[][] familyName;
  protected double italicAngle;
  protected boolean isFixedPitch = false;
  protected int underlinePosition;
  protected int underlineThickness;

  protected TrueTypeFont()
  {
  }

  TrueTypeFont(String paramString1, String paramString2, boolean paramBoolean1, byte[] paramArrayOfByte, boolean paramBoolean2, boolean paramBoolean3)
    throws DocumentException, IOException
  {
    this.justNames = paramBoolean2;
    String str1 = getBaseName(paramString1);
    String str2 = getTTCName(str1);
    if (str1.length() < paramString1.length())
      this.style = paramString1.substring(str1.length());
    this.encoding = paramString2;
    this.embedded = paramBoolean1;
    this.fileName = str2;
    this.fontType = 1;
    this.ttcIndex = "";
    if (str2.length() < str1.length())
      this.ttcIndex = str1.substring(str2.length() + 1);
    if ((this.fileName.toLowerCase().endsWith(".ttf")) || (this.fileName.toLowerCase().endsWith(".otf")) || (this.fileName.toLowerCase().endsWith(".ttc")))
    {
      process(paramArrayOfByte, paramBoolean3);
      if ((!paramBoolean2) && (this.embedded) && (this.os_2.fsType == 2))
        throw new DocumentException(this.fileName + this.style + " cannot be embedded due to licensing restrictions.");
    }
    else
    {
      throw new DocumentException(this.fileName + this.style + " is not a TTF, OTF or TTC font file.");
    }
    if (!this.encoding.startsWith("#"))
      PdfEncodings.convertToBytes(" ", paramString2);
    createEncoding();
  }

  protected static String getTTCName(String paramString)
  {
    int i = paramString.toLowerCase().indexOf(".ttc,");
    if (i < 0)
      return paramString;
    return paramString.substring(0, i + 4);
  }

  void fillTables()
    throws DocumentException, IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("head");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'head' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0] + 16);
    this.head.flags = this.rf.readUnsignedShort();
    this.head.unitsPerEm = this.rf.readUnsignedShort();
    this.rf.skipBytes(16);
    this.head.xMin = this.rf.readShort();
    this.head.yMin = this.rf.readShort();
    this.head.xMax = this.rf.readShort();
    this.head.yMax = this.rf.readShort();
    this.head.macStyle = this.rf.readUnsignedShort();
    arrayOfInt = (int[])this.tables.get("hhea");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'hhea' does not exist " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0] + 4);
    this.hhea.Ascender = this.rf.readShort();
    this.hhea.Descender = this.rf.readShort();
    this.hhea.LineGap = this.rf.readShort();
    this.hhea.advanceWidthMax = this.rf.readUnsignedShort();
    this.hhea.minLeftSideBearing = this.rf.readShort();
    this.hhea.minRightSideBearing = this.rf.readShort();
    this.hhea.xMaxExtent = this.rf.readShort();
    this.hhea.caretSlopeRise = this.rf.readShort();
    this.hhea.caretSlopeRun = this.rf.readShort();
    this.rf.skipBytes(12);
    this.hhea.numberOfHMetrics = this.rf.readUnsignedShort();
    arrayOfInt = (int[])this.tables.get("OS/2");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'OS/2' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0]);
    int i = this.rf.readUnsignedShort();
    this.os_2.xAvgCharWidth = this.rf.readShort();
    this.os_2.usWeightClass = this.rf.readUnsignedShort();
    this.os_2.usWidthClass = this.rf.readUnsignedShort();
    this.os_2.fsType = this.rf.readShort();
    this.os_2.ySubscriptXSize = this.rf.readShort();
    this.os_2.ySubscriptYSize = this.rf.readShort();
    this.os_2.ySubscriptXOffset = this.rf.readShort();
    this.os_2.ySubscriptYOffset = this.rf.readShort();
    this.os_2.ySuperscriptXSize = this.rf.readShort();
    this.os_2.ySuperscriptYSize = this.rf.readShort();
    this.os_2.ySuperscriptXOffset = this.rf.readShort();
    this.os_2.ySuperscriptYOffset = this.rf.readShort();
    this.os_2.yStrikeoutSize = this.rf.readShort();
    this.os_2.yStrikeoutPosition = this.rf.readShort();
    this.os_2.sFamilyClass = this.rf.readShort();
    this.rf.readFully(this.os_2.panose);
    this.rf.skipBytes(16);
    this.rf.readFully(this.os_2.achVendID);
    this.os_2.fsSelection = this.rf.readUnsignedShort();
    this.os_2.usFirstCharIndex = this.rf.readUnsignedShort();
    this.os_2.usLastCharIndex = this.rf.readUnsignedShort();
    this.os_2.sTypoAscender = this.rf.readShort();
    this.os_2.sTypoDescender = this.rf.readShort();
    if (this.os_2.sTypoDescender > 0)
      this.os_2.sTypoDescender = (short)(-this.os_2.sTypoDescender);
    this.os_2.sTypoLineGap = this.rf.readShort();
    this.os_2.usWinAscent = this.rf.readUnsignedShort();
    this.os_2.usWinDescent = this.rf.readUnsignedShort();
    this.os_2.ulCodePageRange1 = 0;
    this.os_2.ulCodePageRange2 = 0;
    if (i > 0)
    {
      this.os_2.ulCodePageRange1 = this.rf.readInt();
      this.os_2.ulCodePageRange2 = this.rf.readInt();
    }
    if (i > 1)
    {
      this.rf.skipBytes(2);
      this.os_2.sCapHeight = this.rf.readShort();
    }
    else
    {
      this.os_2.sCapHeight = (int)(0.7D * this.head.unitsPerEm);
    }
    arrayOfInt = (int[])this.tables.get("post");
    if (arrayOfInt == null)
    {
      this.italicAngle = (-Math.atan2(this.hhea.caretSlopeRun, this.hhea.caretSlopeRise) * 180.0D / 3.141592653589793D);
      return;
    }
    this.rf.seek(arrayOfInt[0] + 4);
    int j = this.rf.readShort();
    int k = this.rf.readUnsignedShort();
    this.italicAngle = (j + k / 16384.0D);
    this.underlinePosition = this.rf.readShort();
    this.underlineThickness = this.rf.readShort();
    this.isFixedPitch = (this.rf.readInt() != 0);
  }

  String getBaseFont()
    throws DocumentException, IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("name");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0] + 2);
    int i = this.rf.readUnsignedShort();
    int j = this.rf.readUnsignedShort();
    for (int k = 0; k < i; k++)
    {
      int m = this.rf.readUnsignedShort();
      int n = this.rf.readUnsignedShort();
      int i1 = this.rf.readUnsignedShort();
      int i2 = this.rf.readUnsignedShort();
      int i3 = this.rf.readUnsignedShort();
      int i4 = this.rf.readUnsignedShort();
      if (i2 != 6)
        continue;
      this.rf.seek(arrayOfInt[0] + j + i4);
      if ((m == 0) || (m == 3))
        return readUnicodeString(i3);
      return readStandardString(i3);
    }
    File localFile = new File(this.fileName);
    return localFile.getName().replace(' ', '-');
  }

  String[][] getNames(int paramInt)
    throws DocumentException, IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("name");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0] + 2);
    int i = this.rf.readUnsignedShort();
    int j = this.rf.readUnsignedShort();
    ArrayList localArrayList = new ArrayList();
    for (int k = 0; k < i; k++)
    {
      m = this.rf.readUnsignedShort();
      int n = this.rf.readUnsignedShort();
      int i1 = this.rf.readUnsignedShort();
      int i2 = this.rf.readUnsignedShort();
      int i3 = this.rf.readUnsignedShort();
      int i4 = this.rf.readUnsignedShort();
      if (i2 != paramInt)
        continue;
      int i5 = this.rf.getFilePointer();
      this.rf.seek(arrayOfInt[0] + j + i4);
      String str;
      if ((m == 0) || (m == 3) || ((m == 2) && (n == 1)))
        str = readUnicodeString(i3);
      else
        str = readStandardString(i3);
      localArrayList.add(new String[] { String.valueOf(m), String.valueOf(n), String.valueOf(i1), str });
      this.rf.seek(i5);
    }
    String[][] arrayOfString; = new String[localArrayList.size()][];
    for (int m = 0; m < localArrayList.size(); m++)
      arrayOfString;[m] = ((String[])localArrayList.get(m));
    return arrayOfString;;
  }

  String[][] getAllNames()
    throws DocumentException, IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("name");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0] + 2);
    int i = this.rf.readUnsignedShort();
    int j = this.rf.readUnsignedShort();
    ArrayList localArrayList = new ArrayList();
    for (int k = 0; k < i; k++)
    {
      m = this.rf.readUnsignedShort();
      int n = this.rf.readUnsignedShort();
      int i1 = this.rf.readUnsignedShort();
      int i2 = this.rf.readUnsignedShort();
      int i3 = this.rf.readUnsignedShort();
      int i4 = this.rf.readUnsignedShort();
      int i5 = this.rf.getFilePointer();
      this.rf.seek(arrayOfInt[0] + j + i4);
      String str;
      if ((m == 0) || (m == 3) || ((m == 2) && (n == 1)))
        str = readUnicodeString(i3);
      else
        str = readStandardString(i3);
      localArrayList.add(new String[] { String.valueOf(i2), String.valueOf(m), String.valueOf(n), String.valueOf(i1), str });
      this.rf.seek(i5);
    }
    String[][] arrayOfString; = new String[localArrayList.size()][];
    for (int m = 0; m < localArrayList.size(); m++)
      arrayOfString;[m] = ((String[])localArrayList.get(m));
    return arrayOfString;;
  }

  void checkCff()
  {
    int[] arrayOfInt = (int[])this.tables.get("CFF ");
    if (arrayOfInt != null)
    {
      this.cff = true;
      this.cffOffset = arrayOfInt[0];
      this.cffLength = arrayOfInt[1];
    }
  }

  void process(byte[] paramArrayOfByte, boolean paramBoolean)
    throws DocumentException, IOException
  {
    this.tables = new HashMap();
    try
    {
      if (paramArrayOfByte == null)
        this.rf = new RandomAccessFileOrArray(this.fileName, paramBoolean, Document.plainRandomAccess);
      else
        this.rf = new RandomAccessFileOrArray(paramArrayOfByte);
      if (this.ttcIndex.length() > 0)
      {
        i = Integer.parseInt(this.ttcIndex);
        if (i < 0)
          throw new DocumentException("The font index for " + this.fileName + " must be positive.");
        String str1 = readStandardString(4);
        if (!str1.equals("ttcf"))
          throw new DocumentException(this.fileName + " is not a valid TTC file.");
        this.rf.skipBytes(4);
        k = this.rf.readInt();
        if (i >= k)
          throw new DocumentException("The font index for " + this.fileName + " must be between 0 and " + (k - 1) + ". It was " + i + ".");
        this.rf.skipBytes(i * 4);
        this.directoryOffset = this.rf.readInt();
      }
      this.rf.seek(this.directoryOffset);
      int i = this.rf.readInt();
      if ((i != 65536) && (i != 1330926671))
        throw new DocumentException(this.fileName + " is not a valid TTF or OTF file.");
      int j = this.rf.readUnsignedShort();
      this.rf.skipBytes(6);
      for (int k = 0; k < j; k++)
      {
        String str2 = readStandardString(4);
        this.rf.skipBytes(4);
        int[] arrayOfInt = new int[2];
        arrayOfInt[0] = this.rf.readInt();
        arrayOfInt[1] = this.rf.readInt();
        this.tables.put(str2, arrayOfInt);
      }
      checkCff();
      this.fontName = getBaseFont();
      this.fullName = getNames(4);
      this.familyName = getNames(1);
      this.allNameEntries = getAllNames();
      if (!this.justNames)
      {
        fillTables();
        readGlyphWidths();
        readCMaps();
        readKerning();
        readBbox();
        this.GlyphWidths = null;
      }
    }
    finally
    {
      if (this.rf != null)
      {
        this.rf.close();
        if (!this.embedded)
          this.rf = null;
      }
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

  protected String readUnicodeString(int paramInt)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    paramInt /= 2;
    for (int i = 0; i < paramInt; i++)
      localStringBuffer.append(this.rf.readChar());
    return localStringBuffer.toString();
  }

  protected void readGlyphWidths()
    throws DocumentException, IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("hmtx");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'hmtx' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0]);
    this.GlyphWidths = new int[this.hhea.numberOfHMetrics];
    for (int i = 0; i < this.hhea.numberOfHMetrics; i++)
    {
      this.GlyphWidths[i] = (this.rf.readUnsignedShort() * 1000 / this.head.unitsPerEm);
      this.rf.readUnsignedShort();
    }
  }

  protected int getGlyphWidth(int paramInt)
  {
    if (paramInt >= this.GlyphWidths.length)
      paramInt = this.GlyphWidths.length - 1;
    return this.GlyphWidths[paramInt];
  }

  private void readBbox()
    throws DocumentException, IOException
  {
    int[] arrayOfInt1 = (int[])this.tables.get("head");
    if (arrayOfInt1 == null)
      throw new DocumentException("Table 'head' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt1[0] + 51);
    int i = this.rf.readUnsignedShort() == 0 ? 1 : 0;
    arrayOfInt1 = (int[])this.tables.get("loca");
    if (arrayOfInt1 == null)
      return;
    this.rf.seek(arrayOfInt1[0]);
    if (i != 0)
    {
      j = arrayOfInt1[1] / 2;
      arrayOfInt2 = new int[j];
      for (k = 0; k < j; k++)
        arrayOfInt2[k] = (this.rf.readUnsignedShort() * 2);
    }
    int j = arrayOfInt1[1] / 4;
    int[] arrayOfInt2 = new int[j];
    for (int k = 0; k < j; k++)
      arrayOfInt2[k] = this.rf.readInt();
    arrayOfInt1 = (int[])this.tables.get("glyf");
    if (arrayOfInt1 == null)
      throw new DocumentException("Table 'glyf' does not exist in " + this.fileName + this.style);
    j = arrayOfInt1[0];
    this.bboxes = new int[arrayOfInt2.length - 1][];
    for (k = 0; k < arrayOfInt2.length - 1; k++)
    {
      int m = arrayOfInt2[k];
      if (m == arrayOfInt2[(k + 1)])
        continue;
      this.rf.seek(j + m + 2);
      this.bboxes[k] = { this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm };
    }
  }

  void readCMaps()
    throws DocumentException, IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("cmap");
    if (arrayOfInt == null)
      throw new DocumentException("Table 'cmap' does not exist in " + this.fileName + this.style);
    this.rf.seek(arrayOfInt[0]);
    this.rf.skipBytes(2);
    int i = this.rf.readUnsignedShort();
    this.fontSpecific = false;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    for (int i1 = 0; i1 < i; i1++)
    {
      int i2 = this.rf.readUnsignedShort();
      int i3 = this.rf.readUnsignedShort();
      int i4 = this.rf.readInt();
      if ((i2 == 3) && (i3 == 0))
      {
        this.fontSpecific = true;
        m = i4;
      }
      else if ((i2 == 3) && (i3 == 1))
      {
        k = i4;
      }
      else if ((i2 == 3) && (i3 == 10))
      {
        n = i4;
      }
      if ((i2 != 1) || (i3 != 0))
        continue;
      j = i4;
    }
    if (j > 0)
    {
      this.rf.seek(arrayOfInt[0] + j);
      i1 = this.rf.readUnsignedShort();
      switch (i1)
      {
      case 0:
        this.cmap10 = readFormat0();
        break;
      case 4:
        this.cmap10 = readFormat4();
        break;
      case 6:
        this.cmap10 = readFormat6();
      }
    }
    if (k > 0)
    {
      this.rf.seek(arrayOfInt[0] + k);
      i1 = this.rf.readUnsignedShort();
      if (i1 == 4)
        this.cmap31 = readFormat4();
    }
    if (m > 0)
    {
      this.rf.seek(arrayOfInt[0] + m);
      i1 = this.rf.readUnsignedShort();
      if (i1 == 4)
        this.cmap10 = readFormat4();
    }
    if (n > 0)
    {
      this.rf.seek(arrayOfInt[0] + n);
      i1 = this.rf.readUnsignedShort();
      switch (i1)
      {
      case 0:
        this.cmapExt = readFormat0();
        break;
      case 4:
        this.cmapExt = readFormat4();
        break;
      case 6:
        this.cmapExt = readFormat6();
        break;
      case 12:
        this.cmapExt = readFormat12();
      }
    }
  }

  HashMap readFormat12()
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    this.rf.skipBytes(2);
    int i = this.rf.readInt();
    this.rf.skipBytes(4);
    int j = this.rf.readInt();
    for (int k = 0; k < j; k++)
    {
      int m = this.rf.readInt();
      int n = this.rf.readInt();
      int i1 = this.rf.readInt();
      for (int i2 = m; i2 <= n; i2++)
      {
        int[] arrayOfInt = new int[2];
        arrayOfInt[0] = i1;
        arrayOfInt[1] = getGlyphWidth(arrayOfInt[0]);
        localHashMap.put(new Integer(i2), arrayOfInt);
        i1++;
      }
    }
    return localHashMap;
  }

  HashMap readFormat0()
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    this.rf.skipBytes(4);
    for (int i = 0; i < 256; i++)
    {
      int[] arrayOfInt = new int[2];
      arrayOfInt[0] = this.rf.readUnsignedByte();
      arrayOfInt[1] = getGlyphWidth(arrayOfInt[0]);
      localHashMap.put(new Integer(i), arrayOfInt);
    }
    return localHashMap;
  }

  HashMap readFormat4()
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    int i = this.rf.readUnsignedShort();
    this.rf.skipBytes(2);
    int j = this.rf.readUnsignedShort() / 2;
    this.rf.skipBytes(6);
    int[] arrayOfInt1 = new int[j];
    for (int k = 0; k < j; k++)
      arrayOfInt1[k] = this.rf.readUnsignedShort();
    this.rf.skipBytes(2);
    int[] arrayOfInt2 = new int[j];
    for (int m = 0; m < j; m++)
      arrayOfInt2[m] = this.rf.readUnsignedShort();
    int[] arrayOfInt3 = new int[j];
    for (int n = 0; n < j; n++)
      arrayOfInt3[n] = this.rf.readUnsignedShort();
    int[] arrayOfInt4 = new int[j];
    for (int i1 = 0; i1 < j; i1++)
      arrayOfInt4[i1] = this.rf.readUnsignedShort();
    int[] arrayOfInt5 = new int[i / 2 - 8 - j * 4];
    for (int i2 = 0; i2 < arrayOfInt5.length; i2++)
      arrayOfInt5[i2] = this.rf.readUnsignedShort();
    for (i2 = 0; i2 < j; i2++)
      for (int i4 = arrayOfInt2[i2]; (i4 <= arrayOfInt1[i2]) && (i4 != 65535); i4++)
      {
        int i3;
        if (arrayOfInt4[i2] == 0)
        {
          i3 = i4 + arrayOfInt3[i2] & 0xFFFF;
        }
        else
        {
          int i5 = i2 + arrayOfInt4[i2] / 2 - j + i4 - arrayOfInt2[i2];
          if (i5 >= arrayOfInt5.length)
            continue;
          i3 = arrayOfInt5[i5] + arrayOfInt3[i2] & 0xFFFF;
        }
        int[] arrayOfInt6 = new int[2];
        arrayOfInt6[0] = i3;
        arrayOfInt6[1] = getGlyphWidth(arrayOfInt6[0]);
        localHashMap.put(new Integer(this.fontSpecific ? i4 : (i4 & 0xFF00) == 61440 ? i4 & 0xFF : i4), arrayOfInt6);
      }
    return localHashMap;
  }

  HashMap readFormat6()
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    this.rf.skipBytes(4);
    int i = this.rf.readUnsignedShort();
    int j = this.rf.readUnsignedShort();
    for (int k = 0; k < j; k++)
    {
      int[] arrayOfInt = new int[2];
      arrayOfInt[0] = this.rf.readUnsignedShort();
      arrayOfInt[1] = getGlyphWidth(arrayOfInt[0]);
      localHashMap.put(new Integer(k + i), arrayOfInt);
    }
    return localHashMap;
  }

  void readKerning()
    throws IOException
  {
    int[] arrayOfInt = (int[])this.tables.get("kern");
    if (arrayOfInt == null)
      return;
    this.rf.seek(arrayOfInt[0] + 2);
    int i = this.rf.readUnsignedShort();
    int j = arrayOfInt[0] + 4;
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      j += k;
      this.rf.seek(j);
      this.rf.skipBytes(2);
      k = this.rf.readUnsignedShort();
      int n = this.rf.readUnsignedShort();
      if ((n & 0xFFF7) != 1)
        continue;
      int i1 = this.rf.readUnsignedShort();
      this.rf.skipBytes(6);
      for (int i2 = 0; i2 < i1; i2++)
      {
        int i3 = this.rf.readInt();
        int i4 = this.rf.readShort() * 1000 / this.head.unitsPerEm;
        this.kerning.put(i3, i4);
      }
    }
  }

  public int getKerning(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = getMetricsTT(paramInt1);
    if (arrayOfInt == null)
      return 0;
    int i = arrayOfInt[0];
    arrayOfInt = getMetricsTT(paramInt2);
    if (arrayOfInt == null)
      return 0;
    int j = arrayOfInt[0];
    return this.kerning.get((i << 16) + j);
  }

  int getRawWidth(int paramInt, String paramString)
  {
    int[] arrayOfInt = getMetricsTT(paramInt);
    if (arrayOfInt == null)
      return 0;
    return arrayOfInt[1];
  }

  protected PdfDictionary getFontDescriptor(PdfIndirectReference paramPdfIndirectReference1, String paramString, PdfIndirectReference paramPdfIndirectReference2)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.FONTDESCRIPTOR);
    localPdfDictionary.put(PdfName.ASCENT, new PdfNumber(this.os_2.sTypoAscender * 1000 / this.head.unitsPerEm));
    localPdfDictionary.put(PdfName.CAPHEIGHT, new PdfNumber(this.os_2.sCapHeight * 1000 / this.head.unitsPerEm));
    localPdfDictionary.put(PdfName.DESCENT, new PdfNumber(this.os_2.sTypoDescender * 1000 / this.head.unitsPerEm));
    localPdfDictionary.put(PdfName.FONTBBOX, new PdfRectangle(this.head.xMin * 1000 / this.head.unitsPerEm, this.head.yMin * 1000 / this.head.unitsPerEm, this.head.xMax * 1000 / this.head.unitsPerEm, this.head.yMax * 1000 / this.head.unitsPerEm));
    if (paramPdfIndirectReference2 != null)
      localPdfDictionary.put(PdfName.CIDSET, paramPdfIndirectReference2);
    if (this.cff)
    {
      if (this.encoding.startsWith("Identity-"))
        localPdfDictionary.put(PdfName.FONTNAME, new PdfName(paramString + this.fontName + "-" + this.encoding));
      else
        localPdfDictionary.put(PdfName.FONTNAME, new PdfName(paramString + this.fontName + this.style));
    }
    else
      localPdfDictionary.put(PdfName.FONTNAME, new PdfName(paramString + this.fontName + this.style));
    localPdfDictionary.put(PdfName.ITALICANGLE, new PdfNumber(this.italicAngle));
    localPdfDictionary.put(PdfName.STEMV, new PdfNumber(80));
    if (paramPdfIndirectReference1 != null)
      if (this.cff)
        localPdfDictionary.put(PdfName.FONTFILE3, paramPdfIndirectReference1);
      else
        localPdfDictionary.put(PdfName.FONTFILE2, paramPdfIndirectReference1);
    int i = 0;
    if (this.isFixedPitch)
      i |= 1;
    i |= (this.fontSpecific ? 4 : 32);
    if ((this.head.macStyle & 0x2) != 0)
      i |= 64;
    if ((this.head.macStyle & 0x1) != 0)
      i |= 262144;
    localPdfDictionary.put(PdfName.FLAGS, new PdfNumber(i));
    return localPdfDictionary;
  }

  protected PdfDictionary getFontBaseType(PdfIndirectReference paramPdfIndirectReference, String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.FONT);
    if (this.cff)
    {
      localPdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE1);
      localPdfDictionary.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
    }
    else
    {
      localPdfDictionary.put(PdfName.SUBTYPE, PdfName.TRUETYPE);
      localPdfDictionary.put(PdfName.BASEFONT, new PdfName(paramString + this.fontName + this.style));
    }
    localPdfDictionary.put(PdfName.BASEFONT, new PdfName(paramString + this.fontName + this.style));
    if (!this.fontSpecific)
    {
      for (int i = paramInt1; i <= paramInt2; i++)
      {
        if (this.differences[i].equals(".notdef"))
          continue;
        paramInt1 = i;
        break;
      }
      if ((this.encoding.equals("Cp1252")) || (this.encoding.equals("MacRoman")))
      {
        localPdfDictionary.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
      }
      else
      {
        localObject = new PdfDictionary(PdfName.ENCODING);
        PdfArray localPdfArray = new PdfArray();
        int k = 1;
        for (int m = paramInt1; m <= paramInt2; m++)
          if (paramArrayOfByte[m] != 0)
          {
            if (k != 0)
            {
              localPdfArray.add(new PdfNumber(m));
              k = 0;
            }
            localPdfArray.add(new PdfName(this.differences[m]));
          }
          else
          {
            k = 1;
          }
        ((PdfDictionary)localObject).put(PdfName.DIFFERENCES, localPdfArray);
        localPdfDictionary.put(PdfName.ENCODING, (PdfObject)localObject);
      }
    }
    localPdfDictionary.put(PdfName.FIRSTCHAR, new PdfNumber(paramInt1));
    localPdfDictionary.put(PdfName.LASTCHAR, new PdfNumber(paramInt2));
    Object localObject = new PdfArray();
    for (int j = paramInt1; j <= paramInt2; j++)
      if (paramArrayOfByte[j] == 0)
        ((PdfArray)localObject).add(new PdfNumber(0));
      else
        ((PdfArray)localObject).add(new PdfNumber(this.widths[j]));
    localPdfDictionary.put(PdfName.WIDTHS, (PdfObject)localObject);
    if (paramPdfIndirectReference != null)
      localPdfDictionary.put(PdfName.FONTDESCRIPTOR, paramPdfIndirectReference);
    return (PdfDictionary)localPdfDictionary;
  }

  protected byte[] getFullFont()
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = null;
    try
    {
      localRandomAccessFileOrArray = new RandomAccessFileOrArray(this.rf);
      localRandomAccessFileOrArray.reOpen();
      byte[] arrayOfByte1 = new byte[localRandomAccessFileOrArray.length()];
      localRandomAccessFileOrArray.readFully(arrayOfByte1);
      arrayOfByte2 = arrayOfByte1;
    }
    finally
    {
      try
      {
        byte[] arrayOfByte2;
        if (localRandomAccessFileOrArray != null)
          localRandomAccessFileOrArray.close();
      }
      catch (Exception localException)
      {
      }
    }
  }

  protected static int[] compactRanges(ArrayList paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      int[] arrayOfInt2 = (int[])paramArrayList.get(i);
      for (int k = 0; k < arrayOfInt2.length; k += 2)
        localArrayList.add(new int[] { Math.max(0, Math.min(arrayOfInt2[k], arrayOfInt2[(k + 1)])), Math.min(65535, Math.max(arrayOfInt2[k], arrayOfInt2[(k + 1)])) });
    }
    int[] arrayOfInt3;
    for (i = 0; i < localArrayList.size() - 1; i++)
      for (j = i + 1; j < localArrayList.size(); j++)
      {
        arrayOfInt3 = (int[])localArrayList.get(i);
        int[] arrayOfInt4 = (int[])localArrayList.get(j);
        if (((arrayOfInt3[0] < arrayOfInt4[0]) || (arrayOfInt3[0] > arrayOfInt4[1])) && ((arrayOfInt3[1] < arrayOfInt4[0]) || (arrayOfInt3[0] > arrayOfInt4[1])))
          continue;
        arrayOfInt3[0] = Math.min(arrayOfInt3[0], arrayOfInt4[0]);
        arrayOfInt3[1] = Math.max(arrayOfInt3[1], arrayOfInt4[1]);
        localArrayList.remove(j);
        j--;
      }
    int[] arrayOfInt1 = new int[localArrayList.size() * 2];
    for (int j = 0; j < localArrayList.size(); j++)
    {
      arrayOfInt3 = (int[])localArrayList.get(j);
      arrayOfInt1[(j * 2)] = arrayOfInt3[0];
      arrayOfInt1[(j * 2 + 1)] = arrayOfInt3[1];
    }
    return arrayOfInt1;
  }

  protected void addRangeUni(HashMap paramHashMap, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && ((this.subsetRanges != null) || (this.directoryOffset > 0)))
    {
      int[] arrayOfInt1 = (this.subsetRanges == null) && (this.directoryOffset > 0) ? new int[] { 0, 65535 } : compactRanges(this.subsetRanges);
      HashMap localHashMap;
      if ((!this.fontSpecific) && (this.cmap31 != null))
        localHashMap = this.cmap31;
      else if ((this.fontSpecific) && (this.cmap10 != null))
        localHashMap = this.cmap10;
      else if (this.cmap31 != null)
        localHashMap = this.cmap31;
      else
        localHashMap = this.cmap10;
      Iterator localIterator = localHashMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        int[] arrayOfInt2 = (int[])localEntry.getValue();
        Integer localInteger = new Integer(arrayOfInt2[0]);
        if (paramHashMap.containsKey(localInteger))
          continue;
        int i = ((Integer)localEntry.getKey()).intValue();
        int j = 1;
        for (int k = 0; k < arrayOfInt1.length; k += 2)
        {
          if ((i < arrayOfInt1[k]) || (i > arrayOfInt1[(k + 1)]))
            continue;
          j = 0;
          break;
        }
        if (j != 0)
          continue;
        paramHashMap.put(localInteger, paramBoolean1 ? new int[] { arrayOfInt2[0], arrayOfInt2[1], i } : null);
      }
    }
  }

  void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException
  {
    int i = ((Integer)paramArrayOfObject[0]).intValue();
    int j = ((Integer)paramArrayOfObject[1]).intValue();
    byte[] arrayOfByte1 = (byte[])paramArrayOfObject[2];
    boolean bool = (((Boolean)paramArrayOfObject[3]).booleanValue()) && (this.subset);
    if (!bool)
    {
      i = 0;
      j = arrayOfByte1.length - 1;
      for (int k = 0; k < arrayOfByte1.length; k++)
        arrayOfByte1[k] = 1;
    }
    PdfIndirectReference localPdfIndirectReference = null;
    Object localObject1 = null;
    PdfIndirectObject localPdfIndirectObject = null;
    String str = "";
    if (this.embedded)
      if (this.cff)
      {
        localObject1 = new BaseFont.StreamFont(readCffFont(), "Type1C", this.compressionLevel);
        localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
        localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
      }
      else
      {
        if (bool)
          str = createSubsetPrefix();
        HashMap localHashMap = new HashMap();
        for (int m = i; m <= j; m++)
        {
          if (arrayOfByte1[m] == 0)
            continue;
          localObject2 = null;
          if (this.specialMap != null)
          {
            int[] arrayOfInt = GlyphList.nameToUnicode(this.differences[m]);
            if (arrayOfInt != null)
              localObject2 = getMetricsTT(arrayOfInt[0]);
          }
          else if (this.fontSpecific)
          {
            localObject2 = getMetricsTT(m);
          }
          else
          {
            localObject2 = getMetricsTT(this.unicodeDifferences[m]);
          }
          if (localObject2 == null)
            continue;
          localHashMap.put(new Integer(localObject2[0]), null);
        }
        addRangeUni(localHashMap, false, bool);
        byte[] arrayOfByte2 = null;
        if ((bool) || (this.directoryOffset != 0) || (this.subsetRanges != null))
        {
          localObject2 = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), localHashMap, this.directoryOffset, true, !bool);
          arrayOfByte2 = ((TrueTypeFontSubSet)localObject2).process();
        }
        else
        {
          arrayOfByte2 = getFullFont();
        }
        Object localObject2 = { arrayOfByte2.length };
        localObject1 = new BaseFont.StreamFont(arrayOfByte2, localObject2, this.compressionLevel);
        localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
        localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
      }
    localObject1 = getFontDescriptor(localPdfIndirectReference, str, null);
    if (localObject1 != null)
    {
      localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
      localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
    }
    localObject1 = getFontBaseType(localPdfIndirectReference, str, i, j, arrayOfByte1);
    paramPdfWriter.addToBody((PdfObject)localObject1, paramPdfIndirectReference);
  }

  protected byte[] readCffFont()
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = new RandomAccessFileOrArray(this.rf);
    byte[] arrayOfByte = new byte[this.cffLength];
    try
    {
      localRandomAccessFileOrArray.reOpen();
      localRandomAccessFileOrArray.seek(this.cffOffset);
      localRandomAccessFileOrArray.readFully(arrayOfByte);
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException2)
      {
      }
    }
    return arrayOfByte;
  }

  public PdfStream getFullFontStream()
    throws IOException, DocumentException
  {
    if (this.cff)
      return new BaseFont.StreamFont(readCffFont(), "Type1C", this.compressionLevel);
    byte[] arrayOfByte = getFullFont();
    int[] arrayOfInt = { arrayOfByte.length };
    return new BaseFont.StreamFont(arrayOfByte, arrayOfInt, this.compressionLevel);
  }

  public float getFontDescriptor(int paramInt, float paramFloat)
  {
    switch (paramInt)
    {
    case 1:
      return this.os_2.sTypoAscender * paramFloat / this.head.unitsPerEm;
    case 2:
      return this.os_2.sCapHeight * paramFloat / this.head.unitsPerEm;
    case 3:
      return this.os_2.sTypoDescender * paramFloat / this.head.unitsPerEm;
    case 4:
      return (float)this.italicAngle;
    case 5:
      return paramFloat * this.head.xMin / this.head.unitsPerEm;
    case 6:
      return paramFloat * this.head.yMin / this.head.unitsPerEm;
    case 7:
      return paramFloat * this.head.xMax / this.head.unitsPerEm;
    case 8:
      return paramFloat * this.head.yMax / this.head.unitsPerEm;
    case 9:
      return paramFloat * this.hhea.Ascender / this.head.unitsPerEm;
    case 10:
      return paramFloat * this.hhea.Descender / this.head.unitsPerEm;
    case 11:
      return paramFloat * this.hhea.LineGap / this.head.unitsPerEm;
    case 12:
      return paramFloat * this.hhea.advanceWidthMax / this.head.unitsPerEm;
    case 13:
      return (this.underlinePosition - this.underlineThickness / 2) * paramFloat / this.head.unitsPerEm;
    case 14:
      return this.underlineThickness * paramFloat / this.head.unitsPerEm;
    case 15:
      return this.os_2.yStrikeoutPosition * paramFloat / this.head.unitsPerEm;
    case 16:
      return this.os_2.yStrikeoutSize * paramFloat / this.head.unitsPerEm;
    case 17:
      return this.os_2.ySubscriptYSize * paramFloat / this.head.unitsPerEm;
    case 18:
      return -this.os_2.ySubscriptYOffset * paramFloat / this.head.unitsPerEm;
    case 19:
      return this.os_2.ySuperscriptYSize * paramFloat / this.head.unitsPerEm;
    case 20:
      return this.os_2.ySuperscriptYOffset * paramFloat / this.head.unitsPerEm;
    }
    return 0.0F;
  }

  public int[] getMetricsTT(int paramInt)
  {
    if (this.cmapExt != null)
      return (int[])this.cmapExt.get(new Integer(paramInt));
    if ((!this.fontSpecific) && (this.cmap31 != null))
      return (int[])this.cmap31.get(new Integer(paramInt));
    if ((this.fontSpecific) && (this.cmap10 != null))
      return (int[])this.cmap10.get(new Integer(paramInt));
    if (this.cmap31 != null)
      return (int[])this.cmap31.get(new Integer(paramInt));
    if (this.cmap10 != null)
      return (int[])this.cmap10.get(new Integer(paramInt));
    return null;
  }

  public String getPostscriptFontName()
  {
    return this.fontName;
  }

  public String[] getCodePagesSupported()
  {
    long l1 = (this.os_2.ulCodePageRange2 << 32) + (this.os_2.ulCodePageRange1 & 0xFFFFFFFF);
    int i = 0;
    long l2 = 1L;
    for (int j = 0; j < 64; j++)
    {
      if (((l1 & l2) != 0L) && (codePages[j] != null))
        i++;
      l2 <<= 1;
    }
    String[] arrayOfString = new String[i];
    i = 0;
    l2 = 1L;
    for (int k = 0; k < 64; k++)
    {
      if (((l1 & l2) != 0L) && (codePages[k] != null))
        arrayOfString[(i++)] = codePages[k];
      l2 <<= 1;
    }
    return arrayOfString;
  }

  public String[][] getFullFontName()
  {
    return this.fullName;
  }

  public String[][] getAllNameEntries()
  {
    return this.allNameEntries;
  }

  public String[][] getFamilyFontName()
  {
    return this.familyName;
  }

  public boolean hasKernPairs()
  {
    return this.kerning.size() > 0;
  }

  public void setPostscriptFontName(String paramString)
  {
    this.fontName = paramString;
  }

  public boolean setKerning(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = getMetricsTT(paramInt1);
    if (arrayOfInt == null)
      return false;
    int i = arrayOfInt[0];
    arrayOfInt = getMetricsTT(paramInt2);
    if (arrayOfInt == null)
      return false;
    int j = arrayOfInt[0];
    this.kerning.put((i << 16) + j, paramInt3);
    return true;
  }

  protected int[] getRawCharBBox(int paramInt, String paramString)
  {
    HashMap localHashMap = null;
    if ((paramString == null) || (this.cmap31 == null))
      localHashMap = this.cmap10;
    else
      localHashMap = this.cmap31;
    if (localHashMap == null)
      return null;
    int[] arrayOfInt = (int[])localHashMap.get(new Integer(paramInt));
    if ((arrayOfInt == null) || (this.bboxes == null))
      return null;
    return this.bboxes[arrayOfInt[0]];
  }

  protected static class WindowsMetrics
  {
    short xAvgCharWidth;
    int usWeightClass;
    int usWidthClass;
    short fsType;
    short ySubscriptXSize;
    short ySubscriptYSize;
    short ySubscriptXOffset;
    short ySubscriptYOffset;
    short ySuperscriptXSize;
    short ySuperscriptYSize;
    short ySuperscriptXOffset;
    short ySuperscriptYOffset;
    short yStrikeoutSize;
    short yStrikeoutPosition;
    short sFamilyClass;
    byte[] panose = new byte[10];
    byte[] achVendID = new byte[4];
    int fsSelection;
    int usFirstCharIndex;
    int usLastCharIndex;
    short sTypoAscender;
    short sTypoDescender;
    short sTypoLineGap;
    int usWinAscent;
    int usWinDescent;
    int ulCodePageRange1;
    int ulCodePageRange2;
    int sCapHeight;
  }

  protected static class HorizontalHeader
  {
    short Ascender;
    short Descender;
    short LineGap;
    int advanceWidthMax;
    short minLeftSideBearing;
    short minRightSideBearing;
    short xMaxExtent;
    short caretSlopeRise;
    short caretSlopeRun;
    int numberOfHMetrics;
  }

  protected static class FontHeader
  {
    int flags;
    int unitsPerEm;
    short xMin;
    short yMin;
    short xMax;
    short yMax;
    int macStyle;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.TrueTypeFont
 * JD-Core Version:    0.6.0
 */