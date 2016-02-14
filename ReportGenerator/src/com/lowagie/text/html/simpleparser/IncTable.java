package com.lowagie.text.html.simpleparser;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class IncTable
{
  private HashMap props = new HashMap();
  private ArrayList rows = new ArrayList();
  private ArrayList cols;

  public IncTable(HashMap paramHashMap)
  {
    this.props.putAll(paramHashMap);
  }

  public void addCol(PdfPCell paramPdfPCell)
  {
    if (this.cols == null)
      this.cols = new ArrayList();
    this.cols.add(paramPdfPCell);
  }

  public void addCols(ArrayList paramArrayList)
  {
    if (this.cols == null)
      this.cols = new ArrayList(paramArrayList);
    else
      this.cols.addAll(paramArrayList);
  }

  public void endRow()
  {
    if (this.cols != null)
    {
      Collections.reverse(this.cols);
      this.rows.add(this.cols);
      this.cols = null;
    }
  }

  public ArrayList getRows()
  {
    return this.rows;
  }

  public PdfPTable buildTable()
  {
    if (this.rows.isEmpty())
      return new PdfPTable(1);
    int i = 0;
    ArrayList localArrayList1 = (ArrayList)this.rows.get(0);
    for (int j = 0; j < localArrayList1.size(); j++)
      i += ((PdfPCell)localArrayList1.get(j)).getColspan();
    PdfPTable localPdfPTable = new PdfPTable(i);
    String str = (String)this.props.get("width");
    if (str == null)
    {
      localPdfPTable.setWidthPercentage(100.0F);
    }
    else if (str.endsWith("%"))
    {
      localPdfPTable.setWidthPercentage(Float.parseFloat(str.substring(0, str.length() - 1)));
    }
    else
    {
      localPdfPTable.setTotalWidth(Float.parseFloat(str));
      localPdfPTable.setLockedWidth(true);
    }
    for (int k = 0; k < this.rows.size(); k++)
    {
      ArrayList localArrayList2 = (ArrayList)this.rows.get(k);
      for (int m = 0; m < localArrayList2.size(); m++)
        localPdfPTable.addCell((PdfPCell)localArrayList2.get(m));
    }
    return localPdfPTable;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.IncTable
 * JD-Core Version:    0.6.0
 */