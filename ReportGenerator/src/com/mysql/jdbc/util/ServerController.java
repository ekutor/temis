/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ServerController
/*     */ {
/*     */   public static final String BASEDIR_KEY = "basedir";
/*     */   public static final String DATADIR_KEY = "datadir";
/*     */   public static final String DEFAULTS_FILE_KEY = "defaults-file";
/*     */   public static final String EXECUTABLE_NAME_KEY = "executable";
/*     */   public static final String EXECUTABLE_PATH_KEY = "executablePath";
/*  79 */   private Process serverProcess = null;
/*     */ 
/*  84 */   private Properties serverProps = null;
/*     */ 
/*  89 */   private Properties systemProps = null;
/*     */ 
/*     */   public ServerController(String baseDir)
/*     */   {
/* 100 */     setBaseDir(baseDir);
/*     */   }
/*     */ 
/*     */   public ServerController(String basedir, String datadir)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setBaseDir(String baseDir)
/*     */   {
/* 122 */     getServerProps().setProperty("basedir", baseDir);
/*     */   }
/*     */ 
/*     */   public void setDataDir(String dataDir)
/*     */   {
/* 132 */     getServerProps().setProperty("datadir", dataDir);
/*     */   }
/*     */ 
/*     */   public Process start()
/*     */     throws IOException
/*     */   {
/* 145 */     if (this.serverProcess != null) {
/* 146 */       throw new IllegalArgumentException("Server already started");
/*     */     }
/* 148 */     this.serverProcess = Runtime.getRuntime().exec(getCommandLine());
/*     */ 
/* 150 */     return this.serverProcess;
/*     */   }
/*     */ 
/*     */   public void stop(boolean forceIfNecessary)
/*     */     throws IOException
/*     */   {
/* 163 */     if (this.serverProcess != null)
/*     */     {
/* 165 */       String basedir = getServerProps().getProperty("basedir");
/*     */ 
/* 167 */       StringBuffer pathBuf = new StringBuffer(basedir);
/*     */ 
/* 169 */       if (!basedir.endsWith(File.separator)) {
/* 170 */         pathBuf.append(File.separator);
/*     */       }
/*     */ 
/* 175 */       pathBuf.append("bin");
/* 176 */       pathBuf.append(File.separator);
/* 177 */       pathBuf.append("mysqladmin shutdown");
/*     */ 
/* 179 */       System.out.println(pathBuf.toString());
/*     */ 
/* 181 */       Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
/*     */ 
/* 183 */       int exitStatus = -1;
/*     */       try
/*     */       {
/* 186 */         exitStatus = mysqladmin.waitFor();
/*     */       }
/*     */       catch (InterruptedException ie)
/*     */       {
/*     */       }
/*     */ 
/* 195 */       if ((exitStatus != 0) && (forceIfNecessary))
/* 196 */         forceStop();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void forceStop()
/*     */   {
/* 205 */     if (this.serverProcess != null) {
/* 206 */       this.serverProcess.destroy();
/* 207 */       this.serverProcess = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Properties getServerProps()
/*     */   {
/* 218 */     if (this.serverProps == null) {
/* 219 */       this.serverProps = new Properties();
/*     */     }
/*     */ 
/* 222 */     return this.serverProps;
/*     */   }
/*     */ 
/*     */   private String getCommandLine()
/*     */   {
/* 232 */     StringBuffer commandLine = new StringBuffer(getFullExecutablePath());
/* 233 */     commandLine.append(buildOptionalCommandLine());
/*     */ 
/* 235 */     return commandLine.toString();
/*     */   }
/*     */ 
/*     */   private String getFullExecutablePath()
/*     */   {
/* 244 */     StringBuffer pathBuf = new StringBuffer();
/*     */ 
/* 246 */     String optionalExecutablePath = getServerProps().getProperty("executablePath");
/*     */ 
/* 249 */     if (optionalExecutablePath == null)
/*     */     {
/* 251 */       String basedir = getServerProps().getProperty("basedir");
/* 252 */       pathBuf.append(basedir);
/*     */ 
/* 254 */       if (!basedir.endsWith(File.separator)) {
/* 255 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */ 
/* 258 */       if (runningOnWindows())
/* 259 */         pathBuf.append("bin");
/*     */       else {
/* 261 */         pathBuf.append("libexec");
/*     */       }
/*     */ 
/* 264 */       pathBuf.append(File.separatorChar);
/*     */     } else {
/* 266 */       pathBuf.append(optionalExecutablePath);
/*     */ 
/* 268 */       if (!optionalExecutablePath.endsWith(File.separator)) {
/* 269 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */     }
/*     */ 
/* 273 */     String executableName = getServerProps().getProperty("executable", "mysqld");
/*     */ 
/* 276 */     pathBuf.append(executableName);
/*     */ 
/* 278 */     return pathBuf.toString();
/*     */   }
/*     */ 
/*     */   private String buildOptionalCommandLine()
/*     */   {
/* 288 */     StringBuffer commandLineBuf = new StringBuffer();
/*     */ 
/* 290 */     if (this.serverProps != null)
/*     */     {
/* 292 */       Iterator iter = this.serverProps.keySet().iterator();
/* 293 */       while (iter.hasNext()) {
/* 294 */         String key = (String)iter.next();
/* 295 */         String value = this.serverProps.getProperty(key);
/*     */ 
/* 297 */         if (!isNonCommandLineArgument(key)) {
/* 298 */           if ((value != null) && (value.length() > 0)) {
/* 299 */             commandLineBuf.append(" \"");
/* 300 */             commandLineBuf.append("--");
/* 301 */             commandLineBuf.append(key);
/* 302 */             commandLineBuf.append("=");
/* 303 */             commandLineBuf.append(value);
/* 304 */             commandLineBuf.append("\"");
/*     */           } else {
/* 306 */             commandLineBuf.append(" --");
/* 307 */             commandLineBuf.append(key);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 313 */     return commandLineBuf.toString();
/*     */   }
/*     */ 
/*     */   private boolean isNonCommandLineArgument(String propName)
/*     */   {
/* 322 */     return (propName.equals("executable")) || (propName.equals("executablePath"));
/*     */   }
/*     */ 
/*     */   private synchronized Properties getSystemProperties()
/*     */   {
/* 332 */     if (this.systemProps == null) {
/* 333 */       this.systemProps = System.getProperties();
/*     */     }
/*     */ 
/* 336 */     return this.systemProps;
/*     */   }
/*     */ 
/*     */   private boolean runningOnWindows()
/*     */   {
/* 345 */     return StringUtils.indexOfIgnoreCase(getSystemProperties().getProperty("os.name"), "WINDOWS") != -1;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.util.ServerController
 * JD-Core Version:    0.6.0
 */