package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;

public class EntitiesToUnicode
{
  public static final HashMap map = new HashMap();

  public static char decodeEntity(String paramString)
  {
    if (paramString.startsWith("#x"))
      try
      {
        return (char)Integer.parseInt(paramString.substring(2), 16);
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        return '\000';
      }
    if (paramString.startsWith("#"))
      try
      {
        return (char)Integer.parseInt(paramString.substring(1));
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        return '\000';
      }
    Character localCharacter = (Character)map.get(paramString);
    if (localCharacter == null)
      return '\000';
    return localCharacter.charValue();
  }

  public static String decodeString(String paramString)
  {
    int i = paramString.indexOf('&');
    if (i == -1)
      return paramString;
    StringBuffer localStringBuffer = new StringBuffer(paramString.substring(0, i));
    while (true)
    {
      int j = paramString.indexOf(';', i);
      if (j == -1)
      {
        localStringBuffer.append(paramString.substring(i));
        return localStringBuffer.toString();
      }
      for (int k = paramString.indexOf('&', i + 1); (k != -1) && (k < j); k = paramString.indexOf('&', i + 1))
      {
        localStringBuffer.append(paramString.substring(i, k));
        i = k;
      }
      char c = decodeEntity(paramString.substring(i + 1, j));
      if (paramString.length() < j + 1)
        return localStringBuffer.toString();
      if (c == 0)
        localStringBuffer.append(paramString.substring(i, j + 1));
      else
        localStringBuffer.append(c);
      i = paramString.indexOf('&', j);
      if (i == -1)
      {
        localStringBuffer.append(paramString.substring(j + 1));
        return localStringBuffer.toString();
      }
      localStringBuffer.append(paramString.substring(j + 1, i));
    }
  }

  static
  {
    map.put("nbsp", new Character(' '));
    map.put("iexcl", new Character('¡'));
    map.put("cent", new Character('¢'));
    map.put("pound", new Character('£'));
    map.put("curren", new Character('¤'));
    map.put("yen", new Character('¥'));
    map.put("brvbar", new Character('¦'));
    map.put("sect", new Character('§'));
    map.put("uml", new Character('¨'));
    map.put("copy", new Character('©'));
    map.put("ordf", new Character('ª'));
    map.put("laquo", new Character('«'));
    map.put("not", new Character('¬'));
    map.put("shy", new Character('­'));
    map.put("reg", new Character('®'));
    map.put("macr", new Character('¯'));
    map.put("deg", new Character('°'));
    map.put("plusmn", new Character('±'));
    map.put("sup2", new Character('²'));
    map.put("sup3", new Character('³'));
    map.put("acute", new Character('´'));
    map.put("micro", new Character('µ'));
    map.put("para", new Character('¶'));
    map.put("middot", new Character('·'));
    map.put("cedil", new Character('¸'));
    map.put("sup1", new Character('¹'));
    map.put("ordm", new Character('º'));
    map.put("raquo", new Character('»'));
    map.put("frac14", new Character('¼'));
    map.put("frac12", new Character('½'));
    map.put("frac34", new Character('¾'));
    map.put("iquest", new Character('¿'));
    map.put("Agrave", new Character('À'));
    map.put("Aacute", new Character('Á'));
    map.put("Acirc", new Character('Â'));
    map.put("Atilde", new Character('Ã'));
    map.put("Auml", new Character('Ä'));
    map.put("Aring", new Character('Å'));
    map.put("AElig", new Character('Æ'));
    map.put("Ccedil", new Character('Ç'));
    map.put("Egrave", new Character('È'));
    map.put("Eacute", new Character('É'));
    map.put("Ecirc", new Character('Ê'));
    map.put("Euml", new Character('Ë'));
    map.put("Igrave", new Character('Ì'));
    map.put("Iacute", new Character('Í'));
    map.put("Icirc", new Character('Î'));
    map.put("Iuml", new Character('Ï'));
    map.put("ETH", new Character('Ð'));
    map.put("Ntilde", new Character('Ñ'));
    map.put("Ograve", new Character('Ò'));
    map.put("Oacute", new Character('Ó'));
    map.put("Ocirc", new Character('Ô'));
    map.put("Otilde", new Character('Õ'));
    map.put("Ouml", new Character('Ö'));
    map.put("times", new Character('×'));
    map.put("Oslash", new Character('Ø'));
    map.put("Ugrave", new Character('Ù'));
    map.put("Uacute", new Character('Ú'));
    map.put("Ucirc", new Character('Û'));
    map.put("Uuml", new Character('Ü'));
    map.put("Yacute", new Character('Ý'));
    map.put("THORN", new Character('Þ'));
    map.put("szlig", new Character('ß'));
    map.put("agrave", new Character('à'));
    map.put("aacute", new Character('á'));
    map.put("acirc", new Character('â'));
    map.put("atilde", new Character('ã'));
    map.put("auml", new Character('ä'));
    map.put("aring", new Character('å'));
    map.put("aelig", new Character('æ'));
    map.put("ccedil", new Character('ç'));
    map.put("egrave", new Character('è'));
    map.put("eacute", new Character('é'));
    map.put("ecirc", new Character('ê'));
    map.put("euml", new Character('ë'));
    map.put("igrave", new Character('ì'));
    map.put("iacute", new Character('í'));
    map.put("icirc", new Character('î'));
    map.put("iuml", new Character('ï'));
    map.put("eth", new Character('ð'));
    map.put("ntilde", new Character('ñ'));
    map.put("ograve", new Character('ò'));
    map.put("oacute", new Character('ó'));
    map.put("ocirc", new Character('ô'));
    map.put("otilde", new Character('õ'));
    map.put("ouml", new Character('ö'));
    map.put("divide", new Character('÷'));
    map.put("oslash", new Character('ø'));
    map.put("ugrave", new Character('ù'));
    map.put("uacute", new Character('ú'));
    map.put("ucirc", new Character('û'));
    map.put("uuml", new Character('ü'));
    map.put("yacute", new Character('ý'));
    map.put("thorn", new Character('þ'));
    map.put("yuml", new Character('ÿ'));
    map.put("fnof", new Character('ƒ'));
    map.put("Alpha", new Character('Α'));
    map.put("Beta", new Character('Β'));
    map.put("Gamma", new Character('Γ'));
    map.put("Delta", new Character('Δ'));
    map.put("Epsilon", new Character('Ε'));
    map.put("Zeta", new Character('Ζ'));
    map.put("Eta", new Character('Η'));
    map.put("Theta", new Character('Θ'));
    map.put("Iota", new Character('Ι'));
    map.put("Kappa", new Character('Κ'));
    map.put("Lambda", new Character('Λ'));
    map.put("Mu", new Character('Μ'));
    map.put("Nu", new Character('Ν'));
    map.put("Xi", new Character('Ξ'));
    map.put("Omicron", new Character('Ο'));
    map.put("Pi", new Character('Π'));
    map.put("Rho", new Character('Ρ'));
    map.put("Sigma", new Character('Σ'));
    map.put("Tau", new Character('Τ'));
    map.put("Upsilon", new Character('Υ'));
    map.put("Phi", new Character('Φ'));
    map.put("Chi", new Character('Χ'));
    map.put("Psi", new Character('Ψ'));
    map.put("Omega", new Character('Ω'));
    map.put("alpha", new Character('α'));
    map.put("beta", new Character('β'));
    map.put("gamma", new Character('γ'));
    map.put("delta", new Character('δ'));
    map.put("epsilon", new Character('ε'));
    map.put("zeta", new Character('ζ'));
    map.put("eta", new Character('η'));
    map.put("theta", new Character('θ'));
    map.put("iota", new Character('ι'));
    map.put("kappa", new Character('κ'));
    map.put("lambda", new Character('λ'));
    map.put("mu", new Character('μ'));
    map.put("nu", new Character('ν'));
    map.put("xi", new Character('ξ'));
    map.put("omicron", new Character('ο'));
    map.put("pi", new Character('π'));
    map.put("rho", new Character('ρ'));
    map.put("sigmaf", new Character('ς'));
    map.put("sigma", new Character('σ'));
    map.put("tau", new Character('τ'));
    map.put("upsilon", new Character('υ'));
    map.put("phi", new Character('φ'));
    map.put("chi", new Character('χ'));
    map.put("psi", new Character('ψ'));
    map.put("omega", new Character('ω'));
    map.put("thetasym", new Character('ϑ'));
    map.put("upsih", new Character('ϒ'));
    map.put("piv", new Character('ϖ'));
    map.put("bull", new Character('•'));
    map.put("hellip", new Character('…'));
    map.put("prime", new Character('′'));
    map.put("Prime", new Character('″'));
    map.put("oline", new Character('‾'));
    map.put("frasl", new Character('⁄'));
    map.put("weierp", new Character('℘'));
    map.put("image", new Character('ℑ'));
    map.put("real", new Character('ℜ'));
    map.put("trade", new Character('™'));
    map.put("alefsym", new Character('ℵ'));
    map.put("larr", new Character('←'));
    map.put("uarr", new Character('↑'));
    map.put("rarr", new Character('→'));
    map.put("darr", new Character('↓'));
    map.put("harr", new Character('↔'));
    map.put("crarr", new Character('↵'));
    map.put("lArr", new Character('⇐'));
    map.put("uArr", new Character('⇑'));
    map.put("rArr", new Character('⇒'));
    map.put("dArr", new Character('⇓'));
    map.put("hArr", new Character('⇔'));
    map.put("forall", new Character('∀'));
    map.put("part", new Character('∂'));
    map.put("exist", new Character('∃'));
    map.put("empty", new Character('∅'));
    map.put("nabla", new Character('∇'));
    map.put("isin", new Character('∈'));
    map.put("notin", new Character('∉'));
    map.put("ni", new Character('∋'));
    map.put("prod", new Character('∏'));
    map.put("sum", new Character('∑'));
    map.put("minus", new Character('−'));
    map.put("lowast", new Character('∗'));
    map.put("radic", new Character('√'));
    map.put("prop", new Character('∝'));
    map.put("infin", new Character('∞'));
    map.put("ang", new Character('∠'));
    map.put("and", new Character('∧'));
    map.put("or", new Character('∨'));
    map.put("cap", new Character('∩'));
    map.put("cup", new Character('∪'));
    map.put("int", new Character('∫'));
    map.put("there4", new Character('∴'));
    map.put("sim", new Character('∼'));
    map.put("cong", new Character('≅'));
    map.put("asymp", new Character('≈'));
    map.put("ne", new Character('≠'));
    map.put("equiv", new Character('≡'));
    map.put("le", new Character('≤'));
    map.put("ge", new Character('≥'));
    map.put("sub", new Character('⊂'));
    map.put("sup", new Character('⊃'));
    map.put("nsub", new Character('⊄'));
    map.put("sube", new Character('⊆'));
    map.put("supe", new Character('⊇'));
    map.put("oplus", new Character('⊕'));
    map.put("otimes", new Character('⊗'));
    map.put("perp", new Character('⊥'));
    map.put("sdot", new Character('⋅'));
    map.put("lceil", new Character('⌈'));
    map.put("rceil", new Character('⌉'));
    map.put("lfloor", new Character('⌊'));
    map.put("rfloor", new Character('⌋'));
    map.put("lang", new Character('〈'));
    map.put("rang", new Character('〉'));
    map.put("loz", new Character('◊'));
    map.put("spades", new Character('♠'));
    map.put("clubs", new Character('♣'));
    map.put("hearts", new Character('♥'));
    map.put("diams", new Character('♦'));
    map.put("quot", new Character('"'));
    map.put("amp", new Character('&'));
    map.put("apos", new Character('\''));
    map.put("lt", new Character('<'));
    map.put("gt", new Character('>'));
    map.put("OElig", new Character('Œ'));
    map.put("oelig", new Character('œ'));
    map.put("Scaron", new Character('Š'));
    map.put("scaron", new Character('š'));
    map.put("Yuml", new Character('Ÿ'));
    map.put("circ", new Character('ˆ'));
    map.put("tilde", new Character('˜'));
    map.put("ensp", new Character(' '));
    map.put("emsp", new Character(' '));
    map.put("thinsp", new Character(' '));
    map.put("zwnj", new Character('‌'));
    map.put("zwj", new Character('‍'));
    map.put("lrm", new Character('‎'));
    map.put("rlm", new Character('‏'));
    map.put("ndash", new Character('–'));
    map.put("mdash", new Character('—'));
    map.put("lsquo", new Character('‘'));
    map.put("rsquo", new Character('’'));
    map.put("sbquo", new Character('‚'));
    map.put("ldquo", new Character('“'));
    map.put("rdquo", new Character('”'));
    map.put("bdquo", new Character('„'));
    map.put("dagger", new Character('†'));
    map.put("Dagger", new Character('‡'));
    map.put("permil", new Character('‰'));
    map.put("lsaquo", new Character('‹'));
    map.put("rsaquo", new Character('›'));
    map.put("euro", new Character('€'));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.simpleparser.EntitiesToUnicode
 * JD-Core Version:    0.6.0
 */