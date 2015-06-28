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

import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadXMLFileHandler {
  List<ControllerConfiguration> deviceControllers;
  private Context context;

  private XmlPullParser parser;

  public ReadXMLFileHandler(Context context) {
    deviceControllers = new ArrayList<ControllerConfiguration>();
    this.context = context;
  }

  public List<ControllerConfiguration> getDeviceControllers() {
    return deviceControllers;
  }

  public List<ControllerConfiguration> parse(InputStream is) {
    XmlPullParserFactory factory;
    parser = null;
    try {
      factory = XmlPullParserFactory.newInstance();
      factory.setNamespaceAware(true);
      parser = factory.newPullParser();

      parser.setInput(is, null);

      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT) {
        String tagname = deform(parser.getName());
        if (eventType == XmlPullParser.START_TAG){
          if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER.toString())) {
            deviceControllers.add(handleMotorController());
          }
          if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER.toString())) {
            deviceControllers.add(handleServoController());
          }
          if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())){
            deviceControllers.add(handleLegacyModule());
          }
        }
        eventType = parser.next();
      } // hit end of document

    } catch (XmlPullParserException e) {
      RobotLog.w("XmlPullParserException");
      e.printStackTrace();
    } catch (IOException e) {
      RobotLog.w("IOException");
      e.printStackTrace();
    }

    return deviceControllers;
  }

  private ControllerConfiguration handleLegacyModule() throws IOException, XmlPullParserException{
    String name = parser.getAttributeValue(null, "name");
    String serialNumber = parser.getAttributeValue(null, "serialNumber");

    ArrayList<DeviceConfiguration> modules = new ArrayList<DeviceConfiguration>();
    int eventType = parser.next();
    String tagname = deform(parser.getName());

    while(eventType != XmlPullParser.END_DOCUMENT) { // we shouldn't reach the end of the document here anyway...
      if (eventType == XmlPullParser.END_TAG) {
        if (tagname == null) {
          // just an empty <DEVICE> </> closing tag
          continue;
        }
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.LEGACY_MODULE_CONTROLLER.toString())) {
          // end of loop...
          return new LegacyModuleControllerConfiguration(name, modules, new SerialNumber(serialNumber));
        }
      }
      if (eventType == XmlPullParser.START_TAG) {
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.COMPASS.toString()) ||
                tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.LIGHT_SENSOR.toString()) ||
                tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.IR_SEEKER.toString()) ||
                tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.ACCELEROMETER.toString()) ||
                tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.GYRO.toString()) ||
                tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.NOTHING.toString())){
          modules.add(handleDevice());
        }
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER.toString())){
          modules.add(handleMotorController());
        }
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER.toString())){
          modules.add(handleServoController());
        }
      }
      eventType = parser.next();
      tagname = deform(parser.getName());
    }

    return new LegacyModuleControllerConfiguration(name, modules, new SerialNumber(serialNumber));
  }

  private DeviceConfiguration handleDevice(){
    String tagname = deform(parser.getName());

    int port = Integer.parseInt(parser.getAttributeValue(null, "port"));
    DeviceConfiguration device = new DeviceConfiguration(port);
    device.setType(device.typeFromString(tagname));
    device.setName(parser.getAttributeValue(null, "name"));

    return device;
  }

  private ControllerConfiguration handleServoController() throws IOException, XmlPullParserException{
    String name = parser.getAttributeValue(null, "name");
    String serialNumber = parser.getAttributeValue(null, "serialNumber");
    String controllerPort = parser.getAttributeValue(null, "port");

    ArrayList<DeviceConfiguration> servos = new ArrayList<DeviceConfiguration>();
    int eventType = parser.next();
    String tagname = deform(parser.getName());

    while(eventType != XmlPullParser.END_DOCUMENT) { // we shouldn't reach the end of the document here anyway...
      if (eventType == XmlPullParser.END_TAG) {
        if (tagname == null) {
          // just an empty <SERVO> </> closing tag
          continue;
        }
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER.toString())) {
          // end of loop...
          ServoControllerConfiguration newController = new ServoControllerConfiguration(name, servos, new SerialNumber(serialNumber));
          newController.setPort(Integer.parseInt(controllerPort));
          return newController;
        }
      }
      if (eventType == XmlPullParser.START_TAG) {
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.SERVO.toString())) {
          int port = Integer.parseInt(parser.getAttributeValue(null, "port"));
          ServoConfiguration servo = new ServoConfiguration(DeviceConfiguration.ConfigurationType.SERVO.toString());
          servo.setPort(port);
          servo.setName(parser.getAttributeValue(null, "name"));
          servos.add(servo);
        }
      }
      eventType = parser.next();
      tagname = deform(parser.getName());
    }
    ServoControllerConfiguration newController = new ServoControllerConfiguration(name, servos, new SerialNumber(serialNumber));
    newController.setPort(Integer.parseInt(controllerPort));
    return newController;
  }

  private ControllerConfiguration handleMotorController() throws IOException, XmlPullParserException{
    String name = parser.getAttributeValue(null, "name");
    String serialNumber = parser.getAttributeValue(null, "serialNumber");
    String controllerPort = parser.getAttributeValue(null, "port");

    ArrayList<DeviceConfiguration> motors = new ArrayList<DeviceConfiguration>();
    int eventType = parser.next();
    String tagname = deform(parser.getName());

    while(eventType != XmlPullParser.END_DOCUMENT) { // we shouldn't reach the end of the document here anyway...
      if (eventType == XmlPullParser.END_TAG) {
        if (tagname == null) {
          // just an empty <MOTOR> </> closing tag
          continue;
        }
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER.toString())) {
          // end of loop...
          MotorControllerConfiguration newController = new MotorControllerConfiguration(name, motors, new SerialNumber(serialNumber));
          newController.setPort(Integer.parseInt(controllerPort));
          return newController;
        }
      }
      if (eventType == XmlPullParser.START_TAG) {
        if (tagname.equalsIgnoreCase(DeviceConfiguration.ConfigurationType.MOTOR.toString())) {
          int port = Integer.parseInt(parser.getAttributeValue(null, "port"));
          MotorConfiguration motor = new MotorConfiguration(DeviceConfiguration.ConfigurationType.MOTOR.toString());
          motor.setPort(port);
          motor.setName(parser.getAttributeValue(null, "name"));
          motors.add(motor);
        }
      }
      eventType = parser.next();
      tagname = deform(parser.getName());
    }

    MotorControllerConfiguration newController = new MotorControllerConfiguration(name, motors, new SerialNumber(serialNumber));
    newController.setPort(Integer.parseInt(controllerPort));
    return newController;
  }

  private String deform(String standardized){
    //RobotLog.e("string: " + standardized);
    if (standardized == null) { return null; }
    if (standardized.equalsIgnoreCase("MotorController")){
      return DeviceConfiguration.ConfigurationType.MOTOR_CONTROLLER.toString();
    }
    if (standardized.equalsIgnoreCase("ServoController")){
      return DeviceConfiguration.ConfigurationType.SERVO_CONTROLLER.toString();
    }
    if (standardized.equalsIgnoreCase("LegacyModuleController")){
      return DeviceConfiguration.ConfigurationType.LEGACY_MODULE_CONTROLLER.toString();
    }
    if (standardized.equalsIgnoreCase("irSeeker")){
      return DeviceConfiguration.ConfigurationType.IR_SEEKER.toString();
    }
    if (standardized.equalsIgnoreCase("lightSensor")){
      return DeviceConfiguration.ConfigurationType.LIGHT_SENSOR.toString();
    }
    else {
      return standardized;
    }
  }

}