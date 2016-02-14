package com.lowagie.text.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class PRAcroForm extends PdfDictionary
{
  ArrayList fields;
  ArrayList stack;
  HashMap fieldByName;
  PdfReader reader;

  public PRAcroForm(PdfReader paramPdfReader)
  {
    this.reader = paramPdfReader;
    this.fields = new ArrayList();
    this.fieldByName = new HashMap();
    this.stack = new ArrayList();
  }

  public int size()
  {
    return this.fields.size();
  }

  public ArrayList getFields()
  {
    return this.fields;
  }

  public FieldInformation getField(String paramString)
  {
    return (FieldInformation)this.fieldByName.get(paramString);
  }

  public PRIndirectReference getRefByName(String paramString)
  {
    FieldInformation localFieldInformation = (FieldInformation)this.fieldByName.get(paramString);
    if (localFieldInformation == null)
      return null;
    return localFieldInformation.getRef();
  }

  public void readAcroForm(PdfDictionary paramPdfDictionary)
  {
    if (paramPdfDictionary == null)
      return;
    this.hashMap = paramPdfDictionary.hashMap;
    pushAttrib(paramPdfDictionary);
    PdfArray localPdfArray = (PdfArray)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.FIELDS));
    iterateFields(localPdfArray, null, null);
  }

  protected void iterateFields(PdfArray paramPdfArray, PRIndirectReference paramPRIndirectReference, String paramString)
  {
    ListIterator localListIterator = paramPdfArray.listIterator();
    while (localListIterator.hasNext())
    {
      PRIndirectReference localPRIndirectReference1 = (PRIndirectReference)localListIterator.next();
      PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObjectRelease(localPRIndirectReference1);
      PRIndirectReference localPRIndirectReference2 = paramPRIndirectReference;
      String str = paramString;
      PdfString localPdfString = (PdfString)localPdfDictionary1.get(PdfName.T);
      int i = localPdfString != null ? 1 : 0;
      if (i != 0)
      {
        localPRIndirectReference2 = localPRIndirectReference1;
        if (paramString == null)
          str = localPdfString.toString();
        else
          str = paramString + '.' + localPdfString.toString();
      }
      PdfArray localPdfArray = (PdfArray)localPdfDictionary1.get(PdfName.KIDS);
      if (localPdfArray != null)
      {
        pushAttrib(localPdfDictionary1);
        iterateFields(localPdfArray, localPRIndirectReference2, str);
        this.stack.remove(this.stack.size() - 1);
        continue;
      }
      if (localPRIndirectReference2 == null)
        continue;
      PdfDictionary localPdfDictionary2 = (PdfDictionary)this.stack.get(this.stack.size() - 1);
      if (i != 0)
        localPdfDictionary2 = mergeAttrib(localPdfDictionary2, localPdfDictionary1);
      localPdfDictionary2.put(PdfName.T, new PdfString(str));
      FieldInformation localFieldInformation = new FieldInformation(str, localPdfDictionary2, localPRIndirectReference2);
      this.fields.add(localFieldInformation);
      this.fieldByName.put(str, localFieldInformation);
    }
  }

  protected PdfDictionary mergeAttrib(PdfDictionary paramPdfDictionary1, PdfDictionary paramPdfDictionary2)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    if (paramPdfDictionary1 != null)
      localPdfDictionary.putAll(paramPdfDictionary1);
    Iterator localIterator = paramPdfDictionary2.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      if ((!localPdfName.equals(PdfName.DR)) && (!localPdfName.equals(PdfName.DA)) && (!localPdfName.equals(PdfName.Q)) && (!localPdfName.equals(PdfName.FF)) && (!localPdfName.equals(PdfName.DV)) && (!localPdfName.equals(PdfName.V)) && (!localPdfName.equals(PdfName.FT)) && (!localPdfName.equals(PdfName.F)))
        continue;
      localPdfDictionary.put(localPdfName, paramPdfDictionary2.get(localPdfName));
    }
    return localPdfDictionary;
  }

  protected void pushAttrib(PdfDictionary paramPdfDictionary)
  {
    PdfDictionary localPdfDictionary = null;
    if (!this.stack.isEmpty())
      localPdfDictionary = (PdfDictionary)this.stack.get(this.stack.size() - 1);
    localPdfDictionary = mergeAttrib(localPdfDictionary, paramPdfDictionary);
    this.stack.add(localPdfDictionary);
  }

  public static class FieldInformation
  {
    String name;
    PdfDictionary info;
    PRIndirectReference ref;

    FieldInformation(String paramString, PdfDictionary paramPdfDictionary, PRIndirectReference paramPRIndirectReference)
    {
      this.name = paramString;
      this.info = paramPdfDictionary;
      this.ref = paramPRIndirectReference;
    }

    public String getName()
    {
      return this.name;
    }

    public PdfDictionary getInfo()
    {
      return this.info;
    }

    public PRIndirectReference getRef()
    {
      return this.ref;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PRAcroForm
 * JD-Core Version:    0.6.0
 */