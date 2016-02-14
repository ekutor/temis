package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfStructureTreeRoot extends PdfDictionary
{
  private HashMap parentTree = new HashMap();
  private PdfIndirectReference reference;
  private PdfWriter writer;

  PdfStructureTreeRoot(PdfWriter paramPdfWriter)
  {
    super(PdfName.STRUCTTREEROOT);
    this.writer = paramPdfWriter;
    this.reference = paramPdfWriter.getPdfIndirectReference();
  }

  public void mapRole(PdfName paramPdfName1, PdfName paramPdfName2)
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)get(PdfName.ROLEMAP);
    if (localPdfDictionary == null)
    {
      localPdfDictionary = new PdfDictionary();
      put(PdfName.ROLEMAP, localPdfDictionary);
    }
    localPdfDictionary.put(paramPdfName1, paramPdfName2);
  }

  public PdfWriter getWriter()
  {
    return this.writer;
  }

  public PdfIndirectReference getReference()
  {
    return this.reference;
  }

  void setPageMark(int paramInt, PdfIndirectReference paramPdfIndirectReference)
  {
    Integer localInteger = new Integer(paramInt);
    PdfArray localPdfArray = (PdfArray)this.parentTree.get(localInteger);
    if (localPdfArray == null)
    {
      localPdfArray = new PdfArray();
      this.parentTree.put(localInteger, localPdfArray);
    }
    localPdfArray.add(paramPdfIndirectReference);
  }

  private void nodeProcess(PdfDictionary paramPdfDictionary, PdfIndirectReference paramPdfIndirectReference)
    throws IOException
  {
    PdfObject localPdfObject = paramPdfDictionary.get(PdfName.K);
    if ((localPdfObject != null) && (localPdfObject.isArray()) && (!((PdfObject)((PdfArray)localPdfObject).getArrayList().get(0)).isNumber()))
    {
      PdfArray localPdfArray = (PdfArray)localPdfObject;
      ArrayList localArrayList = localPdfArray.getArrayList();
      for (int i = 0; i < localArrayList.size(); i++)
      {
        PdfStructureElement localPdfStructureElement = (PdfStructureElement)localArrayList.get(i);
        localArrayList.set(i, localPdfStructureElement.getReference());
        nodeProcess(localPdfStructureElement, localPdfStructureElement.getReference());
      }
    }
    if (paramPdfIndirectReference != null)
      this.writer.addToBody(paramPdfDictionary, paramPdfIndirectReference);
  }

  void buildTree()
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    Object localObject = this.parentTree.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Integer localInteger = (Integer)((Iterator)localObject).next();
      PdfArray localPdfArray = (PdfArray)this.parentTree.get(localInteger);
      localHashMap.put(localInteger, this.writer.addToBody(localPdfArray).getIndirectReference());
    }
    localObject = PdfNumberTree.writeTree(localHashMap, this.writer);
    if (localObject != null)
      put(PdfName.PARENTTREE, this.writer.addToBody((PdfObject)localObject).getIndirectReference());
    nodeProcess(this, this.reference);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfStructureTreeRoot
 * JD-Core Version:    0.6.0
 */