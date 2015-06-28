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

package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class PeerDiscovery implements RobocolParsable {

  public static final short PAYLOAD_SIZE = 10;
  public static final short BUFFER_SIZE = PAYLOAD_SIZE + RobocolParsable.HEADER_LENGTH;

  public static final byte ROBOCOL_VERSION = 1;

  /**
   * Peer type
   */
  public enum PeerType {
    /*
     * NOTE: when adding new message types, do not change existing message
     * type values or you will break backwards capability.
     */
    NOT_SET(0),
    PEER(1),
    GROUP_OWNER(2);

    private static final PeerType[] VALUES_CACHE = PeerType.values();
    private int type;

    /**
     * Create a PeerType from a byte
     * @param b
     * @return PeerType
     */
    public static PeerType fromByte(byte b) {
      PeerType p = NOT_SET;
      try {
        p = VALUES_CACHE[b];
      } catch (ArrayIndexOutOfBoundsException e) {
        RobotLog.w(String.format("Cannot convert %d to Peer: %s", b, e.toString()));
      }
      return p;
    }

    private PeerType(int type) {
      this.type = type;
    }

    /**
     * Return this peer type as a byte
     * @return peer type as byte
     */
    public byte asByte() {
      return (byte)(type);
    }
  }

  private PeerType peerType;

  public PeerDiscovery(PeerDiscovery.PeerType peerType) {
    this.peerType = peerType;
  }

  /**
   * @return the peer type
   */
  public PeerType getPeerType() {
    return peerType;
  }

  @Override
  public MsgType getRobocolMsgType() {
    return RobocolParsable.MsgType.PEER_DISCOVERY;
  }

  @Override
  public byte[] toByteArray() throws RobotCoreException {
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    try {
      buffer.put(getRobocolMsgType().asByte());
      buffer.putShort(PAYLOAD_SIZE);

      buffer.put(ROBOCOL_VERSION);
      buffer.put(peerType.asByte());
    } catch (BufferOverflowException e) {
      RobotLog.logStacktrace(e);
    }
    return buffer.array();
  }

  @Override
  public void fromByteArray(byte[] byteArray) throws RobotCoreException {
    if (byteArray.length < BUFFER_SIZE) {
      throw new RobotCoreException("Expected buffer of at least " + BUFFER_SIZE + " bytes, received " + byteArray.length);
    }

    ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);
    byte version = byteBuffer.get();
    switch (version) {
      case 1:
        peerType = PeerType.fromByte(byteBuffer.get());
        break;
      default:
        break;
    }
  }

  @Override
  public String toString() {
    return String.format("Peer Discovery - peer type: %s", peerType.name());
  }
}
