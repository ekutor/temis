package com.lowagie.text.pdf;

import com.lowagie.text.xml.simpleparser.IanaEncodings;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public final class SimpleNamedDestination
  implements SimpleXMLDocHandler
{
  private HashMap xmlNames;
  private HashMap xmlLast;

  public static HashMap getNamedDestination(PdfReader paramPdfReader, boolean paramBoolean)
  {
    IntHashtable localIntHashtable = new IntHashtable();
    int i = paramPdfReader.getNumberOfPages();
    for (int j = 1; j <= i; j++)
      localIntHashtable.put(paramPdfReader.getPageOrigRef(j).getNumber(), j);
    HashMap localHashMap = paramBoolean ? paramPdfReader.getNamedDestinationFromNames() : paramPdfReader.getNamedDestinationFromStrings();
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      PdfArray localPdfArray = (PdfArray)localEntry.getValue();
      StringBuffer localStringBuffer = new StringBuffer();
      try
      {
        localStringBuffer.append(localIntHashtable.get(localPdfArray.getAsIndirectObject(0).getNumber()));
        localStringBuffer.append(' ').append(localPdfArray.getPdfObject(1).toString().substring(1));
        for (int k = 2; k < localPdfArray.size(); k++)
          localStringBuffer.append(' ').append(localPdfArray.getPdfObject(k).toString());
        localEntry.setValue(localStringBuffer.toString());
      }
      catch (Exception localException)
      {
        localIterator.remove();
      }
    }
    return localHashMap;
  }

  public static void exportToXML(HashMap paramHashMap, OutputStream paramOutputStream, String paramString, boolean paramBoolean)
    throws IOException
  {
    String str = IanaEncodings.getJavaEncoding(paramString);
    BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(paramOutputStream, str));
    exportToXML(paramHashMap, localBufferedWriter, paramString, paramBoolean);
  }

  public static void exportToXML(HashMap paramHashMap, Writer paramWriter, String paramString, boolean paramBoolean)
    throws IOException
  {
    paramWriter.write("<?xml version=\"1.0\" encoding=\"");
    paramWriter.write(SimpleXMLParser.escapeXML(paramString, paramBoolean));
    paramWriter.write("\"?>\n<Destination>\n");
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      String str2 = (String)localEntry.getValue();
      paramWriter.write("  <Name Page=\"");
      paramWriter.write(SimpleXMLParser.escapeXML(str2, paramBoolean));
      paramWriter.write("\">");
      paramWriter.write(SimpleXMLParser.escapeXML(escapeBinaryString(str1), paramBoolean));
      paramWriter.write("</Name>\n");
    }
    paramWriter.write("</Destination>\n");
    paramWriter.flush();
  }

  public static HashMap importFromXML(InputStream paramInputStream)
    throws IOException
  {
    SimpleNamedDestination localSimpleNamedDestination = new SimpleNamedDestination();
    SimpleXMLParser.parse(localSimpleNamedDestination, paramInputStream);
    return localSimpleNamedDestination.xmlNames;
  }

  public static HashMap importFromXML(Reader paramReader)
    throws IOException
  {
    SimpleNamedDestination localSimpleNamedDestination = new SimpleNamedDestination();
    SimpleXMLParser.parse(localSimpleNamedDestination, paramReader);
    return localSimpleNamedDestination.xmlNames;
  }

  static PdfArray createDestinationArray(String paramString, PdfWriter paramPdfWriter)
  {
    PdfArray localPdfArray = new PdfArray();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    int i = Integer.parseInt(localStringTokenizer.nextToken());
    localPdfArray.add(paramPdfWriter.getPageReference(i));
    if (!localStringTokenizer.hasMoreTokens())
    {
      localPdfArray.add(PdfName.XYZ);
      localPdfArray.add(new float[] { 0.0F, 10000.0F, 0.0F });
    }
    else
    {
      String str = localStringTokenizer.nextToken();
      if (str.startsWith("/"))
        str = str.substring(1);
      localPdfArray.add(new PdfName(str));
      for (int j = 0; (j < 4) && (localStringTokenizer.hasMoreTokens()); j++)
      {
        str = localStringTokenizer.nextToken();
        if (str.equals("null"))
          localPdfArray.add(PdfNull.PDFNULL);
        else
          localPdfArray.add(new PdfNumber(str));
      }
    }
    return localPdfArray;
  }

  public static PdfDictionary outputNamedDestinationAsNames(HashMap paramHashMap, PdfWriter paramPdfWriter)
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        String str1 = (String)localEntry.getKey();
        String str2 = (String)localEntry.getValue();
        PdfArray localPdfArray = createDestinationArray(str2, paramPdfWriter);
        PdfName localPdfName = new PdfName(str1);
        localPdfDictionary.put(localPdfName, localPdfArray);
      }
      catch (Exception localException)
      {
      }
    }
    return localPdfDictionary;
  }

  public static PdfDictionary outputNamedDestinationAsStrings(HashMap paramHashMap, PdfWriter paramPdfWriter)
    throws IOException
  {
    HashMap localHashMap = new HashMap(paramHashMap);
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        String str = (String)localEntry.getValue();
        PdfArray localPdfArray = createDestinationArray(str, paramPdfWriter);
        localEntry.setValue(paramPdfWriter.addToBody(localPdfArray).getIndirectReference());
      }
      catch (Exception localException)
      {
        localIterator.remove();
      }
    }
    return PdfNameTree.writeTree(localHashMap, paramPdfWriter);
  }

  public static String escapeBinaryString(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    for (int j = 0; j < i; j++)
    {
      char c = arrayOfChar[j];
      if (c < ' ')
      {
        localStringBuffer.append('\\');
        String str = "00" + Integer.toOctalString(c);
        localStringBuffer.append(str.substring(str.length() - 3));
      }
      else if (c == '\\')
      {
        localStringBuffer.append("\\\\");
      }
      else
      {
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }

  public static String unEscapeBinaryString(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    for (int j = 0; j < i; j++)
    {
      int k = arrayOfChar[j];
      if (k == 92)
      {
        j++;
        if (j >= i)
        {
          localStringBuffer.append('\\');
          break;
        }
        k = arrayOfChar[j];
        if ((k >= 48) && (k <= 55))
        {
          int m = k - 48;
          j++;
          for (int n = 0; (n < 2) && (j < i); n++)
          {
            k = arrayOfChar[j];
            if ((k < 48) || (k > 55))
              break;
            j++;
            m = m * 8 + k - 48;
          }
          j--;
          localStringBuffer.append((char)m);
        }
        else
        {
          localStringBuffer.append(k);
        }
      }
      else
      {
        localStringBuffer.append(k);
      }
    }
    return localStringBuffer.toString();
  }

  public void endDocument()
  {
  }

  public void endElement(String paramString)
  {
    if (paramString.equals("Destination"))
    {
      if ((this.xmlLast == null) && (this.xmlNames != null))
        return;
      throw new RuntimeException("Destination end tag out of place.");
    }
    if (!paramString.equals("Name"))
      throw new RuntimeException("Invalid end tag - " + paramString);
    if ((this.xmlLast == null) || (this.xmlNames == null))
      throw new RuntimeException("Name end tag out of place.");
    if (!this.xmlLast.containsKey("Page"))
      throw new RuntimeException("Page attribute missing.");
    this.xmlNames.put(unEscapeBinaryString((String)this.xmlLast.get("Name")), this.xmlLast.get("Page"));
    this.xmlLast = null;
  }

  public void startDocument()
  {
  }

  public void startElement(String paramString, HashMap paramHashMap)
  {
    if (this.xmlNames == null)
    {
      if (paramString.equals("Destination"))
      {
        this.xmlNames = new HashMap();
        return;
      }
      throw new RuntimeException("Root element is not Destination.");
    }
    if (!paramString.equals("Name"))
      throw new RuntimeException("Tag " + paramString + " not allowed.");
    if (this.xmlLast != null)
      throw new RuntimeException("Nested tags are not allowed.");
    this.xmlLast = new HashMap(paramHashMap);
    this.xmlLast.put("Name", "");
  }

  public void text(String paramString)
  {
    if (this.xmlLast == null)
      return;
    String str = (String)this.xmlLast.get("Name");
    str = str + paramString;
    this.xmlLast.put("Name", str);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.SimpleNamedDestination
 * JD-Core Version:    0.6.0
 */