package com.lowagie.text.pdf.codec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Base64
{
  public static final int NO_OPTIONS = 0;
  public static final int ENCODE = 1;
  public static final int DECODE = 0;
  public static final int GZIP = 2;
  public static final int DONT_BREAK_LINES = 8;
  public static final int URL_SAFE = 16;
  public static final int ORDERED = 32;
  private static final int MAX_LINE_LENGTH = 76;
  private static final byte EQUALS_SIGN = 61;
  private static final byte NEW_LINE = 10;
  private static final String PREFERRED_ENCODING = "UTF-8";
  private static final byte WHITE_SPACE_ENC = -5;
  private static final byte EQUALS_SIGN_ENC = -1;
  private static final byte[] _STANDARD_ALPHABET = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
  private static final byte[] _STANDARD_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9 };
  private static final byte[] _URL_SAFE_ALPHABET = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
  private static final byte[] _URL_SAFE_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9 };
  private static final byte[] _ORDERED_ALPHABET = { 45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
  private static final byte[] _ORDERED_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9 };

  private static final byte[] getAlphabet(int paramInt)
  {
    if ((paramInt & 0x10) == 16)
      return _URL_SAFE_ALPHABET;
    if ((paramInt & 0x20) == 32)
      return _ORDERED_ALPHABET;
    return _STANDARD_ALPHABET;
  }

  private static final byte[] getDecodabet(int paramInt)
  {
    if ((paramInt & 0x10) == 16)
      return _URL_SAFE_DECODABET;
    if ((paramInt & 0x20) == 32)
      return _ORDERED_DECODABET;
    return _STANDARD_DECODABET;
  }

  public static final void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length < 3)
    {
      usage("Not enough arguments.");
    }
    else
    {
      String str1 = paramArrayOfString[0];
      String str2 = paramArrayOfString[1];
      String str3 = paramArrayOfString[2];
      if (str1.equals("-e"))
        encodeFileToFile(str2, str3);
      else if (str1.equals("-d"))
        decodeFileToFile(str2, str3);
      else
        usage("Unknown flag: " + str1);
    }
  }

  private static final void usage(String paramString)
  {
    System.err.println(paramString);
    System.err.println("Usage: java Base64 -e|-d inputfile outputfile");
  }

  private static byte[] encode3to4(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
  {
    encode3to4(paramArrayOfByte2, 0, paramInt1, paramArrayOfByte1, 0, paramInt2);
    return paramArrayOfByte1;
  }

  private static byte[] encode3to4(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
  {
    byte[] arrayOfByte = getAlphabet(paramInt4);
    int i = (paramInt2 > 0 ? paramArrayOfByte1[paramInt1] << 24 >>> 8 : 0) | (paramInt2 > 1 ? paramArrayOfByte1[(paramInt1 + 1)] << 24 >>> 16 : 0) | (paramInt2 > 2 ? paramArrayOfByte1[(paramInt1 + 2)] << 24 >>> 24 : 0);
    switch (paramInt2)
    {
    case 3:
      paramArrayOfByte2[paramInt3] = arrayOfByte[(i >>> 18)];
      paramArrayOfByte2[(paramInt3 + 1)] = arrayOfByte[(i >>> 12 & 0x3F)];
      paramArrayOfByte2[(paramInt3 + 2)] = arrayOfByte[(i >>> 6 & 0x3F)];
      paramArrayOfByte2[(paramInt3 + 3)] = arrayOfByte[(i & 0x3F)];
      return paramArrayOfByte2;
    case 2:
      paramArrayOfByte2[paramInt3] = arrayOfByte[(i >>> 18)];
      paramArrayOfByte2[(paramInt3 + 1)] = arrayOfByte[(i >>> 12 & 0x3F)];
      paramArrayOfByte2[(paramInt3 + 2)] = arrayOfByte[(i >>> 6 & 0x3F)];
      paramArrayOfByte2[(paramInt3 + 3)] = 61;
      return paramArrayOfByte2;
    case 1:
      paramArrayOfByte2[paramInt3] = arrayOfByte[(i >>> 18)];
      paramArrayOfByte2[(paramInt3 + 1)] = arrayOfByte[(i >>> 12 & 0x3F)];
      paramArrayOfByte2[(paramInt3 + 2)] = 61;
      paramArrayOfByte2[(paramInt3 + 3)] = 61;
      return paramArrayOfByte2;
    }
    return paramArrayOfByte2;
  }

  public static String encodeObject(Serializable paramSerializable)
  {
    return encodeObject(paramSerializable, 0);
  }

  public static String encodeObject(Serializable paramSerializable, int paramInt)
  {
    ByteArrayOutputStream localByteArrayOutputStream = null;
    OutputStream localOutputStream = null;
    ObjectOutputStream localObjectOutputStream = null;
    GZIPOutputStream localGZIPOutputStream = null;
    int i = paramInt & 0x2;
    int j = paramInt & 0x8;
    try
    {
      localByteArrayOutputStream = new ByteArrayOutputStream();
      localOutputStream = new OutputStream(localByteArrayOutputStream, 0x1 | paramInt);
      if (i == 2)
      {
        localGZIPOutputStream = new GZIPOutputStream(localOutputStream);
        localObjectOutputStream = new ObjectOutputStream(localGZIPOutputStream);
      }
      else
      {
        localObjectOutputStream = new ObjectOutputStream(localOutputStream);
      }
      localObjectOutputStream.writeObject(paramSerializable);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      Object localObject1 = null;
      return localObject1;
    }
    finally
    {
      try
      {
        localObjectOutputStream.close();
      }
      catch (Exception localException1)
      {
      }
      try
      {
        localGZIPOutputStream.close();
      }
      catch (Exception localException2)
      {
      }
      try
      {
        localOutputStream.close();
      }
      catch (Exception localException3)
      {
      }
      try
      {
        localByteArrayOutputStream.close();
      }
      catch (Exception localException4)
      {
      }
    }
    try
    {
      return new String(localByteArrayOutputStream.toByteArray(), "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    return new String(localByteArrayOutputStream.toByteArray());
  }

  public static String encodeBytes(byte[] paramArrayOfByte)
  {
    return encodeBytes(paramArrayOfByte, 0, paramArrayOfByte.length, 0);
  }

  public static String encodeBytes(byte[] paramArrayOfByte, int paramInt)
  {
    return encodeBytes(paramArrayOfByte, 0, paramArrayOfByte.length, paramInt);
  }

  public static String encodeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return encodeBytes(paramArrayOfByte, paramInt1, paramInt2, 0);
  }

  public static String encodeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3 & 0x8;
    int j = paramInt3 & 0x2;
    if (j == 2)
    {
      ByteArrayOutputStream localByteArrayOutputStream = null;
      GZIPOutputStream localGZIPOutputStream = null;
      localObject1 = null;
      try
      {
        localByteArrayOutputStream = new ByteArrayOutputStream();
        localObject1 = new OutputStream(localByteArrayOutputStream, 0x1 | paramInt3);
        localGZIPOutputStream = new GZIPOutputStream((OutputStream)localObject1);
        localGZIPOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
        localGZIPOutputStream.close();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        Object localObject2 = null;
        return localObject2;
      }
      finally
      {
        try
        {
          localGZIPOutputStream.close();
        }
        catch (Exception localException1)
        {
        }
        try
        {
          ((OutputStream)localObject1).close();
        }
        catch (Exception localException2)
        {
        }
        try
        {
          localByteArrayOutputStream.close();
        }
        catch (Exception localException3)
        {
        }
      }
      try
      {
        return new String(localByteArrayOutputStream.toByteArray(), "UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException1)
      {
        return new String(localByteArrayOutputStream.toByteArray());
      }
    }
    int k = i == 0 ? 1 : 0;
    int m = paramInt2 * 4 / 3;
    Object localObject1 = new byte[m + (paramInt2 % 3 > 0 ? 4 : 0) + (k != 0 ? m / 76 : 0)];
    int n = 0;
    int i1 = 0;
    int i2 = paramInt2 - 2;
    int i3 = 0;
    while (n < i2)
    {
      encode3to4(paramArrayOfByte, n + paramInt1, 3, localObject1, i1, paramInt3);
      i3 += 4;
      if ((k != 0) && (i3 == 76))
      {
        localObject1[(i1 + 4)] = 10;
        i1++;
        i3 = 0;
      }
      n += 3;
      i1 += 4;
    }
    if (n < paramInt2)
    {
      encode3to4(paramArrayOfByte, n + paramInt1, paramInt2 - n, localObject1, i1, paramInt3);
      i1 += 4;
    }
    try
    {
      return new String(localObject1, 0, i1, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException2)
    {
    }
    return (String)new String(localObject1, 0, i1);
  }

  private static int decode4to3(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte = getDecodabet(paramInt3);
    int i;
    if (paramArrayOfByte1[(paramInt1 + 2)] == 61)
    {
      i = (arrayOfByte[paramArrayOfByte1[paramInt1]] & 0xFF) << 18 | (arrayOfByte[paramArrayOfByte1[(paramInt1 + 1)]] & 0xFF) << 12;
      paramArrayOfByte2[paramInt2] = (byte)(i >>> 16);
      return 1;
    }
    if (paramArrayOfByte1[(paramInt1 + 3)] == 61)
    {
      i = (arrayOfByte[paramArrayOfByte1[paramInt1]] & 0xFF) << 18 | (arrayOfByte[paramArrayOfByte1[(paramInt1 + 1)]] & 0xFF) << 12 | (arrayOfByte[paramArrayOfByte1[(paramInt1 + 2)]] & 0xFF) << 6;
      paramArrayOfByte2[paramInt2] = (byte)(i >>> 16);
      paramArrayOfByte2[(paramInt2 + 1)] = (byte)(i >>> 8);
      return 2;
    }
    try
    {
      i = (arrayOfByte[paramArrayOfByte1[paramInt1]] & 0xFF) << 18 | (arrayOfByte[paramArrayOfByte1[(paramInt1 + 1)]] & 0xFF) << 12 | (arrayOfByte[paramArrayOfByte1[(paramInt1 + 2)]] & 0xFF) << 6 | arrayOfByte[paramArrayOfByte1[(paramInt1 + 3)]] & 0xFF;
      paramArrayOfByte2[paramInt2] = (byte)(i >> 16);
      paramArrayOfByte2[(paramInt2 + 1)] = (byte)(i >> 8);
      paramArrayOfByte2[(paramInt2 + 2)] = (byte)i;
      return 3;
    }
    catch (Exception localException)
    {
      System.out.println("" + paramArrayOfByte1[paramInt1] + ": " + arrayOfByte[paramArrayOfByte1[paramInt1]]);
      System.out.println("" + paramArrayOfByte1[(paramInt1 + 1)] + ": " + arrayOfByte[paramArrayOfByte1[(paramInt1 + 1)]]);
      System.out.println("" + paramArrayOfByte1[(paramInt1 + 2)] + ": " + arrayOfByte[paramArrayOfByte1[(paramInt1 + 2)]]);
      System.out.println("" + paramArrayOfByte1[(paramInt1 + 3)] + ": " + arrayOfByte[paramArrayOfByte1[(paramInt1 + 3)]]);
    }
    return -1;
  }

  public static byte[] decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte1 = getDecodabet(paramInt3);
    int i = paramInt2 * 3 / 4;
    byte[] arrayOfByte2 = new byte[i];
    int j = 0;
    byte[] arrayOfByte3 = new byte[4];
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    for (m = paramInt1; m < paramInt1 + paramInt2; m++)
    {
      n = (byte)(paramArrayOfByte[m] & 0x7F);
      i1 = arrayOfByte1[n];
      if (i1 >= -5)
      {
        if (i1 < -1)
          continue;
        arrayOfByte3[(k++)] = n;
        if (k <= 3)
          continue;
        j += decode4to3(arrayOfByte3, 0, arrayOfByte2, j, paramInt3);
        k = 0;
        if (n != 61)
          continue;
        break;
      }
      System.err.println("Bad Base64 input character at " + m + ": " + paramArrayOfByte[m] + "(decimal)");
      return null;
    }
    byte[] arrayOfByte4 = new byte[j];
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 0, j);
    return arrayOfByte4;
  }

  public static byte[] decode(String paramString)
  {
    return decode(paramString, 0);
  }

  public static byte[] decode(String paramString, int paramInt)
  {
    try
    {
      arrayOfByte1 = paramString.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      arrayOfByte1 = paramString.getBytes();
    }
    byte[] arrayOfByte1 = decode(arrayOfByte1, 0, arrayOfByte1.length, paramInt);
    if ((arrayOfByte1 != null) && (arrayOfByte1.length >= 4))
    {
      int i = arrayOfByte1[0] & 0xFF | arrayOfByte1[1] << 8 & 0xFF00;
      if (35615 == i)
      {
        ByteArrayInputStream localByteArrayInputStream = null;
        GZIPInputStream localGZIPInputStream = null;
        ByteArrayOutputStream localByteArrayOutputStream = null;
        byte[] arrayOfByte2 = new byte[2048];
        int j = 0;
        try
        {
          localByteArrayOutputStream = new ByteArrayOutputStream();
          localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
          localGZIPInputStream = new GZIPInputStream(localByteArrayInputStream);
          while ((j = localGZIPInputStream.read(arrayOfByte2)) >= 0)
            localByteArrayOutputStream.write(arrayOfByte2, 0, j);
          arrayOfByte1 = localByteArrayOutputStream.toByteArray();
        }
        catch (IOException localIOException)
        {
        }
        finally
        {
          try
          {
            localByteArrayOutputStream.close();
          }
          catch (Exception localException1)
          {
          }
          try
          {
            localGZIPInputStream.close();
          }
          catch (Exception localException2)
          {
          }
          try
          {
            localByteArrayInputStream.close();
          }
          catch (Exception localException3)
          {
          }
        }
      }
    }
    return arrayOfByte1;
  }

  public static Object decodeToObject(String paramString)
  {
    byte[] arrayOfByte = decode(paramString);
    ByteArrayInputStream localByteArrayInputStream = null;
    ObjectInputStream localObjectInputStream = null;
    Object localObject1 = null;
    try
    {
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
      localObject1 = localObjectInputStream.readObject();
    }
    catch (IOException localException4)
    {
      localIOException.printStackTrace();
    }
    catch (ClassNotFoundException localException6)
    {
      localClassNotFoundException.printStackTrace();
    }
    finally
    {
      try
      {
        localByteArrayInputStream.close();
      }
      catch (Exception localException7)
      {
      }
      try
      {
        localObjectInputStream.close();
      }
      catch (Exception localException8)
      {
      }
    }
    return localObject1;
  }

  public static boolean encodeToFile(byte[] paramArrayOfByte, String paramString)
  {
    int i = 0;
    OutputStream localOutputStream = null;
    try
    {
      localOutputStream = new OutputStream(new FileOutputStream(paramString), 1);
      localOutputStream.write(paramArrayOfByte);
      i = 1;
    }
    catch (IOException localException2)
    {
      i = 0;
    }
    finally
    {
      try
      {
        localOutputStream.close();
      }
      catch (Exception localException3)
      {
      }
    }
    return i;
  }

  public static boolean decodeToFile(String paramString1, String paramString2)
  {
    int i = 0;
    OutputStream localOutputStream = null;
    try
    {
      localOutputStream = new OutputStream(new FileOutputStream(paramString2), 0);
      localOutputStream.write(paramString1.getBytes("UTF-8"));
      i = 1;
    }
    catch (IOException localException2)
    {
      i = 0;
    }
    finally
    {
      try
      {
        localOutputStream.close();
      }
      catch (Exception localException3)
      {
      }
    }
    return i;
  }

  public static byte[] decodeFromFile(String paramString)
  {
    byte[] arrayOfByte1 = null;
    InputStream localInputStream = null;
    try
    {
      File localFile = new File(paramString);
      byte[] arrayOfByte2 = null;
      int i = 0;
      int j = 0;
      if (localFile.length() > 2147483647L)
      {
        System.err.println("File is too big for this convenience method (" + localFile.length() + " bytes).");
        Object localObject1 = null;
        return localObject1;
      }
      arrayOfByte2 = new byte[(int)localFile.length()];
      localInputStream = new InputStream(new BufferedInputStream(new FileInputStream(localFile)), 0);
      while ((j = localInputStream.read(arrayOfByte2, i, 4096)) >= 0)
        i += j;
      arrayOfByte1 = new byte[i];
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, i);
    }
    catch (IOException localException2)
    {
      System.err.println("Error decoding from file " + paramString);
    }
    finally
    {
      try
      {
        localInputStream.close();
      }
      catch (Exception localException4)
      {
      }
    }
    return arrayOfByte1;
  }

  public static String encodeFromFile(String paramString)
  {
    String str = null;
    InputStream localInputStream = null;
    try
    {
      File localFile = new File(paramString);
      byte[] arrayOfByte = new byte[Math.max((int)(localFile.length() * 1.4D), 40)];
      int i = 0;
      int j = 0;
      localInputStream = new InputStream(new BufferedInputStream(new FileInputStream(localFile)), 1);
      while ((j = localInputStream.read(arrayOfByte, i, 4096)) >= 0)
        i += j;
      str = new String(arrayOfByte, 0, i, "UTF-8");
    }
    catch (IOException localException2)
    {
      System.err.println("Error encoding from file " + paramString);
    }
    finally
    {
      try
      {
        localInputStream.close();
      }
      catch (Exception localException3)
      {
      }
    }
    return str;
  }

  public static void encodeFileToFile(String paramString1, String paramString2)
  {
    String str = encodeFromFile(paramString1);
    BufferedOutputStream localBufferedOutputStream = null;
    try
    {
      localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramString2));
      localBufferedOutputStream.write(str.getBytes("US-ASCII"));
    }
    catch (IOException localException2)
    {
      localIOException.printStackTrace();
    }
    finally
    {
      try
      {
        localBufferedOutputStream.close();
      }
      catch (Exception localException3)
      {
      }
    }
  }

  public static void decodeFileToFile(String paramString1, String paramString2)
  {
    byte[] arrayOfByte = decodeFromFile(paramString1);
    BufferedOutputStream localBufferedOutputStream = null;
    try
    {
      localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramString2));
      localBufferedOutputStream.write(arrayOfByte);
    }
    catch (IOException localException2)
    {
      localIOException.printStackTrace();
    }
    finally
    {
      try
      {
        localBufferedOutputStream.close();
      }
      catch (Exception localException3)
      {
      }
    }
  }

  public static class OutputStream extends FilterOutputStream
  {
    private boolean encode;
    private int position;
    private byte[] buffer;
    private int bufferLength;
    private int lineLength;
    private boolean breakLines;
    private byte[] b4;
    private boolean suspendEncoding;
    private int options;
    private byte[] alphabet;
    private byte[] decodabet;

    public OutputStream(OutputStream paramOutputStream)
    {
      this(paramOutputStream, 1);
    }

    public OutputStream(OutputStream paramOutputStream, int paramInt)
    {
      super();
      this.breakLines = ((paramInt & 0x8) != 8);
      this.encode = ((paramInt & 0x1) == 1);
      this.bufferLength = (this.encode ? 3 : 4);
      this.buffer = new byte[this.bufferLength];
      this.position = 0;
      this.lineLength = 0;
      this.suspendEncoding = false;
      this.b4 = new byte[4];
      this.options = paramInt;
      this.alphabet = Base64.access$000(paramInt);
      this.decodabet = Base64.access$100(paramInt);
    }

    public void write(int paramInt)
      throws IOException
    {
      if (this.suspendEncoding)
      {
        this.out.write(paramInt);
        return;
      }
      if (this.encode)
      {
        this.buffer[(this.position++)] = (byte)paramInt;
        if (this.position >= this.bufferLength)
        {
          this.out.write(Base64.access$400(this.b4, this.buffer, this.bufferLength, this.options));
          this.lineLength += 4;
          if ((this.breakLines) && (this.lineLength >= 76))
          {
            this.out.write(10);
            this.lineLength = 0;
          }
          this.position = 0;
        }
      }
      else if (this.decodabet[(paramInt & 0x7F)] > -5)
      {
        this.buffer[(this.position++)] = (byte)paramInt;
        if (this.position >= this.bufferLength)
        {
          int i = Base64.access$300(this.buffer, 0, this.b4, 0, this.options);
          this.out.write(this.b4, 0, i);
          this.position = 0;
        }
      }
      else if (this.decodabet[(paramInt & 0x7F)] != -5)
      {
        throw new IOException("Invalid character in Base64 data.");
      }
    }

    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (this.suspendEncoding)
      {
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      for (int i = 0; i < paramInt2; i++)
        write(paramArrayOfByte[(paramInt1 + i)]);
    }

    public void flushBase64()
      throws IOException
    {
      if (this.position > 0)
        if (this.encode)
        {
          this.out.write(Base64.access$400(this.b4, this.buffer, this.position, this.options));
          this.position = 0;
        }
        else
        {
          throw new IOException("Base64 input not properly padded.");
        }
    }

    public void close()
      throws IOException
    {
      flushBase64();
      super.close();
      this.buffer = null;
      this.out = null;
    }

    public void suspendEncoding()
      throws IOException
    {
      flushBase64();
      this.suspendEncoding = true;
    }

    public void resumeEncoding()
    {
      this.suspendEncoding = false;
    }
  }

  public static class InputStream extends FilterInputStream
  {
    private boolean encode;
    private int position;
    private byte[] buffer;
    private int bufferLength;
    private int numSigBytes;
    private int lineLength;
    private boolean breakLines;
    private int options;
    private byte[] alphabet;
    private byte[] decodabet;

    public InputStream(InputStream paramInputStream)
    {
      this(paramInputStream, 0);
    }

    public InputStream(InputStream paramInputStream, int paramInt)
    {
      super();
      this.breakLines = ((paramInt & 0x8) != 8);
      this.encode = ((paramInt & 0x1) == 1);
      this.bufferLength = (this.encode ? 4 : 3);
      this.buffer = new byte[this.bufferLength];
      this.position = -1;
      this.lineLength = 0;
      this.options = paramInt;
      this.alphabet = Base64.access$000(paramInt);
      this.decodabet = Base64.access$100(paramInt);
    }

    public int read()
      throws IOException
    {
      if (this.position < 0)
      {
        byte[] arrayOfByte;
        int j;
        int k;
        if (this.encode)
        {
          arrayOfByte = new byte[3];
          j = 0;
          for (k = 0; k < 3; k++)
            try
            {
              int m = this.in.read();
              if (m >= 0)
              {
                arrayOfByte[k] = (byte)m;
                j++;
              }
            }
            catch (IOException localIOException)
            {
              if (k != 0)
                continue;
              throw localIOException;
            }
          if (j > 0)
          {
            Base64.access$200(arrayOfByte, 0, j, this.buffer, 0, this.options);
            this.position = 0;
            this.numSigBytes = 4;
          }
          else
          {
            return -1;
          }
        }
        else
        {
          arrayOfByte = new byte[4];
          j = 0;
          for (j = 0; j < 4; j++)
          {
            k = 0;
            do
              k = this.in.read();
            while ((k >= 0) && (this.decodabet[(k & 0x7F)] <= -5));
            if (k < 0)
              break;
            arrayOfByte[j] = (byte)k;
          }
          if (j == 4)
          {
            this.numSigBytes = Base64.access$300(arrayOfByte, 0, this.buffer, 0, this.options);
            this.position = 0;
          }
          else
          {
            if (j == 0)
              return -1;
            throw new IOException("Improperly padded Base64 input.");
          }
        }
      }
      if (this.position >= 0)
      {
        if (this.position >= this.numSigBytes)
          return -1;
        if ((this.encode) && (this.breakLines) && (this.lineLength >= 76))
        {
          this.lineLength = 0;
          return 10;
        }
        this.lineLength += 1;
        int i = this.buffer[(this.position++)];
        if (this.position >= this.bufferLength)
          this.position = -1;
        return i & 0xFF;
      }
      throw new IOException("Error in Base64 code reading stream.");
    }

    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      for (int i = 0; i < paramInt2; i++)
      {
        int j = read();
        if (j >= 0)
        {
          paramArrayOfByte[(paramInt1 + i)] = (byte)j;
        }
        else
        {
          if (i != 0)
            break;
          return -1;
        }
      }
      return i;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.Base64
 * JD-Core Version:    0.6.0
 */