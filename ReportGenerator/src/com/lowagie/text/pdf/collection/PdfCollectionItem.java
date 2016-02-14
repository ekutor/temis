package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.util.Calendar;

public class PdfCollectionItem extends PdfDictionary
{
  PdfCollectionSchema schema;

  public PdfCollectionItem(PdfCollectionSchema paramPdfCollectionSchema)
  {
    super(PdfName.COLLECTIONITEM);
    this.schema = paramPdfCollectionSchema;
  }

  public void addItem(String paramString1, String paramString2)
  {
    PdfName localPdfName = new PdfName(paramString1);
    PdfCollectionField localPdfCollectionField = (PdfCollectionField)this.schema.get(localPdfName);
    put(localPdfName, localPdfCollectionField.getValue(paramString2));
  }

  public void addItem(String paramString, PdfString paramPdfString)
  {
    PdfName localPdfName = new PdfName(paramString);
    PdfCollectionField localPdfCollectionField = (PdfCollectionField)this.schema.get(localPdfName);
    if (localPdfCollectionField.fieldType == 0)
      put(localPdfName, paramPdfString);
  }

  public void addItem(String paramString, PdfDate paramPdfDate)
  {
    PdfName localPdfName = new PdfName(paramString);
    PdfCollectionField localPdfCollectionField = (PdfCollectionField)this.schema.get(localPdfName);
    if (localPdfCollectionField.fieldType == 1)
      put(localPdfName, paramPdfDate);
  }

  public void addItem(String paramString, PdfNumber paramPdfNumber)
  {
    PdfName localPdfName = new PdfName(paramString);
    PdfCollectionField localPdfCollectionField = (PdfCollectionField)this.schema.get(localPdfName);
    if (localPdfCollectionField.fieldType == 2)
      put(localPdfName, paramPdfNumber);
  }

  public void addItem(String paramString, Calendar paramCalendar)
  {
    addItem(paramString, new PdfDate(paramCalendar));
  }

  public void addItem(String paramString, int paramInt)
  {
    addItem(paramString, new PdfNumber(paramInt));
  }

  public void addItem(String paramString, float paramFloat)
  {
    addItem(paramString, new PdfNumber(paramFloat));
  }

  public void addItem(String paramString, double paramDouble)
  {
    addItem(paramString, new PdfNumber(paramDouble));
  }

  public void setPrefix(String paramString1, String paramString2)
  {
    PdfName localPdfName = new PdfName(paramString1);
    PdfObject localPdfObject = get(localPdfName);
    if (localPdfObject == null)
      throw new IllegalArgumentException("You must set a value before adding a prefix.");
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.COLLECTIONSUBITEM);
    localPdfDictionary.put(PdfName.D, localPdfObject);
    localPdfDictionary.put(PdfName.P, new PdfString(paramString2, "UnicodeBig"));
    put(localPdfName, localPdfDictionary);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.collection.PdfCollectionItem
 * JD-Core Version:    0.6.0
 */