package com.lowagie.text;

public class Header extends Meta
{
  private StringBuffer name;

  public Header(String paramString1, String paramString2)
  {
    super(0, paramString2);
    this.name = new StringBuffer(paramString1);
  }

  public String getName()
  {
    return this.name.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Header
 * JD-Core Version:    0.6.0
 */