package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class SimplePatternParser
  implements SimpleXMLDocHandler, PatternConsumer
{
  int currElement;
  PatternConsumer consumer;
  StringBuffer token = new StringBuffer();
  ArrayList exception;
  char hyphenChar = '-';
  SimpleXMLParser parser;
  static final int ELEM_CLASSES = 1;
  static final int ELEM_EXCEPTIONS = 2;
  static final int ELEM_PATTERNS = 3;
  static final int ELEM_HYPHEN = 4;

  public void parse(InputStream paramInputStream, PatternConsumer paramPatternConsumer)
  {
    this.consumer = paramPatternConsumer;
    try
    {
      SimpleXMLParser.parse(this, paramInputStream);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    finally
    {
      try
      {
        paramInputStream.close();
      }
      catch (Exception localException2)
      {
      }
    }
  }

  protected static String getPattern(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      if (Character.isDigit(paramString.charAt(j)))
        continue;
      localStringBuffer.append(paramString.charAt(j));
    }
    return localStringBuffer.toString();
  }

  protected ArrayList normalizeException(ArrayList paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      Object localObject = paramArrayList.get(i);
      if ((localObject instanceof String))
      {
        String str = (String)localObject;
        StringBuffer localStringBuffer = new StringBuffer();
        for (int j = 0; j < str.length(); j++)
        {
          char c = str.charAt(j);
          if (c != this.hyphenChar)
          {
            localStringBuffer.append(c);
          }
          else
          {
            localArrayList.add(localStringBuffer.toString());
            localStringBuffer.setLength(0);
            char[] arrayOfChar = new char[1];
            arrayOfChar[0] = this.hyphenChar;
            localArrayList.add(new Hyphen(new String(arrayOfChar), null, null));
          }
        }
        if (localStringBuffer.length() <= 0)
          continue;
        localArrayList.add(localStringBuffer.toString());
      }
      else
      {
        localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }

  protected String getExceptionWord(ArrayList paramArrayList)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      Object localObject = paramArrayList.get(i);
      if ((localObject instanceof String))
      {
        localStringBuffer.append((String)localObject);
      }
      else
      {
        if (((Hyphen)localObject).noBreak == null)
          continue;
        localStringBuffer.append(((Hyphen)localObject).noBreak);
      }
    }
    return localStringBuffer.toString();
  }

  protected static String getInterletterValues(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    String str = paramString + "a";
    int i = str.length();
    for (int j = 0; j < i; j++)
    {
      char c = str.charAt(j);
      if (Character.isDigit(c))
      {
        localStringBuffer.append(c);
        j++;
      }
      else
      {
        localStringBuffer.append('0');
      }
    }
    return localStringBuffer.toString();
  }

  public void endDocument()
  {
  }

  public void endElement(String paramString)
  {
    if (this.token.length() > 0)
    {
      String str = this.token.toString();
      switch (this.currElement)
      {
      case 1:
        this.consumer.addClass(str);
        break;
      case 2:
        this.exception.add(str);
        this.exception = normalizeException(this.exception);
        this.consumer.addException(getExceptionWord(this.exception), (ArrayList)this.exception.clone());
        break;
      case 3:
        this.consumer.addPattern(getPattern(str), getInterletterValues(str));
        break;
      case 4:
      }
      if (this.currElement != 4)
        this.token.setLength(0);
    }
    if (this.currElement == 4)
      this.currElement = 2;
    else
      this.currElement = 0;
  }

  public void startDocument()
  {
  }

  public void startElement(String paramString, HashMap paramHashMap)
  {
    if (paramString.equals("hyphen-char"))
    {
      String str = (String)paramHashMap.get("value");
      if ((str != null) && (str.length() == 1))
        this.hyphenChar = str.charAt(0);
    }
    else if (paramString.equals("classes"))
    {
      this.currElement = 1;
    }
    else if (paramString.equals("patterns"))
    {
      this.currElement = 3;
    }
    else if (paramString.equals("exceptions"))
    {
      this.currElement = 2;
      this.exception = new ArrayList();
    }
    else if (paramString.equals("hyphen"))
    {
      if (this.token.length() > 0)
        this.exception.add(this.token.toString());
      this.exception.add(new Hyphen((String)paramHashMap.get("pre"), (String)paramHashMap.get("no"), (String)paramHashMap.get("post")));
      this.currElement = 4;
    }
    this.token.setLength(0);
  }

  public void text(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      switch (this.currElement)
      {
      case 1:
        this.consumer.addClass(str);
        break;
      case 2:
        this.exception.add(str);
        this.exception = normalizeException(this.exception);
        this.consumer.addException(getExceptionWord(this.exception), (ArrayList)this.exception.clone());
        this.exception.clear();
        break;
      case 3:
        this.consumer.addPattern(getPattern(str), getInterletterValues(str));
      }
    }
  }

  public void addClass(String paramString)
  {
    System.out.println("class: " + paramString);
  }

  public void addException(String paramString, ArrayList paramArrayList)
  {
    System.out.println("exception: " + paramString + " : " + paramArrayList.toString());
  }

  public void addPattern(String paramString1, String paramString2)
  {
    System.out.println("pattern: " + paramString1 + " : " + paramString2);
  }

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    try
    {
      if (paramArrayOfString.length > 0)
      {
        SimplePatternParser localSimplePatternParser = new SimplePatternParser();
        localSimplePatternParser.parse(new FileInputStream(paramArrayOfString[0]), localSimplePatternParser);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.SimplePatternParser
 * JD-Core Version:    0.6.0
 */