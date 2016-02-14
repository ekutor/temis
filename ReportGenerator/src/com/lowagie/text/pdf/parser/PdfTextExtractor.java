package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;

public class PdfTextExtractor
{
  private final PdfReader reader;
  private final SimpleTextExtractingPdfContentStreamProcessor extractionProcessor;

  public PdfTextExtractor(PdfReader paramPdfReader)
  {
    this.reader = paramPdfReader;
    this.extractionProcessor = new SimpleTextExtractingPdfContentStreamProcessor();
  }

  private byte[] getContentBytesForPage(int paramInt)
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = this.reader.getSafeFile();
    byte[] arrayOfByte = this.reader.getPageContent(paramInt, localRandomAccessFileOrArray);
    localRandomAccessFileOrArray.close();
    return arrayOfByte;
  }

  public String getTextFromPage(int paramInt)
    throws IOException
  {
    PdfDictionary localPdfDictionary1 = this.reader.getPageN(paramInt);
    PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.RESOURCES);
    this.extractionProcessor.processContent(getContentBytesForPage(paramInt), localPdfDictionary2);
    return this.extractionProcessor.getResultantText();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.PdfTextExtractor
 * JD-Core Version:    0.6.0
 */