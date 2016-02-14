package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import com.lowagie.text.xml.xmp.XmpReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xml.sax.SAXException;

class PdfStamperImp extends PdfWriter
{
  HashMap readers2intrefs = new HashMap();
  HashMap readers2file = new HashMap();
  RandomAccessFileOrArray file;
  PdfReader reader;
  IntHashtable myXref = new IntHashtable();
  HashMap pagesToContent = new HashMap();
  boolean closed = false;
  private boolean rotateContents = true;
  protected AcroFields acroFields;
  protected boolean flat = false;
  protected boolean flatFreeText = false;
  protected int[] namePtr = { 0 };
  protected HashSet partialFlattening = new HashSet();
  protected boolean useVp = false;
  protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
  protected HashMap fieldTemplates = new HashMap();
  protected boolean fieldsAdded = false;
  protected int sigFlags = 0;
  protected boolean append;
  protected IntHashtable marked;
  protected int initialXrefSize;
  protected PdfAction openAction;

  PdfStamperImp(PdfReader paramPdfReader, OutputStream paramOutputStream, char paramChar, boolean paramBoolean)
    throws DocumentException, IOException
  {
    super(new PdfDocument(), paramOutputStream);
    if (!paramPdfReader.isOpenedWithFullPermissions())
      throw new BadPasswordException("PdfReader not opened with owner password");
    if (paramPdfReader.isTampered())
      throw new DocumentException("The original document was reused. Read it again from file.");
    paramPdfReader.setTampered(true);
    this.reader = paramPdfReader;
    this.file = paramPdfReader.getSafeFile();
    this.append = paramBoolean;
    if (paramBoolean)
    {
      if (paramPdfReader.isRebuilt())
        throw new DocumentException("Append mode requires a document without errors even if recovery was possible.");
      if (paramPdfReader.isEncrypted())
        this.crypto = new PdfEncryption(paramPdfReader.getDecrypt());
      this.pdf_version.setAppendmode(true);
      this.file.reOpen();
      byte[] arrayOfByte = new byte[8192];
      int i;
      while ((i = this.file.read(arrayOfByte)) > 0)
        this.os.write(arrayOfByte, 0, i);
      this.file.close();
      this.prevxref = paramPdfReader.getLastXref();
      paramPdfReader.setAppendable(true);
    }
    else if (paramChar == 0)
    {
      super.setPdfVersion(paramPdfReader.getPdfVersion());
    }
    else
    {
      super.setPdfVersion(paramChar);
    }
    super.open();
    this.pdf.addWriter(this);
    if (paramBoolean)
    {
      this.body.setRefnum(paramPdfReader.getXrefSize());
      this.marked = new IntHashtable();
      if (paramPdfReader.isNewXrefType())
        this.fullCompression = true;
      if (paramPdfReader.isHybridXref())
        this.fullCompression = false;
    }
    this.initialXrefSize = paramPdfReader.getXrefSize();
  }

  void close(HashMap paramHashMap)
    throws IOException
  {
    if (this.closed)
      return;
    if (this.useVp)
    {
      this.reader.setViewerPreferences(this.viewerPreferences);
      markUsed(this.reader.getTrailer().get(PdfName.ROOT));
    }
    if (this.flat)
      flatFields();
    if (this.flatFreeText)
      flatFreeTextFields();
    addFieldResources();
    PdfDictionary localPdfDictionary1 = this.reader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.PAGES));
    localPdfDictionary2.put(PdfName.ITXT, new PdfString(Document.getRelease()));
    markUsed(localPdfDictionary2);
    PdfDictionary localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.ACROFORM), this.reader.getCatalog());
    if ((this.acroFields != null) && (this.acroFields.getXfa().isChanged()))
    {
      markUsed(localPdfDictionary3);
      if (!this.flat)
        this.acroFields.getXfa().setXfa(this);
    }
    if ((this.sigFlags != 0) && (localPdfDictionary3 != null))
    {
      localPdfDictionary3.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
      markUsed(localPdfDictionary3);
      markUsed(localPdfDictionary1);
    }
    this.closed = true;
    addSharedObjectsToBody();
    setOutlines();
    setJavaScript();
    addFileAttachments();
    if (this.openAction != null)
      localPdfDictionary1.put(PdfName.OPENACTION, this.openAction);
    if (this.pdf.pageLabels != null)
      localPdfDictionary1.put(PdfName.PAGELABELS, this.pdf.pageLabels.getDictionary(this));
    if (!this.documentOCG.isEmpty())
    {
      fillOCProperties(false);
      PdfDictionary localPdfDictionary4 = localPdfDictionary1.getAsDict(PdfName.OCPROPERTIES);
      if (localPdfDictionary4 == null)
      {
        this.reader.getCatalog().put(PdfName.OCPROPERTIES, this.OCProperties);
      }
      else
      {
        localPdfDictionary4.put(PdfName.OCGS, this.OCProperties.get(PdfName.OCGS));
        localObject1 = localPdfDictionary4.getAsDict(PdfName.D);
        if (localObject1 == null)
        {
          localObject1 = new PdfDictionary();
          localPdfDictionary4.put(PdfName.D, (PdfObject)localObject1);
        }
        ((PdfDictionary)localObject1).put(PdfName.ORDER, this.OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
        ((PdfDictionary)localObject1).put(PdfName.RBGROUPS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
        ((PdfDictionary)localObject1).put(PdfName.OFF, this.OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
        ((PdfDictionary)localObject1).put(PdfName.AS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
      }
    }
    int i = -1;
    Object localObject1 = (PRIndirectReference)this.reader.getTrailer().get(PdfName.INFO);
    PdfDictionary localPdfDictionary5 = (PdfDictionary)PdfReader.getPdfObject((PdfObject)localObject1);
    String str1 = null;
    if (localObject1 != null)
      i = ((PRIndirectReference)localObject1).getNumber();
    if ((localPdfDictionary5 != null) && (localPdfDictionary5.get(PdfName.PRODUCER) != null))
      str1 = localPdfDictionary5.getAsString(PdfName.PRODUCER).toString();
    if (str1 == null)
    {
      str1 = Document.getVersion();
    }
    else if (str1.indexOf(Document.getProduct()) == -1)
    {
      localObject2 = new StringBuffer(str1);
      ((StringBuffer)localObject2).append("; modified using ");
      ((StringBuffer)localObject2).append(Document.getVersion());
      str1 = ((StringBuffer)localObject2).toString();
    }
    Object localObject2 = null;
    PdfObject localPdfObject1 = PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.METADATA));
    if ((localPdfObject1 != null) && (localPdfObject1.isStream()))
    {
      localObject2 = PdfReader.getStreamBytesRaw((PRStream)localPdfObject1);
      PdfReader.killIndirect(localPdfDictionary1.get(PdfName.METADATA));
    }
    if (this.xmpMetadata != null)
      localObject2 = this.xmpMetadata;
    PdfDate localPdfDate = new PdfDate();
    Object localObject3;
    if (localObject2 != null)
    {
      PdfStream localPdfStream;
      try
      {
        XmpReader localXmpReader = new XmpReader(localObject2);
        if (!localXmpReader.replace("http://ns.adobe.com/pdf/1.3/", "Producer", str1))
          localXmpReader.add("rdf:Description", "http://ns.adobe.com/pdf/1.3/", "pdf:Producer", str1);
        if (!localXmpReader.replace("http://ns.adobe.com/xap/1.0/", "ModifyDate", localPdfDate.getW3CDate()))
          localXmpReader.add("rdf:Description", "http://ns.adobe.com/xap/1.0/", "xmp:ModifyDate", localPdfDate.getW3CDate());
        localXmpReader.replace("http://ns.adobe.com/xap/1.0/", "MetadataDate", localPdfDate.getW3CDate());
        localPdfStream = new PdfStream(localXmpReader.serializeDoc());
      }
      catch (SAXException localSAXException)
      {
        localPdfStream = new PdfStream(localObject2);
      }
      catch (IOException localIOException)
      {
        localPdfStream = new PdfStream(localObject2);
      }
      localPdfStream.put(PdfName.TYPE, PdfName.METADATA);
      localPdfStream.put(PdfName.SUBTYPE, PdfName.XML);
      if ((this.crypto != null) && (!this.crypto.isMetadataEncrypted()))
      {
        localObject3 = new PdfArray();
        ((PdfArray)localObject3).add(PdfName.CRYPT);
        localPdfStream.put(PdfName.FILTER, (PdfObject)localObject3);
      }
      if ((this.append) && (localPdfObject1 != null))
      {
        this.body.add(localPdfStream, localPdfObject1.getIndRef());
      }
      else
      {
        localPdfDictionary1.put(PdfName.METADATA, this.body.add(localPdfStream).getIndirectReference());
        markUsed(localPdfDictionary1);
      }
    }
    try
    {
      this.file.reOpen();
      alterContents();
      int j = ((PRIndirectReference)this.reader.trailer.get(PdfName.ROOT)).getNumber();
      if (this.append)
      {
        localObject3 = this.marked.getKeys();
        for (int m = 0; m < localObject3.length; m++)
        {
          int n = localObject3[m];
          localObject6 = this.reader.getPdfObjectRelease(n);
          if ((localObject6 == null) || (i == n) || (n >= this.initialXrefSize))
            continue;
          addToBody((PdfObject)localObject6, n, n != j);
        }
        for (m = this.initialXrefSize; m < this.reader.getXrefSize(); m++)
        {
          localObject5 = this.reader.getPdfObject(m);
          if (localObject5 == null)
            continue;
          addToBody((PdfObject)localObject5, getNewObjectNumber(this.reader, m, 0));
        }
      }
      for (int k = 1; k < this.reader.getXrefSize(); k++)
      {
        localObject4 = this.reader.getPdfObjectRelease(k);
        if ((localObject4 == null) || (i == k))
          continue;
        addToBody((PdfObject)localObject4, getNewObjectNumber(this.reader, k, 0), k != j);
      }
    }
    finally
    {
      try
      {
        this.file.close();
      }
      catch (Exception localException2)
      {
      }
    }
    PdfIndirectReference localPdfIndirectReference = null;
    PdfObject localPdfObject2 = null;
    if (this.crypto != null)
    {
      if (this.append)
      {
        localPdfIndirectReference = this.reader.getCryptoRef();
      }
      else
      {
        localObject4 = addToBody(this.crypto.getEncryptionDictionary(), false);
        localPdfIndirectReference = ((PdfIndirectObject)localObject4).getIndirectReference();
      }
      localPdfObject2 = this.crypto.getFileID();
    }
    else
    {
      localPdfObject2 = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
    }
    Object localObject4 = (PRIndirectReference)this.reader.trailer.get(PdfName.ROOT);
    Object localObject5 = new PdfIndirectReference(0, getNewObjectNumber(this.reader, ((PRIndirectReference)localObject4).getNumber(), 0));
    Object localObject6 = null;
    PdfDictionary localPdfDictionary6 = new PdfDictionary();
    Object localObject8;
    Object localObject9;
    Object localObject10;
    if (localPdfDictionary5 != null)
    {
      localObject8 = localPdfDictionary5.getKeys().iterator();
      while (((Iterator)localObject8).hasNext())
      {
        localObject9 = (PdfName)((Iterator)localObject8).next();
        localObject10 = PdfReader.getPdfObject(localPdfDictionary5.get((PdfName)localObject9));
        localPdfDictionary6.put((PdfName)localObject9, (PdfObject)localObject10);
      }
    }
    if (paramHashMap != null)
    {
      localObject8 = paramHashMap.entrySet().iterator();
      while (((Iterator)localObject8).hasNext())
      {
        localObject9 = (Map.Entry)((Iterator)localObject8).next();
        localObject10 = (String)((Map.Entry)localObject9).getKey();
        PdfName localPdfName = new PdfName((String)localObject10);
        String str2 = (String)((Map.Entry)localObject9).getValue();
        if (str2 == null)
        {
          localPdfDictionary6.remove(localPdfName);
          continue;
        }
        localPdfDictionary6.put(localPdfName, new PdfString(str2, "UnicodeBig"));
      }
    }
    localPdfDictionary6.put(PdfName.MODDATE, localPdfDate);
    localPdfDictionary6.put(PdfName.PRODUCER, new PdfString(str1));
    if (this.append)
    {
      if (localObject1 == null)
        localObject6 = addToBody(localPdfDictionary6, false).getIndirectReference();
      else
        localObject6 = addToBody(localPdfDictionary6, ((PRIndirectReference)localObject1).getNumber(), false).getIndirectReference();
    }
    else
      localObject6 = addToBody(localPdfDictionary6, false).getIndirectReference();
    this.body.writeCrossReferenceTable(this.os, (PdfIndirectReference)localObject5, (PdfIndirectReference)localObject6, localPdfIndirectReference, localPdfObject2, this.prevxref);
    if (this.fullCompression)
    {
      this.os.write(getISOBytes("startxref\n"));
      this.os.write(getISOBytes(String.valueOf(this.body.offset())));
      this.os.write(getISOBytes("\n%%EOF\n"));
    }
    else
    {
      localObject8 = new PdfWriter.PdfTrailer(this.body.size(), this.body.offset(), (PdfIndirectReference)localObject5, (PdfIndirectReference)localObject6, localPdfIndirectReference, localPdfObject2, this.prevxref);
      ((PdfWriter.PdfTrailer)localObject8).toPdf(this, this.os);
    }
    this.os.flush();
    if (isCloseStream())
      this.os.close();
    this.reader.close();
  }

  void applyRotation(PdfDictionary paramPdfDictionary, ByteBuffer paramByteBuffer)
  {
    if (!this.rotateContents)
      return;
    Rectangle localRectangle = this.reader.getPageSizeWithRotation(paramPdfDictionary);
    int i = localRectangle.getRotation();
    switch (i)
    {
    case 90:
      paramByteBuffer.append(PdfContents.ROTATE90);
      paramByteBuffer.append(localRectangle.getTop());
      paramByteBuffer.append(' ').append('0').append(PdfContents.ROTATEFINAL);
      break;
    case 180:
      paramByteBuffer.append(PdfContents.ROTATE180);
      paramByteBuffer.append(localRectangle.getRight());
      paramByteBuffer.append(' ');
      paramByteBuffer.append(localRectangle.getTop());
      paramByteBuffer.append(PdfContents.ROTATEFINAL);
      break;
    case 270:
      paramByteBuffer.append(PdfContents.ROTATE270);
      paramByteBuffer.append('0').append(' ');
      paramByteBuffer.append(localRectangle.getRight());
      paramByteBuffer.append(PdfContents.ROTATEFINAL);
    }
  }

  void alterContents()
    throws IOException
  {
    Iterator localIterator = this.pagesToContent.values().iterator();
    while (localIterator.hasNext())
    {
      PageStamp localPageStamp = (PageStamp)localIterator.next();
      PdfDictionary localPdfDictionary = localPageStamp.pageN;
      markUsed(localPdfDictionary);
      PdfArray localPdfArray = null;
      PdfObject localPdfObject = PdfReader.getPdfObject(localPdfDictionary.get(PdfName.CONTENTS), localPdfDictionary);
      if (localPdfObject == null)
      {
        localPdfArray = new PdfArray();
        localPdfDictionary.put(PdfName.CONTENTS, localPdfArray);
      }
      else if (localPdfObject.isArray())
      {
        localPdfArray = (PdfArray)localPdfObject;
        markUsed(localPdfArray);
      }
      else if (localPdfObject.isStream())
      {
        localPdfArray = new PdfArray();
        localPdfArray.add(localPdfDictionary.get(PdfName.CONTENTS));
        localPdfDictionary.put(PdfName.CONTENTS, localPdfArray);
      }
      else
      {
        localPdfArray = new PdfArray();
        localPdfDictionary.put(PdfName.CONTENTS, localPdfArray);
      }
      ByteBuffer localByteBuffer1 = new ByteBuffer();
      if (localPageStamp.under != null)
      {
        localByteBuffer1.append(PdfContents.SAVESTATE);
        applyRotation(localPdfDictionary, localByteBuffer1);
        localByteBuffer1.append(localPageStamp.under.getInternalBuffer());
        localByteBuffer1.append(PdfContents.RESTORESTATE);
      }
      if (localPageStamp.over != null)
        localByteBuffer1.append(PdfContents.SAVESTATE);
      PdfStream localPdfStream = new PdfStream(localByteBuffer1.toByteArray());
      localPdfStream.flateCompress(this.compressionLevel);
      localPdfArray.addFirst(addToBody(localPdfStream).getIndirectReference());
      localByteBuffer1.reset();
      if (localPageStamp.over != null)
      {
        localByteBuffer1.append(' ');
        localByteBuffer1.append(PdfContents.RESTORESTATE);
        ByteBuffer localByteBuffer2 = localPageStamp.over.getInternalBuffer();
        localByteBuffer1.append(localByteBuffer2.getBuffer(), 0, localPageStamp.replacePoint);
        localByteBuffer1.append(PdfContents.SAVESTATE);
        applyRotation(localPdfDictionary, localByteBuffer1);
        localByteBuffer1.append(localByteBuffer2.getBuffer(), localPageStamp.replacePoint, localByteBuffer2.size() - localPageStamp.replacePoint);
        localByteBuffer1.append(PdfContents.RESTORESTATE);
        localPdfStream = new PdfStream(localByteBuffer1.toByteArray());
        localPdfStream.flateCompress(this.compressionLevel);
        localPdfArray.add(addToBody(localPdfStream).getIndirectReference());
      }
      alterResources(localPageStamp);
    }
  }

  void alterResources(PageStamp paramPageStamp)
  {
    paramPageStamp.pageN.put(PdfName.RESOURCES, paramPageStamp.pageResources.getResources());
  }

  protected int getNewObjectNumber(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    IntHashtable localIntHashtable = (IntHashtable)this.readers2intrefs.get(paramPdfReader);
    int i;
    if (localIntHashtable != null)
    {
      i = localIntHashtable.get(paramInt1);
      if (i == 0)
      {
        i = getIndirectReferenceNumber();
        localIntHashtable.put(paramInt1, i);
      }
      return i;
    }
    if (this.currentPdfReaderInstance == null)
    {
      if ((this.append) && (paramInt1 < this.initialXrefSize))
        return paramInt1;
      i = this.myXref.get(paramInt1);
      if (i == 0)
      {
        i = getIndirectReferenceNumber();
        this.myXref.put(paramInt1, i);
      }
      return i;
    }
    return this.currentPdfReaderInstance.getNewObjectNumber(paramInt1, paramInt2);
  }

  RandomAccessFileOrArray getReaderFile(PdfReader paramPdfReader)
  {
    if (this.readers2intrefs.containsKey(paramPdfReader))
    {
      RandomAccessFileOrArray localRandomAccessFileOrArray = (RandomAccessFileOrArray)this.readers2file.get(paramPdfReader);
      if (localRandomAccessFileOrArray != null)
        return localRandomAccessFileOrArray;
      return paramPdfReader.getSafeFile();
    }
    if (this.currentPdfReaderInstance == null)
      return this.file;
    return this.currentPdfReaderInstance.getReaderFile();
  }

  public void registerReader(PdfReader paramPdfReader, boolean paramBoolean)
    throws IOException
  {
    if (this.readers2intrefs.containsKey(paramPdfReader))
      return;
    this.readers2intrefs.put(paramPdfReader, new IntHashtable());
    if (paramBoolean)
    {
      RandomAccessFileOrArray localRandomAccessFileOrArray = paramPdfReader.getSafeFile();
      this.readers2file.put(paramPdfReader, localRandomAccessFileOrArray);
      localRandomAccessFileOrArray.reOpen();
    }
  }

  public void unRegisterReader(PdfReader paramPdfReader)
  {
    if (!this.readers2intrefs.containsKey(paramPdfReader))
      return;
    this.readers2intrefs.remove(paramPdfReader);
    RandomAccessFileOrArray localRandomAccessFileOrArray = (RandomAccessFileOrArray)this.readers2file.get(paramPdfReader);
    if (localRandomAccessFileOrArray == null)
      return;
    this.readers2file.remove(paramPdfReader);
    try
    {
      localRandomAccessFileOrArray.close();
    }
    catch (Exception localException)
    {
    }
  }

  static void findAllObjects(PdfReader paramPdfReader, PdfObject paramPdfObject, IntHashtable paramIntHashtable)
  {
    if (paramPdfObject == null)
      return;
    switch (paramPdfObject.type())
    {
    case 10:
      PRIndirectReference localPRIndirectReference = (PRIndirectReference)paramPdfObject;
      if (paramPdfReader != localPRIndirectReference.getReader())
        return;
      if (paramIntHashtable.containsKey(localPRIndirectReference.getNumber()))
        return;
      paramIntHashtable.put(localPRIndirectReference.getNumber(), 1);
      findAllObjects(paramPdfReader, PdfReader.getPdfObject(paramPdfObject), paramIntHashtable);
      return;
    case 5:
      PdfArray localPdfArray = (PdfArray)paramPdfObject;
      for (int i = 0; i < localPdfArray.size(); i++)
        findAllObjects(paramPdfReader, localPdfArray.getPdfObject(i), paramIntHashtable);
      return;
    case 6:
    case 7:
      PdfDictionary localPdfDictionary = (PdfDictionary)paramPdfObject;
      Iterator localIterator = localPdfDictionary.getKeys().iterator();
      while (localIterator.hasNext())
      {
        PdfName localPdfName = (PdfName)localIterator.next();
        findAllObjects(paramPdfReader, localPdfDictionary.get(localPdfName), paramIntHashtable);
      }
      return;
    case 8:
    case 9:
    }
  }

  public void addComments(FdfReader paramFdfReader)
    throws IOException
  {
    if (this.readers2intrefs.containsKey(paramFdfReader))
      return;
    PdfDictionary localPdfDictionary1 = paramFdfReader.getCatalog();
    localPdfDictionary1 = localPdfDictionary1.getAsDict(PdfName.FDF);
    if (localPdfDictionary1 == null)
      return;
    PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.ANNOTS);
    if ((localPdfArray == null) || (localPdfArray.size() == 0))
      return;
    registerReader(paramFdfReader, false);
    IntHashtable localIntHashtable = new IntHashtable();
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList = new ArrayList();
    Object localObject1;
    Object localObject2;
    for (int i = 0; i < localPdfArray.size(); i++)
    {
      PdfObject localPdfObject1 = localPdfArray.getPdfObject(i);
      PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfObject1);
      localObject1 = localPdfDictionary2.getAsNumber(PdfName.PAGE);
      if ((localObject1 == null) || (((PdfNumber)localObject1).intValue() >= this.reader.getNumberOfPages()))
        continue;
      findAllObjects(paramFdfReader, localPdfObject1, localIntHashtable);
      localArrayList.add(localPdfObject1);
      if (localPdfObject1.type() != 10)
        continue;
      localObject2 = PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.NM));
      if ((localObject2 == null) || (((PdfObject)localObject2).type() != 3))
        continue;
      localHashMap.put(((PdfObject)localObject2).toString(), localPdfObject1);
    }
    int[] arrayOfInt = localIntHashtable.getKeys();
    Object localObject3;
    Object localObject4;
    for (int j = 0; j < arrayOfInt.length; j++)
    {
      int k = arrayOfInt[j];
      localObject1 = paramFdfReader.getPdfObject(k);
      if (((PdfObject)localObject1).type() == 6)
      {
        localObject2 = PdfReader.getPdfObject(((PdfDictionary)localObject1).get(PdfName.IRT));
        if ((localObject2 != null) && (((PdfObject)localObject2).type() == 3))
        {
          localObject3 = (PdfObject)localHashMap.get(((PdfObject)localObject2).toString());
          if (localObject3 != null)
          {
            localObject4 = new PdfDictionary();
            ((PdfDictionary)localObject4).merge((PdfDictionary)localObject1);
            ((PdfDictionary)localObject4).put(PdfName.IRT, (PdfObject)localObject3);
            localObject1 = localObject4;
          }
        }
      }
      addToBody((PdfObject)localObject1, getNewObjectNumber(paramFdfReader, k, 0));
    }
    for (j = 0; j < localArrayList.size(); j++)
    {
      PdfObject localPdfObject2 = (PdfObject)localArrayList.get(j);
      localObject1 = (PdfDictionary)PdfReader.getPdfObject(localPdfObject2);
      localObject2 = ((PdfDictionary)localObject1).getAsNumber(PdfName.PAGE);
      localObject3 = this.reader.getPageN(((PdfNumber)localObject2).intValue() + 1);
      localObject4 = (PdfArray)PdfReader.getPdfObject(((PdfDictionary)localObject3).get(PdfName.ANNOTS), (PdfObject)localObject3);
      if (localObject4 == null)
      {
        localObject4 = new PdfArray();
        ((PdfDictionary)localObject3).put(PdfName.ANNOTS, (PdfObject)localObject4);
        markUsed((PdfObject)localObject3);
      }
      markUsed((PdfObject)localObject4);
      ((PdfArray)localObject4).add(localPdfObject2);
    }
  }

  PageStamp getPageStamp(int paramInt)
  {
    PdfDictionary localPdfDictionary = this.reader.getPageN(paramInt);
    PageStamp localPageStamp = (PageStamp)this.pagesToContent.get(localPdfDictionary);
    if (localPageStamp == null)
    {
      localPageStamp = new PageStamp(this, this.reader, localPdfDictionary);
      this.pagesToContent.put(localPdfDictionary, localPageStamp);
    }
    return localPageStamp;
  }

  PdfContentByte getUnderContent(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > this.reader.getNumberOfPages()))
      return null;
    PageStamp localPageStamp = getPageStamp(paramInt);
    if (localPageStamp.under == null)
      localPageStamp.under = new StampContent(this, localPageStamp);
    return localPageStamp.under;
  }

  PdfContentByte getOverContent(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > this.reader.getNumberOfPages()))
      return null;
    PageStamp localPageStamp = getPageStamp(paramInt);
    if (localPageStamp.over == null)
      localPageStamp.over = new StampContent(this, localPageStamp);
    return localPageStamp.over;
  }

  void correctAcroFieldPages(int paramInt)
  {
    if (this.acroFields == null)
      return;
    if (paramInt > this.reader.getNumberOfPages())
      return;
    HashMap localHashMap = this.acroFields.getFields();
    Iterator localIterator = localHashMap.values().iterator();
    while (localIterator.hasNext())
    {
      AcroFields.Item localItem = (AcroFields.Item)localIterator.next();
      for (int i = 0; i < localItem.size(); i++)
      {
        int j = localItem.getPage(i).intValue();
        if (j < paramInt)
          continue;
        localItem.forcePage(i, j + 1);
      }
    }
  }

  private static void moveRectangle(PdfDictionary paramPdfDictionary, PdfReader paramPdfReader, int paramInt, PdfName paramPdfName, String paramString)
  {
    Rectangle localRectangle = paramPdfReader.getBoxSize(paramInt, paramString);
    if (localRectangle == null)
      paramPdfDictionary.remove(paramPdfName);
    else
      paramPdfDictionary.put(paramPdfName, new PdfRectangle(localRectangle));
  }

  void replacePage(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    PdfDictionary localPdfDictionary1 = this.reader.getPageN(paramInt2);
    if (this.pagesToContent.containsKey(localPdfDictionary1))
      throw new IllegalStateException("This page cannot be replaced: new content was already added");
    PdfImportedPage localPdfImportedPage = getImportedPage(paramPdfReader, paramInt1);
    PdfDictionary localPdfDictionary2 = this.reader.getPageNRelease(paramInt2);
    localPdfDictionary2.remove(PdfName.RESOURCES);
    localPdfDictionary2.remove(PdfName.CONTENTS);
    moveRectangle(localPdfDictionary2, paramPdfReader, paramInt1, PdfName.MEDIABOX, "media");
    moveRectangle(localPdfDictionary2, paramPdfReader, paramInt1, PdfName.CROPBOX, "crop");
    moveRectangle(localPdfDictionary2, paramPdfReader, paramInt1, PdfName.TRIMBOX, "trim");
    moveRectangle(localPdfDictionary2, paramPdfReader, paramInt1, PdfName.ARTBOX, "art");
    moveRectangle(localPdfDictionary2, paramPdfReader, paramInt1, PdfName.BLEEDBOX, "bleed");
    localPdfDictionary2.put(PdfName.ROTATE, new PdfNumber(paramPdfReader.getPageRotation(paramInt1)));
    PdfContentByte localPdfContentByte = getOverContent(paramInt2);
    localPdfContentByte.addTemplate(localPdfImportedPage, 0.0F, 0.0F);
    PageStamp localPageStamp = (PageStamp)this.pagesToContent.get(localPdfDictionary1);
    localPageStamp.replacePoint = localPageStamp.over.getInternalBuffer().size();
  }

  void insertPage(int paramInt, Rectangle paramRectangle)
  {
    Rectangle localRectangle = new Rectangle(paramRectangle);
    int i = localRectangle.getRotation() % 360;
    PdfDictionary localPdfDictionary1 = new PdfDictionary(PdfName.PAGE);
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    PdfArray localPdfArray1 = new PdfArray();
    localPdfArray1.add(PdfName.PDF);
    localPdfArray1.add(PdfName.TEXT);
    localPdfArray1.add(PdfName.IMAGEB);
    localPdfArray1.add(PdfName.IMAGEC);
    localPdfArray1.add(PdfName.IMAGEI);
    localPdfDictionary2.put(PdfName.PROCSET, localPdfArray1);
    localPdfDictionary1.put(PdfName.RESOURCES, localPdfDictionary2);
    localPdfDictionary1.put(PdfName.ROTATE, new PdfNumber(i));
    localPdfDictionary1.put(PdfName.MEDIABOX, new PdfRectangle(localRectangle, i));
    PRIndirectReference localPRIndirectReference1 = this.reader.addPdfObject(localPdfDictionary1);
    Object localObject1;
    PRIndirectReference localPRIndirectReference2;
    PdfDictionary localPdfDictionary3;
    Object localObject2;
    if (paramInt > this.reader.getNumberOfPages())
    {
      localObject1 = this.reader.getPageNRelease(this.reader.getNumberOfPages());
      localPRIndirectReference2 = (PRIndirectReference)((PdfDictionary)localObject1).get(PdfName.PARENT);
      localPRIndirectReference2 = new PRIndirectReference(this.reader, localPRIndirectReference2.getNumber());
      localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference2);
      localObject2 = (PdfArray)PdfReader.getPdfObject(localPdfDictionary3.get(PdfName.KIDS), localPdfDictionary3);
      ((PdfArray)localObject2).add(localPRIndirectReference1);
      markUsed((PdfObject)localObject2);
      this.reader.pageRefs.insertPage(paramInt, localPRIndirectReference1);
    }
    else
    {
      if (paramInt < 1)
        paramInt = 1;
      localObject1 = this.reader.getPageN(paramInt);
      localObject2 = this.reader.getPageOrigRef(paramInt);
      this.reader.releasePage(paramInt);
      localPRIndirectReference2 = (PRIndirectReference)((PdfDictionary)localObject1).get(PdfName.PARENT);
      localPRIndirectReference2 = new PRIndirectReference(this.reader, localPRIndirectReference2.getNumber());
      localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference2);
      PdfArray localPdfArray2 = (PdfArray)PdfReader.getPdfObject(localPdfDictionary3.get(PdfName.KIDS), localPdfDictionary3);
      int j = localPdfArray2.size();
      int k = ((PRIndirectReference)localObject2).getNumber();
      for (int m = 0; m < j; m++)
      {
        PRIndirectReference localPRIndirectReference3 = (PRIndirectReference)localPdfArray2.getPdfObject(m);
        if (k != localPRIndirectReference3.getNumber())
          continue;
        localPdfArray2.add(m, localPRIndirectReference1);
        break;
      }
      if (j == localPdfArray2.size())
        throw new RuntimeException("Internal inconsistence.");
      markUsed(localPdfArray2);
      this.reader.pageRefs.insertPage(paramInt, localPRIndirectReference1);
      correctAcroFieldPages(paramInt);
    }
    localPdfDictionary1.put(PdfName.PARENT, localPRIndirectReference2);
    while (localPdfDictionary3 != null)
    {
      markUsed(localPdfDictionary3);
      localObject1 = (PdfNumber)PdfReader.getPdfObjectRelease(localPdfDictionary3.get(PdfName.COUNT));
      localPdfDictionary3.put(PdfName.COUNT, new PdfNumber(((PdfNumber)localObject1).intValue() + 1));
      localPdfDictionary3 = localPdfDictionary3.getAsDict(PdfName.PARENT);
    }
  }

  boolean isRotateContents()
  {
    return this.rotateContents;
  }

  void setRotateContents(boolean paramBoolean)
  {
    this.rotateContents = paramBoolean;
  }

  boolean isContentWritten()
  {
    return this.body.size() > 1;
  }

  AcroFields getAcroFields()
  {
    if (this.acroFields == null)
      this.acroFields = new AcroFields(this.reader, this);
    return this.acroFields;
  }

  void setFormFlattening(boolean paramBoolean)
  {
    this.flat = paramBoolean;
  }

  void setFreeTextFlattening(boolean paramBoolean)
  {
    this.flatFreeText = paramBoolean;
  }

  boolean partialFormFlattening(String paramString)
  {
    getAcroFields();
    if (this.acroFields.getXfa().isXfaPresent())
      throw new UnsupportedOperationException("Partial form flattening is not supported with XFA forms.");
    if (!this.acroFields.getFields().containsKey(paramString))
      return false;
    this.partialFlattening.add(paramString);
    return true;
  }

  void flatFields()
  {
    if (this.append)
      throw new IllegalArgumentException("Field flattening is not supported in append mode.");
    getAcroFields();
    HashMap localHashMap = this.acroFields.getFields();
    if ((this.fieldsAdded) && (this.partialFlattening.isEmpty()))
    {
      localObject1 = localHashMap.keySet().iterator();
      while (((Iterator)localObject1).hasNext())
        this.partialFlattening.add(((Iterator)localObject1).next());
    }
    Object localObject1 = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
    PdfArray localPdfArray = null;
    if (localObject1 != null)
      localPdfArray = (PdfArray)PdfReader.getPdfObject(((PdfDictionary)localObject1).get(PdfName.FIELDS), (PdfObject)localObject1);
    Iterator localIterator = localHashMap.entrySet().iterator();
    Object localObject2;
    Object localObject3;
    while (localIterator.hasNext())
    {
      localObject2 = (Map.Entry)localIterator.next();
      localObject3 = (String)((Map.Entry)localObject2).getKey();
      if ((!this.partialFlattening.isEmpty()) && (!this.partialFlattening.contains(localObject3)))
        continue;
      AcroFields.Item localItem = (AcroFields.Item)((Map.Entry)localObject2).getValue();
      for (int k = 0; k < localItem.size(); k++)
      {
        PdfDictionary localPdfDictionary1 = localItem.getMerged(k);
        PdfNumber localPdfNumber = localPdfDictionary1.getAsNumber(PdfName.F);
        int m = 0;
        if (localPdfNumber != null)
          m = localPdfNumber.intValue();
        int n = localItem.getPage(k).intValue();
        PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.AP);
        Object localObject7;
        PdfIndirectReference localPdfIndirectReference;
        if ((localPdfDictionary2 != null) && ((m & 0x4) != 0) && ((m & 0x2) == 0))
        {
          localObject4 = localPdfDictionary2.get(PdfName.N);
          localObject5 = null;
          Object localObject6;
          if (localObject4 != null)
          {
            localObject6 = PdfReader.getPdfObject((PdfObject)localObject4);
            if (((localObject4 instanceof PdfIndirectReference)) && (!((PdfObject)localObject4).isIndirect()))
            {
              localObject5 = new PdfAppearance((PdfIndirectReference)localObject4);
            }
            else if ((localObject6 instanceof PdfStream))
            {
              ((PdfDictionary)localObject6).put(PdfName.SUBTYPE, PdfName.FORM);
              localObject5 = new PdfAppearance((PdfIndirectReference)localObject4);
            }
            else if ((localObject6 != null) && (((PdfObject)localObject6).isDictionary()))
            {
              localObject7 = localPdfDictionary1.getAsName(PdfName.AS);
              if (localObject7 != null)
              {
                localPdfIndirectReference = (PdfIndirectReference)((PdfDictionary)localObject6).get((PdfName)localObject7);
                if (localPdfIndirectReference != null)
                {
                  localObject5 = new PdfAppearance(localPdfIndirectReference);
                  if (localPdfIndirectReference.isIndirect())
                  {
                    localObject6 = PdfReader.getPdfObject(localPdfIndirectReference);
                    ((PdfDictionary)localObject6).put(PdfName.SUBTYPE, PdfName.FORM);
                  }
                }
              }
            }
          }
          if (localObject5 != null)
          {
            localObject6 = PdfReader.getNormalizedRectangle(localPdfDictionary1.getAsArray(PdfName.RECT));
            localObject7 = getOverContent(n);
            ((PdfContentByte)localObject7).setLiteral("Q ");
            ((PdfContentByte)localObject7).addTemplate((PdfTemplate)localObject5, ((Rectangle)localObject6).getLeft(), ((Rectangle)localObject6).getBottom());
            ((PdfContentByte)localObject7).setLiteral("q ");
          }
        }
        if (this.partialFlattening.isEmpty())
          continue;
        Object localObject4 = this.reader.getPageN(n);
        Object localObject5 = ((PdfDictionary)localObject4).getAsArray(PdfName.ANNOTS);
        if (localObject5 == null)
          continue;
        for (int i1 = 0; i1 < ((PdfArray)localObject5).size(); i1++)
        {
          localObject7 = ((PdfArray)localObject5).getPdfObject(i1);
          if (!((PdfObject)localObject7).isIndirect())
            continue;
          localPdfIndirectReference = localItem.getWidgetRef(k);
          if ((!localPdfIndirectReference.isIndirect()) || (((PRIndirectReference)localObject7).getNumber() != ((PRIndirectReference)localPdfIndirectReference).getNumber()))
            continue;
          ((PdfArray)localObject5).remove(i1--);
          PRIndirectReference localPRIndirectReference;
          for (Object localObject8 = (PRIndirectReference)localPdfIndirectReference; ; localObject8 = localPRIndirectReference)
          {
            PdfDictionary localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject((PdfObject)localObject8);
            localPRIndirectReference = (PRIndirectReference)localPdfDictionary3.get(PdfName.PARENT);
            PdfReader.killIndirect((PdfObject)localObject8);
            if (localPRIndirectReference == null)
              for (int i2 = 0; i2 < localPdfArray.size(); i2++)
              {
                localObject9 = localPdfArray.getPdfObject(i2);
                if ((!((PdfObject)localObject9).isIndirect()) || (((PRIndirectReference)localObject9).getNumber() != ((PRIndirectReference)localObject8).getNumber()))
                  continue;
                localPdfArray.remove(i2);
                i2--;
              }
            PdfDictionary localPdfDictionary4 = (PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference);
            Object localObject9 = localPdfDictionary4.getAsArray(PdfName.KIDS);
            for (int i3 = 0; i3 < ((PdfArray)localObject9).size(); i3++)
            {
              PdfObject localPdfObject2 = ((PdfArray)localObject9).getPdfObject(i3);
              if ((!localPdfObject2.isIndirect()) || (((PRIndirectReference)localPdfObject2).getNumber() != ((PRIndirectReference)localObject8).getNumber()))
                continue;
              ((PdfArray)localObject9).remove(i3);
              i3--;
            }
            if (!((PdfArray)localObject9).isEmpty())
              break;
          }
        }
        if (!((PdfArray)localObject5).isEmpty())
          continue;
        PdfReader.killIndirect(((PdfDictionary)localObject4).get(PdfName.ANNOTS));
        ((PdfDictionary)localObject4).remove(PdfName.ANNOTS);
      }
    }
    if ((!this.fieldsAdded) && (this.partialFlattening.isEmpty()))
    {
      for (int i = 1; i <= this.reader.getNumberOfPages(); i++)
      {
        localObject2 = this.reader.getPageN(i);
        localObject3 = ((PdfDictionary)localObject2).getAsArray(PdfName.ANNOTS);
        if (localObject3 == null)
          continue;
        for (int j = 0; j < ((PdfArray)localObject3).size(); j++)
        {
          PdfObject localPdfObject1 = ((PdfArray)localObject3).getDirectObject(j);
          if ((((localPdfObject1 instanceof PdfIndirectReference)) && (!localPdfObject1.isIndirect())) || ((localPdfObject1.isDictionary()) && (!PdfName.WIDGET.equals(((PdfDictionary)localPdfObject1).get(PdfName.SUBTYPE)))))
            continue;
          ((PdfArray)localObject3).remove(j);
          j--;
        }
        if (!((PdfArray)localObject3).isEmpty())
          continue;
        PdfReader.killIndirect(((PdfDictionary)localObject2).get(PdfName.ANNOTS));
        ((PdfDictionary)localObject2).remove(PdfName.ANNOTS);
      }
      eliminateAcroformObjects();
    }
  }

  void eliminateAcroformObjects()
  {
    PdfObject localPdfObject1 = this.reader.getCatalog().get(PdfName.ACROFORM);
    if (localPdfObject1 == null)
      return;
    PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObject(localPdfObject1);
    this.reader.killXref(localPdfDictionary1.get(PdfName.XFA));
    localPdfDictionary1.remove(PdfName.XFA);
    PdfObject localPdfObject2 = localPdfDictionary1.get(PdfName.FIELDS);
    if (localPdfObject2 != null)
    {
      PdfDictionary localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary2.put(PdfName.KIDS, localPdfObject2);
      sweepKids(localPdfDictionary2);
      PdfReader.killIndirect(localPdfObject2);
      localPdfDictionary1.put(PdfName.FIELDS, new PdfArray());
    }
  }

  void sweepKids(PdfObject paramPdfObject)
  {
    PdfObject localPdfObject = PdfReader.killIndirect(paramPdfObject);
    if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
      return;
    PdfDictionary localPdfDictionary = (PdfDictionary)localPdfObject;
    PdfArray localPdfArray = (PdfArray)PdfReader.killIndirect(localPdfDictionary.get(PdfName.KIDS));
    if (localPdfArray == null)
      return;
    for (int i = 0; i < localPdfArray.size(); i++)
      sweepKids(localPdfArray.getPdfObject(i));
  }

  private void flatFreeTextFields()
  {
    if (this.append)
      throw new IllegalArgumentException("FreeText flattening is not supported in append mode.");
    for (int i = 1; i <= this.reader.getNumberOfPages(); i++)
    {
      PdfDictionary localPdfDictionary1 = this.reader.getPageN(i);
      PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.ANNOTS);
      if (localPdfArray == null)
        continue;
      Object localObject1;
      for (int j = 0; j < localPdfArray.size(); j++)
      {
        localObject1 = localPdfArray.getDirectObject(j);
        if (((localObject1 instanceof PdfIndirectReference)) && (!((PdfObject)localObject1).isIndirect()))
          continue;
        PdfDictionary localPdfDictionary2 = (PdfDictionary)localObject1;
        if (!((PdfName)localPdfDictionary2.get(PdfName.SUBTYPE)).equals(PdfName.FREETEXT))
          continue;
        PdfNumber localPdfNumber = localPdfDictionary2.getAsNumber(PdfName.F);
        int k = localPdfNumber != null ? localPdfNumber.intValue() : 0;
        if (((k & 0x4) == 0) || ((k & 0x2) != 0))
          continue;
        PdfObject localPdfObject1 = localPdfDictionary2.get(PdfName.AP);
        if (localPdfObject1 == null)
          continue;
        PdfDictionary localPdfDictionary3 = (localPdfObject1 instanceof PdfIndirectReference) ? (PdfDictionary)PdfReader.getPdfObject(localPdfObject1) : (PdfDictionary)localPdfObject1;
        PdfObject localPdfObject2 = localPdfDictionary3.get(PdfName.N);
        PdfAppearance localPdfAppearance = null;
        PdfObject localPdfObject3 = PdfReader.getPdfObject(localPdfObject2);
        if (((localPdfObject2 instanceof PdfIndirectReference)) && (!localPdfObject2.isIndirect()))
        {
          localPdfAppearance = new PdfAppearance((PdfIndirectReference)localPdfObject2);
        }
        else if ((localPdfObject3 instanceof PdfStream))
        {
          ((PdfDictionary)localPdfObject3).put(PdfName.SUBTYPE, PdfName.FORM);
          localPdfAppearance = new PdfAppearance((PdfIndirectReference)localPdfObject2);
        }
        else if (localPdfObject3.isDictionary())
        {
          localObject2 = localPdfDictionary3.getAsName(PdfName.AS);
          if (localObject2 != null)
          {
            localObject3 = (PdfIndirectReference)((PdfDictionary)localPdfObject3).get((PdfName)localObject2);
            if (localObject3 != null)
            {
              localPdfAppearance = new PdfAppearance((PdfIndirectReference)localObject3);
              if (((PdfIndirectReference)localObject3).isIndirect())
              {
                localPdfObject3 = PdfReader.getPdfObject((PdfObject)localObject3);
                ((PdfDictionary)localPdfObject3).put(PdfName.SUBTYPE, PdfName.FORM);
              }
            }
          }
        }
        if (localPdfAppearance == null)
          continue;
        Object localObject2 = PdfReader.getNormalizedRectangle(localPdfDictionary2.getAsArray(PdfName.RECT));
        Object localObject3 = getOverContent(i);
        ((PdfContentByte)localObject3).setLiteral("Q ");
        ((PdfContentByte)localObject3).addTemplate(localPdfAppearance, ((Rectangle)localObject2).getLeft(), ((Rectangle)localObject2).getBottom());
        ((PdfContentByte)localObject3).setLiteral("q ");
      }
      for (j = 0; j < localPdfArray.size(); j++)
      {
        localObject1 = localPdfArray.getAsDict(j);
        if ((localObject1 == null) || (!PdfName.FREETEXT.equals(((PdfDictionary)localObject1).get(PdfName.SUBTYPE))))
          continue;
        localPdfArray.remove(j);
        j--;
      }
      if (!localPdfArray.isEmpty())
        continue;
      PdfReader.killIndirect(localPdfDictionary1.get(PdfName.ANNOTS));
      localPdfDictionary1.remove(PdfName.ANNOTS);
    }
  }

  public PdfIndirectReference getPageReference(int paramInt)
  {
    PRIndirectReference localPRIndirectReference = this.reader.getPageOrigRef(paramInt);
    if (localPRIndirectReference == null)
      throw new IllegalArgumentException("Invalid page number " + paramInt);
    return localPRIndirectReference;
  }

  public void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    throw new RuntimeException("Unsupported in this context. Use PdfStamper.addAnnotation()");
  }

  void addDocumentField(PdfIndirectReference paramPdfIndirectReference)
  {
    PdfDictionary localPdfDictionary1 = this.reader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.ACROFORM), localPdfDictionary1);
    if (localPdfDictionary2 == null)
    {
      localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary1.put(PdfName.ACROFORM, localPdfDictionary2);
      markUsed(localPdfDictionary1);
    }
    PdfArray localPdfArray = (PdfArray)PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.FIELDS), localPdfDictionary2);
    if (localPdfArray == null)
    {
      localPdfArray = new PdfArray();
      localPdfDictionary2.put(PdfName.FIELDS, localPdfArray);
      markUsed(localPdfDictionary2);
    }
    if (!localPdfDictionary2.contains(PdfName.DA))
    {
      localPdfDictionary2.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
      markUsed(localPdfDictionary2);
    }
    localPdfArray.add(paramPdfIndirectReference);
    markUsed(localPdfArray);
  }

  void addFieldResources()
    throws IOException
  {
    if (this.fieldTemplates.isEmpty())
      return;
    PdfDictionary localPdfDictionary1 = this.reader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.ACROFORM), localPdfDictionary1);
    if (localPdfDictionary2 == null)
    {
      localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary1.put(PdfName.ACROFORM, localPdfDictionary2);
      markUsed(localPdfDictionary1);
    }
    PdfDictionary localPdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.DR), localPdfDictionary2);
    if (localPdfDictionary3 == null)
    {
      localPdfDictionary3 = new PdfDictionary();
      localPdfDictionary2.put(PdfName.DR, localPdfDictionary3);
      markUsed(localPdfDictionary2);
    }
    markUsed(localPdfDictionary3);
    Object localObject1 = this.fieldTemplates.keySet().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PdfTemplate)((Iterator)localObject1).next();
      PdfFormField.mergeResources(localPdfDictionary3, (PdfDictionary)((PdfTemplate)localObject2).getResources(), this);
    }
    localObject1 = localPdfDictionary3.getAsDict(PdfName.FONT);
    if (localObject1 == null)
    {
      localObject1 = new PdfDictionary();
      localPdfDictionary3.put(PdfName.FONT, (PdfObject)localObject1);
    }
    if (!((PdfDictionary)localObject1).contains(PdfName.HELV))
    {
      localObject2 = new PdfDictionary(PdfName.FONT);
      ((PdfDictionary)localObject2).put(PdfName.BASEFONT, PdfName.HELVETICA);
      ((PdfDictionary)localObject2).put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
      ((PdfDictionary)localObject2).put(PdfName.NAME, PdfName.HELV);
      ((PdfDictionary)localObject2).put(PdfName.SUBTYPE, PdfName.TYPE1);
      ((PdfDictionary)localObject1).put(PdfName.HELV, addToBody((PdfObject)localObject2).getIndirectReference());
    }
    if (!((PdfDictionary)localObject1).contains(PdfName.ZADB))
    {
      localObject2 = new PdfDictionary(PdfName.FONT);
      ((PdfDictionary)localObject2).put(PdfName.BASEFONT, PdfName.ZAPFDINGBATS);
      ((PdfDictionary)localObject2).put(PdfName.NAME, PdfName.ZADB);
      ((PdfDictionary)localObject2).put(PdfName.SUBTYPE, PdfName.TYPE1);
      ((PdfDictionary)localObject1).put(PdfName.ZADB, addToBody((PdfObject)localObject2).getIndirectReference());
    }
    if (localPdfDictionary2.get(PdfName.DA) == null)
    {
      localPdfDictionary2.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
      markUsed(localPdfDictionary2);
    }
  }

  void expandFields(PdfFormField paramPdfFormField, ArrayList paramArrayList)
  {
    paramArrayList.add(paramPdfFormField);
    ArrayList localArrayList = paramPdfFormField.getKids();
    if (localArrayList != null)
      for (int i = 0; i < localArrayList.size(); i++)
        expandFields((PdfFormField)localArrayList.get(i), paramArrayList);
  }

  void addAnnotation(PdfAnnotation paramPdfAnnotation, PdfDictionary paramPdfDictionary)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      if (paramPdfAnnotation.isForm())
      {
        this.fieldsAdded = true;
        getAcroFields();
        PdfFormField localPdfFormField = (PdfFormField)paramPdfAnnotation;
        if (localPdfFormField.getParent() != null)
          return;
        expandFields(localPdfFormField, localArrayList);
      }
      else
      {
        localArrayList.add(paramPdfAnnotation);
      }
      for (int i = 0; i < localArrayList.size(); i++)
      {
        paramPdfAnnotation = (PdfAnnotation)localArrayList.get(i);
        if (paramPdfAnnotation.getPlaceInPage() > 0)
          paramPdfDictionary = this.reader.getPageN(paramPdfAnnotation.getPlaceInPage());
        Object localObject;
        if (paramPdfAnnotation.isForm())
        {
          if (!paramPdfAnnotation.isUsed())
          {
            localObject = paramPdfAnnotation.getTemplates();
            if (localObject != null)
              this.fieldTemplates.putAll((Map)localObject);
          }
          localObject = (PdfFormField)paramPdfAnnotation;
          if (((PdfFormField)localObject).getParent() == null)
            addDocumentField(((PdfFormField)localObject).getIndirectReference());
        }
        if (paramPdfAnnotation.isAnnotation())
        {
          localObject = PdfReader.getPdfObject(paramPdfDictionary.get(PdfName.ANNOTS), paramPdfDictionary);
          PdfArray localPdfArray = null;
          if ((localObject == null) || (!((PdfObject)localObject).isArray()))
          {
            localPdfArray = new PdfArray();
            paramPdfDictionary.put(PdfName.ANNOTS, localPdfArray);
            markUsed(paramPdfDictionary);
          }
          else
          {
            localPdfArray = (PdfArray)localObject;
          }
          localPdfArray.add(paramPdfAnnotation.getIndirectReference());
          markUsed(localPdfArray);
          if (!paramPdfAnnotation.isUsed())
          {
            PdfRectangle localPdfRectangle = (PdfRectangle)paramPdfAnnotation.get(PdfName.RECT);
            if ((localPdfRectangle != null) && ((localPdfRectangle.left() != 0.0F) || (localPdfRectangle.right() != 0.0F) || (localPdfRectangle.top() != 0.0F) || (localPdfRectangle.bottom() != 0.0F)))
            {
              int j = this.reader.getPageRotation(paramPdfDictionary);
              Rectangle localRectangle = this.reader.getPageSizeWithRotation(paramPdfDictionary);
              switch (j)
              {
              case 90:
                paramPdfAnnotation.put(PdfName.RECT, new PdfRectangle(localRectangle.getTop() - localPdfRectangle.bottom(), localPdfRectangle.left(), localRectangle.getTop() - localPdfRectangle.top(), localPdfRectangle.right()));
                break;
              case 180:
                paramPdfAnnotation.put(PdfName.RECT, new PdfRectangle(localRectangle.getRight() - localPdfRectangle.left(), localRectangle.getTop() - localPdfRectangle.bottom(), localRectangle.getRight() - localPdfRectangle.right(), localRectangle.getTop() - localPdfRectangle.top()));
                break;
              case 270:
                paramPdfAnnotation.put(PdfName.RECT, new PdfRectangle(localPdfRectangle.bottom(), localRectangle.getRight() - localPdfRectangle.left(), localPdfRectangle.top(), localRectangle.getRight() - localPdfRectangle.right()));
              }
            }
          }
        }
        if (paramPdfAnnotation.isUsed())
          continue;
        paramPdfAnnotation.setUsed();
        addToBody(paramPdfAnnotation, paramPdfAnnotation.getIndirectReference());
      }
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  void addAnnotation(PdfAnnotation paramPdfAnnotation, int paramInt)
  {
    paramPdfAnnotation.setPage(paramInt);
    addAnnotation(paramPdfAnnotation, this.reader.getPageN(paramInt));
  }

  private void outlineTravel(PRIndirectReference paramPRIndirectReference)
  {
    while (paramPRIndirectReference != null)
    {
      PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(paramPRIndirectReference);
      PRIndirectReference localPRIndirectReference = (PRIndirectReference)localPdfDictionary.get(PdfName.FIRST);
      if (localPRIndirectReference != null)
        outlineTravel(localPRIndirectReference);
      PdfReader.killIndirect(localPdfDictionary.get(PdfName.DEST));
      PdfReader.killIndirect(localPdfDictionary.get(PdfName.A));
      PdfReader.killIndirect(paramPRIndirectReference);
      paramPRIndirectReference = (PRIndirectReference)localPdfDictionary.get(PdfName.NEXT);
    }
  }

  void deleteOutlines()
  {
    PdfDictionary localPdfDictionary = this.reader.getCatalog();
    PRIndirectReference localPRIndirectReference = (PRIndirectReference)localPdfDictionary.get(PdfName.OUTLINES);
    if (localPRIndirectReference == null)
      return;
    outlineTravel(localPRIndirectReference);
    PdfReader.killIndirect(localPRIndirectReference);
    localPdfDictionary.remove(PdfName.OUTLINES);
    markUsed(localPdfDictionary);
  }

  void setJavaScript()
    throws IOException
  {
    HashMap localHashMap = this.pdf.getDocumentLevelJS();
    if (localHashMap.isEmpty())
      return;
    PdfDictionary localPdfDictionary1 = this.reader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.NAMES), localPdfDictionary1);
    if (localPdfDictionary2 == null)
    {
      localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary1.put(PdfName.NAMES, localPdfDictionary2);
      markUsed(localPdfDictionary1);
    }
    markUsed(localPdfDictionary2);
    PdfDictionary localPdfDictionary3 = PdfNameTree.writeTree(localHashMap, this);
    localPdfDictionary2.put(PdfName.JAVASCRIPT, addToBody(localPdfDictionary3).getIndirectReference());
  }

  void addFileAttachments()
    throws IOException
  {
    HashMap localHashMap1 = this.pdf.getDocumentFileAttachment();
    if (localHashMap1.isEmpty())
      return;
    PdfDictionary localPdfDictionary1 = this.reader.getCatalog();
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.NAMES), localPdfDictionary1);
    if (localPdfDictionary2 == null)
    {
      localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary1.put(PdfName.NAMES, localPdfDictionary2);
      markUsed(localPdfDictionary1);
    }
    markUsed(localPdfDictionary2);
    HashMap localHashMap2 = PdfNameTree.readTree((PdfDictionary)PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.EMBEDDEDFILES)));
    Object localObject = localHashMap1.entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      String str1 = (String)localEntry.getKey();
      int i = 0;
      for (String str2 = str1; localHashMap2.containsKey(str2); str2 = str2 + " " + i)
        i++;
      localHashMap2.put(str2, localEntry.getValue());
    }
    localObject = PdfNameTree.writeTree(localHashMap2, this);
    localPdfDictionary2.put(PdfName.EMBEDDEDFILES, addToBody((PdfObject)localObject).getIndirectReference());
  }

  void makePackage(PdfCollection paramPdfCollection)
  {
    PdfDictionary localPdfDictionary = this.reader.getCatalog();
    localPdfDictionary.put(PdfName.COLLECTION, paramPdfCollection);
  }

  void setOutlines()
    throws IOException
  {
    if (this.newBookmarks == null)
      return;
    deleteOutlines();
    if (this.newBookmarks.isEmpty())
      return;
    PdfDictionary localPdfDictionary = this.reader.getCatalog();
    boolean bool = localPdfDictionary.get(PdfName.DESTS) != null;
    writeOutlines(localPdfDictionary, bool);
    markUsed(localPdfDictionary);
  }

  public void setViewerPreferences(int paramInt)
  {
    this.useVp = true;
    this.viewerPreferences.setViewerPreferences(paramInt);
  }

  public void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    this.useVp = true;
    this.viewerPreferences.addViewerPreference(paramPdfName, paramPdfObject);
  }

  public void setSigFlags(int paramInt)
  {
    this.sigFlags |= paramInt;
  }

  public void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction)
    throws PdfException
  {
    throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
  }

  void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction, int paramInt)
    throws PdfException
  {
    if ((!paramPdfName.equals(PAGE_OPEN)) && (!paramPdfName.equals(PAGE_CLOSE)))
      throw new PdfException("Invalid page additional action type: " + paramPdfName.toString());
    PdfDictionary localPdfDictionary1 = this.reader.getPageN(paramInt);
    PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPdfDictionary1.get(PdfName.AA), localPdfDictionary1);
    if (localPdfDictionary2 == null)
    {
      localPdfDictionary2 = new PdfDictionary();
      localPdfDictionary1.put(PdfName.AA, localPdfDictionary2);
      markUsed(localPdfDictionary1);
    }
    localPdfDictionary2.put(paramPdfName, paramPdfAction);
    markUsed(localPdfDictionary2);
  }

  public void setDuration(int paramInt)
  {
    throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
  }

  public void setTransition(PdfTransition paramPdfTransition)
  {
    throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
  }

  void setDuration(int paramInt1, int paramInt2)
  {
    PdfDictionary localPdfDictionary = this.reader.getPageN(paramInt2);
    if (paramInt1 < 0)
      localPdfDictionary.remove(PdfName.DUR);
    else
      localPdfDictionary.put(PdfName.DUR, new PdfNumber(paramInt1));
    markUsed(localPdfDictionary);
  }

  void setTransition(PdfTransition paramPdfTransition, int paramInt)
  {
    PdfDictionary localPdfDictionary = this.reader.getPageN(paramInt);
    if (paramPdfTransition == null)
      localPdfDictionary.remove(PdfName.TRANS);
    else
      localPdfDictionary.put(PdfName.TRANS, paramPdfTransition.getTransitionDictionary());
    markUsed(localPdfDictionary);
  }

  protected void markUsed(PdfObject paramPdfObject)
  {
    if ((this.append) && (paramPdfObject != null))
    {
      PRIndirectReference localPRIndirectReference = null;
      if (paramPdfObject.type() == 10)
        localPRIndirectReference = (PRIndirectReference)paramPdfObject;
      else
        localPRIndirectReference = paramPdfObject.getIndRef();
      if (localPRIndirectReference != null)
        this.marked.put(localPRIndirectReference.getNumber(), 1);
    }
  }

  protected void markUsed(int paramInt)
  {
    if (this.append)
      this.marked.put(paramInt, 1);
  }

  boolean isAppend()
  {
    return this.append;
  }

  public void setAdditionalAction(PdfName paramPdfName, PdfAction paramPdfAction)
    throws PdfException
  {
    if ((!paramPdfName.equals(DOCUMENT_CLOSE)) && (!paramPdfName.equals(WILL_SAVE)) && (!paramPdfName.equals(DID_SAVE)) && (!paramPdfName.equals(WILL_PRINT)) && (!paramPdfName.equals(DID_PRINT)))
      throw new PdfException("Invalid additional action type: " + paramPdfName.toString());
    PdfDictionary localPdfDictionary = this.reader.getCatalog().getAsDict(PdfName.AA);
    if (localPdfDictionary == null)
    {
      if (paramPdfAction == null)
        return;
      localPdfDictionary = new PdfDictionary();
      this.reader.getCatalog().put(PdfName.AA, localPdfDictionary);
    }
    markUsed(localPdfDictionary);
    if (paramPdfAction == null)
      localPdfDictionary.remove(paramPdfName);
    else
      localPdfDictionary.put(paramPdfName, paramPdfAction);
  }

  public void setOpenAction(PdfAction paramPdfAction)
  {
    this.openAction = paramPdfAction;
  }

  public void setOpenAction(String paramString)
  {
    throw new UnsupportedOperationException("Open actions by name are not supported.");
  }

  public void setThumbnail(Image paramImage)
  {
    throw new UnsupportedOperationException("Use PdfStamper.setThumbnail().");
  }

  void setThumbnail(Image paramImage, int paramInt)
    throws PdfException, DocumentException
  {
    PdfIndirectReference localPdfIndirectReference = getImageReference(addDirectImageSimple(paramImage));
    this.reader.resetReleasePage();
    PdfDictionary localPdfDictionary = this.reader.getPageN(paramInt);
    localPdfDictionary.put(PdfName.THUMB, localPdfIndirectReference);
    this.reader.resetReleasePage();
  }

  public PdfContentByte getDirectContentUnder()
  {
    throw new UnsupportedOperationException("Use PdfStamper.getUnderContent() or PdfStamper.getOverContent()");
  }

  public PdfContentByte getDirectContent()
  {
    throw new UnsupportedOperationException("Use PdfStamper.getUnderContent() or PdfStamper.getOverContent()");
  }

  protected void readOCProperties()
  {
    if (!this.documentOCG.isEmpty())
      return;
    PdfDictionary localPdfDictionary = this.reader.getCatalog().getAsDict(PdfName.OCPROPERTIES);
    if (localPdfDictionary == null)
      return;
    PdfArray localPdfArray1 = localPdfDictionary.getAsArray(PdfName.OCGS);
    HashMap localHashMap = new HashMap();
    Object localObject1 = localPdfArray1.listIterator();
    PdfIndirectReference localPdfIndirectReference;
    PdfLayer localPdfLayer;
    while (((Iterator)localObject1).hasNext())
    {
      localPdfIndirectReference = (PdfIndirectReference)((Iterator)localObject1).next();
      localPdfLayer = new PdfLayer(null);
      localPdfLayer.setRef(localPdfIndirectReference);
      localPdfLayer.setOnPanel(false);
      localPdfLayer.merge((PdfDictionary)PdfReader.getPdfObject(localPdfIndirectReference));
      localHashMap.put(localPdfIndirectReference.toString(), localPdfLayer);
    }
    localObject1 = localPdfDictionary.getAsDict(PdfName.D);
    PdfArray localPdfArray2 = ((PdfDictionary)localObject1).getAsArray(PdfName.OFF);
    if (localPdfArray2 != null)
    {
      localObject2 = localPdfArray2.listIterator();
      while (((Iterator)localObject2).hasNext())
      {
        localPdfIndirectReference = (PdfIndirectReference)((Iterator)localObject2).next();
        localPdfLayer = (PdfLayer)localHashMap.get(localPdfIndirectReference.toString());
        localPdfLayer.setOn(false);
      }
    }
    Object localObject2 = ((PdfDictionary)localObject1).getAsArray(PdfName.ORDER);
    if (localObject2 != null)
      addOrder(null, (PdfArray)localObject2, localHashMap);
    this.documentOCG.addAll(localHashMap.values());
    this.OCGRadioGroup = ((PdfDictionary)localObject1).getAsArray(PdfName.RBGROUPS);
    this.OCGLocked = ((PdfDictionary)localObject1).getAsArray(PdfName.LOCKED);
    if (this.OCGLocked == null)
      this.OCGLocked = new PdfArray();
  }

  private void addOrder(PdfLayer paramPdfLayer, PdfArray paramPdfArray, Map paramMap)
  {
    for (int i = 0; i < paramPdfArray.size(); i++)
    {
      PdfObject localPdfObject = paramPdfArray.getPdfObject(i);
      PdfLayer localPdfLayer;
      if (localPdfObject.isIndirect())
      {
        localPdfLayer = (PdfLayer)paramMap.get(localPdfObject.toString());
        localPdfLayer.setOnPanel(true);
        registerLayer(localPdfLayer);
        if (paramPdfLayer != null)
          paramPdfLayer.addChild(localPdfLayer);
        if ((paramPdfArray.size() <= i + 1) || (!paramPdfArray.getPdfObject(i + 1).isArray()))
          continue;
        i++;
        addOrder(localPdfLayer, (PdfArray)paramPdfArray.getPdfObject(i), paramMap);
      }
      else
      {
        if (!localPdfObject.isArray())
          continue;
        PdfArray localPdfArray1 = (PdfArray)localPdfObject;
        if (localPdfArray1.isEmpty())
          return;
        localPdfObject = localPdfArray1.getPdfObject(0);
        if (localPdfObject.isString())
        {
          localPdfLayer = new PdfLayer(localPdfObject.toString());
          localPdfLayer.setOnPanel(true);
          registerLayer(localPdfLayer);
          if (paramPdfLayer != null)
            paramPdfLayer.addChild(localPdfLayer);
          PdfArray localPdfArray2 = new PdfArray();
          ListIterator localListIterator = localPdfArray1.listIterator();
          while (localListIterator.hasNext())
            localPdfArray2.add((PdfObject)localListIterator.next());
          addOrder(localPdfLayer, localPdfArray2, paramMap);
        }
        else
        {
          addOrder(paramPdfLayer, (PdfArray)localPdfObject, paramMap);
        }
      }
    }
  }

  public Map getPdfLayers()
  {
    if (this.documentOCG.isEmpty())
      readOCProperties();
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.documentOCG.iterator();
    while (localIterator.hasNext())
    {
      PdfLayer localPdfLayer = (PdfLayer)localIterator.next();
      Object localObject;
      if (localPdfLayer.getTitle() == null)
        localObject = localPdfLayer.getAsString(PdfName.NAME).toString();
      else
        localObject = localPdfLayer.getTitle();
      if (localHashMap.containsKey(localObject))
      {
        int i = 2;
        for (String str = (String)localObject + "(" + i + ")"; localHashMap.containsKey(str); str = (String)localObject + "(" + i + ")")
          i++;
        localObject = str;
      }
      localHashMap.put(localObject, localPdfLayer);
    }
    return (Map)localHashMap;
  }

  static class PageStamp
  {
    PdfDictionary pageN;
    StampContent under;
    StampContent over;
    PageResources pageResources;
    int replacePoint = 0;

    PageStamp(PdfStamperImp paramPdfStamperImp, PdfReader paramPdfReader, PdfDictionary paramPdfDictionary)
    {
      this.pageN = paramPdfDictionary;
      this.pageResources = new PageResources();
      PdfDictionary localPdfDictionary = paramPdfDictionary.getAsDict(PdfName.RESOURCES);
      this.pageResources.setOriginalResources(localPdfDictionary, paramPdfStamperImp.namePtr);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfStamperImp
 * JD-Core Version:    0.6.0
 */