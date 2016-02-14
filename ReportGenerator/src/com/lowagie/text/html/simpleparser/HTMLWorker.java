package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

public class HTMLWorker
  implements SimpleXMLDocHandler, DocListener
{
  protected ArrayList objectList;
  protected DocListener document;
  private Paragraph currentParagraph;
  private ChainedProperties cprops = new ChainedProperties();
  private Stack stack = new Stack();
  private boolean pendingTR = false;
  private boolean pendingTD = false;
  private boolean pendingLI = false;
  private StyleSheet style = new StyleSheet();
  private boolean isPRE = false;
  private Stack tableState = new Stack();
  private boolean skipText = false;
  private HashMap interfaceProps;
  private FactoryProperties factoryProperties = new FactoryProperties();
  public static final String tagsSupportedString = "ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike h1 h2 h3 h4 h5 h6 img hr";
  public static final HashMap tagsSupported = new HashMap();

  public HTMLWorker(DocListener paramDocListener)
  {
    this.document = paramDocListener;
  }

  public void setStyleSheet(StyleSheet paramStyleSheet)
  {
    this.style = paramStyleSheet;
  }

  public StyleSheet getStyleSheet()
  {
    return this.style;
  }

  public void setInterfaceProps(HashMap paramHashMap)
  {
    this.interfaceProps = paramHashMap;
    FontFactoryImp localFontFactoryImp = null;
    if (paramHashMap != null)
      localFontFactoryImp = (FontFactoryImp)paramHashMap.get("font_factory");
    if (localFontFactoryImp != null)
      this.factoryProperties.setFontImp(localFontFactoryImp);
  }

  public HashMap getInterfaceProps()
  {
    return this.interfaceProps;
  }

  public void parse(Reader paramReader)
    throws IOException
  {
    SimpleXMLParser.parse(this, null, paramReader, true);
  }

  public static ArrayList parseToList(Reader paramReader, StyleSheet paramStyleSheet)
    throws IOException
  {
    return parseToList(paramReader, paramStyleSheet, null);
  }

  public static ArrayList parseToList(Reader paramReader, StyleSheet paramStyleSheet, HashMap paramHashMap)
    throws IOException
  {
    HTMLWorker localHTMLWorker = new HTMLWorker(null);
    if (paramStyleSheet != null)
      localHTMLWorker.style = paramStyleSheet;
    localHTMLWorker.document = localHTMLWorker;
    localHTMLWorker.setInterfaceProps(paramHashMap);
    localHTMLWorker.objectList = new ArrayList();
    localHTMLWorker.parse(paramReader);
    return localHTMLWorker.objectList;
  }

  public void endDocument()
  {
    try
    {
      for (int i = 0; i < this.stack.size(); i++)
        this.document.add((Element)this.stack.elementAt(i));
      if (this.currentParagraph != null)
        this.document.add(this.currentParagraph);
      this.currentParagraph = null;
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public void startDocument()
  {
    HashMap localHashMap = new HashMap();
    this.style.applyStyle("body", localHashMap);
    this.cprops.addToChain("body", localHashMap);
  }

  public void startElement(String paramString, HashMap paramHashMap)
  {
    if (!tagsSupported.containsKey(paramString))
      return;
    try
    {
      this.style.applyStyle(paramString, paramHashMap);
      String str1 = (String)FactoryProperties.followTags.get(paramString);
      if (str1 != null)
      {
        HashMap localHashMap = new HashMap();
        localHashMap.put(str1, null);
        this.cprops.addToChain(str1, localHashMap);
        return;
      }
      FactoryProperties.insertStyle(paramHashMap, this.cprops);
      if (paramString.equals("a"))
      {
        this.cprops.addToChain(paramString, paramHashMap);
        if (this.currentParagraph == null)
          this.currentParagraph = new Paragraph();
        this.stack.push(this.currentParagraph);
        this.currentParagraph = new Paragraph();
        return;
      }
      if (paramString.equals("br"))
      {
        if (this.currentParagraph == null)
          this.currentParagraph = new Paragraph();
        this.currentParagraph.add(this.factoryProperties.createChunk("\n", this.cprops));
        return;
      }
      Object localObject2;
      Object localObject4;
      String str3;
      float f4;
      if (paramString.equals("hr"))
      {
        int i = 1;
        if (this.currentParagraph == null)
        {
          this.currentParagraph = new Paragraph();
          i = 0;
        }
        if (i != 0)
        {
          int k = this.currentParagraph.getChunks().size();
          if ((k == 0) || (((Chunk)this.currentParagraph.getChunks().get(k - 1)).getContent().endsWith("\n")))
            i = 0;
        }
        localObject2 = (String)paramHashMap.get("align");
        int m = 1;
        if (localObject2 != null)
        {
          if (((String)localObject2).equalsIgnoreCase("left"))
            m = 0;
          if (((String)localObject2).equalsIgnoreCase("right"))
            m = 2;
        }
        localObject4 = (String)paramHashMap.get("width");
        float f1 = 1.0F;
        if (localObject4 != null)
        {
          float f2 = Markup.parseLength((String)localObject4, 12.0F);
          if (f2 > 0.0F)
            f1 = f2;
          if (!((String)localObject4).endsWith("%"))
            f1 = 100.0F;
        }
        str3 = (String)paramHashMap.get("size");
        float f3 = 1.0F;
        if (str3 != null)
        {
          f4 = Markup.parseLength(str3, 12.0F);
          if (f4 > 0.0F)
            f3 = f4;
        }
        if (i != 0)
          this.currentParagraph.add(Chunk.NEWLINE);
        this.currentParagraph.add(new LineSeparator(f3, f1, null, m, this.currentParagraph.getLeading() / 2.0F));
        this.currentParagraph.add(Chunk.NEWLINE);
        return;
      }
      if ((paramString.equals("font")) || (paramString.equals("span")))
      {
        this.cprops.addToChain(paramString, paramHashMap);
        return;
      }
      if (paramString.equals("img"))
      {
        String str2 = (String)paramHashMap.get("src");
        if (str2 == null)
          return;
        this.cprops.addToChain(paramString, paramHashMap);
        localObject2 = null;
        if (this.interfaceProps != null)
        {
          localObject3 = (ImageProvider)this.interfaceProps.get("img_provider");
          if (localObject3 != null)
            localObject2 = ((ImageProvider)localObject3).getImage(str2, paramHashMap, this.cprops, this.document);
          if (localObject2 == null)
          {
            localObject4 = (HashMap)this.interfaceProps.get("img_static");
            if (localObject4 != null)
            {
              localObject5 = (Image)((HashMap)localObject4).get(str2);
              if (localObject5 != null)
                localObject2 = Image.getInstance((Image)localObject5);
            }
            else if (!str2.startsWith("http"))
            {
              localObject5 = (String)this.interfaceProps.get("img_baseurl");
              if (localObject5 != null)
              {
                str2 = (String)localObject5 + str2;
                localObject2 = Image.getInstance(str2);
              }
            }
          }
        }
        if (localObject2 == null)
        {
          if (!str2.startsWith("http"))
          {
            localObject3 = this.cprops.getProperty("image_path");
            if (localObject3 == null)
              localObject3 = "";
            str2 = new File((String)localObject3, str2).getPath();
          }
          localObject2 = Image.getInstance(str2);
        }
        Object localObject3 = (String)paramHashMap.get("align");
        localObject4 = (String)paramHashMap.get("width");
        Object localObject5 = (String)paramHashMap.get("height");
        str3 = this.cprops.getProperty("before");
        String str4 = this.cprops.getProperty("after");
        if (str3 != null)
          ((Image)localObject2).setSpacingBefore(Float.parseFloat(str3));
        if (str4 != null)
          ((Image)localObject2).setSpacingAfter(Float.parseFloat(str4));
        f4 = Markup.parseLength(this.cprops.getProperty("size"), 12.0F);
        if (f4 <= 0.0F)
          f4 = 12.0F;
        float f5 = Markup.parseLength((String)localObject4, f4);
        float f6 = Markup.parseLength((String)localObject5, f4);
        if ((f5 > 0.0F) && (f6 > 0.0F))
        {
          ((Image)localObject2).scaleAbsolute(f5, f6);
        }
        else if (f5 > 0.0F)
        {
          f6 = ((Image)localObject2).getHeight() * f5 / ((Image)localObject2).getWidth();
          ((Image)localObject2).scaleAbsolute(f5, f6);
        }
        else if (f6 > 0.0F)
        {
          f5 = ((Image)localObject2).getWidth() * f6 / ((Image)localObject2).getHeight();
          ((Image)localObject2).scaleAbsolute(f5, f6);
        }
        ((Image)localObject2).setWidthPercentage(0.0F);
        if (localObject3 != null)
        {
          endElement("p");
          int n = 1;
          if (((String)localObject3).equalsIgnoreCase("left"))
            n = 0;
          else if (((String)localObject3).equalsIgnoreCase("right"))
            n = 2;
          ((Image)localObject2).setAlignment(n);
          Img localImg = null;
          boolean bool = false;
          if (this.interfaceProps != null)
          {
            localImg = (Img)this.interfaceProps.get("img_interface");
            if (localImg != null)
              bool = localImg.process((Image)localObject2, paramHashMap, this.cprops, this.document);
          }
          if (!bool)
            this.document.add((Element)localObject2);
          this.cprops.removeChain(paramString);
        }
        else
        {
          this.cprops.removeChain(paramString);
          if (this.currentParagraph == null)
            this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
          this.currentParagraph.add(new Chunk((Image)localObject2, 0.0F, 0.0F));
        }
        return;
      }
      endElement("p");
      if ((paramString.equals("h1")) || (paramString.equals("h2")) || (paramString.equals("h3")) || (paramString.equals("h4")) || (paramString.equals("h5")) || (paramString.equals("h6")))
      {
        if (!paramHashMap.containsKey("size"))
        {
          int j = 7 - Integer.parseInt(paramString.substring(1));
          paramHashMap.put("size", Integer.toString(j));
        }
        this.cprops.addToChain(paramString, paramHashMap);
        return;
      }
      Object localObject1;
      if (paramString.equals("ul"))
      {
        if (this.pendingLI)
          endElement("li");
        this.skipText = true;
        this.cprops.addToChain(paramString, paramHashMap);
        localObject1 = new List(false);
        try
        {
          ((List)localObject1).setIndentationLeft(new Float(this.cprops.getProperty("indent")).floatValue());
        }
        catch (Exception localException2)
        {
          ((List)localObject1).setAutoindent(true);
        }
        ((List)localObject1).setListSymbol("•");
        this.stack.push(localObject1);
        return;
      }
      if (paramString.equals("ol"))
      {
        if (this.pendingLI)
          endElement("li");
        this.skipText = true;
        this.cprops.addToChain(paramString, paramHashMap);
        localObject1 = new List(true);
        try
        {
          ((List)localObject1).setIndentationLeft(new Float(this.cprops.getProperty("indent")).floatValue());
        }
        catch (Exception localException3)
        {
          ((List)localObject1).setAutoindent(true);
        }
        this.stack.push(localObject1);
        return;
      }
      if (paramString.equals("li"))
      {
        if (this.pendingLI)
          endElement("li");
        this.skipText = false;
        this.pendingLI = true;
        this.cprops.addToChain(paramString, paramHashMap);
        localObject1 = FactoryProperties.createListItem(this.cprops);
        this.stack.push(localObject1);
        return;
      }
      if ((paramString.equals("div")) || (paramString.equals("body")) || (paramString.equals("p")))
      {
        this.cprops.addToChain(paramString, paramHashMap);
        return;
      }
      if (paramString.equals("pre"))
      {
        if (!paramHashMap.containsKey("face"))
          paramHashMap.put("face", "Courier");
        this.cprops.addToChain(paramString, paramHashMap);
        this.isPRE = true;
        return;
      }
      if (paramString.equals("tr"))
      {
        if (this.pendingTR)
          endElement("tr");
        this.skipText = true;
        this.pendingTR = true;
        this.cprops.addToChain("tr", paramHashMap);
        return;
      }
      if ((paramString.equals("td")) || (paramString.equals("th")))
      {
        if (this.pendingTD)
          endElement(paramString);
        this.skipText = false;
        this.pendingTD = true;
        this.cprops.addToChain("td", paramHashMap);
        this.stack.push(new IncCell(paramString, this.cprops));
        return;
      }
      if (paramString.equals("table"))
      {
        this.cprops.addToChain("table", paramHashMap);
        localObject1 = new IncTable(paramHashMap);
        this.stack.push(localObject1);
        this.tableState.push(new boolean[] { this.pendingTR, this.pendingTD });
        this.pendingTR = (this.pendingTD = 0);
        this.skipText = true;
        return;
      }
    }
    catch (Exception localException1)
    {
      throw new ExceptionConverter(localException1);
    }
  }

  public void endElement(String paramString)
  {
    if (!tagsSupported.containsKey(paramString))
      return;
    try
    {
      String str = (String)FactoryProperties.followTags.get(paramString);
      if (str != null)
      {
        this.cprops.removeChain(str);
        return;
      }
      if ((paramString.equals("font")) || (paramString.equals("span")))
      {
        this.cprops.removeChain(paramString);
        return;
      }
      Object localObject2;
      Object localObject3;
      if (paramString.equals("a"))
      {
        if (this.currentParagraph == null)
          this.currentParagraph = new Paragraph();
        boolean bool = false;
        if (this.interfaceProps != null)
        {
          localObject2 = (ALink)this.interfaceProps.get("alink_interface");
          if (localObject2 != null)
            bool = ((ALink)localObject2).process(this.currentParagraph, this.cprops);
        }
        if (!bool)
        {
          localObject2 = this.cprops.getProperty("href");
          if (localObject2 != null)
          {
            localObject3 = this.currentParagraph.getChunks();
            int i = ((ArrayList)localObject3).size();
            for (int j = 0; j < i; j++)
            {
              Chunk localChunk = (Chunk)((ArrayList)localObject3).get(j);
              localChunk.setAnchor((String)localObject2);
            }
          }
        }
        localObject2 = (Paragraph)this.stack.pop();
        localObject3 = new Phrase();
        ((Phrase)localObject3).add(this.currentParagraph);
        ((Paragraph)localObject2).add(localObject3);
        this.currentParagraph = ((Paragraph)localObject2);
        this.cprops.removeChain("a");
        return;
      }
      if (paramString.equals("br"))
        return;
      Object localObject1;
      if (this.currentParagraph != null)
        if (this.stack.empty())
        {
          this.document.add(this.currentParagraph);
        }
        else
        {
          localObject1 = this.stack.pop();
          if ((localObject1 instanceof TextElementArray))
          {
            localObject2 = (TextElementArray)localObject1;
            ((TextElementArray)localObject2).add(this.currentParagraph);
          }
          this.stack.push(localObject1);
        }
      this.currentParagraph = null;
      if ((paramString.equals("ul")) || (paramString.equals("ol")))
      {
        if (this.pendingLI)
          endElement("li");
        this.skipText = false;
        this.cprops.removeChain(paramString);
        if (this.stack.empty())
          return;
        localObject1 = this.stack.pop();
        if (!(localObject1 instanceof List))
        {
          this.stack.push(localObject1);
          return;
        }
        if (this.stack.empty())
          this.document.add((Element)localObject1);
        else
          ((TextElementArray)this.stack.peek()).add(localObject1);
        return;
      }
      if (paramString.equals("li"))
      {
        this.pendingLI = false;
        this.skipText = true;
        this.cprops.removeChain(paramString);
        if (this.stack.empty())
          return;
        localObject1 = this.stack.pop();
        if (!(localObject1 instanceof ListItem))
        {
          this.stack.push(localObject1);
          return;
        }
        if (this.stack.empty())
        {
          this.document.add((Element)localObject1);
          return;
        }
        localObject2 = this.stack.pop();
        if (!(localObject2 instanceof List))
        {
          this.stack.push(localObject2);
          return;
        }
        localObject3 = (ListItem)localObject1;
        ((List)localObject2).add(localObject3);
        ArrayList localArrayList = ((ListItem)localObject3).getChunks();
        if (!localArrayList.isEmpty())
          ((ListItem)localObject3).getListSymbol().setFont(((Chunk)localArrayList.get(0)).getFont());
        this.stack.push(localObject2);
        return;
      }
      if ((paramString.equals("div")) || (paramString.equals("body")))
      {
        this.cprops.removeChain(paramString);
        return;
      }
      if (paramString.equals("pre"))
      {
        this.cprops.removeChain(paramString);
        this.isPRE = false;
        return;
      }
      if (paramString.equals("p"))
      {
        this.cprops.removeChain(paramString);
        return;
      }
      if ((paramString.equals("h1")) || (paramString.equals("h2")) || (paramString.equals("h3")) || (paramString.equals("h4")) || (paramString.equals("h5")) || (paramString.equals("h6")))
      {
        this.cprops.removeChain(paramString);
        return;
      }
      if (paramString.equals("table"))
      {
        if (this.pendingTR)
          endElement("tr");
        this.cprops.removeChain("table");
        localObject1 = (IncTable)this.stack.pop();
        localObject2 = ((IncTable)localObject1).buildTable();
        ((PdfPTable)localObject2).setSplitRows(true);
        if (this.stack.empty())
          this.document.add((Element)localObject2);
        else
          ((TextElementArray)this.stack.peek()).add(localObject2);
        localObject3 = (boolean[])this.tableState.pop();
        this.pendingTR = localObject3[0];
        this.pendingTD = localObject3[1];
        this.skipText = false;
        return;
      }
      if (paramString.equals("tr"))
      {
        if (this.pendingTD)
          endElement("td");
        this.pendingTR = false;
        this.cprops.removeChain("tr");
        localObject1 = new ArrayList();
        localObject2 = null;
        do
        {
          localObject3 = this.stack.pop();
          if (!(localObject3 instanceof IncCell))
            continue;
          ((ArrayList)localObject1).add(((IncCell)localObject3).getCell());
        }
        while (!(localObject3 instanceof IncTable));
        localObject2 = (IncTable)localObject3;
        ((IncTable)localObject2).addCols((ArrayList)localObject1);
        ((IncTable)localObject2).endRow();
        this.stack.push(localObject2);
        this.skipText = true;
        return;
      }
      if ((paramString.equals("td")) || (paramString.equals("th")))
      {
        this.pendingTD = false;
        this.cprops.removeChain("td");
        this.skipText = true;
        return;
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  public void text(String paramString)
  {
    if (this.skipText)
      return;
    String str = paramString;
    if (this.isPRE)
    {
      if (this.currentParagraph == null)
        this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
      localObject = this.factoryProperties.createChunk(str, this.cprops);
      this.currentParagraph.add(localObject);
      return;
    }
    if ((str.trim().length() == 0) && (str.indexOf(' ') < 0))
      return;
    Object localObject = new StringBuffer();
    int i = str.length();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      char c;
      switch (c = str.charAt(k))
      {
      case ' ':
        if (j != 0)
          continue;
        ((StringBuffer)localObject).append(c);
        break;
      case '\n':
        if (k <= 0)
          continue;
        j = 1;
        ((StringBuffer)localObject).append(' ');
        break;
      case '\r':
        break;
      case '\t':
        break;
      default:
        j = 0;
        ((StringBuffer)localObject).append(c);
      }
    }
    if (this.currentParagraph == null)
      this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
    Chunk localChunk = this.factoryProperties.createChunk(((StringBuffer)localObject).toString(), this.cprops);
    this.currentParagraph.add(localChunk);
  }

  public boolean add(Element paramElement)
    throws DocumentException
  {
    this.objectList.add(paramElement);
    return true;
  }

  public void clearTextWrap()
    throws DocumentException
  {
  }

  public void close()
  {
  }

  public boolean newPage()
  {
    return true;
  }

  public void open()
  {
  }

  public void resetFooter()
  {
  }

  public void resetHeader()
  {
  }

  public void resetPageCount()
  {
  }

  public void setFooter(HeaderFooter paramHeaderFooter)
  {
  }

  public void setHeader(HeaderFooter paramHeaderFooter)
  {
  }

  public boolean setMarginMirroring(boolean paramBoolean)
  {
    return false;
  }

  public boolean setMarginMirroringTopBottom(boolean paramBoolean)
  {
    return false;
  }

  public boolean setMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return true;
  }

  public void setPageCount(int paramInt)
  {
  }

  public boolean setPageSize(Rectangle paramRectangle)
  {
    return true;
  }

  static
  {
    StringTokenizer localStringTokenizer = new StringTokenizer("ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike h1 h2 h3 h4 h5 h6 img hr");
    while (localStringTokenizer.hasMoreTokens())
      tagsSupported.put(localStringTokenizer.nextToken(), null);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.HTMLWorker
 * JD-Core Version:    0.6.0
 */