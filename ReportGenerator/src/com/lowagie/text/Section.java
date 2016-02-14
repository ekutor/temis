package com.lowagie.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Section extends ArrayList
  implements TextElementArray, LargeElement
{
  public static final int NUMBERSTYLE_DOTTED = 0;
  public static final int NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT = 1;
  private static final long serialVersionUID = 3324172577544748043L;
  protected Paragraph title;
  protected String bookmarkTitle;
  protected int numberDepth;
  protected int numberStyle = 0;
  protected float indentationLeft;
  protected float indentationRight;
  protected float indentation;
  protected boolean bookmarkOpen = true;
  protected boolean triggerNewPage = false;
  protected int subsections = 0;
  protected ArrayList numbers = null;
  protected boolean complete = true;
  protected boolean addedCompletely = false;
  protected boolean notAddedYet = true;

  protected Section()
  {
    this.title = new Paragraph();
    this.numberDepth = 1;
  }

  protected Section(Paragraph paramParagraph, int paramInt)
  {
    this.numberDepth = paramInt;
    this.title = paramParagraph;
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      Iterator localIterator = iterator();
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

  public int type()
  {
    return 13;
  }

  public boolean isChapter()
  {
    return type() == 16;
  }

  public boolean isSection()
  {
    return type() == 13;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
      localArrayList.addAll(((Element)localIterator.next()).getChunks());
    return localArrayList;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return false;
  }

  public void add(int paramInt, Object paramObject)
  {
    if (isAddedCompletely())
      throw new IllegalStateException("This LargeElement has already been added to the Document.");
    try
    {
      Element localElement = (Element)paramObject;
      if (localElement.isNestable())
        super.add(paramInt, localElement);
      else
        throw new ClassCastException("You can't add a " + localElement.getClass().getName() + " to a Section.");
    }
    catch (ClassCastException localClassCastException)
    {
      throw new ClassCastException("Insertion of illegal Element: " + localClassCastException.getMessage());
    }
  }

  public boolean add(Object paramObject)
  {
    if (isAddedCompletely())
      throw new IllegalStateException("This LargeElement has already been added to the Document.");
    try
    {
      Element localElement = (Element)paramObject;
      Object localObject;
      if (localElement.type() == 13)
      {
        localObject = (Section)paramObject;
        ((Section)localObject).setNumbers(++this.subsections, this.numbers);
        return super.add(localObject);
      }
      if (((paramObject instanceof MarkedSection)) && (((MarkedObject)paramObject).element.type() == 13))
      {
        localObject = (MarkedSection)paramObject;
        Section localSection = (Section)((MarkedSection)localObject).element;
        localSection.setNumbers(++this.subsections, this.numbers);
        return super.add(localObject);
      }
      if (localElement.isNestable())
        return super.add(paramObject);
      throw new ClassCastException("You can't add a " + localElement.getClass().getName() + " to a Section.");
    }
    catch (ClassCastException localClassCastException)
    {
    }
    throw new ClassCastException("Insertion of illegal Element: " + localClassCastException.getMessage());
  }

  public boolean addAll(Collection paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
      add(localIterator.next());
    return true;
  }

  public Section addSection(float paramFloat, Paragraph paramParagraph, int paramInt)
  {
    if (isAddedCompletely())
      throw new IllegalStateException("This LargeElement has already been added to the Document.");
    Section localSection = new Section(paramParagraph, paramInt);
    localSection.setIndentation(paramFloat);
    add(localSection);
    return localSection;
  }

  public Section addSection(float paramFloat, Paragraph paramParagraph)
  {
    return addSection(paramFloat, paramParagraph, this.numberDepth + 1);
  }

  public Section addSection(Paragraph paramParagraph, int paramInt)
  {
    return addSection(0.0F, paramParagraph, paramInt);
  }

  public MarkedSection addMarkedSection()
  {
    MarkedSection localMarkedSection = new MarkedSection(new Section(null, this.numberDepth + 1));
    add(localMarkedSection);
    return localMarkedSection;
  }

  public Section addSection(Paragraph paramParagraph)
  {
    return addSection(0.0F, paramParagraph, this.numberDepth + 1);
  }

  public Section addSection(float paramFloat, String paramString, int paramInt)
  {
    return addSection(paramFloat, new Paragraph(paramString), paramInt);
  }

  public Section addSection(String paramString, int paramInt)
  {
    return addSection(new Paragraph(paramString), paramInt);
  }

  public Section addSection(float paramFloat, String paramString)
  {
    return addSection(paramFloat, new Paragraph(paramString));
  }

  public Section addSection(String paramString)
  {
    return addSection(new Paragraph(paramString));
  }

  public void setTitle(Paragraph paramParagraph)
  {
    this.title = paramParagraph;
  }

  public Paragraph getTitle()
  {
    return constructTitle(this.title, this.numbers, this.numberDepth, this.numberStyle);
  }

  public static Paragraph constructTitle(Paragraph paramParagraph, ArrayList paramArrayList, int paramInt1, int paramInt2)
  {
    if (paramParagraph == null)
      return null;
    int i = Math.min(paramArrayList.size(), paramInt1);
    if (i < 1)
      return paramParagraph;
    StringBuffer localStringBuffer = new StringBuffer(" ");
    for (int j = 0; j < i; j++)
    {
      localStringBuffer.insert(0, ".");
      localStringBuffer.insert(0, ((Integer)paramArrayList.get(j)).intValue());
    }
    if (paramInt2 == 1)
      localStringBuffer.deleteCharAt(localStringBuffer.length() - 2);
    Paragraph localParagraph = new Paragraph(paramParagraph);
    localParagraph.add(0, new Chunk(localStringBuffer.toString(), paramParagraph.getFont()));
    return localParagraph;
  }

  public void setNumberDepth(int paramInt)
  {
    this.numberDepth = paramInt;
  }

  public int getNumberDepth()
  {
    return this.numberDepth;
  }

  public void setNumberStyle(int paramInt)
  {
    this.numberStyle = paramInt;
  }

  public int getNumberStyle()
  {
    return this.numberStyle;
  }

  public void setIndentationLeft(float paramFloat)
  {
    this.indentationLeft = paramFloat;
  }

  public float getIndentationLeft()
  {
    return this.indentationLeft;
  }

  public void setIndentationRight(float paramFloat)
  {
    this.indentationRight = paramFloat;
  }

  public float getIndentationRight()
  {
    return this.indentationRight;
  }

  public void setIndentation(float paramFloat)
  {
    this.indentation = paramFloat;
  }

  public float getIndentation()
  {
    return this.indentation;
  }

  public void setBookmarkOpen(boolean paramBoolean)
  {
    this.bookmarkOpen = paramBoolean;
  }

  public boolean isBookmarkOpen()
  {
    return this.bookmarkOpen;
  }

  public void setTriggerNewPage(boolean paramBoolean)
  {
    this.triggerNewPage = paramBoolean;
  }

  public boolean isTriggerNewPage()
  {
    return (this.triggerNewPage) && (this.notAddedYet);
  }

  public void setBookmarkTitle(String paramString)
  {
    this.bookmarkTitle = paramString;
  }

  public Paragraph getBookmarkTitle()
  {
    if (this.bookmarkTitle == null)
      return getTitle();
    return new Paragraph(this.bookmarkTitle);
  }

  public void setChapterNumber(int paramInt)
  {
    this.numbers.set(this.numbers.size() - 1, new Integer(paramInt));
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (!(localObject instanceof Section))
        continue;
      ((Section)localObject).setChapterNumber(paramInt);
    }
  }

  public int getDepth()
  {
    return this.numbers.size();
  }

  private void setNumbers(int paramInt, ArrayList paramArrayList)
  {
    this.numbers = new ArrayList();
    this.numbers.add(new Integer(paramInt));
    this.numbers.addAll(paramArrayList);
  }

  public boolean isNotAddedYet()
  {
    return this.notAddedYet;
  }

  public void setNotAddedYet(boolean paramBoolean)
  {
    this.notAddedYet = paramBoolean;
  }

  protected boolean isAddedCompletely()
  {
    return this.addedCompletely;
  }

  protected void setAddedCompletely(boolean paramBoolean)
  {
    this.addedCompletely = paramBoolean;
  }

  public void flushContent()
  {
    setNotAddedYet(false);
    this.title = null;
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      if ((localElement instanceof Section))
      {
        Section localSection = (Section)localElement;
        if ((!localSection.isComplete()) && (size() == 1))
        {
          localSection.flushContent();
          return;
        }
        localSection.setAddedCompletely(true);
      }
      localIterator.remove();
    }
  }

  public boolean isComplete()
  {
    return this.complete;
  }

  public void setComplete(boolean paramBoolean)
  {
    this.complete = paramBoolean;
  }

  public void newPage()
  {
    add(Chunk.NEXTPAGE);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Section
 * JD-Core Version:    0.6.0
 */