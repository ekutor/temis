package com.lowagie.text;

import com.lowagie.text.factories.RomanAlphabetFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class List
  implements TextElementArray
{
  public static final boolean ORDERED = true;
  public static final boolean UNORDERED = false;
  public static final boolean NUMERICAL = false;
  public static final boolean ALPHABETICAL = true;
  public static final boolean UPPERCASE = false;
  public static final boolean LOWERCASE = true;
  protected ArrayList list = new ArrayList();
  protected boolean numbered = false;
  protected boolean lettered = false;
  protected boolean lowercase = false;
  protected boolean autoindent = false;
  protected boolean alignindent = false;
  protected int first = 1;
  protected Chunk symbol = new Chunk("- ");
  protected String preSymbol = "";
  protected String postSymbol = ". ";
  protected float indentationLeft = 0.0F;
  protected float indentationRight = 0.0F;
  protected float symbolIndent = 0.0F;

  public List()
  {
    this(false, false);
  }

  public List(float paramFloat)
  {
    this.symbolIndent = paramFloat;
  }

  public List(boolean paramBoolean)
  {
    this(paramBoolean, false);
  }

  public List(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.numbered = paramBoolean1;
    this.lettered = paramBoolean2;
    this.autoindent = true;
    this.alignindent = true;
  }

  public List(boolean paramBoolean, float paramFloat)
  {
    this(paramBoolean, false, paramFloat);
  }

  public List(boolean paramBoolean1, boolean paramBoolean2, float paramFloat)
  {
    this.numbered = paramBoolean1;
    this.lettered = paramBoolean2;
    this.symbolIndent = paramFloat;
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      Iterator localIterator = this.list.iterator();
      while (localIterator.hasNext())
        paramElementListener.add((Element)localIterator.next());
      return true;
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return 14;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.list.iterator();
    while (localIterator.hasNext())
      localArrayList.addAll(((Element)localIterator.next()).getChunks());
    return localArrayList;
  }

  public boolean add(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof ListItem))
    {
      localObject = (ListItem)paramObject;
      if ((this.numbered) || (this.lettered))
      {
        Chunk localChunk = new Chunk(this.preSymbol, this.symbol.getFont());
        int i = this.first + this.list.size();
        if (this.lettered)
          localChunk.append(RomanAlphabetFactory.getString(i, this.lowercase));
        else
          localChunk.append(String.valueOf(i));
        localChunk.append(this.postSymbol);
        ((ListItem)localObject).setListSymbol(localChunk);
      }
      else
      {
        ((ListItem)localObject).setListSymbol(this.symbol);
      }
      ((ListItem)localObject).setIndentationLeft(this.symbolIndent, this.autoindent);
      ((ListItem)localObject).setIndentationRight(0.0F);
      return this.list.add(localObject);
    }
    if ((paramObject instanceof List))
    {
      localObject = (List)paramObject;
      ((List)localObject).setIndentationLeft(((List)localObject).getIndentationLeft() + this.symbolIndent);
      this.first -= 1;
      return this.list.add(localObject);
    }
    if ((paramObject instanceof String))
      return add(new ListItem((String)paramObject));
    return false;
  }

  public void normalizeIndentation()
  {
    float f = 0.0F;
    Iterator localIterator = this.list.iterator();
    Element localElement;
    while (localIterator.hasNext())
    {
      localElement = (Element)localIterator.next();
      if (!(localElement instanceof ListItem))
        continue;
      f = Math.max(f, ((ListItem)localElement).getIndentationLeft());
    }
    localIterator = this.list.iterator();
    while (localIterator.hasNext())
    {
      localElement = (Element)localIterator.next();
      if (!(localElement instanceof ListItem))
        continue;
      ((ListItem)localElement).setIndentationLeft(f);
    }
  }

  public void setNumbered(boolean paramBoolean)
  {
    this.numbered = paramBoolean;
  }

  public void setLettered(boolean paramBoolean)
  {
    this.lettered = paramBoolean;
  }

  public void setLowercase(boolean paramBoolean)
  {
    this.lowercase = paramBoolean;
  }

  public void setAutoindent(boolean paramBoolean)
  {
    this.autoindent = paramBoolean;
  }

  public void setAlignindent(boolean paramBoolean)
  {
    this.alignindent = paramBoolean;
  }

  public void setFirst(int paramInt)
  {
    this.first = paramInt;
  }

  public void setListSymbol(Chunk paramChunk)
  {
    this.symbol = paramChunk;
  }

  public void setListSymbol(String paramString)
  {
    this.symbol = new Chunk(paramString);
  }

  public void setIndentationLeft(float paramFloat)
  {
    this.indentationLeft = paramFloat;
  }

  public void setIndentationRight(float paramFloat)
  {
    this.indentationRight = paramFloat;
  }

  public void setSymbolIndent(float paramFloat)
  {
    this.symbolIndent = paramFloat;
  }

  public ArrayList getItems()
  {
    return this.list;
  }

  public int size()
  {
    return this.list.size();
  }

  public boolean isEmpty()
  {
    return this.list.isEmpty();
  }

  public float getTotalLeading()
  {
    if (this.list.size() < 1)
      return -1.0F;
    ListItem localListItem = (ListItem)this.list.get(0);
    return localListItem.getTotalLeading();
  }

  public boolean isNumbered()
  {
    return this.numbered;
  }

  public boolean isLettered()
  {
    return this.lettered;
  }

  public boolean isLowercase()
  {
    return this.lowercase;
  }

  public boolean isAutoindent()
  {
    return this.autoindent;
  }

  public boolean isAlignindent()
  {
    return this.alignindent;
  }

  public int getFirst()
  {
    return this.first;
  }

  public Chunk getSymbol()
  {
    return this.symbol;
  }

  public float getIndentationLeft()
  {
    return this.indentationLeft;
  }

  public float getIndentationRight()
  {
    return this.indentationRight;
  }

  public float getSymbolIndent()
  {
    return this.symbolIndent;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
  }

  public String getPostSymbol()
  {
    return this.postSymbol;
  }

  public void setPostSymbol(String paramString)
  {
    this.postSymbol = paramString;
  }

  public String getPreSymbol()
  {
    return this.preSymbol;
  }

  public void setPreSymbol(String paramString)
  {
    this.preSymbol = paramString;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.List
 * JD-Core Version:    0.6.0
 */