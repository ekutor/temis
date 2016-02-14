package com.lowagie.text;

public class BadElementException extends DocumentException
{
  private static final long serialVersionUID = -799006030723822254L;

  public BadElementException(Exception paramException)
  {
    super(paramException);
  }

  BadElementException()
  {
  }

  public BadElementException(String paramString)
  {
    super(paramString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.BadElementException
 * JD-Core Version:    0.6.0
 */