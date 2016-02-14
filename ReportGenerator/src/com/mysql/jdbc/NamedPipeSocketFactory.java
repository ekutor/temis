/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class NamedPipeSocketFactory
/*     */   implements SocketFactory, SocketMetadata
/*     */ {
/*     */   public static final String NAMED_PIPE_PROP_NAME = "namedPipePath";
/*     */   private Socket namedPipeSocket;
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 189 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 196 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */   public Socket connect(String host, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 204 */     String namedPipePath = props.getProperty("namedPipePath");
/*     */ 
/* 206 */     if (namedPipePath == null)
/* 207 */       namedPipePath = "\\\\.\\pipe\\MySQL";
/* 208 */     else if (namedPipePath.length() == 0) {
/* 209 */       throw new SocketException(Messages.getString("NamedPipeSocketFactory.2") + "namedPipePath" + Messages.getString("NamedPipeSocketFactory.3"));
/*     */     }
/*     */ 
/* 215 */     this.namedPipeSocket = new NamedPipeSocket(namedPipePath);
/*     */ 
/* 217 */     return this.namedPipeSocket;
/*     */   }
/*     */ 
/*     */   public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException
/*     */   {
/* 222 */     return true;
/*     */   }
/*     */ 
/*     */   class RandomAccessFileOutputStream extends OutputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */ 
/*     */     RandomAccessFileOutputStream(RandomAccessFile file)
/*     */     {
/* 143 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 150 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */     public void write(byte[] b)
/*     */       throws IOException
/*     */     {
/* 157 */       this.raFile.write(b);
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 164 */       this.raFile.write(b, off, len);
/*     */     }
/*     */ 
/*     */     public void write(int b)
/*     */       throws IOException
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   class RandomAccessFileInputStream extends InputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */ 
/*     */     RandomAccessFileInputStream(RandomAccessFile file)
/*     */     {
/*  97 */       this.raFile = file;
/*     */     }
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 104 */       return -1;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 111 */       this.raFile.close();
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 118 */       return this.raFile.read();
/*     */     }
/*     */ 
/*     */     public int read(byte[] b)
/*     */       throws IOException
/*     */     {
/* 125 */       return this.raFile.read(b);
/*     */     }
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 132 */       return this.raFile.read(b, off, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   class NamedPipeSocket extends Socket
/*     */   {
/*  47 */     private boolean isClosed = false;
/*     */     private RandomAccessFile namedPipeFile;
/*     */ 
/*     */     NamedPipeSocket(String filePath)
/*     */       throws IOException
/*     */     {
/*  52 */       if ((filePath == null) || (filePath.length() == 0)) {
/*  53 */         throw new IOException(Messages.getString("NamedPipeSocketFactory.4"));
/*     */       }
/*     */ 
/*  57 */       this.namedPipeFile = new RandomAccessFile(filePath, "rw");
/*     */     }
/*     */ 
/*     */     public synchronized void close()
/*     */       throws IOException
/*     */     {
/*  64 */       this.namedPipeFile.close();
/*  65 */       this.isClosed = true;
/*     */     }
/*     */ 
/*     */     public InputStream getInputStream()
/*     */       throws IOException
/*     */     {
/*  72 */       return new NamedPipeSocketFactory.RandomAccessFileInputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
/*     */     }
/*     */ 
/*     */     public OutputStream getOutputStream()
/*     */       throws IOException
/*     */     {
/*  79 */       return new NamedPipeSocketFactory.RandomAccessFileOutputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
/*     */     }
/*     */ 
/*     */     public boolean isClosed()
/*     */     {
/*  86 */       return this.isClosed;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.NamedPipeSocketFactory
 * JD-Core Version:    0.6.0
 */