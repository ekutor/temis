package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class VerticalText
{
  public static final int NO_MORE_TEXT = 1;
  public static final int NO_MORE_COLUMN = 2;
  protected ArrayList chunks = new ArrayList();
  protected PdfContentByte text;
  protected int alignment = 0;
  protected int currentChunkMarker = -1;
  protected PdfChunk currentStandbyChunk;
  protected String splittedChunkText;
  protected float leading;
  protected float startX;
  protected float startY;
  protected int maxLines;
  protected float height;

  public VerticalText(PdfContentByte paramPdfContentByte)
  {
    this.text = paramPdfContentByte;
  }

  public void addText(Phrase paramPhrase)
  {
    Iterator localIterator = paramPhrase.getChunks().iterator();
    while (localIterator.hasNext())
      this.chunks.add(new PdfChunk((Chunk)localIterator.next(), null));
  }

  public void addText(Chunk paramChunk)
  {
    this.chunks.add(new PdfChunk(paramChunk, null));
  }

  public void setVerticalLayout(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, float paramFloat4)
  {
    this.startX = paramFloat1;
    this.startY = paramFloat2;
    this.height = paramFloat3;
    this.maxLines = paramInt;
    setLeading(paramFloat4);
  }

  public void setLeading(float paramFloat)
  {
    this.leading = paramFloat;
  }

  public float getLeading()
  {
    return this.leading;
  }

  protected PdfLine createLine(float paramFloat)
  {
    if (this.chunks.isEmpty())
      return null;
    this.splittedChunkText = null;
    this.currentStandbyChunk = null;
    PdfLine localPdfLine = new PdfLine(0.0F, paramFloat, this.alignment, 0.0F);
    this.currentChunkMarker = 0;
    while (this.currentChunkMarker < this.chunks.size())
    {
      PdfChunk localPdfChunk = (PdfChunk)this.chunks.get(this.currentChunkMarker);
      String str = localPdfChunk.toString();
      this.currentStandbyChunk = localPdfLine.add(localPdfChunk);
      if (this.currentStandbyChunk != null)
      {
        this.splittedChunkText = localPdfChunk.toString();
        localPdfChunk.setValue(str);
        return localPdfLine;
      }
      this.currentChunkMarker += 1;
    }
    return localPdfLine;
  }

  protected void shortenChunkArray()
  {
    if (this.currentChunkMarker < 0)
      return;
    if (this.currentChunkMarker >= this.chunks.size())
    {
      this.chunks.clear();
      return;
    }
    PdfChunk localPdfChunk = (PdfChunk)this.chunks.get(this.currentChunkMarker);
    localPdfChunk.setValue(this.splittedChunkText);
    this.chunks.set(this.currentChunkMarker, this.currentStandbyChunk);
    for (int i = this.currentChunkMarker - 1; i >= 0; i--)
      this.chunks.remove(i);
  }

  public int go()
  {
    return go(false);
  }

  public int go(boolean paramBoolean)
  {
    int i = 0;
    PdfContentByte localPdfContentByte = null;
    if (this.text != null)
      localPdfContentByte = this.text.getDuplicate();
    else if (!paramBoolean)
      throw new NullPointerException("VerticalText.go with simulate==false and text==null.");
    int j = 0;
    while (true)
    {
      if (this.maxLines <= 0)
      {
        j = 2;
        if (!this.chunks.isEmpty())
          break;
        j |= 1;
        break;
      }
      if (this.chunks.isEmpty())
      {
        j = 1;
        break;
      }
      PdfLine localPdfLine = createLine(this.height);
      if ((!paramBoolean) && (i == 0))
      {
        this.text.beginText();
        i = 1;
      }
      shortenChunkArray();
      if (!paramBoolean)
      {
        this.text.setTextMatrix(this.startX, this.startY - localPdfLine.indentLeft());
        writeLine(localPdfLine, this.text, localPdfContentByte);
      }
      this.maxLines -= 1;
      this.startX -= this.leading;
    }
    if (i != 0)
    {
      this.text.endText();
      this.text.add(localPdfContentByte);
    }
    return j;
  }

  void writeLine(PdfLine paramPdfLine, PdfContentByte paramPdfContentByte1, PdfContentByte paramPdfContentByte2)
  {
    Object localObject = null;
    Iterator localIterator = paramPdfLine.iterator();
    while (localIterator.hasNext())
    {
      PdfChunk localPdfChunk = (PdfChunk)localIterator.next();
      if (localPdfChunk.font().compareTo(localObject) != 0)
      {
        localObject = localPdfChunk.font();
        paramPdfContentByte1.setFontAndSize(((PdfFont)localObject).getFont(), ((PdfFont)localObject).size());
      }
      Color localColor = localPdfChunk.color();
      if (localColor != null)
        paramPdfContentByte1.setColorFill(localColor);
      paramPdfContentByte1.showText(localPdfChunk.toString());
      if (localColor == null)
        continue;
      paramPdfContentByte1.resetRGBColorFill();
    }
  }

  public void setOrigin(float paramFloat1, float paramFloat2)
  {
    this.startX = paramFloat1;
    this.startY = paramFloat2;
  }

  public float getOriginX()
  {
    return this.startX;
  }

  public float getOriginY()
  {
    return this.startY;
  }

  public int getMaxLines()
  {
    return this.maxLines;
  }

  public void setMaxLines(int paramInt)
  {
    this.maxLines = paramInt;
  }

  public float getHeight()
  {
    return this.height;
  }

  public void setHeight(float paramFloat)
  {
    this.height = paramFloat;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public int getAlignment()
  {
    return this.alignment;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.VerticalText
 * JD-Core Version:    0.6.0
 */