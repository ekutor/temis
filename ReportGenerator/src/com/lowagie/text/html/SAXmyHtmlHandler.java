package com.lowagie.text.html;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.xml.SAXiTextHandler;
import com.lowagie.text.xml.XmlPeer;
import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;
import org.xml.sax.Attributes;

public class SAXmyHtmlHandler extends SAXiTextHandler
{
  private Properties bodyAttributes = new Properties();
  private boolean tableBorder = false;

  public SAXmyHtmlHandler(DocListener paramDocListener)
  {
    super(paramDocListener, new HtmlTagMap());
  }

  public SAXmyHtmlHandler(DocListener paramDocListener, BaseFont paramBaseFont)
  {
    super(paramDocListener, new HtmlTagMap(), paramBaseFont);
  }

  public SAXmyHtmlHandler(DocListener paramDocListener, HashMap paramHashMap)
  {
    super(paramDocListener, paramHashMap);
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    paramString3 = paramString3.toLowerCase();
    if (HtmlTagMap.isHtml(paramString3))
      return;
    if (HtmlTagMap.isHead(paramString3))
      return;
    if (HtmlTagMap.isTitle(paramString3))
      return;
    Object localObject2;
    if (HtmlTagMap.isMeta(paramString3))
    {
      localObject1 = null;
      localObject2 = null;
      if (paramAttributes != null)
        for (int j = 0; j < paramAttributes.getLength(); j++)
        {
          String str2 = paramAttributes.getQName(j);
          if (str2.equalsIgnoreCase("content"))
          {
            localObject2 = paramAttributes.getValue(j);
          }
          else
          {
            if (!str2.equalsIgnoreCase("name"))
              continue;
            localObject1 = paramAttributes.getValue(j);
          }
        }
      if ((localObject1 != null) && (localObject2 != null))
        this.bodyAttributes.put(localObject1, localObject2);
      return;
    }
    if (HtmlTagMap.isLink(paramString3))
      return;
    if (HtmlTagMap.isBody(paramString3))
    {
      localObject1 = new XmlPeer("itext", paramString3);
      ((XmlPeer)localObject1).addAlias("top", "topmargin");
      ((XmlPeer)localObject1).addAlias("bottom", "bottommargin");
      ((XmlPeer)localObject1).addAlias("right", "rightmargin");
      ((XmlPeer)localObject1).addAlias("left", "leftmargin");
      this.bodyAttributes.putAll(((XmlPeer)localObject1).getAttributes(paramAttributes));
      handleStartingTags(((XmlPeer)localObject1).getTag(), this.bodyAttributes);
      return;
    }
    String str1;
    if (this.myTags.containsKey(paramString3))
    {
      localObject1 = (XmlPeer)this.myTags.get(paramString3);
      if (("table".equals(((XmlPeer)localObject1).getTag())) || ("cell".equals(((XmlPeer)localObject1).getTag())))
      {
        localObject2 = ((XmlPeer)localObject1).getAttributes(paramAttributes);
        if (("table".equals(((XmlPeer)localObject1).getTag())) && ((str1 = ((Properties)localObject2).getProperty("borderwidth")) != null) && (Float.parseFloat(str1 + "f") > 0.0F))
          this.tableBorder = true;
        if (this.tableBorder)
        {
          ((Properties)localObject2).put("left", String.valueOf(true));
          ((Properties)localObject2).put("right", String.valueOf(true));
          ((Properties)localObject2).put("top", String.valueOf(true));
          ((Properties)localObject2).put("bottom", String.valueOf(true));
        }
        handleStartingTags(((XmlPeer)localObject1).getTag(), (Properties)localObject2);
        return;
      }
      handleStartingTags(((XmlPeer)localObject1).getTag(), ((XmlPeer)localObject1).getAttributes(paramAttributes));
      return;
    }
    Object localObject1 = new Properties();
    if (paramAttributes != null)
      for (int i = 0; i < paramAttributes.getLength(); i++)
      {
        str1 = paramAttributes.getQName(i).toLowerCase();
        ((Properties)localObject1).setProperty(str1, paramAttributes.getValue(i).toLowerCase());
      }
    handleStartingTags(paramString3, (Properties)localObject1);
  }

  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    paramString3 = paramString3.toLowerCase();
    if ("paragraph".equals(paramString3))
      try
      {
        this.document.add((Element)this.stack.pop());
        return;
      }
      catch (DocumentException localDocumentException)
      {
        throw new ExceptionConverter(localDocumentException);
      }
    if (HtmlTagMap.isHead(paramString3))
      return;
    if (HtmlTagMap.isTitle(paramString3))
    {
      if (this.currentChunk != null)
        this.bodyAttributes.put("title", this.currentChunk.getContent());
      return;
    }
    if (HtmlTagMap.isMeta(paramString3))
      return;
    if (HtmlTagMap.isLink(paramString3))
      return;
    if (HtmlTagMap.isBody(paramString3))
      return;
    if (this.myTags.containsKey(paramString3))
    {
      XmlPeer localXmlPeer = (XmlPeer)this.myTags.get(paramString3);
      if ("table".equals(localXmlPeer.getTag()))
        this.tableBorder = false;
      super.handleEndingTags(localXmlPeer.getTag());
      return;
    }
    handleEndingTags(paramString3);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.SAXmyHtmlHandler
 * JD-Core Version:    0.6.0
 */