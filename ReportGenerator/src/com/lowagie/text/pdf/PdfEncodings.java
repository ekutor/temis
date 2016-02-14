package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PdfEncodings
{
  protected static final int CIDNONE = 0;
  protected static final int CIDRANGE = 1;
  protected static final int CIDCHAR = 2;
  static final char[] winansiByteToChar = { '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '', '€', 65533, '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', 65533, 'Ž', 65533, 65533, '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', 65533, 'ž', 'Ÿ', ' ', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '­', '®', '¯', '°', '±', '²', '³', '´', 'µ', '¶', '·', '¸', '¹', 'º', '»', '¼', '½', '¾', '¿', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï', 'Ð', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', '×', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'Þ', 'ß', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï', 'ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', '÷', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'þ', 'ÿ' };
  static final char[] pdfEncodingByteToChar = { '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '', '•', '†', '‡', '…', '—', '–', 'ƒ', '⁄', '‹', '›', '−', '‰', '„', '“', '”', '‘', '’', '‚', '™', 64257, 64258, 'Ł', 'Œ', 'Š', 'Ÿ', 'Ž', 'ı', 'ł', 'œ', 'š', 'ž', 65533, '€', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '­', '®', '¯', '°', '±', '²', '³', '´', 'µ', '¶', '·', '¸', '¹', 'º', '»', '¼', '½', '¾', '¿', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï', 'Ð', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', '×', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'Þ', 'ß', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï', 'ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', '÷', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'þ', 'ÿ' };
  static final IntHashtable winansi = new IntHashtable();
  static final IntHashtable pdfEncoding = new IntHashtable();
  static HashMap extraEncodings = new HashMap();
  static final HashMap cmaps;
  public static final byte[][] CRLF_CID_NEWLINE;

  public static final byte[] convertToBytes(String paramString1, String paramString2)
  {
    if (paramString1 == null)
      return new byte[0];
    if ((paramString2 == null) || (paramString2.length() == 0))
    {
      int i = paramString1.length();
      localObject = new byte[i];
      for (int j = 0; j < i; j++)
        localObject[j] = (byte)paramString1.charAt(j);
      return localObject;
    }
    ExtraEncoding localExtraEncoding = (ExtraEncoding)extraEncodings.get(paramString2.toLowerCase());
    if (localExtraEncoding != null)
    {
      localObject = localExtraEncoding.charToByte(paramString1, paramString2);
      if (localObject != null)
        return localObject;
    }
    Object localObject = null;
    if (paramString2.equals("Cp1252"))
      localObject = winansi;
    else if (paramString2.equals("PDF"))
      localObject = pdfEncoding;
    char[] arrayOfChar;
    int k;
    int i1;
    if (localObject != null)
    {
      arrayOfChar = paramString1.toCharArray();
      k = arrayOfChar.length;
      int m = 0;
      byte[] arrayOfByte2 = new byte[k];
      i1 = 0;
      for (int i2 = 0; i2 < k; i2++)
      {
        int i4 = arrayOfChar[i2];
        if ((i4 < 128) || ((i4 > 160) && (i4 <= 255)))
          i1 = i4;
        else
          i1 = ((IntHashtable)localObject).get(i4);
        if (i1 == 0)
          continue;
        arrayOfByte2[(m++)] = (byte)i1;
      }
      if (m == k)
        return arrayOfByte2;
      byte[] arrayOfByte3 = new byte[m];
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 0, m);
      return arrayOfByte3;
    }
    if (paramString2.equals("UnicodeBig"))
    {
      arrayOfChar = paramString1.toCharArray();
      k = arrayOfChar.length;
      byte[] arrayOfByte1 = new byte[arrayOfChar.length * 2 + 2];
      arrayOfByte1[0] = -2;
      arrayOfByte1[1] = -1;
      int n = 2;
      for (i1 = 0; i1 < k; i1++)
      {
        int i3 = arrayOfChar[i1];
        arrayOfByte1[(n++)] = (byte)(i3 >> 8);
        arrayOfByte1[(n++)] = (byte)(i3 & 0xFF);
      }
      return arrayOfByte1;
    }
    try
    {
      return paramString1.getBytes(paramString2);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    throw new ExceptionConverter(localUnsupportedEncodingException);
  }

  public static final byte[] convertToBytes(char paramChar, String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return new byte[] { (byte)paramChar };
    ExtraEncoding localExtraEncoding = (ExtraEncoding)extraEncodings.get(paramString.toLowerCase());
    if (localExtraEncoding != null)
    {
      localObject = localExtraEncoding.charToByte(paramChar, paramString);
      if (localObject != null)
        return localObject;
    }
    Object localObject = null;
    if (paramString.equals("Cp1252"))
      localObject = winansi;
    else if (paramString.equals("PDF"))
      localObject = pdfEncoding;
    if (localObject != null)
    {
      int i = 0;
      if ((paramChar < '') || ((paramChar > ' ') && (paramChar <= 'ÿ')))
        i = paramChar;
      else
        i = ((IntHashtable)localObject).get(paramChar);
      if (i != 0)
        return new byte[] { (byte)i };
      return new byte[0];
    }
    if (paramString.equals("UnicodeBig"))
    {
      byte[] arrayOfByte = new byte[4];
      arrayOfByte[0] = -2;
      arrayOfByte[1] = -1;
      arrayOfByte[2] = (byte)(paramChar >> '\b');
      arrayOfByte[3] = (byte)(paramChar & 0xFF);
      return arrayOfByte;
    }
    try
    {
      return String.valueOf(paramChar).getBytes(paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    throw new ExceptionConverter(localUnsupportedEncodingException);
  }

  public static final String convertToString(byte[] paramArrayOfByte, String paramString)
  {
    if (paramArrayOfByte == null)
      return "";
    if ((paramString == null) || (paramString.length() == 0))
    {
      localObject1 = new char[paramArrayOfByte.length];
      for (int i = 0; i < paramArrayOfByte.length; i++)
        localObject1[i] = (char)(paramArrayOfByte[i] & 0xFF);
      return new String(localObject1);
    }
    Object localObject1 = (ExtraEncoding)extraEncodings.get(paramString.toLowerCase());
    if (localObject1 != null)
    {
      localObject2 = ((ExtraEncoding)localObject1).byteToChar(paramArrayOfByte, paramString);
      if (localObject2 != null)
        return localObject2;
    }
    Object localObject2 = null;
    if (paramString.equals("Cp1252"))
      localObject2 = winansiByteToChar;
    else if (paramString.equals("PDF"))
      localObject2 = pdfEncodingByteToChar;
    if (localObject2 != null)
    {
      int j = paramArrayOfByte.length;
      char[] arrayOfChar = new char[j];
      for (int k = 0; k < j; k++)
        arrayOfChar[k] = localObject2[(paramArrayOfByte[k] & 0xFF)];
      return new String(arrayOfChar);
    }
    try
    {
      return new String(paramArrayOfByte, paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    throw new ExceptionConverter(localUnsupportedEncodingException);
  }

  public static boolean isPdfDocEncoding(String paramString)
  {
    if (paramString == null)
      return true;
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if ((k >= 128) && ((k <= 160) || (k > 255)) && (!pdfEncoding.containsKey(k)))
        return false;
    }
    return true;
  }

  public static void clearCmap(String paramString)
  {
    synchronized (cmaps)
    {
      if (paramString.length() == 0)
        cmaps.clear();
      else
        cmaps.remove(paramString);
    }
  }

  public static void loadCmap(String paramString, byte[][] paramArrayOfByte)
  {
    try
    {
      char[][] arrayOfChar = (char[][])null;
      synchronized (cmaps)
      {
        arrayOfChar = (char[][])cmaps.get(paramString);
      }
      if (arrayOfChar == null)
      {
        arrayOfChar = readCmap(paramString, paramArrayOfByte);
        synchronized (cmaps)
        {
          cmaps.put(paramString, arrayOfChar);
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public static String convertCmap(String paramString, byte[] paramArrayOfByte)
  {
    return convertCmap(paramString, paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public static String convertCmap(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      char[][] arrayOfChar = (char[][])null;
      synchronized (cmaps)
      {
        arrayOfChar = (char[][])cmaps.get(paramString);
      }
      if (arrayOfChar == null)
      {
        arrayOfChar = readCmap(paramString, (byte[][])(byte[][])null);
        synchronized (cmaps)
        {
          cmaps.put(paramString, arrayOfChar);
        }
      }
      return decodeSequence(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar);
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  static String decodeSequence(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[][] paramArrayOfChar)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramInt1 + paramInt2;
    int j = 0;
    for (int k = paramInt1; k < i; k++)
    {
      int m = paramArrayOfByte[k] & 0xFF;
      char[] arrayOfChar = paramArrayOfChar[j];
      int n = arrayOfChar[m];
      if ((n & 0x8000) == 0)
      {
        localStringBuffer.append((char)n);
        j = 0;
      }
      else
      {
        j = n & 0x7FFF;
      }
    }
    return localStringBuffer.toString();
  }

  static char[][] readCmap(String paramString, byte[][] paramArrayOfByte)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new char[256]);
    readCmap(paramString, localArrayList);
    if (paramArrayOfByte != null)
      for (int i = 0; i < paramArrayOfByte.length; i++)
        encodeSequence(paramArrayOfByte[i].length, paramArrayOfByte[i], '翿', localArrayList);
    char[][] arrayOfChar = new char[localArrayList.size()][];
    return (char[][])localArrayList.toArray(arrayOfChar);
  }

  static void readCmap(String paramString, ArrayList paramArrayList)
    throws IOException
  {
    String str = "com/lowagie/text/pdf/fonts/cmaps/" + paramString;
    InputStream localInputStream = BaseFont.getResourceStream(str);
    if (localInputStream == null)
      throw new IOException("The Cmap " + paramString + " was not found.");
    encodeStream(localInputStream, paramArrayList);
    localInputStream.close();
  }

  static void encodeStream(InputStream paramInputStream, ArrayList paramArrayList)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "iso-8859-1"));
    String str1 = null;
    int i = 0;
    byte[] arrayOfByte = new byte[7];
    while ((str1 = localBufferedReader.readLine()) != null)
    {
      if (str1.length() < 6)
        continue;
      StringTokenizer localStringTokenizer;
      String str2;
      int j;
      long l1;
      long l2;
      int m;
      long l3;
      switch (i)
      {
      case 0:
        if (str1.indexOf("begincidrange") >= 0)
        {
          i = 1;
        }
        else if (str1.indexOf("begincidchar") >= 0)
        {
          i = 2;
        }
        else
        {
          if (str1.indexOf("usecmap") < 0)
            break;
          localStringTokenizer = new StringTokenizer(str1);
          str2 = localStringTokenizer.nextToken();
          readCmap(str2.substring(1), paramArrayList);
        }
        break;
      case 1:
        if (str1.indexOf("endcidrange") >= 0)
        {
          i = 0;
        }
        else
        {
          localStringTokenizer = new StringTokenizer(str1);
          str2 = localStringTokenizer.nextToken();
          j = str2.length() / 2 - 1;
          l1 = Long.parseLong(str2.substring(1, str2.length() - 1), 16);
          str2 = localStringTokenizer.nextToken();
          l2 = Long.parseLong(str2.substring(1, str2.length() - 1), 16);
          str2 = localStringTokenizer.nextToken();
          m = Integer.parseInt(str2);
          l3 = l1;
        }
      case 2:
        while (l3 <= l2)
        {
          breakLong(l3, j, arrayOfByte);
          encodeSequence(j, arrayOfByte, (char)m, paramArrayList);
          m++;
          l3 += 1L;
          continue;
          if (str1.indexOf("endcidchar") >= 0)
          {
            i = 0;
          }
          else
          {
            localStringTokenizer = new StringTokenizer(str1);
            str2 = localStringTokenizer.nextToken();
            j = str2.length() / 2 - 1;
            l1 = Long.parseLong(str2.substring(1, str2.length() - 1), 16);
            str2 = localStringTokenizer.nextToken();
            int k = Integer.parseInt(str2);
            breakLong(l1, j, arrayOfByte);
            encodeSequence(j, arrayOfByte, (char)k, paramArrayList);
          }
        }
      }
    }
  }

  static void breakLong(long paramLong, int paramInt, byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramInt; i++)
      paramArrayOfByte[i] = (byte)(int)(paramLong >> (paramInt - 1 - i) * 8);
  }

  static void encodeSequence(int paramInt, byte[] paramArrayOfByte, char paramChar, ArrayList paramArrayList)
  {
    paramInt--;
    int i = 0;
    for (int j = 0; j < paramInt; j++)
    {
      char[] arrayOfChar2 = (char[])paramArrayList.get(i);
      m = paramArrayOfByte[j] & 0xFF;
      int n = arrayOfChar2[m];
      if ((n != 0) && ((n & 0x8000) == 0))
        throw new RuntimeException("Inconsistent mapping.");
      if (n == 0)
      {
        paramArrayList.add(new char[256]);
        n = (char)(paramArrayList.size() - 1 | 0x8000);
        arrayOfChar2[m] = n;
      }
      i = n & 0x7FFF;
    }
    char[] arrayOfChar1 = (char[])paramArrayList.get(i);
    int k = paramArrayOfByte[paramInt] & 0xFF;
    int m = arrayOfChar1[k];
    if ((m & 0x8000) != 0)
      throw new RuntimeException("Inconsistent mapping.");
    arrayOfChar1[k] = paramChar;
  }

  public static void addExtraEncoding(String paramString, ExtraEncoding paramExtraEncoding)
  {
    synchronized (extraEncodings)
    {
      HashMap localHashMap = (HashMap)extraEncodings.clone();
      localHashMap.put(paramString.toLowerCase(), paramExtraEncoding);
      extraEncodings = localHashMap;
    }
  }

  static
  {
    int j;
    for (int i = 128; i < 161; i++)
    {
      j = winansiByteToChar[i];
      if (j == 65533)
        continue;
      winansi.put(j, i);
    }
    for (i = 128; i < 161; i++)
    {
      j = pdfEncodingByteToChar[i];
      if (j == 65533)
        continue;
      pdfEncoding.put(j, i);
    }
    addExtraEncoding("Wingdings", new WingdingsConversion(null));
    addExtraEncoding("Symbol", new SymbolConversion(true));
    addExtraEncoding("ZapfDingbats", new SymbolConversion(false));
    addExtraEncoding("SymbolTT", new SymbolTTConversion(null));
    addExtraEncoding("Cp437", new Cp437Conversion(null));
    cmaps = new HashMap();
    CRLF_CID_NEWLINE = new byte[][] { { 10 }, { 13, 10 } };
  }

  private static class SymbolTTConversion
    implements ExtraEncoding
  {
    private SymbolTTConversion()
    {
    }

    public byte[] charToByte(char paramChar, String paramString)
    {
      if (((paramChar & 0xFF00) == 0) || ((paramChar & 0xFF00) == 61440))
        return new byte[] { (byte)paramChar };
      return new byte[0];
    }

    public byte[] charToByte(String paramString1, String paramString2)
    {
      char[] arrayOfChar = paramString1.toCharArray();
      byte[] arrayOfByte1 = new byte[arrayOfChar.length];
      int i = 0;
      int j = arrayOfChar.length;
      for (int k = 0; k < j; k++)
      {
        int m = arrayOfChar[k];
        if (((m & 0xFF00) != 0) && ((m & 0xFF00) != 61440))
          continue;
        arrayOfByte1[(i++)] = (byte)m;
      }
      if (i == j)
        return arrayOfByte1;
      byte[] arrayOfByte2 = new byte[i];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
      return arrayOfByte2;
    }

    public String byteToChar(byte[] paramArrayOfByte, String paramString)
    {
      return null;
    }

    SymbolTTConversion(PdfEncodings.1 param1)
    {
      this();
    }
  }

  private static class SymbolConversion
    implements ExtraEncoding
  {
    private static final IntHashtable t1 = new IntHashtable();
    private static final IntHashtable t2 = new IntHashtable();
    private IntHashtable translation;
    private static final char[] table1 = { ' ', '!', '∀', '#', '∃', '%', '&', '∋', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '≅', 'Α', 'Β', 'Χ', 'Δ', 'Ε', 'Φ', 'Γ', 'Η', 'Ι', 'ϑ', 'Κ', 'Λ', 'Μ', 'Ν', 'Ο', 'Π', 'Θ', 'Ρ', 'Σ', 'Τ', 'Υ', 'ς', 'Ω', 'Ξ', 'Ψ', 'Ζ', '[', '∴', ']', '⊥', '_', '̅', 'α', 'β', 'χ', 'δ', 'ε', 'ϕ', 'γ', 'η', 'ι', 'φ', 'κ', 'λ', 'μ', 'ν', 'ο', 'π', 'θ', 'ρ', 'σ', 'τ', 'υ', 'ϖ', 'ω', 'ξ', 'ψ', 'ζ', '{', '|', '}', '~', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '€', 'ϒ', '′', '≤', '⁄', '∞', 'ƒ', '♣', '♦', '♥', '♠', '↔', '←', '↑', '→', '↓', '°', '±', '″', '≥', '×', '∝', '∂', '•', '÷', '≠', '≡', '≈', '…', '│', '─', '↵', 'ℵ', 'ℑ', 'ℜ', '℘', '⊗', '⊕', '∅', '∩', '∪', '⊃', '⊇', '⊄', '⊂', '⊆', '∈', '∉', '∠', '∇', '®', '©', '™', '∏', '√', '•', '¬', '∧', '∨', '⇔', '⇐', '⇑', '⇒', '⇓', '◊', '〈', '\000', '\000', '\000', '∑', '⎛', '⎜', '⎝', '⎡', '⎢', '⎣', '⎧', '⎨', '⎩', '⎪', '\000', '〉', '∫', '⌠', '⎮', '⌡', '⎞', '⎟', '⎠', '⎤', '⎥', '⎦', '⎫', '⎬', '⎭', '\000' };
    private static final char[] table2 = { ' ', '✁', '✂', '✃', '✄', '☎', '✆', '✇', '✈', '✉', '☛', '☞', '✌', '✍', '✎', '✏', '✐', '✑', '✒', '✓', '✔', '✕', '✖', '✗', '✘', '✙', '✚', '✛', '✜', '✝', '✞', '✟', '✠', '✡', '✢', '✣', '✤', '✥', '✦', '✧', '★', '✩', '✪', '✫', '✬', '✭', '✮', '✯', '✰', '✱', '✲', '✳', '✴', '✵', '✶', '✷', '✸', '✹', '✺', '✻', '✼', '✽', '✾', '✿', '❀', '❁', '❂', '❃', '❄', '❅', '❆', '❇', '❈', '❉', '❊', '❋', '●', '❍', '■', '❏', '❐', '❑', '❒', '▲', '▼', '◆', '❖', '◗', '❘', '❙', '❚', '❛', '❜', '❝', '❞', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '❡', '❢', '❣', '❤', '❥', '❦', '❧', '♣', '♦', '♥', '♠', '①', '②', '③', '④', '⑤', '⑥', '⑦', '⑧', '⑨', '⑩', '❶', '❷', '❸', '❹', '❺', '❻', '❼', '❽', '❾', '❿', '➀', '➁', '➂', '➃', '➄', '➅', '➆', '➇', '➈', '➉', '➊', '➋', '➌', '➍', '➎', '➏', '➐', '➑', '➒', '➓', '➔', '→', '↔', '↕', '➘', '➙', '➚', '➛', '➜', '➝', '➞', '➟', '➠', '➡', '➢', '➣', '➤', '➥', '➦', '➧', '➨', '➩', '➪', '➫', '➬', '➭', '➮', '➯', '\000', '➱', '➲', '➳', '➴', '➵', '➶', '➷', '➸', '➹', '➺', '➻', '➼', '➽', '➾', '\000' };

    SymbolConversion(boolean paramBoolean)
    {
      if (paramBoolean)
        this.translation = t1;
      else
        this.translation = t2;
    }

    public byte[] charToByte(String paramString1, String paramString2)
    {
      char[] arrayOfChar = paramString1.toCharArray();
      byte[] arrayOfByte1 = new byte[arrayOfChar.length];
      int i = 0;
      int j = arrayOfChar.length;
      for (int k = 0; k < j; k++)
      {
        int m = arrayOfChar[k];
        int n = (byte)this.translation.get(m);
        if (n == 0)
          continue;
        arrayOfByte1[(i++)] = n;
      }
      if (i == j)
        return arrayOfByte1;
      byte[] arrayOfByte2 = new byte[i];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
      return arrayOfByte2;
    }

    public byte[] charToByte(char paramChar, String paramString)
    {
      int i = (byte)this.translation.get(paramChar);
      if (i != 0)
        return new byte[] { i };
      return new byte[0];
    }

    public String byteToChar(byte[] paramArrayOfByte, String paramString)
    {
      return null;
    }

    static
    {
      int j;
      for (int i = 0; i < table1.length; i++)
      {
        j = table1[i];
        if (j == 0)
          continue;
        t1.put(j, i + 32);
      }
      for (i = 0; i < table2.length; i++)
      {
        j = table2[i];
        if (j == 0)
          continue;
        t2.put(j, i + 32);
      }
    }
  }

  private static class Cp437Conversion
    implements ExtraEncoding
  {
    private static IntHashtable c2b = new IntHashtable();
    private static final char[] table = { 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', '¢', '£', '¥', '₧', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', '¬', '½', '¼', '¡', '«', '»', '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐', '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧', '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀', 'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩', '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', ' ' };

    private Cp437Conversion()
    {
    }

    public byte[] charToByte(String paramString1, String paramString2)
    {
      char[] arrayOfChar = paramString1.toCharArray();
      byte[] arrayOfByte1 = new byte[arrayOfChar.length];
      int i = 0;
      int j = arrayOfChar.length;
      for (int k = 0; k < j; k++)
      {
        int m = arrayOfChar[k];
        if (m < 128)
        {
          arrayOfByte1[(i++)] = (byte)m;
        }
        else
        {
          int n = (byte)c2b.get(m);
          if (n == 0)
            continue;
          arrayOfByte1[(i++)] = n;
        }
      }
      if (i == j)
        return arrayOfByte1;
      byte[] arrayOfByte2 = new byte[i];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
      return arrayOfByte2;
    }

    public byte[] charToByte(char paramChar, String paramString)
    {
      if (paramChar < '')
        return new byte[] { (byte)paramChar };
      int i = (byte)c2b.get(paramChar);
      if (i != 0)
        return new byte[] { i };
      return new byte[0];
    }

    public String byteToChar(byte[] paramArrayOfByte, String paramString)
    {
      int i = paramArrayOfByte.length;
      char[] arrayOfChar = new char[i];
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        int m = paramArrayOfByte[k] & 0xFF;
        if (m < 32)
          continue;
        if (m < 128)
        {
          arrayOfChar[(j++)] = (char)m;
        }
        else
        {
          int n = table[(m - 128)];
          arrayOfChar[(j++)] = n;
        }
      }
      return new String(arrayOfChar, 0, j);
    }

    Cp437Conversion(PdfEncodings.1 param1)
    {
      this();
    }

    static
    {
      for (int i = 0; i < table.length; i++)
        c2b.put(table[i], i + 128);
    }
  }

  private static class WingdingsConversion
    implements ExtraEncoding
  {
    private static final byte[] table = { 0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private WingdingsConversion()
    {
    }

    public byte[] charToByte(char paramChar, String paramString)
    {
      if (paramChar == ' ')
        return new byte[] { (byte)paramChar };
      if ((paramChar >= '✁') && (paramChar <= '➾'))
      {
        int i = table[(paramChar - '✀')];
        if (i != 0)
          return new byte[] { i };
      }
      return new byte[0];
    }

    public byte[] charToByte(String paramString1, String paramString2)
    {
      char[] arrayOfChar = paramString1.toCharArray();
      byte[] arrayOfByte1 = new byte[arrayOfChar.length];
      int i = 0;
      int j = arrayOfChar.length;
      for (int k = 0; k < j; k++)
      {
        int m = arrayOfChar[k];
        if (m == 32)
        {
          arrayOfByte1[(i++)] = (byte)m;
        }
        else
        {
          if ((m < 9985) || (m > 10174))
            continue;
          int n = table[(m - 9984)];
          if (n == 0)
            continue;
          arrayOfByte1[(i++)] = n;
        }
      }
      if (i == j)
        return arrayOfByte1;
      byte[] arrayOfByte2 = new byte[i];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
      return arrayOfByte2;
    }

    public String byteToChar(byte[] paramArrayOfByte, String paramString)
    {
      return null;
    }

    WingdingsConversion(PdfEncodings.1 param1)
    {
      this();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfEncodings
 * JD-Core Version:    0.6.0
 */