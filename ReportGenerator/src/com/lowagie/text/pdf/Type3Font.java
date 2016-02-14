package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.util.HashMap;

public class Type3Font extends BaseFont
{
  private boolean[] usedSlot;
  private IntHashtable widths3 = new IntHashtable();
  private HashMap char2glyph = new HashMap();
  private PdfWriter writer;
  private float llx = (0.0F / 0.0F);
  private float lly;
  private float urx;
  private float ury;
  private PageResources pageResources = new PageResources();
  private boolean colorized;

  public Type3Font(PdfWriter paramPdfWriter, char[] paramArrayOfChar, boolean paramBoolean)
  {
    this(paramPdfWriter, paramBoolean);
  }

  public Type3Font(PdfWriter paramPdfWriter, boolean paramBoolean)
  {
    this.writer = paramPdfWriter;
    this.colorized = paramBoolean;
    this.fontType = 5;
    this.usedSlot = new boolean[256];
  }

  public PdfContentByte defineGlyph(char paramChar, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    if ((paramChar == 0) || (paramChar > 'ÿ'))
      throw new IllegalArgumentException("The char " + paramChar + " doesn't belong in this Type3 font");
    this.usedSlot[paramChar] = true;
    Integer localInteger = new Integer(paramChar);
    Type3Glyph localType3Glyph = (Type3Glyph)this.char2glyph.get(localInteger);
    if (localType3Glyph != null)
      return localType3Glyph;
    this.widths3.put(paramChar, (int)paramFloat1);
    if (!this.colorized)
      if (Float.isNaN(this.llx))
      {
        this.llx = paramFloat2;
        this.lly = paramFloat3;
        this.urx = paramFloat4;
        this.ury = paramFloat5;
      }
      else
      {
        this.llx = Math.min(this.llx, paramFloat2);
        this.lly = Math.min(this.lly, paramFloat3);
        this.urx = Math.max(this.urx, paramFloat4);
        this.ury = Math.max(this.ury, paramFloat5);
      }
    localType3Glyph = new Type3Glyph(this.writer, this.pageResources, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, this.colorized);
    this.char2glyph.put(localInteger, localType3Glyph);
    return localType3Glyph;
  }

  public String[][] getFamilyFontName()
  {
    return getFullFontName();
  }

  public float getFontDescriptor(int paramInt, float paramFloat)
  {
    return 0.0F;
  }

  public String[][] getFullFontName()
  {
    return new String[][] { { "", "", "", "" } };
  }

  public String[][] getAllNameEntries()
  {
    return new String[][] { { "4", "", "", "", "" } };
  }

  public int getKerning(int paramInt1, int paramInt2)
  {
    return 0;
  }

  public String getPostscriptFontName()
  {
    return "";
  }

  protected int[] getRawCharBBox(int paramInt, String paramString)
  {
    return null;
  }

  int getRawWidth(int paramInt, String paramString)
  {
    return 0;
  }

  public boolean hasKernPairs()
  {
    return false;
  }

  public boolean setKerning(int paramInt1, int paramInt2, int paramInt3)
  {
    return false;
  }

  public void setPostscriptFontName(String paramString)
  {
  }

  void writeFont(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, Object[] paramArrayOfObject)
    throws DocumentException, IOException
  {
    if (this.writer != paramPdfWriter)
      throw new IllegalArgumentException("Type3 font used with the wrong PdfWriter");
    for (int i = 0; (i < this.usedSlot.length) && (this.usedSlot[i] == 0); i++);
    if (i == this.usedSlot.length)
      throw new DocumentException("No glyphs defined for Type3 font");
    for (int j = this.usedSlot.length - 1; (j >= i) && (this.usedSlot[j] == 0); j--);
    int[] arrayOfInt1 = new int[j - i + 1];
    int[] arrayOfInt2 = new int[j - i + 1];
    int k = 0;
    int m = 0;
    int n = i;
    while (n <= j)
    {
      if (this.usedSlot[n] != 0)
      {
        arrayOfInt2[(k++)] = n;
        arrayOfInt1[m] = this.widths3.get(n);
      }
      n++;
      m++;
    }
    PdfArray localPdfArray = new PdfArray();
    PdfDictionary localPdfDictionary1 = new PdfDictionary();
    int i1 = -1;
    for (int i2 = 0; i2 < k; i2++)
    {
      int i3 = arrayOfInt2[i2];
      if (i3 > i1)
      {
        i1 = i3;
        localPdfArray.add(new PdfNumber(i1));
      }
      i1++;
      int i4 = arrayOfInt2[i2];
      String str = GlyphList.unicodeToName(i4);
      if (str == null)
        str = "a" + i4;
      PdfName localPdfName = new PdfName(str);
      localPdfArray.add(localPdfName);
      Type3Glyph localType3Glyph = (Type3Glyph)this.char2glyph.get(new Integer(i4));
      PdfStream localPdfStream = new PdfStream(localType3Glyph.toPdf(null));
      localPdfStream.flateCompress(this.compressionLevel);
      PdfIndirectReference localPdfIndirectReference = paramPdfWriter.addToBody(localPdfStream).getIndirectReference();
      localPdfDictionary1.put(localPdfName, localPdfIndirectReference);
    }
    PdfDictionary localPdfDictionary2 = new PdfDictionary(PdfName.FONT);
    localPdfDictionary2.put(PdfName.SUBTYPE, PdfName.TYPE3);
    if (this.colorized)
      localPdfDictionary2.put(PdfName.FONTBBOX, new PdfRectangle(0.0F, 0.0F, 0.0F, 0.0F));
    else
      localPdfDictionary2.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
    localPdfDictionary2.put(PdfName.FONTMATRIX, new PdfArray(new float[] { 0.001F, 0.0F, 0.0F, 0.001F, 0.0F, 0.0F }));
    localPdfDictionary2.put(PdfName.CHARPROCS, paramPdfWriter.addToBody(localPdfDictionary1).getIndirectReference());
    PdfDictionary localPdfDictionary3 = new PdfDictionary();
    localPdfDictionary3.put(PdfName.DIFFERENCES, localPdfArray);
    localPdfDictionary2.put(PdfName.ENCODING, paramPdfWriter.addToBody(localPdfDictionary3).getIndirectReference());
    localPdfDictionary2.put(PdfName.FIRSTCHAR, new PdfNumber(i));
    localPdfDictionary2.put(PdfName.LASTCHAR, new PdfNumber(j));
    localPdfDictionary2.put(PdfName.WIDTHS, paramPdfWriter.addToBody(new PdfArray(arrayOfInt1)).getIndirectReference());
    if (this.pageResources.hasResources())
      localPdfDictionary2.put(PdfName.RESOURCES, paramPdfWriter.addToBody(this.pageResources.getResources()).getIndirectReference());
    paramPdfWriter.addToBody(localPdfDictionary2, paramPdfIndirectReference);
  }

  public PdfStream getFullFontStream()
  {
    return null;
  }

  byte[] convertToBytes(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte1 = new byte[arrayOfChar.length];
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
    {
      int k = arrayOfChar[j];
      if (!charExists(k))
        continue;
      arrayOfByte1[(i++)] = (byte)k;
    }
    if (arrayOfByte1.length == i)
      return arrayOfByte1;
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }

  byte[] convertToBytes(int paramInt)
  {
    if (charExists(paramInt))
      return new byte[] { (byte)paramInt };
    return new byte[0];
  }

  public int getWidth(int paramInt)
  {
    if (!this.widths3.containsKey(paramInt))
      throw new IllegalArgumentException("The char " + paramInt + " is not defined in a Type3 font");
    return this.widths3.get(paramInt);
  }

  public int getWidth(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      i += getWidth(arrayOfChar[j]);
    return i;
  }

  public int[] getCharBBox(int paramInt)
  {
    return null;
  }

  public boolean charExists(int paramInt)
  {
    if ((paramInt > 0) && (paramInt < 256))
      return this.usedSlot[paramInt];
    return false;
  }

  public boolean setCharAdvance(int paramInt1, int paramInt2)
  {
    return false;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.Type3Font
 * JD-Core Version:    0.6.0
 */