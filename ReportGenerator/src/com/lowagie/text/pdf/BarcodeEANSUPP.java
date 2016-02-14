package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.awt.Image;

public class BarcodeEANSUPP extends Barcode
{
  protected Barcode ean;
  protected Barcode supp;

  public BarcodeEANSUPP(Barcode paramBarcode1, Barcode paramBarcode2)
  {
    this.n = 8.0F;
    this.ean = paramBarcode1;
    this.supp = paramBarcode2;
  }

  public Rectangle getBarcodeSize()
  {
    Rectangle localRectangle = this.ean.getBarcodeSize();
    localRectangle.setRight(localRectangle.getWidth() + this.supp.getBarcodeSize().getWidth() + this.n);
    return localRectangle;
  }

  public Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    if (this.supp.getFont() != null)
      this.supp.setBarHeight(this.ean.getBarHeight() + this.supp.getBaseline() - this.supp.getFont().getFontDescriptor(2, this.supp.getSize()));
    else
      this.supp.setBarHeight(this.ean.getBarHeight());
    Rectangle localRectangle = this.ean.getBarcodeSize();
    paramPdfContentByte.saveState();
    this.ean.placeBarcode(paramPdfContentByte, paramColor1, paramColor2);
    paramPdfContentByte.restoreState();
    paramPdfContentByte.saveState();
    paramPdfContentByte.concatCTM(1.0F, 0.0F, 0.0F, 1.0F, localRectangle.getWidth() + this.n, localRectangle.getHeight() - this.ean.getBarHeight());
    this.supp.placeBarcode(paramPdfContentByte, paramColor1, paramColor2);
    paramPdfContentByte.restoreState();
    return getBarcodeSize();
  }

  public Image createAwtImage(Color paramColor1, Color paramColor2)
  {
    throw new UnsupportedOperationException("The two barcodes must be composed externally.");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BarcodeEANSUPP
 * JD-Core Version:    0.6.0
 */