package com.lowagie.text.pdf;

import F;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Image;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.events.PdfPTableEventForwarder;
import java.util.ArrayList;

public class PdfPTable
  implements LargeElement
{
  public static final int BASECANVAS = 0;
  public static final int BACKGROUNDCANVAS = 1;
  public static final int LINECANVAS = 2;
  public static final int TEXTCANVAS = 3;
  protected ArrayList rows = new ArrayList();
  protected float totalHeight = 0.0F;
  protected PdfPCell[] currentRow;
  protected int currentRowIdx = 0;
  protected PdfPCell defaultCell = new PdfPCell((Phrase)null);
  protected float totalWidth = 0.0F;
  protected float[] relativeWidths;
  protected float[] absoluteWidths;
  protected PdfPTableEvent tableEvent;
  protected int headerRows;
  protected float widthPercentage = 80.0F;
  private int horizontalAlignment = 1;
  private boolean skipFirstHeader = false;
  private boolean skipLastFooter = false;
  protected boolean isColspan = false;
  protected int runDirection = 0;
  private boolean lockedWidth = false;
  private boolean splitRows = true;
  protected float spacingBefore;
  protected float spacingAfter;
  private boolean extendLastRow;
  private boolean headersInEvent;
  private boolean splitLate = true;
  private boolean keepTogether;
  protected boolean complete = true;
  private int footerRows;
  protected boolean rowCompleted = true;

  protected PdfPTable()
  {
  }

  public PdfPTable(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null)
      throw new NullPointerException("The widths array in PdfPTable constructor can not be null.");
    if (paramArrayOfFloat.length == 0)
      throw new IllegalArgumentException("The widths array in PdfPTable constructor can not have zero length.");
    this.relativeWidths = new float[paramArrayOfFloat.length];
    System.arraycopy(paramArrayOfFloat, 0, this.relativeWidths, 0, paramArrayOfFloat.length);
    this.absoluteWidths = new float[paramArrayOfFloat.length];
    calculateWidths();
    this.currentRow = new PdfPCell[this.absoluteWidths.length];
    this.keepTogether = false;
  }

  public PdfPTable(int paramInt)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException("The number of columns in PdfPTable constructor must be greater than zero.");
    this.relativeWidths = new float[paramInt];
    for (int i = 0; i < paramInt; i++)
      this.relativeWidths[i] = 1.0F;
    this.absoluteWidths = new float[this.relativeWidths.length];
    calculateWidths();
    this.currentRow = new PdfPCell[this.absoluteWidths.length];
    this.keepTogether = false;
  }

  public PdfPTable(PdfPTable paramPdfPTable)
  {
    copyFormat(paramPdfPTable);
    for (int i = 0; (i < this.currentRow.length) && (paramPdfPTable.currentRow[i] != null); i++)
      this.currentRow[i] = new PdfPCell(paramPdfPTable.currentRow[i]);
    for (i = 0; i < paramPdfPTable.rows.size(); i++)
    {
      PdfPRow localPdfPRow = (PdfPRow)paramPdfPTable.rows.get(i);
      if (localPdfPRow != null)
        localPdfPRow = new PdfPRow(localPdfPRow);
      this.rows.add(localPdfPRow);
    }
  }

  public static PdfPTable shallowCopy(PdfPTable paramPdfPTable)
  {
    PdfPTable localPdfPTable = new PdfPTable();
    localPdfPTable.copyFormat(paramPdfPTable);
    return localPdfPTable;
  }

  protected void copyFormat(PdfPTable paramPdfPTable)
  {
    this.relativeWidths = new float[paramPdfPTable.getNumberOfColumns()];
    this.absoluteWidths = new float[paramPdfPTable.getNumberOfColumns()];
    System.arraycopy(paramPdfPTable.relativeWidths, 0, this.relativeWidths, 0, getNumberOfColumns());
    System.arraycopy(paramPdfPTable.absoluteWidths, 0, this.absoluteWidths, 0, getNumberOfColumns());
    this.totalWidth = paramPdfPTable.totalWidth;
    this.totalHeight = paramPdfPTable.totalHeight;
    this.currentRowIdx = 0;
    this.tableEvent = paramPdfPTable.tableEvent;
    this.runDirection = paramPdfPTable.runDirection;
    this.defaultCell = new PdfPCell(paramPdfPTable.defaultCell);
    this.currentRow = new PdfPCell[paramPdfPTable.currentRow.length];
    this.isColspan = paramPdfPTable.isColspan;
    this.splitRows = paramPdfPTable.splitRows;
    this.spacingAfter = paramPdfPTable.spacingAfter;
    this.spacingBefore = paramPdfPTable.spacingBefore;
    this.headerRows = paramPdfPTable.headerRows;
    this.footerRows = paramPdfPTable.footerRows;
    this.lockedWidth = paramPdfPTable.lockedWidth;
    this.extendLastRow = paramPdfPTable.extendLastRow;
    this.headersInEvent = paramPdfPTable.headersInEvent;
    this.widthPercentage = paramPdfPTable.widthPercentage;
    this.splitLate = paramPdfPTable.splitLate;
    this.skipFirstHeader = paramPdfPTable.skipFirstHeader;
    this.skipLastFooter = paramPdfPTable.skipLastFooter;
    this.horizontalAlignment = paramPdfPTable.horizontalAlignment;
    this.keepTogether = paramPdfPTable.keepTogether;
    this.complete = paramPdfPTable.complete;
  }

  public void setWidths(float[] paramArrayOfFloat)
    throws DocumentException
  {
    if (paramArrayOfFloat.length != getNumberOfColumns())
      throw new DocumentException("Wrong number of columns.");
    this.relativeWidths = new float[paramArrayOfFloat.length];
    System.arraycopy(paramArrayOfFloat, 0, this.relativeWidths, 0, paramArrayOfFloat.length);
    this.absoluteWidths = new float[paramArrayOfFloat.length];
    this.totalHeight = 0.0F;
    calculateWidths();
    calculateHeights(true);
  }

  public void setWidths(int[] paramArrayOfInt)
    throws DocumentException
  {
    float[] arrayOfFloat = new float[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++)
      arrayOfFloat[i] = paramArrayOfInt[i];
    setWidths(arrayOfFloat);
  }

  protected void calculateWidths()
  {
    if (this.totalWidth <= 0.0F)
      return;
    float f = 0.0F;
    int i = getNumberOfColumns();
    for (int j = 0; j < i; j++)
      f += this.relativeWidths[j];
    for (j = 0; j < i; j++)
      this.absoluteWidths[j] = (this.totalWidth * this.relativeWidths[j] / f);
  }

  public void setTotalWidth(float paramFloat)
  {
    if (this.totalWidth == paramFloat)
      return;
    this.totalWidth = paramFloat;
    this.totalHeight = 0.0F;
    calculateWidths();
    calculateHeights(true);
  }

  public void setTotalWidth(float[] paramArrayOfFloat)
    throws DocumentException
  {
    if (paramArrayOfFloat.length != getNumberOfColumns())
      throw new DocumentException("Wrong number of columns.");
    this.totalWidth = 0.0F;
    for (int i = 0; i < paramArrayOfFloat.length; i++)
      this.totalWidth += paramArrayOfFloat[i];
    setWidths(paramArrayOfFloat);
  }

  public void setWidthPercentage(float[] paramArrayOfFloat, Rectangle paramRectangle)
    throws DocumentException
  {
    if (paramArrayOfFloat.length != getNumberOfColumns())
      throw new IllegalArgumentException("Wrong number of columns.");
    float f = 0.0F;
    for (int i = 0; i < paramArrayOfFloat.length; i++)
      f += paramArrayOfFloat[i];
    this.widthPercentage = (f / (paramRectangle.getRight() - paramRectangle.getLeft()) * 100.0F);
    setWidths(paramArrayOfFloat);
  }

  public float getTotalWidth()
  {
    return this.totalWidth;
  }

  public float calculateHeights(boolean paramBoolean)
  {
    if (this.totalWidth <= 0.0F)
      return 0.0F;
    this.totalHeight = 0.0F;
    for (int i = 0; i < this.rows.size(); i++)
      this.totalHeight += getRowHeight(i, paramBoolean);
    return this.totalHeight;
  }

  public void calculateHeightsFast()
  {
    calculateHeights(false);
  }

  public PdfPCell getDefaultCell()
  {
    return this.defaultCell;
  }

  public void addCell(PdfPCell paramPdfPCell)
  {
    this.rowCompleted = false;
    PdfPCell localPdfPCell1 = new PdfPCell(paramPdfPCell);
    int i = localPdfPCell1.getColspan();
    i = Math.max(i, 1);
    i = Math.min(i, this.currentRow.length - this.currentRowIdx);
    localPdfPCell1.setColspan(i);
    if (i != 1)
      this.isColspan = true;
    int j = localPdfPCell1.getRunDirection();
    if (j == 0)
      localPdfPCell1.setRunDirection(this.runDirection);
    skipColsWithRowspanAbove();
    int k = 0;
    if (this.currentRowIdx < this.currentRow.length)
    {
      this.currentRow[this.currentRowIdx] = localPdfPCell1;
      this.currentRowIdx += i;
      k = 1;
    }
    skipColsWithRowspanAbove();
    if (this.currentRowIdx >= this.currentRow.length)
    {
      int m = getNumberOfColumns();
      if (this.runDirection == 3)
      {
        localObject = new PdfPCell[m];
        int n = this.currentRow.length;
        for (int i1 = 0; i1 < this.currentRow.length; i1++)
        {
          PdfPCell localPdfPCell2 = this.currentRow[i1];
          int i2 = localPdfPCell2.getColspan();
          n -= i2;
          localObject[n] = localPdfPCell2;
          i1 += i2 - 1;
        }
        this.currentRow = ((PdfPCell)localObject);
      }
      Object localObject = new PdfPRow(this.currentRow);
      if (this.totalWidth > 0.0F)
      {
        ((PdfPRow)localObject).setWidths(this.absoluteWidths);
        this.totalHeight += ((PdfPRow)localObject).getMaxHeights();
      }
      this.rows.add(localObject);
      this.currentRow = new PdfPCell[m];
      this.currentRowIdx = 0;
      this.rowCompleted = true;
    }
    if (k == 0)
    {
      this.currentRow[this.currentRowIdx] = localPdfPCell1;
      this.currentRowIdx += i;
    }
  }

  private void skipColsWithRowspanAbove()
  {
    int i = 1;
    if (this.runDirection == 3)
      i = -1;
    while (rowSpanAbove(this.rows.size(), this.currentRowIdx))
      this.currentRowIdx += i;
  }

  boolean rowSpanAbove(int paramInt1, int paramInt2)
  {
    if ((paramInt2 >= getNumberOfColumns()) || (paramInt2 < 0) || (paramInt1 == 0))
      return false;
    int i = paramInt1 - 1;
    PdfPRow localPdfPRow = (PdfPRow)this.rows.get(i);
    if (localPdfPRow == null)
      return false;
    for (PdfPCell localPdfPCell = localPdfPRow.getCells()[paramInt2]; (localPdfPCell == null) && (i > 0); localPdfPCell = localPdfPRow.getCells()[paramInt2])
    {
      i--;
      localPdfPRow = (PdfPRow)this.rows.get(i);
      if (localPdfPRow == null)
        return false;
    }
    int j = paramInt1 - i;
    int k;
    if (localPdfPCell == null)
    {
      k = paramInt2 - 1;
      for (localPdfPCell = localPdfPRow.getCells()[k]; (localPdfPCell == null) && (i > 0); localPdfPCell = localPdfPRow.getCells()[k])
        k--;
      return (localPdfPCell != null) && (localPdfPCell.getRowspan() > j);
    }
    if ((localPdfPCell.getRowspan() == 1) && (j > 1))
    {
      k = paramInt2 - 1;
      localPdfPRow = (PdfPRow)this.rows.get(i + 1);
      j--;
      for (localPdfPCell = localPdfPRow.getCells()[k]; (localPdfPCell == null) && (k > 0); localPdfPCell = localPdfPRow.getCells()[k])
        k--;
    }
    return (localPdfPCell != null) && (localPdfPCell.getRowspan() > j);
  }

  public void addCell(String paramString)
  {
    addCell(new Phrase(paramString));
  }

  public void addCell(PdfPTable paramPdfPTable)
  {
    this.defaultCell.setTable(paramPdfPTable);
    addCell(this.defaultCell);
    this.defaultCell.setTable(null);
  }

  public void addCell(Image paramImage)
  {
    this.defaultCell.setImage(paramImage);
    addCell(this.defaultCell);
    this.defaultCell.setImage(null);
  }

  public void addCell(Phrase paramPhrase)
  {
    this.defaultCell.setPhrase(paramPhrase);
    addCell(this.defaultCell);
    this.defaultCell.setPhrase(null);
  }

  public float writeSelectedRows(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    return writeSelectedRows(0, -1, paramInt1, paramInt2, paramFloat1, paramFloat2, paramArrayOfPdfContentByte);
  }

  public float writeSelectedRows(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    if (this.totalWidth <= 0.0F)
      throw new RuntimeException("The table width must be greater than zero.");
    int i = this.rows.size();
    if (paramInt3 < 0)
      paramInt3 = 0;
    if (paramInt4 < 0)
      paramInt4 = i;
    else
      paramInt4 = Math.min(paramInt4, i);
    if (paramInt3 >= paramInt4)
      return paramFloat2;
    int j = getNumberOfColumns();
    if (paramInt1 < 0)
      paramInt1 = 0;
    else
      paramInt1 = Math.min(paramInt1, j);
    if (paramInt2 < 0)
      paramInt2 = j;
    else
      paramInt2 = Math.min(paramInt2, j);
    float f1 = paramFloat2;
    for (int k = paramInt3; k < paramInt4; k++)
    {
      PdfPRow localPdfPRow1 = (PdfPRow)this.rows.get(k);
      if (localPdfPRow1 == null)
        continue;
      localPdfPRow1.writeCells(paramInt1, paramInt2, paramFloat1, paramFloat2, paramArrayOfPdfContentByte);
      paramFloat2 -= localPdfPRow1.getMaxHeights();
    }
    if ((this.tableEvent != null) && (paramInt1 == 0) && (paramInt2 == j))
    {
      float[] arrayOfFloat = new float[paramInt4 - paramInt3 + 1];
      arrayOfFloat[0] = f1;
      for (int m = paramInt3; m < paramInt4; m++)
      {
        PdfPRow localPdfPRow2 = (PdfPRow)this.rows.get(m);
        float f2 = 0.0F;
        if (localPdfPRow2 != null)
          f2 = localPdfPRow2.getMaxHeights();
        arrayOfFloat[(m - paramInt3 + 1)] = (arrayOfFloat[(m - paramInt3)] - f2);
      }
      this.tableEvent.tableLayout(this, getEventWidths(paramFloat1, paramInt3, paramInt4, this.headersInEvent), arrayOfFloat, this.headersInEvent ? this.headerRows : 0, paramInt3, paramArrayOfPdfContentByte);
    }
    return paramFloat2;
  }

  public float writeSelectedRows(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, PdfContentByte paramPdfContentByte)
  {
    return writeSelectedRows(0, -1, paramInt1, paramInt2, paramFloat1, paramFloat2, paramPdfContentByte);
  }

  public float writeSelectedRows(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, PdfContentByte paramPdfContentByte)
  {
    int i = getNumberOfColumns();
    if (paramInt1 < 0)
      paramInt1 = 0;
    else
      paramInt1 = Math.min(paramInt1, i);
    if (paramInt2 < 0)
      paramInt2 = i;
    else
      paramInt2 = Math.min(paramInt2, i);
    int j = (paramInt1 != 0) || (paramInt2 != i) ? 1 : 0;
    if (j != 0)
    {
      float f1 = 0.0F;
      for (int k = paramInt1; k < paramInt2; k++)
        f1 += this.absoluteWidths[k];
      paramPdfContentByte.saveState();
      f2 = paramInt1 == 0 ? 10000.0F : 0.0F;
      float f3 = paramInt2 == i ? 10000.0F : 0.0F;
      paramPdfContentByte.rectangle(paramFloat1 - f2, -10000.0F, f1 + f2 + f3, 20000.0F);
      paramPdfContentByte.clip();
      paramPdfContentByte.newPath();
    }
    PdfContentByte[] arrayOfPdfContentByte = beginWritingRows(paramPdfContentByte);
    float f2 = writeSelectedRows(paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramFloat2, arrayOfPdfContentByte);
    endWritingRows(arrayOfPdfContentByte);
    if (j != 0)
      paramPdfContentByte.restoreState();
    return f2;
  }

  public static PdfContentByte[] beginWritingRows(PdfContentByte paramPdfContentByte)
  {
    return new PdfContentByte[] { paramPdfContentByte, paramPdfContentByte.getDuplicate(), paramPdfContentByte.getDuplicate(), paramPdfContentByte.getDuplicate() };
  }

  public static void endWritingRows(PdfContentByte[] paramArrayOfPdfContentByte)
  {
    PdfContentByte localPdfContentByte = paramArrayOfPdfContentByte[0];
    localPdfContentByte.saveState();
    localPdfContentByte.add(paramArrayOfPdfContentByte[1]);
    localPdfContentByte.restoreState();
    localPdfContentByte.saveState();
    localPdfContentByte.setLineCap(2);
    localPdfContentByte.resetRGBColorStroke();
    localPdfContentByte.add(paramArrayOfPdfContentByte[2]);
    localPdfContentByte.restoreState();
    localPdfContentByte.add(paramArrayOfPdfContentByte[3]);
  }

  public int size()
  {
    return this.rows.size();
  }

  public float getTotalHeight()
  {
    return this.totalHeight;
  }

  public float getRowHeight(int paramInt)
  {
    return getRowHeight(paramInt, false);
  }

  public float getRowHeight(int paramInt, boolean paramBoolean)
  {
    if ((this.totalWidth <= 0.0F) || (paramInt < 0) || (paramInt >= this.rows.size()))
      return 0.0F;
    PdfPRow localPdfPRow1 = (PdfPRow)this.rows.get(paramInt);
    if (localPdfPRow1 == null)
      return 0.0F;
    if (paramBoolean)
      localPdfPRow1.setWidths(this.absoluteWidths);
    float f1 = localPdfPRow1.getMaxHeights();
    for (int i = 0; i < this.relativeWidths.length; i++)
    {
      if (!rowSpanAbove(paramInt, i))
        continue;
      for (int j = 1; rowSpanAbove(paramInt - j, i); j++);
      PdfPRow localPdfPRow2 = (PdfPRow)this.rows.get(paramInt - j);
      PdfPCell localPdfPCell = localPdfPRow2.getCells()[i];
      float f2 = 0.0F;
      if (localPdfPCell.getRowspan() == j + 1)
      {
        f2 = localPdfPCell.getMaxHeight();
        while (j > 0)
        {
          f2 -= getRowHeight(paramInt - j);
          j--;
        }
      }
      if (f2 <= f1)
        continue;
      f1 = f2;
    }
    localPdfPRow1.setMaxHeights(f1);
    return f1;
  }

  public float getRowspanHeight(int paramInt1, int paramInt2)
  {
    if ((this.totalWidth <= 0.0F) || (paramInt1 < 0) || (paramInt1 >= this.rows.size()))
      return 0.0F;
    PdfPRow localPdfPRow = (PdfPRow)this.rows.get(paramInt1);
    if ((localPdfPRow == null) || (paramInt2 >= localPdfPRow.getCells().length))
      return 0.0F;
    PdfPCell localPdfPCell = localPdfPRow.getCells()[paramInt2];
    if (localPdfPCell == null)
      return 0.0F;
    float f = 0.0F;
    for (int i = 0; i < localPdfPCell.getRowspan(); i++)
      f += getRowHeight(paramInt1 + i);
    return f;
  }

  public float getHeaderHeight()
  {
    float f = 0.0F;
    int i = Math.min(this.rows.size(), this.headerRows);
    for (int j = 0; j < i; j++)
    {
      PdfPRow localPdfPRow = (PdfPRow)this.rows.get(j);
      if (localPdfPRow == null)
        continue;
      f += localPdfPRow.getMaxHeights();
    }
    return f;
  }

  public float getFooterHeight()
  {
    float f = 0.0F;
    int i = Math.max(0, this.headerRows - this.footerRows);
    int j = Math.min(this.rows.size(), this.headerRows);
    for (int k = i; k < j; k++)
    {
      PdfPRow localPdfPRow = (PdfPRow)this.rows.get(k);
      if (localPdfPRow == null)
        continue;
      f += localPdfPRow.getMaxHeights();
    }
    return f;
  }

  public boolean deleteRow(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.rows.size()))
      return false;
    if (this.totalWidth > 0.0F)
    {
      PdfPRow localPdfPRow = (PdfPRow)this.rows.get(paramInt);
      if (localPdfPRow != null)
        this.totalHeight -= localPdfPRow.getMaxHeights();
    }
    this.rows.remove(paramInt);
    if (paramInt < this.headerRows)
    {
      this.headerRows -= 1;
      if (paramInt >= this.headerRows - this.footerRows)
        this.footerRows -= 1;
    }
    return true;
  }

  public boolean deleteLastRow()
  {
    return deleteRow(this.rows.size() - 1);
  }

  public void deleteBodyRows()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < this.headerRows; i++)
      localArrayList.add(this.rows.get(i));
    this.rows = localArrayList;
    this.totalHeight = 0.0F;
    if (this.totalWidth > 0.0F)
      this.totalHeight = getHeaderHeight();
  }

  public int getNumberOfColumns()
  {
    return this.relativeWidths.length;
  }

  public int getHeaderRows()
  {
    return this.headerRows;
  }

  public void setHeaderRows(int paramInt)
  {
    if (paramInt < 0)
      paramInt = 0;
    this.headerRows = paramInt;
  }

  public ArrayList getChunks()
  {
    return new ArrayList();
  }

  public int type()
  {
    return 23;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
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

  public float getWidthPercentage()
  {
    return this.widthPercentage;
  }

  public void setWidthPercentage(float paramFloat)
  {
    this.widthPercentage = paramFloat;
  }

  public int getHorizontalAlignment()
  {
    return this.horizontalAlignment;
  }

  public void setHorizontalAlignment(int paramInt)
  {
    this.horizontalAlignment = paramInt;
  }

  public PdfPRow getRow(int paramInt)
  {
    return (PdfPRow)this.rows.get(paramInt);
  }

  public ArrayList getRows()
  {
    return this.rows;
  }

  public ArrayList getRows(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramInt1 < 0) || (paramInt2 > size()))
      return localArrayList;
    PdfPRow localPdfPRow1 = adjustCellsInRow(paramInt1, paramInt2);
    int i = 0;
    while (i < getNumberOfColumns())
    {
      j = paramInt1;
      while (rowSpanAbove(j--, i))
      {
        PdfPRow localPdfPRow2 = getRow(j);
        if (localPdfPRow2 == null)
          continue;
        PdfPCell localPdfPCell2 = localPdfPRow2.getCells()[i];
        if (localPdfPCell2 == null)
          continue;
        localPdfPRow1.getCells()[i] = new PdfPCell(localPdfPCell2);
        float f1 = 0.0F;
        int k = Math.min(j + localPdfPCell2.getRowspan(), paramInt2);
        for (int m = paramInt1 + 1; m < k; m++)
          f1 += getRowHeight(m);
        localPdfPRow1.setExtraHeight(i, f1);
        float f2 = getRowspanHeight(j, i) - getRowHeight(paramInt1) - f1;
        localPdfPRow1.getCells()[i].consumeHeight(f2);
      }
      PdfPCell localPdfPCell1 = localPdfPRow1.getCells()[i];
      if (localPdfPCell1 == null)
      {
        i++;
        continue;
      }
      i += localPdfPCell1.getColspan();
    }
    localArrayList.add(localPdfPRow1);
    for (int j = paramInt1 + 1; j < paramInt2; j++)
      localArrayList.add(adjustCellsInRow(j, paramInt2));
    return localArrayList;
  }

  protected PdfPRow adjustCellsInRow(int paramInt1, int paramInt2)
  {
    PdfPRow localPdfPRow = new PdfPRow(getRow(paramInt1));
    localPdfPRow.initExtraHeights();
    PdfPCell[] arrayOfPdfPCell = localPdfPRow.getCells();
    for (int i = 0; i < arrayOfPdfPCell.length; i++)
    {
      PdfPCell localPdfPCell = arrayOfPdfPCell[i];
      if ((localPdfPCell == null) || (localPdfPCell.getRowspan() == 1))
        continue;
      int j = Math.min(paramInt2, paramInt1 + localPdfPCell.getRowspan());
      float f = 0.0F;
      for (int k = paramInt1 + 1; k < j; k++)
        f += getRowHeight(k);
      localPdfPRow.setExtraHeight(i, f);
    }
    return localPdfPRow;
  }

  public void setTableEvent(PdfPTableEvent paramPdfPTableEvent)
  {
    if (paramPdfPTableEvent == null)
    {
      this.tableEvent = null;
    }
    else if (this.tableEvent == null)
    {
      this.tableEvent = paramPdfPTableEvent;
    }
    else if ((this.tableEvent instanceof PdfPTableEventForwarder))
    {
      ((PdfPTableEventForwarder)this.tableEvent).addTableEvent(paramPdfPTableEvent);
    }
    else
    {
      PdfPTableEventForwarder localPdfPTableEventForwarder = new PdfPTableEventForwarder();
      localPdfPTableEventForwarder.addTableEvent(this.tableEvent);
      localPdfPTableEventForwarder.addTableEvent(paramPdfPTableEvent);
      this.tableEvent = localPdfPTableEventForwarder;
    }
  }

  public PdfPTableEvent getTableEvent()
  {
    return this.tableEvent;
  }

  public float[] getAbsoluteWidths()
  {
    return this.absoluteWidths;
  }

  float[][] getEventWidths(float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramInt1 = Math.max(paramInt1, this.headerRows);
      paramInt2 = Math.max(paramInt2, this.headerRows);
    }
    float[][] arrayOfFloat = new float[(paramBoolean ? this.headerRows : 0) + paramInt2 - paramInt1][];
    if (this.isColspan)
    {
      i = 0;
      if (paramBoolean)
        for (int j = 0; j < this.headerRows; j++)
        {
          PdfPRow localPdfPRow = (PdfPRow)this.rows.get(j);
          if (localPdfPRow == null)
            i++;
          else
            arrayOfFloat[(i++)] = localPdfPRow.getEventWidth(paramFloat);
        }
      while (paramInt1 < paramInt2)
      {
        localObject = (PdfPRow)this.rows.get(paramInt1);
        if (localObject == null)
          i++;
        else
          arrayOfFloat[(i++)] = ((PdfPRow)localObject).getEventWidth(paramFloat);
        paramInt1++;
      }
    }
    int i = getNumberOfColumns();
    Object localObject = new float[i + 1];
    localObject[0] = paramFloat;
    for (int k = 0; k < i; k++)
      localObject[(k + 1)] = (localObject[k] + this.absoluteWidths[k]);
    for (k = 0; k < arrayOfFloat.length; k++)
      arrayOfFloat[k] = localObject;
    return (F)arrayOfFloat;
  }

  public boolean isSkipFirstHeader()
  {
    return this.skipFirstHeader;
  }

  public boolean isSkipLastFooter()
  {
    return this.skipLastFooter;
  }

  public void setSkipFirstHeader(boolean paramBoolean)
  {
    this.skipFirstHeader = paramBoolean;
  }

  public void setSkipLastFooter(boolean paramBoolean)
  {
    this.skipLastFooter = paramBoolean;
  }

  public void setRunDirection(int paramInt)
  {
    switch (paramInt)
    {
    case 0:
    case 1:
    case 2:
    case 3:
      this.runDirection = paramInt;
      break;
    default:
      throw new RuntimeException("Invalid run direction: " + paramInt);
    }
  }

  public int getRunDirection()
  {
    return this.runDirection;
  }

  public boolean isLockedWidth()
  {
    return this.lockedWidth;
  }

  public void setLockedWidth(boolean paramBoolean)
  {
    this.lockedWidth = paramBoolean;
  }

  public boolean isSplitRows()
  {
    return this.splitRows;
  }

  public void setSplitRows(boolean paramBoolean)
  {
    this.splitRows = paramBoolean;
  }

  public void setSpacingBefore(float paramFloat)
  {
    this.spacingBefore = paramFloat;
  }

  public void setSpacingAfter(float paramFloat)
  {
    this.spacingAfter = paramFloat;
  }

  public float spacingBefore()
  {
    return this.spacingBefore;
  }

  public float spacingAfter()
  {
    return this.spacingAfter;
  }

  public boolean isExtendLastRow()
  {
    return this.extendLastRow;
  }

  public void setExtendLastRow(boolean paramBoolean)
  {
    this.extendLastRow = paramBoolean;
  }

  public boolean isHeadersInEvent()
  {
    return this.headersInEvent;
  }

  public void setHeadersInEvent(boolean paramBoolean)
  {
    this.headersInEvent = paramBoolean;
  }

  public boolean isSplitLate()
  {
    return this.splitLate;
  }

  public void setSplitLate(boolean paramBoolean)
  {
    this.splitLate = paramBoolean;
  }

  public void setKeepTogether(boolean paramBoolean)
  {
    this.keepTogether = paramBoolean;
  }

  public boolean getKeepTogether()
  {
    return this.keepTogether;
  }

  public int getFooterRows()
  {
    return this.footerRows;
  }

  public void setFooterRows(int paramInt)
  {
    if (paramInt < 0)
      paramInt = 0;
    this.footerRows = paramInt;
  }

  public void completeRow()
  {
    while (!this.rowCompleted)
      addCell(this.defaultCell);
  }

  public void flushContent()
  {
    deleteBodyRows();
    setSkipFirstHeader(true);
  }

  public boolean isComplete()
  {
    return this.complete;
  }

  public void setComplete(boolean paramBoolean)
  {
    this.complete = paramBoolean;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPTable
 * JD-Core Version:    0.6.0
 */