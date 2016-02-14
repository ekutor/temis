package com.lowagie.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class Executable
{
  public static String acroread = null;

  private static Process action(String paramString1, String paramString2, boolean paramBoolean)
    throws IOException
  {
    Process localProcess = null;
    if (paramString2.trim().length() > 0)
      paramString2 = " " + paramString2.trim();
    else
      paramString2 = "";
    if (acroread != null)
      localProcess = Runtime.getRuntime().exec(acroread + paramString2 + " \"" + paramString1 + "\"");
    else if (isWindows())
    {
      if (isWindows9X())
        localProcess = Runtime.getRuntime().exec("command.com /C start acrord32" + paramString2 + " \"" + paramString1 + "\"");
      else
        localProcess = Runtime.getRuntime().exec("cmd /c start acrord32" + paramString2 + " \"" + paramString1 + "\"");
    }
    else if (isMac())
      if (paramString2.trim().length() == 0)
        localProcess = Runtime.getRuntime().exec(new String[] { "/usr/bin/open", paramString1 });
      else
        localProcess = Runtime.getRuntime().exec(new String[] { "/usr/bin/open", paramString2.trim(), paramString1 });
    try
    {
      if ((localProcess != null) && (paramBoolean))
        localProcess.waitFor();
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    return localProcess;
  }

  public static final Process openDocument(String paramString, boolean paramBoolean)
    throws IOException
  {
    return action(paramString, "", paramBoolean);
  }

  public static final Process openDocument(File paramFile, boolean paramBoolean)
    throws IOException
  {
    return openDocument(paramFile.getAbsolutePath(), paramBoolean);
  }

  public static final Process openDocument(String paramString)
    throws IOException
  {
    return openDocument(paramString, false);
  }

  public static final Process openDocument(File paramFile)
    throws IOException
  {
    return openDocument(paramFile, false);
  }

  public static final Process printDocument(String paramString, boolean paramBoolean)
    throws IOException
  {
    return action(paramString, "/p", paramBoolean);
  }

  public static final Process printDocument(File paramFile, boolean paramBoolean)
    throws IOException
  {
    return printDocument(paramFile.getAbsolutePath(), paramBoolean);
  }

  public static final Process printDocument(String paramString)
    throws IOException
  {
    return printDocument(paramString, false);
  }

  public static final Process printDocument(File paramFile)
    throws IOException
  {
    return printDocument(paramFile, false);
  }

  public static final Process printDocumentSilent(String paramString, boolean paramBoolean)
    throws IOException
  {
    return action(paramString, "/p /h", paramBoolean);
  }

  public static final Process printDocumentSilent(File paramFile, boolean paramBoolean)
    throws IOException
  {
    return printDocumentSilent(paramFile.getAbsolutePath(), paramBoolean);
  }

  public static final Process printDocumentSilent(String paramString)
    throws IOException
  {
    return printDocumentSilent(paramString, false);
  }

  public static final Process printDocumentSilent(File paramFile)
    throws IOException
  {
    return printDocumentSilent(paramFile, false);
  }

  public static final void launchBrowser(String paramString)
    throws IOException
  {
    try
    {
      Object localObject;
      Method localMethod;
      if (isMac())
      {
        localObject = Class.forName("com.apple.mrj.MRJFileUtils");
        localMethod = ((Class)localObject).getDeclaredMethod("openURL", new Class[] { String.class });
        localMethod.invoke(null, new Object[] { paramString });
      }
      else if (isWindows())
      {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + paramString);
      }
      else
      {
        localObject = new String[] { "firefox", "opera", "konqueror", "mozilla", "netscape" };
        localMethod = null;
        for (int i = 0; (i < localObject.length) && (localMethod == null); i++)
        {
          if (Runtime.getRuntime().exec(new String[] { "which", localObject[i] }).waitFor() != 0)
            continue;
          localMethod = localObject[i];
        }
        if (localMethod == null)
          throw new Exception("Could not find web browser.");
        Runtime.getRuntime().exec(new String[] { localMethod, paramString });
      }
    }
    catch (Exception localException)
    {
      throw new IOException("Error attempting to launch web browser");
    }
  }

  public static boolean isWindows()
  {
    String str = System.getProperty("os.name").toLowerCase();
    return (str.indexOf("windows") != -1) || (str.indexOf("nt") != -1);
  }

  public static boolean isWindows9X()
  {
    String str = System.getProperty("os.name").toLowerCase();
    return (str.equals("windows 95")) || (str.equals("windows 98"));
  }

  public static boolean isMac()
  {
    String str = System.getProperty("os.name").toLowerCase();
    return str.indexOf("mac") != -1;
  }

  public static boolean isLinux()
  {
    String str = System.getProperty("os.name").toLowerCase();
    return str.indexOf("linux") != -1;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.Executable
 * JD-Core Version:    0.6.0
 */