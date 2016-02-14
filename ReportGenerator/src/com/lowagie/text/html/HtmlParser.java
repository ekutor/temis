package com.lowagie.text.html;

import com.lowagie.text.DocListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlParser extends XmlParser
{
  public void go(DocListener paramDocListener, InputSource paramInputSource)
  {
    try
    {
      this.parser.parse(paramInputSource, new SAXmyHtmlHandler(paramDocListener));
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
    HtmlParser localHtmlParser = new HtmlParser();
    localHtmlParser.go(paramDocListener, paramInputSource);
  }

  public void go(DocListener paramDocListener, String paramString)
  {
    try
    {
      this.parser.parse(paramString, new SAXmyHtmlHandler(paramDocListener));
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

  public static void parse(DocListener paramDocListener, String paramString)
  {
    HtmlParser localHtmlParser = new HtmlParser();
    localHtmlParser.go(paramDocListener, paramString);
  }

  public void go(DocListener paramDocListener, InputStream paramInputStream)
  {
    try
    {
      this.parser.parse(new InputSource(paramInputStream), new SAXmyHtmlHandler(paramDocListener));
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

  public static void parse(DocListener paramDocListener, InputStream paramInputStream)
  {
    HtmlParser localHtmlParser = new HtmlParser();
    localHtmlParser.go(paramDocListener, new InputSource(paramInputStream));
  }

  public void go(DocListener paramDocListener, Reader paramReader)
  {
    try
    {
      this.parser.parse(new InputSource(paramReader), new SAXmyHtmlHandler(paramDocListener));
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

  public static void parse(DocListener paramDocListener, Reader paramReader)
  {
    HtmlParser localHtmlParser = new HtmlParser();
    localHtmlParser.go(paramDocListener, new InputSource(paramReader));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.HtmlParser
 * JD-Core Version:    0.6.0
 */