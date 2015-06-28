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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Hardware Mappings
 *
 * By default this creates a bunch of empty mappings between a string and an
 * instance of a hardware driver.
 */
public class HardwareMap {
  public static class DeviceMapping<DEVICE_TYPE> implements Iterable<DEVICE_TYPE> {
    private Map <String, DEVICE_TYPE> map = new HashMap<String, DEVICE_TYPE>();

    public DEVICE_TYPE get(String deviceName) {
      DEVICE_TYPE device = map.get(deviceName);
      if (device == null) {
        String msg = String.format("Unable to find a hardware device with the name \"%s\"", deviceName);
        throw new IllegalArgumentException(msg);
      }
      return device;
    }

    public void put(String deviceName, DEVICE_TYPE device) {
      map.put(deviceName, device);
    }

    public Iterator<DEVICE_TYPE> iterator() {
      return map.values().iterator();
    }

    public Set<Map.Entry<String, DEVICE_TYPE>> entrySet() {
      return map.entrySet();
    }

    public int size() {
      return map.size();
    }
  }

  public DeviceMapping<DcMotorController> dcMotorController = new DeviceMapping<DcMotorController>();
  public DeviceMapping<DcMotor> dcMotor = new DeviceMapping<DcMotor>();

  public DeviceMapping<ServoController> servoController = new DeviceMapping<ServoController>();
  public DeviceMapping<Servo> servo = new DeviceMapping<Servo>();

  public DeviceMapping<LegacyModule> legacyModule = new DeviceMapping<LegacyModule>();

  public DeviceMapping<AccelerationSensor> accelerationSensor = new DeviceMapping<AccelerationSensor>();
  public DeviceMapping<CompassSensor> compassSensor = new DeviceMapping<CompassSensor>();
  public DeviceMapping<GyroSensor> gyroSensor = new DeviceMapping<GyroSensor>();
  public DeviceMapping<IrSeekerSensor> irSeekerSensor = new DeviceMapping<IrSeekerSensor>();
  public DeviceMapping<LightSensor> lightSensor = new DeviceMapping<LightSensor>();
  public DeviceMapping<UltrasonicSensor> ultrasonicSensor = new DeviceMapping<UltrasonicSensor>();
  public DeviceMapping<VoltageSensor> voltageSensor = new DeviceMapping<VoltageSensor>();
}
