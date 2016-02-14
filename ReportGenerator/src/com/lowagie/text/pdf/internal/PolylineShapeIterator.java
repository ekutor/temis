package com.lowagie.text.pdf.internal;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.NoSuchElementException;

public class PolylineShapeIterator
  implements PathIterator
{
  protected PolylineShape poly;
  protected AffineTransform affine;
  protected int index;

  PolylineShapeIterator(PolylineShape paramPolylineShape, AffineTransform paramAffineTransform)
  {
    this.poly = paramPolylineShape;
    this.affine = paramAffineTransform;
  }

  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone())
      throw new NoSuchElementException("line iterator out of bounds");
    int i = this.index == 0 ? 0 : 1;
    paramArrayOfDouble[0] = this.poly.x[this.index];
    paramArrayOfDouble[1] = this.poly.y[this.index];
    if (this.affine != null)
      this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
    return i;
  }

  public int currentSegment(float[] paramArrayOfFloat)
  {
    if (isDone())
      throw new NoSuchElementException("line iterator out of bounds");
    int i = this.index == 0 ? 0 : 1;
    paramArrayOfFloat[0] = this.poly.x[this.index];
    paramArrayOfFloat[1] = this.poly.y[this.index];
    if (this.affine != null)
      this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
    return i;
  }

  public int getWindingRule()
  {
    return 1;
  }

  public boolean isDone()
  {
    return this.index >= this.poly.np;
  }

  public void next()
  {
    this.index += 1;
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.internal.PolylineShapeIterator
 * JD-Core Version:    0.6.0
 */