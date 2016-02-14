 package com.co.hsg.generator.util;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.security.CodeSource;
 import java.security.ProtectionDomain;
 import java.text.DecimalFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.GregorianCalendar;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Set;
 import java.util.TimeZone;
 import javax.xml.datatype.DatatypeConfigurationException;
 import javax.xml.datatype.DatatypeFactory;
 import javax.xml.datatype.XMLGregorianCalendar;
 
 public class Util
 {
   public static String fechaInicial;
   public static String fechaFinal;
   private static final String[] unidades = { "", "uno", "dos", "tres", 
     "cuarto", "cinco", "seis", "siete", "ocho", "nueve", "diez", 
     "once", "doce", "trece", "catorce", "quince", "dieciseis", 
     "diecisiete", "dieciocho", "diecinueve" };
 
   private static final String[] cientos = { "", "ciento", "doscientos", "trescientos", 
     "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos" };
 
   private static final String[] decenas = { "", "diez", "veinte", "treinta", "cuarenta", 
     "cincuenta", "sesenta", "setenta", "ochenta", "noventa" };
 
   private static final String[] miles = { "mil", "millon", "billon" };
 
   public static String getMeetingDate(Calendar date)
   {
     SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
     SimpleDateFormat stime = new SimpleDateFormat("HHmmss");
     String fecha = sd.format(date.getTime());
     fecha = fecha + "T" + stime.format(date.getTime());
     System.out.println("Fecha Calculada " + fecha);
     return fecha;
   }
 
   public static Calendar convertToCal(String date) {
     SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
     Calendar c = Calendar.getInstance();
     try
     {
       c.setTime(sd.parse(date));
     } catch (ParseException e) {
       e.printStackTrace();
     }
     return c;
   }
 
   public static Calendar convertToCalMeet(String date) {
     System.out.println("Fecha BD " + date);
     SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy hh:mma");
     Calendar c = Calendar.getInstance();
     try
     {
       c.setTime(sd.parse(date));
     } catch (ParseException e) {
       e.printStackTrace();
     }
     return c;
   }
 
   public static Calendar getInitDate(int time) {
     SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
     Calendar c = Calendar.getInstance();
 
     int mes = c.get(2);
     int anho = c.get(1);
 
     c.set(1, anho);
     c.set(2, mes);
     if (time == 0)
       c.set(5, 1);
     else {
       c.add(5, -3);
     }
 
     fechaInicial = sd.format(c.getTime());
 
     return c;
   }
 
   public static String getInitDateString(Calendar c) {
     SimpleDateFormat sd = new SimpleDateFormat("MM-dd-Y H:mm:00");
     return sd.format(c.getTime());
   }
 
   public static String getInitDateDBSugar()
   {
     Calendar c = Calendar.getInstance();
     c.add(10, 5);
     return getInitDateString(c);
   }
 
   public static Calendar getEndDate(int time)
   {
     SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
     Calendar c = Calendar.getInstance();
     if (time == 0) {
       int mes = c.get(2);
       int anho = c.get(1);
       if (mes == 11) {
         mes = 0;
         anho++;
       } else {
         mes++;
       }
       c.set(1, anho);
       c.set(2, mes);
       c.set(5, c.getActualMaximum(5));
     } else {
       c.add(5, 13);
     }
 
     fechaFinal = sd.format(c.getTime());
 
     return c;
   }
 
   public static String getLargeDate(DateType type, int time) {
     SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
     String date = null;
     switch (type) {
     case END:
       Calendar c = getInitDate(time);
       date = sd.format(c.getTime());
       break;
     case START:
       Calendar c2 = getEndDate(time);
       date = sd.format(c2.getTime());
     }
 
     return date;
   }
 
   public static String calculateHour(Long s) {
     Calendar c = Calendar.getInstance(
       TimeZone.getTimeZone("America/Bogota"));
     c.setTimeInMillis(s.longValue());
     SimpleDateFormat sf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
     c.add(10, 5);
     return sf.format(c.getTime());
   }
 
   public static File getParentDirPathClass() {
     CodeSource codeSource = Util.class.getProtectionDomain()
       .getCodeSource();
     File jarFile = null;
     File jarDir = null;
     try {
       jarFile = new File(codeSource.getLocation().toURI().getPath());
       jarDir = jarFile.getParentFile();
     } catch (URISyntaxException e) {
       System.out
         .println("No se puede obtener la ruta del directorio padre");
       e.printStackTrace();
     }
 
     return jarDir;
   }
 
   public static File getFile(String name)
   {
     File dir = getParentDirPathClass();
     File file = null;
     if ((dir != null) && (dir.isDirectory())) {
       file = new File(dir.getAbsolutePath(), name);
     }
     return file;
   }
 
   public static String getAbsolutePath(String resource) {
     CodeSource codeSource = Util.class.getProtectionDomain()
       .getCodeSource();
     File jarFile = null;
     File file = null;
     try
     {
       jarFile = new File(codeSource.getLocation().toURI().getPath());
       file = new File(jarFile.getParentFile().getAbsolutePath(), resource);
     }
     catch (URISyntaxException e) {
       System.out.println("No se puede obtener la ruta del archivo");
       e.printStackTrace();
     }
 
     return file.getAbsolutePath();
   }
 
   public static String errorToString(Exception exc) {
     StringWriter sw = new StringWriter();
     PrintWriter pw = new PrintWriter(sw);
 
     exc.printStackTrace(pw);
 
     return sw.getBuffer().toString();
   }
 
   public static String convertChars(String password) {
     Map converter = new HashMap();
     converter.put("&#039;", "'");
     converter.put("&#034;", "\"");
     converter.put("&quot;", "\"");
     converter.put("&apos;", "'");
     converter.put("&#47;", "/");
     converter.put("&#60;", "<");
     converter.put("&#61;", "=");
     converter.put("&#62;", ">");
     converter.put("&#64;", "@");
     converter.put("&#38;", "&");
     converter.put("&amp;", "&");
 
     Iterator it = converter.entrySet().iterator();
     while (it.hasNext()) {
       Map.Entry data = (Map.Entry)it.next();
       if (password.contains((CharSequence)data.getKey())) {
         password = password.replace((CharSequence)data.getKey(), (CharSequence)data.getValue());
         break;
       }
     }
 
     return password;
   }
 
   public static String formatearValor(String valor) {
     DecimalFormat format = new DecimalFormat("####.##");
     String r = "0";
 
     if ((valor != null) && (valor.length() > 0)) {
       r = format.format(Integer.parseInt(valor));
     }
     return r;
   }
 
   public static int quitarDecimales(String valor, int cant) {
     int vl = -1;
     try {
       if (valor.length() > cant) {
         String vlrsindec = valor.substring(0, valor.length() - cant);
         vl = Integer.parseInt(vlrsindec);
       }
     } catch (NumberFormatException localNumberFormatException) {
     }
     return vl;
   }
 
   public static String getFechaStr(GregorianCalendar cal)
     throws IllegalArgumentException
   {
     String f = "";
     f = f + cal.get(5);
     f = f + "/";
     f = f + (cal.get(2) + 1);
     f = f + "/";
     f = f + cal.get(1);
     f = f + " ";
 
     f = f + cal.get(11);
     f = f + ":";
     f = f + cal.get(12);
     f = f + ":";
     f = f + cal.get(13);
 
     return f;
   }
 
   public static String getFechaEspecial(GregorianCalendar cal)
     throws IllegalArgumentException
   {
     String f = "";
 
     int year = cal.get(1);
     int mes = cal.get(2) + 1;
     int dia = cal.get(5);
 
     f = f + year;
     f = f + (mes < 10 ? "0" + mes : Integer.valueOf(mes));
     f = f + (dia < 10 ? "0" + dia : Integer.valueOf(dia));
 
     return f;
   }
 
   public static String getHora(GregorianCalendar cal)
     throws IllegalArgumentException
   {
     String f = "";
 
     int hora = cal.get(11);
     int min = cal.get(12);
     int seg = cal.get(13);
 
     f = f + (hora < 10 ? "0" + hora : Integer.valueOf(hora));
     f = f + (min < 10 ? "0" + min : Integer.valueOf(min));
     f = f + (seg < 10 ? "0" + seg : Integer.valueOf(seg));
 
     return f;
   }
 
   public static XMLGregorianCalendar getFechaXMlGC(GregorianCalendar cal)
     throws IllegalArgumentException
   {
     TimeZone timeZone = TimeZone.getTimeZone("America/Bogota");
     cal.setTimeZone(timeZone);
     XMLGregorianCalendar xgc = null;
     try
     {
       xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
     }
     catch (DatatypeConfigurationException e)
     {
       e.printStackTrace();
     }
 
     return xgc;
   }
 
   public static List<String> getDates()
   {
     List fechas = new ArrayList();
     GregorianCalendar fecha = new GregorianCalendar();
     String f = "";
     int month = fecha.get(2) + 1;
     int year = fecha.get(1);
     f = f + year;
     f = f + (month < 10 ? "0" + month : Integer.valueOf(month));
     f = f + "01";
 
     fechas.add(f);
 
     if (month == 1) {
       year--;
       month = 12;
     } else {
       month--;
     }
     f = ""+year;
     f = f + (month < 10 ? "0" + month : Integer.valueOf(month));
     f = f + "01";
     fechas.add(f);
 
     return fechas;
   }
 
   public static String getYesterdayMonth()
   {
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     cal.add(5, -1);
     cal.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
 
     SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
 
     String f = formatter.format(cal.getTime());
 
     return f;
   }
 
   public static String neytoString(String cANON) {
     String value = "";
 
     return value;
   }
 
   public static String getMonthName(Calendar c) {
     return c.getDisplayName(2, 2, new Locale("es"));
   }
 
   private static String convert999(int n)
   {
     int n2 = n / 100;
     String s1 = unidades[n2];
     int n3 = n % 100;
     String s2 = convert99(n3);
     if (n <= 99) {
       return s2;
     }
     if (s1.equals("uno")) {
       s1 = "ciento";
     }
 
     if (n % 100 == 0) {
       return "cien";
     }
     return s1 + " " + s2;
   }
 
   private static String convert99(int n)
   {
     if (n < 20) {
       return unidades[n];
     }
     String s = decenas[(n / 10 - 2)];
     if (n % 10 == 0) {
       return s;
     }
     return s + " y " + unidades[(n % 10)];
   }
 
   private static String convertLessThanOneThousand(int number)
   {
     String soFar;
     if (number % 100 < 20) {
       soFar = unidades[(number % 100)];
       number /= 100;
     }
     else {
       soFar = unidades[(number % 10)];
       number /= 10;
 
       soFar = decenas[(number % 10)] + soFar;
       number /= 10;
     }
     if (number == 0) return soFar;
     return cientos[number] + " " + soFar;
   }
 
   public static String convertNumberToWords(String number) {
     try {
       return convertNumberToWords(Long.parseLong(number));
     }
     catch (Exception localException) {
     }
     return "";
   }
 
   public static String convertNumberToWords(long number)
   {
     if (number == 0L) return "cero";
 
     String snumber = Long.toString(number);
 
     String mask = "000000000000";
     DecimalFormat df = new DecimalFormat(mask);
     snumber = df.format(number);
 
     int billions = Integer.parseInt(snumber.substring(0, 3));
 
     int millions = Integer.parseInt(snumber.substring(3, 6));
 
     int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
 
     int thousands = Integer.parseInt(snumber.substring(9, 12));
     String tradBillions;
    

     switch (billions) {
     case 0:
       tradBillions = "";
       break;
     case 1:
       tradBillions = convertLessThanOneThousand(billions) + 
         " billion ";
       break;
     default:
       tradBillions = convertLessThanOneThousand(billions) + 
         " billion ";
     }
     String result = tradBillions;
     String tradMillions;
     switch (millions) {
     case 0:
       tradMillions = "";
       break;
     case 1:
       tradMillions = convertLessThanOneThousand(millions) + 
         " millon ";
       break;
     default:
       tradMillions = convertLessThanOneThousand(millions) + 
         " millon ";
     }
     result = result + tradMillions;
     String tradHundredThousands;
     switch (hundredThousands) {
     case 0:
       tradHundredThousands = "";
       break;
     case 1:
       tradHundredThousands = "mil ";
       break;
     default:
       tradHundredThousands = convertLessThanOneThousand(hundredThousands) + 
         " mil ";
     }
     result = result + tradHundredThousands;
 
     String tradThousand = convertLessThanOneThousand(thousands);
     result = result + tradThousand;
 
     return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
   }
 
   public static void main(String[] args)
   {
     int number = 1000000;
     System.out.println(convertNumberToWords(number));
   }
 
   public static enum DateType
   {
     START, END;
   }
 }