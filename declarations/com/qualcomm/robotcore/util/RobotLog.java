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

package com.qualcomm.robotcore.util;

import com.qualcomm.robotcore.exception.RobotCoreException;

public class RobotLog {

	private RobotLog() {

	}

	public static final String TAG = "RobotCore";

	public static void v(String message) {

	}

	public static void d(String message) {

	}

	public static void i(String message) {

	}

	public static void w(String message) {

	}

	public static void e(String message) {

	}

	public static void logStacktrace(Exception e) {

	}

	public static void logStacktrace(RobotCoreException e) {

	}

	public static void setGlobalErrorMsg(String message) {

	}

	public static void setGlobalErrorMsgAndThrow(String message, RobotCoreException e) throws RobotCoreException {

	}

	public static String getGlobalErrorMsg() {
		return null;
	}

	public static boolean hasGlobalErrorMsg() {
		return false;
	}

	public static void clearGlobalErrorMsg() {

	}

	public static void logAndThrow(String errMsg) throws RobotCoreException {

	}

	public static void writeLogcatToDisk(Object context, final int fileSizeKb) {

	}

	public static String getLogFilename(Object context) {
		return null;
	}

	public static void cancelWriteLogcatToDisk(Object context) {

	}

}
