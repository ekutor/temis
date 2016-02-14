package com.lowagie.text;

import com.lowagie.text.factories.RomanNumberFactory;
import java.util.ArrayList;

public class RomanList extends List
{
  public RomanList()
  {
    super(true);
  }

  public RomanList(int paramInt)
  {
    super(true, paramInt);
  }

  public RomanList(boolean paramBoolean, int paramInt)
  {
    super(true, paramInt);
    this.lowercase = paramBoolean;
  }

  public boolean add(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof ListItem))
    {
      localObject = (ListItem)paramObject;
      Chunk localChunk = new Chunk(this.preSymbol, this.symbol.getFont());
      localChunk.append(RomanNumberFactory.getString(this.first + this.list.size(), this.lowercase));
      localChunk.append(this.postSymbol);
      ((ListItem)localObject).setListSymbol(localChunk);
      ((ListItem)localObject).setIndentationLeft(this.symbolIndent, this.autoindent);
      ((ListItem)localObject).setIndentationRight(0.0F);
      this.list.add(localObject);
    }
    else
    {
      if ((paramObject instanceof List))
      {
        localObject = (List)paramObject;
        ((List)localObject).setIndentationLeft(((List)localObject).getIndentationLeft() + this.symbolIndent);
        this.first -= 1;
        return this.list.add(localObject);
      }
      if ((paramObject instanceof String))
        return add(new ListItem((String)paramObject));
    }
    return false;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.RomanList
 * JD-Core Version:    0.6.0
 */