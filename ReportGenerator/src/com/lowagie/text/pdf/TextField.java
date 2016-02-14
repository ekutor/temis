package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class TextField extends BaseField
{
  private String defaultText;
  private String[] choices;
  private String[] choiceExports;
  private int choiceSelection;
  private int topFirst;
  private float extraMarginLeft;
  private float extraMarginTop;
  private ArrayList substitutionFonts;
  private BaseFont extensionFont;

  public TextField(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString)
  {
    super(paramPdfWriter, paramRectangle, paramString);
  }

  private static boolean checkRTL(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return false;
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
    {
      int j = arrayOfChar[i];
      if ((j >= 1424) && (j < 1920))
        return true;
    }
    return false;
  }

  private static void changeFontSize(Phrase paramPhrase, float paramFloat)
  {
    for (int i = 0; i < paramPhrase.size(); i++)
      ((Chunk)paramPhrase.get(i)).getFont().setSize(paramFloat);
  }

  private Phrase composePhrase(String paramString, BaseFont paramBaseFont, Color paramColor, float paramFloat)
  {
    Phrase localPhrase = null;
    if ((this.extensionFont == null) && ((this.substitutionFonts == null) || (this.substitutionFonts.isEmpty())))
    {
      localPhrase = new Phrase(new Chunk(paramString, new Font(paramBaseFont, paramFloat, 0, paramColor)));
    }
    else
    {
      FontSelector localFontSelector = new FontSelector();
      localFontSelector.addFont(new Font(paramBaseFont, paramFloat, 0, paramColor));
      if (this.extensionFont != null)
        localFontSelector.addFont(new Font(this.extensionFont, paramFloat, 0, paramColor));
      if (this.substitutionFonts != null)
        for (int i = 0; i < this.substitutionFonts.size(); i++)
          localFontSelector.addFont(new Font((BaseFont)this.substitutionFonts.get(i), paramFloat, 0, paramColor));
      localPhrase = localFontSelector.process(paramString);
    }
    return localPhrase;
  }

  public static String removeCRLF(String paramString)
  {
    if ((paramString.indexOf('\n') >= 0) || (paramString.indexOf('\r') >= 0))
    {
      char[] arrayOfChar = paramString.toCharArray();
      StringBuffer localStringBuffer = new StringBuffer(arrayOfChar.length);
      for (int i = 0; i < arrayOfChar.length; i++)
      {
        char c = arrayOfChar[i];
        if (c == '\n')
        {
          localStringBuffer.append(' ');
        }
        else if (c == '\r')
        {
          localStringBuffer.append(' ');
          if ((i >= arrayOfChar.length - 1) || (arrayOfChar[(i + 1)] != '\n'))
            continue;
          i++;
        }
        else
        {
          localStringBuffer.append(c);
        }
      }
      return localStringBuffer.toString();
    }
    return paramString;
  }

  public static String obfuscatePassword(String paramString)
  {
    char[] arrayOfChar = new char[paramString.length()];
    for (int i = 0; i < paramString.length(); i++)
      arrayOfChar[i] = '*';
    return new String(arrayOfChar);
  }

  public PdfAppearance getAppearance()
    throws IOException, DocumentException
  {
    PdfAppearance localPdfAppearance = getBorderAppearance();
    localPdfAppearance.beginVariableText();
    if ((this.text == null) || (this.text.length() == 0))
    {
      localPdfAppearance.endVariableText();
      return localPdfAppearance;
    }
    int i = (this.borderStyle == 2) || (this.borderStyle == 3) ? 1 : 0;
    float f1 = this.box.getHeight() - this.borderWidth * 2.0F - this.extraMarginTop;
    float f2 = this.borderWidth;
    if (i != 0)
    {
      f1 -= this.borderWidth * 2.0F;
      f2 *= 2.0F;
    }
    float f3 = Math.max(f2, 1.0F);
    float f4 = Math.min(f2, f3);
    localPdfAppearance.saveState();
    localPdfAppearance.rectangle(f4, f4, this.box.getWidth() - 2.0F * f4, this.box.getHeight() - 2.0F * f4);
    localPdfAppearance.clip();
    localPdfAppearance.newPath();
    String str1;
    if ((this.options & 0x2000) != 0)
      str1 = obfuscatePassword(this.text);
    else if ((this.options & 0x1000) == 0)
      str1 = removeCRLF(this.text);
    else
      str1 = this.text;
    BaseFont localBaseFont1 = getRealFont();
    Color localColor = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
    int j = checkRTL(str1) ? 2 : 1;
    float f5 = this.fontSize;
    Phrase localPhrase = composePhrase(str1, localBaseFont1, localColor, f5);
    float f6;
    float f7;
    float f10;
    float f11;
    if ((this.options & 0x1000) != 0)
    {
      f6 = this.box.getWidth() - 4.0F * f3 - this.extraMarginLeft;
      f7 = localBaseFont1.getFontDescriptor(8, 1.0F) - localBaseFont1.getFontDescriptor(6, 1.0F);
      ColumnText localColumnText = new ColumnText(null);
      if (f5 == 0.0F)
      {
        f5 = f1 / f7;
        if (f5 > 4.0F)
        {
          if (f5 > 12.0F)
            f5 = 12.0F;
          f10 = Math.max((f5 - 4.0F) / 10.0F, 0.2F);
          localColumnText.setSimpleColumn(0.0F, -f1, f6, 0.0F);
          localColumnText.setAlignment(this.alignment);
          localColumnText.setRunDirection(j);
          while (f5 > 4.0F)
          {
            localColumnText.setYLine(0.0F);
            changeFontSize(localPhrase, f5);
            localColumnText.setText(localPhrase);
            localColumnText.setLeading(f7 * f5);
            int n = localColumnText.go(true);
            if ((n & 0x2) == 0)
              break;
            f5 -= f10;
          }
        }
        if (f5 < 4.0F)
          f5 = 4.0F;
      }
      changeFontSize(localPhrase, f5);
      localColumnText.setCanvas(localPdfAppearance);
      f10 = f5 * f7;
      f11 = f3 + f1 - localBaseFont1.getFontDescriptor(8, f5);
      localColumnText.setSimpleColumn(this.extraMarginLeft + 2.0F * f3, -20000.0F, this.box.getWidth() - 2.0F * f3, f11 + f10);
      localColumnText.setLeading(f10);
      localColumnText.setAlignment(this.alignment);
      localColumnText.setRunDirection(j);
      localColumnText.setText(localPhrase);
      localColumnText.go();
    }
    else
    {
      if (f5 == 0.0F)
      {
        f6 = f1 / (localBaseFont1.getFontDescriptor(7, 1.0F) - localBaseFont1.getFontDescriptor(6, 1.0F));
        changeFontSize(localPhrase, 1.0F);
        f7 = ColumnText.getWidth(localPhrase, j, 0);
        if (f7 == 0.0F)
          f5 = f6;
        else
          f5 = Math.min(f6, (this.box.getWidth() - this.extraMarginLeft - 4.0F * f3) / f7);
        if (f5 < 4.0F)
          f5 = 4.0F;
      }
      changeFontSize(localPhrase, f5);
      f6 = f4 + (this.box.getHeight() - 2.0F * f4 - localBaseFont1.getFontDescriptor(1, f5)) / 2.0F;
      if (f6 < f4)
        f6 = f4;
      if (f6 - f4 < -localBaseFont1.getFontDescriptor(3, f5))
      {
        f7 = -localBaseFont1.getFontDescriptor(3, f5) + f4;
        float f9 = this.box.getHeight() - f4 - localBaseFont1.getFontDescriptor(1, f5);
        f6 = Math.min(f7, Math.max(f6, f9));
      }
      if (((this.options & 0x1000000) != 0) && (this.maxCharacterLength > 0))
      {
        int k = Math.min(this.maxCharacterLength, str1.length());
        int m = 0;
        if (this.alignment == 2)
          m = this.maxCharacterLength - k;
        else if (this.alignment == 1)
          m = (this.maxCharacterLength - k) / 2;
        f10 = (this.box.getWidth() - this.extraMarginLeft) / this.maxCharacterLength;
        f11 = f10 / 2.0F + m * f10;
        if (this.textColor == null)
          localPdfAppearance.setGrayFill(0.0F);
        else
          localPdfAppearance.setColorFill(this.textColor);
        localPdfAppearance.beginText();
        for (int i1 = 0; i1 < localPhrase.size(); i1++)
        {
          Chunk localChunk = (Chunk)localPhrase.get(i1);
          BaseFont localBaseFont2 = localChunk.getFont().getBaseFont();
          localPdfAppearance.setFontAndSize(localBaseFont2, f5);
          StringBuffer localStringBuffer = localChunk.append("");
          for (int i2 = 0; i2 < localStringBuffer.length(); i2++)
          {
            String str2 = localStringBuffer.substring(i2, i2 + 1);
            float f12 = localBaseFont2.getWidthPoint(str2, f5);
            localPdfAppearance.setTextMatrix(this.extraMarginLeft + f11 - f12 / 2.0F, f6 - this.extraMarginTop);
            localPdfAppearance.showText(str2);
            f11 += f10;
          }
        }
        localPdfAppearance.endText();
      }
      else
      {
        float f8;
        switch (this.alignment)
        {
        case 2:
          f8 = this.extraMarginLeft + this.box.getWidth() - 2.0F * f3;
          break;
        case 1:
          f8 = this.extraMarginLeft + this.box.getWidth() / 2.0F;
          break;
        default:
          f8 = this.extraMarginLeft + 2.0F * f3;
        }
        ColumnText.showTextAligned(localPdfAppearance, this.alignment, localPhrase, f8, f6 - this.extraMarginTop, 0.0F, j, 0);
      }
    }
    localPdfAppearance.restoreState();
    localPdfAppearance.endVariableText();
    return localPdfAppearance;
  }

  PdfAppearance getListAppearance()
    throws IOException, DocumentException
  {
    PdfAppearance localPdfAppearance = getBorderAppearance();
    localPdfAppearance.beginVariableText();
    if ((this.choices == null) || (this.choices.length == 0))
    {
      localPdfAppearance.endVariableText();
      return localPdfAppearance;
    }
    int i = this.choiceSelection;
    if (i >= this.choices.length)
      i = this.choices.length - 1;
    if (i < 0)
      i = 0;
    BaseFont localBaseFont = getRealFont();
    float f1 = this.fontSize;
    if (f1 == 0.0F)
      f1 = 12.0F;
    int j = (this.borderStyle == 2) || (this.borderStyle == 3) ? 1 : 0;
    float f2 = this.box.getHeight() - this.borderWidth * 2.0F;
    float f3 = this.borderWidth;
    if (j != 0)
    {
      f2 -= this.borderWidth * 2.0F;
      f3 *= 2.0F;
    }
    float f4 = localBaseFont.getFontDescriptor(8, f1) - localBaseFont.getFontDescriptor(6, f1);
    int k = (int)(f2 / f4) + 1;
    int m = 0;
    int n = 0;
    n = i + k / 2 + 1;
    m = n - k;
    if (m < 0)
    {
      n += m;
      m = 0;
    }
    n = m + k;
    if (n > this.choices.length)
      n = this.choices.length;
    this.topFirst = m;
    localPdfAppearance.saveState();
    localPdfAppearance.rectangle(f3, f3, this.box.getWidth() - 2.0F * f3, this.box.getHeight() - 2.0F * f3);
    localPdfAppearance.clip();
    localPdfAppearance.newPath();
    Color localColor = this.textColor == null ? GrayColor.GRAYBLACK : this.textColor;
    localPdfAppearance.setColorFill(new Color(10, 36, 106));
    localPdfAppearance.rectangle(f3, f3 + f2 - (i - m + 1) * f4, this.box.getWidth() - 2.0F * f3, f4);
    localPdfAppearance.fill();
    float f5 = f3 * 2.0F;
    float f6 = f3 + f2 - localBaseFont.getFontDescriptor(8, f1);
    int i1 = m;
    while (i1 < n)
    {
      String str = this.choices[i1];
      int i2 = checkRTL(str) ? 2 : 1;
      str = removeCRLF(str);
      Phrase localPhrase = composePhrase(str, localBaseFont, i1 == i ? GrayColor.GRAYWHITE : localColor, f1);
      ColumnText.showTextAligned(localPdfAppearance, 0, localPhrase, f5, f6, 0.0F, i2, 0);
      i1++;
      f6 -= f4;
    }
    localPdfAppearance.restoreState();
    localPdfAppearance.endVariableText();
    return localPdfAppearance;
  }

  public PdfFormField getTextField()
    throws IOException, DocumentException
  {
    if (this.maxCharacterLength <= 0)
      this.options &= -16777217;
    if ((this.options & 0x1000000) != 0)
      this.options &= -4097;
    PdfFormField localPdfFormField = PdfFormField.createTextField(this.writer, false, false, this.maxCharacterLength);
    localPdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
    switch (this.alignment)
    {
    case 1:
      localPdfFormField.setQuadding(1);
      break;
    case 2:
      localPdfFormField.setQuadding(2);
    }
    if (this.rotation != 0)
      localPdfFormField.setMKRotation(this.rotation);
    if (this.fieldName != null)
    {
      localPdfFormField.setFieldName(this.fieldName);
      if (!"".equals(this.text))
        localPdfFormField.setValueAsString(this.text);
      if (this.defaultText != null)
        localPdfFormField.setDefaultValueAsString(this.defaultText);
      if ((this.options & 0x1) != 0)
        localPdfFormField.setFieldFlags(1);
      if ((this.options & 0x2) != 0)
        localPdfFormField.setFieldFlags(2);
      if ((this.options & 0x1000) != 0)
        localPdfFormField.setFieldFlags(4096);
      if ((this.options & 0x800000) != 0)
        localPdfFormField.setFieldFlags(8388608);
      if ((this.options & 0x2000) != 0)
        localPdfFormField.setFieldFlags(8192);
      if ((this.options & 0x100000) != 0)
        localPdfFormField.setFieldFlags(1048576);
      if ((this.options & 0x400000) != 0)
        localPdfFormField.setFieldFlags(4194304);
      if ((this.options & 0x1000000) != 0)
        localPdfFormField.setFieldFlags(16777216);
    }
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
    return localPdfFormField;
  }

  public PdfFormField getComboField()
    throws IOException, DocumentException
  {
    return getChoiceField(false);
  }

  public PdfFormField getListField()
    throws IOException, DocumentException
  {
    return getChoiceField(true);
  }

  protected PdfFormField getChoiceField(boolean paramBoolean)
    throws IOException, DocumentException
  {
    this.options &= -16781313;
    String[] arrayOfString = this.choices;
    if (arrayOfString == null)
      arrayOfString = new String[0];
    int i = this.choiceSelection;
    if (i >= arrayOfString.length)
      i = arrayOfString.length - 1;
    if (this.text == null)
      this.text = "";
    if (i >= 0)
      this.text = arrayOfString[i];
    if (i < 0)
      i = 0;
    PdfFormField localPdfFormField = null;
    String[][] arrayOfString1 = (String[][])null;
    if (this.choiceExports == null)
    {
      if (paramBoolean)
        localPdfFormField = PdfFormField.createList(this.writer, arrayOfString, i);
      else
        localPdfFormField = PdfFormField.createCombo(this.writer, (this.options & 0x40000) != 0, arrayOfString, i);
    }
    else
    {
      arrayOfString1 = new String[arrayOfString.length][2];
      for (int j = 0; j < arrayOfString1.length; j++)
      {
        String tmp170_169 = arrayOfString[j];
        arrayOfString1[j][1] = tmp170_169;
        arrayOfString1[j][0] = tmp170_169;
      }
      j = Math.min(arrayOfString.length, this.choiceExports.length);
      for (int k = 0; k < j; k++)
      {
        if (this.choiceExports[k] == null)
          continue;
        arrayOfString1[k][0] = this.choiceExports[k];
      }
      if (paramBoolean)
        localPdfFormField = PdfFormField.createList(this.writer, arrayOfString1, i);
      else
        localPdfFormField = PdfFormField.createCombo(this.writer, (this.options & 0x40000) != 0, arrayOfString1, i);
    }
    localPdfFormField.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
    if (this.rotation != 0)
      localPdfFormField.setMKRotation(this.rotation);
    if (this.fieldName != null)
    {
      localPdfFormField.setFieldName(this.fieldName);
      if (arrayOfString.length > 0)
        if (arrayOfString1 != null)
        {
          localPdfFormField.setValueAsString(arrayOfString1[i][0]);
          localPdfFormField.setDefaultValueAsString(arrayOfString1[i][0]);
        }
        else
        {
          localPdfFormField.setValueAsString(this.text);
          localPdfFormField.setDefaultValueAsString(this.text);
        }
      if ((this.options & 0x1) != 0)
        localPdfFormField.setFieldFlags(1);
      if ((this.options & 0x2) != 0)
        localPdfFormField.setFieldFlags(2);
      if ((this.options & 0x400000) != 0)
        localPdfFormField.setFieldFlags(4194304);
    }
    localPdfFormField.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0F)));
    PdfAppearance localPdfAppearance1;
    if (paramBoolean)
    {
      localPdfAppearance1 = getListAppearance();
      if (this.topFirst > 0)
        localPdfFormField.put(PdfName.TI, new PdfNumber(this.topFirst));
    }
    else
    {
      localPdfAppearance1 = getAppearance();
    }
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
    return localPdfFormField;
  }

  public String getDefaultText()
  {
    return this.defaultText;
  }

  public void setDefaultText(String paramString)
  {
    this.defaultText = paramString;
  }

  public String[] getChoices()
  {
    return this.choices;
  }

  public void setChoices(String[] paramArrayOfString)
  {
    this.choices = paramArrayOfString;
  }

  public String[] getChoiceExports()
  {
    return this.choiceExports;
  }

  public void setChoiceExports(String[] paramArrayOfString)
  {
    this.choiceExports = paramArrayOfString;
  }

  public int getChoiceSelection()
  {
    return this.choiceSelection;
  }

  public void setChoiceSelection(int paramInt)
  {
    this.choiceSelection = paramInt;
  }

  int getTopFirst()
  {
    return this.topFirst;
  }

  public void setExtraMargin(float paramFloat1, float paramFloat2)
  {
    this.extraMarginLeft = paramFloat1;
    this.extraMarginTop = paramFloat2;
  }

  public ArrayList getSubstitutionFonts()
  {
    return this.substitutionFonts;
  }

  public void setSubstitutionFonts(ArrayList paramArrayList)
  {
    this.substitutionFonts = paramArrayList;
  }

  public BaseFont getExtensionFont()
  {
    return this.extensionFont;
  }

  public void setExtensionFont(BaseFont paramBaseFont)
  {
    this.extensionFont = paramBaseFont;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.TextField
 * JD-Core Version:    0.6.0
 */