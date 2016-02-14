package com.lowagie.text;

public abstract interface DocListener extends ElementListener
{
  public abstract void open();

  public abstract void close();

  public abstract boolean newPage();

  public abstract boolean setPageSize(Rectangle paramRectangle);

  public abstract boolean setMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

  public abstract boolean setMarginMirroring(boolean paramBoolean);

  public abstract boolean setMarginMirroringTopBottom(boolean paramBoolean);

  public abstract void setPageCount(int paramInt);

  public abstract void resetPageCount();

  public abstract void setHeader(HeaderFooter paramHeaderFooter);

  public abstract void resetHeader();

  public abstract void setFooter(HeaderFooter paramHeaderFooter);

  public abstract void resetFooter();
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.DocListener
 * JD-Core Version:    0.6.0
 */