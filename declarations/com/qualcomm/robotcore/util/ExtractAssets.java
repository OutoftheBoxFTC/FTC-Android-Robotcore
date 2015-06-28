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
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ExtractAssets {

  private static final String TAG = ExtractAssets.class.getSimpleName();

  public static ArrayList<String> ExtractToStorage(Context context, ArrayList<String> files,
      boolean useInternalStorage) throws IOException {

    // Get state of external storage

    if (!useInternalStorage) {
      String state = Environment.getExternalStorageState();

      if (!Environment.MEDIA_MOUNTED.equals(state)) {
        throw new IOException("External Storage not accessible");
      }
    }

    ArrayList<String> fileList = new ArrayList<String>();
    for (String ipFile : files) {
      ExtractAndCopy(context, ipFile, useInternalStorage, fileList);

      if (fileList != null) {
        Log.d(TAG, "got " + fileList.size() + " elements");

/*        for (String name : fileList) {
          Log.d(TAG, "Got " + name);
        }*/
      }
    }
    return fileList;

  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  private static ArrayList<String> ExtractAndCopy(Context context, String file,
      boolean useInternalStorage, ArrayList<String> retList) {

    Log.d(TAG, "Extracting assests for " + file);

    String[] fileList = null;

    AssetManager am = context.getAssets();

    try {
      fileList = am.list(file);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    //Log.d(TAG, "path: " + file + " assets " + Arrays.toString(fileList));

    InputStream ipStream = null;
    FileOutputStream outStream = null;

    if (fileList.length == 0) {
      // This is the last child, try to access the file
      try {
        ipStream = am.open(file);
        Log.d(TAG, "File: " + file + " opened for streaming");

        // If the name doesn't begin with a file separatorChar, add it
        if (!file.startsWith(File.separator)) {
          // Append file separator
          file = File.separator + file;
        }

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        File fileSystemPath = null;
        if(useInternalStorage)
        {
         fileSystemPath = context.getFilesDir();
        }else{

          fileSystemPath = context.getExternalFilesDir(null);
        }

        String path = fileSystemPath.getPath();
        String outFile = path.concat(file);

        // If the file was already processed, return
        // This happens if the user specifies duplicate entries in the input list

        if (retList != null && retList.contains(outFile)) {
          Log.e(TAG, "Ignoring Duplicate entry for " + outFile);
          return retList;
        }

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        // Get the directory
        int dirPathEnd = outFile.lastIndexOf(File.separatorChar);
        String dirName = outFile.substring(0, dirPathEnd);
        String fileName = outFile.substring(dirPathEnd, outFile.length());

        File fp = new File(dirName);

        if (fp.mkdirs()) {
          Log.d(TAG, "Dir created " + dirName);
        } else {
          //Log.d(TAG, "Dir exists " + dirName);
        }

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        File outFileHandle = new File(fp, fileName);
        outStream = new FileOutputStream(outFileHandle);

        if (outStream != null) {
          // Read from input stream and write to output stream
          byte[] readBuf = new byte[1024];

          int bytesRead = 0;

          while( ( bytesRead = ipStream.read(readBuf)) != -1) {
          outStream.write(readBuf, 0, bytesRead);
          }
        }
        outStream.close();

        if (retList != null) {
          retList.add(outFile);
        }

      } catch (IOException e) {
        Log.d(TAG, "File: " + file + " doesn't exist");
      } finally {
        if (ipStream != null) {
          try {
            ipStream.close();
          } catch (IOException e) {
            Log.d(TAG, "Unable to close in stream");
            e.printStackTrace();
          }
          if (outStream != null) {
            try {
              outStream.close();
            } catch (IOException e) {
              Log.d(TAG, "Unable to close out stream");
              e.printStackTrace();
            }
          }
        }
      }
      return retList;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // Not the last child, recurse

    String ipFilePath = file;

    // Check if the file ends with a path separator, if not, append and get the file name
    // Don't append fileSeparator if it's the top level dir

    if (!file.equals("") && !file.endsWith(File.separator)) {
      // Append the path separator
      ipFilePath = ipFilePath.concat(File.separator);
    }

    for (String ipFile : fileList) {
      String ipFileName = ipFilePath.concat(ipFile);
      ExtractAndCopy(context, ipFileName, useInternalStorage, retList);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    return retList;

  }
}
