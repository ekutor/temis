/*    */ package com.co.hsg.generator.log;
/*    */ 
/*    */ import com.co.hsg.generator.util.Util;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.net.URISyntaxException;
/*    */ import java.util.MissingResourceException;
/*    */ import javax.xml.parsers.DocumentBuilder;
/*    */ import javax.xml.parsers.DocumentBuilderFactory;
/*    */ import javax.xml.parsers.ParserConfigurationException;
/*    */ import org.apache.log4j.LogManager;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.spi.LoggerRepository;
/*    */ import org.apache.log4j.xml.DOMConfigurator;
/*    */ import org.w3c.dom.Document;
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class LogControl
/*    */ {
/*    */   private Logger logError;
/*    */   private Logger logDebug;
/*    */   private Logger logWarning;
/*    */   private Logger logTrace;
/* 27 */   private final String log4jFile = "config/log4j.xml";
/*    */   private static LogControl lgc;
/*    */ 
/*    */   private LogControl()
/*    */   {
/*    */     try
/*    */     {
/* 33 */       cargarConfigLog4j();
/* 34 */       this.logError = Logger.getLogger("LoggerError");
/* 35 */       this.logDebug = Logger.getLogger("LoggerDebug");
/* 36 */       this.logWarning = Logger.getLogger("LoggerWarning");
/* 37 */       this.logTrace = Logger.getLogger("LoggerTrace");
/*    */     } catch (MissingResourceException e) {
/* 39 */       e.printStackTrace();
/*    */     } catch (SAXException e) {
/* 41 */       e.printStackTrace();
/*    */     } catch (IOException e) {
/* 43 */       System.out.println("No se encuentra el archivo de configuracion log4j");
/* 44 */       e.printStackTrace();
/*    */     } catch (ParserConfigurationException e) {
/* 46 */       e.printStackTrace();
/*    */     } catch (URISyntaxException e) {
/* 48 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static LogControl getLog()
/*    */   {
/* 54 */     if (lgc == null) {
/* 55 */       lgc = new LogControl();
/*    */     }
/* 57 */     return lgc;
/*    */   }
/*    */ 
/*    */   public void addTrace(String mensaje) {
/* 61 */     this.logTrace.trace(mensaje);
/*    */   }
/*    */ 
/*    */   public void addDebug(String mensaje) {
/* 65 */     this.logDebug.debug(mensaje);
/*    */   }
/*    */ 
/*    */   public void addInfo(String mensaje)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void addWarn(String mensaje) {
/* 73 */     this.logWarning.warn(mensaje);
/*    */   }
/*    */ 
/*    */   public void addError(String mensaje) {
/* 77 */     this.logError.error(mensaje);
/*    */   }
/*    */ 
/*    */   public void addFatal(String mensaje)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void cargarConfigLog4j()
/*    */     throws MissingResourceException, SAXException, IOException, ParserConfigurationException, URISyntaxException
/*    */   {
/* 96 */     File jarDir = Util.getParentDirPathClass();
/*    */ 
/* 98 */     if ((jarDir != null) && (jarDir.isDirectory())) {
/* 99 */       File log4j = Util.getFile("config/log4j.xml");
/*    */ 
/* 101 */       if (log4j != null) {
/* 102 */         LoggerRepository hierarchy = LogManager.getLoggerRepository();
/*    */ 
/* 104 */         InputStream log4JConfig = new FileInputStream(log4j);
/*    */ 
/* 106 */         Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(log4JConfig);
/* 107 */         DOMConfigurator domConf = new DOMConfigurator();
/* 108 */         domConf.doConfigure(doc.getDocumentElement(), hierarchy);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.log.LogControl
 * JD-Core Version:    0.6.0
 */