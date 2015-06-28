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

/**
 * Legacy Module for working with NXT devices
 */
public interface LegacyModule {

  /**
   * Enable a physical port in NXT I2C read mode
   * @param physicalPort physical port number on the device
   * @param i2cAddress I2C address
   * @param memAddress memory address on the device
   * @param memLength length of memory to read
   */
  public void enableNxtI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int memLength);

  /**
   * Enable a physical port in NXT I2C write mode
   * @param physicalPort physical port number on the device
   * @param i2cAddress I2C address
   * @param memAddress memory address on the device
   * @param initialValues initial values to write
   */
  public void enableNxtI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, byte[] initialValues);

  /**
   * Enable a physical port in analog read mode
   * @param physicalPort physical port number on the device
   */
  public void enableAnalogReadMode(int physicalPort, int i2cAddress);

  /**
   * Enable or disable 9V power on a port
   * @param physicalPort physical port number on the device
   * @param enable true to enable; false to disable
   */
  public void enable9v(int physicalPort, boolean enable);

  /**
   * Set the value of digital line 0 or 1 while in analog mode.
   * <p>
   * These are port pins 5 and 6.
   * @param physicalPort physical port number on the device
   * @param line line 0 or 1
   * @param set true to set; otherwise false
   */
  public void setDigitalLine(int physicalPort, int line, boolean set);

  /**
   * Read the device memory map; only works in NXT I2C read mode
   * @param physicalPort physical port number on the device
   * @return byte array
   */
  public byte[] readLegacyModuleCache(int physicalPort);

  /**
   * Write to the device memory map; only works in NXT I2C write mode
   * @param physicalPort physical port number on the device
   * @param data byte array to write
   */
  public void writeLegacyModuleCache(int physicalPort, byte[] data);

  /**
   * Read an analog value from a device; only works in analog read mode
   * @param physicalPort physical port number on the device
   * @return byte[] containing the two analog values; low byte first, high byte second
   */
  public byte[] readAnalog(int physicalPort);

  /**
   * Determine if a physical port is ready
   * @param physicalPort physical port number on the device
   * @return true if ready for command; false otherwise
   */
  public boolean isPortReady(int physicalPort);

  /**
   * Close the device
   */
  public void close();
}
