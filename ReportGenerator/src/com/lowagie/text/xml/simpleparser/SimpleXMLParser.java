package com.lowagie.text.xml.simpleparser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Stack;

public final class SimpleXMLParser
{
  private static final int UNKNOWN = 0;
  private static final int TEXT = 1;
  private static final int TAG_ENCOUNTERED = 2;
  private static final int EXAMIN_TAG = 3;
  private static final int TAG_EXAMINED = 4;
  private static final int IN_CLOSETAG = 5;
  private static final int SINGLE_TAG = 6;
  private static final int CDATA = 7;
  private static final int COMMENT = 8;
  private static final int PI = 9;
  private static final int ENTITY = 10;
  private static final int QUOTE = 11;
  private static final int ATTRIBUTE_KEY = 12;
  private static final int ATTRIBUTE_EQUAL = 13;
  private static final int ATTRIBUTE_VALUE = 14;
  Stack stack;
  int character = 0;
  int previousCharacter = -1;
  int lines = 1;
  int columns = 0;
  boolean eol = false;
  boolean nowhite = false;
  int state;
  boolean html;
  StringBuffer text = new StringBuffer();
  StringBuffer entity = new StringBuffer();
  String tag = null;
  HashMap attributes = null;
  SimpleXMLDocHandler doc;
  SimpleXMLDocHandlerComment comment;
  int nested = 0;
  int quoteCharacter = 34;
  String attributekey = null;
  String attributevalue = null;

  private SimpleXMLParser(SimpleXMLDocHandler paramSimpleXMLDocHandler, SimpleXMLDocHandlerComment paramSimpleXMLDocHandlerComment, boolean paramBoolean)
  {
    this.doc = paramSimpleXMLDocHandler;
    this.comment = paramSimpleXMLDocHandlerComment;
    this.html = paramBoolean;
    this.stack = new Stack();
    this.state = (paramBoolean ? 1 : 0);
  }

  private void go(Reader paramReader)
    throws IOException
  {
    BufferedReader localBufferedReader;
    if ((paramReader instanceof BufferedReader))
      localBufferedReader = (BufferedReader)paramReader;
    else
      localBufferedReader = new BufferedReader(paramReader);
    this.doc.startDocument();
    while (true)
    {
      if (this.previousCharacter == -1)
      {
        this.character = localBufferedReader.read();
      }
      else
      {
        this.character = this.previousCharacter;
        this.previousCharacter = -1;
      }
      if (this.character == -1)
      {
        if (this.html)
        {
          if ((this.html) && (this.state == 1))
            flush();
          this.doc.endDocument();
        }
        else
        {
          throwException("Missing end tag");
        }
        return;
      }
      if ((this.character == 10) && (this.eol))
      {
        this.eol = false;
        continue;
      }
      if (this.eol)
      {
        this.eol = false;
      }
      else if (this.character == 10)
      {
        this.lines += 1;
        this.columns = 0;
      }
      else if (this.character == 13)
      {
        this.eol = true;
        this.character = 10;
        this.lines += 1;
        this.columns = 0;
      }
      else
      {
        this.columns += 1;
      }
      switch (this.state)
      {
      case 0:
        if (this.character != 60)
          break;
        saveState(1);
        this.state = 2;
        break;
      case 1:
        if (this.character == 60)
        {
          flush();
          saveState(this.state);
          this.state = 2;
        }
        else if (this.character == 38)
        {
          saveState(this.state);
          this.entity.setLength(0);
          this.state = 10;
        }
        else if (Character.isWhitespace((char)this.character))
        {
          if (this.nowhite)
            this.text.append((char)this.character);
          this.nowhite = false;
        }
        else
        {
          this.text.append((char)this.character);
          this.nowhite = true;
        }
        break;
      case 2:
        initTag();
        if (this.character == 47)
        {
          this.state = 5;
        }
        else if (this.character == 63)
        {
          restoreState();
          this.state = 9;
        }
        else
        {
          this.text.append((char)this.character);
          this.state = 3;
        }
        break;
      case 3:
        if (this.character == 62)
        {
          doTag();
          processTag(true);
          initTag();
          this.state = restoreState();
        }
        else if (this.character == 47)
        {
          this.state = 6;
        }
        else if ((this.character == 45) && (this.text.toString().equals("!-")))
        {
          flush();
          this.state = 8;
        }
        else if ((this.character == 91) && (this.text.toString().equals("![CDATA")))
        {
          flush();
          this.state = 7;
        }
        else if ((this.character == 69) && (this.text.toString().equals("!DOCTYP")))
        {
          flush();
          this.state = 9;
        }
        else if (Character.isWhitespace((char)this.character))
        {
          doTag();
          this.state = 4;
        }
        else
        {
          this.text.append((char)this.character);
        }
        break;
      case 4:
        if (this.character == 62)
        {
          processTag(true);
          initTag();
          this.state = restoreState();
        }
        else if (this.character == 47)
        {
          this.state = 6;
        }
        else
        {
          if (Character.isWhitespace((char)this.character))
            break;
          this.text.append((char)this.character);
          this.state = 12;
        }
        break;
      case 5:
        if (this.character == 62)
        {
          doTag();
          processTag(false);
          if ((!this.html) && (this.nested == 0))
            return;
          this.state = restoreState();
        }
        else
        {
          if (Character.isWhitespace((char)this.character))
            break;
          this.text.append((char)this.character);
        }
        break;
      case 6:
        if (this.character != 62)
          throwException("Expected > for tag: <" + this.tag + "/>");
        doTag();
        processTag(true);
        processTag(false);
        initTag();
        if ((!this.html) && (this.nested == 0))
        {
          this.doc.endDocument();
          return;
        }
        this.state = restoreState();
        break;
      case 7:
        if ((this.character == 62) && (this.text.toString().endsWith("]]")))
        {
          this.text.setLength(this.text.length() - 2);
          flush();
          this.state = restoreState();
        }
        else
        {
          this.text.append((char)this.character);
        }
        break;
      case 8:
        if ((this.character == 62) && (this.text.toString().endsWith("--")))
        {
          this.text.setLength(this.text.length() - 2);
          flush();
          this.state = restoreState();
        }
        else
        {
          this.text.append((char)this.character);
        }
        break;
      case 9:
        if (this.character != 62)
          break;
        this.state = restoreState();
        if (this.state != 1)
          break;
        this.state = 0;
        break;
      case 10:
        if (this.character == 59)
        {
          this.state = restoreState();
          String str = this.entity.toString();
          this.entity.setLength(0);
          char c = EntitiesToUnicode.decodeEntity(str);
          if (c == 0)
            this.text.append('&').append(str).append(';');
          else
            this.text.append(c);
        }
        else if (((this.character != 35) && ((this.character < 48) || (this.character > 57)) && ((this.character < 97) || (this.character > 122)) && ((this.character < 65) || (this.character > 90))) || (this.entity.length() >= 7))
        {
          this.state = restoreState();
          this.previousCharacter = this.character;
          this.text.append('&').append(this.entity.toString());
          this.entity.setLength(0);
        }
        else
        {
          this.entity.append((char)this.character);
        }
        break;
      case 11:
        if ((this.html) && (this.quoteCharacter == 32) && (this.character == 62))
        {
          flush();
          processTag(true);
          initTag();
          this.state = restoreState();
        }
        else if ((this.html) && (this.quoteCharacter == 32) && (Character.isWhitespace((char)this.character)))
        {
          flush();
          this.state = 4;
        }
        else if ((this.html) && (this.quoteCharacter == 32))
        {
          this.text.append((char)this.character);
        }
        else if (this.character == this.quoteCharacter)
        {
          flush();
          this.state = 4;
        }
        else if (" \r\n\t".indexOf(this.character) >= 0)
        {
          this.text.append(' ');
        }
        else if (this.character == 38)
        {
          saveState(this.state);
          this.state = 10;
          this.entity.setLength(0);
        }
        else
        {
          this.text.append((char)this.character);
        }
        break;
      case 12:
        if (Character.isWhitespace((char)this.character))
        {
          flush();
          this.state = 13;
        }
        else if (this.character == 61)
        {
          flush();
          this.state = 14;
        }
        else if ((this.html) && (this.character == 62))
        {
          this.text.setLength(0);
          processTag(true);
          initTag();
          this.state = restoreState();
        }
        else
        {
          this.text.append((char)this.character);
        }
        break;
      case 13:
        if (this.character == 61)
        {
          this.state = 14;
        }
        else
        {
          if (Character.isWhitespace((char)this.character))
            break;
          if ((this.html) && (this.character == 62))
          {
            this.text.setLength(0);
            processTag(true);
            initTag();
            this.state = restoreState();
          }
          else if ((this.html) && (this.character == 47))
          {
            flush();
            this.state = 6;
          }
          else if (this.html)
          {
            flush();
            this.text.append((char)this.character);
            this.state = 12;
          }
          else
          {
            throwException("Error in attribute processing.");
          }
        }
        break;
      case 14:
        if ((this.character == 34) || (this.character == 39))
        {
          this.quoteCharacter = this.character;
          this.state = 11;
        }
        else
        {
          if (Character.isWhitespace((char)this.character))
            break;
          if ((this.html) && (this.character == 62))
          {
            flush();
            processTag(true);
            initTag();
            this.state = restoreState();
          }
          else if (this.html)
          {
            this.text.append((char)this.character);
            this.quoteCharacter = 32;
            this.state = 11;
          }
          else
          {
            throwException("Error in attribute processing");
          }
        }
      }
    }
  }

  private int restoreState()
  {
    if (!this.stack.empty())
      return ((Integer)this.stack.pop()).intValue();
    return 0;
  }

  private void saveState(int paramInt)
  {
    this.stack.push(new Integer(paramInt));
  }

  private void flush()
  {
    switch (this.state)
    {
    case 1:
    case 7:
      if (this.text.length() <= 0)
        break;
      this.doc.text(this.text.toString());
      break;
    case 8:
      if (this.comment == null)
        break;
      this.comment.comment(this.text.toString());
      break;
    case 12:
      this.attributekey = this.text.toString();
      if (!this.html)
        break;
      this.attributekey = this.attributekey.toLowerCase();
      break;
    case 11:
    case 14:
      this.attributevalue = this.text.toString();
      this.attributes.put(this.attributekey, this.attributevalue);
      break;
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 9:
    case 10:
    case 13:
    }
    this.text.setLength(0);
  }

  private void initTag()
  {
    this.tag = null;
    this.attributes = new HashMap();
  }

  private void doTag()
  {
    if (this.tag == null)
      this.tag = this.text.toString();
    if (this.html)
      this.tag = this.tag.toLowerCase();
    this.text.setLength(0);
  }

  private void processTag(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.nested += 1;
      this.doc.startElement(this.tag, this.attributes);
    }
    else
    {
      this.nested -= 1;
      this.doc.endElement(this.tag);
    }
  }

  private void throwException(String paramString)
    throws IOException
  {
    throw new IOException(paramString + " near line " + this.lines + ", column " + this.columns);
  }

  public static void parse(SimpleXMLDocHandler paramSimpleXMLDocHandler, SimpleXMLDocHandlerComment paramSimpleXMLDocHandlerComment, Reader paramReader, boolean paramBoolean)
    throws IOException
  {
    SimpleXMLParser localSimpleXMLParser = new SimpleXMLParser(paramSimpleXMLDocHandler, paramSimpleXMLDocHandlerComment, paramBoolean);
    localSimpleXMLParser.go(paramReader);
  }

  public static void parse(SimpleXMLDocHandler paramSimpleXMLDocHandler, InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[4];
    int i = paramInputStream.read(arrayOfByte);
    if (i != 4)
      throw new IOException("Insufficient length.");
    Object localObject1 = getEncodingName(arrayOfByte);
    String str = null;
    Object localObject2;
    int j;
    if (((String)localObject1).equals("UTF-8"))
    {
      localObject2 = new StringBuffer();
      while (((j = paramInputStream.read()) != -1) && (j != 62))
        ((StringBuffer)localObject2).append((char)j);
      str = ((StringBuffer)localObject2).toString();
    }
    else if (((String)localObject1).equals("CP037"))
    {
      localObject2 = new ByteArrayOutputStream();
      while (((j = paramInputStream.read()) != -1) && (j != 110))
        ((ByteArrayOutputStream)localObject2).write(j);
      str = new String(((ByteArrayOutputStream)localObject2).toByteArray(), "CP037");
    }
    if (str != null)
    {
      str = getDeclaredEncoding(str);
      if (str != null)
        localObject1 = str;
    }
    parse(paramSimpleXMLDocHandler, new InputStreamReader(paramInputStream, IanaEncodings.getJavaEncoding((String)localObject1)));
  }

  private static String getDeclaredEncoding(String paramString)
  {
    if (paramString == null)
      return null;
    int i = paramString.indexOf("encoding");
    if (i < 0)
      return null;
    int j = paramString.indexOf('"', i);
    int k = paramString.indexOf('\'', i);
    if (j == k)
      return null;
    int m;
    if (((j < 0) && (k > 0)) || ((k > 0) && (k < j)))
    {
      m = paramString.indexOf('\'', k + 1);
      if (m < 0)
        return null;
      return paramString.substring(k + 1, m);
    }
    if (((k < 0) && (j > 0)) || ((j > 0) && (j < k)))
    {
      m = paramString.indexOf('"', j + 1);
      if (m < 0)
        return null;
      return paramString.substring(j + 1, m);
    }
    return null;
  }

  public static void parse(SimpleXMLDocHandler paramSimpleXMLDocHandler, Reader paramReader)
    throws IOException
  {
    parse(paramSimpleXMLDocHandler, null, paramReader, false);
  }

  public static String escapeXML(String paramString, boolean paramBoolean)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    StringBuffer localStringBuffer = new StringBuffer();
    for (int j = 0; j < i; j++)
    {
      int k = arrayOfChar[j];
      switch (k)
      {
      case 60:
        localStringBuffer.append("&lt;");
        break;
      case 62:
        localStringBuffer.append("&gt;");
        break;
      case 38:
        localStringBuffer.append("&amp;");
        break;
      case 34:
        localStringBuffer.append("&quot;");
        break;
      case 39:
        localStringBuffer.append("&apos;");
        break;
      default:
        if ((k != 9) && (k != 10) && (k != 13) && ((k < 32) || (k > 55295)) && ((k < 57344) || (k > 65533)) && ((k < 65536) || (k > 1114111)))
          continue;
        if ((paramBoolean) && (k > 127))
          localStringBuffer.append("&#").append(k).append(';');
        else
          localStringBuffer.append((char)k);
      }
    }
    return localStringBuffer.toString();
  }

  private static String getEncodingName(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte[0] & 0xFF;
    int j = paramArrayOfByte[1] & 0xFF;
    if ((i == 254) && (j == 255))
      return "UTF-16BE";
    if ((i == 255) && (j == 254))
      return "UTF-16LE";
    int k = paramArrayOfByte[2] & 0xFF;
    if ((i == 239) && (j == 187) && (k == 191))
      return "UTF-8";
    int m = paramArrayOfByte[3] & 0xFF;
    if ((i == 0) && (j == 0) && (k == 0) && (m == 60))
      return "ISO-10646-UCS-4";
    if ((i == 60) && (j == 0) && (k == 0) && (m == 0))
      return "ISO-10646-UCS-4";
    if ((i == 0) && (j == 0) && (k == 60) && (m == 0))
      return "ISO-10646-UCS-4";
    if ((i == 0) && (j == 60) && (k == 0) && (m == 0))
      return "ISO-10646-UCS-4";
    if ((i == 0) && (j == 60) && (k == 0) && (m == 63))
      return "UTF-16BE";
    if ((i == 60) && (j == 0) && (k == 63) && (m == 0))
      return "UTF-16LE";
    if ((i == 76) && (j == 111) && (k == 167) && (m == 148))
      return "CP037";
    return "UTF-8";
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.simpleparser.SimpleXMLParser
 * JD-Core Version:    0.6.0
 */