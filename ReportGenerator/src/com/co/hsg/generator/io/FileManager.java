/*    */ package com.co.hsg.generator.io;
/*    */ 
/*    */ import com.co.hsg.generator.log.LogInfo;
/*    */ import com.co.hsg.generator.util.Util;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class FileManager
/*    */   implements IArchivo
/*    */ {
/*    */   private Properties conf;
/*    */   private InfoDB bean;
/* 19 */   private static String FILE = "config/config.properties";
/*    */   private static FileManager instance;
/*    */ 
/*    */   private FileManager()
/*    */   {
/* 23 */     this(FILE);
/*    */   }
/*    */ 
/*    */   private FileManager(String rutaArchivo) {
/* 27 */     FILE = rutaArchivo;
/* 28 */     this.bean = new InfoDB();
/* 29 */     this.conf = new Properties();
/*    */     try {
/* 31 */       this.conf.load(new FileInputStream(Util.getFile(rutaArchivo)));
/*    */     } catch (Exception e) {
/* 33 */       LogInfo.E("No Existe el Archivo " + FILE, e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static FileManager getInst()
/*    */   {
/* 39 */     if (instance == null) {
/* 40 */       instance = new FileManager();
/*    */     }
/* 42 */     return instance;
/*    */   }
/*    */ 
/*    */   public InfoDB chargeServerProps()
/*    */   {
/* 49 */     this.bean.setHost(this.conf.getProperty("host"));
/* 50 */     this.bean.setUser(this.conf.getProperty("user"));
/* 51 */     this.bean.setPassw(this.conf.getProperty("passw"));
/* 52 */     this.bean.setPort(this.conf.getProperty("port"));
/* 53 */     this.bean.setDb(this.conf.getProperty("db"));
/* 54 */     this.bean.setAdds(this.conf.getProperty("adds"));
/* 55 */     return this.bean;
/*    */   }
/*    */ 
/*    */   public String leerPropiedad(String parametro)
/*    */   {
/* 60 */     return this.conf.getProperty(parametro);
/*    */   }
/*    */ 
/*    */   public void guardarValor(String llave, String valor)
/*    */   {
/* 65 */     if (this.conf != null) {
/* 66 */       this.conf.setProperty(llave, valor);
/*    */ 
/* 68 */       File f = Util.getFile(FILE);
/* 69 */       FileOutputStream fos = null;
/* 70 */       PrintWriter pw = null;
/*    */       try {
/* 72 */         fos = new FileOutputStream(f);
/* 73 */         pw = new PrintWriter(fos);
/*    */ 
/* 75 */         this.conf.store(pw, "Modificado ");
/*    */       }
/*    */       catch (FileNotFoundException e) {
/* 78 */         LogInfo.E("No se encuentra el Archivo de configuracion Ruta: " + f.getAbsolutePath(), 
/* 79 */           e);
/*    */       }
/*    */       catch (IOException e)
/*    */       {
/* 83 */         LogInfo.E("No se puede guardar informacion en el archivo : " + f.getAbsolutePath(), 
/* 84 */           e);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getRuta()
/*    */   {
/* 93 */     return Util.getFile(FILE).getAbsolutePath();
/*    */   }
/*    */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.io.FileManager
 * JD-Core Version:    0.6.0
 */