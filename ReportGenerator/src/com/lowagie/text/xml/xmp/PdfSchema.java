package com.lowagie.text.xml.xmp;

import com.lowagie.text.Document;

public class PdfSchema extends XmpSchema
{
  private static final long serialVersionUID = -1541148669123992185L;
  public static final String DEFAULT_XPATH_ID = "pdf";
  public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/pdf/1.3/";
  public static final String KEYWORDS = "pdf:keywords";
  public static final String VERSION = "pdf:PDFVersion";
  public static final String PRODUCER = "pdf:Producer";

  public PdfSchema()
  {
    super("xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\"");
    addProducer(Document.getVersion());
  }

  public void addKeywords(String paramString)
  {
    setProperty("pdf:keywords", paramString);
  }

  public void addProducer(String paramString)
  {
    setProperty("pdf:Producer", paramString);
  }

  public void addVersion(String paramString)
  {
    setProperty("pdf:PDFVersion", paramString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.PdfSchema
 * JD-Core Version:    0.6.0
 */