package com.lowagie.text.pdf.codec;

import com.lowagie.text.pdf.ByteBuffer;

public class CCITTG4Encoder
{
  private int rowbytes;
  private int rowpixels;
  private int bit = 8;
  private int data;
  private byte[] refline;
  private ByteBuffer outBuf = new ByteBuffer(1024);
  private byte[] dataBp;
  private int offsetData;
  private int sizeData;
  private static byte[] zeroruns = { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  private static byte[] oneruns = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 7, 8 };
  private static final int LENGTH = 0;
  private static final int CODE = 1;
  private static final int RUNLEN = 2;
  private static final int EOL = 1;
  private static final int G3CODE_EOL = -1;
  private static final int G3CODE_INVALID = -2;
  private static final int G3CODE_EOF = -3;
  private static final int G3CODE_INCOMP = -4;
  private int[][] TIFFFaxWhiteCodes = { { 8, 53, 0 }, { 6, 7, 1 }, { 4, 7, 2 }, { 4, 8, 3 }, { 4, 11, 4 }, { 4, 12, 5 }, { 4, 14, 6 }, { 4, 15, 7 }, { 5, 19, 8 }, { 5, 20, 9 }, { 5, 7, 10 }, { 5, 8, 11 }, { 6, 8, 12 }, { 6, 3, 13 }, { 6, 52, 14 }, { 6, 53, 15 }, { 6, 42, 16 }, { 6, 43, 17 }, { 7, 39, 18 }, { 7, 12, 19 }, { 7, 8, 20 }, { 7, 23, 21 }, { 7, 3, 22 }, { 7, 4, 23 }, { 7, 40, 24 }, { 7, 43, 25 }, { 7, 19, 26 }, { 7, 36, 27 }, { 7, 24, 28 }, { 8, 2, 29 }, { 8, 3, 30 }, { 8, 26, 31 }, { 8, 27, 32 }, { 8, 18, 33 }, { 8, 19, 34 }, { 8, 20, 35 }, { 8, 21, 36 }, { 8, 22, 37 }, { 8, 23, 38 }, { 8, 40, 39 }, { 8, 41, 40 }, { 8, 42, 41 }, { 8, 43, 42 }, { 8, 44, 43 }, { 8, 45, 44 }, { 8, 4, 45 }, { 8, 5, 46 }, { 8, 10, 47 }, { 8, 11, 48 }, { 8, 82, 49 }, { 8, 83, 50 }, { 8, 84, 51 }, { 8, 85, 52 }, { 8, 36, 53 }, { 8, 37, 54 }, { 8, 88, 55 }, { 8, 89, 56 }, { 8, 90, 57 }, { 8, 91, 58 }, { 8, 74, 59 }, { 8, 75, 60 }, { 8, 50, 61 }, { 8, 51, 62 }, { 8, 52, 63 }, { 5, 27, 64 }, { 5, 18, 128 }, { 6, 23, 192 }, { 7, 55, 256 }, { 8, 54, 320 }, { 8, 55, 384 }, { 8, 100, 448 }, { 8, 101, 512 }, { 8, 104, 576 }, { 8, 103, 640 }, { 9, 204, 704 }, { 9, 205, 768 }, { 9, 210, 832 }, { 9, 211, 896 }, { 9, 212, 960 }, { 9, 213, 1024 }, { 9, 214, 1088 }, { 9, 215, 1152 }, { 9, 216, 1216 }, { 9, 217, 1280 }, { 9, 218, 1344 }, { 9, 219, 1408 }, { 9, 152, 1472 }, { 9, 153, 1536 }, { 9, 154, 1600 }, { 6, 24, 1664 }, { 9, 155, 1728 }, { 11, 8, 1792 }, { 11, 12, 1856 }, { 11, 13, 1920 }, { 12, 18, 1984 }, { 12, 19, 2048 }, { 12, 20, 2112 }, { 12, 21, 2176 }, { 12, 22, 2240 }, { 12, 23, 2304 }, { 12, 28, 2368 }, { 12, 29, 2432 }, { 12, 30, 2496 }, { 12, 31, 2560 }, { 12, 1, -1 }, { 9, 1, -2 }, { 10, 1, -2 }, { 11, 1, -2 }, { 12, 0, -2 } };
  private int[][] TIFFFaxBlackCodes = { { 10, 55, 0 }, { 3, 2, 1 }, { 2, 3, 2 }, { 2, 2, 3 }, { 3, 3, 4 }, { 4, 3, 5 }, { 4, 2, 6 }, { 5, 3, 7 }, { 6, 5, 8 }, { 6, 4, 9 }, { 7, 4, 10 }, { 7, 5, 11 }, { 7, 7, 12 }, { 8, 4, 13 }, { 8, 7, 14 }, { 9, 24, 15 }, { 10, 23, 16 }, { 10, 24, 17 }, { 10, 8, 18 }, { 11, 103, 19 }, { 11, 104, 20 }, { 11, 108, 21 }, { 11, 55, 22 }, { 11, 40, 23 }, { 11, 23, 24 }, { 11, 24, 25 }, { 12, 202, 26 }, { 12, 203, 27 }, { 12, 204, 28 }, { 12, 205, 29 }, { 12, 104, 30 }, { 12, 105, 31 }, { 12, 106, 32 }, { 12, 107, 33 }, { 12, 210, 34 }, { 12, 211, 35 }, { 12, 212, 36 }, { 12, 213, 37 }, { 12, 214, 38 }, { 12, 215, 39 }, { 12, 108, 40 }, { 12, 109, 41 }, { 12, 218, 42 }, { 12, 219, 43 }, { 12, 84, 44 }, { 12, 85, 45 }, { 12, 86, 46 }, { 12, 87, 47 }, { 12, 100, 48 }, { 12, 101, 49 }, { 12, 82, 50 }, { 12, 83, 51 }, { 12, 36, 52 }, { 12, 55, 53 }, { 12, 56, 54 }, { 12, 39, 55 }, { 12, 40, 56 }, { 12, 88, 57 }, { 12, 89, 58 }, { 12, 43, 59 }, { 12, 44, 60 }, { 12, 90, 61 }, { 12, 102, 62 }, { 12, 103, 63 }, { 10, 15, 64 }, { 12, 200, 128 }, { 12, 201, 192 }, { 12, 91, 256 }, { 12, 51, 320 }, { 12, 52, 384 }, { 12, 53, 448 }, { 13, 108, 512 }, { 13, 109, 576 }, { 13, 74, 640 }, { 13, 75, 704 }, { 13, 76, 768 }, { 13, 77, 832 }, { 13, 114, 896 }, { 13, 115, 960 }, { 13, 116, 1024 }, { 13, 117, 1088 }, { 13, 118, 1152 }, { 13, 119, 1216 }, { 13, 82, 1280 }, { 13, 83, 1344 }, { 13, 84, 1408 }, { 13, 85, 1472 }, { 13, 90, 1536 }, { 13, 91, 1600 }, { 13, 100, 1664 }, { 13, 101, 1728 }, { 11, 8, 1792 }, { 11, 12, 1856 }, { 11, 13, 1920 }, { 12, 18, 1984 }, { 12, 19, 2048 }, { 12, 20, 2112 }, { 12, 21, 2176 }, { 12, 22, 2240 }, { 12, 23, 2304 }, { 12, 28, 2368 }, { 12, 29, 2432 }, { 12, 30, 2496 }, { 12, 31, 2560 }, { 12, 1, -1 }, { 9, 1, -2 }, { 10, 1, -2 }, { 11, 1, -2 }, { 12, 0, -2 } };
  private int[] horizcode = { 3, 1, 0 };
  private int[] passcode = { 4, 1, 0 };
  private int[][] vcodes = { { 7, 3, 0 }, { 6, 3, 0 }, { 3, 3, 0 }, { 1, 1, 0 }, { 3, 2, 0 }, { 6, 2, 0 }, { 7, 2, 0 } };
  private int[] msbmask = { 0, 1, 3, 7, 15, 31, 63, 127, 255 };

  public CCITTG4Encoder(int paramInt)
  {
    this.rowpixels = paramInt;
    this.rowbytes = ((this.rowpixels + 7) / 8);
    this.refline = new byte[this.rowbytes];
  }

  public void fax4Encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.dataBp = paramArrayOfByte;
    this.offsetData = paramInt1;
    this.sizeData = paramInt2;
    while (this.sizeData > 0)
    {
      Fax3Encode2DRow();
      System.arraycopy(this.dataBp, this.offsetData, this.refline, 0, this.rowbytes);
      this.offsetData += this.rowbytes;
      this.sizeData -= this.rowbytes;
    }
  }

  public static byte[] compress(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    CCITTG4Encoder localCCITTG4Encoder = new CCITTG4Encoder(paramInt1);
    localCCITTG4Encoder.fax4Encode(paramArrayOfByte, 0, localCCITTG4Encoder.rowbytes * paramInt2);
    return localCCITTG4Encoder.close();
  }

  public void fax4Encode(byte[] paramArrayOfByte, int paramInt)
  {
    fax4Encode(paramArrayOfByte, 0, this.rowbytes * paramInt);
  }

  private void putcode(int[] paramArrayOfInt)
  {
    putBits(paramArrayOfInt[1], paramArrayOfInt[0]);
  }

  private void putspan(int paramInt, int[][] paramArrayOfInt)
  {
    int[] arrayOfInt;
    while (paramInt >= 2624)
    {
      arrayOfInt = paramArrayOfInt[103];
      i = arrayOfInt[1];
      j = arrayOfInt[0];
      putBits(i, j);
      paramInt -= arrayOfInt[2];
    }
    if (paramInt >= 64)
    {
      arrayOfInt = paramArrayOfInt[(63 + (paramInt >> 6))];
      i = arrayOfInt[1];
      j = arrayOfInt[0];
      putBits(i, j);
      paramInt -= arrayOfInt[2];
    }
    int i = paramArrayOfInt[paramInt][1];
    int j = paramArrayOfInt[paramInt][0];
    putBits(i, j);
  }

  private void putBits(int paramInt1, int paramInt2)
  {
    while (paramInt2 > this.bit)
    {
      this.data |= paramInt1 >> paramInt2 - this.bit;
      paramInt2 -= this.bit;
      this.outBuf.append((byte)this.data);
      this.data = 0;
      this.bit = 8;
    }
    this.data |= (paramInt1 & this.msbmask[paramInt2]) << this.bit - paramInt2;
    this.bit -= paramInt2;
    if (this.bit == 0)
    {
      this.outBuf.append((byte)this.data);
      this.data = 0;
      this.bit = 8;
    }
  }

  private void Fax3Encode2DRow()
  {
    int i = 0;
    int j = pixel(this.dataBp, this.offsetData, 0) != 0 ? 0 : finddiff(this.dataBp, this.offsetData, 0, this.rowpixels, 0);
    for (int k = pixel(this.refline, 0, 0) != 0 ? 0 : finddiff(this.refline, 0, 0, this.rowpixels, 0); ; k = finddiff(this.refline, 0, k, this.rowpixels, pixel(this.dataBp, this.offsetData, i)))
    {
      int n = finddiff2(this.refline, 0, k, this.rowpixels, pixel(this.refline, 0, k));
      if (n >= j)
      {
        int i1 = k - j;
        if ((-3 > i1) || (i1 > 3))
        {
          int m = finddiff2(this.dataBp, this.offsetData, j, this.rowpixels, pixel(this.dataBp, this.offsetData, j));
          putcode(this.horizcode);
          if ((i + j == 0) || (pixel(this.dataBp, this.offsetData, i) == 0))
          {
            putspan(j - i, this.TIFFFaxWhiteCodes);
            putspan(m - j, this.TIFFFaxBlackCodes);
          }
          else
          {
            putspan(j - i, this.TIFFFaxBlackCodes);
            putspan(m - j, this.TIFFFaxWhiteCodes);
          }
          i = m;
        }
        else
        {
          putcode(this.vcodes[(i1 + 3)]);
          i = j;
        }
      }
      else
      {
        putcode(this.passcode);
        i = n;
      }
      if (i >= this.rowpixels)
        break;
      j = finddiff(this.dataBp, this.offsetData, i, this.rowpixels, pixel(this.dataBp, this.offsetData, i));
      k = finddiff(this.refline, 0, i, this.rowpixels, pixel(this.dataBp, this.offsetData, i) ^ 0x1);
    }
  }

  private void Fax4PostEncode()
  {
    putBits(1, 12);
    putBits(1, 12);
    if (this.bit != 8)
    {
      this.outBuf.append((byte)this.data);
      this.data = 0;
      this.bit = 8;
    }
  }

  public byte[] close()
  {
    Fax4PostEncode();
    return this.outBuf.toByteArray();
  }

  private int pixel(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 >= this.rowpixels)
      return 0;
    return (paramArrayOfByte[(paramInt1 + (paramInt2 >> 3))] & 0xFF) >> 7 - (paramInt2 & 0x7) & 0x1;
  }

  private static int find1span(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3 - paramInt2;
    int m = paramInt1 + (paramInt2 >> 3);
    int j;
    int k;
    if ((i > 0) && ((j = paramInt2 & 0x7) != 0))
    {
      k = oneruns[(paramArrayOfByte[m] << j & 0xFF)];
      if (k > 8 - j)
        k = 8 - j;
      if (k > i)
        k = i;
      if (j + k < 8)
        return k;
      i -= k;
      m++;
    }
    else
    {
      k = 0;
    }
    while (i >= 8)
    {
      if (paramArrayOfByte[m] != -1)
        return k + oneruns[(paramArrayOfByte[m] & 0xFF)];
      k += 8;
      i -= 8;
      m++;
    }
    if (i > 0)
    {
      j = oneruns[(paramArrayOfByte[m] & 0xFF)];
      k += (j > i ? i : j);
    }
    return k;
  }

  private static int find0span(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3 - paramInt2;
    int m = paramInt1 + (paramInt2 >> 3);
    int j;
    int k;
    if ((i > 0) && ((j = paramInt2 & 0x7) != 0))
    {
      k = zeroruns[(paramArrayOfByte[m] << j & 0xFF)];
      if (k > 8 - j)
        k = 8 - j;
      if (k > i)
        k = i;
      if (j + k < 8)
        return k;
      i -= k;
      m++;
    }
    else
    {
      k = 0;
    }
    while (i >= 8)
    {
      if (paramArrayOfByte[m] != 0)
        return k + zeroruns[(paramArrayOfByte[m] & 0xFF)];
      k += 8;
      i -= 8;
      m++;
    }
    if (i > 0)
    {
      j = zeroruns[(paramArrayOfByte[m] & 0xFF)];
      k += (j > i ? i : j);
    }
    return k;
  }

  private static int finddiff(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt2 + (paramInt4 != 0 ? find1span(paramArrayOfByte, paramInt1, paramInt2, paramInt3) : find0span(paramArrayOfByte, paramInt1, paramInt2, paramInt3));
  }

  private static int finddiff2(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt2 < paramInt3 ? finddiff(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4) : paramInt3;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.CCITTG4Encoder
 * JD-Core Version:    0.6.0
 */