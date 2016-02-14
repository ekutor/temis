package com.lowagie.text.pdf.codec;

public class TIFFFaxDecoder
{
  private int bitPointer;
  private int bytePointer;
  private byte[] data;
  private int w;
  private int h;
  private int fillOrder;
  private int changingElemSize = 0;
  private int[] prevChangingElems;
  private int[] currChangingElems;
  private int lastChangingElement = 0;
  private int compression = 2;
  private int uncompressedMode = 0;
  private int fillBits = 0;
  private int oneD;
  static int[] table1 = { 0, 1, 3, 7, 15, 31, 63, 127, 255 };
  static int[] table2 = { 0, 128, 192, 224, 240, 248, 252, 254, 255 };
  static byte[] flipTable = { 0, -128, 64, -64, 32, -96, 96, -32, 16, -112, 80, -48, 48, -80, 112, -16, 8, -120, 72, -56, 40, -88, 104, -24, 24, -104, 88, -40, 56, -72, 120, -8, 4, -124, 68, -60, 36, -92, 100, -28, 20, -108, 84, -44, 52, -76, 116, -12, 12, -116, 76, -52, 44, -84, 108, -20, 28, -100, 92, -36, 60, -68, 124, -4, 2, -126, 66, -62, 34, -94, 98, -30, 18, -110, 82, -46, 50, -78, 114, -14, 10, -118, 74, -54, 42, -86, 106, -22, 26, -102, 90, -38, 58, -70, 122, -6, 6, -122, 70, -58, 38, -90, 102, -26, 22, -106, 86, -42, 54, -74, 118, -10, 14, -114, 78, -50, 46, -82, 110, -18, 30, -98, 94, -34, 62, -66, 126, -2, 1, -127, 65, -63, 33, -95, 97, -31, 17, -111, 81, -47, 49, -79, 113, -15, 9, -119, 73, -55, 41, -87, 105, -23, 25, -103, 89, -39, 57, -71, 121, -7, 5, -123, 69, -59, 37, -91, 101, -27, 21, -107, 85, -43, 53, -75, 117, -11, 13, -115, 77, -51, 45, -83, 109, -19, 29, -99, 93, -35, 61, -67, 125, -3, 3, -125, 67, -61, 35, -93, 99, -29, 19, -109, 83, -45, 51, -77, 115, -13, 11, -117, 75, -53, 43, -85, 107, -21, 27, -101, 91, -37, 59, -69, 123, -5, 7, -121, 71, -57, 39, -89, 103, -25, 23, -105, 87, -41, 55, -73, 119, -9, 15, -113, 79, -49, 47, -81, 111, -17, 31, -97, 95, -33, 63, -65, 127, -1 };
  static short[] white = { 6430, 6400, 6400, 6400, 3225, 3225, 3225, 3225, 944, 944, 944, 944, 976, 976, 976, 976, 1456, 1456, 1456, 1456, 1488, 1488, 1488, 1488, 718, 718, 718, 718, 718, 718, 718, 718, 750, 750, 750, 750, 750, 750, 750, 750, 1520, 1520, 1520, 1520, 1552, 1552, 1552, 1552, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 654, 654, 654, 654, 654, 654, 654, 654, 1072, 1072, 1072, 1072, 1104, 1104, 1104, 1104, 1136, 1136, 1136, 1136, 1168, 1168, 1168, 1168, 1200, 1200, 1200, 1200, 1232, 1232, 1232, 1232, 622, 622, 622, 622, 622, 622, 622, 622, 1008, 1008, 1008, 1008, 1040, 1040, 1040, 1040, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 1712, 1712, 1712, 1712, 1744, 1744, 1744, 1744, 846, 846, 846, 846, 846, 846, 846, 846, 1264, 1264, 1264, 1264, 1296, 1296, 1296, 1296, 1328, 1328, 1328, 1328, 1360, 1360, 1360, 1360, 1392, 1392, 1392, 1392, 1424, 1424, 1424, 1424, 686, 686, 686, 686, 686, 686, 686, 686, 910, 910, 910, 910, 910, 910, 910, 910, 1968, 1968, 1968, 1968, 2000, 2000, 2000, 2000, 2032, 2032, 2032, 2032, 16, 16, 16, 16, 10257, 10257, 10257, 10257, 12305, 12305, 12305, 12305, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 878, 878, 878, 878, 878, 878, 878, 878, 1904, 1904, 1904, 1904, 1936, 1936, 1936, 1936, -18413, -18413, -16365, -16365, -14317, -14317, -10221, -10221, 590, 590, 590, 590, 590, 590, 590, 590, 782, 782, 782, 782, 782, 782, 782, 782, 1584, 1584, 1584, 1584, 1616, 1616, 1616, 1616, 1648, 1648, 1648, 1648, 1680, 1680, 1680, 1680, 814, 814, 814, 814, 814, 814, 814, 814, 1776, 1776, 1776, 1776, 1808, 1808, 1808, 1808, 1840, 1840, 1840, 1840, 1872, 1872, 1872, 1872, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, 14353, 14353, 14353, 14353, 16401, 16401, 16401, 16401, 22547, 22547, 24595, 24595, 20497, 20497, 20497, 20497, 18449, 18449, 18449, 18449, 26643, 26643, 28691, 28691, 30739, 30739, -32749, -32749, -30701, -30701, -28653, -28653, -26605, -26605, -24557, -24557, -22509, -22509, -20461, -20461, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232 };
  static short[] additionalMakeup = { 28679, 28679, 31752, -32759, -31735, -30711, -29687, -28663, 29703, 29703, 30727, 30727, -27639, -26615, -25591, -24567 };
  static short[] initBlack = { 3226, 6412, 200, 168, 38, 38, 134, 134, 100, 100, 100, 100, 68, 68, 68, 68 };
  static short[] twoBitBlack = { 292, 260, 226, 226 };
  static short[] black = { 62, 62, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 588, 588, 588, 588, 588, 588, 588, 588, 1680, 1680, 20499, 22547, 24595, 26643, 1776, 1776, 1808, 1808, -24557, -22509, -20461, -18413, 1904, 1904, 1936, 1936, -16365, -14317, 782, 782, 782, 782, 814, 814, 814, 814, -12269, -10221, 10257, 10257, 12305, 12305, 14353, 14353, 16403, 18451, 1712, 1712, 1744, 1744, 28691, 30739, -32749, -30701, -28653, -26605, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 750, 750, 750, 750, 1616, 1616, 1648, 1648, 1424, 1424, 1456, 1456, 1488, 1488, 1520, 1520, 1840, 1840, 1872, 1872, 1968, 1968, 8209, 8209, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 1552, 1552, 1584, 1584, 2000, 2000, 2032, 2032, 976, 976, 1008, 1008, 1040, 1040, 1072, 1072, 1296, 1296, 1328, 1328, 718, 718, 718, 718, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 4113, 4113, 6161, 6161, 848, 848, 880, 880, 912, 912, 944, 944, 622, 622, 622, 622, 654, 654, 654, 654, 1104, 1104, 1136, 1136, 1168, 1168, 1200, 1200, 1232, 1232, 1264, 1264, 686, 686, 686, 686, 1360, 1360, 1392, 1392, 12, 12, 12, 12, 12, 12, 12, 12, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390 };
  static byte[] twoDCodes = { 80, 88, 23, 71, 30, 30, 62, 62, 4, 4, 4, 4, 4, 4, 4, 4, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41 };

  public TIFFFaxDecoder(int paramInt1, int paramInt2, int paramInt3)
  {
    this.fillOrder = paramInt1;
    this.w = paramInt2;
    this.h = paramInt3;
    this.bitPointer = 0;
    this.bytePointer = 0;
    this.prevChangingElems = new int[paramInt2];
    this.currChangingElems = new int[paramInt2];
  }

  public static void reverseBits(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i++)
      paramArrayOfByte[i] = flipTable[(paramArrayOfByte[i] & 0xFF)];
  }

  public void decode1D(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
  {
    this.data = paramArrayOfByte2;
    int i = 0;
    int j = (this.w + 7) / 8;
    this.bitPointer = 0;
    this.bytePointer = 0;
    for (int k = 0; k < paramInt2; k++)
    {
      decodeNextScanline(paramArrayOfByte1, i, paramInt1);
      i += j;
    }
  }

  public void decodeNextScanline(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int i2 = 1;
    this.changingElemSize = 0;
    while (paramInt2 < this.w)
    {
      int m;
      int n;
      while (i2 != 0)
      {
        m = nextNBits(10);
        n = white[m];
        k = n & 0x1;
        i = n >>> 1 & 0xF;
        if (i == 12)
        {
          int i1 = nextLesserThan8Bits(2);
          m = m << 2 & 0xC | i1;
          n = additionalMakeup[m];
          i = n >>> 1 & 0x7;
          j = n >>> 4 & 0xFFF;
          paramInt2 += j;
          updatePointer(4 - i);
          continue;
        }
        if (i == 0)
          throw new RuntimeException("Invalid code encountered.");
        if (i == 15)
          throw new RuntimeException("EOL code word encountered in White run.");
        j = n >>> 5 & 0x7FF;
        paramInt2 += j;
        updatePointer(10 - i);
        if (k != 0)
          continue;
        i2 = 0;
        this.currChangingElems[(this.changingElemSize++)] = paramInt2;
      }
      if (paramInt2 == this.w)
      {
        if (this.compression != 2)
          break;
        advancePointer();
      }
      else
      {
        while (i2 == 0)
        {
          m = nextLesserThan8Bits(4);
          n = initBlack[m];
          k = n & 0x1;
          i = n >>> 1 & 0xF;
          j = n >>> 5 & 0x7FF;
          if (j == 100)
          {
            m = nextNBits(9);
            n = black[m];
            k = n & 0x1;
            i = n >>> 1 & 0xF;
            j = n >>> 5 & 0x7FF;
            if (i == 12)
            {
              updatePointer(5);
              m = nextLesserThan8Bits(4);
              n = additionalMakeup[m];
              i = n >>> 1 & 0x7;
              j = n >>> 4 & 0xFFF;
              setToBlack(paramArrayOfByte, paramInt1, paramInt2, j);
              paramInt2 += j;
              updatePointer(4 - i);
              continue;
            }
            if (i == 15)
              throw new RuntimeException("EOL code word encountered in Black run.");
            setToBlack(paramArrayOfByte, paramInt1, paramInt2, j);
            paramInt2 += j;
            updatePointer(9 - i);
            if (k != 0)
              continue;
            i2 = 1;
            this.currChangingElems[(this.changingElemSize++)] = paramInt2;
            continue;
          }
          if (j == 200)
          {
            m = nextLesserThan8Bits(2);
            n = twoBitBlack[m];
            j = n >>> 5 & 0x7FF;
            i = n >>> 1 & 0xF;
            setToBlack(paramArrayOfByte, paramInt1, paramInt2, j);
            paramInt2 += j;
            updatePointer(2 - i);
            i2 = 1;
            this.currChangingElems[(this.changingElemSize++)] = paramInt2;
            continue;
          }
          setToBlack(paramArrayOfByte, paramInt1, paramInt2, j);
          paramInt2 += j;
          updatePointer(4 - i);
          i2 = 1;
          this.currChangingElems[(this.changingElemSize++)] = paramInt2;
        }
        if (paramInt2 != this.w)
          continue;
        if (this.compression != 2)
          break;
        advancePointer();
      }
    }
    this.currChangingElems[(this.changingElemSize++)] = paramInt2;
  }

  public void decode2D(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, long paramLong)
  {
    this.data = paramArrayOfByte2;
    this.compression = 3;
    this.bitPointer = 0;
    this.bytePointer = 0;
    int i = (this.w + 7) / 8;
    int[] arrayOfInt1 = new int[2];
    int i4 = 0;
    this.oneD = (int)(paramLong & 1L);
    this.uncompressedMode = (int)((paramLong & 0x2) >> 1);
    this.fillBits = (int)((paramLong & 0x4) >> 2);
    if (readEOL(true) != 1)
      throw new RuntimeException("First scanline must be 1D encoded.");
    int i5 = 0;
    decodeNextScanline(paramArrayOfByte1, i5, paramInt1);
    i5 += i;
    for (int i7 = 1; i7 < paramInt2; i7++)
    {
      if (readEOL(false) == 0)
      {
        int[] arrayOfInt2 = this.prevChangingElems;
        this.prevChangingElems = this.currChangingElems;
        this.currChangingElems = arrayOfInt2;
        i4 = 0;
        int j = -1;
        boolean bool = true;
        int i6 = paramInt1;
        this.lastChangingElement = 0;
        while (i6 < this.w)
        {
          getNextChangingElement(j, bool, arrayOfInt1);
          int m = arrayOfInt1[0];
          int n = arrayOfInt1[1];
          int i1 = nextLesserThan8Bits(7);
          i1 = twoDCodes[i1] & 0xFF;
          int i2 = (i1 & 0x78) >>> 3;
          int i3 = i1 & 0x7;
          if (i2 == 0)
          {
            if (!bool)
              setToBlack(paramArrayOfByte1, i5, i6, n - i6);
            i6 = j = n;
            updatePointer(7 - i3);
            continue;
          }
          if (i2 == 1)
          {
            updatePointer(7 - i3);
            int i8;
            if (bool)
            {
              i8 = decodeWhiteCodeWord();
              i6 += i8;
              this.currChangingElems[(i4++)] = i6;
              i8 = decodeBlackCodeWord();
              setToBlack(paramArrayOfByte1, i5, i6, i8);
              i6 += i8;
              this.currChangingElems[(i4++)] = i6;
            }
            else
            {
              i8 = decodeBlackCodeWord();
              setToBlack(paramArrayOfByte1, i5, i6, i8);
              i6 += i8;
              this.currChangingElems[(i4++)] = i6;
              i8 = decodeWhiteCodeWord();
              i6 += i8;
              this.currChangingElems[(i4++)] = i6;
            }
            j = i6;
            continue;
          }
          if (i2 <= 8)
          {
            int k = m + (i2 - 5);
            this.currChangingElems[(i4++)] = k;
            if (!bool)
              setToBlack(paramArrayOfByte1, i5, i6, k - i6);
            i6 = j = k;
            bool = !bool;
            updatePointer(7 - i3);
            continue;
          }
          throw new RuntimeException("Invalid code encountered while decoding 2D group 3 compressed data.");
        }
        this.currChangingElems[(i4++)] = i6;
        this.changingElemSize = i4;
      }
      else
      {
        decodeNextScanline(paramArrayOfByte1, i5, paramInt1);
      }
      i5 += i;
    }
  }

  public void decodeT6(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, long paramLong)
  {
    this.data = paramArrayOfByte2;
    this.compression = 4;
    this.bitPointer = 0;
    this.bytePointer = 0;
    int i = (this.w + 7) / 8;
    int[] arrayOfInt2 = new int[2];
    this.uncompressedMode = (int)((paramLong & 0x2) >> 1);
    Object localObject = this.currChangingElems;
    this.changingElemSize = 0;
    localObject[(this.changingElemSize++)] = this.w;
    localObject[(this.changingElemSize++)] = this.w;
    int i5 = 0;
    for (int i7 = 0; i7 < paramInt2; i7++)
    {
      int j = -1;
      boolean bool = true;
      int[] arrayOfInt1 = this.prevChangingElems;
      this.prevChangingElems = this.currChangingElems;
      localObject = this.currChangingElems = arrayOfInt1;
      int i4 = 0;
      int i6 = paramInt1;
      this.lastChangingElement = 0;
      while (i6 < this.w)
      {
        getNextChangingElement(j, bool, arrayOfInt2);
        int m = arrayOfInt2[0];
        int n = arrayOfInt2[1];
        int i1 = nextLesserThan8Bits(7);
        i1 = twoDCodes[i1] & 0xFF;
        int i2 = (i1 & 0x78) >>> 3;
        int i3 = i1 & 0x7;
        if (i2 == 0)
        {
          if (!bool)
            setToBlack(paramArrayOfByte1, i5, i6, n - i6);
          i6 = j = n;
          updatePointer(7 - i3);
          continue;
        }
        int i8;
        if (i2 == 1)
        {
          updatePointer(7 - i3);
          if (bool)
          {
            i8 = decodeWhiteCodeWord();
            i6 += i8;
            localObject[(i4++)] = i6;
            i8 = decodeBlackCodeWord();
            setToBlack(paramArrayOfByte1, i5, i6, i8);
            i6 += i8;
            localObject[(i4++)] = i6;
          }
          else
          {
            i8 = decodeBlackCodeWord();
            setToBlack(paramArrayOfByte1, i5, i6, i8);
            i6 += i8;
            localObject[(i4++)] = i6;
            i8 = decodeWhiteCodeWord();
            i6 += i8;
            localObject[(i4++)] = i6;
          }
          j = i6;
          continue;
        }
        if (i2 <= 8)
        {
          int k = m + (i2 - 5);
          localObject[(i4++)] = k;
          if (!bool)
            setToBlack(paramArrayOfByte1, i5, i6, k - i6);
          i6 = j = k;
          bool = !bool;
          updatePointer(7 - i3);
          continue;
        }
        if (i2 == 11)
        {
          if (nextLesserThan8Bits(3) != 7)
            throw new RuntimeException("Invalid code encountered while decoding 2D group 4 compressed data.");
          i8 = 0;
          int i9 = 0;
          while (i9 == 0)
          {
            while (nextLesserThan8Bits(1) != 1)
              i8++;
            if (i8 > 5)
            {
              i8 -= 6;
              if ((!bool) && (i8 > 0))
                localObject[(i4++)] = i6;
              i6 += i8;
              if (i8 > 0)
                bool = true;
              if (nextLesserThan8Bits(1) == 0)
              {
                if (!bool)
                  localObject[(i4++)] = i6;
                bool = true;
              }
              else
              {
                if (bool)
                  localObject[(i4++)] = i6;
                bool = false;
              }
              i9 = 1;
            }
            if (i8 == 5)
            {
              if (!bool)
                localObject[(i4++)] = i6;
              i6 += i8;
              bool = true;
              continue;
            }
            i6 += i8;
            localObject[(i4++)] = i6;
            setToBlack(paramArrayOfByte1, i5, i6, 1);
            i6++;
            bool = false;
          }
          continue;
        }
        i6 = this.w;
        updatePointer(7 - i3);
      }
      if (i4 < localObject.length)
        localObject[(i4++)] = i6;
      this.changingElemSize = i4;
      i5 += i;
    }
  }

  private void setToBlack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 8 * paramInt1 + paramInt2;
    int j = i + paramInt3;
    int k = i >> 3;
    int m = i & 0x7;
    if (m > 0)
    {
      int n = 1 << 7 - m;
      int i1 = paramArrayOfByte[k];
      while ((n > 0) && (i < j))
      {
        i1 = (byte)(i1 | n);
        n >>= 1;
        i++;
      }
      paramArrayOfByte[k] = i1;
    }
    k = i >> 3;
    while (i < j - 7)
    {
      paramArrayOfByte[(k++)] = -1;
      i += 8;
    }
    while (i < j)
    {
      k = i >> 3;
      int tmp132_130 = k;
      byte[] tmp132_129 = paramArrayOfByte;
      tmp132_129[tmp132_130] = (byte)(tmp132_129[tmp132_130] | 1 << 7 - (i & 0x7));
      i++;
    }
  }

  private int decodeWhiteCodeWord()
  {
    int i1 = -1;
    int i2 = 0;
    int i3 = 1;
    while (i3 != 0)
    {
      int i = nextNBits(10);
      int j = white[i];
      int m = j & 0x1;
      int k = j >>> 1 & 0xF;
      if (k == 12)
      {
        int n = nextLesserThan8Bits(2);
        i = i << 2 & 0xC | n;
        j = additionalMakeup[i];
        k = j >>> 1 & 0x7;
        i1 = j >>> 4 & 0xFFF;
        i2 += i1;
        updatePointer(4 - k);
        continue;
      }
      if (k == 0)
        throw new RuntimeException("Invalid code encountered.");
      if (k == 15)
        throw new RuntimeException("EOL code word encountered in White run.");
      i1 = j >>> 5 & 0x7FF;
      i2 += i1;
      updatePointer(10 - k);
      if (m != 0)
        continue;
      i3 = 0;
    }
    return i2;
  }

  private int decodeBlackCodeWord()
  {
    int n = -1;
    int i1 = 0;
    int i2 = 0;
    while (i2 == 0)
    {
      int i = nextLesserThan8Bits(4);
      int j = initBlack[i];
      int m = j & 0x1;
      int k = j >>> 1 & 0xF;
      n = j >>> 5 & 0x7FF;
      if (n == 100)
      {
        i = nextNBits(9);
        j = black[i];
        m = j & 0x1;
        k = j >>> 1 & 0xF;
        n = j >>> 5 & 0x7FF;
        if (k == 12)
        {
          updatePointer(5);
          i = nextLesserThan8Bits(4);
          j = additionalMakeup[i];
          k = j >>> 1 & 0x7;
          n = j >>> 4 & 0xFFF;
          i1 += n;
          updatePointer(4 - k);
          continue;
        }
        if (k == 15)
          throw new RuntimeException("EOL code word encountered in Black run.");
        i1 += n;
        updatePointer(9 - k);
        if (m != 0)
          continue;
        i2 = 1;
        continue;
      }
      if (n == 200)
      {
        i = nextLesserThan8Bits(2);
        j = twoBitBlack[i];
        n = j >>> 5 & 0x7FF;
        i1 += n;
        k = j >>> 1 & 0xF;
        updatePointer(2 - k);
        i2 = 1;
        continue;
      }
      i1 += n;
      updatePointer(4 - k);
      i2 = 1;
    }
    return i1;
  }

  private int readEOL(boolean paramBoolean)
  {
    int i;
    if (this.fillBits == 0)
    {
      i = nextNBits(12);
      if ((paramBoolean) && (i == 0) && (nextNBits(4) == 1))
      {
        this.fillBits = 1;
        return 1;
      }
      if (i != 1)
        throw new RuntimeException("Scanline must begin with EOL code word.");
    }
    else if (this.fillBits == 1)
    {
      i = 8 - this.bitPointer;
      if (nextNBits(i) != 0)
        throw new RuntimeException("All fill bits preceding EOL code must be 0.");
      if ((i < 4) && (nextNBits(8) != 0))
        throw new RuntimeException("All fill bits preceding EOL code must be 0.");
      int j;
      while ((j = nextNBits(8)) != 1)
      {
        if (j == 0)
          continue;
        throw new RuntimeException("All fill bits preceding EOL code must be 0.");
      }
    }
    if (this.oneD == 0)
      return 1;
    return nextLesserThan8Bits(1);
  }

  private void getNextChangingElement(int paramInt, boolean paramBoolean, int[] paramArrayOfInt)
  {
    int[] arrayOfInt = this.prevChangingElems;
    int i = this.changingElemSize;
    int j = this.lastChangingElement > 0 ? this.lastChangingElement - 1 : 0;
    if (paramBoolean)
      j &= -2;
    else
      j |= 1;
    for (int k = j; k < i; k += 2)
    {
      int m = arrayOfInt[k];
      if (m <= paramInt)
        continue;
      this.lastChangingElement = k;
      paramArrayOfInt[0] = m;
      break;
    }
    if (k + 1 < i)
      paramArrayOfInt[1] = arrayOfInt[(k + 1)];
  }

  private int nextNBits(int paramInt)
  {
    int m = this.data.length - 1;
    int n = this.bytePointer;
    int i;
    int j;
    int k;
    if (this.fillOrder == 1)
    {
      i = this.data[n];
      if (n == m)
      {
        j = 0;
        k = 0;
      }
      else if (n + 1 == m)
      {
        j = this.data[(n + 1)];
        k = 0;
      }
      else
      {
        j = this.data[(n + 1)];
        k = this.data[(n + 2)];
      }
    }
    else if (this.fillOrder == 2)
    {
      i = flipTable[(this.data[n] & 0xFF)];
      if (n == m)
      {
        j = 0;
        k = 0;
      }
      else if (n + 1 == m)
      {
        j = flipTable[(this.data[(n + 1)] & 0xFF)];
        k = 0;
      }
      else
      {
        j = flipTable[(this.data[(n + 1)] & 0xFF)];
        k = flipTable[(this.data[(n + 2)] & 0xFF)];
      }
    }
    else
    {
      throw new RuntimeException("TIFF_FILL_ORDER tag must be either 1 or 2.");
    }
    int i1 = 8 - this.bitPointer;
    int i2 = paramInt - i1;
    int i3 = 0;
    if (i2 > 8)
    {
      i3 = i2 - 8;
      i2 = 8;
    }
    this.bytePointer += 1;
    int i4 = (i & table1[i1]) << paramInt - i1;
    int i5 = (j & table2[i2]) >>> 8 - i2;
    int i6 = 0;
    if (i3 != 0)
    {
      i5 <<= i3;
      i6 = (k & table2[i3]) >>> 8 - i3;
      i5 |= i6;
      this.bytePointer += 1;
      this.bitPointer = i3;
    }
    else if (i2 == 8)
    {
      this.bitPointer = 0;
      this.bytePointer += 1;
    }
    else
    {
      this.bitPointer = i2;
    }
    int i7 = i4 | i5;
    return i7;
  }

  private int nextLesserThan8Bits(int paramInt)
  {
    int k = this.data.length - 1;
    int m = this.bytePointer;
    int i;
    int j;
    if (this.fillOrder == 1)
    {
      i = this.data[m];
      if (m == k)
        j = 0;
      else
        j = this.data[(m + 1)];
    }
    else if (this.fillOrder == 2)
    {
      i = flipTable[(this.data[m] & 0xFF)];
      if (m == k)
        j = 0;
      else
        j = flipTable[(this.data[(m + 1)] & 0xFF)];
    }
    else
    {
      throw new RuntimeException("TIFF_FILL_ORDER tag must be either 1 or 2.");
    }
    int n = 8 - this.bitPointer;
    int i1 = paramInt - n;
    int i2 = n - paramInt;
    int i3;
    if (i2 >= 0)
    {
      i3 = (i & table1[n]) >>> i2;
      this.bitPointer += paramInt;
      if (this.bitPointer == 8)
      {
        this.bitPointer = 0;
        this.bytePointer += 1;
      }
    }
    else
    {
      i3 = (i & table1[n]) << -i2;
      int i4 = (j & table2[i1]) >>> 8 - i1;
      i3 |= i4;
      this.bytePointer += 1;
      this.bitPointer = i1;
    }
    return i3;
  }

  private void updatePointer(int paramInt)
  {
    int i = this.bitPointer - paramInt;
    if (i < 0)
    {
      this.bytePointer -= 1;
      this.bitPointer = (8 + i);
    }
    else
    {
      this.bitPointer = i;
    }
  }

  private boolean advancePointer()
  {
    if (this.bitPointer != 0)
    {
      this.bytePointer += 1;
      this.bitPointer = 0;
    }
    return true;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.TIFFFaxDecoder
 * JD-Core Version:    0.6.0
 */