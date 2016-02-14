package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class PdfCopy extends PdfWriter
{
  protected HashMap indirects;
  protected HashMap indirectMap;
  protected int currentObjectNum = 1;
  protected PdfReader reader;
  protected PdfIndirectReference acroForm;
  protected int[] namePtr = { 0 };
  private boolean rotateContents = true;
  protected PdfArray fieldArray;
  protected HashMap fieldTemplates;

  public PdfCopy(Document paramDocument, OutputStream paramOutputStream)
    throws DocumentException
  {
    super(new PdfDocument(), paramOutputStream);
    paramDocument.addDocListener(this.pdf);
    this.pdf.addWriter(this);
    this.indirectMap = new HashMap();
  }

  public boolean isRotateContents()
  {
    return this.rotateContents;
  }

  public void setRotateContents(boolean paramBoolean)
  {
    this.rotateContents = paramBoolean;
  }

  public PdfImportedPage getImportedPage(PdfReader paramPdfReader, int paramInt)
  {
    if (this.currentPdfReaderInstance != null)
    {
      if (this.currentPdfReaderInstance.getReader() != paramPdfReader)
      {
        try
        {
          this.currentPdfReaderInstance.getReader().close();
          this.currentPdfReaderInstance.getReaderFile().close();
        }
        catch (IOException localIOException)
        {
        }
        this.currentPdfReaderInstance = paramPdfReader.getPdfReaderInstance(this);
      }
    }
    else
      this.currentPdfReaderInstance = paramPdfReader.getPdfReaderInstance(this);
    return this.currentPdfReaderInstance.getImportedPage(paramInt);
  }

  protected PdfIndirectReference copyIndirect(PRIndirectReference paramPRIndirectReference)
    throws IOException, BadPdfFormatException
  {
    RefKey localRefKey = new RefKey(paramPRIndirectReference);
    IndirectReferences localIndirectReferences = (IndirectReferences)this.indirects.get(localRefKey);
    PdfIndirectReference localPdfIndirectReference;
    if (localIndirectReferences != null)
    {
      localPdfIndirectReference = localIndirectReferences.getRef();
      if (localIndirectReferences.getCopied())
        return localPdfIndirectReference;
    }
    else
    {
      localPdfIndirectReference = this.body.getPdfIndirectReference();
      localIndirectReferences = new IndirectReferences(localPdfIndirectReference);
      this.indirects.put(localRefKey, localIndirectReferences);
    }
    PdfObject localPdfObject1 = PdfReader.getPdfObjectRelease(paramPRIndirectReference);
    if ((localPdfObject1 != null) && (localPdfObject1.isDictionary()))
    {
      PdfObject localPdfObject2 = PdfReader.getPdfObjectRelease(((PdfDictionary)localPdfObject1).get(PdfName.TYPE));
      if ((localPdfObject2 != null) && (PdfName.PAGE.equals(localPdfObject2)))
        return localPdfIndirectReference;
    }
    localIndirectReferences.setCopied();
    localPdfObject1 = copyObject(localPdfObject1);
    addToBody(localPdfObject1, localPdfIndirectReference);
    return localPdfIndirectReference;
  }

  protected PdfDictionary copyDictionary(PdfDictionary paramPdfDictionary)
    throws IOException, BadPdfFormatException
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    PdfObject localPdfObject1 = PdfReader.getPdfObjectRelease(paramPdfDictionary.get(PdfName.TYPE));
    Iterator localIterator = paramPdfDictionary.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      PdfObject localPdfObject2 = paramPdfDictionary.get(localPdfName);
      if ((localPdfObject1 != null) && (PdfName.PAGE.equals(localPdfObject1)))
      {
        if ((localPdfName.equals(PdfName.B)) || (localPdfName.equals(PdfName.PARENT)))
          continue;
        localPdfDictionary.put(localPdfName, copyObject(localPdfObject2));
        continue;
      }
      localPdfDictionary.put(localPdfName, copyObject(localPdfObject2));
    }
    return localPdfDictionary;
  }

  protected PdfStream copyStream(PRStream paramPRStream)
    throws IOException, BadPdfFormatException
  {
    PRStream localPRStream = new PRStream(paramPRStream, null);
    Iterator localIterator = paramPRStream.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      PdfObject localPdfObject = paramPRStream.get(localPdfName);
      localPRStream.put(localPdfName, copyObject(localPdfObject));
    }
    return localPRStream;
  }

  protected PdfArray copyArray(PdfArray paramPdfArray)
    throws IOException, BadPdfFormatException
  {
    PdfArray localPdfArray = new PdfArray();
    ListIterator localListIterator = paramPdfArray.listIterator();
    while (localListIterator.hasNext())
    {
      PdfObject localPdfObject = (PdfObject)localListIterator.next();
      localPdfArray.add(copyObject(localPdfObject));
    }
    return localPdfArray;
  }

  protected PdfObject copyObject(PdfObject paramPdfObject)
    throws IOException, BadPdfFormatException
  {
    if (paramPdfObject == null)
      return PdfNull.PDFNULL;
    switch (paramPdfObject.type)
    {
    case 6:
      return copyDictionary((PdfDictionary)paramPdfObject);
    case 10:
      return copyIndirect((PRIndirectReference)paramPdfObject);
    case 5:
      return copyArray((PdfArray)paramPdfObject);
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 8:
      return paramPdfObject;
    case 7:
      return copyStream((PRStream)paramPdfObject);
    case 9:
    }
    if (paramPdfObject.type < 0)
    {
      String str = ((PdfLiteral)paramPdfObject).toString();
      if ((str.equals("true")) || (str.equals("false")))
        return new PdfBoolean(str);
      return new PdfLiteral(str);
    }
    System.out.println("CANNOT COPY type " + paramPdfObject.type);
    return null;
  }

  protected int setFromIPage(PdfImportedPage paramPdfImportedPage)
  {
    int i = paramPdfImportedPage.getPageNumber();
    PdfReaderInstance localPdfReaderInstance = this.currentPdfReaderInstance = paramPdfImportedPage.getPdfReaderInstance();
    this.reader = localPdfReaderInstance.getReader();
    setFromReader(this.reader);
    return i;
  }

  protected void setFromReader(PdfReader paramPdfReader)
  {
    this.reader = paramPdfReader;
    this.indirects = ((HashMap)this.indirectMap.get(paramPdfReader));
    if (this.indirects == null)
    {
      this.indirects = new HashMap();
      this.indirectMap.put(paramPdfReader, this.indirects);
      PdfDictionary localPdfDictionary = paramPdfReader.getCatalog();
      PRIndirectReference localPRIndirectReference = null;
      PdfObject localPdfObject = localPdfDictionary.get(PdfName.ACROFORM);
      if ((localPdfObject == null) || (localPdfObject.type() != 10))
        return;
      localPRIndirectReference = (PRIndirectReference)localPdfObject;
      if (this.acroForm == null)
        this.acroForm = this.body.getPdfIndirectReference();
      this.indirects.put(new RefKey(localPRIndirectReference), new IndirectReferences(this.acroForm));
    }
  }

  public void addPage(PdfImportedPage paramPdfImportedPage)
    throws IOException, BadPdfFormatException
  {
    int i = setFromIPage(paramPdfImportedPage);
    PdfDictionary localPdfDictionary1 = this.reader.getPageN(i);
    PRIndirectReference localPRIndirectReference = this.reader.getPageOrigRef(i);
    this.reader.releasePage(i);
    RefKey localRefKey = new RefKey(localPRIndirectReference);
    IndirectReferences localIndirectReferences = (IndirectReferences)this.indirects.get(localRefKey);
    if ((localIndirectReferences != null) && (!localIndirectReferences.getCopied()))
    {
      this.pageReferences.add(localIndirectReferences.getRef());
      localIndirectReferences.setCopied();
    }
    PdfIndirectReference localPdfIndirectReference = getCurrentPage();
    if (localIndirectReferences == null)
    {
      localIndirectReferences = new IndirectReferences(localPdfIndirectReference);
      this.indirects.put(localRefKey, localIndirectReferences);
    }
    localIndirectReferences.setCopied();
    PdfDictionary localPdfDictionary2 = copyDictionary(localPdfDictionary1);
    this.root.addPage(localPdfDictionary2);
    this.currentPageNumber += 1;
  }

  public void addPage(Rectangle paramRectangle, int paramInt)
  {
    PdfRectangle localPdfRectangle = new PdfRectangle(paramRectangle, paramInt);
    PageResources localPageResources = new PageResources();
    PdfPage localPdfPage = new PdfPage(localPdfRectangle, new HashMap(), localPageResources.getResources(), 0);
    localPdfPage.put(PdfName.TABS, getTabs());
    this.root.addPage(localPdfPage);
    this.currentPageNumber += 1;
  }

  public void copyAcroForm(PdfReader paramPdfReader)
    throws IOException, BadPdfFormatException
  {
    setFromReader(paramPdfReader);
    PdfDictionary localPdfDictionary1 = paramPdfReader.getCatalog();
    PRIndirectReference localPRIndirectReference = null;
    PdfObject localPdfObject = localPdfDictionary1.get(PdfName.ACROFORM);
    if ((localPdfObject != null) && (localPdfObject.type() == 10))
      localPRIndirectReference = (PRIndirectReference)localPdfObject;
    if (localPRIndirectReference == null)
      return;
    RefKey localRefKey = new RefKey(localPRIndirectReference);
    IndirectReferences localIndirectReferences = (IndirectReferences)this.indirects.get(localRefKey);
    PdfIndirectReference localPdfIndirectReference;
    if (localIndirectReferences != null)
    {
      this.acroForm = (localPdfIndirectReference = localIndirectReferences.getRef());
    }
    else
    {
      this.acroForm = (localPdfIndirectReference = this.body.getPdfIndirectReference());
      localIndirectReferences = new IndirectReferences(localPdfIndirectReference);
      this.indirects.put(localRefKey, localIndirectReferences);
    }
    if (!localIndirectReferences.getCopied())
    {
      localIndirectReferences.setCopied();
      PdfDictionary localPdfDictionary2 = copyDictionary((PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference));
      addToBody(localPdfDictionary2, localPdfIndirectReference);
    }
  }

  protected PdfDictionary getCatalog(PdfIndirectReference paramPdfIndirectReference)
  {
    try
    {
      PdfDocument.PdfCatalog localPdfCatalog = this.pdf.getCatalog(paramPdfIndirectReference);
      if (this.fieldArray == null)
      {
        if (this.acroForm != null)
          localPdfCatalog.put(PdfName.ACROFORM, this.acroForm);
      }
      else
        addFieldResources(localPdfCatalog);
      return localPdfCatalog;
    }
    catch (IOException localIOException)
    {
    }
    throw new ExceptionConverter(localIOException);
  }

  private void addFieldResources(PdfDictionary paramPdfDictionary)
    throws IOException
  {
    if (this.fieldArray == null)
      return;
    PdfDictionary localPdfDictionary1 = new PdfDictionary();
    paramPdfDictionary.put(PdfName.ACROFORM, localPdfDictionary1);
    localPdfDictionary1.put(PdfName.FIELDS, this.fieldArray);
    localPdfDictionary1.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
    if (this.fieldTemplates.isEmpty())
      return;
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary1.put(PdfName.DR, localPdfDictionary2);
    Object localObject1 = this.fieldTemplates.keySet().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PdfTemplate)((Iterator)localObject1).next();
      PdfFormField.mergeResources(localPdfDictionary2, (PdfDictionary)((PdfTemplate)localObject2).getResources());
    }
    localObject1 = localPdfDictionary2.getAsDict(PdfName.FONT);
    if (localObject1 == null)
    {
      localObject1 = new PdfDictionary();
      localPdfDictionary2.put(PdfName.FONT, (PdfObject)localObject1);
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
  }

  public void close()
  {
    if (this.open)
    {
      PdfReaderInstance localPdfReaderInstance = this.currentPdfReaderInstance;
      this.pdf.close();
      super.close();
      if (localPdfReaderInstance != null)
        try
        {
          localPdfReaderInstance.getReader().close();
          localPdfReaderInstance.getReaderFile().close();
        }
        catch (IOException localIOException)
        {
        }
    }
  }

  public PdfIndirectReference add(PdfOutline paramPdfOutline)
  {
    return null;
  }

  public void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
  }

  PdfIndirectReference add(PdfPage paramPdfPage, PdfContents paramPdfContents)
    throws PdfException
  {
    return null;
  }

  public void freeReader(PdfReader paramPdfReader)
    throws IOException
  {
    this.indirectMap.remove(paramPdfReader);
    if ((this.currentPdfReaderInstance != null) && (this.currentPdfReaderInstance.getReader() == paramPdfReader))
    {
      try
      {
        this.currentPdfReaderInstance.getReader().close();
        this.currentPdfReaderInstance.getReaderFile().close();
      }
      catch (IOException localIOException)
      {
      }
      this.currentPdfReaderInstance = null;
    }
  }

  public PageStamp createPageStamp(PdfImportedPage paramPdfImportedPage)
  {
    int i = paramPdfImportedPage.getPageNumber();
    PdfReader localPdfReader = paramPdfImportedPage.getPdfReaderInstance().getReader();
    PdfDictionary localPdfDictionary = localPdfReader.getPageN(i);
    return new PageStamp(localPdfReader, localPdfDictionary, this);
  }

  public static class StampContent extends PdfContentByte
  {
    PageResources pageResources;

    StampContent(PdfWriter paramPdfWriter, PageResources paramPageResources)
    {
      super();
      this.pageResources = paramPageResources;
    }

    public PdfContentByte getDuplicate()
    {
      return new StampContent(this.writer, this.pageResources);
    }

    PageResources getPageResources()
    {
      return this.pageResources;
    }
  }

  public static class PageStamp
  {
    PdfDictionary pageN;
    PdfCopy.StampContent under;
    PdfCopy.StampContent over;
    PageResources pageResources;
    PdfReader reader;
    PdfCopy cstp;

    PageStamp(PdfReader paramPdfReader, PdfDictionary paramPdfDictionary, PdfCopy paramPdfCopy)
    {
      this.pageN = paramPdfDictionary;
      this.reader = paramPdfReader;
      this.cstp = paramPdfCopy;
    }

    public PdfContentByte getUnderContent()
    {
      if (this.under == null)
      {
        if (this.pageResources == null)
        {
          this.pageResources = new PageResources();
          PdfDictionary localPdfDictionary = this.pageN.getAsDict(PdfName.RESOURCES);
          this.pageResources.setOriginalResources(localPdfDictionary, this.cstp.namePtr);
        }
        this.under = new PdfCopy.StampContent(this.cstp, this.pageResources);
      }
      return this.under;
    }

    public PdfContentByte getOverContent()
    {
      if (this.over == null)
      {
        if (this.pageResources == null)
        {
          this.pageResources = new PageResources();
          PdfDictionary localPdfDictionary = this.pageN.getAsDict(PdfName.RESOURCES);
          this.pageResources.setOriginalResources(localPdfDictionary, this.cstp.namePtr);
        }
        this.over = new PdfCopy.StampContent(this.cstp, this.pageResources);
      }
      return this.over;
    }

    public void alterContents()
      throws IOException
    {
      if ((this.over == null) && (this.under == null))
        return;
      PdfArray localPdfArray = null;
      PdfObject localPdfObject = PdfReader.getPdfObject(this.pageN.get(PdfName.CONTENTS), this.pageN);
      if (localPdfObject == null)
      {
        localPdfArray = new PdfArray();
        this.pageN.put(PdfName.CONTENTS, localPdfArray);
      }
      else if (localPdfObject.isArray())
      {
        localPdfArray = (PdfArray)localPdfObject;
      }
      else if (localPdfObject.isStream())
      {
        localPdfArray = new PdfArray();
        localPdfArray.add(this.pageN.get(PdfName.CONTENTS));
        this.pageN.put(PdfName.CONTENTS, localPdfArray);
      }
      else
      {
        localPdfArray = new PdfArray();
        this.pageN.put(PdfName.CONTENTS, localPdfArray);
      }
      ByteBuffer localByteBuffer = new ByteBuffer();
      if (this.under != null)
      {
        localByteBuffer.append(PdfContents.SAVESTATE);
        applyRotation(this.pageN, localByteBuffer);
        localByteBuffer.append(this.under.getInternalBuffer());
        localByteBuffer.append(PdfContents.RESTORESTATE);
      }
      if (this.over != null)
        localByteBuffer.append(PdfContents.SAVESTATE);
      PdfStream localPdfStream = new PdfStream(localByteBuffer.toByteArray());
      localPdfStream.flateCompress(this.cstp.getCompressionLevel());
      PdfIndirectReference localPdfIndirectReference = this.cstp.addToBody(localPdfStream).getIndirectReference();
      localPdfArray.addFirst(localPdfIndirectReference);
      localByteBuffer.reset();
      if (this.over != null)
      {
        localByteBuffer.append(' ');
        localByteBuffer.append(PdfContents.RESTORESTATE);
        localByteBuffer.append(PdfContents.SAVESTATE);
        applyRotation(this.pageN, localByteBuffer);
        localByteBuffer.append(this.over.getInternalBuffer());
        localByteBuffer.append(PdfContents.RESTORESTATE);
        localPdfStream = new PdfStream(localByteBuffer.toByteArray());
        localPdfStream.flateCompress(this.cstp.getCompressionLevel());
        localPdfArray.add(this.cstp.addToBody(localPdfStream).getIndirectReference());
      }
      this.pageN.put(PdfName.RESOURCES, this.pageResources.getResources());
    }

    void applyRotation(PdfDictionary paramPdfDictionary, ByteBuffer paramByteBuffer)
    {
      if (!this.cstp.rotateContents)
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

    private void addDocumentField(PdfIndirectReference paramPdfIndirectReference)
    {
      if (this.cstp.fieldArray == null)
        this.cstp.fieldArray = new PdfArray();
      this.cstp.fieldArray.add(paramPdfIndirectReference);
    }

    private void expandFields(PdfFormField paramPdfFormField, ArrayList paramArrayList)
    {
      paramArrayList.add(paramPdfFormField);
      ArrayList localArrayList = paramPdfFormField.getKids();
      if (localArrayList != null)
        for (int i = 0; i < localArrayList.size(); i++)
          expandFields((PdfFormField)localArrayList.get(i), paramArrayList);
    }

    public void addAnnotation(PdfAnnotation paramPdfAnnotation)
    {
      try
      {
        ArrayList localArrayList = new ArrayList();
        if (paramPdfAnnotation.isForm())
        {
          PdfFormField localPdfFormField = (PdfFormField)paramPdfAnnotation;
          if (localPdfFormField.getParent() != null)
            return;
          expandFields(localPdfFormField, localArrayList);
          if (this.cstp.fieldTemplates == null)
            this.cstp.fieldTemplates = new HashMap();
        }
        else
        {
          localArrayList.add(paramPdfAnnotation);
        }
        for (int i = 0; i < localArrayList.size(); i++)
        {
          paramPdfAnnotation = (PdfAnnotation)localArrayList.get(i);
          Object localObject;
          if (paramPdfAnnotation.isForm())
          {
            if (!paramPdfAnnotation.isUsed())
            {
              localObject = paramPdfAnnotation.getTemplates();
              if (localObject != null)
                this.cstp.fieldTemplates.putAll((Map)localObject);
            }
            localObject = (PdfFormField)paramPdfAnnotation;
            if (((PdfFormField)localObject).getParent() == null)
              addDocumentField(((PdfFormField)localObject).getIndirectReference());
          }
          if (paramPdfAnnotation.isAnnotation())
          {
            localObject = PdfReader.getPdfObject(this.pageN.get(PdfName.ANNOTS), this.pageN);
            PdfArray localPdfArray = null;
            if ((localObject == null) || (!((PdfObject)localObject).isArray()))
            {
              localPdfArray = new PdfArray();
              this.pageN.put(PdfName.ANNOTS, localPdfArray);
            }
            else
            {
              localPdfArray = (PdfArray)localObject;
            }
            localPdfArray.add(paramPdfAnnotation.getIndirectReference());
            if (!paramPdfAnnotation.isUsed())
            {
              PdfRectangle localPdfRectangle = (PdfRectangle)paramPdfAnnotation.get(PdfName.RECT);
              if ((localPdfRectangle != null) && ((localPdfRectangle.left() != 0.0F) || (localPdfRectangle.right() != 0.0F) || (localPdfRectangle.top() != 0.0F) || (localPdfRectangle.bottom() != 0.0F)))
              {
                int j = this.reader.getPageRotation(this.pageN);
                Rectangle localRectangle = this.reader.getPageSizeWithRotation(this.pageN);
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
          this.cstp.addToBody(paramPdfAnnotation, paramPdfAnnotation.getIndirectReference());
        }
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
    }
  }

  protected static class RefKey
  {
    int num;
    int gen;

    RefKey(int paramInt1, int paramInt2)
    {
      this.num = paramInt1;
      this.gen = paramInt2;
    }

    RefKey(PdfIndirectReference paramPdfIndirectReference)
    {
      this.num = paramPdfIndirectReference.getNumber();
      this.gen = paramPdfIndirectReference.getGeneration();
    }

    RefKey(PRIndirectReference paramPRIndirectReference)
    {
      this.num = paramPRIndirectReference.getNumber();
      this.gen = paramPRIndirectReference.getGeneration();
    }

    public int hashCode()
    {
      return (this.gen << 16) + this.num;
    }

    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof RefKey))
        return false;
      RefKey localRefKey = (RefKey)paramObject;
      return (this.gen == localRefKey.gen) && (this.num == localRefKey.num);
    }

    public String toString()
    {
      return Integer.toString(this.num) + ' ' + this.gen;
    }
  }

  static class IndirectReferences
  {
    PdfIndirectReference theRef;
    boolean hasCopied;

    IndirectReferences(PdfIndirectReference paramPdfIndirectReference)
    {
      this.theRef = paramPdfIndirectReference;
      this.hasCopied = false;
    }

    void setCopied()
    {
      this.hasCopied = true;
    }

    boolean getCopied()
    {
      return this.hasCopied;
    }

    PdfIndirectReference getRef()
    {
      return this.theRef;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfCopy
 * JD-Core Version:    0.6.0
 */