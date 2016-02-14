package com.lowagie.text;

public class HeaderFooter extends Rectangle
{
  private boolean numbered;
  private Phrase before = null;
  private int pageN;
  private Phrase after = null;
  private int alignment;

  public HeaderFooter(Phrase paramPhrase1, Phrase paramPhrase2)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    setBorder(3);
    setBorderWidth(1.0F);
    this.numbered = true;
    this.before = paramPhrase1;
    this.after = paramPhrase2;
  }

  public HeaderFooter(Phrase paramPhrase, boolean paramBoolean)
  {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    setBorder(3);
    setBorderWidth(1.0F);
    this.numbered = paramBoolean;
    this.before = paramPhrase;
  }

  public boolean isNumbered()
  {
    return this.numbered;
  }

  public Phrase getBefore()
  {
    return this.before;
  }

  public Phrase getAfter()
  {
    return this.after;
  }

  public void setPageNumber(int paramInt)
  {
    this.pageN = paramInt;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public Paragraph paragraph()
  {
    Paragraph localParagraph = new Paragraph(this.before.getLeading());
    localParagraph.add(this.before);
    if (this.numbered)
      localParagraph.addSpecial(new Chunk(String.valueOf(this.pageN), this.before.getFont()));
    if (this.after != null)
      localParagraph.addSpecial(this.after);
    localParagraph.setAlignment(this.alignment);
    return localParagraph;
  }

  public int alignment()
  {
    return this.alignment;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.HeaderFooter
 * JD-Core Version:    0.6.0
 */