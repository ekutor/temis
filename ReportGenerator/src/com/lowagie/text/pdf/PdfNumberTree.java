package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PdfNumberTree
{
  private static final int leafSize = 64;

  public static PdfDictionary writeTree(HashMap paramHashMap, PdfWriter paramPdfWriter)
    throws IOException
  {
    if (paramHashMap.isEmpty())
      return null;
    Integer[] arrayOfInteger = new Integer[paramHashMap.size()];
    arrayOfInteger = (Integer[])paramHashMap.keySet().toArray(arrayOfInteger);
    Arrays.sort(arrayOfInteger);
    if (arrayOfInteger.length <= 64)
    {
      PdfDictionary localPdfDictionary1 = new PdfDictionary();
      localObject = new PdfArray();
      for (j = 0; j < arrayOfInteger.length; j++)
      {
        ((PdfArray)localObject).add(new PdfNumber(arrayOfInteger[j].intValue()));
        ((PdfArray)localObject).add((PdfObject)paramHashMap.get(arrayOfInteger[j]));
      }
      localPdfDictionary1.put(PdfName.NUMS, (PdfObject)localObject);
      return localPdfDictionary1;
    }
    int i = 64;
    Object localObject = new PdfIndirectReference[(arrayOfInteger.length + 64 - 1) / 64];
    int n;
    for (int j = 0; j < localObject.length; j++)
    {
      int k = j * 64;
      n = Math.min(k + 64, arrayOfInteger.length);
      PdfDictionary localPdfDictionary3 = new PdfDictionary();
      PdfArray localPdfArray2 = new PdfArray();
      localPdfArray2.add(new PdfNumber(arrayOfInteger[k].intValue()));
      localPdfArray2.add(new PdfNumber(arrayOfInteger[(n - 1)].intValue()));
      localPdfDictionary3.put(PdfName.LIMITS, localPdfArray2);
      localPdfArray2 = new PdfArray();
      while (k < n)
      {
        localPdfArray2.add(new PdfNumber(arrayOfInteger[k].intValue()));
        localPdfArray2.add((PdfObject)paramHashMap.get(arrayOfInteger[k]));
        k++;
      }
      localPdfDictionary3.put(PdfName.NUMS, localPdfArray2);
      localObject[j] = paramPdfWriter.addToBody(localPdfDictionary3).getIndirectReference();
    }
    int m;
    for (j = localObject.length; ; j = m)
    {
      if (j <= 64)
      {
        PdfArray localPdfArray1 = new PdfArray();
        for (n = 0; n < j; n++)
          localPdfArray1.add(localObject[n]);
        PdfDictionary localPdfDictionary2 = new PdfDictionary();
        localPdfDictionary2.put(PdfName.KIDS, localPdfArray1);
        return localPdfDictionary2;
      }
      i *= 64;
      m = (arrayOfInteger.length + i - 1) / i;
      for (int i1 = 0; i1 < m; i1++)
      {
        int i2 = i1 * 64;
        int i3 = Math.min(i2 + 64, j);
        PdfDictionary localPdfDictionary4 = new PdfDictionary();
        PdfArray localPdfArray3 = new PdfArray();
        localPdfArray3.add(new PdfNumber(arrayOfInteger[(i1 * i)].intValue()));
        localPdfArray3.add(new PdfNumber(arrayOfInteger[(Math.min((i1 + 1) * i, arrayOfInteger.length) - 1)].intValue()));
        localPdfDictionary4.put(PdfName.LIMITS, localPdfArray3);
        localPdfArray3 = new PdfArray();
        while (i2 < i3)
        {
          localPdfArray3.add(localObject[i2]);
          i2++;
        }
        localPdfDictionary4.put(PdfName.KIDS, localPdfArray3);
        localObject[i1] = paramPdfWriter.addToBody(localPdfDictionary4).getIndirectReference();
      }
    }
  }

  private static void iterateItems(PdfDictionary paramPdfDictionary, HashMap paramHashMap)
  {
    PdfArray localPdfArray = (PdfArray)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.NUMS));
    int i;
    Object localObject;
    if (localPdfArray != null)
      for (i = 0; i < localPdfArray.size(); i++)
      {
        localObject = (PdfNumber)PdfReader.getPdfObjectRelease(localPdfArray.getPdfObject(i++));
        paramHashMap.put(new Integer(((PdfNumber)localObject).intValue()), localPdfArray.getPdfObject(i));
      }
    if ((localPdfArray = (PdfArray)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.KIDS))) != null)
      for (i = 0; i < localPdfArray.size(); i++)
      {
        localObject = (PdfDictionary)PdfReader.getPdfObjectRelease(localPdfArray.getPdfObject(i));
        iterateItems((PdfDictionary)localObject, paramHashMap);
      }
  }

  public static HashMap readTree(PdfDictionary paramPdfDictionary)
  {
    HashMap localHashMap = new HashMap();
    if (paramPdfDictionary != null)
      iterateItems(paramPdfDictionary, localHashMap);
    return localHashMap;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfNumberTree
 * JD-Core Version:    0.6.0
 */