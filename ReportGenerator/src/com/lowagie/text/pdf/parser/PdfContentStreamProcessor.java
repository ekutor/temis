package com.lowagie.text.pdf.parser;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.CMapAwareDocumentFont;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

public abstract class PdfContentStreamProcessor
{
  private Map operators;
  private PdfDictionary resources;
  private Stack gsStack = new Stack();
  private Matrix textMatrix;
  private Matrix textLineMatrix;

  public PdfContentStreamProcessor()
  {
    populateOperators();
    reset();
  }

  private void populateOperators()
  {
    this.operators = new HashMap();
    this.operators.put("q", new PushGraphicsState(null));
    this.operators.put("Q", new PopGraphicsState(null));
    this.operators.put("cm", new ModifyCurrentTransformationMatrix(null));
    this.operators.put("gs", new ProcessGraphicsStateResource(null));
    this.operators.put("Tc", new SetTextCharacterSpacing(null));
    this.operators.put("Tw", new SetTextWordSpacing(null));
    this.operators.put("Tz", new SetTextHorizontalScaling(null));
    this.operators.put("TL", new SetTextLeading(null));
    this.operators.put("Tf", new SetTextFont(null));
    this.operators.put("Tr", new SetTextRenderMode(null));
    this.operators.put("Ts", new SetTextRise(null));
    this.operators.put("BT", new BeginText(null));
    this.operators.put("ET", new EndText(null));
    this.operators.put("Td", new TextMoveStartNextLine(null));
    this.operators.put("TD", new TextMoveStartNextLineWithLeading(null));
    this.operators.put("Tm", new TextSetTextMatrix(null));
    this.operators.put("T*", new TextMoveNextLine(null));
    this.operators.put("Tj", new ShowText(null));
    this.operators.put("'", new MoveNextLineAndShowText(null));
    this.operators.put("\"", new MoveNextLineAndShowTextWithSpacing(null));
    this.operators.put("TJ", new ShowTextArray(null));
  }

  public void reset()
  {
    this.gsStack.removeAllElements();
    this.gsStack.add(new GraphicsState());
    this.textMatrix = null;
    this.textLineMatrix = null;
    this.resources = null;
  }

  public GraphicsState gs()
  {
    return (GraphicsState)this.gsStack.peek();
  }

  public Matrix getCurrentTextMatrix()
  {
    return this.textMatrix;
  }

  public Matrix getCurrentTextLineMatrix()
  {
    return this.textLineMatrix;
  }

  public void invokeOperator(PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
  {
    ContentOperator localContentOperator = (ContentOperator)this.operators.get(paramPdfLiteral.toString());
    if (localContentOperator == null)
      return;
    localContentOperator.invoke(this, paramPdfLiteral, paramArrayList);
  }

  private String decode(PdfString paramPdfString)
  {
    byte[] arrayOfByte = paramPdfString.getBytes();
    return gs().font.decode(arrayOfByte, 0, arrayOfByte.length);
  }

  public abstract void displayText(String paramString, Matrix paramMatrix);

  public float getStringWidth(String paramString, float paramFloat)
  {
    CMapAwareDocumentFont localCMapAwareDocumentFont = gs().font;
    char[] arrayOfChar = paramString.toCharArray();
    float f1 = 0.0F;
    for (int i = 0; i < arrayOfChar.length; i++)
    {
      float f2 = localCMapAwareDocumentFont.getWidth(arrayOfChar[i]) / 1000.0F;
      float f3 = arrayOfChar[i] == ' ' ? gs().wordSpacing : 0.0F;
      f1 += ((f2 - paramFloat / 1000.0F) * gs().fontSize + gs().characterSpacing + f3) * gs().horizontalScaling;
    }
    return f1;
  }

  public void displayPdfString(PdfString paramPdfString, float paramFloat)
  {
    String str = decode(paramPdfString);
    float f = getStringWidth(str, paramFloat);
    Matrix localMatrix = new Matrix(f, 0.0F).multiply(this.textMatrix);
    displayText(str, localMatrix);
    this.textMatrix = localMatrix;
  }

  public void processContent(byte[] paramArrayOfByte, PdfDictionary paramPdfDictionary)
  {
    reset();
    this.resources = paramPdfDictionary;
    try
    {
      PdfContentParser localPdfContentParser = new PdfContentParser(new PRTokeniser(paramArrayOfByte));
      ArrayList localArrayList = new ArrayList();
      while (localPdfContentParser.parse(localArrayList).size() > 0)
      {
        PdfLiteral localPdfLiteral = (PdfLiteral)localArrayList.get(localArrayList.size() - 1);
        invokeOperator(localPdfLiteral, localArrayList);
      }
    }
    catch (Exception localException)
    {
      throw new ExceptionConverter(localException);
    }
  }

  private static class EndText
    implements ContentOperator
  {
    private EndText()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfContentStreamProcessor.access$2202(paramPdfContentStreamProcessor, null);
      PdfContentStreamProcessor.access$2102(paramPdfContentStreamProcessor, null);
    }

    EndText(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class BeginText
    implements ContentOperator
  {
    private BeginText()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfContentStreamProcessor.access$2202(paramPdfContentStreamProcessor, new Matrix());
      PdfContentStreamProcessor.access$2102(paramPdfContentStreamProcessor, paramPdfContentStreamProcessor.textMatrix);
    }

    BeginText(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class PopGraphicsState
    implements ContentOperator
  {
    private PopGraphicsState()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      paramPdfContentStreamProcessor.gsStack.pop();
    }

    PopGraphicsState(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class ModifyCurrentTransformationMatrix
    implements ContentOperator
  {
    private ModifyCurrentTransformationMatrix()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      float f1 = ((PdfNumber)paramArrayList.get(0)).floatValue();
      float f2 = ((PdfNumber)paramArrayList.get(1)).floatValue();
      float f3 = ((PdfNumber)paramArrayList.get(2)).floatValue();
      float f4 = ((PdfNumber)paramArrayList.get(3)).floatValue();
      float f5 = ((PdfNumber)paramArrayList.get(4)).floatValue();
      float f6 = ((PdfNumber)paramArrayList.get(5)).floatValue();
      Matrix localMatrix = new Matrix(f1, f2, f3, f4, f5, f6);
      GraphicsState localGraphicsState = (GraphicsState)paramPdfContentStreamProcessor.gsStack.peek();
      localGraphicsState.ctm = localGraphicsState.ctm.multiply(localMatrix);
    }

    ModifyCurrentTransformationMatrix(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class PushGraphicsState
    implements ContentOperator
  {
    private PushGraphicsState()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      GraphicsState localGraphicsState1 = (GraphicsState)paramPdfContentStreamProcessor.gsStack.peek();
      GraphicsState localGraphicsState2 = new GraphicsState(localGraphicsState1);
      paramPdfContentStreamProcessor.gsStack.push(localGraphicsState2);
    }

    PushGraphicsState(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class ProcessGraphicsStateResource
    implements ContentOperator
  {
    private ProcessGraphicsStateResource()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfName localPdfName = (PdfName)paramArrayList.get(0);
      PdfDictionary localPdfDictionary1 = paramPdfContentStreamProcessor.resources.getAsDict(PdfName.EXTGSTATE);
      if (localPdfDictionary1 == null)
        throw new IllegalArgumentException("Resources do not contain ExtGState entry. Unable to process operator " + paramPdfLiteral);
      PdfDictionary localPdfDictionary2 = localPdfDictionary1.getAsDict(localPdfName);
      if (localPdfDictionary2 == null)
        throw new IllegalArgumentException(localPdfName + " is an unknown graphics state dictionary");
      PdfArray localPdfArray = localPdfDictionary2.getAsArray(PdfName.FONT);
      if (localPdfArray != null)
      {
        CMapAwareDocumentFont localCMapAwareDocumentFont = new CMapAwareDocumentFont((PRIndirectReference)localPdfArray.getPdfObject(0));
        float f = localPdfArray.getAsNumber(1).floatValue();
        paramPdfContentStreamProcessor.gs().font = localCMapAwareDocumentFont;
        paramPdfContentStreamProcessor.gs().fontSize = f;
      }
    }

    ProcessGraphicsStateResource(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextWordSpacing
    implements ContentOperator
  {
    private SetTextWordSpacing()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber = (PdfNumber)paramArrayList.get(0);
      paramPdfContentStreamProcessor.gs().wordSpacing = localPdfNumber.floatValue();
    }

    SetTextWordSpacing(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextCharacterSpacing
    implements ContentOperator
  {
    private SetTextCharacterSpacing()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber = (PdfNumber)paramArrayList.get(0);
      paramPdfContentStreamProcessor.gs().characterSpacing = localPdfNumber.floatValue();
    }

    SetTextCharacterSpacing(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextHorizontalScaling
    implements ContentOperator
  {
    private SetTextHorizontalScaling()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber = (PdfNumber)paramArrayList.get(0);
      paramPdfContentStreamProcessor.gs().horizontalScaling = localPdfNumber.floatValue();
    }

    SetTextHorizontalScaling(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextLeading
    implements ContentOperator
  {
    private SetTextLeading()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber = (PdfNumber)paramArrayList.get(0);
      paramPdfContentStreamProcessor.gs().leading = localPdfNumber.floatValue();
    }

    SetTextLeading(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextRise
    implements ContentOperator
  {
    private SetTextRise()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber = (PdfNumber)paramArrayList.get(0);
      paramPdfContentStreamProcessor.gs().rise = localPdfNumber.floatValue();
    }

    SetTextRise(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextRenderMode
    implements ContentOperator
  {
    private SetTextRenderMode()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber = (PdfNumber)paramArrayList.get(0);
      paramPdfContentStreamProcessor.gs().renderMode = localPdfNumber.intValue();
    }

    SetTextRenderMode(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class SetTextFont
    implements ContentOperator
  {
    private SetTextFont()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfName localPdfName = (PdfName)paramArrayList.get(0);
      float f = ((PdfNumber)paramArrayList.get(1)).floatValue();
      PdfDictionary localPdfDictionary = paramPdfContentStreamProcessor.resources.getAsDict(PdfName.FONT);
      CMapAwareDocumentFont localCMapAwareDocumentFont = new CMapAwareDocumentFont((PRIndirectReference)localPdfDictionary.get(localPdfName));
      paramPdfContentStreamProcessor.gs().font = localCMapAwareDocumentFont;
      paramPdfContentStreamProcessor.gs().fontSize = f;
    }

    SetTextFont(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class TextMoveStartNextLine
    implements ContentOperator
  {
    private TextMoveStartNextLine()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      float f1 = ((PdfNumber)paramArrayList.get(0)).floatValue();
      float f2 = ((PdfNumber)paramArrayList.get(1)).floatValue();
      Matrix localMatrix = new Matrix(f1, f2);
      PdfContentStreamProcessor.access$2202(paramPdfContentStreamProcessor, localMatrix.multiply(paramPdfContentStreamProcessor.textLineMatrix));
      PdfContentStreamProcessor.access$2102(paramPdfContentStreamProcessor, paramPdfContentStreamProcessor.textMatrix);
    }

    TextMoveStartNextLine(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class TextMoveStartNextLineWithLeading
    implements ContentOperator
  {
    private TextMoveStartNextLineWithLeading()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      float f = ((PdfNumber)paramArrayList.get(1)).floatValue();
      ArrayList localArrayList = new ArrayList(1);
      localArrayList.add(0, new PdfNumber(-f));
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("TL"), localArrayList);
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("Td"), paramArrayList);
    }

    TextMoveStartNextLineWithLeading(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class TextSetTextMatrix
    implements ContentOperator
  {
    private TextSetTextMatrix()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      float f1 = ((PdfNumber)paramArrayList.get(0)).floatValue();
      float f2 = ((PdfNumber)paramArrayList.get(1)).floatValue();
      float f3 = ((PdfNumber)paramArrayList.get(2)).floatValue();
      float f4 = ((PdfNumber)paramArrayList.get(3)).floatValue();
      float f5 = ((PdfNumber)paramArrayList.get(4)).floatValue();
      float f6 = ((PdfNumber)paramArrayList.get(5)).floatValue();
      PdfContentStreamProcessor.access$2102(paramPdfContentStreamProcessor, new Matrix(f1, f2, f3, f4, f5, f6));
      PdfContentStreamProcessor.access$2202(paramPdfContentStreamProcessor, paramPdfContentStreamProcessor.textLineMatrix);
    }

    TextSetTextMatrix(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class TextMoveNextLine
    implements ContentOperator
  {
    private TextMoveNextLine()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      ArrayList localArrayList = new ArrayList(2);
      localArrayList.add(0, new PdfNumber(0));
      localArrayList.add(1, new PdfNumber(paramPdfContentStreamProcessor.gs().leading));
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("Td"), localArrayList);
    }

    TextMoveNextLine(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class ShowText
    implements ContentOperator
  {
    private ShowText()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfString localPdfString = (PdfString)paramArrayList.get(0);
      paramPdfContentStreamProcessor.displayPdfString(localPdfString, 0.0F);
    }

    ShowText(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class MoveNextLineAndShowText
    implements ContentOperator
  {
    private MoveNextLineAndShowText()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("T*"), new ArrayList(0));
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("Tj"), paramArrayList);
    }

    MoveNextLineAndShowText(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class MoveNextLineAndShowTextWithSpacing
    implements ContentOperator
  {
    private MoveNextLineAndShowTextWithSpacing()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfNumber localPdfNumber1 = (PdfNumber)paramArrayList.get(0);
      PdfNumber localPdfNumber2 = (PdfNumber)paramArrayList.get(1);
      PdfString localPdfString = (PdfString)paramArrayList.get(2);
      ArrayList localArrayList1 = new ArrayList(1);
      localArrayList1.add(0, localPdfNumber1);
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("Tw"), localArrayList1);
      ArrayList localArrayList2 = new ArrayList(1);
      localArrayList2.add(0, localPdfNumber2);
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("Tc"), localArrayList2);
      ArrayList localArrayList3 = new ArrayList(1);
      localArrayList3.add(0, localPdfString);
      paramPdfContentStreamProcessor.invokeOperator(new PdfLiteral("'"), localArrayList3);
    }

    MoveNextLineAndShowTextWithSpacing(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }

  private static class ShowTextArray
    implements ContentOperator
  {
    private ShowTextArray()
    {
    }

    public void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList paramArrayList)
    {
      PdfArray localPdfArray = (PdfArray)paramArrayList.get(0);
      float f = 0.0F;
      ListIterator localListIterator = localPdfArray.listIterator();
      while (localListIterator.hasNext())
      {
        Object localObject = localListIterator.next();
        if ((localObject instanceof PdfString))
        {
          paramPdfContentStreamProcessor.displayPdfString((PdfString)localObject, f);
          f = 0.0F;
          continue;
        }
        f = ((PdfNumber)localObject).floatValue();
      }
    }

    ShowTextArray(PdfContentStreamProcessor.1 param1)
    {
      this();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.PdfContentStreamProcessor
 * JD-Core Version:    0.6.0
 */