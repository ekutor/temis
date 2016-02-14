/*     */ package com.co.hsg.generator.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.security.CodeSource;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import javax.xml.datatype.DatatypeConfigurationException;
/*     */ import javax.xml.datatype.DatatypeFactory;
/*     */ import javax.xml.datatype.XMLGregorianCalendar;
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   public static String fechaInicial;
/*     */   public static String fechaFinal;
/* 402 */   private static final String[] unidades = { "", "uno", "dos", "tres", 
/* 403 */     "cuarto", "cinco", "seis", "siete", "ocho", "nueve", "diez", 
/* 404 */     "once", "doce", "trece", "catorce", "quince", "dieciseis", 
/* 405 */     "diecisiete", "dieciocho", "diecinueve" };
/*     */ 
/* 407 */   private static final String[] cientos = { "", "ciento", "doscientos", "trescientos", 
/* 408 */     "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos" };
/*     */ 
/* 410 */   private static final String[] decenas = { "", "diez", "veinte", "treinta", "cuarenta", 
/* 411 */     "cincuenta", "sesenta", "setenta", "ochenta", "noventa" };
/*     */ 
/* 413 */   private static final String[] miles = { "mil", "millon", "billon" };
/*     */ 
/*     */   public static String getMeetingDate(Calendar date)
/*     */   {
/*  35 */     SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
/*  36 */     SimpleDateFormat stime = new SimpleDateFormat("HHmmss");
/*  37 */     String fecha = sd.format(date.getTime());
/*  38 */     fecha = fecha + "T" + stime.format(date.getTime());
/*  39 */     System.out.println("Fecha Calculada " + fecha);
/*  40 */     return fecha;
/*     */   }
/*     */ 
/*     */   public static Calendar convertToCal(String date) {
/*  44 */     SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
/*  45 */     Calendar c = Calendar.getInstance();
/*     */     try
/*     */     {
/*  48 */       c.setTime(sd.parse(date));
/*     */     } catch (ParseException e) {
/*  50 */       e.printStackTrace();
/*     */     }
/*  52 */     return c;
/*     */   }
/*     */ 
/*     */   public static Calendar convertToCalMeet(String date) {
/*  56 */     System.out.println("Fecha BD " + date);
/*  57 */     SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy hh:mma");
/*  58 */     Calendar c = Calendar.getInstance();
/*     */     try
/*     */     {
/*  61 */       c.setTime(sd.parse(date));
/*     */     } catch (ParseException e) {
/*  63 */       e.printStackTrace();
/*     */     }
/*  65 */     return c;
/*     */   }
/*     */ 
/*     */   public static Calendar getInitDate(int time) {
/*  69 */     SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
/*  70 */     Calendar c = Calendar.getInstance();
/*     */ 
/*  72 */     int mes = c.get(2);
/*  73 */     int anho = c.get(1);
/*     */ 
/*  80 */     c.set(1, anho);
/*  81 */     c.set(2, mes);
/*  82 */     if (time == 0)
/*  83 */       c.set(5, 1);
/*     */     else {
/*  85 */       c.add(5, -3);
/*     */     }
/*     */ 
/*  88 */     fechaInicial = sd.format(c.getTime());
/*     */ 
/*  90 */     return c;
/*     */   }
/*     */ 
/*     */   public static String getInitDateString(Calendar c) {
/*  94 */     SimpleDateFormat sd = new SimpleDateFormat("MM-dd-Y H:mm:00");
/*  95 */     return sd.format(c.getTime());
/*     */   }
/*     */ 
/*     */   public static String getInitDateDBSugar()
/*     */   {
/* 104 */     Calendar c = Calendar.getInstance();
/* 105 */     c.add(10, 5);
/* 106 */     return getInitDateString(c);
/*     */   }
/*     */ 
/*     */   public static Calendar getEndDate(int time)
/*     */   {
/* 111 */     SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
/* 112 */     Calendar c = Calendar.getInstance();
/* 113 */     if (time == 0) {
/* 114 */       int mes = c.get(2);
/* 115 */       int anho = c.get(1);
/* 116 */       if (mes == 11) {
/* 117 */         mes = 0;
/* 118 */         anho++;
/*     */       } else {
/* 120 */         mes++;
/*     */       }
/* 122 */       c.set(1, anho);
/* 123 */       c.set(2, mes);
/* 124 */       c.set(5, c.getActualMaximum(5));
/*     */     } else {
/* 126 */       c.add(5, 13);
/*     */     }
/*     */ 
/* 129 */     fechaFinal = sd.format(c.getTime());
/*     */ 
/* 131 */     return c;
/*     */   }
/*     */ 
/*     */   public static String getLargeDate(DateType type, int time) {
/* 135 */     SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
/* 136 */     String date = null;
/* 137 */     switch (type) {
/*     */     case END:
/* 139 */       Calendar c = getInitDate(time);
/* 140 */       date = sd.format(c.getTime());
/* 141 */       break;
/*     */     case START:
/* 143 */       Calendar c2 = getEndDate(time);
/* 144 */       date = sd.format(c2.getTime());
/*     */     }
/*     */ 
/* 148 */     return date;
/*     */   }
/*     */ 
/*     */   public static String calculateHour(Long s) {
/* 152 */     Calendar c = Calendar.getInstance(
/* 153 */       TimeZone.getTimeZone("America/Bogota"));
/* 154 */     c.setTimeInMillis(s.longValue());
/* 155 */     SimpleDateFormat sf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
/* 156 */     c.add(10, 5);
/* 157 */     return sf.format(c.getTime());
/*     */   }
/*     */ 
/*     */   public static File getParentDirPathClass() {
/* 161 */     CodeSource codeSource = Util.class.getProtectionDomain()
/* 162 */       .getCodeSource();
/* 163 */     File jarFile = null;
/* 164 */     File jarDir = null;
/*     */     try {
/* 166 */       jarFile = new File(codeSource.getLocation().toURI().getPath());
/* 167 */       jarDir = jarFile.getParentFile();
/*     */     } catch (URISyntaxException e) {
/* 169 */       System.out
/* 170 */         .println("No se puede obtener la ruta del directorio padre");
/* 171 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 174 */     return jarDir;
/*     */   }
/*     */ 
/*     */   public static File getFile(String name)
/*     */   {
/* 179 */     File dir = getParentDirPathClass();
/* 180 */     File file = null;
/* 181 */     if ((dir != null) && (dir.isDirectory())) {
/* 182 */       file = new File(dir.getAbsolutePath(), name);
/*     */     }
/* 184 */     return file;
/*     */   }
/*     */ 
/*     */   public static String getAbsolutePath(String resource) {
/* 188 */     CodeSource codeSource = Util.class.getProtectionDomain()
/* 189 */       .getCodeSource();
/* 190 */     File jarFile = null;
/* 191 */     File file = null;
/*     */     try
/*     */     {
/* 194 */       jarFile = new File(codeSource.getLocation().toURI().getPath());
/* 195 */       file = new File(jarFile.getParentFile().getAbsolutePath(), resource);
/*     */     }
/*     */     catch (URISyntaxException e) {
/* 198 */       System.out.println("No se puede obtener la ruta del archivo");
/* 199 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 202 */     return file.getAbsolutePath();
/*     */   }
/*     */ 
/*     */   public static String errorToString(Exception exc) {
/* 206 */     StringWriter sw = new StringWriter();
/* 207 */     PrintWriter pw = new PrintWriter(sw);
/*     */ 
/* 209 */     exc.printStackTrace(pw);
/*     */ 
/* 211 */     return sw.getBuffer().toString();
/*     */   }
/*     */ 
/*     */   public static String convertChars(String password) {
/* 215 */     Map converter = new HashMap();
/* 216 */     converter.put("&#039;", "'");
/* 217 */     converter.put("&#034;", "\"");
/* 218 */     converter.put("&quot;", "\"");
/* 219 */     converter.put("&apos;", "'");
/* 220 */     converter.put("&#47;", "/");
/* 221 */     converter.put("&#60;", "<");
/* 222 */     converter.put("&#61;", "=");
/* 223 */     converter.put("&#62;", ">");
/* 224 */     converter.put("&#64;", "@");
/* 225 */     converter.put("&#38;", "&");
/* 226 */     converter.put("&amp;", "&");
/*     */ 
/* 228 */     Iterator it = converter.entrySet().iterator();
/* 229 */     while (it.hasNext()) {
/* 230 */       Map.Entry data = (Map.Entry)it.next();
/* 231 */       if (password.contains((CharSequence)data.getKey())) {
/* 232 */         password = password.replace((CharSequence)data.getKey(), (CharSequence)data.getValue());
/* 233 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 237 */     return password;
/*     */   }
/*     */ 
/*     */   public static String formatearValor(String valor) {
/* 241 */     DecimalFormat format = new DecimalFormat("####.##");
/* 242 */     String r = "0";
/*     */ 
/* 244 */     if ((valor != null) && (valor.length() > 0)) {
/* 245 */       r = format.format(Integer.parseInt(valor));
/*     */     }
/* 247 */     return r;
/*     */   }
/*     */ 
/*     */   public static int quitarDecimales(String valor, int cant) {
/* 251 */     int vl = -1;
/*     */     try {
/* 253 */       if (valor.length() > cant) {
/* 254 */         String vlrsindec = valor.substring(0, valor.length() - cant);
/* 255 */         vl = Integer.parseInt(vlrsindec);
/*     */       }
/*     */     } catch (NumberFormatException localNumberFormatException) {
/*     */     }
/* 259 */     return vl;
/*     */   }
/*     */ 
/*     */   public static String getFechaStr(GregorianCalendar cal)
/*     */     throws IllegalArgumentException
/*     */   {
/* 268 */     String f = "";
/* 269 */     f = f + cal.get(5);
/* 270 */     f = f + "/";
/* 271 */     f = f + (cal.get(2) + 1);
/* 272 */     f = f + "/";
/* 273 */     f = f + cal.get(1);
/* 274 */     f = f + " ";
/*     */ 
/* 276 */     f = f + cal.get(11);
/* 277 */     f = f + ":";
/* 278 */     f = f + cal.get(12);
/* 279 */     f = f + ":";
/* 280 */     f = f + cal.get(13);
/*     */ 
/* 282 */     return f;
/*     */   }
/*     */ 
/*     */   public static String getFechaEspecial(GregorianCalendar cal)
/*     */     throws IllegalArgumentException
/*     */   {
/* 291 */     String f = "";
/*     */ 
/* 293 */     int year = cal.get(1);
/* 294 */     int mes = cal.get(2) + 1;
/* 295 */     int dia = cal.get(5);
/*     */ 
/* 297 */     f = f + year;
/* 298 */     f = f + (mes < 10 ? "0" + mes : Integer.valueOf(mes));
/* 299 */     f = f + (dia < 10 ? "0" + dia : Integer.valueOf(dia));
/*     */ 
/* 301 */     return f;
/*     */   }
/*     */ 
/*     */   public static String getHora(GregorianCalendar cal)
/*     */     throws IllegalArgumentException
/*     */   {
/* 310 */     String f = "";
/*     */ 
/* 312 */     int hora = cal.get(11);
/* 313 */     int min = cal.get(12);
/* 314 */     int seg = cal.get(13);
/*     */ 
/* 316 */     f = f + (hora < 10 ? "0" + hora : Integer.valueOf(hora));
/* 317 */     f = f + (min < 10 ? "0" + min : Integer.valueOf(min));
/* 318 */     f = f + (seg < 10 ? "0" + seg : Integer.valueOf(seg));
/*     */ 
/* 320 */     return f;
/*     */   }
/*     */ 
/*     */   public static XMLGregorianCalendar getFechaXMlGC(GregorianCalendar cal)
/*     */     throws IllegalArgumentException
/*     */   {
/* 328 */     TimeZone timeZone = TimeZone.getTimeZone("America/Bogota");
/* 329 */     cal.setTimeZone(timeZone);
/* 330 */     XMLGregorianCalendar xgc = null;
/*     */     try
/*     */     {
/* 333 */       xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
/*     */     }
/*     */     catch (DatatypeConfigurationException e)
/*     */     {
/* 337 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 340 */     return xgc;
/*     */   }
/*     */ 
/*     */   public static List<String> getDates()
/*     */   {
/* 347 */     List fechas = new ArrayList();
/* 348 */     GregorianCalendar fecha = new GregorianCalendar();
/* 349 */     String f = "";
/* 350 */     int month = fecha.get(2) + 1;
/* 351 */     int year = fecha.get(1);
/* 352 */     f = f + year;
/* 353 */     f = f + (month < 10 ? "0" + month : Integer.valueOf(month));
/* 354 */     f = f + "01";
/*     */ 
/* 356 */     fechas.add(f);
/*     */ 
/* 358 */     if (month == 1) {
/* 359 */       year--;
/* 360 */       month = 12;
/*     */     } else {
/* 362 */       month--;
/*     */     }
/* 364 */     f = year;
/* 365 */     f = f + (month < 10 ? "0" + month : Integer.valueOf(month));
/* 366 */     f = f + "01";
/* 367 */     fechas.add(f);
/*     */ 
/* 369 */     return fechas;
/*     */   }
/*     */ 
/*     */   public static String getYesterdayMonth()
/*     */   {
/* 379 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 380 */     cal.add(5, -1);
/* 381 */     cal.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
/*     */ 
/* 383 */     SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
/*     */ 
/* 385 */     String f = formatter.format(cal.getTime());
/*     */ 
/* 387 */     return f;
/*     */   }
/*     */ 
/*     */   public static String neytoString(String cANON) {
/* 391 */     String value = "";
/*     */ 
/* 395 */     return value;
/*     */   }
/*     */ 
/*     */   public static String getMonthName(Calendar c) {
/* 399 */     return c.getDisplayName(2, 2, new Locale("es"));
/*     */   }
/*     */ 
/*     */   private static String convert999(int n)
/*     */   {
/* 444 */     int n2 = n / 100;
/* 445 */     String s1 = unidades[n2];
/* 446 */     int n3 = n % 100;
/* 447 */     String s2 = convert99(n3);
/* 448 */     if (n <= 99) {
/* 449 */       return s2;
/*     */     }
/* 451 */     if (s1.equals("uno")) {
/* 452 */       s1 = "ciento";
/*     */     }
/*     */ 
/* 455 */     if (n % 100 == 0) {
/* 456 */       return "cien";
/*     */     }
/* 458 */     return s1 + " " + s2;
/*     */   }
/*     */ 
/*     */   private static String convert99(int n)
/*     */   {
/* 465 */     if (n < 20) {
/* 466 */       return unidades[n];
/*     */     }
/* 468 */     String s = decenas[(n / 10 - 2)];
/* 469 */     if (n % 10 == 0) {
/* 470 */       return s;
/*     */     }
/* 472 */     return s + " y " + unidades[(n % 10)];
/*     */   }
/*     */ 
/*     */   private static String convertLessThanOneThousand(int number)
/*     */   {
/*     */     String soFar;
/* 479 */     if (number % 100 < 20) {
/* 480 */       String soFar = unidades[(number % 100)];
/* 481 */       number /= 100;
/*     */     }
/*     */     else {
/* 484 */       soFar = unidades[(number % 10)];
/* 485 */       number /= 10;
/*     */ 
/* 487 */       soFar = decenas[(number % 10)] + soFar;
/* 488 */       number /= 10;
/*     */     }
/* 490 */     if (number == 0) return soFar;
/* 491 */     return cientos[number] + " " + soFar;
/*     */   }
/*     */ 
/*     */   public static String convertNumberToWords(String number) {
/*     */     try {
/* 496 */       return convertNumberToWords(Long.parseLong(number));
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 500 */     return "";
/*     */   }
/*     */ 
/*     */   public static String convertNumberToWords(long number)
/*     */   {
/* 505 */     if (number == 0L) return "cero";
/*     */ 
/* 507 */     String snumber = Long.toString(number);
/*     */ 
/* 510 */     String mask = "000000000000";
/* 511 */     DecimalFormat df = new DecimalFormat(mask);
/* 512 */     snumber = df.format(number);
/*     */ 
/* 515 */     int billions = Integer.parseInt(snumber.substring(0, 3));
/*     */ 
/* 517 */     int millions = Integer.parseInt(snumber.substring(3, 6));
/*     */ 
/* 519 */     int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
/*     */ 
/* 521 */     int thousands = Integer.parseInt(snumber.substring(9, 12));
/*     */     String tradBillions;
/*     */     String tradBillions;
/*     */     String tradBillions;
/* 524 */     switch (billions) {
/*     */     case 0:
/* 526 */       tradBillions = "";
/* 527 */       break;
/*     */     case 1:
/* 529 */       tradBillions = convertLessThanOneThousand(billions) + 
/* 530 */         " billion ";
/* 531 */       break;
/*     */     default:
/* 533 */       tradBillions = convertLessThanOneThousand(billions) + 
/* 534 */         " billion ";
/*     */     }
/* 536 */     String result = tradBillions;
/*     */     String tradMillions;
/*     */     String tradMillions;
/*     */     String tradMillions;
/* 539 */     switch (millions) {
/*     */     case 0:
/* 541 */       tradMillions = "";
/* 542 */       break;
/*     */     case 1:
/* 544 */       tradMillions = convertLessThanOneThousand(millions) + 
/* 545 */         " millon ";
/* 546 */       break;
/*     */     default:
/* 548 */       tradMillions = convertLessThanOneThousand(millions) + 
/* 549 */         " millon ";
/*     */     }
/* 551 */     result = result + tradMillions;
/*     */     String tradHundredThousands;
/*     */     String tradHundredThousands;
/*     */     String tradHundredThousands;
/* 554 */     switch (hundredThousands) {
/*     */     case 0:
/* 556 */       tradHundredThousands = "";
/* 557 */       break;
/*     */     case 1:
/* 559 */       tradHundredThousands = "mil ";
/* 560 */       break;
/*     */     default:
/* 562 */       tradHundredThousands = convertLessThanOneThousand(hundredThousands) + 
/* 563 */         " mil ";
/*     */     }
/* 565 */     result = result + tradHundredThousands;
/*     */ 
/* 568 */     String tradThousand = convertLessThanOneThousand(thousands);
/* 569 */     result = result + tradThousand;
/*     */ 
/* 572 */     return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 577 */     int number = 1000000;
/* 578 */     System.out.println(convertNumberToWords(number));
/*     */   }
/*     */ 
/*     */   public static enum DateType
/*     */   {
/*  29 */     START, END;
/*     */   }
/*     */ }

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.co.hsg.generator.util.Util
 * JD-Core Version:    0.6.0
 */