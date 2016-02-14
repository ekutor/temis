package com.lowagie.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class BuildTutorial
{
  static String root;
  static FileWriter build;

  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length == 4)
    {
      File localFile1 = new File(paramArrayOfString[0]);
      File localFile2 = new File(paramArrayOfString[1]);
      File localFile3 = new File(localFile1, paramArrayOfString[2]);
      File localFile4 = new File(localFile1, paramArrayOfString[3]);
      try
      {
        System.out.print("Building tutorial: ");
        root = new File(paramArrayOfString[1], localFile1.getName()).getCanonicalPath();
        System.out.println(root);
        build = new FileWriter(new File(root, "build.xml"));
        build.write("<project name=\"tutorial\" default=\"all\" basedir=\".\">\n");
        build.write("<target name=\"all\">\n");
        action(localFile1, localFile2, localFile3, localFile4);
        build.write("</target>\n</project>");
        build.flush();
        build.close();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
    else
    {
      System.err.println("Wrong number of parameters.\nUsage: BuildSite srcdr destdir xsl_examples xsl_site");
    }
  }

  public static void action(File paramFile1, File paramFile2, File paramFile3, File paramFile4)
    throws IOException
  {
    if (".svn".equals(paramFile1.getName()))
      return;
    System.out.print(paramFile1.getName());
    File localFile;
    Object localObject;
    if (paramFile1.isDirectory())
    {
      System.out.print(" ");
      System.out.println(paramFile1.getCanonicalPath());
      localFile = new File(paramFile2, paramFile1.getName());
      localFile.mkdir();
      File[] arrayOfFile = paramFile1.listFiles();
      if (arrayOfFile != null)
        for (int i = 0; i < arrayOfFile.length; i++)
        {
          localObject = arrayOfFile[i];
          action((File)localObject, localFile, paramFile3, paramFile4);
        }
      System.out.println("... skipped");
    }
    else if (paramFile1.getName().equals("index.xml"))
    {
      System.out.println("... transformed");
      convert(paramFile1, paramFile4, new File(paramFile2, "index.php"));
      localFile = new File(paramFile2, "build.xml");
      localObject = localFile.getCanonicalPath().substring(root.length());
      localObject = ((String)localObject).replace(File.separatorChar, '/');
      if ("/build.xml".equals(localObject))
        return;
      convert(paramFile1, paramFile3, localFile);
      build.write("\t<ant antfile=\"${basedir}");
      build.write((String)localObject);
      build.write("\" target=\"install\" inheritAll=\"false\" />\n");
    }
    else
    {
      System.out.println("... skipped");
    }
  }

  public static void convert(File paramFile1, File paramFile2, File paramFile3)
  {
    try
    {
      TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
      Templates localTemplates = localTransformerFactory.newTemplates(new StreamSource(new FileInputStream(paramFile2)));
      Transformer localTransformer = localTemplates.newTransformer();
      String str = paramFile3.getParentFile().getCanonicalPath().substring(root.length());
      str = str.replace(File.separatorChar, '/');
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < str.length(); i++)
      {
        if (str.charAt(i) != '/')
          continue;
        localStringBuffer.append("/..");
      }
      localTransformer.setParameter("branch", str);
      localTransformer.setParameter("root", localStringBuffer.toString());
      StreamSource localStreamSource = new StreamSource(new FileInputStream(paramFile1));
      StreamResult localStreamResult = new StreamResult(new FileOutputStream(paramFile3));
      localTransformer.transform(localStreamSource, localStreamResult);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.BuildTutorial
 * JD-Core Version:    0.6.0
 */