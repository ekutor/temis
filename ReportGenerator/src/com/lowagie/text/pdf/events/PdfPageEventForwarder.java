package com.lowagie.text.pdf.events;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfPageEventForwarder
  implements PdfPageEvent
{
  protected ArrayList events = new ArrayList();

  public void addPageEvent(PdfPageEvent paramPdfPageEvent)
  {
    this.events.add(paramPdfPageEvent);
  }

  public void onOpenDocument(PdfWriter paramPdfWriter, Document paramDocument)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onOpenDocument(paramPdfWriter, paramDocument);
    }
  }

  public void onStartPage(PdfWriter paramPdfWriter, Document paramDocument)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onStartPage(paramPdfWriter, paramDocument);
    }
  }

  public void onEndPage(PdfWriter paramPdfWriter, Document paramDocument)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onEndPage(paramPdfWriter, paramDocument);
    }
  }

  public void onCloseDocument(PdfWriter paramPdfWriter, Document paramDocument)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onCloseDocument(paramPdfWriter, paramDocument);
    }
  }

  public void onParagraph(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onParagraph(paramPdfWriter, paramDocument, paramFloat);
    }
  }

  public void onParagraphEnd(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onParagraphEnd(paramPdfWriter, paramDocument, paramFloat);
    }
  }

  public void onChapter(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat, Paragraph paramParagraph)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onChapter(paramPdfWriter, paramDocument, paramFloat, paramParagraph);
    }
  }

  public void onChapterEnd(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onChapterEnd(paramPdfWriter, paramDocument, paramFloat);
    }
  }

  public void onSection(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat, int paramInt, Paragraph paramParagraph)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onSection(paramPdfWriter, paramDocument, paramFloat, paramInt, paramParagraph);
    }
  }

  public void onSectionEnd(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onSectionEnd(paramPdfWriter, paramDocument, paramFloat);
    }
  }

  public void onGenericTag(PdfWriter paramPdfWriter, Document paramDocument, Rectangle paramRectangle, String paramString)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPageEvent localPdfPageEvent = (PdfPageEvent)localIterator.next();
      localPdfPageEvent.onGenericTag(paramPdfWriter, paramDocument, paramRectangle, paramString);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.events.PdfPageEventForwarder
 * JD-Core Version:    0.6.0
 */