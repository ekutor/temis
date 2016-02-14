package com.lowagie.text.xml.xmp;

public class PdfA1Schema extends XmpSchema
{
  private static final long serialVersionUID = 5300646133692948168L;
  public static final String DEFAULT_XPATH_ID = "pdfaid";
  public static final String DEFAULT_XPATH_URI = "http://www.aiim.org/pdfa/ns/id/";
  public static final String PART = "pdfaid:part";
  public static final String CONFORMANCE = "pdfaid:conformance";

  public PdfA1Schema()
  {
    super("xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\"");
    addPart("1");
  }

  public void addPart(String paramString)
  {
    setProperty("pdfaid:part", paramString);
  }

  public void addConformance(String paramString)
  {
    setProperty("pdfaid:conformance", paramString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.PdfA1Schema
 * JD-Core Version:    0.6.0
 */