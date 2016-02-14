package com.lowagie.text.pdf;

public class PdfNull extends PdfObject
{
  public static final PdfNull PDFNULL = new PdfNull();
  private static final String CONTENT = "null";

  public PdfNull()
  {
    super(8, "null");
  }

  public String toString()
  {
    return "null";
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfNull
 * JD-Core Version:    0.6.0
 */