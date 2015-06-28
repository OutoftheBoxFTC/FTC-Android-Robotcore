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

import com.qualcomm.robotcore.util.Network;
import com.qualcomm.robotcore.util.RobotLog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketOptions;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Configuration Data for Robocol
 */
public class RobocolConfig {

   public static final int MAX_PACKET_SIZE = SocketOptions.SO_RCVBUF;

   public static final int PORT_NUMBER = 20884;

   public static final int TTL = 3;

   public static final int TIMEOUT = 1000;

   /**
    * Find a bind address that can reach the destAddress. If no bind address
    * can be found, return the loopback address
    * @param destAddress destination address
    * @return address to bind to
    */
   public static InetAddress determineBindAddress(InetAddress destAddress) {
      ArrayList<InetAddress> addresses = Network.getLocalIpAddresses();
      addresses = Network.removeLoopbackAddresses(addresses);
      addresses = Network.removeIPv6Addresses(addresses);
      NetworkInterface iface = null;

      // if an iface has the destAddress, pick that one
      for (InetAddress address : addresses) {
        try {
          iface = NetworkInterface.getByInetAddress(address);
          Enumeration<InetAddress> ifaceAddresses = iface.getInetAddresses();
          while (ifaceAddresses.hasMoreElements()) {
            InetAddress ifaceAddress = ifaceAddresses.nextElement();
            if (ifaceAddress.equals(destAddress)) {
              return ifaceAddress; // we found a match
            }
          }
        } catch (SocketException e) {
          RobotLog.v(String.format("socket exception while trying to get network interface of %s",
              address.getHostAddress()));
        }
      }

      // pick the first address where the destAddress is reachable
      for (InetAddress address : addresses) {
        try {
          iface = NetworkInterface.getByInetAddress(address);
          if (address.isReachable(iface, TTL, TIMEOUT)) {
            return address; // we found a match
          }
        } catch (SocketException e) {
          RobotLog.v(String.format("socket exception while trying to get network interface of %s",
              address.getHostAddress()));
        } catch (IOException e) {
          RobotLog.v(String.format("IO exception while trying to determine if %s is reachable via %s",
              destAddress.getHostAddress(), address.getHostAddress()));
        }
      }

      // We couldn't find a match
      return Network.getLoopbackAddress();
   }
}
