package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.exceptions.InvalidPdfException;
import com.lowagie.text.exceptions.UnsupportedPdfException;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.zip.InflaterInputStream;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class PdfReader
  implements PdfViewerPreferences
{
  static final PdfName[] pageInhCandidates = { PdfName.MEDIABOX, PdfName.ROTATE, PdfName.RESOURCES, PdfName.CROPBOX };
  static final byte[] endstream = PdfEncodings.convertToBytes("endstream", null);
  static final byte[] endobj = PdfEncodings.convertToBytes("endobj", null);
  protected PRTokeniser tokens;
  protected int[] xref;
  protected HashMap objStmMark;
  protected IntHashtable objStmToOffset;
  protected boolean newXrefType;
  private ArrayList xrefObj;
  PdfDictionary rootPages;
  protected PdfDictionary trailer;
  protected PdfDictionary catalog;
  protected PageRefs pageRefs;
  protected PRAcroForm acroForm = null;
  protected boolean acroFormParsed = false;
  protected boolean encrypted = false;
  protected boolean rebuilt = false;
  protected int freeXref;
  protected boolean tampered = false;
  protected int lastXref;
  protected int eofPos;
  protected char pdfVersion;
  protected PdfEncryption decrypt;
  protected byte[] password = null;
  protected Key certificateKey = null;
  protected Certificate certificate = null;
  protected String certificateKeyProvider = null;
  private boolean ownerPasswordUsed;
  protected ArrayList strings = new ArrayList();
  protected boolean sharedStreams = true;
  protected boolean consolidateNamedDestinations = false;
  protected int rValue;
  protected int pValue;
  private int objNum;
  private int objGen;
  private int fileLength;
  private boolean hybridXref;
  private int lastXrefPartial = -1;
  private boolean partial;
  private PRIndirectReference cryptoRef;
  private PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
  private boolean encryptionError;
  private boolean appendable;
  private int readDepth = 0;

  protected PdfReader()
  {
  }

  public PdfReader(String paramString)
    throws IOException
  {
    this(paramString, null);
  }

  public PdfReader(String paramString, byte[] paramArrayOfByte)
    throws IOException
  {
    this.password = paramArrayOfByte;
    this.tokens = new PRTokeniser(paramString);
    readPdf();
  }

  public PdfReader(byte[] paramArrayOfByte)
    throws IOException
  {
    this(paramArrayOfByte, null);
  }

  public PdfReader(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws IOException
  {
    this.password = paramArrayOfByte2;
    this.tokens = new PRTokeniser(paramArrayOfByte1);
    readPdf();
  }

  public PdfReader(String paramString1, Certificate paramCertificate, Key paramKey, String paramString2)
    throws IOException
  {
    this.certificate = paramCertificate;
    this.certificateKey = paramKey;
    this.certificateKeyProvider = paramString2;
    this.tokens = new PRTokeniser(paramString1);
    readPdf();
  }

  public PdfReader(URL paramURL)
    throws IOException
  {
    this(paramURL, null);
  }

  public PdfReader(URL paramURL, byte[] paramArrayOfByte)
    throws IOException
  {
    this.password = paramArrayOfByte;
    this.tokens = new PRTokeniser(new RandomAccessFileOrArray(paramURL));
    readPdf();
  }

  public PdfReader(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    this.password = paramArrayOfByte;
    this.tokens = new PRTokeniser(new RandomAccessFileOrArray(paramInputStream));
    readPdf();
  }

  public PdfReader(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, null);
  }

  public PdfReader(RandomAccessFileOrArray paramRandomAccessFileOrArray, byte[] paramArrayOfByte)
    throws IOException
  {
    this.password = paramArrayOfByte;
    this.partial = true;
    this.tokens = new PRTokeniser(paramRandomAccessFileOrArray);
    readPdfPartial();
  }

  public PdfReader(PdfReader paramPdfReader)
  {
    this.appendable = paramPdfReader.appendable;
    this.consolidateNamedDestinations = paramPdfReader.consolidateNamedDestinations;
    this.encrypted = paramPdfReader.encrypted;
    this.rebuilt = paramPdfReader.rebuilt;
    this.sharedStreams = paramPdfReader.sharedStreams;
    this.tampered = paramPdfReader.tampered;
    this.password = paramPdfReader.password;
    this.pdfVersion = paramPdfReader.pdfVersion;
    this.eofPos = paramPdfReader.eofPos;
    this.freeXref = paramPdfReader.freeXref;
    this.lastXref = paramPdfReader.lastXref;
    this.tokens = new PRTokeniser(paramPdfReader.tokens.getSafeFile());
    if (paramPdfReader.decrypt != null)
      this.decrypt = new PdfEncryption(paramPdfReader.decrypt);
    this.pValue = paramPdfReader.pValue;
    this.rValue = paramPdfReader.rValue;
    this.xrefObj = new ArrayList(paramPdfReader.xrefObj);
    for (int i = 0; i < paramPdfReader.xrefObj.size(); i++)
      this.xrefObj.set(i, duplicatePdfObject((PdfObject)paramPdfReader.xrefObj.get(i), this));
    this.pageRefs = new PageRefs(paramPdfReader.pageRefs, this);
    this.trailer = ((PdfDictionary)duplicatePdfObject(paramPdfReader.trailer, this));
    this.catalog = this.trailer.getAsDict(PdfName.ROOT);
    this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
    this.fileLength = paramPdfReader.fileLength;
    this.partial = paramPdfReader.partial;
    this.hybridXref = paramPdfReader.hybridXref;
    this.objStmToOffset = paramPdfReader.objStmToOffset;
    this.xref = paramPdfReader.xref;
    this.cryptoRef = ((PRIndirectReference)duplicatePdfObject(paramPdfReader.cryptoRef, this));
    this.ownerPasswordUsed = paramPdfReader.ownerPasswordUsed;
  }

  public RandomAccessFileOrArray getSafeFile()
  {
    return this.tokens.getSafeFile();
  }

  protected PdfReaderInstance getPdfReaderInstance(PdfWriter paramPdfWriter)
  {
    return new PdfReaderInstance(this, paramPdfWriter);
  }

  public int getNumberOfPages()
  {
    return this.pageRefs.size();
  }

  public PdfDictionary getCatalog()
  {
    return this.catalog;
  }

  public PRAcroForm getAcroForm()
  {
    if (!this.acroFormParsed)
    {
      this.acroFormParsed = true;
      PdfObject localPdfObject = this.catalog.get(PdfName.ACROFORM);
      if (localPdfObject != null)
        try
        {
          this.acroForm = new PRAcroForm(this);
          this.acroForm.readAcroForm((PdfDictionary)getPdfObject(localPdfObject));
        }
        catch (Exception localException)
        {
          this.acroForm = null;
        }
    }
    return this.acroForm;
  }

  public int getPageRotation(int paramInt)
  {
    return getPageRotation(this.pageRefs.getPageNRelease(paramInt));
  }

  int getPageRotation(PdfDictionary paramPdfDictionary)
  {
    PdfNumber localPdfNumber = paramPdfDictionary.getAsNumber(PdfName.ROTATE);
    if (localPdfNumber == null)
      return 0;
    int i = localPdfNumber.intValue();
    i %= 360;
    return i < 0 ? i + 360 : i;
  }

  public Rectangle getPageSizeWithRotation(int paramInt)
  {
    return getPageSizeWithRotation(this.pageRefs.getPageNRelease(paramInt));
  }

  public Rectangle getPageSizeWithRotation(PdfDictionary paramPdfDictionary)
  {
    Rectangle localRectangle = getPageSize(paramPdfDictionary);
    for (int i = getPageRotation(paramPdfDictionary); i > 0; i -= 90)
      localRectangle = localRectangle.rotate();
    return localRectangle;
  }

  public Rectangle getPageSize(int paramInt)
  {
    return getPageSize(this.pageRefs.getPageNRelease(paramInt));
  }

  public Rectangle getPageSize(PdfDictionary paramPdfDictionary)
  {
    PdfArray localPdfArray = paramPdfDictionary.getAsArray(PdfName.MEDIABOX);
    return getNormalizedRectangle(localPdfArray);
  }

  public Rectangle getCropBox(int paramInt)
  {
    PdfDictionary localPdfDictionary = this.pageRefs.getPageNRelease(paramInt);
    PdfArray localPdfArray = (PdfArray)getPdfObjectRelease(localPdfDictionary.get(PdfName.CROPBOX));
    if (localPdfArray == null)
      return getPageSize(localPdfDictionary);
    return getNormalizedRectangle(localPdfArray);
  }

  public Rectangle getBoxSize(int paramInt, String paramString)
  {
    PdfDictionary localPdfDictionary = this.pageRefs.getPageNRelease(paramInt);
    PdfArray localPdfArray = null;
    if (paramString.equals("trim"))
      localPdfArray = (PdfArray)getPdfObjectRelease(localPdfDictionary.get(PdfName.TRIMBOX));
    else if (paramString.equals("art"))
      localPdfArray = (PdfArray)getPdfObjectRelease(localPdfDictionary.get(PdfName.ARTBOX));
    else if (paramString.equals("bleed"))
      localPdfArray = (PdfArray)getPdfObjectRelease(localPdfDictionary.get(PdfName.BLEEDBOX));
    else if (paramString.equals("crop"))
      localPdfArray = (PdfArray)getPdfObjectRelease(localPdfDictionary.get(PdfName.CROPBOX));
    else if (paramString.equals("media"))
      localPdfArray = (PdfArray)getPdfObjectRelease(localPdfDictionary.get(PdfName.MEDIABOX));
    if (localPdfArray == null)
      return null;
    return getNormalizedRectangle(localPdfArray);
  }

  public HashMap getInfo()
  {
    HashMap localHashMap = new HashMap();
    PdfDictionary localPdfDictionary = this.trailer.getAsDict(PdfName.INFO);
    if (localPdfDictionary == null)
      return localHashMap;
    Iterator localIterator = localPdfDictionary.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      PdfObject localPdfObject = getPdfObject(localPdfDictionary.get(localPdfName));
      if (localPdfObject == null)
        continue;
      String str = localPdfObject.toString();
      switch (localPdfObject.type())
      {
      case 3:
        str = ((PdfString)localPdfObject).toUnicodeString();
        break;
      case 4:
        str = PdfName.decodeName(str);
      }
      localHashMap.put(PdfName.decodeName(localPdfName.toString()), str);
    }
    return localHashMap;
  }

  public static Rectangle getNormalizedRectangle(PdfArray paramPdfArray)
  {
    float f1 = ((PdfNumber)getPdfObjectRelease(paramPdfArray.getPdfObject(0))).floatValue();
    float f2 = ((PdfNumber)getPdfObjectRelease(paramPdfArray.getPdfObject(1))).floatValue();
    float f3 = ((PdfNumber)getPdfObjectRelease(paramPdfArray.getPdfObject(2))).floatValue();
    float f4 = ((PdfNumber)getPdfObjectRelease(paramPdfArray.getPdfObject(3))).floatValue();
    return new Rectangle(Math.min(f1, f3), Math.min(f2, f4), Math.max(f1, f3), Math.max(f2, f4));
  }

  protected void readPdf()
    throws IOException
  {
    try
    {
      this.fileLength = this.tokens.getFile().length();
      this.pdfVersion = this.tokens.checkPdfHeader();
      try
      {
        readXref();
      }
      catch (Exception localException1)
      {
        try
        {
          this.rebuilt = true;
          rebuildXref();
          this.lastXref = -1;
        }
        catch (Exception localException4)
        {
          throw new InvalidPdfException("Rebuild failed: " + localException4.getMessage() + "; Original message: " + localException1.getMessage());
        }
      }
      try
      {
        readDocObj();
      }
      catch (Exception localException2)
      {
        if ((localException2 instanceof BadPasswordException))
          throw new BadPasswordException(localException2.getMessage());
        if ((this.rebuilt) || (this.encryptionError))
          throw new InvalidPdfException(localException2.getMessage());
        this.rebuilt = true;
        this.encrypted = false;
        rebuildXref();
        this.lastXref = -1;
        readDocObj();
      }
      this.strings.clear();
      readPages();
      eliminateSharedStreams();
      removeUnusedObjects();
    }
    finally
    {
      try
      {
        this.tokens.close();
      }
      catch (Exception localException5)
      {
      }
    }
  }

  protected void readPdfPartial()
    throws IOException
  {
    try
    {
      this.fileLength = this.tokens.getFile().length();
      this.pdfVersion = this.tokens.checkPdfHeader();
      try
      {
        readXref();
      }
      catch (Exception localException1)
      {
        try
        {
          this.rebuilt = true;
          rebuildXref();
          this.lastXref = -1;
        }
        catch (Exception localException2)
        {
          throw new InvalidPdfException("Rebuild failed: " + localException2.getMessage() + "; Original message: " + localException1.getMessage());
        }
      }
      readDocObjPartial();
      readPages();
    }
    catch (IOException localIOException)
    {
      try
      {
        this.tokens.close();
      }
      catch (Exception localException3)
      {
      }
      throw localIOException;
    }
  }

  private boolean equalsArray(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
      if (paramArrayOfByte1[i] != paramArrayOfByte2[i])
        return false;
    return true;
  }

  private void readDecryptedDocObj()
    throws IOException
  {
    if (this.encrypted)
      return;
    PdfObject localPdfObject1 = this.trailer.get(PdfName.ENCRYPT);
    if ((localPdfObject1 == null) || (localPdfObject1.toString().equals("null")))
      return;
    this.encryptionError = true;
    byte[] arrayOfByte1 = null;
    this.encrypted = true;
    PdfDictionary localPdfDictionary1 = (PdfDictionary)getPdfObject(localPdfObject1);
    PdfArray localPdfArray1 = this.trailer.getAsArray(PdfName.ID);
    byte[] arrayOfByte2 = null;
    PdfObject localPdfObject2;
    String str;
    if (localPdfArray1 != null)
    {
      localPdfObject2 = localPdfArray1.getPdfObject(0);
      this.strings.remove(localPdfObject2);
      str = localPdfObject2.toString();
      arrayOfByte2 = DocWriter.getISOBytes(str);
      if (localPdfArray1.size() > 1)
        this.strings.remove(localPdfArray1.getPdfObject(1));
    }
    if (arrayOfByte2 == null)
      arrayOfByte2 = new byte[0];
    byte[] arrayOfByte3 = null;
    byte[] arrayOfByte4 = null;
    int i = 0;
    int j = 0;
    PdfObject localPdfObject3 = getPdfObjectRelease(localPdfDictionary1.get(PdfName.FILTER));
    Object localObject1;
    if (localPdfObject3.equals(PdfName.STANDARD))
    {
      str = localPdfDictionary1.get(PdfName.U).toString();
      this.strings.remove(localPdfDictionary1.get(PdfName.U));
      arrayOfByte3 = DocWriter.getISOBytes(str);
      str = localPdfDictionary1.get(PdfName.O).toString();
      this.strings.remove(localPdfDictionary1.get(PdfName.O));
      arrayOfByte4 = DocWriter.getISOBytes(str);
      localPdfObject2 = localPdfDictionary1.get(PdfName.P);
      if (!localPdfObject2.isNumber())
        throw new InvalidPdfException("Illegal P value.");
      this.pValue = ((PdfNumber)localPdfObject2).intValue();
      localPdfObject2 = localPdfDictionary1.get(PdfName.R);
      if (!localPdfObject2.isNumber())
        throw new InvalidPdfException("Illegal R value.");
      this.rValue = ((PdfNumber)localPdfObject2).intValue();
      switch (this.rValue)
      {
      case 2:
        i = 0;
        break;
      case 3:
        localPdfObject2 = localPdfDictionary1.get(PdfName.LENGTH);
        if (!localPdfObject2.isNumber())
          throw new InvalidPdfException("Illegal Length value.");
        j = ((PdfNumber)localPdfObject2).intValue();
        if ((j > 128) || (j < 40) || (j % 8 != 0))
          throw new InvalidPdfException("Illegal Length value.");
        i = 1;
        break;
      case 4:
        PdfDictionary localPdfDictionary2 = (PdfDictionary)localPdfDictionary1.get(PdfName.CF);
        if (localPdfDictionary2 == null)
          throw new InvalidPdfException("/CF not found (encryption)");
        localPdfDictionary2 = (PdfDictionary)localPdfDictionary2.get(PdfName.STDCF);
        if (localPdfDictionary2 == null)
          throw new InvalidPdfException("/StdCF not found (encryption)");
        if (PdfName.V2.equals(localPdfDictionary2.get(PdfName.CFM)))
          i = 1;
        else if (PdfName.AESV2.equals(localPdfDictionary2.get(PdfName.CFM)))
          i = 2;
        else
          throw new UnsupportedPdfException("No compatible encryption found");
        localObject1 = localPdfDictionary1.get(PdfName.ENCRYPTMETADATA);
        if ((localObject1 == null) || (!((PdfObject)localObject1).toString().equals("false")))
          break;
        i |= 8;
        break;
      default:
        throw new UnsupportedPdfException("Unknown encryption type R = " + this.rValue);
      }
    }
    else if (localPdfObject3.equals(PdfName.PUBSEC))
    {
      k = 0;
      localObject1 = null;
      PdfArray localPdfArray2 = null;
      localPdfObject2 = localPdfDictionary1.get(PdfName.V);
      if (!localPdfObject2.isNumber())
        throw new InvalidPdfException("Illegal V value.");
      int m = ((PdfNumber)localPdfObject2).intValue();
      PdfObject localPdfObject4;
      switch (m)
      {
      case 1:
        i = 0;
        j = 40;
        localPdfArray2 = (PdfArray)localPdfDictionary1.get(PdfName.RECIPIENTS);
        break;
      case 2:
        localPdfObject2 = localPdfDictionary1.get(PdfName.LENGTH);
        if (!localPdfObject2.isNumber())
          throw new InvalidPdfException("Illegal Length value.");
        j = ((PdfNumber)localPdfObject2).intValue();
        if ((j > 128) || (j < 40) || (j % 8 != 0))
          throw new InvalidPdfException("Illegal Length value.");
        i = 1;
        localPdfArray2 = (PdfArray)localPdfDictionary1.get(PdfName.RECIPIENTS);
        break;
      case 4:
        PdfDictionary localPdfDictionary3 = (PdfDictionary)localPdfDictionary1.get(PdfName.CF);
        if (localPdfDictionary3 == null)
          throw new InvalidPdfException("/CF not found (encryption)");
        localPdfDictionary3 = (PdfDictionary)localPdfDictionary3.get(PdfName.DEFAULTCRYPTFILTER);
        if (localPdfDictionary3 == null)
          throw new InvalidPdfException("/DefaultCryptFilter not found (encryption)");
        if (PdfName.V2.equals(localPdfDictionary3.get(PdfName.CFM)))
        {
          i = 1;
          j = 128;
        }
        else if (PdfName.AESV2.equals(localPdfDictionary3.get(PdfName.CFM)))
        {
          i = 2;
          j = 128;
        }
        else
        {
          throw new UnsupportedPdfException("No compatible encryption found");
        }
        localPdfObject4 = localPdfDictionary3.get(PdfName.ENCRYPTMETADATA);
        if ((localPdfObject4 != null) && (localPdfObject4.toString().equals("false")))
          i |= 8;
        localPdfArray2 = (PdfArray)localPdfDictionary3.get(PdfName.RECIPIENTS);
        break;
      case 3:
      default:
        throw new UnsupportedPdfException("Unknown encryption type V = " + this.rValue);
      }
      Object localObject2;
      for (int n = 0; n < localPdfArray2.size(); n++)
      {
        localPdfObject4 = localPdfArray2.getPdfObject(n);
        this.strings.remove(localPdfObject4);
        localObject2 = null;
        try
        {
          localObject2 = new CMSEnvelopedData(localPdfObject4.getBytes());
          Iterator localIterator = ((CMSEnvelopedData)localObject2).getRecipientInfos().getRecipients().iterator();
          while (localIterator.hasNext())
          {
            RecipientInformation localRecipientInformation = (RecipientInformation)localIterator.next();
            if ((!localRecipientInformation.getRID().match(this.certificate)) || (k != 0))
              continue;
            localObject1 = localRecipientInformation.getContent(this.certificateKey, this.certificateKeyProvider);
            k = 1;
          }
        }
        catch (Exception localException2)
        {
          throw new ExceptionConverter(localException2);
        }
      }
      if ((k == 0) || (localObject1 == null))
        throw new UnsupportedPdfException("Bad certificate and key.");
      MessageDigest localMessageDigest = null;
      try
      {
        localMessageDigest = MessageDigest.getInstance("SHA-1");
        localMessageDigest.update(localObject1, 0, 20);
        for (int i1 = 0; i1 < localPdfArray2.size(); i1++)
        {
          localObject2 = localPdfArray2.getPdfObject(i1).getBytes();
          localMessageDigest.update(localObject2);
        }
        if ((i & 0x8) != 0)
          localMessageDigest.update(new byte[] { -1, -1, -1, -1 });
        arrayOfByte1 = localMessageDigest.digest();
      }
      catch (Exception localException1)
      {
        throw new ExceptionConverter(localException1);
      }
    }
    this.decrypt = new PdfEncryption();
    this.decrypt.setCryptoMode(i, j);
    if (localPdfObject3.equals(PdfName.STANDARD))
    {
      this.decrypt.setupByOwnerPassword(arrayOfByte2, this.password, arrayOfByte3, arrayOfByte4, this.pValue);
      if (!equalsArray(arrayOfByte3, this.decrypt.userKey, (this.rValue == 3) || (this.rValue == 4) ? 16 : 32))
      {
        this.decrypt.setupByUserPassword(arrayOfByte2, this.password, arrayOfByte4, this.pValue);
        if (!equalsArray(arrayOfByte3, this.decrypt.userKey, (this.rValue == 3) || (this.rValue == 4) ? 16 : 32))
          throw new BadPasswordException("Bad user password");
      }
      else
      {
        this.ownerPasswordUsed = true;
      }
    }
    else if (localPdfObject3.equals(PdfName.PUBSEC))
    {
      this.decrypt.setupByEncryptionKey(arrayOfByte1, j);
      this.ownerPasswordUsed = true;
    }
    for (int k = 0; k < this.strings.size(); k++)
    {
      localObject1 = (PdfString)this.strings.get(k);
      ((PdfString)localObject1).decrypt(this);
    }
    if (localPdfObject1.isIndirect())
    {
      this.cryptoRef = ((PRIndirectReference)localPdfObject1);
      this.xrefObj.set(this.cryptoRef.getNumber(), null);
    }
    this.encryptionError = false;
  }

  public static PdfObject getPdfObjectRelease(PdfObject paramPdfObject)
  {
    PdfObject localPdfObject = getPdfObject(paramPdfObject);
    releaseLastXrefPartial(paramPdfObject);
    return localPdfObject;
  }

  public static PdfObject getPdfObject(PdfObject paramPdfObject)
  {
    if (paramPdfObject == null)
      return null;
    if (!paramPdfObject.isIndirect())
      return paramPdfObject;
    try
    {
      PRIndirectReference localPRIndirectReference = (PRIndirectReference)paramPdfObject;
      int i = localPRIndirectReference.getNumber();
      boolean bool = localPRIndirectReference.getReader().appendable;
      paramPdfObject = localPRIndirectReference.getReader().getPdfObject(i);
      if (paramPdfObject == null)
        return null;
      if (bool)
      {
        switch (paramPdfObject.type())
        {
        case 8:
          paramPdfObject = new PdfNull();
          break;
        case 1:
          paramPdfObject = new PdfBoolean(((PdfBoolean)paramPdfObject).booleanValue());
          break;
        case 4:
          paramPdfObject = new PdfName(paramPdfObject.getBytes());
        }
        paramPdfObject.setIndRef(localPRIndirectReference);
      }
      return paramPdfObject;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public static PdfObject getPdfObjectRelease(PdfObject paramPdfObject1, PdfObject paramPdfObject2)
  {
    PdfObject localPdfObject = getPdfObject(paramPdfObject1, paramPdfObject2);
    releaseLastXrefPartial(paramPdfObject1);
    return localPdfObject;
  }

  public static PdfObject getPdfObject(PdfObject paramPdfObject1, PdfObject paramPdfObject2)
  {
    if (paramPdfObject1 == null)
      return null;
    if (!paramPdfObject1.isIndirect())
    {
      PRIndirectReference localPRIndirectReference = null;
      if ((paramPdfObject2 != null) && ((localPRIndirectReference = paramPdfObject2.getIndRef()) != null) && (localPRIndirectReference.getReader().isAppendable()))
      {
        switch (paramPdfObject1.type())
        {
        case 8:
          paramPdfObject1 = new PdfNull();
          break;
        case 1:
          paramPdfObject1 = new PdfBoolean(((PdfBoolean)paramPdfObject1).booleanValue());
          break;
        case 4:
          paramPdfObject1 = new PdfName(paramPdfObject1.getBytes());
        }
        paramPdfObject1.setIndRef(localPRIndirectReference);
      }
      return paramPdfObject1;
    }
    return getPdfObject(paramPdfObject1);
  }

  public PdfObject getPdfObjectRelease(int paramInt)
  {
    PdfObject localPdfObject = getPdfObject(paramInt);
    releaseLastXrefPartial();
    return localPdfObject;
  }

  public PdfObject getPdfObject(int paramInt)
  {
    try
    {
      this.lastXrefPartial = -1;
      if ((paramInt < 0) || (paramInt >= this.xrefObj.size()))
        return null;
      PdfObject localPdfObject = (PdfObject)this.xrefObj.get(paramInt);
      if ((!this.partial) || (localPdfObject != null))
        return localPdfObject;
      if (paramInt * 2 >= this.xref.length)
        return null;
      localPdfObject = readSingleObject(paramInt);
      this.lastXrefPartial = -1;
      if (localPdfObject != null)
        this.lastXrefPartial = paramInt;
      return localPdfObject;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public void resetLastXrefPartial()
  {
    this.lastXrefPartial = -1;
  }

  public void releaseLastXrefPartial()
  {
    if ((this.partial) && (this.lastXrefPartial != -1))
    {
      this.xrefObj.set(this.lastXrefPartial, null);
      this.lastXrefPartial = -1;
    }
  }

  public static void releaseLastXrefPartial(PdfObject paramPdfObject)
  {
    if (paramPdfObject == null)
      return;
    if (!paramPdfObject.isIndirect())
      return;
    if (!(paramPdfObject instanceof PRIndirectReference))
      return;
    PRIndirectReference localPRIndirectReference = (PRIndirectReference)paramPdfObject;
    PdfReader localPdfReader = localPRIndirectReference.getReader();
    if ((localPdfReader.partial) && (localPdfReader.lastXrefPartial != -1) && (localPdfReader.lastXrefPartial == localPRIndirectReference.getNumber()))
      localPdfReader.xrefObj.set(localPdfReader.lastXrefPartial, null);
    localPdfReader.lastXrefPartial = -1;
  }

  private void setXrefPartialObject(int paramInt, PdfObject paramPdfObject)
  {
    if ((!this.partial) || (paramInt < 0))
      return;
    this.xrefObj.set(paramInt, paramPdfObject);
  }

  public PRIndirectReference addPdfObject(PdfObject paramPdfObject)
  {
    this.xrefObj.add(paramPdfObject);
    return new PRIndirectReference(this, this.xrefObj.size() - 1);
  }

  protected void readPages()
    throws IOException
  {
    this.catalog = this.trailer.getAsDict(PdfName.ROOT);
    this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
    this.pageRefs = new PageRefs(this, null);
  }

  protected void readDocObjPartial()
    throws IOException
  {
    this.xrefObj = new ArrayList(this.xref.length / 2);
    this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
    readDecryptedDocObj();
    if (this.objStmToOffset != null)
    {
      int[] arrayOfInt = this.objStmToOffset.getKeys();
      for (int i = 0; i < arrayOfInt.length; i++)
      {
        int j = arrayOfInt[i];
        this.objStmToOffset.put(j, this.xref[(j * 2)]);
        this.xref[(j * 2)] = -1;
      }
    }
  }

  protected PdfObject readSingleObject(int paramInt)
    throws IOException
  {
    this.strings.clear();
    int i = paramInt * 2;
    int j = this.xref[i];
    if (j < 0)
      return null;
    if (this.xref[(i + 1)] > 0)
      j = this.objStmToOffset.get(this.xref[(i + 1)]);
    if (j == 0)
      return null;
    this.tokens.seek(j);
    this.tokens.nextValidToken();
    if (this.tokens.getTokenType() != 1)
      this.tokens.throwError("Invalid object number.");
    this.objNum = this.tokens.intValue();
    this.tokens.nextValidToken();
    if (this.tokens.getTokenType() != 1)
      this.tokens.throwError("Invalid generation number.");
    this.objGen = this.tokens.intValue();
    this.tokens.nextValidToken();
    if (!this.tokens.getStringValue().equals("obj"))
      this.tokens.throwError("Token 'obj' expected.");
    PdfObject localPdfObject;
    try
    {
      localPdfObject = readPRObject();
      for (int k = 0; k < this.strings.size(); k++)
      {
        PdfString localPdfString = (PdfString)this.strings.get(k);
        localPdfString.decrypt(this);
      }
      if (localPdfObject.isStream())
        checkPRStreamLength((PRStream)localPdfObject);
    }
    catch (Exception localException)
    {
      localPdfObject = null;
    }
    if (this.xref[(i + 1)] > 0)
      localPdfObject = readOneObjStm((PRStream)localPdfObject, this.xref[i]);
    this.xrefObj.set(paramInt, localPdfObject);
    return localPdfObject;
  }

  protected PdfObject readOneObjStm(PRStream paramPRStream, int paramInt)
    throws IOException
  {
    int i = paramPRStream.getAsNumber(PdfName.FIRST).intValue();
    byte[] arrayOfByte = getStreamBytes(paramPRStream, this.tokens.getFile());
    PRTokeniser localPRTokeniser = this.tokens;
    this.tokens = new PRTokeniser(arrayOfByte);
    try
    {
      int j = 0;
      boolean bool = true;
      paramInt++;
      for (int k = 0; k < paramInt; k++)
      {
        bool = this.tokens.nextToken();
        if (!bool)
          break;
        if (this.tokens.getTokenType() != 1)
        {
          bool = false;
          break;
        }
        bool = this.tokens.nextToken();
        if (!bool)
          break;
        if (this.tokens.getTokenType() != 1)
        {
          bool = false;
          break;
        }
        j = this.tokens.intValue() + i;
      }
      if (!bool)
        throw new InvalidPdfException("Error reading ObjStm");
      this.tokens.seek(j);
      PdfObject localPdfObject = readPRObject();
      return localPdfObject;
    }
    finally
    {
      this.tokens = localPRTokeniser;
    }
    throw localObject;
  }

  public double dumpPerc()
  {
    int i = 0;
    for (int j = 0; j < this.xrefObj.size(); j++)
    {
      if (this.xrefObj.get(j) == null)
        continue;
      i++;
    }
    return i * 100.0D / this.xrefObj.size();
  }

  protected void readDocObj()
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    this.xrefObj = new ArrayList(this.xref.length / 2);
    this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
    for (int i = 2; i < this.xref.length; i += 2)
    {
      int j = this.xref[i];
      if ((j <= 0) || (this.xref[(i + 1)] > 0))
        continue;
      this.tokens.seek(j);
      this.tokens.nextValidToken();
      if (this.tokens.getTokenType() != 1)
        this.tokens.throwError("Invalid object number.");
      this.objNum = this.tokens.intValue();
      this.tokens.nextValidToken();
      if (this.tokens.getTokenType() != 1)
        this.tokens.throwError("Invalid generation number.");
      this.objGen = this.tokens.intValue();
      this.tokens.nextValidToken();
      if (!this.tokens.getStringValue().equals("obj"))
        this.tokens.throwError("Token 'obj' expected.");
      PdfObject localPdfObject;
      try
      {
        localPdfObject = readPRObject();
        if (localPdfObject.isStream())
          localArrayList.add(localPdfObject);
      }
      catch (Exception localException)
      {
        localPdfObject = null;
      }
      this.xrefObj.set(i / 2, localPdfObject);
    }
    for (i = 0; i < localArrayList.size(); i++)
      checkPRStreamLength((PRStream)localArrayList.get(i));
    readDecryptedDocObj();
    if (this.objStmMark != null)
    {
      Iterator localIterator = this.objStmMark.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        int k = ((Integer)localEntry.getKey()).intValue();
        IntHashtable localIntHashtable = (IntHashtable)localEntry.getValue();
        readObjStm((PRStream)this.xrefObj.get(k), localIntHashtable);
        this.xrefObj.set(k, null);
      }
      this.objStmMark = null;
    }
    this.xref = null;
  }

  private void checkPRStreamLength(PRStream paramPRStream)
    throws IOException
  {
    int i = this.tokens.length();
    int j = paramPRStream.getOffset();
    int k = 0;
    int m = 0;
    PdfObject localPdfObject = getPdfObjectRelease(paramPRStream.get(PdfName.LENGTH));
    Object localObject;
    if ((localPdfObject != null) && (localPdfObject.type() == 2))
    {
      m = ((PdfNumber)localPdfObject).intValue();
      if (m + j > i - 20)
      {
        k = 1;
      }
      else
      {
        this.tokens.seek(j + m);
        localObject = this.tokens.readString(20);
        if ((!((String)localObject).startsWith("\nendstream")) && (!((String)localObject).startsWith("\r\nendstream")) && (!((String)localObject).startsWith("\rendstream")) && (!((String)localObject).startsWith("endstream")))
          k = 1;
      }
    }
    else
    {
      k = 1;
    }
    if (k != 0)
    {
      localObject = new byte[16];
      this.tokens.seek(j);
      while (true)
      {
        int n = this.tokens.getFilePointer();
        if (!this.tokens.readLineSegment(localObject))
          break;
        if (equalsn(localObject, endstream))
        {
          m = n - j;
        }
        else
        {
          if (!equalsn(localObject, endobj))
            continue;
          this.tokens.seek(n - 16);
          String str = this.tokens.readString(16);
          int i1 = str.indexOf("endstream");
          if (i1 >= 0)
            n = n - 16 + i1;
          m = n - j;
        }
      }
    }
    paramPRStream.setLength(m);
  }

  protected void readObjStm(PRStream paramPRStream, IntHashtable paramIntHashtable)
    throws IOException
  {
    int i = paramPRStream.getAsNumber(PdfName.FIRST).intValue();
    int j = paramPRStream.getAsNumber(PdfName.N).intValue();
    byte[] arrayOfByte = getStreamBytes(paramPRStream, this.tokens.getFile());
    PRTokeniser localPRTokeniser = this.tokens;
    this.tokens = new PRTokeniser(arrayOfByte);
    try
    {
      int[] arrayOfInt1 = new int[j];
      int[] arrayOfInt2 = new int[j];
      boolean bool = true;
      for (int k = 0; k < j; k++)
      {
        bool = this.tokens.nextToken();
        if (!bool)
          break;
        if (this.tokens.getTokenType() != 1)
        {
          bool = false;
          break;
        }
        arrayOfInt2[k] = this.tokens.intValue();
        bool = this.tokens.nextToken();
        if (!bool)
          break;
        if (this.tokens.getTokenType() != 1)
        {
          bool = false;
          break;
        }
        arrayOfInt1[k] = (this.tokens.intValue() + i);
      }
      if (!bool)
        throw new InvalidPdfException("Error reading ObjStm");
      for (k = 0; k < j; k++)
      {
        if (!paramIntHashtable.containsKey(k))
          continue;
        this.tokens.seek(arrayOfInt1[k]);
        PdfObject localPdfObject = readPRObject();
        this.xrefObj.set(arrayOfInt2[k], localPdfObject);
      }
    }
    finally
    {
      this.tokens = localPRTokeniser;
    }
  }

  public static PdfObject killIndirect(PdfObject paramPdfObject)
  {
    if ((paramPdfObject == null) || (paramPdfObject.isNull()))
      return null;
    PdfObject localPdfObject = getPdfObjectRelease(paramPdfObject);
    if (paramPdfObject.isIndirect())
    {
      PRIndirectReference localPRIndirectReference = (PRIndirectReference)paramPdfObject;
      PdfReader localPdfReader = localPRIndirectReference.getReader();
      int i = localPRIndirectReference.getNumber();
      localPdfReader.xrefObj.set(i, null);
      if (localPdfReader.partial)
        localPdfReader.xref[(i * 2)] = -1;
    }
    return localPdfObject;
  }

  private void ensureXrefSize(int paramInt)
  {
    if (paramInt == 0)
      return;
    if (this.xref == null)
    {
      this.xref = new int[paramInt];
    }
    else if (this.xref.length < paramInt)
    {
      int[] arrayOfInt = new int[paramInt];
      System.arraycopy(this.xref, 0, arrayOfInt, 0, this.xref.length);
      this.xref = arrayOfInt;
    }
  }

  protected void readXref()
    throws IOException
  {
    this.hybridXref = false;
    this.newXrefType = false;
    this.tokens.seek(this.tokens.getStartxref());
    this.tokens.nextToken();
    if (!this.tokens.getStringValue().equals("startxref"))
      throw new InvalidPdfException("startxref not found.");
    this.tokens.nextToken();
    if (this.tokens.getTokenType() != 1)
      throw new InvalidPdfException("startxref is not followed by a number.");
    int i = this.tokens.intValue();
    this.lastXref = i;
    this.eofPos = this.tokens.getFilePointer();
    try
    {
      if (readXRefStream(i))
      {
        this.newXrefType = true;
        return;
      }
    }
    catch (Exception localException)
    {
    }
    this.xref = null;
    this.tokens.seek(i);
    this.trailer = readXrefSection();
    for (PdfDictionary localPdfDictionary = this.trailer; ; localPdfDictionary = readXrefSection())
    {
      PdfNumber localPdfNumber = (PdfNumber)localPdfDictionary.get(PdfName.PREV);
      if (localPdfNumber == null)
        break;
      this.tokens.seek(localPdfNumber.intValue());
    }
  }

  protected PdfDictionary readXrefSection()
    throws IOException
  {
    this.tokens.nextValidToken();
    if (!this.tokens.getStringValue().equals("xref"))
      this.tokens.throwError("xref subsection not found");
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    while (true)
    {
      this.tokens.nextValidToken();
      if (this.tokens.getStringValue().equals("trailer"))
        break;
      if (this.tokens.getTokenType() != 1)
        this.tokens.throwError("Object number of the first object in this xref subsection not found");
      i = this.tokens.intValue();
      this.tokens.nextValidToken();
      if (this.tokens.getTokenType() != 1)
        this.tokens.throwError("Number of entries in this xref subsection not found");
      j = this.tokens.intValue() + i;
      if (i == 1)
      {
        n = this.tokens.getFilePointer();
        this.tokens.nextValidToken();
        k = this.tokens.intValue();
        this.tokens.nextValidToken();
        m = this.tokens.intValue();
        if ((k == 0) && (m == 65535))
        {
          i--;
          j--;
        }
        this.tokens.seek(n);
      }
      ensureXrefSize(j * 2);
      for (int n = i; n < j; n++)
      {
        this.tokens.nextValidToken();
        k = this.tokens.intValue();
        this.tokens.nextValidToken();
        m = this.tokens.intValue();
        this.tokens.nextValidToken();
        int i1 = n * 2;
        if (this.tokens.getStringValue().equals("n"))
        {
          if ((this.xref[i1] != 0) || (this.xref[(i1 + 1)] != 0))
            continue;
          this.xref[i1] = k;
        }
        else if (this.tokens.getStringValue().equals("f"))
        {
          if ((this.xref[i1] != 0) || (this.xref[(i1 + 1)] != 0))
            continue;
          this.xref[i1] = -1;
        }
        else
        {
          this.tokens.throwError("Invalid cross-reference entry in this xref subsection");
        }
      }
    }
    PdfDictionary localPdfDictionary = (PdfDictionary)readPRObject();
    PdfNumber localPdfNumber = (PdfNumber)localPdfDictionary.get(PdfName.SIZE);
    ensureXrefSize(localPdfNumber.intValue() * 2);
    PdfObject localPdfObject = localPdfDictionary.get(PdfName.XREFSTM);
    if ((localPdfObject != null) && (localPdfObject.isNumber()))
    {
      int i2 = ((PdfNumber)localPdfObject).intValue();
      try
      {
        readXRefStream(i2);
        this.newXrefType = true;
        this.hybridXref = true;
      }
      catch (IOException localIOException)
      {
        this.xref = null;
        throw localIOException;
      }
    }
    return localPdfDictionary;
  }

  protected boolean readXRefStream(int paramInt)
    throws IOException
  {
    this.tokens.seek(paramInt);
    int i = 0;
    if (!this.tokens.nextToken())
      return false;
    if (this.tokens.getTokenType() != 1)
      return false;
    i = this.tokens.intValue();
    if ((!this.tokens.nextToken()) || (this.tokens.getTokenType() != 1))
      return false;
    if ((!this.tokens.nextToken()) || (!this.tokens.getStringValue().equals("obj")))
      return false;
    PdfObject localPdfObject1 = readPRObject();
    PRStream localPRStream = null;
    if (localPdfObject1.isStream())
    {
      localPRStream = (PRStream)localPdfObject1;
      if (!PdfName.XREF.equals(localPRStream.get(PdfName.TYPE)))
        return false;
    }
    else
    {
      return false;
    }
    if (this.trailer == null)
    {
      this.trailer = new PdfDictionary();
      this.trailer.putAll(localPRStream);
    }
    localPRStream.setLength(((PdfNumber)localPRStream.get(PdfName.LENGTH)).intValue());
    int j = ((PdfNumber)localPRStream.get(PdfName.SIZE)).intValue();
    PdfObject localPdfObject2 = localPRStream.get(PdfName.INDEX);
    PdfArray localPdfArray1;
    if (localPdfObject2 == null)
    {
      localPdfArray1 = new PdfArray();
      localPdfArray1.add(new int[] { 0, j });
    }
    else
    {
      localPdfArray1 = (PdfArray)localPdfObject2;
    }
    PdfArray localPdfArray2 = (PdfArray)localPRStream.get(PdfName.W);
    int k = -1;
    localPdfObject2 = localPRStream.get(PdfName.PREV);
    if (localPdfObject2 != null)
      k = ((PdfNumber)localPdfObject2).intValue();
    ensureXrefSize(j * 2);
    if ((this.objStmMark == null) && (!this.partial))
      this.objStmMark = new HashMap();
    if ((this.objStmToOffset == null) && (this.partial))
      this.objStmToOffset = new IntHashtable();
    byte[] arrayOfByte = getStreamBytes(localPRStream, this.tokens.getFile());
    int m = 0;
    int[] arrayOfInt = new int[3];
    for (int n = 0; n < 3; n++)
      arrayOfInt[n] = localPdfArray2.getAsNumber(n).intValue();
    for (n = 0; n < localPdfArray1.size(); n += 2)
    {
      int i1 = localPdfArray1.getAsNumber(n).intValue();
      int i2 = localPdfArray1.getAsNumber(n + 1).intValue();
      ensureXrefSize((i1 + i2) * 2);
      while (i2-- > 0)
      {
        int i3 = 1;
        if (arrayOfInt[0] > 0)
        {
          i3 = 0;
          for (i4 = 0; i4 < arrayOfInt[0]; i4++)
            i3 = (i3 << 8) + (arrayOfByte[(m++)] & 0xFF);
        }
        int i4 = 0;
        for (int i5 = 0; i5 < arrayOfInt[1]; i5++)
          i4 = (i4 << 8) + (arrayOfByte[(m++)] & 0xFF);
        i5 = 0;
        for (int i6 = 0; i6 < arrayOfInt[2]; i6++)
          i5 = (i5 << 8) + (arrayOfByte[(m++)] & 0xFF);
        i6 = i1 * 2;
        if ((this.xref[i6] == 0) && (this.xref[(i6 + 1)] == 0))
          switch (i3)
          {
          case 0:
            this.xref[i6] = -1;
            break;
          case 1:
            this.xref[i6] = i4;
            break;
          case 2:
            this.xref[i6] = i5;
            this.xref[(i6 + 1)] = i4;
            if (this.partial)
            {
              this.objStmToOffset.put(i4, 0);
            }
            else
            {
              Integer localInteger = new Integer(i4);
              IntHashtable localIntHashtable = (IntHashtable)this.objStmMark.get(localInteger);
              if (localIntHashtable == null)
              {
                localIntHashtable = new IntHashtable();
                localIntHashtable.put(i5, 1);
                this.objStmMark.put(localInteger, localIntHashtable);
              }
              else
              {
                localIntHashtable.put(i5, 1);
              }
            }
          }
        i1++;
      }
    }
    i *= 2;
    if (i < this.xref.length)
      this.xref[i] = -1;
    if (k == -1)
      return true;
    return readXRefStream(k);
  }

  protected void rebuildXref()
    throws IOException
  {
    this.hybridXref = false;
    this.newXrefType = false;
    this.tokens.seek(0);
    Object localObject = new int[1024][];
    int i = 0;
    this.trailer = null;
    byte[] arrayOfByte = new byte[64];
    int[] arrayOfInt;
    while (true)
    {
      j = this.tokens.getFilePointer();
      if (!this.tokens.readLineSegment(arrayOfByte))
        break;
      if (arrayOfByte[0] == 116)
      {
        if (!PdfEncodings.convertToString(arrayOfByte, null).startsWith("trailer"))
          continue;
        this.tokens.seek(j);
        this.tokens.nextToken();
        j = this.tokens.getFilePointer();
        try
        {
          PdfDictionary localPdfDictionary = (PdfDictionary)readPRObject();
          if (localPdfDictionary.get(PdfName.ROOT) != null)
            this.trailer = localPdfDictionary;
          else
            this.tokens.seek(j);
        }
        catch (Exception localException)
        {
          this.tokens.seek(j);
        }
        continue;
      }
      if ((arrayOfByte[0] < 48) || (arrayOfByte[0] > 57))
        continue;
      arrayOfInt = PRTokeniser.checkObjectStart(arrayOfByte);
      if (arrayOfInt == null)
        continue;
      int k = arrayOfInt[0];
      int m = arrayOfInt[1];
      if (k >= localObject.length)
      {
        int n = k * 2;
        int[][] arrayOfInt1 = new int[n][];
        System.arraycopy(localObject, 0, arrayOfInt1, 0, i);
        localObject = arrayOfInt1;
      }
      if (k >= i)
        i = k + 1;
      if ((localObject[k] != null) && (m < localObject[k][1]))
        continue;
      arrayOfInt[0] = j;
      localObject[k] = arrayOfInt;
    }
    if (this.trailer == null)
      throw new InvalidPdfException("trailer not found.");
    this.xref = new int[i * 2];
    for (int j = 0; j < i; j++)
    {
      arrayOfInt = localObject[j];
      if (arrayOfInt == null)
        continue;
      this.xref[(j * 2)] = arrayOfInt[0];
    }
  }

  protected PdfDictionary readDictionary()
    throws IOException
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    while (true)
    {
      this.tokens.nextValidToken();
      if (this.tokens.getTokenType() == 8)
        break;
      if (this.tokens.getTokenType() != 3)
        this.tokens.throwError("Dictionary key is not a name.");
      PdfName localPdfName = new PdfName(this.tokens.getStringValue(), false);
      PdfObject localPdfObject = readPRObject();
      int i = localPdfObject.type();
      if (-i == 8)
        this.tokens.throwError("Unexpected '>>'");
      if (-i == 6)
        this.tokens.throwError("Unexpected ']'");
      localPdfDictionary.put(localPdfName, localPdfObject);
    }
    return localPdfDictionary;
  }

  protected PdfArray readArray()
    throws IOException
  {
    PdfArray localPdfArray = new PdfArray();
    while (true)
    {
      PdfObject localPdfObject = readPRObject();
      int i = localPdfObject.type();
      if (-i == 6)
        break;
      if (-i == 8)
        this.tokens.throwError("Unexpected '>>'");
      localPdfArray.add(localPdfObject);
    }
    return localPdfArray;
  }

  protected PdfObject readPRObject()
    throws IOException
  {
    this.tokens.nextValidToken();
    int i = this.tokens.getTokenType();
    Object localObject;
    switch (i)
    {
    case 7:
      this.readDepth += 1;
      localObject = readDictionary();
      this.readDepth -= 1;
      int j = this.tokens.getFilePointer();
      boolean bool;
      do
        bool = this.tokens.nextToken();
      while ((bool) && (this.tokens.getTokenType() == 4));
      if ((bool) && (this.tokens.getStringValue().equals("stream")))
      {
        int m;
        do
          m = this.tokens.read();
        while ((m == 32) || (m == 9) || (m == 0) || (m == 12));
        if (m != 10)
          m = this.tokens.read();
        if (m != 10)
          this.tokens.backOnePosition(m);
        PRStream localPRStream = new PRStream(this, this.tokens.getFilePointer());
        localPRStream.putAll((PdfDictionary)localObject);
        localPRStream.setObjNum(this.objNum, this.objGen);
        return localPRStream;
      }
      this.tokens.seek(j);
      return localObject;
    case 5:
      this.readDepth += 1;
      localObject = readArray();
      this.readDepth -= 1;
      return localObject;
    case 1:
      return new PdfNumber(this.tokens.getStringValue());
    case 2:
      localObject = new PdfString(this.tokens.getStringValue(), null).setHexWriting(this.tokens.isHexString());
      ((PdfString)localObject).setObjNum(this.objNum, this.objGen);
      if (this.strings != null)
        this.strings.add(localObject);
      return localObject;
    case 3:
      PdfName localPdfName = (PdfName)PdfName.staticNames.get(this.tokens.getStringValue());
      if ((this.readDepth > 0) && (localPdfName != null))
        return localPdfName;
      return new PdfName(this.tokens.getStringValue(), false);
    case 9:
      int k = this.tokens.getReference();
      PRIndirectReference localPRIndirectReference = new PRIndirectReference(this, k, this.tokens.getGeneration());
      return localPRIndirectReference;
    case 4:
    case 6:
    case 8:
    }
    String str = this.tokens.getStringValue();
    if ("null".equals(str))
    {
      if (this.readDepth == 0)
        return new PdfNull();
      return PdfNull.PDFNULL;
    }
    if ("true".equals(str))
    {
      if (this.readDepth == 0)
        return new PdfBoolean(true);
      return PdfBoolean.PDFTRUE;
    }
    if ("false".equals(str))
    {
      if (this.readDepth == 0)
        return new PdfBoolean(false);
      return PdfBoolean.PDFFALSE;
    }
    return (PdfObject)new PdfLiteral(-i, this.tokens.getStringValue());
  }

  public static byte[] FlateDecode(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = FlateDecode(paramArrayOfByte, true);
    if (arrayOfByte == null)
      return FlateDecode(paramArrayOfByte, false);
    return arrayOfByte;
  }

  public static byte[] decodePredictor(byte[] paramArrayOfByte, PdfObject paramPdfObject)
  {
    if ((paramPdfObject == null) || (!paramPdfObject.isDictionary()))
      return paramArrayOfByte;
    PdfDictionary localPdfDictionary = (PdfDictionary)paramPdfObject;
    PdfObject localPdfObject = getPdfObject(localPdfDictionary.get(PdfName.PREDICTOR));
    if ((localPdfObject == null) || (!localPdfObject.isNumber()))
      return paramArrayOfByte;
    int i = ((PdfNumber)localPdfObject).intValue();
    if (i < 10)
      return paramArrayOfByte;
    int j = 1;
    localPdfObject = getPdfObject(localPdfDictionary.get(PdfName.COLUMNS));
    if ((localPdfObject != null) && (localPdfObject.isNumber()))
      j = ((PdfNumber)localPdfObject).intValue();
    int k = 1;
    localPdfObject = getPdfObject(localPdfDictionary.get(PdfName.COLORS));
    if ((localPdfObject != null) && (localPdfObject.isNumber()))
      k = ((PdfNumber)localPdfObject).intValue();
    int m = 8;
    localPdfObject = getPdfObject(localPdfDictionary.get(PdfName.BITSPERCOMPONENT));
    if ((localPdfObject != null) && (localPdfObject.isNumber()))
      m = ((PdfNumber)localPdfObject).intValue();
    DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte));
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramArrayOfByte.length);
    Exception localException1 = k * m / 8;
    Exception localException2 = (k * j * m + 7) / 8;
    Object localObject1 = new byte[localException2];
    Object localObject2 = new byte[localException2];
    while (true)
    {
      int n = 0;
      try
      {
        n = localDataInputStream.read();
        if (n < 0)
          return localByteArrayOutputStream.toByteArray();
        localDataInputStream.readFully(localObject1, 0, localException2);
      }
      catch (Exception localException3)
      {
        return localByteArrayOutputStream.toByteArray();
      }
      switch (n)
      {
      case 0:
        break;
      case 1:
        localException3 = localException1;
      case 2:
      case 3:
      case 4:
      default:
        while (localException3 < localException2)
        {
          Exception tmp326_324 = localException3;
          Object tmp326_322 = localObject1;
          tmp326_322[tmp326_324] = (byte)(tmp326_322[tmp326_324] + localObject1[(localException3 - localException1)]);
          localException3++;
          continue;
          Exception localException4 = 0;
          while (localException4 < localException2)
          {
            Exception tmp359_357 = localException4;
            Object tmp359_355 = localObject1;
            tmp359_355[tmp359_357] = (byte)(tmp359_355[tmp359_357] + localObject2[localException4]);
            localException4++;
            continue;
            for (Exception localException5 = 0; localException5 < localException1; localException5++)
            {
              Exception tmp389_387 = localException5;
              Object tmp389_385 = localObject1;
              tmp389_385[tmp389_387] = (byte)(tmp389_385[tmp389_387] + localObject2[localException5] / 2);
            }
            Exception localException6 = localException1;
            while (localException6 < localException2)
            {
              Exception tmp422_420 = localException6;
              Object tmp422_418 = localObject1;
              tmp422_418[tmp422_420] = (byte)(tmp422_418[tmp422_420] + ((localObject1[(localException6 - localException1)] & 0xFF) + (localObject2[localException6] & 0xFF)) / 2);
              localException6++;
              continue;
              for (Exception localException7 = 0; localException7 < localException1; localException7++)
              {
                Exception tmp471_469 = localException7;
                Object tmp471_467 = localObject1;
                tmp471_467[tmp471_469] = (byte)(tmp471_467[tmp471_469] + localObject2[localException7]);
              }
              Exception localException8 = localException1;
              while (localException8 < localException2)
              {
                int i1 = localObject1[(localException8 - localException1)] & 0xFF;
                int i2 = localObject2[localException8] & 0xFF;
                int i3 = localObject2[(localException8 - localException1)] & 0xFF;
                int i4 = i1 + i2 - i3;
                int i5 = Math.abs(i4 - i1);
                int i6 = Math.abs(i4 - i2);
                int i7 = Math.abs(i4 - i3);
                int i8;
                if ((i5 <= i6) && (i5 <= i7))
                  i8 = i1;
                else if (i6 <= i7)
                  i8 = i2;
                else
                  i8 = i3;
                Exception tmp620_618 = localException8;
                Object tmp620_616 = localObject1;
                tmp620_616[tmp620_618] = (byte)(tmp620_616[tmp620_618] + (byte)i8);
                localException8++;
                continue;
                throw new RuntimeException("PNG filter unknown.");
              }
            }
          }
        }
      }
      try
      {
        localByteArrayOutputStream.write(localObject1);
      }
      catch (IOException localIOException)
      {
      }
      Object localObject3 = localObject2;
      localObject2 = localObject1;
      localObject1 = localObject3;
    }
  }

  public static byte[] FlateDecode(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    InflaterInputStream localInflaterInputStream = new InflaterInputStream(localByteArrayInputStream);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[paramBoolean ? 4092 : 1];
    try
    {
      int i;
      while ((i = localInflaterInputStream.read(arrayOfByte)) >= 0)
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      localInflaterInputStream.close();
      localByteArrayOutputStream.close();
      return localByteArrayOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
      if (paramBoolean)
        return null;
    }
    return localByteArrayOutputStream.toByteArray();
  }

  public static byte[] ASCIIHexDecode(byte[] paramArrayOfByte)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 1;
    int j = 0;
    for (int k = 0; k < paramArrayOfByte.length; k++)
    {
      int m = paramArrayOfByte[k] & 0xFF;
      if (m == 62)
        break;
      if (PRTokeniser.isWhitespace(m))
        continue;
      int n = PRTokeniser.getHex(m);
      if (n == -1)
        throw new RuntimeException("Illegal character in ASCIIHexDecode.");
      if (i != 0)
        j = n;
      else
        localByteArrayOutputStream.write((byte)((j << 4) + n));
      i = i == 0 ? 1 : 0;
    }
    if (i == 0)
      localByteArrayOutputStream.write((byte)(j << 4));
    return localByteArrayOutputStream.toByteArray();
  }

  public static byte[] ASCII85Decode(byte[] paramArrayOfByte)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    int[] arrayOfInt = new int[5];
    for (int j = 0; j < paramArrayOfByte.length; j++)
    {
      int k = paramArrayOfByte[j] & 0xFF;
      if (k == 126)
        break;
      if (PRTokeniser.isWhitespace(k))
        continue;
      if ((k == 122) && (i == 0))
      {
        localByteArrayOutputStream.write(0);
        localByteArrayOutputStream.write(0);
        localByteArrayOutputStream.write(0);
        localByteArrayOutputStream.write(0);
      }
      else
      {
        if ((k < 33) || (k > 117))
          throw new RuntimeException("Illegal character in ASCII85Decode.");
        arrayOfInt[i] = (k - 33);
        i++;
        if (i != 5)
          continue;
        i = 0;
        int m = 0;
        for (int n = 0; n < 5; n++)
          m = m * 85 + arrayOfInt[n];
        localByteArrayOutputStream.write((byte)(m >> 24));
        localByteArrayOutputStream.write((byte)(m >> 16));
        localByteArrayOutputStream.write((byte)(m >> 8));
        localByteArrayOutputStream.write((byte)m);
      }
    }
    j = 0;
    if (i == 2)
    {
      j = arrayOfInt[0] * 85 * 85 * 85 * 85 + arrayOfInt[1] * 85 * 85 * 85 + 614125 + 7225 + 85;
      localByteArrayOutputStream.write((byte)(j >> 24));
    }
    else if (i == 3)
    {
      j = arrayOfInt[0] * 85 * 85 * 85 * 85 + arrayOfInt[1] * 85 * 85 * 85 + arrayOfInt[2] * 85 * 85 + 7225 + 85;
      localByteArrayOutputStream.write((byte)(j >> 24));
      localByteArrayOutputStream.write((byte)(j >> 16));
    }
    else if (i == 4)
    {
      j = arrayOfInt[0] * 85 * 85 * 85 * 85 + arrayOfInt[1] * 85 * 85 * 85 + arrayOfInt[2] * 85 * 85 + arrayOfInt[3] * 85 + 85;
      localByteArrayOutputStream.write((byte)(j >> 24));
      localByteArrayOutputStream.write((byte)(j >> 16));
      localByteArrayOutputStream.write((byte)(j >> 8));
    }
    return localByteArrayOutputStream.toByteArray();
  }

  public static byte[] LZWDecode(byte[] paramArrayOfByte)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    LZWDecoder localLZWDecoder = new LZWDecoder();
    localLZWDecoder.decode(paramArrayOfByte, localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }

  public boolean isRebuilt()
  {
    return this.rebuilt;
  }

  public PdfDictionary getPageN(int paramInt)
  {
    PdfDictionary localPdfDictionary = this.pageRefs.getPageN(paramInt);
    if (localPdfDictionary == null)
      return null;
    if (this.appendable)
      localPdfDictionary.setIndRef(this.pageRefs.getPageOrigRef(paramInt));
    return localPdfDictionary;
  }

  public PdfDictionary getPageNRelease(int paramInt)
  {
    PdfDictionary localPdfDictionary = getPageN(paramInt);
    this.pageRefs.releasePage(paramInt);
    return localPdfDictionary;
  }

  public void releasePage(int paramInt)
  {
    this.pageRefs.releasePage(paramInt);
  }

  public void resetReleasePage()
  {
    this.pageRefs.resetReleasePage();
  }

  public PRIndirectReference getPageOrigRef(int paramInt)
  {
    return this.pageRefs.getPageOrigRef(paramInt);
  }

  public byte[] getPageContent(int paramInt, RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    PdfDictionary localPdfDictionary = getPageNRelease(paramInt);
    if (localPdfDictionary == null)
      return null;
    PdfObject localPdfObject1 = getPdfObjectRelease(localPdfDictionary.get(PdfName.CONTENTS));
    if (localPdfObject1 == null)
      return new byte[0];
    ByteArrayOutputStream localByteArrayOutputStream = null;
    if (localPdfObject1.isStream())
      return getStreamBytes((PRStream)localPdfObject1, paramRandomAccessFileOrArray);
    if (localPdfObject1.isArray())
    {
      PdfArray localPdfArray = (PdfArray)localPdfObject1;
      localByteArrayOutputStream = new ByteArrayOutputStream();
      for (int i = 0; i < localPdfArray.size(); i++)
      {
        PdfObject localPdfObject2 = getPdfObjectRelease(localPdfArray.getPdfObject(i));
        if ((localPdfObject2 == null) || (!localPdfObject2.isStream()))
          continue;
        byte[] arrayOfByte = getStreamBytes((PRStream)localPdfObject2, paramRandomAccessFileOrArray);
        localByteArrayOutputStream.write(arrayOfByte);
        if (i == localPdfArray.size() - 1)
          continue;
        localByteArrayOutputStream.write(10);
      }
      return localByteArrayOutputStream.toByteArray();
    }
    return new byte[0];
  }

  public byte[] getPageContent(int paramInt)
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = getSafeFile();
    try
    {
      localRandomAccessFileOrArray.reOpen();
      byte[] arrayOfByte = getPageContent(paramInt, localRandomAccessFileOrArray);
      return arrayOfByte;
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException2)
      {
      }
    }
    throw localObject;
  }

  protected void killXref(PdfObject paramPdfObject)
  {
    if (paramPdfObject == null)
      return;
    if (((paramPdfObject instanceof PdfIndirectReference)) && (!paramPdfObject.isIndirect()))
      return;
    Object localObject;
    int j;
    switch (paramPdfObject.type())
    {
    case 10:
      int i = ((PRIndirectReference)paramPdfObject).getNumber();
      paramPdfObject = (PdfObject)this.xrefObj.get(i);
      this.xrefObj.set(i, null);
      this.freeXref = i;
      killXref(paramPdfObject);
      break;
    case 5:
      localObject = (PdfArray)paramPdfObject;
      j = 0;
    case 6:
    case 7:
      while (j < ((PdfArray)localObject).size())
      {
        killXref(((PdfArray)localObject).getPdfObject(j));
        j++;
        continue;
        localObject = (PdfDictionary)paramPdfObject;
        Iterator localIterator = ((PdfDictionary)localObject).getKeys().iterator();
        while (localIterator.hasNext())
          killXref(((PdfDictionary)localObject).get((PdfName)localIterator.next()));
      }
    case 8:
    case 9:
    }
  }

  public void setPageContent(int paramInt, byte[] paramArrayOfByte)
  {
    setPageContent(paramInt, paramArrayOfByte, -1);
  }

  public void setPageContent(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    PdfDictionary localPdfDictionary = getPageN(paramInt1);
    if (localPdfDictionary == null)
      return;
    PdfObject localPdfObject = localPdfDictionary.get(PdfName.CONTENTS);
    this.freeXref = -1;
    killXref(localPdfObject);
    if (this.freeXref == -1)
    {
      this.xrefObj.add(null);
      this.freeXref = (this.xrefObj.size() - 1);
    }
    localPdfDictionary.put(PdfName.CONTENTS, new PRIndirectReference(this, this.freeXref));
    this.xrefObj.set(this.freeXref, new PRStream(this, paramArrayOfByte, paramInt2));
  }

  public static byte[] getStreamBytes(PRStream paramPRStream, RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    PdfObject localPdfObject1 = getPdfObjectRelease(paramPRStream.get(PdfName.FILTER));
    byte[] arrayOfByte = getStreamBytesRaw(paramPRStream, paramRandomAccessFileOrArray);
    ArrayList localArrayList1 = new ArrayList();
    if (localPdfObject1 != null)
      if (localPdfObject1.isName())
        localArrayList1.add(localPdfObject1);
      else if (localPdfObject1.isArray())
        localArrayList1 = ((PdfArray)localPdfObject1).getArrayList();
    ArrayList localArrayList2 = new ArrayList();
    PdfObject localPdfObject2 = getPdfObjectRelease(paramPRStream.get(PdfName.DECODEPARMS));
    if ((localPdfObject2 == null) || ((!localPdfObject2.isDictionary()) && (!localPdfObject2.isArray())))
      localPdfObject2 = getPdfObjectRelease(paramPRStream.get(PdfName.DP));
    if (localPdfObject2 != null)
      if (localPdfObject2.isDictionary())
        localArrayList2.add(localPdfObject2);
      else if (localPdfObject2.isArray())
        localArrayList2 = ((PdfArray)localPdfObject2).getArrayList();
    for (int i = 0; i < localArrayList1.size(); i++)
    {
      String str = ((PdfName)getPdfObjectRelease((PdfObject)localArrayList1.get(i))).toString();
      PdfObject localPdfObject3;
      if ((str.equals("/FlateDecode")) || (str.equals("/Fl")))
      {
        arrayOfByte = FlateDecode(arrayOfByte);
        localPdfObject3 = null;
        if (i >= localArrayList2.size())
          continue;
        localPdfObject3 = (PdfObject)localArrayList2.get(i);
        arrayOfByte = decodePredictor(arrayOfByte, localPdfObject3);
      }
      else if ((str.equals("/ASCIIHexDecode")) || (str.equals("/AHx")))
      {
        arrayOfByte = ASCIIHexDecode(arrayOfByte);
      }
      else if ((str.equals("/ASCII85Decode")) || (str.equals("/A85")))
      {
        arrayOfByte = ASCII85Decode(arrayOfByte);
      }
      else if (str.equals("/LZWDecode"))
      {
        arrayOfByte = LZWDecode(arrayOfByte);
        localPdfObject3 = null;
        if (i >= localArrayList2.size())
          continue;
        localPdfObject3 = (PdfObject)localArrayList2.get(i);
        arrayOfByte = decodePredictor(arrayOfByte, localPdfObject3);
      }
      else
      {
        if (str.equals("/Crypt"))
          continue;
        throw new UnsupportedPdfException("The filter " + str + " is not supported.");
      }
    }
    return arrayOfByte;
  }

  public static byte[] getStreamBytes(PRStream paramPRStream)
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = paramPRStream.getReader().getSafeFile();
    try
    {
      localRandomAccessFileOrArray.reOpen();
      byte[] arrayOfByte = getStreamBytes(paramPRStream, localRandomAccessFileOrArray);
      return arrayOfByte;
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException2)
      {
      }
    }
    throw localObject;
  }

  public static byte[] getStreamBytesRaw(PRStream paramPRStream, RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    PdfReader localPdfReader = paramPRStream.getReader();
    byte[] arrayOfByte;
    if (paramPRStream.getOffset() < 0)
    {
      arrayOfByte = paramPRStream.getBytes();
    }
    else
    {
      arrayOfByte = new byte[paramPRStream.getLength()];
      paramRandomAccessFileOrArray.seek(paramPRStream.getOffset());
      paramRandomAccessFileOrArray.readFully(arrayOfByte);
      PdfEncryption localPdfEncryption = localPdfReader.getDecrypt();
      if (localPdfEncryption != null)
      {
        PdfObject localPdfObject1 = getPdfObjectRelease(paramPRStream.get(PdfName.FILTER));
        ArrayList localArrayList = new ArrayList();
        if (localPdfObject1 != null)
          if (localPdfObject1.isName())
            localArrayList.add(localPdfObject1);
          else if (localPdfObject1.isArray())
            localArrayList = ((PdfArray)localPdfObject1).getArrayList();
        int i = 0;
        for (int j = 0; j < localArrayList.size(); j++)
        {
          PdfObject localPdfObject2 = getPdfObjectRelease((PdfObject)localArrayList.get(j));
          if ((localPdfObject2 == null) || (!localPdfObject2.toString().equals("/Crypt")))
            continue;
          i = 1;
          break;
        }
        if (i == 0)
        {
          localPdfEncryption.setHashKey(paramPRStream.getObjNum(), paramPRStream.getObjGen());
          arrayOfByte = localPdfEncryption.decryptByteArray(arrayOfByte);
        }
      }
    }
    return arrayOfByte;
  }

  public static byte[] getStreamBytesRaw(PRStream paramPRStream)
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = paramPRStream.getReader().getSafeFile();
    try
    {
      localRandomAccessFileOrArray.reOpen();
      byte[] arrayOfByte = getStreamBytesRaw(paramPRStream, localRandomAccessFileOrArray);
      return arrayOfByte;
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException2)
      {
      }
    }
    throw localObject;
  }

  public void eliminateSharedStreams()
  {
    if (!this.sharedStreams)
      return;
    this.sharedStreams = false;
    if (this.pageRefs.size() == 1)
      return;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    IntHashtable localIntHashtable = new IntHashtable();
    Object localObject1;
    for (int i = 1; i <= this.pageRefs.size(); i++)
    {
      localObject1 = this.pageRefs.getPageN(i);
      if (localObject1 == null)
        continue;
      PdfObject localPdfObject = getPdfObject(((PdfDictionary)localObject1).get(PdfName.CONTENTS));
      if (localPdfObject == null)
        continue;
      Object localObject2;
      if (localPdfObject.isStream())
      {
        localObject2 = (PRIndirectReference)((PdfDictionary)localObject1).get(PdfName.CONTENTS);
        if (localIntHashtable.containsKey(((PRIndirectReference)localObject2).getNumber()))
        {
          localArrayList1.add(localObject2);
          localArrayList2.add(new PRStream((PRStream)localPdfObject, null));
        }
        else
        {
          localIntHashtable.put(((PRIndirectReference)localObject2).getNumber(), 1);
        }
      }
      else
      {
        if (!localPdfObject.isArray())
          continue;
        localObject2 = (PdfArray)localPdfObject;
        for (int j = 0; j < ((PdfArray)localObject2).size(); j++)
        {
          PRIndirectReference localPRIndirectReference = (PRIndirectReference)((PdfArray)localObject2).getPdfObject(j);
          if (localIntHashtable.containsKey(localPRIndirectReference.getNumber()))
          {
            localArrayList1.add(localPRIndirectReference);
            localArrayList2.add(new PRStream((PRStream)getPdfObject(localPRIndirectReference), null));
          }
          else
          {
            localIntHashtable.put(localPRIndirectReference.getNumber(), 1);
          }
        }
      }
    }
    if (localArrayList2.isEmpty())
      return;
    for (i = 0; i < localArrayList2.size(); i++)
    {
      this.xrefObj.add(localArrayList2.get(i));
      localObject1 = (PRIndirectReference)localArrayList1.get(i);
      ((PRIndirectReference)localObject1).setNumber(this.xrefObj.size() - 1, 0);
    }
  }

  public boolean isTampered()
  {
    return this.tampered;
  }

  public void setTampered(boolean paramBoolean)
  {
    this.tampered = paramBoolean;
    this.pageRefs.keepPages();
  }

  public byte[] getMetadata()
    throws IOException
  {
    PdfObject localPdfObject = getPdfObject(this.catalog.get(PdfName.METADATA));
    if (!(localPdfObject instanceof PRStream))
      return null;
    RandomAccessFileOrArray localRandomAccessFileOrArray = getSafeFile();
    byte[] arrayOfByte = null;
    try
    {
      localRandomAccessFileOrArray.reOpen();
      arrayOfByte = getStreamBytes((PRStream)localPdfObject, localRandomAccessFileOrArray);
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException2)
      {
      }
    }
    return arrayOfByte;
  }

  public int getLastXref()
  {
    return this.lastXref;
  }

  public int getXrefSize()
  {
    return this.xrefObj.size();
  }

  public int getEofPos()
  {
    return this.eofPos;
  }

  public char getPdfVersion()
  {
    return this.pdfVersion;
  }

  public boolean isEncrypted()
  {
    return this.encrypted;
  }

  public int getPermissions()
  {
    return this.pValue;
  }

  public boolean is128Key()
  {
    return this.rValue == 3;
  }

  public PdfDictionary getTrailer()
  {
    return this.trailer;
  }

  PdfEncryption getDecrypt()
  {
    return this.decrypt;
  }

  static boolean equalsn(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i = paramArrayOfByte2.length;
    for (int j = 0; j < i; j++)
      if (paramArrayOfByte1[j] != paramArrayOfByte2[j])
        return false;
    return true;
  }

  static boolean existsName(PdfDictionary paramPdfDictionary, PdfName paramPdfName1, PdfName paramPdfName2)
  {
    PdfObject localPdfObject = getPdfObjectRelease(paramPdfDictionary.get(paramPdfName1));
    if ((localPdfObject == null) || (!localPdfObject.isName()))
      return false;
    PdfName localPdfName = (PdfName)localPdfObject;
    return localPdfName.equals(paramPdfName2);
  }

  static String getFontName(PdfDictionary paramPdfDictionary)
  {
    if (paramPdfDictionary == null)
      return null;
    PdfObject localPdfObject = getPdfObjectRelease(paramPdfDictionary.get(PdfName.BASEFONT));
    if ((localPdfObject == null) || (!localPdfObject.isName()))
      return null;
    return PdfName.decodeName(localPdfObject.toString());
  }

  static String getSubsetPrefix(PdfDictionary paramPdfDictionary)
  {
    if (paramPdfDictionary == null)
      return null;
    String str = getFontName(paramPdfDictionary);
    if (str == null)
      return null;
    if ((str.length() < 8) || (str.charAt(6) != '+'))
      return null;
    for (int i = 0; i < 6; i++)
    {
      int j = str.charAt(i);
      if ((j < 65) || (j > 90))
        return null;
    }
    return str;
  }

  public int shuffleSubsetNames()
  {
    int i = 0;
    for (int j = 1; j < this.xrefObj.size(); j++)
    {
      PdfObject localPdfObject = getPdfObjectRelease(j);
      if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
        continue;
      PdfDictionary localPdfDictionary1 = (PdfDictionary)localPdfObject;
      if (!existsName(localPdfDictionary1, PdfName.TYPE, PdfName.FONT))
        continue;
      String str1;
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if ((existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.TYPE1)) || (existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.MMTYPE1)) || (existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.TRUETYPE)))
      {
        str1 = getSubsetPrefix(localPdfDictionary1);
        if (str1 == null)
          continue;
        localObject1 = BaseFont.createSubsetPrefix() + str1.substring(7);
        localObject2 = new PdfName((String)localObject1);
        localPdfDictionary1.put(PdfName.BASEFONT, (PdfObject)localObject2);
        setXrefPartialObject(j, localPdfDictionary1);
        i++;
        localObject3 = localPdfDictionary1.getAsDict(PdfName.FONTDESCRIPTOR);
        if (localObject3 == null)
          continue;
        ((PdfDictionary)localObject3).put(PdfName.FONTNAME, (PdfObject)localObject2);
      }
      else
      {
        if (!existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.TYPE0))
          continue;
        str1 = getSubsetPrefix(localPdfDictionary1);
        localObject1 = localPdfDictionary1.getAsArray(PdfName.DESCENDANTFONTS);
        if ((localObject1 == null) || (((PdfArray)localObject1).isEmpty()))
          continue;
        localObject2 = ((PdfArray)localObject1).getAsDict(0);
        localObject3 = getSubsetPrefix((PdfDictionary)localObject2);
        if (localObject3 == null)
          continue;
        String str2 = BaseFont.createSubsetPrefix();
        if (str1 != null)
          localPdfDictionary1.put(PdfName.BASEFONT, new PdfName(str2 + str1.substring(7)));
        setXrefPartialObject(j, localPdfDictionary1);
        PdfName localPdfName = new PdfName(str2 + ((String)localObject3).substring(7));
        ((PdfDictionary)localObject2).put(PdfName.BASEFONT, localPdfName);
        i++;
        PdfDictionary localPdfDictionary2 = ((PdfDictionary)localObject2).getAsDict(PdfName.FONTDESCRIPTOR);
        if (localPdfDictionary2 == null)
          continue;
        localPdfDictionary2.put(PdfName.FONTNAME, localPdfName);
      }
    }
    return i;
  }

  public int createFakeFontSubsets()
  {
    int i = 0;
    for (int j = 1; j < this.xrefObj.size(); j++)
    {
      PdfObject localPdfObject = getPdfObjectRelease(j);
      if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
        continue;
      PdfDictionary localPdfDictionary1 = (PdfDictionary)localPdfObject;
      if ((!existsName(localPdfDictionary1, PdfName.TYPE, PdfName.FONT)) || ((!existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.TYPE1)) && (!existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.MMTYPE1)) && (!existsName(localPdfDictionary1, PdfName.SUBTYPE, PdfName.TRUETYPE))))
        continue;
      String str1 = getSubsetPrefix(localPdfDictionary1);
      if (str1 != null)
        continue;
      str1 = getFontName(localPdfDictionary1);
      if (str1 == null)
        continue;
      String str2 = BaseFont.createSubsetPrefix() + str1;
      PdfDictionary localPdfDictionary2 = (PdfDictionary)getPdfObjectRelease(localPdfDictionary1.get(PdfName.FONTDESCRIPTOR));
      if ((localPdfDictionary2 == null) || ((localPdfDictionary2.get(PdfName.FONTFILE) == null) && (localPdfDictionary2.get(PdfName.FONTFILE2) == null) && (localPdfDictionary2.get(PdfName.FONTFILE3) == null)))
        continue;
      localPdfDictionary2 = localPdfDictionary1.getAsDict(PdfName.FONTDESCRIPTOR);
      PdfName localPdfName = new PdfName(str2);
      localPdfDictionary1.put(PdfName.BASEFONT, localPdfName);
      localPdfDictionary2.put(PdfName.FONTNAME, localPdfName);
      setXrefPartialObject(j, localPdfDictionary1);
      i++;
    }
    return i;
  }

  private static PdfArray getNameArray(PdfObject paramPdfObject)
  {
    if (paramPdfObject == null)
      return null;
    paramPdfObject = getPdfObjectRelease(paramPdfObject);
    if (paramPdfObject == null)
      return null;
    if (paramPdfObject.isArray())
      return (PdfArray)paramPdfObject;
    if (paramPdfObject.isDictionary())
    {
      PdfObject localPdfObject = getPdfObjectRelease(((PdfDictionary)paramPdfObject).get(PdfName.D));
      if ((localPdfObject != null) && (localPdfObject.isArray()))
        return (PdfArray)localPdfObject;
    }
    return null;
  }

  public HashMap getNamedDestination()
  {
    return getNamedDestination(false);
  }

  public HashMap getNamedDestination(boolean paramBoolean)
  {
    HashMap localHashMap = getNamedDestinationFromNames(paramBoolean);
    localHashMap.putAll(getNamedDestinationFromStrings());
    return localHashMap;
  }

  public HashMap getNamedDestinationFromNames()
  {
    return getNamedDestinationFromNames(false);
  }

  public HashMap getNamedDestinationFromNames(boolean paramBoolean)
  {
    HashMap localHashMap = new HashMap();
    if (this.catalog.get(PdfName.DESTS) != null)
    {
      PdfDictionary localPdfDictionary = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.DESTS));
      if (localPdfDictionary == null)
        return localHashMap;
      Set localSet = localPdfDictionary.getKeys();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        PdfName localPdfName = (PdfName)localIterator.next();
        PdfArray localPdfArray = getNameArray(localPdfDictionary.get(localPdfName));
        if (localPdfArray == null)
          continue;
        if (paramBoolean)
        {
          localHashMap.put(localPdfName, localPdfArray);
          continue;
        }
        String str = PdfName.decodeName(localPdfName.toString());
        localHashMap.put(str, localPdfArray);
      }
    }
    return localHashMap;
  }

  public HashMap getNamedDestinationFromStrings()
  {
    if (this.catalog.get(PdfName.NAMES) != null)
    {
      PdfDictionary localPdfDictionary = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
      if (localPdfDictionary != null)
      {
        localPdfDictionary = (PdfDictionary)getPdfObjectRelease(localPdfDictionary.get(PdfName.DESTS));
        if (localPdfDictionary != null)
        {
          HashMap localHashMap = PdfNameTree.readTree(localPdfDictionary);
          Iterator localIterator = localHashMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            PdfArray localPdfArray = getNameArray((PdfObject)localEntry.getValue());
            if (localPdfArray != null)
            {
              localEntry.setValue(localPdfArray);
              continue;
            }
            localIterator.remove();
          }
          return localHashMap;
        }
      }
    }
    return new HashMap();
  }

  private boolean replaceNamedDestination(PdfObject paramPdfObject, HashMap paramHashMap)
  {
    paramPdfObject = getPdfObject(paramPdfObject);
    int i = this.lastXrefPartial;
    releaseLastXrefPartial();
    if ((paramPdfObject != null) && (paramPdfObject.isDictionary()))
    {
      PdfObject localPdfObject1 = getPdfObjectRelease(((PdfDictionary)paramPdfObject).get(PdfName.DEST));
      Object localObject = null;
      if (localPdfObject1 != null)
      {
        if (localPdfObject1.isName())
          localObject = localPdfObject1;
        else if (localPdfObject1.isString())
          localObject = localPdfObject1.toString();
        PdfArray localPdfArray1 = (PdfArray)paramHashMap.get(localObject);
        if (localPdfArray1 != null)
        {
          ((PdfDictionary)paramPdfObject).put(PdfName.DEST, localPdfArray1);
          setXrefPartialObject(i, paramPdfObject);
          return true;
        }
      }
      else if ((localPdfObject1 = getPdfObject(((PdfDictionary)paramPdfObject).get(PdfName.A))) != null)
      {
        int j = this.lastXrefPartial;
        releaseLastXrefPartial();
        PdfDictionary localPdfDictionary = (PdfDictionary)localPdfObject1;
        PdfName localPdfName = (PdfName)getPdfObjectRelease(localPdfDictionary.get(PdfName.S));
        if (PdfName.GOTO.equals(localPdfName))
        {
          PdfObject localPdfObject2 = getPdfObjectRelease(localPdfDictionary.get(PdfName.D));
          if (localPdfObject2 != null)
            if (localPdfObject2.isName())
              localObject = localPdfObject2;
            else if (localPdfObject2.isString())
              localObject = localPdfObject2.toString();
          PdfArray localPdfArray2 = (PdfArray)paramHashMap.get(localObject);
          if (localPdfArray2 != null)
          {
            localPdfDictionary.put(PdfName.D, localPdfArray2);
            setXrefPartialObject(j, localPdfObject1);
            setXrefPartialObject(i, paramPdfObject);
            return true;
          }
        }
      }
    }
    return false;
  }

  public void removeFields()
  {
    this.pageRefs.resetReleasePage();
    for (int i = 1; i <= this.pageRefs.size(); i++)
    {
      PdfDictionary localPdfDictionary1 = this.pageRefs.getPageN(i);
      PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.ANNOTS);
      if (localPdfArray == null)
      {
        this.pageRefs.releasePage(i);
      }
      else
      {
        for (int j = 0; j < localPdfArray.size(); j++)
        {
          PdfObject localPdfObject = getPdfObjectRelease(localPdfArray.getPdfObject(j));
          if ((localPdfObject == null) || (!localPdfObject.isDictionary()))
            continue;
          PdfDictionary localPdfDictionary2 = (PdfDictionary)localPdfObject;
          if (!PdfName.WIDGET.equals(localPdfDictionary2.get(PdfName.SUBTYPE)))
            continue;
          localPdfArray.remove(j--);
        }
        if (localPdfArray.isEmpty())
          localPdfDictionary1.remove(PdfName.ANNOTS);
        else
          this.pageRefs.releasePage(i);
      }
    }
    this.catalog.remove(PdfName.ACROFORM);
    this.pageRefs.resetReleasePage();
  }

  public void removeAnnotations()
  {
    this.pageRefs.resetReleasePage();
    for (int i = 1; i <= this.pageRefs.size(); i++)
    {
      PdfDictionary localPdfDictionary = this.pageRefs.getPageN(i);
      if (localPdfDictionary.get(PdfName.ANNOTS) == null)
        this.pageRefs.releasePage(i);
      else
        localPdfDictionary.remove(PdfName.ANNOTS);
    }
    this.catalog.remove(PdfName.ACROFORM);
    this.pageRefs.resetReleasePage();
  }

  public ArrayList getLinks(int paramInt)
  {
    this.pageRefs.resetReleasePage();
    ArrayList localArrayList = new ArrayList();
    PdfDictionary localPdfDictionary1 = this.pageRefs.getPageN(paramInt);
    if (localPdfDictionary1.get(PdfName.ANNOTS) != null)
    {
      PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.ANNOTS);
      for (int i = 0; i < localPdfArray.size(); i++)
      {
        PdfDictionary localPdfDictionary2 = (PdfDictionary)getPdfObjectRelease(localPdfArray.getPdfObject(i));
        if (!PdfName.LINK.equals(localPdfDictionary2.get(PdfName.SUBTYPE)))
          continue;
        localArrayList.add(new PdfAnnotation.PdfImportedLink(localPdfDictionary2));
      }
    }
    this.pageRefs.releasePage(paramInt);
    this.pageRefs.resetReleasePage();
    return localArrayList;
  }

  private void iterateBookmarks(PdfObject paramPdfObject, HashMap paramHashMap)
  {
    while (paramPdfObject != null)
    {
      replaceNamedDestination(paramPdfObject, paramHashMap);
      PdfDictionary localPdfDictionary = (PdfDictionary)getPdfObjectRelease(paramPdfObject);
      PdfObject localPdfObject = localPdfDictionary.get(PdfName.FIRST);
      if (localPdfObject != null)
        iterateBookmarks(localPdfObject, paramHashMap);
      paramPdfObject = localPdfDictionary.get(PdfName.NEXT);
    }
  }

  public void consolidateNamedDestinations()
  {
    if (this.consolidateNamedDestinations)
      return;
    this.consolidateNamedDestinations = true;
    HashMap localHashMap = getNamedDestination(true);
    if (localHashMap.isEmpty())
      return;
    for (int i = 1; i <= this.pageRefs.size(); i++)
    {
      PdfDictionary localPdfDictionary2 = this.pageRefs.getPageN(i);
      PdfObject localPdfObject1;
      PdfArray localPdfArray = (PdfArray)getPdfObject(localPdfObject1 = localPdfDictionary2.get(PdfName.ANNOTS));
      int j = this.lastXrefPartial;
      releaseLastXrefPartial();
      if (localPdfArray == null)
      {
        this.pageRefs.releasePage(i);
      }
      else
      {
        int k = 0;
        for (int m = 0; m < localPdfArray.size(); m++)
        {
          PdfObject localPdfObject2 = localPdfArray.getPdfObject(m);
          if ((!replaceNamedDestination(localPdfObject2, localHashMap)) || (localPdfObject2.isIndirect()))
            continue;
          k = 1;
        }
        if (k != 0)
          setXrefPartialObject(j, localPdfArray);
        if ((k != 0) && (!localPdfObject1.isIndirect()))
          continue;
        this.pageRefs.releasePage(i);
      }
    }
    PdfDictionary localPdfDictionary1 = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.OUTLINES));
    if (localPdfDictionary1 == null)
      return;
    iterateBookmarks(localPdfDictionary1.get(PdfName.FIRST), localHashMap);
  }

  protected static PdfDictionary duplicatePdfDictionary(PdfDictionary paramPdfDictionary1, PdfDictionary paramPdfDictionary2, PdfReader paramPdfReader)
  {
    if (paramPdfDictionary2 == null)
      paramPdfDictionary2 = new PdfDictionary();
    Iterator localIterator = paramPdfDictionary1.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      paramPdfDictionary2.put(localPdfName, duplicatePdfObject(paramPdfDictionary1.get(localPdfName), paramPdfReader));
    }
    return paramPdfDictionary2;
  }

  protected static PdfObject duplicatePdfObject(PdfObject paramPdfObject, PdfReader paramPdfReader)
  {
    if (paramPdfObject == null)
      return null;
    Object localObject1;
    Object localObject2;
    switch (paramPdfObject.type())
    {
    case 6:
      return duplicatePdfDictionary((PdfDictionary)paramPdfObject, null, paramPdfReader);
    case 7:
      localObject1 = (PRStream)paramPdfObject;
      localObject2 = new PRStream((PRStream)localObject1, null, paramPdfReader);
      duplicatePdfDictionary((PdfDictionary)localObject1, (PdfDictionary)localObject2, paramPdfReader);
      return localObject2;
    case 5:
      localObject1 = new PdfArray();
      localObject2 = ((PdfArray)paramPdfObject).listIterator();
      while (((Iterator)localObject2).hasNext())
        ((PdfArray)localObject1).add(duplicatePdfObject((PdfObject)((Iterator)localObject2).next(), paramPdfReader));
      return localObject1;
    case 10:
      localObject1 = (PRIndirectReference)paramPdfObject;
      return new PRIndirectReference(paramPdfReader, ((PRIndirectReference)localObject1).getNumber(), ((PRIndirectReference)localObject1).getGeneration());
    case 8:
    case 9:
    }
    return (PdfObject)(PdfObject)paramPdfObject;
  }

  public void close()
  {
    if (!this.partial)
      return;
    try
    {
      this.tokens.close();
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  protected void removeUnusedNode(PdfObject paramPdfObject, boolean[] paramArrayOfBoolean)
  {
    Stack localStack = new Stack();
    localStack.push(paramPdfObject);
    label430: label599: 
    while (!localStack.empty())
    {
      Object localObject1 = localStack.pop();
      if (localObject1 == null)
        continue;
      ArrayList localArrayList = null;
      PdfDictionary localPdfDictionary = null;
      PdfName[] arrayOfPdfName = null;
      Object[] arrayOfObject = null;
      PRIndirectReference localPRIndirectReference1 = 0;
      if ((localObject1 instanceof PdfObject))
      {
        paramPdfObject = (PdfObject)localObject1;
        switch (paramPdfObject.type())
        {
        case 6:
        case 7:
          localPdfDictionary = (PdfDictionary)paramPdfObject;
          arrayOfPdfName = new PdfName[localPdfDictionary.size()];
          localPdfDictionary.getKeys().toArray(arrayOfPdfName);
          break;
        case 5:
          localArrayList = ((PdfArray)paramPdfObject).getArrayList();
          break;
        case 10:
          localPRIndirectReference2 = (PRIndirectReference)paramPdfObject;
          int i = localPRIndirectReference2.getNumber();
          if (paramArrayOfBoolean[i] != 0)
            continue;
          paramArrayOfBoolean[i] = true;
          localStack.push(getPdfObjectRelease(localPRIndirectReference2));
          break;
        case 8:
        case 9:
        default:
          break;
        }
      }
      arrayOfObject = (Object[])localObject1;
      if ((arrayOfObject[0] instanceof ArrayList))
      {
        localArrayList = (ArrayList)arrayOfObject[0];
        localPRIndirectReference1 = ((Integer)arrayOfObject[1]).intValue();
      }
      else
      {
        arrayOfPdfName = (PdfName[])arrayOfObject[0];
        localPdfDictionary = (PdfDictionary)arrayOfObject[1];
        localPRIndirectReference1 = ((Integer)arrayOfObject[2]).intValue();
      }
      Object localObject2;
      if (localArrayList != null)
      {
        for (localPRIndirectReference2 = localPRIndirectReference1; ; localPRIndirectReference2++)
        {
          if (localPRIndirectReference2 >= localArrayList.size())
            break label430;
          localObject2 = (PdfObject)localArrayList.get(localPRIndirectReference2);
          if (((PdfObject)localObject2).isIndirect())
          {
            int j = ((PRIndirectReference)localObject2).getNumber();
            if ((j >= this.xrefObj.size()) || ((!this.partial) && (this.xrefObj.get(j) == null)))
            {
              localArrayList.set(localPRIndirectReference2, PdfNull.PDFNULL);
              continue;
            }
          }
          if (arrayOfObject == null)
          {
            localStack.push(new Object[] { localArrayList, new Integer(localPRIndirectReference2 + 1) });
          }
          else
          {
            arrayOfObject[1] = new Integer(localPRIndirectReference2 + 1);
            localStack.push(arrayOfObject);
          }
          localStack.push(localObject2);
          break;
        }
        continue;
      }
      for (PRIndirectReference localPRIndirectReference2 = localPRIndirectReference1; ; localPRIndirectReference2++)
      {
        if (localPRIndirectReference2 >= arrayOfPdfName.length)
          break label599;
        localObject2 = arrayOfPdfName[localPRIndirectReference2];
        PdfObject localPdfObject = localPdfDictionary.get((PdfName)localObject2);
        if (localPdfObject.isIndirect())
        {
          int k = ((PRIndirectReference)localPdfObject).getNumber();
          if ((k >= this.xrefObj.size()) || ((!this.partial) && (this.xrefObj.get(k) == null)))
          {
            localPdfDictionary.put((PdfName)localObject2, PdfNull.PDFNULL);
            continue;
          }
        }
        if (arrayOfObject == null)
        {
          localStack.push(new Object[] { arrayOfPdfName, localPdfDictionary, new Integer(localPRIndirectReference2 + 1) });
        }
        else
        {
          arrayOfObject[2] = new Integer(localPRIndirectReference2 + 1);
          localStack.push(arrayOfObject);
        }
        localStack.push(localPdfObject);
        break;
      }
    }
  }

  public int removeUnusedObjects()
  {
    boolean[] arrayOfBoolean = new boolean[this.xrefObj.size()];
    removeUnusedNode(this.trailer, arrayOfBoolean);
    int i = 0;
    if (this.partial)
      for (j = 1; j < arrayOfBoolean.length; j++)
      {
        if (arrayOfBoolean[j] != 0)
          continue;
        this.xref[(j * 2)] = -1;
        this.xref[(j * 2 + 1)] = 0;
        this.xrefObj.set(j, null);
        i++;
      }
    for (int j = 1; j < arrayOfBoolean.length; j++)
    {
      if (arrayOfBoolean[j] != 0)
        continue;
      this.xrefObj.set(j, null);
      i++;
    }
    return i;
  }

  public AcroFields getAcroFields()
  {
    return new AcroFields(this, null);
  }

  public String getJavaScript(RandomAccessFileOrArray paramRandomAccessFileOrArray)
    throws IOException
  {
    PdfDictionary localPdfDictionary1 = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
    if (localPdfDictionary1 == null)
      return null;
    PdfDictionary localPdfDictionary2 = (PdfDictionary)getPdfObjectRelease(localPdfDictionary1.get(PdfName.JAVASCRIPT));
    if (localPdfDictionary2 == null)
      return null;
    HashMap localHashMap = PdfNameTree.readTree(localPdfDictionary2);
    String[] arrayOfString = new String[localHashMap.size()];
    arrayOfString = (String[])localHashMap.keySet().toArray(arrayOfString);
    Arrays.sort(arrayOfString);
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      PdfDictionary localPdfDictionary3 = (PdfDictionary)getPdfObjectRelease((PdfIndirectReference)localHashMap.get(arrayOfString[i]));
      if (localPdfDictionary3 == null)
        continue;
      PdfObject localPdfObject = getPdfObjectRelease(localPdfDictionary3.get(PdfName.JS));
      if (localPdfObject == null)
        continue;
      if (localPdfObject.isString())
      {
        localStringBuffer.append(((PdfString)localPdfObject).toUnicodeString()).append('\n');
      }
      else
      {
        if (!localPdfObject.isStream())
          continue;
        byte[] arrayOfByte = getStreamBytes((PRStream)localPdfObject, paramRandomAccessFileOrArray);
        if ((arrayOfByte.length >= 2) && (arrayOfByte[0] == -2) && (arrayOfByte[1] == -1))
          localStringBuffer.append(PdfEncodings.convertToString(arrayOfByte, "UnicodeBig"));
        else
          localStringBuffer.append(PdfEncodings.convertToString(arrayOfByte, "PDF"));
        localStringBuffer.append('\n');
      }
    }
    return localStringBuffer.toString();
  }

  public String getJavaScript()
    throws IOException
  {
    RandomAccessFileOrArray localRandomAccessFileOrArray = getSafeFile();
    try
    {
      localRandomAccessFileOrArray.reOpen();
      String str = getJavaScript(localRandomAccessFileOrArray);
      return str;
    }
    finally
    {
      try
      {
        localRandomAccessFileOrArray.close();
      }
      catch (Exception localException2)
      {
      }
    }
    throw localObject;
  }

  public void selectPages(String paramString)
  {
    selectPages(SequenceList.expand(paramString, getNumberOfPages()));
  }

  public void selectPages(List paramList)
  {
    this.pageRefs.selectPages(paramList);
    removeUnusedObjects();
  }

  public void setViewerPreferences(int paramInt)
  {
    this.viewerPreferences.setViewerPreferences(paramInt);
    setViewerPreferences(this.viewerPreferences);
  }

  public void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    this.viewerPreferences.addViewerPreference(paramPdfName, paramPdfObject);
    setViewerPreferences(this.viewerPreferences);
  }

  void setViewerPreferences(PdfViewerPreferencesImp paramPdfViewerPreferencesImp)
  {
    paramPdfViewerPreferencesImp.addToCatalog(this.catalog);
  }

  public int getSimpleViewerPreferences()
  {
    return PdfViewerPreferencesImp.getViewerPreferences(this.catalog).getPageLayoutAndMode();
  }

  public boolean isAppendable()
  {
    return this.appendable;
  }

  public void setAppendable(boolean paramBoolean)
  {
    this.appendable = paramBoolean;
    if (paramBoolean)
      getPdfObject(this.trailer.get(PdfName.ROOT));
  }

  public boolean isNewXrefType()
  {
    return this.newXrefType;
  }

  public int getFileLength()
  {
    return this.fileLength;
  }

  public boolean isHybridXref()
  {
    return this.hybridXref;
  }

  PdfIndirectReference getCryptoRef()
  {
    if (this.cryptoRef == null)
      return null;
    return new PdfIndirectReference(0, this.cryptoRef.getNumber(), this.cryptoRef.getGeneration());
  }

  public void removeUsageRights()
  {
    PdfDictionary localPdfDictionary = this.catalog.getAsDict(PdfName.PERMS);
    if (localPdfDictionary == null)
      return;
    localPdfDictionary.remove(PdfName.UR);
    localPdfDictionary.remove(PdfName.UR3);
    if (localPdfDictionary.size() == 0)
      this.catalog.remove(PdfName.PERMS);
  }

  public int getCertificationLevel()
  {
    PdfDictionary localPdfDictionary = this.catalog.getAsDict(PdfName.PERMS);
    if (localPdfDictionary == null)
      return 0;
    localPdfDictionary = localPdfDictionary.getAsDict(PdfName.DOCMDP);
    if (localPdfDictionary == null)
      return 0;
    PdfArray localPdfArray = localPdfDictionary.getAsArray(PdfName.REFERENCE);
    if ((localPdfArray == null) || (localPdfArray.size() == 0))
      return 0;
    localPdfDictionary = localPdfArray.getAsDict(0);
    if (localPdfDictionary == null)
      return 0;
    localPdfDictionary = localPdfDictionary.getAsDict(PdfName.TRANSFORMPARAMS);
    if (localPdfDictionary == null)
      return 0;
    PdfNumber localPdfNumber = localPdfDictionary.getAsNumber(PdfName.P);
    if (localPdfNumber == null)
      return 0;
    return localPdfNumber.intValue();
  }

  public final boolean isOpenedWithFullPermissions()
  {
    return (!this.encrypted) || (this.ownerPasswordUsed);
  }

  public int getCryptoMode()
  {
    if (this.decrypt == null)
      return -1;
    return this.decrypt.getCryptoMode();
  }

  public boolean isMetadataEncrypted()
  {
    if (this.decrypt == null)
      return false;
    return this.decrypt.isMetadataEncrypted();
  }

  public byte[] computeUserPassword()
  {
    if ((!this.encrypted) || (!this.ownerPasswordUsed))
      return null;
    return this.decrypt.computeUserPassword(this.password);
  }

  static class PageRefs
  {
    private PdfReader reader;
    private IntHashtable refsp;
    private ArrayList refsn;
    private ArrayList pageInh;
    private int lastPageRead = -1;
    private int sizep;
    private boolean keepPages;

    private PageRefs(PdfReader paramPdfReader)
      throws IOException
    {
      this.reader = paramPdfReader;
      if (paramPdfReader.partial)
      {
        this.refsp = new IntHashtable();
        PdfNumber localPdfNumber = (PdfNumber)PdfReader.getPdfObjectRelease(paramPdfReader.rootPages.get(PdfName.COUNT));
        this.sizep = localPdfNumber.intValue();
      }
      else
      {
        readPages();
      }
    }

    PageRefs(PageRefs paramPageRefs, PdfReader paramPdfReader)
    {
      this.reader = paramPdfReader;
      this.sizep = paramPageRefs.sizep;
      if (paramPageRefs.refsn != null)
      {
        this.refsn = new ArrayList(paramPageRefs.refsn);
        for (int i = 0; i < this.refsn.size(); i++)
          this.refsn.set(i, PdfReader.duplicatePdfObject((PdfObject)this.refsn.get(i), paramPdfReader));
      }
      this.refsp = ((IntHashtable)paramPageRefs.refsp.clone());
    }

    int size()
    {
      if (this.refsn != null)
        return this.refsn.size();
      return this.sizep;
    }

    void readPages()
      throws IOException
    {
      if (this.refsn != null)
        return;
      this.refsp = null;
      this.refsn = new ArrayList();
      this.pageInh = new ArrayList();
      iteratePages((PRIndirectReference)this.reader.catalog.get(PdfName.PAGES));
      this.pageInh = null;
      this.reader.rootPages.put(PdfName.COUNT, new PdfNumber(this.refsn.size()));
    }

    void reReadPages()
      throws IOException
    {
      this.refsn = null;
      readPages();
    }

    public PdfDictionary getPageN(int paramInt)
    {
      PRIndirectReference localPRIndirectReference = getPageOrigRef(paramInt);
      return (PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference);
    }

    public PdfDictionary getPageNRelease(int paramInt)
    {
      PdfDictionary localPdfDictionary = getPageN(paramInt);
      releasePage(paramInt);
      return localPdfDictionary;
    }

    public PRIndirectReference getPageOrigRefRelease(int paramInt)
    {
      PRIndirectReference localPRIndirectReference = getPageOrigRef(paramInt);
      releasePage(paramInt);
      return localPRIndirectReference;
    }

    public PRIndirectReference getPageOrigRef(int paramInt)
    {
      try
      {
        paramInt--;
        if ((paramInt < 0) || (paramInt >= size()))
          return null;
        if (this.refsn != null)
          return (PRIndirectReference)this.refsn.get(paramInt);
        int i = this.refsp.get(paramInt);
        if (i == 0)
        {
          PRIndirectReference localPRIndirectReference = getSinglePage(paramInt);
          if (this.reader.lastXrefPartial == -1)
            this.lastPageRead = -1;
          else
            this.lastPageRead = paramInt;
          PdfReader.access$302(this.reader, -1);
          this.refsp.put(paramInt, localPRIndirectReference.getNumber());
          if (this.keepPages)
            this.lastPageRead = -1;
          return localPRIndirectReference;
        }
        if (this.lastPageRead != paramInt)
          this.lastPageRead = -1;
        if (this.keepPages)
          this.lastPageRead = -1;
        return new PRIndirectReference(this.reader, i);
      }
      catch (Exception localException)
      {
      }
      throw new ExceptionConverter(localException);
    }

    void keepPages()
    {
      if ((this.refsp == null) || (this.keepPages))
        return;
      this.keepPages = true;
      this.refsp.clear();
    }

    public void releasePage(int paramInt)
    {
      if (this.refsp == null)
        return;
      paramInt--;
      if ((paramInt < 0) || (paramInt >= size()))
        return;
      if (paramInt != this.lastPageRead)
        return;
      this.lastPageRead = -1;
      PdfReader.access$302(this.reader, this.refsp.get(paramInt));
      this.reader.releaseLastXrefPartial();
      this.refsp.remove(paramInt);
    }

    public void resetReleasePage()
    {
      if (this.refsp == null)
        return;
      this.lastPageRead = -1;
    }

    void insertPage(int paramInt, PRIndirectReference paramPRIndirectReference)
    {
      paramInt--;
      if (this.refsn != null)
      {
        if (paramInt >= this.refsn.size())
          this.refsn.add(paramPRIndirectReference);
        else
          this.refsn.add(paramInt, paramPRIndirectReference);
      }
      else
      {
        this.sizep += 1;
        this.lastPageRead = -1;
        if (paramInt >= size())
        {
          this.refsp.put(size(), paramPRIndirectReference.getNumber());
        }
        else
        {
          IntHashtable localIntHashtable = new IntHashtable((this.refsp.size() + 1) * 2);
          Iterator localIterator = this.refsp.getEntryIterator();
          while (localIterator.hasNext())
          {
            IntHashtable.Entry localEntry = (IntHashtable.Entry)localIterator.next();
            int i = localEntry.getKey();
            localIntHashtable.put(i >= paramInt ? i + 1 : i, localEntry.getValue());
          }
          localIntHashtable.put(paramInt, paramPRIndirectReference.getNumber());
          this.refsp = localIntHashtable;
        }
      }
    }

    private void pushPageAttributes(PdfDictionary paramPdfDictionary)
    {
      PdfDictionary localPdfDictionary = new PdfDictionary();
      if (!this.pageInh.isEmpty())
        localPdfDictionary.putAll((PdfDictionary)this.pageInh.get(this.pageInh.size() - 1));
      for (int i = 0; i < PdfReader.pageInhCandidates.length; i++)
      {
        PdfObject localPdfObject = paramPdfDictionary.get(PdfReader.pageInhCandidates[i]);
        if (localPdfObject == null)
          continue;
        localPdfDictionary.put(PdfReader.pageInhCandidates[i], localPdfObject);
      }
      this.pageInh.add(localPdfDictionary);
    }

    private void popPageAttributes()
    {
      this.pageInh.remove(this.pageInh.size() - 1);
    }

    private void iteratePages(PRIndirectReference paramPRIndirectReference)
      throws IOException
    {
      PdfDictionary localPdfDictionary1 = (PdfDictionary)PdfReader.getPdfObject(paramPRIndirectReference);
      PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.KIDS);
      Object localObject1;
      if (localPdfArray == null)
      {
        localPdfDictionary1.put(PdfName.TYPE, PdfName.PAGE);
        PdfDictionary localPdfDictionary2 = (PdfDictionary)this.pageInh.get(this.pageInh.size() - 1);
        Object localObject2 = localPdfDictionary2.getKeys().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject1 = (PdfName)((Iterator)localObject2).next();
          if (localPdfDictionary1.get((PdfName)localObject1) != null)
            continue;
          localPdfDictionary1.put((PdfName)localObject1, localPdfDictionary2.get((PdfName)localObject1));
        }
        if (localPdfDictionary1.get(PdfName.MEDIABOX) == null)
        {
          localObject2 = new PdfArray(new float[] { 0.0F, 0.0F, PageSize.LETTER.getRight(), PageSize.LETTER.getTop() });
          localPdfDictionary1.put(PdfName.MEDIABOX, (PdfObject)localObject2);
        }
        this.refsn.add(paramPRIndirectReference);
      }
      else
      {
        localPdfDictionary1.put(PdfName.TYPE, PdfName.PAGES);
        pushPageAttributes(localPdfDictionary1);
        for (int i = 0; i < localPdfArray.size(); i++)
        {
          localObject1 = localPdfArray.getPdfObject(i);
          if (!((PdfObject)localObject1).isIndirect())
            while (i < localPdfArray.size())
              localPdfArray.remove(i);
          iteratePages((PRIndirectReference)localObject1);
        }
        popPageAttributes();
      }
    }

    protected PRIndirectReference getSinglePage(int paramInt)
    {
      PdfDictionary localPdfDictionary1 = new PdfDictionary();
      Object localObject1 = this.reader.rootPages;
      int i = 0;
      label230: 
      while (true)
      {
        for (int j = 0; j < PdfReader.pageInhCandidates.length; j++)
        {
          localObject2 = ((PdfDictionary)localObject1).get(PdfReader.pageInhCandidates[j]);
          if (localObject2 == null)
            continue;
          localPdfDictionary1.put(PdfReader.pageInhCandidates[j], (PdfObject)localObject2);
        }
        PdfArray localPdfArray = (PdfArray)PdfReader.getPdfObjectRelease(((PdfDictionary)localObject1).get(PdfName.KIDS));
        Object localObject2 = localPdfArray.listIterator();
        while (true)
        {
          if (!((Iterator)localObject2).hasNext())
            break label230;
          PRIndirectReference localPRIndirectReference = (PRIndirectReference)((Iterator)localObject2).next();
          PdfDictionary localPdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference);
          int k = this.reader.lastXrefPartial;
          PdfObject localPdfObject = PdfReader.getPdfObjectRelease(localPdfDictionary2.get(PdfName.COUNT));
          PdfReader.access$302(this.reader, k);
          int m = 1;
          if ((localPdfObject != null) && (localPdfObject.type() == 2))
            m = ((PdfNumber)localPdfObject).intValue();
          if (paramInt < i + m)
          {
            if (localPdfObject == null)
            {
              localPdfDictionary2.mergeDifferent(localPdfDictionary1);
              return localPRIndirectReference;
            }
            this.reader.releaseLastXrefPartial();
            localObject1 = localPdfDictionary2;
            break;
          }
          this.reader.releaseLastXrefPartial();
          i += m;
        }
      }
    }

    private void selectPages(List paramList)
    {
      IntHashtable localIntHashtable = new IntHashtable();
      ArrayList localArrayList1 = new ArrayList();
      int i = size();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localObject = (Integer)localIterator.next();
        int k = ((Integer)localObject).intValue();
        if ((k < 1) || (k > i) || (localIntHashtable.put(k, 1) != 0))
          continue;
        localArrayList1.add(localObject);
      }
      if (this.reader.partial)
        for (int j = 1; j <= i; j++)
        {
          getPageOrigRef(j);
          resetReleasePage();
        }
      PRIndirectReference localPRIndirectReference1 = (PRIndirectReference)this.reader.catalog.get(PdfName.PAGES);
      Object localObject = (PdfDictionary)PdfReader.getPdfObject(localPRIndirectReference1);
      ArrayList localArrayList2 = new ArrayList(localArrayList1.size());
      PdfArray localPdfArray = new PdfArray();
      for (int m = 0; m < localArrayList1.size(); m++)
      {
        n = ((Integer)localArrayList1.get(m)).intValue();
        PRIndirectReference localPRIndirectReference2 = getPageOrigRef(n);
        resetReleasePage();
        localPdfArray.add(localPRIndirectReference2);
        localArrayList2.add(localPRIndirectReference2);
        getPageN(n).put(PdfName.PARENT, localPRIndirectReference1);
      }
      AcroFields localAcroFields = this.reader.getAcroFields();
      int n = localAcroFields.getFields().size() > 0 ? 1 : 0;
      for (int i1 = 1; i1 <= i; i1++)
      {
        if (localIntHashtable.containsKey(i1))
          continue;
        if (n != 0)
          localAcroFields.removeFieldsFromPage(i1);
        PRIndirectReference localPRIndirectReference3 = getPageOrigRef(i1);
        int i2 = localPRIndirectReference3.getNumber();
        this.reader.xrefObj.set(i2, null);
        if (!this.reader.partial)
          continue;
        this.reader.xref[(i2 * 2)] = -1;
        this.reader.xref[(i2 * 2 + 1)] = 0;
      }
      ((PdfDictionary)localObject).put(PdfName.COUNT, new PdfNumber(localArrayList1.size()));
      ((PdfDictionary)localObject).put(PdfName.KIDS, localPdfArray);
      this.refsp = null;
      this.refsn = localArrayList2;
    }

    PageRefs(PdfReader paramPdfReader, PdfReader.1 param1)
      throws IOException
    {
      this(paramPdfReader);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfReader
 * JD-Core Version:    0.6.0
 */