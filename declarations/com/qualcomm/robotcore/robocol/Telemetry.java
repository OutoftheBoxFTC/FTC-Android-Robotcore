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

import java.util.Map;

import com.qualcomm.robotcore.exception.RobotCoreException;

public class Telemetry implements RobocolParsable {

	public final static String DEFAULT_TAG = "TELEMETRY_DATA";

	public Telemetry() {

	}

	public Telemetry(byte[] byteArray) throws RobotCoreException {

	}

	public long getTimestamp() {
		return 0L;
	}

	public void setTag(String tag) {

	}

	public String getTag() {
		return null;
	}

	public void addData(String key, String msg) {

	}

	public void addData(String key, float msg) {

	}

	public void addData(String key, double msg) {

	}

	public Map<String, String> getDataStrings() {
		return null;
	}

	public Map<String, Float> getDataNumbers() {
		return null;
	}

	public boolean hasData() {
		return false;
	}

	public void clearData() {

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

}
