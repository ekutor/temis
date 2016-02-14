package com.lowagie.text;

import java.awt.Color;
import java.util.Properties;
import java.util.Set;

public final class FontFactory
{
  public static final String COURIER = "Courier";
  public static final String COURIER_BOLD = "Courier-Bold";
  public static final String COURIER_OBLIQUE = "Courier-Oblique";
  public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
  public static final String HELVETICA = "Helvetica";
  public static final String HELVETICA_BOLD = "Helvetica-Bold";
  public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
  public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
  public static final String SYMBOL = "Symbol";
  public static final String TIMES = "Times";
  public static final String TIMES_ROMAN = "Times-Roman";
  public static final String TIMES_BOLD = "Times-Bold";
  public static final String TIMES_ITALIC = "Times-Italic";
  public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
  public static final String ZAPFDINGBATS = "ZapfDingbats";
  private static FontFactoryImp fontImp = new FontFactoryImp();
  public static String defaultEncoding = "Cp1252";
  public static boolean defaultEmbedding = false;

  public static Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat, int paramInt, Color paramColor)
  {
    return fontImp.getFont(paramString1, paramString2, paramBoolean, paramFloat, paramInt, paramColor);
  }

  public static Font getFont(String paramString1, String paramString2, boolean paramBoolean1, float paramFloat, int paramInt, Color paramColor, boolean paramBoolean2)
  {
    return fontImp.getFont(paramString1, paramString2, paramBoolean1, paramFloat, paramInt, paramColor, paramBoolean2);
  }

  public static Font getFont(Properties paramProperties)
  {
    fontImp.defaultEmbedding = defaultEmbedding;
    fontImp.defaultEncoding = defaultEncoding;
    return fontImp.getFont(paramProperties);
  }

  public static Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat, int paramInt)
  {
    return getFont(paramString1, paramString2, paramBoolean, paramFloat, paramInt, null);
  }

  public static Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat)
  {
    return getFont(paramString1, paramString2, paramBoolean, paramFloat, -1, null);
  }

  public static Font getFont(String paramString1, String paramString2, boolean paramBoolean)
  {
    return getFont(paramString1, paramString2, paramBoolean, -1.0F, -1, null);
  }

  public static Font getFont(String paramString1, String paramString2, float paramFloat, int paramInt, Color paramColor)
  {
    return getFont(paramString1, paramString2, defaultEmbedding, paramFloat, paramInt, paramColor);
  }

  public static Font getFont(String paramString1, String paramString2, float paramFloat, int paramInt)
  {
    return getFont(paramString1, paramString2, defaultEmbedding, paramFloat, paramInt, null);
  }

  public static Font getFont(String paramString1, String paramString2, float paramFloat)
  {
    return getFont(paramString1, paramString2, defaultEmbedding, paramFloat, -1, null);
  }

  public static Font getFont(String paramString1, String paramString2)
  {
    return getFont(paramString1, paramString2, defaultEmbedding, -1.0F, -1, null);
  }

  public static Font getFont(String paramString, float paramFloat, int paramInt, Color paramColor)
  {
    return getFont(paramString, defaultEncoding, defaultEmbedding, paramFloat, paramInt, paramColor);
  }

  public static Font getFont(String paramString, float paramFloat, Color paramColor)
  {
    return getFont(paramString, defaultEncoding, defaultEmbedding, paramFloat, -1, paramColor);
  }

  public static Font getFont(String paramString, float paramFloat, int paramInt)
  {
    return getFont(paramString, defaultEncoding, defaultEmbedding, paramFloat, paramInt, null);
  }

  public static Font getFont(String paramString, float paramFloat)
  {
    return getFont(paramString, defaultEncoding, defaultEmbedding, paramFloat, -1, null);
  }

  public static Font getFont(String paramString)
  {
    return getFont(paramString, defaultEncoding, defaultEmbedding, -1.0F, -1, null);
  }

  public void registerFamily(String paramString1, String paramString2, String paramString3)
  {
    fontImp.registerFamily(paramString1, paramString2, paramString3);
  }

  public static void register(String paramString)
  {
    register(paramString, null);
  }

  public static void register(String paramString1, String paramString2)
  {
    fontImp.register(paramString1, paramString2);
  }

  public static int registerDirectory(String paramString)
  {
    return fontImp.registerDirectory(paramString);
  }

  public static int registerDirectory(String paramString, boolean paramBoolean)
  {
    return fontImp.registerDirectory(paramString, paramBoolean);
  }

  public static int registerDirectories()
  {
    return fontImp.registerDirectories();
  }

  public static Set getRegisteredFonts()
  {
    return fontImp.getRegisteredFonts();
  }

  public static Set getRegisteredFamilies()
  {
    return fontImp.getRegisteredFamilies();
  }

  public static boolean contains(String paramString)
  {
    return fontImp.isRegistered(paramString);
  }

  public static boolean isRegistered(String paramString)
  {
    return fontImp.isRegistered(paramString);
  }

  public static FontFactoryImp getFontImp()
  {
    return fontImp;
  }

  public static void setFontImp(FontFactoryImp paramFontFactoryImp)
  {
    if (paramFontFactoryImp == null)
      throw new NullPointerException("FontFactoryImp cannot be null.");
    fontImp = paramFontFactoryImp;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.FontFactory
 * JD-Core Version:    0.6.0
 */