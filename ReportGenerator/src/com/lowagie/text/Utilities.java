package com.lowagie.text;

import com.lowagie.text.pdf.PRTokeniser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

public class Utilities
{
  public static Set getKeySet(Hashtable paramHashtable)
  {
    return paramHashtable == null ? Collections.EMPTY_SET : paramHashtable.keySet();
  }

  public static Object[][] addToArray(Object[][] paramArrayOfObject, Object[] paramArrayOfObject1)
  {
    if (paramArrayOfObject == null)
    {
      paramArrayOfObject = new Object[1][];
      paramArrayOfObject[0] = paramArrayOfObject1;
      return paramArrayOfObject;
    }
    Object[][] arrayOfObject; = new Object[paramArrayOfObject.length + 1][];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject;, 0, paramArrayOfObject.length);
    arrayOfObject;[paramArrayOfObject.length] = paramArrayOfObject1;
    return arrayOfObject;;
  }

  public static boolean checkTrueOrFalse(Properties paramProperties, String paramString)
  {
    return "true".equalsIgnoreCase(paramProperties.getProperty(paramString));
  }

  public static String unEscapeURL(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
    {
      char c = arrayOfChar[i];
      if (c == '%')
      {
        if (i + 2 >= arrayOfChar.length)
        {
          localStringBuffer.append(c);
        }
        else
        {
          int j = PRTokeniser.getHex(arrayOfChar[(i + 1)]);
          int k = PRTokeniser.getHex(arrayOfChar[(i + 2)]);
          if ((j < 0) || (k < 0))
          {
            localStringBuffer.append(c);
          }
          else
          {
            localStringBuffer.append((char)(j * 16 + k));
            i += 2;
          }
        }
      }
      else
        localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }

  public static URL toURL(String paramString)
    throws MalformedURLException
  {
    try
    {
      return new URL(paramString);
    }
    catch (Exception localException)
    {
    }
    return new File(paramString).toURI().toURL();
  }

  public static void skip(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    while (paramInt > 0)
    {
      long l = paramInputStream.skip(paramInt);
      if (l <= 0L)
        break;
      paramInt = (int)(paramInt - l);
    }
  }

  public static final float millimetersToPoints(float paramFloat)
  {
    return inchesToPoints(millimetersToInches(paramFloat));
  }

  public static final float millimetersToInches(float paramFloat)
  {
    return paramFloat / 25.4F;
  }

  public static final float pointsToMillimeters(float paramFloat)
  {
    return inchesToMillimeters(pointsToInches(paramFloat));
  }

  public static final float pointsToInches(float paramFloat)
  {
    return paramFloat / 72.0F;
  }

  public static final float inchesToMillimeters(float paramFloat)
  {
    return paramFloat * 25.4F;
  }

  public static final float inchesToPoints(float paramFloat)
  {
    return paramFloat * 72.0F;
  }

  public static boolean isSurrogateHigh(char paramChar)
  {
    return (paramChar >= 55296) && (paramChar <= 56319);
  }

  public static boolean isSurrogateLow(char paramChar)
  {
    return (paramChar >= 56320) && (paramChar <= 57343);
  }

  public static boolean isSurrogatePair(String paramString, int paramInt)
  {
    if ((paramInt < 0) || (paramInt > paramString.length() - 2))
      return false;
    return (isSurrogateHigh(paramString.charAt(paramInt))) && (isSurrogateLow(paramString.charAt(paramInt + 1)));
  }

  public static boolean isSurrogatePair(char[] paramArrayOfChar, int paramInt)
  {
    if ((paramInt < 0) || (paramInt > paramArrayOfChar.length - 2))
      return false;
    return (isSurrogateHigh(paramArrayOfChar[paramInt])) && (isSurrogateLow(paramArrayOfChar[(paramInt + 1)]));
  }

  public static int convertToUtf32(char paramChar1, char paramChar2)
  {
    return (paramChar1 - 55296) * 1024 + (paramChar2 - 56320) + 65536;
  }

  public static int convertToUtf32(char[] paramArrayOfChar, int paramInt)
  {
    return (paramArrayOfChar[paramInt] - 55296) * 1024 + (paramArrayOfChar[(paramInt + 1)] - 56320) + 65536;
  }

  public static int convertToUtf32(String paramString, int paramInt)
  {
    return (paramString.charAt(paramInt) - 55296) * 1024 + (paramString.charAt(paramInt + 1) - 56320) + 65536;
  }

  public static String convertFromUtf32(int paramInt)
  {
    if (paramInt < 65536)
      return Character.toString((char)paramInt);
    paramInt -= 65536;
    return new String(new char[] { (char)(paramInt / 1024 + 55296), (char)(paramInt % 1024 + 56320) });
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Utilities
 * JD-Core Version:    0.6.0
 */