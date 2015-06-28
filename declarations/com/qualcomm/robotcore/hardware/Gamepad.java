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

import android.annotation.TargetApi;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Monitor a hardware gamepad.
 * <p>
 * The buttons, analog sticks, and triggers are represented a public
 * member variables that can be read from or written to directly.
 * <p>
 * Analog sticks are represented as floats that range from -1.0 to +1.0. They will be 0.0 while at
 * rest. The horizontal axis is labeled x, and the vertical axis is labeled y.
 * <p>
 * Triggers are represented as floats that range from 0.0 to 1.0. They will be at 0.0 while at
 * rest.
 * <p>
 * Buttons are boolean values. They will be true if the button is pressed, otherwise they will be
 * false.
 * <p>
 * The dpad is represented as 4 buttons, dpad_up, dpad_down, dpad_left, and dpad_right
 */
public class Gamepad implements RobocolParsable {

  /**
   * A gamepad with an ID equal to ID_UNASSOCIATED has not been associated with any device.
   */
  public static final int ID_UNASSOCIATED = -1;

  /**
   * Optional callback interface for monitoring changes due to MotionEvents and KeyEvents.
   *
   * This interface can be used to notify you if the gamepad changes due to either a KeyEvent or a
   * MotionEvent. It does not notify you if the gamepad changes for other reasons.
   */
  public interface GamepadCallback {

    /**
     * This method will be called whenever the gamepad state has changed due to either a KeyEvent
     * or a MotionEvent.
     * @param gamepad device which state has changed
     */
    public void gamepadChanged(Gamepad gamepad);
  }

  /**
   * left analog stick horizontal axis
   */
  public float left_stick_x = 0f;

  /**
   * left analog stick vertical axis
   */
  public float left_stick_y = 0f;

  /**
   * right analog stick horizontal axis
   */
  public float right_stick_x = 0f;

  /**
   * right analog stick vertical axis
   */
  public float right_stick_y = 0f;

  /**
   * dpad up
   */
  public boolean dpad_up = false;

  /**
   * dpad down
   */
  public boolean dpad_down = false;

  /**
   * dpad left
   */
  public boolean dpad_left = false;

  /**
   * dpad right
   */
  public boolean dpad_right = false;

  /**
   * button a
   */
  public boolean a = false;

  /**
   * button b
   */
  public boolean b = false;

  /**
   * button x
   */
  public boolean x = false;

  /**
   * button y
   */
  public boolean y = false;

  /**
   * button guide - often the large button in the middle of the controller. The OS may
   * capture this button before it is sent to the app; in which case you'll never
   * receive it.
   */
  public boolean guide = false;

  /**
   * button start
   */
  public boolean start = false;

  /**
   * button back
   */
  public boolean back = false;

  /**
   * button left bumper
   */
  public boolean left_bumper = false;

  /**
   * button right bumper
   */
  public boolean right_bumper = false;

  /**
   * left trigger
   */
  public float left_trigger = 0f;

  /**
   * right trigger
   */
  public float right_trigger = 0f;

  /**
   * Which user is this gamepad used by
   */
  public byte user = ID_UNASSOCIATED;

  /**
   * ID assigned to this gamepad by the OS. This value can change each time the device is plugged in
   */
  public int id = ID_UNASSOCIATED;

  /**
   * Relative timestamp of the last time an event was detected
   */
  public long timestamp = 0;

  /**
   * DPAD button will be considered pressed when the movement crosses this
   * threshold
   */
  protected float dpadThreshold = 0.2f;

  /**
   * If the motion value is less than the threshold, the controller will be
   * considered at rest
   */
  protected float joystickDeadzone = 0.2f; // very high, since we don't know the device type

  // private static values used for packaging the gamepad state into a byte array
  private static final short PAYLOAD_SIZE = 42;
  private static final short BUFFER_SIZE = PAYLOAD_SIZE + RobocolParsable.HEADER_LENGTH;

  private static final byte ROBOCOL_VERSION = 2;

  private static final float MAX_MOTION_RANGE = 1.0f;

  private final GamepadCallback callback;

  private static Set<Integer> gameControllerDeviceIdCache = new HashSet<Integer>();

  // Set of devices to consume input events from. If null, inputs from all detected devices will be used.
  private static Set<DeviceId> deviceWhitelist = null;

  /**
   * Container class to identify a vendor/product ID combination.
   *
   * Reusing Map.Entry which provides appropriate .equals/.hashCode
   */
  private static class DeviceId extends java.util.AbstractMap.SimpleEntry<Integer, Integer> {
    private static final long serialVersionUID = -6429575391769944899L;

    public DeviceId(int vendorId, int productId) {
      super(vendorId, productId);
    }

    @SuppressWarnings("unused")
    public int getVendorId() {
      return getKey();
    }

    @SuppressWarnings("unused")
    public int getProductId() {
      return getValue();
    }

  }

  public Gamepad() {
    this(null);
  }

  public Gamepad(GamepadCallback callback) {
    this.callback = callback;
  }

  /**
   * Set the joystick deadzone. Must be between 0 and 1.
   * @param deadzone
   */
  public void setJoystickDeadzone(float deadzone) {
    if (deadzone < 0 || deadzone > MAX_MOTION_RANGE) {
      throw new IllegalArgumentException("deadzone cannot be greater than max joystick value");
    }

    joystickDeadzone = deadzone;
  }

  /**
   * Update the gamepad based on a MotionEvent
   * @param event
   */
  public void update(android.view.MotionEvent event) {

    id = event.getDeviceId();
    timestamp = event.getEventTime();

    left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_X));
    left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Y));
    right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Z));
    right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RZ));
    left_trigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
    right_trigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
    dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > dpadThreshold;
    dpad_up = event.getAxisValue(MotionEvent.AXIS_HAT_Y) < -dpadThreshold;
    dpad_right = event.getAxisValue(MotionEvent.AXIS_HAT_X) > dpadThreshold;
    dpad_left = event.getAxisValue(MotionEvent.AXIS_HAT_X) < -dpadThreshold;

    callCallback();
  }

  /**
   * Update the gamepad based on a KeyEvent
   * @param event
   */
  public void update(android.view.KeyEvent event) {

    id = event.getDeviceId();
    timestamp = event.getEventTime();

    int key = event.getKeyCode();
    if      (key == KeyEvent.KEYCODE_DPAD_UP) dpad_up = pressed(event);
    else if (key == KeyEvent.KEYCODE_DPAD_DOWN) dpad_down = pressed(event);
    else if (key == KeyEvent.KEYCODE_DPAD_RIGHT) dpad_right = pressed(event);
    else if (key == KeyEvent.KEYCODE_DPAD_LEFT) dpad_left = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_A) a = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_B) b = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_X) x = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_Y) y = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_MODE) guide = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_START) start = pressed(event);
    else if (key == KeyEvent.KEYCODE_BACK) back = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_R1) right_bumper = pressed(event);
    else if (key == KeyEvent.KEYCODE_BUTTON_L1) left_bumper = pressed(event);

    callCallback();
  }

  @Override
  public MsgType getRobocolMsgType() {
    return RobocolParsable.MsgType.GAMEPAD;
  }

  @Override
  public byte[] toByteArray() throws RobotCoreException {

    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    try {
      int buttons = 0;

      buffer.put(getRobocolMsgType().asByte());
      buffer.putShort(PAYLOAD_SIZE);
      buffer.put(ROBOCOL_VERSION);
      buffer.putInt(id);
      buffer.putLong(timestamp).array();
      buffer.putFloat(left_stick_x).array();
      buffer.putFloat(left_stick_y).array();
      buffer.putFloat(right_stick_x).array();
      buffer.putFloat(right_stick_y).array();
      buffer.putFloat(left_trigger).array();
      buffer.putFloat(right_trigger).array();

      buttons = (buttons << 1) + (dpad_up ? 1 : 0);
      buttons = (buttons << 1) + (dpad_down ? 1 : 0);
      buttons = (buttons << 1) + (dpad_left ? 1 : 0);
      buttons = (buttons << 1) + (dpad_right ? 1 : 0);
      buttons = (buttons << 1) + (a ? 1 : 0);
      buttons = (buttons << 1) + (b ? 1 : 0);
      buttons = (buttons << 1) + (x ? 1 : 0);
      buttons = (buttons << 1) + (y ? 1 : 0);
      buttons = (buttons << 1) + (guide ? 1 : 0);
      buttons = (buttons << 1) + (start ? 1 : 0);
      buttons = (buttons << 1) + (back ? 1 : 0);
      buttons = (buttons << 1) + (left_bumper ? 1 : 0);
      buttons = (buttons << 1) + (right_bumper ? 1 : 0);
      buffer.putInt(buttons);

      buffer.put(user);
    } catch (BufferOverflowException e) {
      RobotLog.logStacktrace(e);
    }

    return buffer.array();
  }

  @Override
  public void fromByteArray(byte[] byteArray) throws RobotCoreException {
    if (byteArray.length < BUFFER_SIZE) {
      throw new RobotCoreException("Expected buffer of at least " + BUFFER_SIZE + " bytes, received " + byteArray.length);
    }

    ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);

    int buttons = 0;

    byte version = byteBuffer.get();

    // extract version 1 values
    if (version >= 1) {
      id = byteBuffer.getInt();
      timestamp = byteBuffer.getLong();
      left_stick_x = byteBuffer.getFloat();
      left_stick_y = byteBuffer.getFloat();
      right_stick_x = byteBuffer.getFloat();
      right_stick_y = byteBuffer.getFloat();
      left_trigger = byteBuffer.getFloat();
      right_trigger = byteBuffer.getFloat();

      buttons = byteBuffer.getInt();
      dpad_up      = (buttons & 0x1000) != 0 ? true : false;
      dpad_down    = (buttons & 0x0800) != 0 ? true : false;
      dpad_left    = (buttons & 0x0400) != 0 ? true : false;
      dpad_right   = (buttons & 0x0200) != 0 ? true : false;
      a            = (buttons & 0x0100) != 0 ? true : false;
      b            = (buttons & 0x0080) != 0 ? true : false;
      x            = (buttons & 0x0040) != 0 ? true : false;
      y            = (buttons & 0x0020) != 0 ? true : false;
      guide        = (buttons & 0x0010) != 0 ? true : false;
      start        = (buttons & 0x0008) != 0 ? true : false;
      back         = (buttons & 0x0004) != 0 ? true : false;
      left_bumper  = (buttons & 0x0002) != 0 ? true : false;
      right_bumper = (buttons & 0x0001) != 0 ? true : false;
    }

    // extract version 2 values
    if (version >= 2) {
      user = byteBuffer.get();
    }

    callCallback();
  }

  /**
   * Are all analog sticks and triggers in their rest position?
   * @return true if all analog sticks and triggers are at rest; otherwise false
   */
  public boolean atRest() {
    return (
        left_stick_x == 0f && left_stick_y == 0f &&
        right_stick_y == 0f && right_stick_y == 0f &&
        left_trigger == 0f && right_trigger == 0f);
  }

  /**
   * Get the type of gamepad as a String. This method defaults to "Standard".
   * @return gamepad type
   */
  public String type() {
    return "Standard";
  }

  /**
   * Display a summary of this gamepad, including the state of all buttons, analog sticks, and triggers
   * @return a summary
   */
  @Override
  public String toString() {
    String buttons = new String();
    if (dpad_up) buttons += "dpad_up ";
    if (dpad_down) buttons += "dpad_down ";
    if (dpad_left) buttons += "dpad_left ";
    if (dpad_right) buttons += "dpad_right ";
    if (a) buttons += "a ";
    if (b) buttons += "b ";
    if (x) buttons += "x ";
    if (y) buttons += "y ";
    if (guide) buttons += "guide ";
    if (start) buttons += "start ";
    if (back) buttons += "back ";
    if (left_bumper) buttons += "left_bumper ";
    if (right_bumper) buttons += "right_bumper ";

    return String.format("ID: %2d user: %2d lx: % 1.2f ly: % 1.2f rx: % 1.2f ry: % 1.2f lt: %1.2f rt: %1.2f %s",
        id, user, left_stick_x, left_stick_y,
        right_stick_x, right_stick_y, left_trigger, right_trigger, buttons);
  }

  // clean values
  // remove values larger than max
  // apply deadzone logic
  protected float cleanMotionValues(float number) {

    // apply deadzone
    if (number < joystickDeadzone && number > -joystickDeadzone) return 0.0f;

    // apply trim
    if (number >  MAX_MOTION_RANGE) return  MAX_MOTION_RANGE;
    if (number < -MAX_MOTION_RANGE) return -MAX_MOTION_RANGE;

    // scale values to be between deadzone and trim
    if (number < 0) {
      Range.scale(number, joystickDeadzone, MAX_MOTION_RANGE, 0, MAX_MOTION_RANGE);
    }
    if (number > 0) {
      Range.scale(number, -joystickDeadzone, -MAX_MOTION_RANGE, 0, -MAX_MOTION_RANGE);
    }


    return number;
  }

  protected boolean pressed(android.view.KeyEvent event) {
    return event.getAction() == KeyEvent.ACTION_DOWN;
  }

  protected void callCallback() {
    if (callback != null) callback.gamepadChanged(this);
  }

  /**
   * Add a whitelist filter for a specific device vendor/product ID.
   * <p>
   * This adds a whitelist to the gamepad detection method. If a device has been added to the
   * whitelist, then only devices that match the given vendor ID and product ID will be considered
   * gamepads. This method can be called multiple times to add multiple devices to the whitelist.
   * <p>
   * If no whitelist entries have been added, then the default OS detection methods will be used.
   * @param vendorId the vendor ID
   * @param productId the product ID
   */
  public static void enableWhitelistFilter(int vendorId, int productId) {
    if (deviceWhitelist == null) {
      deviceWhitelist = new HashSet<DeviceId>();
    }
    deviceWhitelist.add(new DeviceId(vendorId, productId));
  }

  /**
   * Clear the device whitelist filter.
   */
  public static void clearWhitelistFilter() {
    deviceWhitelist = null;
  }

  /**
   * Does this device ID belong to a gamepad device?
   * @param deviceId
   * @return true, if gamepad device; false otherwise
   */
  @TargetApi(19)
  public static synchronized boolean isGamepadDevice(int deviceId) {

    // check the cache
    if (gameControllerDeviceIdCache.contains(deviceId))
      return true;

    // update game controllers cache, since a new controller might have been plugged in
    gameControllerDeviceIdCache = new HashSet<Integer>();
    int[] deviceIds = InputDevice.getDeviceIds();
    for (int id : deviceIds) {
      InputDevice device = InputDevice.getDevice(id);

      int source = device.getSources();
      if ((source & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
          || (source & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {

        if (android.os.Build.VERSION.SDK_INT >= 19) {
          // null mDeviceWhitelist means all devices are valid
          // non-null mDeviceWhitelist means only use devices in mDeviceWhitelist
          if (deviceWhitelist == null
              || deviceWhitelist.contains(new DeviceId(device.getVendorId(), device.getProductId()))) {
            gameControllerDeviceIdCache.add(id);
          }
        } else {
          gameControllerDeviceIdCache.add(id);
        }
      }
    }

    // check updated cache
    if (gameControllerDeviceIdCache.contains(deviceId))
      return true;

    // this is not an event from a game pad
    return false;
  }
}
