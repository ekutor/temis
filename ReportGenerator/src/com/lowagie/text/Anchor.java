package com.lowagie.text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Anchor extends Phrase
{
  private static final long serialVersionUID = -852278536049236911L;
  protected String name = null;
  protected String reference = null;

  public Anchor()
  {
    super(16.0F);
  }

  public Anchor(float paramFloat)
  {
    super(paramFloat);
  }

  public Anchor(Chunk paramChunk)
  {
    super(paramChunk);
  }

  public Anchor(String paramString)
  {
    super(paramString);
  }

  public Anchor(String paramString, Font paramFont)
  {
    super(paramString, paramFont);
  }

  public Anchor(float paramFloat, Chunk paramChunk)
  {
    super(paramFloat, paramChunk);
  }

  public Anchor(float paramFloat, String paramString)
  {
    super(paramFloat, paramString);
  }

  public Anchor(float paramFloat, String paramString, Font paramFont)
  {
    super(paramFloat, paramString, paramFont);
  }

  public Anchor(Phrase paramPhrase)
  {
    super(paramPhrase);
    if ((paramPhrase instanceof Anchor))
    {
      Anchor localAnchor = (Anchor)paramPhrase;
      setName(localAnchor.name);
      setReference(localAnchor.reference);
    }
  }

  public boolean process(ElementListener paramElementListener)
  {
    try
    {
      Iterator localIterator = getChunks().iterator();
      int i = (this.reference != null) && (this.reference.startsWith("#")) ? 1 : 0;
      int j = 1;
      while (localIterator.hasNext())
      {
        Chunk localChunk = (Chunk)localIterator.next();
        if ((this.name != null) && (j != 0) && (!localChunk.isEmpty()))
        {
          localChunk.setLocalDestination(this.name);
          j = 0;
        }
        if (i != 0)
          localChunk.setLocalGoto(this.reference.substring(1));
        paramElementListener.add(localChunk);
      }
      return true;
    }
    catch (DocumentException localDocumentException)
    {
    }
    return false;
  }

  public ArrayList getChunks()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = iterator();
    int i = (this.reference != null) && (this.reference.startsWith("#")) ? 1 : 0;
    int j = 1;
    while (localIterator.hasNext())
    {
      Chunk localChunk = (Chunk)localIterator.next();
      if ((this.name != null) && (j != 0) && (!localChunk.isEmpty()))
      {
        localChunk.setLocalDestination(this.name);
        j = 0;
      }
      if (i != 0)
        localChunk.setLocalGoto(this.reference.substring(1));
      else if (this.reference != null)
        localChunk.setAnchor(this.reference);
      localArrayList.add(localChunk);
    }
    return localArrayList;
  }

  public int type()
  {
    return 17;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setReference(String paramString)
  {
    this.reference = paramString;
  }

  public String getName()
  {
    return this.name;
  }

  public String getReference()
  {
    return this.reference;
  }

  public URL getUrl()
  {
    try
    {
      return new URL(this.reference);
    }
    catch (MalformedURLException localMalformedURLException)
    {
    }
    return null;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Anchor
 * JD-Core Version:    0.6.0
 */