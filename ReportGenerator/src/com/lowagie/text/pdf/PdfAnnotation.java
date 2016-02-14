package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

public class PdfAnnotation extends PdfDictionary
{
  public static final PdfName HIGHLIGHT_NONE = PdfName.N;
  public static final PdfName HIGHLIGHT_INVERT = PdfName.I;
  public static final PdfName HIGHLIGHT_OUTLINE = PdfName.O;
  public static final PdfName HIGHLIGHT_PUSH = PdfName.P;
  public static final PdfName HIGHLIGHT_TOGGLE = PdfName.T;
  public static final int FLAGS_INVISIBLE = 1;
  public static final int FLAGS_HIDDEN = 2;
  public static final int FLAGS_PRINT = 4;
  public static final int FLAGS_NOZOOM = 8;
  public static final int FLAGS_NOROTATE = 16;
  public static final int FLAGS_NOVIEW = 32;
  public static final int FLAGS_READONLY = 64;
  public static final int FLAGS_LOCKED = 128;
  public static final int FLAGS_TOGGLENOVIEW = 256;
  public static final PdfName APPEARANCE_NORMAL = PdfName.N;
  public static final PdfName APPEARANCE_ROLLOVER = PdfName.R;
  public static final PdfName APPEARANCE_DOWN = PdfName.D;
  public static final PdfName AA_ENTER = PdfName.E;
  public static final PdfName AA_EXIT = PdfName.X;
  public static final PdfName AA_DOWN = PdfName.D;
  public static final PdfName AA_UP = PdfName.U;
  public static final PdfName AA_FOCUS = PdfName.FO;
  public static final PdfName AA_BLUR = PdfName.BL;
  public static final PdfName AA_JS_KEY = PdfName.K;
  public static final PdfName AA_JS_FORMAT = PdfName.F;
  public static final PdfName AA_JS_CHANGE = PdfName.V;
  public static final PdfName AA_JS_OTHER_CHANGE = PdfName.C;
  public static final int MARKUP_HIGHLIGHT = 0;
  public static final int MARKUP_UNDERLINE = 1;
  public static final int MARKUP_STRIKEOUT = 2;
  public static final int MARKUP_SQUIGGLY = 3;
  protected PdfWriter writer;
  protected PdfIndirectReference reference;
  protected HashMap templates;
  protected boolean form = false;
  protected boolean annotation = true;
  protected boolean used = false;
  private int placeInPage = -1;

  public PdfAnnotation(PdfWriter paramPdfWriter, Rectangle paramRectangle)
  {
    this.writer = paramPdfWriter;
    if (paramRectangle != null)
      put(PdfName.RECT, new PdfRectangle(paramRectangle));
  }

  public PdfAnnotation(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, PdfString paramPdfString1, PdfString paramPdfString2)
  {
    this.writer = paramPdfWriter;
    put(PdfName.SUBTYPE, PdfName.TEXT);
    put(PdfName.T, paramPdfString1);
    put(PdfName.RECT, new PdfRectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4));
    put(PdfName.CONTENTS, paramPdfString2);
  }

  public PdfAnnotation(PdfWriter paramPdfWriter, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, PdfAction paramPdfAction)
  {
    this.writer = paramPdfWriter;
    put(PdfName.SUBTYPE, PdfName.LINK);
    put(PdfName.RECT, new PdfRectangle(paramFloat1, paramFloat2, paramFloat3, paramFloat4));
    put(PdfName.A, paramPdfAction);
    put(PdfName.BORDER, new PdfBorderArray(0.0F, 0.0F, 0.0F));
    put(PdfName.C, new PdfColor(0, 0, 255));
  }

  public static PdfAnnotation createScreen(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString1, PdfFileSpecification paramPdfFileSpecification, String paramString2, boolean paramBoolean)
    throws IOException
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.SCREEN);
    localPdfAnnotation.put(PdfName.F, new PdfNumber(4));
    localPdfAnnotation.put(PdfName.TYPE, PdfName.ANNOT);
    localPdfAnnotation.setPage();
    PdfIndirectReference localPdfIndirectReference1 = localPdfAnnotation.getIndirectReference();
    PdfAction localPdfAction = PdfAction.rendition(paramString1, paramPdfFileSpecification, paramString2, localPdfIndirectReference1);
    PdfIndirectReference localPdfIndirectReference2 = paramPdfWriter.addToBody(localPdfAction).getIndirectReference();
    if (paramBoolean)
    {
      PdfDictionary localPdfDictionary = new PdfDictionary();
      localPdfDictionary.put(new PdfName("PV"), localPdfIndirectReference2);
      localPdfAnnotation.put(PdfName.AA, localPdfDictionary);
    }
    localPdfAnnotation.put(PdfName.A, localPdfIndirectReference2);
    return localPdfAnnotation;
  }

  public PdfIndirectReference getIndirectReference()
  {
    if (this.reference == null)
      this.reference = this.writer.getPdfIndirectReference();
    return this.reference;
  }

  public static PdfAnnotation createText(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString1, String paramString2, boolean paramBoolean, String paramString3)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.TEXT);
    if (paramString1 != null)
      localPdfAnnotation.put(PdfName.T, new PdfString(paramString1, "UnicodeBig"));
    if (paramString2 != null)
      localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString2, "UnicodeBig"));
    if (paramBoolean)
      localPdfAnnotation.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
    if (paramString3 != null)
      localPdfAnnotation.put(PdfName.NAME, new PdfName(paramString3));
    return localPdfAnnotation;
  }

  protected static PdfAnnotation createLink(PdfWriter paramPdfWriter, Rectangle paramRectangle, PdfName paramPdfName)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.LINK);
    if (!paramPdfName.equals(HIGHLIGHT_INVERT))
      localPdfAnnotation.put(PdfName.H, paramPdfName);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createLink(PdfWriter paramPdfWriter, Rectangle paramRectangle, PdfName paramPdfName, PdfAction paramPdfAction)
  {
    PdfAnnotation localPdfAnnotation = createLink(paramPdfWriter, paramRectangle, paramPdfName);
    localPdfAnnotation.putEx(PdfName.A, paramPdfAction);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createLink(PdfWriter paramPdfWriter, Rectangle paramRectangle, PdfName paramPdfName, String paramString)
  {
    PdfAnnotation localPdfAnnotation = createLink(paramPdfWriter, paramRectangle, paramPdfName);
    localPdfAnnotation.put(PdfName.DEST, new PdfString(paramString));
    return localPdfAnnotation;
  }

  public static PdfAnnotation createLink(PdfWriter paramPdfWriter, Rectangle paramRectangle, PdfName paramPdfName, int paramInt, PdfDestination paramPdfDestination)
  {
    PdfAnnotation localPdfAnnotation = createLink(paramPdfWriter, paramRectangle, paramPdfName);
    PdfIndirectReference localPdfIndirectReference = paramPdfWriter.getPageReference(paramInt);
    paramPdfDestination.addPage(localPdfIndirectReference);
    localPdfAnnotation.put(PdfName.DEST, paramPdfDestination);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createFreeText(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, PdfContentByte paramPdfContentByte)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.FREETEXT);
    localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    localPdfAnnotation.setDefaultAppearanceString(paramPdfContentByte);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createLine(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.LINE);
    localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    PdfArray localPdfArray = new PdfArray(new PdfNumber(paramFloat1));
    localPdfArray.add(new PdfNumber(paramFloat2));
    localPdfArray.add(new PdfNumber(paramFloat3));
    localPdfArray.add(new PdfNumber(paramFloat4));
    localPdfAnnotation.put(PdfName.L, localPdfArray);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createSquareCircle(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, boolean paramBoolean)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    if (paramBoolean)
      localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.SQUARE);
    else
      localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.CIRCLE);
    localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    return localPdfAnnotation;
  }

  public static PdfAnnotation createMarkup(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, int paramInt, float[] paramArrayOfFloat)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    PdfName localPdfName = PdfName.HIGHLIGHT;
    switch (paramInt)
    {
    case 1:
      localPdfName = PdfName.UNDERLINE;
      break;
    case 2:
      localPdfName = PdfName.STRIKEOUT;
      break;
    case 3:
      localPdfName = PdfName.SQUIGGLY;
    }
    localPdfAnnotation.put(PdfName.SUBTYPE, localPdfName);
    localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfFloat.length; i++)
      localPdfArray.add(new PdfNumber(paramArrayOfFloat[i]));
    localPdfAnnotation.put(PdfName.QUADPOINTS, localPdfArray);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createStamp(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString1, String paramString2)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.STAMP);
    localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString1, "UnicodeBig"));
    localPdfAnnotation.put(PdfName.NAME, new PdfName(paramString2));
    return localPdfAnnotation;
  }

  public static PdfAnnotation createInk(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, float[][] paramArrayOfFloat)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.INK);
    localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    PdfArray localPdfArray1 = new PdfArray();
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      PdfArray localPdfArray2 = new PdfArray();
      float[] arrayOfFloat = paramArrayOfFloat[i];
      for (int j = 0; j < arrayOfFloat.length; j++)
        localPdfArray2.add(new PdfNumber(arrayOfFloat[j]));
      localPdfArray1.add(localPdfArray2);
    }
    localPdfAnnotation.put(PdfName.INKLIST, localPdfArray1);
    return localPdfAnnotation;
  }

  public static PdfAnnotation createFileAttachment(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString1, byte[] paramArrayOfByte, String paramString2, String paramString3)
    throws IOException
  {
    return createFileAttachment(paramPdfWriter, paramRectangle, paramString1, PdfFileSpecification.fileEmbedded(paramPdfWriter, paramString2, paramString3, paramArrayOfByte));
  }

  public static PdfAnnotation createFileAttachment(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, PdfFileSpecification paramPdfFileSpecification)
    throws IOException
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.FILEATTACHMENT);
    if (paramString != null)
      localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    localPdfAnnotation.put(PdfName.FS, paramPdfFileSpecification.getReference());
    return localPdfAnnotation;
  }

  public static PdfAnnotation createPopup(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString, boolean paramBoolean)
  {
    PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, paramRectangle);
    localPdfAnnotation.put(PdfName.SUBTYPE, PdfName.POPUP);
    if (paramString != null)
      localPdfAnnotation.put(PdfName.CONTENTS, new PdfString(paramString, "UnicodeBig"));
    if (paramBoolean)
      localPdfAnnotation.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
    return localPdfAnnotation;
  }

  public void setDefaultAppearanceString(PdfContentByte paramPdfContentByte)
  {
    byte[] arrayOfByte = paramPdfContentByte.getInternalBuffer().toByteArray();
    int i = arrayOfByte.length;
    for (int j = 0; j < i; j++)
    {
      if (arrayOfByte[j] != 10)
        continue;
      arrayOfByte[j] = 32;
    }
    put(PdfName.DA, new PdfString(arrayOfByte));
  }

  public void setFlags(int paramInt)
  {
    if (paramInt == 0)
      remove(PdfName.F);
    else
      put(PdfName.F, new PdfNumber(paramInt));
  }

  public void setBorder(PdfBorderArray paramPdfBorderArray)
  {
    put(PdfName.BORDER, paramPdfBorderArray);
  }

  public void setBorderStyle(PdfBorderDictionary paramPdfBorderDictionary)
  {
    put(PdfName.BS, paramPdfBorderDictionary);
  }

  public void setHighlighting(PdfName paramPdfName)
  {
    if (paramPdfName.equals(HIGHLIGHT_INVERT))
      remove(PdfName.H);
    else
      put(PdfName.H, paramPdfName);
  }

  public void setAppearance(PdfName paramPdfName, PdfTemplate paramPdfTemplate)
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)get(PdfName.AP);
    if (localPdfDictionary == null)
      localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(paramPdfName, paramPdfTemplate.getIndirectReference());
    put(PdfName.AP, localPdfDictionary);
    if (!this.form)
      return;
    if (this.templates == null)
      this.templates = new HashMap();
    this.templates.put(paramPdfTemplate, null);
  }

  public void setAppearance(PdfName paramPdfName, String paramString, PdfTemplate paramPdfTemplate)
  {
    PdfDictionary localPdfDictionary1 = (PdfDictionary)get(PdfName.AP);
    if (localPdfDictionary1 == null)
      localPdfDictionary1 = new PdfDictionary();
    PdfObject localPdfObject = localPdfDictionary1.get(paramPdfName);
    PdfDictionary localPdfDictionary2;
    if ((localPdfObject != null) && (localPdfObject.isDictionary()))
      localPdfDictionary2 = (PdfDictionary)localPdfObject;
    else
      localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(new PdfName(paramString), paramPdfTemplate.getIndirectReference());
    localPdfDictionary1.put(paramPdfName, localPdfDictionary2);
    put(PdfName.AP, localPdfDictionary1);
    if (!this.form)
      return;
    if (this.templates == null)
      this.templates = new HashMap();
    this.templates.put(paramPdfTemplate, null);
  }

  public void setAppearanceState(String paramString)
  {
    if (paramString == null)
    {
      remove(PdfName.AS);
      return;
    }
    put(PdfName.AS, new PdfName(paramString));
  }

  public void setColor(Color paramColor)
  {
    put(PdfName.C, new PdfColor(paramColor));
  }

  public void setTitle(String paramString)
  {
    if (paramString == null)
    {
      remove(PdfName.T);
      return;
    }
    put(PdfName.T, new PdfString(paramString, "UnicodeBig"));
  }

  public void setPopup(PdfAnnotation paramPdfAnnotation)
  {
    put(PdfName.POPUP, paramPdfAnnotation.getIndirectReference());
    paramPdfAnnotation.put(PdfName.PARENT, getIndirectReference());
  }

  public void setAction(PdfAction paramPdfAction)
  {
    put(PdfName.A, paramPdfAction);
  }

  public void setAdditionalActions(PdfName paramPdfName, PdfAction paramPdfAction)
  {
    PdfObject localPdfObject = get(PdfName.AA);
    PdfDictionary localPdfDictionary;
    if ((localPdfObject != null) && (localPdfObject.isDictionary()))
      localPdfDictionary = (PdfDictionary)localPdfObject;
    else
      localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(paramPdfName, paramPdfAction);
    put(PdfName.AA, localPdfDictionary);
  }

  public boolean isUsed()
  {
    return this.used;
  }

  public void setUsed()
  {
    this.used = true;
  }

  public HashMap getTemplates()
  {
    return this.templates;
  }

  public boolean isForm()
  {
    return this.form;
  }

  public boolean isAnnotation()
  {
    return this.annotation;
  }

  public void setPage(int paramInt)
  {
    put(PdfName.P, this.writer.getPageReference(paramInt));
  }

  public void setPage()
  {
    put(PdfName.P, this.writer.getCurrentPage());
  }

  public int getPlaceInPage()
  {
    return this.placeInPage;
  }

  public void setPlaceInPage(int paramInt)
  {
    this.placeInPage = paramInt;
  }

  public void setRotate(int paramInt)
  {
    put(PdfName.ROTATE, new PdfNumber(paramInt));
  }

  PdfDictionary getMK()
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)get(PdfName.MK);
    if (localPdfDictionary == null)
    {
      localPdfDictionary = new PdfDictionary();
      put(PdfName.MK, localPdfDictionary);
    }
    return localPdfDictionary;
  }

  public void setMKRotation(int paramInt)
  {
    getMK().put(PdfName.R, new PdfNumber(paramInt));
  }

  public static PdfArray getMKColor(Color paramColor)
  {
    PdfArray localPdfArray = new PdfArray();
    int i = ExtendedColor.getType(paramColor);
    switch (i)
    {
    case 1:
      localPdfArray.add(new PdfNumber(((GrayColor)paramColor).getGray()));
      break;
    case 2:
      CMYKColor localCMYKColor = (CMYKColor)paramColor;
      localPdfArray.add(new PdfNumber(localCMYKColor.getCyan()));
      localPdfArray.add(new PdfNumber(localCMYKColor.getMagenta()));
      localPdfArray.add(new PdfNumber(localCMYKColor.getYellow()));
      localPdfArray.add(new PdfNumber(localCMYKColor.getBlack()));
      break;
    case 3:
    case 4:
    case 5:
      throw new RuntimeException("Separations, patterns and shadings are not allowed in MK dictionary.");
    default:
      localPdfArray.add(new PdfNumber(paramColor.getRed() / 255.0F));
      localPdfArray.add(new PdfNumber(paramColor.getGreen() / 255.0F));
      localPdfArray.add(new PdfNumber(paramColor.getBlue() / 255.0F));
    }
    return localPdfArray;
  }

  public void setMKBorderColor(Color paramColor)
  {
    if (paramColor == null)
      getMK().remove(PdfName.BC);
    else
      getMK().put(PdfName.BC, getMKColor(paramColor));
  }

  public void setMKBackgroundColor(Color paramColor)
  {
    if (paramColor == null)
      getMK().remove(PdfName.BG);
    else
      getMK().put(PdfName.BG, getMKColor(paramColor));
  }

  public void setMKNormalCaption(String paramString)
  {
    getMK().put(PdfName.CA, new PdfString(paramString, "UnicodeBig"));
  }

  public void setMKRolloverCaption(String paramString)
  {
    getMK().put(PdfName.RC, new PdfString(paramString, "UnicodeBig"));
  }

  public void setMKAlternateCaption(String paramString)
  {
    getMK().put(PdfName.AC, new PdfString(paramString, "UnicodeBig"));
  }

  public void setMKNormalIcon(PdfTemplate paramPdfTemplate)
  {
    getMK().put(PdfName.I, paramPdfTemplate.getIndirectReference());
  }

  public void setMKRolloverIcon(PdfTemplate paramPdfTemplate)
  {
    getMK().put(PdfName.RI, paramPdfTemplate.getIndirectReference());
  }

  public void setMKAlternateIcon(PdfTemplate paramPdfTemplate)
  {
    getMK().put(PdfName.IX, paramPdfTemplate.getIndirectReference());
  }

  public void setMKIconFit(PdfName paramPdfName1, PdfName paramPdfName2, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    if (!paramPdfName1.equals(PdfName.A))
      localPdfDictionary.put(PdfName.SW, paramPdfName1);
    if (!paramPdfName2.equals(PdfName.P))
      localPdfDictionary.put(PdfName.S, paramPdfName2);
    if ((paramFloat1 != 0.5F) || (paramFloat2 != 0.5F))
    {
      PdfArray localPdfArray = new PdfArray(new PdfNumber(paramFloat1));
      localPdfArray.add(new PdfNumber(paramFloat2));
      localPdfDictionary.put(PdfName.A, localPdfArray);
    }
    if (paramBoolean)
      localPdfDictionary.put(PdfName.FB, PdfBoolean.PDFTRUE);
    getMK().put(PdfName.IF, localPdfDictionary);
  }

  public void setMKTextPosition(int paramInt)
  {
    getMK().put(PdfName.TP, new PdfNumber(paramInt));
  }

  public void setLayer(PdfOCG paramPdfOCG)
  {
    put(PdfName.OC, paramPdfOCG.getRef());
  }

  public void setName(String paramString)
  {
    put(PdfName.NM, new PdfString(paramString));
  }

  public static class PdfImportedLink
  {
    float llx;
    float lly;
    float urx;
    float ury;
    HashMap parameters = new HashMap();
    PdfArray destination = null;
    int newPage = 0;

    PdfImportedLink(PdfDictionary paramPdfDictionary)
    {
      this.parameters.putAll(paramPdfDictionary.hashMap);
      try
      {
        this.destination = ((PdfArray)this.parameters.remove(PdfName.DEST));
      }
      catch (ClassCastException localClassCastException)
      {
        throw new IllegalArgumentException("You have to consolidate the named destinations of your reader.");
      }
      if (this.destination != null)
        this.destination = new PdfArray(this.destination);
      PdfArray localPdfArray = (PdfArray)this.parameters.remove(PdfName.RECT);
      this.llx = localPdfArray.getAsNumber(0).floatValue();
      this.lly = localPdfArray.getAsNumber(1).floatValue();
      this.urx = localPdfArray.getAsNumber(2).floatValue();
      this.ury = localPdfArray.getAsNumber(3).floatValue();
    }

    public boolean isInternal()
    {
      return this.destination != null;
    }

    public int getDestinationPage()
    {
      if (!isInternal())
        return 0;
      PdfIndirectReference localPdfIndirectReference = this.destination.getAsIndirectObject(0);
      PRIndirectReference localPRIndirectReference1 = (PRIndirectReference)localPdfIndirectReference;
      PdfReader localPdfReader = localPRIndirectReference1.getReader();
      for (int i = 1; i <= localPdfReader.getNumberOfPages(); i++)
      {
        PRIndirectReference localPRIndirectReference2 = localPdfReader.getPageOrigRef(i);
        if ((localPRIndirectReference2.getGeneration() == localPRIndirectReference1.getGeneration()) && (localPRIndirectReference2.getNumber() == localPRIndirectReference1.getNumber()))
          return i;
      }
      throw new IllegalArgumentException("Page not found.");
    }

    public void setDestinationPage(int paramInt)
    {
      if (!isInternal())
        throw new IllegalArgumentException("Cannot change destination of external link");
      this.newPage = paramInt;
    }

    public void transformDestination(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      if (!isInternal())
        throw new IllegalArgumentException("Cannot change destination of external link");
      if (this.destination.getAsName(1).equals(PdfName.XYZ))
      {
        float f1 = this.destination.getAsNumber(2).floatValue();
        float f2 = this.destination.getAsNumber(3).floatValue();
        float f3 = f1 * paramFloat1 + f2 * paramFloat3 + paramFloat5;
        float f4 = f1 * paramFloat2 + f2 * paramFloat4 + paramFloat6;
        this.destination.set(2, new PdfNumber(f3));
        this.destination.set(3, new PdfNumber(f4));
      }
    }

    public void transformRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      float f1 = this.llx * paramFloat1 + this.lly * paramFloat3 + paramFloat5;
      float f2 = this.llx * paramFloat2 + this.lly * paramFloat4 + paramFloat6;
      this.llx = f1;
      this.lly = f2;
      f1 = this.urx * paramFloat1 + this.ury * paramFloat3 + paramFloat5;
      f2 = this.urx * paramFloat2 + this.ury * paramFloat4 + paramFloat6;
      this.urx = f1;
      this.ury = f2;
    }

    public PdfAnnotation createAnnotation(PdfWriter paramPdfWriter)
    {
      PdfAnnotation localPdfAnnotation = new PdfAnnotation(paramPdfWriter, new Rectangle(this.llx, this.lly, this.urx, this.ury));
      if (this.newPage != 0)
      {
        PdfIndirectReference localPdfIndirectReference = paramPdfWriter.getPageReference(this.newPage);
        this.destination.set(0, localPdfIndirectReference);
      }
      if (this.destination != null)
        localPdfAnnotation.put(PdfName.DEST, this.destination);
      localPdfAnnotation.hashMap.putAll(this.parameters);
      return localPdfAnnotation;
    }

    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer("Imported link: location [");
      localStringBuffer.append(this.llx);
      localStringBuffer.append(' ');
      localStringBuffer.append(this.lly);
      localStringBuffer.append(' ');
      localStringBuffer.append(this.urx);
      localStringBuffer.append(' ');
      localStringBuffer.append(this.ury);
      localStringBuffer.append("] destination ");
      localStringBuffer.append(this.destination);
      localStringBuffer.append(" parameters ");
      localStringBuffer.append(this.parameters);
      return localStringBuffer.toString();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfAnnotation
 * JD-Core Version:    0.6.0
 */