package com.lowagie.text.pdf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntHashtable
  implements Cloneable
{
  private transient Entry[] table;
  private transient int count;
  private int threshold;
  private float loadFactor;

  public IntHashtable()
  {
    this(150, 0.75F);
  }

  public IntHashtable(int paramInt)
  {
    this(paramInt, 0.75F);
  }

  public IntHashtable(int paramInt, float paramFloat)
  {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    if (paramFloat <= 0.0F)
      throw new IllegalArgumentException("Illegal Load: " + paramFloat);
    if (paramInt == 0)
      paramInt = 1;
    this.loadFactor = paramFloat;
    this.table = new Entry[paramInt];
    this.threshold = (int)(paramInt * paramFloat);
  }

  public int size()
  {
    return this.count;
  }

  public boolean isEmpty()
  {
    return this.count == 0;
  }

  public boolean contains(int paramInt)
  {
    Entry[] arrayOfEntry = this.table;
    int i = arrayOfEntry.length;
    while (i-- > 0)
      for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next)
        if (localEntry.value == paramInt)
          return true;
    return false;
  }

  public boolean containsValue(int paramInt)
  {
    return contains(paramInt);
  }

  public boolean containsKey(int paramInt)
  {
    Entry[] arrayOfEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next)
      if ((localEntry.hash == i) && (localEntry.key == paramInt))
        return true;
    return false;
  }

  public int get(int paramInt)
  {
    Entry[] arrayOfEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next)
      if ((localEntry.hash == i) && (localEntry.key == paramInt))
        return localEntry.value;
    return 0;
  }

  protected void rehash()
  {
    int i = this.table.length;
    Entry[] arrayOfEntry1 = this.table;
    int j = i * 2 + 1;
    Entry[] arrayOfEntry2 = new Entry[j];
    this.threshold = (int)(j * this.loadFactor);
    this.table = arrayOfEntry2;
    int k = i;
    while (k-- > 0)
    {
      Entry localEntry1 = arrayOfEntry1[k];
      while (localEntry1 != null)
      {
        Entry localEntry2 = localEntry1;
        localEntry1 = localEntry1.next;
        int m = (localEntry2.hash & 0x7FFFFFFF) % j;
        localEntry2.next = arrayOfEntry2[m];
        arrayOfEntry2[m] = localEntry2;
      }
    }
  }

  public int put(int paramInt1, int paramInt2)
  {
    Entry[] arrayOfEntry = this.table;
    int i = paramInt1;
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next)
    {
      if ((localEntry.hash != i) || (localEntry.key != paramInt1))
        continue;
      int k = localEntry.value;
      localEntry.value = paramInt2;
      return k;
    }
    if (this.count >= this.threshold)
    {
      rehash();
      arrayOfEntry = this.table;
      j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    }
    localEntry = new Entry(i, paramInt1, paramInt2, arrayOfEntry[j]);
    arrayOfEntry[j] = localEntry;
    this.count += 1;
    return 0;
  }

  public int remove(int paramInt)
  {
    Entry[] arrayOfEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry localEntry1 = arrayOfEntry[j];
    Entry localEntry2 = null;
    while (localEntry1 != null)
    {
      if ((localEntry1.hash == i) && (localEntry1.key == paramInt))
      {
        if (localEntry2 != null)
          localEntry2.next = localEntry1.next;
        else
          arrayOfEntry[j] = localEntry1.next;
        this.count -= 1;
        int k = localEntry1.value;
        localEntry1.value = 0;
        return k;
      }
      localEntry2 = localEntry1;
      localEntry1 = localEntry1.next;
    }
    return 0;
  }

  public void clear()
  {
    Entry[] arrayOfEntry = this.table;
    int i = arrayOfEntry.length;
    while (true)
    {
      i--;
      if (i < 0)
        break;
      arrayOfEntry[i] = null;
    }
    this.count = 0;
  }

  public Iterator getEntryIterator()
  {
    return new IntHashtableIterator(this.table);
  }

  public int[] toOrderedKeys()
  {
    int[] arrayOfInt = getKeys();
    Arrays.sort(arrayOfInt);
    return arrayOfInt;
  }

  public int[] getKeys()
  {
    int[] arrayOfInt = new int[this.count];
    int i = 0;
    int j = this.table.length;
    Entry localEntry1 = null;
    while (true)
      if ((localEntry1 != null) || (j-- <= 0) || ((localEntry1 = this.table[j]) != null))
      {
        if (localEntry1 == null)
          break;
        Entry localEntry2 = localEntry1;
        localEntry1 = localEntry2.next;
        arrayOfInt[(i++)] = localEntry2.key;
        continue;
      }
    return arrayOfInt;
  }

  public int getOneKey()
  {
    if (this.count == 0)
      return 0;
    int i = this.table.length;
    Entry localEntry = null;
    while ((i-- > 0) && ((localEntry = this.table[i]) == null));
    if (localEntry == null)
      return 0;
    return localEntry.key;
  }

  public Object clone()
  {
    try
    {
      IntHashtable localIntHashtable = (IntHashtable)super.clone();
      localIntHashtable.table = new Entry[this.table.length];
      int i = this.table.length;
      while (i-- > 0)
        localIntHashtable.table[i] = (this.table[i] != null ? (Entry)this.table[i].clone() : null);
      return localIntHashtable;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    throw new InternalError();
  }

  static class IntHashtableIterator
    implements Iterator
  {
    int index;
    IntHashtable.Entry[] table;
    IntHashtable.Entry entry;

    IntHashtableIterator(IntHashtable.Entry[] paramArrayOfEntry)
    {
      this.table = paramArrayOfEntry;
      this.index = paramArrayOfEntry.length;
    }

    public boolean hasNext()
    {
      if (this.entry != null)
        return true;
      while (this.index-- > 0)
        if ((this.entry = this.table[this.index]) != null)
          return true;
      return false;
    }

    public Object next()
    {
      while ((this.entry == null) && (this.index-- > 0) && ((this.entry = this.table[this.index]) == null));
      if (this.entry != null)
      {
        IntHashtable.Entry localEntry = this.entry;
        this.entry = localEntry.next;
        return localEntry;
      }
      throw new NoSuchElementException("IntHashtableIterator");
    }

    public void remove()
    {
      throw new UnsupportedOperationException("remove() not supported.");
    }
  }

  static class Entry
  {
    int hash;
    int key;
    int value;
    Entry next;

    protected Entry(int paramInt1, int paramInt2, int paramInt3, Entry paramEntry)
    {
      this.hash = paramInt1;
      this.key = paramInt2;
      this.value = paramInt3;
      this.next = paramEntry;
    }

    public int getKey()
    {
      return this.key;
    }

    public int getValue()
    {
      return this.value;
    }

    protected Object clone()
    {
      Entry localEntry = new Entry(this.hash, this.key, this.value, this.next != null ? (Entry)this.next.clone() : null);
      return localEntry;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.IntHashtable
 * JD-Core Version:    0.6.0
 */