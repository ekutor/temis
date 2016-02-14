package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.codec.BmpImage;
import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

public class MetaDo
{
  public static final int META_SETBKCOLOR = 513;
  public static final int META_SETBKMODE = 258;
  public static final int META_SETMAPMODE = 259;
  public static final int META_SETROP2 = 260;
  public static final int META_SETRELABS = 261;
  public static final int META_SETPOLYFILLMODE = 262;
  public static final int META_SETSTRETCHBLTMODE = 263;
  public static final int META_SETTEXTCHAREXTRA = 264;
  public static final int META_SETTEXTCOLOR = 521;
  public static final int META_SETTEXTJUSTIFICATION = 522;
  public static final int META_SETWINDOWORG = 523;
  public static final int META_SETWINDOWEXT = 524;
  public static final int META_SETVIEWPORTORG = 525;
  public static final int META_SETVIEWPORTEXT = 526;
  public static final int META_OFFSETWINDOWORG = 527;
  public static final int META_SCALEWINDOWEXT = 1040;
  public static final int META_OFFSETVIEWPORTORG = 529;
  public static final int META_SCALEVIEWPORTEXT = 1042;
  public static final int META_LINETO = 531;
  public static final int META_MOVETO = 532;
  public static final int META_EXCLUDECLIPRECT = 1045;
  public static final int META_INTERSECTCLIPRECT = 1046;
  public static final int META_ARC = 2071;
  public static final int META_ELLIPSE = 1048;
  public static final int META_FLOODFILL = 1049;
  public static final int META_PIE = 2074;
  public static final int META_RECTANGLE = 1051;
  public static final int META_ROUNDRECT = 1564;
  public static final int META_PATBLT = 1565;
  public static final int META_SAVEDC = 30;
  public static final int META_SETPIXEL = 1055;
  public static final int META_OFFSETCLIPRGN = 544;
  public static final int META_TEXTOUT = 1313;
  public static final int META_BITBLT = 2338;
  public static final int META_STRETCHBLT = 2851;
  public static final int META_POLYGON = 804;
  public static final int META_POLYLINE = 805;
  public static final int META_ESCAPE = 1574;
  public static final int META_RESTOREDC = 295;
  public static final int META_FILLREGION = 552;
  public static final int META_FRAMEREGION = 1065;
  public static final int META_INVERTREGION = 298;
  public static final int META_PAINTREGION = 299;
  public static final int META_SELECTCLIPREGION = 300;
  public static final int META_SELECTOBJECT = 301;
  public static final int META_SETTEXTALIGN = 302;
  public static final int META_CHORD = 2096;
  public static final int META_SETMAPPERFLAGS = 561;
  public static final int META_EXTTEXTOUT = 2610;
  public static final int META_SETDIBTODEV = 3379;
  public static final int META_SELECTPALETTE = 564;
  public static final int META_REALIZEPALETTE = 53;
  public static final int META_ANIMATEPALETTE = 1078;
  public static final int META_SETPALENTRIES = 55;
  public static final int META_POLYPOLYGON = 1336;
  public static final int META_RESIZEPALETTE = 313;
  public static final int META_DIBBITBLT = 2368;
  public static final int META_DIBSTRETCHBLT = 2881;
  public static final int META_DIBCREATEPATTERNBRUSH = 322;
  public static final int META_STRETCHDIB = 3907;
  public static final int META_EXTFLOODFILL = 1352;
  public static final int META_DELETEOBJECT = 496;
  public static final int META_CREATEPALETTE = 247;
  public static final int META_CREATEPATTERNBRUSH = 505;
  public static final int META_CREATEPENINDIRECT = 762;
  public static final int META_CREATEFONTINDIRECT = 763;
  public static final int META_CREATEBRUSHINDIRECT = 764;
  public static final int META_CREATEREGION = 1791;
  public PdfContentByte cb;
  public InputMeta in;
  int left;
  int top;
  int right;
  int bottom;
  int inch;
  MetaState state = new MetaState();

  public MetaDo(InputStream paramInputStream, PdfContentByte paramPdfContentByte)
  {
    this.cb = paramPdfContentByte;
    this.in = new InputMeta(paramInputStream);
  }

  public void readAll()
    throws IOException, DocumentException
  {
    if (this.in.readInt() != -1698247209)
      throw new DocumentException("Not a placeable windows metafile");
    this.in.readWord();
    this.left = this.in.readShort();
    this.top = this.in.readShort();
    this.right = this.in.readShort();
    this.bottom = this.in.readShort();
    this.inch = this.in.readWord();
    this.state.setScalingX((this.right - this.left) / this.inch * 72.0F);
    this.state.setScalingY((this.bottom - this.top) / this.inch * 72.0F);
    this.state.setOffsetWx(this.left);
    this.state.setOffsetWy(this.top);
    this.state.setExtentWx(this.right - this.left);
    this.state.setExtentWy(this.bottom - this.top);
    this.in.readInt();
    this.in.readWord();
    this.in.skip(18);
    this.cb.setLineCap(1);
    this.cb.setLineJoin(1);
    while (true)
    {
      int k = this.in.getLength();
      int i = this.in.readInt();
      if (i < 3)
        break;
      int j = this.in.readWord();
      Object localObject;
      int m;
      int i2;
      int i6;
      int i8;
      int i11;
      int i14;
      float f1;
      float f2;
      float f3;
      float f4;
      float f5;
      float f6;
      float f8;
      float f10;
      float f12;
      float f14;
      float f15;
      float f16;
      ArrayList localArrayList;
      float[] arrayOfFloat;
      int i24;
      int n;
      int i7;
      int i9;
      int i15;
      int i13;
      int i5;
      switch (j)
      {
      case 0:
        break;
      case 247:
      case 322:
      case 1791:
        this.state.addMetaObject(new MetaObject());
        break;
      case 762:
        localObject = new MetaPen();
        ((MetaPen)localObject).init(this.in);
        this.state.addMetaObject((MetaObject)localObject);
        break;
      case 764:
        localObject = new MetaBrush();
        ((MetaBrush)localObject).init(this.in);
        this.state.addMetaObject((MetaObject)localObject);
        break;
      case 763:
        localObject = new MetaFont();
        ((MetaFont)localObject).init(this.in);
        this.state.addMetaObject((MetaObject)localObject);
        break;
      case 301:
        m = this.in.readWord();
        this.state.selectMetaObject(m, this.cb);
        break;
      case 496:
        m = this.in.readWord();
        this.state.deleteMetaObject(m);
        break;
      case 30:
        this.state.saveState(this.cb);
        break;
      case 295:
        m = this.in.readShort();
        this.state.restoreState(m, this.cb);
        break;
      case 523:
        this.state.setOffsetWy(this.in.readShort());
        this.state.setOffsetWx(this.in.readShort());
        break;
      case 524:
        this.state.setExtentWy(this.in.readShort());
        this.state.setExtentWx(this.in.readShort());
        break;
      case 532:
        m = this.in.readShort();
        Point localPoint1 = new Point(this.in.readShort(), m);
        this.state.setCurrentPoint(localPoint1);
        break;
      case 531:
        m = this.in.readShort();
        i2 = this.in.readShort();
        Point localPoint2 = this.state.getCurrentPoint();
        this.cb.moveTo(this.state.transformX(localPoint2.x), this.state.transformY(localPoint2.y));
        this.cb.lineTo(this.state.transformX(i2), this.state.transformY(m));
        this.cb.stroke();
        this.state.setCurrentPoint(new Point(i2, m));
        break;
      case 805:
        this.state.setLineJoinPolygon(this.cb);
        m = this.in.readWord();
        i2 = this.in.readShort();
        i6 = this.in.readShort();
        this.cb.moveTo(this.state.transformX(i2), this.state.transformY(i6));
        for (i8 = 1; i8 < m; i8++)
        {
          i2 = this.in.readShort();
          i6 = this.in.readShort();
          this.cb.lineTo(this.state.transformX(i2), this.state.transformY(i6));
        }
        this.cb.stroke();
        break;
      case 804:
        if (!isNullStrokeFill(false))
        {
          m = this.in.readWord();
          i2 = this.in.readShort();
          i6 = this.in.readShort();
          this.cb.moveTo(this.state.transformX(i2), this.state.transformY(i6));
          for (i8 = 1; i8 < m; i8++)
          {
            i11 = this.in.readShort();
            i14 = this.in.readShort();
            this.cb.lineTo(this.state.transformX(i11), this.state.transformY(i14));
          }
          this.cb.lineTo(this.state.transformX(i2), this.state.transformY(i6));
          strokeAndFill();
        }
        break;
      case 1336:
        if (!isNullStrokeFill(false))
        {
          m = this.in.readWord();
          int[] arrayOfInt = new int[m];
          for (i6 = 0; i6 < arrayOfInt.length; i6++)
            arrayOfInt[i6] = this.in.readWord();
          for (i6 = 0; i6 < arrayOfInt.length; i6++)
          {
            i8 = arrayOfInt[i6];
            i11 = this.in.readShort();
            i14 = this.in.readShort();
            this.cb.moveTo(this.state.transformX(i11), this.state.transformY(i14));
            for (int i16 = 1; i16 < i8; i16++)
            {
              int i18 = this.in.readShort();
              int i20 = this.in.readShort();
              this.cb.lineTo(this.state.transformX(i18), this.state.transformY(i20));
            }
            this.cb.lineTo(this.state.transformX(i11), this.state.transformY(i14));
          }
          strokeAndFill();
        }
        break;
      case 1048:
        if (!isNullStrokeFill(this.state.getLineNeutral()))
        {
          m = this.in.readShort();
          int i3 = this.in.readShort();
          i6 = this.in.readShort();
          i8 = this.in.readShort();
          this.cb.arc(this.state.transformX(i8), this.state.transformY(m), this.state.transformX(i3), this.state.transformY(i6), 0.0F, 360.0F);
          strokeAndFill();
        }
        break;
      case 2071:
        if (!isNullStrokeFill(this.state.getLineNeutral()))
        {
          f1 = this.state.transformY(this.in.readShort());
          f2 = this.state.transformX(this.in.readShort());
          f3 = this.state.transformY(this.in.readShort());
          f4 = this.state.transformX(this.in.readShort());
          f5 = this.state.transformY(this.in.readShort());
          f6 = this.state.transformX(this.in.readShort());
          f8 = this.state.transformY(this.in.readShort());
          f10 = this.state.transformX(this.in.readShort());
          f12 = (f6 + f10) / 2.0F;
          f14 = (f8 + f5) / 2.0F;
          f15 = getArc(f12, f14, f4, f3);
          f16 = getArc(f12, f14, f2, f1);
          f16 -= f15;
          if (f16 <= 0.0F)
            f16 += 360.0F;
          this.cb.arc(f10, f5, f6, f8, f15, f16);
          this.cb.stroke();
        }
        break;
      case 2074:
        if (!isNullStrokeFill(this.state.getLineNeutral()))
        {
          f1 = this.state.transformY(this.in.readShort());
          f2 = this.state.transformX(this.in.readShort());
          f3 = this.state.transformY(this.in.readShort());
          f4 = this.state.transformX(this.in.readShort());
          f5 = this.state.transformY(this.in.readShort());
          f6 = this.state.transformX(this.in.readShort());
          f8 = this.state.transformY(this.in.readShort());
          f10 = this.state.transformX(this.in.readShort());
          f12 = (f6 + f10) / 2.0F;
          f14 = (f8 + f5) / 2.0F;
          f15 = getArc(f12, f14, f4, f3);
          f16 = getArc(f12, f14, f2, f1);
          f16 -= f15;
          if (f16 <= 0.0F)
            f16 += 360.0F;
          localArrayList = PdfContentByte.bezierArc(f10, f5, f6, f8, f15, f16);
          if (!localArrayList.isEmpty())
          {
            arrayOfFloat = (float[])localArrayList.get(0);
            this.cb.moveTo(f12, f14);
            this.cb.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
            for (i24 = 0; i24 < localArrayList.size(); i24++)
            {
              arrayOfFloat = (float[])localArrayList.get(i24);
              this.cb.curveTo(arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5], arrayOfFloat[6], arrayOfFloat[7]);
            }
            this.cb.lineTo(f12, f14);
            strokeAndFill();
          }
        }
        break;
      case 2096:
        if (!isNullStrokeFill(this.state.getLineNeutral()))
        {
          f1 = this.state.transformY(this.in.readShort());
          f2 = this.state.transformX(this.in.readShort());
          f3 = this.state.transformY(this.in.readShort());
          f4 = this.state.transformX(this.in.readShort());
          f5 = this.state.transformY(this.in.readShort());
          f6 = this.state.transformX(this.in.readShort());
          f8 = this.state.transformY(this.in.readShort());
          f10 = this.state.transformX(this.in.readShort());
          f12 = (f6 + f10) / 2.0F;
          f14 = (f8 + f5) / 2.0F;
          f15 = getArc(f12, f14, f4, f3);
          f16 = getArc(f12, f14, f2, f1);
          f16 -= f15;
          if (f16 <= 0.0F)
            f16 += 360.0F;
          localArrayList = PdfContentByte.bezierArc(f10, f5, f6, f8, f15, f16);
          if (!localArrayList.isEmpty())
          {
            arrayOfFloat = (float[])localArrayList.get(0);
            f12 = arrayOfFloat[0];
            f14 = arrayOfFloat[1];
            this.cb.moveTo(f12, f14);
            for (i24 = 0; i24 < localArrayList.size(); i24++)
            {
              arrayOfFloat = (float[])localArrayList.get(i24);
              this.cb.curveTo(arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5], arrayOfFloat[6], arrayOfFloat[7]);
            }
            this.cb.lineTo(f12, f14);
            strokeAndFill();
          }
        }
        break;
      case 1051:
        if (!isNullStrokeFill(true))
        {
          f1 = this.state.transformY(this.in.readShort());
          f2 = this.state.transformX(this.in.readShort());
          f3 = this.state.transformY(this.in.readShort());
          f4 = this.state.transformX(this.in.readShort());
          this.cb.rectangle(f4, f1, f2 - f4, f3 - f1);
          strokeAndFill();
        }
        break;
      case 1564:
        if (!isNullStrokeFill(true))
        {
          f1 = this.state.transformY(0) - this.state.transformY(this.in.readShort());
          f2 = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
          f3 = this.state.transformY(this.in.readShort());
          f4 = this.state.transformX(this.in.readShort());
          f5 = this.state.transformY(this.in.readShort());
          f6 = this.state.transformX(this.in.readShort());
          this.cb.roundRectangle(f6, f3, f4 - f6, f5 - f3, (f1 + f2) / 4.0F);
          strokeAndFill();
        }
        break;
      case 1046:
        f1 = this.state.transformY(this.in.readShort());
        f2 = this.state.transformX(this.in.readShort());
        f3 = this.state.transformY(this.in.readShort());
        f4 = this.state.transformX(this.in.readShort());
        this.cb.rectangle(f4, f1, f2 - f4, f3 - f1);
        this.cb.eoClip();
        this.cb.newPath();
        break;
      case 2610:
        n = this.in.readShort();
        int i4 = this.in.readShort();
        i7 = this.in.readWord();
        i9 = this.in.readWord();
        int i12 = 0;
        i15 = 0;
        int i17 = 0;
        int i19 = 0;
        if ((i9 & 0x6) != 0)
        {
          i12 = this.in.readShort();
          i15 = this.in.readShort();
          i17 = this.in.readShort();
          i19 = this.in.readShort();
        }
        byte[] arrayOfByte2 = new byte[i7];
        for (int i21 = 0; i21 < i7; i21++)
        {
          int i22 = (byte)this.in.readByte();
          if (i22 == 0)
            break;
          arrayOfByte2[i21] = i22;
        }
        String str2;
        try
        {
          str2 = new String(arrayOfByte2, 0, i21, "Cp1252");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException2)
        {
          str2 = new String(arrayOfByte2, 0, i21);
        }
        outputText(i4, n, i9, i12, i15, i17, i19, str2);
        break;
      case 1313:
        n = this.in.readWord();
        byte[] arrayOfByte1 = new byte[n];
        for (i7 = 0; i7 < n; i7++)
        {
          i9 = (byte)this.in.readByte();
          if (i9 == 0)
            break;
          arrayOfByte1[i7] = i9;
        }
        String str1;
        try
        {
          str1 = new String(arrayOfByte1, 0, i7, "Cp1252");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException1)
        {
          str1 = new String(arrayOfByte1, 0, i7);
        }
        n = n + 1 & 0xFFFE;
        this.in.skip(n - i7);
        i13 = this.in.readShort();
        i15 = this.in.readShort();
        outputText(i15, i13, 0, 0, 0, 0, 0, str1);
        break;
      case 513:
        this.state.setCurrentBackgroundColor(this.in.readColor());
        break;
      case 521:
        this.state.setCurrentTextColor(this.in.readColor());
        break;
      case 302:
        this.state.setTextAlign(this.in.readWord());
        break;
      case 258:
        this.state.setBackgroundMode(this.in.readWord());
        break;
      case 262:
        this.state.setPolyFillMode(this.in.readWord());
        break;
      case 1055:
        Color localColor = this.in.readColor();
        i5 = this.in.readShort();
        i7 = this.in.readShort();
        this.cb.saveState();
        this.cb.setColorFill(localColor);
        this.cb.rectangle(this.state.transformX(i7), this.state.transformY(i5), 0.2F, 0.2F);
        this.cb.fill();
        this.cb.restoreState();
        break;
      case 2881:
      case 3907:
        int i1 = this.in.readInt();
        if (j == 3907)
          this.in.readWord();
        i5 = this.in.readShort();
        i7 = this.in.readShort();
        int i10 = this.in.readShort();
        i13 = this.in.readShort();
        float f7 = this.state.transformY(this.in.readShort()) - this.state.transformY(0);
        float f9 = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
        float f11 = this.state.transformY(this.in.readShort());
        float f13 = this.state.transformX(this.in.readShort());
        byte[] arrayOfByte3 = new byte[i * 2 - (this.in.getLength() - k)];
        for (int i23 = 0; i23 < arrayOfByte3.length; i23++)
          arrayOfByte3[i23] = (byte)this.in.readByte();
        try
        {
          ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte3);
          Image localImage = BmpImage.getImage(localByteArrayInputStream, true, arrayOfByte3.length);
          this.cb.saveState();
          this.cb.rectangle(f13, f11, f9, f7);
          this.cb.clip();
          this.cb.newPath();
          localImage.scaleAbsolute(f9 * localImage.getWidth() / i7, -f7 * localImage.getHeight() / i5);
          localImage.setAbsolutePosition(f13 - f9 * i13 / i7, f11 + f7 * i10 / i5 - localImage.getScaledHeight());
          this.cb.addImage(localImage);
          this.cb.restoreState();
        }
        catch (Exception localException)
        {
        }
      }
      this.in.skip(i * 2 - (this.in.getLength() - k));
    }
    this.state.cleanup(this.cb);
  }

  public void outputText(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, String paramString)
  {
    MetaFont localMetaFont = this.state.getCurrentFont();
    float f1 = this.state.transformX(paramInt1);
    float f2 = this.state.transformY(paramInt2);
    float f3 = this.state.transformAngle(localMetaFont.getAngle());
    float f4 = (float)Math.sin(f3);
    float f5 = (float)Math.cos(f3);
    float f6 = localMetaFont.getFontSize(this.state);
    BaseFont localBaseFont = localMetaFont.getFont();
    int i = this.state.getTextAlign();
    float f7 = localBaseFont.getWidthPoint(paramString, f6);
    float f8 = 0.0F;
    float f9 = 0.0F;
    float f10 = localBaseFont.getFontDescriptor(3, f6);
    float f11 = localBaseFont.getFontDescriptor(8, f6);
    this.cb.saveState();
    this.cb.concatCTM(f5, f4, -f4, f5, f1, f2);
    if ((i & 0x6) == 6)
      f8 = -f7 / 2.0F;
    else if ((i & 0x2) == 2)
      f8 = -f7;
    if ((i & 0x18) == 24)
      f9 = 0.0F;
    else if ((i & 0x8) == 8)
      f9 = -f10;
    else
      f9 = -f11;
    if (this.state.getBackgroundMode() == 2)
    {
      localColor = this.state.getCurrentBackgroundColor();
      this.cb.setColorFill(localColor);
      this.cb.rectangle(f8, f9 + f10, f7, f11 - f10);
      this.cb.fill();
    }
    Color localColor = this.state.getCurrentTextColor();
    this.cb.setColorFill(localColor);
    this.cb.beginText();
    this.cb.setFontAndSize(localBaseFont, f6);
    this.cb.setTextMatrix(f8, f9);
    this.cb.showText(paramString);
    this.cb.endText();
    if (localMetaFont.isUnderline())
    {
      this.cb.rectangle(f8, f9 - f6 / 4.0F, f7, f6 / 15.0F);
      this.cb.fill();
    }
    if (localMetaFont.isStrikeout())
    {
      this.cb.rectangle(f8, f9 + f6 / 3.0F, f7, f6 / 15.0F);
      this.cb.fill();
    }
    this.cb.restoreState();
  }

  public boolean isNullStrokeFill(boolean paramBoolean)
  {
    MetaPen localMetaPen = this.state.getCurrentPen();
    MetaBrush localMetaBrush = this.state.getCurrentBrush();
    int i = localMetaPen.getStyle() == 5 ? 1 : 0;
    int j = localMetaBrush.getStyle();
    int k = (j == 0) || ((j == 2) && (this.state.getBackgroundMode() == 2)) ? 1 : 0;
    int m = (i != 0) && (k == 0) ? 1 : 0;
    if (i == 0)
      if (paramBoolean)
        this.state.setLineJoinRectangle(this.cb);
      else
        this.state.setLineJoinPolygon(this.cb);
    return m;
  }

  public void strokeAndFill()
  {
    MetaPen localMetaPen = this.state.getCurrentPen();
    MetaBrush localMetaBrush = this.state.getCurrentBrush();
    int i = localMetaPen.getStyle();
    int j = localMetaBrush.getStyle();
    if (i == 5)
    {
      this.cb.closePath();
      if (this.state.getPolyFillMode() == 1)
        this.cb.eoFill();
      else
        this.cb.fill();
    }
    else
    {
      int k = (j == 0) || ((j == 2) && (this.state.getBackgroundMode() == 2)) ? 1 : 0;
      if (k != 0)
      {
        if (this.state.getPolyFillMode() == 1)
          this.cb.closePathEoFillStroke();
        else
          this.cb.closePathFillStroke();
      }
      else
        this.cb.closePathStroke();
    }
  }

  static float getArc(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    double d = Math.atan2(paramFloat4 - paramFloat2, paramFloat3 - paramFloat1);
    if (d < 0.0D)
      d += 6.283185307179586D;
    return (float)(d / 3.141592653589793D * 180.0D);
  }

  public static byte[] wrapBMP(Image paramImage)
    throws IOException
  {
    if (paramImage.getOriginalType() != 4)
      throw new IOException("Only BMP can be wrapped in WMF.");
    byte[] arrayOfByte = null;
    if (paramImage.getOriginalData() == null)
    {
      InputStream localInputStream = paramImage.getUrl().openStream();
      ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
      int j = 0;
      while ((j = localInputStream.read()) != -1)
        localByteArrayOutputStream1.write(j);
      localInputStream.close();
      arrayOfByte = localByteArrayOutputStream1.toByteArray();
    }
    else
    {
      arrayOfByte = paramImage.getOriginalData();
    }
    int i = arrayOfByte.length - 14 + 1 >>> 1;
    ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
    writeWord(localByteArrayOutputStream2, 1);
    writeWord(localByteArrayOutputStream2, 9);
    writeWord(localByteArrayOutputStream2, 768);
    writeDWord(localByteArrayOutputStream2, 23 + (13 + i) + 3);
    writeWord(localByteArrayOutputStream2, 1);
    writeDWord(localByteArrayOutputStream2, 14 + i);
    writeWord(localByteArrayOutputStream2, 0);
    writeDWord(localByteArrayOutputStream2, 4);
    writeWord(localByteArrayOutputStream2, 259);
    writeWord(localByteArrayOutputStream2, 8);
    writeDWord(localByteArrayOutputStream2, 5);
    writeWord(localByteArrayOutputStream2, 523);
    writeWord(localByteArrayOutputStream2, 0);
    writeWord(localByteArrayOutputStream2, 0);
    writeDWord(localByteArrayOutputStream2, 5);
    writeWord(localByteArrayOutputStream2, 524);
    writeWord(localByteArrayOutputStream2, (int)paramImage.getHeight());
    writeWord(localByteArrayOutputStream2, (int)paramImage.getWidth());
    writeDWord(localByteArrayOutputStream2, 13 + i);
    writeWord(localByteArrayOutputStream2, 2881);
    writeDWord(localByteArrayOutputStream2, 13369376);
    writeWord(localByteArrayOutputStream2, (int)paramImage.getHeight());
    writeWord(localByteArrayOutputStream2, (int)paramImage.getWidth());
    writeWord(localByteArrayOutputStream2, 0);
    writeWord(localByteArrayOutputStream2, 0);
    writeWord(localByteArrayOutputStream2, (int)paramImage.getHeight());
    writeWord(localByteArrayOutputStream2, (int)paramImage.getWidth());
    writeWord(localByteArrayOutputStream2, 0);
    writeWord(localByteArrayOutputStream2, 0);
    localByteArrayOutputStream2.write(arrayOfByte, 14, arrayOfByte.length - 14);
    if ((arrayOfByte.length & 0x1) == 1)
      localByteArrayOutputStream2.write(0);
    writeDWord(localByteArrayOutputStream2, 3);
    writeWord(localByteArrayOutputStream2, 0);
    localByteArrayOutputStream2.close();
    return localByteArrayOutputStream2.toByteArray();
  }

  public static void writeWord(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write(paramInt & 0xFF);
    paramOutputStream.write(paramInt >>> 8 & 0xFF);
  }

  public static void writeDWord(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    writeWord(paramOutputStream, paramInt & 0xFFFF);
    writeWord(paramOutputStream, paramInt >>> 16 & 0xFFFF);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.codec.wmf.MetaDo
 * JD-Core Version:    0.6.0
 */