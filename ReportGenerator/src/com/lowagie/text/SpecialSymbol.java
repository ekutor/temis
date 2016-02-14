package com.lowagie.text;

public class SpecialSymbol
{
  public static int index(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
      if (getCorrespondingSymbol(paramString.charAt(j)) != ' ')
        return j;
    return -1;
  }

  public static Chunk get(char paramChar, Font paramFont)
  {
    char c = getCorrespondingSymbol(paramChar);
    if (c == ' ')
      return new Chunk(String.valueOf(paramChar), paramFont);
    Font localFont = new Font(3, paramFont.getSize(), paramFont.getStyle(), paramFont.getColor());
    String str = String.valueOf(c);
    return new Chunk(str, localFont);
  }

  public static char getCorrespondingSymbol(char paramChar)
  {
    switch (paramChar)
    {
    case 'Α':
      return 'A';
    case 'Β':
      return 'B';
    case 'Γ':
      return 'G';
    case 'Δ':
      return 'D';
    case 'Ε':
      return 'E';
    case 'Ζ':
      return 'Z';
    case 'Η':
      return 'H';
    case 'Θ':
      return 'Q';
    case 'Ι':
      return 'I';
    case 'Κ':
      return 'K';
    case 'Λ':
      return 'L';
    case 'Μ':
      return 'M';
    case 'Ν':
      return 'N';
    case 'Ξ':
      return 'X';
    case 'Ο':
      return 'O';
    case 'Π':
      return 'P';
    case 'Ρ':
      return 'R';
    case 'Σ':
      return 'S';
    case 'Τ':
      return 'T';
    case 'Υ':
      return 'U';
    case 'Φ':
      return 'F';
    case 'Χ':
      return 'C';
    case 'Ψ':
      return 'Y';
    case 'Ω':
      return 'W';
    case 'α':
      return 'a';
    case 'β':
      return 'b';
    case 'γ':
      return 'g';
    case 'δ':
      return 'd';
    case 'ε':
      return 'e';
    case 'ζ':
      return 'z';
    case 'η':
      return 'h';
    case 'θ':
      return 'q';
    case 'ι':
      return 'i';
    case 'κ':
      return 'k';
    case 'λ':
      return 'l';
    case 'μ':
      return 'm';
    case 'ν':
      return 'n';
    case 'ξ':
      return 'x';
    case 'ο':
      return 'o';
    case 'π':
      return 'p';
    case 'ρ':
      return 'r';
    case 'ς':
      return 'V';
    case 'σ':
      return 's';
    case 'τ':
      return 't';
    case 'υ':
      return 'u';
    case 'φ':
      return 'f';
    case 'χ':
      return 'c';
    case 'ψ':
      return 'y';
    case 'ω':
      return 'w';
    case '΢':
    case 'Ϊ':
    case 'Ϋ':
    case 'ά':
    case 'έ':
    case 'ή':
    case 'ί':
    case 'ΰ':
    }
    return ' ';
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.SpecialSymbol
 * JD-Core Version:    0.6.0
 */