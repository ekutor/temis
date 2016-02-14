package com.lowagie.text.pdf.internal;

import com.lowagie.text.Annotation;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PdfAnnotationsImp
{
  protected PdfAcroForm acroForm;
  protected ArrayList annotations;
  protected ArrayList delayedAnnotations = new ArrayList();

  public PdfAnnotationsImp(PdfWriter paramPdfWriter)
  {
    this.acroForm = new PdfAcroForm(paramPdfWriter);
  }

  public boolean hasValidAcroForm()
  {
    return this.acroForm.isValid();
  }

  public PdfAcroForm getAcroForm()
  {
    return this.acroForm;
  }

  public void setSigFlags(int paramInt)
  {
    this.acroForm.setSigFlags(paramInt);
  }

  public void addCalculationOrder(PdfFormField paramPdfFormField)
  {
    this.acroForm.addCalculationOrder(paramPdfFormField);
  }

  public void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    if (paramPdfAnnotation.isForm())
    {
      PdfFormField localPdfFormField = (PdfFormField)paramPdfAnnotation;
      if (localPdfFormField.getParent() == null)
        addFormFieldRaw(localPdfFormField);
    }
    else
    {
      this.annotations.add(paramPdfAnnotation);
    }
  }

  public void addPlainAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    this.annotations.add(paramPdfAnnotation);
  }

  void addFormFieldRaw(PdfFormField paramPdfFormField)
  {
    this.annotations.add(paramPdfFormField);
    ArrayList localArrayList = paramPdfFormField.getKids();
    if (localArrayList != null)
      for (int i = 0; i < localArrayList.size(); i++)
        addFormFieldRaw((PdfFormField)localArrayList.get(i));
  }

  public boolean hasUnusedAnnotations()
  {
    return !this.annotations.isEmpty();
  }

  public void resetAnnotations()
  {
    this.annotations = this.delayedAnnotations;
    this.delayedAnnotations = new ArrayList();
  }

  public PdfArray rotateAnnotations(PdfWriter paramPdfWriter, Rectangle paramRectangle)
  {
    PdfArray localPdfArray = new PdfArray();
    int i = paramRectangle.getRotation() % 360;
    int j = paramPdfWriter.getCurrentPageNumber();
    for (int k = 0; k < this.annotations.size(); k++)
    {
      PdfAnnotation localPdfAnnotation = (PdfAnnotation)this.annotations.get(k);
      int m = localPdfAnnotation.getPlaceInPage();
      if (m > j)
      {
        this.delayedAnnotations.add(localPdfAnnotation);
      }
      else
      {
        Object localObject;
        if (localPdfAnnotation.isForm())
        {
          if (!localPdfAnnotation.isUsed())
          {
            localObject = localPdfAnnotation.getTemplates();
            if (localObject != null)
              this.acroForm.addFieldTemplates((HashMap)localObject);
          }
          localObject = (PdfFormField)localPdfAnnotation;
          if (((PdfFormField)localObject).getParent() == null)
            this.acroForm.addDocumentField(((PdfFormField)localObject).getIndirectReference());
        }
        if (localPdfAnnotation.isAnnotation())
        {
          localPdfArray.add(localPdfAnnotation.getIndirectReference());
          if (!localPdfAnnotation.isUsed())
          {
            localObject = (PdfRectangle)localPdfAnnotation.get(PdfName.RECT);
            if (localObject != null)
              switch (i)
              {
              case 90:
                localPdfAnnotation.put(PdfName.RECT, new PdfRectangle(paramRectangle.getTop() - ((PdfRectangle)localObject).bottom(), ((PdfRectangle)localObject).left(), paramRectangle.getTop() - ((PdfRectangle)localObject).top(), ((PdfRectangle)localObject).right()));
                break;
              case 180:
                localPdfAnnotation.put(PdfName.RECT, new PdfRectangle(paramRectangle.getRight() - ((PdfRectangle)localObject).left(), paramRectangle.getTop() - ((PdfRectangle)localObject).bottom(), paramRectangle.getRight() - ((PdfRectangle)localObject).right(), paramRectangle.getTop() - ((PdfRectangle)localObject).top()));
                break;
              case 270:
                localPdfAnnotation.put(PdfName.RECT, new PdfRectangle(((PdfRectangle)localObject).bottom(), paramRectangle.getRight() - ((PdfRectangle)localObject).left(), ((PdfRectangle)localObject).top(), paramRectangle.getRight() - ((PdfRectangle)localObject).right()));
              }
          }
        }
        if (localPdfAnnotation.isUsed())
          continue;
        localPdfAnnotation.setUsed();
        try
        {
          paramPdfWriter.addToBody(localPdfAnnotation, localPdfAnnotation.getIndirectReference());
        }
        catch (IOException localIOException)
        {
          throw new ExceptionConverter(localIOException);
        }
      }
    }
    return (PdfArray)localPdfArray;
  }

  public static PdfAnnotation convertAnnotation(PdfWriter paramPdfWriter, Annotation paramAnnotation, Rectangle paramRectangle)
    throws IOException
  {
    switch (paramAnnotation.annotationType())
    {
    case 1:
      return new PdfAnnotation(paramPdfWriter, paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury(), new PdfAction((URL)paramAnnotation.attributes().get("url")));
    case 2:
      return new PdfAnnotation(paramPdfWriter, paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury(), new PdfAction((String)paramAnnotation.attributes().get("file")));
    case 3:
      return new PdfAnnotation(paramPdfWriter, paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury(), new PdfAction((String)paramAnnotation.attributes().get("file"), (String)paramAnnotation.attributes().get("destination")));
    case 7:
      boolean[] arrayOfBoolean = (boolean[])paramAnnotation.attributes().get("parameters");
      String str1 = (String)paramAnnotation.attributes().get("file");
      String str2 = (String)paramAnnotation.attributes().get("mime");
      PdfFileSpecification localPdfFileSpecification;
      if (arrayOfBoolean[0] != 0)
        localPdfFileSpecification = PdfFileSpecification.fileEmbedded(paramPdfWriter, str1, str1, null);
      else
        localPdfFileSpecification = PdfFileSpecification.fileExtern(paramPdfWriter, str1);
      PdfAnnotation localPdfAnnotation = PdfAnnotation.createScreen(paramPdfWriter, new Rectangle(paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury()), str1, localPdfFileSpecification, str2, arrayOfBoolean[1]);
      return localPdfAnnotation;
    case 4:
      return new PdfAnnotation(paramPdfWriter, paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury(), new PdfAction((String)paramAnnotation.attributes().get("file"), ((Integer)paramAnnotation.attributes().get("page")).intValue()));
    case 5:
      return new PdfAnnotation(paramPdfWriter, paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury(), new PdfAction(((Integer)paramAnnotation.attributes().get("named")).intValue()));
    case 6:
      return new PdfAnnotation(paramPdfWriter, paramAnnotation.llx(), paramAnnotation.lly(), paramAnnotation.urx(), paramAnnotation.ury(), new PdfAction((String)paramAnnotation.attributes().get("application"), (String)paramAnnotation.attributes().get("parameters"), (String)paramAnnotation.attributes().get("operation"), (String)paramAnnotation.attributes().get("defaultdir")));
    }
    return new PdfAnnotation(paramPdfWriter, paramRectangle.getLeft(), paramRectangle.getBottom(), paramRectangle.getRight(), paramRectangle.getTop(), new PdfString(paramAnnotation.title(), "UnicodeBig"), new PdfString(paramAnnotation.content(), "UnicodeBig"));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.internal.PdfAnnotationsImp
 * JD-Core Version:    0.6.0
 */