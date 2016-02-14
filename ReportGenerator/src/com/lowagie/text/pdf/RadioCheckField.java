package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.io.IOException;

public class RadioCheckField extends BaseField
{
  public static final int TYPE_CHECK = 1;
  public static final int TYPE_CIRCLE = 2;
  public static final int TYPE_CROSS = 3;
  public static final int TYPE_DIAMOND = 4;
  public static final int TYPE_SQUARE = 5;
  public static final int TYPE_STAR = 6;
  private static String[] typeChars = { "4", "l", "8", "u", "n", "H" };
  private int checkType;
  private String onValue;
  private boolean checked;

  public RadioCheckField(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString1, String paramString2)
  {
    super(paramPdfWriter, paramRectangle, paramString1);
    setOnValue(paramString2);
    setCheckType(2);
  }

  public int getCheckType()
  {
    return this.checkType;
  }

  public void setCheckType(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 6))
      paramInt = 2;
    this.checkType = paramInt;
    setText(typeChars[(paramInt - 1)]);
    try
    {
      setFont(BaseFont.createFont("ZapfDingbats", "Cp1252", false));
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public String getOnValue()
  {
    return this.onValue;
  }

  public void setOnValue(String paramString)
  {
    this.onValue = paramString;
  }

  public boolean isChecked()
  {
    return this.checked;
  }

  public void setChecked(boolean paramBoolean)
  {
    this.checked = paramBoolean;
  }

  public PdfAppearance getAppearance(boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, DocumentException
  {
    if ((paramBoolean1) && (this.checkType == 2))
      return getAppearanceRadioCircle(paramBoolean2);
    PdfAppearance localPdfAppearance = getBorderAppearance();
    if (!paramBoolean2)
      return localPdfAppearance;
    BaseFont localBaseFont = getRealFont();
    int i = (this.borderStyle == 2) || (this.borderStyle == 3) ? 1 : 0;
    float f1 = this.box.getHeight() - this.borderWidth * 2.0F;
    float f2 = this.borderWidth;
    if (i != 0)
    {
      f1 -= this.borderWidth * 2.0F;
      f2 *= 2.0F;
    }
    float f3 = i != 0 ? 2.0F * this.borderWidth : this.borderWidth;
    f3 = Math.max(f3, 1.0F);
    float f4 = Math.min(f2, f3);
    float f5 = this.box.getWidth() - 2.0F * f4;
    float f6 = this.box.getHeight() - 2.0F * f4;
    float f7 = this.fontSize;
    if (f7 == 0.0F)
    {
      float f8 = localBaseFont.getWidthPoint(this.text, 1.0F);
      if (f8 == 0.0F)
        f7 = 12.0F;
      else
        f7 = f5 / f8;
      float f9 = f1 / localBaseFont.getFontDescriptor(1, 1.0F);
      f7 = Math.min(f7, f9);
    }
    localPdfAppearance.saveState();
    localPdfAppearance.rectangle(f4, f4, f5, f6);
    localPdfAppearance.clip();
    localPdfAppearance.newPath();
    if (this.textColor == null)
      localPdfAppearance.resetGrayFill();
    else
      localPdfAppearance.setColorFill(this.textColor);
    localPdfAppearance.beginText();
    localPdfAppearance.setFontAndSize(localBaseFont, f7);
    localPdfAppearance.setTextMatrix((this.box.getWidth() - localBaseFont.getWidthPoint(this.text, f7)) / 2.0F, (this.box.getHeight() - localBaseFont.getAscentPoint(this.text, f7)) / 2.0F);
    localPdfAppearance.showText(this.text);
    localPdfAppearance.endText();
    localPdfAppearance.restoreState();
    return localPdfAppearance;
  }

  public PdfAppearance getAppearanceRadioCircle(boolean paramBoolean)
  {
    PdfAppearance localPdfAppearance = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
    switch (this.rotation)
    {
    case 90:
      localPdfAppearance.setMatrix(0.0F, 1.0F, -1.0F, 0.0F, this.box.getHeight(), 0.0F);
      break;
    case 180:
      localPdfAppearance.setMatrix(-1.0F, 0.0F, 0.0F, -1.0F, this.box.getWidth(), this.box.getHeight());
      break;
    case 270:
      localPdfAppearance.setMatrix(0.0F, -1.0F, 1.0F, 0.0F, 0.0F, this.box.getWidth());
    }
    Rectangle localRectangle = new Rectangle(localPdfAppearance.getBoundingBox());
    float f1 = localRectangle.getWidth() / 2.0F;
    float f2 = localRectangle.getHeight() / 2.0F;
    float f3 = (Math.min(localRectangle.getWidth(), localRectangle.getHeight()) - this.borderWidth) / 2.0F;
    if (f3 <= 0.0F)
      return localPdfAppearance;
    if (this.backgroundColor != null)
    {
      localPdfAppearance.setColorFill(this.backgroundColor);
      localPdfAppearance.circle(f1, f2, f3 + this.borderWidth / 2.0F);
      localPdfAppearance.fill();
    }
    if ((this.borderWidth > 0.0F) && (this.borderColor != null))
    {
      localPdfAppearance.setLineWidth(this.borderWidth);
      localPdfAppearance.setColorStroke(this.borderColor);
      localPdfAppearance.circle(f1, f2, f3);
      localPdfAppearance.stroke();
    }
    if (paramBoolean)
    {
      if (this.textColor == null)
        localPdfAppearance.resetGrayFill();
      else
        localPdfAppearance.setColorFill(this.textColor);
      localPdfAppearance.circle(f1, f2, f3 / 2.0F);
      localPdfAppearance.fill();
    }
    return localPdfAppearance;
  }

  public PdfFormField getRadioGroup(boolean paramBoolean1, boolean paramBoolean2)
  {
    PdfFormField localPdfFormField = PdfFormField.createRadioButton(this.writer, paramBoolean1);
    if (paramBoolean2)
      localPdfFormField.setFieldFlags(33554432);
    localPdfFormField.setFieldName(this.fieldName);
    if ((this.options & 0x1) != 0)
      localPdfFormField.setFieldFlags(1);
    if ((this.options & 0x2) != 0)
      localPdfFormField.setFieldFlags(2);
    localPdfFormField.setValueAsName(this.checked ? this.onValue : "Off");
    return localPdfFormField;
  }

  public PdfFormField getRadioField()
    throws IOException, DocumentException
  {
    return getField(true);
  }

  public PdfFormField getCheckField()
    throws IOException, DocumentException
  {
    return getField(false);
  }

  protected PdfFormField getField(boolean paramBoolean)
    throws IOException, DocumentException
  {
    PdfFormField localPdfFormField = null;
    if (paramBoolean)
      localPdfFormField = PdfFormField.createEmpty(this.writer);
    else
      localPdfFormField = PdfFormField.createCheckBox(this.writer);
    localPdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
    if (!paramBoolean)
    {
      localPdfFormField.setFieldName(this.fieldName);
      if ((this.options & 0x1) != 0)
        localPdfFormField.setFieldFlags(1);
      if ((this.options & 0x2) != 0)
        localPdfFormField.setFieldFlags(2);
      localPdfFormField.setValueAsName(this.checked ? this.onValue : "Off");
    }
    if (this.text != null)
      localPdfFormField.setMKNormalCaption(this.text);
    if (this.rotation != 0)
      localPdfFormField.setMKRotation(this.rotation);
    localPdfFormField.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0F)));
    PdfAppearance localPdfAppearance1 = getAppearance(paramBoolean, true);
    PdfAppearance localPdfAppearance2 = getAppearance(paramBoolean, false);
    localPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.onValue, localPdfAppearance1);
    localPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", localPdfAppearance2);
    localPdfFormField.setAppearanceState(this.checked ? this.onValue : "Off");
    PdfAppearance localPdfAppearance3 = (PdfAppearance)localPdfAppearance1.getDuplicate();
    localPdfAppearance3.setFontAndSize(getRealFont(), this.fontSize);
    if (this.textColor == null)
      localPdfAppearance3.setGrayFill(0.0F);
    else
      localPdfAppearance3.setColorFill(this.textColor);
    localPdfFormField.setDefaultAppearanceString(localPdfAppearance3);
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
    return localPdfFormField;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.RadioCheckField
 * JD-Core Version:    0.6.0
 */