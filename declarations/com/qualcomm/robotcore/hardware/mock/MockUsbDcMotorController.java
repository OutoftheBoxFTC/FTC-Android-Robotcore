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

package com.qualcomm.robotcore.hardware.mock;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.util.DifferentialControlLoopCoefficients;

/**
 * Mock USB DC Motor Controller
 *
 * This is an implementation of {@link DcMotorController}
 *
 * Use {@link MockDeviceManager} to create an instance of this class
 */
public class MockUsbDcMotorController extends MockUsbDevice implements DcMotorController {

	public static final byte MAX_ADDRESS = 0x21;
  /**
   * Use MockDeviceManager to create an instance of this class
   *
   * @see MockDeviceManager
   * @param serialNumber USB serial number
   * @param type device type
   */
  protected MockUsbDcMotorController(String serialNumber, DeviceType type) {
    super(serialNumber, type);
  }

  /**
   * Device Name
   *
   * @return device name
   */
  @Override
  public String getDeviceName() {
    return "Mock USB DC Motor Controller";
  }

  @Override
  public void setMotorControllerDeviceMode(DeviceMode mode) {

  }

  @Override
  public DeviceMode getMotorControllerDeviceMode() {
    return null;
  }

  @Override
  public void setMotorChannelMode(int motor, RunMode mode) {

  }

  @Override
  public RunMode getMotorChannelMode(int motor) {
    return RunMode.RUN_USING_ENCODERS;
  }

  @Override
  public void setMotorPower(int motor, double power) {

  }

  @Override
  public double getMotorPower(int motor) {
    return 0.3;
  }

  @Override
  public void setMotorPowerFloat(int motor) {
  }

  @Override
  public boolean getMotorPowerFloat(int motor) {

    return (true);
  }

  @Override
  public void setMotorTargetPosition(int motor, int position) {
  }

  @Override
  public int getMotorTargetPosition(int motor) {

    return (135);
  }

  @Override
  public int getMotorCurrentPosition(int motor) {

    return  (136);
  }

  @Override
  public void setGearRatio(int motor, double ratio) {

  }

  @Override
  public double getGearRatio(int motor) {
    return (double) 13.9;
  }

  @Override
  public void setDifferentialControlLoopCoefficients(int motor,
      DifferentialControlLoopCoefficients pid) {

  }

  @Override
  public DifferentialControlLoopCoefficients getDifferentialControlLoopCoefficients(int motor) {
    return new DifferentialControlLoopCoefficients();
  }

}
