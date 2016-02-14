/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ConnectionImpl;
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ public class MysqlXAConnection extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private ConnectionImpl underlyingConnection;
/*     */   private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
/*     */   private Log log;
/*     */   protected boolean logXaCommands;
/*     */   private static final Constructor<?> JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
/*     */ 
/*     */   protected static MysqlXAConnection getInstance(ConnectionImpl mysqlConnection, boolean logXaCommands)
/*     */     throws SQLException
/*     */   {
/* 112 */     if (!Util.isJdbc4()) {
/* 113 */       return new MysqlXAConnection(mysqlConnection, logXaCommands);
/*     */     }
/*     */ 
/* 116 */     return (MysqlXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlConnection, Boolean.valueOf(logXaCommands) }, mysqlConnection.getExceptionInterceptor());
/*     */   }
/*     */ 
/*     */   public MysqlXAConnection(ConnectionImpl connection, boolean logXaCommands)
/*     */     throws SQLException
/*     */   {
/* 127 */     super(connection);
/* 128 */     this.underlyingConnection = connection;
/* 129 */     this.log = connection.getLog();
/* 130 */     this.logXaCommands = logXaCommands;
/*     */   }
/*     */ 
/*     */   public XAResource getXAResource()
/*     */     throws SQLException
/*     */   {
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   public int getTransactionTimeout()
/*     */     throws XAException
/*     */   {
/* 160 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean setTransactionTimeout(int arg0)
/*     */     throws XAException
/*     */   {
/* 185 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isSameRM(XAResource xares)
/*     */     throws XAException
/*     */   {
/* 205 */     if ((xares instanceof MysqlXAConnection)) {
/* 206 */       return this.underlyingConnection.isSameResource(((MysqlXAConnection)xares).underlyingConnection);
/*     */     }
/*     */ 
/* 210 */     return false;
/*     */   }
/*     */ 
/*     */   public Xid[] recover(int flag)
/*     */     throws XAException
/*     */   {
/* 251 */     return recover(this.underlyingConnection, flag);
/*     */   }
/*     */ 
/*     */   protected static Xid[] recover(Connection c, int flag)
/*     */     throws XAException
/*     */   {
/* 275 */     boolean startRscan = (flag & 0x1000000) > 0;
/* 276 */     boolean endRscan = (flag & 0x800000) > 0;
/*     */ 
/* 278 */     if ((!startRscan) && (!endRscan) && (flag != 0)) {
/* 279 */       throw new MysqlXAException(-5, "Invalid flag, must use TMNOFLAGS, or any combination of TMSTARTRSCAN and TMENDRSCAN", null);
/*     */     }
/*     */ 
/* 292 */     if (!startRscan) {
/* 293 */       return new Xid[0];
/*     */     }
/*     */ 
/* 296 */     ResultSet rs = null;
/* 297 */     Statement stmt = null;
/*     */ 
/* 299 */     List recoveredXidList = new ArrayList();
/*     */     try
/*     */     {
/* 303 */       stmt = c.createStatement();
/*     */ 
/* 305 */       rs = stmt.executeQuery("XA RECOVER");
/*     */ 
/* 307 */       while (rs.next()) {
/* 308 */         int formatId = rs.getInt(1);
/* 309 */         int gtridLength = rs.getInt(2);
/* 310 */         int bqualLength = rs.getInt(3);
/* 311 */         byte[] gtridAndBqual = rs.getBytes(4);
/*     */ 
/* 313 */         byte[] gtrid = new byte[gtridLength];
/* 314 */         byte[] bqual = new byte[bqualLength];
/*     */ 
/* 316 */         if (gtridAndBqual.length != gtridLength + bqualLength) {
/* 317 */           throw new MysqlXAException(105, "Error while recovering XIDs from RM. GTRID and BQUAL are wrong sizes", null);
/*     */         }
/*     */ 
/* 322 */         System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
/*     */ 
/* 324 */         System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
/*     */ 
/* 327 */         recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 331 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 333 */       if (rs != null) {
/*     */         try {
/* 335 */           rs.close();
/*     */         } catch (SQLException sqlEx) {
/* 337 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         }
/*     */       }
/*     */ 
/* 341 */       if (stmt != null) {
/*     */         try {
/* 343 */           stmt.close();
/*     */         } catch (SQLException sqlEx) {
/* 345 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 350 */     int numXids = recoveredXidList.size();
/*     */ 
/* 352 */     Xid[] asXids = new Xid[numXids];
/* 353 */     Object[] asObjects = recoveredXidList.toArray();
/*     */ 
/* 355 */     for (int i = 0; i < numXids; i++) {
/* 356 */       asXids[i] = ((Xid)asObjects[i]);
/*     */     }
/*     */ 
/* 359 */     return asXids;
/*     */   }
/*     */ 
/*     */   public int prepare(Xid xid)
/*     */     throws XAException
/*     */   {
/* 381 */     StringBuffer commandBuf = new StringBuffer();
/* 382 */     commandBuf.append("XA PREPARE ");
/* 383 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 385 */     dispatchCommand(commandBuf.toString());
/*     */ 
/* 387 */     return 0;
/*     */   }
/*     */ 
/*     */   public void forget(Xid xid)
/*     */     throws XAException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void rollback(Xid xid)
/*     */     throws XAException
/*     */   {
/* 423 */     StringBuffer commandBuf = new StringBuffer();
/* 424 */     commandBuf.append("XA ROLLBACK ");
/* 425 */     commandBuf.append(xidToString(xid));
/*     */     try
/*     */     {
/* 428 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 430 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void end(Xid xid, int flags)
/*     */     throws XAException
/*     */   {
/* 462 */     StringBuffer commandBuf = new StringBuffer();
/* 463 */     commandBuf.append("XA END ");
/* 464 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 466 */     switch (flags) {
/*     */     case 67108864:
/* 468 */       break;
/*     */     case 33554432:
/* 470 */       commandBuf.append(" SUSPEND");
/* 471 */       break;
/*     */     case 536870912:
/* 473 */       break;
/*     */     default:
/* 475 */       throw new XAException(-5);
/*     */     }
/*     */ 
/* 478 */     dispatchCommand(commandBuf.toString());
/*     */   }
/*     */ 
/*     */   public void start(Xid xid, int flags)
/*     */     throws XAException
/*     */   {
/* 505 */     StringBuffer commandBuf = new StringBuffer();
/* 506 */     commandBuf.append("XA START ");
/* 507 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 509 */     switch (flags) {
/*     */     case 2097152:
/* 511 */       commandBuf.append(" JOIN");
/* 512 */       break;
/*     */     case 134217728:
/* 514 */       commandBuf.append(" RESUME");
/* 515 */       break;
/*     */     case 0:
/* 518 */       break;
/*     */     default:
/* 520 */       throw new XAException(-5);
/*     */     }
/*     */ 
/* 523 */     dispatchCommand(commandBuf.toString());
/*     */ 
/* 525 */     this.underlyingConnection.setInGlobalTx(true);
/*     */   }
/*     */ 
/*     */   public void commit(Xid xid, boolean onePhase)
/*     */     throws XAException
/*     */   {
/* 550 */     StringBuffer commandBuf = new StringBuffer();
/* 551 */     commandBuf.append("XA COMMIT ");
/* 552 */     commandBuf.append(xidToString(xid));
/*     */ 
/* 554 */     if (onePhase) {
/* 555 */       commandBuf.append(" ONE PHASE");
/*     */     }
/*     */     try
/*     */     {
/* 559 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 561 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private ResultSet dispatchCommand(String command) throws XAException {
/* 566 */     Statement stmt = null;
/*     */     try
/*     */     {
/* 569 */       if (this.logXaCommands) {
/* 570 */         this.log.logDebug("Executing XA statement: " + command);
/*     */       }
/*     */ 
/* 574 */       stmt = this.underlyingConnection.createStatement();
/*     */ 
/* 577 */       stmt.execute(command);
/*     */ 
/* 579 */       ResultSet rs = stmt.getResultSet();
/*     */ 
/* 581 */       localResultSet1 = rs;
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/*     */       ResultSet localResultSet1;
/* 583 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 585 */       if (stmt != null)
/*     */         try {
/* 587 */           stmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
/* 596 */     Integer xaCode = (Integer)MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Integer.valueOf(sqlEx.getErrorCode()));
/*     */ 
/* 599 */     if (xaCode != null) {
/* 600 */       return new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), null);
/*     */     }
/*     */ 
/* 604 */     return new MysqlXAException(sqlEx.getMessage(), null);
/*     */   }
/*     */ 
/*     */   private static String xidToString(Xid xid) {
/* 608 */     byte[] gtrid = xid.getGlobalTransactionId();
/*     */ 
/* 610 */     byte[] btrid = xid.getBranchQualifier();
/*     */ 
/* 612 */     int lengthAsString = 6;
/*     */ 
/* 614 */     if (gtrid != null) {
/* 615 */       lengthAsString += 2 * gtrid.length;
/*     */     }
/*     */ 
/* 618 */     if (btrid != null) {
/* 619 */       lengthAsString += 2 * btrid.length;
/*     */     }
/*     */ 
/* 622 */     String formatIdInHex = Integer.toHexString(xid.getFormatId());
/*     */ 
/* 624 */     lengthAsString += formatIdInHex.length();
/* 625 */     lengthAsString += 3;
/*     */ 
/* 627 */     StringBuffer asString = new StringBuffer(lengthAsString);
/*     */ 
/* 629 */     asString.append("0x");
/*     */ 
/* 631 */     if (gtrid != null) {
/* 632 */       for (int i = 0; i < gtrid.length; i++) {
/* 633 */         String asHex = Integer.toHexString(gtrid[i] & 0xFF);
/*     */ 
/* 635 */         if (asHex.length() == 1) {
/* 636 */           asString.append("0");
/*     */         }
/*     */ 
/* 639 */         asString.append(asHex);
/*     */       }
/*     */     }
/*     */ 
/* 643 */     asString.append(",");
/*     */ 
/* 645 */     if (btrid != null) {
/* 646 */       asString.append("0x");
/*     */ 
/* 648 */       for (int i = 0; i < btrid.length; i++) {
/* 649 */         String asHex = Integer.toHexString(btrid[i] & 0xFF);
/*     */ 
/* 651 */         if (asHex.length() == 1) {
/* 652 */           asString.append("0");
/*     */         }
/*     */ 
/* 655 */         asString.append(asHex);
/*     */       }
/*     */     }
/*     */ 
/* 659 */     asString.append(",0x");
/* 660 */     asString.append(formatIdInHex);
/*     */ 
/* 662 */     return asString.toString();
/*     */   }
/*     */ 
/*     */   public synchronized Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 671 */     Connection connToWrap = getConnection(false, true);
/*     */ 
/* 673 */     return connToWrap;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  77 */     HashMap temp = new HashMap();
/*     */ 
/*  79 */     temp.put(Integer.valueOf(1397), Integer.valueOf(-4));
/*  80 */     temp.put(Integer.valueOf(1398), Integer.valueOf(-5));
/*  81 */     temp.put(Integer.valueOf(1399), Integer.valueOf(-7));
/*  82 */     temp.put(Integer.valueOf(1400), Integer.valueOf(-9));
/*  83 */     temp.put(Integer.valueOf(1401), Integer.valueOf(-3));
/*  84 */     temp.put(Integer.valueOf(1402), Integer.valueOf(100));
/*     */ 
/*  86 */     MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
/*     */ 
/*  92 */     if (Util.isJdbc4())
/*     */       try {
/*  94 */         JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection").getConstructor(new Class[] { ConnectionImpl.class, Boolean.TYPE });
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*  99 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/* 101 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/* 103 */         throw new RuntimeException(e);
/*     */       }
/*     */     else
/* 106 */       JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlXAConnection
 * JD-Core Version:    0.6.0
 */