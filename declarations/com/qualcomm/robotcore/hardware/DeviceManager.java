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

package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.Map;

@SuppressWarnings("unused")
public abstract class DeviceManager {


  /**
   * Enum of Device Types
   */
  public enum DeviceType {
    FTDI_USB_UNKNOWN_DEVICE,
    MODERN_ROBOTICS_USB_UNKNOWN_DEVICE,
    MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER,
    MODERN_ROBOTICS_USB_SERVO_CONTROLLER,
    MODERN_ROBOTICS_USB_LEGACY_MODULE,
    MODERN_ROBOTICS_USB_SENSOR_MUX
  }

  /**
   * Get a listing of all Modern Robotics devices connected.
   * <p>
   * This method will attempt to open all USB devices that are using an FTDI USB chipset. It will
   * then probe the device to determine if it is a Modern Robotics device. Finally, it will close the
   * device.
   * <p>
   * Because of the opening and closing of devices, it is recommended that this method is not called
   * while any FTDI devices are in use.
   *
   * @return a map of serial numbers to Modern Robotics device types
   * @throws RobotCoreException if unable to open a device
   */
  public abstract Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException;

  /**
   * Create an instance of a DcMotorController
   *
   * @param serialNumber serial number of controller
   * @return an instance of a DcMotorController
   * @throws RobotCoreException if unable to create instance
   * @throws InterruptedException
   */
  public abstract DcMotorController createUsbDcMotorController(SerialNumber serialNumber)
      throws RobotCoreException, InterruptedException;

  /**
   * Create an instance of a DcMotor
   *
   * @param controller DC Motor controller this motor is attached to
   * @param portNumber physical port number on the controller
   * @return an instance of a DcMotor
   */
  public DcMotor createDcMotor(DcMotorController controller, int portNumber) {
    return new DcMotor(controller, portNumber, DcMotor.Direction.FORWARD);
  }

  /**
   * Create an instance of a ServoController
   *
   * @param serialNumber serial number of controller
   * @return an instance of a ServoController
   * @throws RobotCoreException if unable to create instance
   * @throws InterruptedException
   */
  public abstract ServoController createUsbServoController(SerialNumber serialNumber)
      throws RobotCoreException, InterruptedException;

  /**
   * Create an instance of a Servo
   *
   * @param controller Servo controller this servo is attached to
   * @param portNumber physical port number on the controller
   * @return an instance of a Servo
   */
  public Servo createServo(ServoController controller, int portNumber) {
    return new Servo(controller, portNumber, Servo.Direction.FORWARD);
  }

  /**
   * Create an instance of a LegacyModule
   *
   * @param serialNumber serial number of legacy module
   * @return an instance of a LegacyModule
   * @throws RobotCoreException if unable to create instance
   * @throws InterruptedException
   */
  public abstract LegacyModule createUsbLegacyModule(SerialNumber serialNumber)
      throws RobotCoreException, InterruptedException;

  /**
   * Create an instance of an NXT DcMotorController
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return a DcMotorController
   */
  public abstract DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort);

  /**
   * Create an instance of an NXT ServoController
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return a ServoController
   */
  public abstract ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort);

  /**
   * Create an instance of a NxtCompassSensor
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return a CompassSensor
   */
  public abstract CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort);

  /**
   * Create an instance of a AccelerationSensor
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return an AccelerationSensor
   */
  public abstract AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort);

  /**
   * Create an instance of a LightSensor
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return a LightSensor
   */
  public abstract LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort);

  /**
   * Create an instance of a IrSeekerSensor
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return a IrSeekerSensor
   */
  public abstract IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort);

  /**
   * Create an instance of an UltrasonicSensor
   * @param legacyModule Legacy Module this device is connected to
   * @param physicalPort port number on the Legacy Module this device is connected to
   * @return a UltrasonicSensor
   */
  public abstract UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a GyroSensor
     * @param legacyModule Legacy Module this device is connected to
     * @param physicalPort port number on the Legacy Module this device is connected to
     * @return a GyroSensor
     */
  public abstract GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort);
}
