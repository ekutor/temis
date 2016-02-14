package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfCell extends Rectangle
{
  private ArrayList lines;
  private PdfLine line;
  private ArrayList images;
  private float leading;
  private int rownumber;
  private int rowspan;
  private float cellspacing;
  private float cellpadding;
  private boolean header = false;
  private float contentHeight = 0.0F;
  private boolean useAscender;
  private boolean useDescender;
  private boolean useBorderPadding;
  private int verticalAlignment;
  private PdfLine firstLine;
  private PdfLine lastLine;
  private int groupNumber;

  public PdfCell(Cell paramCell, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    super(paramFloat1, paramFloat3, paramFloat2, paramFloat3);
    cloneNonPositionParameters(paramCell);
    this.cellpadding = paramFloat5;
    this.cellspacing = paramFloat4;
    this.verticalAlignment = paramCell.getVerticalAlignment();
    this.useAscender = paramCell.isUseAscender();
    this.useDescender = paramCell.isUseDescender();
    this.useBorderPadding = paramCell.isUseBorderPadding();
    this.lines = new ArrayList();
    this.images = new ArrayList();
    this.leading = paramCell.getLeading();
    int i = paramCell.getHorizontalAlignment();
    paramFloat1 += paramFloat4 + paramFloat5;
    paramFloat2 -= paramFloat4 + paramFloat5;
    paramFloat1 += getBorderWidthInside(4);
    paramFloat2 -= getBorderWidthInside(8);
    this.contentHeight = 0.0F;
    this.rowspan = paramCell.getRowspan();
    Object localObject2 = paramCell.getElements();
    float f3;
    while (((Iterator)localObject2).hasNext())
    {
      Element localElement = (Element)((Iterator)localObject2).next();
      switch (localElement.type())
      {
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
        addImage((Image)localElement, paramFloat1, paramFloat2, 0.4F * this.leading, i);
        break;
      case 14:
        if ((this.line != null) && (this.line.size() > 0))
        {
          this.line.resetAlignment();
          addLine(this.line);
        }
        addList((List)localElement, paramFloat1, paramFloat2, i);
        this.line = new PdfLine(paramFloat1, paramFloat2, i, this.leading);
        break;
      default:
        ArrayList localArrayList = new ArrayList();
        processActions(localElement, null, localArrayList);
        int j = 0;
        float f2 = this.leading;
        f3 = paramFloat1;
        float f4 = paramFloat2;
        if ((localElement instanceof Phrase))
          f2 = ((Phrase)localElement).getLeading();
        if ((localElement instanceof Paragraph))
        {
          localObject3 = (Paragraph)localElement;
          f3 += ((Paragraph)localObject3).getIndentationLeft();
          f4 -= ((Paragraph)localObject3).getIndentationRight();
        }
        if (this.line == null)
          this.line = new PdfLine(f3, f4, i, f2);
        Object localObject3 = localElement.getChunks();
        if (((ArrayList)localObject3).isEmpty())
        {
          addLine(this.line);
          this.line = new PdfLine(f3, f4, i, f2);
        }
        else
        {
          Iterator localIterator = ((ArrayList)localObject3).iterator();
          while (localIterator.hasNext())
          {
            Chunk localChunk = (Chunk)localIterator.next();
            PdfChunk localPdfChunk1;
            for (Object localObject1 = new PdfChunk(localChunk, (PdfAction)localArrayList.get(j++)); (localPdfChunk1 = this.line.add((PdfChunk)localObject1)) != null; localObject1 = localPdfChunk1)
            {
              addLine(this.line);
              this.line = new PdfLine(f3, f4, i, f2);
            }
          }
        }
        switch (localElement.type())
        {
        case 12:
        case 13:
        case 16:
          this.line.resetAlignment();
          flushCurrentLine();
        case 14:
        case 15:
        }
      }
    }
    flushCurrentLine();
    if (this.lines.size() > paramCell.getMaxLines())
    {
      while (this.lines.size() > paramCell.getMaxLines())
        removeLine(this.lines.size() - 1);
      if (paramCell.getMaxLines() > 0)
      {
        localObject2 = paramCell.getShowTruncation();
        if ((localObject2 != null) && (((String)localObject2).length() > 0))
        {
          this.lastLine = ((PdfLine)this.lines.get(this.lines.size() - 1));
          if (this.lastLine.size() >= 0)
          {
            PdfChunk localPdfChunk2 = this.lastLine.getChunk(this.lastLine.size() - 1);
            f3 = new PdfChunk((String)localObject2, localPdfChunk2).width();
            while ((localPdfChunk2.toString().length() > 0) && (localPdfChunk2.width() + f3 > paramFloat2 - paramFloat1))
              localPdfChunk2.setValue(localPdfChunk2.toString().substring(0, localPdfChunk2.length() - 1));
            localPdfChunk2.setValue(localPdfChunk2.toString() + (String)localObject2);
          }
          else
          {
            this.lastLine.add(new PdfChunk(new Chunk((String)localObject2), null));
          }
        }
      }
    }
    if ((this.useDescender) && (this.lastLine != null))
      this.contentHeight -= this.lastLine.getDescender();
    if (!this.lines.isEmpty())
    {
      this.firstLine = ((PdfLine)this.lines.get(0));
      f1 = firstLineRealHeight();
      this.contentHeight -= this.firstLine.height();
      this.firstLine.height = f1;
      this.contentHeight += f1;
    }
    float f1 = paramFloat3 - this.contentHeight - 2.0F * cellpadding() - 2.0F * cellspacing();
    f1 -= getBorderWidthInside(1) + getBorderWidthInside(2);
    setBottom(f1);
    this.rownumber = paramInt;
  }

  private void addList(List paramList, float paramFloat1, float paramFloat2, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    processActions(paramList, null, localArrayList);
    int i = 0;
    Iterator localIterator = paramList.getItems().iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      ListItem localListItem;
      Object localObject2;
      switch (localElement.type())
      {
      case 15:
        localListItem = (ListItem)localElement;
        this.line = new PdfLine(paramFloat1 + localListItem.getIndentationLeft(), paramFloat2, paramInt, localListItem.getLeading());
        this.line.setListItem(localListItem);
        localObject2 = localListItem.getChunks().iterator();
      case 14:
        while (((Iterator)localObject2).hasNext())
        {
          PdfChunk localPdfChunk;
          for (Object localObject1 = new PdfChunk((Chunk)((Iterator)localObject2).next(), (PdfAction)localArrayList.get(i++)); (localPdfChunk = this.line.add((PdfChunk)localObject1)) != null; localObject1 = localPdfChunk)
          {
            addLine(this.line);
            this.line = new PdfLine(paramFloat1 + localListItem.getIndentationLeft(), paramFloat2, paramInt, localListItem.getLeading());
          }
          this.line.resetAlignment();
          addLine(this.line);
          this.line = new PdfLine(paramFloat1 + localListItem.getIndentationLeft(), paramFloat2, paramInt, this.leading);
          continue;
          localObject2 = (List)localElement;
          addList((List)localObject2, paramFloat1 + ((List)localObject2).getIndentationLeft(), paramFloat2, paramInt);
        }
      }
    }
  }

  public void setBottom(float paramFloat)
  {
    super.setBottom(paramFloat);
    float f1 = firstLineRealHeight();
    float f2 = this.ury - paramFloat;
    float f3 = cellpadding() * 2.0F + cellspacing() * 2.0F;
    f3 += getBorderWidthInside(1) + getBorderWidthInside(2);
    float f4 = f2 - f3;
    float f5 = 0.0F;
    switch (this.verticalAlignment)
    {
    case 6:
      f5 = f4 - this.contentHeight;
      break;
    case 5:
      f5 = (f4 - this.contentHeight) / 2.0F;
      break;
    default:
      f5 = 0.0F;
    }
    f5 += cellpadding() + cellspacing();
    f5 += getBorderWidthInside(1);
    if (this.firstLine != null)
      this.firstLine.height = (f1 + f5);
  }

  public float getLeft()
  {
    return super.getLeft(this.cellspacing);
  }

  public float getRight()
  {
    return super.getRight(this.cellspacing);
  }

  public float getTop()
  {
    return super.getTop(this.cellspacing);
  }

  public float getBottom()
  {
    return super.getBottom(this.cellspacing);
  }

  private void addLine(PdfLine paramPdfLine)
  {
    this.lines.add(paramPdfLine);
    this.contentHeight += paramPdfLine.height();
    this.lastLine = paramPdfLine;
    this.line = null;
  }

  private PdfLine removeLine(int paramInt)
  {
    PdfLine localPdfLine = (PdfLine)this.lines.remove(paramInt);
    this.contentHeight -= localPdfLine.height();
    if ((paramInt == 0) && (!this.lines.isEmpty()))
    {
      this.firstLine = ((PdfLine)this.lines.get(0));
      float f = firstLineRealHeight();
      this.contentHeight -= this.firstLine.height();
      this.firstLine.height = f;
      this.contentHeight += f;
    }
    return localPdfLine;
  }

  private void flushCurrentLine()
  {
    if ((this.line != null) && (this.line.size() > 0))
      addLine(this.line);
  }

  private float firstLineRealHeight()
  {
    float f = 0.0F;
    if (this.firstLine != null)
    {
      PdfChunk localPdfChunk = this.firstLine.getChunk(0);
      if (localPdfChunk != null)
      {
        Image localImage = localPdfChunk.getImage();
        if (localImage != null)
          f = this.firstLine.getChunk(0).getImage().getScaledHeight();
        else
          f = this.useAscender ? this.firstLine.getAscender() : this.leading;
      }
    }
    return f;
  }

  private float getBorderWidthInside(int paramInt)
  {
    float f = 0.0F;
    if (this.useBorderPadding)
    {
      switch (paramInt)
      {
      case 4:
        f = getBorderWidthLeft();
        break;
      case 8:
        f = getBorderWidthRight();
        break;
      case 1:
        f = getBorderWidthTop();
        break;
      default:
        f = getBorderWidthBottom();
      }
      if (!isUseVariableBorders())
        f /= 2.0F;
    }
    return f;
  }

  private float addImage(Image paramImage, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    Image localImage = Image.getInstance(paramImage);
    if (localImage.getScaledWidth() > paramFloat2 - paramFloat1)
      localImage.scaleToFit(paramFloat2 - paramFloat1, 3.4028235E+38F);
    flushCurrentLine();
    if (this.line == null)
      this.line = new PdfLine(paramFloat1, paramFloat2, paramInt, this.leading);
    PdfLine localPdfLine = this.line;
    paramFloat2 -= paramFloat1;
    paramFloat1 = 0.0F;
    if ((localImage.getAlignment() & 0x2) == 2)
      paramFloat1 = paramFloat2 - localImage.getScaledWidth();
    else if ((localImage.getAlignment() & 0x1) == 1)
      paramFloat1 += (paramFloat2 - paramFloat1 - localImage.getScaledWidth()) / 2.0F;
    Chunk localChunk = new Chunk(localImage, paramFloat1, 0.0F);
    localPdfLine.add(new PdfChunk(localChunk, null));
    addLine(localPdfLine);
    return localPdfLine.height();
  }

  public ArrayList getLines(float paramFloat1, float paramFloat2)
  {
    float f2 = Math.min(getTop(), paramFloat1);
    setTop(f2 + this.cellspacing);
    ArrayList localArrayList = new ArrayList();
    if (getTop() < paramFloat2)
      return localArrayList;
    int i = this.lines.size();
    int j = 1;
    for (int k = 0; (k < i) && (j != 0); k++)
    {
      this.line = ((PdfLine)this.lines.get(k));
      float f1 = this.line.height();
      f2 -= f1;
      if (f2 > paramFloat2 + this.cellpadding + getBorderWidthInside(2))
        localArrayList.add(this.line);
      else
        j = 0;
    }
    float f3 = 0.0F;
    if (!this.header)
      if (j != 0)
      {
        this.lines = new ArrayList();
        this.contentHeight = 0.0F;
      }
      else
      {
        i = localArrayList.size();
        for (int m = 0; m < i; m++)
        {
          this.line = removeLine(0);
          f3 += this.line.height();
        }
      }
    if (f3 > 0.0F)
    {
      Iterator localIterator = this.images.iterator();
      while (localIterator.hasNext())
      {
        Image localImage = (Image)localIterator.next();
        localImage.setAbsolutePosition(localImage.getAbsoluteX(), localImage.getAbsoluteY() - f3 - this.leading);
      }
    }
    return localArrayList;
  }

  public ArrayList getImages(float paramFloat1, float paramFloat2)
  {
    if (getTop() < paramFloat2)
      return new ArrayList();
    paramFloat1 = Math.min(getTop(), paramFloat1);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.images.iterator();
    while ((localIterator.hasNext()) && (!this.header))
    {
      Image localImage = (Image)localIterator.next();
      float f = localImage.getAbsoluteY();
      if (paramFloat1 - f <= paramFloat2 + this.cellpadding)
        continue;
      localImage.setAbsolutePosition(localImage.getAbsoluteX(), paramFloat1 - f);
      localArrayList.add(localImage);
      localIterator.remove();
    }
    return localArrayList;
  }

  boolean isHeader()
  {
    return this.header;
  }

  void setHeader()
  {
    this.header = true;
  }

  boolean mayBeRemoved()
  {
    return (this.header) || ((this.lines.isEmpty()) && (this.images.isEmpty()));
  }

  public int size()
  {
    return this.lines.size();
  }

  private float remainingLinesHeight()
  {
    if (this.lines.isEmpty())
      return 0.0F;
    float f = 0.0F;
    int i = this.lines.size();
    for (int j = 0; j < i; j++)
    {
      PdfLine localPdfLine = (PdfLine)this.lines.get(j);
      f += localPdfLine.height();
    }
    return f;
  }

  public float remainingHeight()
  {
    float f = 0.0F;
    Iterator localIterator = this.images.iterator();
    while (localIterator.hasNext())
    {
      Image localImage = (Image)localIterator.next();
      f += localImage.getScaledHeight();
    }
    return remainingLinesHeight() + this.cellspacing + 2.0F * this.cellpadding + f;
  }

  public float leading()
  {
    return this.leading;
  }

  public int rownumber()
  {
    return this.rownumber;
  }

  public int rowspan()
  {
    return this.rowspan;
  }

  public float cellspacing()
  {
    return this.cellspacing;
  }

  public float cellpadding()
  {
    return this.cellpadding;
  }

  protected void processActions(Element paramElement, PdfAction paramPdfAction, ArrayList paramArrayList)
  {
    Object localObject;
    if (paramElement.type() == 17)
    {
      localObject = ((Anchor)paramElement).getReference();
      if (localObject != null)
        paramPdfAction = new PdfAction((String)localObject);
    }
    switch (paramElement.type())
    {
    case 11:
    case 12:
    case 13:
    case 15:
    case 16:
    case 17:
      localObject = ((ArrayList)paramElement).iterator();
    case 10:
    case 14:
    }
    while (((Iterator)localObject).hasNext())
    {
      processActions((Element)((Iterator)localObject).next(), paramPdfAction, paramArrayList);
      continue;
      paramArrayList.add(paramPdfAction);
      break;
      localObject = ((List)paramElement).getItems().iterator();
      while (((Iterator)localObject).hasNext())
      {
        processActions((Element)((Iterator)localObject).next(), paramPdfAction, paramArrayList);
        continue;
        int i = paramElement.getChunks().size();
        while (i-- > 0)
          paramArrayList.add(paramPdfAction);
      }
    }
  }

  public int getGroupNumber()
  {
    return this.groupNumber;
  }

  void setGroupNumber(int paramInt)
  {
    this.groupNumber = paramInt;
  }

  public Rectangle rectangle(float paramFloat1, float paramFloat2)
  {
    Rectangle localRectangle = new Rectangle(getLeft(), getBottom(), getRight(), getTop());
    localRectangle.cloneNonPositionParameters(this);
    if (getTop() > paramFloat1)
    {
      localRectangle.setTop(paramFloat1);
      localRectangle.setBorder(this.border - (this.border & 0x1));
    }
    if (getBottom() < paramFloat2)
    {
      localRectangle.setBottom(paramFloat2);
      localRectangle.setBorder(this.border - (this.border & 0x2));
    }
    return localRectangle;
  }

  public void setUseAscender(boolean paramBoolean)
  {
    this.useAscender = paramBoolean;
  }

  public boolean isUseAscender()
  {
    return this.useAscender;
  }

  public void setUseDescender(boolean paramBoolean)
  {
    this.useDescender = paramBoolean;
  }

  public boolean isUseDescender()
  {
    return this.useDescender;
  }

  public void setUseBorderPadding(boolean paramBoolean)
  {
    this.useBorderPadding = paramBoolean;
  }

  public boolean isUseBorderPadding()
  {
    return this.useBorderPadding;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfCell
 * JD-Core Version:    0.6.0
 */