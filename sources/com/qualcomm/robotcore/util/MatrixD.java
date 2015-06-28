/*
 * Copyright (c) 2014 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.util;

import android.util.Log;

public class MatrixD {

  private static final String TAG = "MatrixD";
  protected double[][] mData;
  protected int mRows;
  protected int mCols;


  public MatrixD(int rows, int cols) {
    this(new double[rows][cols]);
  }

  public MatrixD(double[] init, int rows, int cols) {
    this(rows, cols);

    if (init == null) {
      throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
    }

    if (init.length != (rows * cols)) {
      throw new IllegalArgumentException(
          "Attempted to initialize MatrixF with rows/cols not matching init data");
    }

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        mData[row][col] = init[col + (cols * row)];
      }
    }
  }

  public MatrixD(float[] init, int rows, int cols) {
    this(rows, cols);

    if (init == null) {
      throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
    }

    if (init.length != (rows * cols)) {
      throw new IllegalArgumentException(
          "Attempted to initialize MatrixF with rows/cols not matching init data");
    }

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        mData[row][col] =    init[col + (cols * row)];
      }
    }
  }

  public MatrixD(double[][] init) {
    mData = init;
    if (mData == null) {
      throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
    }
    mRows = mData.length;
    if (mRows <= 0) {
      throw new IllegalArgumentException("Attempted to initialize MatrixF with 0 rows");
    }

    mCols = mData[0].length;
    for (int row = 0; row < mRows; row++) {
      if (mData[row].length != mCols) {
        throw new IllegalArgumentException(
            "Attempted to initialize MatrixF with rows of unequal length");
      }
    }
  }

  public int numRows() {
    return mRows;
  }

  public int numCols() {
    return mCols;
  }

  public double[][] data() {
    return mData;
  }

  public MatrixD submatrix(int rows, int cols, int rowOffset, int colOffset) {
    if ((rows > numRows()) || (cols > numCols())) {
      throw new IllegalArgumentException(
          "Attempted to get submatrix with size larger than original");
    }
    if (((rowOffset + rows) > numRows()) || ((colOffset + cols) > numCols())) {
      throw new IllegalArgumentException(
          "Attempted to access out of bounds data with row or col offset out of range");
    }

    final double[][] result = new double[rows][cols];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        result[row][col] = data()[rowOffset + row][colOffset + col];
      }
    }
    return new MatrixD(result);
  }


  public boolean setSubmatrix(MatrixD inData, int rows, int cols, int rowOffset, int colOffset) {

    if (inData == null) {
      throw new IllegalArgumentException("Input data to setSubMatrix null");

    }
    if ((rows > numRows()) || (cols > numCols())) {
      throw new IllegalArgumentException(
          "Attempted to get submatrix with size larger than original");
    }

    if (((rowOffset + rows) > numRows()) || ((colOffset + cols) > numCols())) {
      throw new IllegalArgumentException(
          "Attempted to access out of bounds data with row or col offset out of range");
    }

    if ((rows > inData.numRows()) || (cols > inData.numCols())) {
      throw new IllegalArgumentException("Input matrix small for setSubMatrix");
    }

    if (((rowOffset + rows) > inData.numRows()) || ((colOffset + cols) > numCols())) {
      throw new IllegalArgumentException(
          "Input matrix Attempted to access out of bounds data with row or col offset out of range");
    }

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        data()[rowOffset + row][colOffset + col] = inData.data()[row][col];
      }
    }
    return true;
  }



  public MatrixD transpose() {
    final int tCols = mRows;
    final int tRows = mCols;
    final double[][] transposed = new double[tRows][tCols];
    for (int tRow = 0; tRow < tRows; tRow++) {
      for (int tCol = 0; tCol < tCols; tCol++) {
        transposed[tRow][tCol] = mData[tCol][tRow];
      }
    }
    return new MatrixD(transposed);
  }

  public MatrixD add(MatrixD other) {
    final double[][] result = new double[numRows()][numCols()];
    final int resRows = numRows();
    final int resCols = numCols();

    for (int row = 0; row < resRows; row++) {
      for (int col = 0; col < resCols; col++) {
        result[row][col] = data()[row][col] + other.data()[row][col];
      }
    }
    return new MatrixD(result);


  }



  public MatrixD add(double val) {
    final double[][] result = new double[numRows()][numCols()];
    final int resRows = numRows();
    final int resCols = numCols();

    for (int row = 0; row < resRows; row++) {
      for (int col = 0; col < resCols; col++) {
        result[row][col] = data()[row][col] + val;
      }
    }
    return new MatrixD(result);


  }



  public MatrixD subtract(MatrixD other) {
    final double[][] result = new double[numRows()][numCols()];
    final int resRows = numRows();
    final int resCols = numCols();

    for (int row = 0; row < resRows; row++) {
      for (int col = 0; col < resCols; col++) {
        result[row][col] = data()[row][col] - other.data()[row][col];
      }
    }
    return new MatrixD(result);


  }



  public MatrixD subtract(double val) {
    final double[][] result = new double[numRows()][numCols()];
    final int resRows = numRows();
    final int resCols = numCols();

    for (int row = 0; row < resRows; row++) {
      for (int col = 0; col < resCols; col++) {
        result[row][col] = data()[row][col] - val;
      }
    }
    return new MatrixD(result);


  }



  public MatrixD times(MatrixD other) {
    if (numCols() != other.numRows()) {
      throw new IllegalArgumentException(
          "Attempted to multiply matrices of invalid dimensions (AB) where A is " + numRows() + "x"
              + numCols() + ", B is " + other.numRows() + "x" + other.numCols());
    }

    final int mulDim = numCols();

    final int resRows = numRows();
    final int resCols = other.numCols();
    final double[][] result = new double[resRows][resCols];
    for (int row = 0; row < resRows; row++) {
      for (int col = 0; col < resCols; col++) {
        for (int i = 0; i < mulDim; i++) {
          result[row][col] += data()[row][i] * other.data()[i][col];
        }
      }
    }
    return new MatrixD(result);
  }

  public MatrixD times(double f) {
    final double[][] result = new double[numRows()][numCols()];
    for (int row = 0; row < numRows(); row++) {
      for (int col = 0; col < numCols(); col++) {
        result[row][col] = data()[row][col] * f;
      }
    }
    return new MatrixD(result);
  }


  //Length of a 1D matrix, the matrix has to be 1D (Vector)
  public double length() {

    if( !(numRows() == 1 || numCols() == 1) ) {
      throw new IndexOutOfBoundsException("Not a 1D matrix ( " + numRows() + ", " + numCols() + " )");
    }

    double sum = 0d;

    for(int i = 0; i< numRows() ; i++){
      for(int j = 0; j< numCols() ; j++){
        sum+=mData[i][j] * mData[i][j];
      }
    }

    return Math.sqrt(sum);

  }

  @Override
  public String toString() {
    String resultStr = new String();
    for (int row = 0; row < numRows(); row++) {
      String rowStr = new String();
      for (int col = 0; col < numCols(); col++) {
        rowStr += String.format("%.4f", data()[row][col]);
        if (col < (numCols() - 1)) {
          rowStr += ", ";
        }
      }
      resultStr += rowStr;
      if (row < (numRows() - 1)) {
        resultStr += "\n";
      }
    }
    resultStr += "\n";
    return resultStr;
  }

  public static void test() {
    Log.e(TAG, "Hello2 matrix");

    final MatrixD A = new MatrixD(new double[][] { {1, 0, -2}, {0, 3, -1}});
    Log.e(TAG, "Hello3 matrix");

    Log.e(TAG, "A = \n" + A.toString());

    final MatrixD B = new MatrixD(new double[][] { {0, 3}, {-2, -1}, {0, 4}});
    Log.e(TAG, "B = \n" + B.toString());

    final MatrixD AT = A.transpose();
    Log.e(TAG, "A transpose = " + AT.toString());

    final MatrixD BT = B.transpose();
    Log.e(TAG, "B transpose = " + BT.toString());

    final MatrixD AB = A.times(B);
    Log.e(TAG, "AB = \n" + AB.toString());

    final MatrixD BA = B.times(A);
    Log.e(TAG, "BA = \n" + BA.toString());

    final MatrixD BA2 = BA.times((double) 2.0);
    Log.e(TAG, "BA*2 = " + BA2.toString());

    final MatrixD BASub = BA.submatrix(3, 2, 0, 1);
    Log.e(TAG, "BA submatrix 3,2,0,1 = " + BASub);

    final MatrixD BASub2 = BA.submatrix(2, 1, 1, 2);
    Log.e(TAG, "BA submatrix 2,1,1,2 = " + BASub2);


    /**
     *
     * 06-06 04:28:31.510: E/MatrixF(1528): Hello2 matrix 06-06 04:28:31.510: E/MatrixF(1528):
     * Hello3 matrix 06-06 04:28:31.510: E/MatrixF(1528): A = 06-06 04:28:31.510: E/MatrixF(1528):
     * 1.0, 0.0, -2.0 06-06 04:28:31.510: E/MatrixF(1528): 0.0, 3.0, -1.0 06-06 04:28:31.510:
     * E/MatrixF(1528): B = 06-06 04:28:31.510: E/MatrixF(1528): 0.0, 3.0 06-06 04:28:31.510:
     * E/MatrixF(1528): -2.0, -1.0 06-06 04:28:31.510: E/MatrixF(1528): 0.0, 4.0 06-06 04:28:31.510:
     * E/MatrixF(1528): A transpose = 1.0, 0.0 06-06 04:28:31.510: E/MatrixF(1528): 0.0, 3.0 06-06
     * 04:28:31.510: E/MatrixF(1528): -2.0, -1.0 06-06 04:28:31.510: E/MatrixF(1528): B transpose =
     * 0.0, -2.0, 0.0 06-06 04:28:31.510: E/MatrixF(1528): 3.0, -1.0, 4.0 06-06 04:28:31.510:
     * E/MatrixF(1528): AB = 06-06 04:28:31.510: E/MatrixF(1528): 0.0, -5.0 06-06 04:28:31.510:
     * E/MatrixF(1528): -6.0, -7.0 06-06 04:28:31.510: E/MatrixF(1528): BA = 06-06 04:28:31.510:
     * E/MatrixF(1528): 0.0, 9.0, -3.0 06-06 04:28:31.510: E/MatrixF(1528): -2.0, -3.0, 5.0 06-06
     * 04:28:31.510: E/MatrixF(1528): 0.0, 12.0, -4.0 06-06 04:28:31.510: E/MatrixF(1528): BA*2 =
     * 0.0, 18.0, -6.0 06-06 04:28:31.510: E/MatrixF(1528): -4.0, -6.0, 10.0 06-06 04:28:31.510:
     * E/MatrixF(1528): 0.0, 24.0, -8.0 06-06 04:28:31.510: E/MatrixF(1528): BA submatrix 3,2,0,1 =
     * 9.0, -3.0 06-06 04:28:31.510: E/MatrixF(1528): -3.0, 5.0 06-06 04:28:31.510: E/MatrixF(1528):
     * 12.0, -4.0 06-06 04:28:31.510: E/MatrixF(1528): BA submatrix 2,1,1,2 = 5.0 06-06
     * 04:28:31.510: E/MatrixF(1528): -4.0
     */
  }
}
