package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.SplitCharacter;
import com.lowagie.text.Utilities;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class PdfChunk
{
  private static final char[] singleSpace = { ' ' };
  private static final PdfChunk[] thisChunk = new PdfChunk[1];
  private static final float ITALIC_ANGLE = 0.21256F;
  private static final HashMap keysAttributes = new HashMap();
  private static final HashMap keysNoStroke = new HashMap();
  protected String value = "";
  protected String encoding = "Cp1252";
  protected PdfFont font;
  protected BaseFont baseFont;
  protected SplitCharacter splitCharacter;
  protected HashMap attributes = new HashMap();
  protected HashMap noStroke = new HashMap();
  protected boolean newlineSplit;
  protected Image image;
  protected float offsetX;
  protected float offsetY;
  protected boolean changeLeading = false;

  PdfChunk(String paramString, PdfChunk paramPdfChunk)
  {
    thisChunk[0] = this;
    this.value = paramString;
    this.font = paramPdfChunk.font;
    this.attributes = paramPdfChunk.attributes;
    this.noStroke = paramPdfChunk.noStroke;
    this.baseFont = paramPdfChunk.baseFont;
    Object[] arrayOfObject = (Object[])this.attributes.get("IMAGE");
    if (arrayOfObject == null)
    {
      this.image = null;
    }
    else
    {
      this.image = ((Image)arrayOfObject[0]);
      this.offsetX = ((Float)arrayOfObject[1]).floatValue();
      this.offsetY = ((Float)arrayOfObject[2]).floatValue();
      this.changeLeading = ((Boolean)arrayOfObject[3]).booleanValue();
    }
    this.encoding = this.font.getFont().getEncoding();
    this.splitCharacter = ((SplitCharacter)this.noStroke.get("SPLITCHARACTER"));
    if (this.splitCharacter == null)
      this.splitCharacter = DefaultSplitCharacter.DEFAULT;
  }

  PdfChunk(Chunk paramChunk, PdfAction paramPdfAction)
  {
    thisChunk[0] = this;
    this.value = paramChunk.getContent();
    Font localFont = paramChunk.getFont();
    float f = localFont.getSize();
    if (f == -1.0F)
      f = 12.0F;
    this.baseFont = localFont.getBaseFont();
    int i = localFont.getStyle();
    if (i == -1)
      i = 0;
    if (this.baseFont == null)
    {
      this.baseFont = localFont.getCalculatedBaseFont(false);
    }
    else
    {
      if ((i & 0x1) != 0)
        this.attributes.put("TEXTRENDERMODE", new Object[] { new Integer(2), new Float(f / 30.0F), null });
      if ((i & 0x2) != 0)
        this.attributes.put("SKEW", new float[] { 0.0F, 0.21256F });
    }
    this.font = new PdfFont(this.baseFont, f);
    HashMap localHashMap = paramChunk.getAttributes();
    if (localHashMap != null)
    {
      localObject1 = localHashMap.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        Object localObject3 = ((Map.Entry)localObject2).getKey();
        if (keysAttributes.containsKey(localObject3))
        {
          this.attributes.put(localObject3, ((Map.Entry)localObject2).getValue());
          continue;
        }
        if (!keysNoStroke.containsKey(localObject3))
          continue;
        this.noStroke.put(localObject3, ((Map.Entry)localObject2).getValue());
      }
      if ("".equals(localHashMap.get("GENERICTAG")))
        this.attributes.put("GENERICTAG", paramChunk.getContent());
    }
    if (localFont.isUnderlined())
    {
      localObject1 = new Object[] { null, { 0.0F, 0.0666667F, 0.0F, -0.3333333F, 0.0F } };
      localObject2 = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), localObject1);
      this.attributes.put("UNDERLINE", localObject2);
    }
    if (localFont.isStrikethru())
    {
      localObject1 = new Object[] { null, { 0.0F, 0.0666667F, 0.0F, 0.3333333F, 0.0F } };
      localObject2 = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), localObject1);
      this.attributes.put("UNDERLINE", localObject2);
    }
    if (paramPdfAction != null)
      this.attributes.put("ACTION", paramPdfAction);
    this.noStroke.put("COLOR", localFont.getColor());
    this.noStroke.put("ENCODING", this.font.getFont().getEncoding());
    Object localObject1 = (Object[])this.attributes.get("IMAGE");
    if (localObject1 == null)
    {
      this.image = null;
    }
    else
    {
      this.attributes.remove("HSCALE");
      this.image = ((Image)localObject1[0]);
      this.offsetX = ((Float)localObject1[1]).floatValue();
      this.offsetY = ((Float)localObject1[2]).floatValue();
      this.changeLeading = ((Boolean)localObject1[3]).booleanValue();
    }
    this.font.setImage(this.image);
    Object localObject2 = (Float)this.attributes.get("HSCALE");
    if (localObject2 != null)
      this.font.setHorizontalScaling(((Float)localObject2).floatValue());
    this.encoding = this.font.getFont().getEncoding();
    this.splitCharacter = ((SplitCharacter)this.noStroke.get("SPLITCHARACTER"));
    if (this.splitCharacter == null)
      this.splitCharacter = DefaultSplitCharacter.DEFAULT;
  }

  public int getUnicodeEquivalent(int paramInt)
  {
    return this.baseFont.getUnicodeEquivalent(paramInt);
  }

  protected int getWord(String paramString, int paramInt)
  {
    int i = paramString.length();
    while ((paramInt < i) && (Character.isLetter(paramString.charAt(paramInt))))
      paramInt++;
    return paramInt;
  }

  PdfChunk split(float paramFloat)
  {
    this.newlineSplit = false;
    if (this.image != null)
    {
      if (this.image.getScaledWidth() > paramFloat)
      {
        localObject1 = new PdfChunk("￼", this);
        this.value = "";
        this.attributes = new HashMap();
        this.image = null;
        this.font = PdfFont.getDefaultFont();
        return localObject1;
      }
      return null;
    }
    Object localObject1 = (HyphenationEvent)this.noStroke.get("HYPHENATION");
    int i = 0;
    int j = -1;
    float f1 = 0.0F;
    int k = -1;
    float f2 = 0.0F;
    int m = this.value.length();
    char[] arrayOfChar = this.value.toCharArray();
    int n = 0;
    BaseFont localBaseFont = this.font.getFont();
    boolean bool = false;
    if ((localBaseFont.getFontType() == 2) && (localBaseFont.getUnicodeEquivalent(32) != 32));
    Object localObject3;
    while (i < m)
    {
      int i1 = arrayOfChar[i];
      n = (char)localBaseFont.getUnicodeEquivalent(i1);
      if (n == 10)
      {
        this.newlineSplit = true;
        localObject2 = this.value.substring(i + 1);
        this.value = this.value.substring(0, i);
        if (this.value.length() < 1)
          this.value = "\001";
        localObject3 = new PdfChunk((String)localObject2, this);
        return localObject3;
      }
      f1 += this.font.width(i1);
      if (n == 32)
      {
        k = i + 1;
        f2 = f1;
      }
      if (f1 > paramFloat)
        break;
      if (this.splitCharacter.isSplitCharacter(0, i, m, arrayOfChar, thisChunk))
        j = i + 1;
      i++;
      continue;
      while (i < m)
      {
        n = arrayOfChar[i];
        if ((n == 13) || (n == 10))
        {
          this.newlineSplit = true;
          i1 = 1;
          if ((n == 13) && (i + 1 < m) && (arrayOfChar[(i + 1)] == '\n'))
            i1 = 2;
          localObject2 = this.value.substring(i + i1);
          this.value = this.value.substring(0, i);
          if (this.value.length() < 1)
            this.value = " ";
          localObject3 = new PdfChunk((String)localObject2, this);
          return localObject3;
        }
        bool = Utilities.isSurrogatePair(arrayOfChar, i);
        if (bool)
          f1 += this.font.width(Utilities.convertToUtf32(arrayOfChar[i], arrayOfChar[(i + 1)]));
        else
          f1 += this.font.width(n);
        if (n == 32)
        {
          k = i + 1;
          f2 = f1;
        }
        if (bool)
          i++;
        if (f1 > paramFloat)
          break;
        if (this.splitCharacter.isSplitCharacter(0, i, m, arrayOfChar, null))
          j = i + 1;
        i++;
      }
    }
    if (i == m)
      return null;
    if (j < 0)
    {
      String str1 = this.value;
      this.value = "";
      localObject2 = new PdfChunk(str1, this);
      return localObject2;
    }
    if ((k > j) && (this.splitCharacter.isSplitCharacter(0, 0, 1, singleSpace, null)))
      j = k;
    if ((localObject1 != null) && (k >= 0) && (k < i))
    {
      int i2 = getWord(this.value, k);
      if (i2 > k)
      {
        localObject2 = ((HyphenationEvent)localObject1).getHyphenatedWordPre(this.value.substring(k, i2), this.font.getFont(), this.font.size(), paramFloat - f2);
        localObject3 = ((HyphenationEvent)localObject1).getHyphenatedWordPost();
        if (((String)localObject2).length() > 0)
        {
          String str3 = (String)localObject3 + this.value.substring(i2);
          this.value = trim(this.value.substring(0, k) + (String)localObject2);
          PdfChunk localPdfChunk = new PdfChunk(str3, this);
          return localPdfChunk;
        }
      }
    }
    String str2 = this.value.substring(j);
    this.value = trim(this.value.substring(0, j));
    Object localObject2 = new PdfChunk(str2, this);
    return (PdfChunk)(PdfChunk)(PdfChunk)localObject2;
  }

  PdfChunk truncate(float paramFloat)
  {
    if (this.image != null)
    {
      if (this.image.getScaledWidth() > paramFloat)
      {
        PdfChunk localPdfChunk1 = new PdfChunk("", this);
        this.value = "";
        this.attributes.remove("IMAGE");
        this.image = null;
        this.font = PdfFont.getDefaultFont();
        return localPdfChunk1;
      }
      return null;
    }
    int i = 0;
    float f = 0.0F;
    if (paramFloat < this.font.width())
    {
      String str1 = this.value.substring(1);
      this.value = this.value.substring(0, 1);
      PdfChunk localPdfChunk2 = new PdfChunk(str1, this);
      return localPdfChunk2;
    }
    int j = this.value.length();
    boolean bool = false;
    while (i < j)
    {
      bool = Utilities.isSurrogatePair(this.value, i);
      if (bool)
        f += this.font.width(Utilities.convertToUtf32(this.value, i));
      else
        f += this.font.width(this.value.charAt(i));
      if (f > paramFloat)
        break;
      if (bool)
        i++;
      i++;
    }
    if (i == j)
      return null;
    if (i == 0)
    {
      i = 1;
      if (bool)
        i++;
    }
    String str2 = this.value.substring(i);
    this.value = this.value.substring(0, i);
    PdfChunk localPdfChunk3 = new PdfChunk(str2, this);
    return localPdfChunk3;
  }

  PdfFont font()
  {
    return this.font;
  }

  Color color()
  {
    return (Color)this.noStroke.get("COLOR");
  }

  float width()
  {
    return this.font.width(this.value);
  }

  public boolean isNewlineSplit()
  {
    return this.newlineSplit;
  }

  public float getWidthCorrected(float paramFloat1, float paramFloat2)
  {
    if (this.image != null)
      return this.image.getScaledWidth() + paramFloat1;
    int i = 0;
    int j = -1;
    while ((j = this.value.indexOf(' ', j + 1)) >= 0)
      i++;
    return width() + (this.value.length() * paramFloat1 + i * paramFloat2);
  }

  public float getTextRise()
  {
    Float localFloat = (Float)getAttribute("SUBSUPSCRIPT");
    if (localFloat != null)
      return localFloat.floatValue();
    return 0.0F;
  }

  public float trimLastSpace()
  {
    BaseFont localBaseFont = this.font.getFont();
    if ((localBaseFont.getFontType() == 2) && (localBaseFont.getUnicodeEquivalent(32) != 32))
    {
      if ((this.value.length() > 1) && (this.value.endsWith("\001")))
      {
        this.value = this.value.substring(0, this.value.length() - 1);
        return this.font.width(1);
      }
    }
    else if ((this.value.length() > 1) && (this.value.endsWith(" ")))
    {
      this.value = this.value.substring(0, this.value.length() - 1);
      return this.font.width(32);
    }
    return 0.0F;
  }

  public float trimFirstSpace()
  {
    BaseFont localBaseFont = this.font.getFont();
    if ((localBaseFont.getFontType() == 2) && (localBaseFont.getUnicodeEquivalent(32) != 32))
    {
      if ((this.value.length() > 1) && (this.value.startsWith("\001")))
      {
        this.value = this.value.substring(1);
        return this.font.width(1);
      }
    }
    else if ((this.value.length() > 1) && (this.value.startsWith(" ")))
    {
      this.value = this.value.substring(1);
      return this.font.width(32);
    }
    return 0.0F;
  }

  Object getAttribute(String paramString)
  {
    if (this.attributes.containsKey(paramString))
      return this.attributes.get(paramString);
    return this.noStroke.get(paramString);
  }

  boolean isAttribute(String paramString)
  {
    if (this.attributes.containsKey(paramString))
      return true;
    return this.noStroke.containsKey(paramString);
  }

  boolean isStroked()
  {
    return !this.attributes.isEmpty();
  }

  boolean isSeparator()
  {
    return isAttribute("SEPARATOR");
  }

  boolean isHorizontalSeparator()
  {
    if (isAttribute("SEPARATOR"))
    {
      Object[] arrayOfObject = (Object[])getAttribute("SEPARATOR");
      return !((Boolean)arrayOfObject[1]).booleanValue();
    }
    return false;
  }

  boolean isTab()
  {
    return isAttribute("TAB");
  }

  void adjustLeft(float paramFloat)
  {
    Object[] arrayOfObject = (Object[])this.attributes.get("TAB");
    if (arrayOfObject != null)
      this.attributes.put("TAB", new Object[] { arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], new Float(paramFloat) });
  }

  boolean isImage()
  {
    return this.image != null;
  }

  Image getImage()
  {
    return this.image;
  }

  void setImageOffsetX(float paramFloat)
  {
    this.offsetX = paramFloat;
  }

  float getImageOffsetX()
  {
    return this.offsetX;
  }

  void setImageOffsetY(float paramFloat)
  {
    this.offsetY = paramFloat;
  }

  float getImageOffsetY()
  {
    return this.offsetY;
  }

  void setValue(String paramString)
  {
    this.value = paramString;
  }

  public String toString()
  {
    return this.value;
  }

  boolean isSpecialEncoding()
  {
    return (this.encoding.equals("UnicodeBigUnmarked")) || (this.encoding.equals("Identity-H"));
  }

  String getEncoding()
  {
    return this.encoding;
  }

  int length()
  {
    return this.value.length();
  }

  int lengthUtf32()
  {
    if (!"Identity-H".equals(this.encoding))
      return this.value.length();
    int i = 0;
    int j = this.value.length();
    for (int k = 0; k < j; k++)
    {
      if (Utilities.isSurrogateHigh(this.value.charAt(k)))
        k++;
      i++;
    }
    return i;
  }

  boolean isExtSplitCharacter(int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar, PdfChunk[] paramArrayOfPdfChunk)
  {
    return this.splitCharacter.isSplitCharacter(paramInt1, paramInt2, paramInt3, paramArrayOfChar, paramArrayOfPdfChunk);
  }

  String trim(String paramString)
  {
    BaseFont localBaseFont = this.font.getFont();
    if ((localBaseFont.getFontType() == 2) && (localBaseFont.getUnicodeEquivalent(32) != 32));
    while (paramString.endsWith("\001"))
    {
      paramString = paramString.substring(0, paramString.length() - 1);
      continue;
      while ((paramString.endsWith(" ")) || (paramString.endsWith("\t")))
        paramString = paramString.substring(0, paramString.length() - 1);
    }
    return paramString;
  }

  public boolean changeLeading()
  {
    return this.changeLeading;
  }

  float getCharWidth(int paramInt)
  {
    if (noPrint(paramInt))
      return 0.0F;
    return this.font.width(paramInt);
  }

  public static boolean noPrint(int paramInt)
  {
    return ((paramInt >= 8203) && (paramInt <= 8207)) || ((paramInt >= 8234) && (paramInt <= 8238));
  }

  static
  {
    keysAttributes.put("ACTION", null);
    keysAttributes.put("UNDERLINE", null);
    keysAttributes.put("REMOTEGOTO", null);
    keysAttributes.put("LOCALGOTO", null);
    keysAttributes.put("LOCALDESTINATION", null);
    keysAttributes.put("GENERICTAG", null);
    keysAttributes.put("NEWPAGE", null);
    keysAttributes.put("IMAGE", null);
    keysAttributes.put("BACKGROUND", null);
    keysAttributes.put("PDFANNOTATION", null);
    keysAttributes.put("SKEW", null);
    keysAttributes.put("HSCALE", null);
    keysAttributes.put("SEPARATOR", null);
    keysAttributes.put("TAB", null);
    keysNoStroke.put("SUBSUPSCRIPT", null);
    keysNoStroke.put("SPLITCHARACTER", null);
    keysNoStroke.put("HYPHENATION", null);
    keysNoStroke.put("TEXTRENDERMODE", null);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfChunk
 * JD-Core Version:    0.6.0
 */