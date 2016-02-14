package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfLiteral;
import java.util.ArrayList;

public abstract interface ContentOperator
{
  public abstract void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.ContentOperator
 * JD-Core Version:    0.6.0
 */