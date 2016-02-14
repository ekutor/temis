package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import java.util.HashMap;

public class PdfAppearance extends PdfTemplate
{
  public static final HashMap stdFieldFontNames = new HashMap();

  PdfAppearance()
  {
    this.separator = 32;
  }

  PdfAppearance(PdfIndirectReference paramPdfIndirectReference)
  {
    this.thisReference = paramPdfIndirectReference;
  }

  PdfAppearance(PdfWriter paramPdfWriter)
  {
    super(paramPdfWriter);
    this.separator = 32;
  }

  public static PdfAppearance createAppearance(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2)
  {
    return createAppearance(paramPdfWriter, paramFloat1, paramFloat2, null);
  }

  static PdfAppearance createAppearance(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, PdfName paramPdfName)
  {
    PdfAppearance localPdfAppearance = new PdfAppearance(paramPdfWriter);
    localPdfAppearance.setWidth(paramFloat1);
    localPdfAppearance.setHeight(paramFloat2);
    paramPdfWriter.addDirectTemplateSimple(localPdfAppearance, paramPdfName);
    return localPdfAppearance;
  }

  public void setFontAndSize(BaseFont paramBaseFont, float paramFloat)
  {
    checkWriter();
    this.state.size = paramFloat;
    if (paramBaseFont.getFontType() == 4)
      this.state.fontDetails = new FontDetails(null, ((DocumentFont)paramBaseFont).getIndirectReference(), paramBaseFont);
    else
      this.state.fontDetails = this.writer.addSimple(paramBaseFont);
    PdfName localPdfName = (PdfName)stdFieldFontNames.get(paramBaseFont.getPostscriptFontName());
    if (localPdfName == null)
      if ((paramBaseFont.isSubset()) && (paramBaseFont.getFontType() == 3))
      {
        localPdfName = this.state.fontDetails.getFontName();
      }
      else
      {
        localPdfName = new PdfName(paramBaseFont.getPostscriptFontName());
        this.state.fontDetails.setSubset(false);
      }
    PageResources localPageResources = getPageResources();
    localPageResources.addFont(localPdfName, this.state.fontDetails.getIndirectReference());
    this.content.append(localPdfName.getBytes()).append(' ').append(paramFloat).append(" Tf").append_i(this.separator);
  }

  public PdfContentByte getDuplicate()
  {
    PdfAppearance localPdfAppearance = new PdfAppearance();
    localPdfAppearance.writer = this.writer;
    localPdfAppearance.pdf = this.pdf;
    localPdfAppearance.thisReference = this.thisReference;
    localPdfAppearance.pageResources = this.pageResources;
    localPdfAppearance.bBox = new Rectangle(this.bBox);
    localPdfAppearance.group = this.group;
    localPdfAppearance.layer = this.layer;
    if (this.matrix != null)
      localPdfAppearance.matrix = new PdfArray(this.matrix);
    localPdfAppearance.separator = this.separator;
    return localPdfAppearance;
  }

  static
  {
    stdFieldFontNames.put("Courier-BoldOblique", new PdfName("CoBO"));
    stdFieldFontNames.put("Courier-Bold", new PdfName("CoBo"));
    stdFieldFontNames.put("Courier-Oblique", new PdfName("CoOb"));
    stdFieldFontNames.put("Courier", new PdfName("Cour"));
    stdFieldFontNames.put("Helvetica-BoldOblique", new PdfName("HeBO"));
    stdFieldFontNames.put("Helvetica-Bold", new PdfName("HeBo"));
    stdFieldFontNames.put("Helvetica-Oblique", new PdfName("HeOb"));
    stdFieldFontNames.put("Helvetica", PdfName.HELV);
    stdFieldFontNames.put("Symbol", new PdfName("Symb"));
    stdFieldFontNames.put("Times-BoldItalic", new PdfName("TiBI"));
    stdFieldFontNames.put("Times-Bold", new PdfName("TiBo"));
    stdFieldFontNames.put("Times-Italic", new PdfName("TiIt"));
    stdFieldFontNames.put("Times-Roman", new PdfName("TiRo"));
    stdFieldFontNames.put("ZapfDingbats", PdfName.ZADB);
    stdFieldFontNames.put("HYSMyeongJo-Medium", new PdfName("HySm"));
    stdFieldFontNames.put("HYGoThic-Medium", new PdfName("HyGo"));
    stdFieldFontNames.put("HeiseiKakuGo-W5", new PdfName("KaGo"));
    stdFieldFontNames.put("HeiseiMin-W3", new PdfName("KaMi"));
    stdFieldFontNames.put("MHei-Medium", new PdfName("MHei"));
    stdFieldFontNames.put("MSung-Light", new PdfName("MSun"));
    stdFieldFontNames.put("STSong-Light", new PdfName("STSo"));
    stdFieldFontNames.put("MSungStd-Light", new PdfName("MSun"));
    stdFieldFontNames.put("STSongStd-Light", new PdfName("STSo"));
    stdFieldFontNames.put("HYSMyeongJoStd-Medium", new PdfName("HySm"));
    stdFieldFontNames.put("KozMinPro-Regular", new PdfName("KaMi"));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfAppearance
 * JD-Core Version:    0.6.0
 */