package com.lowagie.text.pdf;

public class PdfTransition
{
  public static final int SPLITVOUT = 1;
  public static final int SPLITHOUT = 2;
  public static final int SPLITVIN = 3;
  public static final int SPLITHIN = 4;
  public static final int BLINDV = 5;
  public static final int BLINDH = 6;
  public static final int INBOX = 7;
  public static final int OUTBOX = 8;
  public static final int LRWIPE = 9;
  public static final int RLWIPE = 10;
  public static final int BTWIPE = 11;
  public static final int TBWIPE = 12;
  public static final int DISSOLVE = 13;
  public static final int LRGLITTER = 14;
  public static final int TBGLITTER = 15;
  public static final int DGLITTER = 16;
  protected int duration;
  protected int type;

  public PdfTransition()
  {
    this(6);
  }

  public PdfTransition(int paramInt)
  {
    this(paramInt, 1);
  }

  public PdfTransition(int paramInt1, int paramInt2)
  {
    this.duration = paramInt2;
    this.type = paramInt1;
  }

  public int getDuration()
  {
    return this.duration;
  }

  public int getType()
  {
    return this.type;
  }

  public PdfDictionary getTransitionDictionary()
  {
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.TRANS);
    switch (this.type)
    {
    case 1:
      localPdfDictionary.put(PdfName.S, PdfName.SPLIT);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DM, PdfName.V);
      localPdfDictionary.put(PdfName.M, PdfName.O);
      break;
    case 2:
      localPdfDictionary.put(PdfName.S, PdfName.SPLIT);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DM, PdfName.H);
      localPdfDictionary.put(PdfName.M, PdfName.O);
      break;
    case 3:
      localPdfDictionary.put(PdfName.S, PdfName.SPLIT);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DM, PdfName.V);
      localPdfDictionary.put(PdfName.M, PdfName.I);
      break;
    case 4:
      localPdfDictionary.put(PdfName.S, PdfName.SPLIT);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DM, PdfName.H);
      localPdfDictionary.put(PdfName.M, PdfName.I);
      break;
    case 5:
      localPdfDictionary.put(PdfName.S, PdfName.BLINDS);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DM, PdfName.V);
      break;
    case 6:
      localPdfDictionary.put(PdfName.S, PdfName.BLINDS);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DM, PdfName.H);
      break;
    case 7:
      localPdfDictionary.put(PdfName.S, PdfName.BOX);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.M, PdfName.I);
      break;
    case 8:
      localPdfDictionary.put(PdfName.S, PdfName.BOX);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.M, PdfName.O);
      break;
    case 9:
      localPdfDictionary.put(PdfName.S, PdfName.WIPE);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(0));
      break;
    case 10:
      localPdfDictionary.put(PdfName.S, PdfName.WIPE);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(180));
      break;
    case 11:
      localPdfDictionary.put(PdfName.S, PdfName.WIPE);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(90));
      break;
    case 12:
      localPdfDictionary.put(PdfName.S, PdfName.WIPE);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(270));
      break;
    case 13:
      localPdfDictionary.put(PdfName.S, PdfName.DISSOLVE);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      break;
    case 14:
      localPdfDictionary.put(PdfName.S, PdfName.GLITTER);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(0));
      break;
    case 15:
      localPdfDictionary.put(PdfName.S, PdfName.GLITTER);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(270));
      break;
    case 16:
      localPdfDictionary.put(PdfName.S, PdfName.GLITTER);
      localPdfDictionary.put(PdfName.D, new PdfNumber(this.duration));
      localPdfDictionary.put(PdfName.DI, new PdfNumber(315));
    }
    return localPdfDictionary;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfTransition
 * JD-Core Version:    0.6.0
 */