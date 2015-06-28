package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class RobocolDatagram {

	public RobocolDatagram(RobocolParsable message) throws RobotCoreException {

	}

	public RobocolDatagram(byte[] message) {

	}

	protected RobocolDatagram(DatagramPacket packet) {

	}

	protected RobocolDatagram() {

	}

	public RobocolParsable.MsgType getMsgType() {
		return null;
	}

	public int getLength() {
		return 0;
	}

	public int getPayloadLength() {
		return 0;
	}

	public byte[] getData() {
		return null;
	}

	public void setData(byte[] data) {

	}

	public InetAddress getAddress() {
		return null;
	}

	public void setAddress(InetAddress address) {

	}

	public String toString() {
		return null;
	}

	protected DatagramPacket getPacket() {
		return null;
	}

	protected void setPacket(DatagramPacket packet) {

	}

}
