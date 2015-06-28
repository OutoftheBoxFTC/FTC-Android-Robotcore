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

import java.net.InetAddress;
import java.util.List;

public class WifiDirectAssistant {

	public interface WifiDirectAssistantCallback {
		public void onWifiDirectEvent(Event event);
	}

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

	public synchronized void enable() {

	}

	public synchronized void disable() {

	}

	public synchronized boolean isEnabled() {
		return false;
	}

	public ConnectStatus getConnectStatus() {
		return null;
	}

	public List<?> getPeers() {
		return null;
	}

	public WifiDirectAssistantCallback getCallback() {
		return null;
	}

	public void setCallback(WifiDirectAssistantCallback callback) {

	}

	public String getDeviceMacAddress() {
		return null;
	}

	public String getDeviceName() {
		return null;
	}

	public InetAddress getGroupOwnerAddress() {
		return null;
	}

	public String getGroupOwnerMacAddress() {
		return null;
	}

	public String getGroupOwnerName() {
		return null;
	}

	public String getPassphrase() {
		return null;
	}

	public boolean isWifiP2pEnabled() {
		return false;
	}

	public boolean isConnected() {
		return false;
	}

	public boolean isGroupOwner() {
		return false;
	}

	public void discoverPeers() {

	}

	public void cancelDiscoverPeers() {

	}

	public void createGroup() {

	}

	public void removeGroup() {

	}

	public void connect(Object peer) {

	}

	public String getFailureReason() {
		return null;
	}

	public static String failureReasonToString(int reason) {
		return null;
	}

}
