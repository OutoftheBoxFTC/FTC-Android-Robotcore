package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * RobocolDatagram
 *
 * Used by RobocolServer and RobocolClient to pass messages.
 */
public class RobocolDatagram {

   private DatagramPacket packet;

   /**
    * Construct a RobocolDatagram from a RobocolParsable
    * @param message
    */
   public RobocolDatagram(RobocolParsable message) throws RobotCoreException {
      setData(message.toByteArray());
   }

   /**
    * Construct a RobocolDatagram from a byte array
    * @param message
    */
   public RobocolDatagram(byte[] message) {
      setData(message);
   }

   protected RobocolDatagram(DatagramPacket packet) {
      this.packet = packet;
   }

   protected RobocolDatagram() {
      this.packet = null;
   }

   /**
    * Get the message type
    * @return message type
    */
   public RobocolParsable.MsgType getMsgType() {
      return RobocolParsable.MsgType.fromByte(packet.getData()[0]);
   }

   /**
    * Get the size of this RobocolDatagram, in bytes
    * @return size of this RobocolDatagram, in bytes
    */
   public int getLength() {
      return packet.getLength();
   }

   /**
    * Get the size of the payload, in bytes
    * @return size of payload, in bytes
    */
   public int getPayloadLength() {
      return packet.getLength() - RobocolParsable.HEADER_LENGTH;
   }

   /**
    * Gets the payload of this datagram packet
    * @return byte[] data
    */
   public byte[] getData() {
      return packet.getData();
   }

   public void setData(byte[] data) {
      packet = new DatagramPacket(data, data.length);
   }

   public InetAddress getAddress() {
      return packet.getAddress();
   }

   public void setAddress(InetAddress address) {
      packet.setAddress(address);
   }

   public String toString() {
      int size = 0;
      String type = "NONE";
      String addr = null;

      if (packet != null && packet.getAddress() != null && packet.getLength() > 0) {
         type = RobocolParsable.MsgType.fromByte(packet.getData()[0]).name();
         size = packet.getLength();
         addr = packet.getAddress().getHostAddress();
      }

      return String.format("RobocolDatagram - type:%s, addr:%s, size:%d", type, addr, size);
   }

   protected DatagramPacket getPacket() {
      return packet;
   }

   protected void setPacket(DatagramPacket packet) {
      this.packet = packet;
   }
}
