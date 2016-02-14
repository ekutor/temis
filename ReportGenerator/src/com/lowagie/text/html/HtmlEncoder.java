package com.lowagie.text.html;

import java.awt.Color;

public final class HtmlEncoder
{
  private static final String[] htmlCode = new String[256];

  public static String encode(String paramString)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int k = 0; k < i; k++)
    {
      int j = paramString.charAt(k);
      if (j < 256)
        localStringBuffer.append(htmlCode[j]);
      else
        localStringBuffer.append("&#").append(j).append(';');
    }
    return localStringBuffer.toString();
  }

  public static String encode(Color paramColor)
  {
    StringBuffer localStringBuffer = new StringBuffer("#");
    if (paramColor.getRed() < 16)
      localStringBuffer.append('0');
    localStringBuffer.append(Integer.toString(paramColor.getRed(), 16));
    if (paramColor.getGreen() < 16)
      localStringBuffer.append('0');
    localStringBuffer.append(Integer.toString(paramColor.getGreen(), 16));
    if (paramColor.getBlue() < 16)
      localStringBuffer.append('0');
    localStringBuffer.append(Integer.toString(paramColor.getBlue(), 16));
    return localStringBuffer.toString();
  }

  public static String getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 0:
      return "Left";
    case 1:
      return "Center";
    case 2:
      return "Right";
    case 3:
    case 8:
      return "Justify";
    case 4:
      return "Top";
    case 5:
      return "Middle";
    case 6:
      return "Bottom";
    case 7:
      return "Baseline";
    }
    return "";
  }

  static
  {
    for (int i = 0; i < 10; i++)
      htmlCode[i] = ("&#00" + i + ";");
    for (i = 10; i < 32; i++)
      htmlCode[i] = ("&#0" + i + ";");
    for (i = 32; i < 128; i++)
      htmlCode[i] = String.valueOf((char)i);
    htmlCode[9] = "\t";
    htmlCode[10] = "<br />\n";
    htmlCode[34] = "&quot;";
    htmlCode[38] = "&amp;";
    htmlCode[60] = "&lt;";
    htmlCode[62] = "&gt;";
    for (i = 128; i < 256; i++)
      htmlCode[i] = ("&#" + i + ";");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.HtmlEncoder
 * JD-Core Version:    0.6.0
 */