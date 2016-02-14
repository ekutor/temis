package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class PdfArray extends PdfObject
{
  protected ArrayList arrayList;

  public PdfArray()
  {
    super(5);
    this.arrayList = new ArrayList();
  }

  public PdfArray(PdfObject paramPdfObject)
  {
    super(5);
    this.arrayList = new ArrayList();
    this.arrayList.add(paramPdfObject);
  }

  public PdfArray(float[] paramArrayOfFloat)
  {
    super(5);
    this.arrayList = new ArrayList();
    add(paramArrayOfFloat);
  }

  public PdfArray(int[] paramArrayOfInt)
  {
    super(5);
    this.arrayList = new ArrayList();
    add(paramArrayOfInt);
  }

  public PdfArray(ArrayList paramArrayList)
  {
    this();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
      add((PdfObject)localIterator.next());
  }

  public PdfArray(PdfArray paramPdfArray)
  {
    super(5);
    this.arrayList = new ArrayList(paramPdfArray.arrayList);
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(91);
    Iterator localIterator = this.arrayList.iterator();
    int i = 0;
    Object localObject;
    if (localIterator.hasNext())
    {
      localObject = (PdfObject)localIterator.next();
      if (localObject == null)
        localObject = PdfNull.PDFNULL;
      ((PdfObject)localObject).toPdf(paramPdfWriter, paramOutputStream);
    }
    while (localIterator.hasNext())
    {
      localObject = (PdfObject)localIterator.next();
      if (localObject == null)
        localObject = PdfNull.PDFNULL;
      i = ((PdfObject)localObject).type();
      if ((i != 5) && (i != 6) && (i != 4) && (i != 3))
        paramOutputStream.write(32);
      ((PdfObject)localObject).toPdf(paramPdfWriter, paramOutputStream);
    }
    paramOutputStream.write(93);
  }

  public String toString()
  {
    return this.arrayList.toString();
  }

  public PdfObject set(int paramInt, PdfObject paramPdfObject)
  {
    return (PdfObject)this.arrayList.set(paramInt, paramPdfObject);
  }

  public PdfObject remove(int paramInt)
  {
    return (PdfObject)this.arrayList.remove(paramInt);
  }

  /** @deprecated */
  public ArrayList getArrayList()
  {
    return this.arrayList;
  }

  public int size()
  {
    return this.arrayList.size();
  }

  public boolean isEmpty()
  {
    return this.arrayList.isEmpty();
  }

  public boolean add(PdfObject paramPdfObject)
  {
    return this.arrayList.add(paramPdfObject);
  }

  public boolean add(float[] paramArrayOfFloat)
  {
    for (int i = 0; i < paramArrayOfFloat.length; i++)
      this.arrayList.add(new PdfNumber(paramArrayOfFloat[i]));
    return true;
  }

  public boolean add(int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramArrayOfInt.length; i++)
      this.arrayList.add(new PdfNumber(paramArrayOfInt[i]));
    return true;
  }

  public void add(int paramInt, PdfObject paramPdfObject)
  {
    this.arrayList.add(paramInt, paramPdfObject);
  }

  public void addFirst(PdfObject paramPdfObject)
  {
    this.arrayList.add(0, paramPdfObject);
  }

  public boolean contains(PdfObject paramPdfObject)
  {
    return this.arrayList.contains(paramPdfObject);
  }

  public ListIterator listIterator()
  {
    return this.arrayList.listIterator();
  }

  public PdfObject getPdfObject(int paramInt)
  {
    return (PdfObject)this.arrayList.get(paramInt);
  }

  public PdfObject getDirectObject(int paramInt)
  {
    return PdfReader.getPdfObject(getPdfObject(paramInt));
  }

  public PdfDictionary getAsDict(int paramInt)
  {
    PdfDictionary localPdfDictionary = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isDictionary()))
      localPdfDictionary = (PdfDictionary)localPdfObject;
    return localPdfDictionary;
  }

  public PdfArray getAsArray(int paramInt)
  {
    PdfArray localPdfArray = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isArray()))
      localPdfArray = (PdfArray)localPdfObject;
    return localPdfArray;
  }

  public PdfStream getAsStream(int paramInt)
  {
    PdfStream localPdfStream = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isStream()))
      localPdfStream = (PdfStream)localPdfObject;
    return localPdfStream;
  }

  public PdfString getAsString(int paramInt)
  {
    PdfString localPdfString = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isString()))
      localPdfString = (PdfString)localPdfObject;
    return localPdfString;
  }

  public PdfNumber getAsNumber(int paramInt)
  {
    PdfNumber localPdfNumber = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isNumber()))
      localPdfNumber = (PdfNumber)localPdfObject;
    return localPdfNumber;
  }

  public PdfName getAsName(int paramInt)
  {
    PdfName localPdfName = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isName()))
      localPdfName = (PdfName)localPdfObject;
    return localPdfName;
  }

  public PdfBoolean getAsBoolean(int paramInt)
  {
    PdfBoolean localPdfBoolean = null;
    PdfObject localPdfObject = getDirectObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isBoolean()))
      localPdfBoolean = (PdfBoolean)localPdfObject;
    return localPdfBoolean;
  }

  public PdfIndirectReference getAsIndirectObject(int paramInt)
  {
    PdfIndirectReference localPdfIndirectReference = null;
    PdfObject localPdfObject = getPdfObject(paramInt);
    if ((localPdfObject != null) && (localPdfObject.isIndirect()))
      localPdfIndirectReference = (PdfIndirectReference)localPdfObject;
    return localPdfIndirectReference;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfArray
 * JD-Core Version:    0.6.0
 */