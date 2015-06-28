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

package com.qualcomm.robotcore.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Looper;

import com.qualcomm.robotcore.util.RobotLog;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WifiDirectAssistant {

  public interface WifiDirectAssistantCallback {
    public void onWifiDirectEvent(Event event);
  }

  private static WifiDirectAssistant wifiDirectAssistant = null;

  private final List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
  private Context context = null;
  private boolean isWifiP2pEnabled = false;
  private final IntentFilter intentFilter;
  private final Channel wifiP2pChannel;
  private final WifiP2pManager wifiP2pManager;
  private WifiP2pBroadcastReceiver receiver;
  private final WifiDirectConnectionInfoListener connectionListener;
  private final WifiDirectPeerListListener peerListListener;
  private final WifiDirectGroupInfoListener groupInfoListener;
  private int failureReason = WifiP2pManager.ERROR;
  private ConnectStatus connectStatus = ConnectStatus.NOT_CONNECTED;
  private Event lastEvent = null;

  private String deviceMacAddress = "";
  private String deviceName = "";
  private InetAddress groupOwnerAddress = null;
  private String groupOwnerMacAddress = "";
  private String groupOwnerName = "";
  private String passphrase = "";
  private boolean groupFormed = false;

  // tracks the number of clients, must be thread safe
  private int clients = 0;

  private WifiDirectAssistantCallback callback = null;

  public enum Event {
    DISCOVERING_PEERS,
    PEERS_AVAILABLE,
    GROUP_CREATED,
    CONNECTING,
    CONNECTED_AS_PEER,
    CONNECTED_AS_GROUP_OWNER,
    DISCONNECTED,
    CONNECTION_INFO_AVAILABLE,
    ERROR
  }

  public enum ConnectStatus {
    NOT_CONNECTED,
    CONNECTING,
    CONNECTED,
    GROUP_OWNER,
    ERROR
  }

  /*
   * Maintains the list of wifi p2p peers available
   */
  private class WifiDirectPeerListListener implements PeerListListener {

    /*
     * callback method, called by Android when the peer list changes
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

      peers.clear();
      peers.addAll(peerList.getDeviceList());

      RobotLog.v("Wifi Direct peers found: " + peers.size());
      for (WifiP2pDevice peer : peers) {
        // deviceAddress is the MAC address, deviceName is the human readable name
        String s = "    peer: " + peer.deviceAddress + " " + peer.deviceName;
        RobotLog.v(s);
      }

      sendEvent(Event.PEERS_AVAILABLE);
    }
  }

  /*
   * Updates when this device connects
   */
  private class WifiDirectConnectionInfoListener implements WifiP2pManager.ConnectionInfoListener {

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

      wifiP2pManager.requestGroupInfo(wifiP2pChannel, groupInfoListener);
      groupOwnerAddress = info.groupOwnerAddress;
      RobotLog.d("Group owners address: " + groupOwnerAddress.toString());

      if (info.groupFormed && info.isGroupOwner) {
        RobotLog.d("Wifi Direct group formed, this device is the group owner (GO)");
        connectStatus = ConnectStatus.GROUP_OWNER;
        sendEvent(Event.CONNECTED_AS_GROUP_OWNER);
      } else if (info.groupFormed) {
        RobotLog.d("Wifi Direct group formed, this device is a client");
        connectStatus = ConnectStatus.CONNECTED;
        sendEvent(Event.CONNECTED_AS_PEER);
      } else {
        RobotLog.d("Wifi Direct group NOT formed, ERROR: " + info.toString());
        failureReason = WifiP2pManager.ERROR; // there is no error code for this
        connectStatus = ConnectStatus.ERROR;
        sendEvent(Event.ERROR);
      }
    }
  }

  private class WifiDirectGroupInfoListener implements WifiP2pManager.GroupInfoListener {

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
      if (group == null) return;

      if (group.isGroupOwner()) {
        groupOwnerMacAddress = deviceMacAddress;
        groupOwnerName = deviceName;
      } else {
        WifiP2pDevice go = group.getOwner();
        groupOwnerMacAddress = go.deviceAddress;
        groupOwnerName = go.deviceName;
      }

      passphrase = group.getPassphrase();

      // make sure passphase isn't null
      passphrase = (passphrase != null) ? passphrase : "";

      RobotLog.v("Wifi Direct connection information available");
      sendEvent(Event.CONNECTION_INFO_AVAILABLE);
    }
  }

  private class WifiP2pBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        isWifiP2pEnabled = (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
        RobotLog.d("Wifi Direct state - enabled: " + isWifiP2pEnabled);
      } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        RobotLog.d("Wifi Direct peers changed");
        wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
      } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        WifiP2pInfo wifip2pinfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
        RobotLog.d("Wifi Direct connection changed - connected: " + networkInfo.isConnected());
        if (networkInfo.isConnected()) {
          wifiP2pManager.requestConnectionInfo(wifiP2pChannel, connectionListener);
          wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, null);
        } else {
          connectStatus = ConnectStatus.NOT_CONNECTED;
          if (groupFormed == false) {
            discoverPeers();
          }
          // if we were previously connected, notify that we are now disconnected
          if (isConnected() == true) {
            sendEvent(Event.DISCONNECTED);
          }
          groupFormed = wifip2pinfo.groupFormed;
        }
      } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        RobotLog.d("Wifi Direct this device changed");
        onWifiP2pThisDeviceChanged((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
      }
    }
  }

  public synchronized static WifiDirectAssistant getWifiDirectAssistant(Context context) {
    if (wifiDirectAssistant == null) wifiDirectAssistant = new WifiDirectAssistant(context);

    return wifiDirectAssistant;
  }

  private WifiDirectAssistant(Context context) {
    this.context = context;

    // Set up the intent filter for wifi direct
    intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
    wifiP2pChannel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null);
    receiver = new WifiP2pBroadcastReceiver();
    connectionListener = new WifiDirectConnectionInfoListener();
    peerListListener = new WifiDirectPeerListListener();
    groupInfoListener = new WifiDirectGroupInfoListener();
  }

  public synchronized void enable() {
    clients += 1;
    RobotLog.v("There are " + clients + " Wifi Direct Assistant Clients (+)");

    if (clients == 1) {
      RobotLog.v("Enabling Wifi Direct Assistant");
      if (receiver == null) receiver = new WifiP2pBroadcastReceiver();
      context.registerReceiver(receiver, intentFilter);
    }
  }

  public synchronized void disable() {
    clients -= 1;
    RobotLog.v("There are " + clients + " Wifi Direct Assistant Clients (-)");

    if (clients == 0) {
      RobotLog.v("Disabling Wifi Direct Assistant");
      wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, null);
      wifiP2pManager.cancelConnect(wifiP2pChannel, null);

      try {
        context.unregisterReceiver(receiver);
      } catch (IllegalArgumentException e) {
        // disable() was called, but enable() was never called; ignore
      }
      lastEvent = null;
    }
  }

  public synchronized boolean isEnabled() {
    return (clients > 0);
  }

  public ConnectStatus getConnectStatus() {
    return connectStatus;
  }

  public List<WifiP2pDevice> getPeers() {
    return new ArrayList<WifiP2pDevice>(peers);
  }

  public WifiDirectAssistantCallback getCallback() {
    return callback;
  }

  public void setCallback(WifiDirectAssistantCallback callback) {
    this.callback = callback;
  }

  /**
   * Get the device mac address
   * @return mac address
   */
  public String getDeviceMacAddress() {
    return deviceMacAddress;
  }

  /**
   * Get the device name
   * @return device name
   */
  public String getDeviceName() {
    return deviceName;
  }

  /**
   * Get the IP address of the group owner
   * @return ip address
   */
  public InetAddress getGroupOwnerAddress() {
    return groupOwnerAddress;
  }

  /**
   * Get the group owners mac address
   * @return mac address
   */
  public String getGroupOwnerMacAddress() {
    return groupOwnerMacAddress;
  }

  /**
   * Get the group owners device name
   * @return device name
   */
  public String getGroupOwnerName() {
    return groupOwnerName;
  }

  /**
   * Return the passphrase for this network; only valid if this device is the group owner
   * @return the passphrase to this device
   */
  public String getPassphrase() {
    return passphrase;
  }

  public boolean isWifiP2pEnabled() {
    return isWifiP2pEnabled;
  }

  /**
   * Returns true if connected, or group owner
   * @return true if connected, otherwise false
   */
  public boolean isConnected() {
    return (connectStatus == ConnectStatus.CONNECTED
        || connectStatus == ConnectStatus.GROUP_OWNER);
  }

  /**
   * Returns true if this device is the group owner
   * @return true if group owner, otherwise false
   */
  public boolean isGroupOwner() {
    return (connectStatus == ConnectStatus.GROUP_OWNER);
  }

  /**
   * Discover Wifi Direct peers
   */
  public void discoverPeers() {
    wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {

      @Override
      public void onSuccess() {
        sendEvent(Event.DISCOVERING_PEERS);
        RobotLog.d("Wifi Direct discovering peers");
      }

      @Override
      public void onFailure(int reason) {
        String reasonStr = failureReasonToString(reason);
        failureReason = reason;
        RobotLog.w("Wifi Direct failure while trying to discover peers - reason: " + reasonStr);
        sendEvent(Event.ERROR);
      }
    });
  }

  /**
   * Cancel discover Wifi Direct peers request
   */
  public void cancelDiscoverPeers() {
    RobotLog.d("Wifi Direct stop discovering peers");
    wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, null);
  }

  /**
   * Create a Wifi Direct group
   * <p>
   * Will receive a Event.GROUP_CREATED if the group is created. If there is an
   * error creating group Event.ERROR will be sent. If group already exists, no
   * event will be sent. However, a Event.CONNECTED_AS_GROUP_OWNER should be
   * received.
   */
  public void createGroup() {
    wifiP2pManager.createGroup(wifiP2pChannel, new WifiP2pManager.ActionListener() {

      @Override
      public void onSuccess() {
        connectStatus = ConnectStatus.GROUP_OWNER;
        sendEvent(Event.GROUP_CREATED);
        RobotLog.d("Wifi Direct created group");
      }

      @Override
      public void onFailure(int reason) {
        if (reason == WifiP2pManager.BUSY) {
          // most likely group is already created
          RobotLog.d("Wifi Direct cannot create group, does group already exist?");
        } else {
          String reasonStr = failureReasonToString(reason);
          failureReason = reason;
          RobotLog.w("Wifi Direct failure while trying to create group - reason: " + reasonStr);
          connectStatus = ConnectStatus.ERROR;
          sendEvent(Event.ERROR);
        }
      }
    });
  }

  /**
   * Remove a Wifi Direct group
   */
  public void removeGroup() {
    wifiP2pManager.removeGroup(wifiP2pChannel, null);
  }

  public void connect(WifiP2pDevice peer) {
    if (connectStatus == ConnectStatus.CONNECTING || connectStatus == ConnectStatus.CONNECTED) {
      RobotLog.d("WifiDirect connection request to " + peer.deviceAddress + " ignored, already connected");
      return;
    }

    RobotLog.d("WifiDirect connecting to " + peer.deviceAddress);
    connectStatus = ConnectStatus.CONNECTING;

    WifiP2pConfig config = new WifiP2pConfig();
    config.deviceAddress = peer.deviceAddress;
    config.wps.setup = WpsInfo.PBC;
    config.groupOwnerIntent = 1;

    wifiP2pManager.connect(wifiP2pChannel, config, new WifiP2pManager.ActionListener() {

      @Override
      public void onSuccess() {
        RobotLog.d("WifiDirect connect started");
        sendEvent(Event.CONNECTING);
      }

      @Override
      public void onFailure(int reason) {
        String reasonStr = failureReasonToString(reason);
        failureReason = reason;
        RobotLog.d("WifiDirect connect cannot start - reason: " + reasonStr);
        sendEvent(Event.ERROR);
      }
    });
  }

  private void onWifiP2pThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
    deviceName = wifiP2pDevice.deviceName;
    deviceMacAddress = wifiP2pDevice.deviceAddress;
    RobotLog.v("Wifi Direct device information: " + deviceName + " " + deviceMacAddress);
  }

  public String getFailureReason() {
    return failureReasonToString(failureReason);
  }

  public static String failureReasonToString(int reason) {
    switch (reason) {
      case WifiP2pManager.P2P_UNSUPPORTED:
        return "P2P_UNSUPPORTED";
      case WifiP2pManager.ERROR:
        return "ERROR";
      case WifiP2pManager.BUSY:
        return "BUSY";
      default:
        return "UNKNOWN (reason " + reason + ")" ;
    }
  }

  private void sendEvent(Event event) {
    // don't send duplicate events
    if (lastEvent == event && lastEvent != Event.PEERS_AVAILABLE) return;
    lastEvent = event;

    if (callback != null) callback.onWifiDirectEvent(event);
  }
}
