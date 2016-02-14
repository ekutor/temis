package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class PdfAcroForm extends PdfDictionary
{
  private PdfWriter writer;
  private HashMap fieldTemplates = new HashMap();
  private PdfArray documentFields = new PdfArray();
  private PdfArray calculationOrder = new PdfArray();
  private int sigFlags = 0;

  public PdfAcroForm(PdfWriter paramPdfWriter)
  {
    this.writer = paramPdfWriter;
  }

  public void setNeedAppearances(boolean paramBoolean)
  {
    put(PdfName.NEEDAPPEARANCES, new PdfBoolean(paramBoolean));
  }

  public void addFieldTemplates(HashMap paramHashMap)
  {
    this.fieldTemplates.putAll(paramHashMap);
  }

  public void addDocumentField(PdfIndirectReference paramPdfIndirectReference)
  {
    this.documentFields.add(paramPdfIndirectReference);
  }

  public boolean isValid()
  {
    if (this.documentFields.size() == 0)
      return false;
    put(PdfName.FIELDS, this.documentFields);
    if (this.sigFlags != 0)
      put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
    if (this.calculationOrder.size() > 0)
      put(PdfName.CO, this.calculationOrder);
    if (this.fieldTemplates.isEmpty())
      return true;
    PdfDictionary localPdfDictionary = new PdfDictionary();
    Object localObject = this.fieldTemplates.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      PdfTemplate localPdfTemplate = (PdfTemplate)((Iterator)localObject).next();
      PdfFormField.mergeResources(localPdfDictionary, (PdfDictionary)localPdfTemplate.getResources());
    }
    put(PdfName.DR, localPdfDictionary);
    put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
    localObject = (PdfDictionary)localPdfDictionary.get(PdfName.FONT);
    if (localObject != null)
      this.writer.eliminateFontSubset((PdfDictionary)localObject);
    return true;
  }

  public void addCalculationOrder(PdfFormField paramPdfFormField)
  {
    this.calculationOrder.add(paramPdfFormField.getIndirectReference());
  }

  public void setSigFlags(int paramInt)
  {
    this.sigFlags |= paramInt;
  }

  public void addFormField(PdfFormField paramPdfFormField)
  {
    this.writer.addAnnotation(paramPdfFormField);
  }

  public PdfFormField addHtmlPostButton(String paramString1, String paramString2, String paramString3, String paramString4, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfAction localPdfAction = PdfAction.createSubmitForm(paramString4, null, 4);
    PdfFormField localPdfFormField = new PdfFormField(this.writer, paramFloat2, paramFloat3, paramFloat4, paramFloat5, localPdfAction);
    setButtonParams(localPdfFormField, 65536, paramString1, paramString3);
    drawButton(localPdfFormField, paramString2, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addResetButton(String paramString1, String paramString2, String paramString3, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfAction localPdfAction = PdfAction.createResetForm(null, 0);
    PdfFormField localPdfFormField = new PdfFormField(this.writer, paramFloat2, paramFloat3, paramFloat4, paramFloat5, localPdfAction);
    setButtonParams(localPdfFormField, 65536, paramString1, paramString3);
    drawButton(localPdfFormField, paramString2, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addMap(String paramString1, String paramString2, String paramString3, PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfAction localPdfAction = PdfAction.createSubmitForm(paramString3, null, 20);
    PdfFormField localPdfFormField = new PdfFormField(this.writer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, localPdfAction);
    setButtonParams(localPdfFormField, 65536, paramString1, null);
    PdfAppearance localPdfAppearance = PdfAppearance.createAppearance(this.writer, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance.add(paramPdfContentByte);
    localPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, localPdfAppearance);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public void setButtonParams(PdfFormField paramPdfFormField, int paramInt, String paramString1, String paramString2)
  {
    paramPdfFormField.setButton(paramInt);
    paramPdfFormField.setFlags(4);
    paramPdfFormField.setPage();
    paramPdfFormField.setFieldName(paramString1);
    if (paramString2 != null)
      paramPdfFormField.setValueAsString(paramString2);
  }

  public void drawButton(PdfFormField paramPdfFormField, String paramString, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfAppearance localPdfAppearance = PdfAppearance.createAppearance(this.writer, paramFloat4 - paramFloat2, paramFloat5 - paramFloat3);
    localPdfAppearance.drawButton(0.0F, 0.0F, paramFloat4 - paramFloat2, paramFloat5 - paramFloat3, paramString, paramBaseFont, paramFloat1);
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, localPdfAppearance);
  }

  public PdfFormField addHiddenField(String paramString1, String paramString2)
  {
    PdfFormField localPdfFormField = PdfFormField.createEmpty(this.writer);
    localPdfFormField.setFieldName(paramString1);
    localPdfFormField.setValueAsName(paramString2);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addSingleLineTextField(String paramString1, String paramString2, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createTextField(this.writer, false, false, 0);
    setTextFieldParams(localPdfFormField, paramString2, paramString1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    drawSingleLineOfText(localPdfFormField, paramString2, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addMultiLineTextField(String paramString1, String paramString2, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createTextField(this.writer, true, false, 0);
    setTextFieldParams(localPdfFormField, paramString2, paramString1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    drawMultiLineOfText(localPdfFormField, paramString2, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addSingleLinePasswordField(String paramString1, String paramString2, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createTextField(this.writer, false, true, 0);
    setTextFieldParams(localPdfFormField, paramString2, paramString1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    drawSingleLineOfText(localPdfFormField, paramString2, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public void setTextFieldParams(PdfFormField paramPdfFormField, String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramPdfFormField.setWidget(new Rectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4), PdfAnnotation.HIGHLIGHT_INVERT);
    paramPdfFormField.setValueAsString(paramString1);
    paramPdfFormField.setDefaultValueAsString(paramString1);
    paramPdfFormField.setFieldName(paramString2);
    paramPdfFormField.setFlags(4);
    paramPdfFormField.setPage();
  }

  public void drawSingleLineOfText(PdfFormField paramPdfFormField, String paramString, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfAppearance localPdfAppearance1 = PdfAppearance.createAppearance(this.writer, paramFloat4 - paramFloat2, paramFloat5 - paramFloat3);
    PdfAppearance localPdfAppearance2 = (PdfAppearance)localPdfAppearance1.getDuplicate();
    localPdfAppearance2.setFontAndSize(paramBaseFont, paramFloat1);
    localPdfAppearance2.resetRGBColorFill();
    paramPdfFormField.setDefaultAppearanceString(localPdfAppearance2);
    localPdfAppearance1.drawTextField(0.0F, 0.0F, paramFloat4 - paramFloat2, paramFloat5 - paramFloat3);
    localPdfAppearance1.beginVariableText();
    localPdfAppearance1.saveState();
    localPdfAppearance1.rectangle(3.0F, 3.0F, paramFloat4 - paramFloat2 - 6.0F, paramFloat5 - paramFloat3 - 6.0F);
    localPdfAppearance1.clip();
    localPdfAppearance1.newPath();
    localPdfAppearance1.beginText();
    localPdfAppearance1.setFontAndSize(paramBaseFont, paramFloat1);
    localPdfAppearance1.resetRGBColorFill();
    localPdfAppearance1.setTextMatrix(4.0F, (paramFloat5 - paramFloat3) / 2.0F - paramFloat1 * 0.3F);
    localPdfAppearance1.showText(paramString);
    localPdfAppearance1.endText();
    localPdfAppearance1.restoreState();
    localPdfAppearance1.endVariableText();
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, localPdfAppearance1);
  }

  public void drawMultiLineOfText(PdfFormField paramPdfFormField, String paramString, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfAppearance localPdfAppearance1 = PdfAppearance.createAppearance(this.writer, paramFloat4 - paramFloat2, paramFloat5 - paramFloat3);
    PdfAppearance localPdfAppearance2 = (PdfAppearance)localPdfAppearance1.getDuplicate();
    localPdfAppearance2.setFontAndSize(paramBaseFont, paramFloat1);
    localPdfAppearance2.resetRGBColorFill();
    paramPdfFormField.setDefaultAppearanceString(localPdfAppearance2);
    localPdfAppearance1.drawTextField(0.0F, 0.0F, paramFloat4 - paramFloat2, paramFloat5 - paramFloat3);
    localPdfAppearance1.beginVariableText();
    localPdfAppearance1.saveState();
    localPdfAppearance1.rectangle(3.0F, 3.0F, paramFloat4 - paramFloat2 - 6.0F, paramFloat5 - paramFloat3 - 6.0F);
    localPdfAppearance1.clip();
    localPdfAppearance1.newPath();
    localPdfAppearance1.beginText();
    localPdfAppearance1.setFontAndSize(paramBaseFont, paramFloat1);
    localPdfAppearance1.resetRGBColorFill();
    localPdfAppearance1.setTextMatrix(4.0F, 5.0F);
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\n");
    float f = paramFloat5 - paramFloat3;
    while (localStringTokenizer.hasMoreTokens())
    {
      f -= paramFloat1 * 1.2F;
      localPdfAppearance1.showTextAligned(0, localStringTokenizer.nextToken(), 3.0F, f, 0.0F);
    }
    localPdfAppearance1.endText();
    localPdfAppearance1.restoreState();
    localPdfAppearance1.endVariableText();
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, localPdfAppearance1);
  }

  public PdfFormField addCheckBox(String paramString1, String paramString2, boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfFormField localPdfFormField = PdfFormField.createCheckBox(this.writer);
    setCheckBoxParams(localPdfFormField, paramString1, paramString2, paramBoolean, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    drawCheckBoxAppearences(localPdfFormField, paramString2, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public void setCheckBoxParams(PdfFormField paramPdfFormField, String paramString1, String paramString2, boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramPdfFormField.setWidget(new Rectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4), PdfAnnotation.HIGHLIGHT_TOGGLE);
    paramPdfFormField.setFieldName(paramString1);
    if (paramBoolean)
    {
      paramPdfFormField.setValueAsName(paramString2);
      paramPdfFormField.setAppearanceState(paramString2);
    }
    else
    {
      paramPdfFormField.setValueAsName("Off");
      paramPdfFormField.setAppearanceState("Off");
    }
    paramPdfFormField.setFlags(4);
    paramPdfFormField.setPage();
    paramPdfFormField.setBorderStyle(new PdfBorderDictionary(1.0F, 0));
  }

  public void drawCheckBoxAppearences(PdfFormField paramPdfFormField, String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    BaseFont localBaseFont = null;
    try
    {
      localBaseFont = BaseFont.createFont("ZapfDingbats", "Cp1252", false);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    float f = paramFloat4 - paramFloat2;
    PdfAppearance localPdfAppearance1 = PdfAppearance.createAppearance(this.writer, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    PdfAppearance localPdfAppearance2 = (PdfAppearance)localPdfAppearance1.getDuplicate();
    localPdfAppearance2.setFontAndSize(localBaseFont, f);
    localPdfAppearance2.resetRGBColorFill();
    paramPdfFormField.setDefaultAppearanceString(localPdfAppearance2);
    localPdfAppearance1.drawTextField(0.0F, 0.0F, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance1.saveState();
    localPdfAppearance1.resetRGBColorFill();
    localPdfAppearance1.beginText();
    localPdfAppearance1.setFontAndSize(localBaseFont, f);
    localPdfAppearance1.showTextAligned(1, "4", (paramFloat3 - paramFloat1) / 2.0F, (paramFloat4 - paramFloat2) / 2.0F - f * 0.3F, 0.0F);
    localPdfAppearance1.endText();
    localPdfAppearance1.restoreState();
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, paramString, localPdfAppearance1);
    PdfAppearance localPdfAppearance3 = PdfAppearance.createAppearance(this.writer, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance3.drawTextField(0.0F, 0.0F, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", localPdfAppearance3);
  }

  public PdfFormField getRadioGroup(String paramString1, String paramString2, boolean paramBoolean)
  {
    PdfFormField localPdfFormField = PdfFormField.createRadioButton(this.writer, paramBoolean);
    localPdfFormField.setFieldName(paramString1);
    localPdfFormField.setValueAsName(paramString2);
    return localPdfFormField;
  }

  public void addRadioGroup(PdfFormField paramPdfFormField)
  {
    addFormField(paramPdfFormField);
  }

  public PdfFormField addRadioButton(PdfFormField paramPdfFormField, String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfFormField localPdfFormField = PdfFormField.createEmpty(this.writer);
    localPdfFormField.setWidget(new Rectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4), PdfAnnotation.HIGHLIGHT_TOGGLE);
    String str = ((PdfName)paramPdfFormField.get(PdfName.V)).toString().substring(1);
    if (str.equals(paramString))
      localPdfFormField.setAppearanceState(paramString);
    else
      localPdfFormField.setAppearanceState("Off");
    drawRadioAppearences(localPdfFormField, paramString, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    paramPdfFormField.addKid(localPdfFormField);
    return localPdfFormField;
  }

  public void drawRadioAppearences(PdfFormField paramPdfFormField, String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfAppearance localPdfAppearance1 = PdfAppearance.createAppearance(this.writer, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance1.drawRadioField(0.0F, 0.0F, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2, true);
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, paramString, localPdfAppearance1);
    PdfAppearance localPdfAppearance2 = PdfAppearance.createAppearance(this.writer, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance2.drawRadioField(0.0F, 0.0F, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2, false);
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", localPdfAppearance2);
  }

  public PdfFormField addSelectList(String paramString1, String[] paramArrayOfString, String paramString2, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createList(this.writer, paramArrayOfString, 0);
    setChoiceParams(localPdfFormField, paramString1, paramString2, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfString.length; i++)
      localStringBuffer.append(paramArrayOfString[i]).append('\n');
    drawMultiLineOfText(localPdfFormField, localStringBuffer.toString(), paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addSelectList(String paramString1, String[][] paramArrayOfString, String paramString2, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createList(this.writer, paramArrayOfString, 0);
    setChoiceParams(localPdfFormField, paramString1, paramString2, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfString.length; i++)
      localStringBuffer.append(paramArrayOfString[i][1]).append('\n');
    drawMultiLineOfText(localPdfFormField, localStringBuffer.toString(), paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addComboBox(String paramString1, String[] paramArrayOfString, String paramString2, boolean paramBoolean, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createCombo(this.writer, paramBoolean, paramArrayOfString, 0);
    setChoiceParams(localPdfFormField, paramString1, paramString2, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    if (paramString2 == null)
      paramString2 = paramArrayOfString[0];
    drawSingleLineOfText(localPdfFormField, paramString2, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public PdfFormField addComboBox(String paramString1, String[][] paramArrayOfString, String paramString2, boolean paramBoolean, BaseFont paramBaseFont, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    PdfFormField localPdfFormField = PdfFormField.createCombo(this.writer, paramBoolean, paramArrayOfString, 0);
    setChoiceParams(localPdfFormField, paramString1, paramString2, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    String str = null;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if (!paramArrayOfString[i][0].equals(paramString2))
        continue;
      str = paramArrayOfString[i][1];
      break;
    }
    if (str == null)
      str = paramArrayOfString[0][1];
    drawSingleLineOfText(localPdfFormField, str, paramBaseFont, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public void setChoiceParams(PdfFormField paramPdfFormField, String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramPdfFormField.setWidget(new Rectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4), PdfAnnotation.HIGHLIGHT_INVERT);
    if (paramString2 != null)
    {
      paramPdfFormField.setValueAsString(paramString2);
      paramPdfFormField.setDefaultValueAsString(paramString2);
    }
    paramPdfFormField.setFieldName(paramString1);
    paramPdfFormField.setFlags(4);
    paramPdfFormField.setPage();
    paramPdfFormField.setBorderStyle(new PdfBorderDictionary(2.0F, 0));
  }

  public PdfFormField addSignature(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfFormField localPdfFormField = PdfFormField.createSignature(this.writer);
    setSignatureParams(localPdfFormField, paramString, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    drawSignatureAppearences(localPdfFormField, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    addFormField(localPdfFormField);
    return localPdfFormField;
  }

  public void setSignatureParams(PdfFormField paramPdfFormField, String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramPdfFormField.setWidget(new Rectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4), PdfAnnotation.HIGHLIGHT_INVERT);
    paramPdfFormField.setFieldName(paramString);
    paramPdfFormField.setFlags(4);
    paramPdfFormField.setPage();
    paramPdfFormField.setMKBorderColor(Color.black);
    paramPdfFormField.setMKBackgroundColor(Color.white);
  }

  public void drawSignatureAppearences(PdfFormField paramPdfFormField, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfAppearance localPdfAppearance = PdfAppearance.createAppearance(this.writer, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance.setGrayFill(1.0F);
    localPdfAppearance.rectangle(0.0F, 0.0F, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    localPdfAppearance.fill();
    localPdfAppearance.setGrayStroke(0.0F);
    localPdfAppearance.setLineWidth(1.0F);
    localPdfAppearance.rectangle(0.5F, 0.5F, paramFloat3 - paramFloat1 - 0.5F, paramFloat4 - paramFloat2 - 0.5F);
    localPdfAppearance.closePathStroke();
    localPdfAppearance.saveState();
    localPdfAppearance.rectangle(1.0F, 1.0F, paramFloat3 - paramFloat1 - 2.0F, paramFloat4 - paramFloat2 - 2.0F);
    localPdfAppearance.clip();
    localPdfAppearance.newPath();
    localPdfAppearance.restoreState();
    paramPdfFormField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, localPdfAppearance);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfAcroForm
 * JD-Core Version:    0.6.0
 */