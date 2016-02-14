package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class ColumnText
{
  public static final int AR_NOVOWEL = 1;
  public static final int AR_COMPOSEDTASHKEEL = 4;
  public static final int AR_LIG = 8;
  public static final int DIGITS_EN2AN = 32;
  public static final int DIGITS_AN2EN = 64;
  public static final int DIGITS_EN2AN_INIT_LR = 96;
  public static final int DIGITS_EN2AN_INIT_AL = 128;
  public static final int DIGIT_TYPE_AN = 0;
  public static final int DIGIT_TYPE_AN_EXTENDED = 256;
  protected int runDirection = 0;
  public static final float GLOBAL_SPACE_CHAR_RATIO = 0.0F;
  public static final int START_COLUMN = 0;
  public static final int NO_MORE_TEXT = 1;
  public static final int NO_MORE_COLUMN = 2;
  protected static final int LINE_STATUS_OK = 0;
  protected static final int LINE_STATUS_OFFLIMITS = 1;
  protected static final int LINE_STATUS_NOLINE = 2;
  protected float maxY;
  protected float minY;
  protected float leftX;
  protected float rightX;
  protected int alignment = 0;
  protected ArrayList leftWall;
  protected ArrayList rightWall;
  protected BidiLine bidiLine;
  protected float yLine;
  protected float currentLeading = 16.0F;
  protected float fixedLeading = 16.0F;
  protected float multipliedLeading = 0.0F;
  protected PdfContentByte canvas;
  protected PdfContentByte[] canvases;
  protected int lineStatus;
  protected float indent = 0.0F;
  protected float followingIndent = 0.0F;
  protected float rightIndent = 0.0F;
  protected float extraParagraphSpace = 0.0F;
  protected float rectangularWidth = -1.0F;
  protected boolean rectangularMode = false;
  private float spaceCharRatio = 0.0F;
  private boolean lastWasNewline = true;
  private int linesWritten;
  private float firstLineY;
  private boolean firstLineYDone = false;
  private int arabicOptions = 0;
  protected float descender;
  protected boolean composite = false;
  protected ColumnText compositeColumn;
  protected LinkedList compositeElements;
  protected int listIdx = 0;
  private boolean splittedRow;
  protected Phrase waitPhrase;
  private boolean useAscender = false;
  private float filledWidth;
  private boolean adjustFirstLine = true;

  public ColumnText(PdfContentByte paramPdfContentByte)
  {
    this.canvas = paramPdfContentByte;
  }

  public static ColumnText duplicate(ColumnText paramColumnText)
  {
    ColumnText localColumnText = new ColumnText(null);
    localColumnText.setACopy(paramColumnText);
    return localColumnText;
  }

  public ColumnText setACopy(ColumnText paramColumnText)
  {
    setSimpleVars(paramColumnText);
    if (paramColumnText.bidiLine != null)
      this.bidiLine = new BidiLine(paramColumnText.bidiLine);
    return this;
  }

  protected void setSimpleVars(ColumnText paramColumnText)
  {
    this.maxY = paramColumnText.maxY;
    this.minY = paramColumnText.minY;
    this.alignment = paramColumnText.alignment;
    this.leftWall = null;
    if (paramColumnText.leftWall != null)
      this.leftWall = new ArrayList(paramColumnText.leftWall);
    this.rightWall = null;
    if (paramColumnText.rightWall != null)
      this.rightWall = new ArrayList(paramColumnText.rightWall);
    this.yLine = paramColumnText.yLine;
    this.currentLeading = paramColumnText.currentLeading;
    this.fixedLeading = paramColumnText.fixedLeading;
    this.multipliedLeading = paramColumnText.multipliedLeading;
    this.canvas = paramColumnText.canvas;
    this.canvases = paramColumnText.canvases;
    this.lineStatus = paramColumnText.lineStatus;
    this.indent = paramColumnText.indent;
    this.followingIndent = paramColumnText.followingIndent;
    this.rightIndent = paramColumnText.rightIndent;
    this.extraParagraphSpace = paramColumnText.extraParagraphSpace;
    this.rectangularWidth = paramColumnText.rectangularWidth;
    this.rectangularMode = paramColumnText.rectangularMode;
    this.spaceCharRatio = paramColumnText.spaceCharRatio;
    this.lastWasNewline = paramColumnText.lastWasNewline;
    this.linesWritten = paramColumnText.linesWritten;
    this.arabicOptions = paramColumnText.arabicOptions;
    this.runDirection = paramColumnText.runDirection;
    this.descender = paramColumnText.descender;
    this.composite = paramColumnText.composite;
    this.splittedRow = paramColumnText.splittedRow;
    if (paramColumnText.composite)
    {
      this.compositeElements = new LinkedList(paramColumnText.compositeElements);
      if (this.splittedRow)
      {
        PdfPTable localPdfPTable = (PdfPTable)this.compositeElements.getFirst();
        this.compositeElements.set(0, new PdfPTable(localPdfPTable));
      }
      if (paramColumnText.compositeColumn != null)
        this.compositeColumn = duplicate(paramColumnText.compositeColumn);
    }
    this.listIdx = paramColumnText.listIdx;
    this.firstLineY = paramColumnText.firstLineY;
    this.leftX = paramColumnText.leftX;
    this.rightX = paramColumnText.rightX;
    this.firstLineYDone = paramColumnText.firstLineYDone;
    this.waitPhrase = paramColumnText.waitPhrase;
    this.useAscender = paramColumnText.useAscender;
    this.filledWidth = paramColumnText.filledWidth;
    this.adjustFirstLine = paramColumnText.adjustFirstLine;
  }

  private void addWaitingPhrase()
  {
    if ((this.bidiLine == null) && (this.waitPhrase != null))
    {
      this.bidiLine = new BidiLine();
      Iterator localIterator = this.waitPhrase.getChunks().iterator();
      while (localIterator.hasNext())
        this.bidiLine.addChunk(new PdfChunk((Chunk)localIterator.next(), null));
      this.waitPhrase = null;
    }
  }

  public void addText(Phrase paramPhrase)
  {
    if ((paramPhrase == null) || (this.composite))
      return;
    addWaitingPhrase();
    if (this.bidiLine == null)
    {
      this.waitPhrase = paramPhrase;
      return;
    }
    Iterator localIterator = paramPhrase.getChunks().iterator();
    while (localIterator.hasNext())
      this.bidiLine.addChunk(new PdfChunk((Chunk)localIterator.next(), null));
  }

  public void setText(Phrase paramPhrase)
  {
    this.bidiLine = null;
    this.composite = false;
    this.compositeColumn = null;
    this.compositeElements = null;
    this.listIdx = 0;
    this.splittedRow = false;
    this.waitPhrase = paramPhrase;
  }

  public void addText(Chunk paramChunk)
  {
    if ((paramChunk == null) || (this.composite))
      return;
    addText(new Phrase(paramChunk));
  }

  public void addElement(Element paramElement)
  {
    if (paramElement == null)
      return;
    if ((paramElement instanceof Image))
    {
      Image localImage = (Image)paramElement;
      PdfPTable localPdfPTable = new PdfPTable(1);
      float f = localImage.getWidthPercentage();
      if (f == 0.0F)
      {
        localPdfPTable.setTotalWidth(localImage.getScaledWidth());
        localPdfPTable.setLockedWidth(true);
      }
      else
      {
        localPdfPTable.setWidthPercentage(f);
      }
      localPdfPTable.setSpacingAfter(localImage.getSpacingAfter());
      localPdfPTable.setSpacingBefore(localImage.getSpacingBefore());
      switch (localImage.getAlignment())
      {
      case 0:
        localPdfPTable.setHorizontalAlignment(0);
        break;
      case 2:
        localPdfPTable.setHorizontalAlignment(2);
        break;
      default:
        localPdfPTable.setHorizontalAlignment(1);
      }
      PdfPCell localPdfPCell = new PdfPCell(localImage, true);
      localPdfPCell.setPadding(0.0F);
      localPdfPCell.setBorder(localImage.getBorder());
      localPdfPCell.setBorderColor(localImage.getBorderColor());
      localPdfPCell.setBorderWidth(localImage.getBorderWidth());
      localPdfPCell.setBackgroundColor(localImage.getBackgroundColor());
      localPdfPTable.addCell(localPdfPCell);
      paramElement = localPdfPTable;
    }
    if (paramElement.type() == 10)
      paramElement = new Paragraph((Chunk)paramElement);
    else if (paramElement.type() == 11)
      paramElement = new Paragraph((Phrase)paramElement);
    if ((paramElement instanceof SimpleTable))
      try
      {
        paramElement = ((SimpleTable)paramElement).createPdfPTable();
      }
      catch (DocumentException localDocumentException)
      {
        throw new IllegalArgumentException("Element not allowed.");
      }
    else if ((paramElement.type() != 12) && (paramElement.type() != 14) && (paramElement.type() != 23) && (paramElement.type() != 55))
      throw new IllegalArgumentException("Element not allowed.");
    if (!this.composite)
    {
      this.composite = true;
      this.compositeElements = new LinkedList();
      this.bidiLine = null;
      this.waitPhrase = null;
    }
    this.compositeElements.add(paramElement);
  }

  protected ArrayList convertColumn(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length < 4)
      throw new RuntimeException("No valid column line found.");
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfFloat.length - 2; i += 2)
    {
      float f1 = paramArrayOfFloat[i];
      float f2 = paramArrayOfFloat[(i + 1)];
      float f3 = paramArrayOfFloat[(i + 2)];
      float f4 = paramArrayOfFloat[(i + 3)];
      if (f2 == f4)
        continue;
      float f5 = (f1 - f3) / (f2 - f4);
      float f6 = f1 - f5 * f2;
      float[] arrayOfFloat = new float[4];
      arrayOfFloat[0] = Math.min(f2, f4);
      arrayOfFloat[1] = Math.max(f2, f4);
      arrayOfFloat[2] = f5;
      arrayOfFloat[3] = f6;
      localArrayList.add(arrayOfFloat);
      this.maxY = Math.max(this.maxY, arrayOfFloat[1]);
      this.minY = Math.min(this.minY, arrayOfFloat[0]);
    }
    if (localArrayList.isEmpty())
      throw new RuntimeException("No valid column line found.");
    return localArrayList;
  }

  protected float findLimitsPoint(ArrayList paramArrayList)
  {
    this.lineStatus = 0;
    if ((this.yLine < this.minY) || (this.yLine > this.maxY))
    {
      this.lineStatus = 1;
      return 0.0F;
    }
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      float[] arrayOfFloat = (float[])paramArrayList.get(i);
      if ((this.yLine >= arrayOfFloat[0]) && (this.yLine <= arrayOfFloat[1]))
        return arrayOfFloat[2] * this.yLine + arrayOfFloat[3];
    }
    this.lineStatus = 2;
    return 0.0F;
  }

  protected float[] findLimitsOneLine()
  {
    float f1 = findLimitsPoint(this.leftWall);
    if ((this.lineStatus == 1) || (this.lineStatus == 2))
      return null;
    float f2 = findLimitsPoint(this.rightWall);
    if (this.lineStatus == 2)
      return null;
    return new float[] { f1, f2 };
  }

  protected float[] findLimitsTwoLines()
  {
    int i = 0;
    float[] arrayOfFloat1;
    float[] arrayOfFloat2;
    do
      while (true)
      {
        if ((i != 0) && (this.currentLeading == 0.0F))
          return null;
        i = 1;
        arrayOfFloat1 = findLimitsOneLine();
        if (this.lineStatus == 1)
          return null;
        this.yLine -= this.currentLeading;
        if (this.lineStatus == 2)
          continue;
        arrayOfFloat2 = findLimitsOneLine();
        if (this.lineStatus == 1)
          return null;
        if (this.lineStatus != 2)
          break;
        this.yLine -= this.currentLeading;
      }
    while ((arrayOfFloat1[0] >= arrayOfFloat2[1]) || (arrayOfFloat2[0] >= arrayOfFloat1[1]));
    return new float[] { arrayOfFloat1[0], arrayOfFloat1[1], arrayOfFloat2[0], arrayOfFloat2[1] };
  }

  public void setColumns(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    this.maxY = -1.0E+021F;
    this.minY = 1.0E+021F;
    setYLine(Math.max(paramArrayOfFloat1[1], paramArrayOfFloat1[(paramArrayOfFloat1.length - 1)]));
    this.rightWall = convertColumn(paramArrayOfFloat2);
    this.leftWall = convertColumn(paramArrayOfFloat1);
    this.rectangularWidth = -1.0F;
    this.rectangularMode = false;
  }

  public void setSimpleColumn(Phrase paramPhrase, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, int paramInt)
  {
    addText(paramPhrase);
    setSimpleColumn(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramInt);
  }

  public void setSimpleColumn(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, int paramInt)
  {
    setLeading(paramFloat5);
    this.alignment = paramInt;
    setSimpleColumn(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public void setSimpleColumn(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.leftX = Math.min(paramFloat1, paramFloat3);
    this.maxY = Math.max(paramFloat2, paramFloat4);
    this.minY = Math.min(paramFloat2, paramFloat4);
    this.rightX = Math.max(paramFloat1, paramFloat3);
    this.yLine = this.maxY;
    this.rectangularWidth = (this.rightX - this.leftX);
    if (this.rectangularWidth < 0.0F)
      this.rectangularWidth = 0.0F;
    this.rectangularMode = true;
  }

  public void setLeading(float paramFloat)
  {
    this.fixedLeading = paramFloat;
    this.multipliedLeading = 0.0F;
  }

  public void setLeading(float paramFloat1, float paramFloat2)
  {
    this.fixedLeading = paramFloat1;
    this.multipliedLeading = paramFloat2;
  }

  public float getLeading()
  {
    return this.fixedLeading;
  }

  public float getMultipliedLeading()
  {
    return this.multipliedLeading;
  }

  public void setYLine(float paramFloat)
  {
    this.yLine = paramFloat;
  }

  public float getYLine()
  {
    return this.yLine;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public void setIndent(float paramFloat)
  {
    this.indent = paramFloat;
    this.lastWasNewline = true;
  }

  public float getIndent()
  {
    return this.indent;
  }

  public void setFollowingIndent(float paramFloat)
  {
    this.followingIndent = paramFloat;
    this.lastWasNewline = true;
  }

  public float getFollowingIndent()
  {
    return this.followingIndent;
  }

  public void setRightIndent(float paramFloat)
  {
    this.rightIndent = paramFloat;
    this.lastWasNewline = true;
  }

  public float getRightIndent()
  {
    return this.rightIndent;
  }

  public int go()
    throws DocumentException
  {
    return go(false);
  }

  public int go(boolean paramBoolean)
    throws DocumentException
  {
    if (this.composite)
      return goComposite(paramBoolean);
    addWaitingPhrase();
    if (this.bidiLine == null)
      return 1;
    this.descender = 0.0F;
    this.linesWritten = 0;
    int i = 0;
    float f1 = this.spaceCharRatio;
    Object[] arrayOfObject = new Object[2];
    PdfFont localPdfFont = null;
    Float localFloat = new Float(0.0F);
    arrayOfObject[1] = localFloat;
    PdfDocument localPdfDocument = null;
    PdfContentByte localPdfContentByte1 = null;
    PdfContentByte localPdfContentByte2 = null;
    this.firstLineY = (0.0F / 0.0F);
    int j = 1;
    if (this.runDirection != 0)
      j = this.runDirection;
    if (this.canvas != null)
    {
      localPdfContentByte1 = this.canvas;
      localPdfDocument = this.canvas.getPdfDocument();
      localPdfContentByte2 = this.canvas.getDuplicate();
    }
    else if (!paramBoolean)
    {
      throw new NullPointerException("ColumnText.go with simulate==false and text==null.");
    }
    if (!paramBoolean)
      if (f1 == 0.0F)
        f1 = localPdfContentByte2.getPdfWriter().getSpaceCharRatio();
      else if (f1 < 0.001F)
        f1 = 0.001F;
    float f2 = 0.0F;
    int k = 0;
    while (true)
    {
      f2 = this.lastWasNewline ? this.indent : this.followingIndent;
      PdfLine localPdfLine;
      float f3;
      if (this.rectangularMode)
      {
        if (this.rectangularWidth <= f2 + this.rightIndent)
        {
          k = 2;
          if (!this.bidiLine.isEmpty())
            break;
          k |= 1;
          break;
        }
        if (this.bidiLine.isEmpty())
        {
          k = 1;
          break;
        }
        localPdfLine = this.bidiLine.processLine(this.leftX, this.rectangularWidth - f2 - this.rightIndent, this.alignment, j, this.arabicOptions);
        if (localPdfLine == null)
        {
          k = 1;
          break;
        }
        float[] arrayOfFloat1 = localPdfLine.getMaxSize();
        if ((isUseAscender()) && (Float.isNaN(this.firstLineY)))
          this.currentLeading = localPdfLine.getAscender();
        else
          this.currentLeading = Math.max(this.fixedLeading + arrayOfFloat1[0] * this.multipliedLeading, arrayOfFloat1[1]);
        if ((this.yLine > this.maxY) || (this.yLine - this.currentLeading < this.minY))
        {
          k = 2;
          this.bidiLine.restore();
          break;
        }
        this.yLine -= this.currentLeading;
        if ((!paramBoolean) && (i == 0))
        {
          localPdfContentByte2.beginText();
          i = 1;
        }
        if (Float.isNaN(this.firstLineY))
          this.firstLineY = this.yLine;
        updateFilledWidth(this.rectangularWidth - localPdfLine.widthLeft());
        f3 = this.leftX;
      }
      else
      {
        float f4 = this.yLine;
        float[] arrayOfFloat2 = findLimitsTwoLines();
        if (arrayOfFloat2 == null)
        {
          k = 2;
          if (this.bidiLine.isEmpty())
            k |= 1;
          this.yLine = f4;
          break;
        }
        if (this.bidiLine.isEmpty())
        {
          k = 1;
          this.yLine = f4;
          break;
        }
        f3 = Math.max(arrayOfFloat2[0], arrayOfFloat2[2]);
        float f5 = Math.min(arrayOfFloat2[1], arrayOfFloat2[3]);
        if (f5 - f3 <= f2 + this.rightIndent)
          continue;
        if ((!paramBoolean) && (i == 0))
        {
          localPdfContentByte2.beginText();
          i = 1;
        }
        localPdfLine = this.bidiLine.processLine(f3, f5 - f3 - f2 - this.rightIndent, this.alignment, j, this.arabicOptions);
        if (localPdfLine == null)
        {
          k = 1;
          this.yLine = f4;
          break;
        }
      }
      if (!paramBoolean)
      {
        arrayOfObject[0] = localPdfFont;
        localPdfContentByte2.setTextMatrix(f3 + (localPdfLine.isRTL() ? this.rightIndent : f2) + localPdfLine.indentLeft(), this.yLine);
        localPdfDocument.writeLineToContent(localPdfLine, localPdfContentByte2, localPdfContentByte1, arrayOfObject, f1);
        localPdfFont = (PdfFont)arrayOfObject[0];
      }
      this.lastWasNewline = localPdfLine.isNewlineSplit();
      this.yLine -= (localPdfLine.isNewlineSplit() ? this.extraParagraphSpace : 0.0F);
      this.linesWritten += 1;
      this.descender = localPdfLine.getDescender();
    }
    if (i != 0)
    {
      localPdfContentByte2.endText();
      this.canvas.add(localPdfContentByte2);
    }
    return k;
  }

  public float getExtraParagraphSpace()
  {
    return this.extraParagraphSpace;
  }

  public void setExtraParagraphSpace(float paramFloat)
  {
    this.extraParagraphSpace = paramFloat;
  }

  public void clearChunks()
  {
    if (this.bidiLine != null)
      this.bidiLine.clearChunks();
  }

  public float getSpaceCharRatio()
  {
    return this.spaceCharRatio;
  }

  public void setSpaceCharRatio(float paramFloat)
  {
    this.spaceCharRatio = paramFloat;
  }

  public void setRunDirection(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3))
      throw new RuntimeException("Invalid run direction: " + paramInt);
    this.runDirection = paramInt;
  }

  public int getRunDirection()
  {
    return this.runDirection;
  }

  public int getLinesWritten()
  {
    return this.linesWritten;
  }

  public int getArabicOptions()
  {
    return this.arabicOptions;
  }

  public void setArabicOptions(int paramInt)
  {
    this.arabicOptions = paramInt;
  }

  public float getDescender()
  {
    return this.descender;
  }

  public static float getWidth(Phrase paramPhrase, int paramInt1, int paramInt2)
  {
    ColumnText localColumnText = new ColumnText(null);
    localColumnText.addText(paramPhrase);
    localColumnText.addWaitingPhrase();
    PdfLine localPdfLine = localColumnText.bidiLine.processLine(0.0F, 20000.0F, 0, paramInt1, paramInt2);
    if (localPdfLine == null)
      return 0.0F;
    return 20000.0F - localPdfLine.widthLeft();
  }

  public static float getWidth(Phrase paramPhrase)
  {
    return getWidth(paramPhrase, 1, 0);
  }

  public static void showTextAligned(PdfContentByte paramPdfContentByte, int paramInt1, Phrase paramPhrase, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt2, int paramInt3)
  {
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 2))
      paramInt1 = 0;
    paramPdfContentByte.saveState();
    ColumnText localColumnText = new ColumnText(paramPdfContentByte);
    float f1 = -1.0F;
    float f2 = 2.0F;
    float f3;
    float f4;
    switch (paramInt1)
    {
    case 0:
      f3 = 0.0F;
      f4 = 20000.0F;
      break;
    case 2:
      f3 = -20000.0F;
      f4 = 0.0F;
      break;
    default:
      f3 = -20000.0F;
      f4 = 20000.0F;
    }
    if (paramFloat3 == 0.0F)
    {
      f3 += paramFloat1;
      f1 += paramFloat2;
      f4 += paramFloat1;
      f2 += paramFloat2;
    }
    else
    {
      double d = paramFloat3 * 3.141592653589793D / 180.0D;
      float f5 = (float)Math.cos(d);
      float f6 = (float)Math.sin(d);
      paramPdfContentByte.concatCTM(f5, f6, -f6, f5, paramFloat1, paramFloat2);
    }
    localColumnText.setSimpleColumn(paramPhrase, f3, f1, f4, f2, 2.0F, paramInt1);
    if (paramInt2 == 3)
      if (paramInt1 == 0)
        paramInt1 = 2;
      else if (paramInt1 == 2)
        paramInt1 = 0;
    localColumnText.setAlignment(paramInt1);
    localColumnText.setArabicOptions(paramInt3);
    localColumnText.setRunDirection(paramInt2);
    try
    {
      localColumnText.go();
    }
    catch (DocumentException localDocumentException)
    {
      throw new ExceptionConverter(localDocumentException);
    }
    paramPdfContentByte.restoreState();
  }

  public static void showTextAligned(PdfContentByte paramPdfContentByte, int paramInt, Phrase paramPhrase, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    showTextAligned(paramPdfContentByte, paramInt, paramPhrase, paramFloat1, paramFloat2, paramFloat3, 1, 0);
  }

  protected int goComposite(boolean paramBoolean)
    throws DocumentException
  {
    if (!this.rectangularMode)
      throw new DocumentException("Irregular columns are not supported in composite mode.");
    this.linesWritten = 0;
    this.descender = 0.0F;
    boolean bool = this.adjustFirstLine;
    while (true)
    {
      if (this.compositeElements.isEmpty())
        return 1;
      Element localElement = (Element)this.compositeElements.getFirst();
      Object localObject1;
      float f3;
      int k;
      if (localElement.type() == 12)
      {
        localObject1 = (Paragraph)localElement;
        int i = 0;
        for (int j = 0; j < 2; j++)
        {
          f3 = this.yLine;
          k = 0;
          if (this.compositeColumn == null)
          {
            this.compositeColumn = new ColumnText(this.canvas);
            this.compositeColumn.setUseAscender(bool ? this.useAscender : false);
            this.compositeColumn.setAlignment(((Paragraph)localObject1).getAlignment());
            this.compositeColumn.setIndent(((Paragraph)localObject1).getIndentationLeft() + ((Paragraph)localObject1).getFirstLineIndent());
            this.compositeColumn.setExtraParagraphSpace(((Paragraph)localObject1).getExtraParagraphSpace());
            this.compositeColumn.setFollowingIndent(((Paragraph)localObject1).getIndentationLeft());
            this.compositeColumn.setRightIndent(((Paragraph)localObject1).getIndentationRight());
            this.compositeColumn.setLeading(((Paragraph)localObject1).getLeading(), ((Paragraph)localObject1).getMultipliedLeading());
            this.compositeColumn.setRunDirection(this.runDirection);
            this.compositeColumn.setArabicOptions(this.arabicOptions);
            this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
            this.compositeColumn.addText((Phrase)localObject1);
            if (!bool)
              this.yLine -= ((Paragraph)localObject1).getSpacingBefore();
            k = 1;
          }
          this.compositeColumn.leftX = this.leftX;
          this.compositeColumn.rightX = this.rightX;
          this.compositeColumn.yLine = this.yLine;
          this.compositeColumn.rectangularWidth = this.rectangularWidth;
          this.compositeColumn.rectangularMode = this.rectangularMode;
          this.compositeColumn.minY = this.minY;
          this.compositeColumn.maxY = this.maxY;
          int m = (((Paragraph)localObject1).getKeepTogether()) && (k != 0) && (!bool) ? 1 : 0;
          i = this.compositeColumn.go((paramBoolean) || ((m != 0) && (j == 0)));
          updateFilledWidth(this.compositeColumn.filledWidth);
          if (((i & 0x1) == 0) && (m != 0))
          {
            this.compositeColumn = null;
            this.yLine = f3;
            return 2;
          }
          if ((paramBoolean) || (m == 0))
            break;
          if (j != 0)
            continue;
          this.compositeColumn = null;
          this.yLine = f3;
        }
        bool = false;
        this.yLine = this.compositeColumn.yLine;
        this.linesWritten += this.compositeColumn.linesWritten;
        this.descender = this.compositeColumn.descender;
        if ((i & 0x1) != 0)
        {
          this.compositeColumn = null;
          this.compositeElements.removeFirst();
          this.yLine -= ((Paragraph)localObject1).getSpacingAfter();
        }
        if ((i & 0x2) != 0)
          return 2;
      }
      int i1;
      int i2;
      float f5;
      int i4;
      if (localElement.type() == 14)
      {
        localObject1 = (List)localElement;
        ArrayList localArrayList1 = ((List)localObject1).getItems();
        ListItem localListItem = null;
        f3 = ((List)localObject1).getIndentationLeft();
        k = 0;
        Stack localStack = new Stack();
        for (i1 = 0; i1 < localArrayList1.size(); i1++)
        {
          Object localObject2 = localArrayList1.get(i1);
          if ((localObject2 instanceof ListItem))
          {
            if (k == this.listIdx)
            {
              localListItem = (ListItem)localObject2;
              break;
            }
            k++;
          }
          else if ((localObject2 instanceof List))
          {
            localStack.push(new Object[] { localObject1, new Integer(i1), new Float(f3) });
            localObject1 = (List)localObject2;
            localArrayList1 = ((List)localObject1).getItems();
            f3 += ((List)localObject1).getIndentationLeft();
            i1 = -1;
            continue;
          }
          if ((i1 != localArrayList1.size() - 1) || (localStack.isEmpty()))
            continue;
          Object[] arrayOfObject = (Object[])localStack.pop();
          localObject1 = (List)arrayOfObject[0];
          localArrayList1 = ((List)localObject1).getItems();
          i1 = ((Integer)arrayOfObject[1]).intValue();
          f3 = ((Float)arrayOfObject[2]).floatValue();
        }
        i1 = 0;
        for (i2 = 0; ; i2++)
        {
          if (i2 >= 2)
            break label1255;
          f5 = this.yLine;
          int i3 = 0;
          if (this.compositeColumn == null)
          {
            if (localListItem == null)
            {
              this.listIdx = 0;
              this.compositeElements.removeFirst();
              break;
            }
            this.compositeColumn = new ColumnText(this.canvas);
            this.compositeColumn.setUseAscender(bool ? this.useAscender : false);
            this.compositeColumn.setAlignment(localListItem.getAlignment());
            this.compositeColumn.setIndent(localListItem.getIndentationLeft() + f3 + localListItem.getFirstLineIndent());
            this.compositeColumn.setExtraParagraphSpace(localListItem.getExtraParagraphSpace());
            this.compositeColumn.setFollowingIndent(this.compositeColumn.getIndent());
            this.compositeColumn.setRightIndent(localListItem.getIndentationRight() + ((List)localObject1).getIndentationRight());
            this.compositeColumn.setLeading(localListItem.getLeading(), localListItem.getMultipliedLeading());
            this.compositeColumn.setRunDirection(this.runDirection);
            this.compositeColumn.setArabicOptions(this.arabicOptions);
            this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
            this.compositeColumn.addText(localListItem);
            if (!bool)
              this.yLine -= localListItem.getSpacingBefore();
            i3 = 1;
          }
          this.compositeColumn.leftX = this.leftX;
          this.compositeColumn.rightX = this.rightX;
          this.compositeColumn.yLine = this.yLine;
          this.compositeColumn.rectangularWidth = this.rectangularWidth;
          this.compositeColumn.rectangularMode = this.rectangularMode;
          this.compositeColumn.minY = this.minY;
          this.compositeColumn.maxY = this.maxY;
          i4 = (localListItem.getKeepTogether()) && (i3 != 0) && (!bool) ? 1 : 0;
          i1 = this.compositeColumn.go((paramBoolean) || ((i4 != 0) && (i2 == 0)));
          updateFilledWidth(this.compositeColumn.filledWidth);
          if (((i1 & 0x1) == 0) && (i4 != 0))
          {
            this.compositeColumn = null;
            this.yLine = f5;
            return 2;
          }
          if ((paramBoolean) || (i4 == 0))
            break label1255;
          if (i2 != 0)
            continue;
          this.compositeColumn = null;
          this.yLine = f5;
        }
        label1255: bool = false;
        this.yLine = this.compositeColumn.yLine;
        this.linesWritten += this.compositeColumn.linesWritten;
        this.descender = this.compositeColumn.descender;
        if ((!Float.isNaN(this.compositeColumn.firstLineY)) && (!this.compositeColumn.firstLineYDone))
        {
          if (!paramBoolean)
            showTextAligned(this.canvas, 0, new Phrase(localListItem.getListSymbol()), this.compositeColumn.leftX + f3, this.compositeColumn.firstLineY, 0.0F);
          this.compositeColumn.firstLineYDone = true;
        }
        if ((i1 & 0x1) != 0)
        {
          this.compositeColumn = null;
          this.listIdx += 1;
          this.yLine -= localListItem.getSpacingAfter();
        }
        if ((i1 & 0x2) != 0)
          return 2;
      }
      if (localElement.type() == 23)
      {
        if ((this.yLine < this.minY) || (this.yLine > this.maxY))
          return 2;
        localObject1 = (PdfPTable)localElement;
        if (((PdfPTable)localObject1).size() <= ((PdfPTable)localObject1).getHeaderRows())
        {
          this.compositeElements.removeFirst();
          continue;
        }
        float f1 = this.yLine;
        if ((!bool) && (this.listIdx == 0))
          f1 -= ((PdfPTable)localObject1).spacingBefore();
        float f2 = f1;
        if ((f1 < this.minY) || (f1 > this.maxY))
          return 2;
        this.currentLeading = 0.0F;
        f3 = this.leftX;
        float f4;
        if (((PdfPTable)localObject1).isLockedWidth())
        {
          f4 = ((PdfPTable)localObject1).getTotalWidth();
          updateFilledWidth(f4);
        }
        else
        {
          f4 = this.rectangularWidth * ((PdfPTable)localObject1).getWidthPercentage() / 100.0F;
          ((PdfPTable)localObject1).setTotalWidth(f4);
        }
        int n = ((PdfPTable)localObject1).getHeaderRows();
        i1 = ((PdfPTable)localObject1).getFooterRows();
        if (i1 > n)
          i1 = n;
        i2 = n - i1;
        f5 = ((PdfPTable)localObject1).getHeaderHeight();
        float f6 = ((PdfPTable)localObject1).getFooterHeight();
        i4 = (!bool) && (((PdfPTable)localObject1).isSkipFirstHeader()) && (this.listIdx <= n) ? 1 : 0;
        if (i4 == 0)
        {
          f1 -= f5;
          if ((f1 < this.minY) || (f1 > this.maxY))
          {
            if (bool)
            {
              this.compositeElements.removeFirst();
              continue;
            }
            return 2;
          }
        }
        if (this.listIdx < n)
          this.listIdx = n;
        if (!((PdfPTable)localObject1).isComplete())
          f1 -= f6;
        for (int i5 = this.listIdx; i5 < ((PdfPTable)localObject1).size(); i5++)
        {
          float f7 = ((PdfPTable)localObject1).getRowHeight(i5);
          if (f1 - f7 < this.minY)
            break;
          f1 -= f7;
        }
        if (!((PdfPTable)localObject1).isComplete())
          f1 += f6;
        Object localObject4;
        if (i5 < ((PdfPTable)localObject1).size())
          if ((((PdfPTable)localObject1).isSplitRows()) && ((!((PdfPTable)localObject1).isSplitLate()) || ((i5 == this.listIdx) && (bool))))
          {
            if (!this.splittedRow)
            {
              this.splittedRow = true;
              localObject1 = new PdfPTable((PdfPTable)localObject1);
              this.compositeElements.set(0, localObject1);
              ArrayList localArrayList2 = ((PdfPTable)localObject1).getRows();
              for (int i6 = n; i6 < this.listIdx; i6++)
                localArrayList2.set(i6, null);
            }
            float f8 = f1 - this.minY;
            localObject4 = ((PdfPTable)localObject1).getRow(i5).splitRow((PdfPTable)localObject1, i5, f8);
            if (localObject4 == null)
            {
              if (i5 == this.listIdx)
                return 2;
            }
            else
            {
              f1 = this.minY;
              i5++;
              ((PdfPTable)localObject1).getRows().add(i5, localObject4);
            }
          }
          else
          {
            if ((!((PdfPTable)localObject1).isSplitRows()) && (i5 == this.listIdx) && (bool))
            {
              this.compositeElements.removeFirst();
              this.splittedRow = false;
              continue;
            }
            if ((i5 == this.listIdx) && (!bool) && ((!((PdfPTable)localObject1).isSplitRows()) || (((PdfPTable)localObject1).isSplitLate())) && ((((PdfPTable)localObject1).getFooterRows() == 0) || (((PdfPTable)localObject1).isComplete())))
              return 2;
          }
        bool = false;
        Object localObject3;
        if (!paramBoolean)
        {
          switch (((PdfPTable)localObject1).getHorizontalAlignment())
          {
          case 0:
            break;
          case 2:
            f3 += this.rectangularWidth - f4;
            break;
          default:
            f3 += (this.rectangularWidth - f4) / 2.0F;
          }
          localObject3 = PdfPTable.shallowCopy((PdfPTable)localObject1);
          localObject4 = ((PdfPTable)localObject3).getRows();
          if (i4 == 0)
            for (i8 = 0; i8 < i2; i8++)
            {
              PdfPRow localPdfPRow1 = ((PdfPTable)localObject1).getRow(i8);
              ((ArrayList)localObject4).add(localPdfPRow1);
            }
          ((PdfPTable)localObject3).setHeaderRows(i1);
          ((ArrayList)localObject4).addAll(((PdfPTable)localObject1).getRows(this.listIdx, i5));
          int i8 = !((PdfPTable)localObject1).isSkipLastFooter() ? 1 : 0;
          if (i5 < ((PdfPTable)localObject1).size())
          {
            ((PdfPTable)localObject3).setComplete(true);
            i8 = 1;
          }
          for (int i9 = 0; (i9 < i1) && (((PdfPTable)localObject3).isComplete()) && (i8 != 0); i9++)
            ((ArrayList)localObject4).add(((PdfPTable)localObject1).getRow(i9 + i2));
          float f9 = 0.0F;
          PdfPRow localPdfPRow2 = (PdfPRow)((ArrayList)localObject4).get(((ArrayList)localObject4).size() - 1 - i1);
          if (((PdfPTable)localObject1).isExtendLastRow())
          {
            f9 = localPdfPRow2.getMaxHeights();
            localPdfPRow2.setMaxHeights(f1 - this.minY + f9);
            f1 = this.minY;
          }
          if (this.canvases != null)
            ((PdfPTable)localObject3).writeSelectedRows(0, -1, f3, f2, this.canvases);
          else
            ((PdfPTable)localObject3).writeSelectedRows(0, -1, f3, f2, this.canvas);
          if (((PdfPTable)localObject1).isExtendLastRow())
            localPdfPRow2.setMaxHeights(f9);
        }
        else if ((((PdfPTable)localObject1).isExtendLastRow()) && (this.minY > -1.073742E+009F))
        {
          f1 = this.minY;
        }
        this.yLine = f1;
        if ((i4 == 0) && (!((PdfPTable)localObject1).isComplete()))
          this.yLine += f6;
        if (i5 >= ((PdfPTable)localObject1).size())
        {
          this.yLine -= ((PdfPTable)localObject1).spacingAfter();
          this.compositeElements.removeFirst();
          this.splittedRow = false;
          this.listIdx = 0;
          continue;
        }
        if (this.splittedRow)
        {
          localObject3 = ((PdfPTable)localObject1).getRows();
          for (int i7 = this.listIdx; i7 < i5; i7++)
            ((ArrayList)localObject3).set(i7, null);
        }
        this.listIdx = i5;
        return 2;
      }
      if (localElement.type() == 55)
      {
        if (!paramBoolean)
        {
          localObject1 = (DrawInterface)localElement;
          ((DrawInterface)localObject1).draw(this.canvas, this.leftX, this.minY, this.rightX, this.maxY, this.yLine);
        }
        this.compositeElements.removeFirst();
        continue;
      }
      this.compositeElements.removeFirst();
    }
  }

  public PdfContentByte getCanvas()
  {
    return this.canvas;
  }

  public void setCanvas(PdfContentByte paramPdfContentByte)
  {
    this.canvas = paramPdfContentByte;
    this.canvases = null;
    if (this.compositeColumn != null)
      this.compositeColumn.setCanvas(paramPdfContentByte);
  }

  public void setCanvases(PdfContentByte[] paramArrayOfPdfContentByte)
  {
    this.canvases = paramArrayOfPdfContentByte;
    this.canvas = paramArrayOfPdfContentByte[3];
    if (this.compositeColumn != null)
      this.compositeColumn.setCanvases(paramArrayOfPdfContentByte);
  }

  public PdfContentByte[] getCanvases()
  {
    return this.canvases;
  }

  public boolean zeroHeightElement()
  {
    return (this.composite) && (!this.compositeElements.isEmpty()) && (((Element)this.compositeElements.getFirst()).type() == 55);
  }

  public boolean isUseAscender()
  {
    return this.useAscender;
  }

  public void setUseAscender(boolean paramBoolean)
  {
    this.useAscender = paramBoolean;
  }

  public static boolean hasMoreText(int paramInt)
  {
    return (paramInt & 0x1) == 0;
  }

  public float getFilledWidth()
  {
    return this.filledWidth;
  }

  public void setFilledWidth(float paramFloat)
  {
    this.filledWidth = paramFloat;
  }

  public void updateFilledWidth(float paramFloat)
  {
    if (paramFloat > this.filledWidth)
      this.filledWidth = paramFloat;
  }

  public boolean isAdjustFirstLine()
  {
    return this.adjustFirstLine;
  }

  public void setAdjustFirstLine(boolean paramBoolean)
  {
    this.adjustFirstLine = paramBoolean;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ColumnText
 * JD-Core Version:    0.6.0
 */