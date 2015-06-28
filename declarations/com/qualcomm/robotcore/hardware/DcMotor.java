/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

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

package com.qualcomm.robotcore.hardware;

public class DcMotor {

	public enum Direction { FORWARD, REVERSE };

	protected Direction direction = Direction.FORWARD;
	protected DcMotorController controller = null;
	protected int portNumber = -1;
	protected DcMotorController.RunMode mode = DcMotorController.RunMode.RUN_WITHOUT_ENCODERS;
	protected DcMotorController.DeviceMode devMode = DcMotorController.DeviceMode.WRITE_ONLY;

	public DcMotor(DcMotorController controller, int portNumber) {

	}

	public DcMotor(DcMotorController controller, int portNumber, Direction direction) {

	}

	public DcMotorController getController() {
		return null;
	}

	public void setDirection(Direction direction) {

	}

	public Direction getDirection() {
		return null;
	}

	public int getPortNumber() {
		return 0;
	}

	public void setPower(double power) {

	}

	public double getPower() {
		return 0.0;
	}

	public void setPowerFloat() {

	}

	public boolean getPowerFloat() {
		return false;
	}

	public void setTargetPosition(int position){

	}

	public int getTargetPosition(){
		return 0;
	}

	public int getCurrentPosition(){
		return 0;
	}

	public void setChannelMode(DcMotorController.RunMode mode) {

	}

	public DcMotorController.RunMode getChannelMode(){
		return null;
	}

	public interface MotorCallback {
		public void encoder(int value);
	}

	public void getEncoders(MotorCallback calllback) {

	}

}
