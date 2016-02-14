 package com.co.hsg.generator.log;
 
 import com.co.hsg.generator.util.Util;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.net.URISyntaxException;
 import java.util.MissingResourceException;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import org.apache.log4j.LogManager;
 import org.apache.log4j.Logger;
 import org.apache.log4j.spi.LoggerRepository;
 import org.apache.log4j.xml.DOMConfigurator;
 import org.w3c.dom.Document;
 import org.xml.sax.SAXException;
 
 public class LogControl
 {
   private Logger logError;
   private Logger logDebug;
   private Logger logWarning;
   private Logger logTrace;
   private final String log4jFile = "config/log4j.xml";
   private static LogControl lgc;
 
   private LogControl()
   {
     try
     {
       cargarConfigLog4j();
       this.logError = Logger.getLogger("LoggerError");
       this.logDebug = Logger.getLogger("LoggerDebug");
       this.logWarning = Logger.getLogger("LoggerWarning");
       this.logTrace = Logger.getLogger("LoggerTrace");
     } catch (MissingResourceException e) {
       e.printStackTrace();
     } catch (SAXException e) {
       e.printStackTrace();
     } catch (IOException e) {
       System.out.println("No se encuentra el archivo de configuracion log4j");
       e.printStackTrace();
     } catch (ParserConfigurationException e) {
       e.printStackTrace();
     } catch (URISyntaxException e) {
       e.printStackTrace();
     }
   }
 
   public static LogControl getLog()
   {
     if (lgc == null) {
       lgc = new LogControl();
     }
     return lgc;
   }
 
   public void addTrace(String mensaje) {
     this.logTrace.trace(mensaje);
   }
 
   public void addDebug(String mensaje) {
     this.logDebug.debug(mensaje);
   }
 
   public void addInfo(String mensaje)
   {
   }
 
   public void addWarn(String mensaje) {
     this.logWarning.warn(mensaje);
   }
 
   public void addError(String mensaje) {
     this.logError.error(mensaje);
   }
 
   public void addFatal(String mensaje)
   {
   }
 
   public void cargarConfigLog4j()
     throws MissingResourceException, SAXException, IOException, ParserConfigurationException, URISyntaxException
   {
     File jarDir = Util.getParentDirPathClass();
 
     if ((jarDir != null) && (jarDir.isDirectory())) {
       File log4j = Util.getFile("config/log4j.xml");
 
       if (log4j != null) {
         LoggerRepository hierarchy = LogManager.getLoggerRepository();
 
         InputStream log4JConfig = new FileInputStream(log4j);
 
         Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(log4JConfig);
         DOMConfigurator domConf = new DOMConfigurator();
         domConf.doConfigure(doc.getDocumentElement(), hierarchy);
       }
     }
   }
 }
