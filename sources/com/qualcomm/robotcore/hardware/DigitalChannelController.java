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

import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Interface for working with Digital Channel Controllers
 * <p>
 * Different digital channel controllers will implement this interface.
 */
public interface DigitalChannelController {

  /**
   * Digital channel mode - input or output
   */
  public enum Mode { INPUT, OUTPUT; }

  /**
   * Device Name
   *
   * @return device manufacturer and name
   */
  public abstract String getDeviceName();

  /**
   * Serial Number
   *
   * @return return the USB serial number of this device
   */
  public abstract SerialNumber getSerialNumber();

  /**
   * Version
   *
   * @return get the version of this device
   */
  public abstract int getVersion();

  /**
   * Close this device
   */
  public abstract void close();

  /**
   * Get the mode of a digital channel
   *
   * @param channel
   * @return INPUT or OUTPUT
   */
  public Mode getDigitalChannelMode(int channel);

  /**
   * Set the mode of a digital channel
   *
   * @param channel
   * @param mode INPUT or OUTPUT
   */
  public void setDigitalChannelMode(int channel, Mode mode);

  /**
   * Get the state of a digital channel
   *
   * @param channel
   * @return true if set; otherwise false
   */
  public boolean getDigitalChannelState(int channel);

  /**
   * Set the state of a digital channel
   * <p>
   * The behavior of this method is undefined for INPUT digital channels.
   *
   * @param channel
   * @param state true to set; false to unset
   */
  public void setDigitalChannelState(int channel, boolean state);
}
