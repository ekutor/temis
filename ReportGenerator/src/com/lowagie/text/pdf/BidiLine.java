package com.lowagie.text.pdf;

import com.lowagie.text.Utilities;
import java.util.ArrayList;

public class BidiLine
{
  protected int runDirection;
  protected int pieceSize = 256;
  protected char[] text = new char[this.pieceSize];
  protected PdfChunk[] detailChunks = new PdfChunk[this.pieceSize];
  protected int totalTextLength = 0;
  protected byte[] orderLevels = new byte[this.pieceSize];
  protected int[] indexChars = new int[this.pieceSize];
  protected ArrayList chunks = new ArrayList();
  protected int indexChunk = 0;
  protected int indexChunkChar = 0;
  protected int currentChar = 0;
  protected int storedRunDirection;
  protected char[] storedText = new char[0];
  protected PdfChunk[] storedDetailChunks = new PdfChunk[0];
  protected int storedTotalTextLength = 0;
  protected byte[] storedOrderLevels = new byte[0];
  protected int[] storedIndexChars = new int[0];
  protected int storedIndexChunk = 0;
  protected int storedIndexChunkChar = 0;
  protected int storedCurrentChar = 0;
  protected boolean shortStore;
  protected static final IntHashtable mirrorChars = new IntHashtable();
  protected int arabicOptions;

  public BidiLine()
  {
  }

  public BidiLine(BidiLine paramBidiLine)
  {
    this.runDirection = paramBidiLine.runDirection;
    this.pieceSize = paramBidiLine.pieceSize;
    this.text = ((char[])paramBidiLine.text.clone());
    this.detailChunks = ((PdfChunk[])paramBidiLine.detailChunks.clone());
    this.totalTextLength = paramBidiLine.totalTextLength;
    this.orderLevels = ((byte[])paramBidiLine.orderLevels.clone());
    this.indexChars = ((int[])paramBidiLine.indexChars.clone());
    this.chunks = new ArrayList(paramBidiLine.chunks);
    this.indexChunk = paramBidiLine.indexChunk;
    this.indexChunkChar = paramBidiLine.indexChunkChar;
    this.currentChar = paramBidiLine.currentChar;
    this.storedRunDirection = paramBidiLine.storedRunDirection;
    this.storedText = ((char[])paramBidiLine.storedText.clone());
    this.storedDetailChunks = ((PdfChunk[])paramBidiLine.storedDetailChunks.clone());
    this.storedTotalTextLength = paramBidiLine.storedTotalTextLength;
    this.storedOrderLevels = ((byte[])paramBidiLine.storedOrderLevels.clone());
    this.storedIndexChars = ((int[])paramBidiLine.storedIndexChars.clone());
    this.storedIndexChunk = paramBidiLine.storedIndexChunk;
    this.storedIndexChunkChar = paramBidiLine.storedIndexChunkChar;
    this.storedCurrentChar = paramBidiLine.storedCurrentChar;
    this.shortStore = paramBidiLine.shortStore;
    this.arabicOptions = paramBidiLine.arabicOptions;
  }

  public boolean isEmpty()
  {
    return (this.currentChar >= this.totalTextLength) && (this.indexChunk >= this.chunks.size());
  }

  public void clearChunks()
  {
    this.chunks.clear();
    this.totalTextLength = 0;
    this.currentChar = 0;
  }

  public boolean getParagraph(int paramInt)
  {
    this.runDirection = paramInt;
    this.currentChar = 0;
    this.totalTextLength = 0;
    int i = 0;
    Object localObject1;
    Object localObject2;
    int k;
    while (this.indexChunk < this.chunks.size())
    {
      localObject1 = (PdfChunk)this.chunks.get(this.indexChunk);
      BaseFont localBaseFont = ((PdfChunk)localObject1).font().getFont();
      localObject2 = ((PdfChunk)localObject1).toString();
      k = ((String)localObject2).length();
      while (this.indexChunkChar < k)
      {
        char c = ((String)localObject2).charAt(this.indexChunkChar);
        int j = (char)localBaseFont.getUnicodeEquivalent(c);
        if ((j == 13) || (j == 10))
        {
          if ((j == 13) && (this.indexChunkChar + 1 < k) && (((String)localObject2).charAt(this.indexChunkChar + 1) == '\n'))
            this.indexChunkChar += 1;
          this.indexChunkChar += 1;
          if (this.indexChunkChar >= k)
          {
            this.indexChunkChar = 0;
            this.indexChunk += 1;
          }
          i = 1;
          if (this.totalTextLength != 0)
            break;
          this.detailChunks[0] = localObject1;
          break;
        }
        addPiece(c, (PdfChunk)localObject1);
        this.indexChunkChar += 1;
      }
      if (i != 0)
        break;
      this.indexChunkChar = 0;
      this.indexChunk += 1;
    }
    if (this.totalTextLength == 0)
      return i;
    this.totalTextLength = (trimRight(0, this.totalTextLength - 1) + 1);
    if (this.totalTextLength == 0)
      return true;
    if ((paramInt == 2) || (paramInt == 3))
    {
      if (this.orderLevels.length < this.totalTextLength)
      {
        this.orderLevels = new byte[this.pieceSize];
        this.indexChars = new int[this.pieceSize];
      }
      ArabicLigaturizer.processNumbers(this.text, 0, this.totalTextLength, this.arabicOptions);
      localObject1 = new BidiOrder(this.text, 0, this.totalTextLength, (byte)(paramInt == 3 ? 1 : 0));
      localObject2 = ((BidiOrder)localObject1).getLevels();
      for (k = 0; k < this.totalTextLength; k++)
      {
        this.orderLevels[k] = localObject2[k];
        this.indexChars[k] = k;
      }
      doArabicShapping();
      mirrorGlyphs();
    }
    this.totalTextLength = (trimRightEx(0, this.totalTextLength - 1) + 1);
    return true;
  }

  public void addChunk(PdfChunk paramPdfChunk)
  {
    this.chunks.add(paramPdfChunk);
  }

  public void addChunks(ArrayList paramArrayList)
  {
    this.chunks.addAll(paramArrayList);
  }

  public void addPiece(char paramChar, PdfChunk paramPdfChunk)
  {
    if (this.totalTextLength >= this.pieceSize)
    {
      char[] arrayOfChar = this.text;
      PdfChunk[] arrayOfPdfChunk = this.detailChunks;
      this.pieceSize *= 2;
      this.text = new char[this.pieceSize];
      this.detailChunks = new PdfChunk[this.pieceSize];
      System.arraycopy(arrayOfChar, 0, this.text, 0, this.totalTextLength);
      System.arraycopy(arrayOfPdfChunk, 0, this.detailChunks, 0, this.totalTextLength);
    }
    this.text[this.totalTextLength] = paramChar;
    this.detailChunks[(this.totalTextLength++)] = paramPdfChunk;
  }

  public void save()
  {
    if (this.indexChunk > 0)
    {
      if (this.indexChunk >= this.chunks.size())
      {
        this.chunks.clear();
      }
      else
      {
        this.indexChunk -= 1;
        while (this.indexChunk >= 0)
        {
          this.chunks.remove(this.indexChunk);
          this.indexChunk -= 1;
        }
      }
      this.indexChunk = 0;
    }
    this.storedRunDirection = this.runDirection;
    this.storedTotalTextLength = this.totalTextLength;
    this.storedIndexChunk = this.indexChunk;
    this.storedIndexChunkChar = this.indexChunkChar;
    this.storedCurrentChar = this.currentChar;
    this.shortStore = (this.currentChar < this.totalTextLength);
    if (!this.shortStore)
    {
      if (this.storedText.length < this.totalTextLength)
      {
        this.storedText = new char[this.totalTextLength];
        this.storedDetailChunks = new PdfChunk[this.totalTextLength];
      }
      System.arraycopy(this.text, 0, this.storedText, 0, this.totalTextLength);
      System.arraycopy(this.detailChunks, 0, this.storedDetailChunks, 0, this.totalTextLength);
    }
    if ((this.runDirection == 2) || (this.runDirection == 3))
    {
      if (this.storedOrderLevels.length < this.totalTextLength)
      {
        this.storedOrderLevels = new byte[this.totalTextLength];
        this.storedIndexChars = new int[this.totalTextLength];
      }
      System.arraycopy(this.orderLevels, this.currentChar, this.storedOrderLevels, this.currentChar, this.totalTextLength - this.currentChar);
      System.arraycopy(this.indexChars, this.currentChar, this.storedIndexChars, this.currentChar, this.totalTextLength - this.currentChar);
    }
  }

  public void restore()
  {
    this.runDirection = this.storedRunDirection;
    this.totalTextLength = this.storedTotalTextLength;
    this.indexChunk = this.storedIndexChunk;
    this.indexChunkChar = this.storedIndexChunkChar;
    this.currentChar = this.storedCurrentChar;
    if (!this.shortStore)
    {
      System.arraycopy(this.storedText, 0, this.text, 0, this.totalTextLength);
      System.arraycopy(this.storedDetailChunks, 0, this.detailChunks, 0, this.totalTextLength);
    }
    if ((this.runDirection == 2) || (this.runDirection == 3))
    {
      System.arraycopy(this.storedOrderLevels, this.currentChar, this.orderLevels, this.currentChar, this.totalTextLength - this.currentChar);
      System.arraycopy(this.storedIndexChars, this.currentChar, this.indexChars, this.currentChar, this.totalTextLength - this.currentChar);
    }
  }

  public void mirrorGlyphs()
  {
    for (int i = 0; i < this.totalTextLength; i++)
    {
      if ((this.orderLevels[i] & 0x1) != 1)
        continue;
      int j = mirrorChars.get(this.text[i]);
      if (j == 0)
        continue;
      this.text[i] = (char)j;
    }
  }

  public void doArabicShapping()
  {
    int i = 0;
    int j = 0;
    while (true)
    {
      if (i < this.totalTextLength)
      {
        k = this.text[i];
        if ((k < 1536) || (k > 1791))
        {
          if (i != j)
          {
            this.text[j] = this.text[i];
            this.detailChunks[j] = this.detailChunks[i];
            this.orderLevels[j] = this.orderLevels[i];
          }
          i++;
          j++;
          continue;
        }
      }
      if (i >= this.totalTextLength)
      {
        this.totalTextLength = j;
        return;
      }
      int k = i;
      i++;
      while (i < this.totalTextLength)
      {
        m = this.text[i];
        if ((m < 1536) || (m > 1791))
          break;
        i++;
      }
      int m = i - k;
      int n = ArabicLigaturizer.arabic_shape(this.text, k, m, this.text, j, m, this.arabicOptions);
      if (k != j)
      {
        for (int i1 = 0; i1 < n; i1++)
        {
          this.detailChunks[j] = this.detailChunks[k];
          this.orderLevels[(j++)] = this.orderLevels[(k++)];
        }
        continue;
      }
      j += n;
    }
  }

  public PdfLine processLine(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, int paramInt3)
  {
    this.arabicOptions = paramInt3;
    save();
    boolean bool1 = paramInt2 == 3;
    if (this.currentChar >= this.totalTextLength)
    {
      boolean bool2 = getParagraph(paramInt2);
      if (!bool2)
        return null;
      if (this.totalTextLength == 0)
      {
        ArrayList localArrayList = new ArrayList();
        PdfChunk localPdfChunk1 = new PdfChunk("", this.detailChunks[0]);
        localArrayList.add(localPdfChunk1);
        return new PdfLine(0.0F, 0.0F, 0.0F, paramInt1, true, localArrayList, bool1);
      }
    }
    float f1 = paramFloat2;
    int i = -1;
    if (this.currentChar != 0)
      this.currentChar = trimLeftEx(this.currentChar, this.totalTextLength - 1);
    int j = this.currentChar;
    int k = 0;
    PdfChunk localPdfChunk2 = null;
    float f2 = 0.0F;
    PdfChunk localPdfChunk3 = null;
    boolean bool3 = false;
    boolean bool4 = false;
    while (this.currentChar < this.totalTextLength)
    {
      localPdfChunk2 = this.detailChunks[this.currentChar];
      bool4 = Utilities.isSurrogatePair(this.text, this.currentChar);
      if (bool4)
        k = localPdfChunk2.getUnicodeEquivalent(Utilities.convertToUtf32(this.text, this.currentChar));
      else
        k = localPdfChunk2.getUnicodeEquivalent(this.text[this.currentChar]);
      if (!PdfChunk.noPrint(k))
      {
        if (bool4)
          f2 = localPdfChunk2.getCharWidth(k);
        else
          f2 = localPdfChunk2.getCharWidth(this.text[this.currentChar]);
        bool3 = localPdfChunk2.isExtSplitCharacter(j, this.currentChar, this.totalTextLength, this.text, this.detailChunks);
        if ((bool3) && (Character.isWhitespace((char)k)))
          i = this.currentChar;
        if (paramFloat2 - f2 < 0.0F)
          break;
        if (bool3)
          i = this.currentChar;
        paramFloat2 -= f2;
        localPdfChunk3 = localPdfChunk2;
        if (localPdfChunk2.isTab())
        {
          Object[] arrayOfObject = (Object[])localPdfChunk2.getAttribute("TAB");
          float f3 = ((Float)arrayOfObject[1]).floatValue();
          boolean bool5 = ((Boolean)arrayOfObject[2]).booleanValue();
          if ((bool5) && (f3 < f1 - paramFloat2))
            return new PdfLine(0.0F, f1, paramFloat2, paramInt1, true, createArrayOfPdfChunks(j, this.currentChar - 1), bool1);
          this.detailChunks[this.currentChar].adjustLeft(paramFloat1);
          paramFloat2 = f1 - f3;
        }
        if (bool4)
          this.currentChar += 1;
      }
      this.currentChar += 1;
    }
    if (localPdfChunk3 == null)
    {
      this.currentChar += 1;
      if (bool4)
        this.currentChar += 1;
      return new PdfLine(0.0F, f1, 0.0F, paramInt1, false, createArrayOfPdfChunks(this.currentChar - 1, this.currentChar - 1), bool1);
    }
    if (this.currentChar >= this.totalTextLength)
      return new PdfLine(0.0F, f1, paramFloat2, paramInt1, true, createArrayOfPdfChunks(j, this.totalTextLength - 1), bool1);
    int m = trimRightEx(j, this.currentChar - 1);
    if (m < j)
      return new PdfLine(0.0F, f1, paramFloat2, paramInt1, false, createArrayOfPdfChunks(j, this.currentChar - 1), bool1);
    if (m == this.currentChar - 1)
    {
      HyphenationEvent localHyphenationEvent = (HyphenationEvent)localPdfChunk3.getAttribute("HYPHENATION");
      if (localHyphenationEvent != null)
      {
        int[] arrayOfInt = getWord(j, m);
        if (arrayOfInt != null)
        {
          float f4 = paramFloat2 + getWidth(arrayOfInt[0], this.currentChar - 1);
          String str1 = localHyphenationEvent.getHyphenatedWordPre(new String(this.text, arrayOfInt[0], arrayOfInt[1] - arrayOfInt[0]), localPdfChunk3.font().getFont(), localPdfChunk3.font().size(), f4);
          String str2 = localHyphenationEvent.getHyphenatedWordPost();
          if (str1.length() > 0)
          {
            PdfChunk localPdfChunk4 = new PdfChunk(str1, localPdfChunk3);
            this.currentChar = (arrayOfInt[1] - str2.length());
            return new PdfLine(0.0F, f1, f4 - localPdfChunk3.font().width(str1), paramInt1, false, createArrayOfPdfChunks(j, arrayOfInt[0] - 1, localPdfChunk4), bool1);
          }
        }
      }
    }
    if ((i == -1) || (i >= m))
      return new PdfLine(0.0F, f1, paramFloat2 + getWidth(m + 1, this.currentChar - 1), paramInt1, false, createArrayOfPdfChunks(j, m), bool1);
    this.currentChar = (i + 1);
    m = trimRightEx(j, i);
    if (m < j)
      m = this.currentChar - 1;
    return new PdfLine(0.0F, f1, f1 - getWidth(j, m), paramInt1, false, createArrayOfPdfChunks(j, m), bool1);
  }

  public float getWidth(int paramInt1, int paramInt2)
  {
    int i = 0;
    PdfChunk localPdfChunk = null;
    float f = 0.0F;
    while (paramInt1 <= paramInt2)
    {
      boolean bool = Utilities.isSurrogatePair(this.text, paramInt1);
      if (bool)
      {
        f += this.detailChunks[paramInt1].getCharWidth(Utilities.convertToUtf32(this.text, paramInt1));
        paramInt1++;
      }
      else
      {
        i = this.text[paramInt1];
        localPdfChunk = this.detailChunks[paramInt1];
        if (!PdfChunk.noPrint(localPdfChunk.getUnicodeEquivalent(i)))
          f += this.detailChunks[paramInt1].getCharWidth(i);
      }
      paramInt1++;
    }
    return f;
  }

  public ArrayList createArrayOfPdfChunks(int paramInt1, int paramInt2)
  {
    return createArrayOfPdfChunks(paramInt1, paramInt2, null);
  }

  public ArrayList createArrayOfPdfChunks(int paramInt1, int paramInt2, PdfChunk paramPdfChunk)
  {
    int i = (this.runDirection == 2) || (this.runDirection == 3) ? 1 : 0;
    if (i != 0)
      reorder(paramInt1, paramInt2);
    ArrayList localArrayList = new ArrayList();
    Object localObject = this.detailChunks[paramInt1];
    PdfChunk localPdfChunk = null;
    StringBuffer localStringBuffer = new StringBuffer();
    int j = 0;
    while (paramInt1 <= paramInt2)
    {
      j = i != 0 ? this.indexChars[paramInt1] : paramInt1;
      char c = this.text[j];
      localPdfChunk = this.detailChunks[j];
      if (!PdfChunk.noPrint(localPdfChunk.getUnicodeEquivalent(c)))
        if ((localPdfChunk.isImage()) || (localPdfChunk.isSeparator()) || (localPdfChunk.isTab()))
        {
          if (localStringBuffer.length() > 0)
          {
            localArrayList.add(new PdfChunk(localStringBuffer.toString(), (PdfChunk)localObject));
            localStringBuffer = new StringBuffer();
          }
          localArrayList.add(localPdfChunk);
        }
        else if (localPdfChunk == localObject)
        {
          localStringBuffer.append(c);
        }
        else
        {
          if (localStringBuffer.length() > 0)
          {
            localArrayList.add(new PdfChunk(localStringBuffer.toString(), (PdfChunk)localObject));
            localStringBuffer = new StringBuffer();
          }
          if ((!localPdfChunk.isImage()) && (!localPdfChunk.isSeparator()) && (!localPdfChunk.isTab()))
            localStringBuffer.append(c);
          localObject = localPdfChunk;
        }
      paramInt1++;
    }
    if (localStringBuffer.length() > 0)
      localArrayList.add(new PdfChunk(localStringBuffer.toString(), (PdfChunk)localObject));
    if (paramPdfChunk != null)
      localArrayList.add(paramPdfChunk);
    return (ArrayList)localArrayList;
  }

  public int[] getWord(int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    int j = paramInt2;
    while ((i < this.totalTextLength) && (Character.isLetter(this.text[i])))
      i++;
    if (i == paramInt2)
      return null;
    while ((j >= paramInt1) && (Character.isLetter(this.text[j])))
      j--;
    j++;
    return new int[] { j, i };
  }

  public int trimRight(int paramInt1, int paramInt2)
  {
    for (int i = paramInt2; i >= paramInt1; i--)
    {
      char c = (char)this.detailChunks[i].getUnicodeEquivalent(this.text[i]);
      if (!isWS(c))
        break;
    }
    return i;
  }

  public int trimLeft(int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i <= paramInt2; i++)
    {
      char c = (char)this.detailChunks[i].getUnicodeEquivalent(this.text[i]);
      if (!isWS(c))
        break;
    }
    return i;
  }

  public int trimRightEx(int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    char c = '\000';
    while (i >= paramInt1)
    {
      c = (char)this.detailChunks[i].getUnicodeEquivalent(this.text[i]);
      if ((!isWS(c)) && (!PdfChunk.noPrint(c)))
        break;
      i--;
    }
    return i;
  }

  public int trimLeftEx(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    char c = '\000';
    while (i <= paramInt2)
    {
      c = (char)this.detailChunks[i].getUnicodeEquivalent(this.text[i]);
      if ((!isWS(c)) && (!PdfChunk.noPrint(c)))
        break;
      i++;
    }
    return i;
  }

  public void reorder(int paramInt1, int paramInt2)
  {
    int i = this.orderLevels[paramInt1];
    int j = i;
    int k = i;
    int m = i;
    int i1;
    for (int n = paramInt1 + 1; n <= paramInt2; n++)
    {
      i1 = this.orderLevels[n];
      if (i1 > i)
        i = i1;
      else if (i1 < j)
        j = i1;
      k = (byte)(k & i1);
      m = (byte)(m | i1);
    }
    if ((m & 0x1) == 0)
      return;
    if ((k & 0x1) == 1)
    {
      flip(paramInt1, paramInt2 + 1);
      return;
    }
    j = (byte)(j | 0x1);
    while (i >= j)
    {
      n = paramInt1;
      while (true)
      {
        if ((n <= paramInt2) && (this.orderLevels[n] < i))
        {
          n++;
          continue;
        }
        if (n > paramInt2)
          break;
        for (i1 = n + 1; (i1 <= paramInt2) && (this.orderLevels[i1] >= i); i1++);
        flip(n, i1);
        n = i1 + 1;
      }
      i = (byte)(i - 1);
    }
  }

  public void flip(int paramInt1, int paramInt2)
  {
    int i = (paramInt1 + paramInt2) / 2;
    paramInt2--;
    while (paramInt1 < i)
    {
      int j = this.indexChars[paramInt1];
      this.indexChars[paramInt1] = this.indexChars[paramInt2];
      this.indexChars[paramInt2] = j;
      paramInt1++;
      paramInt2--;
    }
  }

  public static boolean isWS(char paramChar)
  {
    return paramChar <= ' ';
  }

  static
  {
    mirrorChars.put(40, 41);
    mirrorChars.put(41, 40);
    mirrorChars.put(60, 62);
    mirrorChars.put(62, 60);
    mirrorChars.put(91, 93);
    mirrorChars.put(93, 91);
    mirrorChars.put(123, 125);
    mirrorChars.put(125, 123);
    mirrorChars.put(171, 187);
    mirrorChars.put(187, 171);
    mirrorChars.put(8249, 8250);
    mirrorChars.put(8250, 8249);
    mirrorChars.put(8261, 8262);
    mirrorChars.put(8262, 8261);
    mirrorChars.put(8317, 8318);
    mirrorChars.put(8318, 8317);
    mirrorChars.put(8333, 8334);
    mirrorChars.put(8334, 8333);
    mirrorChars.put(8712, 8715);
    mirrorChars.put(8713, 8716);
    mirrorChars.put(8714, 8717);
    mirrorChars.put(8715, 8712);
    mirrorChars.put(8716, 8713);
    mirrorChars.put(8717, 8714);
    mirrorChars.put(8725, 10741);
    mirrorChars.put(8764, 8765);
    mirrorChars.put(8765, 8764);
    mirrorChars.put(8771, 8909);
    mirrorChars.put(8786, 8787);
    mirrorChars.put(8787, 8786);
    mirrorChars.put(8788, 8789);
    mirrorChars.put(8789, 8788);
    mirrorChars.put(8804, 8805);
    mirrorChars.put(8805, 8804);
    mirrorChars.put(8806, 8807);
    mirrorChars.put(8807, 8806);
    mirrorChars.put(8808, 8809);
    mirrorChars.put(8809, 8808);
    mirrorChars.put(8810, 8811);
    mirrorChars.put(8811, 8810);
    mirrorChars.put(8814, 8815);
    mirrorChars.put(8815, 8814);
    mirrorChars.put(8816, 8817);
    mirrorChars.put(8817, 8816);
    mirrorChars.put(8818, 8819);
    mirrorChars.put(8819, 8818);
    mirrorChars.put(8820, 8821);
    mirrorChars.put(8821, 8820);
    mirrorChars.put(8822, 8823);
    mirrorChars.put(8823, 8822);
    mirrorChars.put(8824, 8825);
    mirrorChars.put(8825, 8824);
    mirrorChars.put(8826, 8827);
    mirrorChars.put(8827, 8826);
    mirrorChars.put(8828, 8829);
    mirrorChars.put(8829, 8828);
    mirrorChars.put(8830, 8831);
    mirrorChars.put(8831, 8830);
    mirrorChars.put(8832, 8833);
    mirrorChars.put(8833, 8832);
    mirrorChars.put(8834, 8835);
    mirrorChars.put(8835, 8834);
    mirrorChars.put(8836, 8837);
    mirrorChars.put(8837, 8836);
    mirrorChars.put(8838, 8839);
    mirrorChars.put(8839, 8838);
    mirrorChars.put(8840, 8841);
    mirrorChars.put(8841, 8840);
    mirrorChars.put(8842, 8843);
    mirrorChars.put(8843, 8842);
    mirrorChars.put(8847, 8848);
    mirrorChars.put(8848, 8847);
    mirrorChars.put(8849, 8850);
    mirrorChars.put(8850, 8849);
    mirrorChars.put(8856, 10680);
    mirrorChars.put(8866, 8867);
    mirrorChars.put(8867, 8866);
    mirrorChars.put(8870, 10974);
    mirrorChars.put(8872, 10980);
    mirrorChars.put(8873, 10979);
    mirrorChars.put(8875, 10981);
    mirrorChars.put(8880, 8881);
    mirrorChars.put(8881, 8880);
    mirrorChars.put(8882, 8883);
    mirrorChars.put(8883, 8882);
    mirrorChars.put(8884, 8885);
    mirrorChars.put(8885, 8884);
    mirrorChars.put(8886, 8887);
    mirrorChars.put(8887, 8886);
    mirrorChars.put(8905, 8906);
    mirrorChars.put(8906, 8905);
    mirrorChars.put(8907, 8908);
    mirrorChars.put(8908, 8907);
    mirrorChars.put(8909, 8771);
    mirrorChars.put(8912, 8913);
    mirrorChars.put(8913, 8912);
    mirrorChars.put(8918, 8919);
    mirrorChars.put(8919, 8918);
    mirrorChars.put(8920, 8921);
    mirrorChars.put(8921, 8920);
    mirrorChars.put(8922, 8923);
    mirrorChars.put(8923, 8922);
    mirrorChars.put(8924, 8925);
    mirrorChars.put(8925, 8924);
    mirrorChars.put(8926, 8927);
    mirrorChars.put(8927, 8926);
    mirrorChars.put(8928, 8929);
    mirrorChars.put(8929, 8928);
    mirrorChars.put(8930, 8931);
    mirrorChars.put(8931, 8930);
    mirrorChars.put(8932, 8933);
    mirrorChars.put(8933, 8932);
    mirrorChars.put(8934, 8935);
    mirrorChars.put(8935, 8934);
    mirrorChars.put(8936, 8937);
    mirrorChars.put(8937, 8936);
    mirrorChars.put(8938, 8939);
    mirrorChars.put(8939, 8938);
    mirrorChars.put(8940, 8941);
    mirrorChars.put(8941, 8940);
    mirrorChars.put(8944, 8945);
    mirrorChars.put(8945, 8944);
    mirrorChars.put(8946, 8954);
    mirrorChars.put(8947, 8955);
    mirrorChars.put(8948, 8956);
    mirrorChars.put(8950, 8957);
    mirrorChars.put(8951, 8958);
    mirrorChars.put(8954, 8946);
    mirrorChars.put(8955, 8947);
    mirrorChars.put(8956, 8948);
    mirrorChars.put(8957, 8950);
    mirrorChars.put(8958, 8951);
    mirrorChars.put(8968, 8969);
    mirrorChars.put(8969, 8968);
    mirrorChars.put(8970, 8971);
    mirrorChars.put(8971, 8970);
    mirrorChars.put(9001, 9002);
    mirrorChars.put(9002, 9001);
    mirrorChars.put(10088, 10089);
    mirrorChars.put(10089, 10088);
    mirrorChars.put(10090, 10091);
    mirrorChars.put(10091, 10090);
    mirrorChars.put(10092, 10093);
    mirrorChars.put(10093, 10092);
    mirrorChars.put(10094, 10095);
    mirrorChars.put(10095, 10094);
    mirrorChars.put(10096, 10097);
    mirrorChars.put(10097, 10096);
    mirrorChars.put(10098, 10099);
    mirrorChars.put(10099, 10098);
    mirrorChars.put(10100, 10101);
    mirrorChars.put(10101, 10100);
    mirrorChars.put(10197, 10198);
    mirrorChars.put(10198, 10197);
    mirrorChars.put(10205, 10206);
    mirrorChars.put(10206, 10205);
    mirrorChars.put(10210, 10211);
    mirrorChars.put(10211, 10210);
    mirrorChars.put(10212, 10213);
    mirrorChars.put(10213, 10212);
    mirrorChars.put(10214, 10215);
    mirrorChars.put(10215, 10214);
    mirrorChars.put(10216, 10217);
    mirrorChars.put(10217, 10216);
    mirrorChars.put(10218, 10219);
    mirrorChars.put(10219, 10218);
    mirrorChars.put(10627, 10628);
    mirrorChars.put(10628, 10627);
    mirrorChars.put(10629, 10630);
    mirrorChars.put(10630, 10629);
    mirrorChars.put(10631, 10632);
    mirrorChars.put(10632, 10631);
    mirrorChars.put(10633, 10634);
    mirrorChars.put(10634, 10633);
    mirrorChars.put(10635, 10636);
    mirrorChars.put(10636, 10635);
    mirrorChars.put(10637, 10640);
    mirrorChars.put(10638, 10639);
    mirrorChars.put(10639, 10638);
    mirrorChars.put(10640, 10637);
    mirrorChars.put(10641, 10642);
    mirrorChars.put(10642, 10641);
    mirrorChars.put(10643, 10644);
    mirrorChars.put(10644, 10643);
    mirrorChars.put(10645, 10646);
    mirrorChars.put(10646, 10645);
    mirrorChars.put(10647, 10648);
    mirrorChars.put(10648, 10647);
    mirrorChars.put(10680, 8856);
    mirrorChars.put(10688, 10689);
    mirrorChars.put(10689, 10688);
    mirrorChars.put(10692, 10693);
    mirrorChars.put(10693, 10692);
    mirrorChars.put(10703, 10704);
    mirrorChars.put(10704, 10703);
    mirrorChars.put(10705, 10706);
    mirrorChars.put(10706, 10705);
    mirrorChars.put(10708, 10709);
    mirrorChars.put(10709, 10708);
    mirrorChars.put(10712, 10713);
    mirrorChars.put(10713, 10712);
    mirrorChars.put(10714, 10715);
    mirrorChars.put(10715, 10714);
    mirrorChars.put(10741, 8725);
    mirrorChars.put(10744, 10745);
    mirrorChars.put(10745, 10744);
    mirrorChars.put(10748, 10749);
    mirrorChars.put(10749, 10748);
    mirrorChars.put(10795, 10796);
    mirrorChars.put(10796, 10795);
    mirrorChars.put(10797, 10796);
    mirrorChars.put(10798, 10797);
    mirrorChars.put(10804, 10805);
    mirrorChars.put(10805, 10804);
    mirrorChars.put(10812, 10813);
    mirrorChars.put(10813, 10812);
    mirrorChars.put(10852, 10853);
    mirrorChars.put(10853, 10852);
    mirrorChars.put(10873, 10874);
    mirrorChars.put(10874, 10873);
    mirrorChars.put(10877, 10878);
    mirrorChars.put(10878, 10877);
    mirrorChars.put(10879, 10880);
    mirrorChars.put(10880, 10879);
    mirrorChars.put(10881, 10882);
    mirrorChars.put(10882, 10881);
    mirrorChars.put(10883, 10884);
    mirrorChars.put(10884, 10883);
    mirrorChars.put(10891, 10892);
    mirrorChars.put(10892, 10891);
    mirrorChars.put(10897, 10898);
    mirrorChars.put(10898, 10897);
    mirrorChars.put(10899, 10900);
    mirrorChars.put(10900, 10899);
    mirrorChars.put(10901, 10902);
    mirrorChars.put(10902, 10901);
    mirrorChars.put(10903, 10904);
    mirrorChars.put(10904, 10903);
    mirrorChars.put(10905, 10906);
    mirrorChars.put(10906, 10905);
    mirrorChars.put(10907, 10908);
    mirrorChars.put(10908, 10907);
    mirrorChars.put(10913, 10914);
    mirrorChars.put(10914, 10913);
    mirrorChars.put(10918, 10919);
    mirrorChars.put(10919, 10918);
    mirrorChars.put(10920, 10921);
    mirrorChars.put(10921, 10920);
    mirrorChars.put(10922, 10923);
    mirrorChars.put(10923, 10922);
    mirrorChars.put(10924, 10925);
    mirrorChars.put(10925, 10924);
    mirrorChars.put(10927, 10928);
    mirrorChars.put(10928, 10927);
    mirrorChars.put(10931, 10932);
    mirrorChars.put(10932, 10931);
    mirrorChars.put(10939, 10940);
    mirrorChars.put(10940, 10939);
    mirrorChars.put(10941, 10942);
    mirrorChars.put(10942, 10941);
    mirrorChars.put(10943, 10944);
    mirrorChars.put(10944, 10943);
    mirrorChars.put(10945, 10946);
    mirrorChars.put(10946, 10945);
    mirrorChars.put(10947, 10948);
    mirrorChars.put(10948, 10947);
    mirrorChars.put(10949, 10950);
    mirrorChars.put(10950, 10949);
    mirrorChars.put(10957, 10958);
    mirrorChars.put(10958, 10957);
    mirrorChars.put(10959, 10960);
    mirrorChars.put(10960, 10959);
    mirrorChars.put(10961, 10962);
    mirrorChars.put(10962, 10961);
    mirrorChars.put(10963, 10964);
    mirrorChars.put(10964, 10963);
    mirrorChars.put(10965, 10966);
    mirrorChars.put(10966, 10965);
    mirrorChars.put(10974, 8870);
    mirrorChars.put(10979, 8873);
    mirrorChars.put(10980, 8872);
    mirrorChars.put(10981, 8875);
    mirrorChars.put(10988, 10989);
    mirrorChars.put(10989, 10988);
    mirrorChars.put(10999, 11000);
    mirrorChars.put(11000, 10999);
    mirrorChars.put(11001, 11002);
    mirrorChars.put(11002, 11001);
    mirrorChars.put(12296, 12297);
    mirrorChars.put(12297, 12296);
    mirrorChars.put(12298, 12299);
    mirrorChars.put(12299, 12298);
    mirrorChars.put(12300, 12301);
    mirrorChars.put(12301, 12300);
    mirrorChars.put(12302, 12303);
    mirrorChars.put(12303, 12302);
    mirrorChars.put(12304, 12305);
    mirrorChars.put(12305, 12304);
    mirrorChars.put(12308, 12309);
    mirrorChars.put(12309, 12308);
    mirrorChars.put(12310, 12311);
    mirrorChars.put(12311, 12310);
    mirrorChars.put(12312, 12313);
    mirrorChars.put(12313, 12312);
    mirrorChars.put(12314, 12315);
    mirrorChars.put(12315, 12314);
    mirrorChars.put(65288, 65289);
    mirrorChars.put(65289, 65288);
    mirrorChars.put(65308, 65310);
    mirrorChars.put(65310, 65308);
    mirrorChars.put(65339, 65341);
    mirrorChars.put(65341, 65339);
    mirrorChars.put(65371, 65373);
    mirrorChars.put(65373, 65371);
    mirrorChars.put(65375, 65376);
    mirrorChars.put(65376, 65375);
    mirrorChars.put(65378, 65379);
    mirrorChars.put(65379, 65378);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BidiLine
 * JD-Core Version:    0.6.0
 */