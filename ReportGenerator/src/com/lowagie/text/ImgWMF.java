package com.lowagie.text;

import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaDo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImgWMF extends Image
{
  ImgWMF(Image paramImage)
  {
    super(paramImage);
  }

  public ImgWMF(URL paramURL)
    throws BadElementException, IOException
  {
    super(paramURL);
    processParameters();
  }

  public ImgWMF(String paramString)
    throws BadElementException, MalformedURLException, IOException
  {
    this(Utilities.toURL(paramString));
  }

  public ImgWMF(byte[] paramArrayOfByte)
    throws BadElementException, IOException
  {
    super((URL)null);
    this.rawData = paramArrayOfByte;
    this.originalData = paramArrayOfByte;
    processParameters();
  }

  private void processParameters()
    throws BadElementException, IOException
  {
    this.type = 35;
    this.originalType = 6;
    Object localObject1 = null;
    try
    {
      String str;
      if (this.rawData == null)
      {
        localObject1 = this.url.openStream();
        str = this.url.toString();
      }
      else
      {
        localObject1 = new ByteArrayInputStream(this.rawData);
        str = "Byte array";
      }
      InputMeta localInputMeta = new InputMeta((InputStream)localObject1);
      if (localInputMeta.readInt() != -1698247209)
        throw new BadElementException(str + " is not a valid placeable windows metafile.");
      localInputMeta.readWord();
      int i = localInputMeta.readShort();
      int j = localInputMeta.readShort();
      int k = localInputMeta.readShort();
      int m = localInputMeta.readShort();
      int n = localInputMeta.readWord();
      this.dpiX = 72;
      this.dpiY = 72;
      this.scaledHeight = ((m - j) / n * 72.0F);
      setTop(this.scaledHeight);
      this.scaledWidth = ((k - i) / n * 72.0F);
      setRight(this.scaledWidth);
    }
    finally
    {
      if (localObject1 != null)
        ((InputStream)localObject1).close();
      this.plainWidth = getWidth();
      this.plainHeight = getHeight();
    }
  }

  public void readWMF(PdfTemplate paramPdfTemplate)
    throws IOException, DocumentException
  {
    setTemplateData(paramPdfTemplate);
    paramPdfTemplate.setWidth(getWidth());
    paramPdfTemplate.setHeight(getHeight());
    Object localObject1 = null;
    try
    {
      if (this.rawData == null)
        localObject1 = this.url.openStream();
      else
        localObject1 = new ByteArrayInputStream(this.rawData);
      MetaDo localMetaDo = new MetaDo((InputStream)localObject1, paramPdfTemplate);
      localMetaDo.readAll();
    }
    finally
    {
      if (localObject1 != null)
        ((InputStream)localObject1).close();
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.ImgWMF
 * JD-Core Version:    0.6.0
 */