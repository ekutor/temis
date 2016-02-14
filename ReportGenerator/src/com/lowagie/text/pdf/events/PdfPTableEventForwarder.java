package com.lowagie.text.pdf.events;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfPTableEventForwarder
  implements PdfPTableEvent
{
  protected ArrayList events = new ArrayList();

  public void addTableEvent(PdfPTableEvent paramPdfPTableEvent)
  {
    this.events.add(paramPdfPTableEvent);
  }

  public void tableLayout(PdfPTable paramPdfPTable, float[][] paramArrayOfFloat, float[] paramArrayOfFloat1, int paramInt1, int paramInt2, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    Iterator localIterator = this.events.iterator();
    while (localIterator.hasNext())
    {
      PdfPTableEvent localPdfPTableEvent = (PdfPTableEvent)localIterator.next();
      localPdfPTableEvent.tableLayout(paramPdfPTable, paramArrayOfFloat, paramArrayOfFloat1, paramInt1, paramInt2, paramArrayOfPdfContentByte);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.events.PdfPTableEventForwarder
 * JD-Core Version:    0.6.0
 */