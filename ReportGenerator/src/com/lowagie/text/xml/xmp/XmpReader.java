package com.lowagie.text.xml.xmp;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmpReader
{
  private Document domDocument;

  public XmpReader(byte[] paramArrayOfByte)
    throws SAXException, IOException
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      this.domDocument = localDocumentBuilder.parse(localByteArrayInputStream);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new ExceptionConverter(localParserConfigurationException);
    }
  }

  public boolean replace(String paramString1, String paramString2, String paramString3)
  {
    NodeList localNodeList = this.domDocument.getElementsByTagNameNS(paramString1, paramString2);
    if (localNodeList.getLength() == 0)
      return false;
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      setNodeText(this.domDocument, localNode, paramString3);
    }
    return true;
  }

  public boolean add(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    NodeList localNodeList = this.domDocument.getElementsByTagName(paramString1);
    if (localNodeList.getLength() == 0)
      return false;
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      NamedNodeMap localNamedNodeMap = localNode.getAttributes();
      for (int j = 0; j < localNamedNodeMap.getLength(); j++)
      {
        Object localObject = localNamedNodeMap.item(j);
        if (!paramString2.equals(((Node)localObject).getNodeValue()))
          continue;
        localObject = this.domDocument.createElement(paramString3);
        ((Node)localObject).appendChild(this.domDocument.createTextNode(paramString4));
        localNode.appendChild((Node)localObject);
        return true;
      }
    }
    return false;
  }

  public boolean setNodeText(Document paramDocument, Node paramNode, String paramString)
  {
    if (paramNode == null)
      return false;
    Node localNode = null;
    while ((localNode = paramNode.getFirstChild()) != null)
      paramNode.removeChild(localNode);
    paramNode.appendChild(paramDocument.createTextNode(paramString));
    return true;
  }

  public byte[] serializeDoc()
    throws IOException
  {
    XmlDomWriter localXmlDomWriter = new XmlDomWriter();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localXmlDomWriter.setOutput(localByteArrayOutputStream, null);
    localByteArrayOutputStream.write("<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n".getBytes("UTF-8"));
    localByteArrayOutputStream.flush();
    NodeList localNodeList = this.domDocument.getElementsByTagName("x:xmpmeta");
    localXmlDomWriter.write(localNodeList.item(0));
    localByteArrayOutputStream.flush();
    for (int i = 0; i < 20; i++)
      localByteArrayOutputStream.write("                                                                                                   \n".getBytes());
    localByteArrayOutputStream.write("<?xpacket end=\"w\"?>".getBytes());
    localByteArrayOutputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.xmp.XmpReader
 * JD-Core Version:    0.6.0
 */