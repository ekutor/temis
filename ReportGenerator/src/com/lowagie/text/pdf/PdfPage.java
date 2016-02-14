package com.lowagie.text.pdf;

import java.util.HashMap;

public class PdfPage extends PdfDictionary
{
  private static final String[] boxStrings = { "crop", "trim", "art", "bleed" };
  private static final PdfName[] boxNames = { PdfName.CROPBOX, PdfName.TRIMBOX, PdfName.ARTBOX, PdfName.BLEEDBOX };
  public static final PdfNumber PORTRAIT = new PdfNumber(0);
  public static final PdfNumber LANDSCAPE = new PdfNumber(90);
  public static final PdfNumber INVERTEDPORTRAIT = new PdfNumber(180);
  public static final PdfNumber SEASCAPE = new PdfNumber(270);
  PdfRectangle mediaBox;

  PdfPage(PdfRectangle paramPdfRectangle, HashMap paramHashMap, PdfDictionary paramPdfDictionary, int paramInt)
  {
    super(PAGE);
    this.mediaBox = paramPdfRectangle;
    put(PdfName.MEDIABOX, paramPdfRectangle);
    put(PdfName.RESOURCES, paramPdfDictionary);
    if (paramInt != 0)
      put(PdfName.ROTATE, new PdfNumber(paramInt));
    for (int i = 0; i < boxStrings.length; i++)
    {
      PdfObject localPdfObject = (PdfObject)paramHashMap.get(boxStrings[i]);
      if (localPdfObject == null)
        continue;
      put(boxNames[i], localPdfObject);
    }
  }

  PdfPage(PdfRectangle paramPdfRectangle, HashMap paramHashMap, PdfDictionary paramPdfDictionary)
  {
    this(paramPdfRectangle, paramHashMap, paramPdfDictionary, 0);
  }

  public boolean isParent()
  {
    return false;
  }

  void add(PdfIndirectReference paramPdfIndirectReference)
  {
    put(PdfName.CONTENTS, paramPdfIndirectReference);
  }

  PdfRectangle rotateMediaBox()
  {
    this.mediaBox = this.mediaBox.rotate();
    put(PdfName.MEDIABOX, this.mediaBox);
    return this.mediaBox;
  }

  PdfRectangle getMediaBox()
  {
    return this.mediaBox;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPage
 * JD-Core Version:    0.6.0
 */