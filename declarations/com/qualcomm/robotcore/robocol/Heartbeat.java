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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * Heartbeat message
 * <p>
 * Used to know if the connection between the client/server is still alive
 */
@SuppressWarnings("unused")
public class Heartbeat implements RobocolParsable {

  public enum Token { EMPTY }

  public static final short PAYLOAD_SIZE = 10;
  public static final short BUFFER_SIZE = PAYLOAD_SIZE + RobocolParsable.HEADER_LENGTH;
  public static final short MAX_SEQUENCE_NUMBER = 10000;

  private static final double SECOND_IN_NANO = 1000000000;

  // this variable needs to be protected by a lock
  private static short sequenceNumberGen = 0;

  private long timestamp;
  private short sequenceNumber;

  /**
   * Constructor
   */
  public Heartbeat() {
    sequenceNumber = genNextSequenceNumber();
    timestamp = System.nanoTime();
  }

  public Heartbeat(Heartbeat.Token token) {
    switch (token) {
      case EMPTY:
        // do not generate new values
        sequenceNumber = 0;
        timestamp = 0;
        break;
    }
  }

  /**
   * Timestamp this Heartbeat was created at
   * <p>
   * Device dependent, cannot compare across devices
   * @return timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Number of seconds since Heartbeat was created
   * <p>
   * Device dependent, cannot compare across devices
   * @return elapsed time
   */
  public double getElapsedTime() {
    return (System.nanoTime() - timestamp) / SECOND_IN_NANO;
  }

  /**
   * Sequence number of this Heartbeat. Sequence numbers wrap at MAX_SEQUENCE_NUMBER.
   * @return sequence number
   */
  public short getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * Get Robocol message type
   * @return RobocolParsable.MsgType.HEARTBEAT
   */
  @Override
  public MsgType getRobocolMsgType() {
    return RobocolParsable.MsgType.HEARTBEAT;
  }

  /**
   * Convert this Heartbeat into a byte array
   */
  @Override
  public byte[] toByteArray() throws RobotCoreException {
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    try {
      buffer.put(getRobocolMsgType().asByte());
      buffer.putShort(PAYLOAD_SIZE);

      buffer.putShort(sequenceNumber);
      buffer.putLong(timestamp);
    } catch (BufferOverflowException e) {
      RobotLog.logStacktrace(e);
    }
    return buffer.array();
  }

  /**
   * Populate this Heartbeat from a byte array
   */
  @Override
  public void fromByteArray(byte[] byteArray) throws RobotCoreException {
    if (byteArray.length < BUFFER_SIZE) {
      throw new RobotCoreException("Expected buffer of at least " + BUFFER_SIZE + " bytes, received " + byteArray.length);
    }

    ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);
    sequenceNumber = byteBuffer.getShort();
    timestamp = byteBuffer.getLong();
  }

  /**
   * String containing sequence number and timestamp
   * @return String
   */
  @Override
  public String toString() {
    return String.format("Heartbeat - seq: %4d, time: %d", sequenceNumber, timestamp);
  }

  private synchronized static short genNextSequenceNumber() {
    short next = sequenceNumberGen;
    sequenceNumberGen += 1;
    if (sequenceNumberGen > MAX_SEQUENCE_NUMBER) sequenceNumberGen = 0;
    return next;
  }
}
