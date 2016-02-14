package com.lowagie.text.pdf.internal;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class PolylineShape
  implements Shape
{
  protected int[] x;
  protected int[] y;
  protected int np;

  public PolylineShape(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    this.np = paramInt;
    this.x = new int[this.np];
    this.y = new int[this.np];
    System.arraycopy(paramArrayOfInt1, 0, this.x, 0, this.np);
    System.arraycopy(paramArrayOfInt2, 0, this.y, 0, this.np);
  }

  public Rectangle2D getBounds2D()
  {
    int[] arrayOfInt = rect();
    return arrayOfInt == null ? null : new Rectangle2D.Double(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
  }

  public Rectangle getBounds()
  {
    return getBounds2D().getBounds();
  }

  private int[] rect()
  {
    if (this.np == 0)
      return null;
    int i = this.x[0];
    int j = this.y[0];
    int k = this.x[0];
    int m = this.y[0];
    for (int n = 1; n < this.np; n++)
    {
      if (this.x[n] < i)
        i = this.x[n];
      else if (this.x[n] > k)
        k = this.x[n];
      if (this.y[n] < j)
      {
        j = this.y[n];
      }
      else
      {
        if (this.y[n] <= m)
          continue;
        m = this.y[n];
      }
    }
    return new int[] { i, j, k - i, m - j };
  }

  public boolean contains(double paramDouble1, double paramDouble2)
  {
    return false;
  }

  public boolean contains(Point2D paramPoint2D)
  {
    return false;
  }

  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return false;
  }

  public boolean contains(Rectangle2D paramRectangle2D)
  {
    return false;
  }

  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return intersects(new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4));
  }

  public boolean intersects(Rectangle2D paramRectangle2D)
  {
    if (this.np == 0)
      return false;
    Line2D.Double localDouble = new Line2D.Double(this.x[0], this.y[0], this.x[0], this.y[0]);
    for (int i = 1; i < this.np; i++)
    {
      localDouble.setLine(this.x[(i - 1)], this.y[(i - 1)], this.x[i], this.y[i]);
      if (localDouble.intersects(paramRectangle2D))
        return true;
    }
    return false;
  }

  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new PolylineShapeIterator(this, paramAffineTransform);
  }

  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble)
  {
    return new PolylineShapeIterator(this, paramAffineTransform);
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.internal.PolylineShape
 * JD-Core Version:    0.6.0
 */