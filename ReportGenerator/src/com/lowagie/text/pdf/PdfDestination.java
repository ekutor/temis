package com.lowagie.text.pdf;

public class PdfDestination extends PdfArray
{
  public static final int XYZ = 0;
  public static final int FIT = 1;
  public static final int FITH = 2;
  public static final int FITV = 3;
  public static final int FITR = 4;
  public static final int FITB = 5;
  public static final int FITBH = 6;
  public static final int FITBV = 7;
  private boolean status = false;

  public PdfDestination(int paramInt)
  {
    if (paramInt == 5)
      add(PdfName.FITB);
    else
      add(PdfName.FIT);
  }

  public PdfDestination(int paramInt, float paramFloat)
  {
    super(new PdfNumber(paramFloat));
    switch (paramInt)
    {
    case 4:
    case 5:
    default:
      addFirst(PdfName.FITH);
      break;
    case 3:
      addFirst(PdfName.FITV);
      break;
    case 6:
      addFirst(PdfName.FITBH);
      break;
    case 7:
      addFirst(PdfName.FITBV);
    }
  }

  public PdfDestination(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    super(PdfName.XYZ);
    if (paramFloat1 < 0.0F)
      add(PdfNull.PDFNULL);
    else
      add(new PdfNumber(paramFloat1));
    if (paramFloat2 < 0.0F)
      add(PdfNull.PDFNULL);
    else
      add(new PdfNumber(paramFloat2));
    add(new PdfNumber(paramFloat3));
  }

  public PdfDestination(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    super(PdfName.FITR);
    add(new PdfNumber(paramFloat1));
    add(new PdfNumber(paramFloat2));
    add(new PdfNumber(paramFloat3));
    add(new PdfNumber(paramFloat4));
  }

  public boolean hasPage()
  {
    return this.status;
  }

  public boolean addPage(PdfIndirectReference paramPdfIndirectReference)
  {
    if (!this.status)
    {
      addFirst(paramPdfIndirectReference);
      this.status = true;
      return true;
    }
    return false;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfDestination
 * JD-Core Version:    0.6.0
 */