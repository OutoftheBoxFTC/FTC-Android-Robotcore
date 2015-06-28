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

import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
import com.qualcomm.robotcore.hardware.LegacyModule;

/**
 * Mock USB Legacy Module
 *
 * Use {@link MockDeviceManager} to create an instance of this class
 */
public class MockUsbLegacyModule extends MockUsbDevice implements LegacyModule {

  protected MockUsbLegacyModule(String serialNumber, DeviceType type) {
    super(serialNumber, type);
  }

  @Override
  public void enableNxtI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int memLength) {

  }

  @Override
  public void enableNxtI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, byte[] initialValues) {

  }

  @Override
  public void enableAnalogReadMode(int physicalPort, int i2cAddress) {

  }

  @Override
  public void enable9v(int physicalPort, boolean enable) {

  }

  @Override
  public void setDigitalLine(int physicalPort, int line, boolean set) {

  }

  @Override
  public byte[] readLegacyModuleCache(int physicalPort) {
    return new byte[0];
  }

  @Override
  public void writeLegacyModuleCache(int physicalPort, byte[] data) {

  }

  @Override
  public byte[] readAnalog(int physicalPort) {
    return new byte[0];
  }

  @Override
  public boolean isPortReady(int physicalPort) { return true; }


  @Override
  public String getDeviceName() {
    return new String("Mock Legacy Module");
  }
}
