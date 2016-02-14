package com.lowagie.text.pdf;

import com.lowagie.text.Annotation;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.Rectangle;
import com.lowagie.text.exceptions.IllegalPdfSyntaxException;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PdfContentByte
{
  public static final int ALIGN_CENTER = 1;
  public static final int ALIGN_LEFT = 0;
  public static final int ALIGN_RIGHT = 2;
  public static final int LINE_CAP_BUTT = 0;
  public static final int LINE_CAP_ROUND = 1;
  public static final int LINE_CAP_PROJECTING_SQUARE = 2;
  public static final int LINE_JOIN_MITER = 0;
  public static final int LINE_JOIN_ROUND = 1;
  public static final int LINE_JOIN_BEVEL = 2;
  public static final int TEXT_RENDER_MODE_FILL = 0;
  public static final int TEXT_RENDER_MODE_STROKE = 1;
  public static final int TEXT_RENDER_MODE_FILL_STROKE = 2;
  public static final int TEXT_RENDER_MODE_INVISIBLE = 3;
  public static final int TEXT_RENDER_MODE_FILL_CLIP = 4;
  public static final int TEXT_RENDER_MODE_STROKE_CLIP = 5;
  public static final int TEXT_RENDER_MODE_FILL_STROKE_CLIP = 6;
  public static final int TEXT_RENDER_MODE_CLIP = 7;
  private static final float[] unitRect = { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F };
  protected ByteBuffer content = new ByteBuffer();
  protected PdfWriter writer;
  protected PdfDocument pdf;
  protected GraphicState state = new GraphicState();
  protected ArrayList stateList = new ArrayList();
  protected ArrayList layerDepth;
  protected int separator = 10;
  private int mcDepth = 0;
  private boolean inText = false;
  private static HashMap abrev = new HashMap();

  public PdfContentByte(PdfWriter paramPdfWriter)
  {
    if (paramPdfWriter != null)
    {
      this.writer = paramPdfWriter;
      this.pdf = this.writer.getPdfDocument();
    }
  }

  public String toString()
  {
    return this.content.toString();
  }

  public ByteBuffer getInternalBuffer()
  {
    return this.content;
  }

  public byte[] toPdf(PdfWriter paramPdfWriter)
  {
    sanityCheck();
    return this.content.toByteArray();
  }

  public void add(PdfContentByte paramPdfContentByte)
  {
    if ((paramPdfContentByte.writer != null) && (this.writer != paramPdfContentByte.writer))
      throw new RuntimeException("Inconsistent writers. Are you mixing two documents?");
    this.content.append(paramPdfContentByte.content);
  }

  public float getXTLM()
  {
    return this.state.xTLM;
  }

  public float getYTLM()
  {
    return this.state.yTLM;
  }

  public float getLeading()
  {
    return this.state.leading;
  }

  public float getCharacterSpacing()
  {
    return this.state.charSpace;
  }

  public float getWordSpacing()
  {
    return this.state.wordSpace;
  }

  public float getHorizontalScaling()
  {
    return this.state.scale;
  }

  public void setFlatness(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 100.0F))
      this.content.append(paramFloat).append(" i").append_i(this.separator);
  }

  public void setLineCap(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= 2))
      this.content.append(paramInt).append(" J").append_i(this.separator);
  }

  public void setLineDash(float paramFloat)
  {
    this.content.append("[] ").append(paramFloat).append(" d").append_i(this.separator);
  }

  public void setLineDash(float paramFloat1, float paramFloat2)
  {
    this.content.append("[").append(paramFloat1).append("] ").append(paramFloat2).append(" d").append_i(this.separator);
  }

  public void setLineDash(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.content.append("[").append(paramFloat1).append(' ').append(paramFloat2).append("] ").append(paramFloat3).append(" d").append_i(this.separator);
  }

  public final void setLineDash(float[] paramArrayOfFloat, float paramFloat)
  {
    this.content.append("[");
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      this.content.append(paramArrayOfFloat[i]);
      if (i >= paramArrayOfFloat.length - 1)
        continue;
      this.content.append(' ');
    }
    this.content.append("] ").append(paramFloat).append(" d").append_i(this.separator);
  }

  public void setLineJoin(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= 2))
      this.content.append(paramInt).append(" j").append_i(this.separator);
  }

  public void setLineWidth(float paramFloat)
  {
    this.content.append(paramFloat).append(" w").append_i(this.separator);
  }

  public void setMiterLimit(float paramFloat)
  {
    if (paramFloat > 1.0F)
      this.content.append(paramFloat).append(" M").append_i(this.separator);
  }

  public void clip()
  {
    this.content.append("W").append_i(this.separator);
  }

  public void eoClip()
  {
    this.content.append("W*").append_i(this.separator);
  }

  public void setGrayFill(float paramFloat)
  {
    this.content.append(paramFloat).append(" g").append_i(this.separator);
  }

  public void resetGrayFill()
  {
    this.content.append("0 g").append_i(this.separator);
  }

  public void setGrayStroke(float paramFloat)
  {
    this.content.append(paramFloat).append(" G").append_i(this.separator);
  }

  public void resetGrayStroke()
  {
    this.content.append("0 G").append_i(this.separator);
  }

  private void HelperRGB(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    PdfXConformanceImp.checkPDFXConformance(this.writer, 3, null);
    if (paramFloat1 < 0.0F)
      paramFloat1 = 0.0F;
    else if (paramFloat1 > 1.0F)
      paramFloat1 = 1.0F;
    if (paramFloat2 < 0.0F)
      paramFloat2 = 0.0F;
    else if (paramFloat2 > 1.0F)
      paramFloat2 = 1.0F;
    if (paramFloat3 < 0.0F)
      paramFloat3 = 0.0F;
    else if (paramFloat3 > 1.0F)
      paramFloat3 = 1.0F;
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3);
  }

  public void setRGBColorFillF(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    HelperRGB(paramFloat1, paramFloat2, paramFloat3);
    this.content.append(" rg").append_i(this.separator);
  }

  public void resetRGBColorFill()
  {
    this.content.append("0 g").append_i(this.separator);
  }

  public void setRGBColorStrokeF(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    HelperRGB(paramFloat1, paramFloat2, paramFloat3);
    this.content.append(" RG").append_i(this.separator);
  }

  public void resetRGBColorStroke()
  {
    this.content.append("0 G").append_i(this.separator);
  }

  private void HelperCMYK(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 < 0.0F)
      paramFloat1 = 0.0F;
    else if (paramFloat1 > 1.0F)
      paramFloat1 = 1.0F;
    if (paramFloat2 < 0.0F)
      paramFloat2 = 0.0F;
    else if (paramFloat2 > 1.0F)
      paramFloat2 = 1.0F;
    if (paramFloat3 < 0.0F)
      paramFloat3 = 0.0F;
    else if (paramFloat3 > 1.0F)
      paramFloat3 = 1.0F;
    if (paramFloat4 < 0.0F)
      paramFloat4 = 0.0F;
    else if (paramFloat4 > 1.0F)
      paramFloat4 = 1.0F;
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3).append(' ').append(paramFloat4);
  }

  public void setCMYKColorFillF(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    HelperCMYK(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.content.append(" k").append_i(this.separator);
  }

  public void resetCMYKColorFill()
  {
    this.content.append("0 0 0 1 k").append_i(this.separator);
  }

  public void setCMYKColorStrokeF(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    HelperCMYK(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    this.content.append(" K").append_i(this.separator);
  }

  public void resetCMYKColorStroke()
  {
    this.content.append("0 0 0 1 K").append_i(this.separator);
  }

  public void moveTo(float paramFloat1, float paramFloat2)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(" m").append_i(this.separator);
  }

  public void lineTo(float paramFloat1, float paramFloat2)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(" l").append_i(this.separator);
  }

  public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3).append(' ').append(paramFloat4).append(' ').append(paramFloat5).append(' ').append(paramFloat6).append(" c").append_i(this.separator);
  }

  public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3).append(' ').append(paramFloat4).append(" v").append_i(this.separator);
  }

  public void curveFromTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3).append(' ').append(paramFloat4).append(" y").append_i(this.separator);
  }

  public void circle(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f = 0.5523F;
    moveTo(paramFloat1 + paramFloat3, paramFloat2);
    curveTo(paramFloat1 + paramFloat3, paramFloat2 + paramFloat3 * f, paramFloat1 + paramFloat3 * f, paramFloat2 + paramFloat3, paramFloat1, paramFloat2 + paramFloat3);
    curveTo(paramFloat1 - paramFloat3 * f, paramFloat2 + paramFloat3, paramFloat1 - paramFloat3, paramFloat2 + paramFloat3 * f, paramFloat1 - paramFloat3, paramFloat2);
    curveTo(paramFloat1 - paramFloat3, paramFloat2 - paramFloat3 * f, paramFloat1 - paramFloat3 * f, paramFloat2 - paramFloat3, paramFloat1, paramFloat2 - paramFloat3);
    curveTo(paramFloat1 + paramFloat3 * f, paramFloat2 - paramFloat3, paramFloat1 + paramFloat3, paramFloat2 - paramFloat3 * f, paramFloat1 + paramFloat3, paramFloat2);
  }

  public void rectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3).append(' ').append(paramFloat4).append(" re").append_i(this.separator);
  }

  private boolean compareColors(Color paramColor1, Color paramColor2)
  {
    if ((paramColor1 == null) && (paramColor2 == null))
      return true;
    if ((paramColor1 == null) || (paramColor2 == null))
      return false;
    if ((paramColor1 instanceof ExtendedColor))
      return paramColor1.equals(paramColor2);
    return paramColor2.equals(paramColor1);
  }

  public void variableRectangle(Rectangle paramRectangle)
  {
    float f1 = paramRectangle.getTop();
    float f2 = paramRectangle.getBottom();
    float f3 = paramRectangle.getRight();
    float f4 = paramRectangle.getLeft();
    float f5 = paramRectangle.getBorderWidthTop();
    float f6 = paramRectangle.getBorderWidthBottom();
    float f7 = paramRectangle.getBorderWidthRight();
    float f8 = paramRectangle.getBorderWidthLeft();
    Color localColor1 = paramRectangle.getBorderColorTop();
    Color localColor2 = paramRectangle.getBorderColorBottom();
    Color localColor3 = paramRectangle.getBorderColorRight();
    Color localColor4 = paramRectangle.getBorderColorLeft();
    saveState();
    setLineCap(0);
    setLineJoin(0);
    float f9 = 0.0F;
    int i = 0;
    Color localColor5 = null;
    int j = 0;
    Color localColor6 = null;
    if (f5 > 0.0F)
    {
      setLineWidth(f9 = f5);
      i = 1;
      if (localColor1 == null)
        resetRGBColorStroke();
      else
        setColorStroke(localColor1);
      localColor5 = localColor1;
      moveTo(f4, f1 - f5 / 2.0F);
      lineTo(f3, f1 - f5 / 2.0F);
      stroke();
    }
    if (f6 > 0.0F)
    {
      if (f6 != f9)
        setLineWidth(f9 = f6);
      if ((i == 0) || (!compareColors(localColor5, localColor2)))
      {
        i = 1;
        if (localColor2 == null)
          resetRGBColorStroke();
        else
          setColorStroke(localColor2);
        localColor5 = localColor2;
      }
      moveTo(f3, f2 + f6 / 2.0F);
      lineTo(f4, f2 + f6 / 2.0F);
      stroke();
    }
    boolean bool1;
    boolean bool2;
    if (f7 > 0.0F)
    {
      if (f7 != f9)
        setLineWidth(f9 = f7);
      if ((i == 0) || (!compareColors(localColor5, localColor3)))
      {
        i = 1;
        if (localColor3 == null)
          resetRGBColorStroke();
        else
          setColorStroke(localColor3);
        localColor5 = localColor3;
      }
      bool1 = compareColors(localColor1, localColor3);
      bool2 = compareColors(localColor2, localColor3);
      moveTo(f3 - f7 / 2.0F, bool1 ? f1 : f1 - f5);
      lineTo(f3 - f7 / 2.0F, bool2 ? f2 : f2 + f6);
      stroke();
      if ((!bool1) || (!bool2))
      {
        j = 1;
        if (localColor3 == null)
          resetRGBColorFill();
        else
          setColorFill(localColor3);
        localColor6 = localColor3;
        if (!bool1)
        {
          moveTo(f3, f1);
          lineTo(f3, f1 - f5);
          lineTo(f3 - f7, f1 - f5);
          fill();
        }
        if (!bool2)
        {
          moveTo(f3, f2);
          lineTo(f3, f2 + f6);
          lineTo(f3 - f7, f2 + f6);
          fill();
        }
      }
    }
    if (f8 > 0.0F)
    {
      if (f8 != f9)
        setLineWidth(f8);
      if ((i == 0) || (!compareColors(localColor5, localColor4)))
        if (localColor4 == null)
          resetRGBColorStroke();
        else
          setColorStroke(localColor4);
      bool1 = compareColors(localColor1, localColor4);
      bool2 = compareColors(localColor2, localColor4);
      moveTo(f4 + f8 / 2.0F, bool1 ? f1 : f1 - f5);
      lineTo(f4 + f8 / 2.0F, bool2 ? f2 : f2 + f6);
      stroke();
      if ((!bool1) || (!bool2))
      {
        if ((j == 0) || (!compareColors(localColor6, localColor4)))
          if (localColor4 == null)
            resetRGBColorFill();
          else
            setColorFill(localColor4);
        if (!bool1)
        {
          moveTo(f4, f1);
          lineTo(f4, f1 - f5);
          lineTo(f4 + f8, f1 - f5);
          fill();
        }
        if (!bool2)
        {
          moveTo(f4, f2);
          lineTo(f4, f2 + f6);
          lineTo(f4 + f8, f2 + f6);
          fill();
        }
      }
    }
    restoreState();
  }

  public void rectangle(Rectangle paramRectangle)
  {
    float f1 = paramRectangle.getLeft();
    float f2 = paramRectangle.getBottom();
    float f3 = paramRectangle.getRight();
    float f4 = paramRectangle.getTop();
    Color localColor1 = paramRectangle.getBackgroundColor();
    if (localColor1 != null)
    {
      setColorFill(localColor1);
      rectangle(f1, f2, f3 - f1, f4 - f2);
      fill();
      resetRGBColorFill();
    }
    if (!paramRectangle.hasBorders())
      return;
    if (paramRectangle.isUseVariableBorders())
    {
      variableRectangle(paramRectangle);
    }
    else
    {
      if (paramRectangle.getBorderWidth() != -1.0F)
        setLineWidth(paramRectangle.getBorderWidth());
      Color localColor2 = paramRectangle.getBorderColor();
      if (localColor2 != null)
        setColorStroke(localColor2);
      if (paramRectangle.hasBorder(15))
      {
        rectangle(f1, f2, f3 - f1, f4 - f2);
      }
      else
      {
        if (paramRectangle.hasBorder(8))
        {
          moveTo(f3, f2);
          lineTo(f3, f4);
        }
        if (paramRectangle.hasBorder(4))
        {
          moveTo(f1, f2);
          lineTo(f1, f4);
        }
        if (paramRectangle.hasBorder(2))
        {
          moveTo(f1, f2);
          lineTo(f3, f2);
        }
        if (paramRectangle.hasBorder(1))
        {
          moveTo(f1, f4);
          lineTo(f3, f4);
        }
      }
      stroke();
      if (localColor2 != null)
        resetRGBColorStroke();
    }
  }

  public void closePath()
  {
    this.content.append("h").append_i(this.separator);
  }

  public void newPath()
  {
    this.content.append("n").append_i(this.separator);
  }

  public void stroke()
  {
    this.content.append("S").append_i(this.separator);
  }

  public void closePathStroke()
  {
    this.content.append("s").append_i(this.separator);
  }

  public void fill()
  {
    this.content.append("f").append_i(this.separator);
  }

  public void eoFill()
  {
    this.content.append("f*").append_i(this.separator);
  }

  public void fillStroke()
  {
    this.content.append("B").append_i(this.separator);
  }

  public void closePathFillStroke()
  {
    this.content.append("b").append_i(this.separator);
  }

  public void eoFillStroke()
  {
    this.content.append("B*").append_i(this.separator);
  }

  public void closePathEoFillStroke()
  {
    this.content.append("b*").append_i(this.separator);
  }

  public void addImage(Image paramImage)
    throws DocumentException
  {
    addImage(paramImage, false);
  }

  public void addImage(Image paramImage, boolean paramBoolean)
    throws DocumentException
  {
    if (!paramImage.hasAbsoluteY())
      throw new DocumentException("The image must have absolute positioning.");
    float[] arrayOfFloat = paramImage.matrix();
    arrayOfFloat[4] = (paramImage.getAbsoluteX() - arrayOfFloat[4]);
    arrayOfFloat[5] = (paramImage.getAbsoluteY() - arrayOfFloat[5]);
    addImage(paramImage, arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5], paramBoolean);
  }

  public void addImage(Image paramImage, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    throws DocumentException
  {
    addImage(paramImage, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, false);
  }

  public void addImage(Image paramImage, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, boolean paramBoolean)
    throws DocumentException
  {
    try
    {
      if (paramImage.getLayer() != null)
        beginLayer(paramImage.getLayer());
      Object localObject1;
      if (paramImage.isImgTemplate())
      {
        this.writer.addDirectImageSimple(paramImage);
        localObject1 = paramImage.getTemplateData();
        float f2 = ((PdfTemplate)localObject1).getWidth();
        float f4 = ((PdfTemplate)localObject1).getHeight();
        addTemplate((PdfTemplate)localObject1, paramFloat1 / f2, paramFloat2 / f2, paramFloat3 / f4, paramFloat4 / f4, paramFloat5, paramFloat6);
      }
      else
      {
        this.content.append("q ");
        this.content.append(paramFloat1).append(' ');
        this.content.append(paramFloat2).append(' ');
        this.content.append(paramFloat3).append(' ');
        this.content.append(paramFloat4).append(' ');
        this.content.append(paramFloat5).append(' ');
        this.content.append(paramFloat6).append(" cm");
        Object localObject2;
        Object localObject3;
        if (paramBoolean)
        {
          this.content.append("\nBI\n");
          localObject1 = new PdfImage(paramImage, "", null);
          if ((paramImage instanceof ImgJBIG2))
          {
            localObject2 = ((ImgJBIG2)paramImage).getGlobalBytes();
            if (localObject2 != null)
            {
              localObject3 = new PdfDictionary();
              ((PdfDictionary)localObject3).put(PdfName.JBIG2GLOBALS, this.writer.getReferenceJBIG2Globals(localObject2));
              ((PdfImage)localObject1).put(PdfName.DECODEPARMS, (PdfObject)localObject3);
            }
          }
          localObject2 = ((PdfImage)localObject1).getKeys().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (PdfName)((Iterator)localObject2).next();
            Object localObject4 = ((PdfImage)localObject1).get((PdfName)localObject3);
            String str = (String)abrev.get(localObject3);
            if (str == null)
              continue;
            this.content.append(str);
            int j = 1;
            Object localObject5;
            if ((((PdfName)localObject3).equals(PdfName.COLORSPACE)) && (((PdfObject)localObject4).isArray()))
            {
              localObject5 = (PdfArray)localObject4;
              if ((((PdfArray)localObject5).size() == 4) && (PdfName.INDEXED.equals(((PdfArray)localObject5).getAsName(0))) && (((PdfArray)localObject5).getPdfObject(1).isName()) && (((PdfArray)localObject5).getPdfObject(2).isNumber()) && (((PdfArray)localObject5).getPdfObject(3).isString()))
                j = 0;
            }
            if ((j != 0) && (((PdfName)localObject3).equals(PdfName.COLORSPACE)) && (!((PdfObject)localObject4).isName()))
            {
              localObject5 = this.writer.getColorspaceName();
              PageResources localPageResources = getPageResources();
              localPageResources.addColor((PdfName)localObject5, this.writer.addToBody((PdfObject)localObject4).getIndirectReference());
              localObject4 = localObject5;
            }
            ((PdfObject)localObject4).toPdf(null, this.content);
            this.content.append('\n');
          }
          this.content.append("ID\n");
          ((PdfImage)localObject1).writeContent(this.content);
          this.content.append("\nEI\nQ").append_i(this.separator);
        }
        else
        {
          localObject2 = getPageResources();
          localObject3 = paramImage.getImageMask();
          if (localObject3 != null)
          {
            localObject1 = this.writer.addDirectImageSimple((Image)localObject3);
            ((PageResources)localObject2).addXObject((PdfName)localObject1, this.writer.getImageReference((PdfName)localObject1));
          }
          localObject1 = this.writer.addDirectImageSimple(paramImage);
          localObject1 = ((PageResources)localObject2).addXObject((PdfName)localObject1, this.writer.getImageReference((PdfName)localObject1));
          this.content.append(' ').append(((PdfName)localObject1).getBytes()).append(" Do Q").append_i(this.separator);
        }
      }
      if (paramImage.hasBorders())
      {
        saveState();
        float f1 = paramImage.getWidth();
        float f3 = paramImage.getHeight();
        concatCTM(paramFloat1 / f1, paramFloat2 / f1, paramFloat3 / f3, paramFloat4 / f3, paramFloat5, paramFloat6);
        rectangle(paramImage);
        restoreState();
      }
      if (paramImage.getLayer() != null)
        endLayer();
      Annotation localAnnotation = paramImage.getAnnotation();
      if (localAnnotation == null)
        return;
      float[] arrayOfFloat = new float[unitRect.length];
      for (int i = 0; i < unitRect.length; i += 2)
      {
        arrayOfFloat[i] = (paramFloat1 * unitRect[i] + paramFloat3 * unitRect[(i + 1)] + paramFloat5);
        arrayOfFloat[(i + 1)] = (paramFloat2 * unitRect[i] + paramFloat4 * unitRect[(i + 1)] + paramFloat6);
      }
      float f5 = arrayOfFloat[0];
      float f6 = arrayOfFloat[1];
      float f7 = f5;
      float f8 = f6;
      for (int k = 2; k < arrayOfFloat.length; k += 2)
      {
        f5 = Math.min(f5, arrayOfFloat[k]);
        f6 = Math.min(f6, arrayOfFloat[(k + 1)]);
        f7 = Math.max(f7, arrayOfFloat[k]);
        f8 = Math.max(f8, arrayOfFloat[(k + 1)]);
      }
      localAnnotation = new Annotation(localAnnotation);
      localAnnotation.setDimensions(f5, f6, f7, f8);
      PdfAnnotation localPdfAnnotation = PdfAnnotationsImp.convertAnnotation(this.writer, localAnnotation, new Rectangle(f5, f6, f7, f8));
      if (localPdfAnnotation == null)
        return;
      addAnnotation(localPdfAnnotation);
    }
    catch (Exception localException)
    {
      throw new DocumentException(localException);
    }
  }

  public void reset()
  {
    reset(true);
  }

  public void reset(boolean paramBoolean)
  {
    this.content.reset();
    if (paramBoolean)
      sanityCheck();
    this.state = new GraphicState();
  }

  public void beginText()
  {
    if (this.inText)
      throw new IllegalPdfSyntaxException("Unbalanced begin/end text operators.");
    this.inText = true;
    this.state.xTLM = 0.0F;
    this.state.yTLM = 0.0F;
    this.content.append("BT").append_i(this.separator);
  }

  public void endText()
  {
    if (!this.inText)
      throw new IllegalPdfSyntaxException("Unbalanced begin/end text operators.");
    this.inText = false;
    this.content.append("ET").append_i(this.separator);
  }

  public void saveState()
  {
    this.content.append("q").append_i(this.separator);
    this.stateList.add(new GraphicState(this.state));
  }

  public void restoreState()
  {
    this.content.append("Q").append_i(this.separator);
    int i = this.stateList.size() - 1;
    if (i < 0)
      throw new IllegalPdfSyntaxException("Unbalanced save/restore state operators.");
    this.state = ((GraphicState)this.stateList.get(i));
    this.stateList.remove(i);
  }

  public void setCharacterSpacing(float paramFloat)
  {
    this.state.charSpace = paramFloat;
    this.content.append(paramFloat).append(" Tc").append_i(this.separator);
  }

  public void setWordSpacing(float paramFloat)
  {
    this.state.wordSpace = paramFloat;
    this.content.append(paramFloat).append(" Tw").append_i(this.separator);
  }

  public void setHorizontalScaling(float paramFloat)
  {
    this.state.scale = paramFloat;
    this.content.append(paramFloat).append(" Tz").append_i(this.separator);
  }

  public void setLeading(float paramFloat)
  {
    this.state.leading = paramFloat;
    this.content.append(paramFloat).append(" TL").append_i(this.separator);
  }

  public void setFontAndSize(BaseFont paramBaseFont, float paramFloat)
  {
    checkWriter();
    if ((paramFloat < 1.0E-004F) && (paramFloat > -1.0E-004F))
      throw new IllegalArgumentException("Font size too small: " + paramFloat);
    this.state.size = paramFloat;
    this.state.fontDetails = this.writer.addSimple(paramBaseFont);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = this.state.fontDetails.getFontName();
    localPdfName = localPageResources.addFont(localPdfName, this.state.fontDetails.getIndirectReference());
    this.content.append(localPdfName.getBytes()).append(' ').append(paramFloat).append(" Tf").append_i(this.separator);
  }

  public void setTextRenderingMode(int paramInt)
  {
    this.content.append(paramInt).append(" Tr").append_i(this.separator);
  }

  public void setTextRise(float paramFloat)
  {
    this.content.append(paramFloat).append(" Ts").append_i(this.separator);
  }

  private void showText2(String paramString)
  {
    if (this.state.fontDetails == null)
      throw new NullPointerException("Font and size must be set before writing any text");
    byte[] arrayOfByte = this.state.fontDetails.convertToBytes(paramString);
    escapeString(arrayOfByte, this.content);
  }

  public void showText(String paramString)
  {
    showText2(paramString);
    this.content.append("Tj").append_i(this.separator);
  }

  public static PdfTextArray getKernArray(String paramString, BaseFont paramBaseFont)
  {
    PdfTextArray localPdfTextArray = new PdfTextArray();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramString.length() - 1;
    char[] arrayOfChar = paramString.toCharArray();
    if (i >= 0)
      localStringBuffer.append(arrayOfChar, 0, 1);
    for (int j = 0; j < i; j++)
    {
      char c = arrayOfChar[(j + 1)];
      int k = paramBaseFont.getKerning(arrayOfChar[j], c);
      if (k == 0)
      {
        localStringBuffer.append(c);
      }
      else
      {
        localPdfTextArray.add(localStringBuffer.toString());
        localStringBuffer.setLength(0);
        localStringBuffer.append(arrayOfChar, j + 1, 1);
        localPdfTextArray.add(-k);
      }
    }
    localPdfTextArray.add(localStringBuffer.toString());
    return localPdfTextArray;
  }

  public void showTextKerned(String paramString)
  {
    if (this.state.fontDetails == null)
      throw new NullPointerException("Font and size must be set before writing any text");
    BaseFont localBaseFont = this.state.fontDetails.getBaseFont();
    if (localBaseFont.hasKernPairs())
      showText(getKernArray(paramString, localBaseFont));
    else
      showText(paramString);
  }

  public void newlineShowText(String paramString)
  {
    this.state.yTLM -= this.state.leading;
    showText2(paramString);
    this.content.append("'").append_i(this.separator);
  }

  public void newlineShowText(float paramFloat1, float paramFloat2, String paramString)
  {
    this.state.yTLM -= this.state.leading;
    this.content.append(paramFloat1).append(' ').append(paramFloat2);
    showText2(paramString);
    this.content.append("\"").append_i(this.separator);
    this.state.charSpace = paramFloat2;
    this.state.wordSpace = paramFloat1;
  }

  public void setTextMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.state.xTLM = paramFloat5;
    this.state.yTLM = paramFloat6;
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append_i(32).append(paramFloat3).append_i(32).append(paramFloat4).append_i(32).append(paramFloat5).append_i(32).append(paramFloat6).append(" Tm").append_i(this.separator);
  }

  public void setTextMatrix(float paramFloat1, float paramFloat2)
  {
    setTextMatrix(1.0F, 0.0F, 0.0F, 1.0F, paramFloat1, paramFloat2);
  }

  public void moveText(float paramFloat1, float paramFloat2)
  {
    this.state.xTLM += paramFloat1;
    this.state.yTLM += paramFloat2;
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(" Td").append_i(this.separator);
  }

  public void moveTextWithLeading(float paramFloat1, float paramFloat2)
  {
    this.state.xTLM += paramFloat1;
    this.state.yTLM += paramFloat2;
    this.state.leading = (-paramFloat2);
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(" TD").append_i(this.separator);
  }

  public void newlineText()
  {
    this.state.yTLM -= this.state.leading;
    this.content.append("T*").append_i(this.separator);
  }

  int size()
  {
    return this.content.size();
  }

  static byte[] escapeString(byte[] paramArrayOfByte)
  {
    ByteBuffer localByteBuffer = new ByteBuffer();
    escapeString(paramArrayOfByte, localByteBuffer);
    return localByteBuffer.toByteArray();
  }

  static void escapeString(byte[] paramArrayOfByte, ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.append_i(40);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i];
      switch (j)
      {
      case 13:
        paramByteBuffer.append("\\r");
        break;
      case 10:
        paramByteBuffer.append("\\n");
        break;
      case 9:
        paramByteBuffer.append("\\t");
        break;
      case 8:
        paramByteBuffer.append("\\b");
        break;
      case 12:
        paramByteBuffer.append("\\f");
        break;
      case 40:
      case 41:
      case 92:
        paramByteBuffer.append_i(92).append_i(j);
        break;
      default:
        paramByteBuffer.append_i(j);
      }
    }
    paramByteBuffer.append(")");
  }

  public void addOutline(PdfOutline paramPdfOutline, String paramString)
  {
    checkWriter();
    this.pdf.addOutline(paramPdfOutline, paramString);
  }

  public PdfOutline getRootOutline()
  {
    checkWriter();
    return this.pdf.getRootOutline();
  }

  public float getEffectiveStringWidth(String paramString, boolean paramBoolean)
  {
    BaseFont localBaseFont = this.state.fontDetails.getBaseFont();
    float f;
    if (paramBoolean)
      f = localBaseFont.getWidthPointKerned(paramString, this.state.size);
    else
      f = localBaseFont.getWidthPoint(paramString, this.state.size);
    if ((this.state.charSpace != 0.0F) && (paramString.length() > 1))
      f += this.state.charSpace * (paramString.length() - 1);
    int i = localBaseFont.getFontType();
    if ((this.state.wordSpace != 0.0F) && ((i == 0) || (i == 1) || (i == 5)))
      for (int j = 0; j < paramString.length() - 1; j++)
      {
        if (paramString.charAt(j) != ' ')
          continue;
        f += this.state.wordSpace;
      }
    if (this.state.scale != 100.0D)
      f = f * this.state.scale / 100.0F;
    return f;
  }

  public void showTextAligned(int paramInt, String paramString, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    showTextAligned(paramInt, paramString, paramFloat1, paramFloat2, paramFloat3, false);
  }

  private void showTextAligned(int paramInt, String paramString, float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean)
  {
    if (this.state.fontDetails == null)
      throw new NullPointerException("Font and size must be set before writing any text");
    if (paramFloat3 == 0.0F)
    {
      switch (paramInt)
      {
      case 1:
        paramFloat1 -= getEffectiveStringWidth(paramString, paramBoolean) / 2.0F;
        break;
      case 2:
        paramFloat1 -= getEffectiveStringWidth(paramString, paramBoolean);
      }
      setTextMatrix(paramFloat1, paramFloat2);
      if (paramBoolean)
        showTextKerned(paramString);
      else
        showText(paramString);
    }
    else
    {
      double d = paramFloat3 * 3.141592653589793D / 180.0D;
      float f1 = (float)Math.cos(d);
      float f2 = (float)Math.sin(d);
      float f3;
      switch (paramInt)
      {
      case 1:
        f3 = getEffectiveStringWidth(paramString, paramBoolean) / 2.0F;
        paramFloat1 -= f3 * f1;
        paramFloat2 -= f3 * f2;
        break;
      case 2:
        f3 = getEffectiveStringWidth(paramString, paramBoolean);
        paramFloat1 -= f3 * f1;
        paramFloat2 -= f3 * f2;
      }
      setTextMatrix(f1, f2, -f2, f1, paramFloat1, paramFloat2);
      if (paramBoolean)
        showTextKerned(paramString);
      else
        showText(paramString);
      setTextMatrix(0.0F, 0.0F);
    }
  }

  public void showTextAlignedKerned(int paramInt, String paramString, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    showTextAligned(paramInt, paramString, paramFloat1, paramFloat2, paramFloat3, true);
  }

  public void concatCTM(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.content.append(paramFloat1).append(' ').append(paramFloat2).append(' ').append(paramFloat3).append(' ');
    this.content.append(paramFloat4).append(' ').append(paramFloat5).append(' ').append(paramFloat6).append(" cm").append_i(this.separator);
  }

  public static ArrayList bezierArc(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    float f1;
    if (paramFloat1 > paramFloat3)
    {
      f1 = paramFloat1;
      paramFloat1 = paramFloat3;
      paramFloat3 = f1;
    }
    if (paramFloat4 > paramFloat2)
    {
      f1 = paramFloat2;
      paramFloat2 = paramFloat4;
      paramFloat4 = f1;
    }
    float f2;
    int i;
    if (Math.abs(paramFloat6) <= 90.0F)
    {
      f2 = paramFloat6;
      i = 1;
    }
    else
    {
      i = (int)Math.ceil(Math.abs(paramFloat6) / 90.0F);
      f2 = paramFloat6 / i;
    }
    float f3 = (paramFloat1 + paramFloat3) / 2.0F;
    float f4 = (paramFloat2 + paramFloat4) / 2.0F;
    float f5 = (paramFloat3 - paramFloat1) / 2.0F;
    float f6 = (paramFloat4 - paramFloat2) / 2.0F;
    float f7 = (float)(f2 * 3.141592653589793D / 360.0D);
    float f8 = (float)Math.abs(1.333333333333333D * (1.0D - Math.cos(f7)) / Math.sin(f7));
    ArrayList localArrayList = new ArrayList();
    for (int j = 0; j < i; j++)
    {
      float f9 = (float)((paramFloat5 + j * f2) * 3.141592653589793D / 180.0D);
      float f10 = (float)((paramFloat5 + (j + 1) * f2) * 3.141592653589793D / 180.0D);
      float f11 = (float)Math.cos(f9);
      float f12 = (float)Math.cos(f10);
      float f13 = (float)Math.sin(f9);
      float f14 = (float)Math.sin(f10);
      if (f2 > 0.0F)
        localArrayList.add(new float[] { f3 + f5 * f11, f4 - f6 * f13, f3 + f5 * (f11 - f8 * f13), f4 - f6 * (f13 + f8 * f11), f3 + f5 * (f12 + f8 * f14), f4 - f6 * (f14 - f8 * f12), f3 + f5 * f12, f4 - f6 * f14 });
      else
        localArrayList.add(new float[] { f3 + f5 * f11, f4 - f6 * f13, f3 + f5 * (f11 + f8 * f13), f4 - f6 * (f13 - f8 * f11), f3 + f5 * (f12 - f8 * f14), f4 - f6 * (f14 + f8 * f12), f3 + f5 * f12, f4 - f6 * f14 });
    }
    return localArrayList;
  }

  public void arc(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    ArrayList localArrayList = bezierArc(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    if (localArrayList.isEmpty())
      return;
    float[] arrayOfFloat = (float[])localArrayList.get(0);
    moveTo(arrayOfFloat[0], arrayOfFloat[1]);
    for (int i = 0; i < localArrayList.size(); i++)
    {
      arrayOfFloat = (float[])localArrayList.get(i);
      curveTo(arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5], arrayOfFloat[6], arrayOfFloat[7]);
    }
  }

  public void ellipse(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    arc(paramFloat1, paramFloat2, paramFloat3, paramFloat4, 0.0F, 360.0F);
  }

  public PdfPatternPainter createPattern(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    checkWriter();
    if ((paramFloat3 == 0.0F) || (paramFloat4 == 0.0F))
      throw new RuntimeException("XStep or YStep can not be ZERO.");
    PdfPatternPainter localPdfPatternPainter = new PdfPatternPainter(this.writer);
    localPdfPatternPainter.setWidth(paramFloat1);
    localPdfPatternPainter.setHeight(paramFloat2);
    localPdfPatternPainter.setXStep(paramFloat3);
    localPdfPatternPainter.setYStep(paramFloat4);
    this.writer.addSimplePattern(localPdfPatternPainter);
    return localPdfPatternPainter;
  }

  public PdfPatternPainter createPattern(float paramFloat1, float paramFloat2)
  {
    return createPattern(paramFloat1, paramFloat2, paramFloat1, paramFloat2);
  }

  public PdfPatternPainter createPattern(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Color paramColor)
  {
    checkWriter();
    if ((paramFloat3 == 0.0F) || (paramFloat4 == 0.0F))
      throw new RuntimeException("XStep or YStep can not be ZERO.");
    PdfPatternPainter localPdfPatternPainter = new PdfPatternPainter(this.writer, paramColor);
    localPdfPatternPainter.setWidth(paramFloat1);
    localPdfPatternPainter.setHeight(paramFloat2);
    localPdfPatternPainter.setXStep(paramFloat3);
    localPdfPatternPainter.setYStep(paramFloat4);
    this.writer.addSimplePattern(localPdfPatternPainter);
    return localPdfPatternPainter;
  }

  public PdfPatternPainter createPattern(float paramFloat1, float paramFloat2, Color paramColor)
  {
    return createPattern(paramFloat1, paramFloat2, paramFloat1, paramFloat2, paramColor);
  }

  public PdfTemplate createTemplate(float paramFloat1, float paramFloat2)
  {
    return createTemplate(paramFloat1, paramFloat2, null);
  }

  PdfTemplate createTemplate(float paramFloat1, float paramFloat2, PdfName paramPdfName)
  {
    checkWriter();
    PdfTemplate localPdfTemplate = new PdfTemplate(this.writer);
    localPdfTemplate.setWidth(paramFloat1);
    localPdfTemplate.setHeight(paramFloat2);
    this.writer.addDirectTemplateSimple(localPdfTemplate, paramPdfName);
    return localPdfTemplate;
  }

  public PdfAppearance createAppearance(float paramFloat1, float paramFloat2)
  {
    return createAppearance(paramFloat1, paramFloat2, null);
  }

  PdfAppearance createAppearance(float paramFloat1, float paramFloat2, PdfName paramPdfName)
  {
    checkWriter();
    PdfAppearance localPdfAppearance = new PdfAppearance(this.writer);
    localPdfAppearance.setWidth(paramFloat1);
    localPdfAppearance.setHeight(paramFloat2);
    this.writer.addDirectTemplateSimple(localPdfAppearance, paramPdfName);
    return localPdfAppearance;
  }

  public void addPSXObject(PdfPSXObject paramPdfPSXObject)
  {
    checkWriter();
    PdfName localPdfName = this.writer.addDirectTemplateSimple(paramPdfPSXObject, null);
    PageResources localPageResources = getPageResources();
    localPdfName = localPageResources.addXObject(localPdfName, paramPdfPSXObject.getIndirectReference());
    this.content.append(localPdfName.getBytes()).append(" Do").append_i(this.separator);
  }

  public void addTemplate(PdfTemplate paramPdfTemplate, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    checkWriter();
    checkNoPattern(paramPdfTemplate);
    PdfName localPdfName = this.writer.addDirectTemplateSimple(paramPdfTemplate, null);
    PageResources localPageResources = getPageResources();
    localPdfName = localPageResources.addXObject(localPdfName, paramPdfTemplate.getIndirectReference());
    this.content.append("q ");
    this.content.append(paramFloat1).append(' ');
    this.content.append(paramFloat2).append(' ');
    this.content.append(paramFloat3).append(' ');
    this.content.append(paramFloat4).append(' ');
    this.content.append(paramFloat5).append(' ');
    this.content.append(paramFloat6).append(" cm ");
    this.content.append(localPdfName.getBytes()).append(" Do Q").append_i(this.separator);
  }

  void addTemplateReference(PdfIndirectReference paramPdfIndirectReference, PdfName paramPdfName, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    checkWriter();
    PageResources localPageResources = getPageResources();
    paramPdfName = localPageResources.addXObject(paramPdfName, paramPdfIndirectReference);
    this.content.append("q ");
    this.content.append(paramFloat1).append(' ');
    this.content.append(paramFloat2).append(' ');
    this.content.append(paramFloat3).append(' ');
    this.content.append(paramFloat4).append(' ');
    this.content.append(paramFloat5).append(' ');
    this.content.append(paramFloat6).append(" cm ");
    this.content.append(paramPdfName.getBytes()).append(" Do Q").append_i(this.separator);
  }

  public void addTemplate(PdfTemplate paramPdfTemplate, float paramFloat1, float paramFloat2)
  {
    addTemplate(paramPdfTemplate, 1.0F, 0.0F, 0.0F, 1.0F, paramFloat1, paramFloat2);
  }

  public void setCMYKColorFill(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.content.append((paramInt1 & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((paramInt2 & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((paramInt3 & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((paramInt4 & 0xFF) / 255.0F);
    this.content.append(" k").append_i(this.separator);
  }

  public void setCMYKColorStroke(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.content.append((paramInt1 & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((paramInt2 & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((paramInt3 & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((paramInt4 & 0xFF) / 255.0F);
    this.content.append(" K").append_i(this.separator);
  }

  public void setRGBColorFill(int paramInt1, int paramInt2, int paramInt3)
  {
    HelperRGB((paramInt1 & 0xFF) / 255.0F, (paramInt2 & 0xFF) / 255.0F, (paramInt3 & 0xFF) / 255.0F);
    this.content.append(" rg").append_i(this.separator);
  }

  public void setRGBColorStroke(int paramInt1, int paramInt2, int paramInt3)
  {
    HelperRGB((paramInt1 & 0xFF) / 255.0F, (paramInt2 & 0xFF) / 255.0F, (paramInt3 & 0xFF) / 255.0F);
    this.content.append(" RG").append_i(this.separator);
  }

  public void setColorStroke(Color paramColor)
  {
    PdfXConformanceImp.checkPDFXConformance(this.writer, 1, paramColor);
    int i = ExtendedColor.getType(paramColor);
    Object localObject;
    switch (i)
    {
    case 1:
      setGrayStroke(((GrayColor)paramColor).getGray());
      break;
    case 2:
      localObject = (CMYKColor)paramColor;
      setCMYKColorStrokeF(((CMYKColor)localObject).getCyan(), ((CMYKColor)localObject).getMagenta(), ((CMYKColor)localObject).getYellow(), ((CMYKColor)localObject).getBlack());
      break;
    case 3:
      localObject = (SpotColor)paramColor;
      setColorStroke(((SpotColor)localObject).getPdfSpotColor(), ((SpotColor)localObject).getTint());
      break;
    case 4:
      localObject = (PatternColor)paramColor;
      setPatternStroke(((PatternColor)localObject).getPainter());
      break;
    case 5:
      localObject = (ShadingColor)paramColor;
      setShadingStroke(((ShadingColor)localObject).getPdfShadingPattern());
      break;
    default:
      setRGBColorStroke(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue());
    }
  }

  public void setColorFill(Color paramColor)
  {
    PdfXConformanceImp.checkPDFXConformance(this.writer, 1, paramColor);
    int i = ExtendedColor.getType(paramColor);
    Object localObject;
    switch (i)
    {
    case 1:
      setGrayFill(((GrayColor)paramColor).getGray());
      break;
    case 2:
      localObject = (CMYKColor)paramColor;
      setCMYKColorFillF(((CMYKColor)localObject).getCyan(), ((CMYKColor)localObject).getMagenta(), ((CMYKColor)localObject).getYellow(), ((CMYKColor)localObject).getBlack());
      break;
    case 3:
      localObject = (SpotColor)paramColor;
      setColorFill(((SpotColor)localObject).getPdfSpotColor(), ((SpotColor)localObject).getTint());
      break;
    case 4:
      localObject = (PatternColor)paramColor;
      setPatternFill(((PatternColor)localObject).getPainter());
      break;
    case 5:
      localObject = (ShadingColor)paramColor;
      setShadingFill(((ShadingColor)localObject).getPdfShadingPattern());
      break;
    default:
      setRGBColorFill(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue());
    }
  }

  public void setColorFill(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    checkWriter();
    this.state.colorDetails = this.writer.addSimple(paramPdfSpotColor);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = this.state.colorDetails.getColorName();
    localPdfName = localPageResources.addColor(localPdfName, this.state.colorDetails.getIndirectReference());
    this.content.append(localPdfName.getBytes()).append(" cs ").append(paramFloat).append(" scn").append_i(this.separator);
  }

  public void setColorStroke(PdfSpotColor paramPdfSpotColor, float paramFloat)
  {
    checkWriter();
    this.state.colorDetails = this.writer.addSimple(paramPdfSpotColor);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = this.state.colorDetails.getColorName();
    localPdfName = localPageResources.addColor(localPdfName, this.state.colorDetails.getIndirectReference());
    this.content.append(localPdfName.getBytes()).append(" CS ").append(paramFloat).append(" SCN").append_i(this.separator);
  }

  public void setPatternFill(PdfPatternPainter paramPdfPatternPainter)
  {
    if (paramPdfPatternPainter.isStencil())
    {
      setPatternFill(paramPdfPatternPainter, paramPdfPatternPainter.getDefaultColor());
      return;
    }
    checkWriter();
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = this.writer.addSimplePattern(paramPdfPatternPainter);
    localPdfName = localPageResources.addPattern(localPdfName, paramPdfPatternPainter.getIndirectReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(localPdfName.getBytes()).append(" scn").append_i(this.separator);
  }

  void outputColorNumbers(Color paramColor, float paramFloat)
  {
    PdfXConformanceImp.checkPDFXConformance(this.writer, 1, paramColor);
    int i = ExtendedColor.getType(paramColor);
    switch (i)
    {
    case 0:
      this.content.append(paramColor.getRed() / 255.0F);
      this.content.append(' ');
      this.content.append(paramColor.getGreen() / 255.0F);
      this.content.append(' ');
      this.content.append(paramColor.getBlue() / 255.0F);
      break;
    case 1:
      this.content.append(((GrayColor)paramColor).getGray());
      break;
    case 2:
      CMYKColor localCMYKColor = (CMYKColor)paramColor;
      this.content.append(localCMYKColor.getCyan()).append(' ').append(localCMYKColor.getMagenta());
      this.content.append(' ').append(localCMYKColor.getYellow()).append(' ').append(localCMYKColor.getBlack());
      break;
    case 3:
      this.content.append(paramFloat);
      break;
    default:
      throw new RuntimeException("Invalid color type.");
    }
  }

  public void setPatternFill(PdfPatternPainter paramPdfPatternPainter, Color paramColor)
  {
    if (ExtendedColor.getType(paramColor) == 3)
      setPatternFill(paramPdfPatternPainter, paramColor, ((SpotColor)paramColor).getTint());
    else
      setPatternFill(paramPdfPatternPainter, paramColor, 0.0F);
  }

  public void setPatternFill(PdfPatternPainter paramPdfPatternPainter, Color paramColor, float paramFloat)
  {
    checkWriter();
    if (!paramPdfPatternPainter.isStencil())
      throw new RuntimeException("An uncolored pattern was expected.");
    PageResources localPageResources = getPageResources();
    PdfName localPdfName1 = this.writer.addSimplePattern(paramPdfPatternPainter);
    localPdfName1 = localPageResources.addPattern(localPdfName1, paramPdfPatternPainter.getIndirectReference());
    ColorDetails localColorDetails = this.writer.addSimplePatternColorspace(paramColor);
    PdfName localPdfName2 = localPageResources.addColor(localColorDetails.getColorName(), localColorDetails.getIndirectReference());
    this.content.append(localPdfName2.getBytes()).append(" cs").append_i(this.separator);
    outputColorNumbers(paramColor, paramFloat);
    this.content.append(' ').append(localPdfName1.getBytes()).append(" scn").append_i(this.separator);
  }

  public void setPatternStroke(PdfPatternPainter paramPdfPatternPainter, Color paramColor)
  {
    if (ExtendedColor.getType(paramColor) == 3)
      setPatternStroke(paramPdfPatternPainter, paramColor, ((SpotColor)paramColor).getTint());
    else
      setPatternStroke(paramPdfPatternPainter, paramColor, 0.0F);
  }

  public void setPatternStroke(PdfPatternPainter paramPdfPatternPainter, Color paramColor, float paramFloat)
  {
    checkWriter();
    if (!paramPdfPatternPainter.isStencil())
      throw new RuntimeException("An uncolored pattern was expected.");
    PageResources localPageResources = getPageResources();
    PdfName localPdfName1 = this.writer.addSimplePattern(paramPdfPatternPainter);
    localPdfName1 = localPageResources.addPattern(localPdfName1, paramPdfPatternPainter.getIndirectReference());
    ColorDetails localColorDetails = this.writer.addSimplePatternColorspace(paramColor);
    PdfName localPdfName2 = localPageResources.addColor(localColorDetails.getColorName(), localColorDetails.getIndirectReference());
    this.content.append(localPdfName2.getBytes()).append(" CS").append_i(this.separator);
    outputColorNumbers(paramColor, paramFloat);
    this.content.append(' ').append(localPdfName1.getBytes()).append(" SCN").append_i(this.separator);
  }

  public void setPatternStroke(PdfPatternPainter paramPdfPatternPainter)
  {
    if (paramPdfPatternPainter.isStencil())
    {
      setPatternStroke(paramPdfPatternPainter, paramPdfPatternPainter.getDefaultColor());
      return;
    }
    checkWriter();
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = this.writer.addSimplePattern(paramPdfPatternPainter);
    localPdfName = localPageResources.addPattern(localPdfName, paramPdfPatternPainter.getIndirectReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(localPdfName.getBytes()).append(" SCN").append_i(this.separator);
  }

  public void paintShading(PdfShading paramPdfShading)
  {
    this.writer.addSimpleShading(paramPdfShading);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = localPageResources.addShading(paramPdfShading.getShadingName(), paramPdfShading.getShadingReference());
    this.content.append(localPdfName.getBytes()).append(" sh").append_i(this.separator);
    ColorDetails localColorDetails = paramPdfShading.getColorDetails();
    if (localColorDetails != null)
      localPageResources.addColor(localColorDetails.getColorName(), localColorDetails.getIndirectReference());
  }

  public void paintShading(PdfShadingPattern paramPdfShadingPattern)
  {
    paintShading(paramPdfShadingPattern.getShading());
  }

  public void setShadingFill(PdfShadingPattern paramPdfShadingPattern)
  {
    this.writer.addSimpleShadingPattern(paramPdfShadingPattern);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = localPageResources.addPattern(paramPdfShadingPattern.getPatternName(), paramPdfShadingPattern.getPatternReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(localPdfName.getBytes()).append(" scn").append_i(this.separator);
    ColorDetails localColorDetails = paramPdfShadingPattern.getColorDetails();
    if (localColorDetails != null)
      localPageResources.addColor(localColorDetails.getColorName(), localColorDetails.getIndirectReference());
  }

  public void setShadingStroke(PdfShadingPattern paramPdfShadingPattern)
  {
    this.writer.addSimpleShadingPattern(paramPdfShadingPattern);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = localPageResources.addPattern(paramPdfShadingPattern.getPatternName(), paramPdfShadingPattern.getPatternReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(localPdfName.getBytes()).append(" SCN").append_i(this.separator);
    ColorDetails localColorDetails = paramPdfShadingPattern.getColorDetails();
    if (localColorDetails != null)
      localPageResources.addColor(localColorDetails.getColorName(), localColorDetails.getIndirectReference());
  }

  protected void checkWriter()
  {
    if (this.writer == null)
      throw new NullPointerException("The writer in PdfContentByte is null.");
  }

  public void showText(PdfTextArray paramPdfTextArray)
  {
    if (this.state.fontDetails == null)
      throw new NullPointerException("Font and size must be set before writing any text");
    this.content.append("[");
    ArrayList localArrayList = paramPdfTextArray.getArrayList();
    int i = 0;
    for (int j = 0; j < localArrayList.size(); j++)
    {
      Object localObject = localArrayList.get(j);
      if ((localObject instanceof String))
      {
        showText2((String)localObject);
        i = 0;
      }
      else
      {
        if (i != 0)
          this.content.append(' ');
        else
          i = 1;
        this.content.append(((Float)localObject).floatValue());
      }
    }
    this.content.append("]TJ").append_i(this.separator);
  }

  public PdfWriter getPdfWriter()
  {
    return this.writer;
  }

  public PdfDocument getPdfDocument()
  {
    return this.pdf;
  }

  public void localGoto(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.pdf.localGoto(paramString, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public boolean localDestination(String paramString, PdfDestination paramPdfDestination)
  {
    return this.pdf.localDestination(paramString, paramPdfDestination);
  }

  public PdfContentByte getDuplicate()
  {
    return new PdfContentByte(this.writer);
  }

  public void remoteGoto(String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.pdf.remoteGoto(paramString1, paramString2, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public void remoteGoto(String paramString, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.pdf.remoteGoto(paramString, paramInt, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public void roundRectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    if (paramFloat3 < 0.0F)
    {
      paramFloat1 += paramFloat3;
      paramFloat3 = -paramFloat3;
    }
    if (paramFloat4 < 0.0F)
    {
      paramFloat2 += paramFloat4;
      paramFloat4 = -paramFloat4;
    }
    if (paramFloat5 < 0.0F)
      paramFloat5 = -paramFloat5;
    float f = 0.4477F;
    moveTo(paramFloat1 + paramFloat5, paramFloat2);
    lineTo(paramFloat1 + paramFloat3 - paramFloat5, paramFloat2);
    curveTo(paramFloat1 + paramFloat3 - paramFloat5 * f, paramFloat2, paramFloat1 + paramFloat3, paramFloat2 + paramFloat5 * f, paramFloat1 + paramFloat3, paramFloat2 + paramFloat5);
    lineTo(paramFloat1 + paramFloat3, paramFloat2 + paramFloat4 - paramFloat5);
    curveTo(paramFloat1 + paramFloat3, paramFloat2 + paramFloat4 - paramFloat5 * f, paramFloat1 + paramFloat3 - paramFloat5 * f, paramFloat2 + paramFloat4, paramFloat1 + paramFloat3 - paramFloat5, paramFloat2 + paramFloat4);
    lineTo(paramFloat1 + paramFloat5, paramFloat2 + paramFloat4);
    curveTo(paramFloat1 + paramFloat5 * f, paramFloat2 + paramFloat4, paramFloat1, paramFloat2 + paramFloat4 - paramFloat5 * f, paramFloat1, paramFloat2 + paramFloat4 - paramFloat5);
    lineTo(paramFloat1, paramFloat2 + paramFloat5);
    curveTo(paramFloat1, paramFloat2 + paramFloat5 * f, paramFloat1 + paramFloat5 * f, paramFloat2, paramFloat1 + paramFloat5, paramFloat2);
  }

  public void setAction(PdfAction paramPdfAction, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.pdf.setAction(paramPdfAction, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }

  public void setLiteral(String paramString)
  {
    this.content.append(paramString);
  }

  public void setLiteral(char paramChar)
  {
    this.content.append(paramChar);
  }

  public void setLiteral(float paramFloat)
  {
    this.content.append(paramFloat);
  }

  void checkNoPattern(PdfTemplate paramPdfTemplate)
  {
    if (paramPdfTemplate.getType() == 3)
      throw new RuntimeException("Invalid use of a pattern. A template was expected.");
  }

  public void drawRadioField(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
  {
    float f;
    if (paramFloat1 > paramFloat3)
    {
      f = paramFloat1;
      paramFloat1 = paramFloat3;
      paramFloat3 = f;
    }
    if (paramFloat2 > paramFloat4)
    {
      f = paramFloat2;
      paramFloat2 = paramFloat4;
      paramFloat4 = f;
    }
    setLineWidth(1.0F);
    setLineCap(1);
    setColorStroke(new Color(192, 192, 192));
    arc(paramFloat1 + 1.0F, paramFloat2 + 1.0F, paramFloat3 - 1.0F, paramFloat4 - 1.0F, 0.0F, 360.0F);
    stroke();
    setLineWidth(1.0F);
    setLineCap(1);
    setColorStroke(new Color(160, 160, 160));
    arc(paramFloat1 + 0.5F, paramFloat2 + 0.5F, paramFloat3 - 0.5F, paramFloat4 - 0.5F, 45.0F, 180.0F);
    stroke();
    setLineWidth(1.0F);
    setLineCap(1);
    setColorStroke(new Color(0, 0, 0));
    arc(paramFloat1 + 1.5F, paramFloat2 + 1.5F, paramFloat3 - 1.5F, paramFloat4 - 1.5F, 45.0F, 180.0F);
    stroke();
    if (paramBoolean)
    {
      setLineWidth(1.0F);
      setLineCap(1);
      setColorFill(new Color(0, 0, 0));
      arc(paramFloat1 + 4.0F, paramFloat2 + 4.0F, paramFloat3 - 4.0F, paramFloat4 - 4.0F, 0.0F, 360.0F);
      fill();
    }
  }

  public void drawTextField(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f;
    if (paramFloat1 > paramFloat3)
    {
      f = paramFloat1;
      paramFloat1 = paramFloat3;
      paramFloat3 = f;
    }
    if (paramFloat2 > paramFloat4)
    {
      f = paramFloat2;
      paramFloat2 = paramFloat4;
      paramFloat4 = f;
    }
    setColorStroke(new Color(192, 192, 192));
    setLineWidth(1.0F);
    setLineCap(0);
    rectangle(paramFloat1, paramFloat2, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    stroke();
    setLineWidth(1.0F);
    setLineCap(0);
    setColorFill(new Color(255, 255, 255));
    rectangle(paramFloat1 + 0.5F, paramFloat2 + 0.5F, paramFloat3 - paramFloat1 - 1.0F, paramFloat4 - paramFloat2 - 1.0F);
    fill();
    setColorStroke(new Color(192, 192, 192));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(paramFloat1 + 1.0F, paramFloat2 + 1.5F);
    lineTo(paramFloat3 - 1.5F, paramFloat2 + 1.5F);
    lineTo(paramFloat3 - 1.5F, paramFloat4 - 1.0F);
    stroke();
    setColorStroke(new Color(160, 160, 160));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(paramFloat1 + 1.0F, paramFloat2 + 1.0F);
    lineTo(paramFloat1 + 1.0F, paramFloat4 - 1.0F);
    lineTo(paramFloat3 - 1.0F, paramFloat4 - 1.0F);
    stroke();
    setColorStroke(new Color(0, 0, 0));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(paramFloat1 + 2.0F, paramFloat2 + 2.0F);
    lineTo(paramFloat1 + 2.0F, paramFloat4 - 2.0F);
    lineTo(paramFloat3 - 2.0F, paramFloat4 - 2.0F);
    stroke();
  }

  public void drawButton(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, String paramString, BaseFont paramBaseFont, float paramFloat5)
  {
    float f;
    if (paramFloat1 > paramFloat3)
    {
      f = paramFloat1;
      paramFloat1 = paramFloat3;
      paramFloat3 = f;
    }
    if (paramFloat2 > paramFloat4)
    {
      f = paramFloat2;
      paramFloat2 = paramFloat4;
      paramFloat4 = f;
    }
    setColorStroke(new Color(0, 0, 0));
    setLineWidth(1.0F);
    setLineCap(0);
    rectangle(paramFloat1, paramFloat2, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
    stroke();
    setLineWidth(1.0F);
    setLineCap(0);
    setColorFill(new Color(192, 192, 192));
    rectangle(paramFloat1 + 0.5F, paramFloat2 + 0.5F, paramFloat3 - paramFloat1 - 1.0F, paramFloat4 - paramFloat2 - 1.0F);
    fill();
    setColorStroke(new Color(255, 255, 255));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(paramFloat1 + 1.0F, paramFloat2 + 1.0F);
    lineTo(paramFloat1 + 1.0F, paramFloat4 - 1.0F);
    lineTo(paramFloat3 - 1.0F, paramFloat4 - 1.0F);
    stroke();
    setColorStroke(new Color(160, 160, 160));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(paramFloat1 + 1.0F, paramFloat2 + 1.0F);
    lineTo(paramFloat3 - 1.0F, paramFloat2 + 1.0F);
    lineTo(paramFloat3 - 1.0F, paramFloat4 - 1.0F);
    stroke();
    resetRGBColorFill();
    beginText();
    setFontAndSize(paramBaseFont, paramFloat5);
    showTextAligned(1, paramString, paramFloat1 + (paramFloat3 - paramFloat1) / 2.0F, paramFloat2 + (paramFloat4 - paramFloat2 - paramFloat5) / 2.0F, 0.0F);
    endText();
  }

  public Graphics2D createGraphicsShapes(float paramFloat1, float paramFloat2)
  {
    return new PdfGraphics2D(this, paramFloat1, paramFloat2, null, true, false, 0.0F);
  }

  public Graphics2D createPrinterGraphicsShapes(float paramFloat1, float paramFloat2, PrinterJob paramPrinterJob)
  {
    return new PdfPrinterGraphics2D(this, paramFloat1, paramFloat2, null, true, false, 0.0F, paramPrinterJob);
  }

  public Graphics2D createGraphics(float paramFloat1, float paramFloat2)
  {
    return new PdfGraphics2D(this, paramFloat1, paramFloat2, null, false, false, 0.0F);
  }

  public Graphics2D createPrinterGraphics(float paramFloat1, float paramFloat2, PrinterJob paramPrinterJob)
  {
    return new PdfPrinterGraphics2D(this, paramFloat1, paramFloat2, null, false, false, 0.0F, paramPrinterJob);
  }

  public Graphics2D createGraphics(float paramFloat1, float paramFloat2, boolean paramBoolean, float paramFloat3)
  {
    return new PdfGraphics2D(this, paramFloat1, paramFloat2, null, false, paramBoolean, paramFloat3);
  }

  public Graphics2D createPrinterGraphics(float paramFloat1, float paramFloat2, boolean paramBoolean, float paramFloat3, PrinterJob paramPrinterJob)
  {
    return new PdfPrinterGraphics2D(this, paramFloat1, paramFloat2, null, false, paramBoolean, paramFloat3, paramPrinterJob);
  }

  public Graphics2D createGraphicsShapes(float paramFloat1, float paramFloat2, boolean paramBoolean, float paramFloat3)
  {
    return new PdfGraphics2D(this, paramFloat1, paramFloat2, null, true, paramBoolean, paramFloat3);
  }

  public Graphics2D createPrinterGraphicsShapes(float paramFloat1, float paramFloat2, boolean paramBoolean, float paramFloat3, PrinterJob paramPrinterJob)
  {
    return new PdfPrinterGraphics2D(this, paramFloat1, paramFloat2, null, true, paramBoolean, paramFloat3, paramPrinterJob);
  }

  public Graphics2D createGraphics(float paramFloat1, float paramFloat2, FontMapper paramFontMapper)
  {
    return new PdfGraphics2D(this, paramFloat1, paramFloat2, paramFontMapper, false, false, 0.0F);
  }

  public Graphics2D createPrinterGraphics(float paramFloat1, float paramFloat2, FontMapper paramFontMapper, PrinterJob paramPrinterJob)
  {
    return new PdfPrinterGraphics2D(this, paramFloat1, paramFloat2, paramFontMapper, false, false, 0.0F, paramPrinterJob);
  }

  public Graphics2D createGraphics(float paramFloat1, float paramFloat2, FontMapper paramFontMapper, boolean paramBoolean, float paramFloat3)
  {
    return new PdfGraphics2D(this, paramFloat1, paramFloat2, paramFontMapper, false, paramBoolean, paramFloat3);
  }

  public Graphics2D createPrinterGraphics(float paramFloat1, float paramFloat2, FontMapper paramFontMapper, boolean paramBoolean, float paramFloat3, PrinterJob paramPrinterJob)
  {
    return new PdfPrinterGraphics2D(this, paramFloat1, paramFloat2, paramFontMapper, false, paramBoolean, paramFloat3, paramPrinterJob);
  }

  PageResources getPageResources()
  {
    return this.pdf.getPageResources();
  }

  public void setGState(PdfGState paramPdfGState)
  {
    PdfObject[] arrayOfPdfObject = this.writer.addSimpleExtGState(paramPdfGState);
    PageResources localPageResources = getPageResources();
    PdfName localPdfName = localPageResources.addExtGState((PdfName)arrayOfPdfObject[0], (PdfIndirectReference)arrayOfPdfObject[1]);
    this.content.append(localPdfName.getBytes()).append(" gs").append_i(this.separator);
  }

  public void beginLayer(PdfOCG paramPdfOCG)
  {
    if (((paramPdfOCG instanceof PdfLayer)) && (((PdfLayer)paramPdfOCG).getTitle() != null))
      throw new IllegalArgumentException("A title is not a layer");
    if (this.layerDepth == null)
      this.layerDepth = new ArrayList();
    if ((paramPdfOCG instanceof PdfLayerMembership))
    {
      this.layerDepth.add(new Integer(1));
      beginLayer2(paramPdfOCG);
      return;
    }
    int i = 0;
    for (PdfLayer localPdfLayer = (PdfLayer)paramPdfOCG; localPdfLayer != null; localPdfLayer = localPdfLayer.getParent())
    {
      if (localPdfLayer.getTitle() != null)
        continue;
      beginLayer2(localPdfLayer);
      i++;
    }
    this.layerDepth.add(new Integer(i));
  }

  private void beginLayer2(PdfOCG paramPdfOCG)
  {
    PdfName localPdfName = (PdfName)this.writer.addSimpleProperty(paramPdfOCG, paramPdfOCG.getRef())[0];
    PageResources localPageResources = getPageResources();
    localPdfName = localPageResources.addProperty(localPdfName, paramPdfOCG.getRef());
    this.content.append("/OC ").append(localPdfName.getBytes()).append(" BDC").append_i(this.separator);
  }

  public void endLayer()
  {
    int i = 1;
    if ((this.layerDepth != null) && (!this.layerDepth.isEmpty()))
    {
      i = ((Integer)this.layerDepth.get(this.layerDepth.size() - 1)).intValue();
      this.layerDepth.remove(this.layerDepth.size() - 1);
    }
    else
    {
      throw new IllegalPdfSyntaxException("Unbalanced layer operators.");
    }
    while (i-- > 0)
      this.content.append("EMC").append_i(this.separator);
  }

  public void transform(AffineTransform paramAffineTransform)
  {
    double[] arrayOfDouble = new double[6];
    paramAffineTransform.getMatrix(arrayOfDouble);
    this.content.append(arrayOfDouble[0]).append(' ').append(arrayOfDouble[1]).append(' ').append(arrayOfDouble[2]).append(' ');
    this.content.append(arrayOfDouble[3]).append(' ').append(arrayOfDouble[4]).append(' ').append(arrayOfDouble[5]).append(" cm").append_i(this.separator);
  }

  void addAnnotation(PdfAnnotation paramPdfAnnotation)
  {
    this.writer.addAnnotation(paramPdfAnnotation);
  }

  public void setDefaultColorspace(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    PageResources localPageResources = getPageResources();
    localPageResources.addDefaultColor(paramPdfName, paramPdfObject);
  }

  public void beginMarkedContentSequence(PdfStructureElement paramPdfStructureElement)
  {
    PdfObject localPdfObject = paramPdfStructureElement.get(PdfName.K);
    int i = this.pdf.getMarkPoint();
    if (localPdfObject != null)
    {
      PdfArray localPdfArray = null;
      if (localPdfObject.isNumber())
      {
        localPdfArray = new PdfArray();
        localPdfArray.add(localPdfObject);
        paramPdfStructureElement.put(PdfName.K, localPdfArray);
      }
      else if (localPdfObject.isArray())
      {
        localPdfArray = (PdfArray)localPdfObject;
        if (!localPdfArray.getPdfObject(0).isNumber())
          throw new IllegalArgumentException("The structure has kids.");
      }
      else
      {
        throw new IllegalArgumentException("Unknown object at /K " + localPdfObject.getClass().toString());
      }
      PdfDictionary localPdfDictionary = new PdfDictionary(PdfName.MCR);
      localPdfDictionary.put(PdfName.PG, this.writer.getCurrentPage());
      localPdfDictionary.put(PdfName.MCID, new PdfNumber(i));
      localPdfArray.add(localPdfDictionary);
      paramPdfStructureElement.setPageMark(this.writer.getPageNumber() - 1, -1);
    }
    else
    {
      paramPdfStructureElement.setPageMark(this.writer.getPageNumber() - 1, i);
      paramPdfStructureElement.put(PdfName.PG, this.writer.getCurrentPage());
    }
    this.pdf.incMarkPoint();
    this.mcDepth += 1;
    this.content.append(paramPdfStructureElement.get(PdfName.S).getBytes()).append(" <</MCID ").append(i).append(">> BDC").append_i(this.separator);
  }

  public void endMarkedContentSequence()
  {
    if (this.mcDepth == 0)
      throw new IllegalPdfSyntaxException("Unbalanced begin/end marked content operators.");
    this.mcDepth -= 1;
    this.content.append("EMC").append_i(this.separator);
  }

  public void beginMarkedContentSequence(PdfName paramPdfName, PdfDictionary paramPdfDictionary, boolean paramBoolean)
  {
    if (paramPdfDictionary == null)
    {
      this.content.append(paramPdfName.getBytes()).append(" BMC").append_i(this.separator);
      return;
    }
    this.content.append(paramPdfName.getBytes()).append(' ');
    if (paramBoolean)
    {
      try
      {
        paramPdfDictionary.toPdf(this.writer, this.content);
      }
      catch (Exception localException)
      {
        throw new ExceptionConverter(localException);
      }
    }
    else
    {
      PdfObject[] arrayOfPdfObject;
      if (this.writer.propertyExists(paramPdfDictionary))
        arrayOfPdfObject = this.writer.addSimpleProperty(paramPdfDictionary, null);
      else
        arrayOfPdfObject = this.writer.addSimpleProperty(paramPdfDictionary, this.writer.getPdfIndirectReference());
      PdfName localPdfName = (PdfName)arrayOfPdfObject[0];
      PageResources localPageResources = getPageResources();
      localPdfName = localPageResources.addProperty(localPdfName, (PdfIndirectReference)arrayOfPdfObject[1]);
      this.content.append(localPdfName.getBytes());
    }
    this.content.append(" BDC").append_i(this.separator);
    this.mcDepth += 1;
  }

  public void beginMarkedContentSequence(PdfName paramPdfName)
  {
    beginMarkedContentSequence(paramPdfName, null, false);
  }

  public void sanityCheck()
  {
    if (this.mcDepth != 0)
      throw new IllegalPdfSyntaxException("Unbalanced marked content operators.");
    if (this.inText)
      throw new IllegalPdfSyntaxException("Unbalanced begin/end text operators.");
    if ((this.layerDepth != null) && (!this.layerDepth.isEmpty()))
      throw new IllegalPdfSyntaxException("Unbalanced layer operators.");
    if (!this.stateList.isEmpty())
      throw new IllegalPdfSyntaxException("Unbalanced save/restore state operators.");
  }

  static
  {
    abrev.put(PdfName.BITSPERCOMPONENT, "/BPC ");
    abrev.put(PdfName.COLORSPACE, "/CS ");
    abrev.put(PdfName.DECODE, "/D ");
    abrev.put(PdfName.DECODEPARMS, "/DP ");
    abrev.put(PdfName.FILTER, "/F ");
    abrev.put(PdfName.HEIGHT, "/H ");
    abrev.put(PdfName.IMAGEMASK, "/IM ");
    abrev.put(PdfName.INTENT, "/Intent ");
    abrev.put(PdfName.INTERPOLATE, "/I ");
    abrev.put(PdfName.WIDTH, "/W ");
  }

  static class GraphicState
  {
    FontDetails fontDetails;
    ColorDetails colorDetails;
    float size;
    protected float xTLM = 0.0F;
    protected float yTLM = 0.0F;
    protected float leading = 0.0F;
    protected float scale = 100.0F;
    protected float charSpace = 0.0F;
    protected float wordSpace = 0.0F;

    GraphicState()
    {
    }

    GraphicState(GraphicState paramGraphicState)
    {
      this.fontDetails = paramGraphicState.fontDetails;
      this.colorDetails = paramGraphicState.colorDetails;
      this.size = paramGraphicState.size;
      this.xTLM = paramGraphicState.xTLM;
      this.yTLM = paramGraphicState.yTLM;
      this.leading = paramGraphicState.leading;
      this.scale = paramGraphicState.scale;
      this.charSpace = paramGraphicState.charSpace;
      this.wordSpace = paramGraphicState.wordSpace;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfContentByte
 * JD-Core Version:    0.6.0
 */