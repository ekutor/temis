package com.lowagie.tools;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SplitPdf
{
  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length != 4)
      System.err.println("arguments: srcfile destfile1 destfile2 pagenumber");
    else
      try
      {
        int i = Integer.parseInt(paramArrayOfString[3]);
        PdfReader localPdfReader = new PdfReader(paramArrayOfString[0]);
        int j = localPdfReader.getNumberOfPages();
        System.out.println("There are " + j + " pages in the original file.");
        if ((i < 2) || (i > j))
          throw new DocumentException("You can't split this document at page " + i + "; there is no such page.");
        Document localDocument1 = new Document(localPdfReader.getPageSizeWithRotation(1));
        Document localDocument2 = new Document(localPdfReader.getPageSizeWithRotation(i));
        PdfWriter localPdfWriter1 = PdfWriter.getInstance(localDocument1, new FileOutputStream(paramArrayOfString[1]));
        PdfWriter localPdfWriter2 = PdfWriter.getInstance(localDocument2, new FileOutputStream(paramArrayOfString[2]));
        localDocument1.open();
        PdfContentByte localPdfContentByte1 = localPdfWriter1.getDirectContent();
        localDocument2.open();
        PdfContentByte localPdfContentByte2 = localPdfWriter2.getDirectContent();
        int m = 0;
        PdfImportedPage localPdfImportedPage;
        int k;
        while (m < i - 1)
        {
          m++;
          localDocument1.setPageSize(localPdfReader.getPageSizeWithRotation(m));
          localDocument1.newPage();
          localPdfImportedPage = localPdfWriter1.getImportedPage(localPdfReader, m);
          k = localPdfReader.getPageRotation(m);
          if ((k == 90) || (k == 270))
          {
            localPdfContentByte1.addTemplate(localPdfImportedPage, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, localPdfReader.getPageSizeWithRotation(m).getHeight());
            continue;
          }
          localPdfContentByte1.addTemplate(localPdfImportedPage, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
        }
        while (m < j)
        {
          m++;
          localDocument2.setPageSize(localPdfReader.getPageSizeWithRotation(m));
          localDocument2.newPage();
          localPdfImportedPage = localPdfWriter2.getImportedPage(localPdfReader, m);
          k = localPdfReader.getPageRotation(m);
          if ((k == 90) || (k == 270))
            localPdfContentByte2.addTemplate(localPdfImportedPage, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, localPdfReader.getPageSizeWithRotation(m).getHeight());
          else
            localPdfContentByte2.addTemplate(localPdfImportedPage, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
          System.out.println("Processed page " + m);
        }
        localDocument1.close();
        localDocument2.close();
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.SplitPdf
 * JD-Core Version:    0.6.0
 */