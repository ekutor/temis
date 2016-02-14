package com.lowagie.text.pdf;

public class StampContent extends PdfContentByte
{
  PdfStamperImp.PageStamp ps;
  PageResources pageResources;

  StampContent(PdfStamperImp paramPdfStamperImp, PdfStamperImp.PageStamp paramPageStamp)
  {
    super(paramPdfStamperImp);
    this.ps = paramPageStamp;
    this.pageResources = paramPageStamp.pageResources;
  }

  public void setAction(PdfAction paramPdfAction, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    ((PdfStamperImp)this.writer).addAnnotation(new PdfAnnotation(this.writer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramPdfAction), this.ps.pageN);
  }

  public PdfContentByte getDuplicate()
  {
    return new StampContent((PdfStamperImp)this.writer, this.ps);
  }

  PageResources getPageResources()
  {
    return this.pageResources;
  }

  void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    ((PdfStamperImp)this.writer).addAnnotation(paramPdfAnnotation, this.ps.pageN);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.StampContent
 * JD-Core Version:    0.6.0
 */