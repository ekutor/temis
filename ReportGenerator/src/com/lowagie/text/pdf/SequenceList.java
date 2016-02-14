package com.lowagie.text.pdf;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SequenceList
{
  protected static final int COMMA = 1;
  protected static final int MINUS = 2;
  protected static final int NOT = 3;
  protected static final int TEXT = 4;
  protected static final int NUMBER = 5;
  protected static final int END = 6;
  protected static final char EOT = '￿';
  private static final int FIRST = 0;
  private static final int DIGIT = 1;
  private static final int OTHER = 2;
  private static final int DIGIT2 = 3;
  private static final String NOT_OTHER = "-,!0123456789";
  protected char[] text;
  protected int ptr = 0;
  protected int number;
  protected String other;
  protected int low;
  protected int high;
  protected boolean odd;
  protected boolean even;
  protected boolean inverse;

  protected SequenceList(String paramString)
  {
    this.text = paramString.toCharArray();
  }

  protected char nextChar()
  {
    int i;
    do
    {
      if (this.ptr >= this.text.length)
        return 65535;
      i = this.text[(this.ptr++)];
    }
    while (i <= 32);
    return i;
  }

  protected void putBack()
  {
    this.ptr -= 1;
    if (this.ptr < 0)
      this.ptr = 0;
  }

  protected int getType()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (true)
    {
      int j = nextChar();
      if (j == 65535)
      {
        if (i == 1)
        {
          this.number = Integer.parseInt(this.other = localStringBuffer.toString());
          return 5;
        }
        if (i == 2)
        {
          this.other = localStringBuffer.toString().toLowerCase();
          return 4;
        }
        return 6;
      }
      switch (i)
      {
      case 0:
        switch (j)
        {
        case 33:
          return 3;
        case 45:
          return 2;
        case 44:
          return 1;
        }
        localStringBuffer.append(j);
        if ((j >= 48) && (j <= 57))
          i = 1;
        else
          i = 2;
        break;
      case 1:
        if ((j >= 48) && (j <= 57))
        {
          localStringBuffer.append(j);
        }
        else
        {
          putBack();
          this.number = Integer.parseInt(this.other = localStringBuffer.toString());
          return 5;
        }
      case 2:
        if ("-,!0123456789".indexOf(j) < 0)
        {
          localStringBuffer.append(j);
        }
        else
        {
          putBack();
          this.other = localStringBuffer.toString().toLowerCase();
          return 4;
        }
      }
    }
  }

  private void otherProc()
  {
    if ((this.other.equals("odd")) || (this.other.equals("o")))
    {
      this.odd = true;
      this.even = false;
    }
    else if ((this.other.equals("even")) || (this.other.equals("e")))
    {
      this.odd = false;
      this.even = true;
    }
  }

  protected boolean getAttributes()
  {
    this.low = -1;
    this.high = -1;
    this.odd = (this.even = this.inverse = 0);
    int i = 2;
    while (true)
    {
      int j = getType();
      if ((j == 6) || (j == 1))
      {
        if (i == 1)
          this.high = this.low;
        return j == 6;
      }
      switch (i)
      {
      case 2:
        switch (j)
        {
        case 3:
          this.inverse = true;
          break;
        case 2:
          i = 3;
          break;
        default:
          if (j == 5)
          {
            this.low = this.number;
            i = 1;
          }
          else
          {
            otherProc();
          }
        }
        break;
      case 1:
        switch (j)
        {
        case 3:
          this.inverse = true;
          i = 2;
          this.high = this.low;
          break;
        case 2:
          i = 3;
          break;
        default:
          this.high = this.low;
          i = 2;
          otherProc();
        }
        break;
      case 3:
        switch (j)
        {
        case 3:
          this.inverse = true;
          i = 2;
          break;
        case 2:
          break;
        case 5:
          this.high = this.number;
          i = 2;
          break;
        case 4:
        default:
          i = 2;
          otherProc();
        }
      }
    }
  }

  public static List expand(String paramString, int paramInt)
  {
    SequenceList localSequenceList = new SequenceList(paramString);
    LinkedList localLinkedList = new LinkedList();
    boolean bool = false;
    while (!bool)
    {
      bool = localSequenceList.getAttributes();
      if ((localSequenceList.low == -1) && (localSequenceList.high == -1) && (!localSequenceList.even) && (!localSequenceList.odd))
        continue;
      if (localSequenceList.low < 1)
        localSequenceList.low = 1;
      if ((localSequenceList.high < 1) || (localSequenceList.high > paramInt))
        localSequenceList.high = paramInt;
      if (localSequenceList.low > paramInt)
        localSequenceList.low = paramInt;
      int i = 1;
      if (localSequenceList.inverse)
      {
        if (localSequenceList.low > localSequenceList.high)
        {
          int j = localSequenceList.low;
          localSequenceList.low = localSequenceList.high;
          localSequenceList.high = j;
        }
        ListIterator localListIterator = localLinkedList.listIterator();
        while (localListIterator.hasNext())
        {
          int m = ((Integer)localListIterator.next()).intValue();
          if (((localSequenceList.even) && ((m & 0x1) == 1)) || ((localSequenceList.odd) && ((m & 0x1) == 0)) || (m < localSequenceList.low) || (m > localSequenceList.high))
            continue;
          localListIterator.remove();
        }
        continue;
      }
      if (localSequenceList.low > localSequenceList.high)
      {
        i = -1;
        if ((localSequenceList.odd) || (localSequenceList.even))
        {
          i--;
          if (localSequenceList.even)
            localSequenceList.low &= -2;
          else
            localSequenceList.low -= ((localSequenceList.low & 0x1) == 1 ? 0 : 1);
        }
        k = localSequenceList.low;
        while (k >= localSequenceList.high)
        {
          localLinkedList.add(new Integer(k));
          k += i;
        }
        continue;
      }
      if ((localSequenceList.odd) || (localSequenceList.even))
      {
        i++;
        if (localSequenceList.odd)
          localSequenceList.low |= 1;
        else
          localSequenceList.low += ((localSequenceList.low & 0x1) == 1 ? 1 : 0);
      }
      int k = localSequenceList.low;
      while (k <= localSequenceList.high)
      {
        localLinkedList.add(new Integer(k));
        k += i;
      }
    }
    return localLinkedList;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.SequenceList
 * JD-Core Version:    0.6.0
 */