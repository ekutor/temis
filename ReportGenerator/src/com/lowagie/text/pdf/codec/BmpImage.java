package com.lowagie.text.pdf.codec;

import com.lowagie.text.BadElementException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgRaw;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class BmpImage
{
  private InputStream inputStream;
  private long bitmapFileSize;
  private long bitmapOffset;
  private long compression;
  private long imageSize;
  private byte[] palette;
  private int imageType;
  private int numBands;
  private boolean isBottomUp;
  private int bitsPerPixel;
  private int redMask;
  private int greenMask;
  private int blueMask;
  private int alphaMask;
  public HashMap properties = new HashMap();
  private long xPelsPerMeter;
  private long yPelsPerMeter;
  private static final int VERSION_2_1_BIT = 0;
  private static final int VERSION_2_4_BIT = 1;
  private static final int VERSION_2_8_BIT = 2;
  private static final int VERSION_2_24_BIT = 3;
  private static final int VERSION_3_1_BIT = 4;
  private static final int VERSION_3_4_BIT = 5;
  private static final int VERSION_3_8_BIT = 6;
  private static final int VERSION_3_24_BIT = 7;
  private static final int VERSION_3_NT_16_BIT = 8;
  private static final int VERSION_3_NT_32_BIT = 9;
  private static final int VERSION_4_1_BIT = 10;
  private static final int VERSION_4_4_BIT = 11;
  private static final int VERSION_4_8_BIT = 12;
  private static final int VERSION_4_16_BIT = 13;
  private static final int VERSION_4_24_BIT = 14;
  private static final int VERSION_4_32_BIT = 15;
  private static final int LCS_CALIBRATED_RGB = 0;
  private static final int LCS_sRGB = 1;
  private static final int LCS_CMYK = 2;
  private static final int BI_RGB = 0;
  private static final int BI_RLE8 = 1;
  private static final int BI_RLE4 = 2;
  private static final int BI_BITFIELDS = 3;
  int width;
  int height;

  BmpImage(InputStream paramInputStream, boolean paramBoolean, int paramInt)
    throws IOException
  {
    this.bitmapFileSize = paramInt;
    this.bitmapOffset = 0L;
    process(paramInputStream, paramBoolean);
  }

  public static Image getImage(URL paramURL)
    throws IOException
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramURL.openStream();
      Image localImage1 = getImage(localInputStream);
      localImage1.setUrl(paramURL);
      Image localImage2 = localImage1;
      return localImage2;
    }
    finally
    {
      if (localInputStream != null)
        localInputStream.close();
    }
    throw localObject;
  }

  public static Image getImage(InputStream paramInputStream)
    throws IOException
  {
    return getImage(paramInputStream, false, 0);
  }

  public static Image getImage(InputStream paramInputStream, boolean paramBoolean, int paramInt)
    throws IOException
  {
    BmpImage localBmpImage = new BmpImage(paramInputStream, paramBoolean, paramInt);
    try
    {
      Image localImage = localBmpImage.getImage();
      localImage.setDpi((int)(localBmpImage.xPelsPerMeter * 0.0254D + 0.5D), (int)(localBmpImage.yPelsPerMeter * 0.0254D + 0.5D));
      localImage.setOriginalType(4);
      return localImage;
    }
    catch (BadElementException localBadElementException)
    {
    }
    throw new ExceptionConverter(localBadElementException);
  }

  public static Image getImage(String paramString)
    throws IOException
  {
    return getImage(Utilities.toURL(paramString));
  }

  public static Image getImage(byte[] paramArrayOfByte)
    throws IOException
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    Image localImage = getImage(localByteArrayInputStream);
    localImage.setOriginalData(paramArrayOfByte);
    return localImage;
  }

  protected void process(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    if ((paramBoolean) || ((paramInputStream instanceof BufferedInputStream)))
      this.inputStream = paramInputStream;
    else
      this.inputStream = new BufferedInputStream(paramInputStream);
    if (!paramBoolean)
    {
      if ((readUnsignedByte(this.inputStream) != 66) || (readUnsignedByte(this.inputStream) != 77))
        throw new RuntimeException("Invalid magic value for BMP file.");
      this.bitmapFileSize = readDWord(this.inputStream);
      readWord(this.inputStream);
      readWord(this.inputStream);
      this.bitmapOffset = readDWord(this.inputStream);
    }
    long l1 = readDWord(this.inputStream);
    if (l1 == 12L)
    {
      this.width = readWord(this.inputStream);
      this.height = readWord(this.inputStream);
    }
    else
    {
      this.width = readLong(this.inputStream);
      this.height = readLong(this.inputStream);
    }
    int i = readWord(this.inputStream);
    this.bitsPerPixel = readWord(this.inputStream);
    this.properties.put("color_planes", new Integer(i));
    this.properties.put("bits_per_pixel", new Integer(this.bitsPerPixel));
    this.numBands = 3;
    if (this.bitmapOffset == 0L)
      this.bitmapOffset = l1;
    int i2;
    if (l1 == 12L)
    {
      this.properties.put("bmp_version", "BMP v. 2.x");
      if (this.bitsPerPixel == 1)
        this.imageType = 0;
      else if (this.bitsPerPixel == 4)
        this.imageType = 1;
      else if (this.bitsPerPixel == 8)
        this.imageType = 2;
      else if (this.bitsPerPixel == 24)
        this.imageType = 3;
      int j = (int)((this.bitmapOffset - 14L - l1) / 3L);
      int k = j * 3;
      if (this.bitmapOffset == l1)
      {
        switch (this.imageType)
        {
        case 0:
          k = 6;
          break;
        case 1:
          k = 48;
          break;
        case 2:
          k = 768;
          break;
        case 3:
          k = 0;
        }
        this.bitmapOffset = (l1 + k);
      }
      readPalette(k);
    }
    else
    {
      this.compression = readDWord(this.inputStream);
      this.imageSize = readDWord(this.inputStream);
      this.xPelsPerMeter = readLong(this.inputStream);
      this.yPelsPerMeter = readLong(this.inputStream);
      long l2 = readDWord(this.inputStream);
      long l3 = readDWord(this.inputStream);
      switch ((int)this.compression)
      {
      case 0:
        this.properties.put("compression", "BI_RGB");
        break;
      case 1:
        this.properties.put("compression", "BI_RLE8");
        break;
      case 2:
        this.properties.put("compression", "BI_RLE4");
        break;
      case 3:
        this.properties.put("compression", "BI_BITFIELDS");
      }
      this.properties.put("x_pixels_per_meter", new Long(this.xPelsPerMeter));
      this.properties.put("y_pixels_per_meter", new Long(this.yPelsPerMeter));
      this.properties.put("colors_used", new Long(l2));
      this.properties.put("colors_important", new Long(l3));
      if (l1 == 40L)
      {
        switch ((int)this.compression)
        {
        case 0:
        case 1:
        case 2:
          if (this.bitsPerPixel == 1)
          {
            this.imageType = 4;
          }
          else if (this.bitsPerPixel == 4)
          {
            this.imageType = 5;
          }
          else if (this.bitsPerPixel == 8)
          {
            this.imageType = 6;
          }
          else if (this.bitsPerPixel == 24)
          {
            this.imageType = 7;
          }
          else if (this.bitsPerPixel == 16)
          {
            this.imageType = 8;
            this.redMask = 31744;
            this.greenMask = 992;
            this.blueMask = 31;
            this.properties.put("red_mask", new Integer(this.redMask));
            this.properties.put("green_mask", new Integer(this.greenMask));
            this.properties.put("blue_mask", new Integer(this.blueMask));
          }
          else if (this.bitsPerPixel == 32)
          {
            this.imageType = 9;
            this.redMask = 16711680;
            this.greenMask = 65280;
            this.blueMask = 255;
            this.properties.put("red_mask", new Integer(this.redMask));
            this.properties.put("green_mask", new Integer(this.greenMask));
            this.properties.put("blue_mask", new Integer(this.blueMask));
          }
          int n = (int)((this.bitmapOffset - 14L - l1) / 4L);
          i2 = n * 4;
          if (this.bitmapOffset == l1)
          {
            switch (this.imageType)
            {
            case 4:
              i2 = (int)(l2 == 0L ? 2L : l2) * 4;
              break;
            case 5:
              i2 = (int)(l2 == 0L ? 16L : l2) * 4;
              break;
            case 6:
              i2 = (int)(l2 == 0L ? 256L : l2) * 4;
              break;
            default:
              i2 = 0;
            }
            this.bitmapOffset = (l1 + i2);
          }
          readPalette(i2);
          this.properties.put("bmp_version", "BMP v. 3.x");
          break;
        case 3:
          if (this.bitsPerPixel == 16)
            this.imageType = 8;
          else if (this.bitsPerPixel == 32)
            this.imageType = 9;
          this.redMask = (int)readDWord(this.inputStream);
          this.greenMask = (int)readDWord(this.inputStream);
          this.blueMask = (int)readDWord(this.inputStream);
          this.properties.put("red_mask", new Integer(this.redMask));
          this.properties.put("green_mask", new Integer(this.greenMask));
          this.properties.put("blue_mask", new Integer(this.blueMask));
          if (l2 != 0L)
          {
            i2 = (int)l2 * 4;
            readPalette(i2);
          }
          this.properties.put("bmp_version", "BMP v. 3.x NT");
          break;
        default:
          throw new RuntimeException("Invalid compression specified in BMP file.");
        }
      }
      else if (l1 == 108L)
      {
        this.properties.put("bmp_version", "BMP v. 4.x");
        this.redMask = (int)readDWord(this.inputStream);
        this.greenMask = (int)readDWord(this.inputStream);
        this.blueMask = (int)readDWord(this.inputStream);
        this.alphaMask = (int)readDWord(this.inputStream);
        long l4 = readDWord(this.inputStream);
        int i3 = readLong(this.inputStream);
        int i4 = readLong(this.inputStream);
        int i5 = readLong(this.inputStream);
        int i6 = readLong(this.inputStream);
        int i7 = readLong(this.inputStream);
        int i8 = readLong(this.inputStream);
        int i9 = readLong(this.inputStream);
        int i10 = readLong(this.inputStream);
        int i11 = readLong(this.inputStream);
        long l5 = readDWord(this.inputStream);
        long l6 = readDWord(this.inputStream);
        long l7 = readDWord(this.inputStream);
        if (this.bitsPerPixel == 1)
        {
          this.imageType = 10;
        }
        else if (this.bitsPerPixel == 4)
        {
          this.imageType = 11;
        }
        else if (this.bitsPerPixel == 8)
        {
          this.imageType = 12;
        }
        else if (this.bitsPerPixel == 16)
        {
          this.imageType = 13;
          if ((int)this.compression == 0)
          {
            this.redMask = 31744;
            this.greenMask = 992;
            this.blueMask = 31;
          }
        }
        else if (this.bitsPerPixel == 24)
        {
          this.imageType = 14;
        }
        else if (this.bitsPerPixel == 32)
        {
          this.imageType = 15;
          if ((int)this.compression == 0)
          {
            this.redMask = 16711680;
            this.greenMask = 65280;
            this.blueMask = 255;
          }
        }
        this.properties.put("red_mask", new Integer(this.redMask));
        this.properties.put("green_mask", new Integer(this.greenMask));
        this.properties.put("blue_mask", new Integer(this.blueMask));
        this.properties.put("alpha_mask", new Integer(this.alphaMask));
        int i12 = (int)((this.bitmapOffset - 14L - l1) / 4L);
        int i13 = i12 * 4;
        if (this.bitmapOffset == l1)
        {
          switch (this.imageType)
          {
          case 10:
            i13 = (int)(l2 == 0L ? 2L : l2) * 4;
            break;
          case 11:
            i13 = (int)(l2 == 0L ? 16L : l2) * 4;
            break;
          case 12:
            i13 = (int)(l2 == 0L ? 256L : l2) * 4;
            break;
          default:
            i13 = 0;
          }
          this.bitmapOffset = (l1 + i13);
        }
        readPalette(i13);
        switch ((int)l4)
        {
        case 0:
          this.properties.put("color_space", "LCS_CALIBRATED_RGB");
          this.properties.put("redX", new Integer(i3));
          this.properties.put("redY", new Integer(i4));
          this.properties.put("redZ", new Integer(i5));
          this.properties.put("greenX", new Integer(i6));
          this.properties.put("greenY", new Integer(i7));
          this.properties.put("greenZ", new Integer(i8));
          this.properties.put("blueX", new Integer(i9));
          this.properties.put("blueY", new Integer(i10));
          this.properties.put("blueZ", new Integer(i11));
          this.properties.put("gamma_red", new Long(l5));
          this.properties.put("gamma_green", new Long(l6));
          this.properties.put("gamma_blue", new Long(l7));
          throw new RuntimeException("Not implemented yet.");
        case 1:
          this.properties.put("color_space", "LCS_sRGB");
          break;
        case 2:
          this.properties.put("color_space", "LCS_CMYK");
          throw new RuntimeException("Not implemented yet.");
        }
      }
      else
      {
        this.properties.put("bmp_version", "BMP v. 5.x");
        throw new RuntimeException("BMP version 5 not implemented yet.");
      }
    }
    if (this.height > 0)
    {
      this.isBottomUp = true;
    }
    else
    {
      this.isBottomUp = false;
      this.height = Math.abs(this.height);
    }
    int m;
    byte[] arrayOfByte1;
    byte[] arrayOfByte2;
    byte[] arrayOfByte3;
    if ((this.bitsPerPixel == 1) || (this.bitsPerPixel == 4) || (this.bitsPerPixel == 8))
    {
      this.numBands = 1;
      if ((this.imageType == 0) || (this.imageType == 1) || (this.imageType == 2))
      {
        m = this.palette.length / 3;
        if (m > 256)
          m = 256;
        arrayOfByte1 = new byte[m];
        arrayOfByte2 = new byte[m];
        arrayOfByte3 = new byte[m];
        i2 = 0;
      }
    }
    while (i2 < m)
    {
      int i1 = 3 * i2;
      arrayOfByte3[i2] = this.palette[i1];
      arrayOfByte2[i2] = this.palette[(i1 + 1)];
      arrayOfByte1[i2] = this.palette[(i1 + 2)];
      i2++;
      continue;
      m = this.palette.length / 4;
      if (m > 256)
        m = 256;
      arrayOfByte1 = new byte[m];
      arrayOfByte2 = new byte[m];
      arrayOfByte3 = new byte[m];
      i2 = 0;
      while (i2 < m)
      {
        i1 = 4 * i2;
        arrayOfByte3[i2] = this.palette[i1];
        arrayOfByte2[i2] = this.palette[(i1 + 1)];
        arrayOfByte1[i2] = this.palette[(i1 + 2)];
        i2++;
        continue;
        if (this.bitsPerPixel == 16)
          this.numBands = 3;
        else if (this.bitsPerPixel == 32)
          this.numBands = (this.alphaMask == 0 ? 3 : 4);
        else
          this.numBands = 3;
      }
    }
  }

  private byte[] getPalette(int paramInt)
  {
    if (this.palette == null)
      return null;
    byte[] arrayOfByte = new byte[this.palette.length / paramInt * 3];
    int i = this.palette.length / paramInt;
    for (int j = 0; j < i; j++)
    {
      int k = j * paramInt;
      int m = j * 3;
      arrayOfByte[(m + 2)] = this.palette[(k++)];
      arrayOfByte[(m + 1)] = this.palette[(k++)];
      arrayOfByte[m] = this.palette[k];
    }
    return arrayOfByte;
  }

  private Image getImage()
    throws IOException, BadElementException
  {
    byte[] arrayOfByte = null;
    switch (this.imageType)
    {
    case 0:
      return read1Bit(3);
    case 1:
      return read4Bit(3);
    case 2:
      return read8Bit(3);
    case 3:
      arrayOfByte = new byte[this.width * this.height * 3];
      read24Bit(arrayOfByte);
      return new ImgRaw(this.width, this.height, 3, 8, arrayOfByte);
    case 4:
      return read1Bit(4);
    case 5:
      switch ((int)this.compression)
      {
      case 0:
        return read4Bit(4);
      case 2:
        return readRLE4();
      }
      throw new RuntimeException("Invalid compression specified for BMP file.");
    case 6:
      switch ((int)this.compression)
      {
      case 0:
        return read8Bit(4);
      case 1:
        return readRLE8();
      }
      throw new RuntimeException("Invalid compression specified for BMP file.");
    case 7:
      arrayOfByte = new byte[this.width * this.height * 3];
      read24Bit(arrayOfByte);
      return new ImgRaw(this.width, this.height, 3, 8, arrayOfByte);
    case 8:
      return read1632Bit(false);
    case 9:
      return read1632Bit(true);
    case 10:
      return read1Bit(4);
    case 11:
      switch ((int)this.compression)
      {
      case 0:
        return read4Bit(4);
      case 2:
        return readRLE4();
      }
      throw new RuntimeException("Invalid compression specified for BMP file.");
    case 12:
      switch ((int)this.compression)
      {
      case 0:
        return read8Bit(4);
      case 1:
        return readRLE8();
      }
      throw new RuntimeException("Invalid compression specified for BMP file.");
    case 13:
      return read1632Bit(false);
    case 14:
      arrayOfByte = new byte[this.width * this.height * 3];
      read24Bit(arrayOfByte);
      return new ImgRaw(this.width, this.height, 3, 8, arrayOfByte);
    case 15:
      return read1632Bit(true);
    }
    return null;
  }

  private Image indexedModel(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws BadElementException
  {
    ImgRaw localImgRaw = new ImgRaw(this.width, this.height, 1, paramInt1, paramArrayOfByte);
    PdfArray localPdfArray = new PdfArray();
    localPdfArray.add(PdfName.INDEXED);
    localPdfArray.add(PdfName.DEVICERGB);
    byte[] arrayOfByte = getPalette(paramInt2);
    int i = arrayOfByte.length;
    localPdfArray.add(new PdfNumber(i / 3 - 1));
    localPdfArray.add(new PdfString(arrayOfByte));
    PdfDictionary localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(PdfName.COLORSPACE, localPdfArray);
    localImgRaw.setAdditional(localPdfDictionary);
    return localImgRaw;
  }

  private void readPalette(int paramInt)
    throws IOException
  {
    if (paramInt == 0)
      return;
    this.palette = new byte[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      int j = this.inputStream.read(this.palette, i, paramInt - i);
      if (j < 0)
        throw new RuntimeException("incomplete palette");
      i += j;
    }
    this.properties.put("palette", this.palette);
  }

  private Image read1Bit(int paramInt)
    throws IOException, BadElementException
  {
    byte[] arrayOfByte1 = new byte[(this.width + 7) / 8 * this.height];
    int i = 0;
    int j = (int)Math.ceil(this.width / 8.0D);
    int k = j % 4;
    if (k != 0)
      i = 4 - k;
    int m = (j + i) * this.height;
    byte[] arrayOfByte2 = new byte[m];
    int n = 0;
    while (n < m)
      n += this.inputStream.read(arrayOfByte2, n, m - n);
    if (this.isBottomUp)
      for (i1 = 0; i1 < this.height; i1++)
        System.arraycopy(arrayOfByte2, m - (i1 + 1) * (j + i), arrayOfByte1, i1 * j, j);
    for (int i1 = 0; i1 < this.height; i1++)
      System.arraycopy(arrayOfByte2, i1 * (j + i), arrayOfByte1, i1 * j, j);
    return indexedModel(arrayOfByte1, 1, paramInt);
  }

  private Image read4Bit(int paramInt)
    throws IOException, BadElementException
  {
    byte[] arrayOfByte1 = new byte[(this.width + 1) / 2 * this.height];
    int i = 0;
    int j = (int)Math.ceil(this.width / 2.0D);
    int k = j % 4;
    if (k != 0)
      i = 4 - k;
    int m = (j + i) * this.height;
    byte[] arrayOfByte2 = new byte[m];
    int n = 0;
    while (n < m)
      n += this.inputStream.read(arrayOfByte2, n, m - n);
    if (this.isBottomUp)
      for (i1 = 0; i1 < this.height; i1++)
        System.arraycopy(arrayOfByte2, m - (i1 + 1) * (j + i), arrayOfByte1, i1 * j, j);
    for (int i1 = 0; i1 < this.height; i1++)
      System.arraycopy(arrayOfByte2, i1 * (j + i), arrayOfByte1, i1 * j, j);
    return indexedModel(arrayOfByte1, 4, paramInt);
  }

  private Image read8Bit(int paramInt)
    throws IOException, BadElementException
  {
    byte[] arrayOfByte1 = new byte[this.width * this.height];
    int i = 0;
    int j = this.width * 8;
    if (j % 32 != 0)
    {
      i = (j / 32 + 1) * 32 - j;
      i = (int)Math.ceil(i / 8.0D);
    }
    int k = (this.width + i) * this.height;
    byte[] arrayOfByte2 = new byte[k];
    int m = 0;
    while (m < k)
      m += this.inputStream.read(arrayOfByte2, m, k - m);
    if (this.isBottomUp)
      for (n = 0; n < this.height; n++)
        System.arraycopy(arrayOfByte2, k - (n + 1) * (this.width + i), arrayOfByte1, n * this.width, this.width);
    for (int n = 0; n < this.height; n++)
      System.arraycopy(arrayOfByte2, n * (this.width + i), arrayOfByte1, n * this.width, this.width);
    return indexedModel(arrayOfByte1, 8, paramInt);
  }

  private void read24Bit(byte[] paramArrayOfByte)
  {
    int i = 0;
    int j = this.width * 24;
    if (j % 32 != 0)
    {
      i = (j / 32 + 1) * 32 - j;
      i = (int)Math.ceil(i / 8.0D);
    }
    int k = (this.width * 3 + 3) / 4 * 4 * this.height;
    byte[] arrayOfByte = new byte[k];
    try
    {
      int m = 0;
      while (m < k)
      {
        i1 = this.inputStream.read(arrayOfByte, m, k - m);
        if (i1 < 0)
          break;
        m += i1;
      }
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    int n = 0;
    int i3;
    if (this.isBottomUp)
    {
      i2 = this.width * this.height * 3 - 1;
      i1 = -i;
      for (i3 = 0; i3 < this.height; i3++)
      {
        n = i2 - (i3 + 1) * this.width * 3 + 1;
        i1 += i;
        for (int i4 = 0; i4 < this.width; i4++)
        {
          paramArrayOfByte[(n + 2)] = arrayOfByte[(i1++)];
          paramArrayOfByte[(n + 1)] = arrayOfByte[(i1++)];
          paramArrayOfByte[n] = arrayOfByte[(i1++)];
          n += 3;
        }
      }
    }
    int i1 = -i;
    for (int i2 = 0; i2 < this.height; i2++)
    {
      i1 += i;
      for (i3 = 0; i3 < this.width; i3++)
      {
        paramArrayOfByte[(n + 2)] = arrayOfByte[(i1++)];
        paramArrayOfByte[(n + 1)] = arrayOfByte[(i1++)];
        paramArrayOfByte[n] = arrayOfByte[(i1++)];
        n += 3;
      }
    }
  }

  private int findMask(int paramInt)
  {
    for (int i = 0; (i < 32) && ((paramInt & 0x1) != 1); i++)
      paramInt >>>= 1;
    return paramInt;
  }

  private int findShift(int paramInt)
  {
    for (int i = 0; (i < 32) && ((paramInt & 0x1) != 1); i++)
      paramInt >>>= 1;
    return i;
  }

  private Image read1632Bit(boolean paramBoolean)
    throws IOException, BadElementException
  {
    int i = findMask(this.redMask);
    int j = findShift(this.redMask);
    int k = i + 1;
    int m = findMask(this.greenMask);
    int n = findShift(this.greenMask);
    int i1 = m + 1;
    int i2 = findMask(this.blueMask);
    int i3 = findShift(this.blueMask);
    int i4 = i2 + 1;
    byte[] arrayOfByte = new byte[this.width * this.height * 3];
    int i5 = 0;
    if (!paramBoolean)
    {
      i6 = this.width * 16;
      if (i6 % 32 != 0)
      {
        i5 = (i6 / 32 + 1) * 32 - i6;
        i5 = (int)Math.ceil(i5 / 8.0D);
      }
    }
    int i6 = (int)this.imageSize;
    if (i6 == 0)
      i6 = (int)(this.bitmapFileSize - this.bitmapOffset);
    int i7 = 0;
    int i10;
    int i8;
    if (this.isBottomUp)
      for (i9 = this.height - 1; i9 >= 0; i9--)
      {
        i7 = this.width * 3 * i9;
        for (i10 = 0; i10 < this.width; i10++)
        {
          if (paramBoolean)
            i8 = (int)readDWord(this.inputStream);
          else
            i8 = readWord(this.inputStream);
          arrayOfByte[(i7++)] = (byte)((i8 >>> j & i) * 256 / k);
          arrayOfByte[(i7++)] = (byte)((i8 >>> n & m) * 256 / i1);
          arrayOfByte[(i7++)] = (byte)((i8 >>> i3 & i2) * 256 / i4);
        }
        for (i10 = 0; i10 < i5; i10++)
          this.inputStream.read();
      }
    for (int i9 = 0; i9 < this.height; i9++)
    {
      for (i10 = 0; i10 < this.width; i10++)
      {
        if (paramBoolean)
          i8 = (int)readDWord(this.inputStream);
        else
          i8 = readWord(this.inputStream);
        arrayOfByte[(i7++)] = (byte)((i8 >>> j & i) * 256 / k);
        arrayOfByte[(i7++)] = (byte)((i8 >>> n & m) * 256 / i1);
        arrayOfByte[(i7++)] = (byte)((i8 >>> i3 & i2) * 256 / i4);
      }
      for (i10 = 0; i10 < i5; i10++)
        this.inputStream.read();
    }
    return new ImgRaw(this.width, this.height, 3, 8, arrayOfByte);
  }

  private Image readRLE8()
    throws IOException, BadElementException
  {
    int i = (int)this.imageSize;
    if (i == 0)
      i = (int)(this.bitmapFileSize - this.bitmapOffset);
    byte[] arrayOfByte1 = new byte[i];
    int j = 0;
    while (j < i)
      j += this.inputStream.read(arrayOfByte1, j, i - j);
    Object localObject = decodeRLE(true, arrayOfByte1);
    i = this.width * this.height;
    if (this.isBottomUp)
    {
      byte[] arrayOfByte2 = new byte[localObject.length];
      int k = this.width;
      for (int m = 0; m < this.height; m++)
        System.arraycopy(localObject, i - (m + 1) * k, arrayOfByte2, m * k, k);
      localObject = arrayOfByte2;
    }
    return (Image)indexedModel(localObject, 8, 4);
  }

  private Image readRLE4()
    throws IOException, BadElementException
  {
    int i = (int)this.imageSize;
    if (i == 0)
      i = (int)(this.bitmapFileSize - this.bitmapOffset);
    byte[] arrayOfByte1 = new byte[i];
    int j = 0;
    while (j < i)
      j += this.inputStream.read(arrayOfByte1, j, i - j);
    byte[] arrayOfByte2 = decodeRLE(false, arrayOfByte1);
    if (this.isBottomUp)
    {
      byte[] arrayOfByte3 = arrayOfByte2;
      arrayOfByte2 = new byte[this.width * this.height];
      int m = 0;
      for (i2 = this.height - 1; i2 >= 0; i2--)
      {
        n = i2 * this.width;
        i1 = m + this.width;
        while (m != i1)
          arrayOfByte2[(m++)] = arrayOfByte3[(n++)];
      }
    }
    int k = (this.width + 1) / 2;
    byte[] arrayOfByte4 = new byte[k * this.height];
    int n = 0;
    int i1 = 0;
    for (int i2 = 0; i2 < this.height; i2++)
    {
      for (int i3 = 0; i3 < this.width; i3++)
        if ((i3 & 0x1) == 0)
        {
          arrayOfByte4[(i1 + i3 / 2)] = (byte)(arrayOfByte2[(n++)] << 4);
        }
        else
        {
          int tmp239_238 = (i1 + i3 / 2);
          byte[] tmp239_230 = arrayOfByte4;
          tmp239_230[tmp239_238] = (byte)(tmp239_230[tmp239_238] | (byte)(arrayOfByte2[(n++)] & 0xF));
        }
      i1 += k;
    }
    return indexedModel(arrayOfByte4, 4, 4);
  }

  private byte[] decodeRLE(boolean paramBoolean, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[this.width * this.height];
    try
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      while ((m < this.height) && (i < paramArrayOfByte.length))
      {
        int n = paramArrayOfByte[(i++)] & 0xFF;
        int i1;
        int i2;
        if (n != 0)
        {
          i1 = paramArrayOfByte[(i++)] & 0xFF;
          if (paramBoolean)
            for (i2 = n; i2 != 0; i2--)
              arrayOfByte[(k++)] = (byte)i1;
          for (i2 = 0; i2 < n; i2++)
            arrayOfByte[(k++)] = (byte)((i2 & 0x1) == 1 ? i1 & 0xF : i1 >>> 4 & 0xF);
          j += n;
          continue;
        }
        n = paramArrayOfByte[(i++)] & 0xFF;
        if (n == 1)
          break;
        switch (n)
        {
        case 0:
          j = 0;
          m++;
          k = m * this.width;
          break;
        case 2:
          j += (paramArrayOfByte[(i++)] & 0xFF);
          m += (paramArrayOfByte[(i++)] & 0xFF);
          k = m * this.width + j;
          break;
        default:
          if (paramBoolean)
            for (i1 = n; i1 != 0; i1--)
              arrayOfByte[(k++)] = (byte)(paramArrayOfByte[(i++)] & 0xFF);
          i1 = 0;
          for (i2 = 0; i2 < n; i2++)
          {
            if ((i2 & 0x1) == 0)
              i1 = paramArrayOfByte[(i++)] & 0xFF;
            arrayOfByte[(k++)] = (byte)((i2 & 0x1) == 1 ? i1 & 0xF : i1 >>> 4 & 0xF);
          }
          j += n;
          if (paramBoolean)
          {
            if ((n & 0x1) != 1)
              continue;
            i++;
            continue;
          }
          if (((n & 0x3) != 1) && ((n & 0x3) != 2))
            continue;
          i++;
        }
      }
    }
    catch (RuntimeException localRuntimeException)
    {
    }
    return arrayOfByte;
  }

  private int readUnsignedByte(InputStream paramInputStream)
    throws IOException
  {
    return paramInputStream.read() & 0xFF;
  }

  private int readUnsignedShort(InputStream paramInputStream)
    throws IOException
  {
    int i = readUnsignedByte(paramInputStream);
    int j = readUnsignedByte(paramInputStream);
    return (j << 8 | i) & 0xFFFF;
  }

  private int readShort(InputStream paramInputStream)
    throws IOException
  {
    int i = readUnsignedByte(paramInputStream);
    int j = readUnsignedByte(paramInputStream);
    return j << 8 | i;
  }

  private int readWord(InputStream paramInputStream)
    throws IOException
  {
    return readUnsignedShort(paramInputStream);
  }

  private long readUnsignedInt(InputStream paramInputStream)
    throws IOException
  {
    int i = readUnsignedByte(paramInputStream);
    int j = readUnsignedByte(paramInputStream);
    int k = readUnsignedByte(paramInputStream);
    int m = readUnsignedByte(paramInputStream);
    long l = m << 24 | k << 16 | j << 8 | i;
    return l & 0xFFFFFFFF;
  }

  private int readInt(InputStream paramInputStream)
    throws IOException
  {
    int i = readUnsignedByte(paramInputStream);
    int j = readUnsignedByte(paramInputStream);
    int k = readUnsignedByte(paramInputStream);
    int m = readUnsignedByte(paramInputStream);
    return m << 24 | k << 16 | j << 8 | i;
  }

  private long readDWord(InputStream paramInputStream)
    throws IOException
  {
    return readUnsignedInt(paramInputStream);
  }

  private int readLong(InputStream paramInputStream)
    throws IOException
  {
    return readInt(paramInputStream);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.BmpImage
 * JD-Core Version:    0.6.0
 */