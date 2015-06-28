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

package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages Op Modes
 *
 * Able to switch between op modes
 */
@SuppressWarnings("unused")
public class OpModeManager {

  public static final String DEFAULT_OP_MODE_NAME = "Stop Robot";
  public static final OpMode DEFAULT_OP_MODE = new DefaultOpMode();

  private Map<String, Class<?>> opModeClasses = new HashMap<String, Class<?>>();
  private Map<String, OpMode> opModeObjects = new HashMap<String, OpMode>();

  private String activeOpModeName = DEFAULT_OP_MODE_NAME;
  private OpMode activeOpMode = DEFAULT_OP_MODE;

  private HardwareMap hardwareMap = new HardwareMap();

  private boolean opModeSwapNeeded = false;

  public OpModeManager(HardwareMap hardwareMap, OpModeRegister register) {
    this.hardwareMap = hardwareMap;

    // register our default op mode first, that way the user can override it
    register(DEFAULT_OP_MODE_NAME, DefaultOpMode.class);

    // grab users op modes
    register.register(this);

    // switch to the default op mode
    switchOpModes(DEFAULT_OP_MODE_NAME);
  }

  public void setHardwareMap(HardwareMap hardwareMap) {
    this.hardwareMap = hardwareMap;
  }

  public HardwareMap getHardwareMap() {
    return hardwareMap;
  }

  public Set<String> getOpModes() {
    Set<String> opModelist = new HashSet<String>();
    opModelist.addAll(opModeClasses.keySet());
    opModelist.addAll(opModeObjects.keySet());
    return opModelist;
  }

  public String getActiveOpModeName() { return  activeOpModeName; }

  public OpMode getActiveOpMode() {
    return activeOpMode;
  }

  public void switchOpModes(String name) {
    activeOpModeName = name;
    opModeSwapNeeded = true;
  }

  public void startActiveOpMode() {
    activeOpMode.hardwareMap = hardwareMap;
    activeOpMode.start();
  }

  public void stopActiveOpMode() {
    activeOpMode.stop();
  }

  public void runActiveOpMode(Gamepad[] gamepads) {
    activeOpMode.time = activeOpMode.getRuntime();
    activeOpMode.gamepad1 = gamepads[0];
    activeOpMode.gamepad2 = gamepads[1];

    // run the start method, if needed
    if (opModeSwapNeeded) { performOpModeSwap(); }

    activeOpMode.loop();
  }

  public void logOpModes() {
    int opModeCount = opModeClasses.size() + opModeObjects.size();
    RobotLog.i("There are " + opModeCount + " Op Modes");
    for (Map.Entry<String, Class<?>> entry : opModeClasses.entrySet()) {
      RobotLog.i("   Op Mode: " + entry.getKey());
    }
    for (Map.Entry<String, OpMode> entry : opModeObjects.entrySet()) {
      RobotLog.i("   Op Mode: " + entry.getKey());
    }
  }

  public void register(String name, Class opMode) {
    if (isOpModeRegistered(name)) { throw new IllegalArgumentException("Cannot register the same op mode name twice"); }

    opModeClasses.put(name, opMode);
  }

  public void register(String name, OpMode opMode) {
    if (isOpModeRegistered(name)) { throw new IllegalArgumentException("Cannot register the same op mode name twice"); }

    opModeObjects.put(name, opMode);
  }

  private void performOpModeSwap() {
    RobotLog.i("Attempting to switch to op mode " + activeOpModeName);

    stopActiveOpMode();

    try {
      if (opModeObjects.containsKey(activeOpModeName)) {
        activeOpMode = opModeObjects.get(activeOpModeName);
      } else {
        activeOpMode = (OpMode) opModeClasses.get(activeOpModeName).newInstance();
      }
    } catch (InstantiationException e) {
      failedToSwapOpMode(e);
    } catch (IllegalAccessException e) {
      failedToSwapOpMode(e);
    }

    startActiveOpMode();

    opModeSwapNeeded = false;
  }

  private boolean isOpModeRegistered(String name) {
    return getOpModes().contains(name);
  }

  private void failedToSwapOpMode(Exception e) {
    RobotLog.e("Unable to start op mode " + activeOpModeName);
    RobotLog.logStacktrace(e);
    activeOpModeName = DEFAULT_OP_MODE_NAME;
    activeOpMode = DEFAULT_OP_MODE;
  }

  /*
   * default op mode
   */
  private static class DefaultOpMode extends OpMode {

    public DefaultOpMode() {
      // take no action
    }

    @Override
    public void start() {
      // take no action
      for (DcMotorController motorController : hardwareMap.dcMotorController){
        motorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
      }
    }

    @Override
    public void loop() {
      // power down the servos
      for (ServoController servoController : hardwareMap.servoController) {
        servoController.pwmDisable();
      }

      // power down the motors
      for (DcMotor dcMotor : hardwareMap.dcMotor) {
        dcMotor.setPowerFloat();
      }

      // turn of light sensors
      for (LightSensor light : hardwareMap.lightSensor) {
        light.enableLed(false);
      }

      telemetry.addData("Status", "Robot is stopped");
    }

    @Override
    public void stop() {
      // take no action
    }
  }
}
