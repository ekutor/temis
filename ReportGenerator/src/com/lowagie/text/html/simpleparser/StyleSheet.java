package com.lowagie.text.html.simpleparser;

import java.util.HashMap;
import java.util.Map;

public class StyleSheet
{
  public HashMap classMap = new HashMap();
  public HashMap tagMap = new HashMap();

  public void applyStyle(String paramString, HashMap paramHashMap)
  {
    HashMap localHashMap1 = (HashMap)this.tagMap.get(paramString.toLowerCase());
    if (localHashMap1 != null)
    {
      localObject = new HashMap(localHashMap1);
      ((HashMap)localObject).putAll(paramHashMap);
      paramHashMap.putAll((Map)localObject);
    }
    Object localObject = (String)paramHashMap.get("class");
    if (localObject == null)
      return;
    localHashMap1 = (HashMap)this.classMap.get(((String)localObject).toLowerCase());
    if (localHashMap1 == null)
      return;
    paramHashMap.remove("class");
    HashMap localHashMap2 = new HashMap(localHashMap1);
    localHashMap2.putAll(paramHashMap);
    paramHashMap.putAll(localHashMap2);
  }

  public void loadStyle(String paramString, HashMap paramHashMap)
  {
    this.classMap.put(paramString.toLowerCase(), paramHashMap);
  }

  public void loadStyle(String paramString1, String paramString2, String paramString3)
  {
    paramString1 = paramString1.toLowerCase();
    HashMap localHashMap = (HashMap)this.classMap.get(paramString1);
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      this.classMap.put(paramString1, localHashMap);
    }
    localHashMap.put(paramString2, paramString3);
  }

  public void loadTagStyle(String paramString, HashMap paramHashMap)
  {
    this.tagMap.put(paramString.toLowerCase(), paramHashMap);
  }

  public void loadTagStyle(String paramString1, String paramString2, String paramString3)
  {
    paramString1 = paramString1.toLowerCase();
    HashMap localHashMap = (HashMap)this.tagMap.get(paramString1);
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      this.tagMap.put(paramString1, localHashMap);
    }
    localHashMap.put(paramString2, paramString3);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.html.simpleparser.StyleSheet
 * JD-Core Version:    0.6.0
 */