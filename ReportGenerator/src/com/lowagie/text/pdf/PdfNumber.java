package com.lowagie.text.pdf;

public class PdfNumber extends PdfObject
{
  private double value;

  public PdfNumber(String paramString)
  {
    super(2);
    try
    {
      this.value = Double.parseDouble(paramString.trim());
      setContent(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new RuntimeException(paramString + " is not a valid number - " + localNumberFormatException.toString());
    }
  }

  public PdfNumber(int paramInt)
  {
    super(2);
    this.value = paramInt;
    setContent(String.valueOf(paramInt));
  }

  public PdfNumber(double paramDouble)
  {
    super(2);
    this.value = paramDouble;
    setContent(ByteBuffer.formatDouble(paramDouble));
  }

  public PdfNumber(float paramFloat)
  {
    this(paramFloat);
  }

  public int intValue()
  {
    return (int)this.value;
  }

  public double doubleValue()
  {
    return this.value;
  }

  public float floatValue()
  {
    return (float)this.value;
  }

  public void increment()
  {
    this.value += 1.0D;
    setContent(ByteBuffer.formatDouble(this.value));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfNumber
 * JD-Core Version:    0.6.0
 */