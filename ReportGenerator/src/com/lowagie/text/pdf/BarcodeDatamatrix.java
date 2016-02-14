package com.lowagie.text.pdf;

import com.lowagie.text.BadElementException;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.MemoryImageSource;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;

public class BarcodeDatamatrix
{
  public static final int DM_NO_ERROR = 0;
  public static final int DM_ERROR_TEXT_TOO_BIG = 1;
  public static final int DM_ERROR_INVALID_SQUARE = 3;
  public static final int DM_ERROR_EXTENSION = 5;
  public static final int DM_AUTO = 0;
  public static final int DM_ASCII = 1;
  public static final int DM_C40 = 2;
  public static final int DM_TEXT = 3;
  public static final int DM_B256 = 4;
  public static final int DM_X21 = 5;
  public static final int DM_EDIFACT = 6;
  public static final int DM_RAW = 7;
  public static final int DM_EXTENSION = 32;
  public static final int DM_TEST = 64;
  private static final DmParams[] dmSizes = { new DmParams(10, 10, 10, 10, 3, 3, 5), new DmParams(12, 12, 12, 12, 5, 5, 7), new DmParams(8, 18, 8, 18, 5, 5, 7), new DmParams(14, 14, 14, 14, 8, 8, 10), new DmParams(8, 32, 8, 16, 10, 10, 11), new DmParams(16, 16, 16, 16, 12, 12, 12), new DmParams(12, 26, 12, 26, 16, 16, 14), new DmParams(18, 18, 18, 18, 18, 18, 14), new DmParams(20, 20, 20, 20, 22, 22, 18), new DmParams(12, 36, 12, 18, 22, 22, 18), new DmParams(22, 22, 22, 22, 30, 30, 20), new DmParams(16, 36, 16, 18, 32, 32, 24), new DmParams(24, 24, 24, 24, 36, 36, 24), new DmParams(26, 26, 26, 26, 44, 44, 28), new DmParams(16, 48, 16, 24, 49, 49, 28), new DmParams(32, 32, 16, 16, 62, 62, 36), new DmParams(36, 36, 18, 18, 86, 86, 42), new DmParams(40, 40, 20, 20, 114, 114, 48), new DmParams(44, 44, 22, 22, 144, 144, 56), new DmParams(48, 48, 24, 24, 174, 174, 68), new DmParams(52, 52, 26, 26, 204, 102, 42), new DmParams(64, 64, 16, 16, 280, 140, 56), new DmParams(72, 72, 18, 18, 368, 92, 36), new DmParams(80, 80, 20, 20, 456, 114, 48), new DmParams(88, 88, 22, 22, 576, 144, 56), new DmParams(96, 96, 24, 24, 696, 174, 68), new DmParams(104, 104, 26, 26, 816, 136, 56), new DmParams(120, 120, 20, 20, 1050, 175, 68), new DmParams(132, 132, 22, 22, 1304, 163, 62), new DmParams(144, 144, 24, 24, 1558, 156, 62) };
  private static final String x12 = "\r*> 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private int extOut;
  private short[] place;
  private byte[] image;
  private int height;
  private int width;
  private int ws;
  private int options;

  private void setBit(int paramInt1, int paramInt2, int paramInt3)
  {
    int tmp12_11 = (paramInt2 * paramInt3 + paramInt1 / 8);
    byte[] tmp12_1 = this.image;
    tmp12_1[tmp12_11] = (byte)(tmp12_1[tmp12_11] | (byte)(128 >> (paramInt1 & 0x7)));
  }

  private void draw(byte[] paramArrayOfByte, int paramInt, DmParams paramDmParams)
  {
    int i4 = (paramDmParams.width + this.ws * 2 + 7) / 8;
    Arrays.fill(this.image, 0);
    int i = this.ws;
    int j;
    while (i < paramDmParams.height + this.ws)
    {
      for (j = this.ws; j < paramDmParams.width + this.ws; j += 2)
        setBit(j, i, i4);
      i += paramDmParams.heightSection;
    }
    i = paramDmParams.heightSection - 1 + this.ws;
    while (i < paramDmParams.height + this.ws)
    {
      for (j = this.ws; j < paramDmParams.width + this.ws; j++)
        setBit(j, i, i4);
      i += paramDmParams.heightSection;
    }
    i = this.ws;
    while (i < paramDmParams.width + this.ws)
    {
      for (j = this.ws; j < paramDmParams.height + this.ws; j++)
        setBit(i, j, i4);
      i += paramDmParams.widthSection;
    }
    i = paramDmParams.widthSection - 1 + this.ws;
    while (i < paramDmParams.width + this.ws)
    {
      for (j = 1 + this.ws; j < paramDmParams.height + this.ws; j += 2)
        setBit(i, j, i4);
      i += paramDmParams.widthSection;
    }
    int k = 0;
    int i2 = 0;
    while (i2 < paramDmParams.height)
    {
      for (int n = 1; n < paramDmParams.heightSection - 1; n++)
      {
        int i1 = 0;
        while (i1 < paramDmParams.width)
        {
          for (int m = 1; m < paramDmParams.widthSection - 1; m++)
          {
            int i3 = this.place[(k++)];
            if ((i3 != 1) && ((i3 <= 1) || ((paramArrayOfByte[(i3 / 8 - 1)] & 0xFF & 128 >> i3 % 8) == 0)))
              continue;
            setBit(m + i1 + this.ws, n + i2 + this.ws, i4);
          }
          i1 += paramDmParams.widthSection;
        }
      }
      i2 += paramDmParams.heightSection;
    }
  }

  private static void makePadding(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0)
      return;
    paramArrayOfByte[(paramInt1++)] = -127;
    while (true)
    {
      paramInt2--;
      if (paramInt2 <= 0)
        break;
      int i = 129 + (paramInt1 + 1) * 149 % 253 + 1;
      if (i > 254)
        i -= 254;
      paramArrayOfByte[(paramInt1++)] = (byte)i;
    }
  }

  private static boolean isDigit(int paramInt)
  {
    return (paramInt >= 48) && (paramInt <= 57);
  }

  private static int asciiEncodation(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
  {
    int i = paramInt1;
    int j = paramInt3;
    paramInt2 += paramInt1;
    paramInt4 += paramInt3;
    while (i < paramInt2)
    {
      if (j >= paramInt4)
        return -1;
      int k = paramArrayOfByte1[(i++)] & 0xFF;
      if ((isDigit(k)) && (i < paramInt2) && (isDigit(paramArrayOfByte1[i] & 0xFF)))
      {
        paramArrayOfByte2[(j++)] = (byte)((k - 48) * 10 + (paramArrayOfByte1[(i++)] & 0xFF) - 48 + 130);
        continue;
      }
      if (k > 127)
      {
        if (j + 1 >= paramInt4)
          return -1;
        paramArrayOfByte2[(j++)] = -21;
        paramArrayOfByte2[(j++)] = (byte)(k - 128 + 1);
        continue;
      }
      paramArrayOfByte2[(j++)] = (byte)(k + 1);
    }
    return j - paramInt3;
  }

  private static int b256Encodation(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
  {
    if (paramInt2 == 0)
      return 0;
    if ((paramInt2 < 250) && (paramInt2 + 2 > paramInt4))
      return -1;
    if ((paramInt2 >= 250) && (paramInt2 + 3 > paramInt4))
      return -1;
    paramArrayOfByte2[paramInt3] = -25;
    int i;
    if (paramInt2 < 250)
    {
      paramArrayOfByte2[(paramInt3 + 1)] = (byte)paramInt2;
      i = 2;
    }
    else
    {
      paramArrayOfByte2[(paramInt3 + 1)] = (byte)(paramInt2 / 250 + 249);
      paramArrayOfByte2[(paramInt3 + 2)] = (byte)(paramInt2 % 250);
      i = 3;
    }
    System.arraycopy(paramArrayOfByte1, paramInt1, paramArrayOfByte2, i + paramInt3, paramInt2);
    i += paramInt2 + paramInt3;
    for (int j = paramInt3 + 1; j < i; j++)
    {
      int n = paramArrayOfByte2[j] & 0xFF;
      int k = 149 * (j + 1) % 255 + 1;
      int m = n + k;
      if (m > 255)
        m -= 256;
      paramArrayOfByte2[j] = (byte)m;
    }
    return i - paramInt3;
  }

  private static int X12Encodation(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
  {
    if (paramInt2 == 0)
      return 0;
    int i = 0;
    int j = 0;
    byte[] arrayOfByte = new byte[paramInt2];
    int k = 0;
    while (i < paramInt2)
    {
      int i3 = "\r*> 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf((char)paramArrayOfByte1[(i + paramInt1)]);
      if (i3 >= 0)
      {
        arrayOfByte[i] = (byte)i3;
        k++;
      }
      else
      {
        arrayOfByte[i] = 100;
        if (k >= 6)
          k -= k / 3 * 3;
        for (m = 0; m < k; m++)
          arrayOfByte[(i - m - 1)] = 100;
        k = 0;
      }
      i++;
    }
    if (k >= 6)
      k -= k / 3 * 3;
    for (int m = 0; m < k; m++)
      arrayOfByte[(i - m - 1)] = 100;
    i = 0;
    int i2 = 0;
    while (i < paramInt2)
    {
      i2 = arrayOfByte[i];
      if (j >= paramInt4)
        break;
      if (i2 < 40)
      {
        if ((i == 0) || ((i > 0) && (arrayOfByte[(i - 1)] > 40)))
          paramArrayOfByte2[(paramInt3 + j++)] = -18;
        if (j + 2 > paramInt4)
          break;
        int n = 1600 * arrayOfByte[i] + 40 * arrayOfByte[(i + 1)] + arrayOfByte[(i + 2)] + 1;
        paramArrayOfByte2[(paramInt3 + j++)] = (byte)(n / 256);
        paramArrayOfByte2[(paramInt3 + j++)] = (byte)n;
        i += 2;
      }
      else
      {
        if ((i > 0) && (arrayOfByte[(i - 1)] < 40))
          paramArrayOfByte2[(paramInt3 + j++)] = -2;
        int i1 = paramArrayOfByte1[(i + paramInt1)] & 0xFF;
        if (i1 > 127)
        {
          paramArrayOfByte2[(paramInt3 + j++)] = -21;
          i1 -= 128;
        }
        if (j >= paramInt4)
          break;
        paramArrayOfByte2[(paramInt3 + j++)] = (byte)(i1 + 1);
      }
      i++;
    }
    i2 = 100;
    if (paramInt2 > 0)
      i2 = arrayOfByte[(paramInt2 - 1)];
    if ((i != paramInt2) || ((i2 < 40) && (j >= paramInt4)))
      return -1;
    if (i2 < 40)
      paramArrayOfByte2[(paramInt3 + j++)] = -2;
    return j;
  }

  private static int EdifactEncodation(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4)
  {
    if (paramInt2 == 0)
      return 0;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 18;
    int i1 = 1;
    while (i < paramInt2)
    {
      int n = paramArrayOfByte1[(i + paramInt1)] & 0xFF;
      if ((((n & 0xE0) == 64) || ((n & 0xE0) == 32)) && (n != 95))
      {
        if (i1 != 0)
        {
          if (j + 1 > paramInt4)
            break;
          paramArrayOfByte2[(paramInt3 + j++)] = -16;
          i1 = 0;
        }
        n &= 63;
        k |= n << m;
        if (m == 0)
        {
          if (j + 3 > paramInt4)
            break;
          paramArrayOfByte2[(paramInt3 + j++)] = (byte)(k >> 16);
          paramArrayOfByte2[(paramInt3 + j++)] = (byte)(k >> 8);
          paramArrayOfByte2[(paramInt3 + j++)] = (byte)k;
          k = 0;
          m = 18;
        }
        else
        {
          m -= 6;
        }
      }
      else
      {
        if (i1 == 0)
        {
          k |= 31 << m;
          if (j + (3 - m / 8) > paramInt4)
            break;
          paramArrayOfByte2[(paramInt3 + j++)] = (byte)(k >> 16);
          if (m <= 12)
            paramArrayOfByte2[(paramInt3 + j++)] = (byte)(k >> 8);
          if (m <= 6)
            paramArrayOfByte2[(paramInt3 + j++)] = (byte)k;
          i1 = 1;
          m = 18;
          k = 0;
        }
        if (n > 127)
        {
          if (j >= paramInt4)
            break;
          paramArrayOfByte2[(paramInt3 + j++)] = -21;
          n -= 128;
        }
        if (j >= paramInt4)
          break;
        paramArrayOfByte2[(paramInt3 + j++)] = (byte)(n + 1);
      }
      i++;
    }
    if (i != paramInt2)
      return -1;
    if (i1 == 0)
    {
      k |= 31 << m;
      if (j + (3 - m / 8) > paramInt4)
        return -1;
      paramArrayOfByte2[(paramInt3 + j++)] = (byte)(k >> 16);
      if (m <= 12)
        paramArrayOfByte2[(paramInt3 + j++)] = (byte)(k >> 8);
      if (m <= 6)
        paramArrayOfByte2[(paramInt3 + j++)] = (byte)k;
    }
    return j;
  }

  private static int C40OrTextEncodation(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramInt2 == 0)
      return 0;
    int i = 0;
    int j = 0;
    if (paramBoolean)
      paramArrayOfByte2[(paramInt3 + j++)] = -26;
    else
      paramArrayOfByte2[(paramInt3 + j++)] = -17;
    String str2 = "!\"#$%&'()*+,-./:;<=>?@[\\]^_";
    String str1;
    String str3;
    if (paramBoolean)
    {
      str1 = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      str3 = "`abcdefghijklmnopqrstuvwxyz{|}~";
    }
    else
    {
      str1 = " 0123456789abcdefghijklmnopqrstuvwxyz";
      str3 = "`ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~";
    }
    int[] arrayOfInt = new int[paramInt2 * 4 + 10];
    int k = 0;
    int m = 0;
    int n = 0;
    while (i < paramInt2)
    {
      if (k % 3 == 0)
      {
        m = i;
        n = k;
      }
      int i3 = paramArrayOfByte1[(paramInt1 + i++)] & 0xFF;
      if (i3 > 127)
      {
        i3 -= 128;
        arrayOfInt[(k++)] = 1;
        arrayOfInt[(k++)] = 30;
      }
      int i4 = str1.indexOf((char)i3);
      if (i4 >= 0)
      {
        arrayOfInt[(k++)] = (i4 + 3);
        continue;
      }
      if (i3 < 32)
      {
        arrayOfInt[(k++)] = 0;
        arrayOfInt[(k++)] = i3;
        continue;
      }
      if ((i4 = str2.indexOf((char)i3)) >= 0)
      {
        arrayOfInt[(k++)] = 1;
        arrayOfInt[(k++)] = i4;
        continue;
      }
      if ((i4 = str3.indexOf((char)i3)) < 0)
        continue;
      arrayOfInt[(k++)] = 2;
      arrayOfInt[(k++)] = i4;
    }
    if (k % 3 != 0)
    {
      i = m;
      k = n;
    }
    if (k / 3 * 2 > paramInt4 - 2)
      return -1;
    for (int i1 = 0; i1 < k; i1 += 3)
    {
      int i2 = 1600 * arrayOfInt[i1] + 40 * arrayOfInt[(i1 + 1)] + arrayOfInt[(i1 + 2)] + 1;
      paramArrayOfByte2[(paramInt3 + j++)] = (byte)(i2 / 256);
      paramArrayOfByte2[(paramInt3 + j++)] = (byte)i2;
    }
    paramArrayOfByte2[(j++)] = -2;
    i1 = asciiEncodation(paramArrayOfByte1, i, paramInt2 - i, paramArrayOfByte2, j, paramInt4 - j);
    if (i1 < 0)
      return i1;
    return j + i1;
  }

  private static int getEncodation(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    int[] arrayOfInt = new int[6];
    if (paramInt4 < 0)
      return -1;
    int i = -1;
    paramInt5 &= 7;
    if (paramInt5 == 0)
    {
      arrayOfInt[0] = asciiEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      if ((paramBoolean) && (arrayOfInt[0] >= 0))
        return arrayOfInt[0];
      arrayOfInt[1] = C40OrTextEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, false);
      if ((paramBoolean) && (arrayOfInt[1] >= 0))
        return arrayOfInt[1];
      arrayOfInt[2] = C40OrTextEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, true);
      if ((paramBoolean) && (arrayOfInt[2] >= 0))
        return arrayOfInt[2];
      arrayOfInt[3] = b256Encodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      if ((paramBoolean) && (arrayOfInt[3] >= 0))
        return arrayOfInt[3];
      arrayOfInt[4] = X12Encodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      if ((paramBoolean) && (arrayOfInt[4] >= 0))
        return arrayOfInt[4];
      arrayOfInt[5] = EdifactEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      if ((paramBoolean) && (arrayOfInt[5] >= 0))
        return arrayOfInt[5];
      if ((arrayOfInt[0] < 0) && (arrayOfInt[1] < 0) && (arrayOfInt[2] < 0) && (arrayOfInt[3] < 0) && (arrayOfInt[4] < 0) && (arrayOfInt[5] < 0))
        return -1;
      int j = 0;
      i = 99999;
      for (int k = 0; k < 6; k++)
      {
        if ((arrayOfInt[k] < 0) || (arrayOfInt[k] >= i))
          continue;
        i = arrayOfInt[k];
        j = k;
      }
      if (j == 0)
        i = asciiEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      else if (j == 1)
        i = C40OrTextEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, false);
      else if (j == 2)
        i = C40OrTextEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, true);
      else if (j == 3)
        i = b256Encodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      else if (j == 4)
        i = X12Encodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
      return i;
    }
    switch (paramInt5)
    {
    case 1:
      return asciiEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
    case 2:
      return C40OrTextEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, true);
    case 3:
      return C40OrTextEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, false);
    case 4:
      return b256Encodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
    case 5:
      return X12Encodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
    case 6:
      return EdifactEncodation(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4);
    case 7:
      if (paramInt2 > paramInt4)
        return -1;
      System.arraycopy(paramArrayOfByte1, paramInt1, paramArrayOfByte2, paramInt3, paramInt2);
      return paramInt2;
    }
    return -1;
  }

  private static int getNumber(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    for (int j = 0; j < paramInt2; j++)
    {
      int k = paramArrayOfByte[(paramInt1++)] & 0xFF;
      if ((k < 48) || (k > 57))
        return -1;
      i = i * 10 + k - 48;
    }
    return i;
  }

  private int processExtensions(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
  {
    if ((this.options & 0x20) == 0)
      return 0;
    int i = 0;
    int j = 0;
    int k = 0;
    while (j < paramInt2)
    {
      if (i > 20)
        return -1;
      int i3 = paramArrayOfByte1[(paramInt1 + j++)] & 0xFF;
      i++;
      switch (i3)
      {
      case 46:
        this.extOut = j;
        return k;
      case 101:
        if (j + 6 > paramInt2)
          return -1;
        int m = getNumber(paramArrayOfByte1, paramInt1 + j, 6);
        if (m < 0)
          return -1;
        j += 6;
        paramArrayOfByte2[(k++)] = -15;
        if (m < 127)
        {
          paramArrayOfByte2[(k++)] = (byte)(m + 1);
        }
        else if (m < 16383)
        {
          paramArrayOfByte2[(k++)] = (byte)((m - 127) / 254 + 128);
          paramArrayOfByte2[(k++)] = (byte)((m - 127) % 254 + 1);
        }
        else
        {
          paramArrayOfByte2[(k++)] = (byte)((m - 16383) / 64516 + 192);
          paramArrayOfByte2[(k++)] = (byte)((m - 16383) / 254 % 254 + 1);
          paramArrayOfByte2[(k++)] = (byte)((m - 16383) % 254 + 1);
        }
        break;
      case 115:
        if (i != 1)
          return -1;
        if (j + 9 > paramInt2)
          return -1;
        int n = getNumber(paramArrayOfByte1, paramInt1 + j, 2);
        if ((n <= 0) || (n > 16))
          return -1;
        j += 2;
        int i1 = getNumber(paramArrayOfByte1, paramInt1 + j, 2);
        if ((i1 <= 1) || (i1 > 16))
          return -1;
        j += 2;
        int i2 = getNumber(paramArrayOfByte1, paramInt1 + j, 5);
        if ((i2 < 0) || (n >= 64516))
          return -1;
        j += 5;
        paramArrayOfByte2[(k++)] = -23;
        paramArrayOfByte2[(k++)] = (byte)(n - 1 << 4 | 17 - i1);
        paramArrayOfByte2[(k++)] = (byte)(i2 / 254 + 1);
        paramArrayOfByte2[(k++)] = (byte)(i2 % 254 + 1);
        break;
      case 112:
        if (i != 1)
          return -1;
        paramArrayOfByte2[(k++)] = -22;
        break;
      case 109:
        if (i != 1)
          return -1;
        if (j + 1 > paramInt2)
          return -1;
        i3 = paramArrayOfByte1[(paramInt1 + j++)] & 0xFF;
        if ((i3 != 53) && (i3 != 53))
          return -1;
        paramArrayOfByte2[(k++)] = -22;
        paramArrayOfByte2[(k++)] = (byte)(i3 == 53 ? 'ì' : 'í');
        break;
      case 102:
        if ((i != 1) && ((i != 2) || ((paramArrayOfByte1[paramInt1] != 115) && (paramArrayOfByte1[paramInt1] != 109))))
          return -1;
        paramArrayOfByte2[(k++)] = -24;
      }
    }
    return -1;
  }

  public int generate(String paramString)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = paramString.getBytes("iso-8859-1");
    return generate(arrayOfByte, 0, arrayOfByte.length);
  }

  public int generate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[2500];
    this.extOut = 0;
    int i = processExtensions(paramArrayOfByte, paramInt1, paramInt2, arrayOfByte);
    if (i < 0)
      return 5;
    int j = -1;
    int k;
    DmParams localDmParams1;
    if ((this.height == 0) || (this.width == 0))
    {
      DmParams localDmParams2 = dmSizes[(dmSizes.length - 1)];
      j = getEncodation(paramArrayOfByte, paramInt1 + this.extOut, paramInt2 - this.extOut, arrayOfByte, i, localDmParams2.dataSize - i, this.options, false);
      if (j < 0)
        return 1;
      j += i;
      for (k = 0; (k < dmSizes.length) && (dmSizes[k].dataSize < j); k++);
      localDmParams1 = dmSizes[k];
      this.height = localDmParams1.height;
      this.width = localDmParams1.width;
    }
    else
    {
      for (k = 0; (k < dmSizes.length) && ((this.height != dmSizes[k].height) || (this.width != dmSizes[k].width)); k++);
      if (k == dmSizes.length)
        return 3;
      localDmParams1 = dmSizes[k];
      j = getEncodation(paramArrayOfByte, paramInt1 + this.extOut, paramInt2 - this.extOut, arrayOfByte, i, localDmParams1.dataSize - i, this.options, true);
      if (j < 0)
        return 1;
      j += i;
    }
    if ((this.options & 0x40) != 0)
      return 0;
    this.image = new byte[(localDmParams1.width + 2 * this.ws + 7) / 8 * (localDmParams1.height + 2 * this.ws)];
    makePadding(arrayOfByte, j, localDmParams1.dataSize - j);
    this.place = Placement.doPlacement(localDmParams1.height - localDmParams1.height / localDmParams1.heightSection * 2, localDmParams1.width - localDmParams1.width / localDmParams1.widthSection * 2);
    int m = localDmParams1.dataSize + (localDmParams1.dataSize + 2) / localDmParams1.dataBlock * localDmParams1.errorBlock;
    ReedSolomon.generateECC(arrayOfByte, localDmParams1.dataSize, localDmParams1.dataBlock, localDmParams1.errorBlock);
    draw(arrayOfByte, m, localDmParams1);
    return 0;
  }

  public com.lowagie.text.Image createImage()
    throws BadElementException
  {
    if (this.image == null)
      return null;
    byte[] arrayOfByte = CCITTG4Encoder.compress(this.image, this.width + 2 * this.ws, this.height + 2 * this.ws);
    return com.lowagie.text.Image.getInstance(this.width + 2 * this.ws, this.height + 2 * this.ws, false, 256, 0, arrayOfByte, null);
  }

  public java.awt.Image createAwtImage(Color paramColor1, Color paramColor2)
  {
    if (this.image == null)
      return null;
    int i = paramColor1.getRGB();
    int j = paramColor2.getRGB();
    Canvas localCanvas = new Canvas();
    int k = this.width + 2 * this.ws;
    int m = this.height + 2 * this.ws;
    int[] arrayOfInt = new int[k * m];
    int n = (k + 7) / 8;
    int i1 = 0;
    for (int i2 = 0; i2 < m; i2++)
    {
      int i3 = i2 * n;
      for (int i4 = 0; i4 < k; i4++)
      {
        int i5 = this.image[(i3 + i4 / 8)] & 0xFF;
        i5 <<= i4 % 8;
        arrayOfInt[(i1++)] = ((i5 & 0x80) == 0 ? j : i);
      }
    }
    java.awt.Image localImage = localCanvas.createImage(new MemoryImageSource(k, m, arrayOfInt, 0, k));
    return localImage;
  }

  public byte[] getImage()
  {
    return this.image;
  }

  public int getHeight()
  {
    return this.height;
  }

  public void setHeight(int paramInt)
  {
    this.height = paramInt;
  }

  public int getWidth()
  {
    return this.width;
  }

  public void setWidth(int paramInt)
  {
    this.width = paramInt;
  }

  public int getWs()
  {
    return this.ws;
  }

  public void setWs(int paramInt)
  {
    this.ws = paramInt;
  }

  public int getOptions()
  {
    return this.options;
  }

  public void setOptions(int paramInt)
  {
    this.options = paramInt;
  }

  static class ReedSolomon
  {
    private static final int[] log = { 0, 255, 1, 240, 2, 225, 241, 53, 3, 38, 226, 133, 242, 43, 54, 210, 4, 195, 39, 114, 227, 106, 134, 28, 243, 140, 44, 23, 55, 118, 211, 234, 5, 219, 196, 96, 40, 222, 115, 103, 228, 78, 107, 125, 135, 8, 29, 162, 244, 186, 141, 180, 45, 99, 24, 49, 56, 13, 119, 153, 212, 199, 235, 91, 6, 76, 220, 217, 197, 11, 97, 184, 41, 36, 223, 253, 116, 138, 104, 193, 229, 86, 79, 171, 108, 165, 126, 145, 136, 34, 9, 74, 30, 32, 163, 84, 245, 173, 187, 204, 142, 81, 181, 190, 46, 88, 100, 159, 25, 231, 50, 207, 57, 147, 14, 67, 120, 128, 154, 248, 213, 167, 200, 63, 236, 110, 92, 176, 7, 161, 77, 124, 221, 102, 218, 95, 198, 90, 12, 152, 98, 48, 185, 179, 42, 209, 37, 132, 224, 52, 254, 239, 117, 233, 139, 22, 105, 27, 194, 113, 230, 206, 87, 158, 80, 189, 172, 203, 109, 175, 166, 62, 127, 247, 146, 66, 137, 192, 35, 252, 10, 183, 75, 216, 31, 83, 33, 73, 164, 144, 85, 170, 246, 65, 174, 61, 188, 202, 205, 157, 143, 169, 82, 72, 182, 215, 191, 251, 47, 178, 89, 151, 101, 94, 160, 123, 26, 112, 232, 21, 51, 238, 208, 131, 58, 69, 148, 18, 15, 16, 68, 17, 121, 149, 129, 19, 155, 59, 249, 70, 214, 250, 168, 71, 201, 156, 64, 60, 237, 130, 111, 20, 93, 122, 177, 150 };
    private static final int[] alog = { 1, 2, 4, 8, 16, 32, 64, 128, 45, 90, 180, 69, 138, 57, 114, 228, 229, 231, 227, 235, 251, 219, 155, 27, 54, 108, 216, 157, 23, 46, 92, 184, 93, 186, 89, 178, 73, 146, 9, 18, 36, 72, 144, 13, 26, 52, 104, 208, 141, 55, 110, 220, 149, 7, 14, 28, 56, 112, 224, 237, 247, 195, 171, 123, 246, 193, 175, 115, 230, 225, 239, 243, 203, 187, 91, 182, 65, 130, 41, 82, 164, 101, 202, 185, 95, 190, 81, 162, 105, 210, 137, 63, 126, 252, 213, 135, 35, 70, 140, 53, 106, 212, 133, 39, 78, 156, 21, 42, 84, 168, 125, 250, 217, 159, 19, 38, 76, 152, 29, 58, 116, 232, 253, 215, 131, 43, 86, 172, 117, 234, 249, 223, 147, 11, 22, 44, 88, 176, 77, 154, 25, 50, 100, 200, 189, 87, 174, 113, 226, 233, 255, 211, 139, 59, 118, 236, 245, 199, 163, 107, 214, 129, 47, 94, 188, 85, 170, 121, 242, 201, 191, 83, 166, 97, 194, 169, 127, 254, 209, 143, 51, 102, 204, 181, 71, 142, 49, 98, 196, 165, 103, 206, 177, 79, 158, 17, 34, 68, 136, 61, 122, 244, 197, 167, 99, 198, 161, 111, 222, 145, 15, 30, 60, 120, 240, 205, 183, 67, 134, 33, 66, 132, 37, 74, 148, 5, 10, 20, 40, 80, 160, 109, 218, 153, 31, 62, 124, 248, 221, 151, 3, 6, 12, 24, 48, 96, 192, 173, 119, 238, 241, 207, 179, 75, 150, 1 };
    private static final int[] poly5 = { 228, 48, 15, 111, 62 };
    private static final int[] poly7 = { 23, 68, 144, 134, 240, 92, 254 };
    private static final int[] poly10 = { 28, 24, 185, 166, 223, 248, 116, 255, 110, 61 };
    private static final int[] poly11 = { 175, 138, 205, 12, 194, 168, 39, 245, 60, 97, 120 };
    private static final int[] poly12 = { 41, 153, 158, 91, 61, 42, 142, 213, 97, 178, 100, 242 };
    private static final int[] poly14 = { 156, 97, 192, 252, 95, 9, 157, 119, 138, 45, 18, 186, 83, 185 };
    private static final int[] poly18 = { 83, 195, 100, 39, 188, 75, 66, 61, 241, 213, 109, 129, 94, 254, 225, 48, 90, 188 };
    private static final int[] poly20 = { 15, 195, 244, 9, 233, 71, 168, 2, 188, 160, 153, 145, 253, 79, 108, 82, 27, 174, 186, 172 };
    private static final int[] poly24 = { 52, 190, 88, 205, 109, 39, 176, 21, 155, 197, 251, 223, 155, 21, 5, 172, 254, 124, 12, 181, 184, 96, 50, 193 };
    private static final int[] poly28 = { 211, 231, 43, 97, 71, 96, 103, 174, 37, 151, 170, 53, 75, 34, 249, 121, 17, 138, 110, 213, 141, 136, 120, 151, 233, 168, 93, 255 };
    private static final int[] poly36 = { 245, 127, 242, 218, 130, 250, 162, 181, 102, 120, 84, 179, 220, 251, 80, 182, 229, 18, 2, 4, 68, 33, 101, 137, 95, 119, 115, 44, 175, 184, 59, 25, 225, 98, 81, 112 };
    private static final int[] poly42 = { 77, 193, 137, 31, 19, 38, 22, 153, 247, 105, 122, 2, 245, 133, 242, 8, 175, 95, 100, 9, 167, 105, 214, 111, 57, 121, 21, 1, 253, 57, 54, 101, 248, 202, 69, 50, 150, 177, 226, 5, 9, 5 };
    private static final int[] poly48 = { 245, 132, 172, 223, 96, 32, 117, 22, 238, 133, 238, 231, 205, 188, 237, 87, 191, 106, 16, 147, 118, 23, 37, 90, 170, 205, 131, 88, 120, 100, 66, 138, 186, 240, 82, 44, 176, 87, 187, 147, 160, 175, 69, 213, 92, 253, 225, 19 };
    private static final int[] poly56 = { 175, 9, 223, 238, 12, 17, 220, 208, 100, 29, 175, 170, 230, 192, 215, 235, 150, 159, 36, 223, 38, 200, 132, 54, 228, 146, 218, 234, 117, 203, 29, 232, 144, 238, 22, 150, 201, 117, 62, 207, 164, 13, 137, 245, 127, 67, 247, 28, 155, 43, 203, 107, 233, 53, 143, 46 };
    private static final int[] poly62 = { 242, 93, 169, 50, 144, 210, 39, 118, 202, 188, 201, 189, 143, 108, 196, 37, 185, 112, 134, 230, 245, 63, 197, 190, 250, 106, 185, 221, 175, 64, 114, 71, 161, 44, 147, 6, 27, 218, 51, 63, 87, 10, 40, 130, 188, 17, 163, 31, 176, 170, 4, 107, 232, 7, 94, 166, 224, 124, 86, 47, 11, 204 };
    private static final int[] poly68 = { 220, 228, 173, 89, 251, 149, 159, 56, 89, 33, 147, 244, 154, 36, 73, 127, 213, 136, 248, 180, 234, 197, 158, 177, 68, 122, 93, 213, 15, 160, 227, 236, 66, 139, 153, 185, 202, 167, 179, 25, 220, 232, 96, 210, 231, 136, 223, 239, 181, 241, 59, 52, 172, 25, 49, 232, 211, 189, 64, 54, 108, 153, 132, 63, 96, 103, 82, 186 };

    private static int[] getPoly(int paramInt)
    {
      switch (paramInt)
      {
      case 5:
        return poly5;
      case 7:
        return poly7;
      case 10:
        return poly10;
      case 11:
        return poly11;
      case 12:
        return poly12;
      case 14:
        return poly14;
      case 18:
        return poly18;
      case 20:
        return poly20;
      case 24:
        return poly24;
      case 28:
        return poly28;
      case 36:
        return poly36;
      case 42:
        return poly42;
      case 48:
        return poly48;
      case 56:
        return poly56;
      case 62:
        return poly62;
      case 68:
        return poly68;
      case 6:
      case 8:
      case 9:
      case 13:
      case 15:
      case 16:
      case 17:
      case 19:
      case 21:
      case 22:
      case 23:
      case 25:
      case 26:
      case 27:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 57:
      case 58:
      case 59:
      case 60:
      case 61:
      case 63:
      case 64:
      case 65:
      case 66:
      case 67:
      }
      return null;
    }

    private static void reedSolomonBlock(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int[] paramArrayOfInt)
    {
      for (int i = 0; i <= paramInt2; i++)
        paramArrayOfByte2[i] = 0;
      for (i = 0; i < paramInt1; i++)
      {
        int k = (paramArrayOfByte2[0] ^ paramArrayOfByte1[i]) & 0xFF;
        for (int j = 0; j < paramInt2; j++)
          paramArrayOfByte2[j] = (byte)(paramArrayOfByte2[(j + 1)] ^ (k == 0 ? 0 : (byte)alog[((log[k] + log[paramArrayOfInt[(paramInt2 - j - 1)]]) % 255)]));
      }
    }

    static void generateECC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    {
      int i = (paramInt1 + 2) / paramInt2;
      byte[] arrayOfByte1 = new byte[256];
      byte[] arrayOfByte2 = new byte[256];
      int[] arrayOfInt = getPoly(paramInt3);
      for (int j = 0; j < i; j++)
      {
        int m = 0;
        int k = j;
        while (k < paramInt1)
        {
          arrayOfByte1[(m++)] = paramArrayOfByte[k];
          k += i;
        }
        reedSolomonBlock(arrayOfByte1, m, arrayOfByte2, paramInt3, arrayOfInt);
        m = 0;
        k = j;
        while (k < paramInt3 * i)
        {
          paramArrayOfByte[(paramInt1 + k)] = arrayOfByte2[(m++)];
          k += i;
        }
      }
    }
  }

  static class Placement
  {
    private int nrow;
    private int ncol;
    private short[] array;
    private static final Hashtable cache = new Hashtable();

    static short[] doPlacement(int paramInt1, int paramInt2)
    {
      Integer localInteger = new Integer(paramInt1 * 1000 + paramInt2);
      short[] arrayOfShort = (short[])cache.get(localInteger);
      if (arrayOfShort != null)
        return arrayOfShort;
      Placement localPlacement = new Placement();
      localPlacement.nrow = paramInt1;
      localPlacement.ncol = paramInt2;
      localPlacement.array = new short[paramInt1 * paramInt2];
      localPlacement.ecc200();
      cache.put(localInteger, localPlacement.array);
      return localPlacement.array;
    }

    private void module(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramInt1 < 0)
      {
        paramInt1 += this.nrow;
        paramInt2 += 4 - (this.nrow + 4) % 8;
      }
      if (paramInt2 < 0)
      {
        paramInt2 += this.ncol;
        paramInt1 += 4 - (this.ncol + 4) % 8;
      }
      this.array[(paramInt1 * this.ncol + paramInt2)] = (short)(8 * paramInt3 + paramInt4);
    }

    private void utah(int paramInt1, int paramInt2, int paramInt3)
    {
      module(paramInt1 - 2, paramInt2 - 2, paramInt3, 0);
      module(paramInt1 - 2, paramInt2 - 1, paramInt3, 1);
      module(paramInt1 - 1, paramInt2 - 2, paramInt3, 2);
      module(paramInt1 - 1, paramInt2 - 1, paramInt3, 3);
      module(paramInt1 - 1, paramInt2, paramInt3, 4);
      module(paramInt1, paramInt2 - 2, paramInt3, 5);
      module(paramInt1, paramInt2 - 1, paramInt3, 6);
      module(paramInt1, paramInt2, paramInt3, 7);
    }

    private void corner1(int paramInt)
    {
      module(this.nrow - 1, 0, paramInt, 0);
      module(this.nrow - 1, 1, paramInt, 1);
      module(this.nrow - 1, 2, paramInt, 2);
      module(0, this.ncol - 2, paramInt, 3);
      module(0, this.ncol - 1, paramInt, 4);
      module(1, this.ncol - 1, paramInt, 5);
      module(2, this.ncol - 1, paramInt, 6);
      module(3, this.ncol - 1, paramInt, 7);
    }

    private void corner2(int paramInt)
    {
      module(this.nrow - 3, 0, paramInt, 0);
      module(this.nrow - 2, 0, paramInt, 1);
      module(this.nrow - 1, 0, paramInt, 2);
      module(0, this.ncol - 4, paramInt, 3);
      module(0, this.ncol - 3, paramInt, 4);
      module(0, this.ncol - 2, paramInt, 5);
      module(0, this.ncol - 1, paramInt, 6);
      module(1, this.ncol - 1, paramInt, 7);
    }

    private void corner3(int paramInt)
    {
      module(this.nrow - 3, 0, paramInt, 0);
      module(this.nrow - 2, 0, paramInt, 1);
      module(this.nrow - 1, 0, paramInt, 2);
      module(0, this.ncol - 2, paramInt, 3);
      module(0, this.ncol - 1, paramInt, 4);
      module(1, this.ncol - 1, paramInt, 5);
      module(2, this.ncol - 1, paramInt, 6);
      module(3, this.ncol - 1, paramInt, 7);
    }

    private void corner4(int paramInt)
    {
      module(this.nrow - 1, 0, paramInt, 0);
      module(this.nrow - 1, this.ncol - 1, paramInt, 1);
      module(0, this.ncol - 3, paramInt, 2);
      module(0, this.ncol - 2, paramInt, 3);
      module(0, this.ncol - 1, paramInt, 4);
      module(1, this.ncol - 3, paramInt, 5);
      module(1, this.ncol - 2, paramInt, 6);
      module(1, this.ncol - 1, paramInt, 7);
    }

    private void ecc200()
    {
      Arrays.fill(this.array, 0);
      int k = 1;
      int i = 4;
      int j = 0;
      do
      {
        if ((i == this.nrow) && (j == 0))
          corner1(k++);
        if ((i == this.nrow - 2) && (j == 0) && (this.ncol % 4 != 0))
          corner2(k++);
        if ((i == this.nrow - 2) && (j == 0) && (this.ncol % 8 == 4))
          corner3(k++);
        if ((i == this.nrow + 4) && (j == 2) && (this.ncol % 8 == 0))
          corner4(k++);
        do
        {
          if ((i < this.nrow) && (j >= 0) && (this.array[(i * this.ncol + j)] == 0))
            utah(i, j, k++);
          i -= 2;
          j += 2;
        }
        while ((i >= 0) && (j < this.ncol));
        i++;
        j += 3;
        do
        {
          if ((i >= 0) && (j < this.ncol) && (this.array[(i * this.ncol + j)] == 0))
            utah(i, j, k++);
          i += 2;
          j -= 2;
        }
        while ((i < this.nrow) && (j >= 0));
        i += 3;
        j++;
      }
      while ((i < this.nrow) || (j < this.ncol));
      if (this.array[(this.nrow * this.ncol - 1)] == 0)
      {
        int tmp326_325 = 1;
        this.array[(this.nrow * this.ncol - this.ncol - 2)] = tmp326_325;
        this.array[(this.nrow * this.ncol - 1)] = tmp326_325;
      }
    }
  }

  private static class DmParams
  {
    int height;
    int width;
    int heightSection;
    int widthSection;
    int dataSize;
    int dataBlock;
    int errorBlock;

    DmParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      this.height = paramInt1;
      this.width = paramInt2;
      this.heightSection = paramInt3;
      this.widthSection = paramInt4;
      this.dataSize = paramInt5;
      this.dataBlock = paramInt6;
      this.errorBlock = paramInt7;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BarcodeDatamatrix
 * JD-Core Version:    0.6.0
 */