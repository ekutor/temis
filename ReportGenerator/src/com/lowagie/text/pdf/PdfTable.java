package com.lowagie.text.pdf;

import com.lowagie.text.Cell;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Row;
import com.lowagie.text.Table;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfTable extends Rectangle
{
  private int columns;
  private ArrayList headercells;
  private ArrayList cells;
  protected Table table;
  protected float[] positions;

  PdfTable(Table paramTable, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    super(paramFloat1, paramFloat3, paramFloat2, paramFloat3);
    this.table = paramTable;
    paramTable.complete();
    cloneNonPositionParameters(paramTable);
    this.columns = paramTable.getColumns();
    this.positions = paramTable.getWidths(paramFloat1, paramFloat2 - paramFloat1);
    setLeft(this.positions[0]);
    setRight(this.positions[(this.positions.length - 1)]);
    this.headercells = new ArrayList();
    this.cells = new ArrayList();
    updateRowAdditionsInternal();
  }

  void updateRowAdditions()
  {
    this.table.complete();
    updateRowAdditionsInternal();
    this.table.deleteAllRows();
  }

  private void updateRowAdditionsInternal()
  {
    int i = rows();
    int j = 0;
    int k = 0;
    int m = this.table.getLastHeaderRow() + 1;
    ArrayList localArrayList = new ArrayList();
    int n = this.table.size() + 1;
    float[] arrayOfFloat = new float[n];
    for (int i1 = 0; i1 < n; i1++)
      arrayOfFloat[i1] = getBottom();
    Iterator localIterator = this.table.iterator();
    PdfCell localPdfCell;
    while (localIterator.hasNext())
    {
      boolean bool = false;
      Row localRow = (Row)localIterator.next();
      if (localRow.isEmpty())
      {
        if ((j < n - 1) && (arrayOfFloat[(j + 1)] > arrayOfFloat[j]))
          arrayOfFloat[(j + 1)] = arrayOfFloat[j];
      }
      else
        for (i3 = 0; i3 < localRow.getColumns(); i3++)
        {
          Cell localCell = (Cell)localRow.getCell(i3);
          if (localCell == null)
            continue;
          localPdfCell = new PdfCell(localCell, j + i, this.positions[i3], this.positions[(i3 + localCell.getColspan())], arrayOfFloat[j], cellspacing(), cellpadding());
          if (j < m)
          {
            localPdfCell.setHeader();
            this.headercells.add(localPdfCell);
            if (!this.table.isNotAddedYet())
              continue;
          }
          try
          {
            if (arrayOfFloat[j] - localPdfCell.getHeight() - cellpadding() < arrayOfFloat[(j + localPdfCell.rowspan())])
              arrayOfFloat[(j + localPdfCell.rowspan())] = (arrayOfFloat[j] - localPdfCell.getHeight() - cellpadding());
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1)
          {
            if (arrayOfFloat[j] - localPdfCell.getHeight() < arrayOfFloat[(n - 1)])
              arrayOfFloat[(n - 1)] = (arrayOfFloat[j] - localPdfCell.getHeight());
          }
          localPdfCell.setGroupNumber(k);
          bool |= localCell.getGroupChange();
          localArrayList.add(localPdfCell);
        }
      j++;
      if (!bool)
        continue;
      k++;
    }
    int i2 = localArrayList.size();
    for (int i3 = 0; i3 < i2; i3++)
    {
      localPdfCell = (PdfCell)localArrayList.get(i3);
      try
      {
        localPdfCell.setBottom(arrayOfFloat[(localPdfCell.rownumber() - i + localPdfCell.rowspan())]);
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException2)
      {
        localPdfCell.setBottom(arrayOfFloat[(n - 1)]);
      }
    }
    this.cells.addAll(localArrayList);
    setBottom(arrayOfFloat[(n - 1)]);
  }

  int rows()
  {
    return this.cells.isEmpty() ? 0 : ((PdfCell)this.cells.get(this.cells.size() - 1)).rownumber() + 1;
  }

  public int type()
  {
    return 22;
  }

  ArrayList getHeaderCells()
  {
    return this.headercells;
  }

  boolean hasHeader()
  {
    return !this.headercells.isEmpty();
  }

  ArrayList getCells()
  {
    return this.cells;
  }

  int columns()
  {
    return this.columns;
  }

  final float cellpadding()
  {
    return this.table.getPadding();
  }

  final float cellspacing()
  {
    return this.table.getSpacing();
  }

  public final boolean hasToFitPageTable()
  {
    return this.table.isTableFitsPage();
  }

  public final boolean hasToFitPageCells()
  {
    return this.table.isCellsFitPage();
  }

  public float getOffset()
  {
    return this.table.getOffset();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfTable
 * JD-Core Version:    0.6.0
 */