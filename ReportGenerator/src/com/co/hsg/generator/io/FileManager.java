 package com.co.hsg.generator.io;
 
 import com.co.hsg.generator.log.LogInfo;
 import com.co.hsg.generator.util.Util;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.Properties;
 
 public class FileManager
   implements IArchivo
 {
   private Properties conf;
   private InfoDB bean;
   private static String FILE = "config/config.properties";
   private static FileManager instance;
 
   private FileManager()
   {
     this(FILE);
   }
 
   private FileManager(String rutaArchivo) {
     FILE = rutaArchivo;
     this.bean = new InfoDB();
     this.conf = new Properties();
     try {
       this.conf.load(new FileInputStream(Util.getFile(rutaArchivo)));
     } catch (Exception e) {
       LogInfo.E("No Existe el Archivo " + FILE, e);
     }
   }
 
   public static FileManager getInst()
   {
     if (instance == null) {
       instance = new FileManager();
     }
     return instance;
   }
 
   public InfoDB chargeServerProps()
   {
     this.bean.setHost(this.conf.getProperty("host"));
     this.bean.setUser(this.conf.getProperty("user"));
     this.bean.setPassw(this.conf.getProperty("passw"));
     this.bean.setPort(this.conf.getProperty("port"));
     this.bean.setDb(this.conf.getProperty("db"));
     this.bean.setAdds(this.conf.getProperty("adds"));
     return this.bean;
   }
 
   public String leerPropiedad(String parametro)
   {
     return this.conf.getProperty(parametro);
   }
 
   public void guardarValor(String llave, String valor)
   {
     if (this.conf != null) {
       this.conf.setProperty(llave, valor);
 
       File f = Util.getFile(FILE);
       FileOutputStream fos = null;
       PrintWriter pw = null;
       try {
         fos = new FileOutputStream(f);
         pw = new PrintWriter(fos);
 
         this.conf.store(pw, "Modificado ");
       }
       catch (FileNotFoundException e) {
         LogInfo.E("No se encuentra el Archivo de configuracion Ruta: " + f.getAbsolutePath(), 
           e);
       }
       catch (IOException e)
       {
         LogInfo.E("No se puede guardar informacion en el archivo : " + f.getAbsolutePath(), 
           e);
       }
     }
   }
 
   public String getRuta()
   {
     return Util.getFile(FILE).getAbsolutePath();
   }
 }