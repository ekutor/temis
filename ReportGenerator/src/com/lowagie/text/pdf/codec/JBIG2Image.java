package com.lowagie.text.pdf.codec;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

public class JBIG2Image
{
  public static byte[] getGlobalSegment(RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    try
    {
      JBIG2SegmentReader localJBIG2SegmentReader = new JBIG2SegmentReader(paramRandomAccessFileOrArray);
      localJBIG2SegmentReader.read();
      return localJBIG2SegmentReader.getGlobal(true);
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static Image getJbig2Image(RandomAccessFileOrArray paramRandomAccessFileOrArray, int paramInt)
  {
    if (paramInt < 1)
      throw new IllegalArgumentException("The page number must be >= 1.");
    try
    {
      JBIG2SegmentReader localJBIG2SegmentReader = new JBIG2SegmentReader(paramRandomAccessFileOrArray);
      localJBIG2SegmentReader.read();
      JBIG2SegmentReader.JBIG2Page localJBIG2Page = localJBIG2SegmentReader.getPage(paramInt);
      ImgJBIG2 localImgJBIG2 = new ImgJBIG2(localJBIG2Page.pageBitmapWidth, localJBIG2Page.pageBitmapHeight, localJBIG2Page.getData(true), localJBIG2SegmentReader.getGlobal(true));
      return localImgJBIG2;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public static int getNumberOfPages(RandomAccessFileOrArray paramRandomAccessFileOrArray)
  {
    try
    {
      JBIG2SegmentReader localJBIG2SegmentReader = new JBIG2SegmentReader(paramRandomAccessFileOrArray);
      localJBIG2SegmentReader.read();
      return localJBIG2SegmentReader.numberOfPages();
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.JBIG2Image
 * JD-Core Version:    0.6.0
 */