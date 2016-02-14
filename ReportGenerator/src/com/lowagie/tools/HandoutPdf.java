package com.lowagie.tools;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class HandoutPdf
{
  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length != 3)
      System.err.println("arguments: srcfile destfile pages");
    else
      try
      {
        int i = Integer.parseInt(paramArrayOfString[2]);
        if ((i < 2) || (i > 8))
          throw new DocumentException("You can't have " + i + " pages on one page (minimum 2; maximum 8).");
        float f1 = 30.0F;
        float f2 = 280.0F;
        float f3 = 320.0F;
        float f4 = 565.0F;
        float[] arrayOfFloat1 = new float[i];
        float[] arrayOfFloat2 = new float[i];
        float f5 = (778.0F - 20.0F * (i - 1)) / i;
        arrayOfFloat1[0] = 812.0F;
        arrayOfFloat2[0] = (812.0F - f5);
        for (int j = 1; j < i; j++)
        {
          arrayOfFloat1[j] = (arrayOfFloat2[(j - 1)] - 20.0F);
          arrayOfFloat1[j] -= f5;
        }
        PdfReader localPdfReader = new PdfReader(paramArrayOfString[0]);
        int k = localPdfReader.getNumberOfPages();
        System.out.println("There are " + k + " pages in the original file.");
        Document localDocument = new Document(PageSize.A4);
        PdfWriter localPdfWriter = PdfWriter.getInstance(localDocument, new FileOutputStream(paramArrayOfString[1]));
        localDocument.open();
        PdfContentByte localPdfContentByte = localPdfWriter.getDirectContent();
        int n = 0;
        int i1 = 0;
        while (n < k)
        {
          n++;
          Rectangle localRectangle = localPdfReader.getPageSizeWithRotation(n);
          float f6 = (f2 - f1) / localRectangle.getWidth();
          float f7 = (arrayOfFloat1[i1] - arrayOfFloat2[i1]) / localRectangle.getHeight();
          float f8 = f6 < f7 ? f6 : f7;
          float f9 = f6 == f8 ? 0.0F : (f2 - f1 - localRectangle.getWidth() * f8) / 2.0F;
          float f10 = f7 == f8 ? 0.0F : (arrayOfFloat1[i1] - arrayOfFloat2[i1] - localRectangle.getHeight() * f8) / 2.0F;
          PdfImportedPage localPdfImportedPage = localPdfWriter.getImportedPage(localPdfReader, n);
          int m = localPdfReader.getPageRotation(n);
          if ((m == 90) || (m == 270))
            localPdfContentByte.addTemplate(localPdfImportedPage, 0.0F, -f8, f8, 0.0F, f1 + f9, arrayOfFloat2[i1] + f10 + localRectangle.getHeight() * f8);
          else
            localPdfContentByte.addTemplate(localPdfImportedPage, f8, 0.0F, 0.0F, f8, f1 + f9, arrayOfFloat2[i1] + f10);
          localPdfContentByte.setRGBColorStroke(192, 192, 192);
          localPdfContentByte.rectangle(f3 - 5.0F, arrayOfFloat2[i1] - 5.0F, f4 - f3 + 10.0F, arrayOfFloat1[i1] - arrayOfFloat2[i1] + 10.0F);
          float f11 = arrayOfFloat1[i1] - 19.0F;
          while (f11 > arrayOfFloat2[i1])
          {
            localPdfContentByte.moveTo(f3, f11);
            localPdfContentByte.lineTo(f4, f11);
            f11 -= 16.0F;
          }
          localPdfContentByte.rectangle(f1 + f9, arrayOfFloat2[i1] + f10, localRectangle.getWidth() * f8, localRectangle.getHeight() * f8);
          localPdfContentByte.stroke();
          System.out.println("Processed page " + n);
          i1++;
          if (i1 != i)
            continue;
          i1 = 0;
          localDocument.newPage();
        }
        localDocument.close();
      }
      catch (Exception localException)
      {
        System.err.println(localException.getClass().getName() + ": " + localException.getMessage());
      }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.HandoutPdf
 * JD-Core Version:    0.6.0
 */