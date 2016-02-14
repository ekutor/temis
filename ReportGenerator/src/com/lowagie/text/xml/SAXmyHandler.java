package com.lowagie.text.xml;

import com.lowagie.text.DocListener;
import java.util.HashMap;
import java.util.Properties;
import org.xml.sax.Attributes;

public class SAXmyHandler extends SAXiTextHandler
{
  public SAXmyHandler(DocListener paramDocListener, HashMap paramHashMap)
  {
    super(paramDocListener, paramHashMap);
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    Object localObject;
    if (this.myTags.containsKey(paramString3))
    {
      localObject = (XmlPeer)this.myTags.get(paramString3);
      handleStartingTags(((XmlPeer)localObject).getTag(), ((XmlPeer)localObject).getAttributes(paramAttributes));
    }
    else
    {
      localObject = new Properties();
      if (paramAttributes != null)
        for (int i = 0; i < paramAttributes.getLength(); i++)
        {
          String str = paramAttributes.getQName(i);
          ((Properties)localObject).setProperty(str, paramAttributes.getValue(i));
        }
      handleStartingTags(paramString3, (Properties)localObject);
    }
  }

  public void endElement(String paramString1, String paramString2, String paramString3)
  {
    if (this.myTags.containsKey(paramString3))
    {
      XmlPeer localXmlPeer = (XmlPeer)this.myTags.get(paramString3);
      handleEndingTags(localXmlPeer.getTag());
    }
    else
    {
      handleEndingTags(paramString3);
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.xml.SAXmyHandler
 * JD-Core Version:    0.6.0
 */