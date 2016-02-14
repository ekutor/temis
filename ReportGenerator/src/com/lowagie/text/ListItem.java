package com.lowagie.text;

public class ListItem extends Paragraph
{
  private static final long serialVersionUID = 1970670787169329006L;
  private Chunk symbol;

  public ListItem()
  {
  }

  public ListItem(float paramFloat)
  {
    super(paramFloat);
  }

  public ListItem(Chunk paramChunk)
  {
    super(paramChunk);
  }

  public ListItem(String paramString)
  {
    super(paramString);
  }

  public ListItem(String paramString, Font paramFont)
  {
    super(paramString, paramFont);
  }

  public ListItem(float paramFloat, Chunk paramChunk)
  {
    super(paramFloat, paramChunk);
  }

  public ListItem(float paramFloat, String paramString)
  {
    super(paramFloat, paramString);
  }

  public ListItem(float paramFloat, String paramString, Font paramFont)
  {
    super(paramFloat, paramString, paramFont);
  }

  public ListItem(Phrase paramPhrase)
  {
    super(paramPhrase);
  }

  public int type()
  {
    return 15;
  }

  public void setListSymbol(Chunk paramChunk)
  {
    if (this.symbol == null)
    {
      this.symbol = paramChunk;
      if (this.symbol.getFont().isStandardFont())
        this.symbol.setFont(this.font);
    }
  }

  public void setIndentationLeft(float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean)
      setIndentationLeft(getListSymbol().getWidthPoint());
    else
      setIndentationLeft(paramFloat);
  }

  public Chunk getListSymbol()
  {
    return this.symbol;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ListItem
 * JD-Core Version:    0.6.0
 */