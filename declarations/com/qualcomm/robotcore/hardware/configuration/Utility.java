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

package com.qualcomm.robotcore.hardware.configuration;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.util.SerialNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Utility {

	public static final String AUTOCONFIGURE_FILENAME = "AutoConfigured";
	public static final String CONFIG_FILES_DIR = null;
	public static final String DEFAULT_ROBOT_CONFIG = "robot_config";
	public static final String FILE_EXT = ".xml";
	public static final String DEFAULT_ROBOT_CONFIG_FILENAME = DEFAULT_ROBOT_CONFIG + FILE_EXT;
	public static final String NO_FILE = "No current file!";
	public static final String UNSAVED = "Unsaved";

	public Utility(Object activity){}

	public void createConfigFolder(){}

	public ArrayList<String> getXMLFiles(){
		return null;
	}

	public boolean writeXML(Map<SerialNumber, ControllerConfiguration> deviceControllers){
		return false;
	}

	public void writeToFile(String filename) throws RobotCoreException, IOException{}

	public String getOutput(){
		return null;
	}

	public void complainToast(String msg, Object context) {

	}

	public void createLists(Set<Map.Entry<SerialNumber, DeviceManager.DeviceType>> entries,
			Map<SerialNumber, ControllerConfiguration> deviceControllers) {

	}

	public ArrayList<DeviceConfiguration> createMotorList(){
		return null;
	}

	public ArrayList<DeviceConfiguration> createServoList(){
		return null;
	}

	public ArrayList<DeviceConfiguration> createLegacyModuleList(){
		return null;
	}

	public void updateHeader(String default_name, int pref_hardware_config_filename_id, int fileTextView, int header_id) {

	}

	public void saveToPreferences(String filename, int pref_hardware_config_filename_id) {

	}

	public void changeBackground(int color, int header_id) {

	}

	public String getFilenameFromPrefs(int pref_hardware_config_filename_id, String default_name){
		return null;
	}

	public void resetCount() {

	}

	public void setOrangeText(String msg0, String msg1, int info_id, int layout_id,
			int orange0, int orange1) {

	}

	public void confirmSave() {

	}

	public Object buildBuilder(String title, String message) {
		return null;
	}

	public String prepareFilename(String currentFile) {
		return null;
	}

}
