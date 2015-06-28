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

import com.qualcomm.robotcore.util.DifferentialControlLoopCoefficients;

public interface DcMotorController {

	public enum RunMode {
		RUN_USING_ENCODERS,
		RUN_WITHOUT_ENCODERS,
		RUN_TO_POSITION,
		RESET_ENCODERS
	}

	public enum DeviceMode {
		SWITCHING_TO_READ_MODE,
		SWITCHING_TO_WRITE_MODE,
		READ_ONLY,
		WRITE_ONLY,
		READ_WRITE
	}

	public abstract String getDeviceName();

	public abstract int getVersion();

	public abstract void close();

	public abstract void setMotorControllerDeviceMode(DeviceMode mode);

	public abstract DeviceMode getMotorControllerDeviceMode();

	public abstract void setMotorChannelMode(int motor, RunMode mode);

	public abstract RunMode getMotorChannelMode(int motor);

	public abstract void setMotorPower(int motor, double power);

	public abstract double getMotorPower(int motor);

	public abstract void setMotorPowerFloat(int motor);

	public abstract boolean getMotorPowerFloat(int motor);

	public abstract void setMotorTargetPosition(int motor, int position);

	public abstract int getMotorTargetPosition(int motor);

	public abstract int getMotorCurrentPosition(int motor);

	public abstract void setGearRatio(int motor, double ratio);

	public abstract double getGearRatio(int motor);

	public abstract void setDifferentialControlLoopCoefficients(int motor, DifferentialControlLoopCoefficients pid);

	public abstract DifferentialControlLoopCoefficients getDifferentialControlLoopCoefficients(int motor);

}
