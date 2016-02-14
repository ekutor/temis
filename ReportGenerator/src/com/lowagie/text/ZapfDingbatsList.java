package com.lowagie.text;

import java.util.ArrayList;

public class ZapfDingbatsList extends List
{
  protected int zn;

  public ZapfDingbatsList(int paramInt)
  {
    super(true);
    this.zn = paramInt;
    float f = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
    this.postSymbol = " ";
  }

  public ZapfDingbatsList(int paramInt1, int paramInt2)
  {
    super(true, paramInt2);
    this.zn = paramInt1;
    float f = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
    this.postSymbol = " ";
  }

  public void setCharNumber(int paramInt)
  {
    this.zn = paramInt;
  }

  public int getCharNumber()
  {
    return this.zn;
  }

  public boolean add(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof ListItem))
    {
      localObject = (ListItem)paramObject;
      Chunk localChunk = new Chunk(this.preSymbol, this.symbol.getFont());
      localChunk.append(String.valueOf((char)this.zn));
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
 * Qualified Name:     com.lowagie.text.ZapfDingbatsList
 * JD-Core Version:    0.6.0
 */