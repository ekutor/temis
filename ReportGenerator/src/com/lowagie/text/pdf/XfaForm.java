package com.lowagie.text.pdf;

import com.lowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XfaForm
{
  private Xml2SomTemplate templateSom;
  private Node templateNode;
  private Xml2SomDatasets datasetsSom;
  private Node datasetsNode;
  private AcroFieldsSearch acroFieldsSom;
  private PdfReader reader;
  private boolean xfaPresent;
  private Document domDocument;
  private boolean changed;
  public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";

  public XfaForm()
  {
  }

  public static PdfObject getXfaObject(PdfReader paramPdfReader)
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPdfReader.getCatalog().get(PdfName.ACROFORM));
    if (localPdfDictionary == null)
      return null;
    return PdfReader.getPdfObjectRelease(localPdfDictionary.get(PdfName.XFA));
  }

  public XfaForm(PdfReader paramPdfReader)
    throws IOException, ParserConfigurationException, SAXException
  {
    this.reader = paramPdfReader;
    PdfObject localPdfObject1 = getXfaObject(paramPdfReader);
    if (localPdfObject1 == null)
    {
      this.xfaPresent = false;
      return;
    }
    this.xfaPresent = true;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    if (localPdfObject1.isArray())
    {
      localObject = (PdfArray)localPdfObject1;
      for (int i = 1; i < ((PdfArray)localObject).size(); i += 2)
      {
        PdfObject localPdfObject2 = ((PdfArray)localObject).getDirectObject(i);
        if (!(localPdfObject2 instanceof PRStream))
          continue;
        byte[] arrayOfByte = PdfReader.getStreamBytes((PRStream)localPdfObject2);
        localByteArrayOutputStream.write(arrayOfByte);
      }
    }
    if ((localPdfObject1 instanceof PRStream))
    {
      localObject = PdfReader.getStreamBytes((PRStream)localPdfObject1);
      localByteArrayOutputStream.write(localObject);
    }
    localByteArrayOutputStream.close();
    Object localObject = DocumentBuilderFactory.newInstance();
    ((DocumentBuilderFactory)localObject).setNamespaceAware(true);
    DocumentBuilder localDocumentBuilder = ((DocumentBuilderFactory)localObject).newDocumentBuilder();
    this.domDocument = localDocumentBuilder.parse(new ByteArrayInputStream(localByteArrayOutputStream.toByteArray()));
    extractNodes();
  }

  private void extractNodes()
  {
    for (Node localNode = this.domDocument.getFirstChild(); localNode.getChildNodes().getLength() == 0; localNode = localNode.getNextSibling());
    for (localNode = localNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
    {
      if (localNode.getNodeType() != 1)
        continue;
      String str = localNode.getLocalName();
      if (str.equals("template"))
      {
        this.templateNode = localNode;
        this.templateSom = new Xml2SomTemplate(localNode);
      }
      else
      {
        if (!str.equals("datasets"))
          continue;
        this.datasetsNode = localNode;
        this.datasetsSom = new Xml2SomDatasets(localNode.getFirstChild());
      }
    }
  }

  public static void setXfa(XfaForm paramXfaForm, PdfReader paramPdfReader, PdfWriter paramPdfWriter)
    throws IOException
  {
    PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPdfReader.getCatalog().get(PdfName.ACROFORM));
    if (localPdfDictionary == null)
      return;
    PdfObject localPdfObject = getXfaObject(paramPdfReader);
    if (localPdfObject.isArray())
    {
      localObject1 = (PdfArray)localPdfObject;
      int i = -1;
      int j = -1;
      Object localObject2;
      for (int k = 0; k < ((PdfArray)localObject1).size(); k += 2)
      {
        localObject2 = ((PdfArray)localObject1).getAsString(k);
        if ("template".equals(((PdfString)localObject2).toString()))
          i = k + 1;
        if (!"datasets".equals(((PdfString)localObject2).toString()))
          continue;
        j = k + 1;
      }
      if ((i > -1) && (j > -1))
      {
        paramPdfReader.killXref(((PdfArray)localObject1).getAsIndirectObject(i));
        paramPdfReader.killXref(((PdfArray)localObject1).getAsIndirectObject(j));
        PdfStream localPdfStream = new PdfStream(serializeDoc(paramXfaForm.templateNode));
        localPdfStream.flateCompress(paramPdfWriter.getCompressionLevel());
        ((PdfArray)localObject1).set(i, paramPdfWriter.addToBody(localPdfStream).getIndirectReference());
        localObject2 = new PdfStream(serializeDoc(paramXfaForm.datasetsNode));
        ((PdfStream)localObject2).flateCompress(paramPdfWriter.getCompressionLevel());
        ((PdfArray)localObject1).set(j, paramPdfWriter.addToBody((PdfObject)localObject2).getIndirectReference());
        localPdfDictionary.put(PdfName.XFA, new PdfArray((PdfArray)localObject1));
        return;
      }
    }
    paramPdfReader.killXref(localPdfDictionary.get(PdfName.XFA));
    Object localObject1 = new PdfStream(serializeDoc(paramXfaForm.domDocument));
    ((PdfStream)localObject1).flateCompress(paramPdfWriter.getCompressionLevel());
    PdfIndirectReference localPdfIndirectReference = paramPdfWriter.addToBody((PdfObject)localObject1).getIndirectReference();
    localPdfDictionary.put(PdfName.XFA, localPdfIndirectReference);
  }

  public void setXfa(PdfWriter paramPdfWriter)
    throws IOException
  {
    setXfa(this, this.reader, paramPdfWriter);
  }

  public static byte[] serializeDoc(Node paramNode)
    throws IOException
  {
    XmlDomWriter localXmlDomWriter = new XmlDomWriter();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localXmlDomWriter.setOutput(localByteArrayOutputStream, null);
    localXmlDomWriter.setCanonical(false);
    localXmlDomWriter.write(paramNode);
    localByteArrayOutputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }

  public boolean isXfaPresent()
  {
    return this.xfaPresent;
  }

  public Document getDomDocument()
  {
    return this.domDocument;
  }

  public String findFieldName(String paramString, AcroFields paramAcroFields)
  {
    HashMap localHashMap = paramAcroFields.getFields();
    if (localHashMap.containsKey(paramString))
      return paramString;
    if (this.acroFieldsSom == null)
      if ((localHashMap.isEmpty()) && (this.xfaPresent))
        this.acroFieldsSom = new AcroFieldsSearch(this.datasetsSom.getName2Node().keySet());
      else
        this.acroFieldsSom = new AcroFieldsSearch(localHashMap.keySet());
    if (this.acroFieldsSom.getAcroShort2LongName().containsKey(paramString))
      return (String)this.acroFieldsSom.getAcroShort2LongName().get(paramString);
    return this.acroFieldsSom.inverseSearchGlobal(Xml2Som.splitParts(paramString));
  }

  public String findDatasetsName(String paramString)
  {
    if (this.datasetsSom.getName2Node().containsKey(paramString))
      return paramString;
    return this.datasetsSom.inverseSearchGlobal(Xml2Som.splitParts(paramString));
  }

  public Node findDatasetsNode(String paramString)
  {
    if (paramString == null)
      return null;
    paramString = findDatasetsName(paramString);
    if (paramString == null)
      return null;
    return (Node)this.datasetsSom.getName2Node().get(paramString);
  }

  public static String getNodeText(Node paramNode)
  {
    if (paramNode == null)
      return "";
    return getNodeText(paramNode, "");
  }

  private static String getNodeText(Node paramNode, String paramString)
  {
    for (Node localNode = paramNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
      if (localNode.getNodeType() == 1)
      {
        paramString = getNodeText(localNode, paramString);
      }
      else
      {
        if (localNode.getNodeType() != 3)
          continue;
        paramString = paramString + localNode.getNodeValue();
      }
    return paramString;
  }

  public void setNodeText(Node paramNode, String paramString)
  {
    if (paramNode == null)
      return;
    Node localNode = null;
    while ((localNode = paramNode.getFirstChild()) != null)
      paramNode.removeChild(localNode);
    if (paramNode.getAttributes().getNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode") != null)
      paramNode.getAttributes().removeNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
    paramNode.appendChild(this.domDocument.createTextNode(paramString));
    this.changed = true;
  }

  public void setXfaPresent(boolean paramBoolean)
  {
    this.xfaPresent = paramBoolean;
  }

  public void setDomDocument(Document paramDocument)
  {
    this.domDocument = paramDocument;
    extractNodes();
  }

  public PdfReader getReader()
  {
    return this.reader;
  }

  public void setReader(PdfReader paramPdfReader)
  {
    this.reader = paramPdfReader;
  }

  public boolean isChanged()
  {
    return this.changed;
  }

  public void setChanged(boolean paramBoolean)
  {
    this.changed = paramBoolean;
  }

  public Xml2SomTemplate getTemplateSom()
  {
    return this.templateSom;
  }

  public void setTemplateSom(Xml2SomTemplate paramXml2SomTemplate)
  {
    this.templateSom = paramXml2SomTemplate;
  }

  public Xml2SomDatasets getDatasetsSom()
  {
    return this.datasetsSom;
  }

  public void setDatasetsSom(Xml2SomDatasets paramXml2SomDatasets)
  {
    this.datasetsSom = paramXml2SomDatasets;
  }

  public AcroFieldsSearch getAcroFieldsSom()
  {
    return this.acroFieldsSom;
  }

  public void setAcroFieldsSom(AcroFieldsSearch paramAcroFieldsSearch)
  {
    this.acroFieldsSom = paramAcroFieldsSearch;
  }

  public Node getDatasetsNode()
  {
    return this.datasetsNode;
  }

  public static class Xml2SomTemplate extends XfaForm.Xml2Som
  {
    private boolean dynamicForm;
    private int templateLevel = 0;

    public Xml2SomTemplate(Node paramNode)
    {
      this.order = new ArrayList();
      this.name2Node = new HashMap();
      this.stack = new XfaForm.Stack2();
      this.anform = 0;
      this.inverseSearch = new HashMap();
      processTemplate(paramNode, null);
    }

    public String getFieldType(String paramString)
    {
      Node localNode1 = (Node)this.name2Node.get(paramString);
      if (localNode1 == null)
        return null;
      if (localNode1.getLocalName().equals("exclGroup"))
        return "exclGroup";
      for (Node localNode2 = localNode1.getFirstChild(); (localNode2 != null) && ((localNode2.getNodeType() != 1) || (!localNode2.getLocalName().equals("ui"))); localNode2 = localNode2.getNextSibling());
      if (localNode2 == null)
        return null;
      for (Node localNode3 = localNode2.getFirstChild(); localNode3 != null; localNode3 = localNode3.getNextSibling())
        if ((localNode3.getNodeType() == 1) && ((!localNode3.getLocalName().equals("extras")) || (!localNode3.getLocalName().equals("picture"))))
          return localNode3.getLocalName();
      return null;
    }

    private void processTemplate(Node paramNode, HashMap paramHashMap)
    {
      if (paramHashMap == null)
        paramHashMap = new HashMap();
      HashMap localHashMap = new HashMap();
      for (Node localNode1 = paramNode.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling())
      {
        if (localNode1.getNodeType() != 1)
          continue;
        String str1 = localNode1.getLocalName();
        Node localNode2;
        String str2;
        Object localObject;
        if (str1.equals("subform"))
        {
          localNode2 = localNode1.getAttributes().getNamedItem("name");
          str2 = "#subform";
          int k = 1;
          if (localNode2 != null)
          {
            str2 = escapeSom(localNode2.getNodeValue());
            k = 0;
          }
          if (k != 0)
          {
            localObject = new Integer(this.anform);
            this.anform += 1;
          }
          else
          {
            localObject = (Integer)localHashMap.get(str2);
            if (localObject == null)
              localObject = new Integer(0);
            else
              localObject = new Integer(((Integer)localObject).intValue() + 1);
            localHashMap.put(str2, localObject);
          }
          this.stack.push(str2 + "[" + ((Integer)localObject).toString() + "]");
          this.templateLevel += 1;
          if (k != 0)
            processTemplate(localNode1, paramHashMap);
          else
            processTemplate(localNode1, null);
          this.templateLevel -= 1;
          this.stack.pop();
        }
        else if ((str1.equals("field")) || (str1.equals("exclGroup")))
        {
          localNode2 = localNode1.getAttributes().getNamedItem("name");
          if (localNode2 == null)
            continue;
          str2 = escapeSom(localNode2.getNodeValue());
          Integer localInteger = (Integer)paramHashMap.get(str2);
          if (localInteger == null)
            localInteger = new Integer(0);
          else
            localInteger = new Integer(localInteger.intValue() + 1);
          paramHashMap.put(str2, localInteger);
          this.stack.push(str2 + "[" + localInteger.toString() + "]");
          localObject = printStack();
          this.order.add(localObject);
          inverseSearchAdd((String)localObject);
          this.name2Node.put(localObject, localNode1);
          this.stack.pop();
        }
        else
        {
          if ((this.dynamicForm) || (this.templateLevel <= 0) || (!str1.equals("occur")))
            continue;
          int i = 1;
          int j = 1;
          int m = 1;
          localObject = localNode1.getAttributes().getNamedItem("initial");
          if (localObject != null)
            try
            {
              i = Integer.parseInt(((Node)localObject).getNodeValue().trim());
            }
            catch (Exception localException1)
            {
            }
          localObject = localNode1.getAttributes().getNamedItem("min");
          if (localObject != null)
            try
            {
              j = Integer.parseInt(((Node)localObject).getNodeValue().trim());
            }
            catch (Exception localException2)
            {
            }
          localObject = localNode1.getAttributes().getNamedItem("max");
          if (localObject != null)
            try
            {
              m = Integer.parseInt(((Node)localObject).getNodeValue().trim());
            }
            catch (Exception localException3)
            {
            }
          if ((i == j) && (j == m))
            continue;
          this.dynamicForm = true;
        }
      }
    }

    public boolean isDynamicForm()
    {
      return this.dynamicForm;
    }

    public void setDynamicForm(boolean paramBoolean)
    {
      this.dynamicForm = paramBoolean;
    }
  }

  public static class AcroFieldsSearch extends XfaForm.Xml2Som
  {
    private HashMap acroShort2LongName = new HashMap();

    public AcroFieldsSearch(Collection paramCollection)
    {
      this.inverseSearch = new HashMap();
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        String str1 = (String)localIterator.next();
        String str2 = getShortName(str1);
        this.acroShort2LongName.put(str2, str1);
        inverseSearchAdd(this.inverseSearch, splitParts(str2), str1);
      }
    }

    public HashMap getAcroShort2LongName()
    {
      return this.acroShort2LongName;
    }

    public void setAcroShort2LongName(HashMap paramHashMap)
    {
      this.acroShort2LongName = paramHashMap;
    }
  }

  public static class Xml2SomDatasets extends XfaForm.Xml2Som
  {
    public Xml2SomDatasets(Node paramNode)
    {
      this.order = new ArrayList();
      this.name2Node = new HashMap();
      this.stack = new XfaForm.Stack2();
      this.anform = 0;
      this.inverseSearch = new HashMap();
      processDatasetsInternal(paramNode);
    }

    public Node insertNode(Node paramNode, String paramString)
    {
      XfaForm.Stack2 localStack2 = splitParts(paramString);
      Document localDocument = paramNode.getOwnerDocument();
      Object localObject1 = null;
      paramNode = paramNode.getFirstChild();
      for (int i = 0; i < localStack2.size(); i++)
      {
        String str1 = (String)localStack2.get(i);
        int j = str1.lastIndexOf('[');
        String str2 = str1.substring(0, j);
        j = Integer.parseInt(str1.substring(j + 1, str1.length() - 1));
        int k = -1;
        Object localObject2;
        for (localObject1 = paramNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
        {
          if (((Node)localObject1).getNodeType() != 1)
            continue;
          localObject2 = escapeSom(((Node)localObject1).getLocalName());
          if (!((String)localObject2).equals(str2))
            continue;
          k++;
          if (k == j)
            break;
        }
        while (k < j)
        {
          localObject1 = localDocument.createElementNS(null, str2);
          localObject1 = paramNode.appendChild((Node)localObject1);
          localObject2 = localDocument.createAttributeNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
          ((Node)localObject2).setNodeValue("dataGroup");
          ((Node)localObject1).getAttributes().setNamedItemNS((Node)localObject2);
          k++;
        }
        paramNode = (Node)localObject1;
      }
      inverseSearchAdd(this.inverseSearch, localStack2, paramString);
      this.name2Node.put(paramString, localObject1);
      this.order.add(paramString);
      return (Node)(Node)localObject1;
    }

    private static boolean hasChildren(Node paramNode)
    {
      Node localNode = paramNode.getAttributes().getNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
      if (localNode != null)
      {
        localObject = localNode.getNodeValue();
        if ("dataGroup".equals(localObject))
          return true;
        if ("dataValue".equals(localObject))
          return false;
      }
      if (!paramNode.hasChildNodes())
        return false;
      for (Object localObject = paramNode.getFirstChild(); localObject != null; localObject = ((Node)localObject).getNextSibling())
        if (((Node)localObject).getNodeType() == 1)
          return true;
      return false;
    }

    private void processDatasetsInternal(Node paramNode)
    {
      HashMap localHashMap = new HashMap();
      for (Node localNode = paramNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
      {
        if (localNode.getNodeType() != 1)
          continue;
        String str1 = escapeSom(localNode.getLocalName());
        Integer localInteger = (Integer)localHashMap.get(str1);
        if (localInteger == null)
          localInteger = new Integer(0);
        else
          localInteger = new Integer(localInteger.intValue() + 1);
        localHashMap.put(str1, localInteger);
        if (hasChildren(localNode))
        {
          this.stack.push(str1 + "[" + localInteger.toString() + "]");
          processDatasetsInternal(localNode);
          this.stack.pop();
        }
        else
        {
          this.stack.push(str1 + "[" + localInteger.toString() + "]");
          String str2 = printStack();
          this.order.add(str2);
          inverseSearchAdd(str2);
          this.name2Node.put(str2, localNode);
          this.stack.pop();
        }
      }
    }
  }

  public static class Xml2Som
  {
    protected ArrayList order;
    protected HashMap name2Node;
    protected HashMap inverseSearch;
    protected XfaForm.Stack2 stack;
    protected int anform;

    public static String escapeSom(String paramString)
    {
      int i = paramString.indexOf('.');
      if (i < 0)
        return paramString;
      StringBuffer localStringBuffer = new StringBuffer();
      int j = 0;
      while (i >= 0)
      {
        localStringBuffer.append(paramString.substring(j, i));
        localStringBuffer.append('\\');
        j = i;
        i = paramString.indexOf('.', i + 1);
      }
      localStringBuffer.append(paramString.substring(j));
      return localStringBuffer.toString();
    }

    public static String unescapeSom(String paramString)
    {
      int i = paramString.indexOf('\\');
      if (i < 0)
        return paramString;
      StringBuffer localStringBuffer = new StringBuffer();
      int j = 0;
      while (i >= 0)
      {
        localStringBuffer.append(paramString.substring(j, i));
        j = i + 1;
        i = paramString.indexOf('\\', i + 1);
      }
      localStringBuffer.append(paramString.substring(j));
      return localStringBuffer.toString();
    }

    protected String printStack()
    {
      if (this.stack.empty())
        return "";
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < this.stack.size(); i++)
        localStringBuffer.append('.').append((String)this.stack.get(i));
      return localStringBuffer.substring(1);
    }

    public static String getShortName(String paramString)
    {
      int i = paramString.indexOf(".#subform[");
      if (i < 0)
        return paramString;
      int j = 0;
      StringBuffer localStringBuffer = new StringBuffer();
      while (i >= 0)
      {
        localStringBuffer.append(paramString.substring(j, i));
        i = paramString.indexOf("]", i + 10);
        if (i < 0)
          return localStringBuffer.toString();
        j = i + 1;
        i = paramString.indexOf(".#subform[", j);
      }
      localStringBuffer.append(paramString.substring(j));
      return localStringBuffer.toString();
    }

    public void inverseSearchAdd(String paramString)
    {
      inverseSearchAdd(this.inverseSearch, this.stack, paramString);
    }

    public static void inverseSearchAdd(HashMap paramHashMap, XfaForm.Stack2 paramStack2, String paramString)
    {
      String str = (String)paramStack2.peek();
      Object localObject = (XfaForm.InverseStore)paramHashMap.get(str);
      if (localObject == null)
      {
        localObject = new XfaForm.InverseStore();
        paramHashMap.put(str, localObject);
      }
      for (int i = paramStack2.size() - 2; i >= 0; i--)
      {
        str = (String)paramStack2.get(i);
        int j = ((XfaForm.InverseStore)localObject).part.indexOf(str);
        XfaForm.InverseStore localInverseStore;
        if (j < 0)
        {
          ((XfaForm.InverseStore)localObject).part.add(str);
          localInverseStore = new XfaForm.InverseStore();
          ((XfaForm.InverseStore)localObject).follow.add(localInverseStore);
        }
        else
        {
          localInverseStore = (XfaForm.InverseStore)((XfaForm.InverseStore)localObject).follow.get(j);
        }
        localObject = localInverseStore;
      }
      ((XfaForm.InverseStore)localObject).part.add("");
      ((XfaForm.InverseStore)localObject).follow.add(paramString);
    }

    public String inverseSearchGlobal(ArrayList paramArrayList)
    {
      if (paramArrayList.isEmpty())
        return null;
      XfaForm.InverseStore localInverseStore = (XfaForm.InverseStore)this.inverseSearch.get(paramArrayList.get(paramArrayList.size() - 1));
      if (localInverseStore == null)
        return null;
      for (int i = paramArrayList.size() - 2; i >= 0; i--)
      {
        String str = (String)paramArrayList.get(i);
        int j = localInverseStore.part.indexOf(str);
        if (j < 0)
        {
          if (localInverseStore.isSimilar(str))
            return null;
          return localInverseStore.getDefaultName();
        }
        localInverseStore = (XfaForm.InverseStore)localInverseStore.follow.get(j);
      }
      return localInverseStore.getDefaultName();
    }

    public static XfaForm.Stack2 splitParts(String paramString)
    {
      while (paramString.startsWith("."))
        paramString = paramString.substring(1);
      XfaForm.Stack2 localStack2 = new XfaForm.Stack2();
      int i = 0;
      int j = 0;
      while (true)
      {
        for (j = i; ; j++)
        {
          j = paramString.indexOf('.', j);
          if ((j < 0) || (paramString.charAt(j - 1) != '\\'))
            break;
        }
        if (j < 0)
          break;
        str = paramString.substring(i, j);
        if (!str.endsWith("]"))
          str = str + "[0]";
        localStack2.add(str);
        i = j + 1;
      }
      String str = paramString.substring(i);
      if (!str.endsWith("]"))
        str = str + "[0]";
      localStack2.add(str);
      return localStack2;
    }

    public ArrayList getOrder()
    {
      return this.order;
    }

    public void setOrder(ArrayList paramArrayList)
    {
      this.order = paramArrayList;
    }

    public HashMap getName2Node()
    {
      return this.name2Node;
    }

    public void setName2Node(HashMap paramHashMap)
    {
      this.name2Node = paramHashMap;
    }

    public HashMap getInverseSearch()
    {
      return this.inverseSearch;
    }

    public void setInverseSearch(HashMap paramHashMap)
    {
      this.inverseSearch = paramHashMap;
    }
  }

  public static class Stack2 extends ArrayList
  {
    private static final long serialVersionUID = -7451476576174095212L;

    public Object peek()
    {
      if (size() == 0)
        throw new EmptyStackException();
      return get(size() - 1);
    }

    public Object pop()
    {
      if (size() == 0)
        throw new EmptyStackException();
      Object localObject = get(size() - 1);
      remove(size() - 1);
      return localObject;
    }

    public Object push(Object paramObject)
    {
      add(paramObject);
      return paramObject;
    }

    public boolean empty()
    {
      return size() == 0;
    }
  }

  public static class InverseStore
  {
    protected ArrayList part = new ArrayList();
    protected ArrayList follow = new ArrayList();

    public String getDefaultName()
    {
      Object localObject;
      for (InverseStore localInverseStore = this; ; localInverseStore = (InverseStore)localObject)
      {
        localObject = localInverseStore.follow.get(0);
        if ((localObject instanceof String))
          return (String)localObject;
      }
    }

    public boolean isSimilar(String paramString)
    {
      int i = paramString.indexOf('[');
      paramString = paramString.substring(0, i + 1);
      for (int j = 0; j < this.part.size(); j++)
        if (((String)this.part.get(j)).startsWith(paramString))
          return true;
      return false;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.XfaForm
 * JD-Core Version:    0.6.0
 */