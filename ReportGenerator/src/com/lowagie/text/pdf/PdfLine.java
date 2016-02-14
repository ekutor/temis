package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfLine
{
  protected ArrayList line;
  protected float left;
  protected float width;
  protected int alignment;
  protected float height;
  protected Chunk listSymbol = null;
  protected float symbolIndent;
  protected boolean newlineSplit = false;
  protected float originalWidth;
  protected boolean isRTL = false;

  PdfLine(float paramFloat1, float paramFloat2, int paramInt, float paramFloat3)
  {
    this.left = paramFloat1;
    this.width = (paramFloat2 - paramFloat1);
    this.originalWidth = this.width;
    this.alignment = paramInt;
    this.height = paramFloat3;
    this.line = new ArrayList();
  }

  PdfLine(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, boolean paramBoolean1, ArrayList paramArrayList, boolean paramBoolean2)
  {
    this.left = paramFloat1;
    this.originalWidth = paramFloat2;
    this.width = paramFloat3;
    this.alignment = paramInt;
    this.line = paramArrayList;
    this.newlineSplit = paramBoolean1;
    this.isRTL = paramBoolean2;
  }

  PdfChunk add(PdfChunk paramPdfChunk)
  {
    if ((paramPdfChunk == null) || (paramPdfChunk.toString().equals("")))
      return null;
    PdfChunk localPdfChunk = paramPdfChunk.split(this.width);
    this.newlineSplit = ((paramPdfChunk.isNewlineSplit()) || (localPdfChunk == null));
    if (paramPdfChunk.isTab())
    {
      Object[] arrayOfObject = (Object[])paramPdfChunk.getAttribute("TAB");
      float f = ((Float)arrayOfObject[1]).floatValue();
      boolean bool = ((Boolean)arrayOfObject[2]).booleanValue();
      if ((bool) && (f < this.originalWidth - this.width))
        return paramPdfChunk;
      this.width = (this.originalWidth - f);
      paramPdfChunk.adjustLeft(this.left);
      addToLine(paramPdfChunk);
    }
    else if ((paramPdfChunk.length() > 0) || (paramPdfChunk.isImage()))
    {
      if (localPdfChunk != null)
        paramPdfChunk.trimLastSpace();
      this.width -= paramPdfChunk.width();
      addToLine(paramPdfChunk);
    }
    else
    {
      if (this.line.size() < 1)
      {
        paramPdfChunk = localPdfChunk;
        localPdfChunk = paramPdfChunk.truncate(this.width);
        this.width -= paramPdfChunk.width();
        if (paramPdfChunk.length() > 0)
        {
          addToLine(paramPdfChunk);
          return localPdfChunk;
        }
        if (localPdfChunk != null)
          addToLine(localPdfChunk);
        return null;
      }
      this.width += ((PdfChunk)this.line.get(this.line.size() - 1)).trimLastSpace();
    }
    return localPdfChunk;
  }

  private void addToLine(PdfChunk paramPdfChunk)
  {
    if ((paramPdfChunk.changeLeading) && (paramPdfChunk.isImage()))
    {
      float f = paramPdfChunk.getImage().getScaledHeight() + paramPdfChunk.getImageOffsetY() + paramPdfChunk.getImage().getBorderWidthTop();
      if (f > this.height)
        this.height = f;
    }
    this.line.add(paramPdfChunk);
  }

  public int size()
  {
    return this.line.size();
  }

  public Iterator iterator()
  {
    return this.line.iterator();
  }

  float height()
  {
    return this.height;
  }

  float indentLeft()
  {
    if (this.isRTL)
    {
      switch (this.alignment)
      {
      case 0:
        return this.left + this.width;
      case 1:
        return this.left + this.width / 2.0F;
      }
      return this.left;
    }
    if (getSeparatorCount() == 0)
      switch (this.alignment)
      {
      case 2:
        return this.left + this.width;
      case 1:
        return this.left + this.width / 2.0F;
      }
    return this.left;
  }

  public boolean hasToBeJustified()
  {
    return ((this.alignment == 3) || (this.alignment == 8)) && (this.width != 0.0F);
  }

  public void resetAlignment()
  {
    if (this.alignment == 3)
      this.alignment = 0;
  }

  void setExtraIndent(float paramFloat)
  {
    this.left += paramFloat;
    this.width -= paramFloat;
  }

  float widthLeft()
  {
    return this.width;
  }

  int numberOfSpaces()
  {
    String str = toString();
    int i = str.length();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      if (str.charAt(k) != ' ')
        continue;
      j++;
    }
    return j;
  }

  public void setListItem(ListItem paramListItem)
  {
    this.listSymbol = paramListItem.getListSymbol();
    this.symbolIndent = paramListItem.getIndentationLeft();
  }

  public Chunk listSymbol()
  {
    return this.listSymbol;
  }

  public float listIndent()
  {
    return this.symbolIndent;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = this.line.iterator();
    while (localIterator.hasNext())
      localStringBuffer.append(((PdfChunk)localIterator.next()).toString());
    return localStringBuffer.toString();
  }

  public int GetLineLengthUtf32()
  {
    int i = 0;
    Iterator localIterator = this.line.iterator();
    while (localIterator.hasNext())
      i += ((PdfChunk)localIterator.next()).lengthUtf32();
    return i;
  }

  public boolean isNewlineSplit()
  {
    return (this.newlineSplit) && (this.alignment != 8);
  }

  public int getLastStrokeChunk()
  {
    for (int i = this.line.size() - 1; i >= 0; i--)
    {
      PdfChunk localPdfChunk = (PdfChunk)this.line.get(i);
      if (localPdfChunk.isStroked())
        break;
    }
    return i;
  }

  public PdfChunk getChunk(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.line.size()))
      return null;
    return (PdfChunk)this.line.get(paramInt);
  }

  public float getOriginalWidth()
  {
    return this.originalWidth;
  }

  float[] getMaxSize()
  {
    float f1 = 0.0F;
    float f2 = -10000.0F;
    for (int i = 0; i < this.line.size(); i++)
    {
      PdfChunk localPdfChunk = (PdfChunk)this.line.get(i);
      if (!localPdfChunk.isImage())
        f1 = Math.max(localPdfChunk.font().size(), f1);
      else
        f2 = Math.max(localPdfChunk.getImage().getScaledHeight() + localPdfChunk.getImageOffsetY(), f2);
    }
    return new float[] { f1, f2 };
  }

  boolean isRTL()
  {
    return this.isRTL;
  }

  int getSeparatorCount()
  {
    int i = 0;
    Iterator localIterator = this.line.iterator();
    while (localIterator.hasNext())
    {
      PdfChunk localPdfChunk = (PdfChunk)localIterator.next();
      if (localPdfChunk.isTab())
        return 0;
      if (!localPdfChunk.isHorizontalSeparator())
        continue;
      i++;
    }
    return i;
  }

  public float getWidthCorrected(float paramFloat1, float paramFloat2)
  {
    float f = 0.0F;
    for (int i = 0; i < this.line.size(); i++)
    {
      PdfChunk localPdfChunk = (PdfChunk)this.line.get(i);
      f += localPdfChunk.getWidthCorrected(paramFloat1, paramFloat2);
    }
    return f;
  }

  public float getAscender()
  {
    float f = 0.0F;
    for (int i = 0; i < this.line.size(); i++)
    {
      PdfChunk localPdfChunk = (PdfChunk)this.line.get(i);
      if (localPdfChunk.isImage())
      {
        f = Math.max(f, localPdfChunk.getImage().getScaledHeight() + localPdfChunk.getImageOffsetY());
      }
      else
      {
        PdfFont localPdfFont = localPdfChunk.font();
        f = Math.max(f, localPdfFont.getFont().getFontDescriptor(1, localPdfFont.size()));
      }
    }
    return f;
  }

  public float getDescender()
  {
    float f = 0.0F;
    for (int i = 0; i < this.line.size(); i++)
    {
      PdfChunk localPdfChunk = (PdfChunk)this.line.get(i);
      if (localPdfChunk.isImage())
      {
        f = Math.min(f, localPdfChunk.getImageOffsetY());
      }
      else
      {
        PdfFont localPdfFont = localPdfChunk.font();
        f = Math.min(f, localPdfFont.getFont().getFontDescriptor(3, localPdfFont.size()));
      }
    }
    return f;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfLine
 * JD-Core Version:    0.6.0
 */