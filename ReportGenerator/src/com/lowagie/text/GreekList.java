package com.lowagie.text;

import com.lowagie.text.factories.GreekAlphabetFactory;
import java.util.ArrayList;

public class GreekList extends List
{
  public GreekList()
  {
    super(true);
    setGreekFont();
  }

  public GreekList(int paramInt)
  {
    super(true, paramInt);
    setGreekFont();
  }

  public GreekList(boolean paramBoolean, int paramInt)
  {
    super(true, paramInt);
    this.lowercase = paramBoolean;
    setGreekFont();
  }

  protected void setGreekFont()
  {
    float f = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("Symbol", f, 0));
  }

  public boolean add(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof ListItem))
    {
      localObject = (ListItem)paramObject;
      Chunk localChunk = new Chunk(this.preSymbol, this.symbol.getFont());
      localChunk.append(GreekAlphabetFactory.getString(this.first + this.list.size(), this.lowercase));
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
 * Qualified Name:     com.lowagie.text.GreekList
 * JD-Core Version:    0.6.0
 */