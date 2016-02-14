package com.lowagie.text;

import java.util.ArrayList;

public class Row
  implements Element
{
  public static final int NULL = 0;
  public static final int CELL = 1;
  public static final int TABLE = 2;
  protected int columns;
  protected int currentColumn;
  protected boolean[] reserved;
  protected Object[] cells;
  protected int horizontalAlignment;

  protected Row(int paramInt)
  {
    this.columns = paramInt;
    this.reserved = new boolean[paramInt];
    this.cells = new Object[paramInt];
    this.currentColumn = 0;
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
    return 21;
  }

  public ArrayList getChunks()
  {
    return new ArrayList();
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return false;
  }

  void deleteColumn(int paramInt)
  {
    if ((paramInt >= this.columns) || (paramInt < 0))
      throw new IndexOutOfBoundsException("getCell at illegal index : " + paramInt);
    this.columns -= 1;
    boolean[] arrayOfBoolean = new boolean[this.columns];
    Cell[] arrayOfCell = new Cell[this.columns];
    for (int i = 0; i < paramInt; i++)
    {
      arrayOfBoolean[i] = this.reserved[i];
      arrayOfCell[i] = this.cells[i];
      if ((arrayOfCell[i] == null) || (i + ((Cell)arrayOfCell[i]).getColspan() <= paramInt))
        continue;
      ((Cell)arrayOfCell[i]).setColspan(((Cell)this.cells[i]).getColspan() - 1);
    }
    for (i = paramInt; i < this.columns; i++)
    {
      arrayOfBoolean[i] = this.reserved[(i + 1)];
      arrayOfCell[i] = this.cells[(i + 1)];
    }
    if ((this.cells[paramInt] != null) && (((Cell)this.cells[paramInt]).getColspan() > 1))
    {
      arrayOfCell[paramInt] = this.cells[paramInt];
      ((Cell)arrayOfCell[paramInt]).setColspan(((Cell)arrayOfCell[paramInt]).getColspan() - 1);
    }
    this.reserved = arrayOfBoolean;
    this.cells = arrayOfCell;
  }

  int addElement(Object paramObject)
  {
    return addElement(paramObject, this.currentColumn);
  }

  int addElement(Object paramObject, int paramInt)
  {
    if (paramObject == null)
      throw new NullPointerException("addCell - null argument");
    if ((paramInt < 0) || (paramInt > this.columns))
      throw new IndexOutOfBoundsException("addCell - illegal column argument");
    if ((getObjectID(paramObject) != 1) && (getObjectID(paramObject) != 2))
      throw new IllegalArgumentException("addCell - only Cells or Tables allowed");
    int i = Cell.class.isInstance(paramObject) ? ((Cell)paramObject).getColspan() : 1;
    if (!reserve(paramInt, i))
      return -1;
    this.cells[paramInt] = paramObject;
    this.currentColumn += i - 1;
    return paramInt;
  }

  void setElement(Object paramObject, int paramInt)
  {
    if (this.reserved[paramInt] != 0)
      throw new IllegalArgumentException("setElement - position already taken");
    this.cells[paramInt] = paramObject;
    if (paramObject != null)
      this.reserved[paramInt] = true;
  }

  boolean reserve(int paramInt)
  {
    return reserve(paramInt, 1);
  }

  boolean reserve(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 + paramInt2 > this.columns))
      throw new IndexOutOfBoundsException("reserve - incorrect column/size");
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
    {
      if (this.reserved[i] != 0)
      {
        for (int j = i; j >= paramInt1; j--)
          this.reserved[j] = false;
        return false;
      }
      this.reserved[i] = true;
    }
    return true;
  }

  boolean isReserved(int paramInt)
  {
    return this.reserved[paramInt];
  }

  int getElementID(int paramInt)
  {
    if (this.cells[paramInt] == null)
      return 0;
    if (Cell.class.isInstance(this.cells[paramInt]))
      return 1;
    if (Table.class.isInstance(this.cells[paramInt]))
      return 2;
    return -1;
  }

  int getObjectID(Object paramObject)
  {
    if (paramObject == null)
      return 0;
    if (Cell.class.isInstance(paramObject))
      return 1;
    if (Table.class.isInstance(paramObject))
      return 2;
    return -1;
  }

  public Object getCell(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > this.columns))
      throw new IndexOutOfBoundsException("getCell at illegal index :" + paramInt + " max is " + this.columns);
    return this.cells[paramInt];
  }

  public boolean isEmpty()
  {
    for (int i = 0; i < this.columns; i++)
      if (this.cells[i] != null)
        return false;
    return true;
  }

  public int getColumns()
  {
    return this.columns;
  }

  public void setHorizontalAlignment(int paramInt)
  {
    this.horizontalAlignment = paramInt;
  }

  public int getHorizontalAlignment()
  {
    return this.horizontalAlignment;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Row
 * JD-Core Version:    0.6.0
 */