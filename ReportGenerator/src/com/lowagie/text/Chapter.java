package com.lowagie.text;

import java.util.ArrayList;

public class Chapter extends Section
{
  private static final long serialVersionUID = 1791000695779357361L;

  public Chapter(int paramInt)
  {
    super(null, 1);
    this.numbers = new ArrayList();
    this.numbers.add(new Integer(paramInt));
    this.triggerNewPage = true;
  }

  public Chapter(Paragraph paramParagraph, int paramInt)
  {
    super(paramParagraph, 1);
    this.numbers = new ArrayList();
    this.numbers.add(new Integer(paramInt));
    this.triggerNewPage = true;
  }

  public Chapter(String paramString, int paramInt)
  {
    this(new Paragraph(paramString), paramInt);
  }

  public int type()
  {
    return 16;
  }

  public boolean isNestable()
  {
    return false;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Chapter
 * JD-Core Version:    0.6.0
 */