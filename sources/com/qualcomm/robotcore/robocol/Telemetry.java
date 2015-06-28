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
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Hold telemtry data
 */
@SuppressWarnings("unused")
public class Telemetry implements RobocolParsable {

  public final static String DEFAULT_TAG = "TELEMETRY_DATA";

  private static final Charset CHARSET = Charset.forName("UTF-8");
  private static final int BASE_PAYLOAD_SIZE = 8;

  private final Map<String, String> dataStrings = new HashMap<String, String>();
  private final Map<String, Float> dataNumbers = new HashMap<String, Float>();

  private String tag = ""; // an empty tag is treated as the default tag
  private long timestamp = 0;

  public Telemetry() {
    // default constructor
  }

  public Telemetry(byte[] byteArray) throws RobotCoreException {
    fromByteArray(byteArray);
  }

  /**
   * Timestamp this message was sent. Timestamp is in wall time.
   * @return timestamp, or 0 if never sent
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Set the optional tag value.
   * <p>
   * Setting this to an empty string is equal to setting this to the default value.
   *
   * @see #DEFAULT_TAG
   * @param tag tag this telemetry data
   */
  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * Get the optional tag value
   * @return tag
   */
  public String getTag() {
    if (tag.length() == 0) return DEFAULT_TAG;

    return tag;
  }

  /**
   * Add a data point
   * <p>
   * All messages will be assumed to be in UTF-8.
   * @param key message key
   * @param msg message
   */
  public void addData(String key, String msg) {
    dataStrings.put(key, msg);
  }

  /**
   * Add a data point
   * <p>
   * Msg will be down cast to a float.
   * @param key message key
   * @param msg message
   */
  public void addData(String key, float msg) {
    dataNumbers.put(key, msg);
  }

  /**
   * Add a data point
   * <p>
   * msg will automatically be downcast to a float.
   * @param key message key
   * @param msg message
   */
  public void addData(String key, double msg) {
    dataNumbers.put(key, (float)(msg));
  }

  /**
   * Get a reference to the map of messages
   * @return reference to the messages
   */
  public Map<String, String> getDataStrings() {
    return dataStrings;
  }

  public Map<String, Float> getDataNumbers() {
    return dataNumbers;
  }

  /**
   * Return true if this telemetry object has data added to it
   * @return true if this object has data, otherwise false
   */
  public boolean hasData() {
    return (dataStrings.isEmpty() == false || dataNumbers.isEmpty() == false);
  }

  /**
   * Clear all messages
   * <p>
   * Clear all messages, reset the timestamp to 0
   */
  public void clearData() {
    timestamp = 0;
    dataStrings.clear();
    dataNumbers.clear();
  }

  @Override
  public MsgType getRobocolMsgType() {
    return MsgType.TELEMETRY;
  }

  @Override
  public byte[] toByteArray() throws RobotCoreException {

    /*
     * See countMessageBytes(...) for information about data format
     */

    timestamp = System.currentTimeMillis();

    if (dataStrings.size() > 256) {
      throw new RobotCoreException("Cannot have more than 256 string data points");
    }

    if (dataNumbers.size() > 256) {
      throw new RobotCoreException("Cannot have more than 256 number data points");
    }

    int payloadSize = countMessageBytes() + BASE_PAYLOAD_SIZE;
    int totalSize = RobocolParsable.HEADER_LENGTH + payloadSize;

    if (totalSize > RobocolConfig.MAX_PACKET_SIZE) {
      throw new RobotCoreException(String.format("Cannot send telemetry data of %d bytes; max is %d",
          totalSize, RobocolConfig.MAX_PACKET_SIZE));
    }

    ByteBuffer buffer = ByteBuffer.allocate(totalSize);

    // populate the header
    buffer.put(getRobocolMsgType().asByte());
    buffer.putShort((short)payloadSize);

    // timestamp
    buffer.putLong(timestamp);

    // tag
    if (tag.length() == 0) {
      buffer.put((byte) 0);
    } else {
      byte tagBytes[] = tag.getBytes(CHARSET);

      if (tagBytes.length > 256) {
        throw new RobotCoreException(String.format("Telemetry tag cannot exceed 256 bytes [%s]", tag));
      }

      buffer.put((byte) tagBytes.length);
      buffer.put(tagBytes);
    }

    // data strings
    buffer.put((byte) dataStrings.size());
    for (Entry<String, String> entry : dataStrings.entrySet()) {
      byte[] key = entry.getKey().getBytes(CHARSET);
      byte[] value = entry.getValue().getBytes(CHARSET);

      if (key.length > 256 || value.length > 256) {
        throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%s]",
            entry.getKey(), entry.getValue()));
      }

      buffer.put((byte) key.length);
      buffer.put(key);
      buffer.put((byte) value.length);
      buffer.put(value);
    }

    // data numbers
    buffer.put((byte) dataNumbers.size());
    for (Entry<String, Float> entry : dataNumbers.entrySet()) {
      byte[] key = entry.getKey().getBytes(CHARSET);
      float val = entry.getValue();

      if (key.length > 256) {
        throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%f]",
            entry.getKey(), val));
      }

      buffer.put((byte) key.length);
      buffer.put(key);
      buffer.putFloat(val);
    }

    // done
    return buffer.array();
  }

  @Override
  public void fromByteArray(byte[] byteArray) throws RobotCoreException {

    clearData();

    ByteBuffer buffer = ByteBuffer.wrap(byteArray, RobocolParsable.HEADER_LENGTH, byteArray.length - RobocolParsable.HEADER_LENGTH);

    // timestamp
    timestamp = buffer.getLong();

    // tag
    int tagLength = TypeConversion.unsignedByteToInt(buffer.get());
    if (tagLength == 0) {
      tag = "";
    } else {
      byte[] tagBytes = new byte[tagLength];
      buffer.get(tagBytes);
      tag = new String(tagBytes , CHARSET);
    }

    // data strings
    int stringDataPoints = buffer.get();
    for (int i = 0; i < stringDataPoints; i++) {
      int keyLength = TypeConversion.unsignedByteToInt(buffer.get());
      byte[] keyBytes = new byte[keyLength];
      buffer.get(keyBytes);

      int valLength = TypeConversion.unsignedByteToInt(buffer.get());
      byte[] valBytes = new byte[valLength];
      buffer.get(valBytes);

      String key = new String(keyBytes, CHARSET);
      String val = new String(valBytes, CHARSET);

      dataStrings.put(key, val);
    }

    // data numbers
    int numberDataPoints = buffer.get();
    for (int i = 0; i < numberDataPoints; i++) {
      int keyLength = TypeConversion.unsignedByteToInt(buffer.get());
      byte[] keyBytes = new byte[keyLength];
      buffer.get(keyBytes);
      String key = new String(keyBytes, CHARSET);
      float val = buffer.getFloat();

      dataNumbers.put(key, val);
    }
  }

  private int countMessageBytes() {

    /*
     * Data format
     *
     * bytes    | format | value
     * ---------|--------|---------------------------------
     *  8       | int64  | timestamp
     *  1       | uint8  | length of tag (or 0 for default tag)
     *  varies  | UTF-8  | value of tag
     *  1       | uint8  | count of string data points
     *  varies  | varies | string data points
     *  1       | uint8  | count of number data points
     *  varies  | varies | number data points
     *
     *
     * String Data Points (repeating)
     *
     * bytes    | format | value
     * ---------|--------|---------------------------------
     *  1       | uint8  | length of key
     *  varies  | UTF-8  | key
     *  1       | uint8  | length of value
     *  varies  | UTF-8  | value
     *
     * Number Data Points (repeating)
     *
     * bytes    | format | value
     * ---------|--------|---------------------------------
     *  1       | uint8  | length of key
     *  varies  | UTF-8  | key
     *  4       | float  | value
     */

    int count = 0;

    // count the length of the tag
    count += 1 + tag.getBytes(CHARSET).length;

    // count the string data
    count += 1;
    for(Entry<String, String> entry : dataStrings.entrySet()) {
      count += 1 + entry.getKey().getBytes(CHARSET).length;
      count += 1 + entry.getValue().getBytes(CHARSET).length;
    }

    // count the number data
    count += 1;
    for (Entry<String, Float> entry: dataNumbers.entrySet()) {
      count += 1 + entry.getKey().getBytes(CHARSET).length;
      count += 4;
    }

    return count;
  }

}
