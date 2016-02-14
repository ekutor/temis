package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.HyphenationAuto;
import com.lowagie.text.pdf.HyphenationEvent;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class FactoryProperties
{
  private FontFactoryImp fontImp = FontFactory.getFontImp();
  public static HashMap followTags = new HashMap();

  public Chunk createChunk(String paramString, ChainedProperties paramChainedProperties)
  {
    Font localFont = getFont(paramChainedProperties);
    float f = localFont.getSize();
    f /= 2.0F;
    Chunk localChunk = new Chunk(paramString, localFont);
    if (paramChainedProperties.hasProperty("sub"))
      localChunk.setTextRise(-f);
    else if (paramChainedProperties.hasProperty("sup"))
      localChunk.setTextRise(f);
    localChunk.setHyphenation(getHyphenation(paramChainedProperties));
    return localChunk;
  }

  private static void setParagraphLeading(Paragraph paramParagraph, String paramString)
  {
    if (paramString == null)
    {
      paramParagraph.setLeading(0.0F, 1.5F);
      return;
    }
    try
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ,");
      String str = localStringTokenizer.nextToken();
      float f1 = Float.parseFloat(str);
      if (!localStringTokenizer.hasMoreTokens())
      {
        paramParagraph.setLeading(f1, 0.0F);
        return;
      }
      str = localStringTokenizer.nextToken();
      float f2 = Float.parseFloat(str);
      paramParagraph.setLeading(f1, f2);
    }
    catch (Exception localException)
    {
      paramParagraph.setLeading(0.0F, 1.5F);
    }
  }

  public static void createParagraph(Paragraph paramParagraph, ChainedProperties paramChainedProperties)
  {
    String str = paramChainedProperties.getProperty("align");
    if (str != null)
      if (str.equalsIgnoreCase("center"))
        paramParagraph.setAlignment(1);
      else if (str.equalsIgnoreCase("right"))
        paramParagraph.setAlignment(2);
      else if (str.equalsIgnoreCase("justify"))
        paramParagraph.setAlignment(3);
    paramParagraph.setHyphenation(getHyphenation(paramChainedProperties));
    setParagraphLeading(paramParagraph, paramChainedProperties.getProperty("leading"));
    str = paramChainedProperties.getProperty("before");
    if (str != null)
      try
      {
        paramParagraph.setSpacingBefore(Float.parseFloat(str));
      }
      catch (Exception localException1)
      {
      }
    str = paramChainedProperties.getProperty("after");
    if (str != null)
      try
      {
        paramParagraph.setSpacingAfter(Float.parseFloat(str));
      }
      catch (Exception localException2)
      {
      }
    str = paramChainedProperties.getProperty("extraparaspace");
    if (str != null)
      try
      {
        paramParagraph.setExtraParagraphSpace(Float.parseFloat(str));
      }
      catch (Exception localException3)
      {
      }
  }

  public static Paragraph createParagraph(ChainedProperties paramChainedProperties)
  {
    Paragraph localParagraph = new Paragraph();
    createParagraph(localParagraph, paramChainedProperties);
    return localParagraph;
  }

  public static ListItem createListItem(ChainedProperties paramChainedProperties)
  {
    ListItem localListItem = new ListItem();
    createParagraph(localListItem, paramChainedProperties);
    return localListItem;
  }

  public Font getFont(ChainedProperties paramChainedProperties)
  {
    String str1 = paramChainedProperties.getProperty("face");
    if (str1 != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, ",");
      while (localStringTokenizer.hasMoreTokens())
      {
        str1 = localStringTokenizer.nextToken().trim();
        if (str1.startsWith("\""))
          str1 = str1.substring(1);
        if (str1.endsWith("\""))
          str1 = str1.substring(0, str1.length() - 1);
        if (!this.fontImp.isRegistered(str1))
          continue;
      }
    }
    int i = 0;
    if (paramChainedProperties.hasProperty("i"))
      i |= 2;
    if (paramChainedProperties.hasProperty("b"))
      i |= 1;
    if (paramChainedProperties.hasProperty("u"))
      i |= 4;
    if (paramChainedProperties.hasProperty("s"))
      i |= 8;
    String str2 = paramChainedProperties.getProperty("size");
    float f = 12.0F;
    if (str2 != null)
      f = Float.parseFloat(str2);
    Color localColor = Markup.decodeColor(paramChainedProperties.getProperty("color"));
    String str3 = paramChainedProperties.getProperty("encoding");
    if (str3 == null)
      str3 = "Cp1252";
    return this.fontImp.getFont(str1, str3, true, f, i, localColor);
  }

  public static HyphenationEvent getHyphenation(ChainedProperties paramChainedProperties)
  {
    return getHyphenation(paramChainedProperties.getProperty("hyphenation"));
  }

  public static HyphenationEvent getHyphenation(HashMap paramHashMap)
  {
    return getHyphenation((String)paramHashMap.get("hyphenation"));
  }

  public static HyphenationEvent getHyphenation(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return null;
    String str1 = paramString;
    String str2 = null;
    int i = 2;
    int j = 2;
    int k = paramString.indexOf('_');
    if (k == -1)
      return new HyphenationAuto(str1, str2, i, j);
    str1 = paramString.substring(0, k);
    str2 = paramString.substring(k + 1);
    k = str2.indexOf(',');
    if (k == -1)
      return new HyphenationAuto(str1, str2, i, j);
    paramString = str2.substring(k + 1);
    str2 = str2.substring(0, k);
    k = paramString.indexOf(',');
    if (k == -1)
    {
      i = Integer.parseInt(paramString);
    }
    else
    {
      i = Integer.parseInt(paramString.substring(0, k));
      j = Integer.parseInt(paramString.substring(k + 1));
    }
    return new HyphenationAuto(str1, str2, i, j);
  }

  public static void insertStyle(HashMap paramHashMap)
  {
    String str1 = (String)paramHashMap.get("style");
    if (str1 == null)
      return;
    Properties localProperties = Markup.parseAttributes(str1);
    Iterator localIterator = localProperties.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      if (str2.equals("font-family"))
      {
        paramHashMap.put("face", localProperties.getProperty(str2));
        continue;
      }
      if (str2.equals("font-size"))
      {
        paramHashMap.put("size", Float.toString(Markup.parseLength(localProperties.getProperty(str2))) + "pt");
        continue;
      }
      if (str2.equals("font-style"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        if ((!((String)localObject).equals("italic")) && (!((String)localObject).equals("oblique")))
          continue;
        paramHashMap.put("i", null);
        continue;
      }
      if (str2.equals("font-weight"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        if ((!((String)localObject).equals("bold")) && (!((String)localObject).equals("700")) && (!((String)localObject).equals("800")) && (!((String)localObject).equals("900")))
          continue;
        paramHashMap.put("b", null);
        continue;
      }
      if (str2.equals("text-decoration"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        if (!((String)localObject).equals("underline"))
          continue;
        paramHashMap.put("u", null);
        continue;
      }
      if (str2.equals("color"))
      {
        localObject = Markup.decodeColor(localProperties.getProperty(str2));
        if (localObject == null)
          continue;
        int i = ((Color)localObject).getRGB();
        String str3 = Integer.toHexString(i);
        str3 = "000000" + str3;
        str3 = "#" + str3.substring(str3.length() - 6);
        paramHashMap.put("color", str3);
        continue;
      }
      if (str2.equals("line-height"))
      {
        localObject = localProperties.getProperty(str2).trim();
        float f = Markup.parseLength(localProperties.getProperty(str2));
        if (((String)localObject).endsWith("%"))
        {
          paramHashMap.put("leading", "0," + f / 100.0F);
          continue;
        }
        if ("normal".equalsIgnoreCase((String)localObject))
        {
          paramHashMap.put("leading", "0,1.5");
          continue;
        }
        paramHashMap.put("leading", f + ",0");
        continue;
      }
      if (!str2.equals("text-align"))
        continue;
      Object localObject = localProperties.getProperty(str2).trim().toLowerCase();
      paramHashMap.put("align", localObject);
    }
  }

  public static void insertStyle(HashMap paramHashMap, ChainedProperties paramChainedProperties)
  {
    String str1 = (String)paramHashMap.get("style");
    if (str1 == null)
      return;
    Properties localProperties = Markup.parseAttributes(str1);
    Iterator localIterator = localProperties.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      if (str2.equals("font-family"))
      {
        paramHashMap.put("face", localProperties.getProperty(str2));
        continue;
      }
      if (str2.equals("font-size"))
      {
        float f1 = Markup.parseLength(paramChainedProperties.getProperty("size"), 12.0F);
        if (f1 <= 0.0F)
          f1 = 12.0F;
        paramHashMap.put("size", Float.toString(Markup.parseLength(localProperties.getProperty(str2), f1)) + "pt");
        continue;
      }
      if (str2.equals("font-style"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        if ((!((String)localObject).equals("italic")) && (!((String)localObject).equals("oblique")))
          continue;
        paramHashMap.put("i", null);
        continue;
      }
      if (str2.equals("font-weight"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        if ((!((String)localObject).equals("bold")) && (!((String)localObject).equals("700")) && (!((String)localObject).equals("800")) && (!((String)localObject).equals("900")))
          continue;
        paramHashMap.put("b", null);
        continue;
      }
      if (str2.equals("text-decoration"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        if (!((String)localObject).equals("underline"))
          continue;
        paramHashMap.put("u", null);
        continue;
      }
      if (str2.equals("color"))
      {
        localObject = Markup.decodeColor(localProperties.getProperty(str2));
        if (localObject == null)
          continue;
        int i = ((Color)localObject).getRGB();
        String str3 = Integer.toHexString(i);
        str3 = "000000" + str3;
        str3 = "#" + str3.substring(str3.length() - 6);
        paramHashMap.put("color", str3);
        continue;
      }
      if (str2.equals("line-height"))
      {
        localObject = localProperties.getProperty(str2).trim();
        float f2 = Markup.parseLength(paramChainedProperties.getProperty("size"), 12.0F);
        if (f2 <= 0.0F)
          f2 = 12.0F;
        float f3 = Markup.parseLength(localProperties.getProperty(str2), f2);
        if (((String)localObject).endsWith("%"))
        {
          paramHashMap.put("leading", "0," + f3 / 100.0F);
          return;
        }
        if ("normal".equalsIgnoreCase((String)localObject))
        {
          paramHashMap.put("leading", "0,1.5");
          return;
        }
        paramHashMap.put("leading", f3 + ",0");
        continue;
      }
      if (str2.equals("text-align"))
      {
        localObject = localProperties.getProperty(str2).trim().toLowerCase();
        paramHashMap.put("align", localObject);
        continue;
      }
      if (!str2.equals("padding-left"))
        continue;
      Object localObject = localProperties.getProperty(str2).trim().toLowerCase();
      paramHashMap.put("indent", Float.toString(Markup.parseLength((String)localObject)));
    }
  }

  public FontFactoryImp getFontImp()
  {
    return this.fontImp;
  }

  public void setFontImp(FontFactoryImp paramFontFactoryImp)
  {
    this.fontImp = paramFontFactoryImp;
  }

  static
  {
    followTags.put("i", "i");
    followTags.put("b", "b");
    followTags.put("u", "u");
    followTags.put("sub", "sub");
    followTags.put("sup", "sup");
    followTags.put("em", "i");
    followTags.put("strong", "b");
    followTags.put("s", "s");
    followTags.put("strike", "s");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.FactoryProperties
 * JD-Core Version:    0.6.0
 */