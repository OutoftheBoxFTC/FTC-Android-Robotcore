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

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.Map;

public class MockDeviceManager extends DeviceManager {


	public Map<SerialNumber, DeviceType> devices;
	public MockUsbDcMotorController mock_dc = new MockUsbDcMotorController("7", DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER);
	public MockUsbServoController mock_servo = new MockUsbServoController("6", DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER);
	public MockUsbLegacyModule mock_legmod = new MockUsbLegacyModule("5", DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE);
	public MockNxtUltrasonicSensor mock_ultra = new MockNxtUltrasonicSensor(mock_legmod, 5);
	public MockNxtAccelerationSensor mock_accel = new MockNxtAccelerationSensor(mock_legmod, 4);
	public MockNxtCompassSensor mock_compass = new MockNxtCompassSensor(mock_legmod, 3);
	public MockNxtIrSeekerSensor mock_ir = new MockNxtIrSeekerSensor(mock_legmod, 2);
	public MockNxtLightSensor mock_light = new MockNxtLightSensor(mock_legmod, 1);

	public MockDeviceManager(Object context, EventLoopManager manager) throws RobotCoreException {

	}

	@Override
	public Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException {
		return null;
	}

	@Override
	public DcMotorController createUsbDcMotorController(SerialNumber serialNumber) {
		return null;
	}

	@Override
	public ServoController createUsbServoController(SerialNumber serialNumber) {
		return null;
	}

	@Override
	public LegacyModule createUsbLegacyModule(SerialNumber serialNumber) {
		return null;
	}

	@Override
	public DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

	@Override
	public GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort) {
		return null;
	}

}
