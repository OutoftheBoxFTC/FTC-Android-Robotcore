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
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Comparator;

/**
 * Class used to send and receive commands
 * <p>
 * These commands should be acknowledged by the receiver. The sender may resend the command
 * repeatedly until it receives and acknowledgment from the receiver. The receiver should not
 * reprocess repeated commands.
 */
public class Command implements RobocolParsable, Comparable<Command>, Comparator<Command> {

  public static final int MAX_COMMAND_LENGTH = 256;

  private static final short BASE_PAYLOAD_SIZE = 11;

  private static final Charset CHARSET = Charset.forName("UTF-8");

  String mName;
  String mExtra;
  byte[] mNameBytes;
  byte[] mExtraBytes;
  long mTimestamp;
  boolean mAcknowledged = false;
  byte mAttempts = 0;

  /**
   * Constructor
   * @param name name as string
   */
  public Command(String name) {
    this(name, "");
  }

  /**
   * Constructor
   * @param name name as string
   * @param extra extra data as string
   */
  public Command(String name, String extra) {
    mName = name;
    mExtra = extra;
    mNameBytes = TypeConversion.stringToUtf8(mName);
    mExtraBytes = TypeConversion.stringToUtf8(mExtra);
    mTimestamp = generateTimestamp();

    if (mNameBytes.length > MAX_COMMAND_LENGTH) {
      throw new IllegalArgumentException(String.format("command name length is too long (MAX: %d)", MAX_COMMAND_LENGTH));
    }

    if (mExtraBytes.length > MAX_COMMAND_LENGTH) {
      throw new IllegalArgumentException(String.format("command extra data length is too long (MAX: %d)", MAX_COMMAND_LENGTH));
    }
  }

  public Command(byte[] byteArray) throws RobotCoreException {
    fromByteArray(byteArray);
  }

  /**
   * The receiver should call this method before sending this command back to the sender
   */
  public void acknowledge() {
    mAcknowledged = true;
  }

  /**
   * Check if this command has been acknowledged
   * @return true if acknowledged, otherwise false
   */
  public boolean isAcknowledged() {
    return mAcknowledged;
  }

  /**
   * Get the command name as a string
   * @return command name
   */
  public String getName() {
    return mName;
  }

  /**
   * Get the extra data as a string
   * @return extra string
   */
  public String getExtra() { return mExtra; }

  /**
   * Number of times this command was packaged into a byte array
   * <p>
   * After Byte.MAX_VALUE is reached, this will stop counting and remain at Byte.MAX_VALUE.
   *
   * @return number of times this command was packaged into a byte array
   */
  public byte getAttempts() {
    return mAttempts;
  }

  /**
   * Get the timestamp that this command was created
   * @return timestamp
   */
  public long getTimestamp() {
    return mTimestamp;
  }

  /*
   * (non-Javadoc)
   * @see com.qualcomm.robotcore.robocol.RobocolParsable#getRobocolMsgType()
   */
  @Override
  public MsgType getRobocolMsgType() {
    return RobocolParsable.MsgType.COMMAND;
  }

  /*
   * (non-Javadoc)
   * @see com.qualcomm.robotcore.robocol.RobocolParsable#toByteArray()
   */
  @Override
  public byte[] toByteArray() throws RobotCoreException {

    if (mAttempts != Byte.MAX_VALUE) mAttempts += 1;

    short payloadSize = (short) (BASE_PAYLOAD_SIZE + mNameBytes.length + mExtraBytes.length);

    ByteBuffer buffer = ByteBuffer.allocate(RobocolParsable.HEADER_LENGTH + payloadSize);
    try {
      buffer.put(getRobocolMsgType().asByte());
      buffer.putShort(payloadSize);

      buffer.putLong(mTimestamp);

      buffer.put((byte) (mAcknowledged ? 1 : 0));

      buffer.put((byte) mNameBytes.length);
      buffer.put(mNameBytes);
      buffer.put((byte) mExtraBytes.length);
      buffer.put(mExtraBytes);
    } catch (BufferOverflowException e) {
      RobotLog.logStacktrace(e);
    }
    return buffer.array();
  }

  /*
   * (non-Javadoc)
   * @see com.qualcomm.robotcore.robocol.RobocolParsable#fromByteArray(byte[])
   */
  @Override
  public void fromByteArray(byte[] byteArray) throws RobotCoreException {
    ByteBuffer buffer = ByteBuffer.wrap(byteArray, RobocolParsable.HEADER_LENGTH, byteArray.length - RobocolParsable.HEADER_LENGTH);

    mTimestamp = buffer.getLong();

    mAcknowledged = (buffer.get() == 1);

    int length = TypeConversion.unsignedByteToInt(buffer.get());
    mNameBytes = new byte[length];
    buffer.get(mNameBytes);
    mName = TypeConversion.utf8ToString(mNameBytes);

    length = TypeConversion.unsignedByteToInt(buffer.get());
    mExtraBytes = new byte[length];
    buffer.get(mExtraBytes);
    mExtra = TypeConversion.utf8ToString(mExtraBytes);
  }

  @Override
  public String toString() {
    return String.format("command: %20d %5s %s", mTimestamp, mAcknowledged, mName);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Command) {
      Command c = (Command) o;
      if (this.mName.equals(c.mName) && this.mTimestamp == c.mTimestamp) return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return (int) (mName.hashCode() & mTimestamp);
  }

  @Override
  public int compareTo(Command another) {
    int diff = mName.compareTo(another.mName);

    if (diff != 0) return diff;

    if (mTimestamp < another.mTimestamp) return -1;
    if (mTimestamp > another.mTimestamp) return  1;

    return 0;
  }

  @Override
  public int compare(Command c1, Command c2) {
    return c1.compareTo(c2);
  }

  public static long generateTimestamp() {
    return System.nanoTime();
  }
}