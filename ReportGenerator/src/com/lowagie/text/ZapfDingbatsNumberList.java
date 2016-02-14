package com.lowagie.text;

import java.util.ArrayList;

public class ZapfDingbatsNumberList extends List
{
  protected int type;

  public ZapfDingbatsNumberList(int paramInt)
  {
    super(true);
    this.type = paramInt;
    float f = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
    this.postSymbol = " ";
  }

  public ZapfDingbatsNumberList(int paramInt1, int paramInt2)
  {
    super(true, paramInt2);
    this.type = paramInt1;
    float f = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("ZapfDingbats", f, 0));
    this.postSymbol = " ";
  }

  public void setType(int paramInt)
  {
    this.type = paramInt;
  }

  public int getType()
  {
    return this.type;
  }

  public boolean add(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof ListItem))
    {
      localObject = (ListItem)paramObject;
      Chunk localChunk = new Chunk(this.preSymbol, this.symbol.getFont());
      switch (this.type)
      {
      case 0:
        localChunk.append(String.valueOf((char)(this.first + this.list.size() + 171)));
        break;
      case 1:
        localChunk.append(String.valueOf((char)(this.first + this.list.size() + 181)));
        break;
      case 2:
        localChunk.append(String.valueOf((char)(this.first + this.list.size() + 191)));
        break;
      default:
        localChunk.append(String.valueOf((char)(this.first + this.list.size() + 201)));
      }
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
 * Qualified Name:     com.lowagie.text.ZapfDingbatsNumberList
 * JD-Core Version:    0.6.0
 */