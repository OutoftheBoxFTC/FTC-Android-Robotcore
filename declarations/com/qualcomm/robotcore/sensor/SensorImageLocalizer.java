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

package com.qualcomm.robotcore.sensor;

import java.util.List;

import com.qualcomm.robotcore.util.Pose;


public class SensorImageLocalizer extends SensorBase<Pose>
implements SensorListener<List<TrackedTargetInfo>> {

	public SensorImageLocalizer(List<SensorListener<Pose>> l) {
		super(l);
	}

	@Override
	public boolean initialize() {
		return false;
	}

	@Override
	public boolean shutdown() {
		return false;
	}

	@Override
	public boolean resume() {
		return false;
	}

	@Override
	public boolean pause() {
		return false;
	}

	public void AddListener(SensorListener<Pose> l) {

	}

	public void RemoveListener(SensorListener<Pose> l) {

	}

	public boolean addTargetReference(String targetName, double xTrans, double yTrans, double zTrans,
			double angle, double longSideTransFromCenterToVertex, double shortSideTransFromCenterToVertex) {
		return false;	
	}

	public boolean addRobotToCameraRef( double length, double width, double height, double angle){
		return false;
	}

	public boolean removeTargetReference(String targetName) {
		return false;
	}

	@Override
	public void onUpdate(List<TrackedTargetInfo> targetPoses) {

	}

}
