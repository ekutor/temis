package com.lowagie.text.pdf.codec;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

public class TiffImage
{
  public static int getNumberOfPages(RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    try
    {
      return TIFFDirectory.getNumDirectories(paramRandomAccessFileOrArray);
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  static int getDpi(TIFFField paramTIFFField, int paramInt)
  {
    if (paramTIFFField == null)
      return 0;
    long[] arrayOfLong = paramTIFFField.getAsRational(0);
    float f = (float)arrayOfLong[0] / (float)arrayOfLong[1];
    int i = 0;
    switch (paramInt)
    {
    case 1:
    case 2:
      i = (int)(f + 0.5D);
      break;
    case 3:
      i = (int)(f * 2.54D + 0.5D);
    }
    return i;
  }

  public static Image getTiffImage(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt)
  {
    return getTiffImage(paramRandomAccessFileOrArray, paramInt, false);
  }

  public static Image getTiffImage(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt, boolean paramBoolean)
  {
    if (paramInt < 1)
      throw new IllegalArgumentException("The page number must be >= 1.");
    try
    {
      TIFFDirectory localTIFFDirectory = new TIFFDirectory(paramRandomAccessFileOrArray, paramInt - 1);
      if (localTIFFDirectory.isTagPresent(322))
        throw new IllegalArgumentException("Tiles are not supported.");
      int i = (int)localTIFFDirectory.getFieldAsLong(259);
      switch (i)
      {
      case 2:
      case 3:
      case 4:
      case 32771:
        break;
      default:
        return getTiffImageColor(localTIFFDirectory, paramRandomAccessFileOrArray);
      }
      float f1 = 0.0F;
      if (localTIFFDirectory.isTagPresent(274))
      {
        int j = (int)localTIFFDirectory.getFieldAsLong(274);
        if ((j == 3) || (j == 4))
          f1 = 3.141593F;
        else if ((j == 5) || (j == 8))
          f1 = 1.570796F;
        else if ((j == 6) || (j == 7))
          f1 = -1.570796F;
      }
      Image localImage = null;
      long l1 = 0L;
      long l2 = 0L;
      int k = 1;
      Object localObject1 = (int)localTIFFDirectory.getFieldAsLong(257);
      int m = (int)localTIFFDirectory.getFieldAsLong(256);
      int n = 0;
      int i1 = 0;
      float f2 = 0.0F;
      int i2 = 2;
      if (localTIFFDirectory.isTagPresent(296))
        i2 = (int)localTIFFDirectory.getFieldAsLong(296);
      n = getDpi(localTIFFDirectory.getField(282), i2);
      i1 = getDpi(localTIFFDirectory.getField(283), i2);
      if (i2 == 1)
      {
        if (i1 != 0)
          f2 = n / i1;
        n = 0;
        i1 = 0;
      }
      Object localObject2 = localObject1;
      if (localTIFFDirectory.isTagPresent(278))
        localObject2 = (int)localTIFFDirectory.getFieldAsLong(278);
      if ((localObject2 <= 0) || (localObject2 > localObject1))
        localObject2 = localObject1;
      long[] arrayOfLong1 = getArrayLongShort(localTIFFDirectory, 273);
      long[] arrayOfLong2 = getArrayLongShort(localTIFFDirectory, 279);
      if (((arrayOfLong2 == null) || ((arrayOfLong2.length == 1) && ((arrayOfLong2[0] == 0L) || (arrayOfLong2[0] + arrayOfLong1[0] > paramRandomAccessFileOrArray.length())))) && (localObject1 == localObject2))
        arrayOfLong2 = new long[] { paramRandomAccessFileOrArray.length() - (int)arrayOfLong1[0] };
      int i3 = 0;
      TIFFField localTIFFField1 = localTIFFDirectory.getField(266);
      if (localTIFFField1 != null)
        k = localTIFFField1.getAsInt(0);
      i3 = k == 2 ? 1 : 0;
      int i4 = 0;
      if (localTIFFDirectory.isTagPresent(262))
      {
        long l3 = localTIFFDirectory.getFieldAsLong(262);
        if (l3 == 1L)
          i4 |= 1;
      }
      int i5 = 0;
      Object localObject3;
      Object localObject4;
      switch (i)
      {
      case 2:
      case 32771:
        i5 = 257;
        i4 |= 10;
        break;
      case 3:
        i5 = 257;
        i4 |= 12;
        localObject3 = localTIFFDirectory.getField(292);
        if (localObject3 == null)
          break;
        l1 = ((TIFFField)localObject3).getAsLong(0);
        if ((l1 & 1L) != 0L)
          i5 = 258;
        if ((l1 & 0x4) == 0L)
          break;
        i4 |= 2;
        break;
      case 4:
        i5 = 256;
        localObject4 = localTIFFDirectory.getField(293);
        if (localObject4 == null)
          break;
        l2 = ((TIFFField)localObject4).getAsLong(0);
      }
      if ((paramBoolean) && (localObject2 == localObject1))
      {
        localObject3 = new byte[(int)arrayOfLong2[0]];
        paramRandomAccessFileOrArray.seek(arrayOfLong1[0]);
        paramRandomAccessFileOrArray.readFully(localObject3);
        localImage = Image.getInstance(m, localObject1, false, i5, i4, localObject3);
        localImage.setInverted(true);
      }
      else
      {
        localObject3 = localObject1;
        localObject4 = new CCITTG4Encoder(m);
        for (int i7 = 0; i7 < arrayOfLong1.length; i7++)
        {
          byte[] arrayOfByte2 = new byte[(int)arrayOfLong2[i7]];
          paramRandomAccessFileOrArray.seek(arrayOfLong1[i7]);
          paramRandomAccessFileOrArray.readFully(arrayOfByte2);
          int i8 = Math.min(localObject2, localObject3);
          TIFFFaxDecoder localTIFFFaxDecoder = new TIFFFaxDecoder(k, m, i8);
          byte[] arrayOfByte3 = new byte[(m + 7) / 8 * i8];
          switch (i)
          {
          case 2:
          case 32771:
            localTIFFFaxDecoder.decode1D(arrayOfByte3, arrayOfByte2, 0, i8);
            ((CCITTG4Encoder)localObject4).fax4Encode(arrayOfByte3, i8);
            break;
          case 3:
            try
            {
              localTIFFFaxDecoder.decode2D(arrayOfByte3, arrayOfByte2, 0, i8, l1);
            }
            catch (RuntimeException localRuntimeException2)
            {
              l1 ^= 4L;
              try
              {
                localTIFFFaxDecoder.decode2D(arrayOfByte3, arrayOfByte2, 0, i8, l1);
              }
              catch (RuntimeException localRuntimeException3)
              {
                throw localRuntimeException2;
              }
            }
            ((CCITTG4Encoder)localObject4).fax4Encode(arrayOfByte3, i8);
            break;
          case 4:
            localTIFFFaxDecoder.decodeT6(arrayOfByte3, arrayOfByte2, 0, i8, l2);
            ((CCITTG4Encoder)localObject4).fax4Encode(arrayOfByte3, i8);
          }
          int i6;
          localObject3 -= localObject2;
        }
        byte[] arrayOfByte1 = ((CCITTG4Encoder)localObject4).close();
        localImage = Image.getInstance(m, localObject1, false, 256, i4 & 0x1, arrayOfByte1);
      }
      localImage.setDpi(n, i1);
      localImage.setXYRatio(f2);
      if (localTIFFDirectory.isTagPresent(34675))
        try
        {
          TIFFField localTIFFField2 = localTIFFDirectory.getField(34675);
          localObject4 = ICC_Profile.getInstance(localTIFFField2.getAsBytes());
          if (((ICC_Profile)localObject4).getNumComponents() == 1)
            localImage.tagICC((ICC_Profile)localObject4);
        }
        catch (RuntimeException localRuntimeException1)
        {
        }
      localImage.setOriginalType(5);
      if (f1 != 0.0F)
        localImage.setInitialRotation(f1);
      return localImage;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  protected static Image getTiffImageColor(TIFFDirectory paramTIFFDirectory, RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    try
    {
      int i = (int)paramTIFFDirectory.getFieldAsLong(259);
      int j = 1;
      TIFFLZWDecoder localTIFFLZWDecoder = null;
      switch (i)
      {
      case 1:
      case 5:
      case 6:
      case 7:
      case 8:
      case 32773:
      case 32946:
        break;
      default:
        throw new IllegalArgumentException("The compression " + i + " is not supported.");
      }
      int k = (int)paramTIFFDirectory.getFieldAsLong(262);
      switch (k)
      {
      case 0:
      case 1:
      case 2:
      case 3:
      case 5:
        break;
      case 4:
      default:
        if ((i == 6) || (i == 7))
          break;
        throw new IllegalArgumentException("The photometric " + k + " is not supported.");
      }
      float f = 0.0F;
      if (paramTIFFDirectory.isTagPresent(274))
      {
        m = (int)paramTIFFDirectory.getFieldAsLong(274);
        if ((m == 3) || (m == 4))
          f = 3.141593F;
        else if ((m == 5) || (m == 8))
          f = 1.570796F;
        else if ((m == 6) || (m == 7))
          f = -1.570796F;
      }
      if ((paramTIFFDirectory.isTagPresent(284)) && (paramTIFFDirectory.getFieldAsLong(284) == 2L))
        throw new IllegalArgumentException("Planar images are not supported.");
      if (paramTIFFDirectory.isTagPresent(338))
        throw new IllegalArgumentException("Extra samples are not supported.");
      int m = 1;
      if (paramTIFFDirectory.isTagPresent(277))
        m = (int)paramTIFFDirectory.getFieldAsLong(277);
      int n = 1;
      if (paramTIFFDirectory.isTagPresent(258))
        n = (int)paramTIFFDirectory.getFieldAsLong(258);
      switch (n)
      {
      case 1:
      case 2:
      case 4:
      case 8:
        break;
      case 3:
      case 5:
      case 6:
      case 7:
      default:
        throw new IllegalArgumentException("Bits per sample " + n + " is not supported.");
      }
      Object localObject1 = null;
      TIFFField localTIFFField1 = (int)paramTIFFDirectory.getFieldAsLong(257);
      int i1 = (int)paramTIFFDirectory.getFieldAsLong(256);
      int i2 = 0;
      int i3 = 0;
      int i4 = 2;
      if (paramTIFFDirectory.isTagPresent(296))
        i4 = (int)paramTIFFDirectory.getFieldAsLong(296);
      i2 = getDpi(paramTIFFDirectory.getField(282), i4);
      i3 = getDpi(paramTIFFDirectory.getField(283), i4);
      int i5 = 1;
      int i6 = 0;
      TIFFField localTIFFField2 = paramTIFFDirectory.getField(266);
      if (localTIFFField2 != null)
        i5 = localTIFFField2.getAsInt(0);
      i6 = i5 == 2 ? 1 : 0;
      TIFFField localTIFFField3 = localTIFFField1;
      if (paramTIFFDirectory.isTagPresent(278))
        localTIFFField3 = (int)paramTIFFDirectory.getFieldAsLong(278);
      if ((localTIFFField3 <= 0) || (localTIFFField3 > localTIFFField1))
        localTIFFField3 = localTIFFField1;
      long[] arrayOfLong1 = getArrayLongShort(paramTIFFDirectory, 273);
      long[] arrayOfLong2 = getArrayLongShort(paramTIFFDirectory, 279);
      if (((arrayOfLong2 == null) || ((arrayOfLong2.length == 1) && ((arrayOfLong2[0] == 0L) || (arrayOfLong2[0] + arrayOfLong1[0] > paramRandomAccessFileOrArray.length())))) && (localTIFFField1 == localTIFFField3))
        arrayOfLong2 = new long[] { paramRandomAccessFileOrArray.length() - (int)arrayOfLong1[0] };
      if (i == 5)
      {
        localTIFFField4 = paramTIFFDirectory.getField(317);
        if (localTIFFField4 != null)
        {
          j = localTIFFField4.getAsInt(0);
          if ((j != 1) && (j != 2))
            throw new RuntimeException("Illegal value for Predictor in TIFF file.");
          if ((j == 2) && (n != 8))
            throw new RuntimeException(n + "-bit samples are not supported for Horizontal differencing Predictor.");
        }
        localTIFFLZWDecoder = new TIFFLZWDecoder(i1, j, m);
      }
      TIFFField localTIFFField4 = localTIFFField1;
      ByteArrayOutputStream localByteArrayOutputStream = null;
      DeflaterOutputStream localDeflaterOutputStream = null;
      CCITTG4Encoder localCCITTG4Encoder = null;
      if ((n == 1) && (m == 1))
      {
        localCCITTG4Encoder = new CCITTG4Encoder(i1);
      }
      else
      {
        localByteArrayOutputStream = new ByteArrayOutputStream();
        if ((i != 6) && (i != 7))
          localDeflaterOutputStream = new DeflaterOutputStream(localByteArrayOutputStream);
      }
      Object localObject2;
      if (i == 6)
      {
        if (!paramTIFFDirectory.isTagPresent(513))
          throw new IOException("Missing tag(s) for OJPEG compression.");
        int i8 = (int)paramTIFFDirectory.getFieldAsLong(513);
        int i10 = paramRandomAccessFileOrArray.length() - i8;
        if (paramTIFFDirectory.isTagPresent(514))
          i10 = (int)paramTIFFDirectory.getFieldAsLong(514) + (int)arrayOfLong2[0];
        byte[] arrayOfByte2 = new byte[Math.min(i10, paramRandomAccessFileOrArray.length() - i8)];
        int i12 = paramRandomAccessFileOrArray.getFilePointer();
        i12 += i8;
        paramRandomAccessFileOrArray.seek(i12);
        paramRandomAccessFileOrArray.readFully(arrayOfByte2);
        localObject1 = new Jpeg(arrayOfByte2);
      }
      else if (i == 7)
      {
        if (arrayOfLong2.length > 1)
          throw new IOException("Compression JPEG is only supported with a single strip. This image has " + arrayOfLong2.length + " strips.");
        byte[] arrayOfByte1 = new byte[(int)arrayOfLong2[0]];
        paramRandomAccessFileOrArray.seek(arrayOfLong1[0]);
        paramRandomAccessFileOrArray.readFully(arrayOfByte1);
        localObject1 = new Jpeg(arrayOfByte1);
      }
      else
      {
        for (int i9 = 0; i9 < arrayOfLong1.length; i9++)
        {
          localObject2 = new byte[(int)arrayOfLong2[i9]];
          paramRandomAccessFileOrArray.seek(arrayOfLong1[i9]);
          paramRandomAccessFileOrArray.readFully(localObject2);
          int i11 = Math.min(localTIFFField3, localTIFFField4);
          Object localObject3 = null;
          if (i != 1)
            localObject3 = new byte[(i1 * n * m + 7) / 8 * i11];
          if (i6 != 0)
            TIFFFaxDecoder.reverseBits(localObject2);
          switch (i)
          {
          case 8:
          case 32946:
            inflate(localObject2, localObject3);
            break;
          case 1:
            localObject3 = localObject2;
            break;
          case 32773:
            decodePackbits(localObject2, localObject3);
            break;
          case 5:
            localTIFFLZWDecoder.decode(localObject2, localObject3, i11);
          }
          if ((n == 1) && (m == 1))
            localCCITTG4Encoder.fax4Encode(localObject3, i11);
          else
            localDeflaterOutputStream.write(localObject3);
          int i7;
          localTIFFField4 -= localTIFFField3;
        }
        if ((n == 1) && (m == 1))
        {
          localObject1 = Image.getInstance(i1, localTIFFField1, false, 256, k == 1 ? 1 : 0, localCCITTG4Encoder.close());
        }
        else
        {
          localDeflaterOutputStream.close();
          localObject1 = Image.getInstance(i1, localTIFFField1, m, n, localByteArrayOutputStream.toByteArray());
          ((Image)localObject1).setDeflated(true);
        }
      }
      ((Image)localObject1).setDpi(i2, i3);
      if ((i != 6) && (i != 7))
      {
        if (paramTIFFDirectory.isTagPresent(34675))
          try
          {
            TIFFField localTIFFField5 = paramTIFFDirectory.getField(34675);
            localObject2 = ICC_Profile.getInstance(localTIFFField5.getAsBytes());
            if (m == ((ICC_Profile)localObject2).getNumComponents())
              ((Image)localObject1).tagICC((ICC_Profile)localObject2);
          }
          catch (RuntimeException localRuntimeException)
          {
          }
        if (paramTIFFDirectory.isTagPresent(320))
        {
          TIFFField localTIFFField6 = paramTIFFDirectory.getField(320);
          localObject2 = localTIFFField6.getAsChars();
          byte[] arrayOfByte3 = new byte[localObject2.length];
          int i13 = localObject2.length / 3;
          int i14 = i13 * 2;
          for (int i15 = 0; i15 < i13; i15++)
          {
            arrayOfByte3[(i15 * 3)] = (byte)(localObject2[i15] >>> '\b');
            arrayOfByte3[(i15 * 3 + 1)] = (byte)(localObject2[(i15 + i13)] >>> '\b');
            arrayOfByte3[(i15 * 3 + 2)] = (byte)(localObject2[(i15 + i14)] >>> '\b');
          }
          PdfArray localPdfArray = new PdfArray();
          localPdfArray.add(PdfName.INDEXED);
          localPdfArray.add(PdfName.DEVICERGB);
          localPdfArray.add(new PdfNumber(i13 - 1));
          localPdfArray.add(new PdfString(arrayOfByte3));
          PdfDictionary localPdfDictionary = new PdfDictionary();
          localPdfDictionary.put(PdfName.COLORSPACE, localPdfArray);
          ((Image)localObject1).setAdditional(localPdfDictionary);
        }
        ((Image)localObject1).setOriginalType(5);
      }
      if (k == 0)
        ((Image)localObject1).setInverted(true);
      if (f != 0.0F)
        ((Image)localObject1).setInitialRotation(f);
      return localObject1;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  static long[] getArrayLongShort(TIFFDirectory paramTIFFDirectory, int paramInt)
  {
    TIFFField localTIFFField = paramTIFFDirectory.getField(paramInt);
    if (localTIFFField == null)
      return null;
    long[] arrayOfLong;
    if (localTIFFField.getType() == 4)
    {
      arrayOfLong = localTIFFField.getAsLongs();
    }
    else
    {
      char[] arrayOfChar = localTIFFField.getAsChars();
      arrayOfLong = new long[arrayOfChar.length];
      for (int i = 0; i < arrayOfChar.length; i++)
        arrayOfLong[i] = arrayOfChar[i];
    }
    return arrayOfLong;
  }

  public static void decodePackbits(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i = 0;
    int j = 0;
    try
    {
      while (j < paramArrayOfByte2.length)
      {
        int m = paramArrayOfByte1[(i++)];
        int n;
        if ((m >= 0) && (m <= 127))
          for (n = 0; n < m + 1; n++)
            paramArrayOfByte2[(j++)] = paramArrayOfByte1[(i++)];
        if ((m <= -1) && (m >= -127))
        {
          int k = paramArrayOfByte1[(i++)];
          for (n = 0; n < -m + 1; n++)
            paramArrayOfByte2[(j++)] = k;
        }
        i++;
      }
    }
    catch (Exception localException)
    {
    }
  }

  public static void inflate(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    Inflater localInflater = new Inflater();
    localInflater.setInput(paramArrayOfByte1);
    try
    {
      localInflater.inflate(paramArrayOfByte2);
    }
    catch (DataFormatException localDataFormatException)
    {
      throw new ExceptionConverter(localDataFormatException);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.TiffImage
 * JD-Core Version:    0.6.0
 */