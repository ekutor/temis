package com.lowagie.text;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleCell extends Rectangle
  implements PdfPCellEvent, TextElementArray
{
  public static final boolean ROW = true;
  public static final boolean CELL = false;
  private ArrayList content = new ArrayList();
  private float width = 0.0F;
  private float widthpercentage = 0.0F;
  private float spacing_left = (0.0F / 0.0F);
  private float spacing_right = (0.0F / 0.0F);
  private float spacing_top = (0.0F / 0.0F);
  private float spacing_bottom = (0.0F / 0.0F);
  private float padding_left = (0.0F / 0.0F);
  private float padding_right = (0.0F / 0.0F);
  private float padding_top = (0.0F / 0.0F);
  private float padding_bottom = (0.0F / 0.0F);
  private int colspan = 1;
  private int horizontalAlignment = -1;
  private int verticalAlignment = -1;
  private boolean cellgroup = false;
  protected boolean useAscender = false;
  protected boolean useDescender = false;
  protected boolean useBorderPadding;

  public SimpleCell(boolean paramBoolean)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.cellgroup = paramBoolean;
    setBorder(15);
  }

  public void addElement(Element paramElement)
    throws BadElementException
  {
    if (this.cellgroup)
    {
      if ((paramElement instanceof SimpleCell))
      {
        if (((SimpleCell)paramElement).isCellgroup())
          throw new BadElementException("You can't add one row to another row.");
        this.content.add(paramElement);
        return;
      }
      throw new BadElementException("You can only add cells to rows, no objects of type " + paramElement.getClass().getName());
    }
    if ((paramElement.type() == 12) || (paramElement.type() == 11) || (paramElement.type() == 17) || (paramElement.type() == 10) || (paramElement.type() == 14) || (paramElement.type() == 50) || (paramElement.type() == 32) || (paramElement.type() == 33) || (paramElement.type() == 36) || (paramElement.type() == 34) || (paramElement.type() == 35))
      this.content.add(paramElement);
    else
      throw new BadElementException("You can't add an element of type " + paramElement.getClass().getName() + " to a SimpleCell.");
  }

  public Cell createCell(SimpleCell paramSimpleCell)
    throws BadElementException
  {
    Cell localCell = new Cell();
    localCell.cloneNonPositionParameters(paramSimpleCell);
    localCell.softCloneNonPositionParameters(this);
    localCell.setColspan(this.colspan);
    localCell.setHorizontalAlignment(this.horizontalAlignment);
    localCell.setVerticalAlignment(this.verticalAlignment);
    localCell.setUseAscender(this.useAscender);
    localCell.setUseBorderPadding(this.useBorderPadding);
    localCell.setUseDescender(this.useDescender);
    Iterator localIterator = this.content.iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      localCell.addElement(localElement);
    }
    return localCell;
  }

  public PdfPCell createPdfPCell(SimpleCell paramSimpleCell)
  {
    PdfPCell localPdfPCell = new PdfPCell();
    localPdfPCell.setBorder(0);
    SimpleCell localSimpleCell = new SimpleCell(false);
    localSimpleCell.setSpacing_left(this.spacing_left);
    localSimpleCell.setSpacing_right(this.spacing_right);
    localSimpleCell.setSpacing_top(this.spacing_top);
    localSimpleCell.setSpacing_bottom(this.spacing_bottom);
    localSimpleCell.cloneNonPositionParameters(paramSimpleCell);
    localSimpleCell.softCloneNonPositionParameters(this);
    localPdfPCell.setCellEvent(localSimpleCell);
    localPdfPCell.setHorizontalAlignment(paramSimpleCell.horizontalAlignment);
    localPdfPCell.setVerticalAlignment(paramSimpleCell.verticalAlignment);
    localPdfPCell.setUseAscender(paramSimpleCell.useAscender);
    localPdfPCell.setUseBorderPadding(paramSimpleCell.useBorderPadding);
    localPdfPCell.setUseDescender(paramSimpleCell.useDescender);
    localPdfPCell.setColspan(this.colspan);
    if (this.horizontalAlignment != -1)
      localPdfPCell.setHorizontalAlignment(this.horizontalAlignment);
    if (this.verticalAlignment != -1)
      localPdfPCell.setVerticalAlignment(this.verticalAlignment);
    if (this.useAscender)
      localPdfPCell.setUseAscender(this.useAscender);
    if (this.useBorderPadding)
      localPdfPCell.setUseBorderPadding(this.useBorderPadding);
    if (this.useDescender)
      localPdfPCell.setUseDescender(this.useDescender);
    float f2 = this.spacing_left;
    if (Float.isNaN(f2))
      f2 = 0.0F;
    float f3 = this.spacing_right;
    if (Float.isNaN(f3))
      f3 = 0.0F;
    float f4 = this.spacing_top;
    if (Float.isNaN(f4))
      f4 = 0.0F;
    float f5 = this.spacing_bottom;
    if (Float.isNaN(f5))
      f5 = 0.0F;
    float f1 = this.padding_left;
    if (Float.isNaN(f1))
      f1 = 0.0F;
    localPdfPCell.setPaddingLeft(f1 + f2);
    f1 = this.padding_right;
    if (Float.isNaN(f1))
      f1 = 0.0F;
    localPdfPCell.setPaddingRight(f1 + f3);
    f1 = this.padding_top;
    if (Float.isNaN(f1))
      f1 = 0.0F;
    localPdfPCell.setPaddingTop(f1 + f4);
    f1 = this.padding_bottom;
    if (Float.isNaN(f1))
      f1 = 0.0F;
    localPdfPCell.setPaddingBottom(f1 + f5);
    Iterator localIterator = this.content.iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      localPdfPCell.addElement(localElement);
    }
    return localPdfPCell;
  }

  public void cellLayout(PdfPCell paramPdfPCell, Rectangle paramRectangle, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    float f1 = this.spacing_left;
    if (Float.isNaN(f1))
      f1 = 0.0F;
    float f2 = this.spacing_right;
    if (Float.isNaN(f2))
      f2 = 0.0F;
    float f3 = this.spacing_top;
    if (Float.isNaN(f3))
      f3 = 0.0F;
    float f4 = this.spacing_bottom;
    if (Float.isNaN(f4))
      f4 = 0.0F;
    Rectangle localRectangle = new Rectangle(paramRectangle.getLeft(f1), paramRectangle.getBottom(f4), paramRectangle.getRight(f2), paramRectangle.getTop(f3));
    localRectangle.cloneNonPositionParameters(this);
    paramArrayOfPdfContentByte[1].rectangle(localRectangle);
    localRectangle.setBackgroundColor(null);
    paramArrayOfPdfContentByte[2].rectangle(localRectangle);
  }

  public void setPadding(float paramFloat)
  {
    if (Float.isNaN(this.padding_right))
      setPadding_right(paramFloat);
    if (Float.isNaN(this.padding_left))
      setPadding_left(paramFloat);
    if (Float.isNaN(this.padding_top))
      setPadding_top(paramFloat);
    if (Float.isNaN(this.padding_bottom))
      setPadding_bottom(paramFloat);
  }

  public int getColspan()
  {
    return this.colspan;
  }

  public void setColspan(int paramInt)
  {
    if (paramInt > 0)
      this.colspan = paramInt;
  }

  public float getPadding_bottom()
  {
    return this.padding_bottom;
  }

  public void setPadding_bottom(float paramFloat)
  {
    this.padding_bottom = paramFloat;
  }

  public float getPadding_left()
  {
    return this.padding_left;
  }

  public void setPadding_left(float paramFloat)
  {
    this.padding_left = paramFloat;
  }

  public float getPadding_right()
  {
    return this.padding_right;
  }

  public void setPadding_right(float paramFloat)
  {
    this.padding_right = paramFloat;
  }

  public float getPadding_top()
  {
    return this.padding_top;
  }

  public void setPadding_top(float paramFloat)
  {
    this.padding_top = paramFloat;
  }

  public float getSpacing_left()
  {
    return this.spacing_left;
  }

  public float getSpacing_right()
  {
    return this.spacing_right;
  }

  public float getSpacing_top()
  {
    return this.spacing_top;
  }

  public float getSpacing_bottom()
  {
    return this.spacing_bottom;
  }

  public void setSpacing(float paramFloat)
  {
    this.spacing_left = paramFloat;
    this.spacing_right = paramFloat;
    this.spacing_top = paramFloat;
    this.spacing_bottom = paramFloat;
  }

  public void setSpacing_left(float paramFloat)
  {
    this.spacing_left = paramFloat;
  }

  public void setSpacing_right(float paramFloat)
  {
    this.spacing_right = paramFloat;
  }

  public void setSpacing_top(float paramFloat)
  {
    this.spacing_top = paramFloat;
  }

  public void setSpacing_bottom(float paramFloat)
  {
    this.spacing_bottom = paramFloat;
  }

  public boolean isCellgroup()
  {
    return this.cellgroup;
  }

  public void setCellgroup(boolean paramBoolean)
  {
    this.cellgroup = paramBoolean;
  }

  public int getHorizontalAlignment()
  {
    return this.horizontalAlignment;
  }

  public void setHorizontalAlignment(int paramInt)
  {
    this.horizontalAlignment = paramInt;
  }

  public int getVerticalAlignment()
  {
    return this.verticalAlignment;
  }

  public void setVerticalAlignment(int paramInt)
  {
    this.verticalAlignment = paramInt;
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

  public boolean isUseAscender()
  {
    return this.useAscender;
  }

  public void setUseAscender(boolean paramBoolean)
  {
    this.useAscender = paramBoolean;
  }

  public boolean isUseBorderPadding()
  {
    return this.useBorderPadding;
  }

  public void setUseBorderPadding(boolean paramBoolean)
  {
    this.useBorderPadding = paramBoolean;
  }

  public boolean isUseDescender()
  {
    return this.useDescender;
  }

  public void setUseDescender(boolean paramBoolean)
  {
    this.useDescender = paramBoolean;
  }

  ArrayList getContent()
  {
    return this.content;
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
      return false;
    }
    catch (BadElementException localBadElementException)
    {
    }
    throw new ExceptionConverter(localBadElementException);
  }

  public int type()
  {
    return 20;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.SimpleCell
 * JD-Core Version:    0.6.0
 */