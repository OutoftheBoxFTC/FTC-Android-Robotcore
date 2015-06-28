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
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Base class for Mock USB Devices
 */
public abstract class MockUsbDevice {

  public static final boolean DEBUG_LOGGING = false;

  public String serialNumber;
  public DeviceType type;



  /**
   * Constructor - this class in extended by the actual device type
   * <p>
   * Some hardware need custom read/write loops, this constructor allows classes to provide their
   * own.
   * <p>
   * Blocks until one successful read from device
   *
   * @param serialNumber USB serial number
   * @param device reference to the FT_Device object
   * @param maxAddress max memory address used by this device
   * @param callback callback, may be null
   */
  protected MockUsbDevice(
      String serialNumber, DeviceType type) {

    this.serialNumber = serialNumber;
    this.type = type;

    RobotLog.v("Starting up device " + serialNumber.toString());
  }

  /**
   * Device Name
   *
   * @return device manufacturer
   */
  public abstract String getDeviceName();

  /**
   * Serial Number
   *
   * @return return the USB serial number of this device
   */
  public SerialNumber getSerialNumber() {
    return new SerialNumber(serialNumber);
  }

  /**
   * Version
   *
   * @return get the version of this device
   */
  public int getVersion() {

    return 0;
  }

  /**
   * Close this device
   */
  public void close() {
    RobotLog.v("Shutting down device " + serialNumber.toString());
  }

  /**
   * Write a single byte to the device
   *
   * @param address address to write
   * @param data data to write (will be cast to a byte)
   */
  public void write(byte address, int data) {

  }

  /**
   * Write a single byte to the device
   *
   * @param address address to write
   * @param data data to write (will be cast to a byte)
   */
  public void write(byte address, double data) {

  }

  /**
   * Write to device
   *
   * @param address address to write
   * @param data data to write
   * @throws IllegalArgumentException if address is out of range
   */
  public void write(byte address, byte[] data) {

  }

  /**
   * Read a single byte from device
   *
   * @param address address to read
   * @throws IllegalArgumentException if address is out of range, or if read failed
   */
  public byte read(byte address) {
    return (byte) 2;
  }

  /**
   * Read from device
   *
   * @param address address to read
   * @param size number of bytes to read
   * @throws IllegalArgumentException if address is out of range, or if read failed
   */
  public byte[] read(byte address, int size) {
    byte[] ret = new byte[3];
    return ret;
  }

  protected void dumpBuffers() {

  }

  public static boolean checkReadHeaderForSuccess(byte[] header, int expectedSize) {
	  return true;
  }
}
