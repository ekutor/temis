package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class PdfSignatureAppearance
{
  public static final int SignatureRenderDescription = 0;
  public static final int SignatureRenderNameAndDescription = 1;
  public static final int SignatureRenderGraphicAndDescription = 2;
  public static final PdfName SELF_SIGNED = PdfName.ADOBE_PPKLITE;
  public static final PdfName VERISIGN_SIGNED = PdfName.VERISIGN_PPKVS;
  public static final PdfName WINCER_SIGNED = PdfName.ADOBE_PPKMS;
  public static final int NOT_CERTIFIED = 0;
  public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;
  public static final int CERTIFIED_FORM_FILLING = 2;
  public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;
  private static final float TOP_SECTION = 0.3F;
  private static final float MARGIN = 2.0F;
  private Rectangle rect;
  private Rectangle pageRect;
  private PdfTemplate[] app = new PdfTemplate[5];
  private PdfTemplate frm;
  private PdfStamperImp writer;
  private String layer2Text;
  private String reason;
  private String location;
  private Calendar signDate;
  private String provider;
  private int page = 1;
  private String fieldName;
  private PrivateKey privKey;
  private Certificate[] certChain;
  private CRL[] crlList;
  private PdfName filter;
  private boolean newField;
  private ByteBuffer sigout;
  private OutputStream originalout;
  private File tempFile;
  private PdfDictionary cryptoDictionary;
  private PdfStamper stamper;
  private boolean preClosed = false;
  private PdfSigGenericPKCS sigStandard;
  private int[] range;
  private RandomAccessFile raf;
  private byte[] bout;
  private int boutLen;
  private byte[] externalDigest;
  private byte[] externalRSAdata;
  private String digestEncryptionAlgorithm;
  private HashMap exclusionLocations;
  private int render = 0;
  private Image signatureGraphic = null;
  public static final String questionMark = "% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n";
  private String contact;
  private Font layer2Font;
  private String layer4Text;
  private boolean acro6Layers;
  private int runDirection = 1;
  private SignatureEvent signatureEvent;
  private Image image;
  private float imageScale;
  private int certificationLevel = 0;

  PdfSignatureAppearance(PdfStamperImp paramPdfStamperImp)
  {
    this.writer = paramPdfStamperImp;
    this.signDate = new GregorianCalendar();
    this.fieldName = getNewSigName();
  }

  public int getRender()
  {
    return this.render;
  }

  public void setRender(int paramInt)
  {
    this.render = paramInt;
  }

  public Image getSignatureGraphic()
  {
    return this.signatureGraphic;
  }

  public void setSignatureGraphic(Image paramImage)
  {
    this.signatureGraphic = paramImage;
  }

  public void setLayer2Text(String paramString)
  {
    this.layer2Text = paramString;
  }

  public String getLayer2Text()
  {
    return this.layer2Text;
  }

  public void setLayer4Text(String paramString)
  {
    this.layer4Text = paramString;
  }

  public String getLayer4Text()
  {
    return this.layer4Text;
  }

  public Rectangle getRect()
  {
    return this.rect;
  }

  public boolean isInvisible()
  {
    return (this.rect == null) || (this.rect.getWidth() == 0.0F) || (this.rect.getHeight() == 0.0F);
  }

  public void setCrypto(PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate, CRL[] paramArrayOfCRL, PdfName paramPdfName)
  {
    this.privKey = paramPrivateKey;
    this.certChain = paramArrayOfCertificate;
    this.crlList = paramArrayOfCRL;
    this.filter = paramPdfName;
  }

  public void setVisibleSignature(Rectangle paramRectangle, int paramInt, String paramString)
  {
    if (paramString != null)
    {
      if (paramString.indexOf('.') >= 0)
        throw new IllegalArgumentException("Field names cannot contain a dot.");
      AcroFields localAcroFields = this.writer.getAcroFields();
      AcroFields.Item localItem = localAcroFields.getFieldItem(paramString);
      if (localItem != null)
        throw new IllegalArgumentException("The field " + paramString + " already exists.");
      this.fieldName = paramString;
    }
    if ((paramInt < 1) || (paramInt > this.writer.reader.getNumberOfPages()))
      throw new IllegalArgumentException("Invalid page number: " + paramInt);
    this.pageRect = new Rectangle(paramRectangle);
    this.pageRect.normalize();
    this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
    this.page = paramInt;
    this.newField = true;
  }

  public void setVisibleSignature(String paramString)
  {
    AcroFields localAcroFields = this.writer.getAcroFields();
    AcroFields.Item localItem = localAcroFields.getFieldItem(paramString);
    if (localItem == null)
      throw new IllegalArgumentException("The field " + paramString + " does not exist.");
    PdfDictionary localPdfDictionary = localItem.getMerged(0);
    if (!PdfName.SIG.equals(PdfReader.getPdfObject(localPdfDictionary.get(PdfName.FT))))
      throw new IllegalArgumentException("The field " + paramString + " is not a signature field.");
    this.fieldName = paramString;
    PdfArray localPdfArray = localPdfDictionary.getAsArray(PdfName.RECT);
    float f1 = localPdfArray.getAsNumber(0).floatValue();
    float f2 = localPdfArray.getAsNumber(1).floatValue();
    float f3 = localPdfArray.getAsNumber(2).floatValue();
    float f4 = localPdfArray.getAsNumber(3).floatValue();
    this.pageRect = new Rectangle(f1, f2, f3, f4);
    this.pageRect.normalize();
    this.page = localItem.getPage(0).intValue();
    int i = this.writer.reader.getPageRotation(this.page);
    Rectangle localRectangle = this.writer.reader.getPageSizeWithRotation(this.page);
    switch (i)
    {
    case 90:
      this.pageRect = new Rectangle(this.pageRect.getBottom(), localRectangle.getTop() - this.pageRect.getLeft(), this.pageRect.getTop(), localRectangle.getTop() - this.pageRect.getRight());
      break;
    case 180:
      this.pageRect = new Rectangle(localRectangle.getRight() - this.pageRect.getLeft(), localRectangle.getTop() - this.pageRect.getBottom(), localRectangle.getRight() - this.pageRect.getRight(), localRectangle.getTop() - this.pageRect.getTop());
      break;
    case 270:
      this.pageRect = new Rectangle(localRectangle.getRight() - this.pageRect.getBottom(), this.pageRect.getLeft(), localRectangle.getRight() - this.pageRect.getTop(), this.pageRect.getRight());
    }
    if (i != 0)
      this.pageRect.normalize();
    this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
  }

  public PdfTemplate getLayer(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.app.length))
      return null;
    PdfTemplate localPdfTemplate = this.app[paramInt];
    if (localPdfTemplate == null)
    {
      localPdfTemplate = this.app[paramInt] =  = new PdfTemplate(this.writer);
      localPdfTemplate.setBoundingBox(this.rect);
      this.writer.addDirectTemplateSimple(localPdfTemplate, new PdfName("n" + paramInt));
    }
    return localPdfTemplate;
  }

  public PdfTemplate getTopLayer()
  {
    if (this.frm == null)
    {
      this.frm = new PdfTemplate(this.writer);
      this.frm.setBoundingBox(this.rect);
      this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
    }
    return this.frm;
  }

  public PdfTemplate getAppearance()
    throws DocumentException
  {
    Object localObject1;
    if (isInvisible())
    {
      localObject1 = new PdfTemplate(this.writer);
      ((PdfTemplate)localObject1).setBoundingBox(new Rectangle(0.0F, 0.0F));
      this.writer.addDirectTemplateSimple((PdfTemplate)localObject1, null);
      return localObject1;
    }
    if (this.app[0] == null)
    {
      localObject1 = this.app[0] =  = new PdfTemplate(this.writer);
      ((PdfTemplate)localObject1).setBoundingBox(new Rectangle(100.0F, 100.0F));
      this.writer.addDirectTemplateSimple((PdfTemplate)localObject1, new PdfName("n0"));
      ((PdfTemplate)localObject1).setLiteral("% DSBlank\n");
    }
    if ((this.app[1] == null) && (!this.acro6Layers))
    {
      localObject1 = this.app[1] =  = new PdfTemplate(this.writer);
      ((PdfTemplate)localObject1).setBoundingBox(new Rectangle(100.0F, 100.0F));
      this.writer.addDirectTemplateSimple((PdfTemplate)localObject1, new PdfName("n1"));
      ((PdfTemplate)localObject1).setLiteral("% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n");
    }
    Rectangle localRectangle;
    Object localObject3;
    if (this.app[2] == null)
    {
      if (this.layer2Text == null)
      {
        localObject2 = new StringBuffer();
        ((StringBuffer)localObject2).append("Digitally signed by ").append(PdfPKCS7.getSubjectFields((X509Certificate)this.certChain[0]).getField("CN")).append('\n');
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        ((StringBuffer)localObject2).append("Date: ").append(localSimpleDateFormat.format(this.signDate.getTime()));
        if (this.reason != null)
          ((StringBuffer)localObject2).append('\n').append("Reason: ").append(this.reason);
        if (this.location != null)
          ((StringBuffer)localObject2).append('\n').append("Location: ").append(this.location);
        localObject1 = ((StringBuffer)localObject2).toString();
      }
      else
      {
        localObject1 = this.layer2Text;
      }
      localObject2 = this.app[2] =  = new PdfTemplate(this.writer);
      ((PdfTemplate)localObject2).setBoundingBox(this.rect);
      this.writer.addDirectTemplateSimple((PdfTemplate)localObject2, new PdfName("n2"));
      if (this.image != null)
        if (this.imageScale == 0.0F)
        {
          ((PdfTemplate)localObject2).addImage(this.image, this.rect.getWidth(), 0.0F, 0.0F, this.rect.getHeight(), 0.0F, 0.0F);
        }
        else
        {
          float f2 = this.imageScale;
          if (this.imageScale < 0.0F)
            f2 = Math.min(this.rect.getWidth() / this.image.getWidth(), this.rect.getHeight() / this.image.getHeight());
          f4 = this.image.getWidth() * f2;
          float f6 = this.image.getHeight() * f2;
          float f8 = (this.rect.getWidth() - f4) / 2.0F;
          float f10 = (this.rect.getHeight() - f6) / 2.0F;
          ((PdfTemplate)localObject2).addImage(this.image, f4, 0.0F, 0.0F, f6, f8, f10);
        }
      Font localFont;
      if (this.layer2Font == null)
        localFont = new Font();
      else
        localFont = new Font(this.layer2Font);
      float f4 = localFont.getSize();
      localRectangle = null;
      localObject3 = null;
      if ((this.render == 1) || ((this.render == 2) && (this.signatureGraphic != null)))
      {
        localObject3 = new Rectangle(2.0F, 2.0F, this.rect.getWidth() / 2.0F - 2.0F, this.rect.getHeight() - 2.0F);
        localRectangle = new Rectangle(this.rect.getWidth() / 2.0F + 1.0F, 2.0F, this.rect.getWidth() - 1.0F, this.rect.getHeight() - 2.0F);
        if (this.rect.getHeight() > this.rect.getWidth())
        {
          localObject3 = new Rectangle(2.0F, this.rect.getHeight() / 2.0F, this.rect.getWidth() - 2.0F, this.rect.getHeight());
          localRectangle = new Rectangle(2.0F, 2.0F, this.rect.getWidth() - 2.0F, this.rect.getHeight() / 2.0F - 2.0F);
        }
      }
      else
      {
        localRectangle = new Rectangle(2.0F, 2.0F, this.rect.getWidth() - 2.0F, this.rect.getHeight() * 0.7F - 2.0F);
      }
      Object localObject5;
      if (this.render == 1)
      {
        localObject4 = PdfPKCS7.getSubjectFields((X509Certificate)this.certChain[0]).getField("CN");
        localObject5 = new Rectangle(((Rectangle)localObject3).getWidth() - 2.0F, ((Rectangle)localObject3).getHeight() - 2.0F);
        float f11 = fitText(localFont, (String)localObject4, (Rectangle)localObject5, -1.0F, this.runDirection);
        ColumnText localColumnText = new ColumnText((PdfContentByte)localObject2);
        localColumnText.setRunDirection(this.runDirection);
        localColumnText.setSimpleColumn(new Phrase((String)localObject4, localFont), ((Rectangle)localObject3).getLeft(), ((Rectangle)localObject3).getBottom(), ((Rectangle)localObject3).getRight(), ((Rectangle)localObject3).getTop(), f11, 0);
        localColumnText.go();
      }
      else if (this.render == 2)
      {
        localObject4 = new ColumnText((PdfContentByte)localObject2);
        ((ColumnText)localObject4).setRunDirection(this.runDirection);
        ((ColumnText)localObject4).setSimpleColumn(((Rectangle)localObject3).getLeft(), ((Rectangle)localObject3).getBottom(), ((Rectangle)localObject3).getRight(), ((Rectangle)localObject3).getTop(), 0.0F, 2);
        localObject5 = Image.getInstance(this.signatureGraphic);
        ((Image)localObject5).scaleToFit(((Rectangle)localObject3).getWidth(), ((Rectangle)localObject3).getHeight());
        Paragraph localParagraph = new Paragraph();
        float f12 = 0.0F;
        float f13 = -((Image)localObject5).getScaledHeight() + 15.0F;
        f12 += (((Rectangle)localObject3).getWidth() - ((Image)localObject5).getScaledWidth()) / 2.0F;
        f13 -= (((Rectangle)localObject3).getHeight() - ((Image)localObject5).getScaledHeight()) / 2.0F;
        localParagraph.add(new Chunk((Image)localObject5, f12 + (((Rectangle)localObject3).getWidth() - ((Image)localObject5).getScaledWidth()) / 2.0F, f13, false));
        ((ColumnText)localObject4).addElement(localParagraph);
        ((ColumnText)localObject4).go();
      }
      if (f4 <= 0.0F)
      {
        localObject4 = new Rectangle(localRectangle.getWidth(), localRectangle.getHeight());
        f4 = fitText(localFont, (String)localObject1, (Rectangle)localObject4, 12.0F, this.runDirection);
      }
      Object localObject4 = new ColumnText((PdfContentByte)localObject2);
      ((ColumnText)localObject4).setRunDirection(this.runDirection);
      ((ColumnText)localObject4).setSimpleColumn(new Phrase((String)localObject1, localFont), localRectangle.getLeft(), localRectangle.getBottom(), localRectangle.getRight(), localRectangle.getTop(), f4, 0);
      ((ColumnText)localObject4).go();
    }
    if ((this.app[3] == null) && (!this.acro6Layers))
    {
      localObject1 = this.app[3] =  = new PdfTemplate(this.writer);
      ((PdfTemplate)localObject1).setBoundingBox(new Rectangle(100.0F, 100.0F));
      this.writer.addDirectTemplateSimple((PdfTemplate)localObject1, new PdfName("n3"));
      ((PdfTemplate)localObject1).setLiteral("% DSBlank\n");
    }
    if ((this.app[4] == null) && (!this.acro6Layers))
    {
      localObject1 = this.app[4] =  = new PdfTemplate(this.writer);
      ((PdfTemplate)localObject1).setBoundingBox(new Rectangle(0.0F, this.rect.getHeight() * 0.7F, this.rect.getRight(), this.rect.getTop()));
      this.writer.addDirectTemplateSimple((PdfTemplate)localObject1, new PdfName("n4"));
      if (this.layer2Font == null)
        localObject2 = new Font();
      else
        localObject2 = new Font(this.layer2Font);
      f3 = ((Font)localObject2).getSize();
      String str = "Signature Not Verified";
      if (this.layer4Text != null)
        str = this.layer4Text;
      localRectangle = new Rectangle(this.rect.getWidth() - 4.0F, this.rect.getHeight() * 0.3F - 4.0F);
      f3 = fitText((Font)localObject2, str, localRectangle, 15.0F, this.runDirection);
      localObject3 = new ColumnText((PdfContentByte)localObject1);
      ((ColumnText)localObject3).setRunDirection(this.runDirection);
      ((ColumnText)localObject3).setSimpleColumn(new Phrase(str, (Font)localObject2), 2.0F, 0.0F, this.rect.getWidth() - 2.0F, this.rect.getHeight() - 2.0F, f3, 0);
      ((ColumnText)localObject3).go();
    }
    float f1 = this.writer.reader.getPageRotation(this.page);
    Object localObject2 = new Rectangle(this.rect);
    for (float f3 = f1; f3 > 0; f3 -= 90)
      localObject2 = ((Rectangle)localObject2).rotate();
    if (this.frm == null)
    {
      this.frm = new PdfTemplate(this.writer);
      this.frm.setBoundingBox((Rectangle)localObject2);
      this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
      float f5 = Math.min(this.rect.getWidth(), this.rect.getHeight()) * 0.9F;
      float f7 = (this.rect.getWidth() - f5) / 2.0F;
      float f9 = (this.rect.getHeight() - f5) / 2.0F;
      f5 /= 100.0F;
      if (f1 == 90)
        this.frm.concatCTM(0.0F, 1.0F, -1.0F, 0.0F, this.rect.getHeight(), 0.0F);
      else if (f1 == 180)
        this.frm.concatCTM(-1.0F, 0.0F, 0.0F, -1.0F, this.rect.getWidth(), this.rect.getHeight());
      else if (f1 == 270)
        this.frm.concatCTM(0.0F, -1.0F, 1.0F, 0.0F, 0.0F, this.rect.getWidth());
      this.frm.addTemplate(this.app[0], 0.0F, 0.0F);
      if (!this.acro6Layers)
        this.frm.addTemplate(this.app[1], f5, 0.0F, 0.0F, f5, f7, f9);
      this.frm.addTemplate(this.app[2], 0.0F, 0.0F);
      if (!this.acro6Layers)
      {
        this.frm.addTemplate(this.app[3], f5, 0.0F, 0.0F, f5, f7, f9);
        this.frm.addTemplate(this.app[4], 0.0F, 0.0F);
      }
    }
    PdfTemplate localPdfTemplate = new PdfTemplate(this.writer);
    localPdfTemplate.setBoundingBox((Rectangle)localObject2);
    this.writer.addDirectTemplateSimple(localPdfTemplate, null);
    localPdfTemplate.addTemplate(this.frm, 0.0F, 0.0F);
    return (PdfTemplate)(PdfTemplate)(PdfTemplate)(PdfTemplate)(PdfTemplate)localPdfTemplate;
  }

  public static float fitText(Font paramFont, String paramString, Rectangle paramRectangle, float paramFloat, int paramInt)
  {
    try
    {
      ColumnText localColumnText = null;
      int i = 0;
      if (paramFloat <= 0.0F)
      {
        int j = 0;
        int k = 0;
        char[] arrayOfChar = paramString.toCharArray();
        for (int m = 0; m < arrayOfChar.length; m++)
          if (arrayOfChar[m] == '\n')
          {
            k++;
          }
          else
          {
            if (arrayOfChar[m] != '\r')
              continue;
            j++;
          }
        m = Math.max(j, k) + 1;
        paramFloat = Math.abs(paramRectangle.getHeight()) / m - 0.001F;
      }
      paramFont.setSize(paramFloat);
      Phrase localPhrase = new Phrase(paramString, paramFont);
      localColumnText = new ColumnText(null);
      localColumnText.setSimpleColumn(localPhrase, paramRectangle.getLeft(), paramRectangle.getBottom(), paramRectangle.getRight(), paramRectangle.getTop(), paramFloat, 0);
      localColumnText.setRunDirection(paramInt);
      i = localColumnText.go(true);
      if ((i & 0x1) != 0)
        return paramFloat;
      float f1 = 0.1F;
      float f2 = 0.0F;
      float f3 = paramFloat;
      float f4 = paramFloat;
      for (int n = 0; n < 50; n++)
      {
        f4 = (f2 + f3) / 2.0F;
        localColumnText = new ColumnText(null);
        paramFont.setSize(f4);
        localColumnText.setSimpleColumn(new Phrase(paramString, paramFont), paramRectangle.getLeft(), paramRectangle.getBottom(), paramRectangle.getRight(), paramRectangle.getTop(), f4, 0);
        localColumnText.setRunDirection(paramInt);
        i = localColumnText.go(true);
        if ((i & 0x1) != 0)
        {
          if (f3 - f2 < f4 * f1)
            return f4;
          f2 = f4;
        }
        else
        {
          f3 = f4;
        }
      }
      return f4;
    }
    catch (Exception localException)
    {
    }
    throw new ExceptionConverter(localException);
  }

  public void setExternalDigest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString)
  {
    this.externalDigest = paramArrayOfByte1;
    this.externalRSAdata = paramArrayOfByte2;
    this.digestEncryptionAlgorithm = paramString;
  }

  public String getReason()
  {
    return this.reason;
  }

  public void setReason(String paramString)
  {
    this.reason = paramString;
  }

  public String getLocation()
  {
    return this.location;
  }

  public void setLocation(String paramString)
  {
    this.location = paramString;
  }

  public String getProvider()
  {
    return this.provider;
  }

  public void setProvider(String paramString)
  {
    this.provider = paramString;
  }

  public PrivateKey getPrivKey()
  {
    return this.privKey;
  }

  public Certificate[] getCertChain()
  {
    return this.certChain;
  }

  public CRL[] getCrlList()
  {
    return this.crlList;
  }

  public PdfName getFilter()
  {
    return this.filter;
  }

  public boolean isNewField()
  {
    return this.newField;
  }

  public int getPage()
  {
    return this.page;
  }

  public String getFieldName()
  {
    return this.fieldName;
  }

  public Rectangle getPageRect()
  {
    return this.pageRect;
  }

  public Calendar getSignDate()
  {
    return this.signDate;
  }

  public void setSignDate(Calendar paramCalendar)
  {
    this.signDate = paramCalendar;
  }

  ByteBuffer getSigout()
  {
    return this.sigout;
  }

  void setSigout(ByteBuffer paramByteBuffer)
  {
    this.sigout = paramByteBuffer;
  }

  OutputStream getOriginalout()
  {
    return this.originalout;
  }

  void setOriginalout(OutputStream paramOutputStream)
  {
    this.originalout = paramOutputStream;
  }

  public File getTempFile()
  {
    return this.tempFile;
  }

  void setTempFile(File paramFile)
  {
    this.tempFile = paramFile;
  }

  public String getNewSigName()
  {
    AcroFields localAcroFields = this.writer.getAcroFields();
    String str1 = "Signature";
    int i = 0;
    int j = 0;
    while (true)
    {
      if (j != 0)
        break label133;
      i++;
      String str2 = str1 + i;
      if (localAcroFields.getFieldItem(str2) != null)
        continue;
      str2 = str2 + ".";
      j = 1;
      Iterator localIterator = localAcroFields.getFields().keySet().iterator();
      if (!localIterator.hasNext())
        continue;
      String str3 = (String)localIterator.next();
      if (!str3.startsWith(str2))
        break;
      j = 0;
    }
    label133: str1 = str1 + i;
    return str1;
  }

  public void preClose()
    throws IOException, DocumentException
  {
    preClose(null);
  }

  public void preClose(HashMap paramHashMap)
    throws IOException, DocumentException
  {
    if (this.preClosed)
      throw new DocumentException("Document already pre closed.");
    this.preClosed = true;
    AcroFields localAcroFields = this.writer.getAcroFields();
    String str = getFieldName();
    int i = (!isInvisible()) && (!isNewField()) ? 1 : 0;
    PdfIndirectReference localPdfIndirectReference = this.writer.getPdfIndirectReference();
    this.writer.setSigFlags(3);
    Object localObject1;
    Object localObject4;
    if (i != 0)
    {
      localObject1 = localAcroFields.getFieldItem(str).getWidget(0);
      this.writer.markUsed((PdfObject)localObject1);
      ((PdfDictionary)localObject1).put(PdfName.P, this.writer.getPageReference(getPage()));
      ((PdfDictionary)localObject1).put(PdfName.V, localPdfIndirectReference);
      PdfObject localPdfObject = PdfReader.getPdfObjectRelease(((PdfDictionary)localObject1).get(PdfName.F));
      int n = 0;
      if ((localPdfObject != null) && (localPdfObject.isNumber()))
        n = ((PdfNumber)localPdfObject).intValue();
      n |= 128;
      ((PdfDictionary)localObject1).put(PdfName.F, new PdfNumber(n));
      localObject4 = new PdfDictionary();
      ((PdfDictionary)localObject4).put(PdfName.N, getAppearance().getIndirectReference());
      ((PdfDictionary)localObject1).put(PdfName.AP, (PdfObject)localObject4);
    }
    else
    {
      localObject1 = PdfFormField.createSignature(this.writer);
      ((PdfFormField)localObject1).setFieldName(str);
      ((PdfFormField)localObject1).put(PdfName.V, localPdfIndirectReference);
      ((PdfFormField)localObject1).setFlags(132);
      int k = getPage();
      if (!isInvisible())
        ((PdfFormField)localObject1).setWidget(getPageRect(), null);
      else
        ((PdfFormField)localObject1).setWidget(new Rectangle(0.0F, 0.0F), null);
      ((PdfFormField)localObject1).setAppearance(PdfAnnotation.APPEARANCE_NORMAL, getAppearance());
      ((PdfFormField)localObject1).setPage(k);
      this.writer.addAnnotation((PdfAnnotation)localObject1, k);
    }
    this.exclusionLocations = new HashMap();
    Object localObject2;
    if (this.cryptoDictionary == null)
    {
      if (PdfName.ADOBE_PPKLITE.equals(getFilter()))
        this.sigStandard = new PdfSigGenericPKCS.PPKLite(getProvider());
      else if (PdfName.ADOBE_PPKMS.equals(getFilter()))
        this.sigStandard = new PdfSigGenericPKCS.PPKMS(getProvider());
      else if (PdfName.VERISIGN_PPKVS.equals(getFilter()))
        this.sigStandard = new PdfSigGenericPKCS.VeriSign(getProvider());
      else
        throw new IllegalArgumentException("Unknown filter: " + getFilter());
      this.sigStandard.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
      if (getReason() != null)
        this.sigStandard.setReason(getReason());
      if (getLocation() != null)
        this.sigStandard.setLocation(getLocation());
      if (getContact() != null)
        this.sigStandard.setContact(getContact());
      this.sigStandard.put(PdfName.M, new PdfDate(getSignDate()));
      this.sigStandard.setSignInfo(getPrivKey(), getCertChain(), getCrlList());
      localObject1 = (PdfString)this.sigStandard.get(PdfName.CONTENTS);
      localObject2 = new PdfLiteral((((PdfString)localObject1).toString().length() + (PdfName.ADOBE_PPKLITE.equals(getFilter()) ? 0 : 64)) * 2 + 2);
      this.exclusionLocations.put(PdfName.CONTENTS, localObject2);
      this.sigStandard.put(PdfName.CONTENTS, (PdfObject)localObject2);
      localObject2 = new PdfLiteral(80);
      this.exclusionLocations.put(PdfName.BYTERANGE, localObject2);
      this.sigStandard.put(PdfName.BYTERANGE, (PdfObject)localObject2);
      if (this.certificationLevel > 0)
        addDocMDP(this.sigStandard);
      if (this.signatureEvent != null)
        this.signatureEvent.getSignatureDictionary(this.sigStandard);
      this.writer.addToBody(this.sigStandard, localPdfIndirectReference, false);
    }
    else
    {
      localObject1 = new PdfLiteral(80);
      this.exclusionLocations.put(PdfName.BYTERANGE, localObject1);
      this.cryptoDictionary.put(PdfName.BYTERANGE, (PdfObject)localObject1);
      localObject2 = paramHashMap.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        localObject4 = (PdfName)((Map.Entry)localObject3).getKey();
        Integer localInteger = (Integer)((Map.Entry)localObject3).getValue();
        localObject1 = new PdfLiteral(localInteger.intValue());
        this.exclusionLocations.put(localObject4, localObject1);
        this.cryptoDictionary.put((PdfName)localObject4, (PdfObject)localObject1);
      }
      if (this.certificationLevel > 0)
        addDocMDP(this.cryptoDictionary);
      if (this.signatureEvent != null)
        this.signatureEvent.getSignatureDictionary(this.cryptoDictionary);
      this.writer.addToBody(this.cryptoDictionary, localPdfIndirectReference, false);
    }
    if (this.certificationLevel > 0)
    {
      localObject1 = new PdfDictionary();
      ((PdfDictionary)localObject1).put(new PdfName("DocMDP"), localPdfIndirectReference);
      this.writer.reader.getCatalog().put(new PdfName("Perms"), (PdfObject)localObject1);
    }
    this.writer.close(this.stamper.getMoreInfo());
    this.range = new int[this.exclusionLocations.size() * 2];
    int j = ((PdfLiteral)this.exclusionLocations.get(PdfName.BYTERANGE)).getPosition();
    this.exclusionLocations.remove(PdfName.BYTERANGE);
    int m = 1;
    Object localObject3 = this.exclusionLocations.values().iterator();
    int i4;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (PdfLiteral)((Iterator)localObject3).next();
      i4 = ((PdfLiteral)localObject4).getPosition();
      this.range[(m++)] = i4;
      this.range[(m++)] = (((PdfLiteral)localObject4).getPosLength() + i4);
    }
    Arrays.sort(this.range, 1, this.range.length - 1);
    for (int i1 = 3; i1 < this.range.length - 2; i1 += 2)
      this.range[i1] -= this.range[(i1 - 1)];
    if (this.tempFile == null)
    {
      this.bout = this.sigout.getBuffer();
      this.boutLen = this.sigout.size();
      this.range[(this.range.length - 1)] = (this.boutLen - this.range[(this.range.length - 2)]);
      ByteBuffer localByteBuffer1 = new ByteBuffer();
      localByteBuffer1.append('[');
      for (int i3 = 0; i3 < this.range.length; i3++)
        localByteBuffer1.append(this.range[i3]).append(' ');
      localByteBuffer1.append(']');
      System.arraycopy(localByteBuffer1.getBuffer(), 0, this.bout, j, localByteBuffer1.size());
    }
    else
    {
      try
      {
        this.raf = new RandomAccessFile(this.tempFile, "rw");
        int i2 = (int)this.raf.length();
        this.range[(this.range.length - 1)] = (i2 - this.range[(this.range.length - 2)]);
        ByteBuffer localByteBuffer2 = new ByteBuffer();
        localByteBuffer2.append('[');
        for (i4 = 0; i4 < this.range.length; i4++)
          localByteBuffer2.append(this.range[i4]).append(' ');
        localByteBuffer2.append(']');
        this.raf.seek(j);
        this.raf.write(localByteBuffer2.getBuffer(), 0, localByteBuffer2.size());
      }
      catch (IOException localIOException)
      {
        try
        {
          this.raf.close();
        }
        catch (Exception localException1)
        {
        }
        try
        {
          this.tempFile.delete();
        }
        catch (Exception localException2)
        {
        }
        throw localIOException;
      }
    }
  }

  public void close(PdfDictionary paramPdfDictionary)
    throws IOException, DocumentException
  {
    try
    {
      if (!this.preClosed)
        throw new DocumentException("preClose() must be called first.");
      ByteBuffer localByteBuffer = new ByteBuffer();
      Iterator localIterator = paramPdfDictionary.getKeys().iterator();
      Object localObject1;
      while (localIterator.hasNext())
      {
        localObject1 = (PdfName)localIterator.next();
        PdfObject localPdfObject = paramPdfDictionary.get((PdfName)localObject1);
        PdfLiteral localPdfLiteral = (PdfLiteral)this.exclusionLocations.get(localObject1);
        if (localPdfLiteral == null)
          throw new IllegalArgumentException("The key " + ((PdfName)localObject1).toString() + " didn't reserve space in preClose().");
        localByteBuffer.reset();
        localPdfObject.toPdf(null, localByteBuffer);
        if (localByteBuffer.size() > localPdfLiteral.getPosLength())
          throw new IllegalArgumentException("The key " + ((PdfName)localObject1).toString() + " is too big. Is " + localByteBuffer.size() + ", reserved " + localPdfLiteral.getPosLength());
        if (this.tempFile == null)
        {
          System.arraycopy(localByteBuffer.getBuffer(), 0, this.bout, localPdfLiteral.getPosition(), localByteBuffer.size());
          continue;
        }
        this.raf.seek(localPdfLiteral.getPosition());
        this.raf.write(localByteBuffer.getBuffer(), 0, localByteBuffer.size());
      }
      if (paramPdfDictionary.size() != this.exclusionLocations.size())
        throw new IllegalArgumentException("The update dictionary has less keys than required.");
      if (this.tempFile == null)
      {
        this.originalout.write(this.bout, 0, this.boutLen);
      }
      else if (this.originalout != null)
      {
        this.raf.seek(0L);
        int i = (int)this.raf.length();
        localObject1 = new byte[8192];
        while (i > 0)
        {
          int j = this.raf.read(localObject1, 0, Math.min(localObject1.length, i));
          if (j < 0)
            throw new EOFException("Unexpected EOF");
          this.originalout.write(localObject1, 0, j);
          i -= j;
        }
      }
    }
    finally
    {
      if (this.tempFile != null)
      {
        try
        {
          this.raf.close();
        }
        catch (Exception localException1)
        {
        }
        if (this.originalout != null)
          try
          {
            this.tempFile.delete();
          }
          catch (Exception localException2)
          {
          }
      }
      if (this.originalout != null)
        try
        {
          this.originalout.close();
        }
        catch (Exception localException3)
        {
        }
    }
  }

  private void addDocMDP(PdfDictionary paramPdfDictionary)
  {
    PdfDictionary localPdfDictionary1 = new PdfDictionary();
    PdfDictionary localPdfDictionary2 = new PdfDictionary();
    localPdfDictionary2.put(PdfName.P, new PdfNumber(this.certificationLevel));
    localPdfDictionary2.put(PdfName.V, new PdfName("1.2"));
    localPdfDictionary2.put(PdfName.TYPE, PdfName.TRANSFORMPARAMS);
    localPdfDictionary1.put(PdfName.TRANSFORMMETHOD, PdfName.DOCMDP);
    localPdfDictionary1.put(PdfName.TYPE, PdfName.SIGREF);
    localPdfDictionary1.put(PdfName.TRANSFORMPARAMS, localPdfDictionary2);
    localPdfDictionary1.put(new PdfName("DigestValue"), new PdfString("aa"));
    PdfArray localPdfArray1 = new PdfArray();
    localPdfArray1.add(new PdfNumber(0));
    localPdfArray1.add(new PdfNumber(0));
    localPdfDictionary1.put(new PdfName("DigestLocation"), localPdfArray1);
    localPdfDictionary1.put(new PdfName("DigestMethod"), new PdfName("MD5"));
    localPdfDictionary1.put(PdfName.DATA, this.writer.reader.getTrailer().get(PdfName.ROOT));
    PdfArray localPdfArray2 = new PdfArray();
    localPdfArray2.add(localPdfDictionary1);
    paramPdfDictionary.put(PdfName.REFERENCE, localPdfArray2);
  }

  public InputStream getRangeStream()
  {
    return new RangeStream(this.raf, this.bout, this.range, null);
  }

  public PdfDictionary getCryptoDictionary()
  {
    return this.cryptoDictionary;
  }

  public void setCryptoDictionary(PdfDictionary paramPdfDictionary)
  {
    this.cryptoDictionary = paramPdfDictionary;
  }

  public PdfStamper getStamper()
  {
    return this.stamper;
  }

  void setStamper(PdfStamper paramPdfStamper)
  {
    this.stamper = paramPdfStamper;
  }

  public boolean isPreClosed()
  {
    return this.preClosed;
  }

  public PdfSigGenericPKCS getSigStandard()
  {
    return this.sigStandard;
  }

  public String getContact()
  {
    return this.contact;
  }

  public void setContact(String paramString)
  {
    this.contact = paramString;
  }

  public Font getLayer2Font()
  {
    return this.layer2Font;
  }

  public void setLayer2Font(Font paramFont)
  {
    this.layer2Font = paramFont;
  }

  public boolean isAcro6Layers()
  {
    return this.acro6Layers;
  }

  public void setAcro6Layers(boolean paramBoolean)
  {
    this.acro6Layers = paramBoolean;
  }

  public void setRunDirection(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3))
      throw new RuntimeException("Invalid run direction: " + paramInt);
    this.runDirection = paramInt;
  }

  public int getRunDirection()
  {
    return this.runDirection;
  }

  public SignatureEvent getSignatureEvent()
  {
    return this.signatureEvent;
  }

  public void setSignatureEvent(SignatureEvent paramSignatureEvent)
  {
    this.signatureEvent = paramSignatureEvent;
  }

  public Image getImage()
  {
    return this.image;
  }

  public void setImage(Image paramImage)
  {
    this.image = paramImage;
  }

  public float getImageScale()
  {
    return this.imageScale;
  }

  public void setImageScale(float paramFloat)
  {
    this.imageScale = paramFloat;
  }

  public int getCertificationLevel()
  {
    return this.certificationLevel;
  }

  public void setCertificationLevel(int paramInt)
  {
    this.certificationLevel = paramInt;
  }

  public static abstract interface SignatureEvent
  {
    public abstract void getSignatureDictionary(PdfDictionary paramPdfDictionary);
  }

  private static class RangeStream extends InputStream
  {
    private byte[] b = new byte[1];
    private RandomAccessFile raf;
    private byte[] bout;
    private int[] range;
    private int rangePosition = 0;

    private RangeStream(RandomAccessFile paramRandomAccessFile, byte[] paramArrayOfByte, int[] paramArrayOfInt)
    {
      this.raf = paramRandomAccessFile;
      this.bout = paramArrayOfByte;
      this.range = paramArrayOfInt;
    }

    public int read()
      throws IOException
    {
      int i = read(this.b);
      if (i != 1)
        return -1;
      return this.b[0] & 0xFF;
    }

    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramArrayOfByte == null)
        throw new NullPointerException();
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0))
        throw new IndexOutOfBoundsException();
      if (paramInt2 == 0)
        return 0;
      if (this.rangePosition >= this.range[(this.range.length - 2)] + this.range[(this.range.length - 1)])
        return -1;
      for (int i = 0; i < this.range.length; i += 2)
      {
        int j = this.range[i];
        int k = j + this.range[(i + 1)];
        if (this.rangePosition < j)
          this.rangePosition = j;
        if ((this.rangePosition < j) || (this.rangePosition >= k))
          continue;
        int m = Math.min(paramInt2, k - this.rangePosition);
        if (this.raf == null)
        {
          System.arraycopy(this.bout, this.rangePosition, paramArrayOfByte, paramInt1, m);
        }
        else
        {
          this.raf.seek(this.rangePosition);
          this.raf.readFully(paramArrayOfByte, paramInt1, m);
        }
        this.rangePosition += m;
        return m;
      }
      return -1;
    }

    RangeStream(RandomAccessFile paramRandomAccessFile, byte[] paramArrayOfByte, int[] paramArrayOfInt, PdfSignatureAppearance.1 param1)
    {
      this(paramRandomAccessFile, paramArrayOfByte, paramArrayOfInt);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfSignatureAppearance
 * JD-Core Version:    0.6.0
 */