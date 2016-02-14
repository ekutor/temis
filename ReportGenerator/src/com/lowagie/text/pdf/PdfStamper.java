package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfStamper
  implements PdfViewerPreferences, PdfEncryptionSettings
{
  protected PdfStamperImp stamper;
  private HashMap moreInfo;
  private boolean hasSignature;
  private PdfSignatureAppearance sigApp;

  public PdfStamper(PdfReader paramPdfReader, OutputStream paramOutputStream)
    throws DocumentException, IOException
  {
    this.stamper = new PdfStamperImp(paramPdfReader, paramOutputStream, '\000', false);
  }

  public PdfStamper(PdfReader paramPdfReader, OutputStream paramOutputStream, char paramChar)
    throws DocumentException, IOException
  {
    this.stamper = new PdfStamperImp(paramPdfReader, paramOutputStream, paramChar, false);
  }

  public PdfStamper(PdfReader paramPdfReader, OutputStream paramOutputStream, char paramChar, boolean paramBoolean)
    throws DocumentException, IOException
  {
    this.stamper = new PdfStamperImp(paramPdfReader, paramOutputStream, paramChar, paramBoolean);
  }

  public HashMap getMoreInfo()
  {
    return this.moreInfo;
  }

  public void setMoreInfo(HashMap paramHashMap)
  {
    this.moreInfo = paramHashMap;
  }

  public void replacePage(PdfReader paramPdfReader, int paramInt1, int paramInt2)
  {
    this.stamper.replacePage(paramPdfReader, paramInt1, paramInt2);
  }

  public void insertPage(int paramInt, Rectangle paramRectangle)
  {
    this.stamper.insertPage(paramInt, paramRectangle);
  }

  public PdfSignatureAppearance getSignatureAppearance()
  {
    return this.sigApp;
  }

  public void close()
    throws DocumentException, IOException
  {
    if (!this.hasSignature)
    {
      this.stamper.close(this.moreInfo);
      return;
    }
    this.sigApp.preClose();
    PdfSigGenericPKCS localPdfSigGenericPKCS = this.sigApp.getSigStandard();
    PdfLiteral localPdfLiteral = (PdfLiteral)localPdfSigGenericPKCS.get(PdfName.CONTENTS);
    int i = (localPdfLiteral.getPosLength() - 2) / 2;
    byte[] arrayOfByte1 = new byte[8192];
    InputStream localInputStream = this.sigApp.getRangeStream();
    try
    {
      int j;
      while ((j = localInputStream.read(arrayOfByte1)) > 0)
        localPdfSigGenericPKCS.getSigner().update(arrayOfByte1, 0, j);
    }
    catch (SignatureException localSignatureException)
    {
      throw new ExceptionConverter(localSignatureException);
    }
    arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = localPdfSigGenericPKCS.getSignerContents();
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, arrayOfByte2.length);
    PdfString localPdfString = new PdfString(arrayOfByte1);
    localPdfString.setHexWriting(true);
    PdfDictionary localPdfDictionary = new PdfDictionary();
    localPdfDictionary.put(PdfName.CONTENTS, localPdfString);
    this.sigApp.close(localPdfDictionary);
    this.stamper.reader.close();
  }

  public PdfContentByte getUnderContent(int paramInt)
  {
    return this.stamper.getUnderContent(paramInt);
  }

  public PdfContentByte getOverContent(int paramInt)
  {
    return this.stamper.getOverContent(paramInt);
  }

  public boolean isRotateContents()
  {
    return this.stamper.isRotateContents();
  }

  public void setRotateContents(boolean paramBoolean)
  {
    this.stamper.setRotateContents(paramBoolean);
  }

  public void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, boolean paramBoolean)
    throws DocumentException
  {
    if (this.stamper.isAppend())
      throw new DocumentException("Append mode does not support changing the encryption status.");
    if (this.stamper.isContentWritten())
      throw new DocumentException("Content was already written to the output.");
    this.stamper.setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt, paramBoolean ? 1 : 0);
  }

  public void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
    throws DocumentException
  {
    if (this.stamper.isAppend())
      throw new DocumentException("Append mode does not support changing the encryption status.");
    if (this.stamper.isContentWritten())
      throw new DocumentException("Content was already written to the output.");
    this.stamper.setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2);
  }

  public void setEncryption(boolean paramBoolean, String paramString1, String paramString2, int paramInt)
    throws DocumentException
  {
    setEncryption(DocWriter.getISOBytes(paramString1), DocWriter.getISOBytes(paramString2), paramInt, paramBoolean);
  }

  public void setEncryption(int paramInt1, String paramString1, String paramString2, int paramInt2)
    throws DocumentException
  {
    setEncryption(DocWriter.getISOBytes(paramString1), DocWriter.getISOBytes(paramString2), paramInt2, paramInt1);
  }

  public void setEncryption(Certificate[] paramArrayOfCertificate, int[] paramArrayOfInt, int paramInt)
    throws DocumentException
  {
    if (this.stamper.isAppend())
      throw new DocumentException("Append mode does not support changing the encryption status.");
    if (this.stamper.isContentWritten())
      throw new DocumentException("Content was already written to the output.");
    this.stamper.setEncryption(paramArrayOfCertificate, paramArrayOfInt, paramInt);
  }

  public PdfImportedPage getImportedPage(PdfReader paramPdfReader, int paramInt)
  {
    return this.stamper.getImportedPage(paramPdfReader, paramInt);
  }

  public PdfWriter getWriter()
  {
    return this.stamper;
  }

  public PdfReader getReader()
  {
    return this.stamper.reader;
  }

  public AcroFields getAcroFields()
  {
    return this.stamper.getAcroFields();
  }

  public void setFormFlattening(boolean paramBoolean)
  {
    this.stamper.setFormFlattening(paramBoolean);
  }

  public void setFreeTextFlattening(boolean paramBoolean)
  {
    this.stamper.setFreeTextFlattening(paramBoolean);
  }

  public void addAnnotation(PdfAnnotation paramPdfAnnotation, int paramInt)
  {
    this.stamper.addAnnotation(paramPdfAnnotation, paramInt);
  }

  public PdfFormField addSignature(String paramString, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    PdfAcroForm localPdfAcroForm = this.stamper.getAcroForm();
    PdfFormField localPdfFormField = PdfFormField.createSignature(this.stamper);
    localPdfAcroForm.setSignatureParams(localPdfFormField, paramString, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    localPdfAcroForm.drawSignatureAppearences(localPdfFormField, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    addAnnotation(localPdfFormField, paramInt);
    return localPdfFormField;
  }

  public void addComments(FdfReader paramFdfReader)
    throws IOException
  {
    this.stamper.addComments(paramFdfReader);
  }

  public void setOutlines(List paramList)
  {
    this.stamper.setOutlines(paramList);
  }

  public void setThumbnail(Image paramImage, int paramInt)
    throws PdfException, DocumentException
  {
    this.stamper.setThumbnail(paramImage, paramInt);
  }

  public boolean partialFormFlattening(String paramString)
  {
    return this.stamper.partialFormFlattening(paramString);
  }

  public void addJavaScript(String paramString)
  {
    this.stamper.addJavaScript(paramString, !PdfEncodings.isPdfDocEncoding(paramString));
  }

  public void addFileAttachment(String paramString1, byte[] paramArrayOfByte, String paramString2, String paramString3)
    throws IOException
  {
    addFileAttachment(paramString1, PdfFileSpecification.fileEmbedded(this.stamper, paramString2, paramString3, paramArrayOfByte));
  }

  public void addFileAttachment(String paramString, PdfFileSpecification paramPdfFileSpecification)
    throws IOException
  {
    this.stamper.addFileAttachment(paramString, paramPdfFileSpecification);
  }

  public void makePackage(PdfName paramPdfName)
  {
    PdfCollection localPdfCollection = new PdfCollection(0);
    localPdfCollection.put(PdfName.VIEW, paramPdfName);
    this.stamper.makePackage(localPdfCollection);
  }

  public void makePackage(PdfCollection paramPdfCollection)
  {
    this.stamper.makePackage(paramPdfCollection);
  }

  public void setViewerPreferences(int paramInt)
  {
    this.stamper.setViewerPreferences(paramInt);
  }

  public void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    this.stamper.addViewerPreference(paramPdfName, paramPdfObject);
  }

  public void setXmpMetadata(byte[] paramArrayOfByte)
  {
    this.stamper.setXmpMetadata(paramArrayOfByte);
  }

  public boolean isFullCompression()
  {
    return this.stamper.isFullCompression();
  }

  public void setFullCompression()
  {
    if (this.stamper.isAppend())
      return;
    this.stamper.setFullCompression();
  }

  public void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction, int paramInt)
    throws PdfException
  {
    this.stamper.setPageAction(paramPdfName, paramPdfAction, paramInt);
  }

  public void setDuration(int paramInt1, int paramInt2)
  {
    this.stamper.setDuration(paramInt1, paramInt2);
  }

  public void setTransition(PdfTransition paramPdfTransition, int paramInt)
  {
    this.stamper.setTransition(paramPdfTransition, paramInt);
  }

  public static PdfStamper createSignature(PdfReader paramPdfReader, OutputStream paramOutputStream, char paramChar, File paramFile, boolean paramBoolean)
    throws DocumentException, IOException
  {
    PdfStamper localPdfStamper;
    if (paramFile == null)
    {
      localObject = new ByteBuffer();
      localPdfStamper = new PdfStamper(paramPdfReader, (OutputStream)localObject, paramChar, paramBoolean);
      localPdfStamper.sigApp = new PdfSignatureAppearance(localPdfStamper.stamper);
      localPdfStamper.sigApp.setSigout((ByteBuffer)localObject);
    }
    else
    {
      if (paramFile.isDirectory())
        paramFile = File.createTempFile("pdf", null, paramFile);
      localObject = new FileOutputStream(paramFile);
      localPdfStamper = new PdfStamper(paramPdfReader, (OutputStream)localObject, paramChar, paramBoolean);
      localPdfStamper.sigApp = new PdfSignatureAppearance(localPdfStamper.stamper);
      localPdfStamper.sigApp.setTempFile(paramFile);
    }
    localPdfStamper.sigApp.setOriginalout(paramOutputStream);
    localPdfStamper.sigApp.setStamper(localPdfStamper);
    localPdfStamper.hasSignature = true;
    Object localObject = paramPdfReader.getCatalog();
    PdfDictionary localPdfDictionary = (PdfDictionary)PdfReader.getPdfObject(((PdfDictionary)localObject).get(PdfName.ACROFORM), (PdfObject)localObject);
    if (localPdfDictionary != null)
    {
      localPdfDictionary.remove(PdfName.NEEDAPPEARANCES);
      localPdfStamper.stamper.markUsed(localPdfDictionary);
    }
    return (PdfStamper)localPdfStamper;
  }

  public static PdfStamper createSignature(PdfReader paramPdfReader, OutputStream paramOutputStream, char paramChar)
    throws DocumentException, IOException
  {
    return createSignature(paramPdfReader, paramOutputStream, paramChar, null, false);
  }

  public static PdfStamper createSignature(PdfReader paramPdfReader, OutputStream paramOutputStream, char paramChar, File paramFile)
    throws DocumentException, IOException
  {
    return createSignature(paramPdfReader, paramOutputStream, paramChar, paramFile, false);
  }

  public Map getPdfLayers()
  {
    return this.stamper.getPdfLayers();
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfStamper
 * JD-Core Version:    0.6.0
 */