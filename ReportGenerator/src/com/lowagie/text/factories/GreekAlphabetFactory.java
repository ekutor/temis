package com.lowagie.text.factories;

import com.lowagie.text.SpecialSymbol;
import java.io.PrintStream;

public class GreekAlphabetFactory
{
  public static final String getString(int paramInt)
  {
    return getString(paramInt, true);
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
    if (paramInt < 1)
      return "";
    paramInt--;
    int i = 1;
    int j = 0;
    int k = 24;
    while (paramInt >= k + j)
    {
      i++;
      j += k;
      k *= 24;
    }
    int m = paramInt - j;
    char[] arrayOfChar = new char[i];
    while (i > 0)
    {
      i--;
      arrayOfChar[i] = (char)(m % 24);
      if (arrayOfChar[i] > '\020')
      {
        int tmp84_83 = i;
        char[] tmp84_81 = arrayOfChar;
        tmp84_81[tmp84_83] = (char)(tmp84_81[tmp84_83] + '\001');
      }
      int tmp93_92 = i;
      char[] tmp93_90 = arrayOfChar;
      tmp93_90[tmp93_92] = (char)(tmp93_90[tmp93_92] + (paramBoolean ? 945 : 'Α'));
      arrayOfChar[i] = SpecialSymbol.getCorrespondingSymbol(arrayOfChar[i]);
      m /= 24;
    }
    return String.valueOf(arrayOfChar);
  }

  public static void main(String[] paramArrayOfString)
  {
    for (int i = 1; i < 1000; i++)
      System.out.println(getString(i));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.factories.GreekAlphabetFactory
 * JD-Core Version:    0.6.0
 */