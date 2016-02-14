package com.lowagie.text.xml.xmp;

public class DublinCoreSchema extends XmpSchema
{
  private static final long serialVersionUID = -4551741356374797330L;
  public static final String DEFAULT_XPATH_ID = "dc";
  public static final String DEFAULT_XPATH_URI = "http://purl.org/dc/elements/1.1/";
  public static final String CONTRIBUTOR = "dc:contributor";
  public static final String COVERAGE = "dc:coverage";
  public static final String CREATOR = "dc:creator";
  public static final String DATE = "dc:date";
  public static final String DESCRIPTION = "dc:description";
  public static final String FORMAT = "dc:format";
  public static final String IDENTIFIER = "dc:identifier";
  public static final String LANGUAGE = "dc:language";
  public static final String PUBLISHER = "dc:publisher";
  public static final String RELATION = "dc:relation";
  public static final String RIGHTS = "dc:rights";
  public static final String SOURCE = "dc:source";
  public static final String SUBJECT = "dc:subject";
  public static final String TITLE = "dc:title";
  public static final String TYPE = "dc:type";

  public DublinCoreSchema()
  {
    super("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
    setProperty("dc:format", "application/pdf");
  }

  public void addTitle(String paramString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Alt");
    localXmpArray.add(paramString);
    setProperty("dc:title", localXmpArray);
  }

  public void addDescription(String paramString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Alt");
    localXmpArray.add(paramString);
    setProperty("dc:description", localXmpArray);
  }

  public void addSubject(String paramString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Bag");
    localXmpArray.add(paramString);
    setProperty("dc:subject", localXmpArray);
  }

  public void addSubject(String[] paramArrayOfString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Bag");
    for (int i = 0; i < paramArrayOfString.length; i++)
      localXmpArray.add(paramArrayOfString[i]);
    setProperty("dc:subject", localXmpArray);
  }

  public void addAuthor(String paramString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Seq");
    localXmpArray.add(paramString);
    setProperty("dc:creator", localXmpArray);
  }

  public void addAuthor(String[] paramArrayOfString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Seq");
    for (int i = 0; i < paramArrayOfString.length; i++)
      localXmpArray.add(paramArrayOfString[i]);
    setProperty("dc:creator", localXmpArray);
  }

  public void addPublisher(String paramString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Seq");
    localXmpArray.add(paramString);
    setProperty("dc:publisher", localXmpArray);
  }

  public void addPublisher(String[] paramArrayOfString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Seq");
    for (int i = 0; i < paramArrayOfString.length; i++)
      localXmpArray.add(paramArrayOfString[i]);
    setProperty("dc:publisher", localXmpArray);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.DublinCoreSchema
 * JD-Core Version:    0.6.0
 */