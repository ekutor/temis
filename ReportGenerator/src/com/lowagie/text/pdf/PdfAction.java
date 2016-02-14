package com.lowagie.text.pdf;

import com.lowagie.text.pdf.collection.PdfTargetDictionary;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class PdfAction extends PdfDictionary
{
  public static final int FIRSTPAGE = 1;
  public static final int PREVPAGE = 2;
  public static final int NEXTPAGE = 3;
  public static final int LASTPAGE = 4;
  public static final int PRINTDIALOG = 5;
  public static final int SUBMIT_EXCLUDE = 1;
  public static final int SUBMIT_INCLUDE_NO_VALUE_FIELDS = 2;
  public static final int SUBMIT_HTML_FORMAT = 4;
  public static final int SUBMIT_HTML_GET = 8;
  public static final int SUBMIT_COORDINATES = 16;
  public static final int SUBMIT_XFDF = 32;
  public static final int SUBMIT_INCLUDE_APPEND_SAVES = 64;
  public static final int SUBMIT_INCLUDE_ANNOTATIONS = 128;
  public static final int SUBMIT_PDF = 256;
  public static final int SUBMIT_CANONICAL_FORMAT = 512;
  public static final int SUBMIT_EXCL_NON_USER_ANNOTS = 1024;
  public static final int SUBMIT_EXCL_F_KEY = 2048;
  public static final int SUBMIT_EMBED_FORM = 8196;
  public static final int RESET_EXCLUDE = 1;

  public PdfAction()
  {
  }

  public PdfAction(URL paramURL)
  {
    this(paramURL.toExternalForm());
  }

  public PdfAction(URL paramURL, boolean paramBoolean)
  {
    this(paramURL.toExternalForm(), paramBoolean);
  }

  public PdfAction(String paramString)
  {
    this(paramString, false);
  }

  public PdfAction(String paramString, boolean paramBoolean)
  {
    put(PdfName.S, PdfName.URI);
    put(PdfName.URI, new PdfString(paramString));
    if (paramBoolean)
      put(PdfName.ISMAP, PdfBoolean.PDFTRUE);
  }

  PdfAction(PdfIndirectReference paramPdfIndirectReference)
  {
    put(PdfName.S, PdfName.GOTO);
    put(PdfName.D, paramPdfIndirectReference);
  }

  public PdfAction(String paramString1, String paramString2)
  {
    put(PdfName.S, PdfName.GOTOR);
    put(PdfName.F, new PdfString(paramString1));
    put(PdfName.D, new PdfString(paramString2));
  }

  public PdfAction(String paramString, int paramInt)
  {
    put(PdfName.S, PdfName.GOTOR);
    put(PdfName.F, new PdfString(paramString));
    put(PdfName.D, new PdfLiteral("[" + (paramInt - 1) + " /FitH 10000]"));
  }

  public PdfAction(int paramInt)
  {
    put(PdfName.S, PdfName.NAMED);
    switch (paramInt)
    {
    case 1:
      put(PdfName.N, PdfName.FIRSTPAGE);
      break;
    case 4:
      put(PdfName.N, PdfName.LASTPAGE);
      break;
    case 3:
      put(PdfName.N, PdfName.NEXTPAGE);
      break;
    case 2:
      put(PdfName.N, PdfName.PREVPAGE);
      break;
    case 5:
      put(PdfName.S, PdfName.JAVASCRIPT);
      put(PdfName.JS, new PdfString("this.print(true);\r"));
      break;
    default:
      throw new RuntimeException("Invalid named action.");
    }
  }

  public PdfAction(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    put(PdfName.S, PdfName.LAUNCH);
    if ((paramString2 == null) && (paramString3 == null) && (paramString4 == null))
    {
      put(PdfName.F, new PdfString(paramString1));
    }
    else
    {
      PdfDictionary localPdfDictionary = new PdfDictionary();
      localPdfDictionary.put(PdfName.F, new PdfString(paramString1));
      if (paramString2 != null)
        localPdfDictionary.put(PdfName.P, new PdfString(paramString2));
      if (paramString3 != null)
        localPdfDictionary.put(PdfName.O, new PdfString(paramString3));
      if (paramString4 != null)
        localPdfDictionary.put(PdfName.D, new PdfString(paramString4));
      put(PdfName.WIN, localPdfDictionary);
    }
  }

  public static PdfAction createLaunch(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    return new PdfAction(paramString1, paramString2, paramString3, paramString4);
  }

  public static PdfAction rendition(String paramString1, PdfFileSpecification paramPdfFileSpecification, String paramString2, PdfIndirectReference paramPdfIndirectReference)
    throws IOException
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.RENDITION);
    localPdfAction.put(PdfName.R, new PdfRendition(paramString1, paramPdfFileSpecification, paramString2));
    localPdfAction.put(new PdfName("OP"), new PdfNumber(0));
    localPdfAction.put(new PdfName("AN"), paramPdfIndirectReference);
    return localPdfAction;
  }

  public static PdfAction javaScript(String paramString, PdfWriter paramPdfWriter, boolean paramBoolean)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.JAVASCRIPT);
    if ((paramBoolean) && (paramString.length() < 50))
      localPdfAction.put(PdfName.JS, new PdfString(paramString, "UnicodeBig"));
    else if ((!paramBoolean) && (paramString.length() < 100))
      localPdfAction.put(PdfName.JS, new PdfString(paramString));
    else
      try
      {
        byte[] arrayOfByte = PdfEncodings.convertToBytes(paramString, paramBoolean ? "UnicodeBig" : "PDF");
        PdfStream localPdfStream = new PdfStream(arrayOfByte);
        localPdfStream.flateCompress(paramPdfWriter.getCompressionLevel());
        localPdfAction.put(PdfName.JS, paramPdfWriter.addToBody(localPdfStream).getIndirectReference());
      }
      catch (Exception localException)
      {
        localPdfAction.put(PdfName.JS, new PdfString(paramString));
      }
    return localPdfAction;
  }

  public static PdfAction javaScript(String paramString, PdfWriter paramPdfWriter)
  {
    return javaScript(paramString, paramPdfWriter, false);
  }

  static PdfAction createHide(PdfObject paramPdfObject, boolean paramBoolean)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.HIDE);
    localPdfAction.put(PdfName.T, paramPdfObject);
    if (!paramBoolean)
      localPdfAction.put(PdfName.H, PdfBoolean.PDFFALSE);
    return localPdfAction;
  }

  public static PdfAction createHide(PdfAnnotation paramPdfAnnotation, boolean paramBoolean)
  {
    return createHide(paramPdfAnnotation.getIndirectReference(), paramBoolean);
  }

  public static PdfAction createHide(String paramString, boolean paramBoolean)
  {
    return createHide(new PdfString(paramString), paramBoolean);
  }

  static PdfArray buildArray(Object[] paramArrayOfObject)
  {
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      Object localObject = paramArrayOfObject[i];
      if ((localObject instanceof String))
        localPdfArray.add(new PdfString((String)localObject));
      else if ((localObject instanceof PdfAnnotation))
        localPdfArray.add(((PdfAnnotation)localObject).getIndirectReference());
      else
        throw new RuntimeException("The array must contain String or PdfAnnotation.");
    }
    return localPdfArray;
  }

  public static PdfAction createHide(Object[] paramArrayOfObject, boolean paramBoolean)
  {
    return createHide(buildArray(paramArrayOfObject), paramBoolean);
  }

  public static PdfAction createSubmitForm(String paramString, Object[] paramArrayOfObject, int paramInt)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.SUBMITFORM);
    PdfDictionary localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(PdfName.F, new PdfString(paramString));
    localPdfDictionary.put(PdfName.FS, PdfName.URL);
    localPdfAction.put(PdfName.F, localPdfDictionary);
    if (paramArrayOfObject != null)
      localPdfAction.put(PdfName.FIELDS, buildArray(paramArrayOfObject));
    localPdfAction.put(PdfName.FLAGS, new PdfNumber(paramInt));
    return localPdfAction;
  }

  public static PdfAction createResetForm(Object[] paramArrayOfObject, int paramInt)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.RESETFORM);
    if (paramArrayOfObject != null)
      localPdfAction.put(PdfName.FIELDS, buildArray(paramArrayOfObject));
    localPdfAction.put(PdfName.FLAGS, new PdfNumber(paramInt));
    return localPdfAction;
  }

  public static PdfAction createImportData(String paramString)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.IMPORTDATA);
    localPdfAction.put(PdfName.F, new PdfString(paramString));
    return localPdfAction;
  }

  public void next(PdfAction paramPdfAction)
  {
    PdfObject localPdfObject = get(PdfName.NEXT);
    if (localPdfObject == null)
    {
      put(PdfName.NEXT, paramPdfAction);
    }
    else if (localPdfObject.isDictionary())
    {
      PdfArray localPdfArray = new PdfArray(localPdfObject);
      localPdfArray.add(paramPdfAction);
      put(PdfName.NEXT, localPdfArray);
    }
    else
    {
      ((PdfArray)localPdfObject).add(paramPdfAction);
    }
  }

  public static PdfAction gotoLocalPage(int paramInt, PdfDestination paramPdfDestination, PdfWriter paramPdfWriter)
  {
    PdfIndirectReference localPdfIndirectReference = paramPdfWriter.getPageReference(paramInt);
    paramPdfDestination.addPage(localPdfIndirectReference);
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.GOTO);
    localPdfAction.put(PdfName.D, paramPdfDestination);
    return localPdfAction;
  }

  public static PdfAction gotoLocalPage(String paramString, boolean paramBoolean)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.GOTO);
    if (paramBoolean)
      localPdfAction.put(PdfName.D, new PdfName(paramString));
    else
      localPdfAction.put(PdfName.D, new PdfString(paramString, null));
    return localPdfAction;
  }

  public static PdfAction gotoRemotePage(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.F, new PdfString(paramString1));
    localPdfAction.put(PdfName.S, PdfName.GOTOR);
    if (paramBoolean1)
      localPdfAction.put(PdfName.D, new PdfName(paramString2));
    else
      localPdfAction.put(PdfName.D, new PdfString(paramString2, null));
    if (paramBoolean2)
      localPdfAction.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
    return localPdfAction;
  }

  public static PdfAction gotoEmbedded(String paramString1, PdfTargetDictionary paramPdfTargetDictionary, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
      return gotoEmbedded(paramString1, paramPdfTargetDictionary, new PdfName(paramString2), paramBoolean2);
    return gotoEmbedded(paramString1, paramPdfTargetDictionary, new PdfString(paramString2, null), paramBoolean2);
  }

  public static PdfAction gotoEmbedded(String paramString, PdfTargetDictionary paramPdfTargetDictionary, PdfObject paramPdfObject, boolean paramBoolean)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.GOTOE);
    localPdfAction.put(PdfName.T, paramPdfTargetDictionary);
    localPdfAction.put(PdfName.D, paramPdfObject);
    localPdfAction.put(PdfName.NEWWINDOW, new PdfBoolean(paramBoolean));
    if (paramString != null)
      localPdfAction.put(PdfName.F, new PdfString(paramString));
    return localPdfAction;
  }

  public static PdfAction setOCGstate(ArrayList paramArrayList, boolean paramBoolean)
  {
    PdfAction localPdfAction = new PdfAction();
    localPdfAction.put(PdfName.S, PdfName.SETOCGSTATE);
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      Object localObject = paramArrayList.get(i);
      if (localObject == null)
        continue;
      if ((localObject instanceof PdfIndirectReference))
      {
        localPdfArray.add((PdfIndirectReference)localObject);
      }
      else if ((localObject instanceof PdfLayer))
      {
        localPdfArray.add(((PdfLayer)localObject).getRef());
      }
      else if ((localObject instanceof PdfName))
      {
        localPdfArray.add((PdfName)localObject);
      }
      else if ((localObject instanceof String))
      {
        PdfName localPdfName = null;
        String str = (String)localObject;
        if (str.equalsIgnoreCase("on"))
          localPdfName = PdfName.ON;
        else if (str.equalsIgnoreCase("off"))
          localPdfName = PdfName.OFF;
        else if (str.equalsIgnoreCase("toggle"))
          localPdfName = PdfName.TOGGLE;
        else
          throw new IllegalArgumentException("A string '" + str + " was passed in state. Only 'ON', 'OFF' and 'Toggle' are allowed.");
        localPdfArray.add(localPdfName);
      }
      else
      {
        throw new IllegalArgumentException("Invalid type was passed in state: " + localObject.getClass().getName());
      }
    }
    localPdfAction.put(PdfName.STATE, localPdfArray);
    if (!paramBoolean)
      localPdfAction.put(PdfName.PRESERVERB, PdfBoolean.PDFFALSE);
    return localPdfAction;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfAction
 * JD-Core Version:    0.6.0
 */