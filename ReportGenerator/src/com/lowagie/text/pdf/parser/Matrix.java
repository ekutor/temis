package com.lowagie.text.pdf.parser;

import java.util.Arrays;

public class Matrix
{
  public static final int I11 = 0;
  public static final int I12 = 1;
  public static final int I13 = 2;
  public static final int I21 = 3;
  public static final int I22 = 4;
  public static final int I23 = 5;
  public static final int I31 = 6;
  public static final int I32 = 7;
  public static final int I33 = 8;
  private final float[] vals = { 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F };

  public Matrix()
  {
  }

  public Matrix(float paramFloat1, float paramFloat2)
  {
    this.vals[6] = paramFloat1;
    this.vals[7] = paramFloat2;
  }

  public Matrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.vals[0] = paramFloat1;
    this.vals[1] = paramFloat2;
    this.vals[2] = 0.0F;
    this.vals[3] = paramFloat3;
    this.vals[4] = paramFloat4;
    this.vals[5] = 0.0F;
    this.vals[6] = paramFloat5;
    this.vals[7] = paramFloat6;
    this.vals[8] = 1.0F;
  }

  public float get(int paramInt)
  {
    return this.vals[paramInt];
  }

  public Matrix multiply(Matrix paramMatrix)
  {
    Matrix localMatrix = new Matrix();
    float[] arrayOfFloat1 = this.vals;
    float[] arrayOfFloat2 = paramMatrix.vals;
    float[] arrayOfFloat3 = localMatrix.vals;
    arrayOfFloat3[0] = (arrayOfFloat1[0] * arrayOfFloat2[0] + arrayOfFloat1[1] * arrayOfFloat2[3] + arrayOfFloat1[2] * arrayOfFloat2[6]);
    arrayOfFloat3[1] = (arrayOfFloat1[0] * arrayOfFloat2[1] + arrayOfFloat1[1] * arrayOfFloat2[4] + arrayOfFloat1[2] * arrayOfFloat2[7]);
    arrayOfFloat3[2] = (arrayOfFloat1[0] * arrayOfFloat2[2] + arrayOfFloat1[1] * arrayOfFloat2[5] + arrayOfFloat1[2] * arrayOfFloat2[8]);
    arrayOfFloat3[3] = (arrayOfFloat1[3] * arrayOfFloat2[0] + arrayOfFloat1[4] * arrayOfFloat2[3] + arrayOfFloat1[5] * arrayOfFloat2[6]);
    arrayOfFloat3[4] = (arrayOfFloat1[3] * arrayOfFloat2[1] + arrayOfFloat1[4] * arrayOfFloat2[4] + arrayOfFloat1[5] * arrayOfFloat2[7]);
    arrayOfFloat3[5] = (arrayOfFloat1[3] * arrayOfFloat2[2] + arrayOfFloat1[4] * arrayOfFloat2[5] + arrayOfFloat1[5] * arrayOfFloat2[8]);
    arrayOfFloat3[6] = (arrayOfFloat1[6] * arrayOfFloat2[0] + arrayOfFloat1[7] * arrayOfFloat2[3] + arrayOfFloat1[8] * arrayOfFloat2[6]);
    arrayOfFloat3[7] = (arrayOfFloat1[6] * arrayOfFloat2[1] + arrayOfFloat1[7] * arrayOfFloat2[4] + arrayOfFloat1[8] * arrayOfFloat2[7]);
    arrayOfFloat3[8] = (arrayOfFloat1[6] * arrayOfFloat2[2] + arrayOfFloat1[7] * arrayOfFloat2[5] + arrayOfFloat1[8] * arrayOfFloat2[8]);
    return localMatrix;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Matrix))
      return false;
    return Arrays.equals(this.vals, ((Matrix)paramObject).vals);
  }

  public int hashCode()
  {
    int i = 1;
    for (int j = 0; j < this.vals.length; j++)
      i = 31 * i + Float.floatToIntBits(this.vals[j]);
    return i;
  }

  public String toString()
  {
    return this.vals[0] + "\t" + this.vals[1] + "\t" + this.vals[2] + "\n" + this.vals[3] + "\t" + this.vals[4] + "\t" + this.vals[2] + "\n" + this.vals[6] + "\t" + this.vals[7] + "\t" + this.vals[8];
  }
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.lowagie.text.pdf.parser.Matrix
 * JD-Core Version:    0.6.0
 */