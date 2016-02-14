package com.lowagie.text.pdf;

public class ArabicLigaturizer
{
  private static final char ALEF = 'ا';
  private static final char ALEFHAMZA = 'أ';
  private static final char ALEFHAMZABELOW = 'إ';
  private static final char ALEFMADDA = 'آ';
  private static final char LAM = 'ل';
  private static final char HAMZA = 'ء';
  private static final char TATWEEL = 'ـ';
  private static final char ZWJ = '‍';
  private static final char HAMZAABOVE = 'ٔ';
  private static final char HAMZABELOW = 'ٕ';
  private static final char WAWHAMZA = 'ؤ';
  private static final char YEHHAMZA = 'ئ';
  private static final char WAW = 'و';
  private static final char ALEFMAKSURA = 'ى';
  private static final char YEH = 'ي';
  private static final char FARSIYEH = 'ی';
  private static final char SHADDA = 'ّ';
  private static final char KASRA = 'ِ';
  private static final char FATHA = 'َ';
  private static final char DAMMA = 'ُ';
  private static final char MADDA = 'ٓ';
  private static final char LAM_ALEF = 'ﻻ';
  private static final char LAM_ALEFHAMZA = 'ﻷ';
  private static final char LAM_ALEFHAMZABELOW = 'ﻹ';
  private static final char LAM_ALEFMADDA = 'ﻵ';
  private static final char[][] chartable = { { 'ء', 65152 }, { 'آ', 65153, 65154 }, { 'أ', 65155, 65156 }, { 'ؤ', 65157, 65158 }, { 'إ', 65159, 65160 }, { 'ئ', 65161, 65162, 65163, 65164 }, { 'ا', 65165, 65166 }, { 'ب', 65167, 65168, 65169, 65170 }, { 'ة', 65171, 65172 }, { 'ت', 65173, 65174, 65175, 65176 }, { 'ث', 65177, 65178, 65179, 65180 }, { 'ج', 65181, 65182, 65183, 65184 }, { 'ح', 65185, 65186, 65187, 65188 }, { 'خ', 65189, 65190, 65191, 65192 }, { 'د', 65193, 65194 }, { 'ذ', 65195, 65196 }, { 'ر', 65197, 65198 }, { 'ز', 65199, 65200 }, { 'س', 65201, 65202, 65203, 65204 }, { 'ش', 65205, 65206, 65207, 65208 }, { 'ص', 65209, 65210, 65211, 65212 }, { 'ض', 65213, 65214, 65215, 65216 }, { 'ط', 65217, 65218, 65219, 65220 }, { 'ظ', 65221, 65222, 65223, 65224 }, { 'ع', 65225, 65226, 65227, 65228 }, { 'غ', 65229, 65230, 65231, 65232 }, { 'ـ', 'ـ', 'ـ', 'ـ', 'ـ' }, { 'ف', 65233, 65234, 65235, 65236 }, { 'ق', 65237, 65238, 65239, 65240 }, { 'ك', 65241, 65242, 65243, 65244 }, { 'ل', 65245, 65246, 65247, 65248 }, { 'م', 65249, 65250, 65251, 65252 }, { 'ن', 65253, 65254, 65255, 65256 }, { 'ه', 65257, 65258, 65259, 65260 }, { 'و', 65261, 65262 }, { 'ى', 65263, 65264, 64488, 64489 }, { 'ي', 65265, 65266, 65267, 65268 }, { 'ٱ', 64336, 64337 }, { 'ٹ', 64358, 64359, 64360, 64361 }, { 'ٺ', 64350, 64351, 64352, 64353 }, { 'ٻ', 64338, 64339, 64340, 64341 }, { 'پ', 64342, 64343, 64344, 64345 }, { 'ٿ', 64354, 64355, 64356, 64357 }, { 'ڀ', 64346, 64347, 64348, 64349 }, { 'ڃ', 64374, 64375, 64376, 64377 }, { 'ڄ', 64370, 64371, 64372, 64373 }, { 'چ', 64378, 64379, 64380, 64381 }, { 'ڇ', 64382, 64383, 64384, 64385 }, { 'ڈ', 64392, 64393 }, { 'ڌ', 64388, 64389 }, { 'ڍ', 64386, 64387 }, { 'ڎ', 64390, 64391 }, { 'ڑ', 64396, 64397 }, { 'ژ', 64394, 64395 }, { 'ڤ', 64362, 64363, 64364, 64365 }, { 'ڦ', 64366, 64367, 64368, 64369 }, { 'ک', 64398, 64399, 64400, 64401 }, { 'ڭ', 64467, 64468, 64469, 64470 }, { 'گ', 64402, 64403, 64404, 64405 }, { 'ڱ', 64410, 64411, 64412, 64413 }, { 'ڳ', 64406, 64407, 64408, 64409 }, { 'ں', 64414, 64415 }, { 'ڻ', 64416, 64417, 64418, 64419 }, { 'ھ', 64426, 64427, 64428, 64429 }, { 'ۀ', 64420, 64421 }, { 'ہ', 64422, 64423, 64424, 64425 }, { 'ۅ', 64480, 64481 }, { 'ۆ', 64473, 64474 }, { 'ۇ', 64471, 64472 }, { 'ۈ', 64475, 64476 }, { 'ۉ', 64482, 64483 }, { 'ۋ', 64478, 64479 }, { 'ی', 64508, 64509, 64510, 64511 }, { 'ې', 64484, 64485, 64486, 64487 }, { 'ے', 64430, 64431 }, { 'ۓ', 64432, 64433 } };
  public static final int ar_nothing = 0;
  public static final int ar_novowel = 1;
  public static final int ar_composedtashkeel = 4;
  public static final int ar_lig = 8;
  public static final int DIGITS_EN2AN = 32;
  public static final int DIGITS_AN2EN = 64;
  public static final int DIGITS_EN2AN_INIT_LR = 96;
  public static final int DIGITS_EN2AN_INIT_AL = 128;
  private static final int DIGITS_RESERVED = 160;
  public static final int DIGITS_MASK = 224;
  public static final int DIGIT_TYPE_AN = 0;
  public static final int DIGIT_TYPE_AN_EXTENDED = 256;
  public static final int DIGIT_TYPE_MASK = 256;

  static boolean isVowel(char paramChar)
  {
    return ((paramChar >= 'ً') && (paramChar <= 'ٕ')) || (paramChar == 'ٰ');
  }

  static char charshape(char paramChar, int paramInt)
  {
    int i;
    int j;
    if ((paramChar >= 'ء') && (paramChar <= 'ۓ'))
    {
      i = 0;
      j = chartable.length - 1;
    }
    while (i <= j)
    {
      int k = (i + j) / 2;
      if (paramChar == chartable[k][0])
        return chartable[k][(paramInt + 1)];
      if (paramChar < chartable[k][0])
      {
        j = k - 1;
        continue;
      }
      i = k + 1;
      continue;
      if ((paramChar < 65269) || (paramChar > 65275))
        break;
      return (char)(paramChar + paramInt);
    }
    return paramChar;
  }

  static int shapecount(char paramChar)
  {
    int i;
    int j;
    if ((paramChar >= 'ء') && (paramChar <= 'ۓ') && (!isVowel(paramChar)))
    {
      i = 0;
      j = chartable.length - 1;
    }
    while (i <= j)
    {
      int k = (i + j) / 2;
      if (paramChar == chartable[k][0])
        return chartable[k].length - 1;
      if (paramChar < chartable[k][0])
      {
        j = k - 1;
        continue;
      }
      i = k + 1;
      continue;
      if (paramChar != '‍')
        break;
      return 4;
    }
    return 1;
  }

  static int ligature(char paramChar, charstruct paramcharstruct)
  {
    int i = 0;
    if (paramcharstruct.basechar == 0)
      return 0;
    if (isVowel(paramChar))
    {
      i = 1;
      if ((paramcharstruct.vowel != 0) && (paramChar != 'ّ'))
        i = 2;
      switch (paramChar)
      {
      case 'ّ':
        if (paramcharstruct.mark1 == 0)
          paramcharstruct.mark1 = 'ّ';
        else
          return 0;
      case 'ٕ':
        switch (paramcharstruct.basechar)
        {
        case 'ا':
          paramcharstruct.basechar = 'إ';
          i = 2;
          break;
        case 'ﻻ':
          paramcharstruct.basechar = 65273;
          i = 2;
          break;
        default:
          paramcharstruct.mark1 = 'ٕ';
        }
        break;
      case 'ٔ':
        switch (paramcharstruct.basechar)
        {
        case 'ا':
          paramcharstruct.basechar = 'أ';
          i = 2;
          break;
        case 'ﻻ':
          paramcharstruct.basechar = 65271;
          i = 2;
          break;
        case 'و':
          paramcharstruct.basechar = 'ؤ';
          i = 2;
          break;
        case 'ى':
        case 'ي':
        case 'ی':
          paramcharstruct.basechar = 'ئ';
          i = 2;
          break;
        default:
          paramcharstruct.mark1 = 'ٔ';
        }
        break;
      case 'ٓ':
        switch (paramcharstruct.basechar)
        {
        case 'ا':
          paramcharstruct.basechar = 'آ';
          i = 2;
        }
        break;
      case 'ْ':
      default:
        paramcharstruct.vowel = paramChar;
      }
      if (i == 1)
        paramcharstruct.lignum += 1;
      return i;
    }
    if (paramcharstruct.vowel != 0)
      return 0;
    switch (paramcharstruct.basechar)
    {
    case 'ل':
      switch (paramChar)
      {
      case 'ا':
        paramcharstruct.basechar = 65275;
        paramcharstruct.numshapes = 2;
        i = 3;
        break;
      case 'أ':
        paramcharstruct.basechar = 65271;
        paramcharstruct.numshapes = 2;
        i = 3;
        break;
      case 'إ':
        paramcharstruct.basechar = 65273;
        paramcharstruct.numshapes = 2;
        i = 3;
        break;
      case 'آ':
        paramcharstruct.basechar = 65269;
        paramcharstruct.numshapes = 2;
        i = 3;
      case 'ؤ':
      case 'ئ':
      }
      break;
    case '\000':
      paramcharstruct.basechar = paramChar;
      paramcharstruct.numshapes = shapecount(paramChar);
      i = 1;
    }
    return i;
  }

  static void copycstostring(StringBuffer paramStringBuffer, charstruct paramcharstruct, int paramInt)
  {
    if (paramcharstruct.basechar == 0)
      return;
    paramStringBuffer.append(paramcharstruct.basechar);
    paramcharstruct.lignum -= 1;
    if (paramcharstruct.mark1 != 0)
      if ((paramInt & 0x1) == 0)
      {
        paramStringBuffer.append(paramcharstruct.mark1);
        paramcharstruct.lignum -= 1;
      }
      else
      {
        paramcharstruct.lignum -= 1;
      }
    if (paramcharstruct.vowel != 0)
      if ((paramInt & 0x1) == 0)
      {
        paramStringBuffer.append(paramcharstruct.vowel);
        paramcharstruct.lignum -= 1;
      }
      else
      {
        paramcharstruct.lignum -= 1;
      }
  }

  static void doublelig(StringBuffer paramStringBuffer, int paramInt)
  {
    int i;
    int j = i = paramStringBuffer.length();
    int k = 0;
    int m = 1;
    while (m < j)
    {
      int n = 0;
      if ((paramInt & 0x4) != 0)
        switch (paramStringBuffer.charAt(k))
        {
        case 'ّ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ِ':
            n = 64610;
            break;
          case 'َ':
            n = 64608;
            break;
          case 'ُ':
            n = 64609;
            break;
          case 'ٌ':
            n = 64606;
            break;
          case 'ٍ':
            n = 64607;
          }
          break;
        case 'ِ':
          if (paramStringBuffer.charAt(m) != 'ّ')
            break;
          n = 64610;
          break;
        case 'َ':
          if (paramStringBuffer.charAt(m) != 'ّ')
            break;
          n = 64608;
          break;
        case 'ُ':
          if (paramStringBuffer.charAt(m) != 'ّ')
            break;
          n = 64609;
        }
      if ((paramInt & 0x8) != 0)
        switch (paramStringBuffer.charAt(k))
        {
        case 'ﻟ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﺞ':
            n = 64575;
            break;
          case 'ﺠ':
            n = 64713;
            break;
          case 'ﺢ':
            n = 64576;
            break;
          case 'ﺤ':
            n = 64714;
            break;
          case 'ﺦ':
            n = 64577;
            break;
          case 'ﺨ':
            n = 64715;
            break;
          case 'ﻢ':
            n = 64578;
            break;
          case 'ﻤ':
            n = 64716;
          }
          break;
        case 'ﺗ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﺠ':
            n = 64673;
            break;
          case 'ﺤ':
            n = 64674;
            break;
          case 'ﺨ':
            n = 64675;
          }
          break;
        case 'ﺑ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﺠ':
            n = 64668;
            break;
          case 'ﺤ':
            n = 64669;
            break;
          case 'ﺨ':
            n = 64670;
          }
          break;
        case 'ﻧ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﺠ':
            n = 64722;
            break;
          case 'ﺤ':
            n = 64723;
            break;
          case 'ﺨ':
            n = 64724;
          }
          break;
        case 'ﻨ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﺮ':
            n = 64650;
            break;
          case 'ﺰ':
            n = 64651;
          }
          break;
        case 'ﻣ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﺠ':
            n = 64718;
            break;
          case 'ﺤ':
            n = 64719;
            break;
          case 'ﺨ':
            n = 64720;
            break;
          case 'ﻤ':
            n = 64721;
          }
          break;
        case 'ﻓ':
          switch (paramStringBuffer.charAt(m))
          {
          case 'ﻲ':
            n = 64562;
          }
          break;
        }
      if (n != 0)
      {
        paramStringBuffer.setCharAt(k, n);
        i--;
        m++;
        continue;
      }
      k++;
      paramStringBuffer.setCharAt(k, paramStringBuffer.charAt(m));
      m++;
    }
    paramStringBuffer.setLength(i);
  }

  static boolean connects_to_left(charstruct paramcharstruct)
  {
    return paramcharstruct.numshapes > 2;
  }

  static void shape(char[] paramArrayOfChar, StringBuffer paramStringBuffer, int paramInt)
  {
    int k = 0;
    Object localObject = new charstruct();
    charstruct localcharstruct = new charstruct();
    int j;
    while (k < paramArrayOfChar.length)
    {
      char c = paramArrayOfChar[(k++)];
      int i = ligature(c, localcharstruct);
      if (i == 0)
      {
        int m = shapecount(c);
        if (m == 1)
          j = 0;
        else
          j = 2;
        if (connects_to_left((charstruct)localObject))
          j++;
        j %= localcharstruct.numshapes;
        localcharstruct.basechar = charshape(localcharstruct.basechar, j);
        copycstostring(paramStringBuffer, (charstruct)localObject, paramInt);
        localObject = localcharstruct;
        localcharstruct = new charstruct();
        localcharstruct.basechar = c;
        localcharstruct.numshapes = m;
        localcharstruct.lignum += 1;
        continue;
      }
      if (i != 1)
        continue;
    }
    if (connects_to_left((charstruct)localObject))
      j = 1;
    else
      j = 0;
    j %= localcharstruct.numshapes;
    localcharstruct.basechar = charshape(localcharstruct.basechar, j);
    copycstostring(paramStringBuffer, (charstruct)localObject, paramInt);
    copycstostring(paramStringBuffer, localcharstruct, paramInt);
  }

  static int arabic_shape(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5)
  {
    char[] arrayOfChar = new char[paramInt2];
    for (int i = paramInt2 + paramInt1 - 1; i >= paramInt1; i--)
      arrayOfChar[(i - paramInt1)] = paramArrayOfChar1[i];
    StringBuffer localStringBuffer = new StringBuffer(paramInt2);
    shape(arrayOfChar, localStringBuffer, paramInt5);
    if ((paramInt5 & 0xC) != 0)
      doublelig(localStringBuffer, paramInt5);
    System.arraycopy(localStringBuffer.toString().toCharArray(), 0, paramArrayOfChar2, paramInt3, localStringBuffer.length());
    return localStringBuffer.length();
  }

  static void processNumbers(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 + paramInt2;
    if ((paramInt3 & 0xE0) != 0)
    {
      int j = 48;
      switch (paramInt3 & 0x100)
      {
      case 0:
        j = 1632;
        break;
      case 256:
        j = 1776;
        break;
      }
      int k;
      int m;
      switch (paramInt3 & 0xE0)
      {
      case 32:
        k = j - 48;
        m = paramInt1;
      case 64:
      case 96:
      case 128:
        while (m < i)
        {
          int n = paramArrayOfChar[m];
          if ((n <= 57) && (n >= 48))
          {
            int tmp152_150 = m;
            paramArrayOfChar[tmp152_150] = (char)(paramArrayOfChar[tmp152_150] + k);
          }
          m++;
          continue;
          k = (char)(j + 9);
          m = 48 - j;
          n = paramInt1;
          while (n < i)
          {
            int i1 = paramArrayOfChar[n];
            if ((i1 <= k) && (i1 >= j))
            {
              int tmp213_211 = n;
              paramArrayOfChar[tmp213_211] = (char)(paramArrayOfChar[tmp213_211] + m);
            }
            n++;
            continue;
            shapeToArabicDigitsWithContext(paramArrayOfChar, 0, paramInt2, j, false);
            break;
            shapeToArabicDigitsWithContext(paramArrayOfChar, 0, paramInt2, j, true);
          }
        }
      }
    }
  }

  static void shapeToArabicDigitsWithContext(char[] paramArrayOfChar, int paramInt1, int paramInt2, char paramChar, boolean paramBoolean)
  {
    paramChar = (char)(paramChar - '0');
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
    {
      char c = paramArrayOfChar[j];
      switch (BidiOrder.getDirection(c))
      {
      case 0:
      case 3:
        paramBoolean = false;
        break;
      case 4:
        paramBoolean = true;
        break;
      case 8:
        if ((!paramBoolean) || (c > '9'))
          continue;
        paramArrayOfChar[j] = (char)(c + paramChar);
      case 1:
      case 2:
      case 5:
      case 6:
      case 7:
      }
    }
  }

  static class charstruct
  {
    char basechar;
    char mark1;
    char vowel;
    int lignum;
    int numshapes = 1;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.ArabicLigaturizer
 * JD-Core Version:    0.6.0
 */