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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

public final class SimpleBookmark
  implements SimpleXMLDocHandler
{
  private ArrayList topList;
  private Stack attr = new Stack();

  private static List bookmarkDepth(PdfReader paramPdfReader, PdfDictionary paramPdfDictionary, IntHashtable paramIntHashtable)
  {
    ArrayList localArrayList = new ArrayList();
    while (paramPdfDictionary != null)
    {
      HashMap localHashMap = new HashMap();
      PdfString localPdfString = (PdfString)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.TITLE));
      localHashMap.put("Title", localPdfString.toUnicodeString());
      PdfArray localPdfArray = (PdfArray)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.C));
      if ((localPdfArray != null) && (localPdfArray.size() == 3))
      {
        localObject1 = new ByteBuffer();
        ((ByteBuffer)localObject1).append(localPdfArray.getAsNumber(0).floatValue()).append(' ');
        ((ByteBuffer)localObject1).append(localPdfArray.getAsNumber(1).floatValue()).append(' ');
        ((ByteBuffer)localObject1).append(localPdfArray.getAsNumber(2).floatValue());
        localHashMap.put("Color", PdfEncodings.convertToString(((ByteBuffer)localObject1).toByteArray(), null));
      }
      Object localObject1 = (PdfNumber)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.F));
      Object localObject2;
      if (localObject1 != null)
      {
        int i = ((PdfNumber)localObject1).intValue();
        localObject2 = "";
        if ((i & 0x1) != 0)
          localObject2 = (String)localObject2 + "italic ";
        if ((i & 0x2) != 0)
          localObject2 = (String)localObject2 + "bold ";
        localObject2 = ((String)localObject2).trim();
        if (((String)localObject2).length() != 0)
          localHashMap.put("Style", localObject2);
      }
      PdfNumber localPdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.COUNT));
      if ((localPdfNumber != null) && (localPdfNumber.intValue() < 0))
        localHashMap.put("Open", "false");
      try
      {
        localObject2 = PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.DEST));
        if (localObject2 != null)
        {
          mapGotoBookmark(localHashMap, (PdfObject)localObject2, paramIntHashtable);
        }
        else
        {
          PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.A));
          if (localPdfDictionary2 != null)
            if (PdfName.GOTO.equals(PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.S))))
            {
              localObject2 = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.D));
              if (localObject2 != null)
                mapGotoBookmark(localHashMap, (PdfObject)localObject2, paramIntHashtable);
            }
            else if (PdfName.URI.equals(PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.S))))
            {
              localHashMap.put("Action", "URI");
              localHashMap.put("URI", ((PdfString)PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.URI))).toUnicodeString());
            }
            else
            {
              Object localObject3;
              if (PdfName.GOTOR.equals(PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.S))))
              {
                localObject2 = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.D));
                if (localObject2 != null)
                  if (((PdfObject)localObject2).isString())
                  {
                    localHashMap.put("Named", ((PdfObject)localObject2).toString());
                  }
                  else if (((PdfObject)localObject2).isName())
                  {
                    localHashMap.put("NamedN", PdfName.decodeName(((PdfObject)localObject2).toString()));
                  }
                  else if (((PdfObject)localObject2).isArray())
                  {
                    localObject3 = (PdfArray)localObject2;
                    localObject4 = new StringBuffer();
                    ((StringBuffer)localObject4).append(((PdfArray)localObject3).getPdfObject(0).toString());
                    ((StringBuffer)localObject4).append(' ').append(((PdfArray)localObject3).getPdfObject(1).toString());
                    for (int j = 2; j < ((PdfArray)localObject3).size(); j++)
                      ((StringBuffer)localObject4).append(' ').append(((PdfArray)localObject3).getPdfObject(j).toString());
                    localHashMap.put("Page", ((StringBuffer)localObject4).toString());
                  }
                localHashMap.put("Action", "GoToR");
                localObject3 = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.F));
                if (localObject3 != null)
                  if (((PdfObject)localObject3).isString())
                  {
                    localHashMap.put("File", ((PdfString)localObject3).toUnicodeString());
                  }
                  else if (((PdfObject)localObject3).isDictionary())
                  {
                    localObject3 = PdfReader.getPdfObject(((PdfDictionary)localObject3).get(PdfName.F));
                    if (((PdfObject)localObject3).isString())
                      localHashMap.put("File", ((PdfString)localObject3).toUnicodeString());
                  }
                Object localObject4 = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.NEWWINDOW));
                if (localObject4 != null)
                  localHashMap.put("NewWindow", ((PdfObject)localObject4).toString());
              }
              else if (PdfName.LAUNCH.equals(PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.S))))
              {
                localHashMap.put("Action", "Launch");
                localObject3 = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.F));
                if (localObject3 == null)
                  localObject3 = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.WIN));
                if (localObject3 != null)
                  if (((PdfObject)localObject3).isString())
                  {
                    localHashMap.put("File", ((PdfString)localObject3).toUnicodeString());
                  }
                  else if (((PdfObject)localObject3).isDictionary())
                  {
                    localObject3 = PdfReader.getPdfObjectRelease(((PdfDictionary)localObject3).get(PdfName.F));
                    if (((PdfObject)localObject3).isString())
                      localHashMap.put("File", ((PdfString)localObject3).toUnicodeString());
                  }
              }
            }
        }
      }
      catch (Exception localException)
      {
      }
      PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.FIRST));
      if (localPdfDictionary1 != null)
        localHashMap.put("Kids", bookmarkDepth(paramPdfReader, localPdfDictionary1, paramIntHashtable));
      localArrayList.add(localHashMap);
      paramPdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.NEXT));
    }
    return (List)(List)(List)(List)localArrayList;
  }

  private static void mapGotoBookmark(HashMap paramHashMap, PdfObject paramPdfObject, IntHashtable paramIntHashtable)
  {
    if (paramPdfObject.isString())
      paramHashMap.put("Named", paramPdfObject.toString());
    else if (paramPdfObject.isName())
      paramHashMap.put("Named", PdfName.decodeName(paramPdfObject.toString()));
    else if (paramPdfObject.isArray())
      paramHashMap.put("Page", makeBookmarkParam((PdfArray)paramPdfObject, paramIntHashtable));
    paramHashMap.put("Action", "GoTo");
  }

  private static String makeBookmarkParam(PdfArray paramPdfArray, IntHashtable paramIntHashtable)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    PdfObject localPdfObject = paramPdfArray.getPdfObject(0);
    if (localPdfObject.isNumber())
      localStringBuffer.append(((PdfNumber)localPdfObject).intValue() + 1);
    else
      localStringBuffer.append(paramIntHashtable.get(getNumber((PdfIndirectReference)localPdfObject)));
    localStringBuffer.append(' ').append(paramPdfArray.getPdfObject(1).toString().substring(1));
    for (int i = 2; i < paramPdfArray.size(); i++)
      localStringBuffer.append(' ').append(paramPdfArray.getPdfObject(i).toString());
    return localStringBuffer.toString();
  }

  private static int getNumber(PdfIndirectReference paramPdfIndirectReference)
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPdfIndirectReference);
    if ((localPdfDictionary.contains(PdfName.TYPE)) && (localPdfDictionary.get(PdfName.TYPE).equals(PdfName.PAGES)) && (localPdfDictionary.contains(PdfName.KIDS)))
    {
      PdfArray localPdfArray = (PdfArray)localPdfDictionary.get(PdfName.KIDS);
      paramPdfIndirectReference = (PdfIndirectReference)localPdfArray.getPdfObject(0);
    }
    return paramPdfIndirectReference.getNumber();
  }

  public static List getBookmark(PdfReader paramPdfReader)
  {
    PdfDictionary localPdfDictionary1 = paramPdfReader.getCatalog();
    PdfObject localPdfObject = PdfReader.getPdfObjectRelease(localPdfDictionary1.get(PdfName.OUTLINES));
    if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
      return null;
    PdfDictionary localPdfDictionary2 = (PdfDictionary)localPdfObject;
    IntHashtable localIntHashtable = new IntHashtable();
    int i = paramPdfReader.getNumberOfPages();
    for (int j = 1; j <= i; j++)
    {
      localIntHashtable.put(paramPdfReader.getPageOrigRef(j).getNumber(), j);
      paramPdfReader.releasePage(j);
    }
    return bookmarkDepth(paramPdfReader, (PdfDictionary)PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.FIRST)), localIntHashtable);
  }

  public static void eliminatePages(List paramList, int[] paramArrayOfInt)
  {
    if (paramList == null)
      return;
    ListIterator localListIterator = paramList.listIterator();
    while (localListIterator.hasNext())
    {
      HashMap localHashMap = (HashMap)localListIterator.next();
      int i = 0;
      if ("GoTo".equals(localHashMap.get("Action")))
      {
        localObject = (String)localHashMap.get("Page");
        if (localObject != null)
        {
          localObject = ((String)localObject).trim();
          int j = ((String)localObject).indexOf(' ');
          int k;
          if (j < 0)
            k = Integer.parseInt((String)localObject);
          else
            k = Integer.parseInt(((String)localObject).substring(0, j));
          int m = paramArrayOfInt.length & 0xFFFFFFFE;
          for (int n = 0; n < m; n += 2)
          {
            if ((k < paramArrayOfInt[n]) || (k > paramArrayOfInt[(n + 1)]))
              continue;
            i = 1;
            break;
          }
        }
      }
      Object localObject = (List)localHashMap.get("Kids");
      if (localObject != null)
      {
        eliminatePages((List)localObject, paramArrayOfInt);
        if (((List)localObject).isEmpty())
        {
          localHashMap.remove("Kids");
          localObject = null;
        }
      }
      if (i == 0)
        continue;
      if (localObject == null)
      {
        localListIterator.remove();
        continue;
      }
      localHashMap.remove("Action");
      localHashMap.remove("Page");
      localHashMap.remove("Named");
    }
  }

  public static void shiftPageNumbers(List paramList, int paramInt, int[] paramArrayOfInt)
  {
    if (paramList == null)
      return;
    ListIterator localListIterator = paramList.listIterator();
    while (localListIterator.hasNext())
    {
      HashMap localHashMap = (HashMap)localListIterator.next();
      if ("GoTo".equals(localHashMap.get("Action")))
      {
        localObject = (String)localHashMap.get("Page");
        if (localObject != null)
        {
          localObject = ((String)localObject).trim();
          int i = ((String)localObject).indexOf(' ');
          int j;
          if (i < 0)
            j = Integer.parseInt((String)localObject);
          else
            j = Integer.parseInt(((String)localObject).substring(0, i));
          int k = 0;
          if (paramArrayOfInt == null)
          {
            k = 1;
          }
          else
          {
            int m = paramArrayOfInt.length & 0xFFFFFFFE;
            for (int n = 0; n < m; n += 2)
            {
              if ((j < paramArrayOfInt[n]) || (j > paramArrayOfInt[(n + 1)]))
                continue;
              k = 1;
              break;
            }
          }
          if (k != 0)
            if (i < 0)
              localObject = Integer.toString(j + paramInt);
            else
              localObject = j + paramInt + ((String)localObject).substring(i);
          localHashMap.put("Page", localObject);
        }
      }
      Object localObject = (List)localHashMap.get("Kids");
      if (localObject == null)
        continue;
      shiftPageNumbers((List)localObject, paramInt, paramArrayOfInt);
    }
  }

  static void createOutlineAction(PdfDictionary paramPdfDictionary, HashMap paramHashMap, PdfWriter paramPdfWriter, boolean paramBoolean)
  {
    try
    {
      String str1 = (String)paramHashMap.get("Action");
      String str2;
      Object localObject1;
      Object localObject2;
      String str3;
      int j;
      if ("GoTo".equals(str1))
      {
        if ((str2 = (String)paramHashMap.get("Named")) != null)
        {
          if (paramBoolean)
            paramPdfDictionary.put(PdfName.DEST, new PdfName(str2));
          else
            paramPdfDictionary.put(PdfName.DEST, new PdfString(str2, null));
        }
        else if ((str2 = (String)paramHashMap.get("Page")) != null)
        {
          localObject1 = new PdfArray();
          localObject2 = new StringTokenizer(str2);
          int i = Integer.parseInt(((StringTokenizer)localObject2).nextToken());
          ((PdfArray)localObject1).add(paramPdfWriter.getPageReference(i));
          if (!((StringTokenizer)localObject2).hasMoreTokens())
          {
            ((PdfArray)localObject1).add(PdfName.XYZ);
            ((PdfArray)localObject1).add(new float[] { 0.0F, 10000.0F, 0.0F });
          }
          else
          {
            str3 = ((StringTokenizer)localObject2).nextToken();
            if (str3.startsWith("/"))
              str3 = str3.substring(1);
            ((PdfArray)localObject1).add(new PdfName(str3));
            for (j = 0; (j < 4) && (((StringTokenizer)localObject2).hasMoreTokens()); j++)
            {
              str3 = ((StringTokenizer)localObject2).nextToken();
              if (str3.equals("null"))
                ((PdfArray)localObject1).add(PdfNull.PDFNULL);
              else
                ((PdfArray)localObject1).add(new PdfNumber(str3));
            }
          }
          paramPdfDictionary.put(PdfName.DEST, (PdfObject)localObject1);
        }
      }
      else if ("GoToR".equals(str1))
      {
        localObject1 = new PdfDictionary();
        Object localObject3;
        if ((str2 = (String)paramHashMap.get("Named")) != null)
        {
          ((PdfDictionary)localObject1).put(PdfName.D, new PdfString(str2, null));
        }
        else if ((str2 = (String)paramHashMap.get("NamedN")) != null)
        {
          ((PdfDictionary)localObject1).put(PdfName.D, new PdfName(str2));
        }
        else if ((str2 = (String)paramHashMap.get("Page")) != null)
        {
          localObject2 = new PdfArray();
          localObject3 = new StringTokenizer(str2);
          ((PdfArray)localObject2).add(new PdfNumber(((StringTokenizer)localObject3).nextToken()));
          if (!((StringTokenizer)localObject3).hasMoreTokens())
          {
            ((PdfArray)localObject2).add(PdfName.XYZ);
            ((PdfArray)localObject2).add(new float[] { 0.0F, 10000.0F, 0.0F });
          }
          else
          {
            str3 = ((StringTokenizer)localObject3).nextToken();
            if (str3.startsWith("/"))
              str3 = str3.substring(1);
            ((PdfArray)localObject2).add(new PdfName(str3));
            for (j = 0; (j < 4) && (((StringTokenizer)localObject3).hasMoreTokens()); j++)
            {
              str3 = ((StringTokenizer)localObject3).nextToken();
              if (str3.equals("null"))
                ((PdfArray)localObject2).add(PdfNull.PDFNULL);
              else
                ((PdfArray)localObject2).add(new PdfNumber(str3));
            }
          }
          ((PdfDictionary)localObject1).put(PdfName.D, (PdfObject)localObject2);
        }
        localObject2 = (String)paramHashMap.get("File");
        if ((((PdfDictionary)localObject1).size() > 0) && (localObject2 != null))
        {
          ((PdfDictionary)localObject1).put(PdfName.S, PdfName.GOTOR);
          ((PdfDictionary)localObject1).put(PdfName.F, new PdfString((String)localObject2));
          localObject3 = (String)paramHashMap.get("NewWindow");
          if (localObject3 != null)
            if (((String)localObject3).equals("true"))
              ((PdfDictionary)localObject1).put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
            else if (((String)localObject3).equals("false"))
              ((PdfDictionary)localObject1).put(PdfName.NEWWINDOW, PdfBoolean.PDFFALSE);
          paramPdfDictionary.put(PdfName.A, (PdfObject)localObject1);
        }
      }
      else if ("URI".equals(str1))
      {
        str2 = (String)paramHashMap.get("URI");
        if (str2 != null)
        {
          localObject1 = new PdfDictionary();
          ((PdfDictionary)localObject1).put(PdfName.S, PdfName.URI);
          ((PdfDictionary)localObject1).put(PdfName.URI, new PdfString(str2));
          paramPdfDictionary.put(PdfName.A, (PdfObject)localObject1);
        }
      }
      else if ("Launch".equals(str1))
      {
        str2 = (String)paramHashMap.get("File");
        if (str2 != null)
        {
          localObject1 = new PdfDictionary();
          ((PdfDictionary)localObject1).put(PdfName.S, PdfName.LAUNCH);
          ((PdfDictionary)localObject1).put(PdfName.F, new PdfString(str2));
          paramPdfDictionary.put(PdfName.A, (PdfObject)localObject1);
        }
      }
    }
    catch (Exception localException)
    {
    }
  }

  public static Object[] iterateOutlines(PdfWriter paramPdfWriter, PdfIndirectReference paramPdfIndirectReference, List paramList, boolean paramBoolean)
    throws IOException
  {
    PdfIndirectReference[] arrayOfPdfIndirectReference = new PdfIndirectReference[paramList.size()];
    for (int i = 0; i < arrayOfPdfIndirectReference.length; i++)
      arrayOfPdfIndirectReference[i] = paramPdfWriter.getPdfIndirectReference();
    i = 0;
    int j = 0;
    ListIterator localListIterator = paramList.listIterator();
    while (localListIterator.hasNext())
    {
      HashMap localHashMap = (HashMap)localListIterator.next();
      Object[] arrayOfObject = null;
      List localList = (List)localHashMap.get("Kids");
      if ((localList != null) && (!localList.isEmpty()))
        arrayOfObject = iterateOutlines(paramPdfWriter, arrayOfPdfIndirectReference[i], localList, paramBoolean);
      PdfDictionary localPdfDictionary = new PdfDictionary();
      j++;
      if (arrayOfObject != null)
      {
        localPdfDictionary.put(PdfName.FIRST, (PdfIndirectReference)arrayOfObject[0]);
        localPdfDictionary.put(PdfName.LAST, (PdfIndirectReference)arrayOfObject[1]);
        int k = ((Integer)arrayOfObject[2]).intValue();
        if ("false".equals(localHashMap.get("Open")))
        {
          localPdfDictionary.put(PdfName.COUNT, new PdfNumber(-k));
        }
        else
        {
          localPdfDictionary.put(PdfName.COUNT, new PdfNumber(k));
          j += k;
        }
      }
      localPdfDictionary.put(PdfName.PARENT, paramPdfIndirectReference);
      if (i > 0)
        localPdfDictionary.put(PdfName.PREV, arrayOfPdfIndirectReference[(i - 1)]);
      if (i < arrayOfPdfIndirectReference.length - 1)
        localPdfDictionary.put(PdfName.NEXT, arrayOfPdfIndirectReference[(i + 1)]);
      localPdfDictionary.put(PdfName.TITLE, new PdfString((String)localHashMap.get("Title"), "UnicodeBig"));
      String str1 = (String)localHashMap.get("Color");
      if (str1 != null)
        try
        {
          PdfArray localPdfArray = new PdfArray();
          StringTokenizer localStringTokenizer = new StringTokenizer(str1);
          for (int n = 0; n < 3; n++)
          {
            float f = Float.parseFloat(localStringTokenizer.nextToken());
            if (f < 0.0F)
              f = 0.0F;
            if (f > 1.0F)
              f = 1.0F;
            localPdfArray.add(new PdfNumber(f));
          }
          localPdfDictionary.put(PdfName.C, localPdfArray);
        }
        catch (Exception localException)
        {
        }
      String str2 = (String)localHashMap.get("Style");
      if (str2 != null)
      {
        str2 = str2.toLowerCase();
        int m = 0;
        if (str2.indexOf("italic") >= 0)
          m |= 1;
        if (str2.indexOf("bold") >= 0)
          m |= 2;
        if (m != 0)
          localPdfDictionary.put(PdfName.F, new PdfNumber(m));
      }
      createOutlineAction(localPdfDictionary, localHashMap, paramPdfWriter, paramBoolean);
      paramPdfWriter.addToBody(localPdfDictionary, arrayOfPdfIndirectReference[i]);
      i++;
    }
    return new Object[] { arrayOfPdfIndirectReference[0], arrayOfPdfIndirectReference[(arrayOfPdfIndirectReference.length - 1)], new Integer(j) };
  }

  public static void exportToXMLNode(List paramList, Writer paramWriter, int paramInt, boolean paramBoolean)
    throws IOException
  {
    String str1 = "";
    for (int i = 0; i < paramInt; i++)
      str1 = str1 + "  ";
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      HashMap localHashMap = (HashMap)localIterator1.next();
      String str2 = null;
      paramWriter.write(str1);
      paramWriter.write("<Title ");
      List localList = null;
      Iterator localIterator2 = localHashMap.entrySet().iterator();
      while (localIterator2.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator2.next();
        String str3 = (String)localEntry.getKey();
        if (str3.equals("Title"))
        {
          str2 = (String)localEntry.getValue();
          continue;
        }
        if (str3.equals("Kids"))
        {
          localList = (List)localEntry.getValue();
          continue;
        }
        paramWriter.write(str3);
        paramWriter.write("=\"");
        String str4 = (String)localEntry.getValue();
        if ((str3.equals("Named")) || (str3.equals("NamedN")))
          str4 = SimpleNamedDestination.escapeBinaryString(str4);
        paramWriter.write(SimpleXMLParser.escapeXML(str4, paramBoolean));
        paramWriter.write("\" ");
      }
      paramWriter.write(">");
      if (str2 == null)
        str2 = "";
      paramWriter.write(SimpleXMLParser.escapeXML(str2, paramBoolean));
      if (localList != null)
      {
        paramWriter.write("\n");
        exportToXMLNode(localList, paramWriter, paramInt + 1, paramBoolean);
        paramWriter.write(str1);
      }
      paramWriter.write("</Title>\n");
    }
  }

  public static void exportToXML(List paramList, OutputStream paramOutputStream, String paramString, boolean paramBoolean)
    throws IOException
  {
    String str = IanaEncodings.getJavaEncoding(paramString);
    BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(paramOutputStream, str));
    exportToXML(paramList, localBufferedWriter, paramString, paramBoolean);
  }

  public static void exportToXML(List paramList, Writer paramWriter, String paramString, boolean paramBoolean)
    throws IOException
  {
    paramWriter.write("<?xml version=\"1.0\" encoding=\"");
    paramWriter.write(SimpleXMLParser.escapeXML(paramString, paramBoolean));
    paramWriter.write("\"?>\n<Bookmark>\n");
    exportToXMLNode(paramList, paramWriter, 1, paramBoolean);
    paramWriter.write("</Bookmark>\n");
    paramWriter.flush();
  }

  public static List importFromXML(InputStream paramInputStream)
    throws IOException
  {
    SimpleBookmark localSimpleBookmark = new SimpleBookmark();
    SimpleXMLParser.parse(localSimpleBookmark, paramInputStream);
    return localSimpleBookmark.topList;
  }

  public static List importFromXML(Reader paramReader)
    throws IOException
  {
    SimpleBookmark localSimpleBookmark = new SimpleBookmark();
    SimpleXMLParser.parse(localSimpleBookmark, paramReader);
    return localSimpleBookmark.topList;
  }

  public void endDocument()
  {
  }

  public void endElement(String paramString)
  {
    if (paramString.equals("Bookmark"))
    {
      if (this.attr.isEmpty())
        return;
      throw new RuntimeException("Bookmark end tag out of place.");
    }
    if (!paramString.equals("Title"))
      throw new RuntimeException("Invalid end tag - " + paramString);
    HashMap localHashMap1 = (HashMap)this.attr.pop();
    String str1 = (String)localHashMap1.get("Title");
    localHashMap1.put("Title", str1.trim());
    String str2 = (String)localHashMap1.get("Named");
    if (str2 != null)
      localHashMap1.put("Named", SimpleNamedDestination.unEscapeBinaryString(str2));
    str2 = (String)localHashMap1.get("NamedN");
    if (str2 != null)
      localHashMap1.put("NamedN", SimpleNamedDestination.unEscapeBinaryString(str2));
    if (this.attr.isEmpty())
    {
      this.topList.add(localHashMap1);
    }
    else
    {
      HashMap localHashMap2 = (HashMap)this.attr.peek();
      Object localObject = (List)localHashMap2.get("Kids");
      if (localObject == null)
      {
        localObject = new ArrayList();
        localHashMap2.put("Kids", localObject);
      }
      ((List)localObject).add(localHashMap1);
    }
  }

  public void startDocument()
  {
  }

  public void startElement(String paramString, HashMap paramHashMap)
  {
    if (this.topList == null)
    {
      if (paramString.equals("Bookmark"))
      {
        this.topList = new ArrayList();
        return;
      }
      throw new RuntimeException("Root element is not Bookmark: " + paramString);
    }
    if (!paramString.equals("Title"))
      throw new RuntimeException("Tag " + paramString + " not allowed.");
    HashMap localHashMap = new HashMap(paramHashMap);
    localHashMap.put("Title", "");
    localHashMap.remove("Kids");
    this.attr.push(localHashMap);
  }

  public void text(String paramString)
  {
    if (this.attr.isEmpty())
      return;
    HashMap localHashMap = (HashMap)this.attr.peek();
    String str = (String)localHashMap.get("Title");
    str = str + paramString;
    localHashMap.put("Title", str);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.SimpleBookmark
 * JD-Core Version:    0.6.0
 */