package com.lowagie.text.html;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Header;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Row;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.OutputStreamCounter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;

public class HtmlWriter extends DocWriter
{
  public static final byte[] BEGINCOMMENT = getISOBytes("<!-- ");
  public static final byte[] ENDCOMMENT = getISOBytes(" -->");
  public static final String NBSP = "&nbsp;";
  protected Stack currentfont = new Stack();
  protected Font standardfont = new Font();
  protected String imagepath = null;
  protected int pageN = 0;
  protected HeaderFooter header = null;
  protected HeaderFooter footer = null;
  protected Properties markup = new Properties();

  protected HtmlWriter(Document paramDocument, OutputStream paramOutputStream)
  {
    super(paramDocument, paramOutputStream);
    this.document.addDocListener(this);
    this.pageN = this.document.getPageNumber();
    try
    {
      paramOutputStream.write(60);
      paramOutputStream.write(getISOBytes("html"));
      paramOutputStream.write(62);
      paramOutputStream.write(10);
      paramOutputStream.write(9);
      paramOutputStream.write(60);
      paramOutputStream.write(getISOBytes("head"));
      paramOutputStream.write(62);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public static HtmlWriter getInstance(Document paramDocument, OutputStream paramOutputStream)
  {
    return new HtmlWriter(paramDocument, paramOutputStream);
  }

  public boolean newPage()
  {
    try
    {
      writeStart("div");
      write(" ");
      write("style");
      write("=\"");
      writeCssProperty("page-break-before", "always");
      write("\" /");
      this.os.write(62);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    return true;
  }

  public boolean add(Element paramElement)
    throws DocumentException
  {
    if (this.pause)
      return false;
    if ((this.open) && (!paramElement.isContent()))
      throw new DocumentException("The document is open; you can only add Elements with content.");
    try
    {
      switch (paramElement.type())
      {
      case 0:
        try
        {
          Header localHeader = (Header)paramElement;
          if ("stylesheet".equals(localHeader.getName()))
            writeLink(localHeader);
          else if ("JavaScript".equals(localHeader.getName()))
            writeJavaScript(localHeader);
          else
            writeHeader(localHeader);
        }
        catch (ClassCastException localClassCastException)
        {
        }
        return true;
      case 2:
      case 3:
      case 4:
        Meta localMeta = (Meta)paramElement;
        writeHeader(localMeta);
        return true;
      case 1:
        addTabs(2);
        writeStart("title");
        this.os.write(62);
        addTabs(3);
        write(HtmlEncoder.encode(((Meta)paramElement).getContent()));
        addTabs(2);
        writeEnd("title");
        return true;
      case 7:
        writeComment("Creator: " + HtmlEncoder.encode(((Meta)paramElement).getContent()));
        return true;
      case 5:
        writeComment("Producer: " + HtmlEncoder.encode(((Meta)paramElement).getContent()));
        return true;
      case 6:
        writeComment("Creationdate: " + HtmlEncoder.encode(((Meta)paramElement).getContent()));
        return true;
      case 50:
        if ((paramElement instanceof MarkedSection))
        {
          localObject = (MarkedSection)paramElement;
          addTabs(1);
          writeStart("div");
          writeMarkupAttributes(((MarkedSection)localObject).getMarkupAttributes());
          this.os.write(62);
          MarkedObject localMarkedObject = ((MarkedSection)paramElement).getTitle();
          if (localMarkedObject != null)
          {
            this.markup = localMarkedObject.getMarkupAttributes();
            localMarkedObject.process(this);
          }
          ((MarkedSection)localObject).process(this);
          writeEnd("div");
          return true;
        }
        Object localObject = (MarkedObject)paramElement;
        this.markup = ((MarkedObject)localObject).getMarkupAttributes();
        return ((MarkedObject)localObject).process(this);
      }
      write(paramElement, 2);
      return true;
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  public void open()
  {
    super.open();
    try
    {
      writeComment(Document.getVersion());
      writeComment("CreationDate: " + new Date().toString());
      addTabs(1);
      writeEnd("head");
      addTabs(1);
      writeStart("body");
      if (this.document.leftMargin() > 0.0F)
        write("leftmargin", String.valueOf(this.document.leftMargin()));
      if (this.document.rightMargin() > 0.0F)
        write("rightmargin", String.valueOf(this.document.rightMargin()));
      if (this.document.topMargin() > 0.0F)
        write("topmargin", String.valueOf(this.document.topMargin()));
      if (this.document.bottomMargin() > 0.0F)
        write("bottommargin", String.valueOf(this.document.bottomMargin()));
      if (this.pageSize.getBackgroundColor() != null)
        write("bgcolor", HtmlEncoder.encode(this.pageSize.getBackgroundColor()));
      if (this.document.getJavaScript_onLoad() != null)
        write("onLoad", HtmlEncoder.encode(this.document.getJavaScript_onLoad()));
      if (this.document.getJavaScript_onUnLoad() != null)
        write("onUnLoad", HtmlEncoder.encode(this.document.getJavaScript_onUnLoad()));
      if (this.document.getHtmlStyleClass() != null)
        write("class", this.document.getHtmlStyleClass());
      this.os.write(62);
      initHeader();
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void close()
  {
    try
    {
      initFooter();
      addTabs(1);
      writeEnd("body");
      this.os.write(10);
      writeEnd("html");
      super.close();
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  protected void initHeader()
  {
    if (this.header != null)
      try
      {
        add(this.header.paragraph());
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
  }

  protected void initFooter()
  {
    if (this.footer != null)
      try
      {
        this.footer.setPageNumber(this.pageN + 1);
        add(this.footer.paragraph());
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
  }

  protected void writeHeader(Meta paramMeta)
    throws IOException
  {
    addTabs(2);
    writeStart("meta");
    switch (paramMeta.type())
    {
    case 0:
      write("name", ((Header)paramMeta).getName());
      break;
    case 2:
      write("name", "subject");
      break;
    case 3:
      write("name", "keywords");
      break;
    case 4:
      write("name", "author");
    case 1:
    }
    write("content", HtmlEncoder.encode(paramMeta.getContent()));
    writeEnd();
  }

  protected void writeLink(Header paramHeader)
    throws IOException
  {
    addTabs(2);
    writeStart("link");
    write("rel", paramHeader.getName());
    write("type", "text/css");
    write("href", paramHeader.getContent());
    writeEnd();
  }

  protected void writeJavaScript(Header paramHeader)
    throws IOException
  {
    addTabs(2);
    writeStart("script");
    write("language", "JavaScript");
    if (this.markup.size() > 0)
    {
      writeMarkupAttributes(this.markup);
      this.os.write(62);
      writeEnd("script");
    }
    else
    {
      write("type", "text/javascript");
      this.os.write(62);
      addTabs(2);
      write(new String(BEGINCOMMENT) + "\n");
      write(paramHeader.getContent());
      addTabs(2);
      write("//" + new String(ENDCOMMENT));
      addTabs(2);
      writeEnd("script");
    }
  }

  protected void writeComment(String paramString)
    throws IOException
  {
    addTabs(2);
    this.os.write(BEGINCOMMENT);
    write(paramString);
    this.os.write(ENDCOMMENT);
  }

  public void setStandardFont(Font paramFont)
  {
    this.standardfont = paramFont;
  }

  public boolean isOtherFont(Font paramFont)
  {
    try
    {
      Font localFont = (Font)this.currentfont.peek();
      return localFont.compareTo(paramFont) != 0;
    }
    catch (EmptyStackException localEmptyStackException)
    {
      if (this.standardfont.compareTo(paramFont) == 0)
        return false;
    }
    return true;
  }

  public void setImagepath(String paramString)
  {
    this.imagepath = paramString;
  }

  public void resetImagepath()
  {
    this.imagepath = null;
  }

  public void setHeader(HeaderFooter paramHeaderFooter)
  {
    this.header = paramHeaderFooter;
  }

  public void setFooter(HeaderFooter paramHeaderFooter)
  {
    this.footer = paramHeaderFooter;
  }

  public boolean add(String paramString)
  {
    if (this.pause)
      return false;
    try
    {
      write(paramString);
      return true;
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  protected void write(Element paramElement, int paramInt)
    throws IOException
  {
    Properties localProperties = null;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    String str;
    switch (paramElement.type())
    {
    case 50:
      try
      {
        add(paramElement);
      }
      catch (DocumentException localDocumentException)
      {
        localDocumentException.printStackTrace();
      }
      return;
    case 10:
      localObject1 = (Chunk)paramElement;
      localObject2 = ((Chunk)localObject1).getImage();
      if (localObject2 != null)
      {
        write((Element)localObject2, paramInt);
        return;
      }
      if (((Chunk)localObject1).isEmpty())
        return;
      localObject3 = ((Chunk)localObject1).getAttributes();
      if ((localObject3 != null) && (((HashMap)localObject3).get("NEWPAGE") != null))
        return;
      int j = (isOtherFont(((Chunk)localObject1).getFont())) || (this.markup.size() > 0) ? 1 : 0;
      if (j != 0)
      {
        addTabs(paramInt);
        writeStart("span");
        if (isOtherFont(((Chunk)localObject1).getFont()))
          write(((Chunk)localObject1).getFont(), null);
        writeMarkupAttributes(this.markup);
        this.os.write(62);
      }
      if ((localObject3 != null) && (((HashMap)localObject3).get("SUBSUPSCRIPT") != null))
      {
        if (((Float)((HashMap)localObject3).get("SUBSUPSCRIPT")).floatValue() > 0.0F)
          writeStart("sup");
        else
          writeStart("sub");
        this.os.write(62);
      }
      write(HtmlEncoder.encode(((Chunk)localObject1).getContent()));
      if ((localObject3 != null) && (((HashMap)localObject3).get("SUBSUPSCRIPT") != null))
      {
        this.os.write(60);
        this.os.write(47);
        if (((Float)((HashMap)localObject3).get("SUBSUPSCRIPT")).floatValue() > 0.0F)
          write("sup");
        else
          write("sub");
        this.os.write(62);
      }
      if (j != 0)
        writeEnd("span");
      return;
    case 11:
      localObject1 = (Phrase)paramElement;
      localProperties = new Properties();
      if (((Phrase)localObject1).hasLeading())
        localProperties.setProperty("line-height", ((Phrase)localObject1).getLeading() + "pt");
      addTabs(paramInt);
      writeStart("span");
      writeMarkupAttributes(this.markup);
      write(((Phrase)localObject1).getFont(), localProperties);
      this.os.write(62);
      this.currentfont.push(((Phrase)localObject1).getFont());
      localObject2 = ((Phrase)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
        write((Element)((Iterator)localObject2).next(), paramInt + 1);
      addTabs(paramInt);
      writeEnd("span");
      this.currentfont.pop();
      return;
    case 17:
      localObject1 = (Anchor)paramElement;
      localProperties = new Properties();
      if (((Anchor)localObject1).hasLeading())
        localProperties.setProperty("line-height", ((Anchor)localObject1).getLeading() + "pt");
      addTabs(paramInt);
      writeStart("a");
      if (((Anchor)localObject1).getName() != null)
        write("name", ((Anchor)localObject1).getName());
      if (((Anchor)localObject1).getReference() != null)
        write("href", ((Anchor)localObject1).getReference());
      writeMarkupAttributes(this.markup);
      write(((Anchor)localObject1).getFont(), localProperties);
      this.os.write(62);
      this.currentfont.push(((Anchor)localObject1).getFont());
      localObject2 = ((Anchor)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
        write((Element)((Iterator)localObject2).next(), paramInt + 1);
      addTabs(paramInt);
      writeEnd("a");
      this.currentfont.pop();
      return;
    case 12:
      localObject1 = (Paragraph)paramElement;
      localProperties = new Properties();
      if (((Paragraph)localObject1).hasLeading())
        localProperties.setProperty("line-height", ((Paragraph)localObject1).getTotalLeading() + "pt");
      addTabs(paramInt);
      writeStart("div");
      writeMarkupAttributes(this.markup);
      localObject2 = HtmlEncoder.getAlignment(((Paragraph)localObject1).getAlignment());
      if (!"".equals(localObject2))
        write("align", (String)localObject2);
      write(((Paragraph)localObject1).getFont(), localProperties);
      this.os.write(62);
      this.currentfont.push(((Paragraph)localObject1).getFont());
      localObject3 = ((Paragraph)localObject1).iterator();
      while (((Iterator)localObject3).hasNext())
        write((Element)((Iterator)localObject3).next(), paramInt + 1);
      addTabs(paramInt);
      writeEnd("div");
      this.currentfont.pop();
      return;
    case 13:
    case 16:
      writeSection((Section)paramElement, paramInt);
      return;
    case 14:
      localObject1 = (List)paramElement;
      addTabs(paramInt);
      if (((List)localObject1).isNumbered())
        writeStart("ol");
      else
        writeStart("ul");
      writeMarkupAttributes(this.markup);
      this.os.write(62);
      localObject2 = ((List)localObject1).getItems().iterator();
      while (((Iterator)localObject2).hasNext())
        write((Element)((Iterator)localObject2).next(), paramInt + 1);
      addTabs(paramInt);
      if (((List)localObject1).isNumbered())
        writeEnd("ol");
      else
        writeEnd("ul");
      return;
    case 15:
      localObject1 = (ListItem)paramElement;
      localProperties = new Properties();
      if (((ListItem)localObject1).hasLeading())
        localProperties.setProperty("line-height", ((ListItem)localObject1).getTotalLeading() + "pt");
      addTabs(paramInt);
      writeStart("li");
      writeMarkupAttributes(this.markup);
      write(((ListItem)localObject1).getFont(), localProperties);
      this.os.write(62);
      this.currentfont.push(((ListItem)localObject1).getFont());
      localObject2 = ((ListItem)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
        write((Element)((Iterator)localObject2).next(), paramInt + 1);
      addTabs(paramInt);
      writeEnd("li");
      this.currentfont.pop();
      return;
    case 20:
      localObject1 = (Cell)paramElement;
      addTabs(paramInt);
      if (((Cell)localObject1).isHeader())
        writeStart("th");
      else
        writeStart("td");
      writeMarkupAttributes(this.markup);
      if (((Cell)localObject1).getBorderWidth() != -1.0F)
        write("border", String.valueOf(((Cell)localObject1).getBorderWidth()));
      if (((Cell)localObject1).getBorderColor() != null)
        write("bordercolor", HtmlEncoder.encode(((Cell)localObject1).getBorderColor()));
      if (((Cell)localObject1).getBackgroundColor() != null)
        write("bgcolor", HtmlEncoder.encode(((Cell)localObject1).getBackgroundColor()));
      localObject2 = HtmlEncoder.getAlignment(((Cell)localObject1).getHorizontalAlignment());
      if (!"".equals(localObject2))
        write("align", (String)localObject2);
      localObject2 = HtmlEncoder.getAlignment(((Cell)localObject1).getVerticalAlignment());
      if (!"".equals(localObject2))
        write("valign", (String)localObject2);
      if (((Cell)localObject1).getWidthAsString() != null)
        write("width", ((Cell)localObject1).getWidthAsString());
      if (((Cell)localObject1).getColspan() != 1)
        write("colspan", String.valueOf(((Cell)localObject1).getColspan()));
      if (((Cell)localObject1).getRowspan() != 1)
        write("rowspan", String.valueOf(((Cell)localObject1).getRowspan()));
      if (((Cell)localObject1).getMaxLines() == 1)
        write("style", "white-space: nowrap;");
      this.os.write(62);
      if (((Cell)localObject1).isEmpty())
      {
        write("&nbsp;");
      }
      else
      {
        localObject3 = ((Cell)localObject1).getElements();
        while (((Iterator)localObject3).hasNext())
          write((Element)((Iterator)localObject3).next(), paramInt + 1);
      }
      addTabs(paramInt);
      if (((Cell)localObject1).isHeader())
        writeEnd("th");
      else
        writeEnd("td");
      return;
    case 21:
      localObject1 = (Row)paramElement;
      addTabs(paramInt);
      writeStart("tr");
      writeMarkupAttributes(this.markup);
      this.os.write(62);
      for (int i = 0; i < ((Row)localObject1).getColumns(); i++)
      {
        if ((localObject2 = (Element)((Row)localObject1).getCell(i)) == null)
          continue;
        write((Element)localObject2, paramInt + 1);
      }
      addTabs(paramInt);
      writeEnd("tr");
      return;
    case 22:
      try
      {
        localObject1 = (Table)paramElement;
      }
      catch (ClassCastException localClassCastException)
      {
        try
        {
          localObject1 = ((SimpleTable)paramElement).createTable();
        }
        catch (BadElementException localBadElementException)
        {
          throw new ExceptionConverter(localBadElementException);
        }
      }
      ((Table)localObject1).complete();
      addTabs(paramInt);
      writeStart("table");
      writeMarkupAttributes(this.markup);
      this.os.write(32);
      write("width");
      this.os.write(61);
      this.os.write(34);
      write(String.valueOf(((Table)localObject1).getWidth()));
      if (!((Table)localObject1).isLocked())
        write("%");
      this.os.write(34);
      str = HtmlEncoder.getAlignment(((Table)localObject1).getAlignment());
      if (!"".equals(str))
        write("align", str);
      write("cellpadding", String.valueOf(((Table)localObject1).getPadding()));
      write("cellspacing", String.valueOf(((Table)localObject1).getSpacing()));
      if (((Table)localObject1).getBorderWidth() != -1.0F)
        write("border", String.valueOf(((Table)localObject1).getBorderWidth()));
      if (((Table)localObject1).getBorderColor() != null)
        write("bordercolor", HtmlEncoder.encode(((Table)localObject1).getBorderColor()));
      if (((Table)localObject1).getBackgroundColor() != null)
        write("bgcolor", HtmlEncoder.encode(((Table)localObject1).getBackgroundColor()));
      this.os.write(62);
      Iterator localIterator = ((Table)localObject1).iterator();
      while (localIterator.hasNext())
      {
        Row localRow = (Row)localIterator.next();
        write(localRow, paramInt + 1);
      }
      addTabs(paramInt);
      writeEnd("table");
      return;
    case 29:
      localObject1 = (Annotation)paramElement;
      writeComment(((Annotation)localObject1).title() + ": " + ((Annotation)localObject1).content());
      return;
    case 32:
    case 33:
    case 34:
    case 35:
      localObject1 = (Image)paramElement;
      if (((Image)localObject1).getUrl() == null)
        return;
      addTabs(paramInt);
      writeStart("img");
      str = ((Image)localObject1).getUrl().toString();
      if (this.imagepath != null)
        if (str.indexOf('/') > 0)
          str = this.imagepath + str.substring(str.lastIndexOf('/') + 1);
        else
          str = this.imagepath + str;
      write("src", str);
      if ((((Image)localObject1).getAlignment() & 0x2) > 0)
        write("align", "Right");
      else if ((((Image)localObject1).getAlignment() & 0x1) > 0)
        write("align", "Middle");
      else
        write("align", "Left");
      if (((Image)localObject1).getAlt() != null)
        write("alt", ((Image)localObject1).getAlt());
      write("width", String.valueOf(((Image)localObject1).getScaledWidth()));
      write("height", String.valueOf(((Image)localObject1).getScaledHeight()));
      writeMarkupAttributes(this.markup);
      writeEnd();
      return;
    case 18:
    case 19:
    case 23:
    case 24:
    case 25:
    case 26:
    case 27:
    case 28:
    case 30:
    case 31:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 44:
    case 45:
    case 46:
    case 47:
    case 48:
    case 49:
    }
  }

  protected void writeSection(Section paramSection, int paramInt)
    throws IOException
  {
    if (paramSection.getTitle() != null)
    {
      int i = paramSection.getDepth() - 1;
      if (i > 5)
        i = 5;
      Properties localProperties = new Properties();
      if (paramSection.getTitle().hasLeading())
        localProperties.setProperty("line-height", paramSection.getTitle().getTotalLeading() + "pt");
      addTabs(paramInt);
      writeStart(HtmlTags.H[i]);
      write(paramSection.getTitle().getFont(), localProperties);
      String str = HtmlEncoder.getAlignment(paramSection.getTitle().getAlignment());
      if (!"".equals(str))
        write("align", str);
      writeMarkupAttributes(this.markup);
      this.os.write(62);
      this.currentfont.push(paramSection.getTitle().getFont());
      Iterator localIterator2 = paramSection.getTitle().iterator();
      while (localIterator2.hasNext())
        write((Element)localIterator2.next(), paramInt + 1);
      addTabs(paramInt);
      writeEnd(HtmlTags.H[i]);
      this.currentfont.pop();
    }
    Iterator localIterator1 = paramSection.iterator();
    while (localIterator1.hasNext())
      write((Element)localIterator1.next(), paramInt);
  }

  protected void write(Font paramFont, Properties paramProperties)
    throws IOException
  {
    if ((paramFont == null) || (!isOtherFont(paramFont)))
      return;
    write(" ");
    write("style");
    write("=\"");
    Object localObject;
    if (paramProperties != null)
    {
      localObject = paramProperties.propertyNames();
      while (((Enumeration)localObject).hasMoreElements())
      {
        String str1 = (String)((Enumeration)localObject).nextElement();
        writeCssProperty(str1, paramProperties.getProperty(str1));
      }
    }
    if (isOtherFont(paramFont))
    {
      writeCssProperty("font-family", paramFont.getFamilyname());
      if (paramFont.getSize() != -1.0F)
        writeCssProperty("font-size", paramFont.getSize() + "pt");
      if (paramFont.getColor() != null)
        writeCssProperty("color", HtmlEncoder.encode(paramFont.getColor()));
      int i = paramFont.getStyle();
      localObject = paramFont.getBaseFont();
      if (localObject != null)
      {
        String str2 = ((BaseFont)localObject).getPostscriptFontName().toLowerCase();
        if (str2.indexOf("bold") >= 0)
        {
          if (i == -1)
            i = 0;
          i |= 1;
        }
        if ((str2.indexOf("italic") >= 0) || (str2.indexOf("oblique") >= 0))
        {
          if (i == -1)
            i = 0;
          i |= 2;
        }
      }
      if ((i != -1) && (i != 0))
      {
        switch (i & 0x3)
        {
        case 1:
          writeCssProperty("font-weight", "bold");
          break;
        case 2:
          writeCssProperty("font-style", "italic");
          break;
        case 3:
          writeCssProperty("font-weight", "bold");
          writeCssProperty("font-style", "italic");
        }
        if ((i & 0x4) > 0)
          writeCssProperty("text-decoration", "underline");
        if ((i & 0x8) > 0)
          writeCssProperty("text-decoration", "line-through");
      }
    }
    write("\"");
  }

  protected void writeCssProperty(String paramString1, String paramString2)
    throws IOException
  {
    write(paramString1 + ": " + paramString2 + "; ");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.HtmlWriter
 * JD-Core Version:    0.6.0
 */