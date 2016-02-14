package com.lowagie.text;

public class ChapterAutoNumber extends Chapter
{
  private static final long serialVersionUID = -9217457637987854167L;
  protected boolean numberSet = false;

  public ChapterAutoNumber(Paragraph paramParagraph)
  {
    super(paramParagraph, 0);
  }

  public ChapterAutoNumber(String paramString)
  {
    super(paramString, 0);
  }

  public Section addSection(String paramString)
  {
    if (isAddedCompletely())
      throw new IllegalStateException("This LargeElement has already been added to the Document.");
    return addSection(paramString, 2);
  }

  public Section addSection(Paragraph paramParagraph)
  {
    if (isAddedCompletely())
      throw new IllegalStateException("This LargeElement has already been added to the Document.");
    return addSection(paramParagraph, 2);
  }

  public int setAutomaticNumber(int paramInt)
  {
    if (!this.numberSet)
    {
      paramInt++;
      super.setChapterNumber(paramInt);
      this.numberSet = true;
    }
    return paramInt;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ChapterAutoNumber
 * JD-Core Version:    0.6.0
 */