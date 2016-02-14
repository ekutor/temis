package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCounter extends OutputStream
{
  protected OutputStream out;
  protected int counter = 0;

  public OutputStreamCounter(OutputStream paramOutputStream)
  {
    this.out = paramOutputStream;
  }

  public void close()
    throws IOException
  {
    this.out.close();
  }

  public void flush()
    throws IOException
  {
    this.out.flush();
  }

  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    this.counter += paramArrayOfByte.length;
    this.out.write(paramArrayOfByte);
  }

  public void write(int paramInt)
    throws IOException
  {
    this.counter += 1;
    this.out.write(paramInt);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.counter += paramInt2;
    this.out.write(paramArrayOfByte, paramInt1, paramInt2);
  }

  public int getCounter()
  {
    return this.counter;
  }

  public void resetCounter()
  {
    this.counter = 0;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.OutputStreamCounter
 * JD-Core Version:    0.6.0
 */