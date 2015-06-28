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

package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;

/**
 * Interface implemented by objects that want to be sendable via a RobocolDatagram.
 *
 * @see RobocolDatagram
 */
public interface RobocolParsable {

  /*
   * When implementing this class, you must make sure you populate the header
   * at the beginning of your buffer.
   *
   * Robocol Packet Format
   *
   *   Byte | Field
   * -------|-------------------------
   *    00  | Message type
   * 01-02  | Payload length, in bytes
   *    03+ | Payload
   */

  /**
   * Message Type
   */
  public enum MsgType {
    /*
     * NOTE: when adding new message types, do not change existing message
     * type values or you will break backwards capability.
     */
    EMPTY(0),
    HEARTBEAT(1),
    GAMEPAD(2),
    PEER_DISCOVERY(3),
    COMMAND(4),
    TELEMETRY(5);

    private static final MsgType[] VALUES_CACHE = MsgType.values();
    private final int type;

    /**
     * Create a MsgType from a byte
     * @param b
     * @return MsgType
     */
    public static MsgType fromByte(byte b) {
      MsgType t = EMPTY;
      try {
        t = VALUES_CACHE[b];
      } catch (ArrayIndexOutOfBoundsException e) {
        RobotLog.w(String.format("Cannot convert %d to MsgType: %s", b, e.toString()));
      }
      return t;
    }

    private MsgType(int type) {
      this.type = type;
    }

    /**
     * Return this message type as a byte
     * @return message type as byte
     */
    public byte asByte() {
      return (byte)(type);
    }
  }

  public static final int HEADER_LENGTH = 0x03;
  public static final byte[] EMPTY_HEADER_BUFFER = new byte[RobocolParsable.HEADER_LENGTH];

  /**
   * Get the Robocol MsgType of this RobocolParsable
   * @return message type
   */
  public abstract MsgType getRobocolMsgType();

  /**
   * Convert this object into a byte array. This method will be called when the
   * object is packaged into a RobocolDatagram.
   *
   * @throws RobotCoreException if error
   *
   * @return byte array representing the current object, or null if error
   */
  public abstract byte[] toByteArray() throws RobotCoreException;

  /**
   * Populate the fields of this object based on values of this byte array.
   * @param byteArray byte array from which to populate this object
   *
   * @throws RobotCoreException if unable to parse the byte array.
   */
  public abstract void fromByteArray(byte[] byteArray) throws RobotCoreException;
}
