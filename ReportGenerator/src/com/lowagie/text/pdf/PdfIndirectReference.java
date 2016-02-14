package com.lowagie.text.pdf;

public class PdfIndirectReference extends PdfObject
{
  protected int number;
  protected int generation = 0;

  protected PdfIndirectReference()
  {
    super(0);
  }

  PdfIndirectReference(int paramInt1, int paramInt2, int paramInt3)
  {
    super(0, paramInt2 + " " + paramInt3 + " R");
    this.number = paramInt2;
    this.generation = paramInt3;
  }

  PdfIndirectReference(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 0);
  }

  public int getNumber()
  {
    return this.number;
  }

  public int getGeneration()
  {
    return this.generation;
  }

  public String toString()
  {
    return this.number + " " + this.generation + " R";
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfIndirectReference
 * JD-Core Version:    0.6.0
 */