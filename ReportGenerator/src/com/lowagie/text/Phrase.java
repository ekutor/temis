package com.lowagie.text;

import com.lowagie.text.pdf.HyphenationEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Phrase extends ArrayList
  implements TextElementArray
{
  private static final long serialVersionUID = 2643594602455068231L;
  protected float leading = (0.0F / 0.0F);
  protected Font font;
  protected HyphenationEvent hyphenation = null;

  public Phrase()
  {
    this(16.0F);
  }

  public Phrase(Phrase paramPhrase)
  {
    addAll(paramPhrase);
    this.leading = paramPhrase.getLeading();
    this.font = paramPhrase.getFont();
    setHyphenation(paramPhrase.getHyphenation());
  }

  public Phrase(float paramFloat)
  {
    this.leading = paramFloat;
    this.font = new Font();
  }

  public Phrase(Chunk paramChunk)
  {
    super.add(paramChunk);
    this.font = paramChunk.getFont();
    setHyphenation(paramChunk.getHyphenation());
  }

  public Phrase(float paramFloat, Chunk paramChunk)
  {
    this.leading = paramFloat;
    super.add(paramChunk);
    this.font = paramChunk.getFont();
    setHyphenation(paramChunk.getHyphenation());
  }

  public Phrase(String paramString)
  {
    this((0.0F / 0.0F), paramString, new Font());
  }

  public Phrase(String paramString, Font paramFont)
  {
    this((0.0F / 0.0F), paramString, paramFont);
  }

  public Phrase(float paramFloat, String paramString)
  {
    this(paramFloat, paramString, new Font());
  }

  public Phrase(float paramFloat, String paramString, Font paramFont)
  {
    this.leading = paramFloat;
    this.font = paramFont;
    if ((paramString != null) && (paramString.length() != 0))
      super.add(new Chunk(paramString, paramFont));
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
        paramElementListener.add((Element)localIterator.next());
      return true;
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public int type()
  {
    return 11;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
      localArrayList.addAll(((Element)localIterator.next()).getChunks());
    return localArrayList;
  }

  public boolean isContent()
  {
    return true;
  }

  public boolean isNestable()
  {
    return true;
  }

  public void add(int paramInt, Object paramObject)
  {
    if (paramObject == null)
      return;
    try
    {
      Element localElement = (Element)paramObject;
      if (localElement.type() == 10)
      {
        Chunk localChunk = (Chunk)localElement;
        if (!this.font.isStandardFont())
          localChunk.setFont(this.font.difference(localChunk.getFont()));
        if ((this.hyphenation != null) && (localChunk.getHyphenation() == null) && (!localChunk.isEmpty()))
          localChunk.setHyphenation(this.hyphenation);
        super.add(paramInt, localChunk);
      }
      else if ((localElement.type() == 11) || (localElement.type() == 17) || (localElement.type() == 29) || (localElement.type() == 22) || (localElement.type() == 55) || (localElement.type() == 50))
      {
        super.add(paramInt, localElement);
      }
      else
      {
        throw new ClassCastException(String.valueOf(localElement.type()));
      }
    }
    catch (ClassCastException localClassCastException)
    {
      throw new ClassCastException("Insertion of illegal Element: " + localClassCastException.getMessage());
    }
  }

  public boolean add(Object paramObject)
  {
    if (paramObject == null)
      return false;
    if ((paramObject instanceof String))
      return super.add(new Chunk((String)paramObject, this.font));
    if ((paramObject instanceof RtfElementInterface))
      return super.add(paramObject);
    try
    {
      Element localElement1 = (Element)paramObject;
      switch (localElement1.type())
      {
      case 10:
        return addChunk((Chunk)paramObject);
      case 11:
      case 12:
        Phrase localPhrase = (Phrase)paramObject;
        boolean bool = true;
        Iterator localIterator = localPhrase.iterator();
        while (localIterator.hasNext())
        {
          Element localElement2 = (Element)localIterator.next();
          if ((localElement2 instanceof Chunk))
          {
            bool &= addChunk((Chunk)localElement2);
            continue;
          }
          bool &= add(localElement2);
        }
        return bool;
      case 14:
      case 17:
      case 22:
      case 23:
      case 29:
      case 50:
      case 55:
        return super.add(paramObject);
      }
      throw new ClassCastException(String.valueOf(localElement1.type()));
    }
    catch (ClassCastException localClassCastException)
    {
    }
    throw new ClassCastException("Insertion of illegal Element: " + localClassCastException.getMessage());
  }

  public boolean addAll(Collection paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
      add(localIterator.next());
    return true;
  }

  protected boolean addChunk(Chunk paramChunk)
  {
    Font localFont = paramChunk.getFont();
    String str = paramChunk.getContent();
    if ((this.font != null) && (!this.font.isStandardFont()))
      localFont = this.font.difference(paramChunk.getFont());
    if ((size() > 0) && (!paramChunk.hasAttributes()))
      try
      {
        Chunk localChunk1 = (Chunk)get(size() - 1);
        if ((!localChunk1.hasAttributes()) && ((localFont == null) || (localFont.compareTo(localChunk1.getFont()) == 0)) && (!"".equals(localChunk1.getContent().trim())) && (!"".equals(str.trim())))
        {
          localChunk1.append(str);
          return true;
        }
      }
      catch (ClassCastException localClassCastException)
      {
      }
    Chunk localChunk2 = new Chunk(str, localFont);
    localChunk2.setAttributes(paramChunk.getAttributes());
    if ((this.hyphenation != null) && (localChunk2.getHyphenation() == null) && (!localChunk2.isEmpty()))
      localChunk2.setHyphenation(this.hyphenation);
    return super.add(localChunk2);
  }

  protected void addSpecial(Object paramObject)
  {
    super.add(paramObject);
  }

  public void setLeading(float paramFloat)
  {
    this.leading = paramFloat;
  }

  public void setFont(Font paramFont)
  {
    this.font = paramFont;
  }

  public float getLeading()
  {
    if ((Float.isNaN(this.leading)) && (this.font != null))
      return this.font.getCalculatedLeading(1.5F);
    return this.leading;
  }

  public boolean hasLeading()
  {
    return !Float.isNaN(this.leading);
  }

  public Font getFont()
  {
    return this.font;
  }

  public String getContent()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = getChunks().iterator();
    while (localIterator.hasNext())
      localStringBuffer.append(localIterator.next().toString());
    return localStringBuffer.toString();
  }

  public boolean isEmpty()
  {
    switch (size())
    {
    case 0:
      return true;
    case 1:
      Element localElement = (Element)get(0);
      return (localElement.type() == 10) && (((Chunk)localElement).isEmpty());
    }
    return false;
  }

  public HyphenationEvent getHyphenation()
  {
    return this.hyphenation;
  }

  public void setHyphenation(HyphenationEvent paramHyphenationEvent)
  {
    this.hyphenation = paramHyphenationEvent;
  }

  private Phrase(boolean paramBoolean)
  {
  }

  public static final Phrase getInstance(String paramString)
  {
    return getInstance(16, paramString, new Font());
  }

  public static final Phrase getInstance(int paramInt, String paramString)
  {
    return getInstance(paramInt, paramString, new Font());
  }

  public static final Phrase getInstance(int paramInt, String paramString, Font paramFont)
  {
    Phrase localPhrase = new Phrase(true);
    localPhrase.setLeading(paramInt);
    localPhrase.font = paramFont;
    if ((paramFont.getFamily() != 3) && (paramFont.getFamily() != 4) && (paramFont.getBaseFont() == null))
    {
      int i;
      while ((i = SpecialSymbol.index(paramString)) > -1)
      {
        if (i > 0)
        {
          localObject = paramString.substring(0, i);
          localPhrase.add(new Chunk((String)localObject, paramFont));
          paramString = paramString.substring(i);
        }
        Object localObject = new Font(3, paramFont.getSize(), paramFont.getStyle(), paramFont.getColor());
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(SpecialSymbol.getCorrespondingSymbol(paramString.charAt(0)));
        for (paramString = paramString.substring(1); SpecialSymbol.index(paramString) == 0; paramString = paramString.substring(1))
          localStringBuffer.append(SpecialSymbol.getCorrespondingSymbol(paramString.charAt(0)));
        localPhrase.add(new Chunk(localStringBuffer.toString(), (Font)localObject));
      }
    }
    if ((paramString != null) && (paramString.length() != 0))
      localPhrase.add(new Chunk(paramString, paramFont));
    return (Phrase)localPhrase;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Phrase
 * JD-Core Version:    0.6.0
 */