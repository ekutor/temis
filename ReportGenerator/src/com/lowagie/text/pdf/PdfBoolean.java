package com.lowagie.text.pdf;

public class PdfBoolean extends PdfObject
{
  public static final PdfBoolean PDFTRUE = new PdfBoolean(true);
  public static final PdfBoolean PDFFALSE = new PdfBoolean(false);
  public static final String TRUE = "true";
  public static final String FALSE = "false";
  private boolean value;

  public PdfBoolean(boolean paramBoolean)
  {
    super(1);
    if (paramBoolean)
      setContent("true");
    else
      setContent("false");
    this.value = paramBoolean;
  }

  public PdfBoolean(String paramString)
    throws BadPdfFormatException
  {
    super(1, paramString);
    if (paramString.equals("true"))
      this.value = true;
    else if (paramString.equals("false"))
      this.value = false;
    else
      throw new BadPdfFormatException("The value has to be 'true' of 'false', instead of '" + paramString + "'.");
  }

  public boolean booleanValue()
  {
    return this.value;
  }

  public String toString()
  {
    return this.value ? "true" : "false";
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfBoolean
 * JD-Core Version:    0.6.0
 */