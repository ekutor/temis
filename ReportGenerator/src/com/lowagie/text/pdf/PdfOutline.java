package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfOutline extends PdfDictionary
{
  private PdfIndirectReference reference;
  private int count = 0;
  private PdfOutline parent;
  private PdfDestination destination;
  private PdfAction action;
  protected ArrayList kids = new ArrayList();
  protected PdfWriter writer;
  private String tag;
  private boolean open;
  private Color color;
  private int style = 0;

  PdfOutline(PdfWriter paramPdfWriter)
  {
    super(OUTLINES);
    this.open = true;
    this.parent = null;
    this.writer = paramPdfWriter;
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfAction paramPdfAction, String paramString)
  {
    this(paramPdfOutline, paramPdfAction, paramString, true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfAction paramPdfAction, String paramString, boolean paramBoolean)
  {
    this.action = paramPdfAction;
    initOutline(paramPdfOutline, paramString, paramBoolean);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfDestination paramPdfDestination, String paramString)
  {
    this(paramPdfOutline, paramPdfDestination, paramString, true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfDestination paramPdfDestination, String paramString, boolean paramBoolean)
  {
    this.destination = paramPdfDestination;
    initOutline(paramPdfOutline, paramString, paramBoolean);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfAction paramPdfAction, PdfString paramPdfString)
  {
    this(paramPdfOutline, paramPdfAction, paramPdfString, true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfAction paramPdfAction, PdfString paramPdfString, boolean paramBoolean)
  {
    this(paramPdfOutline, paramPdfAction, paramPdfString.toString(), paramBoolean);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfDestination paramPdfDestination, PdfString paramPdfString)
  {
    this(paramPdfOutline, paramPdfDestination, paramPdfString, true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfDestination paramPdfDestination, PdfString paramPdfString, boolean paramBoolean)
  {
    this(paramPdfOutline, paramPdfDestination, paramPdfString.toString(), true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfAction paramPdfAction, Paragraph paramParagraph)
  {
    this(paramPdfOutline, paramPdfAction, paramParagraph, true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfAction paramPdfAction, Paragraph paramParagraph, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = paramParagraph.getChunks().iterator();
    while (localIterator.hasNext())
    {
      Chunk localChunk = (Chunk)localIterator.next();
      localStringBuffer.append(localChunk.getContent());
    }
    this.action = paramPdfAction;
    initOutline(paramPdfOutline, localStringBuffer.toString(), paramBoolean);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfDestination paramPdfDestination, Paragraph paramParagraph)
  {
    this(paramPdfOutline, paramPdfDestination, paramParagraph, true);
  }

  public PdfOutline(PdfOutline paramPdfOutline, PdfDestination paramPdfDestination, Paragraph paramParagraph, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = paramParagraph.getChunks().iterator();
    while (localIterator.hasNext())
    {
      Chunk localChunk = (Chunk)localIterator.next();
      localStringBuffer.append(localChunk.getContent());
    }
    this.destination = paramPdfDestination;
    initOutline(paramPdfOutline, localStringBuffer.toString(), paramBoolean);
  }

  void initOutline(PdfOutline paramPdfOutline, String paramString, boolean paramBoolean)
  {
    this.open = paramBoolean;
    this.parent = paramPdfOutline;
    this.writer = paramPdfOutline.writer;
    put(PdfName.TITLE, new PdfString(paramString, "UnicodeBig"));
    paramPdfOutline.addKid(this);
    if ((this.destination != null) && (!this.destination.hasPage()))
      setDestinationPage(this.writer.getCurrentPage());
  }

  public void setIndirectReference(PdfIndirectReference paramPdfIndirectReference)
  {
    this.reference = paramPdfIndirectReference;
  }

  public PdfIndirectReference indirectReference()
  {
    return this.reference;
  }

  public PdfOutline parent()
  {
    return this.parent;
  }

  public boolean setDestinationPage(PdfIndirectReference paramPdfIndirectReference)
  {
    if (this.destination == null)
      return false;
    return this.destination.addPage(paramPdfIndirectReference);
  }

  public PdfDestination getPdfDestination()
  {
    return this.destination;
  }

  int getCount()
  {
    return this.count;
  }

  void setCount(int paramInt)
  {
    this.count = paramInt;
  }

  public int level()
  {
    if (this.parent == null)
      return 0;
    return this.parent.level() + 1;
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    if ((this.color != null) && (!this.color.equals(Color.black)))
      put(PdfName.C, new PdfArray(new float[] { this.color.getRed() / 255.0F, this.color.getGreen() / 255.0F, this.color.getBlue() / 255.0F }));
    int i = 0;
    if ((this.style & 0x1) != 0)
      i |= 2;
    if ((this.style & 0x2) != 0)
      i |= 1;
    if (i != 0)
      put(PdfName.F, new PdfNumber(i));
    if (this.parent != null)
      put(PdfName.PARENT, this.parent.indirectReference());
    if ((this.destination != null) && (this.destination.hasPage()))
      put(PdfName.DEST, this.destination);
    if (this.action != null)
      put(PdfName.A, this.action);
    if (this.count != 0)
      put(PdfName.COUNT, new PdfNumber(this.count));
    super.toPdf(paramPdfWriter, paramOutputStream);
  }

  public void addKid(PdfOutline paramPdfOutline)
  {
    this.kids.add(paramPdfOutline);
  }

  public ArrayList getKids()
  {
    return this.kids;
  }

  public void setKids(ArrayList paramArrayList)
  {
    this.kids = paramArrayList;
  }

  public String getTag()
  {
    return this.tag;
  }

  public void setTag(String paramString)
  {
    this.tag = paramString;
  }

  public String getTitle()
  {
    PdfString localPdfString = (PdfString)get(PdfName.TITLE);
    return localPdfString.toString();
  }

  public void setTitle(String paramString)
  {
    put(PdfName.TITLE, new PdfString(paramString, "UnicodeBig"));
  }

  public boolean isOpen()
  {
    return this.open;
  }

  public void setOpen(boolean paramBoolean)
  {
    this.open = paramBoolean;
  }

  public Color getColor()
  {
    return this.color;
  }

  public void setColor(Color paramColor)
  {
    this.color = paramColor;
  }

  public int getStyle()
  {
    return this.style;
  }

  public void setStyle(int paramInt)
  {
    this.style = paramInt;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfOutline
 * JD-Core Version:    0.6.0
 */