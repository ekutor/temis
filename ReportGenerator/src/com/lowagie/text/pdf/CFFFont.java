package com.lowagie.text.pdf;

import B;
import com.lowagie.text.ExceptionConverter;
import java.util.Iterator;
import java.util.LinkedList;

public class CFFFont
{
  static final String[] operatorNames = { "version", "Notice", "FullName", "FamilyName", "Weight", "FontBBox", "BlueValues", "OtherBlues", "FamilyBlues", "FamilyOtherBlues", "StdHW", "StdVW", "UNKNOWN_12", "UniqueID", "XUID", "charset", "Encoding", "CharStrings", "Private", "Subrs", "defaultWidthX", "nominalWidthX", "UNKNOWN_22", "UNKNOWN_23", "UNKNOWN_24", "UNKNOWN_25", "UNKNOWN_26", "UNKNOWN_27", "UNKNOWN_28", "UNKNOWN_29", "UNKNOWN_30", "UNKNOWN_31", "Copyright", "isFixedPitch", "ItalicAngle", "UnderlinePosition", "UnderlineThickness", "PaintType", "CharstringType", "FontMatrix", "StrokeWidth", "BlueScale", "BlueShift", "BlueFuzz", "StemSnapH", "StemSnapV", "ForceBold", "UNKNOWN_12_15", "UNKNOWN_12_16", "LanguageGroup", "ExpansionFactor", "initialRandomSeed", "SyntheticBase", "PostScript", "BaseFontName", "BaseFontBlend", "UNKNOWN_12_24", "UNKNOWN_12_25", "UNKNOWN_12_26", "UNKNOWN_12_27", "UNKNOWN_12_28", "UNKNOWN_12_29", "ROS", "CIDFontVersion", "CIDFontRevision", "CIDFontType", "CIDCount", "UIDBase", "FDArray", "FDSelect", "FontName" };
  static final String[] standardStrings = { ".notdef", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quoteright", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "quoteleft", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "exclamdown", "cent", "sterling", "fraction", "yen", "florin", "section", "currency", "quotesingle", "quotedblleft", "guillemotleft", "guilsinglleft", "guilsinglright", "fi", "fl", "endash", "dagger", "daggerdbl", "periodcentered", "paragraph", "bullet", "quotesinglbase", "quotedblbase", "quotedblright", "guillemotright", "ellipsis", "perthousand", "questiondown", "grave", "acute", "circumflex", "tilde", "macron", "breve", "dotaccent", "dieresis", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "emdash", "AE", "ordfeminine", "Lslash", "Oslash", "OE", "ordmasculine", "ae", "dotlessi", "lslash", "oslash", "oe", "germandbls", "onesuperior", "logicalnot", "mu", "trademark", "Eth", "onehalf", "plusminus", "Thorn", "onequarter", "divide", "brokenbar", "degree", "thorn", "threequarters", "twosuperior", "registered", "minus", "eth", "multiply", "threesuperior", "copyright", "Aacute", "Acircumflex", "Adieresis", "Agrave", "Aring", "Atilde", "Ccedilla", "Eacute", "Ecircumflex", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Ntilde", "Oacute", "Ocircumflex", "Odieresis", "Ograve", "Otilde", "Scaron", "Uacute", "Ucircumflex", "Udieresis", "Ugrave", "Yacute", "Ydieresis", "Zcaron", "aacute", "acircumflex", "adieresis", "agrave", "aring", "atilde", "ccedilla", "eacute", "ecircumflex", "edieresis", "egrave", "iacute", "icircumflex", "idieresis", "igrave", "ntilde", "oacute", "ocircumflex", "odieresis", "ograve", "otilde", "scaron", "uacute", "ucircumflex", "udieresis", "ugrave", "yacute", "ydieresis", "zcaron", "exclamsmall", "Hungarumlautsmall", "dollaroldstyle", "dollarsuperior", "ampersandsmall", "Acutesmall", "parenleftsuperior", "parenrightsuperior", "twodotenleader", "onedotenleader", "zerooldstyle", "oneoldstyle", "twooldstyle", "threeoldstyle", "fouroldstyle", "fiveoldstyle", "sixoldstyle", "sevenoldstyle", "eightoldstyle", "nineoldstyle", "commasuperior", "threequartersemdash", "periodsuperior", "questionsmall", "asuperior", "bsuperior", "centsuperior", "dsuperior", "esuperior", "isuperior", "lsuperior", "msuperior", "nsuperior", "osuperior", "rsuperior", "ssuperior", "tsuperior", "ff", "ffi", "ffl", "parenleftinferior", "parenrightinferior", "Circumflexsmall", "hyphensuperior", "Gravesmall", "Asmall", "Bsmall", "Csmall", "Dsmall", "Esmall", "Fsmall", "Gsmall", "Hsmall", "Ismall", "Jsmall", "Ksmall", "Lsmall", "Msmall", "Nsmall", "Osmall", "Psmall", "Qsmall", "Rsmall", "Ssmall", "Tsmall", "Usmall", "Vsmall", "Wsmall", "Xsmall", "Ysmall", "Zsmall", "colonmonetary", "onefitted", "rupiah", "Tildesmall", "exclamdownsmall", "centoldstyle", "Lslashsmall", "Scaronsmall", "Zcaronsmall", "Dieresissmall", "Brevesmall", "Caronsmall", "Dotaccentsmall", "Macronsmall", "figuredash", "hypheninferior", "Ogoneksmall", "Ringsmall", "Cedillasmall", "questiondownsmall", "oneeighth", "threeeighths", "fiveeighths", "seveneighths", "onethird", "twothirds", "zerosuperior", "foursuperior", "fivesuperior", "sixsuperior", "sevensuperior", "eightsuperior", "ninesuperior", "zeroinferior", "oneinferior", "twoinferior", "threeinferior", "fourinferior", "fiveinferior", "sixinferior", "seveninferior", "eightinferior", "nineinferior", "centinferior", "dollarinferior", "periodinferior", "commainferior", "Agravesmall", "Aacutesmall", "Acircumflexsmall", "Atildesmall", "Adieresissmall", "Aringsmall", "AEsmall", "Ccedillasmall", "Egravesmall", "Eacutesmall", "Ecircumflexsmall", "Edieresissmall", "Igravesmall", "Iacutesmall", "Icircumflexsmall", "Idieresissmall", "Ethsmall", "Ntildesmall", "Ogravesmall", "Oacutesmall", "Ocircumflexsmall", "Otildesmall", "Odieresissmall", "OEsmall", "Oslashsmall", "Ugravesmall", "Uacutesmall", "Ucircumflexsmall", "Udieresissmall", "Yacutesmall", "Thornsmall", "Ydieresissmall", "001.000", "001.001", "001.002", "001.003", "Black", "Bold", "Book", "Light", "Medium", "Regular", "Roman", "Semibold" };
  int nextIndexOffset;
  protected String key;
  protected Object[] args = new Object[48];
  protected int arg_count = 0;
  protected RandomAccessFileOrArray buf;
  private int offSize;
  protected int nameIndexOffset;
  protected int topdictIndexOffset;
  protected int stringIndexOffset;
  protected int gsubrIndexOffset;
  protected int[] nameOffsets;
  protected int[] topdictOffsets;
  protected int[] stringOffsets;
  protected int[] gsubrOffsets;
  protected Font[] fonts;

  public String getString(char paramChar)
  {
    if (paramChar < standardStrings.length)
      return standardStrings[paramChar];
    if (paramChar >= standardStrings.length + (this.stringOffsets.length - 1))
      return null;
    int i = paramChar - standardStrings.length;
    int j = getPosition();
    seek(this.stringOffsets[i]);
    StringBuffer localStringBuffer = new StringBuffer();
    for (int k = this.stringOffsets[i]; k < this.stringOffsets[(i + 1)]; k++)
      localStringBuffer.append(getCard8());
    seek(j);
    return localStringBuffer.toString();
  }

  char getCard8()
  {
    try
    {
      int i = this.buf.readByte();
      return (char)(i & 0xFF);
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  char getCard16()
  {
    try
    {
      return this.buf.readChar();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  int getOffset(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++)
    {
      i *= 256;
      i += getCard8();
    }
    return i;
  }

  void seek(int paramInt)
  {
    try
    {
      this.buf.seek(paramInt);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  short getShort()
  {
    try
    {
      return this.buf.readShort();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  int getInt()
  {
    try
    {
      return this.buf.readInt();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  int getPosition()
  {
    try
    {
      return this.buf.getFilePointer();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  int[] getIndex(int paramInt)
  {
    seek(paramInt);
    int i = getCard16();
    int[] arrayOfInt = new int[i + 1];
    if (i == 0)
    {
      arrayOfInt[0] = -1;
      paramInt += 2;
      return arrayOfInt;
    }
    int j = getCard8();
    for (int k = 0; k <= i; k++)
      arrayOfInt[k] = (paramInt + 2 + 1 + (i + 1) * j - 1 + getOffset(j));
    return arrayOfInt;
  }

  protected void getDictItem()
  {
    for (int i = 0; i < this.arg_count; i++)
      this.args[i] = null;
    this.arg_count = 0;
    this.key = null;
    i = 0;
    while (i == 0)
    {
      int j = getCard8();
      int k;
      if (j == 29)
      {
        k = getInt();
        this.args[this.arg_count] = new Integer(k);
        this.arg_count += 1;
        continue;
      }
      if (j == 28)
      {
        k = getShort();
        this.args[this.arg_count] = new Integer(k);
        this.arg_count += 1;
        continue;
      }
      if ((j >= 32) && (j <= 246))
      {
        k = (byte)(j - 139);
        this.args[this.arg_count] = new Integer(k);
        this.arg_count += 1;
        continue;
      }
      int m;
      if ((j >= 247) && (j <= 250))
      {
        k = getCard8();
        m = (short)((j - 247) * 256 + k + 108);
        this.args[this.arg_count] = new Integer(m);
        this.arg_count += 1;
        continue;
      }
      if ((j >= 251) && (j <= 254))
      {
        k = getCard8();
        m = (short)(-(j - 251) * 256 - k - 108);
        this.args[this.arg_count] = new Integer(m);
        this.arg_count += 1;
        continue;
      }
      if (j == 30)
      {
        String str = "";
        m = 0;
        int n = 0;
        int i1 = 0;
        int i2 = 0;
        while (m == 0)
        {
          if (i1 == 0)
          {
            n = getCard8();
            i1 = 2;
          }
          if (i1 == 1)
          {
            i2 = n / 16;
            i1 = (byte)(i1 - 1);
          }
          if (i1 == 2)
          {
            i2 = n % 16;
            i1 = (byte)(i1 - 1);
          }
          switch (i2)
          {
          case 10:
            str = str + ".";
            break;
          case 11:
            str = str + "E";
            break;
          case 12:
            str = str + "E-";
            break;
          case 14:
            str = str + "-";
            break;
          case 15:
            m = 1;
            break;
          case 13:
          default:
            if ((i2 >= 0) && (i2 <= 9))
            {
              str = str + String.valueOf(i2);
              continue;
            }
            str = str + "<NIBBLE ERROR: " + i2 + '>';
            m = 1;
          }
        }
        this.args[this.arg_count] = str;
        this.arg_count += 1;
        continue;
      }
      if (j > 21)
        continue;
      i = 1;
      if (j != 12)
      {
        this.key = operatorNames[j];
        continue;
      }
      this.key = operatorNames[(' ' + getCard8())];
    }
  }

  protected RangeItem getEntireIndexRange(int paramInt)
  {
    seek(paramInt);
    int i = getCard16();
    if (i == 0)
      return new RangeItem(this.buf, paramInt, 2);
    int j = getCard8();
    seek(paramInt + 2 + 1 + i * j);
    int k = getOffset(j) - 1;
    return new RangeItem(this.buf, paramInt, 3 + (i + 1) * j + k);
  }

  public byte[] getCID(String paramString)
  {
    for (int i = 0; (i < this.fonts.length) && (!paramString.equals(this.fonts[i].name)); i++);
    if (i == this.fonts.length)
      return null;
    LinkedList localLinkedList = new LinkedList();
    seek(0);
    int j = getCard8();
    int k = getCard8();
    int m = getCard8();
    int n = getCard8();
    this.nextIndexOffset = m;
    localLinkedList.addLast(new RangeItem(this.buf, 0, m));
    int i1 = -1;
    int i2 = -1;
    if (!this.fonts[i].isCID)
    {
      seek(this.fonts[i].charstringsOffset);
      i1 = getCard16();
      seek(this.stringIndexOffset);
      i2 = getCard16() + standardStrings.length;
    }
    localLinkedList.addLast(new UInt16Item('\001'));
    localLinkedList.addLast(new UInt8Item('\001'));
    localLinkedList.addLast(new UInt8Item('\001'));
    localLinkedList.addLast(new UInt8Item((char)(1 + this.fonts[i].name.length())));
    localLinkedList.addLast(new StringItem(this.fonts[i].name));
    localLinkedList.addLast(new UInt16Item('\001'));
    localLinkedList.addLast(new UInt8Item('\002'));
    localLinkedList.addLast(new UInt16Item('\001'));
    IndexOffsetItem localIndexOffsetItem = new IndexOffsetItem(2);
    localLinkedList.addLast(localIndexOffsetItem);
    IndexBaseItem localIndexBaseItem = new IndexBaseItem();
    localLinkedList.addLast(localIndexBaseItem);
    DictOffsetItem localDictOffsetItem1 = new DictOffsetItem();
    DictOffsetItem localDictOffsetItem2 = new DictOffsetItem();
    DictOffsetItem localDictOffsetItem3 = new DictOffsetItem();
    DictOffsetItem localDictOffsetItem4 = new DictOffsetItem();
    if (!this.fonts[i].isCID)
    {
      localLinkedList.addLast(new DictNumberItem(i2));
      localLinkedList.addLast(new DictNumberItem(i2 + 1));
      localLinkedList.addLast(new DictNumberItem(0));
      localLinkedList.addLast(new UInt8Item('\f'));
      localLinkedList.addLast(new UInt8Item('\036'));
      localLinkedList.addLast(new DictNumberItem(i1));
      localLinkedList.addLast(new UInt8Item('\f'));
      localLinkedList.addLast(new UInt8Item('"'));
    }
    localLinkedList.addLast(localDictOffsetItem3);
    localLinkedList.addLast(new UInt8Item('\f'));
    localLinkedList.addLast(new UInt8Item('$'));
    localLinkedList.addLast(localDictOffsetItem4);
    localLinkedList.addLast(new UInt8Item('\f'));
    localLinkedList.addLast(new UInt8Item('%'));
    localLinkedList.addLast(localDictOffsetItem1);
    localLinkedList.addLast(new UInt8Item('\017'));
    localLinkedList.addLast(localDictOffsetItem2);
    localLinkedList.addLast(new UInt8Item('\021'));
    seek(this.topdictOffsets[i]);
    while (getPosition() < this.topdictOffsets[(i + 1)])
    {
      int i3 = getPosition();
      getDictItem();
      int i4 = getPosition();
      if ((this.key == "Encoding") || (this.key == "Private") || (this.key == "FDSelect") || (this.key == "FDArray") || (this.key == "charset") || (this.key == "CharStrings"))
        continue;
      localLinkedList.add(new RangeItem(this.buf, i3, i4 - i3));
    }
    localLinkedList.addLast(new IndexMarkerItem(localIndexOffsetItem, localIndexBaseItem));
    if (this.fonts[i].isCID)
    {
      localLinkedList.addLast(getEntireIndexRange(this.stringIndexOffset));
    }
    else
    {
      localObject1 = this.fonts[i].name + "-OneRange";
      if (((String)localObject1).length() > 127)
        localObject1 = ((String)localObject1).substring(0, 127);
      localObject2 = "AdobeIdentity" + (String)localObject1;
      int i5 = this.stringOffsets[(this.stringOffsets.length - 1)] - this.stringOffsets[0];
      int i7 = this.stringOffsets[0] - 1;
      int i8;
      if (i5 + ((String)localObject2).length() <= 255)
        i8 = 1;
      else if (i5 + ((String)localObject2).length() <= 65535)
        i8 = 2;
      else if (i5 + ((String)localObject2).length() <= 16777215)
        i8 = 3;
      else
        i8 = 4;
      localLinkedList.addLast(new UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
      localLinkedList.addLast(new UInt8Item((char)i8));
      for (int i9 = 0; i9 < this.stringOffsets.length; i9++)
        localLinkedList.addLast(new IndexOffsetItem(i8, this.stringOffsets[i9] - i7));
      i9 = this.stringOffsets[(this.stringOffsets.length - 1)] - i7;
      i9 += "Adobe".length();
      localLinkedList.addLast(new IndexOffsetItem(i8, i9));
      i9 += "Identity".length();
      localLinkedList.addLast(new IndexOffsetItem(i8, i9));
      i9 += ((String)localObject1).length();
      localLinkedList.addLast(new IndexOffsetItem(i8, i9));
      localLinkedList.addLast(new RangeItem(this.buf, this.stringOffsets[0], i5));
      localLinkedList.addLast(new StringItem((String)localObject2));
    }
    localLinkedList.addLast(getEntireIndexRange(this.gsubrIndexOffset));
    Object localObject3;
    if (!this.fonts[i].isCID)
    {
      localLinkedList.addLast(new MarkerItem(localDictOffsetItem4));
      localLinkedList.addLast(new UInt8Item('\003'));
      localLinkedList.addLast(new UInt16Item('\001'));
      localLinkedList.addLast(new UInt16Item('\000'));
      localLinkedList.addLast(new UInt8Item('\000'));
      localLinkedList.addLast(new UInt16Item((char)i1));
      localLinkedList.addLast(new MarkerItem(localDictOffsetItem1));
      localLinkedList.addLast(new UInt8Item('\002'));
      localLinkedList.addLast(new UInt16Item('\001'));
      localLinkedList.addLast(new UInt16Item((char)(i1 - 1)));
      localLinkedList.addLast(new MarkerItem(localDictOffsetItem3));
      localLinkedList.addLast(new UInt16Item('\001'));
      localLinkedList.addLast(new UInt8Item('\001'));
      localLinkedList.addLast(new UInt8Item('\001'));
      localObject1 = new IndexOffsetItem(1);
      localLinkedList.addLast(localObject1);
      localObject2 = new IndexBaseItem();
      localLinkedList.addLast(localObject2);
      localLinkedList.addLast(new DictNumberItem(this.fonts[i].privateLength));
      localObject3 = new DictOffsetItem();
      localLinkedList.addLast(localObject3);
      localLinkedList.addLast(new UInt8Item('\022'));
      localLinkedList.addLast(new IndexMarkerItem((OffsetItem)localObject1, (IndexBaseItem)localObject2));
      localLinkedList.addLast(new MarkerItem((OffsetItem)localObject3));
      localLinkedList.addLast(new RangeItem(this.buf, this.fonts[i].privateOffset, this.fonts[i].privateLength));
      if (this.fonts[i].privateSubrs >= 0)
        localLinkedList.addLast(getEntireIndexRange(this.fonts[i].privateSubrs));
    }
    localLinkedList.addLast(new MarkerItem(localDictOffsetItem2));
    localLinkedList.addLast(getEntireIndexRange(this.fonts[i].charstringsOffset));
    Object localObject1 = new int[1];
    localObject1[0] = 0;
    Object localObject2 = localLinkedList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Item)((Iterator)localObject2).next();
      ((Item)localObject3).increment(localObject1);
    }
    localObject2 = localLinkedList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Item)((Iterator)localObject2).next();
      ((Item)localObject3).xref();
    }
    int i6 = localObject1[0];
    byte[] arrayOfByte = new byte[i6];
    localObject2 = localLinkedList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Item localItem = (Item)((Iterator)localObject2).next();
      localItem.emit(arrayOfByte);
    }
    return (B)(B)(B)arrayOfByte;
  }

  public boolean isCID(String paramString)
  {
    for (int i = 0; i < this.fonts.length; i++)
      if (paramString.equals(this.fonts[i].name))
        return this.fonts[i].isCID;
    return false;
  }

  public boolean exists(String paramString)
  {
    for (int i = 0; i < this.fonts.length; i++)
      if (paramString.equals(this.fonts[i].name))
        return true;
    return false;
  }

  public String[] getNames()
  {
    String[] arrayOfString = new String[this.fonts.length];
    for (int i = 0; i < this.fonts.length; i++)
      arrayOfString[i] = this.fonts[i].name;
    return arrayOfString;
  }

  public CFFFont(RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    this.buf = paramRandomAccessFileOrArray;
    seek(0);
    int i = getCard8();
    int j = getCard8();
    int k = getCard8();
    this.offSize = getCard8();
    this.nameIndexOffset = k;
    this.nameOffsets = getIndex(this.nameIndexOffset);
    this.topdictIndexOffset = this.nameOffsets[(this.nameOffsets.length - 1)];
    this.topdictOffsets = getIndex(this.topdictIndexOffset);
    this.stringIndexOffset = this.topdictOffsets[(this.topdictOffsets.length - 1)];
    this.stringOffsets = getIndex(this.stringIndexOffset);
    this.gsubrIndexOffset = this.stringOffsets[(this.stringOffsets.length - 1)];
    this.gsubrOffsets = getIndex(this.gsubrIndexOffset);
    this.fonts = new Font[this.nameOffsets.length - 1];
    int n;
    for (int m = 0; m < this.nameOffsets.length - 1; m++)
    {
      this.fonts[m] = new Font();
      seek(this.nameOffsets[m]);
      this.fonts[m].name = "";
      for (n = this.nameOffsets[m]; n < this.nameOffsets[(m + 1)]; n++)
        this.fonts[m].name += getCard8();
    }
    for (m = 0; m < this.topdictOffsets.length - 1; m++)
    {
      seek(this.topdictOffsets[m]);
      while (getPosition() < this.topdictOffsets[(m + 1)])
      {
        getDictItem();
        if (this.key == "FullName")
        {
          this.fonts[m].fullName = getString((char)((Integer)this.args[0]).intValue());
          continue;
        }
        if (this.key == "ROS")
        {
          this.fonts[m].isCID = true;
          continue;
        }
        if (this.key == "Private")
        {
          this.fonts[m].privateLength = ((Integer)this.args[0]).intValue();
          this.fonts[m].privateOffset = ((Integer)this.args[1]).intValue();
          continue;
        }
        if (this.key == "charset")
        {
          this.fonts[m].charsetOffset = ((Integer)this.args[0]).intValue();
          continue;
        }
        if (this.key == "Encoding")
        {
          this.fonts[m].encodingOffset = ((Integer)this.args[0]).intValue();
          ReadEncoding(this.fonts[m].encodingOffset);
          continue;
        }
        if (this.key == "CharStrings")
        {
          this.fonts[m].charstringsOffset = ((Integer)this.args[0]).intValue();
          n = getPosition();
          this.fonts[m].charstringsOffsets = getIndex(this.fonts[m].charstringsOffset);
          seek(n);
          continue;
        }
        if (this.key == "FDArray")
        {
          this.fonts[m].fdarrayOffset = ((Integer)this.args[0]).intValue();
          continue;
        }
        if (this.key == "FDSelect")
        {
          this.fonts[m].fdselectOffset = ((Integer)this.args[0]).intValue();
          continue;
        }
        if (this.key != "CharstringType")
          continue;
        this.fonts[m].CharstringType = ((Integer)this.args[0]).intValue();
      }
      if (this.fonts[m].privateOffset >= 0)
      {
        seek(this.fonts[m].privateOffset);
        while (getPosition() < this.fonts[m].privateOffset + this.fonts[m].privateLength)
        {
          getDictItem();
          if (this.key != "Subrs")
            continue;
          this.fonts[m].privateSubrs = (((Integer)this.args[0]).intValue() + this.fonts[m].privateOffset);
        }
      }
      if (this.fonts[m].fdarrayOffset < 0)
        continue;
      int[] arrayOfInt = getIndex(this.fonts[m].fdarrayOffset);
      this.fonts[m].fdprivateOffsets = new int[arrayOfInt.length - 1];
      this.fonts[m].fdprivateLengths = new int[arrayOfInt.length - 1];
      for (int i1 = 0; i1 < arrayOfInt.length - 1; i1++)
      {
        seek(arrayOfInt[i1]);
        while (getPosition() < arrayOfInt[(i1 + 1)])
          getDictItem();
        if (this.key != "Private")
          continue;
        this.fonts[m].fdprivateLengths[i1] = ((Integer)this.args[0]).intValue();
        this.fonts[m].fdprivateOffsets[i1] = ((Integer)this.args[1]).intValue();
      }
    }
  }

  void ReadEncoding(int paramInt)
  {
    seek(paramInt);
    int i = getCard8();
  }

  protected final class Font
  {
    public String name;
    public String fullName;
    public boolean isCID = false;
    public int privateOffset = -1;
    public int privateLength = -1;
    public int privateSubrs = -1;
    public int charstringsOffset = -1;
    public int encodingOffset = -1;
    public int charsetOffset = -1;
    public int fdarrayOffset = -1;
    public int fdselectOffset = -1;
    public int[] fdprivateOffsets;
    public int[] fdprivateLengths;
    public int[] fdprivateSubrs;
    public int nglyphs;
    public int nstrings;
    public int CharsetLength;
    public int[] charstringsOffsets;
    public int[] charset;
    public int[] FDSelect;
    public int FDSelectLength;
    public int FDSelectFormat;
    public int CharstringType = 2;
    public int FDArrayCount;
    public int FDArrayOffsize;
    public int[] FDArrayOffsets;
    public int[] PrivateSubrsOffset;
    public int[][] PrivateSubrsOffsetsArray;
    public int[] SubrsOffsets;

    protected Font()
    {
    }
  }

  protected static final class MarkerItem extends CFFFont.Item
  {
    CFFFont.OffsetItem p;

    public MarkerItem(CFFFont.OffsetItem paramOffsetItem)
    {
      this.p = paramOffsetItem;
    }

    public void xref()
    {
      this.p.set(this.myOffset);
    }
  }

  protected static final class DictNumberItem extends CFFFont.Item
  {
    public final int value;
    public int size = 5;

    public DictNumberItem(int paramInt)
    {
      this.value = paramInt;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += this.size;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      if (this.size == 5)
      {
        paramArrayOfByte[this.myOffset] = 29;
        paramArrayOfByte[(this.myOffset + 1)] = (byte)(this.value >>> 24 & 0xFF);
        paramArrayOfByte[(this.myOffset + 2)] = (byte)(this.value >>> 16 & 0xFF);
        paramArrayOfByte[(this.myOffset + 3)] = (byte)(this.value >>> 8 & 0xFF);
        paramArrayOfByte[(this.myOffset + 4)] = (byte)(this.value >>> 0 & 0xFF);
      }
    }
  }

  protected static final class StringItem extends CFFFont.Item
  {
    public String s;

    public StringItem(String paramString)
    {
      this.s = paramString;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += this.s.length();
    }

    public void emit(byte[] paramArrayOfByte)
    {
      for (int i = 0; i < this.s.length(); i++)
        paramArrayOfByte[(this.myOffset + i)] = (byte)(this.s.charAt(i) & 0xFF);
    }
  }

  protected static final class UInt8Item extends CFFFont.Item
  {
    public char value;

    public UInt8Item(char paramChar)
    {
      this.value = paramChar;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += 1;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      paramArrayOfByte[(this.myOffset + 0)] = (byte)(this.value >>> '\000' & 0xFF);
    }
  }

  protected static final class UInt16Item extends CFFFont.Item
  {
    public char value;

    public UInt16Item(char paramChar)
    {
      this.value = paramChar;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += 2;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      paramArrayOfByte[(this.myOffset + 0)] = (byte)(this.value >>> '\b' & 0xFF);
      paramArrayOfByte[(this.myOffset + 1)] = (byte)(this.value >>> '\000' & 0xFF);
    }
  }

  protected static final class UInt32Item extends CFFFont.Item
  {
    public int value;

    public UInt32Item(int paramInt)
    {
      this.value = paramInt;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += 4;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      paramArrayOfByte[(this.myOffset + 0)] = (byte)(this.value >>> 24 & 0xFF);
      paramArrayOfByte[(this.myOffset + 1)] = (byte)(this.value >>> 16 & 0xFF);
      paramArrayOfByte[(this.myOffset + 2)] = (byte)(this.value >>> 8 & 0xFF);
      paramArrayOfByte[(this.myOffset + 3)] = (byte)(this.value >>> 0 & 0xFF);
    }
  }

  protected static final class UInt24Item extends CFFFont.Item
  {
    public int value;

    public UInt24Item(int paramInt)
    {
      this.value = paramInt;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += 3;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      paramArrayOfByte[(this.myOffset + 0)] = (byte)(this.value >>> 16 & 0xFF);
      paramArrayOfByte[(this.myOffset + 1)] = (byte)(this.value >>> 8 & 0xFF);
      paramArrayOfByte[(this.myOffset + 2)] = (byte)(this.value >>> 0 & 0xFF);
    }
  }

  protected static final class DictOffsetItem extends CFFFont.OffsetItem
  {
    public final int size = 5;

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += this.size;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      if (this.size == 5)
      {
        paramArrayOfByte[this.myOffset] = 29;
        paramArrayOfByte[(this.myOffset + 1)] = (byte)(this.value >>> 24 & 0xFF);
        paramArrayOfByte[(this.myOffset + 2)] = (byte)(this.value >>> 16 & 0xFF);
        paramArrayOfByte[(this.myOffset + 3)] = (byte)(this.value >>> 8 & 0xFF);
        paramArrayOfByte[(this.myOffset + 4)] = (byte)(this.value >>> 0 & 0xFF);
      }
    }
  }

  protected static final class SubrMarkerItem extends CFFFont.Item
  {
    private CFFFont.OffsetItem offItem;
    private CFFFont.IndexBaseItem indexBase;

    public SubrMarkerItem(CFFFont.OffsetItem paramOffsetItem, CFFFont.IndexBaseItem paramIndexBaseItem)
    {
      this.offItem = paramOffsetItem;
      this.indexBase = paramIndexBaseItem;
    }

    public void xref()
    {
      this.offItem.set(this.myOffset - this.indexBase.myOffset);
    }
  }

  protected static final class IndexMarkerItem extends CFFFont.Item
  {
    private CFFFont.OffsetItem offItem;
    private CFFFont.IndexBaseItem indexBase;

    public IndexMarkerItem(CFFFont.OffsetItem paramOffsetItem, CFFFont.IndexBaseItem paramIndexBaseItem)
    {
      this.offItem = paramOffsetItem;
      this.indexBase = paramIndexBaseItem;
    }

    public void xref()
    {
      this.offItem.set(this.myOffset - this.indexBase.myOffset + 1);
    }
  }

  protected static final class IndexBaseItem extends CFFFont.Item
  {
  }

  protected static final class IndexOffsetItem extends CFFFont.OffsetItem
  {
    public final int size;

    public IndexOffsetItem(int paramInt1, int paramInt2)
    {
      this.size = paramInt1;
      this.value = paramInt2;
    }

    public IndexOffsetItem(int paramInt)
    {
      this.size = paramInt;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += this.size;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      int i = 0;
      switch (this.size)
      {
      case 4:
        paramArrayOfByte[(this.myOffset + i)] = (byte)(this.value >>> 24 & 0xFF);
        i++;
      case 3:
        paramArrayOfByte[(this.myOffset + i)] = (byte)(this.value >>> 16 & 0xFF);
        i++;
      case 2:
        paramArrayOfByte[(this.myOffset + i)] = (byte)(this.value >>> 8 & 0xFF);
        i++;
      case 1:
        paramArrayOfByte[(this.myOffset + i)] = (byte)(this.value >>> 0 & 0xFF);
        i++;
      }
    }
  }

  protected static final class RangeItem extends CFFFont.Item
  {
    public int offset;
    public int length;
    private RandomAccessFileOrArray buf;

    public RangeItem(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt1, int paramInt2)
    {
      this.offset = paramInt1;
      this.length = paramInt2;
      this.buf = paramRandomAccessFileOrArray;
    }

    public void increment(int[] paramArrayOfInt)
    {
      super.increment(paramArrayOfInt);
      paramArrayOfInt[0] += this.length;
    }

    public void emit(byte[] paramArrayOfByte)
    {
      try
      {
        this.buf.seek(this.offset);
        for (int i = this.myOffset; i < this.myOffset + this.length; i++)
          paramArrayOfByte[i] = this.buf.readByte();
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
    }
  }

  protected static abstract class OffsetItem extends CFFFont.Item
  {
    public int value;

    public void set(int paramInt)
    {
      this.value = paramInt;
    }
  }

  protected static abstract class Item
  {
    protected int myOffset = -1;

    public void increment(int[] paramArrayOfInt)
    {
      this.myOffset = paramArrayOfInt[0];
    }

    public void emit(byte[] paramArrayOfByte)
    {
    }

    public void xref()
    {
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.CFFFont
 * JD-Core Version:    0.6.0
 */