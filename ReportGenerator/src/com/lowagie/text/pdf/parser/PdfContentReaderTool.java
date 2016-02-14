package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PdfContentReaderTool
{
  public static String getDictionaryDetail(PdfDictionary paramPdfDictionary)
  {
    return getDictionaryDetail(paramPdfDictionary, 0);
  }

  public static String getDictionaryDetail(PdfDictionary paramPdfDictionary, int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = paramPdfDictionary.getKeys().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PdfName)((Iterator)localObject1).next();
      PdfObject localPdfObject = paramPdfDictionary.getDirectObject((PdfName)localObject2);
      if (localPdfObject.isDictionary())
        localArrayList.add(localObject2);
      localStringBuffer.append(localObject2);
      localStringBuffer.append('=');
      localStringBuffer.append(localPdfObject);
      localStringBuffer.append(", ");
    }
    localStringBuffer.setLength(localStringBuffer.length() - 2);
    localStringBuffer.append(')');
    Object localObject2 = localArrayList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject1 = (PdfName)((Iterator)localObject2).next();
      localStringBuffer.append('\n');
      for (int i = 0; i < paramInt + 1; i++)
        localStringBuffer.append('\t');
      localStringBuffer.append("Subdictionary ");
      localStringBuffer.append(localObject1);
      localStringBuffer.append(" = ");
      localStringBuffer.append(getDictionaryDetail(paramPdfDictionary.getAsDict((PdfName)localObject1), paramInt + 1));
    }
    return (String)(String)localStringBuffer.toString();
  }

  public static void listContentStreamForPage(PdfReader paramPdfReader, int paramInt, PrintWriter paramPrintWriter)
    throws IOException
  {
    paramPrintWriter.println("==============Page " + paramInt + "====================");
    paramPrintWriter.println("- - - - - Dictionary - - - - - -");
    PdfDictionary localPdfDictionary = paramPdfReader.getPageN(paramInt);
    paramPrintWriter.println(getDictionaryDetail(localPdfDictionary));
    paramPrintWriter.println("- - - - - Content Stream - - - - - -");
    RandomAccessFileOrArray localRandomAccessFileOrArray = paramPdfReader.getSafeFile();
    byte[] arrayOfByte = paramPdfReader.getPageContent(paramInt, localRandomAccessFileOrArray);
    localRandomAccessFileOrArray.close();
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    int i;
    while ((i = localByteArrayInputStream.read()) != -1)
      paramPrintWriter.print((char)i);
    paramPrintWriter.println("- - - - - Text Extraction - - - - - -");
    PdfTextExtractor localPdfTextExtractor = new PdfTextExtractor(paramPdfReader);
    String str = localPdfTextExtractor.getTextFromPage(paramInt);
    if (str.length() != 0)
      paramPrintWriter.println(str);
    else
      paramPrintWriter.println("No text found on page " + paramInt);
    paramPrintWriter.println();
  }

  public static void listContentStream(File paramFile, PrintWriter paramPrintWriter)
    throws IOException
  {
    PdfReader localPdfReader = new PdfReader(paramFile.getCanonicalPath());
    int i = localPdfReader.getNumberOfPages();
    for (int j = 1; j <= i; j++)
      listContentStreamForPage(localPdfReader, j, paramPrintWriter);
  }

  public static void listContentStream(File paramFile, int paramInt, PrintWriter paramPrintWriter)
    throws IOException
  {
    PdfReader localPdfReader = new PdfReader(paramFile.getCanonicalPath());
    listContentStreamForPage(localPdfReader, paramInt, paramPrintWriter);
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      if ((paramArrayOfString.length < 1) || (paramArrayOfString.length > 3))
      {
        System.out.println("Usage:  PdfContentReaderTool <pdf file> [<output file>|stdout] [<page num>]");
        return;
      }
      PrintWriter localPrintWriter = new PrintWriter(System.out);
      if ((paramArrayOfString.length >= 2) && (paramArrayOfString[1].compareToIgnoreCase("stdout") != 0))
      {
        System.out.println("Writing PDF content to " + paramArrayOfString[1]);
        localPrintWriter = new PrintWriter(new FileOutputStream(new File(paramArrayOfString[1])));
      }
      int i = -1;
      if (paramArrayOfString.length >= 3)
        i = Integer.parseInt(paramArrayOfString[2]);
      if (i == -1)
        listContentStream(new File(paramArrayOfString[0]), localPrintWriter);
      else
        listContentStream(new File(paramArrayOfString[0]), i, localPrintWriter);
      localPrintWriter.flush();
      if (paramArrayOfString.length >= 2)
      {
        localPrintWriter.close();
        System.out.println("Finished writing content to " + paramArrayOfString[1]);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace(System.err);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.PdfContentReaderTool
 * JD-Core Version:    0.6.0
 */