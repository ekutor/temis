package com.lowagie.text.factories;

import java.io.PrintStream;

public class RomanAlphabetFactory
{
  public static final String getString(int paramInt)
  {
    if (paramInt < 1)
      throw new NumberFormatException("You can't translate a negative number into an alphabetical value.");
    paramInt--;
    int i = 1;
    int j = 0;
    int k = 26;
    while (paramInt >= k + j)
    {
      i++;
      j += k;
      k *= 26;
    }
    int m = paramInt - j;
    char[] arrayOfChar = new char[i];
    while (i > 0)
    {
      i--;
      arrayOfChar[i] = (char)(97 + m % 26);
      m /= 26;
    }
    return new String(arrayOfChar);
  }

  public static final String getLowerCaseString(int paramInt)
  {
    return getString(paramInt);
  }

  public static final String getUpperCaseString(int paramInt)
  {
    return getString(paramInt).toUpperCase();
  }

  public static final String getString(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
      return getLowerCaseString(paramInt);
    return getUpperCaseString(paramInt);
  }

  public static void main(String[] paramArrayOfString)
  {
    for (int i = 1; i < 32000; i++)
      System.out.println(getString(i));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.factories.RomanAlphabetFactory
 * JD-Core Version:    0.6.0
 */