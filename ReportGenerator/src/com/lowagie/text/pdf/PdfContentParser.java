package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;

public class PdfContentParser
{
  public static final int COMMAND_TYPE = 200;
  private PRTokeniser tokeniser;

  public PdfContentParser(PRTokeniser paramPRTokeniser)
  {
    this.tokeniser = paramPRTokeniser;
  }

  public ArrayList parse(ArrayList paramArrayList)
    throws IOException
  {
    if (paramArrayList == null)
      paramArrayList = new ArrayList();
    else
      paramArrayList.clear();
    PdfObject localPdfObject = null;
    while ((localPdfObject = readPRObject()) != null)
    {
      paramArrayList.add(localPdfObject);
      if (localPdfObject.type() != 200)
        continue;
    }
    return paramArrayList;
  }

  public PRTokeniser getTokeniser()
  {
    return this.tokeniser;
  }

  public void setTokeniser(PRTokeniser paramPRTokeniser)
  {
    this.tokeniser = paramPRTokeniser;
  }

  public PdfDictionary readDictionary()
    throws IOException
  {
    PdfDictionary localPdfDictionary = new PdfDictionary();
    while (true)
    {
      if (!nextValidToken())
        throw new IOException("Unexpected end of file.");
      if (this.tokeniser.getTokenType() == 8)
        break;
      if (this.tokeniser.getTokenType() != 3)
        throw new IOException("Dictionary key is not a name.");
      PdfName localPdfName = new PdfName(this.tokeniser.getStringValue(), false);
      PdfObject localPdfObject = readPRObject();
      int i = localPdfObject.type();
      if (-i == 8)
        throw new IOException("Unexpected '>>'");
      if (-i == 6)
        throw new IOException("Unexpected ']'");
      localPdfDictionary.put(localPdfName, localPdfObject);
    }
    return localPdfDictionary;
  }

  public PdfArray readArray()
    throws IOException
  {
    PdfArray localPdfArray = new PdfArray();
    while (true)
    {
      PdfObject localPdfObject = readPRObject();
      int i = localPdfObject.type();
      if (-i == 6)
        break;
      if (-i == 8)
        throw new IOException("Unexpected '>>'");
      localPdfArray.add(localPdfObject);
    }
    return localPdfArray;
  }

  public PdfObject readPRObject()
    throws IOException
  {
    if (!nextValidToken())
      return null;
    int i = this.tokeniser.getTokenType();
    Object localObject;
    switch (i)
    {
    case 7:
      localObject = readDictionary();
      return localObject;
    case 5:
      return readArray();
    case 2:
      localObject = new PdfString(this.tokeniser.getStringValue(), null).setHexWriting(this.tokeniser.isHexString());
      return localObject;
    case 3:
      return new PdfName(this.tokeniser.getStringValue(), false);
    case 1:
      return new PdfNumber(this.tokeniser.getStringValue());
    case 10:
      return new PdfLiteral(200, this.tokeniser.getStringValue());
    case 4:
    case 6:
    case 8:
    case 9:
    }
    return (PdfObject)new PdfLiteral(-i, this.tokeniser.getStringValue());
  }

  public boolean nextValidToken()
    throws IOException
  {
    while (this.tokeniser.nextToken())
      if (this.tokeniser.getTokenType() != 4)
        return true;
    return false;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfContentParser
 * JD-Core Version:    0.6.0
 */