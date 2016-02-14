package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;

public class PdfTargetDictionary extends PdfDictionary
{
  public PdfTargetDictionary(PdfTargetDictionary paramPdfTargetDictionary)
  {
    put(PdfName.R, PdfName.P);
    if (paramPdfTargetDictionary != null)
      setAdditionalPath(paramPdfTargetDictionary);
  }

  public PdfTargetDictionary(boolean paramBoolean)
  {
    if (paramBoolean)
      put(PdfName.R, PdfName.C);
    else
      put(PdfName.R, PdfName.P);
  }

  public void setEmbeddedFileName(String paramString)
  {
    put(PdfName.N, new PdfString(paramString, null));
  }

  public void setFileAttachmentPagename(String paramString)
  {
    put(PdfName.P, new PdfString(paramString, null));
  }

  public void setFileAttachmentPage(int paramInt)
  {
    put(PdfName.P, new PdfNumber(paramInt));
  }

  public void setFileAttachmentName(String paramString)
  {
    put(PdfName.A, new PdfString(paramString, "UnicodeBig"));
  }

  public void setFileAttachmentIndex(int paramInt)
  {
    put(PdfName.A, new PdfNumber(paramInt));
  }

  public void setAdditionalPath(PdfTargetDictionary paramPdfTargetDictionary)
  {
    put(PdfName.T, paramPdfTargetDictionary);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.collection.PdfTargetDictionary
 * JD-Core Version:    0.6.0
 */