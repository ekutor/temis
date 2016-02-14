package com.lowagie.text;

public class Paragraph extends Phrase
{
  private static final long serialVersionUID = 7852314969733375514L;
  protected int alignment = -1;
  protected float multipliedLeading = 0.0F;
  protected float indentationLeft;
  protected float indentationRight;
  private float firstLineIndent = 0.0F;
  protected float spacingBefore;
  protected float spacingAfter;
  private float extraParagraphSpace = 0.0F;
  protected boolean keeptogether = false;

  public Paragraph()
  {
  }

  public Paragraph(float paramFloat)
  {
    super(paramFloat);
  }

  public Paragraph(Chunk paramChunk)
  {
    super(paramChunk);
  }

  public Paragraph(float paramFloat, Chunk paramChunk)
  {
    super(paramFloat, paramChunk);
  }

  public Paragraph(String paramString)
  {
    super(paramString);
  }

  public Paragraph(String paramString, Font paramFont)
  {
    super(paramString, paramFont);
  }

  public Paragraph(float paramFloat, String paramString)
  {
    super(paramFloat, paramString);
  }

  public Paragraph(float paramFloat, String paramString, Font paramFont)
  {
    super(paramFloat, paramString, paramFont);
  }

  public Paragraph(Phrase paramPhrase)
  {
    super(paramPhrase);
    if ((paramPhrase instanceof Paragraph))
    {
      Paragraph localParagraph = (Paragraph)paramPhrase;
      setAlignment(localParagraph.alignment);
      setLeading(paramPhrase.getLeading(), localParagraph.multipliedLeading);
      setIndentationLeft(localParagraph.getIndentationLeft());
      setIndentationRight(localParagraph.getIndentationRight());
      setFirstLineIndent(localParagraph.getFirstLineIndent());
      setSpacingAfter(localParagraph.spacingAfter());
      setSpacingBefore(localParagraph.spacingBefore());
      setExtraParagraphSpace(localParagraph.getExtraParagraphSpace());
    }
  }

  public int type()
  {
    return 12;
  }

  public boolean add(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof List))
    {
      localObject = (List)paramObject;
      ((List)localObject).setIndentationLeft(((List)localObject).getIndentationLeft() + this.indentationLeft);
      ((List)localObject).setIndentationRight(this.indentationRight);
      return super.add(localObject);
    }
    if ((paramObject instanceof Image))
    {
      super.addSpecial(paramObject);
      return true;
    }
    if ((paramObject instanceof Paragraph))
    {
      super.add(paramObject);
      localObject = getChunks();
      if (!((java.util.List)localObject).isEmpty())
      {
        Chunk localChunk = (Chunk)((java.util.List)localObject).get(((java.util.List)localObject).size() - 1);
        super.add(new Chunk("\n", localChunk.getFont()));
      }
      else
      {
        super.add(Chunk.NEWLINE);
      }
      return true;
    }
    return super.add(paramObject);
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public void setAlignment(String paramString)
  {
    if ("Center".equalsIgnoreCase(paramString))
    {
      this.alignment = 1;
      return;
    }
    if ("Right".equalsIgnoreCase(paramString))
    {
      this.alignment = 2;
      return;
    }
    if ("Justify".equalsIgnoreCase(paramString))
    {
      this.alignment = 3;
      return;
    }
    if ("JustifyAll".equalsIgnoreCase(paramString))
    {
      this.alignment = 8;
      return;
    }
    this.alignment = 0;
  }

  public void setLeading(float paramFloat)
  {
    this.leading = paramFloat;
    this.multipliedLeading = 0.0F;
  }

  public void setMultipliedLeading(float paramFloat)
  {
    this.leading = 0.0F;
    this.multipliedLeading = paramFloat;
  }

  public void setLeading(float paramFloat1, float paramFloat2)
  {
    this.leading = paramFloat1;
    this.multipliedLeading = paramFloat2;
  }

  public void setIndentationLeft(float paramFloat)
  {
    this.indentationLeft = paramFloat;
  }

  public void setIndentationRight(float paramFloat)
  {
    this.indentationRight = paramFloat;
  }

  public void setFirstLineIndent(float paramFloat)
  {
    this.firstLineIndent = paramFloat;
  }

  public void setSpacingBefore(float paramFloat)
  {
    this.spacingBefore = paramFloat;
  }

  public void setSpacingAfter(float paramFloat)
  {
    this.spacingAfter = paramFloat;
  }

  public void setKeepTogether(boolean paramBoolean)
  {
    this.keeptogether = paramBoolean;
  }

  public boolean getKeepTogether()
  {
    return this.keeptogether;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public float getMultipliedLeading()
  {
    return this.multipliedLeading;
  }

  public float getTotalLeading()
  {
    float f = this.font == null ? 12.0F * this.multipliedLeading : this.font.getCalculatedLeading(this.multipliedLeading);
    if ((f > 0.0F) && (!hasLeading()))
      return f;
    return getLeading() + f;
  }

  public float getIndentationLeft()
  {
    return this.indentationLeft;
  }

  public float getIndentationRight()
  {
    return this.indentationRight;
  }

  public float getFirstLineIndent()
  {
    return this.firstLineIndent;
  }

  public float getSpacingBefore()
  {
    return this.spacingBefore;
  }

  public float getSpacingAfter()
  {
    return this.spacingAfter;
  }

  public float getExtraParagraphSpace()
  {
    return this.extraParagraphSpace;
  }

  public void setExtraParagraphSpace(float paramFloat)
  {
    this.extraParagraphSpace = paramFloat;
  }

  /** @deprecated */
  public float spacingBefore()
  {
    return getSpacingBefore();
  }

  /** @deprecated */
  public float spacingAfter()
  {
    return this.spacingAfter;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Paragraph
 * JD-Core Version:    0.6.0
 */