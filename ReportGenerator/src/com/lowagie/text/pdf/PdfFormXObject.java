package com.lowagie.text.pdf;

public class PdfFormXObject extends PdfStream
{
  public static final PdfNumber ZERO = new PdfNumber(0);
  public static final PdfNumber ONE = new PdfNumber(1);
  public static final PdfLiteral MATRIX = new PdfLiteral("[1 0 0 1 0 0]");

  PdfFormXObject(PdfTemplate paramPdfTemplate, int paramInt)
  {
    put(PdfName.TYPE, PdfName.XOBJECT);
    put(PdfName.SUBTYPE, PdfName.FORM);
    put(PdfName.RESOURCES, paramPdfTemplate.getResources());
    put(PdfName.BBOX, new PdfRectangle(paramPdfTemplate.getBoundingBox()));
    put(PdfName.FORMTYPE, ONE);
    if (paramPdfTemplate.getLayer() != null)
      put(PdfName.OC, paramPdfTemplate.getLayer().getRef());
    if (paramPdfTemplate.getGroup() != null)
      put(PdfName.GROUP, paramPdfTemplate.getGroup());
    PdfArray localPdfArray = paramPdfTemplate.getMatrix();
    if (localPdfArray == null)
      put(PdfName.MATRIX, MATRIX);
    else
      put(PdfName.MATRIX, localPdfArray);
    this.bytes = paramPdfTemplate.toPdf(null);
    put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
    flateCompress(paramInt);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfFormXObject
 * JD-Core Version:    0.6.0
 */