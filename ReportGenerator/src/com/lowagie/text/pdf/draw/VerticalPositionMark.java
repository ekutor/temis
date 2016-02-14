package com.lowagie.text.pdf.draw;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.pdf.PdfContentByte;
import java.util.ArrayList;

public class VerticalPositionMark
  implements DrawInterface, Element
{
  protected DrawInterface drawInterface = null;
  protected float offset = 0.0F;

  public VerticalPositionMark()
  {
  }

  public VerticalPositionMark(DrawInterface paramDrawInterface, float paramFloat)
  {
    this.drawInterface = paramDrawInterface;
    this.offset = paramFloat;
  }

  public void draw(PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    if (this.drawInterface != null)
      this.drawInterface.draw(paramPdfContentByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5 + this.offset);
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
    return 55;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return false;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new Chunk(this, true));
    return localArrayList;
  }

  public DrawInterface getDrawInterface()
  {
    return this.drawInterface;
  }

  public void setDrawInterface(DrawInterface paramDrawInterface)
  {
    this.drawInterface = paramDrawInterface;
  }

  public float getOffset()
  {
    return this.offset;
  }

  public void setOffset(float paramFloat)
  {
    this.offset = paramFloat;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.draw.VerticalPositionMark
 * JD-Core Version:    0.6.0
 */