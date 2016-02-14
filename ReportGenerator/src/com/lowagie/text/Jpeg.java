package com.lowagie.text;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Jpeg extends Image
{
  public static final int NOT_A_MARKER = -1;
  public static final int VALID_MARKER = 0;
  public static final int[] VALID_MARKERS = { 192, 193, 194 };
  public static final int UNSUPPORTED_MARKER = 1;
  public static final int[] UNSUPPORTED_MARKERS = { 195, 197, 198, 199, 200, 201, 202, 203, 205, 206, 207 };
  public static final int NOPARAM_MARKER = 2;
  public static final int[] NOPARAM_MARKERS = { 208, 209, 210, 211, 212, 213, 214, 215, 216, 1 };
  public static final int M_APP0 = 224;
  public static final int M_APP2 = 226;
  public static final int M_APPE = 238;
  public static final byte[] JFIF_ID = { 74, 70, 73, 70, 0 };
  private byte[][] icc;

  Jpeg(Image paramImage)
  {
    super(paramImage);
  }

  public Jpeg(URL paramURL)
    throws BadElementException, IOException
  {
    super(paramURL);
    processParameters();
  }

  public Jpeg(byte[] paramArrayOfByte)
    throws BadElementException, IOException
  {
    super((URL)null);
    this.rawData = paramArrayOfByte;
    this.originalData = paramArrayOfByte;
    processParameters();
  }

  public Jpeg(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2)
    throws BadElementException, IOException
  {
    this(paramArrayOfByte);
    this.scaledWidth = paramFloat1;
    this.scaledHeight = paramFloat2;
  }

  private static final int getShort(InputStream paramInputStream)
    throws IOException
  {
    return (paramInputStream.read() << 8) + paramInputStream.read();
  }

  private static final int marker(int paramInt)
  {
    for (int i = 0; i < VALID_MARKERS.length; i++)
      if (paramInt == VALID_MARKERS[i])
        return 0;
    for (i = 0; i < NOPARAM_MARKERS.length; i++)
      if (paramInt == NOPARAM_MARKERS[i])
        return 2;
    for (i = 0; i < UNSUPPORTED_MARKERS.length; i++)
      if (paramInt == UNSUPPORTED_MARKERS[i])
        return 1;
    return -1;
  }

  private void processParameters()
    throws BadElementException, IOException
  {
    this.type = 32;
    this.originalType = 1;
    Object localObject1 = null;
    int j;
    int k;
    try
    {
      String str1;
      if (this.rawData == null)
      {
        localObject1 = this.url.openStream();
        str1 = this.url.toString();
      }
      else
      {
        localObject1 = new ByteArrayInputStream(this.rawData);
        str1 = "Byte array";
      }
      if ((((InputStream)localObject1).read() != 255) || (((InputStream)localObject1).read() != 216))
        throw new BadElementException(str1 + " is not a valid JPEG-file.");
      j = 1;
      while (true)
      {
        int m = ((InputStream)localObject1).read();
        if (m < 0)
          throw new IOException("Premature EOF while reading JPG.");
        if (m != 255)
          continue;
        int n = ((InputStream)localObject1).read();
        byte[] arrayOfByte2;
        int i2;
        int i4;
        int i5;
        if ((j != 0) && (n == 224))
        {
          j = 0;
          k = getShort((InputStream)localObject1);
          if (k < 16)
          {
            Utilities.skip((InputStream)localObject1, k - 2);
            continue;
          }
          arrayOfByte2 = new byte[JFIF_ID.length];
          i2 = ((InputStream)localObject1).read(arrayOfByte2);
          if (i2 != arrayOfByte2.length)
            throw new BadElementException(str1 + " corrupted JFIF marker.");
          i4 = 1;
          for (i5 = 0; i5 < arrayOfByte2.length; i5++)
          {
            if (arrayOfByte2[i5] == JFIF_ID[i5])
              continue;
            i4 = 0;
            break;
          }
          if (i4 == 0)
          {
            Utilities.skip((InputStream)localObject1, k - 2 - arrayOfByte2.length);
            continue;
          }
          Utilities.skip((InputStream)localObject1, 2);
          i5 = ((InputStream)localObject1).read();
          int i6 = getShort((InputStream)localObject1);
          int i7 = getShort((InputStream)localObject1);
          if (i5 == 1)
          {
            this.dpiX = i6;
            this.dpiY = i7;
          }
          else if (i5 == 2)
          {
            this.dpiX = (int)(i6 * 2.54F + 0.5F);
            this.dpiY = (int)(i7 * 2.54F + 0.5F);
          }
          Utilities.skip((InputStream)localObject1, k - 2 - arrayOfByte2.length - 7);
          continue;
        }
        if (n == 238)
        {
          k = getShort((InputStream)localObject1) - 2;
          arrayOfByte2 = new byte[k];
          for (i2 = 0; i2 < k; i2++)
            arrayOfByte2[i2] = (byte)((InputStream)localObject1).read();
          if (arrayOfByte2.length < 12)
            continue;
          String str2 = new String(arrayOfByte2, 0, 5, "ISO-8859-1");
          if (!str2.equals("Adobe"))
            continue;
          this.invert = true;
          continue;
        }
        if (n == 226)
        {
          k = getShort((InputStream)localObject1) - 2;
          arrayOfByte2 = new byte[k];
          for (int i3 = 0; i3 < k; i3++)
            arrayOfByte2[i3] = (byte)((InputStream)localObject1).read();
          if (arrayOfByte2.length < 14)
            continue;
          String str3 = new String(arrayOfByte2, 0, 11, "ISO-8859-1");
          if (!str3.equals("ICC_PROFILE"))
            continue;
          i4 = arrayOfByte2[12] & 0xFF;
          i5 = arrayOfByte2[13] & 0xFF;
          if (this.icc == null)
            this.icc = new byte[i5][];
          this.icc[(i4 - 1)] = arrayOfByte2;
          continue;
        }
        j = 0;
        int i1 = marker(n);
        if (i1 == 0)
        {
          Utilities.skip((InputStream)localObject1, 2);
          if (((InputStream)localObject1).read() != 8)
            throw new BadElementException(str1 + " must have 8 bits per component.");
          this.scaledHeight = getShort((InputStream)localObject1);
          setTop(this.scaledHeight);
          this.scaledWidth = getShort((InputStream)localObject1);
          setRight(this.scaledWidth);
          this.colorspace = ((InputStream)localObject1).read();
          this.bpc = 8;
          break;
        }
        if (i1 == 1)
          throw new BadElementException(str1 + ": unsupported JPEG marker: " + n);
        if (i1 == 2)
          continue;
        Utilities.skip((InputStream)localObject1, getShort((InputStream)localObject1) - 2);
      }
    }
    finally
    {
      if (localObject1 != null)
        ((InputStream)localObject1).close();
    }
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
    if (this.icc != null)
    {
      int i = 0;
      for (j = 0; j < this.icc.length; j++)
      {
        if (this.icc[j] == null)
        {
          this.icc = ((byte[][])null);
          return;
        }
        i += this.icc[j].length - 14;
      }
      byte[] arrayOfByte1 = new byte[i];
      i = 0;
      for (k = 0; k < this.icc.length; k++)
      {
        System.arraycopy(this.icc[k], 14, arrayOfByte1, i, this.icc[k].length - 14);
        i += this.icc[k].length - 14;
      }
      try
      {
        ICC_Profile localICC_Profile = ICC_Profile.getInstance(arrayOfByte1);
        tagICC(localICC_Profile);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
      }
      this.icc = ((byte[][])null);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Jpeg
 * JD-Core Version:    0.6.0
 */