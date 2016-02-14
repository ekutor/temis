package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfDictionary extends PdfObject
{
  public static final PdfName FONT = PdfName.FONT;
  public static final PdfName OUTLINES = PdfName.OUTLINES;
  public static final PdfName PAGE = PdfName.PAGE;
  public static final PdfName PAGES = PdfName.PAGES;
  public static final PdfName CATALOG = PdfName.CATALOG;
  private PdfName dictionaryType = null;
  protected HashMap hashMap = new HashMap();

  public PdfDictionary()
  {
    super(6);
  }

  public PdfDictionary(PdfName paramPdfName)
  {
    this();
    this.dictionaryType = paramPdfName;
    put(PdfName.TYPE, this.dictionaryType);
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(60);
    paramOutputStream.write(60);
    int i = 0;
    Iterator localIterator = this.hashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      PdfObject localPdfObject = (PdfObject)this.hashMap.get(localPdfName);
      localPdfName.toPdf(paramPdfWriter, paramOutputStream);
      i = localPdfObject.type();
      if ((i != 5) && (i != 6) && (i != 4) && (i != 3))
        paramOutputStream.write(32);
      localPdfObject.toPdf(paramPdfWriter, paramOutputStream);
    }
    paramOutputStream.write(62);
    paramOutputStream.write(62);
  }

  public String toString()
  {
    if (get(PdfName.TYPE) == null)
      return "Dictionary";
    return "Dictionary of type: " + get(PdfName.TYPE);
  }

  public void put(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    if ((paramPdfObject == null) || (paramPdfObject.isNull()))
      this.hashMap.remove(paramPdfName);
    else
      this.hashMap.put(paramPdfName, paramPdfObject);
  }

  public void putEx(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    if (paramPdfObject == null)
      return;
    put(paramPdfName, paramPdfObject);
  }

  public void putAll(PdfDictionary paramPdfDictionary)
  {
    this.hashMap.putAll(paramPdfDictionary.hashMap);
  }

  public void remove(PdfName paramPdfName)
  {
    this.hashMap.remove(paramPdfName);
  }

  public PdfObject get(PdfName paramPdfName)
  {
    return (PdfObject)this.hashMap.get(paramPdfName);
  }

  public PdfObject getDirectObject(PdfName paramPdfName)
  {
    return PdfReader.getPdfObject(get(paramPdfName));
  }

  public Set getKeys()
  {
    return this.hashMap.keySet();
  }

  public int size()
  {
    return this.hashMap.size();
  }

  public boolean contains(PdfName paramPdfName)
  {
    return this.hashMap.containsKey(paramPdfName);
  }

  public boolean isFont()
  {
    return FONT.equals(this.dictionaryType);
  }

  public boolean isPage()
  {
    return PAGE.equals(this.dictionaryType);
  }

  public boolean isPages()
  {
    return PAGES.equals(this.dictionaryType);
  }

  public boolean isCatalog()
  {
    return CATALOG.equals(this.dictionaryType);
  }

  public boolean isOutlineTree()
  {
    return OUTLINES.equals(this.dictionaryType);
  }

  public void merge(PdfDictionary paramPdfDictionary)
  {
    this.hashMap.putAll(paramPdfDictionary.hashMap);
  }

  public void mergeDifferent(PdfDictionary paramPdfDictionary)
  {
    Iterator localIterator = paramPdfDictionary.hashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (this.hashMap.containsKey(localObject))
        continue;
      this.hashMap.put(localObject, paramPdfDictionary.hashMap.get(localObject));
    }
  }

  public PdfDictionary getAsDict(PdfName paramPdfName)
  {
    PdfDictionary localPdfDictionary = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isDictionary()))
      localPdfDictionary = (PdfDictionary)localPdfObject;
    return localPdfDictionary;
  }

  public PdfArray getAsArray(PdfName paramPdfName)
  {
    PdfArray localPdfArray = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isArray()))
      localPdfArray = (PdfArray)localPdfObject;
    return localPdfArray;
  }

  public PdfStream getAsStream(PdfName paramPdfName)
  {
    PdfStream localPdfStream = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isStream()))
      localPdfStream = (PdfStream)localPdfObject;
    return localPdfStream;
  }

  public PdfString getAsString(PdfName paramPdfName)
  {
    PdfString localPdfString = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isString()))
      localPdfString = (PdfString)localPdfObject;
    return localPdfString;
  }

  public PdfNumber getAsNumber(PdfName paramPdfName)
  {
    PdfNumber localPdfNumber = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isNumber()))
      localPdfNumber = (PdfNumber)localPdfObject;
    return localPdfNumber;
  }

  public PdfName getAsName(PdfName paramPdfName)
  {
    PdfName localPdfName = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isName()))
      localPdfName = (PdfName)localPdfObject;
    return localPdfName;
  }

  public PdfBoolean getAsBoolean(PdfName paramPdfName)
  {
    PdfBoolean localPdfBoolean = null;
    PdfObject localPdfObject = getDirectObject(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isBoolean()))
      localPdfBoolean = (PdfBoolean)localPdfObject;
    return localPdfBoolean;
  }

  public PdfIndirectReference getAsIndirectObject(PdfName paramPdfName)
  {
    PdfIndirectReference localPdfIndirectReference = null;
    PdfObject localPdfObject = get(paramPdfName);
    if ((localPdfObject != null) && (localPdfObject.isIndirect()))
      localPdfIndirectReference = (PdfIndirectReference)localPdfObject;
    return localPdfIndirectReference;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfDictionary
 * JD-Core Version:    0.6.0
 */