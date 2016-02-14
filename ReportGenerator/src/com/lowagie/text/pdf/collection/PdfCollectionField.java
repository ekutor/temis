package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;

public class PdfCollectionField extends PdfDictionary
{
  public static final int TEXT = 0;
  public static final int DATE = 1;
  public static final int NUMBER = 2;
  public static final int FILENAME = 3;
  public static final int DESC = 4;
  public static final int MODDATE = 5;
  public static final int CREATIONDATE = 6;
  public static final int SIZE = 7;
  protected int fieldType;

  public PdfCollectionField(String paramString, int paramInt)
  {
    super(PdfName.COLLECTIONFIELD);
    put(PdfName.N, new PdfString(paramString, "UnicodeBig"));
    this.fieldType = paramInt;
    switch (paramInt)
    {
    default:
      put(PdfName.SUBTYPE, PdfName.S);
      break;
    case 1:
      put(PdfName.SUBTYPE, PdfName.D);
      break;
    case 2:
      put(PdfName.SUBTYPE, PdfName.N);
      break;
    case 3:
      put(PdfName.SUBTYPE, PdfName.F);
      break;
    case 4:
      put(PdfName.SUBTYPE, PdfName.DESC);
      break;
    case 5:
      put(PdfName.SUBTYPE, PdfName.MODDATE);
      break;
    case 6:
      put(PdfName.SUBTYPE, PdfName.CREATIONDATE);
      break;
    case 7:
      put(PdfName.SUBTYPE, PdfName.SIZE);
    }
  }

  public void setOrder(int paramInt)
  {
    put(PdfName.O, new PdfNumber(paramInt));
  }

  public void setVisible(boolean paramBoolean)
  {
    put(PdfName.V, new PdfBoolean(paramBoolean));
  }

  public void setEditable(boolean paramBoolean)
  {
    put(PdfName.E, new PdfBoolean(paramBoolean));
  }

  public boolean isCollectionItem()
  {
    switch (this.fieldType)
    {
    case 0:
    case 1:
    case 2:
      return true;
    }
    return false;
  }

  public PdfObject getValue(String paramString)
  {
    switch (this.fieldType)
    {
    case 0:
      return new PdfString(paramString, "UnicodeBig");
    case 1:
      return new PdfDate(PdfDate.decode(paramString));
    case 2:
      return new PdfNumber(paramString);
    }
    throw new IllegalArgumentException(paramString + " is not an acceptable value for the field " + get(PdfName.N).toString());
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.collection.PdfCollectionField
 * JD-Core Version:    0.6.0
 */