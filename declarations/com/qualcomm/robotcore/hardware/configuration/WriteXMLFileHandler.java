/* Copyright (c) 2014 Qualcomm Technologies Inc

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

import android.content.Context;
import android.util.Xml;

import com.qualcomm.robotcore.exception.RobotCoreException;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;

//import com.qualcomm.robotcore.util.RobotLog;

public class WriteXMLFileHandler {

  private Context context;
  private XmlSerializer serializer;
  private HashSet<String> names = new HashSet<String>();
  private ArrayList<String> duplicates = new ArrayList<String>();
  private String[] indentation = {"    ", "        ", "            "};
  private int indent = 0;

  public WriteXMLFileHandler(Context context) {
    this.context = context;
    serializer = Xml.newSerializer();
  }


  public String writeXml(ArrayList<ControllerConfiguration> deviceControllerConfigurations){
    duplicates = new ArrayList<String>();
    names = new HashSet<String>();

    StringWriter writer = new StringWriter();
    try {
      serializer.setOutput(writer);
      serializer.startDocument("UTF-8", true);
      serializer.ignorableWhitespace("\n");
      serializer.startTag("", "Robot");
      serializer.ignorableWhitespace("\n");
      for (ControllerConfiguration controllerConfiguration : deviceControllerConfigurations){
        // Do the same thing for DcMotorControllers, ServoControllers, and LegacyModules
        String type = controllerConfiguration.getType().toString();
        if (type.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER.toString()) ||
                type.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER.toString())){
          handleController(controllerConfiguration);
        }
        if (type.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())){
          handleLegacyModuleController(controllerConfiguration);
        }
      }
      serializer.endTag("", "Robot");
      serializer.endDocument();
      return writer.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void checkForDuplicates(String name){
    if (name.equalsIgnoreCase(DeviceConfiguration.DISABLED_DEVICE_NAME)){
      return;
    }
    if (names.contains(name)){
      duplicates.add(name);
    } else {
      names.add(name);
    }
  }

  private void handleLegacyModuleController(ControllerConfiguration controller) throws IOException {
    serializer.ignorableWhitespace(indentation[indent]);
    serializer.startTag("", conform(controller.getType().toString()));
    String name = controller.getName();
    checkForDuplicates(name);
    serializer.attribute("", "name", controller.getName());
    serializer.attribute("", "serialNumber", controller.getSerialNumber().toString());
    serializer.ignorableWhitespace("\n");
    indent++;

    // step through the list of attached devices,
    ArrayList<DeviceConfiguration> devices = (ArrayList<DeviceConfiguration>) controller.getDevices();
    for (DeviceConfiguration device : devices){
      String type = device.getType().toString();
      if (type.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER.toString()) ||
              type.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER.toString())){
        handleController((ControllerConfiguration)device);
      } else {
        buildDevice(serializer, device);
      }
    }

    indent--;
    serializer.ignorableWhitespace(indentation[indent]);
    serializer.endTag("", conform(controller.getType().toString()));
    serializer.ignorableWhitespace("\n");
  }

  private void handleController(ControllerConfiguration controller) throws IOException {
    serializer.ignorableWhitespace(indentation[indent]);
    serializer.startTag("", conform(controller.getType().toString()));
    String name = controller.getName();
    checkForDuplicates(name);
    serializer.attribute("", "name", controller.getName());
    serializer.attribute("", "serialNumber", controller.getSerialNumber().toString());
    serializer.attribute("", "port", String.valueOf(controller.getPort()));
    serializer.ignorableWhitespace("\n");
    indent++;

    // step through the list of attached devices,
    ArrayList<DeviceConfiguration> devices = (ArrayList<DeviceConfiguration>) controller.getDevices();
    for (DeviceConfiguration device : devices){
      buildDevice(serializer, device);
    }
    indent--;
    serializer.ignorableWhitespace(indentation[indent]);
    serializer.endTag("", conform(controller.getType().toString()));
    serializer.ignorableWhitespace("\n");
  }

  private void buildDevice(XmlSerializer serializer, DeviceConfiguration device){
    try {
      serializer.ignorableWhitespace(indentation[indent]);
      serializer.startTag("", conform(device.getType().toString()));
      String name = device.getName();
      checkForDuplicates(name);
      serializer.attribute("", "name", device.getName());
      serializer.attribute("", "port", String.valueOf(device.getPort()));
      serializer.endTag("", conform(device.getType().toString()));
      serializer.ignorableWhitespace("\n");
    } catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  public void writeToFile(String data, String folderName, String filename) throws RobotCoreException, IOException{
    if (duplicates.size() > 0){
      throw new IOException("Duplicate names: " + duplicates);
    }
    filename = filename.replaceFirst("[.][^.]+$", ""); // strip .xml

    File folder = new File(folderName);
    boolean success = true;

    if (!folder.exists()){
      success = folder.mkdir();
    }
    if (success){
      File file = new File(folderName + filename + Utility.FILE_EXT);
      FileOutputStream stream = null;
      try{
        stream = new FileOutputStream(file);
        stream.write(data.getBytes());
      } catch (Exception e){
        e.printStackTrace();
      } finally {
        try {
          stream.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    } else {
      throw new RobotCoreException("Unable to create directory");
    }
  }

  private String conform(String old){

    //UpperCamelCase (first letter capitalized)
    String standardized = old.substring(0, 1) + old.substring(1).toLowerCase();
    int hyphenIndex = old.lastIndexOf("_");

    while (hyphenIndex > 0) {
      int camelIndex = hyphenIndex + 1;
      String start = standardized.substring(0, hyphenIndex);
      String camelized = standardized.substring(camelIndex, camelIndex + 1).toUpperCase();
      String end = standardized.substring(camelIndex+1);

      standardized = start + camelized + end;
      hyphenIndex = standardized.lastIndexOf("_");
    }

    return standardized;
  }
}