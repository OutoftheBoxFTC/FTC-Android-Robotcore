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

package com.qualcomm.robotcore.robocol;

import java.util.Comparator;

import com.qualcomm.robotcore.exception.RobotCoreException;

/**
 * Class used to send and receive commands
 * <p>
 * These commands should be acknowledged by the receiver. The sender may resend the command
 * repeatedly until it receives and acknowledgment from the receiver. The receiver should not
 * reprocess repeated commands.
 */
public class Command implements RobocolParsable, Comparable<Command>, Comparator<Command> {

	public static final int MAX_COMMAND_LENGTH = 256;

	String mName;
	String mExtra;
	byte[] mNameBytes;
	byte[] mExtraBytes;
	long mTimestamp;
	boolean mAcknowledged = false;
	byte mAttempts = 0;

	public Command(String name) {

	}

	public Command(String name, String extra) {

	}

	public Command(byte[] byteArray) throws RobotCoreException {

	}

	public void acknowledge() {

	}

	public boolean isAcknowledged() {
		return false;
	}

	public String getName() {
		return null;
	}

	public String getExtra() {
		return mExtra;
	}

	public byte getAttempts() {
		return 0;
	}

	public long getTimestamp() {
		return 0L;
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

	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public int compareTo(Command another) {
		return 0;
	}

	@Override
	public int compare(Command c1, Command c2) {
		return 0;
	}

	public static long generateTimestamp() {
		return 0L;
	}

}