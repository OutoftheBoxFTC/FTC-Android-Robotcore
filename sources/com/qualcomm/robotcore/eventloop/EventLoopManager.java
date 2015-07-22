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

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.Heartbeat;
import com.qualcomm.robotcore.robocol.PeerDiscovery;
import com.qualcomm.robotcore.robocol.RobocolDatagram;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Event Loop Manager
 * <p>
 * Takes RobocolDatagram messages, converts them into the appropriate data type, and then passes it
 * to the current EventLoop.
 */
public class EventLoopManager {

	private static final boolean DEBUG = false;
	private static final int HEARTBEAT_WAIT_DELAY = 250; // in milliseconds
	private static final int MAX_COMMAND_CACHE = 8;
	private static final int MAX_COMMAND_ATTEMPTS = 10;
	private static final int SOCKET_SCHEDULED_SEND_INTERVAL = 100; // in milliseconds

	public final static String SYSTEM_TELEMETRY = "SYSTEM_TELEMETRY";
	public static final String ROBOT_BATTERY_LEVEL_KEY = "Robot Battery Level";
	public static final String RC_BATTERY_LEVEL_KEY = "RobotController Battery Level";
	public static final String RESTART_OPMODE = "RESTART_OPMODE";
	public static final String OPMODE_RESTART_FINISHED = "OPMODE_RESTART_FINISHED";

	private static final EventLoop EMPTY_EVENT_LOOP = new EmptyEventLoop();

	// If no heartbeat is received in this amount of time, forcibly shut down the robot
	private static final double SECONDS_UNTIL_FORCED_SHUTDOWN = 2.0;

	/**
	 * Callback to monitor when event loop changes state
	 */
	//verified consistant with decompiled
	public interface EventLoopMonitor {
		void onStateChange(State state);
	}

	/*
	 * Responsible for sending scheduled items via the socket
	 * 
	 * appears as 'd' on decompiled version
	 * verified consistant with decompiled
	 */
	private class ScheduledSendRunnable implements Runnable {

		private Set<Command> removeList = new HashSet<Command>();

		@Override
		public void run() {

			while (!Thread.interrupted()) {

				for (Command cmd : commandSendCache) {
					if (cmd.getAttempts() > MAX_COMMAND_ATTEMPTS) {
						// too many attempts
						RobotLog.w("Failed to send command, too many attempts: " + cmd.toString());
						removeList.add(cmd);
					} else if (cmd.isAcknowledged()) {
						// command ack'd, remove from list
						RobotLog.v("Command " + cmd.getName() + " has been acknowledged by remote device");
						removeList.add(cmd);
					} else {
						// no ack received, send
						try {
							RobotLog.v("Sending command: " + cmd.getName() + ", attempt " + cmd.getAttempts());
							socket.send(new RobocolDatagram(cmd.toByteArray()));
						} catch (RobotCoreException e) {
							RobotLog.w("Failed to send command " + cmd.getName());
							RobotLog.logStacktrace(e);
						}
					}
				}

				// update cache
				commandSendCache.removeAll(removeList);
				removeList.clear();

				// sleep
				try {
					Thread.sleep(SOCKET_SCHEDULED_SEND_INTERVAL);
				} catch (InterruptedException e) {
					// if we are interrupted, shut down this thread
					return;
				}
			}
		}
	}

	/*
	 * Responsible for pulling items off the socket and processing them
	 * 
	 * appears as 'c' on decompiled
	 * verified consistant
	 */
	private class RecvRunnable implements Runnable {

		ElapsedTime timer = new ElapsedTime();

		@Override
		public void run() {

			while (true) {
				RobocolDatagram msg = socket.recv();

				if (shutdownRecvLoop == true || socket.isClosed()) {
					return;
				}

				if (msg == null) {
					Thread.yield();
					continue;
				}

				if (RobotLog.hasGlobalErrorMsg()) {
					buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
				}

				if (DEBUG)
					timer.reset();

				try {
					if (EventLoopManager.this.isWaitingForRestart){
						RobotLog.e("Dropped connection... last running op mode: " + EventLoopManager.this.lastActiveOpMode);
						buildAndSendTelemetry(RESTART_OPMODE, lastActiveOpMode);
					}

					switch (msg.getMsgType()) {
					case GAMEPAD:
						processGamepadEvent(msg);
						break;
					case HEARTBEAT:
						processHeartbeatEvent(msg);
						break;
					case PEER_DISCOVERY:
						processPeerDiscoveryEvent(msg);
						break;
					case COMMAND:
						processCommandEvent(msg);
						break;
					case EMPTY:
						processEmptyEvent();
						break;
					default:
						processUnknownEvent(msg);
						break;
					}
				} catch (RobotCoreException e) {
					RobotLog.w("RobotCore event loop cannot process event: " + e);
				}
				
				if (DEBUG)
					timer.log("recv runnable");
			}
		}
	}

	/*
	 * Responsible for calling loop on the assigned event loop
	 * 
	 * appears as 'b' on decompiled
	 * verified consistant
	 */
	private class EventLoopRunnable implements Runnable {
		
		@Override
		public void run() {

			RobotLog.v("EventLoopRunnable has started");
			try {
				ElapsedTime loopTime = new ElapsedTime();
				final double MIN_THROTTLE = 0.0010; // in seconds
				final long THROTTLE_RESOLUTION = 5; // in milliseconds

				while (Thread.interrupted() == false) {

					while (loopTime.time() < MIN_THROTTLE) {
						// don't go faster than throttle allows
						Thread.sleep(THROTTLE_RESOLUTION);
					}
					loopTime.reset();

					if (RobotLog.hasGlobalErrorMsg()) {
						buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
					}

					// skip this iteration if we've never received a heartbeat
					if (lastHeartbeatReceived.startTime() == 0.0) {
						Thread.sleep(HEARTBEAT_WAIT_DELAY);
						continue;
					}

					if (lastHeartbeatReceived.time() > SECONDS_UNTIL_FORCED_SHUTDOWN) {
						// we haven't received a heartbeat from the driver station in a while
						handleDroppedConnection();
						Thread.sleep(1000); //from decompiled version
					}

					// wait for all sync'd devices to be ready
					for (SyncdDevice device : syncdDevices) {
						device.blockUntilReady();
					}

					// run the event loop
					try {
						eventLoop.loop();
					} catch (Exception e) {
						// we should catch everything, since we don't know what the event loop might throw
						RobotLog.e("Event loop threw an exception");
						RobotLog.logStacktrace(e);

						// display error message.
						String errorMsg = e.getMessage();
						RobotLog.setGlobalErrorMsg("User code threw an uncaught exception: " + errorMsg);

						buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());

						throw new RobotCoreException("EventLoop Exception in loop()");
					} finally {
						// notify sync'd devices that the event loop is complete
						for (SyncdDevice device : syncdDevices) {
							device.startBlockingWork();
						}
					}
				}
			} catch (InterruptedException e) {
				// interrupted, cancel this loop
				RobotLog.v("EventLoopRunnable interrupted");
			} catch (RobotCoreException e) {
				RobotLog.v("RobotCoreException in EventLoopManager: " + e.getMessage());
				changeState(State.EMERGENCY_STOP);

				buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
			}
			RobotLog.v("EventLoopRunnable has exited");
		}
	}

	// verified consistant
	public void handleDroppedConnection(){
		this.clientAddr = null; // assume this client is no longer connected
		OpModeManager opModeManager = eventLoop.getOpModeManager();
		if (!this.isWaitingForRestart) {
			this.lastActiveOpMode = opModeManager.getActiveOpModeName();
			this.isWaitingForRestart = true;
		}
		String msg = "Lost connection while running op mode: " + lastActiveOpMode;
		this.changeState(State.DROPPED_CONNECTION);

		if (!this.lastActiveOpMode.equals(OpModeManager.DEFAULT_OP_MODE_NAME)) {
			RobotLog.setGlobalErrorMsg(msg);
		}
		RobotLog.i(msg);
		opModeManager.switchOpModes(OpModeManager.DEFAULT_OP_MODE_NAME);
	}
	
	/*
	 * Empty event loop, used as a sane default
	 * 
	 * appears as 'a' on decompiled
	 * verified consistant
	 */
	private static class EmptyEventLoop implements EventLoop {
		
		@Override
		public void init(EventLoopManager eventProcessor) {} // take no action

		@Override
		public void loop() {} // take no action

		@Override
		public void teardown() {} // take no action

		@Override
		public void processCommand(Command command) {
			RobotLog.w("Dropping command " + command.getName() + ", no active event loop");
		}

		@Override
		public OpModeManager getOpModeManager(){return null;};
	}

	//verified consistant
	public enum State {
		NOT_STARTED,
		INIT,
		RUNNING,
		STOPPED,
		EMERGENCY_STOP,
		DROPPED_CONNECTION
	}

	public State state = State.NOT_STARTED;

	private Thread eventLoopThread = new Thread();
	private Thread scheduledSendThread = new Thread();

	private final RobocolDatagramSocket socket;
	private boolean shutdownRecvLoop = false;
	private boolean isWaitingForRestart = false;

	private ElapsedTime lastHeartbeatReceived = new ElapsedTime();
	private String lastActiveOpMode = "";

	private EventLoop eventLoop = EMPTY_EVENT_LOOP;

	private final Gamepad gamepad[] = { new Gamepad(), new Gamepad() };
	private Heartbeat heartbeat = new Heartbeat(Heartbeat.Token.EMPTY);

	private EventLoopMonitor callback = null;

	private final Set<SyncdDevice> syncdDevices = new CopyOnWriteArraySet<SyncdDevice>();
	private final Command[] commandRecvCache = new Command[MAX_COMMAND_CACHE];
	private int commandRecvCachePosition = 0;

	private final Set<Command> commandSendCache = new CopyOnWriteArraySet<Command>();

	private InetAddress clientAddr;

	/**
	 * Constructor
	 *
	 * @param socket socket for IO with remote device
	 */
	// verified consistant
	public EventLoopManager(RobocolDatagramSocket socket) {
		this.socket = socket;
		this.changeState(State.NOT_STARTED);
	}

	/**
	 * Set a monitor for this event loop
	 *
	 * @param monitor event loop monitor
	 */
	// verified consistant
	public void setMonitor(EventLoopMonitor monitor) {
		this.callback = monitor;
	}

	/**
	 * Start the event processor
	 *
	 * @param eventLoop set initial event loop
	 * @throws RobotCoreException if event loop fails to init
	 */
	// verified consistant
	public void start(EventLoop eventLoop) throws RobotCoreException {
		this.shutdownRecvLoop = false;

		this.scheduledSendThread = new Thread(new ScheduledSendRunnable());
		this.scheduledSendThread.start();

		new Thread(new RecvRunnable()).start();
		this.setEventLoop(eventLoop);
	}

	/**
	 * Shut down the event processor
	 */
	// verified consistant
	public void shutdown() {
		this.socket.close();
		this.scheduledSendThread.interrupt();
		this.shutdownRecvLoop = true;
		this.stopEventLoop();
	}

	/**
	 * Register a sync'd device
	 * 
	 * @param device sync'd device
	 */
	// verified consistant
	public void registerSyncdDevice(SyncdDevice device) {
		this.syncdDevices.add(device);
	}

	/**
	 * Unregister a sync'd device
	 * 
	 * @param device sync'd device
	 */
	// verified consistant
	public void unregisterSyncdDevice(SyncdDevice device) {
		this.syncdDevices.remove(device);
	}

	/**
	 * Replace the current event loop with a new event loop
	 *
	 * @param eventLoop new event loop
	 * @throws RobotCoreException if event loop fails to init
	 */
	// verified consistant
	public void setEventLoop(EventLoop eventLoop) throws RobotCoreException {
		if (eventLoop == null) {
			eventLoop = EMPTY_EVENT_LOOP;
			RobotLog.d("Event loop cannot be null, using empty event loop");
		}

		// cancel the old event loop
		this.stopEventLoop();

		// assign the new event loop
		this.eventLoop = eventLoop;

		// start the new event loop
		this.startEventLoop();
	}

	/**
	 * Get the current event loop
	 *
	 * @return current event loop
	 */
	// verified consistant
	public EventLoop getEventLoop() {
		return this.eventLoop;
	}

	/**
	 * Get the current gamepad state
	 * <p>
	 * Port 0 is assumed
	 *
	 * @see EventLoopManager#getGamepad(int)
	 * @return gamepad
	 */
	// verified consistant
	public Gamepad getGamepad() {
		return getGamepad(0);
	}

	/**
	 * Get the gamepad connected to a particular user
	 * @param port user 0 and 1 are valid
	 * @return gamepad
	 */
	// verified consistant
	public Gamepad getGamepad(int port) {
		Range.throwIfRangeIsInvalid(port, 0, 1);
		return gamepad[port];
	}

	/**
	 * Get the gamepads
	 * <p>
	 * Array index will match the user number
	 * @return gamepad
	 */
	// verified consistant
	public Gamepad[] getGamepads() {
		return gamepad;
	}

	/**
	 * Get the current heartbeat state
	 *
	 * @return heartbeat
	 */
	// verified consistant
	public Heartbeat getHeartbeat() {
		return heartbeat;
	}

	// verified consistant
	private void clearWaitForRestart() {
		isWaitingForRestart = false;
	}

	// verified consistant
	public boolean isWaitingForRestart(){
		return isWaitingForRestart;
	}

	/**
	 * Send telemetry data
	 * <p>
	 * Send the telemetry data, and then clear the sent data
	 * @param telemetry telemetry data
	 */
	// verified consistant
	public void sendTelemetryData(Telemetry telemetry) {
		try {
			socket.send(new RobocolDatagram(telemetry.toByteArray()));
		} catch (RobotCoreException e) {
			RobotLog.w("Failed to send telemetry data");
			RobotLog.logStacktrace(e);
		}

		// clear the stale telemetry data
		telemetry.clearData();
	}

	// verified consistant
	public void sendCommand(Command command) {
		commandSendCache.add(command);
	}

	// verified consistant
	private void startEventLoop() throws RobotCoreException {
		// call the init method
		try {
			this.changeState(State.INIT);
			this.eventLoop.init(this);

			// notify sync'd devices that the event loop init is complete
			for (SyncdDevice device : this.syncdDevices) {
				device.startBlockingWork();
			}
		} catch (Exception e) {
			RobotLog.w("Caught exception during looper init: " + e);
			RobotLog.logStacktrace(e);
			this.changeState(State.EMERGENCY_STOP);

			if (RobotLog.hasGlobalErrorMsg()) {
				buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
			}

			throw new RobotCoreException("Robot failed to start: " + e.getMessage());
		}

		// reset the heartbeat timer
		this.lastHeartbeatReceived = new ElapsedTime(0);

		// start the new event loop
		this.changeState(State.RUNNING);

		this.eventLoopThread = new Thread(new EventLoopRunnable());
		this.eventLoopThread.start();
	}

	// verified consistant
	private void stopEventLoop() {
		// cancel the old event loop
		eventLoopThread.interrupt();

		// TODO: add an 'event loop finish' notification (Thunderbots)		
		// give time for the event loop to finish
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// if we receive an interrupt we will rush the event loop teardown
		}

		// inform old event loop that it's been shut down
		changeState(State.STOPPED);
		try {
			this.eventLoop.teardown();
		} catch (Exception e) {
			RobotLog.w("Caught exception during looper teardown: " + e);
			RobotLog.logStacktrace(e);

			if (RobotLog.hasGlobalErrorMsg()) {
				buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
			}
		}

		eventLoop = EMPTY_EVENT_LOOP;

		// unregister all sync'd devices
		syncdDevices.clear();
	}

	// verified consistant
	private void changeState(State state) {
		this.state = state;
		RobotLog.v("EventLoopManager state is " + state);
		if (callback != null)
			this.callback.onStateChange(state);
	}

	// was named 'restartOpMode'
	// renamed 'noLongerWaitForRestart' in decompiled
	public void noLongerWaitForRestart(String name) {
		this.clearWaitForRestart();
		RobotLog.clearGlobalErrorMsg();
		RobotLog.v("Restarting op mode: " + name);
		this.changeState(State.RUNNING);
	}

	/*
	 * Event processing methods
	 */
	// verified consistant
	private void processGamepadEvent(RobocolDatagram msg) throws RobotCoreException {
		if (DEBUG)
			RobotLog.v("processing gamepad event");
		
		Gamepad incomingGamepad = new Gamepad();
		incomingGamepad.fromByteArray(msg.getData());

		if (incomingGamepad.user < 1 || incomingGamepad.user > 2) {
			// this gamepad user is invalid, we cannot use
			RobotLog.d("Gamepad with user %d received. Only users 1 and 2 are valid"); //TODO: why the unresolved %d? (Thunderbots)
			return;
		}

		int position = incomingGamepad.user - 1;

		// swap out the old gamepad state for the current gamepad state
		gamepad[position] = incomingGamepad;

		if (gamepad[0].id == gamepad[1].id) {
			// a gamepad was moved, reset the old gamepad
			RobotLog.v("Gamepad moved position, removing stale gamepad");
			if (position == 0)
				gamepad[1] = new Gamepad();
			if (position == 1)
				gamepad[0] = new Gamepad();
		}
	}

	// verified consistant
	private void processHeartbeatEvent(RobocolDatagram msg) throws RobotCoreException {
		if (DEBUG)
			RobotLog.v("processing heartbeat event");

		this.socket.send(msg);
		Heartbeat currentHeartbeat = new Heartbeat(Heartbeat.Token.EMPTY);
		currentHeartbeat.fromByteArray(msg.getData());

		this.lastHeartbeatReceived.reset();
		this.heartbeat = currentHeartbeat;
	}

	// verified consistant
	private void processPeerDiscoveryEvent(RobocolDatagram msg) throws RobotCoreException {
		if (DEBUG)
			RobotLog.v("processing peer discovery event");
		
		if (msg.getAddress().equals(clientAddr))
			return; // no action needed

		// do not respond if the empty event loop is running
		if (eventLoop == EMPTY_EVENT_LOOP)
			return;

		// update remoteAddr with latest address
		this.clientAddr = msg.getAddress();
		RobotLog.i("new remote peer discovered: " + clientAddr.getHostAddress());

		try {
			socket.connect(clientAddr);
		} catch (SocketException e) {
			RobotLog.e("Unable to connect to peer:" + e);
		}

		final PeerDiscovery message = new PeerDiscovery(PeerDiscovery.PeerType.PEER);
		RobotLog.v("Sending peer discovery packet");
		RobocolDatagram packet = new RobocolDatagram(message);
		if (socket.getInetAddress() == null)
			packet.setAddress(clientAddr);
		socket.send(packet);
	}

	// verified consistant
	private void processCommandEvent(RobocolDatagram msg) throws RobotCoreException {
		if (DEBUG)
			RobotLog.v("processing command event");

		Command command = new Command(msg.getData());

		// is this a command ack?
		if (command.isAcknowledged()) {
			// yes, remove from send cache and stop processing
			commandSendCache.remove(command);
			return;
		}

		// acknowledge this command
		command.acknowledge();
		socket.send(new RobocolDatagram(command));

		// check if it's in the cache
		for (Command c : commandRecvCache) {
			if (c != null && c.equals(command)) {
				// this command is in the cache, which means we've already handled it
				// no need to continue, just return now
				return;
			}
		}

		// cache the command
		commandRecvCache[(commandRecvCachePosition++) % commandRecvCache.length] = command;

		// process the command
		try {
			eventLoop.processCommand(command);
		} catch (Exception e) {
			// we should catch everything, since we don't know what the event loop might throw
			RobotLog.e("Event loop threw an exception while processing a command");
			RobotLog.logStacktrace(e);
		}
	}

	// verified consistant
	private void processEmptyEvent() {
		// take no action
	}

	// verified consistant
	private void processUnknownEvent(RobocolDatagram msg) {
		RobotLog.w("RobotCore event loop received unknown event type: " + msg.getMsgType().name());
	}

	// verified consistant
	public void buildAndSendTelemetry(String tag, String msg){
		Telemetry telemetry = new Telemetry();
		telemetry.setTag(tag);
		telemetry.addData(tag, msg);
		sendTelemetryData(telemetry);
	}
	
}
