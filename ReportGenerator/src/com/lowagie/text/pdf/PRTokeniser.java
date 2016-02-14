package com.lowagie.text.pdf;

import java.io.IOException;

public class PRTokeniser
{
  public static final int TK_NUMBER = 1;
  public static final int TK_STRING = 2;
  public static final int TK_NAME = 3;
  public static final int TK_COMMENT = 4;
  public static final int TK_START_ARRAY = 5;
  public static final int TK_END_ARRAY = 6;
  public static final int TK_START_DIC = 7;
  public static final int TK_END_DIC = 8;
  public static final int TK_REF = 9;
  public static final int TK_OTHER = 10;
  public static final boolean[] delims = { true, true, false, false, false, false, false, false, false, false, true, true, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
  static final String EMPTY = "";
  protected RandomAccessFileOrArray file;
  protected int type;
  protected String stringValue;
  protected int reference;
  protected int generation;
  protected boolean hexString;

  public PRTokeniser(String paramString)
    throws IOException
  {
    this.file = new RandomAccessFileOrArray(paramString);
  }

  public PRTokeniser(byte[] paramArrayOfByte)
  {
    this.file = new RandomAccessFileOrArray(paramArrayOfByte);
  }

  public PRTokeniser(RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    this.file = paramRandomAccessFileOrArray;
  }

  public void seek(int paramInt)
    throws IOException
  {
    this.file.seek(paramInt);
  }

  public int getFilePointer()
    throws IOException
  {
    return this.file.getFilePointer();
  }

  public void close()
    throws IOException
  {
    this.file.close();
  }

  public int length()
    throws IOException
  {
    return this.file.length();
  }

  public int read()
    throws IOException
  {
    return this.file.read();
  }

  public RandomAccessFileOrArray getSafeFile()
  {
    return new RandomAccessFileOrArray(this.file);
  }

  public RandomAccessFileOrArray getFile()
  {
    return this.file;
  }

  public String readString(int paramInt)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    while (paramInt-- > 0)
    {
      int i = this.file.read();
      if (i == -1)
        break;
      localStringBuffer.append((char)i);
    }
    return localStringBuffer.toString();
  }

  public static final boolean isWhitespace(int paramInt)
  {
    return (paramInt == 0) || (paramInt == 9) || (paramInt == 10) || (paramInt == 12) || (paramInt == 13) || (paramInt == 32);
  }

  public static final boolean isDelimiter(int paramInt)
  {
    return (paramInt == 40) || (paramInt == 41) || (paramInt == 60) || (paramInt == 62) || (paramInt == 91) || (paramInt == 93) || (paramInt == 47) || (paramInt == 37);
  }

  public static final boolean isDelimiterWhitespace(int paramInt)
  {
    return delims[(paramInt + 1)];
  }

  public int getTokenType()
  {
    return this.type;
  }

  public String getStringValue()
  {
    return this.stringValue;
  }

  public int getReference()
  {
    return this.reference;
  }

  public int getGeneration()
  {
    return this.generation;
  }

  public void backOnePosition(int paramInt)
  {
    if (paramInt != -1)
      this.file.pushBack((byte)paramInt);
  }

  public void throwError(String paramString)
    throws IOException
  {
    throw new IOException(paramString + " at file pointer " + this.file.getFilePointer());
  }

  public char checkPdfHeader()
    throws IOException
  {
    this.file.setStartOffset(0);
    String str = readString(1024);
    int i = str.indexOf("%PDF-");
    if (i < 0)
      throw new IOException("PDF header signature not found.");
    this.file.setStartOffset(i);
    return str.charAt(i + 7);
  }

  public void checkFdfHeader()
    throws IOException
  {
    this.file.setStartOffset(0);
    String str = readString(1024);
    int i = str.indexOf("%FDF-1.2");
    if (i < 0)
      throw new IOException("FDF header signature not found.");
    this.file.setStartOffset(i);
  }

  public int getStartxref()
    throws IOException
  {
    int i = Math.min(1024, this.file.length());
    int j = this.file.length() - i;
    this.file.seek(j);
    String str = readString(1024);
    int k = str.lastIndexOf("startxref");
    if (k < 0)
      throw new IOException("PDF startxref not found.");
    return j + k;
  }

  public static int getHex(int paramInt)
  {
    if ((paramInt >= 48) && (paramInt <= 57))
      return paramInt - 48;
    if ((paramInt >= 65) && (paramInt <= 70))
      return paramInt - 65 + 10;
    if ((paramInt >= 97) && (paramInt <= 102))
      return paramInt - 97 + 10;
    return -1;
  }

  public void nextValidToken()
    throws IOException
  {
    int i = 0;
    String str1 = null;
    String str2 = null;
    int j = 0;
    while (nextToken())
    {
      if (this.type == 4)
        continue;
      switch (i)
      {
      case 0:
        if (this.type != 1)
          return;
        j = this.file.getFilePointer();
        str1 = this.stringValue;
        i++;
        break;
      case 1:
        if (this.type != 1)
        {
          this.file.seek(j);
          this.type = 1;
          this.stringValue = str1;
          return;
        }
        str2 = this.stringValue;
        i++;
        break;
      default:
        if ((this.type != 10) || (!this.stringValue.equals("R")))
        {
          this.file.seek(j);
          this.type = 1;
          this.stringValue = str1;
          return;
        }
        this.type = 9;
        this.reference = Integer.parseInt(str1);
        this.generation = Integer.parseInt(str2);
        return;
      }
    }
  }

  public boolean nextToken()
    throws IOException
  {
    int i = 0;
    do
      i = this.file.read();
    while ((i != -1) && (isWhitespace(i)));
    if (i == -1)
      return false;
    StringBuffer localStringBuffer = null;
    this.stringValue = "";
    int j;
    int k;
    switch (i)
    {
    case 91:
      this.type = 5;
      break;
    case 93:
      this.type = 6;
      break;
    case 47:
      localStringBuffer = new StringBuffer();
      this.type = 3;
      while (true)
      {
        i = this.file.read();
        if (delims[(i + 1)] != 0)
          break;
        if (i == 35)
          i = (getHex(this.file.read()) << 4) + getHex(this.file.read());
        localStringBuffer.append((char)i);
      }
      backOnePosition(i);
      break;
    case 62:
      i = this.file.read();
      if (i != 62)
        throwError("'>' not expected");
      this.type = 8;
      break;
    case 60:
      j = this.file.read();
      if (j == 60)
      {
        this.type = 7;
      }
      else
      {
        localStringBuffer = new StringBuffer();
        this.type = 2;
        this.hexString = true;
        k = 0;
        while (true)
        {
          if (isWhitespace(j))
          {
            j = this.file.read();
            continue;
          }
          if (j == 62)
            break;
          j = getHex(j);
          if (j < 0)
            break;
          for (k = this.file.read(); isWhitespace(k); k = this.file.read());
          if (k == 62)
          {
            i = j << 4;
            localStringBuffer.append((char)i);
            break;
          }
          k = getHex(k);
          if (k < 0)
            break;
          i = (j << 4) + k;
          localStringBuffer.append((char)i);
          j = this.file.read();
        }
        if ((j >= 0) && (k >= 0))
          break;
        throwError("Error reading string");
      }
      break;
    case 37:
      this.type = 4;
    case 40:
    default:
      while (true)
      {
        i = this.file.read();
        if ((i == -1) || (i == 13))
          break;
        if (i != 10)
          continue;
        break;
        localStringBuffer = new StringBuffer();
        this.type = 2;
        this.hexString = false;
        j = 0;
        while (true)
        {
          i = this.file.read();
          if (i == -1)
            break;
          if (i == 40)
          {
            j++;
          }
          else if (i == 41)
          {
            j--;
          }
          else if (i == 92)
          {
            k = 0;
            i = this.file.read();
            switch (i)
            {
            case 110:
              i = 10;
              break;
            case 114:
              i = 13;
              break;
            case 116:
              i = 9;
              break;
            case 98:
              i = 8;
              break;
            case 102:
              i = 12;
              break;
            case 40:
            case 41:
            case 92:
              break;
            case 13:
              k = 1;
              i = this.file.read();
              if (i == 10)
                break;
              backOnePosition(i);
              break;
            case 10:
              k = 1;
              break;
            default:
              if ((i < 48) || (i > 55))
                break;
              int m = i - 48;
              i = this.file.read();
              if ((i < 48) || (i > 55))
              {
                backOnePosition(i);
                i = m;
              }
              else
              {
                m = (m << 3) + i - 48;
                i = this.file.read();
                if ((i < 48) || (i > 55))
                {
                  backOnePosition(i);
                  i = m;
                }
                else
                {
                  m = (m << 3) + i - 48;
                  i = m & 0xFF;
                }
              }
            }
            if (k != 0)
              continue;
            if (i < 0)
              break;
          }
          else if (i == 13)
          {
            i = this.file.read();
            if (i < 0)
              break;
            if (i != 10)
            {
              backOnePosition(i);
              i = 10;
            }
          }
          if (j == -1)
            break;
          localStringBuffer.append((char)i);
        }
        if (i != -1)
          break;
        throwError("Error reading string");
        break;
        localStringBuffer = new StringBuffer();
        if ((i == 45) || (i == 43) || (i == 46) || ((i >= 48) && (i <= 57)))
          this.type = 1;
        while (true)
        {
          localStringBuffer.append((char)i);
          i = this.file.read();
          if (i == -1)
            break;
          if (((i >= 48) && (i <= 57)) || (i == 46))
            continue;
          break;
          this.type = 10;
          do
          {
            localStringBuffer.append((char)i);
            i = this.file.read();
          }
          while (delims[(i + 1)] == 0);
        }
        backOnePosition(i);
      }
    }
    if (localStringBuffer != null)
      this.stringValue = localStringBuffer.toString();
    return true;
  }

  public int intValue()
  {
    return Integer.parseInt(this.stringValue);
  }

  public boolean readLineSegment(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = -1;
    int j = 0;
    int k = 0;
    int m = paramArrayOfByte.length;
    while ((k < m) && (isWhitespace(i = read())));
    int n;
    while ((j == 0) && (k < m))
    {
      switch (i)
      {
      case -1:
      case 10:
        j = 1;
        break;
      case 13:
        j = 1;
        n = getFilePointer();
        if (read() == 10)
          break;
        seek(n);
        break;
      default:
        paramArrayOfByte[(k++)] = (byte)i;
      }
      if ((j != 0) || (m <= k))
        break;
      i = read();
    }
    if (k >= m)
    {
      j = 0;
      while (j == 0)
        switch (i = read())
        {
        case -1:
        case 10:
          j = 1;
          break;
        case 13:
          j = 1;
          n = getFilePointer();
          if (read() == 10)
            break;
          seek(n);
        }
    }
    if ((i == -1) && (k == 0))
      return false;
    if (k + 2 <= m)
    {
      paramArrayOfByte[(k++)] = 32;
      paramArrayOfByte[k] = 88;
    }
    return true;
  }

  public static int[] checkObjectStart(byte[] paramArrayOfByte)
  {
    try
    {
      PRTokeniser localPRTokeniser = new PRTokeniser(paramArrayOfByte);
      int i = 0;
      int j = 0;
      if ((!localPRTokeniser.nextToken()) || (localPRTokeniser.getTokenType() != 1))
        return null;
      i = localPRTokeniser.intValue();
      if ((!localPRTokeniser.nextToken()) || (localPRTokeniser.getTokenType() != 1))
        return null;
      j = localPRTokeniser.intValue();
      if (!localPRTokeniser.nextToken())
        return null;
      if (!localPRTokeniser.getStringValue().equals("obj"))
        return null;
      return new int[] { i, j };
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public boolean isHexString()
  {
    return this.hexString;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PRTokeniser
 * JD-Core Version:    0.6.0
 */