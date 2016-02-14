package com.lowagie.text;

public class DocumentException extends Exception
{
  private static final long serialVersionUID = -2191131489390840739L;

  public DocumentException(Exception paramException)
  {
    super(paramException);
  }

  public DocumentException()
  {
  }

  public DocumentException(String paramString)
  {
    super(paramString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.DocumentException
 * JD-Core Version:    0.6.0
 */