package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

class PdfReaderInstance
{
  static final PdfLiteral IDENTITYMATRIX = new PdfLiteral("[1 0 0 1 0 0]");
  static final PdfNumber ONE = new PdfNumber(1);
  int[] myXref;
  PdfReader reader;
  RandomAccessFileOrArray file;
  HashMap importedPages = new HashMap();
  PdfWriter writer;
  HashMap visited = new HashMap();
  ArrayList nextRound = new ArrayList();

  PdfReaderInstance(PdfReader paramPdfReader, PdfWriter paramPdfWriter)
  {
    this.reader = paramPdfReader;
    this.writer = paramPdfWriter;
    this.file = paramPdfReader.getSafeFile();
    this.myXref = new int[paramPdfReader.getXrefSize()];
  }

  PdfReader getReader()
  {
    return this.reader;
  }

  PdfImportedPage getImportedPage(int paramInt)
  {
    if (!this.reader.isOpenedWithFullPermissions())
      throw new IllegalArgumentException("PdfReader not opened with owner password");
    if ((paramInt < 1) || (paramInt > this.reader.getNumberOfPages()))
      throw new IllegalArgumentException("Invalid page number: " + paramInt);
    Integer localInteger = new Integer(paramInt);
    PdfImportedPage localPdfImportedPage = (PdfImportedPage)this.importedPages.get(localInteger);
    if (localPdfImportedPage == null)
    {
      localPdfImportedPage = new PdfImportedPage(this, this.writer, paramInt);
      this.importedPages.put(localInteger, localPdfImportedPage);
    }
    return localPdfImportedPage;
  }

  int getNewObjectNumber(int paramInt1, int paramInt2)
  {
    if (this.myXref[paramInt1] == 0)
    {
      this.myXref[paramInt1] = this.writer.getIndirectReferenceNumber();
      this.nextRound.add(new Integer(paramInt1));
    }
    return this.myXref[paramInt1];
  }

  RandomAccessFileOrArray getReaderFile()
  {
    return this.file;
  }

  PdfObject getResources(int paramInt)
  {
    PdfObject localPdfObject = PdfReader.getPdfObjectRelease(this.reader.getPageNRelease(paramInt).get(PdfName.RESOURCES));
    return localPdfObject;
  }

  PdfStream getFormXObject(int paramInt1, int paramInt2)
    throws IOException
  {
    PdfDictionary localPdfDictionary1 = this.reader.getPageNRelease(paramInt1);
    PdfObject localPdfObject = PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.CONTENTS));
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    byte[] arrayOfByte = null;
    if (localPdfObject != null)
    {
      if (localPdfObject.isStream())
        localPdfDictionary2.putAll((PRStream)localPdfObject);
      else
        arrayOfByte = this.reader.getPageContent(paramInt1, this.file);
    }
    else
      arrayOfByte = new byte[0];
    localPdfDictionary2.put(PdfName.RESOURCES, PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.RESOURCES)));
    localPdfDictionary2.put(PdfName.TYPE, PdfName.XOBJECT);
    localPdfDictionary2.put(PdfName.SUBTYPE, PdfName.FORM);
    PdfImportedPage localPdfImportedPage = (PdfImportedPage)this.importedPages.get(new Integer(paramInt1));
    localPdfDictionary2.put(PdfName.BBOX, new PdfRectangle(localPdfImportedPage.getBoundingBox()));
    PdfArray localPdfArray = localPdfImportedPage.getMatrix();
    if (localPdfArray == null)
      localPdfDictionary2.put(PdfName.MATRIX, IDENTITYMATRIX);
    else
      localPdfDictionary2.put(PdfName.MATRIX, localPdfArray);
    localPdfDictionary2.put(PdfName.FORMTYPE, ONE);
    PRStream localPRStream;
    if (arrayOfByte == null)
    {
      localPRStream = new PRStream((PRStream)localPdfObject, localPdfDictionary2);
    }
    else
    {
      localPRStream = new PRStream(this.reader, arrayOfByte, paramInt2);
      localPRStream.putAll(localPdfDictionary2);
    }
    return localPRStream;
  }

  void writeAllVisited()
    throws IOException
  {
    if (!this.nextRound.isEmpty())
    {
      ArrayList localArrayList = this.nextRound;
      this.nextRound = new ArrayList();
      for (int i = 0; i < localArrayList.size(); i++)
      {
        Integer localInteger = (Integer)localArrayList.get(i);
        if (this.visited.containsKey(localInteger))
          continue;
        this.visited.put(localInteger, null);
        int j = localInteger.intValue();
        this.writer.addToBody(this.reader.getPdfObjectRelease(j), this.myXref[j]);
      }
    }
  }

  void writeAllPages()
    throws IOException
  {
    try
    {
      this.file.reOpen();
      Iterator localIterator = this.importedPages.values().iterator();
      while (localIterator.hasNext())
      {
        PdfImportedPage localPdfImportedPage = (PdfImportedPage)localIterator.next();
        this.writer.addToBody(localPdfImportedPage.getFormXObject(this.writer.getCompressionLevel()), localPdfImportedPage.getIndirectReference());
      }
      writeAllVisited();
    }
    finally
    {
      try
      {
        this.reader.close();
        this.file.close();
      }
      catch (Exception localException2)
      {
      }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfReaderInstance
 * JD-Core Version:    0.6.0
 */