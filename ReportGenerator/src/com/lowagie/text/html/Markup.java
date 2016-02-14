package com.lowagie.text.html;

import java.awt.Color;
import java.util.Properties;
import java.util.StringTokenizer;

public class Markup
{
  public static final String ITEXT_TAG = "tag";
  public static final String HTML_TAG_BODY = "body";
  public static final String HTML_TAG_DIV = "div";
  public static final String HTML_TAG_LINK = "link";
  public static final String HTML_TAG_SPAN = "span";
  public static final String HTML_ATTR_HEIGHT = "height";
  public static final String HTML_ATTR_HREF = "href";
  public static final String HTML_ATTR_REL = "rel";
  public static final String HTML_ATTR_STYLE = "style";
  public static final String HTML_ATTR_TYPE = "type";
  public static final String HTML_ATTR_STYLESHEET = "stylesheet";
  public static final String HTML_ATTR_WIDTH = "width";
  public static final String HTML_ATTR_CSS_CLASS = "class";
  public static final String HTML_ATTR_CSS_ID = "id";
  public static final String HTML_VALUE_JAVASCRIPT = "text/javascript";
  public static final String HTML_VALUE_CSS = "text/css";
  public static final String CSS_KEY_BGCOLOR = "background-color";
  public static final String CSS_KEY_COLOR = "color";
  public static final String CSS_KEY_DISPLAY = "display";
  public static final String CSS_KEY_FONTFAMILY = "font-family";
  public static final String CSS_KEY_FONTSIZE = "font-size";
  public static final String CSS_KEY_FONTSTYLE = "font-style";
  public static final String CSS_KEY_FONTWEIGHT = "font-weight";
  public static final String CSS_KEY_LINEHEIGHT = "line-height";
  public static final String CSS_KEY_MARGIN = "margin";
  public static final String CSS_KEY_MARGINLEFT = "margin-left";
  public static final String CSS_KEY_MARGINRIGHT = "margin-right";
  public static final String CSS_KEY_MARGINTOP = "margin-top";
  public static final String CSS_KEY_MARGINBOTTOM = "margin-bottom";
  public static final String CSS_KEY_PADDING = "padding";
  public static final String CSS_KEY_PADDINGLEFT = "padding-left";
  public static final String CSS_KEY_PADDINGRIGHT = "padding-right";
  public static final String CSS_KEY_PADDINGTOP = "padding-top";
  public static final String CSS_KEY_PADDINGBOTTOM = "padding-bottom";
  public static final String CSS_KEY_BORDERCOLOR = "border-color";
  public static final String CSS_KEY_BORDERWIDTH = "border-width";
  public static final String CSS_KEY_BORDERWIDTHLEFT = "border-left-width";
  public static final String CSS_KEY_BORDERWIDTHRIGHT = "border-right-width";
  public static final String CSS_KEY_BORDERWIDTHTOP = "border-top-width";
  public static final String CSS_KEY_BORDERWIDTHBOTTOM = "border-bottom-width";
  public static final String CSS_KEY_PAGE_BREAK_AFTER = "page-break-after";
  public static final String CSS_KEY_PAGE_BREAK_BEFORE = "page-break-before";
  public static final String CSS_KEY_TEXTALIGN = "text-align";
  public static final String CSS_KEY_TEXTDECORATION = "text-decoration";
  public static final String CSS_KEY_VERTICALALIGN = "vertical-align";
  public static final String CSS_KEY_VISIBILITY = "visibility";
  public static final String CSS_VALUE_ALWAYS = "always";
  public static final String CSS_VALUE_BLOCK = "block";
  public static final String CSS_VALUE_BOLD = "bold";
  public static final String CSS_VALUE_HIDDEN = "hidden";
  public static final String CSS_VALUE_INLINE = "inline";
  public static final String CSS_VALUE_ITALIC = "italic";
  public static final String CSS_VALUE_LINETHROUGH = "line-through";
  public static final String CSS_VALUE_LISTITEM = "list-item";
  public static final String CSS_VALUE_NONE = "none";
  public static final String CSS_VALUE_NORMAL = "normal";
  public static final String CSS_VALUE_OBLIQUE = "oblique";
  public static final String CSS_VALUE_TABLE = "table";
  public static final String CSS_VALUE_TABLEROW = "table-row";
  public static final String CSS_VALUE_TABLECELL = "table-cell";
  public static final String CSS_VALUE_TEXTALIGNLEFT = "left";
  public static final String CSS_VALUE_TEXTALIGNRIGHT = "right";
  public static final String CSS_VALUE_TEXTALIGNCENTER = "center";
  public static final String CSS_VALUE_TEXTALIGNJUSTIFY = "justify";
  public static final String CSS_VALUE_UNDERLINE = "underline";
  public static final float DEFAULT_FONT_SIZE = 12.0F;

  public static float parseLength(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    int k = 1;
    while ((k != 0) && (i < j))
      switch (paramString.charAt(i))
      {
      case '+':
      case '-':
      case '.':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        i++;
        break;
      case ',':
      case '/':
      default:
        k = 0;
      }
    if (i == 0)
      return 0.0F;
    if (i == j)
      return Float.parseFloat(paramString + "f");
    float f = Float.parseFloat(paramString.substring(0, i) + "f");
    paramString = paramString.substring(i);
    if (paramString.startsWith("in"))
      return f * 72.0F;
    if (paramString.startsWith("cm"))
      return f / 2.54F * 72.0F;
    if (paramString.startsWith("mm"))
      return f / 25.4F * 72.0F;
    if (paramString.startsWith("pc"))
      return f * 12.0F;
    return f;
  }

  public static float parseLength(String paramString, float paramFloat)
  {
    if (paramString == null)
      return 0.0F;
    int i = 0;
    int j = paramString.length();
    int k = 1;
    while ((k != 0) && (i < j))
      switch (paramString.charAt(i))
      {
      case '+':
      case '-':
      case '.':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        i++;
        break;
      case ',':
      case '/':
      default:
        k = 0;
      }
    if (i == 0)
      return 0.0F;
    if (i == j)
      return Float.parseFloat(paramString + "f");
    float f = Float.parseFloat(paramString.substring(0, i) + "f");
    paramString = paramString.substring(i);
    if (paramString.startsWith("in"))
      return f * 72.0F;
    if (paramString.startsWith("cm"))
      return f / 2.54F * 72.0F;
    if (paramString.startsWith("mm"))
      return f / 25.4F * 72.0F;
    if (paramString.startsWith("pc"))
      return f * 12.0F;
    if (paramString.startsWith("em"))
      return f * paramFloat;
    if (paramString.startsWith("ex"))
      return f * paramFloat / 2.0F;
    return f;
  }

  public static Color decodeColor(String paramString)
  {
    if (paramString == null)
      return null;
    paramString = paramString.toLowerCase().trim();
    try
    {
      return WebColors.getRGBColor(paramString);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
    }
    return null;
  }

  public static Properties parseAttributes(String paramString)
  {
    Properties localProperties = new Properties();
    if (paramString == null)
      return localProperties;
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, ";");
    while (localStringTokenizer1.hasMoreTokens())
    {
      StringTokenizer localStringTokenizer2 = new StringTokenizer(localStringTokenizer1.nextToken(), ":");
      if (!localStringTokenizer2.hasMoreTokens())
        continue;
      String str1 = localStringTokenizer2.nextToken().trim();
      if (!localStringTokenizer2.hasMoreTokens())
        continue;
      String str2 = localStringTokenizer2.nextToken().trim();
      if (str2.startsWith("\""))
        str2 = str2.substring(1);
      if (str2.endsWith("\""))
        str2 = str2.substring(0, str2.length() - 1);
      localProperties.setProperty(str1.toLowerCase(), str2);
    }
    return localProperties;
  }

  public static String removeComment(String paramString1, String paramString2, String paramString3)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = paramString3.length();
    for (int k = paramString1.indexOf(paramString2, i); k > -1; k = paramString1.indexOf(paramString2, i))
    {
      localStringBuffer.append(paramString1.substring(i, k));
      i = paramString1.indexOf(paramString3, k) + j;
    }
    localStringBuffer.append(paramString1.substring(i));
    return localStringBuffer.toString();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.Markup
 * JD-Core Version:    0.6.0
 */