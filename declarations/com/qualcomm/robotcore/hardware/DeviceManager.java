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

public abstract class DeviceManager {

	public enum DeviceType {
		FTDI_USB_UNKNOWN_DEVICE,
		MODERN_ROBOTICS_USB_UNKNOWN_DEVICE,
		MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER,
		MODERN_ROBOTICS_USB_SERVO_CONTROLLER,
		MODERN_ROBOTICS_USB_LEGACY_MODULE,
		MODERN_ROBOTICS_USB_SENSOR_MUX
	}

	public abstract Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException;

	public abstract DcMotorController createUsbDcMotorController(SerialNumber serialNumber)
			throws RobotCoreException, InterruptedException;

	public DcMotor createDcMotor(DcMotorController controller, int portNumber) {
		return new DcMotor(controller, portNumber, DcMotor.Direction.FORWARD);
	}

	public abstract ServoController createUsbServoController(SerialNumber serialNumber)
			throws RobotCoreException, InterruptedException;

	public Servo createServo(ServoController controller, int portNumber) {
		return new Servo(controller, portNumber, Servo.Direction.FORWARD);
	}

	public abstract LegacyModule createUsbLegacyModule(SerialNumber serialNumber)
			throws RobotCoreException, InterruptedException;

	public abstract DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort);

	public abstract ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort);

	public abstract CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort);

	public abstract AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort);

	public abstract LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort);

	public abstract IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort);

	public abstract UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort);

	public abstract GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort);

}
