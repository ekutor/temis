package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfName;

public abstract interface PdfVersion
{
  public abstract void setPdfVersion(char paramChar);

  public abstract void setAtLeastPdfVersion(char paramChar);

  public abstract void setPdfVersion(PdfName paramPdfName);

  public abstract void addDeveloperExtension(PdfDeveloperExtension paramPdfDeveloperExtension);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.interfaces.PdfVersion
 * JD-Core Version:    0.6.0
 */