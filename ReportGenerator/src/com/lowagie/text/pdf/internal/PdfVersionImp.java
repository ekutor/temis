package com.lowagie.text.pdf.internal;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.interfaces.PdfVersion;
import java.io.IOException;

public class PdfVersionImp
  implements PdfVersion
{
  public static final byte[][] HEADER = { DocWriter.getISOBytes("\n"), DocWriter.getISOBytes("%PDF-"), DocWriter.getISOBytes("\n%âãÏÓ\n") };
  protected boolean headerWasWritten = false;
  protected boolean appendmode = false;
  protected char header_version = '4';
  protected PdfName catalog_version = null;
  protected PdfDictionary extensions = null;

  public void setPdfVersion(char paramChar)
  {
    if ((this.headerWasWritten) || (this.appendmode))
      setPdfVersion(getVersionAsName(paramChar));
    else
      this.header_version = paramChar;
  }

  public void setAtLeastPdfVersion(char paramChar)
  {
    if (paramChar > this.header_version)
      setPdfVersion(paramChar);
  }

  public void setPdfVersion(PdfName paramPdfName)
  {
    if ((this.catalog_version == null) || (this.catalog_version.compareTo(paramPdfName) < 0))
      this.catalog_version = paramPdfName;
  }

  public void setAppendmode(boolean paramBoolean)
  {
    this.appendmode = paramBoolean;
  }

  public void writeHeader(OutputStreamCounter paramOutputStreamCounter)
    throws IOException
  {
    if (this.appendmode)
    {
      paramOutputStreamCounter.write(HEADER[0]);
    }
    else
    {
      paramOutputStreamCounter.write(HEADER[1]);
      paramOutputStreamCounter.write(getVersionAsByteArray(this.header_version));
      paramOutputStreamCounter.write(HEADER[2]);
      this.headerWasWritten = true;
    }
  }

  public PdfName getVersionAsName(char paramChar)
  {
    switch (paramChar)
    {
    case '2':
      return PdfWriter.PDF_VERSION_1_2;
    case '3':
      return PdfWriter.PDF_VERSION_1_3;
    case '4':
      return PdfWriter.PDF_VERSION_1_4;
    case '5':
      return PdfWriter.PDF_VERSION_1_5;
    case '6':
      return PdfWriter.PDF_VERSION_1_6;
    case '7':
      return PdfWriter.PDF_VERSION_1_7;
    }
    return PdfWriter.PDF_VERSION_1_4;
  }

  public byte[] getVersionAsByteArray(char paramChar)
  {
    return DocWriter.getISOBytes(getVersionAsName(paramChar).toString().substring(1));
  }

  public void addToCatalog(PdfDictionary paramPdfDictionary)
  {
    if (this.catalog_version != null)
      paramPdfDictionary.put(PdfName.VERSION, this.catalog_version);
    if (this.extensions != null)
      paramPdfDictionary.put(PdfName.EXTENSIONS, this.extensions);
  }

  public void addDeveloperExtension(PdfDeveloperExtension paramPdfDeveloperExtension)
  {
    if (this.extensions == null)
    {
      this.extensions = new PdfDictionary();
    }
    else
    {
      PdfDictionary localPdfDictionary = this.extensions.getAsDict(paramPdfDeveloperExtension.getPrefix());
      if (localPdfDictionary != null)
      {
        int i = paramPdfDeveloperExtension.getBaseversion().compareTo(localPdfDictionary.getAsName(PdfName.BASEVERSION));
        if (i < 0)
          return;
        i = paramPdfDeveloperExtension.getExtensionLevel() - localPdfDictionary.getAsNumber(PdfName.EXTENSIONLEVEL).intValue();
        if (i <= 0)
          return;
      }
    }
    this.extensions.put(paramPdfDeveloperExtension.getPrefix(), paramPdfDeveloperExtension.getDeveloperExtensions());
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.internal.PdfVersionImp
 * JD-Core Version:    0.6.0
 */