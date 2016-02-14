package com.lowagie.text.pdf.crypto;

public final class IVGenerator
{
  private static ARCFOUREncryption arcfour = new ARCFOUREncryption();

  public static byte[] getIV()
  {
    return getIV(16);
  }

  public static byte[] getIV(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    synchronized (arcfour)
    {
      arcfour.encryptARCFOUR(arrayOfByte);
    }
    return arrayOfByte;
  }

  static
  {
    long l1 = System.currentTimeMillis();
    long l2 = Runtime.getRuntime().freeMemory();
    String str = l1 + "+" + l2;
    arcfour.prepareARCFOURKey(str.getBytes());
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.crypto.IVGenerator
 * JD-Core Version:    0.6.0
 */