package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.HashMap;

public class DocumentFont extends BaseFont
{
  private HashMap metrics = new HashMap();
  private String fontName;
  private PRIndirectReference refFont;
  private PdfDictionary font;
  private IntHashtable uni2byte = new IntHashtable();
  private IntHashtable diffmap;
  private float Ascender = 800.0F;
  private float CapHeight = 700.0F;
  private float Descender = -200.0F;
  private float ItalicAngle = 0.0F;
  private float llx = -50.0F;
  private float lly = -200.0F;
  private float urx = 100.0F;
  private float ury = 900.0F;
  private boolean isType0 = false;
  private BaseFont cjkMirror;
  private static String[] cjkNames = { "HeiseiMin-W3", "HeiseiKakuGo-W5", "STSong-Light", "MHei-Medium", "MSung-Light", "HYGoThic-Medium", "HYSMyeongJo-Medium", "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular" };
  private static String[] cjkEncs = { "UniJIS-UCS2-H", "UniJIS-UCS2-H", "UniGB-UCS2-H", "UniCNS-UCS2-H", "UniCNS-UCS2-H", "UniKS-UCS2-H", "UniKS-UCS2-H", "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H" };
  private static String[] cjkNames2 = { "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular" };
  private static String[] cjkEncs2 = { "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H", "UniCNS-UTF16-H", "UniGB-UTF16-H", "UniKS-UTF16-H", "UniJIS-UTF16-H" };
  private static final int[] stdEnc = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 8217, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 8216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 8220, 171, 8249, 8250, 64257, 64258, 0, 8211, 8224, 8225, 183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 0, 191, 0, 96, 180, 710, 732, 175, 728, 729, 168, 0, 730, 184, 0, 733, 731, 711, 8212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 198, 0, 170, 0, 0, 0, 0, 321, 216, 338, 186, 0, 0, 0, 0, 0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 339, 223, 0, 0, 0, 0 };

  DocumentFont(PRIndirectReference paramPRIndirectReference)
  {
    this.encoding = "";
    this.fontSpecific = false;
    this.refFont = paramPRIndirectReference;
    this.fontType = 4;
    this.font = ((PdfDictionary)PdfReader.getPdfObject(paramPRIndirectReference));
    this.fontName = PdfName.decodeName(this.font.getAsName(PdfName.BASEFONT).toString());
    PdfName localPdfName = this.font.getAsName(PdfName.SUBTYPE);
    if ((PdfName.TYPE1.equals(localPdfName)) || (PdfName.TRUETYPE.equals(localPdfName)))
    {
      doType1TT();
    }
    else
    {
      for (int i = 0; i < cjkNames.length; i++)
      {
        if (!this.fontName.startsWith(cjkNames[i]))
          continue;
        this.fontName = cjkNames[i];
        try
        {
          this.cjkMirror = BaseFont.createFont(this.fontName, cjkEncs[i], false);
        }
        catch (Exception localException1)
        {
          throw new ExceptionConverter(localException1);
        }
        return;
      }
      String str = PdfName.decodeName(this.font.getAsName(PdfName.ENCODING).toString());
      for (int j = 0; j < cjkEncs2.length; j++)
      {
        if (!str.startsWith(cjkEncs2[j]))
          continue;
        try
        {
          if (j > 3)
            j -= 4;
          this.cjkMirror = BaseFont.createFont(cjkNames2[j], cjkEncs2[j], false);
        }
        catch (Exception localException2)
        {
          throw new ExceptionConverter(localException2);
        }
        return;
      }
      if ((PdfName.TYPE0.equals(localPdfName)) && (str.equals("Identity-H")))
      {
        processType0(this.font);
        this.isType0 = true;
      }
    }
  }

  private void processType0(PdfDictionary paramPdfDictionary)
  {
    try
    {
      PdfObject localPdfObject = PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.TOUNICODE));
      PdfArray localPdfArray = (PdfArray)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.DESCENDANTFONTS));
      PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObjectRelease(localPdfArray.getPdfObject(0));
      PdfNumber localPdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.DW));
      int i = 1000;
      if (localPdfNumber != null)
        i = localPdfNumber.intValue();
      IntHashtable localIntHashtable = readWidths((PdfArray)PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.W)));
      PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.FONTDESCRIPTOR));
      fillFontDesc(localPdfDictionary2);
      if (localPdfObject != null)
        fillMetrics(PdfReader.getStreamBytes((PRStream)localPdfObject), localIntHashtable, i);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  private IntHashtable readWidths(PdfArray paramPdfArray)
  {
    IntHashtable localIntHashtable = new IntHashtable();
    if (paramPdfArray == null)
      return localIntHashtable;
    for (int i = 0; i < paramPdfArray.size(); i++)
    {
      int j = ((PdfNumber)PdfReader.getPdfObjectRelease(paramPdfArray.getPdfObject(i))).intValue();
      i++;
      PdfObject localPdfObject = PdfReader.getPdfObjectRelease(paramPdfArray.getPdfObject(i));
      if (localPdfObject.isArray())
      {
        PdfArray localPdfArray = (PdfArray)localPdfObject;
        for (m = 0; m < localPdfArray.size(); m++)
        {
          int n = ((PdfNumber)PdfReader.getPdfObjectRelease(localPdfArray.getPdfObject(m))).intValue();
          localIntHashtable.put(j++, n);
        }
      }
      int k = ((PdfNumber)localPdfObject).intValue();
      i++;
      int m = ((PdfNumber)PdfReader.getPdfObjectRelease(paramPdfArray.getPdfObject(i))).intValue();
      while (j <= k)
      {
        localIntHashtable.put(j, m);
        j++;
      }
    }
    return localIntHashtable;
  }

  private String decodeString(PdfString paramPdfString)
  {
    if (paramPdfString.isHexWriting())
      return PdfEncodings.convertToString(paramPdfString.getBytes(), "UnicodeBigUnmarked");
    return paramPdfString.toUnicodeString();
  }

  private void fillMetrics(byte[] paramArrayOfByte, IntHashtable paramIntHashtable, int paramInt)
  {
    try
    {
      PdfContentParser localPdfContentParser = new PdfContentParser(new PRTokeniser(paramArrayOfByte));
      PdfObject localPdfObject1 = null;
      PdfObject localPdfObject2 = null;
      while ((localPdfObject1 = localPdfContentParser.readPRObject()) != null)
      {
        if (localPdfObject1.type() == 200)
        {
          String str1;
          String str2;
          int k;
          int m;
          if (localPdfObject1.toString().equals("beginbfchar"))
          {
            i = ((PdfNumber)localPdfObject2).intValue();
            for (j = 0; j < i; j++)
            {
              str1 = decodeString((PdfString)localPdfContentParser.readPRObject());
              str2 = decodeString((PdfString)localPdfContentParser.readPRObject());
              if (str2.length() != 1)
                continue;
              k = str1.charAt(0);
              m = str2.charAt(str2.length() - 1);
              int n = paramInt;
              if (paramIntHashtable.containsKey(k))
                n = paramIntHashtable.get(k);
              this.metrics.put(new Integer(m), new int[] { k, n });
            }
            continue;
          }
          if (!localPdfObject1.toString().equals("beginbfrange"))
            continue;
          int i = ((PdfNumber)localPdfObject2).intValue();
          for (int j = 0; j < i; j++)
          {
            str1 = decodeString((PdfString)localPdfContentParser.readPRObject());
            str2 = decodeString((PdfString)localPdfContentParser.readPRObject());
            k = str1.charAt(0);
            m = str2.charAt(0);
            PdfObject localPdfObject3 = localPdfContentParser.readPRObject();
            if (localPdfObject3.isString())
            {
              localObject = decodeString((PdfString)localPdfObject3);
              if (((String)localObject).length() != 1)
                continue;
              for (i1 = ((String)localObject).charAt(((String)localObject).length() - 1); k <= m; i1++)
              {
                int i2 = paramInt;
                if (paramIntHashtable.containsKey(k))
                  i2 = paramIntHashtable.get(k);
                this.metrics.put(new Integer(i1), new int[] { k, i2 });
                k++;
              }
            }
            Object localObject = (PdfArray)localPdfObject3;
            int i1 = 0;
            while (i1 < ((PdfArray)localObject).size())
            {
              String str3 = decodeString(((PdfArray)localObject).getAsString(i1));
              if (str3.length() == 1)
              {
                int i3 = str3.charAt(str3.length() - 1);
                int i4 = paramInt;
                if (paramIntHashtable.containsKey(k))
                  i4 = paramIntHashtable.get(k);
                this.metrics.put(new Integer(i3), new int[] { k, i4 });
              }
              i1++;
              k++;
            }
          }
          continue;
        }
        localPdfObject2 = localPdfObject1;
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  private void doType1TT()
  {
    PdfObject localPdfObject1 = PdfReader.getPdfObject(this.font.get(PdfName.ENCODING));
    if (localPdfObject1 == null)
    {
      fillEncoding(null);
    }
    else if (localPdfObject1.isName())
    {
      fillEncoding((PdfName)localPdfObject1);
    }
    else
    {
      localObject1 = (PdfDictionary)localPdfObject1;
      localPdfObject1 = PdfReader.getPdfObject(((PdfDictionary)localObject1).get(PdfName.BASEENCODING));
      if (localPdfObject1 == null)
        fillEncoding(null);
      else
        fillEncoding((PdfName)localPdfObject1);
      localObject2 = ((PdfDictionary)localObject1).getAsArray(PdfName.DIFFERENCES);
      if (localObject2 != null)
      {
        this.diffmap = new IntHashtable();
        int i = 0;
        for (int j = 0; j < ((PdfArray)localObject2).size(); j++)
        {
          PdfObject localPdfObject2 = ((PdfArray)localObject2).getPdfObject(j);
          if (localPdfObject2.isNumber())
          {
            i = ((PdfNumber)localPdfObject2).intValue();
          }
          else
          {
            int[] arrayOfInt2 = GlyphList.nameToUnicode(PdfName.decodeName(((PdfName)localPdfObject2).toString()));
            if ((arrayOfInt2 != null) && (arrayOfInt2.length > 0))
            {
              this.uni2byte.put(arrayOfInt2[0], i);
              this.diffmap.put(arrayOfInt2[0], i);
            }
            i++;
          }
        }
      }
    }
    Object localObject1 = this.font.getAsArray(PdfName.WIDTHS);
    Object localObject2 = this.font.getAsNumber(PdfName.FIRSTCHAR);
    PdfNumber localPdfNumber = this.font.getAsNumber(PdfName.LASTCHAR);
    if (BuiltinFonts14.containsKey(this.fontName))
    {
      BaseFont localBaseFont;
      try
      {
        localBaseFont = BaseFont.createFont(this.fontName, "Cp1252", false);
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
      int[] arrayOfInt1 = this.uni2byte.toOrderedKeys();
      int i1;
      for (int n = 0; n < arrayOfInt1.length; n++)
      {
        i1 = this.uni2byte.get(arrayOfInt1[n]);
        this.widths[i1] = localBaseFont.getRawWidth(i1, GlyphList.unicodeToName(arrayOfInt1[n]));
      }
      if (this.diffmap != null)
      {
        arrayOfInt1 = this.diffmap.toOrderedKeys();
        for (n = 0; n < arrayOfInt1.length; n++)
        {
          i1 = this.diffmap.get(arrayOfInt1[n]);
          this.widths[i1] = localBaseFont.getRawWidth(i1, GlyphList.unicodeToName(arrayOfInt1[n]));
        }
        this.diffmap = null;
      }
      this.Ascender = localBaseFont.getFontDescriptor(1, 1000.0F);
      this.CapHeight = localBaseFont.getFontDescriptor(2, 1000.0F);
      this.Descender = localBaseFont.getFontDescriptor(3, 1000.0F);
      this.ItalicAngle = localBaseFont.getFontDescriptor(4, 1000.0F);
      this.llx = localBaseFont.getFontDescriptor(5, 1000.0F);
      this.lly = localBaseFont.getFontDescriptor(6, 1000.0F);
      this.urx = localBaseFont.getFontDescriptor(7, 1000.0F);
      this.ury = localBaseFont.getFontDescriptor(8, 1000.0F);
    }
    if ((localObject2 != null) && (localPdfNumber != null) && (localObject1 != null))
    {
      int k = ((PdfNumber)localObject2).intValue();
      for (int m = 0; m < ((PdfArray)localObject1).size(); m++)
        this.widths[(k + m)] = ((PdfArray)localObject1).getAsNumber(m).intValue();
    }
    fillFontDesc(this.font.getAsDict(PdfName.FONTDESCRIPTOR));
  }

  private void fillFontDesc(PdfDictionary paramPdfDictionary)
  {
    if (paramPdfDictionary == null)
      return;
    PdfNumber localPdfNumber = paramPdfDictionary.getAsNumber(PdfName.ASCENT);
    if (localPdfNumber != null)
      this.Ascender = localPdfNumber.floatValue();
    localPdfNumber = paramPdfDictionary.getAsNumber(PdfName.CAPHEIGHT);
    if (localPdfNumber != null)
      this.CapHeight = localPdfNumber.floatValue();
    localPdfNumber = paramPdfDictionary.getAsNumber(PdfName.DESCENT);
    if (localPdfNumber != null)
      this.Descender = localPdfNumber.floatValue();
    localPdfNumber = paramPdfDictionary.getAsNumber(PdfName.ITALICANGLE);
    if (localPdfNumber != null)
      this.ItalicAngle = localPdfNumber.floatValue();
    PdfArray localPdfArray = paramPdfDictionary.getAsArray(PdfName.FONTBBOX);
    if (localPdfArray != null)
    {
      this.llx = localPdfArray.getAsNumber(0).floatValue();
      this.lly = localPdfArray.getAsNumber(1).floatValue();
      this.urx = localPdfArray.getAsNumber(2).floatValue();
      this.ury = localPdfArray.getAsNumber(3).floatValue();
      float f;
      if (this.llx > this.urx)
      {
        f = this.llx;
        this.llx = this.urx;
        this.urx = f;
      }
      if (this.lly > this.ury)
      {
        f = this.lly;
        this.lly = this.ury;
        this.ury = f;
      }
    }
  }

  private void fillEncoding(PdfName paramPdfName)
  {
    char[] arrayOfChar;
    int k;
    if ((PdfName.MAC_ROMAN_ENCODING.equals(paramPdfName)) || (PdfName.WIN_ANSI_ENCODING.equals(paramPdfName)))
    {
      byte[] arrayOfByte = new byte[256];
      for (int j = 0; j < 256; j++)
        arrayOfByte[j] = (byte)j;
      String str1 = "Cp1252";
      if (PdfName.MAC_ROMAN_ENCODING.equals(paramPdfName))
        str1 = "MacRoman";
      String str2 = PdfEncodings.convertToString(arrayOfByte, str1);
      arrayOfChar = str2.toCharArray();
      k = 0;
    }
    while (k < 256)
    {
      this.uni2byte.put(arrayOfChar[k], k);
      k++;
      continue;
      for (int i = 0; i < 256; i++)
        this.uni2byte.put(stdEnc[i], i);
    }
  }

  public String[][] getFamilyFontName()
  {
    return getFullFontName();
  }

  public float getFontDescriptor(int paramInt, float paramFloat)
  {
    if (this.cjkMirror != null)
      return this.cjkMirror.getFontDescriptor(paramInt, paramFloat);
    switch (paramInt)
    {
    case 1:
    case 9:
      return this.Ascender * paramFloat / 1000.0F;
    case 2:
      return this.CapHeight * paramFloat / 1000.0F;
    case 3:
    case 10:
      return this.Descender * paramFloat / 1000.0F;
    case 4:
      return this.ItalicAngle;
    case 5:
      return this.llx * paramFloat / 1000.0F;
    case 6:
      return this.lly * paramFloat / 1000.0F;
    case 7:
      return this.urx * paramFloat / 1000.0F;
    case 8:
      return this.ury * paramFloat / 1000.0F;
    case 11:
      return 0.0F;
    case 12:
      return (this.urx - this.llx) * paramFloat / 1000.0F;
    }
    return 0.0F;
  }

  public String[][] getFullFontName()
  {
    return new String[][] { { "", "", "", this.fontName } };
  }

  public String[][] getAllNameEntries()
  {
    return new String[][] { { "4", "", "", "", this.fontName } };
  }

  public int getKerning(int paramInt1, int paramInt2)
  {
    return 0;
  }

  public String getPostscriptFontName()
  {
    return this.fontName;
  }

  int getRawWidth(int paramInt, String paramString)
  {
    return 0;
  }

  public boolean hasKernPairs()
  {
    return false;
  }

  void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException
  {
  }

  public PdfStream getFullFontStream()
  {
    return null;
  }

  public int getWidth(int paramInt)
  {
    if (this.cjkMirror != null)
      return this.cjkMirror.getWidth(paramInt);
    if (this.isType0)
    {
      int[] arrayOfInt = (int[])this.metrics.get(new Integer(paramInt));
      if (arrayOfInt != null)
        return arrayOfInt[1];
      return 0;
    }
    return super.getWidth(paramInt);
  }

  public int getWidth(String paramString)
  {
    if (this.cjkMirror != null)
      return this.cjkMirror.getWidth(paramString);
    if (this.isType0)
    {
      char[] arrayOfChar = paramString.toCharArray();
      int i = arrayOfChar.length;
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        int[] arrayOfInt = (int[])this.metrics.get(new Integer(arrayOfChar[k]));
        if (arrayOfInt == null)
          continue;
        j += arrayOfInt[1];
      }
      return j;
    }
    return super.getWidth(paramString);
  }

  byte[] convertToBytes(String paramString)
  {
    if (this.cjkMirror != null)
      return PdfEncodings.convertToBytes(paramString, "UnicodeBigUnmarked");
    if (this.isType0)
    {
      arrayOfChar = paramString.toCharArray();
      int i = arrayOfChar.length;
      byte[] arrayOfByte2 = new byte[i * 2];
      k = 0;
      for (int m = 0; m < i; m++)
      {
        int[] arrayOfInt = (int[])this.metrics.get(new Integer(arrayOfChar[m]));
        if (arrayOfInt == null)
          continue;
        int n = arrayOfInt[0];
        arrayOfByte2[(k++)] = (byte)(n / 256);
        arrayOfByte2[(k++)] = (byte)n;
      }
      if (k == arrayOfByte2.length)
        return arrayOfByte2;
      byte[] arrayOfByte4 = new byte[k];
      System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 0, k);
      return arrayOfByte4;
    }
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte1 = new byte[arrayOfChar.length];
    int j = 0;
    for (int k = 0; k < arrayOfChar.length; k++)
    {
      if (!this.uni2byte.containsKey(arrayOfChar[k]))
        continue;
      arrayOfByte1[(j++)] = (byte)this.uni2byte.get(arrayOfChar[k]);
    }
    if (j == arrayOfByte1.length)
      return arrayOfByte1;
    byte[] arrayOfByte3 = new byte[j];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, j);
    return arrayOfByte3;
  }

  byte[] convertToBytes(int paramInt)
  {
    if (this.cjkMirror != null)
      return PdfEncodings.convertToBytes((char)paramInt, "UnicodeBigUnmarked");
    if (this.isType0)
    {
      int[] arrayOfInt = (int[])this.metrics.get(new Integer(paramInt));
      if (arrayOfInt != null)
      {
        int i = arrayOfInt[0];
        return new byte[] { (byte)(i / 256), (byte)i };
      }
      return new byte[0];
    }
    if (this.uni2byte.containsKey(paramInt))
      return new byte[] { (byte)this.uni2byte.get(paramInt) };
    return new byte[0];
  }

  PdfIndirectReference getIndirectReference()
  {
    return this.refFont;
  }

  public boolean charExists(int paramInt)
  {
    if (this.cjkMirror != null)
      return this.cjkMirror.charExists(paramInt);
    if (this.isType0)
      return this.metrics.containsKey(new Integer(paramInt));
    return super.charExists(paramInt);
  }

  public void setPostscriptFontName(String paramString)
  {
  }

  public boolean setKerning(int paramInt1, int paramInt2, int paramInt3)
  {
    return false;
  }

  public int[] getCharBBox(int paramInt)
  {
    return null;
  }

  protected int[] getRawCharBBox(int paramInt, String paramString)
  {
    return null;
  }

  IntHashtable getUni2Byte()
  {
    return this.uni2byte;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.DocumentFont
 * JD-Core Version:    0.6.0
 */