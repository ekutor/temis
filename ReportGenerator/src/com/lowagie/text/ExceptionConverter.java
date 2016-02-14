package com.lowagie.text;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ExceptionConverter extends RuntimeException
{
  private static final long serialVersionUID = 8657630363395849399L;
  private Exception ex;
  private String prefix;

  public ExceptionConverter(Exception paramException)
  {
    this.ex = paramException;
    this.prefix = ((paramException instanceof RuntimeException) ? "" : "ExceptionConverter: ");
  }

  public static final RuntimeException convertException(Exception paramException)
  {
    if ((paramException instanceof RuntimeException))
      return (RuntimeException)paramException;
    return new ExceptionConverter(paramException);
  }

  public Exception getException()
  {
    return this.ex;
  }

  public String getMessage()
  {
    return this.ex.getMessage();
  }

  public String getLocalizedMessage()
  {
    return this.ex.getLocalizedMessage();
  }

  public String toString()
  {
    return this.prefix + this.ex;
  }

  public void printStackTrace()
  {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream paramPrintStream)
  {
    synchronized (paramPrintStream)
    {
      paramPrintStream.print(this.prefix);
      this.ex.printStackTrace(paramPrintStream);
    }
  }

  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    synchronized (paramPrintWriter)
    {
      paramPrintWriter.print(this.prefix);
      this.ex.printStackTrace(paramPrintWriter);
    }
  }

  public Throwable fillInStackTrace()
  {
    return this;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ExceptionConverter
 * JD-Core Version:    0.6.0
 */