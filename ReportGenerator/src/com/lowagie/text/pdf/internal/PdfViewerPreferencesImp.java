package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;

public class PdfViewerPreferencesImp
  implements PdfViewerPreferences
{
  public static final PdfName[] VIEWER_PREFERENCES = { PdfName.HIDETOOLBAR, PdfName.HIDEMENUBAR, PdfName.HIDEWINDOWUI, PdfName.FITWINDOW, PdfName.CENTERWINDOW, PdfName.DISPLAYDOCTITLE, PdfName.NONFULLSCREENPAGEMODE, PdfName.DIRECTION, PdfName.VIEWAREA, PdfName.VIEWCLIP, PdfName.PRINTAREA, PdfName.PRINTCLIP, PdfName.PRINTSCALING, PdfName.DUPLEX, PdfName.PICKTRAYBYPDFSIZE, PdfName.PRINTPAGERANGE, PdfName.NUMCOPIES };
  public static final PdfName[] NONFULLSCREENPAGEMODE_PREFERENCES = { PdfName.USENONE, PdfName.USEOUTLINES, PdfName.USETHUMBS, PdfName.USEOC };
  public static final PdfName[] DIRECTION_PREFERENCES = { PdfName.L2R, PdfName.R2L };
  public static final PdfName[] PAGE_BOUNDARIES = { PdfName.MEDIABOX, PdfName.CROPBOX, PdfName.BLEEDBOX, PdfName.TRIMBOX, PdfName.ARTBOX };
  public static final PdfName[] PRINTSCALING_PREFERENCES = { PdfName.APPDEFAULT, PdfName.NONE };
  public static final PdfName[] DUPLEX_PREFERENCES = { PdfName.SIMPLEX, PdfName.DUPLEXFLIPSHORTEDGE, PdfName.DUPLEXFLIPLONGEDGE };
  private int pageLayoutAndMode = 0;
  private PdfDictionary viewerPreferences = new PdfDictionary();
  private static final int viewerPreferencesMask = 16773120;

  public int getPageLayoutAndMode()
  {
    return this.pageLayoutAndMode;
  }

  public PdfDictionary getViewerPreferences()
  {
    return this.viewerPreferences;
  }

  public void setViewerPreferences(int paramInt)
  {
    this.pageLayoutAndMode |= paramInt;
    if ((paramInt & 0xFFF000) != 0)
    {
      this.pageLayoutAndMode = (0xFF000FFF & this.pageLayoutAndMode);
      if ((paramInt & 0x1000) != 0)
        this.viewerPreferences.put(PdfName.HIDETOOLBAR, PdfBoolean.PDFTRUE);
      if ((paramInt & 0x2000) != 0)
        this.viewerPreferences.put(PdfName.HIDEMENUBAR, PdfBoolean.PDFTRUE);
      if ((paramInt & 0x4000) != 0)
        this.viewerPreferences.put(PdfName.HIDEWINDOWUI, PdfBoolean.PDFTRUE);
      if ((paramInt & 0x8000) != 0)
        this.viewerPreferences.put(PdfName.FITWINDOW, PdfBoolean.PDFTRUE);
      if ((paramInt & 0x10000) != 0)
        this.viewerPreferences.put(PdfName.CENTERWINDOW, PdfBoolean.PDFTRUE);
      if ((paramInt & 0x20000) != 0)
        this.viewerPreferences.put(PdfName.DISPLAYDOCTITLE, PdfBoolean.PDFTRUE);
      if ((paramInt & 0x40000) != 0)
        this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USENONE);
      else if ((paramInt & 0x80000) != 0)
        this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOUTLINES);
      else if ((paramInt & 0x100000) != 0)
        this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USETHUMBS);
      else if ((paramInt & 0x200000) != 0)
        this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOC);
      if ((paramInt & 0x400000) != 0)
        this.viewerPreferences.put(PdfName.DIRECTION, PdfName.L2R);
      else if ((paramInt & 0x800000) != 0)
        this.viewerPreferences.put(PdfName.DIRECTION, PdfName.R2L);
      if ((paramInt & 0x1000000) != 0)
        this.viewerPreferences.put(PdfName.PRINTSCALING, PdfName.NONE);
    }
  }

  private int getIndex(PdfName paramPdfName)
  {
    for (int i = 0; i < VIEWER_PREFERENCES.length; i++)
      if (VIEWER_PREFERENCES[i].equals(paramPdfName))
        return i;
    return -1;
  }

  private boolean isPossibleValue(PdfName paramPdfName, PdfName[] paramArrayOfPdfName)
  {
    for (int i = 0; i < paramArrayOfPdfName.length; i++)
      if (paramArrayOfPdfName[i].equals(paramPdfName))
        return true;
    return false;
  }

  public void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    switch (getIndex(paramPdfName))
    {
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 14:
      if (!(paramPdfObject instanceof PdfBoolean))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 6:
      if ((!(paramPdfObject instanceof PdfName)) || (!isPossibleValue((PdfName)paramPdfObject, NONFULLSCREENPAGEMODE_PREFERENCES)))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 7:
      if ((!(paramPdfObject instanceof PdfName)) || (!isPossibleValue((PdfName)paramPdfObject, DIRECTION_PREFERENCES)))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 8:
    case 9:
    case 10:
    case 11:
      if ((!(paramPdfObject instanceof PdfName)) || (!isPossibleValue((PdfName)paramPdfObject, PAGE_BOUNDARIES)))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 12:
      if ((!(paramPdfObject instanceof PdfName)) || (!isPossibleValue((PdfName)paramPdfObject, PRINTSCALING_PREFERENCES)))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 13:
      if ((!(paramPdfObject instanceof PdfName)) || (!isPossibleValue((PdfName)paramPdfObject, DUPLEX_PREFERENCES)))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 15:
      if (!(paramPdfObject instanceof PdfArray))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
      break;
    case 16:
      if (!(paramPdfObject instanceof PdfNumber))
        break;
      this.viewerPreferences.put(paramPdfName, paramPdfObject);
    }
  }

  public void addToCatalog(PdfDictionary paramPdfDictionary)
  {
    paramPdfDictionary.remove(PdfName.PAGELAYOUT);
    if ((this.pageLayoutAndMode & 0x1) != 0)
      paramPdfDictionary.put(PdfName.PAGELAYOUT, PdfName.SINGLEPAGE);
    else if ((this.pageLayoutAndMode & 0x2) != 0)
      paramPdfDictionary.put(PdfName.PAGELAYOUT, PdfName.ONECOLUMN);
    else if ((this.pageLayoutAndMode & 0x4) != 0)
      paramPdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNLEFT);
    else if ((this.pageLayoutAndMode & 0x8) != 0)
      paramPdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNRIGHT);
    else if ((this.pageLayoutAndMode & 0x10) != 0)
      paramPdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOPAGELEFT);
    else if ((this.pageLayoutAndMode & 0x20) != 0)
      paramPdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOPAGERIGHT);
    paramPdfDictionary.remove(PdfName.PAGEMODE);
    if ((this.pageLayoutAndMode & 0x40) != 0)
      paramPdfDictionary.put(PdfName.PAGEMODE, PdfName.USENONE);
    else if ((this.pageLayoutAndMode & 0x80) != 0)
      paramPdfDictionary.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
    else if ((this.pageLayoutAndMode & 0x100) != 0)
      paramPdfDictionary.put(PdfName.PAGEMODE, PdfName.USETHUMBS);
    else if ((this.pageLayoutAndMode & 0x200) != 0)
      paramPdfDictionary.put(PdfName.PAGEMODE, PdfName.FULLSCREEN);
    else if ((this.pageLayoutAndMode & 0x400) != 0)
      paramPdfDictionary.put(PdfName.PAGEMODE, PdfName.USEOC);
    else if ((this.pageLayoutAndMode & 0x800) != 0)
      paramPdfDictionary.put(PdfName.PAGEMODE, PdfName.USEATTACHMENTS);
    paramPdfDictionary.remove(PdfName.VIEWERPREFERENCES);
    if (this.viewerPreferences.size() > 0)
      paramPdfDictionary.put(PdfName.VIEWERPREFERENCES, this.viewerPreferences);
  }

  public static PdfViewerPreferencesImp getViewerPreferences(PdfDictionary paramPdfDictionary)
  {
    PdfViewerPreferencesImp localPdfViewerPreferencesImp = new PdfViewerPreferencesImp();
    int i = 0;
    PdfName localPdfName = null;
    PdfObject localPdfObject = PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.PAGELAYOUT));
    if ((localPdfObject != null) && (localPdfObject.isName()))
    {
      localPdfName = (PdfName)localPdfObject;
      if (localPdfName.equals(PdfName.SINGLEPAGE))
        i |= 1;
      else if (localPdfName.equals(PdfName.ONECOLUMN))
        i |= 2;
      else if (localPdfName.equals(PdfName.TWOCOLUMNLEFT))
        i |= 4;
      else if (localPdfName.equals(PdfName.TWOCOLUMNRIGHT))
        i |= 8;
      else if (localPdfName.equals(PdfName.TWOPAGELEFT))
        i |= 16;
      else if (localPdfName.equals(PdfName.TWOPAGERIGHT))
        i |= 32;
    }
    localPdfObject = PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.PAGEMODE));
    if ((localPdfObject != null) && (localPdfObject.isName()))
    {
      localPdfName = (PdfName)localPdfObject;
      if (localPdfName.equals(PdfName.USENONE))
        i |= 64;
      else if (localPdfName.equals(PdfName.USEOUTLINES))
        i |= 128;
      else if (localPdfName.equals(PdfName.USETHUMBS))
        i |= 256;
      else if (localPdfName.equals(PdfName.FULLSCREEN))
        i |= 512;
      else if (localPdfName.equals(PdfName.USEOC))
        i |= 1024;
      else if (localPdfName.equals(PdfName.USEATTACHMENTS))
        i |= 2048;
    }
    localPdfViewerPreferencesImp.setViewerPreferences(i);
    localPdfObject = PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.VIEWERPREFERENCES));
    if ((localPdfObject != null) && (localPdfObject.isDictionary()))
    {
      PdfDictionary localPdfDictionary = (PdfDictionary)localPdfObject;
      for (int j = 0; j < VIEWER_PREFERENCES.length; j++)
      {
        localPdfObject = PdfReader.getPdfObjectRelease(localPdfDictionary.get(VIEWER_PREFERENCES[j]));
        localPdfViewerPreferencesImp.addViewerPreference(VIEWER_PREFERENCES[j], localPdfObject);
      }
    }
    return localPdfViewerPreferencesImp;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.internal.PdfViewerPreferencesImp
 * JD-Core Version:    0.6.0
 */