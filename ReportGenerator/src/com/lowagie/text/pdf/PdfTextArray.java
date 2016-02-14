package com.lowagie.text.pdf;

import java.util.ArrayList;

public class PdfTextArray
{
  ArrayList arrayList = new ArrayList();
  private String lastStr;
  private Float lastNum;

  public PdfTextArray(String paramString)
  {
    add(paramString);
  }

  public PdfTextArray()
  {
  }

  public void add(PdfNumber paramPdfNumber)
  {
    add((float)paramPdfNumber.doubleValue());
  }

  public void add(float paramFloat)
  {
    if (paramFloat != 0.0F)
    {
      if (this.lastNum != null)
      {
        this.lastNum = new Float(paramFloat + this.lastNum.floatValue());
        if (this.lastNum.floatValue() != 0.0F)
          replaceLast(this.lastNum);
        else
          this.arrayList.remove(this.arrayList.size() - 1);
      }
      else
      {
        this.lastNum = new Float(paramFloat);
        this.arrayList.add(this.lastNum);
      }
      this.lastStr = null;
    }
  }

  public void add(String paramString)
  {
    if (paramString.length() > 0)
    {
      if (this.lastStr != null)
      {
        this.lastStr += paramString;
        replaceLast(this.lastStr);
      }
      else
      {
        this.lastStr = paramString;
        this.arrayList.add(this.lastStr);
      }
      this.lastNum = null;
    }
  }

  ArrayList getArrayList()
  {
    return this.arrayList;
  }

  private void replaceLast(Object paramObject)
  {
    this.arrayList.set(this.arrayList.size() - 1, paramObject);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfTextArray
 * JD-Core Version:    0.6.0
 */