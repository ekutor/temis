package com.lowagie.text.pdf;

import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class XfdfReader
  implements SimpleXMLDocHandler
{
  private boolean foundRoot = false;
  private Stack fieldNames = new Stack();
  private Stack fieldValues = new Stack();
  HashMap fields;
  protected HashMap listFields;
  String fileSpec;

  public XfdfReader(String paramString)
    throws IOException
  {
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(paramString);
      SimpleXMLParser.parse(this, localFileInputStream);
    }
    finally
    {
      try
      {
        if (localFileInputStream != null)
          localFileInputStream.close();
      }
      catch (Exception localException)
      {
      }
    }
  }

  public XfdfReader(byte[] paramArrayOfByte)
    throws IOException
  {
    SimpleXMLParser.parse(this, new ByteArrayInputStream(paramArrayOfByte));
  }

  public HashMap getFields()
  {
    return this.fields;
  }

  public String getField(String paramString)
  {
    return (String)this.fields.get(paramString);
  }

  public String getFieldValue(String paramString)
  {
    String str = (String)this.fields.get(paramString);
    if (str == null)
      return null;
    return str;
  }

  public List getListValues(String paramString)
  {
    return (List)this.listFields.get(paramString);
  }

  public String getFileSpec()
  {
    return this.fileSpec;
  }

  public void startElement(String paramString, HashMap paramHashMap)
  {
    if (!this.foundRoot)
    {
      if (!paramString.equals("xfdf"))
        throw new RuntimeException("Root element is not Bookmark.");
      this.foundRoot = true;
    }
    if (!paramString.equals("xfdf"))
      if (paramString.equals("f"))
      {
        this.fileSpec = ((String)paramHashMap.get("href"));
      }
      else if (paramString.equals("fields"))
      {
        this.fields = new HashMap();
        this.listFields = new HashMap();
      }
      else if (paramString.equals("field"))
      {
        String str = (String)paramHashMap.get("name");
        this.fieldNames.push(str);
      }
      else if (paramString.equals("value"))
      {
        this.fieldValues.push("");
      }
  }

  public void endElement(String paramString)
  {
    if (paramString.equals("value"))
    {
      String str1 = "";
      for (int i = 0; i < this.fieldNames.size(); i++)
        str1 = str1 + "." + (String)this.fieldNames.elementAt(i);
      if (str1.startsWith("."))
        str1 = str1.substring(1);
      String str2 = (String)this.fieldValues.pop();
      String str3 = (String)this.fields.put(str1, str2);
      if (str3 != null)
      {
        Object localObject = (List)this.listFields.get(str1);
        if (localObject == null)
        {
          localObject = new ArrayList();
          ((List)localObject).add(str3);
        }
        ((List)localObject).add(str2);
        this.listFields.put(str1, localObject);
      }
    }
    else if ((paramString.equals("field")) && (!this.fieldNames.isEmpty()))
    {
      this.fieldNames.pop();
    }
  }

  public void startDocument()
  {
    this.fileSpec = "";
  }

  public void endDocument()
  {
  }

  public void text(String paramString)
  {
    if ((this.fieldNames.isEmpty()) || (this.fieldValues.isEmpty()))
      return;
    String str = (String)this.fieldValues.pop();
    str = str + paramString;
    this.fieldValues.push(str);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.XfdfReader
 * JD-Core Version:    0.6.0
 */