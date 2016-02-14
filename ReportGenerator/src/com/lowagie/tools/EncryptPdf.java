package com.lowagie.tools;

import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class EncryptPdf
{
  private static final int INPUT_FILE = 0;
  private static final int OUTPUT_FILE = 1;
  private static final int USER_PASSWORD = 2;
  private static final int OWNER_PASSWORD = 3;
  private static final int PERMISSIONS = 4;
  private static final int STRENGTH = 5;
  private static final int MOREINFO = 6;
  private static final int[] permit = { 2052, 8, 16, 32, 256, 512, 1024, 4 };

  private static void usage()
  {
    System.out.println("usage: input_file output_file user_password owner_password permissions 128|40 [new info string pairs]");
    System.out.println("permissions is 8 digit long 0 or 1. Each digit has a particular security function:");
    System.out.println();
    System.out.println("AllowPrinting");
    System.out.println("AllowModifyContents");
    System.out.println("AllowCopy");
    System.out.println("AllowModifyAnnotations");
    System.out.println("AllowFillIn (128 bit only)");
    System.out.println("AllowScreenReaders (128 bit only)");
    System.out.println("AllowAssembly (128 bit only)");
    System.out.println("AllowDegradedPrinting (128 bit only)");
    System.out.println("Example permissions to copy and print would be: 10100000");
  }

  public static void main(String[] paramArrayOfString)
  {
    System.out.println("PDF document encryptor");
    if ((paramArrayOfString.length <= 5) || (paramArrayOfString[4].length() != 8))
    {
      usage();
      return;
    }
    try
    {
      int i = 0;
      String str = paramArrayOfString[4];
      for (int j = 0; j < str.length(); j++)
        i |= (str.charAt(j) == '0' ? 0 : permit[j]);
      System.out.println("Reading " + paramArrayOfString[0]);
      PdfReader localPdfReader = new PdfReader(paramArrayOfString[0]);
      System.out.println("Writing " + paramArrayOfString[1]);
      HashMap localHashMap = new HashMap();
      for (int k = 6; k < paramArrayOfString.length - 1; k += 2)
        localHashMap.put(paramArrayOfString[k], paramArrayOfString[(k + 1)]);
      PdfEncryptor.encrypt(localPdfReader, new FileOutputStream(paramArrayOfString[1]), paramArrayOfString[2].getBytes(), paramArrayOfString[3].getBytes(), i, paramArrayOfString[5].equals("128"), localHashMap);
      System.out.println("Done.");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.EncryptPdf
 * JD-Core Version:    0.6.0
 */