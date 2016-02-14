package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.codec.Base64;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.w3c.dom.Node;

public class AcroFields
{
  PdfReader reader;
  PdfWriter writer;
  HashMap fields;
  private int topFirst;
  private HashMap sigNames;
  private boolean append;
  public static final int DA_FONT = 0;
  public static final int DA_SIZE = 1;
  public static final int DA_COLOR = 2;
  private HashMap extensionFonts = new HashMap();
  private XfaForm xfa;
  public static final int FIELD_TYPE_NONE = 0;
  public static final int FIELD_TYPE_PUSHBUTTON = 1;
  public static final int FIELD_TYPE_CHECKBOX = 2;
  public static final int FIELD_TYPE_RADIOBUTTON = 3;
  public static final int FIELD_TYPE_TEXT = 4;
  public static final int FIELD_TYPE_LIST = 5;
  public static final int FIELD_TYPE_COMBO = 6;
  public static final int FIELD_TYPE_SIGNATURE = 7;
  private boolean lastWasString;
  private boolean generateAppearances = true;
  private HashMap localFonts = new HashMap();
  private float extraMarginLeft;
  private float extraMarginTop;
  private ArrayList substitutionFonts;
  private static final HashMap stdFieldFontNames = new HashMap();
  private int totalRevisions;
  private Map fieldCache;
  private static final PdfName[] buttonRemove;

  AcroFields(PdfReader paramPdfReader, PdfWriter paramPdfWriter)
  {
    this.reader = paramPdfReader;
    this.writer = paramPdfWriter;
    try
    {
      this.xfa = new XfaForm(paramPdfReader);
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    if ((paramPdfWriter instanceof PdfStamperImp))
      this.append = ((PdfStamperImp)paramPdfWriter).isAppend();
    fill();
  }

  void fill()
  {
    this.fields = new HashMap();
    PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObjectRelease(this.reader.getCatalog().get(PdfName.ACROFORM));
    if (localPdfDictionary1 == null)
      return;
    PdfArray localPdfArray1 = (PdfArray)PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.FIELDS));
    if ((localPdfArray1 == null) || (localPdfArray1.size() == 0))
      return;
    Object localObject1;
    PdfDictionary localPdfDictionary3;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    for (int i = 1; i <= this.reader.getNumberOfPages(); i++)
    {
      PdfDictionary localPdfDictionary2 = this.reader.getPageNRelease(i);
      localObject1 = (PdfArray)PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.ANNOTS), localPdfDictionary2);
      if (localObject1 == null)
        continue;
      for (int k = 0; k < ((PdfArray)localObject1).size(); k++)
      {
        localPdfDictionary3 = ((PdfArray)localObject1).getAsDict(k);
        if (localPdfDictionary3 == null)
        {
          PdfReader.releaseLastXrefPartial(((PdfArray)localObject1).getAsIndirectObject(k));
        }
        else if (!PdfName.WIDGET.equals(localPdfDictionary3.getAsName(PdfName.SUBTYPE)))
        {
          PdfReader.releaseLastXrefPartial(((PdfArray)localObject1).getAsIndirectObject(k));
        }
        else
        {
          localObject2 = localPdfDictionary3;
          localObject3 = new PdfDictionary();
          ((PdfDictionary)localObject3).putAll(localPdfDictionary3);
          localObject4 = "";
          PdfDictionary localPdfDictionary4 = null;
          PdfObject localPdfObject = null;
          while (localPdfDictionary3 != null)
          {
            ((PdfDictionary)localObject3).mergeDifferent(localPdfDictionary3);
            localObject5 = localPdfDictionary3.getAsString(PdfName.T);
            if (localObject5 != null)
              localObject4 = ((PdfString)localObject5).toUnicodeString() + "." + (String)localObject4;
            if ((localPdfObject == null) && (localPdfDictionary3.get(PdfName.V) != null))
              localPdfObject = PdfReader.getPdfObjectRelease(localPdfDictionary3.get(PdfName.V));
            if ((localPdfDictionary4 == null) && (localObject5 != null))
            {
              localPdfDictionary4 = localPdfDictionary3;
              if ((localPdfDictionary3.get(PdfName.V) == null) && (localPdfObject != null))
                localPdfDictionary4.put(PdfName.V, localPdfObject);
            }
            localPdfDictionary3 = localPdfDictionary3.getAsDict(PdfName.PARENT);
          }
          if (((String)localObject4).length() > 0)
            localObject4 = ((String)localObject4).substring(0, ((String)localObject4).length() - 1);
          Object localObject5 = (Item)this.fields.get(localObject4);
          if (localObject5 == null)
          {
            localObject5 = new Item();
            this.fields.put(localObject4, localObject5);
          }
          if (localPdfDictionary4 == null)
            ((Item)localObject5).addValue((PdfDictionary)localObject2);
          else
            ((Item)localObject5).addValue(localPdfDictionary4);
          ((Item)localObject5).addWidget((PdfDictionary)localObject2);
          ((Item)localObject5).addWidgetRef(((PdfArray)localObject1).getAsIndirectObject(k));
          if (localPdfDictionary1 != null)
            ((PdfDictionary)localObject3).mergeDifferent(localPdfDictionary1);
          ((Item)localObject5).addMerged((PdfDictionary)localObject3);
          ((Item)localObject5).addPage(i);
          ((Item)localObject5).addTabOrder(k);
        }
      }
    }
    PdfNumber localPdfNumber = localPdfDictionary1.getAsNumber(PdfName.SIGFLAGS);
    if ((localPdfNumber == null) || ((localPdfNumber.intValue() & 0x1) != 1))
      return;
    for (int j = 0; j < localPdfArray1.size(); j++)
    {
      localObject1 = localPdfArray1.getAsDict(j);
      if (localObject1 == null)
      {
        PdfReader.releaseLastXrefPartial(localPdfArray1.getAsIndirectObject(j));
      }
      else if (!PdfName.WIDGET.equals(((PdfDictionary)localObject1).getAsName(PdfName.SUBTYPE)))
      {
        PdfReader.releaseLastXrefPartial(localPdfArray1.getAsIndirectObject(j));
      }
      else
      {
        PdfArray localPdfArray2 = (PdfArray)PdfReader.getPdfObjectRelease(((PdfDictionary)localObject1).get(PdfName.KIDS));
        if (localPdfArray2 != null)
          continue;
        localPdfDictionary3 = new PdfDictionary();
        localPdfDictionary3.putAll((PdfDictionary)localObject1);
        localObject2 = ((PdfDictionary)localObject1).getAsString(PdfName.T);
        if (localObject2 == null)
          continue;
        localObject3 = ((PdfString)localObject2).toUnicodeString();
        if (this.fields.containsKey(localObject3))
          continue;
        localObject4 = new Item();
        this.fields.put(localObject3, localObject4);
        ((Item)localObject4).addValue(localPdfDictionary3);
        ((Item)localObject4).addWidget(localPdfDictionary3);
        ((Item)localObject4).addWidgetRef(localPdfArray1.getAsIndirectObject(j));
        ((Item)localObject4).addMerged(localPdfDictionary3);
        ((Item)localObject4).addPage(-1);
        ((Item)localObject4).addTabOrder(-1);
      }
    }
  }

  public String[] getAppearanceStates(String paramString)
  {
    Item localItem = (Item)this.fields.get(paramString);
    if (localItem == null)
      return null;
    HashMap localHashMap = new HashMap();
    PdfDictionary localPdfDictionary1 = localItem.getValue(0);
    PdfString localPdfString = localPdfDictionary1.getAsString(PdfName.OPT);
    Object localObject;
    if (localPdfString != null)
    {
      localHashMap.put(localPdfString.toUnicodeString(), null);
    }
    else
    {
      PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.OPT);
      if (localPdfArray != null)
        for (int j = 0; j < localPdfArray.size(); j++)
        {
          localObject = localPdfArray.getAsString(j);
          if (localObject == null)
            continue;
          localHashMap.put(((PdfString)localObject).toUnicodeString(), null);
        }
    }
    for (int i = 0; i < localItem.size(); i++)
    {
      PdfDictionary localPdfDictionary2 = localItem.getWidget(i);
      localPdfDictionary2 = localPdfDictionary2.getAsDict(PdfName.AP);
      if (localPdfDictionary2 == null)
        continue;
      localPdfDictionary2 = localPdfDictionary2.getAsDict(PdfName.N);
      if (localPdfDictionary2 == null)
        continue;
      localObject = localPdfDictionary2.getKeys().iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = PdfName.decodeName(((PdfName)((Iterator)localObject).next()).toString());
        localHashMap.put(str, null);
      }
    }
    String[] arrayOfString = new String[localHashMap.size()];
    return (String)(String[])localHashMap.keySet().toArray(arrayOfString);
  }

  private String[] getListOption(String paramString, int paramInt)
  {
    Item localItem = getFieldItem(paramString);
    if (localItem == null)
      return null;
    PdfArray localPdfArray = localItem.getMerged(0).getAsArray(PdfName.OPT);
    if (localPdfArray == null)
      return null;
    String[] arrayOfString = new String[localPdfArray.size()];
    for (int i = 0; i < localPdfArray.size(); i++)
    {
      PdfObject localPdfObject = localPdfArray.getDirectObject(i);
      try
      {
        if (localPdfObject.isArray())
          localPdfObject = ((PdfArray)localPdfObject).getDirectObject(paramInt);
        if (localPdfObject.isString())
          arrayOfString[i] = ((PdfString)localPdfObject).toUnicodeString();
        else
          arrayOfString[i] = localPdfObject.toString();
      }
      catch (Exception localException)
      {
        arrayOfString[i] = "";
      }
    }
    return arrayOfString;
  }

  public String[] getListOptionExport(String paramString)
  {
    return getListOption(paramString, 0);
  }

  public String[] getListOptionDisplay(String paramString)
  {
    return getListOption(paramString, 1);
  }

  public boolean setListOption(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if ((paramArrayOfString1 == null) && (paramArrayOfString2 == null))
      return false;
    if ((paramArrayOfString1 != null) && (paramArrayOfString2 != null) && (paramArrayOfString1.length != paramArrayOfString2.length))
      throw new IllegalArgumentException("The export and the display array must have the same size.");
    int i = getFieldType(paramString);
    if ((i != 6) && (i != 5))
      return false;
    Item localItem = (Item)this.fields.get(paramString);
    String[] arrayOfString = null;
    if ((paramArrayOfString1 == null) && (paramArrayOfString2 != null))
      arrayOfString = paramArrayOfString2;
    else if ((paramArrayOfString1 != null) && (paramArrayOfString2 == null))
      arrayOfString = paramArrayOfString1;
    PdfArray localPdfArray1 = new PdfArray();
    if (arrayOfString != null)
      for (j = 0; j < arrayOfString.length; j++)
        localPdfArray1.add(new PdfString(arrayOfString[j], "UnicodeBig"));
    for (int j = 0; j < paramArrayOfString1.length; j++)
    {
      PdfArray localPdfArray2 = new PdfArray();
      localPdfArray2.add(new PdfString(paramArrayOfString1[j], "UnicodeBig"));
      localPdfArray2.add(new PdfString(paramArrayOfString2[j], "UnicodeBig"));
      localPdfArray1.add(localPdfArray2);
    }
    localItem.writeToAll(PdfName.OPT, localPdfArray1, 5);
    return true;
  }

  public int getFieldType(String paramString)
  {
    Item localItem = getFieldItem(paramString);
    if (localItem == null)
      return 0;
    PdfDictionary localPdfDictionary = localItem.getMerged(0);
    PdfName localPdfName = localPdfDictionary.getAsName(PdfName.FT);
    if (localPdfName == null)
      return 0;
    int i = 0;
    PdfNumber localPdfNumber = localPdfDictionary.getAsNumber(PdfName.FF);
    if (localPdfNumber != null)
      i = localPdfNumber.intValue();
    if (PdfName.BTN.equals(localPdfName))
    {
      if ((i & 0x10000) != 0)
        return 1;
      if ((i & 0x8000) != 0)
        return 3;
      return 2;
    }
    if (PdfName.TX.equals(localPdfName))
      return 4;
    if (PdfName.CH.equals(localPdfName))
    {
      if ((i & 0x20000) != 0)
        return 6;
      return 5;
    }
    if (PdfName.SIG.equals(localPdfName))
      return 7;
    return 0;
  }

  public void exportAsFdf(FdfWriter paramFdfWriter)
  {
    Iterator localIterator = this.fields.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Item localItem = (Item)localEntry.getValue();
      String str1 = (String)localEntry.getKey();
      PdfObject localPdfObject = localItem.getMerged(0).get(PdfName.V);
      if (localPdfObject == null)
        continue;
      String str2 = getField(str1);
      if (this.lastWasString)
      {
        paramFdfWriter.setFieldAsString(str1, str2);
        continue;
      }
      paramFdfWriter.setFieldAsName(str1, str2);
    }
  }

  public boolean renameField(String paramString1, String paramString2)
  {
    int i = paramString1.lastIndexOf('.') + 1;
    int j = paramString2.lastIndexOf('.') + 1;
    if (i != j)
      return false;
    if (!paramString1.substring(0, i).equals(paramString2.substring(0, j)))
      return false;
    if (this.fields.containsKey(paramString2))
      return false;
    Item localItem = (Item)this.fields.get(paramString1);
    if (localItem == null)
      return false;
    paramString2 = paramString2.substring(j);
    PdfString localPdfString = new PdfString(paramString2, "UnicodeBig");
    localItem.writeToAll(PdfName.T, localPdfString, 5);
    localItem.markUsed(this, 4);
    this.fields.remove(paramString1);
    this.fields.put(paramString2, localItem);
    return true;
  }

  public static Object[] splitDAelements(String paramString)
  {
    try
    {
      PRTokeniser localPRTokeniser = new PRTokeniser(PdfEncodings.convertToBytes(paramString, null));
      ArrayList localArrayList = new ArrayList();
      Object[] arrayOfObject = new Object[3];
      while (localPRTokeniser.nextToken())
      {
        if (localPRTokeniser.getTokenType() == 4)
          continue;
        if (localPRTokeniser.getTokenType() == 10)
        {
          String str = localPRTokeniser.getStringValue();
          if (str.equals("Tf"))
          {
            if (localArrayList.size() >= 2)
            {
              arrayOfObject[0] = localArrayList.get(localArrayList.size() - 2);
              arrayOfObject[1] = new Float((String)localArrayList.get(localArrayList.size() - 1));
            }
          }
          else
          {
            float f1;
            if (str.equals("g"))
            {
              if (localArrayList.size() >= 1)
              {
                f1 = new Float((String)localArrayList.get(localArrayList.size() - 1)).floatValue();
                if (f1 != 0.0F)
                  arrayOfObject[2] = new GrayColor(f1);
              }
            }
            else
            {
              float f2;
              float f3;
              if (str.equals("rg"))
              {
                if (localArrayList.size() >= 3)
                {
                  f1 = new Float((String)localArrayList.get(localArrayList.size() - 3)).floatValue();
                  f2 = new Float((String)localArrayList.get(localArrayList.size() - 2)).floatValue();
                  f3 = new Float((String)localArrayList.get(localArrayList.size() - 1)).floatValue();
                  arrayOfObject[2] = new Color(f1, f2, f3);
                }
              }
              else if ((str.equals("k")) && (localArrayList.size() >= 4))
              {
                f1 = new Float((String)localArrayList.get(localArrayList.size() - 4)).floatValue();
                f2 = new Float((String)localArrayList.get(localArrayList.size() - 3)).floatValue();
                f3 = new Float((String)localArrayList.get(localArrayList.size() - 2)).floatValue();
                float f4 = new Float((String)localArrayList.get(localArrayList.size() - 1)).floatValue();
                arrayOfObject[2] = new CMYKColor(f1, f2, f3, f4);
              }
            }
          }
          localArrayList.clear();
          continue;
        }
        localArrayList.add(localPRTokeniser.getStringValue());
      }
      return arrayOfObject;
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  public void decodeGenericDictionary(PdfDictionary paramPdfDictionary, BaseField paramBaseField)
    throws IOException, DocumentException
  {
    int i = 0;
    PdfString localPdfString = paramPdfDictionary.getAsString(PdfName.DA);
    Object localObject4;
    Object localObject6;
    if (localPdfString != null)
    {
      localObject1 = splitDAelements(localPdfString.toUnicodeString());
      if (localObject1[1] != null)
        paramBaseField.setFontSize(((Float)localObject1[1]).floatValue());
      if (localObject1[2] != null)
        paramBaseField.setTextColor((Color)localObject1[2]);
      if (localObject1[0] != null)
      {
        localObject2 = paramPdfDictionary.getAsDict(PdfName.DR);
        if (localObject2 != null)
        {
          localObject2 = ((PdfDictionary)localObject2).getAsDict(PdfName.FONT);
          if (localObject2 != null)
          {
            localObject3 = ((PdfDictionary)localObject2).get(new PdfName((String)localObject1[0]));
            Object localObject7;
            if ((localObject3 != null) && (((PdfObject)localObject3).type() == 10))
            {
              localObject4 = (PRIndirectReference)localObject3;
              localObject6 = new DocumentFont((PRIndirectReference)localObject3);
              paramBaseField.setFont((BaseFont)localObject6);
              localObject7 = new Integer(((PRIndirectReference)localObject4).getNumber());
              BaseFont localBaseFont = (BaseFont)this.extensionFonts.get(localObject7);
              if ((localBaseFont == null) && (!this.extensionFonts.containsKey(localObject7)))
              {
                PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObject((PdfObject)localObject3);
                PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.FONTDESCRIPTOR);
                if (localPdfDictionary2 != null)
                {
                  PRStream localPRStream = (PRStream)PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.FONTFILE2));
                  if (localPRStream == null)
                    localPRStream = (PRStream)PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.FONTFILE3));
                  if (localPRStream == null)
                  {
                    this.extensionFonts.put(localObject7, null);
                  }
                  else
                  {
                    try
                    {
                      localBaseFont = BaseFont.createFont("font.ttf", "Identity-H", true, false, PdfReader.getStreamBytes(localPRStream), null);
                    }
                    catch (Exception localException2)
                    {
                    }
                    this.extensionFonts.put(localObject7, localBaseFont);
                  }
                }
              }
              if ((paramBaseField instanceof TextField))
                ((TextField)paramBaseField).setExtensionFont(localBaseFont);
            }
            else
            {
              localObject4 = (BaseFont)this.localFonts.get(localObject1[0]);
              if (localObject4 == null)
              {
                localObject6 = (String[])stdFieldFontNames.get(localObject1[0]);
                if (localObject6 != null)
                  try
                  {
                    localObject7 = "winansi";
                    if (localObject6.length > 1)
                      localObject7 = localObject6[1];
                    localObject4 = BaseFont.createFont(localObject6[0], (String)localObject7, false);
                    paramBaseField.setFont((BaseFont)localObject4);
                  }
                  catch (Exception localException1)
                  {
                  }
              }
              else
              {
                paramBaseField.setFont((BaseFont)localObject4);
              }
            }
          }
        }
      }
    }
    Object localObject1 = paramPdfDictionary.getAsDict(PdfName.MK);
    if (localObject1 != null)
    {
      localObject2 = ((PdfDictionary)localObject1).getAsArray(PdfName.BC);
      localObject3 = getMKColor((PdfArray)localObject2);
      paramBaseField.setBorderColor((Color)localObject3);
      if (localObject3 != null)
        paramBaseField.setBorderWidth(1.0F);
      localObject2 = ((PdfDictionary)localObject1).getAsArray(PdfName.BG);
      paramBaseField.setBackgroundColor(getMKColor((PdfArray)localObject2));
      localObject4 = ((PdfDictionary)localObject1).getAsNumber(PdfName.R);
      if (localObject4 != null)
        paramBaseField.setRotation(((PdfNumber)localObject4).intValue());
    }
    Object localObject2 = paramPdfDictionary.getAsNumber(PdfName.F);
    i = 0;
    paramBaseField.setVisibility(2);
    if (localObject2 != null)
    {
      i = ((PdfNumber)localObject2).intValue();
      if (((i & 0x4) != 0) && ((i & 0x2) != 0))
        paramBaseField.setVisibility(1);
      else if (((i & 0x4) != 0) && ((i & 0x20) != 0))
        paramBaseField.setVisibility(3);
      else if ((i & 0x4) != 0)
        paramBaseField.setVisibility(0);
    }
    localObject2 = paramPdfDictionary.getAsNumber(PdfName.FF);
    i = 0;
    if (localObject2 != null)
      i = ((PdfNumber)localObject2).intValue();
    paramBaseField.setOptions(i);
    if ((i & 0x1000000) != 0)
    {
      localObject3 = paramPdfDictionary.getAsNumber(PdfName.MAXLEN);
      int j = 0;
      if (localObject3 != null)
        j = ((PdfNumber)localObject3).intValue();
      paramBaseField.setMaxCharacterLength(j);
    }
    localObject2 = paramPdfDictionary.getAsNumber(PdfName.Q);
    if (localObject2 != null)
      if (((PdfNumber)localObject2).intValue() == 1)
        paramBaseField.setAlignment(1);
      else if (((PdfNumber)localObject2).intValue() == 2)
        paramBaseField.setAlignment(2);
    Object localObject3 = paramPdfDictionary.getAsDict(PdfName.BS);
    Object localObject5;
    if (localObject3 != null)
    {
      localObject5 = ((PdfDictionary)localObject3).getAsNumber(PdfName.W);
      if (localObject5 != null)
        paramBaseField.setBorderWidth(((PdfNumber)localObject5).floatValue());
      localObject6 = ((PdfDictionary)localObject3).getAsName(PdfName.S);
      if (PdfName.D.equals(localObject6))
        paramBaseField.setBorderStyle(1);
      else if (PdfName.B.equals(localObject6))
        paramBaseField.setBorderStyle(2);
      else if (PdfName.I.equals(localObject6))
        paramBaseField.setBorderStyle(3);
      else if (PdfName.U.equals(localObject6))
        paramBaseField.setBorderStyle(4);
    }
    else
    {
      localObject5 = paramPdfDictionary.getAsArray(PdfName.BORDER);
      if (localObject5 != null)
      {
        if (((PdfArray)localObject5).size() >= 3)
          paramBaseField.setBorderWidth(((PdfArray)localObject5).getAsNumber(2).floatValue());
        if (((PdfArray)localObject5).size() >= 4)
          paramBaseField.setBorderStyle(1);
      }
    }
  }

  PdfAppearance getAppearance(PdfDictionary paramPdfDictionary, String paramString1, String paramString2)
    throws IOException, DocumentException
  {
    this.topFirst = 0;
    TextField localTextField = null;
    if ((this.fieldCache == null) || (!this.fieldCache.containsKey(paramString2)))
    {
      localTextField = new TextField(this.writer, null, null);
      localTextField.setExtraMargin(this.extraMarginLeft, this.extraMarginTop);
      localTextField.setBorderWidth(0.0F);
      localTextField.setSubstitutionFonts(this.substitutionFonts);
      decodeGenericDictionary(paramPdfDictionary, localTextField);
      localObject1 = paramPdfDictionary.getAsArray(PdfName.RECT);
      localObject2 = PdfReader.getNormalizedRectangle((PdfArray)localObject1);
      if ((localTextField.getRotation() == 90) || (localTextField.getRotation() == 270))
        localObject2 = ((Rectangle)localObject2).rotate();
      localTextField.setBox((Rectangle)localObject2);
      if (this.fieldCache != null)
        this.fieldCache.put(paramString2, localTextField);
    }
    else
    {
      localTextField = (TextField)this.fieldCache.get(paramString2);
      localTextField.setWriter(this.writer);
    }
    Object localObject1 = paramPdfDictionary.getAsName(PdfName.FT);
    if (PdfName.TX.equals(localObject1))
    {
      localTextField.setText(paramString1);
      return localTextField.getAppearance();
    }
    if (!PdfName.CH.equals(localObject1))
      throw new DocumentException("An appearance was requested without a variable text field.");
    Object localObject2 = paramPdfDictionary.getAsArray(PdfName.OPT);
    int i = 0;
    PdfNumber localPdfNumber = paramPdfDictionary.getAsNumber(PdfName.FF);
    if (localPdfNumber != null)
      i = localPdfNumber.intValue();
    if (((i & 0x20000) != 0) && (localObject2 == null))
    {
      localTextField.setText(paramString1);
      return localTextField.getAppearance();
    }
    if (localObject2 != null)
    {
      localObject3 = new String[((PdfArray)localObject2).size()];
      String[] arrayOfString = new String[((PdfArray)localObject2).size()];
      for (int j = 0; j < ((PdfArray)localObject2).size(); j++)
      {
        PdfObject localPdfObject = ((PdfArray)localObject2).getPdfObject(j);
        if (localPdfObject.isString())
        {
          String tmp358_355 = ((PdfString)localPdfObject).toUnicodeString();
          arrayOfString[j] = tmp358_355;
          localObject3[j] = tmp358_355;
        }
        else
        {
          PdfArray localPdfArray = (PdfArray)localPdfObject;
          arrayOfString[j] = localPdfArray.getAsString(0).toUnicodeString();
          localObject3[j] = localPdfArray.getAsString(1).toUnicodeString();
        }
      }
      if ((i & 0x20000) != 0)
      {
        for (j = 0; j < localObject3.length; j++)
        {
          if (!paramString1.equals(arrayOfString[j]))
            continue;
          paramString1 = localObject3[j];
          break;
        }
        localTextField.setText(paramString1);
        return localTextField.getAppearance();
      }
      j = 0;
      for (int k = 0; k < arrayOfString.length; k++)
      {
        if (!paramString1.equals(arrayOfString[k]))
          continue;
        j = k;
        break;
      }
      localTextField.setChoices(localObject3);
      localTextField.setChoiceExports(arrayOfString);
      localTextField.setChoiceSelection(j);
    }
    Object localObject3 = localTextField.getListAppearance();
    this.topFirst = localTextField.getTopFirst();
    return (PdfAppearance)(PdfAppearance)(PdfAppearance)localObject3;
  }

  Color getMKColor(PdfArray paramPdfArray)
  {
    if (paramPdfArray == null)
      return null;
    switch (paramPdfArray.size())
    {
    case 1:
      return new GrayColor(paramPdfArray.getAsNumber(0).floatValue());
    case 3:
      return new Color(ExtendedColor.normalize(paramPdfArray.getAsNumber(0).floatValue()), ExtendedColor.normalize(paramPdfArray.getAsNumber(1).floatValue()), ExtendedColor.normalize(paramPdfArray.getAsNumber(2).floatValue()));
    case 4:
      return new CMYKColor(paramPdfArray.getAsNumber(0).floatValue(), paramPdfArray.getAsNumber(1).floatValue(), paramPdfArray.getAsNumber(2).floatValue(), paramPdfArray.getAsNumber(3).floatValue());
    case 2:
    }
    return null;
  }

  public String getField(String paramString)
  {
    if (this.xfa.isXfaPresent())
    {
      paramString = this.xfa.findFieldName(paramString, this);
      if (paramString == null)
        return null;
      paramString = XfaForm.Xml2Som.getShortName(paramString);
      return XfaForm.getNodeText(this.xfa.findDatasetsNode(paramString));
    }
    Item localItem = (Item)this.fields.get(paramString);
    if (localItem == null)
      return null;
    this.lastWasString = false;
    PdfDictionary localPdfDictionary = localItem.getMerged(0);
    PdfObject localPdfObject = PdfReader.getPdfObject(localPdfDictionary.get(PdfName.V));
    if (localPdfObject == null)
      return "";
    if ((localPdfObject instanceof PRStream))
      try
      {
        localObject = PdfReader.getStreamBytes((PRStream)localPdfObject);
        return new String(localObject);
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
    Object localObject = localPdfDictionary.getAsName(PdfName.FT);
    if (PdfName.BTN.equals(localObject))
    {
      PdfNumber localPdfNumber = localPdfDictionary.getAsNumber(PdfName.FF);
      int i = 0;
      if (localPdfNumber != null)
        i = localPdfNumber.intValue();
      if ((i & 0x10000) != 0)
        return "";
      String str = "";
      if ((localPdfObject instanceof PdfName))
        str = PdfName.decodeName(localPdfObject.toString());
      else if ((localPdfObject instanceof PdfString))
        str = ((PdfString)localPdfObject).toUnicodeString();
      PdfArray localPdfArray = localItem.getValue(0).getAsArray(PdfName.OPT);
      if (localPdfArray != null)
      {
        int j = 0;
        try
        {
          j = Integer.parseInt(str);
          PdfString localPdfString = localPdfArray.getAsString(j);
          str = localPdfString.toUnicodeString();
          this.lastWasString = true;
        }
        catch (Exception localException)
        {
        }
      }
      return str;
    }
    if ((localPdfObject instanceof PdfString))
    {
      this.lastWasString = true;
      return ((PdfString)localPdfObject).toUnicodeString();
    }
    if ((localPdfObject instanceof PdfName))
      return PdfName.decodeName(localPdfObject.toString());
    return (String)"";
  }

  public String[] getListSelection(String paramString)
  {
    String str = getField(paramString);
    if (str == null)
      arrayOfString1 = new String[0];
    else
      arrayOfString1 = new String[] { str };
    Item localItem = (Item)this.fields.get(paramString);
    if (localItem == null)
      return arrayOfString1;
    PdfArray localPdfArray = localItem.getMerged(0).getAsArray(PdfName.I);
    if (localPdfArray == null)
      return arrayOfString1;
    String[] arrayOfString1 = new String[localPdfArray.size()];
    String[] arrayOfString2 = getListOptionExport(paramString);
    int i = 0;
    ListIterator localListIterator = localPdfArray.listIterator();
    while (localListIterator.hasNext())
    {
      PdfNumber localPdfNumber = (PdfNumber)localListIterator.next();
      arrayOfString1[(i++)] = arrayOfString2[localPdfNumber.intValue()];
    }
    return arrayOfString1;
  }

  public boolean setFieldProperty(String paramString1, String paramString2, Object paramObject, int[] paramArrayOfInt)
  {
    if (this.writer == null)
      throw new RuntimeException("This AcroFields instance is read-only.");
    try
    {
      Item localItem = (Item)this.fields.get(paramString1);
      if (localItem == null)
        return false;
      InstHit localInstHit = new InstHit(paramArrayOfInt);
      int i;
      PdfDictionary localPdfDictionary1;
      PdfString localPdfString1;
      Object localObject1;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      if (paramString2.equalsIgnoreCase("textfont"))
        for (i = 0; i < localItem.size(); i++)
        {
          if (!localInstHit.isHit(i))
            continue;
          localPdfDictionary1 = localItem.getMerged(i);
          localPdfString1 = localPdfDictionary1.getAsString(PdfName.DA);
          localObject1 = localPdfDictionary1.getAsDict(PdfName.DR);
          if ((localPdfString1 == null) || (localObject1 == null))
            continue;
          localObject2 = splitDAelements(localPdfString1.toUnicodeString());
          localObject3 = new PdfAppearance();
          if (localObject2[0] == null)
            continue;
          localObject4 = (BaseFont)paramObject;
          PdfName localPdfName2 = (PdfName)PdfAppearance.stdFieldFontNames.get(((BaseFont)localObject4).getPostscriptFontName());
          if (localPdfName2 == null)
            localPdfName2 = new PdfName(((BaseFont)localObject4).getPostscriptFontName());
          PdfDictionary localPdfDictionary2 = ((PdfDictionary)localObject1).getAsDict(PdfName.FONT);
          if (localPdfDictionary2 == null)
          {
            localPdfDictionary2 = new PdfDictionary();
            ((PdfDictionary)localObject1).put(PdfName.FONT, localPdfDictionary2);
          }
          PdfIndirectReference localPdfIndirectReference1 = (PdfIndirectReference)localPdfDictionary2.get(localPdfName2);
          PdfDictionary localPdfDictionary3 = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
          markUsed(localPdfDictionary3);
          localObject1 = localPdfDictionary3.getAsDict(PdfName.DR);
          if (localObject1 == null)
          {
            localObject1 = new PdfDictionary();
            localPdfDictionary3.put(PdfName.DR, (PdfObject)localObject1);
          }
          markUsed((PdfObject)localObject1);
          PdfDictionary localPdfDictionary4 = ((PdfDictionary)localObject1).getAsDict(PdfName.FONT);
          if (localPdfDictionary4 == null)
          {
            localPdfDictionary4 = new PdfDictionary();
            ((PdfDictionary)localObject1).put(PdfName.FONT, localPdfDictionary4);
          }
          markUsed(localPdfDictionary4);
          PdfIndirectReference localPdfIndirectReference2 = (PdfIndirectReference)localPdfDictionary4.get(localPdfName2);
          if (localPdfIndirectReference2 != null)
          {
            if (localPdfIndirectReference1 == null)
              localPdfDictionary2.put(localPdfName2, localPdfIndirectReference2);
          }
          else if (localPdfIndirectReference1 == null)
          {
            if (((BaseFont)localObject4).getFontType() == 4)
            {
              localObject5 = new FontDetails(null, ((DocumentFont)localObject4).getIndirectReference(), (BaseFont)localObject4);
            }
            else
            {
              ((BaseFont)localObject4).setSubset(false);
              localObject5 = this.writer.addSimple((BaseFont)localObject4);
              this.localFonts.put(localPdfName2.toString().substring(1), localObject4);
            }
            localPdfDictionary4.put(localPdfName2, ((FontDetails)localObject5).getIndirectReference());
            localPdfDictionary2.put(localPdfName2, ((FontDetails)localObject5).getIndirectReference());
          }
          Object localObject5 = ((PdfAppearance)localObject3).getInternalBuffer();
          ((ByteBuffer)localObject5).append(localPdfName2.getBytes()).append(' ').append(((Float)localObject2[1]).floatValue()).append(" Tf ");
          if (localObject2[2] != null)
            ((PdfAppearance)localObject3).setColorFill((Color)localObject2[2]);
          PdfString localPdfString2 = new PdfString(((PdfAppearance)localObject3).toString());
          localItem.getMerged(i).put(PdfName.DA, localPdfString2);
          localItem.getWidget(i).put(PdfName.DA, localPdfString2);
          markUsed(localItem.getWidget(i));
        }
      if (paramString2.equalsIgnoreCase("textcolor"))
        for (i = 0; i < localItem.size(); i++)
        {
          if (!localInstHit.isHit(i))
            continue;
          localPdfDictionary1 = localItem.getMerged(i);
          localPdfString1 = localPdfDictionary1.getAsString(PdfName.DA);
          if (localPdfString1 == null)
            continue;
          localObject1 = splitDAelements(localPdfString1.toUnicodeString());
          localObject2 = new PdfAppearance();
          if (localObject1[0] == null)
            continue;
          localObject3 = ((PdfAppearance)localObject2).getInternalBuffer();
          ((ByteBuffer)localObject3).append(new PdfName((String)localObject1[0]).getBytes()).append(' ').append(((Float)localObject1[1]).floatValue()).append(" Tf ");
          ((PdfAppearance)localObject2).setColorFill((Color)paramObject);
          localObject4 = new PdfString(((PdfAppearance)localObject2).toString());
          localItem.getMerged(i).put(PdfName.DA, (PdfObject)localObject4);
          localItem.getWidget(i).put(PdfName.DA, (PdfObject)localObject4);
          markUsed(localItem.getWidget(i));
        }
      if (paramString2.equalsIgnoreCase("textsize"))
        for (i = 0; i < localItem.size(); i++)
        {
          if (!localInstHit.isHit(i))
            continue;
          localPdfDictionary1 = localItem.getMerged(i);
          localPdfString1 = localPdfDictionary1.getAsString(PdfName.DA);
          if (localPdfString1 == null)
            continue;
          localObject1 = splitDAelements(localPdfString1.toUnicodeString());
          localObject2 = new PdfAppearance();
          if (localObject1[0] == null)
            continue;
          localObject3 = ((PdfAppearance)localObject2).getInternalBuffer();
          ((ByteBuffer)localObject3).append(new PdfName((String)localObject1[0]).getBytes()).append(' ').append(((Float)paramObject).floatValue()).append(" Tf ");
          if (localObject1[2] != null)
            ((PdfAppearance)localObject2).setColorFill((Color)localObject1[2]);
          localObject4 = new PdfString(((PdfAppearance)localObject2).toString());
          localItem.getMerged(i).put(PdfName.DA, (PdfObject)localObject4);
          localItem.getWidget(i).put(PdfName.DA, (PdfObject)localObject4);
          markUsed(localItem.getWidget(i));
        }
      PdfName localPdfName1;
      int j;
      if ((paramString2.equalsIgnoreCase("bgcolor")) || (paramString2.equalsIgnoreCase("bordercolor")))
      {
        localPdfName1 = paramString2.equalsIgnoreCase("bgcolor") ? PdfName.BG : PdfName.BC;
        j = 0;
      }
      while (j < localItem.size())
      {
        if (localInstHit.isHit(j))
        {
          localPdfDictionary1 = localItem.getMerged(j);
          localObject2 = localPdfDictionary1.getAsDict(PdfName.MK);
          if (localObject2 == null)
          {
            if (paramObject == null)
              return true;
            localObject2 = new PdfDictionary();
            localItem.getMerged(j).put(PdfName.MK, (PdfObject)localObject2);
            localItem.getWidget(j).put(PdfName.MK, (PdfObject)localObject2);
            markUsed(localItem.getWidget(j));
          }
          else
          {
            markUsed((PdfObject)localObject2);
          }
          if (paramObject == null)
            ((PdfDictionary)localObject2).remove(localPdfName1);
          else
            ((PdfDictionary)localObject2).put(localPdfName1, PdfFormField.getMKColor((Color)paramObject));
        }
        j++;
        continue;
        return false;
      }
      return true;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public boolean setFieldProperty(String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt)
  {
    if (this.writer == null)
      throw new RuntimeException("This AcroFields instance is read-only.");
    Item localItem = (Item)this.fields.get(paramString1);
    if (localItem == null)
      return false;
    InstHit localInstHit = new InstHit(paramArrayOfInt);
    if (paramString2.equalsIgnoreCase("flags"))
    {
      PdfNumber localPdfNumber1 = new PdfNumber(paramInt);
      for (int k = 0; k < localItem.size(); k++)
      {
        if (!localInstHit.isHit(k))
          continue;
        localItem.getMerged(k).put(PdfName.F, localPdfNumber1);
        localItem.getWidget(k).put(PdfName.F, localPdfNumber1);
        markUsed(localItem.getWidget(k));
      }
    }
    int i;
    Object localObject;
    if (paramString2.equalsIgnoreCase("setflags"))
      for (i = 0; i < localItem.size(); i++)
      {
        if (!localInstHit.isHit(i))
          continue;
        localObject = localItem.getWidget(i).getAsNumber(PdfName.F);
        int n = 0;
        if (localObject != null)
          n = ((PdfNumber)localObject).intValue();
        localObject = new PdfNumber(n | paramInt);
        localItem.getMerged(i).put(PdfName.F, (PdfObject)localObject);
        localItem.getWidget(i).put(PdfName.F, (PdfObject)localObject);
        markUsed(localItem.getWidget(i));
      }
    PdfNumber localPdfNumber3;
    int i1;
    if (paramString2.equalsIgnoreCase("clrflags"))
      for (i = 0; i < localItem.size(); i++)
      {
        if (!localInstHit.isHit(i))
          continue;
        localObject = localItem.getWidget(i);
        localPdfNumber3 = ((PdfDictionary)localObject).getAsNumber(PdfName.F);
        i1 = 0;
        if (localPdfNumber3 != null)
          i1 = localPdfNumber3.intValue();
        localPdfNumber3 = new PdfNumber(i1 & (paramInt ^ 0xFFFFFFFF));
        localItem.getMerged(i).put(PdfName.F, localPdfNumber3);
        ((PdfDictionary)localObject).put(PdfName.F, localPdfNumber3);
        markUsed((PdfObject)localObject);
      }
    if (paramString2.equalsIgnoreCase("fflags"))
    {
      PdfNumber localPdfNumber2 = new PdfNumber(paramInt);
      for (int m = 0; m < localItem.size(); m++)
      {
        if (!localInstHit.isHit(m))
          continue;
        localItem.getMerged(m).put(PdfName.FF, localPdfNumber2);
        localItem.getValue(m).put(PdfName.FF, localPdfNumber2);
        markUsed(localItem.getValue(m));
      }
    }
    int j;
    PdfDictionary localPdfDictionary;
    if (paramString2.equalsIgnoreCase("setfflags"))
      for (j = 0; j < localItem.size(); j++)
      {
        if (!localInstHit.isHit(j))
          continue;
        localPdfDictionary = localItem.getValue(j);
        localPdfNumber3 = localPdfDictionary.getAsNumber(PdfName.FF);
        i1 = 0;
        if (localPdfNumber3 != null)
          i1 = localPdfNumber3.intValue();
        localPdfNumber3 = new PdfNumber(i1 | paramInt);
        localItem.getMerged(j).put(PdfName.FF, localPdfNumber3);
        localPdfDictionary.put(PdfName.FF, localPdfNumber3);
        markUsed(localPdfDictionary);
      }
    if (paramString2.equalsIgnoreCase("clrfflags"))
      for (j = 0; j < localItem.size(); j++)
      {
        if (!localInstHit.isHit(j))
          continue;
        localPdfDictionary = localItem.getValue(j);
        localPdfNumber3 = localPdfDictionary.getAsNumber(PdfName.FF);
        i1 = 0;
        if (localPdfNumber3 != null)
          i1 = localPdfNumber3.intValue();
        localPdfNumber3 = new PdfNumber(i1 & (paramInt ^ 0xFFFFFFFF));
        localItem.getMerged(j).put(PdfName.FF, localPdfNumber3);
        localPdfDictionary.put(PdfName.FF, localPdfNumber3);
        markUsed(localPdfDictionary);
      }
    return false;
    return true;
  }

  public void mergeXfaData(Node paramNode)
    throws IOException, DocumentException
  {
    XfaForm.Xml2SomDatasets localXml2SomDatasets = new XfaForm.Xml2SomDatasets(paramNode);
    Iterator localIterator = localXml2SomDatasets.getOrder().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = XfaForm.getNodeText((Node)localXml2SomDatasets.getName2Node().get(str1));
      setField(str1, str2);
    }
  }

  public void setFields(FdfReader paramFdfReader)
    throws IOException, DocumentException
  {
    HashMap localHashMap = paramFdfReader.getFields();
    Iterator localIterator = localHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = paramFdfReader.getFieldValue(str1);
      if (str2 == null)
        continue;
      setField(str1, str2);
    }
  }

  public void setFields(XfdfReader paramXfdfReader)
    throws IOException, DocumentException
  {
    HashMap localHashMap = paramXfdfReader.getFields();
    Iterator localIterator = localHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = paramXfdfReader.getFieldValue(str1);
      if (str2 != null)
        setField(str1, str2);
      List localList = paramXfdfReader.getListValues(str1);
      if (localList == null)
        continue;
      setListSelection(str2, (String[])localList.toArray(new String[localList.size()]));
    }
  }

  public boolean regenerateField(String paramString)
    throws IOException, DocumentException
  {
    String str = getField(paramString);
    return setField(paramString, str, str);
  }

  public boolean setField(String paramString1, String paramString2)
    throws IOException, DocumentException
  {
    return setField(paramString1, paramString2, null);
  }

  public boolean setField(String paramString1, String paramString2, String paramString3)
    throws IOException, DocumentException
  {
    if (this.writer == null)
      throw new DocumentException("This AcroFields instance is read-only.");
    if (this.xfa.isXfaPresent())
    {
      paramString1 = this.xfa.findFieldName(paramString1, this);
      if (paramString1 == null)
        return false;
      localObject1 = XfaForm.Xml2Som.getShortName(paramString1);
      localObject2 = this.xfa.findDatasetsNode((String)localObject1);
      if (localObject2 == null)
        localObject2 = this.xfa.getDatasetsSom().insertNode(this.xfa.getDatasetsNode(), (String)localObject1);
      this.xfa.setNodeText((Node)localObject2, paramString2);
    }
    Object localObject1 = (Item)this.fields.get(paramString1);
    if (localObject1 == null)
      return false;
    Object localObject2 = ((Item)localObject1).getMerged(0);
    PdfName localPdfName = ((PdfDictionary)localObject2).getAsName(PdfName.FT);
    Object localObject3;
    int i;
    if (PdfName.TX.equals(localPdfName))
    {
      localObject3 = ((PdfDictionary)localObject2).getAsNumber(PdfName.MAXLEN);
      i = 0;
      if (localObject3 != null)
        i = ((PdfNumber)localObject3).intValue();
      if (i > 0)
        paramString2 = paramString2.substring(0, Math.min(i, paramString2.length()));
    }
    if (paramString3 == null)
      paramString3 = paramString2;
    Object localObject4;
    Object localObject6;
    if ((PdfName.TX.equals(localPdfName)) || (PdfName.CH.equals(localPdfName)))
    {
      localObject3 = new PdfString(paramString2, "UnicodeBig");
      for (i = 0; i < ((Item)localObject1).size(); i++)
      {
        localObject4 = ((Item)localObject1).getValue(i);
        ((PdfDictionary)localObject4).put(PdfName.V, (PdfObject)localObject3);
        ((PdfDictionary)localObject4).remove(PdfName.I);
        markUsed((PdfObject)localObject4);
        localObject2 = ((Item)localObject1).getMerged(i);
        ((PdfDictionary)localObject2).remove(PdfName.I);
        ((PdfDictionary)localObject2).put(PdfName.V, (PdfObject)localObject3);
        PdfDictionary localPdfDictionary1 = ((Item)localObject1).getWidget(i);
        if (this.generateAppearances)
        {
          localObject6 = getAppearance((PdfDictionary)localObject2, paramString3, paramString1);
          if (PdfName.CH.equals(localPdfName))
          {
            localObject7 = new PdfNumber(this.topFirst);
            localPdfDictionary1.put(PdfName.TI, (PdfObject)localObject7);
            ((PdfDictionary)localObject2).put(PdfName.TI, (PdfObject)localObject7);
          }
          Object localObject7 = localPdfDictionary1.getAsDict(PdfName.AP);
          if (localObject7 == null)
          {
            localObject7 = new PdfDictionary();
            localPdfDictionary1.put(PdfName.AP, (PdfObject)localObject7);
            ((PdfDictionary)localObject2).put(PdfName.AP, (PdfObject)localObject7);
          }
          ((PdfDictionary)localObject7).put(PdfName.N, ((PdfAppearance)localObject6).getIndirectReference());
          this.writer.releaseTemplate((PdfTemplate)localObject6);
        }
        else
        {
          localPdfDictionary1.remove(PdfName.AP);
          ((PdfDictionary)localObject2).remove(PdfName.AP);
        }
        markUsed(localPdfDictionary1);
      }
      return true;
    }
    if (PdfName.BTN.equals(localPdfName))
    {
      localObject3 = ((Item)localObject1).getMerged(0).getAsNumber(PdfName.FF);
      i = 0;
      if (localObject3 != null)
        i = ((PdfNumber)localObject3).intValue();
      if ((i & 0x10000) != 0)
      {
        try
        {
          localObject4 = Image.getInstance(Base64.decode(paramString2));
        }
        catch (Exception localException)
        {
          return false;
        }
        localObject5 = getNewPushbuttonFromField(paramString1);
        ((PushbuttonField)localObject5).setImage((Image)localObject4);
        replacePushbuttonField(paramString1, ((PushbuttonField)localObject5).getField());
        return true;
      }
      localObject4 = new PdfName(paramString2);
      Object localObject5 = new ArrayList();
      localObject6 = ((Item)localObject1).getValue(0).getAsArray(PdfName.OPT);
      if (localObject6 != null)
        for (j = 0; j < ((PdfArray)localObject6).size(); j++)
        {
          localObject8 = ((PdfArray)localObject6).getAsString(j);
          if (localObject8 != null)
            ((ArrayList)localObject5).add(((PdfString)localObject8).toUnicodeString());
          else
            ((ArrayList)localObject5).add(null);
        }
      int j = ((ArrayList)localObject5).indexOf(paramString2);
      Object localObject8 = null;
      Object localObject9;
      if (j >= 0)
        localObject9 = localObject8 = new PdfName(String.valueOf(j));
      else
        localObject9 = localObject4;
      for (int k = 0; k < ((Item)localObject1).size(); k++)
      {
        localObject2 = ((Item)localObject1).getMerged(k);
        PdfDictionary localPdfDictionary2 = ((Item)localObject1).getWidget(k);
        PdfDictionary localPdfDictionary3 = ((Item)localObject1).getValue(k);
        markUsed(((Item)localObject1).getValue(k));
        if (localObject8 != null)
        {
          PdfString localPdfString = new PdfString(paramString2, "UnicodeBig");
          localPdfDictionary3.put(PdfName.V, localPdfString);
          ((PdfDictionary)localObject2).put(PdfName.V, localPdfString);
        }
        else
        {
          localPdfDictionary3.put(PdfName.V, (PdfObject)localObject4);
          ((PdfDictionary)localObject2).put(PdfName.V, (PdfObject)localObject4);
        }
        markUsed(localPdfDictionary2);
        if (isInAP(localPdfDictionary2, (PdfName)localObject9))
        {
          ((PdfDictionary)localObject2).put(PdfName.AS, (PdfObject)localObject9);
          localPdfDictionary2.put(PdfName.AS, (PdfObject)localObject9);
        }
        else
        {
          ((PdfDictionary)localObject2).put(PdfName.AS, PdfName.Off);
          localPdfDictionary2.put(PdfName.AS, PdfName.Off);
        }
      }
      return true;
    }
    return false;
  }

  public boolean setListSelection(String paramString, String[] paramArrayOfString)
    throws IOException, DocumentException
  {
    Item localItem = getFieldItem(paramString);
    if (localItem == null)
      return false;
    PdfName localPdfName = localItem.getMerged(0).getAsName(PdfName.FT);
    if (!PdfName.CH.equals(localPdfName))
      return false;
    String[] arrayOfString = getListOptionExport(paramString);
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfString.length; i++)
      for (int j = 0; j < arrayOfString.length; j++)
      {
        if (!arrayOfString[j].equals(paramArrayOfString[i]))
          continue;
        localPdfArray.add(new PdfNumber(j));
      }
    localItem.writeToAll(PdfName.I, localPdfArray, 5);
    localItem.writeToAll(PdfName.V, null, 5);
    localItem.writeToAll(PdfName.AP, null, 3);
    localItem.markUsed(this, 6);
    return true;
  }

  boolean isInAP(PdfDictionary paramPdfDictionary, PdfName paramPdfName)
  {
    PdfDictionary localPdfDictionary1 = paramPdfDictionary.getAsDict(PdfName.AP);
    if (localPdfDictionary1 == null)
      return false;
    PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.N);
    return (localPdfDictionary2 != null) && (localPdfDictionary2.get(paramPdfName) != null);
  }

  public HashMap getFields()
  {
    return this.fields;
  }

  public Item getFieldItem(String paramString)
  {
    if (this.xfa.isXfaPresent())
    {
      paramString = this.xfa.findFieldName(paramString, this);
      if (paramString == null)
        return null;
    }
    return (Item)this.fields.get(paramString);
  }

  public String getTranslatedFieldName(String paramString)
  {
    if (this.xfa.isXfaPresent())
    {
      String str = this.xfa.findFieldName(paramString, this);
      if (str != null)
        paramString = str;
    }
    return paramString;
  }

  public float[] getFieldPositions(String paramString)
  {
    Item localItem = getFieldItem(paramString);
    if (localItem == null)
      return null;
    float[] arrayOfFloat1 = new float[localItem.size() * 5];
    int i = 0;
    for (int j = 0; j < localItem.size(); j++)
      try
      {
        PdfDictionary localPdfDictionary = localItem.getWidget(j);
        PdfArray localPdfArray = localPdfDictionary.getAsArray(PdfName.RECT);
        if (localPdfArray == null)
          continue;
        Rectangle localRectangle1 = PdfReader.getNormalizedRectangle(localPdfArray);
        int k = localItem.getPage(j).intValue();
        int m = this.reader.getPageRotation(k);
        arrayOfFloat1[(i++)] = k;
        if (m != 0)
        {
          Rectangle localRectangle2 = this.reader.getPageSize(k);
          switch (m)
          {
          case 270:
            localRectangle1 = new Rectangle(localRectangle2.getTop() - localRectangle1.getBottom(), localRectangle1.getLeft(), localRectangle2.getTop() - localRectangle1.getTop(), localRectangle1.getRight());
            break;
          case 180:
            localRectangle1 = new Rectangle(localRectangle2.getRight() - localRectangle1.getLeft(), localRectangle2.getTop() - localRectangle1.getBottom(), localRectangle2.getRight() - localRectangle1.getRight(), localRectangle2.getTop() - localRectangle1.getTop());
            break;
          case 90:
            localRectangle1 = new Rectangle(localRectangle1.getBottom(), localRectangle2.getRight() - localRectangle1.getLeft(), localRectangle1.getTop(), localRectangle2.getRight() - localRectangle1.getRight());
          }
          localRectangle1.normalize();
        }
        arrayOfFloat1[(i++)] = localRectangle1.getLeft();
        arrayOfFloat1[(i++)] = localRectangle1.getBottom();
        arrayOfFloat1[(i++)] = localRectangle1.getRight();
        arrayOfFloat1[(i++)] = localRectangle1.getTop();
      }
      catch (Exception localException)
      {
      }
    if (i < arrayOfFloat1.length)
    {
      float[] arrayOfFloat2 = new float[i];
      System.arraycopy(arrayOfFloat1, 0, arrayOfFloat2, 0, i);
      return arrayOfFloat2;
    }
    return arrayOfFloat1;
  }

  private int removeRefFromArray(PdfArray paramPdfArray, PdfObject paramPdfObject)
  {
    if ((paramPdfObject == null) || (!paramPdfObject.isIndirect()))
      return paramPdfArray.size();
    PdfIndirectReference localPdfIndirectReference = (PdfIndirectReference)paramPdfObject;
    for (int i = 0; i < paramPdfArray.size(); i++)
    {
      PdfObject localPdfObject = paramPdfArray.getPdfObject(i);
      if ((!localPdfObject.isIndirect()) || (((PdfIndirectReference)localPdfObject).getNumber() != localPdfIndirectReference.getNumber()))
        continue;
      paramPdfArray.remove(i--);
    }
    return paramPdfArray.size();
  }

  public boolean removeFieldsFromPage(int paramInt)
  {
    if (paramInt < 1)
      return false;
    String[] arrayOfString = new String[this.fields.size()];
    this.fields.keySet().toArray(arrayOfString);
    int i = 0;
    for (int j = 0; j < arrayOfString.length; j++)
    {
      boolean bool = removeField(arrayOfString[j], paramInt);
      i = (i != 0) || (bool) ? 1 : 0;
    }
    return i;
  }

  public boolean removeField(String paramString, int paramInt)
  {
    Item localItem = getFieldItem(paramString);
    if (localItem == null)
      return false;
    PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM), this.reader.getCatalog());
    if (localPdfDictionary1 == null)
      return false;
    PdfArray localPdfArray1 = localPdfDictionary1.getAsArray(PdfName.FIELDS);
    if (localPdfArray1 == null)
      return false;
    for (int i = 0; i < localItem.size(); i++)
    {
      int j = localItem.getPage(i).intValue();
      if ((paramInt != -1) && (paramInt != j))
        continue;
      PdfIndirectReference localPdfIndirectReference1 = localItem.getWidgetRef(i);
      PdfDictionary localPdfDictionary2 = localItem.getWidget(i);
      PdfDictionary localPdfDictionary3 = this.reader.getPageN(j);
      PdfArray localPdfArray2 = localPdfDictionary3.getAsArray(PdfName.ANNOTS);
      if (localPdfArray2 != null)
        if (removeRefFromArray(localPdfArray2, localPdfIndirectReference1) == 0)
        {
          localPdfDictionary3.remove(PdfName.ANNOTS);
          markUsed(localPdfDictionary3);
        }
        else
        {
          markUsed(localPdfArray2);
        }
      PdfReader.killIndirect(localPdfIndirectReference1);
      PdfIndirectReference localPdfIndirectReference2 = localPdfIndirectReference1;
      while ((localPdfIndirectReference1 = localPdfDictionary2.getAsIndirectObject(PdfName.PARENT)) != null)
      {
        localPdfDictionary2 = localPdfDictionary2.getAsDict(PdfName.PARENT);
        PdfArray localPdfArray3 = localPdfDictionary2.getAsArray(PdfName.KIDS);
        if (removeRefFromArray(localPdfArray3, localPdfIndirectReference2) != 0)
          break;
        localPdfIndirectReference2 = localPdfIndirectReference1;
        PdfReader.killIndirect(localPdfIndirectReference1);
      }
      if (localPdfIndirectReference1 == null)
      {
        removeRefFromArray(localPdfArray1, localPdfIndirectReference2);
        markUsed(localPdfArray1);
      }
      if (paramInt == -1)
        continue;
      localItem.remove(i);
      i--;
    }
    if ((paramInt == -1) || (localItem.size() == 0))
      this.fields.remove(paramString);
    return true;
  }

  public boolean removeField(String paramString)
  {
    return removeField(paramString, -1);
  }

  public boolean isGenerateAppearances()
  {
    return this.generateAppearances;
  }

  public void setGenerateAppearances(boolean paramBoolean)
  {
    this.generateAppearances = paramBoolean;
    PdfDictionary localPdfDictionary = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
    if (paramBoolean)
      localPdfDictionary.remove(PdfName.NEEDAPPEARANCES);
    else
      localPdfDictionary.put(PdfName.NEEDAPPEARANCES, PdfBoolean.PDFTRUE);
  }

  public ArrayList getSignatureNames()
  {
    if (this.sigNames != null)
      return new ArrayList(this.sigNames.keySet());
    this.sigNames = new HashMap();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.fields.entrySet().iterator();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      localObject2 = (Item)((Map.Entry)localObject1).getValue();
      localObject3 = ((Item)localObject2).getMerged(0);
      if (!PdfName.SIG.equals(((PdfDictionary)localObject3).get(PdfName.FT)))
        continue;
      PdfDictionary localPdfDictionary = ((PdfDictionary)localObject3).getAsDict(PdfName.V);
      if (localPdfDictionary == null)
        continue;
      PdfString localPdfString = localPdfDictionary.getAsString(PdfName.CONTENTS);
      if (localPdfString == null)
        continue;
      PdfArray localPdfArray = localPdfDictionary.getAsArray(PdfName.BYTERANGE);
      if (localPdfArray == null)
        continue;
      int j = localPdfArray.size();
      if (j < 2)
        continue;
      int k = localPdfArray.getAsNumber(j - 1).intValue() + localPdfArray.getAsNumber(j - 2).intValue();
      localArrayList.add(new Object[] { ((Map.Entry)localObject1).getKey(), { k, 0 } });
    }
    Collections.sort(localArrayList, new SorterComparator(null));
    if (!localArrayList.isEmpty())
    {
      if (((int[])((Object[])localArrayList.get(localArrayList.size() - 1))[1])[0] == this.reader.getFileLength())
        this.totalRevisions = localArrayList.size();
      else
        this.totalRevisions = (localArrayList.size() + 1);
      for (int i = 0; i < localArrayList.size(); i++)
      {
        localObject1 = (Object[])localArrayList.get(i);
        localObject2 = (String)localObject1[0];
        localObject3 = (int[])localObject1[1];
        localObject3[1] = (i + 1);
        this.sigNames.put(localObject2, localObject3);
      }
    }
    return (ArrayList)(ArrayList)(ArrayList)new ArrayList(this.sigNames.keySet());
  }

  public ArrayList getBlankSignatureNames()
  {
    getSignatureNames();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.fields.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Item localItem = (Item)localEntry.getValue();
      PdfDictionary localPdfDictionary = localItem.getMerged(0);
      if ((!PdfName.SIG.equals(localPdfDictionary.getAsName(PdfName.FT))) || (this.sigNames.containsKey(localEntry.getKey())))
        continue;
      localArrayList.add(localEntry.getKey());
    }
    return localArrayList;
  }

  public PdfDictionary getSignatureDictionary(String paramString)
  {
    getSignatureNames();
    paramString = getTranslatedFieldName(paramString);
    if (!this.sigNames.containsKey(paramString))
      return null;
    Item localItem = (Item)this.fields.get(paramString);
    PdfDictionary localPdfDictionary = localItem.getMerged(0);
    return localPdfDictionary.getAsDict(PdfName.V);
  }

  public boolean signatureCoversWholeDocument(String paramString)
  {
    getSignatureNames();
    paramString = getTranslatedFieldName(paramString);
    if (!this.sigNames.containsKey(paramString))
      return false;
    return ((int[])this.sigNames.get(paramString))[0] == this.reader.getFileLength();
  }

  public PdfPKCS7 verifySignature(String paramString)
  {
    return verifySignature(paramString, null);
  }

  public PdfPKCS7 verifySignature(String paramString1, String paramString2)
  {
    PdfDictionary localPdfDictionary = getSignatureDictionary(paramString1);
    if (localPdfDictionary == null)
      return null;
    try
    {
      PdfName localPdfName = localPdfDictionary.getAsName(PdfName.SUBFILTER);
      PdfString localPdfString1 = localPdfDictionary.getAsString(PdfName.CONTENTS);
      PdfPKCS7 localPdfPKCS7 = null;
      if (localPdfName.equals(PdfName.ADBE_X509_RSA_SHA1))
      {
        localPdfString2 = localPdfDictionary.getAsString(PdfName.CERT);
        localPdfPKCS7 = new PdfPKCS7(localPdfString1.getOriginalBytes(), localPdfString2.getBytes(), paramString2);
      }
      else
      {
        localPdfPKCS7 = new PdfPKCS7(localPdfString1.getOriginalBytes(), paramString2);
      }
      updateByteRange(localPdfPKCS7, localPdfDictionary);
      PdfString localPdfString2 = localPdfDictionary.getAsString(PdfName.M);
      if (localPdfString2 != null)
        localPdfPKCS7.setSignDate(PdfDate.decode(localPdfString2.toString()));
      PdfObject localPdfObject = PdfReader.getPdfObject(localPdfDictionary.get(PdfName.NAME));
      if (localPdfObject != null)
        if (localPdfObject.isString())
          localPdfPKCS7.setSignName(((PdfString)localPdfObject).toUnicodeString());
        else if (localPdfObject.isName())
          localPdfPKCS7.setSignName(PdfName.decodeName(localPdfObject.toString()));
      localPdfString2 = localPdfDictionary.getAsString(PdfName.REASON);
      if (localPdfString2 != null)
        localPdfPKCS7.setReason(localPdfString2.toUnicodeString());
      localPdfString2 = localPdfDictionary.getAsString(PdfName.LOCATION);
      if (localPdfString2 != null)
        localPdfPKCS7.setLocation(localPdfString2.toUnicodeString());
      return localPdfPKCS7;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  private void updateByteRange(PdfPKCS7 paramPdfPKCS7, PdfDictionary paramPdfDictionary)
  {
    PdfArray localPdfArray = paramPdfDictionary.getAsArray(PdfName.BYTERANGE);
    RandomAccessFileOrArray localRandomAccessFileOrArray = this.reader.getSafeFile();
    try
    {
      localRandomAccessFileOrArray.reOpen();
      byte[] arrayOfByte = new byte[8192];
      for (int i = 0; i < localPdfArray.size(); i++)
      {
        int j = localPdfArray.getAsNumber(i).intValue();
        i++;
        int k = localPdfArray.getAsNumber(i).intValue();
        localRandomAccessFileOrArray.seek(j);
        while (k > 0)
        {
          int m = localRandomAccessFileOrArray.read(arrayOfByte, 0, Math.min(k, arrayOfByte.length));
          if (m <= 0)
            break;
          k -= m;
          paramPdfPKCS7.update(arrayOfByte, 0, m);
        }
      }
    }
    catch (Exception localException2)
    {
      throw new ExceptionConverter(localException2);
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException3)
      {
      }
    }
  }

  private void markUsed(PdfObject paramPdfObject)
  {
    if (!this.append)
      return;
    ((PdfStamperImp)this.writer).markUsed(paramPdfObject);
  }

  public int getTotalRevisions()
  {
    getSignatureNames();
    return this.totalRevisions;
  }

  public int getRevision(String paramString)
  {
    getSignatureNames();
    paramString = getTranslatedFieldName(paramString);
    if (!this.sigNames.containsKey(paramString))
      return 0;
    return ((int[])this.sigNames.get(paramString))[1];
  }

  public InputStream extractRevision(String paramString)
    throws IOException
  {
    getSignatureNames();
    paramString = getTranslatedFieldName(paramString);
    if (!this.sigNames.containsKey(paramString))
      return null;
    int i = ((int[])this.sigNames.get(paramString))[0];
    RandomAccessFileOrArray localRandomAccessFileOrArray = this.reader.getSafeFile();
    localRandomAccessFileOrArray.reOpen();
    localRandomAccessFileOrArray.seek(0);
    return new RevisionStream(localRandomAccessFileOrArray, i, null);
  }

  public Map getFieldCache()
  {
    return this.fieldCache;
  }

  public void setFieldCache(Map paramMap)
  {
    this.fieldCache = paramMap;
  }

  public void setExtraMargin(float paramFloat1, float paramFloat2)
  {
    this.extraMarginLeft = paramFloat1;
    this.extraMarginTop = paramFloat2;
  }

  public void addSubstitutionFont(BaseFont paramBaseFont)
  {
    if (this.substitutionFonts == null)
      this.substitutionFonts = new ArrayList();
    this.substitutionFonts.add(paramBaseFont);
  }

  public ArrayList getSubstitutionFonts()
  {
    return this.substitutionFonts;
  }

  public void setSubstitutionFonts(ArrayList paramArrayList)
  {
    this.substitutionFonts = paramArrayList;
  }

  public XfaForm getXfa()
  {
    return this.xfa;
  }

  public PushbuttonField getNewPushbuttonFromField(String paramString)
  {
    return getNewPushbuttonFromField(paramString, 0);
  }

  public PushbuttonField getNewPushbuttonFromField(String paramString, int paramInt)
  {
    try
    {
      if (getFieldType(paramString) != 1)
        return null;
      Item localItem = getFieldItem(paramString);
      if (paramInt >= localItem.size())
        return null;
      int i = paramInt * 5;
      float[] arrayOfFloat = getFieldPositions(paramString);
      Rectangle localRectangle = new Rectangle(arrayOfFloat[(i + 1)], arrayOfFloat[(i + 2)], arrayOfFloat[(i + 3)], arrayOfFloat[(i + 4)]);
      PushbuttonField localPushbuttonField = new PushbuttonField(this.writer, localRectangle, null);
      PdfDictionary localPdfDictionary1 = localItem.getMerged(paramInt);
      decodeGenericDictionary(localPdfDictionary1, localPushbuttonField);
      PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.MK);
      if (localPdfDictionary2 != null)
      {
        PdfString localPdfString = localPdfDictionary2.getAsString(PdfName.CA);
        if (localPdfString != null)
          localPushbuttonField.setText(localPdfString.toUnicodeString());
        PdfNumber localPdfNumber = localPdfDictionary2.getAsNumber(PdfName.TP);
        if (localPdfNumber != null)
          localPushbuttonField.setLayout(localPdfNumber.intValue() + 1);
        PdfDictionary localPdfDictionary3 = localPdfDictionary2.getAsDict(PdfName.IF);
        if (localPdfDictionary3 != null)
        {
          localObject = localPdfDictionary3.getAsName(PdfName.SW);
          if (localObject != null)
          {
            int j = 1;
            if (((PdfName)localObject).equals(PdfName.B))
              j = 3;
            else if (((PdfName)localObject).equals(PdfName.S))
              j = 4;
            else if (((PdfName)localObject).equals(PdfName.N))
              j = 2;
            localPushbuttonField.setScaleIcon(j);
          }
          localObject = localPdfDictionary3.getAsName(PdfName.S);
          if ((localObject != null) && (((PdfName)localObject).equals(PdfName.A)))
            localPushbuttonField.setProportionalIcon(false);
          PdfArray localPdfArray = localPdfDictionary3.getAsArray(PdfName.A);
          if ((localPdfArray != null) && (localPdfArray.size() == 2))
          {
            float f1 = localPdfArray.getAsNumber(0).floatValue();
            float f2 = localPdfArray.getAsNumber(1).floatValue();
            localPushbuttonField.setIconHorizontalAdjustment(f1);
            localPushbuttonField.setIconVerticalAdjustment(f2);
          }
          PdfBoolean localPdfBoolean = localPdfDictionary3.getAsBoolean(PdfName.FB);
          if ((localPdfBoolean != null) && (localPdfBoolean.booleanValue()))
            localPushbuttonField.setIconFitToBounds(true);
        }
        Object localObject = localPdfDictionary2.get(PdfName.I);
        if ((localObject != null) && (((PdfObject)localObject).isIndirect()))
          localPushbuttonField.setIconReference((PRIndirectReference)localObject);
      }
      return localPushbuttonField;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public boolean replacePushbuttonField(String paramString, PdfFormField paramPdfFormField)
  {
    return replacePushbuttonField(paramString, paramPdfFormField, 0);
  }

  public boolean replacePushbuttonField(String paramString, PdfFormField paramPdfFormField, int paramInt)
  {
    if (getFieldType(paramString) != 1)
      return false;
    Item localItem = getFieldItem(paramString);
    if (paramInt >= localItem.size())
      return false;
    PdfDictionary localPdfDictionary1 = localItem.getMerged(paramInt);
    PdfDictionary localPdfDictionary2 = localItem.getValue(paramInt);
    PdfDictionary localPdfDictionary3 = localItem.getWidget(paramInt);
    for (int i = 0; i < buttonRemove.length; i++)
    {
      localPdfDictionary1.remove(buttonRemove[i]);
      localPdfDictionary2.remove(buttonRemove[i]);
      localPdfDictionary3.remove(buttonRemove[i]);
    }
    Iterator localIterator = paramPdfFormField.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      if ((localPdfName.equals(PdfName.T)) || (localPdfName.equals(PdfName.RECT)))
        continue;
      if (localPdfName.equals(PdfName.FF))
        localPdfDictionary2.put(localPdfName, paramPdfFormField.get(localPdfName));
      else
        localPdfDictionary3.put(localPdfName, paramPdfFormField.get(localPdfName));
      localPdfDictionary1.put(localPdfName, paramPdfFormField.get(localPdfName));
    }
    return true;
  }

  static
  {
    stdFieldFontNames.put("CoBO", new String[] { "Courier-BoldOblique" });
    stdFieldFontNames.put("CoBo", new String[] { "Courier-Bold" });
    stdFieldFontNames.put("CoOb", new String[] { "Courier-Oblique" });
    stdFieldFontNames.put("Cour", new String[] { "Courier" });
    stdFieldFontNames.put("HeBO", new String[] { "Helvetica-BoldOblique" });
    stdFieldFontNames.put("HeBo", new String[] { "Helvetica-Bold" });
    stdFieldFontNames.put("HeOb", new String[] { "Helvetica-Oblique" });
    stdFieldFontNames.put("Helv", new String[] { "Helvetica" });
    stdFieldFontNames.put("Symb", new String[] { "Symbol" });
    stdFieldFontNames.put("TiBI", new String[] { "Times-BoldItalic" });
    stdFieldFontNames.put("TiBo", new String[] { "Times-Bold" });
    stdFieldFontNames.put("TiIt", new String[] { "Times-Italic" });
    stdFieldFontNames.put("TiRo", new String[] { "Times-Roman" });
    stdFieldFontNames.put("ZaDb", new String[] { "ZapfDingbats" });
    stdFieldFontNames.put("HySm", new String[] { "HYSMyeongJo-Medium", "UniKS-UCS2-H" });
    stdFieldFontNames.put("HyGo", new String[] { "HYGoThic-Medium", "UniKS-UCS2-H" });
    stdFieldFontNames.put("KaGo", new String[] { "HeiseiKakuGo-W5", "UniKS-UCS2-H" });
    stdFieldFontNames.put("KaMi", new String[] { "HeiseiMin-W3", "UniJIS-UCS2-H" });
    stdFieldFontNames.put("MHei", new String[] { "MHei-Medium", "UniCNS-UCS2-H" });
    stdFieldFontNames.put("MSun", new String[] { "MSung-Light", "UniCNS-UCS2-H" });
    stdFieldFontNames.put("STSo", new String[] { "STSong-Light", "UniGB-UCS2-H" });
    buttonRemove = new PdfName[] { PdfName.MK, PdfName.F, PdfName.FF, PdfName.Q, PdfName.BS, PdfName.BORDER };
  }

  private static class SorterComparator
    implements Comparator
  {
    private SorterComparator()
    {
    }

    public int compare(Object paramObject1, Object paramObject2)
    {
      int i = ((int[])((Object[])paramObject1)[1])[0];
      int j = ((int[])((Object[])paramObject2)[1])[0];
      return i - j;
    }

    SorterComparator(AcroFields.1 param1)
    {
      this();
    }
  }

  private static class RevisionStream extends InputStream
  {
    private byte[] b = new byte[1];
    private RandomAccessFileOrArray raf;
    private int length;
    private int rangePosition = 0;
    private boolean closed;

    private RevisionStream(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt)
    {
      this.raf = paramRandomAccessFileOrArray;
      this.length = paramInt;
    }

    public int read()
      throws IOException
    {
      int i = read(this.b);
      if (i != 1)
        return -1;
      return this.b[0] & 0xFF;
    }

    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramArrayOfByte == null)
        throw new NullPointerException();
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0))
        throw new IndexOutOfBoundsException();
      if (paramInt2 == 0)
        return 0;
      if (this.rangePosition >= this.length)
      {
        close();
        return -1;
      }
      int i = Math.min(paramInt2, this.length - this.rangePosition);
      this.raf.readFully(paramArrayOfByte, paramInt1, i);
      this.rangePosition += i;
      return i;
    }

    public void close()
      throws IOException
    {
      if (!this.closed)
      {
        this.raf.close();
        this.closed = true;
      }
    }

    RevisionStream(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt, AcroFields.1 param1)
    {
      this(paramRandomAccessFileOrArray, paramInt);
    }
  }

  private static class InstHit
  {
    IntHashtable hits;

    public InstHit(int[] paramArrayOfInt)
    {
      if (paramArrayOfInt == null)
        return;
      this.hits = new IntHashtable();
      for (int i = 0; i < paramArrayOfInt.length; i++)
        this.hits.put(paramArrayOfInt[i], 1);
    }

    public boolean isHit(int paramInt)
    {
      if (this.hits == null)
        return true;
      return this.hits.containsKey(paramInt);
    }
  }

  public static class Item
  {
    public static final int WRITE_MERGED = 1;
    public static final int WRITE_WIDGET = 2;
    public static final int WRITE_VALUE = 4;

    /** @deprecated */
    public ArrayList values = new ArrayList();

    /** @deprecated */
    public ArrayList widgets = new ArrayList();

    /** @deprecated */
    public ArrayList widget_refs = new ArrayList();

    /** @deprecated */
    public ArrayList merged = new ArrayList();

    /** @deprecated */
    public ArrayList page = new ArrayList();

    /** @deprecated */
    public ArrayList tabOrder = new ArrayList();

    public void writeToAll(PdfName paramPdfName, PdfObject paramPdfObject, int paramInt)
    {
      PdfDictionary localPdfDictionary = null;
      int i;
      if ((paramInt & 0x1) != 0)
        for (i = 0; i < this.merged.size(); i++)
        {
          localPdfDictionary = getMerged(i);
          localPdfDictionary.put(paramPdfName, paramPdfObject);
        }
      if ((paramInt & 0x2) != 0)
        for (i = 0; i < this.widgets.size(); i++)
        {
          localPdfDictionary = getWidget(i);
          localPdfDictionary.put(paramPdfName, paramPdfObject);
        }
      if ((paramInt & 0x4) != 0)
        for (i = 0; i < this.values.size(); i++)
        {
          localPdfDictionary = getValue(i);
          localPdfDictionary.put(paramPdfName, paramPdfObject);
        }
    }

    public void markUsed(AcroFields paramAcroFields, int paramInt)
    {
      int i;
      if ((paramInt & 0x4) != 0)
        for (i = 0; i < size(); i++)
          paramAcroFields.markUsed(getValue(i));
      if ((paramInt & 0x2) != 0)
        for (i = 0; i < size(); i++)
          paramAcroFields.markUsed(getWidget(i));
    }

    public int size()
    {
      return this.values.size();
    }

    void remove(int paramInt)
    {
      this.values.remove(paramInt);
      this.widgets.remove(paramInt);
      this.widget_refs.remove(paramInt);
      this.merged.remove(paramInt);
      this.page.remove(paramInt);
      this.tabOrder.remove(paramInt);
    }

    public PdfDictionary getValue(int paramInt)
    {
      return (PdfDictionary)this.values.get(paramInt);
    }

    void addValue(PdfDictionary paramPdfDictionary)
    {
      this.values.add(paramPdfDictionary);
    }

    public PdfDictionary getWidget(int paramInt)
    {
      return (PdfDictionary)this.widgets.get(paramInt);
    }

    void addWidget(PdfDictionary paramPdfDictionary)
    {
      this.widgets.add(paramPdfDictionary);
    }

    public PdfIndirectReference getWidgetRef(int paramInt)
    {
      return (PdfIndirectReference)this.widget_refs.get(paramInt);
    }

    void addWidgetRef(PdfIndirectReference paramPdfIndirectReference)
    {
      this.widget_refs.add(paramPdfIndirectReference);
    }

    public PdfDictionary getMerged(int paramInt)
    {
      return (PdfDictionary)this.merged.get(paramInt);
    }

    void addMerged(PdfDictionary paramPdfDictionary)
    {
      this.merged.add(paramPdfDictionary);
    }

    public Integer getPage(int paramInt)
    {
      return (Integer)this.page.get(paramInt);
    }

    void addPage(int paramInt)
    {
      this.page.add(new Integer(paramInt));
    }

    void forcePage(int paramInt1, int paramInt2)
    {
      this.page.set(paramInt1, new Integer(paramInt2));
    }

    public Integer getTabOrder(int paramInt)
    {
      return (Integer)this.tabOrder.get(paramInt);
    }

    void addTabOrder(int paramInt)
    {
      this.tabOrder.add(new Integer(paramInt));
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.AcroFields
 * JD-Core Version:    0.6.0
 */