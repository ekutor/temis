package com.lowagie.text.pdf;

import java.util.ArrayList;

public class PdfLayer extends PdfDictionary
  implements PdfOCG
{
  protected PdfIndirectReference ref;
  protected ArrayList children;
  protected PdfLayer parent;
  protected String title;
  private boolean on = true;
  private boolean onPanel = true;

  PdfLayer(String paramString)
  {
    this.title = paramString;
  }

  public static PdfLayer createTitle(String paramString, PdfWriter paramPdfWriter)
  {
    if (paramString == null)
      throw new NullPointerException("Title cannot be null.");
    PdfLayer localPdfLayer = new PdfLayer(paramString);
    paramPdfWriter.registerLayer(localPdfLayer);
    return localPdfLayer;
  }

  public PdfLayer(String paramString, PdfWriter paramPdfWriter)
  {
    super(PdfName.OCG);
    setName(paramString);
    this.ref = paramPdfWriter.getPdfIndirectReference();
    paramPdfWriter.registerLayer(this);
  }

  String getTitle()
  {
    return this.title;
  }

  public void addChild(PdfLayer paramPdfLayer)
  {
    if (paramPdfLayer.parent != null)
      throw new IllegalArgumentException("The layer '" + ((PdfString)paramPdfLayer.get(PdfName.NAME)).toUnicodeString() + "' already has a parent.");
    paramPdfLayer.parent = this;
    if (this.children == null)
      this.children = new ArrayList();
    this.children.add(paramPdfLayer);
  }

  public PdfLayer getParent()
  {
    return this.parent;
  }

  public ArrayList getChildren()
  {
    return this.children;
  }

  public PdfIndirectReference getRef()
  {
    return this.ref;
  }

  void setRef(PdfIndirectReference paramPdfIndirectReference)
  {
    this.ref = paramPdfIndirectReference;
  }

  public void setName(String paramString)
  {
    put(PdfName.NAME, new PdfString(paramString, "UnicodeBig"));
  }

  public PdfObject getPdfObject()
  {
    return this;
  }

  public boolean isOn()
  {
    return this.on;
  }

  public void setOn(boolean paramBoolean)
  {
    this.on = paramBoolean;
  }

  private PdfDictionary getUsage()
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)get(PdfName.USAGE);
    if (localPdfDictionary == null)
    {
      localPdfDictionary = new PdfDictionary();
      put(PdfName.USAGE, localPdfDictionary);
    }
    return localPdfDictionary;
  }

  public void setCreatorInfo(String paramString1, String paramString2)
  {
    PdfDictionary localPdfDictionary1 = getUsage();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.CREATOR, new PdfString(paramString1, "UnicodeBig"));
    localPdfDictionary2.put(PdfName.SUBTYPE, new PdfName(paramString2));
    localPdfDictionary1.put(PdfName.CREATORINFO, localPdfDictionary2);
  }

  public void setLanguage(String paramString, boolean paramBoolean)
  {
    PdfDictionary localPdfDictionary1 = getUsage();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.LANG, new PdfString(paramString, "UnicodeBig"));
    if (paramBoolean)
      localPdfDictionary2.put(PdfName.PREFERRED, PdfName.ON);
    localPdfDictionary1.put(PdfName.LANGUAGE, localPdfDictionary2);
  }

  public void setExport(boolean paramBoolean)
  {
    PdfDictionary localPdfDictionary1 = getUsage();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.EXPORTSTATE, paramBoolean ? PdfName.ON : PdfName.OFF);
    localPdfDictionary1.put(PdfName.EXPORT, localPdfDictionary2);
  }

  public void setZoom(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 <= 0.0F) && (paramFloat2 < 0.0F))
      return;
    PdfDictionary localPdfDictionary1 = getUsage();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    if (paramFloat1 > 0.0F)
      localPdfDictionary2.put(PdfName.MIN_LOWER_CASE, new PdfNumber(paramFloat1));
    if (paramFloat2 >= 0.0F)
      localPdfDictionary2.put(PdfName.MAX_LOWER_CASE, new PdfNumber(paramFloat2));
    localPdfDictionary1.put(PdfName.ZOOM, localPdfDictionary2);
  }

  public void setPrint(String paramString, boolean paramBoolean)
  {
    PdfDictionary localPdfDictionary1 = getUsage();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.SUBTYPE, new PdfName(paramString));
    localPdfDictionary2.put(PdfName.PRINTSTATE, paramBoolean ? PdfName.ON : PdfName.OFF);
    localPdfDictionary1.put(PdfName.PRINT, localPdfDictionary2);
  }

  public void setView(boolean paramBoolean)
  {
    PdfDictionary localPdfDictionary1 = getUsage();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.VIEWSTATE, paramBoolean ? PdfName.ON : PdfName.OFF);
    localPdfDictionary1.put(PdfName.VIEW, localPdfDictionary2);
  }

  public boolean isOnPanel()
  {
    return this.onPanel;
  }

  public void setOnPanel(boolean paramBoolean)
  {
    this.onPanel = paramBoolean;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfLayer
 * JD-Core Version:    0.6.0
 */