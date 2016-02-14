package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.draw.DrawInterface;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class PdfDocument extends Document
{
  protected PdfWriter writer;
  protected PdfContentByte text;
  protected PdfContentByte graphics;
  protected float leading = 0.0F;
  protected int alignment = 0;
  protected float currentHeight = 0.0F;
  protected boolean isSectionTitle = false;
  protected int leadingCount = 0;
  protected PdfAction anchorAction = null;
  protected int textEmptySize;
  protected byte[] xmpMetadata = null;
  protected float nextMarginLeft;
  protected float nextMarginRight;
  protected float nextMarginTop;
  protected float nextMarginBottom;
  protected boolean firstPageEvent = true;
  protected PdfLine line = null;
  protected ArrayList lines = new ArrayList();
  protected int lastElementType = -1;
  static final String hangingPunctuation = ".,;:'";
  protected Indentation indentation = new Indentation();
  protected PdfInfo info = new PdfInfo();
  protected PdfOutline rootOutline;
  protected PdfOutline currentOutline;
  protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
  protected PdfPageLabels pageLabels;
  protected TreeMap localDestinations = new TreeMap();
  int jsCounter;
  protected HashMap documentLevelJS = new HashMap();
  protected static final DecimalFormat SIXTEEN_DIGITS = new DecimalFormat("0000000000000000");
  protected HashMap documentFileAttachment = new HashMap();
  protected String openActionName;
  protected PdfAction openActionAction;
  protected PdfDictionary additionalActions;
  protected PdfCollection collection;
  PdfAnnotationsImp annotationsImp;
  protected int markPoint;
  protected Rectangle nextPageSize = null;
  protected HashMap thisBoxSize = new HashMap();
  protected HashMap boxSize = new HashMap();
  protected boolean pageEmpty = true;
  protected int duration = -1;
  protected PdfTransition transition = null;
  protected PdfDictionary pageAA = null;
  protected PdfIndirectReference thumb;
  protected PageResources pageResources;
  protected boolean strictImageSequence = false;
  protected float imageEnd = -1.0F;
  protected Image imageWait = null;

  public PdfDocument()
  {
    addProducer();
    addCreationDate();
  }

  public void addWriter(PdfWriter paramPdfWriter)
    throws DocumentException
  {
    if (this.writer == null)
    {
      this.writer = paramPdfWriter;
      this.annotationsImp = new PdfAnnotationsImp(paramPdfWriter);
      return;
    }
    throw new DocumentException("You can only add a writer to a PdfDocument once.");
  }

  public float getLeading()
  {
    return this.leading;
  }

  void setLeading(float paramFloat)
  {
    this.leading = paramFloat;
  }

  public boolean add(Element paramElement)
    throws DocumentException
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return false;
    try
    {
      Object localObject1;
      Object localObject3;
      Object localObject4;
      float f1;
      Object localObject2;
      switch (paramElement.type())
      {
      case 0:
        this.info.addkey(((Meta)paramElement).getName(), ((Meta)paramElement).getContent());
        break;
      case 1:
        this.info.addTitle(((Meta)paramElement).getContent());
        break;
      case 2:
        this.info.addSubject(((Meta)paramElement).getContent());
        break;
      case 3:
        this.info.addKeywords(((Meta)paramElement).getContent());
        break;
      case 4:
        this.info.addAuthor(((Meta)paramElement).getContent());
        break;
      case 7:
        this.info.addCreator(((Meta)paramElement).getContent());
        break;
      case 5:
        this.info.addProducer();
        break;
      case 6:
        this.info.addCreationDate();
        break;
      case 10:
        if (this.line == null)
          carriageReturn();
        localObject1 = new PdfChunk((Chunk)paramElement, this.anchorAction);
        while ((localObject3 = this.line.add((PdfChunk)localObject1)) != null)
        {
          carriageReturn();
          localObject1 = localObject3;
          ((PdfChunk)localObject1).trimFirstSpace();
        }
        this.pageEmpty = false;
        if (!((PdfChunk)localObject1).isAttribute("NEWPAGE"))
          break;
        newPage();
        break;
      case 17:
        this.leadingCount += 1;
        localObject1 = (Anchor)paramElement;
        localObject3 = ((Anchor)localObject1).getReference();
        this.leading = ((Anchor)localObject1).getLeading();
        if (localObject3 != null)
          this.anchorAction = new PdfAction((String)localObject3);
        paramElement.process(this);
        this.anchorAction = null;
        this.leadingCount -= 1;
        break;
      case 29:
        if (this.line == null)
          carriageReturn();
        localObject1 = (Annotation)paramElement;
        localObject3 = new Rectangle(0.0F, 0.0F);
        if (this.line != null)
          localObject3 = new Rectangle(((Annotation)localObject1).llx(indentRight() - this.line.widthLeft()), ((Annotation)localObject1).lly(indentTop() - this.currentHeight), ((Annotation)localObject1).urx(indentRight() - this.line.widthLeft() + 20.0F), ((Annotation)localObject1).ury(indentTop() - this.currentHeight - 20.0F));
        localObject4 = PdfAnnotationsImp.convertAnnotation(this.writer, (Annotation)localObject1, (Rectangle)localObject3);
        this.annotationsImp.addPlainAnnotation((PdfAnnotation)localObject4);
        this.pageEmpty = false;
        break;
      case 11:
        this.leadingCount += 1;
        this.leading = ((Phrase)paramElement).getLeading();
        paramElement.process(this);
        this.leadingCount -= 1;
        break;
      case 12:
        this.leadingCount += 1;
        localObject1 = (Paragraph)paramElement;
        addSpacing(((Paragraph)localObject1).getSpacingBefore(), this.leading, ((Paragraph)localObject1).getFont());
        this.alignment = ((Paragraph)localObject1).getAlignment();
        this.leading = ((Paragraph)localObject1).getTotalLeading();
        carriageReturn();
        if (this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom())
          newPage();
        this.indentation.indentLeft += ((Paragraph)localObject1).getIndentationLeft();
        this.indentation.indentRight += ((Paragraph)localObject1).getIndentationRight();
        carriageReturn();
        localObject3 = this.writer.getPageEvent();
        if ((localObject3 != null) && (!this.isSectionTitle))
          ((PdfPageEvent)localObject3).onParagraph(this.writer, this, indentTop() - this.currentHeight);
        if (((Paragraph)localObject1).getKeepTogether())
        {
          carriageReturn();
          localObject4 = new PdfPTable(1);
          ((PdfPTable)localObject4).setWidthPercentage(100.0F);
          PdfPCell localPdfPCell = new PdfPCell();
          localPdfPCell.addElement((Element)localObject1);
          localPdfPCell.setBorder(0);
          localPdfPCell.setPadding(0.0F);
          ((PdfPTable)localObject4).addCell(localPdfPCell);
          this.indentation.indentLeft -= ((Paragraph)localObject1).getIndentationLeft();
          this.indentation.indentRight -= ((Paragraph)localObject1).getIndentationRight();
          add((Element)localObject4);
          this.indentation.indentLeft += ((Paragraph)localObject1).getIndentationLeft();
          this.indentation.indentRight += ((Paragraph)localObject1).getIndentationRight();
        }
        else
        {
          this.line.setExtraIndent(((Paragraph)localObject1).getFirstLineIndent());
          paramElement.process(this);
          carriageReturn();
          addSpacing(((Paragraph)localObject1).getSpacingAfter(), ((Paragraph)localObject1).getTotalLeading(), ((Paragraph)localObject1).getFont());
        }
        if ((localObject3 != null) && (!this.isSectionTitle))
          ((PdfPageEvent)localObject3).onParagraphEnd(this.writer, this, indentTop() - this.currentHeight);
        this.alignment = 0;
        this.indentation.indentLeft -= ((Paragraph)localObject1).getIndentationLeft();
        this.indentation.indentRight -= ((Paragraph)localObject1).getIndentationRight();
        carriageReturn();
        this.leadingCount -= 1;
        break;
      case 13:
      case 16:
        localObject1 = (Section)paramElement;
        localObject3 = this.writer.getPageEvent();
        int i = (((Section)localObject1).isNotAddedYet()) && (((Section)localObject1).getTitle() != null) ? 1 : 0;
        if (((Section)localObject1).isTriggerNewPage())
          newPage();
        if (i != 0)
        {
          float f2 = indentTop() - this.currentHeight;
          int j = this.pageSize.getRotation();
          if ((j == 90) || (j == 180))
            f2 = this.pageSize.getHeight() - f2;
          PdfDestination localPdfDestination = new PdfDestination(2, f2);
          while (this.currentOutline.level() >= ((Section)localObject1).getDepth())
            this.currentOutline = this.currentOutline.parent();
          PdfOutline localPdfOutline = new PdfOutline(this.currentOutline, localPdfDestination, ((Section)localObject1).getBookmarkTitle(), ((Section)localObject1).isBookmarkOpen());
          this.currentOutline = localPdfOutline;
        }
        carriageReturn();
        this.indentation.sectionIndentLeft += ((Section)localObject1).getIndentationLeft();
        this.indentation.sectionIndentRight += ((Section)localObject1).getIndentationRight();
        if ((((Section)localObject1).isNotAddedYet()) && (localObject3 != null))
          if (paramElement.type() == 16)
            ((PdfPageEvent)localObject3).onChapter(this.writer, this, indentTop() - this.currentHeight, ((Section)localObject1).getTitle());
          else
            ((PdfPageEvent)localObject3).onSection(this.writer, this, indentTop() - this.currentHeight, ((Section)localObject1).getDepth(), ((Section)localObject1).getTitle());
        if (i != 0)
        {
          this.isSectionTitle = true;
          add(((Section)localObject1).getTitle());
          this.isSectionTitle = false;
        }
        this.indentation.sectionIndentLeft += ((Section)localObject1).getIndentation();
        paramElement.process(this);
        flushLines();
        this.indentation.sectionIndentLeft -= ((Section)localObject1).getIndentationLeft() + ((Section)localObject1).getIndentation();
        this.indentation.sectionIndentRight -= ((Section)localObject1).getIndentationRight();
        if ((!((Section)localObject1).isComplete()) || (localObject3 == null))
          break;
        if (paramElement.type() == 16)
          ((PdfPageEvent)localObject3).onChapterEnd(this.writer, this, indentTop() - this.currentHeight);
        else
          ((PdfPageEvent)localObject3).onSectionEnd(this.writer, this, indentTop() - this.currentHeight);
        break;
      case 14:
        localObject1 = (com.lowagie.text.List)paramElement;
        if (((com.lowagie.text.List)localObject1).isAlignindent())
          ((com.lowagie.text.List)localObject1).normalizeIndentation();
        this.indentation.listIndentLeft += ((com.lowagie.text.List)localObject1).getIndentationLeft();
        this.indentation.indentRight += ((com.lowagie.text.List)localObject1).getIndentationRight();
        paramElement.process(this);
        this.indentation.listIndentLeft -= ((com.lowagie.text.List)localObject1).getIndentationLeft();
        this.indentation.indentRight -= ((com.lowagie.text.List)localObject1).getIndentationRight();
        carriageReturn();
        break;
      case 15:
        this.leadingCount += 1;
        localObject1 = (ListItem)paramElement;
        addSpacing(((ListItem)localObject1).getSpacingBefore(), this.leading, ((ListItem)localObject1).getFont());
        this.alignment = ((ListItem)localObject1).getAlignment();
        this.indentation.listIndentLeft += ((ListItem)localObject1).getIndentationLeft();
        this.indentation.indentRight += ((ListItem)localObject1).getIndentationRight();
        this.leading = ((ListItem)localObject1).getTotalLeading();
        carriageReturn();
        this.line.setListItem((ListItem)localObject1);
        paramElement.process(this);
        addSpacing(((ListItem)localObject1).getSpacingAfter(), ((ListItem)localObject1).getTotalLeading(), ((ListItem)localObject1).getFont());
        if (this.line.hasToBeJustified())
          this.line.resetAlignment();
        carriageReturn();
        this.indentation.listIndentLeft -= ((ListItem)localObject1).getIndentationLeft();
        this.indentation.indentRight -= ((ListItem)localObject1).getIndentationRight();
        this.leadingCount -= 1;
        break;
      case 30:
        localObject1 = (Rectangle)paramElement;
        this.graphics.rectangle((Rectangle)localObject1);
        this.pageEmpty = false;
        break;
      case 23:
        localObject1 = (PdfPTable)paramElement;
        if (((PdfPTable)localObject1).size() <= ((PdfPTable)localObject1).getHeaderRows())
          break;
        ensureNewLine();
        flushLines();
        addPTable((PdfPTable)localObject1);
        this.pageEmpty = false;
        newLine();
        break;
      case 40:
        ensureNewLine();
        flushLines();
        localObject1 = (MultiColumnText)paramElement;
        f1 = ((MultiColumnText)localObject1).write(this.writer.getDirectContent(), this, indentTop() - this.currentHeight);
        this.currentHeight += f1;
        this.text.moveText(0.0F, -1.0F * f1);
        this.pageEmpty = false;
        break;
      case 22:
        if ((paramElement instanceof SimpleTable))
        {
          localObject1 = ((SimpleTable)paramElement).createPdfPTable();
          if (((PdfPTable)localObject1).size() <= ((PdfPTable)localObject1).getHeaderRows())
            break;
          ensureNewLine();
          flushLines();
          addPTable((PdfPTable)localObject1);
          this.pageEmpty = false;
        }
        else if ((paramElement instanceof Table))
        {
          try
          {
            localObject1 = ((Table)paramElement).createPdfPTable();
            if (((PdfPTable)localObject1).size() <= ((PdfPTable)localObject1).getHeaderRows())
              break;
            ensureNewLine();
            flushLines();
            addPTable((PdfPTable)localObject1);
            this.pageEmpty = false;
          }
          catch (BadElementException localBadElementException)
          {
            f1 = ((Table)paramElement).getOffset();
            if (Float.isNaN(f1))
              f1 = this.leading;
            carriageReturn();
            this.lines.add(new PdfLine(indentLeft(), indentRight(), this.alignment, f1));
            this.currentHeight += f1;
            addPdfTable((Table)paramElement);
          }
        }
        else
        {
          return false;
        }
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
        add((Image)paramElement);
        break;
      case 55:
        localObject2 = (DrawInterface)paramElement;
        ((DrawInterface)localObject2).draw(this.graphics, indentLeft(), indentBottom(), indentRight(), indentTop(), indentTop() - this.currentHeight - (this.leadingCount > 0 ? this.leading : 0.0F));
        this.pageEmpty = false;
        break;
      case 50:
        if ((paramElement instanceof MarkedSection))
        {
          localObject2 = ((MarkedSection)paramElement).getTitle();
          if (localObject2 != null)
            ((MarkedObject)localObject2).process(this);
        }
        localObject2 = (MarkedObject)paramElement;
        ((MarkedObject)localObject2).process(this);
        break;
      case 8:
      case 9:
      case 18:
      case 19:
      case 20:
      case 21:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 31:
      case 37:
      case 38:
      case 39:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
      case 51:
      case 52:
      case 53:
      case 54:
      default:
        return false;
      }
      this.lastElementType = paramElement.type();
      return true;
    }
    catch (Exception localException)
    {
    }
    throw new DocumentException(localException);
  }

  public void open()
  {
    if (!this.open)
    {
      super.open();
      this.writer.open();
      this.rootOutline = new PdfOutline(this.writer);
      this.currentOutline = this.rootOutline;
    }
    try
    {
      initPage();
    }
    catch (DocumentException localDocumentException)
    {
      throw new ExceptionConverter(localDocumentException);
    }
  }

  public void close()
  {
    if (this.close)
      return;
    try
    {
      int i = this.imageWait != null ? 1 : 0;
      newPage();
      if ((this.imageWait != null) || (i != 0))
        newPage();
      if (this.annotationsImp.hasUnusedAnnotations())
        throw new RuntimeException("Not all annotations could be added to the document (the document doesn't have enough pages).");
      PdfPageEvent localPdfPageEvent = this.writer.getPageEvent();
      if (localPdfPageEvent != null)
        localPdfPageEvent.onCloseDocument(this.writer, this);
      super.close();
      this.writer.addLocalDestinations(this.localDestinations);
      calculateOutlineCount();
      writeOutlines();
    }
    catch (Exception localException)
    {
      throw ExceptionConverter.convertException(localException);
    }
    this.writer.close();
  }

  public void setXmpMetadata(byte[] paramArrayOfByte)
  {
    this.xmpMetadata = paramArrayOfByte;
  }

  public boolean newPage()
  {
    this.lastElementType = -1;
    if ((this.writer == null) || ((this.writer.getDirectContent().size() == 0) && (this.writer.getDirectContentUnder().size() == 0) && ((this.pageEmpty) || (this.writer.isPaused()))))
    {
      setNewPageSizeAndMargins();
      return false;
    }
    if ((!this.open) || (this.close))
      throw new RuntimeException("The document isn't open.");
    PdfPageEvent localPdfPageEvent = this.writer.getPageEvent();
    if (localPdfPageEvent != null)
      localPdfPageEvent.onEndPage(this.writer, this);
    super.newPage();
    this.indentation.imageIndentLeft = 0.0F;
    this.indentation.imageIndentRight = 0.0F;
    try
    {
      flushLines();
      int i = this.pageSize.getRotation();
      if (this.writer.isPdfX())
      {
        if ((this.thisBoxSize.containsKey("art")) && (this.thisBoxSize.containsKey("trim")))
          throw new PdfXConformanceException("Only one of ArtBox or TrimBox can exist in the page.");
        if ((!this.thisBoxSize.containsKey("art")) && (!this.thisBoxSize.containsKey("trim")))
          if (this.thisBoxSize.containsKey("crop"))
            this.thisBoxSize.put("trim", this.thisBoxSize.get("crop"));
          else
            this.thisBoxSize.put("trim", new PdfRectangle(this.pageSize, this.pageSize.getRotation()));
      }
      this.pageResources.addDefaultColorDiff(this.writer.getDefaultColorspace());
      if (this.writer.isRgbTransparencyBlending())
      {
        localPdfDictionary = new PdfDictionary();
        localPdfDictionary.put(PdfName.CS, PdfName.DEVICERGB);
        this.pageResources.addDefaultColorDiff(localPdfDictionary);
      }
      PdfDictionary localPdfDictionary = this.pageResources.getResources();
      PdfPage localPdfPage = new PdfPage(new PdfRectangle(this.pageSize, i), this.thisBoxSize, localPdfDictionary, i);
      localPdfPage.put(PdfName.TABS, this.writer.getTabs());
      Object localObject;
      if (this.xmpMetadata != null)
      {
        localObject = new PdfStream(this.xmpMetadata);
        ((PdfStream)localObject).put(PdfName.TYPE, PdfName.METADATA);
        ((PdfStream)localObject).put(PdfName.SUBTYPE, PdfName.XML);
        PdfEncryption localPdfEncryption = this.writer.getEncryption();
        if ((localPdfEncryption != null) && (!localPdfEncryption.isMetadataEncrypted()))
        {
          PdfArray localPdfArray = new PdfArray();
          localPdfArray.add(PdfName.CRYPT);
          ((PdfStream)localObject).put(PdfName.FILTER, localPdfArray);
        }
        localPdfPage.put(PdfName.METADATA, this.writer.addToBody((PdfObject)localObject).getIndirectReference());
      }
      if (this.transition != null)
      {
        localPdfPage.put(PdfName.TRANS, this.transition.getTransitionDictionary());
        this.transition = null;
      }
      if (this.duration > 0)
      {
        localPdfPage.put(PdfName.DUR, new PdfNumber(this.duration));
        this.duration = 0;
      }
      if (this.pageAA != null)
      {
        localPdfPage.put(PdfName.AA, this.writer.addToBody(this.pageAA).getIndirectReference());
        this.pageAA = null;
      }
      if (this.thumb != null)
      {
        localPdfPage.put(PdfName.THUMB, this.thumb);
        this.thumb = null;
      }
      if (this.writer.getUserunit() > 0.0F)
        localPdfPage.put(PdfName.USERUNIT, new PdfNumber(this.writer.getUserunit()));
      if (this.annotationsImp.hasUnusedAnnotations())
      {
        localObject = this.annotationsImp.rotateAnnotations(this.writer, this.pageSize);
        if (((PdfArray)localObject).size() != 0)
          localPdfPage.put(PdfName.ANNOTS, (PdfObject)localObject);
      }
      if (this.writer.isTagged())
        localPdfPage.put(PdfName.STRUCTPARENTS, new PdfNumber(this.writer.getCurrentPageNumber() - 1));
      if (this.text.size() > this.textEmptySize)
        this.text.endText();
      else
        this.text = null;
      this.writer.add(localPdfPage, new PdfContents(this.writer.getDirectContentUnder(), this.graphics, this.text, this.writer.getDirectContent(), this.pageSize));
      initPage();
    }
    catch (DocumentException localDocumentException)
    {
      throw new ExceptionConverter(localDocumentException);
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
    return true;
  }

  public boolean setPageSize(Rectangle paramRectangle)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return false;
    this.nextPageSize = new Rectangle(paramRectangle);
    return true;
  }

  public boolean setMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return false;
    this.nextMarginLeft = paramFloat1;
    this.nextMarginRight = paramFloat2;
    this.nextMarginTop = paramFloat3;
    this.nextMarginBottom = paramFloat4;
    return true;
  }

  public boolean setMarginMirroring(boolean paramBoolean)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return false;
    return super.setMarginMirroring(paramBoolean);
  }

  public boolean setMarginMirroringTopBottom(boolean paramBoolean)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return false;
    return super.setMarginMirroringTopBottom(paramBoolean);
  }

  public void setPageCount(int paramInt)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return;
    super.setPageCount(paramInt);
  }

  public void resetPageCount()
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return;
    super.resetPageCount();
  }

  public void setHeader(HeaderFooter paramHeaderFooter)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return;
    super.setHeader(paramHeaderFooter);
  }

  public void resetHeader()
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return;
    super.resetHeader();
  }

  public void setFooter(HeaderFooter paramHeaderFooter)
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return;
    super.setFooter(paramHeaderFooter);
  }

  public void resetFooter()
  {
    if ((this.writer != null) && (this.writer.isPaused()))
      return;
    super.resetFooter();
  }

  protected void initPage()
    throws DocumentException
  {
    this.pageN += 1;
    this.annotationsImp.resetAnnotations();
    this.pageResources = new PageResources();
    this.writer.resetContent();
    this.graphics = new PdfContentByte(this.writer);
    this.text = new PdfContentByte(this.writer);
    this.text.reset();
    this.text.beginText();
    this.textEmptySize = this.text.size();
    this.markPoint = 0;
    setNewPageSizeAndMargins();
    this.imageEnd = -1.0F;
    this.indentation.imageIndentRight = 0.0F;
    this.indentation.imageIndentLeft = 0.0F;
    this.indentation.indentBottom = 0.0F;
    this.indentation.indentTop = 0.0F;
    this.currentHeight = 0.0F;
    this.thisBoxSize = new HashMap(this.boxSize);
    if ((this.pageSize.getBackgroundColor() != null) || (this.pageSize.hasBorders()) || (this.pageSize.getBorderColor() != null))
      add(this.pageSize);
    float f = this.leading;
    int i = this.alignment;
    doFooter();
    this.text.moveText(left(), top());
    doHeader();
    this.pageEmpty = true;
    try
    {
      if (this.imageWait != null)
      {
        add(this.imageWait);
        this.imageWait = null;
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
    this.leading = f;
    this.alignment = i;
    carriageReturn();
    PdfPageEvent localPdfPageEvent = this.writer.getPageEvent();
    if (localPdfPageEvent != null)
    {
      if (this.firstPageEvent)
        localPdfPageEvent.onOpenDocument(this.writer, this);
      localPdfPageEvent.onStartPage(this.writer, this);
    }
    this.firstPageEvent = false;
  }

  protected void newLine()
    throws DocumentException
  {
    this.lastElementType = -1;
    carriageReturn();
    if ((this.lines != null) && (!this.lines.isEmpty()))
    {
      this.lines.add(this.line);
      this.currentHeight += this.line.height();
    }
    this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
  }

  protected void carriageReturn()
  {
    if (this.lines == null)
      this.lines = new ArrayList();
    if (this.line != null)
      if (this.currentHeight + this.line.height() + this.leading < indentTop() - indentBottom())
      {
        if (this.line.size() > 0)
        {
          this.currentHeight += this.line.height();
          this.lines.add(this.line);
          this.pageEmpty = false;
        }
      }
      else
        newPage();
    if ((this.imageEnd > -1.0F) && (this.currentHeight > this.imageEnd))
    {
      this.imageEnd = -1.0F;
      this.indentation.imageIndentRight = 0.0F;
      this.indentation.imageIndentLeft = 0.0F;
    }
    this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
  }

  public float getVerticalPosition(boolean paramBoolean)
  {
    if (paramBoolean)
      ensureNewLine();
    return top() - this.currentHeight - this.indentation.indentTop;
  }

  protected void ensureNewLine()
  {
    try
    {
      if ((this.lastElementType == 11) || (this.lastElementType == 10))
      {
        newLine();
        flushLines();
      }
    }
    catch (DocumentException localDocumentException)
    {
      throw new ExceptionConverter(localDocumentException);
    }
  }

  protected float flushLines()
    throws DocumentException
  {
    if (this.lines == null)
      return 0.0F;
    if ((this.line != null) && (this.line.size() > 0))
    {
      this.lines.add(this.line);
      this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
    }
    if (this.lines.isEmpty())
      return 0.0F;
    Object[] arrayOfObject = new Object[2];
    PdfFont localPdfFont = null;
    float f1 = 0.0F;
    Float localFloat = new Float(0.0F);
    arrayOfObject[1] = localFloat;
    Iterator localIterator = this.lines.iterator();
    while (localIterator.hasNext())
    {
      PdfLine localPdfLine = (PdfLine)localIterator.next();
      float f2 = localPdfLine.indentLeft() - indentLeft() + this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.sectionIndentLeft;
      this.text.moveText(f2, -localPdfLine.height());
      if (localPdfLine.listSymbol() != null)
        ColumnText.showTextAligned(this.graphics, 0, new Phrase(localPdfLine.listSymbol()), this.text.getXTLM() - localPdfLine.listIndent(), this.text.getYTLM(), 0.0F);
      arrayOfObject[0] = localPdfFont;
      writeLineToContent(localPdfLine, this.text, this.graphics, arrayOfObject, this.writer.getSpaceCharRatio());
      localPdfFont = (PdfFont)arrayOfObject[0];
      f1 += localPdfLine.height();
      this.text.moveText(-f2, 0.0F);
    }
    this.lines = new ArrayList();
    return f1;
  }

  void writeLineToContent(PdfLine paramPdfLine, PdfContentByte paramPdfContentByte1, PdfContentByte paramPdfContentByte2, Object[] paramArrayOfObject, float paramFloat)
    throws DocumentException
  {
    PdfFont localPdfFont = (PdfFont)paramArrayOfObject[0];
    float f1 = ((Float)paramArrayOfObject[1]).floatValue();
    float f2 = 0.0F;
    float f3 = 1.0F;
    float f4 = (0.0F / 0.0F);
    float f5 = 0.0F;
    float f6 = 0.0F;
    float f7 = 0.0F;
    int i = paramPdfLine.numberOfSpaces();
    int j = paramPdfLine.GetLineLengthUtf32();
    int k = (paramPdfLine.hasToBeJustified()) && ((i != 0) || (j > 1)) ? 1 : 0;
    int m = paramPdfLine.getSeparatorCount();
    if (m > 0)
      f7 = paramPdfLine.widthLeft() / m;
    else if (k != 0)
      if ((paramPdfLine.isNewlineSplit()) && (paramPdfLine.widthLeft() >= f1 * (paramFloat * i + j - 1.0F)))
      {
        if (paramPdfLine.isRTL())
          paramPdfContentByte1.moveText(paramPdfLine.widthLeft() - f1 * (paramFloat * i + j - 1.0F), 0.0F);
        f5 = paramFloat * f1;
        f6 = f1;
      }
      else
      {
        float f8 = paramPdfLine.widthLeft();
        PdfChunk localPdfChunk2 = paramPdfLine.getChunk(paramPdfLine.size() - 1);
        if (localPdfChunk2 != null)
        {
          String str = localPdfChunk2.toString();
          int i2;
          if ((str.length() > 0) && (".,;:'".indexOf(i2 = str.charAt(str.length() - 1)) >= 0))
          {
            f11 = f8;
            f8 += localPdfChunk2.font().width(i2) * 0.4F;
            f2 = f8 - f11;
          }
        }
        f9 = f8 / (paramFloat * i + j - 1.0F);
        f5 = paramFloat * f9;
        f6 = f9;
        f1 = f9;
      }
    int n = paramPdfLine.getLastStrokeChunk();
    int i1 = 0;
    float f9 = paramPdfContentByte1.getXTLM();
    float f10 = f9;
    float f11 = paramPdfContentByte1.getYTLM();
    int i3 = 0;
    float f12 = 0.0F;
    Iterator localIterator = paramPdfLine.iterator();
    while (localIterator.hasNext())
    {
      PdfChunk localPdfChunk1 = (PdfChunk)localIterator.next();
      Color localColor = localPdfChunk1.color();
      f3 = 1.0F;
      Object localObject6;
      float f24;
      int i7;
      if (i1 <= n)
      {
        if (k != 0)
          f13 = localPdfChunk1.getWidthCorrected(f6, f5);
        else
          f13 = localPdfChunk1.width();
        if (localPdfChunk1.isStroked())
        {
          localObject1 = paramPdfLine.getChunk(i1 + 1);
          Object[] arrayOfObject;
          DrawInterface localDrawInterface;
          float f20;
          float f22;
          float f23;
          if (localPdfChunk1.isSeparator())
          {
            f13 = f7;
            arrayOfObject = (Object[])localPdfChunk1.getAttribute("SEPARATOR");
            localDrawInterface = (DrawInterface)arrayOfObject[0];
            Boolean localBoolean = (Boolean)arrayOfObject[1];
            f20 = localPdfChunk1.font().size();
            f22 = localPdfChunk1.font().getFont().getFontDescriptor(1, f20);
            f23 = localPdfChunk1.font().getFont().getFontDescriptor(3, f20);
            if (localBoolean.booleanValue())
              localDrawInterface.draw(paramPdfContentByte2, f10, f11 + f23, f10 + paramPdfLine.getOriginalWidth(), f22 - f23, f11);
            else
              localDrawInterface.draw(paramPdfContentByte2, f9, f11 + f23, f9 + f13, f22 - f23, f11);
          }
          float f18;
          if (localPdfChunk1.isTab())
          {
            arrayOfObject = (Object[])localPdfChunk1.getAttribute("TAB");
            localDrawInterface = (DrawInterface)arrayOfObject[0];
            f12 = ((Float)arrayOfObject[1]).floatValue() + ((Float)arrayOfObject[3]).floatValue();
            f18 = localPdfChunk1.font().size();
            f20 = localPdfChunk1.font().getFont().getFontDescriptor(1, f18);
            f22 = localPdfChunk1.font().getFont().getFontDescriptor(3, f18);
            if (f12 > f9)
              localDrawInterface.draw(paramPdfContentByte2, f9, f11 + f22, f12, f20 - f22, f11);
            f23 = f9;
            f9 = f12;
            f12 = f23;
          }
          float f14;
          float[] arrayOfFloat2;
          if (localPdfChunk1.isAttribute("BACKGROUND"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("BACKGROUND")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            float f15 = localPdfChunk1.font().size();
            f18 = localPdfChunk1.font().getFont().getFontDescriptor(1, f15);
            f20 = localPdfChunk1.font().getFont().getFontDescriptor(3, f15);
            localObject6 = (Object[])localPdfChunk1.getAttribute("BACKGROUND");
            paramPdfContentByte2.setColorFill((Color)localObject6[0]);
            arrayOfFloat2 = (float[])localObject6[1];
            paramPdfContentByte2.rectangle(f9 - arrayOfFloat2[0], f11 + f20 - arrayOfFloat2[1] + localPdfChunk1.getTextRise(), f13 - f14 + arrayOfFloat2[0] + arrayOfFloat2[2], f18 - f20 + arrayOfFloat2[1] + arrayOfFloat2[3]);
            paramPdfContentByte2.fill();
            paramPdfContentByte2.setGrayFill(0.0F);
          }
          Object localObject2;
          Object localObject3;
          if (localPdfChunk1.isAttribute("UNDERLINE"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("UNDERLINE")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            localObject2 = (Object[][])localPdfChunk1.getAttribute("UNDERLINE");
            localObject3 = null;
            for (int i5 = 0; i5 < localObject2.length; i5++)
            {
              localObject6 = localObject2[i5];
              localObject3 = (Color)localObject6[0];
              arrayOfFloat2 = (float[])localObject6[1];
              if (localObject3 == null)
                localObject3 = localColor;
              if (localObject3 != null)
                paramPdfContentByte2.setColorStroke((Color)localObject3);
              f24 = localPdfChunk1.font().size();
              paramPdfContentByte2.setLineWidth(arrayOfFloat2[0] + f24 * arrayOfFloat2[1]);
              float f25 = arrayOfFloat2[2] + f24 * arrayOfFloat2[3];
              i7 = (int)arrayOfFloat2[4];
              if (i7 != 0)
                paramPdfContentByte2.setLineCap(i7);
              paramPdfContentByte2.moveTo(f9, f11 + f25);
              paramPdfContentByte2.lineTo(f9 + f13 - f14, f11 + f25);
              paramPdfContentByte2.stroke();
              if (localObject3 != null)
                paramPdfContentByte2.resetGrayStroke();
              if (i7 == 0)
                continue;
              paramPdfContentByte2.setLineCap(0);
            }
            paramPdfContentByte2.setLineWidth(1.0F);
          }
          if (localPdfChunk1.isAttribute("ACTION"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("ACTION")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            paramPdfContentByte1.addAnnotation(new PdfAnnotation(this.writer, f9, f11, f9 + f13 - f14, f11 + localPdfChunk1.font().size(), (PdfAction)localPdfChunk1.getAttribute("ACTION")));
          }
          if (localPdfChunk1.isAttribute("REMOTEGOTO"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("REMOTEGOTO")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            localObject2 = (Object[])localPdfChunk1.getAttribute("REMOTEGOTO");
            localObject3 = (String)localObject2[0];
            if ((localObject2[1] instanceof String))
              remoteGoto((String)localObject3, (String)localObject2[1], f9, f11, f9 + f13 - f14, f11 + localPdfChunk1.font().size());
            else
              remoteGoto((String)localObject3, ((Integer)localObject2[1]).intValue(), f9, f11, f9 + f13 - f14, f11 + localPdfChunk1.font().size());
          }
          if (localPdfChunk1.isAttribute("LOCALGOTO"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("LOCALGOTO")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            localGoto((String)localPdfChunk1.getAttribute("LOCALGOTO"), f9, f11, f9 + f13 - f14, f11 + localPdfChunk1.font().size());
          }
          if (localPdfChunk1.isAttribute("LOCALDESTINATION"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("LOCALDESTINATION")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            localDestination((String)localPdfChunk1.getAttribute("LOCALDESTINATION"), new PdfDestination(0, f9, f11 + localPdfChunk1.font().size(), 0.0F));
          }
          if (localPdfChunk1.isAttribute("GENERICTAG"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("GENERICTAG")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            localObject2 = new Rectangle(f9, f11, f9 + f13 - f14, f11 + localPdfChunk1.font().size());
            localObject3 = this.writer.getPageEvent();
            if (localObject3 != null)
              ((PdfPageEvent)localObject3).onGenericTag(this.writer, this, (Rectangle)localObject2, (String)localPdfChunk1.getAttribute("GENERICTAG"));
          }
          float f19;
          float f21;
          if (localPdfChunk1.isAttribute("PDFANNOTATION"))
          {
            f14 = f1;
            if ((localObject1 != null) && (((PdfChunk)localObject1).isAttribute("PDFANNOTATION")))
              f14 = 0.0F;
            if (localObject1 == null)
              f14 += f2;
            float f16 = localPdfChunk1.font().size();
            f19 = localPdfChunk1.font().getFont().getFontDescriptor(1, f16);
            f21 = localPdfChunk1.font().getFont().getFontDescriptor(3, f16);
            localObject6 = PdfFormField.shallowDuplicate((PdfAnnotation)localPdfChunk1.getAttribute("PDFANNOTATION"));
            ((PdfAnnotation)localObject6).put(PdfName.RECT, new PdfRectangle(f9, f11 + f21, f9 + f13 - f14, f11 + f19));
            paramPdfContentByte1.addAnnotation((PdfAnnotation)localObject6);
          }
          float[] arrayOfFloat1 = (float[])localPdfChunk1.getAttribute("SKEW");
          Float localFloat = (Float)localPdfChunk1.getAttribute("HSCALE");
          if ((arrayOfFloat1 != null) || (localFloat != null))
          {
            f19 = 0.0F;
            f21 = 0.0F;
            if (arrayOfFloat1 != null)
            {
              f19 = arrayOfFloat1[0];
              f21 = arrayOfFloat1[1];
            }
            if (localFloat != null)
              f3 = localFloat.floatValue();
            paramPdfContentByte1.setTextMatrix(f3, f19, f21, 1.0F, f9, f11);
          }
          if (localPdfChunk1.isImage())
          {
            localObject4 = localPdfChunk1.getImage();
            localObject5 = ((Image)localObject4).matrix();
            localObject5[4] = (f9 + localPdfChunk1.getImageOffsetX() - localObject5[4]);
            localObject5[5] = (f11 + localPdfChunk1.getImageOffsetY() - localObject5[5]);
            paramPdfContentByte2.addImage((Image)localObject4, localObject5[0], localObject5[1], localObject5[2], localObject5[3], localObject5[4], localObject5[5]);
            paramPdfContentByte1.moveText(f9 + f1 + ((Image)localObject4).getScaledWidth() - paramPdfContentByte1.getXTLM(), 0.0F);
          }
        }
        f9 += f13;
        i1++;
      }
      if (localPdfChunk1.font().compareTo(localPdfFont) != 0)
      {
        localPdfFont = localPdfChunk1.font();
        paramPdfContentByte1.setFontAndSize(localPdfFont.getFont(), localPdfFont.size());
      }
      float f13 = 0.0F;
      Object localObject1 = (Object[])localPdfChunk1.getAttribute("TEXTRENDERMODE");
      int i4 = 0;
      float f17 = 1.0F;
      Object localObject4 = null;
      Object localObject5 = (Float)localPdfChunk1.getAttribute("SUBSUPSCRIPT");
      if (localObject1 != null)
      {
        i4 = ((Integer)localObject1[0]).intValue() & 0x3;
        if (i4 != 0)
          paramPdfContentByte1.setTextRenderingMode(i4);
        if ((i4 == 1) || (i4 == 2))
        {
          f17 = ((Float)localObject1[1]).floatValue();
          if (f17 != 1.0F)
            paramPdfContentByte1.setLineWidth(f17);
          localObject4 = (Color)localObject1[2];
          if (localObject4 == null)
            localObject4 = localColor;
          if (localObject4 != null)
            paramPdfContentByte1.setColorStroke((Color)localObject4);
        }
      }
      if (localObject5 != null)
        f13 = ((Float)localObject5).floatValue();
      if (localColor != null)
        paramPdfContentByte1.setColorFill(localColor);
      if (f13 != 0.0F)
        paramPdfContentByte1.setTextRise(f13);
      if (localPdfChunk1.isImage())
      {
        i3 = 1;
      }
      else if (localPdfChunk1.isHorizontalSeparator())
      {
        localObject6 = new PdfTextArray();
        ((PdfTextArray)localObject6).add(-f7 * 1000.0F / localPdfChunk1.font.size() / f3);
        paramPdfContentByte1.showText((PdfTextArray)localObject6);
      }
      else if (localPdfChunk1.isTab())
      {
        localObject6 = new PdfTextArray();
        ((PdfTextArray)localObject6).add((f12 - f9) * 1000.0F / localPdfChunk1.font.size() / f3);
        paramPdfContentByte1.showText((PdfTextArray)localObject6);
      }
      else if ((k != 0) && (i > 0) && (localPdfChunk1.isSpecialEncoding()))
      {
        if (f3 != f4)
        {
          f4 = f3;
          paramPdfContentByte1.setWordSpacing(f5 / f3);
          paramPdfContentByte1.setCharacterSpacing(f6 / f3);
        }
        localObject6 = localPdfChunk1.toString();
        int i6 = ((String)localObject6).indexOf(' ');
        if (i6 < 0)
        {
          paramPdfContentByte1.showText((String)localObject6);
        }
        else
        {
          f24 = -f5 * 1000.0F / localPdfChunk1.font.size() / f3;
          PdfTextArray localPdfTextArray = new PdfTextArray(((String)localObject6).substring(0, i6));
          for (i7 = i6; (i6 = ((String)localObject6).indexOf(' ', i7 + 1)) >= 0; i7 = i6)
          {
            localPdfTextArray.add(f24);
            localPdfTextArray.add(((String)localObject6).substring(i7, i6));
          }
          localPdfTextArray.add(f24);
          localPdfTextArray.add(((String)localObject6).substring(i7));
          paramPdfContentByte1.showText(localPdfTextArray);
        }
      }
      else
      {
        if ((k != 0) && (f3 != f4))
        {
          f4 = f3;
          paramPdfContentByte1.setWordSpacing(f5 / f3);
          paramPdfContentByte1.setCharacterSpacing(f6 / f3);
        }
        paramPdfContentByte1.showText(localPdfChunk1.toString());
      }
      if (f13 != 0.0F)
        paramPdfContentByte1.setTextRise(0.0F);
      if (localColor != null)
        paramPdfContentByte1.resetRGBColorFill();
      if (i4 != 0)
        paramPdfContentByte1.setTextRenderingMode(0);
      if (localObject4 != null)
        paramPdfContentByte1.resetRGBColorStroke();
      if (f17 != 1.0F)
        paramPdfContentByte1.setLineWidth(1.0F);
      if ((!localPdfChunk1.isAttribute("SKEW")) && (!localPdfChunk1.isAttribute("HSCALE")))
        continue;
      i3 = 1;
      paramPdfContentByte1.setTextMatrix(f9, f11);
    }
    if (k != 0)
    {
      paramPdfContentByte1.setWordSpacing(0.0F);
      paramPdfContentByte1.setCharacterSpacing(0.0F);
      if (paramPdfLine.isNewlineSplit())
        f1 = 0.0F;
    }
    if (i3 != 0)
      paramPdfContentByte1.moveText(f10 - paramPdfContentByte1.getXTLM(), 0.0F);
    paramArrayOfObject[0] = localPdfFont;
    paramArrayOfObject[1] = new Float(f1);
  }

  protected float indentLeft()
  {
    return left(this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.imageIndentLeft + this.indentation.sectionIndentLeft);
  }

  protected float indentRight()
  {
    return right(this.indentation.indentRight + this.indentation.sectionIndentRight + this.indentation.imageIndentRight);
  }

  protected float indentTop()
  {
    return top(this.indentation.indentTop);
  }

  float indentBottom()
  {
    return bottom(this.indentation.indentBottom);
  }

  protected void addSpacing(float paramFloat1, float paramFloat2, Font paramFont)
  {
    if (paramFloat1 == 0.0F)
      return;
    if (this.pageEmpty)
      return;
    if (this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom())
      return;
    this.leading = paramFloat1;
    carriageReturn();
    if ((paramFont.isUnderlined()) || (paramFont.isStrikethru()))
    {
      paramFont = new Font(paramFont);
      int i = paramFont.getStyle();
      i &= -5;
      i &= -9;
      paramFont.setStyle(i);
    }
    Chunk localChunk = new Chunk(" ", paramFont);
    localChunk.process(this);
    carriageReturn();
    this.leading = paramFloat2;
  }

  PdfInfo getInfo()
  {
    return this.info;
  }

  PdfCatalog getCatalog(PdfIndirectReference paramPdfIndirectReference)
  {
    PdfCatalog localPdfCatalog = new PdfCatalog(paramPdfIndirectReference, this.writer);
    if (this.rootOutline.getKids().size() > 0)
    {
      localPdfCatalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
      localPdfCatalog.put(PdfName.OUTLINES, this.rootOutline.indirectReference());
    }
    this.writer.getPdfVersion().addToCatalog(localPdfCatalog);
    this.viewerPreferences.addToCatalog(localPdfCatalog);
    if (this.pageLabels != null)
      localPdfCatalog.put(PdfName.PAGELABELS, this.pageLabels.getDictionary(this.writer));
    localPdfCatalog.addNames(this.localDestinations, getDocumentLevelJS(), this.documentFileAttachment, this.writer);
    if (this.openActionName != null)
    {
      PdfAction localPdfAction = getLocalGotoAction(this.openActionName);
      localPdfCatalog.setOpenAction(localPdfAction);
    }
    else if (this.openActionAction != null)
    {
      localPdfCatalog.setOpenAction(this.openActionAction);
    }
    if (this.additionalActions != null)
      localPdfCatalog.setAdditionalActions(this.additionalActions);
    if (this.collection != null)
      localPdfCatalog.put(PdfName.COLLECTION, this.collection);
    if (this.annotationsImp.hasValidAcroForm())
      try
      {
        localPdfCatalog.put(PdfName.ACROFORM, this.writer.addToBody(this.annotationsImp.getAcroForm()).getIndirectReference());
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
    return localPdfCatalog;
  }

  void addOutline(PdfOutline paramPdfOutline, String paramString)
  {
    localDestination(paramString, paramPdfOutline.getPdfDestination());
  }

  public PdfOutline getRootOutline()
  {
    return this.rootOutline;
  }

  void calculateOutlineCount()
  {
    if (this.rootOutline.getKids().size() == 0)
      return;
    traverseOutlineCount(this.rootOutline);
  }

  void traverseOutlineCount(PdfOutline paramPdfOutline)
  {
    ArrayList localArrayList = paramPdfOutline.getKids();
    PdfOutline localPdfOutline = paramPdfOutline.parent();
    if (localArrayList.isEmpty())
    {
      if (localPdfOutline != null)
        localPdfOutline.setCount(localPdfOutline.getCount() + 1);
    }
    else
    {
      for (int i = 0; i < localArrayList.size(); i++)
        traverseOutlineCount((PdfOutline)localArrayList.get(i));
      if (localPdfOutline != null)
        if (paramPdfOutline.isOpen())
        {
          localPdfOutline.setCount(paramPdfOutline.getCount() + localPdfOutline.getCount() + 1);
        }
        else
        {
          localPdfOutline.setCount(localPdfOutline.getCount() + 1);
          paramPdfOutline.setCount(-paramPdfOutline.getCount());
        }
    }
  }

  void writeOutlines()
    throws IOException
  {
    if (this.rootOutline.getKids().size() == 0)
      return;
    outlineTree(this.rootOutline);
    this.writer.addToBody(this.rootOutline, this.rootOutline.indirectReference());
  }

  void outlineTree(PdfOutline paramPdfOutline)
    throws IOException
  {
    paramPdfOutline.setIndirectReference(this.writer.getPdfIndirectReference());
    if (paramPdfOutline.parent() != null)
      paramPdfOutline.put(PdfName.PARENT, paramPdfOutline.parent().indirectReference());
    ArrayList localArrayList = paramPdfOutline.getKids();
    int i = localArrayList.size();
    for (int j = 0; j < i; j++)
      outlineTree((PdfOutline)localArrayList.get(j));
    for (j = 0; j < i; j++)
    {
      if (j > 0)
        ((PdfOutline)localArrayList.get(j)).put(PdfName.PREV, ((PdfOutline)localArrayList.get(j - 1)).indirectReference());
      if (j >= i - 1)
        continue;
      ((PdfOutline)localArrayList.get(j)).put(PdfName.NEXT, ((PdfOutline)localArrayList.get(j + 1)).indirectReference());
    }
    if (i > 0)
    {
      paramPdfOutline.put(PdfName.FIRST, ((PdfOutline)localArrayList.get(0)).indirectReference());
      paramPdfOutline.put(PdfName.LAST, ((PdfOutline)localArrayList.get(i - 1)).indirectReference());
    }
    for (j = 0; j < i; j++)
    {
      PdfOutline localPdfOutline = (PdfOutline)localArrayList.get(j);
      this.writer.addToBody(localPdfOutline, localPdfOutline.indirectReference());
    }
  }

  void setViewerPreferences(int paramInt)
  {
    this.viewerPreferences.setViewerPreferences(paramInt);
  }

  void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    this.viewerPreferences.addViewerPreference(paramPdfName, paramPdfObject);
  }

  void setPageLabels(PdfPageLabels paramPdfPageLabels)
  {
    this.pageLabels = paramPdfPageLabels;
  }

  void localGoto(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfAction localPdfAction = getLocalGotoAction(paramString);
    this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, localPdfAction));
  }

  void remoteGoto(String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, new PdfAction(paramString1, paramString2)));
  }

  void remoteGoto(String paramString, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    addAnnotation(new PdfAnnotation(this.writer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, new PdfAction(paramString, paramInt)));
  }

  void setAction(PdfAction paramPdfAction, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    addAnnotation(new PdfAnnotation(this.writer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramPdfAction));
  }

  PdfAction getLocalGotoAction(String paramString)
  {
    Object[] arrayOfObject = (Object[])this.localDestinations.get(paramString);
    if (arrayOfObject == null)
      arrayOfObject = new Object[3];
    PdfAction localPdfAction;
    if (arrayOfObject[0] == null)
    {
      if (arrayOfObject[1] == null)
        arrayOfObject[1] = this.writer.getPdfIndirectReference();
      localPdfAction = new PdfAction((PdfIndirectReference)arrayOfObject[1]);
      arrayOfObject[0] = localPdfAction;
      this.localDestinations.put(paramString, arrayOfObject);
    }
    else
    {
      localPdfAction = (PdfAction)arrayOfObject[0];
    }
    return localPdfAction;
  }

  boolean localDestination(String paramString, PdfDestination paramPdfDestination)
  {
    Object[] arrayOfObject = (Object[])this.localDestinations.get(paramString);
    if (arrayOfObject == null)
      arrayOfObject = new Object[3];
    if (arrayOfObject[2] != null)
      return false;
    arrayOfObject[2] = paramPdfDestination;
    this.localDestinations.put(paramString, arrayOfObject);
    paramPdfDestination.addPage(this.writer.getCurrentPage());
    return true;
  }

  void addJavaScript(PdfAction paramPdfAction)
  {
    if (paramPdfAction.get(PdfName.JS) == null)
      throw new RuntimeException("Only JavaScript actions are allowed.");
    try
    {
      this.documentLevelJS.put(SIXTEEN_DIGITS.format(this.jsCounter++), this.writer.addToBody(paramPdfAction).getIndirectReference());
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  void addJavaScript(String paramString, PdfAction paramPdfAction)
  {
    if (paramPdfAction.get(PdfName.JS) == null)
      throw new RuntimeException("Only JavaScript actions are allowed.");
    try
    {
      this.documentLevelJS.put(paramString, this.writer.addToBody(paramPdfAction).getIndirectReference());
    }
    catch (IOException localIOException)
    {
      throw new ExceptionConverter(localIOException);
    }
  }

  HashMap getDocumentLevelJS()
  {
    return this.documentLevelJS;
  }

  void addFileAttachment(String paramString, PdfFileSpecification paramPdfFileSpecification)
    throws IOException
  {
    if (paramString == null)
    {
      localObject = (PdfString)paramPdfFileSpecification.get(PdfName.DESC);
      if (localObject == null)
        paramString = "";
      else
        paramString = PdfEncodings.convertToString(((PdfString)localObject).getBytes(), null);
    }
    paramPdfFileSpecification.addDescription(paramString, true);
    if (paramString.length() == 0)
      paramString = "Unnamed";
    Object localObject = PdfEncodings.convertToString(new PdfString(paramString, "UnicodeBig").getBytes(), null);
    int i = 0;
    while (this.documentFileAttachment.containsKey(localObject))
    {
      i++;
      localObject = PdfEncodings.convertToString(new PdfString(paramString + " " + i, "UnicodeBig").getBytes(), null);
    }
    this.documentFileAttachment.put(localObject, paramPdfFileSpecification.getReference());
  }

  HashMap getDocumentFileAttachment()
  {
    return this.documentFileAttachment;
  }

  void setOpenAction(String paramString)
  {
    this.openActionName = paramString;
    this.openActionAction = null;
  }

  void setOpenAction(PdfAction paramPdfAction)
  {
    this.openActionAction = paramPdfAction;
    this.openActionName = null;
  }

  void addAdditionalAction(PdfName paramPdfName, PdfAction paramPdfAction)
  {
    if (this.additionalActions == null)
      this.additionalActions = new PdfDictionary();
    if (paramPdfAction == null)
      this.additionalActions.remove(paramPdfName);
    else
      this.additionalActions.put(paramPdfName, paramPdfAction);
    if (this.additionalActions.size() == 0)
      this.additionalActions = null;
  }

  public void setCollection(PdfCollection paramPdfCollection)
  {
    this.collection = paramPdfCollection;
  }

  PdfAcroForm getAcroForm()
  {
    return this.annotationsImp.getAcroForm();
  }

  void setSigFlags(int paramInt)
  {
    this.annotationsImp.setSigFlags(paramInt);
  }

  void addCalculationOrder(PdfFormField paramPdfFormField)
  {
    this.annotationsImp.addCalculationOrder(paramPdfFormField);
  }

  void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    this.pageEmpty = false;
    this.annotationsImp.addAnnotation(paramPdfAnnotation);
  }

  int getMarkPoint()
  {
    return this.markPoint;
  }

  void incMarkPoint()
  {
    this.markPoint += 1;
  }

  void setCropBoxSize(Rectangle paramRectangle)
  {
    setBoxSize("crop", paramRectangle);
  }

  void setBoxSize(String paramString, Rectangle paramRectangle)
  {
    if (paramRectangle == null)
      this.boxSize.remove(paramString);
    else
      this.boxSize.put(paramString, new PdfRectangle(paramRectangle));
  }

  protected void setNewPageSizeAndMargins()
  {
    this.pageSize = this.nextPageSize;
    if ((this.marginMirroring) && ((getPageNumber() & 0x1) == 0))
    {
      this.marginRight = this.nextMarginLeft;
      this.marginLeft = this.nextMarginRight;
    }
    else
    {
      this.marginLeft = this.nextMarginLeft;
      this.marginRight = this.nextMarginRight;
    }
    if ((this.marginMirroringTopBottom) && ((getPageNumber() & 0x1) == 0))
    {
      this.marginTop = this.nextMarginBottom;
      this.marginBottom = this.nextMarginTop;
    }
    else
    {
      this.marginTop = this.nextMarginTop;
      this.marginBottom = this.nextMarginBottom;
    }
  }

  Rectangle getBoxSize(String paramString)
  {
    PdfRectangle localPdfRectangle = (PdfRectangle)this.thisBoxSize.get(paramString);
    if (localPdfRectangle != null)
      return localPdfRectangle.getRectangle();
    return null;
  }

  void setPageEmpty(boolean paramBoolean)
  {
    this.pageEmpty = paramBoolean;
  }

  void setDuration(int paramInt)
  {
    if (paramInt > 0)
      this.duration = paramInt;
    else
      this.duration = -1;
  }

  void setTransition(PdfTransition paramPdfTransition)
  {
    this.transition = paramPdfTransition;
  }

  void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction)
  {
    if (this.pageAA == null)
      this.pageAA = new PdfDictionary();
    this.pageAA.put(paramPdfName, paramPdfAction);
  }

  void setThumbnail(Image paramImage)
    throws PdfException, DocumentException
  {
    this.thumb = this.writer.getImageReference(this.writer.addDirectImageSimple(paramImage));
  }

  PageResources getPageResources()
  {
    return this.pageResources;
  }

  boolean isStrictImageSequence()
  {
    return this.strictImageSequence;
  }

  void setStrictImageSequence(boolean paramBoolean)
  {
    this.strictImageSequence = paramBoolean;
  }

  public void clearTextWrap()
  {
    float f = this.imageEnd - this.currentHeight;
    if (this.line != null)
      f += this.line.height();
    if ((this.imageEnd > -1.0F) && (f > 0.0F))
    {
      carriageReturn();
      this.currentHeight += f;
    }
  }

  protected void add(Image paramImage)
    throws PdfException, DocumentException
  {
    if (paramImage.hasAbsoluteY())
    {
      this.graphics.addImage(paramImage);
      this.pageEmpty = false;
      return;
    }
    if ((this.currentHeight != 0.0F) && (indentTop() - this.currentHeight - paramImage.getScaledHeight() < indentBottom()))
    {
      if ((!this.strictImageSequence) && (this.imageWait == null))
      {
        this.imageWait = paramImage;
        return;
      }
      newPage();
      if ((this.currentHeight != 0.0F) && (indentTop() - this.currentHeight - paramImage.getScaledHeight() < indentBottom()))
      {
        this.imageWait = paramImage;
        return;
      }
    }
    this.pageEmpty = false;
    if (paramImage == this.imageWait)
      this.imageWait = null;
    int i = ((paramImage.getAlignment() & 0x4) == 4) && ((paramImage.getAlignment() & 0x1) != 1) ? 1 : 0;
    int j = (paramImage.getAlignment() & 0x8) == 8 ? 1 : 0;
    float f1 = this.leading / 2.0F;
    if (i != 0)
      f1 += this.leading;
    float f2 = indentTop() - this.currentHeight - paramImage.getScaledHeight() - f1;
    float[] arrayOfFloat = paramImage.matrix();
    float f3 = indentLeft() - arrayOfFloat[4];
    if ((paramImage.getAlignment() & 0x2) == 2)
      f3 = indentRight() - paramImage.getScaledWidth() - arrayOfFloat[4];
    if ((paramImage.getAlignment() & 0x1) == 1)
      f3 = indentLeft() + (indentRight() - indentLeft() - paramImage.getScaledWidth()) / 2.0F - arrayOfFloat[4];
    if (paramImage.hasAbsoluteX())
      f3 = paramImage.getAbsoluteX();
    if (i != 0)
    {
      if ((this.imageEnd < 0.0F) || (this.imageEnd < this.currentHeight + paramImage.getScaledHeight() + f1))
        this.imageEnd = (this.currentHeight + paramImage.getScaledHeight() + f1);
      if ((paramImage.getAlignment() & 0x2) == 2)
        this.indentation.imageIndentRight += paramImage.getScaledWidth() + paramImage.getIndentationLeft();
      else
        this.indentation.imageIndentLeft += paramImage.getScaledWidth() + paramImage.getIndentationRight();
    }
    else if ((paramImage.getAlignment() & 0x2) == 2)
    {
      f3 -= paramImage.getIndentationRight();
    }
    else if ((paramImage.getAlignment() & 0x1) == 1)
    {
      f3 += paramImage.getIndentationLeft() - paramImage.getIndentationRight();
    }
    else
    {
      f3 += paramImage.getIndentationLeft();
    }
    this.graphics.addImage(paramImage, arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], f3, f2 - arrayOfFloat[5]);
    if ((i == 0) && (j == 0))
    {
      this.currentHeight += paramImage.getScaledHeight() + f1;
      flushLines();
      this.text.moveText(0.0F, -(paramImage.getScaledHeight() + f1));
      newLine();
    }
  }

  void addPTable(PdfPTable paramPdfPTable)
    throws DocumentException
  {
    ColumnText localColumnText = new ColumnText(this.writer.getDirectContent());
    if ((paramPdfPTable.getKeepTogether()) && (!fitsPage(paramPdfPTable, 0.0F)) && (this.currentHeight > 0.0F))
      newPage();
    if (this.currentHeight > 0.0F)
    {
      Paragraph localParagraph = new Paragraph();
      localParagraph.setLeading(0.0F);
      localColumnText.addElement(localParagraph);
    }
    localColumnText.addElement(paramPdfPTable);
    boolean bool = paramPdfPTable.isHeadersInEvent();
    paramPdfPTable.setHeadersInEvent(true);
    int i = 0;
    while (true)
    {
      localColumnText.setSimpleColumn(indentLeft(), indentBottom(), indentRight(), indentTop() - this.currentHeight);
      int j = localColumnText.go();
      if ((j & 0x1) != 0)
      {
        this.text.moveText(0.0F, localColumnText.getYLine() - indentTop() + this.currentHeight);
        this.currentHeight = (indentTop() - localColumnText.getYLine());
        break;
      }
      if (indentTop() - this.currentHeight == localColumnText.getYLine())
        i++;
      else
        i = 0;
      if (i == 3)
      {
        add(new Paragraph("ERROR: Infinite table loop"));
        break;
      }
      newPage();
    }
    paramPdfPTable.setHeadersInEvent(bool);
  }

  boolean fitsPage(PdfPTable paramPdfPTable, float paramFloat)
  {
    if (!paramPdfPTable.isLockedWidth())
    {
      float f = (indentRight() - indentLeft()) * paramPdfPTable.getWidthPercentage() / 100.0F;
      paramPdfPTable.setTotalWidth(f);
    }
    ensureNewLine();
    return paramPdfPTable.getTotalHeight() + (this.currentHeight > 0.0F ? paramPdfPTable.spacingBefore() : 0.0F) <= indentTop() - this.currentHeight - indentBottom() - paramFloat;
  }

  private void addPdfTable(Table paramTable)
    throws DocumentException
  {
    flushLines();
    PdfTable localPdfTable = new PdfTable(paramTable, indentLeft(), indentRight(), indentTop() - this.currentHeight);
    RenderingContext localRenderingContext = new RenderingContext();
    localRenderingContext.pagetop = indentTop();
    localRenderingContext.oldHeight = this.currentHeight;
    localRenderingContext.cellGraphics = new PdfContentByte(this.writer);
    localRenderingContext.rowspanMap = new HashMap();
    localRenderingContext.table = localPdfTable;
    ArrayList localArrayList1 = localPdfTable.getHeaderCells();
    ArrayList localArrayList2 = localPdfTable.getCells();
    ArrayList localArrayList3 = extractRows(localArrayList2, localRenderingContext);
    int i = 0;
    while (!localArrayList2.isEmpty())
    {
      localRenderingContext.lostTableBottom = 0.0F;
      int j = 0;
      Iterator localIterator1 = localArrayList3.iterator();
      for (int k = 0; localIterator1.hasNext(); k = 1)
      {
        localObject1 = (ArrayList)localIterator1.next();
        analyzeRow(localArrayList3, localRenderingContext);
        renderCells(localRenderingContext, (java.util.List)localObject1, localPdfTable.hasToFitPageCells() & k);
        if (!mayBeRemoved((ArrayList)localObject1))
          break;
        consumeRowspan((ArrayList)localObject1, localRenderingContext);
        localIterator1.remove();
      }
      localArrayList2.clear();
      Object localObject1 = new HashSet();
      localIterator1 = localArrayList3.iterator();
      PdfCell localPdfCell;
      while (localIterator1.hasNext())
      {
        localObject2 = (ArrayList)localIterator1.next();
        localObject3 = ((ArrayList)localObject2).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localPdfCell = (PdfCell)((Iterator)localObject3).next();
          if (((Set)localObject1).contains(localPdfCell))
            continue;
          localArrayList2.add(localPdfCell);
          ((Set)localObject1).add(localPdfCell);
        }
      }
      Object localObject2 = new Rectangle(localPdfTable);
      ((Rectangle)localObject2).setBorder(localPdfTable.getBorder());
      ((Rectangle)localObject2).setBorderWidth(localPdfTable.getBorderWidth());
      ((Rectangle)localObject2).setBorderColor(localPdfTable.getBorderColor());
      ((Rectangle)localObject2).setBackgroundColor(localPdfTable.getBackgroundColor());
      Object localObject3 = this.writer.getDirectContentUnder();
      ((PdfContentByte)localObject3).rectangle(((Rectangle)localObject2).rectangle(top(), indentBottom()));
      ((PdfContentByte)localObject3).add(localRenderingContext.cellGraphics);
      ((Rectangle)localObject2).setBackgroundColor(null);
      localObject2 = ((Rectangle)localObject2).rectangle(top(), indentBottom());
      ((Rectangle)localObject2).setBorder(localPdfTable.getBorder());
      ((PdfContentByte)localObject3).rectangle((Rectangle)localObject2);
      localRenderingContext.cellGraphics = new PdfContentByte(null);
      if (localArrayList3.isEmpty())
        continue;
      i = 1;
      this.graphics.setLineWidth(localPdfTable.getBorderWidth());
      if ((j != 0) && ((localPdfTable.getBorder() & 0x2) == 2))
      {
        Color localColor = localPdfTable.getBorderColor();
        if (localColor != null)
          this.graphics.setColorStroke(localColor);
        this.graphics.moveTo(localPdfTable.getLeft(), Math.max(localPdfTable.getBottom(), indentBottom()));
        this.graphics.lineTo(localPdfTable.getRight(), Math.max(localPdfTable.getBottom(), indentBottom()));
        this.graphics.stroke();
        if (localColor != null)
          this.graphics.resetRGBColorStroke();
      }
      this.pageEmpty = false;
      float f2 = localRenderingContext.lostTableBottom;
      newPage();
      float f3 = 0.0F;
      int m = 0;
      if (this.currentHeight > 0.0F)
      {
        f3 = 6.0F;
        this.currentHeight += f3;
        m = 1;
        newLine();
        flushLines();
        this.indentation.indentTop = (this.currentHeight - this.leading);
        this.currentHeight = 0.0F;
      }
      else
      {
        flushLines();
      }
      int n = localArrayList1.size();
      if (n > 0)
      {
        localPdfCell = (PdfCell)localArrayList1.get(0);
        float f4 = localPdfCell.getTop(0.0F);
        for (int i2 = 0; i2 < n; i2++)
        {
          localPdfCell = (PdfCell)localArrayList1.get(i2);
          localPdfCell.setTop(indentTop() - f4 + localPdfCell.getTop(0.0F));
          localPdfCell.setBottom(indentTop() - f4 + localPdfCell.getBottom(0.0F));
          localRenderingContext.pagetop = localPdfCell.getBottom();
          localRenderingContext.cellGraphics.rectangle(localPdfCell.rectangle(indentTop(), indentBottom()));
          ArrayList localArrayList4 = localPdfCell.getImages(indentTop(), indentBottom());
          Iterator localIterator2 = localArrayList4.iterator();
          while (localIterator2.hasNext())
          {
            j = 1;
            Image localImage = (Image)localIterator2.next();
            this.graphics.addImage(localImage);
          }
          this.lines = localPdfCell.getLines(indentTop(), indentBottom());
          float f7 = localPdfCell.getTop(indentTop());
          this.text.moveText(0.0F, f7 - f3);
          float f8 = flushLines() - f7 + f3;
          this.text.moveText(0.0F, f8);
        }
        this.currentHeight = (indentTop() - localRenderingContext.pagetop + localPdfTable.cellspacing());
        this.text.moveText(0.0F, localRenderingContext.pagetop - indentTop() - this.currentHeight);
      }
      else if (m != 0)
      {
        localRenderingContext.pagetop = indentTop();
        this.text.moveText(0.0F, -localPdfTable.cellspacing());
      }
      localRenderingContext.oldHeight = (this.currentHeight - f3);
      n = Math.min(localArrayList2.size(), localPdfTable.columns());
      float f5;
      float f6;
      for (int i1 = 0; i1 < n; i1++)
      {
        localPdfCell = (PdfCell)localArrayList2.get(i1);
        if (localPdfCell.getTop(-localPdfTable.cellspacing()) <= localRenderingContext.lostTableBottom)
          continue;
        f5 = localRenderingContext.pagetop - f2 + localPdfCell.getBottom();
        f6 = localPdfCell.remainingHeight();
        if (f5 <= localRenderingContext.pagetop - f6)
          continue;
        f2 += f5 - (localRenderingContext.pagetop - f6);
      }
      n = localArrayList2.size();
      localPdfTable.setTop(indentTop());
      localPdfTable.setBottom(localRenderingContext.pagetop - f2 + localPdfTable.getBottom(localPdfTable.cellspacing()));
      for (i1 = 0; i1 < n; i1++)
      {
        localPdfCell = (PdfCell)localArrayList2.get(i1);
        f5 = localRenderingContext.pagetop - f2 + localPdfCell.getBottom();
        f6 = localRenderingContext.pagetop - f2 + localPdfCell.getTop(-localPdfTable.cellspacing());
        if (f6 > indentTop() - this.currentHeight)
          f6 = indentTop() - this.currentHeight;
        localPdfCell.setTop(f6);
        localPdfCell.setBottom(f5);
      }
    }
    float f1 = localPdfTable.getTop() - localPdfTable.getBottom();
    if (i != 0)
    {
      this.currentHeight = f1;
      this.text.moveText(0.0F, -(f1 - localRenderingContext.oldHeight * 2.0F));
    }
    else
    {
      this.currentHeight = (localRenderingContext.oldHeight + f1);
      this.text.moveText(0.0F, -f1);
    }
    this.pageEmpty = false;
  }

  protected void analyzeRow(ArrayList paramArrayList, RenderingContext paramRenderingContext)
  {
    paramRenderingContext.maxCellBottom = indentBottom();
    int i = 0;
    ArrayList localArrayList = (ArrayList)paramArrayList.get(i);
    int j = 1;
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      PdfCell localPdfCell1 = (PdfCell)localIterator.next();
      j = Math.max(paramRenderingContext.currentRowspan(localPdfCell1), j);
    }
    i += j;
    int k = 1;
    if (i == paramArrayList.size())
    {
      i = paramArrayList.size() - 1;
      k = 0;
    }
    if ((i < 0) || (i >= paramArrayList.size()))
      return;
    localArrayList = (ArrayList)paramArrayList.get(i);
    localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      PdfCell localPdfCell2 = (PdfCell)localIterator.next();
      Rectangle localRectangle = localPdfCell2.rectangle(paramRenderingContext.pagetop, indentBottom());
      if (k != 0)
      {
        paramRenderingContext.maxCellBottom = Math.max(paramRenderingContext.maxCellBottom, localRectangle.getTop());
        continue;
      }
      if (paramRenderingContext.currentRowspan(localPdfCell2) != 1)
        continue;
      paramRenderingContext.maxCellBottom = Math.max(paramRenderingContext.maxCellBottom, localRectangle.getBottom());
    }
  }

  protected boolean mayBeRemoved(ArrayList paramArrayList)
  {
    Iterator localIterator = paramArrayList.iterator();
    boolean bool = true;
    while (localIterator.hasNext())
    {
      PdfCell localPdfCell = (PdfCell)localIterator.next();
      bool &= localPdfCell.mayBeRemoved();
    }
    return bool;
  }

  protected void consumeRowspan(ArrayList paramArrayList, RenderingContext paramRenderingContext)
  {
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      PdfCell localPdfCell = (PdfCell)localIterator.next();
      paramRenderingContext.consumeRowspan(localPdfCell);
    }
  }

  protected ArrayList extractRows(ArrayList paramArrayList, RenderingContext paramRenderingContext)
  {
    Object localObject = null;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator = paramArrayList.iterator();
    int k;
    while (localIterator.hasNext())
    {
      PdfCell localPdfCell1 = (PdfCell)localIterator.next();
      i = 0;
      int j = !localIterator.hasNext() ? 1 : 0;
      k = !localIterator.hasNext() ? 1 : 0;
      if ((localObject != null) && (localPdfCell1.getLeft() <= localObject.getLeft()))
      {
        j = 1;
        k = 0;
      }
      if (k != 0)
      {
        localArrayList2.add(localPdfCell1);
        i = 1;
      }
      if (j != 0)
      {
        if (!localArrayList2.isEmpty())
          localArrayList1.add(localArrayList2);
        localArrayList2 = new ArrayList();
      }
      if (i == 0)
        localArrayList2.add(localPdfCell1);
      localObject = localPdfCell1;
    }
    if (!localArrayList2.isEmpty())
      localArrayList1.add(localArrayList2);
    for (int i = localArrayList1.size() - 1; i >= 0; i--)
    {
      ArrayList localArrayList3 = (ArrayList)localArrayList1.get(i);
      for (k = 0; k < localArrayList3.size(); k++)
      {
        PdfCell localPdfCell2 = (PdfCell)localArrayList3.get(k);
        int m = localPdfCell2.rowspan();
        for (int n = 1; (n < m) && (localArrayList1.size() < i + n); n++)
        {
          ArrayList localArrayList4 = (ArrayList)localArrayList1.get(i + n);
          if (localArrayList4.size() <= k)
            continue;
          localArrayList4.add(k, localPdfCell2);
        }
      }
    }
    return localArrayList1;
  }

  protected void renderCells(RenderingContext paramRenderingContext, java.util.List paramList, boolean paramBoolean)
    throws DocumentException
  {
    PdfCell localPdfCell;
    if (paramBoolean)
    {
      localIterator1 = paramList.iterator();
      while (localIterator1.hasNext())
      {
        localPdfCell = (PdfCell)localIterator1.next();
        if ((!localPdfCell.isHeader()) && (localPdfCell.getBottom() < indentBottom()))
          return;
      }
    }
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      localPdfCell = (PdfCell)localIterator1.next();
      if (paramRenderingContext.isCellRenderedOnPage(localPdfCell, getPageNumber()))
        continue;
      float f1 = 0.0F;
      if (paramRenderingContext.numCellRendered(localPdfCell) >= 1)
        f1 = 1.0F;
      this.lines = localPdfCell.getLines(paramRenderingContext.pagetop, indentBottom() - f1);
      if ((this.lines != null) && (!this.lines.isEmpty()))
      {
        f2 = localPdfCell.getTop(paramRenderingContext.pagetop - paramRenderingContext.oldHeight);
        this.text.moveText(0.0F, f2);
        float f3 = flushLines() - f2;
        this.text.moveText(0.0F, f3);
        if (paramRenderingContext.oldHeight + f3 > this.currentHeight)
          this.currentHeight = (paramRenderingContext.oldHeight + f3);
        paramRenderingContext.cellRendered(localPdfCell, getPageNumber());
      }
      float f2 = Math.max(localPdfCell.getBottom(), indentBottom());
      Rectangle localRectangle1 = paramRenderingContext.table.rectangle(paramRenderingContext.pagetop, indentBottom());
      f2 = Math.max(localRectangle1.getBottom(), f2);
      Rectangle localRectangle2 = localPdfCell.rectangle(localRectangle1.getTop(), f2);
      if (localRectangle2.getHeight() > 0.0F)
      {
        paramRenderingContext.lostTableBottom = f2;
        paramRenderingContext.cellGraphics.rectangle(localRectangle2);
      }
      ArrayList localArrayList = localPdfCell.getImages(paramRenderingContext.pagetop, indentBottom());
      Iterator localIterator2 = localArrayList.iterator();
      while (localIterator2.hasNext())
      {
        Image localImage = (Image)localIterator2.next();
        this.graphics.addImage(localImage);
      }
    }
  }

  float bottom(Table paramTable)
  {
    PdfTable localPdfTable = new PdfTable(paramTable, indentLeft(), indentRight(), indentTop() - this.currentHeight);
    return localPdfTable.getBottom();
  }

  protected void doFooter()
    throws DocumentException
  {
    if (this.footer == null)
      return;
    float f1 = this.indentation.indentLeft;
    float f2 = this.indentation.indentRight;
    float f3 = this.indentation.listIndentLeft;
    float f4 = this.indentation.imageIndentLeft;
    float f5 = this.indentation.imageIndentRight;
    this.indentation.indentLeft = (this.indentation.indentRight = 0.0F);
    this.indentation.listIndentLeft = 0.0F;
    this.indentation.imageIndentLeft = 0.0F;
    this.indentation.imageIndentRight = 0.0F;
    this.footer.setPageNumber(this.pageN);
    this.leading = this.footer.paragraph().getTotalLeading();
    add(this.footer.paragraph());
    this.indentation.indentBottom = this.currentHeight;
    this.text.moveText(left(), indentBottom());
    flushLines();
    this.text.moveText(-left(), -bottom());
    this.footer.setTop(bottom(this.currentHeight));
    this.footer.setBottom(bottom() - 0.75F * this.leading);
    this.footer.setLeft(left());
    this.footer.setRight(right());
    this.graphics.rectangle(this.footer);
    this.indentation.indentBottom = (this.currentHeight + this.leading * 2.0F);
    this.currentHeight = 0.0F;
    this.indentation.indentLeft = f1;
    this.indentation.indentRight = f2;
    this.indentation.listIndentLeft = f3;
    this.indentation.imageIndentLeft = f4;
    this.indentation.imageIndentRight = f5;
  }

  protected void doHeader()
    throws DocumentException
  {
    if (this.header == null)
      return;
    float f1 = this.indentation.indentLeft;
    float f2 = this.indentation.indentRight;
    float f3 = this.indentation.listIndentLeft;
    float f4 = this.indentation.imageIndentLeft;
    float f5 = this.indentation.imageIndentRight;
    this.indentation.indentLeft = (this.indentation.indentRight = 0.0F);
    this.indentation.listIndentLeft = 0.0F;
    this.indentation.imageIndentLeft = 0.0F;
    this.indentation.imageIndentRight = 0.0F;
    this.header.setPageNumber(this.pageN);
    this.leading = this.header.paragraph().getTotalLeading();
    this.text.moveText(0.0F, this.leading);
    add(this.header.paragraph());
    newLine();
    this.indentation.indentTop = (this.currentHeight - this.leading);
    this.header.setTop(top() + this.leading);
    this.header.setBottom(indentTop() + this.leading * 2.0F / 3.0F);
    this.header.setLeft(left());
    this.header.setRight(right());
    this.graphics.rectangle(this.header);
    flushLines();
    this.currentHeight = 0.0F;
    this.indentation.indentLeft = f1;
    this.indentation.indentRight = f2;
    this.indentation.listIndentLeft = f3;
    this.indentation.imageIndentLeft = f4;
    this.indentation.imageIndentRight = f5;
  }

  protected static class RenderingContext
  {
    float pagetop = -1.0F;
    float oldHeight = -1.0F;
    PdfContentByte cellGraphics = null;
    float lostTableBottom;
    float maxCellBottom;
    float maxCellHeight;
    Map rowspanMap;
    Map pageMap = new HashMap();
    public PdfTable table;

    public int consumeRowspan(PdfCell paramPdfCell)
    {
      if (paramPdfCell.rowspan() == 1)
        return 1;
      Integer localInteger = (Integer)this.rowspanMap.get(paramPdfCell);
      if (localInteger == null)
        localInteger = new Integer(paramPdfCell.rowspan());
      localInteger = new Integer(localInteger.intValue() - 1);
      this.rowspanMap.put(paramPdfCell, localInteger);
      if (localInteger.intValue() < 1)
        return 1;
      return localInteger.intValue();
    }

    public int currentRowspan(PdfCell paramPdfCell)
    {
      Integer localInteger = (Integer)this.rowspanMap.get(paramPdfCell);
      if (localInteger == null)
        return paramPdfCell.rowspan();
      return localInteger.intValue();
    }

    public int cellRendered(PdfCell paramPdfCell, int paramInt)
    {
      Integer localInteger1 = (Integer)this.pageMap.get(paramPdfCell);
      if (localInteger1 == null)
        localInteger1 = new Integer(1);
      else
        localInteger1 = new Integer(localInteger1.intValue() + 1);
      this.pageMap.put(paramPdfCell, localInteger1);
      Integer localInteger2 = new Integer(paramInt);
      Object localObject = (Set)this.pageMap.get(localInteger2);
      if (localObject == null)
      {
        localObject = new HashSet();
        this.pageMap.put(localInteger2, localObject);
      }
      ((Set)localObject).add(paramPdfCell);
      return localInteger1.intValue();
    }

    public int numCellRendered(PdfCell paramPdfCell)
    {
      Integer localInteger = (Integer)this.pageMap.get(paramPdfCell);
      if (localInteger == null)
        localInteger = new Integer(0);
      return localInteger.intValue();
    }

    public boolean isCellRenderedOnPage(PdfCell paramPdfCell, int paramInt)
    {
      Integer localInteger = new Integer(paramInt);
      Set localSet = (Set)this.pageMap.get(localInteger);
      if (localSet != null)
        return localSet.contains(paramPdfCell);
      return false;
    }
  }

  public static class Indentation
  {
    float indentLeft = 0.0F;
    float sectionIndentLeft = 0.0F;
    float listIndentLeft = 0.0F;
    float imageIndentLeft = 0.0F;
    float indentRight = 0.0F;
    float sectionIndentRight = 0.0F;
    float imageIndentRight = 0.0F;
    float indentTop = 0.0F;
    float indentBottom = 0.0F;
  }

  static class PdfCatalog extends PdfDictionary
  {
    PdfWriter writer;

    PdfCatalog(PdfIndirectReference paramPdfIndirectReference, PdfWriter paramPdfWriter)
    {
      super();
      this.writer = paramPdfWriter;
      put(PdfName.PAGES, paramPdfIndirectReference);
    }

    void addNames(TreeMap paramTreeMap, HashMap paramHashMap1, HashMap paramHashMap2, PdfWriter paramPdfWriter)
    {
      if ((paramTreeMap.isEmpty()) && (paramHashMap1.isEmpty()) && (paramHashMap2.isEmpty()))
        return;
      try
      {
        PdfDictionary localPdfDictionary = new PdfDictionary();
        Object localObject1;
        if (!paramTreeMap.isEmpty())
        {
          localObject1 = new PdfArray();
          Object localObject2 = paramTreeMap.entrySet().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
            String str = (String)localEntry.getKey();
            Object[] arrayOfObject = (Object[])localEntry.getValue();
            if (arrayOfObject[2] == null)
              continue;
            PdfIndirectReference localPdfIndirectReference = (PdfIndirectReference)arrayOfObject[1];
            ((PdfArray)localObject1).add(new PdfString(str, null));
            ((PdfArray)localObject1).add(localPdfIndirectReference);
          }
          if (((PdfArray)localObject1).size() > 0)
          {
            localObject2 = new PdfDictionary();
            ((PdfDictionary)localObject2).put(PdfName.NAMES, (PdfObject)localObject1);
            localPdfDictionary.put(PdfName.DESTS, paramPdfWriter.addToBody((PdfObject)localObject2).getIndirectReference());
          }
        }
        if (!paramHashMap1.isEmpty())
        {
          localObject1 = PdfNameTree.writeTree(paramHashMap1, paramPdfWriter);
          localPdfDictionary.put(PdfName.JAVASCRIPT, paramPdfWriter.addToBody((PdfObject)localObject1).getIndirectReference());
        }
        if (!paramHashMap2.isEmpty())
          localPdfDictionary.put(PdfName.EMBEDDEDFILES, paramPdfWriter.addToBody(PdfNameTree.writeTree(paramHashMap2, paramPdfWriter)).getIndirectReference());
        if (localPdfDictionary.size() > 0)
          put(PdfName.NAMES, paramPdfWriter.addToBody(localPdfDictionary).getIndirectReference());
      }
      catch (IOException localIOException)
      {
        throw new ExceptionConverter(localIOException);
      }
    }

    void setOpenAction(PdfAction paramPdfAction)
    {
      put(PdfName.OPENACTION, paramPdfAction);
    }

    void setAdditionalActions(PdfDictionary paramPdfDictionary)
    {
      try
      {
        put(PdfName.AA, this.writer.addToBody(paramPdfDictionary).getIndirectReference());
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
    }
  }

  public static class PdfInfo extends PdfDictionary
  {
    PdfInfo()
    {
      addProducer();
      addCreationDate();
    }

    PdfInfo(String paramString1, String paramString2, String paramString3)
    {
      this();
      addTitle(paramString2);
      addSubject(paramString3);
      addAuthor(paramString1);
    }

    void addTitle(String paramString)
    {
      put(PdfName.TITLE, new PdfString(paramString, "UnicodeBig"));
    }

    void addSubject(String paramString)
    {
      put(PdfName.SUBJECT, new PdfString(paramString, "UnicodeBig"));
    }

    void addKeywords(String paramString)
    {
      put(PdfName.KEYWORDS, new PdfString(paramString, "UnicodeBig"));
    }

    void addAuthor(String paramString)
    {
      put(PdfName.AUTHOR, new PdfString(paramString, "UnicodeBig"));
    }

    void addCreator(String paramString)
    {
      put(PdfName.CREATOR, new PdfString(paramString, "UnicodeBig"));
    }

    void addProducer()
    {
      put(PdfName.PRODUCER, new PdfString(Document.getVersion()));
    }

    void addCreationDate()
    {
      PdfDate localPdfDate = new PdfDate();
      put(PdfName.CREATIONDATE, localPdfDate);
      put(PdfName.MODDATE, localPdfDate);
    }

    void addkey(String paramString1, String paramString2)
    {
      if ((paramString1.equals("Producer")) || (paramString1.equals("CreationDate")))
        return;
      put(new PdfName(paramString1), new PdfString(paramString2, "UnicodeBig"));
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfDocument
 * JD-Core Version:    0.6.0
 */