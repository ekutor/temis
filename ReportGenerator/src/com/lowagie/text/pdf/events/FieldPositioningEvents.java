package com.lowagie.text.pdf.events;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TextField;
import java.io.IOException;
import java.util.HashMap;

public class FieldPositioningEvents extends PdfPageEventHelper
  implements PdfPCellEvent
{
  protected HashMap genericChunkFields = new HashMap();
  protected PdfFormField cellField = null;
  protected PdfWriter fieldWriter = null;
  protected PdfFormField parent = null;
  public float padding;

  public FieldPositioningEvents()
  {
  }

  public void addField(String paramString, PdfFormField paramPdfFormField)
  {
    this.genericChunkFields.put(paramString, paramPdfFormField);
  }

  public FieldPositioningEvents(PdfWriter paramPdfWriter, PdfFormField paramPdfFormField)
  {
    this.cellField = paramPdfFormField;
    this.fieldWriter = paramPdfWriter;
  }

  public FieldPositioningEvents(PdfFormField paramPdfFormField1, PdfFormField paramPdfFormField2)
  {
    this.cellField = paramPdfFormField2;
    this.parent = paramPdfFormField1;
  }

  public FieldPositioningEvents(PdfWriter paramPdfWriter, String paramString)
    throws IOException, DocumentException
  {
    this.fieldWriter = paramPdfWriter;
    TextField localTextField = new TextField(paramPdfWriter, new Rectangle(0.0F, 0.0F), paramString);
    localTextField.setFontSize(14.0F);
    this.cellField = localTextField.getTextField();
  }

  public FieldPositioningEvents(PdfWriter paramPdfWriter, PdfFormField paramPdfFormField, String paramString)
    throws IOException, DocumentException
  {
    this.parent = paramPdfFormField;
    TextField localTextField = new TextField(paramPdfWriter, new Rectangle(0.0F, 0.0F), paramString);
    localTextField.setFontSize(14.0F);
    this.cellField = localTextField.getTextField();
  }

  public void setPadding(float paramFloat)
  {
    this.padding = paramFloat;
  }

  public void setParent(PdfFormField paramPdfFormField)
  {
    this.parent = paramPdfFormField;
  }

  public void onGenericTag(PdfWriter paramPdfWriter, Document paramDocument, Rectangle paramRectangle, String paramString)
  {
    paramRectangle.setBottom(paramRectangle.getBottom() - 3.0F);
    PdfFormField localPdfFormField = (PdfFormField)this.genericChunkFields.get(paramString);
    if (localPdfFormField == null)
    {
      TextField localTextField = new TextField(paramPdfWriter, new Rectangle(paramRectangle.getLeft(this.padding), paramRectangle.getBottom(this.padding), paramRectangle.getRight(this.padding), paramRectangle.getTop(this.padding)), paramString);
      localTextField.setFontSize(14.0F);
      try
      {
        localPdfFormField = localTextField.getTextField();
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
    }
    else
    {
      localPdfFormField.put(PdfName.RECT, new PdfRectangle(paramRectangle.getLeft(this.padding), paramRectangle.getBottom(this.padding), paramRectangle.getRight(this.padding), paramRectangle.getTop(this.padding)));
    }
    if (this.parent == null)
      paramPdfWriter.addAnnotation(localPdfFormField);
    else
      this.parent.addKid(localPdfFormField);
  }

  public void cellLayout(PdfPCell paramPdfPCell, Rectangle paramRectangle, PdfContentByte[] paramArrayOfPdfContentByte)
  {
    if ((this.cellField == null) || ((this.fieldWriter == null) && (this.parent == null)))
      throw new ExceptionConverter(new IllegalArgumentException("You have used the wrong constructor for this FieldPositioningEvents class."));
    this.cellField.put(PdfName.RECT, new PdfRectangle(paramRectangle.getLeft(this.padding), paramRectangle.getBottom(this.padding), paramRectangle.getRight(this.padding), paramRectangle.getTop(this.padding)));
    if (this.parent == null)
      this.fieldWriter.addAnnotation(this.cellField);
    else
      this.parent.addKid(this.cellField);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.events.FieldPositioningEvents
 * JD-Core Version:    0.6.0
 */