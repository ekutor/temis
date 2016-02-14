package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.awt.color.ICC_Profile;

public class PdfICCBased extends PdfStream
{
  public PdfICCBased(ICC_Profile paramICC_Profile)
  {
    this(paramICC_Profile, -1);
  }

  public PdfICCBased(ICC_Profile paramICC_Profile, int paramInt)
  {
    try
    {
      int i = paramICC_Profile.getNumComponents();
      switch (i)
      {
      case 1:
        put(PdfName.ALTERNATE, PdfName.DEVICEGRAY);
        break;
      case 3:
        put(PdfName.ALTERNATE, PdfName.DEVICERGB);
        break;
      case 4:
        put(PdfName.ALTERNATE, PdfName.DEVICECMYK);
        break;
      case 2:
      default:
        throw new PdfException(i + " component(s) is not supported in PDF1.4");
      }
      put(PdfName.N, new PdfNumber(i));
      this.bytes = paramICC_Profile.getData();
      flateCompress(paramInt);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfICCBased
 * JD-Core Version:    0.6.0
 */