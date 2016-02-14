package com.lowagie.text;

import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class FontFactoryImp
{
  private Properties trueTypeFonts = new Properties();
  private static String[] TTFamilyOrder = { "3", "1", "1033", "3", "0", "1033", "1", "0", "0", "0", "3", "0" };
  private Hashtable fontFamilies = new Hashtable();
  public String defaultEncoding = "Cp1252";
  public boolean defaultEmbedding = false;

  public FontFactoryImp()
  {
    this.trueTypeFonts.setProperty("Courier".toLowerCase(), "Courier");
    this.trueTypeFonts.setProperty("Courier-Bold".toLowerCase(), "Courier-Bold");
    this.trueTypeFonts.setProperty("Courier-Oblique".toLowerCase(), "Courier-Oblique");
    this.trueTypeFonts.setProperty("Courier-BoldOblique".toLowerCase(), "Courier-BoldOblique");
    this.trueTypeFonts.setProperty("Helvetica".toLowerCase(), "Helvetica");
    this.trueTypeFonts.setProperty("Helvetica-Bold".toLowerCase(), "Helvetica-Bold");
    this.trueTypeFonts.setProperty("Helvetica-Oblique".toLowerCase(), "Helvetica-Oblique");
    this.trueTypeFonts.setProperty("Helvetica-BoldOblique".toLowerCase(), "Helvetica-BoldOblique");
    this.trueTypeFonts.setProperty("Symbol".toLowerCase(), "Symbol");
    this.trueTypeFonts.setProperty("Times-Roman".toLowerCase(), "Times-Roman");
    this.trueTypeFonts.setProperty("Times-Bold".toLowerCase(), "Times-Bold");
    this.trueTypeFonts.setProperty("Times-Italic".toLowerCase(), "Times-Italic");
    this.trueTypeFonts.setProperty("Times-BoldItalic".toLowerCase(), "Times-BoldItalic");
    this.trueTypeFonts.setProperty("ZapfDingbats".toLowerCase(), "ZapfDingbats");
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("Courier");
    localArrayList.add("Courier-Bold");
    localArrayList.add("Courier-Oblique");
    localArrayList.add("Courier-BoldOblique");
    this.fontFamilies.put("Courier".toLowerCase(), localArrayList);
    localArrayList = new ArrayList();
    localArrayList.add("Helvetica");
    localArrayList.add("Helvetica-Bold");
    localArrayList.add("Helvetica-Oblique");
    localArrayList.add("Helvetica-BoldOblique");
    this.fontFamilies.put("Helvetica".toLowerCase(), localArrayList);
    localArrayList = new ArrayList();
    localArrayList.add("Symbol");
    this.fontFamilies.put("Symbol".toLowerCase(), localArrayList);
    localArrayList = new ArrayList();
    localArrayList.add("Times-Roman");
    localArrayList.add("Times-Bold");
    localArrayList.add("Times-Italic");
    localArrayList.add("Times-BoldItalic");
    this.fontFamilies.put("Times".toLowerCase(), localArrayList);
    this.fontFamilies.put("Times-Roman".toLowerCase(), localArrayList);
    localArrayList = new ArrayList();
    localArrayList.add("ZapfDingbats");
    this.fontFamilies.put("ZapfDingbats".toLowerCase(), localArrayList);
  }

  public Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat, int paramInt, Color paramColor)
  {
    return getFont(paramString1, paramString2, paramBoolean, paramFloat, paramInt, paramColor, true);
  }

  public Font getFont(String paramString1, String paramString2, boolean paramBoolean1, float paramFloat, int paramInt, Color paramColor, boolean paramBoolean2)
  {
    if (paramString1 == null)
      return new Font(-1, paramFloat, paramInt, paramColor);
    String str1 = paramString1.toLowerCase();
    ArrayList localArrayList = (ArrayList)this.fontFamilies.get(str1);
    if (localArrayList != null)
    {
      int i = paramInt == -1 ? 0 : paramInt;
      int j = 0;
      int k = 0;
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        String str3 = str2.toLowerCase();
        j = 0;
        if (str3.toLowerCase().indexOf("bold") != -1)
          j |= 1;
        if ((str3.toLowerCase().indexOf("italic") != -1) || (str3.toLowerCase().indexOf("oblique") != -1))
          j |= 2;
        if ((i & 0x3) != j)
          continue;
        paramString1 = str2;
        k = 1;
      }
      if ((paramInt != -1) && (k != 0))
        paramInt &= (j ^ 0xFFFFFFFF);
    }
    BaseFont localBaseFont = null;
    try
    {
      try
      {
        localBaseFont = BaseFont.createFont(paramString1, paramString2, paramBoolean1, paramBoolean2, null, null, true);
      }
      catch (DocumentException localDocumentException1)
      {
      }
      if (localBaseFont == null)
      {
        paramString1 = this.trueTypeFonts.getProperty(paramString1.toLowerCase());
        if (paramString1 == null)
          return new Font(-1, paramFloat, paramInt, paramColor);
        localBaseFont = BaseFont.createFont(paramString1, paramString2, paramBoolean1, paramBoolean2, null, null);
      }
    }
    catch (DocumentException localDocumentException2)
    {
      throw new ExceptionConverter(localDocumentException2);
    }
    catch (IOException localIOException)
    {
      return new Font(-1, paramFloat, paramInt, paramColor);
    }
    catch (NullPointerException localNullPointerException)
    {
      return new Font(-1, paramFloat, paramInt, paramColor);
    }
    return new Font(localBaseFont, paramFloat, paramInt, paramColor);
  }

  public Font getFont(Properties paramProperties)
  {
    Object localObject1 = null;
    Object localObject2 = this.defaultEncoding;
    boolean bool = this.defaultEmbedding;
    float f = -1.0F;
    int i = 0;
    Color localColor = null;
    String str = paramProperties.getProperty("style");
    if ((str != null) && (str.length() > 0))
    {
      localObject3 = Markup.parseAttributes(str);
      if (((Properties)localObject3).isEmpty())
      {
        paramProperties.put("style", str);
      }
      else
      {
        localObject1 = ((Properties)localObject3).getProperty("font-family");
        if (localObject1 != null)
          while (((String)localObject1).indexOf(',') != -1)
          {
            localObject4 = ((String)localObject1).substring(0, ((String)localObject1).indexOf(','));
            if (isRegistered((String)localObject4))
            {
              localObject1 = localObject4;
              continue;
            }
            localObject1 = ((String)localObject1).substring(((String)localObject1).indexOf(',') + 1);
          }
        if ((str = ((Properties)localObject3).getProperty("font-size")) != null)
          f = Markup.parseLength(str);
        if ((str = ((Properties)localObject3).getProperty("font-weight")) != null)
          i |= Font.getStyleValue(str);
        if ((str = ((Properties)localObject3).getProperty("font-style")) != null)
          i |= Font.getStyleValue(str);
        if ((str = ((Properties)localObject3).getProperty("color")) != null)
          localColor = Markup.decodeColor(str);
        paramProperties.putAll((Map)localObject3);
        localObject4 = ((Properties)localObject3).keys();
        while (((Enumeration)localObject4).hasMoreElements())
        {
          localObject5 = ((Enumeration)localObject4).nextElement();
          paramProperties.put(localObject5, ((Properties)localObject3).get(localObject5));
        }
      }
    }
    if ((str = paramProperties.getProperty("encoding")) != null)
      localObject2 = str;
    if ("true".equals(paramProperties.getProperty("embedded")))
      bool = true;
    if ((str = paramProperties.getProperty("font")) != null)
      localObject1 = str;
    if ((str = paramProperties.getProperty("size")) != null)
      f = Markup.parseLength(str);
    if ((str = paramProperties.getProperty("style")) != null)
      i |= Font.getStyleValue(str);
    if ((str = paramProperties.getProperty("fontstyle")) != null)
      i |= Font.getStyleValue(str);
    Object localObject3 = paramProperties.getProperty("red");
    Object localObject4 = paramProperties.getProperty("green");
    Object localObject5 = paramProperties.getProperty("blue");
    if ((localObject3 != null) || (localObject4 != null) || (localObject5 != null))
    {
      int j = 0;
      int k = 0;
      int m = 0;
      if (localObject3 != null)
        j = Integer.parseInt((String)localObject3);
      if (localObject4 != null)
        k = Integer.parseInt((String)localObject4);
      if (localObject5 != null)
        m = Integer.parseInt((String)localObject5);
      localColor = new Color(j, k, m);
    }
    else if ((str = paramProperties.getProperty("color")) != null)
    {
      localColor = Markup.decodeColor(str);
    }
    if (localObject1 == null)
      return getFont(null, (String)localObject2, bool, f, i, localColor);
    return (Font)(Font)(Font)(Font)(Font)getFont((String)localObject1, (String)localObject2, bool, f, i, localColor);
  }

  public Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat, int paramInt)
  {
    return getFont(paramString1, paramString2, paramBoolean, paramFloat, paramInt, null);
  }

  public Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat)
  {
    return getFont(paramString1, paramString2, paramBoolean, paramFloat, -1, null);
  }

  public Font getFont(String paramString1, String paramString2, boolean paramBoolean)
  {
    return getFont(paramString1, paramString2, paramBoolean, -1.0F, -1, null);
  }

  public Font getFont(String paramString1, String paramString2, float paramFloat, int paramInt, Color paramColor)
  {
    return getFont(paramString1, paramString2, this.defaultEmbedding, paramFloat, paramInt, paramColor);
  }

  public Font getFont(String paramString1, String paramString2, float paramFloat, int paramInt)
  {
    return getFont(paramString1, paramString2, this.defaultEmbedding, paramFloat, paramInt, null);
  }

  public Font getFont(String paramString1, String paramString2, float paramFloat)
  {
    return getFont(paramString1, paramString2, this.defaultEmbedding, paramFloat, -1, null);
  }

  public Font getFont(String paramString, float paramFloat, Color paramColor)
  {
    return getFont(paramString, this.defaultEncoding, this.defaultEmbedding, paramFloat, -1, paramColor);
  }

  public Font getFont(String paramString1, String paramString2)
  {
    return getFont(paramString1, paramString2, this.defaultEmbedding, -1.0F, -1, null);
  }

  public Font getFont(String paramString, float paramFloat, int paramInt, Color paramColor)
  {
    return getFont(paramString, this.defaultEncoding, this.defaultEmbedding, paramFloat, paramInt, paramColor);
  }

  public Font getFont(String paramString, float paramFloat, int paramInt)
  {
    return getFont(paramString, this.defaultEncoding, this.defaultEmbedding, paramFloat, paramInt, null);
  }

  public Font getFont(String paramString, float paramFloat)
  {
    return getFont(paramString, this.defaultEncoding, this.defaultEmbedding, paramFloat, -1, null);
  }

  public Font getFont(String paramString)
  {
    return getFont(paramString, this.defaultEncoding, this.defaultEmbedding, -1.0F, -1, null);
  }

  public void registerFamily(String paramString1, String paramString2, String paramString3)
  {
    if (paramString3 != null)
      this.trueTypeFonts.setProperty(paramString2, paramString3);
    ArrayList localArrayList = (ArrayList)this.fontFamilies.get(paramString1);
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      localArrayList.add(paramString2);
      this.fontFamilies.put(paramString1, localArrayList);
    }
    else
    {
      int i = paramString2.length();
      int j = 0;
      for (int k = 0; k < localArrayList.size(); k++)
      {
        if (((String)localArrayList.get(k)).length() < i)
          continue;
        localArrayList.add(k, paramString2);
        j = 1;
        break;
      }
      if (j == 0)
        localArrayList.add(paramString2);
    }
  }

  public void register(String paramString)
  {
    register(paramString, null);
  }

  public void register(String paramString1, String paramString2)
  {
    try
    {
      Object localObject;
      String[][] arrayOfString;
      String str2;
      String str3;
      int m;
      String str4;
      if ((paramString1.toLowerCase().endsWith(".ttf")) || (paramString1.toLowerCase().endsWith(".otf")) || (paramString1.toLowerCase().indexOf(".ttc,") > 0))
      {
        localObject = BaseFont.getAllFontNames(paramString1, "Cp1252", null);
        this.trueTypeFonts.setProperty(((String)localObject[0]).toLowerCase(), paramString1);
        if (paramString2 != null)
          this.trueTypeFonts.setProperty(paramString2.toLowerCase(), paramString1);
        arrayOfString = (String[][])localObject[2];
        for (int j = 0; j < arrayOfString.length; j++)
          this.trueTypeFonts.setProperty(arrayOfString[j][3].toLowerCase(), paramString1);
        str2 = null;
        str3 = null;
        arrayOfString = (String[][])localObject[1];
        for (int k = 0; k < TTFamilyOrder.length; k += 3)
          for (m = 0; m < arrayOfString.length; m++)
          {
            if ((!TTFamilyOrder[k].equals(arrayOfString[m][0])) || (!TTFamilyOrder[(k + 1)].equals(arrayOfString[m][1])) || (!TTFamilyOrder[(k + 2)].equals(arrayOfString[m][2])))
              continue;
            str3 = arrayOfString[m][3].toLowerCase();
            k = TTFamilyOrder.length;
            break;
          }
        if (str3 != null)
        {
          str4 = "";
          arrayOfString = (String[][])localObject[2];
          m = 0;
        }
      }
      else
      {
        while (m < arrayOfString.length)
        {
          for (int n = 0; n < TTFamilyOrder.length; n += 3)
          {
            if ((!TTFamilyOrder[n].equals(arrayOfString[m][0])) || (!TTFamilyOrder[(n + 1)].equals(arrayOfString[m][1])) || (!TTFamilyOrder[(n + 2)].equals(arrayOfString[m][2])))
              continue;
            str2 = arrayOfString[m][3];
            if (str2.equals(str4))
              continue;
            str4 = str2;
            registerFamily(str3, str2, null);
            break;
          }
          m++;
          continue;
          if (paramString1.toLowerCase().endsWith(".ttc"))
          {
            if (paramString2 != null)
              System.err.println("class FontFactory: You can't define an alias for a true type collection.");
            localObject = BaseFont.enumerateTTCNames(paramString1);
            for (int i = 0; i < localObject.length; i++)
              register(paramString1 + "," + i);
          }
          if ((!paramString1.toLowerCase().endsWith(".afm")) && (!paramString1.toLowerCase().endsWith(".pfm")))
            break;
          localObject = BaseFont.createFont(paramString1, "Cp1252", false);
          String str1 = localObject.getFullFontName()[0][3].toLowerCase();
          str2 = localObject.getFamilyFontName()[0][3].toLowerCase();
          str3 = ((BaseFont)localObject).getPostscriptFontName().toLowerCase();
          registerFamily(str2, str1, null);
          this.trueTypeFonts.setProperty(str3, paramString1);
          this.trueTypeFonts.setProperty(str1, paramString1);
        }
      }
    }
    catch (DocumentException localDocumentException)
    {
      throw new ExceptionConverter(localDocumentException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public int registerDirectory(String paramString)
  {
    return registerDirectory(paramString, false);
  }

  public int registerDirectory(String paramString, boolean paramBoolean)
  {
    int i = 0;
    try
    {
      File localFile1 = new File(paramString);
      if ((!localFile1.exists()) || (!localFile1.isDirectory()))
        return 0;
      String[] arrayOfString = localFile1.list();
      if (arrayOfString == null)
        return 0;
      for (int j = 0; j < arrayOfString.length; j++)
        try
        {
          localFile1 = new File(paramString, arrayOfString[j]);
          if (localFile1.isDirectory())
          {
            if (paramBoolean)
              i += registerDirectory(localFile1.getAbsolutePath(), true);
          }
          else
          {
            String str1 = localFile1.getPath();
            String str2 = str1.length() < 4 ? null : str1.substring(str1.length() - 4).toLowerCase();
            if ((".afm".equals(str2)) || (".pfm".equals(str2)))
            {
              File localFile2 = new File(str1.substring(0, str1.length() - 4) + ".pfb");
              if (localFile2.exists())
              {
                register(str1, null);
                i++;
              }
            }
            else if ((".ttf".equals(str2)) || (".otf".equals(str2)) || (".ttc".equals(str2)))
            {
              register(str1, null);
              i++;
            }
          }
        }
        catch (Exception localException2)
        {
        }
    }
    catch (Exception localException1)
    {
    }
    return i;
  }

  public int registerDirectories()
  {
    int i = 0;
    i += registerDirectory("c:/windows/fonts");
    i += registerDirectory("c:/winnt/fonts");
    i += registerDirectory("d:/windows/fonts");
    i += registerDirectory("d:/winnt/fonts");
    i += registerDirectory("/usr/share/X11/fonts", true);
    i += registerDirectory("/usr/X/lib/X11/fonts", true);
    i += registerDirectory("/usr/openwin/lib/X11/fonts", true);
    i += registerDirectory("/usr/share/fonts", true);
    i += registerDirectory("/usr/X11R6/lib/X11/fonts", true);
    i += registerDirectory("/Library/Fonts");
    i += registerDirectory("/System/Library/Fonts");
    return i;
  }

  public Set getRegisteredFonts()
  {
    return Utilities.getKeySet(this.trueTypeFonts);
  }

  public Set getRegisteredFamilies()
  {
    return Utilities.getKeySet(this.fontFamilies);
  }

  public boolean isRegistered(String paramString)
  {
    return this.trueTypeFonts.containsKey(paramString.toLowerCase());
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.FontFactoryImp
 * JD-Core Version:    0.6.0
 */