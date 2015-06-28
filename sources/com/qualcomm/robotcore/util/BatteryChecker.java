/*
 * Copyright (c) 2015 Qualcomm Technologies Inc
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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;

public class BatteryChecker {

  public interface BatteryWatcher{
    // called whenever the battery watcher should receive the latest
    // battery level, reported as a percent.
    public abstract void updateBatteryLevel(float percent);
  }
  private Context context;
  private long delay;
  private BatteryWatcher watcher;
  protected Handler batteryHandler;

  public BatteryChecker(Context context, BatteryWatcher watcher, long delay){
    this.context = context;
    this.watcher = watcher;
    this.delay = delay;
    batteryHandler = new Handler();
  }


  // There are no guarantees about how frequently the hardware will broadcast the battery level
  // so it's more reliable to do our own polling. I register the receiver with a null receiver
  // since I don't care about actually receiving the broadcast. registerReceiver() gives me
  // the intent with all the info I want. Then I do some processing, and I'm done.

  Runnable batteryLevelChecker = new Runnable() {
    @Override
    public void run() {
      IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
      Intent intent = context.registerReceiver(null, batteryLevelFilter);

      int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
      int percent = -1;
      if (currentLevel >= 0 && scale > 0) {
        percent = (currentLevel * 100) / scale;
      }

      watcher.updateBatteryLevel(percent);
      RobotLog.i("Battery Level Remaining: " + percent);

      batteryHandler.postDelayed(batteryLevelChecker, delay);
    }
  };

  public void startBatteryMonitoring(){
    batteryHandler.postDelayed(batteryLevelChecker, 0);
  }

  public void endBatteryMonitoring(){
    batteryHandler.removeCallbacks(batteryLevelChecker);
  }
}
