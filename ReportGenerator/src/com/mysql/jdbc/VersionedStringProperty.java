/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ 
/*     */ class VersionedStringProperty
/*     */ {
/*     */   int majorVersion;
/*     */   int minorVersion;
/*     */   int subminorVersion;
/* 919 */   boolean preferredValue = false;
/*     */   String propertyInfo;
/*     */ 
/*     */   VersionedStringProperty(String property)
/*     */   {
/* 924 */     property = property.trim();
/*     */ 
/* 926 */     if (property.startsWith("*")) {
/* 927 */       property = property.substring(1);
/* 928 */       this.preferredValue = true;
/*     */     }
/*     */ 
/* 931 */     if (property.startsWith(">")) {
/* 932 */       property = property.substring(1);
/*     */ 
/* 934 */       int charPos = 0;
/*     */ 
/* 936 */       for (charPos = 0; charPos < property.length(); charPos++) {
/* 937 */         char c = property.charAt(charPos);
/*     */ 
/* 939 */         if ((!Character.isWhitespace(c)) && (!Character.isDigit(c)) && (c != '.'))
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */ 
/* 945 */       String versionInfo = property.substring(0, charPos);
/* 946 */       List versionParts = StringUtils.split(versionInfo, ".", true);
/*     */ 
/* 948 */       this.majorVersion = Integer.parseInt(((String)versionParts.get(0)).toString());
/*     */ 
/* 950 */       if (versionParts.size() > 1)
/* 951 */         this.minorVersion = Integer.parseInt(((String)versionParts.get(1)).toString());
/*     */       else {
/* 953 */         this.minorVersion = 0;
/*     */       }
/*     */ 
/* 956 */       if (versionParts.size() > 2) {
/* 957 */         this.subminorVersion = Integer.parseInt(((String)versionParts.get(2)).toString());
/*     */       }
/*     */       else {
/* 960 */         this.subminorVersion = 0;
/*     */       }
/*     */ 
/* 963 */       this.propertyInfo = property.substring(charPos);
/*     */     } else {
/* 965 */       this.majorVersion = (this.minorVersion = this.subminorVersion = 0);
/* 966 */       this.propertyInfo = property;
/*     */     }
/*     */   }
/*     */ 
/*     */   VersionedStringProperty(String property, int major, int minor, int subminor) {
/* 971 */     this.propertyInfo = property;
/* 972 */     this.majorVersion = major;
/* 973 */     this.minorVersion = minor;
/* 974 */     this.subminorVersion = subminor;
/*     */   }
/*     */ 
/*     */   boolean isOkayForVersion(Connection conn) throws SQLException {
/* 978 */     return conn.versionMeetsMinimum(this.majorVersion, this.minorVersion, this.subminorVersion);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 984 */     return this.propertyInfo;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.VersionedStringProperty
 * JD-Core Version:    0.6.0
 */