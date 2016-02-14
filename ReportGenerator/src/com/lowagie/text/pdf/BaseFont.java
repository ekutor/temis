package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class BaseFont
{
  public static final String COURIER = "Courier";
  public static final String COURIER_BOLD = "Courier-Bold";
  public static final String COURIER_OBLIQUE = "Courier-Oblique";
  public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
  public static final String HELVETICA = "Helvetica";
  public static final String HELVETICA_BOLD = "Helvetica-Bold";
  public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
  public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
  public static final String SYMBOL = "Symbol";
  public static final String TIMES_ROMAN = "Times-Roman";
  public static final String TIMES_BOLD = "Times-Bold";
  public static final String TIMES_ITALIC = "Times-Italic";
  public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
  public static final String ZAPFDINGBATS = "ZapfDingbats";
  public static final int ASCENT = 1;
  public static final int CAPHEIGHT = 2;
  public static final int DESCENT = 3;
  public static final int ITALICANGLE = 4;
  public static final int BBOXLLX = 5;
  public static final int BBOXLLY = 6;
  public static final int BBOXURX = 7;
  public static final int BBOXURY = 8;
  public static final int AWT_ASCENT = 9;
  public static final int AWT_DESCENT = 10;
  public static final int AWT_LEADING = 11;
  public static final int AWT_MAXADVANCE = 12;
  public static final int UNDERLINE_POSITION = 13;
  public static final int UNDERLINE_THICKNESS = 14;
  public static final int STRIKETHROUGH_POSITION = 15;
  public static final int STRIKETHROUGH_THICKNESS = 16;
  public static final int SUBSCRIPT_SIZE = 17;
  public static final int SUBSCRIPT_OFFSET = 18;
  public static final int SUPERSCRIPT_SIZE = 19;
  public static final int SUPERSCRIPT_OFFSET = 20;
  public static final int FONT_TYPE_T1 = 0;
  public static final int FONT_TYPE_TT = 1;
  public static final int FONT_TYPE_CJK = 2;
  public static final int FONT_TYPE_TTUNI = 3;
  public static final int FONT_TYPE_DOCUMENT = 4;
  public static final int FONT_TYPE_T3 = 5;
  public static final String IDENTITY_H = "Identity-H";
  public static final String IDENTITY_V = "Identity-V";
  public static final String CP1250 = "Cp1250";
  public static final String CP1252 = "Cp1252";
  public static final String CP1257 = "Cp1257";
  public static final String WINANSI = "Cp1252";
  public static final String MACROMAN = "MacRoman";
  public static final int[] CHAR_RANGE_LATIN = { 0, 383, 8192, 8303, 8352, 8399, 64256, 64262 };
  public static final int[] CHAR_RANGE_ARABIC = { 0, 127, 1536, 1663, 8352, 8399, 64336, 64511, 65136, 65279 };
  public static final int[] CHAR_RANGE_HEBREW = { 0, 127, 1424, 1535, 8352, 8399, 64285, 64335 };
  public static final int[] CHAR_RANGE_CYRILLIC = { 0, 127, 1024, 1327, 8192, 8303, 8352, 8399 };
  public static final boolean EMBEDDED = true;
  public static final boolean NOT_EMBEDDED = false;
  public static final boolean CACHED = true;
  public static final boolean NOT_CACHED = false;
  public static final String RESOURCE_PATH = "com/lowagie/text/pdf/fonts/";
  public static final char CID_NEWLINE = '翿';
  protected ArrayList subsetRanges;
  int fontType;
  public static final String notdef = ".notdef";
  protected int[] widths = new int[256];
  protected String[] differences = new String[256];
  protected char[] unicodeDifferences = new char[256];
  protected int[][] charBBoxes = new int[256][];
  protected String encoding;
  protected boolean embedded;
  protected int compressionLevel = -1;
  protected boolean fontSpecific = true;
  protected static HashMap fontCache = new HashMap();
  protected static final HashMap BuiltinFonts14 = new HashMap();
  protected boolean forceWidthsOutput = false;
  protected boolean directTextToByte = false;
  protected boolean subset = true;
  protected boolean fastWinansi = false;
  protected IntHashtable specialMap;

  public static BaseFont createFont()
    throws DocumentException, IOException
  {
    return createFont("Helvetica", "Cp1252", false);
  }

  public static BaseFont createFont(String paramString1, String paramString2, boolean paramBoolean)
    throws DocumentException, IOException
  {
    return createFont(paramString1, paramString2, paramBoolean, true, null, null, false);
  }

  public static BaseFont createFont(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    throws DocumentException, IOException
  {
    return createFont(paramString1, paramString2, paramBoolean1, true, null, null, paramBoolean2);
  }

  public static BaseFont createFont(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws DocumentException, IOException
  {
    return createFont(paramString1, paramString2, paramBoolean1, paramBoolean2, paramArrayOfByte1, paramArrayOfByte2, false);
  }

  public static BaseFont createFont(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean3)
    throws DocumentException, IOException
  {
    return createFont(paramString1, paramString2, paramBoolean1, paramBoolean2, paramArrayOfByte1, paramArrayOfByte2, false, false);
  }

  public static BaseFont createFont(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean3, boolean paramBoolean4)
    throws DocumentException, IOException
  {
    String str1 = getBaseName(paramString1);
    paramString2 = normalizeEncoding(paramString2);
    boolean bool1 = BuiltinFonts14.containsKey(paramString1);
    boolean bool2 = bool1 ? false : CJKFont.isCJKFont(str1, paramString2);
    if ((bool1) || (bool2))
      paramBoolean1 = false;
    else if ((paramString2.equals("Identity-H")) || (paramString2.equals("Identity-V")))
      paramBoolean1 = true;
    BaseFont localBaseFont = null;
    Object localObject1 = null;
    String str2 = paramString1 + "\n" + paramString2 + "\n" + paramBoolean1;
    if (paramBoolean2)
    {
      synchronized (fontCache)
      {
        localBaseFont = (BaseFont)fontCache.get(str2);
      }
      if (localBaseFont != null)
        return localBaseFont;
    }
    if ((bool1) || (paramString1.toLowerCase().endsWith(".afm")) || (paramString1.toLowerCase().endsWith(".pfm")))
    {
      localObject1 = new Type1Font(paramString1, paramString2, paramBoolean1, paramArrayOfByte1, paramArrayOfByte2, paramBoolean4);
      ((BaseFont)localObject1).fastWinansi = paramString2.equals("Cp1252");
    }
    else if ((str1.toLowerCase().endsWith(".ttf")) || (str1.toLowerCase().endsWith(".otf")) || (str1.toLowerCase().indexOf(".ttc,") > 0))
    {
      if ((paramString2.equals("Identity-H")) || (paramString2.equals("Identity-V")))
      {
        localObject1 = new TrueTypeFontUnicode(paramString1, paramString2, paramBoolean1, paramArrayOfByte1, paramBoolean4);
      }
      else
      {
        localObject1 = new TrueTypeFont(paramString1, paramString2, paramBoolean1, paramArrayOfByte1, false, paramBoolean4);
        ((BaseFont)localObject1).fastWinansi = paramString2.equals("Cp1252");
      }
    }
    else if (bool2)
    {
      localObject1 = new CJKFont(paramString1, paramString2, paramBoolean1);
    }
    else
    {
      if (paramBoolean3)
        return null;
      throw new DocumentException("Font '" + paramString1 + "' with '" + paramString2 + "' is not recognized.");
    }
    if (paramBoolean2)
      synchronized (fontCache)
      {
        localBaseFont = (BaseFont)fontCache.get(str2);
        if (localBaseFont != null)
          return localBaseFont;
        fontCache.put(str2, localObject1);
      }
    return (BaseFont)localObject1;
  }

  public static BaseFont createFont(PRIndirectReference paramPRIndirectReference)
  {
    return new DocumentFont(paramPRIndirectReference);
  }

  protected static String getBaseName(String paramString)
  {
    if (paramString.endsWith(",Bold"))
      return paramString.substring(0, paramString.length() - 5);
    if (paramString.endsWith(",Italic"))
      return paramString.substring(0, paramString.length() - 7);
    if (paramString.endsWith(",BoldItalic"))
      return paramString.substring(0, paramString.length() - 11);
    return paramString;
  }

  protected static String normalizeEncoding(String paramString)
  {
    if ((paramString.equals("winansi")) || (paramString.equals("")))
      return "Cp1252";
    if (paramString.equals("macroman"))
      return "MacRoman";
    return paramString;
  }

  protected void createEncoding()
  {
    if (this.encoding.startsWith("#"))
    {
      this.specialMap = new IntHashtable();
      StringTokenizer localStringTokenizer = new StringTokenizer(this.encoding.substring(1), " ,\t\n\r\f");
      String str4;
      int m;
      if (localStringTokenizer.nextToken().equals("full"))
        while (localStringTokenizer.hasMoreTokens())
        {
          String str2 = localStringTokenizer.nextToken();
          str4 = localStringTokenizer.nextToken();
          m = (char)Integer.parseInt(localStringTokenizer.nextToken(), 16);
          int n;
          if (str2.startsWith("'"))
            n = str2.charAt(1);
          else
            n = Integer.parseInt(str2);
          n %= 256;
          this.specialMap.put(m, n);
          this.differences[n] = str4;
          this.unicodeDifferences[n] = m;
          this.widths[n] = getRawWidth(m, str4);
          this.charBBoxes[n] = getRawCharBBox(m, str4);
        }
      int j = 0;
      if (localStringTokenizer.hasMoreTokens())
        j = Integer.parseInt(localStringTokenizer.nextToken());
      while ((localStringTokenizer.hasMoreTokens()) && (j < 256))
      {
        str4 = localStringTokenizer.nextToken();
        m = Integer.parseInt(str4, 16) % 65536;
        String str5 = GlyphList.unicodeToName(m);
        if (str5 == null)
          continue;
        this.specialMap.put(m, j);
        this.differences[j] = str5;
        this.unicodeDifferences[j] = (char)m;
        this.widths[j] = getRawWidth(m, str5);
        this.charBBoxes[j] = getRawCharBBox(m, str5);
        j++;
      }
      for (j = 0; j < 256; j++)
      {
        if (this.differences[j] != null)
          continue;
        this.differences[j] = ".notdef";
      }
    }
    if (this.fontSpecific)
      for (int i = 0; i < 256; i++)
      {
        this.widths[i] = getRawWidth(i, null);
        this.charBBoxes[i] = getRawCharBBox(i, null);
      }
    byte[] arrayOfByte = new byte[1];
    for (int i1 = 0; i1 < 256; i1++)
    {
      arrayOfByte[0] = (byte)i1;
      String str1 = PdfEncodings.convertToString(arrayOfByte, this.encoding);
      int k;
      if (str1.length() > 0)
        k = str1.charAt(0);
      else
        k = 63;
      String str3 = GlyphList.unicodeToName(k);
      if (str3 == null)
        str3 = ".notdef";
      this.differences[i1] = str3;
      this.unicodeDifferences[i1] = k;
      this.widths[i1] = getRawWidth(k, str3);
      this.charBBoxes[i1] = getRawCharBBox(k, str3);
    }
  }

  abstract int getRawWidth(int paramInt, String paramString);

  public abstract int getKerning(int paramInt1, int paramInt2);

  public abstract boolean setKerning(int paramInt1, int paramInt2, int paramInt3);

  public int getWidth(int paramInt)
  {
    if (this.fastWinansi)
    {
      if ((paramInt < 128) || ((paramInt >= 160) && (paramInt <= 255)))
        return this.widths[paramInt];
      return this.widths[PdfEncodings.winansi.get(paramInt)];
    }
    int i = 0;
    byte[] arrayOfByte = convertToBytes((char)paramInt);
    for (int j = 0; j < arrayOfByte.length; j++)
      i += this.widths[(0xFF & arrayOfByte[j])];
    return i;
  }

  public int getWidth(String paramString)
  {
    int i = 0;
    if (this.fastWinansi)
    {
      int j = paramString.length();
      for (k = 0; k < j; k++)
      {
        int m = paramString.charAt(k);
        if ((m < 128) || ((m >= 160) && (m <= 255)))
          i += this.widths[m];
        else
          i += this.widths[PdfEncodings.winansi.get(m)];
      }
      return i;
    }
    byte[] arrayOfByte = convertToBytes(paramString);
    for (int k = 0; k < arrayOfByte.length; k++)
      i += this.widths[(0xFF & arrayOfByte[k])];
    return i;
  }

  public int getDescent(String paramString)
  {
    int i = 0;
    char[] arrayOfChar = paramString.toCharArray();
    for (int j = 0; j < arrayOfChar.length; j++)
    {
      int[] arrayOfInt = getCharBBox(arrayOfChar[j]);
      if ((arrayOfInt == null) || (arrayOfInt[1] >= i))
        continue;
      i = arrayOfInt[1];
    }
    return i;
  }

  public int getAscent(String paramString)
  {
    int i = 0;
    char[] arrayOfChar = paramString.toCharArray();
    for (int j = 0; j < arrayOfChar.length; j++)
    {
      int[] arrayOfInt = getCharBBox(arrayOfChar[j]);
      if ((arrayOfInt == null) || (arrayOfInt[3] <= i))
        continue;
      i = arrayOfInt[3];
    }
    return i;
  }

  public float getDescentPoint(String paramString, float paramFloat)
  {
    return getDescent(paramString) * 0.001F * paramFloat;
  }

  public float getAscentPoint(String paramString, float paramFloat)
  {
    return getAscent(paramString) * 0.001F * paramFloat;
  }

  public float getWidthPointKerned(String paramString, float paramFloat)
  {
    float f = getWidth(paramString) * 0.001F * paramFloat;
    if (!hasKernPairs())
      return f;
    int i = paramString.length() - 1;
    int j = 0;
    char[] arrayOfChar = paramString.toCharArray();
    for (int k = 0; k < i; k++)
      j += getKerning(arrayOfChar[k], arrayOfChar[(k + 1)]);
    return f + j * 0.001F * paramFloat;
  }

  public float getWidthPoint(String paramString, float paramFloat)
  {
    return getWidth(paramString) * 0.001F * paramFloat;
  }

  public float getWidthPoint(int paramInt, float paramFloat)
  {
    return getWidth(paramInt) * 0.001F * paramFloat;
  }

  byte[] convertToBytes(String paramString)
  {
    if (this.directTextToByte)
      return PdfEncodings.convertToBytes(paramString, null);
    if (this.specialMap != null)
    {
      byte[] arrayOfByte1 = new byte[paramString.length()];
      int i = 0;
      int j = paramString.length();
      for (int k = 0; k < j; k++)
      {
        int m = paramString.charAt(k);
        if (!this.specialMap.containsKey(m))
          continue;
        arrayOfByte1[(i++)] = (byte)this.specialMap.get(m);
      }
      if (i < j)
      {
        byte[] arrayOfByte2 = new byte[i];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
        return arrayOfByte2;
      }
      return arrayOfByte1;
    }
    return PdfEncodings.convertToBytes(paramString, this.encoding);
  }

  byte[] convertToBytes(int paramInt)
  {
    if (this.directTextToByte)
      return PdfEncodings.convertToBytes((char)paramInt, null);
    if (this.specialMap != null)
    {
      if (this.specialMap.containsKey(paramInt))
        return new byte[] { (byte)this.specialMap.get(paramInt) };
      return new byte[0];
    }
    return PdfEncodings.convertToBytes((char)paramInt, this.encoding);
  }

  abstract void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException;

  abstract PdfStream getFullFontStream()
    throws IOException, DocumentException;

  public String getEncoding()
  {
    return this.encoding;
  }

  public abstract float getFontDescriptor(int paramInt, float paramFloat);

  public int getFontType()
  {
    return this.fontType;
  }

  public boolean isEmbedded()
  {
    return this.embedded;
  }

  public boolean isFontSpecific()
  {
    return this.fontSpecific;
  }

  public static String createSubsetPrefix()
  {
    String str = "";
    for (int i = 0; i < 6; i++)
      str = str + (char)(int)(Math.random() * 26.0D + 65.0D);
    return str + "+";
  }

  char getUnicodeDifferences(int paramInt)
  {
    return this.unicodeDifferences[paramInt];
  }

  public abstract String getPostscriptFontName();

  public abstract void setPostscriptFontName(String paramString);

  public abstract String[][] getFullFontName();

  public abstract String[][] getAllNameEntries();

  public static String[][] getFullFontName(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws DocumentException, IOException
  {
    String str = getBaseName(paramString1);
    Object localObject = null;
    if ((str.toLowerCase().endsWith(".ttf")) || (str.toLowerCase().endsWith(".otf")) || (str.toLowerCase().indexOf(".ttc,") > 0))
      localObject = new TrueTypeFont(paramString1, "Cp1252", false, paramArrayOfByte, true, false);
    else
      localObject = createFont(paramString1, paramString2, false, false, paramArrayOfByte, null);
    return (String)((BaseFont)localObject).getFullFontName();
  }

  public static Object[] getAllFontNames(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws DocumentException, IOException
  {
    String str = getBaseName(paramString1);
    Object localObject = null;
    if ((str.toLowerCase().endsWith(".ttf")) || (str.toLowerCase().endsWith(".otf")) || (str.toLowerCase().indexOf(".ttc,") > 0))
      localObject = new TrueTypeFont(paramString1, "Cp1252", false, paramArrayOfByte, true, false);
    else
      localObject = createFont(paramString1, paramString2, false, false, paramArrayOfByte, null);
    return (Object)new Object[] { ((BaseFont)localObject).getPostscriptFontName(), ((BaseFont)localObject).getFamilyFontName(), ((BaseFont)localObject).getFullFontName() };
  }

  public static String[][] getAllNameEntries(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws DocumentException, IOException
  {
    String str = getBaseName(paramString1);
    Object localObject = null;
    if ((str.toLowerCase().endsWith(".ttf")) || (str.toLowerCase().endsWith(".otf")) || (str.toLowerCase().indexOf(".ttc,") > 0))
      localObject = new TrueTypeFont(paramString1, "Cp1252", false, paramArrayOfByte, true, false);
    else
      localObject = createFont(paramString1, paramString2, false, false, paramArrayOfByte, null);
    return (String)((BaseFont)localObject).getAllNameEntries();
  }

  public abstract String[][] getFamilyFontName();

  public String[] getCodePagesSupported()
  {
    return new String[0];
  }

  public static String[] enumerateTTCNames(String paramString)
    throws DocumentException, IOException
  {
    return new EnumerateTTC(paramString).getNames();
  }

  public static String[] enumerateTTCNames(byte[] paramArrayOfByte)
    throws DocumentException, IOException
  {
    return new EnumerateTTC(paramArrayOfByte).getNames();
  }

  public int[] getWidths()
  {
    return this.widths;
  }

  public String[] getDifferences()
  {
    return this.differences;
  }

  public char[] getUnicodeDifferences()
  {
    return this.unicodeDifferences;
  }

  public boolean isForceWidthsOutput()
  {
    return this.forceWidthsOutput;
  }

  public void setForceWidthsOutput(boolean paramBoolean)
  {
    this.forceWidthsOutput = paramBoolean;
  }

  public boolean isDirectTextToByte()
  {
    return this.directTextToByte;
  }

  public void setDirectTextToByte(boolean paramBoolean)
  {
    this.directTextToByte = paramBoolean;
  }

  public boolean isSubset()
  {
    return this.subset;
  }

  public void setSubset(boolean paramBoolean)
  {
    this.subset = paramBoolean;
  }

  public static InputStream getResourceStream(String paramString)
  {
    return getResourceStream(paramString, null);
  }

  public static InputStream getResourceStream(String paramString, ClassLoader paramClassLoader)
  {
    if (paramString.startsWith("/"))
      paramString = paramString.substring(1);
    InputStream localInputStream = null;
    if (paramClassLoader != null)
    {
      localInputStream = paramClassLoader.getResourceAsStream(paramString);
      if (localInputStream != null)
        return localInputStream;
    }
    try
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader != null)
        localInputStream = localClassLoader.getResourceAsStream(paramString);
    }
    catch (Throwable localThrowable)
    {
    }
    if (localInputStream == null)
      localInputStream = BaseFont.class.getResourceAsStream("/" + paramString);
    if (localInputStream == null)
      localInputStream = ClassLoader.getSystemResourceAsStream(paramString);
    return localInputStream;
  }

  public int getUnicodeEquivalent(int paramInt)
  {
    return paramInt;
  }

  public int getCidCode(int paramInt)
  {
    return paramInt;
  }

  public abstract boolean hasKernPairs();

  public boolean charExists(int paramInt)
  {
    byte[] arrayOfByte = convertToBytes(paramInt);
    return arrayOfByte.length > 0;
  }

  public boolean setCharAdvance(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = convertToBytes(paramInt1);
    if (arrayOfByte.length == 0)
      return false;
    this.widths[(0xFF & arrayOfByte[0])] = paramInt2;
    return true;
  }

  private static void addFont(PRIndirectReference paramPRIndirectReference, IntHashtable paramIntHashtable, ArrayList paramArrayList)
  {
    PdfObject localPdfObject = PdfReader.getPdfObject(paramPRIndirectReference);
    if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
      return;
    PdfDictionary localPdfDictionary = (PdfDictionary)localPdfObject;
    PdfName localPdfName1 = localPdfDictionary.getAsName(PdfName.SUBTYPE);
    if ((!PdfName.TYPE1.equals(localPdfName1)) && (!PdfName.TRUETYPE.equals(localPdfName1)))
      return;
    PdfName localPdfName2 = localPdfDictionary.getAsName(PdfName.BASEFONT);
    paramArrayList.add(new Object[] { PdfName.decodeName(localPdfName2.toString()), paramPRIndirectReference });
    paramIntHashtable.put(paramPRIndirectReference.getNumber(), 1);
  }

  private static void recourseFonts(PdfDictionary paramPdfDictionary, IntHashtable paramIntHashtable, ArrayList paramArrayList, int paramInt)
  {
    paramInt++;
    if (paramInt > 50)
      return;
    PdfDictionary localPdfDictionary1 = paramPdfDictionary.getAsDict(PdfName.RESOURCES);
    if (localPdfDictionary1 == null)
      return;
    PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.FONT);
    Object localObject2;
    if (localPdfDictionary2 != null)
    {
      localObject1 = localPdfDictionary2.getKeys().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = localPdfDictionary2.get((PdfName)((Iterator)localObject1).next());
        if ((localObject2 == null) || (!((PdfObject)localObject2).isIndirect()))
          continue;
        int i = ((PRIndirectReference)localObject2).getNumber();
        if (paramIntHashtable.containsKey(i))
          continue;
        addFont((PRIndirectReference)localObject2, paramIntHashtable, paramArrayList);
      }
    }
    Object localObject1 = localPdfDictionary1.getAsDict(PdfName.XOBJECT);
    if (localObject1 != null)
    {
      localObject2 = ((PdfDictionary)localObject1).getKeys().iterator();
      while (((Iterator)localObject2).hasNext())
        recourseFonts(((PdfDictionary)localObject1).getAsDict((PdfName)((Iterator)localObject2).next()), paramIntHashtable, paramArrayList, paramInt);
    }
  }

  public static ArrayList getDocumentFonts(PdfReader paramPdfReader)
  {
    IntHashtable localIntHashtable = new IntHashtable();
    ArrayList localArrayList = new ArrayList();
    int i = paramPdfReader.getNumberOfPages();
    for (int j = 1; j <= i; j++)
      recourseFonts(paramPdfReader.getPageN(j), localIntHashtable, localArrayList, 1);
    return localArrayList;
  }

  public static ArrayList getDocumentFonts(PdfReader paramPdfReader, int paramInt)
  {
    IntHashtable localIntHashtable = new IntHashtable();
    ArrayList localArrayList = new ArrayList();
    recourseFonts(paramPdfReader.getPageN(paramInt), localIntHashtable, localArrayList, 1);
    return localArrayList;
  }

  public int[] getCharBBox(int paramInt)
  {
    byte[] arrayOfByte = convertToBytes(paramInt);
    if (arrayOfByte.length == 0)
      return null;
    return this.charBBoxes[(arrayOfByte[0] & 0xFF)];
  }

  protected abstract int[] getRawCharBBox(int paramInt, String paramString);

  public void correctArabicAdvance()
  {
    for (int i = 1611; i <= 1624; i = (char)(i + 1))
      setCharAdvance(i, 0);
    setCharAdvance(1648, 0);
    for (i = 1750; i <= 1756; i = (char)(i + 1))
      setCharAdvance(i, 0);
    for (i = 1759; i <= 1764; i = (char)(i + 1))
      setCharAdvance(i, 0);
    for (i = 1767; i <= 1768; i = (char)(i + 1))
      setCharAdvance(i, 0);
    for (i = 1770; i <= 1773; i = (char)(i + 1))
      setCharAdvance(i, 0);
  }

  public void addSubsetRange(int[] paramArrayOfInt)
  {
    if (this.subsetRanges == null)
      this.subsetRanges = new ArrayList();
    this.subsetRanges.add(paramArrayOfInt);
  }

  public int getCompressionLevel()
  {
    return this.compressionLevel;
  }

  public void setCompressionLevel(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 9))
      this.compressionLevel = -1;
    else
      this.compressionLevel = paramInt;
  }

  static
  {
    BuiltinFonts14.put("Courier", PdfName.COURIER);
    BuiltinFonts14.put("Courier-Bold", PdfName.COURIER_BOLD);
    BuiltinFonts14.put("Courier-BoldOblique", PdfName.COURIER_BOLDOBLIQUE);
    BuiltinFonts14.put("Courier-Oblique", PdfName.COURIER_OBLIQUE);
    BuiltinFonts14.put("Helvetica", PdfName.HELVETICA);
    BuiltinFonts14.put("Helvetica-Bold", PdfName.HELVETICA_BOLD);
    BuiltinFonts14.put("Helvetica-BoldOblique", PdfName.HELVETICA_BOLDOBLIQUE);
    BuiltinFonts14.put("Helvetica-Oblique", PdfName.HELVETICA_OBLIQUE);
    BuiltinFonts14.put("Symbol", PdfName.SYMBOL);
    BuiltinFonts14.put("Times-Roman", PdfName.TIMES_ROMAN);
    BuiltinFonts14.put("Times-Bold", PdfName.TIMES_BOLD);
    BuiltinFonts14.put("Times-BoldItalic", PdfName.TIMES_BOLDITALIC);
    BuiltinFonts14.put("Times-Italic", PdfName.TIMES_ITALIC);
    BuiltinFonts14.put("ZapfDingbats", PdfName.ZAPFDINGBATS);
  }

  static class StreamFont extends PdfStream
  {
    public StreamFont(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt)
      throws DocumentException
    {
      try
      {
        this.bytes = paramArrayOfByte;
        put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        for (int i = 0; i < paramArrayOfInt.length; i++)
          put(new PdfName("Length" + (i + 1)), new PdfNumber(paramArrayOfInt[i]));
        flateCompress(paramInt);
      }
      catch (Exception localException)
      {
        throw new DocumentException(localException);
      }
    }

    public StreamFont(byte[] paramArrayOfByte, String paramString, int paramInt)
      throws DocumentException
    {
      try
      {
        this.bytes = paramArrayOfByte;
        put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        if (paramString != null)
          put(PdfName.SUBTYPE, new PdfName(paramString));
        flateCompress(paramInt);
      }
      catch (Exception localException)
      {
        throw new DocumentException(localException);
      }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BaseFont
 * JD-Core Version:    0.6.0
 */