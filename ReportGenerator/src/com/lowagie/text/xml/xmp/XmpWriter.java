package com.lowagie.text.xml.xmp;

import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class XmpWriter
{
  public static final String UTF8 = "UTF-8";
  public static final String UTF16 = "UTF-16";
  public static final String UTF16BE = "UTF-16BE";
  public static final String UTF16LE = "UTF-16LE";
  public static final String EXTRASPACE = "                                                                                                   \n";
  protected int extraSpace;
  protected OutputStreamWriter writer;
  protected String about;
  public static final String XPACKET_PI_BEGIN = "<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n";
  public static final String XPACKET_PI_END_W = "<?xpacket end=\"w\"?>";
  public static final String XPACKET_PI_END_R = "<?xpacket end=\"r\"?>";
  protected char end = 'w';

  public XmpWriter(OutputStream paramOutputStream, String paramString, int paramInt)
    throws IOException
  {
    this.extraSpace = paramInt;
    this.writer = new OutputStreamWriter(paramOutputStream, paramString);
    this.writer.write("<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n");
    this.writer.write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n");
    this.writer.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
    this.about = "";
  }

  public XmpWriter(OutputStream paramOutputStream)
    throws IOException
  {
    this(paramOutputStream, "UTF-8", 20);
  }

  public void setReadOnly()
  {
    this.end = 'r';
  }

  public void setAbout(String paramString)
  {
    this.about = paramString;
  }

  public void addRdfDescription(String paramString1, String paramString2)
    throws IOException
  {
    this.writer.write("<rdf:Description rdf:about=\"");
    this.writer.write(this.about);
    this.writer.write("\" ");
    this.writer.write(paramString1);
    this.writer.write(">");
    this.writer.write(paramString2);
    this.writer.write("</rdf:Description>\n");
  }

  public void addRdfDescription(XmpSchema paramXmpSchema)
    throws IOException
  {
    this.writer.write("<rdf:Description rdf:about=\"");
    this.writer.write(this.about);
    this.writer.write("\" ");
    this.writer.write(paramXmpSchema.getXmlns());
    this.writer.write(">");
    this.writer.write(paramXmpSchema.toString());
    this.writer.write("</rdf:Description>\n");
  }

  public void close()
    throws IOException
  {
    this.writer.write("</rdf:RDF>");
    this.writer.write("</x:xmpmeta>\n");
    for (int i = 0; i < this.extraSpace; i++)
      this.writer.write("                                                                                                   \n");
    this.writer.write(this.end == 'r' ? "<?xpacket end=\"r\"?>" : "<?xpacket end=\"w\"?>");
    this.writer.flush();
    this.writer.close();
  }

  public XmpWriter(OutputStream paramOutputStream, PdfDictionary paramPdfDictionary, int paramInt)
    throws IOException
  {
    this(paramOutputStream);
    if (paramPdfDictionary != null)
    {
      DublinCoreSchema localDublinCoreSchema = new DublinCoreSchema();
      PdfSchema localPdfSchema = new PdfSchema();
      XmpBasicSchema localXmpBasicSchema = new XmpBasicSchema();
      Object localObject = paramPdfDictionary.getKeys().iterator();
      while (((Iterator)localObject).hasNext())
      {
        PdfName localPdfName = (PdfName)((Iterator)localObject).next();
        PdfObject localPdfObject = paramPdfDictionary.get(localPdfName);
        if (localPdfObject == null)
          continue;
        if (PdfName.TITLE.equals(localPdfName))
          localDublinCoreSchema.addTitle(((PdfString)localPdfObject).toUnicodeString());
        if (PdfName.AUTHOR.equals(localPdfName))
          localDublinCoreSchema.addAuthor(((PdfString)localPdfObject).toUnicodeString());
        if (PdfName.SUBJECT.equals(localPdfName))
        {
          localDublinCoreSchema.addSubject(((PdfString)localPdfObject).toUnicodeString());
          localDublinCoreSchema.addDescription(((PdfString)localPdfObject).toUnicodeString());
        }
        if (PdfName.KEYWORDS.equals(localPdfName))
          localPdfSchema.addKeywords(((PdfString)localPdfObject).toUnicodeString());
        if (PdfName.CREATOR.equals(localPdfName))
          localXmpBasicSchema.addCreatorTool(((PdfString)localPdfObject).toUnicodeString());
        if (PdfName.PRODUCER.equals(localPdfName))
          localPdfSchema.addProducer(((PdfString)localPdfObject).toUnicodeString());
        if (PdfName.CREATIONDATE.equals(localPdfName))
          localXmpBasicSchema.addCreateDate(((PdfDate)localPdfObject).getW3CDate());
        if (!PdfName.MODDATE.equals(localPdfName))
          continue;
        localXmpBasicSchema.addModDate(((PdfDate)localPdfObject).getW3CDate());
      }
      if (localDublinCoreSchema.size() > 0)
        addRdfDescription(localDublinCoreSchema);
      if (localPdfSchema.size() > 0)
        addRdfDescription(localPdfSchema);
      if (localXmpBasicSchema.size() > 0)
        addRdfDescription(localXmpBasicSchema);
      if ((paramInt == 3) || (paramInt == 4))
      {
        localObject = new PdfA1Schema();
        if (paramInt == 3)
          ((PdfA1Schema)localObject).addConformance("A");
        else
          ((PdfA1Schema)localObject).addConformance("B");
        addRdfDescription((XmpSchema)localObject);
      }
    }
  }

  public XmpWriter(OutputStream paramOutputStream, Map paramMap)
    throws IOException
  {
    this(paramOutputStream);
    if (paramMap != null)
    {
      DublinCoreSchema localDublinCoreSchema = new DublinCoreSchema();
      PdfSchema localPdfSchema = new PdfSchema();
      XmpBasicSchema localXmpBasicSchema = new XmpBasicSchema();
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str1 = (String)localEntry.getKey();
        String str2 = (String)localEntry.getValue();
        if (str2 == null)
          continue;
        if ("Title".equals(str1))
          localDublinCoreSchema.addTitle(str2);
        if ("Author".equals(str1))
          localDublinCoreSchema.addAuthor(str2);
        if ("Subject".equals(str1))
        {
          localDublinCoreSchema.addSubject(str2);
          localDublinCoreSchema.addDescription(str2);
        }
        if ("Keywords".equals(str1))
          localPdfSchema.addKeywords(str2);
        if ("Creator".equals(str1))
          localXmpBasicSchema.addCreatorTool(str2);
        if ("Producer".equals(str1))
          localPdfSchema.addProducer(str2);
        if ("CreationDate".equals(str1))
          localXmpBasicSchema.addCreateDate(PdfDate.getW3CDate(str2));
        if (!"ModDate".equals(str1))
          continue;
        localXmpBasicSchema.addModDate(PdfDate.getW3CDate(str2));
      }
      if (localDublinCoreSchema.size() > 0)
        addRdfDescription(localDublinCoreSchema);
      if (localPdfSchema.size() > 0)
        addRdfDescription(localPdfSchema);
      if (localXmpBasicSchema.size() > 0)
        addRdfDescription(localXmpBasicSchema);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.XmpWriter
 * JD-Core Version:    0.6.0
 */