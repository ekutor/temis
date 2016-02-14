package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Utilities;
import java.util.ArrayList;

public class FontSelector
{
  protected ArrayList fonts = new ArrayList();

  public void addFont(Font paramFont)
  {
    if (paramFont.getBaseFont() != null)
    {
      this.fonts.add(paramFont);
      return;
    }
    BaseFont localBaseFont = paramFont.getCalculatedBaseFont(true);
    Font localFont = new Font(localBaseFont, paramFont.getSize(), paramFont.getCalculatedStyle(), paramFont.getColor());
    this.fonts.add(localFont);
  }

  public Phrase process(String paramString)
  {
    int i = this.fonts.size();
    if (i == 0)
      throw new IndexOutOfBoundsException("No font is defined.");
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length;
    StringBuffer localStringBuffer = new StringBuffer();
    Font localFont = null;
    int k = -1;
    Phrase localPhrase = new Phrase();
    for (int m = 0; m < j; m++)
    {
      char c = arrayOfChar[m];
      if ((c == '\n') || (c == '\r'))
      {
        localStringBuffer.append(c);
      }
      else
      {
        if (Utilities.isSurrogatePair(arrayOfChar, m))
        {
          n = Utilities.convertToUtf32(arrayOfChar, m);
          for (int i1 = 0; i1 < i; i1++)
          {
            localFont = (Font)this.fonts.get(i1);
            if (!localFont.getBaseFont().charExists(n))
              continue;
            if (k != i1)
            {
              if ((localStringBuffer.length() > 0) && (k != -1))
              {
                Chunk localChunk3 = new Chunk(localStringBuffer.toString(), (Font)this.fonts.get(k));
                localPhrase.add(localChunk3);
                localStringBuffer.setLength(0);
              }
              k = i1;
            }
            localStringBuffer.append(c);
            m++;
            localStringBuffer.append(arrayOfChar[m]);
            break;
          }
        }
        for (int n = 0; n < i; n++)
        {
          localFont = (Font)this.fonts.get(n);
          if (!localFont.getBaseFont().charExists(c))
            continue;
          if (k != n)
          {
            if ((localStringBuffer.length() > 0) && (k != -1))
            {
              Chunk localChunk2 = new Chunk(localStringBuffer.toString(), (Font)this.fonts.get(k));
              localPhrase.add(localChunk2);
              localStringBuffer.setLength(0);
            }
            k = n;
          }
          localStringBuffer.append(c);
          break;
        }
      }
    }
    if (localStringBuffer.length() > 0)
    {
      Chunk localChunk1 = new Chunk(localStringBuffer.toString(), (Font)this.fonts.get(k == -1 ? 0 : k));
      localPhrase.add(localChunk1);
    }
    return localPhrase;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.FontSelector
 * JD-Core Version:    0.6.0
 */