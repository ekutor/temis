package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.util.HashMap;

class EnumerateTTC extends TrueTypeFont
{
  protected String[] names;

  EnumerateTTC(String paramString)
    throws DocumentException, IOException
  {
    this.fileName = paramString;
    this.rf = new RandomAccessFileOrArray(paramString);
    findNames();
  }

  EnumerateTTC(byte[] paramArrayOfByte)
    throws DocumentException, IOException
  {
    this.fileName = "Byte array TTC";
    this.rf = new RandomAccessFileOrArray(paramArrayOfByte);
    findNames();
  }

  void findNames()
    throws DocumentException, IOException
  {
    this.tables = new HashMap();
    try
    {
      String str1 = readStandardString(4);
      if (!str1.equals("ttcf"))
        throw new DocumentException(this.fileName + " is not a valid TTC file.");
      this.rf.skipBytes(4);
      int i = this.rf.readInt();
      this.names = new String[i];
      int j = this.rf.getFilePointer();
      for (int k = 0; k < i; k++)
      {
        this.tables.clear();
        this.rf.seek(j);
        this.rf.skipBytes(k * 4);
        this.directoryOffset = this.rf.readInt();
        this.rf.seek(this.directoryOffset);
        if (this.rf.readInt() != 65536)
          throw new DocumentException(this.fileName + " is not a valid TTF file.");
        int m = this.rf.readUnsignedShort();
        this.rf.skipBytes(6);
        for (int n = 0; n < m; n++)
        {
          String str2 = readStandardString(4);
          this.rf.skipBytes(4);
          int[] arrayOfInt = new int[2];
          arrayOfInt[0] = this.rf.readInt();
          arrayOfInt[1] = this.rf.readInt();
          this.tables.put(str2, arrayOfInt);
        }
        this.names[k] = getBaseFont();
      }
    }
    finally
    {
      if (this.rf != null)
        this.rf.close();
    }
  }

  String[] getNames()
  {
    return this.names;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.EnumerateTTC
 * JD-Core Version:    0.6.0
 */