/*     */ package com.mysql.jdbc.profiler;
/*     */ 
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class ProfilerEvent
/*     */ {
/*     */   public static final byte TYPE_WARN = 0;
/*     */   public static final byte TYPE_OBJECT_CREATION = 1;
/*     */   public static final byte TYPE_PREPARE = 2;
/*     */   public static final byte TYPE_QUERY = 3;
/*     */   public static final byte TYPE_EXECUTE = 4;
/*     */   public static final byte TYPE_FETCH = 5;
/*     */   public static final byte TYPE_SLOW_QUERY = 6;
/*     */   protected byte eventType;
/*     */   protected long connectionId;
/*     */   protected int statementId;
/*     */   protected int resultSetId;
/*     */   protected long eventCreationTime;
/*     */   protected long eventDuration;
/*     */   protected String durationUnits;
/*     */   protected int hostNameIndex;
/*     */   protected String hostName;
/*     */   protected int catalogIndex;
/*     */   protected String catalog;
/*     */   protected int eventCreationPointIndex;
/*     */   protected String eventCreationPointDesc;
/*     */   protected String message;
/*     */ 
/*     */   public ProfilerEvent(byte eventType, String hostName, String catalog, long connectionId, int statementId, int resultSetId, long eventCreationTime, long eventDuration, String durationUnits, String eventCreationPointDesc, String eventCreationPoint, String message)
/*     */   {
/* 177 */     this.eventType = eventType;
/* 178 */     this.connectionId = connectionId;
/* 179 */     this.statementId = statementId;
/* 180 */     this.resultSetId = resultSetId;
/* 181 */     this.eventCreationTime = eventCreationTime;
/* 182 */     this.eventDuration = eventDuration;
/* 183 */     this.durationUnits = durationUnits;
/* 184 */     this.eventCreationPointDesc = eventCreationPointDesc;
/* 185 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getEventCreationPointAsString()
/*     */   {
/* 194 */     return this.eventCreationPointDesc;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 203 */     StringBuffer buf = new StringBuffer(32);
/*     */ 
/* 205 */     switch (this.eventType) {
/*     */     case 4:
/* 207 */       buf.append("EXECUTE");
/* 208 */       break;
/*     */     case 5:
/* 211 */       buf.append("FETCH");
/* 212 */       break;
/*     */     case 1:
/* 215 */       buf.append("CONSTRUCT");
/* 216 */       break;
/*     */     case 2:
/* 219 */       buf.append("PREPARE");
/* 220 */       break;
/*     */     case 3:
/* 223 */       buf.append("QUERY");
/* 224 */       break;
/*     */     case 0:
/* 227 */       buf.append("WARN");
/* 228 */       break;
/*     */     case 6:
/* 230 */       buf.append("SLOW QUERY");
/* 231 */       break;
/*     */     default:
/* 233 */       buf.append("UNKNOWN");
/*     */     }
/*     */ 
/* 236 */     buf.append(" created: ");
/* 237 */     buf.append(new Date(this.eventCreationTime));
/* 238 */     buf.append(" duration: ");
/* 239 */     buf.append(this.eventDuration);
/* 240 */     buf.append(" connection: ");
/* 241 */     buf.append(this.connectionId);
/* 242 */     buf.append(" statement: ");
/* 243 */     buf.append(this.statementId);
/* 244 */     buf.append(" resultset: ");
/* 245 */     buf.append(this.resultSetId);
/*     */ 
/* 247 */     if (this.message != null) {
/* 248 */       buf.append(" message: ");
/* 249 */       buf.append(this.message);
/*     */     }
/*     */ 
/* 253 */     if (this.eventCreationPointDesc != null) {
/* 254 */       buf.append("\n\nEvent Created at:\n");
/* 255 */       buf.append(this.eventCreationPointDesc);
/*     */     }
/*     */ 
/* 258 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static ProfilerEvent unpack(byte[] buf)
/*     */     throws Exception
/*     */   {
/* 271 */     int pos = 0;
/*     */ 
/* 273 */     byte eventType = buf[(pos++)];
/* 274 */     long connectionId = readInt(buf, pos);
/* 275 */     pos += 8;
/* 276 */     int statementId = readInt(buf, pos);
/* 277 */     pos += 4;
/* 278 */     int resultSetId = readInt(buf, pos);
/* 279 */     pos += 4;
/* 280 */     long eventCreationTime = readLong(buf, pos);
/* 281 */     pos += 8;
/* 282 */     long eventDuration = readLong(buf, pos);
/* 283 */     pos += 4;
/*     */ 
/* 285 */     byte[] eventDurationUnits = readBytes(buf, pos);
/* 286 */     pos += 4;
/*     */ 
/* 288 */     if (eventDurationUnits != null) {
/* 289 */       pos += eventDurationUnits.length;
/*     */     }
/*     */ 
/* 292 */     readInt(buf, pos);
/* 293 */     pos += 4;
/* 294 */     byte[] eventCreationAsBytes = readBytes(buf, pos);
/* 295 */     pos += 4;
/*     */ 
/* 297 */     if (eventCreationAsBytes != null) {
/* 298 */       pos += eventCreationAsBytes.length;
/*     */     }
/*     */ 
/* 301 */     byte[] message = readBytes(buf, pos);
/* 302 */     pos += 4;
/*     */ 
/* 304 */     if (message != null) {
/* 305 */       pos += message.length;
/*     */     }
/*     */ 
/* 308 */     return new ProfilerEvent(eventType, "", "", connectionId, statementId, resultSetId, eventCreationTime, eventDuration, StringUtils.toString(eventDurationUnits, "ISO8859_1"), StringUtils.toString(eventCreationAsBytes, "ISO8859_1"), null, StringUtils.toString(message, "ISO8859_1"));
/*     */   }
/*     */ 
/*     */   public byte[] pack()
/*     */     throws Exception
/*     */   {
/* 324 */     int len = 29;
/*     */ 
/* 326 */     byte[] eventCreationAsBytes = null;
/*     */ 
/* 328 */     getEventCreationPointAsString();
/*     */ 
/* 330 */     if (this.eventCreationPointDesc != null) {
/* 331 */       eventCreationAsBytes = StringUtils.getBytes(this.eventCreationPointDesc, "ISO8859_1");
/*     */ 
/* 333 */       len += 4 + eventCreationAsBytes.length;
/*     */     } else {
/* 335 */       len += 4;
/*     */     }
/*     */ 
/* 338 */     byte[] messageAsBytes = null;
/*     */ 
/* 340 */     if (this.message != null) {
/* 341 */       messageAsBytes = StringUtils.getBytes(this.message, "ISO8859_1");
/* 342 */       len += 4 + messageAsBytes.length;
/*     */     } else {
/* 344 */       len += 4;
/*     */     }
/*     */ 
/* 347 */     byte[] durationUnitsAsBytes = null;
/*     */ 
/* 349 */     if (this.durationUnits != null) {
/* 350 */       durationUnitsAsBytes = StringUtils.getBytes(this.durationUnits, "ISO8859_1");
/* 351 */       len += 4 + durationUnitsAsBytes.length;
/*     */     } else {
/* 353 */       len += 4;
/* 354 */       durationUnitsAsBytes = StringUtils.getBytes("", "ISO8859_1");
/*     */     }
/*     */ 
/* 357 */     byte[] buf = new byte[len];
/*     */ 
/* 359 */     int pos = 0;
/*     */ 
/* 361 */     buf[(pos++)] = this.eventType;
/* 362 */     pos = writeLong(this.connectionId, buf, pos);
/* 363 */     pos = writeInt(this.statementId, buf, pos);
/* 364 */     pos = writeInt(this.resultSetId, buf, pos);
/* 365 */     pos = writeLong(this.eventCreationTime, buf, pos);
/* 366 */     pos = writeLong(this.eventDuration, buf, pos);
/* 367 */     pos = writeBytes(durationUnitsAsBytes, buf, pos);
/* 368 */     pos = writeInt(this.eventCreationPointIndex, buf, pos);
/*     */ 
/* 370 */     if (eventCreationAsBytes != null)
/* 371 */       pos = writeBytes(eventCreationAsBytes, buf, pos);
/*     */     else {
/* 373 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */ 
/* 376 */     if (messageAsBytes != null)
/* 377 */       pos = writeBytes(messageAsBytes, buf, pos);
/*     */     else {
/* 379 */       pos = writeInt(0, buf, pos);
/*     */     }
/*     */ 
/* 382 */     return buf;
/*     */   }
/*     */ 
/*     */   private static int writeInt(int i, byte[] buf, int pos)
/*     */   {
/* 387 */     buf[(pos++)] = (byte)(i & 0xFF);
/* 388 */     buf[(pos++)] = (byte)(i >>> 8);
/* 389 */     buf[(pos++)] = (byte)(i >>> 16);
/* 390 */     buf[(pos++)] = (byte)(i >>> 24);
/*     */ 
/* 392 */     return pos;
/*     */   }
/*     */ 
/*     */   private static int writeLong(long l, byte[] buf, int pos) {
/* 396 */     buf[(pos++)] = (byte)(int)(l & 0xFF);
/* 397 */     buf[(pos++)] = (byte)(int)(l >>> 8);
/* 398 */     buf[(pos++)] = (byte)(int)(l >>> 16);
/* 399 */     buf[(pos++)] = (byte)(int)(l >>> 24);
/* 400 */     buf[(pos++)] = (byte)(int)(l >>> 32);
/* 401 */     buf[(pos++)] = (byte)(int)(l >>> 40);
/* 402 */     buf[(pos++)] = (byte)(int)(l >>> 48);
/* 403 */     buf[(pos++)] = (byte)(int)(l >>> 56);
/*     */ 
/* 405 */     return pos;
/*     */   }
/*     */ 
/*     */   private static int writeBytes(byte[] msg, byte[] buf, int pos) {
/* 409 */     pos = writeInt(msg.length, buf, pos);
/*     */ 
/* 411 */     System.arraycopy(msg, 0, buf, pos, msg.length);
/*     */ 
/* 413 */     return pos + msg.length;
/*     */   }
/*     */ 
/*     */   private static int readInt(byte[] buf, int pos) {
/* 417 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   private static long readLong(byte[] buf, int pos)
/*     */   {
/* 423 */     return buf[(pos++)] & 0xFF | (buf[(pos++)] & 0xFF) << 8 | (buf[(pos++)] & 0xFF) << 16 | (buf[(pos++)] & 0xFF) << 24 | (buf[(pos++)] & 0xFF) << 32 | (buf[(pos++)] & 0xFF) << 40 | (buf[(pos++)] & 0xFF) << 48 | (buf[(pos++)] & 0xFF) << 56;
/*     */   }
/*     */ 
/*     */   private static byte[] readBytes(byte[] buf, int pos)
/*     */   {
/* 433 */     int length = readInt(buf, pos);
/*     */ 
/* 435 */     pos += 4;
/*     */ 
/* 437 */     byte[] msg = new byte[length];
/* 438 */     System.arraycopy(buf, pos, msg, 0, length);
/*     */ 
/* 440 */     return msg;
/*     */   }
/*     */ 
/*     */   public String getCatalog()
/*     */   {
/* 449 */     return this.catalog;
/*     */   }
/*     */ 
/*     */   public long getConnectionId()
/*     */   {
/* 458 */     return this.connectionId;
/*     */   }
/*     */ 
/*     */   public long getEventCreationTime()
/*     */   {
/* 468 */     return this.eventCreationTime;
/*     */   }
/*     */ 
/*     */   public long getEventDuration()
/*     */   {
/* 477 */     return this.eventDuration;
/*     */   }
/*     */ 
/*     */   public String getDurationUnits()
/*     */   {
/* 484 */     return this.durationUnits;
/*     */   }
/*     */ 
/*     */   public byte getEventType()
/*     */   {
/* 493 */     return this.eventType;
/*     */   }
/*     */ 
/*     */   public int getResultSetId()
/*     */   {
/* 502 */     return this.resultSetId;
/*     */   }
/*     */ 
/*     */   public int getStatementId()
/*     */   {
/* 511 */     return this.statementId;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 520 */     return this.message;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfilerEvent
 * JD-Core Version:    0.6.0
 */