package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.List;

public class PdfCopyFields
  implements PdfViewerPreferences, PdfEncryptionSettings
{
  private PdfCopyFieldsImp fc;

  public PdfCopyFields(OutputStream paramOutputStream)
    throws DocumentException
  {
    this.fc = new PdfCopyFieldsImp(paramOutputStream);
  }

  public PdfCopyFields(OutputStream paramOutputStream, char paramChar)
    throws DocumentException
  {
    this.fc = new PdfCopyFieldsImp(paramOutputStream, paramChar);
  }

  public void addDocument(PdfReader paramPdfReader)
    throws DocumentException, IOException
  {
    this.fc.addDocument(paramPdfReader);
  }

  public void addDocument(PdfReader paramPdfReader, List paramList)
    throws DocumentException, IOException
  {
    this.fc.addDocument(paramPdfReader, paramList);
  }

  public void addDocument(PdfReader paramPdfReader, String paramString)
    throws DocumentException, IOException
  {
    this.fc.addDocument(paramPdfReader, SequenceList.expand(paramString, paramPdfReader.getNumberOfPages()));
  }

  public void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, boolean paramBoolean)
    throws DocumentException
  {
    this.fc.setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt, paramBoolean ? 1 : 0);
  }

  public void setEncryption(boolean paramBoolean, String paramString1, String paramString2, int paramInt)
    throws DocumentException
  {
    setEncryption(DocWriter.getISOBytes(paramString1), DocWriter.getISOBytes(paramString2), paramInt, paramBoolean);
  }

  public void close()
  {
    this.fc.close();
  }

  public void open()
  {
    this.fc.openDoc();
  }

  public void addJavaScript(String paramString)
  {
    this.fc.addJavaScript(paramString, !PdfEncodings.isPdfDocEncoding(paramString));
  }

  public void setOutlines(List paramList)
  {
    this.fc.setOutlines(paramList);
  }

  public PdfWriter getWriter()
  {
    return this.fc;
  }

  public boolean isFullCompression()
  {
    return this.fc.isFullCompression();
  }

  public void setFullCompression()
  {
    this.fc.setFullCompression();
  }

  public void setEncryption(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
    throws DocumentException
  {
    this.fc.setEncryption(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2);
  }

  public void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject)
  {
    this.fc.addViewerPreference(paramPdfName, paramPdfObject);
  }

  public void setViewerPreferences(int paramInt)
  {
    this.fc.setViewerPreferences(paramInt);
  }

  public void setEncryption(Certificate[] paramArrayOfCertificate, int[] paramArrayOfInt, int paramInt)
    throws DocumentException
  {
    this.fc.setEncryption(paramArrayOfCertificate, paramArrayOfInt, paramInt);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfCopyFields
 * JD-Core Version:    0.6.0
 */