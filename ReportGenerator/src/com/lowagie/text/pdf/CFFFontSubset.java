package com.lowagie.text.pdf;

import B;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class CFFFontSubset extends CFFFont
{
  static final String[] SubrsFunctions = { "RESERVED_0", "hstem", "RESERVED_2", "vstem", "vmoveto", "rlineto", "hlineto", "vlineto", "rrcurveto", "RESERVED_9", "callsubr", "return", "escape", "RESERVED_13", "endchar", "RESERVED_15", "RESERVED_16", "RESERVED_17", "hstemhm", "hintmask", "cntrmask", "rmoveto", "hmoveto", "vstemhm", "rcurveline", "rlinecurve", "vvcurveto", "hhcurveto", "shortint", "callgsubr", "vhcurveto", "hvcurveto" };
  static final String[] SubrsEscapeFuncs = { "RESERVED_0", "RESERVED_1", "RESERVED_2", "and", "or", "not", "RESERVED_6", "RESERVED_7", "RESERVED_8", "abs", "add", "sub", "div", "RESERVED_13", "neg", "eq", "RESERVED_16", "RESERVED_17", "drop", "RESERVED_19", "put", "get", "ifelse", "random", "mul", "RESERVED_25", "sqrt", "dup", "exch", "index", "roll", "RESERVED_31", "RESERVED_32", "RESERVED_33", "hflex", "flex", "hflex1", "flex1", "RESERVED_REST" };
  static final byte ENDCHAR_OP = 14;
  static final byte RETURN_OP = 11;
  HashMap GlyphsUsed;
  ArrayList glyphsInList;
  HashMap FDArrayUsed = new HashMap();
  HashMap[] hSubrsUsed;
  ArrayList[] lSubrsUsed;
  HashMap hGSubrsUsed = new HashMap();
  ArrayList lGSubrsUsed = new ArrayList();
  HashMap hSubrsUsedNonCID = new HashMap();
  ArrayList lSubrsUsedNonCID = new ArrayList();
  byte[][] NewLSubrsIndex;
  byte[] NewSubrsIndexNonCID;
  byte[] NewGSubrsIndex;
  byte[] NewCharStringsIndex;
  int GBias = 0;
  LinkedList OutputList;
  int NumOfHints = 0;

  public CFFFontSubset(RandomAccessFileOrArray paramRandomAccessFileOrArray, HashMap paramHashMap)
  {
    super(paramRandomAccessFileOrArray);
    this.GlyphsUsed = paramHashMap;
    this.glyphsInList = new ArrayList(paramHashMap.keySet());
    for (int i = 0; i < this.fonts.length; i++)
    {
      seek(this.fonts[i].charstringsOffset);
      this.fonts[i].nglyphs = getCard16();
      seek(this.stringIndexOffset);
      this.fonts[i].nstrings = (getCard16() + standardStrings.length);
      this.fonts[i].charstringsOffsets = getIndex(this.fonts[i].charstringsOffset);
      if (this.fonts[i].fdselectOffset >= 0)
      {
        readFDSelect(i);
        BuildFDArrayUsed(i);
      }
      if (this.fonts[i].isCID)
        ReadFDArray(i);
      this.fonts[i].CharsetLength = CountCharset(this.fonts[i].charsetOffset, this.fonts[i].nglyphs);
    }
  }

  int CountCharset(int paramInt1, int paramInt2)
  {
    int j = 0;
    seek(paramInt1);
    int i = getCard8();
    switch (i)
    {
    case 0:
      j = 1 + 2 * paramInt2;
      break;
    case 1:
      j = 1 + 3 * CountRange(paramInt2, 1);
      break;
    case 2:
      j = 1 + 4 * CountRange(paramInt2, 2);
      break;
    }
    return j;
  }

  int CountRange(int paramInt1, int paramInt2)
  {
    int i = 0;
    int k = 1;
    while (k < paramInt1)
    {
      i++;
      int j = getCard16();
      int m;
      if (paramInt2 == 1)
        m = getCard8();
      else
        m = getCard16();
      k += m + 1;
    }
    return i;
  }

  protected void readFDSelect(int paramInt)
  {
    int i = this.fonts[paramInt].nglyphs;
    int[] arrayOfInt = new int[i];
    seek(this.fonts[paramInt].fdselectOffset);
    this.fonts[paramInt].FDSelectFormat = getCard8();
    int j;
    switch (this.fonts[paramInt].FDSelectFormat)
    {
    case 0:
      for (j = 0; j < i; j++)
        arrayOfInt[j] = getCard8();
      this.fonts[paramInt].FDSelectLength = (this.fonts[paramInt].nglyphs + 1);
      break;
    case 3:
      j = getCard16();
      int k = 0;
      int m = getCard16();
      for (int n = 0; n < j; n++)
      {
        int i1 = getCard8();
        int i2 = getCard16();
        int i3 = i2 - m;
        for (int i4 = 0; i4 < i3; i4++)
        {
          arrayOfInt[k] = i1;
          k++;
        }
        m = i2;
      }
      this.fonts[paramInt].FDSelectLength = (3 + j * 3 + 2);
      break;
    }
    this.fonts[paramInt].FDSelect = arrayOfInt;
  }

  protected void BuildFDArrayUsed(int paramInt)
  {
    int[] arrayOfInt = this.fonts[paramInt].FDSelect;
    for (int i = 0; i < this.glyphsInList.size(); i++)
    {
      int j = ((Integer)this.glyphsInList.get(i)).intValue();
      int k = arrayOfInt[j];
      this.FDArrayUsed.put(new Integer(k), null);
    }
  }

  protected void ReadFDArray(int paramInt)
  {
    seek(this.fonts[paramInt].fdarrayOffset);
    this.fonts[paramInt].FDArrayCount = getCard16();
    this.fonts[paramInt].FDArrayOffsize = getCard8();
    if (this.fonts[paramInt].FDArrayOffsize < 4)
      this.fonts[paramInt].FDArrayOffsize += 1;
    this.fonts[paramInt].FDArrayOffsets = getIndex(this.fonts[paramInt].fdarrayOffset);
  }

  public byte[] Process(String paramString)
    throws IOException
  {
    try
    {
      this.buf.reOpen();
      for (int i = 0; (i < this.fonts.length) && (!paramString.equals(this.fonts[i].name)); i++);
      if (i == this.fonts.length)
      {
        arrayOfByte1 = null;
        return arrayOfByte1;
      }
      if (this.gsubrIndexOffset >= 0)
        this.GBias = CalcBias(this.gsubrIndexOffset, i);
      BuildNewCharString(i);
      BuildNewLGSubrs(i);
      byte[] arrayOfByte1 = BuildNewFile(i);
      byte[] arrayOfByte2 = arrayOfByte1;
      return arrayOfByte2;
    }
    finally
    {
      try
      {
        this.buf.close();
      }
      catch (Exception localException3)
      {
      }
    }
    throw localObject;
  }

  protected int CalcBias(int paramInt1, int paramInt2)
  {
    seek(paramInt1);
    int i = getCard16();
    if (this.fonts[paramInt2].CharstringType == 1)
      return 0;
    if (i < 1240)
      return 107;
    if (i < 33900)
      return 1131;
    return 32768;
  }

  protected void BuildNewCharString(int paramInt)
    throws IOException
  {
    this.NewCharStringsIndex = BuildNewIndex(this.fonts[paramInt].charstringsOffsets, this.GlyphsUsed, 14);
  }

  protected void BuildNewLGSubrs(int paramInt)
    throws IOException
  {
    if (this.fonts[paramInt].isCID)
    {
      this.hSubrsUsed = new HashMap[this.fonts[paramInt].fdprivateOffsets.length];
      this.lSubrsUsed = new ArrayList[this.fonts[paramInt].fdprivateOffsets.length];
      this.NewLSubrsIndex = new byte[this.fonts[paramInt].fdprivateOffsets.length][];
      this.fonts[paramInt].PrivateSubrsOffset = new int[this.fonts[paramInt].fdprivateOffsets.length];
      this.fonts[paramInt].PrivateSubrsOffsetsArray = new int[this.fonts[paramInt].fdprivateOffsets.length][];
      ArrayList localArrayList = new ArrayList(this.FDArrayUsed.keySet());
      for (int i = 0; i < localArrayList.size(); i++)
      {
        int j = ((Integer)localArrayList.get(i)).intValue();
        this.hSubrsUsed[j] = new HashMap();
        this.lSubrsUsed[j] = new ArrayList();
        BuildFDSubrsOffsets(paramInt, j);
        if (this.fonts[paramInt].PrivateSubrsOffset[j] < 0)
          continue;
        BuildSubrUsed(paramInt, j, this.fonts[paramInt].PrivateSubrsOffset[j], this.fonts[paramInt].PrivateSubrsOffsetsArray[j], this.hSubrsUsed[j], this.lSubrsUsed[j]);
        this.NewLSubrsIndex[j] = BuildNewIndex(this.fonts[paramInt].PrivateSubrsOffsetsArray[j], this.hSubrsUsed[j], 11);
      }
    }
    if (this.fonts[paramInt].privateSubrs >= 0)
    {
      this.fonts[paramInt].SubrsOffsets = getIndex(this.fonts[paramInt].privateSubrs);
      BuildSubrUsed(paramInt, -1, this.fonts[paramInt].privateSubrs, this.fonts[paramInt].SubrsOffsets, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID);
    }
    BuildGSubrsUsed(paramInt);
    if (this.fonts[paramInt].privateSubrs >= 0)
      this.NewSubrsIndexNonCID = BuildNewIndex(this.fonts[paramInt].SubrsOffsets, this.hSubrsUsedNonCID, 11);
    this.NewGSubrsIndex = BuildNewIndex(this.gsubrOffsets, this.hGSubrsUsed, 11);
  }

  protected void BuildFDSubrsOffsets(int paramInt1, int paramInt2)
  {
    this.fonts[paramInt1].PrivateSubrsOffset[paramInt2] = -1;
    seek(this.fonts[paramInt1].fdprivateOffsets[paramInt2]);
    while (getPosition() < this.fonts[paramInt1].fdprivateOffsets[paramInt2] + this.fonts[paramInt1].fdprivateLengths[paramInt2])
    {
      getDictItem();
      if (this.key != "Subrs")
        continue;
      this.fonts[paramInt1].PrivateSubrsOffset[paramInt2] = (((Integer)this.args[0]).intValue() + this.fonts[paramInt1].fdprivateOffsets[paramInt2]);
    }
    if (this.fonts[paramInt1].PrivateSubrsOffset[paramInt2] >= 0)
      this.fonts[paramInt1].PrivateSubrsOffsetsArray[paramInt2] = getIndex(this.fonts[paramInt1].PrivateSubrsOffset[paramInt2]);
  }

  protected void BuildSubrUsed(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, HashMap paramHashMap, ArrayList paramArrayList)
  {
    int i = CalcBias(paramInt3, paramInt1);
    int k;
    int m;
    int n;
    for (int j = 0; j < this.glyphsInList.size(); j++)
    {
      k = ((Integer)this.glyphsInList.get(j)).intValue();
      m = this.fonts[paramInt1].charstringsOffsets[k];
      n = this.fonts[paramInt1].charstringsOffsets[(k + 1)];
      if (paramInt2 >= 0)
      {
        EmptyStack();
        this.NumOfHints = 0;
        int i1 = this.fonts[paramInt1].FDSelect[k];
        if (i1 != paramInt2)
          continue;
        ReadASubr(m, n, this.GBias, i, paramHashMap, paramArrayList, paramArrayOfInt);
      }
      else
      {
        ReadASubr(m, n, this.GBias, i, paramHashMap, paramArrayList, paramArrayOfInt);
      }
    }
    for (j = 0; j < paramArrayList.size(); j++)
    {
      k = ((Integer)paramArrayList.get(j)).intValue();
      if ((k >= paramArrayOfInt.length - 1) || (k < 0))
        continue;
      m = paramArrayOfInt[k];
      n = paramArrayOfInt[(k + 1)];
      ReadASubr(m, n, this.GBias, i, paramHashMap, paramArrayList, paramArrayOfInt);
    }
  }

  protected void BuildGSubrsUsed(int paramInt)
  {
    int i = 0;
    int j = 0;
    if (this.fonts[paramInt].privateSubrs >= 0)
    {
      i = CalcBias(this.fonts[paramInt].privateSubrs, paramInt);
      j = this.lSubrsUsedNonCID.size();
    }
    for (int k = 0; k < this.lGSubrsUsed.size(); k++)
    {
      int m = ((Integer)this.lGSubrsUsed.get(k)).intValue();
      if ((m >= this.gsubrOffsets.length - 1) || (m < 0))
        continue;
      int n = this.gsubrOffsets[m];
      int i1 = this.gsubrOffsets[(m + 1)];
      if (this.fonts[paramInt].isCID)
      {
        ReadASubr(n, i1, this.GBias, 0, this.hGSubrsUsed, this.lGSubrsUsed, null);
      }
      else
      {
        ReadASubr(n, i1, this.GBias, i, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[paramInt].SubrsOffsets);
        if (j >= this.lSubrsUsedNonCID.size())
          continue;
        for (int i2 = j; i2 < this.lSubrsUsedNonCID.size(); i2++)
        {
          int i3 = ((Integer)this.lSubrsUsedNonCID.get(i2)).intValue();
          if ((i3 >= this.fonts[paramInt].SubrsOffsets.length - 1) || (i3 < 0))
            continue;
          int i4 = this.fonts[paramInt].SubrsOffsets[i3];
          int i5 = this.fonts[paramInt].SubrsOffsets[(i3 + 1)];
          ReadASubr(i4, i5, this.GBias, i, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[paramInt].SubrsOffsets);
        }
        j = this.lSubrsUsedNonCID.size();
      }
    }
  }

  protected void ReadASubr(int paramInt1, int paramInt2, int paramInt3, int paramInt4, HashMap paramHashMap, ArrayList paramArrayList, int[] paramArrayOfInt)
  {
    EmptyStack();
    this.NumOfHints = 0;
    seek(paramInt1);
    while (getPosition() < paramInt2)
    {
      ReadCommand();
      int i = getPosition();
      Object localObject = null;
      if (this.arg_count > 0)
        localObject = this.args[(this.arg_count - 1)];
      int j = this.arg_count;
      HandelStack();
      if (this.key == "callsubr")
      {
        if (j <= 0)
          continue;
        k = ((Integer)localObject).intValue() + paramInt4;
        if (!paramHashMap.containsKey(new Integer(k)))
        {
          paramHashMap.put(new Integer(k), null);
          paramArrayList.add(new Integer(k));
        }
        CalcHints(paramArrayOfInt[k], paramArrayOfInt[(k + 1)], paramInt4, paramInt3, paramArrayOfInt);
        seek(i);
        continue;
      }
      if (this.key == "callgsubr")
      {
        if (j <= 0)
          continue;
        k = ((Integer)localObject).intValue() + paramInt3;
        if (!this.hGSubrsUsed.containsKey(new Integer(k)))
        {
          this.hGSubrsUsed.put(new Integer(k), null);
          this.lGSubrsUsed.add(new Integer(k));
        }
        CalcHints(this.gsubrOffsets[k], this.gsubrOffsets[(k + 1)], paramInt4, paramInt3, paramArrayOfInt);
        seek(i);
        continue;
      }
      if ((this.key == "hstem") || (this.key == "vstem") || (this.key == "hstemhm") || (this.key == "vstemhm"))
      {
        this.NumOfHints += j / 2;
        continue;
      }
      if ((this.key != "hintmask") && (this.key != "cntrmask"))
        continue;
      int k = this.NumOfHints / 8;
      if ((this.NumOfHints % 8 != 0) || (k == 0))
        k++;
      for (int m = 0; m < k; m++)
        getCard8();
    }
  }

  protected void HandelStack()
  {
    int i = StackOpp();
    if (i < 2)
    {
      if (i == 1)
      {
        PushStack();
      }
      else
      {
        i *= -1;
        for (int j = 0; j < i; j++)
          PopStack();
      }
    }
    else
      EmptyStack();
  }

  protected int StackOpp()
  {
    if (this.key == "ifelse")
      return -3;
    if ((this.key == "roll") || (this.key == "put"))
      return -2;
    if ((this.key == "callsubr") || (this.key == "callgsubr") || (this.key == "add") || (this.key == "sub") || (this.key == "div") || (this.key == "mul") || (this.key == "drop") || (this.key == "and") || (this.key == "or") || (this.key == "eq"))
      return -1;
    if ((this.key == "abs") || (this.key == "neg") || (this.key == "sqrt") || (this.key == "exch") || (this.key == "index") || (this.key == "get") || (this.key == "not") || (this.key == "return"))
      return 0;
    if ((this.key == "random") || (this.key == "dup"))
      return 1;
    return 2;
  }

  protected void EmptyStack()
  {
    for (int i = 0; i < this.arg_count; i++)
      this.args[i] = null;
    this.arg_count = 0;
  }

  protected void PopStack()
  {
    if (this.arg_count > 0)
    {
      this.args[(this.arg_count - 1)] = null;
      this.arg_count -= 1;
    }
  }

  protected void PushStack()
  {
    this.arg_count += 1;
  }

  protected void ReadCommand()
  {
    this.key = null;
    int i = 0;
    while (i == 0)
    {
      int j = getCard8();
      int k;
      int m;
      if (j == 28)
      {
        k = getCard8();
        m = getCard8();
        this.args[this.arg_count] = new Integer(k << 8 | m);
        this.arg_count += 1;
        continue;
      }
      if ((j >= 32) && (j <= 246))
      {
        this.args[this.arg_count] = new Integer(j - 139);
        this.arg_count += 1;
        continue;
      }
      if ((j >= 247) && (j <= 250))
      {
        k = getCard8();
        this.args[this.arg_count] = new Integer((j - 247) * 256 + k + 108);
        this.arg_count += 1;
        continue;
      }
      if ((j >= 251) && (j <= 254))
      {
        k = getCard8();
        this.args[this.arg_count] = new Integer(-(j - 251) * 256 - k - 108);
        this.arg_count += 1;
        continue;
      }
      if (j == 255)
      {
        k = getCard8();
        m = getCard8();
        int n = getCard8();
        int i1 = getCard8();
        this.args[this.arg_count] = new Integer(k << 24 | m << 16 | n << 8 | i1);
        this.arg_count += 1;
        continue;
      }
      if ((j > 31) || (j == 28))
        continue;
      i = 1;
      if (j == 12)
      {
        k = getCard8();
        if (k > SubrsEscapeFuncs.length - 1)
          k = SubrsEscapeFuncs.length - 1;
        this.key = SubrsEscapeFuncs[k];
        continue;
      }
      this.key = SubrsFunctions[j];
    }
  }

  protected int CalcHints(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    seek(paramInt1);
    while (getPosition() < paramInt2)
    {
      ReadCommand();
      int i = getPosition();
      Object localObject = null;
      if (this.arg_count > 0)
        localObject = this.args[(this.arg_count - 1)];
      int j = this.arg_count;
      HandelStack();
      if (this.key == "callsubr")
      {
        if (j <= 0)
          continue;
        k = ((Integer)localObject).intValue() + paramInt3;
        CalcHints(paramArrayOfInt[k], paramArrayOfInt[(k + 1)], paramInt3, paramInt4, paramArrayOfInt);
        seek(i);
        continue;
      }
      if (this.key == "callgsubr")
      {
        if (j <= 0)
          continue;
        k = ((Integer)localObject).intValue() + paramInt4;
        CalcHints(this.gsubrOffsets[k], this.gsubrOffsets[(k + 1)], paramInt3, paramInt4, paramArrayOfInt);
        seek(i);
        continue;
      }
      if ((this.key == "hstem") || (this.key == "vstem") || (this.key == "hstemhm") || (this.key == "vstemhm"))
      {
        this.NumOfHints += j / 2;
        continue;
      }
      if ((this.key != "hintmask") && (this.key != "cntrmask"))
        continue;
      int k = this.NumOfHints / 8;
      if ((this.NumOfHints % 8 != 0) || (k == 0))
        k++;
      for (int m = 0; m < k; m++)
        getCard8();
    }
    return this.NumOfHints;
  }

  protected byte[] BuildNewIndex(int[] paramArrayOfInt, HashMap paramHashMap, byte paramByte)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (int k = 0; k < paramArrayOfInt.length; k++)
    {
      arrayOfInt[k] = j;
      if (paramHashMap.containsKey(new Integer(k)))
        j += paramArrayOfInt[(k + 1)] - paramArrayOfInt[k];
      else
        i++;
    }
    byte[] arrayOfByte = new byte[j + i];
    int m = 0;
    for (int n = 0; n < paramArrayOfInt.length - 1; n++)
    {
      int i1 = arrayOfInt[n];
      int i2 = arrayOfInt[(n + 1)];
      arrayOfInt[n] = (i1 + m);
      if (i1 != i2)
      {
        this.buf.seek(paramArrayOfInt[n]);
        this.buf.readFully(arrayOfByte, i1 + m, i2 - i1);
      }
      else
      {
        arrayOfByte[(i1 + m)] = paramByte;
        m++;
      }
    }
    arrayOfInt[(paramArrayOfInt.length - 1)] += m;
    return AssembleIndex(arrayOfInt, arrayOfByte);
  }

  protected byte[] AssembleIndex(int[] paramArrayOfInt, byte[] paramArrayOfByte)
  {
    int i = (char)(paramArrayOfInt.length - 1);
    int j = paramArrayOfInt[(paramArrayOfInt.length - 1)];
    int k;
    if (j <= 255)
      k = 1;
    else if (j <= 65535)
      k = 2;
    else if (j <= 16777215)
      k = 3;
    else
      k = 4;
    byte[] arrayOfByte = new byte[3 + k * (i + 1) + paramArrayOfByte.length];
    int m = 0;
    arrayOfByte[(m++)] = (byte)(i >>> 8 & 0xFF);
    arrayOfByte[(m++)] = (byte)(i >>> 0 & 0xFF);
    arrayOfByte[(m++)] = k;
    for (int n = 0; n < paramArrayOfInt.length; n++)
    {
      int i1 = paramArrayOfInt[n] - paramArrayOfInt[0] + 1;
      switch (k)
      {
      case 4:
        arrayOfByte[(m++)] = (byte)(i1 >>> 24 & 0xFF);
      case 3:
        arrayOfByte[(m++)] = (byte)(i1 >>> 16 & 0xFF);
      case 2:
        arrayOfByte[(m++)] = (byte)(i1 >>> 8 & 0xFF);
      case 1:
        arrayOfByte[(m++)] = (byte)(i1 >>> 0 & 0xFF);
      }
    }
    for (n = 0; n < paramArrayOfByte.length; n++)
      arrayOfByte[(m++)] = paramArrayOfByte[n];
    return arrayOfByte;
  }

  protected byte[] BuildNewFile(int paramInt)
  {
    this.OutputList = new LinkedList();
    CopyHeader();
    BuildIndexHeader(1, 1, 1);
    this.OutputList.addLast(new CFFFont.UInt8Item((char)(1 + this.fonts[paramInt].name.length())));
    this.OutputList.addLast(new CFFFont.StringItem(this.fonts[paramInt].name));
    BuildIndexHeader(1, 2, 1);
    CFFFont.IndexOffsetItem localIndexOffsetItem = new CFFFont.IndexOffsetItem(2);
    this.OutputList.addLast(localIndexOffsetItem);
    CFFFont.IndexBaseItem localIndexBaseItem = new CFFFont.IndexBaseItem();
    this.OutputList.addLast(localIndexBaseItem);
    CFFFont.DictOffsetItem localDictOffsetItem1 = new CFFFont.DictOffsetItem();
    CFFFont.DictOffsetItem localDictOffsetItem2 = new CFFFont.DictOffsetItem();
    CFFFont.DictOffsetItem localDictOffsetItem3 = new CFFFont.DictOffsetItem();
    CFFFont.DictOffsetItem localDictOffsetItem4 = new CFFFont.DictOffsetItem();
    CFFFont.DictOffsetItem localDictOffsetItem5 = new CFFFont.DictOffsetItem();
    if (!this.fonts[paramInt].isCID)
    {
      this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[paramInt].nstrings));
      this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[paramInt].nstrings + 1));
      this.OutputList.addLast(new CFFFont.DictNumberItem(0));
      this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
      this.OutputList.addLast(new CFFFont.UInt8Item('\036'));
      this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[paramInt].nglyphs));
      this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
      this.OutputList.addLast(new CFFFont.UInt8Item('"'));
    }
    seek(this.topdictOffsets[paramInt]);
    while (getPosition() < this.topdictOffsets[(paramInt + 1)])
    {
      int i = getPosition();
      getDictItem();
      int j = getPosition();
      if ((this.key == "Encoding") || (this.key == "Private") || (this.key == "FDSelect") || (this.key == "FDArray") || (this.key == "charset") || (this.key == "CharStrings"))
        continue;
      this.OutputList.add(new CFFFont.RangeItem(this.buf, i, j - i));
    }
    CreateKeys(localDictOffsetItem3, localDictOffsetItem4, localDictOffsetItem1, localDictOffsetItem2);
    this.OutputList.addLast(new CFFFont.IndexMarkerItem(localIndexOffsetItem, localIndexBaseItem));
    if (this.fonts[paramInt].isCID)
      this.OutputList.addLast(getEntireIndexRange(this.stringIndexOffset));
    else
      CreateNewStringIndex(paramInt);
    this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewGSubrsIndex), 0, this.NewGSubrsIndex.length));
    if (this.fonts[paramInt].isCID)
    {
      this.OutputList.addLast(new CFFFont.MarkerItem(localDictOffsetItem4));
      if (this.fonts[paramInt].fdselectOffset >= 0)
        this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.fonts[paramInt].fdselectOffset, this.fonts[paramInt].FDSelectLength));
      else
        CreateFDSelect(localDictOffsetItem4, this.fonts[paramInt].nglyphs);
      this.OutputList.addLast(new CFFFont.MarkerItem(localDictOffsetItem1));
      this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.fonts[paramInt].charsetOffset, this.fonts[paramInt].CharsetLength));
      if (this.fonts[paramInt].fdarrayOffset >= 0)
      {
        this.OutputList.addLast(new CFFFont.MarkerItem(localDictOffsetItem3));
        Reconstruct(paramInt);
      }
      else
      {
        CreateFDArray(localDictOffsetItem3, localDictOffsetItem5, paramInt);
      }
    }
    else
    {
      CreateFDSelect(localDictOffsetItem4, this.fonts[paramInt].nglyphs);
      CreateCharset(localDictOffsetItem1, this.fonts[paramInt].nglyphs);
      CreateFDArray(localDictOffsetItem3, localDictOffsetItem5, paramInt);
    }
    if (this.fonts[paramInt].privateOffset >= 0)
    {
      localObject1 = new CFFFont.IndexBaseItem();
      this.OutputList.addLast(localObject1);
      this.OutputList.addLast(new CFFFont.MarkerItem(localDictOffsetItem5));
      localObject2 = new CFFFont.DictOffsetItem();
      CreateNonCIDPrivate(paramInt, (CFFFont.OffsetItem)localObject2);
      CreateNonCIDSubrs(paramInt, (CFFFont.IndexBaseItem)localObject1, (CFFFont.OffsetItem)localObject2);
    }
    this.OutputList.addLast(new CFFFont.MarkerItem(localDictOffsetItem2));
    this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewCharStringsIndex), 0, this.NewCharStringsIndex.length));
    Object localObject1 = new int[1];
    localObject1[0] = 0;
    Object localObject2 = this.OutputList.iterator();
    CFFFont.Item localItem1;
    while (((Iterator)localObject2).hasNext())
    {
      localItem1 = (CFFFont.Item)((Iterator)localObject2).next();
      localItem1.increment(localObject1);
    }
    localObject2 = this.OutputList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localItem1 = (CFFFont.Item)((Iterator)localObject2).next();
      localItem1.xref();
    }
    int k = localObject1[0];
    byte[] arrayOfByte = new byte[k];
    localObject2 = this.OutputList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      CFFFont.Item localItem2 = (CFFFont.Item)((Iterator)localObject2).next();
      localItem2.emit(arrayOfByte);
    }
    return (B)(B)arrayOfByte;
  }

  protected void CopyHeader()
  {
    seek(0);
    int i = getCard8();
    int j = getCard8();
    int k = getCard8();
    int m = getCard8();
    this.nextIndexOffset = k;
    this.OutputList.addLast(new CFFFont.RangeItem(this.buf, 0, k));
  }

  protected void BuildIndexHeader(int paramInt1, int paramInt2, int paramInt3)
  {
    this.OutputList.addLast(new CFFFont.UInt16Item((char)paramInt1));
    this.OutputList.addLast(new CFFFont.UInt8Item((char)paramInt2));
    switch (paramInt2)
    {
    case 1:
      this.OutputList.addLast(new CFFFont.UInt8Item((char)paramInt3));
      break;
    case 2:
      this.OutputList.addLast(new CFFFont.UInt16Item((char)paramInt3));
      break;
    case 3:
      this.OutputList.addLast(new CFFFont.UInt24Item((char)paramInt3));
      break;
    case 4:
      this.OutputList.addLast(new CFFFont.UInt32Item((char)paramInt3));
      break;
    }
  }

  protected void CreateKeys(CFFFont.OffsetItem paramOffsetItem1, CFFFont.OffsetItem paramOffsetItem2, CFFFont.OffsetItem paramOffsetItem3, CFFFont.OffsetItem paramOffsetItem4)
  {
    this.OutputList.addLast(paramOffsetItem1);
    this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
    this.OutputList.addLast(new CFFFont.UInt8Item('$'));
    this.OutputList.addLast(paramOffsetItem2);
    this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
    this.OutputList.addLast(new CFFFont.UInt8Item('%'));
    this.OutputList.addLast(paramOffsetItem3);
    this.OutputList.addLast(new CFFFont.UInt8Item('\017'));
    this.OutputList.addLast(paramOffsetItem4);
    this.OutputList.addLast(new CFFFont.UInt8Item('\021'));
  }

  protected void CreateNewStringIndex(int paramInt)
  {
    String str1 = this.fonts[paramInt].name + "-OneRange";
    if (str1.length() > 127)
      str1 = str1.substring(0, 127);
    String str2 = "AdobeIdentity" + str1;
    int i = this.stringOffsets[(this.stringOffsets.length - 1)] - this.stringOffsets[0];
    int j = this.stringOffsets[0] - 1;
    int k;
    if (i + str2.length() <= 255)
      k = 1;
    else if (i + str2.length() <= 65535)
      k = 2;
    else if (i + str2.length() <= 16777215)
      k = 3;
    else
      k = 4;
    this.OutputList.addLast(new CFFFont.UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
    this.OutputList.addLast(new CFFFont.UInt8Item((char)k));
    for (int m = 0; m < this.stringOffsets.length; m++)
      this.OutputList.addLast(new CFFFont.IndexOffsetItem(k, this.stringOffsets[m] - j));
    m = this.stringOffsets[(this.stringOffsets.length - 1)] - j;
    m += "Adobe".length();
    this.OutputList.addLast(new CFFFont.IndexOffsetItem(k, m));
    m += "Identity".length();
    this.OutputList.addLast(new CFFFont.IndexOffsetItem(k, m));
    m += str1.length();
    this.OutputList.addLast(new CFFFont.IndexOffsetItem(k, m));
    this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.stringOffsets[0], i));
    this.OutputList.addLast(new CFFFont.StringItem(str2));
  }

  protected void CreateFDSelect(CFFFont.OffsetItem paramOffsetItem, int paramInt)
  {
    this.OutputList.addLast(new CFFFont.MarkerItem(paramOffsetItem));
    this.OutputList.addLast(new CFFFont.UInt8Item('\003'));
    this.OutputList.addLast(new CFFFont.UInt16Item('\001'));
    this.OutputList.addLast(new CFFFont.UInt16Item('\000'));
    this.OutputList.addLast(new CFFFont.UInt8Item('\000'));
    this.OutputList.addLast(new CFFFont.UInt16Item((char)paramInt));
  }

  protected void CreateCharset(CFFFont.OffsetItem paramOffsetItem, int paramInt)
  {
    this.OutputList.addLast(new CFFFont.MarkerItem(paramOffsetItem));
    this.OutputList.addLast(new CFFFont.UInt8Item('\002'));
    this.OutputList.addLast(new CFFFont.UInt16Item('\001'));
    this.OutputList.addLast(new CFFFont.UInt16Item((char)(paramInt - 1)));
  }

  protected void CreateFDArray(CFFFont.OffsetItem paramOffsetItem1, CFFFont.OffsetItem paramOffsetItem2, int paramInt)
  {
    this.OutputList.addLast(new CFFFont.MarkerItem(paramOffsetItem1));
    BuildIndexHeader(1, 1, 1);
    CFFFont.IndexOffsetItem localIndexOffsetItem = new CFFFont.IndexOffsetItem(1);
    this.OutputList.addLast(localIndexOffsetItem);
    CFFFont.IndexBaseItem localIndexBaseItem = new CFFFont.IndexBaseItem();
    this.OutputList.addLast(localIndexBaseItem);
    int i = this.fonts[paramInt].privateLength;
    int j = CalcSubrOffsetSize(this.fonts[paramInt].privateOffset, this.fonts[paramInt].privateLength);
    if (j != 0)
      i += 5 - j;
    this.OutputList.addLast(new CFFFont.DictNumberItem(i));
    this.OutputList.addLast(paramOffsetItem2);
    this.OutputList.addLast(new CFFFont.UInt8Item('\022'));
    this.OutputList.addLast(new CFFFont.IndexMarkerItem(localIndexOffsetItem, localIndexBaseItem));
  }

  void Reconstruct(int paramInt)
  {
    CFFFont.DictOffsetItem[] arrayOfDictOffsetItem1 = new CFFFont.DictOffsetItem[this.fonts[paramInt].FDArrayOffsets.length - 1];
    CFFFont.IndexBaseItem[] arrayOfIndexBaseItem = new CFFFont.IndexBaseItem[this.fonts[paramInt].fdprivateOffsets.length];
    CFFFont.DictOffsetItem[] arrayOfDictOffsetItem2 = new CFFFont.DictOffsetItem[this.fonts[paramInt].fdprivateOffsets.length];
    ReconstructFDArray(paramInt, arrayOfDictOffsetItem1);
    ReconstructPrivateDict(paramInt, arrayOfDictOffsetItem1, arrayOfIndexBaseItem, arrayOfDictOffsetItem2);
    ReconstructPrivateSubrs(paramInt, arrayOfIndexBaseItem, arrayOfDictOffsetItem2);
  }

  void ReconstructFDArray(int paramInt, CFFFont.OffsetItem[] paramArrayOfOffsetItem)
  {
    BuildIndexHeader(this.fonts[paramInt].FDArrayCount, this.fonts[paramInt].FDArrayOffsize, 1);
    CFFFont.IndexOffsetItem[] arrayOfIndexOffsetItem = new CFFFont.IndexOffsetItem[this.fonts[paramInt].FDArrayOffsets.length - 1];
    for (int i = 0; i < this.fonts[paramInt].FDArrayOffsets.length - 1; i++)
    {
      arrayOfIndexOffsetItem[i] = new CFFFont.IndexOffsetItem(this.fonts[paramInt].FDArrayOffsize);
      this.OutputList.addLast(arrayOfIndexOffsetItem[i]);
    }
    CFFFont.IndexBaseItem localIndexBaseItem = new CFFFont.IndexBaseItem();
    this.OutputList.addLast(localIndexBaseItem);
    for (int j = 0; j < this.fonts[paramInt].FDArrayOffsets.length - 1; j++)
    {
      if (this.FDArrayUsed.containsKey(new Integer(j)))
      {
        seek(this.fonts[paramInt].FDArrayOffsets[j]);
        while (getPosition() < this.fonts[paramInt].FDArrayOffsets[(j + 1)])
        {
          int k = getPosition();
          getDictItem();
          int m = getPosition();
          if (this.key == "Private")
          {
            int n = ((Integer)this.args[0]).intValue();
            int i1 = CalcSubrOffsetSize(this.fonts[paramInt].fdprivateOffsets[j], this.fonts[paramInt].fdprivateLengths[j]);
            if (i1 != 0)
              n += 5 - i1;
            this.OutputList.addLast(new CFFFont.DictNumberItem(n));
            paramArrayOfOffsetItem[j] = new CFFFont.DictOffsetItem();
            this.OutputList.addLast(paramArrayOfOffsetItem[j]);
            this.OutputList.addLast(new CFFFont.UInt8Item('\022'));
            seek(m);
            continue;
          }
          this.OutputList.addLast(new CFFFont.RangeItem(this.buf, k, m - k));
        }
      }
      this.OutputList.addLast(new CFFFont.IndexMarkerItem(arrayOfIndexOffsetItem[j], localIndexBaseItem));
    }
  }

  void ReconstructPrivateDict(int paramInt, CFFFont.OffsetItem[] paramArrayOfOffsetItem1, CFFFont.IndexBaseItem[] paramArrayOfIndexBaseItem, CFFFont.OffsetItem[] paramArrayOfOffsetItem2)
  {
    for (int i = 0; i < this.fonts[paramInt].fdprivateOffsets.length; i++)
    {
      if (!this.FDArrayUsed.containsKey(new Integer(i)))
        continue;
      this.OutputList.addLast(new CFFFont.MarkerItem(paramArrayOfOffsetItem1[i]));
      paramArrayOfIndexBaseItem[i] = new CFFFont.IndexBaseItem();
      this.OutputList.addLast(paramArrayOfIndexBaseItem[i]);
      seek(this.fonts[paramInt].fdprivateOffsets[i]);
      while (getPosition() < this.fonts[paramInt].fdprivateOffsets[i] + this.fonts[paramInt].fdprivateLengths[i])
      {
        int j = getPosition();
        getDictItem();
        int k = getPosition();
        if (this.key == "Subrs")
        {
          paramArrayOfOffsetItem2[i] = new CFFFont.DictOffsetItem();
          this.OutputList.addLast(paramArrayOfOffsetItem2[i]);
          this.OutputList.addLast(new CFFFont.UInt8Item('\023'));
          continue;
        }
        this.OutputList.addLast(new CFFFont.RangeItem(this.buf, j, k - j));
      }
    }
  }

  void ReconstructPrivateSubrs(int paramInt, CFFFont.IndexBaseItem[] paramArrayOfIndexBaseItem, CFFFont.OffsetItem[] paramArrayOfOffsetItem)
  {
    for (int i = 0; i < this.fonts[paramInt].fdprivateLengths.length; i++)
    {
      if ((paramArrayOfOffsetItem[i] == null) || (this.fonts[paramInt].PrivateSubrsOffset[i] < 0))
        continue;
      this.OutputList.addLast(new CFFFont.SubrMarkerItem(paramArrayOfOffsetItem[i], paramArrayOfIndexBaseItem[i]));
      this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewLSubrsIndex[i]), 0, this.NewLSubrsIndex[i].length));
    }
  }

  int CalcSubrOffsetSize(int paramInt1, int paramInt2)
  {
    int i = 0;
    seek(paramInt1);
    while (getPosition() < paramInt1 + paramInt2)
    {
      int j = getPosition();
      getDictItem();
      int k = getPosition();
      if (this.key != "Subrs")
        continue;
      i = k - j - 1;
    }
    return i;
  }

  protected int countEntireIndexRange(int paramInt)
  {
    seek(paramInt);
    int i = getCard16();
    if (i == 0)
      return 2;
    int j = getCard8();
    seek(paramInt + 2 + 1 + i * j);
    int k = getOffset(j) - 1;
    return 3 + (i + 1) * j + k;
  }

  void CreateNonCIDPrivate(int paramInt, CFFFont.OffsetItem paramOffsetItem)
  {
    seek(this.fonts[paramInt].privateOffset);
    while (getPosition() < this.fonts[paramInt].privateOffset + this.fonts[paramInt].privateLength)
    {
      int i = getPosition();
      getDictItem();
      int j = getPosition();
      if (this.key == "Subrs")
      {
        this.OutputList.addLast(paramOffsetItem);
        this.OutputList.addLast(new CFFFont.UInt8Item('\023'));
        continue;
      }
      this.OutputList.addLast(new CFFFont.RangeItem(this.buf, i, j - i));
    }
  }

  void CreateNonCIDSubrs(int paramInt, CFFFont.IndexBaseItem paramIndexBaseItem, CFFFont.OffsetItem paramOffsetItem)
  {
    this.OutputList.addLast(new CFFFont.SubrMarkerItem(paramOffsetItem, paramIndexBaseItem));
    this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewSubrsIndexNonCID), 0, this.NewSubrsIndexNonCID.length));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.CFFFontSubset
 * JD-Core Version:    0.6.0
 */