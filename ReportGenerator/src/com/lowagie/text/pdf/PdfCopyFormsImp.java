package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

class PdfCopyFormsImp extends PdfCopyFieldsImp
{
  PdfCopyFormsImp(OutputStream paramOutputStream)
    throws DocumentException
  {
    super(paramOutputStream);
  }

  public void copyDocumentFields(PdfReader paramPdfReader)
    throws DocumentException
  {
    if (!paramPdfReader.isOpenedWithFullPermissions())
      throw new IllegalArgumentException("PdfReader not opened with owner password");
    if (this.readers2intrefs.containsKey(paramPdfReader))
    {
      paramPdfReader = new PdfReader(paramPdfReader);
    }
    else
    {
      if (paramPdfReader.isTampered())
        throw new DocumentException("The document was reused.");
      paramPdfReader.consolidateNamedDestinations();
      paramPdfReader.setTampered(true);
    }
    paramPdfReader.shuffleSubsetNames();
    this.readers2intrefs.put(paramPdfReader, new IntHashtable());
    this.fields.add(paramPdfReader.getAcroFields());
    updateCalculationOrder(paramPdfReader);
  }

  void mergeFields()
  {
    for (int i = 0; i < this.fields.size(); i++)
    {
      HashMap localHashMap = ((AcroFields)this.fields.get(i)).getFields();
      mergeWithMaster(localHashMap);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.PdfCopyFormsImp
 * JD-Core Version:    0.6.0
 */