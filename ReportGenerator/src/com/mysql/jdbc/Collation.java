/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.util.Map;
/*      */ 
/*      */ class Collation
/*      */ {
/*      */   public int index;
/*      */   public String collationName;
/*      */   public String charsetName;
/*      */   public String javaCharsetName;
/*      */ 
/*      */   public Collation(int index, String collationName, String charsetName)
/*      */   {
/*  995 */     this.index = index;
/*  996 */     this.collationName = collationName;
/*  997 */     this.charsetName = charsetName;
/*  998 */     this.javaCharsetName = ((String)CharsetMapping.MYSQL_TO_JAVA_CHARSET_MAP.get(charsetName));
/*      */   }
/*      */ 
/*      */   public Collation(int index, String collationName, String charsetName, String javaCharsetName) {
/* 1002 */     this.index = index;
/* 1003 */     this.collationName = collationName;
/* 1004 */     this.charsetName = charsetName;
/* 1005 */     this.javaCharsetName = javaCharsetName;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1009 */     StringBuffer asString = new StringBuffer();
/* 1010 */     asString.append("[");
/* 1011 */     asString.append("index=");
/* 1012 */     asString.append(this.index);
/* 1013 */     asString.append(",collationName=");
/* 1014 */     asString.append(this.collationName);
/* 1015 */     asString.append(",charsetName=");
/* 1016 */     asString.append(this.charsetName);
/* 1017 */     asString.append(",javaCharsetName=");
/* 1018 */     asString.append(this.javaCharsetName);
/* 1019 */     asString.append("]");
/* 1020 */     return asString.toString();
/*      */   }
/*      */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.Collation
 * JD-Core Version:    0.6.0
 */