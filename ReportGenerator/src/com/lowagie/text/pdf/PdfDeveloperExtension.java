package com.lowagie.text.pdf;

public class PdfDeveloperExtension
{
  public static final PdfDeveloperExtension ADOBE_1_7_EXTENSIONLEVEL3 = new PdfDeveloperExtension(PdfName.ADBE, PdfWriter.PDF_VERSION_1_7, 3);
  protected PdfName prefix;
  protected PdfName baseversion;
  protected int extensionLevel;

  public PdfDeveloperExtension(PdfName paramPdfName1, PdfName paramPdfName2, int paramInt)
  {
    this.prefix = paramPdfName1;
    this.baseversion = paramPdfName2;
    this.extensionLevel = paramInt;
  }

  public PdfName getPrefix()
  {
    return this.prefix;
  }

  public PdfName getBaseversion()
  {
    return this.baseversion;
  }

  public int getExtensionLevel()
  {
    return this.extensionLevel;
  }

  public PdfDictionary getDeveloperExtensions()
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(PdfName.BASEVERSION, this.baseversion);
    localPdfDictionary.put(PdfName.EXTENSIONLEVEL, new PdfNumber(this.extensionLevel));
    return localPdfDictionary;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfDeveloperExtension
 * JD-Core Version:    0.6.0
 */