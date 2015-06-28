/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.Range;

/**
 * Control a single servo
 */
public class Servo {

  /**
   * Motor direction
   */
  public enum Direction { FORWARD, REVERSE };

  public final static double MIN_POSITION = 0.0;
  public final static double MAX_POSITION = 1.0;

  protected ServoController controller = null;
  protected int portNumber = -1;

  protected Direction direction = Direction.FORWARD;
  protected double minPosition = 0.0;
  protected double maxPosition = 1.0;

  /**
   * Constructor
   * @param controller Servo controller that this servo is attached to
   * @param portNumber physical port number on the servo controller
   */
  public Servo(ServoController controller, int portNumber) {
    this(controller, portNumber, Direction.FORWARD);
  }

  /**
   * COnstructor
   * @param controller Servo controller that this servo is attached to
   * @param portNumber physical port number on the servo controller
   * @param direction FORWARD for normal operation, REVERSE to reverse operation
   */
  public Servo(ServoController controller, int portNumber, Direction direction) {
    this.direction = direction;
    this.controller = controller;
    this.portNumber = portNumber;
  }

  /**
   * Get Servo Controller
   * @return servo controller
   */
  public ServoController getController() {
    return controller;
  }

  /**
   * Set the direction
   * @param direction direction
   */
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * Get the direction
   * @return direction
   */
  public Direction getDirection() {
    return direction;
  }

  /**
   * Get Channel
   * @return channel
   */
  public int getPortNumber() {
    return portNumber;
  }

  /**
   * Set the position of the servo
   * @param position from 0.0 to 1.0
   */
  public void setPosition(double position) {
    if (direction == Direction.REVERSE) position = reverse(position);
    double scaled = Range.scale(position, MIN_POSITION, MAX_POSITION, minPosition, maxPosition);
    controller.setServoPosition(portNumber, scaled);
  }

  /**
   * Get the position of the servo
   * @return position, scaled from 0.0 to 1.0
   */
  public double getPosition() {
    double position = controller.getServoPosition(portNumber);
    if (direction == Direction.REVERSE) position = reverse(position);
    double scaled = Range.scale(position, minPosition, maxPosition, MIN_POSITION, MAX_POSITION);
    return Range.clip(scaled, MIN_POSITION, MAX_POSITION);
  }

  /**
   * Automatically scale the position of the servo.
   * <p>
   * For example, if scaleRange(0.2, 0.8) is set; then servo positions will be
   * scaled to fit in that range.<br>
   * setPosition(0.0) scales to 0.2<br>
   * setPosition(1.0) scales to 0.8<br>
   * setPosition(0.5) scales to 0.5<br>
   * setPosition(0.25) scales to 0.35<br>
   * setPosition(0.75) scales to 0.65<br>
   * <p>
   * This is useful if you don't want the servo to move past a given position,
   * but don't want to manually scale the input to setPosition each time.
   * getPosition() will scale the value back to a value between 0.0 and 1.0. If
   * you need to know the actual position use
   * Servo.getController().getServoPosition(Servo.getChannel()).
   *
   * @param min
   *           minimum position of the servo from 0.0 to 1.0
   * @param max
   *           maximum position of the servo from 0.0 to 1.0
   * @throws IllegalArgumentException if out of bounds, or min >= max
   */
  public void scaleRange(double min, double max) throws IllegalArgumentException {
    Range.throwIfRangeIsInvalid(min, MIN_POSITION, MAX_POSITION);
    Range.throwIfRangeIsInvalid(max, MIN_POSITION, MAX_POSITION);

    if (min >= max) {
      throw new IllegalArgumentException("min must be less than max");
    }

    minPosition = min;
    maxPosition = max;
  }

  private double reverse(double position) {
    return MAX_POSITION - position + MIN_POSITION;
  }
}
