package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.IOException;

public class PdfFunction
{
  protected PdfWriter writer;
  protected PdfIndirectReference reference;
  protected PdfDictionary dictionary;

  protected PdfFunction(PdfWriter paramPdfWriter)
  {
    this.writer = paramPdfWriter;
  }

  PdfIndirectReference getReference()
  {
    try
    {
      if (this.reference == null)
        this.reference = this.writer.addToBody(this.dictionary).getIndirectReference();
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    return this.reference;
  }

  public static PdfFunction type0(PdfWriter paramPdfWriter, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int[] paramArrayOfInt, int paramInt1, int paramInt2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, byte[] paramArrayOfByte)
  {
    PdfFunction localPdfFunction = new PdfFunction(paramPdfWriter);
    localPdfFunction.dictionary = new PdfStream(paramArrayOfByte);
    ((PdfStream)localPdfFunction.dictionary).flateCompress(paramPdfWriter.getCompressionLevel());
    localPdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(0));
    localPdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(paramArrayOfFloat1));
    localPdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(paramArrayOfFloat2));
    localPdfFunction.dictionary.put(PdfName.SIZE, new PdfArray(paramArrayOfInt));
    localPdfFunction.dictionary.put(PdfName.BITSPERSAMPLE, new PdfNumber(paramInt1));
    if (paramInt2 != 1)
      localPdfFunction.dictionary.put(PdfName.ORDER, new PdfNumber(paramInt2));
    if (paramArrayOfFloat3 != null)
      localPdfFunction.dictionary.put(PdfName.ENCODE, new PdfArray(paramArrayOfFloat3));
    if (paramArrayOfFloat4 != null)
      localPdfFunction.dictionary.put(PdfName.DECODE, new PdfArray(paramArrayOfFloat4));
    return localPdfFunction;
  }

  public static PdfFunction type2(PdfWriter paramPdfWriter, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, float paramFloat)
  {
    PdfFunction localPdfFunction = new PdfFunction(paramPdfWriter);
    localPdfFunction.dictionary = new PdfDictionary();
    localPdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(2));
    localPdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(paramArrayOfFloat1));
    if (paramArrayOfFloat2 != null)
      localPdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(paramArrayOfFloat2));
    if (paramArrayOfFloat3 != null)
      localPdfFunction.dictionary.put(PdfName.C0, new PdfArray(paramArrayOfFloat3));
    if (paramArrayOfFloat4 != null)
      localPdfFunction.dictionary.put(PdfName.C1, new PdfArray(paramArrayOfFloat4));
    localPdfFunction.dictionary.put(PdfName.N, new PdfNumber(paramFloat));
    return localPdfFunction;
  }

  public static PdfFunction type3(PdfWriter paramPdfWriter, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, PdfFunction[] paramArrayOfPdfFunction, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4)
  {
    PdfFunction localPdfFunction = new PdfFunction(paramPdfWriter);
    localPdfFunction.dictionary = new PdfDictionary();
    localPdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(3));
    localPdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(paramArrayOfFloat1));
    if (paramArrayOfFloat2 != null)
      localPdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(paramArrayOfFloat2));
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayOfPdfFunction.length; i++)
      localPdfArray.add(paramArrayOfPdfFunction[i].getReference());
    localPdfFunction.dictionary.put(PdfName.FUNCTIONS, localPdfArray);
    localPdfFunction.dictionary.put(PdfName.BOUNDS, new PdfArray(paramArrayOfFloat3));
    localPdfFunction.dictionary.put(PdfName.ENCODE, new PdfArray(paramArrayOfFloat4));
    return localPdfFunction;
  }

  public static PdfFunction type4(PdfWriter paramPdfWriter, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length()];
    for (int i = 0; i < arrayOfByte.length; i++)
      arrayOfByte[i] = (byte)paramString.charAt(i);
    PdfFunction localPdfFunction = new PdfFunction(paramPdfWriter);
    localPdfFunction.dictionary = new PdfStream(arrayOfByte);
    ((PdfStream)localPdfFunction.dictionary).flateCompress(paramPdfWriter.getCompressionLevel());
    localPdfFunction.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(4));
    localPdfFunction.dictionary.put(PdfName.DOMAIN, new PdfArray(paramArrayOfFloat1));
    localPdfFunction.dictionary.put(PdfName.RANGE, new PdfArray(paramArrayOfFloat2));
    return localPdfFunction;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfFunction
 * JD-Core Version:    0.6.0
 */