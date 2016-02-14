package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class FdfWriter
{
  private static final byte[] HEADER_FDF = DocWriter.getISOBytes("%FDF-1.2\n%âãÏÓ\n");
  HashMap fields = new HashMap();
  private String file;

  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    Wrt localWrt = new Wrt(paramOutputStream, this);
    localWrt.writeTo();
  }

  boolean setField(String paramString, PdfObject paramPdfObject)
  {
    HashMap localHashMap = this.fields;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
    if (!localStringTokenizer.hasMoreTokens())
      return false;
    String str;
    Object localObject;
    while (true)
    {
      str = localStringTokenizer.nextToken();
      localObject = localHashMap.get(str);
      if (!localStringTokenizer.hasMoreTokens())
        break;
      if (localObject == null)
      {
        localObject = new HashMap();
        localHashMap.put(str, localObject);
        localHashMap = (HashMap)localObject;
        continue;
      }
      if ((localObject instanceof HashMap))
      {
        localHashMap = (HashMap)localObject;
        continue;
      }
      return false;
    }
    if (!(localObject instanceof HashMap))
    {
      localHashMap.put(str, paramPdfObject);
      return true;
    }
    return false;
  }

  void iterateFields(HashMap paramHashMap1, HashMap paramHashMap2, String paramString)
  {
    Iterator localIterator = paramHashMap2.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      Object localObject = localEntry.getValue();
      if ((localObject instanceof HashMap))
      {
        iterateFields(paramHashMap1, (HashMap)localObject, paramString + "." + str);
        continue;
      }
      paramHashMap1.put((paramString + "." + str).substring(1), localObject);
    }
  }

  public boolean removeField(String paramString)
  {
    HashMap localHashMap = this.fields;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
    if (!localStringTokenizer.hasMoreTokens())
      return false;
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    while (true)
    {
      String str = localStringTokenizer.nextToken();
      localObject = localHashMap.get(str);
      if (localObject == null)
        return false;
      localArrayList.add(localHashMap);
      localArrayList.add(str);
      if (!localStringTokenizer.hasMoreTokens())
        break;
      if ((localObject instanceof HashMap))
      {
        localHashMap = (HashMap)localObject;
        continue;
      }
      return false;
    }
    if ((localObject instanceof HashMap))
      return false;
    for (int i = localArrayList.size() - 2; i >= 0; i -= 2)
    {
      localHashMap = (HashMap)localArrayList.get(i);
      localObject = (String)localArrayList.get(i + 1);
      localHashMap.remove(localObject);
      if (!localHashMap.isEmpty())
        break;
    }
    return true;
  }

  public HashMap getFields()
  {
    HashMap localHashMap = new HashMap();
    iterateFields(localHashMap, this.fields, "");
    return localHashMap;
  }

  public String getField(String paramString)
  {
    HashMap localHashMap = this.fields;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
    if (!localStringTokenizer.hasMoreTokens())
      return null;
    Object localObject;
    while (true)
    {
      String str = localStringTokenizer.nextToken();
      localObject = localHashMap.get(str);
      if (localObject == null)
        return null;
      if (!localStringTokenizer.hasMoreTokens())
        break;
      if ((localObject instanceof HashMap))
      {
        localHashMap = (HashMap)localObject;
        continue;
      }
      return null;
    }
    if ((localObject instanceof HashMap))
      return null;
    if (((PdfObject)localObject).isString())
      return ((PdfString)localObject).toUnicodeString();
    return PdfName.decodeName(localObject.toString());
  }

  public boolean setFieldAsName(String paramString1, String paramString2)
  {
    return setField(paramString1, new PdfName(paramString2));
  }

  public boolean setFieldAsString(String paramString1, String paramString2)
  {
    return setField(paramString1, new PdfString(paramString2, "UnicodeBig"));
  }

  public boolean setFieldAsAction(String paramString, PdfAction paramPdfAction)
  {
    return setField(paramString, paramPdfAction);
  }

  public void setFields(FdfReader paramFdfReader)
  {
    HashMap localHashMap = paramFdfReader.getFields();
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      PdfDictionary localPdfDictionary = (PdfDictionary)localEntry.getValue();
      PdfObject localPdfObject = localPdfDictionary.get(PdfName.V);
      if (localPdfObject != null)
        setField(str, localPdfObject);
      localPdfObject = localPdfDictionary.get(PdfName.A);
      if (localPdfObject == null)
        continue;
      setField(str, localPdfObject);
    }
  }

  public void setFields(PdfReader paramPdfReader)
  {
    setFields(paramPdfReader.getAcroFields());
  }

  public void setFields(AcroFields paramAcroFields)
  {
    Iterator localIterator = paramAcroFields.getFields().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      AcroFields.Item localItem = (AcroFields.Item)localEntry.getValue();
      PdfDictionary localPdfDictionary = localItem.getMerged(0);
      PdfObject localPdfObject1 = PdfReader.getPdfObjectRelease(localPdfDictionary.get(PdfName.V));
      if (localPdfObject1 == null)
        continue;
      PdfObject localPdfObject2 = PdfReader.getPdfObjectRelease(localPdfDictionary.get(PdfName.FT));
      if ((localPdfObject2 == null) || (PdfName.SIG.equals(localPdfObject2)))
        continue;
      setField(str, localPdfObject1);
    }
  }

  public String getFile()
  {
    return this.file;
  }

  public void setFile(String paramString)
  {
    this.file = paramString;
  }

  static class Wrt extends PdfWriter
  {
    private FdfWriter fdf;

    Wrt(OutputStream paramOutputStream, FdfWriter paramFdfWriter)
      throws IOException
    {
      super(paramOutputStream);
      this.fdf = paramFdfWriter;
      this.os.write(FdfWriter.HEADER_FDF);
      this.body = new PdfWriter.PdfBody(this);
    }

    void writeTo()
      throws IOException
    {
      PdfDictionary localPdfDictionary1 = new PdfDictionary();
      localPdfDictionary1.put(PdfName.FIELDS, calculate(this.fdf.fields));
      if (this.fdf.file != null)
        localPdfDictionary1.put(PdfName.F, new PdfString(this.fdf.file, "UnicodeBig"));
      PdfDictionary localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary2.put(PdfName.FDF, localPdfDictionary1);
      PdfIndirectReference localPdfIndirectReference = addToBody(localPdfDictionary2).getIndirectReference();
      this.os.write(getISOBytes("trailer\n"));
      PdfDictionary localPdfDictionary3 = new PdfDictionary();
      localPdfDictionary3.put(PdfName.ROOT, localPdfIndirectReference);
      localPdfDictionary3.toPdf(null, this.os);
      this.os.write(getISOBytes("\n%%EOF\n"));
      this.os.close();
    }

    PdfArray calculate(HashMap paramHashMap)
      throws IOException
    {
      PdfArray localPdfArray = new PdfArray();
      Iterator localIterator = paramHashMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        Object localObject = localEntry.getValue();
        PdfDictionary localPdfDictionary = new PdfDictionary();
        localPdfDictionary.put(PdfName.T, new PdfString(str, "UnicodeBig"));
        if ((localObject instanceof HashMap))
          localPdfDictionary.put(PdfName.KIDS, calculate((HashMap)localObject));
        else if ((localObject instanceof PdfAction))
          localPdfDictionary.put(PdfName.A, (PdfAction)localObject);
        else
          localPdfDictionary.put(PdfName.V, (PdfObject)localObject);
        localPdfArray.add(localPdfDictionary);
      }
      return localPdfArray;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.FdfWriter
 * JD-Core Version:    0.6.0
 */