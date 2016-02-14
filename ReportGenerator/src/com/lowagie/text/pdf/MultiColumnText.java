package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Phrase;
import java.util.ArrayList;

public class MultiColumnText
  implements Element
{
  public static final float AUTOMATIC = -1.0F;
  private float desiredHeight;
  private float totalHeight;
  private boolean overflow;
  private float top;
  private ColumnText columnText;
  private ArrayList columnDefs = new ArrayList();
  private boolean simple = true;
  private int currentColumn = 0;
  private float nextY = -1.0F;
  private boolean columnsRightToLeft = false;
  private PdfDocument document;

  public MultiColumnText()
  {
    this(-1.0F);
  }

  public MultiColumnText(float paramFloat)
  {
    this.desiredHeight = paramFloat;
    this.top = -1.0F;
    this.columnText = new ColumnText(null);
    this.totalHeight = 0.0F;
  }

  public MultiColumnText(float paramFloat1, float paramFloat2)
  {
    this.desiredHeight = paramFloat2;
    this.top = paramFloat1;
    this.nextY = paramFloat1;
    this.columnText = new ColumnText(null);
    this.totalHeight = 0.0F;
  }

  public boolean isOverflow()
  {
    return this.overflow;
  }

  public void useColumnParams(ColumnText paramColumnText)
  {
    this.columnText.setSimpleVars(paramColumnText);
  }

  public void addColumn(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    ColumnDef localColumnDef = new ColumnDef(paramArrayOfFloat1, paramArrayOfFloat2);
    if (!localColumnDef.isSimple())
      this.simple = false;
    this.columnDefs.add(localColumnDef);
  }

  public void addSimpleColumn(float paramFloat1, float paramFloat2)
  {
    ColumnDef localColumnDef = new ColumnDef(paramFloat1, paramFloat2);
    this.columnDefs.add(localColumnDef);
  }

  public void addRegularColumns(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    float f1 = paramFloat1;
    float f2 = paramFloat2 - paramFloat1;
    float f3 = (f2 - paramFloat3 * (paramInt - 1)) / paramInt;
    for (int i = 0; i < paramInt; i++)
    {
      addSimpleColumn(f1, f1 + f3);
      f1 += f3 + paramFloat3;
    }
  }

  public void addText(Phrase paramPhrase)
  {
    this.columnText.addText(paramPhrase);
  }

  public void addText(Chunk paramChunk)
  {
    this.columnText.addText(paramChunk);
  }

  public void addElement(Element paramElement)
    throws DocumentException
  {
    if (this.simple)
      this.columnText.addElement(paramElement);
    else if ((paramElement instanceof Phrase))
      this.columnText.addText((Phrase)paramElement);
    else if ((paramElement instanceof Chunk))
      this.columnText.addText((Chunk)paramElement);
    else
      throw new DocumentException("Can't add " + paramElement.getClass() + " to MultiColumnText with complex columns");
  }

  public float write(PdfContentByte paramPdfContentByte, PdfDocument paramPdfDocument, float paramFloat)
    throws DocumentException
  {
    this.document = paramPdfDocument;
    this.columnText.setCanvas(paramPdfContentByte);
    if (this.columnDefs.isEmpty())
      throw new DocumentException("MultiColumnText has no columns");
    this.overflow = false;
    float f1 = 0.0F;
    int i = 0;
    try
    {
      while (i == 0)
      {
        if (this.top == -1.0F)
          this.top = paramPdfDocument.getVerticalPosition(true);
        else if (this.nextY == -1.0F)
          this.nextY = paramPdfDocument.getVerticalPosition(true);
        ColumnDef localColumnDef = (ColumnDef)this.columnDefs.get(getCurrentColumn());
        this.columnText.setYLine(this.top);
        float[] arrayOfFloat1 = localColumnDef.resolvePositions(4);
        float[] arrayOfFloat2 = localColumnDef.resolvePositions(8);
        if ((paramPdfDocument.isMarginMirroring()) && (paramPdfDocument.getPageNumber() % 2 == 0))
        {
          float f2 = paramPdfDocument.rightMargin() - paramPdfDocument.left();
          arrayOfFloat1 = (float[])arrayOfFloat1.clone();
          arrayOfFloat2 = (float[])arrayOfFloat2.clone();
          for (int k = 0; k < arrayOfFloat1.length; k += 2)
            arrayOfFloat1[k] -= f2;
          for (k = 0; k < arrayOfFloat2.length; k += 2)
            arrayOfFloat2[k] -= f2;
        }
        f1 = Math.max(f1, getHeight(arrayOfFloat1, arrayOfFloat2));
        if (localColumnDef.isSimple())
          this.columnText.setSimpleColumn(arrayOfFloat1[2], arrayOfFloat1[3], arrayOfFloat2[0], arrayOfFloat2[1]);
        else
          this.columnText.setColumns(arrayOfFloat1, arrayOfFloat2);
        int j = this.columnText.go();
        if ((j & 0x1) != 0)
        {
          i = 1;
          this.top = this.columnText.getYLine();
          continue;
        }
        if (shiftCurrentColumn())
        {
          this.top = this.nextY;
          continue;
        }
        this.totalHeight += f1;
        if ((this.desiredHeight != -1.0F) && (this.totalHeight >= this.desiredHeight))
        {
          this.overflow = true;
          break;
        }
        paramFloat = this.nextY;
        newPage();
        f1 = 0.0F;
      }
    }
    catch (DocumentException localDocumentException)
    {
      localDocumentException.printStackTrace();
      throw localDocumentException;
    }
    if ((this.desiredHeight == -1.0F) && (this.columnDefs.size() == 1))
      f1 = paramFloat - this.columnText.getYLine();
    return f1;
  }

  private void newPage()
    throws DocumentException
  {
    resetCurrentColumn();
    if (this.desiredHeight == -1.0F)
      this.top = (this.nextY = -1.0F);
    else
      this.top = this.nextY;
    this.totalHeight = 0.0F;
    if (this.document != null)
      this.document.newPage();
  }

  private float getHeight(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float f1 = 1.4E-45F;
    float f2 = 3.4028235E+38F;
    for (int i = 0; i < paramArrayOfFloat1.length; i += 2)
    {
      f2 = Math.min(f2, paramArrayOfFloat1[(i + 1)]);
      f1 = Math.max(f1, paramArrayOfFloat1[(i + 1)]);
    }
    for (i = 0; i < paramArrayOfFloat2.length; i += 2)
    {
      f2 = Math.min(f2, paramArrayOfFloat2[(i + 1)]);
      f1 = Math.max(f1, paramArrayOfFloat2[(i + 1)]);
    }
    return f1 - f2;
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
    return 40;
  }

  public ArrayList getChunks()
  {
    return null;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return false;
  }

  private float getColumnBottom()
  {
    if (this.desiredHeight == -1.0F)
      return this.document.bottom();
    return Math.max(this.top - (this.desiredHeight - this.totalHeight), this.document.bottom());
  }

  public void nextColumn()
    throws DocumentException
  {
    this.currentColumn = ((this.currentColumn + 1) % this.columnDefs.size());
    this.top = this.nextY;
    if (this.currentColumn == 0)
      newPage();
  }

  public int getCurrentColumn()
  {
    if (this.columnsRightToLeft)
      return this.columnDefs.size() - this.currentColumn - 1;
    return this.currentColumn;
  }

  public void resetCurrentColumn()
  {
    this.currentColumn = 0;
  }

  public boolean shiftCurrentColumn()
  {
    if (this.currentColumn + 1 < this.columnDefs.size())
    {
      this.currentColumn += 1;
      return true;
    }
    return false;
  }

  public void setColumnsRightToLeft(boolean paramBoolean)
  {
    this.columnsRightToLeft = paramBoolean;
  }

  public void setSpaceCharRatio(float paramFloat)
  {
    this.columnText.setSpaceCharRatio(paramFloat);
  }

  public void setRunDirection(int paramInt)
  {
    this.columnText.setRunDirection(paramInt);
  }

  public void setArabicOptions(int paramInt)
  {
    this.columnText.setArabicOptions(paramInt);
  }

  public void setAlignment(int paramInt)
  {
    this.columnText.setAlignment(paramInt);
  }

  private class ColumnDef
  {
    private float[] left;
    private float[] right;

    ColumnDef(float[] paramArrayOfFloat1, float[] arg3)
    {
      this.left = paramArrayOfFloat1;
      Object localObject;
      this.right = localObject;
    }

    ColumnDef(float paramFloat1, float arg3)
    {
      this.left = new float[4];
      this.left[0] = paramFloat1;
      this.left[1] = MultiColumnText.access$100(MultiColumnText.this);
      this.left[2] = paramFloat1;
      if ((MultiColumnText.this.desiredHeight == -1.0F) || (MultiColumnText.this.top == -1.0F))
        this.left[3] = -1.0F;
      else
        this.left[3] = (MultiColumnText.access$100(MultiColumnText.this) - MultiColumnText.access$200(MultiColumnText.this));
      this.right = new float[4];
      Object localObject;
      this.right[0] = localObject;
      this.right[1] = MultiColumnText.access$100(MultiColumnText.this);
      this.right[2] = localObject;
      if ((MultiColumnText.this.desiredHeight == -1.0F) || (MultiColumnText.this.top == -1.0F))
        this.right[3] = -1.0F;
      else
        this.right[3] = (MultiColumnText.access$100(MultiColumnText.this) - MultiColumnText.access$200(MultiColumnText.this));
    }

    float[] resolvePositions(int paramInt)
    {
      if (paramInt == 4)
        return resolvePositions(this.left);
      return resolvePositions(this.right);
    }

    private float[] resolvePositions(float[] paramArrayOfFloat)
    {
      if (!isSimple())
      {
        paramArrayOfFloat[1] = MultiColumnText.access$100(MultiColumnText.this);
        return paramArrayOfFloat;
      }
      if (MultiColumnText.this.top == -1.0F)
        throw new RuntimeException("resolvePositions called with top=AUTOMATIC (-1).  Top position must be set befure lines can be resolved");
      paramArrayOfFloat[1] = MultiColumnText.access$100(MultiColumnText.this);
      paramArrayOfFloat[3] = MultiColumnText.access$300(MultiColumnText.this);
      return paramArrayOfFloat;
    }

    private boolean isSimple()
    {
      return (this.left.length == 4) && (this.right.length == 4) && (this.left[0] == this.left[2]) && (this.right[0] == this.right[2]);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.MultiColumnText
 * JD-Core Version:    0.6.0
 */