package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.ArrayList;

public class PdfPages
{
  private ArrayList pages = new ArrayList();
  private ArrayList parents = new ArrayList();
  private int leafSize = 10;
  private PdfWriter writer;
  private PdfIndirectReference topParent;

  PdfPages(PdfWriter paramPdfWriter)
  {
    this.writer = paramPdfWriter;
  }

  void addPage(PdfDictionary paramPdfDictionary)
  {
    try
    {
      if (this.pages.size() % this.leafSize == 0)
        this.parents.add(this.writer.getPdfIndirectReference());
      PdfIndirectReference localPdfIndirectReference1 = (PdfIndirectReference)this.parents.get(this.parents.size() - 1);
      paramPdfDictionary.put(PdfName.PARENT, localPdfIndirectReference1);
      PdfIndirectReference localPdfIndirectReference2 = this.writer.getCurrentPage();
      this.writer.addToBody(paramPdfDictionary, localPdfIndirectReference2);
      this.pages.add(localPdfIndirectReference2);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  PdfIndirectReference addPageRef(PdfIndirectReference paramPdfIndirectReference)
  {
    try
    {
      if (this.pages.size() % this.leafSize == 0)
        this.parents.add(this.writer.getPdfIndirectReference());
      this.pages.add(paramPdfIndirectReference);
      return (PdfIndirectReference)this.parents.get(this.parents.size() - 1);
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  PdfIndirectReference writePageTree()
    throws IOException
  {
    if (this.pages.isEmpty())
      throw new IOException("The document has no pages.");
    int i = 1;
    Object localObject1 = this.parents;
    Object localObject2 = this.pages;
    for (ArrayList localArrayList1 = new ArrayList(); ; localArrayList1 = new ArrayList())
    {
      i *= this.leafSize;
      int j = this.leafSize;
      int k = ((ArrayList)localObject2).size() % this.leafSize;
      if (k == 0)
        k = this.leafSize;
      for (int m = 0; m < ((ArrayList)localObject1).size(); m++)
      {
        int i1 = i;
        int n;
        if (m == ((ArrayList)localObject1).size() - 1)
        {
          n = k;
          i1 = this.pages.size() % i;
          if (i1 == 0)
            i1 = i;
        }
        else
        {
          n = j;
        }
        PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.PAGES);
        localPdfDictionary.put(PdfName.COUNT, new PdfNumber(i1));
        PdfArray localPdfArray = new PdfArray();
        ArrayList localArrayList2 = localPdfArray.getArrayList();
        localArrayList2.addAll(((ArrayList)localObject2).subList(m * j, m * j + n));
        localPdfDictionary.put(PdfName.KIDS, localPdfArray);
        if (((ArrayList)localObject1).size() > 1)
        {
          if (m % this.leafSize == 0)
            localArrayList1.add(this.writer.getPdfIndirectReference());
          localPdfDictionary.put(PdfName.PARENT, (PdfIndirectReference)localArrayList1.get(m / this.leafSize));
        }
        else
        {
          localPdfDictionary.put(PdfName.ITXT, new PdfString(Document.getRelease()));
        }
        this.writer.addToBody(localPdfDictionary, (PdfIndirectReference)((ArrayList)localObject1).get(m));
      }
      if (((ArrayList)localObject1).size() == 1)
      {
        this.topParent = ((PdfIndirectReference)((ArrayList)localObject1).get(0));
        return this.topParent;
      }
      localObject2 = localObject1;
      localObject1 = localArrayList1;
    }
  }

  PdfIndirectReference getTopParent()
  {
    return this.topParent;
  }

  void setLinearMode(PdfIndirectReference paramPdfIndirectReference)
  {
    if (this.parents.size() > 1)
      throw new RuntimeException("Linear page mode can only be called with a single parent.");
    if (paramPdfIndirectReference != null)
    {
      this.topParent = paramPdfIndirectReference;
      this.parents.clear();
      this.parents.add(paramPdfIndirectReference);
    }
    this.leafSize = 10000000;
  }

  void addPage(PdfIndirectReference paramPdfIndirectReference)
  {
    this.pages.add(paramPdfIndirectReference);
  }

  int reorderPages(int[] paramArrayOfInt)
    throws DocumentException
  {
    if (paramArrayOfInt == null)
      return this.pages.size();
    if (this.parents.size() > 1)
      throw new DocumentException("Page reordering requires a single parent in the page tree. Call PdfWriter.setLinearMode() after open.");
    if (paramArrayOfInt.length != this.pages.size())
      throw new DocumentException("Page reordering requires an array with the same size as the number of pages.");
    int i = this.pages.size();
    boolean[] arrayOfBoolean = new boolean[i];
    for (int j = 0; j < i; j++)
    {
      k = paramArrayOfInt[j];
      if ((k < 1) || (k > i))
        throw new DocumentException("Page reordering requires pages between 1 and " + i + ". Found " + k + ".");
      if (arrayOfBoolean[(k - 1)] != 0)
        throw new DocumentException("Page reordering requires no page repetition. Page " + k + " is repeated.");
      arrayOfBoolean[(k - 1)] = true;
    }
    Object[] arrayOfObject = this.pages.toArray();
    for (int k = 0; k < i; k++)
      this.pages.set(k, arrayOfObject[(paramArrayOfInt[k] - 1)]);
    return i;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPages
 * JD-Core Version:    0.6.0
 */