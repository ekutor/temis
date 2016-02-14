package com.lowagie.text.pdf.codec;

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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class GifImage
{
  protected DataInputStream in;
  protected int width;
  protected int height;
  protected boolean gctFlag;
  protected int bgIndex;
  protected int bgColor;
  protected int pixelAspect;
  protected boolean lctFlag;
  protected boolean interlace;
  protected int lctSize;
  protected int ix;
  protected int iy;
  protected int iw;
  protected int ih;
  protected byte[] block = new byte[256];
  protected int blockSize = 0;
  protected int dispose = 0;
  protected boolean transparency = false;
  protected int delay = 0;
  protected int transIndex;
  protected static final int MaxStackSize = 4096;
  protected short[] prefix;
  protected byte[] suffix;
  protected byte[] pixelStack;
  protected byte[] pixels;
  protected byte[] m_out;
  protected int m_bpc;
  protected int m_gbpc;
  protected byte[] m_global_table;
  protected byte[] m_local_table;
  protected byte[] m_curr_table;
  protected int m_line_stride;
  protected byte[] fromData;
  protected URL fromUrl;
  protected ArrayList frames = new ArrayList();

  public GifImage(URL paramURL)
    throws IOException
  {
    this.fromUrl = paramURL;
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramURL.openStream();
      process(localInputStream);
    }
    finally
    {
      if (localInputStream != null)
        localInputStream.close();
    }
  }

  public GifImage(String paramString)
    throws IOException
  {
    this(Utilities.toURL(paramString));
  }

  public GifImage(byte[] paramArrayOfByte)
    throws IOException
  {
    this.fromData = paramArrayOfByte;
    ByteArrayInputStream localByteArrayInputStream = null;
    try
    {
      localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      process(localByteArrayInputStream);
    }
    finally
    {
      if (localByteArrayInputStream != null)
        localByteArrayInputStream.close();
    }
  }

  public GifImage(InputStream paramInputStream)
    throws IOException
  {
    process(paramInputStream);
  }

  public int getFrameCount()
  {
    return this.frames.size();
  }

  public Image getImage(int paramInt)
  {
    GifFrame localGifFrame = (GifFrame)this.frames.get(paramInt - 1);
    return localGifFrame.image;
  }

  public int[] getFramePosition(int paramInt)
  {
    GifFrame localGifFrame = (GifFrame)this.frames.get(paramInt - 1);
    return new int[] { localGifFrame.ix, localGifFrame.iy };
  }

  public int[] getLogicalScreen()
  {
    return new int[] { this.width, this.height };
  }

  void process(InputStream paramInputStream)
    throws IOException
  {
    this.in = new DataInputStream(new BufferedInputStream(paramInputStream));
    readHeader();
    readContents();
    if (this.frames.isEmpty())
      throw new IOException("The file does not contain any valid image.");
  }

  protected void readHeader()
    throws IOException
  {
    String str = "";
    for (int i = 0; i < 6; i++)
      str = str + (char)this.in.read();
    if (!str.startsWith("GIF8"))
      throw new IOException("Gif signature nor found.");
    readLSD();
    if (this.gctFlag)
      this.m_global_table = readColorTable(this.m_gbpc);
  }

  protected void readLSD()
    throws IOException
  {
    this.width = readShort();
    this.height = readShort();
    int i = this.in.read();
    this.gctFlag = ((i & 0x80) != 0);
    this.m_gbpc = ((i & 0x7) + 1);
    this.bgIndex = this.in.read();
    this.pixelAspect = this.in.read();
  }

  protected int readShort()
    throws IOException
  {
    return this.in.read() | this.in.read() << 8;
  }

  protected int readBlock()
    throws IOException
  {
    this.blockSize = this.in.read();
    if (this.blockSize <= 0)
      return this.blockSize = 0;
    for (int i = 0; i < this.blockSize; i++)
    {
      int j = this.in.read();
      if (j < 0)
        return this.blockSize = i;
      this.block[i] = (byte)j;
    }
    return this.blockSize;
  }

  protected byte[] readColorTable(int paramInt)
    throws IOException
  {
    int i = 1 << paramInt;
    int j = 3 * i;
    paramInt = newBpc(paramInt);
    byte[] arrayOfByte = new byte[(1 << paramInt) * 3];
    this.in.readFully(arrayOfByte, 0, j);
    return arrayOfByte;
  }

  protected static int newBpc(int paramInt)
  {
    switch (paramInt)
    {
    case 1:
    case 2:
    case 4:
      break;
    case 3:
      return 4;
    default:
      return 8;
    }
    return paramInt;
  }

  protected void readContents()
    throws IOException
  {
    int i = 0;
    while (i == 0)
    {
      int j = this.in.read();
      switch (j)
      {
      case 44:
        readImage();
        break;
      case 33:
        j = this.in.read();
        switch (j)
        {
        case 249:
          readGraphicControlExt();
          break;
        case 255:
          readBlock();
          skip();
          break;
        default:
          skip();
        }
        break;
      default:
        i = 1;
      }
    }
  }

  protected void readImage()
    throws IOException
  {
    this.ix = readShort();
    this.iy = readShort();
    this.iw = readShort();
    this.ih = readShort();
    int i = this.in.read();
    this.lctFlag = ((i & 0x80) != 0);
    this.interlace = ((i & 0x40) != 0);
    this.lctSize = (2 << (i & 0x7));
    this.m_bpc = newBpc(this.m_gbpc);
    if (this.lctFlag)
    {
      this.m_curr_table = readColorTable((i & 0x7) + 1);
      this.m_bpc = newBpc((i & 0x7) + 1);
    }
    else
    {
      this.m_curr_table = this.m_global_table;
    }
    if ((this.transparency) && (this.transIndex >= this.m_curr_table.length / 3))
      this.transparency = false;
    if ((this.transparency) && (this.m_bpc == 1))
    {
      byte[] arrayOfByte = new byte[12];
      System.arraycopy(this.m_curr_table, 0, arrayOfByte, 0, 6);
      this.m_curr_table = arrayOfByte;
      this.m_bpc = 2;
    }
    boolean bool = decodeImageData();
    if (!bool)
      skip();
    ImgRaw localImgRaw = null;
    try
    {
      localImgRaw = new ImgRaw(this.iw, this.ih, 1, this.m_bpc, this.m_out);
      PdfArray localPdfArray = new PdfArray();
      localPdfArray.add(PdfName.INDEXED);
      localPdfArray.add(PdfName.DEVICERGB);
      int j = this.m_curr_table.length;
      localPdfArray.add(new PdfNumber(j / 3 - 1));
      localPdfArray.add(new PdfString(this.m_curr_table));
      PdfDictionary localPdfDictionary = new PdfDictionary();
      localPdfDictionary.put(PdfName.COLORSPACE, localPdfArray);
      localImgRaw.setAdditional(localPdfDictionary);
      if (this.transparency)
        localImgRaw.setTransparency(new int[] { this.transIndex, this.transIndex });
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    localImgRaw.setOriginalType(3);
    localImgRaw.setOriginalData(this.fromData);
    localImgRaw.setUrl(this.fromUrl);
    GifFrame localGifFrame = new GifFrame();
    localGifFrame.image = localImgRaw;
    localGifFrame.ix = this.ix;
    localGifFrame.iy = this.iy;
    this.frames.add(localGifFrame);
  }

  protected boolean decodeImageData()
    throws IOException
  {
    int i = -1;
    int j = this.iw * this.ih;
    int i14 = 0;
    if (this.prefix == null)
      this.prefix = new short[4096];
    if (this.suffix == null)
      this.suffix = new byte[4096];
    if (this.pixelStack == null)
      this.pixelStack = new byte[4097];
    this.m_line_stride = ((this.iw * this.m_bpc + 7) / 8);
    this.m_out = new byte[this.m_line_stride * this.ih];
    int i15 = 1;
    int i16 = this.interlace ? 8 : 1;
    int i17 = 0;
    int i18 = 0;
    int i10 = this.in.read();
    int m = 1 << i10;
    int i2 = m + 1;
    int k = m + 2;
    int i4 = i;
    int i1 = i10 + 1;
    int n = (1 << i1) - 1;
    for (int i6 = 0; i6 < m; i6++)
    {
      this.prefix[i6] = 0;
      this.suffix[i6] = (byte)i6;
    }
    int i13;
    int i12;
    int i11;
    int i7;
    int i5;
    int i9 = i5 = i7 = i11 = i12 = i13 = 0;
    int i8 = 0;
    while (i8 < j)
    {
      if (i12 == 0)
      {
        if (i5 < i1)
        {
          if (i7 == 0)
          {
            i7 = readBlock();
            if (i7 <= 0)
            {
              i14 = 1;
              break;
            }
            i13 = 0;
          }
          i9 += ((this.block[i13] & 0xFF) << i5);
          i5 += 8;
          i13++;
          i7--;
          continue;
        }
        i6 = i9 & n;
        i9 >>= i1;
        i5 -= i1;
        if ((i6 > k) || (i6 == i2))
          break;
        if (i6 == m)
        {
          i1 = i10 + 1;
          n = (1 << i1) - 1;
          k = m + 2;
          i4 = i;
          continue;
        }
        if (i4 == i)
        {
          this.pixelStack[(i12++)] = this.suffix[i6];
          i4 = i6;
          i11 = i6;
          continue;
        }
        int i3 = i6;
        if (i6 == k)
          this.pixelStack[(i12++)] = (byte)i11;
        for (i6 = i4; i6 > m; i6 = this.prefix[i6])
          this.pixelStack[(i12++)] = this.suffix[i6];
        i11 = this.suffix[i6] & 0xFF;
        if (k >= 4096)
          break;
        this.pixelStack[(i12++)] = (byte)i11;
        this.prefix[k] = (short)i4;
        this.suffix[k] = (byte)i11;
        k++;
        if (((k & n) == 0) && (k < 4096))
        {
          i1++;
          n += k;
        }
        i4 = i3;
      }
      i12--;
      i8++;
      setPixel(i18, i17, this.pixelStack[i12]);
      i18++;
      if (i18 < this.iw)
        continue;
      i18 = 0;
      i17 += i16;
      if (i17 < this.ih)
        continue;
      if (this.interlace)
      {
        do
        {
          i15++;
          switch (i15)
          {
          case 2:
            i17 = 4;
            break;
          case 3:
            i17 = 2;
            i16 = 4;
            break;
          case 4:
            i17 = 1;
            i16 = 2;
            break;
          default:
            i17 = this.ih - 1;
            i16 = 0;
          }
        }
        while (i17 >= this.ih);
        continue;
      }
      i17 = this.ih - 1;
      i16 = 0;
    }
    return i14;
  }

  protected void setPixel(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if (this.m_bpc == 8)
    {
      i = paramInt1 + this.iw * paramInt2;
      this.m_out[i] = (byte)paramInt3;
    }
    else
    {
      i = this.m_line_stride * paramInt2 + paramInt1 / (8 / this.m_bpc);
      int j = paramInt3 << 8 - this.m_bpc * (paramInt1 % (8 / this.m_bpc)) - this.m_bpc;
      int tmp81_79 = i;
      byte[] tmp81_76 = this.m_out;
      tmp81_76[tmp81_79] = (byte)(tmp81_76[tmp81_79] | j);
    }
  }

  protected void resetFrame()
  {
  }

  protected void readGraphicControlExt()
    throws IOException
  {
    this.in.read();
    int i = this.in.read();
    this.dispose = ((i & 0x1C) >> 2);
    if (this.dispose == 0)
      this.dispose = 1;
    this.transparency = ((i & 0x1) != 0);
    this.delay = (readShort() * 10);
    this.transIndex = this.in.read();
    this.in.read();
  }

  protected void skip()
    throws IOException
  {
    do
      readBlock();
    while (this.blockSize > 0);
  }

  static class GifFrame
  {
    Image image;
    int ix;
    int iy;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.GifImage
 * JD-Core Version:    0.6.0
 */