package com.lowagie.text;

import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.BmpImage;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import com.lowagie.text.pdf.codec.GifImage;
import com.lowagie.text.pdf.codec.JBIG2Image;
import com.lowagie.text.pdf.codec.PngImage;
import com.lowagie.text.pdf.codec.TiffImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class Image extends Rectangle
{
  public static final int DEFAULT = 0;
  public static final int RIGHT = 2;
  public static final int LEFT = 0;
  public static final int MIDDLE = 1;
  public static final int TEXTWRAP = 4;
  public static final int UNDERLYING = 8;
  public static final int AX = 0;
  public static final int AY = 1;
  public static final int BX = 2;
  public static final int BY = 3;
  public static final int CX = 4;
  public static final int CY = 5;
  public static final int DX = 6;
  public static final int DY = 7;
  public static final int ORIGINAL_NONE = 0;
  public static final int ORIGINAL_JPEG = 1;
  public static final int ORIGINAL_PNG = 2;
  public static final int ORIGINAL_GIF = 3;
  public static final int ORIGINAL_BMP = 4;
  public static final int ORIGINAL_TIFF = 5;
  public static final int ORIGINAL_WMF = 6;
  public static final int ORIGINAL_PS = 7;
  public static final int ORIGINAL_JPEG2000 = 8;
  public static final int ORIGINAL_JBIG2 = 9;
  protected int type;
  protected URL url;
  protected byte[] rawData;
  protected int bpc = 1;
  protected PdfTemplate[] template = new PdfTemplate[1];
  protected int alignment;
  protected String alt;
  protected float absoluteX = (0.0F / 0.0F);
  protected float absoluteY = (0.0F / 0.0F);
  protected float plainWidth;
  protected float plainHeight;
  protected float scaledWidth;
  protected float scaledHeight;
  protected int compressionLevel = -1;
  protected Long mySerialId = getSerialId();
  private PdfIndirectReference directReference;
  static long serialId = 0L;
  protected float rotationRadians;
  private float initialRotation;
  protected float indentationLeft = 0.0F;
  protected float indentationRight = 0.0F;
  protected float spacingBefore;
  protected float spacingAfter;
  private float widthPercentage = 100.0F;
  protected Annotation annotation = null;
  protected PdfOCG layer;
  protected boolean interpolation;
  protected int originalType = 0;
  protected byte[] originalData;
  protected boolean deflated = false;
  protected int dpiX = 0;
  protected int dpiY = 0;
  private float XYRatio = 0.0F;
  protected int colorspace = -1;
  protected boolean invert = false;
  protected ICC_Profile profile = null;
  private PdfDictionary additional = null;
  protected boolean mask = false;
  protected Image imageMask;
  private boolean smask;
  protected int[] transparency;

  public Image(URL paramURL)
  {
    super(0.0F, 0.0F);
    this.url = paramURL;
    this.alignment = 0;
    this.rotationRadians = 0.0F;
  }

  public static Image getInstance(URL paramURL)
    throws BadElementException, MalformedURLException, IOException
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramURL.openStream();
      int i = localInputStream.read();
      int j = localInputStream.read();
      int k = localInputStream.read();
      int m = localInputStream.read();
      int n = localInputStream.read();
      int i1 = localInputStream.read();
      int i2 = localInputStream.read();
      int i3 = localInputStream.read();
      localInputStream.close();
      localInputStream = null;
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if ((i == 71) && (j == 73) && (k == 70))
      {
        localObject1 = new GifImage(paramURL);
        localObject2 = ((GifImage)localObject1).getImage(1);
        localObject3 = localObject2;
        return localObject3;
      }
      if ((i == 255) && (j == 216))
      {
        localObject1 = new Jpeg(paramURL);
        return localObject1;
      }
      if ((i == 0) && (j == 0) && (k == 0) && (m == 12))
      {
        localObject1 = new Jpeg2000(paramURL);
        return localObject1;
      }
      if ((i == 255) && (j == 79) && (k == 255) && (m == 81))
      {
        localObject1 = new Jpeg2000(paramURL);
        return localObject1;
      }
      if ((i == PngImage.PNGID[0]) && (j == PngImage.PNGID[1]) && (k == PngImage.PNGID[2]) && (m == PngImage.PNGID[3]))
      {
        localObject1 = PngImage.getImage(paramURL);
        return localObject1;
      }
      if ((i == 215) && (j == 205))
      {
        localObject1 = new ImgWMF(paramURL);
        return localObject1;
      }
      if ((i == 66) && (j == 77))
      {
        localObject1 = BmpImage.getImage(paramURL);
        return localObject1;
      }
      if (((i == 77) && (j == 77) && (k == 0) && (m == 42)) || ((i == 73) && (j == 73) && (k == 42) && (m == 0)))
      {
        localObject1 = null;
        try
        {
          if (paramURL.getProtocol().equals("file"))
          {
            localObject2 = paramURL.getFile();
            localObject2 = Utilities.unEscapeURL((String)localObject2);
            localObject1 = new RandomAccessFileOrArray((String)localObject2);
          }
          else
          {
            localObject1 = new RandomAccessFileOrArray(paramURL);
          }
          localObject2 = TiffImage.getTiffImage((RandomAccessFileOrArray)localObject1, 1);
          ((Image)localObject2).url = paramURL;
          localObject3 = localObject2;
          if (localObject1 != null)
            ((RandomAccessFileOrArray)localObject1).close();
          if (localInputStream != null)
            localInputStream.close();
          return localObject3;
        }
        finally
        {
          if (localObject1 != null)
            ((RandomAccessFileOrArray)localObject1).close();
        }
      }
      if ((i == 151) && (j == 74) && (k == 66) && (m == 50) && (n == 13) && (i1 == 10) && (i2 == 26) && (i3 == 10))
      {
        localObject1 = null;
        try
        {
          if (paramURL.getProtocol().equals("file"))
          {
            localObject2 = paramURL.getFile();
            localObject2 = Utilities.unEscapeURL((String)localObject2);
            localObject1 = new RandomAccessFileOrArray((String)localObject2);
          }
          else
          {
            localObject1 = new RandomAccessFileOrArray(paramURL);
          }
          localObject2 = JBIG2Image.getJbig2Image((RandomAccessFileOrArray)localObject1, 1);
          ((Image)localObject2).url = paramURL;
          localObject3 = localObject2;
          if (localObject1 != null)
            ((RandomAccessFileOrArray)localObject1).close();
          if (localInputStream != null)
            localInputStream.close();
          return localObject3;
        }
        finally
        {
          if (localObject1 != null)
            ((RandomAccessFileOrArray)localObject1).close();
        }
      }
      throw new IOException(paramURL.toString() + " is not a recognized imageformat.");
    }
    finally
    {
      if (localInputStream != null)
        localInputStream.close();
    }
    throw localObject6;
  }

  public static Image getInstance(String paramString)
    throws BadElementException, MalformedURLException, IOException
  {
    return getInstance(Utilities.toURL(paramString));
  }

  public static Image getInstance(byte[] paramArrayOfByte)
    throws BadElementException, MalformedURLException, IOException
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    try
    {
      localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      int i = localByteArrayInputStream.read();
      int j = localByteArrayInputStream.read();
      int k = localByteArrayInputStream.read();
      int m = localByteArrayInputStream.read();
      localByteArrayInputStream.close();
      localByteArrayInputStream = null;
      Object localObject1;
      Image localImage1;
      if ((i == 71) && (j == 73) && (k == 70))
      {
        localObject1 = new GifImage(paramArrayOfByte);
        localImage1 = ((GifImage)localObject1).getImage(1);
        return localImage1;
      }
      if ((i == 255) && (j == 216))
      {
        localObject1 = new Jpeg(paramArrayOfByte);
        return localObject1;
      }
      if ((i == 0) && (j == 0) && (k == 0) && (m == 12))
      {
        localObject1 = new Jpeg2000(paramArrayOfByte);
        return localObject1;
      }
      if ((i == 255) && (j == 79) && (k == 255) && (m == 81))
      {
        localObject1 = new Jpeg2000(paramArrayOfByte);
        return localObject1;
      }
      if ((i == PngImage.PNGID[0]) && (j == PngImage.PNGID[1]) && (k == PngImage.PNGID[2]) && (m == PngImage.PNGID[3]))
      {
        localObject1 = PngImage.getImage(paramArrayOfByte);
        return localObject1;
      }
      if ((i == 215) && (j == 205))
      {
        localObject1 = new ImgWMF(paramArrayOfByte);
        return localObject1;
      }
      if ((i == 66) && (j == 77))
      {
        localObject1 = BmpImage.getImage(paramArrayOfByte);
        return localObject1;
      }
      if (((i == 77) && (j == 77) && (k == 0) && (m == 42)) || ((i == 73) && (j == 73) && (k == 42) && (m == 0)))
      {
        localObject1 = null;
        try
        {
          localObject1 = new RandomAccessFileOrArray(paramArrayOfByte);
          localImage1 = TiffImage.getTiffImage((RandomAccessFileOrArray)localObject1, 1);
          if (localImage1.getOriginalData() == null)
            localImage1.setOriginalData(paramArrayOfByte);
          Image localImage2 = localImage1;
          if (localObject1 != null)
            ((RandomAccessFileOrArray)localObject1).close();
          if (localByteArrayInputStream != null)
            localByteArrayInputStream.close();
          return localImage2;
        }
        finally
        {
          if (localObject1 != null)
            ((RandomAccessFileOrArray)localObject1).close();
        }
      }
      if ((i == 151) && (j == 74) && (k == 66) && (m == 50))
      {
        localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
        localByteArrayInputStream.skip(4L);
        int n = localByteArrayInputStream.read();
        int i1 = localByteArrayInputStream.read();
        int i2 = localByteArrayInputStream.read();
        int i3 = localByteArrayInputStream.read();
        if ((n == 13) && (i1 == 10) && (i2 == 26) && (i3 == 10))
        {
          int i4 = localByteArrayInputStream.read();
          int i5 = -1;
          if ((i4 & 0x2) == 2)
            i5 = localByteArrayInputStream.read() << 24 | localByteArrayInputStream.read() << 16 | localByteArrayInputStream.read() << 8 | localByteArrayInputStream.read();
          localByteArrayInputStream.close();
          RandomAccessFileOrArray localRandomAccessFileOrArray = null;
          try
          {
            localRandomAccessFileOrArray = new RandomAccessFileOrArray(paramArrayOfByte);
            Image localImage3 = JBIG2Image.getJbig2Image(localRandomAccessFileOrArray, 1);
            if (localImage3.getOriginalData() == null)
              localImage3.setOriginalData(paramArrayOfByte);
            Image localImage4 = localImage3;
            if (localRandomAccessFileOrArray != null)
              localRandomAccessFileOrArray.close();
            return localImage4;
          }
          finally
          {
            if (localRandomAccessFileOrArray != null)
              localRandomAccessFileOrArray.close();
          }
        }
      }
      throw new IOException("The byte array is not a recognized imageformat.");
    }
    finally
    {
      if (localByteArrayInputStream != null)
        localByteArrayInputStream.close();
    }
    throw localObject4;
  }

  public static Image getInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws BadElementException
  {
    return getInstance(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, null);
  }

  public static Image getInstance(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    ImgJBIG2 localImgJBIG2 = new ImgJBIG2(paramInt1, paramInt2, paramArrayOfByte1, paramArrayOfByte2);
    return localImgJBIG2;
  }

  public static Image getInstance(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
    throws BadElementException
  {
    return getInstance(paramInt1, paramInt2, paramBoolean, paramInt3, paramInt4, paramArrayOfByte, null);
  }

  public static Image getInstance(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int[] paramArrayOfInt)
    throws BadElementException
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length != 2))
      throw new BadElementException("Transparency length must be equal to 2 with CCITT images");
    ImgCCITT localImgCCITT = new ImgCCITT(paramInt1, paramInt2, paramBoolean, paramInt3, paramInt4, paramArrayOfByte);
    localImgCCITT.transparency = paramArrayOfInt;
    return localImgCCITT;
  }

  public static Image getInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int[] paramArrayOfInt)
    throws BadElementException
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length != paramInt3 * 2))
      throw new BadElementException("Transparency length must be equal to (componentes * 2)");
    if ((paramInt3 == 1) && (paramInt4 == 1))
    {
      localObject = CCITTG4Encoder.compress(paramArrayOfByte, paramInt1, paramInt2);
      return getInstance(paramInt1, paramInt2, false, 256, 1, localObject, paramArrayOfInt);
    }
    Object localObject = new ImgRaw(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
    ((Image)localObject).transparency = paramArrayOfInt;
    return (Image)localObject;
  }

  public static Image getInstance(PdfTemplate paramPdfTemplate)
    throws BadElementException
  {
    return new ImgTemplate(paramPdfTemplate);
  }

  public static Image getInstance(java.awt.Image paramImage, Color paramColor, boolean paramBoolean)
    throws BadElementException, IOException
  {
    if ((paramImage instanceof BufferedImage))
    {
      localObject = (BufferedImage)paramImage;
      if (((BufferedImage)localObject).getType() == 12)
        paramBoolean = true;
    }
    Object localObject = new PixelGrabber(paramImage, 0, 0, -1, -1, true);
    try
    {
      ((PixelGrabber)localObject).grabPixels();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new IOException("java.awt.Image Interrupted waiting for pixels!");
    }
    if ((((PixelGrabber)localObject).getStatus() & 0x80) != 0)
      throw new IOException("java.awt.Image fetch aborted or errored");
    int i = ((PixelGrabber)localObject).getWidth();
    int j = ((PixelGrabber)localObject).getHeight();
    int[] arrayOfInt1 = (int[])((PixelGrabber)localObject).getPixels();
    if (paramBoolean)
    {
      int k = i / 8 + ((i & 0x7) != 0 ? 1 : 0);
      arrayOfByte2 = new byte[k * j];
      m = 0;
      n = j * i;
      i1 = 1;
      if (paramColor != null)
        i1 = paramColor.getRed() + paramColor.getGreen() + paramColor.getBlue() < 384 ? 0 : 1;
      int[] arrayOfInt2 = null;
      i3 = 128;
      int i4 = 0;
      i5 = 0;
      if (paramColor != null)
        for (i6 = 0; i6 < n; i6++)
        {
          i7 = arrayOfInt1[i6] >> 24 & 0xFF;
          if (i7 < 250)
          {
            if (i1 == 1)
              i5 |= i3;
          }
          else if ((arrayOfInt1[i6] & 0x888) != 0)
            i5 |= i3;
          i3 >>= 1;
          if ((i3 == 0) || (i4 + 1 >= i))
          {
            arrayOfByte2[(m++)] = (byte)i5;
            i3 = 128;
            i5 = 0;
          }
          i4++;
          if (i4 < i)
            continue;
          i4 = 0;
        }
      for (i6 = 0; i6 < n; i6++)
      {
        if (arrayOfInt2 == null)
        {
          i7 = arrayOfInt1[i6] >> 24 & 0xFF;
          if (i7 == 0)
          {
            arrayOfInt2 = new int[2];
            int tmp384_383 = ((arrayOfInt1[i6] & 0x888) != 0 ? 255 : 0);
            arrayOfInt2[1] = tmp384_383;
            arrayOfInt2[0] = tmp384_383;
          }
        }
        if ((arrayOfInt1[i6] & 0x888) != 0)
          i5 |= i3;
        i3 >>= 1;
        if ((i3 == 0) || (i4 + 1 >= i))
        {
          arrayOfByte2[(m++)] = (byte)i5;
          i3 = 128;
          i5 = 0;
        }
        i4++;
        if (i4 < i)
          continue;
        i4 = 0;
      }
      return getInstance(i, j, 1, 1, arrayOfByte2, arrayOfInt2);
    }
    byte[] arrayOfByte1 = new byte[i * j * 3];
    byte[] arrayOfByte2 = null;
    int m = 0;
    int n = j * i;
    int i1 = 255;
    int i2 = 255;
    int i3 = 255;
    if (paramColor != null)
    {
      i1 = paramColor.getRed();
      i2 = paramColor.getGreen();
      i3 = paramColor.getBlue();
    }
    int[] arrayOfInt3 = null;
    if (paramColor != null)
      for (i5 = 0; i5 < n; i5++)
      {
        i6 = arrayOfInt1[i5] >> 24 & 0xFF;
        if (i6 < 250)
        {
          arrayOfByte1[(m++)] = (byte)i1;
          arrayOfByte1[(m++)] = (byte)i2;
          arrayOfByte1[(m++)] = (byte)i3;
        }
        else
        {
          arrayOfByte1[(m++)] = (byte)(arrayOfInt1[i5] >> 16 & 0xFF);
          arrayOfByte1[(m++)] = (byte)(arrayOfInt1[i5] >> 8 & 0xFF);
          arrayOfByte1[(m++)] = (byte)(arrayOfInt1[i5] & 0xFF);
        }
      }
    int i5 = 0;
    arrayOfByte2 = new byte[i * j];
    int i6 = 0;
    for (int i7 = 0; i7 < n; i7++)
    {
      int i8 = arrayOfByte2[i7] = (byte)(arrayOfInt1[i7] >> 24 & 0xFF);
      if (i6 == 0)
        if ((i8 != 0) && (i8 != -1))
          i6 = 1;
        else if (arrayOfInt3 == null)
        {
          if (i8 == 0)
          {
            i5 = arrayOfInt1[i7] & 0xFFFFFF;
            arrayOfInt3 = new int[6];
            int tmp789_788 = (i5 >> 16 & 0xFF);
            arrayOfInt3[1] = tmp789_788;
            arrayOfInt3[0] = tmp789_788;
            int tmp807_806 = (i5 >> 8 & 0xFF);
            arrayOfInt3[3] = tmp807_806;
            arrayOfInt3[2] = tmp807_806;
            int tmp822_821 = (i5 & 0xFF);
            arrayOfInt3[5] = tmp822_821;
            arrayOfInt3[4] = tmp822_821;
          }
        }
        else if ((arrayOfInt1[i7] & 0xFFFFFF) != i5)
          i6 = 1;
      arrayOfByte1[(m++)] = (byte)(arrayOfInt1[i7] >> 16 & 0xFF);
      arrayOfByte1[(m++)] = (byte)(arrayOfInt1[i7] >> 8 & 0xFF);
      arrayOfByte1[(m++)] = (byte)(arrayOfInt1[i7] & 0xFF);
    }
    if (i6 != 0)
      arrayOfInt3 = null;
    else
      arrayOfByte2 = null;
    Image localImage1 = getInstance(i, j, 3, 8, arrayOfByte1, arrayOfInt3);
    if (arrayOfByte2 != null)
    {
      Image localImage2 = getInstance(i, j, 1, 8, arrayOfByte2);
      try
      {
        localImage2.makeMask();
        localImage1.setImageMask(localImage2);
      }
      catch (DocumentException localDocumentException)
      {
        throw new ExceptionConverter(localDocumentException);
      }
    }
    return (Image)localImage1;
  }

  public static Image getInstance(java.awt.Image paramImage, Color paramColor)
    throws BadElementException, IOException
  {
    return getInstance(paramImage, paramColor, false);
  }

  public static Image getInstance(PdfWriter paramPdfWriter, java.awt.Image paramImage, float paramFloat)
    throws BadElementException, IOException
  {
    return getInstance(new PdfContentByte(paramPdfWriter), paramImage, paramFloat);
  }

  public static Image getInstance(PdfContentByte paramPdfContentByte, java.awt.Image paramImage, float paramFloat)
    throws BadElementException, IOException
  {
    PixelGrabber localPixelGrabber = new PixelGrabber(paramImage, 0, 0, -1, -1, true);
    try
    {
      localPixelGrabber.grabPixels();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new IOException("java.awt.Image Interrupted waiting for pixels!");
    }
    if ((localPixelGrabber.getStatus() & 0x80) != 0)
      throw new IOException("java.awt.Image fetch aborted or errored");
    int i = localPixelGrabber.getWidth();
    int j = localPixelGrabber.getHeight();
    PdfTemplate localPdfTemplate = paramPdfContentByte.createTemplate(i, j);
    Graphics2D localGraphics2D = localPdfTemplate.createGraphics(i, j, true, paramFloat);
    localGraphics2D.drawImage(paramImage, 0, 0, null);
    localGraphics2D.dispose();
    return getInstance(localPdfTemplate);
  }

  public PdfIndirectReference getDirectReference()
  {
    return this.directReference;
  }

  public void setDirectReference(PdfIndirectReference paramPdfIndirectReference)
  {
    this.directReference = paramPdfIndirectReference;
  }

  public static Image getInstance(PRIndirectReference paramPRIndirectReference)
    throws BadElementException
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPRIndirectReference);
    int i = ((PdfNumber)PdfReader.getPdfObjectRelease(localPdfDictionary.get(PdfName.WIDTH))).intValue();
    int j = ((PdfNumber)PdfReader.getPdfObjectRelease(localPdfDictionary.get(PdfName.HEIGHT))).intValue();
    Image localImage = null;
    PdfObject localPdfObject = localPdfDictionary.get(PdfName.SMASK);
    if ((localPdfObject != null) && (localPdfObject.isIndirect()))
    {
      localImage = getInstance((PRIndirectReference)localPdfObject);
    }
    else
    {
      localPdfObject = localPdfDictionary.get(PdfName.MASK);
      if ((localPdfObject != null) && (localPdfObject.isIndirect()))
      {
        localObject = PdfReader.getPdfObjectRelease(localPdfObject);
        if ((localObject instanceof PdfDictionary))
          localImage = getInstance((PRIndirectReference)localPdfObject);
      }
    }
    Object localObject = new ImgRaw(i, j, 1, 1, null);
    ((Image)localObject).imageMask = localImage;
    ((Image)localObject).directReference = paramPRIndirectReference;
    return (Image)localObject;
  }

  protected Image(Image paramImage)
  {
    super(paramImage);
    this.type = paramImage.type;
    this.url = paramImage.url;
    this.rawData = paramImage.rawData;
    this.bpc = paramImage.bpc;
    this.template = paramImage.template;
    this.alignment = paramImage.alignment;
    this.alt = paramImage.alt;
    this.absoluteX = paramImage.absoluteX;
    this.absoluteY = paramImage.absoluteY;
    this.plainWidth = paramImage.plainWidth;
    this.plainHeight = paramImage.plainHeight;
    this.scaledWidth = paramImage.scaledWidth;
    this.scaledHeight = paramImage.scaledHeight;
    this.mySerialId = paramImage.mySerialId;
    this.directReference = paramImage.directReference;
    this.rotationRadians = paramImage.rotationRadians;
    this.initialRotation = paramImage.initialRotation;
    this.indentationLeft = paramImage.indentationLeft;
    this.indentationRight = paramImage.indentationRight;
    this.spacingBefore = paramImage.spacingBefore;
    this.spacingAfter = paramImage.spacingAfter;
    this.widthPercentage = paramImage.widthPercentage;
    this.annotation = paramImage.annotation;
    this.layer = paramImage.layer;
    this.interpolation = paramImage.interpolation;
    this.originalType = paramImage.originalType;
    this.originalData = paramImage.originalData;
    this.deflated = paramImage.deflated;
    this.dpiX = paramImage.dpiX;
    this.dpiY = paramImage.dpiY;
    this.XYRatio = paramImage.XYRatio;
    this.colorspace = paramImage.colorspace;
    this.invert = paramImage.invert;
    this.profile = paramImage.profile;
    this.additional = paramImage.additional;
    this.mask = paramImage.mask;
    this.imageMask = paramImage.imageMask;
    this.smask = paramImage.smask;
    this.transparency = paramImage.transparency;
  }

  public static Image getInstance(Image paramImage)
  {
    if (paramImage == null)
      return null;
    try
    {
      Class localClass = paramImage.getClass();
      Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { Image.class });
      return (Image)localConstructor.newInstance(new Object[] { paramImage });
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public int type()
  {
    return this.type;
  }

  public boolean isNestable()
  {
    return true;
  }

  public boolean isJpeg()
  {
    return this.type == 32;
  }

  public boolean isImgRaw()
  {
    return this.type == 34;
  }

  public boolean isImgTemplate()
  {
    return this.type == 35;
  }

  public URL getUrl()
  {
    return this.url;
  }

  public void setUrl(URL paramURL)
  {
    this.url = paramURL;
  }

  public byte[] getRawData()
  {
    return this.rawData;
  }

  public int getBpc()
  {
    return this.bpc;
  }

  public PdfTemplate getTemplateData()
  {
    return this.template[0];
  }

  public void setTemplateData(PdfTemplate paramPdfTemplate)
  {
    this.template[0] = paramPdfTemplate;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public String getAlt()
  {
    return this.alt;
  }

  public void setAlt(String paramString)
  {
    this.alt = paramString;
  }

  public void setAbsolutePosition(float paramFloat1, float paramFloat2)
  {
    this.absoluteX = paramFloat1;
    this.absoluteY = paramFloat2;
  }

  public boolean hasAbsoluteX()
  {
    return !Float.isNaN(this.absoluteX);
  }

  public float getAbsoluteX()
  {
    return this.absoluteX;
  }

  public boolean hasAbsoluteY()
  {
    return !Float.isNaN(this.absoluteY);
  }

  public float getAbsoluteY()
  {
    return this.absoluteY;
  }

  public float getScaledWidth()
  {
    return this.scaledWidth;
  }

  public float getScaledHeight()
  {
    return this.scaledHeight;
  }

  public float getPlainWidth()
  {
    return this.plainWidth;
  }

  public float getPlainHeight()
  {
    return this.plainHeight;
  }

  public void scaleAbsolute(float paramFloat1, float paramFloat2)
  {
    this.plainWidth = paramFloat1;
    this.plainHeight = paramFloat2;
    float[] arrayOfFloat = matrix();
    this.scaledWidth = (arrayOfFloat[6] - arrayOfFloat[4]);
    this.scaledHeight = (arrayOfFloat[7] - arrayOfFloat[5]);
    setWidthPercentage(0.0F);
  }

  public void scaleAbsoluteWidth(float paramFloat)
  {
    this.plainWidth = paramFloat;
    float[] arrayOfFloat = matrix();
    this.scaledWidth = (arrayOfFloat[6] - arrayOfFloat[4]);
    this.scaledHeight = (arrayOfFloat[7] - arrayOfFloat[5]);
    setWidthPercentage(0.0F);
  }

  public void scaleAbsoluteHeight(float paramFloat)
  {
    this.plainHeight = paramFloat;
    float[] arrayOfFloat = matrix();
    this.scaledWidth = (arrayOfFloat[6] - arrayOfFloat[4]);
    this.scaledHeight = (arrayOfFloat[7] - arrayOfFloat[5]);
    setWidthPercentage(0.0F);
  }

  public void scalePercent(float paramFloat)
  {
    scalePercent(paramFloat, paramFloat);
  }

  public void scalePercent(float paramFloat1, float paramFloat2)
  {
    this.plainWidth = (getWidth() * paramFloat1 / 100.0F);
    this.plainHeight = (getHeight() * paramFloat2 / 100.0F);
    float[] arrayOfFloat = matrix();
    this.scaledWidth = (arrayOfFloat[6] - arrayOfFloat[4]);
    this.scaledHeight = (arrayOfFloat[7] - arrayOfFloat[5]);
    setWidthPercentage(0.0F);
  }

  public void scaleToFit(float paramFloat1, float paramFloat2)
  {
    scalePercent(100.0F);
    float f1 = paramFloat1 * 100.0F / getScaledWidth();
    float f2 = paramFloat2 * 100.0F / getScaledHeight();
    scalePercent(f1 < f2 ? f1 : f2);
    setWidthPercentage(0.0F);
  }

  public float[] matrix()
  {
    float[] arrayOfFloat = new float[8];
    float f1 = (float)Math.cos(this.rotationRadians);
    float f2 = (float)Math.sin(this.rotationRadians);
    arrayOfFloat[0] = (this.plainWidth * f1);
    arrayOfFloat[1] = (this.plainWidth * f2);
    arrayOfFloat[2] = (-this.plainHeight * f2);
    arrayOfFloat[3] = (this.plainHeight * f1);
    if (this.rotationRadians < 1.570796326794897D)
    {
      arrayOfFloat[4] = arrayOfFloat[2];
      arrayOfFloat[5] = 0.0F;
      arrayOfFloat[6] = arrayOfFloat[0];
      arrayOfFloat[7] = (arrayOfFloat[1] + arrayOfFloat[3]);
    }
    else if (this.rotationRadians < 3.141592653589793D)
    {
      arrayOfFloat[4] = (arrayOfFloat[0] + arrayOfFloat[2]);
      arrayOfFloat[5] = arrayOfFloat[3];
      arrayOfFloat[6] = 0.0F;
      arrayOfFloat[7] = arrayOfFloat[1];
    }
    else if (this.rotationRadians < 4.71238898038469D)
    {
      arrayOfFloat[4] = arrayOfFloat[0];
      arrayOfFloat[5] = (arrayOfFloat[1] + arrayOfFloat[3]);
      arrayOfFloat[6] = arrayOfFloat[2];
      arrayOfFloat[7] = 0.0F;
    }
    else
    {
      arrayOfFloat[4] = 0.0F;
      arrayOfFloat[5] = arrayOfFloat[1];
      arrayOfFloat[6] = (arrayOfFloat[0] + arrayOfFloat[2]);
      arrayOfFloat[7] = arrayOfFloat[3];
    }
    return arrayOfFloat;
  }

  protected static synchronized Long getSerialId()
  {
    serialId += 1L;
    return new Long(serialId);
  }

  public Long getMySerialId()
  {
    return this.mySerialId;
  }

  public float getImageRotation()
  {
    double d = 6.283185307179586D;
    float f = (float)((this.rotationRadians - this.initialRotation) % d);
    if (f < 0.0F)
      f = (float)(f + d);
    return f;
  }

  public void setRotation(float paramFloat)
  {
    double d = 6.283185307179586D;
    this.rotationRadians = (float)((paramFloat + this.initialRotation) % d);
    if (this.rotationRadians < 0.0F)
      this.rotationRadians = (float)(this.rotationRadians + d);
    float[] arrayOfFloat = matrix();
    this.scaledWidth = (arrayOfFloat[6] - arrayOfFloat[4]);
    this.scaledHeight = (arrayOfFloat[7] - arrayOfFloat[5]);
  }

  public void setRotationDegrees(float paramFloat)
  {
    double d = 3.141592653589793D;
    setRotation(paramFloat / 180.0F * (float)d);
  }

  public float getInitialRotation()
  {
    return this.initialRotation;
  }

  public void setInitialRotation(float paramFloat)
  {
    float f = this.rotationRadians - this.initialRotation;
    this.initialRotation = paramFloat;
    setRotation(f);
  }

  public float getIndentationLeft()
  {
    return this.indentationLeft;
  }

  public void setIndentationLeft(float paramFloat)
  {
    this.indentationLeft = paramFloat;
  }

  public float getIndentationRight()
  {
    return this.indentationRight;
  }

  public void setIndentationRight(float paramFloat)
  {
    this.indentationRight = paramFloat;
  }

  public float getSpacingBefore()
  {
    return this.spacingBefore;
  }

  public void setSpacingBefore(float paramFloat)
  {
    this.spacingBefore = paramFloat;
  }

  public float getSpacingAfter()
  {
    return this.spacingAfter;
  }

  public void setSpacingAfter(float paramFloat)
  {
    this.spacingAfter = paramFloat;
  }

  public float getWidthPercentage()
  {
    return this.widthPercentage;
  }

  public void setWidthPercentage(float paramFloat)
  {
    this.widthPercentage = paramFloat;
  }

  public void setAnnotation(Annotation paramAnnotation)
  {
    this.annotation = paramAnnotation;
  }

  public Annotation getAnnotation()
  {
    return this.annotation;
  }

  public PdfOCG getLayer()
  {
    return this.layer;
  }

  public void setLayer(PdfOCG paramPdfOCG)
  {
    this.layer = paramPdfOCG;
  }

  public boolean isInterpolation()
  {
    return this.interpolation;
  }

  public void setInterpolation(boolean paramBoolean)
  {
    this.interpolation = paramBoolean;
  }

  public int getOriginalType()
  {
    return this.originalType;
  }

  public void setOriginalType(int paramInt)
  {
    this.originalType = paramInt;
  }

  public byte[] getOriginalData()
  {
    return this.originalData;
  }

  public void setOriginalData(byte[] paramArrayOfByte)
  {
    this.originalData = paramArrayOfByte;
  }

  public boolean isDeflated()
  {
    return this.deflated;
  }

  public void setDeflated(boolean paramBoolean)
  {
    this.deflated = paramBoolean;
  }

  public int getDpiX()
  {
    return this.dpiX;
  }

  public int getDpiY()
  {
    return this.dpiY;
  }

  public void setDpi(int paramInt1, int paramInt2)
  {
    this.dpiX = paramInt1;
    this.dpiY = paramInt2;
  }

  public float getXYRatio()
  {
    return this.XYRatio;
  }

  public void setXYRatio(float paramFloat)
  {
    this.XYRatio = paramFloat;
  }

  public int getColorspace()
  {
    return this.colorspace;
  }

  public boolean isInverted()
  {
    return this.invert;
  }

  public void setInverted(boolean paramBoolean)
  {
    this.invert = paramBoolean;
  }

  public void tagICC(ICC_Profile paramICC_Profile)
  {
    this.profile = paramICC_Profile;
  }

  public boolean hasICCProfile()
  {
    return this.profile != null;
  }

  public ICC_Profile getICCProfile()
  {
    return this.profile;
  }

  public PdfDictionary getAdditional()
  {
    return this.additional;
  }

  public void setAdditional(PdfDictionary paramPdfDictionary)
  {
    this.additional = paramPdfDictionary;
  }

  public void simplifyColorspace()
  {
    if (this.additional == null)
      return;
    PdfArray localPdfArray1 = this.additional.getAsArray(PdfName.COLORSPACE);
    if (localPdfArray1 == null)
      return;
    PdfObject localPdfObject = simplifyColorspace(localPdfArray1);
    Object localObject;
    if (localPdfObject.isName())
    {
      localObject = localPdfObject;
    }
    else
    {
      localObject = localPdfArray1;
      PdfName localPdfName = localPdfArray1.getAsName(0);
      if ((PdfName.INDEXED.equals(localPdfName)) && (localPdfArray1.size() >= 2))
      {
        PdfArray localPdfArray2 = localPdfArray1.getAsArray(1);
        if (localPdfArray2 != null)
          localPdfArray1.set(1, simplifyColorspace(localPdfArray2));
      }
    }
    this.additional.put(PdfName.COLORSPACE, (PdfObject)localObject);
  }

  private PdfObject simplifyColorspace(PdfArray paramPdfArray)
  {
    if (paramPdfArray == null)
      return paramPdfArray;
    PdfName localPdfName = paramPdfArray.getAsName(0);
    if (PdfName.CALGRAY.equals(localPdfName))
      return PdfName.DEVICEGRAY;
    if (PdfName.CALRGB.equals(localPdfName))
      return PdfName.DEVICERGB;
    return paramPdfArray;
  }

  public boolean isMask()
  {
    return this.mask;
  }

  public void makeMask()
    throws DocumentException
  {
    if (!isMaskCandidate())
      throw new DocumentException("This image can not be an image mask.");
    this.mask = true;
  }

  public boolean isMaskCandidate()
  {
    if ((this.type == 34) && (this.bpc > 255))
      return true;
    return this.colorspace == 1;
  }

  public Image getImageMask()
  {
    return this.imageMask;
  }

  public void setImageMask(Image paramImage)
    throws DocumentException
  {
    if (this.mask)
      throw new DocumentException("An image mask cannot contain another image mask.");
    if (!paramImage.mask)
      throw new DocumentException("The image mask is not a mask. Did you do makeMask()?");
    this.imageMask = paramImage;
    this.smask = ((paramImage.bpc > 1) && (paramImage.bpc <= 8));
  }

  public boolean isSmask()
  {
    return this.smask;
  }

  public void setSmask(boolean paramBoolean)
  {
    this.smask = paramBoolean;
  }

  public int[] getTransparency()
  {
    return this.transparency;
  }

  public void setTransparency(int[] paramArrayOfInt)
  {
    this.transparency = paramArrayOfInt;
  }

  public int getCompressionLevel()
  {
    return this.compressionLevel;
  }

  public void setCompressionLevel(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 9))
      this.compressionLevel = -1;
    else
      this.compressionLevel = paramInt;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.Image
 * JD-Core Version:    0.6.0
 */