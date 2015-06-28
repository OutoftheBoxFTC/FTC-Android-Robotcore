package com.qualcomm.robotcore.robocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class RobocolDatagramSocket {

	public enum State {
		LISTENING,
		CLOSED,
		ERROR
	}

	public RobocolDatagramSocket() {

	}

	public void listen(InetAddress destAddress) throws SocketException {

	}

	public void bind(InetSocketAddress bindAddress) throws SocketException {

	}

	public void connect(InetAddress connectAddress) throws SocketException {

	}

	public void close() {

	}

	public void send(RobocolDatagram message) {

	}

	public RobocolDatagram recv() {
		return null;
	}

	public State getState() {
		return null;
	}

	public InetAddress getInetAddress() {
		return null;
	}

	public InetAddress getLocalAddress() {
		return null;
	}

	public boolean isRunning() {
		return false;
	}

	public boolean isClosed() {
		return false;
	}

}
