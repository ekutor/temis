package com.lowagie.text.pdf;

import com.lowagie.text.pdf.internal.PolylineShape;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Line2D.Double;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class PdfGraphics2D extends Graphics2D
{
  private static final int FILL = 1;
  private static final int STROKE = 2;
  private static final int CLIP = 3;
  private BasicStroke strokeOne = new BasicStroke(1.0F);
  private static final AffineTransform IDENTITY = new AffineTransform();
  private Font font;
  private BaseFont baseFont;
  private float fontSize;
  private AffineTransform transform;
  private Paint paint;
  private Color background;
  private float width;
  private float height;
  private Area clip;
  private RenderingHints rhints = new RenderingHints(null);
  private Stroke stroke;
  private Stroke originalStroke;
  private PdfContentByte cb;
  private HashMap baseFonts;
  private boolean disposeCalled = false;
  private FontMapper fontMapper;
  private ArrayList kids;
  private boolean kid = false;
  private Graphics2D dg2 = new BufferedImage(2, 2, 1).createGraphics();
  private boolean onlyShapes = false;
  private Stroke oldStroke;
  private Paint paintFill;
  private Paint paintStroke;
  private MediaTracker mediaTracker;
  protected boolean underline;
  protected PdfGState[] fillGState = new PdfGState[256];
  protected PdfGState[] strokeGState = new PdfGState[256];
  protected int currentFillGState = 255;
  protected int currentStrokeGState = 255;
  public static final int AFM_DIVISOR = 1000;
  private boolean convertImagesToJPEG = false;
  private float jpegQuality = 0.95F;
  private float alpha;
  private Composite composite;
  private Paint realPaint;

  private PdfGraphics2D()
  {
    this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
  }

  PdfGraphics2D(PdfContentByte paramPdfContentByte, float paramFloat1, float paramFloat2, FontMapper paramFontMapper, boolean paramBoolean1, boolean paramBoolean2, float paramFloat3)
  {
    this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    this.convertImagesToJPEG = paramBoolean2;
    this.jpegQuality = paramFloat3;
    this.onlyShapes = paramBoolean1;
    this.transform = new AffineTransform();
    this.baseFonts = new HashMap();
    if (!paramBoolean1)
    {
      this.fontMapper = paramFontMapper;
      if (this.fontMapper == null)
        this.fontMapper = new DefaultFontMapper();
    }
    this.paint = Color.black;
    this.background = Color.white;
    setFont(new Font("sanserif", 0, 12));
    this.cb = paramPdfContentByte;
    paramPdfContentByte.saveState();
    this.width = paramFloat1;
    this.height = paramFloat2;
    this.clip = new Area(new Rectangle2D.Float(0.0F, 0.0F, paramFloat1, paramFloat2));
    clip(this.clip);
    this.originalStroke = (this.stroke = this.oldStroke = this.strokeOne);
    setStrokeDiff(this.stroke, null);
    paramPdfContentByte.saveState();
  }

  public void draw(Shape paramShape)
  {
    followPath(paramShape, 2);
  }

  public boolean drawImage(java.awt.Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, null, paramAffineTransform, null, paramImageObserver);
  }

  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    BufferedImage localBufferedImage = paramBufferedImage;
    if (paramBufferedImageOp != null)
    {
      localBufferedImage = paramBufferedImageOp.createCompatibleDestImage(paramBufferedImage, paramBufferedImage.getColorModel());
      localBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, localBufferedImage);
    }
    drawImage(localBufferedImage, paramInt1, paramInt2, null);
  }

  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform)
  {
    Object localObject = null;
    if ((paramRenderedImage instanceof BufferedImage))
    {
      localObject = (BufferedImage)paramRenderedImage;
    }
    else
    {
      ColorModel localColorModel = paramRenderedImage.getColorModel();
      int i = paramRenderedImage.getWidth();
      int j = paramRenderedImage.getHeight();
      WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(i, j);
      boolean bool = localColorModel.isAlphaPremultiplied();
      Hashtable localHashtable = new Hashtable();
      String[] arrayOfString = paramRenderedImage.getPropertyNames();
      if (arrayOfString != null)
        for (int k = 0; k < arrayOfString.length; k++)
          localHashtable.put(arrayOfString[k], paramRenderedImage.getProperty(arrayOfString[k]));
      BufferedImage localBufferedImage = new BufferedImage(localColorModel, localWritableRaster, bool, localHashtable);
      paramRenderedImage.copyData(localWritableRaster);
      localObject = localBufferedImage;
    }
    drawImage((java.awt.Image)localObject, paramAffineTransform, null);
  }

  public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform)
  {
    drawRenderedImage(paramRenderableImage.createDefaultRendering(), paramAffineTransform);
  }

  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    drawString(paramString, paramInt1, paramInt2);
  }

  public static double asPoints(double paramDouble, int paramInt)
  {
    return paramDouble * paramInt / 1000.0D;
  }

  protected void doAttributes(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    this.underline = false;
    Set localSet = paramAttributedCharacterIterator.getAttributes().keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      AttributedCharacterIterator.Attribute localAttribute = (AttributedCharacterIterator.Attribute)localIterator.next();
      if (!(localAttribute instanceof TextAttribute))
        continue;
      TextAttribute localTextAttribute = (TextAttribute)localAttribute;
      if (localTextAttribute.equals(TextAttribute.FONT))
      {
        localObject = (Font)paramAttributedCharacterIterator.getAttributes().get(localTextAttribute);
        setFont((Font)localObject);
        continue;
      }
      if (localTextAttribute.equals(TextAttribute.UNDERLINE))
      {
        if (paramAttributedCharacterIterator.getAttributes().get(localTextAttribute) != TextAttribute.UNDERLINE_ON)
          continue;
        this.underline = true;
        continue;
      }
      if (localTextAttribute.equals(TextAttribute.SIZE))
      {
        localObject = paramAttributedCharacterIterator.getAttributes().get(localTextAttribute);
        if ((localObject instanceof Integer))
        {
          int i = ((Integer)localObject).intValue();
          setFont(getFont().deriveFont(getFont().getStyle(), i));
          continue;
        }
        if (!(localObject instanceof Float))
          continue;
        float f = ((Float)localObject).floatValue();
        setFont(getFont().deriveFont(getFont().getStyle(), f));
        continue;
      }
      if (localTextAttribute.equals(TextAttribute.FOREGROUND))
      {
        setColor((Color)paramAttributedCharacterIterator.getAttributes().get(localTextAttribute));
        continue;
      }
      if (localTextAttribute.equals(TextAttribute.FAMILY))
      {
        localObject = getFont();
        localMap = ((Font)localObject).getAttributes();
        localMap.put(TextAttribute.FAMILY, paramAttributedCharacterIterator.getAttributes().get(localTextAttribute));
        setFont(((Font)localObject).deriveFont(localMap));
        continue;
      }
      if (localTextAttribute.equals(TextAttribute.POSTURE))
      {
        localObject = getFont();
        localMap = ((Font)localObject).getAttributes();
        localMap.put(TextAttribute.POSTURE, paramAttributedCharacterIterator.getAttributes().get(localTextAttribute));
        setFont(((Font)localObject).deriveFont(localMap));
        continue;
      }
      if (!localTextAttribute.equals(TextAttribute.WEIGHT))
        continue;
      Object localObject = getFont();
      Map localMap = ((Font)localObject).getAttributes();
      localMap.put(TextAttribute.WEIGHT, paramAttributedCharacterIterator.getAttributes().get(localTextAttribute));
      setFont(((Font)localObject).deriveFont(localMap));
    }
  }

  public void drawString(String paramString, float paramFloat1, float paramFloat2)
  {
    if (paramString.length() == 0)
      return;
    setFillPaint();
    if (this.onlyShapes)
    {
      drawGlyphVector(this.font.layoutGlyphVector(getFontRenderContext(), paramString.toCharArray(), 0, paramString.length(), 0), paramFloat1, paramFloat2);
    }
    else
    {
      int i = 0;
      AffineTransform localAffineTransform1 = getTransform();
      AffineTransform localAffineTransform2 = getTransform();
      localAffineTransform2.translate(paramFloat1, paramFloat2);
      localAffineTransform2.concatenate(this.font.getTransform());
      setTransform(localAffineTransform2);
      AffineTransform localAffineTransform3 = normalizeMatrix();
      AffineTransform localAffineTransform4 = AffineTransform.getScaleInstance(1.0D, -1.0D);
      localAffineTransform3.concatenate(localAffineTransform4);
      double[] arrayOfDouble = new double[6];
      localAffineTransform3.getMatrix(arrayOfDouble);
      this.cb.beginText();
      this.cb.setFontAndSize(this.baseFont, this.fontSize);
      if ((this.font.isItalic()) && (this.font.getFontName().equals(this.font.getName())))
      {
        float f1 = this.baseFont.getFontDescriptor(4, 1000.0F);
        float f2 = this.font.getItalicAngle();
        if (f2 == 0.0F)
          f2 = 15.0F;
        else
          f2 = -f2;
        if (f1 == 0.0F)
          arrayOfDouble[2] = (f2 / 100.0F);
      }
      this.cb.setTextMatrix((float)arrayOfDouble[0], (float)arrayOfDouble[1], (float)arrayOfDouble[2], (float)arrayOfDouble[3], (float)arrayOfDouble[4], (float)arrayOfDouble[5]);
      Float localFloat1 = (Float)this.font.getAttributes().get(TextAttribute.WIDTH);
      localFloat1 = localFloat1 == null ? TextAttribute.WIDTH_REGULAR : localFloat1;
      if (!TextAttribute.WIDTH_REGULAR.equals(localFloat1))
        this.cb.setHorizontalScaling(100.0F / localFloat1.floatValue());
      Object localObject2;
      if (this.baseFont.getPostscriptFontName().toLowerCase().indexOf("bold") < 0)
      {
        Float localFloat2 = (Float)this.font.getAttributes().get(TextAttribute.WEIGHT);
        if (localFloat2 == null)
          localFloat2 = this.font.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR;
        if (((this.font.isBold()) || (localFloat2.floatValue() >= TextAttribute.WEIGHT_SEMIBOLD.floatValue())) && (this.font.getFontName().equals(this.font.getName())))
        {
          float f3 = this.font.getSize2D() * (localFloat2.floatValue() - TextAttribute.WEIGHT_REGULAR.floatValue()) / 30.0F;
          if ((f3 != 1.0F) && ((this.realPaint instanceof Color)))
          {
            this.cb.setTextRenderingMode(2);
            this.cb.setLineWidth(f3);
            Color localColor = (Color)this.realPaint;
            int j = localColor.getAlpha();
            if (j != this.currentStrokeGState)
            {
              this.currentStrokeGState = j;
              localObject2 = this.strokeGState[j];
              if (localObject2 == null)
              {
                localObject2 = new PdfGState();
                ((PdfGState)localObject2).setStrokeOpacity(j / 255.0F);
                this.strokeGState[j] = localObject2;
              }
              this.cb.setGState((PdfGState)localObject2);
            }
            this.cb.setColorStroke(localColor);
            i = 1;
          }
        }
      }
      double d1 = 0.0D;
      if (this.font.getSize2D() > 0.0F)
      {
        float f4 = 1000.0F / this.font.getSize2D();
        Font localFont = this.font.deriveFont(AffineTransform.getScaleInstance(f4, f4));
        d1 = localFont.getStringBounds(paramString, getFontRenderContext()).getWidth();
        if (localFont.isTransformed())
          d1 /= f4;
      }
      Object localObject1 = getRenderingHint(HyperLinkKey.KEY_INSTANCE);
      float f5;
      if ((localObject1 != null) && (!localObject1.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)))
      {
        f5 = 1000.0F / this.font.getSize2D();
        localObject2 = this.font.deriveFont(AffineTransform.getScaleInstance(f5, f5));
        double d3 = ((Font)localObject2).getStringBounds(paramString, getFontRenderContext()).getHeight();
        if (((Font)localObject2).isTransformed())
          d3 /= f5;
        double d4 = this.cb.getXTLM();
        double d5 = this.cb.getYTLM();
        PdfAction localPdfAction = new PdfAction(localObject1.toString());
        this.cb.setAction(localPdfAction, (float)d4, (float)d5, (float)(d4 + d1), (float)(d5 + d3));
      }
      if (paramString.length() > 1)
      {
        f5 = ((float)d1 - this.baseFont.getWidthPoint(paramString, this.fontSize)) / (paramString.length() - 1);
        this.cb.setCharacterSpacing(f5);
      }
      this.cb.showText(paramString);
      if (paramString.length() > 1)
        this.cb.setCharacterSpacing(0.0F);
      if (!TextAttribute.WIDTH_REGULAR.equals(localFloat1))
        this.cb.setHorizontalScaling(100.0F);
      if (i != 0)
        this.cb.setTextRenderingMode(0);
      this.cb.endText();
      setTransform(localAffineTransform1);
      if (this.underline)
      {
        int k = 50;
        double d2 = asPoints(k, (int)this.fontSize);
        Stroke localStroke = this.originalStroke;
        setStroke(new BasicStroke((float)d2));
        paramFloat2 = (float)(paramFloat2 + asPoints(k, (int)this.fontSize));
        Line2D.Double localDouble = new Line2D.Double(paramFloat1, paramFloat2, d1 + paramFloat1, paramFloat2);
        draw(localDouble);
        setStroke(localStroke);
      }
    }
  }

  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
  }

  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramAttributedCharacterIterator.getEndIndex());
    int j;
    for (int i = paramAttributedCharacterIterator.first(); i != 65535; j = paramAttributedCharacterIterator.next())
    {
      if (paramAttributedCharacterIterator.getIndex() == paramAttributedCharacterIterator.getRunStart())
      {
        if (localStringBuffer.length() > 0)
        {
          drawString(localStringBuffer.toString(), paramFloat1, paramFloat2);
          FontMetrics localFontMetrics = getFontMetrics();
          paramFloat1 = (float)(paramFloat1 + localFontMetrics.getStringBounds(localStringBuffer.toString(), this).getWidth());
          localStringBuffer.delete(0, localStringBuffer.length());
        }
        doAttributes(paramAttributedCharacterIterator);
      }
      localStringBuffer.append(i);
    }
    drawString(localStringBuffer.toString(), paramFloat1, paramFloat2);
    this.underline = false;
  }

  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    Shape localShape = paramGlyphVector.getOutline(paramFloat1, paramFloat2);
    fill(localShape);
  }

  public void fill(Shape paramShape)
  {
    followPath(paramShape, 1);
  }

  public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean)
  {
    if (paramBoolean)
      paramShape = this.stroke.createStrokedShape(paramShape);
    paramShape = this.transform.createTransformedShape(paramShape);
    Area localArea = new Area(paramShape);
    if (this.clip != null)
      localArea.intersect(this.clip);
    return localArea.intersects(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
  }

  public GraphicsConfiguration getDeviceConfiguration()
  {
    return this.dg2.getDeviceConfiguration();
  }

  public void setComposite(Composite paramComposite)
  {
    if ((paramComposite instanceof AlphaComposite))
    {
      AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
      if (localAlphaComposite.getRule() == 3)
      {
        this.alpha = localAlphaComposite.getAlpha();
        this.composite = localAlphaComposite;
        if ((this.realPaint != null) && ((this.realPaint instanceof Color)))
        {
          Color localColor = (Color)this.realPaint;
          this.paint = new Color(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), (int)(localColor.getAlpha() * this.alpha));
        }
        return;
      }
    }
    this.composite = paramComposite;
    this.alpha = 1.0F;
  }

  public void setPaint(Paint paramPaint)
  {
    if (paramPaint == null)
      return;
    this.paint = paramPaint;
    this.realPaint = paramPaint;
    if (((this.composite instanceof AlphaComposite)) && ((paramPaint instanceof Color)))
    {
      AlphaComposite localAlphaComposite = (AlphaComposite)this.composite;
      if (localAlphaComposite.getRule() == 3)
      {
        Color localColor = (Color)paramPaint;
        this.paint = new Color(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), (int)(localColor.getAlpha() * this.alpha));
        this.realPaint = paramPaint;
      }
    }
  }

  private Stroke transformStroke(Stroke paramStroke)
  {
    if (!(paramStroke instanceof BasicStroke))
      return paramStroke;
    BasicStroke localBasicStroke = (BasicStroke)paramStroke;
    float f = (float)Math.sqrt(Math.abs(this.transform.getDeterminant()));
    float[] arrayOfFloat = localBasicStroke.getDashArray();
    if (arrayOfFloat != null)
      for (int i = 0; i < arrayOfFloat.length; i++)
        arrayOfFloat[i] *= f;
    return new BasicStroke(localBasicStroke.getLineWidth() * f, localBasicStroke.getEndCap(), localBasicStroke.getLineJoin(), localBasicStroke.getMiterLimit(), arrayOfFloat, localBasicStroke.getDashPhase() * f);
  }

  private void setStrokeDiff(Stroke paramStroke1, Stroke paramStroke2)
  {
    if (paramStroke1 == paramStroke2)
      return;
    if (!(paramStroke1 instanceof BasicStroke))
      return;
    BasicStroke localBasicStroke1 = (BasicStroke)paramStroke1;
    boolean bool = paramStroke2 instanceof BasicStroke;
    BasicStroke localBasicStroke2 = null;
    if (bool)
      localBasicStroke2 = (BasicStroke)paramStroke2;
    if ((!bool) || (localBasicStroke1.getLineWidth() != localBasicStroke2.getLineWidth()))
      this.cb.setLineWidth(localBasicStroke1.getLineWidth());
    if ((!bool) || (localBasicStroke1.getEndCap() != localBasicStroke2.getEndCap()))
      switch (localBasicStroke1.getEndCap())
      {
      case 0:
        this.cb.setLineCap(0);
        break;
      case 2:
        this.cb.setLineCap(2);
        break;
      default:
        this.cb.setLineCap(1);
      }
    if ((!bool) || (localBasicStroke1.getLineJoin() != localBasicStroke2.getLineJoin()))
      switch (localBasicStroke1.getLineJoin())
      {
      case 0:
        this.cb.setLineJoin(0);
        break;
      case 2:
        this.cb.setLineJoin(2);
        break;
      default:
        this.cb.setLineJoin(1);
      }
    if ((!bool) || (localBasicStroke1.getMiterLimit() != localBasicStroke2.getMiterLimit()))
      this.cb.setMiterLimit(localBasicStroke1.getMiterLimit());
    int i;
    if (bool)
    {
      if (localBasicStroke1.getDashArray() != null)
      {
        if (localBasicStroke1.getDashPhase() != localBasicStroke2.getDashPhase())
          i = 1;
        else if (!Arrays.equals(localBasicStroke1.getDashArray(), localBasicStroke2.getDashArray()))
          i = 1;
        else
          i = 0;
      }
      else if (localBasicStroke2.getDashArray() != null)
        i = 1;
      else
        i = 0;
    }
    else
      i = 1;
    if (i != 0)
    {
      float[] arrayOfFloat = localBasicStroke1.getDashArray();
      if (arrayOfFloat == null)
      {
        this.cb.setLiteral("[]0 d\n");
      }
      else
      {
        this.cb.setLiteral('[');
        int j = arrayOfFloat.length;
        for (int k = 0; k < j; k++)
        {
          this.cb.setLiteral(arrayOfFloat[k]);
          this.cb.setLiteral(' ');
        }
        this.cb.setLiteral(']');
        this.cb.setLiteral(localBasicStroke1.getDashPhase());
        this.cb.setLiteral(" d\n");
      }
    }
  }

  public void setStroke(Stroke paramStroke)
  {
    this.originalStroke = paramStroke;
    this.stroke = transformStroke(paramStroke);
  }

  public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject)
  {
    if (paramObject != null)
      this.rhints.put(paramKey, paramObject);
    else if ((paramKey instanceof HyperLinkKey))
      this.rhints.put(paramKey, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    else
      this.rhints.remove(paramKey);
  }

  public Object getRenderingHint(RenderingHints.Key paramKey)
  {
    return this.rhints.get(paramKey);
  }

  public void setRenderingHints(Map paramMap)
  {
    this.rhints.clear();
    this.rhints.putAll(paramMap);
  }

  public void addRenderingHints(Map paramMap)
  {
    this.rhints.putAll(paramMap);
  }

  public RenderingHints getRenderingHints()
  {
    return this.rhints;
  }

  public void translate(int paramInt1, int paramInt2)
  {
    translate(paramInt1, paramInt2);
  }

  public void translate(double paramDouble1, double paramDouble2)
  {
    this.transform.translate(paramDouble1, paramDouble2);
  }

  public void rotate(double paramDouble)
  {
    this.transform.rotate(paramDouble);
  }

  public void rotate(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.transform.rotate(paramDouble1, paramDouble2, paramDouble3);
  }

  public void scale(double paramDouble1, double paramDouble2)
  {
    this.transform.scale(paramDouble1, paramDouble2);
    this.stroke = transformStroke(this.originalStroke);
  }

  public void shear(double paramDouble1, double paramDouble2)
  {
    this.transform.shear(paramDouble1, paramDouble2);
  }

  public void transform(AffineTransform paramAffineTransform)
  {
    this.transform.concatenate(paramAffineTransform);
    this.stroke = transformStroke(this.originalStroke);
  }

  public void setTransform(AffineTransform paramAffineTransform)
  {
    this.transform = new AffineTransform(paramAffineTransform);
    this.stroke = transformStroke(this.originalStroke);
  }

  public AffineTransform getTransform()
  {
    return new AffineTransform(this.transform);
  }

  public Paint getPaint()
  {
    if (this.realPaint != null)
      return this.realPaint;
    return this.paint;
  }

  public Composite getComposite()
  {
    return this.composite;
  }

  public void setBackground(Color paramColor)
  {
    this.background = paramColor;
  }

  public Color getBackground()
  {
    return this.background;
  }

  public Stroke getStroke()
  {
    return this.originalStroke;
  }

  public FontRenderContext getFontRenderContext()
  {
    boolean bool1 = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
    boolean bool2 = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
    return new FontRenderContext(new AffineTransform(), bool1, bool2);
  }

  public Graphics create()
  {
    PdfGraphics2D localPdfGraphics2D = new PdfGraphics2D();
    localPdfGraphics2D.rhints.putAll(this.rhints);
    localPdfGraphics2D.onlyShapes = this.onlyShapes;
    localPdfGraphics2D.transform = new AffineTransform(this.transform);
    localPdfGraphics2D.baseFonts = this.baseFonts;
    localPdfGraphics2D.fontMapper = this.fontMapper;
    localPdfGraphics2D.paint = this.paint;
    localPdfGraphics2D.fillGState = this.fillGState;
    localPdfGraphics2D.currentFillGState = this.currentFillGState;
    localPdfGraphics2D.strokeGState = this.strokeGState;
    localPdfGraphics2D.background = this.background;
    localPdfGraphics2D.mediaTracker = this.mediaTracker;
    localPdfGraphics2D.convertImagesToJPEG = this.convertImagesToJPEG;
    localPdfGraphics2D.jpegQuality = this.jpegQuality;
    localPdfGraphics2D.setFont(this.font);
    localPdfGraphics2D.cb = this.cb.getDuplicate();
    localPdfGraphics2D.cb.saveState();
    localPdfGraphics2D.width = this.width;
    localPdfGraphics2D.height = this.height;
    localPdfGraphics2D.followPath(new Area(new Rectangle2D.Float(0.0F, 0.0F, this.width, this.height)), 3);
    if (this.clip != null)
      localPdfGraphics2D.clip = new Area(this.clip);
    localPdfGraphics2D.composite = this.composite;
    localPdfGraphics2D.stroke = this.stroke;
    localPdfGraphics2D.originalStroke = this.originalStroke;
    localPdfGraphics2D.strokeOne = ((BasicStroke)localPdfGraphics2D.transformStroke(localPdfGraphics2D.strokeOne));
    localPdfGraphics2D.oldStroke = localPdfGraphics2D.strokeOne;
    localPdfGraphics2D.setStrokeDiff(localPdfGraphics2D.oldStroke, null);
    localPdfGraphics2D.cb.saveState();
    if (localPdfGraphics2D.clip != null)
      localPdfGraphics2D.followPath(localPdfGraphics2D.clip, 3);
    localPdfGraphics2D.kid = true;
    if (this.kids == null)
      this.kids = new ArrayList();
    this.kids.add(new Integer(this.cb.getInternalBuffer().size()));
    this.kids.add(localPdfGraphics2D);
    return localPdfGraphics2D;
  }

  public PdfContentByte getContent()
  {
    return this.cb;
  }

  public Color getColor()
  {
    if ((this.paint instanceof Color))
      return (Color)this.paint;
    return Color.black;
  }

  public void setColor(Color paramColor)
  {
    setPaint(paramColor);
  }

  public void setPaintMode()
  {
  }

  public void setXORMode(Color paramColor)
  {
  }

  public Font getFont()
  {
    return this.font;
  }

  public void setFont(Font paramFont)
  {
    if (paramFont == null)
      return;
    if (this.onlyShapes)
    {
      this.font = paramFont;
      return;
    }
    if (paramFont == this.font)
      return;
    this.font = paramFont;
    this.fontSize = paramFont.getSize2D();
    this.baseFont = getCachedBaseFont(paramFont);
  }

  private BaseFont getCachedBaseFont(Font paramFont)
  {
    synchronized (this.baseFonts)
    {
      BaseFont localBaseFont = (BaseFont)this.baseFonts.get(paramFont.getFontName());
      if (localBaseFont == null)
      {
        localBaseFont = this.fontMapper.awtToPdf(paramFont);
        this.baseFonts.put(paramFont.getFontName(), localBaseFont);
      }
      return localBaseFont;
    }
  }

  public FontMetrics getFontMetrics(Font paramFont)
  {
    return this.dg2.getFontMetrics(paramFont);
  }

  public Rectangle getClipBounds()
  {
    if (this.clip == null)
      return null;
    return getClip().getBounds();
  }

  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle2D.Double localDouble = new Rectangle2D.Double(paramInt1, paramInt2, paramInt3, paramInt4);
    clip(localDouble);
  }

  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle2D.Double localDouble = new Rectangle2D.Double(paramInt1, paramInt2, paramInt3, paramInt4);
    setClip(localDouble);
  }

  public void clip(Shape paramShape)
  {
    if (paramShape == null)
    {
      setClip(null);
      return;
    }
    paramShape = this.transform.createTransformedShape(paramShape);
    if (this.clip == null)
      this.clip = new Area(paramShape);
    else
      this.clip.intersect(new Area(paramShape));
    followPath(paramShape, 3);
  }

  public Shape getClip()
  {
    try
    {
      return this.transform.createInverse().createTransformedShape(this.clip);
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
    }
    return null;
  }

  public void setClip(Shape paramShape)
  {
    this.cb.restoreState();
    this.cb.saveState();
    if (paramShape != null)
      paramShape = this.transform.createTransformedShape(paramShape);
    if (paramShape == null)
    {
      this.clip = null;
    }
    else
    {
      this.clip = new Area(paramShape);
      followPath(paramShape, 3);
    }
    this.paintFill = (this.paintStroke = null);
    this.currentFillGState = (this.currentStrokeGState = 'ÿ');
    this.oldStroke = this.strokeOne;
  }

  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
  }

  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Line2D.Double localDouble = new Line2D.Double(paramInt1, paramInt2, paramInt3, paramInt4);
    draw(localDouble);
  }

  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    draw(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
  }

  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    fill(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
  }

  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Paint localPaint = this.paint;
    setPaint(this.background);
    fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    setPaint(localPaint);
  }

  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    RoundRectangle2D.Double localDouble = new RoundRectangle2D.Double(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    draw(localDouble);
  }

  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    RoundRectangle2D.Double localDouble = new RoundRectangle2D.Double(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    fill(localDouble);
  }

  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Ellipse2D.Float localFloat = new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
    draw(localFloat);
  }

  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Ellipse2D.Float localFloat = new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
    fill(localFloat);
  }

  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Arc2D.Double localDouble = new Arc2D.Double(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0);
    draw(localDouble);
  }

  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Arc2D.Double localDouble = new Arc2D.Double(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2);
    fill(localDouble);
  }

  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    PolylineShape localPolylineShape = new PolylineShape(paramArrayOfInt1, paramArrayOfInt2, paramInt);
    draw(localPolylineShape);
  }

  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    Polygon localPolygon = new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
    draw(localPolygon);
  }

  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    Polygon localPolygon = new Polygon();
    for (int i = 0; i < paramInt; i++)
      localPolygon.addPoint(paramArrayOfInt1[i], paramArrayOfInt2[i]);
    fill(localPolygon);
  }

  public boolean drawImage(java.awt.Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver);
  }

  public boolean drawImage(java.awt.Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver);
  }

  public boolean drawImage(java.awt.Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    waitForImage(paramImage);
    return drawImage(paramImage, paramInt1, paramInt2, paramImage.getWidth(paramImageObserver), paramImage.getHeight(paramImageObserver), paramColor, paramImageObserver);
  }

  public boolean drawImage(java.awt.Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    waitForImage(paramImage);
    double d1 = paramInt3 / paramImage.getWidth(paramImageObserver);
    double d2 = paramInt4 / paramImage.getHeight(paramImageObserver);
    AffineTransform localAffineTransform = AffineTransform.getTranslateInstance(paramInt1, paramInt2);
    localAffineTransform.scale(d1, d2);
    return drawImage(paramImage, null, localAffineTransform, paramColor, paramImageObserver);
  }

  public boolean drawImage(java.awt.Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null, paramImageObserver);
  }

  public boolean drawImage(java.awt.Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    waitForImage(paramImage);
    double d1 = paramInt3 - paramInt1;
    double d2 = paramInt4 - paramInt2;
    double d3 = paramInt7 - paramInt5;
    double d4 = paramInt8 - paramInt6;
    if ((d1 == 0.0D) || (d2 == 0.0D) || (d3 == 0.0D) || (d4 == 0.0D))
      return true;
    double d5 = d1 / d3;
    double d6 = d2 / d4;
    double d7 = paramInt5 * d5;
    double d8 = paramInt6 * d6;
    AffineTransform localAffineTransform = AffineTransform.getTranslateInstance(paramInt1 - d7, paramInt2 - d8);
    localAffineTransform.scale(d5, d6);
    BufferedImage localBufferedImage = new BufferedImage(paramImage.getWidth(paramImageObserver), paramImage.getHeight(paramImageObserver), 12);
    Graphics localGraphics = localBufferedImage.getGraphics();
    localGraphics.fillRect(paramInt5, paramInt6, (int)d3, (int)d4);
    drawImage(paramImage, localBufferedImage, localAffineTransform, null, paramImageObserver);
    localGraphics.dispose();
    return true;
  }

  public void dispose()
  {
    if (this.kid)
      return;
    if (!this.disposeCalled)
    {
      this.disposeCalled = true;
      this.cb.restoreState();
      this.cb.restoreState();
      this.dg2.dispose();
      this.dg2 = null;
      if (this.kids != null)
      {
        ByteBuffer localByteBuffer1 = new ByteBuffer();
        internalDispose(localByteBuffer1);
        ByteBuffer localByteBuffer2 = this.cb.getInternalBuffer();
        localByteBuffer2.reset();
        localByteBuffer2.append(localByteBuffer1);
      }
    }
  }

  private void internalDispose(ByteBuffer paramByteBuffer)
  {
    int i = 0;
    int j = 0;
    ByteBuffer localByteBuffer = this.cb.getInternalBuffer();
    if (this.kids != null)
      for (int k = 0; k < this.kids.size(); k += 2)
      {
        j = ((Integer)this.kids.get(k)).intValue();
        PdfGraphics2D localPdfGraphics2D = (PdfGraphics2D)this.kids.get(k + 1);
        localPdfGraphics2D.cb.restoreState();
        localPdfGraphics2D.cb.restoreState();
        paramByteBuffer.append(localByteBuffer.getBuffer(), i, j - i);
        localPdfGraphics2D.dg2.dispose();
        localPdfGraphics2D.dg2 = null;
        localPdfGraphics2D.internalDispose(paramByteBuffer);
        i = j;
      }
    paramByteBuffer.append(localByteBuffer.getBuffer(), i, localByteBuffer.size() - i);
  }

  private void followPath(Shape paramShape, int paramInt)
  {
    if (paramShape == null)
      return;
    if ((paramInt == 2) && (!(this.stroke instanceof BasicStroke)))
    {
      paramShape = this.stroke.createStrokedShape(paramShape);
      followPath(paramShape, 1);
      return;
    }
    if (paramInt == 2)
    {
      setStrokeDiff(this.stroke, this.oldStroke);
      this.oldStroke = this.stroke;
      setStrokePaint();
    }
    else if (paramInt == 1)
    {
      setFillPaint();
    }
    int i = 0;
    PathIterator localPathIterator;
    if (paramInt == 3)
      localPathIterator = paramShape.getPathIterator(IDENTITY);
    else
      localPathIterator = paramShape.getPathIterator(this.transform);
    float[] arrayOfFloat = new float[6];
    while (!localPathIterator.isDone())
    {
      i++;
      int j = localPathIterator.currentSegment(arrayOfFloat);
      normalizeY(arrayOfFloat);
      switch (j)
      {
      case 4:
        this.cb.closePath();
        break;
      case 3:
        this.cb.curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
        break;
      case 1:
        this.cb.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 0:
        this.cb.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 2:
        this.cb.curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
      }
      localPathIterator.next();
    }
    switch (paramInt)
    {
    case 1:
      if (i <= 0)
        break;
      if (localPathIterator.getWindingRule() == 0)
        this.cb.eoFill();
      else
        this.cb.fill();
      break;
    case 2:
      if (i <= 0)
        break;
      this.cb.stroke();
      break;
    default:
      if (i == 0)
        this.cb.rectangle(0.0F, 0.0F, 0.0F, 0.0F);
      if (localPathIterator.getWindingRule() == 0)
        this.cb.eoClip();
      else
        this.cb.clip();
      this.cb.newPath();
    }
  }

  private float normalizeY(float paramFloat)
  {
    return this.height - paramFloat;
  }

  private void normalizeY(float[] paramArrayOfFloat)
  {
    paramArrayOfFloat[1] = normalizeY(paramArrayOfFloat[1]);
    paramArrayOfFloat[3] = normalizeY(paramArrayOfFloat[3]);
    paramArrayOfFloat[5] = normalizeY(paramArrayOfFloat[5]);
  }

  private AffineTransform normalizeMatrix()
  {
    double[] arrayOfDouble = new double[6];
    AffineTransform localAffineTransform = AffineTransform.getTranslateInstance(0.0D, 0.0D);
    localAffineTransform.getMatrix(arrayOfDouble);
    arrayOfDouble[3] = -1.0D;
    arrayOfDouble[5] = this.height;
    localAffineTransform = new AffineTransform(arrayOfDouble);
    localAffineTransform.concatenate(this.transform);
    return localAffineTransform;
  }

  private boolean drawImage(java.awt.Image paramImage1, java.awt.Image paramImage2, AffineTransform paramAffineTransform, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramAffineTransform == null)
      paramAffineTransform = new AffineTransform();
    else
      paramAffineTransform = new AffineTransform(paramAffineTransform);
    paramAffineTransform.translate(0.0D, paramImage1.getHeight(paramImageObserver));
    paramAffineTransform.scale(paramImage1.getWidth(paramImageObserver), paramImage1.getHeight(paramImageObserver));
    AffineTransform localAffineTransform1 = normalizeMatrix();
    AffineTransform localAffineTransform2 = AffineTransform.getScaleInstance(1.0D, -1.0D);
    localAffineTransform1.concatenate(paramAffineTransform);
    localAffineTransform1.concatenate(localAffineTransform2);
    double[] arrayOfDouble = new double[6];
    localAffineTransform1.getMatrix(arrayOfDouble);
    Object localObject1;
    if (this.currentFillGState != 255)
    {
      localObject1 = this.fillGState['ÿ'];
      if (localObject1 == null)
      {
        localObject1 = new PdfGState();
        ((PdfGState)localObject1).setFillOpacity(1.0F);
        this.fillGState['ÿ'] = localObject1;
      }
      this.cb.setGState((PdfGState)localObject1);
    }
    try
    {
      localObject1 = null;
      Object localObject3;
      if (!this.convertImagesToJPEG)
      {
        localObject1 = com.lowagie.text.Image.getInstance(paramImage1, paramColor);
      }
      else
      {
        localObject2 = new BufferedImage(paramImage1.getWidth(null), paramImage1.getHeight(null), 1);
        localObject3 = ((BufferedImage)localObject2).createGraphics();
        ((Graphics2D)localObject3).drawImage(paramImage1, 0, 0, paramImage1.getWidth(null), paramImage1.getHeight(null), null);
        ((Graphics2D)localObject3).dispose();
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        JPEGImageWriteParam localJPEGImageWriteParam = new JPEGImageWriteParam(Locale.getDefault());
        localJPEGImageWriteParam.setCompressionMode(2);
        localJPEGImageWriteParam.setCompressionQuality(this.jpegQuality);
        ImageWriter localImageWriter = (ImageWriter)ImageIO.getImageWritersByFormatName("jpg").next();
        ImageOutputStream localImageOutputStream = ImageIO.createImageOutputStream(localByteArrayOutputStream);
        localImageWriter.setOutput(localImageOutputStream);
        localImageWriter.write(null, new IIOImage((RenderedImage)localObject2, null, null), localJPEGImageWriteParam);
        localImageWriter.dispose();
        localImageOutputStream.close();
        ((BufferedImage)localObject2).flush();
        localObject2 = null;
        localObject1 = com.lowagie.text.Image.getInstance(localByteArrayOutputStream.toByteArray());
      }
      if (paramImage2 != null)
      {
        localObject2 = com.lowagie.text.Image.getInstance(paramImage2, null, true);
        ((com.lowagie.text.Image)localObject2).makeMask();
        ((com.lowagie.text.Image)localObject2).setInverted(true);
        ((com.lowagie.text.Image)localObject1).setImageMask((com.lowagie.text.Image)localObject2);
      }
      this.cb.addImage((com.lowagie.text.Image)localObject1, (float)arrayOfDouble[0], (float)arrayOfDouble[1], (float)arrayOfDouble[2], (float)arrayOfDouble[3], (float)arrayOfDouble[4], (float)arrayOfDouble[5]);
      Object localObject2 = getRenderingHint(HyperLinkKey.KEY_INSTANCE);
      if ((localObject2 != null) && (!localObject2.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)))
      {
        localObject3 = new PdfAction(localObject2.toString());
        this.cb.setAction((PdfAction)localObject3, (float)arrayOfDouble[4], (float)arrayOfDouble[5], (float)(arrayOfDouble[0] + arrayOfDouble[4]), (float)(arrayOfDouble[3] + arrayOfDouble[5]));
      }
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException();
    }
    if (this.currentFillGState != 255)
    {
      PdfGState localPdfGState = this.fillGState[this.currentFillGState];
      this.cb.setGState(localPdfGState);
    }
    return true;
  }

  private boolean checkNewPaint(Paint paramPaint)
  {
    if (this.paint == paramPaint)
      return false;
    return (!(this.paint instanceof Color)) || (!this.paint.equals(paramPaint));
  }

  private void setFillPaint()
  {
    if (checkNewPaint(this.paintFill))
    {
      this.paintFill = this.paint;
      setPaint(false, 0.0D, 0.0D, true);
    }
  }

  private void setStrokePaint()
  {
    if (checkNewPaint(this.paintStroke))
    {
      this.paintStroke = this.paint;
      setPaint(false, 0.0D, 0.0D, false);
    }
  }

  private void setPaint(boolean paramBoolean1, double paramDouble1, double paramDouble2, boolean paramBoolean2)
  {
    Object localObject1;
    Object localObject3;
    if ((this.paint instanceof Color))
    {
      localObject1 = (Color)this.paint;
      int i = ((Color)localObject1).getAlpha();
      if (paramBoolean2)
      {
        if (i != this.currentFillGState)
        {
          this.currentFillGState = i;
          localObject3 = this.fillGState[i];
          if (localObject3 == null)
          {
            localObject3 = new PdfGState();
            ((PdfGState)localObject3).setFillOpacity(i / 255.0F);
            this.fillGState[i] = localObject3;
          }
          this.cb.setGState((PdfGState)localObject3);
        }
        this.cb.setColorFill((Color)localObject1);
      }
      else
      {
        if (i != this.currentStrokeGState)
        {
          this.currentStrokeGState = i;
          localObject3 = this.strokeGState[i];
          if (localObject3 == null)
          {
            localObject3 = new PdfGState();
            ((PdfGState)localObject3).setStrokeOpacity(i / 255.0F);
            this.strokeGState[i] = localObject3;
          }
          this.cb.setGState((PdfGState)localObject3);
        }
        this.cb.setColorStroke((Color)localObject1);
      }
    }
    else
    {
      Object localObject2;
      Object localObject4;
      Object localObject5;
      Object localObject6;
      Object localObject7;
      if ((this.paint instanceof GradientPaint))
      {
        localObject1 = (GradientPaint)this.paint;
        localObject2 = ((GradientPaint)localObject1).getPoint1();
        this.transform.transform((Point2D)localObject2, (Point2D)localObject2);
        localObject3 = ((GradientPaint)localObject1).getPoint2();
        this.transform.transform((Point2D)localObject3, (Point2D)localObject3);
        localObject4 = ((GradientPaint)localObject1).getColor1();
        localObject5 = ((GradientPaint)localObject1).getColor2();
        localObject6 = PdfShading.simpleAxial(this.cb.getPdfWriter(), (float)((Point2D)localObject2).getX(), normalizeY((float)((Point2D)localObject2).getY()), (float)((Point2D)localObject3).getX(), normalizeY((float)((Point2D)localObject3).getY()), (Color)localObject4, (Color)localObject5);
        localObject7 = new PdfShadingPattern((PdfShading)localObject6);
        if (paramBoolean2)
          this.cb.setShadingFill((PdfShadingPattern)localObject7);
        else
          this.cb.setShadingStroke((PdfShadingPattern)localObject7);
      }
      else if ((this.paint instanceof TexturePaint))
      {
        try
        {
          localObject1 = (TexturePaint)this.paint;
          localObject2 = ((TexturePaint)localObject1).getImage();
          localObject3 = ((TexturePaint)localObject1).getAnchorRect();
          localObject4 = com.lowagie.text.Image.getInstance((java.awt.Image)localObject2, null);
          localObject5 = this.cb.createPattern(((com.lowagie.text.Image)localObject4).getWidth(), ((com.lowagie.text.Image)localObject4).getHeight());
          localObject6 = normalizeMatrix();
          ((AffineTransform)localObject6).translate(((Rectangle2D)localObject3).getX(), ((Rectangle2D)localObject3).getY());
          ((AffineTransform)localObject6).scale(((Rectangle2D)localObject3).getWidth() / ((com.lowagie.text.Image)localObject4).getWidth(), -((Rectangle2D)localObject3).getHeight() / ((com.lowagie.text.Image)localObject4).getHeight());
          localObject7 = new double[6];
          ((AffineTransform)localObject6).getMatrix(localObject7);
          ((PdfPatternPainter)localObject5).setPatternMatrix((float)localObject7[0], (float)localObject7[1], (float)localObject7[2], (float)localObject7[3], (float)localObject7[4], (float)localObject7[5]);
          ((com.lowagie.text.Image)localObject4).setAbsolutePosition(0.0F, 0.0F);
          ((PdfPatternPainter)localObject5).addImage((com.lowagie.text.Image)localObject4);
          if (paramBoolean2)
            this.cb.setPatternFill((PdfPatternPainter)localObject5);
          else
            this.cb.setPatternStroke((PdfPatternPainter)localObject5);
        }
        catch (Exception localException1)
        {
          if (paramBoolean2)
            this.cb.setColorFill(Color.gray);
          else
            this.cb.setColorStroke(Color.gray);
        }
      }
      else
      {
        try
        {
          BufferedImage localBufferedImage = null;
          int j = 6;
          if (this.paint.getTransparency() == 1)
            j = 5;
          localBufferedImage = new BufferedImage((int)this.width, (int)this.height, j);
          localObject3 = (Graphics2D)localBufferedImage.getGraphics();
          ((Graphics2D)localObject3).transform(this.transform);
          localObject4 = this.transform.createInverse();
          localObject5 = new Rectangle2D.Double(0.0D, 0.0D, localBufferedImage.getWidth(), localBufferedImage.getHeight());
          localObject5 = ((AffineTransform)localObject4).createTransformedShape((Shape)localObject5);
          ((Graphics2D)localObject3).setPaint(this.paint);
          ((Graphics2D)localObject3).fill((Shape)localObject5);
          if (paramBoolean1)
          {
            localObject6 = new AffineTransform();
            ((AffineTransform)localObject6).scale(1.0D, -1.0D);
            ((AffineTransform)localObject6).translate(-paramDouble1, -paramDouble2);
            ((Graphics2D)localObject3).drawImage(localBufferedImage, (AffineTransform)localObject6, null);
          }
          ((Graphics2D)localObject3).dispose();
          localObject3 = null;
          localObject6 = com.lowagie.text.Image.getInstance(localBufferedImage, null);
          localObject7 = this.cb.createPattern(this.width, this.height);
          ((com.lowagie.text.Image)localObject6).setAbsolutePosition(0.0F, 0.0F);
          ((PdfPatternPainter)localObject7).addImage((com.lowagie.text.Image)localObject6);
          if (paramBoolean2)
            this.cb.setPatternFill((PdfPatternPainter)localObject7);
          else
            this.cb.setPatternStroke((PdfPatternPainter)localObject7);
        }
        catch (Exception localException2)
        {
          if (paramBoolean2)
            this.cb.setColorFill(Color.gray);
          else
            this.cb.setColorStroke(Color.gray);
        }
      }
    }
  }

  private synchronized void waitForImage(java.awt.Image paramImage)
  {
    if (this.mediaTracker == null)
      this.mediaTracker = new MediaTracker(new FakeComponent(null));
    this.mediaTracker.addImage(paramImage, 0);
    try
    {
      this.mediaTracker.waitForID(0);
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    this.mediaTracker.removeImage(paramImage);
  }

  public static class HyperLinkKey extends RenderingHints.Key
  {
    public static final HyperLinkKey KEY_INSTANCE = new HyperLinkKey(9999);
    public static final Object VALUE_HYPERLINKKEY_OFF = "0";

    protected HyperLinkKey(int paramInt)
    {
      super();
    }

    public boolean isCompatibleValue(Object paramObject)
    {
      return true;
    }

    public String toString()
    {
      return "HyperLinkKey";
    }
  }

  private static class FakeComponent extends Component
  {
    private static final long serialVersionUID = 6450197945596086638L;

    private FakeComponent()
    {
    }

    FakeComponent(PdfGraphics2D.1 param1)
    {
      this();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfGraphics2D
 * JD-Core Version:    0.6.0
 */