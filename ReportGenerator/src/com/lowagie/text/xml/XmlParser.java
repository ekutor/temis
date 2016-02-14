package com.lowagie.text.xml;

import com.lowagie.text.DocListener;
import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlParser
{
  protected SAXParser parser;

  public XmlParser()
  {
    try
    {
      this.parser = SAXParserFactory.newInstance().newSAXParser();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new ExceptionConverter(localParserConfigurationException);
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
  }

  public void go(DocListener paramDocListener, InputSource paramInputSource)
  {
    try
    {
      this.parser.parse(paramInputSource, new SAXiTextHandler(paramDocListener));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void go(DocListener paramDocListener, InputSource paramInputSource, String paramString)
  {
    try
    {
      this.parser.parse(paramInputSource, new SAXmyHandler(paramDocListener, new TagMap(paramString)));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void go(DocListener paramDocListener, InputSource paramInputSource, InputStream paramInputStream)
  {
    try
    {
      this.parser.parse(paramInputSource, new SAXmyHandler(paramDocListener, new TagMap(paramInputStream)));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void go(DocListener paramDocListener, InputSource paramInputSource, HashMap paramHashMap)
  {
    try
    {
      this.parser.parse(paramInputSource, new SAXmyHandler(paramDocListener, paramHashMap));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void go(DocListener paramDocListener, String paramString)
  {
    try
    {
      this.parser.parse(paramString, new SAXiTextHandler(paramDocListener));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void go(DocListener paramDocListener, String paramString1, String paramString2)
  {
    try
    {
      this.parser.parse(paramString1, new SAXmyHandler(paramDocListener, new TagMap(paramString2)));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void go(DocListener paramDocListener, String paramString, HashMap paramHashMap)
  {
    try
    {
      this.parser.parse(paramString, new SAXmyHandler(paramDocListener, paramHashMap));
    }
    catch (SAXException localSAXException)
    {
      throw new ExceptionConverter(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public static void parse(DocListener paramDocListener, InputSource paramInputSource)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, paramInputSource);
  }

  public static void parse(DocListener paramDocListener, InputSource paramInputSource, String paramString)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, paramInputSource, paramString);
  }

  public static void parse(DocListener paramDocListener, InputSource paramInputSource, HashMap paramHashMap)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, paramInputSource, paramHashMap);
  }

  public static void parse(DocListener paramDocListener, String paramString)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, paramString);
  }

  public static void parse(DocListener paramDocListener, String paramString1, String paramString2)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, paramString1, paramString2);
  }

  public static void parse(DocListener paramDocListener, String paramString, HashMap paramHashMap)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, paramString, paramHashMap);
  }

  public static void parse(DocListener paramDocListener, InputStream paramInputStream)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, new InputSource(paramInputStream));
  }

  public static void parse(DocListener paramDocListener, InputStream paramInputStream, String paramString)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, new InputSource(paramInputStream), paramString);
  }

  public static void parse(DocListener paramDocListener, InputStream paramInputStream, HashMap paramHashMap)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, new InputSource(paramInputStream), paramHashMap);
  }

  public static void parse(DocListener paramDocListener, Reader paramReader)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, new InputSource(paramReader));
  }

  public static void parse(DocListener paramDocListener, Reader paramReader, String paramString)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, new InputSource(paramReader), paramString);
  }

  public static void parse(DocListener paramDocListener, Reader paramReader, HashMap paramHashMap)
  {
    XmlParser localXmlParser = new XmlParser();
    localXmlParser.go(paramDocListener, new InputSource(paramReader), paramHashMap);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.XmlParser
 * JD-Core Version:    0.6.0
 */