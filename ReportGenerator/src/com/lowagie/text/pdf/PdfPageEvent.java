package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;

public abstract interface PdfPageEvent
{
  public abstract void onOpenDocument(PdfWriter paramPdfWriter, Document paramDocument);

  public abstract void onStartPage(PdfWriter paramPdfWriter, Document paramDocument);

  public abstract void onEndPage(PdfWriter paramPdfWriter, Document paramDocument);

  public abstract void onCloseDocument(PdfWriter paramPdfWriter, Document paramDocument);

  public abstract void onParagraph(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat);

  public abstract void onParagraphEnd(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat);

  public abstract void onChapter(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat, Paragraph paramParagraph);

  public abstract void onChapterEnd(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat);

  public abstract void onSection(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat, int paramInt, Paragraph paramParagraph);

  public abstract void onSectionEnd(PdfWriter paramPdfWriter, Document paramDocument, float paramFloat);

  public abstract void onGenericTag(PdfWriter paramPdfWriter, Document paramDocument, Rectangle paramRectangle, String paramString);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfPageEvent
 * JD-Core Version:    0.6.0
 */