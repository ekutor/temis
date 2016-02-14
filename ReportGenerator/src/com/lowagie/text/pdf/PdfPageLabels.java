package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.factories.RomanAlphabetFactory;
import com.lowagie.text.factories.RomanNumberFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PdfPageLabels
{
  public static final int DECIMAL_ARABIC_NUMERALS = 0;
  public static final int UPPERCASE_ROMAN_NUMERALS = 1;
  public static final int LOWERCASE_ROMAN_NUMERALS = 2;
  public static final int UPPERCASE_LETTERS = 3;
  public static final int LOWERCASE_LETTERS = 4;
  public static final int EMPTY = 5;
  static PdfName[] numberingStyle = { PdfName.D, PdfName.R, new PdfName("r"), PdfName.A, new PdfName("a") };
  private HashMap map = new HashMap();

  public PdfPageLabels()
  {
    addPageLabel(1, 0, null, 1);
  }

  public void addPageLabel(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    if ((paramInt1 < 1) || (paramInt3 < 1))
      throw new IllegalArgumentException("In a page label the page numbers must be greater or equal to 1.");
    PdfDictionary localPdfDictionary = new PdfDictionary();
    if ((paramInt2 >= 0) && (paramInt2 < numberingStyle.length))
      localPdfDictionary.put(PdfName.S, numberingStyle[paramInt2]);
    if (paramString != null)
      localPdfDictionary.put(PdfName.P, new PdfString(paramString, "UnicodeBig"));
    if (paramInt3 != 1)
      localPdfDictionary.put(PdfName.ST, new PdfNumber(paramInt3));
    this.map.put(new Integer(paramInt1 - 1), localPdfDictionary);
  }

  public void addPageLabel(int paramInt1, int paramInt2, String paramString)
  {
    addPageLabel(paramInt1, paramInt2, paramString, 1);
  }

  public void addPageLabel(int paramInt1, int paramInt2)
  {
    addPageLabel(paramInt1, paramInt2, null, 1);
  }

  public void addPageLabel(PdfPageLabelFormat paramPdfPageLabelFormat)
  {
    addPageLabel(paramPdfPageLabelFormat.physicalPage, paramPdfPageLabelFormat.numberStyle, paramPdfPageLabelFormat.prefix, paramPdfPageLabelFormat.logicalPage);
  }

  public void removePageLabel(int paramInt)
  {
    if (paramInt <= 1)
      return;
    this.map.remove(new Integer(paramInt - 1));
  }

  PdfDictionary getDictionary(PdfWriter paramPdfWriter)
  {
    try
    {
      return PdfNumberTree.writeTree(this.map, paramPdfWriter);
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  public static String[] getPageLabels(PdfReader paramPdfReader)
  {
    int i = paramPdfReader.getNumberOfPages();
    PdfDictionary localPdfDictionary1 = paramPdfReader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.PAGELABELS));
    if (localPdfDictionary2 == null)
      return null;
    String[] arrayOfString = new String[i];
    HashMap localHashMap = PdfNumberTree.readTree(localPdfDictionary2);
    int j = 1;
    String str = "";
    int k = 68;
    for (int m = 0; m < i; m++)
    {
      Integer localInteger = new Integer(m);
      if (localHashMap.containsKey(localInteger))
      {
        PdfDictionary localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)localHashMap.get(localInteger));
        if (localPdfDictionary3.contains(PdfName.ST))
          j = ((PdfNumber)localPdfDictionary3.get(PdfName.ST)).intValue();
        else
          j = 1;
        if (localPdfDictionary3.contains(PdfName.P))
          str = ((PdfString)localPdfDictionary3.get(PdfName.P)).toUnicodeString();
        if (localPdfDictionary3.contains(PdfName.S))
          k = ((PdfName)localPdfDictionary3.get(PdfName.S)).toString().charAt(1);
      }
      switch (k)
      {
      default:
        arrayOfString[m] = (str + j);
        break;
      case 82:
        arrayOfString[m] = (str + RomanNumberFactory.getUpperCaseString(j));
        break;
      case 114:
        arrayOfString[m] = (str + RomanNumberFactory.getLowerCaseString(j));
        break;
      case 65:
        arrayOfString[m] = (str + RomanAlphabetFactory.getUpperCaseString(j));
        break;
      case 97:
        arrayOfString[m] = (str + RomanAlphabetFactory.getLowerCaseString(j));
      }
      j++;
    }
    return arrayOfString;
  }

  public static PdfPageLabelFormat[] getPageLabelFormats(PdfReader paramPdfReader)
  {
    PdfDictionary localPdfDictionary1 = paramPdfReader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.PAGELABELS));
    if (localPdfDictionary2 == null)
      return null;
    HashMap localHashMap = PdfNumberTree.readTree(localPdfDictionary2);
    Integer[] arrayOfInteger = new Integer[localHashMap.size()];
    arrayOfInteger = (Integer[])localHashMap.keySet().toArray(arrayOfInteger);
    Arrays.sort(arrayOfInteger);
    PdfPageLabelFormat[] arrayOfPdfPageLabelFormat = new PdfPageLabelFormat[localHashMap.size()];
    for (int k = 0; k < arrayOfInteger.length; k++)
    {
      Integer localInteger = arrayOfInteger[k];
      PdfDictionary localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)localHashMap.get(localInteger));
      int j;
      if (localPdfDictionary3.contains(PdfName.ST))
        j = ((PdfNumber)localPdfDictionary3.get(PdfName.ST)).intValue();
      else
        j = 1;
      String str;
      if (localPdfDictionary3.contains(PdfName.P))
        str = ((PdfString)localPdfDictionary3.get(PdfName.P)).toUnicodeString();
      else
        str = "";
      int i;
      if (localPdfDictionary3.contains(PdfName.S))
      {
        int m = ((PdfName)localPdfDictionary3.get(PdfName.S)).toString().charAt(1);
        switch (m)
        {
        case 82:
          i = 1;
          break;
        case 114:
          i = 2;
          break;
        case 65:
          i = 3;
          break;
        case 97:
          i = 4;
          break;
        default:
          i = 0;
          break;
        }
      }
      else
      {
        i = 5;
      }
      arrayOfPdfPageLabelFormat[k] = new PdfPageLabelFormat(localInteger.intValue() + 1, i, str, j);
    }
    return arrayOfPdfPageLabelFormat;
  }

  public static class PdfPageLabelFormat
  {
    public int physicalPage;
    public int numberStyle;
    public String prefix;
    public int logicalPage;

    public PdfPageLabelFormat(int paramInt1, int paramInt2, String paramString, int paramInt3)
    {
      this.physicalPage = paramInt1;
      this.numberStyle = paramInt2;
      this.prefix = paramString;
      this.logicalPage = paramInt3;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPageLabels
 * JD-Core Version:    0.6.0
 */