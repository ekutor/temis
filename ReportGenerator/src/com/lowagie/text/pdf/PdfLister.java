package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class PdfLister
{
  PrintStream out;

  public PdfLister(PrintStream paramPrintStream)
  {
    this.out = paramPrintStream;
  }

  public void listAnyObject(PdfObject paramPdfObject)
  {
    switch (paramPdfObject.type())
    {
    case 5:
      listArray((PdfArray)paramPdfObject);
      break;
    case 6:
      listDict((PdfDictionary)paramPdfObject);
      break;
    case 3:
      this.out.println("(" + paramPdfObject.toString() + ")");
      break;
    case 4:
    default:
      this.out.println(paramPdfObject.toString());
    }
  }

  public void listDict(PdfDictionary paramPdfDictionary)
  {
    this.out.println("<<");
    Iterator localIterator = paramPdfDictionary.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      PdfObject localPdfObject = paramPdfDictionary.get(localPdfName);
      this.out.print(localPdfName.toString());
      this.out.print(' ');
      listAnyObject(localPdfObject);
    }
    this.out.println(">>");
  }

  public void listArray(PdfArray paramPdfArray)
  {
    this.out.println('[');
    ListIterator localListIterator = paramPdfArray.listIterator();
    while (localListIterator.hasNext())
    {
      PdfObject localPdfObject = (PdfObject)localListIterator.next();
      listAnyObject(localPdfObject);
    }
    this.out.println(']');
  }

  public void listStream(PRStream paramPRStream, PdfReaderInstance paramPdfReaderInstance)
  {
    try
    {
      listDict(paramPRStream);
      this.out.println("startstream");
      byte[] arrayOfByte = PdfReader.getStreamBytes(paramPRStream);
      int i = arrayOfByte.length - 1;
      for (int j = 0; j < i; j++)
      {
        if ((arrayOfByte[j] != 13) || (arrayOfByte[(j + 1)] == 10))
          continue;
        arrayOfByte[j] = 10;
      }
      this.out.println(new String(arrayOfByte));
      this.out.println("endstream");
    }
    catch (IOException localIOException)
    {
      System.err.println("I/O exception: " + localIOException);
    }
  }

  public void listPage(PdfImportedPage paramPdfImportedPage)
  {
    int i = paramPdfImportedPage.getPageNumber();
    PdfReaderInstance localPdfReaderInstance = paramPdfImportedPage.getPdfReaderInstance();
    PdfReader localPdfReader = localPdfReaderInstance.getReader();
    PdfDictionary localPdfDictionary = localPdfReader.getPageN(i);
    listDict(localPdfDictionary);
    PdfObject localPdfObject1 = PdfReader.getPdfObject(localPdfDictionary.get(PdfName.CONTENTS));
    if (localPdfObject1 == null)
      return;
    switch (localPdfObject1.type)
    {
    case 7:
      listStream((PRStream)localPdfObject1, localPdfReaderInstance);
      break;
    case 5:
      ListIterator localListIterator = ((PdfArray)localPdfObject1).listIterator();
      while (localListIterator.hasNext())
      {
        PdfObject localPdfObject2 = PdfReader.getPdfObject((PdfObject)localListIterator.next());
        listStream((PRStream)localPdfObject2, localPdfReaderInstance);
        this.out.println("-----------");
      }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfLister
 * JD-Core Version:    0.6.0
 */