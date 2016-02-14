/*     */ package com.keypoint;
/*     */ 
/*     */ import java.awt.Image;
/*     */ import java.awt.image.PixelGrabber;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ 
/*     */ public class PngEncoder
/*     */ {
/*     */   public static final boolean ENCODE_ALPHA = true;
/*     */   public static final boolean NO_ALPHA = false;
/*     */   public static final int FILTER_NONE = 0;
/*     */   public static final int FILTER_SUB = 1;
/*     */   public static final int FILTER_UP = 2;
/*     */   public static final int FILTER_LAST = 2;
/*  77 */   protected static final byte[] IHDR = { 73, 72, 68, 82 };
/*     */ 
/*  80 */   protected static final byte[] IDAT = { 73, 68, 65, 84 };
/*     */ 
/*  83 */   protected static final byte[] IEND = { 73, 69, 78, 68 };
/*     */ 
/*  86 */   protected static final byte[] PHYS = { 112, 72, 89, 115 };
/*     */   protected byte[] pngBytes;
/*     */   protected byte[] priorRow;
/*     */   protected byte[] leftBytes;
/*     */   protected Image image;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected int bytePos;
/*     */   protected int maxPos;
/* 114 */   protected CRC32 crc = new CRC32();
/*     */   protected long crcValue;
/*     */   protected boolean encodeAlpha;
/*     */   protected int filter;
/*     */   protected int bytesPerPixel;
/* 129 */   private int xDpi = 0;
/*     */ 
/* 132 */   private int yDpi = 0;
/*     */ 
/* 135 */   private static float INCH_IN_METER_UNIT = 0.0254F;
/*     */   protected int compressionLevel;
/*     */ 
/*     */   public PngEncoder()
/*     */   {
/* 147 */     this(null, false, 0, 0);
/*     */   }
/*     */ 
/*     */   public PngEncoder(Image image)
/*     */   {
/* 158 */     this(image, false, 0, 0);
/*     */   }
/*     */ 
/*     */   public PngEncoder(Image image, boolean encodeAlpha)
/*     */   {
/* 170 */     this(image, encodeAlpha, 0, 0);
/*     */   }
/*     */ 
/*     */   public PngEncoder(Image image, boolean encodeAlpha, int whichFilter)
/*     */   {
/* 183 */     this(image, encodeAlpha, whichFilter, 0);
/*     */   }
/*     */ 
/*     */   public PngEncoder(Image image, boolean encodeAlpha, int whichFilter, int compLevel)
/*     */   {
/* 200 */     this.image = image;
/* 201 */     this.encodeAlpha = encodeAlpha;
/* 202 */     setFilter(whichFilter);
/* 203 */     if ((compLevel >= 0) && (compLevel <= 9))
/* 204 */       this.compressionLevel = compLevel;
/*     */   }
/*     */ 
/*     */   public void setImage(Image image)
/*     */   {
/* 216 */     this.image = image;
/* 217 */     this.pngBytes = null;
/*     */   }
/*     */ 
/*     */   public Image getImage()
/*     */   {
/* 226 */     return this.image;
/*     */   }
/*     */ 
/*     */   public byte[] pngEncode(boolean encodeAlpha)
/*     */   {
/* 237 */     byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
/*     */ 
/* 239 */     if (this.image == null) {
/* 240 */       return null;
/*     */     }
/* 242 */     this.width = this.image.getWidth(null);
/* 243 */     this.height = this.image.getHeight(null);
/*     */ 
/* 249 */     this.pngBytes = new byte[(this.width + 1) * this.height * 3 + 200];
/*     */ 
/* 254 */     this.maxPos = 0;
/*     */ 
/* 256 */     this.bytePos = writeBytes(pngIdBytes, 0);
/*     */ 
/* 258 */     writeHeader();
/* 259 */     writeResolution();
/*     */ 
/* 261 */     if (writeImageData()) {
/* 262 */       writeEnd();
/* 263 */       this.pngBytes = resizeByteArray(this.pngBytes, this.maxPos);
/*     */     }
/*     */     else {
/* 266 */       this.pngBytes = null;
/*     */     }
/* 268 */     return this.pngBytes;
/*     */   }
/*     */ 
/*     */   public byte[] pngEncode()
/*     */   {
/* 278 */     return pngEncode(this.encodeAlpha);
/*     */   }
/*     */ 
/*     */   public void setEncodeAlpha(boolean encodeAlpha)
/*     */   {
/* 287 */     this.encodeAlpha = encodeAlpha;
/*     */   }
/*     */ 
/*     */   public boolean getEncodeAlpha()
/*     */   {
/* 296 */     return this.encodeAlpha;
/*     */   }
/*     */ 
/*     */   public void setFilter(int whichFilter)
/*     */   {
/* 305 */     this.filter = 0;
/* 306 */     if (whichFilter <= 2)
/* 307 */       this.filter = whichFilter;
/*     */   }
/*     */ 
/*     */   public int getFilter()
/*     */   {
/* 317 */     return this.filter;
/*     */   }
/*     */ 
/*     */   public void setCompressionLevel(int level)
/*     */   {
/* 327 */     if ((level >= 0) && (level <= 9))
/* 328 */       this.compressionLevel = level;
/*     */   }
/*     */ 
/*     */   public int getCompressionLevel()
/*     */   {
/* 338 */     return this.compressionLevel;
/*     */   }
/*     */ 
/*     */   protected byte[] resizeByteArray(byte[] array, int newLength)
/*     */   {
/* 350 */     byte[] newArray = new byte[newLength];
/* 351 */     int oldLength = array.length;
/*     */ 
/* 353 */     System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
/* 354 */     return newArray;
/*     */   }
/*     */ 
/*     */   protected int writeBytes(byte[] data, int offset)
/*     */   {
/* 369 */     this.maxPos = Math.max(this.maxPos, offset + data.length);
/* 370 */     if (data.length + offset > this.pngBytes.length) {
/* 371 */       this.pngBytes = resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, data.length));
/*     */     }
/*     */ 
/* 374 */     System.arraycopy(data, 0, this.pngBytes, offset, data.length);
/* 375 */     return offset + data.length;
/*     */   }
/*     */ 
/*     */   protected int writeBytes(byte[] data, int nBytes, int offset)
/*     */   {
/* 391 */     this.maxPos = Math.max(this.maxPos, offset + nBytes);
/* 392 */     if (nBytes + offset > this.pngBytes.length) {
/* 393 */       this.pngBytes = resizeByteArray(this.pngBytes, this.pngBytes.length + Math.max(1000, nBytes));
/*     */     }
/*     */ 
/* 396 */     System.arraycopy(data, 0, this.pngBytes, offset, nBytes);
/* 397 */     return offset + nBytes;
/*     */   }
/*     */ 
/*     */   protected int writeInt2(int n, int offset)
/*     */   {
/* 408 */     byte[] temp = { (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF) };
/* 409 */     return writeBytes(temp, offset);
/*     */   }
/*     */ 
/*     */   protected int writeInt4(int n, int offset)
/*     */   {
/* 420 */     byte[] temp = { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF) };
/*     */ 
/* 424 */     return writeBytes(temp, offset);
/*     */   }
/*     */ 
/*     */   protected int writeByte(int b, int offset)
/*     */   {
/* 435 */     byte[] temp = { (byte)b };
/* 436 */     return writeBytes(temp, offset);
/*     */   }
/*     */ 
/*     */   protected void writeHeader()
/*     */   {
/* 444 */     int startPos = this.bytePos = writeInt4(13, this.bytePos);
/* 445 */     this.bytePos = writeBytes(IHDR, this.bytePos);
/* 446 */     this.width = this.image.getWidth(null);
/* 447 */     this.height = this.image.getHeight(null);
/* 448 */     this.bytePos = writeInt4(this.width, this.bytePos);
/* 449 */     this.bytePos = writeInt4(this.height, this.bytePos);
/* 450 */     this.bytePos = writeByte(8, this.bytePos);
/* 451 */     this.bytePos = writeByte(this.encodeAlpha ? 6 : 2, this.bytePos);
/*     */ 
/* 453 */     this.bytePos = writeByte(0, this.bytePos);
/* 454 */     this.bytePos = writeByte(0, this.bytePos);
/* 455 */     this.bytePos = writeByte(0, this.bytePos);
/* 456 */     this.crc.reset();
/* 457 */     this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
/* 458 */     this.crcValue = this.crc.getValue();
/* 459 */     this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
/*     */   }
/*     */ 
/*     */   protected void filterSub(byte[] pixels, int startPos, int width)
/*     */   {
/* 473 */     int offset = this.bytesPerPixel;
/* 474 */     int actualStart = startPos + offset;
/* 475 */     int nBytes = width * this.bytesPerPixel;
/* 476 */     int leftInsert = offset;
/* 477 */     int leftExtract = 0;
/*     */ 
/* 479 */     for (int i = actualStart; i < startPos + nBytes; i++) {
/* 480 */       this.leftBytes[leftInsert] = pixels[i];
/* 481 */       pixels[i] = (byte)((pixels[i] - this.leftBytes[leftExtract]) % 256);
/*     */ 
/* 483 */       leftInsert = (leftInsert + 1) % 15;
/* 484 */       leftExtract = (leftExtract + 1) % 15;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void filterUp(byte[] pixels, int startPos, int width)
/*     */   {
/* 498 */     int nBytes = width * this.bytesPerPixel;
/*     */ 
/* 500 */     for (int i = 0; i < nBytes; i++) {
/* 501 */       byte currentByte = pixels[(startPos + i)];
/* 502 */       pixels[(startPos + i)] = (byte)((pixels[(startPos + i)] - this.priorRow[i]) % 256);
/*     */ 
/* 504 */       this.priorRow[i] = currentByte;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean writeImageData()
/*     */   {
/* 518 */     int rowsLeft = this.height;
/* 519 */     int startRow = 0;
/*     */ 
/* 534 */     this.bytesPerPixel = (this.encodeAlpha ? 4 : 3);
/*     */ 
/* 536 */     Deflater scrunch = new Deflater(this.compressionLevel);
/* 537 */     ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);
/*     */ 
/* 539 */     DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes, scrunch);
/*     */     try
/*     */     {
/* 542 */       while (rowsLeft > 0) {
/* 543 */         int nRows = Math.min(32767 / (this.width * (this.bytesPerPixel + 1)), rowsLeft);
/*     */ 
/* 545 */         nRows = Math.max(nRows, 1);
/*     */ 
/* 547 */         int[] pixels = new int[this.width * nRows];
/*     */ 
/* 549 */         PixelGrabber pg = new PixelGrabber(this.image, 0, startRow, this.width, nRows, pixels, 0, this.width);
/*     */         try
/*     */         {
/* 552 */           pg.grabPixels();
/*     */         }
/*     */         catch (Exception e) {
/* 555 */           System.err.println("interrupted waiting for pixels!");
/* 556 */           return false;
/*     */         }
/* 558 */         if ((pg.getStatus() & 0x80) != 0) {
/* 559 */           System.err.println("image fetch aborted or errored");
/* 560 */           return false;
/*     */         }
/*     */ 
/* 567 */         byte[] scanLines = new byte[this.width * nRows * this.bytesPerPixel + nRows];
/*     */ 
/* 570 */         if (this.filter == 1) {
/* 571 */           this.leftBytes = new byte[16];
/*     */         }
/* 573 */         if (this.filter == 2) {
/* 574 */           this.priorRow = new byte[this.width * this.bytesPerPixel];
/*     */         }
/*     */ 
/* 577 */         int scanPos = 0;
/* 578 */         int startPos = 1;
/* 579 */         for (int i = 0; i < this.width * nRows; i++) {
/* 580 */           if (i % this.width == 0) {
/* 581 */             scanLines[(scanPos++)] = (byte)this.filter;
/* 582 */             startPos = scanPos;
/*     */           }
/* 584 */           scanLines[(scanPos++)] = (byte)(pixels[i] >> 16 & 0xFF);
/* 585 */           scanLines[(scanPos++)] = (byte)(pixels[i] >> 8 & 0xFF);
/* 586 */           scanLines[(scanPos++)] = (byte)(pixels[i] & 0xFF);
/* 587 */           if (this.encodeAlpha) {
/* 588 */             scanLines[(scanPos++)] = (byte)(pixels[i] >> 24 & 0xFF);
/*     */           }
/*     */ 
/* 591 */           if ((i % this.width != this.width - 1) || (this.filter == 0))
/*     */             continue;
/* 593 */           if (this.filter == 1) {
/* 594 */             filterSub(scanLines, startPos, this.width);
/*     */           }
/* 596 */           if (this.filter == 2) {
/* 597 */             filterUp(scanLines, startPos, this.width);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 605 */         compBytes.write(scanLines, 0, scanPos);
/*     */ 
/* 607 */         startRow += nRows;
/* 608 */         rowsLeft -= nRows;
/*     */       }
/* 610 */       compBytes.close();
/*     */ 
/* 615 */       byte[] compressedLines = outBytes.toByteArray();
/* 616 */       int nCompressed = compressedLines.length;
/*     */ 
/* 618 */       this.crc.reset();
/* 619 */       this.bytePos = writeInt4(nCompressed, this.bytePos);
/* 620 */       this.bytePos = writeBytes(IDAT, this.bytePos);
/* 621 */       this.crc.update(IDAT);
/* 622 */       this.bytePos = writeBytes(compressedLines, nCompressed, this.bytePos);
/*     */ 
/* 624 */       this.crc.update(compressedLines, 0, nCompressed);
/*     */ 
/* 626 */       this.crcValue = this.crc.getValue();
/* 627 */       this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
/* 628 */       scrunch.finish();
/* 629 */       scrunch.end();
/* 630 */       return true;
/*     */     }
/*     */     catch (IOException e) {
/* 633 */       System.err.println(e.toString());
/* 634 */     }return false;
/*     */   }
/*     */ 
/*     */   protected void writeEnd()
/*     */   {
/* 642 */     this.bytePos = writeInt4(0, this.bytePos);
/* 643 */     this.bytePos = writeBytes(IEND, this.bytePos);
/* 644 */     this.crc.reset();
/* 645 */     this.crc.update(IEND);
/* 646 */     this.crcValue = this.crc.getValue();
/* 647 */     this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
/*     */   }
/*     */ 
/*     */   public void setXDpi(int xDpi)
/*     */   {
/* 657 */     this.xDpi = Math.round(xDpi / INCH_IN_METER_UNIT);
/*     */   }
/*     */ 
/*     */   public int getXDpi()
/*     */   {
/* 667 */     return Math.round(this.xDpi * INCH_IN_METER_UNIT);
/*     */   }
/*     */ 
/*     */   public void setYDpi(int yDpi)
/*     */   {
/* 676 */     this.yDpi = Math.round(yDpi / INCH_IN_METER_UNIT);
/*     */   }
/*     */ 
/*     */   public int getYDpi()
/*     */   {
/* 685 */     return Math.round(this.yDpi * INCH_IN_METER_UNIT);
/*     */   }
/*     */ 
/*     */   public void setDpi(int xDpi, int yDpi)
/*     */   {
/* 695 */     this.xDpi = Math.round(xDpi / INCH_IN_METER_UNIT);
/* 696 */     this.yDpi = Math.round(yDpi / INCH_IN_METER_UNIT);
/*     */   }
/*     */ 
/*     */   protected void writeResolution()
/*     */   {
/* 703 */     if ((this.xDpi > 0) && (this.yDpi > 0))
/*     */     {
/* 705 */       int startPos = this.bytePos = writeInt4(9, this.bytePos);
/* 706 */       this.bytePos = writeBytes(PHYS, this.bytePos);
/* 707 */       this.bytePos = writeInt4(this.xDpi, this.bytePos);
/* 708 */       this.bytePos = writeInt4(this.yDpi, this.bytePos);
/* 709 */       this.bytePos = writeByte(1, this.bytePos);
/*     */ 
/* 711 */       this.crc.reset();
/* 712 */       this.crc.update(this.pngBytes, startPos, this.bytePos - startPos);
/* 713 */       this.crcValue = this.crc.getValue();
/* 714 */       this.bytePos = writeInt4((int)this.crcValue, this.bytePos);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.keypoint.PngEncoder
 * JD-Core Version:    0.6.0
 */