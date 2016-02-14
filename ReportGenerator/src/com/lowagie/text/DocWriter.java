package com.lowagie.text;

import com.lowagie.text.pdf.OutputStreamCounter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public abstract class DocWriter
  implements DocListener
{
  public static final byte NEWLINE = 10;
  public static final byte TAB = 9;
  public static final byte LT = 60;
  public static final byte SPACE = 32;
  public static final byte EQUALS = 61;
  public static final byte QUOTE = 34;
  public static final byte GT = 62;
  public static final byte FORWARD = 47;
  protected Rectangle pageSize;
  protected Document document;
  protected OutputStreamCounter os;
  protected boolean open = false;
  protected boolean pause = false;
  protected boolean closeStream = true;

  protected DocWriter()
  {
  }

  protected DocWriter(Document paramDocument, OutputStream paramOutputStream)
  {
    this.document = paramDocument;
    this.os = new OutputStreamCounter(new BufferedOutputStream(paramOutputStream));
  }

  public boolean add(Element paramElement)
    throws DocumentException
  {
    return false;
  }

  public void open()
  {
    this.open = true;
  }

  public boolean setPageSize(Rectangle paramRectangle)
  {
    this.pageSize = paramRectangle;
    return true;
  }

  public boolean setMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return false;
  }

  public boolean newPage()
  {
    return this.open;
  }

  public void setHeader(HeaderFooter paramHeaderFooter)
  {
  }

  public void resetHeader()
  {
  }

  public void setFooter(HeaderFooter paramHeaderFooter)
  {
  }

  public void resetFooter()
  {
  }

  public void resetPageCount()
  {
  }

  public void setPageCount(int paramInt)
  {
  }

  public void close()
  {
    this.open = false;
    try
    {
      this.os.flush();
      if (this.closeStream)
        this.os.close();
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public static final byte[] getISOBytes(String paramString)
  {
    if (paramString == null)
      return null;
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i];
    for (int j = 0; j < i; j++)
      arrayOfByte[j] = (byte)paramString.charAt(j);
    return arrayOfByte;
  }

  public void pause()
  {
    this.pause = true;
  }

  public boolean isPaused()
  {
    return this.pause;
  }

  public void resume()
  {
    this.pause = false;
  }

  public void flush()
  {
    try
    {
      this.os.flush();
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  protected void write(String paramString)
    throws IOException
  {
    this.os.write(getISOBytes(paramString));
  }

  protected void addTabs(int paramInt)
    throws IOException
  {
    this.os.write(10);
    for (int i = 0; i < paramInt; i++)
      this.os.write(9);
  }

  protected void write(String paramString1, String paramString2)
    throws IOException
  {
    this.os.write(32);
    write(paramString1);
    this.os.write(61);
    this.os.write(34);
    write(paramString2);
    this.os.write(34);
  }

  protected void writeStart(String paramString)
    throws IOException
  {
    this.os.write(60);
    write(paramString);
  }

  protected void writeEnd(String paramString)
    throws IOException
  {
    this.os.write(60);
    this.os.write(47);
    write(paramString);
    this.os.write(62);
  }

  protected void writeEnd()
    throws IOException
  {
    this.os.write(32);
    this.os.write(47);
    this.os.write(62);
  }

  protected boolean writeMarkupAttributes(Properties paramProperties)
    throws IOException
  {
    if (paramProperties == null)
      return false;
    Iterator localIterator = paramProperties.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = String.valueOf(localIterator.next());
      write(str, paramProperties.getProperty(str));
    }
    paramProperties.clear();
    return true;
  }

  public boolean isCloseStream()
  {
    return this.closeStream;
  }

  public void setCloseStream(boolean paramBoolean)
  {
    this.closeStream = paramBoolean;
  }

  public boolean setMarginMirroring(boolean paramBoolean)
  {
    return false;
  }

  public boolean setMarginMirroringTopBottom(boolean paramBoolean)
  {
    return false;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.DocWriter
 * JD-Core Version:    0.6.0
 */