package com.lowagie.text.pdf;

import java.io.IOException;

public class PdfMediaClipData extends PdfDictionary
{
  PdfMediaClipData(String paramString1, PdfFileSpecification paramPdfFileSpecification, String paramString2)
    throws IOException
  {
    put(PdfName.TYPE, new PdfName("MediaClip"));
    put(PdfName.S, new PdfName("MCD"));
    put(PdfName.N, new PdfString("Media clip for " + paramString1));
    put(new PdfName("CT"), new PdfString(paramString2));
    PdfDictionary localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(new PdfName("TF"), new PdfString("TEMPACCESS"));
    put(new PdfName("P"), localPdfDictionary);
    put(PdfName.D, paramPdfFileSpecification.getReference());
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfMediaClipData
 * JD-Core Version:    0.6.0
 */