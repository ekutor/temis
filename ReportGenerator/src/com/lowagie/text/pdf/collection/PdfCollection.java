package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;

public class PdfCollection extends PdfDictionary
{
  public static final int DETAILS = 0;
  public static final int TILE = 1;
  public static final int HIDDEN = 2;

  public PdfCollection(int paramInt)
  {
    super(PdfName.COLLECTION);
    switch (paramInt)
    {
    case 1:
      put(PdfName.VIEW, PdfName.T);
      break;
    case 2:
      put(PdfName.VIEW, PdfName.H);
      break;
    default:
      put(PdfName.VIEW, PdfName.D);
    }
  }

  public void setInitialDocument(String paramString)
  {
    put(PdfName.D, new PdfString(paramString, null));
  }

  public void setSchema(PdfCollectionSchema paramPdfCollectionSchema)
  {
    put(PdfName.SCHEMA, paramPdfCollectionSchema);
  }

  public PdfCollectionSchema getSchema()
  {
    return (PdfCollectionSchema)get(PdfName.SCHEMA);
  }

  public void setSort(PdfCollectionSort paramPdfCollectionSort)
  {
    put(PdfName.SORT, paramPdfCollectionSort);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.collection.PdfCollection
 * JD-Core Version:    0.6.0
 */