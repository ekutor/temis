package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;

public class DefaultFontMapper
  implements FontMapper
{
  private HashMap aliases = new HashMap();
  private HashMap mapper = new HashMap();

  public BaseFont awtToPdf(Font paramFont)
  {
    try
    {
      BaseFontParameters localBaseFontParameters = getBaseFontParameters(paramFont.getFontName());
      if (localBaseFontParameters != null)
        return BaseFont.createFont(localBaseFontParameters.fontName, localBaseFontParameters.encoding, localBaseFontParameters.embedded, localBaseFontParameters.cached, localBaseFontParameters.ttfAfm, localBaseFontParameters.pfb);
      String str1 = null;
      String str2 = paramFont.getName();
      if ((str2.equalsIgnoreCase("DialogInput")) || (str2.equalsIgnoreCase("Monospaced")) || (str2.equalsIgnoreCase("Courier")))
      {
        if (paramFont.isItalic())
        {
          if (paramFont.isBold())
            str1 = "Courier-BoldOblique";
          else
            str1 = "Courier-Oblique";
        }
        else if (paramFont.isBold())
          str1 = "Courier-Bold";
        else
          str1 = "Courier";
      }
      else if ((str2.equalsIgnoreCase("Serif")) || (str2.equalsIgnoreCase("TimesRoman")))
      {
        if (paramFont.isItalic())
        {
          if (paramFont.isBold())
            str1 = "Times-BoldItalic";
          else
            str1 = "Times-Italic";
        }
        else if (paramFont.isBold())
          str1 = "Times-Bold";
        else
          str1 = "Times-Roman";
      }
      else if (paramFont.isItalic())
      {
        if (paramFont.isBold())
          str1 = "Helvetica-BoldOblique";
        else
          str1 = "Helvetica-Oblique";
      }
      else if (paramFont.isBold())
        str1 = "Helvetica-Bold";
      else
        str1 = "Helvetica";
      return BaseFont.createFont(str1, "Cp1252", false);
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public Font pdfToAwt(BaseFont paramBaseFont, int paramInt)
  {
    String[][] arrayOfString = paramBaseFont.getFullFontName();
    if (arrayOfString.length == 1)
      return new Font(arrayOfString[0][3], 0, paramInt);
    Object localObject1 = null;
    Object localObject2 = null;
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String[] arrayOfString1 = arrayOfString[i];
      if ((arrayOfString1[0].equals("1")) && (arrayOfString1[1].equals("0")))
      {
        localObject1 = arrayOfString1[3];
      }
      else
      {
        if (!arrayOfString1[2].equals("1033"))
          continue;
        localObject2 = arrayOfString1[3];
        break;
      }
    }
    Object localObject3 = localObject2;
    if (localObject3 == null)
      localObject3 = localObject1;
    if (localObject3 == null)
      localObject3 = arrayOfString[0][3];
    return (Font)new Font((String)localObject3, 0, paramInt);
  }

  public void putName(String paramString, BaseFontParameters paramBaseFontParameters)
  {
    this.mapper.put(paramString, paramBaseFontParameters);
  }

  public void putAlias(String paramString1, String paramString2)
  {
    this.aliases.put(paramString1, paramString2);
  }

  public BaseFontParameters getBaseFontParameters(String paramString)
  {
    String str = (String)this.aliases.get(paramString);
    if (str == null)
      return (BaseFontParameters)this.mapper.get(paramString);
    BaseFontParameters localBaseFontParameters = (BaseFontParameters)this.mapper.get(str);
    if (localBaseFontParameters == null)
      return (BaseFontParameters)this.mapper.get(paramString);
    return localBaseFontParameters;
  }

  public void insertNames(Object[] paramArrayOfObject, String paramString)
  {
    String[][] arrayOfString = (String[][])paramArrayOfObject[2];
    String str = null;
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String[] arrayOfString1 = arrayOfString[i];
      if (!arrayOfString1[2].equals("1033"))
        continue;
      str = arrayOfString1[3];
      break;
    }
    if (str == null)
      str = arrayOfString[0][3];
    BaseFontParameters localBaseFontParameters = new BaseFontParameters(paramString);
    this.mapper.put(str, localBaseFontParameters);
    for (int j = 0; j < arrayOfString.length; j++)
      this.aliases.put(arrayOfString[j][3], str);
    this.aliases.put(paramArrayOfObject[0], str);
  }

  public int insertDirectory(String paramString)
  {
    File localFile = new File(paramString);
    if ((!localFile.exists()) || (!localFile.isDirectory()))
      return 0;
    File[] arrayOfFile = localFile.listFiles();
    if (arrayOfFile == null)
      return 0;
    int i = 0;
    for (int j = 0; j < arrayOfFile.length; j++)
    {
      localFile = arrayOfFile[j];
      String str1 = localFile.getPath().toLowerCase();
      try
      {
        Object localObject;
        if ((str1.endsWith(".ttf")) || (str1.endsWith(".otf")) || (str1.endsWith(".afm")))
        {
          localObject = BaseFont.getAllFontNames(localFile.getPath(), "Cp1252", null);
          insertNames(localObject, localFile.getPath());
          i++;
        }
        else if (str1.endsWith(".ttc"))
        {
          localObject = BaseFont.enumerateTTCNames(localFile.getPath());
          for (int k = 0; k < localObject.length; k++)
          {
            String str2 = localFile.getPath() + "," + k;
            Object[] arrayOfObject = BaseFont.getAllFontNames(str2, "Cp1252", null);
            insertNames(arrayOfObject, str2);
          }
          i++;
        }
      }
      catch (Exception localException)
      {
      }
    }
    return i;
  }

  public HashMap getMapper()
  {
    return this.mapper;
  }

  public HashMap getAliases()
  {
    return this.aliases;
  }

  public static class BaseFontParameters
  {
    public String fontName;
    public String encoding;
    public boolean embedded;
    public boolean cached;
    public byte[] ttfAfm;
    public byte[] pfb;

    public BaseFontParameters(String paramString)
    {
      this.fontName = paramString;
      this.encoding = "Cp1252";
      this.embedded = true;
      this.cached = true;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.DefaultFontMapper
 * JD-Core Version:    0.6.0
 */