package com.lowagie.text.pdf.codec.wmf;

public class MetaObject
{
  public static final int META_NOT_SUPPORTED = 0;
  public static final int META_PEN = 1;
  public static final int META_BRUSH = 2;
  public static final int META_FONT = 3;
  public int type = 0;

  public MetaObject()
  {
  }

  public MetaObject(int paramInt)
  {
    this.type = paramInt;
  }

  public int getType()
  {
    return this.type;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.MetaObject
 * JD-Core Version:    0.6.0
 */