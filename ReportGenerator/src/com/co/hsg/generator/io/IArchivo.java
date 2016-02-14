package com.co.hsg.generator.io;

public abstract interface IArchivo
{
  public abstract String leerPropiedad(String paramString);

  public abstract void guardarValor(String paramString1, String paramString2);

  public abstract String getRuta();
}
