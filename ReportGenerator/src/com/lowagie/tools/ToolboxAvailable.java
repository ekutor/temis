package com.lowagie.tools;

import com.lowagie.text.Document;
import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

public class ToolboxAvailable
{
  public static void main(String[] paramArrayOfString)
  {
    if (GraphicsEnvironment.isHeadless())
      System.out.println(Document.getVersion() + " Toolbox error: headless display");
    else
      try
      {
        Class localClass = Class.forName("com.lowagie.toolbox.Toolbox");
        Method localMethod = localClass.getMethod("main", new Class[] { paramArrayOfString.getClass() });
        localMethod.invoke(null, new Object[] { paramArrayOfString });
      }
      catch (Exception localException)
      {
        JOptionPane.showMessageDialog(null, "You need the iText-toolbox.jar with class com.lowagie.toolbox.Toolbox to use the iText Toolbox.", Document.getVersion() + " Toolbox error", 0);
      }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.tools.ToolboxAvailable
 * JD-Core Version:    0.6.0
 */