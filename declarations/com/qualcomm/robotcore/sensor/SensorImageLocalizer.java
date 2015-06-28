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

import android.util.Log;

import com.qualcomm.robotcore.util.MatrixD;
import com.qualcomm.robotcore.util.Pose;
import com.qualcomm.robotcore.util.PoseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SensorImageLocalizer extends SensorBase<Pose> implements
SensorListener<List<TrackedTargetInfo>> {

  private final boolean ENABLE_DEBUG_LOGS = false;
  private final String TAG = "SensorImageLocalizer";
  private final Map<String, TargetInfo> mTargetMap;
  private Pose mRobotwrtCamera;

  private class TrackedTargetData{

    //Time in seconds after which the count will be reset
    public final static int RESET_COUNT_TIME_LIMIT = 120;

    //Time in seconds to switch between two targets
    public final static int TARGET_SWITCH_INTERVAL = 10;
    public long lastTimeReported;
    public long lastTimeTracked;
    @SuppressWarnings("unused")
    public int count;
    public String id;
    @SuppressWarnings("unused")
    public double confidence;
  }

  private final HashMap<String,TrackedTargetData> mTargetData = new HashMap<String, TrackedTargetData>();
  private TrackedTargetData mLastTargetReported;

  public SensorImageLocalizer(List<SensorListener<Pose>> l) {
    super(l);
    mTargetMap = new HashMap<String, TargetInfo>();
  }

  @Override
  public boolean initialize() {
    return true;
  }

  @Override
  public boolean shutdown() {
    return true;
  }

  @Override
  public boolean resume() {
    return true;
  }

  @Override
  public boolean pause() {
    return true;
  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public void AddListener(SensorListener<Pose> l) {
    synchronized (mListeners) {
      if (!mListeners.contains(l)) {
        mListeners.add(l);
      }
    }
  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public void RemoveListener(SensorListener<Pose> l) {
    synchronized (mListeners) {
      if (mListeners.contains(l)) {
        mListeners.remove(l);
      }
    }
  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  /**
   * Add a target to the 'world map'.
   *
   * Only targets added to this map will be used by SensorImageLocalizer to compute camera pose.
   *
   * @param targetName unique identifier for target, must match target name in TargetInfo from
   *        whatever image target tracker sensor provides input to SensorImageLocalizer
   *
   * @param xTrans describes target position and rotation in world coordinates
   *
   * @param yTrans describes target position and rotation in world coordinates
   *
   * @param zTrans describes target position and rotation in world coordinates
   *
   * @return true if the targetName was not previously mapped and the mapping was added
   *         successfully, false otherwise
   */

  // This is the translation of the corner of the poster/box, not the center of the target
  // xTrans: Translation along the x-axis
  // yTrans: Translation along the y-axis
  // angle: Angle along +z, note this is in the real world coordinate frame

  // Note the distances and angles are in real world coordinates
  public boolean addTargetReference(String targetName, double xTrans, double yTrans, double zTrans,
      double angle, double longSideTransFromCenterToVertex, double shortSideTransFromCenterToVertex) {

    if (targetName == null){
      throw new IllegalArgumentException("Null targetInfoWorldRef");
    }

    if (mTargetMap.containsKey(targetName)) {
      return false;
    } else {

      //note the image localizer works with the image provided by the camera, it's in the "camera"
      //coordinate frame convention, where the robot moves in the xz plane, hence the pose matrix
      //is constructed by rotating along the y axis

      MatrixD rotMat = Pose.makeRotationY( Math.toRadians(angle));

      //Create a 3,4 matrix

      MatrixD poseMat = new MatrixD(3, 4);

      //Set the rotation matrix
      poseMat.setSubmatrix(rotMat, 3, 3, 0, 0);

      //Set the translation matrix

      //Note the order, this is converting from the world to camera coordinate frame

      // camera +x = world +y
      // camera +y = world +z
      // camera +z = world +x

      poseMat.data()[0][3] = yTrans;
      poseMat.data()[1][3] = zTrans;
      poseMat.data()[2][3] = xTrans;

      Pose targetPose = new Pose(poseMat);

      Log.d(TAG, "Target Pose \n" + poseMat);

      //Create the targetInfo, this incorporates the poster size
      TargetSize targetSize = new TargetSize(targetName, longSideTransFromCenterToVertex,
          shortSideTransFromCenterToVertex);
      TargetInfo targetInfo = new TargetInfo(targetName, targetPose, targetSize);

      mTargetMap.put(targetName, targetInfo);

      return true;
    }
  }


  //Adds the robot reference w.r.t the camera

  //Initially the assumption is just a translation and rotation around the robot z axis
  public boolean addRobotToCameraRef( double length, double width, double height, double angle){

    MatrixD rotMat = new MatrixD(3,3);

    //Note the change in angle, the angles are w.r.t the axis going up, account for that

    rotMat = Pose.makeRotationY(-angle);

    MatrixD poseMat = new MatrixD(3,4);

    //Set the rotation
    poseMat.setSubmatrix(rotMat, 3, 3, 0, 0);

    //Note the sign flip for Y axis

    poseMat.data()[0][3] = width;
    poseMat.data()[1][3] = -height;
    poseMat.data()[2][3] = length;
    mRobotwrtCamera = new Pose(poseMat);
    return true;


  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public boolean removeTargetReference(String targetName) {
    if (targetName == null) {
      throw new IllegalArgumentException("Null targetName");
    }

    if (mTargetMap.containsKey(targetName)) {
      mTargetMap.remove(targetName);
      return true;
    } else {
      return false;
    }
  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  private void printAngles(MatrixD rotComponentZ, String text) {

    //Check the matrix is of expected size

    if(rotComponentZ.numRows() != 3 || rotComponentZ.numCols()!=1){
      throw new IllegalArgumentException("Invalid matrix dimension ( " + rotComponentZ.numRows() +
          ", " + rotComponentZ.numCols() + " )");
    }

    double headingX = Math.atan2(rotComponentZ.data()[2][0],rotComponentZ.data()[0][0]);
    headingX = Math.toDegrees(headingX);

    double headingZ = Math.atan2(rotComponentZ.data()[0][0],rotComponentZ.data()[2][0]);
    headingZ = Math.toDegrees(headingZ);

    double length = rotComponentZ.length();
    double headingUp =  Math.asin(rotComponentZ.data()[1][0]/length);
    headingUp = Math.toDegrees(headingUp);

    Log.d(TAG,text + String.format(": x %8.4f z %8.4f up %8.4f",headingX,headingZ,headingUp));
    return;
  }

  private boolean checkForValidTarget(TrackedTargetInfo target) {
    long currTime = System.currentTimeMillis()/1000;

    //Check if the map entry exists
    TrackedTargetData entry = null;

    if( mTargetData.containsKey(target.mTargetInfo.mTargetName)){
      entry = mTargetData.get(target.mTargetInfo.mTargetName);
      entry.lastTimeTracked = target.mTimeTracked;
      entry.confidence = target.mConfidence;
      if( (currTime - entry.lastTimeTracked) > TrackedTargetData.RESET_COUNT_TIME_LIMIT){
        entry.count = 1;
      }else{
        entry.count++;
      }
    }else{
      entry = new TrackedTargetData();
      entry.confidence = target.mConfidence;
      entry.id = target.mTargetInfo.mTargetName;
      entry.lastTimeTracked = target.mTimeTracked;
      entry.count = 1;
      mTargetData.put(target.mTargetInfo.mTargetName,entry);
    }
      if( (mLastTargetReported != null) &&
          (mLastTargetReported.id != entry.id) &&
          (currTime - mLastTargetReported.lastTimeReported) < TrackedTargetData.TARGET_SWITCH_INTERVAL){
        Log.d(TAG,"Ignoring target " + target.mTargetInfo.mTargetName + " Time diff " +
            (currTime - mLastTargetReported.lastTimeReported));
        return false;
    }

    return true;
  }

  /**
   * compute the camera pose in world coordinates, given a target pose in camera coordinates (from
   * sensor) AND target pose in world coordinates (must have been previously added to world map via
   * addTargetReference)
   */
  @Override
  public void onUpdate(List<TrackedTargetInfo> targetPoses) {

    Log.d(TAG, "SensorImageLocalizer onUpdate");

    if ((targetPoses == null) || (targetPoses.size() < 1)) {
      Log.d(TAG, "SensorImageLocalizer onUpdate NULL");
      update(null);
      return;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //Check if the target information exists

    boolean targetFound = false;

    double bestConfidence = Double.MIN_VALUE;
    long currTime = System.currentTimeMillis()/1000;
    TrackedTargetInfo selectedTarget = null;
    TrackedTargetData selectedTargetData = null;

    for(TrackedTargetInfo target: targetPoses){
      if(mTargetMap.containsKey(target.mTargetInfo.mTargetName)){

        //See if this target can be used
        boolean targetSelected = checkForValidTarget(target);

        if(targetSelected && (target.mConfidence > bestConfidence)){
          selectedTargetData = mTargetData.get(target.mTargetInfo.mTargetName);
          selectedTarget = target;
          bestConfidence = target.mConfidence;
          targetFound = true;
          Log.d(TAG,"Potential target " + target.mTargetInfo.mTargetName + " Confidence " +
          target.mConfidence);
        }else{
          Log.d(TAG,"Ignoring target " +  target.mTargetInfo.mTargetName + " Confidence " +
              target.mConfidence);
        }
      }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    if(!targetFound){
      update(null);
      return;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //Get the target info
    TargetInfo mapRef = mTargetMap.get(selectedTarget.mTargetInfo.mTargetName);

    //Set the last reported time

    selectedTargetData.lastTimeReported = currTime;
    mLastTargetReported = selectedTargetData;
    Log.d(TAG,"Selected target " + selectedTarget.mTargetInfo.mTargetName + " time " + currTime);

    //Step 1:

    //Express the camera to robot transformation w.r.t camera
    //mRobotwrtCamera has that information

    //Get the robot w.r.t target
    MatrixD robotWrtCamera=null;

    if(mRobotwrtCamera!=null){
      robotWrtCamera = mRobotwrtCamera.poseMatrix.submatrix(3, 3, 0, 0);

    }

    //Get the target w.r.t camera
    MatrixD targetWrtCamera = selectedTarget.mTargetInfo.mTargetPose.poseMatrix.submatrix(3, 3, 0, 0);

    //Get the camera w.r.t target
    MatrixD cameraWrtTarget = targetWrtCamera.transpose();

    //Get the target w.r.t world
    MatrixD targetWrtWorld = mapRef.mTargetPose.poseMatrix.submatrix(3, 3, 0, 0);

    //Change from "camera coordinate frame" to "target coordinate frame"

    //Rotate the coordinate frame, NOT the vector by 90 degrees clockwise.
    //While constructing the matrix, this is equivalent to rotating the vector by 90 degrees
    //counter-clockwise (-90)

    //Followed by 90 degrees clockwise rotation around x

    //The order of matrix multiplication is x followed by y

    MatrixD transformAxes = Pose.makeRotationX( Math.toRadians(90));
    transformAxes = transformAxes.times(Pose.makeRotationY( Math.toRadians(90)));

    //Heading = transformAxes * target w.r.t world * camera w.r.t target * robot w.r.t camera
    MatrixD robotHeading = transformAxes.times(targetWrtWorld).times(cameraWrtTarget);

    if(robotWrtCamera != null){
      robotHeading = robotHeading.times(robotWrtCamera);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    if(ENABLE_DEBUG_LOGS){

      MatrixD robotWrtTarget = null;
      if(robotWrtCamera != null){
        robotWrtTarget = cameraWrtTarget.times(robotWrtCamera);
        printAngles( robotWrtCamera.submatrix(3, 1, 0, 2), "ROBOT_HEADING_CAMERA" );
      }else{
        robotWrtTarget = cameraWrtTarget.times(1);
      }

      MatrixD robotWrtWorld = targetWrtWorld.times(robotWrtTarget);
      MatrixD robotHeadingDebug = transformAxes.times(robotWrtWorld);
      printAngles( robotWrtTarget.submatrix(3, 1, 0, 2), "ROBOT_TARGET_HEADING" );
      printAngles( targetWrtWorld.submatrix(3, 1, 0, 2), "TARGET_WORLD_HEADING" );
      printAngles( robotWrtWorld.submatrix(3, 1, 0, 2), "ROBOT_WORLD_HEADING" );

      double[] angles = PoseUtils.getAnglesAroundZ(robotHeadingDebug);
      Log.d(TAG,String.format("ROBOT_HEADING_DEBUG: x %8.4f z %8.4f up %8.4f",angles[0],angles[1],angles[2]));

      angles = PoseUtils.getAnglesAroundZ(robotHeading);
      Log.d(TAG,String.format("ROBOT_HEADING: x %8.4f z %8.4f up %8.4f",angles[0],angles[1],angles[2]));
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


    //Get the translation portion

    // We have a vector from camera to target
    // express that in terms of target
    // we have the target center to target vertex transformation
    // we have the target vertex to world transformation

    //Target to target vertex

    MatrixD targetToVertex = new MatrixD(3, 1);

    targetToVertex.data()[0][0] = mapRef.mTargetSize.mLongSide;
    targetToVertex.data()[1][0] = mapRef.mTargetSize.mShortSide;
    targetToVertex.data()[2][0] = 0;

    //Camera to target, expressed in target coordinates

    MatrixD tPose = selectedTarget.mTargetInfo.mTargetPose.getTranslationMatrix();

    MatrixD transVec = cameraWrtTarget.times(tPose);

    if(ENABLE_DEBUG_LOGS){
      Log.d(TAG,String.format ("CAMERA_TARGET_TRANS_BEFORE_TPOSE: x %8.4f y %8.4f z %8.4f",
          tPose.data()[0][0], tPose.data()[1][0], tPose.data()[2][0]));

      Log.d(TAG,String.format ("CAMERA_TARGET_TRANS: x %8.4f y %8.4f z %8.4f",
          transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));
    }

    MatrixD tRobotWrtCamera = new MatrixD(3, 1);

    if(mRobotwrtCamera!=null){
      tRobotWrtCamera = mRobotwrtCamera.getTranslationMatrix();
    }

    MatrixD transVecRobotWrtTarget = cameraWrtTarget.times(tRobotWrtCamera);

    if(ENABLE_DEBUG_LOGS){
      Log.d(TAG,String.format("ROBOT_CAMERA_TRANS: x %8.4f y %8.4f z %8.4f",
          transVecRobotWrtTarget.data()[0][0], transVecRobotWrtTarget.data()[1][0], transVecRobotWrtTarget.data()[2][0]));
    }

    // RC + CV = RV in target coordinates
    transVec = transVec.add(transVecRobotWrtTarget);

    if(ENABLE_DEBUG_LOGS){
      Log.d(TAG,String.format("ROBOT_TARGET_TRANS: x %8.4f y %8.4f z %8.4f",
          transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));
    }

    //Add the vector to the target vertex
    transVec = transVec.add(targetToVertex);

    if(ENABLE_DEBUG_LOGS){
      Log.d(TAG,String.format("ROBOT_VERTEX_TRANS: x %8.4f y %8.4f z %8.4f",
          transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));
    }

    //At this point we have the vector from robot to the target vertex expressed in
    //target coordinates

    //Express the vector in world coordinates

    transVec = targetWrtWorld.times(transVec);

    if(ENABLE_DEBUG_LOGS){
      Log.d(TAG,String.format("WORLD_TRANS: x %8.4f y %8.4f z %8.4f",
          transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));
    }

    //we have the robot to target vertex in world coordinates, add the world to target vector
    //target to target vertex

    MatrixD transVecTargetWrtWorld = mapRef.mTargetPose.getTranslationMatrix();

    transVec = transVecTargetWrtWorld.subtract(transVec);

    if(ENABLE_DEBUG_LOGS){

      Log.d(TAG,String.format("FINAL_TRANS: x %8.4f y %8.4f z %8.4f",
          transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));
    }

    //Robot translation

    transVec = transformAxes.times(transVec);
    if(ENABLE_DEBUG_LOGS){
      Log.d(TAG,String.format("ROBOT_TRANS: x %8.4f y %8.4f z %8.4f",
          transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));
    }

    //Create the pose matrix
    MatrixD robotPosMat = new MatrixD(3,4);
    robotPosMat.setSubmatrix(robotHeading, 3, 3, 0, 0);

    robotPosMat.setSubmatrix(transVec, 3, 1, 0, 3);

    Pose robotPose = new Pose(robotPosMat);

    double[] angles = PoseUtils.getAnglesAroundZ(robotPose);
    Log.d(TAG,String.format("POSE_HEADING: x %8.4f z %8.4f up %8.4f",angles[0],angles[1],angles[2]));

    transVec = robotPose.getTranslationMatrix();
    Log.d(TAG,String.format("POSE_TRANS: x %8.4f y %8.4f z %8.4f",
        transVec.data()[0][0], transVec.data()[1][0], transVec.data()[2][0]));

    update(robotPose);
    return;

  }

}
