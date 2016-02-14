package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.CMapAwareDocumentFont;

public class GraphicsState
{
  Matrix ctm;
  float characterSpacing;
  float wordSpacing;
  float horizontalScaling;
  float leading;
  CMapAwareDocumentFont font;
  float fontSize;
  int renderMode;
  float rise;
  boolean knockout;

  public GraphicsState()
  {
    this.ctm = new Matrix();
    this.characterSpacing = 0.0F;
    this.wordSpacing = 0.0F;
    this.horizontalScaling = 1.0F;
    this.leading = 0.0F;
    this.font = null;
    this.fontSize = 0.0F;
    this.renderMode = 0;
    this.rise = 0.0F;
    this.knockout = true;
  }

  public GraphicsState(GraphicsState paramGraphicsState)
  {
    this.ctm = paramGraphicsState.ctm;
    this.characterSpacing = paramGraphicsState.characterSpacing;
    this.wordSpacing = paramGraphicsState.wordSpacing;
    this.horizontalScaling = paramGraphicsState.horizontalScaling;
    this.leading = paramGraphicsState.leading;
    this.font = paramGraphicsState.font;
    this.fontSize = paramGraphicsState.fontSize;
    this.renderMode = paramGraphicsState.renderMode;
    this.rise = paramGraphicsState.rise;
    this.knockout = paramGraphicsState.knockout;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.GraphicsState
 * JD-Core Version:    0.6.0
 */