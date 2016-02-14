package com.lowagie.text.html.simpleparser;

import java.util.ArrayList;
import java.util.HashMap;

public class ChainedProperties
{
  public static final int[] fontSizes = { 8, 10, 12, 14, 18, 24, 36 };
  public ArrayList chain = new ArrayList();

  public String getProperty(String paramString)
  {
    for (int i = this.chain.size() - 1; i >= 0; i--)
    {
      Object[] arrayOfObject = (Object[])this.chain.get(i);
      HashMap localHashMap = (HashMap)arrayOfObject[1];
      String str = (String)localHashMap.get(paramString);
      if (str != null)
        return str;
    }
    return null;
  }

  public boolean hasProperty(String paramString)
  {
    for (int i = this.chain.size() - 1; i >= 0; i--)
    {
      Object[] arrayOfObject = (Object[])this.chain.get(i);
      HashMap localHashMap = (HashMap)arrayOfObject[1];
      if (localHashMap.containsKey(paramString))
        return true;
    }
    return false;
  }

  public void addToChain(String paramString, HashMap paramHashMap)
  {
    String str1 = (String)paramHashMap.get("size");
    if (str1 != null)
      if (str1.endsWith("pt"))
      {
        paramHashMap.put("size", str1.substring(0, str1.length() - 2));
      }
      else
      {
        int i = 0;
        if ((str1.startsWith("+")) || (str1.startsWith("-")))
        {
          String str2 = getProperty("basefontsize");
          if (str2 == null)
            str2 = "12";
          float f = Float.parseFloat(str2);
          int j = (int)f;
          for (int k = fontSizes.length - 1; k >= 0; k--)
          {
            if (j < fontSizes[k])
              continue;
            i = k;
            break;
          }
          k = Integer.parseInt(str1.startsWith("+") ? str1.substring(1) : str1);
          i += k;
        }
        else
        {
          try
          {
            i = Integer.parseInt(str1) - 1;
          }
          catch (NumberFormatException localNumberFormatException)
          {
            i = 0;
          }
        }
        if (i < 0)
          i = 0;
        else if (i >= fontSizes.length)
          i = fontSizes.length - 1;
        paramHashMap.put("size", Integer.toString(fontSizes[i]));
      }
    this.chain.add(new Object[] { paramString, paramHashMap });
  }

  public void removeChain(String paramString)
  {
    for (int i = this.chain.size() - 1; i >= 0; i--)
    {
      if (!paramString.equals(((Object[])this.chain.get(i))[0]))
        continue;
      this.chain.remove(i);
      return;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.ChainedProperties
 * JD-Core Version:    0.6.0
 */