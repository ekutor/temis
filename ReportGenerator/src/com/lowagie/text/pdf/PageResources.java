package com.lowagie.text.pdf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

class PageResources
{
  protected PdfDictionary fontDictionary = new PdfDictionary();
  protected PdfDictionary xObjectDictionary = new PdfDictionary();
  protected PdfDictionary colorDictionary = new PdfDictionary();
  protected PdfDictionary patternDictionary = new PdfDictionary();
  protected PdfDictionary shadingDictionary = new PdfDictionary();
  protected PdfDictionary extGStateDictionary = new PdfDictionary();
  protected PdfDictionary propertyDictionary = new PdfDictionary();
  protected HashMap forbiddenNames;
  protected PdfDictionary originalResources;
  protected int[] namePtr = { 0 };
  protected HashMap usedNames;

  void setOriginalResources(PdfDictionary paramPdfDictionary, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null)
      this.namePtr = paramArrayOfInt;
    this.forbiddenNames = new HashMap();
    this.usedNames = new HashMap();
    if (paramPdfDictionary == null)
      return;
    this.originalResources = new PdfDictionary();
    this.originalResources.merge(paramPdfDictionary);
    Iterator localIterator = paramPdfDictionary.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      PdfObject localPdfObject = PdfReader.getPdfObject(paramPdfDictionary.get(localPdfName));
      if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
        continue;
      PdfDictionary localPdfDictionary = (PdfDictionary)localPdfObject;
      Object localObject = localPdfDictionary.getKeys().iterator();
      while (((Iterator)localObject).hasNext())
        this.forbiddenNames.put(((Iterator)localObject).next(), null);
      localObject = new PdfDictionary();
      ((PdfDictionary)localObject).merge(localPdfDictionary);
      this.originalResources.put(localPdfName, (PdfObject)localObject);
    }
  }

  PdfName translateName(PdfName paramPdfName)
  {
    PdfName localPdfName = paramPdfName;
    if (this.forbiddenNames != null)
    {
      localPdfName = (PdfName)this.usedNames.get(paramPdfName);
      if (localPdfName == null)
      {
        while (true)
        {
          int tmp46_45 = 0;
          int[] tmp46_42 = this.namePtr;
          int tmp48_47 = tmp46_42[tmp46_45];
          tmp46_42[tmp46_45] = (tmp48_47 + 1);
          localPdfName = new PdfName("Xi" + tmp48_47);
          if (!this.forbiddenNames.containsKey(localPdfName))
            break;
        }
        this.usedNames.put(paramPdfName, localPdfName);
      }
    }
    return localPdfName;
  }

  PdfName addFont(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.fontDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  PdfName addXObject(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.xObjectDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  PdfName addColor(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.colorDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  void addDefaultColor(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    if ((paramPdfObject == null) || (paramPdfObject.isNull()))
      this.colorDictionary.remove(paramPdfName);
    else
      this.colorDictionary.put(paramPdfName, paramPdfObject);
  }

  void addDefaultColor(PdfDictionary paramPdfDictionary)
  {
    this.colorDictionary.merge(paramPdfDictionary);
  }

  void addDefaultColorDiff(PdfDictionary paramPdfDictionary)
  {
    this.colorDictionary.mergeDifferent(paramPdfDictionary);
  }

  PdfName addShading(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.shadingDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  PdfName addPattern(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.patternDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  PdfName addExtGState(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.extGStateDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  PdfName addProperty(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference)
  {
    paramPdfName = translateName(paramPdfName);
    this.propertyDictionary.put(paramPdfName, paramPdfIndirectReference);
    return paramPdfName;
  }

  PdfDictionary getResources()
  {
    PdfResources localPdfResources = new PdfResources();
    if (this.originalResources != null)
      localPdfResources.putAll(this.originalResources);
    localPdfResources.put(PdfName.PROCSET, new PdfLiteral("[/PDF /Text /ImageB /ImageC /ImageI]"));
    localPdfResources.add(PdfName.FONT, this.fontDictionary);
    localPdfResources.add(PdfName.XOBJECT, this.xObjectDictionary);
    localPdfResources.add(PdfName.COLORSPACE, this.colorDictionary);
    localPdfResources.add(PdfName.PATTERN, this.patternDictionary);
    localPdfResources.add(PdfName.SHADING, this.shadingDictionary);
    localPdfResources.add(PdfName.EXTGSTATE, this.extGStateDictionary);
    localPdfResources.add(PdfName.PROPERTIES, this.propertyDictionary);
    return localPdfResources;
  }

  boolean hasResources()
  {
    return (this.fontDictionary.size() > 0) || (this.xObjectDictionary.size() > 0) || (this.colorDictionary.size() > 0) || (this.patternDictionary.size() > 0) || (this.shadingDictionary.size() > 0) || (this.extGStateDictionary.size() > 0) || (this.propertyDictionary.size() > 0);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PageResources
 * JD-Core Version:    0.6.0
 */