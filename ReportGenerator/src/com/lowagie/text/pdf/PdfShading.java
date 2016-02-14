package com.lowagie.text.pdf;

import java.awt.Color;
import java.io.IOException;

public class PdfShading
{
  protected PdfDictionary shading;
  protected PdfWriter writer;
  protected int shadingType;
  protected ColorDetails colorDetails;
  protected PdfName shadingName;
  protected PdfIndirectReference shadingReference;
  private Color cspace;
  protected float[] bBox;
  protected boolean antiAlias = false;

  protected PdfShading(PdfWriter paramPdfWriter)
  {
    this.writer = paramPdfWriter;
  }

  protected void setColorSpace(Color paramColor)
  {
    this.cspace = paramColor;
    int i = ExtendedColor.getType(paramColor);
    Object localObject = null;
    switch (i)
    {
    case 1:
      localObject = PdfName.DEVICEGRAY;
      break;
    case 2:
      localObject = PdfName.DEVICECMYK;
      break;
    case 3:
      SpotColor localSpotColor = (SpotColor)paramColor;
      this.colorDetails = this.writer.addSimple(localSpotColor.getPdfSpotColor());
      localObject = this.colorDetails.getIndirectReference();
      break;
    case 4:
    case 5:
      throwColorSpaceError();
    default:
      localObject = PdfName.DEVICERGB;
    }
    this.shading.put(PdfName.COLORSPACE, (PdfObject)localObject);
  }

  public Color getColorSpace()
  {
    return this.cspace;
  }

  public static void throwColorSpaceError()
  {
    throw new IllegalArgumentException("A tiling or shading pattern cannot be used as a color space in a shading pattern");
  }

  public static void checkCompatibleColors(Color paramColor1, Color paramColor2)
  {
    int i = ExtendedColor.getType(paramColor1);
    int j = ExtendedColor.getType(paramColor2);
    if (i != j)
      throw new IllegalArgumentException("Both colors must be of the same type.");
    if ((i == 3) && (((SpotColor)paramColor1).getPdfSpotColor() != ((SpotColor)paramColor2).getPdfSpotColor()))
      throw new IllegalArgumentException("The spot color must be the same, only the tint can vary.");
    if ((i == 4) || (i == 5))
      throwColorSpaceError();
  }

  public static float[] getColorArray(Color paramColor)
  {
    int i = ExtendedColor.getType(paramColor);
    switch (i)
    {
    case 1:
      return new float[] { ((GrayColor)paramColor).getGray() };
    case 2:
      CMYKColor localCMYKColor = (CMYKColor)paramColor;
      return new float[] { localCMYKColor.getCyan(), localCMYKColor.getMagenta(), localCMYKColor.getYellow(), localCMYKColor.getBlack() };
    case 3:
      return new float[] { ((SpotColor)paramColor).getTint() };
    case 0:
      return new float[] { paramColor.getRed() / 255.0F, paramColor.getGreen() / 255.0F, paramColor.getBlue() / 255.0F };
    }
    throwColorSpaceError();
    return null;
  }

  public static PdfShading type1(PdfWriter paramPdfWriter, Color paramColor, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, PdfFunction paramPdfFunction)
  {
    PdfShading localPdfShading = new PdfShading(paramPdfWriter);
    localPdfShading.shading = new PdfDictionary();
    localPdfShading.shadingType = 1;
    localPdfShading.shading.put(PdfName.SHADINGTYPE, new PdfNumber(localPdfShading.shadingType));
    localPdfShading.setColorSpace(paramColor);
    if (paramArrayOfFloat1 != null)
      localPdfShading.shading.put(PdfName.DOMAIN, new PdfArray(paramArrayOfFloat1));
    if (paramArrayOfFloat2 != null)
      localPdfShading.shading.put(PdfName.MATRIX, new PdfArray(paramArrayOfFloat2));
    localPdfShading.shading.put(PdfName.FUNCTION, paramPdfFunction.getReference());
    return localPdfShading;
  }

  public static PdfShading type2(PdfWriter paramPdfWriter, Color paramColor, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, PdfFunction paramPdfFunction, boolean[] paramArrayOfBoolean)
  {
    PdfShading localPdfShading = new PdfShading(paramPdfWriter);
    localPdfShading.shading = new PdfDictionary();
    localPdfShading.shadingType = 2;
    localPdfShading.shading.put(PdfName.SHADINGTYPE, new PdfNumber(localPdfShading.shadingType));
    localPdfShading.setColorSpace(paramColor);
    localPdfShading.shading.put(PdfName.COORDS, new PdfArray(paramArrayOfFloat1));
    if (paramArrayOfFloat2 != null)
      localPdfShading.shading.put(PdfName.DOMAIN, new PdfArray(paramArrayOfFloat2));
    localPdfShading.shading.put(PdfName.FUNCTION, paramPdfFunction.getReference());
    if ((paramArrayOfBoolean != null) && ((paramArrayOfBoolean[0] != 0) || (paramArrayOfBoolean[1] != 0)))
    {
      PdfArray localPdfArray = new PdfArray(paramArrayOfBoolean[0] != 0 ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
      localPdfArray.add(paramArrayOfBoolean[1] != 0 ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
      localPdfShading.shading.put(PdfName.EXTEND, localPdfArray);
    }
    return localPdfShading;
  }

  public static PdfShading type3(PdfWriter paramPdfWriter, Color paramColor, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, PdfFunction paramPdfFunction, boolean[] paramArrayOfBoolean)
  {
    PdfShading localPdfShading = type2(paramPdfWriter, paramColor, paramArrayOfFloat1, paramArrayOfFloat2, paramPdfFunction, paramArrayOfBoolean);
    localPdfShading.shadingType = 3;
    localPdfShading.shading.put(PdfName.SHADINGTYPE, new PdfNumber(localPdfShading.shadingType));
    return localPdfShading;
  }

  public static PdfShading simpleAxial(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Color paramColor1, Color paramColor2, boolean paramBoolean1, boolean paramBoolean2)
  {
    checkCompatibleColors(paramColor1, paramColor2);
    PdfFunction localPdfFunction = PdfFunction.type2(paramPdfWriter, new float[] { 0.0F, 1.0F }, null, getColorArray(paramColor1), getColorArray(paramColor2), 1.0F);
    return type2(paramPdfWriter, paramColor1, new float[] { paramFloat1, paramFloat2, paramFloat3, paramFloat4 }, null, localPdfFunction, new boolean[] { paramBoolean1, paramBoolean2 });
  }

  public static PdfShading simpleAxial(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Color paramColor1, Color paramColor2)
  {
    return simpleAxial(paramPdfWriter, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramColor1, paramColor2, true, true);
  }

  public static PdfShading simpleRadial(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, Color paramColor1, Color paramColor2, boolean paramBoolean1, boolean paramBoolean2)
  {
    checkCompatibleColors(paramColor1, paramColor2);
    PdfFunction localPdfFunction = PdfFunction.type2(paramPdfWriter, new float[] { 0.0F, 1.0F }, null, getColorArray(paramColor1), getColorArray(paramColor2), 1.0F);
    return type3(paramPdfWriter, paramColor1, new float[] { paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6 }, null, localPdfFunction, new boolean[] { paramBoolean1, paramBoolean2 });
  }

  public static PdfShading simpleRadial(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, Color paramColor1, Color paramColor2)
  {
    return simpleRadial(paramPdfWriter, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramColor1, paramColor2, true, true);
  }

  PdfName getShadingName()
  {
    return this.shadingName;
  }

  PdfIndirectReference getShadingReference()
  {
    if (this.shadingReference == null)
      this.shadingReference = this.writer.getPdfIndirectReference();
    return this.shadingReference;
  }

  void setName(int paramInt)
  {
    this.shadingName = new PdfName("Sh" + paramInt);
  }

  void addToBody()
    throws IOException
  {
    if (this.bBox != null)
      this.shading.put(PdfName.BBOX, new PdfArray(this.bBox));
    if (this.antiAlias)
      this.shading.put(PdfName.ANTIALIAS, PdfBoolean.PDFTRUE);
    this.writer.addToBody(this.shading, getShadingReference());
  }

  PdfWriter getWriter()
  {
    return this.writer;
  }

  ColorDetails getColorDetails()
  {
    return this.colorDetails;
  }

  public float[] getBBox()
  {
    return this.bBox;
  }

  public void setBBox(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length != 4)
      throw new IllegalArgumentException("BBox must be a 4 element array.");
    this.bBox = paramArrayOfFloat;
  }

  public boolean isAntiAlias()
  {
    return this.antiAlias;
  }

  public void setAntiAlias(boolean paramBoolean)
  {
    this.antiAlias = paramBoolean;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfShading
 * JD-Core Version:    0.6.0
 */