package com.lowagie.text.factories;

import java.io.PrintStream;

public class RomanNumberFactory
{
  private static final RomanDigit[] roman = { new RomanDigit('m', 1000, false), new RomanDigit('d', 500, false), new RomanDigit('c', 100, true), new RomanDigit('l', 50, false), new RomanDigit('x', 10, true), new RomanDigit('v', 5, false), new RomanDigit('i', 1, true) };

  public static final String getString(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramInt < 0)
    {
      localStringBuffer.append('-');
      paramInt = -paramInt;
    }
    if (paramInt > 3000)
    {
      localStringBuffer.append('|');
      localStringBuffer.append(getString(paramInt / 1000));
      localStringBuffer.append('|');
      paramInt -= paramInt / 1000 * 1000;
    }
    for (int i = 0; ; i++)
    {
      RomanDigit localRomanDigit = roman[i];
      while (paramInt >= localRomanDigit.value)
      {
        localStringBuffer.append(localRomanDigit.digit);
        paramInt -= localRomanDigit.value;
      }
      if (paramInt <= 0)
        break;
      int j = i;
      do
        j++;
      while (!roman[j].pre);
      if (paramInt + roman[j].value < localRomanDigit.value)
        continue;
      localStringBuffer.append(roman[j].digit).append(localRomanDigit.digit);
      paramInt -= localRomanDigit.value - roman[j].value;
    }
    return localStringBuffer.toString();
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
    for (int i = 1; i < 2000; i++)
      System.out.println(getString(i));
  }

  private static class RomanDigit
  {
    public char digit;
    public int value;
    public boolean pre;

    RomanDigit(char paramChar, int paramInt, boolean paramBoolean)
    {
      this.digit = paramChar;
      this.value = paramInt;
      this.pre = paramBoolean;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.factories.RomanNumberFactory
 * JD-Core Version:    0.6.0
 */