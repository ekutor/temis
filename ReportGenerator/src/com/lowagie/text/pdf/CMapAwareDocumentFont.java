package com.lowagie.text.pdf;

import com.lowagie.text.pdf.fonts.cmaps.CMap;
import com.lowagie.text.pdf.fonts.cmaps.CMapParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CMapAwareDocumentFont extends DocumentFont
{
  private PdfDictionary fontDic;
  private int spaceWidth;
  private CMap toUnicodeCmap;
  private char[] cidbyte2uni;

  public CMapAwareDocumentFont(PRIndirectReference paramPRIndirectReference)
  {
    super(paramPRIndirectReference);
    this.fontDic = ((PdfDictionary)PdfReader.getPdfObjectRelease(paramPRIndirectReference));
    processToUnicode();
    if (this.toUnicodeCmap == null)
      processUni2Byte();
    this.spaceWidth = super.getWidth(32);
    if (this.spaceWidth == 0)
      this.spaceWidth = computeAverageWidth();
  }

  private void processToUnicode()
  {
    PdfObject localPdfObject = this.fontDic.get(PdfName.TOUNICODE);
    if (localPdfObject != null)
      try
      {
        byte[] arrayOfByte = PdfReader.getStreamBytes((PRStream)PdfReader.getPdfObjectRelease(localPdfObject));
        CMapParser localCMapParser = new CMapParser();
        this.toUnicodeCmap = localCMapParser.parse(new ByteArrayInputStream(arrayOfByte));
      }
      catch (IOException localIOException)
      {
        throw new Error("Unable to process ToUnicode map - " + localIOException.getMessage(), localIOException);
      }
  }

  private void processUni2Byte()
  {
    IntHashtable localIntHashtable = getUni2Byte();
    int[] arrayOfInt = localIntHashtable.toOrderedKeys();
    this.cidbyte2uni = new char[256];
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      int j = localIntHashtable.get(arrayOfInt[i]);
      if (this.cidbyte2uni[j] != 0)
        continue;
      this.cidbyte2uni[j] = (char)arrayOfInt[i];
    }
  }

  private int computeAverageWidth()
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < this.widths.length; k++)
    {
      if (this.widths[k] == 0)
        continue;
      j += this.widths[k];
      i++;
    }
    return i != 0 ? j / i : 0;
  }

  public int getWidth(int paramInt)
  {
    if (paramInt == 32)
      return this.spaceWidth;
    return super.getWidth(paramInt);
  }

  private String decodeSingleCID(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.toUnicodeCmap != null)
    {
      if (paramInt1 + paramInt2 > paramArrayOfByte.length)
        throw new ArrayIndexOutOfBoundsException("Invalid index: " + paramInt1 + paramInt2);
      return this.toUnicodeCmap.lookup(paramArrayOfByte, paramInt1, paramInt2);
    }
    if (paramInt2 == 1)
      return new String(this.cidbyte2uni, 0xFF & paramArrayOfByte[paramInt1], 1);
    throw new Error("Multi-byte glyphs not implemented yet");
  }

  public String decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
    {
      String str = decodeSingleCID(paramArrayOfByte, i, 1);
      if (str == null)
      {
        str = decodeSingleCID(paramArrayOfByte, i, 2);
        i++;
      }
      localStringBuffer.append(str);
    }
    return localStringBuffer.toString();
  }

  /** @deprecated */
  public String encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return decode(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.CMapAwareDocumentFont
 * JD-Core Version:    0.6.0
 */