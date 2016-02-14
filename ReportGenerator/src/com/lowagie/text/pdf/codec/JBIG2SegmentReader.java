package com.lowagie.text.pdf.codec;

import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class JBIG2SegmentReader
{
  public static final int SYMBOL_DICTIONARY = 0;
  public static final int INTERMEDIATE_TEXT_REGION = 4;
  public static final int IMMEDIATE_TEXT_REGION = 6;
  public static final int IMMEDIATE_LOSSLESS_TEXT_REGION = 7;
  public static final int PATTERN_DICTIONARY = 16;
  public static final int INTERMEDIATE_HALFTONE_REGION = 20;
  public static final int IMMEDIATE_HALFTONE_REGION = 22;
  public static final int IMMEDIATE_LOSSLESS_HALFTONE_REGION = 23;
  public static final int INTERMEDIATE_GENERIC_REGION = 36;
  public static final int IMMEDIATE_GENERIC_REGION = 38;
  public static final int IMMEDIATE_LOSSLESS_GENERIC_REGION = 39;
  public static final int INTERMEDIATE_GENERIC_REFINEMENT_REGION = 40;
  public static final int IMMEDIATE_GENERIC_REFINEMENT_REGION = 42;
  public static final int IMMEDIATE_LOSSLESS_GENERIC_REFINEMENT_REGION = 43;
  public static final int PAGE_INFORMATION = 48;
  public static final int END_OF_PAGE = 49;
  public static final int END_OF_STRIPE = 50;
  public static final int END_OF_FILE = 51;
  public static final int PROFILES = 52;
  public static final int TABLES = 53;
  public static final int EXTENSION = 62;
  private final SortedMap segments = new TreeMap();
  private final SortedMap pages = new TreeMap();
  private final SortedSet globals = new TreeSet();
  private RandomAccessFileOrArray ra;
  private boolean sequential;
  private boolean number_of_pages_known;
  private int number_of_pages = -1;
  private boolean read = false;

  public JBIG2SegmentReader(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    this.ra = paramRandomAccessFileOrArray;
  }

  public static byte[] copyByteArray(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
  }

  public void read()
    throws IOException
  {
    if (this.read)
      throw new IllegalStateException("already attempted a read() on this Jbig2 File");
    this.read = true;
    readFileHeader();
    JBIG2Segment localJBIG2Segment;
    if (this.sequential)
    {
      do
      {
        localJBIG2Segment = readHeader();
        readSegment(localJBIG2Segment);
        this.segments.put(new Integer(localJBIG2Segment.segmentNumber), localJBIG2Segment);
      }
      while (this.ra.getFilePointer() < this.ra.length());
    }
    else
    {
      do
      {
        localJBIG2Segment = readHeader();
        this.segments.put(new Integer(localJBIG2Segment.segmentNumber), localJBIG2Segment);
      }
      while (localJBIG2Segment.type != 51);
      Iterator localIterator = this.segments.keySet().iterator();
      while (localIterator.hasNext())
        readSegment((JBIG2Segment)this.segments.get(localIterator.next()));
    }
  }

  void readSegment(JBIG2Segment paramJBIG2Segment)
    throws IOException
  {
    int i = this.ra.getFilePointer();
    if (paramJBIG2Segment.dataLength == 4294967295L)
      return;
    byte[] arrayOfByte = new byte[(int)paramJBIG2Segment.dataLength];
    this.ra.read(arrayOfByte);
    paramJBIG2Segment.data = arrayOfByte;
    if (paramJBIG2Segment.type == 48)
    {
      int j = this.ra.getFilePointer();
      this.ra.seek(i);
      int k = this.ra.readInt();
      int m = this.ra.readInt();
      this.ra.seek(j);
      JBIG2Page localJBIG2Page = (JBIG2Page)this.pages.get(new Integer(paramJBIG2Segment.page));
      if (localJBIG2Page == null)
        throw new IllegalStateException("referring to widht/height of page we havent seen yet? " + paramJBIG2Segment.page);
      localJBIG2Page.pageBitmapWidth = k;
      localJBIG2Page.pageBitmapHeight = m;
    }
  }

  JBIG2Segment readHeader()
    throws IOException
  {
    int i = this.ra.getFilePointer();
    int j = this.ra.readInt();
    JBIG2Segment localJBIG2Segment = new JBIG2Segment(j);
    int k = this.ra.read();
    boolean bool1 = (k & 0x80) == 128;
    localJBIG2Segment.deferredNonRetain = bool1;
    boolean bool2 = (k & 0x40) == 64;
    int m = k & 0x3F;
    localJBIG2Segment.type = m;
    int n = this.ra.read();
    int i1 = (n & 0xE0) >> 5;
    int[] arrayOfInt = null;
    boolean[] arrayOfBoolean = null;
    if (i1 == 7)
    {
      this.ra.seek(this.ra.getFilePointer() - 1);
      i1 = this.ra.readInt() & 0x1FFFFFFF;
      arrayOfBoolean = new boolean[i1 + 1];
      i2 = 0;
      i3 = 0;
      do
      {
        int i4 = i2 % 8;
        if (i4 == 0)
          i3 = this.ra.read();
        arrayOfBoolean[i2] = ((1 << i4 & i3) >> i4 == 1 ? 1 : false);
        i2++;
      }
      while (i2 <= i1);
    }
    else
    {
      if (i1 <= 4)
      {
        arrayOfBoolean = new boolean[i1 + 1];
        n &= 31;
        for (i2 = 0; i2 <= i1; i2++)
          arrayOfBoolean[i2] = ((1 << i2 & n) >> i2 == 1 ? 1 : false);
      }
      if ((i1 == 5) || (i1 == 6))
        throw new IllegalStateException("count of referred-to segments had bad value in header for segment " + j + " starting at " + i);
    }
    localJBIG2Segment.segmentRetentionFlags = arrayOfBoolean;
    localJBIG2Segment.countOfReferredToSegments = i1;
    arrayOfInt = new int[i1 + 1];
    for (int i2 = 1; i2 <= i1; i2++)
      if (j <= 256)
        arrayOfInt[i2] = this.ra.read();
      else if (j <= 65536)
        arrayOfInt[i2] = this.ra.readUnsignedShort();
      else
        arrayOfInt[i2] = (int)this.ra.readUnsignedInt();
    localJBIG2Segment.referredToSegmentNumbers = arrayOfInt;
    int i3 = this.ra.getFilePointer() - i;
    if (bool2)
      i2 = this.ra.readInt();
    else
      i2 = this.ra.read();
    if (i2 < 0)
      throw new IllegalStateException("page " + i2 + " invalid for segment " + j + " starting at " + i);
    localJBIG2Segment.page = i2;
    localJBIG2Segment.page_association_size = bool2;
    localJBIG2Segment.page_association_offset = i3;
    if ((i2 > 0) && (!this.pages.containsKey(new Integer(i2))))
      this.pages.put(new Integer(i2), new JBIG2Page(i2, this));
    if (i2 > 0)
      ((JBIG2Page)this.pages.get(new Integer(i2))).addSegment(localJBIG2Segment);
    else
      this.globals.add(localJBIG2Segment);
    long l = this.ra.readUnsignedInt();
    localJBIG2Segment.dataLength = l;
    int i5 = this.ra.getFilePointer();
    this.ra.seek(i);
    byte[] arrayOfByte = new byte[i5 - i];
    this.ra.read(arrayOfByte);
    localJBIG2Segment.headerData = arrayOfByte;
    return localJBIG2Segment;
  }

  void readFileHeader()
    throws IOException
  {
    this.ra.seek(0);
    byte[] arrayOfByte1 = new byte[8];
    this.ra.read(arrayOfByte1);
    byte[] arrayOfByte2 = { -105, 74, 66, 50, 13, 10, 26, 10 };
    for (int i = 0; i < arrayOfByte1.length; i++)
    {
      if (arrayOfByte1[i] == arrayOfByte2[i])
        continue;
      throw new IllegalStateException("file header idstring not good at byte " + i);
    }
    i = this.ra.read();
    this.sequential = ((i & 0x1) == 1);
    this.number_of_pages_known = ((i & 0x2) == 0);
    if ((i & 0xFC) != 0)
      throw new IllegalStateException("file header flags bits 2-7 not 0");
    if (this.number_of_pages_known)
      this.number_of_pages = this.ra.readInt();
  }

  public int numberOfPages()
  {
    return this.pages.size();
  }

  public int getPageHeight(int paramInt)
  {
    return ((JBIG2Page)this.pages.get(new Integer(paramInt))).pageBitmapHeight;
  }

  public int getPageWidth(int paramInt)
  {
    return ((JBIG2Page)this.pages.get(new Integer(paramInt))).pageBitmapWidth;
  }

  public JBIG2Page getPage(int paramInt)
  {
    return (JBIG2Page)this.pages.get(new Integer(paramInt));
  }

  public byte[] getGlobal(boolean paramBoolean)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      Iterator localIterator = this.globals.iterator();
      while (localIterator.hasNext())
      {
        JBIG2Segment localJBIG2Segment = (JBIG2Segment)localIterator.next();
        if ((paramBoolean) && ((localJBIG2Segment.type == 51) || (localJBIG2Segment.type == 49)))
          continue;
        localByteArrayOutputStream.write(localJBIG2Segment.headerData);
        localByteArrayOutputStream.write(localJBIG2Segment.data);
      }
      localByteArrayOutputStream.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    if (localByteArrayOutputStream.size() <= 0)
      return null;
    return localByteArrayOutputStream.toByteArray();
  }

  public String toString()
  {
    if (this.read)
      return "Jbig2SegmentReader: number of pages: " + numberOfPages();
    return "Jbig2SegmentReader in indeterminate state.";
  }

  public static class JBIG2Page
  {
    public final int page;
    private final JBIG2SegmentReader sr;
    private final SortedMap segs = new TreeMap();
    public int pageBitmapWidth = -1;
    public int pageBitmapHeight = -1;

    public JBIG2Page(int paramInt, JBIG2SegmentReader paramJBIG2SegmentReader)
    {
      this.page = paramInt;
      this.sr = paramJBIG2SegmentReader;
    }

    public byte[] getData(boolean paramBoolean)
      throws IOException
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      Iterator localIterator = this.segs.keySet().iterator();
      while (localIterator.hasNext())
      {
        Integer localInteger = (Integer)localIterator.next();
        JBIG2SegmentReader.JBIG2Segment localJBIG2Segment = (JBIG2SegmentReader.JBIG2Segment)this.segs.get(localInteger);
        if ((paramBoolean) && ((localJBIG2Segment.type == 51) || (localJBIG2Segment.type == 49)))
          continue;
        if (paramBoolean)
        {
          byte[] arrayOfByte = JBIG2SegmentReader.copyByteArray(localJBIG2Segment.headerData);
          if (localJBIG2Segment.page_association_size)
          {
            arrayOfByte[localJBIG2Segment.page_association_offset] = 0;
            arrayOfByte[(localJBIG2Segment.page_association_offset + 1)] = 0;
            arrayOfByte[(localJBIG2Segment.page_association_offset + 2)] = 0;
            arrayOfByte[(localJBIG2Segment.page_association_offset + 3)] = 1;
          }
          else
          {
            arrayOfByte[localJBIG2Segment.page_association_offset] = 1;
          }
          localByteArrayOutputStream.write(arrayOfByte);
        }
        else
        {
          localByteArrayOutputStream.write(localJBIG2Segment.headerData);
        }
        localByteArrayOutputStream.write(localJBIG2Segment.data);
      }
      localByteArrayOutputStream.close();
      return localByteArrayOutputStream.toByteArray();
    }

    public void addSegment(JBIG2SegmentReader.JBIG2Segment paramJBIG2Segment)
    {
      this.segs.put(new Integer(paramJBIG2Segment.segmentNumber), paramJBIG2Segment);
    }
  }

  public static class JBIG2Segment
    implements Comparable
  {
    public final int segmentNumber;
    public long dataLength = -1L;
    public int page = -1;
    public int[] referredToSegmentNumbers = null;
    public boolean[] segmentRetentionFlags = null;
    public int type = -1;
    public boolean deferredNonRetain = false;
    public int countOfReferredToSegments = -1;
    public byte[] data = null;
    public byte[] headerData = null;
    public boolean page_association_size = false;
    public int page_association_offset = -1;

    public JBIG2Segment(int paramInt)
    {
      this.segmentNumber = paramInt;
    }

    public int compareTo(Object paramObject)
    {
      return compareTo((JBIG2Segment)paramObject);
    }

    public int compareTo(JBIG2Segment paramJBIG2Segment)
    {
      return this.segmentNumber - paramJBIG2Segment.segmentNumber;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.JBIG2SegmentReader
 * JD-Core Version:    0.6.0
 */