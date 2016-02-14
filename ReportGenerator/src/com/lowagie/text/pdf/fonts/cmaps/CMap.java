package com.lowagie.text.pdf.fonts.cmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMap
{
  private List codeSpaceRanges = new ArrayList();
  private Map singleByteMappings = new HashMap();
  private Map doubleByteMappings = new HashMap();

  public boolean hasOneByteMappings()
  {
    return !this.singleByteMappings.isEmpty();
  }

  public boolean hasTwoByteMappings()
  {
    return !this.doubleByteMappings.isEmpty();
  }

  public String lookup(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    String str = null;
    Integer localInteger = null;
    if (paramInt2 == 1)
    {
      localInteger = new Integer((paramArrayOfByte[paramInt1] + 256) % 256);
      str = (String)this.singleByteMappings.get(localInteger);
    }
    else if (paramInt2 == 2)
    {
      int i = (paramArrayOfByte[paramInt1] + 256) % 256;
      i <<= 8;
      i += (paramArrayOfByte[(paramInt1 + 1)] + 256) % 256;
      localInteger = new Integer(i);
      str = (String)this.doubleByteMappings.get(localInteger);
    }
    return str;
  }

  public void addMapping(byte[] paramArrayOfByte, String paramString)
    throws IOException
  {
    if (paramArrayOfByte.length == 1)
    {
      this.singleByteMappings.put(new Integer(paramArrayOfByte[0]), paramString);
    }
    else if (paramArrayOfByte.length == 2)
    {
      int i = paramArrayOfByte[0] & 0xFF;
      i <<= 8;
      i |= paramArrayOfByte[1] & 0xFF;
      this.doubleByteMappings.put(new Integer(i), paramString);
    }
    else
    {
      throw new IOException("Mapping code should be 1 or two bytes and not " + paramArrayOfByte.length);
    }
  }

  public void addCodespaceRange(CodespaceRange paramCodespaceRange)
  {
    this.codeSpaceRanges.add(paramCodespaceRange);
  }

  public List getCodeSpaceRanges()
  {
    return this.codeSpaceRanges;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.fonts.cmaps.CMap
 * JD-Core Version:    0.6.0
 */