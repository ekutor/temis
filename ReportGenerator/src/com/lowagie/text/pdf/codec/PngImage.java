package com.lowagie.text.pdf.codec;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgRaw;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class PngImage
{
  public static final int[] PNGID = { 137, 80, 78, 71, 13, 10, 26, 10 };
  public static final String IHDR = "IHDR";
  public static final String PLTE = "PLTE";
  public static final String IDAT = "IDAT";
  public static final String IEND = "IEND";
  public static final String tRNS = "tRNS";
  public static final String pHYs = "pHYs";
  public static final String gAMA = "gAMA";
  public static final String cHRM = "cHRM";
  public static final String sRGB = "sRGB";
  public static final String iCCP = "iCCP";
  private static final int TRANSFERSIZE = 4096;
  private static final int PNG_FILTER_NONE = 0;
  private static final int PNG_FILTER_SUB = 1;
  private static final int PNG_FILTER_UP = 2;
  private static final int PNG_FILTER_AVERAGE = 3;
  private static final int PNG_FILTER_PAETH = 4;
  private static final PdfName[] intents = { PdfName.PERCEPTUAL, PdfName.RELATIVECOLORIMETRIC, PdfName.SATURATION, PdfName.ABSOLUTECOLORIMETRIC };
  InputStream is;
  DataInputStream dataStream;
  int width;
  int height;
  int bitDepth;
  int colorType;
  int compressionMethod;
  int filterMethod;
  int interlaceMethod;
  PdfDictionary additional = new PdfDictionary();
  byte[] image;
  byte[] smask;
  byte[] trans;
  NewByteArrayOutputStream idat = new NewByteArrayOutputStream();
  int dpiX;
  int dpiY;
  float XYRatio;
  boolean genBWMask;
  boolean palShades;
  int transRedGray = -1;
  int transGreen = -1;
  int transBlue = -1;
  int inputBands;
  int bytesPerPixel;
  byte[] colorTable;
  float gamma = 1.0F;
  boolean hasCHRM = false;
  float xW;
  float yW;
  float xR;
  float yR;
  float xG;
  float yG;
  float xB;
  float yB;
  PdfName intent;
  ICC_Profile icc_profile;

  PngImage(InputStream paramInputStream)
  {
    this.is = paramInputStream;
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
    PngImage localPngImage = new PngImage(paramInputStream);
    return localPngImage.getImage();
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

  boolean checkMarker(String paramString)
  {
    if (paramString.length() != 4)
      return false;
    for (int i = 0; i < 4; i++)
    {
      int j = paramString.charAt(i);
      if (((j < 97) || (j > 122)) && ((j < 65) || (j > 90)))
        return false;
    }
    return true;
  }

  void readPng()
    throws IOException
  {
    for (int i = 0; i < PNGID.length; i++)
    {
      if (PNGID[i] == this.is.read())
        continue;
      throw new IOException("File is not a valid PNG.");
    }
    byte[] arrayOfByte1 = new byte[4096];
    while (true)
    {
      int j = getInt(this.is);
      String str = getString(this.is);
      if ((j < 0) || (!checkMarker(str)))
        throw new IOException("Corrupted PNG file.");
      int k;
      if ("IDAT".equals(str))
        while (j != 0)
        {
          k = this.is.read(arrayOfByte1, 0, Math.min(j, 4096));
          if (k < 0)
            return;
          this.idat.write(arrayOfByte1, 0, k);
          j -= k;
        }
      int i2;
      if ("tRNS".equals(str))
      {
        switch (this.colorType)
        {
        case 0:
          if (j < 2)
            break;
          j -= 2;
          k = getWord(this.is);
          if (this.bitDepth == 16)
            this.transRedGray = k;
          else
            this.additional.put(PdfName.MASK, new PdfLiteral("[" + k + " " + k + "]"));
          break;
        case 2:
          if (j < 6)
            break;
          j -= 6;
          k = getWord(this.is);
          int n = getWord(this.is);
          i2 = getWord(this.is);
          if (this.bitDepth == 16)
          {
            this.transRedGray = k;
            this.transGreen = n;
            this.transBlue = i2;
          }
          else
          {
            this.additional.put(PdfName.MASK, new PdfLiteral("[" + k + " " + k + " " + n + " " + n + " " + i2 + " " + i2 + "]"));
          }
          break;
        case 3:
          if (j <= 0)
            break;
          this.trans = new byte[j];
          for (k = 0; k < j; k++)
            this.trans[k] = (byte)this.is.read();
          j = 0;
        case 1:
        }
        Utilities.skip(this.is, j);
      }
      else if ("IHDR".equals(str))
      {
        this.width = getInt(this.is);
        this.height = getInt(this.is);
        this.bitDepth = this.is.read();
        this.colorType = this.is.read();
        this.compressionMethod = this.is.read();
        this.filterMethod = this.is.read();
        this.interlaceMethod = this.is.read();
      }
      else if ("PLTE".equals(str))
      {
        if (this.colorType == 3)
        {
          PdfArray localPdfArray = new PdfArray();
          localPdfArray.add(PdfName.INDEXED);
          localPdfArray.add(getColorspace());
          localPdfArray.add(new PdfNumber(j / 3 - 1));
          ByteBuffer localByteBuffer = new ByteBuffer();
          while (j-- > 0)
            localByteBuffer.append_i(this.is.read());
          localPdfArray.add(new PdfString(this.colorTable = localByteBuffer.toByteArray()));
          this.additional.put(PdfName.COLORSPACE, localPdfArray);
        }
        else
        {
          Utilities.skip(this.is, j);
        }
      }
      else
      {
        int m;
        int i1;
        if ("pHYs".equals(str))
        {
          m = getInt(this.is);
          i1 = getInt(this.is);
          i2 = this.is.read();
          if (i2 == 1)
          {
            this.dpiX = (int)(m * 0.0254F + 0.5F);
            this.dpiY = (int)(i1 * 0.0254F + 0.5F);
          }
          else if (i1 != 0)
          {
            this.XYRatio = (m / i1);
          }
        }
        else if ("cHRM".equals(str))
        {
          this.xW = (getInt(this.is) / 100000.0F);
          this.yW = (getInt(this.is) / 100000.0F);
          this.xR = (getInt(this.is) / 100000.0F);
          this.yR = (getInt(this.is) / 100000.0F);
          this.xG = (getInt(this.is) / 100000.0F);
          this.yG = (getInt(this.is) / 100000.0F);
          this.xB = (getInt(this.is) / 100000.0F);
          this.yB = (getInt(this.is) / 100000.0F);
          this.hasCHRM = ((Math.abs(this.xW) >= 1.0E-004F) && (Math.abs(this.yW) >= 1.0E-004F) && (Math.abs(this.xR) >= 1.0E-004F) && (Math.abs(this.yR) >= 1.0E-004F) && (Math.abs(this.xG) >= 1.0E-004F) && (Math.abs(this.yG) >= 1.0E-004F) && (Math.abs(this.xB) >= 1.0E-004F) && (Math.abs(this.yB) >= 1.0E-004F));
        }
        else if ("sRGB".equals(str))
        {
          m = this.is.read();
          this.intent = intents[m];
          this.gamma = 2.2F;
          this.xW = 0.3127F;
          this.yW = 0.329F;
          this.xR = 0.64F;
          this.yR = 0.33F;
          this.xG = 0.3F;
          this.yG = 0.6F;
          this.xB = 0.15F;
          this.yB = 0.06F;
          this.hasCHRM = true;
        }
        else if ("gAMA".equals(str))
        {
          m = getInt(this.is);
          if (m != 0)
          {
            this.gamma = (100000.0F / m);
            if (!this.hasCHRM)
            {
              this.xW = 0.3127F;
              this.yW = 0.329F;
              this.xR = 0.64F;
              this.yR = 0.33F;
              this.xG = 0.3F;
              this.yG = 0.6F;
              this.xB = 0.15F;
              this.yB = 0.06F;
              this.hasCHRM = true;
            }
          }
        }
        else if ("iCCP".equals(str))
        {
          do
            j--;
          while (this.is.read() != 0);
          this.is.read();
          j--;
          byte[] arrayOfByte2 = new byte[j];
          i1 = 0;
          while (j > 0)
          {
            i2 = this.is.read(arrayOfByte2, i1, j);
            if (i2 < 0)
              throw new IOException("Premature end of file.");
            i1 += i2;
            j -= i2;
          }
          byte[] arrayOfByte3 = PdfReader.FlateDecode(arrayOfByte2, true);
          arrayOfByte2 = null;
          try
          {
            this.icc_profile = ICC_Profile.getInstance(arrayOfByte3);
          }
          catch (RuntimeException localRuntimeException)
          {
            this.icc_profile = null;
          }
        }
        else
        {
          if ("IEND".equals(str))
            break;
          Utilities.skip(this.is, j);
        }
      }
      Utilities.skip(this.is, 4);
    }
  }

  PdfObject getColorspace()
  {
    if (this.icc_profile != null)
    {
      if ((this.colorType & 0x2) == 0)
        return PdfName.DEVICEGRAY;
      return PdfName.DEVICERGB;
    }
    if ((this.gamma == 1.0F) && (!this.hasCHRM))
    {
      if ((this.colorType & 0x2) == 0)
        return PdfName.DEVICEGRAY;
      return PdfName.DEVICERGB;
    }
    PdfArray localPdfArray1 = new PdfArray();
    PdfDictionary localPdfDictionary = new PdfDictionary();
    if ((this.colorType & 0x2) == 0)
    {
      if (this.gamma == 1.0F)
        return PdfName.DEVICEGRAY;
      localPdfArray1.add(PdfName.CALGRAY);
      localPdfDictionary.put(PdfName.GAMMA, new PdfNumber(this.gamma));
      localPdfDictionary.put(PdfName.WHITEPOINT, new PdfLiteral("[1 1 1]"));
      localPdfArray1.add(localPdfDictionary);
    }
    else
    {
      Object localObject = new PdfLiteral("[1 1 1]");
      localPdfArray1.add(PdfName.CALRGB);
      if (this.gamma != 1.0F)
      {
        PdfArray localPdfArray2 = new PdfArray();
        PdfNumber localPdfNumber = new PdfNumber(this.gamma);
        localPdfArray2.add(localPdfNumber);
        localPdfArray2.add(localPdfNumber);
        localPdfArray2.add(localPdfNumber);
        localPdfDictionary.put(PdfName.GAMMA, localPdfArray2);
      }
      if (this.hasCHRM)
      {
        float f1 = this.yW * ((this.xG - this.xB) * this.yR - (this.xR - this.xB) * this.yG + (this.xR - this.xG) * this.yB);
        float f2 = this.yR * ((this.xG - this.xB) * this.yW - (this.xW - this.xB) * this.yG + (this.xW - this.xG) * this.yB) / f1;
        float f3 = f2 * this.xR / this.yR;
        float f4 = f2 * ((1.0F - this.xR) / this.yR - 1.0F);
        float f5 = -this.yG * ((this.xR - this.xB) * this.yW - (this.xW - this.xB) * this.yR + (this.xW - this.xR) * this.yB) / f1;
        float f6 = f5 * this.xG / this.yG;
        float f7 = f5 * ((1.0F - this.xG) / this.yG - 1.0F);
        float f8 = this.yB * ((this.xR - this.xG) * this.yW - (this.xW - this.xG) * this.yW + (this.xW - this.xR) * this.yG) / f1;
        float f9 = f8 * this.xB / this.yB;
        float f10 = f8 * ((1.0F - this.xB) / this.yB - 1.0F);
        float f11 = f3 + f6 + f9;
        float f12 = 1.0F;
        float f13 = f4 + f7 + f10;
        PdfArray localPdfArray3 = new PdfArray();
        localPdfArray3.add(new PdfNumber(f11));
        localPdfArray3.add(new PdfNumber(f12));
        localPdfArray3.add(new PdfNumber(f13));
        localObject = localPdfArray3;
        PdfArray localPdfArray4 = new PdfArray();
        localPdfArray4.add(new PdfNumber(f3));
        localPdfArray4.add(new PdfNumber(f2));
        localPdfArray4.add(new PdfNumber(f4));
        localPdfArray4.add(new PdfNumber(f6));
        localPdfArray4.add(new PdfNumber(f5));
        localPdfArray4.add(new PdfNumber(f7));
        localPdfArray4.add(new PdfNumber(f9));
        localPdfArray4.add(new PdfNumber(f8));
        localPdfArray4.add(new PdfNumber(f10));
        localPdfDictionary.put(PdfName.MATRIX, localPdfArray4);
      }
      localPdfDictionary.put(PdfName.WHITEPOINT, (PdfObject)localObject);
      localPdfArray1.add(localPdfDictionary);
    }
    return (PdfObject)localPdfArray1;
  }

  Image getImage()
    throws IOException
  {
    readPng();
    try
    {
      int i = 0;
      int j = 0;
      this.palShades = false;
      if (this.trans != null)
        for (k = 0; k < this.trans.length; k++)
        {
          m = this.trans[k] & 0xFF;
          if (m == 0)
          {
            i++;
            j = k;
          }
          if ((m == 0) || (m == 255))
            continue;
          this.palShades = true;
          break;
        }
      if ((this.colorType & 0x4) != 0)
        this.palShades = true;
      this.genBWMask = ((!this.palShades) && ((i > 1) || (this.transRedGray >= 0)));
      if ((!this.palShades) && (!this.genBWMask) && (i == 1))
        this.additional.put(PdfName.MASK, new PdfLiteral("[" + j + " " + j + "]"));
      int k = (this.interlaceMethod == 1) || (this.bitDepth == 16) || ((this.colorType & 0x4) != 0) || (this.palShades) || (this.genBWMask) ? 1 : 0;
      switch (this.colorType)
      {
      case 0:
        this.inputBands = 1;
        break;
      case 2:
        this.inputBands = 3;
        break;
      case 3:
        this.inputBands = 1;
        break;
      case 4:
        this.inputBands = 2;
        break;
      case 6:
        this.inputBands = 4;
      case 1:
      case 5:
      }
      if (k != 0)
        decodeIdat();
      int m = this.inputBands;
      if ((this.colorType & 0x4) != 0)
        m--;
      int n = this.bitDepth;
      if (n == 16)
        n = 8;
      Object localObject1;
      Object localObject2;
      if (this.image != null)
      {
        if (this.colorType == 3)
          localObject1 = new ImgRaw(this.width, this.height, m, n, this.image);
        else
          localObject1 = Image.getInstance(this.width, this.height, m, n, this.image);
      }
      else
      {
        localObject1 = new ImgRaw(this.width, this.height, m, n, this.idat.toByteArray());
        ((Image)localObject1).setDeflated(true);
        localObject2 = new PdfDictionary();
        ((PdfDictionary)localObject2).put(PdfName.BITSPERCOMPONENT, new PdfNumber(this.bitDepth));
        ((PdfDictionary)localObject2).put(PdfName.PREDICTOR, new PdfNumber(15));
        ((PdfDictionary)localObject2).put(PdfName.COLUMNS, new PdfNumber(this.width));
        ((PdfDictionary)localObject2).put(PdfName.COLORS, new PdfNumber((this.colorType == 3) || ((this.colorType & 0x2) == 0) ? 1 : 3));
        this.additional.put(PdfName.DECODEPARMS, (PdfObject)localObject2);
      }
      if (this.additional.get(PdfName.COLORSPACE) == null)
        this.additional.put(PdfName.COLORSPACE, getColorspace());
      if (this.intent != null)
        this.additional.put(PdfName.INTENT, this.intent);
      if (this.additional.size() > 0)
        ((Image)localObject1).setAdditional(this.additional);
      if (this.icc_profile != null)
        ((Image)localObject1).tagICC(this.icc_profile);
      if (this.palShades)
      {
        localObject2 = Image.getInstance(this.width, this.height, 1, 8, this.smask);
        ((Image)localObject2).makeMask();
        ((Image)localObject1).setImageMask((Image)localObject2);
      }
      if (this.genBWMask)
      {
        localObject2 = Image.getInstance(this.width, this.height, 1, 1, this.smask);
        ((Image)localObject2).makeMask();
        ((Image)localObject1).setImageMask((Image)localObject2);
      }
      ((Image)localObject1).setDpi(this.dpiX, this.dpiY);
      ((Image)localObject1).setXYRatio(this.XYRatio);
      ((Image)localObject1).setOriginalType(2);
      return localObject1;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  void decodeIdat()
  {
    int i = this.bitDepth;
    if (i == 16)
      i = 8;
    int j = -1;
    this.bytesPerPixel = (this.bitDepth == 16 ? 2 : 1);
    switch (this.colorType)
    {
    case 0:
      j = (i * this.width + 7) / 8 * this.height;
      break;
    case 2:
      j = this.width * 3 * this.height;
      this.bytesPerPixel *= 3;
      break;
    case 3:
      if (this.interlaceMethod == 1)
        j = (i * this.width + 7) / 8 * this.height;
      this.bytesPerPixel = 1;
      break;
    case 4:
      j = this.width * this.height;
      this.bytesPerPixel *= 2;
      break;
    case 6:
      j = this.width * 3 * this.height;
      this.bytesPerPixel *= 4;
    case 1:
    case 5:
    }
    if (j >= 0)
      this.image = new byte[j];
    if (this.palShades)
      this.smask = new byte[this.width * this.height];
    else if (this.genBWMask)
      this.smask = new byte[(this.width + 7) / 8 * this.height];
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this.idat.getBuf(), 0, this.idat.size());
    InflaterInputStream localInflaterInputStream = new InflaterInputStream(localByteArrayInputStream, new Inflater());
    this.dataStream = new DataInputStream(localInflaterInputStream);
    if (this.interlaceMethod != 1)
    {
      decodePass(0, 0, 1, 1, this.width, this.height);
    }
    else
    {
      decodePass(0, 0, 8, 8, (this.width + 7) / 8, (this.height + 7) / 8);
      decodePass(4, 0, 8, 8, (this.width + 3) / 8, (this.height + 7) / 8);
      decodePass(0, 4, 4, 8, (this.width + 3) / 4, (this.height + 3) / 8);
      decodePass(2, 0, 4, 4, (this.width + 1) / 4, (this.height + 3) / 4);
      decodePass(0, 2, 2, 4, (this.width + 1) / 2, (this.height + 1) / 4);
      decodePass(1, 0, 2, 2, this.width / 2, (this.height + 1) / 2);
      decodePass(0, 1, 1, 2, this.width, this.height / 2);
    }
  }

  void decodePass(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((paramInt5 == 0) || (paramInt6 == 0))
      return;
    int i = (this.inputBands * paramInt5 * this.bitDepth + 7) / 8;
    Object localObject1 = new byte[i];
    Object localObject2 = new byte[i];
    int j = 0;
    int k = paramInt2;
    while (j < paramInt6)
    {
      int m = 0;
      try
      {
        m = this.dataStream.read();
        this.dataStream.readFully(localObject1, 0, i);
      }
      catch (Exception localException)
      {
      }
      switch (m)
      {
      case 0:
        break;
      case 1:
        decodeSubFilter(localObject1, i, this.bytesPerPixel);
        break;
      case 2:
        decodeUpFilter(localObject1, localObject2, i);
        break;
      case 3:
        decodeAverageFilter(localObject1, localObject2, i, this.bytesPerPixel);
        break;
      case 4:
        decodePaethFilter(localObject1, localObject2, i, this.bytesPerPixel);
        break;
      default:
        throw new RuntimeException("PNG filter unknown.");
      }
      processPixels(localObject1, paramInt1, paramInt3, k, paramInt5);
      Object localObject3 = localObject2;
      localObject2 = localObject1;
      localObject1 = localObject3;
      j++;
      k += paramInt4;
    }
  }

  void processPixels(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int[] arrayOfInt1 = getPixel(paramArrayOfByte);
    int k = 0;
    switch (this.colorType)
    {
    case 0:
    case 3:
    case 4:
      k = 1;
      break;
    case 2:
    case 6:
      k = 3;
    case 1:
    case 5:
    }
    int j;
    int m;
    int i;
    if (this.image != null)
    {
      j = paramInt1;
      m = (k * this.width * (this.bitDepth == 16 ? 8 : this.bitDepth) + 7) / 8;
      for (i = 0; i < paramInt4; i++)
      {
        setPixel(this.image, arrayOfInt1, this.inputBands * i, k, j, paramInt3, this.bitDepth, m);
        j += paramInt2;
      }
    }
    int[] arrayOfInt2;
    int n;
    if (this.palShades)
    {
      if ((this.colorType & 0x4) != 0)
      {
        if (this.bitDepth == 16)
          for (m = 0; m < paramInt4; m++)
            arrayOfInt1[(m * this.inputBands + k)] >>>= 8;
        m = this.width;
        j = paramInt1;
        for (i = 0; i < paramInt4; i++)
        {
          setPixel(this.smask, arrayOfInt1, this.inputBands * i + k, 1, j, paramInt3, 8, m);
          j += paramInt2;
        }
      }
      m = this.width;
      arrayOfInt2 = new int[1];
      j = paramInt1;
      for (i = 0; i < paramInt4; i++)
      {
        n = arrayOfInt1[i];
        if (n < this.trans.length)
          arrayOfInt2[0] = this.trans[n];
        else
          arrayOfInt2[0] = 255;
        setPixel(this.smask, arrayOfInt2, 0, 1, j, paramInt3, 8, m);
        j += paramInt2;
      }
    }
    if (this.genBWMask)
      switch (this.colorType)
      {
      case 3:
        m = (this.width + 7) / 8;
        arrayOfInt2 = new int[1];
        j = paramInt1;
        i = 0;
      case 0:
      case 2:
        while (i < paramInt4)
        {
          n = arrayOfInt1[i];
          arrayOfInt2[0] = ((n < this.trans.length) && (this.trans[n] == 0) ? 1 : 0);
          setPixel(this.smask, arrayOfInt2, 0, 1, j, paramInt3, 1, m);
          j += paramInt2;
          i++;
          continue;
          m = (this.width + 7) / 8;
          arrayOfInt2 = new int[1];
          j = paramInt1;
          i = 0;
          while (i < paramInt4)
          {
            n = arrayOfInt1[i];
            arrayOfInt2[0] = (n == this.transRedGray ? 1 : 0);
            setPixel(this.smask, arrayOfInt2, 0, 1, j, paramInt3, 1, m);
            j += paramInt2;
            i++;
            continue;
            m = (this.width + 7) / 8;
            arrayOfInt2 = new int[1];
            j = paramInt1;
            for (i = 0; i < paramInt4; i++)
            {
              n = this.inputBands * i;
              arrayOfInt2[0] = ((arrayOfInt1[n] == this.transRedGray) && (arrayOfInt1[(n + 1)] == this.transGreen) && (arrayOfInt1[(n + 2)] == this.transBlue) ? 1 : 0);
              setPixel(this.smask, arrayOfInt2, 0, 1, j, paramInt3, 1, m);
              j += paramInt2;
            }
          }
        }
      case 1:
      }
  }

  static int getPixel(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt3 == 8)
    {
      i = paramInt4 * paramInt2 + paramInt1;
      return paramArrayOfByte[i] & 0xFF;
    }
    int i = paramInt4 * paramInt2 + paramInt1 / (8 / paramInt3);
    int j = paramArrayOfByte[i] >> 8 - paramInt3 * (paramInt1 % (8 / paramInt3)) - paramInt3;
    return j & (1 << paramInt3) - 1;
  }

  static void setPixel(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (paramInt5 == 8)
    {
      i = paramInt6 * paramInt4 + paramInt2 * paramInt3;
      for (j = 0; j < paramInt2; j++)
        paramArrayOfByte[(i + j)] = (byte)paramArrayOfInt[(j + paramInt1)];
    }
    if (paramInt5 == 16)
    {
      i = paramInt6 * paramInt4 + paramInt2 * paramInt3;
      for (j = 0; j < paramInt2; j++)
        paramArrayOfByte[(i + j)] = (byte)(paramArrayOfInt[(j + paramInt1)] >>> 8);
    }
    int i = paramInt6 * paramInt4 + paramInt3 / (8 / paramInt5);
    int j = paramArrayOfInt[paramInt1] << 8 - paramInt5 * (paramInt3 % (8 / paramInt5)) - paramInt5;
    int tmp141_139 = i;
    paramArrayOfByte[tmp141_139] = (byte)(paramArrayOfByte[tmp141_139] | j);
  }

  int[] getPixel(byte[] paramArrayOfByte)
  {
    switch (this.bitDepth)
    {
    case 8:
      arrayOfInt = new int[paramArrayOfByte.length];
      for (i = 0; i < arrayOfInt.length; i++)
        paramArrayOfByte[i] &= 255;
      return arrayOfInt;
    case 16:
      arrayOfInt = new int[paramArrayOfByte.length / 2];
      for (i = 0; i < arrayOfInt.length; i++)
        arrayOfInt[i] = (((paramArrayOfByte[(i * 2)] & 0xFF) << 8) + (paramArrayOfByte[(i * 2 + 1)] & 0xFF));
      return arrayOfInt;
    }
    int[] arrayOfInt = new int[paramArrayOfByte.length * 8 / this.bitDepth];
    int i = 0;
    int j = 8 / this.bitDepth;
    int k = (1 << this.bitDepth) - 1;
    for (int m = 0; m < paramArrayOfByte.length; m++)
      for (int n = j - 1; n >= 0; n--)
        arrayOfInt[(i++)] = (paramArrayOfByte[m] >>> this.bitDepth * n & k);
    return arrayOfInt;
  }

  private static void decodeSubFilter(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2; i < paramInt1; i++)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      j += (paramArrayOfByte[(i - paramInt2)] & 0xFF);
      paramArrayOfByte[i] = (byte)j;
    }
  }

  private static void decodeUpFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfByte1[i] & 0xFF;
      int k = paramArrayOfByte2[i] & 0xFF;
      paramArrayOfByte1[i] = (byte)(j + k);
    }
  }

  private static void decodeAverageFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
  {
    int i;
    int k;
    for (int m = 0; m < paramInt2; m++)
    {
      i = paramArrayOfByte1[m] & 0xFF;
      k = paramArrayOfByte2[m] & 0xFF;
      paramArrayOfByte1[m] = (byte)(i + k / 2);
    }
    for (m = paramInt2; m < paramInt1; m++)
    {
      i = paramArrayOfByte1[m] & 0xFF;
      int j = paramArrayOfByte1[(m - paramInt2)] & 0xFF;
      k = paramArrayOfByte2[m] & 0xFF;
      paramArrayOfByte1[m] = (byte)(i + (j + k) / 2);
    }
  }

  private static int paethPredictor(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 + paramInt2 - paramInt3;
    int j = Math.abs(i - paramInt1);
    int k = Math.abs(i - paramInt2);
    int m = Math.abs(i - paramInt3);
    if ((j <= k) && (j <= m))
      return paramInt1;
    if (k <= m)
      return paramInt2;
    return paramInt3;
  }

  private static void decodePaethFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
  {
    int i;
    int k;
    for (int n = 0; n < paramInt2; n++)
    {
      i = paramArrayOfByte1[n] & 0xFF;
      k = paramArrayOfByte2[n] & 0xFF;
      paramArrayOfByte1[n] = (byte)(i + k);
    }
    for (n = paramInt2; n < paramInt1; n++)
    {
      i = paramArrayOfByte1[n] & 0xFF;
      int j = paramArrayOfByte1[(n - paramInt2)] & 0xFF;
      k = paramArrayOfByte2[n] & 0xFF;
      int m = paramArrayOfByte2[(n - paramInt2)] & 0xFF;
      paramArrayOfByte1[n] = (byte)(i + paethPredictor(j, k, m));
    }
  }

  public static final int getInt(InputStream paramInputStream)
    throws IOException
  {
    return (paramInputStream.read() << 24) + (paramInputStream.read() << 16) + (paramInputStream.read() << 8) + paramInputStream.read();
  }

  public static final int getWord(InputStream paramInputStream)
    throws IOException
  {
    return (paramInputStream.read() << 8) + paramInputStream.read();
  }

  public static final String getString(InputStream paramInputStream)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < 4; i++)
      localStringBuffer.append((char)paramInputStream.read());
    return localStringBuffer.toString();
  }

  static class NewByteArrayOutputStream extends ByteArrayOutputStream
  {
    public byte[] getBuf()
    {
      return this.buf;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.PngImage
 * JD-Core Version:    0.6.0
 */