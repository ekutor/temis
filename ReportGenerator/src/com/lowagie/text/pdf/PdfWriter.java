package com.lowagie.text.pdf;

import com.lowagie.text.DocListener;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.ImgWMF;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.events.PdfPageEventForwarder;
import com.lowagie.text.pdf.interfaces.PdfAnnotations;
import com.lowagie.text.pdf.interfaces.PdfDocumentActions;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfPageActions;
import com.lowagie.text.pdf.interfaces.PdfRunDirection;
import com.lowagie.text.pdf.interfaces.PdfVersion;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.pdf.interfaces.PdfXConformance;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import com.lowagie.text.xml.xmp.XmpWriter;
import java.awt.Color;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PdfWriter extends DocWriter
  implements PdfViewerPreferences, PdfEncryptionSettings, PdfVersion, PdfDocumentActions, PdfPageActions, PdfXConformance, PdfRunDirection, PdfAnnotations
{
  public static final int GENERATION_MAX = 65535;
  protected PdfDocument pdf;
  protected PdfContentByte directContent;
  protected PdfContentByte directContentUnder;
  protected PdfBody body;
  protected PdfDictionary extraCatalog;
  protected PdfPages root = new PdfPages(this);
  protected ArrayList pageReferences = new ArrayList();
  protected int currentPageNumber = 1;
  protected PdfName tabs = null;
  private PdfPageEvent pageEvent;
  protected int prevxref = 0;
  protected List newBookmarks;
  public static final char VERSION_1_2 = '2';
  public static final char VERSION_1_3 = '3';
  public static final char VERSION_1_4 = '4';
  public static final char VERSION_1_5 = '5';
  public static final char VERSION_1_6 = '6';
  public static final char VERSION_1_7 = '7';
  public static final PdfName PDF_VERSION_1_2 = new PdfName("1.2");
  public static final PdfName PDF_VERSION_1_3 = new PdfName("1.3");
  public static final PdfName PDF_VERSION_1_4 = new PdfName("1.4");
  public static final PdfName PDF_VERSION_1_5 = new PdfName("1.5");
  public static final PdfName PDF_VERSION_1_6 = new PdfName("1.6");
  public static final PdfName PDF_VERSION_1_7 = new PdfName("1.7");
  protected PdfVersionImp pdf_version = new PdfVersionImp();
  public static final int PageLayoutSinglePage = 1;
  public static final int PageLayoutOneColumn = 2;
  public static final int PageLayoutTwoColumnLeft = 4;
  public static final int PageLayoutTwoColumnRight = 8;
  public static final int PageLayoutTwoPageLeft = 16;
  public static final int PageLayoutTwoPageRight = 32;
  public static final int PageModeUseNone = 64;
  public static final int PageModeUseOutlines = 128;
  public static final int PageModeUseThumbs = 256;
  public static final int PageModeFullScreen = 512;
  public static final int PageModeUseOC = 1024;
  public static final int PageModeUseAttachments = 2048;
  public static final int HideToolbar = 4096;
  public static final int HideMenubar = 8192;
  public static final int HideWindowUI = 16384;
  public static final int FitWindow = 32768;
  public static final int CenterWindow = 65536;
  public static final int DisplayDocTitle = 131072;
  public static final int NonFullScreenPageModeUseNone = 262144;
  public static final int NonFullScreenPageModeUseOutlines = 524288;
  public static final int NonFullScreenPageModeUseThumbs = 1048576;
  public static final int NonFullScreenPageModeUseOC = 2097152;
  public static final int DirectionL2R = 4194304;
  public static final int DirectionR2L = 8388608;
  public static final int PrintScalingNone = 16777216;
  public static final PdfName DOCUMENT_CLOSE = PdfName.WC;
  public static final PdfName WILL_SAVE = PdfName.WS;
  public static final PdfName DID_SAVE = PdfName.DS;
  public static final PdfName WILL_PRINT = PdfName.WP;
  public static final PdfName DID_PRINT = PdfName.DP;
  public static final int SIGNATURE_EXISTS = 1;
  public static final int SIGNATURE_APPEND_ONLY = 2;
  protected byte[] xmpMetadata = null;
  public static final int PDFXNONE = 0;
  public static final int PDFX1A2001 = 1;
  public static final int PDFX32002 = 2;
  public static final int PDFA1A = 3;
  public static final int PDFA1B = 4;
  private PdfXConformanceImp pdfxConformance = new PdfXConformanceImp();
  public static final int STANDARD_ENCRYPTION_40 = 0;
  public static final int STANDARD_ENCRYPTION_128 = 1;
  public static final int ENCRYPTION_AES_128 = 2;
  static final int ENCRYPTION_MASK = 7;
  public static final int DO_NOT_ENCRYPT_METADATA = 8;
  public static final int EMBEDDED_FILES_ONLY = 24;
  public static final int ALLOW_PRINTING = 2052;
  public static final int ALLOW_MODIFY_CONTENTS = 8;
  public static final int ALLOW_COPY = 16;
  public static final int ALLOW_MODIFY_ANNOTATIONS = 32;
  public static final int ALLOW_FILL_IN = 256;
  public static final int ALLOW_SCREENREADERS = 512;
  public static final int ALLOW_ASSEMBLY = 1024;
  public static final int ALLOW_DEGRADED_PRINTING = 4;

  /** @deprecated */
  public static final int AllowPrinting = 2052;

  /** @deprecated */
  public static final int AllowModifyContents = 8;

  /** @deprecated */
  public static final int AllowCopy = 16;

  /** @deprecated */
  public static final int AllowModifyAnnotations = 32;

  /** @deprecated */
  public static final int AllowFillIn = 256;

  /** @deprecated */
  public static final int AllowScreenReaders = 512;

  /** @deprecated */
  public static final int AllowAssembly = 1024;

  /** @deprecated */
  public static final int AllowDegradedPrinting = 4;

  /** @deprecated */
  public static final boolean STRENGTH40BITS = false;

  /** @deprecated */
  public static final boolean STRENGTH128BITS = true;
  protected PdfEncryption crypto;
  protected boolean fullCompression = false;
  protected int compressionLevel = -1;
  protected LinkedHashMap documentFonts = new LinkedHashMap();
  protected int fontNumber = 1;
  protected HashMap formXObjects = new HashMap();
  protected int formXObjectsCounter = 1;
  protected HashMap importedPages = new HashMap();
  protected PdfReaderInstance currentPdfReaderInstance;
  protected HashMap documentColors = new HashMap();
  protected int colorNumber = 1;
  protected HashMap documentPatterns = new HashMap();
  protected int patternNumber = 1;
  protected HashMap documentShadingPatterns = new HashMap();
  protected HashMap documentShadings = new HashMap();
  protected HashMap documentExtGState = new HashMap();
  protected HashMap documentProperties = new HashMap();
  protected boolean tagged = false;
  protected PdfStructureTreeRoot structureTreeRoot;
  protected HashSet documentOCG = new HashSet();
  protected ArrayList documentOCGorder = new ArrayList();
  protected PdfOCProperties OCProperties;
  protected PdfArray OCGRadioGroup = new PdfArray();
  protected PdfArray OCGLocked = new PdfArray();
  public static final PdfName PAGE_OPEN = PdfName.O;
  public static final PdfName PAGE_CLOSE = PdfName.C;
  protected PdfDictionary group;
  public static final float SPACE_CHAR_RATIO_DEFAULT = 2.5F;
  public static final float NO_SPACE_CHAR_RATIO = 10000000.0F;
  private float spaceCharRatio = 2.5F;
  public static final int RUN_DIRECTION_DEFAULT = 0;
  public static final int RUN_DIRECTION_NO_BIDI = 1;
  public static final int RUN_DIRECTION_LTR = 2;
  public static final int RUN_DIRECTION_RTL = 3;
  protected int runDirection = 1;
  protected float userunit = 0.0F;
  protected PdfDictionary defaultColorspace = new PdfDictionary();
  protected HashMap documentSpotPatterns = new HashMap();
  protected ColorDetails patternColorspaceRGB;
  protected ColorDetails patternColorspaceGRAY;
  protected ColorDetails patternColorspaceCMYK;
  protected PdfDictionary imageDictionary = new PdfDictionary();
  private HashMap images = new HashMap();
  protected HashMap JBIG2Globals = new HashMap();
  private boolean userProperties;
  private boolean rgbTransparencyBlending;

  protected PdfWriter()
  {
  }

  protected PdfWriter(PdfDocument paramPdfDocument, OutputStream paramOutputStream)
  {
    super(paramPdfDocument, paramOutputStream);
    this.pdf = paramPdfDocument;
    this.directContent = new PdfContentByte(this);
    this.directContentUnder = new PdfContentByte(this);
  }

  public static PdfWriter getInstance(Document paramDocument, OutputStream paramOutputStream)
    throws DocumentException
  {
    PdfDocument localPdfDocument = new PdfDocument();
    paramDocument.addDocListener(localPdfDocument);
    PdfWriter localPdfWriter = new PdfWriter(localPdfDocument, paramOutputStream);
    localPdfDocument.addWriter(localPdfWriter);
    return localPdfWriter;
  }

  public static PdfWriter getInstance(Document paramDocument, OutputStream paramOutputStream, DocListener paramDocListener)
    throws DocumentException
  {
    PdfDocument localPdfDocument = new PdfDocument();
    localPdfDocument.addDocListener(paramDocListener);
    paramDocument.addDocListener(localPdfDocument);
    PdfWriter localPdfWriter = new PdfWriter(localPdfDocument, paramOutputStream);
    localPdfDocument.addWriter(localPdfWriter);
    return localPdfWriter;
  }

  PdfDocument getPdfDocument()
  {
    return this.pdf;
  }

  public PdfDictionary getInfo()
  {
    return this.pdf.getInfo();
  }

  public float getVerticalPosition(boolean paramBoolean)
  {
    return this.pdf.getVerticalPosition(paramBoolean);
  }

  public void setInitialLeading(float paramFloat)
    throws DocumentException
  {
    if (this.open)
      throw new DocumentException("You can't set the initial leading if the document is already open.");
    this.pdf.setLeading(paramFloat);
  }

  public PdfContentByte getDirectContent()
  {
    if (!this.open)
      throw new RuntimeException("The document is not open.");
    return this.directContent;
  }

  public PdfContentByte getDirectContentUnder()
  {
    if (!this.open)
      throw new RuntimeException("The document is not open.");
    return this.directContentUnder;
  }

  void resetContent()
  {
    this.directContent.reset();
    this.directContentUnder.reset();
  }

  void addLocalDestinations(TreeMap paramTreeMap)
    throws IOException
  {
    Iterator localIterator = paramTreeMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      Object[] arrayOfObject = (Object[])localEntry.getValue();
      PdfDestination localPdfDestination = (PdfDestination)arrayOfObject[2];
      if (arrayOfObject[1] == null)
        arrayOfObject[1] = getPdfIndirectReference();
      if (localPdfDestination == null)
      {
        addToBody(new PdfString("invalid_" + str), (PdfIndirectReference)arrayOfObject[1]);
        continue;
      }
      addToBody(localPdfDestination, (PdfIndirectReference)arrayOfObject[1]);
    }
  }

  public PdfIndirectObject addToBody(PdfObject paramPdfObject)
    throws IOException
  {
    PdfIndirectObject localPdfIndirectObject = this.body.add(paramPdfObject);
    return localPdfIndirectObject;
  }

  public PdfIndirectObject addToBody(PdfObject paramPdfObject, boolean paramBoolean)
    throws IOException
  {
    PdfIndirectObject localPdfIndirectObject = this.body.add(paramPdfObject, paramBoolean);
    return localPdfIndirectObject;
  }

  public PdfIndirectObject addToBody(PdfObject paramPdfObject, PdfIndirectReference paramPdfIndirectReference)
    throws IOException
  {
    PdfIndirectObject localPdfIndirectObject = this.body.add(paramPdfObject, paramPdfIndirectReference);
    return localPdfIndirectObject;
  }

  public PdfIndirectObject addToBody(PdfObject paramPdfObject, PdfIndirectReference paramPdfIndirectReference, boolean paramBoolean)
    throws IOException
  {
    PdfIndirectObject localPdfIndirectObject = this.body.add(paramPdfObject, paramPdfIndirectReference, paramBoolean);
    return localPdfIndirectObject;
  }

  public PdfIndirectObject addToBody(PdfObject paramPdfObject, int paramInt)
    throws IOException
  {
    PdfIndirectObject localPdfIndirectObject = this.body.add(paramPdfObject, paramInt);
    return localPdfIndirectObject;
  }

  public PdfIndirectObject addToBody(PdfObject paramPdfObject, int paramInt, boolean paramBoolean)
    throws IOException
  {
    PdfIndirectObject localPdfIndirectObject = this.body.add(paramPdfObject, paramInt, paramBoolean);
    return localPdfIndirectObject;
  }

  public PdfIndirectReference getPdfIndirectReference()
  {
    return this.body.getPdfIndirectReference();
  }

  int getIndirectReferenceNumber()
  {
    return this.body.getIndirectReferenceNumber();
  }

  OutputStreamCounter getOs()
  {
    return this.os;
  }

  protected PdfDictionary getCatalog(PdfIndirectReference paramPdfIndirectReference)
  {
    PdfDocument.PdfCatalog localPdfCatalog = this.pdf.getCatalog(paramPdfIndirectReference);
    if (this.tagged)
    {
      try
      {
        getStructureTreeRoot().buildTree();
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
      localPdfCatalog.put(PdfName.STRUCTTREEROOT, this.structureTreeRoot.getReference());
      PdfDictionary localPdfDictionary = new PdfDictionary();
      localPdfDictionary.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
      if (this.userProperties)
        localPdfDictionary.put(PdfName.USERPROPERTIES, PdfBoolean.PDFTRUE);
      localPdfCatalog.put(PdfName.MARKINFO, localPdfDictionary);
    }
    if (!this.documentOCG.isEmpty())
    {
      fillOCProperties(false);
      localPdfCatalog.put(PdfName.OCPROPERTIES, this.OCProperties);
    }
    return localPdfCatalog;
  }

  public PdfDictionary getExtraCatalog()
  {
    if (this.extraCatalog == null)
      this.extraCatalog = new PdfDictionary();
    return this.extraCatalog;
  }

  public void setLinearPageMode()
  {
    this.root.setLinearMode(null);
  }

  public int reorderPages(int[] paramArrayOfInt)
    throws DocumentException
  {
    return this.root.reorderPages(paramArrayOfInt);
  }

  public PdfIndirectReference getPageReference(int paramInt)
  {
    paramInt--;
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("The page numbers start at 1.");
    PdfIndirectReference localPdfIndirectReference;
    if (paramInt < this.pageReferences.size())
    {
      localPdfIndirectReference = (PdfIndirectReference)this.pageReferences.get(paramInt);
      if (localPdfIndirectReference == null)
      {
        localPdfIndirectReference = this.body.getPdfIndirectReference();
        this.pageReferences.set(paramInt, localPdfIndirectReference);
      }
    }
    else
    {
      int i = paramInt - this.pageReferences.size();
      for (int j = 0; j < i; j++)
        this.pageReferences.add(null);
      localPdfIndirectReference = this.body.getPdfIndirectReference();
      this.pageReferences.add(localPdfIndirectReference);
    }
    return localPdfIndirectReference;
  }

  public int getPageNumber()
  {
    return this.pdf.getPageNumber();
  }

  PdfIndirectReference getCurrentPage()
  {
    return getPageReference(this.currentPageNumber);
  }

  public int getCurrentPageNumber()
  {
    return this.currentPageNumber;
  }

  public void setTabs(PdfName paramPdfName)
  {
    this.tabs = paramPdfName;
  }

  public PdfName getTabs()
  {
    return this.tabs;
  }

  PdfIndirectReference add(PdfPage paramPdfPage, PdfContents paramPdfContents)
    throws PdfException
  {
    if (!this.open)
      throw new PdfException("The document isn't open.");
    PdfIndirectObject localPdfIndirectObject;
    try
    {
      localPdfIndirectObject = addToBody(paramPdfContents);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    paramPdfPage.add(localPdfIndirectObject.getIndirectReference());
    if (this.group != null)
    {
      paramPdfPage.put(PdfName.GROUP, this.group);
      this.group = null;
    }
    else if (this.rgbTransparencyBlending)
    {
      PdfDictionary localPdfDictionary = new PdfDictionary();
      localPdfDictionary.put(PdfName.TYPE, PdfName.GROUP);
      localPdfDictionary.put(PdfName.S, PdfName.TRANSPARENCY);
      localPdfDictionary.put(PdfName.CS, PdfName.DEVICERGB);
      paramPdfPage.put(PdfName.GROUP, localPdfDictionary);
    }
    this.root.addPage(paramPdfPage);
    this.currentPageNumber += 1;
    return null;
  }

  public void setPageEvent(PdfPageEvent paramPdfPageEvent)
  {
    if (paramPdfPageEvent == null)
    {
      this.pageEvent = null;
    }
    else if (this.pageEvent == null)
    {
      this.pageEvent = paramPdfPageEvent;
    }
    else if ((this.pageEvent instanceof PdfPageEventForwarder))
    {
      ((PdfPageEventForwarder)this.pageEvent).addPageEvent(paramPdfPageEvent);
    }
    else
    {
      PdfPageEventForwarder localPdfPageEventForwarder = new PdfPageEventForwarder();
      localPdfPageEventForwarder.addPageEvent(this.pageEvent);
      localPdfPageEventForwarder.addPageEvent(paramPdfPageEvent);
      this.pageEvent = localPdfPageEventForwarder;
    }
  }

  public PdfPageEvent getPageEvent()
  {
    return this.pageEvent;
  }

  public void open()
  {
    super.open();
    try
    {
      this.pdf_version.writeHeader(this.os);
      this.body = new PdfBody(this);
      if (this.pdfxConformance.isPdfX32002())
      {
        PdfDictionary localPdfDictionary = new PdfDictionary();
        localPdfDictionary.put(PdfName.GAMMA, new PdfArray(new float[] { 2.2F, 2.2F, 2.2F }));
        localPdfDictionary.put(PdfName.MATRIX, new PdfArray(new float[] { 0.4124F, 0.2126F, 0.0193F, 0.3576F, 0.7152F, 0.1192F, 0.1805F, 0.0722F, 0.9505F }));
        localPdfDictionary.put(PdfName.WHITEPOINT, new PdfArray(new float[] { 0.9505F, 1.0F, 1.089F }));
        PdfArray localPdfArray = new PdfArray(PdfName.CALRGB);
        localPdfArray.add(localPdfDictionary);
        setDefaultColorspace(PdfName.DEFAULTRGB, addToBody(localPdfArray).getIndirectReference());
      }
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  public void close()
  {
    if (this.open)
    {
      if (this.currentPageNumber - 1 != this.pageReferences.size())
        throw new RuntimeException("The page " + this.pageReferences.size() + " was requested but the document has only " + (this.currentPageNumber - 1) + " pages.");
      this.pdf.close();
      try
      {
        addSharedObjectsToBody();
        PdfIndirectReference localPdfIndirectReference1 = this.root.writePageTree();
        PdfDictionary localPdfDictionary = getCatalog(localPdfIndirectReference1);
        if (this.xmpMetadata != null)
        {
          localObject1 = new PdfStream(this.xmpMetadata);
          ((PdfStream)localObject1).put(PdfName.TYPE, PdfName.METADATA);
          ((PdfStream)localObject1).put(PdfName.SUBTYPE, PdfName.XML);
          if ((this.crypto != null) && (!this.crypto.isMetadataEncrypted()))
          {
            localObject2 = new PdfArray();
            ((PdfArray)localObject2).add(PdfName.CRYPT);
            ((PdfStream)localObject1).put(PdfName.FILTER, (PdfObject)localObject2);
          }
          localPdfDictionary.put(PdfName.METADATA, this.body.add((PdfObject)localObject1).getIndirectReference());
        }
        if (isPdfX())
        {
          this.pdfxConformance.completeInfoDictionary(getInfo());
          this.pdfxConformance.completeExtraCatalog(getExtraCatalog());
        }
        if (this.extraCatalog != null)
          localPdfDictionary.mergeDifferent(this.extraCatalog);
        writeOutlines(localPdfDictionary, false);
        Object localObject1 = addToBody(localPdfDictionary, false);
        Object localObject2 = addToBody(getInfo(), false);
        PdfIndirectReference localPdfIndirectReference2 = null;
        PdfObject localPdfObject = null;
        this.body.flushObjStm();
        Object localObject3;
        if (this.crypto != null)
        {
          localObject3 = addToBody(this.crypto.getEncryptionDictionary(), false);
          localPdfIndirectReference2 = ((PdfIndirectObject)localObject3).getIndirectReference();
          localPdfObject = this.crypto.getFileID();
        }
        else
        {
          localPdfObject = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
        }
        this.body.writeCrossReferenceTable(this.os, ((PdfIndirectObject)localObject1).getIndirectReference(), ((PdfIndirectObject)localObject2).getIndirectReference(), localPdfIndirectReference2, localPdfObject, this.prevxref);
        if (this.fullCompression)
        {
          this.os.write(getISOBytes("startxref\n"));
          this.os.write(getISOBytes(String.valueOf(this.body.offset())));
          this.os.write(getISOBytes("\n%%EOF\n"));
        }
        else
        {
          localObject3 = new PdfTrailer(this.body.size(), this.body.offset(), ((PdfIndirectObject)localObject1).getIndirectReference(), ((PdfIndirectObject)localObject2).getIndirectReference(), localPdfIndirectReference2, localPdfObject, this.prevxref);
          ((PdfTrailer)localObject3).toPdf(this, this.os);
        }
        super.close();
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
    }
  }

  protected void addSharedObjectsToBody()
    throws IOException
  {
    Iterator localIterator = this.documentFonts.values().iterator();
    Object localObject1;
    while (localIterator.hasNext())
    {
      localObject1 = (FontDetails)localIterator.next();
      ((FontDetails)localObject1).writeFont(this);
    }
    localIterator = this.formXObjects.values().iterator();
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (Object[])localIterator.next();
      localObject2 = (PdfTemplate)localObject1[1];
      if (((localObject2 != null) && ((((PdfTemplate)localObject2).getIndirectReference() instanceof PRIndirectReference))) || (localObject2 == null) || (((PdfTemplate)localObject2).getType() != 1))
        continue;
      addToBody(((PdfTemplate)localObject2).getFormXObject(this.compressionLevel), ((PdfTemplate)localObject2).getIndirectReference());
    }
    localIterator = this.importedPages.values().iterator();
    while (localIterator.hasNext())
    {
      this.currentPdfReaderInstance = ((PdfReaderInstance)localIterator.next());
      this.currentPdfReaderInstance.writeAllPages();
    }
    this.currentPdfReaderInstance = null;
    localIterator = this.documentColors.values().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (ColorDetails)localIterator.next();
      addToBody(((ColorDetails)localObject1).getSpotColor(this), ((ColorDetails)localObject1).getIndirectReference());
    }
    localIterator = this.documentPatterns.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (PdfPatternPainter)localIterator.next();
      addToBody(((PdfPatternPainter)localObject1).getPattern(this.compressionLevel), ((PdfPatternPainter)localObject1).getIndirectReference());
    }
    localIterator = this.documentShadingPatterns.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (PdfShadingPattern)localIterator.next();
      ((PdfShadingPattern)localObject1).addToBody();
    }
    localIterator = this.documentShadings.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (PdfShading)localIterator.next();
      ((PdfShading)localObject1).addToBody();
    }
    localIterator = this.documentExtGState.entrySet().iterator();
    PdfObject[] arrayOfPdfObject;
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      localObject2 = (PdfDictionary)((Map.Entry)localObject1).getKey();
      arrayOfPdfObject = (PdfObject[])((Map.Entry)localObject1).getValue();
      addToBody((PdfObject)localObject2, (PdfIndirectReference)arrayOfPdfObject[1]);
    }
    localIterator = this.documentProperties.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      localObject2 = ((Map.Entry)localObject1).getKey();
      arrayOfPdfObject = (PdfObject[])((Map.Entry)localObject1).getValue();
      if ((localObject2 instanceof PdfLayerMembership))
      {
        PdfLayerMembership localPdfLayerMembership = (PdfLayerMembership)localObject2;
        addToBody(localPdfLayerMembership.getPdfObject(), localPdfLayerMembership.getRef());
        continue;
      }
      if ((!(localObject2 instanceof PdfDictionary)) || ((localObject2 instanceof PdfLayer)))
        continue;
      addToBody((PdfDictionary)localObject2, (PdfIndirectReference)arrayOfPdfObject[1]);
    }
    localIterator = this.documentOCG.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (PdfOCG)localIterator.next();
      addToBody(((PdfOCG)localObject1).getPdfObject(), ((PdfOCG)localObject1).getRef());
    }
  }

  public PdfOutline getRootOutline()
  {
    return this.directContent.getRootOutline();
  }

  public void setOutlines(List paramList)
  {
    this.newBookmarks = paramList;
  }

  protected void writeOutlines(PdfDictionary paramPdfDictionary, boolean paramBoolean)
    throws IOException
  {
    if ((this.newBookmarks == null) || (this.newBookmarks.isEmpty()))
      return;
    PdfDictionary localPdfDictionary = new PdfDictionary();
    PdfIndirectReference localPdfIndirectReference = getPdfIndirectReference();
    Object[] arrayOfObject = SimpleBookmark.iterateOutlines(this, localPdfIndirectReference, this.newBookmarks, paramBoolean);
    localPdfDictionary.put(PdfName.FIRST, (PdfIndirectReference)arrayOfObject[0]);
    localPdfDictionary.put(PdfName.LAST, (PdfIndirectReference)arrayOfObject[1]);
    localPdfDictionary.put(PdfName.COUNT, new PdfNumber(((Integer)arrayOfObject[2]).intValue()));
    addToBody(localPdfDictionary, localPdfIndirectReference);
    paramPdfDictionary.put(PdfName.OUTLINES, localPdfIndirectReference);
  }

  public void setPdfVersion(char paramChar)
  {
    this.pdf_version.setPdfVersion(paramChar);
  }

  public void setAtLeastPdfVersion(char paramChar)
  {
    this.pdf_version.setAtLeastPdfVersion(paramChar);
  }

  public void setPdfVersion(PdfName paramPdfName)
  {
    this.pdf_version.setPdfVersion(paramPdfName);
  }

  public void addDeveloperExtension(PdfDeveloperExtension paramPdfDeveloperExtension)
  {
    this.pdf_version.addDeveloperExtension(paramPdfDeveloperExtension);
  }

  PdfVersionImp getPdfVersion()
  {
    return this.pdf_version;
  }

  public void setViewerPreferences(int paramInt)
  {
    this.pdf.setViewerPreferences(paramInt);
  }

  public void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    this.pdf.addViewerPreference(paramPdfName, paramPdfObject);
  }

  public void setPageLabels(PdfPageLabels paramPdfPageLabels)
  {
    this.pdf.setPageLabels(paramPdfPageLabels);
  }

  public void addJavaScript(PdfAction paramPdfAction)
  {
    this.pdf.addJavaScript(paramPdfAction);
  }

  public void addJavaScript(String paramString, boolean paramBoolean)
  {
    addJavaScript(PdfAction.javaScript(paramString, this, paramBoolean));
  }

  public void addJavaScript(String paramString)
  {
    addJavaScript(paramString, false);
  }

  public void addJavaScript(String paramString, PdfAction paramPdfAction)
  {
    this.pdf.addJavaScript(paramString, paramPdfAction);
  }

  public void addJavaScript(String paramString1, String paramString2, boolean paramBoolean)
  {
    addJavaScript(paramString1, PdfAction.javaScript(paramString2, this, paramBoolean));
  }

  public void addJavaScript(String paramString1, String paramString2)
  {
    addJavaScript(paramString1, paramString2, false);
  }

  public void addFileAttachment(String paramString1, byte[] paramArrayOfByte, String paramString2, String paramString3)
    throws IOException
  {
    addFileAttachment(paramString1, PdfFileSpecification.fileEmbedded(this, paramString2, paramString3, paramArrayOfByte));
  }

  public void addFileAttachment(String paramString, PdfFileSpecification paramPdfFileSpecification)
    throws IOException
  {
    this.pdf.addFileAttachment(paramString, paramPdfFileSpecification);
  }

  public void addFileAttachment(PdfFileSpecification paramPdfFileSpecification)
    throws IOException
  {
    addFileAttachment(null, paramPdfFileSpecification);
  }

  public void setOpenAction(String paramString)
  {
    this.pdf.setOpenAction(paramString);
  }

  public void setOpenAction(PdfAction paramPdfAction)
  {
    this.pdf.setOpenAction(paramPdfAction);
  }

  public void setAdditionalAction(PdfName paramPdfName, PdfAction paramPdfAction)
    throws DocumentException
  {
    if ((!paramPdfName.equals(DOCUMENT_CLOSE)) && (!paramPdfName.equals(WILL_SAVE)) && (!paramPdfName.equals(DID_SAVE)) && (!paramPdfName.equals(WILL_PRINT)) && (!paramPdfName.equals(DID_PRINT)))
      throw new DocumentException("Invalid additional action type: " + paramPdfName.toString());
    this.pdf.addAdditionalAction(paramPdfName, paramPdfAction);
  }

  public void setCollection(PdfCollection paramPdfCollection)
  {
    setAtLeastPdfVersion('7');
    this.pdf.setCollection(paramPdfCollection);
  }

  public PdfAcroForm getAcroForm()
  {
    return this.pdf.getAcroForm();
  }

  public void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    this.pdf.addAnnotation(paramPdfAnnotation);
  }

  void addAnnotation(PdfAnnotation paramPdfAnnotation, int paramInt)
  {
    addAnnotation(paramPdfAnnotation);
  }

  public void addCalculationOrder(PdfFormField paramPdfFormField)
  {
    this.pdf.addCalculationOrder(paramPdfFormField);
  }

  public void setSigFlags(int paramInt)
  {
    this.pdf.setSigFlags(paramInt);
  }

  public void setXmpMetadata(byte[] paramArrayOfByte)
  {
    this.xmpMetadata = paramArrayOfByte;
  }

  public void setPageXmpMetadata(byte[] paramArrayOfByte)
  {
    this.pdf.setXmpMetadata(paramArrayOfByte);
  }

  public void createXmpMetadata()
  {
    setXmpMetadata(createXmpMetadataBytes());
  }

  private byte[] createXmpMetadataBytes()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      XmpWriter localXmpWriter = new XmpWriter(localByteArrayOutputStream, this.pdf.getInfo(), this.pdfxConformance.getPDFXConformance());
      localXmpWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return localByteArrayOutputStream.toByteArray();
  }

  public void setPDFXConformance(int paramInt)
  {
    if (this.pdfxConformance.getPDFXConformance() == paramInt)
      return;
    if (this.pdf.isOpen())
      throw new PdfXConformanceException("PDFX conformance can only be set before opening the document.");
    if (this.crypto != null)
      throw new PdfXConformanceException("A PDFX conforming document cannot be encrypted.");
    if ((paramInt == 3) || (paramInt == 4))
      setPdfVersion('4');
    else if (paramInt != 0)
      setPdfVersion('3');
    this.pdfxConformance.setPDFXConformance(paramInt);
  }

  public int getPDFXConformance()
  {
    return this.pdfxConformance.getPDFXConformance();
  }

  public boolean isPdfX()
  {
    return this.pdfxConformance.isPdfX();
  }

  public void setOutputIntents(String paramString1, String paramString2, String paramString3, String paramString4, ICC_Profile paramICC_Profile)
    throws IOException
  {
    getExtraCatalog();
    PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.OUTPUTINTENT);
    if (paramString2 != null)
      localPdfDictionary.put(PdfName.OUTPUTCONDITION, new PdfString(paramString2, "UnicodeBig"));
    if (paramString1 != null)
      localPdfDictionary.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(paramString1, "UnicodeBig"));
    if (paramString3 != null)
      localPdfDictionary.put(PdfName.REGISTRYNAME, new PdfString(paramString3, "UnicodeBig"));
    if (paramString4 != null)
      localPdfDictionary.put(PdfName.INFO, new PdfString(paramString4, "UnicodeBig"));
    Object localObject;
    if (paramICC_Profile != null)
    {
      localObject = new PdfICCBased(paramICC_Profile, this.compressionLevel);
      localPdfDictionary.put(PdfName.DESTOUTPUTPROFILE, addToBody((PdfObject)localObject).getIndirectReference());
    }
    if ((this.pdfxConformance.isPdfA1()) || ("PDFA/1".equals(paramString2)))
      localObject = PdfName.GTS_PDFA1;
    else
      localObject = PdfName.GTS_PDFX;
    localPdfDictionary.put(PdfName.S, (PdfObject)localObject);
    this.extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(localPdfDictionary));
  }

  public void setOutputIntents(String paramString1, String paramString2, String paramString3, String paramString4, byte[] paramArrayOfByte)
    throws IOException
  {
    ICC_Profile localICC_Profile = paramArrayOfByte == null ? null : ICC_Profile.getInstance(paramArrayOfByte);
    setOutputIntents(paramString1, paramString2, paramString3, paramString4, localICC_Profile);
  }

  public boolean setOutputIntents(PdfReader paramPdfReader, boolean paramBoolean)
    throws IOException
  {
    PdfDictionary localPdfDictionary1 = paramPdfReader.getCatalog();
    PdfArray localPdfArray = localPdfDictionary1.getAsArray(PdfName.OUTPUTINTENTS);
    if (localPdfArray == null)
      return false;
    if (localPdfArray.isEmpty())
      return false;
    PdfDictionary localPdfDictionary2 = localPdfArray.getAsDict(0);
    PdfObject localPdfObject = PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.S));
    if ((localPdfObject == null) || (!PdfName.GTS_PDFX.equals(localPdfObject)))
      return false;
    if (paramBoolean)
      return true;
    PRStream localPRStream = (PRStream)PdfReader.getPdfObject(localPdfDictionary2.get(PdfName.DESTOUTPUTPROFILE));
    byte[] arrayOfByte = null;
    if (localPRStream != null)
      arrayOfByte = PdfReader.getStreamBytes(localPRStream);
    setOutputIntents(getNameString(localPdfDictionary2, PdfName.OUTPUTCONDITIONIDENTIFIER), getNameString(localPdfDictionary2, PdfName.OUTPUTCONDITION), getNameString(localPdfDictionary2, PdfName.REGISTRYNAME), getNameString(localPdfDictionary2, PdfName.INFO), arrayOfByte);
    return true;
  }

  private static String getNameString(PdfDictionary paramPdfDictionary, PdfName paramPdfName)
  {
    PdfObject localPdfObject = PdfReader.getPdfObject(paramPdfDictionary.get(paramPdfName));
    if ((localPdfObject == null) || (!localPdfObject.isString()))
      return null;
    return ((PdfString)localPdfObject).toUnicodeString();
  }

  PdfEncryption getEncryption()
  {
    return this.crypto;
  }

  public void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
    throws DocumentException
  {
    if (this.pdf.isOpen())
      throw new DocumentException("Encryption can only be added before opening the document.");
    this.crypto = new PdfEncryption();
    this.crypto.setCryptoMode(paramInt2, 0);
    this.crypto.setupAllKeys(paramArrayOfByte1, paramArrayOfByte2, paramInt1);
  }

  public void setEncryption(Certificate[] paramArrayOfCertificate, int[] paramArrayOfInt, int paramInt)
    throws DocumentException
  {
    if (this.pdf.isOpen())
      throw new DocumentException("Encryption can only be added before opening the document.");
    this.crypto = new PdfEncryption();
    if (paramArrayOfCertificate != null)
      for (int i = 0; i < paramArrayOfCertificate.length; i++)
        this.crypto.addRecipient(paramArrayOfCertificate[i], paramArrayOfInt[i]);
    this.crypto.setCryptoMode(paramInt, 0);
    this.crypto.getEncryptionDictionary();
  }

  /** @deprecated */
  public void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, boolean paramBoolean)
    throws DocumentException
  {
    setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt, paramBoolean ? 1 : 0);
  }

  /** @deprecated */
  public void setEncryption(boolean paramBoolean, String paramString1, String paramString2, int paramInt)
    throws DocumentException
  {
    setEncryption(getISOBytes(paramString1), getISOBytes(paramString2), paramInt, paramBoolean ? 1 : 0);
  }

  /** @deprecated */
  public void setEncryption(int paramInt1, String paramString1, String paramString2, int paramInt2)
    throws DocumentException
  {
    setEncryption(getISOBytes(paramString1), getISOBytes(paramString2), paramInt2, paramInt1);
  }

  public boolean isFullCompression()
  {
    return this.fullCompression;
  }

  public void setFullCompression()
  {
    this.fullCompression = true;
    setAtLeastPdfVersion('5');
  }

  public int getCompressionLevel()
  {
    return this.compressionLevel;
  }

  public void setCompressionLevel(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 9))
      this.compressionLevel = -1;
    else
      this.compressionLevel = paramInt;
  }

  FontDetails addSimple(BaseFont paramBaseFont)
  {
    if (paramBaseFont.getFontType() == 4)
      return new FontDetails(new PdfName("F" + this.fontNumber++), ((DocumentFont)paramBaseFont).getIndirectReference(), paramBaseFont);
    FontDetails localFontDetails = (FontDetails)this.documentFonts.get(paramBaseFont);
    if (localFontDetails == null)
    {
      PdfXConformanceImp.checkPDFXConformance(this, 4, paramBaseFont);
      localFontDetails = new FontDetails(new PdfName("F" + this.fontNumber++), this.body.getPdfIndirectReference(), paramBaseFont);
      this.documentFonts.put(paramBaseFont, localFontDetails);
    }
    return localFontDetails;
  }

  void eliminateFontSubset(PdfDictionary paramPdfDictionary)
  {
    Iterator localIterator = this.documentFonts.values().iterator();
    while (localIterator.hasNext())
    {
      FontDetails localFontDetails = (FontDetails)localIterator.next();
      if (paramPdfDictionary.get(localFontDetails.getFontName()) == null)
        continue;
      localFontDetails.setSubset(false);
    }
  }

  PdfName addDirectTemplateSimple(PdfTemplate paramPdfTemplate, PdfName paramPdfName)
  {
    PdfIndirectReference localPdfIndirectReference = paramPdfTemplate.getIndirectReference();
    Object[] arrayOfObject = (Object[])this.formXObjects.get(localPdfIndirectReference);
    PdfName localPdfName = null;
    try
    {
      if (arrayOfObject == null)
      {
        if (paramPdfName == null)
        {
          localPdfName = new PdfName("Xf" + this.formXObjectsCounter);
          this.formXObjectsCounter += 1;
        }
        else
        {
          localPdfName = paramPdfName;
        }
        if (paramPdfTemplate.getType() == 2)
        {
          PdfImportedPage localPdfImportedPage = (PdfImportedPage)paramPdfTemplate;
          PdfReader localPdfReader = localPdfImportedPage.getPdfReaderInstance().getReader();
          if (!this.importedPages.containsKey(localPdfReader))
            this.importedPages.put(localPdfReader, localPdfImportedPage.getPdfReaderInstance());
          paramPdfTemplate = null;
        }
        this.formXObjects.put(localPdfIndirectReference, new Object[] { localPdfName, paramPdfTemplate });
      }
      else
      {
        localPdfName = (PdfName)arrayOfObject[0];
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    return localPdfName;
  }

  public void releaseTemplate(PdfTemplate paramPdfTemplate)
    throws IOException
  {
    PdfIndirectReference localPdfIndirectReference = paramPdfTemplate.getIndirectReference();
    Object[] arrayOfObject = (Object[])this.formXObjects.get(localPdfIndirectReference);
    if ((arrayOfObject == null) || (arrayOfObject[1] == null))
      return;
    PdfTemplate localPdfTemplate = (PdfTemplate)arrayOfObject[1];
    if ((localPdfTemplate.getIndirectReference() instanceof PRIndirectReference))
      return;
    if (localPdfTemplate.getType() == 1)
    {
      addToBody(localPdfTemplate.getFormXObject(this.compressionLevel), localPdfTemplate.getIndirectReference());
      arrayOfObject[1] = null;
    }
  }

  public PdfImportedPage getImportedPage(PdfReader paramPdfReader, int paramInt)
  {
    PdfReaderInstance localPdfReaderInstance = (PdfReaderInstance)this.importedPages.get(paramPdfReader);
    if (localPdfReaderInstance == null)
    {
      localPdfReaderInstance = paramPdfReader.getPdfReaderInstance(this);
      this.importedPages.put(paramPdfReader, localPdfReaderInstance);
    }
    return localPdfReaderInstance.getImportedPage(paramInt);
  }

  public void freeReader(PdfReader paramPdfReader)
    throws IOException
  {
    this.currentPdfReaderInstance = ((PdfReaderInstance)this.importedPages.get(paramPdfReader));
    if (this.currentPdfReaderInstance == null)
      return;
    this.currentPdfReaderInstance.writeAllPages();
    this.currentPdfReaderInstance = null;
    this.importedPages.remove(paramPdfReader);
  }

  public int getCurrentDocumentSize()
  {
    return this.body.offset() + this.body.size() * 20 + 72;
  }

  protected int getNewObjectNumber(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    return this.currentPdfReaderInstance.getNewObjectNumber(paramInt1, paramInt2);
  }

  RandomAccessFileOrArray getReaderFile(PdfReader paramPdfReader)
  {
    return this.currentPdfReaderInstance.getReaderFile();
  }

  PdfName getColorspaceName()
  {
    return new PdfName("CS" + this.colorNumber++);
  }

  ColorDetails addSimple(PdfSpotColor paramPdfSpotColor)
  {
    ColorDetails localColorDetails = (ColorDetails)this.documentColors.get(paramPdfSpotColor);
    if (localColorDetails == null)
    {
      localColorDetails = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), paramPdfSpotColor);
      this.documentColors.put(paramPdfSpotColor, localColorDetails);
    }
    return localColorDetails;
  }

  PdfName addSimplePattern(PdfPatternPainter paramPdfPatternPainter)
  {
    PdfName localPdfName = (PdfName)this.documentPatterns.get(paramPdfPatternPainter);
    try
    {
      if (localPdfName == null)
      {
        localPdfName = new PdfName("P" + this.patternNumber);
        this.patternNumber += 1;
        this.documentPatterns.put(paramPdfPatternPainter, localPdfName);
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    return localPdfName;
  }

  void addSimpleShadingPattern(PdfShadingPattern paramPdfShadingPattern)
  {
    if (!this.documentShadingPatterns.containsKey(paramPdfShadingPattern))
    {
      paramPdfShadingPattern.setName(this.patternNumber);
      this.patternNumber += 1;
      this.documentShadingPatterns.put(paramPdfShadingPattern, null);
      addSimpleShading(paramPdfShadingPattern.getShading());
    }
  }

  void addSimpleShading(PdfShading paramPdfShading)
  {
    if (!this.documentShadings.containsKey(paramPdfShading))
    {
      this.documentShadings.put(paramPdfShading, null);
      paramPdfShading.setName(this.documentShadings.size());
    }
  }

  PdfObject[] addSimpleExtGState(PdfDictionary paramPdfDictionary)
  {
    if (!this.documentExtGState.containsKey(paramPdfDictionary))
    {
      PdfXConformanceImp.checkPDFXConformance(this, 6, paramPdfDictionary);
      this.documentExtGState.put(paramPdfDictionary, new PdfObject[] { new PdfName("GS" + (this.documentExtGState.size() + 1)), getPdfIndirectReference() });
    }
    return (PdfObject[])this.documentExtGState.get(paramPdfDictionary);
  }

  PdfObject[] addSimpleProperty(Object paramObject, PdfIndirectReference paramPdfIndirectReference)
  {
    if (!this.documentProperties.containsKey(paramObject))
    {
      if ((paramObject instanceof PdfOCG))
        PdfXConformanceImp.checkPDFXConformance(this, 7, null);
      this.documentProperties.put(paramObject, new PdfObject[] { new PdfName("Pr" + (this.documentProperties.size() + 1)), paramPdfIndirectReference });
    }
    return (PdfObject[])this.documentProperties.get(paramObject);
  }

  boolean propertyExists(Object paramObject)
  {
    return this.documentProperties.containsKey(paramObject);
  }

  public void setTagged()
  {
    if (this.open)
      throw new IllegalArgumentException("Tagging must be set before opening the document.");
    this.tagged = true;
  }

  public boolean isTagged()
  {
    return this.tagged;
  }

  public PdfStructureTreeRoot getStructureTreeRoot()
  {
    if ((this.tagged) && (this.structureTreeRoot == null))
      this.structureTreeRoot = new PdfStructureTreeRoot(this);
    return this.structureTreeRoot;
  }

  public PdfOCProperties getOCProperties()
  {
    fillOCProperties(true);
    return this.OCProperties;
  }

  public void addOCGRadioGroup(ArrayList paramArrayList)
  {
    PdfArray localPdfArray = new PdfArray();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      PdfLayer localPdfLayer = (PdfLayer)paramArrayList.get(i);
      if (localPdfLayer.getTitle() != null)
        continue;
      localPdfArray.add(localPdfLayer.getRef());
    }
    if (localPdfArray.size() == 0)
      return;
    this.OCGRadioGroup.add(localPdfArray);
  }

  public void lockLayer(PdfLayer paramPdfLayer)
  {
    this.OCGLocked.add(paramPdfLayer.getRef());
  }

  private static void getOCGOrder(PdfArray paramPdfArray, PdfLayer paramPdfLayer)
  {
    if (!paramPdfLayer.isOnPanel())
      return;
    if (paramPdfLayer.getTitle() == null)
      paramPdfArray.add(paramPdfLayer.getRef());
    ArrayList localArrayList = paramPdfLayer.getChildren();
    if (localArrayList == null)
      return;
    PdfArray localPdfArray = new PdfArray();
    if (paramPdfLayer.getTitle() != null)
      localPdfArray.add(new PdfString(paramPdfLayer.getTitle(), "UnicodeBig"));
    for (int i = 0; i < localArrayList.size(); i++)
      getOCGOrder(localPdfArray, (PdfLayer)localArrayList.get(i));
    if (localPdfArray.size() > 0)
      paramPdfArray.add(localPdfArray);
  }

  private void addASEvent(PdfName paramPdfName1, PdfName paramPdfName2)
  {
    PdfArray localPdfArray = new PdfArray();
    Object localObject1 = this.documentOCG.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PdfLayer)((Iterator)localObject1).next();
      localPdfDictionary = (PdfDictionary)((PdfLayer)localObject2).get(PdfName.USAGE);
      if ((localPdfDictionary == null) || (localPdfDictionary.get(paramPdfName2) == null))
        continue;
      localPdfArray.add(((PdfLayer)localObject2).getRef());
    }
    if (localPdfArray.size() == 0)
      return;
    localObject1 = (PdfDictionary)this.OCProperties.get(PdfName.D);
    Object localObject2 = (PdfArray)((PdfDictionary)localObject1).get(PdfName.AS);
    if (localObject2 == null)
    {
      localObject2 = new PdfArray();
      ((PdfDictionary)localObject1).put(PdfName.AS, (PdfObject)localObject2);
    }
    PdfDictionary localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(PdfName.EVENT, paramPdfName1);
    localPdfDictionary.put(PdfName.CATEGORY, new PdfArray(paramPdfName2));
    localPdfDictionary.put(PdfName.OCGS, localPdfArray);
    ((PdfArray)localObject2).add(localPdfDictionary);
  }

  protected void fillOCProperties(boolean paramBoolean)
  {
    if (this.OCProperties == null)
      this.OCProperties = new PdfOCProperties();
    if (paramBoolean)
    {
      this.OCProperties.remove(PdfName.OCGS);
      this.OCProperties.remove(PdfName.D);
    }
    if (this.OCProperties.get(PdfName.OCGS) == null)
    {
      localObject1 = new PdfArray();
      localObject2 = this.documentOCG.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (PdfLayer)((Iterator)localObject2).next();
        ((PdfArray)localObject1).add(((PdfLayer)localObject3).getRef());
      }
      this.OCProperties.put(PdfName.OCGS, (PdfObject)localObject1);
    }
    if (this.OCProperties.get(PdfName.D) != null)
      return;
    Object localObject1 = new ArrayList(this.documentOCGorder);
    Object localObject2 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (PdfLayer)((Iterator)localObject2).next();
      if (((PdfLayer)localObject3).getParent() == null)
        continue;
      ((Iterator)localObject2).remove();
    }
    localObject2 = new PdfArray();
    Object localObject3 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (PdfLayer)((Iterator)localObject3).next();
      getOCGOrder((PdfArray)localObject2, (PdfLayer)localObject4);
    }
    localObject3 = new PdfDictionary();
    this.OCProperties.put(PdfName.D, (PdfObject)localObject3);
    ((PdfDictionary)localObject3).put(PdfName.ORDER, (PdfObject)localObject2);
    Object localObject4 = new PdfArray();
    Iterator localIterator = this.documentOCG.iterator();
    while (localIterator.hasNext())
    {
      PdfLayer localPdfLayer = (PdfLayer)localIterator.next();
      if (localPdfLayer.isOn())
        continue;
      ((PdfArray)localObject4).add(localPdfLayer.getRef());
    }
    if (((PdfArray)localObject4).size() > 0)
      ((PdfDictionary)localObject3).put(PdfName.OFF, (PdfObject)localObject4);
    if (this.OCGRadioGroup.size() > 0)
      ((PdfDictionary)localObject3).put(PdfName.RBGROUPS, this.OCGRadioGroup);
    if (this.OCGLocked.size() > 0)
      ((PdfDictionary)localObject3).put(PdfName.LOCKED, this.OCGLocked);
    addASEvent(PdfName.VIEW, PdfName.ZOOM);
    addASEvent(PdfName.VIEW, PdfName.VIEW);
    addASEvent(PdfName.PRINT, PdfName.PRINT);
    addASEvent(PdfName.EXPORT, PdfName.EXPORT);
    ((PdfDictionary)localObject3).put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
  }

  void registerLayer(PdfOCG paramPdfOCG)
  {
    PdfXConformanceImp.checkPDFXConformance(this, 7, null);
    if ((paramPdfOCG instanceof PdfLayer))
    {
      PdfLayer localPdfLayer = (PdfLayer)paramPdfOCG;
      if (localPdfLayer.getTitle() == null)
      {
        if (!this.documentOCG.contains(paramPdfOCG))
        {
          this.documentOCG.add(paramPdfOCG);
          this.documentOCGorder.add(paramPdfOCG);
        }
      }
      else
        this.documentOCGorder.add(paramPdfOCG);
    }
    else
    {
      throw new IllegalArgumentException("Only PdfLayer is accepted.");
    }
  }

  public Rectangle getPageSize()
  {
    return this.pdf.getPageSize();
  }

  public void setCropBoxSize(Rectangle paramRectangle)
  {
    this.pdf.setCropBoxSize(paramRectangle);
  }

  public void setBoxSize(String paramString, Rectangle paramRectangle)
  {
    this.pdf.setBoxSize(paramString, paramRectangle);
  }

  public Rectangle getBoxSize(String paramString)
  {
    return this.pdf.getBoxSize(paramString);
  }

  public void setPageEmpty(boolean paramBoolean)
  {
    this.pdf.setPageEmpty(paramBoolean);
  }

  public void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction)
    throws DocumentException
  {
    if ((!paramPdfName.equals(PAGE_OPEN)) && (!paramPdfName.equals(PAGE_CLOSE)))
      throw new DocumentException("Invalid page additional action type: " + paramPdfName.toString());
    this.pdf.setPageAction(paramPdfName, paramPdfAction);
  }

  public void setDuration(int paramInt)
  {
    this.pdf.setDuration(paramInt);
  }

  public void setTransition(PdfTransition paramPdfTransition)
  {
    this.pdf.setTransition(paramPdfTransition);
  }

  public void setThumbnail(Image paramImage)
    throws PdfException, DocumentException
  {
    this.pdf.setThumbnail(paramImage);
  }

  public PdfDictionary getGroup()
  {
    return this.group;
  }

  public void setGroup(PdfDictionary paramPdfDictionary)
  {
    this.group = paramPdfDictionary;
  }

  public float getSpaceCharRatio()
  {
    return this.spaceCharRatio;
  }

  public void setSpaceCharRatio(float paramFloat)
  {
    if (paramFloat < 0.001F)
      this.spaceCharRatio = 0.001F;
    else
      this.spaceCharRatio = paramFloat;
  }

  public void setRunDirection(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 3))
      throw new RuntimeException("Invalid run direction: " + paramInt);
    this.runDirection = paramInt;
  }

  public int getRunDirection()
  {
    return this.runDirection;
  }

  public float getUserunit()
  {
    return this.userunit;
  }

  public void setUserunit(float paramFloat)
    throws DocumentException
  {
    if ((paramFloat < 1.0F) || (paramFloat > 75000.0F))
      throw new DocumentException("UserUnit should be a value between 1 and 75000.");
    this.userunit = paramFloat;
    setAtLeastPdfVersion('6');
  }

  public PdfDictionary getDefaultColorspace()
  {
    return this.defaultColorspace;
  }

  public void setDefaultColorspace(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    if ((paramPdfObject == null) || (paramPdfObject.isNull()))
      this.defaultColorspace.remove(paramPdfName);
    this.defaultColorspace.put(paramPdfName, paramPdfObject);
  }

  ColorDetails addSimplePatternColorspace(Color paramColor)
  {
    int i = ExtendedColor.getType(paramColor);
    if ((i == 4) || (i == 5))
      throw new RuntimeException("An uncolored tile pattern can not have another pattern or shading as color.");
    try
    {
      Object localObject;
      switch (i)
      {
      case 0:
        if (this.patternColorspaceRGB == null)
        {
          this.patternColorspaceRGB = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
          localObject = new PdfArray(PdfName.PATTERN);
          ((PdfArray)localObject).add(PdfName.DEVICERGB);
          addToBody((PdfObject)localObject, this.patternColorspaceRGB.getIndirectReference());
        }
        return this.patternColorspaceRGB;
      case 2:
        if (this.patternColorspaceCMYK == null)
        {
          this.patternColorspaceCMYK = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
          localObject = new PdfArray(PdfName.PATTERN);
          ((PdfArray)localObject).add(PdfName.DEVICECMYK);
          addToBody((PdfObject)localObject, this.patternColorspaceCMYK.getIndirectReference());
        }
        return this.patternColorspaceCMYK;
      case 1:
        if (this.patternColorspaceGRAY == null)
        {
          this.patternColorspaceGRAY = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
          localObject = new PdfArray(PdfName.PATTERN);
          ((PdfArray)localObject).add(PdfName.DEVICEGRAY);
          addToBody((PdfObject)localObject, this.patternColorspaceGRAY.getIndirectReference());
        }
        return this.patternColorspaceGRAY;
      case 3:
        localObject = addSimple(((SpotColor)paramColor).getPdfSpotColor());
        ColorDetails localColorDetails = (ColorDetails)this.documentSpotPatterns.get(localObject);
        if (localColorDetails == null)
        {
          localColorDetails = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
          PdfArray localPdfArray = new PdfArray(PdfName.PATTERN);
          localPdfArray.add(((ColorDetails)localObject).getIndirectReference());
          addToBody(localPdfArray, localColorDetails.getIndirectReference());
          this.documentSpotPatterns.put(localObject, localColorDetails);
        }
        return localColorDetails;
      }
      throw new RuntimeException("Invalid color type in PdfWriter.addSimplePatternColorspace().");
    }
    catch (Exception localException)
    {
    }
    throw new RuntimeException(localException.getMessage());
  }

  public boolean isStrictImageSequence()
  {
    return this.pdf.isStrictImageSequence();
  }

  public void setStrictImageSequence(boolean paramBoolean)
  {
    this.pdf.setStrictImageSequence(paramBoolean);
  }

  public void clearTextWrap()
    throws DocumentException
  {
    this.pdf.clearTextWrap();
  }

  public PdfName addDirectImageSimple(Image paramImage)
    throws PdfException, DocumentException
  {
    return addDirectImageSimple(paramImage, null);
  }

  public PdfName addDirectImageSimple(Image paramImage, PdfIndirectReference paramPdfIndirectReference)
    throws PdfException, DocumentException
  {
    PdfName localPdfName;
    if (this.images.containsKey(paramImage.getMySerialId()))
    {
      localPdfName = (PdfName)this.images.get(paramImage.getMySerialId());
    }
    else
    {
      if (paramImage.isImgTemplate())
      {
        localPdfName = new PdfName("img" + this.images.size());
        if ((paramImage instanceof ImgWMF))
          try
          {
            ImgWMF localImgWMF = (ImgWMF)paramImage;
            localImgWMF.readWMF(PdfTemplate.createTemplate(this, 0.0F, 0.0F));
          }
          catch (Exception localException)
          {
            throw new DocumentException(localException);
          }
      }
      else
      {
        PdfIndirectReference localPdfIndirectReference1 = paramImage.getDirectReference();
        if (localPdfIndirectReference1 != null)
        {
          localObject1 = new PdfName("img" + this.images.size());
          this.images.put(paramImage.getMySerialId(), localObject1);
          this.imageDictionary.put((PdfName)localObject1, localPdfIndirectReference1);
          return localObject1;
        }
        Object localObject1 = paramImage.getImageMask();
        PdfIndirectReference localPdfIndirectReference2 = null;
        if (localObject1 != null)
        {
          localObject2 = (PdfName)this.images.get(((Image)localObject1).getMySerialId());
          localPdfIndirectReference2 = getImageReference((PdfName)localObject2);
        }
        Object localObject2 = new PdfImage(paramImage, "img" + this.images.size(), localPdfIndirectReference2);
        Object localObject3;
        Object localObject4;
        if ((paramImage instanceof ImgJBIG2))
        {
          localObject3 = ((ImgJBIG2)paramImage).getGlobalBytes();
          if (localObject3 != null)
          {
            localObject4 = new PdfDictionary();
            ((PdfDictionary)localObject4).put(PdfName.JBIG2GLOBALS, getReferenceJBIG2Globals(localObject3));
            ((PdfImage)localObject2).put(PdfName.DECODEPARMS, (PdfObject)localObject4);
          }
        }
        if (paramImage.hasICCProfile())
        {
          localObject3 = new PdfICCBased(paramImage.getICCProfile(), paramImage.getCompressionLevel());
          localObject4 = add((PdfICCBased)localObject3);
          PdfArray localPdfArray1 = new PdfArray();
          localPdfArray1.add(PdfName.ICCBASED);
          localPdfArray1.add((PdfObject)localObject4);
          PdfArray localPdfArray2 = ((PdfImage)localObject2).getAsArray(PdfName.COLORSPACE);
          if (localPdfArray2 != null)
          {
            if ((localPdfArray2.size() > 1) && (PdfName.INDEXED.equals(localPdfArray2.getPdfObject(0))))
              localPdfArray2.set(1, localPdfArray1);
            else
              ((PdfImage)localObject2).put(PdfName.COLORSPACE, localPdfArray1);
          }
          else
            ((PdfImage)localObject2).put(PdfName.COLORSPACE, localPdfArray1);
        }
        add((PdfImage)localObject2, paramPdfIndirectReference);
        localPdfName = ((PdfImage)localObject2).name();
      }
      this.images.put(paramImage.getMySerialId(), localPdfName);
    }
    return (PdfName)(PdfName)(PdfName)(PdfName)localPdfName;
  }

  PdfIndirectReference add(PdfImage paramPdfImage, PdfIndirectReference paramPdfIndirectReference)
    throws PdfException
  {
    if (!this.imageDictionary.contains(paramPdfImage.name()))
    {
      PdfXConformanceImp.checkPDFXConformance(this, 5, paramPdfImage);
      if ((paramPdfIndirectReference instanceof PRIndirectReference))
      {
        PRIndirectReference localPRIndirectReference = (PRIndirectReference)paramPdfIndirectReference;
        paramPdfIndirectReference = new PdfIndirectReference(0, getNewObjectNumber(localPRIndirectReference.getReader(), localPRIndirectReference.getNumber(), localPRIndirectReference.getGeneration()));
      }
      try
      {
        if (paramPdfIndirectReference == null)
          paramPdfIndirectReference = addToBody(paramPdfImage).getIndirectReference();
        else
          addToBody(paramPdfImage, paramPdfIndirectReference);
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
      this.imageDictionary.put(paramPdfImage.name(), paramPdfIndirectReference);
      return paramPdfIndirectReference;
    }
    return (PdfIndirectReference)this.imageDictionary.get(paramPdfImage.name());
  }

  PdfIndirectReference getImageReference(PdfName paramPdfName)
  {
    return (PdfIndirectReference)this.imageDictionary.get(paramPdfName);
  }

  protected PdfIndirectReference add(PdfICCBased paramPdfICCBased)
  {
    PdfIndirectObject localPdfIndirectObject;
    try
    {
      localPdfIndirectObject = addToBody(paramPdfICCBased);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    return localPdfIndirectObject.getIndirectReference();
  }

  protected PdfIndirectReference getReferenceJBIG2Globals(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      return null;
    Object localObject = this.JBIG2Globals.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      localPdfStream = (PdfStream)((Iterator)localObject).next();
      if (Arrays.equals(paramArrayOfByte, localPdfStream.getBytes()))
        return (PdfIndirectReference)this.JBIG2Globals.get(localPdfStream);
    }
    PdfStream localPdfStream = new PdfStream(paramArrayOfByte);
    try
    {
      localObject = addToBody(localPdfStream);
    }
    catch (IOException localIOException)
    {
      return null;
    }
    this.JBIG2Globals.put(localPdfStream, ((PdfIndirectObject)localObject).getIndirectReference());
    return (PdfIndirectReference)((PdfIndirectObject)localObject).getIndirectReference();
  }

  public boolean fitsPage(Table paramTable, float paramFloat)
  {
    return this.pdf.bottom(paramTable) > this.pdf.indentBottom() + paramFloat;
  }

  public boolean fitsPage(Table paramTable)
  {
    return fitsPage(paramTable, 0.0F);
  }

  public boolean isUserProperties()
  {
    return this.userProperties;
  }

  public void setUserProperties(boolean paramBoolean)
  {
    this.userProperties = paramBoolean;
  }

  public boolean isRgbTransparencyBlending()
  {
    return this.rgbTransparencyBlending;
  }

  public void setRgbTransparencyBlending(boolean paramBoolean)
  {
    this.rgbTransparencyBlending = paramBoolean;
  }

  static class PdfTrailer extends PdfDictionary
  {
    int offset;

    PdfTrailer(int paramInt1, int paramInt2, PdfIndirectReference paramPdfIndirectReference1, PdfIndirectReference paramPdfIndirectReference2, PdfIndirectReference paramPdfIndirectReference3, PdfObject paramPdfObject, int paramInt3)
    {
      this.offset = paramInt2;
      put(PdfName.SIZE, new PdfNumber(paramInt1));
      put(PdfName.ROOT, paramPdfIndirectReference1);
      if (paramPdfIndirectReference2 != null)
        put(PdfName.INFO, paramPdfIndirectReference2);
      if (paramPdfIndirectReference3 != null)
        put(PdfName.ENCRYPT, paramPdfIndirectReference3);
      if (paramPdfObject != null)
        put(PdfName.ID, paramPdfObject);
      if (paramInt3 > 0)
        put(PdfName.PREV, new PdfNumber(paramInt3));
    }

    public void toPdf(PdfWriter paramPdfWriter, OutputStream paramOutputStream)
      throws IOException
    {
      paramOutputStream.write(DocWriter.getISOBytes("trailer\n"));
      super.toPdf(null, paramOutputStream);
      paramOutputStream.write(DocWriter.getISOBytes("\nstartxref\n"));
      paramOutputStream.write(DocWriter.getISOBytes(String.valueOf(this.offset)));
      paramOutputStream.write(DocWriter.getISOBytes("\n%%EOF\n"));
    }
  }

  public static class PdfBody
  {
    private static final int OBJSINSTREAM = 200;
    private TreeSet xrefs = new TreeSet();
    private int refnum;
    private int position;
    private PdfWriter writer;
    private ByteBuffer index;
    private ByteBuffer streamObjects;
    private int currentObjNum;
    private int numObj = 0;

    PdfBody(PdfWriter paramPdfWriter)
    {
      this.xrefs.add(new PdfCrossReference(0, 0, 65535));
      this.position = paramPdfWriter.getOs().getCounter();
      this.refnum = 1;
      this.writer = paramPdfWriter;
    }

    void setRefnum(int paramInt)
    {
      this.refnum = paramInt;
    }

    private PdfCrossReference addToObjStm(PdfObject paramPdfObject, int paramInt)
      throws IOException
    {
      if (this.numObj >= 200)
        flushObjStm();
      if (this.index == null)
      {
        this.index = new ByteBuffer();
        this.streamObjects = new ByteBuffer();
        this.currentObjNum = getIndirectReferenceNumber();
        this.numObj = 0;
      }
      int i = this.streamObjects.size();
      int j = this.numObj++;
      PdfEncryption localPdfEncryption = this.writer.crypto;
      this.writer.crypto = null;
      paramPdfObject.toPdf(this.writer, this.streamObjects);
      this.writer.crypto = localPdfEncryption;
      this.streamObjects.append(' ');
      this.index.append(paramInt).append(' ').append(i).append(' ');
      return new PdfCrossReference(2, paramInt, this.currentObjNum, j);
    }

    private void flushObjStm()
      throws IOException
    {
      if (this.numObj == 0)
        return;
      int i = this.index.size();
      this.index.append(this.streamObjects);
      PdfStream localPdfStream = new PdfStream(this.index.toByteArray());
      localPdfStream.flateCompress(this.writer.getCompressionLevel());
      localPdfStream.put(PdfName.TYPE, PdfName.OBJSTM);
      localPdfStream.put(PdfName.N, new PdfNumber(this.numObj));
      localPdfStream.put(PdfName.FIRST, new PdfNumber(i));
      add(localPdfStream, this.currentObjNum);
      this.index = null;
      this.streamObjects = null;
      this.numObj = 0;
    }

    PdfIndirectObject add(PdfObject paramPdfObject)
      throws IOException
    {
      return add(paramPdfObject, getIndirectReferenceNumber());
    }

    PdfIndirectObject add(PdfObject paramPdfObject, boolean paramBoolean)
      throws IOException
    {
      return add(paramPdfObject, getIndirectReferenceNumber(), paramBoolean);
    }

    PdfIndirectReference getPdfIndirectReference()
    {
      return new PdfIndirectReference(0, getIndirectReferenceNumber());
    }

    int getIndirectReferenceNumber()
    {
      int i = this.refnum++;
      this.xrefs.add(new PdfCrossReference(i, 0, 65535));
      return i;
    }

    PdfIndirectObject add(PdfObject paramPdfObject, PdfIndirectReference paramPdfIndirectReference)
      throws IOException
    {
      return add(paramPdfObject, paramPdfIndirectReference.getNumber());
    }

    PdfIndirectObject add(PdfObject paramPdfObject, PdfIndirectReference paramPdfIndirectReference, boolean paramBoolean)
      throws IOException
    {
      return add(paramPdfObject, paramPdfIndirectReference.getNumber(), paramBoolean);
    }

    PdfIndirectObject add(PdfObject paramPdfObject, int paramInt)
      throws IOException
    {
      return add(paramPdfObject, paramInt, true);
    }

    PdfIndirectObject add(PdfObject paramPdfObject, int paramInt, boolean paramBoolean)
      throws IOException
    {
      if ((paramBoolean) && (paramPdfObject.canBeInObjStm()) && (this.writer.isFullCompression()))
      {
        localObject1 = addToObjStm(paramPdfObject, paramInt);
        localObject2 = new PdfIndirectObject(paramInt, paramPdfObject, this.writer);
        if (!this.xrefs.add(localObject1))
        {
          this.xrefs.remove(localObject1);
          this.xrefs.add(localObject1);
        }
        return localObject2;
      }
      Object localObject1 = new PdfIndirectObject(paramInt, paramPdfObject, this.writer);
      Object localObject2 = new PdfCrossReference(paramInt, this.position);
      if (!this.xrefs.add(localObject2))
      {
        this.xrefs.remove(localObject2);
        this.xrefs.add(localObject2);
      }
      ((PdfIndirectObject)localObject1).writeTo(this.writer.getOs());
      this.position = this.writer.getOs().getCounter();
      return (PdfIndirectObject)(PdfIndirectObject)localObject1;
    }

    int offset()
    {
      return this.position;
    }

    int size()
    {
      return Math.max(((PdfCrossReference)this.xrefs.last()).getRefnum() + 1, this.refnum);
    }

    void writeCrossReferenceTable(OutputStream paramOutputStream, PdfIndirectReference paramPdfIndirectReference1, PdfIndirectReference paramPdfIndirectReference2, PdfIndirectReference paramPdfIndirectReference3, PdfObject paramPdfObject, int paramInt)
      throws IOException
    {
      int i = 0;
      if (this.writer.isFullCompression())
      {
        flushObjStm();
        i = getIndirectReferenceNumber();
        this.xrefs.add(new PdfCrossReference(i, this.position));
      }
      PdfCrossReference localPdfCrossReference = (PdfCrossReference)this.xrefs.first();
      int j = localPdfCrossReference.getRefnum();
      int k = 0;
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator1 = this.xrefs.iterator();
      while (localIterator1.hasNext())
      {
        localPdfCrossReference = (PdfCrossReference)localIterator1.next();
        if (j + k == localPdfCrossReference.getRefnum())
        {
          k++;
          continue;
        }
        localArrayList.add(new Integer(j));
        localArrayList.add(new Integer(k));
        j = localPdfCrossReference.getRefnum();
        k = 1;
      }
      localArrayList.add(new Integer(j));
      localArrayList.add(new Integer(k));
      int n;
      if (this.writer.isFullCompression())
      {
        int m = 4;
        n = -16777216;
        while ((m > 1) && ((n & this.position) == 0))
        {
          n >>>= 8;
          m--;
        }
        ByteBuffer localByteBuffer = new ByteBuffer();
        Object localObject = this.xrefs.iterator();
        while (((Iterator)localObject).hasNext())
        {
          localPdfCrossReference = (PdfCrossReference)((Iterator)localObject).next();
          localPdfCrossReference.toPdf(m, localByteBuffer);
        }
        localObject = new PdfStream(localByteBuffer.toByteArray());
        localByteBuffer = null;
        ((PdfStream)localObject).flateCompress(this.writer.getCompressionLevel());
        ((PdfStream)localObject).put(PdfName.SIZE, new PdfNumber(size()));
        ((PdfStream)localObject).put(PdfName.ROOT, paramPdfIndirectReference1);
        if (paramPdfIndirectReference2 != null)
          ((PdfStream)localObject).put(PdfName.INFO, paramPdfIndirectReference2);
        if (paramPdfIndirectReference3 != null)
          ((PdfStream)localObject).put(PdfName.ENCRYPT, paramPdfIndirectReference3);
        if (paramPdfObject != null)
          ((PdfStream)localObject).put(PdfName.ID, paramPdfObject);
        ((PdfStream)localObject).put(PdfName.W, new PdfArray(new int[] { 1, m, 2 }));
        ((PdfStream)localObject).put(PdfName.TYPE, PdfName.XREF);
        PdfArray localPdfArray = new PdfArray();
        for (int i1 = 0; i1 < localArrayList.size(); i1++)
          localPdfArray.add(new PdfNumber(((Integer)localArrayList.get(i1)).intValue()));
        ((PdfStream)localObject).put(PdfName.INDEX, localPdfArray);
        if (paramInt > 0)
          ((PdfStream)localObject).put(PdfName.PREV, new PdfNumber(paramInt));
        PdfEncryption localPdfEncryption = this.writer.crypto;
        this.writer.crypto = null;
        PdfIndirectObject localPdfIndirectObject = new PdfIndirectObject(i, (PdfObject)localObject, this.writer);
        localPdfIndirectObject.writeTo(this.writer.getOs());
        this.writer.crypto = localPdfEncryption;
      }
      else
      {
        paramOutputStream.write(DocWriter.getISOBytes("xref\n"));
        Iterator localIterator2 = this.xrefs.iterator();
        for (n = 0; n < localArrayList.size(); n += 2)
        {
          j = ((Integer)localArrayList.get(n)).intValue();
          k = ((Integer)localArrayList.get(n + 1)).intValue();
          paramOutputStream.write(DocWriter.getISOBytes(String.valueOf(j)));
          paramOutputStream.write(DocWriter.getISOBytes(" "));
          paramOutputStream.write(DocWriter.getISOBytes(String.valueOf(k)));
          paramOutputStream.write(10);
          while (k-- > 0)
          {
            localPdfCrossReference = (PdfCrossReference)localIterator2.next();
            localPdfCrossReference.toPdf(paramOutputStream);
          }
        }
      }
    }

    static class PdfCrossReference
      implements Comparable
    {
      private int type;
      private int offset;
      private int refnum;
      private int generation;

      PdfCrossReference(int paramInt1, int paramInt2, int paramInt3)
      {
        this.type = 0;
        this.offset = paramInt2;
        this.refnum = paramInt1;
        this.generation = paramInt3;
      }

      PdfCrossReference(int paramInt1, int paramInt2)
      {
        this.type = 1;
        this.offset = paramInt2;
        this.refnum = paramInt1;
        this.generation = 0;
      }

      PdfCrossReference(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        this.type = paramInt1;
        this.offset = paramInt3;
        this.refnum = paramInt2;
        this.generation = paramInt4;
      }

      int getRefnum()
      {
        return this.refnum;
      }

      public void toPdf(OutputStream paramOutputStream)
        throws IOException
      {
        StringBuffer localStringBuffer1 = new StringBuffer("0000000000").append(this.offset);
        localStringBuffer1.delete(0, localStringBuffer1.length() - 10);
        StringBuffer localStringBuffer2 = new StringBuffer("00000").append(this.generation);
        localStringBuffer2.delete(0, localStringBuffer2.length() - 5);
        localStringBuffer1.append(' ').append(localStringBuffer2).append(this.generation == 65535 ? " f \n" : " n \n");
        paramOutputStream.write(DocWriter.getISOBytes(localStringBuffer1.toString()));
      }

      public void toPdf(int paramInt, OutputStream paramOutputStream)
        throws IOException
      {
        paramOutputStream.write((byte)this.type);
        while (true)
        {
          paramInt--;
          if (paramInt < 0)
            break;
          paramOutputStream.write((byte)(this.offset >>> 8 * paramInt & 0xFF));
        }
        paramOutputStream.write((byte)(this.generation >>> 8 & 0xFF));
        paramOutputStream.write((byte)(this.generation & 0xFF));
      }

      public int compareTo(Object paramObject)
      {
        PdfCrossReference localPdfCrossReference = (PdfCrossReference)paramObject;
        return this.refnum == localPdfCrossReference.refnum ? 0 : this.refnum < localPdfCrossReference.refnum ? -1 : 1;
      }

      public boolean equals(Object paramObject)
      {
        if ((paramObject instanceof PdfCrossReference))
        {
          PdfCrossReference localPdfCrossReference = (PdfCrossReference)paramObject;
          return this.refnum == localPdfCrossReference.refnum;
        }
        return false;
      }

      public int hashCode()
      {
        return this.refnum;
      }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfWriter
 * JD-Core Version:    0.6.0
 */