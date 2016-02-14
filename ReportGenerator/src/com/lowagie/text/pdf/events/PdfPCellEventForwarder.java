package com.lowagie.text.pdf.events;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfPCellEventForwarder
  implements PdfPCellEvent
{
  protected ArrayList events = new ArrayList();

  public void addCellEvent(PdfPCellEvent paramPdfPCellEvent)
  {
    this.events.add(paramPdfPCellEvent);
  }

  public void cellLayout(PdfPCell paramPdfPCell, Rectangle paramRectangle, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPCellEvent localPdfPCellEvent = (PdfPCellEvent)localIterator.next();
      localPdfPCellEvent.cellLayout(paramPdfPCell, paramRectangle, paramArrayOfPdfContentByte);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.events.PdfPCellEventForwarder
 * JD-Core Version:    0.6.0
 */