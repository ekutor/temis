package com.lowagie.text.html;

import com.lowagie.text.ElementTags;
import java.util.HashMap;

public class HtmlTagMap extends HashMap
{
  private static final long serialVersionUID = 5287430058473705350L;

  public HtmlTagMap()
  {
    HtmlPeer localHtmlPeer = new HtmlPeer("itext", "html");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "span");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("chunk", "font");
    localHtmlPeer.addAlias("font", "face");
    localHtmlPeer.addAlias("size", "point-size");
    localHtmlPeer.addAlias("color", "color");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("anchor", "a");
    localHtmlPeer.addAlias("name", "name");
    localHtmlPeer.addAlias("reference", "href");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", "p");
    localHtmlPeer.addAlias("align", "align");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", "div");
    localHtmlPeer.addAlias("align", "align");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", HtmlTags.H[0]);
    localHtmlPeer.addValue("size", "20");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", HtmlTags.H[1]);
    localHtmlPeer.addValue("size", "18");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", HtmlTags.H[2]);
    localHtmlPeer.addValue("size", "16");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", HtmlTags.H[3]);
    localHtmlPeer.addValue("size", "14");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", HtmlTags.H[4]);
    localHtmlPeer.addValue("size", "12");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("paragraph", HtmlTags.H[5]);
    localHtmlPeer.addValue("size", "10");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("list", "ol");
    localHtmlPeer.addValue("numbered", "true");
    localHtmlPeer.addValue("symbolindent", "20");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("list", "ul");
    localHtmlPeer.addValue("numbered", "false");
    localHtmlPeer.addValue("symbolindent", "20");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("listitem", "li");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "i");
    localHtmlPeer.addValue("fontstyle", "italic");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "em");
    localHtmlPeer.addValue("fontstyle", "italic");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "b");
    localHtmlPeer.addValue("fontstyle", "bold");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "strong");
    localHtmlPeer.addValue("fontstyle", "bold");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "s");
    localHtmlPeer.addValue("fontstyle", "line-through");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "code");
    localHtmlPeer.addValue("font", "Courier");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "var");
    localHtmlPeer.addValue("font", "Courier");
    localHtmlPeer.addValue("fontstyle", "italic");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("phrase", "u");
    localHtmlPeer.addValue("fontstyle", "underline");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("chunk", "sup");
    localHtmlPeer.addValue(ElementTags.SUBSUPSCRIPT, "6.0");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("chunk", "sub");
    localHtmlPeer.addValue(ElementTags.SUBSUPSCRIPT, "-6.0");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("horizontalrule", "hr");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("table", "table");
    localHtmlPeer.addAlias("width", "width");
    localHtmlPeer.addAlias("backgroundcolor", "bgcolor");
    localHtmlPeer.addAlias("bordercolor", "bordercolor");
    localHtmlPeer.addAlias("columns", "cols");
    localHtmlPeer.addAlias("cellpadding", "cellpadding");
    localHtmlPeer.addAlias("cellspacing", "cellspacing");
    localHtmlPeer.addAlias("borderwidth", "border");
    localHtmlPeer.addAlias("align", "align");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("row", "tr");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("cell", "td");
    localHtmlPeer.addAlias("width", "width");
    localHtmlPeer.addAlias("backgroundcolor", "bgcolor");
    localHtmlPeer.addAlias("bordercolor", "bordercolor");
    localHtmlPeer.addAlias("colspan", "colspan");
    localHtmlPeer.addAlias("rowspan", "rowspan");
    localHtmlPeer.addAlias("nowrap", "nowrap");
    localHtmlPeer.addAlias("horizontalalign", "align");
    localHtmlPeer.addAlias("verticalalign", "valign");
    localHtmlPeer.addValue("header", "false");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("cell", "th");
    localHtmlPeer.addAlias("width", "width");
    localHtmlPeer.addAlias("backgroundcolor", "bgcolor");
    localHtmlPeer.addAlias("bordercolor", "bordercolor");
    localHtmlPeer.addAlias("colspan", "colspan");
    localHtmlPeer.addAlias("rowspan", "rowspan");
    localHtmlPeer.addAlias("nowrap", "nowrap");
    localHtmlPeer.addAlias("horizontalalign", "align");
    localHtmlPeer.addAlias("verticalalign", "valign");
    localHtmlPeer.addValue("header", "true");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("image", "img");
    localHtmlPeer.addAlias("url", "src");
    localHtmlPeer.addAlias("alt", "alt");
    localHtmlPeer.addAlias("plainwidth", "width");
    localHtmlPeer.addAlias("plainheight", "height");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
    localHtmlPeer = new HtmlPeer("newline", "br");
    put(localHtmlPeer.getAlias(), localHtmlPeer);
  }

  public static boolean isHtml(String paramString)
  {
    return "html".equalsIgnoreCase(paramString);
  }

  public static boolean isHead(String paramString)
  {
    return "head".equalsIgnoreCase(paramString);
  }

  public static boolean isMeta(String paramString)
  {
    return "meta".equalsIgnoreCase(paramString);
  }

  public static boolean isLink(String paramString)
  {
    return "link".equalsIgnoreCase(paramString);
  }

  public static boolean isTitle(String paramString)
  {
    return "title".equalsIgnoreCase(paramString);
  }

  public static boolean isBody(String paramString)
  {
    return "body".equalsIgnoreCase(paramString);
  }

  public static boolean isSpecialTag(String paramString)
  {
    return (isHtml(paramString)) || (isHead(paramString)) || (isMeta(paramString)) || (isLink(paramString)) || (isBody(paramString));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.HtmlTagMap
 * JD-Core Version:    0.6.0
 */