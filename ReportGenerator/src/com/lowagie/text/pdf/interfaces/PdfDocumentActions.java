package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;

public abstract interface PdfDocumentActions
{
  public abstract void setOpenAction(String paramString);

  public abstract void setOpenAction(PdfAction paramPdfAction);

  public abstract void setAdditionalAction(PdfName paramPdfName, PdfAction paramPdfAction)
    throws DocumentException;
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.interfaces.PdfDocumentActions
 * JD-Core Version:    0.6.0
 */