package com.lowagie.text.pdf;

class ColorDetails
{
  PdfIndirectReference indirectReference;
  PdfName colorName;
  PdfSpotColor spotcolor;

  ColorDetails(PdfName paramPdfName, PdfIndirectReference paramPdfIndirectReference, PdfSpotColor paramPdfSpotColor)
  {
    this.colorName = paramPdfName;
    this.indirectReference = paramPdfIndirectReference;
    this.spotcolor = paramPdfSpotColor;
  }

  PdfIndirectReference getIndirectReference()
  {
    return this.indirectReference;
  }

  PdfName getColorName()
  {
    return this.colorName;
  }

  PdfObject getSpotColor(PdfWriter paramPdfWriter)
  {
    return this.spotcolor.getSpotObject(paramPdfWriter);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ColorDetails
 * JD-Core Version:    0.6.0
 */