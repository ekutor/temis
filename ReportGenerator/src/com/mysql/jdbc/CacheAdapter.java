package com.mysql.jdbc;

import java.util.Set;

public abstract interface CacheAdapter<K, V>
{
  public abstract V get(K paramK);

  public abstract void put(K paramK, V paramV);

  public abstract void invalidate(K paramK);

  public abstract void invalidateAll(Set<K> paramSet);

  public abstract void invalidateAll();
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.CacheAdapter
 * JD-Core Version:    0.6.0
 */