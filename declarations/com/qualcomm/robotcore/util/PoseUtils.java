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


/**
 * Utility functions for pose operations
 */
public class PoseUtils {

  // Given a pose, extract angles around Z axes, the angles are X,Y and Up


  static public double[] getAnglesAroundZ(Pose inputPose) {
    double[] retVal = null;

    if (inputPose != null && inputPose.poseMatrix != null) {
      MatrixD rotMat = inputPose.poseMatrix.submatrix(3, 3, 0, 0);
      retVal = getAnglesAroundZ(rotMat);
    } else {
      Log.e("PoseUtils", "null input");
    }

    return retVal;
  }



  static public double[] getAnglesAroundZ(MatrixD rotMat) {

    // Check the dimension

    if (rotMat.numRows() != 3 || rotMat.numCols() != 3) {
      throw new IllegalArgumentException("Invalid Matrix Dimension: Expected (3,3) got ("
          + rotMat.numRows() + "," + rotMat.numCols() + ")");
    }
    // Extract the unit vector along the Z axis

    // This will be the last column of the rotation matrix in the pose matrix

    // For sake of completeness, multiply the rot matrix with a unit vector along the z axis

    double[][] unitVec = new double[][] { {0}, {0}, {1}};

    MatrixD rotComponentZ = new MatrixD(unitVec);
    rotComponentZ = rotMat.times(rotComponentZ);

    // Extract the angles

    double headingX = Math.atan2(rotComponentZ.data()[1][0], rotComponentZ.data()[0][0]);
    headingX = Math.toDegrees(headingX);

    double headingY = Math.atan2(rotComponentZ.data()[0][0], rotComponentZ.data()[1][0]);
    headingY = Math.toDegrees(headingY);

    double length = rotComponentZ.length();

    double headingUp = Math.asin(rotComponentZ.data()[2][0] / length);
    headingUp = Math.toDegrees(headingUp);

    double[] retVal = {headingX, headingY, headingUp};
    return (retVal);
  }

  /**
   * Given two angles in degrees, determine the smallest angle between them.
   *
   * For example, given angles 90 and 170, the smallest angle difference would be 80
   * and the larger angle difference would be 280.
   *
   * @param firstAngleDeg
   * @param secondAngleDeg
   * @return smallest angle
   */
  public static double smallestAngularDifferenceDegrees(double firstAngleDeg, double secondAngleDeg) {
    double rawDiffDeg = firstAngleDeg - secondAngleDeg;
    double rawDiffRad = rawDiffDeg * Math.PI / 180;
    double wrappedDiffRad = Math.atan2(Math.sin(rawDiffRad), Math.cos(rawDiffRad));
    double wrappedDiffDeg = wrappedDiffRad * 180 / Math.PI;

    return wrappedDiffDeg;
  }


}
