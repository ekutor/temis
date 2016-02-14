package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.awt.Color;

public class PdfPRow
{
  public static final float BOTTOM_LIMIT = -1.073742E+009F;
  public static final float RIGHT_LIMIT = 20000.0F;
  protected PdfPCell[] cells;
  protected float[] widths;
  protected float[] extraHeights;
  protected float maxHeight = 0.0F;
  protected boolean calculated = false;
  private int[] canvasesPos;

  public PdfPRow(PdfPCell[] paramArrayOfPdfPCell)
  {
    this.cells = paramArrayOfPdfPCell;
    this.widths = new float[paramArrayOfPdfPCell.length];
    initExtraHeights();
  }

  public PdfPRow(PdfPRow paramPdfPRow)
  {
    this.maxHeight = paramPdfPRow.maxHeight;
    this.calculated = paramPdfPRow.calculated;
    this.cells = new PdfPCell[paramPdfPRow.cells.length];
    for (int i = 0; i < this.cells.length; i++)
    {
      if (paramPdfPRow.cells[i] == null)
        continue;
      this.cells[i] = new PdfPCell(paramPdfPRow.cells[i]);
    }
    this.widths = new float[this.cells.length];
    System.arraycopy(paramPdfPRow.widths, 0, this.widths, 0, this.cells.length);
    initExtraHeights();
  }

  public boolean setWidths(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length != this.cells.length)
      return false;
    System.arraycopy(paramArrayOfFloat, 0, this.widths, 0, this.cells.length);
    float f = 0.0F;
    this.calculated = false;
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      PdfPCell localPdfPCell = this.cells[i];
      if (localPdfPCell == null)
      {
        f += paramArrayOfFloat[i];
      }
      else
      {
        localPdfPCell.setLeft(f);
        int j = i + localPdfPCell.getColspan();
        while (i < j)
        {
          f += paramArrayOfFloat[i];
          i++;
        }
        i--;
        localPdfPCell.setRight(f);
        localPdfPCell.setTop(0.0F);
      }
    }
    return true;
  }

  public void initExtraHeights()
  {
    this.extraHeights = new float[this.cells.length];
    for (int i = 0; i < this.extraHeights.length; i++)
      this.extraHeights[i] = 0.0F;
  }

  public void setExtraHeight(int paramInt, float paramFloat)
  {
    if ((paramInt < 0) || (paramInt >= this.cells.length))
      return;
    this.extraHeights[paramInt] = paramFloat;
  }

  public float calculateHeights()
  {
    this.maxHeight = 0.0F;
    for (int i = 0; i < this.cells.length; i++)
    {
      PdfPCell localPdfPCell = this.cells[i];
      float f = 0.0F;
      if (localPdfPCell == null)
        continue;
      f = localPdfPCell.getMaxHeight();
      if ((f <= this.maxHeight) || (localPdfPCell.getRowspan() != 1))
        continue;
      this.maxHeight = f;
    }
    this.calculated = true;
    return this.maxHeight;
  }

  public void writeBorderAndBackground(float paramFloat1, float paramFloat2, float paramFloat3, PdfPCell paramPdfPCell, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    Color localColor = paramPdfPCell.getBackgroundColor();
    if ((localColor != null) || (paramPdfPCell.hasBorders()))
    {
      float f1 = paramPdfPCell.getRight() + paramFloat1;
      float f2 = paramPdfPCell.getTop() + paramFloat2;
      float f3 = paramPdfPCell.getLeft() + paramFloat1;
      float f4 = f2 - paramFloat3;
      Object localObject;
      if (localColor != null)
      {
        localObject = paramArrayOfPdfContentByte[1];
        ((PdfContentByte)localObject).setColorFill(localColor);
        ((PdfContentByte)localObject).rectangle(f3, f4, f1 - f3, f2 - f4);
        ((PdfContentByte)localObject).fill();
      }
      if (paramPdfPCell.hasBorders())
      {
        localObject = new Rectangle(f3, f4, f1, f2);
        ((Rectangle)localObject).cloneNonPositionParameters(paramPdfPCell);
        ((Rectangle)localObject).setBackgroundColor(null);
        PdfContentByte localPdfContentByte = paramArrayOfPdfContentByte[2];
        localPdfContentByte.rectangle((Rectangle)localObject);
      }
    }
  }

  protected void saveAndRotateCanvases(PdfContentByte[] paramArrayOfPdfContentByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    int i = 4;
    if (this.canvasesPos == null)
      this.canvasesPos = new int[i * 2];
    for (int j = 0; j < i; j++)
    {
      ByteBuffer localByteBuffer = paramArrayOfPdfContentByte[j].getInternalBuffer();
      this.canvasesPos[(j * 2)] = localByteBuffer.size();
      paramArrayOfPdfContentByte[j].saveState();
      paramArrayOfPdfContentByte[j].concatCTM(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
      this.canvasesPos[(j * 2 + 1)] = localByteBuffer.size();
    }
  }

  protected void restoreCanvases(PdfContentByte[] paramArrayOfPdfContentByte)
  {
    int i = 4;
    for (int j = 0; j < i; j++)
    {
      ByteBuffer localByteBuffer = paramArrayOfPdfContentByte[j].getInternalBuffer();
      int k = localByteBuffer.size();
      paramArrayOfPdfContentByte[j].restoreState();
      if (k != this.canvasesPos[(j * 2 + 1)])
        continue;
      localByteBuffer.setSize(this.canvasesPos[(j * 2)]);
    }
  }

  public static float setColumn(ColumnText paramColumnText, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 > paramFloat3)
      paramFloat3 = paramFloat1;
    if (paramFloat2 > paramFloat4)
      paramFloat4 = paramFloat2;
    paramColumnText.setSimpleColumn(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    return paramFloat4;
  }

  public void writeCells(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    if (!this.calculated)
      calculateHeights();
    if (paramInt2 < 0)
      paramInt2 = this.cells.length;
    else
      paramInt2 = Math.min(paramInt2, this.cells.length);
    if (paramInt1 < 0)
      paramInt1 = 0;
    if (paramInt1 >= paramInt2)
      return;
    for (int i = paramInt1; (i >= 0) && (this.cells[i] == null); i--)
    {
      if (i <= 0)
        continue;
      paramFloat1 -= this.widths[(i - 1)];
    }
    if (i < 0)
      i = 0;
    if (this.cells[i] != null)
      paramFloat1 -= this.cells[i].getLeft();
    for (int j = i; j < paramInt2; j++)
    {
      PdfPCell localPdfPCell = this.cells[j];
      if (localPdfPCell == null)
        continue;
      float f1 = this.maxHeight + this.extraHeights[j];
      writeBorderAndBackground(paramFloat1, paramFloat2, f1, localPdfPCell, paramArrayOfPdfContentByte);
      Image localImage = localPdfPCell.getImage();
      float f2 = localPdfPCell.getTop() + paramFloat2 - localPdfPCell.getEffectivePaddingTop();
      if (localPdfPCell.getHeight() <= f1)
        switch (localPdfPCell.getVerticalAlignment())
        {
        case 6:
          f2 = localPdfPCell.getTop() + paramFloat2 - f1 + localPdfPCell.getHeight() - localPdfPCell.getEffectivePaddingTop();
          break;
        case 5:
          f2 = localPdfPCell.getTop() + paramFloat2 + (localPdfPCell.getHeight() - f1) / 2.0F - localPdfPCell.getEffectivePaddingTop();
          break;
        }
      float f4;
      if (localImage != null)
      {
        if (localPdfPCell.getRotation() != 0)
        {
          localImage = Image.getInstance(localImage);
          localImage.setRotation(localImage.getImageRotation() + (float)(localPdfPCell.getRotation() * 3.141592653589793D / 180.0D));
        }
        int k = 0;
        if (localPdfPCell.getHeight() > f1)
        {
          localImage.scalePercent(100.0F);
          f4 = (f1 - localPdfPCell.getEffectivePaddingTop() - localPdfPCell.getEffectivePaddingBottom()) / localImage.getScaledHeight();
          localImage.scalePercent(f4 * 100.0F);
          k = 1;
        }
        f4 = localPdfPCell.getLeft() + paramFloat1 + localPdfPCell.getEffectivePaddingLeft();
        if (k != 0)
        {
          switch (localPdfPCell.getHorizontalAlignment())
          {
          case 1:
            f4 = paramFloat1 + (localPdfPCell.getLeft() + localPdfPCell.getEffectivePaddingLeft() + localPdfPCell.getRight() - localPdfPCell.getEffectivePaddingRight() - localImage.getScaledWidth()) / 2.0F;
            break;
          case 2:
            f4 = paramFloat1 + localPdfPCell.getRight() - localPdfPCell.getEffectivePaddingRight() - localImage.getScaledWidth();
            break;
          }
          f2 = localPdfPCell.getTop() + paramFloat2 - localPdfPCell.getEffectivePaddingTop();
        }
        localImage.setAbsolutePosition(f4, f2 - localImage.getScaledHeight());
        try
        {
          paramArrayOfPdfContentByte[3].addImage(localImage);
        }
        catch (DocumentException localDocumentException1)
        {
          throw new ExceptionConverter(localDocumentException1);
        }
      }
      else
      {
        float f3;
        float f8;
        float f7;
        if ((localPdfPCell.getRotation() == 90) || (localPdfPCell.getRotation() == 270))
        {
          f3 = f1 - localPdfPCell.getEffectivePaddingTop() - localPdfPCell.getEffectivePaddingBottom();
          f4 = localPdfPCell.getWidth() - localPdfPCell.getEffectivePaddingLeft() - localPdfPCell.getEffectivePaddingRight();
          ColumnText localColumnText1 = ColumnText.duplicate(localPdfPCell.getColumn());
          localColumnText1.setCanvases(paramArrayOfPdfContentByte);
          localColumnText1.setSimpleColumn(0.0F, 0.0F, f3 + 0.001F, -f4);
          try
          {
            localColumnText1.go(true);
          }
          catch (DocumentException localDocumentException2)
          {
            throw new ExceptionConverter(localDocumentException2);
          }
          float f6 = -localColumnText1.getYLine();
          if ((f3 <= 0.0F) || (f4 <= 0.0F))
            f6 = 0.0F;
          if (f6 > 0.0F)
          {
            if (localPdfPCell.isUseDescender())
              f6 -= localColumnText1.getDescender();
            localColumnText1 = ColumnText.duplicate(localPdfPCell.getColumn());
            localColumnText1.setCanvases(paramArrayOfPdfContentByte);
            localColumnText1.setSimpleColumn(-0.003F, -0.001F, f3 + 0.003F, f6);
            if (localPdfPCell.getRotation() == 90)
            {
              f8 = localPdfPCell.getTop() + paramFloat2 - f1 + localPdfPCell.getEffectivePaddingBottom();
              switch (localPdfPCell.getVerticalAlignment())
              {
              case 6:
                f7 = localPdfPCell.getLeft() + paramFloat1 + localPdfPCell.getWidth() - localPdfPCell.getEffectivePaddingRight();
                break;
              case 5:
                f7 = localPdfPCell.getLeft() + paramFloat1 + (localPdfPCell.getWidth() + localPdfPCell.getEffectivePaddingLeft() - localPdfPCell.getEffectivePaddingRight() + f6) / 2.0F;
                break;
              default:
                f7 = localPdfPCell.getLeft() + paramFloat1 + localPdfPCell.getEffectivePaddingLeft() + f6;
              }
              saveAndRotateCanvases(paramArrayOfPdfContentByte, 0.0F, 1.0F, -1.0F, 0.0F, f7, f8);
            }
            else
            {
              f8 = localPdfPCell.getTop() + paramFloat2 - localPdfPCell.getEffectivePaddingTop();
              switch (localPdfPCell.getVerticalAlignment())
              {
              case 6:
                f7 = localPdfPCell.getLeft() + paramFloat1 + localPdfPCell.getEffectivePaddingLeft();
                break;
              case 5:
                f7 = localPdfPCell.getLeft() + paramFloat1 + (localPdfPCell.getWidth() + localPdfPCell.getEffectivePaddingLeft() - localPdfPCell.getEffectivePaddingRight() - f6) / 2.0F;
                break;
              default:
                f7 = localPdfPCell.getLeft() + paramFloat1 + localPdfPCell.getWidth() - localPdfPCell.getEffectivePaddingRight() - f6;
              }
              saveAndRotateCanvases(paramArrayOfPdfContentByte, 0.0F, -1.0F, 1.0F, 0.0F, f7, f8);
            }
            try
            {
              localColumnText1.go();
            }
            catch (DocumentException localDocumentException4)
            {
              throw new ExceptionConverter(localDocumentException4);
            }
            finally
            {
              restoreCanvases(paramArrayOfPdfContentByte);
            }
          }
        }
        else
        {
          f3 = localPdfPCell.getFixedHeight();
          f4 = localPdfPCell.getRight() + paramFloat1 - localPdfPCell.getEffectivePaddingRight();
          float f5 = localPdfPCell.getLeft() + paramFloat1 + localPdfPCell.getEffectivePaddingLeft();
          if (localPdfPCell.isNoWrap())
            switch (localPdfPCell.getHorizontalAlignment())
            {
            case 1:
              f4 += 10000.0F;
              f5 -= 10000.0F;
              break;
            case 2:
              if (localPdfPCell.getRotation() == 180)
                f4 += 20000.0F;
              else
                f5 -= 20000.0F;
              break;
            default:
              if (localPdfPCell.getRotation() == 180)
                f5 -= 20000.0F;
              else
                f4 += 20000.0F;
            }
          ColumnText localColumnText2 = ColumnText.duplicate(localPdfPCell.getColumn());
          localColumnText2.setCanvases(paramArrayOfPdfContentByte);
          f7 = f2 - (f1 - localPdfPCell.getEffectivePaddingTop() - localPdfPCell.getEffectivePaddingBottom());
          if ((f3 > 0.0F) && (localPdfPCell.getHeight() > f1))
          {
            f2 = localPdfPCell.getTop() + paramFloat2 - localPdfPCell.getEffectivePaddingTop();
            f7 = localPdfPCell.getTop() + paramFloat2 - f1 + localPdfPCell.getEffectivePaddingBottom();
          }
          if (((f2 > f7) || (localColumnText2.zeroHeightElement())) && (f5 < f4))
          {
            localColumnText2.setSimpleColumn(f5, f7 - 0.001F, f4, f2);
            if (localPdfPCell.getRotation() == 180)
            {
              f8 = f5 + f4;
              float f9 = paramFloat2 + paramFloat2 - f1 + localPdfPCell.getEffectivePaddingBottom() - localPdfPCell.getEffectivePaddingTop();
              saveAndRotateCanvases(paramArrayOfPdfContentByte, -1.0F, 0.0F, 0.0F, -1.0F, f8, f9);
            }
            try
            {
              localColumnText2.go();
              if (localPdfPCell.getRotation() == 180)
                restoreCanvases(paramArrayOfPdfContentByte);
            }
            catch (DocumentException localDocumentException3)
            {
              throw new ExceptionConverter(localDocumentException3);
            }
            finally
            {
              if (localPdfPCell.getRotation() == 180)
                restoreCanvases(paramArrayOfPdfContentByte);
            }
          }
        }
      }
      PdfPCellEvent localPdfPCellEvent = localPdfPCell.getCellEvent();
      if (localPdfPCellEvent == null)
        continue;
      Rectangle localRectangle = new Rectangle(localPdfPCell.getLeft() + paramFloat1, localPdfPCell.getTop() + paramFloat2 - f1, localPdfPCell.getRight() + paramFloat1, localPdfPCell.getTop() + paramFloat2);
      localPdfPCellEvent.cellLayout(localPdfPCell, localRectangle, paramArrayOfPdfContentByte);
    }
  }

  public boolean isCalculated()
  {
    return this.calculated;
  }

  public float getMaxHeights()
  {
    if (this.calculated)
      return this.maxHeight;
    return calculateHeights();
  }

  public void setMaxHeights(float paramFloat)
  {
    this.maxHeight = paramFloat;
  }

  float[] getEventWidth(float paramFloat)
  {
    int i = 0;
    for (int j = 0; j < this.cells.length; j++)
    {
      if (this.cells[j] == null)
        continue;
      i++;
    }
    float[] arrayOfFloat = new float[i + 1];
    i = 0;
    arrayOfFloat[(i++)] = paramFloat;
    for (int k = 0; k < this.cells.length; k++)
    {
      if (this.cells[k] == null)
        continue;
      arrayOfFloat[i] = (arrayOfFloat[(i - 1)] + this.cells[k].getWidth());
      i++;
    }
    return arrayOfFloat;
  }

  public PdfPRow splitRow(PdfPTable paramPdfPTable, int paramInt, float paramFloat)
  {
    PdfPCell[] arrayOfPdfPCell = new PdfPCell[this.cells.length];
    float[] arrayOfFloat1 = new float[this.cells.length];
    float[] arrayOfFloat2 = new float[this.cells.length];
    int i = 1;
    for (int j = 0; j < this.cells.length; j++)
    {
      float f1 = paramFloat;
      PdfPCell localPdfPCell2 = this.cells[j];
      Object localObject;
      if (localPdfPCell2 == null)
      {
        int k = paramInt;
        if (!paramPdfPTable.rowSpanAbove(k, j))
          continue;
        f1 += paramPdfPTable.getRowHeight(k);
        while (true)
        {
          k--;
          if (!paramPdfPTable.rowSpanAbove(k, j))
            break;
          f1 += paramPdfPTable.getRowHeight(k);
        }
        localObject = paramPdfPTable.getRow(k);
        if ((localObject == null) || (localObject.getCells()[j] == null))
          continue;
        arrayOfPdfPCell[j] = new PdfPCell(localObject.getCells()[j]);
        arrayOfPdfPCell[j].consumeHeight(f1);
        arrayOfPdfPCell[j].setRowspan(localObject.getCells()[j].getRowspan() - paramInt + k);
        i = 0;
      }
      else
      {
        arrayOfFloat1[j] = localPdfPCell2.getFixedHeight();
        arrayOfFloat2[j] = localPdfPCell2.getMinimumHeight();
        Image localImage = localPdfPCell2.getImage();
        localObject = new PdfPCell(localPdfPCell2);
        if (localImage != null)
        {
          if (f1 > localPdfPCell2.getEffectivePaddingBottom() + localPdfPCell2.getEffectivePaddingTop() + 2.0F)
          {
            ((PdfPCell)localObject).setPhrase(null);
            i = 0;
          }
        }
        else
        {
          ColumnText localColumnText = ColumnText.duplicate(localPdfPCell2.getColumn());
          float f3 = localPdfPCell2.getLeft() + localPdfPCell2.getEffectivePaddingLeft();
          float f4 = localPdfPCell2.getTop() + localPdfPCell2.getEffectivePaddingBottom() - f1;
          float f5 = localPdfPCell2.getRight() - localPdfPCell2.getEffectivePaddingRight();
          float f6 = localPdfPCell2.getTop() - localPdfPCell2.getEffectivePaddingTop();
          float f2;
          switch (localPdfPCell2.getRotation())
          {
          case 90:
          case 270:
            f2 = setColumn(localColumnText, f4, f3, f6, f5);
            break;
          default:
            f2 = setColumn(localColumnText, f3, f4, localPdfPCell2.isNoWrap() ? 20000.0F : f5, f6);
          }
          int m;
          try
          {
            m = localColumnText.go(true);
          }
          catch (DocumentException localDocumentException)
          {
            throw new ExceptionConverter(localDocumentException);
          }
          int n = localColumnText.getYLine() == f2 ? 1 : 0;
          if (n != 0)
          {
            ((PdfPCell)localObject).setColumn(ColumnText.duplicate(localPdfPCell2.getColumn()));
            localColumnText.setFilledWidth(0.0F);
          }
          else if ((m & 0x1) == 0)
          {
            ((PdfPCell)localObject).setColumn(localColumnText);
            localColumnText.setFilledWidth(0.0F);
          }
          else
          {
            ((PdfPCell)localObject).setPhrase(null);
          }
          i = (i != 0) && (n != 0) ? 1 : 0;
        }
        arrayOfPdfPCell[j] = localObject;
        localPdfPCell2.setFixedHeight(f1);
      }
    }
    if (i != 0)
    {
      for (j = 0; j < this.cells.length; j++)
      {
        PdfPCell localPdfPCell1 = this.cells[j];
        if (localPdfPCell1 == null)
          continue;
        if (arrayOfFloat1[j] > 0.0F)
          localPdfPCell1.setFixedHeight(arrayOfFloat1[j]);
        else
          localPdfPCell1.setMinimumHeight(arrayOfFloat2[j]);
      }
      return null;
    }
    calculateHeights();
    PdfPRow localPdfPRow = new PdfPRow(arrayOfPdfPCell);
    localPdfPRow.widths = ((float[])this.widths.clone());
    localPdfPRow.calculateHeights();
    return (PdfPRow)localPdfPRow;
  }

  public PdfPCell[] getCells()
  {
    return this.cells;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPRow
 * JD-Core Version:    0.6.0
 */