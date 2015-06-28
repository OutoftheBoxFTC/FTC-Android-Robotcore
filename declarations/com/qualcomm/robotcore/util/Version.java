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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Version {
  private static final String TAG = "Version";

  /** BUILD_VERSION for library, populated via custom_rules.xml */
  public static final String BUILD_VERSION = "buildtest";
  public static final String LABEL_VERSION = "1.0";
  // This is the default string in case no version was explicitly supplied
  private static String sBuildLabel = "Unspecified";
  private static int sBuildLabelId = -1;

  /** Obtain the label */
  public static String getBuildLabel(Context c) {
    // This is an expensive call, only do once
    if (sBuildLabelId == -1) {
      sBuildLabelId = c.getResources().getIdentifier("buildLabel", "string", c.getPackageName());

      // If found, id will be greater than 0
      if (sBuildLabelId > 0) {
        // Overwrite the default version string with the obtained one
        sBuildLabel = c.getResources().getString(sBuildLabelId);
      }
      
      // Append build timestamp to the version string
      sBuildLabel += ", Timestamp: " + getAppBuildTime(c);
    }

    return sBuildLabel;
  }

  /** Obtain the app build timestamp */
  private static String getAppBuildTime(Context c) {

    // Extract the timestamp from the application
    long appBuildTimestamp = 0;
    try {
      final ApplicationInfo appInfo =
          c.getPackageManager().getApplicationInfo(c.getPackageName(), 0);
      final ZipFile zipFile = new ZipFile(appInfo.sourceDir);
      final ZipEntry zipEntry = zipFile.getEntry("META-INF/MANIFEST.MF");
      appBuildTimestamp = zipEntry.getTime();
      zipFile.close();
    } catch (final Exception e) {
      Log.e(TAG, "Failed to obtain application timestamp!");
      e.printStackTrace();
    }

    // Convert timestamp into formatted calendar date and time string
    final Date date = new Date(appBuildTimestamp);
    final SimpleDateFormat formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    final Calendar cal = new GregorianCalendar();
    cal.setTime(date);

    return formattedTimestamp.format(date);
  }
}
