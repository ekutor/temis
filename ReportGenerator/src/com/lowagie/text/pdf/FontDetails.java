package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Utilities;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

class FontDetails
{
  PdfIndirectReference indirectReference;
  PdfName fontName;
  BaseFont baseFont;
  TrueTypeFontUnicode ttu;
  CJKFont cjkFont;
  byte[] shortTag;
  HashMap longTag;
  IntHashtable cjkTag;
  int fontType;
  boolean symbolic;
  protected boolean subset = true;

  FontDetails(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference, BaseFont paramBaseFont)
  {
    this.fontName = paramPdfName;
    this.indirectReference = paramPdfIndirectReference;
    this.baseFont = paramBaseFont;
    this.fontType = paramBaseFont.getFontType();
    switch (this.fontType)
    {
    case 0:
    case 1:
      this.shortTag = new byte[256];
      break;
    case 2:
      this.cjkTag = new IntHashtable();
      this.cjkFont = ((CJKFont)paramBaseFont);
      break;
    case 3:
      this.longTag = new HashMap();
      this.ttu = ((TrueTypeFontUnicode)paramBaseFont);
      this.symbolic = paramBaseFont.isFontSpecific();
    }
  }

  PdfIndirectReference getIndirectReference()
  {
    return this.indirectReference;
  }

  PdfName getFontName()
  {
    return this.fontName;
  }

  BaseFont getBaseFont()
  {
    return this.baseFont;
  }

  byte[] convertToBytes(String paramString)
  {
    byte[] arrayOfByte = null;
    int i;
    int j;
    switch (this.fontType)
    {
    case 5:
      return this.baseFont.convertToBytes(paramString);
    case 0:
    case 1:
      arrayOfByte = this.baseFont.convertToBytes(paramString);
      i = arrayOfByte.length;
      j = 0;
    case 2:
    case 4:
    case 3:
      while (j < i)
      {
        this.shortTag[(arrayOfByte[j] & 0xFF)] = 1;
        j++;
        continue;
        i = paramString.length();
        for (j = 0; j < i; j++)
          this.cjkTag.put(this.cjkFont.getCidCode(paramString.charAt(j)), 0);
        arrayOfByte = this.baseFont.convertToBytes(paramString);
        break;
        arrayOfByte = this.baseFont.convertToBytes(paramString);
        break;
        try
        {
          i = paramString.length();
          int[] arrayOfInt = null;
          char[] arrayOfChar = new char[i];
          int k = 0;
          if (this.symbolic)
          {
            arrayOfByte = PdfEncodings.convertToBytes(paramString, "symboltt");
            i = arrayOfByte.length;
            for (m = 0; m < i; m++)
            {
              arrayOfInt = this.ttu.getMetricsTT(arrayOfByte[m] & 0xFF);
              if (arrayOfInt == null)
                continue;
              this.longTag.put(new Integer(arrayOfInt[0]), new int[] { arrayOfInt[0], arrayOfInt[1], this.ttu.getUnicodeDifferences(arrayOfByte[m] & 0xFF) });
              arrayOfChar[(k++)] = (char)arrayOfInt[0];
            }
          }
          for (int m = 0; m < i; m++)
          {
            int n;
            if (Utilities.isSurrogatePair(paramString, m))
            {
              n = Utilities.convertToUtf32(paramString, m);
              m++;
            }
            else
            {
              n = paramString.charAt(m);
            }
            arrayOfInt = this.ttu.getMetricsTT(n);
            if (arrayOfInt == null)
              continue;
            int i1 = arrayOfInt[0];
            Integer localInteger = new Integer(i1);
            if (!this.longTag.containsKey(localInteger))
              this.longTag.put(localInteger, new int[] { i1, arrayOfInt[1], n });
            arrayOfChar[(k++)] = (char)i1;
          }
          String str = new String(arrayOfChar, 0, k);
          arrayOfByte = str.getBytes("UnicodeBigUnmarked");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new ExceptionConverter(localUnsupportedEncodingException);
        }
      }
    }
    return arrayOfByte;
  }

  void writeFont(PdfWriter paramPdfWriter)
  {
    try
    {
      switch (this.fontType)
      {
      case 5:
        this.baseFont.writeFont(paramPdfWriter, this.indirectReference, null);
        break;
      case 0:
      case 1:
        for (int i = 0; (i < 256) && (this.shortTag[i] == 0); i++);
        for (int j = 255; (j >= i) && (this.shortTag[j] == 0); j--);
        if (i > 255)
        {
          i = 255;
          j = 255;
        }
        this.baseFont.writeFont(paramPdfWriter, this.indirectReference, new Object[] { new Integer(i), new Integer(j), this.shortTag, Boolean.valueOf(this.subset) });
        break;
      case 2:
        this.baseFont.writeFont(paramPdfWriter, this.indirectReference, new Object[] { this.cjkTag });
        break;
      case 3:
        this.baseFont.writeFont(paramPdfWriter, this.indirectReference, new Object[] { this.longTag, Boolean.valueOf(this.subset) });
      case 4:
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public boolean isSubset()
  {
    return this.subset;
  }

  public void setSubset(boolean paramBoolean)
  {
    this.subset = paramBoolean;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.FontDetails
 * JD-Core Version:    0.6.0
 */