package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;

public class PdfCollectionSchema extends PdfDictionary
{
  public PdfCollectionSchema()
  {
    super(PdfName.COLLECTIONSCHEMA);
  }

  public void addField(String paramString, PdfCollectionField paramPdfCollectionField)
  {
    put(new PdfName(paramString), paramPdfCollectionField);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.collection.PdfCollectionSchema
 * JD-Core Version:    0.6.0
 */