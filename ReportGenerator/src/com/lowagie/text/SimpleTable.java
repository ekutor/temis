package com.lowagie.text;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleTable extends Rectangle
  implements PdfPTableEvent, TextElementArray
{
  private ArrayList content = new ArrayList();
  private float width = 0.0F;
  private float widthpercentage = 0.0F;
  private float cellspacing;
  private float cellpadding;
  private int alignment;

  public SimpleTable()
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    setBorder(15);
    setBorderWidth(2.0F);
  }

  public void addElement(SimpleCell paramSimpleCell)
    throws BadElementException
  {
    if (!paramSimpleCell.isCellgroup())
      throw new BadElementException("You can't add cells to a table directly, add them to a row first.");
    this.content.add(paramSimpleCell);
  }

  public Table createTable()
    throws BadElementException
  {
    if (this.content.isEmpty())
      throw new BadElementException("Trying to create a table without rows.");
    SimpleCell localSimpleCell1 = (SimpleCell)this.content.get(0);
    int i = 0;
    Object localObject = localSimpleCell1.getContent().iterator();
    SimpleCell localSimpleCell2;
    while (((Iterator)localObject).hasNext())
    {
      localSimpleCell2 = (SimpleCell)((Iterator)localObject).next();
      i += localSimpleCell2.getColspan();
    }
    localObject = new float[i];
    float[] arrayOfFloat = new float[i];
    Table localTable = new Table(i);
    localTable.setAlignment(this.alignment);
    localTable.setSpacing(this.cellspacing);
    localTable.setPadding(this.cellpadding);
    localTable.cloneNonPositionParameters(this);
    Iterator localIterator1 = this.content.iterator();
    while (localIterator1.hasNext())
    {
      localSimpleCell1 = (SimpleCell)localIterator1.next();
      int j = 0;
      Iterator localIterator2 = localSimpleCell1.getContent().iterator();
      while (localIterator2.hasNext())
      {
        localSimpleCell2 = (SimpleCell)localIterator2.next();
        localTable.addCell(localSimpleCell2.createCell(localSimpleCell1));
        if (localSimpleCell2.getColspan() == 1)
        {
          if (localSimpleCell2.getWidth() > 0.0F)
            localObject[j] = localSimpleCell2.getWidth();
          if (localSimpleCell2.getWidthpercentage() > 0.0F)
            arrayOfFloat[j] = localSimpleCell2.getWidthpercentage();
        }
        j += localSimpleCell2.getColspan();
      }
    }
    float f = 0.0F;
    for (int k = 0; k < i; k++)
    {
      if (localObject[k] == 0.0F)
      {
        f = 0.0F;
        break;
      }
      f += localObject[k];
    }
    if (f > 0.0F)
    {
      localTable.setWidth(f);
      localTable.setLocked(true);
      localTable.setWidths(localObject);
    }
    else
    {
      for (k = 0; k < i; k++)
      {
        if (arrayOfFloat[k] == 0.0F)
        {
          f = 0.0F;
          break;
        }
        f += arrayOfFloat[k];
      }
      if (f > 0.0F)
        localTable.setWidths(arrayOfFloat);
    }
    if (this.width > 0.0F)
    {
      localTable.setWidth(this.width);
      localTable.setLocked(true);
    }
    else if (this.widthpercentage > 0.0F)
    {
      localTable.setWidth(this.widthpercentage);
    }
    return (Table)localTable;
  }

  public PdfPTable createPdfPTable()
    throws DocumentException
  {
    if (this.content.isEmpty())
      throw new BadElementException("Trying to create a table without rows.");
    SimpleCell localSimpleCell1 = (SimpleCell)this.content.get(0);
    int i = 0;
    Object localObject = localSimpleCell1.getContent().iterator();
    SimpleCell localSimpleCell2;
    while (((Iterator)localObject).hasNext())
    {
      localSimpleCell2 = (SimpleCell)((Iterator)localObject).next();
      i += localSimpleCell2.getColspan();
    }
    localObject = new float[i];
    float[] arrayOfFloat = new float[i];
    PdfPTable localPdfPTable = new PdfPTable(i);
    localPdfPTable.setTableEvent(this);
    localPdfPTable.setHorizontalAlignment(this.alignment);
    Iterator localIterator1 = this.content.iterator();
    while (localIterator1.hasNext())
    {
      localSimpleCell1 = (SimpleCell)localIterator1.next();
      int j = 0;
      Iterator localIterator2 = localSimpleCell1.getContent().iterator();
      while (localIterator2.hasNext())
      {
        localSimpleCell2 = (SimpleCell)localIterator2.next();
        if (Float.isNaN(localSimpleCell2.getSpacing_left()))
          localSimpleCell2.setSpacing_left(this.cellspacing / 2.0F);
        if (Float.isNaN(localSimpleCell2.getSpacing_right()))
          localSimpleCell2.setSpacing_right(this.cellspacing / 2.0F);
        if (Float.isNaN(localSimpleCell2.getSpacing_top()))
          localSimpleCell2.setSpacing_top(this.cellspacing / 2.0F);
        if (Float.isNaN(localSimpleCell2.getSpacing_bottom()))
          localSimpleCell2.setSpacing_bottom(this.cellspacing / 2.0F);
        localSimpleCell2.setPadding(this.cellpadding);
        localPdfPTable.addCell(localSimpleCell2.createPdfPCell(localSimpleCell1));
        if (localSimpleCell2.getColspan() == 1)
        {
          if (localSimpleCell2.getWidth() > 0.0F)
            localObject[j] = localSimpleCell2.getWidth();
          if (localSimpleCell2.getWidthpercentage() > 0.0F)
            arrayOfFloat[j] = localSimpleCell2.getWidthpercentage();
        }
        j += localSimpleCell2.getColspan();
      }
    }
    float f = 0.0F;
    for (int k = 0; k < i; k++)
    {
      if (localObject[k] == 0.0F)
      {
        f = 0.0F;
        break;
      }
      f += localObject[k];
    }
    if (f > 0.0F)
    {
      localPdfPTable.setTotalWidth(f);
      localPdfPTable.setWidths(localObject);
    }
    else
    {
      for (k = 0; k < i; k++)
      {
        if (arrayOfFloat[k] == 0.0F)
        {
          f = 0.0F;
          break;
        }
        f += arrayOfFloat[k];
      }
      if (f > 0.0F)
        localPdfPTable.setWidths(arrayOfFloat);
    }
    if (this.width > 0.0F)
      localPdfPTable.setTotalWidth(this.width);
    if (this.widthpercentage > 0.0F)
      localPdfPTable.setWidthPercentage(this.widthpercentage);
    return (PdfPTable)localPdfPTable;
  }

  public void tableLayout(PdfPTable paramPdfPTable, float[][] paramArrayOfFloat, float[] paramArrayOfFloat1, int paramInt1, int paramInt2, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    float[] arrayOfFloat = paramArrayOfFloat[0];
    Rectangle localRectangle = new Rectangle(arrayOfFloat[0], paramArrayOfFloat1[(paramArrayOfFloat1.length - 1)], arrayOfFloat[(arrayOfFloat.length - 1)], paramArrayOfFloat1[0]);
    localRectangle.cloneNonPositionParameters(this);
    int i = localRectangle.getBorder();
    localRectangle.setBorder(0);
    paramArrayOfPdfContentByte[1].rectangle(localRectangle);
    localRectangle.setBorder(i);
    localRectangle.setBackgroundColor(null);
    paramArrayOfPdfContentByte[2].rectangle(localRectangle);
  }

  public float getCellpadding()
  {
    return this.cellpadding;
  }

  public void setCellpadding(float paramFloat)
  {
    this.cellpadding = paramFloat;
  }

  public float getCellspacing()
  {
    return this.cellspacing;
  }

  public void setCellspacing(float paramFloat)
  {
    this.cellspacing = paramFloat;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public float getWidth()
  {
    return this.width;
  }

  public void setWidth(float paramFloat)
  {
    this.width = paramFloat;
  }

  public float getWidthpercentage()
  {
    return this.widthpercentage;
  }

  public void setWidthpercentage(float paramFloat)
  {
    this.widthpercentage = paramFloat;
  }

  public int type()
  {
    return 22;
  }

  public boolean isNestable()
  {
    return true;
  }

  public boolean add(Object paramObject)
  {
    try
    {
      addElement((SimpleCell)paramObject);
      return true;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (BadElementException localBadElementException)
    {
    }
    throw new ExceptionConverter(localBadElementException);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.SimpleTable
 * JD-Core Version:    0.6.0
 */