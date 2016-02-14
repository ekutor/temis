package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public final class PdfEncryptor
{
  public static void encrypt(PdfReader paramPdfReader, OutputStream paramOutputStream, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, boolean paramBoolean)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper = new PdfStamper(paramPdfReader, paramOutputStream);
    localPdfStamper.setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt, paramBoolean);
    localPdfStamper.close();
  }

  public static void encrypt(PdfReader paramPdfReader, OutputStream paramOutputStream, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, boolean paramBoolean, HashMap paramHashMap)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper = new PdfStamper(paramPdfReader, paramOutputStream);
    localPdfStamper.setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt, paramBoolean);
    localPdfStamper.setMoreInfo(paramHashMap);
    localPdfStamper.close();
  }

  public static void encrypt(PdfReader paramPdfReader, OutputStream paramOutputStream, boolean paramBoolean, String paramString1, String paramString2, int paramInt)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper = new PdfStamper(paramPdfReader, paramOutputStream);
    localPdfStamper.setEncryption(paramBoolean, paramString1, paramString2, paramInt);
    localPdfStamper.close();
  }

  public static void encrypt(PdfReader paramPdfReader, OutputStream paramOutputStream, boolean paramBoolean, String paramString1, String paramString2, int paramInt, HashMap paramHashMap)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper = new PdfStamper(paramPdfReader, paramOutputStream);
    localPdfStamper.setEncryption(paramBoolean, paramString1, paramString2, paramInt);
    localPdfStamper.setMoreInfo(paramHashMap);
    localPdfStamper.close();
  }

  public static void encrypt(PdfReader paramPdfReader, OutputStream paramOutputStream, int paramInt1, String paramString1, String paramString2, int paramInt2, HashMap paramHashMap)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper = new PdfStamper(paramPdfReader, paramOutputStream);
    localPdfStamper.setEncryption(paramInt1, paramString1, paramString2, paramInt2);
    localPdfStamper.setMoreInfo(paramHashMap);
    localPdfStamper.close();
  }

  public static void encrypt(PdfReader paramPdfReader, OutputStream paramOutputStream, int paramInt1, String paramString1, String paramString2, int paramInt2)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper = new PdfStamper(paramPdfReader, paramOutputStream);
    localPdfStamper.setEncryption(paramInt1, paramString1, paramString2, paramInt2);
    localPdfStamper.close();
  }

  public static String getPermissionsVerbose(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer("Allowed:");
    if ((0x804 & paramInt) == 2052)
      localStringBuffer.append(" Printing");
    if ((0x8 & paramInt) == 8)
      localStringBuffer.append(" Modify contents");
    if ((0x10 & paramInt) == 16)
      localStringBuffer.append(" Copy");
    if ((0x20 & paramInt) == 32)
      localStringBuffer.append(" Modify annotations");
    if ((0x100 & paramInt) == 256)
      localStringBuffer.append(" Fill in");
    if ((0x200 & paramInt) == 512)
      localStringBuffer.append(" Screen readers");
    if ((0x400 & paramInt) == 1024)
      localStringBuffer.append(" Assembly");
    if ((0x4 & paramInt) == 4)
      localStringBuffer.append(" Degraded printing");
    return localStringBuffer.toString();
  }

  public static boolean isPrintingAllowed(int paramInt)
  {
    return (0x804 & paramInt) == 2052;
  }

  public static boolean isModifyContentsAllowed(int paramInt)
  {
    return (0x8 & paramInt) == 8;
  }

  public static boolean isCopyAllowed(int paramInt)
  {
    return (0x10 & paramInt) == 16;
  }

  public static boolean isModifyAnnotationsAllowed(int paramInt)
  {
    return (0x20 & paramInt) == 32;
  }

  public static boolean isFillInAllowed(int paramInt)
  {
    return (0x100 & paramInt) == 256;
  }

  public static boolean isScreenReadersAllowed(int paramInt)
  {
    return (0x200 & paramInt) == 512;
  }

  public static boolean isAssemblyAllowed(int paramInt)
  {
    return (0x400 & paramInt) == 1024;
  }

  public static boolean isDegradedPrintingAllowed(int paramInt)
  {
    return (0x4 & paramInt) == 4;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfEncryptor
 * JD-Core Version:    0.6.0
 */