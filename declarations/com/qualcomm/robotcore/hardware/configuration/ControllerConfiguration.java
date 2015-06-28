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

package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.util.SerialNumber;

import java.io.Serializable;
import java.util.List;

public class ControllerConfiguration extends DeviceConfiguration implements Serializable {


  public static final SerialNumber NO_SERIAL_NUMBER = new SerialNumber("-1");

  private List<DeviceConfiguration> devices;
  private SerialNumber serialNumber;
  private ConfigurationType type = ConfigurationType.NOTHING;

  public ControllerConfiguration(String name, List<DeviceConfiguration> devices, SerialNumber serialNumber, ConfigurationType type) {
    super(type);
    super.setName(name);
    this.devices = devices;
    this.serialNumber = serialNumber;
  }

  public List<DeviceConfiguration> getDevices() {
    return devices;
  }

  public ConfigurationType getType(){
    return super.getType();
  }

  public SerialNumber getSerialNumber(){
    return this.serialNumber;
  }

  public void addDevices(List<DeviceConfiguration> devices){
    this.devices = devices;
  }

  public ConfigurationType deviceTypeToConfigType(DeviceManager.DeviceType type){
    if (type == DeviceManager.DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER){
      return ConfigurationType.MOTOR_CONTROLLER;
    }
    if (type == DeviceManager.DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER){
      return ConfigurationType.SERVO_CONTROLLER;
    }
    if (type == DeviceManager.DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE){
      return ConfigurationType.LEGACY_MODULE_CONTROLLER;
    }
    else {
      return ConfigurationType.NOTHING;
    }
  }

  public DeviceManager.DeviceType configTypeToDeviceType(ConfigurationType type){
    if (type == ConfigurationType.MOTOR_CONTROLLER){
      return DeviceManager.DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER;
    }
    if (type == ConfigurationType.SERVO_CONTROLLER){
      return DeviceManager.DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER;
    }
    if (type == ConfigurationType.LEGACY_MODULE_CONTROLLER){
      return DeviceManager.DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE;
    }
    else {
      return DeviceManager.DeviceType.FTDI_USB_UNKNOWN_DEVICE;
    }
  }

}

