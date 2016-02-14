package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Phrase;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.PdfPCell;
import java.util.ArrayList;

public class IncCell
  implements TextElementArray
{
  private ArrayList chunks = new ArrayList();
  private PdfPCell cell = new PdfPCell((Phrase)null);

  public IncCell(String paramString, ChainedProperties paramChainedProperties)
  {
    String str = paramChainedProperties.getProperty("colspan");
    if (str != null)
      this.cell.setColspan(Integer.parseInt(str));
    str = paramChainedProperties.getProperty("align");
    if (paramString.equals("th"))
      this.cell.setHorizontalAlignment(1);
    if (str != null)
      if ("center".equalsIgnoreCase(str))
        this.cell.setHorizontalAlignment(1);
      else if ("right".equalsIgnoreCase(str))
        this.cell.setHorizontalAlignment(2);
      else if ("left".equalsIgnoreCase(str))
        this.cell.setHorizontalAlignment(0);
      else if ("justify".equalsIgnoreCase(str))
        this.cell.setHorizontalAlignment(3);
    str = paramChainedProperties.getProperty("valign");
    this.cell.setVerticalAlignment(5);
    if (str != null)
      if ("top".equalsIgnoreCase(str))
        this.cell.setVerticalAlignment(4);
      else if ("bottom".equalsIgnoreCase(str))
        this.cell.setVerticalAlignment(6);
    str = paramChainedProperties.getProperty("border");
    float f = 0.0F;
    if (str != null)
      f = Float.parseFloat(str);
    this.cell.setBorderWidth(f);
    str = paramChainedProperties.getProperty("cellpadding");
    if (str != null)
      this.cell.setPadding(Float.parseFloat(str));
    this.cell.setUseDescender(true);
    str = paramChainedProperties.getProperty("bgcolor");
    this.cell.setBackgroundColor(Markup.decodeColor(str));
  }

  public boolean add(Object paramObject)
  {
    if (!(paramObject instanceof Element))
      return false;
    this.cell.addElement((Element)paramObject);
    return true;
  }

  public ArrayList getChunks()
  {
    return this.chunks;
  }

  public boolean process(ElementListener paramElementListener)
  {
    return true;
  }

  public int type()
  {
    return 30;
  }

  public PdfPCell getCell()
  {
    return this.cell;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.IncCell
 * JD-Core Version:    0.6.0
 */