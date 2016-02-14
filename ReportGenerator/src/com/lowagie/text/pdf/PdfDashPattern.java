package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PdfDashPattern extends PdfArray
{
  private float dash = -1.0F;
  private float gap = -1.0F;
  private float phase = -1.0F;

  public PdfDashPattern()
  {
  }

  public PdfDashPattern(float paramFloat)
  {
    super(new PdfNumber(paramFloat));
    this.dash = paramFloat;
  }

  public PdfDashPattern(float paramFloat1, float paramFloat2)
  {
    super(new PdfNumber(paramFloat1));
    add(new PdfNumber(paramFloat2));
    this.dash = paramFloat1;
    this.gap = paramFloat2;
  }

  public PdfDashPattern(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    super(new PdfNumber(paramFloat1));
    add(new PdfNumber(paramFloat2));
    this.dash = paramFloat1;
    this.gap = paramFloat2;
    this.phase = paramFloat3;
  }

  public void add(float paramFloat)
  {
    add(new PdfNumber(paramFloat));
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(91);
    if (this.dash >= 0.0F)
    {
      new PdfNumber(this.dash).toPdf(paramPdfWriter, paramOutputStream);
      if (this.gap >= 0.0F)
      {
        paramOutputStream.write(32);
        new PdfNumber(this.gap).toPdf(paramPdfWriter, paramOutputStream);
      }
    }
    paramOutputStream.write(93);
    if (this.phase >= 0.0F)
    {
      paramOutputStream.write(32);
      new PdfNumber(this.phase).toPdf(paramPdfWriter, paramOutputStream);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfDashPattern
 * JD-Core Version:    0.6.0
 */