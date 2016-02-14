package com.lowagie.text.pdf;

import java.io.IOException;

public class PdfRendition extends PdfDictionary
{
  PdfRendition(String paramString1, PdfFileSpecification paramPdfFileSpecification, String paramString2)
    throws IOException
  {
    put(PdfName.S, new PdfName("MR"));
    put(PdfName.N, new PdfString("Rendition for " + paramString1));
    put(PdfName.C, new PdfMediaClipData(paramString1, paramPdfFileSpecification, paramString2));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfRendition
 * JD-Core Version:    0.6.0
 */