package com.lowagie.text.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class Pfm2afm
{
  private RandomAccessFileOrArray in;
  private PrintWriter out;
  private short vers;
  private int h_len;
  private String copyright;
  private short type;
  private short points;
  private short verres;
  private short horres;
  private short ascent;
  private short intleading;
  private short extleading;
  private byte italic;
  private byte uline;
  private byte overs;
  private short weight;
  private byte charset;
  private short pixwidth;
  private short pixheight;
  private byte kind;
  private short avgwidth;
  private short maxwidth;
  private int firstchar;
  private int lastchar;
  private byte defchar;
  private byte brkchar;
  private short widthby;
  private int device;
  private int face;
  private int bits;
  private int bitoff;
  private short extlen;
  private int psext;
  private int chartab;
  private int res1;
  private int kernpairs;
  private int res2;
  private int fontname;
  private short capheight;
  private short xheight;
  private short ascender;
  private short descender;
  private boolean isMono;
  private int[] Win2PSStd = { 0, 0, 0, 0, 197, 198, 199, 0, 202, 0, 205, 206, 207, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 169, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 193, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 0, 0, 184, 166, 185, 188, 178, 179, 195, 189, 0, 172, 234, 0, 0, 0, 0, 96, 0, 170, 186, 183, 177, 208, 196, 0, 0, 173, 250, 0, 0, 0, 0, 161, 162, 163, 168, 165, 0, 167, 200, 0, 227, 171, 0, 0, 0, 197, 0, 0, 0, 0, 194, 0, 182, 180, 203, 0, 235, 187, 0, 0, 0, 191, 0, 0, 0, 0, 0, 0, 225, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 233, 0, 0, 0, 0, 0, 0, 251, 0, 0, 0, 0, 0, 0, 241, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 249, 0, 0, 0, 0, 0, 0, 0 };
  private int[] WinClass = { 0, 0, 0, 0, 2, 2, 2, 0, 2, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
  private String[] WinChars = { "W00", "W01", "W02", "W03", "macron", "breve", "dotaccent", "W07", "ring", "W09", "W0a", "W0b", "W0c", "W0d", "W0e", "W0f", "hungarumlaut", "ogonek", "caron", "W13", "W14", "W15", "W16", "W17", "W18", "W19", "W1a", "W1b", "W1c", "W1d", "W1e", "W1f", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quotesingle", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "grave", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "W7f", "euro", "W81", "quotesinglbase", "florin", "quotedblbase", "ellipsis", "dagger", "daggerdbl", "circumflex", "perthousand", "Scaron", "guilsinglleft", "OE", "W8d", "Zcaron", "W8f", "W90", "quoteleft", "quoteright", "quotedblleft", "quotedblright", "bullet", "endash", "emdash", "tilde", "trademark", "scaron", "guilsinglright", "oe", "W9d", "zcaron", "Ydieresis", "reqspace", "exclamdown", "cent", "sterling", "currency", "yen", "brokenbar", "section", "dieresis", "copyright", "ordfeminine", "guillemotleft", "logicalnot", "syllable", "registered", "macron", "degree", "plusminus", "twosuperior", "threesuperior", "acute", "mu", "paragraph", "periodcentered", "cedilla", "onesuperior", "ordmasculine", "guillemotright", "onequarter", "onehalf", "threequarters", "questiondown", "Agrave", "Aacute", "Acircumflex", "Atilde", "Adieresis", "Aring", "AE", "Ccedilla", "Egrave", "Eacute", "Ecircumflex", "Edieresis", "Igrave", "Iacute", "Icircumflex", "Idieresis", "Eth", "Ntilde", "Ograve", "Oacute", "Ocircumflex", "Otilde", "Odieresis", "multiply", "Oslash", "Ugrave", "Uacute", "Ucircumflex", "Udieresis", "Yacute", "Thorn", "germandbls", "agrave", "aacute", "acircumflex", "atilde", "adieresis", "aring", "ae", "ccedilla", "egrave", "eacute", "ecircumflex", "edieresis", "igrave", "iacute", "icircumflex", "idieresis", "eth", "ntilde", "ograve", "oacute", "ocircumflex", "otilde", "odieresis", "divide", "oslash", "ugrave", "uacute", "ucircumflex", "udieresis", "yacute", "thorn", "ydieresis" };

  private Pfm2afm(RandomAccessFileOrArray paramRandomAccessFileOrArray, OutputStream paramOutputStream)
    throws IOException
  {
    this.in = paramRandomAccessFileOrArray;
    this.out = new PrintWriter(new OutputStreamWriter(paramOutputStream, "ISO-8859-1"));
  }

  public static void convert(RandomAccessFileOrArray paramRandomAccessFileOrArray, OutputStream paramOutputStream)
    throws IOException
  {
    Pfm2afm localPfm2afm = new Pfm2afm(paramRandomAccessFileOrArray, paramOutputStream);
    localPfm2afm.openpfm();
    localPfm2afm.putheader();
    localPfm2afm.putchartab();
    localPfm2afm.putkerntab();
    localPfm2afm.puttrailer();
    localPfm2afm.out.flush();
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      RandomAccessFileOrArray localRandomAccessFileOrArray = new RandomAccessFileOrArray(paramArrayOfString[0]);
      FileOutputStream localFileOutputStream = new FileOutputStream(paramArrayOfString[1]);
      convert(localRandomAccessFileOrArray, localFileOutputStream);
      localRandomAccessFileOrArray.close();
      localFileOutputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  private String readString(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    this.in.readFully(arrayOfByte);
    for (int i = 0; (i < arrayOfByte.length) && (arrayOfByte[i] != 0); i++);
    return new String(arrayOfByte, 0, i, "ISO-8859-1");
  }

  private String readString()
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    while (true)
    {
      int i = this.in.read();
      if (i <= 0)
        break;
      localStringBuffer.append((char)i);
    }
    return localStringBuffer.toString();
  }

  private void outval(int paramInt)
  {
    this.out.print(' ');
    this.out.print(paramInt);
  }

  private void outchar(int paramInt1, int paramInt2, String paramString)
  {
    this.out.print("C ");
    outval(paramInt1);
    this.out.print(" ; WX ");
    outval(paramInt2);
    if (paramString != null)
    {
      this.out.print(" ; N ");
      this.out.print(paramString);
    }
    this.out.print(" ;\n");
  }

  private void openpfm()
    throws IOException
  {
    this.in.seek(0);
    this.vers = this.in.readShortLE();
    this.h_len = this.in.readIntLE();
    this.copyright = readString(60);
    this.type = this.in.readShortLE();
    this.points = this.in.readShortLE();
    this.verres = this.in.readShortLE();
    this.horres = this.in.readShortLE();
    this.ascent = this.in.readShortLE();
    this.intleading = this.in.readShortLE();
    this.extleading = this.in.readShortLE();
    this.italic = (byte)this.in.read();
    this.uline = (byte)this.in.read();
    this.overs = (byte)this.in.read();
    this.weight = this.in.readShortLE();
    this.charset = (byte)this.in.read();
    this.pixwidth = this.in.readShortLE();
    this.pixheight = this.in.readShortLE();
    this.kind = (byte)this.in.read();
    this.avgwidth = this.in.readShortLE();
    this.maxwidth = this.in.readShortLE();
    this.firstchar = this.in.read();
    this.lastchar = this.in.read();
    this.defchar = (byte)this.in.read();
    this.brkchar = (byte)this.in.read();
    this.widthby = this.in.readShortLE();
    this.device = this.in.readIntLE();
    this.face = this.in.readIntLE();
    this.bits = this.in.readIntLE();
    this.bitoff = this.in.readIntLE();
    this.extlen = this.in.readShortLE();
    this.psext = this.in.readIntLE();
    this.chartab = this.in.readIntLE();
    this.res1 = this.in.readIntLE();
    this.kernpairs = this.in.readIntLE();
    this.res2 = this.in.readIntLE();
    this.fontname = this.in.readIntLE();
    if ((this.h_len != this.in.length()) || (this.extlen != 30) || (this.fontname < 75) || (this.fontname > 512))
      throw new IOException("Not a valid PFM file.");
    this.in.seek(this.psext + 14);
    this.capheight = this.in.readShortLE();
    this.xheight = this.in.readShortLE();
    this.ascender = this.in.readShortLE();
    this.descender = this.in.readShortLE();
  }

  private void putheader()
    throws IOException
  {
    this.out.print("StartFontMetrics 2.0\n");
    if (this.copyright.length() > 0)
      this.out.print("Comment " + this.copyright + '\n');
    this.out.print("FontName ");
    this.in.seek(this.fontname);
    String str = readString();
    this.out.print(str);
    this.out.print("\nEncodingScheme ");
    if (this.charset != 0)
      this.out.print("FontSpecific\n");
    else
      this.out.print("AdobeStandardEncoding\n");
    this.out.print("FullName " + str.replace('-', ' '));
    if (this.face != 0)
    {
      this.in.seek(this.face);
      this.out.print("\nFamilyName " + readString());
    }
    this.out.print("\nWeight ");
    if ((this.weight > 475) || (str.toLowerCase().indexOf("bold") >= 0))
      this.out.print("Bold");
    else if (((this.weight < 325) && (this.weight != 0)) || (str.toLowerCase().indexOf("light") >= 0))
      this.out.print("Light");
    else if (str.toLowerCase().indexOf("black") >= 0)
      this.out.print("Black");
    else
      this.out.print("Medium");
    this.out.print("\nItalicAngle ");
    if ((this.italic != 0) || (str.toLowerCase().indexOf("italic") >= 0))
      this.out.print("-12.00");
    else
      this.out.print("0");
    this.out.print("\nIsFixedPitch ");
    if (((this.kind & 0x1) == 0) || (this.avgwidth == this.maxwidth))
    {
      this.out.print("true");
      this.isMono = true;
    }
    else
    {
      this.out.print("false");
      this.isMono = false;
    }
    this.out.print("\nFontBBox");
    if (this.isMono)
      outval(-20);
    else
      outval(-100);
    outval(-(this.descender + 5));
    outval(this.maxwidth + 10);
    outval(this.ascent + 5);
    this.out.print("\nCapHeight");
    outval(this.capheight);
    this.out.print("\nXHeight");
    outval(this.xheight);
    this.out.print("\nDescender");
    outval(this.descender);
    this.out.print("\nAscender");
    outval(this.ascender);
    this.out.print('\n');
  }

  private void putchartab()
    throws IOException
  {
    int i = this.lastchar - this.firstchar + 1;
    int[] arrayOfInt1 = new int[i];
    this.in.seek(this.chartab);
    for (int j = 0; j < i; j++)
      arrayOfInt1[j] = this.in.readUnsignedShortLE();
    int[] arrayOfInt2 = new int[256];
    if (this.charset == 0)
      for (k = this.firstchar; k <= this.lastchar; k++)
      {
        if (this.Win2PSStd[k] == 0)
          continue;
        arrayOfInt2[this.Win2PSStd[k]] = k;
      }
    this.out.print("StartCharMetrics");
    outval(i);
    this.out.print('\n');
    if (this.charset != 0)
      for (k = this.firstchar; k <= this.lastchar; k++)
      {
        if (arrayOfInt1[(k - this.firstchar)] == 0)
          continue;
        outchar(k, arrayOfInt1[(k - this.firstchar)], null);
      }
    for (int k = 0; k < 256; k++)
    {
      int m = arrayOfInt2[k];
      if (m == 0)
        continue;
      outchar(k, arrayOfInt1[(m - this.firstchar)], this.WinChars[m]);
      arrayOfInt1[(m - this.firstchar)] = 0;
    }
    for (k = this.firstchar; k <= this.lastchar; k++)
    {
      if (arrayOfInt1[(k - this.firstchar)] == 0)
        continue;
      outchar(-1, arrayOfInt1[(k - this.firstchar)], this.WinChars[k]);
    }
    this.out.print("EndCharMetrics\n");
  }

  private void putkerntab()
    throws IOException
  {
    if (this.kernpairs == 0)
      return;
    this.in.seek(this.kernpairs);
    int i = this.in.readUnsignedShortLE();
    int j = 0;
    int[] arrayOfInt = new int[i * 3];
    int k = 0;
    while (k < arrayOfInt.length)
    {
      arrayOfInt[(k++)] = this.in.read();
      arrayOfInt[(k++)] = this.in.read();
      if ((arrayOfInt[(k++)] = this.in.readShortLE()) == 0)
        continue;
      j++;
    }
    if (j == 0)
      return;
    this.out.print("StartKernData\nStartKernPairs");
    outval(j);
    this.out.print('\n');
    for (k = 0; k < arrayOfInt.length; k += 3)
    {
      if (arrayOfInt[(k + 2)] == 0)
        continue;
      this.out.print("KPX ");
      this.out.print(this.WinChars[arrayOfInt[k]]);
      this.out.print(' ');
      this.out.print(this.WinChars[arrayOfInt[(k + 1)]]);
      outval(arrayOfInt[(k + 2)]);
      this.out.print('\n');
    }
    this.out.print("EndKernPairs\nEndKernData\n");
  }

  private void puttrailer()
  {
    this.out.print("EndFontMetrics\n");
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.Pfm2afm
 * JD-Core Version:    0.6.0
 */