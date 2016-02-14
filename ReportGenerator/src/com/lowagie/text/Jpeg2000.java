package com.lowagie.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Jpeg2000 extends Image
{
  public static final int JP2_JP = 1783636000;
  public static final int JP2_IHDR = 1768449138;
  public static final int JPIP_JPIP = 1785751920;
  public static final int JP2_FTYP = 1718909296;
  public static final int JP2_JP2H = 1785737832;
  public static final int JP2_COLR = 1668246642;
  public static final int JP2_JP2C = 1785737827;
  public static final int JP2_URL = 1970433056;
  public static final int JP2_DBTL = 1685348972;
  public static final int JP2_BPCC = 1651532643;
  public static final int JP2_JP2 = 1785737760;
  InputStream inp;
  int boxLength;
  int boxType;

  Jpeg2000(Image paramImage)
  {
    super(paramImage);
  }

  public Jpeg2000(URL paramURL)
    throws BadElementException, IOException
  {
    super(paramURL);
    processParameters();
  }

  public Jpeg2000(byte[] paramArrayOfByte)
    throws BadElementException, IOException
  {
    super((URL)null);
    this.rawData = paramArrayOfByte;
    this.originalData = paramArrayOfByte;
    processParameters();
  }

  public Jpeg2000(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2)
    throws BadElementException, IOException
  {
    this(paramArrayOfByte);
    this.scaledWidth = paramFloat1;
    this.scaledHeight = paramFloat2;
  }

  private int cio_read(int paramInt)
    throws IOException
  {
    int i = 0;
    for (int j = paramInt - 1; j >= 0; j--)
      i += (this.inp.read() << (j << 3));
    return i;
  }

  public void jp2_read_boxhdr()
    throws IOException
  {
    this.boxLength = cio_read(4);
    this.boxType = cio_read(4);
    if (this.boxLength == 1)
    {
      if (cio_read(4) != 0)
        throw new IOException("Cannot handle box sizes higher than 2^32");
      this.boxLength = cio_read(4);
      if (this.boxLength == 0)
        throw new IOException("Unsupported box size == 0");
    }
    else if (this.boxLength == 0)
    {
      throw new IOException("Unsupported box size == 0");
    }
  }

  private void processParameters()
    throws IOException
  {
    this.type = 33;
    this.originalType = 8;
    this.inp = null;
    try
    {
      String str;
      if (this.rawData == null)
      {
        this.inp = this.url.openStream();
        str = this.url.toString();
      }
      else
      {
        this.inp = new ByteArrayInputStream(this.rawData);
        str = "Byte array";
      }
      this.boxLength = cio_read(4);
      if (this.boxLength == 12)
      {
        this.boxType = cio_read(4);
        if (1783636000 != this.boxType)
          throw new IOException("Expected JP Marker");
        if (218793738 != cio_read(4))
          throw new IOException("Error with JP Marker");
        jp2_read_boxhdr();
        if (1718909296 != this.boxType)
          throw new IOException("Expected FTYP Marker");
        Utilities.skip(this.inp, this.boxLength - 8);
        jp2_read_boxhdr();
        do
        {
          if (1785737832 == this.boxType)
            continue;
          if (this.boxType == 1785737827)
            throw new IOException("Expected JP2H Marker");
          Utilities.skip(this.inp, this.boxLength - 8);
          jp2_read_boxhdr();
        }
        while (1785737832 != this.boxType);
        jp2_read_boxhdr();
        if (1768449138 != this.boxType)
          throw new IOException("Expected IHDR Marker");
        this.scaledHeight = cio_read(4);
        setTop(this.scaledHeight);
        this.scaledWidth = cio_read(4);
        setRight(this.scaledWidth);
        this.bpc = -1;
      }
      else if (this.boxLength == -11534511)
      {
        Utilities.skip(this.inp, 4);
        int i = cio_read(4);
        int j = cio_read(4);
        int k = cio_read(4);
        int m = cio_read(4);
        Utilities.skip(this.inp, 16);
        this.colorspace = cio_read(2);
        this.bpc = 8;
        this.scaledHeight = (j - m);
        setTop(this.scaledHeight);
        this.scaledWidth = (i - k);
        setRight(this.scaledWidth);
      }
      else
      {
        throw new IOException("Not a valid Jpeg2000 file");
      }
    }
    finally
    {
      if (this.inp != null)
      {
        try
        {
          this.inp.close();
        }
        catch (Exception localException)
        {
        }
        this.inp = null;
      }
    }
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Jpeg2000
 * JD-Core Version:    0.6.0
 */