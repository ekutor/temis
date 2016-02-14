package com.lowagie.text.pdf;

import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.StringTokenizer;

public class GlyphList
{
  private static HashMap unicode2names = new HashMap();
  private static HashMap names2unicode = new HashMap();

  public static int[] nameToUnicode(String paramString)
  {
    return (int[])names2unicode.get(paramString);
  }

  public static String unicodeToName(int paramInt)
  {
    return (String)unicode2names.get(new Integer(paramInt));
  }

  static
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/glyphlist.txt", new FontsResourceAnchor().getClass().getClassLoader());
      if (localInputStream == null)
      {
        localObject1 = "glyphlist.txt not found as resource. (It must exist as resource in the package com.lowagie.text.pdf.fonts)";
        throw new Exception((String)localObject1);
      }
      Object localObject1 = new byte[1024];
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      while (true)
      {
        int i = localInputStream.read(localObject1);
        if (i < 0)
          break;
        localByteArrayOutputStream.write(localObject1, 0, i);
      }
      localInputStream.close();
      localInputStream = null;
      String str1 = PdfEncodings.convertToString(localByteArrayOutputStream.toByteArray(), null);
      StringTokenizer localStringTokenizer1 = new StringTokenizer(str1, "\r\n");
      while (localStringTokenizer1.hasMoreTokens())
      {
        String str2 = localStringTokenizer1.nextToken();
        if (str2.startsWith("#"))
          continue;
        StringTokenizer localStringTokenizer2 = new StringTokenizer(str2, " ;\r\n\t\f");
        String str3 = null;
        String str4 = null;
        if (!localStringTokenizer2.hasMoreTokens())
          continue;
        str3 = localStringTokenizer2.nextToken();
        if (!localStringTokenizer2.hasMoreTokens())
          continue;
        str4 = localStringTokenizer2.nextToken();
        Integer localInteger = Integer.valueOf(str4, 16);
        unicode2names.put(localInteger, str3);
        names2unicode.put(str3, new int[] { localInteger.intValue() });
      }
    }
    catch (Exception localException1)
    {
      System.err.println("glyphlist.txt loading error: " + localException1.getMessage());
    }
    finally
    {
      if (localInputStream != null)
        try
        {
          localInputStream.close();
        }
        catch (Exception localException2)
        {
        }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.GlyphList
 * JD-Core Version:    0.6.0
 */