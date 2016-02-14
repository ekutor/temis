package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.exceptions.BadPasswordException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

class PdfCopyFieldsImp extends PdfWriter
{
  private static final PdfName iTextTag = new PdfName("_iTextTag_");
  private static final Integer zero = new Integer(0);
  ArrayList readers = new ArrayList();
  HashMap readers2intrefs = new HashMap();
  HashMap pages2intrefs = new HashMap();
  HashMap visited = new HashMap();
  ArrayList fields = new ArrayList();
  RandomAccessFileOrArray file;
  HashMap fieldTree = new HashMap();
  ArrayList pageRefs = new ArrayList();
  ArrayList pageDics = new ArrayList();
  PdfDictionary resources = new PdfDictionary();
  PdfDictionary form;
  boolean closing = false;
  Document nd;
  private HashMap tabOrder;
  private ArrayList calculationOrder = new ArrayList();
  private ArrayList calculationOrderRefs;
  private boolean hasSignature;
  protected static final HashMap widgetKeys = new HashMap();
  protected static final HashMap fieldKeys = new HashMap();

  PdfCopyFieldsImp(OutputStream paramOutputStream)
    throws DocumentException
  {
    this(paramOutputStream, '\000');
  }

  PdfCopyFieldsImp(OutputStream paramOutputStream, char paramChar)
    throws DocumentException
  {
    super(new PdfDocument(), paramOutputStream);
    this.pdf.addWriter(this);
    if (paramChar != 0)
      super.setPdfVersion(paramChar);
    this.nd = new Document();
    this.nd.addDocListener(this.pdf);
  }

  void addDocument(PdfReader paramPdfReader, List paramList)
    throws DocumentException, IOException
  {
    if ((!this.readers2intrefs.containsKey(paramPdfReader)) && (paramPdfReader.isTampered()))
      throw new DocumentException("The document was reused.");
    paramPdfReader = new PdfReader(paramPdfReader);
    paramPdfReader.selectPages(paramList);
    if (paramPdfReader.getNumberOfPages() == 0)
      return;
    paramPdfReader.setTampered(false);
    addDocument(paramPdfReader);
  }

  void addDocument(PdfReader paramPdfReader)
    throws DocumentException, IOException
  {
    if (!paramPdfReader.isOpenedWithFullPermissions())
      throw new BadPasswordException("PdfReader not opened with owner password");
    openDoc();
    if (this.readers2intrefs.containsKey(paramPdfReader))
    {
      paramPdfReader = new PdfReader(paramPdfReader);
    }
    else
    {
      if (paramPdfReader.isTampered())
        throw new DocumentException("The document was reused.");
      paramPdfReader.consolidateNamedDestinations();
      paramPdfReader.setTampered(true);
    }
    paramPdfReader.shuffleSubsetNames();
    this.readers2intrefs.put(paramPdfReader, new IntHashtable());
    this.readers.add(paramPdfReader);
    int i = paramPdfReader.getNumberOfPages();
    IntHashtable localIntHashtable = new IntHashtable();
    for (int j = 1; j <= i; j++)
    {
      localIntHashtable.put(paramPdfReader.getPageOrigRef(j).getNumber(), 1);
      paramPdfReader.releasePage(j);
    }
    this.pages2intrefs.put(paramPdfReader, localIntHashtable);
    this.visited.put(paramPdfReader, new IntHashtable());
    this.fields.add(paramPdfReader.getAcroFields());
    updateCalculationOrder(paramPdfReader);
  }

  private static String getCOName(PdfReader paramPdfReader, PRIndirectReference paramPRIndirectReference)
  {
    String str = "";
    while (paramPRIndirectReference != null)
    {
      PdfObject localPdfObject = PdfReader.getPdfObject(paramPRIndirectReference);
      if ((localPdfObject == null) || (localPdfObject.type() != 6))
        break;
      PdfDictionary localPdfDictionary = (PdfDictionary)localPdfObject;
      PdfString localPdfString = localPdfDictionary.getAsString(PdfName.T);
      if (localPdfString != null)
        str = localPdfString.toUnicodeString() + "." + str;
      paramPRIndirectReference = (PRIndirectReference)localPdfDictionary.get(PdfName.PARENT);
    }
    if (str.endsWith("."))
      str = str.substring(0, str.length() - 1);
    return str;
  }

  protected void updateCalculationOrder(PdfReader paramPdfReader)
  {
    PdfDictionary localPdfDictionary1 = paramPdfReader.getCatalog();
    PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.ACROFORM);
    if (localPdfDictionary2 == null)
      return;
    PdfArray localPdfArray = localPdfDictionary2.getAsArray(PdfName.CO);
    if ((localPdfArray == null) || (localPdfArray.size() == 0))
      return;
    AcroFields localAcroFields = paramPdfReader.getAcroFields();
    for (int i = 0; i < localPdfArray.size(); i++)
    {
      PdfObject localPdfObject = localPdfArray.getPdfObject(i);
      if ((localPdfObject == null) || (!localPdfObject.isIndirect()))
        continue;
      String str = getCOName(paramPdfReader, (PRIndirectReference)localPdfObject);
      if (localAcroFields.getFieldItem(str) == null)
        continue;
      str = "." + str;
      if (this.calculationOrder.contains(str))
        continue;
      this.calculationOrder.add(str);
    }
  }

  void propagate(PdfObject paramPdfObject, PdfIndirectReference paramPdfIndirectReference, boolean paramBoolean)
    throws IOException
  {
    if (paramPdfObject == null)
      return;
    if ((paramPdfObject instanceof PdfIndirectReference))
      return;
    Object localObject1;
    Object localObject2;
    switch (paramPdfObject.type())
    {
    case 6:
    case 7:
      localObject1 = (PdfDictionary)paramPdfObject;
      localObject2 = ((PdfDictionary)localObject1).getKeys().iterator();
    case 5:
    case 10:
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = (PdfName)((Iterator)localObject2).next();
        if ((paramBoolean) && ((((PdfName)localObject3).equals(PdfName.PARENT)) || (((PdfName)localObject3).equals(PdfName.KIDS))))
          continue;
        Object localObject4 = ((PdfDictionary)localObject1).get((PdfName)localObject3);
        if ((localObject4 != null) && (((PdfObject)localObject4).isIndirect()))
        {
          PRIndirectReference localPRIndirectReference = (PRIndirectReference)localObject4;
          if ((setVisited(localPRIndirectReference)) || (isPage(localPRIndirectReference)))
            continue;
          PdfIndirectReference localPdfIndirectReference = getNewReference(localPRIndirectReference);
          propagate(PdfReader.getPdfObjectRelease(localPRIndirectReference), localPdfIndirectReference, paramBoolean);
          continue;
        }
        propagate((PdfObject)localObject4, null, paramBoolean);
        continue;
        localObject1 = ((PdfArray)paramPdfObject).listIterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (PdfObject)((Iterator)localObject1).next();
          if ((localObject2 != null) && (((PdfObject)localObject2).isIndirect()))
          {
            localObject3 = (PRIndirectReference)localObject2;
            if ((isVisited((PRIndirectReference)localObject3)) || (isPage((PRIndirectReference)localObject3)))
              continue;
            localObject4 = getNewReference((PRIndirectReference)localObject3);
            propagate(PdfReader.getPdfObjectRelease((PdfObject)localObject3), (PdfIndirectReference)localObject4, paramBoolean);
            continue;
          }
          propagate((PdfObject)localObject2, null, paramBoolean);
          continue;
          throw new RuntimeException("Reference pointing to reference.");
        }
      }
    case 8:
    case 9:
    }
  }

  private void adjustTabOrder(PdfArray paramPdfArray, PdfIndirectReference paramPdfIndirectReference, PdfNumber paramPdfNumber)
  {
    int i = paramPdfNumber.intValue();
    ArrayList localArrayList = (ArrayList)this.tabOrder.get(paramPdfArray);
    int j;
    int k;
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      j = paramPdfArray.size() - 1;
      for (k = 0; k < j; k++)
        localArrayList.add(zero);
      localArrayList.add(new Integer(i));
      this.tabOrder.put(paramPdfArray, localArrayList);
      paramPdfArray.add(paramPdfIndirectReference);
    }
    else
    {
      j = localArrayList.size() - 1;
      for (k = j; k >= 0; k--)
      {
        if (((Integer)localArrayList.get(k)).intValue() > i)
          continue;
        localArrayList.add(k + 1, new Integer(i));
        paramPdfArray.add(k + 1, paramPdfIndirectReference);
        j = -2;
        break;
      }
      if (j != -2)
      {
        localArrayList.add(0, new Integer(i));
        paramPdfArray.add(0, paramPdfIndirectReference);
      }
    }
  }

  protected PdfArray branchForm(HashMap paramHashMap, PdfIndirectReference paramPdfIndirectReference, String paramString)
    throws IOException
  {
    PdfArray localPdfArray1 = new PdfArray();
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      Object localObject1 = localEntry.getValue();
      PdfIndirectReference localPdfIndirectReference1 = getPdfIndirectReference();
      PdfDictionary localPdfDictionary1 = new PdfDictionary();
      if (paramPdfIndirectReference != null)
        localPdfDictionary1.put(PdfName.PARENT, paramPdfIndirectReference);
      localPdfDictionary1.put(PdfName.T, new PdfString(str1, "UnicodeBig"));
      String str2 = paramString + "." + str1;
      int i = this.calculationOrder.indexOf(str2);
      if (i >= 0)
        this.calculationOrderRefs.set(i, localPdfIndirectReference1);
      if ((localObject1 instanceof HashMap))
      {
        localPdfDictionary1.put(PdfName.KIDS, branchForm((HashMap)localObject1, localPdfIndirectReference1, str2));
        localPdfArray1.add(localPdfIndirectReference1);
        addToBody(localPdfDictionary1, localPdfIndirectReference1);
        continue;
      }
      ArrayList localArrayList = (ArrayList)localObject1;
      localPdfDictionary1.mergeDifferent((PdfDictionary)localArrayList.get(0));
      Object localObject2;
      if (localArrayList.size() == 3)
      {
        localPdfDictionary1.mergeDifferent((PdfDictionary)localArrayList.get(2));
        int j = ((Integer)localArrayList.get(1)).intValue();
        PdfDictionary localPdfDictionary2 = (PdfDictionary)this.pageDics.get(j - 1);
        PdfArray localPdfArray3 = localPdfDictionary2.getAsArray(PdfName.ANNOTS);
        if (localPdfArray3 == null)
        {
          localPdfArray3 = new PdfArray();
          localPdfDictionary2.put(PdfName.ANNOTS, localPdfArray3);
        }
        localObject2 = (PdfNumber)localPdfDictionary1.get(iTextTag);
        localPdfDictionary1.remove(iTextTag);
        adjustTabOrder(localPdfArray3, localPdfIndirectReference1, (PdfNumber)localObject2);
      }
      else
      {
        PdfArray localPdfArray2 = new PdfArray();
        for (int k = 1; k < localArrayList.size(); k += 2)
        {
          int m = ((Integer)localArrayList.get(k)).intValue();
          localObject2 = (PdfDictionary)this.pageDics.get(m - 1);
          PdfArray localPdfArray4 = ((PdfDictionary)localObject2).getAsArray(PdfName.ANNOTS);
          if (localPdfArray4 == null)
          {
            localPdfArray4 = new PdfArray();
            ((PdfDictionary)localObject2).put(PdfName.ANNOTS, localPdfArray4);
          }
          PdfDictionary localPdfDictionary3 = new PdfDictionary();
          localPdfDictionary3.merge((PdfDictionary)localArrayList.get(k + 1));
          localPdfDictionary3.put(PdfName.PARENT, localPdfIndirectReference1);
          PdfNumber localPdfNumber = (PdfNumber)localPdfDictionary3.get(iTextTag);
          localPdfDictionary3.remove(iTextTag);
          PdfIndirectReference localPdfIndirectReference2 = addToBody(localPdfDictionary3).getIndirectReference();
          adjustTabOrder(localPdfArray4, localPdfIndirectReference2, localPdfNumber);
          localPdfArray2.add(localPdfIndirectReference2);
          propagate(localPdfDictionary3, null, false);
        }
        localPdfDictionary1.put(PdfName.KIDS, localPdfArray2);
      }
      localPdfArray1.add(localPdfIndirectReference1);
      addToBody(localPdfDictionary1, localPdfIndirectReference1);
      propagate(localPdfDictionary1, null, false);
    }
    return (PdfArray)localPdfArray1;
  }

  protected void createAcroForms()
    throws IOException
  {
    if (this.fieldTree.isEmpty())
      return;
    this.form = new PdfDictionary();
    this.form.put(PdfName.DR, this.resources);
    propagate(this.resources, null, false);
    this.form.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
    this.tabOrder = new HashMap();
    this.calculationOrderRefs = new ArrayList(this.calculationOrder);
    this.form.put(PdfName.FIELDS, branchForm(this.fieldTree, null, ""));
    if (this.hasSignature)
      this.form.put(PdfName.SIGFLAGS, new PdfNumber(3));
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < this.calculationOrderRefs.size(); i++)
    {
      Object localObject = this.calculationOrderRefs.get(i);
      if (!(localObject instanceof PdfIndirectReference))
        continue;
      localPdfArray.add((PdfIndirectReference)localObject);
    }
    if (localPdfArray.size() > 0)
      this.form.put(PdfName.CO, localPdfArray);
  }

  public void close()
  {
    if (this.closing)
    {
      super.close();
      return;
    }
    this.closing = true;
    try
    {
      closeIt();
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  protected void closeIt()
    throws IOException
  {
    for (int i = 0; i < this.readers.size(); i++)
      ((PdfReader)this.readers.get(i)).removeFields();
    Object localObject1;
    int j;
    for (i = 0; i < this.readers.size(); i++)
    {
      localObject1 = (PdfReader)this.readers.get(i);
      for (j = 1; j <= ((PdfReader)localObject1).getNumberOfPages(); j++)
      {
        this.pageRefs.add(getNewReference(((PdfReader)localObject1).getPageOrigRef(j)));
        this.pageDics.add(((PdfReader)localObject1).getPageN(j));
      }
    }
    mergeFields();
    createAcroForms();
    Object localObject2;
    Object localObject3;
    for (i = 0; i < this.readers.size(); i++)
    {
      localObject1 = (PdfReader)this.readers.get(i);
      for (j = 1; j <= ((PdfReader)localObject1).getNumberOfPages(); j++)
      {
        localObject2 = ((PdfReader)localObject1).getPageN(j);
        localObject3 = getNewReference(((PdfReader)localObject1).getPageOrigRef(j));
        PdfIndirectReference localPdfIndirectReference = this.root.addPageRef((PdfIndirectReference)localObject3);
        ((PdfDictionary)localObject2).put(PdfName.PARENT, localPdfIndirectReference);
        propagate((PdfObject)localObject2, (PdfIndirectReference)localObject3, false);
      }
    }
    Iterator localIterator = this.readers2intrefs.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      PdfReader localPdfReader = (PdfReader)((Map.Entry)localObject1).getKey();
      try
      {
        this.file = localPdfReader.getSafeFile();
        this.file.reOpen();
        localObject2 = (IntHashtable)((Map.Entry)localObject1).getValue();
        localObject3 = ((IntHashtable)localObject2).toOrderedKeys();
        for (int k = 0; k < localObject3.length; k++)
        {
          PRIndirectReference localPRIndirectReference = new PRIndirectReference(localPdfReader, localObject3[k]);
          addToBody(PdfReader.getPdfObjectRelease(localPRIndirectReference), ((IntHashtable)localObject2).get(localObject3[k]));
        }
        try
        {
          this.file.close();
          localPdfReader.close();
        }
        catch (Exception localException1)
        {
        }
        continue;
      }
      finally
      {
        try
        {
          this.file.close();
          localPdfReader.close();
        }
        catch (Exception localException2)
        {
        }
      }
    }
    this.pdf.close();
  }

  void addPageOffsetToField(HashMap paramHashMap, int paramInt)
  {
    if (paramInt == 0)
      return;
    Iterator localIterator = paramHashMap.values().iterator();
    while (localIterator.hasNext())
    {
      AcroFields.Item localItem = (AcroFields.Item)localIterator.next();
      for (int i = 0; i < localItem.size(); i++)
      {
        int j = localItem.getPage(i).intValue();
        localItem.forcePage(i, j + paramInt);
      }
    }
  }

  void createWidgets(ArrayList paramArrayList, AcroFields.Item paramItem)
  {
    for (int i = 0; i < paramItem.size(); i++)
    {
      paramArrayList.add(paramItem.getPage(i));
      PdfDictionary localPdfDictionary1 = paramItem.getMerged(i);
      PdfObject localPdfObject = localPdfDictionary1.get(PdfName.DR);
      if (localPdfObject != null)
        PdfFormField.mergeResources(this.resources, (PdfDictionary)PdfReader.getPdfObject(localPdfObject));
      PdfDictionary localPdfDictionary2 = new PdfDictionary();
      Iterator localIterator = localPdfDictionary1.getKeys().iterator();
      while (localIterator.hasNext())
      {
        PdfName localPdfName = (PdfName)localIterator.next();
        if (!widgetKeys.containsKey(localPdfName))
          continue;
        localPdfDictionary2.put(localPdfName, localPdfDictionary1.get(localPdfName));
      }
      localPdfDictionary2.put(iTextTag, new PdfNumber(paramItem.getTabOrder(i).intValue() + 1));
      paramArrayList.add(localPdfDictionary2);
    }
  }

  void mergeField(String paramString, AcroFields.Item paramItem)
  {
    HashMap localHashMap = this.fieldTree;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
    if (!localStringTokenizer.hasMoreTokens())
      return;
    String str;
    Object localObject1;
    while (true)
    {
      str = localStringTokenizer.nextToken();
      localObject1 = localHashMap.get(str);
      if (!localStringTokenizer.hasMoreTokens())
        break;
      if (localObject1 == null)
      {
        localObject1 = new HashMap();
        localHashMap.put(str, localObject1);
        localHashMap = (HashMap)localObject1;
        continue;
      }
      if ((localObject1 instanceof HashMap))
      {
        localHashMap = (HashMap)localObject1;
        continue;
      }
      return;
    }
    if ((localObject1 instanceof HashMap))
      return;
    PdfDictionary localPdfDictionary = paramItem.getMerged(0);
    Object localObject2;
    Object localObject3;
    PdfName localPdfName1;
    if (localObject1 == null)
    {
      localObject2 = new PdfDictionary();
      if (PdfName.SIG.equals(localPdfDictionary.get(PdfName.FT)))
        this.hasSignature = true;
      localObject3 = localPdfDictionary.getKeys().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localPdfName1 = (PdfName)((Iterator)localObject3).next();
        if (!fieldKeys.containsKey(localPdfName1))
          continue;
        ((PdfDictionary)localObject2).put(localPdfName1, localPdfDictionary.get(localPdfName1));
      }
      localObject3 = new ArrayList();
      ((ArrayList)localObject3).add(localObject2);
      createWidgets((ArrayList)localObject3, paramItem);
      localHashMap.put(str, localObject3);
    }
    else
    {
      localObject2 = (ArrayList)localObject1;
      localObject3 = (PdfDictionary)((ArrayList)localObject2).get(0);
      localPdfName1 = (PdfName)((PdfDictionary)localObject3).get(PdfName.FT);
      PdfName localPdfName2 = (PdfName)localPdfDictionary.get(PdfName.FT);
      if ((localPdfName1 == null) || (!localPdfName1.equals(localPdfName2)))
        return;
      int i = 0;
      PdfObject localPdfObject1 = ((PdfDictionary)localObject3).get(PdfName.FF);
      if ((localPdfObject1 != null) && (localPdfObject1.isNumber()))
        i = ((PdfNumber)localPdfObject1).intValue();
      int j = 0;
      PdfObject localPdfObject2 = localPdfDictionary.get(PdfName.FF);
      if ((localPdfObject2 != null) && (localPdfObject2.isNumber()))
        j = ((PdfNumber)localPdfObject2).intValue();
      if (localPdfName1.equals(PdfName.BTN))
      {
        if (((i ^ j) & 0x10000) != 0)
          return;
        if (((i & 0x10000) == 0) && (((i ^ j) & 0x8000) != 0))
          return;
      }
      else if ((localPdfName1.equals(PdfName.CH)) && (((i ^ j) & 0x20000) != 0))
      {
        return;
      }
      createWidgets((ArrayList)localObject2, paramItem);
    }
  }

  void mergeWithMaster(HashMap paramHashMap)
  {
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      mergeField(str, (AcroFields.Item)localEntry.getValue());
    }
  }

  void mergeFields()
  {
    int i = 0;
    for (int j = 0; j < this.fields.size(); j++)
    {
      HashMap localHashMap = ((AcroFields)this.fields.get(j)).getFields();
      addPageOffsetToField(localHashMap, i);
      mergeWithMaster(localHashMap);
      i += ((PdfReader)this.readers.get(j)).getNumberOfPages();
    }
  }

  public PdfIndirectReference getPageReference(int paramInt)
  {
    return (PdfIndirectReference)this.pageRefs.get(paramInt - 1);
  }

  protected PdfDictionary getCatalog(PdfIndirectReference paramPdfIndirectReference)
  {
    try
    {
      PdfDocument.PdfCatalog localPdfCatalog = this.pdf.getCatalog(paramPdfIndirectReference);
      if (this.form != null)
      {
        PdfIndirectReference localPdfIndirectReference = addToBody(this.form).getIndirectReference();
        localPdfCatalog.put(PdfName.ACROFORM, localPdfIndirectReference);
      }
      return localPdfCatalog;
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  protected PdfIndirectReference getNewReference(PRIndirectReference paramPRIndirectReference)
  {
    return new PdfIndirectReference(0, getNewObjectNumber(paramPRIndirectReference.getReader(), paramPRIndirectReference.getNumber(), 0));
  }

  protected int getNewObjectNumber(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    IntHashtable localIntHashtable = (IntHashtable)this.readers2intrefs.get(paramPdfReader);
    int i = localIntHashtable.get(paramInt1);
    if (i == 0)
    {
      i = getIndirectReferenceNumber();
      localIntHashtable.put(paramInt1, i);
    }
    return i;
  }

  protected boolean setVisited(PRIndirectReference paramPRIndirectReference)
  {
    IntHashtable localIntHashtable = (IntHashtable)this.visited.get(paramPRIndirectReference.getReader());
    if (localIntHashtable != null)
      return localIntHashtable.put(paramPRIndirectReference.getNumber(), 1) != 0;
    return false;
  }

  protected boolean isVisited(PRIndirectReference paramPRIndirectReference)
  {
    IntHashtable localIntHashtable = (IntHashtable)this.visited.get(paramPRIndirectReference.getReader());
    if (localIntHashtable != null)
      return localIntHashtable.containsKey(paramPRIndirectReference.getNumber());
    return false;
  }

  protected boolean isVisited(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    IntHashtable localIntHashtable = (IntHashtable)this.readers2intrefs.get(paramPdfReader);
    return localIntHashtable.containsKey(paramInt1);
  }

  protected boolean isPage(PRIndirectReference paramPRIndirectReference)
  {
    IntHashtable localIntHashtable = (IntHashtable)this.pages2intrefs.get(paramPRIndirectReference.getReader());
    if (localIntHashtable != null)
      return localIntHashtable.containsKey(paramPRIndirectReference.getNumber());
    return false;
  }

  RandomAccessFileOrArray getReaderFile(PdfReader paramPdfReader)
  {
    return this.file;
  }

  public void openDoc()
  {
    if (!this.nd.isOpen())
      this.nd.open();
  }

  static
  {
    Integer localInteger = new Integer(1);
    widgetKeys.put(PdfName.SUBTYPE, localInteger);
    widgetKeys.put(PdfName.CONTENTS, localInteger);
    widgetKeys.put(PdfName.RECT, localInteger);
    widgetKeys.put(PdfName.NM, localInteger);
    widgetKeys.put(PdfName.M, localInteger);
    widgetKeys.put(PdfName.F, localInteger);
    widgetKeys.put(PdfName.BS, localInteger);
    widgetKeys.put(PdfName.BORDER, localInteger);
    widgetKeys.put(PdfName.AP, localInteger);
    widgetKeys.put(PdfName.AS, localInteger);
    widgetKeys.put(PdfName.C, localInteger);
    widgetKeys.put(PdfName.A, localInteger);
    widgetKeys.put(PdfName.STRUCTPARENT, localInteger);
    widgetKeys.put(PdfName.OC, localInteger);
    widgetKeys.put(PdfName.H, localInteger);
    widgetKeys.put(PdfName.MK, localInteger);
    widgetKeys.put(PdfName.DA, localInteger);
    widgetKeys.put(PdfName.Q, localInteger);
    fieldKeys.put(PdfName.AA, localInteger);
    fieldKeys.put(PdfName.FT, localInteger);
    fieldKeys.put(PdfName.TU, localInteger);
    fieldKeys.put(PdfName.TM, localInteger);
    fieldKeys.put(PdfName.FF, localInteger);
    fieldKeys.put(PdfName.V, localInteger);
    fieldKeys.put(PdfName.DV, localInteger);
    fieldKeys.put(PdfName.DS, localInteger);
    fieldKeys.put(PdfName.RV, localInteger);
    fieldKeys.put(PdfName.OPT, localInteger);
    fieldKeys.put(PdfName.MAXLEN, localInteger);
    fieldKeys.put(PdfName.TI, localInteger);
    fieldKeys.put(PdfName.I, localInteger);
    fieldKeys.put(PdfName.LOCK, localInteger);
    fieldKeys.put(PdfName.SV, localInteger);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfCopyFieldsImp
 * JD-Core Version:    0.6.0
 */