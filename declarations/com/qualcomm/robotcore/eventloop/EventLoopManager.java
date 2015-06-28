/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
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

package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.Heartbeat;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.robocol.Telemetry;


public class EventLoopManager {

	public final static String SYSTEM_TELEMETRY = "SYSTEM_TELEMETRY";
	public static final String ROBOT_BATTERY_LEVEL_KEY = "Robot Battery Level";
	public static final String RC_BATTERY_LEVEL_KEY = "RobotController Battery Level";
	public static final String RESTART_OPMODE = "RESTART_OPMODE";
	public static final String OPMODE_RESTART_FINISHED = "OPMODE_RESTART_FINISHED";

	public interface EventLoopMonitor {
		void onStateChange(State state);
	}

	public void handleDroppedConnection() {

	}

	public enum State {NOT_STARTED, INIT, RUNNING, STOPPED, EMERGENCY_STOP, DROPPED_CONNECTION}

	public State state = State.NOT_STARTED;

	public EventLoopManager(RobocolDatagramSocket socket) {

	}

	public void setMonitor(EventLoopMonitor monitor) {

	}
	public void start(EventLoop eventLoop) throws RobotCoreException {

	}

	public void shutdown() {

	}


	public void registerSyncdDevice(SyncdDevice device) {

	}


	public void unregisterSyncdDevice(SyncdDevice device) {

	}

	public void setEventLoop(EventLoop eventLoop) throws RobotCoreException {

	}

	public EventLoop getEventLoop() {
		return null;
	}

	public Gamepad getGamepad() {
		return null;
	}

	public Gamepad getGamepad(int port) {
		return null;
	}

	public Gamepad[] getGamepads() {
		return null;
	}

	public Heartbeat getHeartbeat() {
		return null;
	}

	public boolean isWaitingForRestart(){
		return false;
	}

	public void sendTelemetryData(Telemetry telemetry) {

	}

	public void sendCommand(Command command) {

	}

	public void restartOpMode(String name) {

	}

	public void buildAndSendTelemetry(String tag, String msg){

	}
}
