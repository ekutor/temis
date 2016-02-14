package com.lowagie.text.pdf;

public abstract interface ExtraEncoding
{
  public abstract byte[] charToByte(String paramString1, String paramString2);

  public abstract byte[] charToByte(char paramChar, String paramString);

  public abstract String byteToChar(byte[] paramArrayOfByte, String paramString);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ExtraEncoding
 * JD-Core Version:    0.6.0
 */