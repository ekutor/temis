package com.lowagie.text.xml.xmp;

public class XmpBasicSchema extends XmpSchema
{
  private static final long serialVersionUID = -2416613941622479298L;
  public static final String DEFAULT_XPATH_ID = "xmp";
  public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/xap/1.0/";
  public static final String ADVISORY = "xmp:Advisory";
  public static final String BASEURL = "xmp:BaseURL";
  public static final String CREATEDATE = "xmp:CreateDate";
  public static final String CREATORTOOL = "xmp:CreatorTool";
  public static final String IDENTIFIER = "xmp:Identifier";
  public static final String METADATADATE = "xmp:MetadataDate";
  public static final String MODIFYDATE = "xmp:ModifyDate";
  public static final String NICKNAME = "xmp:Nickname";
  public static final String THUMBNAILS = "xmp:Thumbnails";

  public XmpBasicSchema()
  {
    super("xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"");
  }

  public void addCreatorTool(String paramString)
  {
    setProperty("xmp:CreatorTool", paramString);
  }

  public void addCreateDate(String paramString)
  {
    setProperty("xmp:CreateDate", paramString);
  }

  public void addModDate(String paramString)
  {
    setProperty("xmp:ModifyDate", paramString);
  }

  public void addMetaDataDate(String paramString)
  {
    setProperty("xmp:MetadataDate", paramString);
  }

  public void addIdentifiers(String[] paramArrayOfString)
  {
    XmpArray localXmpArray = new XmpArray("rdf:Bag");
    for (int i = 0; i < paramArrayOfString.length; i++)
      localXmpArray.add(paramArrayOfString[i]);
    setProperty("xmp:Identifier", localXmpArray);
  }

  public void addNickname(String paramString)
  {
    setProperty("xmp:Nickname", paramString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.XmpBasicSchema
 * JD-Core Version:    0.6.0
 */