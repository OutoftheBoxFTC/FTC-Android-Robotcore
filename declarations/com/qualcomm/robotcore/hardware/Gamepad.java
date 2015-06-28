/*
 * Copyright (c) 2014 Qualcomm Technologies Inc
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

package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable;

public class Gamepad implements RobocolParsable {

	public static final int ID_UNASSOCIATED = -1;

	public interface GamepadCallback {

		public void gamepadChanged(Gamepad gamepad);

	}

	public float left_stick_x = 0f;
	public float left_stick_y = 0f;
	public float right_stick_x = 0f;
	public float right_stick_y = 0f;

	public boolean dpad_up = false;
	public boolean dpad_down = false;
	public boolean dpad_left = false;
	public boolean dpad_right = false;

	public boolean a = false;
	public boolean b = false;
	public boolean x = false;
	public boolean y = false;

	public boolean guide = false;
	public boolean start = false;
	public boolean back = false;

	public boolean left_bumper = false;
	public boolean right_bumper = false;
	public float left_trigger = 0f;
	public float right_trigger = 0f;

	public byte user = ID_UNASSOCIATED;
	public int id = ID_UNASSOCIATED;
	public long timestamp = 0;
	protected float dpadThreshold = 0.2f;
	protected float joystickDeadzone = 0.2f;

	public Gamepad() {

	}

	public Gamepad(GamepadCallback callback) {

	}

	public void setJoystickDeadzone(float deadzone) {

	}

	public void update(Object event) {

	}

	@Override
	public MsgType getRobocolMsgType() {
		return null;
	}

	@Override
	public byte[] toByteArray() throws RobotCoreException {
		return null;
	}

	@Override
	public void fromByteArray(byte[] byteArray) throws RobotCoreException {

	}

	public boolean atRest() {
		return false;
	}

	public String type() {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	protected float cleanMotionValues(float number) {
		return 0;
	}

	protected boolean pressed(Object event) {
		return false;
	}

	protected void callCallback() {}

	public static void enableWhitelistFilter(int vendorId, int productId) {

	}

	public static void clearWhitelistFilter() {

	}

	public static synchronized boolean isGamepadDevice(int deviceId) {
		return false;
	}

}
