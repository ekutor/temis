package com.lowagie.text.xml;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.Meta;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.factories.ElementFactory;
import com.lowagie.text.html.HtmlTagMap;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.xml.simpleparser.EntitiesToSymbol;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXiTextHandler extends DefaultHandler
{
  protected DocListener document;
  protected Stack stack;
  protected int chapters = 0;
  protected Chunk currentChunk = null;
  protected boolean ignore = false;
  protected boolean controlOpenClose = true;
  float topMargin = 36.0F;
  float rightMargin = 36.0F;
  float leftMargin = 36.0F;
  float bottomMargin = 36.0F;
  protected HashMap myTags;
  private BaseFont bf = null;

  public SAXiTextHandler(DocListener paramDocListener)
  {
    this.document = paramDocListener;
    this.stack = new Stack();
  }

  public SAXiTextHandler(DocListener paramDocListener, HtmlTagMap paramHtmlTagMap)
  {
    this(paramDocListener);
    this.myTags = paramHtmlTagMap;
  }

  public SAXiTextHandler(DocListener paramDocListener, HtmlTagMap paramHtmlTagMap, BaseFont paramBaseFont)
  {
    this(paramDocListener, paramHtmlTagMap);
    this.bf = paramBaseFont;
  }

  public SAXiTextHandler(DocListener paramDocListener, HashMap paramHashMap)
  {
    this(paramDocListener);
    this.myTags = paramHashMap;
  }

  public void setControlOpenClose(boolean paramBoolean)
  {
    this.controlOpenClose = paramBoolean;
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    Properties localProperties = new Properties();
    if (paramAttributes != null)
      for (int i = 0; i < paramAttributes.getLength(); i++)
      {
        String str = paramAttributes.getQName(i);
        localProperties.setProperty(str, paramAttributes.getValue(i));
      }
    handleStartingTags(paramString3, localProperties);
  }

  public void handleStartingTags(String paramString, Properties paramProperties)
  {
    if ((this.ignore) || ("ignore".equals(paramString)))
    {
      this.ignore = true;
      return;
    }
    Object localObject1;
    if (this.currentChunk != null)
    {
      try
      {
        localObject1 = (TextElementArray)this.stack.pop();
      }
      catch (EmptyStackException localEmptyStackException1)
      {
        if (this.bf == null)
          localObject1 = new Paragraph("", new Font());
        else
          localObject1 = new Paragraph("", new Font(this.bf));
      }
      ((TextElementArray)localObject1).add(this.currentChunk);
      this.stack.push(localObject1);
      this.currentChunk = null;
    }
    if ("chunk".equals(paramString))
    {
      this.currentChunk = ElementFactory.getChunk(paramProperties);
      if (this.bf != null)
        this.currentChunk.setFont(new Font(this.bf));
      return;
    }
    if ("entity".equals(paramString))
    {
      localObject1 = new Font();
      if (this.currentChunk != null)
      {
        handleEndingTags("chunk");
        localObject1 = this.currentChunk.getFont();
      }
      this.currentChunk = EntitiesToSymbol.get(paramProperties.getProperty("id"), (Font)localObject1);
      return;
    }
    if ("phrase".equals(paramString))
    {
      this.stack.push(ElementFactory.getPhrase(paramProperties));
      return;
    }
    if ("anchor".equals(paramString))
    {
      this.stack.push(ElementFactory.getAnchor(paramProperties));
      return;
    }
    if (("paragraph".equals(paramString)) || ("title".equals(paramString)))
    {
      this.stack.push(ElementFactory.getParagraph(paramProperties));
      return;
    }
    if ("list".equals(paramString))
    {
      this.stack.push(ElementFactory.getList(paramProperties));
      return;
    }
    if ("listitem".equals(paramString))
    {
      this.stack.push(ElementFactory.getListItem(paramProperties));
      return;
    }
    if ("cell".equals(paramString))
    {
      this.stack.push(ElementFactory.getCell(paramProperties));
      return;
    }
    Object localObject3;
    if ("table".equals(paramString))
    {
      localObject1 = ElementFactory.getTable(paramProperties);
      localObject3 = ((Table)localObject1).getProportionalWidths();
      for (int i = 0; i < localObject3.length; i++)
      {
        if (localObject3[i] != 0.0F)
          continue;
        localObject3[i] = (100.0F / localObject3.length);
      }
      try
      {
        ((Table)localObject1).setWidths(localObject3);
      }
      catch (BadElementException localBadElementException)
      {
        throw new ExceptionConverter(localBadElementException);
      }
      this.stack.push(localObject1);
      return;
    }
    if ("section".equals(paramString))
    {
      localObject1 = (Element)this.stack.pop();
      try
      {
        localObject3 = ElementFactory.getSection((Section)localObject1, paramProperties);
      }
      catch (ClassCastException localClassCastException)
      {
        throw new ExceptionConverter(localClassCastException);
      }
      this.stack.push(localObject1);
      this.stack.push(localObject3);
      return;
    }
    if ("chapter".equals(paramString))
    {
      this.stack.push(ElementFactory.getChapter(paramProperties));
      return;
    }
    if ("image".equals(paramString))
      try
      {
        localObject1 = ElementFactory.getImage(paramProperties);
        try
        {
          addImage((Image)localObject1);
          return;
        }
        catch (EmptyStackException localEmptyStackException2)
        {
          try
          {
            this.document.add((Element)localObject1);
          }
          catch (DocumentException localDocumentException1)
          {
            throw new ExceptionConverter(localDocumentException1);
          }
          return;
        }
      }
      catch (Exception localException1)
      {
        throw new ExceptionConverter(localException1);
      }
    Object localObject2;
    if ("annotation".equals(paramString))
    {
      localObject2 = ElementFactory.getAnnotation(paramProperties);
      try
      {
        try
        {
          TextElementArray localTextElementArray = (TextElementArray)this.stack.pop();
          try
          {
            localTextElementArray.add(localObject2);
          }
          catch (Exception localException2)
          {
            this.document.add((Element)localObject2);
          }
          this.stack.push(localTextElementArray);
        }
        catch (EmptyStackException localEmptyStackException5)
        {
          this.document.add((Element)localObject2);
        }
        return;
      }
      catch (DocumentException localDocumentException2)
      {
        throw new ExceptionConverter(localDocumentException2);
      }
    }
    if (isNewline(paramString))
    {
      try
      {
        localObject2 = (TextElementArray)this.stack.pop();
        ((TextElementArray)localObject2).add(Chunk.NEWLINE);
        this.stack.push(localObject2);
      }
      catch (EmptyStackException localEmptyStackException3)
      {
        if (this.currentChunk == null)
          try
          {
            this.document.add(Chunk.NEWLINE);
          }
          catch (DocumentException localDocumentException3)
          {
            throw new ExceptionConverter(localDocumentException3);
          }
        else
          this.currentChunk.append("\n");
      }
      return;
    }
    if (isNewpage(paramString))
    {
      try
      {
        localObject2 = (TextElementArray)this.stack.pop();
        Chunk localChunk = new Chunk("");
        localChunk.setNewPage();
        if (this.bf != null)
          localChunk.setFont(new Font(this.bf));
        ((TextElementArray)localObject2).add(localChunk);
        this.stack.push(localObject2);
      }
      catch (EmptyStackException localEmptyStackException4)
      {
        this.document.newPage();
      }
      return;
    }
    Object localObject4;
    if ("horizontalrule".equals(paramString))
    {
      localObject4 = new LineSeparator(1.0F, 100.0F, null, 1, 0.0F);
      try
      {
        localObject2 = (TextElementArray)this.stack.pop();
        ((TextElementArray)localObject2).add(localObject4);
        this.stack.push(localObject2);
      }
      catch (EmptyStackException localEmptyStackException6)
      {
        try
        {
          this.document.add((Element)localObject4);
        }
        catch (DocumentException localDocumentException4)
        {
          throw new ExceptionConverter(localDocumentException4);
        }
      }
      return;
    }
    if (isDocumentRoot(paramString))
    {
      Rectangle localRectangle = null;
      String str = null;
      Iterator localIterator = paramProperties.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        localObject4 = paramProperties.getProperty((String)localObject2);
        try
        {
          if ("left".equalsIgnoreCase((String)localObject2))
            this.leftMargin = Float.parseFloat((String)localObject4 + "f");
          if ("right".equalsIgnoreCase((String)localObject2))
            this.rightMargin = Float.parseFloat((String)localObject4 + "f");
          if ("top".equalsIgnoreCase((String)localObject2))
            this.topMargin = Float.parseFloat((String)localObject4 + "f");
          if ("bottom".equalsIgnoreCase((String)localObject2))
            this.bottomMargin = Float.parseFloat((String)localObject4 + "f");
        }
        catch (Exception localException3)
        {
          throw new ExceptionConverter(localException3);
        }
        if ("pagesize".equals(localObject2))
          try
          {
            Object localObject5 = localObject4;
            Field localField = PageSize.class.getField(localObject5);
            localRectangle = (Rectangle)localField.get(null);
          }
          catch (Exception localException4)
          {
            throw new ExceptionConverter(localException4);
          }
        if ("orientation".equals(localObject2))
          try
          {
            if ("landscape".equals(localObject4))
              str = "landscape";
          }
          catch (Exception localException5)
          {
            throw new ExceptionConverter(localException5);
          }
        try
        {
          this.document.add(new Meta((String)localObject2, (String)localObject4));
        }
        catch (DocumentException localDocumentException5)
        {
          throw new ExceptionConverter(localDocumentException5);
        }
      }
      if (localRectangle != null)
      {
        if ("landscape".equals(str))
          localRectangle = localRectangle.rotate();
        this.document.setPageSize(localRectangle);
      }
      this.document.setMargins(this.leftMargin, this.rightMargin, this.topMargin, this.bottomMargin);
      if (this.controlOpenClose)
        this.document.open();
    }
  }

  protected void addImage(Image paramImage)
    throws EmptyStackException
  {
    Object localObject = this.stack.pop();
    if (((localObject instanceof Chapter)) || ((localObject instanceof Section)) || ((localObject instanceof Cell)))
    {
      ((TextElementArray)localObject).add(paramImage);
      this.stack.push(localObject);
      return;
    }
    Stack localStack = new Stack();
    while ((!(localObject instanceof Chapter)) && (!(localObject instanceof Section)) && (!(localObject instanceof Cell)))
    {
      localStack.push(localObject);
      if ((localObject instanceof Anchor))
        paramImage.setAnnotation(new Annotation(0.0F, 0.0F, 0.0F, 0.0F, ((Anchor)localObject).getReference()));
      localObject = this.stack.pop();
    }
    ((TextElementArray)localObject).add(paramImage);
    this.stack.push(localObject);
    while (!localStack.empty())
      this.stack.push(localStack.pop());
  }

  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }

  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (this.ignore)
      return;
    String str = new String(paramArrayOfChar, paramInt1, paramInt2);
    if ((str.trim().length() == 0) && (str.indexOf(' ') < 0))
      return;
    StringBuffer localStringBuffer = new StringBuffer();
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
        localStringBuffer.append(c);
        break;
      case '\n':
        if (k <= 0)
          continue;
        j = 1;
        localStringBuffer.append(' ');
        break;
      case '\r':
        break;
      case '\t':
        break;
      default:
        j = 0;
        localStringBuffer.append(c);
      }
    }
    if (this.currentChunk == null)
    {
      if (this.bf == null)
        this.currentChunk = new Chunk(localStringBuffer.toString());
      else
        this.currentChunk = new Chunk(localStringBuffer.toString(), new Font(this.bf));
    }
    else
      this.currentChunk.append(localStringBuffer.toString());
  }

  public void setBaseFont(BaseFont paramBaseFont)
  {
    this.bf = paramBaseFont;
  }

  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    handleEndingTags(paramString3);
  }

  public void handleEndingTags(String paramString)
  {
    if ("ignore".equals(paramString))
    {
      this.ignore = false;
      return;
    }
    if (this.ignore)
      return;
    if ((isNewpage(paramString)) || ("annotation".equals(paramString)) || ("image".equals(paramString)) || (isNewline(paramString)))
      return;
    try
    {
      Object localObject1;
      if ("title".equals(paramString))
      {
        localObject1 = (Paragraph)this.stack.pop();
        if (this.currentChunk != null)
        {
          ((Paragraph)localObject1).add(this.currentChunk);
          this.currentChunk = null;
        }
        Section localSection = (Section)this.stack.pop();
        localSection.setTitle((Paragraph)localObject1);
        this.stack.push(localSection);
        return;
      }
      if (this.currentChunk != null)
      {
        try
        {
          localObject1 = (TextElementArray)this.stack.pop();
        }
        catch (EmptyStackException localEmptyStackException2)
        {
          localObject1 = new Paragraph();
        }
        ((TextElementArray)localObject1).add(this.currentChunk);
        this.stack.push(localObject1);
        this.currentChunk = null;
      }
      if ("chunk".equals(paramString))
        return;
      if (("phrase".equals(paramString)) || ("anchor".equals(paramString)) || ("list".equals(paramString)) || ("paragraph".equals(paramString)))
      {
        localObject1 = (Element)this.stack.pop();
        try
        {
          TextElementArray localTextElementArray1 = (TextElementArray)this.stack.pop();
          localTextElementArray1.add(localObject1);
          this.stack.push(localTextElementArray1);
        }
        catch (EmptyStackException localEmptyStackException3)
        {
          this.document.add((Element)localObject1);
        }
        return;
      }
      Object localObject2;
      if ("listitem".equals(paramString))
      {
        localObject1 = (ListItem)this.stack.pop();
        localObject2 = (com.lowagie.text.List)this.stack.pop();
        ((com.lowagie.text.List)localObject2).add(localObject1);
        this.stack.push(localObject2);
      }
      if ("table".equals(paramString))
      {
        localObject1 = (Table)this.stack.pop();
        try
        {
          localObject2 = (TextElementArray)this.stack.pop();
          ((TextElementArray)localObject2).add(localObject1);
          this.stack.push(localObject2);
        }
        catch (EmptyStackException localEmptyStackException4)
        {
          this.document.add((Element)localObject1);
        }
        return;
      }
      if ("row".equals(paramString))
      {
        localObject1 = new ArrayList();
        int i = 0;
        Object localObject3;
        Cell localCell;
        while (true)
        {
          localObject3 = (Element)this.stack.pop();
          if (((Element)localObject3).type() != 20)
            break;
          localCell = (Cell)localObject3;
          i += localCell.getColspan();
          ((ArrayList)localObject1).add(localCell);
        }
        Table localTable = (Table)localObject3;
        if (localTable.getColumns() < i)
          localTable.addColumns(i - localTable.getColumns());
        Collections.reverse((java.util.List)localObject1);
        float[] arrayOfFloat = new float[i];
        boolean[] arrayOfBoolean = new boolean[i];
        for (int j = 0; j < i; j++)
        {
          arrayOfFloat[j] = 0.0F;
          arrayOfBoolean[j] = true;
        }
        float f1 = 0.0F;
        int k = 0;
        Object localObject4 = ((ArrayList)localObject1).iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localCell = (Cell)((Iterator)localObject4).next();
          localObject3 = localCell.getWidthAsString();
          if (localCell.getWidth() == 0.0F)
          {
            if ((localCell.getColspan() == 1) && (arrayOfFloat[k] == 0.0F))
              try
              {
                arrayOfFloat[k] = (100.0F / i);
                f1 += arrayOfFloat[k];
              }
              catch (Exception localException1)
              {
              }
            else if (localCell.getColspan() == 1)
              arrayOfBoolean[k] = false;
          }
          else if ((localCell.getColspan() == 1) && (((String)localObject3).endsWith("%")))
            try
            {
              arrayOfFloat[k] = Float.parseFloat(((String)localObject3).substring(0, ((String)localObject3).length() - 1) + "f");
              f1 += arrayOfFloat[k];
            }
            catch (Exception localException2)
            {
            }
          k += localCell.getColspan();
          localTable.addCell(localCell);
        }
        localObject4 = localTable.getProportionalWidths();
        if (localObject4.length == i)
        {
          float f2 = 0.0F;
          for (int m = 0; m < i; m++)
          {
            if ((arrayOfBoolean[m] == 0) || (localObject4[m] == 0.0F))
              continue;
            f2 += localObject4[m];
            arrayOfFloat[m] = localObject4[m];
          }
          if (100.0D >= f1)
            for (m = 0; m < localObject4.length; m++)
            {
              if ((arrayOfFloat[m] != 0.0F) || (localObject4[m] == 0.0F))
                continue;
              arrayOfFloat[m] = (localObject4[m] / f2 * (100.0F - f1));
            }
          localTable.setWidths(arrayOfFloat);
        }
        this.stack.push(localTable);
      }
      if ("cell".equals(paramString))
        return;
      if ("section".equals(paramString))
      {
        this.stack.pop();
        return;
      }
      if ("chapter".equals(paramString))
      {
        this.document.add((Element)this.stack.pop());
        return;
      }
      if (isDocumentRoot(paramString))
        try
        {
          while (true)
          {
            localObject1 = (Element)this.stack.pop();
            try
            {
              TextElementArray localTextElementArray2 = (TextElementArray)this.stack.pop();
              localTextElementArray2.add(localObject1);
              this.stack.push(localTextElementArray2);
            }
            catch (EmptyStackException localEmptyStackException5)
            {
              this.document.add((Element)localObject1);
            }
          }
        }
        catch (EmptyStackException localEmptyStackException1)
        {
          if (this.controlOpenClose)
            this.document.close();
          return;
        }
    }
    catch (DocumentException localDocumentException)
    {
      throw new ExceptionConverter(localDocumentException);
    }
  }

  private boolean isNewpage(String paramString)
  {
    return "newpage".equals(paramString);
  }

  private boolean isNewline(String paramString)
  {
    return "newline".equals(paramString);
  }

  protected boolean isDocumentRoot(String paramString)
  {
    return "itext".equals(paramString);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.SAXiTextHandler
 * JD-Core Version:    0.6.0
 */