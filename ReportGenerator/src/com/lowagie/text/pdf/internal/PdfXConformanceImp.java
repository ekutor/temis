package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PatternColor;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.pdf.ShadingColor;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.interfaces.PdfXConformance;
import java.awt.Color;

public class PdfXConformanceImp
  implements PdfXConformance
{
  public static final int PDFXKEY_COLOR = 1;
  public static final int PDFXKEY_CMYK = 2;
  public static final int PDFXKEY_RGB = 3;
  public static final int PDFXKEY_FONT = 4;
  public static final int PDFXKEY_IMAGE = 5;
  public static final int PDFXKEY_GSTATE = 6;
  public static final int PDFXKEY_LAYER = 7;
  protected int pdfxConformance = 0;

  public void setPDFXConformance(int paramInt)
  {
    this.pdfxConformance = paramInt;
  }

  public int getPDFXConformance()
  {
    return this.pdfxConformance;
  }

  public boolean isPdfX()
  {
    return this.pdfxConformance != 0;
  }

  public boolean isPdfX1A2001()
  {
    return this.pdfxConformance == 1;
  }

  public boolean isPdfX32002()
  {
    return this.pdfxConformance == 2;
  }

  public boolean isPdfA1()
  {
    return (this.pdfxConformance == 3) || (this.pdfxConformance == 4);
  }

  public boolean isPdfA1A()
  {
    return this.pdfxConformance == 3;
  }

  public void completeInfoDictionary(PdfDictionary paramPdfDictionary)
  {
    if ((isPdfX()) && (!isPdfA1()))
    {
      if (paramPdfDictionary.get(PdfName.GTS_PDFXVERSION) == null)
        if (isPdfX1A2001())
        {
          paramPdfDictionary.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-1:2001"));
          paramPdfDictionary.put(new PdfName("GTS_PDFXConformance"), new PdfString("PDF/X-1a:2001"));
        }
        else if (isPdfX32002())
        {
          paramPdfDictionary.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-3:2002"));
        }
      if (paramPdfDictionary.get(PdfName.TITLE) == null)
        paramPdfDictionary.put(PdfName.TITLE, new PdfString("Pdf document"));
      if (paramPdfDictionary.get(PdfName.CREATOR) == null)
        paramPdfDictionary.put(PdfName.CREATOR, new PdfString("Unknown"));
      if (paramPdfDictionary.get(PdfName.TRAPPED) == null)
        paramPdfDictionary.put(PdfName.TRAPPED, new PdfName("False"));
    }
  }

  public void completeExtraCatalog(PdfDictionary paramPdfDictionary)
  {
    if ((isPdfX()) && (!isPdfA1()) && (paramPdfDictionary.get(PdfName.OUTPUTINTENTS) == null))
    {
      PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.OUTPUTINTENT);
      localPdfDictionary.put(PdfName.OUTPUTCONDITION, new PdfString("SWOP CGATS TR 001-1995"));
      localPdfDictionary.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("CGATS TR 001"));
      localPdfDictionary.put(PdfName.REGISTRYNAME, new PdfString("http://www.color.org"));
      localPdfDictionary.put(PdfName.INFO, new PdfString(""));
      localPdfDictionary.put(PdfName.S, PdfName.GTS_PDFX);
      paramPdfDictionary.put(PdfName.OUTPUTINTENTS, new PdfArray(localPdfDictionary));
    }
  }

  public static void checkPDFXConformance(PdfWriter paramPdfWriter, int paramInt, Object paramObject)
  {
    if ((paramPdfWriter == null) || (!paramPdfWriter.isPdfX()))
      return;
    int i = paramPdfWriter.getPDFXConformance();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    switch (paramInt)
    {
    case 1:
      switch (i)
      {
      case 1:
        if ((paramObject instanceof ExtendedColor))
        {
          localObject1 = (ExtendedColor)paramObject;
          switch (((ExtendedColor)localObject1).getType())
          {
          case 1:
          case 2:
            return;
          case 0:
            throw new PdfXConformanceException("Colorspace RGB is not allowed.");
          case 3:
            localObject2 = (SpotColor)localObject1;
            checkPDFXConformance(paramPdfWriter, 1, ((SpotColor)localObject2).getPdfSpotColor().getAlternativeCS());
            break;
          case 5:
            localObject3 = (ShadingColor)localObject1;
            checkPDFXConformance(paramPdfWriter, 1, ((ShadingColor)localObject3).getPdfShadingPattern().getShading().getColorSpace());
            break;
          case 4:
            PatternColor localPatternColor = (PatternColor)localObject1;
            checkPDFXConformance(paramPdfWriter, 1, localPatternColor.getPainter().getDefaultColor());
          }
        }
        else if ((paramObject instanceof Color))
        {
          throw new PdfXConformanceException("Colorspace RGB is not allowed.");
        }
      }
      break;
    case 2:
      break;
    case 3:
      if (i != 1)
        break;
      throw new PdfXConformanceException("Colorspace RGB is not allowed.");
    case 4:
      if (((BaseFont)paramObject).isEmbedded())
        break;
      throw new PdfXConformanceException("All the fonts must be embedded. This one isn't: " + ((BaseFont)paramObject).getPostscriptFontName());
    case 5:
      localObject1 = (PdfImage)paramObject;
      if (((PdfImage)localObject1).get(PdfName.SMASK) != null)
        throw new PdfXConformanceException("The /SMask key is not allowed in images.");
      switch (i)
      {
      case 1:
        localObject2 = ((PdfImage)localObject1).get(PdfName.COLORSPACE);
        if (localObject2 == null)
          return;
        if (((PdfObject)localObject2).isName())
        {
          if (PdfName.DEVICERGB.equals(localObject2))
            throw new PdfXConformanceException("Colorspace RGB is not allowed.");
        }
        else if ((((PdfObject)localObject2).isArray()) && (PdfName.CALRGB.equals(((PdfArray)localObject2).getPdfObject(0))))
          throw new PdfXConformanceException("Colorspace CalRGB is not allowed.");
      }
      break;
    case 6:
      localObject2 = (PdfDictionary)paramObject;
      localObject3 = ((PdfDictionary)localObject2).get(PdfName.BM);
      if ((localObject3 != null) && (!PdfGState.BM_NORMAL.equals(localObject3)) && (!PdfGState.BM_COMPATIBLE.equals(localObject3)))
        throw new PdfXConformanceException("Blend mode " + ((PdfObject)localObject3).toString() + " not allowed.");
      localObject3 = ((PdfDictionary)localObject2).get(PdfName.CA);
      double d = 0.0D;
      if ((localObject3 != null) && ((d = ((PdfNumber)localObject3).doubleValue()) != 1.0D))
        throw new PdfXConformanceException("Transparency is not allowed: /CA = " + d);
      localObject3 = ((PdfDictionary)localObject2).get(PdfName.ca);
      d = 0.0D;
      if ((localObject3 == null) || ((d = ((PdfNumber)localObject3).doubleValue()) == 1.0D))
        break;
      throw new PdfXConformanceException("Transparency is not allowed: /ca = " + d);
    case 7:
      throw new PdfXConformanceException("Layers are not allowed.");
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.internal.PdfXConformanceImp
 * JD-Core Version:    0.6.0
 */