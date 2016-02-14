package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Utilities;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

class TrueTypeFontUnicode extends TrueTypeFont
  implements Comparator
{
  boolean vertical = false;
  private static final byte[] rotbits = { -128, 64, 32, 16, 8, 4, 2, 1 };

  TrueTypeFontUnicode(String paramString1, String paramString2, boolean paramBoolean1, byte[] paramArrayOfByte, boolean paramBoolean2)
    throws DocumentException, IOException
  {
    String str1 = getBaseName(paramString1);
    String str2 = getTTCName(str1);
    if (str1.length() < paramString1.length())
      this.style = paramString1.substring(str1.length());
    this.encoding = paramString2;
    this.embedded = paramBoolean1;
    this.fileName = str2;
    this.ttcIndex = "";
    if (str2.length() < str1.length())
      this.ttcIndex = str1.substring(str2.length() + 1);
    this.fontType = 3;
    if (((this.fileName.toLowerCase().endsWith(".ttf")) || (this.fileName.toLowerCase().endsWith(".otf")) || (this.fileName.toLowerCase().endsWith(".ttc"))) && ((paramString2.equals("Identity-H")) || (paramString2.equals("Identity-V"))) && (paramBoolean1))
    {
      process(paramArrayOfByte, paramBoolean2);
      if (this.os_2.fsType == 2)
        throw new DocumentException(this.fileName + this.style + " cannot be embedded due to licensing restrictions.");
      if (((this.cmap31 == null) && (!this.fontSpecific)) || ((this.cmap10 == null) && (this.fontSpecific)))
        this.directTextToByte = true;
      if (this.fontSpecific)
      {
        this.fontSpecific = false;
        String str3 = this.encoding;
        this.encoding = "";
        createEncoding();
        this.encoding = str3;
        this.fontSpecific = true;
      }
    }
    else
    {
      throw new DocumentException(this.fileName + " " + this.style + " is not a TTF font file.");
    }
    this.vertical = paramString2.endsWith("V");
  }

  public int getWidth(int paramInt)
  {
    if (this.vertical)
      return 1000;
    if (this.fontSpecific)
    {
      if (((paramInt & 0xFF00) == 0) || ((paramInt & 0xFF00) == 61440))
        return getRawWidth(paramInt & 0xFF, null);
      return 0;
    }
    return getRawWidth(paramInt, this.encoding);
  }

  public int getWidth(String paramString)
  {
    if (this.vertical)
      return paramString.length() * 1000;
    int i = 0;
    if (this.fontSpecific)
    {
      char[] arrayOfChar = paramString.toCharArray();
      k = arrayOfChar.length;
      for (int m = 0; m < k; m++)
      {
        int n = arrayOfChar[m];
        if (((n & 0xFF00) != 0) && ((n & 0xFF00) != 61440))
          continue;
        i += getRawWidth(n & 0xFF, null);
      }
    }
    int j = paramString.length();
    for (int k = 0; k < j; k++)
      if (Utilities.isSurrogatePair(paramString, k))
      {
        i += getRawWidth(Utilities.convertToUtf32(paramString, k), this.encoding);
        k++;
      }
      else
      {
        i += getRawWidth(paramString.charAt(k), this.encoding);
      }
    return i;
  }

  private PdfStream getToUnicode(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length == 0)
      return null;
    StringBuffer localStringBuffer = new StringBuffer("/CIDInit /ProcSet findresource begin\n12 dict begin\nbegincmap\n/CIDSystemInfo\n<< /Registry (TTX+0)\n/Ordering (T42UV)\n/Supplement 0\n>> def\n/CMapName /TTX+0 def\n/CMapType 2 def\n1 begincodespacerange\n<0000><FFFF>\nendcodespacerange\n");
    int i = 0;
    for (int j = 0; j < paramArrayOfObject.length; j++)
    {
      if (i == 0)
      {
        if (j != 0)
          localStringBuffer.append("endbfrange\n");
        i = Math.min(100, paramArrayOfObject.length - j);
        localStringBuffer.append(i).append(" beginbfrange\n");
      }
      i--;
      localObject = (int[])paramArrayOfObject[j];
      String str2 = toHex(localObject[0]);
      localStringBuffer.append(str2).append(str2).append(toHex(localObject[2])).append('\n');
    }
    localStringBuffer.append("endbfrange\nendcmap\nCMapName currentdict /CMap defineresource pop\nend end\n");
    String str1 = localStringBuffer.toString();
    Object localObject = new PdfStream(PdfEncodings.convertToBytes(str1, null));
    ((PdfStream)localObject).flateCompress(this.compressionLevel);
    return (PdfStream)localObject;
  }

  private static String toHex4(int paramInt)
  {
    String str = "0000" + Integer.toHexString(paramInt);
    return str.substring(str.length() - 4);
  }

  static String toHex(int paramInt)
  {
    if (paramInt < 65536)
      return "<" + toHex4(paramInt) + ">";
    paramInt -= 65536;
    int i = paramInt / 1024 + 55296;
    int j = paramInt % 1024 + 56320;
    return "[<" + toHex4(i) + toHex4(j) + ">]";
  }

  private PdfDictionary getCIDFontType2(PdfIndirectReference paramPdfIndirectReference, String paramString, Object[] paramArrayOfObject)
  {
    PdfDictionary localPdfDictionary1 = new PdfDictionary(PdfName.FONT);
    if (this.cff)
    {
      localPdfDictionary1.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
      localPdfDictionary1.put(PdfName.BASEFONT, new PdfName(paramString + this.fontName + "-" + this.encoding));
    }
    else
    {
      localPdfDictionary1.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE2);
      localPdfDictionary1.put(PdfName.BASEFONT, new PdfName(paramString + this.fontName));
    }
    localPdfDictionary1.put(PdfName.FONTDESCRIPTOR, paramPdfIndirectReference);
    if (!this.cff)
      localPdfDictionary1.put(PdfName.CIDTOGIDMAP, PdfName.IDENTITY);
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.REGISTRY, new PdfString("Adobe"));
    localPdfDictionary2.put(PdfName.ORDERING, new PdfString("Identity"));
    localPdfDictionary2.put(PdfName.SUPPLEMENT, new PdfNumber(0));
    localPdfDictionary1.put(PdfName.CIDSYSTEMINFO, localPdfDictionary2);
    if (!this.vertical)
    {
      localPdfDictionary1.put(PdfName.DW, new PdfNumber(1000));
      StringBuffer localStringBuffer = new StringBuffer("[");
      int i = -10;
      int j = 1;
      for (int k = 0; k < paramArrayOfObject.length; k++)
      {
        int[] arrayOfInt = (int[])paramArrayOfObject[k];
        if (arrayOfInt[1] == 1000)
          continue;
        int m = arrayOfInt[0];
        if (m == i + 1)
        {
          localStringBuffer.append(' ').append(arrayOfInt[1]);
        }
        else
        {
          if (j == 0)
            localStringBuffer.append(']');
          j = 0;
          localStringBuffer.append(m).append('[').append(arrayOfInt[1]);
        }
        i = m;
      }
      if (localStringBuffer.length() > 1)
      {
        localStringBuffer.append("]]");
        localPdfDictionary1.put(PdfName.W, new PdfLiteral(localStringBuffer.toString()));
      }
    }
    return localPdfDictionary1;
  }

  private PdfDictionary getFontBaseType(PdfIndirectReference paramPdfIndirectReference1, String paramString, PdfIndirectReference paramPdfIndirectReference2)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.FONT);
    localPdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE0);
    if (this.cff)
      localPdfDictionary.put(PdfName.BASEFONT, new PdfName(paramString + this.fontName + "-" + this.encoding));
    else
      localPdfDictionary.put(PdfName.BASEFONT, new PdfName(paramString + this.fontName));
    localPdfDictionary.put(PdfName.ENCODING, new PdfName(this.encoding));
    localPdfDictionary.put(PdfName.DESCENDANTFONTS, new PdfArray(paramPdfIndirectReference1));
    if (paramPdfIndirectReference2 != null)
      localPdfDictionary.put(PdfName.TOUNICODE, paramPdfIndirectReference2);
    return localPdfDictionary;
  }

  public int compare(Object paramObject1, Object paramObject2)
  {
    int i = ((int[])paramObject1)[0];
    int j = ((int[])paramObject2)[0];
    if (i < j)
      return -1;
    if (i == j)
      return 0;
    return 1;
  }

  void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException
  {
    HashMap localHashMap = (HashMap)paramArrayOfObject[0];
    addRangeUni(localHashMap, true, this.subset);
    Object[] arrayOfObject = localHashMap.values().toArray();
    Arrays.sort(arrayOfObject, this);
    PdfIndirectReference localPdfIndirectReference1 = null;
    Object localObject1 = null;
    PdfIndirectObject localPdfIndirectObject = null;
    PdfIndirectReference localPdfIndirectReference2 = null;
    if ((paramPdfWriter.getPDFXConformance() == 3) || (paramPdfWriter.getPDFXConformance() == 4))
    {
      if (arrayOfObject.length == 0)
      {
        localObject2 = new PdfStream(new byte[] { -128 });
      }
      else
      {
        int i = ((int[])arrayOfObject[(arrayOfObject.length - 1)])[0];
        localObject4 = new byte[i / 8 + 1];
        for (int j = 0; j < arrayOfObject.length; j++)
        {
          int k = ((int[])arrayOfObject[j])[0];
          int tmp147_146 = (k / 8);
          Object tmp147_140 = localObject4;
          tmp147_140[tmp147_146] = (byte)(tmp147_140[tmp147_146] | rotbits[(k % 8)]);
        }
        localObject2 = new PdfStream(localObject4);
        ((PdfStream)localObject2).flateCompress(this.compressionLevel);
      }
      localPdfIndirectReference2 = paramPdfWriter.addToBody((PdfObject)localObject2).getIndirectReference();
    }
    if (this.cff)
    {
      localObject2 = readCffFont();
      if ((this.subset) || (this.subsetRanges != null))
      {
        localObject3 = new CFFFontSubset(new RandomAccessFileOrArray(localObject2), localHashMap);
        localObject2 = ((CFFFontSubset)localObject3).Process(localObject3.getNames()[0]);
      }
      localObject1 = new BaseFont.StreamFont(localObject2, "CIDFontType0C", this.compressionLevel);
      localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
      localPdfIndirectReference1 = localPdfIndirectObject.getIndirectReference();
    }
    else
    {
      if ((this.subset) || (this.directoryOffset != 0))
      {
        localObject3 = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), localHashMap, this.directoryOffset, false, false);
        localObject2 = ((TrueTypeFontSubSet)localObject3).process();
      }
      else
      {
        localObject2 = getFullFont();
      }
      localObject3 = new int[] { localObject2.length };
      localObject1 = new BaseFont.StreamFont(localObject2, localObject3, this.compressionLevel);
      localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
      localPdfIndirectReference1 = localPdfIndirectObject.getIndirectReference();
    }
    Object localObject2 = "";
    if (this.subset)
      localObject2 = createSubsetPrefix();
    Object localObject3 = getFontDescriptor(localPdfIndirectReference1, (String)localObject2, localPdfIndirectReference2);
    localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject3);
    localPdfIndirectReference1 = localPdfIndirectObject.getIndirectReference();
    localObject1 = getCIDFontType2(localPdfIndirectReference1, (String)localObject2, arrayOfObject);
    localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
    localPdfIndirectReference1 = localPdfIndirectObject.getIndirectReference();
    localObject1 = getToUnicode(arrayOfObject);
    Object localObject4 = null;
    if (localObject1 != null)
    {
      localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject1);
      localObject4 = localPdfIndirectObject.getIndirectReference();
    }
    localObject1 = getFontBaseType(localPdfIndirectReference1, (String)localObject2, (PdfIndirectReference)localObject4);
    paramPdfWriter.addToBody((PdfObject)localObject1, paramPdfIndirectReference);
  }

  public PdfStream getFullFontStream()
    throws IOException, DocumentException
  {
    if (this.cff)
      return new BaseFont.StreamFont(readCffFont(), "CIDFontType0C", this.compressionLevel);
    return super.getFullFontStream();
  }

  byte[] convertToBytes(String paramString)
  {
    return null;
  }

  byte[] convertToBytes(int paramInt)
  {
    return null;
  }

  public int[] getMetricsTT(int paramInt)
  {
    if (this.cmapExt != null)
      return (int[])this.cmapExt.get(new Integer(paramInt));
    HashMap localHashMap = null;
    if (this.fontSpecific)
      localHashMap = this.cmap10;
    else
      localHashMap = this.cmap31;
    if (localHashMap == null)
      return null;
    if (this.fontSpecific)
    {
      if (((paramInt & 0xFFFFFF00) == 0) || ((paramInt & 0xFFFFFF00) == 61440))
        return (int[])localHashMap.get(new Integer(paramInt & 0xFF));
      return null;
    }
    return (int[])localHashMap.get(new Integer(paramInt));
  }

  public boolean charExists(int paramInt)
  {
    return getMetricsTT(paramInt) != null;
  }

  public boolean setCharAdvance(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = getMetricsTT(paramInt1);
    if (arrayOfInt == null)
      return false;
    arrayOfInt[1] = paramInt2;
    return true;
  }

  public int[] getCharBBox(int paramInt)
  {
    if (this.bboxes == null)
      return null;
    int[] arrayOfInt = getMetricsTT(paramInt);
    if (arrayOfInt == null)
      return null;
    return this.bboxes[arrayOfInt[0]];
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.TrueTypeFontUnicode
 * JD-Core Version:    0.6.0
 */