package com.lowagie.text.pdf;

public final class BidiOrder
{
  private byte[] initialTypes;
  private byte[] embeddings;
  private byte paragraphEmbeddingLevel = -1;
  private int textLength;
  private byte[] resultTypes;
  private byte[] resultLevels;
  public static final byte L = 0;
  public static final byte LRE = 1;
  public static final byte LRO = 2;
  public static final byte R = 3;
  public static final byte AL = 4;
  public static final byte RLE = 5;
  public static final byte RLO = 6;
  public static final byte PDF = 7;
  public static final byte EN = 8;
  public static final byte ES = 9;
  public static final byte ET = 10;
  public static final byte AN = 11;
  public static final byte CS = 12;
  public static final byte NSM = 13;
  public static final byte BN = 14;
  public static final byte B = 15;
  public static final byte S = 16;
  public static final byte WS = 17;
  public static final byte ON = 18;
  public static final byte TYPE_MIN = 0;
  public static final byte TYPE_MAX = 18;
  private static final byte[] rtypes = new byte[65536];
  private static char[] baseTypes = { '\000', '\b', '\016', '\t', '\t', '\020', '\n', '\n', '\017', '\013', '\013', '\020', '\f', '\f', '\021', '\r', '\r', '\017', '\016', '\033', '\016', '\034', '\036', '\017', '\037', '\037', '\020', ' ', ' ', '\021', '!', '"', '\022', '#', '%', '\n', '&', '*', '\022', '+', '+', '\n', ',', ',', '\f', '-', '-', '\n', '.', '.', '\f', '/', '/', '\t', '0', '9', '\b', ':', ':', '\f', ';', '@', '\022', 'A', 'Z', '\000', '[', '`', '\022', 'a', 'z', '\000', '{', '~', '\022', '', '', '\016', '', '', '\017', '', '', '\016', ' ', ' ', '\f', '¡', '¡', '\022', '¢', '¥', '\n', '¦', '©', '\022', 'ª', 'ª', '\000', '«', '¯', '\022', '°', '±', '\n', '²', '³', '\b', '´', '´', '\022', 'µ', 'µ', '\000', '¶', '¸', '\022', '¹', '¹', '\b', 'º', 'º', '\000', '»', '¿', '\022', 'À', 'Ö', '\000', '×', '×', '\022', 'Ø', 'ö', '\000', '÷', '÷', '\022', 'ø', 'ʸ', '\000', 'ʹ', 'ʺ', '\022', 'ʻ', 'ˁ', '\000', '˂', 'ˏ', '\022', 'ː', 'ˑ', '\000', '˒', '˟', '\022', 'ˠ', 'ˤ', '\000', '˥', '˭', '\022', 'ˮ', 'ˮ', '\000', '˯', '˿', '\022', '̀', '͗', '\r', '͘', '͜', '\000', '͝', 'ͯ', '\r', 'Ͱ', 'ͳ', '\000', 'ʹ', '͵', '\022', 'Ͷ', 'ͽ', '\000', ';', ';', '\022', 'Ϳ', '΃', '\000', '΄', '΅', '\022', 'Ά', 'Ά', '\000', '·', '·', '\022', 'Έ', 'ϵ', '\000', '϶', '϶', '\022', 'Ϸ', '҂', '\000', '҃', '҆', '\r', '҇', '҇', '\000', '҈', '҉', '\r', 'Ҋ', '։', '\000', '֊', '֊', '\022', '֋', '֐', '\000', '֑', '֡', '\r', '֢', '֢', '\000', '֣', 'ֹ', '\r', 'ֺ', 'ֺ', '\000', 'ֻ', 'ֽ', '\r', '־', '־', '\003', 'ֿ', 'ֿ', '\r', '׀', '׀', '\003', 'ׁ', 'ׂ', '\r', '׃', '׃', '\003', 'ׄ', 'ׄ', '\r', 'ׅ', '׏', '\000', 'א', 'ת', '\003', '׫', 'ׯ', '\000', 'װ', '״', '\003', '׵', '׿', '\000', '؀', '؃', '\004', '؄', '؋', '\000', '،', '،', '\f', '؍', '؍', '\004', '؎', '؏', '\022', 'ؐ', 'ؕ', '\r', 'ؖ', 'ؚ', '\000', '؛', '؛', '\004', '؜', '؞', '\000', '؟', '؟', '\004', 'ؠ', 'ؠ', '\000', 'ء', 'غ', '\004', 'ػ', 'ؿ', '\000', 'ـ', 'ي', '\004', 'ً', '٘', '\r', 'ٙ', 'ٟ', '\000', '٠', '٩', '\013', '٪', '٪', '\n', '٫', '٬', '\013', '٭', 'ٯ', '\004', 'ٰ', 'ٰ', '\r', 'ٱ', 'ە', '\004', 'ۖ', 'ۜ', '\r', '۝', '۝', '\004', '۞', 'ۤ', '\r', 'ۥ', 'ۦ', '\004', 'ۧ', 'ۨ', '\r', '۩', '۩', '\022', '۪', 'ۭ', '\r', 'ۮ', 'ۯ', '\004', '۰', '۹', '\b', 'ۺ', '܍', '\004', '܎', '܎', '\000', '܏', '܏', '\016', 'ܐ', 'ܐ', '\004', 'ܑ', 'ܑ', '\r', 'ܒ', 'ܯ', '\004', 'ܰ', '݊', '\r', '݋', '݌', '\000', 'ݍ', 'ݏ', '\004', 'ݐ', 'ݿ', '\000', 'ހ', 'ޥ', '\004', 'ަ', 'ް', '\r', 'ޱ', 'ޱ', '\004', '޲', 'ऀ', '\000', 'ँ', 'ं', '\r', 'ः', 'ऻ', '\000', '़', '़', '\r', 'ऽ', 'ी', '\000', 'ु', 'ै', '\r', 'ॉ', 'ौ', '\000', '्', '्', '\r', 'ॎ', 'ॐ', '\000', '॑', '॔', '\r', 'ॕ', 'ॡ', '\000', 'ॢ', 'ॣ', '\r', '।', 'ঀ', '\000', 'ঁ', 'ঁ', '\r', 'ং', '঻', '\000', '়', '়', '\r', 'ঽ', 'ী', '\000', 'ু', 'ৄ', '\r', '৅', 'ৌ', '\000', '্', '্', '\r', 'ৎ', 'ৡ', '\000', 'ৢ', 'ৣ', '\r', '৤', 'ৱ', '\000', '৲', '৳', '\n', '৴', '਀', '\000', 'ਁ', 'ਂ', '\r', 'ਃ', '਻', '\000', '਼', '਼', '\r', '਽', 'ੀ', '\000', 'ੁ', 'ੂ', '\r', '੃', '੆', '\000', 'ੇ', 'ੈ', '\r', '੉', '੊', '\000', 'ੋ', '੍', '\r', '੎', '੯', '\000', 'ੰ', 'ੱ', '\r', 'ੲ', '઀', '\000', 'ઁ', 'ં', '\r', 'ઃ', '઻', '\000', '઼', '઼', '\r', 'ઽ', 'ી', '\000', 'ુ', 'ૅ', '\r', '૆', '૆', '\000', 'ે', 'ૈ', '\r', 'ૉ', 'ૌ', '\000', '્', '્', '\r', '૎', 'ૡ', '\000', 'ૢ', 'ૣ', '\r', '૤', '૰', '\000', '૱', '૱', '\n', '૲', '଀', '\000', 'ଁ', 'ଁ', '\r', 'ଂ', '଻', '\000', '଼', '଼', '\r', 'ଽ', 'ା', '\000', 'ି', 'ି', '\r', 'ୀ', 'ୀ', '\000', 'ୁ', 'ୃ', '\r', 'ୄ', 'ୌ', '\000', '୍', '୍', '\r', '୎', '୕', '\000', 'ୖ', 'ୖ', '\r', 'ୗ', '஁', '\000', 'ஂ', 'ஂ', '\r', 'ஃ', 'ி', '\000', 'ீ', 'ீ', '\r', 'ு', 'ௌ', '\000', '்', '்', '\r', '௎', '௲', '\000', '௳', '௸', '\022', '௹', '௹', '\n', '௺', '௺', '\022', '௻', 'ఽ', '\000', 'ా', 'ీ', '\r', 'ు', '౅', '\000', 'ె', 'ై', '\r', '౉', '౉', '\000', 'ొ', '్', '\r', '౎', '౔', '\000', 'ౕ', 'ౖ', '\r', '౗', '಻', '\000', '಼', '಼', '\r', 'ಽ', 'ೋ', '\000', 'ೌ', '್', '\r', '೎', 'ീ', '\000', 'ു', 'ൃ', '\r', 'ൄ', 'ൌ', '\000', '്', '്', '\r', 'ൎ', '෉', '\000', '්', '්', '\r', '෋', 'ෑ', '\000', 'ි', 'ු', '\r', '෕', '෕', '\000', 'ූ', 'ූ', '\r', '෗', 'ะ', '\000', 'ั', 'ั', '\r', 'า', 'ำ', '\000', 'ิ', 'ฺ', '\r', '฻', '฾', '\000', '฿', '฿', '\n', 'เ', 'ๆ', '\000', '็', '๎', '\r', '๏', 'ະ', '\000', 'ັ', 'ັ', '\r', 'າ', 'ຳ', '\000', 'ິ', 'ູ', '\r', '຺', '຺', '\000', 'ົ', 'ຼ', '\r', 'ຽ', '໇', '\000', '່', 'ໍ', '\r', '໎', '༗', '\000', '༘', '༙', '\r', '༚', '༴', '\000', '༵', '༵', '\r', '༶', '༶', '\000', '༷', '༷', '\r', '༸', '༸', '\000', '༹', '༹', '\r', '༺', '༽', '\022', '༾', '཰', '\000', 'ཱ', 'ཾ', '\r', 'ཿ', 'ཿ', '\000', 'ྀ', '྄', '\r', '྅', '྅', '\000', '྆', '྇', '\r', 'ྈ', 'ྏ', '\000', 'ྐ', 'ྗ', '\r', '྘', '྘', '\000', 'ྙ', 'ྼ', '\r', '྽', '࿅', '\000', '࿆', '࿆', '\r', '࿇', 'ာ', '\000', 'ိ', 'ူ', '\r', 'ေ', 'ေ', '\000', 'ဲ', 'ဲ', '\r', 'ဳ', 'ဵ', '\000', 'ံ', '့', '\r', 'း', 'း', '\000', '္', '္', '\r', '်', 'ၗ', '\000', 'ၘ', 'ၙ', '\r', 'ၚ', 'ᙿ', '\000', ' ', ' ', '\021', 'ᚁ', 'ᚚ', '\000', '᚛', '᚜', '\022', '᚝', 'ᜑ', '\000', 'ᜒ', '᜔', '\r', '᜕', 'ᜱ', '\000', 'ᜲ', '᜴', '\r', '᜵', 'ᝑ', '\000', 'ᝒ', 'ᝓ', '\r', '᝔', '᝱', '\000', 'ᝲ', 'ᝳ', '\r', '᝴', 'ា', '\000', 'ិ', 'ួ', '\r', 'ើ', 'ៅ', '\000', 'ំ', 'ំ', '\r', 'ះ', 'ៈ', '\000', '៉', '៓', '\r', '។', '៚', '\000', '៛', '៛', '\n', 'ៜ', 'ៜ', '\000', '៝', '៝', '\r', '៞', '៯', '\000', '៰', '៹', '\022', '៺', '៿', '\000', '᠀', '᠊', '\022', '᠋', '᠍', '\r', '᠎', '᠎', '\021', '᠏', 'ᢨ', '\000', 'ᢩ', 'ᢩ', '\r', 'ᢪ', '᤟', '\000', 'ᤠ', 'ᤢ', '\r', 'ᤣ', 'ᤦ', '\000', 'ᤧ', 'ᤫ', '\r', '᤬', 'ᤱ', '\000', 'ᤲ', 'ᤲ', '\r', 'ᤳ', 'ᤸ', '\000', '᤹', '᤻', '\r', '᤼', '᤿', '\000', '᥀', '᥀', '\022', '᥁', '᥃', '\000', '᥄', '᥅', '\022', '᥆', '᧟', '\000', '᧠', '᧿', '\022', 'ᨀ', 'ᾼ', '\000', '᾽', '᾽', '\022', 'ι', 'ι', '\000', '᾿', '῁', '\022', 'ῂ', 'ῌ', '\000', '῍', '῏', '\022', 'ῐ', '῜', '\000', '῝', '῟', '\022', 'ῠ', 'Ῥ', '\000', '῭', '`', '\022', '῰', 'ῼ', '\000', '´', '῾', '\022', '῿', '῿', '\000', ' ', ' ', '\021', '​', '‍', '\016', '‎', '‎', '\000', '‏', '‏', '\003', '‐', '‧', '\022', ' ', ' ', '\021', ' ', ' ', '\017', '‪', '‪', '\001', '‫', '‫', '\005', '‬', '‬', '\007', '‭', '‭', '\002', '‮', '‮', '\006', ' ', ' ', '\021', '‰', '‴', '\n', '‵', '⁔', '\022', '⁕', '⁖', '\000', '⁗', '⁗', '\022', '⁘', '⁞', '\000', ' ', ' ', '\021', '⁠', '⁣', '\016', '⁤', '⁩', '\000', '⁪', '⁯', '\016', '⁰', '⁰', '\b', 'ⁱ', '⁳', '\000', '⁴', '⁹', '\b', '⁺', '⁻', '\n', '⁼', '⁾', '\022', 'ⁿ', 'ⁿ', '\000', '₀', '₉', '\b', '₊', '₋', '\n', '₌', '₎', '\022', '₏', '₟', '\000', '₠', '₱', '\n', '₲', '⃏', '\000', '⃐', '⃪', '\r', '⃫', '⃿', '\000', '℀', '℁', '\022', 'ℂ', 'ℂ', '\000', '℃', '℆', '\022', 'ℇ', 'ℇ', '\000', '℈', '℉', '\022', 'ℊ', 'ℓ', '\000', '℔', '℔', '\022', 'ℕ', 'ℕ', '\000', '№', '℘', '\022', 'ℙ', 'ℝ', '\000', '℞', '℣', '\022', 'ℤ', 'ℤ', '\000', '℥', '℥', '\022', 'Ω', 'Ω', '\000', '℧', '℧', '\022', 'ℨ', 'ℨ', '\000', '℩', '℩', '\022', 'K', 'ℭ', '\000', '℮', '℮', '\n', 'ℯ', 'ℱ', '\000', 'Ⅎ', 'Ⅎ', '\022', 'ℳ', 'ℹ', '\000', '℺', '℻', '\022', 'ℼ', 'ℿ', '\000', '⅀', '⅄', '\022', 'ⅅ', 'ⅉ', '\000', '⅊', '⅋', '\022', '⅌', '⅒', '\000', '⅓', '⅟', '\022', 'Ⅰ', '↏', '\000', '←', '∑', '\022', '−', '∓', '\n', '∔', '⌵', '\022', '⌶', '⍺', '\000', '⍻', '⎔', '\022', '⎕', '⎕', '\000', '⎖', '⏐', '\022', '⏑', '⏿', '\000', '␀', '␦', '\022', '␧', '␿', '\000', '⑀', '⑊', '\022', '⑋', '⑟', '\000', '①', '⒛', '\b', '⒜', 'ⓩ', '\000', '⓪', '⓪', '\b', '⓫', '☗', '\022', '☘', '☘', '\000', '☙', '♽', '\022', '♾', '♿', '\000', '⚀', '⚑', '\022', '⚒', '⚟', '\000', '⚠', '⚡', '\022', '⚢', '✀', '\000', '✁', '✄', '\022', '✅', '✅', '\000', '✆', '✉', '\022', '✊', '✋', '\000', '✌', '✧', '\022', '✨', '✨', '\000', '✩', '❋', '\022', '❌', '❌', '\000', '❍', '❍', '\022', '❎', '❎', '\000', '❏', '❒', '\022', '❓', '❕', '\000', '❖', '❖', '\022', '❗', '❗', '\000', '❘', '❞', '\022', '❟', '❠', '\000', '❡', '➔', '\022', '➕', '➗', '\000', '➘', '➯', '\022', '➰', '➰', '\000', '➱', '➾', '\022', '➿', '⟏', '\000', '⟐', '⟫', '\022', '⟬', '⟯', '\000', '⟰', '⬍', '\022', '⬎', '⹿', '\000', '⺀', '⺙', '\022', '⺚', '⺚', '\000', '⺛', '⻳', '\022', '⻴', '⻿', '\000', '⼀', '⿕', '\022', '⿖', '⿯', '\000', '⿰', '⿻', '\022', '⿼', '⿿', '\000', '　', '　', '\021', '、', '〄', '\022', '々', '〇', '\000', '〈', '〠', '\022', '〡', '〩', '\000', '〪', '〯', '\r', '〰', '〰', '\022', '〱', '〵', '\000', '〶', '〷', '\022', '〸', '〼', '\000', '〽', '〿', '\022', '぀', '゘', '\000', '゙', '゚', '\r', '゛', '゜', '\022', 'ゝ', 'ゟ', '\000', '゠', '゠', '\022', 'ァ', 'ヺ', '\000', '・', '・', '\022', 'ー', '㈜', '\000', '㈝', '㈞', '\022', '㈟', '㉏', '\000', '㉐', '㉟', '\022', '㉠', '㉻', '\000', '㉼', '㉽', '\022', '㉾', '㊰', '\000', '㊱', '㊿', '\022', '㋀', '㋋', '\000', '㋌', '㋏', '\022', '㋐', '㍶', '\000', '㍷', '㍺', '\022', '㍻', '㏝', '\000', '㏞', '㏟', '\022', '㏠', '㏾', '\000', '㏿', '㏿', '\022', '㐀', '䶿', '\000', '䷀', '䷿', '\022', '一', 42127, '\000', 42128, 42182, '\022', 42183, 64284, '\000', 64285, 64285, '\003', 64286, 64286, '\r', 64287, 64296, '\003', 64297, 64297, '\n', 64298, 64310, '\003', 64311, 64311, '\000', 64312, 64316, '\003', 64317, 64317, '\000', 64318, 64318, '\003', 64319, 64319, '\000', 64320, 64321, '\003', 64322, 64322, '\000', 64323, 64324, '\003', 64325, 64325, '\000', 64326, 64335, '\003', 64336, 64433, '\004', 64434, 64466, '\000', 64467, 64829, '\004', 64830, 64831, '\022', 64832, 64847, '\000', 64848, 64911, '\004', 64912, 64913, '\000', 64914, 64967, '\004', 64968, 65007, '\000', 65008, 65020, '\004', 65021, 65021, '\022', 65022, 65023, '\000', 65024, 65039, '\r', 65040, 65055, '\000', 65056, 65059, '\r', 65060, 65071, '\000', 65072, 65103, '\022', 65104, 65104, '\f', 65105, 65105, '\022', 65106, 65106, '\f', 65107, 65107, '\000', 65108, 65108, '\022', 65109, 65109, '\f', 65110, 65118, '\022', 65119, 65119, '\n', 65120, 65121, '\022', 65122, 65123, '\n', 65124, 65126, '\022', 65127, 65127, '\000', 65128, 65128, '\022', 65129, 65130, '\n', 65131, 65131, '\022', 65132, 65135, '\000', 65136, 65140, '\004', 65141, 65141, '\000', 65142, 65276, '\004', 65277, 65278, '\000', 65279, 65279, '\016', 65280, 65280, '\000', 65281, 65282, '\022', 65283, 65285, '\n', 65286, 65290, '\022', 65291, 65291, '\n', 65292, 65292, '\f', 65293, 65293, '\n', 65294, 65294, '\f', 65295, 65295, '\t', 65296, 65305, '\b', 65306, 65306, '\f', 65307, 65312, '\022', 65313, 65338, '\000', 65339, 65344, '\022', 65345, 65370, '\000', 65371, 65381, '\022', 65382, 65503, '\000', 65504, 65505, '\n', 65506, 65508, '\022', 65509, 65510, '\n', 65511, 65511, '\000', 65512, 65518, '\022', 65519, 65528, '\000', 65529, 65531, '\016', 65532, 65533, '\022', 65534, 65535, '\000' };

  public BidiOrder(byte[] paramArrayOfByte)
  {
    validateTypes(paramArrayOfByte);
    this.initialTypes = ((byte[])paramArrayOfByte.clone());
    runAlgorithm();
  }

  public BidiOrder(byte[] paramArrayOfByte, byte paramByte)
  {
    validateTypes(paramArrayOfByte);
    validateParagraphEmbeddingLevel(paramByte);
    this.initialTypes = ((byte[])paramArrayOfByte.clone());
    this.paragraphEmbeddingLevel = paramByte;
    runAlgorithm();
  }

  public BidiOrder(char[] paramArrayOfChar, int paramInt1, int paramInt2, byte paramByte)
  {
    this.initialTypes = new byte[paramInt2];
    for (int i = 0; i < paramInt2; i++)
      this.initialTypes[i] = rtypes[paramArrayOfChar[(paramInt1 + i)]];
    validateParagraphEmbeddingLevel(paramByte);
    this.paragraphEmbeddingLevel = paramByte;
    runAlgorithm();
  }

  public static final byte getDirection(char paramChar)
  {
    return rtypes[paramChar];
  }

  private void runAlgorithm()
  {
    this.textLength = this.initialTypes.length;
    this.resultTypes = ((byte[])this.initialTypes.clone());
    if (this.paragraphEmbeddingLevel == -1)
      determineParagraphEmbeddingLevel();
    this.resultLevels = new byte[this.textLength];
    setLevels(0, this.textLength, this.paragraphEmbeddingLevel);
    determineExplicitEmbeddingLevels();
    this.textLength = removeExplicitCodes();
    int i = this.paragraphEmbeddingLevel;
    int k;
    for (int j = 0; j < this.textLength; j = k)
    {
      byte b1 = this.resultLevels[j];
      byte b2 = typeForLevel(Math.max(i, b1));
      for (k = j + 1; (k < this.textLength) && (this.resultLevels[k] == b1); k++);
      int m = k < this.textLength ? this.resultLevels[k] : this.paragraphEmbeddingLevel;
      byte b3 = typeForLevel(Math.max(m, b1));
      resolveWeakTypes(j, k, b1, b2, b3);
      resolveNeutralTypes(j, k, b1, b2, b3);
      resolveImplicitLevels(j, k, b1, b2, b3);
      i = b1;
    }
    this.textLength = reinsertExplicitCodes(this.textLength);
  }

  private void determineParagraphEmbeddingLevel()
  {
    int i = -1;
    for (int j = 0; j < this.textLength; j++)
    {
      int k = this.resultTypes[j];
      if ((k != 0) && (k != 4) && (k != 3))
        continue;
      i = k;
      break;
    }
    if (i == -1)
      this.paragraphEmbeddingLevel = 0;
    else if (i == 0)
      this.paragraphEmbeddingLevel = 0;
    else
      this.paragraphEmbeddingLevel = 1;
  }

  private void determineExplicitEmbeddingLevels()
  {
    this.embeddings = processEmbeddings(this.resultTypes, this.paragraphEmbeddingLevel);
    for (int i = 0; i < this.textLength; i++)
    {
      int j = this.embeddings[i];
      if ((j & 0x80) != 0)
      {
        j = (byte)(j & 0x7F);
        this.resultTypes[i] = typeForLevel(j);
      }
      this.resultLevels[i] = j;
    }
  }

  private int removeExplicitCodes()
  {
    int i = 0;
    for (int j = 0; j < this.textLength; j++)
    {
      int k = this.initialTypes[j];
      if ((k == 1) || (k == 5) || (k == 2) || (k == 6) || (k == 7) || (k == 14))
        continue;
      this.embeddings[i] = this.embeddings[j];
      this.resultTypes[i] = this.resultTypes[j];
      this.resultLevels[i] = this.resultLevels[j];
      i++;
    }
    return i;
  }

  private int reinsertExplicitCodes(int paramInt)
  {
    int i = this.initialTypes.length;
    while (true)
    {
      i--;
      if (i < 0)
        break;
      int j = this.initialTypes[i];
      if ((j == 1) || (j == 5) || (j == 2) || (j == 6) || (j == 7) || (j == 14))
      {
        this.embeddings[i] = 0;
        this.resultTypes[i] = j;
        this.resultLevels[i] = -1;
        continue;
      }
      paramInt--;
      this.embeddings[i] = this.embeddings[paramInt];
      this.resultTypes[i] = this.resultTypes[paramInt];
      this.resultLevels[i] = this.resultLevels[paramInt];
    }
    if (this.resultLevels[0] == -1)
      this.resultLevels[0] = this.paragraphEmbeddingLevel;
    for (i = 1; i < this.initialTypes.length; i++)
    {
      if (this.resultLevels[i] != -1)
        continue;
      this.resultLevels[i] = this.resultLevels[(i - 1)];
    }
    return this.initialTypes.length;
  }

  private static byte[] processEmbeddings(byte[] paramArrayOfByte, byte paramByte)
  {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[62];
    int j = 0;
    int k = 0;
    int m = 0;
    int n = paramByte;
    int i1 = paramByte;
    for (int i2 = 0; i2 < i; i2++)
    {
      arrayOfByte1[i2] = i1;
      int i3 = paramArrayOfByte[i2];
      switch (i3)
      {
      case 1:
      case 2:
      case 5:
      case 6:
        if (m == 0)
        {
          int i4;
          if ((i3 == 5) || (i3 == 6))
            i4 = (byte)(n + 1 | 0x1);
          else
            i4 = (byte)(n + 2 & 0xFFFFFFFE);
          if (i4 < 62)
          {
            arrayOfByte2[j] = i1;
            j++;
            n = i4;
            if ((i3 == 2) || (i3 == 6))
              i1 = (byte)(i4 | 0x80);
            else
              i1 = i4;
            arrayOfByte1[i2] = i1;
            continue;
          }
          if (n == 60)
          {
            k++;
            continue;
          }
        }
        m++;
        break;
      case 7:
        if (m > 0)
        {
          m--;
        }
        else if ((k > 0) && (n != 61))
        {
          k--;
        }
        else
        {
          if (j <= 0)
            continue;
          j--;
          i1 = arrayOfByte2[j];
          n = (byte)(i1 & 0x7F);
        }
        break;
      case 15:
        j = 0;
        m = 0;
        k = 0;
        n = paramByte;
        i1 = paramByte;
        arrayOfByte1[i2] = paramByte;
      case 3:
      case 4:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      }
    }
    return arrayOfByte1;
  }

  private void resolveWeakTypes(int paramInt1, int paramInt2, byte paramByte1, byte paramByte2, byte paramByte3)
  {
    int i = paramByte2;
    int k;
    for (int j = paramInt1; j < paramInt2; j++)
    {
      k = this.resultTypes[j];
      if (k == 13)
        this.resultTypes[j] = i;
      else
        i = k;
    }
    int m;
    for (j = paramInt1; j < paramInt2; j++)
    {
      if (this.resultTypes[j] != 8)
        continue;
      for (k = j - 1; k >= paramInt1; k--)
      {
        m = this.resultTypes[k];
        if ((m != 0) && (m != 3) && (m != 4))
          continue;
        if (m != 4)
          break;
        this.resultTypes[j] = 11;
        break;
      }
    }
    for (j = paramInt1; j < paramInt2; j++)
    {
      if (this.resultTypes[j] != 4)
        continue;
      this.resultTypes[j] = 3;
    }
    for (j = paramInt1 + 1; j < paramInt2 - 1; j++)
    {
      if ((this.resultTypes[j] != 9) && (this.resultTypes[j] != 12))
        continue;
      k = this.resultTypes[(j - 1)];
      m = this.resultTypes[(j + 1)];
      if ((k == 8) && (m == 8))
      {
        this.resultTypes[j] = 8;
      }
      else
      {
        if ((this.resultTypes[j] != 12) || (k != 11) || (m != 11))
          continue;
        this.resultTypes[j] = 11;
      }
    }
    int n;
    for (j = paramInt1; j < paramInt2; j++)
    {
      if (this.resultTypes[j] != 10)
        continue;
      k = j;
      m = findRunLimit(k, paramInt2, new byte[] { 10 });
      n = k == paramInt1 ? paramByte2 : this.resultTypes[(k - 1)];
      if (n != 8)
        n = m == paramInt2 ? paramByte3 : this.resultTypes[m];
      if (n == 8)
        setTypes(k, m, 8);
      j = m;
    }
    for (j = paramInt1; j < paramInt2; j++)
    {
      k = this.resultTypes[j];
      if ((k != 9) && (k != 10) && (k != 12))
        continue;
      this.resultTypes[j] = 18;
    }
    for (j = paramInt1; j < paramInt2; j++)
    {
      if (this.resultTypes[j] != 8)
        continue;
      k = paramByte2;
      for (m = j - 1; m >= paramInt1; m--)
      {
        n = this.resultTypes[m];
        if ((n != 0) && (n != 3))
          continue;
        k = n;
        break;
      }
      if (k != 0)
        continue;
      this.resultTypes[j] = 0;
    }
  }

  private void resolveNeutralTypes(int paramInt1, int paramInt2, byte paramByte1, byte paramByte2, byte paramByte3)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = this.resultTypes[i];
      if ((j != 17) && (j != 18) && (j != 15) && (j != 16))
        continue;
      int k = i;
      int m = findRunLimit(k, paramInt2, new byte[] { 15, 16, 17, 18 });
      byte b1;
      if (k == paramInt1)
      {
        b1 = paramByte2;
      }
      else
      {
        b1 = this.resultTypes[(k - 1)];
        if ((b1 != 0) && (b1 != 3))
          if (b1 == 11)
            b1 = 3;
          else if (b1 == 8)
            b1 = 3;
      }
      int n;
      if (m == paramInt2)
      {
        n = paramByte3;
      }
      else
      {
        n = this.resultTypes[m];
        if ((n != 0) && (n != 3))
          if (n == 11)
            n = 3;
          else if (n == 8)
            n = 3;
      }
      byte b2;
      if (b1 == n)
        b2 = b1;
      else
        b2 = typeForLevel(paramByte1);
      setTypes(k, m, b2);
      i = m;
    }
  }

  private void resolveImplicitLevels(int paramInt1, int paramInt2, byte paramByte1, byte paramByte2, byte paramByte3)
  {
    int j;
    if ((paramByte1 & 0x1) == 0)
      for (i = paramInt1; i < paramInt2; i++)
      {
        j = this.resultTypes[i];
        if (j == 0)
          continue;
        if (j == 3)
        {
          int tmp44_42 = i;
          byte[] tmp44_39 = this.resultLevels;
          tmp44_39[tmp44_42] = (byte)(tmp44_39[tmp44_42] + 1);
        }
        else
        {
          int tmp59_57 = i;
          byte[] tmp59_54 = this.resultLevels;
          tmp59_54[tmp59_57] = (byte)(tmp59_54[tmp59_57] + 2);
        }
      }
    for (int i = paramInt1; i < paramInt2; i++)
    {
      j = this.resultTypes[i];
      if (j == 3)
        continue;
      int tmp104_102 = i;
      byte[] tmp104_99 = this.resultLevels;
      tmp104_99[tmp104_102] = (byte)(tmp104_99[tmp104_102] + 1);
    }
  }

  public byte[] getLevels()
  {
    return getLevels(new int[] { this.textLength });
  }

  public byte[] getLevels(int[] paramArrayOfInt)
  {
    validateLineBreaks(paramArrayOfInt, this.textLength);
    byte[] arrayOfByte = (byte[])this.resultLevels.clone();
    int k;
    for (int i = 0; i < arrayOfByte.length; i++)
    {
      j = this.initialTypes[i];
      if ((j != 15) && (j != 16))
        continue;
      arrayOfByte[i] = this.paragraphEmbeddingLevel;
      for (k = i - 1; (k >= 0) && (isWhitespace(this.initialTypes[k])); k--)
        arrayOfByte[k] = this.paragraphEmbeddingLevel;
    }
    i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      k = paramArrayOfInt[j];
      for (int m = k - 1; (m >= i) && (isWhitespace(this.initialTypes[m])); m--)
        arrayOfByte[m] = this.paragraphEmbeddingLevel;
      i = k;
    }
    return arrayOfByte;
  }

  public int[] getReordering(int[] paramArrayOfInt)
  {
    validateLineBreaks(paramArrayOfInt, this.textLength);
    byte[] arrayOfByte = getLevels(paramArrayOfInt);
    return computeMultilineReordering(arrayOfByte, paramArrayOfInt);
  }

  private static int[] computeMultilineReordering(byte[] paramArrayOfByte, int[] paramArrayOfInt)
  {
    int[] arrayOfInt1 = new int[paramArrayOfByte.length];
    int i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      int k = paramArrayOfInt[j];
      byte[] arrayOfByte = new byte[k - i];
      System.arraycopy(paramArrayOfByte, i, arrayOfByte, 0, arrayOfByte.length);
      int[] arrayOfInt2 = computeReordering(arrayOfByte);
      for (int m = 0; m < arrayOfInt2.length; m++)
        arrayOfInt1[(i + m)] = (arrayOfInt2[m] + i);
      i = k;
    }
    return arrayOfInt1;
  }

  private static int[] computeReordering(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++)
      arrayOfInt[j] = j;
    j = 0;
    int k = 63;
    int n;
    for (int m = 0; m < i; m++)
    {
      n = paramArrayOfByte[m];
      if (n > j)
        j = n;
      if (((n & 0x1) == 0) || (n >= k))
        continue;
      k = n;
    }
    for (m = j; m >= k; m--)
      for (n = 0; n < i; n++)
      {
        if (paramArrayOfByte[n] < m)
          continue;
        int i1 = n;
        for (int i2 = n + 1; (i2 < i) && (paramArrayOfByte[i2] >= m); i2++);
        int i3 = i1;
        for (int i4 = i2 - 1; i3 < i4; i4--)
        {
          int i5 = arrayOfInt[i3];
          arrayOfInt[i3] = arrayOfInt[i4];
          arrayOfInt[i4] = i5;
          i3++;
        }
        n = i2;
      }
    return arrayOfInt;
  }

  public byte getBaseLevel()
  {
    return this.paragraphEmbeddingLevel;
  }

  private static boolean isWhitespace(byte paramByte)
  {
    switch (paramByte)
    {
    case 1:
    case 2:
    case 5:
    case 6:
    case 7:
    case 14:
    case 17:
      return true;
    case 3:
    case 4:
    case 8:
    case 9:
    case 10:
    case 11:
    case 12:
    case 13:
    case 15:
    case 16:
    }
    return false;
  }

  private static byte typeForLevel(int paramInt)
  {
    return (paramInt & 0x1) == 0 ? 0 : 3;
  }

  private int findRunLimit(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    paramInt1--;
    paramInt1++;
    if (paramInt1 < paramInt2)
    {
      int i = this.resultTypes[paramInt1];
      for (int j = 0; ; j++)
      {
        if (j >= paramArrayOfByte.length)
          break label47;
        if (i == paramArrayOfByte[j])
          break;
      }
      label47: return paramInt1;
    }
    return paramInt2;
  }

  private int findRunStart(int paramInt, byte[] paramArrayOfByte)
  {
    paramInt--;
    if (paramInt >= 0)
    {
      int i = this.resultTypes[paramInt];
      for (int j = 0; ; j++)
      {
        if (j >= paramArrayOfByte.length)
          break label41;
        if (i == paramArrayOfByte[j])
          break;
      }
      label41: return paramInt + 1;
    }
    return 0;
  }

  private void setTypes(int paramInt1, int paramInt2, byte paramByte)
  {
    for (int i = paramInt1; i < paramInt2; i++)
      this.resultTypes[i] = paramByte;
  }

  private void setLevels(int paramInt1, int paramInt2, byte paramByte)
  {
    for (int i = paramInt1; i < paramInt2; i++)
      this.resultLevels[i] = paramByte;
  }

  private static void validateTypes(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("types is null");
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      if ((paramArrayOfByte[i] >= 0) && (paramArrayOfByte[i] <= 18))
        continue;
      throw new IllegalArgumentException("illegal type value at " + i + ": " + paramArrayOfByte[i]);
    }
    for (i = 0; i < paramArrayOfByte.length - 1; i++)
    {
      if (paramArrayOfByte[i] != 15)
        continue;
      throw new IllegalArgumentException("B type before end of paragraph at index: " + i);
    }
  }

  private static void validateParagraphEmbeddingLevel(byte paramByte)
  {
    if ((paramByte != -1) && (paramByte != 0) && (paramByte != 1))
      throw new IllegalArgumentException("illegal paragraph embedding level: " + paramByte);
  }

  private static void validateLineBreaks(int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      int k = paramArrayOfInt[j];
      if (k <= i)
        throw new IllegalArgumentException("bad linebreak: " + k + " at index: " + j);
      i = k;
    }
    if (i != paramInt)
      throw new IllegalArgumentException("last linebreak must be at " + paramInt);
  }

  static
  {
    for (int i = 0; i < baseTypes.length; i++)
    {
      int j = baseTypes[i];
      i++;
      int k = baseTypes[i];
      i++;
      int m = (byte)baseTypes[i];
      while (j <= k)
        rtypes[(j++)] = m;
    }
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.BidiOrder
 * JD-Core Version:    0.6.0
 */