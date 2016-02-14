package com.lowagie.text;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Chunk
  implements Element
{
  public static final String OBJECT_REPLACEMENT_CHARACTER = "￼";
  public static final Chunk NEWLINE = new Chunk("\n");
  public static final Chunk NEXTPAGE = new Chunk("");
  protected StringBuffer content = null;
  protected Font font = null;
  protected HashMap attributes = null;
  public static final String SEPARATOR = "SEPARATOR";
  public static final String TAB = "TAB";
  public static final String HSCALE = "HSCALE";
  public static final String UNDERLINE = "UNDERLINE";
  public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
  public static final String SKEW = "SKEW";
  public static final String BACKGROUND = "BACKGROUND";
  public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
  public static final String SPLITCHARACTER = "SPLITCHARACTER";
  public static final String HYPHENATION = "HYPHENATION";
  public static final String REMOTEGOTO = "REMOTEGOTO";
  public static final String LOCALGOTO = "LOCALGOTO";
  public static final String LOCALDESTINATION = "LOCALDESTINATION";
  public static final String GENERICTAG = "GENERICTAG";
  public static final String IMAGE = "IMAGE";
  public static final String ACTION = "ACTION";
  public static final String NEWPAGE = "NEWPAGE";
  public static final String PDFANNOTATION = "PDFANNOTATION";
  public static final String COLOR = "COLOR";
  public static final String ENCODING = "ENCODING";

  public Chunk()
  {
    this.content = new StringBuffer();
    this.font = new Font();
  }

  public Chunk(Chunk paramChunk)
  {
    if (paramChunk.content != null)
      this.content = new StringBuffer(paramChunk.content.toString());
    if (paramChunk.font != null)
      this.font = new Font(paramChunk.font);
    if (paramChunk.attributes != null)
      this.attributes = new HashMap(paramChunk.attributes);
  }

  public Chunk(String paramString, Font paramFont)
  {
    this.content = new StringBuffer(paramString);
    this.font = paramFont;
  }

  public Chunk(String paramString)
  {
    this(paramString, new Font());
  }

  public Chunk(char paramChar, Font paramFont)
  {
    this.content = new StringBuffer();
    this.content.append(paramChar);
    this.font = paramFont;
  }

  public Chunk(char paramChar)
  {
    this(paramChar, new Font());
  }

  public Chunk(Image paramImage, float paramFloat1, float paramFloat2)
  {
    this("￼", new Font());
    Image localImage = Image.getInstance(paramImage);
    localImage.setAbsolutePosition((0.0F / 0.0F), (0.0F / 0.0F));
    setAttribute("IMAGE", new Object[] { localImage, new Float(paramFloat1), new Float(paramFloat2), Boolean.FALSE });
  }

  public Chunk(DrawInterface paramDrawInterface)
  {
    this(paramDrawInterface, false);
  }

  public Chunk(DrawInterface paramDrawInterface, boolean paramBoolean)
  {
    this("￼", new Font());
    setAttribute("SEPARATOR", new Object[] { paramDrawInterface, Boolean.valueOf(paramBoolean) });
  }

  public Chunk(DrawInterface paramDrawInterface, float paramFloat)
  {
    this(paramDrawInterface, paramFloat, false);
  }

  public Chunk(DrawInterface paramDrawInterface, float paramFloat, boolean paramBoolean)
  {
    this("￼", new Font());
    if (paramFloat < 0.0F)
      throw new IllegalArgumentException("A tab position may not be lower than 0; yours is " + paramFloat);
    setAttribute("TAB", new Object[] { paramDrawInterface, new Float(paramFloat), Boolean.valueOf(paramBoolean), new Float(0.0F) });
  }

  public Chunk(Image paramImage, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    this("￼", new Font());
    setAttribute("IMAGE", new Object[] { paramImage, new Float(paramFloat1), new Float(paramFloat2), Boolean.valueOf(paramBoolean) });
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      return paramElementListener.add(this);
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return 10;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(this);
    return localArrayList;
  }

  public StringBuffer append(String paramString)
  {
    return this.content.append(paramString);
  }

  public void setFont(Font paramFont)
  {
    this.font = paramFont;
  }

  public Font getFont()
  {
    return this.font;
  }

  public String getContent()
  {
    return this.content.toString();
  }

  public String toString()
  {
    return getContent();
  }

  public boolean isEmpty()
  {
    return (this.content.toString().trim().length() == 0) && (this.content.toString().indexOf("\n") == -1) && (this.attributes == null);
  }

  public float getWidthPoint()
  {
    if (getImage() != null)
      return getImage().getScaledWidth();
    return this.font.getCalculatedBaseFont(true).getWidthPoint(getContent(), this.font.getCalculatedSize()) * getHorizontalScaling();
  }

  public boolean hasAttributes()
  {
    return this.attributes != null;
  }

  public HashMap getAttributes()
  {
    return this.attributes;
  }

  public void setAttributes(HashMap paramHashMap)
  {
    this.attributes = paramHashMap;
  }

  private Chunk setAttribute(String paramString, Object paramObject)
  {
    if (this.attributes == null)
      this.attributes = new HashMap();
    this.attributes.put(paramString, paramObject);
    return this;
  }

  public Chunk setHorizontalScaling(float paramFloat)
  {
    return setAttribute("HSCALE", new Float(paramFloat));
  }

  public float getHorizontalScaling()
  {
    if (this.attributes == null)
      return 1.0F;
    Float localFloat = (Float)this.attributes.get("HSCALE");
    if (localFloat == null)
      return 1.0F;
    return localFloat.floatValue();
  }

  public Chunk setUnderline(float paramFloat1, float paramFloat2)
  {
    return setUnderline(null, paramFloat1, 0.0F, paramFloat2, 0.0F, 0);
  }

  public Chunk setUnderline(Color paramColor, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
  {
    if (this.attributes == null)
      this.attributes = new HashMap();
    Object[] arrayOfObject = { paramColor, { paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt } };
    Object[][] arrayOfObject1 = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), arrayOfObject);
    return setAttribute("UNDERLINE", arrayOfObject1);
  }

  public Chunk setTextRise(float paramFloat)
  {
    return setAttribute("SUBSUPSCRIPT", new Float(paramFloat));
  }

  public float getTextRise()
  {
    if ((this.attributes != null) && (this.attributes.containsKey("SUBSUPSCRIPT")))
    {
      Float localFloat = (Float)this.attributes.get("SUBSUPSCRIPT");
      return localFloat.floatValue();
    }
    return 0.0F;
  }

  public Chunk setSkew(float paramFloat1, float paramFloat2)
  {
    paramFloat1 = (float)Math.tan(paramFloat1 * 3.141592653589793D / 180.0D);
    paramFloat2 = (float)Math.tan(paramFloat2 * 3.141592653589793D / 180.0D);
    return setAttribute("SKEW", new float[] { paramFloat1, paramFloat2 });
  }

  public Chunk setBackground(Color paramColor)
  {
    return setBackground(paramColor, 0.0F, 0.0F, 0.0F, 0.0F);
  }

  public Chunk setBackground(Color paramColor, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return setAttribute("BACKGROUND", new Object[] { paramColor, { paramFloat1, paramFloat2, paramFloat3, paramFloat4 } });
  }

  public Chunk setTextRenderMode(int paramInt, float paramFloat, Color paramColor)
  {
    return setAttribute("TEXTRENDERMODE", new Object[] { new Integer(paramInt), new Float(paramFloat), paramColor });
  }

  public Chunk setSplitCharacter(SplitCharacter paramSplitCharacter)
  {
    return setAttribute("SPLITCHARACTER", paramSplitCharacter);
  }

  public Chunk setHyphenation(HyphenationEvent paramHyphenationEvent)
  {
    return setAttribute("HYPHENATION", paramHyphenationEvent);
  }

  public Chunk setRemoteGoto(String paramString1, String paramString2)
  {
    return setAttribute("REMOTEGOTO", new Object[] { paramString1, paramString2 });
  }

  public Chunk setRemoteGoto(String paramString, int paramInt)
  {
    return setAttribute("REMOTEGOTO", new Object[] { paramString, new Integer(paramInt) });
  }

  public Chunk setLocalGoto(String paramString)
  {
    return setAttribute("LOCALGOTO", paramString);
  }

  public Chunk setLocalDestination(String paramString)
  {
    return setAttribute("LOCALDESTINATION", paramString);
  }

  public Chunk setGenericTag(String paramString)
  {
    return setAttribute("GENERICTAG", paramString);
  }

  public Image getImage()
  {
    if (this.attributes == null)
      return null;
    Object[] arrayOfObject = (Object[])this.attributes.get("IMAGE");
    if (arrayOfObject == null)
      return null;
    return (Image)arrayOfObject[0];
  }

  public Chunk setAction(PdfAction paramPdfAction)
  {
    return setAttribute("ACTION", paramPdfAction);
  }

  public Chunk setAnchor(URL paramURL)
  {
    return setAttribute("ACTION", new PdfAction(paramURL.toExternalForm()));
  }

  public Chunk setAnchor(String paramString)
  {
    return setAttribute("ACTION", new PdfAction(paramString));
  }

  public Chunk setNewPage()
  {
    return setAttribute("NEWPAGE", null);
  }

  public Chunk setAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    return setAttribute("PDFANNOTATION", paramPdfAnnotation);
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
  }

  public HyphenationEvent getHyphenation()
  {
    if (this.attributes == null)
      return null;
    return (HyphenationEvent)this.attributes.get("HYPHENATION");
  }

  static
  {
    NEXTPAGE.setNewPage();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Chunk
 * JD-Core Version:    0.6.0
 */