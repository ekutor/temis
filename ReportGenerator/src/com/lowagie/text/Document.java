package com.lowagie.text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Document
  implements DocListener
{
  private static final String ITEXT = "iText";
  private static final String RELEASE = "2.1.7";
  private static final String ITEXT_VERSION = "iText 2.1.7 by 1T3XT";
  public static boolean compress = true;
  public static boolean plainRandomAccess = false;
  public static float wmfFontCorrection = 0.86F;
  private ArrayList listeners = new ArrayList();
  protected boolean open;
  protected boolean close;
  protected Rectangle pageSize;
  protected float marginLeft = 0.0F;
  protected float marginRight = 0.0F;
  protected float marginTop = 0.0F;
  protected float marginBottom = 0.0F;
  protected boolean marginMirroring = false;
  protected boolean marginMirroringTopBottom = false;
  protected String javaScript_onLoad = null;
  protected String javaScript_onUnLoad = null;
  protected String htmlStyleClass = null;
  protected int pageN = 0;
  protected HeaderFooter header = null;
  protected HeaderFooter footer = null;
  protected int chapternumber = 0;

  public Document()
  {
    this(PageSize.A4);
  }

  public Document(Rectangle paramRectangle)
  {
    this(paramRectangle, 36.0F, 36.0F, 36.0F, 36.0F);
  }

  public Document(Rectangle paramRectangle, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.pageSize = paramRectangle;
    this.marginLeft = paramFloat1;
    this.marginRight = paramFloat2;
    this.marginTop = paramFloat3;
    this.marginBottom = paramFloat4;
  }

  public void addDocListener(DocListener paramDocListener)
  {
    this.listeners.add(paramDocListener);
  }

  public void removeDocListener(DocListener paramDocListener)
  {
    this.listeners.remove(paramDocListener);
  }

  public boolean add(Element paramElement)
    throws DocumentException
  {
    if (this.close)
      throw new DocumentException("The document has been closed. You can't add any Elements.");
    if ((!this.open) && (paramElement.isContent()))
      throw new DocumentException("The document is not open yet; you can only add Meta information.");
    boolean bool = false;
    if ((paramElement instanceof ChapterAutoNumber))
      this.chapternumber = ((ChapterAutoNumber)paramElement).setAutomaticNumber(this.chapternumber);
    Object localObject = this.listeners.iterator();
    while (((Iterator)localObject).hasNext())
    {
      DocListener localDocListener = (DocListener)((Iterator)localObject).next();
      bool |= localDocListener.add(paramElement);
    }
    if ((paramElement instanceof LargeElement))
    {
      localObject = (LargeElement)paramElement;
      if (!((LargeElement)localObject).isComplete())
        ((LargeElement)localObject).flushContent();
    }
    return bool;
  }

  public void open()
  {
    if (!this.close)
      this.open = true;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setPageSize(this.pageSize);
      localDocListener.setMargins(this.marginLeft, this.marginRight, this.marginTop, this.marginBottom);
      localDocListener.open();
    }
  }

  public boolean setPageSize(Rectangle paramRectangle)
  {
    this.pageSize = paramRectangle;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setPageSize(paramRectangle);
    }
    return true;
  }

  public boolean setMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.marginLeft = paramFloat1;
    this.marginRight = paramFloat2;
    this.marginTop = paramFloat3;
    this.marginBottom = paramFloat4;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setMargins(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    }
    return true;
  }

  public boolean newPage()
  {
    if ((!this.open) || (this.close))
      return false;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.newPage();
    }
    return true;
  }

  public void setHeader(HeaderFooter paramHeaderFooter)
  {
    this.header = paramHeaderFooter;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setHeader(paramHeaderFooter);
    }
  }

  public void resetHeader()
  {
    this.header = null;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.resetHeader();
    }
  }

  public void setFooter(HeaderFooter paramHeaderFooter)
  {
    this.footer = paramHeaderFooter;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setFooter(paramHeaderFooter);
    }
  }

  public void resetFooter()
  {
    this.footer = null;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.resetFooter();
    }
  }

  public void resetPageCount()
  {
    this.pageN = 0;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.resetPageCount();
    }
  }

  public void setPageCount(int paramInt)
  {
    this.pageN = paramInt;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setPageCount(paramInt);
    }
  }

  public int getPageNumber()
  {
    return this.pageN;
  }

  public void close()
  {
    if (!this.close)
    {
      this.open = false;
      this.close = true;
    }
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.close();
    }
  }

  public boolean addHeader(String paramString1, String paramString2)
  {
    try
    {
      return add(new Header(paramString1, paramString2));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addTitle(String paramString)
  {
    try
    {
      return add(new Meta(1, paramString));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addSubject(String paramString)
  {
    try
    {
      return add(new Meta(2, paramString));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addKeywords(String paramString)
  {
    try
    {
      return add(new Meta(3, paramString));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addAuthor(String paramString)
  {
    try
    {
      return add(new Meta(4, paramString));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addCreator(String paramString)
  {
    try
    {
      return add(new Meta(7, paramString));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addProducer()
  {
    try
    {
      return add(new Meta(5, getVersion()));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public boolean addCreationDate()
  {
    try
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
      return add(new Meta(6, localSimpleDateFormat.format(new Date())));
    }
    catch (DocumentException localDocumentException)
    {
    }
    throw new ExceptionConverter(localDocumentException);
  }

  public float leftMargin()
  {
    return this.marginLeft;
  }

  public float rightMargin()
  {
    return this.marginRight;
  }

  public float topMargin()
  {
    return this.marginTop;
  }

  public float bottomMargin()
  {
    return this.marginBottom;
  }

  public float left()
  {
    return this.pageSize.getLeft(this.marginLeft);
  }

  public float right()
  {
    return this.pageSize.getRight(this.marginRight);
  }

  public float top()
  {
    return this.pageSize.getTop(this.marginTop);
  }

  public float bottom()
  {
    return this.pageSize.getBottom(this.marginBottom);
  }

  public float left(float paramFloat)
  {
    return this.pageSize.getLeft(this.marginLeft + paramFloat);
  }

  public float right(float paramFloat)
  {
    return this.pageSize.getRight(this.marginRight + paramFloat);
  }

  public float top(float paramFloat)
  {
    return this.pageSize.getTop(this.marginTop + paramFloat);
  }

  public float bottom(float paramFloat)
  {
    return this.pageSize.getBottom(this.marginBottom + paramFloat);
  }

  public Rectangle getPageSize()
  {
    return this.pageSize;
  }

  public boolean isOpen()
  {
    return this.open;
  }

  public static final String getProduct()
  {
    return "iText";
  }

  public static final String getRelease()
  {
    return "2.1.7";
  }

  public static final String getVersion()
  {
    return "iText 2.1.7 by 1T3XT";
  }

  public void setJavaScript_onLoad(String paramString)
  {
    this.javaScript_onLoad = paramString;
  }

  public String getJavaScript_onLoad()
  {
    return this.javaScript_onLoad;
  }

  public void setJavaScript_onUnLoad(String paramString)
  {
    this.javaScript_onUnLoad = paramString;
  }

  public String getJavaScript_onUnLoad()
  {
    return this.javaScript_onUnLoad;
  }

  public void setHtmlStyleClass(String paramString)
  {
    this.htmlStyleClass = paramString;
  }

  public String getHtmlStyleClass()
  {
    return this.htmlStyleClass;
  }

  public boolean setMarginMirroring(boolean paramBoolean)
  {
    this.marginMirroring = paramBoolean;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setMarginMirroring(paramBoolean);
    }
    return true;
  }

  public boolean setMarginMirroringTopBottom(boolean paramBoolean)
  {
    this.marginMirroringTopBottom = paramBoolean;
    Iterator localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      DocListener localDocListener = (DocListener)localIterator.next();
      localDocListener.setMarginMirroringTopBottom(paramBoolean);
    }
    return true;
  }

  public boolean isMarginMirroring()
  {
    return this.marginMirroring;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Document
 * JD-Core Version:    0.6.0
 */