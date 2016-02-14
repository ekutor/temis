package com.lowagie.text;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Annotation
  implements Element
{
  public static final int TEXT = 0;
  public static final int URL_NET = 1;
  public static final int URL_AS_STRING = 2;
  public static final int FILE_DEST = 3;
  public static final int FILE_PAGE = 4;
  public static final int NAMED_DEST = 5;
  public static final int LAUNCH = 6;
  public static final int SCREEN = 7;
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String URL = "url";
  public static final String FILE = "file";
  public static final String DESTINATION = "destination";
  public static final String PAGE = "page";
  public static final String NAMED = "named";
  public static final String APPLICATION = "application";
  public static final String PARAMETERS = "parameters";
  public static final String OPERATION = "operation";
  public static final String DEFAULTDIR = "defaultdir";
  public static final String LLX = "llx";
  public static final String LLY = "lly";
  public static final String URX = "urx";
  public static final String URY = "ury";
  public static final String MIMETYPE = "mime";
  protected int annotationtype;
  protected HashMap annotationAttributes = new HashMap();
  protected float llx = (0.0F / 0.0F);
  protected float lly = (0.0F / 0.0F);
  protected float urx = (0.0F / 0.0F);
  protected float ury = (0.0F / 0.0F);

  private Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.llx = paramFloat1;
    this.lly = paramFloat2;
    this.urx = paramFloat3;
    this.ury = paramFloat4;
  }

  public Annotation(Annotation paramAnnotation)
  {
    this.annotationtype = paramAnnotation.annotationtype;
    this.annotationAttributes = paramAnnotation.annotationAttributes;
    this.llx = paramAnnotation.llx;
    this.lly = paramAnnotation.lly;
    this.urx = paramAnnotation.urx;
    this.ury = paramAnnotation.ury;
  }

  public Annotation(String paramString1, String paramString2)
  {
    this.annotationtype = 0;
    this.annotationAttributes.put("title", paramString1);
    this.annotationAttributes.put("content", paramString2);
  }

  public Annotation(String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 0;
    this.annotationAttributes.put("title", paramString1);
    this.annotationAttributes.put("content", paramString2);
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, URL paramURL)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 1;
    this.annotationAttributes.put("url", paramURL);
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, String paramString)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 2;
    this.annotationAttributes.put("file", paramString);
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, String paramString1, String paramString2)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 3;
    this.annotationAttributes.put("file", paramString1);
    this.annotationAttributes.put("destination", paramString2);
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, String paramString1, String paramString2, boolean paramBoolean)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 7;
    this.annotationAttributes.put("file", paramString1);
    this.annotationAttributes.put("mime", paramString2);
    this.annotationAttributes.put("parameters", new boolean[] { false, paramBoolean });
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, String paramString, int paramInt)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 4;
    this.annotationAttributes.put("file", paramString);
    this.annotationAttributes.put("page", new Integer(paramInt));
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 5;
    this.annotationAttributes.put("named", new Integer(paramInt));
  }

  public Annotation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.annotationtype = 6;
    this.annotationAttributes.put("application", paramString1);
    this.annotationAttributes.put("parameters", paramString2);
    this.annotationAttributes.put("operation", paramString3);
    this.annotationAttributes.put("defaultdir", paramString4);
  }

  public int type()
  {
    return 29;
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      return paramElementListener.add(this);
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public ArrayList getChunks()
  {
    return new ArrayList();
  }

  public void setDimensions(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.llx = paramFloat1;
    this.lly = paramFloat2;
    this.urx = paramFloat3;
    this.ury = paramFloat4;
  }

  public float llx()
  {
    return this.llx;
  }

  public float lly()
  {
    return this.lly;
  }

  public float urx()
  {
    return this.urx;
  }

  public float ury()
  {
    return this.ury;
  }

  public float llx(float paramFloat)
  {
    if (Float.isNaN(this.llx))
      return paramFloat;
    return this.llx;
  }

  public float lly(float paramFloat)
  {
    if (Float.isNaN(this.lly))
      return paramFloat;
    return this.lly;
  }

  public float urx(float paramFloat)
  {
    if (Float.isNaN(this.urx))
      return paramFloat;
    return this.urx;
  }

  public float ury(float paramFloat)
  {
    if (Float.isNaN(this.ury))
      return paramFloat;
    return this.ury;
  }

  public int annotationType()
  {
    return this.annotationtype;
  }

  public String title()
  {
    String str = (String)this.annotationAttributes.get("title");
    if (str == null)
      str = "";
    return str;
  }

  public String content()
  {
    String str = (String)this.annotationAttributes.get("content");
    if (str == null)
      str = "";
    return str;
  }

  public HashMap attributes()
  {
    return this.annotationAttributes;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Annotation
 * JD-Core Version:    0.6.0
 */