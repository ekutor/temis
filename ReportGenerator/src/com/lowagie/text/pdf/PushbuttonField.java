package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.io.IOException;

public class PushbuttonField extends BaseField
{
  public static final int LAYOUT_LABEL_ONLY = 1;
  public static final int LAYOUT_ICON_ONLY = 2;
  public static final int LAYOUT_ICON_TOP_LABEL_BOTTOM = 3;
  public static final int LAYOUT_LABEL_TOP_ICON_BOTTOM = 4;
  public static final int LAYOUT_ICON_LEFT_LABEL_RIGHT = 5;
  public static final int LAYOUT_LABEL_LEFT_ICON_RIGHT = 6;
  public static final int LAYOUT_LABEL_OVER_ICON = 7;
  public static final int SCALE_ICON_ALWAYS = 1;
  public static final int SCALE_ICON_NEVER = 2;
  public static final int SCALE_ICON_IS_TOO_BIG = 3;
  public static final int SCALE_ICON_IS_TOO_SMALL = 4;
  private int layout = 1;
  private Image image;
  private PdfTemplate template;
  private int scaleIcon = 1;
  private boolean proportionalIcon = true;
  private float iconVerticalAdjustment = 0.5F;
  private float iconHorizontalAdjustment = 0.5F;
  private boolean iconFitToBounds;
  private PdfTemplate tp;
  private PRIndirectReference iconReference;

  public PushbuttonField(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString)
  {
    super(paramPdfWriter, paramRectangle, paramString);
  }

  public int getLayout()
  {
    return this.layout;
  }

  public void setLayout(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 7))
      throw new IllegalArgumentException("Layout out of bounds.");
    this.layout = paramInt;
  }

  public Image getImage()
  {
    return this.image;
  }

  public void setImage(Image paramImage)
  {
    this.image = paramImage;
    this.template = null;
  }

  public PdfTemplate getTemplate()
  {
    return this.template;
  }

  public void setTemplate(PdfTemplate paramPdfTemplate)
  {
    this.template = paramPdfTemplate;
    this.image = null;
  }

  public int getScaleIcon()
  {
    return this.scaleIcon;
  }

  public void setScaleIcon(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 4))
      paramInt = 1;
    this.scaleIcon = paramInt;
  }

  public boolean isProportionalIcon()
  {
    return this.proportionalIcon;
  }

  public void setProportionalIcon(boolean paramBoolean)
  {
    this.proportionalIcon = paramBoolean;
  }

  public float getIconVerticalAdjustment()
  {
    return this.iconVerticalAdjustment;
  }

  public void setIconVerticalAdjustment(float paramFloat)
  {
    if (paramFloat < 0.0F)
      paramFloat = 0.0F;
    else if (paramFloat > 1.0F)
      paramFloat = 1.0F;
    this.iconVerticalAdjustment = paramFloat;
  }

  public float getIconHorizontalAdjustment()
  {
    return this.iconHorizontalAdjustment;
  }

  public void setIconHorizontalAdjustment(float paramFloat)
  {
    if (paramFloat < 0.0F)
      paramFloat = 0.0F;
    else if (paramFloat > 1.0F)
      paramFloat = 1.0F;
    this.iconHorizontalAdjustment = paramFloat;
  }

  private float calculateFontSize(float paramFloat1, float paramFloat2)
    throws IOException, DocumentException
  {
    BaseFont localBaseFont = getRealFont();
    float f1 = this.fontSize;
    if (f1 == 0.0F)
    {
      float f2 = localBaseFont.getWidthPoint(this.text, 1.0F);
      if (f2 == 0.0F)
        f1 = 12.0F;
      else
        f1 = paramFloat1 / f2;
      float f3 = paramFloat2 / (1.0F - localBaseFont.getFontDescriptor(3, 1.0F));
      f1 = Math.min(f1, f3);
      if (f1 < 4.0F)
        f1 = 4.0F;
    }
    return f1;
  }

  public PdfAppearance getAppearance()
    throws IOException, DocumentException
  {
    PdfAppearance localPdfAppearance = getBorderAppearance();
    Rectangle localRectangle1 = new Rectangle(localPdfAppearance.getBoundingBox());
    if (((this.text == null) || (this.text.length() == 0)) && ((this.layout == 1) || ((this.image == null) && (this.template == null) && (this.iconReference == null))))
      return localPdfAppearance;
    if ((this.layout == 2) && (this.image == null) && (this.template == null) && (this.iconReference == null))
      return localPdfAppearance;
    BaseFont localBaseFont = getRealFont();
    int i = (this.borderStyle == 2) || (this.borderStyle == 3) ? 1 : 0;
    float f1 = localRectangle1.getHeight() - this.borderWidth * 2.0F;
    float f2 = this.borderWidth;
    if (i != 0)
    {
      f1 -= this.borderWidth * 2.0F;
      f2 *= 2.0F;
    }
    float f3 = i != 0 ? 2.0F * this.borderWidth : this.borderWidth;
    f3 = Math.max(f3, 1.0F);
    float f4 = Math.min(f2, f3);
    this.tp = null;
    float f5 = (0.0F / 0.0F);
    float f6 = 0.0F;
    float f7 = this.fontSize;
    float f8 = localRectangle1.getWidth() - 2.0F * f4 - 2.0F;
    float f9 = localRectangle1.getHeight() - 2.0F * f4;
    float f10 = this.iconFitToBounds ? 0.0F : f4 + 1.0F;
    int j = this.layout;
    if ((this.image == null) && (this.template == null) && (this.iconReference == null))
      j = 1;
    Rectangle localRectangle2 = null;
    while (true)
    {
      float f11;
      switch (j)
      {
      case 1:
      case 7:
        if ((this.text != null) && (this.text.length() > 0) && (f8 > 0.0F) && (f9 > 0.0F))
        {
          f7 = calculateFontSize(f8, f9);
          f5 = (localRectangle1.getWidth() - localBaseFont.getWidthPoint(this.text, f7)) / 2.0F;
          f6 = (localRectangle1.getHeight() - localBaseFont.getFontDescriptor(1, f7)) / 2.0F;
        }
      case 2:
        if ((j == 7) || (j == 2))
          localRectangle2 = new Rectangle(localRectangle1.getLeft() + f10, localRectangle1.getBottom() + f10, localRectangle1.getRight() - f10, localRectangle1.getTop() - f10);
        break;
      case 3:
        if ((this.text == null) || (this.text.length() == 0) || (f8 <= 0.0F) || (f9 <= 0.0F))
        {
          j = 2;
          continue;
        }
        f11 = localRectangle1.getHeight() * 0.35F - f4;
        if (f11 > 0.0F)
          f7 = calculateFontSize(f8, f11);
        else
          f7 = 4.0F;
        f5 = (localRectangle1.getWidth() - localBaseFont.getWidthPoint(this.text, f7)) / 2.0F;
        f6 = f4 - localBaseFont.getFontDescriptor(3, f7);
        localRectangle2 = new Rectangle(localRectangle1.getLeft() + f10, f6 + f7, localRectangle1.getRight() - f10, localRectangle1.getTop() - f10);
        break;
      case 4:
        if ((this.text == null) || (this.text.length() == 0) || (f8 <= 0.0F) || (f9 <= 0.0F))
        {
          j = 2;
          continue;
        }
        f11 = localRectangle1.getHeight() * 0.35F - f4;
        if (f11 > 0.0F)
          f7 = calculateFontSize(f8, f11);
        else
          f7 = 4.0F;
        f5 = (localRectangle1.getWidth() - localBaseFont.getWidthPoint(this.text, f7)) / 2.0F;
        f6 = localRectangle1.getHeight() - f4 - f7;
        if (f6 < f4)
          f6 = f4;
        localRectangle2 = new Rectangle(localRectangle1.getLeft() + f10, localRectangle1.getBottom() + f10, localRectangle1.getRight() - f10, f6 + localBaseFont.getFontDescriptor(3, f7));
        break;
      case 6:
        if ((this.text == null) || (this.text.length() == 0) || (f8 <= 0.0F) || (f9 <= 0.0F))
        {
          j = 2;
          continue;
        }
        f12 = localRectangle1.getWidth() * 0.35F - f4;
        if (f12 > 0.0F)
          f7 = calculateFontSize(f8, f12);
        else
          f7 = 4.0F;
        if (localBaseFont.getWidthPoint(this.text, f7) >= f8)
        {
          j = 1;
          f7 = this.fontSize;
          continue;
        }
        f5 = f4 + 1.0F;
        f6 = (localRectangle1.getHeight() - localBaseFont.getFontDescriptor(1, f7)) / 2.0F;
        localRectangle2 = new Rectangle(f5 + localBaseFont.getWidthPoint(this.text, f7), localRectangle1.getBottom() + f10, localRectangle1.getRight() - f10, localRectangle1.getTop() - f10);
        break;
      case 5:
        if ((this.text == null) || (this.text.length() == 0) || (f8 <= 0.0F) || (f9 <= 0.0F))
        {
          j = 2;
          continue;
        }
        f12 = localRectangle1.getWidth() * 0.35F - f4;
        if (f12 > 0.0F)
          f7 = calculateFontSize(f8, f12);
        else
          f7 = 4.0F;
        if (localBaseFont.getWidthPoint(this.text, f7) >= f8)
        {
          j = 1;
          f7 = this.fontSize;
          continue;
        }
        f5 = localRectangle1.getWidth() - localBaseFont.getWidthPoint(this.text, f7) - f4 - 1.0F;
        f6 = (localRectangle1.getHeight() - localBaseFont.getFontDescriptor(1, f7)) / 2.0F;
        localRectangle2 = new Rectangle(localRectangle1.getLeft() + f10, localRectangle1.getBottom() + f10, f5 - 1.0F, localRectangle1.getTop() - f10);
      }
    }
    if (f6 < localRectangle1.getBottom() + f4)
      f6 = localRectangle1.getBottom() + f4;
    if ((localRectangle2 != null) && ((localRectangle2.getWidth() <= 0.0F) || (localRectangle2.getHeight() <= 0.0F)))
      localRectangle2 = null;
    int k = 0;
    float f12 = 0.0F;
    float f13 = 0.0F;
    PdfArray localPdfArray = null;
    if (localRectangle2 != null)
      if (this.image != null)
      {
        this.tp = new PdfTemplate(this.writer);
        this.tp.setBoundingBox(new Rectangle(this.image));
        this.writer.addDirectTemplateSimple(this.tp, PdfName.FRM);
        this.tp.addImage(this.image, this.image.getWidth(), 0.0F, 0.0F, this.image.getHeight(), 0.0F, 0.0F);
        k = 1;
        f12 = this.tp.getBoundingBox().getWidth();
        f13 = this.tp.getBoundingBox().getHeight();
      }
      else if (this.template != null)
      {
        this.tp = new PdfTemplate(this.writer);
        this.tp.setBoundingBox(new Rectangle(this.template.getWidth(), this.template.getHeight()));
        this.writer.addDirectTemplateSimple(this.tp, PdfName.FRM);
        this.tp.addTemplate(this.template, this.template.getBoundingBox().getLeft(), this.template.getBoundingBox().getBottom());
        k = 1;
        f12 = this.tp.getBoundingBox().getWidth();
        f13 = this.tp.getBoundingBox().getHeight();
      }
      else if (this.iconReference != null)
      {
        PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.iconReference);
        if (localPdfDictionary != null)
        {
          Rectangle localRectangle3 = PdfReader.getNormalizedRectangle(localPdfDictionary.getAsArray(PdfName.BBOX));
          localPdfArray = localPdfDictionary.getAsArray(PdfName.MATRIX);
          k = 1;
          f12 = localRectangle3.getWidth();
          f13 = localRectangle3.getHeight();
        }
      }
    if (k != 0)
    {
      float f14 = localRectangle2.getWidth() / f12;
      float f15 = localRectangle2.getHeight() / f13;
      if (this.proportionalIcon)
      {
        switch (this.scaleIcon)
        {
        case 3:
          f14 = Math.min(f14, f15);
          f14 = Math.min(f14, 1.0F);
          break;
        case 4:
          f14 = Math.min(f14, f15);
          f14 = Math.max(f14, 1.0F);
          break;
        case 2:
          f14 = 1.0F;
          break;
        default:
          f14 = Math.min(f14, f15);
        }
        f15 = f14;
      }
      else
      {
        switch (this.scaleIcon)
        {
        case 3:
          f14 = Math.min(f14, 1.0F);
          f15 = Math.min(f15, 1.0F);
          break;
        case 4:
          f14 = Math.max(f14, 1.0F);
          f15 = Math.max(f15, 1.0F);
          break;
        case 2:
          f14 = f15 = 1.0F;
          break;
        }
      }
      float f16 = localRectangle2.getLeft() + (localRectangle2.getWidth() - f12 * f14) * this.iconHorizontalAdjustment;
      float f17 = localRectangle2.getBottom() + (localRectangle2.getHeight() - f13 * f15) * this.iconVerticalAdjustment;
      localPdfAppearance.saveState();
      localPdfAppearance.rectangle(localRectangle2.getLeft(), localRectangle2.getBottom(), localRectangle2.getWidth(), localRectangle2.getHeight());
      localPdfAppearance.clip();
      localPdfAppearance.newPath();
      if (this.tp != null)
      {
        localPdfAppearance.addTemplate(this.tp, f14, 0.0F, 0.0F, f15, f16, f17);
      }
      else
      {
        float f18 = 0.0F;
        float f19 = 0.0F;
        if ((localPdfArray != null) && (localPdfArray.size() == 6))
        {
          PdfNumber localPdfNumber = localPdfArray.getAsNumber(4);
          if (localPdfNumber != null)
            f18 = localPdfNumber.floatValue();
          localPdfNumber = localPdfArray.getAsNumber(5);
          if (localPdfNumber != null)
            f19 = localPdfNumber.floatValue();
        }
        localPdfAppearance.addTemplateReference(this.iconReference, PdfName.FRM, f14, 0.0F, 0.0F, f15, f16 - f18 * f14, f17 - f19 * f15);
      }
      localPdfAppearance.restoreState();
    }
    if (!Float.isNaN(f5))
    {
      localPdfAppearance.saveState();
      localPdfAppearance.rectangle(f4, f4, localRectangle1.getWidth() - 2.0F * f4, localRectangle1.getHeight() - 2.0F * f4);
      localPdfAppearance.clip();
      localPdfAppearance.newPath();
      if (this.textColor == null)
        localPdfAppearance.resetGrayFill();
      else
        localPdfAppearance.setColorFill(this.textColor);
      localPdfAppearance.beginText();
      localPdfAppearance.setFontAndSize(localBaseFont, f7);
      localPdfAppearance.setTextMatrix(f5, f6);
      localPdfAppearance.showText(this.text);
      localPdfAppearance.endText();
      localPdfAppearance.restoreState();
    }
    return localPdfAppearance;
  }

  public PdfFormField getField()
    throws IOException, DocumentException
  {
    PdfFormField localPdfFormField = PdfFormField.createPushButton(this.writer);
    localPdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
    if (this.fieldName != null)
    {
      localPdfFormField.setFieldName(this.fieldName);
      if ((this.options & 0x1) != 0)
        localPdfFormField.setFieldFlags(1);
      if ((this.options & 0x2) != 0)
        localPdfFormField.setFieldFlags(2);
    }
    if (this.text != null)
      localPdfFormField.setMKNormalCaption(this.text);
    if (this.rotation != 0)
      localPdfFormField.setMKRotation(this.rotation);
    localPdfFormField.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0F)));
    PdfAppearance localPdfAppearance1 = getAppearance();
    localPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, localPdfAppearance1);
    PdfAppearance localPdfAppearance2 = (PdfAppearance)localPdfAppearance1.getDuplicate();
    localPdfAppearance2.setFontAndSize(getRealFont(), this.fontSize);
    if (this.textColor == null)
      localPdfAppearance2.setGrayFill(0.0F);
    else
      localPdfAppearance2.setColorFill(this.textColor);
    localPdfFormField.setDefaultAppearanceString(localPdfAppearance2);
    if (this.borderColor != null)
      localPdfFormField.setMKBorderColor(this.borderColor);
    if (this.backgroundColor != null)
      localPdfFormField.setMKBackgroundColor(this.backgroundColor);
    switch (this.visibility)
    {
    case 1:
      localPdfFormField.setFlags(6);
      break;
    case 2:
      break;
    case 3:
      localPdfFormField.setFlags(36);
      break;
    default:
      localPdfFormField.setFlags(4);
    }
    if (this.tp != null)
      localPdfFormField.setMKNormalIcon(this.tp);
    localPdfFormField.setMKTextPosition(this.layout - 1);
    PdfName localPdfName = PdfName.A;
    if (this.scaleIcon == 3)
      localPdfName = PdfName.B;
    else if (this.scaleIcon == 4)
      localPdfName = PdfName.S;
    else if (this.scaleIcon == 2)
      localPdfName = PdfName.N;
    localPdfFormField.setMKIconFit(localPdfName, this.proportionalIcon ? PdfName.P : PdfName.A, this.iconHorizontalAdjustment, this.iconVerticalAdjustment, this.iconFitToBounds);
    return localPdfFormField;
  }

  public boolean isIconFitToBounds()
  {
    return this.iconFitToBounds;
  }

  public void setIconFitToBounds(boolean paramBoolean)
  {
    this.iconFitToBounds = paramBoolean;
  }

  public PRIndirectReference getIconReference()
  {
    return this.iconReference;
  }

  public void setIconReference(PRIndirectReference paramPRIndirectReference)
  {
    this.iconReference = paramPRIndirectReference;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PushbuttonField
 * JD-Core Version:    0.6.0
 */