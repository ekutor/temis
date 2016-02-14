package com.lowagie.text.pdf;

public class PdfStructureElement extends PdfDictionary
{
  private PdfStructureElement parent;
  private PdfStructureTreeRoot top;
  private PdfIndirectReference reference;

  public PdfStructureElement(PdfStructureElement paramPdfStructureElement, PdfName paramPdfName)
  {
    this.top = paramPdfStructureElement.top;
    init(paramPdfStructureElement, paramPdfName);
    this.parent = paramPdfStructureElement;
    put(PdfName.P, paramPdfStructureElement.reference);
  }

  public PdfStructureElement(PdfStructureTreeRoot paramPdfStructureTreeRoot, PdfName paramPdfName)
  {
    this.top = paramPdfStructureTreeRoot;
    init(paramPdfStructureTreeRoot, paramPdfName);
    put(PdfName.P, paramPdfStructureTreeRoot.getReference());
  }

  private void init(PdfDictionary paramPdfDictionary, PdfName paramPdfName)
  {
    PdfObject localPdfObject = paramPdfDictionary.get(PdfName.K);
    PdfArray localPdfArray = null;
    if ((localPdfObject != null) && (!localPdfObject.isArray()))
      throw new IllegalArgumentException("The parent has already another function.");
    if (localPdfObject == null)
    {
      localPdfArray = new PdfArray();
      paramPdfDictionary.put(PdfName.K, localPdfArray);
    }
    else
    {
      localPdfArray = (PdfArray)localPdfObject;
    }
    localPdfArray.add(this);
    put(PdfName.S, paramPdfName);
    this.reference = this.top.getWriter().getPdfIndirectReference();
  }

  public PdfDictionary getParent()
  {
    return this.parent;
  }

  void setPageMark(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 0)
      put(PdfName.K, new PdfNumber(paramInt2));
    this.top.setPageMark(paramInt1, this.reference);
  }

  public PdfIndirectReference getReference()
  {
    return this.reference;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfStructureElement
 * JD-Core Version:    0.6.0
 */