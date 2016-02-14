package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.events.PdfPCellEventForwarder;
import java.util.List;

public class PdfPCell extends Rectangle
{
  private ColumnText column = new ColumnText(null);
  private int verticalAlignment = 4;
  private float paddingLeft = 2.0F;
  private float paddingRight = 2.0F;
  private float paddingTop = 2.0F;
  private float paddingBottom = 2.0F;
  private float fixedHeight = 0.0F;
  private float minimumHeight;
  private boolean noWrap = false;
  private PdfPTable table;
  private int colspan = 1;
  private int rowspan = 1;
  private Image image;
  private PdfPCellEvent cellEvent;
  private boolean useDescender;
  private boolean useBorderPadding = false;
  protected Phrase phrase;
  private int rotation;

  public PdfPCell()
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    this.column.setLeading(0.0F, 1.0F);
  }

  public PdfPCell(Phrase paramPhrase)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    this.column.addText(this.phrase = paramPhrase);
    this.column.setLeading(0.0F, 1.0F);
  }

  public PdfPCell(Image paramImage)
  {
    this(paramImage, false);
  }

  public PdfPCell(Image paramImage, boolean paramBoolean)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    if (paramBoolean)
    {
      this.image = paramImage;
      this.column.setLeading(0.0F, 1.0F);
      setPadding(this.borderWidth / 2.0F);
    }
    else
    {
      this.column.addText(this.phrase = new Phrase(new Chunk(paramImage, 0.0F, 0.0F)));
      this.column.setLeading(0.0F, 1.0F);
      setPadding(0.0F);
    }
  }

  public PdfPCell(PdfPTable paramPdfPTable)
  {
    this(paramPdfPTable, null);
  }

  public PdfPCell(PdfPTable paramPdfPTable, PdfPCell paramPdfPCell)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    this.column.setLeading(0.0F, 1.0F);
    this.table = paramPdfPTable;
    paramPdfPTable.setWidthPercentage(100.0F);
    paramPdfPTable.setExtendLastRow(true);
    this.column.addElement(paramPdfPTable);
    if (paramPdfPCell != null)
    {
      cloneNonPositionParameters(paramPdfPCell);
      this.verticalAlignment = paramPdfPCell.verticalAlignment;
      this.paddingLeft = paramPdfPCell.paddingLeft;
      this.paddingRight = paramPdfPCell.paddingRight;
      this.paddingTop = paramPdfPCell.paddingTop;
      this.paddingBottom = paramPdfPCell.paddingBottom;
      this.colspan = paramPdfPCell.colspan;
      this.rowspan = paramPdfPCell.rowspan;
      this.cellEvent = paramPdfPCell.cellEvent;
      this.useDescender = paramPdfPCell.useDescender;
      this.useBorderPadding = paramPdfPCell.useBorderPadding;
      this.rotation = paramPdfPCell.rotation;
    }
    else
    {
      setPadding(0.0F);
    }
  }

  public PdfPCell(PdfPCell paramPdfPCell)
  {
    super(paramPdfPCell.llx, paramPdfPCell.lly, paramPdfPCell.urx, paramPdfPCell.ury);
    cloneNonPositionParameters(paramPdfPCell);
    this.verticalAlignment = paramPdfPCell.verticalAlignment;
    this.paddingLeft = paramPdfPCell.paddingLeft;
    this.paddingRight = paramPdfPCell.paddingRight;
    this.paddingTop = paramPdfPCell.paddingTop;
    this.paddingBottom = paramPdfPCell.paddingBottom;
    this.phrase = paramPdfPCell.phrase;
    this.fixedHeight = paramPdfPCell.fixedHeight;
    this.minimumHeight = paramPdfPCell.minimumHeight;
    this.noWrap = paramPdfPCell.noWrap;
    this.colspan = paramPdfPCell.colspan;
    this.rowspan = paramPdfPCell.rowspan;
    if (paramPdfPCell.table != null)
      this.table = new PdfPTable(paramPdfPCell.table);
    this.image = Image.getInstance(paramPdfPCell.image);
    this.cellEvent = paramPdfPCell.cellEvent;
    this.useDescender = paramPdfPCell.useDescender;
    this.column = ColumnText.duplicate(paramPdfPCell.column);
    this.useBorderPadding = paramPdfPCell.useBorderPadding;
    this.rotation = paramPdfPCell.rotation;
  }

  public void addElement(Element paramElement)
  {
    if (this.table != null)
    {
      this.table = null;
      this.column.setText(null);
    }
    this.column.addElement(paramElement);
  }

  public Phrase getPhrase()
  {
    return this.phrase;
  }

  public void setPhrase(Phrase paramPhrase)
  {
    this.table = null;
    this.image = null;
    this.column.setText(this.phrase = paramPhrase);
  }

  public int getHorizontalAlignment()
  {
    return this.column.getAlignment();
  }

  public void setHorizontalAlignment(int paramInt)
  {
    this.column.setAlignment(paramInt);
  }

  public int getVerticalAlignment()
  {
    return this.verticalAlignment;
  }

  public void setVerticalAlignment(int paramInt)
  {
    if (this.table != null)
      this.table.setExtendLastRow(paramInt == 4);
    this.verticalAlignment = paramInt;
  }

  public float getEffectivePaddingLeft()
  {
    if (isUseBorderPadding())
    {
      float f = getBorderWidthLeft() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingLeft + f;
    }
    return this.paddingLeft;
  }

  public float getPaddingLeft()
  {
    return this.paddingLeft;
  }

  public void setPaddingLeft(float paramFloat)
  {
    this.paddingLeft = paramFloat;
  }

  public float getEffectivePaddingRight()
  {
    if (isUseBorderPadding())
    {
      float f = getBorderWidthRight() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingRight + f;
    }
    return this.paddingRight;
  }

  public float getPaddingRight()
  {
    return this.paddingRight;
  }

  public void setPaddingRight(float paramFloat)
  {
    this.paddingRight = paramFloat;
  }

  public float getEffectivePaddingTop()
  {
    if (isUseBorderPadding())
    {
      float f = getBorderWidthTop() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingTop + f;
    }
    return this.paddingTop;
  }

  public float getPaddingTop()
  {
    return this.paddingTop;
  }

  public void setPaddingTop(float paramFloat)
  {
    this.paddingTop = paramFloat;
  }

  public float getEffectivePaddingBottom()
  {
    if (isUseBorderPadding())
    {
      float f = getBorderWidthBottom() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingBottom + f;
    }
    return this.paddingBottom;
  }

  public float getPaddingBottom()
  {
    return this.paddingBottom;
  }

  public void setPaddingBottom(float paramFloat)
  {
    this.paddingBottom = paramFloat;
  }

  public void setPadding(float paramFloat)
  {
    this.paddingBottom = paramFloat;
    this.paddingTop = paramFloat;
    this.paddingLeft = paramFloat;
    this.paddingRight = paramFloat;
  }

  public boolean isUseBorderPadding()
  {
    return this.useBorderPadding;
  }

  public void setUseBorderPadding(boolean paramBoolean)
  {
    this.useBorderPadding = paramBoolean;
  }

  public void setLeading(float paramFloat1, float paramFloat2)
  {
    this.column.setLeading(paramFloat1, paramFloat2);
  }

  public float getLeading()
  {
    return this.column.getLeading();
  }

  public float getMultipliedLeading()
  {
    return this.column.getMultipliedLeading();
  }

  public void setIndent(float paramFloat)
  {
    this.column.setIndent(paramFloat);
  }

  public float getIndent()
  {
    return this.column.getIndent();
  }

  public float getExtraParagraphSpace()
  {
    return this.column.getExtraParagraphSpace();
  }

  public void setExtraParagraphSpace(float paramFloat)
  {
    this.column.setExtraParagraphSpace(paramFloat);
  }

  public void setFixedHeight(float paramFloat)
  {
    this.fixedHeight = paramFloat;
    this.minimumHeight = 0.0F;
  }

  public float getFixedHeight()
  {
    return this.fixedHeight;
  }

  public boolean hasFixedHeight()
  {
    return getFixedHeight() > 0.0F;
  }

  public void setMinimumHeight(float paramFloat)
  {
    this.minimumHeight = paramFloat;
    this.fixedHeight = 0.0F;
  }

  public float getMinimumHeight()
  {
    return this.minimumHeight;
  }

  public boolean hasMinimumHeight()
  {
    return getMinimumHeight() > 0.0F;
  }

  public boolean isNoWrap()
  {
    return this.noWrap;
  }

  public void setNoWrap(boolean paramBoolean)
  {
    this.noWrap = paramBoolean;
  }

  public PdfPTable getTable()
  {
    return this.table;
  }

  void setTable(PdfPTable paramPdfPTable)
  {
    this.table = paramPdfPTable;
    this.column.setText(null);
    this.image = null;
    if (paramPdfPTable != null)
    {
      paramPdfPTable.setExtendLastRow(this.verticalAlignment == 4);
      this.column.addElement(paramPdfPTable);
      paramPdfPTable.setWidthPercentage(100.0F);
    }
  }

  public int getColspan()
  {
    return this.colspan;
  }

  public void setColspan(int paramInt)
  {
    this.colspan = paramInt;
  }

  public int getRowspan()
  {
    return this.rowspan;
  }

  public void setRowspan(int paramInt)
  {
    this.rowspan = paramInt;
  }

  public void setFollowingIndent(float paramFloat)
  {
    this.column.setFollowingIndent(paramFloat);
  }

  public float getFollowingIndent()
  {
    return this.column.getFollowingIndent();
  }

  public void setRightIndent(float paramFloat)
  {
    this.column.setRightIndent(paramFloat);
  }

  public float getRightIndent()
  {
    return this.column.getRightIndent();
  }

  public float getSpaceCharRatio()
  {
    return this.column.getSpaceCharRatio();
  }

  public void setSpaceCharRatio(float paramFloat)
  {
    this.column.setSpaceCharRatio(paramFloat);
  }

  public void setRunDirection(int paramInt)
  {
    this.column.setRunDirection(paramInt);
  }

  public int getRunDirection()
  {
    return this.column.getRunDirection();
  }

  public Image getImage()
  {
    return this.image;
  }

  public void setImage(Image paramImage)
  {
    this.column.setText(null);
    this.table = null;
    this.image = paramImage;
  }

  public PdfPCellEvent getCellEvent()
  {
    return this.cellEvent;
  }

  public void setCellEvent(PdfPCellEvent paramPdfPCellEvent)
  {
    if (paramPdfPCellEvent == null)
    {
      this.cellEvent = null;
    }
    else if (this.cellEvent == null)
    {
      this.cellEvent = paramPdfPCellEvent;
    }
    else if ((this.cellEvent instanceof PdfPCellEventForwarder))
    {
      ((PdfPCellEventForwarder)this.cellEvent).addCellEvent(paramPdfPCellEvent);
    }
    else
    {
      PdfPCellEventForwarder localPdfPCellEventForwarder = new PdfPCellEventForwarder();
      localPdfPCellEventForwarder.addCellEvent(this.cellEvent);
      localPdfPCellEventForwarder.addCellEvent(paramPdfPCellEvent);
      this.cellEvent = localPdfPCellEventForwarder;
    }
  }

  public int getArabicOptions()
  {
    return this.column.getArabicOptions();
  }

  public void setArabicOptions(int paramInt)
  {
    this.column.setArabicOptions(paramInt);
  }

  public boolean isUseAscender()
  {
    return this.column.isUseAscender();
  }

  public void setUseAscender(boolean paramBoolean)
  {
    this.column.setUseAscender(paramBoolean);
  }

  public boolean isUseDescender()
  {
    return this.useDescender;
  }

  public void setUseDescender(boolean paramBoolean)
  {
    this.useDescender = paramBoolean;
  }

  public ColumnText getColumn()
  {
    return this.column;
  }

  public List getCompositeElements()
  {
    return getColumn().compositeElements;
  }

  public void setColumn(ColumnText paramColumnText)
  {
    this.column = paramColumnText;
  }

  public int getRotation()
  {
    return this.rotation;
  }

  public void setRotation(int paramInt)
  {
    paramInt %= 360;
    if (paramInt < 0)
      paramInt += 360;
    if (paramInt % 90 != 0)
      throw new IllegalArgumentException("Rotation must be a multiple of 90.");
    this.rotation = paramInt;
  }

  void consumeHeight(float paramFloat)
  {
    float f1 = getRight() - getEffectivePaddingRight();
    float f2 = getLeft() + getEffectivePaddingLeft();
    float f3 = paramFloat - getEffectivePaddingTop() - getEffectivePaddingBottom();
    if ((getRotation() != 90) && (getRotation() != 270))
      this.column.setSimpleColumn(f2, f3 + 0.001F, f1, 0.0F);
    else
      this.column.setSimpleColumn(0.0F, f2, f3 + 0.001F, f1);
    try
    {
      this.column.go(true);
    }
    catch (DocumentException localDocumentException)
    {
    }
  }

  public float getMaxHeight()
  {
    int i = (getRotation() == 90) || (getRotation() == 270) ? 1 : 0;
    Image localImage = getImage();
    float f3;
    float f4;
    if (localImage != null)
    {
      localImage.scalePercent(100.0F);
      float f1 = i != 0 ? localImage.getScaledHeight() : localImage.getScaledWidth();
      f3 = (getRight() - getEffectivePaddingRight() - getEffectivePaddingLeft() - getLeft()) / f1;
      localImage.scalePercent(f3 * 100.0F);
      f4 = i != 0 ? localImage.getScaledWidth() : localImage.getScaledHeight();
      setBottom(getTop() - getEffectivePaddingTop() - getEffectivePaddingBottom() - f4);
    }
    else if ((i != 0) && (hasFixedHeight()))
    {
      setBottom(getTop() - getFixedHeight());
    }
    else
    {
      ColumnText localColumnText = ColumnText.duplicate(getColumn());
      float f5;
      float f6;
      if (i != 0)
      {
        f3 = 20000.0F;
        f4 = getRight() - getEffectivePaddingRight();
        f5 = 0.0F;
        f6 = getLeft() + getEffectivePaddingLeft();
      }
      else
      {
        f3 = isNoWrap() ? 20000.0F : getRight() - getEffectivePaddingRight();
        f4 = getTop() - getEffectivePaddingTop();
        f5 = getLeft() + getEffectivePaddingLeft();
        f6 = hasFixedHeight() ? f4 + getEffectivePaddingBottom() - getFixedHeight() : -1.073742E+009F;
      }
      PdfPRow.setColumn(localColumnText, f5, f6, f3, f4);
      try
      {
        localColumnText.go(true);
      }
      catch (DocumentException localDocumentException)
      {
        throw new ExceptionConverter(localDocumentException);
      }
      if (i != 0)
      {
        setBottom(getTop() - getEffectivePaddingTop() - getEffectivePaddingBottom() - localColumnText.getFilledWidth());
      }
      else
      {
        float f7 = localColumnText.getYLine();
        if (isUseDescender())
          f7 += localColumnText.getDescender();
        setBottom(f7 - getEffectivePaddingBottom());
      }
    }
    float f2 = getHeight();
    if (f2 < getFixedHeight())
      f2 = getFixedHeight();
    else if (f2 < getMinimumHeight())
      f2 = getMinimumHeight();
    return f2;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPCell
 * JD-Core Version:    0.6.0
 */