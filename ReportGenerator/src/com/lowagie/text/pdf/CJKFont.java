package com.lowagie.text.pdf;

import C;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

class CJKFont extends BaseFont
{
  static final String CJK_ENCODING = "UnicodeBigUnmarked";
  private static final int FIRST = 0;
  private static final int BRACKET = 1;
  private static final int SERIAL = 2;
  private static final int V1Y = 880;
  static Properties cjkFonts = new Properties();
  static Properties cjkEncodings = new Properties();
  static Hashtable allCMaps = new Hashtable();
  static Hashtable allFonts = new Hashtable();
  private static boolean propertiesLoaded = false;
  private String fontName;
  private String style = "";
  private String CMap;
  private boolean cidDirect = false;
  private char[] translationMap;
  private IntHashtable vMetrics;
  private IntHashtable hMetrics;
  private HashMap fontDesc;
  private boolean vertical = false;

  private static void loadProperties()
  {
    if (propertiesLoaded)
      return;
    synchronized (allFonts)
    {
      if (propertiesLoaded)
        return;
      try
      {
        InputStream localInputStream = getResourceStream("com/lowagie/text/pdf/fonts/cjkfonts.properties");
        cjkFonts.load(localInputStream);
        localInputStream.close();
        localInputStream = getResourceStream("com/lowagie/text/pdf/fonts/cjkencodings.properties");
        cjkEncodings.load(localInputStream);
        localInputStream.close();
      }
      catch (Exception localException)
      {
        cjkFonts = new Properties();
        cjkEncodings = new Properties();
      }
      propertiesLoaded = true;
    }
  }

  CJKFont(String paramString1, String paramString2, boolean paramBoolean)
    throws DocumentException
  {
    loadProperties();
    this.fontType = 2;
    String str1 = getBaseName(paramString1);
    if (!isCJKFont(str1, paramString2))
      throw new DocumentException("Font '" + paramString1 + "' with '" + paramString2 + "' encoding is not a CJK font.");
    if (str1.length() < paramString1.length())
    {
      this.style = paramString1.substring(str1.length());
      paramString1 = str1;
    }
    this.fontName = paramString1;
    this.encoding = "UnicodeBigUnmarked";
    this.vertical = paramString2.endsWith("V");
    this.CMap = paramString2;
    Object localObject1;
    Object localObject2;
    if (paramString2.startsWith("Identity-"))
    {
      this.cidDirect = true;
      localObject1 = cjkFonts.getProperty(paramString1);
      localObject1 = ((String)localObject1).substring(0, ((String)localObject1).indexOf('_'));
      localObject2 = (char[])allCMaps.get(localObject1);
      if (localObject2 == null)
      {
        localObject2 = readCMap((String)localObject1);
        if (localObject2 == null)
          throw new DocumentException("The cmap " + (String)localObject1 + " does not exist as a resource.");
        localObject2[32767] = 10;
        allCMaps.put(localObject1, localObject2);
      }
      this.translationMap = ((C)localObject2);
    }
    else
    {
      localObject1 = (char[])allCMaps.get(paramString2);
      if (localObject1 == null)
      {
        localObject2 = cjkEncodings.getProperty(paramString2);
        if (localObject2 == null)
          throw new DocumentException("The resource cjkencodings.properties does not contain the encoding " + paramString2);
        StringTokenizer localStringTokenizer = new StringTokenizer((String)localObject2);
        String str2 = localStringTokenizer.nextToken();
        localObject1 = (char[])allCMaps.get(str2);
        if (localObject1 == null)
        {
          localObject1 = readCMap(str2);
          allCMaps.put(str2, localObject1);
        }
        if (localStringTokenizer.hasMoreTokens())
        {
          String str3 = localStringTokenizer.nextToken();
          char[] arrayOfChar = readCMap(str3);
          for (int i = 0; i < 65536; i++)
          {
            if (arrayOfChar[i] != 0)
              continue;
            arrayOfChar[i] = localObject1[i];
          }
          allCMaps.put(paramString2, arrayOfChar);
          localObject1 = arrayOfChar;
        }
      }
      this.translationMap = ((C)localObject1);
    }
    this.fontDesc = ((HashMap)allFonts.get(paramString1));
    if (this.fontDesc == null)
    {
      this.fontDesc = readFontProperties(paramString1);
      allFonts.put(paramString1, this.fontDesc);
    }
    this.hMetrics = ((IntHashtable)this.fontDesc.get("W"));
    this.vMetrics = ((IntHashtable)this.fontDesc.get("W2"));
  }

  public static boolean isCJKFont(String paramString1, String paramString2)
  {
    loadProperties();
    String str = cjkFonts.getProperty(paramString1);
    return (str != null) && ((paramString2.equals("Identity-H")) || (paramString2.equals("Identity-V")) || (str.indexOf("_" + paramString2 + "_") >= 0));
  }

  public int getWidth(int paramInt)
  {
    int i = paramInt;
    if (!this.cidDirect)
      i = this.translationMap[i];
    int j;
    if (this.vertical)
      j = this.vMetrics.get(i);
    else
      j = this.hMetrics.get(i);
    if (j > 0)
      return j;
    return 1000;
  }

  public int getWidth(String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = paramString.charAt(j);
      if (!this.cidDirect)
        k = this.translationMap[k];
      int m;
      if (this.vertical)
        m = this.vMetrics.get(k);
      else
        m = this.hMetrics.get(k);
      if (m > 0)
        i += m;
      else
        i += 1000;
    }
    return i;
  }

  int getRawWidth(int paramInt, String paramString)
  {
    return 0;
  }

  public int getKerning(int paramInt1, int paramInt2)
  {
    return 0;
  }

  private PdfDictionary getFontDescriptor()
  {
    PdfDictionary localPdfDictionary1 = new PdfDictionary(PdfName.FONTDESCRIPTOR);
    localPdfDictionary1.put(PdfName.ASCENT, new PdfLiteral((String)this.fontDesc.get("Ascent")));
    localPdfDictionary1.put(PdfName.CAPHEIGHT, new PdfLiteral((String)this.fontDesc.get("CapHeight")));
    localPdfDictionary1.put(PdfName.DESCENT, new PdfLiteral((String)this.fontDesc.get("Descent")));
    localPdfDictionary1.put(PdfName.FLAGS, new PdfLiteral((String)this.fontDesc.get("Flags")));
    localPdfDictionary1.put(PdfName.FONTBBOX, new PdfLiteral((String)this.fontDesc.get("FontBBox")));
    localPdfDictionary1.put(PdfName.FONTNAME, new PdfName(this.fontName + this.style));
    localPdfDictionary1.put(PdfName.ITALICANGLE, new PdfLiteral((String)this.fontDesc.get("ItalicAngle")));
    localPdfDictionary1.put(PdfName.STEMV, new PdfLiteral((String)this.fontDesc.get("StemV")));
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.PANOSE, new PdfString((String)this.fontDesc.get("Panose"), null));
    localPdfDictionary1.put(PdfName.STYLE, localPdfDictionary2);
    return localPdfDictionary1;
  }

  private PdfDictionary getCIDFont(PdfIndirectReference paramPdfIndirectReference, IntHashtable paramIntHashtable)
  {
    PdfDictionary localPdfDictionary1 = new PdfDictionary(PdfName.FONT);
    localPdfDictionary1.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
    localPdfDictionary1.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
    localPdfDictionary1.put(PdfName.FONTDESCRIPTOR, paramPdfIndirectReference);
    int[] arrayOfInt = paramIntHashtable.toOrderedKeys();
    String str = convertToHCIDMetrics(arrayOfInt, this.hMetrics);
    if (str != null)
      localPdfDictionary1.put(PdfName.W, new PdfLiteral(str));
    if (this.vertical)
    {
      str = convertToVCIDMetrics(arrayOfInt, this.vMetrics, this.hMetrics);
      if (str != null)
        localPdfDictionary1.put(PdfName.W2, new PdfLiteral(str));
    }
    else
    {
      localPdfDictionary1.put(PdfName.DW, new PdfNumber(1000));
    }
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.REGISTRY, new PdfString((String)this.fontDesc.get("Registry"), null));
    localPdfDictionary2.put(PdfName.ORDERING, new PdfString((String)this.fontDesc.get("Ordering"), null));
    localPdfDictionary2.put(PdfName.SUPPLEMENT, new PdfLiteral((String)this.fontDesc.get("Supplement")));
    localPdfDictionary1.put(PdfName.CIDSYSTEMINFO, localPdfDictionary2);
    return localPdfDictionary1;
  }

  private PdfDictionary getFontBaseType(PdfIndirectReference paramPdfIndirectReference)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.FONT);
    localPdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE0);
    String str = this.fontName;
    if (this.style.length() > 0)
      str = str + "-" + this.style.substring(1);
    str = str + "-" + this.CMap;
    localPdfDictionary.put(PdfName.BASEFONT, new PdfName(str));
    localPdfDictionary.put(PdfName.ENCODING, new PdfName(this.CMap));
    localPdfDictionary.put(PdfName.DESCENDANTFONTS, new PdfArray(paramPdfIndirectReference));
    return localPdfDictionary;
  }

  void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException
  {
    IntHashtable localIntHashtable = (IntHashtable)paramArrayOfObject[0];
    PdfIndirectReference localPdfIndirectReference = null;
    PdfDictionary localPdfDictionary = null;
    PdfIndirectObject localPdfIndirectObject = null;
    localPdfDictionary = getFontDescriptor();
    if (localPdfDictionary != null)
    {
      localPdfIndirectObject = paramPdfWriter.addToBody(localPdfDictionary);
      localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
    }
    localPdfDictionary = getCIDFont(localPdfIndirectReference, localIntHashtable);
    if (localPdfDictionary != null)
    {
      localPdfIndirectObject = paramPdfWriter.addToBody(localPdfDictionary);
      localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
    }
    localPdfDictionary = getFontBaseType(localPdfIndirectReference);
    paramPdfWriter.addToBody(localPdfDictionary, paramPdfIndirectReference);
  }

  public PdfStream getFullFontStream()
  {
    return null;
  }

  private float getDescNumber(String paramString)
  {
    return Integer.parseInt((String)this.fontDesc.get(paramString));
  }

  private float getBBox(int paramInt)
  {
    String str1 = (String)this.fontDesc.get("FontBBox");
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, " []\r\n\t\f");
    String str2 = localStringTokenizer.nextToken();
    for (int i = 0; i < paramInt; i++)
      str2 = localStringTokenizer.nextToken();
    return Integer.parseInt(str2);
  }

  public float getFontDescriptor(int paramInt, float paramFloat)
  {
    switch (paramInt)
    {
    case 1:
    case 9:
      return getDescNumber("Ascent") * paramFloat / 1000.0F;
    case 2:
      return getDescNumber("CapHeight") * paramFloat / 1000.0F;
    case 3:
    case 10:
      return getDescNumber("Descent") * paramFloat / 1000.0F;
    case 4:
      return getDescNumber("ItalicAngle");
    case 5:
      return paramFloat * getBBox(0) / 1000.0F;
    case 6:
      return paramFloat * getBBox(1) / 1000.0F;
    case 7:
      return paramFloat * getBBox(2) / 1000.0F;
    case 8:
      return paramFloat * getBBox(3) / 1000.0F;
    case 11:
      return 0.0F;
    case 12:
      return paramFloat * (getBBox(2) - getBBox(0)) / 1000.0F;
    }
    return 0.0F;
  }

  public String getPostscriptFontName()
  {
    return this.fontName;
  }

  public String[][] getFullFontName()
  {
    return new String[][] { { "", "", "", this.fontName } };
  }

  public String[][] getAllNameEntries()
  {
    return new String[][] { { "4", "", "", "", this.fontName } };
  }

  public String[][] getFamilyFontName()
  {
    return getFullFontName();
  }

  static char[] readCMap(String paramString)
  {
    try
    {
      paramString = paramString + ".cmap";
      InputStream localInputStream = getResourceStream("com/lowagie/text/pdf/fonts/" + paramString);
      char[] arrayOfChar = new char[65536];
      for (int i = 0; i < 65536; i++)
        arrayOfChar[i] = (char)((localInputStream.read() << 8) + localInputStream.read());
      localInputStream.close();
      return arrayOfChar;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  static IntHashtable createMetric(String paramString)
  {
    IntHashtable localIntHashtable = new IntHashtable();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    while (localStringTokenizer.hasMoreTokens())
    {
      int i = Integer.parseInt(localStringTokenizer.nextToken());
      localIntHashtable.put(i, Integer.parseInt(localStringTokenizer.nextToken()));
    }
    return localIntHashtable;
  }

  static String convertToHCIDMetrics(int[] paramArrayOfInt, IntHashtable paramIntHashtable)
  {
    if (paramArrayOfInt.length == 0)
      return null;
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramArrayOfInt.length; k++)
    {
      i = paramArrayOfInt[k];
      j = paramIntHashtable.get(i);
      if (j == 0)
        continue;
      k++;
      break;
    }
    if (j == 0)
      return null;
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('[');
    localStringBuffer.append(i);
    int m = 0;
    for (int n = k; n < paramArrayOfInt.length; n++)
    {
      int i1 = paramArrayOfInt[n];
      int i2 = paramIntHashtable.get(i1);
      if (i2 == 0)
        continue;
      switch (m)
      {
      case 0:
        if ((i1 == i + 1) && (i2 == j))
        {
          m = 2;
        }
        else if (i1 == i + 1)
        {
          m = 1;
          localStringBuffer.append('[').append(j);
        }
        else
        {
          localStringBuffer.append('[').append(j).append(']').append(i1);
        }
        break;
      case 1:
        if ((i1 == i + 1) && (i2 == j))
        {
          m = 2;
          localStringBuffer.append(']').append(i);
        }
        else if (i1 == i + 1)
        {
          localStringBuffer.append(' ').append(j);
        }
        else
        {
          m = 0;
          localStringBuffer.append(' ').append(j).append(']').append(i1);
        }
        break;
      case 2:
        if ((i1 == i + 1) && (i2 == j))
          break;
        localStringBuffer.append(' ').append(i).append(' ').append(j).append(' ').append(i1);
        m = 0;
      }
      j = i2;
      i = i1;
    }
    switch (m)
    {
    case 0:
      localStringBuffer.append('[').append(j).append("]]");
      break;
    case 1:
      localStringBuffer.append(' ').append(j).append("]]");
      break;
    case 2:
      localStringBuffer.append(' ').append(i).append(' ').append(j).append(']');
    }
    return localStringBuffer.toString();
  }

  static String convertToVCIDMetrics(int[] paramArrayOfInt, IntHashtable paramIntHashtable1, IntHashtable paramIntHashtable2)
  {
    if (paramArrayOfInt.length == 0)
      return null;
    int i = 0;
    int j = 0;
    int k = 0;
    for (int m = 0; m < paramArrayOfInt.length; m++)
    {
      i = paramArrayOfInt[m];
      j = paramIntHashtable1.get(i);
      if (j != 0)
      {
        m++;
        break;
      }
      k = paramIntHashtable2.get(i);
    }
    if (j == 0)
      return null;
    if (k == 0)
      k = 1000;
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('[');
    localStringBuffer.append(i);
    int n = 0;
    for (int i1 = m; i1 < paramArrayOfInt.length; i1++)
    {
      int i2 = paramArrayOfInt[i1];
      int i3 = paramIntHashtable1.get(i2);
      if (i3 == 0)
        continue;
      int i4 = paramIntHashtable2.get(i);
      if (i4 == 0)
        i4 = 1000;
      switch (n)
      {
      case 0:
        if ((i2 == i + 1) && (i3 == j) && (i4 == k))
          n = 2;
        else
          localStringBuffer.append(' ').append(i).append(' ').append(-j).append(' ').append(k / 2).append(' ').append(880).append(' ').append(i2);
        break;
      case 2:
        if ((i2 == i + 1) && (i3 == j) && (i4 == k))
          break;
        localStringBuffer.append(' ').append(i).append(' ').append(-j).append(' ').append(k / 2).append(' ').append(880).append(' ').append(i2);
        n = 0;
      }
      j = i3;
      i = i2;
      k = i4;
    }
    localStringBuffer.append(' ').append(i).append(' ').append(-j).append(' ').append(k / 2).append(' ').append(880).append(" ]");
    return localStringBuffer.toString();
  }

  static HashMap readFontProperties(String paramString)
  {
    try
    {
      paramString = paramString + ".properties";
      InputStream localInputStream = getResourceStream("com/lowagie/text/pdf/fonts/" + paramString);
      Properties localProperties = new Properties();
      localProperties.load(localInputStream);
      localInputStream.close();
      IntHashtable localIntHashtable1 = createMetric(localProperties.getProperty("W"));
      localProperties.remove("W");
      IntHashtable localIntHashtable2 = createMetric(localProperties.getProperty("W2"));
      localProperties.remove("W2");
      HashMap localHashMap = new HashMap();
      Enumeration localEnumeration = localProperties.keys();
      while (localEnumeration.hasMoreElements())
      {
        Object localObject = localEnumeration.nextElement();
        localHashMap.put(localObject, localProperties.getProperty((String)localObject));
      }
      localHashMap.put("W", localIntHashtable1);
      localHashMap.put("W2", localIntHashtable2);
      return localHashMap;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public int getUnicodeEquivalent(int paramInt)
  {
    if (this.cidDirect)
      return this.translationMap[paramInt];
    return paramInt;
  }

  public int getCidCode(int paramInt)
  {
    if (this.cidDirect)
      return paramInt;
    return this.translationMap[paramInt];
  }

  public boolean hasKernPairs()
  {
    return false;
  }

  public boolean charExists(int paramInt)
  {
    return this.translationMap[paramInt] != 0;
  }

  public boolean setCharAdvance(int paramInt1, int paramInt2)
  {
    return false;
  }

  public void setPostscriptFontName(String paramString)
  {
    this.fontName = paramString;
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
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.CJKFont
 * JD-Core Version:    0.6.0
 */