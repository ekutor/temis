package com.lowagie.text.pdf.fonts.cmaps;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMapParser
{
  private static final String BEGIN_CODESPACE_RANGE = "begincodespacerange";
  private static final String BEGIN_BASE_FONT_CHAR = "beginbfchar";
  private static final String BEGIN_BASE_FONT_RANGE = "beginbfrange";
  private static final String MARK_END_OF_DICTIONARY = ">>";
  private static final String MARK_END_OF_ARRAY = "]";
  private byte[] tokenParserByteBuffer = new byte[512];

  public CMap parse(InputStream paramInputStream)
    throws IOException
  {
    PushbackInputStream localPushbackInputStream = new PushbackInputStream(paramInputStream);
    CMap localCMap = new CMap();
    Object localObject1 = null;
    Object localObject2 = null;
    while ((localObject2 = parseNextToken(localPushbackInputStream)) != null)
    {
      if ((localObject2 instanceof Operator))
      {
        Operator localOperator = (Operator)localObject2;
        Number localNumber;
        int i;
        byte[] arrayOfByte1;
        Object localObject3;
        Object localObject4;
        if (localOperator.op.equals("begincodespacerange"))
        {
          localNumber = (Number)localObject1;
          for (i = 0; i < localNumber.intValue(); i++)
          {
            arrayOfByte1 = (byte[])parseNextToken(localPushbackInputStream);
            localObject3 = (byte[])parseNextToken(localPushbackInputStream);
            localObject4 = new CodespaceRange();
            ((CodespaceRange)localObject4).setStart(arrayOfByte1);
            ((CodespaceRange)localObject4).setEnd(localObject3);
            localCMap.addCodespaceRange((CodespaceRange)localObject4);
          }
        }
        Object localObject5;
        if (localOperator.op.equals("beginbfchar"))
        {
          localNumber = (Number)localObject1;
          for (i = 0; i < localNumber.intValue(); i++)
          {
            arrayOfByte1 = (byte[])parseNextToken(localPushbackInputStream);
            localObject3 = parseNextToken(localPushbackInputStream);
            if ((localObject3 instanceof byte[]))
            {
              localObject4 = (byte[])localObject3;
              localObject5 = createStringFromBytes(localObject4);
              localCMap.addMapping(arrayOfByte1, (String)localObject5);
            }
            else if ((localObject3 instanceof LiteralName))
            {
              localCMap.addMapping(arrayOfByte1, ((LiteralName)localObject3).name);
            }
            else
            {
              throw new IOException("Error parsing CMap beginbfchar, expected{COSString or COSName} and not " + localObject3);
            }
          }
        }
        if (localOperator.op.equals("beginbfrange"))
        {
          localNumber = (Number)localObject1;
          for (i = 0; i < localNumber.intValue(); i++)
          {
            arrayOfByte1 = (byte[])parseNextToken(localPushbackInputStream);
            localObject3 = (byte[])parseNextToken(localPushbackInputStream);
            localObject4 = parseNextToken(localPushbackInputStream);
            localObject5 = null;
            byte[] arrayOfByte2 = null;
            if ((localObject4 instanceof List))
            {
              localObject5 = (List)localObject4;
              arrayOfByte2 = (byte[])((List)localObject5).get(0);
            }
            else
            {
              arrayOfByte2 = (byte[])localObject4;
            }
            String str = null;
            int j = 0;
            int k = 0;
            while (k == 0)
            {
              if (compare(arrayOfByte1, localObject3) >= 0)
                k = 1;
              str = createStringFromBytes(arrayOfByte2);
              localCMap.addMapping(arrayOfByte1, str);
              increment(arrayOfByte1);
              if (localObject5 == null)
              {
                increment(arrayOfByte2);
                continue;
              }
              j++;
              if (j >= ((List)localObject5).size())
                continue;
              arrayOfByte2 = (byte[])((List)localObject5).get(j);
            }
          }
        }
      }
      localObject1 = localObject2;
    }
    return (CMap)(CMap)(CMap)localCMap;
  }

  private Object parseNextToken(PushbackInputStream paramPushbackInputStream)
    throws IOException
  {
    Object localObject1 = null;
    for (int i = paramPushbackInputStream.read(); (i == 9) || (i == 32) || (i == 13) || (i == 10); i = paramPushbackInputStream.read());
    StringBuffer localStringBuffer1;
    Object localObject2;
    int n;
    StringBuffer localStringBuffer2;
    switch (i)
    {
    case 37:
      localStringBuffer1 = new StringBuffer();
      localStringBuffer1.append((char)i);
      readUntilEndOfLine(paramPushbackInputStream, localStringBuffer1);
      localObject1 = localStringBuffer1.toString();
      break;
    case 40:
      localStringBuffer1 = new StringBuffer();
      for (int m = paramPushbackInputStream.read(); (m != -1) && (m != 41); m = paramPushbackInputStream.read())
        localStringBuffer1.append((char)m);
      localObject1 = localStringBuffer1.toString();
      break;
    case 62:
      int j = paramPushbackInputStream.read();
      if (j == 62)
        localObject1 = ">>";
      else
        throw new IOException("Error: expected the end of a dictionary.");
    case 93:
      localObject1 = "]";
      break;
    case 91:
      ArrayList localArrayList = new ArrayList();
      for (localObject2 = parseNextToken(paramPushbackInputStream); localObject2 != "]"; localObject2 = parseNextToken(paramPushbackInputStream))
        localArrayList.add(localObject2);
      localObject1 = localArrayList;
      break;
    case 60:
      int k = paramPushbackInputStream.read();
      if (k == 60)
      {
        localObject2 = new HashMap();
        for (Object localObject3 = parseNextToken(paramPushbackInputStream); ((localObject3 instanceof LiteralName)) && (localObject3 != ">>"); localObject3 = parseNextToken(paramPushbackInputStream))
        {
          Object localObject4 = parseNextToken(paramPushbackInputStream);
          ((Map)localObject2).put(((LiteralName)localObject3).name, localObject4);
        }
        localObject1 = localObject2;
      }
      else
      {
        n = 16;
        int i1 = -1;
        while ((k != -1) && (k != 62))
        {
          int i2 = 0;
          if ((k >= 48) && (k <= 57))
            i2 = k - 48;
          else if ((k >= 65) && (k <= 70))
            i2 = 10 + k - 65;
          else if ((k >= 97) && (k <= 102))
            i2 = 10 + k - 97;
          else
            throw new IOException("Error: expected hex character and not " + (char)k + ":" + k);
          i2 *= n;
          if (n == 16)
          {
            i1++;
            this.tokenParserByteBuffer[i1] = 0;
            n = 1;
          }
          else
          {
            n = 16;
          }
          int tmp634_632 = i1;
          byte[] tmp634_629 = this.tokenParserByteBuffer;
          tmp634_629[tmp634_632] = (byte)(tmp634_629[tmp634_632] + i2);
          k = paramPushbackInputStream.read();
        }
        byte[] arrayOfByte = new byte[i1 + 1];
        System.arraycopy(this.tokenParserByteBuffer, 0, arrayOfByte, 0, i1 + 1);
        localObject1 = arrayOfByte;
      }
      break;
    case 47:
      localStringBuffer2 = new StringBuffer();
      for (n = paramPushbackInputStream.read(); !isWhitespaceOrEOF(n); n = paramPushbackInputStream.read())
        localStringBuffer2.append((char)n);
      localObject1 = new LiteralName(localStringBuffer2.toString(), null);
      break;
    case -1:
      break;
    case 48:
    case 49:
    case 50:
    case 51:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
      localStringBuffer2 = new StringBuffer();
      localStringBuffer2.append((char)i);
      for (i = paramPushbackInputStream.read(); (!isWhitespaceOrEOF(i)) && ((Character.isDigit((char)i)) || (i == 46)); i = paramPushbackInputStream.read())
        localStringBuffer2.append((char)i);
      paramPushbackInputStream.unread(i);
      String str = localStringBuffer2.toString();
      if (str.indexOf('.') >= 0)
        localObject1 = new Double(str);
      else
        localObject1 = new Integer(localStringBuffer2.toString());
      break;
    default:
      localStringBuffer2 = new StringBuffer();
      localStringBuffer2.append((char)i);
      for (i = paramPushbackInputStream.read(); !isWhitespaceOrEOF(i); i = paramPushbackInputStream.read())
        localStringBuffer2.append((char)i);
      localObject1 = new Operator(localStringBuffer2.toString(), null);
    }
    return localObject1;
  }

  private void readUntilEndOfLine(InputStream paramInputStream, StringBuffer paramStringBuffer)
    throws IOException
  {
    for (int i = paramInputStream.read(); (i != -1) && (i != 13) && (i != 10); i = paramInputStream.read())
      paramStringBuffer.append((char)i);
  }

  private boolean isWhitespaceOrEOF(int paramInt)
  {
    return (paramInt == -1) || (paramInt == 32) || (paramInt == 13) || (paramInt == 10);
  }

  private void increment(byte[] paramArrayOfByte)
  {
    increment(paramArrayOfByte, paramArrayOfByte.length - 1);
  }

  private void increment(byte[] paramArrayOfByte, int paramInt)
  {
    if ((paramInt > 0) && ((paramArrayOfByte[paramInt] + 256) % 256 == 255))
    {
      paramArrayOfByte[paramInt] = 0;
      increment(paramArrayOfByte, paramInt - 1);
    }
    else
    {
      paramArrayOfByte[paramInt] = (byte)(paramArrayOfByte[paramInt] + 1);
    }
  }

  private String createStringFromBytes(byte[] paramArrayOfByte)
    throws IOException
  {
    String str = null;
    if (paramArrayOfByte.length == 1)
      str = new String(paramArrayOfByte);
    else
      str = new String(paramArrayOfByte, "UTF-16BE");
    return str;
  }

  private int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i = 1;
    int j = 0;
    for (int k = 0; (k < paramArrayOfByte1.length) && (j == 0); k++)
    {
      if (paramArrayOfByte1[k] == paramArrayOfByte2[k])
        continue;
      if ((paramArrayOfByte1[k] + 256) % 256 < (paramArrayOfByte2[k] + 256) % 256)
      {
        j = 1;
        i = -1;
      }
      else
      {
        j = 1;
        i = 1;
      }
    }
    return i;
  }

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    if (paramArrayOfString.length != 1)
    {
      System.err.println("usage: java org.pdfbox.cmapparser.CMapParser <CMAP File>");
      System.exit(-1);
    }
    CMapParser localCMapParser = new CMapParser();
    CMap localCMap = localCMapParser.parse(new FileInputStream(paramArrayOfString[0]));
    System.out.println("Result:" + localCMap);
  }

  private class Operator
  {
    private String op;
    private final CMapParser this$0;

    private Operator(String arg2)
    {
      Object localObject;
      this.op = localObject;
    }

    Operator(String param1, CMapParser.1 arg3)
    {
      this(param1);
    }
  }

  private class LiteralName
  {
    private String name;
    private final CMapParser this$0;

    private LiteralName(String arg2)
    {
      Object localObject;
      this.name = localObject;
    }

    LiteralName(String param1, CMapParser.1 arg3)
    {
      this(param1);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.fonts.cmaps.CMapParser
 * JD-Core Version:    0.6.0
 */