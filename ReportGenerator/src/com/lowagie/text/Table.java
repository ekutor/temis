package com.lowagie.text;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class Table extends Rectangle
  implements LargeElement
{
  private int columns;
  private ArrayList rows = new ArrayList();
  private Point curPosition = new Point(0, 0);
  private Cell defaultCell = new Cell(true);
  private int lastHeaderRow = -1;
  private int alignment = 1;
  private float cellpadding;
  private float cellspacing;
  private float width = 80.0F;
  private boolean locked = false;
  private float[] widths;
  private boolean mTableInserted = false;
  protected boolean autoFillEmptyCells = false;
  boolean tableFitsPage = false;
  boolean cellsFitPage = false;
  float offset = (0.0F / 0.0F);
  protected boolean convert2pdfptable = false;
  protected boolean notAddedYet = true;
  protected boolean complete = true;

  public Table(int paramInt)
    throws BadElementException
  {
    this(paramInt, 1);
  }

  public Table(int paramInt1, int paramInt2)
    throws BadElementException
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    setBorder(15);
    setBorderWidth(1.0F);
    this.defaultCell.setBorder(15);
    if (paramInt1 <= 0)
      throw new BadElementException("A table should have at least 1 column.");
    this.columns = paramInt1;
    for (int i = 0; i < paramInt2; i++)
      this.rows.add(new Row(paramInt1));
    this.curPosition = new Point(0, 0);
    this.widths = new float[paramInt1];
    float f = 100.0F / paramInt1;
    for (int j = 0; j < paramInt1; j++)
      this.widths[j] = f;
  }

  public Table(Table paramTable)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    cloneNonPositionParameters(paramTable);
    this.columns = paramTable.columns;
    this.rows = paramTable.rows;
    this.curPosition = paramTable.curPosition;
    this.defaultCell = paramTable.defaultCell;
    this.lastHeaderRow = paramTable.lastHeaderRow;
    this.alignment = paramTable.alignment;
    this.cellpadding = paramTable.cellpadding;
    this.cellspacing = paramTable.cellspacing;
    this.width = paramTable.width;
    this.widths = paramTable.widths;
    this.autoFillEmptyCells = paramTable.autoFillEmptyCells;
    this.tableFitsPage = paramTable.tableFitsPage;
    this.cellsFitPage = paramTable.cellsFitPage;
    this.offset = paramTable.offset;
    this.convert2pdfptable = paramTable.convert2pdfptable;
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
    return 22;
  }

  public ArrayList getChunks()
  {
    return new ArrayList();
  }

  public boolean isNestable()
  {
    return true;
  }

  public int getColumns()
  {
    return this.columns;
  }

  public int size()
  {
    return this.rows.size();
  }

  public Dimension getDimension()
  {
    return new Dimension(this.columns, size());
  }

  public Cell getDefaultCell()
  {
    return this.defaultCell;
  }

  public void setDefaultCell(Cell paramCell)
  {
    this.defaultCell = paramCell;
  }

  public int getLastHeaderRow()
  {
    return this.lastHeaderRow;
  }

  public void setLastHeaderRow(int paramInt)
  {
    this.lastHeaderRow = paramInt;
  }

  public int endHeaders()
  {
    this.lastHeaderRow = (this.curPosition.x - 1);
    return this.lastHeaderRow;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public void setAlignment(String paramString)
  {
    if ("Left".equalsIgnoreCase(paramString))
    {
      this.alignment = 0;
      return;
    }
    if ("right".equalsIgnoreCase(paramString))
    {
      this.alignment = 2;
      return;
    }
    this.alignment = 1;
  }

  public float getPadding()
  {
    return this.cellpadding;
  }

  public void setPadding(float paramFloat)
  {
    this.cellpadding = paramFloat;
  }

  public float getSpacing()
  {
    return this.cellspacing;
  }

  public void setSpacing(float paramFloat)
  {
    this.cellspacing = paramFloat;
  }

  public void setAutoFillEmptyCells(boolean paramBoolean)
  {
    this.autoFillEmptyCells = paramBoolean;
  }

  public float getWidth()
  {
    return this.width;
  }

  public void setWidth(float paramFloat)
  {
    this.width = paramFloat;
  }

  public boolean isLocked()
  {
    return this.locked;
  }

  public void setLocked(boolean paramBoolean)
  {
    this.locked = paramBoolean;
  }

  public float[] getProportionalWidths()
  {
    return this.widths;
  }

  public void setWidths(float[] paramArrayOfFloat)
    throws BadElementException
  {
    if (paramArrayOfFloat.length != this.columns)
      throw new BadElementException("Wrong number of columns.");
    float f1 = 0.0F;
    for (int i = 0; i < this.columns; i++)
      f1 += paramArrayOfFloat[i];
    this.widths[(this.columns - 1)] = 100.0F;
    for (int j = 0; j < this.columns - 1; j++)
    {
      float f2 = 100.0F * paramArrayOfFloat[j] / f1;
      this.widths[j] = f2;
      this.widths[(this.columns - 1)] -= f2;
    }
  }

  public void setWidths(int[] paramArrayOfInt)
    throws DocumentException
  {
    float[] arrayOfFloat = new float[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++)
      arrayOfFloat[i] = paramArrayOfInt[i];
    setWidths(arrayOfFloat);
  }

  public boolean isTableFitsPage()
  {
    return this.tableFitsPage;
  }

  public void setTableFitsPage(boolean paramBoolean)
  {
    this.tableFitsPage = paramBoolean;
    if (paramBoolean)
      setCellsFitPage(true);
  }

  public boolean isCellsFitPage()
  {
    return this.cellsFitPage;
  }

  public void setCellsFitPage(boolean paramBoolean)
  {
    this.cellsFitPage = paramBoolean;
  }

  public void setOffset(float paramFloat)
  {
    this.offset = paramFloat;
  }

  public float getOffset()
  {
    return this.offset;
  }

  public boolean isConvert2pdfptable()
  {
    return this.convert2pdfptable;
  }

  public void setConvert2pdfptable(boolean paramBoolean)
  {
    this.convert2pdfptable = paramBoolean;
  }

  public void addCell(Cell paramCell, int paramInt1, int paramInt2)
    throws BadElementException
  {
    addCell(paramCell, new Point(paramInt1, paramInt2));
  }

  public void addCell(Cell paramCell, Point paramPoint)
    throws BadElementException
  {
    if (paramCell == null)
      throw new NullPointerException("addCell - cell has null-value");
    if (paramPoint == null)
      throw new NullPointerException("addCell - point has null-value");
    if (paramCell.isTable())
      insertTable((Table)paramCell.getElements().next(), paramPoint);
    if (paramPoint.x < 0)
      throw new BadElementException("row coordinate of location must be >= 0");
    if ((paramPoint.y <= 0) && (paramPoint.y > this.columns))
      throw new BadElementException("column coordinate of location must be >= 0 and < nr of columns");
    if (!isValidLocation(paramCell, paramPoint))
      throw new BadElementException("Adding a cell at the location (" + paramPoint.x + "," + paramPoint.y + ") with a colspan of " + paramCell.getColspan() + " and a rowspan of " + paramCell.getRowspan() + " is illegal (beyond boundaries/overlapping).");
    if (paramCell.getBorder() == -1)
      paramCell.setBorder(this.defaultCell.getBorder());
    paramCell.fill();
    placeCell(this.rows, paramCell, paramPoint);
    setCurrentLocationToNextValidPosition(paramPoint);
  }

  public void addCell(Cell paramCell)
  {
    try
    {
      addCell(paramCell, this.curPosition);
    }
    catch (BadElementException localBadElementException)
    {
    }
  }

  public void addCell(Phrase paramPhrase)
    throws BadElementException
  {
    addCell(paramPhrase, this.curPosition);
  }

  public void addCell(Phrase paramPhrase, Point paramPoint)
    throws BadElementException
  {
    Cell localCell = new Cell(paramPhrase);
    localCell.setBorder(this.defaultCell.getBorder());
    localCell.setBorderWidth(this.defaultCell.getBorderWidth());
    localCell.setBorderColor(this.defaultCell.getBorderColor());
    localCell.setBackgroundColor(this.defaultCell.getBackgroundColor());
    localCell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
    localCell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
    localCell.setColspan(this.defaultCell.getColspan());
    localCell.setRowspan(this.defaultCell.getRowspan());
    addCell(localCell, paramPoint);
  }

  public void addCell(String paramString)
    throws BadElementException
  {
    addCell(new Phrase(paramString), this.curPosition);
  }

  public void addCell(String paramString, Point paramPoint)
    throws BadElementException
  {
    addCell(new Phrase(paramString), paramPoint);
  }

  public void insertTable(Table paramTable)
  {
    if (paramTable == null)
      throw new NullPointerException("insertTable - table has null-value");
    insertTable(paramTable, this.curPosition);
  }

  public void insertTable(Table paramTable, int paramInt1, int paramInt2)
  {
    if (paramTable == null)
      throw new NullPointerException("insertTable - table has null-value");
    insertTable(paramTable, new Point(paramInt1, paramInt2));
  }

  public void insertTable(Table paramTable, Point paramPoint)
  {
    if (paramTable == null)
      throw new NullPointerException("insertTable - table has null-value");
    if (paramPoint == null)
      throw new NullPointerException("insertTable - point has null-value");
    this.mTableInserted = true;
    paramTable.complete();
    if (paramPoint.y > this.columns)
      throw new IllegalArgumentException("insertTable -- wrong columnposition(" + paramPoint.y + ") of location; max =" + this.columns);
    int i = paramPoint.x + 1 - this.rows.size();
    int j = 0;
    if (i > 0)
      while (j < i)
      {
        this.rows.add(new Row(this.columns));
        j++;
      }
    ((Row)this.rows.get(paramPoint.x)).setElement(paramTable, paramPoint.y);
    setCurrentLocationToNextValidPosition(paramPoint);
  }

  public void addColumns(int paramInt)
  {
    ArrayList localArrayList = new ArrayList(this.rows.size());
    int i = this.columns + paramInt;
    for (int j = 0; j < this.rows.size(); j++)
    {
      Row localRow = new Row(i);
      for (k = 0; k < this.columns; k++)
        localRow.setElement(((Row)this.rows.get(j)).getCell(k), k);
      for (k = this.columns; (k < i) && (j < this.curPosition.x); k++)
        localRow.setElement(null, k);
      localArrayList.add(localRow);
    }
    float[] arrayOfFloat = new float[i];
    System.arraycopy(this.widths, 0, arrayOfFloat, 0, this.columns);
    for (int k = this.columns; k < i; k++)
      arrayOfFloat[k] = 0.0F;
    this.columns = i;
    this.widths = arrayOfFloat;
    this.rows = localArrayList;
  }

  public void deleteColumn(int paramInt)
    throws BadElementException
  {
    float[] arrayOfFloat = new float[--this.columns];
    System.arraycopy(this.widths, 0, arrayOfFloat, 0, paramInt);
    System.arraycopy(this.widths, paramInt + 1, arrayOfFloat, paramInt, this.columns - paramInt);
    setWidths(arrayOfFloat);
    System.arraycopy(this.widths, 0, arrayOfFloat, 0, this.columns);
    this.widths = arrayOfFloat;
    int i = this.rows.size();
    for (int j = 0; j < i; j++)
    {
      Row localRow = (Row)this.rows.get(j);
      localRow.deleteColumn(paramInt);
      this.rows.set(j, localRow);
    }
    if (paramInt == this.columns)
      this.curPosition.setLocation(this.curPosition.x + 1, 0);
  }

  public boolean deleteRow(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.rows.size()))
      return false;
    this.rows.remove(paramInt);
    this.curPosition.setLocation(this.curPosition.x - 1, this.curPosition.y);
    return true;
  }

  public void deleteAllRows()
  {
    this.rows.clear();
    this.rows.add(new Row(this.columns));
    this.curPosition.setLocation(0, 0);
    this.lastHeaderRow = -1;
  }

  public boolean deleteLastRow()
  {
    return deleteRow(this.rows.size() - 1);
  }

  public void complete()
  {
    if (this.mTableInserted)
    {
      mergeInsertedTables();
      this.mTableInserted = false;
    }
    if (this.autoFillEmptyCells)
      fillEmptyMatrixCells();
  }

  public Object getElement(int paramInt1, int paramInt2)
  {
    return ((Row)this.rows.get(paramInt1)).getCell(paramInt2);
  }

  private void mergeInsertedTables()
  {
    int i = 0;
    int j = 0;
    float[] arrayOfFloat1 = null;
    int[] arrayOfInt1 = new int[this.columns];
    float[][] arrayOfFloat = new float[this.columns][];
    int[] arrayOfInt2 = new int[this.rows.size()];
    ArrayList localArrayList = null;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    Table localTable = null;
    int i4;
    int i7;
    for (j = 0; j < this.columns; j++)
    {
      i2 = 1;
      float[] arrayOfFloat2 = null;
      for (i = 0; i < this.rows.size(); i++)
      {
        if (!Table.class.isInstance(((Row)this.rows.get(i)).getCell(j)))
          continue;
        k = 1;
        localTable = (Table)((Row)this.rows.get(i)).getCell(j);
        if (arrayOfFloat2 == null)
        {
          arrayOfFloat2 = localTable.widths;
          i2 = arrayOfFloat2.length;
        }
        else
        {
          i4 = localTable.getDimension().width;
          float[] arrayOfFloat3 = new float[i4 * arrayOfFloat2.length];
          float f1 = 0.0F;
          float f2 = 0.0F;
          float f3 = 0.0F;
          i7 = 0;
          int i8 = 0;
          int i10 = 0;
          f1 += arrayOfFloat2[0];
          f2 += localTable.widths[0];
          while ((i7 < arrayOfFloat2.length) && (i8 < i4))
          {
            if (f2 > f1)
            {
              arrayOfFloat3[i10] = (f1 - f3);
              i7++;
              if (i7 < arrayOfFloat2.length)
                f1 += arrayOfFloat2[i7];
            }
            else
            {
              arrayOfFloat3[i10] = (f2 - f3);
              i8++;
              if (Math.abs(f2 - f1) < 0.0001D)
              {
                i7++;
                if (i7 < arrayOfFloat2.length)
                  f1 += arrayOfFloat2[i7];
              }
              if (i8 < i4)
                f2 += localTable.widths[i8];
            }
            f3 += arrayOfFloat3[i10];
            i10++;
          }
          arrayOfFloat2 = new float[i10];
          System.arraycopy(arrayOfFloat3, 0, arrayOfFloat2, 0, i10);
          i2 = i10;
        }
      }
      arrayOfFloat[j] = arrayOfFloat2;
      n += i2;
      arrayOfInt1[j] = i2;
    }
    for (i = 0; i < this.rows.size(); i++)
    {
      i1 = 1;
      for (j = 0; j < this.columns; j++)
      {
        if (!Table.class.isInstance(((Row)this.rows.get(i)).getCell(j)))
          continue;
        k = 1;
        localTable = (Table)((Row)this.rows.get(i)).getCell(j);
        if (localTable.getDimension().height <= i1)
          continue;
        i1 = localTable.getDimension().height;
      }
      m += i1;
      arrayOfInt2[i] = i1;
    }
    if ((n != this.columns) || (m != this.rows.size()) || (k != 0))
    {
      arrayOfFloat1 = new float[n];
      int i3 = 0;
      for (i4 = 0; i4 < this.widths.length; i4++)
      {
        if (arrayOfInt1[i4] != 1)
          for (i5 = 0; i5 < arrayOfInt1[i4]; i5++)
          {
            arrayOfFloat1[i3] = (this.widths[i4] * arrayOfFloat[i4][i5] / 100.0F);
            i3++;
          }
        arrayOfFloat1[i3] = this.widths[i4];
        i3++;
      }
      localArrayList = new ArrayList(m);
      for (i = 0; i < m; i++)
        localArrayList.add(new Row(n));
      i4 = 0;
      int i5 = 0;
      Object localObject1 = null;
      for (i = 0; i < this.rows.size(); i++)
      {
        i5 = 0;
        i1 = 1;
        for (j = 0; j < this.columns; j++)
        {
          if (Table.class.isInstance(((Row)this.rows.get(i)).getCell(j)))
          {
            localTable = (Table)((Row)this.rows.get(i)).getCell(j);
            localObject2 = new int[localTable.widths.length + 1];
            int i6 = 0;
            i7 = 0;
            while (i6 < localTable.widths.length)
            {
              localObject2[i6] = (i5 + i7);
              float f4 = localTable.widths[i6];
              float f5 = 0.0F;
              while (i7 < arrayOfInt1[j])
              {
                f5 += arrayOfFloat[j][(i7++)];
                if (Math.abs(f4 - f5) >= 0.0001D)
                  continue;
              }
              i6++;
            }
            localObject2[i6] = (i5 + i7);
            for (int i9 = 0; i9 < localTable.getDimension().height; i9++)
              for (int i11 = 0; i11 < localTable.getDimension().width; i11++)
              {
                localObject1 = localTable.getElement(i9, i11);
                if (localObject1 == null)
                  continue;
                int i12 = i5 + i11;
                if (Cell.class.isInstance(localObject1))
                {
                  Cell localCell = (Cell)localObject1;
                  i12 = localObject2[i11];
                  int i13 = localObject2[(i11 + localCell.getColspan())];
                  localCell.setColspan(i13 - i12);
                }
                ((Row)localArrayList.get(i9 + i4)).addElement(localObject1, i12);
              }
          }
          Object localObject2 = getElement(i, j);
          if (Cell.class.isInstance(localObject2))
          {
            ((Cell)localObject2).setRowspan(((Cell)((Row)this.rows.get(i)).getCell(j)).getRowspan() + arrayOfInt2[i] - 1);
            ((Cell)localObject2).setColspan(((Cell)((Row)this.rows.get(i)).getCell(j)).getColspan() + arrayOfInt1[j] - 1);
            placeCell(localArrayList, (Cell)localObject2, new Point(i4, i5));
          }
          i5 += arrayOfInt1[j];
        }
        i4 += arrayOfInt2[i];
      }
      this.columns = n;
      this.rows = localArrayList;
      this.widths = arrayOfFloat1;
    }
  }

  private void fillEmptyMatrixCells()
  {
    try
    {
      for (int i = 0; i < this.rows.size(); i++)
        for (int j = 0; j < this.columns; j++)
        {
          if (((Row)this.rows.get(i)).isReserved(j))
            continue;
          addCell(this.defaultCell, new Point(i, j));
        }
    }
    catch (BadElementException localBadElementException)
    {
      throw new ExceptionConverter(localBadElementException);
    }
  }

  private boolean isValidLocation(Cell paramCell, Point paramPoint)
  {
    if (paramPoint.x < this.rows.size())
    {
      if (paramPoint.y + paramCell.getColspan() > this.columns)
        return false;
      int i = this.rows.size() - paramPoint.x > paramCell.getRowspan() ? paramCell.getRowspan() : this.rows.size() - paramPoint.x;
      int j = this.columns - paramPoint.y > paramCell.getColspan() ? paramCell.getColspan() : this.columns - paramPoint.y;
      for (int k = paramPoint.x; k < paramPoint.x + i; k++)
        for (int m = paramPoint.y; m < paramPoint.y + j; m++)
          if (((Row)this.rows.get(k)).isReserved(m))
            return false;
    }
    return paramPoint.y + paramCell.getColspan() <= this.columns;
  }

  private void assumeTableDefaults(Cell paramCell)
  {
    if (paramCell.getBorder() == -1)
      paramCell.setBorder(this.defaultCell.getBorder());
    if (paramCell.getBorderWidth() == -1.0F)
      paramCell.setBorderWidth(this.defaultCell.getBorderWidth());
    if (paramCell.getBorderColor() == null)
      paramCell.setBorderColor(this.defaultCell.getBorderColor());
    if (paramCell.getBackgroundColor() == null)
      paramCell.setBackgroundColor(this.defaultCell.getBackgroundColor());
    if (paramCell.getHorizontalAlignment() == -1)
      paramCell.setHorizontalAlignment(this.defaultCell.getHorizontalAlignment());
    if (paramCell.getVerticalAlignment() == -1)
      paramCell.setVerticalAlignment(this.defaultCell.getVerticalAlignment());
  }

  private void placeCell(ArrayList paramArrayList, Cell paramCell, Point paramPoint)
  {
    Row localRow = null;
    int j = paramPoint.x + paramCell.getRowspan() - paramArrayList.size();
    assumeTableDefaults(paramCell);
    if (paramPoint.x + paramCell.getRowspan() > paramArrayList.size())
      for (i = 0; i < j; i++)
      {
        localRow = new Row(this.columns);
        paramArrayList.add(localRow);
      }
    for (int i = paramPoint.x + 1; i < paramPoint.x + paramCell.getRowspan(); i++)
    {
      if (((Row)paramArrayList.get(i)).reserve(paramPoint.y, paramCell.getColspan()))
        continue;
      throw new RuntimeException("addCell - error in reserve");
    }
    localRow = (Row)paramArrayList.get(paramPoint.x);
    localRow.addElement(paramCell, paramPoint.y);
  }

  private void setCurrentLocationToNextValidPosition(Point paramPoint)
  {
    int i = paramPoint.x;
    int j = paramPoint.y;
    do
      if (j + 1 == this.columns)
      {
        i++;
        j = 0;
      }
      else
      {
        j++;
      }
    while ((i < this.rows.size()) && (j < this.columns) && (((Row)this.rows.get(i)).isReserved(j)));
    this.curPosition = new Point(i, j);
  }

  public float[] getWidths(float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat = new float[this.columns + 1];
    float f;
    if (this.locked)
      f = 100.0F * this.width / paramFloat2;
    else
      f = this.width;
    switch (this.alignment)
    {
    case 0:
      arrayOfFloat[0] = paramFloat1;
      break;
    case 2:
      arrayOfFloat[0] = (paramFloat1 + paramFloat2 * (100.0F - f) / 100.0F);
      break;
    case 1:
    default:
      arrayOfFloat[0] = (paramFloat1 + paramFloat2 * (100.0F - f) / 200.0F);
    }
    paramFloat2 = paramFloat2 * f / 100.0F;
    for (int i = 1; i < this.columns; i++)
      arrayOfFloat[i] = (arrayOfFloat[(i - 1)] + this.widths[(i - 1)] * paramFloat2 / 100.0F);
    arrayOfFloat[this.columns] = (arrayOfFloat[0] + paramFloat2);
    return arrayOfFloat;
  }

  public Iterator iterator()
  {
    return this.rows.iterator();
  }

  public PdfPTable createPdfPTable()
    throws BadElementException
  {
    if (!this.convert2pdfptable)
      throw new BadElementException("No error, just an old style table");
    setAutoFillEmptyCells(true);
    complete();
    PdfPTable localPdfPTable = new PdfPTable(this.widths);
    localPdfPTable.setComplete(this.complete);
    if (isNotAddedYet())
      localPdfPTable.setSkipFirstHeader(true);
    SimpleTable localSimpleTable = new SimpleTable();
    localSimpleTable.cloneNonPositionParameters(this);
    localSimpleTable.setCellspacing(this.cellspacing);
    localPdfPTable.setTableEvent(localSimpleTable);
    localPdfPTable.setHeaderRows(this.lastHeaderRow + 1);
    localPdfPTable.setSplitLate(this.cellsFitPage);
    localPdfPTable.setKeepTogether(this.tableFitsPage);
    if (!Float.isNaN(this.offset))
      localPdfPTable.setSpacingBefore(this.offset);
    localPdfPTable.setHorizontalAlignment(this.alignment);
    if (this.locked)
    {
      localPdfPTable.setTotalWidth(this.width);
      localPdfPTable.setLockedWidth(true);
    }
    else
    {
      localPdfPTable.setWidthPercentage(this.width);
    }
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Row localRow = (Row)localIterator.next();
      for (int i = 0; i < localRow.getColumns(); i++)
      {
        Element localElement;
        if ((localElement = (Element)localRow.getCell(i)) == null)
          continue;
        PdfPCell localPdfPCell;
        if ((localElement instanceof Table))
        {
          localPdfPCell = new PdfPCell(((Table)localElement).createPdfPTable());
        }
        else if ((localElement instanceof Cell))
        {
          localPdfPCell = ((Cell)localElement).createPdfPCell();
          localPdfPCell.setPadding(this.cellpadding + this.cellspacing / 2.0F);
          SimpleCell localSimpleCell = new SimpleCell(false);
          localSimpleCell.cloneNonPositionParameters((Cell)localElement);
          localSimpleCell.setSpacing(this.cellspacing * 2.0F);
          localPdfPCell.setCellEvent(localSimpleCell);
        }
        else
        {
          localPdfPCell = new PdfPCell();
        }
        localPdfPTable.addCell(localPdfPCell);
      }
    }
    return localPdfPTable;
  }

  public boolean isNotAddedYet()
  {
    return this.notAddedYet;
  }

  public void setNotAddedYet(boolean paramBoolean)
  {
    this.notAddedYet = paramBoolean;
  }

  public void flushContent()
  {
    setNotAddedYet(false);
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < getLastHeaderRow() + 1; i++)
      localArrayList.add(this.rows.get(i));
    this.rows = localArrayList;
  }

  public boolean isComplete()
  {
    return this.complete;
  }

  public void setComplete(boolean paramBoolean)
  {
    this.complete = paramBoolean;
  }

  /** @deprecated */
  public Cell getDefaultLayout()
  {
    return getDefaultCell();
  }

  /** @deprecated */
  public void setDefaultLayout(Cell paramCell)
  {
    this.defaultCell = paramCell;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Table
 * JD-Core Version:    0.6.0
 */