package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfFormField extends PdfAnnotation
{
  public static final int FF_READ_ONLY = 1;
  public static final int FF_REQUIRED = 2;
  public static final int FF_NO_EXPORT = 4;
  public static final int FF_NO_TOGGLE_TO_OFF = 16384;
  public static final int FF_RADIO = 32768;
  public static final int FF_PUSHBUTTON = 65536;
  public static final int FF_MULTILINE = 4096;
  public static final int FF_PASSWORD = 8192;
  public static final int FF_COMBO = 131072;
  public static final int FF_EDIT = 262144;
  public static final int FF_FILESELECT = 1048576;
  public static final int FF_MULTISELECT = 2097152;
  public static final int FF_DONOTSPELLCHECK = 4194304;
  public static final int FF_DONOTSCROLL = 8388608;
  public static final int FF_COMB = 16777216;
  public static final int FF_RADIOSINUNISON = 33554432;
  public static final int Q_LEFT = 0;
  public static final int Q_CENTER = 1;
  public static final int Q_RIGHT = 2;
  public static final int MK_NO_ICON = 0;
  public static final int MK_NO_CAPTION = 1;
  public static final int MK_CAPTION_BELOW = 2;
  public static final int MK_CAPTION_ABOVE = 3;
  public static final int MK_CAPTION_RIGHT = 4;
  public static final int MK_CAPTION_LEFT = 5;
  public static final int MK_CAPTION_OVERLAID = 6;
  public static final PdfName IF_SCALE_ALWAYS = PdfName.A;
  public static final PdfName IF_SCALE_BIGGER = PdfName.B;
  public static final PdfName IF_SCALE_SMALLER = PdfName.S;
  public static final PdfName IF_SCALE_NEVER = PdfName.N;
  public static final PdfName IF_SCALE_ANAMORPHIC = PdfName.A;
  public static final PdfName IF_SCALE_PROPORTIONAL = PdfName.P;
  public static final boolean MULTILINE = true;
  public static final boolean SINGLELINE = false;
  public static final boolean PLAINTEXT = false;
  public static final boolean PASSWORD = true;
  static PdfName[] mergeTarget = { PdfName.FONT, PdfName.XOBJECT, PdfName.COLORSPACE, PdfName.PATTERN };
  protected PdfFormField parent;
  protected ArrayList kids;

  public PdfFormField(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, PdfAction paramPdfAction)
  {
    super(paramPdfWriter, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramPdfAction);
    put(PdfName.TYPE, PdfName.ANNOT);
    put(PdfName.SUBTYPE, PdfName.WIDGET);
    this.annotation = true;
  }

  protected PdfFormField(PdfWriter paramPdfWriter)
  {
    super(paramPdfWriter, null);
    this.form = true;
    this.annotation = false;
  }

  public void setWidget(Rectangle paramRectangle, PdfName paramPdfName)
  {
    put(PdfName.TYPE, PdfName.ANNOT);
    put(PdfName.SUBTYPE, PdfName.WIDGET);
    put(PdfName.RECT, new PdfRectangle(paramRectangle));
    this.annotation = true;
    if ((paramPdfName != null) && (!paramPdfName.equals(HIGHLIGHT_INVERT)))
      put(PdfName.H, paramPdfName);
  }

  public static PdfFormField createEmpty(PdfWriter paramPdfWriter)
  {
    PdfFormField localPdfFormField = new PdfFormField(paramPdfWriter);
    return localPdfFormField;
  }

  public void setButton(int paramInt)
  {
    put(PdfName.FT, PdfName.BTN);
    if (paramInt != 0)
      put(PdfName.FF, new PdfNumber(paramInt));
  }

  protected static PdfFormField createButton(PdfWriter paramPdfWriter, int paramInt)
  {
    PdfFormField localPdfFormField = new PdfFormField(paramPdfWriter);
    localPdfFormField.setButton(paramInt);
    return localPdfFormField;
  }

  public static PdfFormField createPushButton(PdfWriter paramPdfWriter)
  {
    return createButton(paramPdfWriter, 65536);
  }

  public static PdfFormField createCheckBox(PdfWriter paramPdfWriter)
  {
    return createButton(paramPdfWriter, 0);
  }

  public static PdfFormField createRadioButton(PdfWriter paramPdfWriter, boolean paramBoolean)
  {
    return createButton(paramPdfWriter, 32768 + (paramBoolean ? 16384 : 0));
  }

  public static PdfFormField createTextField(PdfWriter paramPdfWriter, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    PdfFormField localPdfFormField = new PdfFormField(paramPdfWriter);
    localPdfFormField.put(PdfName.FT, PdfName.TX);
    int i = paramBoolean1 ? 4096 : 0;
    i += (paramBoolean2 ? 8192 : 0);
    localPdfFormField.put(PdfName.FF, new PdfNumber(i));
    if (paramInt > 0)
      localPdfFormField.put(PdfName.MAXLEN, new PdfNumber(paramInt));
    return localPdfFormField;
  }

  protected static PdfFormField createChoice(PdfWriter paramPdfWriter, int paramInt1, PdfArray paramPdfArray, int paramInt2)
  {
    PdfFormField localPdfFormField = new PdfFormField(paramPdfWriter);
    localPdfFormField.put(PdfName.FT, PdfName.CH);
    localPdfFormField.put(PdfName.FF, new PdfNumber(paramInt1));
    localPdfFormField.put(PdfName.OPT, paramPdfArray);
    if (paramInt2 > 0)
      localPdfFormField.put(PdfName.TI, new PdfNumber(paramInt2));
    return localPdfFormField;
  }

  public static PdfFormField createList(PdfWriter paramPdfWriter, String[] paramArrayOfString, int paramInt)
  {
    return createChoice(paramPdfWriter, 0, processOptions(paramArrayOfString), paramInt);
  }

  public static PdfFormField createList(PdfWriter paramPdfWriter, String[][] paramArrayOfString, int paramInt)
  {
    return createChoice(paramPdfWriter, 0, processOptions(paramArrayOfString), paramInt);
  }

  public static PdfFormField createCombo(PdfWriter paramPdfWriter, boolean paramBoolean, String[] paramArrayOfString, int paramInt)
  {
    return createChoice(paramPdfWriter, 131072 + (paramBoolean ? 262144 : 0), processOptions(paramArrayOfString), paramInt);
  }

  public static PdfFormField createCombo(PdfWriter paramPdfWriter, boolean paramBoolean, String[][] paramArrayOfString, int paramInt)
  {
    return createChoice(paramPdfWriter, 131072 + (paramBoolean ? 262144 : 0), processOptions(paramArrayOfString), paramInt);
  }

  protected static PdfArray processOptions(String[] paramArrayOfString)
  {
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfString.length; i++)
      localPdfArray.add(new PdfString(paramArrayOfString[i], "UnicodeBig"));
    return localPdfArray;
  }

  protected static PdfArray processOptions(String[][] paramArrayOfString)
  {
    PdfArray localPdfArray1 = new PdfArray();
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String[] arrayOfString = paramArrayOfString[i];
      PdfArray localPdfArray2 = new PdfArray(new PdfString(arrayOfString[0], "UnicodeBig"));
      localPdfArray2.add(new PdfString(arrayOfString[1], "UnicodeBig"));
      localPdfArray1.add(localPdfArray2);
    }
    return localPdfArray1;
  }

  public static PdfFormField createSignature(PdfWriter paramPdfWriter)
  {
    PdfFormField localPdfFormField = new PdfFormField(paramPdfWriter);
    localPdfFormField.put(PdfName.FT, PdfName.SIG);
    return localPdfFormField;
  }

  public PdfFormField getParent()
  {
    return this.parent;
  }

  public void addKid(PdfFormField paramPdfFormField)
  {
    paramPdfFormField.parent = this;
    if (this.kids == null)
      this.kids = new ArrayList();
    this.kids.add(paramPdfFormField);
  }

  public ArrayList getKids()
  {
    return this.kids;
  }

  public int setFieldFlags(int paramInt)
  {
    PdfNumber localPdfNumber = (PdfNumber)get(PdfName.FF);
    int i;
    if (localPdfNumber == null)
      i = 0;
    else
      i = localPdfNumber.intValue();
    int j = i | paramInt;
    put(PdfName.FF, new PdfNumber(j));
    return i;
  }

  public void setValueAsString(String paramString)
  {
    put(PdfName.V, new PdfString(paramString, "UnicodeBig"));
  }

  public void setValueAsName(String paramString)
  {
    put(PdfName.V, new PdfName(paramString));
  }

  public void setValue(PdfSignature paramPdfSignature)
  {
    put(PdfName.V, paramPdfSignature);
  }

  public void setDefaultValueAsString(String paramString)
  {
    put(PdfName.DV, new PdfString(paramString, "UnicodeBig"));
  }

  public void setDefaultValueAsName(String paramString)
  {
    put(PdfName.DV, new PdfName(paramString));
  }

  public void setFieldName(String paramString)
  {
    if (paramString != null)
      put(PdfName.T, new PdfString(paramString, "UnicodeBig"));
  }

  public void setUserName(String paramString)
  {
    put(PdfName.TU, new PdfString(paramString, "UnicodeBig"));
  }

  public void setMappingName(String paramString)
  {
    put(PdfName.TM, new PdfString(paramString, "UnicodeBig"));
  }

  public void setQuadding(int paramInt)
  {
    put(PdfName.Q, new PdfNumber(paramInt));
  }

  static void mergeResources(PdfDictionary paramPdfDictionary1, PdfDictionary paramPdfDictionary2, PdfStamperImp paramPdfStamperImp)
  {
    Object localObject = null;
    PdfDictionary localPdfDictionary1 = null;
    PdfName localPdfName = null;
    for (int i = 0; i < mergeTarget.length; i++)
    {
      localPdfName = mergeTarget[i];
      PdfDictionary localPdfDictionary2 = paramPdfDictionary2.getAsDict(localPdfName);
      if ((localObject = localPdfDictionary2) == null)
        continue;
      if ((localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObject(paramPdfDictionary1.get(localPdfName), paramPdfDictionary1)) == null)
        localPdfDictionary1 = new PdfDictionary();
      localPdfDictionary1.mergeDifferent(localObject);
      paramPdfDictionary1.put(localPdfName, localPdfDictionary1);
      if (paramPdfStamperImp == null)
        continue;
      paramPdfStamperImp.markUsed(localPdfDictionary1);
    }
  }

  static void mergeResources(PdfDictionary paramPdfDictionary1, PdfDictionary paramPdfDictionary2)
  {
    mergeResources(paramPdfDictionary1, paramPdfDictionary2, null);
  }

  public void setUsed()
  {
    this.used = true;
    if (this.parent != null)
      put(PdfName.PARENT, this.parent.getIndirectReference());
    if (this.kids != null)
    {
      localObject = new PdfArray();
      for (int i = 0; i < this.kids.size(); i++)
        ((PdfArray)localObject).add(((PdfFormField)this.kids.get(i)).getIndirectReference());
      put(PdfName.KIDS, (PdfObject)localObject);
    }
    if (this.templates == null)
      return;
    Object localObject = new PdfDictionary();
    Iterator localIterator = this.templates.keySet().iterator();
    while (localIterator.hasNext())
    {
      PdfTemplate localPdfTemplate = (PdfTemplate)localIterator.next();
      mergeResources((PdfDictionary)localObject, (PdfDictionary)localPdfTemplate.getResources());
    }
    put(PdfName.DR, (PdfObject)localObject);
  }

  public static PdfAnnotation shallowDuplicate(PdfAnnotation paramPdfAnnotation)
  {
    Object localObject;
    if (paramPdfAnnotation.isForm())
    {
      localObject = new PdfFormField(paramPdfAnnotation.writer);
      PdfFormField localPdfFormField1 = (PdfFormField)localObject;
      PdfFormField localPdfFormField2 = (PdfFormField)paramPdfAnnotation;
      localPdfFormField1.parent = localPdfFormField2.parent;
      localPdfFormField1.kids = localPdfFormField2.kids;
    }
    else
    {
      localObject = new PdfAnnotation(paramPdfAnnotation.writer, null);
    }
    ((PdfAnnotation)localObject).merge(paramPdfAnnotation);
    ((PdfAnnotation)localObject).form = paramPdfAnnotation.form;
    ((PdfAnnotation)localObject).annotation = paramPdfAnnotation.annotation;
    ((PdfAnnotation)localObject).templates = paramPdfAnnotation.templates;
    return (PdfAnnotation)localObject;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfFormField
 * JD-Core Version:    0.6.0
 */