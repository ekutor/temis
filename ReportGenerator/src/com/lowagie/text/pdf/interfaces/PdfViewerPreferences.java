package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

public abstract interface PdfViewerPreferences
{
  public abstract void setViewerPreferences(int paramInt);

  public abstract void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.interfaces.PdfViewerPreferences
 * JD-Core Version:    0.6.0
 */