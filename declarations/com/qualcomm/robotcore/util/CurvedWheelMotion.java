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
 * This class consists of static utility functions that calculate robot motions for different drive
 * mechanisms.
 */
public class CurvedWheelMotion {

  /**
   * Given rotational velocity where (+) is CCW, calculate linear wheel velocity where (+) is
   * robot forward.
   */
  public static int velocityForRotationMmPerSec(int rotateAroundXInMM, int rotateAroundYInMM,
      double rotationalVelocityInDegPerSec, int wheelOffsetXInMm, int wheelOffsetYInMm) {

    int radius = (int) Math.sqrt(
        Math.pow((wheelOffsetXInMm - rotateAroundXInMM), 2) +
        Math.pow((wheelOffsetYInMm - rotateAroundYInMM), 2)
        );

    int wheelVelocityInMmPerSec = (int) (rotationalVelocityInDegPerSec *
        ((2 * Math.PI * radius) / 360));
    RobotLog.d("CurvedWheelMotion rX " + rotateAroundXInMM + ", theta " +
        rotationalVelocityInDegPerSec + ", velocity " +
        wheelVelocityInMmPerSec);

    return wheelVelocityInMmPerSec;
  }


  /**
   * Calculate the left or right wheel velocity for a differential drive robot given its
   * translational velocity and rotational velocities.
   * @param linearVelocityInMmPerSec robot linear velocity in mm per sec (+ve = forward, -ve = reverse)
   * @param rotationalVelocityInDegPerSec robot rotational velocity in degrees per sec (+ve = CCW, -ve = CW)
   * @param wheelRadiusInMm radius of each wheel in mm
   * @param axleLengthInMm axle length in mm
   * @param leftWheel boolean indicating this is for the left wheel (true) or right wheel (false)
   * @return translational left wheel velocity (along the ground)
   * @note for a skid-steering (4-wheel) robot, this calculation is approximate.
   */
  public static int getDiffDriveRobotWheelVelocity(final int linearVelocityInMmPerSec,
      final double rotationalVelocityInDegPerSec, final int wheelRadiusInMm,
      final int axleLengthInMm, boolean leftWheel) {
    // We need to implement the following equation
    // vleft = (2v – ωL) / 2R

    // convert robot velocity to radians per second
    final double rotationalVelocityInRadsPerSec = Math.toRadians(rotationalVelocityInDegPerSec);

    // calculate the left wheel rotational velocity
    final double wheelVelocityInRadsPerSec;
    if(leftWheel == true)
    {
      wheelVelocityInRadsPerSec =
          ((2 * linearVelocityInMmPerSec) - (rotationalVelocityInRadsPerSec * axleLengthInMm))
              / (2 * wheelRadiusInMm);
    }
    else
    {
      wheelVelocityInRadsPerSec =
          ((2 * linearVelocityInMmPerSec) + (rotationalVelocityInRadsPerSec * axleLengthInMm))
              / (2 * wheelRadiusInMm);
    }

    // calculate translational velocities (* R)
    final int wheelVelocityInMMPerSec = (int) (wheelVelocityInRadsPerSec * wheelRadiusInMm);

    return wheelVelocityInMMPerSec;
  }


  /**
   * Calculate the translational velocity for a differential drive robot given its left and right
   * wheel velocities.
   * @param leftVelocityInMmPerSec left wheel velocity in mm per sec (+ve = forward, -ve = reverse)
   * @param rightVelocityInMmPerSec right wheel velocity in mm per sec (+ve = forward, -ve = reverse)
   * @return robot translational (linear) velocity in mm per sec (+ve = forward, -ve = reverse)
   */
  public static int getDiffDriveRobotTransVelocity(final int leftVelocityInMmPerSec,
      final int rightVelocityInMmPerSec) {
    // V = (vleft + vright) / 2
    final int transVelInMMPerSec = (leftVelocityInMmPerSec + rightVelocityInMmPerSec) / 2;

    return transVelInMMPerSec;
  }


  /**
   * Calculate the rotational velocity for a differential drive robot given its left and right
   * wheel velocities.
   * @param leftVelocityInMmPerSec left wheel velocity in mm per sec (+ve = forward, -ve = reverse)
   * @param rightVelocityInMmPerSec right wheel velocity in mm per sec (+ve = forward, -ve = reverse)
   * @param axleLengthInMm length of axle (distance between the wheels) in mm
   * @return robot rotational velocity in degrees per second (+ve = CCW, -ve = CW)
   */
  public static double getDiffDriveRobotRotVelocity(final int leftVelocityInMmPerSec,
      final int rightVelocityInMmPerSec, final int axleLengthInMm) {
    // ω = (vright + vleft) / L
    final double rotVelocityInRadiansPerSec =
        (rightVelocityInMmPerSec - leftVelocityInMmPerSec) / axleLengthInMm;

    final double rotVelocityInDegreesPerSec = Math.toDegrees(rotVelocityInRadiansPerSec);

    return rotVelocityInDegreesPerSec;
  }
}
