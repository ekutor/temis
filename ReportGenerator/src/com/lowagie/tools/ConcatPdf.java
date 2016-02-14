package com.lowagie.tools;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ConcatPdf
{
  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length < 2)
      System.err.println("arguments: file1 [file2 ...] destfile");
    else
      try
      {
        int i = 0;
        ArrayList localArrayList = new ArrayList();
        int j = 0;
        String str = paramArrayOfString[(paramArrayOfString.length - 1)];
        Document localDocument = null;
        PdfCopy localPdfCopy = null;
        while (j < paramArrayOfString.length - 1)
        {
          PdfReader localPdfReader = new PdfReader(paramArrayOfString[j]);
          localPdfReader.consolidateNamedDestinations();
          int k = localPdfReader.getNumberOfPages();
          List localList = SimpleBookmark.getBookmark(localPdfReader);
          if (localList != null)
          {
            if (i != 0)
              SimpleBookmark.shiftPageNumbers(localList, i, null);
            localArrayList.addAll(localList);
          }
          i += k;
          System.out.println("There are " + k + " pages in " + paramArrayOfString[j]);
          if (j == 0)
          {
            localDocument = new Document(localPdfReader.getPageSizeWithRotation(1));
            localPdfCopy = new PdfCopy(localDocument, new FileOutputStream(str));
            localDocument.open();
          }
          int m = 0;
          while (m < k)
          {
            m++;
            PdfImportedPage localPdfImportedPage = localPdfCopy.getImportedPage(localPdfReader, m);
            localPdfCopy.addPage(localPdfImportedPage);
            System.out.println("Processed page " + m);
          }
          localPdfCopy.freeReader(localPdfReader);
          j++;
        }
        if (!localArrayList.isEmpty())
          localPdfCopy.setOutlines(localArrayList);
        localDocument.close();
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.ConcatPdf
 * JD-Core Version:    0.6.0
 */