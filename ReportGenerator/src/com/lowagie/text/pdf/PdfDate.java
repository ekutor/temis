package com.lowagie.text.pdf;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class PdfDate extends PdfString
{
  private static final int[] DATE_SPACE = { 1, 4, 0, 2, 2, -1, 5, 2, 0, 11, 2, 0, 12, 2, 0, 13, 2, 0 };

  public PdfDate(Calendar paramCalendar)
  {
    StringBuffer localStringBuffer = new StringBuffer("D:");
    localStringBuffer.append(setLength(paramCalendar.get(1), 4));
    localStringBuffer.append(setLength(paramCalendar.get(2) + 1, 2));
    localStringBuffer.append(setLength(paramCalendar.get(5), 2));
    localStringBuffer.append(setLength(paramCalendar.get(11), 2));
    localStringBuffer.append(setLength(paramCalendar.get(12), 2));
    localStringBuffer.append(setLength(paramCalendar.get(13), 2));
    int i = (paramCalendar.get(15) + paramCalendar.get(16)) / 3600000;
    if (i == 0)
    {
      localStringBuffer.append('Z');
    }
    else if (i < 0)
    {
      localStringBuffer.append('-');
      i = -i;
    }
    else
    {
      localStringBuffer.append('+');
    }
    if (i != 0)
    {
      localStringBuffer.append(setLength(i, 2)).append('\'');
      int j = Math.abs((paramCalendar.get(15) + paramCalendar.get(16)) / 60000) - i * 60;
      localStringBuffer.append(setLength(j, 2)).append('\'');
    }
    this.value = localStringBuffer.toString();
  }

  public PdfDate()
  {
    this(new GregorianCalendar());
  }

  private String setLength(int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramInt1);
    while (localStringBuffer.length() < paramInt2)
      localStringBuffer.insert(0, "0");
    localStringBuffer.setLength(paramInt2);
    return localStringBuffer.toString();
  }

  public String getW3CDate()
  {
    return getW3CDate(this.value);
  }

  public static String getW3CDate(String paramString)
  {
    if (paramString.startsWith("D:"))
      paramString = paramString.substring(2);
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramString.length() < 4)
      return "0000";
    localStringBuffer.append(paramString.substring(0, 4));
    paramString = paramString.substring(4);
    if (paramString.length() < 2)
      return localStringBuffer.toString();
    localStringBuffer.append('-').append(paramString.substring(0, 2));
    paramString = paramString.substring(2);
    if (paramString.length() < 2)
      return localStringBuffer.toString();
    localStringBuffer.append('-').append(paramString.substring(0, 2));
    paramString = paramString.substring(2);
    if (paramString.length() < 2)
      return localStringBuffer.toString();
    localStringBuffer.append('T').append(paramString.substring(0, 2));
    paramString = paramString.substring(2);
    if (paramString.length() < 2)
    {
      localStringBuffer.append(":00Z");
      return localStringBuffer.toString();
    }
    localStringBuffer.append(':').append(paramString.substring(0, 2));
    paramString = paramString.substring(2);
    if (paramString.length() < 2)
    {
      localStringBuffer.append('Z');
      return localStringBuffer.toString();
    }
    localStringBuffer.append(':').append(paramString.substring(0, 2));
    paramString = paramString.substring(2);
    if ((paramString.startsWith("-")) || (paramString.startsWith("+")))
    {
      String str1 = paramString.substring(0, 1);
      paramString = paramString.substring(1);
      String str2 = "00";
      String str3 = "00";
      if (paramString.length() >= 2)
      {
        str2 = paramString.substring(0, 2);
        if (paramString.length() > 2)
        {
          paramString = paramString.substring(3);
          if (paramString.length() >= 2)
            str3 = paramString.substring(0, 2);
        }
        localStringBuffer.append(str1).append(str2).append(':').append(str3);
        return localStringBuffer.toString();
      }
    }
    localStringBuffer.append('Z');
    return localStringBuffer.toString();
  }

  public static Calendar decode(String paramString)
  {
    try
    {
      if (paramString.startsWith("D:"))
        paramString = paramString.substring(2);
      int i = paramString.length();
      int j = paramString.indexOf('Z');
      GregorianCalendar localGregorianCalendar;
      if (j >= 0)
      {
        i = j;
        localGregorianCalendar = new GregorianCalendar(new SimpleTimeZone(0, "ZPDF"));
      }
      else
      {
        k = 1;
        j = paramString.indexOf('+');
        if (j < 0)
        {
          j = paramString.indexOf('-');
          if (j >= 0)
            k = -1;
        }
        if (j < 0)
        {
          localGregorianCalendar = new GregorianCalendar();
        }
        else
        {
          int m = Integer.parseInt(paramString.substring(j + 1, j + 3)) * 60;
          if (j + 5 < paramString.length())
            m += Integer.parseInt(paramString.substring(j + 4, j + 6));
          localGregorianCalendar = new GregorianCalendar(new SimpleTimeZone(m * k * 60000, "ZPDF"));
          i = j;
        }
      }
      localGregorianCalendar.clear();
      j = 0;
      for (int k = 0; (k < DATE_SPACE.length) && (j < i); k += 3)
      {
        localGregorianCalendar.set(DATE_SPACE[k], Integer.parseInt(paramString.substring(j, j + DATE_SPACE[(k + 1)])) + DATE_SPACE[(k + 2)]);
        j += DATE_SPACE[(k + 1)];
      }
      return localGregorianCalendar;
    }
    catch (Exception localException)
    {
    }
    return null;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfDate
 * JD-Core Version:    0.6.0
 */