package com.lowagie.text.pdf.hyphenation;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Stack;

public class TernaryTree
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = 5313366505322983510L;
  protected char[] lo;
  protected char[] hi;
  protected char[] eq;
  protected char[] sc;
  protected CharVector kv;
  protected char root;
  protected char freenode;
  protected int length;
  protected static final int BLOCK_SIZE = 2048;

  TernaryTree()
  {
    init();
  }

  protected void init()
  {
    this.root = '\000';
    this.freenode = '\001';
    this.length = 0;
    this.lo = new char[2048];
    this.hi = new char[2048];
    this.eq = new char[2048];
    this.sc = new char[2048];
    this.kv = new CharVector();
  }

  public void insert(String paramString, char paramChar)
  {
    int i = paramString.length() + 1;
    if (this.freenode + i > this.eq.length)
      redimNodeArrays(this.eq.length + 2048);
    char[] arrayOfChar = new char[i--];
    paramString.getChars(0, i, arrayOfChar, 0);
    arrayOfChar[i] = '\000';
    this.root = insert(this.root, arrayOfChar, 0, paramChar);
  }

  public void insert(char[] paramArrayOfChar, int paramInt, char paramChar)
  {
    int i = strlen(paramArrayOfChar) + 1;
    if (this.freenode + i > this.eq.length)
      redimNodeArrays(this.eq.length + 2048);
    this.root = insert(this.root, paramArrayOfChar, paramInt, paramChar);
  }

  private char insert(char paramChar1, char[] paramArrayOfChar, int paramInt, char paramChar2)
  {
    int i = strlen(paramArrayOfChar, paramInt);
    if (paramChar1 == 0)
    {
      paramChar1 = this.freenode++;
      this.eq[paramChar1] = paramChar2;
      this.length += 1;
      this.hi[paramChar1] = '\000';
      if (i > 0)
      {
        this.sc[paramChar1] = 65535;
        this.lo[paramChar1] = (char)this.kv.alloc(i + 1);
        strcpy(this.kv.getArray(), this.lo[paramChar1], paramArrayOfChar, paramInt);
      }
      else
      {
        this.sc[paramChar1] = '\000';
        this.lo[paramChar1] = '\000';
      }
      return paramChar1;
    }
    if (this.sc[paramChar1] == 65535)
    {
      j = this.freenode++;
      this.lo[j] = this.lo[paramChar1];
      this.eq[j] = this.eq[paramChar1];
      this.lo[paramChar1] = '\000';
      if (i > 0)
      {
        this.sc[paramChar1] = this.kv.get(this.lo[j]);
        this.eq[paramChar1] = j;
        int tmp214_212 = j;
        char[] tmp214_209 = this.lo;
        tmp214_209[tmp214_212] = (char)(tmp214_209[tmp214_212] + '\001');
        if (this.kv.get(this.lo[j]) == 0)
        {
          this.lo[j] = '\000';
          this.sc[j] = '\000';
          this.hi[j] = '\000';
        }
        else
        {
          this.sc[j] = 65535;
        }
      }
      else
      {
        this.sc[j] = 65535;
        this.hi[paramChar1] = j;
        this.sc[paramChar1] = '\000';
        this.eq[paramChar1] = paramChar2;
        this.length += 1;
        return paramChar1;
      }
    }
    int j = paramArrayOfChar[paramInt];
    if (j < this.sc[paramChar1])
      this.lo[paramChar1] = insert(this.lo[paramChar1], paramArrayOfChar, paramInt, paramChar2);
    else if (j == this.sc[paramChar1])
    {
      if (j != 0)
        this.eq[paramChar1] = insert(this.eq[paramChar1], paramArrayOfChar, paramInt + 1, paramChar2);
      else
        this.eq[paramChar1] = paramChar2;
    }
    else
      this.hi[paramChar1] = insert(this.hi[paramChar1], paramArrayOfChar, paramInt, paramChar2);
    return paramChar1;
  }

  public static int strcmp(char[] paramArrayOfChar1, int paramInt1, char[] paramArrayOfChar2, int paramInt2)
  {
    while (paramArrayOfChar1[paramInt1] == paramArrayOfChar2[paramInt2])
    {
      if (paramArrayOfChar1[paramInt1] == 0)
        return 0;
      paramInt1++;
      paramInt2++;
    }
    return paramArrayOfChar1[paramInt1] - paramArrayOfChar2[paramInt2];
  }

  public static int strcmp(String paramString, char[] paramArrayOfChar, int paramInt)
  {
    int k = paramString.length();
    for (int i = 0; i < k; i++)
    {
      int j = paramString.charAt(i) - paramArrayOfChar[(paramInt + i)];
      if (j != 0)
        return j;
      if (paramArrayOfChar[(paramInt + i)] == 0)
        return j;
    }
    if (paramArrayOfChar[(paramInt + i)] != 0)
      return -paramArrayOfChar[(paramInt + i)];
    return 0;
  }

  public static void strcpy(char[] paramArrayOfChar1, int paramInt1, char[] paramArrayOfChar2, int paramInt2)
  {
    while (paramArrayOfChar2[paramInt2] != 0)
      paramArrayOfChar1[(paramInt1++)] = paramArrayOfChar2[(paramInt2++)];
    paramArrayOfChar1[paramInt1] = '\000';
  }

  public static int strlen(char[] paramArrayOfChar, int paramInt)
  {
    int i = 0;
    for (int j = paramInt; (j < paramArrayOfChar.length) && (paramArrayOfChar[j] != 0); j++)
      i++;
    return i;
  }

  public static int strlen(char[] paramArrayOfChar)
  {
    return strlen(paramArrayOfChar, 0);
  }

  public int find(String paramString)
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[i + 1];
    paramString.getChars(0, i, arrayOfChar, 0);
    arrayOfChar[i] = '\000';
    return find(arrayOfChar, 0);
  }

  public int find(char[] paramArrayOfChar, int paramInt)
  {
    int j = this.root;
    int k = paramInt;
    while (j != 0)
    {
      if (this.sc[j] == 65535)
      {
        if (strcmp(paramArrayOfChar, k, this.kv.getArray(), this.lo[j]) == 0)
          return this.eq[j];
        return -1;
      }
      int m = paramArrayOfChar[k];
      int i = m - this.sc[j];
      if (i == 0)
      {
        if (m == 0)
          return this.eq[j];
        k++;
        j = this.eq[j];
        continue;
      }
      if (i < 0)
      {
        j = this.lo[j];
        continue;
      }
      j = this.hi[j];
    }
    return -1;
  }

  public boolean knows(String paramString)
  {
    return find(paramString) >= 0;
  }

  private void redimNodeArrays(int paramInt)
  {
    int i = paramInt < this.lo.length ? paramInt : this.lo.length;
    char[] arrayOfChar = new char[paramInt];
    System.arraycopy(this.lo, 0, arrayOfChar, 0, i);
    this.lo = arrayOfChar;
    arrayOfChar = new char[paramInt];
    System.arraycopy(this.hi, 0, arrayOfChar, 0, i);
    this.hi = arrayOfChar;
    arrayOfChar = new char[paramInt];
    System.arraycopy(this.eq, 0, arrayOfChar, 0, i);
    this.eq = arrayOfChar;
    arrayOfChar = new char[paramInt];
    System.arraycopy(this.sc, 0, arrayOfChar, 0, i);
    this.sc = arrayOfChar;
  }

  public int size()
  {
    return this.length;
  }

  public Object clone()
  {
    TernaryTree localTernaryTree = new TernaryTree();
    localTernaryTree.lo = ((char[])this.lo.clone());
    localTernaryTree.hi = ((char[])this.hi.clone());
    localTernaryTree.eq = ((char[])this.eq.clone());
    localTernaryTree.sc = ((char[])this.sc.clone());
    localTernaryTree.kv = ((CharVector)this.kv.clone());
    localTernaryTree.root = this.root;
    localTernaryTree.freenode = this.freenode;
    localTernaryTree.length = this.length;
    return localTernaryTree;
  }

  protected void insertBalanced(String[] paramArrayOfString, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 < 1)
      return;
    int i = paramInt2 >> 1;
    insert(paramArrayOfString[(i + paramInt1)], paramArrayOfChar[(i + paramInt1)]);
    insertBalanced(paramArrayOfString, paramArrayOfChar, paramInt1, i);
    insertBalanced(paramArrayOfString, paramArrayOfChar, paramInt1 + i + 1, paramInt2 - i - 1);
  }

  public void balance()
  {
    int i = 0;
    int j = this.length;
    String[] arrayOfString = new String[j];
    char[] arrayOfChar = new char[j];
    Iterator localIterator = new Iterator();
    while (localIterator.hasMoreElements())
    {
      arrayOfChar[i] = localIterator.getValue();
      arrayOfString[(i++)] = ((String)localIterator.nextElement());
    }
    init();
    insertBalanced(arrayOfString, arrayOfChar, 0, j);
  }

  public void trimToSize()
  {
    balance();
    redimNodeArrays(this.freenode);
    CharVector localCharVector = new CharVector();
    localCharVector.alloc(1);
    TernaryTree localTernaryTree = new TernaryTree();
    compact(localCharVector, localTernaryTree, this.root);
    this.kv = localCharVector;
    this.kv.trimToSize();
  }

  private void compact(CharVector paramCharVector, TernaryTree paramTernaryTree, char paramChar)
  {
    if (paramChar == 0)
      return;
    if (this.sc[paramChar] == 65535)
    {
      int i = paramTernaryTree.find(this.kv.getArray(), this.lo[paramChar]);
      if (i < 0)
      {
        i = paramCharVector.alloc(strlen(this.kv.getArray(), this.lo[paramChar]) + 1);
        strcpy(paramCharVector.getArray(), i, this.kv.getArray(), this.lo[paramChar]);
        paramTernaryTree.insert(paramCharVector.getArray(), i, (char)i);
      }
      this.lo[paramChar] = (char)i;
    }
    else
    {
      compact(paramCharVector, paramTernaryTree, this.lo[paramChar]);
      if (this.sc[paramChar] != 0)
        compact(paramCharVector, paramTernaryTree, this.eq[paramChar]);
      compact(paramCharVector, paramTernaryTree, this.hi[paramChar]);
    }
  }

  public Enumeration keys()
  {
    return new Iterator();
  }

  public void printStats()
  {
    System.out.println("Number of keys = " + Integer.toString(this.length));
    System.out.println("Node count = " + Integer.toString(this.freenode));
    System.out.println("Key Array length = " + Integer.toString(this.kv.length()));
  }

  public class Iterator
    implements Enumeration
  {
    int cur = -1;
    String curkey;
    Stack ns = new Stack();
    StringBuffer ks = new StringBuffer();

    public Iterator()
    {
      rewind();
    }

    public void rewind()
    {
      this.ns.removeAllElements();
      this.ks.setLength(0);
      this.cur = TernaryTree.this.root;
      run();
    }

    public Object nextElement()
    {
      String str = this.curkey;
      this.cur = up();
      run();
      return str;
    }

    public char getValue()
    {
      if (this.cur >= 0)
        return TernaryTree.this.eq[this.cur];
      return '\000';
    }

    public boolean hasMoreElements()
    {
      return this.cur != -1;
    }

    private int up()
    {
      Item localItem = new Item();
      int i = 0;
      if (this.ns.empty())
        return -1;
      if ((this.cur != 0) && (TernaryTree.this.sc[this.cur] == 0))
        return TernaryTree.this.lo[this.cur];
      int j = 1;
      while (j != 0)
      {
        localItem = (Item)this.ns.pop();
        Item tmp76_75 = localItem;
        tmp76_75.child = (char)(tmp76_75.child + '\001');
        switch (localItem.child)
        {
        case '\001':
          if (TernaryTree.this.sc[localItem.parent] != 0)
          {
            i = TernaryTree.this.eq[localItem.parent];
            this.ns.push(localItem.clone());
            this.ks.append(TernaryTree.this.sc[localItem.parent]);
          }
          else
          {
            Item tmp180_179 = localItem;
            tmp180_179.child = (char)(tmp180_179.child + '\001');
            this.ns.push(localItem.clone());
            i = TernaryTree.this.hi[localItem.parent];
          }
          j = 0;
          break;
        case '\002':
          i = TernaryTree.this.hi[localItem.parent];
          this.ns.push(localItem.clone());
          if (this.ks.length() > 0)
            this.ks.setLength(this.ks.length() - 1);
          j = 0;
          break;
        default:
          if (this.ns.empty())
            return -1;
          j = 1;
        }
      }
      return i;
    }

    private int run()
    {
      if (this.cur == -1)
        return -1;
      int i = 0;
      while (true)
      {
        if (this.cur != 0)
          if (TernaryTree.this.sc[this.cur] == 65535)
          {
            i = 1;
          }
          else
          {
            this.ns.push(new Item((char)this.cur, '\000'));
            if (TernaryTree.this.sc[this.cur] == 0)
            {
              i = 1;
            }
            else
            {
              this.cur = TernaryTree.this.lo[this.cur];
              continue;
            }
          }
        if (i != 0)
          break;
        this.cur = up();
        if (this.cur == -1)
          return -1;
      }
      StringBuffer localStringBuffer = new StringBuffer(this.ks.toString());
      if (TernaryTree.this.sc[this.cur] == 65535)
      {
        int j = TernaryTree.this.lo[this.cur];
        while (TernaryTree.this.kv.get(j) != 0)
          localStringBuffer.append(TernaryTree.this.kv.get(j++));
      }
      this.curkey = localStringBuffer.toString();
      return 0;
    }

    private class Item
      implements Cloneable
    {
      char parent;
      char child;

      public Item()
      {
        this.parent = '\000';
        this.child = '\000';
      }

      public Item(char paramChar1, char arg3)
      {
        this.parent = paramChar1;
        char c;
        this.child = c;
      }

      public Object clone()
      {
        return new Item(TernaryTree.Iterator.this, this.parent, this.child);
      }
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.hyphenation.TernaryTree
 * JD-Core Version:    0.6.0
 */