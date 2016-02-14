package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Color;

public abstract class Barcode
{
  public static final int EAN13 = 1;
  public static final int EAN8 = 2;
  public static final int UPCA = 3;
  public static final int UPCE = 4;
  public static final int SUPP2 = 5;
  public static final int SUPP5 = 6;
  public static final int POSTNET = 7;
  public static final int PLANET = 8;
  public static final int CODE128 = 9;
  public static final int CODE128_UCC = 10;
  public static final int CODE128_RAW = 11;
  public static final int CODABAR = 12;
  protected float x;
  protected float n;
  protected BaseFont font;
  protected float size;
  protected float baseline;
  protected float barHeight;
  protected int textAlignment;
  protected boolean generateChecksum;
  protected boolean checksumText;
  protected boolean startStopText;
  protected boolean extended;
  protected String code = "";
  protected boolean guardBars;
  protected int codeType;
  protected float inkSpreading = 0.0F;
  protected String altText;

  public float getX()
  {
    return this.x;
  }

  public void setX(float paramFloat)
  {
    this.x = paramFloat;
  }

  public float getN()
  {
    return this.n;
  }

  public void setN(float paramFloat)
  {
    this.n = paramFloat;
  }

  public BaseFont getFont()
  {
    return this.font;
  }

  public void setFont(BaseFont paramBaseFont)
  {
    this.font = paramBaseFont;
  }

  public float getSize()
  {
    return this.size;
  }

  public void setSize(float paramFloat)
  {
    this.size = paramFloat;
  }

  public float getBaseline()
  {
    return this.baseline;
  }

  public void setBaseline(float paramFloat)
  {
    this.baseline = paramFloat;
  }

  public float getBarHeight()
  {
    return this.barHeight;
  }

  public void setBarHeight(float paramFloat)
  {
    this.barHeight = paramFloat;
  }

  public int getTextAlignment()
  {
    return this.textAlignment;
  }

  public void setTextAlignment(int paramInt)
  {
    this.textAlignment = paramInt;
  }

  public boolean isGenerateChecksum()
  {
    return this.generateChecksum;
  }

  public void setGenerateChecksum(boolean paramBoolean)
  {
    this.generateChecksum = paramBoolean;
  }

  public boolean isChecksumText()
  {
    return this.checksumText;
  }

  public void setChecksumText(boolean paramBoolean)
  {
    this.checksumText = paramBoolean;
  }

  public boolean isStartStopText()
  {
    return this.startStopText;
  }

  public void setStartStopText(boolean paramBoolean)
  {
    this.startStopText = paramBoolean;
  }

  public boolean isExtended()
  {
    return this.extended;
  }

  public void setExtended(boolean paramBoolean)
  {
    this.extended = paramBoolean;
  }

  public String getCode()
  {
    return this.code;
  }

  public void setCode(String paramString)
  {
    this.code = paramString;
  }

  public boolean isGuardBars()
  {
    return this.guardBars;
  }

  public void setGuardBars(boolean paramBoolean)
  {
    this.guardBars = paramBoolean;
  }

  public int getCodeType()
  {
    return this.codeType;
  }

  public void setCodeType(int paramInt)
  {
    this.codeType = paramInt;
  }

  public abstract Rectangle getBarcodeSize();

  public abstract Rectangle placeBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2);

  public PdfTemplate createTemplateWithBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    PdfTemplate localPdfTemplate = paramPdfContentByte.createTemplate(0.0F, 0.0F);
    Rectangle localRectangle = placeBarcode(localPdfTemplate, paramColor1, paramColor2);
    localPdfTemplate.setBoundingBox(localRectangle);
    return localPdfTemplate;
  }

  public com.lowagie.text.Image createImageWithBarcode(PdfContentByte paramPdfContentByte, Color paramColor1, Color paramColor2)
  {
    try
    {
      return com.lowagie.text.Image.getInstance(createTemplateWithBarcode(paramPdfContentByte, paramColor1, paramColor2));
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public abstract java.awt.Image createAwtImage(Color paramColor1, Color paramColor2);

  public float getInkSpreading()
  {
    return this.inkSpreading;
  }

  public void setInkSpreading(float paramFloat)
  {
    this.inkSpreading = paramFloat;
  }

  public String getAltText()
  {
    return this.altText;
  }

  public void setAltText(String paramString)
  {
    this.altText = paramString;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.Barcode
 * JD-Core Version:    0.6.0
 */