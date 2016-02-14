package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.StringTokenizer;

class Type1Font extends BaseFont
{
  private static FontsResourceAnchor resourceAnchor;
  protected byte[] pfb;
  private String FontName;
  private String FullName;
  private String FamilyName;
  private String Weight = "";
  private float ItalicAngle = 0.0F;
  private boolean IsFixedPitch = false;
  private String CharacterSet;
  private int llx = -50;
  private int lly = -200;
  private int urx = 1000;
  private int ury = 900;
  private int UnderlinePosition = -100;
  private int UnderlineThickness = 50;
  private String EncodingScheme = "FontSpecific";
  private int CapHeight = 700;
  private int XHeight = 480;
  private int Ascender = 800;
  private int Descender = -200;
  private int StdHW;
  private int StdVW = 80;
  private HashMap CharMetrics = new HashMap();
  private HashMap KernPairs = new HashMap();
  private String fileName;
  private boolean builtinFont = false;
  private static final int[] PFB_TYPES = { 1, 2, 1 };

  Type1Font(String paramString1, String paramString2, boolean paramBoolean1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean2)
    throws DocumentException, IOException
  {
    if ((paramBoolean1) && (paramArrayOfByte1 != null) && (paramArrayOfByte2 == null))
      throw new DocumentException("Two byte arrays are needed if the Type1 font is embedded.");
    if ((paramBoolean1) && (paramArrayOfByte1 != null))
      this.pfb = paramArrayOfByte2;
    this.encoding = paramString2;
    this.embedded = paramBoolean1;
    this.fileName = paramString1;
    this.fontType = 0;
    RandomAccessFileOrArray localRandomAccessFileOrArray = null;
    InputStream localInputStream = null;
    Object localObject1;
    if (BuiltinFonts14.containsKey(paramString1))
    {
      this.embedded = false;
      this.builtinFont = true;
      localObject1 = new byte[1024];
      try
      {
        if (resourceAnchor == null)
          resourceAnchor = new FontsResourceAnchor();
        localInputStream = getResourceStream("com/lowagie/text/pdf/fonts/" + paramString1 + ".afm", resourceAnchor.getClass().getClassLoader());
        if (localInputStream == null)
        {
          localObject2 = paramString1 + " not found as resource. (The *.afm files must exist as resources in the package com.lowagie.text.pdf.fonts)";
          System.err.println((String)localObject2);
          throw new DocumentException((String)localObject2);
        }
        Object localObject2 = new ByteArrayOutputStream();
        while (true)
        {
          int i = localInputStream.read(localObject1);
          if (i < 0)
            break;
          ((ByteArrayOutputStream)localObject2).write(localObject1, 0, i);
        }
        localObject1 = ((ByteArrayOutputStream)localObject2).toByteArray();
      }
      finally
      {
        if (localInputStream != null)
          try
          {
            localInputStream.close();
          }
          catch (Exception localException1)
          {
          }
      }
      try
      {
        localRandomAccessFileOrArray = new RandomAccessFileOrArray(localObject1);
        process(localRandomAccessFileOrArray);
      }
      finally
      {
        if (localRandomAccessFileOrArray != null)
          try
          {
            localRandomAccessFileOrArray.close();
          }
          catch (Exception localException2)
          {
          }
      }
    }
    else if (paramString1.toLowerCase().endsWith(".afm"))
    {
      try
      {
        if (paramArrayOfByte1 == null)
          localRandomAccessFileOrArray = new RandomAccessFileOrArray(paramString1, paramBoolean2, Document.plainRandomAccess);
        else
          localRandomAccessFileOrArray = new RandomAccessFileOrArray(paramArrayOfByte1);
        process(localRandomAccessFileOrArray);
      }
      finally
      {
        if (localRandomAccessFileOrArray != null)
          try
          {
            localRandomAccessFileOrArray.close();
          }
          catch (Exception localException3)
          {
          }
      }
    }
    else if (paramString1.toLowerCase().endsWith(".pfm"))
    {
      try
      {
        localObject1 = new ByteArrayOutputStream();
        if (paramArrayOfByte1 == null)
          localRandomAccessFileOrArray = new RandomAccessFileOrArray(paramString1, paramBoolean2, Document.plainRandomAccess);
        else
          localRandomAccessFileOrArray = new RandomAccessFileOrArray(paramArrayOfByte1);
        Pfm2afm.convert(localRandomAccessFileOrArray, (OutputStream)localObject1);
        localRandomAccessFileOrArray.close();
        localRandomAccessFileOrArray = new RandomAccessFileOrArray(((ByteArrayOutputStream)localObject1).toByteArray());
        process(localRandomAccessFileOrArray);
      }
      finally
      {
        if (localRandomAccessFileOrArray != null)
          try
          {
            localRandomAccessFileOrArray.close();
          }
          catch (Exception localException4)
          {
          }
      }
    }
    else
    {
      throw new DocumentException(paramString1 + " is not an AFM or PFM font file.");
    }
    this.EncodingScheme = this.EncodingScheme.trim();
    if ((this.EncodingScheme.equals("AdobeStandardEncoding")) || (this.EncodingScheme.equals("StandardEncoding")))
      this.fontSpecific = false;
    if (!this.encoding.startsWith("#"))
      PdfEncodings.convertToBytes(" ", paramString2);
    createEncoding();
  }

  int getRawWidth(int paramInt, String paramString)
  {
    Object[] arrayOfObject;
    if (paramString == null)
    {
      arrayOfObject = (Object[])this.CharMetrics.get(new Integer(paramInt));
    }
    else
    {
      if (paramString.equals(".notdef"))
        return 0;
      arrayOfObject = (Object[])this.CharMetrics.get(paramString);
    }
    if (arrayOfObject != null)
      return ((Integer)arrayOfObject[1]).intValue();
    return 0;
  }

  public int getKerning(int paramInt1, int paramInt2)
  {
    String str1 = GlyphList.unicodeToName(paramInt1);
    if (str1 == null)
      return 0;
    String str2 = GlyphList.unicodeToName(paramInt2);
    if (str2 == null)
      return 0;
    Object[] arrayOfObject = (Object[])this.KernPairs.get(str1);
    if (arrayOfObject == null)
      return 0;
    for (int i = 0; i < arrayOfObject.length; i += 2)
      if (str2.equals(arrayOfObject[i]))
        return ((Integer)arrayOfObject[(i + 1)]).intValue();
    return 0;
  }

  public void process(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws DocumentException, IOException
  {
    int i = 0;
    String str1;
    Object localObject1;
    String str2;
    while ((str1 = paramRandomAccessFileOrArray.readLine()) != null)
    {
      localObject1 = new StringTokenizer(str1, " ,\n\r\t\f");
      if (!((StringTokenizer)localObject1).hasMoreTokens())
        continue;
      str2 = ((StringTokenizer)localObject1).nextToken();
      if (str2.equals("FontName"))
      {
        this.FontName = ((StringTokenizer)localObject1).nextToken("ÿ").substring(1);
        continue;
      }
      if (str2.equals("FullName"))
      {
        this.FullName = ((StringTokenizer)localObject1).nextToken("ÿ").substring(1);
        continue;
      }
      if (str2.equals("FamilyName"))
      {
        this.FamilyName = ((StringTokenizer)localObject1).nextToken("ÿ").substring(1);
        continue;
      }
      if (str2.equals("Weight"))
      {
        this.Weight = ((StringTokenizer)localObject1).nextToken("ÿ").substring(1);
        continue;
      }
      if (str2.equals("ItalicAngle"))
      {
        this.ItalicAngle = Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("IsFixedPitch"))
      {
        this.IsFixedPitch = ((StringTokenizer)localObject1).nextToken().equals("true");
        continue;
      }
      if (str2.equals("CharacterSet"))
      {
        this.CharacterSet = ((StringTokenizer)localObject1).nextToken("ÿ").substring(1);
        continue;
      }
      if (str2.equals("FontBBox"))
      {
        this.llx = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        this.lly = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        this.urx = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        this.ury = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("UnderlinePosition"))
      {
        this.UnderlinePosition = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("UnderlineThickness"))
      {
        this.UnderlineThickness = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("EncodingScheme"))
      {
        this.EncodingScheme = ((StringTokenizer)localObject1).nextToken("ÿ").substring(1);
        continue;
      }
      if (str2.equals("CapHeight"))
      {
        this.CapHeight = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("XHeight"))
      {
        this.XHeight = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("Ascender"))
      {
        this.Ascender = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("Descender"))
      {
        this.Descender = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("StdHW"))
      {
        this.StdHW = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (str2.equals("StdVW"))
      {
        this.StdVW = (int)Float.parseFloat(((StringTokenizer)localObject1).nextToken());
        continue;
      }
      if (!str2.equals("StartCharMetrics"))
        continue;
      i = 1;
    }
    if (i == 0)
      throw new DocumentException("Missing StartCharMetrics in " + this.fileName);
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    while ((str1 = paramRandomAccessFileOrArray.readLine()) != null)
    {
      localObject1 = new StringTokenizer(str1);
      if (!((StringTokenizer)localObject1).hasMoreTokens())
        continue;
      str2 = ((StringTokenizer)localObject1).nextToken();
      if (str2.equals("EndCharMetrics"))
      {
        i = 0;
        break;
      }
      localObject2 = new Integer(-1);
      localObject3 = new Integer(250);
      localObject4 = "";
      localObject5 = null;
      localObject1 = new StringTokenizer(str1, ";");
      while (((StringTokenizer)localObject1).hasMoreTokens())
      {
        localObject6 = new StringTokenizer(((StringTokenizer)localObject1).nextToken());
        if (!((StringTokenizer)localObject6).hasMoreTokens())
          continue;
        str2 = ((StringTokenizer)localObject6).nextToken();
        if (str2.equals("C"))
        {
          localObject2 = Integer.valueOf(((StringTokenizer)localObject6).nextToken());
          continue;
        }
        if (str2.equals("WX"))
        {
          localObject3 = new Integer((int)Float.parseFloat(((StringTokenizer)localObject6).nextToken()));
          continue;
        }
        if (str2.equals("N"))
        {
          localObject4 = ((StringTokenizer)localObject6).nextToken();
          continue;
        }
        if (!str2.equals("B"))
          continue;
        localObject5 = new int[] { Integer.parseInt(((StringTokenizer)localObject6).nextToken()), Integer.parseInt(((StringTokenizer)localObject6).nextToken()), Integer.parseInt(((StringTokenizer)localObject6).nextToken()), Integer.parseInt(((StringTokenizer)localObject6).nextToken()) };
      }
      Object localObject6 = { localObject2, localObject3, localObject4, localObject5 };
      if (((Integer)localObject2).intValue() >= 0)
        this.CharMetrics.put(localObject2, localObject6);
      this.CharMetrics.put(localObject4, localObject6);
    }
    if (i != 0)
      throw new DocumentException("Missing EndCharMetrics in " + this.fileName);
    if (!this.CharMetrics.containsKey("nonbreakingspace"))
    {
      localObject1 = (Object[])this.CharMetrics.get("space");
      if (localObject1 != null)
        this.CharMetrics.put("nonbreakingspace", localObject1);
    }
    while ((str1 = paramRandomAccessFileOrArray.readLine()) != null)
    {
      localObject1 = new StringTokenizer(str1);
      if (!((StringTokenizer)localObject1).hasMoreTokens())
        continue;
      str2 = ((StringTokenizer)localObject1).nextToken();
      if (str2.equals("EndFontMetrics"))
        return;
      if (!str2.equals("StartKernPairs"))
        continue;
      i = 1;
    }
    if (i == 0)
      throw new DocumentException("Missing EndFontMetrics in " + this.fileName);
    while ((str1 = paramRandomAccessFileOrArray.readLine()) != null)
    {
      localObject1 = new StringTokenizer(str1);
      if (!((StringTokenizer)localObject1).hasMoreTokens())
        continue;
      str2 = ((StringTokenizer)localObject1).nextToken();
      if (str2.equals("KPX"))
      {
        localObject2 = ((StringTokenizer)localObject1).nextToken();
        localObject3 = ((StringTokenizer)localObject1).nextToken();
        localObject4 = new Integer((int)Float.parseFloat(((StringTokenizer)localObject1).nextToken()));
        localObject5 = (Object[])this.KernPairs.get(localObject2);
        if (localObject5 == null)
        {
          this.KernPairs.put(localObject2, new Object[] { localObject3, localObject4 });
          continue;
        }
        int j = localObject5.length;
        Object[] arrayOfObject = new Object[j + 2];
        System.arraycopy(localObject5, 0, arrayOfObject, 0, j);
        arrayOfObject[j] = localObject3;
        arrayOfObject[(j + 1)] = localObject4;
        this.KernPairs.put(localObject2, arrayOfObject);
        continue;
      }
      if (!str2.equals("EndKernPairs"))
        continue;
      i = 0;
    }
    if (i != 0)
      throw new DocumentException("Missing EndKernPairs in " + this.fileName);
    paramRandomAccessFileOrArray.close();
  }

  public PdfStream getFullFontStream()
    throws DocumentException
  {
    if ((this.builtinFont) || (!this.embedded))
      return null;
    RandomAccessFileOrArray localRandomAccessFileOrArray = null;
    try
    {
      String str = this.fileName.substring(0, this.fileName.length() - 3) + "pfb";
      if (this.pfb == null)
        localRandomAccessFileOrArray = new RandomAccessFileOrArray(str, true, Document.plainRandomAccess);
      else
        localRandomAccessFileOrArray = new RandomAccessFileOrArray(this.pfb);
      int i = localRandomAccessFileOrArray.length();
      byte[] arrayOfByte = new byte[i - 18];
      int[] arrayOfInt = new int[3];
      int j = 0;
      for (int k = 0; k < 3; k++)
      {
        if (localRandomAccessFileOrArray.read() != 128)
          throw new DocumentException("Start marker missing in " + str);
        if (localRandomAccessFileOrArray.read() != PFB_TYPES[k])
          throw new DocumentException("Incorrect segment type in " + str);
        int m = localRandomAccessFileOrArray.read();
        m += (localRandomAccessFileOrArray.read() << 8);
        m += (localRandomAccessFileOrArray.read() << 16);
        m += (localRandomAccessFileOrArray.read() << 24);
        arrayOfInt[k] = m;
        while (m != 0)
        {
          int n = localRandomAccessFileOrArray.read(arrayOfByte, j, m);
          if (n < 0)
            throw new DocumentException("Premature end in " + str);
          j += n;
          m -= n;
        }
      }
      localStreamFont = new BaseFont.StreamFont(arrayOfByte, arrayOfInt, this.compressionLevel);
    }
    catch (Exception localException1)
    {
      BaseFont.StreamFont localStreamFont;
      throw new DocumentException(localException1);
    }
    finally
    {
      if (localRandomAccessFileOrArray != null)
        try
        {
          localRandomAccessFileOrArray.close();
        }
        catch (Exception localException2)
        {
        }
    }
  }

  private PdfDictionary getFontDescriptor(PdfIndirectReference paramPdfIndirectReference)
  {
    if (this.builtinFont)
      return null;
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.FONTDESCRIPTOR);
    localPdfDictionary.put(PdfName.ASCENT, new PdfNumber(this.Ascender));
    localPdfDictionary.put(PdfName.CAPHEIGHT, new PdfNumber(this.CapHeight));
    localPdfDictionary.put(PdfName.DESCENT, new PdfNumber(this.Descender));
    localPdfDictionary.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
    localPdfDictionary.put(PdfName.FONTNAME, new PdfName(this.FontName));
    localPdfDictionary.put(PdfName.ITALICANGLE, new PdfNumber(this.ItalicAngle));
    localPdfDictionary.put(PdfName.STEMV, new PdfNumber(this.StdVW));
    if (paramPdfIndirectReference != null)
      localPdfDictionary.put(PdfName.FONTFILE, paramPdfIndirectReference);
    int i = 0;
    if (this.IsFixedPitch)
      i |= 1;
    i |= (this.fontSpecific ? 4 : 32);
    if (this.ItalicAngle < 0.0F)
      i |= 64;
    if ((this.FontName.indexOf("Caps") >= 0) || (this.FontName.endsWith("SC")))
      i |= 131072;
    if (this.Weight.equals("Bold"))
      i |= 262144;
    localPdfDictionary.put(PdfName.FLAGS, new PdfNumber(i));
    return localPdfDictionary;
  }

  private PdfDictionary getFontBaseType(PdfIndirectReference paramPdfIndirectReference, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.FONT);
    localPdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE1);
    localPdfDictionary.put(PdfName.BASEFONT, new PdfName(this.FontName));
    int i = (this.encoding.equals("Cp1252")) || (this.encoding.equals("MacRoman")) ? 1 : 0;
    Object localObject;
    if ((!this.fontSpecific) || (this.specialMap != null))
    {
      for (int j = paramInt1; j <= paramInt2; j++)
      {
        if (this.differences[j].equals(".notdef"))
          continue;
        paramInt1 = j;
        break;
      }
      if (i != 0)
      {
        localPdfDictionary.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
      }
      else
      {
        localObject = new PdfDictionary(PdfName.ENCODING);
        PdfArray localPdfArray = new PdfArray();
        int m = 1;
        for (int n = paramInt1; n <= paramInt2; n++)
          if (paramArrayOfByte[n] != 0)
          {
            if (m != 0)
            {
              localPdfArray.add(new PdfNumber(n));
              m = 0;
            }
            localPdfArray.add(new PdfName(this.differences[n]));
          }
          else
          {
            m = 1;
          }
        ((PdfDictionary)localObject).put(PdfName.DIFFERENCES, localPdfArray);
        localPdfDictionary.put(PdfName.ENCODING, (PdfObject)localObject);
      }
    }
    if ((this.specialMap != null) || (this.forceWidthsOutput) || (!this.builtinFont) || ((!this.fontSpecific) && (i == 0)))
    {
      localPdfDictionary.put(PdfName.FIRSTCHAR, new PdfNumber(paramInt1));
      localPdfDictionary.put(PdfName.LASTCHAR, new PdfNumber(paramInt2));
      localObject = new PdfArray();
      for (int k = paramInt1; k <= paramInt2; k++)
        if (paramArrayOfByte[k] == 0)
          ((PdfArray)localObject).add(new PdfNumber(0));
        else
          ((PdfArray)localObject).add(new PdfNumber(this.widths[k]));
      localPdfDictionary.put(PdfName.WIDTHS, (PdfObject)localObject);
    }
    if ((!this.builtinFont) && (paramPdfIndirectReference != null))
      localPdfDictionary.put(PdfName.FONTDESCRIPTOR, paramPdfIndirectReference);
    return (PdfDictionary)localPdfDictionary;
  }

  void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException
  {
    int i = ((Integer)paramArrayOfObject[0]).intValue();
    int j = ((Integer)paramArrayOfObject[1]).intValue();
    byte[] arrayOfByte = (byte[])paramArrayOfObject[2];
    int k = (((Boolean)paramArrayOfObject[3]).booleanValue()) && (this.subset) ? 1 : 0;
    if (k == 0)
    {
      i = 0;
      j = arrayOfByte.length - 1;
      for (int m = 0; m < arrayOfByte.length; m++)
        arrayOfByte[m] = 1;
    }
    PdfIndirectReference localPdfIndirectReference = null;
    Object localObject = null;
    PdfIndirectObject localPdfIndirectObject = null;
    localObject = getFullFontStream();
    if (localObject != null)
    {
      localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject);
      localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
    }
    localObject = getFontDescriptor(localPdfIndirectReference);
    if (localObject != null)
    {
      localPdfIndirectObject = paramPdfWriter.addToBody((PdfObject)localObject);
      localPdfIndirectReference = localPdfIndirectObject.getIndirectReference();
    }
    localObject = getFontBaseType(localPdfIndirectReference, i, j, arrayOfByte);
    paramPdfWriter.addToBody((PdfObject)localObject, paramPdfIndirectReference);
  }

  public float getFontDescriptor(int paramInt, float paramFloat)
  {
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
    case 13:
      return this.UnderlinePosition * paramFloat / 1000.0F;
    case 14:
      return this.UnderlineThickness * paramFloat / 1000.0F;
    }
    return 0.0F;
  }

  public String getPostscriptFontName()
  {
    return this.FontName;
  }

  public String[][] getFullFontName()
  {
    return new String[][] { { "", "", "", this.FullName } };
  }

  public String[][] getAllNameEntries()
  {
    return new String[][] { { "4", "", "", "", this.FullName } };
  }

  public String[][] getFamilyFontName()
  {
    return new String[][] { { "", "", "", this.FamilyName } };
  }

  public boolean hasKernPairs()
  {
    return !this.KernPairs.isEmpty();
  }

  public void setPostscriptFontName(String paramString)
  {
    this.FontName = paramString;
  }

  public boolean setKerning(int paramInt1, int paramInt2, int paramInt3)
  {
    String str1 = GlyphList.unicodeToName(paramInt1);
    if (str1 == null)
      return false;
    String str2 = GlyphList.unicodeToName(paramInt2);
    if (str2 == null)
      return false;
    Object[] arrayOfObject1 = (Object[])this.KernPairs.get(str1);
    if (arrayOfObject1 == null)
    {
      arrayOfObject1 = new Object[] { str2, new Integer(paramInt3) };
      this.KernPairs.put(str1, arrayOfObject1);
      return true;
    }
    for (int i = 0; i < arrayOfObject1.length; i += 2)
    {
      if (!str2.equals(arrayOfObject1[i]))
        continue;
      arrayOfObject1[(i + 1)] = new Integer(paramInt3);
      return true;
    }
    i = arrayOfObject1.length;
    Object[] arrayOfObject2 = new Object[i + 2];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, i);
    arrayOfObject2[i] = str2;
    arrayOfObject2[(i + 1)] = new Integer(paramInt3);
    this.KernPairs.put(str1, arrayOfObject2);
    return true;
  }

  protected int[] getRawCharBBox(int paramInt, String paramString)
  {
    Object[] arrayOfObject;
    if (paramString == null)
    {
      arrayOfObject = (Object[])this.CharMetrics.get(new Integer(paramInt));
    }
    else
    {
      if (paramString.equals(".notdef"))
        return null;
      arrayOfObject = (Object[])this.CharMetrics.get(paramString);
    }
    if (arrayOfObject != null)
      return (int[])arrayOfObject[3];
    return null;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.Type1Font
 * JD-Core Version:    0.6.0
 */