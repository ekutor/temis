package com.lowagie.text.pdf;

import java.util.Collection;
import java.util.HashSet;

public class PdfLayerMembership extends PdfDictionary
  implements PdfOCG
{
  public static final PdfName ALLON = new PdfName("AllOn");
  public static final PdfName ANYON = new PdfName("AnyOn");
  public static final PdfName ANYOFF = new PdfName("AnyOff");
  public static final PdfName ALLOFF = new PdfName("AllOff");
  PdfIndirectReference ref;
  PdfArray members = new PdfArray();
  HashSet layers = new HashSet();

  public PdfLayerMembership(PdfWriter paramPdfWriter)
  {
    super(PdfName.OCMD);
    put(PdfName.OCGS, this.members);
    this.ref = paramPdfWriter.getPdfIndirectReference();
  }

  public PdfIndirectReference getRef()
  {
    return this.ref;
  }

  public void addMember(PdfLayer paramPdfLayer)
  {
    if (!this.layers.contains(paramPdfLayer))
    {
      this.members.add(paramPdfLayer.getRef());
      this.layers.add(paramPdfLayer);
    }
  }

  public Collection getLayers()
  {
    return this.layers;
  }

  public void setVisibilityPolicy(PdfName paramPdfName)
  {
    put(PdfName.P, paramPdfName);
  }

  public PdfObject getPdfObject()
  {
    return this;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfLayerMembership
 * JD-Core Version:    0.6.0
 */