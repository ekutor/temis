package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class PdfLiteral extends PdfObject
{
  private int position;

  public PdfLiteral(String paramString)
  {
    super(0, paramString);
  }

  public PdfLiteral(byte[] paramArrayOfByte)
  {
    super(0, paramArrayOfByte);
  }

  public PdfLiteral(int paramInt)
  {
    super(0, (byte[])null);
    this.bytes = new byte[paramInt];
    Arrays.fill(this.bytes, 32);
  }

  public PdfLiteral(int paramInt, String paramString)
  {
    super(paramInt, paramString);
  }

  public PdfLiteral(int paramInt, byte[] paramArrayOfByte)
  {
    super(paramInt, paramArrayOfByte);
  }

  public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramOutputStream instanceof OutputStreamCounter))
      this.position = ((OutputStreamCounter)paramOutputStream).getCounter();
    super.toPdf(paramPdfWriter, paramOutputStream);
  }

  public int getPosition()
  {
    return this.position;
  }

  public int getPosLength()
  {
    if (this.bytes != null)
      return this.bytes.length;
    return 0;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfLiteral
 * JD-Core Version:    0.6.0
 */