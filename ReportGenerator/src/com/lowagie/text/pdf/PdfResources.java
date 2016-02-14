package com.lowagie.text.pdf;

class PdfResources extends PdfDictionary
{
  void add(PdfName paramPdfName, PdfDictionary paramPdfDictionary)
  {
    if (paramPdfDictionary.size() == 0)
      return;
    PdfDictionary localPdfDictionary = getAsDict(paramPdfName);
    if (localPdfDictionary == null)
      put(paramPdfName, paramPdfDictionary);
    else
      localPdfDictionary.putAll(paramPdfDictionary);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfResources
 * JD-Core Version:    0.6.0
 */