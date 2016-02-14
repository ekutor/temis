package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.pdf.BaseFont;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

public class Hyphenator
{
  private static Hashtable hyphenTrees = new Hashtable();
  private HyphenationTree hyphenTree = null;
  private int remainCharCount = 2;
  private int pushCharCount = 2;
  private static final String defaultHyphLocation = "com/lowagie/text/pdf/hyphenation/hyph/";
  private static String hyphenDir = "";

  public Hyphenator(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    this.hyphenTree = getHyphenationTree(paramString1, paramString2);
    this.remainCharCount = paramInt1;
    this.pushCharCount = paramInt2;
  }

  public static HyphenationTree getHyphenationTree(String paramString1, String paramString2)
  {
    String str = paramString1;
    if ((paramString2 != null) && (!paramString2.equals("none")))
      str = str + "_" + paramString2;
    if (hyphenTrees.containsKey(str))
      return (HyphenationTree)hyphenTrees.get(str);
    if (hyphenTrees.containsKey(paramString1))
      return (HyphenationTree)hyphenTrees.get(paramString1);
    HyphenationTree localHyphenationTree = getResourceHyphenationTree(str);
    if (localHyphenationTree == null)
      localHyphenationTree = getFileHyphenationTree(str);
    if (localHyphenationTree != null)
      hyphenTrees.put(str, localHyphenationTree);
    return localHyphenationTree;
  }

  public static HyphenationTree getResourceHyphenationTree(String paramString)
  {
    try
    {
      InputStream localInputStream = BaseFont.getResourceStream("com/lowagie/text/pdf/hyphenation/hyph/" + paramString + ".xml");
      if ((localInputStream == null) && (paramString.length() > 2))
        localInputStream = BaseFont.getResourceStream("com/lowagie/text/pdf/hyphenation/hyph/" + paramString.substring(0, 2) + ".xml");
      if (localInputStream == null)
        return null;
      HyphenationTree localHyphenationTree = new HyphenationTree();
      localHyphenationTree.loadSimplePatterns(localInputStream);
      return localHyphenationTree;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static HyphenationTree getFileHyphenationTree(String paramString)
  {
    try
    {
      if (hyphenDir == null)
        return null;
      FileInputStream localFileInputStream = null;
      File localFile = new File(hyphenDir, paramString + ".xml");
      if (localFile.canRead())
        localFileInputStream = new FileInputStream(localFile);
      if ((localFileInputStream == null) && (paramString.length() > 2))
      {
        localFile = new File(hyphenDir, paramString.substring(0, 2) + ".xml");
        if (localFile.canRead())
          localFileInputStream = new FileInputStream(localFile);
      }
      if (localFileInputStream == null)
        return null;
      HyphenationTree localHyphenationTree = new HyphenationTree();
      localHyphenationTree.loadSimplePatterns(localFileInputStream);
      return localHyphenationTree;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static Hyphenation hyphenate(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2)
  {
    HyphenationTree localHyphenationTree = getHyphenationTree(paramString1, paramString2);
    if (localHyphenationTree == null)
      return null;
    return localHyphenationTree.hyphenate(paramString3, paramInt1, paramInt2);
  }

  public static Hyphenation hyphenate(String paramString1, String paramString2, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    HyphenationTree localHyphenationTree = getHyphenationTree(paramString1, paramString2);
    if (localHyphenationTree == null)
      return null;
    return localHyphenationTree.hyphenate(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setMinRemainCharCount(int paramInt)
  {
    this.remainCharCount = paramInt;
  }

  public void setMinPushCharCount(int paramInt)
  {
    this.pushCharCount = paramInt;
  }

  public void setLanguage(String paramString1, String paramString2)
  {
    this.hyphenTree = getHyphenationTree(paramString1, paramString2);
  }

  public Hyphenation hyphenate(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (this.hyphenTree == null)
      return null;
    return this.hyphenTree.hyphenate(paramArrayOfChar, paramInt1, paramInt2, this.remainCharCount, this.pushCharCount);
  }

  public Hyphenation hyphenate(String paramString)
  {
    if (this.hyphenTree == null)
      return null;
    return this.hyphenTree.hyphenate(paramString, this.remainCharCount, this.pushCharCount);
  }

  public static String getHyphenDir()
  {
    return hyphenDir;
  }

  public static void setHyphenDir(String paramString)
  {
    hyphenDir = paramString;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.Hyphenator
 * JD-Core Version:    0.6.0
 */