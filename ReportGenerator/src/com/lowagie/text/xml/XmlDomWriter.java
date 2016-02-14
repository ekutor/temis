package com.lowagie.text.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlDomWriter
{
  protected PrintWriter fOut;
  protected boolean fCanonical;
  protected boolean fXML11;

  public XmlDomWriter()
  {
  }

  public XmlDomWriter(boolean paramBoolean)
  {
    this.fCanonical = paramBoolean;
  }

  public void setCanonical(boolean paramBoolean)
  {
    this.fCanonical = paramBoolean;
  }

  public void setOutput(OutputStream paramOutputStream, String paramString)
    throws UnsupportedEncodingException
  {
    if (paramString == null)
      paramString = "UTF8";
    OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(paramOutputStream, paramString);
    this.fOut = new PrintWriter(localOutputStreamWriter);
  }

  public void setOutput(Writer paramWriter)
  {
    this.fOut = ((paramWriter instanceof PrintWriter) ? (PrintWriter)paramWriter : new PrintWriter(paramWriter));
  }

  public void write(Node paramNode)
  {
    if (paramNode == null)
      return;
    int i = paramNode.getNodeType();
    Object localObject;
    String str2;
    Node localNode;
    switch (i)
    {
    case 9:
      localObject = (Document)paramNode;
      this.fXML11 = false;
      if (!this.fCanonical)
      {
        if (this.fXML11)
          this.fOut.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
        else
          this.fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.fOut.flush();
        write(((Document)localObject).getDoctype());
      }
      write(((Document)localObject).getDocumentElement());
      break;
    case 10:
      localObject = (DocumentType)paramNode;
      this.fOut.print("<!DOCTYPE ");
      this.fOut.print(((DocumentType)localObject).getName());
      String str1 = ((DocumentType)localObject).getPublicId();
      str2 = ((DocumentType)localObject).getSystemId();
      if (str1 != null)
      {
        this.fOut.print(" PUBLIC '");
        this.fOut.print(str1);
        this.fOut.print("' '");
        this.fOut.print(str2);
        this.fOut.print('\'');
      }
      else if (str2 != null)
      {
        this.fOut.print(" SYSTEM '");
        this.fOut.print(str2);
        this.fOut.print('\'');
      }
      String str3 = ((DocumentType)localObject).getInternalSubset();
      if (str3 != null)
      {
        this.fOut.println(" [");
        this.fOut.print(str3);
        this.fOut.print(']');
      }
      this.fOut.println('>');
      break;
    case 1:
      this.fOut.print('<');
      this.fOut.print(paramNode.getNodeName());
      localObject = sortAttributes(paramNode.getAttributes());
      for (int j = 0; j < localObject.length; j++)
      {
        str2 = localObject[j];
        this.fOut.print(' ');
        this.fOut.print(str2.getNodeName());
        this.fOut.print("=\"");
        normalizeAndPrint(str2.getNodeValue(), true);
        this.fOut.print('"');
      }
      this.fOut.print('>');
      this.fOut.flush();
      localNode = paramNode.getFirstChild();
    case 5:
    case 4:
    case 3:
    case 7:
    case 8:
      while (localNode != null)
      {
        write(localNode);
        localNode = localNode.getNextSibling();
        continue;
        if (this.fCanonical)
          for (localObject = paramNode.getFirstChild(); localObject != null; localObject = ((Node)localObject).getNextSibling())
            write((Node)localObject);
        this.fOut.print('&');
        this.fOut.print(paramNode.getNodeName());
        this.fOut.print(';');
        this.fOut.flush();
        break;
        if (this.fCanonical)
        {
          normalizeAndPrint(paramNode.getNodeValue(), false);
        }
        else
        {
          this.fOut.print("<![CDATA[");
          this.fOut.print(paramNode.getNodeValue());
          this.fOut.print("]]>");
        }
        this.fOut.flush();
        break;
        normalizeAndPrint(paramNode.getNodeValue(), false);
        this.fOut.flush();
        break;
        this.fOut.print("<?");
        this.fOut.print(paramNode.getNodeName());
        localObject = paramNode.getNodeValue();
        if ((localObject != null) && (((String)localObject).length() > 0))
        {
          this.fOut.print(' ');
          this.fOut.print((String)localObject);
        }
        this.fOut.print("?>");
        this.fOut.flush();
        break;
        if (this.fCanonical)
          break;
        this.fOut.print("<!--");
        localObject = paramNode.getNodeValue();
        if ((localObject != null) && (((String)localObject).length() > 0))
          this.fOut.print((String)localObject);
        this.fOut.print("-->");
        this.fOut.flush();
      }
    case 2:
    case 6:
    }
    if (i == 1)
    {
      this.fOut.print("</");
      this.fOut.print(paramNode.getNodeName());
      this.fOut.print('>');
      this.fOut.flush();
    }
  }

  protected Attr[] sortAttributes(NamedNodeMap paramNamedNodeMap)
  {
    int i = paramNamedNodeMap != null ? paramNamedNodeMap.getLength() : 0;
    Attr[] arrayOfAttr = new Attr[i];
    for (int j = 0; j < i; j++)
      arrayOfAttr[j] = ((Attr)paramNamedNodeMap.item(j));
    for (j = 0; j < i - 1; j++)
    {
      Object localObject = arrayOfAttr[j].getNodeName();
      int k = j;
      for (int m = j + 1; m < i; m++)
      {
        String str = arrayOfAttr[m].getNodeName();
        if (str.compareTo((String)localObject) >= 0)
          continue;
        localObject = str;
        k = m;
      }
      if (k == j)
        continue;
      Attr localAttr = arrayOfAttr[j];
      arrayOfAttr[j] = arrayOfAttr[k];
      arrayOfAttr[k] = localAttr;
    }
    return (Attr)arrayOfAttr;
  }

  protected void normalizeAndPrint(String paramString, boolean paramBoolean)
  {
    int i = paramString != null ? paramString.length() : 0;
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      normalizeAndPrint(c, paramBoolean);
    }
  }

  protected void normalizeAndPrint(char paramChar, boolean paramBoolean)
  {
    switch (paramChar)
    {
    case '<':
      this.fOut.print("&lt;");
      break;
    case '>':
      this.fOut.print("&gt;");
      break;
    case '&':
      this.fOut.print("&amp;");
      break;
    case '"':
      if (paramBoolean)
      {
        this.fOut.print("&quot;");
        return;
      }
      this.fOut.print("\"");
      break;
    case '\r':
      this.fOut.print("&#xD;");
      break;
    case '\n':
      if (!this.fCanonical)
        break;
      this.fOut.print("&#xA;");
      break;
    }
    if (((this.fXML11) && (((paramChar >= '\001') && (paramChar <= '\037') && (paramChar != '\t') && (paramChar != '\n')) || ((paramChar >= '') && (paramChar <= '')) || (paramChar == ' '))) || ((paramBoolean) && ((paramChar == '\t') || (paramChar == '\n'))))
    {
      this.fOut.print("&#x");
      this.fOut.print(Integer.toHexString(paramChar).toUpperCase());
      this.fOut.print(";");
    }
    else
    {
      this.fOut.print(paramChar);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.XmlDomWriter
 * JD-Core Version:    0.6.0
 */