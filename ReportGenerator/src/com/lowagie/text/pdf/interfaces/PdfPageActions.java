package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfTransition;

public abstract interface PdfPageActions
{
  public abstract void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction)
    throws DocumentException;

  public abstract void setDuration(int paramInt);

  public abstract void setTransition(PdfTransition paramPdfTransition);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.interfaces.PdfPageActions
 * JD-Core Version:    0.6.0
 */