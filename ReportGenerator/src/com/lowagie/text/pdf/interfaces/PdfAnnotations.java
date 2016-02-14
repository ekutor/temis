package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfFormField;

public abstract interface PdfAnnotations
{
  public abstract PdfAcroForm getAcroForm();

  public abstract void addAnnotation(PdfAnnotation paramPdfAnnotation);

  public abstract void addCalculationOrder(PdfFormField paramPdfFormField);

  public abstract void setSigFlags(int paramInt);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.interfaces.PdfAnnotations
 * JD-Core Version:    0.6.0
 */