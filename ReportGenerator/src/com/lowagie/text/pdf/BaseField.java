package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class BaseField
{
  public static final float BORDER_WIDTH_THIN = 1.0F;
  public static final float BORDER_WIDTH_MEDIUM = 2.0F;
  public static final float BORDER_WIDTH_THICK = 3.0F;
  public static final int VISIBLE = 0;
  public static final int HIDDEN = 1;
  public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
  public static final int HIDDEN_BUT_PRINTABLE = 3;
  public static final int READ_ONLY = 1;
  public static final int REQUIRED = 2;
  public static final int MULTILINE = 4096;
  public static final int DO_NOT_SCROLL = 8388608;
  public static final int PASSWORD = 8192;
  public static final int FILE_SELECTION = 1048576;
  public static final int DO_NOT_SPELL_CHECK = 4194304;
  public static final int EDIT = 262144;
  public static final int COMB = 16777216;
  protected float borderWidth = 1.0F;
  protected int borderStyle = 0;
  protected Color borderColor;
  protected Color backgroundColor;
  protected Color textColor;
  protected BaseFont font;
  protected float fontSize = 0.0F;
  protected int alignment = 0;
  protected PdfWriter writer;
  protected String text;
  protected Rectangle box;
  protected int rotation = 0;
  protected int visibility;
  protected String fieldName;
  protected int options;
  protected int maxCharacterLength;
  private static final HashMap fieldKeys = new HashMap();

  public BaseField(PdfWriter paramPdfWriter, Rectangle paramRectangle, String paramString)
  {
    this.writer = paramPdfWriter;
    setBox(paramRectangle);
    this.fieldName = paramString;
  }

  protected BaseFont getRealFont()
    throws IOException, DocumentException
  {
    if (this.font == null)
      return BaseFont.createFont("Helvetica", "Cp1252", false);
    return this.font;
  }

  protected PdfAppearance getBorderAppearance()
  {
    PdfAppearance localPdfAppearance = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
    switch (this.rotation)
    {
    case 90:
      localPdfAppearance.setMatrix(0.0F, 1.0F, -1.0F, 0.0F, this.box.getHeight(), 0.0F);
      break;
    case 180:
      localPdfAppearance.setMatrix(-1.0F, 0.0F, 0.0F, -1.0F, this.box.getWidth(), this.box.getHeight());
      break;
    case 270:
      localPdfAppearance.setMatrix(0.0F, -1.0F, 1.0F, 0.0F, 0.0F, this.box.getWidth());
    }
    localPdfAppearance.saveState();
    if (this.backgroundColor != null)
    {
      localPdfAppearance.setColorFill(this.backgroundColor);
      localPdfAppearance.rectangle(0.0F, 0.0F, this.box.getWidth(), this.box.getHeight());
      localPdfAppearance.fill();
    }
    if (this.borderStyle == 4)
    {
      if ((this.borderWidth != 0.0F) && (this.borderColor != null))
      {
        localPdfAppearance.setColorStroke(this.borderColor);
        localPdfAppearance.setLineWidth(this.borderWidth);
        localPdfAppearance.moveTo(0.0F, this.borderWidth / 2.0F);
        localPdfAppearance.lineTo(this.box.getWidth(), this.borderWidth / 2.0F);
        localPdfAppearance.stroke();
      }
    }
    else if (this.borderStyle == 2)
    {
      if ((this.borderWidth != 0.0F) && (this.borderColor != null))
      {
        localPdfAppearance.setColorStroke(this.borderColor);
        localPdfAppearance.setLineWidth(this.borderWidth);
        localPdfAppearance.rectangle(this.borderWidth / 2.0F, this.borderWidth / 2.0F, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        localPdfAppearance.stroke();
      }
      Color localColor = this.backgroundColor;
      if (localColor == null)
        localColor = Color.white;
      localPdfAppearance.setGrayFill(1.0F);
      drawTopFrame(localPdfAppearance);
      localPdfAppearance.setColorFill(localColor.darker());
      drawBottomFrame(localPdfAppearance);
    }
    else if (this.borderStyle == 3)
    {
      if ((this.borderWidth != 0.0F) && (this.borderColor != null))
      {
        localPdfAppearance.setColorStroke(this.borderColor);
        localPdfAppearance.setLineWidth(this.borderWidth);
        localPdfAppearance.rectangle(this.borderWidth / 2.0F, this.borderWidth / 2.0F, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        localPdfAppearance.stroke();
      }
      localPdfAppearance.setGrayFill(0.5F);
      drawTopFrame(localPdfAppearance);
      localPdfAppearance.setGrayFill(0.75F);
      drawBottomFrame(localPdfAppearance);
    }
    else if ((this.borderWidth != 0.0F) && (this.borderColor != null))
    {
      if (this.borderStyle == 1)
        localPdfAppearance.setLineDash(3.0F, 0.0F);
      localPdfAppearance.setColorStroke(this.borderColor);
      localPdfAppearance.setLineWidth(this.borderWidth);
      localPdfAppearance.rectangle(this.borderWidth / 2.0F, this.borderWidth / 2.0F, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
      localPdfAppearance.stroke();
      if (((this.options & 0x1000000) != 0) && (this.maxCharacterLength > 1))
      {
        float f1 = this.box.getWidth() / this.maxCharacterLength;
        float f2 = this.borderWidth / 2.0F;
        float f3 = this.box.getHeight() - this.borderWidth / 2.0F;
        for (int i = 1; i < this.maxCharacterLength; i++)
        {
          float f4 = f1 * i;
          localPdfAppearance.moveTo(f4, f2);
          localPdfAppearance.lineTo(f4, f3);
        }
        localPdfAppearance.stroke();
      }
    }
    localPdfAppearance.restoreState();
    return localPdfAppearance;
  }

  protected static ArrayList getHardBreaks(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    StringBuffer localStringBuffer = new StringBuffer();
    for (int j = 0; j < i; j++)
    {
      char c = arrayOfChar[j];
      if (c == '\r')
      {
        if ((j + 1 < i) && (arrayOfChar[(j + 1)] == '\n'))
          j++;
        localArrayList.add(localStringBuffer.toString());
        localStringBuffer = new StringBuffer();
      }
      else if (c == '\n')
      {
        localArrayList.add(localStringBuffer.toString());
        localStringBuffer = new StringBuffer();
      }
      else
      {
        localStringBuffer.append(c);
      }
    }
    localArrayList.add(localStringBuffer.toString());
    return localArrayList;
  }

  protected static void trimRight(StringBuffer paramStringBuffer)
  {
    int i = paramStringBuffer.length();
    while (true)
    {
      if (i == 0)
        return;
      i--;
      if (paramStringBuffer.charAt(i) != ' ')
        return;
      paramStringBuffer.setLength(i);
    }
  }

  protected static ArrayList breakLines(ArrayList paramArrayList, BaseFont paramBaseFont, float paramFloat1, float paramFloat2)
  {
    ArrayList localArrayList = new ArrayList();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      localStringBuffer.setLength(0);
      float f = 0.0F;
      char[] arrayOfChar = ((String)paramArrayList.get(i)).toCharArray();
      int j = arrayOfChar.length;
      int k = 0;
      int m = -1;
      char c = '\000';
      int n = 0;
      for (int i1 = 0; i1 < j; i1++)
      {
        c = arrayOfChar[i1];
        switch (k)
        {
        case 0:
          f += paramBaseFont.getWidthPoint(c, paramFloat1);
          localStringBuffer.append(c);
          if (f > paramFloat2)
          {
            f = 0.0F;
            if (localStringBuffer.length() > 1)
            {
              i1--;
              localStringBuffer.setLength(localStringBuffer.length() - 1);
            }
            localArrayList.add(localStringBuffer.toString());
            localStringBuffer.setLength(0);
            n = i1;
            if (c == ' ')
              k = 2;
            else
              k = 1;
          }
          else
          {
            if (c == ' ')
              continue;
            k = 1;
          }
          break;
        case 1:
          f += paramBaseFont.getWidthPoint(c, paramFloat1);
          localStringBuffer.append(c);
          if (c == ' ')
            m = i1;
          if (f <= paramFloat2)
            continue;
          f = 0.0F;
          if (m >= 0)
          {
            i1 = m;
            localStringBuffer.setLength(m - n);
            trimRight(localStringBuffer);
            localArrayList.add(localStringBuffer.toString());
            localStringBuffer.setLength(0);
            n = i1;
            m = -1;
            k = 2;
          }
          else
          {
            if (localStringBuffer.length() > 1)
            {
              i1--;
              localStringBuffer.setLength(localStringBuffer.length() - 1);
            }
            localArrayList.add(localStringBuffer.toString());
            localStringBuffer.setLength(0);
            n = i1;
            if (c != ' ')
              continue;
            k = 2;
          }
          break;
        case 2:
          if (c == ' ')
            continue;
          f = 0.0F;
          i1--;
          k = 1;
        }
      }
      trimRight(localStringBuffer);
      localArrayList.add(localStringBuffer.toString());
    }
    return localArrayList;
  }

  private void drawTopFrame(PdfAppearance paramPdfAppearance)
  {
    paramPdfAppearance.moveTo(this.borderWidth, this.borderWidth);
    paramPdfAppearance.lineTo(this.borderWidth, this.box.getHeight() - this.borderWidth);
    paramPdfAppearance.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
    paramPdfAppearance.lineTo(this.box.getWidth() - 2.0F * this.borderWidth, this.box.getHeight() - 2.0F * this.borderWidth);
    paramPdfAppearance.lineTo(2.0F * this.borderWidth, this.box.getHeight() - 2.0F * this.borderWidth);
    paramPdfAppearance.lineTo(2.0F * this.borderWidth, 2.0F * this.borderWidth);
    paramPdfAppearance.lineTo(this.borderWidth, this.borderWidth);
    paramPdfAppearance.fill();
  }

  private void drawBottomFrame(PdfAppearance paramPdfAppearance)
  {
    paramPdfAppearance.moveTo(this.borderWidth, this.borderWidth);
    paramPdfAppearance.lineTo(this.box.getWidth() - this.borderWidth, this.borderWidth);
    paramPdfAppearance.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
    paramPdfAppearance.lineTo(this.box.getWidth() - 2.0F * this.borderWidth, this.box.getHeight() - 2.0F * this.borderWidth);
    paramPdfAppearance.lineTo(this.box.getWidth() - 2.0F * this.borderWidth, 2.0F * this.borderWidth);
    paramPdfAppearance.lineTo(2.0F * this.borderWidth, 2.0F * this.borderWidth);
    paramPdfAppearance.lineTo(this.borderWidth, this.borderWidth);
    paramPdfAppearance.fill();
  }

  public float getBorderWidth()
  {
    return this.borderWidth;
  }

  public void setBorderWidth(float paramFloat)
  {
    this.borderWidth = paramFloat;
  }

  public int getBorderStyle()
  {
    return this.borderStyle;
  }

  public void setBorderStyle(int paramInt)
  {
    this.borderStyle = paramInt;
  }

  public Color getBorderColor()
  {
    return this.borderColor;
  }

  public void setBorderColor(Color paramColor)
  {
    this.borderColor = paramColor;
  }

  public Color getBackgroundColor()
  {
    return this.backgroundColor;
  }

  public void setBackgroundColor(Color paramColor)
  {
    this.backgroundColor = paramColor;
  }

  public Color getTextColor()
  {
    return this.textColor;
  }

  public void setTextColor(Color paramColor)
  {
    this.textColor = paramColor;
  }

  public BaseFont getFont()
  {
    return this.font;
  }

  public void setFont(BaseFont paramBaseFont)
  {
    this.font = paramBaseFont;
  }

  public float getFontSize()
  {
    return this.fontSize;
  }

  public void setFontSize(float paramFloat)
  {
    this.fontSize = paramFloat;
  }

  public int getAlignment()
  {
    return this.alignment;
  }

  public void setAlignment(int paramInt)
  {
    this.alignment = paramInt;
  }

  public String getText()
  {
    return this.text;
  }

  public void setText(String paramString)
  {
    this.text = paramString;
  }

  public Rectangle getBox()
  {
    return this.box;
  }

  public void setBox(Rectangle paramRectangle)
  {
    if (paramRectangle == null)
    {
      this.box = null;
    }
    else
    {
      this.box = new Rectangle(paramRectangle);
      this.box.normalize();
    }
  }

  public int getRotation()
  {
    return this.rotation;
  }

  public void setRotation(int paramInt)
  {
    if (paramInt % 90 != 0)
      throw new IllegalArgumentException("Rotation must be a multiple of 90.");
    paramInt %= 360;
    if (paramInt < 0)
      paramInt += 360;
    this.rotation = paramInt;
  }

  public void setRotationFromPage(Rectangle paramRectangle)
  {
    setRotation(paramRectangle.getRotation());
  }

  public int getVisibility()
  {
    return this.visibility;
  }

  public void setVisibility(int paramInt)
  {
    this.visibility = paramInt;
  }

  public String getFieldName()
  {
    return this.fieldName;
  }

  public void setFieldName(String paramString)
  {
    this.fieldName = paramString;
  }

  public int getOptions()
  {
    return this.options;
  }

  public void setOptions(int paramInt)
  {
    this.options = paramInt;
  }

  public int getMaxCharacterLength()
  {
    return this.maxCharacterLength;
  }

  public void setMaxCharacterLength(int paramInt)
  {
    this.maxCharacterLength = paramInt;
  }

  public PdfWriter getWriter()
  {
    return this.writer;
  }

  public void setWriter(PdfWriter paramPdfWriter)
  {
    this.writer = paramPdfWriter;
  }

  public static void moveFields(PdfDictionary paramPdfDictionary1, PdfDictionary paramPdfDictionary2)
  {
    Iterator localIterator = paramPdfDictionary1.getKeys().iterator();
    while (localIterator.hasNext())
    {
      PdfName localPdfName = (PdfName)localIterator.next();
      if (!fieldKeys.containsKey(localPdfName))
        continue;
      if (paramPdfDictionary2 != null)
        paramPdfDictionary2.put(localPdfName, paramPdfDictionary1.get(localPdfName));
      localIterator.remove();
    }
  }

  static
  {
    fieldKeys.putAll(PdfCopyFieldsImp.fieldKeys);
    fieldKeys.put(PdfName.T, new Integer(1));
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BaseField
 * JD-Core Version:    0.6.0
 */