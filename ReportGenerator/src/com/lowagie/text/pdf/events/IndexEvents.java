package com.lowagie.text.pdf.events;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndexEvents extends PdfPageEventHelper
{
  private Map indextag = new TreeMap();
  private long indexcounter = 0L;
  private List indexentry = new ArrayList();
  private Comparator comparator = new Comparator()
  {
    public int compare(Object paramObject1, Object paramObject2)
    {
      IndexEvents.Entry localEntry1 = (IndexEvents.Entry)paramObject1;
      IndexEvents.Entry localEntry2 = (IndexEvents.Entry)paramObject2;
      int i = 0;
      if ((localEntry1.getIn1() != null) && (localEntry2.getIn1() != null) && ((i = localEntry1.getIn1().compareToIgnoreCase(localEntry2.getIn1())) == 0) && (localEntry1.getIn2() != null) && (localEntry2.getIn2() != null) && ((i = localEntry1.getIn2().compareToIgnoreCase(localEntry2.getIn2())) == 0) && (localEntry1.getIn3() != null) && (localEntry2.getIn3() != null))
        i = localEntry1.getIn3().compareToIgnoreCase(localEntry2.getIn3());
      return i;
    }
  };

  public void onGenericTag(PdfWriter paramPdfWriter, Document paramDocument, Rectangle paramRectangle, String paramString)
  {
    this.indextag.put(paramString, new Integer(paramPdfWriter.getPageNumber()));
  }

  public Chunk create(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Chunk localChunk = new Chunk(paramString1);
    String str = "idx_" + this.indexcounter++;
    localChunk.setGenericTag(str);
    localChunk.setLocalDestination(str);
    Entry localEntry = new Entry(paramString2, paramString3, paramString4, str);
    this.indexentry.add(localEntry);
    return localChunk;
  }

  public Chunk create(String paramString1, String paramString2)
  {
    return create(paramString1, paramString2, "", "");
  }

  public Chunk create(String paramString1, String paramString2, String paramString3)
  {
    return create(paramString1, paramString2, paramString3, "");
  }

  public void create(Chunk paramChunk, String paramString1, String paramString2, String paramString3)
  {
    String str = "idx_" + this.indexcounter++;
    paramChunk.setGenericTag(str);
    paramChunk.setLocalDestination(str);
    Entry localEntry = new Entry(paramString1, paramString2, paramString3, str);
    this.indexentry.add(localEntry);
  }

  public void create(Chunk paramChunk, String paramString)
  {
    create(paramChunk, paramString, "", "");
  }

  public void create(Chunk paramChunk, String paramString1, String paramString2)
  {
    create(paramChunk, paramString1, paramString2, "");
  }

  public void setComparator(Comparator paramComparator)
  {
    this.comparator = paramComparator;
  }

  public List getSortedEntries()
  {
    HashMap localHashMap = new HashMap();
    for (int i = 0; i < this.indexentry.size(); i++)
    {
      Entry localEntry1 = (Entry)this.indexentry.get(i);
      String str = localEntry1.getKey();
      Entry localEntry2 = (Entry)localHashMap.get(str);
      if (localEntry2 != null)
      {
        localEntry2.addPageNumberAndTag(localEntry1.getPageNumber(), localEntry1.getTag());
      }
      else
      {
        localEntry1.addPageNumberAndTag(localEntry1.getPageNumber(), localEntry1.getTag());
        localHashMap.put(str, localEntry1);
      }
    }
    ArrayList localArrayList = new ArrayList(localHashMap.values());
    Collections.sort(localArrayList, this.comparator);
    return localArrayList;
  }

  public class Entry
  {
    private String in1;
    private String in2;
    private String in3;
    private String tag;
    private List pagenumbers = new ArrayList();
    private List tags = new ArrayList();

    public Entry(String paramString1, String paramString2, String paramString3, String arg5)
    {
      this.in1 = paramString1;
      this.in2 = paramString2;
      this.in3 = paramString3;
      Object localObject;
      this.tag = localObject;
    }

    public String getIn1()
    {
      return this.in1;
    }

    public String getIn2()
    {
      return this.in2;
    }

    public String getIn3()
    {
      return this.in3;
    }

    public String getTag()
    {
      return this.tag;
    }

    public int getPageNumber()
    {
      int i = -1;
      Integer localInteger = (Integer)IndexEvents.this.indextag.get(this.tag);
      if (localInteger != null)
        i = localInteger.intValue();
      return i;
    }

    public void addPageNumberAndTag(int paramInt, String paramString)
    {
      this.pagenumbers.add(new Integer(paramInt));
      this.tags.add(paramString);
    }

    public String getKey()
    {
      return this.in1 + "!" + this.in2 + "!" + this.in3;
    }

    public List getPagenumbers()
    {
      return this.pagenumbers;
    }

    public List getTags()
    {
      return this.tags;
    }

    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(this.in1).append(' ');
      localStringBuffer.append(this.in2).append(' ');
      localStringBuffer.append(this.in3).append(' ');
      for (int i = 0; i < this.pagenumbers.size(); i++)
        localStringBuffer.append(this.pagenumbers.get(i)).append(' ');
      return localStringBuffer.toString();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.events.IndexEvents
 * JD-Core Version:    0.6.0
 */