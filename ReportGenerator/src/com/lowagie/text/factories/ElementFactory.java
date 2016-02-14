package com.lowagie.text.factories;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.ElementTags;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.Utilities;
import com.lowagie.text.html.Markup;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public class ElementFactory
{
  public static Chunk getChunk(Properties paramProperties)
  {
    Chunk localChunk = new Chunk();
    localChunk.setFont(FontFactory.getFont(paramProperties));
    String str1 = paramProperties.getProperty("itext");
    if (str1 != null)
      localChunk.append(str1);
    str1 = paramProperties.getProperty(ElementTags.LOCALGOTO);
    if (str1 != null)
      localChunk.setLocalGoto(str1);
    str1 = paramProperties.getProperty(ElementTags.REMOTEGOTO);
    if (str1 != null)
    {
      String str2 = paramProperties.getProperty("page");
      if (str2 != null)
      {
        localChunk.setRemoteGoto(str1, Integer.parseInt(str2));
      }
      else
      {
        String str3 = paramProperties.getProperty("destination");
        if (str3 != null)
          localChunk.setRemoteGoto(str1, str3);
      }
    }
    str1 = paramProperties.getProperty(ElementTags.LOCALDESTINATION);
    if (str1 != null)
      localChunk.setLocalDestination(str1);
    str1 = paramProperties.getProperty(ElementTags.SUBSUPSCRIPT);
    if (str1 != null)
      localChunk.setTextRise(Float.parseFloat(str1 + "f"));
    str1 = paramProperties.getProperty("vertical-align");
    if ((str1 != null) && (str1.endsWith("%")))
    {
      float f = Float.parseFloat(str1.substring(0, str1.length() - 1) + "f") / 100.0F;
      localChunk.setTextRise(f * localChunk.getFont().getSize());
    }
    str1 = paramProperties.getProperty(ElementTags.GENERICTAG);
    if (str1 != null)
      localChunk.setGenericTag(str1);
    str1 = paramProperties.getProperty("backgroundcolor");
    if (str1 != null)
      localChunk.setBackground(Markup.decodeColor(str1));
    return localChunk;
  }

  public static Phrase getPhrase(Properties paramProperties)
  {
    Phrase localPhrase = new Phrase();
    localPhrase.setFont(FontFactory.getFont(paramProperties));
    String str = paramProperties.getProperty("leading");
    if (str != null)
      localPhrase.setLeading(Float.parseFloat(str + "f"));
    str = paramProperties.getProperty("line-height");
    if (str != null)
      localPhrase.setLeading(Markup.parseLength(str, 12.0F));
    str = paramProperties.getProperty("itext");
    if (str != null)
    {
      Chunk localChunk = new Chunk(str);
      if ((str = paramProperties.getProperty(ElementTags.GENERICTAG)) != null)
        localChunk.setGenericTag(str);
      localPhrase.add(localChunk);
    }
    return localPhrase;
  }

  public static Anchor getAnchor(Properties paramProperties)
  {
    Anchor localAnchor = new Anchor(getPhrase(paramProperties));
    String str = paramProperties.getProperty("name");
    if (str != null)
      localAnchor.setName(str);
    str = (String)paramProperties.remove("reference");
    if (str != null)
      localAnchor.setReference(str);
    return localAnchor;
  }

  public static Paragraph getParagraph(Properties paramProperties)
  {
    Paragraph localParagraph = new Paragraph(getPhrase(paramProperties));
    String str = paramProperties.getProperty("align");
    if (str != null)
      localParagraph.setAlignment(str);
    str = paramProperties.getProperty("indentationleft");
    if (str != null)
      localParagraph.setIndentationLeft(Float.parseFloat(str + "f"));
    str = paramProperties.getProperty("indentationright");
    if (str != null)
      localParagraph.setIndentationRight(Float.parseFloat(str + "f"));
    return localParagraph;
  }

  public static ListItem getListItem(Properties paramProperties)
  {
    ListItem localListItem = new ListItem(getParagraph(paramProperties));
    return localListItem;
  }

  public static List getList(Properties paramProperties)
  {
    List localList = new List();
    localList.setNumbered(Utilities.checkTrueOrFalse(paramProperties, "numbered"));
    localList.setLettered(Utilities.checkTrueOrFalse(paramProperties, "lettered"));
    localList.setLowercase(Utilities.checkTrueOrFalse(paramProperties, "lowercase"));
    localList.setAutoindent(Utilities.checkTrueOrFalse(paramProperties, "autoindent"));
    localList.setAlignindent(Utilities.checkTrueOrFalse(paramProperties, "alignindent"));
    String str = paramProperties.getProperty("first");
    if (str != null)
    {
      char c = str.charAt(0);
      if (Character.isLetter(c))
        localList.setFirst(c);
      else
        localList.setFirst(Integer.parseInt(str));
    }
    str = paramProperties.getProperty("listsymbol");
    if (str != null)
      localList.setListSymbol(new Chunk(str, FontFactory.getFont(paramProperties)));
    str = paramProperties.getProperty("indentationleft");
    if (str != null)
      localList.setIndentationLeft(Float.parseFloat(str + "f"));
    str = paramProperties.getProperty("indentationright");
    if (str != null)
      localList.setIndentationRight(Float.parseFloat(str + "f"));
    str = paramProperties.getProperty("symbolindent");
    if (str != null)
      localList.setSymbolIndent(Float.parseFloat(str));
    return localList;
  }

  public static Cell getCell(Properties paramProperties)
  {
    Cell localCell = new Cell();
    localCell.setHorizontalAlignment(paramProperties.getProperty("horizontalalign"));
    localCell.setVerticalAlignment(paramProperties.getProperty("verticalalign"));
    String str = paramProperties.getProperty("width");
    if (str != null)
      localCell.setWidth(str);
    str = paramProperties.getProperty("colspan");
    if (str != null)
      localCell.setColspan(Integer.parseInt(str));
    str = paramProperties.getProperty("rowspan");
    if (str != null)
      localCell.setRowspan(Integer.parseInt(str));
    str = paramProperties.getProperty("leading");
    if (str != null)
      localCell.setLeading(Float.parseFloat(str + "f"));
    localCell.setHeader(Utilities.checkTrueOrFalse(paramProperties, "header"));
    if (Utilities.checkTrueOrFalse(paramProperties, "nowrap"))
      localCell.setMaxLines(1);
    setRectangleProperties(localCell, paramProperties);
    return localCell;
  }

  public static Table getTable(Properties paramProperties)
  {
    try
    {
      String str = paramProperties.getProperty("widths");
      Table localTable;
      if (str != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str, ";");
        ArrayList localArrayList = new ArrayList();
        while (localStringTokenizer.hasMoreTokens())
          localArrayList.add(localStringTokenizer.nextToken());
        localTable = new Table(localArrayList.size());
        float[] arrayOfFloat = new float[localTable.getColumns()];
        for (int i = 0; i < localArrayList.size(); i++)
        {
          str = (String)localArrayList.get(i);
          arrayOfFloat[i] = Float.parseFloat(str + "f");
        }
        localTable.setWidths(arrayOfFloat);
      }
      else
      {
        str = paramProperties.getProperty("columns");
        try
        {
          localTable = new Table(Integer.parseInt(str));
        }
        catch (Exception localException)
        {
          localTable = new Table(1);
        }
      }
      localTable.setBorder(15);
      localTable.setBorderWidth(1.0F);
      localTable.getDefaultCell().setBorder(15);
      str = paramProperties.getProperty("lastHeaderRow");
      if (str != null)
        localTable.setLastHeaderRow(Integer.parseInt(str));
      str = paramProperties.getProperty("align");
      if (str != null)
        localTable.setAlignment(str);
      str = paramProperties.getProperty("cellspacing");
      if (str != null)
        localTable.setSpacing(Float.parseFloat(str + "f"));
      str = paramProperties.getProperty("cellpadding");
      if (str != null)
        localTable.setPadding(Float.parseFloat(str + "f"));
      str = paramProperties.getProperty("offset");
      if (str != null)
        localTable.setOffset(Float.parseFloat(str + "f"));
      str = paramProperties.getProperty("width");
      if (str != null)
        if (str.endsWith("%"))
        {
          localTable.setWidth(Float.parseFloat(str.substring(0, str.length() - 1) + "f"));
        }
        else
        {
          localTable.setWidth(Float.parseFloat(str + "f"));
          localTable.setLocked(true);
        }
      localTable.setTableFitsPage(Utilities.checkTrueOrFalse(paramProperties, "tablefitspage"));
      localTable.setCellsFitPage(Utilities.checkTrueOrFalse(paramProperties, "cellsfitpage"));
      localTable.setConvert2pdfptable(Utilities.checkTrueOrFalse(paramProperties, "convert2pdfp"));
      setRectangleProperties(localTable, paramProperties);
      return localTable;
    }
    catch (BadElementException localBadElementException)
    {
    }
    throw new ExceptionConverter(localBadElementException);
  }

  private static void setRectangleProperties(Rectangle paramRectangle, Properties paramProperties)
  {
    String str1 = paramProperties.getProperty("borderwidth");
    if (str1 != null)
      paramRectangle.setBorderWidth(Float.parseFloat(str1 + "f"));
    int i = 0;
    if (Utilities.checkTrueOrFalse(paramProperties, "left"))
      i |= 4;
    if (Utilities.checkTrueOrFalse(paramProperties, "right"))
      i |= 8;
    if (Utilities.checkTrueOrFalse(paramProperties, "top"))
      i |= 1;
    if (Utilities.checkTrueOrFalse(paramProperties, "bottom"))
      i |= 2;
    paramRectangle.setBorder(i);
    String str2 = paramProperties.getProperty("red");
    String str3 = paramProperties.getProperty("green");
    String str4 = paramProperties.getProperty("blue");
    int j;
    int k;
    int m;
    if ((str2 != null) || (str3 != null) || (str4 != null))
    {
      j = 0;
      k = 0;
      m = 0;
      if (str2 != null)
        j = Integer.parseInt(str2);
      if (str3 != null)
        k = Integer.parseInt(str3);
      if (str4 != null)
        m = Integer.parseInt(str4);
      paramRectangle.setBorderColor(new Color(j, k, m));
    }
    else
    {
      paramRectangle.setBorderColor(Markup.decodeColor(paramProperties.getProperty("bordercolor")));
    }
    str2 = (String)paramProperties.remove("bgred");
    str3 = (String)paramProperties.remove("bggreen");
    str4 = (String)paramProperties.remove("bgblue");
    str1 = paramProperties.getProperty("backgroundcolor");
    if ((str2 != null) || (str3 != null) || (str4 != null))
    {
      j = 0;
      k = 0;
      m = 0;
      if (str2 != null)
        j = Integer.parseInt(str2);
      if (str3 != null)
        k = Integer.parseInt(str3);
      if (str4 != null)
        m = Integer.parseInt(str4);
      paramRectangle.setBackgroundColor(new Color(j, k, m));
    }
    else if (str1 != null)
    {
      paramRectangle.setBackgroundColor(Markup.decodeColor(str1));
    }
    else
    {
      str1 = paramProperties.getProperty("grayfill");
      if (str1 != null)
        paramRectangle.setGrayFill(Float.parseFloat(str1 + "f"));
    }
  }

  public static ChapterAutoNumber getChapter(Properties paramProperties)
  {
    ChapterAutoNumber localChapterAutoNumber = new ChapterAutoNumber("");
    setSectionParameters(localChapterAutoNumber, paramProperties);
    return localChapterAutoNumber;
  }

  public static Section getSection(Section paramSection, Properties paramProperties)
  {
    Section localSection = paramSection.addSection("");
    setSectionParameters(localSection, paramProperties);
    return localSection;
  }

  private static void setSectionParameters(Section paramSection, Properties paramProperties)
  {
    String str = paramProperties.getProperty("numberdepth");
    if (str != null)
      paramSection.setNumberDepth(Integer.parseInt(str));
    str = paramProperties.getProperty("indent");
    if (str != null)
      paramSection.setIndentation(Float.parseFloat(str + "f"));
    str = paramProperties.getProperty("indentationleft");
    if (str != null)
      paramSection.setIndentationLeft(Float.parseFloat(str + "f"));
    str = paramProperties.getProperty("indentationright");
    if (str != null)
      paramSection.setIndentationRight(Float.parseFloat(str + "f"));
  }

  public static Image getImage(Properties paramProperties)
    throws BadElementException, MalformedURLException, IOException
  {
    String str1 = paramProperties.getProperty("url");
    if (str1 == null)
      throw new MalformedURLException("The URL of the image is missing.");
    Image localImage = Image.getInstance(str1);
    str1 = paramProperties.getProperty("align");
    int i = 0;
    if (str1 != null)
      if ("Left".equalsIgnoreCase(str1))
        i |= 0;
      else if ("Right".equalsIgnoreCase(str1))
        i |= 2;
      else if ("Middle".equalsIgnoreCase(str1))
        i |= 1;
    if ("true".equalsIgnoreCase(paramProperties.getProperty("underlying")))
      i |= 8;
    if ("true".equalsIgnoreCase(paramProperties.getProperty("textwrap")))
      i |= 4;
    localImage.setAlignment(i);
    str1 = paramProperties.getProperty("alt");
    if (str1 != null)
      localImage.setAlt(str1);
    String str2 = paramProperties.getProperty("absolutex");
    String str3 = paramProperties.getProperty("absolutey");
    if ((str2 != null) && (str3 != null))
      localImage.setAbsolutePosition(Float.parseFloat(str2 + "f"), Float.parseFloat(str3 + "f"));
    str1 = paramProperties.getProperty("plainwidth");
    if (str1 != null)
      localImage.scaleAbsoluteWidth(Float.parseFloat(str1 + "f"));
    str1 = paramProperties.getProperty("plainheight");
    if (str1 != null)
      localImage.scaleAbsoluteHeight(Float.parseFloat(str1 + "f"));
    str1 = paramProperties.getProperty("rotation");
    if (str1 != null)
      localImage.setRotation(Float.parseFloat(str1 + "f"));
    return localImage;
  }

  public static Annotation getAnnotation(Properties paramProperties)
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    String str1 = paramProperties.getProperty("llx");
    if (str1 != null)
      f1 = Float.parseFloat(str1 + "f");
    str1 = paramProperties.getProperty("lly");
    if (str1 != null)
      f2 = Float.parseFloat(str1 + "f");
    str1 = paramProperties.getProperty("urx");
    if (str1 != null)
      f3 = Float.parseFloat(str1 + "f");
    str1 = paramProperties.getProperty("ury");
    if (str1 != null)
      f4 = Float.parseFloat(str1 + "f");
    String str2 = paramProperties.getProperty("title");
    String str3 = paramProperties.getProperty("content");
    if ((str2 != null) || (str3 != null))
      return new Annotation(str2, str3, f1, f2, f3, f4);
    str1 = paramProperties.getProperty("url");
    if (str1 != null)
      return new Annotation(f1, f2, f3, f4, str1);
    str1 = paramProperties.getProperty("named");
    if (str1 != null)
      return new Annotation(f1, f2, f3, f4, Integer.parseInt(str1));
    String str4 = paramProperties.getProperty("file");
    String str5 = paramProperties.getProperty("destination");
    String str6 = (String)paramProperties.remove("page");
    if (str4 != null)
    {
      if (str5 != null)
        return new Annotation(f1, f2, f3, f4, str4, str5);
      if (str6 != null)
        return new Annotation(f1, f2, f3, f4, str4, Integer.parseInt(str6));
    }
    return new Annotation("", "", f1, f2, f3, f4);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.factories.ElementFactory
 * JD-Core Version:    0.6.0
 */