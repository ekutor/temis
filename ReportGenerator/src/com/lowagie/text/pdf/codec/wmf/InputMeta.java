package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.Utilities;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

public class InputMeta
{
  InputStream in;
  int length;

  public InputMeta(InputStream paramInputStream)
  {
    this.in = paramInputStream;
  }

  public int readWord()
    throws IOException
  {
    this.length += 2;
    int i = this.in.read();
    if (i < 0)
      return 0;
    return i + (this.in.read() << 8) & 0xFFFF;
  }

  public int readShort()
    throws IOException
  {
    int i = readWord();
    if (i > 32767)
      i -= 65536;
    return i;
  }

  public int readInt()
    throws IOException
  {
    this.length += 4;
    int i = this.in.read();
    if (i < 0)
      return 0;
    int j = this.in.read() << 8;
    int k = this.in.read() << 16;
    return i + j + k + (this.in.read() << 24);
  }

  public int readByte()
    throws IOException
  {
    this.length += 1;
    return this.in.read() & 0xFF;
  }

  public void skip(int paramInt)
    throws IOException
  {
    this.length += paramInt;
    Utilities.skip(this.in, paramInt);
  }

  public int getLength()
  {
    return this.length;
  }

  public Color readColor()
    throws IOException
  {
    int i = readByte();
    int j = readByte();
    int k = readByte();
    readByte();
    return new Color(i, j, k);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.InputMeta
 * JD-Core Version:    0.6.0
 */