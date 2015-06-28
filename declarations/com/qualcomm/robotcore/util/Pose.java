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


/**
 * Defines the pose of a target
 */

public class Pose {

  // Position in mm
  public double transX;
  public double transY;
  public double transZ;

  public MatrixD poseMatrix;

  // Angles in degrees
//  public double angleX;
//  public double angleY;
//  public double angleZ;

  /**
   * Construct a pose from a pose matrix
   * @param poseMatrix Matrix from which to construct the pose
   */
  public Pose(MatrixD poseMatrix) {
    if (poseMatrix == null) {
      throw new IllegalArgumentException("Attempted to construct Pose from null matrix");
    }

    //Check the matrix dimension

    if( poseMatrix.numRows() != 3 || poseMatrix.numCols() !=4){
      throw new IllegalArgumentException("Invalid matrix size ( " +
                      poseMatrix.numRows() + ", " + poseMatrix.numCols() + " )");
    }

    this.poseMatrix = poseMatrix;

    transX = poseMatrix.data()[0][3];
    transY = poseMatrix.data()[1][3];
    transZ = poseMatrix.data()[2][3];

  }


  public Pose( double transX, double transY, double transZ) {
     this.transX = transX;
     this.transY = transY;
     this.transZ = transZ;

     //Create an identity matrix

     poseMatrix = new MatrixD(3,4);
     poseMatrix.data()[0][0] = poseMatrix.data()[1][1] = poseMatrix.data()[2][2] = 1;
     poseMatrix.data()[0][3] = transX;
     poseMatrix.data()[1][3] = transY;
     poseMatrix.data()[2][3] = transZ;

    }

  /**
   * Default constructor
   */
  public Pose() {
    transX = 0;
    transY = 0;
    transZ = 0;
    }

//  /**
//   * create and return the pose matrix
//   * @return pose matrix
//   */
//  public MatrixD getPoseMatrix() {
//    final MatrixD targetRotationMatrix = getRotationMatrix();
//    final float[][] t = { {(float) transX}, {(float) transY}, {(float) transZ}};
//    final MatrixD targetTranslationMatrix = new MatrixD(t);
//    final MatrixD targetPoseMatrix =
//        PoseUtils.poseMatrix(targetRotationMatrix, targetTranslationMatrix);
//    return targetPoseMatrix;
//
//  }

  /**
   * Create and return the translation matrix
   * @return translation matrix
   */
  public MatrixD getTranslationMatrix() {
    final double[][] t = { {transX}, {transY}, {transZ}};
    final MatrixD targetTranslationMatrix = new MatrixD(t);
    return targetTranslationMatrix;

  }

//  /**
//   * Create and return the rotation matrix
//   * @return Rotation matrix
//   */
//  public MatrixD getRotationMatrix() {
//    return getRotationMatrix((int) angleX, (int) angleY, (int) angleZ);
//  }

  /**
   * Create and return a rotation matrix for a right handed coordinate sytem
   * @param angle Angle around x-axis
   * @return Rotation matrix
   */
  static public MatrixD makeRotationX(double angle){

    //Create the 2D matrix

    double[][] data = new double[3][3];
    double cosA = Math.cos(angle);
    double sinA = Math.sin(angle);

    data[0][0] = 1;
    data[0][1] = data[0][2] = data[1][0] = data[2][0] =  0;

    data[1][1] = data[2][2] = cosA;
    data[1][2] = -sinA;
    data[2][1] = sinA;

    return (new MatrixD(data));
  }

  /**
   * Create and return a rotation matrix for a right handed coordinate sytem
   * @param angle Angle around y-axis
   * @return Rotation matrix
   */
  static public MatrixD makeRotationY(double angle){

    //Create the 2D matrix

    double[][] data = new double[3][3];
    double cosA = Math.cos(angle);
    double sinA = Math.sin(angle);

    //Set the
    data[0][1] = data[1][0] = data[1][2] = data[2][1] =  0;
    data[1][1] = 1;

    data[0][0] = data[2][2] = cosA;
    data[0][2] = sinA;
    data[2][0] = -sinA;

    return (new MatrixD(data));
  }

  /**
   * Create and return a rotation matrix for a right handed coordinate sytem
   * @param angle Angle around z-axis
   * @return Rotation matrix
   */
  static public MatrixD makeRotationZ(double angle){

    //Create the 2D matrix

    double[][] data = new double[3][3];
    double cosA = Math.cos(angle);
    double sinA = Math.sin(angle);

    data[2][2] = 1;
    data[2][0] = data[2][1] = data[0][2] = data[1][2] =  0;

    data[0][0] = data[1][1] = cosA;
    data[0][1] = -sinA;
    data[1][0] = sinA;

    return (new MatrixD(data));
  }

/** Returns a string representation for the pose
   * @return string representation for the pose
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    //Get the angles

    double[] angles = PoseUtils.getAnglesAroundZ(this);

    sb.append(String.format("(XYZ %1$,.2f ", transX));
    sb.append(String.format(" %1$,.2f ", transY));
    sb.append(String.format(" %1$,.2f mm)", transZ));

    sb.append(String.format("(Angles %1$,.2f, ", angles[0]));
    sb.append(String.format(" %1$,.2f, ", angles[1]));
    sb.append(String.format(" %1$,.2f ", angles[2]));
    sb.append((char) 0x00B0);
    sb.append(")");

    return sb.toString();
  }

  /**
   * Calculates the distance
   * @return distance
   */
  public double getDistanceInMm() {
    return Math.sqrt(Math.pow(transX, 2) + Math.pow(transY, 2) + Math.pow(transZ, 2));
  }


}

