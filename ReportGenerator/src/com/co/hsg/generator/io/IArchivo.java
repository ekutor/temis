package com.co.hsg.generator.io;

public abstract interface IArchivo
{
  public abstract String leerPropiedad(String paramString);

  public abstract void guardarValor(String paramString1, String paramString2);

  public abstract String getRuta();
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.io.IArchivo
 * JD-Core Version:    0.6.0
 */