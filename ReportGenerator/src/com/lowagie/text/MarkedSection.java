package com.lowagie.text;

import java.util.Collection;
import java.util.Iterator;

public class MarkedSection extends MarkedObject
{
  protected MarkedObject title = null;

  public MarkedSection(Section paramSection)
  {
    if (paramSection.title != null)
    {
      this.title = new MarkedObject(paramSection.title);
      paramSection.setTitle(null);
    }
    this.element = paramSection;
  }

  public void add(int paramInt, Object paramObject)
  {
    ((Section)this.element).add(paramInt, paramObject);
  }

  public boolean add(Object paramObject)
  {
    return ((Section)this.element).add(paramObject);
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      Iterator localIterator = ((Section)this.element).iterator();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        paramElementListener.add(localElement);
      }
      return true;
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public boolean addAll(Collection paramCollection)
  {
    return ((Section)this.element).addAll(paramCollection);
  }

  public MarkedSection addSection(float paramFloat, int paramInt)
  {
    MarkedSection localMarkedSection = ((Section)this.element).addMarkedSection();
    localMarkedSection.setIndentation(paramFloat);
    localMarkedSection.setNumberDepth(paramInt);
    return localMarkedSection;
  }

  public MarkedSection addSection(float paramFloat)
  {
    MarkedSection localMarkedSection = ((Section)this.element).addMarkedSection();
    localMarkedSection.setIndentation(paramFloat);
    return localMarkedSection;
  }

  public MarkedSection addSection(int paramInt)
  {
    MarkedSection localMarkedSection = ((Section)this.element).addMarkedSection();
    localMarkedSection.setNumberDepth(paramInt);
    return localMarkedSection;
  }

  public MarkedSection addSection()
  {
    return ((Section)this.element).addMarkedSection();
  }

  public void setTitle(MarkedObject paramMarkedObject)
  {
    if ((paramMarkedObject.element instanceof Paragraph))
      this.title = paramMarkedObject;
  }

  public MarkedObject getTitle()
  {
    Paragraph localParagraph = Section.constructTitle((Paragraph)this.title.element, ((Section)this.element).numbers, ((Section)this.element).numberDepth, ((Section)this.element).numberStyle);
    MarkedObject localMarkedObject = new MarkedObject(localParagraph);
    localMarkedObject.markupAttributes = this.title.markupAttributes;
    return localMarkedObject;
  }

  public void setNumberDepth(int paramInt)
  {
    ((Section)this.element).setNumberDepth(paramInt);
  }

  public void setIndentationLeft(float paramFloat)
  {
    ((Section)this.element).setIndentationLeft(paramFloat);
  }

  public void setIndentationRight(float paramFloat)
  {
    ((Section)this.element).setIndentationRight(paramFloat);
  }

  public void setIndentation(float paramFloat)
  {
    ((Section)this.element).setIndentation(paramFloat);
  }

  public void setBookmarkOpen(boolean paramBoolean)
  {
    ((Section)this.element).setBookmarkOpen(paramBoolean);
  }

  public void setTriggerNewPage(boolean paramBoolean)
  {
    ((Section)this.element).setTriggerNewPage(paramBoolean);
  }

  public void setBookmarkTitle(String paramString)
  {
    ((Section)this.element).setBookmarkTitle(paramString);
  }

  public void newPage()
  {
    ((Section)this.element).newPage();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.MarkedSection
 * JD-Core Version:    0.6.0
 */