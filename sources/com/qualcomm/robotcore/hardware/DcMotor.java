/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

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

/**
 * Control a DC Motor attached to a DC Motor Controller
 *
 * @see com.qualcomm.robotcore.hardware.DcMotorController
 */
public class DcMotor {

  /**
   * Motor direction
   */
  public enum Direction { FORWARD, REVERSE };

  protected Direction direction = Direction.FORWARD;
  protected DcMotorController controller = null;
  protected int portNumber = -1;
  protected DcMotorController.RunMode mode = DcMotorController.RunMode.RUN_WITHOUT_ENCODERS;
  protected DcMotorController.DeviceMode devMode = DcMotorController.DeviceMode.WRITE_ONLY;


  /**
   * Constructor
   *
   * @param controller DC motor controller this motor is attached to
   * @param portNumber portNumber position on the controller
   */
  public DcMotor(DcMotorController controller, int portNumber) {
    this(controller, portNumber, Direction.FORWARD);
  }

  /**
   * Constructor
   *
   * @param controller DC motor controller this motor is attached to
   * @param portNumber portNumber port number on the controller
   * @param direction direction this motor should spin
   */
  public DcMotor(DcMotorController controller, int portNumber, Direction direction) {
    this.controller = controller;
    this.portNumber = portNumber;
    this.direction = direction;
  }

  /**
   * Get DC motor controller
   *
   * @return controller
   */
  public DcMotorController getController() {
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
   * Get port number
   *
   * @return portNumber
   */
  public int getPortNumber() {
    return portNumber;
  }

  /**
   * Set the current motor power
   *
   * @param power from -1.0 to 1.0
   */
  public void setPower(double power) {
    if (direction == Direction.REVERSE) power *= -1;
    if (mode == DcMotorController.RunMode.RUN_TO_POSITION) power = Math.abs(power);
    controller.setMotorPower(portNumber, power);
  }

  /**
   * Get the current motor power
   *
   * @return scaled from -1.0 to 1.0
   */
  public double getPower() {
    double power = controller.getMotorPower(portNumber);
    if (direction == Direction.REVERSE && power != 0.0) power *= -1;
    return power;
  }

  /**
   * Allow motor to float
   */
  public void setPowerFloat() {
    controller.setMotorPowerFloat(portNumber);
  }

  /**
   * Is motor power set to float?
   *
   * @return true of motor is set to float
   */
  public boolean getPowerFloat() {
    return controller.getMotorPowerFloat(portNumber);
  }

  /**
   * Set the motor target position, where 1.0 is one full rotation
   * Motor power should be positive if using run to position
   *  @param position range from Integer.MIN_VALUE to Integer.MAX_VALUE
   *
   *
   */
  public void setTargetPosition(int position){
    controller.setMotorTargetPosition(portNumber, position);
  }

  /**
   * Get the current motor target position
   *
   * @return scaled, where 1.0 is one full rotation
   */
  public int getTargetPosition(){
    return controller.getMotorTargetPosition(portNumber);
  }

  /**
   * Get the current encoder value
   *
   * @return double indicating current position
   */
  public int getCurrentPosition(){
    return controller.getMotorCurrentPosition(portNumber);
  }

  /**
   * Set the current channel mode
   *
   * @param mode run mode
   */
  public void setChannelMode(DcMotorController.RunMode mode) {
    this.mode = mode;
    controller.setMotorChannelMode(portNumber, mode);
  }


  /**
   * Get the current channel mode
   *
   * @return run mode
   */
  public DcMotorController.RunMode getChannelMode(){
    return controller.getMotorChannelMode(portNumber);
  }

  /**
   * mock API
   *
   * TODO: decide if we want to remove or implement this API
   */

  public interface MotorCallback {
    public void encoder(int value);
  }

  public void getEncoders(MotorCallback calllback) {

  }
}
