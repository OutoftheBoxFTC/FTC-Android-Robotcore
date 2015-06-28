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

import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages the sending of peer discovery packets to the peer discovery device
 */
public class PeerDiscoveryManager {

   private class PeerDiscoveryRunnable implements Runnable {

      @Override
      public void run() {
         try {
            RobotLog.v("Sending peer discovery packet");
            RobocolDatagram packet = new RobocolDatagram(message);
            if (socket.getInetAddress() == null) packet.setAddress(peerDiscoveryDevice);
            socket.send(packet);
         } catch (RobotCoreException e) {
            RobotLog.d("Unable to send peer discovery packet: " + e.toString());
         }
      }
   }

   private InetAddress peerDiscoveryDevice;
   private final RobocolDatagramSocket socket;
   private ScheduledExecutorService discoveryLoopService;
   private ScheduledFuture<?> discoveryLoopFuture;
   private final PeerDiscovery message = new PeerDiscovery(PeerDiscovery.PeerType.PEER);

   /**
    * Constructor
    * @param socket socket to send packets from
    */
   public PeerDiscoveryManager(RobocolDatagramSocket socket) {
      this.socket = socket;
   }

   /**
    * Get the IP address of the peer discovery device
    * @return InetAddress of peer discovery device
    */
   public InetAddress getPeerDiscoveryDevice() {
      return peerDiscoveryDevice;
   }

   /**
    * Start peer discovery
    */
   public void start(InetAddress peerDiscoveryDevice) {
      RobotLog.v("Starting peer discovery");

     if (peerDiscoveryDevice == socket.getLocalAddress()) {
       // we already know about our self
       RobotLog.v("No need for peer discovery, we are the peer discovery device");
       return;
     }

      // stop and old peer discovery service
      if (discoveryLoopFuture != null) discoveryLoopFuture.cancel(true);

      // start the peer discovery service
      this.peerDiscoveryDevice = peerDiscoveryDevice;
      discoveryLoopService = Executors.newSingleThreadScheduledExecutor();
      discoveryLoopFuture = discoveryLoopService.scheduleAtFixedRate(new PeerDiscoveryRunnable(), 1, 1, TimeUnit.SECONDS);
   }

   /**
    * Stop peer discovery
    */
   public void stop() {
      RobotLog.v("Stopping peer discovery");
      if (discoveryLoopFuture != null) discoveryLoopFuture.cancel(true);
   }

}
