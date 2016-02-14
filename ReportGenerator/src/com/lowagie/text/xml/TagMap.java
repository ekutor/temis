package com.lowagie.text.xml;

import com.lowagie.text.ExceptionConverter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class TagMap extends HashMap
{
  private static final long serialVersionUID = -6809383366554350820L;

  public TagMap(String paramString)
  {
    try
    {
      init(TagMap.class.getClassLoader().getResourceAsStream(paramString));
    }
    catch (Exception localException)
    {
      try
      {
        init(new FileInputStream(paramString));
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        throw new ExceptionConverter(localFileNotFoundException);
      }
    }
  }

  public TagMap(InputStream paramInputStream)
  {
    init(paramInputStream);
  }

  protected void init(InputStream paramInputStream)
  {
    try
    {
      SAXParser localSAXParser = SAXParserFactory.newInstance().newSAXParser();
      localSAXParser.parse(new InputSource(paramInputStream), new AttributeHandler(this));
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  class AttributeHandler extends DefaultHandler
  {
    public static final String TAG = "tag";
    public static final String ATTRIBUTE = "attribute";
    public static final String NAME = "name";
    public static final String ALIAS = "alias";
    public static final String VALUE = "value";
    public static final String CONTENT = "content";
    private HashMap tagMap;
    private XmlPeer currentPeer;

    public AttributeHandler(HashMap arg2)
    {
      Object localObject;
      this.tagMap = localObject;
    }

    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    {
      String str1 = paramAttributes.getValue("name");
      String str2 = paramAttributes.getValue("alias");
      String str3 = paramAttributes.getValue("value");
      if (str1 != null)
        if ("tag".equals(paramString3))
        {
          this.currentPeer = new XmlPeer(str1, str2);
        }
        else if ("attribute".equals(paramString3))
        {
          if (str2 != null)
            this.currentPeer.addAlias(str1, str2);
          if (str3 != null)
            this.currentPeer.addValue(str1, str3);
        }
      str3 = paramAttributes.getValue("content");
      if (str3 != null)
        this.currentPeer.setContent(str3);
    }

    public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
    }

    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
    }

    public void endElement(String paramString1, String paramString2, String paramString3)
    {
      if ("tag".equals(paramString3))
        this.tagMap.put(this.currentPeer.getAlias(), this.currentPeer);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.TagMap
 * JD-Core Version:    0.6.0
 */