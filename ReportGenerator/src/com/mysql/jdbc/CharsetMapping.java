/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class CharsetMapping
/*     */ {
/*  50 */   private static final Properties CHARSET_CONFIG = new Properties();
/*     */   public static final String[] INDEX_TO_CHARSET;
/*     */   public static final String[] INDEX_TO_COLLATION;
/*     */   public static final int MAP_SIZE = 255;
/*     */   public static final Map<Integer, String> STATIC_INDEX_TO_MYSQL_CHARSET_MAP;
/*     */   public static final Map<String, Integer> STATIC_CHARSET_TO_NUM_BYTES_MAP;
/*     */   public static final Map<String, Integer> STATIC_4_0_CHARSET_TO_NUM_BYTES_MAP;
/*     */   private static final Map<String, List<VersionedStringProperty>> JAVA_TO_MYSQL_CHARSET_MAP;
/*     */   private static final Map<String, List<VersionedStringProperty>> JAVA_UC_TO_MYSQL_CHARSET_MAP;
/*     */   private static final Map<String, String> ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP;
/*     */   private static final Map<String, String> MULTIBYTE_CHARSETS;
/*     */   public static final Map<String, String> MYSQL_TO_JAVA_CHARSET_MAP;
/*     */   private static final Map<String, Integer> MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP;
/*     */   private static final String MYSQL_CHARSET_NAME_armscii8 = "armscii8";
/*     */   private static final String MYSQL_CHARSET_NAME_ascii = "ascii";
/*     */   private static final String MYSQL_CHARSET_NAME_big5 = "big5";
/*     */   private static final String MYSQL_CHARSET_NAME_binary = "binary";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1250 = "cp1250";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1251 = "cp1251";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1256 = "cp1256";
/*     */   private static final String MYSQL_CHARSET_NAME_cp1257 = "cp1257";
/*     */   private static final String MYSQL_CHARSET_NAME_cp850 = "cp850";
/*     */   private static final String MYSQL_CHARSET_NAME_cp852 = "cp852";
/*     */   private static final String MYSQL_CHARSET_NAME_cp866 = "cp866";
/*     */   private static final String MYSQL_CHARSET_NAME_cp932 = "cp932";
/*     */   private static final String MYSQL_CHARSET_NAME_dec8 = "dec8";
/*     */   private static final String MYSQL_CHARSET_NAME_eucjpms = "eucjpms";
/*     */   private static final String MYSQL_CHARSET_NAME_euckr = "euckr";
/*     */   private static final String MYSQL_CHARSET_NAME_gb2312 = "gb2312";
/*     */   private static final String MYSQL_CHARSET_NAME_gbk = "gbk";
/*     */   private static final String MYSQL_CHARSET_NAME_geostd8 = "geostd8";
/*     */   private static final String MYSQL_CHARSET_NAME_greek = "greek";
/*     */   private static final String MYSQL_CHARSET_NAME_hebrew = "hebrew";
/*     */   private static final String MYSQL_CHARSET_NAME_hp8 = "hp8";
/*     */   private static final String MYSQL_CHARSET_NAME_keybcs2 = "keybcs2";
/*     */   private static final String MYSQL_CHARSET_NAME_koi8r = "koi8r";
/*     */   private static final String MYSQL_CHARSET_NAME_koi8u = "koi8u";
/*     */   private static final String MYSQL_CHARSET_NAME_latin1 = "latin1";
/*     */   private static final String MYSQL_CHARSET_NAME_latin2 = "latin2";
/*     */   private static final String MYSQL_CHARSET_NAME_latin5 = "latin5";
/*     */   private static final String MYSQL_CHARSET_NAME_latin7 = "latin7";
/*     */   private static final String MYSQL_CHARSET_NAME_macce = "macce";
/*     */   private static final String MYSQL_CHARSET_NAME_macroman = "macroman";
/*     */   private static final String MYSQL_CHARSET_NAME_sjis = "sjis";
/*     */   private static final String MYSQL_CHARSET_NAME_swe7 = "swe7";
/*     */   private static final String MYSQL_CHARSET_NAME_tis620 = "tis620";
/*     */   private static final String MYSQL_CHARSET_NAME_ucs2 = "ucs2";
/*     */   private static final String MYSQL_CHARSET_NAME_ujis = "ujis";
/*     */   private static final String MYSQL_CHARSET_NAME_utf16 = "utf16";
/*     */   private static final String MYSQL_CHARSET_NAME_utf16le = "utf16le";
/*     */   private static final String MYSQL_CHARSET_NAME_utf32 = "utf32";
/*     */   private static final String MYSQL_CHARSET_NAME_utf8 = "utf8";
/*     */   private static final String MYSQL_CHARSET_NAME_utf8mb4 = "utf8mb4";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_croat = "croat";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_czech = "czech";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_danish = "danish";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_dos = "dos";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_estonia = "estonia";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_euc_kr = "euc_kr";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_german1 = "german1";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_hungarian = "hungarian";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_koi8_ru = "koi8_ru";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_koi8_ukr = "koi8_ukr";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_latin1_de = "latin1_de";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_usa7 = "usa7";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_win1250 = "win1250";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_win1251 = "win1251";
/*     */   private static final String MYSQL_4_0_CHARSET_NAME_win1251ukr = "win1251ukr";
/*     */   private static final String NOT_USED = "ISO8859_1";
/*     */ 
/*     */   public static final String getMysqlEncodingForJavaEncoding(String javaEncodingUC, Connection conn)
/*     */     throws SQLException
/*     */   {
/*     */     SQLException sqlEx;
/*     */     try
/*     */     {
/* 693 */       List mysqlEncodings = (List)JAVA_UC_TO_MYSQL_CHARSET_MAP.get(javaEncodingUC);
/*     */ 
/* 695 */       if (mysqlEncodings != null) {
/* 696 */         Iterator iter = mysqlEncodings.iterator();
/*     */ 
/* 698 */         VersionedStringProperty versionedProp = null;
/*     */ 
/* 700 */         while (iter.hasNext()) {
/* 701 */           VersionedStringProperty propToCheck = (VersionedStringProperty)iter.next();
/*     */ 
/* 703 */           if (conn == null)
/*     */           {
/* 706 */             return propToCheck.toString();
/*     */           }
/*     */ 
/* 709 */           if ((versionedProp != null) && (!versionedProp.preferredValue) && 
/* 710 */             (versionedProp.majorVersion == propToCheck.majorVersion) && (versionedProp.minorVersion == propToCheck.minorVersion) && (versionedProp.subminorVersion == propToCheck.subminorVersion))
/*     */           {
/* 713 */             return versionedProp.toString();
/*     */           }
/*     */ 
/* 717 */           if (!propToCheck.isOkayForVersion(conn)) break;
/* 718 */           if (propToCheck.preferredValue) {
/* 719 */             return propToCheck.toString();
/*     */           }
/*     */ 
/* 722 */           versionedProp = propToCheck;
/*     */         }
/*     */ 
/* 728 */         if (versionedProp != null) {
/* 729 */           return versionedProp.toString();
/*     */         }
/*     */       }
/*     */ 
/* 733 */       return null;
/*     */     } catch (SQLException ex) {
/* 735 */       throw ex;
/*     */     } catch (RuntimeException ex) {
/* 737 */       sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 738 */       sqlEx.initCause(ex);
/* 739 */     }throw sqlEx;
/*     */   }
/*     */ 
/*     */   static final int getNumberOfCharsetsConfigured()
/*     */   {
/* 745 */     return MYSQL_TO_JAVA_CHARSET_MAP.size() / 2;
/*     */   }
/*     */ 
/*     */   static final String getCharacterEncodingForErrorMessages(ConnectionImpl conn)
/*     */     throws SQLException
/*     */   {
/* 765 */     if (conn.versionMeetsMinimum(5, 5, 0)) {
/* 766 */       String errorMessageEncodingMysql = conn.getServerVariable("character_set_results");
/* 767 */       if (errorMessageEncodingMysql != null) {
/* 768 */         String javaEncoding = conn.getJavaEncodingForMysqlEncoding(errorMessageEncodingMysql);
/* 769 */         if (javaEncoding != null) {
/* 770 */           return javaEncoding;
/*     */         }
/*     */       }
/*     */ 
/* 774 */       return "UTF-8";
/*     */     }
/*     */ 
/* 777 */     String errorMessageFile = conn.getServerVariable("language");
/*     */ 
/* 779 */     if ((errorMessageFile == null) || (errorMessageFile.length() == 0))
/*     */     {
/* 781 */       return "Cp1252";
/*     */     }
/*     */ 
/* 784 */     int endWithoutSlash = errorMessageFile.length();
/*     */ 
/* 786 */     if ((errorMessageFile.endsWith("/")) || (errorMessageFile.endsWith("\\"))) {
/* 787 */       endWithoutSlash--;
/*     */     }
/*     */ 
/* 790 */     int lastSlashIndex = errorMessageFile.lastIndexOf('/', endWithoutSlash - 1);
/*     */ 
/* 792 */     if (lastSlashIndex == -1) {
/* 793 */       lastSlashIndex = errorMessageFile.lastIndexOf('\\', endWithoutSlash - 1);
/*     */     }
/*     */ 
/* 796 */     if (lastSlashIndex == -1) {
/* 797 */       lastSlashIndex = 0;
/*     */     }
/*     */ 
/* 800 */     if ((lastSlashIndex == endWithoutSlash) || (endWithoutSlash < lastSlashIndex))
/*     */     {
/* 802 */       return "Cp1252";
/*     */     }
/*     */ 
/* 805 */     errorMessageFile = errorMessageFile.substring(lastSlashIndex + 1, endWithoutSlash);
/*     */ 
/* 807 */     String errorMessageEncodingMysql = (String)ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP.get(errorMessageFile);
/*     */ 
/* 809 */     if (errorMessageEncodingMysql == null)
/*     */     {
/* 811 */       return "Cp1252";
/*     */     }
/*     */ 
/* 814 */     String javaEncoding = conn.getJavaEncodingForMysqlEncoding(errorMessageEncodingMysql);
/*     */ 
/* 816 */     if (javaEncoding == null)
/*     */     {
/* 818 */       return "Cp1252";
/*     */     }
/*     */ 
/* 821 */     return javaEncoding;
/*     */   }
/*     */ 
/*     */   static final boolean isAliasForSjis(String encoding) {
/* 825 */     return ("SJIS".equalsIgnoreCase(encoding)) || ("WINDOWS-31J".equalsIgnoreCase(encoding)) || ("MS932".equalsIgnoreCase(encoding)) || ("SHIFT_JIS".equalsIgnoreCase(encoding)) || ("CP943".equalsIgnoreCase(encoding));
/*     */   }
/*     */ 
/*     */   static final boolean isMultibyteCharset(String javaEncodingName)
/*     */   {
/* 834 */     String javaEncodingNameUC = javaEncodingName.toUpperCase(Locale.ENGLISH);
/*     */ 
/* 837 */     return MULTIBYTE_CHARSETS.containsKey(javaEncodingNameUC);
/*     */   }
/*     */ 
/*     */   private static void populateMapWithKeyValuePairsUnversioned(String configKey, Map<String, String> mapToPopulate, boolean addUppercaseKeys) {
/* 841 */     String javaToMysqlConfig = CHARSET_CONFIG.getProperty(configKey);
/* 842 */     if (javaToMysqlConfig == null) throw new RuntimeException("Could not find configuration value \"" + configKey + "\" in Charsets.properties resource");
/*     */ 
/* 844 */     List mappings = StringUtils.split(javaToMysqlConfig, ",", true);
/* 845 */     if (mappings == null) throw new RuntimeException("Missing/corrupt entry for \"" + configKey + "\" in Charsets.properties.");
/*     */ 
/* 847 */     Iterator mappingsIter = mappings.iterator();
/* 848 */     while (mappingsIter.hasNext()) {
/* 849 */       String aMapping = (String)mappingsIter.next();
/* 850 */       List parsedPair = StringUtils.split(aMapping, "=", true);
/* 851 */       if (parsedPair.size() != 2) throw new RuntimeException("Syntax error in Charsets.properties resource for token \"" + aMapping + "\".");
/*     */ 
/* 853 */       String key = ((String)parsedPair.get(0)).toString();
/* 854 */       String value = ((String)parsedPair.get(1)).toString();
/* 855 */       mapToPopulate.put(key, value);
/*     */ 
/* 857 */       if (addUppercaseKeys) mapToPopulate.put(key.toUpperCase(Locale.ENGLISH), value); 
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void populateMapWithKeyValuePairsVersioned(String configKey, Map<String, List<VersionedStringProperty>> mapToPopulate, boolean addUppercaseKeys)
/*     */   {
/* 862 */     String javaToMysqlConfig = CHARSET_CONFIG.getProperty(configKey);
/* 863 */     if (javaToMysqlConfig == null) throw new RuntimeException("Could not find configuration value \"" + configKey + "\" in Charsets.properties resource");
/*     */ 
/* 865 */     List mappings = StringUtils.split(javaToMysqlConfig, ",", true);
/* 866 */     if (mappings == null) throw new RuntimeException("Missing/corrupt entry for \"" + configKey + "\" in Charsets.properties.");
/*     */ 
/* 868 */     Iterator mappingsIter = mappings.iterator();
/* 869 */     while (mappingsIter.hasNext()) {
/* 870 */       String aMapping = (String)mappingsIter.next();
/* 871 */       List parsedPair = StringUtils.split(aMapping, "=", true);
/* 872 */       if (parsedPair.size() != 2) throw new RuntimeException("Syntax error in Charsets.properties resource for token \"" + aMapping + "\".");
/*     */ 
/* 874 */       String key = ((String)parsedPair.get(0)).toString();
/* 875 */       String value = ((String)parsedPair.get(1)).toString();
/*     */ 
/* 877 */       List versionedProperties = (List)mapToPopulate.get(key);
/*     */ 
/* 879 */       if (versionedProperties == null) {
/* 880 */         versionedProperties = new ArrayList();
/* 881 */         mapToPopulate.put(key, versionedProperties);
/*     */       }
/*     */ 
/* 884 */       VersionedStringProperty verProp = new VersionedStringProperty(value);
/* 885 */       versionedProperties.add(verProp);
/*     */ 
/* 887 */       if (addUppercaseKeys) {
/* 888 */         String keyUc = key.toUpperCase(Locale.ENGLISH);
/* 889 */         versionedProperties = (List)mapToPopulate.get(keyUc);
/*     */ 
/* 891 */         if (versionedProperties == null) {
/* 892 */           versionedProperties = new ArrayList();
/* 893 */           mapToPopulate.put(keyUc, versionedProperties);
/*     */         }
/*     */ 
/* 896 */         versionedProperties.add(verProp);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int getCharsetIndexForMysqlEncodingName(String name) {
/* 902 */     if (name == null) {
/* 903 */       return 0;
/*     */     }
/*     */ 
/* 906 */     Integer asInt = (Integer)MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP.get(name);
/*     */ 
/* 908 */     if (asInt == null) {
/* 909 */       return 0;
/*     */     }
/*     */ 
/* 912 */     return asInt.intValue();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 152 */     HashMap tempNumBytesMap = new HashMap();
/* 153 */     tempNumBytesMap.put("armscii8", Integer.valueOf(1));
/* 154 */     tempNumBytesMap.put("ascii", Integer.valueOf(1));
/* 155 */     tempNumBytesMap.put("big5", Integer.valueOf(2));
/* 156 */     tempNumBytesMap.put("binary", Integer.valueOf(1));
/* 157 */     tempNumBytesMap.put("cp1250", Integer.valueOf(1));
/* 158 */     tempNumBytesMap.put("cp1251", Integer.valueOf(1));
/* 159 */     tempNumBytesMap.put("cp1256", Integer.valueOf(1));
/* 160 */     tempNumBytesMap.put("cp1257", Integer.valueOf(1));
/* 161 */     tempNumBytesMap.put("cp850", Integer.valueOf(1));
/* 162 */     tempNumBytesMap.put("cp852", Integer.valueOf(1));
/* 163 */     tempNumBytesMap.put("cp866", Integer.valueOf(1));
/* 164 */     tempNumBytesMap.put("cp932", Integer.valueOf(2));
/* 165 */     tempNumBytesMap.put("dec8", Integer.valueOf(1));
/* 166 */     tempNumBytesMap.put("eucjpms", Integer.valueOf(3));
/* 167 */     tempNumBytesMap.put("euckr", Integer.valueOf(2));
/* 168 */     tempNumBytesMap.put("gb2312", Integer.valueOf(2));
/* 169 */     tempNumBytesMap.put("gbk", Integer.valueOf(2));
/* 170 */     tempNumBytesMap.put("geostd8", Integer.valueOf(1));
/* 171 */     tempNumBytesMap.put("greek", Integer.valueOf(1));
/* 172 */     tempNumBytesMap.put("hebrew", Integer.valueOf(1));
/* 173 */     tempNumBytesMap.put("hp8", Integer.valueOf(1));
/* 174 */     tempNumBytesMap.put("keybcs2", Integer.valueOf(1));
/* 175 */     tempNumBytesMap.put("koi8r", Integer.valueOf(1));
/* 176 */     tempNumBytesMap.put("koi8u", Integer.valueOf(1));
/* 177 */     tempNumBytesMap.put("latin1", Integer.valueOf(1));
/* 178 */     tempNumBytesMap.put("latin2", Integer.valueOf(1));
/* 179 */     tempNumBytesMap.put("latin5", Integer.valueOf(1));
/* 180 */     tempNumBytesMap.put("latin7", Integer.valueOf(1));
/* 181 */     tempNumBytesMap.put("macce", Integer.valueOf(1));
/* 182 */     tempNumBytesMap.put("macroman", Integer.valueOf(1));
/* 183 */     tempNumBytesMap.put("sjis", Integer.valueOf(2));
/* 184 */     tempNumBytesMap.put("swe7", Integer.valueOf(1));
/* 185 */     tempNumBytesMap.put("tis620", Integer.valueOf(1));
/* 186 */     tempNumBytesMap.put("ucs2", Integer.valueOf(2));
/* 187 */     tempNumBytesMap.put("ujis", Integer.valueOf(3));
/* 188 */     tempNumBytesMap.put("utf16", Integer.valueOf(4));
/* 189 */     tempNumBytesMap.put("utf16le", Integer.valueOf(4));
/* 190 */     tempNumBytesMap.put("utf32", Integer.valueOf(4));
/* 191 */     tempNumBytesMap.put("utf8", Integer.valueOf(3));
/* 192 */     tempNumBytesMap.put("utf8mb4", Integer.valueOf(4));
/* 193 */     STATIC_CHARSET_TO_NUM_BYTES_MAP = Collections.unmodifiableMap(tempNumBytesMap);
/*     */ 
/* 195 */     tempNumBytesMap = new HashMap();
/* 196 */     tempNumBytesMap.put("croat", Integer.valueOf(1));
/* 197 */     tempNumBytesMap.put("czech", Integer.valueOf(1));
/* 198 */     tempNumBytesMap.put("danish", Integer.valueOf(1));
/* 199 */     tempNumBytesMap.put("dos", Integer.valueOf(1));
/* 200 */     tempNumBytesMap.put("estonia", Integer.valueOf(1));
/* 201 */     tempNumBytesMap.put("euc_kr", Integer.valueOf(2));
/* 202 */     tempNumBytesMap.put("german1", Integer.valueOf(1));
/* 203 */     tempNumBytesMap.put("hungarian", Integer.valueOf(1));
/* 204 */     tempNumBytesMap.put("koi8_ru", Integer.valueOf(1));
/* 205 */     tempNumBytesMap.put("koi8_ukr", Integer.valueOf(1));
/* 206 */     tempNumBytesMap.put("latin1_de", Integer.valueOf(1));
/* 207 */     tempNumBytesMap.put("usa7", Integer.valueOf(1));
/* 208 */     tempNumBytesMap.put("win1250", Integer.valueOf(1));
/* 209 */     tempNumBytesMap.put("win1251", Integer.valueOf(1));
/* 210 */     tempNumBytesMap.put("win1251ukr", Integer.valueOf(1));
/* 211 */     STATIC_4_0_CHARSET_TO_NUM_BYTES_MAP = Collections.unmodifiableMap(tempNumBytesMap);
/*     */ 
/* 213 */     CHARSET_CONFIG.setProperty("javaToMysqlMappings", "US-ASCII =\t\t\tusa7,US-ASCII =\t\t\t>4.1.0 ascii,Big5 = \t\t\t\tbig5,GBK = \t\t\t\tgbk,SJIS = \t\t\t\tsjis,EUC_CN = \t\t\tgb2312,EUC_JP = \t\t\tujis,EUC_JP_Solaris = \t>5.0.3 eucjpms,EUC_KR = \t\t\teuc_kr,EUC_KR = \t\t\t>4.1.0 euckr,ISO8859_1 =\t\t\t*latin1,ISO8859_1 =\t\t\tlatin1_de,ISO8859_1 =\t\t\tgerman1,ISO8859_1 =\t\t\tdanish,ISO8859_2 =\t\t\tlatin2,ISO8859_2 =\t\t\tczech,ISO8859_2 =\t\t\thungarian,ISO8859_2  =\t\tcroat,ISO8859_7  =\t\tgreek,ISO8859_7  =\t\tlatin7,ISO8859_8  = \t\thebrew,ISO8859_9  =\t\tlatin5,ISO8859_13 =\t\tlatvian,ISO8859_13 =\t\tlatvian1,ISO8859_13 =\t\testonia,Cp437 =             *>4.1.0 cp850,Cp437 =\t\t\t\tdos,Cp850 =\t\t\t\tcp850,Cp852 = \t\t\tcp852,Cp866 = \t\t\tcp866,KOI8_R = \t\t\tkoi8_ru,KOI8_R = \t\t\t>4.1.0 koi8r,TIS620 = \t\t\ttis620,Cp1250 = \t\t\tcp1250,Cp1250 = \t\t\twin1250,Cp1251 = \t\t\t*>4.1.0 cp1251,Cp1251 = \t\t\twin1251,Cp1251 = \t\t\tcp1251cias,Cp1251 = \t\t\tcp1251csas,Cp1256 = \t\t\tcp1256,Cp1251 = \t\t\twin1251ukr,Cp1252 =             latin1,Cp1257 = \t\t\tcp1257,MacRoman = \t\t\tmacroman,MacCentralEurope = \tmacce,UTF-8 = \t\tutf8,UTF-8 =\t\t\t\t*> 5.5.2 utf8mb4,UnicodeBig = \tucs2,US-ASCII =\t\tbinary,Cp943 =        \tsjis,MS932 =\t\t\tsjis,MS932 =        \t>4.1.11 cp932,WINDOWS-31J =\tsjis,WINDOWS-31J = \t>4.1.11 cp932,CP932 =\t\t\tsjis,CP932 =\t\t\t*>4.1.11 cp932,SHIFT_JIS = \tsjis,ASCII =\t\t\tascii,LATIN5 =\t\tlatin5,LATIN7 =\t\tlatin7,HEBREW =\t\thebrew,GREEK =\t\t\tgreek,EUCKR =\t\t\teuckr,GB2312 =\t\tgb2312,LATIN2 =\t\tlatin2,UTF-16 = \t>5.2.0 utf16,UTF-16LE = \t>5.6.0 utf16le,UTF-32 = \t>5.2.0 utf32");
/*     */ 
/* 292 */     HashMap javaToMysqlMap = new HashMap();
/*     */ 
/* 294 */     populateMapWithKeyValuePairsVersioned("javaToMysqlMappings", javaToMysqlMap, false);
/* 295 */     JAVA_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(javaToMysqlMap);
/*     */ 
/* 298 */     HashMap mysqlToJavaMap = new HashMap();
/*     */ 
/* 300 */     Set keySet = JAVA_TO_MYSQL_CHARSET_MAP.keySet();
/* 301 */     Iterator javaCharsets = keySet.iterator();
/* 302 */     while (javaCharsets.hasNext()) {
/* 303 */       String javaEncodingName = (String)javaCharsets.next();
/* 304 */       List mysqlEncodingList = (List)JAVA_TO_MYSQL_CHARSET_MAP.get(javaEncodingName);
/*     */ 
/* 306 */       Iterator mysqlEncodings = mysqlEncodingList.iterator();
/*     */ 
/* 308 */       String mysqlEncodingName = null;
/*     */ 
/* 310 */       while (mysqlEncodings.hasNext()) {
/* 311 */         VersionedStringProperty mysqlProp = (VersionedStringProperty)mysqlEncodings.next();
/* 312 */         mysqlEncodingName = mysqlProp.toString();
/*     */ 
/* 314 */         mysqlToJavaMap.put(mysqlEncodingName, javaEncodingName);
/* 315 */         mysqlToJavaMap.put(mysqlEncodingName.toUpperCase(Locale.ENGLISH), javaEncodingName);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 320 */     mysqlToJavaMap.put("cp932", "Windows-31J");
/* 321 */     mysqlToJavaMap.put("CP932", "Windows-31J");
/*     */ 
/* 323 */     MYSQL_TO_JAVA_CHARSET_MAP = Collections.unmodifiableMap(mysqlToJavaMap);
/*     */ 
/* 326 */     TreeMap ucMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 327 */     Iterator javaNamesKeys = JAVA_TO_MYSQL_CHARSET_MAP.keySet().iterator();
/* 328 */     while (javaNamesKeys.hasNext()) {
/* 329 */       String key = (String)javaNamesKeys.next();
/* 330 */       ucMap.put(key.toUpperCase(Locale.ENGLISH), JAVA_TO_MYSQL_CHARSET_MAP.get(key));
/*     */     }
/* 332 */     JAVA_UC_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(ucMap);
/*     */ 
/* 338 */     HashMap tempMapMulti = new HashMap();
/*     */ 
/* 340 */     CHARSET_CONFIG.setProperty("multibyteCharsets", "Big5 = \t\t\tbig5,GBK = \t\t\tgbk,SJIS = \t\t\tsjis,EUC_CN = \t\tgb2312,EUC_JP = \t\tujis,EUC_JP_Solaris = eucjpms,EUC_KR = \t\teuc_kr,EUC_KR = \t\t>4.1.0 euckr,Cp943 =        \tsjis,Cp943 = \t\tcp943,WINDOWS-31J =\tsjis,WINDOWS-31J = \tcp932,CP932 =\t\t\tcp932,MS932 =\t\t\tsjis,MS932 =        \tcp932,SHIFT_JIS = \tsjis,EUCKR =\t\t\teuckr,GB2312 =\t\tgb2312,UTF-8 = \t\tutf8,utf8 =          utf8,UnicodeBig = \tucs2,UTF-16 = \t>5.2.0 utf16,UTF-16LE = \t>5.6.0 utf16le,UTF-32 = \t>5.2.0 utf32");
/*     */ 
/* 375 */     populateMapWithKeyValuePairsUnversioned("multibyteCharsets", tempMapMulti, true);
/*     */ 
/* 377 */     MULTIBYTE_CHARSETS = Collections.unmodifiableMap(tempMapMulti);
/*     */ 
/* 379 */     Collation[] collation = new Collation['ÿ'];
/* 380 */     collation[1] = new Collation(1, "big5_chinese_ci", "big5");
/* 381 */     collation[2] = new Collation(2, "latin2_czech_cs", "latin2");
/* 382 */     collation[3] = new Collation(3, "dec8_swedish_ci", "dec8", "ISO8859_1");
/* 383 */     collation[4] = new Collation(4, "cp850_general_ci", "cp850", "ISO8859_1");
/* 384 */     collation[5] = new Collation(5, "latin1_german1_ci", "latin1");
/* 385 */     collation[6] = new Collation(6, "hp8_english_ci", "hp8", "ISO8859_1");
/* 386 */     collation[7] = new Collation(7, "koi8r_general_ci", "koi8r");
/* 387 */     collation[8] = new Collation(8, "latin1_swedish_ci", "latin1");
/* 388 */     collation[9] = new Collation(9, "latin2_general_ci", "latin2");
/* 389 */     collation[10] = new Collation(10, "swe7_swedish_ci", "swe7", "ISO8859_1");
/* 390 */     collation[11] = new Collation(11, "ascii_general_ci", "ascii");
/* 391 */     collation[12] = new Collation(12, "ujis_japanese_ci", "ujis");
/* 392 */     collation[13] = new Collation(13, "sjis_japanese_ci", "sjis");
/* 393 */     collation[14] = new Collation(14, "cp1251_bulgarian_ci", "cp1251");
/* 394 */     collation[15] = new Collation(15, "latin1_danish_ci", "latin1");
/* 395 */     collation[16] = new Collation(16, "hebrew_general_ci", "hebrew");
/* 396 */     collation[17] = new Collation(17, "latin1_german1_ci", "win1251");
/* 397 */     collation[18] = new Collation(18, "tis620_thai_ci", "tis620");
/* 398 */     collation[19] = new Collation(19, "euckr_korean_ci", "euckr");
/* 399 */     collation[20] = new Collation(20, "latin7_estonian_cs", "latin7", "ISO8859_13");
/* 400 */     collation[21] = new Collation(21, "latin2_hungarian_ci", "latin2");
/* 401 */     collation[22] = new Collation(22, "koi8u_general_ci", "koi8u", "KOI8_R");
/* 402 */     collation[23] = new Collation(23, "cp1251_ukrainian_ci", "cp1251");
/* 403 */     collation[24] = new Collation(24, "gb2312_chinese_ci", "gb2312");
/* 404 */     collation[25] = new Collation(25, "greek_general_ci", "greek");
/* 405 */     collation[26] = new Collation(26, "cp1250_general_ci", "cp1250");
/* 406 */     collation[27] = new Collation(27, "latin2_croatian_ci", "latin2");
/* 407 */     collation[28] = new Collation(28, "gbk_chinese_ci", "gbk");
/* 408 */     collation[29] = new Collation(29, "cp1257_lithuanian_ci", "cp1257");
/* 409 */     collation[30] = new Collation(30, "latin5_turkish_ci", "latin5");
/* 410 */     collation[31] = new Collation(31, "latin1_german2_ci", "latin1");
/* 411 */     collation[32] = new Collation(32, "armscii8_general_ci", "armscii8", "ISO8859_1");
/* 412 */     collation[33] = new Collation(33, "utf8_general_ci", "utf8");
/* 413 */     collation[34] = new Collation(34, "cp1250_czech_cs", "cp1250");
/* 414 */     collation[35] = new Collation(35, "ucs2_general_ci", "ucs2");
/* 415 */     collation[36] = new Collation(36, "cp866_general_ci", "cp866");
/* 416 */     collation[37] = new Collation(37, "keybcs2_general_ci", "keybcs2", "Cp895");
/* 417 */     collation[38] = new Collation(38, "macce_general_ci", "macce");
/* 418 */     collation[39] = new Collation(39, "macroman_general_ci", "macroman");
/* 419 */     collation[40] = new Collation(40, "cp852_general_ci", "cp852", "LATIN2");
/* 420 */     collation[41] = new Collation(41, "latin7_general_ci", "latin7", "ISO8859_13");
/* 421 */     collation[42] = new Collation(42, "latin7_general_cs", "latin7", "ISO8859_13");
/* 422 */     collation[43] = new Collation(43, "macce_bin", "macce");
/* 423 */     collation[44] = new Collation(44, "cp1250_croatian_ci", "cp1250");
/* 424 */     collation[45] = new Collation(45, "utf8mb4_general_ci", "utf8mb4");
/* 425 */     collation[46] = new Collation(46, "utf8mb4_bin", "utf8mb4");
/* 426 */     collation[47] = new Collation(47, "latin1_bin", "latin1");
/* 427 */     collation[48] = new Collation(48, "latin1_general_ci", "latin1");
/* 428 */     collation[49] = new Collation(49, "latin1_general_cs", "latin1");
/* 429 */     collation[50] = new Collation(50, "cp1251_bin", "cp1251");
/* 430 */     collation[51] = new Collation(51, "cp1251_general_ci", "cp1251");
/* 431 */     collation[52] = new Collation(52, "cp1251_general_cs", "cp1251");
/* 432 */     collation[53] = new Collation(53, "macroman_bin", "macroman");
/* 433 */     collation[54] = new Collation(54, "utf16_general_ci", "utf16");
/* 434 */     collation[55] = new Collation(55, "utf16_bin", "utf16");
/* 435 */     collation[56] = new Collation(56, "utf16le_general_ci", "utf16le");
/* 436 */     collation[57] = new Collation(57, "cp1256_general_ci", "cp1256");
/* 437 */     collation[58] = new Collation(58, "cp1257_bin", "cp1257");
/* 438 */     collation[59] = new Collation(59, "cp1257_general_ci", "cp1257");
/* 439 */     collation[60] = new Collation(60, "utf32_general_ci", "utf32");
/* 440 */     collation[61] = new Collation(61, "utf32_bin", "utf32");
/* 441 */     collation[62] = new Collation(62, "utf16le_bin", "utf16le");
/* 442 */     collation[63] = new Collation(63, "binary", "binary");
/* 443 */     collation[64] = new Collation(64, "armscii8_bin", "armscii8", "ISO8859_2");
/* 444 */     collation[65] = new Collation(65, "ascii_bin", "ascii");
/* 445 */     collation[66] = new Collation(66, "cp1250_bin", "cp1250");
/* 446 */     collation[67] = new Collation(67, "cp1256_bin", "cp1256");
/* 447 */     collation[68] = new Collation(68, "cp866_bin", "cp866");
/* 448 */     collation[69] = new Collation(69, "dec8_bin", "dec8", "US-ASCII");
/* 449 */     collation[70] = new Collation(70, "greek_bin", "greek");
/* 450 */     collation[71] = new Collation(71, "hebrew_bin", "hebrew");
/* 451 */     collation[72] = new Collation(72, "hp8_bin", "hp8", "US-ASCII");
/* 452 */     collation[73] = new Collation(73, "keybcs2_bin", "keybcs2", "Cp895");
/* 453 */     collation[74] = new Collation(74, "koi8r_bin", "koi8r");
/* 454 */     collation[75] = new Collation(75, "koi8u_bin", "koi8u", "KOI8_R");
/* 455 */     collation[76] = new Collation(76, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 456 */     collation[77] = new Collation(77, "latin2_bin", "latin2");
/* 457 */     collation[78] = new Collation(78, "latin5_bin", "latin5");
/* 458 */     collation[79] = new Collation(79, "latin7_bin", "latin7");
/* 459 */     collation[80] = new Collation(80, "cp850_bin", "cp850");
/* 460 */     collation[81] = new Collation(81, "cp852_bin", "cp852");
/* 461 */     collation[82] = new Collation(82, "swe7_bin", "swe7", "ISO8859_1");
/* 462 */     collation[83] = new Collation(83, "utf8_bin", "utf8");
/* 463 */     collation[84] = new Collation(84, "big5_bin", "big5");
/* 464 */     collation[85] = new Collation(85, "euckr_bin", "euckr");
/* 465 */     collation[86] = new Collation(86, "gb2312_bin", "gb2312");
/* 466 */     collation[87] = new Collation(87, "gbk_bin", "gbk");
/* 467 */     collation[88] = new Collation(88, "sjis_bin", "sjis");
/* 468 */     collation[89] = new Collation(89, "tis620_bin", "tis620");
/* 469 */     collation[90] = new Collation(90, "ucs2_bin", "ucs2");
/* 470 */     collation[91] = new Collation(91, "ujis_bin", "ujis");
/* 471 */     collation[92] = new Collation(92, "geostd8_general_ci", "geostd8", "US-ASCII");
/* 472 */     collation[93] = new Collation(93, "geostd8_bin", "geostd8", "US-ASCII");
/* 473 */     collation[94] = new Collation(94, "latin1_spanish_ci", "latin1");
/* 474 */     collation[95] = new Collation(95, "cp932_japanese_ci", "cp932");
/* 475 */     collation[96] = new Collation(96, "cp932_bin", "cp932");
/* 476 */     collation[97] = new Collation(97, "eucjpms_japanese_ci", "eucjpms");
/* 477 */     collation[98] = new Collation(98, "eucjpms_bin", "eucjpms");
/* 478 */     collation[99] = new Collation(99, "cp1250_polish_ci", "cp1250");
/* 479 */     collation[100] = new Collation(100, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 480 */     collation[101] = new Collation(101, "utf16_unicode_ci", "utf16");
/* 481 */     collation[102] = new Collation(102, "utf16_icelandic_ci", "utf16");
/* 482 */     collation[103] = new Collation(103, "utf16_latvian_ci", "utf16");
/* 483 */     collation[104] = new Collation(104, "utf16_romanian_ci", "utf16");
/* 484 */     collation[105] = new Collation(105, "utf16_slovenian_ci", "utf16");
/* 485 */     collation[106] = new Collation(106, "utf16_polish_ci", "utf16");
/* 486 */     collation[107] = new Collation(107, "utf16_estonian_ci", "utf16");
/* 487 */     collation[108] = new Collation(108, "utf16_spanish_ci", "utf16");
/* 488 */     collation[109] = new Collation(109, "utf16_swedish_ci", "utf16");
/* 489 */     collation[110] = new Collation(110, "utf16_turkish_ci", "utf16");
/* 490 */     collation[111] = new Collation(111, "utf16_czech_ci", "utf16");
/* 491 */     collation[112] = new Collation(112, "utf16_danish_ci", "utf16");
/* 492 */     collation[113] = new Collation(113, "utf16_lithuanian_ci", "utf16");
/* 493 */     collation[114] = new Collation(114, "utf16_slovak_ci", "utf16");
/* 494 */     collation[115] = new Collation(115, "utf16_spanish2_ci", "utf16");
/* 495 */     collation[116] = new Collation(116, "utf16_roman_ci", "utf16");
/* 496 */     collation[117] = new Collation(117, "utf16_persian_ci", "utf16");
/* 497 */     collation[118] = new Collation(118, "utf16_esperanto_ci", "utf16");
/* 498 */     collation[119] = new Collation(119, "utf16_hungarian_ci", "utf16");
/* 499 */     collation[120] = new Collation(120, "utf16_sinhala_ci", "utf16");
/* 500 */     collation[121] = new Collation(121, "utf16_german2_ci", "utf16");
/* 501 */     collation[122] = new Collation(122, "utf16_croatian_ci", "utf16");
/* 502 */     collation[123] = new Collation(123, "utf16_unicode_520_ci", "utf16");
/* 503 */     collation[124] = new Collation(124, "utf16_vietnamese_ci", "utf16");
/* 504 */     collation[125] = new Collation(125, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 505 */     collation[126] = new Collation(126, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 506 */     collation[127] = new Collation(127, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 507 */     collation[''] = new Collation(128, "ucs2_unicode_ci", "ucs2");
/* 508 */     collation[''] = new Collation(129, "ucs2_icelandic_ci", "ucs2");
/* 509 */     collation[''] = new Collation(130, "ucs2_latvian_ci", "ucs2");
/* 510 */     collation[''] = new Collation(131, "ucs2_romanian_ci", "ucs2");
/* 511 */     collation[''] = new Collation(132, "ucs2_slovenian_ci", "ucs2");
/* 512 */     collation[''] = new Collation(133, "ucs2_polish_ci", "ucs2");
/* 513 */     collation[''] = new Collation(134, "ucs2_estonian_ci", "ucs2");
/* 514 */     collation[''] = new Collation(135, "ucs2_spanish_ci", "ucs2");
/* 515 */     collation[''] = new Collation(136, "ucs2_swedish_ci", "ucs2");
/* 516 */     collation[''] = new Collation(137, "ucs2_turkish_ci", "ucs2");
/* 517 */     collation[''] = new Collation(138, "ucs2_czech_ci", "ucs2");
/* 518 */     collation[''] = new Collation(139, "ucs2_danish_ci", "ucs2");
/* 519 */     collation[''] = new Collation(140, "ucs2_lithuanian_ci", "ucs2");
/* 520 */     collation[''] = new Collation(141, "ucs2_slovak_ci", "ucs2");
/* 521 */     collation[''] = new Collation(142, "ucs2_spanish2_ci", "ucs2");
/* 522 */     collation[''] = new Collation(143, "ucs2_roman_ci", "ucs2");
/* 523 */     collation[''] = new Collation(144, "ucs2_persian_ci", "ucs2");
/* 524 */     collation[''] = new Collation(145, "ucs2_esperanto_ci", "ucs2");
/* 525 */     collation[''] = new Collation(146, "ucs2_hungarian_ci", "ucs2");
/* 526 */     collation[''] = new Collation(147, "ucs2_sinhala_ci", "ucs2");
/* 527 */     collation[''] = new Collation(148, "ucs2_german2_ci", "ucs2");
/* 528 */     collation[''] = new Collation(149, "ucs2_croatian_ci", "ucs2");
/* 529 */     collation[''] = new Collation(150, "ucs2_unicode_520_ci", "ucs2");
/* 530 */     collation[''] = new Collation(151, "ucs2_vietnamese_ci", "ucs2");
/* 531 */     collation[''] = new Collation(152, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 532 */     collation[''] = new Collation(153, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 533 */     collation[''] = new Collation(154, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 534 */     collation[''] = new Collation(155, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 535 */     collation[''] = new Collation(156, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 536 */     collation[''] = new Collation(157, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 537 */     collation[''] = new Collation(158, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 538 */     collation[''] = new Collation(159, "ucs2_general_mysql500_ci", "ucs2");
/* 539 */     collation[' '] = new Collation(160, "utf32_unicode_ci", "utf32");
/* 540 */     collation['¡'] = new Collation(161, "utf32_icelandic_ci", "utf32");
/* 541 */     collation['¢'] = new Collation(162, "utf32_latvian_ci", "utf32");
/* 542 */     collation['£'] = new Collation(163, "utf32_romanian_ci", "utf32");
/* 543 */     collation['¤'] = new Collation(164, "utf32_slovenian_ci", "utf32");
/* 544 */     collation['¥'] = new Collation(165, "utf32_polish_ci", "utf32");
/* 545 */     collation['¦'] = new Collation(166, "utf32_estonian_ci", "utf32");
/* 546 */     collation['§'] = new Collation(167, "utf32_spanish_ci", "utf32");
/* 547 */     collation['¨'] = new Collation(168, "utf32_swedish_ci", "utf32");
/* 548 */     collation['©'] = new Collation(169, "utf32_turkish_ci", "utf32");
/* 549 */     collation['ª'] = new Collation(170, "utf32_czech_ci", "utf32");
/* 550 */     collation['«'] = new Collation(171, "utf32_danish_ci", "utf32");
/* 551 */     collation['¬'] = new Collation(172, "utf32_lithuanian_ci", "utf32");
/* 552 */     collation['­'] = new Collation(173, "utf32_slovak_ci", "utf32");
/* 553 */     collation['®'] = new Collation(174, "utf32_spanish2_ci", "utf32");
/* 554 */     collation['¯'] = new Collation(175, "utf32_roman_ci", "utf32");
/* 555 */     collation['°'] = new Collation(176, "utf32_persian_ci", "utf32");
/* 556 */     collation['±'] = new Collation(177, "utf32_esperanto_ci", "utf32");
/* 557 */     collation['²'] = new Collation(178, "utf32_hungarian_ci", "utf32");
/* 558 */     collation['³'] = new Collation(179, "utf32_sinhala_ci", "utf32");
/* 559 */     collation['´'] = new Collation(180, "utf32_german2_ci", "utf32");
/* 560 */     collation['µ'] = new Collation(181, "utf32_croatian_ci", "utf32");
/* 561 */     collation['¶'] = new Collation(182, "utf32_unicode_520_ci", "utf32");
/* 562 */     collation['·'] = new Collation(183, "utf32_vietnamese_ci", "utf32");
/* 563 */     collation['¸'] = new Collation(184, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 564 */     collation['¹'] = new Collation(185, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 565 */     collation['º'] = new Collation(186, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 566 */     collation['»'] = new Collation(187, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 567 */     collation['¼'] = new Collation(188, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 568 */     collation['½'] = new Collation(189, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 569 */     collation['¾'] = new Collation(190, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 570 */     collation['¿'] = new Collation(191, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 571 */     collation['À'] = new Collation(192, "utf8_unicode_ci", "utf8");
/* 572 */     collation['Á'] = new Collation(193, "utf8_icelandic_ci", "utf8");
/* 573 */     collation['Â'] = new Collation(194, "utf8_latvian_ci", "utf8");
/* 574 */     collation['Ã'] = new Collation(195, "utf8_romanian_ci", "utf8");
/* 575 */     collation['Ä'] = new Collation(196, "utf8_slovenian_ci", "utf8");
/* 576 */     collation['Å'] = new Collation(197, "utf8_polish_ci", "utf8");
/* 577 */     collation['Æ'] = new Collation(198, "utf8_estonian_ci", "utf8");
/* 578 */     collation['Ç'] = new Collation(199, "utf8_spanish_ci", "utf8");
/* 579 */     collation['È'] = new Collation(200, "utf8_swedish_ci", "utf8");
/* 580 */     collation['É'] = new Collation(201, "utf8_turkish_ci", "utf8");
/* 581 */     collation['Ê'] = new Collation(202, "utf8_czech_ci", "utf8");
/* 582 */     collation['Ë'] = new Collation(203, "utf8_danish_ci", "utf8");
/* 583 */     collation['Ì'] = new Collation(204, "utf8_lithuanian_ci", "utf8");
/* 584 */     collation['Í'] = new Collation(205, "utf8_slovak_ci", "utf8");
/* 585 */     collation['Î'] = new Collation(206, "utf8_spanish2_ci", "utf8");
/* 586 */     collation['Ï'] = new Collation(207, "utf8_roman_ci", "utf8");
/* 587 */     collation['Ð'] = new Collation(208, "utf8_persian_ci", "utf8");
/* 588 */     collation['Ñ'] = new Collation(209, "utf8_esperanto_ci", "utf8");
/* 589 */     collation['Ò'] = new Collation(210, "utf8_hungarian_ci", "utf8");
/* 590 */     collation['Ó'] = new Collation(211, "utf8_sinhala_ci", "utf8");
/* 591 */     collation['Ô'] = new Collation(212, "utf8_german2_ci", "utf8");
/* 592 */     collation['Õ'] = new Collation(213, "utf8_croatian_ci", "utf8");
/* 593 */     collation['Ö'] = new Collation(214, "utf8_unicode_520_ci", "utf8");
/* 594 */     collation['×'] = new Collation(215, "utf8_vietnamese_ci", "utf8");
/* 595 */     collation['Ø'] = new Collation(216, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 596 */     collation['Ù'] = new Collation(217, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 597 */     collation['Ú'] = new Collation(218, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 598 */     collation['Û'] = new Collation(219, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 599 */     collation['Ü'] = new Collation(220, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 600 */     collation['Ý'] = new Collation(221, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 601 */     collation['Þ'] = new Collation(222, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 602 */     collation['ß'] = new Collation(223, "utf8_general_mysql500_ci", "utf8");
/* 603 */     collation['à'] = new Collation(224, "utf8mb4_unicode_ci", "utf8mb4");
/* 604 */     collation['á'] = new Collation(225, "utf8mb4_icelandic_ci", "utf8mb4");
/* 605 */     collation['â'] = new Collation(226, "utf8mb4_latvian_ci", "utf8mb4");
/* 606 */     collation['ã'] = new Collation(227, "utf8mb4_romanian_ci", "utf8mb4");
/* 607 */     collation['ä'] = new Collation(228, "utf8mb4_slovenian_ci", "utf8mb4");
/* 608 */     collation['å'] = new Collation(229, "utf8mb4_polish_ci", "utf8mb4");
/* 609 */     collation['æ'] = new Collation(230, "utf8mb4_estonian_ci", "utf8mb4");
/* 610 */     collation['ç'] = new Collation(231, "utf8mb4_spanish_ci", "utf8mb4");
/* 611 */     collation['è'] = new Collation(232, "utf8mb4_swedish_ci", "utf8mb4");
/* 612 */     collation['é'] = new Collation(233, "utf8mb4_turkish_ci", "utf8mb4");
/* 613 */     collation['ê'] = new Collation(234, "utf8mb4_czech_ci", "utf8mb4");
/* 614 */     collation['ë'] = new Collation(235, "utf8mb4_danish_ci", "utf8mb4");
/* 615 */     collation['ì'] = new Collation(236, "utf8mb4_lithuanian_ci", "utf8mb4");
/* 616 */     collation['í'] = new Collation(237, "utf8mb4_slovak_ci", "utf8mb4");
/* 617 */     collation['î'] = new Collation(238, "utf8mb4_spanish2_ci", "utf8mb4");
/* 618 */     collation['ï'] = new Collation(239, "utf8mb4_roman_ci", "utf8mb4");
/* 619 */     collation['ð'] = new Collation(240, "utf8mb4_persian_ci", "utf8mb4");
/* 620 */     collation['ñ'] = new Collation(241, "utf8mb4_esperanto_ci", "utf8mb4");
/* 621 */     collation['ò'] = new Collation(242, "utf8mb4_hungarian_ci", "utf8mb4");
/* 622 */     collation['ó'] = new Collation(243, "utf8mb4_sinhala_ci", "utf8mb4");
/* 623 */     collation['ô'] = new Collation(244, "utf8mb4_german2_ci", "utf8mb4");
/* 624 */     collation['õ'] = new Collation(245, "utf8mb4_croatian_ci", "utf8mb4");
/* 625 */     collation['ö'] = new Collation(246, "utf8mb4_unicode_520_ci", "utf8mb4");
/* 626 */     collation['÷'] = new Collation(247, "utf8mb4_vietnamese_ci", "utf8mb4");
/* 627 */     collation['ø'] = new Collation(248, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 628 */     collation['ù'] = new Collation(249, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 629 */     collation['ú'] = new Collation(250, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 630 */     collation['û'] = new Collation(251, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 631 */     collation['ü'] = new Collation(252, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 632 */     collation['ý'] = new Collation(253, "latin1_german1_ci", "latin1", "ISO8859_1");
/* 633 */     collation['þ'] = new Collation(254, "utf8mb3_general_cs", "utf8");
/*     */ 
/* 635 */     INDEX_TO_COLLATION = new String['ÿ'];
/* 636 */     INDEX_TO_CHARSET = new String['ÿ'];
/* 637 */     Map indexToMysqlCharset = new HashMap();
/* 638 */     Map indexMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*     */ 
/* 640 */     for (int i = 1; i < 255; i++) {
/* 641 */       INDEX_TO_COLLATION[i] = collation[i].collationName;
/* 642 */       indexToMysqlCharset.put(Integer.valueOf(i), collation[i].charsetName);
/* 643 */       INDEX_TO_CHARSET[i] = collation[i].javaCharsetName;
/*     */ 
/* 645 */       if (INDEX_TO_CHARSET[i] == null) continue; indexMap.put(INDEX_TO_CHARSET[i], Integer.valueOf(i));
/*     */     }
/*     */ 
/* 649 */     for (int i = 1; i < 255; i++) {
/* 650 */       if (INDEX_TO_COLLATION[i] == null) throw new RuntimeException("Assertion failure: No mapping from charset index " + i + " to a mysql collation");
/* 651 */       if (indexToMysqlCharset.get(Integer.valueOf(i)) == null) throw new RuntimeException("Assertion failure: No mapping from charset index " + i + " to a mysql character set");
/* 652 */       if (INDEX_TO_CHARSET[i] != null) continue; throw new RuntimeException("Assertion failure: No mapping from charset index " + i + " to a Java character set");
/*     */     }
/*     */ 
/* 655 */     MYSQL_ENCODING_NAME_TO_CHARSET_INDEX_MAP = Collections.unmodifiableMap(indexMap);
/* 656 */     STATIC_INDEX_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(indexToMysqlCharset);
/*     */ 
/* 658 */     Map tempMap = new HashMap();
/*     */ 
/* 660 */     tempMap.put("czech", "latin2");
/* 661 */     tempMap.put("danish", "latin1");
/* 662 */     tempMap.put("dutch", "latin1");
/* 663 */     tempMap.put("english", "latin1");
/* 664 */     tempMap.put("estonian", "latin7");
/* 665 */     tempMap.put("french", "latin1");
/* 666 */     tempMap.put("german", "latin1");
/* 667 */     tempMap.put("greek", "greek");
/* 668 */     tempMap.put("hungarian", "latin2");
/* 669 */     tempMap.put("italian", "latin1");
/* 670 */     tempMap.put("japanese", "ujis");
/* 671 */     tempMap.put("japanese-sjis", "sjis");
/* 672 */     tempMap.put("korean", "euckr");
/* 673 */     tempMap.put("norwegian", "latin1");
/* 674 */     tempMap.put("norwegian-ny", "latin1");
/* 675 */     tempMap.put("polish", "latin2");
/* 676 */     tempMap.put("portuguese", "latin1");
/* 677 */     tempMap.put("romanian", "latin2");
/* 678 */     tempMap.put("russian", "koi8r");
/* 679 */     tempMap.put("serbian", "cp1250");
/* 680 */     tempMap.put("slovak", "latin2");
/* 681 */     tempMap.put("spanish", "latin1");
/* 682 */     tempMap.put("swedish", "latin1");
/* 683 */     tempMap.put("ukrainian", "koi8u");
/*     */ 
/* 685 */     ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(tempMap);
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.CharsetMapping
 * JD-Core Version:    0.6.0
 */