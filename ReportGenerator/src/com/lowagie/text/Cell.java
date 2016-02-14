package com.lowagie.text;

import com.lowagie.text.pdf.PdfPCell;
import java.util.ArrayList;
import java.util.Iterator;

public class Cell extends Rectangle
  implements TextElementArray
{
  protected ArrayList arrayList = null;
  protected int horizontalAlignment = -1;
  protected int verticalAlignment = -1;
  protected float width;
  protected boolean percentage = false;
  protected int colspan = 1;
  protected int rowspan = 1;
  float leading = (0.0F / 0.0F);
  protected boolean header;
  protected int maxLines = 2147483647;
  String showTruncation;
  protected boolean useAscender = false;
  protected boolean useDescender = false;
  protected boolean useBorderPadding;
  protected boolean groupChange = true;

  public Cell()
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    setBorder(-1);
    setBorderWidth(0.5F);
    this.arrayList = new ArrayList();
  }

  public Cell(boolean paramBoolean)
  {
    this();
    this.arrayList.add(new Paragraph(0.0F));
  }

  public Cell(String paramString)
  {
    this();
    try
    {
      addElement(new Paragraph(paramString));
    }
    catch (BadElementException localBadElementException)
    {
    }
  }

  public Cell(Element paramElement)
    throws BadElementException
  {
    this();
    if ((paramElement instanceof Phrase))
      setLeading(((Phrase)paramElement).getLeading());
    addElement(paramElement);
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      return paramElementListener.add(this);
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return 20;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.arrayList.iterator();
    while (localIterator.hasNext())
      localArrayList.addAll(((Element)localIterator.next()).getChunks());
    return localArrayList;
  }

  public int getHorizontalAlignment()
  {
    return this.horizontalAlignment;
  }

  public void setHorizontalAlignment(int paramInt)
  {
    this.horizontalAlignment = paramInt;
  }

  public void setHorizontalAlignment(String paramString)
  {
    setHorizontalAlignment(ElementTags.alignmentValue(paramString));
  }

  public int getVerticalAlignment()
  {
    return this.verticalAlignment;
  }

  public void setVerticalAlignment(int paramInt)
  {
    this.verticalAlignment = paramInt;
  }

  public void setVerticalAlignment(String paramString)
  {
    setVerticalAlignment(ElementTags.alignmentValue(paramString));
  }

  public void setWidth(float paramFloat)
  {
    this.width = paramFloat;
  }

  public void setWidth(String paramString)
  {
    if (paramString.endsWith("%"))
    {
      paramString = paramString.substring(0, paramString.length() - 1);
      this.percentage = true;
    }
    this.width = Integer.parseInt(paramString);
  }

  public float getWidth()
  {
    return this.width;
  }

  public String getWidthAsString()
  {
    String str = String.valueOf(this.width);
    if (str.endsWith(".0"))
      str = str.substring(0, str.length() - 2);
    if (this.percentage)
      str = str + "%";
    return str;
  }

  public void setColspan(int paramInt)
  {
    this.colspan = paramInt;
  }

  public int getColspan()
  {
    return this.colspan;
  }

  public void setRowspan(int paramInt)
  {
    this.rowspan = paramInt;
  }

  public int getRowspan()
  {
    return this.rowspan;
  }

  public void setLeading(float paramFloat)
  {
    this.leading = paramFloat;
  }

  public float getLeading()
  {
    if (Float.isNaN(this.leading))
      return 16.0F;
    return this.leading;
  }

  public void setHeader(boolean paramBoolean)
  {
    this.header = paramBoolean;
  }

  public boolean isHeader()
  {
    return this.header;
  }

  public void setMaxLines(int paramInt)
  {
    this.maxLines = paramInt;
  }

  public int getMaxLines()
  {
    return this.maxLines;
  }

  public void setShowTruncation(String paramString)
  {
    this.showTruncation = paramString;
  }

  public String getShowTruncation()
  {
    return this.showTruncation;
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

  public boolean getGroupChange()
  {
    return this.groupChange;
  }

  public void setGroupChange(boolean paramBoolean)
  {
    this.groupChange = paramBoolean;
  }

  public int size()
  {
    return this.arrayList.size();
  }

  public Iterator getElements()
  {
    return this.arrayList.iterator();
  }

  public void clear()
  {
    this.arrayList.clear();
  }

  public boolean isEmpty()
  {
    switch (size())
    {
    case 0:
      return true;
    case 1:
      Element localElement = (Element)this.arrayList.get(0);
      switch (localElement.type())
      {
      case 10:
        return ((Chunk)localElement).isEmpty();
      case 11:
      case 12:
      case 17:
        return ((Phrase)localElement).isEmpty();
      case 14:
        return ((List)localElement).isEmpty();
      case 13:
      case 15:
      case 16:
      }
      return false;
    }
    return false;
  }

  void fill()
  {
    if (size() == 0)
      this.arrayList.add(new Paragraph(0.0F));
  }

  public boolean isTable()
  {
    return (size() == 1) && (((Element)this.arrayList.get(0)).type() == 22);
  }

  public void addElement(Element paramElement)
    throws BadElementException
  {
    Object localObject1;
    Object localObject2;
    if (isTable())
    {
      localObject1 = (Table)this.arrayList.get(0);
      localObject2 = new Cell(paramElement);
      ((Cell)localObject2).setBorder(0);
      ((Cell)localObject2).setColspan(((Table)localObject1).getColumns());
      ((Table)localObject1).addCell((Cell)localObject2);
      return;
    }
    switch (paramElement.type())
    {
    case 15:
    case 20:
    case 21:
      throw new BadElementException("You can't add listitems, rows or cells to a cell.");
    case 14:
      localObject1 = (List)paramElement;
      if (Float.isNaN(this.leading))
        setLeading(((List)localObject1).getTotalLeading());
      if (((List)localObject1).isEmpty())
        return;
      this.arrayList.add(paramElement);
      return;
    case 11:
    case 12:
    case 17:
      localObject2 = (Phrase)paramElement;
      if (Float.isNaN(this.leading))
        setLeading(((Phrase)localObject2).getLeading());
      if (((Phrase)localObject2).isEmpty())
        return;
      this.arrayList.add(paramElement);
      return;
    case 10:
      if (((Chunk)paramElement).isEmpty())
        return;
      this.arrayList.add(paramElement);
      return;
    case 22:
      Table localTable = new Table(3);
      float[] arrayOfFloat = new float[3];
      arrayOfFloat[1] = ((Table)paramElement).getWidth();
      switch (((Table)paramElement).getAlignment())
      {
      case 0:
        arrayOfFloat[0] = 0.0F;
        arrayOfFloat[2] = (100.0F - arrayOfFloat[1]);
        break;
      case 1:
        arrayOfFloat[0] = ((100.0F - arrayOfFloat[1]) / 2.0F);
        arrayOfFloat[2] = arrayOfFloat[0];
        break;
      case 2:
        arrayOfFloat[0] = (100.0F - arrayOfFloat[1]);
        arrayOfFloat[2] = 0.0F;
      }
      localTable.setWidths(arrayOfFloat);
      if (this.arrayList.isEmpty())
      {
        localTable.addCell(getDummyCell());
      }
      else
      {
        localCell = new Cell();
        localCell.setBorder(0);
        localCell.setColspan(3);
        Iterator localIterator = this.arrayList.iterator();
        while (localIterator.hasNext())
          localCell.add(localIterator.next());
        localTable.addCell(localCell);
      }
      Cell localCell = new Cell();
      localCell.setBorder(0);
      localTable.addCell(localCell);
      localTable.insertTable((Table)paramElement);
      localCell = new Cell();
      localCell.setBorder(0);
      localTable.addCell(localCell);
      localTable.addCell(getDummyCell());
      clear();
      this.arrayList.add(localTable);
      return;
    case 13:
    case 16:
    case 18:
    case 19:
    }
    this.arrayList.add(paramElement);
  }

  public boolean add(Object paramObject)
  {
    try
    {
      addElement((Element)paramObject);
      return true;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new ClassCastException("You can only add objects that implement the Element interface.");
    }
    catch (BadElementException localBadElementException)
    {
    }
    throw new ClassCastException(localBadElementException.getMessage());
  }

  private static Cell getDummyCell()
  {
    Cell localCell = new Cell(true);
    localCell.setColspan(3);
    localCell.setBorder(0);
    return localCell;
  }

  public PdfPCell createPdfPCell()
    throws BadElementException
  {
    if (this.rowspan > 1)
      throw new BadElementException("PdfPCells can't have a rowspan > 1");
    if (isTable())
      return new PdfPCell(((Table)this.arrayList.get(0)).createPdfPTable());
    PdfPCell localPdfPCell = new PdfPCell();
    localPdfPCell.setVerticalAlignment(this.verticalAlignment);
    localPdfPCell.setHorizontalAlignment(this.horizontalAlignment);
    localPdfPCell.setColspan(this.colspan);
    localPdfPCell.setUseBorderPadding(this.useBorderPadding);
    localPdfPCell.setUseDescender(this.useDescender);
    localPdfPCell.setLeading(getLeading(), 0.0F);
    localPdfPCell.cloneNonPositionParameters(this);
    localPdfPCell.setNoWrap(getMaxLines() == 1);
    Iterator localIterator = getElements();
    while (localIterator.hasNext())
    {
      Object localObject = (Element)localIterator.next();
      if ((((Element)localObject).type() == 11) || (((Element)localObject).type() == 12))
      {
        Paragraph localParagraph = new Paragraph((Phrase)localObject);
        localParagraph.setAlignment(this.horizontalAlignment);
        localObject = localParagraph;
      }
      localPdfPCell.addElement((Element)localObject);
    }
    return (PdfPCell)localPdfPCell;
  }

  public float getTop()
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float getBottom()
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float getLeft()
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float getRight()
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float top(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float bottom(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float left(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public float right(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell can't be calculated. See the FAQ.");
  }

  public void setTop(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
  }

  public void setBottom(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
  }

  public void setLeft(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
  }

  public void setRight(int paramInt)
  {
    throw new UnsupportedOperationException("Dimensions of a Cell are attributed automagically. See the FAQ.");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Cell
 * JD-Core Version:    0.6.0
 */