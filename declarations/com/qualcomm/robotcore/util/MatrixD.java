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

public class MatrixD {

	protected double[][] mData;
	protected int mRows;
	protected int mCols;

	public MatrixD(int rows, int cols) {

	}

	public MatrixD(double[] init, int rows, int cols) {

	}

	public MatrixD(float[] init, int rows, int cols) {

	}

	public MatrixD(double[][] init) {

	}

	public int numRows() {
		return 0;
	}

	public int numCols() {
		return 0;
	}

	public double[][] data() {
		return null;
	}

	public MatrixD submatrix(int rows, int cols, int rowOffset, int colOffset) {
		return null;
	}


	public boolean setSubmatrix(MatrixD inData, int rows, int cols, int rowOffset, int colOffset) {
		return false;
	}

	public MatrixD transpose() {
		return null;
	}

	public MatrixD add(MatrixD other) {
		return null;
	}

	public MatrixD add(double val) {
		return null;
	}

	public MatrixD subtract(MatrixD other) {
		return null;
	}

	public MatrixD subtract(double val) {
		return null;
	}

	public MatrixD times(MatrixD other) {
		return null;
	}

	public MatrixD times(double f) {
		return null;
	}

	public double length() {
		return 0.0;
	}

	@Override
	public String toString() {
		return null;
	}

	public static void test() {

	}

}
