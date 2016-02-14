package com.lowagie.text.pdf.hyphenation;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class HyphenationTree extends TernaryTree
  implements PatternConsumer
{
  private static final long serialVersionUID = -7763254239309429432L;
  protected ByteVector vspace = new ByteVector();
  protected HashMap stoplist = new HashMap(23);
  protected TernaryTree classmap = new TernaryTree();
  private transient TernaryTree ivalues;

  public HyphenationTree()
  {
    this.vspace.alloc(1);
  }

  protected int packValues(String paramString)
  {
    int j = paramString.length();
    int k = (j & 0x1) == 1 ? (j >> 1) + 2 : (j >> 1) + 1;
    int m = this.vspace.alloc(k);
    byte[] arrayOfByte = this.vspace.getArray();
    for (int i = 0; i < j; i++)
    {
      int n = i >> 1;
      int i1 = (byte)(paramString.charAt(i) - '0' + 1 & 0xF);
      if ((i & 0x1) == 1)
        arrayOfByte[(n + m)] = (byte)(arrayOfByte[(n + m)] | i1);
      else
        arrayOfByte[(n + m)] = (byte)(i1 << 4);
    }
    arrayOfByte[(k - 1 + m)] = 0;
    return m;
  }

  protected String unpackValues(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = this.vspace.get(paramInt++); i != 0; i = this.vspace.get(paramInt++))
    {
      char c = (char)((i >>> 4) - 1 + 48);
      localStringBuffer.append(c);
      c = (char)(i & 0xF);
      if (c == 0)
        break;
      c = (char)(c - '\001' + 48);
      localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }

  public void loadSimplePatterns(InputStream paramInputStream)
  {
    SimplePatternParser localSimplePatternParser = new SimplePatternParser();
    this.ivalues = new TernaryTree();
    localSimplePatternParser.parse(paramInputStream, this);
    trimToSize();
    this.vspace.trimToSize();
    this.classmap.trimToSize();
    this.ivalues = null;
  }

  public String findPattern(String paramString)
  {
    int i = super.find(paramString);
    if (i >= 0)
      return unpackValues(i);
    return "";
  }

  protected int hstrcmp(char[] paramArrayOfChar1, int paramInt1, char[] paramArrayOfChar2, int paramInt2)
  {
    while (paramArrayOfChar1[paramInt1] == paramArrayOfChar2[paramInt2])
    {
      if (paramArrayOfChar1[paramInt1] == 0)
        return 0;
      paramInt1++;
      paramInt2++;
    }
    if (paramArrayOfChar2[paramInt2] == 0)
      return 0;
    return paramArrayOfChar1[paramInt1] - paramArrayOfChar2[paramInt2];
  }

  protected byte[] getValues(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = this.vspace.get(paramInt++); i != 0; i = this.vspace.get(paramInt++))
    {
      char c = (char)((i >>> 4) - 1);
      localStringBuffer.append(c);
      c = (char)(i & 0xF);
      if (c == 0)
        break;
      c = (char)(c - '\001');
      localStringBuffer.append(c);
    }
    byte[] arrayOfByte = new byte[localStringBuffer.length()];
    for (int j = 0; j < arrayOfByte.length; j++)
      arrayOfByte[j] = (byte)localStringBuffer.charAt(j);
    return arrayOfByte;
  }

  protected void searchPatterns(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
  {
    int i = paramInt;
    int m = paramArrayOfChar[i];
    int j = this.root;
    while ((j > 0) && (j < this.sc.length))
    {
      byte[] arrayOfByte;
      int i1;
      if (this.sc[j] == 65535)
      {
        if (hstrcmp(paramArrayOfChar, i, this.kv.getArray(), this.lo[j]) == 0)
        {
          arrayOfByte = getValues(this.eq[j]);
          n = paramInt;
          for (i1 = 0; i1 < arrayOfByte.length; i1++)
          {
            if ((n < paramArrayOfByte.length) && (arrayOfByte[i1] > paramArrayOfByte[n]))
              paramArrayOfByte[n] = arrayOfByte[i1];
            n++;
          }
        }
        return;
      }
      int n = m - this.sc[j];
      if (n == 0)
      {
        if (m == 0)
          break;
        i++;
        m = paramArrayOfChar[i];
        j = this.eq[j];
        for (int k = j; ; k = this.lo[k])
        {
          if ((k <= 0) || (k >= this.sc.length) || (this.sc[k] == 65535))
            break label292;
          if (this.sc[k] != 0)
            continue;
          arrayOfByte = getValues(this.eq[k]);
          i1 = paramInt;
          for (int i2 = 0; i2 < arrayOfByte.length; i2++)
          {
            if ((i1 < paramArrayOfByte.length) && (arrayOfByte[i2] > paramArrayOfByte[i1]))
              paramArrayOfByte[i1] = arrayOfByte[i2];
            i1++;
          }
          break;
        }
        label292: continue;
      }
      j = n < 0 ? this.lo[j] : this.hi[j];
    }
  }

  public Hyphenation hyphenate(String paramString, int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = paramString.toCharArray();
    return hyphenate(arrayOfChar, 0, arrayOfChar.length, paramInt1, paramInt2);
  }

  public Hyphenation hyphenate(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    char[] arrayOfChar1 = new char[paramInt2 + 3];
    char[] arrayOfChar2 = new char[2];
    int j = 0;
    int k = paramInt2;
    int m = 0;
    for (int i = 1; i <= paramInt2; i++)
    {
      arrayOfChar2[0] = paramArrayOfChar[(paramInt1 + i - 1)];
      int n = this.classmap.find(arrayOfChar2, 0);
      if (n < 0)
      {
        if (i == 1 + j)
          j++;
        else
          m = 1;
        k--;
      }
      else if (m == 0)
      {
        arrayOfChar1[(i - j)] = (char)n;
      }
      else
      {
        return null;
      }
    }
    paramInt2 = k;
    if (paramInt2 < paramInt3 + paramInt4)
      return null;
    int[] arrayOfInt = new int[paramInt2 + 1];
    int i1 = 0;
    String str = new String(arrayOfChar1, 1, paramInt2);
    if (this.stoplist.containsKey(str))
    {
      localObject1 = (ArrayList)this.stoplist.get(str);
      int i2 = 0;
      for (i = 0; i < ((ArrayList)localObject1).size(); i++)
      {
        Object localObject2 = ((ArrayList)localObject1).get(i);
        if (!(localObject2 instanceof String))
          continue;
        i2 += ((String)localObject2).length();
        if ((i2 < paramInt3) || (i2 >= paramInt2 - paramInt4))
          continue;
        arrayOfInt[(i1++)] = (i2 + j);
      }
    }
    arrayOfChar1[0] = '.';
    arrayOfChar1[(paramInt2 + 1)] = '.';
    arrayOfChar1[(paramInt2 + 2)] = '\000';
    Object localObject1 = new byte[paramInt2 + 3];
    for (i = 0; i < paramInt2 + 1; i++)
      searchPatterns(arrayOfChar1, i, localObject1);
    for (i = 0; i < paramInt2; i++)
    {
      if (((localObject1[(i + 1)] & 0x1) != 1) || (i < paramInt3) || (i > paramInt2 - paramInt4))
        continue;
      arrayOfInt[(i1++)] = (i + j);
    }
    if (i1 > 0)
    {
      localObject1 = new int[i1];
      System.arraycopy(arrayOfInt, 0, localObject1, 0, i1);
      return new Hyphenation(new String(paramArrayOfChar, paramInt1, paramInt2), localObject1);
    }
    return (Hyphenation)null;
  }

  public void addClass(String paramString)
  {
    if (paramString.length() > 0)
    {
      char c = paramString.charAt(0);
      char[] arrayOfChar = new char[2];
      arrayOfChar[1] = '\000';
      for (int i = 0; i < paramString.length(); i++)
      {
        arrayOfChar[0] = paramString.charAt(i);
        this.classmap.insert(arrayOfChar, 0, c);
      }
    }
  }

  public void addException(String paramString, ArrayList paramArrayList)
  {
    this.stoplist.put(paramString, paramArrayList);
  }

  public void addPattern(String paramString1, String paramString2)
  {
    int i = this.ivalues.find(paramString2);
    if (i <= 0)
    {
      i = packValues(paramString2);
      this.ivalues.insert(paramString2, (char)i);
    }
    insert(paramString1, (char)i);
  }

  public void printStats()
  {
    System.out.println("Value space size = " + Integer.toString(this.vspace.length()));
    super.printStats();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.HyphenationTree
 * JD-Core Version:    0.6.0
 */