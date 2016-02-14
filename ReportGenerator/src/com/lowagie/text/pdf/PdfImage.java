package com.lowagie.text.pdf;

import com.lowagie.text.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class PdfImage extends PdfStream
{
  static final int TRANSFERSIZE = 4096;
  protected PdfName name = null;

  public PdfImage(Image paramImage, String paramString, PdfIndirectReference paramPdfIndirectReference)
    throws BadPdfFormatException
  {
    this.name = new PdfName(paramString);
    put(PdfName.TYPE, PdfName.XOBJECT);
    put(PdfName.SUBTYPE, PdfName.IMAGE);
    put(PdfName.WIDTH, new PdfNumber(paramImage.getWidth()));
    put(PdfName.HEIGHT, new PdfNumber(paramImage.getHeight()));
    if (paramImage.getLayer() != null)
      put(PdfName.OC, paramImage.getLayer().getRef());
    if ((paramImage.isMask()) && ((paramImage.getBpc() == 1) || (paramImage.getBpc() > 255)))
      put(PdfName.IMAGEMASK, PdfBoolean.PDFTRUE);
    if (paramPdfIndirectReference != null)
      if (paramImage.isSmask())
        put(PdfName.SMASK, paramPdfIndirectReference);
      else
        put(PdfName.MASK, paramPdfIndirectReference);
    if ((paramImage.isMask()) && (paramImage.isInverted()))
      put(PdfName.DECODE, new PdfLiteral("[1 0]"));
    if (paramImage.isInterpolation())
      put(PdfName.INTERPOLATE, PdfBoolean.PDFTRUE);
    Object localObject1 = null;
    try
    {
      if (paramImage.isImgRaw())
      {
        int i = paramImage.getColorspace();
        int[] arrayOfInt = paramImage.getTransparency();
        int k;
        if ((arrayOfInt != null) && (!paramImage.isMask()) && (paramPdfIndirectReference == null))
        {
          String str2 = "[";
          for (k = 0; k < arrayOfInt.length; k++)
            str2 = str2 + arrayOfInt[k] + " ";
          str2 = str2 + "]";
          put(PdfName.MASK, new PdfLiteral(str2));
        }
        this.bytes = paramImage.getRawData();
        put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        int j = paramImage.getBpc();
        if (j > 255)
        {
          if (!paramImage.isMask())
            put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
          put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
          put(PdfName.FILTER, PdfName.CCITTFAXDECODE);
          k = j - 257;
          PdfDictionary localPdfDictionary2 = new PdfDictionary();
          if (k != 0)
            localPdfDictionary2.put(PdfName.K, new PdfNumber(k));
          if ((i & 0x1) != 0)
            localPdfDictionary2.put(PdfName.BLACKIS1, PdfBoolean.PDFTRUE);
          if ((i & 0x2) != 0)
            localPdfDictionary2.put(PdfName.ENCODEDBYTEALIGN, PdfBoolean.PDFTRUE);
          if ((i & 0x4) != 0)
            localPdfDictionary2.put(PdfName.ENDOFLINE, PdfBoolean.PDFTRUE);
          if ((i & 0x8) != 0)
            localPdfDictionary2.put(PdfName.ENDOFBLOCK, PdfBoolean.PDFFALSE);
          localPdfDictionary2.put(PdfName.COLUMNS, new PdfNumber(paramImage.getWidth()));
          localPdfDictionary2.put(PdfName.ROWS, new PdfNumber(paramImage.getHeight()));
          put(PdfName.DECODEPARMS, localPdfDictionary2);
        }
        else
        {
          switch (i)
          {
          case 1:
            put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
            if (!paramImage.isInverted())
              break;
            put(PdfName.DECODE, new PdfLiteral("[1 0]"));
            break;
          case 3:
            put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            if (!paramImage.isInverted())
              break;
            put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0]"));
            break;
          case 2:
          case 4:
          default:
            put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
            if (!paramImage.isInverted())
              break;
            put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
          }
          PdfDictionary localPdfDictionary1 = paramImage.getAdditional();
          if (localPdfDictionary1 != null)
            putAll(localPdfDictionary1);
          if ((paramImage.isMask()) && ((paramImage.getBpc() == 1) || (paramImage.getBpc() > 8)))
            remove(PdfName.COLORSPACE);
          put(PdfName.BITSPERCOMPONENT, new PdfNumber(paramImage.getBpc()));
          if (paramImage.isDeflated())
            put(PdfName.FILTER, PdfName.FLATEDECODE);
          else
            flateCompress(paramImage.getCompressionLevel());
        }
        jsr 604;
      }
      String str1;
      if (paramImage.getRawData() == null)
      {
        localObject1 = paramImage.getUrl().openStream();
        str1 = paramImage.getUrl().toString();
      }
      else
      {
        localObject1 = new ByteArrayInputStream(paramImage.getRawData());
        str1 = "Byte array";
      }
      switch (paramImage.type())
      {
      case 32:
        put(PdfName.FILTER, PdfName.DCTDECODE);
        switch (paramImage.getColorspace())
        {
        case 1:
          put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
          break;
        case 3:
          put(PdfName.COLORSPACE, PdfName.DEVICERGB);
          break;
        default:
          put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
          if (!paramImage.isInverted())
            break;
          put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
        }
        put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
        if (paramImage.getRawData() != null)
        {
          this.bytes = paramImage.getRawData();
          put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
          jsr 366;
        }
        this.streamBytes = new ByteArrayOutputStream();
        transferBytes((InputStream)localObject1, this.streamBytes, -1);
        break;
      case 33:
        put(PdfName.FILTER, PdfName.JPXDECODE);
        if (paramImage.getColorspace() > 0)
        {
          switch (paramImage.getColorspace())
          {
          case 1:
            put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
            break;
          case 3:
            put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            break;
          default:
            put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
          }
          put(PdfName.BITSPERCOMPONENT, new PdfNumber(paramImage.getBpc()));
        }
        if (paramImage.getRawData() != null)
        {
          this.bytes = paramImage.getRawData();
          put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
          jsr 203;
        }
        this.streamBytes = new ByteArrayOutputStream();
        transferBytes((InputStream)localObject1, this.streamBytes, -1);
        break;
      case 36:
        put(PdfName.FILTER, PdfName.JBIG2DECODE);
        put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
        put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
        if (paramImage.getRawData() != null)
        {
          this.bytes = paramImage.getRawData();
          put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
          jsr 106;
        }
        this.streamBytes = new ByteArrayOutputStream();
        transferBytes((InputStream)localObject1, this.streamBytes, -1);
        break;
      case 34:
      case 35:
      default:
        throw new BadPdfFormatException(str1 + " is an unknown Image format.");
      }
      put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
    }
    catch (IOException localIOException)
    {
      throw new BadPdfFormatException(localIOException.getMessage());
    }
    finally
    {
      if (localObject1 != null)
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (Exception localException)
        {
        }
    }
  }

  public PdfName name()
  {
    return this.name;
  }

  static void transferBytes(InputStream paramInputStream, OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[4096];
    if (paramInt < 0)
      paramInt = 134217727;
    while (paramInt != 0)
    {
      int i = paramInputStream.read(arrayOfByte, 0, Math.min(paramInt, 4096));
      if (i < 0)
        return;
      paramOutputStream.write(arrayOfByte, 0, i);
      paramInt -= i;
    }
  }

  protected void importAll(PdfImage paramPdfImage)
  {
    this.name = paramPdfImage.name;
    this.compressed = paramPdfImage.compressed;
    this.compressionLevel = paramPdfImage.compressionLevel;
    this.streamBytes = paramPdfImage.streamBytes;
    this.bytes = paramPdfImage.bytes;
    this.hashMap = paramPdfImage.hashMap;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfImage
 * JD-Core Version:    0.6.0
 */