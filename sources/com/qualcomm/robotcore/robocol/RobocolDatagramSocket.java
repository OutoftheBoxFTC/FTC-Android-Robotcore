package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.util.RobotLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;

/**
 * Heavyweight multi-threaded datagram socket with non-blocking IO.
 * <p>
 * Base class of RobocolClient and RobocolServer.
 */
public class RobocolDatagramSocket {

  private static final boolean DEBUG = false;

  public enum State {
    LISTENING,  /// Socket is ready
    CLOSED,     /// Socket is not ready
    ERROR       /// Socket is in error state
  }

  private final byte[] buffer = new byte[RobocolConfig.MAX_PACKET_SIZE];

  private DatagramSocket socket;
  private final DatagramPacket packetRecv = new DatagramPacket(buffer, buffer.length);
  private final RobocolDatagram msgRecv = new RobocolDatagram();

  volatile private State state;

  public RobocolDatagramSocket() {
    state = State.CLOSED;
  }

  public void listen(InetAddress destAddress) throws SocketException {
    bind(new InetSocketAddress(RobocolConfig.determineBindAddress(destAddress), RobocolConfig.PORT_NUMBER));
  }

  public void bind(InetSocketAddress bindAddress) throws SocketException {

    if (state != State.CLOSED) {
      close();
    }
    state = State.LISTENING;

    // start up the socket
    RobotLog.d("RobocolDatagramSocket binding to " + bindAddress.toString());
    socket = new DatagramSocket(bindAddress);
  }

  public void connect(InetAddress connectAddress) throws SocketException {
    InetSocketAddress addr = new InetSocketAddress(connectAddress, RobocolConfig.PORT_NUMBER);
    RobotLog.d("RobocolDatagramSocket connected to " + addr.toString());
    socket.connect(addr);
  }

  public void close() {
    state = State.CLOSED;

    if (socket != null) socket.close();

    RobotLog.d("RobocolDatagramSocket is closed");
  }

  public void send(RobocolDatagram message) {

    try {
      socket.send(message.getPacket());
      if (DEBUG) RobotLog.v("sent network packet to " + message.getPacket().getAddress().toString());
    } catch (IllegalArgumentException e) {
      RobotLog.w("Unable to send RobocolDatagram: " + e.toString());
      RobotLog.w("               " + message.toString());
    } catch (IOException e) {
      // socket was unable to send
      RobotLog.w("Unable to send RobocolDatagram: " + e.toString());
      RobotLog.w("               " + message.toString());
    } catch (NullPointerException e) {
      RobotLog.w("Unable to send RobocolDatagram: " + e.toString());
      RobotLog.w("               " + message.toString());
    }
  }

  /**
   * Receive a RobocolDatagram packet
   * @return packet; or null if error
   */
  public RobocolDatagram recv() {

    try {
      socket.receive(packetRecv);
      if (DEBUG) RobotLog.v("received network packet from " + packetRecv.getAddress().toString());
    } catch (PortUnreachableException e) {
      RobotLog.d("RobocolDatagramSocket receive error: remote port unreachable");
      return null;
    } catch (IOException e) {
      RobotLog.d("RobocolDatagramSocket receive error: " + e.toString());
      return null;
    } catch (NullPointerException e) {
      RobotLog.d("RobocolDatagramSocket receive error: " + e.toString());
    }

    msgRecv.setPacket(packetRecv);

    return msgRecv;
  }

  public State getState() {
    return state;
  }

  public InetAddress getInetAddress() {
    if (socket == null) return null;

    return socket.getInetAddress();
  }

  public InetAddress getLocalAddress() {
    if (socket == null) return null;

    return socket.getLocalAddress();
  }

  public boolean isRunning() {
    return (state == State.LISTENING);
  }

  public boolean isClosed() {
    return (state == State.CLOSED);
  }
}
