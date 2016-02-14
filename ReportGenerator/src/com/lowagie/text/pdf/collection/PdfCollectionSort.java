package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

public class PdfCollectionSort extends PdfDictionary
{
  public PdfCollectionSort(String paramString)
  {
    super(PdfName.COLLECTIONSORT);
    put(PdfName.S, new PdfName(paramString));
  }

  public PdfCollectionSort(String[] paramArrayOfString)
  {
    super(PdfName.COLLECTIONSORT);
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfString.length; i++)
      localPdfArray.add(new PdfName(paramArrayOfString[i]));
    put(PdfName.S, localPdfArray);
  }

  public void setSortOrder(boolean paramBoolean)
  {
    PdfObject localPdfObject = get(PdfName.S);
    if ((localPdfObject instanceof PdfName))
      put(PdfName.A, new PdfBoolean(paramBoolean));
    else
      throw new IllegalArgumentException("You have to define a boolean array for this collection sort dictionary.");
  }

  public void setSortOrder(boolean[] paramArrayOfBoolean)
  {
    PdfObject localPdfObject = get(PdfName.S);
    if ((localPdfObject instanceof PdfArray))
    {
      if (((PdfArray)localPdfObject).size() != paramArrayOfBoolean.length)
        throw new IllegalArgumentException("The number of booleans in this array doesn't correspond with the number of fields.");
      PdfArray localPdfArray = new PdfArray();
      for (int i = 0; i < paramArrayOfBoolean.length; i++)
        localPdfArray.add(new PdfBoolean(paramArrayOfBoolean[i]));
      put(PdfName.A, localPdfArray);
    }
    else
    {
      throw new IllegalArgumentException("You need a single boolean for this collection sort dictionary.");
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.collection.PdfCollectionSort
 * JD-Core Version:    0.6.0
 */